using EncompassSocialSurvey.DAL;
using EncompassSocialSurvey.Translator;
using EncompassSocialSurvey.ViewModel;
using System;
using System.Collections.Generic;
using EncompassSocialSurvey.Entity;

namespace EncompassSocialSurvey.Service
{
    public class LoanService
    {
        public CRMBatchTrackerEntity getCrmBatchTracker(long companyId, string source)
        {
            Logger.Debug("Inside method getCrmBatchTracker");
            LoanRepository loanRepo = new LoanRepository();
            CRMBatchTrackerEntity entity = null;
            try
            {
                entity = loanRepo.getCrmBatchTrackerByCompanyAndSource(companyId, source);
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanService.getCrmBatchTracker(): ", ex);
                throw;
            }
            return entity;
        }
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
    }
}
