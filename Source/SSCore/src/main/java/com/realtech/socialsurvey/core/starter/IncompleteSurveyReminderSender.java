package com.realtech.socialsurvey.core.starter;

import java.util.ArrayList;
import java.util.List;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;

public class IncompleteSurveyReminderSender extends QuartzJobBean {

	public static final Logger LOG = LoggerFactory.getLogger(IncompleteSurveyReminderSender.class);

	private SurveyHandler surveyHandler;

	private EmailServices emailServices;

	private UserManagementService userManagementService;

	private OrganizationManagementService organizationManagementService;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) {
		LOG.info("Executing IncompleteSurveyReminderSender");
		// initialize the dependencies
		initializeDependencies(jobExecutionContext.getMergedJobDataMap());
		for (Company company : organizationManagementService.getAllCompanies()) {
			List<SurveyDetails> incompleteSurveyCustomers = surveyHandler.getIncompleteSurveyCustomersEmail(company.getCompanyId());
			List<Long> agents = new ArrayList<>();
			List<String> customers = new ArrayList<>();
			for (SurveyDetails survey : incompleteSurveyCustomers) {
				try {
					sendEmail(emailServices, organizationManagementService, userManagementService, survey, company.getCompanyId());
				}
				catch (InvalidInputException e) {
					e.printStackTrace();
				}
				agents.add(survey.getAgentId());
				customers.add(survey.getCustomerEmail());
			}
			surveyHandler.updateReminderCount(agents, customers);
		}
		LOG.info("Completed IncompleteSurveyReminderSender");
	}

	private void initializeDependencies(JobDataMap jobMap) {
		surveyHandler = (SurveyHandler) jobMap.get("surveyHandler");
		emailServices = (EmailServices) jobMap.get("emailServices");
		userManagementService = (UserManagementService) jobMap.get("userManagementService");
		organizationManagementService = (OrganizationManagementService) jobMap.get("organizationManagementService");

	}

	private static void sendEmail(EmailServices emailServices, OrganizationManagementService organizationManagementService,
			UserManagementService userManagementService, SurveyDetails survey, long companyId) throws InvalidInputException {
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
			mailBody = mailBody.replaceAll("\\[Name\\]", survey.getCustomerFirstName() + " " + survey.getCustomerLastName());
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
			AgentSettings agentSettings = userManagementService.getUserSettings(survey.getAgentId());
			String agentTitle = "";
			if (agentSettings.getContact_details() != null && agentSettings.getContact_details().getTitle() != null) {
				agentTitle = agentSettings.getContact_details().getTitle();
			}

			String agentPhone = "";
			if (agentSettings.getContact_details() != null && agentSettings.getContact_details().getContact_numbers() != null
					&& agentSettings.getContact_details().getContact_numbers().getWork() != null) {
				agentPhone = agentSettings.getContact_details().getContact_numbers().getWork();
			}

			User user = userManagementService.getUserByUserId(survey.getAgentId());
			String companyName = user.getCompany().getCompany();

			try {
				emailServices.sendDefaultSurveyReminderMail(survey.getCustomerEmail(),
						survey.getCustomerFirstName() + " " + survey.getCustomerLastName(), survey.getAgentName(), survey.getUrl(), agentPhone,
						agentTitle, companyName);
			}
			catch (InvalidInputException | UndeliveredEmailException e) {
				LOG.error(
						"Exception caught in IncompleteSurveyReminderSender.main while trying to send reminder mail to "
								+ survey.getCustomerFirstName() + " for completion of survey. Nested exception is ", e);
			}
		}
	}
}