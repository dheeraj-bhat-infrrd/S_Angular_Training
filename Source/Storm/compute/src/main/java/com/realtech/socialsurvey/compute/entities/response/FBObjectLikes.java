package com.realtech.socialsurvey.compute.entities.response;

/**
 * @author Lavanya
 */

public class FBObjectLikes
{
    private static final long serialVersionUID = 1L;

    private FbLikesSummary summary;

    public FbLikesSummary getSummary() { return this.summary; }

    public void setSummary(FbLikesSummary summary) { this.summary = summary; }


    @Override public String toString()
    {
        return "FBObjectLikes{" + "summary=" + summary + '}';
    }
}
