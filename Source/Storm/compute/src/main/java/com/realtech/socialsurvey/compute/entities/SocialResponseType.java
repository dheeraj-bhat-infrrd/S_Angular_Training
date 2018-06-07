package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;

import com.realtech.socialsurvey.compute.enums.SocialFeedType;

/**
 * @author manish
 *
 */
public class SocialResponseType implements Serializable
{
    private static final long serialVersionUID = 1L;
    private SocialFeedType type;
    
    public SocialFeedType getType()
    {
        return type;
    }
    public void setType( SocialFeedType type )
    {
        this.type = type;
    }
}
