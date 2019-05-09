package com.realtech.socialsurvey.core.entities;

import com.realtech.socialsurvey.core.vo.SocialMediaVO;

import java.util.List;

/**
 * User entity from the search
 */
public class UserFromSearch {

	private long userId;
	private String firstName;
	private String lastName;
	private String emailId;
	private String loginName;
	private int isOwner;
	private String displayName;
	private long companyId;
	private int status;
	private boolean isRegionAdmin;
	private boolean isBranchAdmin;
	private boolean isAgent;
	private List<Long> regions;
	private List<Long> branches;
	private boolean canEdit;
	private String title;
	private String aboutMe;
	private String profileUrl;
	private String profileName;
	private String profileImageUrl;
	private String profileImageThumbnail;
	private List<Long> agentIds;
	private long reviewCount;
	private boolean isProfileImageSet;
	private List<SocialMediaVO> socialMediaVOs;
	private String address;
	private String contactNumber;
	private String disclaimer;
	private String webUrl;
	private Licenses licenses;

	public List<Long> getRegions() {
		return regions;
	}

	public void setRegions(List<Long> regions) {
		this.regions = regions;
	}

	public List<Long> getBranches() {
		return branches;
	}

	public void setBranches(List<Long> branches) {
		this.branches = branches;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public int getIsOwner() {
		return isOwner;
	}

	public void setIsOwner(int isOwner) {
		this.isOwner = isOwner;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean getIsRegionAdmin() {
		return isRegionAdmin;
	}

	public void setRegionAdmin(boolean isRegionAdmin) {
		this.isRegionAdmin = isRegionAdmin;
	}

	public boolean getIsBranchAdmin() {
		return isBranchAdmin;
	}

	public void setBranchAdmin(boolean isBranchAdmin) {
		this.isBranchAdmin = isBranchAdmin;
	}

	public boolean getIsAgent() {
		return isAgent;
	}

	public void setAgent(boolean isAgent) {
		this.isAgent = isAgent;
	}

	public boolean getCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAboutMe() {
		return aboutMe;
	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public List<Long> getAgentIds() {
		return agentIds;
	}

	public void setAgentIds(List<Long> agentIds) {
		this.agentIds = agentIds;
	}

	public long getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(long reviewCount) {
		this.reviewCount = reviewCount;
	}

	/**
     * @return the profileImageThumbnail
     */
    public String getProfileImageThumbnail()
    {
        return profileImageThumbnail;
    }

    /**
     * @param profileImageThumbnail the profileImageThumbnail to set
     */
    public void setProfileImageThumbnail( String profileImageThumbnail )
    {
        this.profileImageThumbnail = profileImageThumbnail;
    }

    /**
     * @return the isProfileImageSet
     */
    public boolean isProfileImageSet()
    {
        return isProfileImageSet;
    }

    /**
     * @param isProfileImageSet the isProfileImageSet to set
     */
    public void setProfileImageSet( boolean isProfileImageSet )
    {
        this.isProfileImageSet = isProfileImageSet;
    }


	public List<SocialMediaVO> getSocialMediaVOs()
	{
		return socialMediaVOs;
	}


	public void setSocialMediaVOs( List<SocialMediaVO> socialMediaVOs )
	{
		this.socialMediaVOs = socialMediaVOs;
	}
	
	


	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getDisclaimer() {
		return disclaimer;
	}

	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}

	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}
	
	public Licenses getLicenses() {
		return licenses;
	}

	public void setLicenses(Licenses licenses) {
		this.licenses = licenses;
	}


	@Override public String toString()
	{
		return "UserFromSearch{" + "userId=" + userId + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\''
			+ ", emailId='" + emailId + '\'' + ", loginName='" + loginName + '\'' + ", isOwner=" + isOwner + ", displayName='"
			+ displayName + '\'' + ", companyId=" + companyId + ", status=" + status + ", isRegionAdmin=" + isRegionAdmin
			+ ", isBranchAdmin=" + isBranchAdmin + ", isAgent=" + isAgent + ", regions=" + regions + ", branches=" + branches
			+ ", canEdit=" + canEdit + ", title='" + title + '\'' + ", aboutMe='" + aboutMe + '\'' + ", profileUrl='"
			+ profileUrl + '\'' + ", profileName='" + profileName + '\'' + ", profileImageUrl='" + profileImageUrl + '\''
			+ ", profileImageThumbnail='" + profileImageThumbnail + '\'' + ", agentIds=" + agentIds + ", reviewCount="
			+ reviewCount + ", isProfileImageSet=" + isProfileImageSet + ", socialMediaVOs=" + socialMediaVOs + ", address='"
			+ address + '\'' + ", contactNumber='" + contactNumber + '\'' + ", disclaimer='" + disclaimer + '\'' + ", webUrl='"
			+ webUrl + '\'' + ", licenses=" + licenses + '}';
	}
}