/**
 * 
 */
package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author Subhrajit
 *
 */
public class SurveyResultsReportVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String surveyDetailsId;
	private String userFirstName;
	private String userLastName;
	private String customerFirstName;
	private String customerLastName;
	private Timestamp surveySentDate;
	private Timestamp surveyCompletedDate;
	private Integer timeInterval;
	private String surveySource;
	private String surveySourceId;
	private double surveyScore;
	private String gateway;
	private String customerComments;
	private String agreedToShare;
	private String branchName;
	private String clickTroughForCompany;
	private String clickTroughForAgent;
	private String clickTroughForRegion;
	private String clickTroughForBranch;
    private List<SurveyResponseTable> surveyResponseList;
    //SS-1505 ADD FEILDS
    private String participantType;
    private String agentEmailId;
    private String customerEmailId;
  //SS-1486
    private String state;
    private String city;
    
	public String getSurveyDetailsId() {
		return surveyDetailsId;
	}
	public void setSurveyDetailsId(String surveyDetailsId) {
		this.surveyDetailsId = surveyDetailsId;
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
	public List<SurveyResponseTable> getSurveyResponseList() {
		return surveyResponseList;
	}
	public void setSurveyResponseList(List<SurveyResponseTable> surveyResponseList) {
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
        return "SurveyResultsReportVO [surveyDetailsId=" + surveyDetailsId + ", userFirstName=" + userFirstName
            + ", userLastName=" + userLastName + ", customerFirstName=" + customerFirstName + ", customerLastName="
            + customerLastName + ", surveySentDate=" + surveySentDate + ", surveyCompletedDate=" + surveyCompletedDate
            + ", timeInterval=" + timeInterval + ", surveySource=" + surveySource + ", surveySourceId=" + surveySourceId
            + ", surveyScore=" + surveyScore + ", gateway=" + gateway + ", customerComments=" + customerComments
            + ", agreedToShare=" + agreedToShare + ", branchName=" + branchName + ", clickTroughForCompany="
            + clickTroughForCompany + ", clickTroughForAgent=" + clickTroughForAgent + ", clickTroughForRegion="
            + clickTroughForRegion + ", clickTroughForBranch=" + clickTroughForBranch + ", surveyResponseList=" + surveyResponseList + ", participantType=" + participantType
            + ", agentEmailId=" + agentEmailId + ", customerEmailId=" + customerEmailId + ", state=" + state + ", city=" + city
            + "]";
    }
	

}
