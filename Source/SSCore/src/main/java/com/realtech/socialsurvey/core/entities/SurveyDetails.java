package com.realtech.socialsurvey.core.entities;

import java.util.Date;
import java.util.List;


public class SurveyDetails
{
    private long agentId;
    private String agentName;
    private int reminderCount;
    private int socialPostsReminder;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;
    private long companyId;
    private long regionId;
    private long branchId;
    private int stage;
    private double score;
    private String review;
    private String mood;
    private Date createdOn;
    private Date modifiedOn;
    private List<String> sharedOn;
    private String custRelationWithAgent;
    private String initiatedBy;
    private Date lastReminderForIncompleteSurvey;
    private List<Date> remindersForIncompleteSurveys;
    private Date lastReminderForSocialPost;
    private List<Date> remindersForSocialPosts;
    private String url;
    private List<SurveyResponse> surveyResponse;
    private boolean editable;
    private String completeProfileUrl;
    private String yelpProfileUrl;
    private String zillowProfileUrl;
    private String lendingTreeProfileUrl;
    private String realtorProfileUrl;
    private String source;
    private String sourceId;
    private String agreedToShare;
    private String googleApi;
    private String faceBookShareUrl;
    private boolean isAbusive;
    private String _id;
    private SocialMediaPostDetails socialMediaPostDetails;
    private boolean underResolution;
    private boolean isAbuseRepByUser;
    private SocialMediaPostResponseDetails socialMediaPostResponseDetails;
    private boolean showSurveyOnUI;
    private String summary;

    public SocialMediaPostResponseDetails getSocialMediaPostResponseDetails()
    {
        return socialMediaPostResponseDetails;
    }


    public void setSocialMediaPostResponseDetails( SocialMediaPostResponseDetails socialMediaPostResponseDetails )
    {
        this.socialMediaPostResponseDetails = socialMediaPostResponseDetails;
    }


    public String get_id()
    {
        return _id;
    }


    public void set_id( String _id )
    {
        this._id = _id;
    }


    public String getGoogleApi()
    {
        return googleApi;
    }


    public void setGoogleApi( String googleApi )
    {
        this.googleApi = googleApi;
    }


    public String getFaceBookShareUrl()
    {
        return faceBookShareUrl;
    }


    public void setFaceBookShareUrl( String faceBookShareUrl )
    {
        this.faceBookShareUrl = faceBookShareUrl;
    }


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


    public int getReminderCount()
    {
        return reminderCount;
    }


    public void setReminderCount( int reminderCount )
    {
        this.reminderCount = reminderCount;
    }


    public int getSocialPostsReminder()
    {
        return socialPostsReminder;
    }


    public void setSocialPostsReminder( int socialPostsReminder )
    {
        this.socialPostsReminder = socialPostsReminder;
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


    public String getCustomerEmail()
    {
        return customerEmail;
    }


    public void setCustomerEmail( String customerEmail )
    {
        this.customerEmail = customerEmail;
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


    public String getMood()
    {
        return mood;
    }


    public void setMood( String mood )
    {
        this.mood = mood;
    }


    public Date getModifiedOn()
    {
        return modifiedOn;
    }


    public void setModifiedOn( Date modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    public List<String> getSharedOn()
    {
        return sharedOn;
    }


    public void setSharedOn( List<String> sharedOn )
    {
        this.sharedOn = sharedOn;
    }


    public String getCustRelationWithAgent()
    {
        return custRelationWithAgent;
    }


    public void setCustRelationWithAgent( String custRelationWithAgent )
    {
        this.custRelationWithAgent = custRelationWithAgent;
    }


    public String getInitiatedBy()
    {
        return initiatedBy;
    }


    public void setInitiatedBy( String initiatedBy )
    {
        this.initiatedBy = initiatedBy;
    }


    public List<SurveyResponse> getSurveyResponse()
    {
        return surveyResponse;
    }


    public void setSurveyResponse( List<SurveyResponse> surveyResponse )
    {
        this.surveyResponse = surveyResponse;
    }


    public Date getCreatedOn()
    {
        return createdOn;
    }


    public void setCreatedOn( Date createdOn )
    {
        this.createdOn = createdOn;
    }


    public Date getLastReminderForIncompleteSurvey()
    {
        return lastReminderForIncompleteSurvey;
    }


    public void setLastReminderForIncompleteSurvey( Date lastReminderForIncompleteSurvey )
    {
        this.lastReminderForIncompleteSurvey = lastReminderForIncompleteSurvey;
    }


    public List<Date> getRemindersForIncompleteSurveys()
    {
        return remindersForIncompleteSurveys;
    }


    public void setRemindersForIncompleteSurveys( List<Date> remindersForIncompleteSurveys )
    {
        this.remindersForIncompleteSurveys = remindersForIncompleteSurveys;
    }


    public Date getLastReminderForSocialPost()
    {
        return lastReminderForSocialPost;
    }


    public void setLastReminderForSocialPost( Date lastReminderForSocialPost )
    {
        this.lastReminderForSocialPost = lastReminderForSocialPost;
    }


    public List<Date> getRemindersForSocialPosts()
    {
        return remindersForSocialPosts;
    }


    public void setRemindersForSocialPosts( List<Date> remindersForSocialPosts )
    {
        this.remindersForSocialPosts = remindersForSocialPosts;
    }


    public String getUrl()
    {
        return url;
    }


    public void setUrl( String url )
    {
        this.url = url;
    }


    public boolean getEditable()
    {
        return editable;
    }


    public void setEditable( boolean editable )
    {
        this.editable = editable;
    }


    public String getCompleteProfileUrl()
    {
        return completeProfileUrl;
    }


    public void setCompleteProfileUrl( String completeProfileUrl )
    {
        this.completeProfileUrl = completeProfileUrl;
    }


    public String getYelpProfileUrl()
    {
        return yelpProfileUrl;
    }


    public void setYelpProfileUrl( String yelpProfileUrl )
    {
        this.yelpProfileUrl = yelpProfileUrl;
    }


    public String getZillowProfileUrl()
    {
        return zillowProfileUrl;
    }


    public void setZillowProfileUrl( String zillowProfileUrl )
    {
        this.zillowProfileUrl = zillowProfileUrl;
    }


    public String getLendingTreeProfileUrl()
    {
        return lendingTreeProfileUrl;
    }


    public void setLendingTreeProfileUrl( String lendingTreeProfileUrl )
    {
        this.lendingTreeProfileUrl = lendingTreeProfileUrl;
    }


    public String getSource()
    {
        return source;
    }


    public void setSource( String source )
    {
        this.source = source;
    }


    public String getAgreedToShare()
    {
        return agreedToShare;
    }


    public void setAgreedToShare( String agreedToShare )
    {
        this.agreedToShare = agreedToShare;
    }


    public String getRealtorProfileUrl()
    {
        return realtorProfileUrl;
    }


    public void setRealtorProfileUrl( String realtorProfileUrl )
    {
        this.realtorProfileUrl = realtorProfileUrl;
    }


    public String getSourceId()
    {
        return sourceId;
    }


    public void setSourceId( String sourceId )
    {
        this.sourceId = sourceId;
    }


    public boolean isAbusive()
    {
        return isAbusive;
    }


    public void setAbusive( boolean isAbusive )
    {
        this.isAbusive = isAbusive;
    }


    public SocialMediaPostDetails getSocialMediaPostDetails()
    {
        return socialMediaPostDetails;
    }


    public void setSocialMediaPostDetails( SocialMediaPostDetails socialMediaPostDetails )
    {
        this.socialMediaPostDetails = socialMediaPostDetails;
    }
    public boolean isUnderResolution()
    {
        return underResolution;
    }


    public void setUnderResolution( boolean underResolution )
    {
        this.underResolution = underResolution;
    }


    public boolean isAbuseRepByUser()
    {
        return isAbuseRepByUser;
    }


    public void setAbuseRepByUser( boolean isAbuseRepByUser )
    {
        this.isAbuseRepByUser = isAbuseRepByUser;
    }


    public boolean isShowSurveyOnUI()
    {
        return showSurveyOnUI;
    }


    public void setShowSurveyOnUI( boolean showSurveyOnUI )
    {
        this.showSurveyOnUI = showSurveyOnUI;
    }


    public String getSummary()
    {
        return summary;
    }


    public void setSummary( String summary )
    {
        this.summary = summary;
    }
}