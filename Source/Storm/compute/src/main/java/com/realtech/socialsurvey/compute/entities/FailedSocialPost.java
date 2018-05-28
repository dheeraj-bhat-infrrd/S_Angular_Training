package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;


@Entity ( "failed_messages")
public class FailedSocialPost extends FailedMessage implements Serializable
{

    private static final long serialVersionUID = 1L;

    @Embedded
    private SocialResponseObject<?> data;


    public SocialResponseObject<?> getData()
    {
        return data;
    }


    public void setData( SocialResponseObject<?> data )
    {
        this.data = data;
    }


}
