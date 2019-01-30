
using Newtonsoft.Json;
using System.Runtime.Serialization;
namespace EncompassSocialSurvey.Entity
{
    [DataContract]
    public class CompanyCredential
    {
        [DataMember(Name = "companyName")]
        public string CompanyName { get; set; }

        [DataMember(Name = "encompassCrmInfo")]
        public EncompassCredential EncompassCredential { get; set; }

    }

}
