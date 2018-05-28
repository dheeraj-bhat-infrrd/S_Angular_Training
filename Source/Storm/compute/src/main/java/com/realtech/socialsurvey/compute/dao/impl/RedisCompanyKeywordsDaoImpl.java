package com.realtech.socialsurvey.compute.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.compute.common.RedisDB;
import com.realtech.socialsurvey.compute.common.RedisKeyConstants;
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
    private static final Logger LOG = LoggerFactory.getLogger( RedisCompanyKeywordsDaoImpl.class );

    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.compute.dao.RedisCompanyKeywordsDao#getCompanyKeywordsForCompanyId(long)
     */
    @Override
    public List<Keyword> getCompanyKeywordsForCompanyId( long companyIden )
    {
        try ( Jedis jedis = RedisDB.getPoolInstance().getResource() ) {
            LOG.info( "Executing method getCompanyKeywordsForCompanyId {}", companyIden );
            // Return all keywords 
            String keywordsString = jedis.hget( RedisKeyConstants.COMPANYKEYWORDS_KEY_PREFIX + companyIden,RedisKeyConstants.KEYWORDS );
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
            String modifiedOnString = jedis.hget(RedisKeyConstants.COMPANYKEYWORDS_KEY_PREFIX + companyId, RedisKeyConstants.MODIFIEDON );
            return StringUtils.isNotEmpty( modifiedOnString ) ? Long.parseLong( modifiedOnString ) : 0L;
        } catch (JedisConnectionException e){
            LOG.error(e.getMessage());
            return 0L;
        }
    }
    
}
