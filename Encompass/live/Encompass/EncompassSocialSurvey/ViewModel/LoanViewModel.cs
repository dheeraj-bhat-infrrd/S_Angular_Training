
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
        public string EngagementClosedTime { get; set; }
        public int ReminderCounts { get; set; }
        public string LastReminderTime { get; set; }
        public int Status { get; set; }
        public string CreatedOn { get; set; }
        public string ModifiedOn { get; set; }
        public string LoanNumber { get; set; }
        public string City { get; set; }
        public string State { get; set; }

        public int ParticipantType { get; set; }

        public string PropertyAddress { get; set; }
        public string LoanProcessorName { get; set; }
        public string LoanProcessorEmail { get; set; }

        public string customFieldOne { get; set; }
        public string customFieldTwo { get; set; }
        public string customFieldThree { get; set; }
        public string customFieldFour { get; set; }
        public string customFieldFive { get; set; }
    }
}
