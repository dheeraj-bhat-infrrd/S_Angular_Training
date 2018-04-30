package com.realtech.socialsurvey.compute.entities;

public class ContactDetails
{
    private String name;
    private MailDetails mailDetails;


    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    public MailDetails getMailDetails()
    {
        return mailDetails;
    }


    public void setMailDetails( MailDetails mailDetails )
    {
        this.mailDetails = mailDetails;
    }


    @Override public String toString()
    {
        return "ContactDetails{" + "name='" + name + '\'' + ", mailDetails=" + mailDetails + '}';
    }
}