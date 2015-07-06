package com.realtech.socialsurvey.core.starter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.FileUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;

/*
 * This class is responsible for getting all the profile images from Mongodb which point to Linkedin.
 * It stores into the Amazon server and updates the same in MongoDB.
 */
public class ImageLoader extends QuartzJobBean {

	public static final Logger LOG = LoggerFactory.getLogger(ImageLoader.class);
	
	private OrganizationManagementService organizationManagementService;
	private ProfileManagementService profileManagementService;
	private FileUploadService fileUploadService;
	
	private String amazonImageBucket;
	private String cdnUrl;
	
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) {
		LOG.info("Executing ImageUploader");
		new File(CommonConstants.TEMP_FOLDER).mkdir();
		// initialize the dependencies
		initializeDependencies(jobExecutionContext.getMergedJobDataMap());
		// Fetch all the profile images pointing to linkedin for company, regions, branches and individuals.
		Map<Long,OrganizationUnitSettings> companySettings = organizationManagementService.getSettingsMapWithLinkedinImage(CommonConstants.COMPANY);
		Map<Long,OrganizationUnitSettings> regionSettings = organizationManagementService.getSettingsMapWithLinkedinImage(CommonConstants.REGION_COLUMN);
		Map<Long,OrganizationUnitSettings> branchSettings = organizationManagementService.getSettingsMapWithLinkedinImage(CommonConstants.BRANCH_NAME_COLUMN);
		Map<Long,OrganizationUnitSettings> agentSettings = organizationManagementService.getSettingsMapWithLinkedinImage("agent");
		
		// Process all the company profile images.
		for(Entry<Long, OrganizationUnitSettings> companySetting:companySettings.entrySet()){
			try{
				String image = loadImages(companySetting.getValue());
				profileManagementService.updateProfileImage(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySetting.getValue(), image);
			} catch (Exception e) {
				LOG.error("Exception caught in ImageLoader while copying image from linkedin to SocialSurvey server. "
						+ "Nested exception is ", e);
				continue;
			}
		}
		
		// Process all the region profile images.
		for(Entry<Long, OrganizationUnitSettings> regionSetting:regionSettings.entrySet()){
			try{
				String image = loadImages(regionSetting.getValue());
				profileManagementService.updateProfileImage(MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSetting.getValue(), image);
			} catch (Exception e) {
				LOG.error("Exception caught in ImageLoader while copying image from linkedin to SocialSurvey server. "
						+ "Nested exception is ", e);
				continue;
			}
		}
		
		// Process all the branch profile images.
		for(Entry<Long, OrganizationUnitSettings> branchSetting:branchSettings.entrySet()){
			try{
				String image = loadImages(branchSetting.getValue());
				profileManagementService.updateProfileImage(MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSetting.getValue(), image);
			} catch (Exception e) {
				LOG.error("Exception caught in ImageLoader while copying image from linkedin to SocialSurvey server. "
						+ "Nested exception is ", e);
				continue;
			}
		}
		
		// Process all the individual profile images.
		for(Entry<Long, OrganizationUnitSettings> agentSetting:agentSettings.entrySet()){
			try{
				String image = loadImages(agentSetting.getValue());
				profileManagementService.updateProfileImage(MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSetting.getValue(), image);
			} catch (Exception e) {
				LOG.error("Exception caught in ImageLoader while copying image from linkedin to SocialSurvey server. "
						+ "Nested exception is ", e);
				continue;
			}
		}
		LOG.info("Completed ImageUploader");
	}
	
	private String loadImages(OrganizationUnitSettings setting) throws Exception{
		String linkedinImageUrl = setting.getProfileImageUrl();
		String[] imageUrl = linkedinImageUrl.split(CommonConstants.FILE_SEPARATOR);
		String imageName = imageUrl[imageUrl.length-1]+".png";
		String destination = copyImage(linkedinImageUrl, imageName);
		return destination;
	}
	
	private String copyImage(String source, String imageName) throws Exception {
		
		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		// Create a method instance.
		GetMethod method = new GetMethod(source);

		// Provide custom retry handler
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
		String fileName = "";
		try {
			// Execute the method
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				LOG.error("Method failed: " + method.getStatusLine());
			}

			InputStream photoStream = method.getResponseBodyAsStream();

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			RandomAccessFile file = new RandomAccessFile(CommonConstants.TEMP_FOLDER + CommonConstants.FILE_SEPARATOR + imageName, "rw");

			byte[] buffer = new byte[4096];
			for (int read = 0; (read = photoStream.read(buffer)) != -1; out.write(buffer, 0, read)) {}
			file.write(out.toByteArray());
			file.close();

			photoStream.close();
			method.releaseConnection();
			fileName = fileUploadService.fileUploadHandler(new File(CommonConstants.TEMP_FOLDER, imageName), imageName);
			FileUtils.deleteQuietly(new File(CommonConstants.TEMP_FOLDER + CommonConstants.FILE_SEPARATOR + imageName));
			LOG.info("Successfully retrieved photo of contact");
		}
		catch (Exception e) {
			LOG.error(e.getMessage() + ": " + source);
			throw e;
		}

		return cdnUrl + CommonConstants.FILE_SEPARATOR + amazonImageBucket + CommonConstants.FILE_SEPARATOR + fileName;
	}

	private void initializeDependencies(JobDataMap jobMap) {
		organizationManagementService = (OrganizationManagementService) jobMap.get("organizationManagementService");
		fileUploadService = (FileUploadService) jobMap.get("fileUploadService");
		profileManagementService = (ProfileManagementService) jobMap.get("profileManagementService");
		
		amazonImageBucket = (String) jobMap.get("amazonImageBucket");
        cdnUrl = (String) jobMap.get("cdnUrl");
	}
}