package com.realtech.socialsurvey.core.starter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.MailContent;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;

public class IncompleteSurveyReminderSender extends QuartzJobBean {

	public static final Logger LOG = LoggerFactory.getLogger(IncompleteSurveyReminderSender.class);

	private SurveyHandler surveyHandler;
	private EmailServices emailServices;
	private UserManagementService userManagementService;
	private OrganizationManagementService organizationManagementService;
	private SolrSearchService solrSearchService;
	private EmailFormatHelper emailFormatHelper;
	private String applicationBaseUrl;
	private String applicationLogoUrl;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) {
		LOG.info("Executing IncompleteSurveyReminderSender");

		// initialize the dependencies
		initializeDependencies(jobExecutionContext.getMergedJobDataMap());

		for (Company company : organizationManagementService.getAllCompanies()) {
			Map<String, Integer> reminderMap = surveyHandler.getReminderInformationForCompany(company.getCompanyId());
			int reminderInterval = reminderMap.get(CommonConstants.SURVEY_REMINDER_INTERVAL);
			int reminderCount = reminderMap.get(CommonConstants.SURVEY_REMINDER_COUNT);

			List<SurveyPreInitiation> incompleteSurveyCustomers = surveyHandler.getIncompleteSurveyCustomersEmail(company);
			for (SurveyPreInitiation survey : incompleteSurveyCustomers) {
				if (survey.getReminderCounts() < reminderCount) {
					long surveyLastRemindedTime = survey.getLastReminderTime().getTime();
					long currentTime = System.currentTimeMillis();
					if (surveyHandler.checkIfTimeIntervalHasExpired(surveyLastRemindedTime, currentTime, reminderInterval)) {
						try {
							/*if (survey.getSurveySource().equalsIgnoreCase(CommonConstants.CRM_SOURCE_ENCOMPASS)) {
								sendMailToAgent(survey);
							}*/
							sendEmail(emailServices, organizationManagementService, userManagementService, survey, company.getCompanyId());
							// Change status to Sent Mail
							surveyHandler.markSurveyAsSent(survey);
							surveyHandler.updateReminderCount(survey.getSurveyPreIntitiationId());
						}
						catch (InvalidInputException e) {
							LOG.error(
									"InvalidInputException caught in executeInternal() method of IncompleteSurveyReminderSender. Nested exception is ",
									e);
						}
					}
				}
				else {
					LOG.debug("This survey " + survey.getSurveyPreIntitiationId() + " has exceeded the reminder count ");
				}
			}
		}
		LOG.info("Completed IncompleteSurveyReminderSender");
	}

	private void sendMailToAgent(SurveyPreInitiation survey) {
		try {
			emailServices.sendAgentSurveyReminderMail(survey.getCustomerEmailId(), survey);
		}
		catch (InvalidInputException | UndeliveredEmailException e) {
			LOG.error("Exception caught " + e.getMessage());
		}
	}

	private void initializeDependencies(JobDataMap jobMap) {
		surveyHandler = (SurveyHandler) jobMap.get("surveyHandler");
		emailServices = (EmailServices) jobMap.get("emailServices");
		userManagementService = (UserManagementService) jobMap.get("userManagementService");
		organizationManagementService = (OrganizationManagementService) jobMap.get("organizationManagementService");
		solrSearchService = (SolrSearchService) jobMap.get("solrSearchService");
		emailFormatHelper = (EmailFormatHelper) jobMap.get("emailFormatHelper");
		applicationBaseUrl = (String) jobMap.get("applicationBaseUrl");
		applicationLogoUrl = (String) jobMap.get("applicationLogoUrl");
	}

	private void sendEmail(EmailServices emailServices, OrganizationManagementService organizationManagementService,
			UserManagementService userManagementService, SurveyPreInitiation survey, long companyId) throws InvalidInputException {
		// Send email to complete survey to each customer.
		OrganizationUnitSettings companySettings = null;
		String agentName = "";
		User user = null;

		user = userManagementService.getUserByUserId(survey.getAgentId());

		if (user != null) {
			agentName = user.getFirstName();
		}

		String surveyLink = surveyHandler.composeLink(survey.getAgentId(), survey.getCustomerEmailId(), survey.getCustomerFirstName(),
				survey.getCustomerLastName());
		try {
			companySettings = organizationManagementService.getCompanySettings(companyId);
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException occured while trying to fetch company settings.");
		}

		// Fetching agent settings.
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
		String companyName = user.getCompany().getCompany();
		String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		String fullAddress = "";

		// Null check
		if (companySettings != null && companySettings.getMail_content() != null
				&& companySettings.getMail_content().getTake_survey_reminder_mail() != null) {
			MailContent mailContent = companySettings.getMail_content().getTake_survey_reminder_mail();
			String mailBody = emailFormatHelper.replaceEmailBodyWithParams(mailContent.getMail_body(), mailContent.getParam_order());
			String agentSignature = emailFormatHelper.buildAgentSignature(agentPhone, agentTitle, companyName);

			mailBody = mailBody.replaceAll("\\[LogoUrl\\]", applicationLogoUrl);
			mailBody = mailBody.replaceAll("\\[BaseUrl\\]", applicationBaseUrl);
			mailBody = mailBody.replaceAll("\\[AgentName\\]", agentName);
			mailBody = mailBody.replaceAll("\\[FirstName\\]", survey.getCustomerFirstName());
			mailBody = mailBody.replaceAll("\\[Name\\]", survey.getCustomerFirstName() + " " + survey.getCustomerLastName());
			mailBody = mailBody.replaceAll("\\[Link\\]", surveyLink);
			mailBody = mailBody.replaceAll("\\[AgentSignature\\]", agentSignature);
			mailBody = mailBody.replaceAll("\\[RecipientEmail\\]", survey.getCustomerEmailId());
			mailBody = mailBody.replaceAll("\\[SenderEmail\\]", user.getEmailId());
			mailBody = mailBody.replaceAll("\\[CompanyName\\]", companyName);
			mailBody = mailBody.replaceAll("\\[InitiatedDate\\]", dateFormat.format(new Date()));
			mailBody = mailBody.replaceAll("\\[CurrentYear\\]", currentYear);
			mailBody = mailBody.replaceAll("\\[FullAddress\\]", fullAddress);
			mailBody = mailBody.replaceAll("null", "");
			String mailSubject = CommonConstants.REMINDER_MAIL_SUBJECT + agentName;
			if (mailContent.getMail_subject() != null && !mailContent.getMail_subject().isEmpty()) {
				mailSubject = mailContent.getMail_subject();
				mailSubject = mailSubject.replaceAll("\\[AgentName\\]", agentName);
			}

			try {
				emailServices.sendSurveyReminderMail(survey.getCustomerEmailId(), mailSubject, mailBody);
			}
			catch (InvalidInputException | UndeliveredEmailException e) {
				LOG.error("Exception caught while sending mail to " + survey.getCustomerEmailId() + " .Nested exception is ", e);
			}
		}
		else {
			try {
				emailServices.sendDefaultSurveyReminderMail(survey.getCustomerEmailId(), survey.getCustomerFirstName(), agentName, surveyLink,
						agentPhone, agentTitle, companyName);
			}
			catch (InvalidInputException | UndeliveredEmailException e) {
				LOG.error(
						"Exception caught in IncompleteSurveyReminderSender.main while trying to send reminder mail to "
								+ survey.getCustomerFirstName() + " for completion of survey. Nested exception is ", e);
			}
		}
	}
}