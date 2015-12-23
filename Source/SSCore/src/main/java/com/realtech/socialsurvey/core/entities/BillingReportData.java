package com.realtech.socialsurvey.core.entities;

import java.util.List;


/**
 * entity to store billing report data
 */
public class BillingReportData
{
    private long userId;
    private String firstName;
    private String lastName;
    private String loginName;
    private long regionId;
    private long branchId;
    private String region;
    private String branch;
    private List<Long> profilesMasterIds;


    /**
     * @return the userId
     */
    public long getUserId()
    {
        return userId;
    }


    /**
     * @param userId the userId to set
     */
    public void setUserId( long userId )
    {
        this.userId = userId;
    }


    /**
     * @return the firstName
     */
    public String getFirstName()
    {
        return firstName;
    }


    /**
     * @param firstName the firstName to set
     */
    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }


    /**
     * @return the lastName
     */
    public String getLastName()
    {
        return lastName;
    }


    /**
     * @param lastName the lastName to set
     */
    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }


    /**
     * @return the loginName
     */
    public String getLoginName()
    {
        return loginName;
    }


    /**
     * @param loginName the loginName to set
     */
    public void setLoginName( String loginName )
    {
        this.loginName = loginName;
    }


    /**
     * @return the regionId
     */
    public long getRegionId()
    {
        return regionId;
    }


    /**
     * @param regionId the regionId to set
     */
    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }


    /**
     * @return the branchId
     */
    public long getBranchId()
    {
        return branchId;
    }


    /**
     * @param branchId the branchId to set
     */
    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
    }


    /**
     * @return the region
     */
    public String getRegion()
    {
        return region;
    }


    /**
     * @param region the region to set
     */
    public void setRegion( String region )
    {
        this.region = region;
    }


    /**
     * @return the branch
     */
    public String getBranch()
    {
        return branch;
    }


    /**
     * @param branch the branch to set
     */
    public void setBranch( String branch )
    {
        this.branch = branch;
    }


    /**
     * @return the profilesMasterIds
     */
    public List<Long> getProfilesMasterIds()
    {
        return profilesMasterIds;
    }


    /**
     * @param profilesMasterIds the profilesMasterIds to set
     */
    public void setProfilesMasterIds( List<Long> profilesMasterIds )
    {
        this.profilesMasterIds = profilesMasterIds;
    }
}
