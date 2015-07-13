package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
// JIRA: SS-7: By RM02: BOC
import java.util.List;


/**
 * Entity for sending mail, contains all the attributes required for mail sending
 */

public class EmailEntity implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * Constants for indicating the recipient type, i.e mail to be sent as to/cc or bcc
     */
    public static final int RECIPIENT_TYPE_TO = 0;
    public static final int RECIPIENT_TYPE_CC = 1;
    public static final int RECIPIENT_TYPE_BCC = 2;

    private List<String> recipients;
    private String subject;
    private String body;
    private int recipientType;
    private String senderEmailId;
    private String senderName;
    private String senderPassword;
    private String attachmentLocation;


    public List<String> getRecipients()
    {
        return recipients;
    }


    public void setRecipients( List<String> recipients )
    {
        this.recipients = recipients;
    }


    public String getSubject()
    {
        return subject;
    }


    public void setSubject( String subject )
    {
        this.subject = subject;
    }


    public String getBody()
    {
        return body;
    }


    public void setBody( String body )
    {
        this.body = body;
    }


    public int getRecipientType()
    {
        return recipientType;
    }


    public void setRecipientType( int recipientType )
    {
        this.recipientType = recipientType;
    }


    public String getSenderEmailId()
    {
        return senderEmailId;
    }


    public void setSenderEmailId( String senderEmailId )
    {
        this.senderEmailId = senderEmailId;
    }


    public String getSenderName()
    {
        return senderName;
    }


    public void setSenderName( String senderName )
    {
        this.senderName = senderName;
    }


    public String getSenderPassword()
    {
        return senderPassword;
    }


    public void setSenderPassword( String senderPassword )
    {
        this.senderPassword = senderPassword;
    }


    public String getAttachmentLocation()
    {
        return attachmentLocation;
    }


    public void setAttachmentLocation( String attachmentLocation )
    {
        this.attachmentLocation = attachmentLocation;
    }


    @Override
    public String toString()
    {
        return "EmailEntity [recipients=" + recipients + ", subject=" + subject + ", body=" + body + ", recipientType="
            + recipientType + ", senderEmailId=" + senderEmailId + ", senderName=" + senderName;
    }


}

// JIRA: SS-7: By RM02: EOC
