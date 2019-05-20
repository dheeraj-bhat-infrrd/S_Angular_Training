package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;


/**
 * @author manish
 *
 */
public class SSTimeZone implements Serializable
{

    private static final long serialVersionUID = 1L;
    private int sequenceId;
    private String zoneId;
    private String displayName;
    
    public int getSequenceId()
    {
        return sequenceId;
    }


    public void setSequenceId( int sequenceId )
    {
        this.sequenceId = sequenceId;
    }


    public String getZoneId()
    {
        return zoneId;
    }


    public void setZoneId( String zoneId )
    {
        this.zoneId = zoneId;
    }


    public String getDisplayName()
    {
        return displayName;
    }


    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }


    @Override
    public String toString()
    {
        return "SSTimeZone [sequenceId=" + sequenceId + ", zoneId=" + zoneId + ", displayName=" + displayName + "]";
    }


}
