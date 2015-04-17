package com.realtech.socialsurvey.core.starter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;

public class IncompleteSocialPostReminderSender {

	@Autowired
	private URLGenerator urlGenerator;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private SurveyHandler surveyHandler;

	public static final Logger LOG = LoggerFactory.getLogger(IncompleteSocialPostReminderSender.class);

	private static List<String> socialSites = new ArrayList<>();

	public static void main(String[] args) {
		@SuppressWarnings("resource") ApplicationContext context = new ClassPathXmlApplicationContext("ss-starter-config.xml");
		SurveyHandler surveyHandler = (SurveyHandler) context.getBean("surveyHandler");
		EmailServices emailServices = (EmailServices) context.getBean("emailServices");
		populateSocialSites();
		IncompleteSocialPostReminderSender sender = new IncompleteSocialPostReminderSender();
		OrganizationManagementService organizationManagementService = (OrganizationManagementService) context
				.getBean("organizationManagementService");
		StringBuilder links = new StringBuilder();
		for (Company company : organizationManagementService.getAllCompanies()) {
			List<SurveyDetails> incompleteSocialPostCustomers = surveyHandler.getIncompleteSocialPostCustomersEmail(company.getCompanyId());
			for (SurveyDetails survey : incompleteSocialPostCustomers) {
				for (String site : getRemainingSites(new HashSet<String>(survey.getSharedOn()))) {
					try {
						links.append("\nFor ").append(site).append(" : ").append(sender.generateQueryParams(survey, site));
					}
					catch (InvalidInputException e) {
						LOG.error("InvalidInputException occured while generating URL for " + site + ". Nested exception is ", e);
					}
				}
				// Send email to complete social post for survey to each customer.
				try {
					emailServices.sendSocialPostReminderMail(survey.getCustomerEmail(),
							survey.getCustomerFirstName() + " " + survey.getCustomerLastName(), survey.getAgentName(), links.toString());
				}
				catch (InvalidInputException | UndeliveredEmailException e) {
					LOG.error(
							"Exception caught in IncompleteSurveyReminderSender.main while trying to send reminder mail to "
									+ survey.getCustomerFirstName() + " for completion of survey. Nested exception is ", e);
				}
			}
		}
	}

	private static void populateSocialSites() {
		socialSites.add("facebook");
		socialSites.add("twitter");
		socialSites.add("yelp");
		socialSites.add("google");
		socialSites.add("linkedin");
	}

	private String generateQueryParams(SurveyDetails survey, String socialSite) throws InvalidInputException {
		LOG.debug("Method to generate URL parameters for Facebook, generateUrlParamsForFacebook() started.");
		Map<String, String> params = new HashMap<>();
		String subUrl = "";
		switch (socialSite) {
			case "facebook":
				;
				subUrl = "posttofacebook";
				break;
			case "twitter":
				subUrl = "posttotwitter";
				break;
			case "linkedin":
				subUrl = "posttolinkedin";
				break;
			case "yelp":
				subUrl = "getyelplinkrest";
				break;
			case "google":
				subUrl = "getgooglepluslinkrest";
				break;
		}

		AgentSettings agentSettings = userManagementService.getUserSettings(survey.getAgentId());
		params.put("agentName", survey.getAgentName());
		params.put("agentProfileLink", surveyHandler.getApplicationBaseUrl() + "rest/profile/" + agentSettings.getProfileUrl());
		params.put("firstName", survey.getCustomerFirstName());
		params.put("lastName", survey.getCustomerLastName());
		params.put("agentId", survey.getAgentId() + "");
		params.put("rating", survey.getScore() + "");
		params.put("customerEmail", survey.getCustomerEmail());
		params.put("feedback", survey.getReview());
		LOG.debug("Method to generate URL parameters for Facebook, generateUrlParamsForFacebook() finished.");
		return urlGenerator.generateUrl(params, surveyHandler.getApplicationBaseUrl() + subUrl);
	}

	private static List<String> getRemainingSites(Set<String> sharedOn) {
		List<String> allElems = new ArrayList<String>(socialSites);
		allElems.removeAll(sharedOn);
		return allElems;
	}

}
