package com.realtech.socialsurvey.core.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;

/**
 * Mongo implementation of settings
 *
 */
@Repository
public class MongoOrganizationUnitSettingDaoImpl implements OrganizationUnitSettingsDao, InitializingBean {
	
	public static final String COMPANY_SETTINGS_COLLECTION = "COMPANY_SETTINGS";
	public static final String REGION_SETTINGS_COLLECTION = "REGION_SETTINGS";
	public static final String BRANCH_SETTINGS_COLLECTION = "BRANCH_SETTINGS";
	public static final String AGENT_SETTINGS_COLLECTION = "AGENT_SETTINGS";
	
	private static final Logger LOG = LoggerFactory.getLogger(MongoOrganizationUnitSettingDaoImpl.class);
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public void insertOrganizationUnitSettings(OrganizationUnitSettings organizationUnitSettings, String collectionName) {
		LOG.info("Creating "+collectionName+" document. Organiztion Unit id: "+organizationUnitSettings.getIden());
		LOG.debug("Inserting into "+collectionName+". Object: "+organizationUnitSettings.toString());
		mongoTemplate.insert(organizationUnitSettings, collectionName);
		LOG.info("Inserted into "+collectionName);
	}

	@Override
	public void insertAgentSettings(AgentSettings agentSettings) {
		LOG.info("Inseting agent settings. Agent id: "+agentSettings.getIden());
		LOG.debug("Inserting agent settings: "+agentSettings.toString());
		mongoTemplate.insert(agentSettings, AGENT_SETTINGS_COLLECTION);
		LOG.info("Inserted into agent settings");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("Checking if collections are created in mongodb");
		if(!mongoTemplate.collectionExists(COMPANY_SETTINGS_COLLECTION)){
			LOG.info("Creating "+COMPANY_SETTINGS_COLLECTION);
			mongoTemplate.createCollection(COMPANY_SETTINGS_COLLECTION);
		}
		if(!mongoTemplate.collectionExists(REGION_SETTINGS_COLLECTION)){
			LOG.info("Creating "+REGION_SETTINGS_COLLECTION);
			mongoTemplate.createCollection(REGION_SETTINGS_COLLECTION);
		}
		if(!mongoTemplate.collectionExists(BRANCH_SETTINGS_COLLECTION)){
			LOG.info("Creating "+BRANCH_SETTINGS_COLLECTION);
			mongoTemplate.createCollection(BRANCH_SETTINGS_COLLECTION);
		}
		if(!mongoTemplate.collectionExists(AGENT_SETTINGS_COLLECTION)){
			LOG.info("Creating "+AGENT_SETTINGS_COLLECTION);
			mongoTemplate.createCollection(AGENT_SETTINGS_COLLECTION);
		}
	}

}
