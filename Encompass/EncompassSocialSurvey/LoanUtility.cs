
using EllieMae.Encompass.BusinessObjects.Loans;
using EllieMae.Encompass.Query;
using EllieMae.Encompass.BusinessObjects.Users;
using EllieMae.Encompass.Collections;
using EncompassSocialSurvey.ViewModel;
using System.Collections.Generic;
using System;
using EncompassSocialSurvey.Service;
using EncompassSocialSurvey.Entity;
using EllieMae.Encompass.Reporting;

namespace EncompassSocialSurvey
{
    public class LoanUtility
    {

        #region static members

        private static int DAYS_INTERVAL = EncompassSocialSurveyConfiguration.DefaultDaysIntervalToFetch; // should not get loans older than DAYS_INTERVAL from NOW
        private DateTime lastFetchedTime = Convert.ToDateTime(EncompassSocialSurveyConstant.DEFAULT_ENGAGEMENT_CLOSE_TIME);
        private static long SUMMIT_ID = EncompassSocialSurveyConfiguration.SummitCompanyId;

        #endregion

        // Populates the loan list with the contents of a folder

        #region private methods

        /// <summary>
        /// append fieldid list
        /// </summary>
        /// <param name="fieldIds"></param>
        /// <param name="fieldId"></param>
        /// <returns></returns>
        private StringList AppendInitialFieldIdList(StringList fieldIds, string fieldId)
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

        /// <summary>
        /// create criteria
        /// </summary>
        /// <param name="noOfDays"></param>
        /// <param name="field"></param>
        /// <returns></returns>
        private QueryCriterion createCriteria(int noOfDays, string field)
        {
            DateFieldCriterion upperLimitCriteria = new DateFieldCriterion();
            upperLimitCriteria.FieldName = "Fields." + field;
            upperLimitCriteria.Value = DateTime.Today;
            upperLimitCriteria.MatchType = OrdinalFieldMatchType.LessThanOrEquals;
            upperLimitCriteria.Precision = DateFieldMatchPrecision.Day;

            DateFieldCriterion lowerLimitCriteria = new DateFieldCriterion();
            lowerLimitCriteria.FieldName = "Fields." + field;
            lowerLimitCriteria.Value = DateTime.Today.AddDays(-1 * noOfDays);
            lowerLimitCriteria.MatchType = OrdinalFieldMatchType.GreaterThanOrEquals;
            lowerLimitCriteria.Precision = DateFieldMatchPrecision.Day;
            return upperLimitCriteria.And(lowerLimitCriteria);
        }

        /// <summary>
        /// populate loan in loan view model
        /// </summary>
        /// <param name="runningCompanyId"></param>
        /// <param name="fieldid"></param>
        /// <param name="isProductionRun"></param>
        /// <param name="noOfDaysToFetch"></param>
        /// <param name="emailDomain"></param>
        /// <param name="emailPrefix"></param>
        /// <returns></returns>
        public List<LoanViewModel> PopulateLoanList(EncompassGlobal encompassGlobal, EncompassCredential encompassCredential, Boolean isProductionRun, string emailDomain, string emailPrefix)
        {
            Logger.Info("Entering the method LoanUtility.PopulateLoanList() ");
            int noOfDaysToFetch = encompassCredential.numberOfDays;
            long runningCompanyId = encompassCredential.CompanyId;
            string fieldid = encompassCredential.fieldId;
            List<LoanViewModel> returnLoansViewModel = null;
            CRMBatchTrackerEntity crmBatchTracker = null;
            try
            {
                returnLoansViewModel = new List<LoanViewModel>();

                #region Popualted FieldIds // list of ids to get the details from loan

                // List of fields that is to obtained from the current encompass session
                StringList fields = constructFieldList(encompassCredential);

                // convert to cannonical Field IDs
                convertTocannonicalFields(fields);

                Logger.Debug("Company ID: " + encompassCredential.CompanyId + ", Querying for fields: " + string.Join(",", fields.ToArray()));

                #endregion


                LoanService loanService = new LoanService();
                DateTime lastRunTime = EncompassSocialSurveyConstant.EPOCH_TIME;

                if( isProductionRun ){
                    crmBatchTracker = loanService.getCrmBatchTracker(runningCompanyId, EncompassSocialSurveyConstant.SURVEY_SOURCE);

                    //update the recent record fetch start date or if crm batch tracker is null than insert new entry
                    InsertOrUpdateLastRunStartTime(crmBatchTracker, loanService, runningCompanyId, EncompassSocialSurveyConstant.SURVEY_SOURCE);

                    lastRunTime = crmBatchTracker.RecentRecordFetchedDate;
                    Logger.Info("Last Record Fetch time is " + lastRunTime);
                }
                
                // process number of days to query
                int noOfDays = determineNumberOfDays(lastRunTime, runningCompanyId, isProductionRun, noOfDaysToFetch);

                // serarch criterion based on closed date
                QueryCriterion query = createCriteria(noOfDays, determineClosedDateFieldId(encompassCredential));

                // get the cursor for search results
                LoanReportCursor cursor = encompassGlobal.EncompassLoginSession.Reports.OpenReportCursor(fields, query);

                Logger.Info("No of loan info received from encomass : " + cursor.Count);

                int count = cursor.Count;
                int startIndex = 0;
                int batchSize = 1000;

                while (batchSize <= count)
                {
                    processLoanReportData(encompassCredential, returnLoansViewModel, cursor.GetItems(startIndex, batchSize), isProductionRun, emailDomain, emailPrefix);
                    startIndex += batchSize;
                    count -= batchSize;
                }

                if (count > 0)
                {
                    processLoanReportData(encompassCredential, returnLoansViewModel, cursor.GetItems(startIndex, count), isProductionRun, emailDomain, emailPrefix);
                }


                if (isProductionRun)
                {
                    Logger.Debug("Updating crm batch tracker");
                    if (returnLoansViewModel.Count <= 0)
                    {
                        Logger.Debug("Notifying admin no records were fetched in this run ");
                        SendMailToAdminForNoRecordFetched(runningCompanyId);
                    }
                    else
                    {
                        //update last record fetch time if new records has been fetched
                        UpdateRecentRecordFetchTimeInCrmBatchTracker(crmBatchTracker, loanService);
                    }

                    //update last run end date
                    UpdateLastRunEndTimeInCrmBatchTracker(crmBatchTracker, loanService);


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
                    UpdateErrorInCrmBatchTracker(crmBatchTracker, ex.Message);
                }

                throw;
            }

            Logger.Info("Exiting the method LoanUtility.PopopulateLoanList()");
            return returnLoansViewModel;
        }

        private int determineNumberOfDays(DateTime lastRunTime, long runningCompanyId, bool isProductionRun, int noOfDaysToFetch)
        {
            int noOfDays = 0;
            if (isProductionRun && DateTime.Compare(lastRunTime, EncompassSocialSurveyConstant.EPOCH_TIME) != 0 )
            {
                int result = DateTime.Compare(lastRunTime, EncompassSocialSurveyConstant.EPOCH_TIME);
                if (result != 0)
                {
                    int days = (DateTime.Now - lastRunTime).Days;
                    if (days > EncompassSocialSurveyConstant.DAYS_BEFORE)
                        noOfDays = days;
                    else
                        noOfDays = EncompassSocialSurveyConstant.DAYS_BEFORE;

                    //If the company is summit, get records only from 18/7/2016
                    if (runningCompanyId == SUMMIT_ID)
                    {
                        int daysSinceStart = (DateTime.Now - EncompassSocialSurveyConstant.SUMMIT_BEGIN_TIME).Days;
                        if (noOfDays > daysSinceStart)
                            noOfDays = daysSinceStart;
                    }
                }
                else
                {
                    //If the company is summit, get records only from 18/7/2016
                    if (runningCompanyId == SUMMIT_ID)
                    {
                        noOfDays = (DateTime.Now - EncompassSocialSurveyConstant.SUMMIT_BEGIN_TIME).Days;
                    }
                    else
                    {
                        noOfDays = DAYS_INTERVAL;
                    }
                }
            }
            else
            {
                //lastRunTime = DateTime.Now.AddDays(-1 * noOfDaysToFetch);
                noOfDays = noOfDaysToFetch;
            }
            return noOfDays;
        }


        private void processLoanReportData(EncompassCredential credential, List<LoanViewModel> returnLoansViewModel, LoanReportDataList loanReportDataList, bool isProductionRun, string emailDomain, string emailPrefix)
        {
            foreach (LoanReportData loan in loanReportDataList)
            {
                try
                {

                    // parse closed date
                    //check if engagement closed time is greater than current time
                    DateTime engagementClosedTimeString = (DateTime)loan["Fields." + determineClosedDateFieldId(credential)];

                    if (!isClosedDateValid(engagementClosedTimeString))
                    {
                        continue;
                    }

                    StringList fieldsReceived = loan.GetFieldNames();

                    // get the required field values
                    //string guid = loan.Guid;
                    string loanNumber = loan["Fields." + EncompassSocialSurveyConstant.LOAN_NUMBER_FIELD] as string;
                    //string loanOfficerId = loan["Fields." + EncompassSocialSurveyConstant.LOAN_OFFICER_ID_FIELD] as string;
                    string loanOfficerName = loan["Fields." + EncompassSocialSurveyConstant.LOAN_OFFICER_NAME_FIELD] as string;
                    string loanOfficerEmail = loan["Fields." + EncompassSocialSurveyConstant.LOAN_OFFICER_EMAIL_FIELD] as string;
                    string borrowerFirstName = loan["Fields." + EncompassSocialSurveyConstant.BORROWER_FIRST_NAME_FIELD] as string;
                    string borrowerLastName = loan["Fields." + EncompassSocialSurveyConstant.BORROWER_LAST_NAME_FIELD] as string;
                    string borrowerEmail = loan["Fields." + EncompassSocialSurveyConstant.BORROWER_EMAIL_FIELD] as string;
                    string coBorrowerFirstName = loan["Fields." + EncompassSocialSurveyConstant.COBORROWER_FIRST_NAME_FIELD] as string;
                    string coBorrowerLastName = loan["Fields." + EncompassSocialSurveyConstant.COBORROWER_LAST_NAME_FIELD] as string;
                    string coBorrowerEmail = loan["Fields." + EncompassSocialSurveyConstant.COBORROWER_EMAIL_FIELD] as string;
                    //string subjectPropertyStreet = loan["Fields." + EncompassSocialSurveyConstant.SUBJECT_PROPERTY_STREET_FIELD] as string;
                    string subjectPropertyCity = loan["Fields." + EncompassSocialSurveyConstant.SUBJECT_PROPERTY_CITY_FIELD] as string;
                    //string subjectPropertyCountry = loan["Fields." + EncompassSocialSurveyConstant.SUBJECT_PROPERTY_COUNTRY_FIELD] as string;
                    string subjectPropertyState = loan["Fields." + EncompassSocialSurveyConstant.SUBJECT_PROPERTY_STATE_FIELD] as string;
                    //string subjectPropertyZip = loan["Fields." + EncompassSocialSurveyConstant.SUBJECT_PROPERTY_ZIP_FIELD] as string;
                    string propertyAddress = null;
                    string loanProcessorName = null;
                    string loanProcessorEmail = null;

                    if (!string.IsNullOrWhiteSpace(credential.propertyAddress))
                    {
                        propertyAddress = loan["Fields." + credential.propertyAddress] as string;
                    }

                    if (!string.IsNullOrWhiteSpace(credential.loanProcessorName))
                    {
                        loanProcessorName = loan["Fields." + credential.loanProcessorName] as string;
                    }

                    if (!string.IsNullOrWhiteSpace(credential.loanProcessorEmail))
                    {
                        loanProcessorEmail = loan["Fields." + credential.loanProcessorEmail] as string;
                    }


                    string agentId = "0";
                    string agentName = (loanOfficerName != null) ? loanOfficerName : "";
                    string agentEmail = (loanOfficerEmail != null) ? loanOfficerEmail : "";

                    // process email IDs
                    if (!string.IsNullOrWhiteSpace(emailDomain))
                    {
                        loanOfficerEmail = ReplaceEmailAddress(loanOfficerEmail, emailDomain, emailPrefix);
                        borrowerEmail = ReplaceEmailAddress(borrowerEmail, emailDomain, emailPrefix);
                        coBorrowerEmail = ReplaceEmailAddress(coBorrowerEmail, emailDomain, emailPrefix);
                        loanProcessorEmail = ReplaceEmailAddress(loanProcessorEmail, emailDomain, emailPrefix);
                    }


                    // borrower survey
                    LoanViewModel forLoanVM_Borrower = new LoanViewModel();

                    forLoanVM_Borrower.SurveySource = EncompassSocialSurveyConstant.SURVEY_SOURCE;
                    forLoanVM_Borrower.LoanNumber = loanNumber;
                    forLoanVM_Borrower.SurveySourceId = loanNumber;
                    forLoanVM_Borrower.CompanyId = credential.CompanyId;
                    forLoanVM_Borrower.AgentId = agentId;
                    forLoanVM_Borrower.AgentName = agentName;
                    forLoanVM_Borrower.CustomerFirstName = borrowerFirstName;
                    forLoanVM_Borrower.CustomerLastName = borrowerLastName;
                    forLoanVM_Borrower.State = subjectPropertyState;
                    forLoanVM_Borrower.City = subjectPropertyCity;
                    forLoanVM_Borrower.CustomerEmailId = borrowerEmail;
                    forLoanVM_Borrower.AgentEmailId = agentEmail;
                    forLoanVM_Borrower.ReminderCounts = EncompassSocialSurveyConstant.REMINDER_COUNT;
                    forLoanVM_Borrower.LastReminderTime = EncompassSocialSurveyConstant.LAST_REMINDER_TIME;
                    forLoanVM_Borrower.EngagementClosedTime = engagementClosedTimeString;
                    forLoanVM_Borrower.Status = EncompassSocialSurveyConstant.STATUS;
                    forLoanVM_Borrower.ParticipantType = EncompassSocialSurveyConstant.PARTICIPANT_TYPE_BORROWER;
                    forLoanVM_Borrower.LoanProcessorName = loanProcessorName;
                    forLoanVM_Borrower.LoanProcessorEmail = loanProcessorEmail;
                    forLoanVM_Borrower.PropertyAddress = propertyAddress;

                    returnLoansViewModel.Add(forLoanVM_Borrower);

                    //add coborrower
                    if (!(string.IsNullOrWhiteSpace(coBorrowerFirstName) && string.IsNullOrWhiteSpace(coBorrowerLastName)) && !string.IsNullOrWhiteSpace(coBorrowerEmail))
                    {
                        LoanViewModel forLoanVM_Co_Borrower = new LoanViewModel();
                        forLoanVM_Co_Borrower.SurveySource = EncompassSocialSurveyConstant.SURVEY_SOURCE;
                        forLoanVM_Co_Borrower.SurveySourceId = loanNumber;
                        forLoanVM_Co_Borrower.CompanyId = credential.CompanyId;
                        forLoanVM_Co_Borrower.AgentId = agentId;
                        forLoanVM_Co_Borrower.AgentName = agentName;
                        forLoanVM_Co_Borrower.CustomerFirstName = coBorrowerFirstName;
                        forLoanVM_Co_Borrower.CustomerLastName = coBorrowerLastName;
                        forLoanVM_Co_Borrower.CustomerEmailId = coBorrowerEmail;
                        forLoanVM_Co_Borrower.AgentEmailId = agentEmail;
                        forLoanVM_Co_Borrower.ReminderCounts = EncompassSocialSurveyConstant.REMINDER_COUNT;
                        forLoanVM_Co_Borrower.LastReminderTime = EncompassSocialSurveyConstant.LAST_REMINDER_TIME;
                        forLoanVM_Co_Borrower.EngagementClosedTime = engagementClosedTimeString;
                        forLoanVM_Co_Borrower.Status = EncompassSocialSurveyConstant.STATUS;
                        forLoanVM_Co_Borrower.State = subjectPropertyState;
                        forLoanVM_Co_Borrower.City = subjectPropertyCity;
                        forLoanVM_Co_Borrower.ParticipantType = EncompassSocialSurveyConstant.PARTICIPANT_TYPE_CO_BORROWER;
                        forLoanVM_Borrower.LoanProcessorName = loanProcessorName;
                        forLoanVM_Borrower.LoanProcessorEmail = loanProcessorEmail;
                        forLoanVM_Borrower.PropertyAddress = propertyAddress;

                        returnLoansViewModel.Add(forLoanVM_Co_Borrower);
                    }

                    //check for partner survey
                    if (credential.allowPartnerSurvey)
                    {

                        String buyerEmail = fieldsReceived.Contains("Fields." + credential.buyerAgentEmail) ? loan["Fields." + credential.buyerAgentEmail] as string : "";
                        String buyerName = fieldsReceived.Contains("Fields." + credential.buyerAgentName) ? loan["Fields." + credential.buyerAgentName] as string : "";
                        String sellerEmail = fieldsReceived.Contains("Fields." + credential.sellerAgentEmail) ? loan["Fields." + credential.sellerAgentEmail] as string : "";
                        String sellerName = fieldsReceived.Contains("Fields." + credential.sellerAgentName) ? loan["Fields." + credential.sellerAgentName] as string : "";


                        // parse emails
                        if (string.IsNullOrWhiteSpace(emailDomain))
                        {
                            buyerEmail = ReplaceEmailAddress(buyerEmail, emailDomain, emailPrefix);
                            sellerEmail = ReplaceEmailAddress(sellerEmail, emailDomain, emailPrefix);
                        }

                        //for adding buyer agent
                        if (string.IsNullOrWhiteSpace(buyerEmail) == false)
                        {
                            string[] buyerNames = parseFirstAndLastName(buyerName);

                            LoanViewModel forLoanVM_buyer_agent = new LoanViewModel();
                            forLoanVM_buyer_agent.SurveySource = EncompassSocialSurveyConstant.SURVEY_SOURCE;
                            forLoanVM_buyer_agent.SurveySourceId = loanNumber;
                            forLoanVM_buyer_agent.CompanyId = credential.CompanyId;
                            forLoanVM_buyer_agent.AgentId = agentId;
                            forLoanVM_buyer_agent.AgentName = agentName;
                            forLoanVM_buyer_agent.CustomerFirstName = buyerNames[0];
                            forLoanVM_buyer_agent.CustomerLastName = buyerNames[1];
                            forLoanVM_buyer_agent.CustomerEmailId = buyerEmail;
                            forLoanVM_buyer_agent.AgentEmailId = agentEmail;
                            forLoanVM_buyer_agent.ReminderCounts = EncompassSocialSurveyConstant.REMINDER_COUNT;
                            forLoanVM_buyer_agent.LastReminderTime = EncompassSocialSurveyConstant.LAST_REMINDER_TIME;
                            forLoanVM_buyer_agent.EngagementClosedTime = engagementClosedTimeString;
                            forLoanVM_buyer_agent.Status = EncompassSocialSurveyConstant.STATUS;
                            forLoanVM_buyer_agent.State = subjectPropertyState;
                            forLoanVM_buyer_agent.City = subjectPropertyCity;
                            forLoanVM_buyer_agent.ParticipantType = EncompassSocialSurveyConstant.PARTICIPANT_TYPE_BUYER_AGENT;
                            forLoanVM_Borrower.LoanProcessorName = loanProcessorName;
                            forLoanVM_Borrower.LoanProcessorEmail = loanProcessorEmail;
                            forLoanVM_Borrower.PropertyAddress = propertyAddress;

                            returnLoansViewModel.Add(forLoanVM_buyer_agent);
                        }


                        //for adding seller agent
                        if (string.IsNullOrWhiteSpace(sellerEmail) == false)
                        {
                            string[] sellerNames = parseFirstAndLastName(sellerName);

                            LoanViewModel forLoanVM_seller_agent = new LoanViewModel();
                            forLoanVM_seller_agent.SurveySource = EncompassSocialSurveyConstant.SURVEY_SOURCE;
                            forLoanVM_seller_agent.SurveySourceId = loanNumber;
                            forLoanVM_seller_agent.CompanyId = credential.CompanyId;
                            forLoanVM_seller_agent.AgentId = agentId;
                            forLoanVM_seller_agent.AgentName = agentName;
                            forLoanVM_seller_agent.CustomerFirstName = sellerNames[0];
                            forLoanVM_seller_agent.CustomerLastName = sellerNames[1];
                            forLoanVM_seller_agent.CustomerEmailId = sellerEmail;
                            forLoanVM_seller_agent.AgentEmailId = agentEmail;
                            forLoanVM_seller_agent.ReminderCounts = EncompassSocialSurveyConstant.REMINDER_COUNT;
                            forLoanVM_seller_agent.LastReminderTime = EncompassSocialSurveyConstant.LAST_REMINDER_TIME;
                            forLoanVM_seller_agent.EngagementClosedTime = engagementClosedTimeString;
                            forLoanVM_seller_agent.Status = EncompassSocialSurveyConstant.STATUS;
                            forLoanVM_seller_agent.State = subjectPropertyState;
                            forLoanVM_seller_agent.City = subjectPropertyCity;
                            forLoanVM_seller_agent.ParticipantType = EncompassSocialSurveyConstant.PARTICIPANT_TYPE_LISTING_AGENT;
                            forLoanVM_Borrower.LoanProcessorName = loanProcessorName;
                            forLoanVM_Borrower.LoanProcessorEmail = loanProcessorEmail;
                            forLoanVM_Borrower.PropertyAddress = propertyAddress;

                            returnLoansViewModel.Add(forLoanVM_seller_agent);
                        }

                    }

                    if (isProductionRun)
                    {
                        Logger.Debug("Updating last fetched time");
                        UpdateLastFetchedTime(engagementClosedTimeString);
                    }
                }
                catch (System.Exception ex)
                {
                    Logger.Error("Caught an exception: LoanUtility.LopopulateLoanList(): ", ex);

                }
            }
        }

        private string[] parseFirstAndLastName(string name)
        {
            if (string.IsNullOrWhiteSpace(name))
            {
                return new string[] { "", "" };
            }
            else
            {
                string[] names = name.Split(' ');

                if (names.Length == 1)
                {
                    return new string[] { names[0], "" };
                }
                else if (names[names.Length - 1].StartsWith("Jr") && name.Length > 2)
                {
                    return new string[] { string.Join(" ", names, 0, names.Length - 2), string.Join(" ", names, names.Length - 2, 2) };
                }
                else
                {
                    return new string[] { string.Join(" ", names, 0, names.Length - 1), names[names.Length - 1] };
                }
            }
        }

        private bool isClosedDateValid(DateTime engagementClosedTime)
        {
            if (engagementClosedTime == null || engagementClosedTime == DateTime.MinValue)
            {
                Logger.Debug("EngagementClosedTime for loan is not recieved, on to the next loan...");
                return false;
            }
            else
            {
                Logger.Debug("EngagementClosedTime for loan is : " + engagementClosedTime);

                if (DateTime.Compare(engagementClosedTime, DateTime.Now) > 0)
                {
                    Logger.Debug("Engagement cloed time " + engagementClosedTime + " is greater than current date so skipping the record");
                    return false;
                }
                return true;
            }
        }


        /// <summary>
        /// Insert count of records fetched in crm batch tracker history
        /// </summary>
        /// <param name="loanService"></param>
        /// <param name="id"></param>
        /// <param name="count"></param>
        public void InsertCrmBatchTrackerHistory(LoanService loanService, long crmBatchTrackerId, int count)
        {
            CrmBatchTrackerHistory entity = new CrmBatchTrackerHistory();
            Logger.Debug("Inside method Loan.Utility.InsertCrmBatchTrackerHistory() ");
            if (loanService != null)
            {

                entity.CrmBatchTrackerID = crmBatchTrackerId;
                entity.Status = 1;
                entity.CreatedOn = DateTime.Now;
                entity.ModifiedOn = DateTime.Now;
                entity.CreatedBy = "encompass";
                entity.ModifiedBy = "encompass";
                entity.CountOfRecordsFetched = count;

                loanService.InsertCrmBatchTrackerHistory(entity);
            }
            Logger.Debug("Exit method Loan.Utility.InsertCrmBatchTrackerHistory() ");

        }

        /// <summary>
        /// update the count of recent records fetched in crm batch tracker
        /// </summary>
        /// <param name="loanService"></param>
        /// <param name="crmBatchTracker"></param>
        /// <param name="count"></param>
        public void UpdateLastRunRecordFetechedCountInCrmBatchTracker(LoanService loanService, CRMBatchTrackerEntity crmBatchTracker, int count)
        {
            Logger.Debug("Inside method ULoanUtility.pdateRecentFetechedRecordsCountInCrmBatchTracker() ");
            if (loanService != null && crmBatchTracker != null)
            {
                crmBatchTracker.LastRunRecordFetchedCount = count;
                crmBatchTracker.ModifiedOn = DateTime.Now;
                loanService.UpdateCrmbatchTracker(crmBatchTracker);
            }

            Logger.Debug("Exiting method LoanUtility.UpdateRecentFetechedRecordsCountInCrmBatchTracker() ");
        }

        /// <summary>
        /// sends mail to admin
        /// </summary>
        /// <param name="runningCompanyId"></param>
        public void SendMailToAdminForNoRecordFetched(long runningCompanyId)
        {
            var Subject = "No Records Fetched In This Run !!!";
            var BodyText = "Encompass was not able to fetch any new records for company id " + runningCompanyId + " which ran on " + DateTime.Now;
            CommonUtility.SendMailToAdmin(Subject, BodyText);
        }

        /// <summary>
        /// updates last record fetched time 
        /// </summary>
        /// <param name="loanCloseTime"></param>
        private void UpdateLastFetchedTime(DateTime loanCloseTime)
        {
            if (DateTime.Compare(loanCloseTime, DateTime.Now) < 0)
            {
                int result = DateTime.Compare(lastFetchedTime, loanCloseTime);
                if (result < 0)
                {
                    lastFetchedTime = loanCloseTime;
                }
            }
        }

        /// <summary>
        /// insert or updates last run start time in crm batch tracker
        /// </summary>
        /// <param name="entity"></param>
        /// <param name="companyId"></param>
        /// <param name="source"></param>
        private void InsertOrUpdateLastRunStartTime(CRMBatchTrackerEntity entity, LoanService loanService, long companyId, string source)
        {

            Logger.Debug("Inside method insertOrUpdateLastRunStartTime() ");
            if (entity == null)
            {
                entity = new CRMBatchTrackerEntity();
                entity.CompanyId = companyId;
                entity.CreatedOn = DateTime.Now;
                entity.ModifiedOn = DateTime.Now;
                entity.LastRunStartDate = DateTime.Now;
                entity.LastRunEndDate = EncompassSocialSurveyConstant.EPOCH_TIME;
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

        /// <summary>
        /// updates error in crm batch tracker
        /// </summary>
        /// <param name="entity"></param>
        /// <param name="error"></param>
        private void UpdateErrorInCrmBatchTracker(CRMBatchTrackerEntity entity, String error)
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

        /// <summary>
        /// updates recent record fetch time in crm batch tracker
        /// </summary>
        /// <param name="entity"></param>
        private void UpdateRecentRecordFetchTimeInCrmBatchTracker(CRMBatchTrackerEntity entity, LoanService loanService)
        {
            Logger.Debug("Inside method updateRecentRecordFetchTimeInCrmBatchTracker() ");
            if (entity != null)
            {
                entity.RecentRecordFetchedDate = lastFetchedTime;
                entity.error = null;
                entity.ModifiedOn = DateTime.Now;
                loanService.UpdateCrmbatchTracker(entity);
            }
        }

        /// <summary>
        /// updates last run end time in crm batch tracker
        /// </summary>
        /// <param name="entity"></param>
        private void UpdateLastRunEndTimeInCrmBatchTracker(CRMBatchTrackerEntity entity, LoanService loanService)
        {
            Logger.Debug("Inside method updateLastRunEndTimeInCrmBatchTracker() ");
            if (entity != null)
            {
                entity.LastRunEndDate = DateTime.Now;
                entity.error = null;
                entity.ModifiedOn = DateTime.Now;
                loanService.UpdateCrmbatchTracker(entity);
            }
        }

        /// <summary>
        /// replace email with some prefix and domain
        /// </summary>
        /// <param name="email"></param>
        /// <param name="emailDomain"></param>
        /// <param name="emailPrefix"></param>
        /// <returns></returns>
        private string ReplaceEmailAddress(string email, string emailDomain, string emailPrefix)
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

        #endregion


        private string determineClosedDateFieldId(EncompassCredential credential)
        {
            if (!String.IsNullOrWhiteSpace(credential.fieldId))
            {
                return credential.fieldId;
            }
            else
            {
                // pick default
                return EncompassSocialSurveyConstant.TRANSACTION_CLOSED_DATE_FIELD;
            }
        }


        public StringList constructFieldList(EncompassCredential credential)
        {
            Logger.Debug("Initializing initialFieldList");
            StringList fieldIds = new StringList();

            // Loan Information
            fieldIds.Add(EncompassSocialSurveyConstant.LOAN_NUMBER_FIELD);

            // Loan Officer Information
            //fieldIds.Add(EncompassSocialSurveyConstant.LOAN_OFFICER_ID_FIELD);
            fieldIds.Add(EncompassSocialSurveyConstant.LOAN_OFFICER_NAME_FIELD);
            fieldIds.Add(EncompassSocialSurveyConstant.LOAN_OFFICER_EMAIL_FIELD);

            // Borrower Information
            fieldIds.Add(EncompassSocialSurveyConstant.BORROWER_FIRST_NAME_FIELD);
            fieldIds.Add(EncompassSocialSurveyConstant.BORROWER_LAST_NAME_FIELD);
            fieldIds.Add(EncompassSocialSurveyConstant.BORROWER_EMAIL_FIELD);

            // Co-Borrower Information
            fieldIds.Add(EncompassSocialSurveyConstant.COBORROWER_FIRST_NAME_FIELD);
            fieldIds.Add(EncompassSocialSurveyConstant.COBORROWER_LAST_NAME_FIELD);
            fieldIds.Add(EncompassSocialSurveyConstant.COBORROWER_EMAIL_FIELD);

            // Subject property information
            //fieldIds.Add(EncompassSocialSurveyConstant.SUBJECT_PROPERTY_STREET_FIELD);
            fieldIds.Add(EncompassSocialSurveyConstant.SUBJECT_PROPERTY_CITY_FIELD);
            //fieldIds.Add(EncompassSocialSurveyConstant.SUBJECT_PROPERTY_COUNTRY_FIELD);
            fieldIds.Add(EncompassSocialSurveyConstant.SUBJECT_PROPERTY_STATE_FIELD);
            //fieldIds.Add(EncompassSocialSurveyConstant.SUBJECT_PROPERTY_ZIP_FIELD);

            // Transaction date
            fieldIds.Add(determineClosedDateFieldId(credential));

            // Custom Property Address
            if (!String.IsNullOrWhiteSpace(credential.propertyAddress))
            {
                fieldIds.Add(credential.propertyAddress);
            }


            // Vender( Buyers agent / Sellers agent ) Information
            if (credential.allowPartnerSurvey)
            {
                if (!string.IsNullOrWhiteSpace(credential.buyerAgentName))
                {
                    fieldIds.Add(credential.buyerAgentName);
                }
                else
                {
                    // pick default
                    //fieldIds.Add(EncompassSocialSurveyConstant.BUYER_AGENT_NAME_FIELD);
                    //fieldIds.Add(EncompassSocialSurveyConstant.BUYER_AGENT_CONTACT_NAME_FIELD);
                }

                if (!string.IsNullOrWhiteSpace(credential.buyerAgentEmail))
                {
                    fieldIds.Add(credential.buyerAgentEmail);
                }
                else
                {
                    // pick default
                    //fieldIds.Add(EncompassSocialSurveyConstant.BUYER_AGENT_EMAIL_FIELD);
                }

                if (!string.IsNullOrWhiteSpace(credential.sellerAgentName))
                {
                    fieldIds.Add(credential.sellerAgentName);
                }
                else
                {
                    // pick default
                    //fieldIds.Add(EncompassSocialSurveyConstant.SELLER_AGENT_NAME_FIELD);
                    //fieldIds.Add(EncompassSocialSurveyConstant.SELLER_AGENT_CONTACT_NAME_FIELD);
                }

                if (!string.IsNullOrWhiteSpace(credential.sellerAgentEmail))
                {
                    fieldIds.Add(credential.sellerAgentEmail);
                }
                else
                {
                    // pick default
                    //fieldIds.Add(EncompassSocialSurveyConstant.SELLER_AGENT_EMAIL_FIELD);
                }

            }


            // Loan Processor Information
            if (!string.IsNullOrWhiteSpace(credential.loanProcessorName))
            {
                fieldIds.Add(credential.loanProcessorName);
            }
            else
            {
                // pick default
                //fieldIds.Add(EncompassSocialSurveyConstant.LOAN_PROCESSOR_NAME_FIELD);
            }


            if (!string.IsNullOrWhiteSpace(credential.loanProcessorEmail))
            {
                fieldIds.Add(credential.loanProcessorEmail);
            }
            else
            {
                // pick default
                //fieldIds.Add(EncompassSocialSurveyConstant.LOAN_PROCESSOR_EMAIL_FIELD);
            }

            return fieldIds;
        }


        private void convertTocannonicalFields(StringList fields)
        {
            if (fields != null)
            {
                for (int i = 0; i < fields.Count; i++)
                {
                    string temp = fields[i];
                    if (!temp.StartsWith("Fields."))
                    {
                        fields[i] = "Fields." + fields[i];
                    }
                }
            }
        }


    }
}
