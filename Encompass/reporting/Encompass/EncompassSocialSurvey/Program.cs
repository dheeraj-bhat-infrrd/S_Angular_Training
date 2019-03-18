using EncompassSocialSurvey.Entity;
using EncompassSocialSurvey.Service;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace EncompassSocialSurvey
{
    class Program
    {
        #region static variables declaration

        private static readonly int MAX_DEGREE_OF_PARALLELISM = EncompassSocialSurveyConfiguration.MaxNoOfParallelThreads;

        #endregion
        
        static void Main(string[] args)
        {
            log4net.Config.BasicConfigurator.Configure();

            Logger.Info("Entering into method: Program.Main()");

            #region get company credentials and process loan
            try
            {

                CompanyCredentialService _ccService = new CompanyCredentialService();

                #region For Production run

                Logger.Debug("Getting company details for production run");
                var companyCredentialsProd = _ccService.GetCompanyCredentials(EncompassSocialSurveyConstant.companyRecordTypeSaveData);
                Logger.Info("Company credentials count for production run: " + companyCredentialsProd.Count);
                Logger.Debug("Processing loans for companies ");
                ProcessLoanForCompanies(companyCredentialsProd, true, _ccService);

                #endregion

                #region For Generating report

                Logger.Debug("Getting company details for generating report");
                var companyCredentialsGenerateReport = _ccService.GetCompanyCredentials(EncompassSocialSurveyConstant.companyRecordTypeGenerateReport);
                Logger.Info("Company credentials count for generating report: " + companyCredentialsGenerateReport.Count);
                Logger.Debug("Processing loans for companies ");
                ProcessLoanForCompanies(companyCredentialsGenerateReport, false, _ccService);

                #endregion

            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: Program.Main()", ex);
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
        private static void ProcessLoanForCompanies(List<CompanyCredential> companyCredentials, Boolean isProductionRun, CompanyCredentialService _ccService)
        {
            Logger.Info("Entering the method ProcessLoanForCompanies.ProcessLoanForCompanies()");

            #region Processing each company loans in parallel

            Parallel.ForEach(companyCredentials, new ParallelOptions { MaxDegreeOfParallelism = MAX_DEGREE_OF_PARALLELISM }, (forCompCredential) =>
            {
                LoanService loanSerivce = new LoanService();
                EncompassGlobal encompassGlobal = new EncompassGlobal();

                try
                {

                    Logger.Debug("Starting loan processing for company: " + forCompCredential.CompanyName + " "
                       + " companyId: " + forCompCredential.EncompassCredential.CompanyId
                       + " : companyUserName : " + forCompCredential.EncompassCredential.UserName
                       + " : companyURL : " + forCompCredential.EncompassCredential.EncompassUrl);

                    //send main to admin
                    String StartMailSubject = "Starting loan processing for company: " + forCompCredential.CompanyName;
                    String StartMailBodyText = "Starting loan processing for company: " + forCompCredential.CompanyName
                    + " at : " + DateTime.Now;
                    CommonUtility.SendMailToAdmin(StartMailSubject, StartMailBodyText);

                    Logger.Debug("Logging into encompass");
                    encompassGlobal.GetUserLoginSesssion(forCompCredential);

                    var ssEnv = System.Configuration.ConfigurationManager.AppSettings[EncompassSocialSurveyConstant.SETUP_ENVIRONMENT];
                    Logger.Debug("SSEnv = " + ssEnv);
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
                        int noOfRecordsInserted = 0;

                        var loansVM = _loanUtility.PopulateLoanList(encompassGlobal, forCompCredential.EncompassCredential, isProductionRun, emailDomain, emailPrefix);

                        if (isProductionRun)
                        {
                            try
                            {
                                //if (null == loansVM) continue;
                                //if (loansVM.Count <= 0) continue;
                                Logger.Debug("Saving loans for company : " + forCompCredential.CompanyName + " id : " + forCompCredential.EncompassCredential.CompanyId);
                                // process for insert
                                if (loansVM != null && loansVM.Count > 0)
                                {
                                    noOfRecordsInserted = loanSerivce.InsertLoans(loansVM);

                                    string successMessage = "Successfully received " + noOfRecordsInserted + " transactions via encompass today";
                                    //send success notification 
                                    _ccService.SendNotification(forCompCredential.EncompassCredential.CompanyId,successMessage,
                                        GetCurrentMilli(), "SUCCESS");
                                    
                                }
                                CRMBatchTrackerEntity crmBatchTracker = loanSerivce.getCrmBatchTracker(forCompCredential.EncompassCredential.CompanyId, EncompassSocialSurveyConstant.SURVEY_SOURCE);
                                if (crmBatchTracker != null)
                                {
                                    //update recent records fetched count in crm batch tracker
                                    _loanUtility.UpdateLastRunRecordFetechedCountInCrmBatchTracker(loanSerivce,
                                        crmBatchTracker, noOfRecordsInserted);

                                    //insert count of records fetched in crm batch tracker history  
                                    _loanUtility.InsertCrmBatchTrackerHistory(loanSerivce, crmBatchTracker.Id,
                                        noOfRecordsInserted);
                                }
                            }
                            catch (Exception ex)
                            {
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
                                var createdFilePath = loanSerivce.CreateLoanListCSV(loansVM, forCompCredential.CompanyName);
                                loanSerivce.SendLoanReportToEmailAddresses(createdFilePath, forCompCredential.EncompassCredential.emailAddressForReport, forCompCredential.EncompassCredential.numberOfDays);
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


                    //send main to admin
                    String EndMailSubject = "Done loan processing for company: " + forCompCredential.CompanyName;
                    String EndMailBodyText = "Done loan processing for company: " + forCompCredential.CompanyName + " "
                     + "at : " + DateTime.Now;
                    CommonUtility.SendMailToAdmin(EndMailSubject, EndMailBodyText);

                    Logger.Info("Done loan processing for company: " + forCompCredential.CompanyName + " "
                     + " companyId: " + forCompCredential.EncompassCredential.CompanyId
                     + " : companyUserName : " + forCompCredential.EncompassCredential.UserName
                     + " : companyURL : " + forCompCredential.EncompassCredential.EncompassUrl);


                }
                catch (Exception ex)
                {
                    // Let's process the loan for other company
                    Logger.Error("Caught an exception, companiesCredentials: Program.ProcessLoanForCompanies():", ex);
                    String Subject = "Error while connecting to encompass";
                    String BodyText = "An error has been occurred while connecting to encompass for company : " + forCompCredential.CompanyName + " with id : " + forCompCredential.EncompassCredential.CompanyId + " on " + DateTime.Now + ".";
                    BodyText += ex.Message;
                    //send mail to socialsurvey admin
                    CommonUtility.SendMailToAdmin(Subject, BodyText);

                    String errorMessage;
                    
                    if (ex.Message.Contains("InvalidPassword"))
                    {
                        errorMessage = EncompassSocialSurveyConstant.INVALID_PWD_MESSAGE;
                    }
                    else if(ex.Message.Contains("Version mismatch"))
                    {
                        errorMessage = EncompassSocialSurveyConstant.VERSION_MISMATCH_MESSAGE;
                    }
                    else if (ex.Message.Contains("UserNotFound"))
                    {
                        errorMessage = EncompassSocialSurveyConstant.USER_NOTFOUND_MESSAGE;
                    }
                    else if (ex.Message.Contains("UserLocked"))
                    {
                        errorMessage = EncompassSocialSurveyConstant.USER_LOCKED_MESSAGE;
                    }
                    else
                    {
                        errorMessage = EncompassSocialSurveyConstant.TECHNICAIL_ERROR_MESSAGE;
                    }

                    //save the error notification
                    _ccService.SendNotification(forCompCredential.EncompassCredential.CompanyId, errorMessage,
                        GetCurrentMilli(), "ERROR");
                    //send mail to encompass admin
                    _ccService.SendMailToEncompassAdmin(forCompCredential.EncompassCredential.CompanyId, errorMessage);
                }
                finally
                {
                    // close the session
                    if (null != encompassGlobal.EncompassLoginSession)
                        encompassGlobal.EncompassLoginSession.End();
                }

            }
            );

            #endregion

            Logger.Info("Exiting the method ProcessLoanForCompanies.ProcessLoanForCompanies()");
        }

        public static long GetCurrentMilli()
        {
            DateTime Jan1970 = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);
            TimeSpan javaSpan = DateTime.UtcNow - Jan1970;
            return (long)javaSpan.TotalMilliseconds;
        }

        #endregion
    }
}
