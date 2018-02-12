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

                    LinkedInToken token = null;

                    if ( mediaToken.getSocialMediaTokens() != null ) {
                        token = mediaToken.getSocialMediaTokens().getLinkedInToken();
                    }

                    // Check rate limiting for company
                    if ( isRateLimitExceeded( /* pass media token*/ ) || IS_LNCALLED ) {
                        LOG.warn( "Rate limit exceeded" );
                        break;
                    }
                    // Get SocailMediaToken for company
                    Long companyId = mediaToken.getCompanyId();

                    //Call facebook api to get facebook page post.
                    List<LinkedinFeedData> feeds = linkedinFeedProcessor.fetchFeeds( companyId, token );
                    IS_LNCALLED = true;
                    LOG.debug( "Total tweet fetched : {}", feeds.size() );
                    for ( LinkedinFeedData linkedinFeedData : feeds ) {

                        SocialResponseObject<LinkedinFeedData> responseWrapper = getSocialResponseObject( companyId,
                            linkedinFeedData );

                        String responseWrapperString = new Gson().toJson( responseWrapper );

                        _collector.emit( new Values( companyId.toString(), responseWrapperString ) );
                        LOG.debug( "Emitted successfully {}", responseWrapper );
                    }

                }
            }
            // End loop for companies
        } catch ( Exception e ) {
            LOG.error( "Error while fetching post from linkedin.", e );
        }
    }


    /**
     * Create SocialResponseObject with common fields
     * @param companyId
     * @param linkedinFeedData
     * @return
     */
    private SocialResponseObject<LinkedinFeedData> getSocialResponseObject( long companyId, LinkedinFeedData linkedinFeedData )
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

        SocialResponseObject<LinkedinFeedData> responseWrapper = new SocialResponseObject<>( companyId, SocialFeedType.LINKEDIN,
            text, linkedinFeedData, 1 );

        responseWrapper.setUpdatedTime( updatedDate  );
        responseWrapper.setCreatedTime( updatedDate );

        responseWrapper.setHash( responseWrapper.getText().hashCode() );
        responseWrapper.setPostId(id);
        return responseWrapper;
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
