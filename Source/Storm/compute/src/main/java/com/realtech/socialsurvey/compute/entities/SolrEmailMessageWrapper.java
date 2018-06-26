package com.realtech.socialsurvey.compute.entities;

import java.util.ArrayList;
import java.util.List;

public class SolrEmailMessageWrapper extends EmailMessage
{

    private static final long serialVersionUID = 1L;

    private String sendgridMessageId;
    private String emailDeliveredDate;
    private String emailDefferedDate;
    private String emailBlockedDate;
    private String emailOpenedDate;
    private String emailMarkedSpamDate;
    private String emailAttemptedDate;
    private String emailUnsubscribeDate;
    private String emailBounceDate;
    private String emailLinkClickedDate;
    private String emailDroppedDate;
    
    private List<String> attachmentDetail;

    public SolrEmailMessageWrapper( EmailMessage emailMessage )
    {
        this.randomUUID = emailMessage.randomUUID;
        this.attachments = emailMessage.attachments;
        this.body = emailMessage.body;
        this.companyId = emailMessage.companyId;
        this.mailType = emailMessage.mailType;
        this.recipients = emailMessage.recipients;
        this.recipientType = emailMessage.recipientType;
        this.sendEmailThrough = emailMessage.sendEmailThrough;
        this.senderEmailId = emailMessage.senderEmailId;
        this.senderName = emailMessage.senderName;
        this.senderPassword = emailMessage.senderPassword;
        this.subject = emailMessage.subject;
        this.surveySourceId = emailMessage.surveySourceId;
        this.branchName = emailMessage.branchName;
        this.regionName = emailMessage.regionName;
        this.recipientsName = emailMessage.recipientsName;
        this.branchId = emailMessage.branchId;
        this.regionId = emailMessage.regionId;
        this.agentId = emailMessage.agentId;
        this.agentEmailId = emailMessage.agentEmailId;
        this.isRetried = emailMessage.isRetried;
        this.isEmailUnsubscribed = emailMessage.isEmailUnsubscribed;
        this.unsubscribedEmails = emailMessage.unsubscribedEmails;
        
        if(emailMessage.attachments != null && !emailMessage.attachments.isEmpty()){
            attachmentDetail = new ArrayList<>();
            for ( EmailAttachment emailAttachment : emailMessage.attachments ) {
                attachmentDetail.add( emailAttachment.getFilePath() );
            }
        }
    }


    public SolrEmailMessageWrapper( EmailMessage emailMessage, String sendgridMessageId, String emailDeliveredDate,
        String emailDefferedDate, String emailBlockedDate, String emailOpenedDate, String emailMarkedSpamDate,
        String emailAttemptedDate, String emailUnsubscribeDate, String emailBounceDate, String emailLinkClickedDate,
        String emailDroppedDate )
    {
        this( emailMessage );
        this.sendgridMessageId = sendgridMessageId;
        this.emailDeliveredDate = emailDeliveredDate;
        this.emailDefferedDate = emailDefferedDate;
        this.emailBlockedDate = emailBlockedDate;
        this.emailOpenedDate = emailOpenedDate;
        this.emailMarkedSpamDate = emailMarkedSpamDate;
        this.emailAttemptedDate = emailAttemptedDate;
        this.emailUnsubscribeDate = emailUnsubscribeDate;
        this.emailBounceDate = emailBounceDate;
        this.emailLinkClickedDate = emailLinkClickedDate;
        this.emailDroppedDate = emailDroppedDate;
    }


    public String getSendgridMessageId()
    {
        return sendgridMessageId;
    }


    public void setSendgridMessageId( String sendgridMessageId )
    {
        this.sendgridMessageId = sendgridMessageId;
    }


    public String getEmailDeliveredDate()
    {
        return emailDeliveredDate;
    }


    public void setEmailDeliveredDate( String emailDeliveredDate )
    {
        this.emailDeliveredDate = emailDeliveredDate;
    }


    public String getEmailDefferedDate()
    {
        return emailDefferedDate;
    }


    public void setEmailDefferedDate( String emailDefferedDate )
    {
        this.emailDefferedDate = emailDefferedDate;
    }


    public String getEmailBlockedDate()
    {
        return emailBlockedDate;
    }


    public void setEmailBlockedDate( String emailBlockedDate )
    {
        this.emailBlockedDate = emailBlockedDate;
    }


    public String getEmailOpenedDate()
    {
        return emailOpenedDate;
    }


    public void setEmailOpenedDate( String emailOpenedDate )
    {
        this.emailOpenedDate = emailOpenedDate;
    }


    public String getEmailMarkedSpamDate()
    {
        return emailMarkedSpamDate;
    }


    public void setEmailMarkedSpamDate( String emailMarkedSpamDate )
    {
        this.emailMarkedSpamDate = emailMarkedSpamDate;
    }


    public String getEmailAttemptedDate()
    {
        return emailAttemptedDate;
    }


    public void setEmailAttemptedDate( String emailAttemptedDate )
    {
        this.emailAttemptedDate = emailAttemptedDate;
    }


    public String getEmailUnsubscribeDate()
    {
        return emailUnsubscribeDate;
    }


    public void setEmailUnsubscribeDate( String emailUnsubscribeDate )
    {
        this.emailUnsubscribeDate = emailUnsubscribeDate;
    }


    public String getEmailBounceDate()
    {
        return emailBounceDate;
    }


    public void setEmailBounceDate( String emailBounceDate )
    {
        this.emailBounceDate = emailBounceDate;
    }
    
    public String getEmailLinkClickedDate() {
		return emailLinkClickedDate;
	}


	public void setEmailLinkClickedDate(String emailLinkClickedDate) {
		this.emailLinkClickedDate = emailLinkClickedDate;
	}


    public List<String> getAttachmentDetail()
    {
        return attachmentDetail;
    }


    public void setAttachmentDetail( List<String> attachmentDetail )
    {
        this.attachmentDetail = attachmentDetail;
    }


    public String getEmailDroppedDate()
    {
        return emailDroppedDate;
    }


    public void setEmailDroppedDate( String emailDroppedDate )
    {
        this.emailDroppedDate = emailDroppedDate;
    }

}
