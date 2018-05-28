package com.realtech.socialsurvey.core.dao.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.dao.RedisDao;
import com.realtech.socialsurvey.core.entities.Keyword;
import com.realtech.socialsurvey.core.entities.SocialMonitorTrustedSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.lang.reflect.Type;
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
    private static final String COMPANY_TRUSTED_SOURCE_KEY_PREFIX = "companytrustedsources:";
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
    

    @SuppressWarnings("unchecked")
	@Override
    public void updateCompanyIdsForSM( long companyId, boolean isSocialMonitorEnabled )
    {
        LOGGER.info( "Trying to update SocialMonitor enabled companyIds to redis ", companyId );

        String companyIds = (String) hashOps.get( SOCIAL_MONITOR_COMPANYIDS, COMPANYIDS );
        Type listType = new TypeToken<List<Long>>() {}.getType();
        List<Long> companyIdList = new Gson().fromJson( companyIds, listType );
        if ( !isSocialMonitorEnabled ) {
            if ( companyIdList != null && !companyIdList.isEmpty() ) {
                if ( companyIdList.contains( companyId ) ) {
                    companyIdList.remove( companyId );
                    hashOps.put( SOCIAL_MONITOR_COMPANYIDS, COMPANYIDS, new Gson().toJson( companyIdList ) );
                }
            }
        } else {
            if ( companyIdList == null || companyIdList.isEmpty() ) {
                companyIdList = Arrays.asList( companyId );
                hashOps.put( SOCIAL_MONITOR_COMPANYIDS, COMPANYIDS, new Gson().toJson( companyIdList ) );
            } else {
                if ( !companyIdList.contains( companyId ) ) {
                    companyIdList.add( companyId );
                    hashOps.put( SOCIAL_MONITOR_COMPANYIDS, COMPANYIDS, new Gson().toJson( companyIdList ) );
                }
            }
        }

    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void addTruestedSources(long companyId, List<SocialMonitorTrustedSource> truestedSources) {
        LOGGER.info("Trying to add truestedSources {} for comapnyId {} to redis ", truestedSources, companyId);
        Map<String, Object> map = new HashMap<>();
        map.put("truestedSources", new Gson().toJson(truestedSources));
        map.put("modifiedOn", String.valueOf(System.currentTimeMillis()));
        hashOps.putAll(COMPANY_TRUSTED_SOURCE_KEY_PREFIX + companyId, map);
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        redisTemplate.getConnectionFactory().getConnection().ping();
    }


    

}
