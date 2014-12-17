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
	public boolean isBranchAdditionAllowed(User user, AccountType accountType) throws InvalidInputException {
		LOG.info("Method to check if further branch addition is allowed, called for user : " + user);
		if (user == null) {
			throw new InvalidInputException("User is null in isMaxBranchAdditionExceeded");
		}
		if (accountType == null) {
			throw new InvalidInputException("Account type is null in isMaxBranchAdditionExceeded");
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
				throw new InvalidInputException("Account type is invalid in isMaxBranchAdditionExceeded");
		}
		LOG.info("Returning from isBranchAdditionAllowed for user : " + user.getUserId() + " isMaxBranchAdditionExceeded is :"
				+ isBranchAdditionAllowed);
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
			throw new InvalidInputException("User is null in isMaxRegionAdditionExceeded");
		}
		if (accountType == null) {
			throw new InvalidInputException("Account type is null in isMaxRegionAdditionExceeded");
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
		LOG.info("Returning from isRegionAdditionAllowed for user : " + user.getUserId() + " isRegionAdditionAllowed is :"
				+ isRegionAdditionAllowed);
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
