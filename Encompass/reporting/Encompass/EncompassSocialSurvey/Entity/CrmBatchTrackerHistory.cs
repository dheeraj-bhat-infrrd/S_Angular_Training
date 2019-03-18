using System;
namespace EncompassSocialSurvey.Entity
{
   public class CrmBatchTrackerHistory:BaseEntity
    {
        public long HistoryId { get; set; }
        public long CrmBatchTrackerID { get; set; }
        public byte Status { get; set; }
        public int CountOfRecordsFetched { get; set; }
        
    }
}
