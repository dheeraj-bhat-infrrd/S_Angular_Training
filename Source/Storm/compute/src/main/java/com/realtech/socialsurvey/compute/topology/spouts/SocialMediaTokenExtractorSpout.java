package com.realtech.socialsurvey.compute.topology.spouts;

import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao;
import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


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

    SpoutOutputCollector _collector;

    private List<SocialMediaTokenResponse> mediaTokens;

    private RedisSocialMediaStateDao redisSocialMediaStateDao;

    @Override
    public void open( @SuppressWarnings ( "rawtypes") Map conf, TopologyContext context, SpoutOutputCollector collector )
    {
        super.open( conf, context, collector );
        this._collector = collector;
        this.mediaTokens = new ArrayList<>();
        this.redisSocialMediaStateDao = new RedisSocialMediaStateDaoImpl();
    }


    @Override
    public void nextTuple()
    {
        try {
            if ( !redisSocialMediaStateDao.waitForNextFetch() ) {
                // get all mediatokens
                Optional<List<SocialMediaTokenResponse>> mediaTokensResult = SSAPIOperations.getInstance().getMediaTokens();
                if ( mediaTokensResult.isPresent() ) {
                    this.mediaTokens = mediaTokensResult.get();
                }

                for ( SocialMediaTokenResponse mediaToken : mediaTokens ) {
                    Long companyId = mediaToken.getCompanyId();

                    if ( mediaToken.getSocialMediaTokens() != null ) {

                        _collector.emit( "FacebookStream", new Values( companyId.toString(), mediaToken ) );
                       // _collector.emit( "LinkedinStream", new Values( companyId.toString(), mediaToken ) );
                        _collector.emit( "TwitterStream", new Values( companyId.toString(), mediaToken ) );
                        _collector.emit( "InstagramStream", new Values(companyId.toString(), mediaToken));
                    }

                }
            }
            // End loop for companies
        } catch ( Exception e ) {
            LOG.error( "Error in SocialMediaTokenExtractorSpout.nextTuple()", e );
        }
    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declareStream( "FacebookStream", new Fields( COMPANY_ID, MEDIA_TOKEN ) );
        declarer.declareStream( "TwitterStream", new Fields( COMPANY_ID, MEDIA_TOKEN ) );
        //declarer.declareStream( "LinkedinStream", new Fields( COMPANY_ID, MEDIA_TOKEN ) );
        declarer.declareStream( "InstagramStream", new Fields( COMPANY_ID, MEDIA_TOKEN ) );
    }
}
