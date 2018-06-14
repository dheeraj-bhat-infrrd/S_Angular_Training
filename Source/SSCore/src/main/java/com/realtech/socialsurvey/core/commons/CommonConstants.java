package com.realtech.socialsurvey.core.commons;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    //default email settings 
    public static final String SENDGRID_SENDER_SOCIALSURVEYME_USERNAME = "SENDGRID_SENDER_SOCIALSURVEYME_USERNAME";
    public static final String SENDGRID_SENDER_SOCIALSURVEYME_PASSWORD = "SENDGRID_SENDER_SOCIALSURVEYME_PASSWORD";
    public static final String SENDGRID_SENDER_SOCIALSURVEYME_NAME = "SENDGRID_SENDER_SOCIALSURVEYME_NAME";
    public static final String SEND_EMAIL_THROUGH_SOCIALSURVEY_ME = "socialsurvey.me";

    //email settings for socialsurvey.us
    public static final String SENDGRID_SENDER_SOCIALSURVEYUS_USERNAME = "SENDGRID_SENDER_SOCIALSURVEYUS_USERNAME";
    public static final String SENDGRID_SENDER_SOCIALSURVEYUS_PASSWORD = "SENDGRID_SENDER_SOCIALSURVEYUS_PASSWORD";
    public static final String SENDGRID_SENDER_SOCIALSURVEYUS_NAME = "SENDGRID_SENDER_SOCIALSURVEYUS_NAME";
    public static final String SEND_EMAIL_THROUGH_SOCIALSURVEY_US = "socialsurvey.us";

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
    public static final int PROFILES_MASTER_SM_ADMIN_PROFILE_ID = 6;
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
    public static final String SET_REGISTRATION_PASSWORD = "/registeraccount/setregistrationpassword.do";

    public static final String LOGIN_URL = "login.do";

    /**
     * Status constants
     */
    public static final int ONE = 1;
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_INACTIVE = 0;
    public static final int STATUS_SURVEY_TEMPLATE = 2;
    public static final int STATUS_NOT_VERIFIED = 2;
    public static final int STATUS_INCOMPLETE = 4;
    public static final int STATUS_UNDER_PROCESSING = 2;
    public static final int STATUS_ACCOUNT_DISABLED = 2;
    public static final int STATUS_COMPANY_DISABLED = 2;
    public static final int STATUS_COMPANY_DELETED = 11;
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
    public static final int DISABLED_ACCOUNT_PROCESSED = 2;
    //Reporting status in file upload table 
    public static final int STATUS_PENDING = 1;
    public static final int STATUS_DONE = 0;
    public static final int STATUS_FAIL = 4; // changing the status failed to 4 since the status under processing is 2
    public static final int STATUS_DELETE = 3;
    public static final int STATUS_VIEW = 5;    


    

    public static final int IS_PRIMARY_FALSE = 0;
    public static final int IS_PRIMARY_TRUE = 1;
    
    public static final int IS_SURVEY_REQUEST_SENT_FALSE = 0;
    public static final int IS_SURVEY_REQUEST_SENT_TRUE = 1;

    //Reporting batch size 
    public static final int BATCH_SIZE = 5000;

    // Survey pre initiation deleted as part company deletion, status is set to 11.
    public static final int SURVEY_STATUS_PRE_INITIATED = 1;
    public static final int STATUS_SURVEYPREINITIATION_PROCESSED = 1;
    public static final int SURVEY_STATUS_INITIATED = 2;
    public static final int STATUS_SURVEYPREINITIATION_CORRUPT_RECORD = 3;
    public static final int STATUS_SURVEYPREINITIATION_NOT_PROCESSED = 4;
    public static final int STATUS_SURVEYPREINITIATION_DUPLICATE_RECORD = 5;
    public static final int STATUS_SURVEYPREINITIATION_COMPLETE = 7;
    public static final int STATUS_SURVEYPREINITIATION_OLD_RECORD = 8;
    public static final int STATUS_SURVEYPREINITIATION_IGNORED_RECORD = 9;
    public static final int STATUS_SURVEYPREINITIATION_DELETED = 0;
    public static final int STATUS_SURVEYPREINITIATION_MISMATCH_RECORD = 10;
    public static final int STATUS_SURVEYPREINITIATION_SURVEY_NOT_ALLOWED = 11;
    public static final int STATUS_SURVEYPREINITIATION_UNSUBSCRIBED = 12;

    public static final int SURVEY_PARTICIPANT_TYPE_BORROWER = 1;
    public static final int SURVEY_PARTICIPANT_TYPE_COBORROWER = 2;
    public static final int SURVEY_PARTICIPANT_TYPE_BUYER_AGENT = 3;
    public static final int SURVEY_PARTICIPANT_TYPE_SELLER_AGENT = 4;


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
    public static final String CUSTOMER_FIRST_NAME_COLUMN = "customerFirstName";
    public static final String ENGAGEMENT_CLOSED_TIME = "engagementClosedTime";
    public static final String BATCH_TYPE_COLUMN = "batchType";
    public static final String SOURCE_COLUMN = "source";
    public static final String SURVEY_PREINITIATION_ID_COLUMN = "surveyPreIntitiationId";
    public static final String HOLD_SENDING_EMAIL_COLUMN = "holdSendingMail";
    public static final String IS_PRIMARY_COLUMN = "isPrimary";
    public static final String SURVEY_LAST_REMINDER_TIME = "lastReminderTime";
    public static final String PROFILE_VALUE_COLUMN = "profileValue";
    public static final String PROFILE_LEVEL_COLUMN = "profileLevel";
    public static final String ENCOMPASS_SDK_VERSION_COLUMN = "sdkVersion";
    public static final String INCLUDE_IN_TRANSACTION_MONITOR = "includeInTransactionMonitor";
    public static final String PROPERTY_ADDRESS = "propertyAddress";
    public static final String LOAN_PROCESSOR_NAME = "loanProcessorName";

    //batch type constant for batch tracker
    public static final String BATCH_TYPE_REVIEW_COUNT_UPDATER = "reviewCountUpdater";
    public static final String BATCH_TYPE_SOCIAL_MONITOR_LAST_BUILD = "socialMonitorLastBuildTime";
    public static final String BATCH_TYPE_APPLICATION_SITE_MAP_GENERATOR = "ApplicationSiteMapGenerator";
    public static final String BATCH_TYPE_EMAIL_READER = "EmailProcessor";
    public static final String BATCH_TYPE_INCOMPLETE_SOCIAL_POST_REMINDER_SENDER = "IncompleteSocialPostReminderSender";
    public static final String BATCH_TYPE_INCOMPLETE_SURVEY_REMINDER_SENDER = "IncompleteSurveyReminderSender";
    public static final String BATCH_TYPE_UPDATE_SUBSCRIPTION_PRICE_STARTER = "updateSubscriptionPriceStarter";
    public static final String BATCH_TYPE_HIDE_USERS_OF_HIDDEN_COMPANIES_IN_SOLR = "hideHiddenCompanyUsersFromSearch";
    public static final String BATCH_TYPE_UPDATE_SUBSCRIPTION_PRICE_STARTER_FOR_ALL_COMPANIES = "updateSubscriptionPriceStarterForAllCompanies";
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
    public static final String BATCH_TYPE_MONTHLY_DIGEST_STARTER = "MonthlyDigestStarter";
    public static final String BATCH_TYPE_SURVEY_CSV_UPLOAD_PROCESSOR = "SurveyCsvUploadProcessor";
    public static final String BATCH_TYPE_TRANSACTION_ACTIVITY_MONITOR = "transactionActivityMonitor";
    public static final String BATCH_TYPE_FTP_FILE_UPLOADER = "FTPFileUploader";

    
    //batch name constant for batch tracker
    public static final String BATCH_NAME_REVIEW_COUNT_UPDATER = "Agent's review count in solr updater";
    public static final String BATCH_NAME_SOCIAL_MONITOR_LAST_BUILD = "Social Post Import In Solr";
    public static final String BATCH_NAME_APPLICATION_SITE_MAP_GENERATOR = "Site Map Generator Batch";
    public static final String BATCH_NAME_EMAIL_READER = "Email Reader And Processor";
    public static final String BATCH_NAME_INCOMPLETE_SOCIAL_POST_REMINDER_SENDER = "incomplete Social Post Reminder Sender";
    public static final String BATCH_NAME_INCOMPLETE_SURVEY_REMINDER_SENDER = "Incomplete Survey Reminder Sender";
    public static final String BATCH_NAME_UPDATE_SUBSCRIPTION_PRICE_STARTER = "Update Subscription Price For Accounts";
    public static final String BATCH_NAME_HIDE_USERS_OF_HIDDEN_COMPANIES_IN_SOLR = "hide Users From Search By Adding a field in user schema for Hidden companies";
    public static final String BATCH_NAME_UPDATE_SUBSCRIPTION_PRICE_STARTER_FOR_ALL_COMPANIES = "Update Subscription Price For All Companies";
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
    public static final String BATCH_NAME_FILE_UPLOAD_REPORTS_GENERATOR = "File Upload And Reports Generator";
    public static final String COMPANIES_BILLING_REPORT_GENERATOR = "Companies Billing Report Generator";
    public static final String BATCH_NAME_ZILLOW_REVIEW_PROCESSOR_AND_AUTO_POSTER = "Zillow review processor and auto poster";
    public static final String BATCH_NAME_HIERARCHY_UPLOAD_PROCESSOR = "Company Hierarchy Upload Processor";
    public static final String BATCH_NAME_MONTHLY_DIGEST_STARTER = "Monthly digest process starter for companies";
    public static final String BATCH_NAME_SURVEY_CSV_UPLOAD_PROCESSOR = "Survey CSV file Upload Processor";
    public static final String BATCH_NAME_TRANSACTION_ACTIVITY_MONITOR = "Transaction Activity Monitor";
    public static final String BATCH_NAME_FTP_FILE_UPLOADER = "FTP files uploader";


    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_BILLING_REPORT = "Billing report";
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_USERS_REPORT = "Company user report";
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_HIERARCHY_REPORT = "Hierarchy report";
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REGISTRATION_REPORT = "Company registration report";
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_SURVEY_DATA_REPORT = "Survey data report";
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_USER_RANKING_REPORT = "User ranking report";
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_SOCIAL_MONITOR_REPORT = "Social monitor report";
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_INCOMPLETE_SURVEY_REPORT = "Incomplete survey data report";
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_USER_ADOPTION_REPORT = "User adoption report";
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_SURVEY_STATS_REPORT = "Survey stats report for reporting";
    //Note -> the USER_ADOPTION_REPORT was renamed to VERIFIED_USERS_REPORT
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_VERIFIED_USERS_REPORT = "Verified users report for reporting";
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_COMPANY_USER_REPORT = "Company user report for reporting";
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_SURVEY_RESULTS_COMPANY_REPORT = "Survey results company report for reporting";
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_SURVEY_TRANSACTION_REPORT = "Survey transaction report for reporting";
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_USER_RANKING_MONTHLY_REPORT = "User ranking monthly report for reporting";
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_USER_RANKING_YEARLY_REPORT = "User ranking yearly report for reporting";
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_NPS_WEEK_REPORT = "NPS weekly report for reporting";
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_NPS_MONTH_REPORT = "NPS monthly report for reporting";
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_BRANCH_RANKING_MONTHLY_REPORT = "Branch Ranking Monthly Report for reporting";
    public static final String BATCH_FILE_UPLOAD_REPORTS_GENERATOR_REPORTING_BRANCH_RANKING_YEARLY_REPORT = "Branch ranking yearly report for reporting";


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
    public static final String MONGO_CLASS_COLUMN = "_class";
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
    public static final String PROFILE_IMAGE_RECTANGULAR_THUMBNAIL_COLUMN = "profileImageUrlRectangularThumbnail";
    public static final String LOGO_THUMBNAIL_COLUMN = "logoThumbnail";
    public static final String IS_PROFILE_IMAGE_PROCESSED_COLUMN = "isProfileImageProcessed";
    public static final String IS_LOGO_IMAGE_PROCESSED_COLUMN = "isLogoImageProcessed";
    public static final String IS_UNMARKED_ABUSIVE_COLUMN = "isUnmarkedAbusive";
    public static final String DELETED_SOCIAL_MEDIA_TOKENS_COLUMN = "deletedSocialTokens";
    public static final String SHOW_SURVEY_ON_UI_COLUMN = "showSurveyOnUI";
    public static final String SUMMARY_COLUMN = "summary";
    public static final String REVIEW_COLUMN = "review";
    public static final String USER_ENCRYPTED_ID = "userEncryptedId";
    public static final String ENCRYPTED_ID = "encryptedId";
    public static final String SURVEY_COMPLETED_DATE_COLUMN = "surveyCompletedDate";
    public static final String SURVEY_UPDATED_DATE_COLUMN = "surveyUpdatedDate";
    public static final String SURVEY_TRANSACTION_DATE_COLUMN = "surveyTransactionDate";
    public static final String ACCOUNT_DISABLE_DATE_COLUMN = "disableDate";
    public static final String IS_FORCE_DELETE_COLUMN = "isForceDelete";
    public static final String SURVEY_LAST_ABUSE_REPORTED_DATE = "lastAbuseReportedDate";
    public static final String NPS_SCORE_COLUMN = "npsScore";
    public static final String ENTITY_ALERT_DETAILS_COLUMN = "entityAlertDetails";
    public static final String RETAKE_SURVEY_COLUMN = "retakeSurvey";
    public static final String NO_OF_RETAKE_COLUMN = "noOfRetake";
    public static final String LAST_RETAKE_REQUEST_DATE_COLUMN = "lastRetakeRequestDate";
    public static final String RETAKE_SURVEY_HISTORY_COLUMN = "retakeSurveyHistory"; 
    public static final String OPEN_RETAKE_SURVEY_REQUEST_COLUMN = "openRetakeSurveyRequest";
    public static final String ABUSIVE_NOTIFY_COLUMN = "abusiveNotify";

    /**
     * Constants to be used in code for referencing variables(i.e in maps or session attributes)
     */
    public static final String ACCOUNT_TYPE_IN_SESSION = "accounttype";
    public static final String CANONICAL_USERSETTINGS_IN_SESSION = "cannonicalusersettings";
    public static final String COMPANY_NAME = "companyName";
    public static final String UNIQUE_IDENTIFIER = "uniqueIdentifier";
    public static final String REFERRAL_CODE = "referralcode";
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
    public static final String USER_ZILLOW_PROFILE_TYPE_NMLS = "nmls";
    public static final String USER_ZILLOW_NMLS_ID = "nmls-id";
    public static final String USER_ZILLOW_PROFILE_TYPE_PROFILENAME = "profileName";
    public static final String USER_ACCOUNT_SETTINGS = "accountSettings";
    public static final String USER_APP_SETTINGS = "appSettings";
    public static final String COMPLAIN_REG_SETTINGS = "complaintRegSettings";
    public static final String ABUSIVE_MAIL_SETTINGS = "abusiveMailSettings";
    public static final String ERROR = "error";
    public static final String ZILLOW_PROFILE_ERROR = "zillow-error";
    public static final String ZILLOW_NMLS_REQUIRED_ERROR = "zillow-nmls-required-error";
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
    public static final String FACEBOOK_PIXEL_ID = "facebookPixelId";
    public static final String FACEBOOK_PIXEL = "facebookPixel";
    public static final String REALTOR_SOCIAL_SITE = "realtor";
    public static final String GOOGLE_BUSINESS_SOCIAL_SITE = "google business";
    public static final String GOOGLE_SOCIAL_SITE = "google";
    public static final String SOCIAL_SURVEY_SOCIAL_SITE = "socialsurvey";
    public static final String INSTAGRAM_SOCIAL_SITE = "instagram";
    public static final String TWITTER_BASE_URL = "www.twitter.com/";
    public static final String COMPANY_ID = "companyId";
    public static final String REGION_ID = "regionId";
    public static final String BRANCH_ID = "branchId";
    public static final String AGENT_ID = "agentId";
    public static final String FLOW_REGISTRATION = "registration";
    public static final String POPUP_FLAG_IN_SESSION = "popupStatus";
    public static final String ACTIVE_SESSIONS_FOUND = "activeSessionFound";
    public static final String NO_GOOGLE_PLUS_FOUND = "nogoogleplusfound";
    public static final String ENCOMPASS_VERSION_LIST = "encompassVersionList";

    public static final String BILLING_MODE_ATTRIBUTE_IN_SESSION = "billingMode";

    public static final String USER_ASSIGNMENTS = "assignments";
    public static final String ENTITY_ID_COLUMN = "entityId";
    public static final String ENTITY_NAME_COLUMN = "entityName";
    public static final String ENTITY_TYPE_COLUMN = "entityType";

    public static final String FILE_UPLOAD_TYPE_COLUMN = "uploadType";

    public static final String SHOW_ON_UI_COLUMN = "showOnUI";

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
    public static final int DEFAULT_MAX_REMINDER_COUNT = 3;
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
    public static final String CRM_INFO_SOURCE_API = "API";
    public static final String CRM_INFO_SOURCE_ENCOMPASS = "encompass";
    public static final String CRM_INFO_SOURCE_FTP = "ftp";
    public static final String CRM_SOURCE_ENCOMPASS = "ENCOMPASS";
    public static final String CRM_SOURCE_DOTLOOP = "DOTLOOP";
    public static final String SURVEY_SOURCE_BULK_UPLOAD = "bulk";
    public static final String SURVEY_SOURCE_3RD_PARTY = "3rd Party Review";
    public static final String SURVEY_SOURCE_FILE_UPLOAD = "upload";
    public static final String SURVEY_SOURCE_FTP = "ftp";
    public static final String CRM_INFO_DRY_RUN_STATE = "dryrun";
    public static final String CRM_INFO_PRODUCTION_STATE = "prod";
    public static final String ENCOMPASS_CLIENT_URL_COLUMN = "clientUrl";
    public static final String ENCOMPASS_USERNAME_COLUMN = "userName";
    public static final String ENCOMPASS_PASSWORD_COLUMN = "password";
    public static final String ENCOMPASS_GENERATE_REPORT_COLUMN = "generateReport";
    public static final String ENCOMPASS_DEFAULT_FEILD_ID = "1997";
    public static final String ENCOMPASS_VERSION_COULMN = "version";
    public static final List<String> CRM_UNVERIFIED_SOURCES =  new ArrayList<>(Arrays.asList(new String[] { "agent" , "admin", "customer"}));

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
    public static final String USER_IS_HIDDEN_FROM_SEARCH_SOLR = "hiddenFromSearchResults";
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
    public static final int PAYMENT_RETRIES = 1;

    // Survey Constants
    public static final String QUESTION_MULTIPLE_CHOICE = "mcq";
    public static final String QUESTION_RATING = "range";
    public static final String QUESTION_0to10 = "0to10";
    public static final int QUESTION_RATING_VALUE_TRUE = 1;
    public static final int QUESTION_RATING_VALUE_FALSE = 0;
    public static final boolean QUESTION_VALUE_TRUE = true;
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
    public static final String TEMP_HIERARCHY_UPLOAD_COLLECTION = "TEMP_HIERARCHY_UPLOAD";
    public static final String SURVEY_CSV_UPLOAD_COLLECTION = "SURVEY_CSV_UPLOAD";
    public static final String FAILED_CLICK_EVENTS_COLLECTION = "FAILED_CLICK_EVENTS";
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
    public static final String DATE_FORMAT_WITH_TZ = "MM/dd/YYYY HH:MM:SS z";

    /*
     * Constats for Find a pro
     */
    public static final int FIND_PRO_START_INDEX = 0;
    public static final int FIND_PRO_BATCH_SIZE = 10;

    // Braintree subscription types
    public static final int SUBSCRIPTION_WENT_PAST_DUE = 1;
    public static final int SUBSCRIPTION_CHARGED_UNSUCCESSFULLY = 2;
    public static final int SUBSCRIPTION_CHARGED_SUCCESSFULLY = 3;
    public static final int SUBSCRIPTION_CANCELED = 4;


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
    public static final String HEADER_FIRST_NAME = "User First Name";
    public static final String HEADER_LAST_NAME = "User Last Name";
    public static final String HEADER_AVG_SCORE = "Avg Score";
    public static final String HEADER_SUM_SURVEYS = "Total Surveys";
    public static final String HEADER_REGISTRATION_DATE = "User Registration Date";
    public static final String HEADER_COMPLETED_SURVEY_COUNT = "Completed Surveys Count";
    public static final String HEADER_INCOMPLETE_SURVEY_COUNT = "Incomplete Surveys Count";

    // Constants for Survey Results Report
    public static final String HEADER_AGENT_FIRST_NAME = "User First Name";
    public static final String HEADER_AGENT_LAST_NAME = "User Last Name";
    public static final String HEADER_CUSTOMER_FIRST_NAME = "Customer First Name";
    public static final String HEADER_CUSTOMER_LAST_NAME = "Customer Last Name";
    public static final String HEADER_SURVEY_SENT_DATE = "Survey Sent";
    public static final String HEADER_SURVEY_COMPLETED_DATE = "Survey Completed";
    public static final String HEADER_SURVEY_TIME_INTERVAL = "Time Interval";
    public static final String HEADER_SURVEY_SOURCE = "Survey Source";
    public static final String HEADER_SURVEY_SOURCE_ID = "Survey Source Id";
    public static final String HEADER_SURVEY_SCORE = "Score";
    public static final String HEADER_SURVEY_QUESTION = "Q";
    public static final String HEADER_SURVEY_GATEWAY = "Gateway";
    public static final String HEADER_CUSTOMER_COMMENTS = "Customer Comments";
    public static final String HEADER_AGREED_SHARE = "Agreed to Share";
    public static final String HEADER_CLICK_THROUGH_FOR_COMPANY = "Click through for company";
    public static final String HEADER_CLICK_THROUGH_FOR_AGENT = "Click through for agent";
    public static final String HEADER_CLICK_THROUGH_FOR_REGIONS = "Click through for regions";
    public static final String HEADER_CLICK_THROUGH_FOR_BRANCHES = "Click through for branches";
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

    public static final String SURVEY_REMINDER_COUNT = "reminderCounts";
    public static final String SURVEY_REMINDER_INTERVAL = "reminderInterval";
    public static final String IS_SURVEY_REMINDER_DISABLED = "isReminderDisabled";

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
    public static final String KEY_DOTLOOP_PROPERTY_ADDRESS = "Property Address";
    public static final String KEY_DOTLOOP_STATE = "stateOrProvince";

    // Social media message template constants
    public static final DecimalFormat RANKING_FORMAT_TWITTER = new DecimalFormat( "#.#" );
    public static final DecimalFormat SOCIAL_RANKING_FORMAT = new DecimalFormat( "#.#" );
    public static final DecimalFormat SOCIAL_RANKING_WHOLE_FORMAT = new DecimalFormat( "#" );

    public static final String TWITTER_MESSAGE = "%s Star Survey Response from %s for %s on %s ";
    public static final String GSF_TWITTER_MESSAGE = "Read what another happy customer has to say about #GoGSF #mortgage %s";
    public static final String ZILLOW_TWITTER_MESSAGE = "%s Star response from %s for %s on %s ";

    // file upload types
    public static final int FILE_UPLOAD_HIERARCHY_TYPE = 1;
    public static final int FILE_UPLOAD_SURVEY_TYPE = 2;
    public static final int FILE_UPLOAD_BILLING_REPORT = 3;
    public static final int FILE_UPLOAD_COMPANY_USERS_REPORT = 4;
    public static final int FILE_UPLOAD_COMPANY_HIERARCHY_REPORT = 5;
    public static final int FILE_UPLOAD_COMPANY_REGISTRATION_REPORT = 6;
    public static final int FILE_UPLOAD_SURVEY_DATA_REPORT = 7;
    public static final int FILE_UPLOAD_USER_RANKING_REPORT = 8;
    public static final int FILE_UPLOAD_SOCIAL_MONITOR_REPORT = 9;
    public static final int FILE_UPLOAD_INCOMPLETE_SURVEY_REPORT = 10;
    public static final int FILE_UPLOAD_USER_ADOPTION_REPORT = 11;

    //reporting file upload status 
    public static final int FILE_UPLOAD_REPORTING_SURVEY_STATS_REPORT = 101;
    //Note -> the USER_ADOPTION_REPORT was renamed to VERIFIED_USERS_REPORT
    public static final int FILE_UPLOAD_REPORTING_VERIFIED_USERS_REPORT = 102;
    public static final int FILE_UPLOAD_REPORTING_COMPANY_USERS_REPORT = 103;
    public static final int FILE_UPLOAD_REPORTING_SURVEY_RESULTS_REPORT = 104;
    public static final int FILE_UPLOAD_REPORTING_SURVEY_TRANSACTION_REPORT = 105;
    public static final int FILE_UPLOAD_REPORTING_USER_RANKING_MONTHLY_REPORT = 106;
    public static final int FILE_UPLOAD_REPORTING_USER_RANKING_YEARLY_REPORT = 107;
    public static final int FILE_UPLOAD_REPORTING_INCOMPLETE_SURVEY_REPORT = 108;
    public static final int FILE_UPLOAD_REPORTING_COMPANY_DETAILS_REPORT = 109;
    public static final int FILE_UPLOAD_REPORTING_NPS_WEEK_REPORT = 110;
    public static final int FILE_UPLOAD_REPORTING_NPS_MONTH_REPORT = 111;
    public static final int FILE_UPLOAD_REPORTING_BRANCH_RANKING_MONTHLY_REPORT = 112;
    public static final int FILE_UPLOAD_REPORTING_BRANCH_RANKING_YEARLY_REPORT = 113;
    
    public static final int FILE_UPLOAD_REPORTING_DIGEST = 200;
    
    //Generating survey invitation email report through storm
    public static final int FILE_UPLOAD_SURVEY_INVITATION_EMAIL_REPORT = 1001;
    
    //Social Monitor reports
    public static final int FILE_UPLOAD_SOCIAL_MONITOR_DATE_REPORT = 301;
    public static final int FILE_UPLOAD_SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD= 302;

    public static final int NPS_REPORT_TYPE_WEEK = 1;
    public static final int NPS_REPORT_TYPE_MONTH = 2;

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

    public static final String URL_PARAM_VERIFICATION_REQUEST_TYPE = "verificationRequestType";
    public static final String URL_PARAM_VERIFICATION_REQUEST_TYPE_TO_ADMIN = "admin";
    public static final String URL_PARAM_VERIFICATION_REQUEST_TYPE_TO_USER = "user";


    public static final String URL_PARAM_RETAKE_SURVEY = "retakeSurvey";


    //Url Details Mongo Column constants
    public static final String URL_COLUMN = "url";

    //mark abusive by application constant
    public static final String REPORT_ABUSE_BY_APPLICATION_NAME = "Application";
    public static final String REPORT_ABUSE_BY_APPLICATION_EMAIL = "Reported By Application";

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
    public static final String GOOGLE_BUSINESS_LABEL = "GOOGLE BUSINESS";
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
    public static final String STATUS_INCOMPLETE_MONGO = "I";

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
    public static final String CHR_USERS_SEND_EMAIL = "Send Email";
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
    public static final String CHR_1 = "1";
    public static final String CHR_2 = "2";
    public static final String CHR_CITY = "City";
    public static final String CHR_STATE = "State";
    public static final String CHR_ZIP = "Zip";
    public static final String CHR_ID_DESC = "Unique ID used for assignment.";
    public static final String CHR_BRANCH_REGION_ID_DESC = "Region ID from the Region sheet.";
    public static final String CHR_REGION_REGION_NAME_DESC = "Name to be displayed.";
    public static final String CHR_FACEBOOK = "Facebook";
    public static final String CHR_TWITTER = "Twitter";
    public static final String CHR_LINKEDIN = "Linkedin";
    public static final String CHR_GOOGLE = "Google";
    public static final String CHR_ZILLOW = "Zillow";
    public static final String CHR_YELP = "Yelp";
    public static final String CHR_LENDING_TREE = "Lendingtree";
    public static final String CHR_REALTOR = "Realtor";
    public static final String CHR_GOOGLE_BUSINESS = "Google Business";

    public static final String SOCIAL_SURVEY_ACCESS_LAVEL = "SocialSurvey Access Level ";
    public static final String SOCIAL_SURVEY_INVITE_SENT = "SocialSurvey Invite sent";
    public static final String DATE_LAST_INVITE_SENT = "Date last invite sent";
    public static final String PROFILE_VERIFIED = "Profile Verified ";
    public static final String DATE_OF_LAST_LOGIN = "Date of last log-in";
    public static final String PROFILE_COMPLETE = "Profile Complete ";
    public static final String SOCIALLY_CONNECTED = "Socially Connected";
    public static final String DATE_CONNECTION_ESTABLISHED = "Date connection established";
    public static final String CONNECTION_STATUS = "Connection Status";
    public static final String DATE_OF_LAST_POST = "Date of last post";
    public static final String DATE_ADOPTION_COMPLETED = "Date Adoption completed";
    public static final String DATE_LAST_SURVEY_SENT = "Date last survey sent";
    public static final String DATE_LAST_SURVEY_POSTED = "Date last survey posted";
    public static final String USER_ADDRESS = "User Address";

    //user report mail subject and body
    public static final String COMPANY_USERS_REPORT_MAIL_SUBJ = "Company Users Report for ";
    public static final String COMPANY_USERS_REPORT_MAIL_BODY = "Here is the company users report you requested. Please refer to the attachment for the report.";

    //API Call details constants
    public static final String EXTERNAL_API_CALL_DETAILS_COLLECTION = "EXTERNAL_API_CALL_DETAILS";
    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_POST = "POST";
    // Error Suffix for the email address already taken
    public static final String EMAIL_ADDRESS_TAKEN_ERROR_SUFFIX = " already taken";


    //Hierarchy Upload constants
    public static final String HIERARCHY_UPLOAD_COLLECTION = "HIERARCHY_UPLOAD";

    public static final String ZILLOW_CALL_REQUEST = "webservice/ProReviews.htm?output=json&returncompletecontent=true&count=50";


    public static final String ZILLOW_PROFILE_URL = "http://zillow.com/profile/";
    public static final String ZILLOW_LENDER_PROFILE_URL = "http://zillow.com/lender-profile/";

    public static final List<String> ZILLOW_LENDING_VERTICALS = new ArrayList<String>( Arrays.asList( "Mortgage" ) );

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


    //Company registration stage
    public static final String COMPANY_REGISTRATION_STAGE_STARTED = "The registration has been initiated";
    public static final String COMPANY_REGISTRATION_STAGE_COMPLETE = "The registration has been completed successfully";
    public static final String COMPANY_REGISTRATION_STAGE_PAYMENT_PENDING = "The registration has been initiated, but no payment has been made";

    public static final String ENCOMPASS_CONNECTION = "ENCOMPASS_CONNECTION";
    public static final String LONEWOLF_CONNECTION = "LONEWOLF_CONNECTION";
    public static final String ACTION_ENABLED = "ENABLED";
    public static final String ACTION_DISABLED = "DISABLED";

    public static final int UNMATCHED_USER_TABID = 1;
    public static final int PROCESSED_USER_TABID = 2;
    public static final int MAPPED_USER_TABID = 3;
    public static final int CORRUPT_USER_TABID = 4;

    public static final String ACCOUNT_REGISTER = "ACCREG";
    public static final int ENTERPRISE_PLAN_ID = 3;

    public static final String PLAN_ID = "planId";

    public static final String BATCH_TYPE_LONE_WOLF_REVIEW_PROCESSOR = "loneWolfReviewProcessor";
    public static final String BATCH_NAME_LONE_WOLF_REVIEW_PROCESSOR = "Lone Wolf Review Processor";
    public static final String CRM_SOURCE_LONEWOLF = "LONEWOLF";

    // loan wol crm info
    public static final String LONEWOLF_DRY_RUN_STATE = "dryrun";
    public static final String LONEWOLF_PRODUCTION_STATE = "prod";

    public static final String BATCH_TYPE_SOCIAL_MEDIA_TOKEN_EXPIRY_SCHEDULER = "socialMediaTokenExpiryScheduler";
    public static final String BATCH_NAME_SOCIAL_MEDIA_TOKEN_EXPIRY_SCHEDULER = "Social Media Token Expiry Scheduler";

    //loan wolf api parameter
    public static final String LONEWOLF_QUERY_PARAM_$TOP = "$top";
    public static final String LONEWOLF_QUERY_PARAM_$FILTER = "$filter";
    public static final String LONEWOLF_QUERY_PARAM_$ORDERBY = "$OrderBy";
    public static final String LONEWOLF_QUERY_PARAM_$SKIP = "$skip";

    public static final String LONEWOLF_QUERY_PARAM_ORDERBY_VALUE = "CloseDate+desc";
    public static final int LONEWOLF_TRANSACTION_API_BATCH_SIZE = 50;

    //Update Social Media From Email
    public static final String SOCIAL_AUTH_FROM_EMAIL_URL = "rest/socialauthfromemail.do";
    public static final String URL_PARAM_SOCIAL_MEDIA_TYPE = "social"; //Defile Social Media
    public static final String URL_PARAM_COLUMN_NAME = "columnName"; //Entity Type
    public static final String URL_PARAM_COLUMN_VALUE = "columnValue"; //Entity ID
    public static final String URL_PARAM_ACCOUNT_MASTER_ID = "accountMasterId"; //For Account Type

    public static final Object SOCIAL_SURVEY_PROFILE_URL = "SocialSurvey Profile";
    public static final Object TOTAL_REVIEWS = "Total Reviews";
    public static final Object SOCIAL_SURVEY_REVIEWS = "SocialSurvey Reviews";
    public static final Object ZILLOW_REVIEWS = "Zillow Reviews";
    public static final Object ABUSIVE_REVIEWS = "Abusive Reviews";
    public static final Object THIRD_PARTY_REVIEWS = "3rd Party Reviews";

    //vendasta constants
    public static final String VENDASTA_ACCESS = "vendastaAccessible";
    public static final String VENDASTA_REPUTATION_MANAGEMENT_ID = "RM";
    public static final String VENDASTA_SSO_TICKET_ID_COLUMN = "vendastaSingleSignOnTicketId";
    public static final String VENDASTA_SSO_TICKET_COLUMN = "vendastaSingleSignOnTicket";
    public static final String VENDASTA_SSO_TOKEN_COLUMN = "vendastaSingleSignOnToken";
    public static final String VENDASTA_PRODUCT_ID_COLUMN = "productId";
    public static final String VENDASTA_STATUS_COLUMN = "status";
    public static final String VENDASTA = "vendasta";

    //vendasta batch constants
    public static final String BATCH_TYPE_PROCESSED_SSO_TICKET_REMOVER = "ssoTicketRemover";
    public static final String BATCH_NAME_PROCESSED_SSO_TICKET_REMOVER = "remove used vendasta sso tickets";
    public static final int VENDASTA_TICKET_EXPIRED = 1;

    //survey API
    public static final String SURVEY_API_REQUEST_PARAMETER_AUTHORIZATION = "Authorization";
    public static final int SURVEY_API_DEFAUAT_BATCH_SIZE = 1000;
    public static final String SURVEY_API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    // upload related constants
    public static final String DUPLICATE_SOURCE_ID_SUBSTRING = "_duplicate_";
    public static final String PARSED_HIERARCHY_UPLOAD_COLLECTION = "PARSED_HIERARCHY_UPLOAD";

    // upload related status v2.0
    public static final int HIERARCHY_UPLOAD_STATUS_NEW_ENTRY = -1;
    public static final int HIERARCHY_UPLOAD_STATUS_INITIATED = 0;
    public static final int HIERARCHY_UPLOAD_STATUS_VERIFING = 1;
    public static final int HIERARCHY_UPLOAD_STATUS_VERIFIED_WITH_GENERAL_ERRORS = 2;
    public static final int HIERARCHY_UPLOAD_STATUS_VERIFIED_WITH_ERRORS_OR_WARNINGS = 3;
    public static final int HIERARCHY_UPLOAD_STATUS_IMPORTING = 4;
    public static final int HIERARCHY_UPLOAD_STATUS_IMPORTED_WITH_ERRORS = 5;
    public static final int HIERARCHY_UPLOAD_STATUS_IMPORTED = 6;
    public static final int HIERARCHY_UPLOAD_STATUS_UNSCHEDULED_ABORT = 7;
    public static final int HIERARCHY_UPLOAD_STATUS_VERIFIED_SUCCESSFULLY = 8;
    
    
    //Reporting constants
    public static final String REPORTING_SURVEY_STATS_REPORT = "Survey Stats Report";
    //Note -> the USER_ADOPTION_REPORT was renamed to VERIFIED_USERS_REPORT
    public static final String REPORTING_VERIFIED_USERS_REPORT = "Verified Users Report";
    public static final String REPORTING_COMPANY_USERS_REPORT = "Company User Report";
    public static final String REPORTING_SURVEY_RESULTS_COMPANY_REPORT = "Survey Results Report";
    public static final String REPORTING_SURVEY_TRANSACTION_REPORT = "Survey Transaction Report";
    public static final String REPORTING_USER_RANKING_MONTHLY_REPORT = "User Ranking Report For Month";
    public static final String REPORTING_USER_RANKING_YEARLY_REPORT = "User Ranking Report For Year";
    public static final String REPORTING_INCOMPLETE_SURVEY_REPORT = "Incomplete Survey Results Report";
    public static final String REPORTING_NPS_REPORT_FOR_WEEK = "NPS Report for Week";
    public static final String REPORTING_NPS_REPORT_FOR_MONTH = "NPS Report for Month";
    public static final String REPORTING_NPS_REPORT = "NPS Report";
    public static final String REPORTING_API_DATE_FORMAT = "MMM dd, yyyy";
    public static final String SURVEY_INVITATION_EMAIL_REPORT = "Survey Invitation Email Report";
    public static final String REPORTING_BRANCH_RANKING_MONTHLY_REPORT = "Branch Ranking Report For Month";
    public static final String REPORTING_BRANCH_RANKING_YEARLY_REPORT = "Branch Ranking Report For Year";
    
    public static final String REPORTING_DIGEST = "Monthly Digest";
    
    public static final String SOCIAL_MONITOR_DATE_REPORT = "Social Monitor Date based Report";
    public static final String SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD = "Social Monitor Date Report for keyword";

    public static final String SURVEY_DETAILS_ID_COLUMN = "surveyDetailsId";
    public static final String SURVEY_RESULTS_REPORT_MODIFIED_ON = "reportModifiedOn";
    public static final String SURVEY_RESULTS_IS_DELETED = "isDeleted";
    public static final String TRX_MONTH = "trxMonth";
    public static final String SURVEY_RESULTS_COMPLETED_DATE = "surveyCompletedDate";

    public static final String THIS_MONTH="thisMonth";
    public static final String THIS_YEAR="thisYear";
    public static final String LEADERBOARD_YEAR="year";
    public static final String LEADERBOARD_MONTH="month";
    public static final String IS_ELIGIBLE = "isEligible";
    
    public static final String RANK = "rank";
    public static final String INTERNAL_BRANCH_RANK = "internalBranchRank";
    public static final String INTERNAL_REGION_RANK = "internalRegionRank";

    public static final String MONTH_VAL = "monthVal";
    public static final String YEAR_VAL = "yearVal";
    public static final String QUESTION_ID = "questionId";
    public static final long DEFAULT_QUESTION_ID = -1;
    
    public static final String TRANSACTION_MONITOR_DATE_COLUMN = "transactionDate";
    public static final String SURVEY_STATS_MONITOR_DATE_COLUMN = "statsDate";

    //Survey API survey status
    public static final String SURVEY_API_SURVEY_STATUS_COMPLETE = "complete";
    public static final String SURVEY_API_SURVEY_STATUS_INCOMPLETE = "incomplete";
    public static final String SURVEY_API_SURVEY_STATUS_ALL = "all";


    public static final String NOT_AVAILABLE = "N/A";

    //Encompass test connection htt prequest initial
    public static final String HTTP_REQUEST_URL_INITIAL = "http://";

    //Ranking Requirements Defaults
    public static final float MIN_COMPLETED_PERCENTAGE = 40.0f;
    public static final int MIN_DAYS_OF_REGISTRATION = 90;
    public static final int MIN_NO_OF_REVIEWS = 25;
    public static final int MONTH_OFFSET = 3;
    public static final int YEAR_OFFSET = -1;


    public static final String SQUARE_THUMBNAIL = "squareThumbnail";
    public static final String RECTANGULAR_THUMBNAIL = "rectangularThumbnail";

    //Digest
    public static final String MONTH = "month";
    public static final String YEAR = "year";

    public static final String SURVEY_MAIL_THRESHOLD = "surveyCompletedMailThreshold";

    // Email Constants
    public static final String EMAIL_TYPE_REGISTRATION_INVITATION_MAIL = "REGISTRATION_INVITATION_MAIL";
    public static final String EMAIL_TYPE_NEW_REGISTRATION_INVITATION_MAIL = "NEW_REGISTRATION_INVITATION_MAIL";
    public static final String EMAIL_TYPE_COMPANY_REGISTRATION_STAGE_MAIL = "COMPANY_REGISTRATION_STAGE_MAIL";
    public static final String EMAIL_TYPE_AGENT_SURVEY_REMINDER_MAIL = "AGENT_SURVEY_REMINDER_MAIL";
    public static final String EMAIL_TYPE_RESET_PASSWORD_EMAIL = "RESET_PASSWORD_EMAIL";
    public static final String EMAIL_TYPE_INVITATION_TO_SOCIALSURVEY_ADMIN_EMAIL = "INVITATION_TO_SOCIALSURVEY_ADMIN_EMAIL";
    public static final String EMAIL_TYPE_SUBSCRIPTION_CHARGE_UNSUCCESSFUL_EMAIL = "SUBSCRIPTION_CHARGE_UNSUCCESSFUL_EMAIL";
    public static final String EMAIL_TYPE_EMAIL_VERIFICATION_MAIL = "EMAIL_VERIFICATION_MAIL";
    public static final String EMAIL_TYPE_EMAIL_VERIFICATION_REQUESTMAIL_TO_ADMIN_MAIL = "EMAIL_VERIFICATION_REQUESTMAIL_TO_ADMIN_MAIL";
    public static final String EMAIL_TYPE_EMAIL_VERIFIED_NOTIFICATION_MAIL = "EMAIL_VERIFIED_NOTIFICATION_MAIL";
    public static final String EMAIL_TYPE_EMAIL_VERIFIED_NOTIFICATION_MAIL_TO_ADMIN_MAIL = "EMAIL_VERIFIED_NOTIFICATION_MAIL_TO_ADMIN_MAIL";
    public static final String EMAIL_TYPE_VERIFICATION_MAIL = "VERIFICATION_MAIL";
    public static final String EMAIL_TYPE_REGISTRATION_COMPLETION_EMAIL = "REGISTRATION_COMPLETION_EMAIL";
    public static final String EMAIL_TYPE_FATAL_EXCEPTION_EMAIL = "FATAL_EXCEPTION_EMAIL";
    public static final String EMAIL_TYPE_EMAILSENDING_FAILURE_MAIL = "EMAILSENDING_FAILURE_MAIL";
    public static final String EMAIL_TYPE_RETRY_CHARGE_EMAIL = "RETRY_CHARGE_EMAIL";
    public static final String EMAIL_TYPE_RETRY_EXHAUSTED_EMAIL = "RETRY_EXHAUSTED_EMAIL";
    public static final String EMAIL_TYPE_ACCOUNT_DISABLED_MAIL = "ACCOUNT_DISABLED_MAIL";
    public static final String EMAIL_TYPE_ACCOUNT_DELETION_MAIL = "ACCOUNT_DELETION_MAIL";
    public static final String EMAIL_TYPE_ACCOUNT_UPGRADE_MAIL = "ACCOUNT_UPGRADE_MAIL";
    public static final String EMAIL_TYPE_DEFAULT_SURVEY_COMPLETION_MAIL = "DEFAULT_SURVEY_COMPLETION_MAIL";
    public static final String EMAIL_TYPE_DEFAULT_SURVEY_COMPLETION_UNPLEASANT_MAIL = "DEFAULT_SURVEY_COMPLETION_UNPLEASANT_MAIL";
    public static final String EMAIL_TYPE_DEFAULT_SURVEY_REMINDER_MAIL = "DEFAULT_SURVEY_REMINDER_MAIL";
    public static final String EMAIL_TYPE_SURVEY_REMINDER_MAIL = "SURVEY_REMINDER_MAIL";
    public static final String EMAIL_TYPE_SURVEY_COMPLETION_TO_ADMINS_AND_AGENT_MAIL = "SURVEY_COMPLETION_TO_ADMINS_AND_AGENT_MAIL";
    public static final String EMAIL_TYPE_DEFAULT_SOCIAL_POST_REMINDER_MAIL = "DEFAULT_SOCIAL_POST_REMINDER_MAIL";
    public static final String EMAIL_TYPE_CONTACT_US_MAIL = "CONTACT_US_MAIL";
    public static final String EMAIL_TYPE_DEFAULT_SURVEY_INVITATION_MAIL = "DEFAULT_SURVEY_INVITATION_MAIL";
    public static final String EMAIL_TYPE_SURVEY_INVITATION_MAIL = "SURVEY_INVITATION_MAIL";
    public static final String EMAIL_TYPE_SURVEY_COMPLETION_MAIL = "SURVEY_COMPLETION_MAIL";
    public static final String EMAIL_TYPE_SURVEY_COMPLETION_UNPLEASANT_MAIL = "SURVEY_COMPLETION_UNPLEASANT_MAIL";
    public static final String EMAIL_TYPE_SURVEY_SOCIALPOST_REMINDER_MAIL = "SURVEY_SOCIALPOST_REMINDER_MAIL";
    public static final String EMAIL_TYPE_SURVEY_RESTART_MAIL = "SURVEY_RESTART_MAIL";
    public static final String EMAIL_TYPE_ACCOUNT_BLOCKING_MAIL = "ACCOUNT_BLOCKING_MAIL";
    public static final String EMAIL_TYPE_ACCOUNT_REACTIVATION_MAIL = "ACCOUNT_REACTIVATION_MAIL";
    public static final String EMAIL_TYPE_SUBSCRIPTION_REVISION_MAIL = "SUBSCRIPTION_REVISION_MAIL";
    public static final String EMAIL_TYPE_MANUAL_REGISTRATION_LINK_MAIL = "MANUAL_REGISTRATION_LINK_MAIL";
    public static final String EMAIL_TYPE_DEFAULT_SURVEY_INVITATION_MAIL_BY_CUSTOMER_MAIL = "DEFAULT_SURVEY_INVITATION_MAIL_BY_CUSTOMER_MAIL";
    public static final String EMAIL_TYPE_SURVEY_INVITATION_MAIL_BY_CUSTOMER = "SURVEY_INVITATION_MAIL_BY_CUSTOMER";
    public static final String EMAIL_TYPE_DEFAULT_SURVEY_RESTART_MAIL = "DEFAULT_SURVEY_RESTART_MAIL";
    public static final String EMAIL_TYPE_SOCIAL_CONNECT_MAIL = "SOCIAL_CONNECT_MAIL";
    public static final String EMAIL_TYPE_REPORT_ABUSE_MAIL = "REPORT_ABUSE_MAIL";
    public static final String EMAIL_TYPE_SURVEY_REPORT_MAIL = "SURVEY_REPORT_MAIL";
    public static final String EMAIL_TYPE_CORRUPT_DATA_FROM_CRM_NOTIFICATION_MAIL = "CORRUPT_DATA_FROM_CRM_NOTIFICATION_MAIL";
    public static final String EMAIL_TYPE_INVALID_EMAILS_NOTIFICATION_MAIL = "INVALID_EMAILS_NOTIFICATION_MAIL";
    public static final String EMAIL_TYPE_RECORDS_NOT_UPLOADED_CRM_NOTIFICATION_MAIL = "RECORDS_NOT_UPLOADED_CRM_NOTIFICATION_MAIL";
    public static final String EMAIL_TYPE_HELP_MAIL_TO_ADMIN = "HELP_MAIL_TO_ADMIN";
    public static final String EMAIL_TYPE_ZILLOW_CALL_EXCEEDED_MAIL_TO_ADMIN = "ZILLOW_CALL_EXCEEDED_MAIL_TO_ADMIN";
    public static final String EMAIL_TYPE_REPORT_BUG_MAIL_TO_ADMIN = "REPORT_BUG_MAIL_TO_ADMIN";
    public static final String EMAIL_TYPE_REPORT_BUG_MAIL_TO_ADMIN_FOR_EXCEPTION_IN_BATCH = "REPORT_BUG_MAIL_TO_ADMIN_FOR_EXCEPTION_IN_BATCH";
    public static final String EMAIL_TYPE_COMPLAINT_HANDLE_MAIL = "COMPLAINT_HANDLE_MAIL";
    public static final String EMAIL_TYPE_ZILLOW_REVIEW_COMPLAINT_HANDLE_MAIL = "ZILLOW_REVIEW_COMPLAINT_HANDLE_MAIL";
    public static final String EMAIL_TYPE_FORWARD_CUSTOMER_REPLY_MAIL = "FORWARD_CUSTOMER_REPLY_MAIL";
    public static final String EMAIL_TYPE_BILLING_REPORT_MAIL = "BILLING_REPORT_MAIL";
    public static final String EMAIL_TYPE_CUSTOM_MAIL = "CUSTOM_MAIL";
    public static final String EMAIL_TYPE_CUSTOM_REPORT_MAIL = "CUSTOM_REPORT_MAIL";
    public static final String EMAIL_TYPE_SOCIAL_MEDIA_TOKEN_EXPIRY_EMAIL = "SOCIAL_MEDIA_TOKEN_EXPIRY_EMAIL";
    public static final String EMAIL_TYPE_PAYMENT_FAILED_ALERT_EMAIL = "PAYMENT_FAILED_ALERT_EMAIL";
    public static final String EMAIL_TYPE_PAYMENT_FAILED_ALERT_EMAIL_TO_ADMIN = "PAYMENT_FAILED_ALERT_EMAIL_TO_ADMIN";
    public static final String EMAIL_TYPE_CANCEL_SUBSCRIPTION_REQUEST_ALERT_MAIL = "CANCEL_SUBSCRIPTION_REQUEST_ALERT_MAIL";
    public static final String EMAIL_TYPE_WEB_EXCEPTION_EMAIL = "WEB_EXCEPTION_EMAIL";
    public static final String EMAIL_TYPE_NO_TRANSACTION_ALERT_MAIL = "NO_TRANSACTION_ALERT_MAIL";
    public static final String EMAIL_TYPE_HIGH_VOULME_UNPROCESSED_TRANSACTION_ALERT_MAIL = "HIGH_VOULME_UNPROCESSED_TRANSACTION_ALERT_MAIL";
    public static final String EMAIL_TYPE_LESS_VOULME_OF_TRANSACTION_RECEIVED_ALERT_MAIL = "LESS_VOULME_OF_TRANSACTION_RECEIVED_ALERT_MAIL";
    public static final String EMAIL_TYPE_MONTHLY_DIGEST_MAIL = "MONTHLY_DIGEST_MAIL";
    public static final String EMAIL_TYPE_DIGEST_ERROR_MAIL_FOR_COMPANY = "DIGEST_ERROR_MAIL_FOR_COMPANY";
    public static final String EMAIL_TYPE_UNSUCCESSFUL_SURVEY_CSV_UPLOAD_MAIL_TO_ADMIN = "UNSUCCESSFUL_SURVEY_CSV_UPLOAD_MAIL_TO_ADMIN";
    public static final String EMAIL_TYPE_UNSUCCESSFUL_SURVEY_CSV_UPLOAD_MAIL_TO_UPLOADER = "UNSUCCESSFUL_SURVEY_CSV_UPLOAD_MAIL_TO_UPLOADER";
    public static final String EMAIL_TYPE_SUCCESSFUL_SURVEY_CSV_UPLOAD_MAIL_TO_UPLOADER = "SUCCESSFUL_SURVEY_CSV_UPLOAD_MAIL_TO_UPLOADER";
    public static final String EMAIL_TYPE_SOCIAL_MONITOR_ACTION_MAIL_TO_USER = "EMAIL_TYPE_SOCIAL_MONITOR_ACTION_MAIL_TO_USER";
    public static final String EMAIL_TYPE_ABUSIVE_HANDLE_MAIL = "ABUSIVE_HANDLE_MAIL";
    public static final String EMAIL_TYPE_USER_ADDITION_MAIL = "USER_ADDITION_MAIL";
    public static final String EMAIL_TYPE_USER_DELETION_MAIL = "USER_DELETION_MAIL";
    public static final String EMAIL_TYPE_FTP_FILE_UPLOADER = "FTP_FILE_UPLOADER";
    public static final String EMAIL_TYPE_FTP_SUCCESSFULLY_PROCESSED_MAIL = "FTP_SUCCESSFULLY_PROCESSED_MAIL";
    
    public static final String REDIRECT = "redirect:/";
    public static final String EVENT_CLICK = "click";

    public static final String COLLECTION_TYPE = "collectionType";

    public static final String AGENT_COLUMN = "agent";

    public static final String PROFILE_URL = "profileUrl";

    public static final String OFFICE = "office";

    public static final String GOOGLE_CAPTCHA_RESPONSE = "g-recaptcha-response";

    public static final String INVALID_CAPTCHA = "invalidCaptcha";
    
    //trnsaction monitor alert type
    public static final String ALERT_TYPE_ERROR = "error";
    public static final String ALERT_TYPE_WARNING = "warning";
    public static final String ALERT_TYPE_NORMAL = "normal";

    public static final String COMMA_SEPERATOR_PATTERN = "\\s*,\\s*";
 

    //JobLogDetails
    public static final String JOB_LOG_ID = "jobLogId";
    public static final String JOB_NAME = "jobName";
    public static final String STATUS_DUMMY = "Dummy";
    public static final String STATUS_FINISHED = "Finished";
    public static final String STATUS_RUNNING = "Running";
    public static final String CENTRALIZED_JOB_NAME = "CentralizedMainJob";
    public static final String REPORTING_JOB_NAME = "ReportingMainJob";
    public static final String USER_RANKING_JOB_NAME = "UserRankingJob";
    public static final String IS_MANUAL = "isManual";
    

    public static final String TIMEZONE_EST = "EST5EDT";
    
    //retake request 
    //Constants for survey request send type
    public static final String RETAKE_REQUEST_AGENT = "agent";
    public static final String RETAKE_REQUEST_CUSTOMER = "customer";
    
    public static final String SURVEY_RESPONSE_QUESTION = "surveyResponse.question";
    public static final String SURVEY_RESPONSE_QUESTION_TYPE = "surveyResponse.questionType";
    public static final String QUESTION_TYPE_MCQ = "sb-sel-mcq";

    //failed stream message mongo batch size
    public static final  int FAILED_STREAM_MSGS_BATCH_SIZE = 100;

    public static final String DIGEST_USER_RANKING_COLUMN = "Company Ranking";

    public static final String DIGEST_USER_NAME_COLUMN = "Name";

    public static final String DIGEST_AVG_SCORE_COLUMN = "Average Score";

    public static final String DIGEST_REVIEWS_COLUMN = "Reviews";

    public static final String DIGEST_USER_RANKING_TITLE = "Top Users";

    public static final String DIGEST_USER_RANKING_DESC =  "Congratulations to your top performers";

    public static final int DEFAULT_NPS_QUESTION_ORDER = 999;

    public static final String DIGEST_NPS_SECTION_TITLE = "NPS Rating";

    public static final String DIGEST_NPS_SECTION_DESC = "Your NPS rating is calculated by Promoters minus detractors based on the response to the NPS question over Total surveys completed that included the NPS question.";

    public static final String TOTAL = "Total";

    public static final String RIGHT = "right";

    public static final String CENTER = "center";

    public static final String LEFT = "left";

    public static final String DIGEST_MAIL_NPS_TEXT = "NPS";

    public static final int STATUS_REPORT_NO_RECORDS = 3;
    
    // Time frames
    public static final String TIME_FRAME_PAST_MONTH = "PastMonth";
    public static final String TIME_FRAME_THIS_MONTH = "ThisMonth";
    public static final String TIME_FRAME_ALL_TIME = "AllTime";
    
    //social monitor constants
    public static final String PROFILE_TYPE = "profileType";

    public static final String CALLBACK = "callback";

    public static final String FTP_SERVER_ONE = "FTP1";
    public static final String FTP_SERVER_TWO = "FTP2";

    public static final String UTF_8_ENCODING = "UTF-8";

    public static final String OS_LINUX = "linux";

    //Unsubscribed Email Constatns SS-1547
    public static final int STATUS_UNSUBSCRIBED = 1;
    public static final int STATUS_RESUBSCRIBED = 2;
    public static final int LEVEL_APPLICATION = 1;
    public static final int LEVEL_COMPANY = 2;
    public static final String LEVEL_COLUMN = "level";
    public static final String UNSUBSCRIBE_URL = "rest/unsubscribe/customeremail";
    public static final String STATUS_SS_USER_ADMIN = "Can not unsubscribe. Email id belongs to social survey user or admin.";
    public static final String STATUS_ALREADY_UNSUBSCRIBED = "Can not unsubscribe. Email id has already been unsubscribed.";
    public static final String STATUS_SUCCESS_UNSUBSCRIBE = "Email id successfully unsubscribed.";
    public static final String STATUS_ALREADY_RESUBSCRIBED = "Can not resubscribe. Email id already resubscribed.";
    public static final String STATUS_NOT_IN_UNSUBSCRIBED_LIST = "Can not resubscribe. Email id not unsubscribed before.";
    public static final String STATUS_SUCCESS_RESUBSCRIBE = "Email id successfully resubscribed.";
    public static final String STATUS_UNSUBSCRIBE_FAILED = "FAILED TO UNSUBSCRIBE";
}
