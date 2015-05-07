package com.realtech.socialsurvey.core.services.surveybuilder.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.MailContent;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;

// JIRA SS-119 by RM-05:BOC
@Component
public class SurveyHandlerImpl implements SurveyHandler, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(SurveyHandlerImpl.class);

	private static String SWEAR_WORDS;

	@Autowired
	private SolrSearchService solrSearchService;

	@Autowired
	private SurveyDetailsDao surveyDetailsDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserProfileDao userProfileDao;

	@Autowired
	private URLGenerator urlGenerator;

	@Autowired
	private EmailFormatHelper emailFormatHelper;

	@Autowired
	private OrganizationUnitSettingsDao organizationUnitSettingsDao;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private OrganizationManagementService organizationManagementService;

	@Autowired
	private EmailServices emailServices;

	@Value("${APPLICATION_BASE_URL}")
	private String applicationBaseUrl;

	@Value("${MOODS_TO_SEND_MAIL}")
	private String moodsToSendMail;

	@Value("${GOOGLE_SHARE_URI}")
	private String googleShareUri;

	/**
	 * Method to store question and answer format into mongo.
	 * 
	 * @param agentId
	 * @throws InvalidInputException
	 * @throws Exception
	 */
	@Override
	@Transactional
	public SurveyDetails storeInitialSurveyDetails(long agentId, String customerEmail, String firstName, String lastName, int reminderCount,
			String custRelationWithAgent, String baseUrl) throws SolrException, NoRecordsFetchedException, InvalidInputException {

		LOG.info("Method to store initial details of survey, storeInitialSurveyAnswers() started.");

		String agentName;
		long branchId = 0;
		long companyId = 0;
		long regionId = 0;

		User user = userDao.findById(User.class, agentId);
		companyId = user.getCompany().getCompanyId();
		agentName = user.getFirstName() + " " + user.getLastName();
		for (UserProfile userProfile : user.getUserProfiles()) {
			if (userProfile.getAgentId() == agentId) {
				branchId = userProfile.getBranchId();
				regionId = userProfile.getRegionId();
			}
		}

		SurveyDetails surveyDetails = new SurveyDetails();
		surveyDetails.setAgentId(agentId);
		surveyDetails.setAgentName(agentName);
		surveyDetails.setBranchId(branchId);
		surveyDetails.setCustomerFirstName(firstName);
		surveyDetails.setCustomerLastName(lastName);
		surveyDetails.setCompanyId(companyId);
		surveyDetails.setCustomerEmail(customerEmail);
		surveyDetails.setRegionId(regionId);
		surveyDetails.setStage(CommonConstants.INITIAL_INDEX);
		surveyDetails.setReminderCount(reminderCount);
		surveyDetails.setModifiedOn(new Date());
		surveyDetails.setSurveyResponse(new ArrayList<SurveyResponse>());
		surveyDetails.setCustRelationWithAgent(custRelationWithAgent);
		surveyDetails.setUrl(getSurveyUrl(agentId, customerEmail, baseUrl));
		surveyDetails.setEditable(true);
		SurveyDetails survey = surveyDetailsDao.getSurveyByAgentIdAndCustomerEmail(agentId, customerEmail);
		LOG.info("Method to store initial details of survey, storeInitialSurveyAnswers() finished.");
		if (survey == null) {
			surveyDetailsDao.insertSurveyDetails(surveyDetails);
			return null;
		}
		else {
			return survey;
		}
	}

	/*
	 * Method to generate survey URL to start a survey directly based upon agentId and customer
	 * email id.
	 */
	@Override
	public String getSurveyUrl(long agentId, String customerEmail, String baseUrl) throws InvalidInputException {
		Map<String, String> urlParam = new HashMap<>();
		urlParam.put(CommonConstants.AGENT_ID_COLUMN, agentId + "");
		urlParam.put(CommonConstants.CUSTOMER_EMAIL_COLUMN, customerEmail);
		return urlGenerator.generateUrl(urlParam, baseUrl);
	}

	/*
	 * Method to update answers to all the questions and current stage in MongoDB.
	 * @param agentId
	 * @param customerEmail
	 * @param question
	 * @param answer
	 * @param stage
	 * @throws Exception
	 */
	@Override
	public void updateCustomerAnswersInSurvey(long agentId, String customerEmail, String question, String questionType, String answer, int stage) {
		LOG.info("Method to update answers provided by customer in SURVEY_DETAILS, updateCustomerAnswersInSurvey() started.");
		SurveyResponse surveyResponse = new SurveyResponse();
		surveyResponse.setAnswer(answer);
		surveyResponse.setQuestion(question);
		surveyResponse.setQuestionType(questionType);
		surveyDetailsDao.updateCustomerResponse(agentId, customerEmail, surveyResponse, stage);
		LOG.info("Method to update answers provided by customer in SURVEY_DETAILS, updateCustomerAnswersInSurvey() finished.");
	}

	/*
	 * Method to update customer review and final score on the basis of rating questions in
	 * SURVEY_DETAILS.
	 */
	@Override
	public void updateGatewayQuestionResponseAndScore(long agentId, String customerEmail, String mood, String review, boolean isAbusive) {
		LOG.info("Method to update customer review and final score on the basis of rating questions in SURVEY_DETAILS, updateCustomerAnswersInSurvey() started.");
		surveyDetailsDao.updateGatewayAnswer(agentId, customerEmail, mood, review, isAbusive);
		surveyDetailsDao.updateFinalScore(agentId, customerEmail);
		LOG.info("Method to update customer review and final score on the basis of rating questions in SURVEY_DETAILS, updateCustomerAnswersInSurvey() finished.");
	}

	public SurveyDetails getSurveyDetails(long agentId, String customerEmail) {
		LOG.info("Method getSurveyDetails() to return survey details by agent id and customer email started.");
		SurveyDetails surveyDetails;
		surveyDetails = surveyDetailsDao.getSurveyByAgentIdAndCustomerEmail(agentId, customerEmail);
		LOG.info("Method getSurveyDetails() to return survey details by agent id and customer email finished.");
		return surveyDetails;
	}

	/*
	 * Method to update a survey as clicked when user triggers the survey and page of the first
	 * question starts loading.
	 */
	public void updateSurveyAsClicked(long agentId, String customerEmail) {
		LOG.info("Method updateSurveyAsClicked() to mark the survey as clicked, started");
		surveyDetailsDao.updateSurveyAsClicked(agentId, customerEmail);
		LOG.info("Method updateSurveyAsClicked() to mark the survey as clicked, finished");
	}

	/*
	 * Method to increase reminder count by 1. This method is called every time a reminder mail is
	 * sent to the customer.
	 */
	@Override
	public void updateReminderCount(long agentId, String customerEmail) {
		LOG.info("Method to increase reminder count by 1, updateReminderCount() started.");
		surveyDetailsDao.updateReminderCount(agentId, customerEmail);
		LOG.info("Method to increase reminder count by 1, updateReminderCount() finished.");
	}

	@Override
	public String getApplicationBaseUrl() {
		return applicationBaseUrl;
	}

	@Override
	public String getSwearWords() {
		return SWEAR_WORDS;
	}

	/*
	 * Loads string of swear words from file containing all the swear words.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Resource resource = new ClassPathResource("swear-words");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));
			String text;
			List<String> swearWordsLst = new ArrayList<>();
			text = br.readLine();
			while (text != null) {
				swearWordsLst.add(text.trim());
				text = br.readLine();
			}
			SWEAR_WORDS = new Gson().toJson(swearWordsLst);
		}
		catch (IOException e) {
			LOG.error("Error parsing list of swear words. Nested exception is ", e);
			throw e;
		}
	}

	/*
	 * Method to get list of all the admins' emailIds, an agent comes under. Later on these emailIds
	 * are used for sending emails in case of any sad review for the agent.
	 */
	@Transactional
	@Override
	public List<String> getEmailIdsOfAdminsInHierarchy(long agentId) throws InvalidInputException {
		List<String> emailIdsOfAdmins = new ArrayList<>();
		List<UserProfile> admins = new ArrayList<>();
		User agent = userDao.findById(User.class, agentId);
		Map<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.USER_COLUMN, agent);
		queries.put(CommonConstants.PROFILE_MASTER_COLUMN,
				userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID));
		List<UserProfile> agentProfiles = userProfileDao.findByKeyValue(UserProfile.class, queries);
		for (UserProfile agentProfile : agentProfiles) {
			queries.clear();
			queries.put(CommonConstants.BRANCH_ID_COLUMN, agentProfile.getBranchId());
			queries.put(CommonConstants.PROFILE_MASTER_COLUMN,
					userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID));
			admins.addAll(userProfileDao.findByKeyValue(UserProfile.class, queries));
			queries.clear();
			queries.put(CommonConstants.REGION_ID_COLUMN, agentProfile.getRegionId());
			queries.put(CommonConstants.PROFILE_MASTER_COLUMN,
					userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID));
			admins.addAll(userProfileDao.findByKeyValue(UserProfile.class, queries));
		}
		for (UserProfile admin : admins) {
			emailIdsOfAdmins.add(admin.getEmailId());
		}
		return emailIdsOfAdmins;
	}

	/*
	 * Method to get list of customers' email ids who have not completed survey yet. It checks if
	 * max number of reminder mails have been sent. It also checks if required number of days have
	 * been passed since the last mail was sent.
	 */
	@Override
	public List<SurveyDetails> getIncompleteSurveyCustomersEmail(long companyId) {
		LOG.info("started.");
		int surveyReminderInterval = 0;
		int maxReminders = 0;
		List<SurveyDetails> incompleteSurveyCustomers = new ArrayList<>();
		OrganizationUnitSettings organizationUnitSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(companyId,
				MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		// Fetching surveyReminderInterval and max number of reminders for a company.
		if (organizationUnitSettings != null) {
			SurveySettings surveySettings = organizationUnitSettings.getSurvey_settings();
			if (surveySettings != null) {
				if (!surveySettings.getIsReminderDisabled() && surveySettings.getSurvey_reminder_interval_in_days() > 0) {
					surveyReminderInterval = surveySettings.getSurvey_reminder_interval_in_days();
					maxReminders = surveySettings.getMax_number_of_survey_reminders();
				}
			}
		}
		incompleteSurveyCustomers = surveyDetailsDao.getIncompleteSurveyCustomers(companyId, surveyReminderInterval, maxReminders);
		LOG.info("finished.");
		return incompleteSurveyCustomers;
	}

	@Override
	public void updateReminderCount(List<Long> agents, List<String> customers) {
		LOG.info("Method to increase reminder count by 1, updateReminderCount() started.");
		surveyDetailsDao.updateReminderCount(agents, customers);
		LOG.info("Method to increase reminder count by 1, updateReminderCount() finished.");
	}

	/*
	 * Method to get surveys
	 */
	@Override
	public List<SurveyDetails> getIncompleteSocialPostSurveys(long companyId) {
		LOG.info("started.");
		int surveyReminderInterval = 0;
		int maxReminders = 0;
		float autopostScore = 0;
		List<SurveyDetails> incompleteSocialPostCustomers = new ArrayList<>();
		OrganizationUnitSettings organizationUnitSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(companyId,
				MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		// Fetching surveyReminderInterval and max number of reminders for a company.
		if (organizationUnitSettings != null) {
			SurveySettings surveySettings = organizationUnitSettings.getSurvey_settings();
			if (surveySettings != null) {
				if (!surveySettings.getIsReminderDisabled() && surveySettings.getSurvey_reminder_interval_in_days() > 0) {
					surveyReminderInterval = surveySettings.getSurvey_reminder_interval_in_days();
					maxReminders = surveySettings.getMax_number_of_survey_reminders();
					autopostScore = surveySettings.getShow_survey_above_score();
				}
			}
		}
		incompleteSocialPostCustomers = surveyDetailsDao.getIncompleteSocialPostCustomersEmail(companyId, surveyReminderInterval, maxReminders,
				autopostScore);
		LOG.info("finished.");
		return incompleteSocialPostCustomers;
	}

	@Override
	public String getMoodsToSendMail() {
		return moodsToSendMail;
	}

	@Override
	public String getGoogleShareUri() {
		return googleShareUri;
	}

	@Override
	public void increaseSurveyCountForAgent(long agentId) throws SolrException {
		LOG.info("Method to increase survey count for agent started.");
		organizationUnitSettingsDao.updateCompletedSurveyCountForAgent(agentId);
		solrSearchService.updateCompletedSurveyCountForUserInSolr(agentId);
		LOG.info("Method to increase survey count for agent finished.");
	}

	@Override
	public void updateSharedOn(List<String> socialSites, long agentId, String customerEmail) {
		LOG.info("Method to update sharedOn in SurveyDetails collection, updateSharedOn() started.");
		surveyDetailsDao.updateSharedOn(socialSites, agentId, customerEmail);
		LOG.info("Method to update sharedOn in SurveyDetails collection, updateSharedOn() finished.");
	}

	@Override
	public void changeStatusOfSurvey(long agentId, String customerEmail, boolean editable) {
		LOG.info("Method to update status of survey in SurveyDetails collection, changeStatusOfSurvey() started.");
		surveyDetailsDao.changeStatusOfSurvey(agentId, customerEmail, editable);
		LOG.info("Method to update status of survey in SurveyDetails collection, changeStatusOfSurvey() finished.");
	}

	/*
	 * Method to store initial details of customer to initiate survey and send a mail with link of
	 * survey.
	 */
	@Override
	@Transactional
	public void sendSurveyInvitationMail(String custFirstName, String custLastName, String custEmail, String custRelationWithAgent, User user,
			boolean isAgent) throws InvalidInputException, SolrException, NoRecordsFetchedException, UndeliveredEmailException {
		LOG.debug("Method sendSurveyInvitationMail() called from DashboardController.");
		if (custFirstName == null || custFirstName.isEmpty()) {
			LOG.error("Null/Empty value found for customer's first name.");
			throw new InvalidInputException("Null/Empty value found for customer's first name.");
		}
		if (custLastName == null || custLastName.isEmpty()) {
			LOG.error("Null/Empty value found for customer's last name.");
			throw new InvalidInputException("Null/Empty value found for customer's last name.");
		}
		if (custEmail == null || custEmail.isEmpty()) {
			LOG.error("Null/Empty value found for customer's email id.");
			throw new InvalidInputException("Null/Empty value found for customer's email id.");
		}

		String link = composeLink(user.getUserId(), custEmail);
		storeInitialSurveyDetails(user.getUserId(), custEmail, custFirstName, custLastName, 0, custRelationWithAgent, link);
		
		if (isAgent)
			sendInvitationMailByAgent(user, custFirstName, custLastName, custEmail, link);
		else
			sendInvitationMailByCustomer(user, custFirstName, custLastName, custEmail, link);
		LOG.debug("Method sendSurveyInvitationMail() finished from DashboardController.");
	}

	/*
	 * Method to compose link for sending to a user to start survey started.
	 */
	private String composeLink(long userId, String custEmail) throws InvalidInputException {
		LOG.debug("Method composeLink() started");
		Map<String, String> urlParams = new HashMap<>();
		urlParams.put(CommonConstants.AGENT_ID_COLUMN, userId + "");
		urlParams.put(CommonConstants.CUSTOMER_EMAIL_COLUMN, custEmail);
		LOG.debug("Method composeLink() finished");
		return urlGenerator.generateUrl(urlParams, getApplicationBaseUrl() + "rest/survey/showsurveypageforurl");
	}

	/*
	 * Method to send email by agent to initiate survey.
	 */
	private void sendInvitationMailByAgent(User user, String custFirstName, String custLastName, String custEmail, String link)
			throws InvalidInputException, UndeliveredEmailException {
		LOG.debug("sendInvitationMailByAgent() started.");

		// fetching params
		AgentSettings agentSettings = userManagementService.getUserSettings(user.getUserId());
		String companyName = user.getCompany().getCompany();
		String agentTitle = "";
		if (agentSettings.getContact_details() != null && agentSettings.getContact_details().getTitle() != null) {
			agentTitle = agentSettings.getContact_details().getTitle();
		}

		String agentPhone = "";
		if (agentSettings.getContact_details() != null && agentSettings.getContact_details().getContact_numbers() != null
				&& agentSettings.getContact_details().getContact_numbers().getWork() != null) {
			agentPhone = agentSettings.getContact_details().getContact_numbers().getWork();
		}

		String agentName = user.getFirstName();
		if (user.getLastName() != null && !user.getLastName().isEmpty()) {
			agentName = user.getFirstName() + " " + user.getLastName();
		}
		String agentSignature = emailFormatHelper.buildAgentSignature(agentPhone, agentTitle, companyName);

		OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings(user.getCompany().getCompanyId());
		if (companySettings != null && companySettings.getMail_content() != null && companySettings.getMail_content().getTake_survey_mail() != null) {

			MailContent takeSurvey = companySettings.getMail_content().getTake_survey_mail();
			String mailBody = emailFormatHelper.replaceEmailBodyWithParams(takeSurvey.getMail_body(), takeSurvey.getParam_order());
			mailBody = mailBody.replaceAll("\\[AgentName\\]", agentName);
			mailBody = mailBody.replaceAll("\\[Name\\]", custFirstName + " " + custLastName);
			mailBody = mailBody.replaceAll("\\[Link\\]", link);
			mailBody = mailBody.replaceAll("\\[AgentSignature\\]", agentSignature);
			mailBody = mailBody.replaceAll("null", "");

			String mailSubject = CommonConstants.SURVEY_MAIL_SUBJECT + agentName;
			try {
				emailServices.sendSurveyInvitationMail(custEmail, mailSubject, mailBody, user.getEmailId(), user.getFirstName()
						+ (user.getLastName() != null ? " " + user.getLastName() : ""));
			}
			catch (InvalidInputException | UndeliveredEmailException e) {
				LOG.error("Exception caught while sending mail to " + custEmail + ". Nested exception is ", e);
			}
		}
		else {
			emailServices.sendDefaultSurveyInvitationMail(custEmail, custFirstName + " " + custLastName, user.getFirstName()
					+ (user.getLastName() != null ? " " + user.getLastName() : ""), link, user.getEmailId(), agentSignature);
		}
		LOG.debug("sendInvitationMailByAgent() finished.");
	}
	
	/*
	 * Method to send email by customer to initiate survey.
	 */
	private void sendInvitationMailByCustomer(User user, String custFirstName, String custLastName, String custEmail, String link)
			throws InvalidInputException, UndeliveredEmailException {
		LOG.debug("sendInvitationMailByCustomer() started.");

		OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings(user.getCompany().getCompanyId());
		if (companySettings != null && companySettings.getMail_content() != null
				&& companySettings.getMail_content().getTake_survey_mail_customer() != null) {

			MailContent takeSurveyCustomer = companySettings.getMail_content().getTake_survey_mail_customer();
			String mailBody = emailFormatHelper.replaceEmailBodyWithParams(takeSurveyCustomer.getMail_body(), takeSurveyCustomer.getParam_order());
			mailBody = mailBody.replaceAll("\\[AgentName\\]", user.getFirstName() + " " + user.getLastName());
			mailBody = mailBody.replaceAll("\\[Name\\]", custFirstName + " " + custLastName);
			mailBody = mailBody.replaceAll("\\[Link\\]", link);
			mailBody = mailBody.replaceAll("null", "");

			String mailSubject = CommonConstants.SURVEY_MAIL_SUBJECT_CUSTOMER;
			try {
				emailServices.sendSurveyInvitationMailByCustomer(custEmail, mailSubject, mailBody, user.getEmailId(),
						user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : ""));
			}
			catch (InvalidInputException | UndeliveredEmailException e) {
				LOG.error("Exception caught while sending mail to " + custEmail + ". Nested exception is ", e);
			}
		}
		else {
			emailServices.sendDefaultSurveyInvitationMailByCustomer(custEmail, custFirstName + " " + custLastName,
					user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : ""), link, user.getEmailId());
		}
		LOG.debug("sendInvitationMailByCustomer() finished.");
	}
}
// JIRA SS-119 by RM-05:EOC