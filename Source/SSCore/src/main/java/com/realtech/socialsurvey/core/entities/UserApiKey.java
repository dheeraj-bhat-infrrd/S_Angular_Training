package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the USER_API_KEYS database table.
 */
@Entity
@Table ( name = "USER_API_KEYS")
@NamedQuery ( name = "USER_API_KEYS.findAll", query = "SELECT u FROM UserApiKey u")
public class UserApiKey implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "USER_API_KEY_ID")
    private long userApiKeyId;

    @Column ( name = "API_SECRET")
    private String apiSecret;

    @Column ( name = "API_KEY")
    private String apiKey;

    @Column ( name = "COMPANY_ID")
    private long companyId;

    @Column ( name = "STATUS")
    private int status;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;


    public long getUserApiKeyId()
    {
        return userApiKeyId;
    }


    public void setUserApiKeyId( long userApiKeyId )
    {
        this.userApiKeyId = userApiKeyId;
    }


    public String getApiSecret()
    {
        return apiSecret;
    }


    public void setApiSecret( String apiSecret )
    {
        this.apiSecret = apiSecret;
    }


    public String getApiKey()
    {
        return apiKey;
    }


    public void setApiKey( String apiKey )
    {
        this.apiKey = apiKey;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public int getStatus()
    {
        return status;
    }


    public void setStatus( int status )
    {
        this.status = status;
    }


    public Timestamp getCreatedOn()
    {
        return createdOn;
    }


    public void setCreatedOn( Timestamp createdOn )
    {
        this.createdOn = createdOn;
    }


    public Timestamp getModifiedOn()
    {
        return modifiedOn;
    }


    public void setModifiedOn( Timestamp modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }

}
