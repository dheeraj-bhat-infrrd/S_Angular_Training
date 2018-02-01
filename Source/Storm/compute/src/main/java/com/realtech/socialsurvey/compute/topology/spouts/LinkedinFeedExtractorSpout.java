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
import com.realtech.socialsurvey.compute.common.LinkedinAPIOperations;
import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedData;
import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedResponse;
import com.realtech.socialsurvey.compute.enums.SocialFeedType;


/**
 * @author manish
 * Linkedin Feed processor spout
 */
public class LinkedinFeedExtractorSpout extends BaseComputeSpout
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( LinkedinFeedExtractorSpout.class );

    private SpoutOutputCollector _collector;

    private boolean IS_LNCALLED = false;


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
                    String accessToken = mediaToken.getSocialMediaTokens().getLinkedInToken().getLinkedInAccessToken();

                    // Check rate limiting for company
                    if ( !isRateLimitExceeded( /* pass media token*/ ) && !IS_LNCALLED ) {
                        // Get SocailMediaToken for company
                        Long companyId = mediaToken.getCompanyId();

                        //Call facebook api to get facebook page post.
                        Optional<LinkedinFeedResponse> response = LinkedinAPIOperations.getInstance().fetchFeeds( "2414183", 0,

                            100, null, accessToken );
                        if ( response.isPresent() ) {
                            IS_LNCALLED = true;
                            LOG.debug( "response  : ", response.get() );
                            for ( LinkedinFeedData linkedInResponse : response.get().getValues() ) {
                                String text = "";
                                if ( linkedInResponse.getUpdateContent() != null
                                    && linkedInResponse.getUpdateContent().getCompanyStatusUpdate() != null
                                    && linkedInResponse.getUpdateContent().getCompanyStatusUpdate().getShare() != null ) {
                                    text = linkedInResponse.getUpdateContent().getCompanyStatusUpdate().getShare().getComment();
                                }

                                SocialResponseObject<LinkedinFeedData> responseWrapper = new SocialResponseObject<>( companyId,
                                    SocialFeedType.LINKEDIN, text, linkedInResponse, 1 );
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
            LOG.error( "Error while fetching post from linkedin.", e );
        }
    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "companyId", "post" ) );
    }
}
