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
	
}
