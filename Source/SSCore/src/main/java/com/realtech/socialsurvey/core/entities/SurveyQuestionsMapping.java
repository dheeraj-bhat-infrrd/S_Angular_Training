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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the SURVEY_QUESTIONS_MAPPING database table.
 * 
 */
@Entity
@Table(name="SURVEY_QUESTIONS_MAPPING")
@NamedQuery(name="SurveyQuestionsMapping.findAll", query="SELECT s FROM SurveyQuestionsMapping s")
public class SurveyQuestionsMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="SURVEY_QUESTIONS_MAPPING_ID")
	private long surveyQuestionsMappingId;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_ON")
	private Timestamp createdOn;

	@Column(name="IS_RATING_QUESTION")
	private int isRatingQuestion;
	
	@Column(name="IS_USER_RANKING_QUESTION")
    private int isUserRankingQuestion;
	
	@Column(name="IS_NPS_QUESTION")
    private int isNPSQuestion;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_ON")
	private Timestamp modifiedOn;

	@Column(name="QUESTION_ORDER")
	private int questionOrder;

	@Column(name="STATUS")
	private int status;

	//bi-directional many-to-one association to Survey
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="SURVEY_ID")
	private Survey survey;

	//bi-directional many-to-one association to SurveyQuestion
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="SURVEY_QUESTIONS_ID")
	private SurveyQuestion surveyQuestion;

	public SurveyQuestionsMapping() {
	}

	public long getSurveyQuestionsMappingId() {
		return this.surveyQuestionsMappingId;
	}

	public void setSurveyQuestionsMappingId(long surveyQuestionsMappingId) {
		this.surveyQuestionsMappingId = surveyQuestionsMappingId;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public int getIsRatingQuestion() {
		return this.isRatingQuestion;
	}

	public void setIsRatingQuestion(int isRatingQuestion) {
		this.isRatingQuestion = isRatingQuestion;
	}

	public int getIsUserRankingQuestion()
    {
        return isUserRankingQuestion;
    }

    public void setIsUserRankingQuestion( int isUserRankingQuestion )
    {
        this.isUserRankingQuestion = isUserRankingQuestion;
    }
    
    public int getIsNPSQuestion()
    {
        return isNPSQuestion;
    }

    public void setIsNPSQuestion( int isNPSQuestion )
    {
        this.isNPSQuestion = isNPSQuestion;
    }

    public String getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getModifiedOn() {
		return this.modifiedOn;
	}

	public void setModifiedOn(Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public int getQuestionOrder() {
		return this.questionOrder;
	}

	public void setQuestionOrder(int questionOrder) {
		this.questionOrder = questionOrder;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Survey getSurvey() {
		return this.survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public SurveyQuestion getSurveyQuestion() {
		return this.surveyQuestion;
	}

	public void setSurveyQuestion(SurveyQuestion surveyQuestion) {
		this.surveyQuestion = surveyQuestion;
	}

}