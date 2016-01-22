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
                if (string.IsNullOrWhiteSpace(companyCredential.EncompassCredential.UserName)) return;
                if (string.IsNullOrWhiteSpace(companyCredential.EncompassCredential.Password)) return;

                // now set the user name  password
                EncompassGlobal.EncompassUserName = companyCredential.EncompassCredential.UserName;
                EncompassGlobal.EncompassPassword = companyCredential.EncompassCredential.Password;
                EncompassGlobal.EncompassUrl = companyCredential.EncompassCredential.EncompassUrl;

                // Start the session
                EllieMae.Encompass.Client.Session s = new EllieMae.Encompass.Client.Session();



                // 
                if (companyCredential.EncompassCredential.EncompassUrl == "")
                    s.StartOffline(companyCredential.EncompassCredential.UserName, companyCredential.EncompassCredential.Password);
                else
                    s.Start(companyCredential.EncompassCredential.EncompassUrl, companyCredential.EncompassCredential.UserName, companyCredential.EncompassCredential.Password);

                // set the static object
                EncompassGlobal.EncompassLoginSession = s;
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: EncompassGlobal.GetUserLoginSesssion(): ", ex);
                String Subject = "Error while connecting to encompass";
                String BodyText = "An error has been occurred while connecting to encompass for company : " + companyCredential .CompanyName + " with id : " + companyCredential.EncompassCredential.CompanyId + " on " + DateTime.Now + ".";
                BodyText += ex.Message;
                CommonUtility.SendMailToAdmin(Subject, BodyText);
                throw ex;
            }
            Logger.Info("Exiting the method EncompassGlobal.GetUserLoginSesssion()");
        }

    }
}
