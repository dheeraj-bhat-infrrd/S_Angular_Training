package com.realtech.socialsurvey.core.starter;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.realtech.socialsurvey.core.utils.mongo.PostToWall;

public class PostToSocialSurvey {

	public static final Logger LOG = LoggerFactory.getLogger(PostToSocialSurvey.class);

	public static void main(String[] args) {
		LOG.info("Starting up the PostToSocialSurvey");

		// TODO parse emailIds
		List<String> emailIds = new ArrayList<String>();
		emailIds.add("ss_agent1@mailinator.com");

		ApplicationContext context = new ClassPathXmlApplicationContext("ss-starter-config.xml");
		PostToWall postToWall = (PostToWall) context.getBean("postToWall");
		postToWall.postStatusForUser(emailIds);

		// Closing the context
		LOG.info("Finished the PostToSocialSurvey");
		((ConfigurableApplicationContext) context).close();
	}
}