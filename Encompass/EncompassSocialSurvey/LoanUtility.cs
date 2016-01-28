
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
        private static DateTime lastFetchedTime = Convert.ToDateTime(EncompassSocialSurveyConstant.DEFAULT_ENGAGEMENT_CLOSE_TIME);
        // Populates the loan list with the contents of a folder

        private StringList appendInitialFieldIdList(StringList fieldIds, string fieldId)
        {
            if (!string.IsNullOrWhiteSpace(fieldId))
            {
                fieldIds[8] = fieldId;  // closed date
            }

            return fieldIds;
        }

        /*private QueryCriterion createCriteria(DateTime lastFetchedTime, string field)
        {
            DateFieldCriterion upperLimitCriteria = new DateFieldCriterion();
            upperLimitCriteria.FieldName = "Fields." + field;
            upperLimitCriteria.Value = DateTime.Now;
            upperLimitCriteria.MatchType = OrdinalFieldMatchType.LessThanOrEquals;

            DateFieldCriterion lowerLimitCriteria = new DateFieldCriterion();
            lowerLimitCriteria.FieldName = "Fields." + field;

            int result = DateTime.Compare(lastFetchedTime, EncompassSocialSurveyConstant.EPOCH_TIME);
            if (result != 0)
            {
                //Create and return a criteria to return the loans processed between Now and three days back.
                lowerLimitCriteria.Value = DateTime.Now.AddDays(-1 * EncompassSocialSurveyConstant.DAYS_BEFORE);
            }
            else
            {
                //If the loans are being fetched for the first time, get loans data for the past N days.
                lowerLimitCriteria.Value = DateTime.Now.AddDays(-1 * DAYS_INTERVAL);
            }
            lowerLimitCriteria.MatchType = OrdinalFieldMatchType.GreaterThanOrEquals;
            return upperLimitCriteria.And(lowerLimitCriteria);
        }*/



        private QueryCriterion createCriteria(int noOfDays, string field)
        {
            DateFieldCriterion upperLimitCriteria = new DateFieldCriterion();
            upperLimitCriteria.FieldName = "Fields." + field;
            upperLimitCriteria.Value = DateTime.Now;
            upperLimitCriteria.MatchType = OrdinalFieldMatchType.LessThanOrEquals;

            DateFieldCriterion lowerLimitCriteria = new DateFieldCriterion();
            lowerLimitCriteria.FieldName = "Fields." + field;
            lowerLimitCriteria.Value = DateTime.Now.AddDays(-1 * noOfDays);
            lowerLimitCriteria.MatchType = OrdinalFieldMatchType.GreaterThanOrEquals;
            return upperLimitCriteria.And(lowerLimitCriteria);
        }



        public List<LoanViewModel> PopulateLoanList(long runningCompanyId, string fieldid, Boolean isProductionRun , int noOfDaysToFetch ,  string emailDomain, string emailPrefix)
        {
            Logger.Info("Entering the method LoanUtility.PopulateLoanList() ");
            int noOfDays = 0;
            List<LoanViewModel> returnLoansViewModel = null;
            CRMBatchTrackerEntity crmBatchTracker = null;
            try
            {
                returnLoansViewModel = new List<LoanViewModel>();

                #region Popualted FieldIds // list of ids to get the details from loan

                StringList fieldIds = EncompassSocialSurveyConstant.initialFieldList();
                fieldIds = appendInitialFieldIdList(fieldIds, fieldid);

                #endregion

                LoanService loanService = new LoanService();

                DateTime lastRunTime = EncompassSocialSurveyConstant.EPOCH_TIME;
                if (isProductionRun)
                {
                    crmBatchTracker = loanService.getCrmBatchTracker(runningCompanyId, EncompassSocialSurveyConstant.SURVEY_SOURCE);
                    //update the recent record fetch start date or if crm batch tracker is null than insert new entry
                    insertOrUpdateLastRunStartTime(crmBatchTracker, runningCompanyId, EncompassSocialSurveyConstant.SURVEY_SOURCE);
                    if (crmBatchTracker == null)
                    {
                        crmBatchTracker = loanService.getCrmBatchTracker(runningCompanyId, EncompassSocialSurveyConstant.SURVEY_SOURCE);
                    }
                    lastRunTime = crmBatchTracker.RecentRecordFetchedDate;
                    int result = DateTime.Compare(lastFetchedTime, EncompassSocialSurveyConstant.EPOCH_TIME);
                    if (result != 0)
                    {
                        noOfDays = EncompassSocialSurveyConstant.DAYS_BEFORE;
                    }
                    else
                    {
                        noOfDays = DAYS_INTERVAL;
                    }
                }
                else 
                {
                    //lastRunTime = DateTime.Now.AddDays(-1 * noOfDaysToFetch);
                    noOfDays = noOfDaysToFetch;
                }
                
                Logger.Info("Last Record Fetch time is " + lastRunTime);
                Logger.Info("Company Id  " + runningCompanyId);

                //fieldIds[8] is the loan closed date
                LoanIdentityList loanIdentityList = EncompassGlobal.EncompassLoginSession.Loans.Query(createCriteria(noOfDays, fieldIds[8]));

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

                        forLoanVM_Borrower.SurveySource = EncompassSocialSurveyConstant.SURVEY_SOURCE;
                        Logger.Debug("EngagementClosedTime for loan is : " + fieldValues[8]);
                        //check if engagement closed time is greater than current time
                        DateTime engagementClosedTime = Convert.ToDateTime(fieldValues[8]);
                        int result = DateTime.Compare(engagementClosedTime, DateTime.Now);
                        if (result > 0)
                        {
                            Logger.Debug("Engagement cloed time " + fieldValues[8] + " is greater than current date so skipping the record");
                            continue;
                        }

                        // remove the flower bracket from GUID
                        forLoanVM_Borrower.SurveySourceId = id.Guid.ToString().Replace("{", "").Replace("}", "");

                        forLoanVM_Borrower.CompanyId = runningCompanyId;
                        forLoanVM_Borrower.AgentId = (loanOfficer != null) ? loanOfficer.ID : "";
                        forLoanVM_Borrower.AgentName = (loanOfficer != null) ? loanOfficer.FullName : "";
                        

                        forLoanVM_Borrower.CustomerFirstName = fieldValues[2];
                        forLoanVM_Borrower.CustomerLastName = fieldValues[3];

                        string agentEmailId = (loanOfficer != null) ? loanOfficer.Email : "";
                        string emailId = fieldValues[4];

                        if (string.IsNullOrWhiteSpace(emailDomain))
                        {

                            forLoanVM_Borrower.CustomerEmailId = emailId;
                            forLoanVM_Borrower.AgentEmailId = agentEmailId;
                        }
                        else
                        {
                            forLoanVM_Borrower.CustomerEmailId = replaceEmailAddress(emailId, emailDomain, emailPrefix);
                            forLoanVM_Borrower.AgentEmailId = replaceEmailAddress(agentEmailId, emailDomain, emailPrefix);
                        }

                        forLoanVM_Borrower.ReminderCounts = EncompassSocialSurveyConstant.REMINDER_COUNT;
                        forLoanVM_Borrower.LastReminderTime = EncompassSocialSurveyConstant.LAST_REMINDER_TIME;
                        forLoanVM_Borrower.EngagementClosedTime = fieldValues[8];
                        forLoanVM_Borrower.Status = EncompassSocialSurveyConstant.STATUS;

                        returnLoansViewModel.Add(forLoanVM_Borrower);

                        if ((string.IsNullOrWhiteSpace(fieldValues[5]) && string.IsNullOrWhiteSpace(fieldValues[6])) == false)
                        {
                            Logger.Debug("Found CoBorrower , fetching the required details");
                            LoanViewModel forLoanVM_Co_Borrower = new LoanViewModel();
                            forLoanVM_Co_Borrower.SurveySource = EncompassSocialSurveyConstant.SURVEY_SOURCE;
                            forLoanVM_Co_Borrower.SurveySourceId = id.Guid.ToString().Replace("{", "").Replace("}", "");
                            forLoanVM_Co_Borrower.CompanyId = runningCompanyId;


                            forLoanVM_Co_Borrower.AgentId = (loanOfficer != null) ? loanOfficer.ID : "";
                            forLoanVM_Co_Borrower.AgentName = (loanOfficer != null) ? loanOfficer.FullName : "";
                            
                            forLoanVM_Co_Borrower.CustomerFirstName = fieldValues[5];
                            forLoanVM_Co_Borrower.CustomerLastName = fieldValues[6];

                            string coborrowerEmailId = fieldValues[7];

                            if (string.IsNullOrWhiteSpace(emailDomain))
                            {

                                forLoanVM_Co_Borrower.CustomerEmailId = coborrowerEmailId;
                                forLoanVM_Co_Borrower.AgentEmailId = agentEmailId;
                            }
                            else
                            {
                                forLoanVM_Co_Borrower.CustomerEmailId = replaceEmailAddress(coborrowerEmailId, emailDomain, emailPrefix);
                                forLoanVM_Co_Borrower.AgentEmailId = replaceEmailAddress(agentEmailId, emailDomain, emailPrefix);
                            }



                            forLoanVM_Co_Borrower.ReminderCounts = EncompassSocialSurveyConstant.REMINDER_COUNT;
                            forLoanVM_Co_Borrower.LastReminderTime = EncompassSocialSurveyConstant.LAST_REMINDER_TIME;
                            forLoanVM_Co_Borrower.EngagementClosedTime = fieldValues[8];
                            forLoanVM_Co_Borrower.Status = EncompassSocialSurveyConstant.STATUS;

                            returnLoansViewModel.Add(forLoanVM_Co_Borrower);
                        }

                        if (null != runningLoan)
                        {
                            Logger.Debug("Closing the loan ");
                            runningLoan.Close();
                        }

                        if (isProductionRun) 
                        {
                            Logger.Debug("Updating last fetched time");
                            updateLastFetchedTime(fieldValues[8]);
                        }
                        
                    }
                    catch (System.Exception ex)
                    {
                        Logger.Error("Caught an exception: LoanUtility.LopopulateLoanList(): ", ex);

                    }
                }

                #endregion // Load the list

                if (isProductionRun)
                {
                    Logger.Debug("Updating crm batch tracker");
                    if (returnLoansViewModel.Count <= 0)
                    {
                        Logger.Debug("Notifying admin no records were fetched in this run ");
                        sendMailToAdminForNoRecordFetched(runningCompanyId);
                    }
                    else
                    {
                        //update last record fetch time if new records has been fetched
                        updateRecentRecordFetchTimeInCrmBatchTracker(crmBatchTracker);
                    }


                    //update last run end date
                    updateLastRunEndTimeInCrmBatchTracker(crmBatchTracker);
                }
            

            }
            catch (System.Exception ex)
            {
                Logger.Error("Caught an exception: LoanUtility.LopopulateLoanList(): ", ex);
                String Subject = "Error while fetching data from encompass";
                String BodyText = "Error while fetchign data from encompass for company with id : " + runningCompanyId + " on " + DateTime.Now + " \n ";
                BodyText += ex.Message;
                CommonUtility.SendMailToAdmin(Subject, BodyText);
                if (isProductionRun)
                {
                    //update error in crm batch tracker
                    updateErrorInCrmBatchTracker(crmBatchTracker, ex.Message);
                }
                
                throw;
            }

            Logger.Info("Exiting the method LoanUtility.PopopulateLoanList()");
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


        private void insertOrUpdateLastRunStartTime(CRMBatchTrackerEntity entity, long companyId, string source)
        {
             LoanService loanService = new LoanService();
             Logger.Debug("Inside method insertOrUpdateLastRunStartTime() ");
            if (entity == null)
            {
                entity = new CRMBatchTrackerEntity();
                entity.CompanyId = companyId;
                entity.CreatedOn = DateTime.Now;
                entity.ModifiedOn = DateTime.Now;
                entity.LastRunStartDate = DateTime.Now;
                entity.LastRunEndDate =  EncompassSocialSurveyConstant.EPOCH_TIME;
                entity.RecentRecordFetchedDate = EncompassSocialSurveyConstant.EPOCH_TIME;
                entity.Source = source;
                entity.error = null;
                entity.description = null;
                loanService.InsertCrmBatchTracker(entity);
            }
            else
            {
                entity.LastRunStartDate = DateTime.Now;
                entity.ModifiedOn = DateTime.Now;
                loanService.UpdateCrmbatchTracker(entity);
            }
        
        }


        private void updateErrorInCrmBatchTracker(CRMBatchTrackerEntity entity , String error)
        {
            LoanService loanService = new LoanService();
            Logger.Debug("Inside method updateErrorInCrmBatchTracker() ");
            if (entity != null)
            {
                entity.error = error;
                entity.ModifiedOn = DateTime.Now;
                loanService.UpdateCrmbatchTracker(entity);
            }

        }

        private void updateRecentRecordFetchTimeInCrmBatchTracker(CRMBatchTrackerEntity entity)
        {
            LoanService loanService = new LoanService();
            Logger.Debug("Inside method updateRecentRecordFetchTimeInCrmBatchTracker() ");
            if (entity != null)
            {
                entity.RecentRecordFetchedDate = lastFetchedTime;
                entity.error = null;
                entity.ModifiedOn = DateTime.Now;
                loanService.UpdateCrmbatchTracker(entity);
            }
        }


        private void updateLastRunEndTimeInCrmBatchTracker(CRMBatchTrackerEntity entity)
        {
            LoanService loanService = new LoanService();
            Logger.Debug("Inside method updateLastRunEndTimeInCrmBatchTracker() ");
            if (entity != null)
            {
                entity.LastRunEndDate = DateTime.Now;
                entity.error = null;
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
