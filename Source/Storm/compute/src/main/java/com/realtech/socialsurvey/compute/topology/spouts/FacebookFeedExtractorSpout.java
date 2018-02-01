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
import com.realtech.socialsurvey.compute.common.FacebookAPIOperations;
import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.FacebookFeedData;
import com.realtech.socialsurvey.compute.entities.response.FacebookResponse;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.enums.SocialFeedType;


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


    @Override
    public void open( @SuppressWarnings ( "rawtypes") Map conf, TopologyContext context, SpoutOutputCollector collector )
    {
        super.open( conf, context, collector );
        this._collector = collector;
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
                    String accessToken = mediaToken.getSocialMediaTokens().getFacebookToken().getFacebookAccessToken();

                    // Check rate limiting for company
                    if ( !isRateLimitExceeded( /* pass media token*/ ) && !isFbCalled ) {
                        // Get SocailMediaToken for company
                        Long companyId = mediaToken.getCompanyId();

                        //Call facebook api to get facebook page post.
                        Optional<FacebookResponse> response = FacebookAPIOperations.getInstance().fetchFeeds( "Timesnow",
                            accessToken, 0L, 0L, "", "" );
                        if ( response.isPresent() ) {
                            isFbCalled = true;
                            LOG.debug( "response  : ", response.get() );
                            for ( FacebookFeedData fbResponse : response.get().getData() ) {


                                SocialResponseObject<FacebookFeedData> responseWrapper = new SocialResponseObject<>(
                                        companyId, SocialFeedType.FACEBOOK, fbResponse.getMessage(), fbResponse, 1 );

                                responseWrapper.setHash( responseWrapper.getText().hashCode() );

                                Gson gson = new Gson();

                                String responseWrapperString = gson.toJson( responseWrapper );

                                _collector.emit( new Values( Long.toString( companyId ), responseWrapperString ) );
                                LOG.debug( "Emitted successfully {}", responseWrapper );
                            }
                        } else {
                            LOG.debug( "No feed found" );
                        }
                    } else {
                        LOG.warn( "Rate limit exceeded" );
                    }
                }
            }
            // End loop for companies
        } catch ( Exception e ) {
            LOG.error( "Error while fetching post from facebook.", e );
        }
    }


    /*private List<FacebookFeedData> getFacebookFeeds( String pageId, String accessToken )
    {
        //TODO Get it from redis;
        String after = null;
        long since = 0L, until = 0L;

        if ( after == null || after.isEmpty() ) {
            Calendar cal = Calendar.getInstance();
            cal.add( Calendar.DAY_OF_YEAR, -10 );
            since = cal.getTimeInMillis();
            after = "";
        }
    
        Optional<FacebookResponse> response = FacebookAPIOperations.getInstance().fetchFeeds( pageId, accessToken, since,
            until, after, "" );
    
        List<FacebookFeedData> feeds = new ArrayList<>();
    

        if ( response.isPresent() ) {
            feeds.addAll( response.get().getData() );
            
            after = response.get().getPaging().getCursors().getAfter();
            
            while ( after != null && !after.isEmpty() ) {
                
            }
        } 
        return feeds;
    }*/


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "companyId", "post" ) );
    }
}
