﻿
using System;
namespace EncompassSocialSurvey.Entity
{
    public class LoanEntity
    {
        public string SurveyPreInitiationId { get; set; }
        public string SurveySource { get; set; }
        public string SurveySourceId { get; set; }
        
        //
        public long CompanyId { get; set; }
        public string AgentId { get; set; }
        public string AgentName { get; set; }
        public string AgentEmailId { get; set; }

        public string CustomerFirstName { get; set; }
        public string CustomerLastName { get; set; }
        public string CustomerEmailId { get; set; }
        public string CustomerInteractionDetails { get; set; }
        public string State { get; set; }
        public string City { get; set; }

        //
        public DateTime EngagementClosedTime { get; set; }
        public int ReminderCounts { get; set; }
        public DateTime LastReminderTime { get; set; }
        public int Status { get; set; }
        public int ParticipantType { get; set; }


        //
        public DateTime CreatedOn { get; set; }
        public DateTime ModifiedOn { get; set; }
    }
}
