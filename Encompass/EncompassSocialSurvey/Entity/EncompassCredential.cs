using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace EncompassSocialSurvey.Entity
{
    [DataContract]
    public class EncompassCredential
    {
        [DataMember(Name = "companyId")]
        public long CompanyId { get; set; }

        [DataMember(Name = "crm_username")]
        public string UserName { get; set; }

        [DataMember(Name = "crm_password")]
        public string Password { get; set; }

        [DataMember(Name = "url")]
        public string EncompassUrl { get; set; }

        [DataMember(Name = "crm_fieldId")]
        public string fieldId { get; set; }

        [DataMember(Name = "emailAddressForReport")]
        public string emailAddressForReport { get; set; }

        [DataMember(Name = "numberOfDays")]
        public int numberOfDays { get; set; }
    }
}
