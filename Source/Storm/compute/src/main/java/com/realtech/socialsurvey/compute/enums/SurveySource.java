package com.realtech.socialsurvey.compute.enums;

/**
 * @author Lavanya
 */

/* Represents the source of the review */
public enum SurveySource
{
    FACEBOOK("facebook") , GOOGLE("google");

    private String value;


    SurveySource( String value )
    {
        this.value = value;
    }


    public String getValue()
    {
        return value;
    }
}
