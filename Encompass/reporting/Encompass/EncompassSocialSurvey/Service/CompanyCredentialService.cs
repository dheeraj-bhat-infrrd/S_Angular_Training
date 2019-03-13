using EncompassSocialSurvey.Entity;
using EncompassSocialSurvey.MongoDAL;
using System;
using System.Collections.Generic;
using System.IO;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Web.Script.Serialization;

namespace EncompassSocialSurvey.Service
{
    public class CompanyCredentialService
    {

        public void DisableReportGenerationForCompany(long companyId) {
            Logger.Info("Inside method DisableReportGenerationForCompany for companyId : " + companyId);

            HttpClient client = new HttpClient();

            var url = EncompassSocialSurveyConfiguration.disableGenerateReportUrl + "/" + companyId;
            Logger.Debug("url for http request is :" + url);

            var response = client.GetAsync(url).Result;

            if (response.IsSuccessStatusCode)
            {
                var responseContent = response.Content;
                // by calling .Result you are synchronously reading the result
                string responseString = responseContent.ReadAsStringAsync().Result;
                Logger.Debug("Response of http request is : " + responseString);
            }
            else {
                Logger.Debug("Error while disabling generate report for company from http request for companyId : " + companyId);
                string Subject = "Error while disabling generate report for company from http request";
                string BodyText = "An error has been occurred disabling generate report for company from http request for companyID   : " + companyId + " on " + DateTime.Now + ".";
                BodyText += " \n Response of HTTP request is : " + response.ToString();
                CommonUtility.SendMailToAdmin(Subject, BodyText);
            }


            Logger.Info("method DisableReportGenerationForCompany for companyId : " + companyId + "ended");
              
        }

        public void SendMailToEncompassAdmin(long companyId, String errorMessage)
        {
            Logger.Info("Inside method sendErrorMail for companyId : " + companyId);

            HttpClient client = new HttpClient();
            
            var url = EncompassSocialSurveyConfiguration.sendErrorMailUrl ;
            Logger.Debug("url for http request is :" + url);

            var jsonString = "{\"companyId\":"+companyId+",\"message\":"+ "\""+errorMessage+"\""+"}";

            Logger.Info("Json : " + jsonString);

            var httpContent = new StringContent(jsonString, Encoding.UTF8, "application/json");

            var response = client.PostAsync(url, httpContent).Result;

            if (response.IsSuccessStatusCode)
            {
                var responseContent = response.Content;
                // by calling .Result you are synchronously reading the result
                string responseString = responseContent.ReadAsStringAsync().Result;
                Logger.Debug("Response of http request is : " + responseString);
            }
            else
            {
                Logger.Debug("Error while sending error notification mail to admin for companyId : " + companyId);
                string Subject = "Error while sending error notification mail to admin";
                string BodyText = "An error occured while sending error notification mail to admin for companyID   : " + companyId + " on " + DateTime.Now + ".";
                BodyText += " \n Response of HTTP request is : " + response.ToString();
                CommonUtility.SendMailToAdmin(Subject, BodyText);
            }


            Logger.Info("method SendErrorMail for companyId : " + companyId + "ended");

        }

        public void SendNotification(long companyId, String errorMessage, long receivedOn, String type)
        {
            Logger.Info("Inside method SendNotification for companyId : " + companyId);

            HttpClient client = new HttpClient();

            var url = EncompassSocialSurveyConfiguration.sendErrorNotificationUrl;
            Logger.Debug("url for http request is :" + url);

            var jsonString = "{\"companyId\":" + companyId + ",\"message\":" + "\""+errorMessage+"\"" +
                ",\"type\":" + "\""+type+"\"" + ",\"receivedOn\":" + receivedOn + "}";
            var httpContent = new StringContent(jsonString, Encoding.UTF8, "application/json");

            
            var response = client.PostAsync(url, httpContent).Result;

            if (response.IsSuccessStatusCode)
            {
                var responseContent = response.Content;
                // by calling .Result you are synchronously reading the result
                string responseString = responseContent.ReadAsStringAsync().Result;
                Logger.Debug("Response of http request is : " + responseString);
            }
            else
            {
                Logger.Debug("Error while sending notification to SSAPI for companyId : " + companyId);
                string Subject = "Error while sending notification to SSAPI";
                string BodyText = "An error has occurred while sending notification to SSAPI for companyID   : " + companyId + " on " + DateTime.Now + ".";
                BodyText += " \n Response of HTTP request is : " + response.ToString();
                CommonUtility.SendMailToAdmin(Subject, BodyText);
            }


            Logger.Info("method SendNotification for companyId : " + companyId + "ended");

        }

        // based on the given json object get the company details
        public List<CompanyCredential> GetCompanyCredentials(string companyRecordType)
        {
            Logger.Info("Inside method GetCompanyCredentials for records type : " + companyRecordType);
            List<CompanyCredential> returnValue = new List<CompanyCredential>();

            HttpClient client = new HttpClient();
            client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
            
            //generate url based on company recoed type
            var url = EncompassSocialSurveyConfiguration.fetchCompanyCredentialsURL;
            url = url +  "?" + EncompassSocialSurveyConstant.fetchCompaniesUrlParameterState + "=";
            if (companyRecordType.Equals(EncompassSocialSurveyConstant.companyRecordTypeSaveData)) 
            {
                url += EncompassSocialSurveyConstant.fetchCompaniesUrlParameterStateProd;
            }
            else if (companyRecordType.Equals(EncompassSocialSurveyConstant.companyRecordTypeGenerateReport))
            {
                url += EncompassSocialSurveyConstant.fetchCompaniesUrlParameterStateDryRun;
            }

            //get the version of encompass being used
            string encompassVersion = EncompassSocialSurveyConfiguration.fetchEncompassVersion;
            url += "&" + EncompassSocialSurveyConstant.fetchCompaniesUrlParameterVersion + "=" + encompassVersion;


            url += "&status=";
            //get companies based on their status
            if (EncompassSocialSurveyConfiguration.fetchCompanyTypeDeterminant.Equals("A") )
            {
                url += "A";
            }
            else if (EncompassSocialSurveyConfiguration.fetchCompanyTypeDeterminant.Equals("I"))
            {
                url += "I";
            }
            else if (EncompassSocialSurveyConfiguration.fetchCompanyTypeDeterminant.Equals("D"))
            {
                url += "D";
            }

            Logger.Debug("url for http request is :" + url);
            var response = client.GetAsync(url).Result;

            if (response.IsSuccessStatusCode)
            {
                var responseContent = response.Content;

                // by calling .Result you are synchronously reading the result
                string responseString = responseContent.ReadAsStringAsync().Result;
                Logger.Debug("Response of http request is : " + responseString);

                DataContractJsonSerializer ser = new DataContractJsonSerializer(typeof(List<CompanyCredential>));
                MemoryStream stream = new MemoryStream(Encoding.UTF8.GetBytes(responseString));
                List<CompanyCredential> routes_list = (List<CompanyCredential>)ser.ReadObject(stream);

                returnValue = routes_list;
            }
            else {
                Logger.Debug("Error while fetching companies credential from http request " );
                string Subject = "Error while fetching companies credential from http request";
                string BodyText = "An error has been occurred while fetching companies credential from http request for recoed type  : " + companyRecordType + " on " + DateTime.Now + ".";
                BodyText += " \n Response of HTTP request is : " + response.ToString();
                CommonUtility.SendMailToAdmin(Subject, BodyText);
            }

            return returnValue;
        }
    }
}
