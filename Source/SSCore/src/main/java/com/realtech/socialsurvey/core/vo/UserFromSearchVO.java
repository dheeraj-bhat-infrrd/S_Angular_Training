package com.realtech.socialsurvey.core.vo;

import java.io.Serializable;

/**
 * @author manish
 *
 */
public class UserFromSearchVO implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private long userId;
    private String displayName;
    private String emailId;
    private boolean branchAdmin;
    private boolean agent;
    private boolean regionAdmin;
    private boolean owner;
    
    public long getUserId()
    {
        return userId;
    }
    public void setUserId( long userId )
    {
        this.userId = userId;
    }
    public String getDisplayName()
    {
        return displayName;
    }
    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }
    public String getEmailId()
    {
        return emailId;
    }
    public void setEmailId( String emailId )
    {
        this.emailId = emailId;
    }
    public boolean isBranchAdmin()
    {
        return branchAdmin;
    }
    public void setBranchAdmin( boolean branchAdmin )
    {
        this.branchAdmin = branchAdmin;
    }
    public boolean isAgent()
    {
        return agent;
    }
    public void setAgent( boolean agent )
    {
        this.agent = agent;
    }
    public boolean isRegionAdmin()
    {
        return regionAdmin;
    }
    public void setRegionAdmin( boolean regionAdmin )
    {
        this.regionAdmin = regionAdmin;
    }
    public boolean isOwner()
    {
        return owner;
    }
    public void setOwner( boolean owner )
    {
        this.owner = owner;
    }
    @Override
    public String toString()
    {
        return "UserFromSearchVO [userId=" + userId + ", displayName=" + displayName + ", emailId=" + emailId + ", branchAdmin="
            + branchAdmin + ", agent=" + agent + ", regionAdmin=" + regionAdmin + ", owner=" + owner + "]";
    }
}
