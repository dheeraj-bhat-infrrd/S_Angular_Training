package com.realtech.socialsurvey.core.entities;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Holds the company settings
 */
@Document
public class OrganizationUnitSettings {

	@Id
	private String id;
	private long iden;
	private String uniqueIdentifier;
	private float profile_completion;
	private String profileName;
	private String profileUrl;
	private String profileImageUrl;
	private String logo;
	private boolean isLocationEnabled;
	private boolean isAccountDisabled;
	private boolean isDefaultBySystem;
	private boolean isSeoContentModified;
	private ContactDetailsSettings contact_details;
	private String vertical;
	private CRMInfo crm_info;
	private MailContentSettings mail_content;
	private Licenses licenses;
	private List<Association> associations;
	private List<Achievement> achievements;
	private SurveySettings survey_settings;
	private SocialMediaTokens socialMediaTokens;
	private LockSettings lockSettings;
	private LinkedInProfileData linkedInProfileData;
	private String createdBy;
	private String modifiedBy;
	private long createdOn;
	private long modifiedOn;
	private String completeProfileUrl;
	private List<ProfileStage> profileStages;
	private String disclaimer;
	// image processing
	private String profileImageUrlThumbnail;
	private String logoThumbnail;
	private boolean isProfileImageProcessed;
	private boolean isLogoImageProcessed;
	private String[] hideSectionsFromProfilePage;
	private SocialMediaTokens deletedSocialTokens;

	// status of the record, A for active and D for deleted
	private String status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getIden() {
		return iden;
	}

	public void setIden(long iden) {
		this.iden = iden;
	}

	public float getProfile_completion() {
		return profile_completion;
	}

	public void setProfile_completion(float profile_completion) {
		this.profile_completion = profile_completion;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public boolean getIsLocationEnabled() {
		return isLocationEnabled;
	}

	public void setLocationEnabled(boolean isLocationEnabled) {
		this.isLocationEnabled = isLocationEnabled;
	}

	public boolean getIsAccountDisabled() {
		return isAccountDisabled;
	}

	public void setAccountDisabled(boolean isAccountDisabled) {
		this.isAccountDisabled = isAccountDisabled;
	}

	public boolean isDefaultBySystem() {
		return isDefaultBySystem;
	}

	public void setDefaultBySystem(boolean isDefaultBySystem) {
		this.isDefaultBySystem = isDefaultBySystem;
	}

	public boolean isSeoContentModified() {
		return isSeoContentModified;
	}

	public void setSeoContentModified(boolean isSeoContentModified) {
		this.isSeoContentModified = isSeoContentModified;
	}

	public ContactDetailsSettings getContact_details() {
		return contact_details;
	}

	public void setContact_details(ContactDetailsSettings contact_details) {
		this.contact_details = contact_details;
	}

	public String getVertical() {
		return vertical;
	}

	public void setVertical(String vertical) {
		this.vertical = vertical;
	}

	public CRMInfo getCrm_info() {
		return crm_info;
	}

	public void setCrm_info(CRMInfo crm_info) {
		this.crm_info = crm_info;
	}

	public MailContentSettings getMail_content() {
		return mail_content;
	}

	public void setMail_content(MailContentSettings mail_content) {
		this.mail_content = mail_content;
	}

	public Licenses getLicenses() {
		return licenses;
	}

	public void setLicenses(Licenses licenses) {
		this.licenses = licenses;
	}

	public List<Association> getAssociations() {
		return associations;
	}

	public void setAssociations(List<Association> associations) {
		this.associations = associations;
	}

	public List<Achievement> getAchievements() {
		return achievements;
	}

	public void setAchievements(List<Achievement> achievements) {
		this.achievements = achievements;
	}

	public SurveySettings getSurvey_settings() {
		return survey_settings;
	}

	public void setSurvey_settings(SurveySettings survey_settings) {
		this.survey_settings = survey_settings;
	}

	public SocialMediaTokens getSocialMediaTokens() {
		return socialMediaTokens;
	}

	public LockSettings getLockSettings() {
		return lockSettings;
	}

	public void setLockSettings(LockSettings lockSettings) {
		this.lockSettings = lockSettings;
	}

	public void setSocialMediaTokens(SocialMediaTokens socialMediaTokens) {
		this.socialMediaTokens = socialMediaTokens;
	}

	public LinkedInProfileData getLinkedInProfileData() {
		return linkedInProfileData;
	}

	public void setLinkedInProfileData(LinkedInProfileData linkedInProfileData) {
		this.linkedInProfileData = linkedInProfileData;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public long getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(long modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getCompleteProfileUrl() {
		return completeProfileUrl;
	}

	public void setCompleteProfileUrl(String completeProfileUrl) {
		this.completeProfileUrl = completeProfileUrl;
	}

	public List<ProfileStage> getProfileStages() {
		return profileStages;
	}

	public void setProfileStages(List<ProfileStage> profileStages) {
		this.profileStages = profileStages;
	}

	public String getDisclaimer() {
		return disclaimer;
	}

	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}

	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}

	public String getProfileImageUrlThumbnail() {
		return profileImageUrlThumbnail;
	}

	public void setProfileImageUrlThumbnail(String profileImageUrlThumbnail) {
		this.profileImageUrlThumbnail = profileImageUrlThumbnail;
	}

	public String getLogoThumbnail() {
		return logoThumbnail;
	}

	public void setLogoThumbnail(String logoThumbnail) {
		this.logoThumbnail = logoThumbnail;
	}

	public boolean isProfileImageProcessed() {
		return isProfileImageProcessed;
	}

	public void setProfileImageProcessed(boolean isProfileImageProcessed) {
		this.isProfileImageProcessed = isProfileImageProcessed;
	}

	public boolean isLogoImageProcessed() {
		return isLogoImageProcessed;
	}

	public void setLogoImageProcessed(boolean isLogoImageProcessed) {
		this.isLogoImageProcessed = isLogoImageProcessed;
	}

	public String[] getHideSectionsFromProfilePage() {
		return hideSectionsFromProfilePage;
	}

	public void setHideSectionsFromProfilePage(String[] hideSectionsFromProfilePage) {
		this.hideSectionsFromProfilePage = hideSectionsFromProfilePage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
     * @return the deletedSocialTokens
     */
    public SocialMediaTokens getDeletedSocialTokens()
    {
        return deletedSocialTokens;
    }

    /**
     * @param deletedSocialTokens the deletedSocialTokens to set
     */
    public void setDeletedSocialTokens( SocialMediaTokens deletedSocialTokens )
    {
        this.deletedSocialTokens = deletedSocialTokens;
    }

    @Override
	public String toString() {
		return "OrganizationUnitSettings [id=" + id + ", iden=" + iden + ", profile_completion=" + profile_completion + ", profileName="
				+ profileName + ", profileUrl=" + profileUrl + ", profileImageUrl=" + profileImageUrl + ", logo=" + logo + ", isLocationEnabled="
				+ isLocationEnabled + ", isAccountDisabled=" + isAccountDisabled + ", contact_details=" + contact_details + ", vertical=" + vertical
				+ ", crm_info=" + crm_info + ", mail_content=" + mail_content + ", licenses=" + licenses + ", associations=" + associations
				+ ", achievements=" + achievements + ", survey_settings=" + survey_settings + ", socialMediaTokens=" + socialMediaTokens
				+ ", lockSettings=" + lockSettings + ", disclaimer=" + disclaimer + ", createdBy=" + createdBy + ", modifiedBy=" + modifiedBy
				+ ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn + "]";
	}
}