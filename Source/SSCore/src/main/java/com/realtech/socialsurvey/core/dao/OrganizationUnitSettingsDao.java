package com.realtech.socialsurvey.core.dao;

import java.util.List;
import com.realtech.socialsurvey.core.entities.IndividualSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.User;

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
	 * Inserts individual settings
	 * 
	 * @param user
	 */
	public void insertIndividualSettings(User user);

	/**
	 * Fetches the organization unit settings for the given identifier and collection name
	 * 
	 * @param identifier
	 * @param collectionName
	 * @return
	 */
	public OrganizationUnitSettings fetchOrganizationUnitSettingsById(long identifier, String collectionName);

	/**
	 * Returns the individual settings
	 * 
	 * @param identitifier
	 * @return
	 */
	public IndividualSettings fetchIndividualSettingsById(long identifier);

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
	 * @param individualSettings
	 * @param collectionName
	 */
	public void updateParticularKeyIndividualSettings(String keyToUpdate, Object updatedRecord, IndividualSettings individualSettings,
			String collectionName);

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
}