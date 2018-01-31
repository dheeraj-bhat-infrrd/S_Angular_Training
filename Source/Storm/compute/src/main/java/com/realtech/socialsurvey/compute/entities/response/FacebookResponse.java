package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;
import java.util.List;


/**
 * @author manish
 *
 */
public class FacebookResponse implements Serializable
{
    private static final long serialVersionUID = 1L;
    private List<FacebookFeedData> data;
    private FBPaging paging;


    public List<FacebookFeedData> getData()
    {
        return data;
    }


    public FBPaging getPaging()
    {
        return paging;
    }


    public void setData( List<FacebookFeedData> data )
    {
        this.data = data;
    }


    public void setPaging( FBPaging paging )
    {
        this.paging = paging;
    }


}

