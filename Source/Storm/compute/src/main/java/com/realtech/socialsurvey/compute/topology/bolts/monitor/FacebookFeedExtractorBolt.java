package com.realtech.socialsurvey.compute.topology.bolts.monitor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao;
import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import com.realtech.socialsurvey.compute.entities.FacebookTokenForSM;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.FacebookFeedData;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.enums.ProfileType;
import com.realtech.socialsurvey.compute.enums.SocialFeedStatus;
import com.realtech.socialsurvey.compute.enums.SocialFeedType;
import com.realtech.socialsurvey.compute.feeds.FacebookFeedProcessor;
import com.realtech.socialsurvey.compute.feeds.impl.FacebookFeedProcessorImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBolt;
import com.realtech.socialsurvey.compute.utils.UrlHelper;
import redis.clients.jedis.exceptions.JedisConnectionException;


/**
 * @author manish
 *
 */
public class FacebookFeedExtractorBolt extends BaseComputeBolt implements Serializable
{

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( FacebookFeedExtractorBolt.class );

    private FacebookFeedProcessor facebookFeedProcessor;
    private RedisSocialMediaStateDao socialMediaStateDao;


    private boolean isRateLimitExceeded( SocialMediaTokenResponse mediaToken )
    {
        if ( mediaToken.getSocialMediaTokens() != null && mediaToken.getSocialMediaTokens().getFacebookToken() != null ) {
            FacebookTokenForSM token = mediaToken.getSocialMediaTokens().getFacebookToken();
            String pageId = UrlHelper.getFacebookPageIdFromURL( token.getFacebookPageLink() );

            if (socialMediaStateDao.isFacebookApplicationLockSet() || socialMediaStateDao.isFacebookPageLockSet( pageId ) || socialMediaStateDao
                .isFacebookTokenLockSet( mediaToken.getSocialMediaTokens().getFacebookToken().getFacebookAccessTokenToPost() ) ) {
                LOG.warn( "Facebook feed extractor reached rate limiting." );
                return true;
            }
        }
        return false;
    }


    @Override
    public void prepare( @SuppressWarnings ( "rawtypes") Map stormConf, TopologyContext context, OutputCollector collector )
    {
        super.prepare( stormConf, context, collector );
        this.facebookFeedProcessor = new FacebookFeedProcessorImpl();
        this.socialMediaStateDao = new RedisSocialMediaStateDaoImpl();
    }


    @Override
    public void execute( Tuple input )
    {
        try {
            SocialMediaTokenResponse mediaToken = (SocialMediaTokenResponse) input.getValueByField( "mediaToken" );
            Long companyId = mediaToken.getCompanyId();

            // Check rate limiting for company
            if ( isRateLimitExceeded( mediaToken ) ) {
                LOG.warn( "Rate limit exceeded" );
            }
            //check if the facebook token has expired
            else if(mediaToken.getSocialMediaTokens()!=null && mediaToken.getSocialMediaTokens().getFacebookToken()!= null &&
                mediaToken.getSocialMediaTokens().getFacebookToken().isTokenExpiryAlertSent()) {
                LOG.warn( "Socialmedia Token has been expired having profileLink {}",
                    mediaToken.getSocialMediaTokens().getFacebookToken().getFacebookPageLink() );
            }
            else {
                List<FacebookFeedData> feeds = facebookFeedProcessor.fetchFeeds( companyId, mediaToken );

                String lastFetchedKey = getLastFetchedKey( mediaToken );

                LOG.debug( "Total tweet fetched : {}", feeds.size() );
                for ( FacebookFeedData facebookFeedData : feeds ) {
                    SocialResponseObject<FacebookFeedData> responseWrapper = createSocialResponseObject( mediaToken,
                        facebookFeedData );
                    responseWrapper.setPageLink( mediaToken.getSocialMediaTokens().getFacebookToken().getFacebookPageLink() );
                    String responseWrapperString = new Gson().toJson( responseWrapper );

                    _collector.emit( new Values( Long.toString( companyId ), responseWrapperString, lastFetchedKey ) );
                    LOG.debug( "Emitted successfully {}", responseWrapper );
                }
            }
        }
        // End loop for companies
        catch (JedisConnectionException jce){
            LOG.error("Redis might be down !!! Error message is {}", jce.getMessage());
        }

        catch ( Exception e ) {
            LOG.error( "Error while fetching post from facebook.", e );
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
        if ( mediaToken.getSocialMediaTokens() != null && mediaToken.getSocialMediaTokens().getFacebookToken() != null ) {
            FacebookTokenForSM token = mediaToken.getSocialMediaTokens().getFacebookToken();
            String pageId = UrlHelper.getFacebookPageIdFromURL( token.getFacebookPageLink() );
            lastFetchedKey = mediaToken.getProfileType().toString() + "_" + mediaToken.getIden() + "_" + pageId;
        }
        return lastFetchedKey;
    }


    /**
     * Create SocialResponseObject with common fields
     * @param mediaToken
     * @param facebookFeedData
     * @return
     */
    private SocialResponseObject<FacebookFeedData> createSocialResponseObject( SocialMediaTokenResponse mediaToken,
        FacebookFeedData facebookFeedData )
    {
        SocialResponseObject<FacebookFeedData> responseWrapper = new SocialResponseObject<>( mediaToken.getCompanyId(),
            SocialFeedType.FACEBOOK, facebookFeedData.getMessage(), facebookFeedData, 1, SocialFeedStatus.NEW );

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

        if ( facebookFeedData.getMessage() != null ) {
            responseWrapper.setHash( responseWrapper.getText().hashCode() );
        }

        responseWrapper.setPostId( facebookFeedData.getId() );
        //Id is postId_companyId
        responseWrapper.setId( facebookFeedData.getId() + "_" + responseWrapper.getCompanyId() );
        responseWrapper.setPictures(Arrays.asList(facebookFeedData.getFullPicture()));
        responseWrapper.setOwnerName( mediaToken.getContactDetails().getName() );
        responseWrapper.setOwnerEmail( mediaToken.getContactDetails().getMailDetails().getEmailId() );
        responseWrapper.setPostLink( facebookFeedData.getPostLink() );

        if(facebookFeedData.getApplication() != null && StringUtils.isNotEmpty(facebookFeedData.getApplication().getName()))
        		responseWrapper.setPostSource(facebookFeedData.getApplication().getName());
        
        if ( facebookFeedData.getUpdatedTime() > 0 ) {
            responseWrapper.setUpdatedTime( facebookFeedData.getUpdatedTime() * 1000 );
        }

        if ( facebookFeedData.getCreatedTime() > 0 ) {
            responseWrapper.setCreatedTime( facebookFeedData.getCreatedTime() * 1000 );
        }

        return responseWrapper;
    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "companyId", "post", "lastFetchedKey" ) );
    }


    public FacebookFeedProcessor getFacebookFeedProcessor()
    {
        return facebookFeedProcessor;
    }


    public void setFacebookFeedProcessor( FacebookFeedProcessor facebookFeedProcessor )
    {
        this.facebookFeedProcessor = facebookFeedProcessor;
    }
}
