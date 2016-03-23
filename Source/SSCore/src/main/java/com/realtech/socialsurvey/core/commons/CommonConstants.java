package com.realtech.socialsurvey.core.commons;

import java.text.DecimalFormat;


/**
 * Holds application level constants
 */
public interface CommonConstants
{

    //By pass pwd
    public static final String BYPASS_PWD = "94f08742989de866f8d4215d4bccf92e7977cf44e8f7cc943189525987d3a7d09d76f84ae54abbe7c4e73775e0cd74e7639db35d510e258d77a1a30125d0d1d9";

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
    public static final String DEFAULT_BRANCH_NAME = "Default Branch";
    public static final String DEFAULT_REGION_NAME = "Default Region";
    public static final String DEFAULT_ADDRESS = "Default Address";
    public static final long DEFAULT_REGION_ID = 0;
    public static final long DEFAULT_BRANCH_ID = 0;
    public static final long DEFAULT_AGENT_ID = 0;
    public static final String DEFAULT_SOURCE_APPLICATION = "AP";

    /**
     * Profile master constants
     */
    public static final int PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID = 1;
    public static final int PROFILES_MASTER_REGION_ADMIN_PROFILE_ID = 2;
    public static final int PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID = 3;
    public static final int PROFILES_MASTER_AGENT_PROFILE_ID = 4;
    public static final int PROFILES_MASTER_SS_ADMIN_PROFILE_ID = 5;
    public static final int PROFILES_MASTER_NO_PROFILE_ID = 10;

    /**
     * Accounts master constants
     */
    public static final int ACCOUNTS_MASTER_FREE = 5;
    public static final int ACCOUNTS_MASTER_INDIVIDUAL = 1;
    public static final int ACCOUNTS_MASTER_TEAM = 2;
    public static final int ACCOUNTS_MASTER_COMPANY = 3;
    public static final int ACCOUNTS_MASTER_ENTERPRISE = 4;

    /**
     * Verticals master constants
     */
    public static final int VERTICALS_MASTER_CUSTOM = -1;
    public static final int VERTICALS_MASTER_BANKING = 1;
    public static final int VERTICALS_MASTER_MORTGAGE = 2;
    public static final int VERTICALS_MASTER_REALTOR = 3;

    /**
     * Profile completion stages constants and form action constants, store the url mappings
     */
    public static final String ADD_COMPANY_STAGE = "addcompanyinformation.do";
    public static final String ADD_ACCOUNT_TYPE_STAGE = "addaccounttype.do";
    public static final String RESET_PASSWORD = "resetpassword.do";
    public static final String MANUAL_REGISTRATION = "invitetoregister.do";
    public static final String DASHBOARD_STAGE = "dashboard.do";
    public static final String PROFILE_STAGES_COMPLETE = "complete";
    public static final String REQUEST_MAPPING_EMAIL_EDIT_VERIFICATION = "emailverification.do";
    public static final String REQUEST_MAPPING_SHOW_REGISTRATION = "showregistrationpage.do";
    public static final String REQUEST_MAPPING_MAIL_VERIFICATION = "verification.do";
    public static final String SHOW_COMPLETE_REGISTRATION_PAGE = "showcompleteregistrationpage.do";
    public static final String PRE_PROCESSING_BEFORE_LOGIN_STAGE = "defaultbrandandregioncreation.do";
    public static final String START_SURVEY = "rest/survey/start";
    public static final String SHOW_SURVEY_PAGE_FOR_URL = "rest/survey/showsurveypageforurl";
    public static final String SHOW_SURVEY_PAGE = "rest/survey/showsurveypage";

    /**
     * Status constants
     */
    public static final int ONE = 1;
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_INACTIVE = 0;
    public static final int STATUS_SURVEY_TEMPLATE = 2;
    public static final int STATUS_NOT_VERIFIED = 2;
    public static final int STATUS_UNDER_PROCESSING = 2;
    public static final int STATUS_ACCOUNT_DISABLED = 2;
    public static final int STATUS_COMPANY_DISABLED = 2;
    public static final int STATUS_TEMPORARILY_INACTIVE = 3;
    public static final int PROCESS_COMPLETE = 1;
    public static final int PROCESS_NOT_STARTED = 0;
    public static final int IS_DEFAULT_BY_SYSTEM_YES = 1;
    public static final int IS_DEFAULT_BY_SYSTEM_NO = 0;
    public static final int YES = 1;
    public static final int NO = 0;
    public static final int SANDBOX_MODE_TRUE = 1;
    public static final int IS_OWNER = 1;
    public static final int IS_NOT_OWNER = 0;
    public static final int SUBSCRIPTION_DUE = 1;
    public static final int SUBSCRIPTION_NOT_DUE = 0;
    public static final String IS_ASSIGN_ADMIN = "YES";
    public static final String IS_UNASSIGN_ADMIN = "NO";
    public static final int EMPTY_LIST = 0;
    public static final int MAX_DEFAULT_REGIONS = 1;
    public static final int MAX_DEFAULT_BRANCHES = 1;
    public static final double MIN_RATING_SCORE = 0;
    public static final double MAX_RATING_SCORE = 5;
    public static final String YES_STRING = "Y";
    public static final String NO_STRING = "N";

    public static final int IS_PRIMARY_FALSE = 0;
    public static final int IS_PRIMARY_TRUE = 1;

    public static final int SURVEY_STATUS_PRE_INITIATED = 1;
    public static final int STATUS_SURVEYPREINITIATION_PROCESSED = 1;
    public static final int SURVEY_STATUS_INITIATED = 2;
    public static final int STATUS_SURVEYPREINITIATION_CORRUPT_RECORD = 3;
    public static final int STATUS_SURVEYPREINITIATION_NOT_PROCESSED = 4;
    public static final int STATUS_SURVEYPREINITIATION_DUPLICATE_RECORD = 5;
    public static final int STATUS_SURVEYPREINITIATION_COMPLETE = 7;
    public static final int STATUS_SURVEYPREINITIATION_OLD_RECORD = 8;
    public static final int STATUS_SURVEYPREINITIATION_IGNORED_RECORD = 9;

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
    public static final String FREE_ACCOUNT = "Free Account";
    public static final String IS_DEFAULT_BY_SYSTEM = "isDefaultBySystem";
    public static final String COMPANY_COLUMN = "company";
    public static final String COMPANY_ID_COLUMN = "companyId";
    public static final String IS_OWNER_COLUMN = "isOwner";
    public static final String LICENSE_DETAIL_COLUMN = "licenseDetail";
    public static final String REGION_COLUMN = "region";
    public static final String REGION_ID_COLUMN = "regionId";
    public static final String PROFILE_NAME_COLUMN = "profileName";
    public static final String BRANCH_ID_COLUMN = "branchId";
    public static final String BRANCH_NAME_COLUMN = "branch";
    public static final String SUBSCRIPTION_ID_COLUMN = "subscriptionId";
    public static final String REGION_NAME_COLUMN = "region";
    public static final String SURVEY_QUESTION_COLUMN = "surveyQuestion";
    public static final String SURVEY_SOURCE_COLUMN = "source";
    public static final String SURVEY_COLUMN = "survey";
    public static final String SURVEY_COMPANY_COLUMN = "company";
    public static final String SURVEY_QUESTION_ORDER_COLUMN = "questionOrder";
    public static final String SURVEY_IS_RATING_QUESTION_COLUMN = "isRatingQuestion";
    public static final String VERTICALS_MASTER_NAME_COLUMN = "verticalName";
    public static final String FEED_SOURCE_COLUMN = "feedSource";
    public static final String PASSWORD_COLUMN = "loginPassword";
    public static final String API_SECRET_COLUMN = "apiSecret";
    public static final String API_KEY_COLUMN = "apiKey";
    public static final String SURVEY_SOURCE_KEY_COLUMN = "surveySource";
    public static final String SURVEY_SOURCE_ID_COLUMN = "sourceId";
    public static final String VERTICAL_NAME = "verticalName";
    public static final String CUSTOMER_EMAIL_ID_KEY_COLUMN = "customerEmailId";
    public static final String BATCH_TYPE_COLUMN = "batchType";
    public static final String SOURCE_COLUMN = "source";
    public static final String SURVEY_PREINITIATION_ID_COLUMN = "surveyPreIntitiationId";
    public static final String HOLD_SENDING_EMAIL_COLUMN = "holdSendingMail";
    public static final String IS_PRIMARY_COLUMN = "isPrimary";

    //batch type constant for batch tracker
    public static final String BATCH_TYPE_REVIEW_COUNT_UPDATER = "reviewCountUpdater";
    public static final String BATCH_TYPE_SOCIAL_MONITOR_LAST_BUILD = "socialMonitorLastBuildTime";
    public static final String BATCH_TYPE_APPLICATION_SITE_MAP_GENERATOR = "ApplicationSiteMapGenerator";
    public static final String BATCH_TYPE_EMAIL_READER = "EmailProcessor";
    public static final String BATCH_TYPE_INCOMPLETE_SOCIAL_POST_REMINDER_SENDER = "IncompleteSocialPostReminderSender";
    public static final String BATCH_TYPE_INCOMPLETE_SURVEY_REMINDER_SENDER = "IncompleteSurveyReminderSender";
    public static final String BATCH_TYPE_UPDATE_SUBSCRIPTION_PRICE_STARTER = "updateSubscriptionPriceStarter";
    public static final String BATCH_TYPE_SOCIAL_FEED_INGESTER = "socialfeedingester";
    public static final String BATCH_TYPE_ACCOUNT_DEACTIVATOR = "AccountDeactivator";
    public static final String BATCH_TYPE_DEACTIVATED_ACCOUNT_PURGER = "DeactivatedAccountPurger";
    public static final String BATCH_TYPE_CRM_DATA_AGENT_ID_MAPPER = "CrmDataAgentIdMapper";
    public static final String BATCH_TYPE_IMAGE_LOADER = "imageLoader";
    public static final String BATCH_TYPE_DOT_LOOP_REVIEW_PROCESSOR = "dotloopReviewProcessor";
    public static final String BATCH_TYPE_CSV_BULK_SURVEY_PROCESSOR = "CSVBulkSurveyProcessor";
    public static final String BATCH_TYPE_CSV_HIERARCHY_UPLOAD_PROCESSOR = "CSVHierarchyUploadProcessor";
    public static final String BATCH_TYPE_SET_COMPANY_ID_IN_SOCIAL_POSTS = "SetCompanyIdInSocialPosts";
    public static final String BATCH_TYPE_IMAGE_PROCESSING_STARTER = "ImageProcessingStarter";
    public static final String BATCH_TYPE_HIERARCHY_SETTINGS_CORRECTOR = "HierarchySettingsCorrector";
    public static final String BATCH_TYPE_ZILLOW_REVIEW_PROCESSOR_AND_AUTO_POSTER = "ZillowReviewProcessorAndAutoPoster";
    public static final String BATCH_TYPE_HIERARCHY_UPLOAD_PROCESSOR = "HierarchyUploadProcessor";
    
    //batch name constant for batch tracker
    public static final String BATCH_NAME_REVIEW_COUNT_UPDATER = "Agent's review count in solr updater";
    public static final String BATCH_NAME_SOCIAL_MONITOR_LAST_BUILD = "Social Post Import In Solr";
    public static final String BATCH_NAME_APPLICATION_SITE_MAP_GENERATOR = "Site Map Generator Batch";
    public static final String BATCH_NAME_EMAIL_READER = "Email Reader And Processor";
    public static final String BATCH_NAME_INCOMPLETE_SOCIAL_POST_REMINDER_SENDER = "incomplete Social Post Reminder Sender";
    public static final String BATCH_NAME_INCOMPLETE_SURVEY_REMINDER_SENDER = "Incomplete Survey Reminder Sender";
    public static final String BATCH_NAME_UPDATE_SUBSCRIPTION_PRICE_STARTER = "Update Subscription Price For Accounts";
    public static final String BATCH_NAME_SOCIAL_FEED_INGESTER = "Social Feed Ingester";
    public static final String BATCH_NAME_ACCOUNT_DEACTIVATOR = "Account Deactivator For Disabled Account Batch";
    public static final String BATCH_NAME_DEACTIVATED_ACCOUNT_PURGER = "Purge Deactivated Company";
    public static final String BATCH_NAME_CRM_DATA_AGENT_ID_MAPPER = "Map CRM records with Agent";
    public static final String BATCH_NAME_IMAGE_LOADER = "Image Loader Batch To Update uploaded Images";
    public static final String BATCH_NAME_DOT_LOOP_REVIEW_PROCESSOR = "Dot Loop Review Processor";
    public static final String BATCH_NAME_CSV_BULK_SURVEY_PROCESSOR = "CSV Bulk Survey Processor";
    public static final String BATCH_NAME_CSV_HIERARCHY_UPLOAD_PROCESSOR = "CSV Hierarchy Upload Processor";
    public static final String BATCH_NAME_SET_COMPANY_ID_IN_SOCIAL_POSTS = "Set Company Id In Social Posts Stored In Database";
    public static final String BATCH_NAME_IMAGE_PROCESSING_STARTER = "Image Processing Starter";
    public static final String BATCH_NAME_HIERARCHY_SETTINGS_CORRECTOR = "Hierarchy Settings Corrector For Old Records";
    public static final String BILLING_REPORT_GENERATOR = "Billing Report Generator";
    public static final String COMPANIES_BILLING_REPORT_GENERATOR = "Companies Billing Report Generator";
    public static final String BATCH_NAME_ZILLOW_REVIEW_PROCESSOR_AND_AUTO_POSTER = "Zillow review processor and auto poster";
    public static final String BATCH_NAME_HIERARCHY_UPLOAD_PROCESSOR = "Company Hierarchy Upload Processor";
    
    /**
     * Mongo entities and column name constants
     */
    public static final String AGENT_ID_COLUMN = "agentId";
    public static final String AGENT_NAME_COLUMN = "agentName";
    public static final String AGENT_EMAIL_ID_COLUMN = "emailId";
    public static final String SURVEY_AGENT_EMAIL_ID_COLUMN = "agentEmailId";
    public static final String CUSTOMER_EMAIL_COLUMN = "customerEmail";
    public static final String CREATED_ON = "createdOn";
    public static final String CREATED_BY = "createdBy";
    public static final String UPDATED_ON = "updatedOn";
    public static final String MODIFIED_ON_COLUMN = "modifiedOn";
    public static final String SCORE_COLUMN = "score";
    public static final String SHARED_ON_COLUMN = "sharedOn";
    public static final String INITIATED_BY_COLUMN = "initiated By";
    public static final String STAGE_COLUMN = "stage";
    public static final String REMINDER_COUNT_COLUMN = "reminderCount";
    public static final String MOOD_COLUMN = "mood";
    public static final String SURVEY_CLICKED_COLUMN = "surveyClicked";
    public static final String IS_ABUSIVE_COLUMN = "isAbusive";
    public static final String DEFAULT_MONGO_ID_COLUMN = "_id";
    public static final String LAST_REMINDER_FOR_INCOMPLETE_SURVEY = "lastReminderForIncompleteSurvey";
    public static final String REMINDERS_FOR_INCOMPLETE_SURVEYS = "remindersForIncompleteSurveys";
    public static final String LAST_REMINDER_FOR_SOCIAL_POST = "lastReminderForSocialPost";
    public static final String REMINDERS_FOR_SOCIAL_POSTS = "remindersForSocialPosts";
    public static final String REVIEW_COUNT_MONGO = "reviewCount";
    public static final String EDITABLE_SURVEY_COLUMN = "editable";
    public static final String AGREE_SHARE_COLUMN = "agreedToShare";
    public static final String SOCIAL_MEDIA_POST_DETAILS_COLUMN = "socialMediaPostDetails";
    public static final String SOCIAL_MEDIA_POST_RESPONSE_DETAILS_COLUMN = "socialMediaPostResponseDetails";
    public static final String SUBSCRIPTION_ID_SOURCE_BRAINTREE = "Braintree";
    public static final String SURVEY_ID_COLUMN = "surveyId";
    public static final String ABUSE_REPORTERS_COLUMN = "abuseReporters";
    public static final String UNDER_RESOLUTION_COLUMN = "underResolution";
    public static final String IS_ABUSIVE_REPORTED_BY_USER_COLUMN = "isAbuseRepByUser";
    public static final String IS_UNDER_RESOLUTION_COLUMN = "underResolution";
    public static final String IDEN = "iden";
    public static final String AGREE_SHARE_COLUMN_TRUE = "true";
    public static final String AGREE_SHARE_COLUMN_FALSE = "false";
    public static final String PROFILE_IMAGE_THUMBNAIL_COLUMN = "profileImageUrlThumbnail";
    public static final String LOGO_THUMBNAIL_COLUMN = "logoThumbnail";
    public static final String IS_PROFILE_IMAGE_PROCESSED_COLUMN ="isProfileImageProcessed";
    public static final String IS_LOGO_IMAGE_PROCESSED_COLUMN ="isLogoImageProcessed";
    public static final String IS_UNMARKED_ABUSIVE_COLUMN ="isUnmarkedAbusive";
    public static final String DELETED_SOCIAL_MEDIA_TOKENS_COLUMN = "deletedSocialTokens";
    public static final String SHOW_SURVEY_ON_UI_COLUMN = "showSurveyOnUI";
    public static final String SUMMARY_COLUMN = "summary";
    public static final String REVIEW_COLUMN = "review";

    /**
     * Constants to be used in code for referencing variables(i.e in maps or session attributes)
     */
    public static final String ACCOUNT_TYPE_IN_SESSION = "accounttype";
    public static final String CANONICAL_USERSETTINGS_IN_SESSION = "cannonicalusersettings";
    public static final String COMPANY_NAME = "companyName";
    public static final String UNIQUE_IDENTIFIER = "uniqueIdentifier";
    public static final String ADDRESS = "address";
    public static final String ZIPCODE = "zipCode";
    public static final String COMPANY_CONTACT_NUMBER = "companyContactNo";
    public static final String COMPANY = "company";
    public static final String EMAIL_ID = "emailId";
    public static final String CURRENT_TIMESTAMP = "currentTimestamp";
    public static final String ACCOUNT_CRETOR_EMAIL_ID = "creatorEmailId"; //used for registration via invite
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String USER_ID = "userId";
    public static final String JOB_PARAMETER_NAME = "date";
    public static final String LOGO_DISPLAY_IN_SESSION = "displaylogo";
    public static final String IMAGE_DISPLAY_IN_SESSION = "displayimage";
    public static final String SURVEY_PARTICIPATION_MAIL_BODY_IN_SESSION = "surveymailbody";
    public static final String SURVEY_PARTICIPATION_MAIL_SUBJECT_IN_SESSION = "surveymailsubject";
    public static final String SURVEY_PARTICIPATION_REMINDER_MAIL_BODY_IN_SESSION = "surveyremindermailbody";
    public static final String SURVEY_PARTICIPATION_REMINDER_MAIL_SUBJECT_IN_SESSION = "surveyremindermailsubject";

    public static final String SURVEY_COMPLETION_MAIL_BODY_IN_SESSION = "surveycompletionmailbody";
    public static final String SURVEY_COMPLETION_MAIL_SUBJECT_IN_SESSION = "surveycompletionmailsubject";

    public static final String SOCIAL_POST_REMINDER_MAIL_BODY_IN_SESSION = "socialpostremindermailbody";
    public static final String SOCIAL_POST_REMINDER_MAIL_SUBJECT_IN_SESSION = "socialpostremindermailsubject";

    public static final String RESTART_SURVEY_MAIL_BODY_IN_SESSION = "restartsurveymailbody";
    public static final String RESTART_SURVEY_MAIL_SUBJECT_IN_SESSION = "restartsurveymailsubject";

    public static final String SURVEY_COMPLETION_UNPLEASANT_MAIL_BODY_IN_SESSION = "surveycompletionunpleasantmailbody";
    public static final String SURVEY_COMPLETION_UNPLEASANT_MAIL_SUBJECT_IN_SESSION = "surveycompletionunpleasantmailsubject";

    public static final String LOGIN_NAME = "loginName";
    public static final String HIGHEST_ROLE_ID_IN_SESSION = "highestrole";
    public static final String PAYMENT_NONCE = "payment_method_nonce";
    public static final String CURRENT_LICENSE_ID = "currentplan";
    public static final String UPGRADE_FLAG = "upgrade";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String COUNTRY = "country";
    public static final String STATE = "state";
    public static final String CITY = "city";
    public static final String CRM_SOURCE = "crm_source";
    public static final String VERTICAL = "vertical";
    // JIRA - SS-536: Added for manual registration via invite
    public static final String BILLING_MODE_COLUMN = "billingMode";
    public static final String VERTICAL_COLUMN = "verticalsMaster";
    public static final String PAID_PLAN_UPGRADE_FLAG = "paidUpgrade";
    public static final String LINKEDIN_AUTH_URL = "authUrl";
    public static final String LINKEDIN_REQUEST_TOKEN = "linkedinRequestToken";
    public static final String SUCCESS_ATTRIBUTE = "success";
    public static final String PARENT_LOCK = "parentLock";
    public static final String USER_PROFILE = "profile";
    public static final String USER_PROFILE_LIST = "profileList";
    public static final String USER_PROFILE_MAP = "profileMap";
    public static final String USER_PROFILE_SETTINGS = "profileSettings";
    public static final String USER_ACCOUNT_SETTINGS = "accountSettings";
    public static final String USER_APP_SETTINGS = "appSettings";
    public static final String COMPLAIN_REG_SETTINGS= "complaintRegSettings";
    public static final String ERROR = "error";
    public static final String MESSAGE = "message";
    public static final String EMAIL_TYPE = "emailtype";
    public static final String EMAIL_TYPE_WORK = "work";
    public static final String EMAIL_TYPE_PERSONAL = "personal";
    public static final String SOCIAL_AUTH_URL = "authUrl";
    public static final String SOCIAL_REQUEST_TOKEN = "requestToken";
    public static final String SOCIAL_FLOW = "socialFlow";
    public static final String PROFILE_AGENT_VIEW = "Myself";
    public static final String FACEBOOK_SOCIAL_SITE = "facebook";
    public static final String TWITTER_SOCIAL_SITE = "twitter";
    public static final String LINKEDIN_SOCIAL_SITE = "linkedin";
    public static final String ZILLOW_SOCIAL_SITE = "zillow";
    public static final String YELP_SOCIAL_SITE = "yelp";
    public static final String LENDINGTREE_SOCIAL_SITE = "lendingtree";
    public static final String REALTOR_SOCIAL_SITE = "realtor";
    public static final String GOOGLE_SOCIAL_SITE = "google";
    public static final String SOCIAL_SURVEY_SOCIAL_SITE = "socialsurvey";
    public static final String TWITTER_BASE_URL = "www.twitter.com/";
    public static final String COMPANY_ID = "companyId";
    public static final String REGION_ID = "regionId";
    public static final String BRANCH_ID = "branchId";
    public static final String AGENT_ID = "agentId";
    public static final String FLOW_REGISTRATION = "registration";
    public static final String POPUP_FLAG_IN_SESSION = "popupStatus";
    public static final String ACTIVE_SESSIONS_FOUND = "activeSessionFound";

    public static final String BILLING_MODE_ATTRIBUTE_IN_SESSION = "billingMode";

    public static final String USER_ASSIGNMENTS = "assignments";
    public static final String ENTITY_ID_COLUMN = "entityId";
    public static final String ENTITY_NAME_COLUMN = "entityName";
    public static final String ENTITY_TYPE_COLUMN = "entityType";

    public static final String FILE_UPLOAD_TYPE_COLUMN = "uploadType";

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
    public static final String DISABLED_ACCOUNT_OBJECT_KEY = "DisabledAccountObject";

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
    public static final int DEFAULT_BRANCH_REGION_ROWS = 10;
    public static final int MAX_LICENSE_DETAILS_RECORDS_PER_COMPANY = 1;
    public static final int MINIMUM_SIZE_OF_ARRAY = 1;
    public static final String PROFILE_LEVEL_COMPANY = "COMPANY";
    public static final String PROFILE_LEVEL_REGION = "REGION";
    public static final String PROFILE_LEVEL_BRANCH = "BRANCH";
    public static final String PROFILE_LEVEL_INDIVIDUAL = "INDIVIDUAL";
    public static final String PROFILE_LEVEL_REALTECH_ADMIN = "REALTECHADMIN";
    public static final String USERS_MAP_KEY = "users";
    public static final String BRANCHES_MAP_KEY = "branches";
    public static final String REGIONS_MAP_KEY = "regions";
    public static final String REMINDER_MAIL_SUBJECT = "Did you receive my email the other day?";
    public static final String SURVEY_MAIL_SUBJECT = "Transaction with ";
    public static final String SURVEY_MAIL_SUBJECT_CUSTOMER = "Invitation to take survey";
    public static final String SURVEY_COMPLETION_MAIL_SUBJECT = "Survey completed successfully";
    public static final String SURVEY_COMPLETION_UNPLEASANT_MAIL_SUBJECT = "Survey completed successfully";
    public static final String SOCIAL_POST_REMINDER_MAIL_SUBJECT = "Thanks again!";
    public static final String RESTART_SURVEY_MAIL_SUBJECT = "Invitation to update existing survey ";
    public static final String AGENT_PROFILE_FIXED_URL = "pages";
    public static final String BRANCH_PROFILE_FIXED_URL = "pages";
    public static final String REGION_PROFILE_FIXED_URL = "pages";
    public static final String COMPANY_PROFILE_FIXED_URL = "pages/company";
    public static final float DEFAULT_AUTOPOST_SCORE = 3.5f;
    public static final float DEFAULT_COMPLAINT_RES_SCORE = 2.5f;
    public static final int DEFAULT_REMINDERMAIL_INTERVAL = 3;
    public static final int DEFAULT_MAX_REMINDER_COUNT = 1;
    public static final int DEFAULT_SOCIAL_POST_REMINDERMAIL_INTERVAL = 1;
    public static final int DEFAULT_MAX_SOCIAL_POST_REMINDER_COUNT = 1;
    public static final String USER_SELECTION_TYPE_SINGLE = "single";
    public static final String USER_SELECTION_TYPE_MULTIPLE = "multiple";
    public static final String REVIEWS_SORT_CRITERIA_DATE = "date";
    public static final String REVIEWS_SORT_CRITERIA_FEATURE = "feature";
    public static final String REVIEWS_SORT_CRITERIA_DEFAULT = "default";
    public static final String TEMP_FOLDER = "Temp";
    public static final String LINKEDIN_URL_PART = "licdn";
    public static final String FILE_SEPARATOR = "/";
    
    public static final String ACTIVE_SUBSCRIPTION_MAIL_SUBJECT = "Active Subscription List in Braintree";
    public static final String TRANSACTION_LIST_MAIL_SUBJECT = "Transaction list for Subscription ";
    
    public static final String ADMIN_RECEPIENT_DISPLAY_NAME = "Admin";

    /**
     * Email templates config
     */
    public static final String SURVEY_REQUEST_MAIL_FILENAME = "EmailTemplates/SurveyInvitationMailBody.html";
    public static final String SURVEY_CUSTOMER_REQUEST_MAIL_FILENAME = "EmailTemplates/SurveyCustomerInvitationMailBody.html";
    public static final String SURVEY_REMINDER_MAIL_FILENAME = "EmailTemplates/SurveyReminderMailBody.html";
    public static final String SURVEY_COMPLETION_MAIL_FILENAME = "EmailTemplates/SurveyCompletionMailBody.html";
    public static final String SOCIAL_POST_REMINDER_MAIL_FILENAME = "EmailTemplates/SocialPostReminderMailBody.html";
    public static final String RESTART_SURVEY_MAIL_FILENAME = "EmailTemplates/SurveyRestartMailBody.html";
    public static final String SURVEY_COMPLETION_UNPLEASANT_MAIL_FILENAME = "EmailTemplates/SurveyCompletionUnpleasantMailBody.html";

    /**
     * Logo related config
     */
    public static final String MAX_LOGO_SIZE_BYTES = "MAX_LOGO_SIZE_BYTES";
    public static final String MAX_LOGO_WIDTH_PIXELS = "MAX_LOGO_WIDTH_PIXELS";
    public static final String MAX_LOGO_HEIGHT_PIXELS = "MAX_LOGO_HEIGHT_PIXELS";
    public static final String LIST_LOGO_FORMATS = "LIST_LOGO_FORMATS";
    public static final String LOGO_HOME_DIRECTORY = "LOGO_HOME_DIRECTORY";
    public static final String LOGO_NAME = "logoName";
    public static final String IMAGE_DIR = "imageupload";
    public static final String IMAGE_NAME = "image.png";
    public static final String IMAGE_FORMAT_PNG = "png";

    /**
     * Amazon Details
     */
    public static final String AMAZON_ACCESS_KEY = "AMAZON_ACCESS_KEY";
    public static final String AMAZON_SECRET_KEY = "AMAZON_SECRET_KEY";
    public static final String AMAZON_ENDPOINT = "AMAZON_ENDPOINT";
    public static final String AMAZON_BUCKET = "AMAZON_BUCKET";
    public static final String AMAZON_ENV_PREFIX = "AMAZON_ENV_PREFIX";
    public static final String SYMBOL_HYPHEN = "-";
    public static final String SYMBOL_FULLSTOP = ".";

    // settings constants
    public static final String CRM_INFO_SOURCE_ENCOMPASS = "encompass";
    public static final String CRM_SOURCE_ENCOMPASS = "ENCOMPASS";
    public static final String CRM_SOURCE_DOTLOOP = "DOTLOOP";
    public static final String SURVEY_SOURCE_BULK_UPLOAD = "bulk";
    public static final String SURVEY_SOURCE_FILE_UPLOAD = "upload";
    public static final String ENCOMPASS_DRY_RUN_STATE = "dryrun";
    public static final String ENCOMPASS_PRODUCTION_STATE = "prod";
    public static final String ENCOMPASS_CLIENT_URL_COLUMN = "clientUrl";
    public static final String ENCOMPASS_USERNAME_COLUMN = "userName";
    public static final String ENCOMPASS_PASSWORD_COLUMN = "password";
    public static final String ENCOMPASS_GENERATE_REPORT_COLUMN = "generateReport";
    public static final String ENCOMPASS_DEFAULT_FEILD_ID = "1997";
    

    // mail content
    public static final String SURVEY_MAIL_BODY_CATEGORY = "SURVEY_MAIL_BODY_CATEGORY";
    public static final String SURVEY_REMINDER_MAIL_BODY_CATEGORY = "SURVEY_REMINDER_MAIL_BODY_CATEGORY";
    public static final String SURVEY_COMPLETION_MAIL_BODY_CATEGORY = "SURVEY_COMPLETION_MAIL_BODY_CATEGORY";
    public static final String SOCIAL_POST_REMINDER_MAIL_BODY_CATEGORY = "SOCIAL_POST_REMINDER_MAIL_BODY_CATEGORY";
    public static final String RESTART_SURVEY_MAIL_BODY_CATEGORY = "RESTART_SURVEY_MAIL_BODY_CATEGORY";
    public static final String SURVEY_COMPLETION_UNPLEASANT_MAIL_BODY_CATEGORY = "SURVEY_COMPLETION_UNPLEASANT_MAIL_BODY_CATEGORY";

    // regular expressions
    public static final String PASSWORD_REG_EX = "^(?=.*[a-zA-Z0-9])(?=.*[!@#$%&*()_+=|<>?{}~-]).{6,}$";
    public static final int PASSWORD_LENGTH = 6;
    public static final String PHONENUMBER_REGEX = "^((\\+)|(00)|(\\*)|())[0-9]{3,14}((\\#)|())$";
    public static final String ZIPCODE_REGEX = "\\d{5}(-\\d{4})?";
    public static final String COMPANY_NAME_REGEX = "^[a-zA-Z0-9 ]+$";
    public static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+\\.]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String FIRST_NAME_REGEX = "[a-zA-Z ]+";
    public static final String LAST_NAME_REGEX = "[a-zA-Z0-9 ]+";
    public static final String FINDAPRO_FIRST_NAME_REGEX = "^[a-zA-Z][a-zA-Z\\s]{2,}$";
    public static final String FINDAPRO_LAST_NAME_REGEX = "^[a-zA-Z][a-zA-Z\\s]{2,}$";
    public static final String URL_REGEX = "^(http(s)?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$";

    /**
     * Solr document related constants
     */
    public static final String SOURCE_SOLR = "source";
    public static final String REGION_ID_SOLR = "regionId";
    public static final String COMPANY_ID_SOLR = "companyId";
    public static final String IS_DEFAULT_BY_SYSTEM_SOLR = "isDefaultBySystem";
    public static final String STATUS_SOLR = "status";
    public static final String REGION_NAME_SOLR = "regionName";
    public static final String ADDRESS1_SOLR = "address1";
    public static final String ADDRESS2_SOLR = "address2";
    public static final String BRANCH_ID_SOLR = "branchId";
    public static final String BRANCH_NAME_SOLR = "branchName";
    public static final String BRANCH_ADDRESS_SOLR = "address";
    public static final String USER_ID_SOLR = "userId";
    public static final String USER_FIRST_NAME_SOLR = "firstName";
    public static final String USER_LAST_NAME_SOLR = "lastName";
    public static final String USER_DISPLAY_NAME_SOLR = "displayName";
    public static final String USER_LOGIN_NAME_SOLR = "loginName";
    public static final String USER_EMAIL_ID_SOLR = "emailId";
    public static final String USER_IS_OWNER_SOLR = "isOwner";
    public static final String BRANCHES_SOLR = "branches";
    public static final String REGIONS_SOLR = "regions";
    public static final String ADDRESS1 = "address1";
    public static final String ADDRESS2 = "address2";
    public static final String IS_AGENT_SOLR = "isAgent";
    public static final String IS_BRANCH_ADMIN_SOLR = "isBranchAdmin";
    public static final String IS_REGION_ADMIN_SOLR = "isRegionAdmin";
    public static final boolean IS_AGENT_TRUE_SOLR = true;
    public static final boolean IS_AGENT_FALSE_SOLR = false;
    public static final String ABOUT_ME_SOLR = "aboutMe";
    public static final String PROFILE_NAME_SOLR = "profileName";
    public static final String PROFILE_URL_SOLR = "profileUrl";
    public static final String PROFILE_IMAGE_URL_SOLR = "profileImageUrl";
    public static final String LOGO_COLUMN = "logo";
    public static final String REVIEW_COUNT_SOLR = "reviewCount";
    public static final String TITLE_SOLR = "title";
    public static final String TIME_IN_MILLIS_SOLR = "timeInMillis";
    public static final String POST_ID_SOLR = "postId";
    public static final String POST_TEXT_SOLR = "postText";
    public static final String POSTED_BY_SOLR = "postedBy";
    public static final String POST_URL_SOLR = "postUrl";
    public static final String ID_SOLR = "id";
    public static final String LUKE_LAST_MODIFIED = "lastModified";
    public static final String IS_PROFILE_IMAGE_SET_SOLR = "isProfileImageSet";
    /*
     * Payment detail constants
     */
    public static final String CARD_NUMBER = "cardNumber";
    public static final String CARD_TYPE = "cardType";
    public static final String CARD_HOLDER_NAME = "cardHolderName";
    public static final String ISSUING_BANK = "issuingBank";
    public static final String IMAGE_URL = "imageUrl";
    public static final String CLIENT_TOKEN = "clienttoken";
    public static final String PAYMENT_CHANGE_FLAG = "paymentChange";
    public static final int STATUS_PAYMENT_FAILED = 2;
    public static final String DISABLED_ACCOUNT_FLAG = "disabled";
    public static final int PAYMENT_RETRIES = 2;

    // Survey Constants
    public static final String QUESTION_MULTIPLE_CHOICE = "mcq";
    public static final String QUESTION_RATING = "range";
    public static final int QUESTION_RATING_VALUE_TRUE = 1;
    public static final int QUESTION_RATING_VALUE_FALSE = 0;
    public static final int SURVEY_STAGE_COMPLETE = -1;
    public static final String SURVEY_CUSTOMER_MOOD_SAD = "sad";

    /**
     * Error codes
     */
    public static final int ERROR_CODE_GENERAL = 100;
    public static final int ERROR_CODE_COMPANY_PROFILE_PRECONDITION_FAILURE = 101;
    public static final int ERROR_CODE_COMPANY_PROFILE_SERVICE_FAILURE = 102;
    public static final int ERROR_CODE_REGION_PROFILE_PRECONDITION_FAILURE = 103;
    public static final int ERROR_CODE_REGION_PROFILE_SERVICE_FAILURE = 104;
    public static final int ERROR_CODE_BRANCH_PROFILE_PRECONDITION_FAILURE = 105;
    public static final int ERROR_CODE_BRANCH_PROFILE_SERVICE_FAILURE = 106;
    public static final int ERROR_CODE_REGION_FETCH_PRECONDITION_FAILURE = 107;
    public static final int ERROR_CODE_REGION_FETCH_SERVICE_FAILURE = 108;
    public static final int ERROR_CODE_COMPANY_INDIVIDUALS_FETCH_PRECONDITION_FAILURE = 109;
    public static final int ERROR_CODE_COMPANY_INDIVIDUALS_FETCH_SERVICE_FAILURE = 110;
    public static final int ERROR_CODE_COMPANY_BRANCHES_FETCH_PRECONDITION_FAILURE = 111;
    public static final int ERROR_CODE_COMPANY_BRANCHES_FETCH_SERVICE_FAILURE = 112;
    public static final int ERROR_CODE_BRANCH_INDIVIDUALS_FETCH_PRECONDITION_FAILURE = 113;
    public static final int ERROR_CODE_BRANCH_INDIVIDUALS_FETCH_SERVICE_FAILURE = 114;
    public static final int ERROR_CODE_REGION_BRANCHES_FETCH_PRECONDITION_FAILURE = 115;
    public static final int ERROR_CODE_REGION_BRANCHES_FETCH_SERVICE_FAILURE = 116;
    public static final int ERROR_CODE_REGION_INDIVIDUALS_FETCH_PRECONDITION_FAILURE = 117;
    public static final int ERROR_CODE_REGION_INDIVIDUALS_FETCH_SERVICE_FAILURE = 118;
    public static final int ERROR_CODE_INDIVIDUAL_PROFILE_SERVICE_FAILURE = 119;
    public static final int ERROR_CODE_INDIVIDUAL_PROFILE_SERVICE_PRECONDITION_FAILURE = 120;
    public static final int ERROR_CODE_COMPANY_REVIEWS_FETCH_PRECONDITION_FAILURE = 121;
    public static final int ERROR_CODE_COMPANY_REVIEWS_FETCH_FAILURE = 122;
    public static final int ERROR_CODE_AVERAGE_RATING_FETCH_PRECONDITION_FAILURE = 123;
    public static final int ERROR_CODE_AVERAGE_RATING_FETCH_FAILURE = 124;
    public static final int ERROR_CODE_REVIEWS_COUNT_FETCH_FAILURE = 125;
    public static final int ERROR_CODE_REVIEWS_COUNT_FETCH_PRECONDITION_FAILURE = 126;
    public static final int ERROR_CODE_REGION_REVIEWS_FETCH_PRECONDITION_FAILURE = 127;
    public static final int ERROR_CODE_REGION_REVIEWS_FETCH_FAILURE = 128;
    public static final int ERROR_CODE_BRANCH_REVIEWS_FETCH_PRECONDITION_FAILURE = 129;
    public static final int ERROR_CODE_BRANCH_REVIEWS_FETCH_FAILURE = 130;
    public static final int ERROR_CODE_PRO_LIST_FETCH_PRECONDITION_FAILURE = 131;
    public static final int ERROR_CODE_PRO_LIST_FETCH_FAILURE = 132;
    public static final int ERROR_CODE_INDIVIDUAL_POSTS_FETCH_PRECONDITION_FAILURE = 133;
    public static final int ERROR_CODE_INDIVIDUAL_POSTS_FETCH_FAILURE = 134;
    public static final int ERROR_CODE_COMPANY_POSTS_FETCH_PRECONDITION_FAILURE = 135;
    public static final int ERROR_CODE_COMPANY_POSTS_FETCH_FAILURE = 136;
    public static final int ERROR_CODE_REGION_POSTS_FETCH_PRECONDITION_FAILURE = 137;
    public static final int ERROR_CODE_REGION_POSTS_FETCH_FAILURE = 138;
    public static final int ERROR_CODE_BRANCH_POSTS_FETCH_PRECONDITION_FAILURE = 139;
    public static final int ERROR_CODE_BRANCH_POSTS_FETCH_FAILURE = 140;
    public static final int ERROR_CODE_ENCOMPASS_COMPANY_FETCH_FAILURE = 141;
    public static final int ERROR_CODE_ENCOMPASS_NO_COMPANIES_CONNECTED = 142;

    /**
     * Service codes
     */
    public static final int SERVICE_CODE_GENERAL = 100;
    public static final int SERVICE_CODE_COMPANY_PROFILE = 101;
    public static final int SERVICE_CODE_REGION_PROFILE = 102;
    public static final int SERVICE_CODE_BRANCH_PROFILE = 103;
    public static final int SERVICE_CODE_FETCH_ALL_REGIONS = 104;

    /*
     * Mongo constants
     */
    public static final String COMPANY_SETTINGS_COLLECTION = "COMPANY_SETTINGS";
    public static final int SERVICE_CODE_FETCH_COMPANY_INDIVIDUALS = 105;
    public static final int SERVICE_CODE_FETCH_COMPANY_BRANCHES = 106;
    public static final int SERVICE_CODE_FETCH_BRANCH_INDIVIDUALS = 107;
    public static final int SERVICE_CODE_FETCH_REGION_BRANCHES = 108;
    public static final int SERVICE_CODE_FETCH_REGION_INDIVIDUALS = 109;
    public static final int SERVICE_CODE_INDIVIDUAL_PROFILE = 110;
    public static final int SERVICE_CODE_COMPANY_REVIEWS = 111;
    public static final int SERVICE_CODE_COMPANY_AVERAGE_RATINGS = 112;
    public static final int SERVICE_CODE_COMPANY_REVIEWS_COUNT = 113;
    public static final int SERVICE_CODE_REGION_AVERAGE_RATINGS = 114;
    public static final int SERVICE_CODE_REGION_REVIEWS = 115;
    public static final int SERVICE_CODE_REGION_REVIEWS_COUNT = 115;
    public static final int SERVICE_CODE_BRANCH_AVERAGE_RATINGS = 116;
    public static final int SERVICE_CODE_BRANCH_REVIEWS = 117;
    public static final int SERVICE_CODE_BRANCH_REVIEWS_COUNT = 118;
    public static final int SERVICE_CODE_PRO_LIST_FETCH = 119;
    public static final int SERVICE_CODE_INDIVIDUAL_AVERAGE_RATINGS = 120;
    public static final int SERVICE_CODE_INDIVIDUAL_REVIEWS_COUNT = 121;
    public static final int SERVICE_CODE_INDIVIDUAL_REVIEWS = 122;
    public static final int SERVICE_CODE_INDIVIDUAL_POSTS = 123;
    public static final int SERVICE_CODE_COMPANY_POSTS = 124;
    public static final int SERVICE_CODE_BRANCH_POSTS = 125;
    public static final int SERVICE_CODE_REGION_POSTS = 126;
    public static final int SERVICE_CODE_COMPANY_CRM_INFO = 127;

    /*
     * Mongo column and collection constants
     */
    public static final String SOCIAL_MEDIA_TOKEN_MONGO_KEY = "socialMediaTokens";
    public static final String REGION_SETTINGS_COLLECTION = "REGION_SETTINGS";
    public static final String BRANCH_SETTINGS_COLLECTION = "BRANCH_SETTINGS";
    public static final String AGENT_SETTINGS_COLLECTION = "AGENT_SETTINGS";
    public static final String SOCIAL_POST_COLLECTION = "SOCIAL_POST";
    public static final String SOCIAL_HISTORY_COLLECTION = "SOCIAL_CONNECTION_HISTORY";
    public static final String UPLOAD_HIERARCHY_DETAILS_COLLECTION = "UPLOAD_HIERARCHY_DETAILS";

    /*
     * Mongo social post Source
     */
    public static final String POST_SOURCE_SOCIAL_SURVEY = "SocialSurvey";

    /*
     * Constants related to Dash board profile.
     */
    public static final int MAX_SURVEY_SCORE = 5;
    public static final int MAX_SENT_SURVEY_COUNT = 10;
    public static final int MAX_SOCIAL_POSTS = 10;
    public static final String DATE_FORMAT = "MM/dd/yyyy";

    /*
     * Constats for Find a pro
     */
    public static final int FIND_PRO_START_INDEX = 0;
    public static final int FIND_PRO_BATCH_SIZE = 10;

    // Braintree subscription types
    public static final int SUBSCRIPTION_WENT_PAST_DUE = 1;
    public static final int SUBSCRIPTION_CHARGED_UNSUCCESSFULLY = 2;
    public static final int SUBSCRIPTION_CHARGED_SUCCESSFULLY = 3;

    // Subscription price modification result constants
    public static final String SUBSCRIPTION_PRICE_CHANGED = "SUBSCRIPTION_PRICE_CHANGED";
    public static final String SUBSCRIPTION_OLD_PRICE = "SUBSCRIPTION_OLD_PRICE";
    public static final String SUBSCRIPTION_REVISED_PRICE = "SUBSCRIPTION_REVISED_PRICE";
    public static final String SUBSCRIPTION_REVISED_NUMOFUSERS = "SUBSCRIPTION_REVISED_NUMOFUSERS";

    // Billing Modes
    public static final String BILLING_MODE_AUTO = "A";
    public static final String BILLING_MODE_INVOICE = "I";
    public static final String INVOICE_BILLED_DEFULAT_SUBSCRIPTION_ID = "invoicebilling";

    // API constants
    public static final String API_KEY_FROM_URL = "api_key";

    // Email constants
    public static final String ELEMENTS_DELIMITER = "$$";
    public static final String HEADER_MARKER = "HEADER^^";
    public static final String RECIPIENT_MARKER = "RECIPIENT^^";
    public static final String LINK_MARKER = "LINK^^";
    public static final String URL_MARKER = "URL^^";
    public static final String NAME_MARKER = "NAME^^";
    public static final String FIRSTNAME_MARKER = "FIRSTNAME^^";
    public static final String LASTNAME_MARKER = "LASTNAME^^";
    public static final String RETRYDAYS_MARKER = "RETRYDAYS^^";
    public static final String RETRIES_MARKER = "RETRIES^^";
    public static final String AGENTNAME_MARKER = "AGENTNAME^^";
    public static final String AGENTPHONE_MARKER = "AGENTPHONE^^";
    public static final String AGENTTITLE_MARKER = "AGENTTITLE^^";
    public static final String COMPANYNAME_MARKER = "COMPANYNAME^^";
    public static final String LOGINNAME_MARKER = "LOGINNAME^^";
    public static final String PROFILENAME_MARKER = "PROFILENAME^^";
    public static final String SURVEYDETAIL_MARKER = "SURVEYDETAIL^^";
    public static final String RECIPIENT_NAME_MARKER = "RECIPIENTNAME^^";
    public static final String CUSTOMER_NAME_MARKER = "CUSTOMERNAME^^";
    public static final String CUSTOMER_RATING_MARKER = "CUSTOMERRATING^^";
    public static final String AGENTEMAIL_MARKER = "AGENTEMAIL^^";

    //Constants for user agent profile page
    public static final int USER_AGENT_NUMBER_REVIEWS = 100;
    public static final int USER_AGENT_NUMBER_POST = 100;

    //Default vertcial crm mapping id
    public static final long DEFAULT_VERTICAL_CRM_ID = -1;
    public static final Integer DEFAULT_VERTICAL_ID = -1;

    public static final int EXPIRE_AFTER_DAYS = -1;
    public static final int DECIMALS_TO_ROUND_OFF = 3;

    //Constants for survey request send type
    public static final String SURVEY_REQUEST_AGENT = "agent";
    public static final String SURVEY_REQUEST_ADMIN = "admin";

    // Constants for Agent Ranking Report
    public static final String HEADER_AGENT_RANK = "User Rank";
    public static final String HEADER_FIRST_NAME = "First Name";
    public static final String HEADER_LAST_NAME = "Last Name";
    public static final String HEADER_AVG_SCORE = "Avg Score";
    public static final String HEADER_SUM_SURVEYS = "Sum Surveys";
    public static final String HEADER_REGISTRATION_DATE = "Registration Date";

    // Constants for Survey Results Report
    public static final String HEADER_AGENT_FIRST_NAME = "User First Name";
    public static final String HEADER_AGENT_LAST_NAME = "User Last Name";
    public static final String HEADER_CUSTOMER_FIRST_NAME = "Customer First Name";
    public static final String HEADER_CUSTOMER_LAST_NAME = "Customer Last Name";
    public static final String HEADER_SURVEY_SENT_DATE = "Survey Sent";
    public static final String HEADER_SURVEY_COMPLETED_DATE = "Survey Completed";
    public static final String HEADER_SURVEY_TIME_INTERVAL = "Time Interval";
    public static final String HEADER_SURVEY_SOURCE = "Survey Source";
    public static final String HEADER_SURVEY_SCORE = "Score";
    public static final String HEADER_SURVEY_QUESTION = "Q";
    public static final String HEADER_SURVEY_GATEWAY = "Gateway";
    public static final String HEADER_CUSTOMER_COMMENTS = "Customer Comments";
    public static final String HEADER_AGREED_SHARE = "Agreed to Share";
    public static final String HEADER_CLICK_THROUGH = "Click through";
    public static final String STATUS_YES = "Yes";
    public static final String STATUS_NO = "No";

    // Constants for Social Monitor Report
    public static final String HEADER_POST_COMMENT = "Post Comment";
    public static final String HEADER_POST_DATE = "Post Date";
    public static final String HEADER_POST_SOURCE = "Source";
    public static final String HEADER_POSTED_BY = "Posted By";
    public static final String HEADER_POST_URL = "Post URL";
    public static final String HEADER_POST_LEVEL = "Post Level";
    public static final String HEADER_POST_LEVEL_NAME = "Post Level Name";


    public static final String VALID_USERS_LIST = "validUsersList";

    public static final String INVALID_USERS_LIST = "invalidUsersList";

    public static final String INVALID_USERS_ASSIGN_LIST = "invalidUsersAssignList";

    public static final String BRANCH_OBJECT = "branch";

    public static final String REGION_OBJECT = "region";

    public static final String SURVEY_REMINDER_COUNT = "reminderCount";
    public static final String SURVEY_REMINDER_INTERVAL = "reminderInterval";

    //Session variable
    public static final String REALTECH_USER_ID = "realTechAdminId";
    public static final String COMPANY_ADMIN_SWITCH_USER_ID = "companyAdminSwitchId";
    public static final String REGION_ADMIN_SWITCH_USER_ID = "regionAdminSwitchId";
    public static final String BRANCH_ADMIN_SWITCH_USER_ID = "branchAdminSwitchId";
    public static final String IS_AUTO_LOGIN = "isAutoLogin";

    public static final String PATTERN_FIRST = "patternFirst";

    public static final String PATTERN_LAST = "patternLast";

    public static final String SURVEY_SOURCE_ZILLOW = "Zillow";

    //Excel constants
    public final String EXCEL_FORMAT = "application/vnd.ms-excel";
    public final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    public final String EXCEL_FILE_EXTENSION = ".xlsx";

    public final String EPOCH_REMINDER_TIME = "02/01/1970";

    //Settings constants
    public static final boolean SET_SETTINGS = true;
    public static final boolean UNSET_SETTINGS = false;
    public static final int SET_BY_COMPANY = 1;
    public static final int SET_BY_REGION = 2;
    public static final int SET_BY_COMPANY_N_REGION = 3;
    public static final int SET_BY_BRANCH = 4;
    public static final int SET_BY_COMPANY_N_BRANCH = 5;
    public static final int SET_BY_REGION_N_BRANCH = 6;
    public static final int SET_BY_COMPANY_N_REGION_N_BRANCH = 7;
    public static final int SET_BY_NONE = 0;
    public static final int LOCKED_BY_NONE = 0;

    public static final String SETTING_SCORE = "setScore";

    public static final String LOCK_SCORE = "lockScore";


    public static final String AUTHORIZATION_HEADER = "Bearer ";

    // dotloop db keys
    public static final String KEY_DOTLOOP_PROFILE_ID_COLUMN = "profileId";
    public static final String KEY_DOTLOOP_PROFILE_LOOP_ID_COLUMN = "loopId";

    // Social media message template constants
    public static final DecimalFormat RANKING_FORMAT_TWITTER = new DecimalFormat( "#.#" );
    public static final DecimalFormat SOCIAL_RANKING_FORMAT = new DecimalFormat( "#.#" );
    public static final DecimalFormat SOCIAL_RANKING_WHOLE_FORMAT = new DecimalFormat( "#" );

    public static final String TWITTER_MESSAGE = "%s Star Survey Response from %s for %s on %s ";
    public static final String ZILLOW_TWITTER_MESSAGE = "%s Star response from %s for %s on %s ";

    // file upload types
    public static final int FILE_UPLOAD_HIERARCHY_TYPE = 1;
    public static final int FILE_UPLOAD_SURVEY_TYPE = 2;
    public static final int FILE_UPLOAD_BILLING_REPORT = 3;

    public static final String AGENT_MEDIA_POST_DETAILS_COLUMN = "agentMediaPostDetails";
    public static final String BRANCH_MEDIA_POST_DETAILS_COLUMN = "branchMediaPostDetailsList";
    public static final String COMPANY_MEDIA_POST_DETAILS_COLUMN = "companyMediaPostDetails";
    public static final String REGION_MEDIA_POST_DETAILS_COLUMN = "regionMediaPostDetailsList";

    public static final long REALTECH_ADMIN_ID = 1;

    public static final String BULK_SURVEY_VALID = "Valid";

    public static final String BULK_SURVEY_INVALID = "Invalid";
    
    // aggregate constants
    public static final String AGGREGATE_BY_DAY = "day";
    public static final String AGGREGATE_BY_WEEK = "week";
    public static final String AGGREGATE_BY_MONTH = "month";
    public static final String AGGREGATE_BY_YEAR = "year";
    
    // Survey mood constant
    public static final String SURVEY_MOOD_GREAT = "Great";
    public static final String SURVEY_MOOD_OK = "OK";
    public static final String SURVEY_MOOD_UNPLEASANT = "Unpleasant";
    
    // image types
    public static final String IMAGE_TYPE_LOGO = "logo";
    public static final String IMAGE_TYPE_PROFILE = "profile";

    //URL Type constants
    public static final String MANUAL_REGISTRATION_URL_TYPE = "Manual Registration";
    public static final String SHOW_SURVEY_PAGE_FOR_URL_URL_TYPE = "Show Survey Page For Url";
    public static final String EMAIL_VERIFICATION_URL_TYPE = "Email Verification";
    public static final String SHOW_SURVEY_PAGE_URL_TYPE = "Show Survey Page";
    public static final String SHOW_EMAIL_REGISTRATION_PAGE_URL_TYPE = "Registration Page";
    public static final String SHOW_COMPLETE_REGISTRATION_PAGE_URL_TYPE = "Complete Registration Page";
    public static final String RESET_PASSWORD_URL_TYPE = "Reset Password";
    public static final String VERIFICATION_URL_TYPE = "Verification";
    public static final String UNKNOWN_URL_TYPE = "Unknown";
    public static final int STATUS_NOTACCESSED = 0;
    public static final int STATUS_ACCESSED = 1;
    public static final String SHORTENED_URL_SUFFIX = "mail.do";

    // URL Type constants
    public static final String URL_DETAILS_STATUS_COLUMN = "status";
    public static final String URL_DETAILS_ACCESS_DATES_COLUMN = "accessDates";
    public static final String URL_DETAILS_MODIFIED_ON_COLUMN = "modifiedOn";
    public static final String URL_DETAILS_QUERY_PARAMS_COLUMN = "queryParams";
    
    public static final String URL_PARAM_RESET_PASSWORD = "resetorset";
    public static final String URL_PARAM_RESETORSET_VALUE_RESET = "reset";
    public static final String URL_PARAM_RESETORSET_VALUE_SET = "set";
    

    //Url Details Mongo Column constants
    public static final String URL_COLUMN = "url";
    
    //mark abusive by application constant
    public static final String REPORT_ABUSE_BY_APPLICSTION_NAME = "Application";
    public static final String REPORT_ABUSE_BY_APPLICSTION_EMAIL = "Reported By Application";

    // sendgrid inbound mail status
    public static final String SENDGRID_OK_STATUS = "OK";

    // Forward Mail Details Mongo Column constants
    public static final String SENDER_MAIL_ID_COLUMN = "senderMailId";
    public static final String RECIPIENT_MAIL_ID_COLUMN = "recipientMailId";
    public static final String MESSAGE_ID_COLUMN = "messageId";
    public static final String MESSAGE_HASH_COLUMN = "messageHash";
    public static final String FORWARD_MAIL_DETAILS_STATUS_COLUMN = "status";
    public static final String FORWARD_MAIL_DETAILS_MODIFIED_ON_COLUMN = "modifiedOn";
    
    //Social Media Connections History constants
    public static final String SOCIAL_MEDIA_DISCONNECTED = "disconnected";
    public static final String SOCIAL_MEDIA_CONNECTED = "connected";

    // Zillow Organization Unit Settings Mongo Column constants
    public static final String ZILLOW_REVIEW_COUNT_COLUMN = "zillowReviewCount";
    public static final String ZILLOW_REVIEW_AVERAGE_COLUMN = "zillowReviewAverage";

    // Zillow total score constant
    public static final String ZILLOW_TOTAL_SCORE = "zillowTotalScore";

    // Profile url constants
    public static final String PROFILE_TYPE_COMPANY = "company";
    public static final String PROFILE_TYPE_REGION = "region";
    public static final String PROFILE_TYPE_BRANCH = "branch";
    public static final String PROFILE_TYPE_INDIVIDUAL = "individual";

    // Zillow fetch failure response
    public static final String ZILLOW_FETCH_FAIL_RESPONSE = "{\"zillowCallBreak\":true}";

    // Constants for User Adoption Report
    public static final String HEADER_COMPANY = "Company";
    public static final String HEADER_REGION = "Region";
    public static final String HEADER_BRANCH = "Branch";
    public static final String HEADER_INVITED_USERS = "Invited Users ";
    public static final String HEADER_ACTIVE_USERS = "Active Users";
    public static final String HEADER_ADOPTION_RATES = "Adoption Rates";
    
    
    // Constants for Billing Report
    public static final String HEADER_ADDRESS = "Address";
    public static final String HEADER_USER_ID = "User ID";
    public static final String HEADER_LOGIN_ID = "Login ID";
    public static final String HEADER_PUBLIC_PROFILE_URL = "Public profile URL";
    public static final String HEADER_IS_AGENT = "Is Agent";
    public static final String HEADER_STATE = "State";

    //Zillow connection
    public static final int ZILLOW_CONNECTED = 1;
    public static final int ZILLOW_DISCONNECTED = 0;

    // MySQL IS_ZILLOW_CONNECTED column name constant
    public static final String IS_ZILLOW_CONNECTED_COLUMN = "IS_ZILLOW_CONNECTED";
    
    // sections that can be hidden from public profile page
    public static final String HIDE_RECENT_POSTS = "recent_posts";
    public static final String HIDE_SOCIAL_REVIEW = "social_reviews";

    // Social Post Connection Buttons
    public static final String REALTOR_LABEL = "REALTOR.COM";
    public static final String LENDING_TREE_LABEL = "LENDING TREE";
    public static final String ZILLOW_LABEL = "ZILLOW";
    public static final String YELP_LABEL = "YELP!";
    public static final String GOOGLE_PLUS_LABEL = "GOOGLE+";
    public static final String LINKEDIN_LABEL = "LINKEDIN";
    public static final String TWITTER_LABEL = "TWITTER";
    public static final String FACEBOOK_LABEL = "FACEBOOK";
    
    // Status in mongo for active and inactive records
    public static final String STATUS_ACTIVE_MONGO = "A";
    public static final String STATUS_DELETED_MONGO = "D";

    // Company Hierarchy Report Headers
    public static final String CHR_YES = "Yes";
    public static final String CHR_NO = "No";
    public static final String CHR_USERS_USER_ID = "User ID";
    public static final String CHR_USERS_FIRST_NAME = "First Name";
    public static final String CHR_USERS_LAST_NAME = "Last Name";
    public static final String CHR_USERS_TITLE = "Title";
    public static final String CHR_USERS_OFFICE_ASSIGNMENTS = "Office Assignment(s)";
    public static final String CHR_USERS_REGION_ASSIGNMENTS = "Region Assignment(s)";
    public static final String CHR_USERS_PUBLIC_PROFILE = "Public Profile";
    public static final String CHR_USERS_OFFICE_ADMIN_PRIVILEGE = "Office Admin Privilege(s)";
    public static final String CHR_USERS_REGION_ADMIN_PRIVILEGE = "Region Admin Privilege";
    public static final String CHR_USERS_EMAIL = "Email";
    public static final String CHR_USERS_PHONE = "Phone";
    public static final String CHR_USERS_WEBSITE = "Website";
    public static final String CHR_USERS_LICENSE = "License(s)";
    public static final String CHR_USERS_LEGAL_DISCLAIMER = "Legal Disclaimer";
    public static final String CHR_USERS_PHOTO = "Photo";
    public static final String CHR_USERS_ABOUT_ME_DESCRIPTION = "About Me Description";
    public static final String CHR_USERS_USER_ID_DESC = "Unique ID used for record updates.";
    public static final String CHR_USERS_TITLE_DESC = "Title to be displayed.";
    public static final String CHR_USERS_OFFICE_ASSIGNMENTS_DESC = "List each Office ID from the Offices sheet.";
    public static final String CHR_USERS_REGION_ASSIGNMENTS_DESC = "List each Region ID from the Region sheet.";
    public static final String CHR_USERS_PUBLIC_PROFILE_DESC = "Yes, if they should have a webpage and be surveyable.";
    public static final String CHR_USERS_OFFICE_ADMIN_PRIVILEGE_DESC = "Yes, if they should have access to manage Office Account";
    public static final String CHR_USERS_REGION_ADMIN_PRIVILEGE_DESC = "Yes, if they should have access to manage Region Account";
    public static final String CHR_USERS_EMAIL_DESC = "Email address to send registration request.";
    public static final String CHR_USERS_PHONE_DESC = "Phone number to be publicly displayed.";
    public static final String CHR_USERS_WEBSITE_DESC = "Link to website to be publicly displayed.";
    public static final String CHR_USERS_LICENSE_DESC = "List of licenses to be displayed publicly.";
    public static final String CHR_USERS_LEGAL_DISCLAIMER_DESC = "Plain text legal disclaimer to be displayed on footer of all pages.";
    public static final String CHR_USERS_PHOTO_DESC = "URL of Agents Photo";
    public static final String CHR_USERS_ABOUT_ME_DESCRIPTION_DESC = "A few paragraphs about the user displayed publicly.";
    public static final String CHR_BRANCH_BRANCH_ID = "Office ID";
    public static final String CHR_BRANCH_BRANCH_NAME = "Office Name";
    public static final String CHR_REGION_REGION_ID = "Region ID";
    public static final String CHR_REGION_REGION_NAME = "Region Name";
    public static final String CHR_ADDRESS_1 = "Address 1";
    public static final String CHR_ADDRESS_2 = "Address 2";
    public static final String CHR_CITY = "City";
    public static final String CHR_STATE = "State";
    public static final String CHR_ZIP = "Zip";
    public static final String CHR_ID_DESC = "Unique ID used for assignment.";
    public static final String CHR_BRANCH_REGION_ID_DESC = "Region ID from the Region sheet.";
    public static final String CHR_REGION_REGION_NAME_DESC = "Name to be displayed.";
    
    //API Call details constants
    public static final String EXTERNAL_API_CALL_DETAILS_COLLECTION = "EXTERNAL_API_CALL_DETAILS";
    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_POST = "POST";
    // Error Suffix for the email address already taken
    public static final String EMAIL_ADDRESS_TAKEN_ERROR_SUFFIX = " already taken";


    //Hierarchy Upload constants
    public static final String HIERARCHY_UPLOAD_COLLECTION = "HIERARCHY_UPLOAD";

    public static final String ZILLOW_CALL_REQUEST = "webservice/ProReviews.htm?output=json&returncompletecontent=true&count=50";

    // Zillow Temp Post Table column names
    public static final String ZILLOW_REVIEW_URL_COLUMN = "zillowReviewUrl";
    public static final String ENTITY_COLUMN_NAME_COLUMN = "entityColumnName";
    public static final String ZILLOW_ENTITY_ID_COLUMN = "entityId";
    public static final String ZILLOW_REVIEW_DATE_COLUMN = "zillowReviewDate";

    // Auto Post Tracker Table column names
    public static final String REVIEW_SOURCE_COLUMN = "reviewSource";
    public static final String REVIEW_SOURCE_URL_COLUMN = "reviewSourceUrl";
    public static final String REVIEW_DATE_COLUMN = "reviewDate";

    public static final String HIERARCHY_REGION_HEADERS_INVALID = "Invalid headers in the Regions sheet";

    public static final String HIERARCHY_BRANCH_HEADERS_INVALID = "Invalid headers in the Offices sheet";

    public static final String HIERARCHY_USER_HEADERS_INVALID = "Invalid headers in the Users sheet";

    //Hierarchy upload error constants
    public static final String USER_UPLOAD_ERROR_LIST = "USER_UPLOAD";
    public static final String BRANCH_UPLOAD_ERROR_LIST = "BRANCH_UPLOAD";
    public static final String REGION_UPLOAD_ERROR_LIST = "REGION_UPLOAD";
    public static final String USER_DELETE_ERROR_LIST = "USER_DELETE";
    public static final String BRANCH_DELETE_ERROR_LIST = "BRANCH_DELETE";
    public static final String REGION_DELETE_ERROR_LIST = "REGION_DELETE";
    
    //Hierarchy upload status constants
    
    public static final int HIERARCHY_UPLOAD_ENTITY_INITIATED = 1;
    public static final int HIERARCHY_UPLOAD_ENTITY_STARTED = 2;
    public static final int HIERARCHY_UPLOAD_ENTITY_DONE = 0;
    public static final int HIERARCHY_UPLOAD_ENTITY_ERROR = 3;
    public static final int HIERARCHY_UPLOAD_UPLOAD_COMPLETE = 4;
    public static final int HIERARCHY_UPLOAD_ERROR = 5;
    public static final int HIERARCHY_UPLOAD_NO_UPLOAD = 6;
    
    public static final char UPLOAD_MODE_APPEND = 'A';
    public static final char UPLOAD_MODE_REPLACE = 'R';
    
    //Hierarchy upload message constants
    public static final String UPLOAD_MSG_INITIATED = "Import initiated";
    public static final String UPLOAD_MSG_STARTED = "Import started";
    public static final String UPLOAD_MSG_UPLOADING_REGIONS = "Importing regions";
    public static final String UPLOAD_MSG_UPLOADING_BRANCHES = "Importing offices";
    public static final String UPLOAD_MSG_UPLOADING_USERS = "Importing users";
    public static final String UPLOAD_MSG_DELETING_USERS = "Deleting removed users";
    public static final String UPLOAD_MSG_DELETING_BRANCHES = "Deleting removed offices";
    public static final String UPLOAD_MSG_DELETING_REGIONS = "Deleting removed regions";
    public static final String UPLOAD_MSG_UPLOAD_COMPLETE = "Import successful";
    public static final String UPLOAD_MSG_UPLOAD_ERROR = "Error importing company hierarchy";
    public static final String UPLOAD_MSG_NO_UPLOAD = "";
    public static final String UPLOAD_ADDED_REGIONS = "No. of regions added : ";
    public static final String UPLOAD_MODIFIED_REGIONS = "No. of regions modified : ";
    public static final String UPLOAD_DELETED_REGIONS = "No. of regions deleted : ";
    public static final String UPLOAD_ADDED_BRANCHES = "No. of offices added : ";
    public static final String UPLOAD_MODIFIED_BRANCHES = "No. of offices modified : ";
    public static final String UPLOAD_DELETED_BRANCHES = "No. of offices deleted : ";
    public static final String UPLOAD_ADDED_USERS = "No. of users added : ";
    public static final String UPLOAD_MODIFIED_USERS = "No. of users modified : ";
    public static final String UPLOAD_DELETED_USERS = "No. of users deleted : ";
    
}
