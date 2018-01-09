package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;


@Entity ( "failed_messages")
public class FailedSocialPost extends FailedMessage implements Serializable
{

    private static final long serialVersionUID = 1L;

    @Embedded
    private SocialPost data;


    public SocialPost getData()
    {
        return data;
    }


    public void setData( SocialPost data )
    {
        this.data = data;
    }


}
