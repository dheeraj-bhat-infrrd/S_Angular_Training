﻿
using System;
using System.Configuration;
using EllieMae.Encompass.Collections;

namespace EncompassSocialSurvey
{
    public class EncompassSocialSurverConstant
    {
        public const string SURVEY_SOURCE = "encompass";

        public const string SETUP_ENVIRONMENT = "ss_environment";

        public const string EMAIL_DOMAIN_REPLACEMENT = "email_domain_to_replace";

        public const string EMAIL_DOMAIN_PREFIX = "email_address_prefix";

        public const string SETUP_ENVIRONMENT_TEST = "test";

        private static StringList fieldIds = null;
        
        // MM/dd/yyyy
        public const string LAST_REMINDER_TIME = "01/01/1970";
        public const string DEFAULT_ENGAGEMENT_CLOSE_TIME = "01/01/1970";
        public static readonly DateTime EPOCH_TIME = new DateTime(1970, 1, 2, 0, 0, 0);
        public const int REMINDER_COUNT = 0;
        public const int STATUS = 4;

        public const string COMPANY_CREDENTIALS_CRM_SOURCE = "encompass";

        //
        public const string MongoCompanyInfoCollection = "MongoCompanyInfoCollection";

        public static StringList initialFieldList()
        {
            if (fieldIds == null)
            {
                Logger.Debug("Initializing initialFieldList");
                fieldIds = new StringList();
                fieldIds.Add("364");         // Loan Number
                fieldIds.Add("LoanTeamMember.Name.Loan Officer"); // Loan Processor Name
                fieldIds.Add("36");          // Customer First Name
                fieldIds.Add("37");          // Customer Last Name
                fieldIds.Add("1240");        // CustomerEmailId

                fieldIds.Add("68");    // Co-BorrowerFirstName
                fieldIds.Add("69");    // Co-BorrowerLastName
                fieldIds.Add("1268");  // Co-BorrowerEmailId
                fieldIds.Add("748"); // closed date
            }
            return fieldIds;
        }

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
