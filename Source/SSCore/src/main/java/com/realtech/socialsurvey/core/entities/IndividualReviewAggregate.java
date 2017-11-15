package com.realtech.socialsurvey.core.entities;

public class IndividualReviewAggregate
{
    private boolean surveyIdValid;
    private String invalidMessage;
    private SurveyDetails review;
    private String reviewJson;
    private AgentSettings agentSettings;


    public boolean getSurveyIdValid()
    {
        return surveyIdValid;
    }


    public void setSurveyIdValid( boolean isSurveyIdValid )
    {
        this.surveyIdValid = isSurveyIdValid;
    }


    public String getInvalidMessage()
    {
        return invalidMessage;
    }


    public void setInvalidMessage( String invalidMessage )
    {
        this.invalidMessage = invalidMessage;
    }


    public SurveyDetails getReview()
    {
        return review;
    }


    public void setReview( SurveyDetails review )
    {
        this.review = review;
    }


    public String getReviewJson()
    {
        return reviewJson;
    }


    public void setReviewJson( String reviewJson )
    {
        this.reviewJson = reviewJson;
    }


    public AgentSettings getAgentSettings()
    {
        return agentSettings;
    }


    public void setAgentSettings( AgentSettings agentSettings )
    {
        this.agentSettings = agentSettings;
    }


    @Override
    public String toString()
    {
        return "IndividualReviewAggregate [surveyIdValid=" + surveyIdValid + ", invalidMessage=" + invalidMessage + ", review="
            + review + ", reviewJson=" + reviewJson + ", agentSettings=" + agentSettings + "]";
    }
}
