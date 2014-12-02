package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * The persistent class for the SURVEY database table.
 */
@Entity
@Table(name = "SURVEY")
@NamedQuery(name = "Survey.findAll", query = "SELECT s FROM Survey s")
public class Survey implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SURVEY_ID")
	private long surveyId;

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

	@Column(name = "SURVEY_NAME")
	private String surveyName;

	// bi-directional many-to-one association to Company
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;

	// bi-directional many-to-one association to SurveyQuestionsMapping
	@OneToMany(mappedBy = "survey", fetch = FetchType.LAZY)
	private List<SurveyQuestionsMapping> surveyQuestionsMappings;

	public Survey() {}

	public long getSurveyId() {
		return this.surveyId;
	}

	public void setSurveyId(long surveyId) {
		this.surveyId = surveyId;
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

	public String getSurveyName() {
		return this.surveyName;
	}

	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<SurveyQuestionsMapping> getSurveyQuestionsMappings() {
		return this.surveyQuestionsMappings;
	}

	public void setSurveyQuestionsMappings(List<SurveyQuestionsMapping> surveyQuestionsMappings) {
		this.surveyQuestionsMappings = surveyQuestionsMappings;
	}

	public SurveyQuestionsMapping addSurveyQuestionsMapping(SurveyQuestionsMapping surveyQuestionsMapping) {
		getSurveyQuestionsMappings().add(surveyQuestionsMapping);
		surveyQuestionsMapping.setSurvey(this);

		return surveyQuestionsMapping;
	}

	public SurveyQuestionsMapping removeSurveyQuestionsMapping(SurveyQuestionsMapping surveyQuestionsMapping) {
		getSurveyQuestionsMappings().remove(surveyQuestionsMapping);
		surveyQuestionsMapping.setSurvey(null);

		return surveyQuestionsMapping;
	}

}