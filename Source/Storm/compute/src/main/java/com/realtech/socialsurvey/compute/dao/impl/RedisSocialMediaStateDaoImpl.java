package com.realtech.socialsurvey.compute.dao.impl;

import com.realtech.socialsurvey.compute.common.RedisDB;
import com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;


/**
 * Class to save and retrieve last fetched since time/id
 * @author manish
 *
 */
public class RedisSocialMediaStateDaoImpl implements RedisSocialMediaStateDao
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( RedisCompanyKeywordsDaoImpl.class );
    private static final String PREVIOUS = "previous";
    private static final String CURRENT = "current";
    private static final String TWITTER_LOCK = "twitterLock:";


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

    @Override
    public void setTwitterLock(int secondsUntilReset, String pageId) {
        LOG.info("Setting twitter lock on pageId {}", pageId);
        try(Jedis jedis = RedisDB.getPoolInstance().getResource()) {
            jedis.setex(TWITTER_LOCK+pageId, secondsUntilReset,"lock"+pageId);
        }
    }

    @Override
    public boolean isTwitterLockSet(String pageId) {
        try(Jedis jedis = RedisDB.getPoolInstance().getResource()) {
           return jedis.exists(TWITTER_LOCK+pageId);
        }
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
