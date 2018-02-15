package com.realtech.socialsurvey.compute.entities.response;

import com.realtech.socialsurvey.compute.enums.ActionHistoryType;

import java.io.Serializable;
import java.util.Date;


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
    

    public Date getCreatedDate()
    {
        return createdDate;
    }


    public void setCreatedDate( Date createdDate )
    {
        this.createdDate = createdDate;
    }
    
    @Override
    public String toString()
    {
        return "ActionHistory [createdDate=" + createdDate + ", ownerName=" + ownerName + ", text=" + text + ", actionType="
            + actionType + "]";
    }
}
