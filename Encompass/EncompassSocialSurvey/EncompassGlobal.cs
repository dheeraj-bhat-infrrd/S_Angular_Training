using EllieMae.Encompass.Collections;
using EncompassSocialSurvey.Entity;
using EncompassSocialSurvey.Service;
using System;
using System.Collections.Generic;
using System.Net.Http;

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
                // set up cipher service
                CipherService cipherService = new CipherService();

                // if user name or password field is empty return
                if (string.IsNullOrWhiteSpace(companyCredential.EncompassCredential.UserName)) return;
                if (string.IsNullOrWhiteSpace(companyCredential.EncompassCredential.Password)) return;

                // now set the user name  password( encrypted )
                EncompassUserName = companyCredential.EncompassCredential.UserName;
                EncompassPassword = cipherService.decrypt( companyCredential.EncompassCredential.Password, "");
                EncompassUrl = companyCredential.EncompassCredential.EncompassUrl;
                
                // Start the session
                EllieMae.Encompass.Client.Session s = new EllieMae.Encompass.Client.Session();

                // 
                if (companyCredential.EncompassCredential.EncompassUrl == "")
                    s.StartOffline(EncompassUserName, EncompassPassword);

                else
                    s.Start(EncompassUrl, EncompassUserName, EncompassPassword);

                // set the static object
                EncompassLoginSession = s;
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: EncompassGlobal.GetUserLoginSesssion(): ", ex);
                processMismatchError( companyCredential, ex);
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
                fieldIds.Add("14"); // State
                fieldIds.Add("12"); // City
            }
            return fieldIds;
        }


        private void processMismatchError( CompanyCredential companyCredential, Exception error)
        {
            Logger.Debug("checking for mismatch errors");
            try
            {
                if (error.Message.IndexOf("version mismatch", StringComparison.OrdinalIgnoreCase) >= 0)
                {
                    string serverVersionString = error.Message.Substring(error.Message.IndexOf("server version = ", StringComparison.OrdinalIgnoreCase));

                    // length of 'server version = ' is 17
                    if (serverVersionString.Length > 17)
                    {
                        serverVersionString = serverVersionString.Substring(17);

                        // server encompass version
                        string versionString = serverVersionString.Split(' ')[0];

                        if (versionString.Length > 0)
                        {
                            HttpClient client = new HttpClient();

                            string versionUpdateUrl = EncompassSocialSurveyConfiguration.fetchSsapiUrl;
                            versionUpdateUrl += "/encompass/" + companyCredential.EncompassCredential.CompanyId + "/version/update";

                            versionUpdateUrl += "?version=" + versionString;

                            // update the encompass version of the company
                            var response = client.PostAsync(versionUpdateUrl,null).Result;
                            if( !response.IsSuccessStatusCode ){
                                Logger.Error("Unable to update the encompass version for company with ID : " + companyCredential.EncompassCredential.CompanyId );
                            }
                            else
                            {
                                Logger.Info("Sucessfully updated encompass version to " + versionString + " for company with ID : " + companyCredential.EncompassCredential.CompanyId );
                            }
                        }

                    }
                }
            }
            catch (Exception errorWhileParsingError)
            {
                Logger.Error("Unable to look for version mismatch error", errorWhileParsingError );
            }
        }

    }
}
