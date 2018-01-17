package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;
import java.util.List;


/**
 * @author manish
 *
 */
public class SocialPost implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String id;
    private String type;
    private String message;
    private long companyId;
    private List<String> foundKeywords;


    public String getId()
    {
        return id;
    }


    public void setId( String id )
    {
        this.id = id;
    }


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


    public List<String> getFoundKeywords()
    {
        return foundKeywords;
    }


    public void setFoundKeywords( List<String> foundKeywords )
    {
        this.foundKeywords = foundKeywords;
    }


}