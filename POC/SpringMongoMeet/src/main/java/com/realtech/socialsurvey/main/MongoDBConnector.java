package com.realtech.socialsurvey.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.realtech.socialsurvey.repositories.mongo.RepositoryPerson;

public class MongoDBConnector {

	public static void main(String[] args) {
		
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("sscore-beans.xml");
		RepositoryPerson personRepository = context.getBean(RepositoryPerson.class);																				 
		personRepository.log41YearsOldPerson();
	}
}