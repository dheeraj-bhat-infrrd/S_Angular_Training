package com.realtech.socialsurvey.core.vo;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * @author manish
 * Reponse VO for sms survey reminder
 */
public class SmsSurveyReminderResponseVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private long surveyPreInitiationId;
    private String customerContactNumber;
    // customerFirstName + space + survey.customerLastName
    private String customerName;
    private int reminderCountsSms;
    private Timestamp modifiedOn;
    private String message;
    private String responseType;


    public long getSurveyPreInitiationId()
    {
        return surveyPreInitiationId;
    }


    public void setSurveyPreInitiationId( long surveyPreInitiationId )
    {
        this.surveyPreInitiationId = surveyPreInitiationId;
    }


    public String getMessage()
    {
        return message;
    }


    public void setMessage( String message )
    {
        this.message = message;
    }


    public String getResponseType()
    {
        return responseType;
    }


    public void setResponseType( String responseType )
    {
        this.responseType = responseType;
    }


    public String getCustomerContactNumber()
    {
        return customerContactNumber;
    }


    public void setCustomerContactNumber( String customerContactNumber )
    {
        this.customerContactNumber = customerContactNumber;
    }


    public String getCustomerName()
    {
        return customerName;
    }


    public void setCustomerName( String customerName )
    {
        this.customerName = customerName;
    }


    public int getReminderCountsSms()
    {
        return reminderCountsSms;
    }


    public void setReminderCountsSms( int reminderCountsSms )
    {
        this.reminderCountsSms = reminderCountsSms;
    }


    public Timestamp getModifiedOn()
    {
        return modifiedOn;
    }


    public void setModifiedOn( Timestamp modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    @Override
    public String toString()
    {
        return "SmsSurveyReminderResponseVO [surveyPreInitiationId=" + surveyPreInitiationId + ", customerContactNumber="
            + customerContactNumber + ", customerName=" + customerName + ", reminderCountsSms=" + reminderCountsSms
            + ", modifiedOn=" + modifiedOn + ", message=" + message + ", responseType=" + responseType + "]";
    }
    
}
