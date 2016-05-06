package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the verticals_master database table.
 * 
 */
@Entity
@Table ( name = "VERTICALS_MASTER")
@NamedQuery ( name = "VerticalsMaster.findAll", query = "SELECT v FROM VerticalsMaster v")
public class VerticalsMaster implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @Column ( name = "VERTICALS_MASTER_ID")
    private int verticalsMasterId;

    @Column ( name = "CREATED_BY")
    private String createdBy;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "MODIFIED_BY")
    private String modifiedBy;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    private int status;

    @Column ( name = "VERTICAL_NAME")
    private String verticalName;

    @Column ( name = "PRIORITY_ORDER")
    private int priorityOrder;

    //bi-directional many-to-one association to VerticalCrmMapping
    @OneToMany ( mappedBy = "verticalsMaster")
    private List<VerticalCrmMapping> verticalCrmMappings;

    //bi-directional many-to-one association to Survey
    @OneToMany ( mappedBy = "verticalsMaster")
    private List<SurveyVerticalMapping> surveys;

    //bi-directional many-to-one association to Survey
    @OneToMany ( mappedBy = "verticalsMaster")
    private List<Company> companies;


    public VerticalsMaster()
    {}


    public int getPriorityOrder()
    {
        return priorityOrder;
    }


    public void setPriorityOrder( int priorityOrder )
    {
        this.priorityOrder = priorityOrder;
    }


    public int getVerticalsMasterId()
    {
        return this.verticalsMasterId;
    }


    public void setVerticalsMasterId( int verticalsMasterId )
    {
        this.verticalsMasterId = verticalsMasterId;
    }


    public String getCreatedBy()
    {
        return this.createdBy;
    }


    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }


    public Timestamp getCreatedOn()
    {
        return this.createdOn;
    }


    public void setCreatedOn( Timestamp createdOn )
    {
        this.createdOn = createdOn;
    }


    public String getModifiedBy()
    {
        return this.modifiedBy;
    }


    public void setModifiedBy( String modifiedBy )
    {
        this.modifiedBy = modifiedBy;
    }


    public Timestamp getModifiedOn()
    {
        return this.modifiedOn;
    }


    public void setModifiedOn( Timestamp modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    public int getStatus()
    {
        return this.status;
    }


    public void setStatus( int status )
    {
        this.status = status;
    }


    public String getVerticalName()
    {
        return this.verticalName;
    }


    public void setVerticalName( String verticalName )
    {
        this.verticalName = verticalName;
    }


    public List<VerticalCrmMapping> getVerticalCrmMappings()
    {
        return this.verticalCrmMappings;
    }


    public void setVerticalCrmMappings( List<VerticalCrmMapping> verticalCrmMappings )
    {
        this.verticalCrmMappings = verticalCrmMappings;
    }


    public VerticalCrmMapping addVerticalCrmMapping( VerticalCrmMapping verticalCrmMapping )
    {
        getVerticalCrmMappings().add( verticalCrmMapping );
        verticalCrmMapping.setVerticalsMaster( this );

        return verticalCrmMapping;
    }


    public VerticalCrmMapping removeVerticalCrmMapping( VerticalCrmMapping verticalCrmMapping )
    {
        getVerticalCrmMappings().remove( verticalCrmMapping );
        verticalCrmMapping.setVerticalsMaster( null );

        return verticalCrmMapping;
    }


    public List<Company> getCompanies()
    {
        return companies;
    }


    public void setCompanies( List<Company> companies )
    {
        this.companies = companies;
    }


    public List<SurveyVerticalMapping> getSurveys()
    {
        return surveys;
    }


    public void setSurveys( List<SurveyVerticalMapping> surveys )
    {
        this.surveys = surveys;
    }
}