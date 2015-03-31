package com.realtech.socialsurvey.core.starter;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;

public class IncompleteSurveyReminderSender {

	public static final Logger LOG = LoggerFactory.getLogger(IncompleteSurveyReminderSender.class);

	public static void main(String[] args) {
		@SuppressWarnings("resource") ApplicationContext context = new ClassPathXmlApplicationContext("ss-starter-config.xml");
		SurveyHandler surveyHandler = (SurveyHandler) context.getBean("surveyHandler");
		EmailServices emailServices = (EmailServices) context.getBean("emailServices");
		long companyId = 1;
		List<SurveyDetails> incompleteSurveyCustomers = surveyHandler.getIncompleteSurveyCustomersEmail(companyId);
		List<Long> agents = new ArrayList<>();
		List<String> customers = new ArrayList<>();
		for (SurveyDetails survey : incompleteSurveyCustomers) {
			// Send email to complete survey to each customer.
			try {
				emailServices.sendSurveyReminderMail(survey.getCustomerEmail(), survey.getCustomerFirstName() + " " + survey.getCustomerLastName(),
						survey.getAgentName());
				agents.add(survey.getAgentId());
				customers.add(survey.getCustomerEmail());
			}
			catch (InvalidInputException | UndeliveredEmailException e) {
				LOG.error(
						"Exception caught in IncompleteSurveyReminderSender.main while trying to send reminder mail to "
								+ survey.getCustomerFirstName() + " for completion of survey. Nested exception is ", e);
			}
		}
		surveyHandler.updateReminderCount(agents, customers);
	}

}
