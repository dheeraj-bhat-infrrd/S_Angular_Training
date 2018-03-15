package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;


public class SegmentsEntity implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private long iden;
    private String name;
    private String profileImageUrl;
    private long regionId;


    public long getIden()
    {
        return iden;
    }


    public void setIden( long iden )
    {
        this.iden = iden;
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


    public long getRegionId()
    {
        return regionId;
    }


    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }


    @Override
    public String toString()
    {
        return "SegmentsEntity [iden=" + iden + ", name=" + name + ", profileImageUrl=" + profileImageUrl + ", regionId="
            + regionId + "]";
    }


}
