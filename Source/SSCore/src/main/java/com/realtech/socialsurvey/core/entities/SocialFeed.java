package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Mongo entity class for new social monitor
 * @author manish
 *
 */
@Document
public class SocialFeed implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String type;
    private String message;
    private long companyId;
    private List<String> foundKeywords;


    public String getType()
    {
        return type;
    }


    public String getMessage()
    {
        return message;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public List<String> getFoundKeywords()
    {
        return foundKeywords;
    }


    public void setType( String type )
    {
        this.type = type;
    }


    public void setMessage( String message )
    {
        this.message = message;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public void setFoundKeywords( List<String> foundKeywords )
    {
        this.foundKeywords = foundKeywords;
    }


    public String getId()
    {
        return id;
    }


    public void setId( String id )
    {
        this.id = id;
    }


    @Override
    public String toString()
    {
        return "SocialFeed [id=" + id + ", type=" + type + ", message=" + message + ", companyId=" + companyId
            + ", foundKeywords=" + foundKeywords + "]";
    }
}
