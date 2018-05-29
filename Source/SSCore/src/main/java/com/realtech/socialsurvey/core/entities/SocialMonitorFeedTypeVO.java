package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;


public class SocialMonitorFeedTypeVO implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private boolean isFacebook;
    private boolean isTwitter;
    private boolean isLinkedin;
    private boolean isInstagram;


    public boolean isFacebook()
    {
        return isFacebook;
    }


    public void setFacebook( boolean isFacebook )
    {
        this.isFacebook = isFacebook;
    }


    public boolean isTwitter()
    {
        return isTwitter;
    }


    public void setTwitter( boolean isTwitter )
    {
        this.isTwitter = isTwitter;
    }


    public boolean isLinkedin()
    {
        return isLinkedin;
    }


    public void setLinkedin( boolean isLinkedin )
    {
        this.isLinkedin = isLinkedin;
    }


    public boolean isInstagram()
    {
        return isInstagram;
    }


    public void setInstagram( boolean isInstagram )
    {
        this.isInstagram = isInstagram;
    }


    @Override
    public String toString()
    {
        return "SocialMonitorFeedTypeVO [isFacebook=" + isFacebook + ", isTwitter=" + isTwitter + ", isLinkedin=" + isLinkedin
            + ", isInstagram=" + isInstagram + "]";
    }


}
