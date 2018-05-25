package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;

import org.mongodb.morphia.annotations.Embedded;


public class UnsavedUserEvent extends FailedMessage implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Embedded
    private UserEvent data;


    public UserEvent getData()
    {
        return data;
    }


    public void setData( UserEvent data )
    {
        this.data = data;
    }


}
