package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the crm_batch_tracker_history database table.
 */
@Entity
@Table ( name = "CRM_BATCH_TRACKER_HISTORY")
public class CrmBatchTrackerHistory implements Serializable {
	
	 private static final long serialVersionUID = 1L;

     @Id
	 @Column ( name = "HISTORY_ID")
	 private long historyId;
     
     @Column(name="CRM_BATCH_TRACKER_ID")
     public long crmBatchTrackerID;
     
     @Column(name="STATUS")
     public int status;
     
     @Column(name="COUNT_OF_RECORDS_FETCHED")
     public int countOfRecordsFetched;
     
     @Column ( name = "CREATED_ON")
     private Timestamp createdOn;

     @Column ( name = "CREATED_BY")
     private String createdBy;
     
     @Column ( name = "MODIFIED_ON")
     private Timestamp modifiedOn;

     @Column ( name = "MODIFIED_By")
     private String modifiedBy;
     
     public CrmBatchTrackerHistory()
     {}
     
     public long getHistoryId() {
		return historyId;
	}

	public void setHistoryId(long historyId) {
		this.historyId = historyId;
	}

	public long getCrmBatchTrackerID() {
		return crmBatchTrackerID;
	}

	public void setCrmBatchTrackerID(long crmBatchTrackerID) {
		this.crmBatchTrackerID = crmBatchTrackerID;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCountOfRecordsFetched() {
		return countOfRecordsFetched;
	}

	public void setCountOfRecordsFetched(int countOfRecordsFetched) {
		this.countOfRecordsFetched = countOfRecordsFetched;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

     
     
     

}
