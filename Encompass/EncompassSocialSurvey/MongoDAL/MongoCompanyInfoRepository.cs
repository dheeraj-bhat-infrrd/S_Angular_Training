using EncompassSocialSurvey.Entity;
using MongoDB.Bson.Serialization;
using MongoDB.Driver;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EncompassSocialSurvey.MongoDAL
{
    class MongoCompanyInfoRepository
    {
        private List<SoicalSurveryCompanyInfo> GetCompnayCollectionEntity()
        {
            Logger.Info("Entering the method MongoCompanyInfoRepository.GetCompnayCollectionEntity()");
            try
            {
                SSMongoContext ssContext = new SSMongoContext();
                var socialSurveyCollection = ssContext.SoicalSurveryCompanyInfos;

               

                var resultSet = socialSurveyCollection.Find<SoicalSurveryCompanyInfo>(_ => true).ToListAsync<SoicalSurveryCompanyInfo>();

                //
                Logger.Info("Exiting the method MongoCompanyInfoRepository.GetCompnayCollectionEntity()");
                //
                return resultSet.Result;
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception MongoCompanyInfoRepository.GetCompanyCredentials(): ", ex);
                throw ex;
            }
        }

        public List<CompanyCredential> GetCompanyCredentials()
        {
            Logger.Info("Entering the method MongoCompanyInfoRepository.GetCompanyCredentials()");
            List<CompanyCredential> returnValue = new List<CompanyCredential>();

            try
            {
                var listCompanyInfo = GetCompnayCollectionEntity();

                if (null != listCompanyInfo && listCompanyInfo.Count > 0)
                    Logger.Info("Total company info result found : Count : " + listCompanyInfo.Count);

                foreach (var ssCompInfo in listCompanyInfo)
                {
                    //
                    if (null != ssCompInfo.crm_info)
                        Logger.Info("Not to include: crm_info.crm_source : " + ssCompInfo.crm_info.crm_source);

                    if (ssCompInfo.crm_info != null && ssCompInfo.crm_info.crm_source == EncompassSocialSurveyConstant.COMPANY_CREDENTIALS_CRM_SOURCE)
                    {
                        CompanyCredential forCompCredential = new CompanyCredential();

                        // TODO: Raushan check with Nishit
                        forCompCredential.EncompassCredential.CompanyId = ssCompInfo.crm_info.companyId;

                        //
                        forCompCredential.EncompassCredential.EncompassUrl = ssCompInfo.crm_info.url;
                        forCompCredential.EncompassCredential.UserName = ssCompInfo.crm_info.crm_username;
                        forCompCredential.EncompassCredential.Password = ssCompInfo.crm_info.crm_password;
                        forCompCredential.EncompassCredential.fieldId = ssCompInfo.crm_info.crm_fieldId;

                        forCompCredential.EncompassCredential.numberOfDays = ssCompInfo.crm_info.numberOfDays;
                        forCompCredential.EncompassCredential.emailAddressForReport = ssCompInfo.crm_info.emailAddressForReport;
                        
                        returnValue.Add(forCompCredential);
                    }
                }

            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception:MongoCompanyInfoRepository.GetCompanyCredentials()", ex);
                throw;
            }

            Logger.Info("Exiting the method MongoCompanyInfoRepository.GetCompanyCredentials()");
            return returnValue;
        }
    }
}
