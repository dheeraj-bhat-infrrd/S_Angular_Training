package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;

/**
 * @author manish
 *
 */
public class SocialFeedMediaEntity implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String type;
    
    private String url;
    
    private String thumbnailUrl;

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }
    
    

    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }

    public void setThumbnailUrl( String thumbnailUrl )
    {
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public String toString()
    {
        return "SocialFeedMediaEntity [type=" + type + ", url=" + url + ", thumbnailUrl=" + thumbnailUrl + "]";
    }
}
