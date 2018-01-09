package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;


@Entity ( "failed_messages")
public class FailedEmailMessage extends FailedMessage implements Serializable
{

    private static final long serialVersionUID = 1L;

    @Embedded
    private EmailMessage data;


    public EmailMessage getData()
    {
        return data;
    }


    public void setData( EmailMessage data )
    {
        this.data = data;
    }


}
