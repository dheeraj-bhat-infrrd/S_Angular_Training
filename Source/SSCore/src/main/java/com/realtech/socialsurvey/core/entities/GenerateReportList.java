package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "generate_report_list")
public class GenerateReportList implements Serializable
{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "generate_report_list_id")
    private long generateReportListId;
    
    @Column ( name = "current_date")
    private Timestamp currentDate;
    
    @Column ( name = "report_name")
    private String reportName;

    @Column ( name = "start_date")
    private Timestamp startDate;
    
    @Column ( name = "end_date")
    private Timestamp endDate;
    
    @Column ( name = "first_name")
    private String firstName;
    
    @Column ( name = "last_name")
    private String lastName;
    
    @Column ( name = "entity_id")
    private long entityId;
    
    @Column ( name = "entity_type")
    private String entityType;
    
    @Column ( name = "status")
    private long status;
    
    
    public Long getGenerateReportListId()
    {
        return generateReportListId;
    }


    public void setGenerateReportListId( Long generateReportListId )
    {
        this.generateReportListId = generateReportListId;
    }


    public Timestamp getCurrentDate()
    {
        return currentDate;
    }


    public void setCurrentDate( Timestamp currentDate )
    {
        this.currentDate = currentDate;
    }


    public String getReportName()
    {
        return reportName;
    }


    public void setReportName( String reportName )
    {
        this.reportName = reportName;
    }


    public Timestamp getStartDate()
    {
        return startDate;
    }


    public void setStartDate( Timestamp startDate )
    {
        this.startDate = startDate;
    }


    public Timestamp getEndDate()
    {
        return endDate;
    }


    public void setEndDate( Timestamp endDate )
    {
        this.endDate = endDate;
    }


    public String getFirstName()
    {
        return firstName;
    }


    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }


    public String getLastName()
    {
        return lastName;
    }


    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }


    public Long getEntityId()
    {
        return entityId;
    }


    public void setEntityId( Long entityId )
    {
        this.entityId = entityId;
    }


    public String getEntityType()
    {
        return entityType;
    }


    public void setEntityType( String entityType )
    {
        this.entityType = entityType;
    }


    public long getStatus()
    {
        return status;
    }


    public void setStatus( long status )
    {
        this.status = status;
    }


    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }


    @Override
    public String toString() {
        return "GenerateReportList [generateReportListId=" +generateReportListId +"currentDate=" + currentDate + ", reportName=" + reportName + ", startDate=" + startDate
                + ", endDate=" + endDate + ", firstName=" + firstName + ", lastName=" + lastName 
                + ", entityId=" + entityId + ", entityType="  + entityType + ", status="+status + "]";
    }
}
