package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.LockSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.HierarchyManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

// JIRA SS-37 BY RM02 BOC

/**
 * Implementation class for Hierarchy management services
 */
@Component
public class HierarchyManagementServiceImpl implements HierarchyManagementService {

	private static final Logger LOG = LoggerFactory.getLogger(HierarchyManagementServiceImpl.class);

	@Autowired
	private GenericDao<Branch, Long> branchDao;

	@Autowired
	private OrganizationUnitSettingsDao organizationUnitSettingsDao;

	@Autowired
	private GenericDao<Region, Long> regionDao;

	@Autowired
	private GenericDao<UserProfile, Long> userProfileDao;

	@Autowired
	private OrganizationManagementService organizationManagementService;

	@Autowired
	private SolrSearchService solrSearchService;

	@Autowired
	private Utils utils;

	/**
	 * Fetch list of branches in a company
	 * 
	 * @param company
	 * @return List of branches
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public List<Branch> getAllBranchesForCompany(Company company) throws InvalidInputException {
		if (company == null) {
			LOG.error("Company object passed can not be null");
			throw new InvalidInputException("Invalid Company passed");
		}
		LOG.info("Fetching the list of branches for company :" + company.getCompany());
		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.COMPANY_COLUMN, company);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		List<Branch> branchList = branchDao.findByKeyValue(Branch.class, queries);
		LOG.info("Branch list fetched for the company " + company);
		return branchList;
	}

	/**
	 * Fetch list of regions in a company
	 * 
	 * @param company
	 * @return List of regions
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public List<Region> getAllRegionsForCompany(Company company) throws InvalidInputException {
		if (company == null) {
			LOG.error("Company object passed can not be null");
			throw new InvalidInputException("Invalid Company passed");
		}

		LOG.info("Fetching the list of regions for company :" + company.getCompany());

		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.COMPANY_COLUMN, company);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);

		List<Region> regionList = regionDao.findByKeyValue(Region.class, queries);
		LOG.info("Region list fetched for the company " + company);
		return regionList;
	}

	/**
	 * Updates status of a branch
	 * 
	 * @param user
	 * @param branchId
	 * @param status
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	@Override
	@Transactional
	public void updateBranchStatus(User user, long branchId, int status) throws InvalidInputException, SolrException {
		LOG.info("Update branch of id :" + branchId + " status to :" + status);
		if (user == null) {
			throw new InvalidInputException("User is null in updateRegionStatus");
		}
		if (branchId <= 0l) {
			throw new InvalidInputException("BranchId is not set in updateRegionStatus");
		}

		LOG.debug("Fetching the branch object by ID");
		Branch branch = branchDao.findById(Branch.class, branchId);
		if (branch == null) {
			LOG.error("No branch present with the branch Id :" + branchId);
			throw new InvalidInputException("No branch present with the branch Id :" + branchId);
		}

		branch.setStatus(status);
		branch.setModifiedBy(String.valueOf(user.getUserId()));
		branch.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		branchDao.update(branch);

		LOG.debug("Updating document of the branch in solr");
		solrSearchService.addOrUpdateBranchToSolr(branch);
		LOG.info("Branch status for branch ID :" + branchId + "/t successfully updated to:" + status);
	}

	/**
	 * Updates the status of region
	 * 
	 * @param regionId
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	@Override
	@Transactional
	public void updateRegionStatus(User user, long regionId, int status) throws InvalidInputException, SolrException {
		LOG.info("Method updateRegionStatus called for regionId : " + regionId + " and status : " + status);
		if (user == null) {
			throw new InvalidInputException("User is null in updateRegionStatus");
		}
		if (regionId <= 0l) {
			throw new InvalidInputException("RegionId is not set in updateRegionStatus");
		}
		Region region = regionDao.findById(Region.class, regionId);
		if (region == null) {
			LOG.error("No region present with the region Id :" + regionId);
			throw new InvalidInputException("No region present with the region Id :" + regionId);
		}
		region.setStatus(status);
		region.setModifiedBy(String.valueOf(user.getUserId()));
		region.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		regionDao.update(region);

		LOG.debug("Updating document of the region in solr");
		solrSearchService.addOrUpdateRegionToSolr(region);

		LOG.info("Region status for region ID :" + regionId + "/t successfully updated to " + status);
	}

	/**
	 * Fetch list of branches in a company for a Region
	 * 
	 * @param regionId
	 * @return List of branches
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public List<Branch> getAllBranchesInRegion(long regionId) throws InvalidInputException {
		if (regionId <= 0l) {
			throw new InvalidInputException("RegionId is not set in getAllBranchesForRegion");
		}
		Region region = regionDao.findById(Region.class, regionId);
		if (region == null) {
			LOG.error("No region present with the region Id :" + regionId);
			throw new InvalidInputException("No region present with the region Id :" + regionId);
		}
		LOG.info("Fetching the list of branches for region :" + region);

		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.REGION_COLUMN, region);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		List<Branch> branchList = branchDao.findByKeyValue(Branch.class, queries);

		LOG.info("Branch list fetched for the region " + region);
		return branchList;
	}

	/**
	 * Method to fetch count of branches in a company for a Region
	 * 
	 * @param regionId
	 * @return List of branches
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public long getCountBranchesInRegion(long regionId) throws InvalidInputException {
		if (regionId <= 0l) {
			throw new InvalidInputException("RegionId is not set in getAllBranchesForRegion");
		}
		Region region = regionDao.findById(Region.class, regionId);
		if (region == null) {
			LOG.error("No region present with the region Id :" + regionId);
			throw new InvalidInputException("No region present with the region Id :" + regionId);
		}
		LOG.info("Fetching the list of branches for region :" + region);

		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.REGION_COLUMN, region);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		long branchCount = branchDao.findNumberOfRowsByKeyValue(Branch.class, queries);

		LOG.info("Branch list fetched for the region " + region);
		return branchCount;
	}

	/**
	 * Method to fetch UserProfiles associated with a branch
	 * 
	 * @param company
	 * @param branchId
	 * @return
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public List<UserProfile> getAllUserProfilesInBranch(long branchId) throws InvalidInputException {
		if (branchId <= 0l) {
			throw new InvalidInputException("RegionId is not set in getAllUserProfilesForBranch");
		}
		LOG.info("Fetching the list of users for branch :" + branchId);

		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.BRANCH_ID_COLUMN, branchId);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		List<UserProfile> userList = userProfileDao.findByKeyValue(UserProfile.class, queries);

		LOG.info("Users list fetched for the branch " + branchId);
		return userList;
	}

	/**
	 * Method to fetch count of UserProfiles associated with a branch
	 * 
	 * @param company
	 * @param branchId
	 * @return
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public long getCountUsersInBranch(long branchId) throws InvalidInputException {
		if (branchId <= 0l) {
			throw new InvalidInputException("RegionId is not set in getAllUserProfilesForBranch");
		}
		LOG.info("Fetching the list of users for branch :" + branchId);

		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.BRANCH_ID_COLUMN, branchId);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		long usersCount = userProfileDao.findNumberOfRowsByKeyValue(UserProfile.class, queries);

		LOG.info("Users list fetched for the branch " + branchId);
		return usersCount;
	}

	/**
	 * Method to check if branches allowed to be added have succeeded the max limit for a user and
	 * account type
	 * 
	 * @param user
	 * @param accountType
	 * @return
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public boolean isBranchAdditionAllowed(User user, AccountType accountType) throws InvalidInputException {
		LOG.info("Method to check if further branch addition is allowed, called for user : " + user);
		if (user == null) {
			throw new InvalidInputException("User is null in isBranchAdditionAllowed");
		}
		if (accountType == null) {
			throw new InvalidInputException("Account type is null in isBranchAdditionAllowed");
		}
		boolean isBranchAdditionAllowed = true;
		switch (accountType) {
			case INDIVIDUAL:
				LOG.info("Checking branch addition for account type INDIVIDUAL");
				// No branch addition is allowed for individual
				isBranchAdditionAllowed = false;
				break;
			case TEAM:
				LOG.info("Checking branch addition for account type TEAM");
				isBranchAdditionAllowed = false;
				break;
			case COMPANY:
				LOG.info("Checking branch addition for account type COMPANY");
				isBranchAdditionAllowed = true;
				break;
			case ENTERPRISE:
				LOG.info("Checking branch addition for account type INDIVIDUAL");
				isBranchAdditionAllowed = true;
				break;
			default:
				throw new InvalidInputException("Account type is invalid in isBranchAdditionAllowed");
		}
		LOG.info("Returning from isBranchAdditionAllowed for user : " + user.getUserId() + " isBranchAdditionAllowed is :" + isBranchAdditionAllowed);

		return isBranchAdditionAllowed;
	}

	/**
	 * Method to check if regions allowed to be added have succeeded the max limit for a user and
	 * account type
	 * 
	 * @param user
	 * @param accountType
	 * @return
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public boolean isRegionAdditionAllowed(User user, AccountType accountType) throws InvalidInputException {
		LOG.info("Method to check if further region addition is allowed called for user : " + user);
		if (user == null) {
			throw new InvalidInputException("User is null in isRegionAdditionAllowed");
		}
		if (accountType == null) {
			throw new InvalidInputException("Account type is null in isRegionAdditionAllowed");
		}
		boolean isRegionAdditionAllowed = true;
		switch (accountType) {
			case INDIVIDUAL:
				LOG.info("Checking Region addition for account type INDIVIDUAL");
				// No region addition is allowed for individual
				isRegionAdditionAllowed = false;
				break;
			case TEAM:
				LOG.info("Checking Region addition for account type TEAM");
				// No region addition is allowed for team
				isRegionAdditionAllowed = false;
				break;
			case COMPANY:
				LOG.info("Checking Region addition for account type COMPANY");
				isRegionAdditionAllowed = false;
				break;
			case ENTERPRISE:
				LOG.info("Checking Region addition for account type ENTERPRISE");
				isRegionAdditionAllowed = true;
				break;
			default:
				throw new InvalidInputException("Account type is invalid in isRegionAdditionAllowed");
		}
		LOG.info("Returning from isRegionAdditionAllowed for user : " + user.getUserId() + " isRegionAdditionAllowed is :" + isRegionAdditionAllowed);
		return isRegionAdditionAllowed;
	}

	/**
	 * Method to add a new branch from UI
	 * 
	 * @param user
	 * @param regionId
	 * @param branchName
	 * @param branchAddress1
	 * @param branchAddress2
	 * @return
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	@Override
	@Transactional
	public Branch addNewBranch(User user, long regionId, int isDefaultBySystem, String branchName, String branchAddress1, String branchAddress2)
			throws InvalidInputException, SolrException {
		if (user == null) {
			throw new InvalidInputException("User is null in addNewBranch");
		}
		if (branchName == null || branchName.isEmpty()) {
			throw new InvalidInputException("Branch name is null in addNewBranch");
		}
		if (branchAddress1 == null || branchAddress1.isEmpty()) {
			throw new InvalidInputException("Branch address is null in addNewBranch");
		}
		LOG.info("Method add new branch called for regionId : " + regionId + " and branchName : " + branchName);
		Region region = null;
		LOG.debug("Fetching region for branch to be added");
		/**
		 * If region is selected by user, select it from db
		 */
		if (regionId > 0l) {
			region = regionDao.findById(Region.class, regionId);
		}
		/**
		 * else select the default region from db for that company
		 */
		else {
			LOG.debug("Selecting the default region for company");
			Map<String, Object> queries = new HashMap<String, Object>();
			queries.put(CommonConstants.COMPANY, user.getCompany());
			queries.put(CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.YES);
			List<Region> regions = regionDao.findByKeyValue(Region.class, queries);
			if (regions != null && !regions.isEmpty()) {
				region = regions.get(0);
			}
		}
		if (region == null) {
			throw new InvalidInputException("No region is present in db for the company while adding branch");
		}

		Branch branch = organizationManagementService.addBranch(user, region, branchName, isDefaultBySystem);
		branch.setAddress1(branchAddress1);
		branch.setAddress2(branchAddress2);

		LOG.debug("Adding new branch into mongo");
		insertBranchSettings(branch);

		LOG.debug("Updating branch table with profile name");
		branchDao.update(branch);

		LOG.debug("Adding newly added branch to solr");
		solrSearchService.addOrUpdateBranchToSolr(branch);

		LOG.info("Successfully completed method add new branch for regionId : " + region.getRegionId() + " and branchName : " + branchName);
		return branch;

	}

	/**
	 * Method to generate profile name and profile url for a branch and also set them in
	 * organization unit settings
	 * 
	 * @param branch
	 * @param organizationSettings
	 * @throws InvalidInputException
	 */
	private void generateAndSetBranchProfileNameAndUrl(Branch branch, OrganizationUnitSettings organizationSettings) throws InvalidInputException {
		LOG.debug("Method to generate branch profile name called for branch: " + branch);
		String branchProfileName = null;
		if (branch == null) {
			throw new InvalidInputException("Branch is null in generateAndSetRegionProfileNameAndUrl");
		}
		String branchName = branch.getBranch();
		if (branchName == null || branchName.isEmpty()) {
			throw new InvalidInputException("Branch name is null or empty in generateAndSetRegionProfileNameAndUrl");
		}

		branchProfileName = branchName.trim().replaceAll(" ", "-").toLowerCase();

		OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(branch.getCompany().getCompanyId(),
				MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		if (companySettings != null) {
			String companyProfileName = companySettings.getProfileName();
			String branchProfileUrl = utils.generateBranchProfileUrl(companyProfileName, branchProfileName);

			LOG.debug("Checking if profileName:" + branchProfileName + " is already taken by a branch in the company :" + branch.getCompany());
			/**
			 * Uniqueness of profile name is checked by url since combination of company profile
			 * name and branch profile name is unique
			 */
			OrganizationUnitSettings regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileUrl(branchProfileUrl,
					MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION);
			/**
			 * if there exists a branch with the profile name formed, append branch iden to get the
			 * unique profile name and also regenerate url with new profile name
			 */
			if (regionSettings != null) {
				LOG.debug("Profile name was not unique hence appending id to it to get a unique one");
				branchProfileName = branchProfileName + branch.getBranchId();
				branchProfileUrl = utils.generateBranchProfileUrl(companyProfileName, branchProfileName);
			}
			organizationSettings.setProfileName(branchProfileName);
			organizationSettings.setProfileUrl(branchProfileUrl);
			/**
			 * set profile name in branch for setting value in sql tables
			 */
			branch.setProfileName(branchProfileName);
		}
		else {
			LOG.warn("Company settings not found in generateAndSetRegionProfileNameAndUrl");
		}

		LOG.debug("Method to generate and set branch profile name and url excecuted successfully");
	}

	/**
	 * Method to form ContactDetailsSettings object from branch
	 * 
	 * @param branch
	 * @return
	 */
	private ContactDetailsSettings getContactDetailsSettingsFromBranch(Branch branch) {
		LOG.debug("Method getContactDetailsSettingsFromBranch called for branch :" + branch);
		ContactDetailsSettings contactSettings = new ContactDetailsSettings();
		contactSettings.setName(branch.getBranch());
		contactSettings.setAddress1(branch.getAddress1());
		contactSettings.setAddress2(branch.getAddress2());

		LOG.debug("Method getContactDetailsSettingsFromBranch finished.Returning :" + contactSettings);
		return contactSettings;
	}

	/**
	 * Method to form ContactDetailsSettings object from region
	 * 
	 * @param region
	 * @return
	 */
	private ContactDetailsSettings getContactDetailsSettingsFromRegion(Region region) {
		LOG.debug("Method getContactDetailsSettingsFromRegion called for branch :" + region);
		ContactDetailsSettings contactSettings = new ContactDetailsSettings();
		contactSettings.setName(region.getRegion());
		contactSettings.setAddress1(region.getAddress1());
		contactSettings.setAddress2(region.getAddress2());

		LOG.debug("Method getContactDetailsSettingsFromRegion finished.Returning :" + contactSettings);
		return contactSettings;
	}

	/**
	 * Method to add a new region
	 * 
	 * @param user
	 * @param regionName
	 * @param address1
	 * @param address2
	 * @return
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	@Override
	@Transactional
	public Region addNewRegion(User user, String regionName, int isDefaultBySystem, String address1, String address2) throws InvalidInputException,
			SolrException {
		if (user == null) {
			throw new InvalidInputException("User is null in addNewRegion");
		}
		if (regionName == null || regionName.isEmpty()) {
			throw new InvalidInputException("Region name is null in addNewRegion");
		}
		if (address1 == null || address1.isEmpty()) {
			throw new InvalidInputException("Address1 is null in addNewRegion");
		}
		LOG.info("Method add new region called for regionName : " + regionName);

		Region region = organizationManagementService.addRegion(user, isDefaultBySystem, regionName);
		region.setAddress1(address1);
		region.setAddress2(address2);

		LOG.debug("Calling method to insert region settings");
		insertRegionSettings(region);

		regionDao.update(region);

		LOG.debug("Updating solr with newly inserted region");
		solrSearchService.addOrUpdateRegionToSolr(region);

		LOG.info("Successfully completed method add new region for regionName : " + regionName);
		return region;
	}

	/**
	 * Method to generate profile name and profile url for a region and also set them in
	 * organization unit settings
	 * 
	 * @param region
	 * @return
	 * @throws InvalidInputException
	 */
	private void generateAndSetRegionProfileNameAndUrl(Region region, OrganizationUnitSettings organizationSettings) throws InvalidInputException {
		LOG.debug("Method generateAndSetRegionProfileNameAndUrl called for region: " + region);
		String regionProfileName = null;
		if (region == null) {
			throw new InvalidInputException("Region is null in generateAndSetRegionProfileNameAndUrl");
		}
		String regionName = region.getRegion();
		if (regionName == null || regionName.isEmpty()) {
			throw new InvalidInputException("Region name is null or empty in generateAndSetRegionProfileNameAndUrl");
		}

		regionProfileName = regionName.trim().replaceAll(" ", "-").toLowerCase();
		LOG.debug("Checking if profileName:" + regionProfileName + " is already taken by a region in the company :" + region.getCompany());

		OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(region.getCompany().getCompanyId(),
				MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		if (companySettings != null) {
			String companyProfileName = companySettings.getProfileName();
			String regionProfileUrl = utils.generateRegionProfileUrl(companyProfileName, regionProfileName);

			/**
			 * Uniqueness of profile name is checked by url since combination of company profile
			 * name and region profile name is unique
			 */
			OrganizationUnitSettings regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileUrl(regionProfileUrl,
					MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION);
			/**
			 * if there exists a region with the profile name formed, append region iden to get the
			 * unique profile name and also regenerate url with new profile name
			 */
			if (regionSettings != null) {
				LOG.debug("Profile name was not unique hence appending id to it to get a unique one");
				regionProfileName = regionProfileName + region.getRegionId();
				regionProfileUrl = utils.generateRegionProfileUrl(companyProfileName, regionProfileName);
			}
			organizationSettings.setProfileName(regionProfileName);
			organizationSettings.setProfileUrl(regionProfileUrl);

			/**
			 * Set the profile name in region object to update in sql later
			 */
			region.setProfileName(regionProfileName);
		}
		else {
			LOG.warn("Company settings not found in generateAndSetRegionProfileNameAndUrl");
		}

		LOG.debug("Method generateAndSetRegionProfileNameAndUrl excecuted successfully");
	}

	/**
	 * Method to update a branch
	 * 
	 * @throws SolrException
	 */
	@Override
	@Transactional
	public void updateBranch(long branchId, long regionId, String branchName, String branchAddress1, String branchAddress2, User user)
			throws InvalidInputException, SolrException {
		if (user == null) {
			throw new InvalidInputException("User is null in update branch");
		}
		if (branchName == null || branchName.isEmpty()) {
			throw new InvalidInputException("Branch name is null in update branch");
		}
		if (branchAddress1 == null || branchAddress1.isEmpty()) {
			throw new InvalidInputException("Branch address is null in update branch");
		}
		if (branchId <= 0l) {
			throw new InvalidInputException("Branch id is invalid in update branch");
		}

		LOG.info("Method update branch called for branchId:" + branchId + " ,regionId:" + regionId + " branchName : " + branchName
				+ " ,branchAddress:" + branchAddress1);
		Branch branch = branchDao.findById(Branch.class, branchId);
		if (branch == null) {
			throw new InvalidInputException("No branch present for the required id in database while updating branch");
		}
		LOG.debug("Checking if the region of branch is changed");

		/**
		 * In case of branch attached to default region, regionId is 0 hence perform update only
		 * when the regionId is not the default one
		 */
		if (regionId > 0l && regionId != branch.getRegion().getRegionId()) {
			Region region = regionDao.findById(Region.class, regionId);
			if (region == null) {
				throw new InvalidInputException("No region present for the required id in database while updating branch");
			}
			branch.setRegion(region);
		}
		branch.setBranch(branchName);
		branch.setAddress1(branchAddress1);
		branch.setAddress2(branchAddress2);
		branch.setModifiedBy(String.valueOf(user.getUserId()));
		branch.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		branchDao.update(branch);

		LOG.debug("Update branch in mongo");
		ContactDetailsSettings contactDetailsSettings = getContactDetailsSettingsFromBranch(branch);
		organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByCriteria(MongoOrganizationUnitSettingDaoImpl.KEY_CONTACT_DETAIL_SETTINGS,
				contactDetailsSettings, MongoOrganizationUnitSettingDaoImpl.KEY_IDENTIFIER, branchId,
				MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION);

		LOG.debug("Updating branch in solr");
		solrSearchService.addOrUpdateBranchToSolr(branch);
		LOG.info("Method to update branch completed successfully");
	}

	/**
	 * Method to update a region
	 * 
	 * @throws SolrException
	 */
	@Override
	@Transactional
	public void updateRegion(long regionId, String regionName, String regionAddress1, String regionAddress2, User user) throws InvalidInputException,
			SolrException {
		if (user == null) {
			throw new InvalidInputException("User is null in update region");
		}
		if (regionName == null || regionName.isEmpty()) {
			throw new InvalidInputException("Region name is null in update region");
		}
		if (regionAddress1 == null || regionAddress1.isEmpty()) {
			throw new InvalidInputException("Region address is null in update region");
		}
		if (regionId <= 0l) {
			throw new InvalidInputException("Region id is invalid in update region");
		}
		LOG.info("Method update region called for regionId:" + regionId + " branchName : " + regionName + " ,regionAddress1:" + regionAddress1);
		Region region = regionDao.findById(Region.class, regionId);
		if (region == null) {
			throw new InvalidInputException("No region present for the required id in database while updating region");
		}
		region.setRegion(regionName);
		region.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		region.setModifiedBy(String.valueOf(user.getUserId()));
		region.setAddress1(regionAddress1);
		region.setAddress2(regionAddress2);
		regionDao.update(region);

		LOG.debug("Updating region in mongo");
		ContactDetailsSettings contactDetailsSettings = getContactDetailsSettingsFromRegion(region);
		organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByCriteria(MongoOrganizationUnitSettingDaoImpl.KEY_CONTACT_DETAIL_SETTINGS,
				contactDetailsSettings, MongoOrganizationUnitSettingDaoImpl.KEY_IDENTIFIER, regionId,
				MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION);

		LOG.debug("Updating region in solr");
		solrSearchService.addOrUpdateRegionToSolr(region);

		LOG.info("Method to update region completed successfully");
	}

	/**
	 * Method to check whether a user can view region based on his profiles
	 * 
	 * @param userProfiles
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean isRegionViewAllowed(List<UserProfile> userProfiles) {
		// TODO implement this
		return true;
	}

	/**
	 * Method to check whether a user can view branch based on his profiles
	 * 
	 * @param userProfiles
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean isBranchViewAllowed(List<UserProfile> userProfiles) {
		// TODO implement this
		return true;
	}

	/**
	 * Method to check whether a user has privileges to build hierarchy
	 */
	@Override
	public boolean canBuildHierarchy(User user, AccountType accountType) {
		// TODO implement this
		return true;
	}

	/**
	 * Method to check whether a user has privileges to edit company information
	 */
	@Override
	public boolean canEditCompany(User user, AccountType accountType) {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * Method to insert region settings into mongo
	 * 
	 * @param region
	 * @throws InvalidInputException
	 */
	public void insertRegionSettings(Region region) throws InvalidInputException {
		LOG.info("Method for inserting region settings called for region : " + region);
		OrganizationUnitSettings organizationSettings = new OrganizationUnitSettings();
		organizationSettings.setIden(region.getRegionId());
		organizationSettings.setCreatedBy(region.getCreatedBy());
		organizationSettings.setCreatedOn(System.currentTimeMillis());
		organizationSettings.setModifiedBy(region.getModifiedBy());
		organizationSettings.setModifiedOn(System.currentTimeMillis());

		// Calling method to generate and set region profile name and url
		generateAndSetRegionProfileNameAndUrl(region, organizationSettings);

		ContactDetailsSettings contactSettings = getContactDetailsSettingsFromRegion(region);
		organizationSettings.setContact_details(contactSettings);
		organizationSettings.setLockSettings(new LockSettings());

		organizationUnitSettingsDao.insertOrganizationUnitSettings(organizationSettings,
				MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION);
		LOG.info("Method for inserting region settings finished");
	}

	/**
	 * Method to insert branch settings into mongo
	 * 
	 * @param branch
	 * @throws InvalidInputException
	 */
	public void insertBranchSettings(Branch branch) throws InvalidInputException {
		LOG.info("Method to insert branch settings called for branch : " + branch);
		OrganizationUnitSettings organizationSettings = new OrganizationUnitSettings();
		organizationSettings.setIden(branch.getBranchId());
		organizationSettings.setCreatedBy(branch.getCreatedBy());
		organizationSettings.setCreatedOn(System.currentTimeMillis());
		organizationSettings.setModifiedBy(branch.getModifiedBy());
		organizationSettings.setModifiedOn(System.currentTimeMillis());

		// Calling method to generate and set profile name and profile url
		generateAndSetBranchProfileNameAndUrl(branch, organizationSettings);

		ContactDetailsSettings contactSettings = getContactDetailsSettingsFromBranch(branch);
		organizationSettings.setContact_details(contactSettings);
		organizationSettings.setLockSettings(new LockSettings());

		organizationUnitSettingsDao.insertOrganizationUnitSettings(organizationSettings,
				MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION);
		LOG.info("Method to insert branch settings finished for branch : " + branch);
	}
}
// JIRA SS-37 BY RM02 EOC
