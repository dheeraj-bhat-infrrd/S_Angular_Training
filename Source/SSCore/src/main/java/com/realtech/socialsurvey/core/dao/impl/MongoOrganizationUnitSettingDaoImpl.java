package com.realtech.socialsurvey.core.dao.impl;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import com.mongodb.BasicDBObject;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;

/**
 * Mongo implementation of settings
 */
@Repository
public class MongoOrganizationUnitSettingDaoImpl implements OrganizationUnitSettingsDao, InitializingBean {

	public static final String COMPANY_SETTINGS_COLLECTION = "COMPANY_SETTINGS";
	public static final String REGION_SETTINGS_COLLECTION = "REGION_SETTINGS";
	public static final String BRANCH_SETTINGS_COLLECTION = "BRANCH_SETTINGS";
	public static final String AGENT_SETTINGS_COLLECTION = "AGENT_SETTINGS";
	public static final String KEY_CRM_INFO = "crm_info";
	public static final String KEY_MAIL_CONTENT = "mail_content";
	public static final String KEY_SURVEY_SETTINGS = "survey_settings";
	public static final String KEY_LOCATION_ENABLED = "isLocationEnabled";
	public static final String KEY_ACCOUNT_DISABLED = "isAccountDisabled";
	public static final String KEY_CONTACT_DETAIL_SETTINGS = "contact_details";

	public static final String KEY_IDENTIFIER = "iden";

	private static final Logger LOG = LoggerFactory.getLogger(MongoOrganizationUnitSettingDaoImpl.class);

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void insertOrganizationUnitSettings(OrganizationUnitSettings organizationUnitSettings, String collectionName) {
		LOG.info("Creating " + collectionName + " document. Organiztion Unit id: " + organizationUnitSettings.getIden());
		LOG.debug("Inserting into " + collectionName + ". Object: " + organizationUnitSettings.toString());
		mongoTemplate.insert(organizationUnitSettings, collectionName);
		LOG.info("Inserted into " + collectionName);
	}

	@Override
	public void insertAgentSettings(AgentSettings agentSettings) {
		LOG.info("Inseting agent settings. Agent id: " + agentSettings.getIden());
		LOG.debug("Inserting agent settings: " + agentSettings.toString());
		mongoTemplate.insert(agentSettings, AGENT_SETTINGS_COLLECTION);
		LOG.info("Inserted into agent settings");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("Checking if collections are created in mongodb");
		if (!mongoTemplate.collectionExists(COMPANY_SETTINGS_COLLECTION)) {
			LOG.info("Creating " + COMPANY_SETTINGS_COLLECTION);
			mongoTemplate.createCollection(COMPANY_SETTINGS_COLLECTION);
			createIndexOnIden(COMPANY_SETTINGS_COLLECTION);
		}
		if (!mongoTemplate.collectionExists(REGION_SETTINGS_COLLECTION)) {
			LOG.info("Creating " + REGION_SETTINGS_COLLECTION);
			mongoTemplate.createCollection(REGION_SETTINGS_COLLECTION);
			createIndexOnIden(REGION_SETTINGS_COLLECTION);
		}
		if (!mongoTemplate.collectionExists(BRANCH_SETTINGS_COLLECTION)) {
			LOG.info("Creating " + BRANCH_SETTINGS_COLLECTION);
			mongoTemplate.createCollection(BRANCH_SETTINGS_COLLECTION);
			createIndexOnIden(BRANCH_SETTINGS_COLLECTION);
		}
		if (!mongoTemplate.collectionExists(AGENT_SETTINGS_COLLECTION)) {
			LOG.info("Creating " + AGENT_SETTINGS_COLLECTION);
			mongoTemplate.createCollection(AGENT_SETTINGS_COLLECTION);
			createIndexOnIden(AGENT_SETTINGS_COLLECTION);
		}
	}

	@Override
	public OrganizationUnitSettings fetchOrganizationUnitSettingsById(long identifier, String collectionName) {
		LOG.info("Fetch organization unit settings from " + collectionName + " for id: " + identifier);
		OrganizationUnitSettings settings = mongoTemplate.findOne(new BasicQuery(new BasicDBObject(KEY_IDENTIFIER, identifier)),
				OrganizationUnitSettings.class, collectionName);
		return settings;
	}

	@Override
	public AgentSettings fetchAgentSettingsById(long identifier) {
		LOG.info("Fetch agent settings from for id: " + identifier);
		AgentSettings settings = mongoTemplate.findOne(new BasicQuery(new BasicDBObject(KEY_IDENTIFIER, identifier)), AgentSettings.class,
				AGENT_SETTINGS_COLLECTION);
		return settings;
	}

	@Override
	public void updateParticularKeyOrganizationUnitSettings(String keyToUpdate, Object updatedRecord, OrganizationUnitSettings unitSettings,
			String collectionName) {
		LOG.info("Updating unit setting in " + collectionName + " with " + unitSettings + " for key: " + keyToUpdate + " wtih value: "
				+ updatedRecord);
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(unitSettings.getId()));
		Update update = new Update().set(keyToUpdate, updatedRecord);
		LOG.debug("Updating the unit settings");
		mongoTemplate.updateFirst(query, update, OrganizationUnitSettings.class, collectionName);
		LOG.info("Updated the unit setting");
	}
	
	/**
	 * Fetchs the list of names of logos being used.
	 * @return
	 */
	@Override
	public List<String> fetchLogoList() {
		
		LOG.info("Fetching the list of logos being used");		
		List<OrganizationUnitSettings> settingsList = mongoTemplate.findAll(OrganizationUnitSettings.class, COMPANY_SETTINGS_COLLECTION);
		List<String> logoList = new ArrayList<>();
		LOG.info("Preparing the list of logo names");
		for(OrganizationUnitSettings settings : settingsList){
			String logoName = settings.getLogo();
			if ( logoName != null && !logoName.isEmpty()){
				logoList.add(logoName);				
			}
		}
		
		LOG.info("Returning the list prepared!");
		return logoList;
	}
	
	/**
	 * Updates a particular key of organization unit settings based on criteria specified
	 */
	@Override
	public void updateKeyOrganizationUnitSettingsByCriteria(String keyToUpdate, Object updatedRecord, String criteriaKey, Object criteriaValue,
			String collectionName) {
		LOG.info("Method updateKeyOrganizationUnitSettingsByCriteria called in collection name :" + collectionName + " for keyToUpdate :"
				+ keyToUpdate + " criteria key :" + criteriaKey);
		Query query = new Query();
		query.addCriteria(Criteria.where(criteriaKey).is(criteriaValue));
		Update update = new Update().set(keyToUpdate, updatedRecord);
		LOG.debug("Updating unit settings based on criteria");
		mongoTemplate.updateMulti(query, update, OrganizationUnitSettings.class, collectionName);
		LOG.info("Successfully completed updation of unit settings");
	}

	// creates index on field 'iden'
	private void createIndexOnIden(String collectionName) {
		LOG.debug("Creating unique index on 'iden' for " + collectionName);
		mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on(KEY_IDENTIFIER, Sort.Direction.ASC).unique());
		LOG.debug("Index created");
	}

}
