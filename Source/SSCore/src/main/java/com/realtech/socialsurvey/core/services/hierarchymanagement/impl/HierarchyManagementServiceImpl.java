package com.realtech.socialsurvey.core.services.hierarchymanagement.impl;

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
import com.realtech.socialsurvey.core.services.hierarchymanagement.HierarchyManagementService;

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

	private static final String COMPANY = "company";

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
		List<Branch> branchList = branchDao.findByColumn(Branch.class, COMPANY, company);
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
		List<Region> regionList = regionDao.findByColumn(Region.class, COMPANY, company);
		LOG.info("Region list fetched for the company " + company);
		return regionList;
	}

	/**
	 * update branch status to inactive
	 * 
	 * @param BranchId
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public void deleteBranch(long branchId) throws InvalidInputException {
		LOG.info("Update branch status to inactive on delete");

		LOG.debug("Fetch the branch object by ID");
		Branch branch = branchDao.findById(Branch.class, branchId);
		if (branch == null) {
			LOG.error("No branch present with the branch Id :" + branchId);
			throw new InvalidInputException("No branch present with the branch Id :" + branchId);
		}

		// set branch status as inactive
		branch.setStatus(CommonConstants.STATUS_INACTIVE);
		// update the branch status in database
		branchDao.update(branch);
		LOG.info("Branch status for branch ID :" + branchId + "/t successfully updated to inacitive");
	}

	/**
	 * update region status to inactive
	 * 
	 * @param regionId
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public void deleteRegion(long regionId) throws InvalidInputException {
		LOG.info("Update region status to inactive on delete");
		Region region = regionDao.findById(Region.class, regionId);
		if (region == null) {
			LOG.error("No region present with the region Id :" + regionId);
			throw new InvalidInputException("No region present with the region Id :" + regionId);
		}
		// set region status as inactive
		region.setStatus(CommonConstants.STATUS_INACTIVE);
		// update the region status in the database
		regionDao.update(region);
		LOG.info("Region status for region ID :" + regionId + "/t successfully updated to inacitive");
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
			int numberOfBranches = getBranchesCount(user.getCompany());
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
	private int getBranchesCount(Company company) {
		LOG.debug("Getting branches count for : " + company);

		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.COMPANY_NAME, company);
		queries.put(CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.STATUS_INACTIVE);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		long branchesCount = branchDao.findNumberOfRowsByKeyValue(Branch.class, queries);

		LOG.debug("Returning branches count for : " + company + " branches count is : " + branchesCount);
		return 0;

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
}
// JIRA SS-37 BY RM02 EOC
