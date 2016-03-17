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
 * The persistent class for the ZILLOW_TEMP_POST database table.
 */
@Entity
@Table ( name = "ZILLOW_TEMP_POST")
@NamedQuery ( name = "ZillowTempPost.findAll", query = "SELECT s FROM ZillowTempPost s")
public class ZillowTempPost
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

    @Column ( name = "ZILLOW_REVIEW_URL")
    private String zillowReviewUrl;

    @Column ( name = "ZILLOW_REVIEW_SOURCE_LINK")
    private String zillowReviewSourceLink;

    @Column ( name = "ZILLOW_REVIEW_RATING")
    private double zillowReviewRating;

    @Column ( name = "ZILLOW_REVIEWER_NAME")
    private String zillowReviewerName;

    @Column ( name = "ZILLOW_REVIEW_SUMMARY")
    private String zillowReviewSummary;

    @Column ( name = "ZILLOW_REVIEW_DESCRIPTION")
    private String zillowReviewDescription;

    @Column ( name = "ZILLOW_REVIEW_DATE")
    private Timestamp zillowReviewDate;

    @Column ( name = "ZILLOW_SURVEY_ID")
    private String zillowSurveyId;

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
     * @return the zillowReviewUrl
     */
    public String getZillowReviewUrl()
    {
        return zillowReviewUrl;
    }


    /**
     * @param zillowReviewUrl the zillowReviewUrl to set
     */
    public void setZillowReviewUrl( String zillowReviewUrl )
    {
        this.zillowReviewUrl = zillowReviewUrl;
    }


    /**
     * @return the zillowReviewSourceLink
     */
    public String getZillowReviewSourceLink()
    {
        return zillowReviewSourceLink;
    }


    /**
     * @param zillowReviewSourceLink the zillowReviewSourceLink to set
     */
    public void setZillowReviewSourceLink( String zillowReviewSourceLink )
    {
        this.zillowReviewSourceLink = zillowReviewSourceLink;
    }


    /**
     * @return the zillowReviewRating
     */
    public double getZillowReviewRating()
    {
        return zillowReviewRating;
    }


    /**
     * @param zillowReviewRating the zillowReviewRating to set
     */
    public void setZillowReviewRating( double zillowReviewRating )
    {
        this.zillowReviewRating = zillowReviewRating;
    }


    /**
     * @return the zillowReviewerName
     */
    public String getZillowReviewerName()
    {
        return zillowReviewerName;
    }


    /**
     * @param zillowReviewerName the zillowReviewerName to set
     */
    public void setZillowReviewerName( String zillowReviewerName )
    {
        this.zillowReviewerName = zillowReviewerName;
    }


    /**
     * @return the zillowReviewSummary
     */
    public String getZillowReviewSummary()
    {
        return zillowReviewSummary;
    }


    /**
     * @param zillowReviewSummary the zillowReviewSummary to set
     */
    public void setZillowReviewSummary( String zillowReviewSummary )
    {
        this.zillowReviewSummary = zillowReviewSummary;
    }


    /**
     * @return the zillowReviewDescription
     */
    public String getZillowReviewDescription()
    {
        return zillowReviewDescription;
    }


    /**
     * @param zillowReviewDescription the zillowReviewDescription to set
     */
    public void setZillowReviewDescription( String zillowReviewDescription )
    {
        this.zillowReviewDescription = zillowReviewDescription;
    }


    /**
     * @return the zillowReviewDate
     */
    public Timestamp getZillowReviewDate()
    {
        return zillowReviewDate;
    }


    /**
     * @param zillowReviewDate the zillowReviewDate to set
     */
    public void setZillowReviewDate( Timestamp zillowReviewDate )
    {
        this.zillowReviewDate = zillowReviewDate;
    }


    /**
     * @return the zillowSurveyId
     */
    public String getZillowSurveyId()
    {
        return zillowSurveyId;
    }


    /**
     * @param zillowSurveyId the zillowSurveyId to set
     */
    public void setZillowSurveyId( String zillowSurveyId )
    {
        this.zillowSurveyId = zillowSurveyId;
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
