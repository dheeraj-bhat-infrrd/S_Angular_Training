package com.realtech.socialsurvey.compute.topology.spouts;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao;
import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokensPaginated;
import com.realtech.socialsurvey.compute.enums.ProfileType;
import redis.clients.jedis.exceptions.JedisConnectionException;


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

    private RedisSocialMediaStateDao redisSocialMediaStateDao;
    
    private static final int PAGE_SIZE = 100;


    @Override
    public void open( @SuppressWarnings ( "rawtypes") Map conf, TopologyContext context, SpoutOutputCollector collector )
    {
        super.open( conf, context, collector );
        this._collector = collector;
        this.redisSocialMediaStateDao = new RedisSocialMediaStateDaoImpl();
    }


    @Override
    public void nextTuple()
    {
        try {
            // Check every 5 second for waitForNextFetch
            Utils.sleep( 5000 );
            if ( !redisSocialMediaStateDao.waitForNextFetch() ) {
                // get all media tokens
                int skipCount =0;
                Optional<SocialMediaTokensPaginated> mediaTokensResultPaginated = null;
                do {
                    mediaTokensResultPaginated = SSAPIOperations.getInstance()
                        .getMediaTokensPaginated( skipCount, PAGE_SIZE );
                    
                    if ( mediaTokensResultPaginated.isPresent() && mediaTokensResultPaginated.get().getTotalRecord() != 0 ) {
                        SocialMediaTokensPaginated mediaTokens = mediaTokensResultPaginated.get();

                        for ( SocialMediaTokenResponse companiesToken : mediaTokens.getCompaniesTokens() ) {
                            companiesToken.setProfileType( ProfileType.COMPANY );
                            emitSocialMediaTokensToStream( companiesToken.getIden(), companiesToken );
                        }

                        for ( SocialMediaTokenResponse regionsToken : mediaTokens.getRegionsTokens() ) {
                            Long companyId = mediaTokensResultPaginated.get().getRegionCompanyIdMap().get( regionsToken.getIden() );
                            regionsToken.setProfileType( ProfileType.REGION );
                            emitSocialMediaTokensToStream( companyId, regionsToken );
                        }

                        for ( SocialMediaTokenResponse branchesToken : mediaTokens.getBranchesTokens() ) {
                            Long companyId = mediaTokensResultPaginated.get().getBranchCompanyIdMap().get( branchesToken.getIden() );
                            branchesToken.setProfileType( ProfileType.BRANCH );
                            emitSocialMediaTokensToStream( companyId, branchesToken );
                        }

                        for ( SocialMediaTokenResponse agentsToken : mediaTokens.getAgentsTokens() ) {
                            Long companyId = mediaTokensResultPaginated.get().getAgentCompanyIdMap().get( agentsToken.getIden() );
                            agentsToken.setProfileType( ProfileType.AGENT );
                            emitSocialMediaTokensToStream( companyId, agentsToken );
                        }
                        
                        if(mediaTokensResultPaginated.get().getTotalRecord() < PAGE_SIZE) {
                            break;
                        } else {
                            skipCount += PAGE_SIZE;
                        }
                    }
                    Utils.sleep( 300 );
                } while(mediaTokensResultPaginated.isPresent());
            }
            // End loop for companies
        } catch ( JedisConnectionException jedisConnectionException ) {
            LOG.error( "Unbale to connect to redis {}", jedisConnectionException.getMessage() );
        }
        catch ( Exception e ) {
            LOG.error( "Error in SocialMediaTokenExtractorSpout.nextTuple()", e );
        }
    }
    

    private void emitSocialMediaTokensToStream( Long companyId, SocialMediaTokenResponse mediaToken )
    {
        if ( mediaToken.getSocialMediaTokens() != null && ( companyId != null ) ) {
            mediaToken.setCompanyId( companyId );
            List<Long> companyIdsWithSM = redisSocialMediaStateDao.getCompanyIdsForSM();
            if (companyIdsWithSM != null && !companyIdsWithSM.isEmpty() && companyIdsWithSM.contains( companyId ) ) {
                _collector.emit( "FacebookStream", new Values( companyId.toString(), mediaToken ) );
                //_collector.emit( "LinkedinStream", new Values( companyId.toString(), mediaToken ) );
                _collector.emit( "TwitterStream", new Values( companyId.toString(), mediaToken ) );
                _collector.emit( "InstagramStream", new Values( companyId.toString(), mediaToken ) );
            }
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
