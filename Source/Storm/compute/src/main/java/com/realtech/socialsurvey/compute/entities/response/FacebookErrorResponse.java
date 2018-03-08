package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;

/**
 * @author manish
 *
 */
public class FacebookErrorResponse implements Serializable
{
    private static final long serialVersionUID = 1L;
    private FacebookError error;

    public FacebookError getError()
    {
        return error;
    }


    public void setError( FacebookError error )
    {
        this.error = error;
    }

}

