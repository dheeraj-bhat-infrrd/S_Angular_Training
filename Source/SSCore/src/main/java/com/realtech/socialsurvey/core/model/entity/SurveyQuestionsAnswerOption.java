package com.realtech.socialsurvey.core.model.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the survey_questions_answer_options database table.
 * 
 */
@Entity
@Table(name="survey_questions_answer_options")
@NamedQuery(name="SurveyQuestionsAnswerOption.findAll", query="SELECT s FROM SurveyQuestionsAnswerOption s")
public class SurveyQuestionsAnswerOption implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="SURVEY_QUESTIONS_ANSWER_OPTIONS_ID")
	private int surveyQuestionsAnswerOptionsId;

	private String answer;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_ON")
	private Timestamp createdOn;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_ON")
	private Timestamp modifiedOn;

	private int status;

	//bi-directional many-to-one association to SurveyQuestion
	@ManyToOne
	@JoinColumn(name="SURVEY_QUESTIONS_ID")
	private SurveyQuestion surveyQuestion;

	public SurveyQuestionsAnswerOption() {
	}

	public int getSurveyQuestionsAnswerOptionsId() {
		return this.surveyQuestionsAnswerOptionsId;
	}

	public void setSurveyQuestionsAnswerOptionsId(int surveyQuestionsAnswerOptionsId) {
		this.surveyQuestionsAnswerOptionsId = surveyQuestionsAnswerOptionsId;
	}

	public String getAnswer() {
		return this.answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
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

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public SurveyQuestion getSurveyQuestion() {
		return this.surveyQuestion;
	}

	public void setSurveyQuestion(SurveyQuestion surveyQuestion) {
		this.surveyQuestion = surveyQuestion;
	}

}