package com.realtech.socialsurvey.core.entities;

public class IndividualReviewAggregate
{
    private boolean surveyIdValid;
    private String invalidMessage;
    private SurveyDetails review;
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
        return "IndividualReviewAggregate [isSurveyIdValid=" + surveyIdValid + ", invalidMessage=" + invalidMessage
            + ", review=" + review + ", agentSettings=" + agentSettings + "]";
    }
}
