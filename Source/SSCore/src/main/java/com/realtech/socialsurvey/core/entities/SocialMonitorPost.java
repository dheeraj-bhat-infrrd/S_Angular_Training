package com.realtech.socialsurvey.core.entities;

/**
 * Social Monitor Post: Holds all the details present in the SocialPost entity, along with the company,region,branch and agent name, if present
 *
 */
public class SocialMonitorPost
{
    private String source;
    private long companyId = -1;
    private long regionId = -1;
    private long branchId = -1;
    private long agentId = -1;
    private long timeInMillis = 0;
    private String postId;
    private String postText;
    private String postedBy;
    private String postUrl;
    private String _id;
    private String companyName = "";
    private String regionName = "";
    private String branchName = "";
    private String agentName = "";


    public String getSource()
    {
        return source;
    }


    public void setSource( String source )
    {
        this.source = source;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


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


    public long getAgentId()
    {
        return agentId;
    }


    public void setAgentId( long agentId )
    {
        this.agentId = agentId;
    }


    public long getTimeInMillis()
    {
        return timeInMillis;
    }


    public void setTimeInMillis( long timeInMillis )
    {
        this.timeInMillis = timeInMillis;
    }


    public String getPostId()
    {
        return postId;
    }


    public void setPostId( String postId )
    {
        this.postId = postId;
    }


    public String getPostText()
    {
        return postText;
    }


    public void setPostText( String postText )
    {
        this.postText = postText;
    }


    public String getPostedBy()
    {
        return postedBy;
    }


    public void setPostedBy( String postedBy )
    {
        this.postedBy = postedBy;
    }


    public String getPostUrl()
    {
        return postUrl;
    }


    public void setPostUrl( String postUrl )
    {
        this.postUrl = postUrl;
    }


    public String get_id()
    {
        return _id;
    }


    public void set_id( String _id )
    {
        this._id = _id;
    }


    public String getCompanyName()
    {
        return companyName;
    }


    public void setCompanyName( String companyName )
    {
        this.companyName = companyName;
    }


    public String getRegionName()
    {
        return regionName;
    }


    public void setRegionName( String regionName )
    {
        this.regionName = regionName;
    }


    public String getBranchName()
    {
        return branchName;
    }


    public void setBranchName( String branchName )
    {
        this.branchName = branchName;
    }


    public String getAgentName()
    {
        return agentName;
    }


    public void setAgentName( String agentName )
    {
        this.agentName = agentName;
    }
}
