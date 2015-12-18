package com.realtech.socialsurvey.core.entities;

import java.util.Date;

/**
 * 
 * @author rohit
 *
 */
public class SocialMediaSharedError
{
    private String errorMessage;
    
    private Date errorDate;
    
    private String accessToken;

    
    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage( String errorMessage )
    {
        this.errorMessage = errorMessage;
    }

    public Date getErrorDate()
    {
        return errorDate;
    }

    public void setErrorDate( Date errorDate )
    {
        this.errorDate = errorDate;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken( String accessToken )
    {
        this.accessToken = accessToken;
    }

}
