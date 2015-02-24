package com.realtech.socialsurvey.core.dao;

import java.util.List;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;

/**
 * Gets the organization settings
 */
public interface OrganizationUnitSettingsDao {

	/**
	 * Inserts an organization settings
	 * 
	 * @param organizationUnitSettings
	 * @param collectionName
	 */
	public void insertOrganizationUnitSettings(OrganizationUnitSettings organizationUnitSettings, String collectionName);

	/**
	 * Inserts agent settings
	 * 
	 * @param agentSettings
	 */
	public void insertAgentSettings(AgentSettings agentSettings);

	/**
	 * Fetches the organization unit settings for the given identifier and collection name
	 * 
	 * @param identifier
	 * @param collectionName
	 * @return
	 */
	public OrganizationUnitSettings fetchOrganizationUnitSettingsById(long identifier, String collectionName);

	/**
	 * Returns the agent settings
	 * 
	 * @param identitifier
	 * @return
	 */
	public AgentSettings fetchAgentSettingsById(long identifier);

	/**
	 * Updates a particular element in the collection
	 * 
	 * @param keyToUpdate
	 * @param updatedRecord
	 * @param unitSettings
	 * @param collectionName
	 */
	public void updateParticularKeyOrganizationUnitSettings(String keyToUpdate, Object updatedRecord, OrganizationUnitSettings unitSettings,
			String collectionName);

	/**
	 * Updates a particular element in the collection
	 * 
	 * @param keyToUpdate
	 * @param updatedRecord
	 * @param agentSettings
	 */
	public void updateParticularKeyAgentSettings(String keyToUpdate, Object updatedRecord, AgentSettings agentSettings);
	
	/**
	 * Fetchs the list of names of logos being used.
	 * 
	 * @return
	 */
	public List<String> fetchLogoList();

	/**
	 * Updates a particular key of organization unit settings based on criteria specified
	 * 
	 * @param keyToUpdate
	 * @param updatedRecord
	 * @param criteriaKey
	 * @param criteriaValue
	 * @param collectionName
	 */
	public void updateKeyOrganizationUnitSettingsByCriteria(String keyToUpdate, Object updatedRecord, String criteriaKey, Object criteriaValue,
			String collectionName);

	/**
	 * Method to fetch organization unit settings on the basis of profile name
	 * 
	 * @param profileName
	 * @param collectionName
	 * @return
	 */
	public OrganizationUnitSettings fetchOrganizationUnitSettingsByProfileName(String profileName, String collectionName);
	
	/**
	 *  Method to fetch organization unit settings on the basis of profile url
	 * @param profileUrl
	 * @param collectionName
	 * @return
	 */
	public OrganizationUnitSettings fetchOrganizationUnitSettingsByProfileUrl(String profileUrl, String collectionName);

}
