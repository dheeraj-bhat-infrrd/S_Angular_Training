package com.realtech.socialsurvey.compute.topology.spouts;

/**
 * @author Lavanya
 */

import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao;
import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokensPaginated;
import com.realtech.socialsurvey.compute.enums.ProfileType;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Map;
import java.util.Optional;


/**
 * This spout fetches all the facebook token from compnay, region
 * and branch level. After which it'll emit to the bolts for
 * further processing.
 * */
public class FacebookTokenExtractorSpout extends BaseComputeSpout
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( FacebookTokenExtractorSpout.class );

    SpoutOutputCollector _collector;

    private RedisSocialMediaStateDao redisSocialMediaStateDao;

    private static final int PAGE_SIZE = 100;

    @Override
    public void open( @SuppressWarnings ( "rawtypes") Map conf, TopologyContext context, SpoutOutputCollector collector )
    {
        super.open( conf, context, collector );
        this._collector = collector;
        this.redisSocialMediaStateDao = new RedisSocialMediaStateDaoImpl();
    }

    @Override public void nextTuple()
    {
        try {
            // Check every 5 second for waitForNextFetch
            Utils.sleep( 5000 );
            if ( !redisSocialMediaStateDao.waitForNextFacebooktFetch() ) {
                // get all media tokens
                int skipCount = 0;
                Optional<SocialMediaTokensPaginated> mediaTokensResultPaginated ;
                do {
                    mediaTokensResultPaginated = SSAPIOperations.getInstance().getFbTokensPaginated( skipCount, PAGE_SIZE );

                    if ( mediaTokensResultPaginated.isPresent() && mediaTokensResultPaginated.get().getTotalRecord() != 0 ) {
                        SocialMediaTokensPaginated mediaTokens = mediaTokensResultPaginated.get();

                        for ( SocialMediaTokenResponse companiesToken : mediaTokens.getCompaniesTokens() ) {
                            companiesToken.setProfileType( ProfileType.COMPANY );
                            _collector.emit( new Values( companiesToken.getIden(), companiesToken ) );
                        }

                        for ( SocialMediaTokenResponse regionsToken : mediaTokens.getRegionsTokens() ) {
                            Long companyId = mediaTokensResultPaginated.get().getRegionCompanyIdMap().get( regionsToken.getIden() );
                            regionsToken.setProfileType( ProfileType.REGION );
                            _collector.emit( new Values( companyId, regionsToken ) );
                        }

                        for ( SocialMediaTokenResponse branchesToken : mediaTokens.getBranchesTokens() ) {
                            Long companyId = mediaTokensResultPaginated.get().getBranchCompanyIdMap().get( branchesToken.getIden() );
                            branchesToken.setProfileType( ProfileType.BRANCH );
                            _collector.emit( new Values( companyId, branchesToken ) );
                        }

                        for ( SocialMediaTokenResponse agentsToken : mediaTokens.getAgentsTokens() ) {
                            Long companyId = mediaTokensResultPaginated.get().getAgentCompanyIdMap().get( agentsToken.getIden() );
                            agentsToken.setProfileType( ProfileType.AGENT );
                            _collector.emit( new Values( companyId, agentsToken ) );
                        }

                        if ( mediaTokensResultPaginated.get().getTotalRecord() < PAGE_SIZE ) {
                            break;
                        } else {
                            skipCount += PAGE_SIZE;
                        }
                    }
                    Utils.sleep( 300 );
                } while ( mediaTokensResultPaginated.isPresent() );
            }
        } catch ( JedisConnectionException jedisConnectionException ) {
            LOG.error( "Unbale to connect to redis {}", jedisConnectionException.getMessage() );
        }
        catch ( Exception e ) {
            LOG.error( "Error in SocialMediaTokenExtractorSpout.nextTuple()", e );
        }
    }


    @Override public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "companyId", "facebookToken" ) );
    }
}
