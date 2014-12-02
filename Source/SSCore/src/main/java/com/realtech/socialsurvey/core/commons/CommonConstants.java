package com.realtech.socialsurvey.core.commons;

/**
 * Holds application level constants
 */
public interface CommonConstants {

	// default company id for application. if any entity is linked to this id, then its an orphan
	// entity
	public static final int DEFAULT_COMPANY_ID = 1;
	// no profile constant. this is linked to a user profile in case no profile has been attached to
	// it
	public static final int PROFILES_MASTER_NO_PROFILE_ID = 1;
	public static final int PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID = 2;
	public static final int PROFILES_MASTER_REGION_ADMIN_PROFILE_ID = 3;
	public static final int PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID = 4;
	public static final int PROFILES_MASTER_AGENT_PROFILE_ID = 5;
	public static final String DEFAULT_BRANCH_NAME = "Defult Branch";
	public static final String DEFAULT_REGION_NAME = "Defult Region";
	public static final int DEFAULT_REGION_ID = 0;
	public static final int DEFAULT_BRANCH_ID = 0;
	public static final int DEFAULT_AGENT_ID = 0;
	public static final String DEFAULT_SOURCE_APPLICATION = "AP";
	public static final String GUEST_USER_NAME = "GUEST";
	public static final String ADMIN_USER_NAME = "ADMIN";
	public static final Integer STATUS_ACTIVE = 1;
	public static final Integer STATUS_INACTIVE = 0;
	public static final String USER_INVITE_INVITATION_PARAMETERS_COLUMN = "invitationParameters";
	public static final String STATUS_COLUMN = "status";
	public static final String USER_INVITE_INVITATION_VALID_UNTIL = "invitationValidUntil";
	public static final String REQUEST_MAPPING_SHOW_REGISTRATION = "showregistrationpage.do";	
	public static final String CONFIG_PROPERTIES_FILE = "config.properties";
	public static final String SENDGRID_SENDER_USERNAME = "SENDGRID_SENDER_USERNAME";
	public static final String SENDGRID_SENDER_NAME = "SENDGRID_SENDER_NAME";
	public static final String SENDGRID_SENDER_PASSWORD = "SENDGRID_SENDER_PASSWORD";
	public static final String MESSAGE_PROPERTIES_FILE = "displaymessage.properties";
	public static final int IS_DEFAULT_BY_SYSTEM_YES = 1;
	public static final int IS_DEFAULT_BY_SYSTEM_NO = 0;
	// Constants to be used in code for referencing variables(i.e in maps or session attributes)
	public static final String USER_IN_SESSION = "user";
	public static final String COMPANY_NAME = "companyName";
	public static final String ADDRESS = "address";
	public static final String ZIPCODE = "zipCode";
	public static final String COMPANY_CONTACT_NUMBER = "companyContactNo";
	public static final String SURVEY_QUESTION_COLUMN = "surveyQuestion";
}