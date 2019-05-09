package com.realtech.socialsurvey.core.vo;

import java.io.Serializable;


/**
 * @author Lavanya
 */


/**
 * This class is used as a VO for user
 */
public class UserVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private long userId;
    private String fullName;
    private String emailId;
    private int status;
    private String firstName;
    private String lastName;
    private String loginName;
    private int isOwner;
    private long companyId;
    private String profilesMasterId;
    private String displayName;
    private String branchId;
    private String regionId;

    public UserVo()
    {
    }

    public UserVo( long userId, String fullName, String emailId, int status )
    {
        super();
        this.userId = userId;
        this.fullName = fullName;
        this.emailId = emailId;
        this.status = status;
    }


    public long getUserId()
    {
        return userId;
    }


    public void setUserId( long userId )
    {
        this.userId = userId;
    }


    public String getEmailId()
    {
        return emailId;
    }


    public void setEmailId( String emailId )
    {
        this.emailId = emailId;
    }


    public int getStatus()
    {
        return status;
    }


    public void setStatus( int status )
    {
        this.status = status;
    }


    public String getFullName()
    {
        return fullName;
    }


    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }


    public String getFirstName()
    {
        return firstName;
    }


    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }


    public String getLastName()
    {
        return lastName;
    }


    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }


    public String getLoginName()
    {
        return loginName;
    }


    public void setLoginName( String loginName )
    {
        this.loginName = loginName;
    }


    public int getIsOwner()
    {
        return isOwner;
    }


    public void setIsOwner( int isOwner )
    {
        this.isOwner = isOwner;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public String getProfilesMasterId()
    {
        return profilesMasterId;
    }


    public void setProfilesMasterId( String profilesMasterId )
    {
        this.profilesMasterId = profilesMasterId;
    }


    public String getDisplayName()
    {
        return displayName;
    }


    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }


    public String getBranchId()
    {
        return branchId;
    }


    public void setBranchId( String branchId )
    {
        this.branchId = branchId;
    }


    public String getRegionId()
    {
        return regionId;
    }


    public void setRegionId( String regionId )
    {
        this.regionId = regionId;
    }


    @Override public String toString()
    {
        return "UserVo{" + "userId=" + userId + ", fullName='" + fullName + '\'' + ", emailId='" + emailId + '\'' + '}';
    }
}
