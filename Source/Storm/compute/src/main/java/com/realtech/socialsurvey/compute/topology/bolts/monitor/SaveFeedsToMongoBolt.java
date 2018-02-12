package com.realtech.socialsurvey.compute.topology.bolts.monitor;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.realtech.socialsurvey.compute.entities.FailedMessage;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.api.APIIntegrationException;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;
import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.dao.RedisCompanyKeywordsDao;
import com.realtech.socialsurvey.compute.dao.impl.RedisCompanyKeywordsDaoImpl;
import com.realtech.socialsurvey.compute.entities.SocialResponseType;
import com.realtech.socialsurvey.compute.entities.response.FacebookFeedData;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.entities.response.TwitterFeedData;
import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedData;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBolt;


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

    private RedisCompanyKeywordsDao redisCompanyKeywordsDao = new RedisCompanyKeywordsDaoImpl();

    @SuppressWarnings ( "unchecked")
    @Override
    public void executeTuple( Tuple input )
    {
        LOG.info( "Executing save post to mongo bolt." );
        long companyId = input.getLongByField( "companyId" );
        SocialResponseType socialResponseType = (SocialResponseType) input.getValueByField( "type" );
        SocialResponseObject<?> post = null;
        boolean isSuccess = true;

        if ( socialResponseType != null ) {
            SocialResponseObject socialPost = (SocialResponseObject) input.getValueByField( "post" );
            if ( socialPost != null ) {
                try{
                    //do not add a post if its already present in mongo
                    SocialResponseObject socialResponseObject = SSAPIOperations.getInstance().getPostFromMongo(socialPost.getId());
                    if(socialResponseObject == null){
                        if ( socialResponseType.getType().equals( "FACEBOOK" ) ) {
                            post = (SocialResponseObject<FacebookFeedData>) socialPost;
                            addSocialFacebookFeedToMongo( (SocialResponseObject<FacebookFeedData>) post );
                        } else if ( socialResponseType.getType().equals( "TWITTER" ) ) {
                            post = (SocialResponseObject<TwitterFeedData>) socialPost;
                            addSocialTwitterFeedToMongo( (SocialResponseObject<TwitterFeedData>) post );
                        } else if ( socialResponseType.getType().equals( "LINKEDIN" ) ) {
                            post = (SocialResponseObject<LinkedinFeedData>) socialPost;
                            addSocialLinkedinFeedToMongo( (SocialResponseObject<LinkedinFeedData>) post );
                        }
                        _collector.emit("RETRY_STREAM", input, new Values(isSuccess, post));
                    }
                    else
                        post = socialResponseObject;

                    LOG.info( "Successfully emitted message to UpdateSocialPostDuplicateCount" );
                    _collector.emit( "SUCCESS_STREAM", input, Arrays.asList(isSuccess, companyId, post ) );

                } catch (IOException | APIIntegrationException e) {
                    //save the feed into mongo as temporary exception if it happened for first time
                    isSuccess = false;
                    if(!post.isRetried()){
                        FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
                        failedMessagesService.insertTemporaryFailedSocialPost(post);
                    }
                    else
                        _collector.emit("RETRY_STREAM", input, new Values(isSuccess, post));
                }


            } else {
                LOG.warn( "Social post is null" );
            }


        }

    }

    @Override
    public List<Object> prepareTupleForFailure() {
        return null;
    }


    private boolean addSocialFacebookFeedToMongo( SocialResponseObject<FacebookFeedData> socialPost ) throws IOException {
        return SSAPIOperations.getInstance().saveFeedToMongo( socialPost );
    }


    private boolean addSocialTwitterFeedToMongo( SocialResponseObject<TwitterFeedData> socialPost ) throws IOException, APIIntegrationException {
        return SSAPIOperations.getInstance().saveTwitterFeedToMongo( socialPost );
    }


    private boolean addSocialLinkedinFeedToMongo( SocialResponseObject<LinkedinFeedData> socialPost ) throws IOException {
        return SSAPIOperations.getInstance().saveLinkedinFeedToMongo( socialPost );
    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declareStream("SUCCESS_STREAM", new Fields("isSuccess", "companyId", "post"));
        declarer.declareStream("RETRY_STREAM", new Fields( "isSuccess", "post") );
    }
}
