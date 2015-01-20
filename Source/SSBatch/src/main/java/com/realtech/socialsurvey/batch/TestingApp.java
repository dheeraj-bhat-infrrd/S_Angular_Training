package com.realtech.socialsurvey.batch;

// JIRA: SS-61: By RM03

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestingApp {

	@SuppressWarnings("resource")
	public static void main(String areg[]) throws Exception {

		@SuppressWarnings("unused") ApplicationContext context = new ClassPathXmlApplicationContext("launch-context.xml");

	}

}