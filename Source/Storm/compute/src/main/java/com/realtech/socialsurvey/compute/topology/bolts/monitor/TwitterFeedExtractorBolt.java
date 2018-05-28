package com.realtech.socialsurvey.compute.topology.bolts.monitor;


import com.google.gson.Gson;
import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao;
import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.TwitterTokenForSM;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.entities.response.TwitterFeedData;
import com.realtech.socialsurvey.compute.enums.ProfileType;
import com.realtech.socialsurvey.compute.enums.SocialFeedType;
import com.realtech.socialsurvey.compute.feeds.TwitterFeedProcessor;
import com.realtech.socialsurvey.compute.feeds.impl.TwitterFeedProcessorImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBolt;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;
import com.realtech.socialsurvey.compute.utils.UrlHelper;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.List;
import java.util.Map;


/**
 * @author manish
 *
 */
public class TwitterFeedExtractorBolt extends BaseComputeBolt
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( TwitterFeedExtractorBolt.class );
    private RedisSocialMediaStateDao socialMediaStateDao;

    private TwitterFeedProcessor twitterFeedProcessor;


    private String twitterConsumerKey;
    private String twitterConsumerSecret;

    private boolean isRateLimitExceeded(String pageId)
    {
       return socialMediaStateDao.isTwitterLockSet(pageId);
    }

    @Override
    public void execute( Tuple input )
    {
        try {
            SocialMediaTokenResponse mediaToken = (SocialMediaTokenResponse) input.getValueByField( "mediaToken" );
            TwitterTokenForSM twitterToken = mediaToken.getSocialMediaTokens() != null ? mediaToken.getSocialMediaTokens().getTwitterToken() : null ;
            if(twitterToken != null) {
                Long companyId = mediaToken.getCompanyId();
                String pageId = UrlHelper.getTwitterPageIdFromURL(twitterToken.getTwitterPageLink());
                // Check rate limiting for company
                if (isRateLimitExceeded(pageId)) {
                    LOG.warn("Rate limit exceeded for pageId {}", pageId);
                } else {
                    String lastFetchedKey = getLastFetchedKey(mediaToken);

                    //Call twitter api to get tweets.
                    List<TwitterFeedData> response = twitterFeedProcessor.fetchFeed(companyId, mediaToken, this.twitterConsumerKey,
                            this.twitterConsumerSecret);
                    LOG.info("Total tweet fetched : {}", response.size());
                    for (TwitterFeedData twitterFeedData : response) {
                        SocialResponseObject<TwitterFeedData> responseWrapper = createSocialResponseObject(mediaToken,
                                twitterFeedData);
                        
                        String responseWrapperString = new Gson().toJson(responseWrapper);
                        _collector.emit(new Values(companyId.toString(), responseWrapperString, lastFetchedKey));
                        LOG.trace("Emitted successfully {}", responseWrapper);
                    }
                    // End loop for companies
                }
            } else
                LOG.warn( "No twitter token found for company {}", mediaToken.getCompanyId() );

        }
        catch ( JedisConnectionException jedisConnectionException ) {
            LOG.warn("Redis might be down !!! Error message is {}", jedisConnectionException.getMessage());
        }
        catch ( Exception e ) {
            LOG.error( "Error while fetching post from Twitter.", e );
        }
    }


    /**
     * Method for creating lastfetched key
     * @param mediaToken
     * @return
     */
    private String getLastFetchedKey( SocialMediaTokenResponse mediaToken )
    {
        String lastFetchedKey = "";
        if ( mediaToken.getSocialMediaTokens() != null && mediaToken.getSocialMediaTokens().getTwitterToken() != null ) {
            TwitterTokenForSM token = mediaToken.getSocialMediaTokens().getTwitterToken();
            String pageId = UrlHelper.getTwitterPageIdFromURL( token.getTwitterPageLink() );
            lastFetchedKey = mediaToken.getProfileType().toString() + "_" + mediaToken.getIden() + "_" + pageId;
        }
        return lastFetchedKey;
    }


    /**
     *  Create SocialResponseObject with common fields
     * @param mediaToken
     * @param twitterFeedData
     * @return
     */
    private SocialResponseObject<TwitterFeedData> createSocialResponseObject( SocialMediaTokenResponse mediaToken,
        TwitterFeedData twitterFeedData )
    {
        SocialResponseObject<TwitterFeedData> responseWrapper = new SocialResponseObject<>( mediaToken.getCompanyId(),
            SocialFeedType.TWITTER, twitterFeedData.getText(), twitterFeedData, 1 );
        
        responseWrapper.setPageLink( mediaToken.getSocialMediaTokens().getTwitterToken().getTwitterPageLink() );
        responseWrapper.setPostLink( mediaToken.getSocialMediaTokens().getTwitterToken().getTwitterPageLink() +"/status/"+ twitterFeedData.getId());

        if ( mediaToken.getProfileType() != null ) {
            responseWrapper.setProfileType( mediaToken.getProfileType() );
            if ( mediaToken.getProfileType() == ProfileType.COMPANY ) {
                responseWrapper.setCompanyId( mediaToken.getIden() );
            } else if ( mediaToken.getProfileType() == ProfileType.REGION ) {
                responseWrapper.setRegionId( mediaToken.getIden() );
            } else if ( mediaToken.getProfileType() == ProfileType.BRANCH ) {
                responseWrapper.setBranchId( mediaToken.getIden() );
            } else if ( mediaToken.getProfileType() == ProfileType.AGENT ) {
                responseWrapper.setAgentId( mediaToken.getIden() );
            }
        }

        if ( twitterFeedData.getText() != null && !twitterFeedData.getText().isEmpty() ) {
            String twitterText = ChararcterUtils.getTextIfRetweet( responseWrapper.getText() );
            responseWrapper.setHash( twitterText.hashCode() );
        }

        responseWrapper.setPostId( String.valueOf( twitterFeedData.getId() ) );
        //Id is postId_companyId
        responseWrapper.setId( String.valueOf( twitterFeedData.getId() ) + "_" + responseWrapper.getCompanyId() );
        responseWrapper.setPictures(twitterFeedData.getPictures());
        responseWrapper.setOwnerName( mediaToken.getContactDetails().getName() );
        responseWrapper.setOwnerEmail( mediaToken.getContactDetails().getMailDetails().getEmailId() );
        responseWrapper.setPostSource(twitterFeedData.getSource());
        if ( twitterFeedData.getCreatedAt() != null ) {
            responseWrapper.setCreatedTime( twitterFeedData.getCreatedAt().getTime() );
            responseWrapper.setUpdatedTime( twitterFeedData.getCreatedAt().getTime() );
        }

        return responseWrapper;
    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "companyId", "post", "lastFetchedKey" ) );
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);
        this.twitterFeedProcessor = new TwitterFeedProcessorImpl();
        this.socialMediaStateDao = new RedisSocialMediaStateDaoImpl();
        twitterConsumerKey = (String) stormConf.get(ComputeConstants.TWITTER_CONSUMER_KEY);
        twitterConsumerSecret = (String) stormConf.get(ComputeConstants.TWITTER_CONSUMER_SECRET);
    }

    public TwitterFeedProcessor getTwitterFeedProcessor()
    {
        return twitterFeedProcessor;
    }


    public void setTwitterFeedProcessor( TwitterFeedProcessor twitterFeedProcessor )
    {
        this.twitterFeedProcessor = twitterFeedProcessor;
    }
}
