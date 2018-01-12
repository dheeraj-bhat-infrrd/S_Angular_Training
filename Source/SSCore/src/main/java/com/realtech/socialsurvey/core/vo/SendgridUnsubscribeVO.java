package com.realtech.socialsurvey.core.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SendgridUnsubscribeVO
{

    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date created;

    
    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated( Date created )
    {
        this.created = created;
    }
}
