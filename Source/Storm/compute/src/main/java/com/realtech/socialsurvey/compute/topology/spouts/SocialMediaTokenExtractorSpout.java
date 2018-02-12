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

    private SpoutOutputCollector _collector;
    
    private static boolean isFetched; 

    @Override
    public void open( @SuppressWarnings ( "rawtypes") Map conf, TopologyContext context, SpoutOutputCollector collector )
    {
        super.open( conf, context, collector );
        this._collector = collector;
    }

    @Override
    public void nextTuple()
    {
        try {

            if(!isFetched){
                isFetched = true;
                Optional<List<SocialMediaTokenResponse>> mediaTokens = SSAPIOperations.getInstance().getMediaTokens();

                if ( mediaTokens.isPresent() ) {

                    for ( SocialMediaTokenResponse mediaToken : mediaTokens.get() ) {
                        Long companyId = mediaToken.getCompanyId();
                        
                        if ( mediaToken.getSocialMediaTokens() != null ) {
                            
                            _collector.emit( "FacebookStream", new Values(companyId.toString(),mediaToken) );
                            _collector.emit( "LinkedinStream", new Values(companyId.toString(),mediaToken) );
                            _collector.emit( "TwitterStream", new Values(companyId.toString(),mediaToken) );
                        }

                    }
                }
            }
            // End loop for companies
        } catch ( Exception e ) {
            LOG.error( "Error while fetching post from facebook.", e );
        }
    }

    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declareStream( "FacebookStream", new Fields( "companyId", "mediaToken" ) );
        declarer.declareStream( "TwitterStream", new Fields( "companyId", "mediaToken" ) );
        declarer.declareStream( "LinkedinStream", new Fields( "companyId", "mediaToken" ) );
    }
}
