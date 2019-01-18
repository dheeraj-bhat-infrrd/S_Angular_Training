/**
 * 
 */
package com.realtech.socialsurvey.core.entities;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Subhrajit
 *
 */
@Entity
@Table (name = "DELETE_DATA_TRACKER")
public class DeleteDataTracker
{
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "DELETE_DATA_TRACKER_ID")
    private long id;
    
    @Column(name="ENTITY_ID")
    private String entityId;

    @Column(name="ENTITY_TYPE")
    private String entityType;
    
    @Column(name = "IS_DELETED")
    private int isDeleted;
    
    @Column(name = "CREATED_ON")
    private Timestamp createdOn;
    
    @Column(name="MODIFIED_ON")
    private Timestamp modifiedOn;

    public String getEntityId()
    {
        return entityId;
    }

    public void setEntityId( String entityId )
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

    public int getIsDeleted()
    {
        return isDeleted;
    }

    public void setIsDeleted( int isDeleted )
    {
        this.isDeleted = isDeleted;
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

    @Override
    public String toString()
    {
        return "DeleteDataTracker [id=" + id + ", entityId=" + entityId + ", entityType=" + entityType + ", isDeleted="
            + isDeleted + ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn + "]";
    }

}
