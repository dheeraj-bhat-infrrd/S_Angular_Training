package com.realtech.socialsurvey.core.entities;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the EXTERNAL_SURVEY_TRACKER database table.
 */
@Entity
@Table ( name = "EXTERNAL_SURVEY_TRACKER")
@NamedQuery ( name = "ExternalSurveyTracker.findAll", query = "SELECT s FROM ExternalSurveyTracker s")
public class ExternalSurveyTracker
{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "ID")
    private long id;

    @Column ( name = "ENTITY_COLUMN_NAME")
    private String entityColumnName;

    @Column ( name = "ENTITY_ID")
    private long entityId;

    @Column ( name = "REVIEW_SOURCE")
    private String reviewSource;

    @Column ( name = "REVIEW_SOURCE_LINK")
    private String reviewSourceLink;

    @Column ( name = "REVIEW_SOURCE_URL")
    private String reviewSourceUrl;

    @Column ( name = "REVIEW_RATING")
    private double reviewRating;

    @Column ( name = "COMPLAINT_RES_STATUS")
    private int complaintResolutionStatus;

    @Column ( name = "REVIEW_DATE")
    private Timestamp reviewDate;

    @Column ( name = "CREATED_BY")
    private String createdBy;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "MODIFIED_BY")
    private String modifiedBy;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;


    /**
     * @return the id
     */
    public long getId()
    {
        return id;
    }


    /**
     * @param id the id to set
     */
    public void setId( long id )
    {
        this.id = id;
    }


    /**
     * @return the entityColumnName
     */
    public String getEntityColumnName()
    {
        return entityColumnName;
    }


    /**
     * @param entityColumnName the entityColumnName to set
     */
    public void setEntityColumnName( String entityColumnName )
    {
        this.entityColumnName = entityColumnName;
    }


    /**
     * @return the entityId
     */
    public long getEntityId()
    {
        return entityId;
    }


    /**
     * @param entityId the entityId to set
     */
    public void setEntityId( long entityId )
    {
        this.entityId = entityId;
    }


    /**
     * @return the reviewSource
     */
    public String getReviewSource()
    {
        return reviewSource;
    }


    /**
     * @param reviewSource the reviewSource to set
     */
    public void setReviewSource( String reviewSource )
    {
        this.reviewSource = reviewSource;
    }


    /**
     * @return the reviewSourceLink
     */
    public String getReviewSourceLink()
    {
        return reviewSourceLink;
    }


    /**
     * @param reviewSourceLink the reviewSourceLink to set
     */
    public void setReviewSourceLink( String reviewSourceLink )
    {
        this.reviewSourceLink = reviewSourceLink;
    }


    /**
     * @return the reviewSourceUrl
     */
    public String getReviewSourceUrl()
    {
        return reviewSourceUrl;
    }


    /**
     * @param reviewSourceUrl the reviewSourceUrl to set
     */
    public void setReviewSourceUrl( String reviewSourceUrl )
    {
        this.reviewSourceUrl = reviewSourceUrl;
    }


    /**
     * @return the reviewRating
     */
    public double getReviewRating()
    {
        return reviewRating;
    }


    /**
     * @param reviewRating the reviewRating to set
     */
    public void setReviewRating( double reviewRating )
    {
        this.reviewRating = reviewRating;
    }


    /**
     * @return the complaintResolutionStatus
     */
    public int getComplaintResolutionStatus()
    {
        return complaintResolutionStatus;
    }


    /**
     * @param complaintResolutionStatus the complaintResolutionStatus to set
     */
    public void setComplaintResolutionStatus( int complaintResolutionStatus )
    {
        this.complaintResolutionStatus = complaintResolutionStatus;
    }


    /**
     * @return the reviewDate
     */
    public Timestamp getReviewDate()
    {
        return reviewDate;
    }


    /**
     * @param reviewDate the reviewDate to set
     */
    public void setReviewDate( Timestamp reviewDate )
    {
        this.reviewDate = reviewDate;
    }


    /**
     * @return the createdBy
     */
    public String getCreatedBy()
    {
        return createdBy;
    }


    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }


    /**
     * @return the createdOn
     */
    public Timestamp getCreatedOn()
    {
        return createdOn;
    }


    /**
     * @param createdOn the createdOn to set
     */
    public void setCreatedOn( Timestamp createdOn )
    {
        this.createdOn = createdOn;
    }


    /**
     * @return the modifiedBy
     */
    public String getModifiedBy()
    {
        return modifiedBy;
    }


    /**
     * @param modifiedBy the modifiedBy to set
     */
    public void setModifiedBy( String modifiedBy )
    {
        this.modifiedBy = modifiedBy;
    }


    /**
     * @return the modifiedOn
     */
    public Timestamp getModifiedOn()
    {
        return modifiedOn;
    }


    /**
     * @param modifiedOn the modifiedOn to set
     */
    public void setModifiedOn( Timestamp modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    /**
     * @return the serialversionuid
     */
    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }

}
