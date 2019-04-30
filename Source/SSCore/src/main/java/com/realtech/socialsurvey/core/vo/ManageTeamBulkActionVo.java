package com.realtech.socialsurvey.core.vo;

import java.io.Serializable;


public class ManageTeamBulkActionVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private long userId;
    private String userEmailId;
    private  String profileName;
    private String publicPageUrl;
    private String message;


    public long getUserId()
    {
        return userId;
    }


    public void setUserId( long userId )
    {
        this.userId = userId;
    }


    public String getProfileName()
    {
        return profileName;
    }


    public void setProfileName( String profileName )
    {
        this.profileName = profileName;
    }


    public String getPublicPageUrl()
    {
        return publicPageUrl;
    }


    public void setPublicPageUrl( String publicPageUrl )
    {
        this.publicPageUrl = publicPageUrl;
    }


    public String getMessage()
    {
        return message;
    }


    public void setMessage( String message )
    {
        this.message = message;
    }


    public String getUserEmailId()
    {
        return userEmailId;
    }


    public void setUserEmailId( String userEmailId )
    {
        this.userEmailId = userEmailId;
    }
}
