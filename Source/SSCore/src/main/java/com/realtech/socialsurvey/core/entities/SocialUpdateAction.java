package com.realtech.socialsurvey.core.entities;

import java.util.Date;


public class SocialUpdateAction
{
    private String _id;
    private long agentId;
    private long branchId;
    private long regionId;
    private long companyId;
    private Date updateTime;
    private String socialMediaSource;
    private String action;
    private String link;


    public String get_id()
    {
        return _id;
    }


    public void set_id( String _id )
    {
        this._id = _id;
    }


    public long getAgentId()
    {
        return agentId;
    }


    public void setAgentId( long agentId )
    {
        this.agentId = agentId;
    }


    public long getBranchId()
    {
        return branchId;
    }


    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
    }


    public long getRegionId()
    {
        return regionId;
    }


    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public Date getUpdateTime()
    {
        return updateTime;
    }


    public void setUpdateTime( Date updateTime )
    {
        this.updateTime = updateTime;
    }


    public String getSocialMediaSource()
    {
        return socialMediaSource;
    }


    public void setSocialMediaSource( String socialMediaSource )
    {
        this.socialMediaSource = socialMediaSource;
    }


    public String getAction()
    {
        return action;
    }


    public void setAction( String action )
    {
        this.action = action;
    }


    public String getLink()
    {
        return link;
    }


    public void setLink( String link )
    {
        this.link = link;
    }
}
