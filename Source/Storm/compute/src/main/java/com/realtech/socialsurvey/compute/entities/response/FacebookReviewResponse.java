package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;
import java.util.List;


/**
 * @author Lavanya
 */

public class FacebookReviewResponse implements Serializable
{
    private static final long serialVersionUID = 1L;

    private List<FacebookReviewData> data;
    private FBPaging paging;


    public List<FacebookReviewData> getData()
    {
        return data;
    }


    public void setData( List<FacebookReviewData> data )
    {
        this.data = data;
    }


    public FBPaging getPaging()
    {
        return paging;
    }


    public void setPaging( FBPaging paging )
    {
        this.paging = paging;
    }
}
