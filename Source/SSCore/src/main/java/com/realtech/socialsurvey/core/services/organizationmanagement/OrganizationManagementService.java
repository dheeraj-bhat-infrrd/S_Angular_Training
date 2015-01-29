package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.util.List;
import java.util.Map;
import com.realtech.socialsurvey.core.entities.Achievement;
import com.realtech.socialsurvey.core.entities.Association;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchSettings;
import com.realtech.socialsurvey.core.entities.CRMInfo;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.Licenses;
import com.realtech.socialsurvey.core.entities.MailContentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

public interface OrganizationManagementService {

	public User addCompanyInformation(User user, Map<String, String> organizationalDetails) throws SolrException;

	public AccountType addAccountTypeForCompany(User user, String accountType) throws InvalidInputException, SolrException;
	

	public long fetchAccountTypeMasterIdForCompany(Company company) throws InvalidInputException;

	public Branch addBranch(User user, Region region, String branchName, int isDefaultBySystem);

	public Region addRegion(User user, int isDefaultBySystem, String regionName);

	/**
	 * Edits the company information of the user. The user should have privileges to edit the
	 * company
	 * 
	 * @param user
	 */
	public void editCompanySettings(User user);

	/**
	 * Gets the company settings of the user.
	 * 
	 * @param user
	 * @return company settings
	 * @throws InvalidInputException
	 */
	public OrganizationUnitSettings getCompanySettings(User user) throws InvalidInputException;

	/**
	 * Gets the region settings of the list of user profiles.
	 * 
	 * @param userProfiles
	 * @return map of regions associated with the user profiles
	 * @throws InvalidInputException
	 */
	public Map<Long, OrganizationUnitSettings> getRegionSettingsForUserProfiles(List<UserProfile> userProfiles) throws InvalidInputException;

	/**
	 * Gets the branch settings of the list of user profiles.
	 * 
	 * @param userProfiles
	 * @return map of branches associated with the user profiles
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public Map<Long, OrganizationUnitSettings> getBranchSettingsForUserProfiles(List<UserProfile> userProfiles) throws InvalidInputException,
			NoRecordsFetchedException;

	/**
	 * Gets region settings for the user profile
	 * 
	 * @param userProfile
	 * @return
	 * @throws InvalidInputException
	 */
	public OrganizationUnitSettings getRegionSettings(long regionId) throws InvalidInputException;

	/**
	 * Method to fetch branch settings along with the required region settings of region to which
	 * the branch belongs
	 * 
	 * @param userProfile
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public BranchSettings getBranchSettings(long branchId) throws InvalidInputException, NoRecordsFetchedException;

	/**
	 * Updates the crm info in the settings
	 * 
	 * @param companySettings
	 * @param crmInfo
	 * @throws InvalidInputException
	 */
	public void updateCRMDetails(OrganizationUnitSettings companySettings, CRMInfo crmInfo) throws InvalidInputException;

	/**
	 * Updates the SurveySettings in the UserSettings
	 * 
	 * @param companySettings
	 * @param surveySettings
	 * @throws InvalidInputException
	 */
	public boolean updateSurveySettings(OrganizationUnitSettings companySettings, SurveySettings surveySettings) throws InvalidInputException;

	/**
	 * Updates the LocationEnbling in the settings
	 * 
	 * @param companySettings
	 * @param surveySettings
	 * @throws InvalidInputException
	 */
	public void updateLocationEnabled(OrganizationUnitSettings companySettings, boolean isLocationEnabled) throws InvalidInputException;

	/**
	 * Updates the AccountDisabled in the settings
	 * 
	 * @param companySettings
	 * @param surveySettings
	 * @throws InvalidInputException
	 */
	public void updateAccountDisabled(OrganizationUnitSettings companySettings, boolean isAccountDisabled) throws InvalidInputException;

	/**
	 * Updates the mail body content for company settings
	 * 
	 * @param companySettings
	 * @param mailBody
	 * @param mailCategory
	 * @return
	 * @throws InvalidInputException
	 */
	public MailContentSettings updateSurveyParticipationMailBody(OrganizationUnitSettings companySettings, String mailBody, String mailCategory)
			throws InvalidInputException;

	/**
	 * Adds a Disabled Account record in the database
	 * 
	 * @param companyId
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws PaymentException
	 */
	public void addDisabledAccount(long companyId) throws InvalidInputException, NoRecordsFetchedException, PaymentException;

	/**
	 * Soft deletes a Disabled Account record in the database
	 * 
	 * @param companyId
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws PaymentException
	 */
	public void deleteDisabledAccount(long companyId) throws InvalidInputException, NoRecordsFetchedException;

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
	public Licenses addLicences(String collection, OrganizationUnitSettings unitSettings, List<String> authorisedIn)
			throws InvalidInputException;
	
	/**
	 * Method to update social media tokens in profile
	 * 
	 * @param collection
	 * @param unitSettings
	 * @param mediaTokens
	 * @throws InvalidInputException
	 */
	public void updateSocialMediaTokens(String collection, OrganizationUnitSettings unitSettings, SocialMediaTokens mediaTokens) throws InvalidInputException;

	// JIRA SS-97 by RM-06 : EOC
}
