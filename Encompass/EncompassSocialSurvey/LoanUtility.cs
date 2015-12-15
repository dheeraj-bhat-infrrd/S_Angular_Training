
using EllieMae.Encompass.BusinessObjects.Loans;
using EllieMae.Encompass.Query;
using EllieMae.Encompass.BusinessObjects.Users;
using EllieMae.Encompass.Collections;
using EncompassSocialSurvey.ViewModel;
using System.Collections;
using System.Collections.Generic;
using System;
using EncompassSocialSurvey.Service;
using EncompassSocialSurvey.Entity;
using System.Net;
using System.Net.Mail;
using SendGrid;

namespace EncompassSocialSurvey
{
    public class LoanUtility
    {
        private static int DAYS_INTERVAL = EncompassSocialSurveyConfiguration.DefaultDaysIntervalToFetch; // should not get loans older than DAYS_INTERVAL from NOW
        private static DateTime lastFetchedTime = Convert.ToDateTime(EncompassSocialSurverConstant.DEFAULT_ENGAGEMENT_CLOSE_TIME);
        // Populates the loan list with the contents of a folder

        private StringList appendInitialFieldIdList(StringList fieldIds, string fieldId)
        {
            if (!string.IsNullOrWhiteSpace(fieldId))
            {
                fieldIds[8] = fieldId;  // closed date
            }

            return fieldIds;
        }

        private DateFieldCriterion createCriteria(DateTime lastFetchedTime, string field)
        {
            DateFieldCriterion dateCriteria = new DateFieldCriterion();
            dateCriteria.FieldName = "Fields." + field;
            int result = DateTime.Compare(lastFetchedTime, EncompassSocialSurverConstant.EPOCH_TIME);
            if (result != 0)
            {
                dateCriteria.Value = lastFetchedTime;
            }
            else
            {
                dateCriteria.Value = DateTime.Now.AddDays(-1 * DAYS_INTERVAL);
            }
            dateCriteria.MatchType = OrdinalFieldMatchType.GreaterThanOrEquals;
            return dateCriteria;

        }

        public List<LoanViewModel> LopulateLoanList(long runningCompanyId, string fieldid, string emailDomain, string emailPrefix)
        {
            Logger.Info("Entering the method LoanUtility.LopopulateLoanList() ");

            List<LoanViewModel> returnLoansViewModel = null;
            CRMBatchTrackerEntity crmBatchTracker = null;
            try
            {
                returnLoansViewModel = new List<LoanViewModel>();

                #region Popualted FieldIds // list of ids to get the details from loan

                StringList fieldIds = EncompassSocialSurverConstant.initialFieldList();
                fieldIds = appendInitialFieldIdList(fieldIds, fieldid);

                #endregion

                LoanService loanService = new LoanService();

                DateTime lastRunTime = EncompassSocialSurverConstant.EPOCH_TIME;
                crmBatchTracker = loanService.getCrmBatchTracker(runningCompanyId, EncompassSocialSurverConstant.SURVEY_SOURCE);
                if (crmBatchTracker != null)
                {
                    lastRunTime = crmBatchTracker.RecentRecordFetchedDate;
                }
                Logger.Info("Last Run time  " + lastRunTime);
                Logger.Info("Company Id  " + runningCompanyId);
                LoanIdentityList loanIdentityList = EncompassGlobal.EncompassLoginSession.Loans.Query(createCriteria(lastRunTime, fieldIds[8]));
                #region Load the list

                foreach (LoanIdentity id in loanIdentityList)
                {
                    try
                    {
                        Logger.Debug("Fetching loan from loanid " + id.Guid);
                        StringList fieldValues = EncompassGlobal.EncompassLoginSession.Loans.SelectFields(id.Guid, fieldIds);

                        Loan runningLoan = EncompassGlobal.EncompassLoginSession.Loans.Open(id.Guid);
                        User loanOfficer = null;
                        if (false == string.IsNullOrWhiteSpace(runningLoan.LoanOfficerID))
                            loanOfficer = EncompassGlobal.EncompassLoginSession.Users.GetUser(runningLoan.LoanOfficerID);

                        LoanViewModel forLoanVM_Borrower = new LoanViewModel();

                        forLoanVM_Borrower.SurveySource = EncompassSocialSurverConstant.SURVEY_SOURCE;

                        // remove the flower bracket from GUID
                        forLoanVM_Borrower.SurveySourceId = id.Guid.ToString().Replace("{", "").Replace("}", "");

                        forLoanVM_Borrower.CompanyId = runningCompanyId;
                        forLoanVM_Borrower.AgentId = (loanOfficer != null) ? loanOfficer.ID : "";
                        forLoanVM_Borrower.AgentName = (loanOfficer != null) ? loanOfficer.FullName : "";
                        

                        forLoanVM_Borrower.CustomerFirstName = fieldValues[2];
                        forLoanVM_Borrower.CustomerLastName = fieldValues[3];


                        string emailId = fieldValues[4];

                        if (string.IsNullOrWhiteSpace(emailDomain))
                        {

                            forLoanVM_Borrower.CustomerEmailId = emailId;
                            forLoanVM_Borrower.AgentEmailId = (loanOfficer != null) ? loanOfficer.Email : "";
                        }
                        else
                        {
                            forLoanVM_Borrower.CustomerEmailId = replaceEmailAddress(emailId, emailDomain, emailPrefix);
                            forLoanVM_Borrower.AgentEmailId = replaceEmailAddress((loanOfficer != null) ? loanOfficer.Email : "", emailDomain, emailPrefix);
                        }

                        forLoanVM_Borrower.ReminderCounts = EncompassSocialSurverConstant.REMINDER_COUNT;
                        forLoanVM_Borrower.LastReminderTime = EncompassSocialSurverConstant.LAST_REMINDER_TIME;
                        forLoanVM_Borrower.EngagementClosedTime = fieldValues[8];
                        forLoanVM_Borrower.Status = EncompassSocialSurverConstant.STATUS;

                        returnLoansViewModel.Add(forLoanVM_Borrower);

                        if ((string.IsNullOrWhiteSpace(fieldValues[5]) && string.IsNullOrWhiteSpace(fieldValues[6])) == false)
                        {
                            Logger.Debug("Found CoBorrower , fetching the required details");
                            LoanViewModel forLoanVM_Co_Borrower = new LoanViewModel();
                            forLoanVM_Co_Borrower.SurveySource = EncompassSocialSurverConstant.SURVEY_SOURCE;
                            forLoanVM_Co_Borrower.SurveySourceId = id.Guid.ToString().Replace("{", "").Replace("}", "");
                            forLoanVM_Co_Borrower.CompanyId = runningCompanyId;


                            forLoanVM_Co_Borrower.AgentId = (loanOfficer != null) ? loanOfficer.ID : "";
                            forLoanVM_Co_Borrower.AgentName = (loanOfficer != null) ? loanOfficer.FullName : "";
                            forLoanVM_Co_Borrower.AgentEmailId = (loanOfficer != null) ? loanOfficer.Email : "";

                            forLoanVM_Co_Borrower.CustomerFirstName = fieldValues[5];
                            forLoanVM_Co_Borrower.CustomerLastName = fieldValues[6];

                            string coborrowerEmailId = fieldValues[7];

                            if (string.IsNullOrWhiteSpace(emailDomain))
                            {

                                forLoanVM_Co_Borrower.CustomerEmailId = coborrowerEmailId;
                            }
                            else
                            {
                                forLoanVM_Co_Borrower.CustomerEmailId = replaceEmailAddress(coborrowerEmailId, emailDomain, emailPrefix);
                            }



                            forLoanVM_Co_Borrower.ReminderCounts = EncompassSocialSurverConstant.REMINDER_COUNT;
                            forLoanVM_Co_Borrower.LastReminderTime = EncompassSocialSurverConstant.LAST_REMINDER_TIME;
                            forLoanVM_Co_Borrower.EngagementClosedTime = fieldValues[8];
                            forLoanVM_Co_Borrower.Status = EncompassSocialSurverConstant.STATUS;

                            returnLoansViewModel.Add(forLoanVM_Co_Borrower);
                        }

                        if (null != runningLoan)
                        {
                            Logger.Debug("Closing the loan ");
                            runningLoan.Close();
                        }
                        Logger.Debug("Updating last fetched time");
                        updateLastFetchedTime(fieldValues[8]);

                    }
                    catch (System.Exception ex)
                    {
                        Logger.Error("Caught an exception: LoanUtility.LopopulateLoanList(): ", ex);

                    }
                }

                #endregion // Load the list

            }
            catch (System.Exception ex)
            {
                Logger.Error("Caught an exception: LoanUtility.LopopulateLoanList(): ", ex);
                String Subject = "Error while fetching data from encompass";
                String BodyText = "<br> Error while fetchign data from encompass for company with id : " + runningCompanyId + " on " + DateTime.Now + "<br>";
                BodyText += ex.Message;
                CommonUtility.SendMailToAdmin(Subject, BodyText);
                throw;
            }


            Logger.Debug("Updating crm batch tracker");
            if (returnLoansViewModel.Count > 0)
            {
                insertOrUpdateCrmBatchTracker(crmBatchTracker, runningCompanyId, EncompassSocialSurverConstant.SURVEY_SOURCE);
            }
            else
            {
                Logger.Debug("Notifying admin no records were fetched in this run ");
                sendMailToAdminForNoRecordFetched(runningCompanyId);
            }

            Logger.Info("Exiting the method LoanUtility.LopopulateLoanList()");
            return returnLoansViewModel;
        }


        public void sendMailToAdminForNoRecordFetched(long runningCompanyId)
        {
            var Subject = "No Records Fetched In This Run !!!";
            var BodyText = "Encompass was not able to fetch any new records for company id " + runningCompanyId + " which ran on " + DateTime.Now;
            CommonUtility.SendMailToAdmin(Subject, BodyText);
        }

        private void updateLastFetchedTime(string field)
        {
            DateTime loanCloseTime = Convert.ToDateTime(field);
            if (DateTime.Compare(loanCloseTime, DateTime.Now) < 0)
            {
                int result = DateTime.Compare(lastFetchedTime, loanCloseTime);
                if (result < 0)
                {
                    lastFetchedTime = loanCloseTime;
                }
            }
        }

        private void insertOrUpdateCrmBatchTracker(CRMBatchTrackerEntity entity, long companyId, string source)
        {
            LoanService loanService = new LoanService();
            Logger.Debug("Inside method insertOrUpdateCrmBatchTracker() ");
            if (entity == null)
            {
                entity = new CRMBatchTrackerEntity();
                entity.CompanyId = companyId;
                entity.CreatedOn = DateTime.Now;
                entity.ModifiedOn = DateTime.Now;
                entity.RecentRecordFetchedDate = lastFetchedTime;
                entity.Source = source;
                loanService.InsertCrmBatchTracker(entity);
            }
            else
            {
                entity.RecentRecordFetchedDate = lastFetchedTime;
                entity.ModifiedOn = DateTime.Now;
                loanService.UpdateCrmbatchTracker(entity);
            }

        }


        private string replaceEmailAddress(string email, string emailDomain, string emailPrefix)
        {
            Logger.Debug("Inside method replaceEmailAddress");
            if (string.IsNullOrWhiteSpace(email))
            {
                return "";
            }
            else
            {
                email = email.Replace("@", "+");
                Logger.Debug("Transitional email address: " + email);
                if (string.IsNullOrWhiteSpace(emailPrefix))
                {
                    email = email + "@" + emailDomain;
                }
                else
                {
                    email = emailPrefix + "+" + email + "@" + emailDomain;
                }

                Logger.Debug("Final replaced email address: " + email);
                return email;
            }
        }
    }
}
