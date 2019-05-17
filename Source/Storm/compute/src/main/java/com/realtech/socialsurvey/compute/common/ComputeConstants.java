package com.realtech.socialsurvey.compute.common;

/**
 * Constants for the application
 * @author nishit
 *
 */
public final class ComputeConstants
{
    private ComputeConstants()
    {}

    public static final String SEND_EMAIL_THROUGH_SOCIALSURVEY_ME = "socialsurvey.me";
    public static final String SEND_EMAIL_THROUGH_SOCIALSURVEY_US = "socialsurvey.us";

    // sendgrid keys
    public static final String SENDGRID_ME_API_KEY = "sendgrid.me.api.key";
    public static final String SENDGRID_US_API_KEY = "sendgrid.us.api.key";

    //amazon keys
    public static final String AMAZON_ACCESS_KEY = "amazon.access.key";
    public static final String AMAZON_SECRET_KEY = "amazon.secret.key";

    // property files
    public static final String APPLICATION_PROPERTY_FILE = "application";

    // Runtime params map
    public static final String RUNTIME_PARAMS = "RUNTIME_PARAMS";
    public static final String PROFILE = "profile";

    // database properties
    public static final String MONGO_DB_URI = "MONGO_DB_URI";
    public static final String STREAM_DATABASE = "stream_db";

    // API end point properties
    public static final String SOLR_API_ENDPOINT = "SOLR_API_ENDPOINT";
    
    // Zookeper end point
    public static final String ZOOKEEPER_BROKERS_ENDPOINT = "ZOOKEEPER_BROKERS_ENDPOINT";
    
    // SS api end point
    public static final String SS_API_ENDPOINT = "SS_API_ENDPOINT";

    // Misc properties
    public static final String SALES_LEAD_EMAIL_ADDRESS = "SALES_LEAD_EMAIL_ADDRESS";
    public static final String ADMIN_EMAIL_ADDRESS = "ADMIN_EMAIL_ADDRESS";
    public static final String ADMIN_EMAIL_ADDRESS_NAME = "ADMIN_EMAIL_ADDRESS_NAME";
    public static final String SS_DATABASE = "ss_db";
    public static final String FACEBOOK_API_ENDPOINT = "FACEBOOK_API_ENDPOINT";
    
    public static final String LINKED_IN_REST_API_URI = "LINKED_IN_REST_API_URI";
    
    public static final String MEDIA_TOKENS_FETCH_TIME_INTERVAL = "MEDIA_TOKENS_FETCH_TIME_INTERVAL";

    // Amazon S3 constants
    public static final String AMAZON_BUCKET = "AMAZON_BUCKET";
    public static final String AMAZON_REPORTS_BUCKET = "AMAZON_REPORTS_BUCKET";
    public static final String AMAZON_ENDPOINT = "AMAZON_ENDPOINT";
    
    public static final String FILEUPLOAD_DIRECTORY_LOCATION = "FILEUPLOAD_DIRECTORY_LOCATION";
    public static final String REDIS_HOST = "REDIS_HOST";
    public static final String REDIS_PORT = "REDIS_PORT";
    
    // kafka properties
    public static final String BROKER_URL = "BROKER_URL";
    public static final String SOCIAL_POST_TOPIC = "SOCIAL_POST_TOPIC";

    // Twitter consumer keys
    public static final String TWITTER_CONSUMER_KEY = "twitter.consumer.key";
    public static final String TWITTER_CONSUMER_SECRET = "twitter.consumer.secret";
    
    public static final String TWITTER_FIRST_RETRIEVAL_COUNT = "twitter.first.retrieval.count";
    
    //Solr Pivots
    public static final String SOLR_PIVOT_AGENT_EMAIL_ATTEMPT = "agentId,emailAttemptedDate";
    public static final String SOLR_PIVOT_AGENT_DELIVERED = "agentId,emailDeliveredDate";
    public static final String SOLR_PIVOT_AGENT_DIFFERED = "agentId,emailDefferedDate";
    public static final String SOLR_PIVOT_AGENT_BLOCKED = "agentId,emailBlockedDate";
    public static final String SOLR_PIVOT_AGENT_OPENED = "agentId,emailOpenedDate";
    public static final String SOLR_PIVOT_AGENT_SPAMED = "agentId,emailMarkedSpamDate";
    public static final String SOLR_PIVOT_AGENT_UNSUBSCRIBED = "agentId,emailUnsubscribeDate";
    public static final String SOLR_PIVOT_AGENT_BOUNCED = "agentId,emailBounceDate";
    public static final String SOLR_PIVOT_AGENT_LINK_CLICKED = "agentId,emailLinkClickedDate";
    public static final String SOLR_PIVOT_AGENT_DROPPED = "agentId,emailDroppedDate";

    public static int BATCH_SIZE = 5000;
    
    public static final String AUTH_HEADER = "social.monitor.auth.header";

    //social media token fields
    public static final String FACEBOOK_TOKEN_EXPIRY_FIELD = "socialMediaTokens.facebookToken.tokenExpiryAlertSent";
    public static final String LINKEDIN_TOKEN_EXPIRY_FIELD = "socialMediaTokens.linkedInToken.tokenExpiryAlertSent";
    public static final String INSTAGRAM_TOKEN_EXPIRY_FIELD = "socialMediaTokens.instagramToken.tokenExpiryAlertSent";

    //social media lastfetched fields
    public static final String FBREVIEW_LAST_FETCHED_FIELD = "socialMediaLastFetched.fbReviewLastFetched.";

    public static final String SOCIAL_MONITOR_REPORT_BATCH_SIZE = "SOCIAL_MONITOR_REPORT_BATCH_SIZE";

    //Download from S3 to local temp
    public static final String LOCAL_DIR_LOCATION = "LOCAL_DIR_LOCATION";
    
    //ftp batching
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String FTP_SURVEY_BATCH_SIZE = "FTP_SURVEY_BATCH_SIZE";
    
    public static final String PROFILE_LEVEL_COMPANY  = "COMPANY";
    public static final String PROFILE_LEVEL_REGION  = "REGION";
    public static final String PROFILE_LEVEL_BRANCH  = "BRANCH";
    public static final String PROFILE_LEVEL_INDIVIDUAL  = "INDIVIDUAL";
    
    public static String WIDGET_RESOURCES_URL = "WIDGET_RESOURCES_URL";
    
    public static final String LAST_BATCH_TIME_GOOOGLE = "last_batch_time_google";
    public static final String GMB_ACCOUNT = "GMB_ACCOUNT";
    public static final String REGION_SETTINGS_COLLECTION = "REGION_SETTINGS";
    public static final String BRANCH_SETTINGS_COLLECTION = "BRANCH_SETTINGS";
    public static final String AGENT_SETTINGS_COLLECTION = "AGENT_SETTINGS";
    public static final String COMPANY_SETTINGS_COLLECTION = "COMPANY_SETTINGS";
    
    public static final String TWILIO_STATUS_CALLBACK_URL = "TWILIO_STATUS_CALLBACK_URL";
    public static final String TWILIO_ACCOUNT_SID = "TWILIO_ACCOUNT_SID";
    public static final String TWILIO_AUTH_TOKEN = "TWILIO_AUTH_TOKEN";
    public static final String TWILIO_FROM_NUMBER = "TWILIO_FROM_NUMBER";
    public static final String TWILIO_BODY_PARAM_KEY = "Body";
    public static final String TWILIO_FROM_PARAM_KEY = "From";
    public static final String TWILIO_TO_PARAM_KEY = "To";
    public static final String TWILIO_STATUS_CALLBACK_URL_PARAM_KEY = "StatusCallback";

}
