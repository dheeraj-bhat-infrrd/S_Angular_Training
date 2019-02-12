package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table ( name = "company_user_report")
public class CompanyUserReport implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "company_user_table_id")
    private String companyUserTableId;
    
    @Column ( name = "user_id")
    private long userId;
    
    @Column ( name = "company_id")
    private long companyId;
    
    @Column ( name = "first_name")
    private String firstName;
    
    @Column ( name = "last_name")
    private String lastName;
    
    @Column ( name = "email")
    private String email;
    
    @Column ( name = "social_survey_access_level")
    private String socialSurveyAccessLevel;
    
    @Column ( name = "office_branch_assignment")
    private String officeBranchAssignment;
    
    @Column ( name = "region_assignment")
    private String regionAssignment;
    
    @Column ( name = "office_admin")
    private String officeAdmin;
    
    @Column ( name = "region_admin")
    private String regionAdmin;
    
    @Column ( name = "ss_invite_sent_date")
    private Date ssInviteSentDate;
    
    @Column ( name = "email_verified")
    private String emailVerified;
    
    @Column ( name = "last_login_date")
    private Date lastLoginDate;
    
    @Column ( name = "profile_complete")
    private String profileComplete;
    
    @Column ( name = "disclaimer")
    private String disclaimer;
    
    @Column ( name = "address")
    private String address;
    
    @Column ( name = "socially_connected")
    private String sociallyConnected;
    
    @Column ( name = "fb_connection_established_date")
    private Date fbConnectionEstablishedDate;
    
    @Column ( name = "fb_connection_status")
    private String fbConnectionStatus;
    
    @Column ( name = "last_post_date_fb")
    private Date lastPostDateFb;
    
    @Column ( name = "fb_url")
    private String fbUrl;
    
    @Column ( name = "twitter_connection_established_date")
    private Date twitterConnectionEstablishedDate;
    
    @Column ( name = "twitter_connection_status")
    private String twitterConnectionStatus;
    
    @Column ( name = "last_post_date_twitter")
    private Date lastPostDateTwitter;
    
    @Column ( name = "twitter_url")
    private String twitterUrl;
    
    @Column ( name = "linkedin_connection_established_date")
    private Date linkedinConnectionEstablishedDate;
    
    @Column ( name = "linkedin_connection_status")
    private String linkedinConnectionStatus;
    
    @Column ( name = "last_post_date_linkedin")
    private Date lastPostDateLinkedin;
    
    @Column ( name = "linkedin_url")
    private String linkedinUrl;
    
    @Column ( name = "google_plus_url")
    private String googlePlusUrl;
    
    @Column ( name = "zillow_url")
    private String zillowUrl;
    
    @Column ( name = "yelp_url")
    private String yelpUrl;
    
    @Column ( name = "realtor_url")
    private String realtorUrl;
    
    @Column ( name = "gb_url")
    private String gbUrl;
    
    @Column ( name = "lendingtree_url")
    private String lendingtreeUrl;
    
    @Column ( name = "email_verified_date")
    private Date emailVerifiedDate;
    
    @Column ( name = "adoption_completed_date")
    private Date adoptionCompletedDate;
    
    @Column ( name = "last_survey_sent_date")
    private Date lastSurveySentDate;
    
    @Column ( name = "last_survey_posted_date")
    private Date lastSurveyPostedDate;
    
    @Column ( name = "ss_profile")
    private String ssProfile;
    
    @Column ( name = "total_reviews")
    private long totalReviews;
    
    @Column ( name = "ss_reviews")
    private long ssReviews;
    
    @Column ( name = "zillow_reviews")
    private long zillowReviews;
    
    @Column ( name = "abusive_reviews")
    private long abusiveReviews;
    
    @Column ( name = "3rd_party_reviews")
    private long thirdPartyReviews;
    
    @Column ( name = "opted_out")
    private Integer optedOut;

    public Integer getOptedOut()
    {
        return optedOut;
    }

    public void setOptedOut( Integer optedOut )
    {
        this.optedOut = optedOut;
    }

    public String getCompanyUserTableId()
    {
        return companyUserTableId;
    }

    public void setCompanyUserTableId( String companyUserTableId )
    {
        this.companyUserTableId = companyUserTableId;
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

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public String getSocialSurveyAccessLevel()
    {
        return socialSurveyAccessLevel;
    }

    public void setSocialSurveyAccessLevel( String socialSurveyAccessLevel )
    {
        this.socialSurveyAccessLevel = socialSurveyAccessLevel;
    }

    public String getOfficeBranchAssignment()
    {
        return officeBranchAssignment;
    }

    public void setOfficeBranchAssignment( String officeBranchAssignment )
    {
        this.officeBranchAssignment = officeBranchAssignment;
    }

    public String getRegionAssignment()
    {
        return regionAssignment;
    }

    public void setRegionAssignment( String regionAssignment )
    {
        this.regionAssignment = regionAssignment;
    }

    public String getOfficeAdmin()
    {
        return officeAdmin;
    }

    public void setOfficeAdmin( String officeAdmin )
    {
        this.officeAdmin = officeAdmin;
    }

    public String getRegionAdmin()
    {
        return regionAdmin;
    }

    public void setRegionAdmin( String regionAdmin )
    {
        this.regionAdmin = regionAdmin;
    }

    public Date getSsInviteSentDate()
    {
        return ssInviteSentDate;
    }

    public void setSsInviteSentDate( Date ssInviteSentDate )
    {
        this.ssInviteSentDate = ssInviteSentDate;
    }

    public String getEmailVerified()
    {
        return emailVerified;
    }

    public void setEmailVerified( String emailVerified )
    {
        this.emailVerified = emailVerified;
    }

    public Date getLastLoginDate()
    {
        return lastLoginDate;
    }

    public void setLastLoginDate( Date lastLoginDate )
    {
        this.lastLoginDate = lastLoginDate;
    }

    public String getProfileComplete()
    {
        return profileComplete;
    }

    public void setProfileComplete( String profileComplete )
    {
        this.profileComplete = profileComplete;
    }

    public String getDisclaimer()
    {
        return disclaimer;
    }

    public void setDisclaimer( String disclaimer )
    {
        this.disclaimer = disclaimer;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress( String address )
    {
        this.address = address;
    }

    public String getSociallyConnected()
    {
        return sociallyConnected;
    }

    public void setSociallyConnected( String sociallyConnected )
    {
        this.sociallyConnected = sociallyConnected;
    }

    public Date getFbConnectionEstablishedDate()
    {
        return fbConnectionEstablishedDate;
    }

    public void setFbConnectionEstablishedDate( Date fbConnectionEstablishedDate )
    {
        this.fbConnectionEstablishedDate = fbConnectionEstablishedDate;
    }

    public String getFbConnectionStatus()
    {
        return fbConnectionStatus;
    }

    public void setFbConnectionStatus( String fbConnectionStatus )
    {
        this.fbConnectionStatus = fbConnectionStatus;
    }

    public Date getLastPostDateFb()
    {
        return lastPostDateFb;
    }

    public void setLastPostDateFb( Date lastPostDateFb )
    {
        this.lastPostDateFb = lastPostDateFb;
    }

    public String getFbUrl()
    {
        return fbUrl;
    }

    public void setFbUrl( String fbUrl )
    {
        this.fbUrl = fbUrl;
    }
    
    public Date getTwitterConnectionEstablishedDate()
    {
        return twitterConnectionEstablishedDate;
    }

    public void setTwitterConnectionEstablishedDate( Date twitterConnectionEstablishedDate )
    {
        this.twitterConnectionEstablishedDate = twitterConnectionEstablishedDate;
    }

    public String getTwitterConnectionStatus()
    {
        return twitterConnectionStatus;
    }

    public void setTwitterConnectionStatus( String twitterConnectionStatus )
    {
        this.twitterConnectionStatus = twitterConnectionStatus;
    }

    public Date getLastPostDateTwitter()
    {
        return lastPostDateTwitter;
    }

    public void setLastPostDateTwitter( Date lastPostDateTwitter )
    {
        this.lastPostDateTwitter = lastPostDateTwitter;
    }

    public String getTwitterUrl()
    {
        return twitterUrl;
    }

    public void setTwitterUrl( String twitterUrl )
    {
        this.twitterUrl = twitterUrl;
    }

    public Date getLinkedinConnectionEstablishedDate()
    {
        return linkedinConnectionEstablishedDate;
    }

    public void setLinkedinConnectionEstablishedDate( Date linkedinConnectionEstablishedDate )
    {
        this.linkedinConnectionEstablishedDate = linkedinConnectionEstablishedDate;
    }

    public String getLinkedinConnectionStatus()
    {
        return linkedinConnectionStatus;
    }

    public void setLinkedinConnectionStatus( String linkedinConnectionStatus )
    {
        this.linkedinConnectionStatus = linkedinConnectionStatus;
    }

    public Date getLastPostDateLinkedin()
    {
        return lastPostDateLinkedin;
    }

    public void setLastPostDateLinkedin( Date lastPostDateLinkedin )
    {
        this.lastPostDateLinkedin = lastPostDateLinkedin;
    }

    public String getLinkedinUrl()
    {
        return linkedinUrl;
    }

    public void setLinkedinUrl( String linkedinUrl )
    {
        this.linkedinUrl = linkedinUrl;
    }

    public String getGooglePlusUrl()
    {
        return googlePlusUrl;
    }

    public void setGooglePlusUrl( String googlePlusUrl )
    {
        this.googlePlusUrl = googlePlusUrl;
    }

    public String getZillowUrl()
    {
        return zillowUrl;
    }

    public void setZillowUrl( String zillowUrl )
    {
        this.zillowUrl = zillowUrl;
    }

    public String getYelpUrl()
    {
        return yelpUrl;
    }

    public void setYelpUrl( String yelpUrl )
    {
        this.yelpUrl = yelpUrl;
    }

    public String getRealtorUrl()
    {
        return realtorUrl;
    }

    public void setRealtorUrl( String realtorUrl )
    {
        this.realtorUrl = realtorUrl;
    }

    public String getGbUrl()
    {
        return gbUrl;
    }

    public void setGbUrl( String gbUrl )
    {
        this.gbUrl = gbUrl;
    }

    public String getLendingtreeUrl()
    {
        return lendingtreeUrl;
    }

    public void setLendingtreeUrl( String lendingtreeUrl )
    {
        this.lendingtreeUrl = lendingtreeUrl;
    }

    public Date getEmailVerifiedDate()
    {
        return emailVerifiedDate;
    }

    public void setEmailVerifiedDate( Date emailVerifiedDate )
    {
        this.emailVerifiedDate = emailVerifiedDate;
    }

    public Date getAdoptionCompletedDate()
    {
        return adoptionCompletedDate;
    }

    public void setAdoptionCompletedDate( Date adoptionCompletedDate )
    {
        this.adoptionCompletedDate = adoptionCompletedDate;
    }

    public Date getLastSurveySentDate()
    {
        return lastSurveySentDate;
    }

    public void setLastSurveySentDate( Date lastSurveySentDate )
    {
        this.lastSurveySentDate = lastSurveySentDate;
    }

    public Date getLastSurveyPostedDate()
    {
        return lastSurveyPostedDate;
    }

    public void setLastSurveyPostedDate( Date lastSurveyPostedDate )
    {
        this.lastSurveyPostedDate = lastSurveyPostedDate;
    }

    public String getSsProfile()
    {
        return ssProfile;
    }

    public void setSsProfile( String ssProfile )
    {
        this.ssProfile = ssProfile;
    }

    public long getTotalReviews()
    {
        return totalReviews;
    }

    public void setTotalReviews( long totalReviews )
    {
        this.totalReviews = totalReviews;
    }

    public long getSsReviews()
    {
        return ssReviews;
    }

    public void setSsReviews( long ssReviews )
    {
        this.ssReviews = ssReviews;
    }

    public long getZillowReviews()
    {
        return zillowReviews;
    }

    public void setZillowReviews( long zillowReviews )
    {
        this.zillowReviews = zillowReviews;
    }

    public long getAbusiveReviews()
    {
        return abusiveReviews;
    }

    public void setAbusiveReviews( long abusiveReviews )
    {
        this.abusiveReviews = abusiveReviews;
    }

    public long getThirdPartyReviews()
    {
        return thirdPartyReviews;
    }

    public void setThirdPartyReviews( long thirdPartyReviews )
    {
        this.thirdPartyReviews = thirdPartyReviews;
    }

    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }
    
    @Override
    public String toString(){
        return "companyUserReport [companyUserTableId=" + companyUserTableId + ", userId=" + userId + ", companyId="+companyId+", firstName=" + firstName + ", lastName=" + lastName
            + ", email=" + email + ", socialSurveyAccessLevel=" + socialSurveyAccessLevel + ", officeBranchAssignment=" + officeBranchAssignment 
            + ", regionAssignment=" + regionAssignment + ", ssInviteSentDate=" + ", ssInviteSentDate=" + emailVerified + ", emailVerified=" +", "
            + "lastLoginDate=" + lastLoginDate + ", profileComplete=" + profileComplete + ", disclaimer=" + disclaimer + ", address=" +
            address + ", sociallyConnected=" +sociallyConnected + ", fbConnectionEstablishedDate=" +fbConnectionEstablishedDate + ", fbConnectionStatus=" +fbConnectionStatus + ", lastPostDateFb=" +
            lastPostDateFb +", twitterConnectionEstablishedDate=" +twitterConnectionEstablishedDate +", twitterConnectionStatus=" +twitterConnectionStatus 
            +", lastPostDateTwitter=" +lastPostDateTwitter +", linkedinConnectionEstablishedDate=" +linkedinConnectionEstablishedDate+ ", linkedinConnectionStatus=" +linkedinConnectionStatus+ 
            ", lastPostDateLinkedin=" +lastPostDateLinkedin+ ",googlePlusUrl=" +googlePlusUrl+ ", zillowUrl=" +zillowUrl+ 
            ", yelpUrl=" +yelpUrl+ ",realtorUrl=" +realtorUrl+ ", gbUrl=" +gbUrl+ ", lendingtreeUrl=" +lendingtreeUrl+ 
            ", emailVerifiedDate=" +emailVerifiedDate+" , adoptionCompletedDate=" +adoptionCompletedDate+ ", lastSurveySentDate=" +lastSurveySentDate+ ", lastSurveyPostedDate=" +lastSurveyPostedDate+ 
            ", ssProfile=" +ssProfile+ ", totalReviews=" +totalReviews+ ", ssReviews=" +ssReviews+ ", zillowReviews=" +zillowReviews+ ", abusiveReviews=" +abusiveReviews+ ", thirdPartyReviews=" +thirdPartyReviews+"]"; }
    

}
