package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the VENDASTA_SSO_TICKET database table.
 * 
 */
@Entity
@Table ( name = "VENDASTA_SSO_TICKET")
public class VendastaSingleSignOnTicket implements Serializable
{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "VENDASTA_SSO_TICKET_ID")
    private long vendastaSingleSignOnTicketId;

    @Column ( name = "VENDASTA_SSO_TICKET")
    private String vendastaSingleSignOnTicket;

    @Column ( name = "PRODUCT_ID")
    private String productId;

    @Column ( name = "VENDASTA_SSO_TOKEN")
    private String vendastaSingleSignOnToken;

    @Column ( name = "STATUS")
    private boolean status;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column ( name = "CREATED_BY")
    private String createdBy;

    @Column ( name = "MODIFIED_BY")
    private String modifiedBy;


    public long getVendastaSingleSignOnTicketId()
    {
        return vendastaSingleSignOnTicketId;
    }


    public void setVendastaSingleSignOnTicketId( long vendastaSingleSignOnTicketId )
    {
        this.vendastaSingleSignOnTicketId = vendastaSingleSignOnTicketId;
    }


    public String getVendastaSingleSignOnTicket()
    {
        return vendastaSingleSignOnTicket;
    }


    public void setVendastaSingleSignOnTicket( String vendastaSingleSignOnTicket )
    {
        this.vendastaSingleSignOnTicket = vendastaSingleSignOnTicket;
    }


    public String getProductId()
    {
        return productId;
    }


    public void setProductId( String productId )
    {
        this.productId = productId;
    }


    public String getVendastaSingleSignOnToken()
    {
        return vendastaSingleSignOnToken;
    }


    public void setVendastaSingleSignOnToken( String vendastaSingleSignOnToken )
    {
        this.vendastaSingleSignOnToken = vendastaSingleSignOnToken;
    }


    public boolean getStatus()
    {
        return status;
    }


    public void setStatus( boolean status )
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


    public String getCreatedBy()
    {
        return createdBy;
    }


    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }


    public String getModifiedBy()
    {
        return modifiedBy;
    }


    public void setModifiedBy( String modifiedBy )
    {
        this.modifiedBy = modifiedBy;
    }
    
}
