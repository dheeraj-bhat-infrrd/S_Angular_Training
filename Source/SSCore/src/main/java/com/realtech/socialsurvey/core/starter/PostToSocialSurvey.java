package com.realtech.socialsurvey.core.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PostToSocialSurvey {

	public static final Logger LOG = LoggerFactory.getLogger(PostToSocialSurvey.class);

	public static void main(String[] args) {
		LOG.info("Starting up the PostToSocialSurvey");
		ApplicationContext context = new ClassPathXmlApplicationContext("ss-starter-config.xml");

		Runnable postToWall = (Runnable) context.getBean("postToWall");
		Thread postToWallThread = new Thread(postToWall);
		postToWallThread.start();

		try {
			postToWallThread.join();
		}
		catch (InterruptedException e) {
			LOG.error("Exception while joining th import threads. ", e);
		}

		// Closing the context
		LOG.info("Finished the PostToSocialSurvey");
		((ConfigurableApplicationContext) context).close();
	}
}