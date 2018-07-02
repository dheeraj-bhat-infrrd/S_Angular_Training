package com.realtech.socialsurvey.compute.entities.response;

import com.google.gson.annotations.SerializedName;


/**
 * @author Lavanya
 */

public class FBCommentsSummary
{
    private static final long serialVersionUID = 1L;

    @SerializedName ( "total_count")
    private int totalCount;


    public int getTotalCount()
    {
        return totalCount;
    }


    public void setTotalCount( int totalCount )
    {
        this.totalCount = totalCount;
    }


    @Override public String toString()
    {
        return "FBCommentsSummary{" + "totalCount=" + totalCount + '}';
    }
}
