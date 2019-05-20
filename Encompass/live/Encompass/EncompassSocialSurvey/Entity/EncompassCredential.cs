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


        [DataMember(Name = "allowPartnerSurvey")]
        public bool allowPartnerSurvey { get; set; }


        [DataMember(Name = "buyerAgentEmail")]
        public string buyerAgentEmail { get; set; }


        [DataMember(Name = "buyerAgentName")]
        public string buyerAgentName { get; set; }


        [DataMember(Name = "sellerAgentEmail")]
        public string sellerAgentEmail { get; set; }


        [DataMember(Name = "sellerAgentName")]
        public string sellerAgentName { get; set; }


        [DataMember(Name = "propertyAddress")]
        public string propertyAddress { get; set; }


        [DataMember(Name = "loanProcessorName")]
        public string loanProcessorName { get; set; }


        [DataMember(Name = "loanProcessorEmail")]
        public string loanProcessorEmail { get; set; }

        [DataMember(Name = "customFieldOne")]
        public string customFieldOne { get; set; }

        [DataMember(Name = "customFieldTwo")]
        public string customFieldTwo { get; set; }

        [DataMember(Name = "customFieldThree")]
        public string customFieldThree { get; set; }

        [DataMember(Name = "customFieldFour")]
        public string customFieldFour { get; set; }

        [DataMember(Name = "customFieldFive")]
        public string customFieldFive { get; set; }

        [DataMember(Name = "contactNumber")]
        public string ContactNumber { get; set; }

    } 
}