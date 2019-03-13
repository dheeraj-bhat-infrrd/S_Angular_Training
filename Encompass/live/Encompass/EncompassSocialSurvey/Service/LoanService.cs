using EncompassSocialSurvey.DAL;
using EncompassSocialSurvey.Translator;
using EncompassSocialSurvey.ViewModel;
using System;
using System.Collections.Generic;
using EncompassSocialSurvey.Entity;
using Excel = Microsoft.Office.Interop.Excel;
using System.IO;

namespace EncompassSocialSurvey.Service
{
    public class LoanService   
    {

        private static string tempFolderPath = EncompassSocialSurveyConfiguration.TempFolderPath;

        #region public methods

        /// <summary>
        /// Gets crm batch tracker
        /// </summary>
        /// <param name="companyId"></param>
        /// <param name="source"></param>
        /// <returns></returns>
        public CRMBatchTrackerEntity getCrmBatchTracker(long companyId, string source)
        {
            Logger.Debug("Inside method getCrmBatchTracker");
            LoanRepository loanRepo = new LoanRepository();
            CRMBatchTrackerEntity entity = null;
            try
            {
                entity = loanRepo.GetCrmBatchTrackerByCompanyAndSource(companyId, source);
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanService.getCrmBatchTracker(): ", ex);
                throw;
            }
            return entity;
        }

        /// <summary>
        /// checks if company exist or not
        /// </summary>
        /// <param name="companyId"></param>
        /// <returns></returns>
        public Boolean isCompanyActive(long companyId)
        {
            Logger.Debug("Inside method getCompany");
            LoanRepository loanRepo = new LoanRepository();
            Company company = null;
            try
            {
                company = loanRepo.GetCompanyById(companyId);
                if(company.status.Equals(EncompassSocialSurveyConstant.COMPANY_INACTIVE)){
                    return false;
                }else{
                    return true;
                }
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanService.getCompany(): ", ex);
                throw;
            }
        }

        /// <summary>
        /// updates crm batch tracker
        /// </summary>
        /// <param name="entity"></param>
        public void UpdateCrmbatchTracker(CRMBatchTrackerEntity entity)
        {
            Logger.Debug("Inside method updateCrmBatchTracker");
               LoanRepository loanRepo = new LoanRepository();
            try {
                loanRepo.UpdateCrmBatchTracker(entity);
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanService.UpdateCrmbatchTracker(): ", ex);
                throw;
            }

        }

        /// <summary>
        /// Insert crm batch tracker
        /// </summary>
        /// <param name="entity"></param>
        public void InsertCrmBatchTracker(CRMBatchTrackerEntity entity)
        {
            Logger.Info("Inside methid InsertCrmBatchTracker for Company " + entity.CompanyId);
            Logger.Debug("Insert the record into db");
            LoanRepository loanRepo = new LoanRepository();
            try {
                loanRepo.InsertCRMBatchTracker(entity);
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanService.InsertCrmBatchTracker(): ", ex);
                throw;
            }

            

        }

        /// <summary>
        /// Insert Loan
        /// </summary>
        /// <param name="loansVM"></param>
        /// <returns></returns>
        public int InsertLoans(List<LoanViewModel> loansVM)
        {
            Logger.Info("Entering the method LoanService.InsertLoans(List<>):");
            int returnValue = 0;

            try
            {
                if (null == loansVM && loansVM.Count <= 0) return returnValue;

                Logger.Debug("Convert loan object into laon entity ");
                LoanTranslator loanTranslator = new LoanTranslator();
                var loansEntity = loanTranslator.GetLoanEntity(loansVM);

                Logger.Debug("Insert the record into db");
                LoanRepository loanRepo = new LoanRepository();
                returnValue = loanRepo.InserLoan(loansEntity);
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanService.InsertLoans(List<>): ", ex);
                throw;
            }

            Logger.Info("Exiting the method LoanService.InsertLoans(List<>)");
            return returnValue;
        }

        /// <summary>
        /// send loan reports
        /// </summary>
        /// <param name="filePath"></param>
        /// <param name="emailAddresses"></param>
        /// <param name="noOfDays"></param>
        public void SendLoanReportToEmailAddresses(String filePath, String emailAddresses, int noOfDays)
        {
            Logger.Info("Entering the method LoanService.sendLoanReportToEmailAddresses");
            try
            {
                char[] seperator = { ',' };
                if (emailAddresses == null || emailAddresses.Equals(""))
                {
                    throw new Exception("Parameter emailAddresses can't be null or empty");
                }

                string[] emailList = Array.ConvertAll(emailAddresses.Split(seperator, StringSplitOptions.RemoveEmptyEntries), p => p.Trim());
                Logger.Debug("Email address to send user record list are : " + emailList.ToString());
                String subject = "Results from encompass for last : " + noOfDays + " days ";
                String body = "Hi, \n Please find the list of customers fetched from encompass for last : " + noOfDays + " days ";
                CommonUtility.SendMailToEmailAdresses(subject, body, emailList, filePath);
            }
            catch (Exception ex)
            {
                Logger.Error("Error while sending report to customer" + ex.Message);
                throw ex;
            }
        }

        /// <summary>
        /// creates csv report
        /// </summary>
        /// <param name="loansVM"></param>
        /// <param name="companyName"></param>
        /// <returns></returns>
        public String CreateLoanListCSV(List<LoanViewModel> loansVM, String companyName)
        {
            Logger.Info("Entering the method LoanService.createLoanListCSV for company : " + companyName);
            try
            {
                String fileName = companyName + "_" + DateTime.Now.ToString("yyyy-MM-dd HH-mm-ss") + ".csv";
                String filePath = EncompassSocialSurveyConfiguration.TempFolderPath + Path.DirectorySeparatorChar + fileName;
                // Write header
                var csv = new System.Text.StringBuilder();
                var newLine = string.Format("{0},{1},{2},{3},{4},{5},{6},{7},{8},{9},{10},{11},{12},{13},{14},{15}", "Loan Number", "Customer First Name", "Customer Last Name", "Customer Email Address", "Agent Email Address", "Loan Id", "Engagement Closed Time", "Participant Type","Loan Processor Name", "Loan Processor Email", "Property Address", "Custom Field One", "Custom Field Two", "Custom Field Three", "Custom Field Four", "Custom Field Five");
                csv.AppendLine(newLine);
                if (null != loansVM)
                {
                    foreach (var loanVM in loansVM)
                    {
                        String ParticipantType = GetParticipantType(loanVM.ParticipantType);

                        Logger.Info("CustomerFirstName is  : " + loanVM.CustomerFirstName + " for loan " + loanVM.SurveySourceId + " and participant type ParticipantType");


                        newLine = null;
                        newLine = string.Format("\"{0}\",\"{1}\",\"{2}\",\"{3}\",\"{4}\",\"{5}\",\"{6}\",\"{7}\",\"{8}\",\"{9}\",\"{10}\",\"{11}\",\"{12}\",\"{13}\",\"{14}\",\"{15}\"", loanVM.LoanNumber, loanVM.CustomerFirstName, loanVM.CustomerLastName, loanVM.CustomerEmailId, loanVM.AgentEmailId, loanVM.SurveySourceId, loanVM.EngagementClosedTime, ParticipantType, loanVM.LoanProcessorName, loanVM.LoanProcessorEmail, loanVM.PropertyAddress, loanVM.customFieldOne, loanVM.customFieldTwo, loanVM.customFieldThree, loanVM.customFieldFour, loanVM.customFieldFive);
                        csv.AppendLine(newLine);
                    }
                }
                File.WriteAllText(filePath, csv.ToString());
                return filePath;
            }
            catch (Exception e) {
                Logger.Error("Error while generating excel sheet for company " + companyName + " Message : " + e.Message);
                throw e;
            }
        }

        /// <summary>
        /// cretate excel sheet for loans
        /// </summary>
        /// <param name="loansVM"></param>
        /// <param name="companyName"></param>
        /// <returns></returns>
     
        /// <summary>
        /// insert crm batch tracker history with count of records fetched
        /// </summary>
        /// <param name="entity"></param>
        public void InsertCrmBatchTrackerHistory(CrmBatchTrackerHistory entity)
        {
            Logger.Info("Inside method LoanService.InsertCrmBatchTrackerHistory for CRM ID: " + entity.CrmBatchTrackerID);
            Logger.Debug("Insert the record into db");
            LoanRepository loanRepo = new LoanRepository();
            try
            {
                loanRepo.InsertCRMBatchTrackerHistory(entity);
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanService.InsertCrmBatchTrackerHistory(): ", ex);
                throw;
            }
            Logger.Info("Exit method LoanService.InsertCrmBatchTrackerHistory for CRM ID: " + entity.CrmBatchTrackerID);
        }

        private String GetParticipantType(int participantTypeInt)
        {
            String participantType = "";
            if (participantTypeInt == EncompassSocialSurveyConstant.PARTICIPANT_TYPE_BORROWER)
            {
                participantType = "Borrower";
            }
            else if (participantTypeInt == EncompassSocialSurveyConstant.PARTICIPANT_TYPE_CO_BORROWER)
            {
                participantType = "CoBorrower";
            }
            else if (participantTypeInt == EncompassSocialSurveyConstant.PARTICIPANT_TYPE_BUYER_AGENT)
            {
                participantType = "Buyer Agent";
            }
            else if (participantTypeInt == EncompassSocialSurveyConstant.PARTICIPANT_TYPE_LISTING_AGENT)
            {
                participantType = "Seller Agent";
            }
            return participantType;
        }

        #endregion
    }
    
}
