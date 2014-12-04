package com.realtech.socialsurvey.core.services.hierarchymanagement.impl;

import java.util.List;
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
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.hierarchymanagement.HierarchyManagementServices;

// JIRA SS-37 BY RM02 BOC

/**
 * Implementation class for Hierarchy management services
 */
@Component
public class HierarchyManagementServiceImpl implements HierarchyManagementServices {

	private static final Logger LOG = LoggerFactory.getLogger(HierarchyManagementServiceImpl.class);

	@Autowired
	private GenericDao<Branch, Integer> branchDao;

	@Autowired
	private GenericDao<Region, Integer> regionDao;

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
	public void deleteBranch(int branchId) throws InvalidInputException {
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
		branchDao.saveOrUpdate(branch);
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
	public void deleteRegion(int regionId) throws InvalidInputException {
		LOG.info("Update region status to inactive on delete");
		LOG.debug("Fetch the branch object by ID");
		Region region = regionDao.findById(Region.class, regionId);
		if (region == null) {
			LOG.error("No branch present with the region Id :" + regionId);
			throw new InvalidInputException("No branch present with the region Id :" + regionId);
		}
		// set region status as inactive
		region.setStatus(CommonConstants.STATUS_INACTIVE);
		// update the region status in the database
		regionDao.saveOrUpdate(region);
		LOG.info("Branch status for region ID :" + regionId + "/t successfully updated to inacitive");
	}

}
// JIRA SS-37 BY RM02 EOC
