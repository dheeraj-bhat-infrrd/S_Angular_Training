package com.realtech.socialsurvey.core.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SolrFullImport {

	public static final Logger LOG = LoggerFactory.getLogger(SolrFullImport.class);

	public static void main(String[] args) {
		LOG.info("Starting up the SolrFullImport");
		ApplicationContext context = new ClassPathXmlApplicationContext("ss-starter-config.xml");

		// Regions full import
		Runnable regionsFullImport = (Runnable) context.getBean("regionsFullImport");
		Thread regionImportThread = new Thread(regionsFullImport);
		regionImportThread.start();

		// Branches full import
		Runnable branchesFullImport = (Runnable) context.getBean("branchesFullImport");
		Thread branchImportThread = new Thread(branchesFullImport);
		branchImportThread.start();

		// Users full import
		Runnable usersFullImport = (Runnable) context.getBean("usersFullImport");
		Thread userImportThread = new Thread(usersFullImport);
		userImportThread.start();

		LOG.info("Started the SolrFullImport");
		try {
			regionImportThread.join();
			branchImportThread.join();
			userImportThread.join();
		}
		catch (InterruptedException e) {
			LOG.error("Exception while joining th import threads. ", e);
		}

		// Closing the context
		LOG.info("Finished the SolrFullImport");
		((ConfigurableApplicationContext) context).close();
	}
}