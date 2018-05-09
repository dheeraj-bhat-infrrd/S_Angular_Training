package com.realtech.socialsurvey.core.dao.impl;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.dao.RedisDao;
import com.realtech.socialsurvey.core.entities.Keyword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@Transactional
public class RedisDaoImpl implements RedisDao, InitializingBean {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource(name = "stringRedisTemplate")
    HashOperations hashOps;

    private static final Logger LOGGER = LoggerFactory.getLogger( RedisDaoImpl.class );
    private static final String COMPANYKEYWORDS_KEY_PREFIX = "companykeywords:";
    private static final String TWITTER_LOCK = "twitterLock";
    private static final String FACEBOOK_LOCK = "facebookLock";
    private static final String SOCIAL_MONITOR_COMPANYIDS = "socialMonitorEnabledCompanyIds";
    private static final String COMPANYIDS = "companyIds";


    @Override
    public void addKeywords(long companyId, List<Keyword> keywords) {
        LOGGER.info("Trying to add keywords {} for comapnyId {} to redis ", keywords, companyId);
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", new Gson().toJson(keywords));
        map.put("modifiedOn", String.valueOf(System.currentTimeMillis()));
        hashOps.putAll(COMPANYKEYWORDS_KEY_PREFIX + companyId, map);
    }


    @Override
    public Map<String, Long> getFacebookLock()
    {
        LOGGER.info( "Trying to get keys with facebook lock" );
        Set<String> keys = redisTemplate.keys( "*" + FACEBOOK_LOCK + "*" );
        Map<String, Long> locks=new HashMap<>();
        for (String key:keys) {
            locks.put( key, redisTemplate.getExpire( key ) );
        }
        
        return locks;
    }
    
    @Override
    public Map<String, Long> getTwitterLock()
    {
        LOGGER.info( "Trying to get keys with twitter lock" );
        Set<String> keys = redisTemplate.keys( "*" + TWITTER_LOCK + "*" );
        Map<String, Long> locks=new HashMap<>();
        for (String key:keys) {
            locks.put( key, redisTemplate.getExpire( key ) );
        }
        
        return locks;
    }
    

    @Override
    public void addCompanyIdsForSM( long companyId )
    {
        LOGGER.info( "Trying to add SocialMonitor enabled comapnyIds to redis ", companyId );

        List<Long> companyIds = (List<Long>) hashOps.get( SOCIAL_MONITOR_COMPANYIDS, COMPANYIDS );
        if ( companyIds == null || companyIds.isEmpty() ) {
            companyIds = Arrays.asList( companyId );
            hashOps.put( SOCIAL_MONITOR_COMPANYIDS, COMPANYIDS, new Gson().toJson( companyIds ) );
        } else {
            if ( !companyIds.contains( companyId ) ) {
                companyIds.add( companyId );
                hashOps.put( SOCIAL_MONITOR_COMPANYIDS, COMPANYIDS, new Gson().toJson( companyIds ) );
            }
        }


    }
    

    @Override
    public void afterPropertiesSet() throws Exception {
        redisTemplate.getConnectionFactory().getConnection().ping();
    }

}
