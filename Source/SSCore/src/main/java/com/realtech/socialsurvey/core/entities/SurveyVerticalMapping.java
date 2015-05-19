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
 * The persistent class for the SURVEY_VERTICAL_MAPPING database table.
 */
@Entity
@Table(name = "SURVEY_VERTICAL_MAPPING")
@NamedQuery(name = "SurveyVerticalMapping.findAll", query = "SELECT s FROM SurveyVerticalMapping s")
public class SurveyVerticalMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SURVEY_VERTICAL_ID")
	private long surveyVerticalMappingId;

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

	// bi-directional many-to-one association to Survey
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SURVEY_ID")
	private Survey survey;

	// bi-directional many-to-one association to Company
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VERTICAL_ID")
	private VerticalsMaster verticalsMaster;

	public SurveyVerticalMapping() {}

	public long getSurveyVerticalMappingId() {
		return surveyVerticalMappingId;
	}

	public void setSurveyVerticalMappingId(long surveyVerticalMappingId) {
		this.surveyVerticalMappingId = surveyVerticalMappingId;
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

	public Survey getSurvey() {
		return this.survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public VerticalsMaster getVerticalsMaster() {
		return verticalsMaster;
	}

	public void setVerticalsMaster(VerticalsMaster verticalsMaster) {
		this.verticalsMaster = verticalsMaster;
	}
}