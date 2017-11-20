package com.realtech.socialsurvey.core.entities;

public class IndividualReviewAggregate
{
    private boolean surveyIdValid;
    private String invalidMessage;
    private SurveyDetails review;
    private OrganizationUnitSettings unitSettings;


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


    public OrganizationUnitSettings getUnitSettings()
    {
        return unitSettings;
    }


    public void setUnitSettings( OrganizationUnitSettings unitSettings )
    {
        this.unitSettings = unitSettings;
    }


    @Override
    public String toString()
    {
        return "IndividualReviewAggregate [surveyIdValid=" + surveyIdValid + ", invalidMessage=" + invalidMessage + ", review="
            + review + ", unitSettings=" + unitSettings + "]";
    }
}
