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
import com.realtech.socialsurvey.compute.entities.LinkedInToken;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedData;
import com.realtech.socialsurvey.compute.enums.SocialFeedType;
import com.realtech.socialsurvey.compute.feeds.LinkedinFeedProcessor;
import com.realtech.socialsurvey.compute.feeds.impl.LinkedinFeedProcessorImpl;


/**
 * @author manish
 * Linkedin Feed processor spout
 */
public class LinkedinFeedExtractorSpout extends BaseComputeSpout
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( LinkedinFeedExtractorSpout.class );

    private SpoutOutputCollector _collector;

    private LinkedinFeedProcessor linkedinFeedProcessor;

    private boolean IS_LNCALLED = false;


    @Override
    public void open( @SuppressWarnings ( "rawtypes") Map conf, TopologyContext context, SpoutOutputCollector collector )
    {
        super.open( conf, context, collector );
        this._collector = collector;
        this.setLinkedinFeedProcessor( new LinkedinFeedProcessorImpl() );
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
                    LinkedInToken token = mediaToken.getSocialMediaTokens().getLinkedInToken();

                    // Check rate limiting for company
                    if ( !isRateLimitExceeded( /* pass media token*/ ) && !IS_LNCALLED ) {
                        // Get SocailMediaToken for company
                        Long companyId = mediaToken.getCompanyId();

                        //Call facebook api to get facebook page post.
                        List<LinkedinFeedData> feeds = linkedinFeedProcessor.fetchFeeds(companyId, token );
                        IS_LNCALLED = true;
                        LOG.debug( "response  : ", feeds );
                        for ( LinkedinFeedData linkedInResponse : feeds ) {
                            String text = "";
                            String id = null;
                            if ( linkedInResponse.getUpdateContent() != null
                                && linkedInResponse.getUpdateContent().getCompanyStatusUpdate() != null
                                && linkedInResponse.getUpdateContent().getCompanyStatusUpdate().getShare() != null ) {
                                text = linkedInResponse.getUpdateContent().getCompanyStatusUpdate().getShare().getComment();
                                id = linkedInResponse.getUpdateContent().getCompanyStatusUpdate().getShare().getId();
                            }

                            SocialResponseObject<LinkedinFeedData> responseWrapper = new SocialResponseObject<>( companyId,
                                SocialFeedType.LINKEDIN, text, linkedInResponse, 1 );
                            responseWrapper.setHash( responseWrapper.getText().hashCode() );
                            //set the postId for responseObject which will be used to uniquely identify a message
                            responseWrapper.setPostId(id);

                            Gson gson = new Gson();

                            String responseWrapperString = gson.toJson( responseWrapper );

                            _collector.emit( new Values( companyId.toString(), responseWrapperString ) );
                            LOG.debug( "Emitted successfully {}", responseWrapper );
                        }
                    } else {
                        LOG.warn( "Rate limit exceeded" );
                    }
                }
            }
            // End loop for companies
        } catch ( Exception e ) {
            LOG.error( "Error while fetching post from linkedin.", e );
        }
    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "companyId", "post" ) );
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
