package com.realtech.socialsurvey.core.entities;

import java.util.List;


public class LoneWolfMember
{
    private String Id;
    private String FirstName;
    private String LastName;
    private List<LoneWolfEmailAddress> EmailAddresses;


    public String getId()
    {
        return Id;
    }


    public void setId( String id )
    {
        Id = id;
    }


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
