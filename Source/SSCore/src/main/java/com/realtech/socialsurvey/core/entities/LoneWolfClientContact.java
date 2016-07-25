package com.realtech.socialsurvey.core.entities;

import java.util.List;


public class LoneWolfClientContact
{
    private String FirstName;
    private String LastName;
    private List<LoneWolfEmailAddress> EmailAddresses;


    public String getFirstName()
    {
        return FirstName;
    }


    public void setFirstName( String firstName )
    {
        FirstName = firstName;
    }


    public String getLastName()
    {
        return LastName;
    }


    public void setLastName( String lastName )
    {
        LastName = lastName;
    }


    public List<LoneWolfEmailAddress> getEmailAddresses()
    {
        return EmailAddresses;
    }


    public void setEmailAddresses( List<LoneWolfEmailAddress> emailAddresses )
    {
        EmailAddresses = emailAddresses;
    }
}
