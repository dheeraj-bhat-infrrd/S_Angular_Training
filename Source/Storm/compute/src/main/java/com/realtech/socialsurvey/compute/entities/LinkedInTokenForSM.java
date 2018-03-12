package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;


/**
 * @author manish
 *
 */
public class LinkedInTokenForSM implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String linkedInId;
    private String linkedInPageLink;
    private String linkedInAccessToken;


    public String getLinkedInId()
    {
        return linkedInId;
    }


    public void setLinkedInId( String linkedInId )
    {
        this.linkedInId = linkedInId;
    }


    public String getLinkedInPageLink()
    {
        return linkedInPageLink;
    }


    public void setLinkedInPageLink( String linkedInPageLink )
    {
        this.linkedInPageLink = linkedInPageLink;
    }


    public String getLinkedInAccessToken()
    {
        return linkedInAccessToken;
    }


    public void setLinkedInAccessToken( String linkedInAccessToken )
    {
        this.linkedInAccessToken = linkedInAccessToken;
    }


    @Override
    public String toString()
    {
        return "LinkdenInToken [linkedInId=" + linkedInId + ", linkedInPageLink=" + linkedInPageLink + "]";
    }
}