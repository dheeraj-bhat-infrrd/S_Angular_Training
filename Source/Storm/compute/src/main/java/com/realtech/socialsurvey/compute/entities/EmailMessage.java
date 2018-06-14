package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;
import java.util.List;

import org.mongodb.morphia.annotations.Embedded;


/**
 * Email message for sending mail, contains all the attributes required for mail sending
 */

@Embedded
public class EmailMessage implements Serializable
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

    protected String randomUUID;
    protected List<String> recipients;
    protected String subject;
    protected String body;
    protected int recipientType;
    protected String senderEmailId;
    protected String senderName;
    protected String senderPassword;
    protected String sendEmailThrough;
    protected String mailType;
    protected long companyId;
    protected boolean sendMailToSalesLead;
    protected boolean holdSendingMail;
    protected List<EmailAttachment> attachments;
    protected String surveySourceId;
    protected List<String> recipientsName;
    protected String branchName;
    protected String regionName;
    protected long branchId;
    protected long regionId;
    protected long agentId;
    protected String agentEmailId;
    protected boolean isRetried;
    protected boolean isEmailUnsubscribed;
    protected List<String> unsubscribedEmails;

    public String getRandomUUID()
    {
        return randomUUID;
    }


    public void setRandomUUID( String uuId )
    {
        this.randomUUID = uuId;
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
    
    public List<EmailAttachment> getAttachments()
    {
        return attachments;
    }


    public void setAttachments( List<EmailAttachment> attachments )
    {
        this.attachments = attachments;
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

    public void setRetried(boolean retried) {
        isRetried = retried;
    }

    public boolean isRetried() {
        return isRetried;
    }

    public boolean isEmailUnsubscribed()
    {
        return isEmailUnsubscribed;
    }


    public void setEmailUnsubscribed( boolean isEmailUnsubscribed )
    {
        this.isEmailUnsubscribed = isEmailUnsubscribed;
    }


    public List<String> getUnsubscribedEmails()
    {
        return unsubscribedEmails;
    }


    public void setUnsubscribedEmails( List<String> unsubscribedEmails )
    {
        this.unsubscribedEmails = unsubscribedEmails;
    }


    @Override
    public String toString()
    {
        return "EmailMessage [randomUUID=" + randomUUID + ", recipients=" + recipients + ", subject=" + subject + ", body="
            + body + ", recipientType=" + recipientType + ", senderEmailId=" + senderEmailId + ", senderName=" + senderName
            + ", senderPassword=" + senderPassword + ", sendEmailThrough=" + sendEmailThrough + ", mailType=" + mailType
            + ", companyId=" + companyId + ", sendMailToSalesLead=" + sendMailToSalesLead + ", holdSendingMail="
            + holdSendingMail + ", attachments=" + attachments + ", surveySourceId=" + surveySourceId + ", recipientsName="
            + recipientsName + ", branchName=" + branchName + ", regionName=" + regionName + ", branchId=" + branchId
            + ", regionId=" + regionId + ", agentId=" + agentId + ", agentEmailId=" + agentEmailId + ", isRetried=" + isRetried
            + ", isEmailUnsubscribed=" + isEmailUnsubscribed + ", unsubscribedEmails=" + unsubscribedEmails + "]";
    }


}

// JIRA: SS-7: By RM02: EOC
