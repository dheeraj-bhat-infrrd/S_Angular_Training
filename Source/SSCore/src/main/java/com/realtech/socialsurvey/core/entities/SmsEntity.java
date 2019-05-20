package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.UUID;


/**
 * Entity for sending sms, contains all the attributes required for sms sending
 */

public class SmsEntity implements Serializable
{

    private static final long serialVersionUID = 1L;

    private String randomUUID = UUID.randomUUID().toString();
    private String recipientName;
    private String recipientContactNumber;
    private String smsText;
    private String surveyUrl;
    private long agentId;
    private long companyId;
    private long spiId;
    private String smsCategory;


    public String getRandomUUID()
    {
        return randomUUID;
    }


    public String getRecipientName()
    {
        return recipientName;
    }


    public void setRecipientName( String recipientName )
    {
        this.recipientName = recipientName;
    }


    public String getRecipientContactNumber()
    {
        return recipientContactNumber;
    }


    public void setRecipientContactNumber( String recipientContactNumber )
    {
        this.recipientContactNumber = recipientContactNumber;
    }


    public String getSmsText()
    {
        return smsText;
    }


    public void setSmsText( String smsText )
    {
        this.smsText = smsText;
    }


    public String getSurveyUrl()
    {
        return surveyUrl;
    }


    public void setSurveyUrl( String surveyUrl )
    {
        this.surveyUrl = surveyUrl;
    }


    public long getAgentId()
    {
        return agentId;
    }


    public void setAgentId( long agentId )
    {
        this.agentId = agentId;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public long getSpiId()
    {
        return spiId;
    }


    public void setSpiId( long spiId )
    {
        this.spiId = spiId;
    }


    public String getSmsCategory()
    {
        return smsCategory;
    }


    public void setSmsCategory( String smsCategory )
    {
        this.smsCategory = smsCategory;
    }


    @Override
    public String toString()
    {
        return "SmsEntity [randomUUID=" + randomUUID + ", recipientName=" + recipientName + ", recipientContactNumber="
            + recipientContactNumber + ", smsText=" + smsText + ", surveyUrl=" + surveyUrl + ", agentId=" + agentId
            + ", companyId=" + companyId + ", spiId=" + spiId + ", smsCategory=" + smsCategory + "]";
    }
}
