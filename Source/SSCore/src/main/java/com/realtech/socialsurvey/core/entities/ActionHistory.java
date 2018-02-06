package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.Date;

import com.realtech.socialsurvey.core.enums.ActionHistoryType;


/**
 * Action history for social post
 * @author manish
 *
 */
public class ActionHistory implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Date createdDate;
    private String ownerName;
    private String text;
    private ActionHistoryType actionType;

    public Date getCreatedDate()
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


    public void setCreatedDate( Date createdDate )
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


    public ActionHistoryType getActionType()
    {
        return actionType;
    }


    public void setActionType( ActionHistoryType actionType )
    {
        this.actionType = actionType;
    }


    @Override
    public String toString()
    {
        return "ActionHistory [createdDate=" + createdDate + ", ownerName=" + ownerName + ", text=" + text + ", actionType="
            + actionType + "]";
    }
}
