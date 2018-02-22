package com.realtech.socialsurvey.compute.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.RedisDB;
import com.realtech.socialsurvey.compute.dao.RedisSinceRecordFetchedDao;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;


/**
 * Class to save and retrieve last fetched since time/id
 * @author manish
 *
 */
public class RedisSinceRecordFetchedDaoImpl implements RedisSinceRecordFetchedDao
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( RedisCompanyKeywordsDaoImpl.class );
    private static final String PREVIOUS = "previous";
    private static final String CURRENT = "current";


    @Override
    public boolean saveLastFetched( String key, String currValue, String prevValue )
    {
        if(currValue == null || prevValue == null) {
            throw new IllegalArgumentException( "currValue/prevValue cann't be null" );
        }
        
        Jedis jedis = RedisDB.getPoolInstance().getResource();
        try {
            if ( jedis != null ) {
                jedis.hset( key, CURRENT, currValue );
                jedis.hset( key, PREVIOUS, prevValue );
                return true;
            }
        } catch ( JedisException e ) {
            LOG.warn( "Jedis exception", e );
            jedis.close();
        } finally {
            if ( null != jedis ) {
                jedis.close();
            }
        }
        return false;
    }


    /**
     * Reset lastFetched current to previous
     * @param key
     * @return
     */
    @Override
    public boolean resetLastFetched( String key )
    {
        Jedis jedis = RedisDB.getPoolInstance().getResource();
        try {
            if ( jedis != null ) {
                String prevValue = jedis.hget( key, PREVIOUS );
                jedis.hset( key, CURRENT, prevValue );
                return true;
            }
        } catch ( JedisException e ) {
            LOG.warn( "Jedis exception", e );
            jedis.close();
        } finally {
            if ( null != jedis ) {
                jedis.close();
            }
        }
        return false;
    }


    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.compute.dao.RedisSinceRecordFetchedDao#getLastFetched(java.lang.String)
     */
    @Override
    public String getLastFetched( String key )
    {
        
        try (Jedis jedis = RedisDB.getPoolInstance().getResource();){
            LOG.info( "Executing method getLastFetched {}", key );
            // Return all keywords 
            return jedis.hget( key, CURRENT );
        }
    }
}
