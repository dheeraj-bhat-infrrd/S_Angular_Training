
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

        public const string MAX_NO_OF_PARALLEL_THREADS = "MaxNoOfParallelThreads";

        
        // MM/dd/yyyy
        public const string LAST_REMINDER_TIME = "01/01/1970";
        public const string DEFAULT_ENGAGEMENT_CLOSE_TIME = "01/01/1970";
        public static readonly DateTime EPOCH_TIME = new DateTime(1970, 1, 2, 0, 0, 0);
        public const int REMINDER_COUNT = 0;
        public const int STATUS = 4;

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
