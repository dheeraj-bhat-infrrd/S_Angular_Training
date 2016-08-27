package com.realtech.socialsurvey.core.entities;

import java.util.Date;


public class SurveyImportVO
{
    private long userId;
    private String userEmailId;
    private String city;
    private String state;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmailAddress;
    private Date surveyDate;
    private double score;


    public long getUserId()
    {
        return userId;
    }


    public void setUserId( long userId )
    {
        this.userId = userId;
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


    public String getCustomerEmailAddress()
    {
        return customerEmailAddress;
    }


    public void setCustomerEmailAddress( String customerEmailAddress )
    {
        this.customerEmailAddress = customerEmailAddress;
    }


    public Date getSurveyDate()
    {
        return surveyDate;
    }


    public void setSurveyDate( Date surveyDate )
    {
        this.surveyDate = surveyDate;
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


    private String review;


    public String getUserEmailId()
    {
        return userEmailId;
    }


    public void setUserEmailId( String userEmailId )
    {
        this.userEmailId = userEmailId;
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
}
