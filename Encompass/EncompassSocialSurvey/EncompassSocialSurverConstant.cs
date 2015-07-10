
using System;
using System.Configuration;
namespace EncompassSocialSurvey
{
    public class EncompassSocialSurverConstant
    {
        public const string SURVEY_SOURCE = "encompass";
        
        // MM/dd/yyyy
        public const string LAST_REMINDER_TIME = "01/01/1970";
        public const int REMINDER_COUNT = 0;
        public const int STATUS = 2;

        public const string COMPANY_CREDENTIALS_CRM_SOURCE = "encompass";

        //
        public const string MongoCompanyInfoCollection = "MongoCompanyInfoCollection";

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
