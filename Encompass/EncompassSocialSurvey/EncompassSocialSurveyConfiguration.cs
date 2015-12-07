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
                return System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurverConstant.SENDGRID_USERNAME];
            }
        }

        // var sendgridPassword = System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurverConstant.SENDGRID_PASSWORD];

        public static String SendgridPassword
        {
            get
            {
                return System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurverConstant.SENDGRID_PASSWORD];
            }
        }

        // var adminEmailAddress = System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurverConstant.ADMIN_EMAIL_ADDRESS];

        public static String AdminEmailAddress
        {
            get
            {
                return System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurverConstant.ADMIN_EMAIL_ADDRESS];
            }
        }

        // var sendgridFromAddress = System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurverConstant.SENDGRID_FROM_ADDRESS];

        public static String SendgridFromAddress
        {
            get
            {
                return System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurverConstant.SENDGRID_FROM_ADDRESS];
            }
        }

        // var sendgridName = System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurverConstant.SENDGRID_FROM_NAME];

        public static String SendgridName
        {
            get
            {
                return System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurverConstant.SENDGRID_FROM_NAME];
            }
        }


    }
}
