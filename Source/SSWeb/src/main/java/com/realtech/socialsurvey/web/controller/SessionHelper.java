package com.realtech.socialsurvey.web.controller;

import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.EmailTemplateConstants;
import com.realtech.socialsurvey.core.commons.UserProfileComparator;
import com.realtech.socialsurvey.core.entities.FileContentReplacements;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.utils.FileOperations;
import com.realtech.socialsurvey.core.utils.PropertyFileReader;

/**
 * Manipulates the values in session
 *
 */
@Component
public class SessionHelper {

	private static final Logger LOG = LoggerFactory.getLogger(SessionHelper.class);
	
	@Autowired
	private FileOperations fileOperations;
	@Autowired
	private UserManagementService userManagementService;
	@Autowired
	private PropertyFileReader propertyFileReader;

	public void getCanonicalSettings(HttpSession session) throws InvalidInputException{
		LOG.info("Getting canonical settings");
		User user = (User)session.getAttribute(CommonConstants.USER_IN_SESSION);
		AccountType accountType = (AccountType)session.getAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION);
		LOG.info("Getting settings for "+user.toString()+" for account type "+accountType);
		UserSettings userSettings = userManagementService.getCanonicalUserSettings(user, accountType);
		session.setAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION, userSettings);
		LOG.info("Set the settings in session");
	}
	
	public void setSettingVariablesInSession(HttpSession session) {
		LOG.info("Settings related session values being set.");
		UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
		if (session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION) != null) {
			// setting the logo name
			setLogo(session, userSettings);
			// check for the mail content
			setMailContent(session, userSettings);
			// set the highest role from the user's profiles
			setHighestRole(session, (User)session.getAttribute(CommonConstants.USER_IN_SESSION));
		}
	}
	
	private void setLogo(HttpSession session, UserSettings userSettings){
		LOG.debug("Setting logo name in the session");
		// check if company has a logo
		if (userSettings.getCompanySettings().getLogo() != null) {
			LOG.debug("Settings logo image from company settings");
			
			String endpoint = propertyFileReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE, CommonConstants.AMAZON_ENDPOINT);
			String bucket = propertyFileReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE, CommonConstants.AMAZON_BUCKET);
			String logoUrl = endpoint + "/" + bucket + "/" + userSettings.getCompanySettings().getLogo();
			
			session.setAttribute(CommonConstants.LOGO_DISPLAY_IN_SESSION, logoUrl);
		}
		else {
			LOG.debug("Could not find logo settings in company. Checking in lower heirarchy.");
			// TODO: Check the lower level hierarchy for logo
		}
	}
	
	private void setMailContent(HttpSession session, UserSettings userSettings){
		LOG.debug("Setting mail content in the session");
		String body = null;
		FileContentReplacements replacements = new FileContentReplacements();
		replacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_PARTICIPATION_MAIL_BODY);
		if (userSettings.getCompanySettings().getMail_content() == null) {
			LOG.debug("Setting default survey participation mail body.");
			// set the mail contents
			try {
				body = fileOperations.replaceFileContents(replacements);
				session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_MAIL_BODY_IN_SESSION, body);
				session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_REMINDER_MAIL_BODY_IN_SESSION, body);
			}
			catch (InvalidInputException e) {
				LOG.warn("Could not set mail content for survey participation");
			}
		}
		else {
			LOG.debug("Company already has mail body settings. Hence, setting the same");
			if (userSettings.getCompanySettings().getMail_content().getTake_survey_mail() != null) {
				session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_MAIL_BODY_IN_SESSION, userSettings.getCompanySettings()
						.getMail_content().getTake_survey_mail().getMail_body());
			}
			else {
				try {
					body = fileOperations.replaceFileContents(replacements);
					session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_MAIL_BODY_IN_SESSION, body);
				}
				catch (InvalidInputException e) {
					LOG.warn("Could not set mail content for survey participation");
				}
			}
			if (userSettings.getCompanySettings().getMail_content().getTake_survey_reminder_mail() != null) {
				session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_REMINDER_MAIL_BODY_IN_SESSION, userSettings.getCompanySettings()
						.getMail_content().getTake_survey_reminder_mail().getMail_body());
			}
			else {
				try {
					body = fileOperations.replaceFileContents(replacements);
					session.setAttribute(CommonConstants.SURVEY_PARTICIPATION_REMINDER_MAIL_BODY_IN_SESSION, body);
				}
				catch (InvalidInputException e) {
					LOG.warn("Could not set mail content for survey participation reminder");
				}
			}
		}
	}
	
	private void setHighestRole(HttpSession session, User user){
		LOG.debug("Checking the highest role");
		List<UserProfile> userProfiles = user.getUserProfiles();
		if(userProfiles != null){
			// sort the user profiles
			Collections.sort(userProfiles, new UserProfileComparator());
			// get the first one. that one will be the highest
			session.setAttribute(CommonConstants.HIGHEST_ROLE_ID_IN_SESSION, userProfiles.get(0).getProfilesMaster().getProfileId());
		}
	}
}
