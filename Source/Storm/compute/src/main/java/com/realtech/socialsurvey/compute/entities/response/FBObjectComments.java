package com.realtech.socialsurvey.compute.entities.response;

/**
 * @author Lavanya
 */

public class FBObjectComments
{
    private static final long serialVersionUID = 1L;

    private FBCommentsSummary summary;


    public FBCommentsSummary getSummary()
    {
        return summary;
    }


    public void setSummary( FBCommentsSummary summary )
    {
        this.summary = summary;
    }


    @Override public String toString()
    {
        return "FBObjectComments{" + "summary=" + summary + '}';
    }
}
