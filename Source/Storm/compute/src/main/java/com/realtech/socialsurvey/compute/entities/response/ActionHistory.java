package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Action history for social post
 * @author manish
 *
 */
public class ActionHistory implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Timestamp createdDate;
    private String ownerName;
    private String text;
    private int actionType;
    public Timestamp getCreatedDate()
    {
        return createdDate;
    }
    public String getOwnerName()
    {
        return ownerName;
    }
    public String getText()
    {
        return text;
    }
    public int getActionType()
    {
        return actionType;
    }
    public void setCreatedDate( Timestamp createdDate )
    {
        this.createdDate = createdDate;
    }
    public void setOwnerName( String ownerName )
    {
        this.ownerName = ownerName;
    }
    public void setText( String text )
    {
        this.text = text;
    }
    public void setActionType( int actionType )
    {
        this.actionType = actionType;
    }
}
