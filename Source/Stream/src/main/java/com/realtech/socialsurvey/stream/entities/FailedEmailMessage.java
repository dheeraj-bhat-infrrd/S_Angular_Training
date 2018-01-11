package com.realtech.socialsurvey.stream.entities;

import com.realtech.socialsurvey.stream.messages.EmailMessage;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;


/**
 * Created by nishit on 04/01/18.
 */
@Document ( collection = "failed_messages")
public class FailedEmailMessage extends FailedMessage implements Serializable
{

    private static final long serialVersionUID = 1L;

    private EmailMessage data;


    public EmailMessage getData()
    {
        return data;
    }


    public void setData( EmailMessage data )
    {
        this.data = data;
    }


    @Override public String toString()
    {
        return "FailedEmailMessage{" +
            "data=" + data +
            "} " + super.toString();
    }
}
