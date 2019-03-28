package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;


/**
 * @author Lavanya
 */

//Represents the comments given by the ss-admins on the admin dashboard page
public class Notes implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String note;
    private long createdOn;
    private String createdBy;

    public Notes()
    {
    }

    public Notes( String note, long createdOn, String createdBy )
    {
        this.note = note;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
    }


    public String getNote()
    {
        return note;
    }


    public void setNote( String note )
    {
        this.note = note;
    }


    public long getCreatedOn()
    {
        return createdOn;
    }


    public void setCreatedOn( long createdOn )
    {
        this.createdOn = createdOn;
    }


    public String getCreatedBy()
    {
        return createdBy;
    }


    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }


    @Override public String toString()
    {
        return "Notes{" + "note='" + note + '\'' + ", createdOn=" + createdOn + ", createdBy='" + createdBy + '\'' + '}';
    }
}
