package com.realtech.socialsurvey.core.entities;

import java.util.Date;
import java.util.List;


public class SurveyDetails
{
    /**
	 * 
	 */

	private String _id;
   
    //agent details
    private long agentId;
    private String agentName;
    private String agentEmailId;

    //entity ids
    private long companyId;
    private long regionId;
    private long branchId;    

    private String regionName;
    private String branchName;
    
    //customer details
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;
    
    //transaction details
    private long surveyPreIntitiationId;
    private String initiatedBy;
    private String custRelationWithAgent;
    private String surveyGeoLocation;
    private String surveyType;
    private String city;
    private String state;
    private String source;
    private String sourceId;
    private Date surveyTransactionDate;
    
    //review details
    private int stage;
    private double score;
    private String review;
    private String mood;
    private String summary;
    private List<SurveyResponse> surveyResponse;
    private String url;
    private String completeProfileUrl;

    //social media details
    private String agreedToShare;
    private List<String> sharedOn;
    private String yelpProfileUrl;
    private String zillowProfileUrl;
    private String lendingTreeProfileUrl;
    private String realtorProfileUrl;
    private String googleBusinessProfileUrl;
    private String googleApi;
    private String faceBookShareUrl;
    private SocialMediaPostDetails socialMediaPostDetails;
    private SocialMediaPostResponseDetails socialMediaPostResponseDetails;

    //reminder details
    private int reminderCount;
    private Date lastReminderForIncompleteSurvey;
    private List<Date> remindersForIncompleteSurveys;
    private int socialPostsReminder;
    private Date lastReminderForSocialPost;
    private List<Date> remindersForSocialPosts;

    //dates
    private Date surveySentDate;
    private Date surveyCompletedDate;
    private Date surveyUpdatedDate;
    private Date createdOn;
    private Date modifiedOn;
    
    //
    private boolean showSurveyOnUI;
    private boolean editable;
    private boolean underResolution;
    
    //abusive details
    private boolean isAbusive;
    private boolean isAbuseRepByUser;
    private Date lastAbuseReportedDate;
    
    //retake details
    private boolean openRetakeSurveyRequest;
    private boolean retakeSurvey;
    private int noOfRetake;
    private Date lastRetakeRequestDate;
    private List<RetakeSurveyHistory> retakeSurveyHistory; 
    
    //NPS details
    private int npsScore = -1;
    
    //abusive email set
    private boolean abusiveNotify;
    
    //adding property address to survey details
    private String propertyAddress;
        
    public String getAgentEmailId()
    {
        return agentEmailId;
    }


    public void setAgentEmailId( String agentEmailId )
    {
        this.agentEmailId = agentEmailId;
    }


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


    public String getRegionName() {
		return regionName;
	}


	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}


	public String getBranchName() {
		return branchName;
	}


	public void setBranchName(String branchName) {
		this.branchName = branchName;
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


    public long getSurveyPreIntitiationId()
    {
        return surveyPreIntitiationId;
    }


    public void setSurveyPreIntitiationId( long surveyPreIntitiationId )
    {
        this.surveyPreIntitiationId = surveyPreIntitiationId;
    }


    public boolean isRetakeSurvey()
    {
        return retakeSurvey;
    }


    public void setRetakeSurvey( boolean retakeSurvey )
    {
        this.retakeSurvey = retakeSurvey;
    }


    public Date getSurveySentDate()
    {
        return surveySentDate;
    }


    public void setSurveySentDate( Date surveySentDate )
    {
        this.surveySentDate = surveySentDate;
    }


    public Date getSurveyCompletedDate()
    {
        return surveyCompletedDate;
    }


    public void setSurveyCompletedDate( Date surveyCompletedDate )
    {
        this.surveyCompletedDate = surveyCompletedDate;
    }


    public Date getSurveyTransactionDate()
    {
        return surveyTransactionDate;
    }


    public void setSurveyTransactionDate( Date surveyTransactionDate )
    {
        this.surveyTransactionDate = surveyTransactionDate;
    }


    public String getSurveyGeoLocation()
    {
        return surveyGeoLocation;
    }


    public void setSurveyGeoLocation( String surveyGeoLocation )
    {
        this.surveyGeoLocation = surveyGeoLocation;
    }


    public String getSurveyType()
    {
        return surveyType;
    }


    public void setSurveyType( String surveyType )
    {
        this.surveyType = surveyType;
    }

    public String getGoogleBusinessProfileUrl()
    {
        return googleBusinessProfileUrl;
    }


    public void setGoogleBusinessProfileUrl( String googleBusinessProfileUrl )
    {
        this.googleBusinessProfileUrl = googleBusinessProfileUrl;
    }


	public String getCity()
	{
		return city;
	}


	public void setCity( String city )
	{
		this.city = city;
	}


	public String getState()
	{
		return state;
	}


	public void setState( String state )
	{
		this.state = state;
	}


    public Date getSurveyUpdatedDate()
    {
        return surveyUpdatedDate;
    }


    public void setSurveyUpdatedDate( Date surveyUpdatedDate )
    {
        this.surveyUpdatedDate = surveyUpdatedDate;
    }


    public Date getLastAbuseReportedDate()
    {
        return lastAbuseReportedDate;
    }


    public void setLastAbuseReportedDate( Date lastAbuseReportedDate )
    {
        this.lastAbuseReportedDate = lastAbuseReportedDate;
    }


    public int getNpsScore()
    {
        return npsScore;
    }


    public void setNpsScore( int npsScore )
    {
        this.npsScore = npsScore;
    }

    public int getNoOfRetake()
    {
        return noOfRetake;
    }


    public void setNoOfRetake( int noOfRetake )
    {
        this.noOfRetake = noOfRetake;
    }


    public Date getLastRetakeRequestDate()
    {
        return lastRetakeRequestDate;
    }


    public void setLastRetakeRequestDate( Date lastRetakeRequestDate )
    {
        this.lastRetakeRequestDate = lastRetakeRequestDate;
    }


    public List<RetakeSurveyHistory> getRetakeSurveyHistory()
    {
        return retakeSurveyHistory;
    }


    	public void setRetakeSurveyHistory( List<RetakeSurveyHistory> retakeSurveyHistory )
    {
        this.retakeSurveyHistory = retakeSurveyHistory;
    }


	public boolean isOpenRetakeSurveyRequest() {
		return openRetakeSurveyRequest;
	}


	public void setOpenRetakeSurveyRequest(boolean openRetakeSurveyRequest) {
		this.openRetakeSurveyRequest = openRetakeSurveyRequest;
	}


	public boolean isAbusiveNotify() {
		return abusiveNotify;
	}


	public void setAbusiveNotify(boolean abusiveNotify) {
		this.abusiveNotify = abusiveNotify;
	}


    public String getPropertyAddress()
    {
        return propertyAddress;
    }


    public void setPropertyAddress( String propertyAddress )
    {
        this.propertyAddress = propertyAddress;
    }

	
		
    
}