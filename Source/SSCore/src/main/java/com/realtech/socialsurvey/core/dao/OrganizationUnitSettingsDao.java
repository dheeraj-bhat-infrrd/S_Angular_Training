package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;

/**
 * Gets the organization settings
 *
 */
public interface OrganizationUnitSettingsDao {

	/**
	 * Inserts an organization settings
	 * @param organizationUnitSettings
	 * @param collectionName
	 */
	public void insertOrganizationUnitSettings(OrganizationUnitSettings organizationUnitSettings, String collectionName);
	
	/**
	 * Inserts agent settings
	 * @param agentSettings
	 */
	public void insertAgentSettings(AgentSettings agentSettings);
	
	/**
	 * Fetches the organization unit settings for the given identifier and collection name
	 * @param identifier
	 * @param collectionName
	 * @return
	 */
	public OrganizationUnitSettings fetchOrganizationUnitSettingsById(long identifier, String collectionName);
	
	/**
	 * Returns the agent settings 
	 * @param identitifier
	 * @return
	 */
	public AgentSettings fetchAgentSettingsById(long identifier);
	
}
