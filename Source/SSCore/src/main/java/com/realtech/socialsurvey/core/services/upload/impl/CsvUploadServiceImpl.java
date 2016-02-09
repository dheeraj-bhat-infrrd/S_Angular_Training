package com.realtech.socialsurvey.core.services.upload.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchUploadVO;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ContactNumberSettings;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.Licenses;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RegionUploadVO;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserEmailMapping;
import com.realtech.socialsurvey.core.entities.UserUploadVO;
import com.realtech.socialsurvey.core.entities.WebAddressSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.BranchAdditionException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.RegionAdditionException;
import com.realtech.socialsurvey.core.exception.UserAdditionException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserAssignmentException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.upload.CsvUploadService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;

@Component
public class CsvUploadServiceImpl implements CsvUploadService {

	private static final String REGION_SHEET = "Regions";
	private static final String BRANCH_SHEET = "Offices";
	private static final String USERS_SHEET = "Users";

	private static final int REGION_ID_INDEX = 0;
	private static final int REGION_NAME_INDEX = 1;
	private static final int REGION_ADDRESS1_INDEX = 2;
	private static final int REGION_ADDRESS2_INDEX = 3;
	private static final int REGION_CITY_INDEX = 4;
	private static final int REGION_STATE_INDEX = 5;
	private static final int REGION_ZIP_INDEX = 6;

	private static final int BRANCH_ID_INDEX = 0;
	private static final int BRANCH_NAME_INDEX = 1;
	private static final int BRANCH_REGION_ID_INDEX = 2;
	private static final int BRANCH_ADDRESS1_INDEX = 3;
	private static final int BRANCH_ADDRESS2_INDEX = 4;
	private static final int BRANCH_CITY_INDEX = 5;
	private static final int BRANCH_STATE_INDEX = 6;
	private static final int BRANCH_ZIP_INDEX = 7;

	private static final int USER_FIRST_NAME_INDEX = 1;
	private static final int USER_LAST_NAME_INDEX = 2;
	private static final int USER_TITLE_INDEX = 3;
	private static final int USER_BRANCH_ID_INDEX = 4;
	private static final int USER_REGION_ID_INDEX = 5;
	private static final int USER_HAS_PUBLIC_PAGE_INDEX = 6;
	private static final int USER_BRANCH_ID_ADMIN_INDEX = 7;
	private static final int USER_REGION_ID_ADMIN_INDEX = 8;
	private static final int USER_EMAIL_INDEX = 9;
	private static final int USER_PHONE_NUMBER = 10;
	private static final int USER_WEBSITE = 11;
	private static final int USER_LICENSES = 12;
	private static final int USER_LEGAL_DISCLAIMER = 13;
	private static final int USER_PHOTO_PROFILE_URL = 14;
	private static final int USER_ABOUT_ME_DESCRIPTION = 15;

	private static final String COUNTRY = "United States";
	private static final String COUNTRY_CODE = "US";

	@Autowired
	private OrganizationManagementService organizationManagementService;

	@Autowired
	private UserDao userDao;

	@Resource
	@Qualifier("branch")
	private BranchDao branchDao;

	@Autowired
	private GenericDao<FileUpload, Long> fileUploadDao;

	@Autowired
	private GenericDao<Region, Long> regionDao;

	@Autowired
	private EncryptionHelper encryptionHelper;

	@Autowired
	private SolrSearchService solrSearchService;

	@Autowired
	private ProfileManagementService profileManagementService;

	@Autowired
	private OrganizationUnitSettingsDao organizationUnitSettingsDao;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private EmailServices emailServices;

	@Autowired
	private FileUploadService fileUploadService;
	
	@Autowired
	private Utils utils;
	
    @Autowired
    private GenericDao<UserEmailMapping, Long> userEmailMappingDao;


	@Value("${FILEUPLOAD_DIRECTORY_LOCATION}")
	private String fileDirectory;

	@Value("${MASK_EMAIL_ADDRESS}")
	private String maskEmail;

	@Value("${FILE_DIRECTORY_LOCATION}")
	private String fileDirectoryLocation;

	@Value("${APPLICATION_ADMIN_EMAIL}")
	private String adminEmailId;

	@Value("${APPLICATION_ADMIN_NAME}")
	private String adminName;

	@Value("${CDN_PATH}")
	private String amazonEndpoint;

	@Value("${AMAZON_IMAGE_BUCKET}")
	private String amazonImageBucket;
	
	private static Logger LOG = LoggerFactory.getLogger(CsvUploadServiceImpl.class);

	@Transactional
	@Override
	public List<String> parseAndUploadTempCsv(FileUpload fileUpload) throws InvalidInputException {
		if (fileUpload == null || fileUpload.getFileName() == null || fileUpload.getFileName().isEmpty() || fileUpload.getCompany() == null
				|| fileUpload.getAdminUserId() <= 0l) {
			LOG.info("Invalid upload details");
			throw new InvalidInputException("File name is not provided");
		}
		InputStream fileStream = null;
		List<String> regionErrors = null;
		List<String> branchErrors = null;
		List<String> userErrors = new ArrayList<String>();
		User adminUser = getUser(fileUpload.getAdminUserId());
		adminUser.setCompanyAdmin(true);
		try {
			fileStream = new FileInputStream(fileDirectory + fileUpload.getFileName());
			XSSFWorkbook workBook = new XSSFWorkbook(fileStream);
			List<RegionUploadVO> uploadedRegions = parseAndUploadRegions(fileUpload, workBook, regionErrors, adminUser);
			List<BranchUploadVO> uploadedBranches = parseAndUploadBranches(fileUpload, workBook, branchErrors, uploadedRegions, adminUser);
			userErrors = parseAndUploadUsers(fileUpload, workBook, userErrors, uploadedRegions, uploadedBranches, adminUser);

			if (userErrors != null && !userErrors.isEmpty()) {
				LOG.debug("Sending mail to realtech admin for users who were not uploaded due to some exception ");
				generateExcelForFailedRecordsAndSendMail(userErrors);

			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (fileStream != null) {
				try {
					fileStream.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		LOG.info("Parsing and uploading hierarchy " + fileUpload.getFileName() + " for company " + fileUpload.getCompany().getCompany());

		return null;
	}

	private void generateExcelForFailedRecordsAndSendMail(List<String> userErrors) {
		int rownum = 1;
		int count = 1;
		boolean excelCreated = false;
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Records not uploaded");
		sheet = fillHeaders(sheet);
		for (String error : userErrors) {
			Row row = sheet.createRow(rownum++);
			row = fillCellsInRow(row, error, count++);
		}

		String fileName = "Record_Upload_Failure" + "_" + System.currentTimeMillis();
		FileOutputStream fileOutput = null;
		InputStream inputStream = null;
		File file = null;
		String filePath = null;
		try {
			file = new File(fileDirectoryLocation + File.separator + fileName + ".xls");
			fileOutput = new FileOutputStream(file);
			file.createNewFile();
			workbook.write(fileOutput);
			filePath = file.getPath();
			excelCreated = true;
		}
		catch (FileNotFoundException fe) {
			LOG.error("Exception caught " + fe.getMessage());
			excelCreated = false;
		}
		catch (IOException e) {
			LOG.error("Exception caught " + e.getMessage());
			excelCreated = false;
		}
		finally {
			try {
				fileOutput.close();
				if (inputStream != null) {
					inputStream.close();
				}
			}
			catch (IOException e) {
				LOG.error("Exception caught " + e.getMessage());
				excelCreated = false;
			}
		}
		if (excelCreated) {
			try {
				Map<String , String > attachmentsDetails = new HashMap<String, String>();
				attachmentsDetails.put("CorruptRecords.xls", filePath);
				emailServices.sendRecordsNotUploadedCrmNotificationMail(adminName, "", adminEmailId, attachmentsDetails );
			}
			catch (InvalidInputException e) {
				LOG.error("Exception caught " + e.getMessage());
			}
			catch (UndeliveredEmailException e) {
				LOG.error("Exception caught " + e.getMessage());
			}
		}
	}

	private Row fillCellsInRow(Row row, String userErrorString, int counter) {
		int cellnum = 0;
		Cell cell1 = row.createCell(cellnum++);
		cell1.setCellValue(counter);
		Cell cell2 = row.createCell(cellnum++);
		cell2.setCellValue(userErrorString);
		return row;

	}

	public HSSFSheet fillHeaders(HSSFSheet sheet) {
		int cellnum = 0;
		Row row = sheet.createRow(0);
		Cell cell1 = row.createCell(cellnum++);
		cell1.setCellValue("S.No");
		Cell cell2 = row.createCell(cellnum++);
		cell2.setCellValue("Error");

		return sheet;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<String> parseAndUploadUsers(FileUpload fileUpload, XSSFWorkbook workBook, List<String> userErrors,
			List<RegionUploadVO> uploadedRegions, List<BranchUploadVO> uploadedBranches, User adminUser) {
		LOG.debug("Parsing and uploading users: BEGIN");
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		Map<UserUploadVO, User> map = new HashMap<UserUploadVO, User>();
		List<UserUploadVO> userUploads = parseUsers(fileUpload, workBook, userErrors, uploadedRegions, uploadedBranches, adminUser);
		if (userUploads != null && !userUploads.isEmpty()) {
			LOG.info("Uploading users to database.");
			userMap = uploadUsers(userUploads, adminUser, userErrors);
			map = (HashMap) userMap.get("ValidUser");
			userErrors = (List) userMap.get("InvalidUser");
			if (map != null && !map.isEmpty()) {
				LOG.debug("Adding extra user details ");
				for (Map.Entry<UserUploadVO, User> entry : map.entrySet()) {
					UserUploadVO userUploadVO = entry.getKey();
					User uploadedUser = entry.getValue();
					try {
						updateUserSettingsInMongo(uploadedUser, userUploadVO, userErrors);
					}
					catch (Exception e) {
						userErrors.add("Exception caught for user " + uploadedUser.getUsername() + " " + uploadedUser.getUserId());
					}
				}
			}
		}
		else {
			LOG.info("No users to upload into the database.");
		}
		return userErrors;
	}

	private void updateUserSettingsInMongo(User user, UserUploadVO userUploadVO, List<String> userErrors) throws InvalidInputException {
		LOG.debug("Inside method updateUserSettingsInMongo ");
		AgentSettings agentSettings = userManagementService.getAgentSettingsForUserProfiles(user.getUserId());
		if (agentSettings == null) {
			userErrors.add("No company settings found for user " + user.getUsername() + " " + user.getUserId());

		}
		else {
			ContactDetailsSettings contactDetailsSettings = agentSettings.getContact_details();
			if (contactDetailsSettings == null) {
				contactDetailsSettings = new ContactDetailsSettings();
			}
			ContactNumberSettings contactNumberSettings = contactDetailsSettings.getContact_numbers();
			if (contactNumberSettings == null) {
				contactNumberSettings = new ContactNumberSettings();
			}
			contactNumberSettings.setWork(userUploadVO.getPhoneNumber());
			contactDetailsSettings.setContact_numbers(contactNumberSettings);
			contactDetailsSettings.setAbout_me(userUploadVO.getAboutMeDescription());
			contactDetailsSettings.setTitle(userUploadVO.getTitle());
			WebAddressSettings webAddressSettings = contactDetailsSettings.getWeb_addresses();
			if (webAddressSettings == null) {
				webAddressSettings = new WebAddressSettings();
			}
			webAddressSettings.setWork(userUploadVO.getWebsiteUrl());
			contactDetailsSettings.setWeb_addresses(webAddressSettings);
			agentSettings.setContact_details(contactDetailsSettings);

			if (userUploadVO.getLicense() != null && !userUploadVO.getLicense().isEmpty()) {
				Licenses licenses = agentSettings.getLicenses();
				if (licenses == null) {
					licenses = new Licenses();
				}
				List<String> authorizedIn = licenses.getAuthorized_in();
				if (authorizedIn == null) {
					authorizedIn = new ArrayList<String>();
				}
				licenses.setAuthorized_in(getAllStateLicenses(userUploadVO.getLicense(), authorizedIn));
				agentSettings.setLicenses(licenses);
				if (licenses != null && licenses.getAuthorized_in() != null && !licenses.getAuthorized_in().isEmpty()) {
					organizationUnitSettingsDao.updateParticularKeyAgentSettings(MongoOrganizationUnitSettingDaoImpl.KEY_LICENCES, licenses,
							agentSettings);
				}
			}
			agentSettings.setDisclaimer(userUploadVO.getLegalDisclaimer());

			profileManagementService.updateAgentContactDetails(MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings,
					contactDetailsSettings);

			if (userUploadVO.getLegalDisclaimer() != null) {
				organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_DISCLAIMER,
						userUploadVO.getLegalDisclaimer(), agentSettings, MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION);
			}

			if (userUploadVO.getUserPhotoUrl() != null) {

				updateProfileImageForAgent(userUploadVO.getUserPhotoUrl(), agentSettings);
				/*
				 * profileManagementService.updateProfileImage(
				 * MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings,
				 * userUploadVO.getUserPhotoUrl() );
				 */
			}
		}
	}

	private void updateProfileImageForAgent(String userPhotoUrl, AgentSettings agentSettings) throws InvalidInputException {
		LOG.debug("Uploading for agent " + agentSettings.getIden() + " with photo: " + userPhotoUrl);
		// TODO: Check if the image is local or online. In case it is local, then we should
		// upload that to S3 and then link the same
		/*
		 * String profileImageUrl = null; if
		 * (userPhotoUrl.trim().matches(CommonConstants.URL_REGEX)) {
		 * LOG.debug("Profile photo is publicaly available"); profileImageUrl = userPhotoUrl; } else
		 * { LOG.debug("User photo is locally available. Uploading the image to cloud"); File
		 * imageFile = new File(userPhotoUrl); String imageName =
		 * userPhotoUrl.substring(userPhotoUrl.lastIndexOf(CommonConstants.FILE_SEPARATOR)); String
		 * profileImageName = fileUploadService.fileUploadHandler(imageFile, imageName);
		 * profileImageUrl = amazonEndpoint + CommonConstants.FILE_SEPARATOR + amazonImageBucket +
		 * CommonConstants.FILE_SEPARATOR + profileImageName; }
		 */
		profileManagementService.updateProfileImage(MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, userPhotoUrl);
	}

	private List<String> getAllStateLicenses(String licenses, List<String> authorizedIn) {
		String toRemove = "Licensed State(s):";
		if (licenses.indexOf(toRemove) != -1) {
			licenses = licenses.substring(licenses.indexOf("Licensed State(s):") + toRemove.length(), licenses.length());
		}
		licenses = licenses.trim();
		authorizedIn.add(licenses);
		return authorizedIn;
	}

	private List<UserUploadVO> parseUsers(FileUpload fileUpload, XSSFWorkbook workBook, List<String> userErrors,
			List<RegionUploadVO> uploadedRegions, List<BranchUploadVO> uploadedBranches, User adminUser) {
		LOG.debug("Parsing users sheet");
		List<UserUploadVO> usersToBeUploaded = new ArrayList<>();
		XSSFSheet userSheet = workBook.getSheet(USERS_SHEET);
		Iterator<Row> rows = userSheet.rowIterator();
		Iterator<Cell> cells = null;
		XSSFRow row = null;
		XSSFCell cell = null;
		UserUploadVO uploadedUser = null;
		boolean rowContainsError = false;
		while (rows.hasNext()) {
			row = (XSSFRow) rows.next();
			// skip the first 2 rows. first row is the schema and second is the header
			if (row.getRowNum() < 2) {
				continue;
			}
			cells = row.cellIterator();
			uploadedUser = new UserUploadVO();
			int cellIndex = 0;
			while (cells.hasNext()) {
				cell = (XSSFCell) cells.next();
				cellIndex = cell.getColumnIndex();
				LOG.debug("Column " + cell.getColumnIndex());
				if (cellIndex == USER_FIRST_NAME_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						uploadedUser.setFirstName(cell.getStringCellValue().trim());
					}
					else {
						LOG.error("First name is not present");
						rowContainsError = true;
						break;
					}
				}
				else if (cellIndex == USER_LAST_NAME_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						uploadedUser.setLastName(cell.getStringCellValue().trim());
					}
				}
				else if (cellIndex == USER_TITLE_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						uploadedUser.setTitle(cell.getStringCellValue().trim());
					}
				}
				else if (cellIndex == USER_BRANCH_ID_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						// map it with the region
						String sourceBranchId = null;
						if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC){
							sourceBranchId = String.valueOf(cell.getNumericCellValue());
						}else  if(cell.getCellType() == XSSFCell.CELL_TYPE_STRING){
							sourceBranchId = cell.getStringCellValue();
						}
						try {
							long branchId = getBranchIdFromSourceId(uploadedBranches, sourceBranchId);
							uploadedUser.setBranchId(branchId);
							uploadedUser.setSourceBranchId(sourceBranchId);
						}
						catch (UserAdditionException bae) {
							LOG.error("Could not find branch");
							rowContainsError = true;
							break;
						}
					}
				}
				else if (cellIndex == USER_REGION_ID_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						// map it with the region
						String sourceRegionId = null;
						if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC){
							sourceRegionId = String.valueOf(cell.getNumericCellValue());
						}else  if(cell.getCellType() == XSSFCell.CELL_TYPE_STRING){
							sourceRegionId = cell.getStringCellValue();
						}
						try {
							long regionId = getRegionIdFromSourceId(uploadedRegions, sourceRegionId);
							uploadedUser.setRegionId(regionId);
							uploadedUser.setSourceRegionId(sourceRegionId);
						}
						catch (BranchAdditionException bae) {
							LOG.error("Could not find region");
							rowContainsError = true;
							break;
						}
					}
				}
				else if (cellIndex == USER_HAS_PUBLIC_PAGE_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						String hasProfilePage = cell.getStringCellValue();
						if (hasProfilePage.equalsIgnoreCase("YES")) {
							uploadedUser.setAgent(true);
						}
					}
				}
				else if (cellIndex == USER_BRANCH_ID_ADMIN_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						String cellValue = null;
						if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
							cellValue = cell.getStringCellValue();
						}
						else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
							long lCellValue = (long) cell.getNumericCellValue();
							cellValue = String.valueOf(lCellValue);
						}
						if (cellValue != null && !cellValue.isEmpty() && !cellValue.equalsIgnoreCase("No")) {
							uploadedUser.setBranchAdmin(true);
						}
					}
				}
				else if (cellIndex == USER_REGION_ID_ADMIN_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						String cellValue = null;
						if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
							cellValue = cell.getStringCellValue();
						}
						else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
							long lCellValue = (long) cell.getNumericCellValue();
							cellValue = String.valueOf(lCellValue);
						}
						if (cellValue != null && !cellValue.isEmpty() && !cellValue.equalsIgnoreCase("No")) {
							uploadedUser.setRegionAdmin(true);
						}
					}
				}
				else if (cellIndex == USER_EMAIL_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						String emailId = cell.getStringCellValue().trim();
						if (maskEmail.equals(CommonConstants.YES_STRING)) {
							emailId = utils.maskEmailAddress(emailId);
							if (emailId != null) {
								uploadedUser.setEmailId(uploadedUser.getFirstName()
										+ (uploadedUser.getLastName() != null ? " " + uploadedUser.getLastName() : "") + " <" + emailId + ">");
							}
							else {
								LOG.error("Masking email address is not present");
								rowContainsError = true;
								break;
							}
						}
						else {
							uploadedUser.setEmailId(uploadedUser.getFirstName()
									+ (uploadedUser.getLastName() != null ? " " + uploadedUser.getLastName() : "") + " <" + emailId + ">");
						}
					}
					else {
						LOG.error("Email address is not present");
						rowContainsError = true;
						break;
					}
				}
				else if (cellIndex == USER_PHOTO_PROFILE_URL) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						String userPhotoUrl = cell.getStringCellValue();
						uploadedUser.setUserPhotoUrl(userPhotoUrl);
					}
				}
				else if (cellIndex == USER_PHONE_NUMBER) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC){
							uploadedUser.setPhoneNumber(String.valueOf((long)cell.getNumericCellValue()));
						}else{
							uploadedUser.setPhoneNumber(cell.getStringCellValue());
						}
					}
				}
				else if (cellIndex == USER_WEBSITE) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						String websiteUrl = cell.getStringCellValue();
						uploadedUser.setWebsiteUrl(websiteUrl);
					}
				}
				else if (cellIndex == USER_LICENSES) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						String license = cell.getStringCellValue();
						uploadedUser.setLicense(license);
					}
				}
				else if (cellIndex == USER_LEGAL_DISCLAIMER) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						String legalDisclaimer = cell.getStringCellValue();
						uploadedUser.setLegalDisclaimer(legalDisclaimer);
					}
				}
				else if (cellIndex == USER_ABOUT_ME_DESCRIPTION) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						String aboutMeDescription = cell.getStringCellValue();
						uploadedUser.setAboutMeDescription(aboutMeDescription);
					}
				}
			}
			if (rowContainsError) {
				LOG.error("Could not process row");
				if (userErrors == null) {
					userErrors = new ArrayList<>();
				}
				userErrors.add("Error in user row " + row.getRowNum());
				rowContainsError = false;
				continue;
			}
			if (uploadedUser.getBranchId() == 0l && uploadedUser.getRegionId() == 0l) {
				uploadedUser.setBelongsToCompany(true);
			}
			usersToBeUploaded.add(uploadedUser);
		}
		return usersToBeUploaded;
	}

	// modifies the list of branchesToUpload with the actual branch id
	private Map<Object, Object> uploadUsers(List<UserUploadVO> usersToUpload, User adminUser, List<String> userErrors) {
		LOG.debug("Uploading users to database");
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		Map<UserUploadVO, User> map = new HashMap<UserUploadVO, User>();
		for (UserUploadVO userToBeUploaded : usersToUpload) {
			try {
                if (checkIfEmailIdExists(userToBeUploaded.getEmailId(), adminUser.getCompany())) {
                	try {
                		User user = assignUser(userToBeUploaded, adminUser);
                		if (user != null) {
                			map.put(userToBeUploaded, user);
                		}
                	}
                	catch (UserAdditionException e) {
                		LOG.error("UserAdditionException while adding user: " + userToBeUploaded.getEmailId());
                		userErrors.add("UserAdditionException while adding user: " + userToBeUploaded.getEmailId() + " Exception is : " + e.getMessage());
                	}
                	catch (InvalidInputException e) {
                		LOG.error("InvalidInputException while adding user: " + userToBeUploaded.getEmailId());
                		userErrors.add("InvalidInputException while adding user: " + userToBeUploaded.getEmailId() + " Exception is : " + e.getMessage());
                	}
                	catch (SolrException e) {
                		LOG.error("SolrException while adding user: " + userToBeUploaded.getEmailId());
                		userErrors.add("SolrException while adding user: " + userToBeUploaded.getEmailId() + " Exception is : " + e.getMessage());
                	}
                	catch (NoRecordsFetchedException e) {
                		LOG.error("NoRecordsFetchedException while adding user: " + userToBeUploaded.getEmailId());
                		userErrors.add("NoRecordsFetchedException while adding user: " + userToBeUploaded.getEmailId() + " Exception is : "
                				+ e.getMessage());
                	}
                	catch (UserAssignmentException e) {
                		LOG.error("UserAssignmentException while adding user: " + userToBeUploaded.getEmailId());
                		userErrors.add("UserAssignmentException while adding user: " + userToBeUploaded.getEmailId() + " Exception is : "
                				+ e.getMessage());
                	}
                }
                else {
                	// add user
                	try {
                		User user = addUser(userToBeUploaded, adminUser);
                		if (user != null) {
                			map.put(userToBeUploaded, user);
                		}
                	}
                	catch (InvalidInputException e) {
                		LOG.error("InvalidInputException while adding user: " + userToBeUploaded.getEmailId());
                		userErrors.add("InvalidInputException while adding user: " + userToBeUploaded.getEmailId() + " Exception is : " + e.getMessage());
                	}
                	catch (NoRecordsFetchedException e) {
                		LOG.error("NoRecordsFetchedException while adding user: " + userToBeUploaded.getEmailId());
                		userErrors.add("NoRecordsFetchedException while adding user: " + userToBeUploaded.getEmailId() + " Exception is : "
                				+ e.getMessage());
                	}
                	catch (SolrException e) {
                		LOG.error("SolrException while adding user: " + userToBeUploaded.getEmailId());
                		userErrors.add("SolrException while adding user: " + userToBeUploaded.getEmailId() + " Exception is : " + e.getMessage());
                	}
                	catch (UserAssignmentException e) {
                		LOG.error("UserAssignmentException while adding user: " + userToBeUploaded.getEmailId());
                		userErrors.add("UserAssignmentException while adding user: " + userToBeUploaded.getEmailId() + " Exception is : "
                				+ e.getMessage());
                	}
                	catch (UserAdditionException e) {
                		LOG.error("UserAdditionException while adding user: " + userToBeUploaded.getEmailId());
                		userErrors.add("UserAdditionException while adding user: " + userToBeUploaded.getEmailId() + " Exception is : " + e.getMessage());
                	}
                }
            } catch ( InvalidInputException e ) {
                LOG.error("InvalidInputException while adding user: " + userToBeUploaded.getEmailId());
                userErrors.add("InvalidInputException while adding user: " + userToBeUploaded.getEmailId() + " Exception is : " + e.getMessage());
            }
		}
		userMap.put("ValidUser", map);
		userMap.put("InvalidUser", userErrors);
		return userMap;

	}

	private List<BranchUploadVO> parseAndUploadBranches(FileUpload fileUpload, XSSFWorkbook workBook, List<String> branchErrors,
			List<RegionUploadVO> uploadedRegions, User adminUser) {
		LOG.debug("Parsing and uploading branches: BEGIN");
		List<BranchUploadVO> branchUploads = parseBranches(fileUpload, workBook, branchErrors, uploadedRegions, adminUser);
		if (branchUploads != null && !branchUploads.isEmpty()) {
			LOG.info("Uploading branches to database.");
			uploadBranches(branchUploads, adminUser, branchErrors);
		}
		else {
			LOG.info("No branches to upload into the database.");
		}
		return branchUploads;
	}

	private List<BranchUploadVO> parseBranches(FileUpload fileUpload, XSSFWorkbook workBook, List<String> branchErrors,
			List<RegionUploadVO> uploadedRegions, User adminUser) {
		LOG.debug("Parsing branches sheet");
		List<BranchUploadVO> branchesToBeUploaded = new ArrayList<>();
		XSSFSheet branchSheet = workBook.getSheet(BRANCH_SHEET);
		Iterator<Row> rows = branchSheet.rowIterator();
		Iterator<Cell> cells = null;
		XSSFRow row = null;
		XSSFCell cell = null;
		BranchUploadVO uploadedBranch = null;
		boolean rowContainsError = false;
		while (rows.hasNext()) {
			row = (XSSFRow) rows.next();
			// skip the first 2 row. first row is the schema and second is the header
			if (row.getRowNum() < 2) {
				continue;
			}
			cells = row.cellIterator();
			uploadedBranch = new BranchUploadVO();
			int cellIndex = 0;
			while (cells.hasNext()) {
				cell = (XSSFCell) cells.next();
				cellIndex = cell.getColumnIndex();
				if (cellIndex == BRANCH_ID_INDEX) {
					if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
						try {
							uploadedBranch.setSourceBranchId(String.valueOf(cell.getNumericCellValue()));
						}
						catch (NumberFormatException nfe) {
							// TODO: mark this record as error
							LOG.error("Source branch id is not present");
							rowContainsError = true;
							break;
						}
					}else{
						uploadedBranch.setSourceBranchId(cell.getStringCellValue());
					}
				}
				else if (cellIndex == BRANCH_NAME_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						uploadedBranch.setBranchName(cell.getStringCellValue().trim());
					}
					else {
						LOG.error("branch name not present");
						rowContainsError = true;
						break;
					}
				}
				else if (cellIndex == BRANCH_REGION_ID_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						// map it with the region
						String sourceRegionId = null;
						if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC){
							sourceRegionId = String.valueOf(cell.getNumericCellValue());
						}else if(cell.getCellType() == XSSFCell.CELL_TYPE_STRING){
							sourceRegionId = cell.getStringCellValue();
						}
						try {
							long regionId = getRegionIdFromSourceId(uploadedRegions, sourceRegionId);
							uploadedBranch.setRegionId(regionId);
							uploadedBranch.setSourceRegionId(sourceRegionId);
						}
						catch (BranchAdditionException bae) {
							LOG.error("Could not find region");
							rowContainsError = true;
							break;
						}
					}
				}
				else if (cellIndex == BRANCH_ADDRESS1_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						uploadedBranch.setBranchAddress1(cell.getStringCellValue());
						uploadedBranch.setAddressSet(true);
					}
				}
				else if (cellIndex == BRANCH_ADDRESS2_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						uploadedBranch.setBranchAddress2(cell.getStringCellValue());
						uploadedBranch.setAddressSet(true);
					}
				}
				else if (cellIndex == BRANCH_CITY_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						uploadedBranch.setBranchCity(cell.getStringCellValue());
					}
					else {
						LOG.error("branch city not present");
						rowContainsError = true;
						break;
					}
				}
				else if (cellIndex == BRANCH_STATE_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						uploadedBranch.setBranchState(cell.getStringCellValue());
					}
				}
				else if (cellIndex == BRANCH_ZIP_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
							uploadedBranch.setBranchZipcode(cell.getStringCellValue());
						}
						else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
							uploadedBranch.setBranchZipcode(String.valueOf((int) cell.getNumericCellValue()));
						}
					}
				}
			}
			if (rowContainsError) {
				LOG.error("Could not process row");
				if (branchErrors == null) {
					branchErrors = new ArrayList<>();
				}
				branchErrors.add("Error in Branch row " + row.getRowNum());
				rowContainsError = false;
				continue;
			}
			branchesToBeUploaded.add(uploadedBranch);
		}
		return branchesToBeUploaded;
	}

	// modifies the list of branchesToUpload with the actual branch id
	private void uploadBranches(List<BranchUploadVO> branchesToUpload, User adminUser, List<String> branchErrors) {
		LOG.debug("Uploading branches");
		Branch branch = null;
		for (BranchUploadVO branchToUpload : branchesToUpload) {
			try {
				branch = createBranch(adminUser, branchToUpload);
				branchToUpload.setBranchId(branch.getBranchId());
			}
			catch (InvalidInputException e) {
				LOG.error("InvalidInputException while uploading branch to database. " + branchToUpload.getSourceRegionId(), e);
				branchErrors.add("Error while uploading branch to database. " + branchToUpload.getSourceRegionId());
			}
			catch (BranchAdditionException e) {
				LOG.error("RegionAdditionException while uploading branch to database. " + branchToUpload.getSourceRegionId(), e);
				branchErrors.add("Error while uploading branch to database. " + branchToUpload.getSourceRegionId());
			}
			catch (SolrException e) {
				LOG.error("SolrException while uploading branch to database. " + branchToUpload.getSourceRegionId(), e);
				branchErrors.add("Error while uploading branch to database. " + branchToUpload.getSourceRegionId());
			}
		}
	}

	long getRegionIdFromSourceId(List<RegionUploadVO> uploadedRegions, String regionSourceId) throws BranchAdditionException {
		LOG.debug("Getting region id from source id");
		long regionId = 0;
		for (RegionUploadVO uploadedRegion : uploadedRegions) {
			if (uploadedRegion.getSourceRegionId().equals(regionSourceId)) {
				regionId = uploadedRegion.getRegionId();
				break;
			}
		}
		if (regionId == 0l) {
			throw new BranchAdditionException("Could not find region id for the region");
		}
		return regionId;
	}

	long getBranchIdFromSourceId(List<BranchUploadVO> uploadedBranches, String regionBranchId) throws UserAdditionException {
		LOG.debug("Getting branch id from source id");
		long branchId = 0;
		for (BranchUploadVO uploadedBranch : uploadedBranches) {
			if (uploadedBranch.getSourceBranchId().equals(regionBranchId)) {
				branchId = uploadedBranch.getBranchId();
				break;
			}
		}
		if (branchId == 0l) {
			throw new UserAdditionException("Could not find brach id for the user");
		}
		return branchId;
	}

	private List<RegionUploadVO> parseAndUploadRegions(FileUpload fileUpload, XSSFWorkbook workBook, List<String> regionErrors, User adminUser) {
		LOG.debug("Parsing and uploading regions: BEGIN");
		List<RegionUploadVO> regionUploads = parseRegions(fileUpload, workBook, regionErrors, adminUser);
		// uploading regions
		if (regionUploads != null && !regionUploads.isEmpty()) {
			LOG.info("Uploading regions to database.");
			uploadRegions(regionUploads, adminUser, regionErrors);
		}
		else {
			LOG.info("No regions to upload into the database.");
		}
		return regionUploads;
	}

	private List<RegionUploadVO> parseRegions(FileUpload fileUpload, XSSFWorkbook workBook, List<String> regionErrors, User adminUser) {
		LOG.debug("Parsing regions from CSV");
		List<RegionUploadVO> uploadedRegions = new ArrayList<RegionUploadVO>();
		XSSFSheet regionSheet = workBook.getSheet(REGION_SHEET);
		Iterator<Row> rows = regionSheet.rowIterator();
		Iterator<Cell> cells = null;
		XSSFRow row = null;
		XSSFCell cell = null;
		RegionUploadVO uploadedRegion = null;
		boolean rowContainsError = false;
		while (rows.hasNext()) {
			row = (XSSFRow) rows.next();
			// skip the first 2 row. first row is the schema and second is the header
			if (row.getRowNum() < 2) {
				continue;
			}
			cells = row.cellIterator();
			uploadedRegion = new RegionUploadVO();
			int cellIndex = 0;
			while (cells.hasNext()) {
				cell = (XSSFCell) cells.next();
				cellIndex = cell.getColumnIndex();
				if (cellIndex == REGION_ID_INDEX) {
					if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
						try {
							uploadedRegion.setSourceRegionId(String.valueOf(cell.getNumericCellValue()));
						}
						catch (NumberFormatException nfe) {
							// TODO: mark this record as error
							LOG.error("Source region id is not present");
							rowContainsError = true;
							break;
						}
					}
					else {
						uploadedRegion.setSourceRegionId(cell.getStringCellValue());
					}
				}
				else if (cellIndex == REGION_NAME_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						uploadedRegion.setRegionName(cell.getStringCellValue().trim());
					}
					else {
						LOG.error("Region name is not present");
						rowContainsError = true;
						break;
					}
				}
				else if (cellIndex == REGION_ADDRESS1_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						uploadedRegion.setRegionAddress1(cell.getStringCellValue());
						uploadedRegion.setAddressSet(true);
					}
				}
				else if (cellIndex == REGION_ADDRESS2_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						uploadedRegion.setRegionAddress2(cell.getStringCellValue());
						uploadedRegion.setAddressSet(true);
					}
				}
				else if (cellIndex == REGION_CITY_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						uploadedRegion.setRegionCity(cell.getStringCellValue());
					}
				}
				else if (cellIndex == REGION_STATE_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						uploadedRegion.setRegionState(cell.getStringCellValue());
					}
				}
				else if (cellIndex == REGION_ZIP_INDEX) {
					if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
						if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
							uploadedRegion.setRegionZipcode(cell.getStringCellValue());
						}
						else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
							uploadedRegion.setRegionZipcode(String.valueOf((int) cell.getNumericCellValue()));
						}
					}
				}
			}
			if (rowContainsError) {
				LOG.error("Could not process row");
				if (regionErrors == null) {
					regionErrors = new ArrayList<>();
				}
				regionErrors.add("Error in Region row " + row.getRowNum());
				rowContainsError = false;
				continue;
			}
			uploadedRegions.add(uploadedRegion);
		}
		return uploadedRegions;
	}

	// modifies the list of regionsToUpload with the actual region id
	private void uploadRegions(List<RegionUploadVO> regionsToUpload, User adminUser, List<String> regionErrors) {
		LOG.debug("Uploading regions");
		Region region = null;
		for (RegionUploadVO regionToUpload : regionsToUpload) {
			try {
				region = createRegion(adminUser, regionToUpload);
				regionToUpload.setRegionId(region.getRegionId());
			}
			catch (InvalidInputException e) {
				LOG.error("InvalidInputException while uploading region to database. " + regionToUpload.getSourceRegionId(), e);
				regionErrors.add("Error while uploading region to database. " + regionToUpload.getSourceRegionId());
			}
			catch (RegionAdditionException e) {
				LOG.error("RegionAdditionException while uploading region to database. " + regionToUpload.getSourceRegionId(), e);
				regionErrors.add("Error while uploading region to database. " + regionToUpload.getSourceRegionId());
			}
			catch (SolrException e) {
				LOG.error("SolrException while uploading region to database. " + regionToUpload.getSourceRegionId(), e);
				regionErrors.add("Error while uploading region to database. " + regionToUpload.getSourceRegionId());
			}
		}
	}


    boolean checkIfEmailIdExists( String emailId, Company company ) throws InvalidInputException
    {
        boolean status = false;
        emailId = extractEmailId( emailId );
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "EmailId is empty" );
        }
        try {
            userManagementService.getUserByEmailAddress( emailId );
            status = true;
        } catch ( NoRecordsFetchedException e ) {
            status = false;
        }
        return status;
    }
    
    
    boolean checkIfEmailIdExistsWithCompany( String emailId, Company company ) throws InvalidInputException
    {
        boolean status = false;
        emailId = extractEmailId( emailId );
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "EmailId is empty" );
        }
        try {
            User user = userManagementService.getUserByEmailAddress( emailId );
            if ( user.getCompany().getCompanyId() == company.getCompanyId() ) {
                status = true;
            }
        } catch ( NoRecordsFetchedException e ) {
            status = false;
        }
        return status;
    }
    
    

    public static String[] removeElements( String[] input, String deleteMe )
    {
        List<String> result = new LinkedList<String>();

        for ( String item : input )
            if ( !deleteMe.equals( item ) )
                result.add( item );

        String[] modifiedArray = result.toArray( new String[result.size()] );
        return modifiedArray;
    }
    
    
    String extractEmailId( String emailId )
    {
        if ( emailId.contains( "\"" ) ) {
            emailId = emailId.replace( "\"", "" );
        }
        String firstName = "";
        String lastName = "";
        String toRemove = null;
        if ( emailId.indexOf( "@" ) != -1 && emailId.indexOf( "." ) != -1 ) {
            if ( emailId.contains( " " ) ) {
                String[] userArray = emailId.split( " " );
                String[] userInformation = removeElements( userArray, "" );
                List<String> tempList = new LinkedList<String>();
                for ( String str : userInformation ) {
                    tempList.add( str );
                }
                String tempString = "";
                for ( int i = 0; i < tempList.size(); i++ ) {

                    LOG.debug( "removing extra spaces " );
                    if ( tempList.get( i ).equalsIgnoreCase( "<" ) ) {
                        if ( i + 1 < tempList.size() ) {
                            if ( !tempList.get( i + 1 ).contains( "<" ) ) {
                                tempString = tempList.get( i ).concat( tempList.get( i + 1 ) );

                                toRemove = tempList.get( i + 1 );
                                if ( i + 2 < tempList.size() ) {

                                    if ( tempList.get( i + 2 ).equalsIgnoreCase( ">" ) ) {
                                        tempString = tempString.concat( tempList.get( i + 2 ) );


                                    }
                                }
                            }
                        }
                    } else if ( tempList.get( i ).equalsIgnoreCase( ">" ) ) {
                        if ( !tempList.get( i - 1 ).contains( ">" ) ) {
                            if ( tempString.isEmpty() ) {
                                tempString = tempList.get( i - 1 ).concat( tempList.get( i ) );
                                toRemove = tempList.get( i - 1 );
                            }

                        }
                    }

                }
                if ( !tempString.isEmpty() ) {
                    tempList.add( tempString );
                }
                Iterator<String> it = tempList.iterator();
                while ( it.hasNext() ) {
                    String iteratedValue = it.next();
                    if ( iteratedValue.equalsIgnoreCase( "<" ) || iteratedValue.equalsIgnoreCase( ">" ) ) {
                        it.remove();
                    }
                    if ( toRemove != null ) {
                        if ( iteratedValue.equalsIgnoreCase( toRemove ) ) {
                            it.remove();
                        }
                    }
                }
                userInformation = tempList.toArray( new String[tempList.size()] );
                if ( userInformation.length >= 3 ) {
                    LOG.debug( "This contains middle name as well" );
                    for ( int i = 0; i < userInformation.length - 1; i++ ) {
                        firstName = firstName + userInformation[i] + " ";
                    }
                    firstName = firstName.trim();
                    lastName = userInformation[userInformation.length - 1];
                    if ( lastName.contains( "<" ) ) {
                        emailId = lastName.substring( lastName.indexOf( "<" ) + 1, lastName.length() - 1 );
                        lastName = lastName.substring( 0, lastName.indexOf( "<" ) );
                        if ( lastName.equalsIgnoreCase( "" ) ) {
                            lastName = userInformation[userInformation.length - 2];
                            if ( firstName.contains( lastName ) ) {
                                firstName = firstName.substring( 0, firstName.indexOf( lastName ) );
                            }
                        }
                    }

                } else if ( userInformation.length == 2 ) {
                    firstName = userInformation[0];
                    lastName = userInformation[1];
                    if ( lastName.contains( "<" ) ) {
                        emailId = lastName.substring( lastName.indexOf( "<" ) + 1, lastName.length() - 1 );
                        lastName = lastName.substring( 0, lastName.indexOf( "<" ) );
                    }
                }
            } else {
                LOG.debug( "Contains no space hence wont have a last name" );
                lastName = null;
                if ( emailId.contains( "<" ) ) {
                    firstName = emailId.substring( 0, emailId.indexOf( "<" ) );
                    if ( firstName.equalsIgnoreCase( "" ) ) {
                        firstName = emailId.substring( emailId.indexOf( "<" ) + 1, emailId.indexOf( "@" ) );
                    }
                    emailId = emailId.substring( emailId.indexOf( "<" ) + 1, emailId.indexOf( ">" ) );

                } else {
                    LOG.debug( "This doesnt contain a first name and last name" );
                    firstName = emailId.substring( 0, emailId.indexOf( "@" ) );
                }

            }
        }
        return emailId;
    }
    
	Company getCompany(User user) throws InvalidInputException {
		Company company = user.getCompany();
		if (company == null) {
			LOG.error("Company property not found in admin user object!");
			throw new InvalidInputException("Company property not found in admin user object!");

		}
		return company;
	}

	LicenseDetail getLicenseDetail(Company company) throws InvalidInputException {
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

	@SuppressWarnings("unchecked")
	User addUser(UserUploadVO user, User adminUser) throws InvalidInputException, NoRecordsFetchedException, SolrException,
			UserAssignmentException, UserAdditionException {
		User uploadedUser = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<User> userList = new ArrayList<User>();
		if ( checkIfEmailIdExists( user.getEmailId(), adminUser.getCompany() ) ) {
            throw new UserAdditionException( "The user already exists" );
        }
		if (user.isBelongsToCompany()) {
			// He belongs to the company
			LOG.debug("Adding user : " + user.getEmailId() + " belongs to company");
			map = organizationManagementService.addIndividual(adminUser, 0, 0, 0, new String[] { user.getEmailId() }, false, true);
			if (map != null) {
				userList = (List<User>) map.get(CommonConstants.VALID_USERS_LIST);
			}
		}
		else if (user.getBranchId() > 0l) {
			// He belongs to a branch
			LOG.debug("Adding user : " + user.getEmailId() + " belongs to branch : " + user.getBranchId());
			Branch branch = branchDao.findById(Branch.class, user.getBranchId());

			if (user.isBranchAdmin()) {
				LOG.debug("User is the branch admin");
				map = organizationManagementService.addIndividual(adminUser, 0, branch.getBranchId(), branch.getRegion().getRegionId(),
						new String[] { user.getEmailId() }, true, true);
				if (user.isAgent()) {
					organizationManagementService.addIndividual(adminUser, 0, branch.getBranchId(), branch.getRegion().getRegionId(),
							new String[] { user.getEmailId() }, false, true);
				}
				if (map != null) {
					userList = (List<User>) map.get(CommonConstants.VALID_USERS_LIST);
				}
				LOG.debug("Added user : " + user.getEmailId());
			}
			else {
				LOG.debug("User is not the branch admin");
				map = organizationManagementService.addIndividual(adminUser, 0, branch.getBranchId(), branch.getRegion().getRegionId(),
						new String[] { user.getEmailId() }, false, true);
				if (map != null) {
					userList = (List<User>) map.get(CommonConstants.VALID_USERS_LIST);
				}
				LOG.debug("Added user : " + user.getEmailId());
			}
		}
		else if (user.getRegionId() > 0l) {
			// He belongs to the region
			LOG.debug("Adding user : " + user.getEmailId() + " belongs to region : " + user.getRegionId());
			Region region = regionDao.findById(Region.class, user.getRegionId());
			if (user.isRegionAdmin()) {
				LOG.debug("User is the region admin.");
				map = organizationManagementService.addIndividual(adminUser, 0, 0, region.getRegionId(), new String[] { user.getEmailId() }, true, true);
				if (user.isAgent()) {
					organizationManagementService.addIndividual(adminUser, 0, 0, region.getRegionId(), new String[] { user.getEmailId() }, false, true);
				}
				if (map != null) {
					userList = (List<User>) map.get(CommonConstants.VALID_USERS_LIST);
				}
				LOG.debug("Added user : " + user.getEmailId());
			}
			else {
				LOG.debug("User is not the admin of the region");
				map = organizationManagementService.addIndividual(adminUser, 0, 0, region.getRegionId(), new String[] { user.getEmailId() }, false, true);
				if (map != null) {
					userList = (List<User>) map.get(CommonConstants.VALID_USERS_LIST);
				}
				LOG.debug("Added user : " + user.getEmailId());
			}
		}
		else {
			LOG.error("Please specifiy where the user belongs!");
			throw new UserAdditionException("Please specifiy where the user belongs!");
		}

		if (userList != null && !userList.isEmpty()) {
			uploadedUser = userList.get(0);
		}
		return uploadedUser;

	}

	User assignUser(UserUploadVO user, User adminUser) throws UserAdditionException, InvalidInputException, SolrException,
			NoRecordsFetchedException, UserAssignmentException {

		LOG.info("User already exists so assigning user to approprite place");
        if ( !( checkIfEmailIdExistsWithCompany( user.getEmailId(), adminUser.getCompany() ) ) ) {
            throw new UserAdditionException( "User : " + user.getEmailId()
                + " belongs to a different company" );
        }
		User assigneeUser = userManagementService.getUserByEmailAddress( extractEmailId(user.getEmailId()) );

		if (user.isBelongsToCompany()) {
			LOG.debug("Assigning user id : " + assigneeUser.getUserId());
			organizationManagementService.addIndividual(adminUser, assigneeUser.getUserId(), 0, 0, null, false, true);
		}
		else if (user.getBranchId() > 0l) {
			// User belongs to a branch
			LOG.debug("Assigning user : " + user.getEmailId() + " belongs to branch : " + user.getBranchId());
			Branch branch = branchDao.findById(Branch.class, user.getBranchId());
			if (user.isBranchAdmin()) {
				LOG.debug("User is the branch admin");
				organizationManagementService.addIndividual(adminUser, assigneeUser.getUserId(), branch.getBranchId(), branch.getRegion()
						.getRegionId(), null, true, true);
				if (user.isAgent()) {
					organizationManagementService.addIndividual(adminUser, assigneeUser.getUserId(), branch.getBranchId(), branch.getRegion()
							.getRegionId(), null, false, true);
				}
				LOG.debug("Added user : " + user.getEmailId());
			}
			else {
				LOG.debug("User is not the branch admin");
				organizationManagementService.addIndividual(adminUser, assigneeUser.getUserId(), branch.getBranchId(), branch.getRegion()
						.getRegionId(), null, false, true);
				LOG.debug("Added user : " + user.getEmailId());
			}
		}
		else if (user.getRegionId() > 0l) {
			// He belongs to the region
			LOG.debug("Assigning user : " + user.getEmailId() + " belongs to region : " + user.getRegionId());
			Region region = regionDao.findById(Region.class, user.getRegionId());
			if (user.isRegionAdmin()) {
				LOG.debug("User is the region admin.");
				organizationManagementService.addIndividual(adminUser, assigneeUser.getUserId(), 0, region.getRegionId(), null, true, true);
				LOG.debug("Added user : " + user.getEmailId());
				if (user.isAgent()) {
					organizationManagementService.addIndividual(adminUser, assigneeUser.getUserId(), 0, region.getRegionId(), null, false, true);
				}

			}
			else {
				LOG.debug("User is not the admin of the region");
				organizationManagementService.addIndividual(adminUser, assigneeUser.getUserId(), 0, region.getRegionId(), null, false, true);
				LOG.debug("Added user : " + user.getEmailId());
			}
		}

		return assigneeUser;

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
				if (!organizationManagementService.validateEmail( user.getEmailId() )) {
					LOG.error("Email id for the user is invalid!");
					throw new UserAdditionException("Email id for the user is invalid!");
				}
			}
		}

		if (checkIfEmailIdExists(user.getEmailId(), company)) {
			LOG.debug("Validations complete, adding user!");
			assignUser(user, adminUser);
			LOG.debug("User added!");
		}
		else {
			LOG.debug("User already exists. Assigning him appropriately");
			addUser(user, adminUser);
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
	public Branch createBranch(User adminUser, BranchUploadVO branch) throws InvalidInputException, BranchAdditionException, SolrException {
		Branch newBranch = null;
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

			/*
			 * if (!validateBranch(branch, company)) {
			 * LOG.error("Branch with the name already exists!"); throw new
			 * BranchAdditionException("Branch with the name already exists!"); } if
			 * (branch.getAssignedRegionName() != null) { // He belongs to the region
			 * LOG.debug("Adding branch : " + branch.getBranchName() + " belongs to region : " +
			 * branch.getAssignedRegionName()); Map<String, Object> queries = new HashMap<String,
			 * Object>(); queries.put(CommonConstants.REGION_NAME_COLUMN,
			 * branch.getAssignedRegionName()); queries.put(CommonConstants.COMPANY_COLUMN,
			 * adminUser.getCompany()); queries.put(CommonConstants.STATUS_COLUMN,
			 * CommonConstants.STATUS_ACTIVE); List<Region> regions =
			 * regionDao.findByKeyValue(Region.class, queries); if (regions == null ||
			 * regions.isEmpty()) { LOG.error("Region name is invalid!"); throw new
			 * BranchAdditionException("Region name is invalid!"); } else { Region region =
			 * regions.get(CommonConstants.INITIAL_INDEX);
			 * organizationManagementService.addNewBranch(adminUser, region.getRegionId(),
			 * CommonConstants.NO, branch.getBranchName(), branch.getBranchAddress1(),
			 * branch.getBranchAddress2(), branch.getBranchCountry(), branch.getBranchCountryCode(),
			 * branch.getBranchState(), branch.getBranchCity(), branch.getBranchZipcode());
			 * LOG.debug("Branch added!"); } } else if (branch.isAssignToCompany()) {
			 * LOG.debug("adding branch : " + branch.getBranchName() + " under the company!");
			 * organizationManagementService.addNewBranch(adminUser, 0l, CommonConstants.NO,
			 * branch.getBranchName(), branch.getBranchAddress1(), branch.getBranchAddress2(),
			 * branch.getBranchCountry(), branch.getBranchCountryCode(), branch.getBranchState(),
			 * branch.getBranchCity(), branch.getBranchZipcode()); LOG.debug("Branch added!"); }
			 * else { LOG.error("Please specifiy where the branch belongs!"); throw new
			 * BranchAdditionException("Please specifiy where the branch belongs!"); }
			 */
			newBranch = organizationManagementService.addNewBranch(adminUser, branch.getRegionId(), CommonConstants.NO, branch.getBranchName(),
					branch.getBranchAddress1(), branch.getBranchAddress2(), COUNTRY, COUNTRY_CODE, branch.getBranchState(), branch.getBranchCity(),
					branch.getBranchZipcode());
		}
		else {
			LOG.error("admin user : " + adminUser.getEmailId() + " is not authorized to add branches! Accounttype : "
					+ companyLicenseDetail.getAccountsMaster().getAccountName());
			throw new BranchAdditionException("admin user : " + adminUser.getEmailId() + " is not authorized to add branches!");
		}
		return newBranch;
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
	public Region createRegion(User adminUser, RegionUploadVO region) throws InvalidInputException, RegionAdditionException, SolrException {
		Region newRegion = null;
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
			/*if (!validateRegion(region, company)) {
				LOG.error("Region with that name already exists!");
				throw new RegionAdditionException("Region with that name already exists!");
			}*/
			LOG.debug("Adding region : " + region.getRegionName());
			newRegion = organizationManagementService.addNewRegion(adminUser, region.getRegionName(), CommonConstants.NO, region.getRegionAddress1(),
					region.getRegionAddress2(), region.getRegionCountry(), region.getRegionCountryCode(), region.getRegionState(),
					region.getRegionCity(), region.getRegionZipcode());
			organizationManagementService.addNewBranch(adminUser, newRegion.getRegionId(), CommonConstants.YES, CommonConstants.DEFAULT_BRANCH_NAME,
					null, null, null, null, null, null, null);
		}
		else {
			LOG.error("admin user : " + adminUser.getEmailId() + " is not authorized to add regions");
			throw new RegionAdditionException("admin user : " + adminUser.getEmailId() + " is not authorized to add regions");
		}
		return newRegion;
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
					if (user.getLoginPassword() == null) {
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

	@Transactional
	@Override
	public List<FileUpload> getFilesToBeUploaded() throws NoRecordsFetchedException {
		LOG.info("Check if files need to be uploaded");
		Map<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.FILE_UPLOAD_TYPE_COLUMN, CommonConstants.FILE_UPLOAD_HIERARCHY_TYPE);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		List<FileUpload> filesToBeUploaded = fileUploadDao.findByKeyValue(FileUpload.class, queries);
		if (filesToBeUploaded == null || filesToBeUploaded.isEmpty()) {
			throw new NoRecordsFetchedException("No files to be uploaded");
		}
		return filesToBeUploaded;
	}

	@Transactional
	@Override
	public void updateFileUploadRecord(FileUpload fileUpload) throws InvalidInputException {
		LOG.info("Check if files need to be uploaded");
		if (fileUpload == null) {
			throw new InvalidInputException("File upload is null");
		}
		fileUploadDao.update(fileUpload);
	}

}
