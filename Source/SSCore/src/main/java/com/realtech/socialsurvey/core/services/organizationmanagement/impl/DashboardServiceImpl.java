package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.services.organizationmanagement.DashboardService;

// JIRA SS-137 BY RM05:BOC
/**
 * Class with methods defined to show dash board of user.
 */

@Component
public class DashboardServiceImpl implements DashboardService, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(DashboardServiceImpl.class);
	private static Map<String, Integer> weightageColumns;

	@Autowired
	private SurveyDetailsDao surveyDetailsDao;

	@Autowired
	private OrganizationUnitSettingsDao organizationUnitSettingsDao;

	@Override
	public long getAllSurveyCountForPastNdays(String columnName, long columnValue, int numberOfDays) {
		return surveyDetailsDao.getSentSurveyCount(columnName, columnValue, numberOfDays);
	}

	@Override
	public long getCompletedSurveyCountForPastNdays(String columnName, long columnValue, int numberOfDays) {
		return surveyDetailsDao.getCompletedSurveyCount(columnName, columnValue, numberOfDays);
	}

	@Override
	public long getClickedSurveyCountForPastNdays(String columnName, long columnValue, int numberOfDays) {
		return surveyDetailsDao.getClickedSurveyCount(columnName, columnValue, numberOfDays);
	}

	@Override
	public long getSocialPostsForPastNdays(String columnName, long columnValue, int numberOfDays) {
		return surveyDetailsDao.getSocialPostsCount(columnName, columnValue, numberOfDays);
	}

	@Override
	public double getSurveyScore(String columnName, long columnValue, int numberOfDays) {
		return surveyDetailsDao.getRatingForPastNdays(columnName, columnValue, numberOfDays, true);
	}

	@Override
	public int getProfileCompletionPercentage(User user, String columnName, long columnValue, UserSettings userSettings) {
		LOG.info("Method to calculate profile completion percentage started.");
		int totalWeight = 0;
		double currentWeight = 0;
		OrganizationUnitSettings organizationUnitSettings = new OrganizationUnitSettings();
		switch (columnName) {
			case CommonConstants.COMPANY_ID_COLUMN:
				organizationUnitSettings = userSettings.getCompanySettings();
				break;
			case CommonConstants.REGION_ID_COLUMN:
				organizationUnitSettings = userSettings.getRegionSettings().get(columnValue);
				break;
			case CommonConstants.BRANCH_ID_COLUMN:
				organizationUnitSettings = userSettings.getBranchSettings().get(columnValue);
				break;
			case CommonConstants.AGENT_ID_COLUMN:
				organizationUnitSettings = userSettings.getAgentSettings();
				break;
			default:
				LOG.error("Invalid value passed for columnName. It should be either of companyId/regionId/branchId/agentId.");
		}
		if (weightageColumns.containsKey("email")) {
			totalWeight += weightageColumns.get("email");
			if (organizationUnitSettings.getContact_details() != null && organizationUnitSettings.getContact_details().getMail_ids() != null)
				currentWeight += weightageColumns.get("email");
		}
		if (weightageColumns.containsKey("about_me")) {
			totalWeight += weightageColumns.get("about_me");
			if (organizationUnitSettings.getContact_details() != null && organizationUnitSettings.getContact_details().getAbout_me() != null)
				currentWeight += weightageColumns.get("about_me");
		}
		if (weightageColumns.containsKey("contact_number")) {
			totalWeight += weightageColumns.get("contact_number");
			if (organizationUnitSettings.getContact_details() != null && organizationUnitSettings.getContact_details().getContact_numbers() != null)
				currentWeight += weightageColumns.get("contact_number");
		}
		if (weightageColumns.containsKey("profile_image")) {
			totalWeight += weightageColumns.get("profile_image");
			if (organizationUnitSettings.getProfileImageUrl() != null)
				currentWeight += weightageColumns.get("profile_image");
		}
		if (weightageColumns.containsKey("title")) {
			totalWeight += weightageColumns.get("title");
			if (organizationUnitSettings.getContact_details().getTitle() != null)
				currentWeight += weightageColumns.get("title");
		}
		LOG.info("Method to calculate profile completion percentage finished.");
		try {
			return (int) Math.round(currentWeight * 100 / totalWeight);
		}
		catch (ArithmeticException e) {
			LOG.error("Exception caught in getProfileCompletionPercentage(). Nested exception is ", e);
			return 0;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		weightageColumns = new HashMap<>();
		weightageColumns.put("email", 1);
		weightageColumns.put("about_me", 1);
		weightageColumns.put("title", 1);
		weightageColumns.put("profile_image", 1);
		weightageColumns.put("contact_number", 1);
	}

	/*
	 * Method to calculate number of badges based upon surveyScore, count of surveys sent and
	 * profile completeness.
	 */
	@Override
	public int getBadges(int surveyScore, int surveyCount, int socialPosts, int profileCompleteness) {
		LOG.info("Method to calculate number of badges started.");
		int badges = 0;
		double normalizedSurveyScore = surveyScore * 25 / CommonConstants.MAX_SURVEY_SCORE;
		double normalizedProfileCompleteness = profileCompleteness * 25 / 100;
		if (surveyCount > CommonConstants.MAX_SENT_SURVEY_COUNT)
			surveyCount = CommonConstants.MAX_SENT_SURVEY_COUNT;
		double normalizedSurveyCount = surveyCount * 25 / CommonConstants.MAX_SENT_SURVEY_COUNT;
		if (socialPosts > CommonConstants.MAX_SOCIAL_POSTS)
			socialPosts = CommonConstants.MAX_SOCIAL_POSTS;
		double normalizedSocialPosts = socialPosts * 25 / CommonConstants.MAX_SOCIAL_POSTS;
		int overallPercentage = (int) Math.round(normalizedSurveyScore + normalizedProfileCompleteness + normalizedSurveyCount
				+ normalizedSocialPosts);
		if (overallPercentage < 34)
			badges = 1;
		else if (overallPercentage < 67)
			badges = 2;
		else
			badges = 3;
		LOG.info("Method to calculate number of badges finished.");
		return badges;
	}

	/*
	 * Method to create excel file from all the completed survey data.
	 */
	@Override
	public XSSFWorkbook downloadCompleteSurveyData(List<SurveyDetails> surveyDetails, String fileLocation) throws IOException {
		// Blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Create a blank sheet
		XSSFSheet sheet = workbook.createSheet();
		XSSFDataFormat df = workbook.createDataFormat();
		CellStyle style = workbook.createCellStyle();
		style.setDataFormat(df.getFormat("d-mm-yyyy"));
		Integer counter = 1;
		int max = 0;
		int internalMax = 0;
		// This data needs to be written (List<Object>)
		Map<String, List<Object>> data = new TreeMap<>();
		List<Object> surveyDetailsToPopulate = new ArrayList<>();
		for (SurveyDetails survey : surveyDetails) {
			internalMax = 0;
			surveyDetailsToPopulate.add(survey.getCustomerFirstName());
			surveyDetailsToPopulate.add(survey.getCustomerLastName());
			surveyDetailsToPopulate.add(survey.getCreatedOn());
			surveyDetailsToPopulate.add(survey.getModifiedOn());
			surveyDetailsToPopulate.add(survey.getScore());
			surveyDetailsToPopulate.add(survey.getMood());
			surveyDetailsToPopulate.add(survey.getReview());
			if (survey.getSharedOn() == null)
				surveyDetailsToPopulate.add(null);
			else
				surveyDetailsToPopulate.add(StringUtils.join(survey.getSharedOn(), ","));
			for (SurveyResponse response : survey.getSurveyResponse()) {
				internalMax++;
				surveyDetailsToPopulate.add(response.getAnswer());
			}
			data.put((++counter).toString(), surveyDetailsToPopulate);
			surveyDetailsToPopulate = new ArrayList<>();
			if (internalMax > max)
				max = internalMax;
		}
		surveyDetailsToPopulate.add("First Name");
		surveyDetailsToPopulate.add("Last Name");
		surveyDetailsToPopulate.add("Started On");
		surveyDetailsToPopulate.add("Completed On");
		surveyDetailsToPopulate.add("Score");
		surveyDetailsToPopulate.add("Gateway Question");
		surveyDetailsToPopulate.add("Comments");
		surveyDetailsToPopulate.add("Social");
		for (counter = 1; counter <= max; counter++) {
			surveyDetailsToPopulate.add("Question " + counter);
		}
		data.put("1", surveyDetailsToPopulate);

		// Iterate over data and write to sheet
		Set<String> keyset = data.keySet();
		int rownum = 0;
		for (String key : keyset) {
			Row row = sheet.createRow(rownum++);
			List<Object> objArr = data.get(key);
			int cellnum = 0;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);
				if (obj instanceof String)
					cell.setCellValue((String) obj);
				else if (obj instanceof Integer)
					cell.setCellValue((Integer) obj);
				else if (obj instanceof Double)
					cell.setCellValue((Double) obj);
				else if (obj instanceof Date){
					cell.setCellStyle(style);
					cell.setCellValue((Date) obj);
				}
			}
		}
		/** try {
			// Write the workbook in file system
			FileOutputStream out = new FileOutputStream(new File(fileLocation));
			workbook.write(out);
			out.close();
		}
		catch (IOException e) {
			LOG.error("IOException caught in downloadCompleteSurveyData() while trying to create excel file at " + fileLocation);
			throw e;
		}*/
		return workbook;
	}

	/*
	 * Method to create excel file from all the incomplete survey data.
	 */
	@Override
	public XSSFWorkbook downloadIncompleteSurveyData(List<SurveyDetails> surveyDetails, String fileLocation) throws IOException {
		// Blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Create a blank sheet
		XSSFSheet sheet = workbook.createSheet();
		XSSFDataFormat df = workbook.createDataFormat();
		CellStyle style = workbook.createCellStyle();
		style.setDataFormat(df.getFormat("d-mm-yyyy"));
		Integer counter = 1;
		int max = 0;
		int internalMax = 0;
		// This data needs to be written (List<Object>)
		Map<String, List<Object>> data = new TreeMap<>();
		List<Object> surveyDetailsToPopulate = new ArrayList<>();
		for (SurveyDetails survey : surveyDetails) {
			internalMax = 0;
			surveyDetailsToPopulate.add(survey.getCustomerFirstName());
			surveyDetailsToPopulate.add(survey.getCustomerLastName());
			surveyDetailsToPopulate.add(survey.getCustomerEmail());
			surveyDetailsToPopulate.add(survey.getCreatedOn());
			surveyDetailsToPopulate.add(survey.getModifiedOn());
			surveyDetailsToPopulate.add(survey.getUrl());
			data.put((++counter).toString(), surveyDetailsToPopulate);
			surveyDetailsToPopulate = new ArrayList<>();
			if (internalMax > max)
				max = internalMax;
		}
		surveyDetailsToPopulate.add("First Name");
		surveyDetailsToPopulate.add("Last Name");
		surveyDetailsToPopulate.add("Email Id");
		surveyDetailsToPopulate.add("Started On");
		surveyDetailsToPopulate.add("Last Updated On");
		surveyDetailsToPopulate.add("Link To Survey");
		for (counter = 1; counter <= max; counter++) {
			internalMax++;
			surveyDetailsToPopulate.add("Question " + counter);
		}
		data.put("1", surveyDetailsToPopulate);

		// Iterate over data and write to sheet
		Set<String> keyset = data.keySet();
		int rownum = 0;
		for (String key : keyset) {
			Row row = sheet.createRow(rownum++);
			List<Object> objArr = data.get(key);
			int cellnum = 0;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);
				if (obj instanceof String)
					cell.setCellValue((String) obj);
				else if (obj instanceof Integer)
					cell.setCellValue((Integer) obj);
				else if (obj instanceof Date){
					cell.setCellStyle(style);
					cell.setCellValue((Date) obj);
				}
			}
		}
		/**try {
			// Write the workbook in file system
			File file = new File(fileLocation);
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			workbook.write(out);
			out.close();
		}
		catch (IOException e) {
			LOG.error("IOException caught in downloadCompleteSurveyData() while trying to create excel file at " + fileLocation);
			throw e;
		}*/
		return workbook;
	}
	
	@Override
	public Map<String, Map<String, Long>> getSurveyDetailsForGraph(String columnName, long columnValue, String reportType) throws ParseException{
		Map<String, Map<String, Long>> map = new HashMap<String, Map<String, Long>>();
		map.put("clicked", surveyDetailsDao.getClickedSurveyByCriteria(columnName, columnValue, reportType));
		map.put("sent", surveyDetailsDao.getSentSurveyByCriteria(columnName, columnValue, reportType));
		map.put("complete", surveyDetailsDao.getCompletedSurveyByCriteria(columnName, columnValue, reportType));
		map.put("socialposts", surveyDetailsDao.getSocialPostsCountByCriteria(columnName, columnValue, reportType));
		return map;
	}
}
// JIRA SS-137 BY RM05:EOC