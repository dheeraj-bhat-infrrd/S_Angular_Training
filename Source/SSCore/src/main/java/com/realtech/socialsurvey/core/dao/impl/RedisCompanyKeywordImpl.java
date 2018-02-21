package com.realtech.socialsurvey.core.dao.impl;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.dao.RedisCompanyKeywordsDao;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class RedisCompanyKeywordImpl implements RedisCompanyKeywordsDao, InitializingBean {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource(name = "stringRedisTemplate")
    HashOperations hashOps;

    private static final Logger LOGGER = LoggerFactory.getLogger( RedisCompanyKeywordImpl.class );
    private static final String COMPANYKEYWORDS_KEY_PREFIX = "companykeywords:";

    @Override
    public void addKeywords(long companyId, List<Keyword> keywords) {
        LOGGER.info("Trying to add keywords {} for comapnyId {} to redis ", keywords, companyId);
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", new Gson().toJson(keywords));
        map.put("modifiedOn", String.valueOf(System.currentTimeMillis()));
        hashOps.putAll(COMPANYKEYWORDS_KEY_PREFIX + companyId, map);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        redisTemplate.getConnectionFactory().getConnection().ping();
    }
}
