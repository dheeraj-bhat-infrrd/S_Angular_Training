package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 *  The persistent class for the job_log_details database table.
 *
 */
@Entity
@Table ( name = "job_log_details")
public class JobLogDetails implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "JOB_LOG_ID")
    private long jobLogId;

    @Column ( name = "JOB_NAME")
    private String jobName;

    @Column ( name = "STATUS")
    private String status;

    @Column ( name = "JOB_START_TIME")
    private Timestamp jobStartTime;

    @Column ( name = "JOB_END_TIME")
    private Timestamp jobEndTime;

    @Column ( name = "CURRENT_JOB_NAME")
    private String currentJobName;

    @Column ( name = "JOB_UUID")
    private String jobUuid;
    
    @Column ( name = "ENTITY_ID")
    private Long entityId;
    
    @Column ( name = "ENTITY_TYPE")
    private String entityType;


    public long getJobLogId()
    {
        return jobLogId;
    }


    public void setJobLogId( long jobLogId )
    {
        this.jobLogId = jobLogId;
    }


    public String getJobName()
    {
        return jobName;
    }


    public void setJobName( String jobName )
    {
        this.jobName = jobName;
    }


    public String getStatus()
    {
        return status;
    }


    public void setStatus( String status )
    {
        this.status = status;
    }


   

    public Timestamp getJobStartTime()
    {
        return jobStartTime;
    }


    public void setJobStartTime( Timestamp jobStartTime )
    {
        this.jobStartTime = jobStartTime;
    }


    public Timestamp getJobEndTime()
    {
        return jobEndTime;
    }


    public void setJobEndTime( Timestamp jobEndTime )
    {
        this.jobEndTime = jobEndTime;
    }


    public String getCurrentJobName()
    {
        return currentJobName;
    }


    public void setCurrentJobName( String currentJobName )
    {
        this.currentJobName = currentJobName;
    }


    public String getJobUuid()
    {
        return jobUuid;
    }


    public void setJobUuid( String jobUuid )
    {
        this.jobUuid = jobUuid;
    }
    

    public long getEntityId() {
		return entityId;
	}


	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}


	public String getEntityType() {
		return entityType;
	}


	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}


	@Override
	public String toString() {
		return "JobLogDetails [jobLogId=" + jobLogId + ", jobName=" + jobName + ", status=" + status + ", jobStartTime="
				+ jobStartTime + ", jobEndTime=" + jobEndTime + ", currentJobName=" + currentJobName + ", jobUuid="
				+ jobUuid + ", entityId=" + entityId + ", entityType=" + entityType + "]";
	}


}
