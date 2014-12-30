package com.realtech.socialsurvey.web.controller;

import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.EmailTemplateConstants;
import com.realtech.socialsurvey.core.entities.FileContentReplacements;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.utils.FileOperations;

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
		if (session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION) != null) {
			// setting the logo name
			UserSettings userSettings = (UserSettings) session.getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
			// check if company has a logo
			if (userSettings.getCompanySettings().getLogo() != null) {
				LOG.debug("Settings logo image from company settings");
				session.setAttribute(CommonConstants.LOGO_DISPLAY_IN_SESSION, userSettings.getCompanySettings().getLogo());
			}
			else {
				LOG.debug("Could not find logo settings in company. Checking in lower heirarchy.");
				// TODO: Check the lower level hierarchy for logo
			}
			// check for the mail content
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
	}
}
