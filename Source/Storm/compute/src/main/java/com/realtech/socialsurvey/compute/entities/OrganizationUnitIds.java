/**
 * 
 */
package com.realtech.socialsurvey.compute.entities;


/**
 * @author Subhrajit
 *
 */
public class OrganizationUnitIds
{
    private long companyId;
    private long regionId;
    private long branchId;
    private long agentId;
    private String agentName;
    private String completeProfileUrl;
    private String profileType;
    private SocialMediaLastFetched socialMediaLastFetched;
    
    
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
    public String getAgentName()
    {
        return agentName;
    }
    public void setAgentName( String agentName )
    {
        this.agentName = agentName;
    }
    public String getCompleteProfileUrl()
    {
        return completeProfileUrl;
    }
    public void setCompleteProfileUrl( String completeProfileUrl )
    {
        this.completeProfileUrl = completeProfileUrl;
    }
    public String getProfileType()
    {
        return profileType;
    }
    public void setProfileType( String profileType )
    {
        this.profileType = profileType;
    }
    public SocialMediaLastFetched getSocialMediaLastFetched()
    {
        return socialMediaLastFetched;
    }
    public void setSocialMediaLastFetched( SocialMediaLastFetched socialMediaLastFetched )
    {
        this.socialMediaLastFetched = socialMediaLastFetched;
    }
    @Override
    public String toString()
    {
        return "OrganizationUnitIds [companyId=" + companyId + ", regionId=" + regionId + ", branchId=" + branchId
            + ", agentId=" + agentId + ", agentName=" + agentName + ", completeProfileUrl=" + completeProfileUrl
            + ", profileType=" + profileType + ", socialMediaLastFetched=" + socialMediaLastFetched + "]";
    }

}
