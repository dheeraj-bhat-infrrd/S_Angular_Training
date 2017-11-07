package com.realtech.socialsurvey.core.entities;

import java.util.List;
import java.util.Set;


/*
 * The view class for User
 */
public class UserUploadVO
{

    // user information ( 16 fields )
    private long userId;
    private String firstName;
    private String lastName;
    private String title;
    private String emailId;
    private String phoneNumber;
    private String websiteUrl;
    private String license;
    private String legalDisclaimer;
    private String aboutMeDescription;
    private String userPhotoUrl;

    private String sourceUserId;


    /* profile related information*/
    private Set<String> assignedBranches;
    private Set<String> assignedRegions;

    private Set<String> assignedBranchesAdmin;
    private Set<String> assignedRegionsAdmin;


    //~~~~~~ meta data on user Upload Value Object : BEGIN ~~~~~~~~~

    // previous values of  user information ( 15 fields )
    private List<StringUploadHistory> firstNameHistory;
    private List<StringUploadHistory> lastNameHistory;
    private List<StringUploadHistory> titleHistory;
    private List<StringUploadHistory> emailIdHistory;
    private List<StringUploadHistory> phoneNumberHistory;
    private List<StringUploadHistory> websiteUrlHistory;
    private List<StringUploadHistory> licenseHistory;
    private List<StringUploadHistory> legalDisclaimerHistory;
    private List<StringUploadHistory> aboutMeDescriptionHistory;
    private List<StringUploadHistory> userPhotoUrlHistory;


    /* profile related information*/
    private List<StringSetUploadHistory> assignedBranchesHistory;
    private List<StringSetUploadHistory> assignedRegionsHistory;

    private List<StringSetUploadHistory> assignedBranchesAdminHistory;
    private List<StringSetUploadHistory> assignedRegionsAdminHistory;


    // field modification flags
    private boolean isEmailModified;

    private boolean isAssignedBranchesModified;
    private boolean isAssignedRegionsModified;

    private boolean isAssignedBranchesAdminModified;
    private boolean isAssignedRegionsAdminModified;


    // user entity specific meta data
    private boolean isUserAdded;
    private boolean isUserModified;
    private boolean isUserVerified;
    private boolean isUserProcessed;


    // object( Upload Value Object ) specific meta data
    private boolean isErrorRecord;
    private boolean isWarningRecord;

    // upload specific meta data
    private boolean sendMail;
    private int rowNum;

    //~~~~~~ meta data on user Upload Value Object : END ~~~~~~~~~


    // Setters and getters : BEGIN

    public long getUserId()
    {
        return userId;
    }


    public Set<String> getAssignedBranches()
    {
        return assignedBranches;
    }


    public void setAssignedBranches( Set<String> assignedBranches )
    {
        this.assignedBranches = assignedBranches;
    }


    public Set<String> getAssignedRegions()
    {
        return assignedRegions;
    }


    public void setAssignedRegions( Set<String> assignedRegions )
    {
        this.assignedRegions = assignedRegions;
    }


    public Set<String> getAssignedBranchesAdmin()
    {
        return assignedBranchesAdmin;
    }


    public void setAssignedBranchesAdmin( Set<String> assignedBranchesAdmin )
    {
        this.assignedBranchesAdmin = assignedBranchesAdmin;
    }


    public Set<String> getAssignedRegionsAdmin()
    {
        return assignedRegionsAdmin;
    }


    public void setAssignedRegionsAdmin( Set<String> assignedRegionsAdmin )
    {
        this.assignedRegionsAdmin = assignedRegionsAdmin;
    }


    public List<StringSetUploadHistory> getAssignedBranchesHistory()
    {
        return assignedBranchesHistory;
    }


    public void setAssignedBranchesHistory( List<StringSetUploadHistory> assignedBranchesHistory )
    {
        this.assignedBranchesHistory = assignedBranchesHistory;
    }


    public List<StringSetUploadHistory> getAssignedRegionsHistory()
    {
        return assignedRegionsHistory;
    }


    public void setAssignedRegionsHistory( List<StringSetUploadHistory> assignedRegionsHistory )
    {
        this.assignedRegionsHistory = assignedRegionsHistory;
    }


    public List<StringSetUploadHistory> getAssignedBranchesAdminHistory()
    {
        return assignedBranchesAdminHistory;
    }


    public void setAssignedBranchesAdminHistory( List<StringSetUploadHistory> assignedBranchesAdminHistory )
    {
        this.assignedBranchesAdminHistory = assignedBranchesAdminHistory;
    }


    public List<StringSetUploadHistory> getAssignedRegionsAdminHistory()
    {
        return assignedRegionsAdminHistory;
    }


    public void setAssignedRegionsAdminHistory( List<StringSetUploadHistory> assignedRegionsAdminHistory )
    {
        this.assignedRegionsAdminHistory = assignedRegionsAdminHistory;
    }


    public boolean isUserProcessed()
    {
        return isUserProcessed;
    }


    public void setUserProcessed( boolean isUserProcessed )
    {
        this.isUserProcessed = isUserProcessed;
    }


    public void setUserId( long userId )
    {
        this.userId = userId;
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


    public String getTitle()
    {
        return title;
    }


    public void setTitle( String title )
    {
        this.title = title;
    }


    public String getEmailId()
    {
        return emailId;
    }


    public void setEmailId( String emailId )
    {
        this.emailId = emailId;
    }


    public String getPhoneNumber()
    {
        return phoneNumber;
    }


    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }


    public String getWebsiteUrl()
    {
        return websiteUrl;
    }


    public void setWebsiteUrl( String websiteUrl )
    {
        this.websiteUrl = websiteUrl;
    }


    public String getLicense()
    {
        return license;
    }


    public void setLicense( String license )
    {
        this.license = license;
    }


    public String getLegalDisclaimer()
    {
        return legalDisclaimer;
    }


    public void setLegalDisclaimer( String legalDisclaimer )
    {
        this.legalDisclaimer = legalDisclaimer;
    }


    public String getAboutMeDescription()
    {
        return aboutMeDescription;
    }


    public void setAboutMeDescription( String aboutMeDescription )
    {
        this.aboutMeDescription = aboutMeDescription;
    }


    public String getUserPhotoUrl()
    {
        return userPhotoUrl;
    }


    public void setUserPhotoUrl( String userPhotoUrl )
    {
        this.userPhotoUrl = userPhotoUrl;
    }


    public String getSourceUserId()
    {
        return sourceUserId;
    }


    public void setSourceUserId( String sourceUserId )
    {
        this.sourceUserId = sourceUserId;
    }


    public List<StringUploadHistory> getFirstNameHistory()
    {
        return firstNameHistory;
    }


    public void setFirstNameHistory( List<StringUploadHistory> firstNameHistory )
    {
        this.firstNameHistory = firstNameHistory;
    }


    public List<StringUploadHistory> getLastNameHistory()
    {
        return lastNameHistory;
    }


    public void setLastNameHistory( List<StringUploadHistory> lastNameHistory )
    {
        this.lastNameHistory = lastNameHistory;
    }


    public List<StringUploadHistory> getTitleHistory()
    {
        return titleHistory;
    }


    public void setTitleHistory( List<StringUploadHistory> titleHistory )
    {
        this.titleHistory = titleHistory;
    }


    public List<StringUploadHistory> getEmailIdHistory()
    {
        return emailIdHistory;
    }


    public void setEmailIdHistory( List<StringUploadHistory> emailIdHistory )
    {
        this.emailIdHistory = emailIdHistory;
    }


    public List<StringUploadHistory> getPhoneNumberHistory()
    {
        return phoneNumberHistory;
    }


    public void setPhoneNumberHistory( List<StringUploadHistory> phoneNumberHistory )
    {
        this.phoneNumberHistory = phoneNumberHistory;
    }


    public List<StringUploadHistory> getWebsiteUrlHistory()
    {
        return websiteUrlHistory;
    }


    public void setWebsiteUrlHistory( List<StringUploadHistory> websiteUrlHistory )
    {
        this.websiteUrlHistory = websiteUrlHistory;
    }


    public List<StringUploadHistory> getLicenseHistory()
    {
        return licenseHistory;
    }


    public void setLicenseHistory( List<StringUploadHistory> licenseHistory )
    {
        this.licenseHistory = licenseHistory;
    }


    public List<StringUploadHistory> getLegalDisclaimerHistory()
    {
        return legalDisclaimerHistory;
    }


    public void setLegalDisclaimerHistory( List<StringUploadHistory> legalDisclaimerHistory )
    {
        this.legalDisclaimerHistory = legalDisclaimerHistory;
    }


    public List<StringUploadHistory> getAboutMeDescriptionHistory()
    {
        return aboutMeDescriptionHistory;
    }


    public void setAboutMeDescriptionHistory( List<StringUploadHistory> aboutMeDescriptionHistory )
    {
        this.aboutMeDescriptionHistory = aboutMeDescriptionHistory;
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


    public boolean isUserVerified()
    {
        return isUserVerified;
    }


    public void setUserVerified( boolean isUserVerified )
    {
        this.isUserVerified = isUserVerified;
    }


    public boolean isErrorRecord()
    {
        return isErrorRecord;
    }


    public void setErrorRecord( boolean isErrorRecord )
    {
        this.isErrorRecord = isErrorRecord;
    }


    public boolean isWarningRecord()
    {
        return isWarningRecord;
    }


    public void setWarningRecord( boolean isWarningRecord )
    {
        this.isWarningRecord = isWarningRecord;
    }


    public boolean isSendMail()
    {
        return sendMail;
    }


    public void setSendMail( boolean sendMail )
    {
        this.sendMail = sendMail;
    }


    public int getRowNum()
    {
        return rowNum;
    }


    public void setRowNum( int rowNum )
    {
        this.rowNum = rowNum;
    }
    // Setters and getters : END


    // priority on source Id and then internal Id
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


    public boolean isEmailModified()
    {
        return isEmailModified;
    }


    public void setEmailModified( boolean isEmailModified )
    {
        this.isEmailModified = isEmailModified;
    }


    public boolean isAssignedBranchesModified()
    {
        return isAssignedBranchesModified;
    }


    public void setAssignedBranchesModified( boolean isAssignedBranchesModified )
    {
        this.isAssignedBranchesModified = isAssignedBranchesModified;
    }


    public boolean isAssignedRegionsModified()
    {
        return isAssignedRegionsModified;
    }


    public void setAssignedRegionsModified( boolean isAssignedRegionsModified )
    {
        this.isAssignedRegionsModified = isAssignedRegionsModified;
    }


    public boolean isAssignedBranchesAdminModified()
    {
        return isAssignedBranchesAdminModified;
    }


    public void setAssignedBranchesAdminModified( boolean isAssignedBranchesAdminModified )
    {
        this.isAssignedBranchesAdminModified = isAssignedBranchesAdminModified;
    }


    public boolean isAssignedRegionsAdminModified()
    {
        return isAssignedRegionsAdminModified;
    }


    public void setAssignedRegionsAdminModified( boolean isAssignedRegionsAdminModified )
    {
        this.isAssignedRegionsAdminModified = isAssignedRegionsAdminModified;
    }

}