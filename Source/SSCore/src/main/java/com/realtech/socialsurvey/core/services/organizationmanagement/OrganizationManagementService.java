
package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchFromSearch;
import com.realtech.socialsurvey.core.entities.BranchSettings;
import com.realtech.socialsurvey.core.entities.CRMInfo;
import com.realtech.socialsurvey.core.entities.CollectionDotloopProfileMapping;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyHiddenNotification;
import com.realtech.socialsurvey.core.entities.CompanyView;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.DisabledAccount;
import com.realtech.socialsurvey.core.entities.EncompassSdkVersion;
import com.realtech.socialsurvey.core.entities.FeedIngestionEntity;
import com.realtech.socialsurvey.core.entities.FilterKeywordsResponse;
import com.realtech.socialsurvey.core.entities.Keyword;
import com.realtech.socialsurvey.core.entities.HierarchySettingsCompare;
import com.realtech.socialsurvey.core.entities.LoopProfileMapping;
import com.realtech.socialsurvey.core.entities.MailContent;
import com.realtech.socialsurvey.core.entities.MailContentSettings;
import com.realtech.socialsurvey.core.entities.MultiplePhrasesVO;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfileImageUrlData;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RegionFromSearch;
import com.realtech.socialsurvey.core.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SocialMediaTokensPaginated;
import com.realtech.socialsurvey.core.entities.SocialMonitorTrustedSource;
import com.realtech.socialsurvey.core.entities.StateLookup;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.UploadValidation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.entities.UserHierarchyAssignments;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.VerticalCrmMapping;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionCancellationUnsuccessfulException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


/**
 * @author Ustav
 *
 */
/**
 * @author sandra
 *
 */
public interface OrganizationManagementService
{

    public User addCompanyInformation( User user, Map<String, String> organizationalDetails )
        throws SolrException, InvalidInputException;


    public AccountType addAccountTypeForCompany( User user, String accountType ) throws InvalidInputException, SolrException;


    public long fetchAccountTypeMasterIdForCompany( Company company ) throws InvalidInputException;


    /**
     * Edits the company information of the user. The user should have privileges to edit the
     * company
     * 
     * @param user
     */
    public void editCompanySettings( User user );


    /**
     * Gets the company settings of the user.
     * 
     * @param user
     * @return company settings
     * @throws InvalidInputException
     */
    public OrganizationUnitSettings getCompanySettings( User user ) throws InvalidInputException;


    /**
     * Gets the company settings of the companyId.
     * 
     * @param companyId
     * @return company settings
     * @throws InvalidInputException
     */
    public OrganizationUnitSettings getCompanySettings( long companyId ) throws InvalidInputException;


    /**
     * Gets the region settings of the list of user profiles.
     * 
     * @param userProfiles
     * @return map of regions associated with the user profiles
     * @throws InvalidInputException
     */
    public Map<Long, OrganizationUnitSettings> getRegionSettingsForUserProfiles( List<UserProfile> userProfiles )
        throws InvalidInputException;


    /**
     * Gets the branch settings of the list of user profiles.
     * 
     * @param userProfiles
     * @return map of branches associated with the user profiles
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public Map<Long, OrganizationUnitSettings> getBranchSettingsForUserProfiles( List<UserProfile> userProfiles )
        throws InvalidInputException, NoRecordsFetchedException;


    /**
     * Gets region settings for the user profile
     * 
     * @param userProfile
     * @return
     * @throws InvalidInputException
     */
    public OrganizationUnitSettings getRegionSettings( long regionId ) throws InvalidInputException;


    /**
     * Method to fetch branch settings along with the required region settings of region to which
     * the branch belongs
     * 
     * @param userProfile
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public BranchSettings getBranchSettings( long branchId ) throws InvalidInputException, NoRecordsFetchedException;


    public OrganizationUnitSettings getBranchSettingsDefault( long branchId )
        throws InvalidInputException, NoRecordsFetchedException;


    /**
     * Gets agent settings for the id passed
     * @param agentId
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public AgentSettings getAgentSettings( long agentId ) throws InvalidInputException, NoRecordsFetchedException;


    /**
     * Updates the crm info in the settings
     * 
     * @param companySettings
     * @param crmInfo
     * @throws InvalidInputException
     */
    public void updateCRMDetails( OrganizationUnitSettings companySettings, CRMInfo crmInfo, String fullyQualifiedClass )
        throws InvalidInputException;


    /**
     * Updates the SurveySettings in the UserSettings
     * 
     * @param companySettings
     * @param surveySettings
     * @throws InvalidInputException
     */
    public boolean updateSurveySettings( OrganizationUnitSettings companySettings, SurveySettings surveySettings )
        throws InvalidInputException;


    /**
     * Updates the LocationEnbling in the settings
     * 
     * @param companySettings
     * @param surveySettings
     * @throws InvalidInputException
     */
    public void updateLocationEnabled( OrganizationUnitSettings companySettings, boolean isLocationEnabled )
        throws InvalidInputException;


    /**
     * Updates the AccountDisabled in the settings
     * 
     * @param companySettings
     * @param surveySettings
     * @throws InvalidInputException
     */
    public void updateAccountDisabled( OrganizationUnitSettings companySettings, boolean isAccountDisabled )
        throws InvalidInputException;


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
    public MailContentSettings updateSurveyParticipationMailBody( OrganizationUnitSettings companySettings, String mailSubject,
        String mailBody, String mailCategory ) throws InvalidInputException;


    public MailContentSettings revertSurveyParticipationMailBody( OrganizationUnitSettings companySettings,
        String mailCategory ) throws NonFatalException;


    public ArrayList<String> getSurveyParamOrder( String category ) throws InvalidInputException;


    /**
     * Adds a Disabled Account record in the database
     * 
     * @param companyId
     * @return 
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     * @throws PaymentException
     */
    public DisabledAccount addDisabledAccount( long companyId, boolean forceDisable, long modifiedBy )
        throws InvalidInputException, NoRecordsFetchedException, PaymentException;


    /**
     * Soft deletes a Disabled Account record in the database
     * 
     * @param companyId
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     * @throws PaymentException
     */
    public void inactiveDisabledAccount( long companyId ) throws InvalidInputException, NoRecordsFetchedException;


    /**
     * Method called to update databases on plan upgrade
     * 
     * @param company
     * @param newAccountsMasterPlanId
     * @throws NoRecordsFetchedException
     * @throws InvalidInputException
     * @throws SolrException
     */
    public void upgradeAccount( Company company, int newAccountsMasterPlanId )
        throws NoRecordsFetchedException, InvalidInputException, SolrException;


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
    public List<Region> getRegionsForCompany( String companyProfileName )
        throws InvalidInputException, ProfileNotFoundException;


    /**
     * Method to get list of branches directly linked to a company
     * 
     * @param companyProfileName
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public List<Branch> getBranchesUnderCompany( String companyProfileName )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException;


    /**
     * Method to get list of branches linked to a region
     * 
     * @param companyProfileName
     * @param regionProfileName
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public List<Branch> getBranchesForRegion( String companyProfileName, String regionProfileName )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException;


    /**
     * Method to fetch the default branch associated with a region
     * 
     * @param regionId
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public Branch getDefaultBranchForRegion( long regionId ) throws InvalidInputException, NoRecordsFetchedException;


    /**
     * Method to get the default region of a company
     * 
     * @param company
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public Region getDefaultRegionForCompany( Company company ) throws InvalidInputException, NoRecordsFetchedException;


    /**
     * Method to get all branches under the region whose regionId is provided
     * 
     * @param regionId
     * @return
     * @throws InvalidInputException
     */
    public List<Branch> getBranchesByRegionId( long regionId ) throws InvalidInputException;


    /**
     * Method to get all branches under the regions specified
     * 
     * @param regionIds
     * @return
     * @throws InvalidInputException
     */
    public List<Branch> getBranchesByRegionIds( Set<Long> regionIds ) throws InvalidInputException;


    /**
     * Method to add a branch
     * 
     * @param user
     * @param region
     * @param branchName
     * @param isDefaultBySystem
     * @return
     */
    public Branch addBranch( User user, Region region, String branchName, int isDefaultBySystem );


    /**
     * Method to add a region
     * 
     * @param user
     * @param isDefaultBySystem
     * @param regionName
     * @return
     */
    public Region addRegion( User user, int isDefaultBySystem, String regionName );


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
     * @param holdSendingMail - true value will not send mail to the user till the record is verified.
     * @param isAddedByRealtechOrSSAdmin
     * @return
     * @throws InvalidInputException
     * @throws SolrException
     * @throws NoRecordsFetchedException
     * @throws UserAssignmentException
     */
    public Map<String, Object> addNewRegionWithUser( User user, String regionName, int isDefaultBySystem, String address1,
        String address2, String country, String countryCode, String state, String city, String zipcode, long selectedUserId,
        String[] emailIdsArray, boolean isAdmin, boolean holdSendingMail, boolean isAddedByRealtechOrSSAdmin )
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException;


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
     * @param isAddedByRealtechOrSSAdmin
     * @return
     * @throws InvalidInputException
     * @throws SolrException
     * @throws NoRecordsFetchedException
     * @throws UserAssignmentException
     */
    public Map<String, Object> updateRegion( User user, long regionId, String regionName, String address1, String address2,
        String country, String countryCode, String state, String city, String zipcode, long selectedUserId,
        String[] emailIdsArray, boolean isAdmin, boolean holdSendingMail, boolean isAddedByRealtechOrSSAdmin )
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException;


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
     * @param isAddedByRealtechOrSSAdmin
     * @return
     * @throws InvalidInputException
     * @throws SolrException
     * @throws NoRecordsFetchedException
     * @throws UserAssignmentException
     */
    public Map<String, Object> updateBranch( User user, long branchId, long regionId, String branchName, String address1,
        String address2, String country, String countryCode, String state, String city, String zipcode, long selectedUserId,
        String[] emailIdsArray, boolean isAdmin, boolean holdSendingMail, boolean isAddedByRealtechOrSSAdmin )
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException;


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
    public void assignRegionToUser( User adminUser, long regionId, User assigneeUser, boolean isAdmin )
        throws InvalidInputException, NoRecordsFetchedException, SolrException;


    /**
     * Method to add a new branch anassigneeUserd assign the user to the newly created branch if
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
     * @param holdSendingMail - true value will not send mail to the user till the record is verified.
     * @param isAddedByRealtechOrSSAdmin
     * @return
     * @throws InvalidInputException
     * @throws SolrException
     * @throws NoRecordsFetchedException
     * @throws UserAssignmentException
     */
    public Map<String, Object> addNewBranchWithUser( User user, String branchName, long regionId, int isDefaultBySystem,
        String address1, String address2, String country, String countryCode, String state, String city, String zipcode,
        long selectedUserId, String[] emailIdsArray, boolean isAdmin, boolean holdSendingMail, boolean isAddedByRealtechOrSSAdmin )
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException;


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
    public void assignBranchToUser( User adminUser, long branchId, long regionId, User assigneeUser, boolean isAdmin )
        throws InvalidInputException, NoRecordsFetchedException, SolrException;


    /**
     * Method to add a new user or assign existing user under a company/region or branch
     * @param adminUser
     * @param selectedUserId
     * @param branchId
     * @param regionId
     * @param emailIdsArray
     * @param isAdmin
     * @param holdSendingMail - true value will not send mail to the user till the record is verified.
     * @param sendMail
     * @param isAddedByRealtechOrSSAdmin
     * @param isSocialMonitorAdmin
     *
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     * @throws SolrException
     * @throws UserAssignmentException
     */
    public Map<String, Object> addIndividual( User adminUser, long selectedUserId, long branchId, long regionId,
        String[] emailIdsArray, boolean isAdmin, boolean holdSendingMail, boolean sendMail, boolean isAddedByRealtechOrSSAdmin, boolean isSocialMonitorAdmin )
        throws InvalidInputException, NoRecordsFetchedException, SolrException, UserAssignmentException;


    /**
     * Method to fetch all branches of a company
     * 
     * @param company
     * @return
     * @throws InvalidInputException
     */
    public List<Branch> getAllBranchesForCompany( Company company ) throws InvalidInputException;


    public List<Branch> getAllBranchesForCompanyWithProjections( Company company ) throws InvalidInputException;


    /**
     * Method to fetch branches mapped to a region
     * 
     * @param regionId
     * @return
     * @throws InvalidInputException
     */
    public List<Branch> getAllBranchesInRegion( long regionId ) throws InvalidInputException;


    public List<Branch> getAllBranchesInRegionWithProjections( long regionId ) throws InvalidInputException;


    /**
     * Method to fetch UserProfiles mapped to a branch
     * 
     * @param branchId
     * @return
     * @throws InvalidInputException
     */
    public List<UserProfile> getAllUserProfilesInBranch( long branchId ) throws InvalidInputException;


    /**
     * Method to fetch count of branches mapped to a region
     * 
     * @param regionId
     * @return
     * @throws InvalidInputException
     */
    public long getCountBranchesInRegion( long regionId ) throws InvalidInputException;


    /**
     * Method to fetch count of UserProfiles mapped to a branch
     * 
     * @param branchId
     * @return
     * @throws InvalidInputException
     */
    public long getCountUsersInBranch( long branchId ) throws InvalidInputException;


    /**
     * Method to fetch all regions of a company
     * 
     * @param company
     * @return
     * @throws InvalidInputException
     */
    public List<Region> getAllRegionsForCompany( Company company ) throws InvalidInputException;


    public List<Region> getAllRegionsForCompanyWithProjections( Company company ) throws InvalidInputException;


    /**
     * Method to update status of a branch
     * 
     * @param user
     * @param branchId
     * @param status
     * @throws InvalidInputException
     * @throws SolrException
     */
    public void updateBranchStatus( User user, long branchId, int status ) throws InvalidInputException, SolrException;


    /**
     * Method to update status of a region
     * 
     * @param user
     * @param regionId
     * @param status
     * @throws InvalidInputException
     * @throws SolrException
     */
    public void updateRegionStatus( User user, long regionId, int status ) throws InvalidInputException, SolrException;


    /**
     * Method to check whether a branch addition is allowed for given account type and user
     * 
     * @param user
     * @param accountType
     * @return
     * @throws InvalidInputException
     */
    public boolean isBranchAdditionAllowed( User user, AccountType accountType ) throws InvalidInputException;


    /**
     * Method to check whether a region addition is allowed for given account type and user
     * 
     * @param user
     * @param accountType
     * @return
     * @throws InvalidInputException
     */
    public boolean isRegionAdditionAllowed( User user, AccountType accountType ) throws InvalidInputException;


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
    public Branch addNewBranch( User user, long regionId, int isDefaultBySystem, String branchName, String address1,
        String address2, String country, String countryCode, String state, String city, String zipcode )
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
    public Region addNewRegion( User user, String regionName, int isDefaultBySystem, String address1, String address2,
        String country, String countryCode, String state, String city, String zipcode )
        throws InvalidInputException, SolrException;


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
    public void updateBranch( long branchId, long regionId, String branchName, String branchAddress1, String branchAddress2,
        User user ) throws InvalidInputException, SolrException;


    /**
     * Method to check whether a user has privileges to build hierarchy
     * 
     * @param user
     * @param accountType
     * @return
     */
    public boolean canBuildHierarchy( User user, AccountType accountType );


    /**
     * Method to check whether a user has privileges to edit company information
     * 
     * @param user
     * @param accountType
     * @return
     */
    public boolean canEditCompany( User user, AccountType accountType );


    /**
     * Method to insert region settings into mongo
     * 
     * @param region
     * @throws InvalidInputException
     */
    public void insertRegionSettings( Region region ) throws InvalidInputException;


    /**
     * Method to insert branch settings into mongo
     * 
     * @param branch
     * @throws InvalidInputException
     */
    public void insertBranchSettings( Branch branch ) throws InvalidInputException;


    /**
     * Method to get the list of region ids for a user and profile master id specified
     * 
     * @param user
     * @param profileMasterId
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public Set<Long> getRegionIdsForUser( User user, int profileMasterId )
        throws InvalidInputException, NoRecordsFetchedException;


    /**
     * Method to get the list of branch ids for a user and profile master id specified
     * 
     * @param user
     * @param profileMasterId
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public Set<Long> getBranchIdsForUser( User user, int profileMasterId )
        throws InvalidInputException, NoRecordsFetchedException;


    /**
     * Method to get the list of all the company ids
     */
    public Set<Company> getAllCompanies();


    public Map<Long, BranchFromSearch> fetchBranchesMapByCompany( long companyId )
        throws InvalidInputException, SolrException, MalformedURLException;


    public String fetchBranchesByCompany( long companyId ) throws InvalidInputException, SolrException, MalformedURLException;


    public Map<Long, RegionFromSearch> fetchRegionsMapByCompany( long companyId )
        throws InvalidInputException, SolrException, MalformedURLException;


    public String fetchRegionsByCompany( long companyId ) throws InvalidInputException, SolrException, MalformedURLException;


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
    public List<BranchFromSearch> getBranchesUnderCompanyFromSolr( Company company, int start )
        throws InvalidInputException, NoRecordsFetchedException, SolrException;


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
    public List<UserFromSearch> getUsersUnderCompanyFromSolr( Company company, int start )
        throws InvalidInputException, NoRecordsFetchedException, SolrException;


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
    public List<UserFromSearch> getUsersUnderRegionFromSolr( Set<Long> regionIds, int start, int rows )
        throws InvalidInputException, NoRecordsFetchedException, SolrException;


    /**
     * @return
     */
    public List<StateLookup> getUsStateList();


    /**
     * @param stateId
     * @return
     */
    public String getZipCodesByStateId( int stateId );


    /**
     * @param maxDisableDate
     * @return
     */
    public List<DisabledAccount> disableAccounts( Date maxDisableDate );


    /**
     * @param graceSpan
     * @return
     */
    public List<DisabledAccount> getAccountsForPurge( int graceSpan );


    /**
     * Method to delete a company from Solr
     * @param company
     * @throws InvalidInputException
     * @throws SolrException
     */
    void deleteCompanyFromSolr( Company company ) throws InvalidInputException, SolrException;


    /**
     * @param company
     * @throws InvalidInputException
     * @throws SolrException
     */
    public void purgeCompany( Company company ) throws InvalidInputException, SolrException;


    /**
     * @param company
     * @param loggedInUser 
     * @throws InvalidInputException
     * @throws SolrException
     */
    public void deleteCompany( Company company, User loggedInUser, int status ) throws InvalidInputException, SolrException;


    /**
     * @param branchId
     * @param user
     * @param assignments
     * @throws InvalidInputException
     * @throws SolrException
     */
    public void deleteBranchDataFromAllSources( long branchId, User user, UserHierarchyAssignments assignments, int status )
        throws InvalidInputException, SolrException;


    /**
     * @param regionId
     * @param user
     * @param assignments
     * @throws InvalidInputException
     * @throws SolrException
     */
    public void deleteRegionDataFromAllSources( long regionId, User user, UserHierarchyAssignments assignments, int status )
        throws InvalidInputException, SolrException;


    /**
     * @param company
     * @throws DatabaseException
     */
    public void updateCompany( Company company ) throws DatabaseException;


    /**
     * Method to set company status to deleted in mongo
     * @param company
     * @throws InvalidInputException
     */
    public void deactivateCompanyInMongo( Company company ) throws InvalidInputException;


    /**
     * @param user
     * @return
     * @throws InvalidInputException
     */
    public List<VerticalCrmMapping> getCrmMapping( User user ) throws InvalidInputException;


    /**
     * @param string
     * @return
     */
    public Map<Long, OrganizationUnitSettings> getSettingsMapWithLinkedinImage( String string );


    /**
     * @param idToRemove
     * @param collectionName
     */
    public void removeOrganizationUnitSettings( Long idToRemove, String collectionName );


    /**
     * @param idsToRemove
     * @param collectionName
     */
    public void removeOrganizationUnitSettings( List<Long> idsToRemove, String collectionName );


    /**
     * @return
     */
    public SurveySettings retrieveDefaultSurveyProperties();


    /**
     * @param surveySettings
     * @param mood
     * @return
     */
    public String resetDefaultSurveyText( SurveySettings surveySettings, String mood );


    /**
     * @param searchKey
     * @return
     */
    public List<Company> getCompaniesByName( String searchKey );


    /**
     * @param companyId
     * @return
     */
    public Company getCompanyById( long companyId );


    /**
     * @return
     */
    public List<OrganizationUnitSettings> getAllActiveCompaniesFromMongo();


    /**
     * @return
     */
    public List<OrganizationUnitSettings> getAllCompaniesFromMongo();


    /**
     * @param searchKey
     * @return
     */
    public List<OrganizationUnitSettings> getCompaniesByNameFromMongo( String searchKey );


    /**
     * @param searchKey
     * @param accountType
     * @param status
     * @return
     */
    public List<OrganizationUnitSettings> getCompaniesByKeyValueFromMongo( String searchKey, int accountType, int status,
        boolean inCompleteCompany, int noOfDays );


    /**
     * @param startDate
     * @param endDate
     * @return
     */
    public List<Company> getCompaniesByDateRange( Date startDate, Date endDate );


    /**
     * @param companies
     * @param fileName
     * @return
     */
    public XSSFWorkbook downloadCompanyReport( List<Company> companies );


    /**
     * @param regionId
     * @return
     */
    public Company getPrimaryCompanyByRegion( long regionId );


    /**
     * @param branchId
     * @return
     */
    public Region getPrimaryRegionByBranch( long branchId );


    /**
     * @param collectionName
     * @param skipCount
     * @param batchSize
     * @return
     */
    public List<FeedIngestionEntity> fetchSocialMediaTokens( String collectionName, int skipCount, int batchSize );


    /**
     * @param mailContentSettings
     * @param organizationUnitSettings
     * @param collectionName
     */
    public void updateMailContentForOrganizationUnit( MailContentSettings mailContentSettings,
        OrganizationUnitSettings organizationUnitSettings, String collectionName );


    /**
     * @param fileName
     * @return
     * @throws IOException
     */
    public String readMailContentFromFile( String fileName ) throws IOException;


    /**
     * Method to update the profile-name in the branch table.
     * @param branchId
     * @param profileName
     */
    void updateBranchProfileName( long branchId, String profileName );


    /**
     * Method to update the profile-name in the region table.
     * @param regionId
     * @param profileName
     */
    void updateRegionProfileName( long regionId, String profileName );


    /**
     * Updates the region
     * @param region
     */
    public void updateRegion( Region region );


    /**
     * Updates the branch
     * @param branch
     */
    public void updateBranch( Branch branch );


    /**
     * @param profileId
     * @param collectionName
     * @param collectionId
     * @return
     * @throws InvalidInputException
     */
    public long getLoopsCountByProfile( String profileId, String collectionName, long collectionId )
        throws InvalidInputException;


    /**
     * @param loopProfileMapping
     */
    public void saveLoopsForProfile( LoopProfileMapping loopProfileMapping ) throws InvalidInputException;


    /**
     * @param profileId
     * @param loopId
     * @param collectionName
     * @param collectionId
     * @return
     * @throws InvalidInputException
     */
    public LoopProfileMapping getLoopByProfileAndLoopId( String profileId, String loopId, String collectionName,
        long collectionId ) throws InvalidInputException;


    /**
     * @param organizationUnitId
     * @param profileId
     * @return
     * @throws InvalidInputException
     */
    public CollectionDotloopProfileMapping getCollectionDotloopMappingByCollectionIdAndProfileId( String collectionName,
        long organizationUnitId, String profileId ) throws InvalidInputException;


    /**
     * @param profileId
     * @return
     */
    public CollectionDotloopProfileMapping getCollectionDotloopMappingByProfileId( String profileId )
        throws InvalidInputException;


    /**
     * @param companyDotloopProfileMapping
     * @return
     * @throws InvalidInputException
     */
    public CollectionDotloopProfileMapping saveCollectionDotLoopProfileMapping(
        CollectionDotloopProfileMapping collectionDotloopProfileMapping ) throws InvalidInputException;


    /**
     * @param collectionDotloopProfileMapping
     * @throws InvalidInputException
     */
    public void updateCollectionDotLoopProfileMapping( CollectionDotloopProfileMapping collectionDotloopProfileMapping )
        throws InvalidInputException;


    /**
     * Gets the organization unit settings list based on crm source and mongo collection name
     * @param crmSource
     * @param collectionName
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public List<OrganizationUnitSettings> getOrganizationUnitSettingsForCRMSource( String crmSource, String collectionName )
        throws InvalidInputException, NoRecordsFetchedException;


    /**
     * @param unitSettings
     * @param collectionName
     * @param crmInfo
     * @param fullyQualifiedClass
     * @throws InvalidInputException
     */
    public void updateCRMDetailsForAnyUnitSettings( OrganizationUnitSettings unitSettings, String collectionName,
        CRMInfo crmInfo, String fullyQualifiedClass ) throws InvalidInputException;


    MailContent deleteMailBodyFromSetting( OrganizationUnitSettings companySettings, String mailCategory )
        throws NonFatalException;


    /**
     * @param collectionName
     * @param unitSettings
     * @param surveySettings
     * @return
     * @throws InvalidInputException
     */
    public boolean updateScoreForSurvey( String collectionName, OrganizationUnitSettings unitSettings,
        SurveySettings surveySettings ) throws InvalidInputException;


    public Boolean validateEmail( String emailId ) throws InvalidInputException;


    String getAllUsersUnderCompanyFromSolr( Company company )
        throws InvalidInputException, NoRecordsFetchedException, SolrException;


    List<ProfileImageUrlData> fetchProfileImageUrlsForEntityList( String entityType, HashSet<Long> entityList )
        throws InvalidInputException;


    List<Region> getAllRegions();


    List<Branch> getAllBranches();


    List<User> getAllUsers();


    Map<Long, String> getListOfUnprocessedImages( String collectionName, String imageType ) throws InvalidInputException;


    void updateImageForOrganizationUnitSetting( long iden, String fileName, String thumbnailFileName,
        String rectangularThumbnailFileName, String collectionName, String imageType, boolean flagValue, boolean isThumbnail )
        throws InvalidInputException;


    public List<Region> getRegionsForCompany( long companyId ) throws InvalidInputException, ProfileNotFoundException;


    public List<Branch> getBranchesUnderCompany( long companyId )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException;


    public List<Region> getRegionsForRegionIds( Set<Long> regionIds ) throws InvalidInputException;


    public List<Branch> getBranchesForBranchIds( Set<Long> branchIds ) throws InvalidInputException;


    /**
     * Method to change profileurl of entity on delete
     * JIRA SS-1365
     * 
     * @param entityType
     * @param entityId
     * @throws InvalidInputException
     */
    void updateProfileUrlAndStatusForDeletedEntity( String entityType, long entityId ) throws InvalidInputException;

    //    public Set<Long> getRegionsConnectedToZillow( Set<Long> regionIds );


    //    public Set<Long> getIndividualsForBranchesConnectedWithZillow( Set<Long> branchIds ) throws InvalidInputException;


    //    public Set<Long> getIndividualsForRegionsConnectedWithZillow( Set<Long> regionIds ) throws InvalidInputException, NoRecordsFetchedException;


    //    public Set<Long> getBranchesConnectedToZillow( Set<Long> branchIds ) throws InvalidInputException;


    //    public Set<Long> getIndividualsForCompanyConnectedWithZillow( long companyId ) throws InvalidInputException,
    //        NoRecordsFetchedException, ProfileNotFoundException;


    //    public Map<String, Set<Long>> getAllIdsUnderRegionsConnectedToZillow( Set<Long> regionIds );

    public Set<Long> getAllRegionsUnderCompanyConnectedToZillow( long iden, int start_index, int batch_size )
        throws InvalidInputException;


    public Set<Long> getAllBranchesUnderProfileTypeConnectedToZillow( String profileType, long iden, int start_index,
        int batch_size ) throws InvalidInputException;


    public Set<Long> getAllUsersUnderProfileTypeConnectedToZillow( String profileType, long iden, int start_index,
        int batch_size ) throws InvalidInputException;


    public List<OrganizationUnitSettings> getCompanyListForEncompass( String state, String encompassVersion )
        throws InvalidInputException, NoRecordsFetchedException;


    /**
     * Get a list of non default active branches
     * @return
     * @throws NoRecordsFetchedException
     */
    public List<Branch> getAllNonDefaultBranches() throws NoRecordsFetchedException;


    /**
     * Get a list of non default active regions
     * @return
     * @throws NoRecordsFetchedException
     */
    public List<Region> getAllNonDefaultRegions() throws NoRecordsFetchedException;


    /**
     * Get the compare objects for mismatched branch hierarchy settings
     * @param branches
     * @return
     */
    public List<HierarchySettingsCompare> mismatchBranchHierarchySettings( List<Branch> branches );


    /**
     * Get the compare objects for mismatched region hierarchy settings
     * @param regions
     * @return
     */
    public List<HierarchySettingsCompare> mismatchRegionHierarchySettings( List<Region> regions );


    /**
     * Validates the upload file
     * @param uploadFileName
     * @return
     */
    public UploadValidation validateUserUploadSheet( String uploadFileName ) throws InvalidInputException;


    List<Region> getRegionsBySearchKey( String searchKey ) throws InvalidInputException, SolrException;


    List<Branch> getBranchesBySearchKey( String searchKey ) throws InvalidInputException, SolrException;


    List<UserFromSearch> getUsersBySearchKey( String searchKey ) throws InvalidInputException, SolrException;


    public List<Long> getRegionIdsUnderCompany( long companyId, int start, int batchSize ) throws InvalidInputException;


    public List<Long> getBranchIdsUnderCompany( long companyId, int start, int batchSize ) throws InvalidInputException;


    public List<Long> getAgentIdsUnderCompany( long companyId, int start, int batchSize ) throws InvalidInputException;


    public List<OrganizationUnitSettings> fetchUnitSettingsConnectedToZillow( String collectionName, List<Long> ids );


    public List<OrganizationUnitSettings> fetchUnitSettingsForSocialMediaTokens( String collectionName );


    public Map<String, List<User>> getUsersFromEmailIdsAndInvite( String[] emailIdsArray, User adminUser,
        boolean holdSendingMail, boolean sendMail, boolean isAddedByRealtechOrSSAdmin ) throws InvalidInputException;


    public void pushZillowReviews( List<SurveyDetails> surveyDetailsList, String collectionName,
        OrganizationUnitSettings profileSettings, long companyId ) throws InvalidInputException;


    /**
     * Method to deactivate accounts
     */
    public void accountDeactivator();


    public void logEvent( String eventType, String action, String modifiedBy, long companyId, int agentId, int regionId,
        int branchId );


    /**
     * Returns agent social media tokens
     * @param iden
     * @return
     * @throws InvalidInputException
     */
    public SocialMediaTokens getAgentSocialMediaTokens( long iden ) throws InvalidInputException;


    public List<AgentSettings> getAllAgentsFromMongo();


    void updateUserEncryptedIdOfSetting( AgentSettings agentSettings, String userEncryptedId );


    public void deactivatedAccountPurger();


    public void hierarchySettingsCorrector();


    public void imageProcessorStarter();


    public void addOrganizationalDetails( User user, Company company, Map<String, String> organizationalDetails )
        throws InvalidInputException;


    public String generateProfileNameForCompany( String companyName, long iden ) throws InvalidInputException;


    /**
     * Method to activate the company status to active
     * @param company
     * @return
     * @throws InvalidInputException
     */
    public Company activateCompany( Company company ) throws InvalidInputException;


    /**
     * 
     * @param columnName
     * @param columnValue
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public List<String> getExpiredSocailMedia( String columnName, long columnValue )
        throws InvalidInputException, NoRecordsFetchedException;


    public List<CompanyHiddenNotification> getCompaniesWithHiddenSectionEnabled();


    public void deleteCompanyHiddenNotificationRecord( CompanyHiddenNotification record );


    public List<Company> getCompaniesByBillingModeAuto();


    void askFbToRescrapePagesForSettings( Set<Long> entityIds, String collectionName );


    void unsubscribeCompany( Company company ) throws SubscriptionCancellationUnsuccessfulException, InvalidInputException;


    public void updateSortCriteriaForCompany( OrganizationUnitSettings companySettings, String sortCriteria )
        throws InvalidInputException;


    void updateSendEmailThroughForCompany( OrganizationUnitSettings companySettings, String sendEmailThrough )
        throws InvalidInputException;


    void markDisabledAccountAsProcessed( Company company ) throws InvalidInputException;


    void processCancelSubscriptionRequest( OrganizationUnitSettings companySettings, boolean isAccountDisabled, long userId )
        throws NonFatalException;


    void processDeactivateCompany( Company company, long userId )
        throws InvalidInputException, SolrException, NonFatalException;


    void updateAllowPartnerSurveyForAllUsers( Set<Long> userIds, boolean allowPartnerSurvey ) throws InvalidInputException;


    void updatellowPartnerSurveyForUser( AgentSettings agentSettings, boolean allowPartnerSurvey );


    boolean isPartnerSurveyAllowedForComapny( long companyId );


    void updateSurveyAssignments( User user, List<UserProfile> userProfileList, long oldUserProfileId );


    public List<User> getUsersUnderBranch( Branch branch ) throws InvalidInputException;


    public void updateCompanyIdInMySQLForUser( User userToBeRelocated, Company targetCompany ) throws InvalidInputException;


    public List<EncompassSdkVersion> getActiveEncompassSdkVersions();


    public String getEncompassHostByVersion( String sdkVersion ) throws InvalidInputException;


    public List<Company> getAllActiveEnterpriseCompanies();


    public List<Company> getCompaniesByCompanyIds( Set<Long> companyIds );


    public Map<Long, Long> getUsersCountForCompanies();


    public List<Long> getHiddenPublicPageCompanyIds();


    public List<Long> getHiddenPublicPageRegionIds();


    public List<Long> getHiddenPublicPageBranchIds();


    public List<Long> getHiddenPublicPageUserIds();


    public List<CompanyView> getAllActiveEnterpriseCompanyViews();

    public List<OrganizationUnitSettings> getCompaniesForTransactionMonitor();


    void updateTransactionMonitorSettingForCompany( long companyId, boolean includeForTransactionMonitor );

    public String getFacebookPixelImageTagsFromHierarchy( OrganizationUnitSettings companySettings,
        OrganizationUnitSettings regionSettings, OrganizationUnitSettings branchSetting, OrganizationUnitSettings unitSettings );

    /**
     * Method to add filter keywords in OrganizationUnitSettings.
     * @param companyId : companyId for updaing keywords
     * @param keywords : List of Keyword to insert/updates
     * @return : will return list Of keywords(with detail)  
     * @throws InvalidInputException
     */
    public List<Keyword> addKeyworodsToCompanySettings( long companyId, List<Keyword> keywords )
        throws InvalidInputException;


    /**
     * Method to get filter keywords by company id
     * @param companyId
     * @return
     * @throws InvalidInputException 
     */
    public FilterKeywordsResponse getCompanyKeywordsByCompanyId( long companyId, int startIndex, int limit, String monitorType, String searchPhrase ) throws InvalidInputException;
    
    /**
     * Method to enable keyword by keyword id
     * @param companyId
     * @param keywordId
     * @return
     * @throws InvalidInputException
     */
    public Keyword enableKeyworodForCompanySettings( long companyId, String keywordId)
        throws InvalidInputException;
    
    /**
     * method to disable keyword by keyword id
     * @param companyId
     * @param keywordId
     * @return
     * @throws InvalidInputException
     */
    public Keyword disableKeyworodForCompanySettings( long companyId, String keywordId)
        throws InvalidInputException;

    public Set<String> parseEmailsList( String emailsStr );

    public boolean updateDigestRecipients( String entityType, long entityId, Set<String> emails )
        throws InvalidInputException, NoRecordsFetchedException;

    public List<OrganizationUnitSettings> getCompaniesByAlertType( String alertType );


    public String getCollectionFromProfileLevel( String profileLevel ) throws InvalidInputException;


    public Set<String> getAdminEmailsSpecificForAHierarchy( String profileLevel, long iden ) throws InvalidInputException;


    /**
     * Method to fetch all media tokens with profile name
     * @param skipCount
     * @param batchSize
     * @return
     * @throws InvalidInputException 
     */
    List<SocialMediaTokenResponse> fetchSocialMediaTokensResponse(int skipCount, int batchSize ) throws InvalidInputException;

	public List<Long> filterCompanyIdsByStatus(List<Long> companies, String status) throws InvalidInputException;


	public boolean decryptEncompassPasswordIfPossible(OrganizationUnitSettings unitSettings);


	/**
	 * @param companyId
	 * @param mailId
	 * @return
	 * @throws InvalidInputException 
	 * @throws NonFatalException 
	 */
	public void updateAbusiveMailService(long companyId, String mailId) throws InvalidInputException, NonFatalException;


	/**
	 * @param companyId
	 * @throws NonFatalException
	 */
	public void unsetAbusiveMailService(long companyId) throws NonFatalException;


	/**
	 * @param companySettings
	 * @param keyToUpdate
	 * @param collectionName
	 * @return
	 * @throws InvalidInputException
	 */
	public boolean unsetKey(OrganizationUnitSettings companySettings, String keyToUpdate, String collectionName)
			throws InvalidInputException;


	/**
	 * @param companyId
	 * @throws NonFatalException
	 */
	public void unsetComplaintResService(long companyId) throws NonFatalException; 

	public boolean doesSurveyHaveNPSQuestions( User user );

    
	void updateIsLoginPreventedForUser( Long userId, boolean isLoginPrevented ) throws InvalidInputException;

    public Map<String, Long> getFacebookAndTwitterLocks( String lockType ) throws InvalidInputException;

    

	void updateIsLoginPreventedForUsers( List<Long> userIdList, boolean isLoginPrevented ) throws InvalidInputException;


	void updateHidePublicPageForUser( Long userId, boolean hidePublicPage) throws InvalidInputException;
    
    
    void updateHidePublicPageForUsers( List<Long> userIdList, boolean hidePublicPage ) throws InvalidInputException;
    

    /**
     * Method to delete keywords from company
     * @param companyId
     * @param keywordIds
     * @return
     * @throws InvalidInputException
     */
    public List<Keyword> deleteKeywordsFromCompany( long companyId, List<String> keywordIds ) throws InvalidInputException;


    /**
     * Method to add or update keyword to a company
     * @param companyId
     * @param keyword
     * @return
     * @throws InvalidInputException
     */
    public List<Keyword> addKeywordToCompanySettings( long companyId, Keyword keyword ) throws InvalidInputException;

    public SocialMediaTokensPaginated fetchSocialMediaTokensPaginated( int skipCount, int batchSize )
        throws InvalidInputException;

    /**
     * Method to add multiple phrases in a keyword to a company
     * @param companyId
     * @param multiplePhrasesVO
     * @return
     * @throws InvalidInputException
     */
    public List<Keyword> addMultiplePhrasesToCompany( long companyId, MultiplePhrasesVO multiplePhrasesVO ) throws InvalidInputException;



    public List<String> validateSocailMedia( String columnName, long columnValue ) throws InvalidInputException, NoRecordsFetchedException;

	ContactDetailsSettings fetchContactDetailByEncryptedId(String encryptedId, String collection);
	
	void updateSocialMediaForUser( Long userId, boolean disableSocialMediaTokens ) throws InvalidInputException;
	

    void updateSocialMediaForUsers( List<Long> userIdList, boolean disableSocialMediaTokens ) throws InvalidInputException;

	/**
	 * 
	 * @param companyId
	 * @param version
	 * @return
	 * @throws InvalidInputException
	 */
    public  boolean updateEncompassVersion( long companyId, String version ) throws InvalidInputException;



    public boolean updateUserAdditionDeletionRecipients( String entityType, long entityId, Set<String> emails )
        throws InvalidInputException, NoRecordsFetchedException;


    public void sendUserAdditionMail( User adminUser, User user )
        throws InvalidInputException, UndeliveredEmailException, NoRecordsFetchedException;


    public void sendUserDeletionMail( User adminUser, User user )
        throws InvalidInputException, UndeliveredEmailException, NoRecordsFetchedException;


	public List<SocialMonitorTrustedSource> addTrustedSourceToCompany(long companyId, String trustedSource) throws InvalidInputException;

	boolean updateSocialMediaToken( String collection, long iden, String fieldToUpdate, boolean value )
        throws InvalidInputException;

    public void unsetWebAddressInProfile( long entityId, String entityType ) throws NonFatalException;



    public boolean updateEntitySettings( String entityType, long entityId, String flagToBeUpdated, String status );

    
    void updateAgentProfileDisable( long companyId, boolean isAgentProfileDisabled ) throws InvalidInputException;


    void updateAgentsProfileDisable( List<Long> agentId, boolean isAgentProfileDisabled ) throws InvalidInputException;
    
    public boolean isSocialMonitorAdmin(Long agentId) throws InvalidInputException;
    
    public boolean hasRegisteredForSummit(Long companyId) throws InvalidInputException;
    
    public void setHasRegisteredForSummit(Long companyId, boolean isShowSummitPopup) throws InvalidInputException;

	boolean isShowSummitPopup(Long companyId) throws InvalidInputException;

	void setShowSummitPopup(Long companyId, boolean isShowSummitPopup) throws InvalidInputException;


    /**
     * @param companyId
     * @param trustedSource
     * @return
     * @throws InvalidInputException
     */
    public List<SocialMonitorTrustedSource> removeTrustedSourceToCompany( long companyId, String trustedSource )
        throws InvalidInputException;

}