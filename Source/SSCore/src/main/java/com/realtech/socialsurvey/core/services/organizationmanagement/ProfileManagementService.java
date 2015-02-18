package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.util.List;
import com.realtech.socialsurvey.core.entities.Achievement;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Association;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.Licenses;
import com.realtech.socialsurvey.core.entities.LockSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;

public interface ProfileManagementService {

	/**
	 * Finalize Lock settings in the hierarchy
	 * 
	 * @param user
	 * @param accountType
	 * @param settings
	 * @throws InvalidInputException
	 */
	public LockSettings finalizeHigherLockSettings(User user, AccountType accountType, UserSettings settings, long branchId, long regionId)
			throws InvalidInputException;

	/**
	 * Finalize profile settings in the hierarchy
	 * 
	 * @param user
	 * @param accountType
	 * @param settings
	 * @throws InvalidInputException
	 */
	public OrganizationUnitSettings finalizeProfile(User user, AccountType accountType, UserSettings settings, long agentId, long branchId,
			long regionId) throws InvalidInputException;

	
	// JIRA SS-97 by RM-06 : BOC
	/**
	 * Method to update logo of a company
	 * 
	 * @param collection
	 * @param companySettings
	 * @param logo
	 * @throws InvalidInputException
	 */
	public void updateLogo(String collection, OrganizationUnitSettings companySettings, String logo) throws InvalidInputException;

	public void updateProfileImage(String collection, OrganizationUnitSettings companySettings, String logo) throws InvalidInputException;

	public LockSettings updateLockSettings(String collection, OrganizationUnitSettings unitSettings,
			LockSettings lockSettings) throws InvalidInputException;
	/**
	 * Method to update company contact information
	 * 
	 * @param collection
	 * @param unitSettings
	 * @param contactDetailsSettings
	 * @return
	 * @throws InvalidInputException
	 */
	public ContactDetailsSettings updateContactDetails(String collection, OrganizationUnitSettings unitSettings,
			ContactDetailsSettings contactDetailsSettings) throws InvalidInputException;

	public ContactDetailsSettings updateAgentContactDetails(String collection, AgentSettings agentSettings,
			ContactDetailsSettings contactDetailsSettings) throws InvalidInputException;

	/**
	 * Method to add an association
	 * 
	 * @param collection
	 * @param unitSettings
	 * @param associationList
	 * @return
	 * @throws InvalidInputException
	 */
	public List<Association> addAssociations(String collection, OrganizationUnitSettings unitSettings, List<Association> associations)
			throws InvalidInputException;

	public List<Association> addAgentAssociations(String collection, AgentSettings agentSettings, List<Association> associations)
			throws InvalidInputException;

	/**
	 * Method to add an achievement
	 * 
	 * @param collection
	 * @param unitSettings
	 * @param achievements
	 * @return
	 * @throws InvalidInputException
	 */
	public List<Achievement> addAchievements(String collection, OrganizationUnitSettings unitSettings, List<Achievement> achievements)
			throws InvalidInputException;

	public List<Achievement> addAgentAchievements(String collection, AgentSettings agentSettings, List<Achievement> achievements)
			throws InvalidInputException;

	/**
	 * Method to add licence details
	 * 
	 * @param collection
	 * @param unitSettings
	 * @param authorisedIn
	 * @param licenseDisclaimer
	 * @return
	 * @throws InvalidInputException
	 */
	public Licenses addLicences(String collection, OrganizationUnitSettings unitSettings, List<String> authorisedIn) throws InvalidInputException;

	public Licenses addAgentLicences(String collection, AgentSettings agentSettings, List<String> authorisedIn) throws InvalidInputException;

	/**
	 * Method to update social media tokens in profile
	 * 
	 * @param collection
	 * @param unitSettings
	 * @param mediaTokens
	 * @throws InvalidInputException
	 */
	public void updateSocialMediaTokens(String collection, OrganizationUnitSettings unitSettings, SocialMediaTokens mediaTokens)
			throws InvalidInputException;

	/**
	 * Method to fetch all users under the specified branch of specified company
	 * 
	 * @param companyProfileName
	 * @param branchProfileName
	 * @return
	 * @throws InvalidInputException
	 */
	public List<AgentSettings> getIndividualsForBranch(String companyProfileName, String branchProfileName) throws InvalidInputException;

	/**
	 * Method to fetch all users under the specified branch of specified company
	 * 
	 * @param branchId
	 * @return
	 * @throws InvalidInputException
	 */
	public List<AgentSettings> getIndividualsByBranchId(long branchId) throws InvalidInputException;

	/**
	 * Method to fetch all users under the specified region of specified company
	 * 
	 * @param companyProfileName
	 * @param regionProfileName
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public List<AgentSettings> getIndividualsForRegion(String companyProfileName, String regionProfileName) throws InvalidInputException,
			NoRecordsFetchedException;

	/**
	 * Method to fetch all individuals directly linked to a company
	 * 
	 * @param companyProfileName
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public List<AgentSettings> getIndividualsForCompany(String companyProfileName) throws InvalidInputException, NoRecordsFetchedException;

	/**
	 * Method to get the region profile based on region and company profile name
	 * 
	 * @param companyProfileName
	 * @param regionProfileName
	 * @return
	 * @throws InvalidInputException
	 */
	public OrganizationUnitSettings getRegionByProfileName(String companyProfileName, String regionProfileName) throws InvalidInputException;

	/**
	 * Method to get the branch profile based on branch and company profile name
	 * 
	 * @param companyProfileName
	 * @param branchProfileName
	 * @return
	 * @throws InvalidInputException
	 */
	public OrganizationUnitSettings getBranchByProfileName(String companyProfileName, String branchProfileName) throws InvalidInputException;

	/**
	 * JIRA SS-117 by RM-02 Method to fetch company profile when profile name is provided
	 * 
	 * @param collection
	 * @param companySettings
	 * @param logo
	 * @throws InvalidInputException
	 */
	public OrganizationUnitSettings getCompanyProfileByProfileName(String profileName) throws InvalidInputException;

	/**
	 * Method to get profile of an individual
	 * 
	 * @param companyProfileName
	 * @param profileName
	 * @return
	 * @throws InvalidInputException
	 */
	public OrganizationUnitSettings getIndividualByProfileName(String companyProfileName, String profileName) throws InvalidInputException;

	/**
	 * Method to get aggregated reviews of all agents of a company
	 * 
	 * @param companyId
	 * @return
	 * @throws InvalidInputException
	 */
	public List<SurveyDetails> getReviewsForCompany(long companyId) throws InvalidInputException;

	/**
	 * Method to get the average rating of all individuals of a company
	 * 
	 * @param companyId
	 * @return
	 * @throws InvalidInputException
	 */
	public double getAverageRatingForCompany(long companyId) throws InvalidInputException;
}
