/**
 * 
 */
package com.realtech.socialsurvey.core.vo;

/**
 * @author Subhrajit
 *
 */
public class SurveyDetailsVO
{
    
    private long agentId;
    private long companyId;
    private long regionId;
    private long branchId;
    private String agentName;
    private String completeProfileUrl;
    private String customerFirstName;
    private String customerLastName;
    private String source;
    private String sourceId;
    private String review;
    private String summary;
    private double score;
    private long surveyTransactionDate;
    private int stage;
    private String agreedToShare;
    private long surveySentDate;
    private long surveyCompletedDate;
    private long createdOn;
    private long modifiedOn;
    private long surveyUpdatedDate;
    private boolean showSurveyOnUI;
    private String regionName;
    private String branchName;
    private String profileType;
    private String fbRecommendationType;

    public long getAgentId()
    {
        return agentId;
    }
    public void setAgentId( long agentId )
    {
        this.agentId = agentId;
    }
    public String getAgentName()
    {
        return agentName;
    }
    public void setAgentName( String agentName )
    {
        this.agentName = agentName;
    }
    public long getCompanyId()
    {
        return companyId;
    }
    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }
    public long getRegionId()
    {
        return regionId;
    }
    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }
    public long getBranchId()
    {
        return branchId;
    }
    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
    }
    public String getCustomerFirstName()
    {
        return customerFirstName;
    }
    public void setCustomerFirstName( String customerFirstName )
    {
        this.customerFirstName = customerFirstName;
    }
    public String getCustomerLastName()
    {
        return customerLastName;
    }
    public void setCustomerLastName( String customerLastName )
    {
        this.customerLastName = customerLastName;
    }
    public String getSource()
    {
        return source;
    }
    public void setSource( String source )
    {
        this.source = source;
    }
    public String getSourceId()
    {
        return sourceId;
    }
    public void setSourceId( String sourceId )
    {
        this.sourceId = sourceId;
    }
    public long getSurveyTransactionDate()
    {
        return surveyTransactionDate;
    }
    public void setSurveyTransactionDate( long surveyTransactionDate )
    {
        this.surveyTransactionDate = surveyTransactionDate;
    }
    public int getStage()
    {
        return stage;
    }
    public void setStage( int stage )
    {
        this.stage = stage;
    }
    public double getScore()
    {
        return score;
    }
    public void setScore( double score )
    {
        this.score = score;
    }
    public String getReview()
    {
        return review;
    }
    public void setReview( String review )
    {
        this.review = review;
    }
    public String getSummary()
    {
        return summary;
    }
    public void setSummary( String summary )
    {
        this.summary = summary;
    }
    public String getCompleteProfileUrl()
    {
        return completeProfileUrl;
    }
    public void setCompleteProfileUrl( String completeProfileUrl )
    {
        this.completeProfileUrl = completeProfileUrl;
    }
    public String getAgreedToShare()
    {
        return agreedToShare;
    }
    public void setAgreedToShare( String agreedToShare )
    {
        this.agreedToShare = agreedToShare;
    }
    public long getSurveySentDate()
    {
        return surveySentDate;
    }
    public void setSurveySentDate( long surveySentDate )
    {
        this.surveySentDate = surveySentDate;
    }
    public long getSurveyCompletedDate()
    {
        return surveyCompletedDate;
    }
    public void setSurveyCompletedDate( long surveyCompletedDate )
    {
        this.surveyCompletedDate = surveyCompletedDate;
    }
    public long getCreatedOn()
    {
        return createdOn;
    }
    public void setCreatedOn( long createdOn )
    {
        this.createdOn = createdOn;
    }
    public long getModifiedOn()
    {
        return modifiedOn;
    }
    public void setModifiedOn( long modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }
    public boolean getShowSurveyOnUI()
    {
        return showSurveyOnUI;
    }
    public void setShowSurveyOnUI( boolean showSurveyOnUI )
    {
        this.showSurveyOnUI = showSurveyOnUI;
    }
    public long getSurveyUpdatedDate()
    {
        return surveyUpdatedDate;
    }
    public void setSurveyUpdatedDate( long surveyUpdatedDate )
    {
        this.surveyUpdatedDate = surveyUpdatedDate;
    }


    public String getProfileType()
    {
        return profileType;
    }


    public void setProfileType( String profileType )
    {
        this.profileType = profileType;
    }


    public String getRegionName()
    {
        return regionName;
    }


    public void setRegionName( String regionName )
    {
        this.regionName = regionName;
    }


    public String getBranchName()
    {
        return branchName;
    }


    public void setBranchName( String branchName )
    {
        this.branchName = branchName;
    }


    public String getFbRecommendationType()
    {
        return fbRecommendationType;
    }


    public void setFbRecommendationType( String fbRecommendationType )
    {
        this.fbRecommendationType = fbRecommendationType;
    }
}