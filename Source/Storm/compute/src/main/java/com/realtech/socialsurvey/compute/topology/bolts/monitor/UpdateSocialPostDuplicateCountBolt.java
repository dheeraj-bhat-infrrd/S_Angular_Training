package com.realtech.socialsurvey.compute.topology.bolts.monitor;

import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.exception.FatalException;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.api.APIIntegrationException;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBolt;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UpdateSocialPostDuplicateCountBolt extends BaseComputeBoltWithAck {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( SaveFeedsToMongoBolt.class );

    @Override
    public void executeTuple(Tuple tuple) {
        LOG.info("Executing update duplicate feedCount bolt ... ");
        long companyId = tuple.getLongByField("companyId");
        SocialResponseObject<?> socialPost = (SocialResponseObject<?>) tuple.getValueByField( "post" );
        boolean success = tuple.getBooleanByField("isSuccess");

        int hash = socialPost != null ? socialPost.getHash() : 0;
        boolean isSuccess = false;
        FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
        LOG.debug("HashCode of the social text = {}", hash);

        if(success && hash != 0) {
            try {
                //check if posts with same hash are already present
                Optional<Long> duplicateCount = getSocialPostDuplicateCount(hash, companyId);
                if (duplicateCount.isPresent() && duplicateCount.get() > 1) {
                    //update all the duplicateCount field of the social post
                    Optional<Long> updatedPosts = updateSocialPostDuplicateCount(hash, companyId, duplicateCount.get());
                    if (updatedPosts.isPresent() && updatedPosts.get() == duplicateCount.get()) {
                        isSuccess = true;
                        LOG.info(" Total {} docs were successfully updated for having hash {} and postId {} ", updatedPosts.get(), hash, socialPost.getPostId());
                        _collector.emit("RETRY_STREAM", tuple, new Values(isSuccess, socialPost));
                    } else {
                        LOG.warn("Something went wrong while updating duplicateCount of post having postId {} !!! ", socialPost.getPostId());
                        //insert as a permanent failure
                        failedMessagesService.insertPermanentlyFailedSocialPost(socialPost,
                                new FatalException("DuplicateCount and updated document count do not match"));
                    }
                } else if (duplicateCount.isPresent() && duplicateCount.get() == null) {
                    LOG.warn("Social Post with hash {} and postId {} is not present in mongo !!! Something went wrong !!!", socialPost.getPostId(), hash);
                    //insert as a permanent failure
                    failedMessagesService.insertPermanentlyFailedSocialPost(socialPost,
                            new FatalException("Social Post is not present in mongo !!! Something went wrong !!!"));
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
        return new Values(false, 0, null);
    }

    private Optional<Long> updateSocialPostDuplicateCount(int hash, long companyId, long duplicateCount) throws IOException {
        return SSAPIOperations.getInstance().updateSocialPostDuplicateCount(hash, companyId, duplicateCount);
    }

    private Optional<Long> getSocialPostDuplicateCount(int hash, long comapanyId) throws IOException {
        return SSAPIOperations.getInstance().getSocialPostDuplicateCount( hash, comapanyId);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("SUCCESS_STREAM", new Fields("isSuccess", "companyId", "post" ) );
        declarer.declareStream("RETRY_STREAM", new Fields( "isSuccess", "post") );
    }
}