package com.realtech.socialsurvey.compute.topology.spouts;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.FacebookToken;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.FacebookFeedData;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.enums.SocialFeedType;
import com.realtech.socialsurvey.compute.feeds.FacebookFeedProcessor;
import com.realtech.socialsurvey.compute.feeds.impl.FacebookFeedProcessorImpl;


/**
 * @author manish
 *
 */
public class FacebookFeedExtractorSpout extends BaseComputeSpout
{

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( FacebookFeedExtractorSpout.class );

    private SpoutOutputCollector _collector;
    public boolean isFbCalled;
    private FacebookFeedProcessor facebookFeedProcessor;


    @Override
    public void open( @SuppressWarnings ( "rawtypes") Map conf, TopologyContext context, SpoutOutputCollector collector )
    {
        super.open( conf, context, collector );
        this._collector = collector;
        this.facebookFeedProcessor = new FacebookFeedProcessorImpl();
    }


    private boolean isRateLimitExceeded()
    {
        // TODO ckech for ratelimiting for facebook api (based on user-id, page-id, )
        return false;
    }


    @Override
    public void nextTuple()
    {
        try {

            Optional<List<SocialMediaTokenResponse>> mediaTokens = SSAPIOperations.getInstance().getMediaTokens();

            if ( mediaTokens.isPresent() ) {

                for ( SocialMediaTokenResponse mediaToken : mediaTokens.get() ) {
                    Long companyId = mediaToken.getCompanyId();
                    FacebookToken token = null;
                    if ( mediaToken.getSocialMediaTokens() != null ) {
                        token = mediaToken.getSocialMediaTokens().getFacebookToken();
                    }

                    // Check rate limiting for company
                    if ( isRateLimitExceeded( /* pass media token*/ ) || isFbCalled ) {
                        LOG.warn( "Rate limit exceeded" );
                        break;
                    }

                    List<FacebookFeedData> feeds = facebookFeedProcessor.fetchFeeds( companyId, token );
                    isFbCalled = true;

                    LOG.debug( "Total tweet fetched : {}", feeds.size() );
                    for ( FacebookFeedData facebookFeedData : feeds ) {
                        SocialResponseObject<FacebookFeedData> responseWrapper = createSocialResponseObject( companyId,
                            facebookFeedData );
                        String responseWrapperString = new Gson().toJson( responseWrapper );
                        _collector.emit( new Values( Long.toString( companyId ), responseWrapperString ) );
                        LOG.debug( "Emitted successfully {}", responseWrapper );
                    }

                }
            }
            // End loop for companies
        } catch ( Exception e ) {
            LOG.error( "Error while fetching post from facebook.", e );
        }
    }


    /**
     * Create SocialResponseObject with common fields
     * @param companyId
     * @param facebookFeedData
     * @return
     */
    private SocialResponseObject<FacebookFeedData> createSocialResponseObject( long companyId,
        FacebookFeedData facebookFeedData )
    {
        SocialResponseObject<FacebookFeedData> responseWrapper = new SocialResponseObject<>( companyId, SocialFeedType.FACEBOOK,
            facebookFeedData.getMessage(), facebookFeedData, 1 );

        if ( facebookFeedData.getMessage() != null ) {
            responseWrapper.setHash( responseWrapper.getText().hashCode() );
        }

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
