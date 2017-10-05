package com.realtech.socialsurvey.core.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name= "user_adoption_report")
public class UserAdoptionReport
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "user_adoption_report_id")
    private String userAdoptionReportId;
    
    @Column(name = "company_id")
    private long companyId;
    
    @Column(name = "company_name")
    private String companyName;
    
    @Column(name = "region_id")
    private long regionId;
    
    @Column(name = "region_name")
    private String regionName;
    
    @Column(name = "branch_id")
    private long branchId;
    
    @Column(name = "branch_name")
    private String branchName;
    
    @Column(name = "invited_users")
    private int invitedUsers;
    
    @Column(name = "active_users")
    private int activeUsers;
    
    @Column(name = "adoption_rate")
    private BigDecimal  adoptionRate;

    public String getUserAdoptionReportId()
    {
        return userAdoptionReportId;
    }

    public void setUserAdoptionReportId( String userAdoptionReportId )
    {
        this.userAdoptionReportId = userAdoptionReportId;
    }

    public long getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName( String companyName )
    {
        this.companyName = companyName;
    }

    public long getRegionId()
    {
        return regionId;
    }

    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }

    public String getRegionName()
    {
        return regionName;
    }

    public void setRegionName( String regionName )
    {
        this.regionName = regionName;
    }

    public long getBranchId()
    {
        return branchId;
    }

    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
    }

    public String getBranchName()
    {
        return branchName;
    }

    public void setBranchName( String branchName )
    {
        this.branchName = branchName;
    }

    public int getInvitedUsers()
    {
        return invitedUsers;
    }

    public void setInvitedUsers( int invitedUsers )
    {
        this.invitedUsers = invitedUsers;
    }

    public int getActiveUsers()
    {
        return activeUsers;
    }

    public void setActiveUsers( int activeUsers )
    {
        this.activeUsers = activeUsers;
    }

    public BigDecimal getAdoptionRate()
    {
        return adoptionRate;
    }

    public void setAdoptionRate( BigDecimal adoptionRate )
    {
        this.adoptionRate = adoptionRate;
    }

    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }
    
    @Override
    public String toString() {
        return "UserAdoptionReport [userAdoptionReportId=" + userAdoptionReportId + ", companyId=" + companyId + ", companyName=" + companyName + ", regionId=" + regionId
                + ", regionName=" + regionName + ", branchId=" + branchId + ", branchName=" + branchName + ", invitedUsers=" + invitedUsers + ", activeUsers=" +  activeUsers + ", adoptionRate=" + adoptionRate +"]";
    }

}
