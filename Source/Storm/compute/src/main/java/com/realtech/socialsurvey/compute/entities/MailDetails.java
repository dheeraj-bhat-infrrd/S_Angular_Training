package com.realtech.socialsurvey.compute.entities;

public class MailDetails
{
    private String emailId;


    public String getEmailId()
    {
        return emailId;
    }


    public void setEmailId( String emailId )
    {
        this.emailId = emailId;
    }


    @Override
    public String toString()
    {
        return "MailDetails{" + "emailId='" + emailId + '\'' + '}';
    }
}
