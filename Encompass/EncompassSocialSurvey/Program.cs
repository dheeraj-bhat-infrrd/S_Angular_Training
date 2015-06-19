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
                    + " : companyURL : " + forCompCredential.EncompassUrl);
                try
                {
                    // do the login & create the login session 
                    EncompassGlobal.GetUserLoginSesssion(forCompCredential);

                    // 1st Get all the folder
                    LoanUtility _loanUtility = new LoanUtility();
                    ArrayList loanFolder = _loanUtility.PopulateLoanFolderList();

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
                            var loansVM = _loanUtility.LopulateLoanList(EncompassGlobal.EncompassLoginSession.Loans.Folders[folderName], forCompCredential.CompanyId);

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
