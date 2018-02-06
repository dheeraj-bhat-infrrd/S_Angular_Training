package com.realtech.socialsurvey.compute.dao.impl;

import java.io.Serializable;
import java.util.List;

import com.realtech.socialsurvey.compute.enums.SSApiBreakerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.compute.common.RedisDB;
import com.realtech.socialsurvey.compute.dao.RedisCompanyKeywordsDao;
import com.realtech.socialsurvey.compute.entities.Keyword;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;

import redis.clients.jedis.Jedis;


/**
 * @author manish
 * DAO to fetch Keywords from redis
 */
public class RedisCompanyKeywordsDaoImpl implements RedisCompanyKeywordsDao, Serializable
{
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( RedisCompanyKeywordsDaoImpl.class );


    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.compute.dao.RedisCompanyKeywordsDao#getCompanyKeywordsForCompanyId(long)
     */
    @Override
    public List<Keyword> getCompanyKeywordsForCompanyId( long companyIden )
    {
        Jedis jedis = null;
        try {
            LOG.info( "Executing method getCompanyKeywordsForCompanyId {}", companyIden );
            jedis = RedisDB.getPoolInstance().getResource();
            // Return all keywords 
            String keywordsString = jedis.hget( RedisDB.COMPANYKEYWORDS_KEY_PREFIX + companyIden, "keywords" );
            return ConversionUtils.deserialize( keywordsString, new TypeToken<List<Keyword>>() {}.getType() );
        } finally {
            if ( jedis != null ) {
                jedis.close();
            }
        }

    }


    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.compute.dao.RedisCompanyKeywordsDao#getKeywordModifiedOn(long)
     */
    public long getKeywordModifiedOn( long companyId )
    {
        Jedis jedis = null;
        try {
            jedis = RedisDB.getPoolInstance().getResource();
            // Return all keywords 
            String modifiedOnString = jedis.hget( RedisDB.COMPANYKEYWORDS_KEY_PREFIX + companyId, "modifiedon" );
            return Long.parseLong( modifiedOnString );
        } finally {
            if ( jedis != null ) {
                jedis.close();
            }
        }
    }

    public int getSSApiRetryCount(){
        try(Jedis jedis = RedisDB.getPoolInstance().getResource()){
            String retryCount = jedis.get(RedisDB.SSAPI_RETRY_COUNT);
            if(retryCount == null ){
                jedis.setnx(RedisDB.SSAPI_RETRY_COUNT, "0");
            }
            return Integer.parseInt(retryCount);
        }
    }

    @Override
    public void setSSApiBreakerStateKeys() {
        try(Jedis jedis = RedisDB.getPoolInstance().getResource()){
            //set the breaker_status, time and increment the retrycount
            jedis.set(RedisDB.SSAPI_BREAKER_STATUS, SSApiBreakerStatus.ON.name());

            if(jedis.get(RedisDB.SSAPI_BREAKER_ON_TIME) == null)
                jedis.setnx(RedisDB.SSAPI_BREAKER_ON_TIME, String.valueOf(System.currentTimeMillis()));
            else
                jedis.set(RedisDB.SSAPI_BREAKER_ON_TIME, String.valueOf(System.currentTimeMillis()));

            jedis.incr(RedisDB.SSAPI_RETRY_COUNT);
        }
    }

    @Override
    public void unsetSSApiBreakerStateKeys() {
        try(Jedis jedis = RedisDB.getPoolInstance().getResource()){
            //resets the breaker_status, time and increment the retrycount
            jedis.setnx(RedisDB.SSAPI_BREAKER_STATUS, SSApiBreakerStatus.OFF.name());
            jedis.set(RedisDB.SSAPI_BREAKER_ON_TIME, "0");
            jedis.set(RedisDB.SSAPI_RETRY_COUNT, "0");
        }
    }

    @Override
    public String getSSApiBreakerStatus() {
        try(Jedis jedis = RedisDB.getPoolInstance().getResource()){
            String breakerStatus = jedis.get(RedisDB.SSAPI_BREAKER_STATUS);
            if(breakerStatus == null ){
                jedis.setnx(RedisDB.SSAPI_BREAKER_STATUS, SSApiBreakerStatus.OFF.name());
            }
            return breakerStatus;
        }
    }


}
