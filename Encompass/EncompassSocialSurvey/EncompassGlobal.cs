using EncompassSocialSurvey.Entity;
using System;
using System.Collections.Generic;

namespace EncompassSocialSurvey
{
    public class EncompassGlobal
    {
        public static EllieMae.Encompass.Client.Session EncompassLoginSession { get; set; }

        public static string EncompassUserName { get; set; }
        public static string EncompassPassword { get; set; }
        public static string EncompassUrl { get; set; }

        //
        // public static ArrayList LoanFolders { get; set; }

        /// <summary>
        /// Set the current user credentials to the global objects
        /// </summary>
        /// <param name="companyCredential"></param>
        public static void GetUserLoginSesssion(CompanyCredential companyCredential)
        {
            Logger.Info("Entering the method EncompassGlobal.GetUserLoginSesssion()");
            try
            {
                // if user name or password field is empty return
                if (string.IsNullOrWhiteSpace(companyCredential.UserName)) return;
                if (string.IsNullOrWhiteSpace(companyCredential.Password)) return;

                // now set the user name  password
                EncompassGlobal.EncompassUserName = companyCredential.UserName;
                EncompassGlobal.EncompassPassword = companyCredential.Password;
                EncompassGlobal.EncompassUrl = companyCredential.EncompassUrl;

                // Start the session
                EllieMae.Encompass.Client.Session s = new EllieMae.Encompass.Client.Session();



                // 
                if (companyCredential.EncompassUrl == "")
                    s.StartOffline(companyCredential.UserName, companyCredential.Password);
                else
                    s.Start(companyCredential.EncompassUrl, companyCredential.UserName, companyCredential.Password);

                // set the static object
                EncompassGlobal.EncompassLoginSession = s;
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: EncompassGlobal.GetUserLoginSesssion(): ", ex);
                String Subject = "Error while connecting to encompass";
                String BodyText = "An error has been occurred while connecting to encompass for company with id : " + companyCredential.CompanyId + " on " + DateTime.Now + ".";
                BodyText += ex.Message;
                CommonUtility.SendMailToAdmin(Subject, BodyText);
                throw ex;
            }
            Logger.Info("Exiting the method EncompassGlobal.GetUserLoginSesssion()");
        }

    }
}
