package com.realtech.socialsurvey.compute.topology.spouts;

import java.util.ArrayList;
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

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;
import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;


/**
 * @author manish
 *
 */
public class SocialMediaTokenExtractorSpout extends BaseComputeSpout
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( SocialMediaTokenExtractorSpout.class );

    private static final String MEDIA_TOKEN = "mediaToken";
    private static final String COMPANY_ID = "companyId";
    
    private long waitTime;

    SpoutOutputCollector _collector;

    List<SocialMediaTokenResponse> mediaTokens;
    
    static int count = 0;


    @Override
    public void open( @SuppressWarnings ( "rawtypes") Map conf, TopologyContext context, SpoutOutputCollector collector )
    {
        super.open( conf, context, collector );
        this._collector = collector;
        this.mediaTokens = new ArrayList<>();
        this.waitTime = 1000L * Long.parseLong(LocalPropertyFileHandler.getInstance()
            .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.LINKED_IN_REST_API_URI ).orElse( "3600" ));
        //this.setMediaTokensPeriodically();
    }


    @Override
    public void nextTuple()
    {
        try {
            // get all mediatokens
            Optional<List<SocialMediaTokenResponse>> mediaTokensResult = SSAPIOperations.getInstance().getMediaTokens();
            if ( mediaTokensResult.isPresent() ) {
                this.mediaTokens = mediaTokensResult.get();
            }
            
            for ( SocialMediaTokenResponse mediaToken : mediaTokens ) {
                Long companyId = mediaToken.getCompanyId();

                if ( mediaToken.getSocialMediaTokens() != null ) {

                    _collector.emit( "FacebookStream", new Values( companyId.toString(), mediaToken ) );
                    _collector.emit( "LinkedinStream", new Values( companyId.toString(), mediaToken ) );
                    _collector.emit( "TwitterStream", new Values( companyId.toString(), mediaToken ) );
                }

            }
            
            Thread.sleep( waitTime );
            // End loop for companies
        } catch ( Exception e ) {
            LOG.error( "Error while fetching post from facebook.", e );
        }
    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declareStream( "FacebookStream", new Fields( COMPANY_ID, MEDIA_TOKEN ) );
        declarer.declareStream( "TwitterStream", new Fields( COMPANY_ID, MEDIA_TOKEN ) );
        declarer.declareStream( "LinkedinStream", new Fields( COMPANY_ID, MEDIA_TOKEN ) );
    }
}
