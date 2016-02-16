package com.realtech.socialsurvey.core.entities;

import java.util.List;


/*
 * The view class for User
 */
public class UserUploadVO
{

    private long userId;
    private String sourceUserId;
    private boolean isSourceUserIdGenerated;
    private String firstName;
    private boolean isFirstNameModified;
    private List<StringUploadHistory> firstNameHistory;
    private String lastName;
    private boolean isLastNameModified;
    private List<StringUploadHistory> lastNameHistory;
    private String title;
    private boolean isTitleModified;
    private List<StringUploadHistory> titleHistory;
    private String sourceBranchId;
    private boolean isSourceBranchIdModified;
    private List<StringUploadHistory> sourceBranchIdHistory;
    private long branchId;
    private boolean isBranchIdModified;
    private List<LongUploadHistory> branchIdHistory;
    private String sourceRegionId;
    private boolean isSourceRegionIdModified;
    private List<StringUploadHistory> sourceRegionIdHistory;
    private long regionId;
    private boolean isRegionIdModified;
    private List<LongUploadHistory> regionIdHistory;
    private boolean isAgent;
    private boolean isAgentModified;
    private List<BooleanUploadHistory> isAgentHistory;
    private String emailId;
    private boolean isEmailIdModified;
    private List<StringUploadHistory> emailIdHistory;
    private boolean belongsToCompany;
    private boolean isBelongsToCompanyModified;
    private List<BooleanUploadHistory> belongsToCompanyHistory;
    private boolean assignToCompany;
    private boolean isAssignToCompanyModified;
    private List<BooleanUploadHistory> assignToCompanyHistory;
    private String assignedBranchName;
    private boolean isAssignedBranchNameModified;
    private List<StringUploadHistory> assignedBranchNameHistory;
    private List<String> assignedBranches;
    private boolean isAssignedBranchesModified;
    private List<StringListUploadHistory> assignedBranchesHistory;
    private String assignedRegionName;
    private boolean isAssignedRegionNameModified;
    private List<StringUploadHistory> assignedRegionNameHistory;
    private List<String> assignedRegions;
    private boolean isAssignedRegionsModified;
    private List<StringListUploadHistory> assignedRegionsHistory;
    private boolean isBranchAdmin;
    private boolean isBranchAdminModified;
    private BooleanUploadHistory[] isBranchAdminHistory;
    private List<String> assignedBranchesAdmin;
    private boolean isAssignedBrachesAdminModified;
    private List<StringListUploadHistory> assignedBrachesAdminHistory;
    private boolean isRegionAdmin;
    private boolean isRegionAdminModified;
    private List<BooleanUploadHistory> isRegionAdminHistory;
    private List<String> assignedRegionsAdmin;
    private boolean isAssignedRegionsAdminModified;
    private List<StringListUploadHistory> assignedRegionsAdminHistory;
    private String phoneNumber;
    private boolean isPhoneNumberModified;
    private List<StringUploadHistory> phoneNumberHistory;
    private String websiteUrl;
    private boolean isWebsiteUrlModified;
    private List<StringUploadHistory> websiteUrlHistory;
    private String license;
    private boolean isLicenseModified;
    private List<StringUploadHistory> licenseHistory;
    private String legalDisclaimer;
    private boolean isLegalDisclaimerModified;
    private List<StringUploadHistory> legalDisclaimerHistory;
    private String aboutMeDescription;
    private boolean isAboutMeDescriptionModified;
    private List<StringUploadHistory> aboutMeDescriptionHistory;
    private String userPhotoUrl;
    private boolean isUserPhotoUrlModified;
    private List<StringUploadHistory> userPhotoUrlHistory;
    private boolean isUserAdded;
    private boolean isUserModified;
    private List<Long> regionIds;
    private List<Long> branchIds;
    private boolean isErrorRecord;
    private boolean isDeletedRecord;
    private boolean isWarningRecord;
    private int rowNum;


    public boolean isWarningRecord()
    {
        return isWarningRecord;
    }


    public void setWarningRecord( boolean isWarningRecord )
    {
        this.isWarningRecord = isWarningRecord;
    }


    public int getRowNum()
    {
        return rowNum;
    }


    public void setRowNum( int rowNum )
    {
        this.rowNum = rowNum;
    }


    public long getUserId()
    {
        return userId;
    }


    public void setUserId( long userId )
    {
        this.userId = userId;
    }


    public boolean isSourceUserIdGenerated()
    {
        return isSourceUserIdGenerated;
    }


    public void setSourceUserIdGenerated( boolean isSourceUserIdGenerated )
    {
        this.isSourceUserIdGenerated = isSourceUserIdGenerated;
    }


    public String getSourceUserId()
    {
        return sourceUserId;
    }


    public void setSourceUserId( String sourceUserId )
    {
        this.sourceUserId = sourceUserId;
    }


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


    public List<StringUploadHistory> getFirstNameHistory()
    {
        return firstNameHistory;
    }


    public void setFirstNameHistory( List<StringUploadHistory> firstNameHistory )
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


    public List<StringUploadHistory> getLastNameHistory()
    {
        return lastNameHistory;
    }


    public void setLastNameHistory( List<StringUploadHistory> lastNameHistory )
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


    public List<StringUploadHistory> getTitleHistory()
    {
        return titleHistory;
    }


    public void setTitleHistory( List<StringUploadHistory> titleHistory )
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


    public List<StringUploadHistory> getSourceBranchIdHistory()
    {
        return sourceBranchIdHistory;
    }


    public void setSourceBranchIdHistory( List<StringUploadHistory> sourceBranchIdHistory )
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


    public List<LongUploadHistory> getBranchIdHistory()
    {
        return branchIdHistory;
    }


    public void setBranchIdHistory( List<LongUploadHistory> branchIdHistory )
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


    public List<StringUploadHistory> getSourceRegionIdHistory()
    {
        return sourceRegionIdHistory;
    }


    public void setSourceRegionIdHistory( List<StringUploadHistory> sourceRegionIdHistory )
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


    public List<LongUploadHistory> getRegionIdHistory()
    {
        return regionIdHistory;
    }


    public void setRegionIdHistory( List<LongUploadHistory> regionIdHistory )
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


    public List<BooleanUploadHistory> getIsAgentHistory()
    {
        return isAgentHistory;
    }


    public void setIsAgentHistory( List<BooleanUploadHistory> isAgentHistory )
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


    public List<StringUploadHistory> getEmailIdHistory()
    {
        return emailIdHistory;
    }


    public void setEmailIdHistory( List<StringUploadHistory> emailIdHistory )
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


    public List<BooleanUploadHistory> getBelongsToCompanyHistory()
    {
        return belongsToCompanyHistory;
    }


    public void setBelongsToCompanyHistory( List<BooleanUploadHistory> belongsToCompanyHistory )
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


    public List<BooleanUploadHistory> getAssignToCompanyHistory()
    {
        return assignToCompanyHistory;
    }


    public void setAssignToCompanyHistory( List<BooleanUploadHistory> assignToCompanyHistory )
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


    public List<StringUploadHistory> getAssignedBranchNameHistory()
    {
        return assignedBranchNameHistory;
    }


    public void setAssignedBranchNameHistory( List<StringUploadHistory> assignedBranchNameHistory )
    {
        this.assignedBranchNameHistory = assignedBranchNameHistory;
    }


    public List<String> getAssignedBranches()
    {
        return assignedBranches;
    }


    public void setAssignedBranches( List<String> assignedBranches )
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


    public List<StringListUploadHistory> getAssignedBranchesHistory()
    {
        return assignedBranchesHistory;
    }


    public void setAssignedBranchesHistory( List<StringListUploadHistory> assignedBranchesHistory )
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


    public List<StringUploadHistory> getAssignedRegionNameHistory()
    {
        return assignedRegionNameHistory;
    }


    public void setAssignedRegionNameHistory( List<StringUploadHistory> assignedRegionNameHistory )
    {
        this.assignedRegionNameHistory = assignedRegionNameHistory;
    }


    public List<String> getAssignedRegions()
    {
        return assignedRegions;
    }


    public void setAssignedRegions( List<String> assignedRegions )
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


    public List<StringListUploadHistory> getAssignedRegionsHistory()
    {
        return assignedRegionsHistory;
    }


    public void setAssignedRegionsHistory( List<StringListUploadHistory> assignedRegionsHistory )
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


    public List<String> getAssignedBranchesAdmin()
    {
        return assignedBranchesAdmin;
    }


    public void setAssignedBranchesAdmin( List<String> assignedBrachesAdmin )
    {
        this.assignedBranchesAdmin = assignedBrachesAdmin;
    }


    public boolean isAssignedBrachesAdminModified()
    {
        return isAssignedBrachesAdminModified;
    }


    public void setAssignedBrachesAdminModified( boolean isAssignedBrachesAdminModified )
    {
        this.isAssignedBrachesAdminModified = isAssignedBrachesAdminModified;
    }


    public List<StringListUploadHistory> getAssignedBrachesAdminHistory()
    {
        return assignedBrachesAdminHistory;
    }


    public void setAssignedBrachesAdminHistory( List<StringListUploadHistory> assignedBrachesAdminHistory )
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


    public List<BooleanUploadHistory> getIsRegionAdminHistory()
    {
        return isRegionAdminHistory;
    }


    public void setIsRegionAdminHistory( List<BooleanUploadHistory> isRegionAdminHistory )
    {
        this.isRegionAdminHistory = isRegionAdminHistory;
    }


    public List<String> getAssignedRegionsAdmin()
    {
        return assignedRegionsAdmin;
    }


    public void setAssignedRegionsAdmin( List<String> assignedRegionsAdmin )
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


    public List<StringListUploadHistory> getAssignedRegionsAdminHistory()
    {
        return assignedRegionsAdminHistory;
    }


    public void setAssignedRegionsAdminHistory( List<StringListUploadHistory> assignedRegionsAdminHistory )
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


    public List<StringUploadHistory> getPhoneNumberHistory()
    {
        return phoneNumberHistory;
    }


    public void setPhoneNumberHistory( List<StringUploadHistory> phoneNumberHistory )
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


    public List<StringUploadHistory> getWebsiteUrlHistory()
    {
        return websiteUrlHistory;
    }


    public void setWebsiteUrlHistory( List<StringUploadHistory> websiteUrlHistory )
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


    public List<StringUploadHistory> getLicenseHistory()
    {
        return licenseHistory;
    }


    public void setLicenseHistory( List<StringUploadHistory> licenseHistory )
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


    public List<StringUploadHistory> getLegalDisclaimerHistory()
    {
        return legalDisclaimerHistory;
    }


    public void setLegalDisclaimerHistory( List<StringUploadHistory> legalDisclaimerHistory )
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


    public List<StringUploadHistory> getAboutMeDescriptionHistory()
    {
        return aboutMeDescriptionHistory;
    }


    public void setAboutMeDescriptionHistory( List<StringUploadHistory> aboutMeDescriptionHistory )
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


    public List<StringUploadHistory> getUserPhotoUrlHistory()
    {
        return userPhotoUrlHistory;
    }


    public void setUserPhotoUrlHistory( List<StringUploadHistory> userPhotoUrlHistory )
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


    /**
     * @return the regionIds
     */
    public List<Long> getRegionIds()
    {
        return regionIds;
    }


    /**
     * @param regionIds the regionIds to set
     */
    public void setRegionIds( List<Long> regionIds )
    {
        this.regionIds = regionIds;
    }


    /**
     * @return the branchIds
     */
    public List<Long> getBranchIds()
    {
        return branchIds;
    }


    /**
     * @param branchIds the branchIds to set
     */
    public void setBranchIds( List<Long> branchIds )
    {
        this.branchIds = branchIds;

    }


    public boolean isErrorRecord()
    {
        return isErrorRecord;
    }


    public void setErrorRecord( boolean isErrorRecord )
    {
        this.isErrorRecord = isErrorRecord;
    }


    public boolean isDeletedRecord()
    {
        return isDeletedRecord;
    }


    public void setDeletedRecord( boolean isDeletedRecord )
    {
        this.isDeletedRecord = isDeletedRecord;
    }


    @Override
    public boolean equals( Object uploadVo )
    {
        UserUploadVO userUploadVO = (UserUploadVO) uploadVo;
        if ( this.sourceUserId != null && !this.sourceUserId.isEmpty() && userUploadVO.sourceUserId != null
            && !userUploadVO.sourceUserId.isEmpty() ) {
            return this.sourceUserId.equals( userUploadVO.sourceUserId );
        } else if ( this.userId != 0 && userUploadVO.userId != 0 ) {
            return ( this.userId == userUploadVO.userId );
        } else {
            return false;
        }
    }


    @Override
    public int hashCode()
    {
        if ( sourceUserId != null && !sourceUserId.isEmpty() ) {
            return sourceUserId.hashCode();
        } else {
            return ( new Long( userId ) ).hashCode();
        }
    }

}
