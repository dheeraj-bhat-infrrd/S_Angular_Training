
using EllieMae.Encompass.BusinessObjects.Loans;
using EllieMae.Encompass.BusinessObjects.Users;
using EllieMae.Encompass.Collections;
using EncompassSocialSurvey.ViewModel;
using System.Collections;
using System.Collections.Generic;

namespace EncompassSocialSurvey
{
    public class LoanUtility
    {
        // Populates the loan list with the contents of a folder
        public ArrayList PopulateLoanFolderList()
        {
            Logger.Info("Entering the method LoanUtility.PopulateLoanFolderList()");
            //build data source 
            ArrayList folders = new ArrayList();

            try
            {
                // Load the list with the identities of the loans
                foreach (LoanFolder folder in EncompassGlobal.EncompassLoginSession.Loans.Folders)
                    folders.Add(folder.Name);

                //Sort folders
                folders.Sort();
            }
            catch (System.Exception ex)
            {
                Logger.Error("Caught an exception: LoanUtility.PopulateLoanFolderList()", ex);
                throw ex;
            }

            //
            Logger.Info("Exiting the method LoanUtility.PopulateLoanFolderList()");
            return folders;
        }

        public List<LoanViewModel> LopulateLoanListWithKey(LoanFolder parentFolder, long runningCompanyId)
        {
            Logger.Info("Entering the method LoanUtility.LopopulateLoanList()");

            // Get the contents of the folder
            LoanIdentityList loans = parentFolder.GetContents();

            List<LoanViewModel> returnLoansViewModel = null;

            if (null == loans) return returnLoansViewModel;
            if (loans.Count <= 0) return returnLoansViewModel;

            try
            {
                returnLoansViewModel = new List<LoanViewModel>();
                StringList fieldIds = new StringList();

                #region Popualted FieldIds // list of ids to get the details from loan

                fieldIds.Add("364");         // Loan Number
                fieldIds.Add("LoanTeamMember.Name.Loan Officer"); // Loan Processor Name

                fieldIds.Add("36");          // Customer First Name
                fieldIds.Add("37");          // Customer Last Name
                fieldIds.Add("1240");        // CustomerEmailId

                fieldIds.Add("68");    // Co-BorrowerFirstName
                fieldIds.Add("69");    // Co-BorrowerLastName
                fieldIds.Add("1268");  // Co-BorrowerEmailId

                fieldIds.Add("748");      // closed date

                #endregion  // Popualted FieldIds // list of ids to get the details from loan


                string surveySource = EncompassSocialSurverConstant.SURVEY_SOURCE;

                // company id coming 
                long companyId = runningCompanyId;
                string lastReminderTime = EncompassSocialSurverConstant.LAST_REMINDER_TIME;
                int reminderCount = EncompassSocialSurverConstant.REMINDER_COUNT;
                int status = EncompassSocialSurverConstant.STATUS;

                #region Load the list
                // Load the list with the identities of the loans
                foreach (LoanIdentity id in loans)
                {
                    //  dataRow = dataTable.NewRow();
                    StringList fieldValues = EncompassGlobal.EncompassLoginSession.Loans.SelectFields(id.Guid, fieldIds);

                    // Open the loan using the GUID specified on the command line
                    Loan loan = EncompassGlobal.EncompassLoginSession.Loans.Open(id.Guid);
                    User loanOfficer = EncompassGlobal.EncompassLoginSession.Users.GetUser(loan.LoanOfficerID);


                    // TODO: Raushan: uncommend this line of code: Consider only the closed loans
                    // if loan is not closed " closed field value will be null/empty/[//]
                    if (string.IsNullOrWhiteSpace(fieldValues[8]) || fieldValues[8].Equals("//") || fieldValues[8].Equals(@"\\"))
                        continue;

                    // if loan is already closed then
                    LoanViewModel forLoanVM_Borrower = new LoanViewModel();

                    forLoanVM_Borrower.SurveySource = surveySource;

                    // remove the flower bracket from GUID
                    forLoanVM_Borrower.SurveySourceId = id.Guid.ToString().Replace("{", "").Replace("}", "");

                    // TODO: incoming company id
                    forLoanVM_Borrower.CompanyId = companyId;

                    // TODO: not getting any field for agent id: keeping loan number as agent id
                    forLoanVM_Borrower.AgentId = fieldValues[0];
                    forLoanVM_Borrower.AgentName = fieldValues[1];

                    forLoanVM_Borrower.AgentId = loanOfficer.ID;
                    forLoanVM_Borrower.AgentName = loanOfficer.FullName;

                    forLoanVM_Borrower.CustomerFirstName = fieldValues[2];
                    forLoanVM_Borrower.CustomerLastName = fieldValues[3];
                    forLoanVM_Borrower.CustomerEmailId = fieldValues[4];

                    // TODO: not getting any field for reminder count: by default set it by 0
                    forLoanVM_Borrower.ReminderCounts = reminderCount;

                    // TODO: not getting any field for LastReminderTime
                    forLoanVM_Borrower.LastReminderTime = lastReminderTime;

                    forLoanVM_Borrower.EngagementClosedTime = fieldValues[8];
                    forLoanVM_Borrower.Status = status;

                    returnLoansViewModel.Add(forLoanVM_Borrower);

                    // if co-borrower value is there, then add one more item
                    if ((string.IsNullOrWhiteSpace(fieldValues[5]) && string.IsNullOrWhiteSpace(fieldValues[6])) == false)
                    {
                        LoanViewModel forLoanVM_Co_Borrower = new LoanViewModel();
                        forLoanVM_Co_Borrower.SurveySource = surveySource;
                        forLoanVM_Co_Borrower.SurveySourceId = id.Guid;
                        forLoanVM_Co_Borrower.CompanyId = companyId;

                        // TODO: not getting any field for agent id: keeping loan number as agent id
                        forLoanVM_Co_Borrower.AgentId = fieldValues[0];
                        forLoanVM_Co_Borrower.AgentName = fieldValues[1];

                        forLoanVM_Co_Borrower.AgentId = loanOfficer.ID;
                        forLoanVM_Co_Borrower.AgentName = loanOfficer.FullName;

                        forLoanVM_Co_Borrower.CustomerFirstName = fieldValues[5];
                        forLoanVM_Co_Borrower.CustomerLastName = fieldValues[6];
                        forLoanVM_Co_Borrower.CustomerEmailId = fieldValues[7];

                        forLoanVM_Co_Borrower.ReminderCounts = reminderCount;

                        // TODO: not getting any field for LastReminderTime
                        forLoanVM_Co_Borrower.LastReminderTime = lastReminderTime;

                        forLoanVM_Co_Borrower.EngagementClosedTime = fieldValues[8];
                        forLoanVM_Co_Borrower.Status = status;

                        //
                        returnLoansViewModel.Add(forLoanVM_Co_Borrower);
                    }
                }

                #endregion // Load the list
            }
            catch (System.Exception ex)
            {
                Logger.Error("Caught an exception: LoanUtility.LopopulateLoanList(): ", ex);
                throw;
            }

            Logger.Info("Exiting the method LoanUtility.LopopulateLoanList()");
            return returnLoansViewModel;
        }

        public List<LoanViewModel> LopulateLoanList(LoanFolder parentFolder, long runningCompanyId, string fieldid, string emailDomain, string emailPrefix)
        {
            Logger.Info("Entering the method LoanUtility.LopopulateLoanList(): FolderName: " + parentFolder.DisplayName);

            // Get the contents of the folder
            LoanIdentityList loans = parentFolder.GetContents();

            List<LoanViewModel> returnLoansViewModel = null;

            if (null == loans) return returnLoansViewModel;
            if (loans.Count <= 0) return returnLoansViewModel;

            try
            {
                returnLoansViewModel = new List<LoanViewModel>();
                StringList fieldIds = new StringList();

                #region Popualted FieldIds // list of ids to get the details from loan

                fieldIds.Add("364");         // Loan Number
                fieldIds.Add("LoanTeamMember.Name.Loan Officer"); // Loan Processor Name
                fieldIds.Add("36");          // Customer First Name
                fieldIds.Add("37");          // Customer Last Name
                fieldIds.Add("1240");        // CustomerEmailId

                fieldIds.Add("68");    // Co-BorrowerFirstName
                fieldIds.Add("69");    // Co-BorrowerLastName
                fieldIds.Add("1268");  // Co-BorrowerEmailId

                if(string.IsNullOrWhiteSpace(fieldid))
                {
                    fieldIds.Add("748");   // closed date
                }
                else
                {
                    fieldIds.Add(fieldid); //User provided field
                }
                    
              
                #endregion  // Popualted FieldIds // list of ids to get the details from loan


                string surveySource = EncompassSocialSurverConstant.SURVEY_SOURCE;

                // company id coming 
                long companyId = runningCompanyId;
                string lastReminderTime = EncompassSocialSurverConstant.LAST_REMINDER_TIME;
                int reminderCount = EncompassSocialSurverConstant.REMINDER_COUNT;
                int status = EncompassSocialSurverConstant.STATUS;

                #region Load the list
                // Load the list with the identities of the loans
                foreach (LoanIdentity id in loans)
                {
                    //  dataRow = dataTable.NewRow();
                    StringList fieldValues = EncompassGlobal.EncompassLoginSession.Loans.SelectFields(id.Guid, fieldIds);


                    // TODO: Raushan: uncommend this line of code: Consider only the closed loans
                    // if loan is not closed " funded field value will be null/empty/[//]
                    if (string.IsNullOrWhiteSpace(fieldValues[8]) || fieldValues[8].Equals("//") || fieldValues[8].Equals(@"\\"))
                    {
                        Logger.Info("Exiting the method LoanUtility.LopopulateLoanList(): It's not a closed loan. : LoanGUID : " + id.Guid);
                        continue;
                    }

                    // Open the loan using the GUID specified on the command line
                    Loan runningLoan = EncompassGlobal.EncompassLoginSession.Loans.Open(id.Guid);
                    User loanOfficer = null;
                    if (false == string.IsNullOrWhiteSpace(runningLoan.LoanOfficerID))
                        loanOfficer = EncompassGlobal.EncompassLoginSession.Users.GetUser(runningLoan.LoanOfficerID);

                    // if loan is already closed then
                    LoanViewModel forLoanVM_Borrower = new LoanViewModel();

                    forLoanVM_Borrower.SurveySource = surveySource;

                    // remove the flower bracket from GUID
                    forLoanVM_Borrower.SurveySourceId = id.Guid.ToString().Replace("{", "").Replace("}", "");

                    // TODO: incoming company id
                    forLoanVM_Borrower.CompanyId = companyId;

                    // TODO: not getting any field for agent id: keeping loan number as agent id
                    //forLoanVM_Borrower.AgentId = fieldValues[0];
                    //forLoanVM_Borrower.AgentName = fieldValues[1];
                   
                   
                    forLoanVM_Borrower.AgentId = (loanOfficer != null) ? loanOfficer.ID : "";
                    forLoanVM_Borrower.AgentName = (loanOfficer != null) ? loanOfficer.FullName : "";
                    forLoanVM_Borrower.AgentEmailId = (loanOfficer != null) ? loanOfficer.Email : "";

                    forLoanVM_Borrower.CustomerFirstName = fieldValues[2];
                    forLoanVM_Borrower.CustomerLastName = fieldValues[3];


                    string emailId = fieldValues[4];

                    if (string.IsNullOrWhiteSpace(emailDomain))
                    {

                        forLoanVM_Borrower.CustomerEmailId = emailId;
                    }
                    else
                    {
                        forLoanVM_Borrower.CustomerEmailId = replaceEmailAddress(emailId, emailDomain, emailPrefix);
                    }




                    // TODO: not getting any field for reminder count: by default set it by 0
                    forLoanVM_Borrower.ReminderCounts = reminderCount;

                    // TODO: not getting any field for LastReminderTime
                    forLoanVM_Borrower.LastReminderTime = lastReminderTime;

                    forLoanVM_Borrower.EngagementClosedTime = fieldValues[8];
                    forLoanVM_Borrower.Status = status;

                    returnLoansViewModel.Add(forLoanVM_Borrower);

                    // if co-borrower value is there, then add one more item
                    if ((string.IsNullOrWhiteSpace(fieldValues[5]) && string.IsNullOrWhiteSpace(fieldValues[6])) == false)
                    {
                        LoanViewModel forLoanVM_Co_Borrower = new LoanViewModel();
                        forLoanVM_Co_Borrower.SurveySource = surveySource;
                        forLoanVM_Co_Borrower.SurveySourceId = id.Guid.ToString().Replace("{", "").Replace("}", "");
                        forLoanVM_Co_Borrower.CompanyId = companyId;

                        // TODO: not getting any field for agent id: keeping loan number as agent id
                        //forLoanVM_Co_Borrower.AgentId = fieldValues[0];
                        //forLoanVM_Co_Borrower.AgentName = fieldValues[1];

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

                        

                        forLoanVM_Co_Borrower.ReminderCounts = reminderCount;

                        // TODO: not getting any field for LastReminderTime
                        forLoanVM_Co_Borrower.LastReminderTime = lastReminderTime;

                        forLoanVM_Co_Borrower.EngagementClosedTime = fieldValues[8];
                        forLoanVM_Co_Borrower.Status = status;

                        //
                        returnLoansViewModel.Add(forLoanVM_Co_Borrower);
                    }

                    // close the loan
                    if (null != runningLoan)
                        runningLoan.Close();
                }

                #endregion // Load the list
            }
            catch (System.Exception ex)
            {
                Logger.Error("Caught an exception: LoanUtility.LopopulateLoanList(): ", ex);
                throw;
            }

            Logger.Info("Exiting the method LoanUtility.LopopulateLoanList()");
            return returnLoansViewModel;
        }


        private string replaceEmailAddress(string email, string emailDomain, string emailPrefix)
        {
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
