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
        public bool InsertLoans(List<LoanViewModel> loansVM)
        {
            Logger.Info("Entering the method LoanService.InsertLoans(List<>):");
            bool returnValue = false;

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
                var newLine = string.Format("{0},{1},{2},{3},{4},{5}", "Customer First Name", "Customer Last Name", "Customer Email Address", "Agent Email Address", "Loan Id", "Engagement Closed Time");
                csv.AppendLine(newLine);
                if (null != loansVM)
                {
                    foreach (var loanVM in loansVM)
                    {
                        newLine = null;
                        newLine = string.Format("{0},{1},{2},{3},{4},{5}", loanVM.CustomerFirstName, loanVM.CustomerLastName, loanVM.CustomerEmailId, loanVM.AgentEmailId, loanVM.SurveySourceId, loanVM.EngagementClosedTime);
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
        public String CreateExcelSpreadSheetForLoanlist(List<LoanViewModel> loansVM , String companyName)
        {
            Logger.Info("Entering the method LoanService.createExcelSpreadSheetForLoanlist for company : " + companyName);
            Excel.Application xlApp = new Microsoft.Office.Interop.Excel.Application();
            if (xlApp == null)
            {
                Logger.Error("Error while creating excel sheet for company. Can't able to create excel application object" );
                return null;
            }

            Excel.Workbook xlWorkBook;
            Excel.Worksheet xlWorkSheet;
            object misValue = System.Reflection.Missing.Value;

            try {
                xlWorkBook = xlApp.Workbooks.Add(misValue);
                xlWorkSheet = (Microsoft.Office.Interop.Excel.Worksheet)xlWorkBook.ActiveSheet;
                xlWorkSheet.Cells[1, 1] = "Customer First Name";
                xlWorkSheet.Cells[1, 2] = "Customer Last Name";
                xlWorkSheet.Cells[1, 3] = "Customer Email Address";
                xlWorkSheet.Cells[1, 4] = "Agent Email Address";
                xlWorkSheet.Cells[1, 5] = "Loan Id";
                xlWorkSheet.Cells[1, 6] = "Engagement Closed Time";

                int rowCount = 1;
                if (null != loansVM)
                {
                    foreach (var loanVM in loansVM)
                    {
                        rowCount++;
                        xlWorkSheet.Cells[rowCount, 1] = loanVM.CustomerFirstName;
                        xlWorkSheet.Cells[rowCount, 2] = loanVM.CustomerLastName;
                        xlWorkSheet.Cells[rowCount, 3] = loanVM.CustomerEmailId;
                        xlWorkSheet.Cells[rowCount, 4] = loanVM.AgentEmailId;
                        xlWorkSheet.Cells[rowCount, 5] = loanVM.SurveySourceId;
                        xlWorkSheet.Cells[rowCount, 6] = loanVM.EngagementClosedTime;
                    }
                }


                var celLrangE = xlWorkSheet.Range[xlWorkSheet.Cells[1, 1], xlWorkSheet.Cells[rowCount, 6]];
                celLrangE.EntireColumn.AutoFit();

                String fileName = companyName + "_" + DateTime.Now.ToString("yyyy-MM-dd HH-mm-ss") + ".xlsx";
                String filePath = EncompassSocialSurveyConfiguration.TempFolderPath + Path.DirectorySeparatorChar + fileName;
                xlWorkBook.SaveAs(filePath, Excel.XlFileFormat.xlWorkbookNormal, misValue, misValue, misValue, misValue, Excel.XlSaveAsAccessMode.xlExclusive, misValue, misValue, misValue, misValue, misValue);
                xlWorkBook.Close(true, misValue, misValue);
                xlApp.Quit();
                Logger.Debug("Successfully created user report for compnay : " + companyName + " at : " + filePath);
                return filePath;
            }catch(Exception e){
                Logger.Error("Error while generating excel sheet for company " + companyName + " Message : " + e.Message);
                throw e;
            }                   
        }

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

        #endregion
    }
}
