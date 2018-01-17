package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;


/**
 * @author manish
 *
 */
public class Keyword implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String id;
    private String phrase;
    private long createdOn;
    private long modifiedOn;
    private String status;


    public String getId()
    {
        return id;
    }


    public void setId( String id )
    {
        this.id = id;
    }


    public String getPhrase()
    {
        return phrase;
    }


    public void setPhrase( String phrase )
    {
        this.phrase = phrase;
    }


    public String getStatus()
    {
        return status;
    }


    public void setStatus( String status )
    {
        this.status = status;
    }


    public long getCreatedOn()
    {
        return createdOn;
    }


    public long getModifiedOn()
    {
        return modifiedOn;
    }


    public void setCreatedOn( long createdOn )
    {
        this.createdOn = createdOn;
    }


    public void setModifiedOn( long modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    @Override
    public String toString()
    {
        return "Keyword [id=" + id + ", phrase=" + phrase + ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn
            + ", status=" + status + "]";
    }

}