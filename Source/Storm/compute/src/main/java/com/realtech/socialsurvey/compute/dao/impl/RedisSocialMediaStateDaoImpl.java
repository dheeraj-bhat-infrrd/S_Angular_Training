package com.realtech.socialsurvey.compute.dao.impl;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;
import com.realtech.socialsurvey.compute.common.RedisDB;
import com.realtech.socialsurvey.compute.common.RedisKeyConstants;
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

    private static final int waitForNextFetchTime = Integer.parseInt( LocalPropertyFileHandler.getInstance()
        .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.MEDIA_TOKENS_FETCH_TIME_INTERVAL )
        .orElse( "3600" ) );


    @Override
    public boolean saveLastFetched( String key, String currValue, String prevValue )
    {
        if ( currValue == null || prevValue == null ) {
            throw new IllegalArgumentException( "currValue/prevValue cann't be null" );
        }

        Jedis jedis = RedisDB.getPoolInstance().getResource();
        try {
            if ( jedis != null ) {
                jedis.hset( key, RedisKeyConstants.CURRENT, currValue );
                jedis.hset( key, RedisKeyConstants.PREVIOUS, prevValue );
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
                String prevValue = jedis.hget( key, RedisKeyConstants.PREVIOUS );
                jedis.hset( key, RedisKeyConstants.CURRENT, prevValue );
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

        try ( Jedis jedis = RedisDB.getPoolInstance().getResource(); ) {
            LOG.info( "Executing method getLastFetched {}", key );
            // Return all keywords 
            return jedis.hget( key, RedisKeyConstants.CURRENT );
        }
    }


    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao#waitForNextFetch()
     */
    @Override
    public boolean waitForNextFetch()
    {
        LOG.info( "Executing method getLastFetched waitForNextFetch" );
        try ( Jedis jedis = RedisDB.getPoolInstance().getResource(); ) {
            
            if ( jedis.exists( RedisKeyConstants.WAIT_FOR_NEXT_FETCH ) 
                || jedis.exists( RedisKeyConstants.IS_KAFKA_DOWN )) {
                return true;
            } else {
                jedis.setex( RedisKeyConstants.WAIT_FOR_NEXT_FETCH, waitForNextFetchTime, "true" );
                return false;
            }
        }
    }


    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao#setWaitForNextFetch(int)
     */
    @Override
    public boolean addWithExpire( String key, String value, int seconds )
    {
        LOG.info( "Executing method getLastFetched addWithExpire" );
        try ( Jedis jedis = RedisDB.getPoolInstance().getResource(); ) {
            jedis.setex( key, seconds, value );
            return false;
        }
    }
    
    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao#setWaitForNextFetch(int)
     */
    @Override
    public long getTTLForKey( String key )
    {
        LOG.info( "Executing method getLastFetched getTTLForKey" );
        try ( Jedis jedis = RedisDB.getPoolInstance().getResource(); ) {
            return jedis.ttl( key );
        }
    }


    @Override
    public boolean setWaitForNextFetch( int seconds )
    {
        // TODO Auto-generated method stub
        return false;
    }
}
