package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;


/**
 * @author Lavanya
 */

//Class to store various social media last fetched times

public class SocialMediaLastFetched implements Serializable
{
    private static final long serialVersionUID = 1L;

    private FBReviewLastFetched fbReviewLastFetched;
    private GoogleReviewLastFetched googleReviewLastFetched;


    public FBReviewLastFetched getFbReviewLastFetched()
    {
        return fbReviewLastFetched;
    }


    public void setFbReviewLastFetched( FBReviewLastFetched fbReviewLastFetched )
    {
        this.fbReviewLastFetched = fbReviewLastFetched;
    }
    


    public GoogleReviewLastFetched getGoogleReviewLastFetched()
    {
        return googleReviewLastFetched;
    }


    public void setGoogleReviewLastFetched( GoogleReviewLastFetched googleReviewLastFetched )
    {
        this.googleReviewLastFetched = googleReviewLastFetched;
    }


    @Override
    public String toString()
    {
        return "SocialMediaLastFetched [fbReviewLastFetched=" + fbReviewLastFetched + ", googleReviewLastFetched="
            + googleReviewLastFetched + "]";
    }

}
