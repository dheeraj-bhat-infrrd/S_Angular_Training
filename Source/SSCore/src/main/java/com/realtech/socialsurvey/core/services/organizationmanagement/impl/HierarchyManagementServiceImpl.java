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
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.HierarchyManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;

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
	private GenericDao<Region, Long> regionDao;

	@Autowired
	private GenericDao<UserProfile, Long> userProfileDao;

	@Autowired
	private OrganizationManagementService organizationManagementService;

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
		Map<String,Object> queries = new HashMap<String,Object>();
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

		Map<String,Object> queries = new HashMap<String,Object>();
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
	 */
	@Override
	@Transactional
	public void updateBranchStatus(User user, long branchId, int status) throws InvalidInputException {
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
		LOG.info("Branch status for branch ID :" + branchId + "/t successfully updated to:" + status);
	}

	/**
	 * Updates the status of region
	 * 
	 * @param regionId
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public void updateRegionStatus(User user, long regionId, int status) throws InvalidInputException {
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
	public List<Branch> getAllBranchesForRegion(long regionId) throws InvalidInputException {
		if (regionId <= 0l) {
			throw new InvalidInputException("RegionId is not set in getAllBranchesForRegion");
		}
		Region region = regionDao.findById(Region.class, regionId);
		if (region == null) {
			LOG.error("No region present with the region Id :" + regionId);
			throw new InvalidInputException("No region present with the region Id :" + regionId);
		}
		LOG.info("Fetching the list of branches for region :" + region);
		
		Map<String,Object> queries = new HashMap<String,Object>();
		queries.put(CommonConstants.REGION_COLUMN, region);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		List<Branch> branchList = branchDao.findByKeyValue(Branch.class, queries);
		
		LOG.info("Branch list fetched for the region " + region);
		return branchList;
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
	public List<UserProfile> getAllUserProfilesForBranch(long branchId) throws InvalidInputException {
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
	 * @return
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public Branch addNewBranch(User user, long regionId, String branchName) throws InvalidInputException {
		if (user == null) {
			throw new InvalidInputException("User is null in addNewBranch");
		}
		if (branchName == null || branchName.isEmpty()) {
			throw new InvalidInputException("Branch name is null in addNewBranch");
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

		Branch branch = organizationManagementService.addBranch(user, region, branchName, CommonConstants.NO);
		LOG.info("Successfully completed method add new branch for regionId : " + region.getRegionId() + " and branchName : " + branchName);
		return branch;

	}

	/**
	 * Method to add a new region
	 * 
	 * @param user
	 * @param regionName
	 * @return
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public Region addNewRegion(User user, String regionName) throws InvalidInputException {
		if (user == null) {
			throw new InvalidInputException("User is null in addNewRegion");
		}
		if (regionName == null || regionName.isEmpty()) {
			throw new InvalidInputException("Region name is null in addNewRegion");
		}
		LOG.info("Method add new region called for regionName : " + regionName);

		Region region = organizationManagementService.addRegion(user, CommonConstants.NO, regionName);

		LOG.info("Successfully completed method add new region for regionName : " + regionName);
		return region;
	}

	/**
	 * Method to update a branch
	 */
	@Override
	@Transactional
	public void updateBranch(long branchId, long regionId, String branchName, String branchAddress, User user) throws InvalidInputException {
		if (user == null) {
			throw new InvalidInputException("User is null in update branch");
		}
		if (branchName == null || branchName.isEmpty()) {
			throw new InvalidInputException("Branch name is null in update branch");
		}
		if (branchAddress == null || branchAddress.isEmpty()) {
			throw new InvalidInputException("Branch address is null in update branch");
		}
		if (branchId <= 0l) {
			throw new InvalidInputException("Branch id is invalid in update branch");
		}

		LOG.info("Method update branch called for branchId:" + branchId + " ,regionId:" + regionId + " branchName : " + branchName
				+ " ,branchAddress:" + branchAddress);
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
		branch.setModifiedBy(String.valueOf(user.getUserId()));
		branch.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		// TODO update address details
		branchDao.update(branch);
		LOG.info("Method to update branch completed successfully");
	}

	/**
	 * Method to update a region
	 */
	@Override
	@Transactional
	public void updateRegion(long regionId, String regionName, String regionAddress, User user) throws InvalidInputException {
		if (user == null) {
			throw new InvalidInputException("User is null in update region");
		}
		if (regionName == null || regionName.isEmpty()) {
			throw new InvalidInputException("Region name is null in update region");
		}
		if (regionAddress == null || regionAddress.isEmpty()) {
			throw new InvalidInputException("Region address is null in update region");
		}
		if (regionId <= 0l) {
			throw new InvalidInputException("Region id is invalid in update region");
		}
		LOG.info("Method update region called for regionId:" + regionId + " branchName : " + regionName + " ,regionAddress:" + regionAddress);
		Region region = regionDao.findById(Region.class, regionId);
		if (region == null) {
			throw new InvalidInputException("No region present for the required id in database while updating region");
		}
		region.setRegion(regionName);
		region.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		region.setModifiedBy(String.valueOf(user.getUserId()));
		// TODO update address
		regionDao.update(region);
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

}
// JIRA SS-37 BY RM02 EOC
