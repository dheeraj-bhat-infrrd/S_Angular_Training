
using System;
namespace EncompassSocialSurvey.Entity
{
    public class CRMBatchTrackerEntity
    {
        public long Id { get; set; }
        public string Source { get; set; }
        public long CompanyId { get; set; }
        public DateTime RecentRecordFetchedDate { get; set; }
        public DateTime CreatedOn { get; set; }
        public DateTime ModifiedOn { get; set; }
    }
}
