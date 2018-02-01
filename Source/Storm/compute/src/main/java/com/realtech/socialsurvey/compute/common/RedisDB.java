package com.realtech.socialsurvey.compute.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.compute.entities.Keyword;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;


/**
 * @author manish
 *
 */
public class RedisDB
{
    private static final Logger LOG = LoggerFactory.getLogger( RedisDB.class );
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 6379;
    public static final String COMPANYKEYWORDS_KEY_PREFIX = "companykeywords:";
    private static JedisPool pool;
    private static String host;
    private static int port; // 6379 for NonSSL, 6380 for SSL
    private static int operationTimeout = 10000;
    private static JedisPoolConfig config;

    static {
        host = LocalPropertyFileHandler.getInstance()
            .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.REDIS_HOST ).orElse( null );
        port =Integer.parseInt(LocalPropertyFileHandler.getInstance()
            .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.REDIS_PORT ).orElse( "" ));
    }


    public static synchronized JedisPool getPoolInstance()
    {
        if ( pool == null ) {
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

    public List<Keyword> getKeywordsFromRedis( long companyId )
    {
        Jedis jedis = getPoolInstance().getResource();
        // Return all keywords 
        String keywordsString = jedis.hget( COMPANYKEYWORDS_KEY_PREFIX + companyId, "keywords" );
        Gson gson = new Gson();
        return gson.fromJson( keywordsString, new TypeToken<List<Keyword>>() {}.getType() );
    }


    public long getKeywordModifiedOn( long companyId )
    {
        Jedis jedis = getPoolInstance().getResource();
        // Return all keywords 
        String modifiedOnString = jedis.hget( COMPANYKEYWORDS_KEY_PREFIX + companyId, "modifiedon" );
        return Long.parseLong( modifiedOnString );
    }


    public void saveKeywords( List<Keyword> keywords, long companyId )
    {
        String key = COMPANYKEYWORDS_KEY_PREFIX + companyId;
        Map<String, String> map = new HashMap<>();
        Gson gson = new Gson();
        String keywordsString = gson.toJson( keywords );
        map.put( "keywords", keywordsString );
        map.put( "modifiedon", Long.toString( System.currentTimeMillis() ) );
        Jedis jedis = getPoolInstance().getResource();
        try {
            jedis.hmset( key, map );
        } catch ( JedisException e ) {
            if ( jedis != null ) {
                jedis.close();
            }
        } finally {
            if ( null != jedis ) {
                jedis.close();
            }
        }
    }
    
    public static void main( String[] args )
    {
        RedisDB app = new RedisDB();
        System.out.println( app.getKeywordsFromRedis( 684L ) );;
        /*List<Keyword> keywords = new ArrayList<>();
        Keyword k1=  new Keyword();
        
        
        Keyword k2=  new Keyword();
        keywords.add( new Keyword() );
        app.saveKeywords( keywords, companyId );*/
    }
}
