using EllieMae.Encompass.Collections;
using EncompassSocialSurvey.Entity;
using System;
using System.Collections.Generic;

namespace EncompassSocialSurvey
{
    public class EncompassGlobal
    {
        public EllieMae.Encompass.Client.Session EncompassLoginSession { get; set; }

        public string EncompassUserName { get; set; }
        public string EncompassPassword { get; set; }
        public string EncompassUrl { get; set; }
        private StringList fieldIds = null;

        //
        // public static ArrayList LoanFolders { get; set; }

        /// <summary>
        /// Set the current user credentials to the global objects
        /// </summary>
        /// <param name="companyCredential"></param>
        public void GetUserLoginSesssion(CompanyCredential companyCredential)
        {
            Logger.Info("Entering the method EncompassGlobal.GetUserLoginSesssion()");
            try
            {
                // if user name or password field is empty return
                if (string.IsNullOrWhiteSpace(companyCredential.EncompassCredential.UserName)) return;
                if (string.IsNullOrWhiteSpace(companyCredential.EncompassCredential.Password)) return;

                // now set the user name  password
                EncompassUserName = companyCredential.EncompassCredential.UserName;
                EncompassPassword = companyCredential.EncompassCredential.Password;
                EncompassUrl = companyCredential.EncompassCredential.EncompassUrl;
                
                // Start the session
                EllieMae.Encompass.Client.Session s = new EllieMae.Encompass.Client.Session();



                // 
                if (companyCredential.EncompassCredential.EncompassUrl == "")
                    s.StartOffline(companyCredential.EncompassCredential.UserName, companyCredential.EncompassCredential.Password);

                else
                    s.Start(companyCredential.EncompassCredential.EncompassUrl, companyCredential.EncompassCredential.UserName, companyCredential.EncompassCredential.Password);

                // set the static object
                EncompassLoginSession = s;
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: EncompassGlobal.GetUserLoginSesssion(): ", ex);
                throw ex;
            }
            Logger.Info("Exiting the method EncompassGlobal.GetUserLoginSesssion()");
        }

        public StringList InitialFieldList()
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

    }
}
