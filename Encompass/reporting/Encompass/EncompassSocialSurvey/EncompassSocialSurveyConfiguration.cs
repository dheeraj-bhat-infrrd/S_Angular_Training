using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EncompassSocialSurvey
{
    public class EncompassSocialSurveyConfiguration
    {
        // var sendgridUsername = System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurverConstant.SENDGRID_USERNAME];

        public static string SendgridUsername
        {
            get
            {
                return System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.SENDGRID_USERNAME];
            }
        }

        // var sendgridPassword = System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurverConstant.SENDGRID_PASSWORD];

        public static String SendgridPassword
        {
            get
            {
                return System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.SENDGRID_PASSWORD];
            }
        }

        // var adminEmailAddress = System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurverConstant.ADMIN_EMAIL_ADDRESS];

        public static String AdminEmailAddress
        {
            get
            {
                return System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.ADMIN_EMAIL_ADDRESS];
            }
        }

        // var sendgridFromAddress = System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurverConstant.SENDGRID_FROM_ADDRESS];

        public static String SendgridFromAddress
        {
            get
            {
                return System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.SENDGRID_FROM_ADDRESS];
            }
        }

        // var sendgridName = System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurverConstant.SENDGRID_FROM_NAME];

        public static String SendgridName
        {
            get
            {
                return System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.SENDGRID_FROM_NAME];
            }
        }


        public static int DefaultDaysIntervalToFetch
        {
            get
            {
                return Int32.Parse(System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.DEFAULT_DAYS_INTERVAL]);
            }
        }

        public static long SummitCompanyId
        {
            get
            {
                return long.Parse(System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.SUMMIT_ID]);
            }
        }


        public static string TempFolderPath
        {
            get
            {
                return System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.TEMP_FOLDER_PATH];
            }
        }

        public static string fetchCompanyCredentialsURL
        {
            get
            {
                return System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.FETCH_COMPANY_CREDENTIALS_URL];
            }
        }

        public static string disableGenerateReportUrl
        {
            get
            {
                return System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.DISABLE_GENERATE_REPORT_URL];
            }
        }

        public static string sendErrorMailUrl
        {
            get
            {
                return System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.SEND_ERROR_MAIL_URL];
            }
        }

        public static string sendErrorNotificationUrl
        {
            get
            {
                return System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.SEND_ERROR_NOTIFICATION_URL];
            }
        }

        public static int MaxNoOfParallelThreads
        { 
            get
            {
               return Int32.Parse(System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.MAX_NO_OF_PARALLEL_THREADS]);
            }
        }

        public static string fetchEncompassVersion
        {
            get
            {
                return System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.ENCOMPASS_VERSION];
            }
        }

        public static string fetchCompanyTypeDeterminant
        {
            get
            {
                return System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.COMPANY_TYPE_DETERMINANT];
            }
        }

        public static string fetchSsapiUrl
        {
            get
            {
                return System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.SSAPI_URL];
            }
        }
    }
}
