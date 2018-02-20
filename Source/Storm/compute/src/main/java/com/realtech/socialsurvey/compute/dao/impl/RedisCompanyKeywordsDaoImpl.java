package com.realtech.socialsurvey.compute.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.compute.common.RedisDB;
import com.realtech.socialsurvey.compute.dao.RedisCompanyKeywordsDao;
import com.realtech.socialsurvey.compute.entities.Keyword;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;


/**
 * @author manish
 * DAO to fetch Keywords from redis
 */
public class RedisCompanyKeywordsDaoImpl implements RedisCompanyKeywordsDao
{
    private static final long serialVersionUID = 1L;

    private static final String MODIFIEDON = "modifiedOn";

    private static final String KEYWORDS = "keywords";

    private static final Logger LOG = LoggerFactory.getLogger( RedisCompanyKeywordsDaoImpl.class );

    public static final String COMPANYKEYWORDS_KEY_PREFIX = "companykeywords:";


    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.compute.dao.RedisCompanyKeywordsDao#getCompanyKeywordsForCompanyId(long)
     */
    @Override
    public List<Keyword> getCompanyKeywordsForCompanyId( long companyIden )
    {
        try ( Jedis jedis = RedisDB.getPoolInstance().getResource() ) {
            LOG.info( "Executing method getCompanyKeywordsForCompanyId {}", companyIden );
            // Return all keywords 
            String keywordsString = jedis.hget( COMPANYKEYWORDS_KEY_PREFIX + companyIden, KEYWORDS );
            return ConversionUtils.deserialize( keywordsString, new TypeToken<List<Keyword>>() {}.getType() );
        } catch ( JedisConnectionException e ) {
            LOG.error( e.getMessage() );
            return null;
        }

    }


    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.compute.dao.RedisCompanyKeywordsDao#getKeywordModifiedOn(long)
     */
    public long getKeywordModifiedOn( long companyId )
    {
        try ( Jedis jedis = RedisDB.getPoolInstance().getResource() ) {
            // Return all keywords 
            String modifiedOnString = jedis.hget( COMPANYKEYWORDS_KEY_PREFIX + companyId, MODIFIEDON );
            return Long.parseLong( modifiedOnString );
        } catch (JedisConnectionException e){
            LOG.error(e.getMessage());
            return 0;
        }
    }

}
