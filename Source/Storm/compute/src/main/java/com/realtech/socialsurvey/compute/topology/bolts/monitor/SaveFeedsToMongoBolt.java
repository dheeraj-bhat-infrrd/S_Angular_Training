package com.realtech.socialsurvey.compute.topology.bolts.monitor;


import java.util.Arrays;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
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
public class SaveFeedsToMongoBolt extends BaseComputeBolt
{

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( SaveFeedsToMongoBolt.class );

    private RedisCompanyKeywordsDao redisCompanyKeywordsDao = new RedisCompanyKeywordsDaoImpl();

    @SuppressWarnings ( "unchecked")
    @Override
    public void execute( Tuple input )
    {
        LOG.info( "Executing save post to mongo bolt." );
        long companyId = input.getLongByField( "companyId" );
        SocialResponseType socialResponseType = (SocialResponseType) input.getValueByField( "type" );
        SocialResponseObject<?> post = null;
        boolean isSuccess = true;
        boolean isSocialPostSaved = false;

        if ( socialResponseType != null ) {
            Object socialPost = input.getValueByField( "post" );
            if ( socialPost != null ) {
                if ( socialResponseType.getType().equals( "FACEBOOK" ) ) {
                    post = (SocialResponseObject<FacebookFeedData>) socialPost;
                    isSocialPostSaved = addSocialFacebookFeedToMongo( (SocialResponseObject<FacebookFeedData>) post );
                } else if ( socialResponseType.getType().equals( "TWITTER" ) ) {
                    post = (SocialResponseObject<TwitterFeedData>) socialPost;
                    isSocialPostSaved = addSocialTwitterFeedToMongo( (SocialResponseObject<TwitterFeedData>) post );
                } else if ( socialResponseType.getType().equals( "LINKEDIN" ) ) {
                    post = (SocialResponseObject<LinkedinFeedData>) socialPost;
                    isSocialPostSaved = addSocialLinkedinFeedToMongo( (SocialResponseObject<LinkedinFeedData>) post );
                }

                //if post is not added to mongo then repost to kafa and update redis keys
                if ( isSocialPostSaved ) {
                    _collector.emit( "SUCCESS_STREAM", input, Arrays.asList(isSuccess, companyId, post ) );
                    _collector.ack( input );
                    LOG.info( "Successfully emitted message." );
                }
                /* isSuccess = false;
                    redisCompanyKeywordsDao.setSSApiBreakerStateKeys();
                    repostMessageToKafka(input, companyId, post);*/
            } else {
                LOG.warn( "Social post is null" );
            }


        }

    }

    private int getSSApiRetryCountFromRedis() {
        return redisCompanyKeywordsDao.getSSApiRetryCount();
    }

    private void repostMessageToKafka(Tuple input, long companyId, SocialResponseObject<?> post) {
        LOG.warn("Something went wrong while adding posts to mongo!!!");
        _collector.emit("ERROR_STREAM",input, Arrays.asList(Long.toString(companyId), new Gson().toJson(post)));
    }


    private boolean addSocialFacebookFeedToMongo( SocialResponseObject<FacebookFeedData> socialPost )
    {
        return SSAPIOperations.getInstance().saveFeedToMongo( socialPost );
    }


    private boolean addSocialTwitterFeedToMongo( SocialResponseObject<TwitterFeedData> socialPost )
    {
        return SSAPIOperations.getInstance().saveTwitterFeedToMongo( socialPost );
    }


    private boolean addSocialLinkedinFeedToMongo( SocialResponseObject<LinkedinFeedData> socialPost )
    {
        return SSAPIOperations.getInstance().saveLinkedinFeedToMongo( socialPost );
    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declareStream("SUCCESS_STREAM", new Fields("isSuccess", "companyId", "post"));
        declarer.declareStream("ERROR_STREAM", new Fields( "companyId", "post" ) );
    }
}
