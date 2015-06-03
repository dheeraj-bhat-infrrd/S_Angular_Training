using EncompassSocialSurvey.Entity;
using EncompassSocialSurvey.MongoDAL;
using System.Collections.Generic;

namespace EncompassSocialSurvey.Service
{
    public class CompanyCredentialService
    {

        // based on the given json object get the company details
        public List<CompanyCredential> GetCompanyCredentials()
        {
            List<CompanyCredential> returnValue = new List<CompanyCredential>();

            //// TODO: Get the credentials from json file
            //returnValue.Add(new CompanyCredential()
            //{
            //    EncompassUrl = "https://TEBE11026010.ea.elliemae.net$TEBE11026010",
            //    UserName = "realtech",
            //    Password = "123456"
            //});

            MongoCompanyInfoRepository _companyInfoRepository = new MongoCompanyInfoRepository();
            returnValue = _companyInfoRepository.GetCompanyCredentials();

            //
            return returnValue;
        }
    }
}
