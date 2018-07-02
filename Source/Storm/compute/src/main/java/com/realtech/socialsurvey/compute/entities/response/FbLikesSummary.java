package com.realtech.socialsurvey.compute.entities.response;

import com.google.gson.annotations.SerializedName;


/**
 * @author Lavanya
 */

public class FbLikesSummary
{
    private static final long serialVersionUID = 1L;

    @SerializedName ( "total_count")
    private int totalCount;

    @SerializedName ( "can_like")
    private boolean canLike;

    @SerializedName ( "has_liked")
    private boolean hasLiked;

    public int getTotalCount() { return this.totalCount; }

    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public boolean getCanLike() { return this.canLike; }

    public void setCanLike(boolean canLike) { this.canLike = canLike; }

    public boolean getHasLiked() { return this.hasLiked; }

    public void setHasLiked( boolean hasLiked )
    {
        this.hasLiked = hasLiked;
    }


    @Override public String toString()
    {
        return "FbLikesSummary{" + "totalCount=" + totalCount + ", canLike=" + canLike + ", hasLiked=" + hasLiked + '}';
    }
}
