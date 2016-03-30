package com.realtech.socialsurvey.core.entities;

import java.util.Date;

/**
 * 
 * @author rohit
 *
 */
public class SocialMediaPostResponse
{
    private String responseMessage;
    
    private Date postDate;

    private String accessToken;

    private String referenceUrl;
  

    public String getResponseMessage()
    {
        return responseMessage;
    }

    public void setResponseMessage( String responseMessage )
    {
        this.responseMessage = responseMessage;
    }
    
    public Date getPostDate()
    {
        return postDate;
    }

    public void setPostDate( Date postDate )
    {
        this.postDate = postDate;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken( String accessToken )
    {
        this.accessToken = accessToken;
    }


    public String getReferenceUrl()
    {
        return referenceUrl;
    }


    public void setReferenceUrl( String referenceUrl )
    {
        this.referenceUrl = referenceUrl;
    }

}
