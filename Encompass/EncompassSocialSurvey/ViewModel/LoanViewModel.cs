
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
        public string fundedTime { get; set; }
        public int ReminderCounts { get; set; }
        public string LastReminderTime { get; set; }
        public int Status { get; set; }
        public string CreatedOn { get; set; }
        public string ModifiedOn { get; set; }
    }
}
