package com.realtech.socialsurvey.compute.topology.bolts.monitor;

import com.google.gson.Gson;
import com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao;
import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.InstagramMediaData;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.enums.ProfileType;
import com.realtech.socialsurvey.compute.enums.SocialFeedType;
import com.realtech.socialsurvey.compute.feeds.InstagramFeedProcessor;
import com.realtech.socialsurvey.compute.feeds.impl.InstagramFeedProcessorImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBolt;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InstagramFeedExactorBolt extends BaseComputeBolt implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( InstagramFeedExactorBolt.class );

    private InstagramFeedProcessor instagramFeedProcessor;
    private RedisSocialMediaStateDao socialMediaStateDao;

    @Override
    public void execute(Tuple input) {
        try {
            SocialMediaTokenResponse mediaToken = (SocialMediaTokenResponse) input.getValueByField("mediaToken");
            Long companyId = mediaToken.getCompanyId();
            if (isRateLimitExceeded( /*pass the media token*/)) {
                LOG.warn("Rate limit exceeded");
            } else {
                List<InstagramMediaData> feeds = instagramFeedProcessor.fetchFeeds(companyId, mediaToken);
                LOG.debug( "Total tweet fetched : {}", feeds.size() );

                for ( InstagramMediaData instagramMediaData : feeds ) {
                    SocialResponseObject<InstagramMediaData> responseWrapper = createSocialResponseObject( mediaToken,
                            instagramMediaData );
                    String responseWrapperString = new Gson().toJson( responseWrapper );

                    _collector.emit( new Values( Long.toString( companyId ), responseWrapperString ) );
                    LOG.debug( "Emitted successfully {}", responseWrapper );
                }
            }
        } catch (Exception e) {
            LOG.error(" Error while fetching posts from instragram {} ", e );
        }
    }

    private SocialResponseObject<InstagramMediaData> createSocialResponseObject(SocialMediaTokenResponse mediaToken,
                                                                              InstagramMediaData instagramMediaData) {
        SocialResponseObject<InstagramMediaData> responseWrapper = new SocialResponseObject<>( mediaToken.getCompanyId(),
                SocialFeedType.INSTAGRAM, instagramMediaData.getCaption(), instagramMediaData, 1 );

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
        responseWrapper.setId( instagramMediaData.getIgId() );
        responseWrapper.setOwnerProfileImage(mediaToken.getProfileImageUrl());
        responseWrapper.setPictures(Arrays.asList(instagramMediaData.getMediaUrl()));

        if ( instagramMediaData.getTimestamp() > 0 ) {
            responseWrapper.setCreatedTime( instagramMediaData.getTimestamp() * 1000 );
        }

        return responseWrapper;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    private boolean isRateLimitExceeded(){
        return false;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);
        this.instagramFeedProcessor = new InstagramFeedProcessorImpl();
        this.socialMediaStateDao = new RedisSocialMediaStateDaoImpl();
    }
}
