package com.realtech.socialsurvey.compute.entities.response;

import com.realtech.socialsurvey.compute.enums.ActionHistoryType;

import java.io.Serializable;


/**
 * Action history for social post
 * @author manish
 *
 */
public class ActionHistory implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String actionHistoryId;
    private long createdDate;
    private String ownerName;
    private String text;
    private ActionHistoryType actionType;
    private boolean isStatusChange;


    public String getOwnerName()
    {
        return ownerName;
    }


    public String getText()
    {
        return text;
    }

    public void setOwnerName( String ownerName )
    {
        this.ownerName = ownerName;
    }


    public void setText( String text )
    {
        this.text = text;
    }


    public ActionHistoryType getActionType()
    {
        return actionType;
    }


    public void setActionType( ActionHistoryType actionType )
    {
        this.actionType = actionType;
    }
    

    public long getCreatedDate()
    {
        return createdDate;
    }


    public void setCreatedDate( long createdDate )
    {
        this.createdDate = createdDate;
    }


    public String getActionHistoryId()
    {
        return actionHistoryId;
    }


    public void setActionHistoryId( String actionHistoryId )
    {
        this.actionHistoryId = actionHistoryId;
    }


    public boolean isStatusChange()
    {
        return isStatusChange;
    }


    public void setStatusChange( boolean isStatusChange )
    {
        this.isStatusChange = isStatusChange;
    }


    @Override
    public String toString()
    {
        return "ActionHistory [actionHistoryId=" + actionHistoryId + ", createdDate=" + createdDate + ", ownerName=" + ownerName
            + ", text=" + text + ", actionType=" + actionType + ", isStatusChange=" + isStatusChange + "]";
    }



}
