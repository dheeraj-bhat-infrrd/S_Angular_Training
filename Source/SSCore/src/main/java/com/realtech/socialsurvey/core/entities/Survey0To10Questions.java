package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * The persistent class for the SURVEY_0TO10_QUESTIONS database table.
 */
@Entity
@Table(name = "SURVEY_0TO10_QUESTIONS")
@NamedQuery(name = "Survey0To10Questions.findAll", query = "SELECT s FROM Survey0To10Questions s")
public class Survey0To10Questions implements Serializable 
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SURVEY_0TO10_ID")
    private long survey0To10Id;
    
    @Column(name = "IS_NPS_QUESTION")
    private int isNPSQuestion;
    
    @Column(name = "CONSIDER_FOR_SCORE")
    private int considerForScore;
    
    @Column(name = "NOT_AT_ALL_LIKELY")
    private String notAtAllLikely;
    
    @Column(name = "VERY_LIKELY")
    private String veryLikely;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column(name = "STATUS")
    private int status;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SURVEY_QUESTIONS_MAPPING_ID")
    private SurveyQuestion surveyQuestion;

    public long getSurvey0To10Id()
    {
        return survey0To10Id;
    }

    public void setSurvey0To10Id( long survey0To10Id )
    {
        this.survey0To10Id = survey0To10Id;
    }

    public int getIsNPSQuestion()
    {
        return isNPSQuestion;
    }

    public void setIsNPSQuestion( int isNPSQuestion )
    {
        this.isNPSQuestion = isNPSQuestion;
    }

    public int getConsiderForScore()
    {
        return considerForScore;
    }

    public void setConsiderForScore( int considerForScore )
    {
        this.considerForScore = considerForScore;
    }

    public String getNotAtAllLikely()
    {
        return notAtAllLikely;
    }

    public void setNotAtAllLikely( String notAtAllLikely )
    {
        this.notAtAllLikely = notAtAllLikely;
    }

    public String getVeryLikely()
    {
        return veryLikely;
    }

    public void setVeryLikely( String veryLikely )
    {
        this.veryLikely = veryLikely;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedOn()
    {
        return createdOn;
    }

    public void setCreatedOn( Timestamp createdOn )
    {
        this.createdOn = createdOn;
    }

    public String getModifiedBy()
    {
        return modifiedBy;
    }

    public void setModifiedBy( String modifiedBy )
    {
        this.modifiedBy = modifiedBy;
    }

    public Timestamp getModifiedOn()
    {
        return modifiedOn;
    }

    public void setModifiedOn( Timestamp modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus( int status )
    {
        this.status = status;
    }

    public SurveyQuestion getSurveyQuestion()
    {
        return surveyQuestion;
    }

    public void setSurveyQuestion( SurveyQuestion surveyQuestion )
    {
        this.surveyQuestion = surveyQuestion;
    }

    @Override
    public String toString()
    {
        return "Survey0To10Questions [survey0To10Id=" + survey0To10Id + ", isNPSQuestion=" + isNPSQuestion
            + ", considerForScore=" + considerForScore + ", notAtAllLikely=" + notAtAllLikely + ", veryLikely=" + veryLikely
            + ", createdBy=" + createdBy + ", createdOn=" + createdOn + ", modifiedBy=" + modifiedBy + ", modifiedOn="
            + modifiedOn + ", status=" + status + ", surveyQuestion=" + surveyQuestion + "]";
    }
    
    
}
