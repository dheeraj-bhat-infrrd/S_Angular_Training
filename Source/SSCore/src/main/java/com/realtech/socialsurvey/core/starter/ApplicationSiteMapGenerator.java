package com.realtech.socialsurvey.core.starter;

import java.io.File;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.utils.sitemap.SiteMapGenerator;

/**
 * Started app to generate sitemap for the application
 *
 */
@Component("appsitemapgenerator")
public class ApplicationSiteMapGenerator extends QuartzJobBean{
	
	public static final Logger LOG = LoggerFactory.getLogger(ApplicationSiteMapGenerator.class);
	
	private SiteMapGenerator companySiteMapGenerator;
	private SiteMapGenerator regionSiteMapGenerator;
	private SiteMapGenerator branchSiteMapGenerator;
	private SiteMapGenerator agentSiteMapGenerator;
	private FileUploadService uploadService;
	
	private String envPrefix;
	
	private String companySiteMapPath;
	private String regionSiteMapPath;
	private String branchSiteMapPath;
	private String individualSiteMapPath;
	
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) {
		LOG.info("Starting up the ApplicationSiteMapGenerator.");
		initializeDependencies(jobExecutionContext.getMergedJobDataMap());
		companySiteMapGenerator.setInterval(SiteMapGenerator.DAILY_CONTENT);
		companySiteMapGenerator.setOrganizationUnit(SiteMapGenerator.ORG_COMPANY);
		Thread companySiteMapGeneratorThread = new Thread(companySiteMapGenerator);
		companySiteMapGeneratorThread.start();
		regionSiteMapGenerator.setInterval(SiteMapGenerator.DAILY_CONTENT);
		regionSiteMapGenerator.setOrganizationUnit(SiteMapGenerator.ORG_REGION);
		Thread regionSiteMapGeneratorThread = new Thread(regionSiteMapGenerator);
		regionSiteMapGeneratorThread.start();
		branchSiteMapGenerator.setInterval(SiteMapGenerator.DAILY_CONTENT);
		branchSiteMapGenerator.setOrganizationUnit(SiteMapGenerator.ORG_BRANCH);
		Thread branchSiteMapGeneratorThread = new Thread(branchSiteMapGenerator);
		branchSiteMapGeneratorThread.start();
		agentSiteMapGenerator.setInterval(SiteMapGenerator.DAILY_CONTENT);
		agentSiteMapGenerator.setOrganizationUnit(SiteMapGenerator.ORG_INDIVIDUAL);
		Thread agentSiteMapGeneratorThread = new Thread(agentSiteMapGenerator);
		agentSiteMapGeneratorThread.start();
		try {
			companySiteMapGeneratorThread.join();
			regionSiteMapGeneratorThread.join();
			branchSiteMapGeneratorThread.join();
			agentSiteMapGeneratorThread.join();
		}
		catch (InterruptedException e) {
			LOG.error("Exception while joining to sitemap threads. ", e);
		}
		LOG.info("Done creating sitemaps. Now dumping the sitemaps");
		// upload company sitemap
		try {
			uploadFile(companySiteMapPath, uploadService, envPrefix);
		}catch (NonFatalException e) {
			LOG.error("Could not upload company sitemap to amazon", e);
		}
			try {
				uploadFile(regionSiteMapPath, uploadService, envPrefix);
			}
			catch (NonFatalException e) {
				LOG.error("Could not upload region sitemap to amazon", e);
			}
			try {
				uploadFile(branchSiteMapPath, uploadService, envPrefix);
			}
			catch (NonFatalException e) {
				LOG.error("Could not upload office sitemap to amazon", e);
			}
		try{
			uploadFile(individualSiteMapPath, uploadService, envPrefix);
		}
		catch (NonFatalException e) {
			LOG.error("Could not upload individual sitemap to amazon", e);
		}
		
	}
	
	private void initializeDependencies(JobDataMap jobMap) {
		companySiteMapGenerator = (SiteMapGenerator) jobMap.get("companySiteMapGenerator");
		regionSiteMapGenerator = (SiteMapGenerator) jobMap.get("regionSiteMapGenerator");
		branchSiteMapGenerator = (SiteMapGenerator) jobMap.get("branchSiteMapGenerator");
		agentSiteMapGenerator = (SiteMapGenerator) jobMap.get("agentSiteMapGenerator");
		uploadService = (FileUploadService) jobMap.get("uploadService");
		envPrefix = (String) jobMap.get("envPrefix");
		companySiteMapPath = (String) jobMap.get("companySiteMapPath");
		regionSiteMapPath = (String) jobMap.get("regionSiteMapPath");
		branchSiteMapPath = (String) jobMap.get("branchSiteMapPath");
		individualSiteMapPath = (String) jobMap.get("individualSiteMapPath");
	}
	
	public void uploadFile(String filePath, FileUploadService uploadService, String envPrefix) throws NonFatalException{
		LOG.info("Uploading "+filePath+" to Amazon");
		uploadService.uploadFileAtDefautBucket(new File(filePath), envPrefix+ CommonConstants.FILE_SEPARATOR +filePath.substring(filePath.lastIndexOf(CommonConstants.FILE_SEPARATOR)+1));
		
	}

}
