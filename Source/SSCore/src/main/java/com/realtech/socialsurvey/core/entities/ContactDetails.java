package com.realtech.socialsurvey.core.entities;

import org.springframework.data.mongodb.core.mapping.Field;


public class ContactDetails
{
    private String name;
    @Field("mail_ids")
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
