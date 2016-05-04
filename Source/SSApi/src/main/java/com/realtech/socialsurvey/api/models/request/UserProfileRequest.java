package com.realtech.socialsurvey.api.models.request;

import com.realtech.socialsurvey.api.models.Phone;


/**
 * @author Shipra Goyal, RareMile
 *
 */
public class UserProfileRequest
{
    private int userId;
    private String firstName;
    private String lastName;
    private String title;
    private String profilePhotoUrl;
    private Phone phone1;
    private Phone phone2;
    private String website;
    private String location;


    public Phone getPhone1()
    {
        return phone1;
    }


    public void setPhone1( Phone phone1 )
    {
        this.phone1 = phone1;
    }


    public Phone getPhone2()
    {
        return phone2;
    }


    public void setPhone2( Phone phone2 )
    {
        this.phone2 = phone2;
    }


    public String getWebsite()
    {
        return website;
    }


    public void setWebsite( String website )
    {
        this.website = website;
    }


    public String getLocation()
    {
        return location;
    }


    public void setLocation( String location )
    {
        this.location = location;
    }


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
