package com.realtech.socialsurvey.compute.topology.bolts.monitor;


import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;
import com.realtech.socialsurvey.compute.exception.MongoSaveException;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * Class to save the post/feed to mongo
 * @author manish
 * 
 *
 */
public class SaveFeedsToMongoBolt extends BaseComputeBoltWithAck
{

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( SaveFeedsToMongoBolt.class );

    @SuppressWarnings ( "unchecked")
    @Override
    public void executeTuple( Tuple input )
    {
        LOG.debug( "Executing save post to mongo bolt." );
        long companyId = input.getLongByField( "companyId" );
        boolean isSuccess = false;
        String postId = null;
        SocialResponseObject<?> socialPost = (SocialResponseObject<?>) input.getValueByField( "post" );
        if ( socialPost != null ) {
            postId = socialPost.getPostId();
            try {
                //do not add a post if its already present in mongo
                LOG.info("Adding new social post to mongo");
                addSocialPostToMongo(socialPost);
                isSuccess = true;
                _collector.emit("RETRY_STREAM", input, new Values(isSuccess, socialPost));
            } catch (MongoSaveException duplicateKeyException) {
                if(socialPost.isRetried()) {
                    LOG.info("Social post having postId = {} was already saved in mongo.But duplicateCount not updated.", postId);
                    isSuccess = true;
                } else
                    LOG.warn("Duplicate post with postId {} !!! Hence not saving to mongo", postId);
            } catch (IOException | APIIntegrationException e) {
                //save the feed into mongo as temporary exception if it happened for first time
                LOG.error("Exception occured", e.getMessage());
                if(!socialPost.isRetried()){
                    FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
                    failedMessagesService.insertTemporaryFailedSocialPost(socialPost);
                }
                else
                    _collector.emit("RETRY_STREAM", input, new Values(isSuccess, socialPost));
            }

        } else {
            LOG.warn( "Social post is null" );
        }
        LOG.info( "Emitting message wih socialPost having postId = {}, isSuccess = {} to UpdateSocialPostDuplicateCount ", postId , isSuccess);
        _collector.emit( "SUCCESS_STREAM", input, Arrays.asList(isSuccess, companyId, socialPost ) );
    }

    private boolean addSocialPostToMongo(SocialResponseObject<?> socialPost) throws IOException {
        return SSAPIOperations.getInstance().saveFeedToMongo(socialPost);
    }

    @Override
    public List<Object> prepareTupleForFailure() {
        return new Values(false, 0L, null);
    }

    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer ) {
        declarer.declareStream("SUCCESS_STREAM", new Fields("isSuccess", "companyId", "post"));
        declarer.declareStream("RETRY_STREAM", new Fields( "isSuccess", "post") );
    }
}
