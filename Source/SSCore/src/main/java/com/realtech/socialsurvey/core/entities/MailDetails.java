package com.realtech.socialsurvey.core.entities;

import org.springframework.data.mongodb.core.mapping.Field;


public class MailDetails
{
    @Field("work")
    private String emailId;


    public String getEmailId()
    {
        return emailId;
    }


    public void setEmailId( String emailId )
    {
        this.emailId = emailId;
    }


    @Override public String toString()
    {
        return "MailDetails{" + "emailId='" + emailId + '\'' + '}';
    }
}
