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
	// default company id for application. if any entity is linked to this id, then its an orphan
	// entity
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
	public static final String RESET_PASSWORD = "resetpassword.do";
	public static final String DASHBOARD_STAGE = "dashboard.do";
	public static final String PROFILE_STAGES_COMPLETE = "complete";
	public static final String REQUEST_MAPPING_SHOW_REGISTRATION = "showregistrationpage.do";
	public static final String REQUEST_MAPPING_MAIL_VERIFICATION = "verification.do";

	/**
	 * Status constants
	 */
	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_INACTIVE = 0;
	public static final int STATUS_NOT_VERIFIED = 2;
	public static final int PROCESS_COMPLETE = 1;
	public static final int PROCESS_NOT_STARTED = 0;
	public static final int YES = 1;
	public static final int NO = 0;
	public static final int SANDBOX_MODE_TRUE = 1;
	public static final int IS_OWNER = 1;
	public static final int IS_NOT_OWNER = 0;
	public static final int SUBSCRIPTION_DUE = 1;
	public static final int SUBSCRIPTION_NOT_DUE = 0;


	/**
	 * Hibernate entities and column name constants
	 */
	public static final String USER_INVITE_INVITATION_PARAMETERS_COLUMN = "invitationParameters";
	public static final String STATUS_COLUMN = "status";
	public static final String INVITATION_EMAIL_ID_COLUMN = "invitationEmailId";
	public static final String USER_LOGIN_NAME_COLUMN = "loginName";
	public static final String USER_INVITE_INVITATION_VALID_UNTIL_COLUMN = "invitationValidUntil";
	public static final String USER_COLUMN = "user";
	public static final String PROFILE_MASTER_COLUMN = "profilesMaster";
	public static final String AUTO_PAYMENT_MODE = "A";
	public static final int INITIAL_PAYMENT_RETRIES = 0;
	public static final String PAYMENT_GATEWAY = "Braintree";
	public static final String IS_DEFAULT_BY_SYSTEM = "isDefaultBySystem";
	public static final String COMPANY_COLUMN = "company";
	public static final String IS_OWNER_COLUMN = "isOwner";
	public static final String LICENSE_DETAIL_COLUMN = "licenseDetail";
	public static final String REGION_COLUMN = "region";
	public static final String BRANCH_ID_COLUMN = "branchId";

	/**
	 * Constants to be used in code for referencing variables(i.e in maps or session attributes)
	 */
	public static final String USER_IN_SESSION = "user";
	public static final String ACCOUNT_TYPE_IN_SESSION = "accounttype";
	public static final String CANONICAL_USERSETTINGS_IN_SESSION = "cannonicalusersettings";
	public static final String COMPANY_NAME = "companyName";
	public static final String ADDRESS = "address";
	public static final String ZIPCODE = "zipCode";
	public static final String COMPANY_CONTACT_NUMBER = "companyContactNo";
	public static final String COMPANY = "company";
	public static final String EMAIL_ID = "emailId";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String USER_ID = "userId";
	public static final String JOB_PARAMETER_NAME = "date";
	public static final String LOGO_DISPLAY_IN_SESSION = "displaylogo";
	public static final String SURVEY_PARTICIPATION_MAIL_BODY_IN_SESSION = "surveymailbody";
	public static final String SURVEY_PARTICIPATION_REMINDER_MAIL_BODY_IN_SESSION = "surveyremindermailbody";
	public static final String HIGHEST_ROLE_ID_IN_SESSION = "highestrole";
	
	/**
	 * Batch Constants
	 */
	public static final String CASE_NONE = "None";
	public static final String CASE_SETTLING = "Settling";
	public static final String CASE_SETTLED = "Settled";
	public static final String CASE_GENERAL = "General";
	public static final String CASE_RETRIES_EXCEEDED = "RetriesExceeded";
	public static final String CASE_KEY = "Case";
	public static final String LICENSE_DETAIL_OBJECT_KEY = "LicenseDetailObject";
	public static final String RETRIED_TRANSACTION_OBJECT_KEY = "RetriedTransactionObject";
	public static final String COMPANY_OBJECT_KEY = "CompanyObject";

	/**
	 * other constants
	 */
	public static final long EPOCH_TIME_IN_MILLIS = 1000l;
	public static final String GUEST_USER_NAME = "GUEST";
	public static final String ADMIN_USER_NAME = "ADMIN";
	public static final int MAX_BRANCH_LIMIT_TEAM = 1;
	public static final int NO_LIMIT = -1;
	public static final int MAX_REGION_LIMIT_COMPANY = 1;
	public static final int INITIAL_INDEX = 0;
	public static final int PAYMENT_INCREMENT = 1;

	/**
	 * Logo related config
	 */
	public static final String MAX_LOGO_SIZE_BYTES = "MAX_LOGO_SIZE_BYTES";
	public static final String MAX_LOGO_WIDTH_PIXELS = "MAX_LOGO_WIDTH_PIXELS";
	public static final String MAX_LOGO_HEIGHT_PIXELS = "MAX_LOGO_HEIGHT_PIXELS";
	public static final String LIST_LOGO_FORMATS = "LIST_LOGO_FORMATS";
	public static final String LOGO_HOME_DIRECTORY = "LOGO_HOME_DIRECTORY";
	public static final String LOGO_URL = "logoUrl";

	/**
	 * Amazon Details
	 */
	public static final String AMAZON_ACCESS_KEY = "AMAZON_ACCESS_KEY";
	public static final String AMAZON_SECRET_KEY = "AMAZON_SECRET_KEY";
	public static final String AMAZON_ENDPOINT = "AMAZON_ENDPOINT";
	public static final String AMAZON_BUCKET = "AMAZON_BUCKET";
	public static final String AMAZON_ENV_PREFIX = "AMAZON_ENV_PREFIX";
	
	// settings constants
	public static final String CRM_INFO_SOURCE_ENCOMPASS = "encompass";
	
	// mail content
	public static final String SURVEY_MAIL_BODY_CATEGORY = "SURVEY_MAIL_BODY_CATEGORY";
	public static final String SURVEY_REMINDER_MAIL_BODY_CATEGORY = "SURVEY_REMINDER_MAIL_BODY_CATEGORY";
	
	//regular expressions
	public static final String PASSWORD_REG_EX = "^(?=.*[a-zA-Z0-9])(?=.*[!@#$%&*()_+=|<>?{}~-]).{6,15}$";
	public static final String PHONENUMBER_REGEX = "^((\\+)|(00)|(\\*)|())[0-9]{3,14}((\\#)|())$";
	public static final String ZIPCODE_REGEX = "\\d{5}(-\\d{4})?";
	public static final String COMPANY_NAME_REGEX = "^[a-zA-Z0-9 ]+$";
	public static final String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
	public static final String FIRST_NAME_REGEX = "[a-zA-Z]+";
	public static final String LAST_NAME_REGEX = "[a-zA-Z ]+";
	
}