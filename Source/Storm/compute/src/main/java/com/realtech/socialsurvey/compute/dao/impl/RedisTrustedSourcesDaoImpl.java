package com.realtech.socialsurvey.compute.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.compute.common.RedisDB;
import com.realtech.socialsurvey.compute.common.RedisKeyConstants;
import com.realtech.socialsurvey.compute.dao.RedisTrustedSourcesDao;
import com.realtech.socialsurvey.compute.entities.SocialMonitorTrustedSource;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisTrustedSourcesDaoImpl implements RedisTrustedSourcesDao {

	
	private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( RedisTrustedSourcesDaoImpl.class );
	
	 @Override
	    public List<SocialMonitorTrustedSource> getCompanyTruestedSourcesForCompanyId( long companyIden )
	    {
	        try ( Jedis jedis = RedisDB.getPoolInstance().getResource() ) {
	            LOG.info( "Executing method getCompanyTruestedSourcesForCompanyId {}", companyIden );
	            // Return all keywords 
	            String keywordsString = jedis.hget( RedisKeyConstants.COMPANY_TRUSTED_SOURCES_KEY_PREFIX + companyIden,RedisKeyConstants.TRUSTED_SOURCES );
	            return ConversionUtils.deserialize( keywordsString, new TypeToken<List<SocialMonitorTrustedSource>>() {}.getType() );
	        } catch ( JedisConnectionException e ) {
	            LOG.error( e.getMessage() );
	            return null;
	        }
	    }
}
