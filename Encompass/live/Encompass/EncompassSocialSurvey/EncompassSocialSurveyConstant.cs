
using System;
using System.Configuration;
using EllieMae.Encompass.Collections;

namespace EncompassSocialSurvey
{
    public class EncompassSocialSurveyConstant
    {
        public const string SURVEY_SOURCE = "encompass";

        public const string COMPANY_INACTIVE = "0";

        public const string SETUP_ENVIRONMENT = "ss_environment";

        public const string EMAIL_DOMAIN_REPLACEMENT = "email_domain_to_replace";

        public const string EMAIL_DOMAIN_PREFIX = "email_address_prefix";

        public const string SETUP_ENVIRONMENT_TEST = "test";

        public const string SENDGRID_USERNAME = "sendgird_username_property";

        public const string SENDGRID_FROM_NAME = "sendgird_name_property";

        public const string SENDGRID_FROM_ADDRESS = "sendgird_from_address_property";

        public const string SENDGRID_PASSWORD = "sendgird_password_property";

        public const string ADMIN_EMAIL_ADDRESS = "application_admin_email";

        public const string DEFAULT_DAYS_INTERVAL = "default_days_interval";

        public const string TEMP_FOLDER_PATH = "temp_folder_path";

        public const string FETCH_COMPANY_CREDENTIALS_URL = "fetch_company_credentials_url";

        public const string DISABLE_GENERATE_REPORT_URL = "disable_generate_report_url";

        public const string SEND_ERROR_MAIL_URL = "send_error_mail_url";

        public const string SEND_ERROR_NOTIFICATION_URL = "send_error_notification_url";

        public const string MAX_NO_OF_PARALLEL_THREADS = "MaxNoOfParallelThreads";
        //Summit Company ID
        public const string SUMMIT_ID = "summit_company_id";

        
        // MM/dd/yyyy
        public const string LAST_REMINDER_TIME = "01/01/1970";
        public const string DEFAULT_ENGAGEMENT_CLOSE_TIME = "01/01/1970";
        public static readonly DateTime EPOCH_TIME = new DateTime(1970, 1, 2, 0, 0, 0);

        //TODO: Convert this into a map
        //Starting Date Time for Summit
        public static readonly DateTime SUMMIT_BEGIN_TIME = new DateTime(2016, 7, 16, 0, 0, 0);
        
        public const int REMINDER_COUNT = 0;
        public const int STATUS = 4;

        public const int PARTICIPANT_TYPE_BORROWER = 1;
        public const int PARTICIPANT_TYPE_CO_BORROWER = 2;
        public const int PARTICIPANT_TYPE_LISTING_AGENT = 3;
        public const int PARTICIPANT_TYPE_BUYER_AGENT = 4;


        public const int DAYS_BEFORE = 3;

        public const string COMPANY_CREDENTIALS_CRM_SOURCE = "encompass";

        //
        public const string MongoCompanyInfoCollection = "MongoCompanyInfoCollection";


        //fetch company type
        public const string companyRecordTypeSaveData = "prod";
        public const string companyRecordTypeGenerateReport = "generateReport";

        //rest call constant
        public const string fetchCompaniesUrlParameterState = "state";

        public const string fetchCompaniesUrlParameterStateProd = "prod";

        public const string fetchCompaniesUrlParameterStateDryRun = "dryrun";

        public const string fetchCompaniesUrlParameterVersion = "version";

        public const string ENCOMPASS_VERSION = "encompass_version";

        //field list index

        public const int BUYER_AGENT_EMAIL_INDEX = 11;

        public const int BUYER_AGENT_NAME_INDEX = 12;

        public const int SELLER_AGENT_EMAIL_INDEX = 13;

        public const int SELLER_AGENT_NAME_INDEX = 14;

        public const int PROPERTY_ADDRESS_INDEX = 15;

        public const int LOAN_PROCESSOR_NAME_INDEX = 16;

        public const int LOAN_PROCESSOR_EMAIL_INDEX = 17;

        public const int CUSTOM_FIELD_ONE_INDEX = 18;

        public const int CUSTOM_FIELD_TWO_INDEX = 19;

        public const int CUSTOM_FIELD_THREE_INDEX = 20;

        public const int CUSTOM_FIELD_FOUR_INDEX = 21;

        public const int CUSTOM_FIELD_FIVE_INDEX = 22;

        public const string SOCIAL_SURVEY_ENCRYPTION_SALT = "6f90b8d50f490e647d92e2a74d2c44d7";

        public const string SOCIAL_SURVEY_ENCRYPTION_KEY = "";

        public const string COMPANY_TYPE_DETERMINANT = "companyTypeDeterminant";

        public const string SSAPI_URL = "ssapiUrl";

        //Encompass errors
        public const string INVALID_PWD_MESSAGE = "The password for Encompass is either incorrect or has expired, please reset the password and contact your CSM or support@socialsurvey.com";
        public const string VERSION_MISMATCH_MESSAGE = "Encompass connection failed due to version mismatch, if you recently upgraded/downgraded encompass version please inform your CSM or email support@socialsurvey.com";
        public const string USER_NOTFOUND_MESSAGE = "Encompass connection failed as the credentials provided are incorrect, please confirm the socialsurvey user exists with appropriate permissions and contact your CSM or support@socialsurvey.com";
        public const string USER_LOCKED_MESSAGE = "Encompass connection failed as the user access for socialsurvey has been locked, please reset the password for socialsurvey user in encompass and contact your CSM or email support@socialsurvey.com";
        public const string TECHNICAIL_ERROR_MESSAGE = "A technical issue caused encompass connection to fail, please contact your CSM or support@socialsurvey.com for further details";


        // UTF-8 code page
        public const int CIPHER_BYTE_CODING_TYPE = 65001;


        public static string GetAppSettingsValue(string key)
        {
            Logger.Info("EncompassSocialSurverConstant:GetAppSettingsValue() : Enter into Method");
            try
            {
                string searchValue = ConfigurationManager.AppSettings[key];
                Logger.Info("EncompassSocialSurverConstant:GetAppSettingsValue() : Exit from method");

                return searchValue;
            }
            catch (Exception e)
            {
                Logger.Error("EncompassSocialSurverConstant:GetAppSettingsValue() : Caught an Error " + e);
                throw;
            }
        }
    }
}
