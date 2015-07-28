using EncompassSocialSurvey.DAL;
using EncompassSocialSurvey.Entity;
using EncompassSocialSurvey.Service;
using System.Collections;
using System.Collections.Generic;

namespace EncompassSocialSurvey
{
    class Program
    {
        static void Main(string[] args)
        {
            log4net.Config.BasicConfigurator.Configure();
            // Logger.XmlConfigure();

            Logger.Info("Entering into method: Program.Main()");

            try
            {
                //var stringDecrypted = CommonUtility.Decrypt("", "86e25dfad40cfe35ad938bf82929f88c");

                // var stringDecrypted = CommonUtility.Decrypt("86e25dfad40cfe35ad938bf82929f88c");

                // get company details
                CompanyCredentialService _ccService = new CompanyCredentialService();
                var companyCredentials = _ccService.GetCompanyCredentials();

                // it
                Logger.Info("Company credentials count from mongodb: " + companyCredentials.Count);

                // now process encompass loans
                ProcessLoanForCompanies(companyCredentials);
            }
            catch (System.Exception ex)
            {
                Logger.Error("Caught an exception: Program.Main()", ex);
            }
            finally
            {
                if (null != EncompassGlobal.EncompassLoginSession && EncompassGlobal.EncompassLoginSession.IsConnected)
                    EncompassGlobal.EncompassLoginSession.End();
            }
            Logger.Info("Exiting the method Program.Main()");
        }

        private static void ProcessLoanForCompanies(List<CompanyCredential> companyCredentials)
        {
            Logger.Info("Entering the method ProcessLoanForCompanies.ProcessLoanForCompanies()");
            foreach (var forCompCredential in companyCredentials)
            {

                Logger.Info("Strating loan processing for company: "
                    + " companyId: " + forCompCredential.CompanyId
                    + " : companyUserName : " + forCompCredential.UserName
                    + " : password : " + forCompCredential.Password
                    + " : companyURL : " + forCompCredential.EncompassUrl);
                  
                try
                {
                    // do the login & create the login session 
                    EncompassGlobal.GetUserLoginSesssion(forCompCredential);

                    // 1st Get all the folder
                    LoanUtility _loanUtility = new LoanUtility();
                    ArrayList loanFolder = _loanUtility.PopulateLoanFolderList();

                    // get the ss_environment value
                    var ssEnv = System.Configuration.ConfigurationManager.AppSettings["ss_environment"];
                    string fieldId = forCompCredential.fieldId;
                    string emailDomain = null;
                    string emailPrefix = null;
                    if (ssEnv.Equals("test"))
                    {
                        emailDomain = System.Configuration.ConfigurationManager.AppSettings["email_domain_to_replace"];
                        emailPrefix = System.Configuration.ConfigurationManager.AppSettings["email_address_prefix"];
                        Logger.Debug("Email Domain: " + emailDomain);
                        Logger.Debug("Email Prefix: " + emailPrefix);
                    }

                    // process loan for each loan folder
                    foreach (string folderName in loanFolder)
                    {
                        Logger.Info("Strating loan Folder:  : "
                   + " companyId: " + forCompCredential.CompanyId
                   + " : companyUserName : " + forCompCredential.UserName
                    + " : folderName : " + folderName);
                        try
                        {
                            // 1st Get loan VM
                            var loansVM = _loanUtility.LopulateLoanList(EncompassGlobal.EncompassLoginSession.Loans.Folders[folderName], forCompCredential.CompanyId, fieldId, emailDomain, emailPrefix);

                            // 2nd if no loansVM continue
                            if (null == loansVM) continue;
                            if (loansVM.Count <= 0) continue;

                            // process for insert

                            LoanService loanSerivce = new LoanService();
                            loanSerivce.InsertLoans(loansVM);
                        }
                        catch (System.Exception ex)
                        {
                            // If getting any exception here, don't throw, let's process the other folder's loan
                            Logger.Error("Caught an exception, loanFolder: Program.ProcessLoanForCompanies():", ex);
                            // TODO: Log the exception
                        }

                        Logger.Info("Done loan Folder:  : "
                + " companyId: " + forCompCredential.CompanyId
                + " : companyUserName : " + forCompCredential.UserName
                 + " : folderName : " + folderName);
                    }
                }
                catch (System.Exception ex)
                {
                    // Let's process the loan for other company
                    Logger.Error("Caught an exception, companiesCredentials: Program.ProcessLoanForCompanies():", ex);
                    // TODO: Log the exception
                }
                finally
                {
                    // close the session
                    if (null != EncompassGlobal.EncompassLoginSession)
                        EncompassGlobal.EncompassLoginSession.End();
                }
                Logger.Info("Done loan processing for company: "
                      + " companyId: " + forCompCredential.CompanyId
                      + " : companyUserName : " + forCompCredential.UserName
                      + " : companyURL : " + forCompCredential.EncompassUrl);
            }

            //
            Logger.Info("Exiting the method ProcessLoanForCompanies.ProcessLoanForCompanies()");
        }
    }
}
