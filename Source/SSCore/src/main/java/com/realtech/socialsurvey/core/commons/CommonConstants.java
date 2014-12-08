package com.realtech.socialsurvey.core.commons;

/**
 * Holds application level constants
 */
public interface CommonConstants {

	/**
	 * Property file constants
	 */
	public static final String CONFIG_PROPERTIES_FILE = "config.properties";
	public static final String MESSAGE_PROPERTIES_FILE = "displaymessage.properties";
	public static final String SENDGRID_SENDER_USERNAME = "SENDGRID_SENDER_USERNAME";
	public static final String SENDGRID_SENDER_NAME = "SENDGRID_SENDER_NAME";
	public static final String SENDGRID_SENDER_PASSWORD = "SENDGRID_SENDER_PASSWORD";

	
	/**
	 * Default constants
	 */
	// default company id for application. if any entity is linked to this id, then its an orphan entity
	public static final long DEFAULT_COMPANY_ID = 1;
	public static final String DEFAULT_BRANCH_NAME = "Defult Branch";
	public static final String DEFAULT_REGION_NAME = "Defult Region";
	public static final long DEFAULT_REGION_ID = 0;
	public static final long DEFAULT_BRANCH_ID = 0;
	public static final long DEFAULT_AGENT_ID = 0;
	public static final String DEFAULT_SOURCE_APPLICATION = "AP";

	/**
	 * Profile master constants
	 */
	public static final int PROFILES_MASTER_NO_PROFILE_ID = 1;
	public static final int PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID = 2;
	public static final int PROFILES_MASTER_REGION_ADMIN_PROFILE_ID = 3;
	public static final int PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID = 4;
	public static final int PROFILES_MASTER_AGENT_PROFILE_ID = 5;
	
	/**
	 * Profile completion stages constants and form action constants, store the url mappings
	 */
	public static final String ADD_COMPANY_STAGE = "addcompanyinformation.do";
	public static final String ADD_ACCOUNT_TYPE_STAGE = "addaccounttype.do";
	public static final String PAYMENT_STAGE = "payment.do";
	public static final String RESET_PASSWORD = "resetpassword.do";
	public static final String LOGIN_STAGE = "dashboard.do";
	public static final String PROFILE_STAGES_COMPLETE = "complete";

	/**
	 * Status constants
	 */
	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_INACTIVE = 0;
	public static final int PROCESS_COMPLETE = 1;
	public static final int PROCESS_NOT_STARTED = 0;
	public static final int IS_DEFAULT_BY_SYSTEM_YES = 1;
	public static final int IS_DEFAULT_BY_SYSTEM_NO = 0;

	/**
	 * Hibernate entities and column name constants
	 */
	public static final String USER_INVITE_INVITATION_PARAMETERS_COLUMN = "invitationParameters";
	public static final String STATUS_COLUMN = "status";
	public static final String INVITATION_EMAIL_ID_COLUMN = "invitationEmailId";
	public static final String USER_LOGIN_NAME_COLUMN = "loginName";
	public static final String USER_INVITE_INVITATION_VALID_UNTIL_COLUMN = "invitationValidUntil";
	public static final String REQUEST_MAPPING_SHOW_REGISTRATION = "showregistrationpage.do";
	public static final String USER_COLUMN = "user";
	public static final String PROFILE_MASTER_COLUMN = "profilesMaster";
	public static final String AUTO_PAYMENT_MODE = "A";
	public static final int INITIAL_PAYMENT_RETRIES = 0;
	public static final String SURVEY_QUESTION_COLUMN = "surveyQuestion";
	public static final String SURVEY_COLUMN = "survey";
	public static final String SURVEY_COMPANY_COLUMN = "company";

	/**
	 * Constants to be used in code for referencing variables(i.e in maps or session attributes)
	 */
	public static final String USER_IN_SESSION = "user";
	public static final String COMPANY_NAME = "companyName";
	public static final String ADDRESS = "address";
	public static final String ZIPCODE = "zipCode";
	public static final String COMPANY_CONTACT_NUMBER = "companyContactNo";

	/**
	 * other constants
	 */
	public static final long EPOCH_TIME_IN_MILLIS = 1000l;
	public static final String GUEST_USER_NAME = "GUEST";
	public static final String ADMIN_USER_NAME = "ADMIN";
}