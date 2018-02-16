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
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.FacebookFeedData;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.enums.ProfileType;
import com.realtech.socialsurvey.compute.enums.SocialFeedType;
import com.realtech.socialsurvey.compute.feeds.FacebookFeedProcessor;
import com.realtech.socialsurvey.compute.feeds.impl.FacebookFeedProcessorImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBolt;


/**
 * @author manish
 *
 */
public class FacebookFeedExtractorBolt extends BaseComputeBolt
{

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( FacebookFeedExtractorBolt.class );

    private FacebookFeedProcessor facebookFeedProcessor = new FacebookFeedProcessorImpl();

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
            Long companyId = mediaToken.getCompanyId();
            FacebookToken token = null;
            if ( mediaToken.getSocialMediaTokens() != null ) {
                token = mediaToken.getSocialMediaTokens().getFacebookToken();
            }

            // Check rate limiting for company
            if ( isRateLimitExceeded( /* pass media token*/ ) ) {
                LOG.warn( "Rate limit exceeded" );
            }

            List<FacebookFeedData> feeds = facebookFeedProcessor.fetchFeeds( companyId, token );

            LOG.debug( "Total tweet fetched : {}", feeds.size() );
            for ( FacebookFeedData facebookFeedData : feeds ) {
                SocialResponseObject<FacebookFeedData> responseWrapper = createSocialResponseObject( mediaToken,
                    facebookFeedData );
                String responseWrapperString = new Gson().toJson( responseWrapper );
                _collector.emit( new Values( Long.toString( companyId ), responseWrapperString ) );
                LOG.debug( "Emitted successfully {}", responseWrapper );
            }

        }
        // End loop for companies
        catch ( Exception e ) {
            LOG.error( "Error while fetching post from facebook.", e );
        }
    }


    /**
     * Create SocialResponseObject with common fields
     * @param companyId
     * @param facebookFeedData
     * @return
     */
    private SocialResponseObject<FacebookFeedData> createSocialResponseObject( SocialMediaTokenResponse mediaToken,
        FacebookFeedData facebookFeedData )
    {
        SocialResponseObject<FacebookFeedData> responseWrapper = new SocialResponseObject<>( mediaToken.getCompanyId(), SocialFeedType.FACEBOOK,
            facebookFeedData.getMessage(), facebookFeedData, 1 );
        
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

        responseWrapper.setPostId(facebookFeedData.getId());

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
        declarer.declare( new Fields( "companyId", "post" ) );
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
