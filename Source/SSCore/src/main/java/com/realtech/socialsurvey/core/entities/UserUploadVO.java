package com.realtech.socialsurvey.core.entities;

/*
 * The view class for User
 */
public class UserUploadVO
{

    private String firstName;
    private boolean isFirstNameModified;
    private StringUploadHistory[] firstNameHistory;
    private String lastName;
    private boolean isLastNameModified;
    private StringUploadHistory[] lastNameHistory;
    private String title;
    private boolean isTitleModified;
    private StringUploadHistory[] titleHistory;
    private String sourceBranchId;
    private boolean isSourceBranchIdModified;
    private StringUploadHistory[] sourceBranchIdHistory;
    private long branchId;
    private boolean isBranchIdModified;
    private LongUploadHistory[] branchIdHistory;
    private String sourceRegionId;
    private boolean isSourceRegionIdModified;
    private StringUploadHistory[] sourceRegionIdHistory;
    private long regionId;
    private boolean isRegionIdModified;
    private LongUploadHistory[] regionIdHistory;
    private boolean isAgent;
    private boolean isAgentModified;
    private BooleanUploadHistory[] isAgentHistory;
    private String emailId;
    private boolean isEmailIdModified;
    private StringUploadHistory[] emailIdHistory;
    private boolean belongsToCompany;
    private boolean isBelongsToCompanyModified;
    private BooleanUploadHistory[] belongsToCompanyHistory;
    private boolean assignToCompany;
    private boolean isAssignToCompanyModified;
    private BooleanUploadHistory[] assignToCompanyHistory;
    private String assignedBranchName;
    private boolean isAssignedBranchNameModified;
    private StringUploadHistory[] assignedBranchNameHistory;
    private String[] assignedBranches;
    private boolean isAssignedBranchesModified;
    private StringArrayUploadHistory[] assignedBranchesHistory;
    private String assignedRegionName;
    private boolean isAssignedRegionNameModified;
    private StringUploadHistory[] assignedRegionNameHistory;
    private String[] assignedRegions;
    private boolean isAssignedRegionsModified;
    private StringArrayUploadHistory[] assignedRegionsHistory;
    private boolean isBranchAdmin;
    private boolean isBranchAdminModified;
    private BooleanUploadHistory[] isBranchAdminHistory;
    private String[] assignedBrachesAdmin;
    private boolean isAssignedBrachesAdminModified;
    private StringArrayUploadHistory[] assignedBrachesAdminHistory;
    private boolean isRegionAdmin;
    private boolean isRegionAdminModified;
    private BooleanUploadHistory[] isRegionAdminHistory;
    private String[] assignedRegionsAdmin;
    private boolean isAssignedRegionsAdminModified;
    private StringArrayUploadHistory[] assignedRegionsAdminHistory;
    private String phoneNumber;
    private boolean isPhoneNumberModified;
    private StringUploadHistory[] phoneNumberHistory;
    private String websiteUrl;
    private boolean isWebsiteUrlModified;
    private StringUploadHistory[] websiteUrlHistory;
    private String license;
    private boolean isLicenseModified;
    private StringUploadHistory[] licenseHistory;
    private String legalDisclaimer;
    private boolean isLegalDisclaimerModified;
    private StringUploadHistory[] legalDisclaimerHistory;
    private String aboutMeDescription;
    private boolean isAboutMeDescriptionModified;
    private StringUploadHistory[] aboutMeDescriptionHistory;
    private String userPhotoUrl;
    private boolean isUserPhotoUrlModified;
    private StringUploadHistory[] userPhotoUrlHistory;
    private boolean isUserAdded;
    private boolean isUserModified;


    public String getFirstName()
    {
        return firstName;
    }


    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }


    public boolean isFirstNameModified()
    {
        return isFirstNameModified;
    }


    public void setFirstNameModified( boolean isFirstNameModified )
    {
        this.isFirstNameModified = isFirstNameModified;
    }


    public StringUploadHistory[] getFirstNameHistory()
    {
        return firstNameHistory;
    }


    public void setFirstNameHistory( StringUploadHistory[] firstNameHistory )
    {
        this.firstNameHistory = firstNameHistory;
    }


    public String getLastName()
    {
        return lastName;
    }


    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }


    public boolean isLastNameModified()
    {
        return isLastNameModified;
    }


    public void setLastNameModified( boolean isLastNameModified )
    {
        this.isLastNameModified = isLastNameModified;
    }


    public StringUploadHistory[] getLastNameHistory()
    {
        return lastNameHistory;
    }


    public void setLastNameHistory( StringUploadHistory[] lastNameHistory )
    {
        this.lastNameHistory = lastNameHistory;
    }


    public String getTitle()
    {
        return title;
    }


    public void setTitle( String title )
    {
        this.title = title;
    }


    public boolean isTitleModified()
    {
        return isTitleModified;
    }


    public void setTitleModified( boolean isTitleModified )
    {
        this.isTitleModified = isTitleModified;
    }


    public StringUploadHistory[] getTitleHistory()
    {
        return titleHistory;
    }


    public void setTitleHistory( StringUploadHistory[] titleHistory )
    {
        this.titleHistory = titleHistory;
    }


    public String getSourceBranchId()
    {
        return sourceBranchId;
    }


    public void setSourceBranchId( String sourceBranchId )
    {
        this.sourceBranchId = sourceBranchId;
    }


    public boolean isSourceBranchIdModified()
    {
        return isSourceBranchIdModified;
    }


    public void setSourceBranchIdModified( boolean isSourceBranchIdModified )
    {
        this.isSourceBranchIdModified = isSourceBranchIdModified;
    }


    public StringUploadHistory[] getSourceBranchIdHistory()
    {
        return sourceBranchIdHistory;
    }


    public void setSourceBranchIdHistory( StringUploadHistory[] sourceBranchIdHistory )
    {
        this.sourceBranchIdHistory = sourceBranchIdHistory;
    }


    public long getBranchId()
    {
        return branchId;
    }


    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
    }


    public boolean isBranchIdModified()
    {
        return isBranchIdModified;
    }


    public void setBranchIdModified( boolean isBranchIdModified )
    {
        this.isBranchIdModified = isBranchIdModified;
    }


    public LongUploadHistory[] getBranchIdHistory()
    {
        return branchIdHistory;
    }


    public void setBranchIdHistory( LongUploadHistory[] branchIdHistory )
    {
        this.branchIdHistory = branchIdHistory;
    }


    public String getSourceRegionId()
    {
        return sourceRegionId;
    }


    public void setSourceRegionId( String sourceRegionId )
    {
        this.sourceRegionId = sourceRegionId;
    }


    public boolean isSourceRegionIdModified()
    {
        return isSourceRegionIdModified;
    }


    public void setSourceRegionIdModified( boolean isSourceRegionIdModified )
    {
        this.isSourceRegionIdModified = isSourceRegionIdModified;
    }


    public StringUploadHistory[] getSourceRegionIdHistory()
    {
        return sourceRegionIdHistory;
    }


    public void setSourceRegionIdHistory( StringUploadHistory[] sourceRegionIdHistory )
    {
        this.sourceRegionIdHistory = sourceRegionIdHistory;
    }


    public long getRegionId()
    {
        return regionId;
    }


    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }


    public boolean isRegionIdModified()
    {
        return isRegionIdModified;
    }


    public void setRegionIdModified( boolean isRegionIdModified )
    {
        this.isRegionIdModified = isRegionIdModified;
    }


    public LongUploadHistory[] getRegionIdHistory()
    {
        return regionIdHistory;
    }


    public void setRegionIdHistory( LongUploadHistory[] regionIdHistory )
    {
        this.regionIdHistory = regionIdHistory;
    }


    public boolean isAgent()
    {
        return isAgent;
    }


    public void setAgent( boolean isAgent )
    {
        this.isAgent = isAgent;
    }


    public boolean isAgentModified()
    {
        return isAgentModified;
    }


    public void setAgentModified( boolean isAgentModified )
    {
        this.isAgentModified = isAgentModified;
    }


    public BooleanUploadHistory[] getIsAgentHistory()
    {
        return isAgentHistory;
    }


    public void setIsAgentHistory( BooleanUploadHistory[] isAgentHistory )
    {
        this.isAgentHistory = isAgentHistory;
    }


    public String getEmailId()
    {
        return emailId;
    }


    public void setEmailId( String emailId )
    {
        this.emailId = emailId;
    }


    public boolean isEmailIdModified()
    {
        return isEmailIdModified;
    }


    public void setEmailIdModified( boolean isEmailIdModified )
    {
        this.isEmailIdModified = isEmailIdModified;
    }


    public StringUploadHistory[] getEmailIdHistory()
    {
        return emailIdHistory;
    }


    public void setEmailIdHistory( StringUploadHistory[] emailIdHistory )
    {
        this.emailIdHistory = emailIdHistory;
    }


    public boolean isBelongsToCompany()
    {
        return belongsToCompany;
    }


    public void setBelongsToCompany( boolean belongsToCompany )
    {
        this.belongsToCompany = belongsToCompany;
    }


    public boolean isBelongsToCompanyModified()
    {
        return isBelongsToCompanyModified;
    }


    public void setBelongsToCompanyModified( boolean isBelongsToCompanyModified )
    {
        this.isBelongsToCompanyModified = isBelongsToCompanyModified;
    }


    public BooleanUploadHistory[] getBelongsToCompanyHistory()
    {
        return belongsToCompanyHistory;
    }


    public void setBelongsToCompanyHistory( BooleanUploadHistory[] belongsToCompanyHistory )
    {
        this.belongsToCompanyHistory = belongsToCompanyHistory;
    }


    public boolean isAssignToCompany()
    {
        return assignToCompany;
    }


    public void setAssignToCompany( boolean assignToCompany )
    {
        this.assignToCompany = assignToCompany;
    }


    public boolean isAssignToCompanyModified()
    {
        return isAssignToCompanyModified;
    }


    public void setAssignToCompanyModified( boolean isAssignToCompanyModified )
    {
        this.isAssignToCompanyModified = isAssignToCompanyModified;
    }


    public BooleanUploadHistory[] getAssignToCompanyHistory()
    {
        return assignToCompanyHistory;
    }


    public void setAssignToCompanyHistory( BooleanUploadHistory[] assignToCompanyHistory )
    {
        this.assignToCompanyHistory = assignToCompanyHistory;
    }


    public String getAssignedBranchName()
    {
        return assignedBranchName;
    }


    public void setAssignedBranchName( String assignedBranchName )
    {
        this.assignedBranchName = assignedBranchName;
    }


    public boolean isAssignedBranchNameModified()
    {
        return isAssignedBranchNameModified;
    }


    public void setAssignedBranchNameModified( boolean isAssignedBranchNameModified )
    {
        this.isAssignedBranchNameModified = isAssignedBranchNameModified;
    }


    public StringUploadHistory[] getAssignedBranchNameHistory()
    {
        return assignedBranchNameHistory;
    }


    public void setAssignedBranchNameHistory( StringUploadHistory[] assignedBranchNameHistory )
    {
        this.assignedBranchNameHistory = assignedBranchNameHistory;
    }


    public String[] getAssignedBranches()
    {
        return assignedBranches;
    }


    public void setAssignedBranches( String[] assignedBranches )
    {
        this.assignedBranches = assignedBranches;
    }


    public boolean isAssignedBranchesModified()
    {
        return isAssignedBranchesModified;
    }


    public void setAssignedBranchesModified( boolean isAssignedBranchesModified )
    {
        this.isAssignedBranchesModified = isAssignedBranchesModified;
    }


    public StringArrayUploadHistory[] getAssignedBranchesHistory()
    {
        return assignedBranchesHistory;
    }


    public void setAssignedBranchesHistory( StringArrayUploadHistory[] assignedBranchesHistory )
    {
        this.assignedBranchesHistory = assignedBranchesHistory;
    }


    public String getAssignedRegionName()
    {
        return assignedRegionName;
    }


    public void setAssignedRegionName( String assignedRegionName )
    {
        this.assignedRegionName = assignedRegionName;
    }


    public boolean isAssignedRegionNameModified()
    {
        return isAssignedRegionNameModified;
    }


    public void setAssignedRegionNameModified( boolean isAssignedRegionNameModified )
    {
        this.isAssignedRegionNameModified = isAssignedRegionNameModified;
    }


    public StringUploadHistory[] getAssignedRegionNameHistory()
    {
        return assignedRegionNameHistory;
    }


    public void setAssignedRegionNameHistory( StringUploadHistory[] assignedRegionNameHistory )
    {
        this.assignedRegionNameHistory = assignedRegionNameHistory;
    }


    public String[] getAssignedRegions()
    {
        return assignedRegions;
    }


    public void setAssignedRegions( String[] assignedRegions )
    {
        this.assignedRegions = assignedRegions;
    }


    public boolean isAssignedRegionsModified()
    {
        return isAssignedRegionsModified;
    }


    public void setAssignedRegionsModified( boolean isAssignedRegionsModified )
    {
        this.isAssignedRegionsModified = isAssignedRegionsModified;
    }


    public StringArrayUploadHistory[] getAssignedRegionsHistory()
    {
        return assignedRegionsHistory;
    }


    public void setAssignedRegionsHistory( StringArrayUploadHistory[] assignedRegionsHistory )
    {
        this.assignedRegionsHistory = assignedRegionsHistory;
    }


    public boolean isBranchAdmin()
    {
        return isBranchAdmin;
    }


    public void setBranchAdmin( boolean isBranchAdmin )
    {
        this.isBranchAdmin = isBranchAdmin;
    }


    public boolean isBranchAdminModified()
    {
        return isBranchAdminModified;
    }


    public void setBranchAdminModified( boolean isBranchAdminModified )
    {
        this.isBranchAdminModified = isBranchAdminModified;
    }


    public BooleanUploadHistory[] getIsBranchAdminHistory()
    {
        return isBranchAdminHistory;
    }


    public void setIsBranchAdminHistory( BooleanUploadHistory[] isBranchAdminHistory )
    {
        this.isBranchAdminHistory = isBranchAdminHistory;
    }


    public String[] getAssignedBrachesAdmin()
    {
        return assignedBrachesAdmin;
    }


    public void setAssignedBrachesAdmin( String[] assignedBrachesAdmin )
    {
        this.assignedBrachesAdmin = assignedBrachesAdmin;
    }


    public boolean isAssignedBrachesAdminModified()
    {
        return isAssignedBrachesAdminModified;
    }


    public void setAssignedBrachesAdminModified( boolean isAssignedBrachesAdminModified )
    {
        this.isAssignedBrachesAdminModified = isAssignedBrachesAdminModified;
    }


    public StringArrayUploadHistory[] getAssignedBrachesAdminHistory()
    {
        return assignedBrachesAdminHistory;
    }


    public void setAssignedBrachesAdminHistory( StringArrayUploadHistory[] assignedBrachesAdminHistory )
    {
        this.assignedBrachesAdminHistory = assignedBrachesAdminHistory;
    }


    public boolean isRegionAdmin()
    {
        return isRegionAdmin;
    }


    public void setRegionAdmin( boolean isRegionAdmin )
    {
        this.isRegionAdmin = isRegionAdmin;
    }


    public boolean isRegionAdminModified()
    {
        return isRegionAdminModified;
    }


    public void setRegionAdminModified( boolean isRegionAdminModified )
    {
        this.isRegionAdminModified = isRegionAdminModified;
    }


    public BooleanUploadHistory[] getIsRegionAdminHistory()
    {
        return isRegionAdminHistory;
    }


    public void setIsRegionAdminHistory( BooleanUploadHistory[] isRegionAdminHistory )
    {
        this.isRegionAdminHistory = isRegionAdminHistory;
    }


    public String[] getAssignedRegionsAdmin()
    {
        return assignedRegionsAdmin;
    }


    public void setAssignedRegionsAdmin( String[] assignedRegionsAdmin )
    {
        this.assignedRegionsAdmin = assignedRegionsAdmin;
    }


    public boolean isAssignedRegionsAdminModified()
    {
        return isAssignedRegionsAdminModified;
    }


    public void setAssignedRegionsAdminModified( boolean isAssignedRegionsAdminModified )
    {
        this.isAssignedRegionsAdminModified = isAssignedRegionsAdminModified;
    }


    public StringArrayUploadHistory[] getAssignedRegionsAdminHistory()
    {
        return assignedRegionsAdminHistory;
    }


    public void setAssignedRegionsAdminHistory( StringArrayUploadHistory[] assignedRegionsAdminHistory )
    {
        this.assignedRegionsAdminHistory = assignedRegionsAdminHistory;
    }


    public String getPhoneNumber()
    {
        return phoneNumber;
    }


    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }


    public boolean isPhoneNumberModified()
    {
        return isPhoneNumberModified;
    }


    public void setPhoneNumberModified( boolean isPhoneNumberModified )
    {
        this.isPhoneNumberModified = isPhoneNumberModified;
    }


    public StringUploadHistory[] getPhoneNumberHistory()
    {
        return phoneNumberHistory;
    }


    public void setPhoneNumberHistory( StringUploadHistory[] phoneNumberHistory )
    {
        this.phoneNumberHistory = phoneNumberHistory;
    }


    public String getWebsiteUrl()
    {
        return websiteUrl;
    }


    public void setWebsiteUrl( String websiteUrl )
    {
        this.websiteUrl = websiteUrl;
    }


    public boolean isWebsiteUrlModified()
    {
        return isWebsiteUrlModified;
    }


    public void setWebsiteUrlModified( boolean isWebsiteUrlModified )
    {
        this.isWebsiteUrlModified = isWebsiteUrlModified;
    }


    public StringUploadHistory[] getWebsiteUrlHistory()
    {
        return websiteUrlHistory;
    }


    public void setWebsiteUrlHistory( StringUploadHistory[] websiteUrlHistory )
    {
        this.websiteUrlHistory = websiteUrlHistory;
    }


    public String getLicense()
    {
        return license;
    }


    public void setLicense( String license )
    {
        this.license = license;
    }


    public boolean isLicenseModified()
    {
        return isLicenseModified;
    }


    public void setLicenseModified( boolean isLicenseModified )
    {
        this.isLicenseModified = isLicenseModified;
    }


    public StringUploadHistory[] getLicenseHistory()
    {
        return licenseHistory;
    }


    public void setLicenseHistory( StringUploadHistory[] licenseHistory )
    {
        this.licenseHistory = licenseHistory;
    }


    public String getLegalDisclaimer()
    {
        return legalDisclaimer;
    }


    public void setLegalDisclaimer( String legalDisclaimer )
    {
        this.legalDisclaimer = legalDisclaimer;
    }


    public boolean isLegalDisclaimerModified()
    {
        return isLegalDisclaimerModified;
    }


    public void setLegalDisclaimerModified( boolean isLegalDisclaimerModified )
    {
        this.isLegalDisclaimerModified = isLegalDisclaimerModified;
    }


    public StringUploadHistory[] getLegalDisclaimerHistory()
    {
        return legalDisclaimerHistory;
    }


    public void setLegalDisclaimerHistory( StringUploadHistory[] legalDisclaimerHistory )
    {
        this.legalDisclaimerHistory = legalDisclaimerHistory;
    }


    public String getAboutMeDescription()
    {
        return aboutMeDescription;
    }


    public void setAboutMeDescription( String aboutMeDescription )
    {
        this.aboutMeDescription = aboutMeDescription;
    }


    public boolean isAboutMeDescriptionModified()
    {
        return isAboutMeDescriptionModified;
    }


    public void setAboutMeDescriptionModified( boolean isAboutMeDescriptionModified )
    {
        this.isAboutMeDescriptionModified = isAboutMeDescriptionModified;
    }


    public StringUploadHistory[] getAboutMeDescriptionHistory()
    {
        return aboutMeDescriptionHistory;
    }


    public void setAboutMeDescriptionHistory( StringUploadHistory[] aboutMeDescriptionHistory )
    {
        this.aboutMeDescriptionHistory = aboutMeDescriptionHistory;
    }


    public String getUserPhotoUrl()
    {
        return userPhotoUrl;
    }


    public void setUserPhotoUrl( String userPhotoUrl )
    {
        this.userPhotoUrl = userPhotoUrl;
    }


    public boolean isUserPhotoUrlModified()
    {
        return isUserPhotoUrlModified;
    }


    public void setUserPhotoUrlModified( boolean isUserPhotoUrlModified )
    {
        this.isUserPhotoUrlModified = isUserPhotoUrlModified;
    }


    public StringUploadHistory[] getUserPhotoUrlHistory()
    {
        return userPhotoUrlHistory;
    }


    public void setUserPhotoUrlHistory( StringUploadHistory[] userPhotoUrlHistory )
    {
        this.userPhotoUrlHistory = userPhotoUrlHistory;
    }


    public boolean isUserAdded()
    {
        return isUserAdded;
    }


    public void setUserAdded( boolean isUserAdded )
    {
        this.isUserAdded = isUserAdded;
    }


    public boolean isUserModified()
    {
        return isUserModified;
    }


    public void setUserModified( boolean isUserModified )
    {
        this.isUserModified = isUserModified;
    }


}
