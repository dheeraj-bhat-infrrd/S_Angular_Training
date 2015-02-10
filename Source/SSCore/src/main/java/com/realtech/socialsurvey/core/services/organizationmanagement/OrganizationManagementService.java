package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.util.List;
import java.util.Map;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchSettings;
import com.realtech.socialsurvey.core.entities.CRMInfo;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.MailContentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

public interface OrganizationManagementService {

	public User addCompanyInformation(User user, Map<String, String> organizationalDetails) throws SolrException, InvalidInputException;

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

	/**
	 * Method called to update databases on plan upgrade
	 * 
	 * @param company
	 * @param newAccountsMasterPlanId
	 * @throws NoRecordsFetchedException
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	public void upgradeAccount(Company company, int newAccountsMasterPlanId) throws NoRecordsFetchedException, InvalidInputException, SolrException;

	/**
	 * Method to fetch the verticals master list
	 * 
	 * @return
	 */
	public List<VerticalsMaster> getAllVerticalsMaster() throws InvalidInputException;

	// JIRA SS-97 by RM-06 : EOC

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
	 * Method to fetch all regions of a company
	 * 
	 * @param companyProfileName
	 * @return
	 * @throws InvalidInputException
	 */
	public List<Region> getRegionsForCompany(String companyProfileName) throws InvalidInputException;

	/**
	 * Method to get list of branches directly linked to a company
	 * 
	 * @param companyProfileName
	 * @return
	 * @throws InvalidInputException
	 */
	public List<Branch> getBranchesUnderCompany(String companyProfileName) throws InvalidInputException;

	/**
	 * Method to get list of branches linked to a region
	 * 
	 * @param companyProfileName
	 * @param regionProfileName
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException 
	 */
	public List<Branch> getBranchesForRegion(String companyProfileName, String regionProfileName) throws InvalidInputException, NoRecordsFetchedException;
}
