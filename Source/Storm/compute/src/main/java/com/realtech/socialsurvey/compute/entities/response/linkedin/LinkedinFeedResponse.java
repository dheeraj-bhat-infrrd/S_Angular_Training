
package com.realtech.socialsurvey.compute.entities.response.linkedin;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;


/**
 * @author manish
 * 
 */
public class LinkedinFeedResponse implements Serializable
{

    private static final long serialVersionUID = 1L;

    @SerializedName ( "_count")
    private int count;

    @SerializedName ( "_start")
    private int start;

    @SerializedName ( "_total")
    private int total;

    private List<LinkedinFeedData> values;


    public int getCount()
    {
        return count;
    }


    public void setCount( int count )
    {
        this.count = count;
    }


    public int getStart()
    {
        return start;
    }


    public void setStart( int start )
    {
        this.start = start;
    }


    public int getTotal()
    {
        return total;
    }


    public void setTotal( int total )
    {
        this.total = total;
    }


    public List<LinkedinFeedData> getValues()
    {
        return values;
    }


    public void setValues( List<LinkedinFeedData> values )
    {
        this.values = values;
    }


    @Override
    public String toString()
    {
        return "LinkedinFeedResponse [count=" + count + ", start=" + start + ", total=" + total + ", values=" + values + "]";
    }
}
