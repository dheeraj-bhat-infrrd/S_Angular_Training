package com.realtech.socialsurvey.compute.topology.bolts.monitor;

import com.google.gson.Gson;
import com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao;
import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import com.realtech.socialsurvey.compute.entities.InstagramTokenForSM;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.InstagramMediaData;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.enums.ProfileType;
import com.realtech.socialsurvey.compute.enums.SocialFeedStatus;
import com.realtech.socialsurvey.compute.enums.SocialFeedType;
import com.realtech.socialsurvey.compute.feeds.InstagramFeedProcessor;
import com.realtech.socialsurvey.compute.feeds.impl.InstagramFeedProcessorImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBolt;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InstagramFeedExactorBolt extends BaseComputeBolt {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( InstagramFeedExactorBolt.class );

    private InstagramFeedProcessor instagramFeedProcessor;
    private RedisSocialMediaStateDao socialMediaStateDao;

    @Override
    public void execute(Tuple input) {
        try {
            SocialMediaTokenResponse mediaToken = (SocialMediaTokenResponse) input.getValueByField("mediaToken");
            InstagramTokenForSM igToken = mediaToken.getSocialMediaTokens()!= null ? mediaToken.getSocialMediaTokens().getInstagramToken() : null;
            if(igToken != null){
                Long companyId = mediaToken.getCompanyId();
                if (isRateLimitExceeded( mediaToken)) {
                    LOG.warn(" Rate limit exceeded for instagram account {} ", igToken.getPageLink() );
                } else if(mediaToken.getSocialMediaTokens()!=null && mediaToken.getSocialMediaTokens().getInstagramToken()!= null &&
                    mediaToken.getSocialMediaTokens().getInstagramToken().isTokenExpiryAlertSent()) {
                    LOG.warn( "Socialmedia Token has been expired having profileLink {}",
                        mediaToken.getSocialMediaTokens().getInstagramToken().getPageLink() );
                } else {
                    List<InstagramMediaData> feeds = instagramFeedProcessor.fetchFeeds(companyId, mediaToken);
                    LOG.debug( "Total tweet fetched : {}", feeds.size() );

                    String pageId = UrlHelper.getInstagramPageIdFromURL( igToken.getPageLink() );
                    String lastFetchedKey = mediaToken.getProfileType().toString() + "_" + mediaToken.getIden() + "_" + pageId;

                    for ( InstagramMediaData instagramMediaData : feeds ) {
                        SocialResponseObject<InstagramMediaData> responseWrapper = createSocialResponseObject( mediaToken,
                                instagramMediaData );
                        responseWrapper.setPageLink( mediaToken.getSocialMediaTokens().getInstagramToken().getPageLink() );
                        String responseWrapperString = new Gson().toJson( responseWrapper );

                        _collector.emit( new Values( Long.toString( companyId ), responseWrapperString, lastFetchedKey ) );
                        LOG.debug( "Emitted successfully {}", responseWrapper );
                    }
                }
            } else
                LOG.warn( "No facebook token found for company {}", mediaToken.getCompanyId() );

        } catch (JedisConnectionException jce){
            LOG.error("Redis might be down !!! Error message is {}", jce.getMessage());
        }
        catch (Exception e) {
            LOG.error(" Error while fetching posts from instragram {} ", e );
        }
    }

    private SocialResponseObject<InstagramMediaData> createSocialResponseObject(SocialMediaTokenResponse mediaToken,
                                                                                InstagramMediaData instagramMediaData) {
        SocialResponseObject<InstagramMediaData> responseWrapper = new SocialResponseObject<>( mediaToken.getCompanyId(),
                SocialFeedType.INSTAGRAM, instagramMediaData.getCaption(), instagramMediaData, 1, SocialFeedStatus.NEW );

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

        if ( instagramMediaData.getCaption() != null ) {
            responseWrapper.setHash( responseWrapper.getText().hashCode() );
        }

        responseWrapper.setPostId( instagramMediaData.getIgId() );
        //Id is postId_companyId
        responseWrapper.setId( instagramMediaData.getIgId() + "_" + responseWrapper.getCompanyId() );
        responseWrapper.setPictures(Arrays.asList(instagramMediaData.getMediaUrl()));
        responseWrapper.setOwnerName( mediaToken.getContactDetails().getName() );
        responseWrapper.setOwnerEmail( mediaToken.getContactDetails().getMailDetails().getEmailId() );
        responseWrapper.setPostLink( instagramMediaData.getPostLink() );

        if ( instagramMediaData.getTimestamp() > 0 ) {
            responseWrapper.setCreatedTime( instagramMediaData.getTimestamp() * 1000 );
            responseWrapper.setUpdatedTime( instagramMediaData.getTimestamp() * 1000 );
        }

        return responseWrapper;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare( new Fields( "companyId", "post", "lastFetchedKey" ) );
    }

    private boolean isRateLimitExceeded(SocialMediaTokenResponse mediaToken){
        String pageId = UrlHelper.getInstagramPageIdFromURL( mediaToken.getSocialMediaTokens().getInstagramToken().getPageLink() );
        return socialMediaStateDao.isFacebookApplicationLockSet() || socialMediaStateDao.isFacebookPageLockSet( pageId )
            || socialMediaStateDao
            .isFacebookTokenLockSet( mediaToken.getSocialMediaTokens().getInstagramToken().getAccessTokenToPost() );
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);
        this.instagramFeedProcessor = new InstagramFeedProcessorImpl();
        this.socialMediaStateDao = new RedisSocialMediaStateDaoImpl();
    }
}
