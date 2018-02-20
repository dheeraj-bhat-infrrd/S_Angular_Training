package com.realtech.socialsurvey.compute.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


/**
 * @author manish
 *
 */
public class RedisDB
{
    private static final Logger LOG = LoggerFactory.getLogger( RedisDB.class );
    private static JedisPool pool;
    private static String host;
    private static int port; // 6379 for NonSSL, 6380 for SSL
    private static int operationTimeout = 10000;
    private static JedisPoolConfig config;


    // To avoid class instantiation.
    private RedisDB()
    {}

    static {
        host = LocalPropertyFileHandler.getInstance()
            .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.REDIS_HOST ).orElse( null );
        port = Integer.parseInt( LocalPropertyFileHandler.getInstance()
            .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.REDIS_PORT ).orElse( null ) );
    }


    public static synchronized JedisPool getPoolInstance()
    {
        if ( pool == null ) {
            LOG.debug( "Creating jedis instance with {}:{}", host, port );
            JedisPoolConfig poolConfig = getPoolConfig();
            pool = new JedisPool( poolConfig, host, port );
        }
        return pool;
    }


    public static JedisPoolConfig getPoolConfig()
    {
        if ( config == null ) {
            JedisPoolConfig poolConfig = new JedisPoolConfig();

            int maxConnections = 200;
            poolConfig.setMaxTotal( maxConnections );
            poolConfig.setMaxIdle( maxConnections );

            poolConfig.setBlockWhenExhausted( true );

            // How long to wait before throwing when pool is exhausted
            poolConfig.setMaxWaitMillis( operationTimeout );

            poolConfig.setMinIdle( 50 );

            config = poolConfig;
        }

        return config;
    }
}
