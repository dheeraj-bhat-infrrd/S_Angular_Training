package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;

public class SocialResponseType implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String type;

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }
}
