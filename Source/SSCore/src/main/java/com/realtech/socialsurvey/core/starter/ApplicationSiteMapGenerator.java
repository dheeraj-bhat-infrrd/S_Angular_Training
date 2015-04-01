package com.realtech.socialsurvey.core.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
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
	}
	

}
