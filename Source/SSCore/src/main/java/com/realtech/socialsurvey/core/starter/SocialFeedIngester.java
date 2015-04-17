package com.realtech.socialsurvey.core.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.realtech.socialsurvey.core.feed.impl.SocialFeedIngestionKickStarter;

/**
 * Ingester for social feed
 */
public class SocialFeedIngester {

	private static final Logger LOG = LoggerFactory.getLogger(SocialFeedIngester.class);

	public static void main(String[] args) {
		LOG.info("Starting ingesting social feed");
		
		LOG.debug("Loading the application context");
		ApplicationContext context = new ClassPathXmlApplicationContext("ss-starter-config.xml");
		SocialFeedIngestionKickStarter ingestor = context.getBean(SocialFeedIngestionKickStarter.class);
		
		ingestor.setContext(context);
		ingestor.startFeedIngestion();
	}
}