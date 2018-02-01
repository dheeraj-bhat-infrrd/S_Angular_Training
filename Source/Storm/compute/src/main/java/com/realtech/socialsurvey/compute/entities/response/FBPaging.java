package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;

public class FBPaging implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String next;
    private String previous;
    private FBCursors cursors;


    public String getNext()
    {
        return next;
    }


    public FBCursors getCursors()
    {
        return cursors;
    }


    public void setNext( String next )
    {
        this.next = next;
    }


    public void setCursors( FBCursors cursors )
    {
        this.cursors = cursors;
    }


    public String getPrevious()
    {
        return previous;
    }


    public void setPrevious( String previous )
    {
        this.previous = previous;
    }
}