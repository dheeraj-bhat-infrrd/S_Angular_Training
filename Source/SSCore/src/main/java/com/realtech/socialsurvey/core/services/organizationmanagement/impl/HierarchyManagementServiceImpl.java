package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
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

	@Resource
	@Qualifier("userProfile")
	private UserProfileDao userProfileDao;

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
		List<Branch> branchList = branchDao.findByColumn(Branch.class, CommonConstants.COMPANY_COLUMN, company);
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
		List<Region> regionList = regionDao.findByColumn(Region.class, CommonConstants.COMPANY_COLUMN, company);
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
	public boolean isMaxBranchAdditionExceeded(User user, AccountType accountType) throws InvalidInputException {
		LOG.info("Method to check if max branch addition exceeded is called for user : " + user);
		if (user == null) {
			throw new InvalidInputException("User is null in isMaxBranchAdditionExceeded");
		}
		if (accountType == null) {
			throw new InvalidInputException("Account type is null in isMaxBranchAdditionExceeded");
		}
		boolean isMaxBranchAdditionExceeded = false;
		switch (accountType) {
			case INDIVIDUAL:
				// No branch addition is allowed for individual
				isMaxBranchAdditionExceeded = true;
				break;
			case TEAM:
				isMaxBranchAdditionExceeded = isMaxBranchAdditionExceeded(user, CommonConstants.MAX_BRANCH_LIMIT_TEAM);
				break;
			case COMPANY:
				isMaxBranchAdditionExceeded = isMaxBranchAdditionExceeded(user, CommonConstants.NO_LIMIT);
				break;
			case ENTERPRISE:
				isMaxBranchAdditionExceeded = isMaxBranchAdditionExceeded(user, CommonConstants.NO_LIMIT);
				break;
			default:
				throw new InvalidInputException("Account type is invalid in isMaxBranchAdditionExceeded");
		}
		LOG.info("Returning from isMaxBranchAdditionExceeded for user : " + user.getUserId() + " isMaxBranchAdditionExceeded is :"
				+ isMaxBranchAdditionExceeded);
		return isMaxBranchAdditionExceeded;
	}

	/**
	 * Method to check if the maximum number of branches allowed to be added has exceeded comparing
	 * already existing branches with max allowed branches passed
	 * 
	 * @param user
	 * @return
	 */
	private boolean isMaxBranchAdditionExceeded(User user, int maxBranchesAllowed) {
		LOG.debug("Method isMaxBranchAdditionExceeded called for user : " + user.getUserId() + " and maxBranchesAllowed :" + maxBranchesAllowed);
		boolean isMaxBranchAdditionExceededForTeam = false;
		if (maxBranchesAllowed != CommonConstants.NO_LIMIT) {
			long numberOfBranches = getBranchesCount(user.getCompany());
			if (numberOfBranches == maxBranchesAllowed) {
				isMaxBranchAdditionExceededForTeam = true;
			}
		}
		else {
			LOG.debug("No limit present for branch addition for user : " + user.getUserId());
		}

		LOG.debug("Method isMaxBranchAdditionExceededForTeam finished for user : " + user.getUserId() + " Returning : "
				+ isMaxBranchAdditionExceededForTeam);
		return isMaxBranchAdditionExceededForTeam;
	}

	/**
	 * Method to get the count of branches for a company
	 * 
	 * @param company
	 * @return
	 */
	private long getBranchesCount(Company company) {
		LOG.debug("Getting branches count for : " + company);

		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.COMPANY, company);
		queries.put(CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.STATUS_INACTIVE);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		long branchesCount = branchDao.findNumberOfRowsByKeyValue(Branch.class, queries);

		LOG.debug("Returning branches count for : " + company + " branches count is : " + branchesCount);
		return branchesCount;

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
	public boolean isMaxRegionAdditionExceeded(User user, AccountType accountType) throws InvalidInputException {
		LOG.info("Method to check if max region addition exceeded is called for user : " + user);
		if (user == null) {
			throw new InvalidInputException("User is null in isMaxRegionAdditionExceeded");
		}
		if (accountType == null) {
			throw new InvalidInputException("Account type is null in isMaxRegionAdditionExceeded");
		}
		boolean isMaxRegionAdditionExceeded = false;
		switch (accountType) {
			case INDIVIDUAL:
				// No region addition is allowed for individual
				isMaxRegionAdditionExceeded = true;
				break;
			case TEAM:
				// No region addition is allowed for team
				isMaxRegionAdditionExceeded = true;
				break;
			case COMPANY:
				isMaxRegionAdditionExceeded = isMaxRegionAdditionExceeded(user, CommonConstants.MAX_REGION_LIMIT_COMPANY);
				break;
			case ENTERPRISE:
				isMaxRegionAdditionExceeded = isMaxRegionAdditionExceeded(user, CommonConstants.NO_LIMIT);
				break;
			default:
				throw new InvalidInputException("Account type is invalid in isMaxRegionAdditionExceeded");
		}
		LOG.info("Returning from isMaxRegionAdditionExceeded for user : " + user.getUserId() + " isMaxRegionAdditionExceeded is :"
				+ isMaxRegionAdditionExceeded);
		return isMaxRegionAdditionExceeded;
	}

	/**
	 * Method to check if the maximum number of regions allowed to be added has exceeded comparing
	 * already existing regions with max allowed regions passed
	 * 
	 * @param user
	 * @param maxRegionsAllowed
	 * @return
	 */
	private boolean isMaxRegionAdditionExceeded(User user, int maxRegionsAllowed) {
		LOG.debug("Method isMaxRegionAdditionExceeded called for user : " + user.getUserId() + " and maxRegionsAllowed :" + maxRegionsAllowed);
		boolean isMaxRegionAdditionExceededForTeam = false;
		if (maxRegionsAllowed != CommonConstants.NO_LIMIT) {
			long numberOfRegions = getRegionsCount(user.getCompany());
			if (numberOfRegions == maxRegionsAllowed) {
				isMaxRegionAdditionExceededForTeam = true;
			}
		}
		else {
			LOG.debug("No limit present for region addition for user : " + user.getUserId());
		}
		LOG.debug("Method isMaxRegionAdditionExceeded finished for user : " + user.getUserId() + " Returning : " + isMaxRegionAdditionExceededForTeam);
		return isMaxRegionAdditionExceededForTeam;
	}

	/**
	 * Method to get count of regions
	 * 
	 * @param company
	 * @return
	 */
	private long getRegionsCount(Company company) {
		LOG.debug("Getting regions count for : " + company);

		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.COMPANY_NAME, company.getCompany());
		queries.put(CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.STATUS_INACTIVE);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		long regionsCount = regionDao.findNumberOfRowsByKeyValue(Region.class, queries);

		LOG.debug("Returning regions count for : " + company + " regions count is : " + regionsCount);
		return regionsCount;
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
			queries.put(CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.IS_DEFAULT_BY_SYSTEM_YES);
			List<Region> regions = regionDao.findByKeyValue(Region.class, queries);
			if (regions != null && !regions.isEmpty()) {
				region = regions.get(0);
			}
		}
		if (region == null) {
			throw new InvalidInputException("No region is present in db for the company while adding branch");
		}

		Branch branch = organizationManagementService.addBranch(user, region, branchName, CommonConstants.STATUS_INACTIVE);
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

		Region region = organizationManagementService.addRegion(user, CommonConstants.STATUS_INACTIVE, regionName);

		LOG.info("Successfully completed method add new region for regionName : " + regionName);
		return region;
	}
}
// JIRA SS-37 BY RM02 EOC
