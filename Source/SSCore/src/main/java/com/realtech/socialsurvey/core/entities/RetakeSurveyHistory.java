package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class RetakeSurveyHistory
{
    
    private double score;
    private String review;
    private String mood;
    private String summary;
    private List<SurveyResponse> surveyResponse;
    private String url;
    private String completeProfileUrl;
    private SocialMediaPostDetails socialMediaPostDetails;
    
    
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
    public String getSummary()
    {
        return summary;
    }
    public void setSummary( String summary )
    {
        this.summary = summary;
    }
    public List<SurveyResponse> getSurveyResponse()
    {
        return surveyResponse;
    }
    public void setSurveyResponse( List<SurveyResponse> surveyResponse )
    {
        this.surveyResponse = surveyResponse;
    }
    public String getUrl()
    {
        return url;
    }
    public void setUrl( String url )
    {
        this.url = url;
    }
    public String getCompleteProfileUrl()
    {
        return completeProfileUrl;
    }
    public void setCompleteProfileUrl( String completeProfileUrl )
    {
        this.completeProfileUrl = completeProfileUrl;
    }
    public SocialMediaPostDetails getSocialMediaPostDetails()
    {
        return socialMediaPostDetails;
    }
    public void setSocialMediaPostDetails( SocialMediaPostDetails socialMediaPostDetails )
    {
        this.socialMediaPostDetails = socialMediaPostDetails;
    }


}
