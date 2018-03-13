package com.realtech.socialsurvey.compute.topology.bolts.monitor;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;

public class UpdateSocialPostDuplicateCountBolt extends BaseComputeBoltWithAck {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( UpdateSocialPostDuplicateCountBolt.class );

    @Override
    public void executeTuple(Tuple tuple) {
        LOG.debug("Executing update duplicate feedCount bolt ... ");
        long companyId = tuple.getLongByField("companyId");
        SocialResponseObject<?> socialPost = (SocialResponseObject<?>) tuple.getValueByField( "post" );
        boolean success = tuple.getBooleanByField("isSuccess");

        int hash = socialPost != null ? socialPost.getHash() : 0;
        boolean isSuccess = false;
        FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
        LOG.debug("HashCode of the social text = {}", hash);

        if(success && hash != 0) {
            try {
                Optional<Long> updatedPosts = updateSocialPostDuplicateCount(hash, companyId);
                if (updatedPosts.isPresent() &&  updatedPosts.get() > 1) {
                    isSuccess = true;
                    LOG.info(" Total {} docs were successfully updated for having hash {} and postId {} ",
                            updatedPosts.get(), hash, socialPost.getPostId());
                    _collector.emit("RETRY_STREAM", tuple, new Values(isSuccess, socialPost));
                } else if ( !updatedPosts.isPresent() || updatedPosts.get() == 0){
                    LOG.error("Something went wrong while updating the social post having postId {}", socialPost.getPostId());
                    failedMessagesService.insertPermanentlyFailedSocialPost(socialPost,
                            new Exception("Something went wrong while updating the social post"));
                }
            } catch (IOException | APIIntegrationException e ) {
                if(!socialPost.isRetried()){
                    failedMessagesService.insertTemporaryFailedSocialPost(socialPost);
                }
                else
                    _collector.emit("RETRY_STREAM", tuple, new Values(isSuccess, socialPost));
            }
        }
        else{
            LOG.warn("Text is null.. so ignoring");
        }
        _collector.emit( "SUCCESS_STREAM",tuple, new Values(isSuccess, companyId, socialPost));
    }

    @Override
    public List<Object> prepareTupleForFailure() {
        return new Values(false, 0L, null);
    }

    private Optional<Long> updateSocialPostDuplicateCount(int hash, long companyId) throws IOException {
        return SSAPIOperations.getInstance().updateSocialPostDuplicateCount(hash, companyId);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("SUCCESS_STREAM", new Fields("isSuccess", "companyId", "post" ) );
        declarer.declareStream("RETRY_STREAM", new Fields( "isSuccess", "post") );
    }
}