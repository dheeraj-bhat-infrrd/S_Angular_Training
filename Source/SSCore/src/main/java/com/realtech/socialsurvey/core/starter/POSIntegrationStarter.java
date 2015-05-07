package com.realtech.socialsurvey.core.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.integration.pos.EncompassDataHandler;

/**
 * Starter class for POS Integration
 *
 */
public class POSIntegrationStarter {

	public static final Logger LOG = LoggerFactory.getLogger(POSIntegrationStarter.class);
	
	public static void main(String[] args) {
		LOG.debug("Loading the application context");
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("ss-starter-config.xml");
		// start the ecnompass intergration
		EncompassDataHandler encompassHandler = context.getBean(EncompassDataHandler.class);
		try {
			encompassHandler.fetchClosedEngagments();
		}
		catch (InvalidInputException e) {
			LOG.error("Exception while fetching closed engagements", e);
		}
	}

}
