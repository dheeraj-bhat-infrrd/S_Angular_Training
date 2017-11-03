package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table ( name = "user_profile")
public class ReportingUserProfile implements Serializable
{


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "USER_PROFILE_ID")
    private long userProfileId;

    @Column ( name = "USER_ID")
    private long userId;

    @Column ( name = "COMPANY_ID")
    private long companyId;

    @Column ( name = "EMAIL_ID")
    private String emailId;

    @Column ( name = "REGION_ID")
    private long regionId;

    @Column ( name = "BRANCH_ID")
    private long branchId;

    @Column ( name = "AGENT_ID")
    private long agentId;

    @Column ( name = "PROFILES_MASTER_ID")
    private ProfilesMaster profilesMasterId;

    @Column ( name = "USER_PROFILE_TYPE")
    private String userProfileType;

    @Column ( name = "PROFILE_COMPLETION_STAGE")
    private String profileCompletionStage;

    @Column ( name = "IS_PROFILE_COMPLETE")
    private int isProfileComplete;

    @Column ( name = "IS_PRIMARY")
    private int isPrimary;

    @Column ( name = "STATUS")
    private int status;

    @Column ( name = "CREATED_BY")
    private String createdBy;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "MODIFIED_BY")
    private String modifiedBy;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column ( name = "CREATED_ON_EST")
    private Timestamp createdOnEst;

    @Column ( name = "MODIFIED_ON_EST")
    private Timestamp modifiedOnEst;


    public long getUserProfileId()
    {
        return userProfileId;
    }


    public void setUserProfileId( long userProfileId )
    {
        this.userProfileId = userProfileId;
    }


    public long getUserId()
    {
        return userId;
    }


    public void setUserId( long userId )
    {
        this.userId = userId;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public String getEmailId()
    {
        return emailId;
    }


    public void setEmailId( String emailId )
    {
        this.emailId = emailId;
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


    public ProfilesMaster getProfilesMasterId()
    {
        return profilesMasterId;
    }


    public void setProfilesMasterId( ProfilesMaster profilesMasterId )
    {
        this.profilesMasterId = profilesMasterId;
    }


    public String getUserProfileType()
    {
        return userProfileType;
    }


    public void setUserProfileType( String userProfileType )
    {
        this.userProfileType = userProfileType;
    }


    public String getProfileCompletionStage()
    {
        return profileCompletionStage;
    }


    public void setProfileCompletionStage( String profileCompletionStage )
    {
        this.profileCompletionStage = profileCompletionStage;
    }


    public int getIsProfileComplete()
    {
        return isProfileComplete;
    }


    public void setIsProfileComplete( int isProfileComplete )
    {
        this.isProfileComplete = isProfileComplete;
    }


    public int getIsPrimary()
    {
        return isPrimary;
    }


    public void setIsPrimary( int isPrimary )
    {
        this.isPrimary = isPrimary;
    }


    public int getStatus()
    {
        return status;
    }


    public void setStatus( int status )
    {
        this.status = status;
    }


    public String getCreatedBy()
    {
        return createdBy;
    }


    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }


    public Timestamp getCreatedOn()
    {
        return createdOn;
    }


    public void setCreatedOn( Timestamp createdOn )
    {
        this.createdOn = createdOn;
    }


    public String getModifiedBy()
    {
        return modifiedBy;
    }


    public void setModifiedBy( String modifiedBy )
    {
        this.modifiedBy = modifiedBy;
    }


    public Timestamp getModifiedOn()
    {
        return modifiedOn;
    }


    public void setModifiedOn( Timestamp modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    public Timestamp getCreatedOnEst()
    {
        return createdOnEst;
    }


    public void setCreatedOnEst( Timestamp createdOnEst )
    {
        this.createdOnEst = createdOnEst;
    }


    public Timestamp getModifiedOnEst()
    {
        return modifiedOnEst;
    }


    public void setModifiedOnEst( Timestamp modifiedOnEst )
    {
        this.modifiedOnEst = modifiedOnEst;
    }


    @Override
    public String toString()
    {
        return "ReportingUserProfile [userProfileId=" + userProfileId + ", userId=" + userId + ", companyId=" + companyId
            + ", emailId=" + emailId + ", regionId=" + regionId + ", branchId=" + branchId + ", agentId=" + agentId
            + ", profilesMasterId=" + profilesMasterId + ", userProfileType=" + userProfileType + ", profileCompletionStage="
            + profileCompletionStage + ", isProfileComplete=" + isProfileComplete + ", isPrimary=" + isPrimary + ", status="
            + status + ", createdBy=" + createdBy + ", createdOn=" + createdOn + ", modifiedBy=" + modifiedBy + ", modifiedOn="
            + modifiedOn + ", createdOnEst=" + createdOnEst + ", modifiedOnEst=" + modifiedOnEst + "]";
    }


}
