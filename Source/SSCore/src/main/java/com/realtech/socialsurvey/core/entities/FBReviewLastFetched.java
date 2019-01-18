package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;


/**
 * @author Lavanya
 */

public class FBReviewLastFetched implements Serializable
{
    private static final long serialVersionUID = 1L;

    private long previous;
    private long current;


    public FBReviewLastFetched( long previous, long current )
    {
        this.previous = previous;
        this.current = current;
    }


    public long getPrevious()
    {
        return previous;
    }


    public void setPrevious( long previous )
    {
        this.previous = previous;
    }


    public long getCurrent()
    {
        return current;
    }


    public void setCurrent( long current )
    {
        this.current = current;
    }


    @Override public String toString()
    {
        return "FBReviewLastFetched{" + "previous=" + previous + ", current=" + current + '}';
    }
}
