package com.realtech.socialsurvey.api.models;

public class VendastaRmAccountResponse
{
    private String message;
    private long entityId;
    private String entityType;
    private String customerIdentifier;


    public VendastaRmAccountResponse( String message, long entityId, String entityType, String customerIdentifier )
    {
        super();
        this.message = message;
        this.entityId = entityId;
        this.entityType = entityType;
        this.customerIdentifier = customerIdentifier;
    }


    public String getMessage()
    {
        return message;
    }


    public void setMessage( String message )
    {
        this.message = message;
    }


    public long getEntityId()
    {
        return entityId;
    }


    public void setEntityId( long entityId )
    {
        this.entityId = entityId;
    }


    public String getEntityType()
    {
        return entityType;
    }


    public void setEntityType( String entityType )
    {
        this.entityType = entityType;
    }


    public String getCustomerIdentifier()
    {
        return customerIdentifier;
    }


    public void setCustomerIdentifier( String customerIdentifier )
    {
        this.customerIdentifier = customerIdentifier;
    }
}
