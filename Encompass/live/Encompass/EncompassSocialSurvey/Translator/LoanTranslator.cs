using EncompassSocialSurvey.Entity;
using EncompassSocialSurvey.ViewModel;
using System;
using System.Collections.Generic;

namespace EncompassSocialSurvey.Translator
{
    public class LoanTranslator
    {
        public List<LoanEntity> GetLoanEntity(List<LoanViewModel> inputLoansVM)
        {
            Logger.Info("Entering the method LoanTranslator.GetLoanEntity(List<>)");
            List<LoanEntity> returnLoanEntity = new List<LoanEntity>();

            try
            {
                foreach (var loanVM in inputLoansVM)
                {
                    LoanEntity forLoanEntity = GetLoanEntity(loanVM);
                    if (forLoanEntity != null) returnLoanEntity.Add(forLoanEntity);
                }
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanTranslator.GetLoanEntity(List<>): ", ex);
                throw ex;
            }

            Logger.Info("Exiting the method LoanTranslator.GetLoanEntity(List<>)");
            return returnLoanEntity;
        }

        public LoanEntity GetLoanEntity(LoanViewModel loanVM)
        {
            Logger.Info("Entering the method LoanTranslator.GetLoanEntity()");
            if (loanVM == null) return null;

            Logger.Info("EngagementClosedTime for loan id " + loanVM.SurveySourceId + " is : " + loanVM.EngagementClosedTime);
            if (string.IsNullOrEmpty(loanVM.EngagementClosedTime) || loanVM.EngagementClosedTime.Equals("//") || loanVM.EngagementClosedTime.Equals("00/00/0000"))
            {
                Logger.Debug("EngagementClosedTime for loan " + loanVM.SurveySourceId + " is inappropriate");
                loanVM.EngagementClosedTime = EncompassSocialSurveyConstant.DEFAULT_ENGAGEMENT_CLOSE_TIME;
                Logger.Debug("Updated EngagementClosedTime for loan " + loanVM.SurveySourceId + " is " + loanVM.EngagementClosedTime);
            }
            
            LoanEntity returnLoanEntity = null;
            try
            {
                //
                returnLoanEntity = new LoanEntity()
                {
                    SurveySource = loanVM.SurveySource,
                    SurveySourceId = loanVM.SurveySourceId,
                    AgentId = loanVM.AgentId,
                    AgentName = loanVM.AgentName,
                    AgentEmailId = loanVM.AgentEmailId,

                    CompanyId = loanVM.CompanyId,
                    CustomerFirstName = loanVM.CustomerFirstName,
                    CustomerLastName = loanVM.CustomerLastName,
                    CustomerEmailId = loanVM.CustomerEmailId,

                    EngagementClosedTime = CommonUtility.ConvertStringToDateTime(loanVM.EngagementClosedTime),
                    ReminderCounts = loanVM.ReminderCounts,
                    LastReminderTime = CommonUtility.ConvertStringToDateTime(loanVM.LastReminderTime),
                    Status = loanVM.Status,
                    CreatedOn = DateTime.Now,
                    State = loanVM.State,
                    City = loanVM.City,
                    ParticipantType = loanVM.ParticipantType,
                    PropertyAddress = loanVM.PropertyAddress,
                    LoanProcessorName = loanVM.LoanProcessorName,
                    LoanProcessorEmail = loanVM.LoanProcessorEmail,
                    customFieldOne = loanVM.customFieldOne,
                    customFieldTwo = loanVM.customFieldTwo,
                    customFieldThree = loanVM.customFieldThree,
                    customFieldFour = loanVM.customFieldFour,
                    customFieldFive = loanVM.customFieldFive,

                };
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanTranslator.GetLoanEntity(): ", ex);
                throw ex;
            }

            //
            Logger.Info("Exiting the method LoanTranslator.GetLoanEntity()");
            return returnLoanEntity;
        }
    }
}
