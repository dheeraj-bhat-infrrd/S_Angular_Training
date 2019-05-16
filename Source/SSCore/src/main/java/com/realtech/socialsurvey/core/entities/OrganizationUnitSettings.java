package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import com.realtech.socialsurvey.core.entities.widget.WidgetConfiguration;


/**
 * Holds the company settings
 */
@Document
public class OrganizationUnitSettings implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private long iden;
    private String uniqueIdentifier;
    private float profile_completion;
    private String profileName;
    private String profileUrl;
    private String profileImageUrl;
    private String logo;
    private Boolean allowConfigureSecondaryWorkflow;
    private boolean isLocationEnabled;
    private boolean isAccountDisabled;
    private boolean isDefaultBySystem;
    private boolean isSeoContentModified;
    private boolean vendastaAccessible;
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
    private String profileImageUrlRectangularThumbnail;
    private String logoThumbnail;
    private boolean isProfileImageProcessed;
    private boolean isLogoImageProcessed;
    private String[] hideSectionsFromProfilePage;
    private SocialMediaTokens deletedSocialTokens;
    private boolean allowOverrideForSocialMedia; // allow social media connection from auto login as well for all the entities of a company
    private boolean allowZillowAutoPost;
    // status of the record, A for active and D for deleted
    private String status;
    private boolean hiddenSection; // to hide public page of all the users of a company
    private boolean sendEmailFromCompany; // send all the emails on behalf of company
    private String reviewSortCriteria;
    private String sendEmailThrough;
    private boolean hideFromBreadCrumb; // to hide any entity from breadcrumb
    private boolean hidePublicPage; //to hide entity form public page and sitemap
    private Set<String> digestRecipients;
    private boolean includeForTransactionMonitor; // include a company to transaction monitor
    private String encryptedId;
    private boolean isCopyToClipboard;
    private SocialMediaShareFromMailConfig socialMediaShareFromMailConfig;
    private boolean manualCustomerSuurveyPrevented;
    private boolean addPhotosToReview;
    
    // vendasta product details 
    private VendastaProductSettings vendasta_rm_settings;

    //Reporting ranking requirements
    private RankingRequirements ranking_requirements;
    //are the 'Contact Us' emails routed to the company admin always?
    private boolean contactUsEmailsRoutedToCompanyAdmin;

    // flag that decides whether to send the monthly digest mail
    private boolean sendMonthlyDigestMail;
    
    private List<Keyword> filterKeywords;
    
    //Social Monitor Macros
    private List<SocialMonitorMacro> socialMonitorMacros;
    private List<SavedDigestRecord> savedDigestRecords;
    
    private Set<String> userAddDeleteNotificationRecipients;
    
    //SocialMonitor flag
    private boolean isSocialMonitorEnabled;
    
    //trusted sources for socail monitor
    private List<SocialMonitorTrustedSource> socialMonitorTrustedSources;

    private boolean isAgentProfileDisabled;

    private String[] swearWords;
    
    private boolean hasRegisteredForSummit;
    
    private boolean isShowSummitPopup;

    private SocialMediaLastFetched socialMediaLastFetched;
    
    private List<TransactionSourceFtp> transactionSourceFtpList;
    
    //widget configuration
    private WidgetConfiguration widgetConfiguration;
    
    private ZillowShareConfig zillowShareConfig;
    
    private CustomFieldsNameMapping customFieldsNameMapping;

    private Notification notification;
    
    private GeoJsonPoint geoLocation;
    
    private SurveyStats surveyStats;

    private double distanceField;
    
    private long companyId;
    
    private String optoutText;
    private Boolean isLoginEnableAllowed;

    //customer success information
    private String customerSuccessName;
    private long customerSuccessId;
    private String customerSuccessOwner;
    private long closedDate;
    private String rvp;
    private String tmcClient;
    private String surveyDataSource;
    private String dbaForCompany;
    private String potential;
    private long gapAnalysisDate;
    private String customerSettings;
    private String servicesSold;
    private String transferReviewPolicy;
    private String tag;
    private String realtorSurveys;
    private String happyWorkflowSettings;

    //customer success point of contact information
    private String primaryPoc;
    private String poc2;
    private String email;
    private String emailPoc2;
    private String secondaryEmail;
    private String phonePoc2;
    private String phone;
    private long lastConversationDate;

    //ss-admin notes
    List<Notes> notes;

    private boolean isIncompleteSurveyDeleteEnabled = true;

    private boolean branchAdminAllowedToDeleteUser = true;//To Allow Branch Admin Delete User.

    private boolean regionAdminAllowedToDeleteUser = true;//To Allow Region Admin Delete User

    private boolean branchAdminAllowedToAddUser = true;//To Allow Branch Admin Add User

    private boolean regionAdminAllowedToAddUser = true;//To Allow Region Admin Add User
    
    
    public boolean isIncompleteSurveyDeleteEnabled() {
    	return isIncompleteSurveyDeleteEnabled;
    }


    public void setIncompleteSurveyDeleteEnabled(boolean isIncompleteSurveyDeleteEnabled) {
    	this.isIncompleteSurveyDeleteEnabled = isIncompleteSurveyDeleteEnabled;
    }


    public boolean isShowSummitPopup() {
		return isShowSummitPopup;
	}


	public void setShowSummitPopup(boolean isShowSummitPopup) {
		this.isShowSummitPopup = isShowSummitPopup;
	}


	public boolean hasRegisteredForSummit() {
		return hasRegisteredForSummit;
	}


	public void sethasRegisteredForSummit(boolean showSummitPopup) {
		this.hasRegisteredForSummit = showSummitPopup;
	}


	public List<Keyword> getFilterKeywords()
    {
        return filterKeywords;
    }


    public void setFilterKeywords( List<Keyword> filterKeywords )
    {
        this.filterKeywords = filterKeywords;
    }


    //alert details
    private EntityAlertDetails entityAlertDetails;

    private boolean allowPartnerSurvey;


    public boolean isAllowPartnerSurvey()
    {
        return allowPartnerSurvey;
    }

    public void setAllowPartnerSurvey( boolean allowPartnerSurvey )
    {
        this.allowPartnerSurvey = allowPartnerSurvey;
    }

    public boolean isSendEmailFromCompany()
    {
        return sendEmailFromCompany;
    }


    public void setSendEmailFromCompany( boolean sendEmailFromCompany )
    {
        this.sendEmailFromCompany = sendEmailFromCompany;
    }


    public boolean isHiddenSection()
    {
        return hiddenSection;
    }


    public void setHiddenSection( boolean hiddenSection )
    {
        this.hiddenSection = hiddenSection;
    }


    public String getId()
    {
        return id;
    }


    public void setId( String id )
    {
        this.id = id;
    }


    public long getIden()
    {
        return iden;
    }


    public void setIden( long iden )
    {
        this.iden = iden;
    }


    public float getProfile_completion()
    {
        return profile_completion;
    }


    public void setProfile_completion( float profile_completion )
    {
        this.profile_completion = profile_completion;
    }


    public String getProfileName()
    {
        return profileName;
    }


    public void setProfileName( String profileName )
    {
        this.profileName = profileName;
    }


    public String getProfileUrl()
    {
        return profileUrl;
    }


    public void setProfileUrl( String profileUrl )
    {
        this.profileUrl = profileUrl;
    }


    public String getProfileImageUrl()
    {
        return profileImageUrl;
    }


    public void setProfileImageUrl( String profileImageUrl )
    {
        this.profileImageUrl = profileImageUrl;
    }


    public String getLogo()
    {
        return logo;
    }


    public void setLogo( String logo )
    {
        this.logo = logo;
    }


    public Boolean getAllowConfigureSecondaryWorkflow()
    {
        return allowConfigureSecondaryWorkflow;
    }


    public void setAllowConfigureSecondaryWorkflow( Boolean allowConfigureSecondaryWorkflow )
    {
        this.allowConfigureSecondaryWorkflow = allowConfigureSecondaryWorkflow;
    }


    public boolean getIsLocationEnabled()
    {
        return isLocationEnabled;
    }


    public void setLocationEnabled( boolean isLocationEnabled )
    {
        this.isLocationEnabled = isLocationEnabled;
    }


    public boolean getIsAccountDisabled()
    {
        return isAccountDisabled;
    }


    public void setAccountDisabled( boolean isAccountDisabled )
    {
        this.isAccountDisabled = isAccountDisabled;
    }


    public boolean isDefaultBySystem()
    {
        return isDefaultBySystem;
    }


    public void setDefaultBySystem( boolean isDefaultBySystem )
    {
        this.isDefaultBySystem = isDefaultBySystem;
    }


    public boolean isSeoContentModified()
    {
        return isSeoContentModified;
    }


    public void setSeoContentModified( boolean isSeoContentModified )
    {
        this.isSeoContentModified = isSeoContentModified;
    }


    public boolean isVendastaAccessible()
    {
        return vendastaAccessible;
    }


    public void setVendastaAccess( boolean vendastaAccess )
    {
        this.vendastaAccessible = vendastaAccess;
    }


    public ContactDetailsSettings getContact_details()
    {
        return contact_details;
    }


    public void setContact_details( ContactDetailsSettings contact_details )
    {
        this.contact_details = contact_details;
    }


    public String getVertical()
    {
        return vertical;
    }


    public void setVertical( String vertical )
    {
        this.vertical = vertical;
    }


    public CRMInfo getCrm_info()
    {
        return crm_info;
    }


    public void setCrm_info( CRMInfo crm_info )
    {
        this.crm_info = crm_info;
    }


    public MailContentSettings getMail_content()
    {
        return mail_content;
    }


    public void setMail_content( MailContentSettings mail_content )
    {
        this.mail_content = mail_content;
    }


    public Licenses getLicenses()
    {
        return licenses;
    }


    public void setLicenses( Licenses licenses )
    {
        this.licenses = licenses;
    }


    public List<Association> getAssociations()
    {
        return associations;
    }


    public void setAssociations( List<Association> associations )
    {
        this.associations = associations;
    }


    public List<Achievement> getAchievements()
    {
        return achievements;
    }


    public void setAchievements( List<Achievement> achievements )
    {
        this.achievements = achievements;
    }


    public SurveySettings getSurvey_settings()
    {
        return survey_settings;
    }


    public void setSurvey_settings( SurveySettings survey_settings )
    {
        this.survey_settings = survey_settings;
    }


    public SocialMediaTokens getSocialMediaTokens()
    {
        return socialMediaTokens;
    }


    public LockSettings getLockSettings()
    {
        return lockSettings;
    }


    public void setLockSettings( LockSettings lockSettings )
    {
        this.lockSettings = lockSettings;
    }


    public void setSocialMediaTokens( SocialMediaTokens socialMediaTokens )
    {
        this.socialMediaTokens = socialMediaTokens;
    }


    public LinkedInProfileData getLinkedInProfileData()
    {
        return linkedInProfileData;
    }


    public void setLinkedInProfileData( LinkedInProfileData linkedInProfileData )
    {
        this.linkedInProfileData = linkedInProfileData;
    }


    public String getCreatedBy()
    {
        return createdBy;
    }


    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }


    public String getModifiedBy()
    {
        return modifiedBy;
    }


    public void setModifiedBy( String modifiedBy )
    {
        this.modifiedBy = modifiedBy;
    }


    public long getCreatedOn()
    {
        return createdOn;
    }


    public void setCreatedOn( long createdOn )
    {
        this.createdOn = createdOn;
    }


    public long getModifiedOn()
    {
        return modifiedOn;
    }


    public void setModifiedOn( long modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    public String getCompleteProfileUrl()
    {
        return completeProfileUrl;
    }


    public void setCompleteProfileUrl( String completeProfileUrl )
    {
        this.completeProfileUrl = completeProfileUrl;
    }


    public List<ProfileStage> getProfileStages()
    {
        return profileStages;
    }


    public void setProfileStages( List<ProfileStage> profileStages )
    {
        this.profileStages = profileStages;
    }


    public String getDisclaimer()
    {
        return disclaimer;
    }


    public void setDisclaimer( String disclaimer )
    {
        this.disclaimer = disclaimer;
    }


    public String getUniqueIdentifier()
    {
        return uniqueIdentifier;
    }


    public void setUniqueIdentifier( String uniqueIdentifier )
    {
        this.uniqueIdentifier = uniqueIdentifier;
    }


    public String getProfileImageUrlThumbnail()
    {
        return profileImageUrlThumbnail;
    }


    public void setProfileImageUrlThumbnail( String profileImageUrlThumbnail )
    {
        this.profileImageUrlThumbnail = profileImageUrlThumbnail;
    }


    public String getLogoThumbnail()
    {
        return logoThumbnail;
    }


    public void setLogoThumbnail( String logoThumbnail )
    {
        this.logoThumbnail = logoThumbnail;
    }


    public boolean isProfileImageProcessed()
    {
        return isProfileImageProcessed;
    }


    public void setProfileImageProcessed( boolean isProfileImageProcessed )
    {
        this.isProfileImageProcessed = isProfileImageProcessed;
    }


    public boolean isLogoImageProcessed()
    {
        return isLogoImageProcessed;
    }


    public void setLogoImageProcessed( boolean isLogoImageProcessed )
    {
        this.isLogoImageProcessed = isLogoImageProcessed;
    }


    public String[] getHideSectionsFromProfilePage()
    {
        return hideSectionsFromProfilePage;
    }


    public void setHideSectionsFromProfilePage( String[] hideSectionsFromProfilePage )
    {
        this.hideSectionsFromProfilePage = hideSectionsFromProfilePage;
    }


    public String getStatus()
    {
        return status;
    }


    public void setStatus( String status )
    {
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


    /**
     * @return the allowOverrideForSocialMedia
     */
    public boolean isAllowOverrideForSocialMedia()
    {
        return allowOverrideForSocialMedia;
    }


    /**
     * @param allowOverrideForSocialMedia the allowOverrideForSocialMedia to set
     */
    public void setAllowOverrideForSocialMedia( boolean allowOverrideForSocialMedia )
    {
        this.allowOverrideForSocialMedia = allowOverrideForSocialMedia;
    }


    /**
     * @return the allowZillowAutoPost
     */
    public boolean isAllowZillowAutoPost()
    {
        return allowZillowAutoPost;
    }


    /**
     * @param allowZillowAutoPost the allowZillowAutoPost to set
     */
    public void setAllowZillowAutoPost( boolean allowZillowAutoPost )
    {
        this.allowZillowAutoPost = allowZillowAutoPost;
    }


    public VendastaProductSettings getVendasta_rm_settings()
    {
        return vendasta_rm_settings;
    }


    public void setVendasta_rm_settings( VendastaProductSettings vendasta_rm_settings )
    {
        this.vendasta_rm_settings = vendasta_rm_settings;
    }


    public String getReviewSortCriteria()
    {
        return reviewSortCriteria;
    }


    public void setReviewSortCriteria( String reviewSortCriteria )
    {
        this.reviewSortCriteria = reviewSortCriteria;
    }


    public String getSendEmailThrough()
    {
        return sendEmailThrough;
    }


    public void setSendEmailThrough( String sendEmailThrough )
    {
        this.sendEmailThrough = sendEmailThrough;
    }


    public RankingRequirements getRankingRequirements()
    {
        return ranking_requirements;
    }


    public void setRankingRequirements( RankingRequirements ranking_requirements )
    {
        this.ranking_requirements = ranking_requirements;
    }


    public boolean isContactUsEmailsRoutedToCompanyAdmin()
    {
        return contactUsEmailsRoutedToCompanyAdmin;
    }


    public void setContactUsEmailsRoutedToCompanyAdmin( boolean contactUsEmailsRoutedToCompanyAdmin )
    {
        this.contactUsEmailsRoutedToCompanyAdmin = contactUsEmailsRoutedToCompanyAdmin;
    }


    public boolean getHideFromBreadCrumb()
    {
        return hideFromBreadCrumb;
    }


    public void setHideFromBreadCrumb( boolean hideFromBreadCrumb )
    {
        this.hideFromBreadCrumb = hideFromBreadCrumb;
    }


    public String getProfileImageUrlRectangularThumbnail()
    {
        return profileImageUrlRectangularThumbnail;
    }


    public void setProfileImageUrlRectangularThumbnail( String profileImageUrlRectangularThumbnail )
    {
        this.profileImageUrlRectangularThumbnail = profileImageUrlRectangularThumbnail;
    }


    public boolean isSendMonthlyDigestMail()
    {
        return sendMonthlyDigestMail;
    }


    public void setSendMonthlyDigestMail( boolean sendMonthlyDigestMail )
    {
        this.sendMonthlyDigestMail = sendMonthlyDigestMail;
    }


    public boolean isHidePublicPage()
    {
        return hidePublicPage;
    }


    public void setHidePublicPage( boolean hidePublicPage )
    {
        this.hidePublicPage = hidePublicPage;
    }


    public Set<String> getDigestRecipients()
    {
        return digestRecipients;
    }


    public void setDigestRecipients( Set<String> digestRecipients )
    {
        this.digestRecipients = digestRecipients;
    }


    public boolean getIncludeForTransactionMonitor()
    {
        return includeForTransactionMonitor;
    }


    public void setIncludeForTransactionMonitor( boolean includeForTransactionMonitor )
    {
        this.includeForTransactionMonitor = includeForTransactionMonitor;
    }


    public EntityAlertDetails getEntityAlertDetails()
    {
        return entityAlertDetails;
    }


    public void setEntityAlertDetails( EntityAlertDetails entityAlertDetails )
    {
        this.entityAlertDetails = entityAlertDetails;
    }
    
  
    public List<SocialMonitorMacro> getSocialMonitorMacros() {
		return socialMonitorMacros;
	}


	public void setSocialMonitorMacros(List<SocialMonitorMacro> socialMonitorMacros) {
		this.socialMonitorMacros = socialMonitorMacros;
	}
    


    public List<SavedDigestRecord> getSavedDigestRecords()
    {
        return savedDigestRecords;
    }


    public void setSavedDigestRecords( List<SavedDigestRecord> savedDigestRecords )
    {
        this.savedDigestRecords = savedDigestRecords;
    }
    
    


    public List<TransactionSourceFtp> getTransactionSourceFtpList()
    {
        return transactionSourceFtpList;
    }


    public void setTransactionSourceFtpList( List<TransactionSourceFtp> transactionSourceFtpList )
    {
        this.transactionSourceFtpList = transactionSourceFtpList;
    }


    public String getEncryptedId() {
		return encryptedId;
	}


	public void setEncryptedId(String encryptedId) {
		this.encryptedId = encryptedId;
	}

	

	public Set<String> getUserAddDeleteNotificationRecipients()
    {
        return userAddDeleteNotificationRecipients;
    }


    public void setUserAddDeleteNotificationRecipients( Set<String> userAddDeleteNotificationRecipients )
    {
        this.userAddDeleteNotificationRecipients = userAddDeleteNotificationRecipients;
    }

    public boolean isAgentProfileDisabled()
    {
        return isAgentProfileDisabled;
    }


    public void setAgentProfileDisabled( boolean isAgentProfileDisabled )
    {
        this.isAgentProfileDisabled = isAgentProfileDisabled;
    }

    public String[] getSwearWords()
    {
        return swearWords;
    }


    public void setSwearWords( String[] swearWords )
    {
        this.swearWords = swearWords;
    }


    public boolean isSocialMonitorEnabled()
    {
        return isSocialMonitorEnabled;
    }
	

    public void setSocialMonitorEnabled( boolean isSocialMonitorEnabled )
    {
        this.isSocialMonitorEnabled = isSocialMonitorEnabled;
    }


    public List<SocialMonitorTrustedSource> getSocialMonitorTrustedSources() {
		return socialMonitorTrustedSources;
	}


	public void setSocialMonitorTrustedSources(List<SocialMonitorTrustedSource> socialMonitorTrustedSources) {
		this.socialMonitorTrustedSources = socialMonitorTrustedSources;
	}

    public boolean getIsCopyToClipboard()
    {
        return isCopyToClipboard;
    }


    public void setIsCopyToClipboard( boolean isCopyToClipboard )
    {
        this.isCopyToClipboard = isCopyToClipboard;
    }


    public WidgetConfiguration getWidgetConfiguration()
    {
        return widgetConfiguration;
    }


    public void setWidgetConfiguration( WidgetConfiguration widgetConfiguration )
    {
        this.widgetConfiguration = widgetConfiguration;
    }


    public ZillowShareConfig getZillowShareConfig() {
		return zillowShareConfig;
	}


	public void setZillowShareConfig(ZillowShareConfig zillowShareConfig) {
		this.zillowShareConfig = zillowShareConfig;
	}


	public SocialMediaShareFromMailConfig getSocialMediaShareFromMailConfig() {
		return socialMediaShareFromMailConfig;
	}


	public void setSocialMediaShareFromMailConfig(SocialMediaShareFromMailConfig socialMediaShareFromMailConfig) {
		this.socialMediaShareFromMailConfig = socialMediaShareFromMailConfig;
	}


	public boolean isManualCustomerSuurveyPrevented() {
		return manualCustomerSuurveyPrevented;
	}


	public void setManualCustomerSuurveyPrevented(boolean manualCustomerSuurveyPrevented) {
		this.manualCustomerSuurveyPrevented = manualCustomerSuurveyPrevented;
	}


	public CustomFieldsNameMapping getCustomFieldsNameMapping() {
		return customFieldsNameMapping;
	}


	public void setCustomFieldsNameMapping(CustomFieldsNameMapping customFieldsNameMapping) {
		this.customFieldsNameMapping = customFieldsNameMapping;
	}

	public GeoJsonPoint getGeoLocation() {
		return geoLocation;
	}


	public void setGeoLocation(GeoJsonPoint geoLocation) {
		this.geoLocation = geoLocation;
	}

	public double getDistanceField() {
		return distanceField;
	}


	public SurveyStats getSurveyStats() {
		return surveyStats;
	}


	public void setSurveyStats(SurveyStats surveyStats) {
		this.surveyStats = surveyStats;
	}


	public long getCompanyId() {
		return companyId;
	}


	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}


    public SocialMediaLastFetched getSocialMediaLastFetched()
    {
        return socialMediaLastFetched;
    }


    public void setSocialMediaLastFetched( SocialMediaLastFetched socialMediaLastFetched )
    {
        this.socialMediaLastFetched = socialMediaLastFetched;
    }


    public String getOptoutText()
    {
        return optoutText;
    }


    public void setOptoutText( String optoutText )
    {
        this.optoutText = optoutText;
    }


    public Boolean getIsLoginEnableAllowed()
    {
        return isLoginEnableAllowed;
    }


    public void setIsLoginEnableAllowed( Boolean isLoginEnableAllowed )
    {
        this.isLoginEnableAllowed = isLoginEnableAllowed;
    }


    public Notification getNotification()
    {
        return notification;
    }


    public void setNotification( Notification notification )
    {
        this.notification = notification;
    }
    

    public boolean getBranchAdminAllowedToDeleteUser()
    {
        return branchAdminAllowedToDeleteUser;
    }


    public void setBranchAdminAllowedToDeleteUser( boolean branchAdminAllowedToDeleteUser )
    {
        this.branchAdminAllowedToDeleteUser = branchAdminAllowedToDeleteUser;
    }


    public boolean getRegionAdminAllowedToDeleteUser()
    {
        return regionAdminAllowedToDeleteUser;
    }


    public void setRegionAdminAllowedToDeleteUser( boolean regionAdminAllowedToDeleteUser )
    {
        this.regionAdminAllowedToDeleteUser = regionAdminAllowedToDeleteUser;
    }


    public boolean getBranchAdminAllowedToAddUser()
    {
        return branchAdminAllowedToAddUser;
    }


    public void setBranchAdminAllowedToAddUser( boolean branchAdminAllowedToAddUser )
    {
        this.branchAdminAllowedToAddUser = branchAdminAllowedToAddUser;
    }


    public boolean getRegionAdminAllowedToAddUser()
    {
        return regionAdminAllowedToAddUser;
    }


    public void setRegionAdminAllowedToAddUser( boolean regionAdminAllowedToAddUser )
    {
        this.regionAdminAllowedToAddUser = regionAdminAllowedToAddUser;
    }


    public String getCustomerSuccessName()
    {
        return customerSuccessName;
    }


    public void setCustomerSuccessName( String customerSuccessName )
    {
        this.customerSuccessName = customerSuccessName;
    }


    public long getCustomerSuccessId()
    {
        return customerSuccessId;
    }


    public void setCustomerSuccessId( long customerSuccessId )
    {
        this.customerSuccessId = customerSuccessId;
    }


    public String getCustomerSuccessOwner()
    {
        return customerSuccessOwner;
    }


    public void setCustomerSuccessOwner( String customerSuccessOwner )
    {
        this.customerSuccessOwner = customerSuccessOwner;
    }

    public long getClosedDate()
    {
        return closedDate;
    }


    public void setClosedDate( long closedDate )
    {
        this.closedDate = closedDate;
    }


    public String getRvp()
    {
        return rvp;
    }


    public void setRvp( String rvp )
    {
        this.rvp = rvp;
    }


    public String getTmcClient()
    {
        return tmcClient;
    }


    public void setTmcClient( String tmcClient )
    {
        this.tmcClient = tmcClient;
    }


    public String getSurveyDataSource()
    {
        return surveyDataSource;
    }


    public void setSurveyDataSource( String surveyDataSource )
    {
        this.surveyDataSource = surveyDataSource;
    }


    public String getDbaForCompany()
    {
        return dbaForCompany;
    }


    public void setDbaForCompany( String dbaForCompany )
    {
        this.dbaForCompany = dbaForCompany;
    }


    public String getPotential()
    {
        return potential;
    }


    public void setPotential( String potential )
    {
        this.potential = potential;
    }


    public String getCustomerSettings()
    {
        return customerSettings;
    }


    public void setCustomerSettings( String customerSettings )
    {
        this.customerSettings = customerSettings;
    }


    public String getServicesSold()
    {
        return servicesSold;
    }


    public void setServicesSold( String servicesSold )
    {
        this.servicesSold = servicesSold;
    }


    public String getTransferReviewPolicy()
    {
        return transferReviewPolicy;
    }


    public void setTransferReviewPolicy( String transferReviewPolicy )
    {
        this.transferReviewPolicy = transferReviewPolicy;
    }


    public String getTag()
    {
        return tag;
    }


    public void setTag( String tag )
    {
        this.tag = tag;
    }


    public String getRealtorSurveys()
    {
        return realtorSurveys;
    }


    public void setRealtorSurveys( String realtorSurveys )
    {
        this.realtorSurveys = realtorSurveys;
    }


    public String getHappyWorkflowSettings()
    {
        return happyWorkflowSettings;
    }


    public void setHappyWorkflowSettings( String happyWorkflowSettings )
    {
        this.happyWorkflowSettings = happyWorkflowSettings;
    }


    public long getGapAnalysisDate()
    {
        return gapAnalysisDate;
    }


    public void setGapAnalysisDate( long gapAnalysisDate )
    {
        this.gapAnalysisDate = gapAnalysisDate;
    }


    public String getPrimaryPoc()
    {
        return primaryPoc;
    }


    public void setPrimaryPoc( String primaryPoc )
    {
        this.primaryPoc = primaryPoc;
    }


    public String getPoc2()
    {
        return poc2;
    }


    public void setPoc2( String poc2 )
    {
        this.poc2 = poc2;
    }


    public String getEmail()
    {
        return email;
    }


    public void setEmail( String email )
    {
        this.email = email;
    }


    public String getEmailPoc2()
    {
        return emailPoc2;
    }


    public void setEmailPoc2( String emailPoc2 )
    {
        this.emailPoc2 = emailPoc2;
    }


    public String getSecondaryEmail()
    {
        return secondaryEmail;
    }


    public void setSecondaryEmail( String secondaryEmail )
    {
        this.secondaryEmail = secondaryEmail;
    }


    public String getPhonePoc2()
    {
        return phonePoc2;
    }


    public void setPhonePoc2( String phonePoc2 )
    {
        this.phonePoc2 = phonePoc2;
    }


    public String getPhone()
    {
        return phone;
    }


    public void setPhone( String phone )
    {
        this.phone = phone;
    }


    public long getLastConversationDate()
    {
        return lastConversationDate;
    }


    public void setLastConversationDate( long lastConversationDate )
    {
        this.lastConversationDate = lastConversationDate;
    }


    public List<Notes> getNotes()
    {
        return notes;
    }


    public void setNotes( List<Notes> notes )
    {
        this.notes = notes;
    }


    public boolean isAddPhotosToReview() {
		return addPhotosToReview;
	}


	public void setAddPhotosToReview(boolean addPhotosToReview) {
		this.addPhotosToReview = addPhotosToReview;
	}


	@Override
    public String toString()
    {
        return "OrganizationUnitSettings [id=" + id + ", iden=" + iden + ", uniqueIdentifier=" + uniqueIdentifier
            + ", profile_completion=" + profile_completion + ", profileName=" + profileName + ", profileUrl=" + profileUrl
            + ", profileImageUrl=" + profileImageUrl + ", logo=" + logo + ", isLocationEnabled=" + isLocationEnabled
            + ", isAccountDisabled=" + isAccountDisabled + ", isDefaultBySystem=" + isDefaultBySystem
            + ", isSeoContentModified=" + isSeoContentModified + ", vendastaAccessible=" + vendastaAccessible
            + ", contact_details=" + contact_details + ", vertical=" + vertical + ", crm_info=" + crm_info + ", mail_content="
            + mail_content + ", licenses=" + licenses + ", associations=" + associations + ", achievements=" + achievements
            + ", survey_settings=" + survey_settings + ", socialMediaTokens=" + socialMediaTokens + ", lockSettings="
            + lockSettings + ", linkedInProfileData=" + linkedInProfileData + ", createdBy=" + createdBy + ", modifiedBy="
            + modifiedBy + ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn + ", completeProfileUrl="
            + completeProfileUrl + ", profileStages=" + profileStages + ", disclaimer=" + disclaimer
            + ", profileImageUrlThumbnail=" + profileImageUrlThumbnail + ", profileImageUrlRectangularThumbnail="
            + profileImageUrlRectangularThumbnail + ", logoThumbnail=" + logoThumbnail + ", isProfileImageProcessed="
            + isProfileImageProcessed + ", isLogoImageProcessed=" + isLogoImageProcessed + ", hideSectionsFromProfilePage="
            + Arrays.toString( hideSectionsFromProfilePage ) + ", deletedSocialTokens=" + deletedSocialTokens
            + ", allowOverrideForSocialMedia=" + allowOverrideForSocialMedia + ", allowZillowAutoPost=" + allowZillowAutoPost
            + ", status=" + status + ", hiddenSection=" + hiddenSection + ", sendEmailFromCompany=" + sendEmailFromCompany
            + ", reviewSortCriteria=" + reviewSortCriteria + ", sendEmailThrough=" + sendEmailThrough + ", hideFromBreadCrumb="
            + hideFromBreadCrumb + ", hidePublicPage=" + hidePublicPage + ", digestRecipients=" + digestRecipients
            + ", includeForTransactionMonitor=" + includeForTransactionMonitor + ", encryptedId=" + encryptedId
            + ", isCopyToClipboard=" + isCopyToClipboard + ", vendasta_rm_settings=" + vendasta_rm_settings
            + ", ranking_requirements=" + ranking_requirements + ", contactUsEmailsRoutedToCompanyAdmin="
            + contactUsEmailsRoutedToCompanyAdmin + ", sendMonthlyDigestMail=" + sendMonthlyDigestMail + ", filterKeywords="
            + filterKeywords + ", socialMonitorMacros=" + socialMonitorMacros + ", savedDigestRecords=" + savedDigestRecords
            + ", userAddDeleteNotificationRecipients=" + userAddDeleteNotificationRecipients + ", isSocialMonitorEnabled="
            + isSocialMonitorEnabled + ", socialMonitorTrustedSources=" + socialMonitorTrustedSources
            + ", isAgentProfileDisabled=" + isAgentProfileDisabled + ", swearWords=" + Arrays.toString( swearWords )
            + ", hasRegisteredForSummit=" + hasRegisteredForSummit + ", isShowSummitPopup=" + isShowSummitPopup
            + ", transactionSourceFtpList=" + transactionSourceFtpList + ", entityAlertDetails=" + entityAlertDetails
            + ", widgetConfiguration=" + widgetConfiguration + ", socialMediaLastFetched=" + socialMediaLastFetched
            + ", allowPartnerSurvey=" + allowPartnerSurvey + "]";
    }

}