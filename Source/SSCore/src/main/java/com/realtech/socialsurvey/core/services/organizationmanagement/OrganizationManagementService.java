package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchFromSearch;
import com.realtech.socialsurvey.core.entities.BranchSettings;
import com.realtech.socialsurvey.core.entities.CRMInfo;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.DisabledAccount;
import com.realtech.socialsurvey.core.entities.MailContentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RegionFromSearch;
import com.realtech.socialsurvey.core.entities.StateLookup;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.VerticalCrmMapping;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

public interface OrganizationManagementService {

	public User addCompanyInformation(User user, Map<String, String> organizationalDetails) throws SolrException, InvalidInputException;

	public AccountType addAccountTypeForCompany(User user, String accountType) throws InvalidInputException, SolrException;

	public long fetchAccountTypeMasterIdForCompany(Company company) throws InvalidInputException;

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
	 * Gets the company settings of the companyId.
	 * 
	 * @param companyId
	 * @return company settings
	 * @throws InvalidInputException
	 */
	public OrganizationUnitSettings getCompanySettings(long companyId) throws InvalidInputException;

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
	public void updateCRMDetails(OrganizationUnitSettings companySettings, CRMInfo crmInfo, String fullyQualifiedClass) throws InvalidInputException;

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
	 * @param mailSubject
	 * @param mailBody
	 * @param mailCategory
	 * @return
	 * @throws InvalidInputException
	 */
	public MailContentSettings updateSurveyParticipationMailBody(OrganizationUnitSettings companySettings, String mailSubject, String mailBody,
			String mailCategory) throws InvalidInputException;

	/**
	 * Adds a Disabled Account record in the database
	 * 
	 * @param companyId
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws PaymentException
	 */
	public void addDisabledAccount(long companyId, boolean forceDisable) throws InvalidInputException, NoRecordsFetchedException, PaymentException;

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
	 * Method to fetch all regions of a company
	 * 
	 * @param companyProfileName
	 * @return
	 * @throws InvalidInputException
	 */
	public List<Region> getRegionsForCompany(String companyProfileName) throws InvalidInputException, ProfileNotFoundException;

	/**
	 * Method to get list of branches directly linked to a company
	 * 
	 * @param companyProfileName
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public List<Branch> getBranchesUnderCompany(String companyProfileName) throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException;

	/**
	 * Method to get list of branches linked to a region
	 * 
	 * @param companyProfileName
	 * @param regionProfileName
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public List<Branch> getBranchesForRegion(String companyProfileName, String regionProfileName) throws InvalidInputException,
			NoRecordsFetchedException, ProfileNotFoundException;

	/**
	 * Method to fetch the default branch associated with a region
	 * 
	 * @param regionId
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public Branch getDefaultBranchForRegion(long regionId) throws InvalidInputException, NoRecordsFetchedException;

	/**
	 * Method to get the default region of a company
	 * 
	 * @param company
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public Region getDefaultRegionForCompany(Company company) throws InvalidInputException, NoRecordsFetchedException;

	/**
	 * Method to get all branches under the region whose regionId is provided
	 * 
	 * @param regionId
	 * @return
	 * @throws InvalidInputException
	 */
	public List<Branch> getBranchesByRegionId(long regionId) throws InvalidInputException;

	/**
	 * Method to get all branches under the regions specified
	 * 
	 * @param regionIds
	 * @return
	 * @throws InvalidInputException
	 */
	public List<Branch> getBranchesByRegionIds(Set<Long> regionIds) throws InvalidInputException;

	/**
	 * Method to add a branch
	 * 
	 * @param user
	 * @param region
	 * @param branchName
	 * @param isDefaultBySystem
	 * @return
	 */
	public Branch addBranch(User user, Region region, String branchName, int isDefaultBySystem);

	/**
	 * Method to add a region
	 * 
	 * @param user
	 * @param isDefaultBySystem
	 * @param regionName
	 * @return
	 */
	public Region addRegion(User user, int isDefaultBySystem, String regionName);

	/**
	 * Method to add a new region and assign the user to the newly created region if userId or
	 * emailId is provided
	 * 
	 * @param user
	 * @param regionName
	 * @param isDefaultBySystem
	 * @param address1
	 * @param address2
	 * @param country
	 * @param countryCode
	 * @param state
	 * @param city
	 * @param zipcode
	 * @param selectedUserId
	 * @param emailIdsArray
	 * @param isAdmin
	 * @return
	 * @throws InvalidInputException
	 * @throws SolrException
	 * @throws NoRecordsFetchedException
	 * @throws UserAssignmentException
	 */
	public Region addNewRegionWithUser(User user, String regionName, int isDefaultBySystem, String address1, String address2, String country, String countryCode, String state, String city, String zipcode, long selectedUserId,
			String[] emailIdsArray, boolean isAdmin) throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException;

	/**
	 * Method to update a region and assign user if specified
	 * 
	 * @param user
	 * @param regionId
	 * @param regionName
	 * @param address1
	 * @param address2
	 * @param country
	 * @param countryCode
	 * @param state
	 * @param city
	 * @param zipcode
	 * @param selectedUserId
	 * @param emailIdsArray
	 * @param isAdmin
	 * @return
	 * @throws InvalidInputException
	 * @throws SolrException
	 * @throws NoRecordsFetchedException
	 * @throws UserAssignmentException
	 */
	public Region updateRegion(User user, long regionId, String regionName, String address1, String address2, String country, String countryCode, String state, String city, String zipcode, long selectedUserId,
			String[] emailIdsArray, boolean isAdmin) throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException;

	/**
	 * Method to update a branch and assign a user if specified
	 * 
	 * @param user
	 * @param branchId
	 * @param regionId
	 * @param branchName
	 * @param address1
	 * @param address2
	 * @param country
	 * @param countryCode
	 * @param state
	 * @param city
	 * @param zipcode
	 * @param selectedUserId
	 * @param emailIdsArray
	 * @param isAdmin
	 * @return
	 * @throws InvalidInputException
	 * @throws SolrException
	 * @throws NoRecordsFetchedException
	 * @throws UserAssignmentException
	 */
	public Branch updateBranch(User user, long branchId, long regionId, String branchName, String address1, String address2, String country, String countryCode, String state, String city, String zipcode, long selectedUserId,
			String[] emailIdsArray, boolean isAdmin) throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException;

	/**
	 * Method to assign a user to a region
	 * 
	 * @param adminUser
	 * @param regionId
	 * @param assigneeUser
	 * @param isAdmin
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws SolrException
	 */
	public void assignRegionToUser(User adminUser, long regionId, User assigneeUser, boolean isAdmin) throws InvalidInputException,
			NoRecordsFetchedException, SolrException;

	/**
	 *  Method to add a new branch anassigneeUserd assign the user to the newly created branch if
	 * userId or emailId is provided
	 * 
	 * @param user
	 * @param branchName
	 * @param regionId
	 * @param isDefaultBySystem
	 * @param address1
	 * @param address2
	 * @param country
	 * @param countryCode
	 * @param state
	 * @param city
	 * @param zipcode
	 * @param selectedUserId
	 * @param emailIdsArray
	 * @param isAdmin
	 * @return
	 * @throws InvalidInputException
	 * @throws SolrException
	 * @throws NoRecordsFetchedException
	 * @throws UserAssignmentException
	 */
	public Branch addNewBranchWithUser(User user, String branchName, long regionId, int isDefaultBySystem, String address1, String address2, String country, String countryCode, String state, String city, String zipcode,
			long selectedUserId, String[] emailIdsArray, boolean isAdmin) throws InvalidInputException, SolrException, NoRecordsFetchedException,
			UserAssignmentException;

	/**
	 * Method to assign a user to a branch
	 * 
	 * @param adminUser
	 * @param branchId
	 * @param regionId
	 * @param assigneeUser
	 * @param isAdmin
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws SolrException
	 */
	public void assignBranchToUser(User adminUser, long branchId, long regionId, User assigneeUser, boolean isAdmin) throws InvalidInputException,
			NoRecordsFetchedException, SolrException;

	/**
	 * Method to add a new user or assign existing user under a company/region or branch
	 * 
	 * @param adminUser
	 * @param selectedUserId
	 * @param branchId
	 * @param regionId
	 * @param emailIdsArray
	 * @param isAdmin
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws SolrException
	 * @throws UserAssignmentException
	 */
	public void addIndividual(User adminUser, long selectedUserId, long branchId, long regionId, String[] emailIdsArray, boolean isAdmin)
			throws InvalidInputException, NoRecordsFetchedException, SolrException, UserAssignmentException;

	/**
	 * Method to fetch all branches of a company
	 * 
	 * @param company
	 * @return
	 * @throws InvalidInputException
	 */
	public List<Branch> getAllBranchesForCompany(Company company) throws InvalidInputException;

	/**
	 * Method to fetch branches mapped to a region
	 * 
	 * @param regionId
	 * @return
	 * @throws InvalidInputException
	 */
	public List<Branch> getAllBranchesInRegion(long regionId) throws InvalidInputException;

	/**
	 * Method to fetch UserProfiles mapped to a branch
	 * 
	 * @param branchId
	 * @return
	 * @throws InvalidInputException
	 */
	public List<UserProfile> getAllUserProfilesInBranch(long branchId) throws InvalidInputException;

	/**
	 * Method to fetch count of branches mapped to a region
	 * 
	 * @param regionId
	 * @return
	 * @throws InvalidInputException
	 */
	public long getCountBranchesInRegion(long regionId) throws InvalidInputException;

	/**
	 * Method to fetch count of UserProfiles mapped to a branch
	 * 
	 * @param branchId
	 * @return
	 * @throws InvalidInputException
	 */
	public long getCountUsersInBranch(long branchId) throws InvalidInputException;

	/**
	 * Method to fetch all regions of a company
	 * 
	 * @param company
	 * @return
	 * @throws InvalidInputException
	 */
	public List<Region> getAllRegionsForCompany(Company company) throws InvalidInputException;

	/**
	 * Method to update status of a branch
	 * 
	 * @param user
	 * @param branchId
	 * @param status
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	public void updateBranchStatus(User user, long branchId, int status) throws InvalidInputException, SolrException;

	/**
	 * Method to update status of a region
	 * 
	 * @param user
	 * @param regionId
	 * @param status
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	public void updateRegionStatus(User user, long regionId, int status) throws InvalidInputException, SolrException;

	/**
	 * Method to check whether a branch addition is allowed for given account type and user
	 * 
	 * @param user
	 * @param accountType
	 * @return
	 * @throws InvalidInputException
	 */
	public boolean isBranchAdditionAllowed(User user, AccountType accountType) throws InvalidInputException;

	/**
	 * Method to check whether a region addition is allowed for given account type and user
	 * 
	 * @param user
	 * @param accountType
	 * @return
	 * @throws InvalidInputException
	 */
	public boolean isRegionAdditionAllowed(User user, AccountType accountType) throws InvalidInputException;

	/**
	 * Method to add a branch
	 * 
	 * @param user
	 * @param regionId
	 * @param isDefaultBySystem
	 * @param branchName
	 * @param address1
	 * @param address2
	 * @param country
	 * @param countryCode
	 * @param state
	 * @param city
	 * @param zipcode
	 * @return
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	public Branch addNewBranch(User user, long regionId, int isDefaultBySystem, String branchName, String address1, String address2, String country, String countryCode, String state, String city, String zipcode)
			throws InvalidInputException, SolrException;

	/**
	 * Method to add a new region
	 * 
	 * @param user
	 * @param regionName
	 * @param isDefaultBySystem
	 * @param address1
	 * @param address2
	 * @param country
	 * @param countryCode
	 * @param state
	 * @param city
	 * @param zipcode
	 * @return
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	public Region addNewRegion(User user, String regionName, int isDefaultBySystem, String address1, String address2, String country, String countryCode, String state, String city, String zipcode) throws InvalidInputException,
			SolrException;

	/**
	 * Method to update a branch
	 * 
	 * @param branchId
	 * @param regionId
	 * @param branchName
	 * @param branchAddress1
	 * @param branchAddress2
	 * @param user
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	public void updateBranch(long branchId, long regionId, String branchName, String branchAddress1, String branchAddress2, User user)
			throws InvalidInputException, SolrException;

	/**
	 * Method to check whether a user has privileges to build hierarchy
	 * 
	 * @param user
	 * @param accountType
	 * @return
	 */
	public boolean canBuildHierarchy(User user, AccountType accountType);

	/**
	 * Method to check whether a user has privileges to edit company information
	 * 
	 * @param user
	 * @param accountType
	 * @return
	 */
	public boolean canEditCompany(User user, AccountType accountType);

	/**
	 * Method to insert region settings into mongo
	 * 
	 * @param region
	 * @throws InvalidInputException
	 */
	public void insertRegionSettings(Region region) throws InvalidInputException;

	/**
	 * Method to insert branch settings into mongo
	 * 
	 * @param branch
	 * @throws InvalidInputException
	 */
	public void insertBranchSettings(Branch branch) throws InvalidInputException;

	/**
	 * Method to get the list of region ids for a user and profile master id specified
	 * 
	 * @param user
	 * @param profileMasterId
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public Set<Long> getRegionIdsForUser(User user, int profileMasterId) throws InvalidInputException, NoRecordsFetchedException;

	/**
	 * Method to get the list of branch ids for a user and profile master id specified
	 * 
	 * @param user
	 * @param profileMasterId
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public Set<Long> getBranchIdsForUser(User user, int profileMasterId) throws InvalidInputException, NoRecordsFetchedException;

	/**
	 * Method to get the list of all the company ids
	 */
	public Set<Company> getAllCompanies();

	public Map<Long, BranchFromSearch> fetchBranchesMapByCompany(long companyId) throws InvalidInputException, SolrException, MalformedURLException;

	public Map<Long, RegionFromSearch> fetchRegionsMapByCompany(long companyId) throws InvalidInputException, SolrException, MalformedURLException;

	/**
	 * Method to get the list of branches from solr which are directly assigned to the company
	 * 
	 * @param company
	 * @param start
	 * @param rows
	 * @return
	 * @throws NoRecordsFetchedException
	 * @throws SolrException
	 */
	public List<BranchFromSearch> getBranchesUnderCompanyFromSolr(Company company, int start) throws InvalidInputException,
			NoRecordsFetchedException, SolrException;

	/**
	 * Method to get the list of users from solr which are directly assigned to the company
	 * 
	 * @param company
	 * @param start
	 * @param rows
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws SolrException
	 */
	public List<UserFromSearch> getUsersUnderCompanyFromSolr(Company company, int start) throws InvalidInputException,
			NoRecordsFetchedException, SolrException;

	/**
	 * Method to get the list of users from solr which are directly assigned to the regions
	 * specified
	 * 
	 * @param regionIds
	 * @param start
	 * @param rows
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws SolrException
	 */
	public List<UserFromSearch> getUsersUnderRegionFromSolr(Set<Long> regionIds, int start, int rows) throws InvalidInputException,
			NoRecordsFetchedException, SolrException;
	
	public List<StateLookup> getUsStateList();
	
	public String getZipCodesByStateId(int stateId);
	
	public List<DisabledAccount> disableAccounts(Date maxDisableDate);

	public List<DisabledAccount> getAccountsForPurge(int graceSpan);

	public void purgeCompany(Company company) throws InvalidInputException, SolrException;

	public void updateCompany(Company company) throws DatabaseException;

	public List<VerticalCrmMapping> getCrmMapping(User user);

	public Map<Long, OrganizationUnitSettings> getSettingsMapWithLinkedinImage(
			String string);
	
	public SurveySettings retrieveDefaultSurveyProperties();
}