package com.realtech.socialsurvey.stream.entities;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;


@Document ( collection = "failed_messages")
public class FailedSocialPost extends FailedMessage implements Serializable
{

    private static final long serialVersionUID = 1L;

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