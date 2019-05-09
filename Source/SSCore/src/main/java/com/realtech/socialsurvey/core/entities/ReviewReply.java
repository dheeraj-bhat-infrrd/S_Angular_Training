package com.realtech.socialsurvey.core.entities;

import java.util.Date;


public class ReviewReply
{
    //unique uuid of the reply
    private String replyId;

    //the actual reply text
    private String replyText;

    //name of the user giving the reply
    private String replyByName;

    //user id of the user giving the reply
    private String replyById;

    private int profileMasterId;

    //standard created_on, modified_on fields
    private Date createdOn;
    private Date modifiedOn;


    public String getReplyId()
    {
        return replyId;
    }


    public void setReplyId( String replyId )
    {
        this.replyId = replyId;
    }


    public String getReplyText()
    {
        return replyText;
    }


    public void setReplyText( String replyText )
    {
        this.replyText = replyText;
    }


    public String getReplyByName()
    {
        return replyByName;
    }


    public void setReplyByName( String replyByName )
    {
        this.replyByName = replyByName;
    }


    public String getReplyById()
    {
        return replyById;
    }


    public void setReplyById( String replyById )
    {
        this.replyById = replyById;
    }


    public int getProfileMasterId()
    {
        return profileMasterId;
    }


    public void setProfileMasterId( int profileMasterId )
    {
        this.profileMasterId = profileMasterId;
    }


    public Date getCreatedOn()
    {
        return createdOn;
    }


    public void setCreatedOn( Date createdOn )
    {
        this.createdOn = createdOn;
    }


    public Date getModifiedOn()
    {
        return modifiedOn;
    }


    public void setModifiedOn( Date modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    @Override
    public String toString()
    {
        return "ReviewReply [replyId=" + replyId + ", replyText=" + replyText + ", replyByName=" + replyByName + ", replyById="
            + replyById + ", profileMasterId=" + profileMasterId + ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn
            + "]";
    }


}
