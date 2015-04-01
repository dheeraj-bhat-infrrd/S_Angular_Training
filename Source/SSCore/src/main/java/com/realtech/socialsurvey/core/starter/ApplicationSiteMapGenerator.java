package com.realtech.socialsurvey.core.starter;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.utils.sitemap.SiteMapGenerator;

/**
 * Started app to generate sitemap for the application
 *
 */
public class ApplicationSiteMapGenerator {
	
	public static final Logger LOG = LoggerFactory.getLogger(ApplicationSiteMapGenerator.class);
	
	public static void main(String[] args){
		LOG.info("Starting up the email consumer.");
		LOG.debug("Loading the application context");
		ApplicationContext context = new ClassPathXmlApplicationContext("ss-starter-config.xml");
		// start threads to get the sitemap entries
		Thread companySiteMapGeneratorThread = new Thread(new SiteMapGenerator(SiteMapGenerator.DAILY_CONTENT, SiteMapGenerator.ORG_COMPANY, context));
		companySiteMapGeneratorThread.run();
		Thread regionSiteMapGeneratorThread = new Thread(new SiteMapGenerator(SiteMapGenerator.DAILY_CONTENT, SiteMapGenerator.ORG_REGION, context));
		regionSiteMapGeneratorThread.run();
		Thread branchSiteMapGeneratorThread = new Thread(new SiteMapGenerator(SiteMapGenerator.DAILY_CONTENT, SiteMapGenerator.ORG_BRANCH, context));
		branchSiteMapGeneratorThread.run();
		Thread agentSiteMapGeneratorThread = new Thread(new SiteMapGenerator(SiteMapGenerator.DAILY_CONTENT, SiteMapGenerator.ORG_INDIVIDUAL, context));
		agentSiteMapGeneratorThread.run();
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
		FileUploadService uploadService = context.getBean(FileUploadService.class);
		String envPrefix = context.getEnvironment().getProperty("AMAZON_ENV_PREFIX");
		try {
			ApplicationSiteMapGenerator.uploadFile(context.getEnvironment().getProperty("COMPANY_SITEMAP_PATH"), uploadService, envPrefix);
			ApplicationSiteMapGenerator.uploadFile(context.getEnvironment().getProperty("REGION_SITEMAP_PATH"), uploadService, envPrefix);
			ApplicationSiteMapGenerator.uploadFile(context.getEnvironment().getProperty("BRANCH_SITEMAP_PATH"), uploadService, envPrefix);
			ApplicationSiteMapGenerator.uploadFile(context.getEnvironment().getProperty("INDIVIDUAL_SITEMAP_PATH"), uploadService, envPrefix);
		}
		catch (NonFatalException e) {
			LOG.error("Could not upload file to amazon", e);
		}
	}
	
	public static void uploadFile(String filePath, FileUploadService uploadService, String envPrefix) throws NonFatalException{
		LOG.info("Uploading "+filePath+" to Amazon");
		uploadService.uploadFile(new File(filePath), envPrefix+File.separator+filePath.substring(filePath.lastIndexOf(File.separator)+1));
		
	}

}
