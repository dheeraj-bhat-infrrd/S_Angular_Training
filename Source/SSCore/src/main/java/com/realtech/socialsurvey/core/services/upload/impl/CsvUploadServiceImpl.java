package com.realtech.socialsurvey.core.services.upload.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchUploadVO;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RegionUploadVO;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserUploadVO;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.BranchAdditionException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.RegionAdditionException;
import com.realtech.socialsurvey.core.exception.UserAdditionException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserAssignmentException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.upload.CsvUploadService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;

@Component
public class CsvUploadServiceImpl implements CsvUploadService {

	@Autowired
	private OrganizationManagementService organizationManagementService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private GenericDao<Branch, Long> branchDao;

	@Autowired
	private GenericDao<Region, Long> regionDao;

	@Autowired
	private EncryptionHelper encryptionHelper;

	@Autowired
	private SolrSearchService solrSearchService;

	@Autowired
	private UserManagementService userManagementService;

	private static Logger LOG = LoggerFactory.getLogger(CsvUploadServiceImpl.class);

	/**
	 * Parses a csv and returns a map of lists of users,branches and regions
	 * 
	 * @param fileName
	 * @return
	 */
	@Transactional
	@Override
	public Map<String, List<Object>> parseCsv(String fileName) {

		Map<String, List<Object>> uploadObjects = parseTestFile(fileName);

		return uploadObjects;
	}

	private boolean validateUser(String emailId, Company company) {
		boolean status = false;
		try {
			userDao.getActiveUser(emailId);
		}
		catch (NoRecordsFetchedException e) {
			status = true;
		}
		return status;
	}

	private boolean validateBranch(BranchUploadVO branchUploadVO, Company company) throws InvalidInputException, NoRecordsFetchedException,
			BranchAdditionException {
		boolean status = false;

		if (branchUploadVO.isAssignToCompany()) {
			Region defaultRegion = organizationManagementService.getDefaultRegionForCompany(company);
			Map<String, Object> queries = new HashMap<String, Object>();
			queries.put(CommonConstants.BRANCH_NAME_COLUMN, branchUploadVO.getBranchName());
			queries.put(CommonConstants.REGION_COLUMN, defaultRegion);
			queries.put(CommonConstants.COMPANY_COLUMN, company);
			List<Branch> existingBranches = branchDao.findByKeyValue(Branch.class, queries);
			if (existingBranches == null || existingBranches.isEmpty()) {
				status = true;
			}
		}
		else if (branchUploadVO.getAssignedRegionName() != null && !branchUploadVO.getAssignedRegionName().isEmpty()) {
			Map<String, Object> queries = new HashMap<String, Object>();
			queries.put(CommonConstants.COMPANY_COLUMN, company);
			queries.put(CommonConstants.REGION_NAME_COLUMN, branchUploadVO.getAssignedRegionName());
			List<Region> regions = regionDao.findByKeyValue(Region.class, queries);
			if (regions == null || regions.isEmpty()) {
				LOG.error("Invalid region name given!");
				throw new BranchAdditionException("Invalid region name given!");
			}
			queries = new HashMap<String, Object>();
			queries.put(CommonConstants.BRANCH_NAME_COLUMN, branchUploadVO.getBranchName());
			queries.put(CommonConstants.REGION_COLUMN, regions.get(CommonConstants.INITIAL_INDEX));
			queries.put(CommonConstants.COMPANY_COLUMN, company);
			List<Branch> existingBranches = branchDao.findByKeyValue(Branch.class, queries);
			if (existingBranches == null || existingBranches.isEmpty()) {
				status = true;
			}
		}
		else {
			LOG.error("Please specify where the branch belongs");
			throw new BranchAdditionException("Please specify where the branch belongs");
		}
		return status;
	}

	private boolean validateRegion(RegionUploadVO regionUploadVO, Company company) {

		List<Region> regions = null;
		boolean status = false;

		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.REGION_NAME_COLUMN, regionUploadVO.getRegionName());
		queries.put(CommonConstants.COMPANY_COLUMN, company);
		regions = regionDao.findByKeyValue(Region.class, queries);

		if (regions == null || regions.isEmpty()) {
			status = true;
		}

		return status;
	}

	private Company getCompany(User user) throws InvalidInputException {
		Company company = user.getCompany();
		if (company == null) {
			LOG.error("Company property not found in admin user object!");
			throw new InvalidInputException("Company property not found in admin user object!");

		}
		return company;
	}

	private LicenseDetail getLicenseDetail(Company company) throws InvalidInputException {
		LicenseDetail companyLicenseDetail = null;
		if (company.getLicenseDetails() != null && !company.getLicenseDetails().isEmpty()) {
			companyLicenseDetail = company.getLicenseDetails().get(CommonConstants.INITIAL_INDEX);
		}
		else {
			LOG.error("License Detail property not found in admin user's company object!");
			throw new InvalidInputException("License Detail property not found in admin user's company object!");
		}
		return companyLicenseDetail;
	}

	private void addUser(UserUploadVO user, User adminUser) throws InvalidInputException, NoRecordsFetchedException, SolrException,
			UserAssignmentException, UserAdditionException {

		if (user.isBelongsToCompany()) {
			// He belongs to the company
			LOG.debug("Adding user : " + user.getEmailId() + " belongs to company");
			organizationManagementService.addIndividual(adminUser, 0, 0, 0, new String[] { user.getEmailId() }, false);
		}
		else if (user.getAssignedBranchName() != null) {
			// He belongs to a branch
			LOG.debug("Adding user : " + user.getEmailId() + " belongs to branch : " + user.getAssignedBranchName());
			Map<String, Object> queries = new HashMap<String, Object>();
			queries.put(CommonConstants.BRANCH_NAME_COLUMN, user.getAssignedBranchName());
			queries.put(CommonConstants.COMPANY_COLUMN, adminUser.getCompany());
			queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
			List<Branch> branches = branchDao.findByKeyValue(Branch.class, queries);
			if (branches == null || branches.isEmpty()) {
				LOG.error("Branch name is invalid!");
				throw new UserAdditionException("Branch name is invalid!");
			}
			if (branches.size() > 1) {
				LOG.error("Ambuigity in assigning to branch. More than one braches of the name found");
				throw new UserAdditionException("Ambuigity in assigning to branch. More than one braches of the name found");
			}
			else {
				Branch branch = branches.get(CommonConstants.INITIAL_INDEX);
				if (user.isBranchAdmin()) {
					LOG.debug("User is the branch admin");
					organizationManagementService.addIndividual(adminUser, 0, branch.getBranchId(), branch.getRegion().getRegionId(),
							new String[] { user.getEmailId() }, true);
					LOG.debug("Added user : " + user.getEmailId());
				}
				else {
					LOG.debug("User is not the branch admin");
					organizationManagementService.addIndividual(adminUser, 0, branch.getBranchId(), branch.getRegion().getRegionId(),
							new String[] { user.getEmailId() }, false);
					LOG.debug("Added user : " + user.getEmailId());
				}
			}
		}
		else if (user.getAssignedRegionName() != null) {
			// He belongs to the region
			LOG.debug("Adding user : " + user.getEmailId() + " belongs to region : " + user.getAssignedRegionName());
			Map<String, Object> queries = new HashMap<String, Object>();
			queries.put(CommonConstants.REGION_NAME_COLUMN, user.getAssignedRegionName());
			queries.put(CommonConstants.COMPANY_COLUMN, adminUser.getCompany());
			queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
			List<Region> regions = regionDao.findByKeyValue(Region.class, queries);
			if (regions == null || regions.isEmpty()) {
				LOG.error("Region name is invalid!");
				throw new UserAdditionException("Region name is invalid!");
			}
			else {
				Region region = regions.get(CommonConstants.INITIAL_INDEX);
				if (user.isRegionAdmin()) {
					LOG.debug("User is the region admin.");
					organizationManagementService.addIndividual(adminUser, 0, 0, region.getRegionId(), new String[] { user.getEmailId() }, true);
					LOG.debug("Added user : " + user.getEmailId());
				}
				else {
					LOG.debug("User is not the admin of the region");
					organizationManagementService.addIndividual(adminUser, 0, 0, region.getRegionId(), new String[] { user.getEmailId() }, false);
					LOG.debug("Added user : " + user.getEmailId());
				}
			}
		}
		else {
			LOG.error("Please specifiy where the user belongs!");
			throw new UserAdditionException("Please specifiy where the user belongs!");
		}

	}

	private void assignUser(UserUploadVO user, User adminUser) throws UserAdditionException, InvalidInputException, SolrException,
			NoRecordsFetchedException, UserAssignmentException {

		LOG.info("User already exists so assigning user to approprite place");
		List<User> assigneeUsers = userDao.findByColumn(User.class, CommonConstants.EMAIL_ID, user.getEmailId());
		if (assigneeUsers == null || assigneeUsers.isEmpty()) {
			LOG.error("User : " + user.getEmailId() + " not found in the database");
			throw new UserAdditionException("User : " + user.getEmailId() + " not found in the database");
		}
		User assigneeUser = assigneeUsers.get(CommonConstants.INITIAL_INDEX);

		if (user.isBelongsToCompany()) {
			LOG.debug("Assigning user id : " + assigneeUser.getUserId());
			organizationManagementService.addIndividual(adminUser, assigneeUser.getUserId(), 0, 0, null, false);
		}
		else if (user.getAssignedBranchName() != null) {
			// He belongs to a branch
			LOG.debug("Assigning user : " + user.getEmailId() + " belongs to branch : " + user.getAssignedBranchName());
			Map<String, Object> queries = new HashMap<String, Object>();
			queries.put(CommonConstants.BRANCH_NAME_COLUMN, user.getAssignedBranchName());
			queries.put(CommonConstants.COMPANY_COLUMN, adminUser.getCompany());
			queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
			List<Branch> branches = branchDao.findByKeyValue(Branch.class, queries);
			if (branches == null || branches.isEmpty()) {
				LOG.error("Branch name is invalid!");
				throw new UserAdditionException("Branch name is invalid!");
			}
			if (branches.size() > 1) {
				LOG.error("Ambuigity in assigning to branch. More than one braches of the name found");
				throw new UserAdditionException("Ambuigity in assigning to branch. More than one braches of the name found");
			}
			else {
				Branch branch = branches.get(CommonConstants.INITIAL_INDEX);
				if (user.isBranchAdmin()) {
					LOG.debug("User is the branch admin");
					organizationManagementService.addIndividual(adminUser, assigneeUser.getUserId(), branch.getBranchId(), branch.getRegion()
							.getRegionId(), null, true);
					LOG.debug("Added user : " + user.getEmailId());
				}
				else {
					LOG.debug("User is not the branch admin");
					organizationManagementService.addIndividual(adminUser, assigneeUser.getUserId(), branch.getBranchId(), branch.getRegion()
							.getRegionId(), null, false);
					LOG.debug("Added user : " + user.getEmailId());
				}
			}
		}
		else if (user.getAssignedRegionName() != null) {
			// He belongs to the region
			LOG.debug("Assigning user : " + user.getEmailId() + " belongs to region : " + user.getAssignedRegionName());
			Map<String, Object> queries = new HashMap<String, Object>();
			queries.put(CommonConstants.REGION_NAME_COLUMN, user.getAssignedRegionName());
			queries.put(CommonConstants.COMPANY_COLUMN, adminUser.getCompany());
			queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
			List<Region> regions = regionDao.findByKeyValue(Region.class, queries);
			if (regions == null || regions.isEmpty()) {
				LOG.error("Region name is invalid!");
				throw new UserAdditionException("Region name is invalid!");
			}
			else {
				Region region = regions.get(CommonConstants.INITIAL_INDEX);
				if (user.isRegionAdmin()) {
					LOG.debug("User is the region admin.");
					organizationManagementService.addIndividual(adminUser, assigneeUser.getUserId(), 0, region.getRegionId(), null, true);
					LOG.debug("Added user : " + user.getEmailId());
				}
				else {
					LOG.debug("User is not the admin of the region");
					organizationManagementService.addIndividual(adminUser, assigneeUser.getUserId(), 0, region.getRegionId(), null, false);
					LOG.debug("Added user : " + user.getEmailId());
				}
			}
		}

	}

	/**
	 * Creates a user and assigns him under the appropriate branch or region else company.
	 * 
	 * @param adminUser
	 * @param user
	 * @throws InvalidInputException
	 * @throws UserAdditionException
	 * @throws NoRecordsFetchedException
	 * @throws SolrException
	 * @throws UserAssignmentException
	 */
	@Transactional
	@Override
	public void createUser(User adminUser, UserUploadVO user) throws InvalidInputException, UserAdditionException, NoRecordsFetchedException,
			SolrException, UserAssignmentException {

		if (adminUser == null) {
			LOG.error("admin user parameter is null!");
			throw new InvalidInputException("admin user parameter is null!");
		}
		if (user == null) {
			LOG.error("user parameter is null!");
			throw new InvalidInputException("user parameter is null!");
		}

		LOG.info("createUser called to create user : " + user.getEmailId());
		Company company = getCompany(adminUser);
		LicenseDetail companyLicenseDetail = getLicenseDetail(company);

		if (companyLicenseDetail.getAccountsMaster().getMaxUsersAllowed() != CommonConstants.NO_LIMIT) {
			if (userDao.getUsersCountForCompany(company) >= companyLicenseDetail.getAccountsMaster().getMaxUsersAllowed()) {
				LOG.error("Max number of users added! Cannot add more users.");
				throw new UserAdditionException("Max number of users added! Cannot add more users.");
			}
			else {
				if (!user.getEmailId().matches(CommonConstants.EMAIL_REGEX)) {
					LOG.error("Email id for the user is invalid!");
					throw new UserAdditionException("Email id for the user is invalid!");
				}
			}
		}

		if (validateUser(user.getEmailId(), company)) {
			LOG.debug("Validations complete, adding user!");
			addUser(user, adminUser);
			LOG.debug("User added!");
		}
		else {
			LOG.debug("User already exists. Assigning him appropriately");
			assignUser(user, adminUser);
			LOG.debug("User assigned");
		}

	}

	/**
	 * Creates a branch and assigns it under the appropriate region or company
	 * 
	 * @param adminUser
	 * @param branch
	 * @throws InvalidInputException
	 * @throws BranchAdditionException
	 * @throws SolrException
	 * @throws NoRecordsFetchedException
	 */
	@Transactional
	@Override
	public void createBranch(User adminUser, BranchUploadVO branch) throws InvalidInputException, BranchAdditionException, SolrException,
			NoRecordsFetchedException {
		if (adminUser == null) {
			LOG.error("admin user parameter is null!");
			throw new InvalidInputException("admin user parameter is null!");
		}
		if (branch == null) {
			LOG.error("branch parameter is null!");
			throw new InvalidInputException("branch parameter is null!");
		}

		LOG.info("createBranch called to create branch :  " + branch.getBranchName());
		Company company = getCompany(adminUser);
		LicenseDetail companyLicenseDetail = getLicenseDetail(company);

		if (organizationManagementService.isBranchAdditionAllowed(adminUser,
				AccountType.getAccountType(companyLicenseDetail.getAccountsMaster().getAccountsMasterId()))) {

			if (!validateBranch(branch, company)) {
				LOG.error("Branch with the name already exists!");
				throw new BranchAdditionException("Branch with the name already exists!");
			}

			if (branch.getAssignedRegionName() != null) {
				// He belongs to the region
				LOG.debug("Adding branch : " + branch.getBranchName() + " belongs to region : " + branch.getAssignedRegionName());
				Map<String, Object> queries = new HashMap<String, Object>();
				queries.put(CommonConstants.REGION_NAME_COLUMN, branch.getAssignedRegionName());
				queries.put(CommonConstants.COMPANY_COLUMN, adminUser.getCompany());
				queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
				List<Region> regions = regionDao.findByKeyValue(Region.class, queries);
				if (regions == null || regions.isEmpty()) {
					LOG.error("Region name is invalid!");
					throw new BranchAdditionException("Region name is invalid!");
				}
				else {
					Region region = regions.get(CommonConstants.INITIAL_INDEX);
					organizationManagementService.addNewBranch(adminUser, region.getRegionId(), CommonConstants.NO, branch.getBranchName(),
							branch.getBranchAddress1(), branch.getBranchAddress2());
					LOG.debug("Branch added!");
				}
			}
			else if (branch.isAssignToCompany()) {
				LOG.debug("adding branch : " + branch.getBranchName() + " under the company!");
				organizationManagementService.addNewBranch(adminUser, 0l, CommonConstants.NO, branch.getBranchName(), branch.getBranchAddress1(),
						branch.getBranchAddress2());
				LOG.debug("Branch added!");
			}
			else {
				LOG.error("Please specifiy where the branch belongs!");
				throw new BranchAdditionException("Please specifiy where the branch belongs!");
			}
		}
		else {
			LOG.error("admin user : " + adminUser.getEmailId() + " is not authorized to add branches! Accounttype : "
					+ companyLicenseDetail.getAccountsMaster().getAccountName());
			throw new BranchAdditionException("admin user : " + adminUser.getEmailId() + " is not authorized to add branches!");
		}
	}

	/**
	 * Creates a region
	 * 
	 * @param adminUser
	 * @param region
	 * @throws InvalidInputException
	 * @throws RegionAdditionException
	 * @throws SolrException
	 */
	@Transactional
	@Override
	public void createRegion(User adminUser, RegionUploadVO region) throws InvalidInputException, RegionAdditionException, SolrException {
		if (adminUser == null) {
			LOG.error("admin user parameter is null!");
			throw new InvalidInputException("admin user parameter is null!");
		}
		if (region == null) {
			LOG.error("region parameter is null!");
			throw new InvalidInputException("region parameter is null!");
		}
		LOG.info("createRegion called to add region : " + region.getRegionName());
		Company company = getCompany(adminUser);
		LicenseDetail licenseDetail = getLicenseDetail(company);

		if (organizationManagementService.isRegionAdditionAllowed(adminUser,
				AccountType.getAccountType(licenseDetail.getAccountsMaster().getAccountsMasterId()))) {
			if (!validateRegion(region, company)) {
				LOG.error("Region with that name already exists!");
				throw new RegionAdditionException("Region with that name already exists!");
			}
			LOG.debug("Adding region : " + region.getRegionName());
			Region newRegion = organizationManagementService.addNewRegion(adminUser, region.getRegionName(), CommonConstants.NO,
					region.getRegionAddress1(), region.getRegionAddress2());
			organizationManagementService.addNewBranch(adminUser, newRegion.getRegionId(), CommonConstants.YES, CommonConstants.DEFAULT_BRANCH_NAME,
					CommonConstants.DEFAULT_ADDRESS, null);
		}
		else {
			LOG.error("admin user : " + adminUser.getEmailId() + " is not authorized to add regions");
			throw new RegionAdditionException("admin user : " + adminUser.getEmailId() + " is not authorized to add regions");
		}

	}

	/**
	 * Used to get the admin user while testing
	 * 
	 * @return
	 */
	@Transactional
	@Override
	public User getUser(long userId) {
		User adminUser = userDao.findById(User.class, userId);
		return adminUser;
	}

	/**
	 * Takes a map of objects and creates them and returns list of errors if any
	 * 
	 * @param uploadObjects
	 * @param adminUser
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws SolrException
	 * @throws UserAssignmentException
	 */
	@Transactional
	@Override
	public List<String> createAndReturnErrors(Map<String, List<Object>> uploadObjects, User adminUser) throws InvalidInputException,
			NoRecordsFetchedException, SolrException, UserAssignmentException {

		if (uploadObjects == null || uploadObjects.isEmpty()) {
			LOG.error("uploadObjects parameter is null or empty!");
			throw new InvalidInputException("uploadObjects parameter is null or empty!");
		}
		List<String> errorList = new ArrayList<String>();
		LOG.info("Creating all the users,branches and regions");

		if (adminUser.getStatus() == CommonConstants.STATUS_NOT_VERIFIED) {
			LOG.error("User has not verified account. So region addition not allowed!");
			errorList.add("ERROR : Account has not been verified!");
			return errorList;
		}

		// We create all the regions
		List<Object> regionUploadVOs = uploadObjects.get(CommonConstants.REGIONS_MAP_KEY);
		if (regionUploadVOs != null && !regionUploadVOs.isEmpty()) {
			LOG.debug("Creating all the regions");
			RegionUploadVO region = null;
			for (Object regionUploadVO : regionUploadVOs) {
				try {
					region = (RegionUploadVO) regionUploadVO;
					LOG.debug("Creating region : " + region.getRegionName());
					createRegion(adminUser, region);
					region = null;
				}
				catch (RegionAdditionException e) {
					LOG.error("ERROR : " + " while adding region : " + region.getRegionName() + " message : " + e.getMessage());
					errorList.add("ERROR : " + " while adding region : " + region.getRegionName() + " message : " + e.getMessage());
				}
			}
			LOG.debug("Creation of all regions complete!");
		}

		// We create all the branches
		List<Object> branchUploadVOs = uploadObjects.get(CommonConstants.BRANCHES_MAP_KEY);
		if (branchUploadVOs != null && !branchUploadVOs.isEmpty()) {
			LOG.debug("Creating all the branches");
			BranchUploadVO branch = null;
			for (Object branchUploadVO : branchUploadVOs) {
				try {
					branch = (BranchUploadVO) branchUploadVO;
					LOG.debug("Creating branch : " + branch.getBranchName());
					createBranch(adminUser, branch);
					branch = null;
				}
				catch (BranchAdditionException e) {
					LOG.error("ERROR : " + " while adding branch : " + branch.getBranchName() + " message : " + e.getMessage());
					errorList.add("ERROR : " + " while adding branch : " + branch.getBranchName() + " message : " + e.getMessage());
				}
			}
			LOG.debug("Creation of all branches complete!");
		}

		// First we create all the users
		List<Object> userUploadVOs = uploadObjects.get(CommonConstants.USERS_MAP_KEY);
		if (userUploadVOs != null && !userUploadVOs.isEmpty()) {
			LOG.debug("Creating all the users");
			UserUploadVO user = null;
			for (Object userUploadVO : userUploadVOs) {
				try {
					user = (UserUploadVO) userUploadVO;
					LOG.debug("Creating user : " + user.getEmailId());
					createUser(adminUser, user);
					user = null;
				}
				catch (UserAdditionException e) {
					LOG.error("ERROR : " + " while adding user : " + user.getEmailId() + " message : " + e.getMessage());
					errorList.add("ERROR : " + " while adding user : " + user.getEmailId() + " message : " + e.getMessage());
				}
				catch (InvalidInputException e) {
					if (e.getMessage() != null && e.getMessage().equals(DisplayMessageConstants.USER_ASSIGNMENT_ALREADY_EXISTS)) {
						LOG.error("ERROR : " + " while adding user : " + user.getEmailId() + " message : " + e.getMessage());
						errorList.add("ERROR : " + " while adding user : " + user.getEmailId() + " message : User aleardy exists and assigned!");
					}
					else {
						LOG.info(e.getErrorCode());
						throw e;
					}
				}
			}
			LOG.debug("Creation of all users complete!");
		}

		LOG.info("Objects created. Returning the list of errors");

		return errorList;
	}

	private Map<String, List<Object>> parseTestFile(String txtFileName) {
		BufferedReader bfrReader = null;
		FileReader reader = null;
		boolean isRegionLine = false;
		boolean isBranchLine = false;
		boolean isUserLine = false;
		List<RegionUploadVO> regionUploads = new ArrayList<RegionUploadVO>();
		List<BranchUploadVO> branchUploads = new ArrayList<BranchUploadVO>();
		List<UserUploadVO> userUploads = new ArrayList<UserUploadVO>();
		Map<String, List<Object>> uploadObjects = new HashMap<>();
		try {
			reader = new FileReader(txtFileName);
			bfrReader = new BufferedReader(reader);
			String line = null;
			RegionUploadVO regionVO = null;
			BranchUploadVO branchVO = null;
			UserUploadVO userVO = null;
			while ((line = bfrReader.readLine()) != null) {
				if (line.equals("##Regions")) {
					isRegionLine = true;
					isBranchLine = false;
					isUserLine = false;
					continue;
				}
				if (line.equals("##Branches")) {
					isRegionLine = false;
					isBranchLine = true;
					isUserLine = false;
					continue;
				}
				if (line.equals("##Users")) {
					isRegionLine = false;
					isBranchLine = false;
					isUserLine = true;
					continue;
				}
				if (isRegionLine) {
					regionVO = createRegionUploadVO(line);
					if (regionVO != null) {
						regionUploads.add(regionVO);
					}
				}
				if (isBranchLine) {
					branchVO = createBranchUploadVO(line);
					if (branchVO != null) {
						branchUploads.add(branchVO);
					}
				}
				if (isUserLine) {
					userVO = createUserUploadVO(line);
					if (userVO != null) {
						userUploads.add(userVO);
					}
				}
			}
			uploadObjects.put(CommonConstants.REGIONS_MAP_KEY, new ArrayList<Object>(regionUploads));
			uploadObjects.put(CommonConstants.BRANCHES_MAP_KEY, new ArrayList<Object>(branchUploads));
			uploadObjects.put(CommonConstants.USERS_MAP_KEY, new ArrayList<Object>(userUploads));

		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (bfrReader != null) {
				try {
					bfrReader.close();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return uploadObjects;
	}

	private RegionUploadVO createRegionUploadVO(String line) {
		LOG.debug("Create region upload vo from: " + line);
		RegionUploadVO upload = null;
		String[] tokens = line.split("\t");
		if (tokens != null && tokens.length == 2) {
			upload = new RegionUploadVO();
			upload.setRegionName(tokens[0]);
			upload.setRegionAddress1(tokens[1]);
		}
		return upload;
	}

	private BranchUploadVO createBranchUploadVO(String line) {
		LOG.debug("Creating branch upload vo from: " + line);
		BranchUploadVO upload = null;
		String[] tokens = line.split("\t");
		if (tokens != null && tokens.length == 3) {
			upload = new BranchUploadVO();
			upload.setBranchName(tokens[0]);
			upload.setBranchAddress1(tokens[1]);
			if (tokens[2].equals("#Company")) {
				upload.setAssignToCompany(true);
			}
			else {
				upload.setAssignToCompany(false);
				upload.setAssignedRegionName(tokens[2].substring(1));
			}
		}
		return upload;
	}

	private UserUploadVO createUserUploadVO(String line) {
		LOG.debug("Creating user vo from: " + line);
		UserUploadVO upload = null;
		String[] tokens = line.split("\t");
		if (tokens != null) {
			upload = new UserUploadVO();
			upload.setEmailId(tokens[1]);
			if (tokens.length == 3) {
				// user under company
				upload.setBelongsToCompany(true);
			}
			else {
				// admin of either branch or region
				if (tokens[2].equals("#region")) {
					// upload.setRegionAdmin(true);
					upload.setAssignedRegionName(tokens[3].substring(1));
				}
				else if (tokens[2].equals("#branch")) {
					upload.setAssignedBranchName(tokens[3].substring(1));
				}
				if (tokens.length == 5) {
					// admin
					if (upload.getAssignedRegionName() != null && !upload.getAssignedRegionName().isEmpty()) {
						upload.setRegionAdmin(true);
					}
					else {
						upload.setBranchAdmin(true);
					}
				}
			}
		}
		return upload;
	}

	@Transactional
	@Override
	public void postProcess(User adminUser) {
		LOG.info("Post processing..");
		// TODO: to be taken out for live example
		// get list of all users and activate them
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put(CommonConstants.COMPANY_COLUMN, adminUser.getCompany());
		queryMap.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED);
		List<User> userList = userDao.findByKeyValue(User.class, queryMap);
		if (userList != null) {
			for (User user : userList) {
				try {
					if(user.getLoginPassword() ==  null){
						user.setIsAtleastOneUserprofileComplete(CommonConstants.STATUS_ACTIVE);
						user.setStatus(CommonConstants.STATUS_ACTIVE);
						user.setModifiedBy(String.valueOf(user.getUserId()));
						user.setModifiedOn(new Timestamp(System.currentTimeMillis()));
	
						/**
						 * Set the new password
						 */
						String encryptedPassword = encryptionHelper.encryptSHA512("d#demo");
						user.setLoginPassword(encryptedPassword);
	
						userDao.saveOrUpdate(user);
	
						// update the solr status too
						solrSearchService.editUserInSolr(user.getUserId(), CommonConstants.STATUS_SOLR, String.valueOf(user.getStatus()));
					}
				}
				catch (InvalidInputException ie) {
					LOG.error("Error while post processing user " + user.toString(), ie);
				}
				catch (SolrException e) {
					LOG.error("SOLR issue while post processing user " + user.toString(), e);
				}
			}
		}

	}

}
