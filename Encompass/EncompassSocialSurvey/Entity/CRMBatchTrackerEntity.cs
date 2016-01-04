
using System;
namespace EncompassSocialSurvey.Entity
{
    public class CRMBatchTrackerEntity
    {
        public long Id { get; set; }
        public string Source { get; set; }
        public long CompanyId { get; set; }
        public DateTime RecentRecordFetchedStartDate { get; set; }
        public DateTime RecentRecordFetchedEndDate { get; set; }
        public DateTime CreatedOn { get; set; }
        public DateTime ModifiedOn { get; set; }
        public string error { get; set; }
        public string description { get; set; }
    }
}
