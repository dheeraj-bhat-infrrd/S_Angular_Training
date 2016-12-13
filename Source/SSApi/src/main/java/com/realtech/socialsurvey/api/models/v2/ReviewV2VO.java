package com.realtech.socialsurvey.api.models.v2;

import java.util.List;

import org.springframework.stereotype.Component;


@Component
public class ReviewV2VO
{


    private String source;
    private String reviewCompletedDateTime;
    private String reviewUpdatedDateTime;
    private String rating;
    private String summary;
    private String description;
    private boolean agreedToShare;
    private boolean isReportedAbusive;
    private boolean verifiedCustomer;
    private boolean retakeSurvey;
    private List<SurveyResponseV2VO> surveyResponses;


    public String getSource()
    {
        return source;
    }


    public void setSource( String source )
    {
        this.source = source;
    }


    public String getReviewCompletedDateTime()
    {
        return reviewCompletedDateTime;
    }


    public void setReviewCompletedDateTime( String reviewCompletedDateTime )
    {
        this.reviewCompletedDateTime = reviewCompletedDateTime;
    }


    public String getReviewUpdatedDateTime()
    {
        return reviewUpdatedDateTime;
    }


    public void setReviewUpdatedDateTime( String reviewUpdatedDateTime )
    {
        this.reviewUpdatedDateTime = reviewUpdatedDateTime;
    }


    public String getRating()
    {
        return rating;
    }


    public void setRating( String rating )
    {
        this.rating = rating;
    }


    public String getSummary()
    {
        return summary;
    }


    public void setSummary( String summary )
    {
        this.summary = summary;
    }


    public String getDescription()
    {
        return description;
    }


    public void setDescription( String description )
    {
        this.description = description;
    }


    public boolean isAgreedToShare()
    {
        return agreedToShare;
    }


    public void setAgreedToShare( boolean agreedToShare )
    {
        this.agreedToShare = agreedToShare;
    }


    public boolean isReportedAbusive()
    {
        return isReportedAbusive;
    }


    public void setReportedAbusive( boolean isReportedAbusive )
    {
        this.isReportedAbusive = isReportedAbusive;
    }


    public boolean isVerifiedCustomer()
    {
        return verifiedCustomer;
    }


    public void setVerifiedCustomer( boolean verifiedCustomer )
    {
        this.verifiedCustomer = verifiedCustomer;
    }


    public boolean isRetakeSurvey()
    {
        return retakeSurvey;
    }


    public void setRetakeSurvey( boolean retakeSurvey )
    {
        this.retakeSurvey = retakeSurvey;
    }


    public List<SurveyResponseV2VO> getSurveyResponses()
    {
        return surveyResponses;
    }


    public void setSurveyResponses( List<SurveyResponseV2VO> surveyResponses )
    {
        this.surveyResponses = surveyResponses;
    }
}
