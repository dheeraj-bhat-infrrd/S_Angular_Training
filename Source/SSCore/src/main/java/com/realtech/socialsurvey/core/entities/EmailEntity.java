package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
// JIRA: SS-7: By RM02: BOC
import java.util.List;
import java.util.UUID;


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

    private String _id;
    	private String randomUUID = UUID.randomUUID().toString();
    private List<String> recipients;
    private String subject;
    private String body;
    private int recipientType;
    private String senderEmailId;
    private String senderName;
    private String senderPassword;
    private String sendEmailThrough;
    private String mailType;
    private long companyId;
    private boolean sendMailToSalesLead;
    private boolean holdSendingMail;
    private List<EmailAttachment> attachments;
    private String surveySourceId;
    private List<String> recipientsName;
    private String branchName;
    private String regionName;
    private long branchId;
    private long regionId;
    private long agentId;
    private String agentEmailId;
    private boolean isRetried;

    private boolean streamRetryFailed;

    public String get_id() {
		return _id;
	}


	public void set_id(String _id) {
		this._id = _id;
	}
	
    public String getRandomUUID()
    {
        return this.randomUUID;
    }


    public List<EmailAttachment> getAttachments()
    {
        return attachments;
    }


    public void setAttachments( List<EmailAttachment> attachments )
    {
        this.attachments = attachments;
    }


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


    public String getSendEmailThrough()
    {
        return sendEmailThrough;
    }


    public void setSendEmailThrough( String sendEmailThrough )
    {
        this.sendEmailThrough = sendEmailThrough;
    }


    public String getMailType()
    {
        return mailType;
    }


    public void setMailType( String mailType )
    {
        this.mailType = mailType;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public boolean isSendMailToSalesLead()
    {
        return sendMailToSalesLead;
    }


    public void setSendMailToSalesLead( boolean sendMailToSalesLead )
    {
        this.sendMailToSalesLead = sendMailToSalesLead;
    }


    public boolean isHoldSendingMail()
    {
        return holdSendingMail;
    }


    public void setHoldSendingMail( boolean holdSendingMail )
    {
        this.holdSendingMail = holdSendingMail;
    }

    public String getSurveySourceId() {
        return surveySourceId;
    }

    public void setSurveySourceId(String surveySourceId) {
        this.surveySourceId = surveySourceId;
    }

    public List<String> getRecipientsName() {
        return recipientsName;
    }

    public void setRecipientsName(List<String> recipientsName) {
        this.recipientsName = recipientsName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public long getBranchId() {
        return branchId;
    }

    public void setBranchId(long branchId) {
        this.branchId = branchId;
    }

    public long getRegionId() {
        return regionId;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public String getAgentEmailId() {
        return agentEmailId;
    }

    public void setAgentEmailId(String agentEmailId) {
        this.agentEmailId = agentEmailId;
    }

    public boolean isRetried() {
        return isRetried;
    }

    public void setRetried(boolean retried) {
        isRetried = retried;
    }

    public boolean isStreamRetryFailed() {
		return streamRetryFailed;
	}


	public void setStreamRetryFailed(boolean streamRetryFailed) {
		this.streamRetryFailed = streamRetryFailed;
	}


    @Override
    public String toString() {
        return "EmailEntity{" +
                "randomUUID='" + randomUUID + '\'' +
                ", recipients=" + recipients +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", recipientType=" + recipientType +
                ", senderEmailId='" + senderEmailId + '\'' +
                ", senderName='" + senderName + '\'' +
                ", senderPassword='" + senderPassword + '\'' +
                ", sendEmailThrough='" + sendEmailThrough + '\'' +
                ", mailType='" + mailType + '\'' +
                ", companyId=" + companyId +
                ", sendMailToSalesLead=" + sendMailToSalesLead +
                ", holdSendingMail=" + holdSendingMail +
                ", attachments=" + attachments +
                ", surveySourceId='" + surveySourceId + '\'' +
                ", recipientsName=" + recipientsName +
                ", branchName='" + branchName + '\'' +
                ", regionName='" + regionName + '\'' +
                ", branchId=" + branchId +
                ", regionId=" + regionId +
                ", agentId=" + agentId +
                ", agentEmailId='" + agentEmailId + '\'' +
                ", isRetried=" + isRetried +
                '}';
    }
}

// JIRA: SS-7: By RM02: EOC
