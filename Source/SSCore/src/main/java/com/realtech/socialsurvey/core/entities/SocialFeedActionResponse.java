package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.List;


public class SocialFeedActionResponse implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<String> successPostIds;


    public List<String> getSuccessPostIds()
    {
        return successPostIds;
    }


    public void setSuccessPostIds( List<String> successPostIds )
    {
        this.successPostIds = successPostIds;
    }


    @Override
    public String toString()
    {
        return "SocialFeedActionResponse [successPostIds=" + successPostIds + "]";
    }


}
