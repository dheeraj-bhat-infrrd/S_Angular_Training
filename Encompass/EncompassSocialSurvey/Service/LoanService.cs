using EncompassSocialSurvey.DAL;
using EncompassSocialSurvey.Translator;
using EncompassSocialSurvey.ViewModel;
using System;
using System.Collections.Generic;

namespace EncompassSocialSurvey.Service
{
    public class LoanService
    {
        public bool InsertLoans(List<LoanViewModel> loansVM)
        {
            Logger.Info("Entering the method LoanService.InsertLoans(List<>)");
            bool returnValue = false;

            try
            {
                // if no loans to process check for next loan folder
                if (null == loansVM && loansVM.Count <= 0) return returnValue;

                // 2nd convert loans vm to loanEntity
                LoanTranslator loanTranslator = new LoanTranslator();
                var loansEntity = loanTranslator.GetLoanEntity(loansVM);

                // 3rd now insert the records into db
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
    }
}
