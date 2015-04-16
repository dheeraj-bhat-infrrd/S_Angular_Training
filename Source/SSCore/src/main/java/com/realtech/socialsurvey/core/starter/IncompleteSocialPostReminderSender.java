package com.realtech.socialsurvey.core.starter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;

public class IncompleteSocialPostReminderSender {

	public static final Logger LOG = LoggerFactory.getLogger(IncompleteSocialPostReminderSender.class);

	private static List<String> socialSites = new ArrayList<>();
	
	public static void main(String[] args) {
		@SuppressWarnings("resource") ApplicationContext context = new ClassPathXmlApplicationContext("ss-starter-config.xml");
		SurveyHandler surveyHandler = (SurveyHandler) context.getBean("surveyHandler");
		EmailServices emailServices = (EmailServices) context.getBean("emailServices");
		populateSocialSites();
		OrganizationManagementService organizationManagementService = (OrganizationManagementService) context
				.getBean("organizationManagementService");
		for (Company company : organizationManagementService.getAllCompanies()) {
			List<SurveyDetails> incompleteSocialPostCustomers = surveyHandler.getIncompleteSocialPostCustomersEmail(company.getCompanyId());
			for (SurveyDetails survey : incompleteSocialPostCustomers) {
				getRemainingSites(new HashSet<String>(survey.getSharedOn()));
				// Send email to complete social post for survey to each customer.
				try {
					emailServices.sendSocialPostReminderMail(survey.getCustomerEmail(),
							survey.getCustomerFirstName() + " " + survey.getCustomerLastName(), survey.getAgentName());
				}
				catch (InvalidInputException | UndeliveredEmailException e) {
					LOG.error(
							"Exception caught in IncompleteSurveyReminderSender.main while trying to send reminder mail to "
									+ survey.getCustomerFirstName() + " for completion of survey. Nested exception is ", e);
				}
			}
		}
	}
	
	private static void populateSocialSites(){
		socialSites.add("facebook");
		socialSites.add("twitter");
		socialSites.add("yelp");
		socialSites.add("google");
		socialSites.add("linkedin");
	}

	private static List<String> getRemainingSites(Set<String> sharedOn) {
		List<String> allElems = new ArrayList<String>(socialSites);
		allElems.removeAll(sharedOn);
		return allElems;
	}

}
