package com.realtech.socialsurvey.compute.enums;

public enum SocialFeedMediaType
{
    
    IMAGE("photo"), VIDEO("video"), LINK("link"), STATUS("status");
    
    String value;
    
    SocialFeedMediaType(String value) {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
