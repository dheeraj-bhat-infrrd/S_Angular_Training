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
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.TwitterToken;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.entities.response.TwitterFeedData;
import com.realtech.socialsurvey.compute.enums.SocialFeedType;
import com.realtech.socialsurvey.compute.feeds.TwitterFeedProcessor;
import com.realtech.socialsurvey.compute.feeds.impl.TwitterFeedProcessorImpl;


/**
 * @author manish
 *
 */
public class TwitterFeedExtractorSpout extends BaseComputeSpout
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( TwitterFeedExtractorSpout.class );
    private SpoutOutputCollector _collector;
    private TwitterFeedProcessor twitterFeedProcessor;

    public boolean isTwitterCalled;


    @Override
    public void open( @SuppressWarnings ( "rawtypes") Map conf, TopologyContext context, SpoutOutputCollector collector )
    {
        super.open( conf, context, collector );
        this._collector = collector;
        this.setTwitterFeedProcessor( new TwitterFeedProcessorImpl() );
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
                    TwitterToken token = mediaToken.getSocialMediaTokens().getTwitterToken();
                    // Check rate limiting for company
                    if ( !isRateLimitExceeded( /* pass media token*/ ) && !isTwitterCalled ) {
                        isTwitterCalled = true;
                        // Get SocailMediaToken for company
                        Long companyId = mediaToken.getCompanyId();

                        //Call facebook api to get facebook page post.
                        List<TwitterFeedData> response = fetchFeeds( companyId, token );
                        if ( response != null ) {
                            LOG.debug( "response  : ", response.size() );
                            for ( TwitterFeedData twitterFeedData : response ) {
                                SocialResponseObject<TwitterFeedData> responseWrapper = new SocialResponseObject<>( companyId,

                                    SocialFeedType.TWITTER, twitterFeedData.getText(), twitterFeedData, 1 );
                                responseWrapper.setHash( responseWrapper.getText().hashCode() );
                                Gson gson = new Gson();
                                String responseWrapperString = gson.toJson( responseWrapper );
                                _collector.emit( new Values( companyId.toString(), responseWrapperString ) );
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
            LOG.error( "Error while fetching post from Twitter.", e );
        }
    }


    private List<TwitterFeedData> fetchFeeds( long companyIden, TwitterToken token )
    {
        return twitterFeedProcessor.fetchFeed( companyIden, token );
    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "companyId", "post" ) );
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
