package com.realtech.socialsurvey.core.starter;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;

public class IncompleteSurveyReminderSender {

	public static final Logger LOG = LoggerFactory.getLogger(IncompleteSurveyReminderSender.class);

	public static void main(String[] args) {
		@SuppressWarnings("resource") ApplicationContext context = new ClassPathXmlApplicationContext("ss-starter-config.xml");
		SurveyHandler surveyHandler = (SurveyHandler) context.getBean("surveyHandler");
		EmailServices emailServices = (EmailServices) context.getBean("emailServices");
		OrganizationManagementService organizationManagementService = (OrganizationManagementService) context
				.getBean("organizationManagementService");
		for (Company company : organizationManagementService.getAllCompanies()) {
			List<SurveyDetails> incompleteSurveyCustomers = surveyHandler.getIncompleteSurveyCustomersEmail(company.getCompanyId());
			List<Long> agents = new ArrayList<>();
			List<String> customers = new ArrayList<>();
			for (SurveyDetails survey : incompleteSurveyCustomers) {
				sendEmail(emailServices, organizationManagementService, survey, company.getCompanyId());
				agents.add(survey.getAgentId());
				customers.add(survey.getCustomerEmail());
			}
			surveyHandler.updateReminderCount(agents, customers);
		}
	}

	private static void sendEmail(EmailServices emailServices, OrganizationManagementService organizationManagementService, SurveyDetails survey,
			long companyId) {
		// Send email to complete survey to each customer.
		OrganizationUnitSettings companySettings = null;
		try {
			companySettings = organizationManagementService.getCompanySettings(companyId);
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException occured while trying to fetch company settings.");
		}

		// Null check
		if (companySettings != null && companySettings.getMail_content() != null
				&& companySettings.getMail_content().getTake_survey_reminder_mail() != null) {
			String mailBody = companySettings.getMail_content().getTake_survey_reminder_mail().getMail_body();
			mailBody = mailBody.replaceAll("\\[AgentName\\]", survey.getAgentName());
			mailBody = mailBody.replaceAll("\\[Name\\]", survey.getCustomerFirstName()+" "+survey.getCustomerLastName());
			mailBody = mailBody.replaceAll("\\[Link\\]", survey.getUrl());
			String mailSubject = CommonConstants.REMINDER_MAIL_SUBJECT;
			try {
				emailServices.sendSurveyReminderMail(survey.getCustomerEmail(), mailSubject, mailBody);
			}
			catch (InvalidInputException | UndeliveredEmailException e) {
				LOG.error("Exception caught while sending mail to " + survey.getCustomerEmail() + " .NEsted exception is ", e);
			}
		}
		else {
			try {
				emailServices.sendDefaultSurveyReminderMail(survey.getCustomerEmail(),
						survey.getCustomerFirstName() + " " + survey.getCustomerLastName(), survey.getAgentName(), survey.getUrl());
			}
			catch (InvalidInputException | UndeliveredEmailException e) {
				LOG.error(
						"Exception caught in IncompleteSurveyReminderSender.main while trying to send reminder mail to "
								+ survey.getCustomerFirstName() + " for completion of survey. Nested exception is ", e);
			}
		}
	}

}
