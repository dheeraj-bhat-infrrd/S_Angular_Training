using EncompassSocialSurvey.Entity;
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

                //
                var deleteIt  = ssContext.Database.ListCollectionsAsync().Result;

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

                foreach (var ssCompInfo in listCompanyInfo)
                {
                    if (ssCompInfo.crm_info != null && ssCompInfo.crm_info.crm_source == EncompassSocialSurverConstant.COMPANY_CREDENTIALS_CRM_SOURCE)
                    {
                        CompanyCredential forCompCredential = new CompanyCredential();

                        // TODO: Raushan check with Nishit
                        forCompCredential.CompanyId =  0; // ssCompInfo.crm_info.Com

                        //
                        forCompCredential.EncompassUrl = ssCompInfo.crm_info.url;
                        forCompCredential.UserName = ssCompInfo.crm_info.crm_username;
                        forCompCredential.Password = ssCompInfo.crm_info.crm_password;

                        //
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
