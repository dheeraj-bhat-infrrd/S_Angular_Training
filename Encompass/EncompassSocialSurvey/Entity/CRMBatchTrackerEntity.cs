
using System;
namespace EncompassSocialSurvey.Entity
{
    public class CRMBatchTrackerEntity
    {
        public long Id { get; set; }
        public string Source { get; set; }
        public long CompanyId { get; set; }
        public DateTime LastRunStartDate { get; set; }
        public DateTime LastRunEndDate { get; set; }
        public DateTime RecentRecordFetchedDate { get; set; }
        public int LastRunRecordFetchedCount { get; set; }
        public DateTime CreatedOn { get; set; }
        public DateTime ModifiedOn { get; set; }
        public string error { get; set; }
        public string description { get; set; }
    }
}
