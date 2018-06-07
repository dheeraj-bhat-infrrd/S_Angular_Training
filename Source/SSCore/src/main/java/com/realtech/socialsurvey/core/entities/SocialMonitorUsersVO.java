package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;


public class SocialMonitorUsersVO implements Serializable
{
    private static final long serialVersionUID = 1L;
    private long regionId;
    private long branchId;
    private long userId;
    private String name;
    private String profileImageUrl;


    public long getRegionId()
    {
        return regionId;
    }


    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }


    public long getBranchId()
    {
        return branchId;
    }


    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
    }


    public long getUserId()
    {
        return userId;
    }


    public void setUserId( long userId )
    {
        this.userId = userId;
    }


    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    public String getProfileImageUrl()
    {
        return profileImageUrl;
    }


    public void setProfileImageUrl( String profileImageUrl )
    {
        this.profileImageUrl = profileImageUrl;
    }


    @Override
    public String toString()
    {
        return "SocialMonitorUsersVO [regionId=" + regionId + ", branchId=" + branchId + ", userId=" + userId + ", name=" + name
            + ", profileImageUrl=" + profileImageUrl + "]";
    }


    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) ( branchId ^ ( branchId >>> 32 ) );
        result = prime * result + (int) ( regionId ^ ( regionId >>> 32 ) );
        result = prime * result + (int) ( userId ^ ( userId >>> 32 ) );
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
        SocialMonitorUsersVO other = (SocialMonitorUsersVO) obj;
        if ( branchId != other.branchId )
            return false;
        if ( regionId != other.regionId )
            return false;
        if ( userId != other.userId )
            return false;
        return true;
    }

}
