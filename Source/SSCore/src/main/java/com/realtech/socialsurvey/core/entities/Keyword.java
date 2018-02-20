package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;

/**
 * @author manish
 *
 */

public class Keyword implements Serializable {
    private String phrase;
    private String id;
    private long createdOn;
    private long modifiedOn;
    private int status;
    private MonitorType monitorType;


    public int getStatus()
    {
        return status;
    }


    public void setStatus( int status )
    {
        this.status = status;
    }


    public String getPhrase()
    {
        return phrase;
    }


    public String getId()
    {
        return id;
    }


    public void setPhrase( String phrase )
    {
        this.phrase = phrase;
    }


    public void setId( String id )
    {
        this.id = id;
    }

    public long getCreatedOn()
    {
        return createdOn;
    }


    public long getModifiedOn()
    {
        return modifiedOn;
    }


    public void setCreatedOn( long createdOn )
    {
        this.createdOn = createdOn;
    }


    public void setModifiedOn( long modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }
    

    public MonitorType getMonitorType() {
		return monitorType;
	}


	public void setMonitorType(MonitorType monitorType) {
		this.monitorType = monitorType;
	}


    @Override
	public String toString() {
		return "Keyword [phrase=" + phrase + ", id=" + id + ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn
				+ ", status=" + status + ", monitorType=" + monitorType + "]";
	}


	@Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
        return result;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        Keyword other = (Keyword) obj;
        if ( id == null ) {
            if ( other.id != null )
                return false;
        } else if ( !id.equals( other.id ) )
            return false;
        return true;
    }
}
