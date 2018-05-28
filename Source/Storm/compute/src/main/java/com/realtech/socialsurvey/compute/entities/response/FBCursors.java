package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;

public class FBCursors implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String before;
    private String after;


    public String getBefore()
    {
        return before;
    }


    public String getAfter()
    {
        return after;
    }


    public void setBefore( String before )
    {
        this.before = before;
    }


    public void setAfter( String after )
    {
        this.after = after;
    }

}