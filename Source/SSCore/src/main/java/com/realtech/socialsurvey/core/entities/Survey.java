package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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

	@Column(name = "IS_SURVEY_BUILDING_COMPLETE")
	private int isSurveyBuildingComplete;

	@Column(name = "SURVEY_NAME")
	private String surveyName;

	// bi-directional many-to-one association to SurveyCompanyMapping
	@OneToMany(mappedBy = "survey", fetch = FetchType.LAZY)
	private List<SurveyVerticalMapping> surveyVerticalMappings;

	// bi-directional many-to-one association to SurveyCompanyMapping
	@OneToMany(mappedBy = "survey", fetch = FetchType.LAZY)
	private List<SurveyCompanyMapping> surveyCompanyMappings;

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

	public int getIsSurveyBuildingComplete() {
		return isSurveyBuildingComplete;
	}

	public void setIsSurveyBuildingComplete(int isSurveyBuildingComplete) {
		this.isSurveyBuildingComplete = isSurveyBuildingComplete;
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

	public List<SurveyCompanyMapping> getSurveyCompanyMappings() {
		return this.surveyCompanyMappings;
	}

	public void setSurveyCompanyMappings(List<SurveyCompanyMapping> surveyCompanyMappings) {
		this.surveyCompanyMappings = surveyCompanyMappings;
	}

	public SurveyCompanyMapping addSurveyCompanyMapping(SurveyCompanyMapping surveyCompanyMapping) {
		getSurveyCompanyMappings().add(surveyCompanyMapping);
		surveyCompanyMapping.setSurvey(this);

		return surveyCompanyMapping;
	}

	public SurveyCompanyMapping removeSurveyCompanyMapping(SurveyCompanyMapping surveyCompanyMapping) {
		getSurveyCompanyMappings().remove(surveyCompanyMapping);
		surveyCompanyMapping.setSurvey(null);

		return surveyCompanyMapping;
	}

	public List<SurveyVerticalMapping> getSurveyVerticalMappings() {
		return surveyVerticalMappings;
	}

	public void setSurveyVerticalMappings(List<SurveyVerticalMapping> surveyVerticalMappings) {
		this.surveyVerticalMappings = surveyVerticalMappings;
	}

	public SurveyVerticalMapping addSurveyVerticalMapping(SurveyVerticalMapping surveyVerticalMapping) {
		getSurveyVerticalMappings().add(surveyVerticalMapping);
		surveyVerticalMapping.setSurvey(this);

		return surveyVerticalMapping;
	}

	public SurveyVerticalMapping removeSurveyVerticalMapping(SurveyVerticalMapping surveyVerticalMapping) {
		getSurveyVerticalMappings().remove(surveyVerticalMapping);
		surveyVerticalMapping.setSurvey(null);

		return surveyVerticalMapping;
	}
}