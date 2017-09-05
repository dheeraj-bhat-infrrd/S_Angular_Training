package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "survey_results_company_report")
public class SurveyResultsCompanyReport implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SURVEY_RESULTS_COMPANY_REPORT_ID")
	private String surveyStatsReportId;

	@Column(name = "SURVEY_DETAILS_ID")
	private String surveyDetailsId;
	
	@Column(name = "COMPANY_ID")
	private long companyId;
	
	@Column(name = "AGENT_ID")
	private long agentId;

	@Column(name = "USER_FIRST_NAME")
	private String userFirstName;

	@Column(name = "USER_LAST_NAME")
	private String userLastName;

	@Column(name = "CUSTOMER_FIRST_NAME")
	private String customerFirstName;

	@Column(name = "CUSTOMER_LAST_NAME")
	private String customerLastName;

	@Column(name = "SURVEY_SENT_DATE")
	private Date surveySentDate;

	@Column(name = "SURVEY_COMPLETED_DATE")
	private Date surveyCompletedDate;

	@Column(name = "TIME_INTERVAL")
	private long timeInterval;

	@Column(name = "SURVEY_SOURCE")
	private String surveySource;

	@Column(name = "SURVEY_SOURCE_ID")
	private String surveySourceId;

	@Column(name = "SURVEY_SCORE")
	private long surveyScore;

	@Column(name = "GATEWAY")
	private String gateway;

	@Column(name = "CUSTOMER_COMMENTS")
	private String customerComments;

	@Column(name = "AGREED_TO_SHARE")
	private String agreedToShare;

	@Column(name = "BRANCH_NAME")
	private String branchName;

	@Column(name = "CLICK_THROUGH_FOR_COMPANY")
	private String clickTroughForCompany;

	@Column(name = "CLICK_THROUGH_FOR_AGENT")
	private String clickTroughForAgent;

	@Column(name = "CLICK_THROUGH_FOR_REGION")
	private String clickTroughForRegion;

	@Column(name = "CLICK_THROUGH_FOR_BRANCH")
	private String clickTroughForBranch;
	
	@Column(name = "REPORT_MODIFIED_ON")
	private Timestamp reportModifiedOn;
	

    @Column(name = "IS_DELETED")
    private boolean isDeleted;
  
	public String getSurveyStatsReportId() {
		return surveyStatsReportId;
	}

	public void setSurveyStatsReportId(String surveyStatsReportId) {
		this.surveyStatsReportId = surveyStatsReportId;
	}

	public String getSurveyDetailsId() {
		return surveyDetailsId;
	}

	public void setSurveyDetailsId(String surveyDetailsId) {
		this.surveyDetailsId = surveyDetailsId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public long getAgentId() {
		return agentId;
	}

	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getCustomerFirstName() {
		return customerFirstName;
	}

	public void setCustomerFirstName(String customerFirstName) {
		this.customerFirstName = customerFirstName;
	}

	public String getCustomerLastName() {
		return customerLastName;
	}

	public void setCustomerLastName(String customerLastName) {
		this.customerLastName = customerLastName;
	}

	public Date getSurveySentDate() {
		return surveySentDate;
	}

	public void setSurveySentDate(Date surveySentDate) {
		this.surveySentDate = surveySentDate;
	}

	public Date getSurveyCompletedDate() {
		return surveyCompletedDate;
	}

	public void setSurveyCompletedDate(Date surveyCompletedDate) {
		this.surveyCompletedDate = surveyCompletedDate;
	}

	public long getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(long timeInterval) {
		this.timeInterval = timeInterval;
	}

	public String getSurveySource() {
		return surveySource;
	}

	public void setSurveySource(String surveySource) {
		this.surveySource = surveySource;
	}

	public String getSurveySourceId() {
		return surveySourceId;
	}

	public void setSurveySourceId(String surveySourceId) {
		this.surveySourceId = surveySourceId;
	}

	public long getSurveyScore() {
		return surveyScore;
	}

	public void setSurveyScore(long surveyScore) {
		this.surveyScore = surveyScore;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getCustomerComments() {
		return customerComments;
	}

	public void setCustomerComments(String customerComments) {
		this.customerComments = customerComments;
	}

	public String getAgreedToShare() {
		return agreedToShare;
	}

	public void setAgreedToShare(String agreedToShare) {
		this.agreedToShare = agreedToShare;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getClickTroughForCompany() {
		return clickTroughForCompany;
	}

	public void setClickTroughForCompany(String clickTroughForCompany) {
		this.clickTroughForCompany = clickTroughForCompany;
	}

	public String getClickTroughForAgent() {
		return clickTroughForAgent;
	}

	public void setClickTroughForAgent(String clickTroughForAgent) {
		this.clickTroughForAgent = clickTroughForAgent;
	}

	public String getClickTroughForRegion() {
		return clickTroughForRegion;
	}

	public void setClickTroughForRegion(String clickTroughForRegion) {
		this.clickTroughForRegion = clickTroughForRegion;
	}

	public String getClickTroughForBranch() {
		return clickTroughForBranch;
	}

	public void setClickTroughForBranch(String clickTroughForBranch) {
		this.clickTroughForBranch = clickTroughForBranch;
	}

	public Timestamp getReportModifiedOn() {
		return reportModifiedOn;
	}

	public void setReportModifiedOn(Timestamp reportModifiedOn) {
		this.reportModifiedOn = reportModifiedOn;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

    @Override
    public String toString()
    {
        return "SurveyResultsCompanyReport [surveyStatsReportId=" + surveyStatsReportId + ", surveyDetailsId=" + surveyDetailsId
            + ", companyId=" + companyId + ", agentId=" + agentId + ", userFirstName=" + userFirstName + ", userLastName="
            + userLastName + ", customerFirstName=" + customerFirstName + ", customerLastName=" + customerLastName
            + ", surveySentDate=" + surveySentDate + ", surveyCompletedDate=" + surveyCompletedDate + ", timeInterval="
            + timeInterval + ", surveySource=" + surveySource + ", surveySourceId=" + surveySourceId + ", surveyScore="
            + surveyScore + ", gateway=" + gateway + ", customerComments=" + customerComments + ", agreedToShare="
            + agreedToShare + ", branchName=" + branchName + ", clickTroughForCompany=" + clickTroughForCompany
            + ", clickTroughForAgent=" + clickTroughForAgent + ", clickTroughForRegion=" + clickTroughForRegion
            + ", clickTroughForBranch=" + clickTroughForBranch + ", reportModifiedOn=" + reportModifiedOn + ", isDeleted="
            + isDeleted + "]";
    }

	
	
}
