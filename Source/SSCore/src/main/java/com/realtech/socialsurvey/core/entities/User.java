package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.realtech.socialsurvey.core.commons.CommonConstants;


/**
 * The persistent class for the users database table.
 */
@Entity
@Table ( name = "USERS")
@NamedQuery ( name = "User.findAll", query = "SELECT u FROM User u")
public class User implements UserDetails, Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "USER_ID")
    private long userId;

    @Column ( name = "CREATED_BY")
    private String createdBy;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "FIRST_NAME")
    private String firstName;

    @Column ( name = "LAST_NAME")
    private String lastName;

    @Column ( name = "EMAIL_ID")
    private String emailId;

    @Column ( name = "IS_ATLEAST_ONE_USERPROFILE_COMPLETE")
    private int isAtleastOneUserprofileComplete;

    @Column ( name = "IS_OWNER")
    private int isOwner;

    @Column ( name = "LAST_LOGIN")
    private Timestamp lastLogin;
    
    @Column ( name = "LAST_USER_LOGIN")
    private Timestamp lastUserLogin;

    @Column ( name = "NUM_OF_LOGINS")
    private long numOfLogins;

    @Column ( name = "SUPER_ADMIN")
    private long superAdmin;

    @Column ( name = "LOGIN_NAME")
    private String loginName;

    @Column ( name = "LOGIN_PASSWORD")
    private String loginPassword;

    @Column ( name = "MODIFIED_BY")
    private String modifiedBy;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column ( name = "SOURCE_USER_ID")
    private int sourceUserId;

    @Column ( name = "SOURCE")
    private String source;

    @Column ( name = "STATUS")
    private int status;

    @Column ( name = "IS_ZILLOW_CONNECTED")
    private int isZillowConnected;

    @Column ( name = "ZILLOW_REVIEW_COUNT")
    private int zillowReviewCount;

    @Column ( name = "ZILLOW_AVERAGE_SCORE")
    private double zillowAverageScore;

    @Column ( name = "REGISTRATION_STAGE")
    private String registrationStage;

    @Column ( name = "FORCE_PASSWORD")
    private int isForcePassword;
    
    @Column ( name = "LAST_INVITATION_SENT_DATE")
    private Timestamp lastInvitationSentDate;
    
    @Column ( name = "ADOPTION_COMPLETION_DATE")
    private Timestamp adoptionCompletionDate;

    @Transient
    private boolean agent;

    @Transient
    private boolean branchAdmin;

    @Transient
    private boolean regionAdmin;

    @Transient
    private boolean companyAdmin;

    @Transient
    private String profileName;

    @Transient
    private String profileUrl;

    @Transient
    private String mappedEmails;

    // bi-directional many-to-one association to UserProfile
    @OneToMany ( mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserProfile> userProfiles;

    // bi-directional many-to-one association to Company
    @ManyToOne
    @JoinColumn ( name = "COMPANY_ID")
    private Company company;

    // bi-directional many-to-one association to RemovedUser
    @OneToMany ( mappedBy = "user")
    private List<RemovedUser> removedUsers;

    // bi-directional many-to-one association to UserEmailMapping
    @OneToMany ( mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserEmailMapping> userEmailMappings;


    @OneToMany ( mappedBy = "user", fetch = FetchType.LAZY)
    private List<SurveyPreInitiation> surveyPreInitiation;

    public Timestamp getLastInvitationSentDate()
    {
        return lastInvitationSentDate;
    }


    public void setLastInvitationSentDate( Timestamp lastInvitationSentDate )
    {
        this.lastInvitationSentDate = lastInvitationSentDate;
    }
    
    
    public long getUserId()
    {
        return this.userId;
    }


    public void setUserId( long userId )
    {
        this.userId = userId;
    }


    public String getCreatedBy()
    {
        return this.createdBy;
    }


    public String getRegistrationStage()
    {
        return registrationStage;
    }


    public void setRegistrationStage( String registrationStage )
    {
        this.registrationStage = registrationStage;
    }


    public int getIsForcePassword()
    {
        return isForcePassword;
    }


    public void setIsForcePassword( int isForcePassword )
    {
        this.isForcePassword = isForcePassword;
    }


    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }


    public Timestamp getCreatedOn()
    {
        return this.createdOn;
    }


    public void setCreatedOn( Timestamp createdOn )
    {
        this.createdOn = createdOn;
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


    public String getEmailId()
    {
        return this.emailId;
    }


    public void setEmailId( String emailId )
    {
        this.emailId = emailId;
    }


    public int getIsOwner()
    {
        return isOwner;
    }


    public void setIsOwner( int isOwner )
    {
        this.isOwner = isOwner;
    }


    public Timestamp getLastLogin()
    {
        return this.lastLogin;
    }


    public void setLastLogin( Timestamp lastLogin )
    {
        this.lastLogin = lastLogin;
    }


    public Timestamp getLastUserLogin()
    {
        return lastUserLogin;
    }


    public void setLastUserLogin( Timestamp lastUserLogin )
    {
        this.lastUserLogin = lastUserLogin;
    }


    public long getNumOfLogins()
    {
        return numOfLogins;
    }


    public void setNumOfLogins( long numOfLogins )
    {
        this.numOfLogins = numOfLogins;
    }


    public long getSuperAdmin()
    {
        return superAdmin;
    }


    public void setSuperAdmin( long superAdmin )
    {
        this.superAdmin = superAdmin;
    }


    public boolean isSuperAdmin()
    {
        if ( this.superAdmin == CommonConstants.STATUS_ACTIVE ) {
            return true;
        }
        return false;
    }


    public String getLoginName()
    {
        return this.loginName;
    }


    public void setLoginName( String loginName )
    {
        this.loginName = loginName;
    }


    public int getIsAtleastOneUserprofileComplete()
    {
        return isAtleastOneUserprofileComplete;
    }


    public void setIsAtleastOneUserprofileComplete( int isAtleastOneUserprofileComplete )
    {
        this.isAtleastOneUserprofileComplete = isAtleastOneUserprofileComplete;
    }


    public String getLoginPassword()
    {
        return this.loginPassword;
    }


    public void setLoginPassword( String loginPassword )
    {
        this.loginPassword = loginPassword;
    }


    public String getModifiedBy()
    {
        return this.modifiedBy;
    }


    public void setModifiedBy( String modifiedBy )
    {
        this.modifiedBy = modifiedBy;
    }


    public Timestamp getModifiedOn()
    {
        return this.modifiedOn;
    }


    public void setModifiedOn( Timestamp modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    public String getSource()
    {
        return this.source;
    }


    public void setSource( String source )
    {
        this.source = source;
    }


    public int getSourceUserId()
    {
        return this.sourceUserId;
    }


    public void setSourceUserId( int sourceUserId )
    {
        this.sourceUserId = sourceUserId;
    }


    public int getStatus()
    {
        return this.status;
    }


    public void setStatus( int status )
    {
        this.status = status;
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


    public List<UserProfile> getUserProfiles()
    {
        return this.userProfiles;
    }


    public void setUserProfiles( List<UserProfile> userProfiles )
    {
        this.userProfiles = userProfiles;
    }


    public UserProfile addUserProfile( UserProfile userProfile )
    {
        getUserProfiles().add( userProfile );
        userProfile.setUser( this );

        return userProfile;
    }


    public UserProfile removeUserProfile( UserProfile userProfile )
    {
        getUserProfiles().remove( userProfile );
        userProfile.setUser( null );

        return userProfile;
    }


    public Company getCompany()
    {
        return this.company;
    }


    public void setCompany( Company company )
    {
        this.company = company;
    }


    public boolean isAgent()
    {
        return agent;
    }


    public void setAgent( boolean agent )
    {
        this.agent = agent;
    }


    public boolean isBranchAdmin()
    {
        return branchAdmin;
    }


    public void setBranchAdmin( boolean branchAdmin )
    {
        this.branchAdmin = branchAdmin;
    }


    public boolean isRegionAdmin()
    {
        return regionAdmin;
    }


    public void setRegionAdmin( boolean regionAdmin )
    {
        this.regionAdmin = regionAdmin;
    }


    public boolean isCompanyAdmin()
    {
        return companyAdmin;
    }


    public void setCompanyAdmin( boolean companyAdmin )
    {
        this.companyAdmin = companyAdmin;
    }


    public List<RemovedUser> getRemovedUsers()
    {
        return this.removedUsers;
    }


    public void setRemovedUsers( List<RemovedUser> removedUsers )
    {
        this.removedUsers = removedUsers;
    }


    public RemovedUser addRemovedUser( RemovedUser removedUser )
    {
        getRemovedUsers().add( removedUser );
        removedUser.setUser( this );

        return removedUser;
    }


    public RemovedUser removeRemovedUser( RemovedUser removedUser )
    {
        getRemovedUsers().remove( removedUser );
        removedUser.setUser( null );

        return removedUser;
    }

    @Transient
    private boolean accountNonExpired = true;
    @Transient
    private boolean accountNonLocked = true;
    @Transient
    private boolean credentialsNonExpired = true;
    @Transient
    private boolean enabled = true;
    @Transient
    private GrantedAuthority[] authorities;


    public User()
    {
        this.authorities = new GrantedAuthority[] { new SimpleGrantedAuthority( "ROLE_USER" ) };
    }


    public void setAuthorities( GrantedAuthority[] authorities )
    {
        this.authorities = authorities.clone();
    }


    public String getUsername()
    {
        return emailId;
    }


    public boolean isAccountNonExpired()
    {
        return accountNonExpired;
    }


    public boolean isAccountNonLocked()
    {
        return accountNonLocked;
    }


    public boolean isCredentialsNonExpired()
    {
        return credentialsNonExpired;
    }


    public boolean isEnabled()
    {
        return enabled;
    }


    /**
     * @return the isZillowConnected
     */
    public int getIsZillowConnected()
    {
        return isZillowConnected;
    }


    /**
     * @param isZillowConnected the isZillowConnected to set
     */
    public void setIsZillowConnected( int isZillowConnected )
    {
        this.isZillowConnected = isZillowConnected;
    }


    /**
     * @return the zillowReviewCount
     */
    public int getZillowReviewCount()
    {
        return zillowReviewCount;
    }


    /**
     * @param zillowReviewCount the zillowReviewCount to set
     */
    public void setZillowReviewCount( int zillowReviewCount )
    {
        this.zillowReviewCount = zillowReviewCount;
    }


    /**
     * @return the zillowAverageScore
     */
    public double getZillowAverageScore()
    {
        return zillowAverageScore;
    }


    /**
     * @param zillowAverageScore the zillowAverageScore to set
     */
    public void setZillowAverageScore( double zillowAverageScore )
    {
        this.zillowAverageScore = zillowAverageScore;
    }


    @Override
    public String toString()
    {
        return "User [id=" + userId + ", firstName=" + firstName + ", email=" + emailId + "]";
    }


    @Override
    public String getPassword()
    {
        return null;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return null;
    }


    /**
     * @return the userEmailMappings
     */
    public List<UserEmailMapping> getUserEmailMappings()
    {
        return userEmailMappings;
    }


    /**
     * @param userEmailMappings the userEmailMappings to set
     */
    public void setUserEmailMappings( List<UserEmailMapping> userEmailMappings )
    {
        this.userEmailMappings = userEmailMappings;
    }


    public String getMappedEmails()
    {
        return mappedEmails;
    }


    public void setMappedEmails( String mappedEmails )
    {
        this.mappedEmails = mappedEmails;
    }


    public Timestamp getAdoptionCompletionDate()
    {
        return adoptionCompletionDate;
    }


    public void setAdoptionCompletionDate( Timestamp adoptionCompletionDate )
    {
        this.adoptionCompletionDate = adoptionCompletionDate;
    }
}