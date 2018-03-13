package com.realtech.socialsurvey.compute.topology.bolts.monitor;

import java.util.List;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.realtech.socialsurvey.compute.entities.FacebookToken;
import com.realtech.socialsurvey.compute.entities.LinkedInToken;
import com.realtech.socialsurvey.compute.entities.LinkedInTokenForSM;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.TwitterToken;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedData;
import com.realtech.socialsurvey.compute.enums.ProfileType;
import com.realtech.socialsurvey.compute.enums.SocialFeedType;
import com.realtech.socialsurvey.compute.feeds.LinkedinFeedProcessor;
import com.realtech.socialsurvey.compute.feeds.impl.LinkedinFeedProcessorImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBolt;
import com.realtech.socialsurvey.compute.utils.UrlHelper;


/**
 * @author manish
 * Linkedin Feed processor spout
 */
public class LinkedinFeedExtractorBolt extends BaseComputeBolt
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( LinkedinFeedExtractorBolt.class );

    private LinkedinFeedProcessor linkedinFeedProcessor = new LinkedinFeedProcessorImpl();


    private boolean isRateLimitExceeded()
    {
        // TODO ckech for ratelimiting for facebook api (based on user-id, page-id, )
        return false;
    }


    @Override
    public void execute( Tuple input )
    {
        try {

            SocialMediaTokenResponse mediaToken = (SocialMediaTokenResponse) input.getValueByField( "mediaToken" );

            // Check rate limiting for company
            if ( isRateLimitExceeded( /* pass media token*/ ) ) {
                LOG.warn( "Rate limit exceeded" );
            }
            // Get SocailMediaToken for company
            Long companyId = mediaToken.getCompanyId();
            
            String lastFetchedKey = getLastFetchedKey( mediaToken );
            //Call facebook api to get facebook page post.
            List<LinkedinFeedData> feeds = linkedinFeedProcessor.fetchFeeds( companyId, mediaToken );
            LOG.debug( "Total tweet fetched : {}", feeds.size() );
            for ( LinkedinFeedData linkedinFeedData : feeds ) {

                SocialResponseObject<LinkedinFeedData> responseWrapper = getSocialResponseObject( mediaToken,
                    linkedinFeedData );

                String responseWrapperString = new Gson().toJson( responseWrapper );

                _collector.emit( new Values( companyId.toString(), responseWrapperString, lastFetchedKey ) );
                LOG.debug( "Emitted successfully {}", responseWrapper );
            }

            // End loop for companies
        } catch (

        Exception e ) {
            LOG.error( "Error while fetching post from linkedin.", e );
        }
    }
    
    /**
     * Method for creating lastfetched key
     * @param mediaToken
     * @return
     */
    private String getLastFetchedKey(SocialMediaTokenResponse mediaToken){
        String lastFetchedKey = "";
        
        if ( mediaToken.getSocialMediaTokens() != null && mediaToken.getSocialMediaTokens().getFacebookToken() != null) {
            LinkedInTokenForSM token = mediaToken.getSocialMediaTokens().getLinkedInToken();
            String pageId = UrlHelper.getFacebookPageIdFromURL( token.getLinkedInPageLink() );
            lastFetchedKey = mediaToken.getProfileType().toString() + "_" + mediaToken.getIden() + "_" + pageId;
        }
        return lastFetchedKey;
    }


    /**
     * Create SocialResponseObject with common fields
     * @param mediaToken
     * @param linkedinFeedData
     * @return
     */
    private SocialResponseObject<LinkedinFeedData> getSocialResponseObject( SocialMediaTokenResponse mediaToken,
        LinkedinFeedData linkedinFeedData )
    {

        String text = "";
        String id = null;
        long updatedDate = 0L;
        if ( linkedinFeedData.getUpdateContent() != null && linkedinFeedData.getUpdateContent().getCompanyStatusUpdate() != null
            && linkedinFeedData.getUpdateContent().getCompanyStatusUpdate().getShare() != null ) {
            text = linkedinFeedData.getUpdateContent().getCompanyStatusUpdate().getShare().getComment();
            id = linkedinFeedData.getUpdateContent().getCompanyStatusUpdate().getShare().getId();

            if ( linkedinFeedData.getUpdateContent().getCompanyStatusUpdate().getShare().getTimestamp() > 0 ) {
                updatedDate = linkedinFeedData.getUpdateContent().getCompanyStatusUpdate().getShare().getTimestamp();
            }

        }

        SocialResponseObject<LinkedinFeedData> responseWrapper = new SocialResponseObject<>( mediaToken.getCompanyId(),
            SocialFeedType.LINKEDIN, text, linkedinFeedData, 1 );

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

        responseWrapper.setUpdatedTime( updatedDate );
        responseWrapper.setCreatedTime( updatedDate );

        responseWrapper.setHash( responseWrapper.getText().hashCode() );
        responseWrapper.setPostId( id );
        responseWrapper.setId( id );
        return responseWrapper;
    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "companyId", "post", "lastFetchedKey" ) );
    }


    public LinkedinFeedProcessor getLinkedinFeedProcessor()
    {
        return linkedinFeedProcessor;
    }


    public void setLinkedinFeedProcessor( LinkedinFeedProcessor linkedinFeedProcessor )
    {
        this.linkedinFeedProcessor = linkedinFeedProcessor;
    }
}
