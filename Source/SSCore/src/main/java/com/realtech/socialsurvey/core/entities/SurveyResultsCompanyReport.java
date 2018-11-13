package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "survey_results_company_report")
public class SurveyResultsCompanyReport implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
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
	private Timestamp surveySentDate;

	@Column(name = "SURVEY_COMPLETED_DATE")
	private Timestamp surveyCompletedDate;

	@Column(name = "TIME_INTERVAL")
	private Integer timeInterval;

	@Column(name = "SURVEY_SOURCE")
	private String surveySource;

	@Column(name = "SURVEY_SOURCE_ID")
	private String surveySourceId;

	@Column(name = "SURVEY_SCORE")
	private double surveyScore;

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
	
    @Transient
    private List<SurveyResponseTable> surveyResponseList;
    
    //SS-1505 ADD FEILDS
    @Column(name = "PARTICIPANT_TYPE")
    private String participantType;
    
    @Column(name = "AGENT_EMAILID")
    private String agentEmailId;
    
    @Column(name = "CUSTOMER_EMAIL_ID")
    private String customerEmailId;
    
    //SS-1486
    @Column(name = "STATE")
    private String state;
    
    @Column(name = "CITY")
    private String city;
  

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

	public Timestamp getSurveySentDate() {
		return surveySentDate;
	}

	public void setSurveySentDate(Timestamp surveySentDate) {
		this.surveySentDate = surveySentDate;
	}

	public Timestamp getSurveyCompletedDate() {
		return surveyCompletedDate;
	}

	public void setSurveyCompletedDate(Timestamp surveyCompletedDate) {
		this.surveyCompletedDate = surveyCompletedDate;
	}

	public Integer getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(Integer timeInterval) {
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

	public double getSurveyScore() {
		return surveyScore;
	}

	public void setSurveyScore(double surveyScore) {
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

    public List<SurveyResponseTable> getSurveyResponseList()
    {
        return surveyResponseList;
    }

    public void setSurveyResponseList( List<SurveyResponseTable> surveyResponseList )
    {
        this.surveyResponseList = surveyResponseList;
    }

    public String getParticipantType()
    {
        return participantType;
    }

    public void setParticipantType( String participantType )
    {
        this.participantType = participantType;
    }

    public String getAgentEmailId()
    {
        return agentEmailId;
    }

    public void setAgentEmailId( String agentEmailId )
    {
        this.agentEmailId = agentEmailId;
    }

    public String getCustomerEmailId()
    {
        return customerEmailId;
    }

    public void setCustomerEmailId( String customerEmailId )
    {
        this.customerEmailId = customerEmailId;
    }

    
    public String getState()
    {
        return state;
    }

    public void setState( String state )
    {
        this.state = state;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity( String city )
    {
        this.city = city;
    }

    @Override
    public String toString()
    {
        return "SurveyResultsCompanyReport [surveyDetailsId=" + surveyDetailsId + ", companyId=" + companyId + ", agentId="
            + agentId + ", userFirstName=" + userFirstName + ", userLastName=" + userLastName + ", customerFirstName="
            + customerFirstName + ", customerLastName=" + customerLastName + ", surveySentDate=" + surveySentDate
            + ", surveyCompletedDate=" + surveyCompletedDate + ", timeInterval=" + timeInterval + ", surveySource="
            + surveySource + ", surveySourceId=" + surveySourceId + ", surveyScore=" + surveyScore + ", gateway=" + gateway
            + ", customerComments=" + customerComments + ", agreedToShare=" + agreedToShare + ", branchName=" + branchName
            + ", clickTroughForCompany=" + clickTroughForCompany + ", clickTroughForAgent=" + clickTroughForAgent
            + ", clickTroughForRegion=" + clickTroughForRegion + ", clickTroughForBranch=" + clickTroughForBranch
            + ", surveyResponseList=" + surveyResponseList + ", participantType=" + participantType + ", agentEmailId="
            + agentEmailId + ", customerEmailId=" + customerEmailId + ", state=" + state + ", city=" + city + "]";
    }

	
	
}
