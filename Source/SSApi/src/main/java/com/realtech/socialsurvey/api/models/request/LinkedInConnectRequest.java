package com.realtech.socialsurvey.api.models.request;

/**
 * @author Shipra Goyal, RareMile
 *
 */
public class LinkedInConnectRequest
{
    private String firstName;
    private String lastName;
    private String title;
    private String profilePhotoUrl;
    private int userId;


    public String getFirstName()
    {
        return firstName;
    }


    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }


    public String getLastName()
    {
        return lastName;
    }


    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }


    public String getTitle()
    {
        return title;
    }


    public void setTitle( String title )
    {
        this.title = title;
    }


    public String getProfilePhotoUrl()
    {
        return profilePhotoUrl;
    }


    public void setProfilePhotoUrl( String profilePhotoUrl )
    {
        this.profilePhotoUrl = profilePhotoUrl;
    }


    public int getUserId()
    {
        return userId;
    }


    public void setUserId( int userId )
    {
        this.userId = userId;
    }
}
