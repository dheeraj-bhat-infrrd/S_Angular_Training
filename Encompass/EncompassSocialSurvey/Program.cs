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

            Logger.Info("Entering into method: Program.Main()");
            try
            {
                Logger.Debug("Getting company details ");
                CompanyCredentialService _ccService = new CompanyCredentialService();
                var companyCredentials = _ccService.GetCompanyCredentials();


                Logger.Info("Company credentials count from mongodb: " + companyCredentials.Count);

                Logger.Debug("Processing loans for company ");
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
                LoanService loanSerivce = new LoanService();

                if (loanSerivce.isCompanyActive(forCompCredential.CompanyId))
                {
                    Logger.Debug("Starting loan processing for company: "
                        + " companyId: " + forCompCredential.CompanyId
                        + " : companyUserName : " + forCompCredential.UserName
                        + " : companyURL : " + forCompCredential.EncompassUrl);

                    try
                    {
                        Logger.Debug("Logging into encompass");
                        EncompassGlobal.GetUserLoginSesssion(forCompCredential);

                        var ssEnv = System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurverConstant.SETUP_ENVIRONMENT];
                        Logger.Debug("SSEnv = " + ssEnv);
                        string fieldId = forCompCredential.fieldId;
                        string emailDomain = null;
                        string emailPrefix = null;
                        if (ssEnv.Equals(EncompassSocialSurverConstant.SETUP_ENVIRONMENT_TEST))
                        {
                            emailDomain = System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurverConstant.EMAIL_DOMAIN_REPLACEMENT];
                            emailPrefix = System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurverConstant.EMAIL_DOMAIN_PREFIX];
                            Logger.Debug("Email Domain: " + emailDomain);
                            Logger.Debug("Email Prefix: " + emailPrefix);
                        }

                        try
                        {
                            LoanUtility _loanUtility = new LoanUtility();

                            var loansVM = _loanUtility.LopulateLoanList(forCompCredential.CompanyId, fieldId, emailDomain, emailPrefix);

                            if (null == loansVM) continue;
                            if (loansVM.Count <= 0) continue;

                            // process for insert


                            loanSerivce.InsertLoans(loansVM);
                        }
                        catch (System.Exception ex)
                        {
                            // If getting any exception here, don't throw, let's process the other folder's loan
                            Logger.Error("Caught an exception, loanFolder: Program.ProcessLoanForCompanies():", ex);
                            // TODO: Log the exception
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
            }


            Logger.Info("Exiting the method ProcessLoanForCompanies.ProcessLoanForCompanies()");
        }
    }
}
