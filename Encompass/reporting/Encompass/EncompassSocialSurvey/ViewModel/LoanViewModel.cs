
using System;
namespace EncompassSocialSurvey.ViewModel
{
    public class LoanViewModel
    {
        public string SurveyPreInitiationId { get; set; }
        public string SurveySource { get; set; }
        public string SurveySourceId { get; set; }
        public long CompanyId { get; set; }
        public string AgentId { get; set; }
        public string AgentName { get; set; }
        public string AgentEmailId { get; set; }
        public string CustomerFirstName { get; set; }
        public string CustomerLastName { get; set; }
        public string CustomerEmailId { get; set; }
        public string CustomerInteractionDetails { get; set; }
        public DateTime EngagementClosedTime { get; set; }
        public int ReminderCounts { get; set; }
        public DateTime LastReminderTime { get; set; }
        public int Status { get; set; }
        public DateTime CreatedOn { get; set; }
        public DateTime ModifiedOn { get; set; }
        public string LoanNumber { get; set; }
        public string City { get; set; }
        public string State { get; set; }

        public int ParticipantType { get; set; }

        public string PropertyAddress { get; set; }
        public string LoanProcessorName { get; set; }
        public string LoanProcessorEmail { get; set; }

        public string CustomFieldOne { get; set; }
        public string CustomFieldTwo { get; set; }
        public string CustomFieldThree { get; set; }
        public string CustomFieldFour { get; set; }
        public string CustomFieldFive { get; set; }
       
    }
}
