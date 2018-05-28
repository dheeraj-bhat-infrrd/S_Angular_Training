package com.realtech.socialsurvey.core.vo;

import com.realtech.socialsurvey.core.enums.SocialMediaConnectionStatus;


public class SocialMediaVO
{
    private String socialMedia;
    private SocialMediaConnectionStatus status;


    public SocialMediaVO() { }


    public SocialMediaVO( String socialMedia )
    {
        this.socialMedia = socialMedia;
        this.status = SocialMediaConnectionStatus.NOT_CONNECTED;
    }


    public SocialMediaVO( String socialMedia, SocialMediaConnectionStatus status )
    {
    	this.socialMedia = socialMedia;
        this.status = status;
    }


    public String getSocialMedia()
    {
        return socialMedia;
    }


    public void setSocialMedia( String socialMedia )
    {
    	this.socialMedia = socialMedia;
    }


    public SocialMediaConnectionStatus getStatus()
    {
        return status;
    }


    public void setStatus( SocialMediaConnectionStatus status )
    {
        this.status = status;
    }


    @Override public String toString()
    {
        return "SocialMediaVO{" + "SocialMedia='" + socialMedia + '\'' + ", status='" + status + '\'' + '}';
    }
}
