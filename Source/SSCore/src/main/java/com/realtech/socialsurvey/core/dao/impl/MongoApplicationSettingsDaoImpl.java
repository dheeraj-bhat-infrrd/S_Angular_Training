package com.realtech.socialsurvey.core.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.realtech.socialsurvey.core.dao.MongoApplicationSettingsDao;
import com.realtech.socialsurvey.core.entities.ApplicationSettings;

/**
 * @author sandra
 *
 */
@Repository
public class MongoApplicationSettingsDaoImpl implements MongoApplicationSettingsDao {

	private static final Logger LOG = LoggerFactory.getLogger(MongoOrganizationUnitSettingDaoImpl.class);

	public static final String APPLICATION_SETTINGS_COLLECTION = "APPLICATION_SETTINGS";

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public ApplicationSettings getApplicationSettings() {
		Query query = new Query();
		query.limit(1);
		ApplicationSettings applicationSetting = mongoTemplate.findOne(query,ApplicationSettings.class, APPLICATION_SETTINGS_COLLECTION);
		return applicationSetting;
	}
	
}
