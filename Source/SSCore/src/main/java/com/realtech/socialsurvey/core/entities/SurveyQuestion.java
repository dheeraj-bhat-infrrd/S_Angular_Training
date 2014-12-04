package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * The persistent class for the SURVEY_QUESTIONS database table.
 */
@Entity
@Table(name = "SURVEY_QUESTIONS")
@NamedQuery(name = "SurveyQuestion.findAll", query = "SELECT s FROM SurveyQuestion s")
public class SurveyQuestion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SURVEY_QUESTIONS_ID")
	private int surveyQuestionsId;

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

	@Column(name = "SURVEY_QUESTION")
	private String surveyQuestion;

	@Column(name = "SURVEY_QUESTIONS_CODE")
	private String surveyQuestionsCode;

	// bi-directional many-to-one association to SurveyQuestionsAnswerOption
	@OneToMany(mappedBy = "surveyQuestion", fetch = FetchType.LAZY)
	private List<SurveyQuestionsAnswerOption> surveyQuestionsAnswerOptions;

	// bi-directional many-to-one association to SurveyQuestionsMapping
	@OneToMany(mappedBy = "surveyQuestion", fetch = FetchType.LAZY)
	private List<SurveyQuestionsMapping> surveyQuestionsMappings;

	public SurveyQuestion() {}

	public int getSurveyQuestionsId() {
		return this.surveyQuestionsId;
	}

	public void setSurveyQuestionsId(int surveyQuestionsId) {
		this.surveyQuestionsId = surveyQuestionsId;
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

	public String getSurveyQuestion() {
		return this.surveyQuestion;
	}

	public void setSurveyQuestion(String surveyQuestion) {
		this.surveyQuestion = surveyQuestion;
	}

	public String getSurveyQuestionsCode() {
		return this.surveyQuestionsCode;
	}

	public void setSurveyQuestionsCode(String surveyQuestionsCode) {
		this.surveyQuestionsCode = surveyQuestionsCode;
	}

	public List<SurveyQuestionsAnswerOption> getSurveyQuestionsAnswerOptions() {
		return this.surveyQuestionsAnswerOptions;
	}

	public void setSurveyQuestionsAnswerOptions(List<SurveyQuestionsAnswerOption> surveyQuestionsAnswerOptions) {
		this.surveyQuestionsAnswerOptions = surveyQuestionsAnswerOptions;
	}

	public SurveyQuestionsAnswerOption addSurveyQuestionsAnswerOption(SurveyQuestionsAnswerOption surveyQuestionsAnswerOption) {
		getSurveyQuestionsAnswerOptions().add(surveyQuestionsAnswerOption);
		surveyQuestionsAnswerOption.setSurveyQuestion(this);

		return surveyQuestionsAnswerOption;
	}

	public SurveyQuestionsAnswerOption removeSurveyQuestionsAnswerOption(SurveyQuestionsAnswerOption surveyQuestionsAnswerOption) {
		getSurveyQuestionsAnswerOptions().remove(surveyQuestionsAnswerOption);
		surveyQuestionsAnswerOption.setSurveyQuestion(null);

		return surveyQuestionsAnswerOption;
	}

	public List<SurveyQuestionsMapping> getSurveyQuestionsMappings() {
		return this.surveyQuestionsMappings;
	}

	public void setSurveyQuestionsMappings(List<SurveyQuestionsMapping> surveyQuestionsMappings) {
		this.surveyQuestionsMappings = surveyQuestionsMappings;
	}

	public SurveyQuestionsMapping addSurveyQuestionsMapping(SurveyQuestionsMapping surveyQuestionsMapping) {
		getSurveyQuestionsMappings().add(surveyQuestionsMapping);
		surveyQuestionsMapping.setSurveyQuestion(this);

		return surveyQuestionsMapping;
	}

	public SurveyQuestionsMapping removeSurveyQuestionsMapping(SurveyQuestionsMapping surveyQuestionsMapping) {
		getSurveyQuestionsMappings().remove(surveyQuestionsMapping);
		surveyQuestionsMapping.setSurveyQuestion(null);

		return surveyQuestionsMapping;
	}

}