package com.realtech.socialsurvey.core.entities;

public class FacebookPixelToken
{
    
    private String pixelId;
    private String pixelImgTag;

    
    public String getPixelId()
    {
        return pixelId;
    }
    public void setPixelId( String pixelId )
    {
        this.pixelId = pixelId;
    }
    public String getPixelImgTag()
    {
        return pixelImgTag;
    }
    public void setPixelImgTag( String pixelImgTag )
    {
        this.pixelImgTag = pixelImgTag;
    }


    @Override public String toString()
    {
        return "FacebookPixelToken{" + "pixelId='" + pixelId + '\'' + ", pixelImgTag='" + pixelImgTag + '\'' + '}';
    }
}
