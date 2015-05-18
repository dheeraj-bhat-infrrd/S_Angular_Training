package com.realtech.socialsurvey.core.starter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Starts the batch quartz
 *
 */
public class BatchStarter {

	public static void main(String[] args){
		@SuppressWarnings("unused") ApplicationContext context = new ClassPathXmlApplicationContext("ss-starter-config.xml");
	}
}
