using EncompassSocialSurvey.DAL;
using EncompassSocialSurvey.Entity;
using EncompassSocialSurvey.Service;
using System;
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
            #region get company credentials and process loan
            try
            {
                CompanyCredentialService _ccService = new CompanyCredentialService();
               
                Logger.Debug("Getting company details for production run");
                var companyCredentialsProd = _ccService.GetCompanyCredentials(EncompassSocialSurveyConstant.companyRecordTypeSaveData);
                Logger.Info("Company credentials count for production run: " + companyCredentialsProd.Count);
                Logger.Debug("Processing loans for companies ");
                ProcessLoanForCompanies(companyCredentialsProd , true); 

                Logger.Debug("Getting company details for generating report");
                var companyCredentialsGenerateReport = _ccService.GetCompanyCredentials(EncompassSocialSurveyConstant.companyRecordTypeGenerateReport);
                Logger.Info("Company credentials count for generating report: " + companyCredentialsGenerateReport.Count);
                Logger.Debug("Processing loans for companies ");
                ProcessLoanForCompanies(companyCredentialsGenerateReport , false);

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

            #endregion

            Logger.Info("Exiting the method Program.Main()");
        }

        #region private methods
       
        /// <summary>
        /// process loan for companies
        /// </summary>
        /// <param name="companyCredentials"></param>
        /// <param name="isProductionRun"></param>
        private static void ProcessLoanForCompanies(List<CompanyCredential> companyCredentials , Boolean isProductionRun)
        {
            Logger.Info("Entering the method ProcessLoanForCompanies.ProcessLoanForCompanies()");
            CompanyCredentialService _ccService = new CompanyCredentialService();

            foreach (var forCompCredential in companyCredentials)
            {
                LoanService loanSerivce = new LoanService();


                try
                {

                    if (loanSerivce.isCompanyActive(forCompCredential.EncompassCredential.CompanyId))
                     {
                        Logger.Debug("Starting loan processing for company: " + forCompCredential.CompanyName + " " 
                           + " companyId: " + forCompCredential.EncompassCredential.CompanyId
                           + " : companyUserName : " + forCompCredential.EncompassCredential.UserName
                           + " : companyURL : " + forCompCredential.EncompassCredential.EncompassUrl);

                    
                        Logger.Debug("Logging into encompass");
                        EncompassGlobal.GetUserLoginSesssion(forCompCredential);

                        var ssEnv = System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.SETUP_ENVIRONMENT];
                        Logger.Debug("SSEnv = " + ssEnv);
                        string fieldId = forCompCredential.EncompassCredential.fieldId;
                        string emailDomain = null;
                        string emailPrefix = null;
                        if (ssEnv.Equals(EncompassSocialSurveyConstant.SETUP_ENVIRONMENT_TEST))
                        {
                            emailDomain = System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.EMAIL_DOMAIN_REPLACEMENT];
                            emailPrefix = System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.EMAIL_DOMAIN_PREFIX];
                            Logger.Debug("Email Domain: " + emailDomain);
                            Logger.Debug("Email Prefix: " + emailPrefix);
                        }

                        try
                        {
                            LoanUtility _loanUtility = new LoanUtility();

                            var loansVM = _loanUtility.PopulateLoanList(forCompCredential.EncompassCredential.CompanyId, fieldId, isProductionRun, forCompCredential.EncompassCredential.numberOfDays, emailDomain, emailPrefix);

                            if (isProductionRun)
                            {
                                try
                                {
                                    if (null == loansVM) continue;
                                    if (loansVM.Count <= 0) continue;
                                    Logger.Debug("Saving loans for company : " + forCompCredential.CompanyName + " id : "  + forCompCredential.EncompassCredential.CompanyId);
                                    // process for insert
                                    loanSerivce.InsertLoans(loansVM);
                                }
                                catch (Exception ex) {
                                    string Subject = "Exception While processing encompass records";
                                    String BodyText = "An error has been occurred while storing the encompass record for company : " + forCompCredential.CompanyName + " id : " + forCompCredential.EncompassCredential.CompanyId + " on " + DateTime.Now + ".";
                                    BodyText += ex.Message;
                                    CommonUtility.SendMailToAdmin(Subject, BodyText);
                                    throw ex;
                                }
                            }
                            else 
                            {
                                try
                                {

                                    Logger.Debug("Generating report for company : " + forCompCredential.CompanyName);
                                    //generate report and send it
                                    var createdFilePath = loanSerivce.createLoanListCSV(loansVM, forCompCredential.CompanyName);
                                    loanSerivce.sendLoanReportToEmailAddresses(createdFilePath, forCompCredential.EncompassCredential.emailAddressForReport, forCompCredential.EncompassCredential.numberOfDays);
                                    //disable generate report for company
                                    Logger.Debug("Disabling generate report for company " + forCompCredential.CompanyName);
                                    _ccService.DisableReportGenerationForCompany(forCompCredential.EncompassCredential.CompanyId);
                                }
                                catch (Exception ex)
                                {
                                    string Subject = "Exception While processing encompass records";
                                    String BodyText = "An error has been occurred while generating the encompass record's report for company : " + forCompCredential.CompanyName + " id : " + forCompCredential.EncompassCredential.CompanyId + " on " + DateTime.Now + ".";
                                    BodyText += ex.Message;
                                    CommonUtility.SendMailToAdmin(Subject, BodyText);
                                    throw ex;
                                }
                            }
                                      
                            
                        }
                        catch (System.Exception ex)
                        {
                            // If getting any exception here, don't throw, let's process the other folder's loan
                            Logger.Error("Caught an exception, loanFolder: Program.ProcessLoanForCompanies():", ex);
                        }


                         Logger.Info("Done loan processing for company: " + forCompCredential.CompanyName + " "
                          + " companyId: " + forCompCredential.EncompassCredential.CompanyId
                          + " : companyUserName : " + forCompCredential.EncompassCredential.UserName
                          + " : companyURL : " + forCompCredential.EncompassCredential.EncompassUrl);
                     }

                }
                catch (System.Exception ex)
                {
                        // Let's process the loan for other company
                        Logger.Error("Caught an exception, companiesCredentials: Program.ProcessLoanForCompanies():", ex);
                        String Subject = "Error while connecting to encompass";
                        String BodyText = "An error has been occurred while connecting to encompass for company : " + forCompCredential.CompanyName + " with id : " + forCompCredential.EncompassCredential.CompanyId + " on " + DateTime.Now + ".";
                        BodyText += ex.Message;
                        CommonUtility.SendMailToAdmin(Subject, BodyText);
                }
                finally
                {
                    // close the session
                    if (null != EncompassGlobal.EncompassLoginSession)
                     EncompassGlobal.EncompassLoginSession.End();
                }
                   
            }


            Logger.Info("Exiting the method ProcessLoanForCompanies.ProcessLoanForCompanies()");
        }

       
        #endregion
    }
}
