package com.realtech.socialsurvey.core.services.upload.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.entities.SurveyUploadVO;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.services.surveybuilder.impl.DuplicateSurveyRequestException;
import com.realtech.socialsurvey.core.services.surveybuilder.impl.SelfSurveyInitiationException;
import com.realtech.socialsurvey.core.services.upload.BulkSurveyFileUpload;

@Component
public class BulkSurveyFileUploadImpl implements BulkSurveyFileUpload {

	private static final Logger LOG = LoggerFactory.getLogger(BulkSurveyFileUploadImpl.class);

	private static final int AGENT_EMAIL_INDEX = 0;
	private static final int CUSTOMER_FIRSTNAME_INDEX = 1;
	private static final int CUSTOMER_LASTNAME_INDEX = 2;
	private static final int CUSTOMER_EMAIL_INDEX = 3;

	@Value("${FILEUPLOAD_DIRECTORY_LOCATION}")
	private String fileDirectory;
	
	@Value("${MASK_EMAIL_ADDRESS}")
	private String maskEmail;

	@Autowired
	private SurveyHandler surveyHandler;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private GenericDao<FileUpload, Long> fileUploadDao;
	
	@Autowired
	private Utils utils;

	private List<String> uploadErrors;

	@Override
	public void uploadBulkSurveyFile(FileUpload fileUpload) throws InvalidInputException, ProfileNotFoundException {
		LOG.info("Uploading file for bulk survey");
		if (fileUpload == null || fileUpload.getFileName() == null || fileUpload.getFileName().isEmpty() || fileUpload.getCompany() == null) {
			LOG.error("Invalid file upload for bulk survey upload");
			throw new InvalidInputException("Invalid file upload for bulk survey upload");
		}
		List<SurveyUploadVO> uploadList = parseSurveyCSVFile(fileUpload.getFileName());
		initiateSurvey(uploadList, fileUpload.getCompany().getCompanyId());
	}

	private List<SurveyUploadVO> parseSurveyCSVFile(String fileName) {
		LOG.debug("Parsing file " + fileName + " for survey");
		InputStream fileStream = null;
		List<SurveyUploadVO> surveyUploadList = null;
		Set<String> customerEamilAddressTracker = new HashSet<>(); // tracks duplicate customer
																	// email address
		try {
			fileStream = new FileInputStream(fileDirectory + fileName);
			XSSFWorkbook workBook = new XSSFWorkbook(fileStream);
			XSSFSheet regionSheet = workBook.getSheetAt(0);
			Iterator<Row> rows = regionSheet.rowIterator();
			Iterator<Cell> cells = null;
			XSSFRow row = null;
			XSSFCell cell = null;
			uploadErrors = new ArrayList<String>();
			SurveyUploadVO surveyUploadVO = null;
			surveyUploadList = new ArrayList<SurveyUploadVO>();
			while (rows.hasNext()) {
				row = (XSSFRow) rows.next();
				// skip the first 1 row for the header
				if (row.getRowNum() < 1) {
					continue;
				}
				LOG.info("Processing row " + row.getRowNum() + " from the file.");
				surveyUploadVO = new SurveyUploadVO();
				cells = row.cellIterator();
				boolean rowContainsError = false;
				while (cells.hasNext()) {
					cell = (XSSFCell) cells.next();
					if (cell.getColumnIndex() == AGENT_EMAIL_INDEX) {
						if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
							if (!cell.getStringCellValue().isEmpty()) {
								surveyUploadVO.setAgentEmailId(cell.getStringCellValue().trim());
							}
							else {
								LOG.error("Agent email is not present");
								uploadErrors.add("Row " + row.getRowNum() + " has error. Agent email address is not present");
								rowContainsError = true;
								break;
							}
						}
						else {
							LOG.error("Agent email is not present");
							uploadErrors.add("Row " + row.getRowNum() + " has error. Agent email address is not present");
							rowContainsError = true;
							break;
						}
					}
					else if (cell.getColumnIndex() == CUSTOMER_FIRSTNAME_INDEX) {
						if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
							if (!cell.getStringCellValue().isEmpty()) {
								surveyUploadVO.setCustomerFirstName(cell.getStringCellValue().trim());
							}
							else {
								LOG.error("Customer first name is not present");
								uploadErrors.add("Row " + row.getRowNum() + " has error. Customer first name is not present");
								rowContainsError = true;
								break;
							}
						}
						else {
							LOG.error("Customer first name is not present");
							uploadErrors.add("Row " + row.getRowNum() + " has error. Customer first name is not present");
							rowContainsError = true;
							break;
						}
					}
					else if (cell.getColumnIndex() == CUSTOMER_LASTNAME_INDEX) {
						if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
							surveyUploadVO.setCustomerLastName(cell.getStringCellValue().trim());
						}
					}
					else if (cell.getColumnIndex() == CUSTOMER_EMAIL_INDEX) {
						if (cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
							if (!cell.getStringCellValue().isEmpty()) {
								String emailId = cell.getStringCellValue().trim();
								if(maskEmail.equals(CommonConstants.YES_STRING)){
									emailId = utils.maskEmailAddress(emailId); 
								}
								surveyUploadVO.setCustomerEmailId(emailId);
							}
							else {
								LOG.error("Customer email address is not present");
								uploadErrors.add("Row " + row.getRowNum() + " has error. Customer email address is not present");
								rowContainsError = true;
								break;
							}
							if (!customerEamilAddressTracker.add(surveyUploadVO.getCustomerEmailId())) {
								LOG.error("Customer email address is already present");
								uploadErrors.add("Row " + row.getRowNum() + " has error. Customer email address is already present");
								rowContainsError = true;
								break;
							}
						}
						else {
							LOG.error("Customer email address is not present");
							uploadErrors.add("Row " + row.getRowNum() + " has error. Customer email address is not present");
							rowContainsError = true;
							break;
						}
					}
				}
				if (!rowContainsError) {
					// check survey obect if it is fine
					if (checkUploadObject(surveyUploadVO)) {
						LOG.debug("Adding " + row.getRowNum() + " to the upload list.");
						surveyUploadList.add(surveyUploadVO);
					}
				}
			}
		}
		catch (FileNotFoundException e) {
			LOG.error("Could not parse the file: " + e.getMessage(), e);
		}
		catch (IOException e) {
			LOG.error("Could not parse the file: " + e.getMessage(), e);
		}
		LOG.debug("Parsing file " + fileName + " for survey complete.");
		return surveyUploadList;
	}

	boolean checkUploadObject(SurveyUploadVO surveyUpload) {
		if (surveyUpload.getAgentEmailId() == null || surveyUpload.getAgentEmailId().isEmpty()) {
			LOG.warn("Agent email is not present");
			return false;
		}
		if (surveyUpload.getCustomerFirstName() == null || surveyUpload.getCustomerFirstName().isEmpty()) {
			LOG.warn("Customer first name is not present");
			return false;
		}
		if (surveyUpload.getCustomerEmailId() == null || surveyUpload.getCustomerEmailId().isEmpty()) {
			LOG.warn("Customer email is not present");
			return false;
		}

		if (surveyUpload.getCustomerEmailId() != null && surveyUpload.getAgentEmailId() != null
				&& surveyUpload.getCustomerEmailId().trim().equalsIgnoreCase(surveyUpload.getAgentEmailId().trim())) {
			LOG.warn("Survey being sent for self");
			return false;
		}
		return true;
	}

	void initiateSurvey(List<SurveyUploadVO> surveyUploadList, long companyId) throws ProfileNotFoundException {
		LOG.debug("Sending survey reminders");
		for (SurveyUploadVO surveyUpload : surveyUploadList) {
			LOG.debug("Sending survey request to " + surveyUpload.getCustomerFirstName() + " at " + surveyUpload.getCustomerEmailId());
			try {
				User agent = userManagementService.getUserByEmail(surveyUpload.getAgentEmailId());
				if (agent.getCompany().getCompanyId() == companyId) {
					surveyHandler.initiateSurveyRequest(agent.getUserId(), surveyUpload.getCustomerEmailId(), surveyUpload.getCustomerFirstName(),
							surveyUpload.getCustomerLastName(), CommonConstants.SURVEY_SOURCE_FILE_UPLOAD, null );
				}
				else {
					LOG.warn("Agent does not belong to the company");
					throw new InvalidInputException("Agent does not belong to the company.");
				}
			}
			catch (InvalidInputException | NoRecordsFetchedException | DuplicateSurveyRequestException | SelfSurveyInitiationException
					| SolrException | UndeliveredEmailException e) {
				LOG.error("Could not send survey request to " + surveyUpload.getCustomerEmailId(), e);
			}
		}
	}

	@Override
	@Transactional
	public List<FileUpload> getSurveyUploadFiles() throws NoRecordsFetchedException {
		LOG.info("Fetching the list of files to be uploaded to send bulk suvey requests");
		Map<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.FILE_UPLOAD_TYPE_COLUMN, CommonConstants.FILE_UPLOAD_SURVEY_TYPE);
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
		LOG.info("Updating file record status");
		if (fileUpload == null) {
			throw new InvalidInputException("File upload is null");
		}
		fileUploadDao.update(fileUpload);
	}
}
