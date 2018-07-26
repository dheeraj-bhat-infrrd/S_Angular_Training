package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.UnavailableException;

import com.realtech.socialsurvey.core.entities.Achievement;
import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Association;
import com.realtech.socialsurvey.core.entities.BreadCrumb;
import com.realtech.socialsurvey.core.entities.CompanyPositions;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.Licenses;
import com.realtech.socialsurvey.core.entities.LinkedInProfileData;
import com.realtech.socialsurvey.core.entities.LockSettings;
import com.realtech.socialsurvey.core.entities.MiscValues;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfileStage;
import com.realtech.socialsurvey.core.entities.PublicProfileAggregate;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserCompositeEntity;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.ProfileRedirectionException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;


public interface ProfileManagementService
{

    /**
     * Finalize Lock settings in the hierarchy
     * 
     * @param user
     * @param accountType
     * @param settings
     * @throws InvalidInputException
     */
    public LockSettings aggregateParentLockSettings( User user, AccountType accountType, UserSettings settings, long branchId,
        long regionId, int profilesMaster ) throws InvalidInputException, NoRecordsFetchedException;


    /**
     * Finalize profile settings in the hierarchy
     * 
     * @param user
     * @param accountType
     * @param settings
     * @throws InvalidInputException
     */
    public OrganizationUnitSettings aggregateUserProfile( User user, AccountType accountType, UserSettings settings,
        long branchId, long regionId, int profilesMaster ) throws InvalidInputException, NoRecordsFetchedException;


    public String aggregateDisclaimer( OrganizationUnitSettings unitSettings, String entity ) throws InvalidInputException;


    // JIRA SS-97 by RM-06 : BOC
    /**
     * Method to update logo of a company
     * 
     * @param collection
     * @param companySettings
     * @param logo
     * @throws InvalidInputException
     */
    public void updateLogo( String collection, OrganizationUnitSettings companySettings, String logo )
        throws InvalidInputException;


    public void updateProfileImage( String collection, OrganizationUnitSettings companySettings, String logo )
        throws InvalidInputException;


    public void updateDisclaimer( String collection, OrganizationUnitSettings unitSettings, String disclaimer )
        throws InvalidInputException;


    public LockSettings updateLockSettings( String collection, OrganizationUnitSettings unitSettings,
        LockSettings lockSettings ) throws InvalidInputException;


    /**
     * Method to update company contact information
     * 
     * @param collection
     * @param unitSettings
     * @param contactDetailsSettings
     * @return
     * @throws InvalidInputException
     */
    public ContactDetailsSettings updateContactDetails( String collection, OrganizationUnitSettings unitSettings,
        ContactDetailsSettings contactDetailsSettings ) throws InvalidInputException;


    public ContactDetailsSettings updateAgentContactDetails( String collection, AgentSettings agentSettings,
        ContactDetailsSettings contactDetailsSettings ) throws InvalidInputException;


    /**
     * Method to add an association
     * 
     * @param collection
     * @param unitSettings
     * @param associationList
     * @return
     * @throws InvalidInputException
     */
    public List<Association> addAssociations( String collection, OrganizationUnitSettings unitSettings,
        List<Association> associations ) throws InvalidInputException;


    public List<Association> addAgentAssociations( String collection, AgentSettings agentSettings,
        List<Association> associations ) throws InvalidInputException;


    /**
     * Method to add an achievement
     * 
     * @param collection
     * @param unitSettings
     * @param achievements
     * @return
     * @throws InvalidInputException
     */
    public List<Achievement> addAchievements( String collection, OrganizationUnitSettings unitSettings,
        List<Achievement> achievements ) throws InvalidInputException;


    public List<Achievement> addAgentAchievements( String collection, AgentSettings agentSettings,
        List<Achievement> achievements ) throws InvalidInputException;


    /**
     * Method to add licence details
     * 
     * @param collection
     * @param unitSettings
     * @param authorisedIn
     * @param licenseDisclaimer
     * @return
     * @throws InvalidInputException
     */
    public Licenses addLicences( String collection, OrganizationUnitSettings unitSettings, List<String> authorisedIn )
        throws InvalidInputException;


    public Licenses addAgentLicences( String collection, AgentSettings agentSettings, List<String> authorisedIn )
        throws InvalidInputException;


    /**
     * Method to update social media tokens in profile
     * 
     * @param collection
     * @param unitSettings
     * @param mediaTokens
     * @throws InvalidInputException
     */
    public void updateSocialMediaTokens( String collection, OrganizationUnitSettings unitSettings,
        SocialMediaTokens mediaTokens ) throws InvalidInputException;


    /**
     * Method to fetch all users under the specified branch of specified company
     * 
     * @param companyProfileName
     * @param branchProfileName
     * @return
     * @throws InvalidInputException
     */
    public List<AgentSettings> getIndividualsForBranch( String companyProfileName, String branchProfileName )
        throws InvalidInputException, ProfileNotFoundException;


    /**
     * Method to fetch all users under the specified branch of specified company
     * 
     * @param branchId
     * @return
     * @throws InvalidInputException
     */
    public List<AgentSettings> getIndividualsByBranchId( long branchId ) throws InvalidInputException;


    public List<AgentSettings> getIndividualsByBranchId( long branchId, int startIndex, int batchSize )
        throws InvalidInputException;


    /**
     * Gets count of incomplete surveys for all time
     * @param iden
     * @param profileLevel
     * @param startDate
     * @param endDate
     * @return
     * @throws InvalidInputException
     */
    public long getIncompleteSurveyCount(long iden, String profileLevel, Date startDate, Date endDate) throws InvalidInputException;
    
    /**
     * Method to fetch all users for the list of branches specified
     * 
     * @param branchIds
     * @return
     * @throws InvalidInputException
     */
    public List<AgentSettings> getIndividualsByBranchIds( Set<Long> branchIds ) throws InvalidInputException;


    /**
     * Method to fetch all users under the specified region of specified company
     * 
     * @param companyProfileName
     * @param regionProfileName
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public List<AgentSettings> getIndividualsForRegion( String companyProfileName, String regionProfileName )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException;


    /**
     * Method to fetch all users under the specified region
     * 
     * @param branchId
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public List<AgentSettings> getIndividualsByRegionId( long regionId )
        throws InvalidInputException, NoRecordsFetchedException;


    /**
     * Method to fetch all users under the specified list of regions
     * 
     * @param regionIds
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public List<AgentSettings> getIndividualsByRegionIds( Set<Long> regionIds )
        throws InvalidInputException, NoRecordsFetchedException;


    /**
     * Method to fetch all individuals directly linked to a company
     * 
     * @param companyProfileName
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public List<AgentSettings> getIndividualsForCompany( String companyProfileName )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException;


    /**
     * Method to get the region profile based on region and company profile name
     * 
     * @param companyProfileName
     * @param regionProfileName
     * @return
     * @throws InvalidInputException
     */
    public OrganizationUnitSettings getRegionByProfileName( String companyProfileName, String regionProfileName )
        throws ProfileNotFoundException, InvalidInputException;


    /**
     * Method to get the branch profile based on branch and company profile name
     * 
     * @param companyProfileName
     * @param branchProfileName
     * @return
     * @throws InvalidInputException
     */
    public OrganizationUnitSettings getBranchByProfileName( String companyProfileName, String branchProfileName )
        throws ProfileNotFoundException, InvalidInputException;


    /**
     * JIRA SS-117 by RM-02 Method to fetch company profile when profile name is provided
     * 
     * @param collection
     * @param companySettings
     * @param logo
     * @throws InvalidInputException
     */
    public OrganizationUnitSettings getCompanyProfileByProfileName( String profileName ) throws ProfileNotFoundException;


    /**
     * Method to get profile of an individual
     * 
     * @param profileName
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public OrganizationUnitSettings getIndividualByProfileName( String profileName )
        throws ProfileNotFoundException, InvalidInputException, NoRecordsFetchedException;


    public SocialMediaTokens aggregateSocialProfiles( OrganizationUnitSettings unitSettings, String entity )
        throws InvalidInputException, NoRecordsFetchedException;


    public User getUserByProfileName( String profileName, boolean checkStatus )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException;


    /**
     * Gets the user and the agent setting objects based on profile name.
     * @param agentProfileName
     * @param checkStatus
     * @return
     * @throws ProfileNotFoundException
     */
    public UserCompositeEntity getCompositeUserObjectByProfileName( String agentProfileName, boolean checkStatus )
        throws ProfileNotFoundException;


    /**
     * Method to fetch reviews based on the profile level specified, iden is one of
     * agentId/branchId/regionId or companyId based on the profile level
     * 
     * @param iden
     * @param startScore
     * @param limitScore
     * @param startIndex
     * @param numOfRows
     * @param profileLevel
     * @param startDate
     * @param endDate
     * @param surveySources TODO
     * @param order TODO
     * @param addAgentInfo TODO
     * @return
     * @throws InvalidInputException
     */
    public List<SurveyDetails> getReviews( long iden, double startScore, double limitScore, int startIndex, int numOfRows,
        String profileLevel, boolean fetchAbusive, Date startDate, Date endDate, String sortCriteria, List<String> surveySources, String order, boolean addAgentInfo )
        throws InvalidInputException;


    /**
     * Method to get average ratings based on the profile level specified, iden is one of
     * agentId/branchId/regionId or companyId based on the profile level
     * 
     * @param companyId
     * @param profileLevel
     * @return
     * @throws InvalidInputException
     */
    public double getAverageRatings( long companyId, String profileLevel, boolean aggregateAbusive )
        throws InvalidInputException;


    /**
     * Method to get the reviews count for a company within limit of rating score specified
     * 
     * @param companyId
     * @param minScore
     * @param maxScore
     * @return
     */
    public long getReviewsCountForCompany( long companyId, double minScore, double maxScore, boolean fetchAbusive,
        boolean notRecommended );


    /**
     * Method to get reviews count based on the profile level specified, iden is one of
     * agentId/branchId/regionId or companyId based on the profile level within limit of rating
     * score specified
     * 
     * @param iden
     * @param minScore
     * @param maxScore
     * @param profileLevel
     * @return
     * @throws InvalidInputException
     */
    public long getReviewsCount( long iden, double minScore, double maxScore, String profileLevel, boolean fetchAbusive,
        boolean notRecommended ) throws InvalidInputException;


    /**
     * Method to get the list of individuals for branch/region or company as specified ide in one of
     * branchId/regionId/companyId
     * 
     * @param iden
     * @param profileLevel
     * @return
     * @throws MalformedURLException
     * @throws SolrException
     */
    public Collection<UserFromSearch> getProListByProfileLevel( long iden, String profileLevel, int start, int numOfRows )
        throws InvalidInputException, SolrException;


    public void generateVerificationUrl( Map<String, String> urlParams, String applicationUrl, String recipientMailId,
        String recipientName ) throws InvalidInputException, UndeliveredEmailException;


    public String updateEmailVerificationStatus( String urlParamsStr ) throws InvalidInputException, NonFatalException;


    /**
     * Method to fetch reviews based on the profile level specified, iden is one of
     * agentId/branchId/regionId or companyId based on the profile level
     * 
     * @param iden
     * @param startScore
     * @param limitScore
     * @param startIndex
     * @param numOfRows
     * @param profileLevel
     * @return
     * @throws InvalidInputException
     */
    public List<SurveyPreInitiation> getIncompleteSurvey( long iden, double startScore, double limitScore, int startIndex,
        int numOfRows, String profileLevel, Date startDate, Date endDate, boolean realtechAdmin ) throws InvalidInputException;


    /**
     * Method that mails the contact us message to the respective individual,branch,region,company
     * 
     * @param agentProfileName
     * @param message
     * @param senderMailId
     * @param profileType
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     * @throws UndeliveredEmailException
     */
    public void findProfileMailIdAndSendMail( String companyProfileName, String agentProfileName, String message, String senderName, String senderMailId,
        String profileType )
        throws InvalidInputException, NoRecordsFetchedException, UndeliveredEmailException, ProfileNotFoundException;


    public void addSocialPosts( User user, long entityId, String entityType, String postText ) throws InvalidInputException;


    public List<SocialPost> getSocialPosts( long entityId, String entityType, int startIndex, int batchSize )
        throws InvalidInputException;


    public long getPostsCountForUser( String columnName, long columnValue );


    /**
     * Updates linkedin profile data to collection
     * 
     * @param collectionName
     * @param organizationUnitSettings
     * @param linkedInProfileData
     * @throws InvalidInputException
     */
    public void updateLinkedInProfileData( String collectionName, OrganizationUnitSettings organizationUnitSettings,
        LinkedInProfileData linkedInProfileData ) throws InvalidInputException;


    /**
     * Updates the expertise for agent
     * 
     * @param agentSettings
     * @param expertise
     * @throws InvalidInputException
     */
    public void updateAgentExpertise( AgentSettings agentSettings, List<String> expertise ) throws InvalidInputException;


    /**
     * Updates the agents hobbies
     * 
     * @param agentSettings
     * @param hobbies
     * @throws InvalidInputException
     */
    public void updateAgentHobbies( AgentSettings agentSettings, List<String> hobbies ) throws InvalidInputException;


    /**
     * Updates the company positions for an agent
     * 
     * @param agentSettings
     * @param companyPositions
     * @throws InvalidInputException
     */
    public void updateAgentCompanyPositions( AgentSettings agentSettings, List<CompanyPositions> companyPositions )
        throws InvalidInputException;


    public void updateProfileStages( List<ProfileStage> profileStages, OrganizationUnitSettings settings,
        String collectionName );


    public void setAgentProfileUrlForReview( List<SurveyDetails> reviews ) throws InvalidInputException;


    public void updateVertical( String collection, OrganizationUnitSettings companySettings, String vertical )
        throws InvalidInputException;


    public void updateCompanyName( long userId, long companyId, String companyName ) throws InvalidInputException;


    public void updateRegionName( long userId, long regionId, String regionName ) throws InvalidInputException;


    public void updateBranchName( long userId, long branchId, String branchName ) throws InvalidInputException;


    public void updateIndividualName( long userId, long individualId, String individualName ) throws InvalidInputException;


    public void updateCompanyEmail( long companyId, String emailId ) throws InvalidInputException, NonFatalException;


    public void updateIndividualEmail( long userId, String emailId ) throws InvalidInputException;


    public List<AgentRankingReport> getAgentReport( long iden, String profileLevel, Date startDate, Date endDate,
        Object object ) throws InvalidInputException;


    public List<BreadCrumb> getIndividualsBreadCrumb( Long userId )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException;


    public List<BreadCrumb> getRegionsBreadCrumb( OrganizationUnitSettings regionProfile )
        throws InvalidInputException, NoRecordsFetchedException;


    public List<BreadCrumb> getBranchsBreadCrumb( OrganizationUnitSettings branchProfile )
        throws InvalidInputException, NoRecordsFetchedException;


    public List<OrganizationUnitSettings> getCompanyList( String verticalName )
        throws InvalidInputException, ProfileNotFoundException;


    public Map<String, String> findNamesfromProfileName( String profileName );


    public OrganizationUnitSettings aggregateAgentDetails( User user, OrganizationUnitSettings profileSettings,
        LockSettings parentLockSettings ) throws InvalidInputException, NoRecordsFetchedException;


    public void addOrUpdateAgentPositions( List<CompanyPositions> companyPositions, AgentSettings agentSettings );


    public List<AgentSettings> getIndividualsByRegionId( long regionId, int startIndex, int batchSize )
        throws InvalidInputException, NoRecordsFetchedException;


    public Map<SettingsForApplication, OrganizationUnit> getPrimaryHierarchyByEntity( String entityType, long entityId )
        throws InvalidInputException, InvalidSettingsStateException, ProfileNotFoundException;


    public OrganizationUnitSettings getRegionSettingsByProfileName( String companyProfileName, String regionProfileName )
        throws ProfileNotFoundException, InvalidInputException;


    public OrganizationUnitSettings getBranchSettingsByProfileName( String companyProfileName, String branchProfileName )
        throws ProfileNotFoundException, InvalidInputException;


    public OrganizationUnitSettings fillUnitSettings( OrganizationUnitSettings unitSettings, String currentProfileName,
        OrganizationUnitSettings companyUnitSettings, OrganizationUnitSettings regionUnitSettings,
        OrganizationUnitSettings branchUnitSettings, OrganizationUnitSettings agentUnitSettings,
        Map<SettingsForApplication, OrganizationUnit> map, boolean isFetchRequiredDataFromHierarchy );


    public OrganizationUnitSettings getRegionProfileByBranch( OrganizationUnitSettings branchSettings )
        throws ProfileNotFoundException;


    public Map<String, Long> getPrimaryHierarchyByAgentProfile( OrganizationUnitSettings agentSettings )
        throws InvalidInputException, ProfileNotFoundException;


    public OrganizationUnitSettings getIndividualSettingsByProfileName( String agentProfileName )
        throws ProfileNotFoundException, InvalidInputException, NoRecordsFetchedException;


    public void updateEmailsWithLogo( OrganizationUnitSettings unitSettings, String logoUrl, String collectionName );


    public void deleteSocialPost( String postMongoId ) throws InvalidInputException;


    Map<String, Long> getHierarchyDetailsByEntity( String entityType, long entityId )
        throws InvalidInputException, ProfileNotFoundException;


    LockSettings fetchHierarchyLockSettings( long companyId, long branchId, long regionId, String entityType )
        throws NonFatalException;


    Date convertStringToDate( String dateString );


    List<SocialPost> getCumulativeSocialPosts( long entityId, String entityType, int startIndex, int numOfRows,
        String profileLevel, Date startDate, Date endDate ) throws InvalidInputException, NoRecordsFetchedException;


    public List<SurveyDetails> fetchAndSaveZillowData( OrganizationUnitSettings profile, String collection, long companyId,
        boolean fromBatch, boolean fromPublicPage ) throws InvalidInputException, UnavailableException;


    public double getAverageRatings( long companyId, String profileLevel, boolean aggregateAbusive, boolean includeZillow,
        long zillowTotalScore, long zillowReviewCount ) throws InvalidInputException;


    public long getReviewsCount( long iden, double minScore, double maxScore, String profileLevel, boolean fetchAbusive,
        boolean notRecommended, boolean includeZillow, long zillowReviewCount ) throws InvalidInputException;


    public List<AgentSettings> getIndividualsForCompany( long companyId )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException;


    public Map<String, Long> getZillowTotalScoreAndReviewCountForProfileLevel( String profileLevel, long iden );


    public List<SurveyDetails> fillSurveyDetailsFromReviewMap( List<SurveyDetails> surveyDetailsList, String collectionName,
        OrganizationUnitSettings profile, long companyId, boolean fromBatch, boolean fromPublicPage )
        throws InvalidInputException;


    public List<SurveyDetails> getReviewsForReports( long iden, double startScore, double limitScore, int startIndex,
        int numOfRows, String profileLevel, boolean fetchAbusive, Date startDate, Date endDate, String sortCriteria )
        throws InvalidInputException;


    void removeTokensFromProfile( OrganizationUnitSettings profile );


    /**
     * 
     * @param mailIds
     * @param companyId
     * @param entityType
     * @param entitySettings
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    void generateAndSendEmailVerificationRequestLinkToAdmin( List<MiscValues> mailIds, long companyId, String entityType,
        OrganizationUnitSettings entitySettings ) throws InvalidInputException, UndeliveredEmailException;


    public void imageLoader();


    List<SurveyDetails> buildSurveyDetailFromZillowAgentReviewMap( Map<String, Object> map );


    List<SurveyDetails> buildSurveyDetailFromZillowLenderReviewMap( Map<String, Object> map );


    void modifyZillowCallCount( Map<String, Object> map );


    public String processSortCriteria( long companyId, String sortCriteria );
    
    public Integer fetchAndSaveNmlsId( OrganizationUnitSettings profile, String collectionName, long companyId,
        boolean fromBatch, boolean fromPublicPage ) throws InvalidInputException, UnavailableException;


    public void removeProfileImage( String collection, OrganizationUnitSettings companySettings )
        throws InvalidInputException;

    public String buildJsonMessageWithStatus( int status, String message );


    public PublicProfileAggregate buildPublicProfileAggregate( PublicProfileAggregate profileAggregate, boolean isBotRequest )
        throws InvalidInputException, ProfileNotFoundException, InvalidSettingsStateException, NoRecordsFetchedException, ProfileRedirectionException;


    public boolean isAgent( User user ) throws InvalidInputException;


    public String publicProfileRedirection( PublicProfileAggregate profileAggregate ) throws ProfileNotFoundException, InvalidInputException;


    public boolean isCaptchaForContactUsMailProcessed( String remoteAddr, String parameter ) throws InvalidInputException;

    public void updateFacebookPixelId( String entityType, long entityId, String pixelId, UserSettings userSettings )
        throws NonFatalException;


    /**
     * @param emailIdsStr
     * @return
     * @throws NonFatalException
     */
    public List<MiscValues> processEmailIdsInput( String emailIdsStr ) throws NonFatalException;


    /**
     * @param contactDetails
     * @param mailIds
     * @throws InvalidInputException
     * @throws UserAlreadyExistsException
     */
    public void updateEmailIdInContactDetails( ContactDetailsSettings contactDetails, List<MiscValues> mailIds ) throws InvalidInputException, UserAlreadyExistsException;


    /**
     * @param unitSettings
     * @param isWorkEmailLockedByCompany
     * @param companyId
     * @param collectionType
     * @param mailIds
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     * @throws UserAlreadyExistsException
     */
    void updateVerifiedEmail( OrganizationUnitSettings unitSettings, boolean isWorkEmailLockedByCompany, long companyId,
        String collectionType, List<MiscValues> mailIds )
        throws InvalidInputException, UndeliveredEmailException, UserAlreadyExistsException;

    /**
     * 
     * @param source
     * @param imageName
     * @return
     * @throws Exception
     */
    public String copyImage( String source, String imageName ) throws Exception;


    /**
     * 
     * @param collection
     * @param unitSettings
     * @param mediaTokens
     * @throws InvalidInputException
     */
    public void disconnectSelectedSocialMedia( String collection, OrganizationUnitSettings unitSettings, SocialMediaTokens mediaTokens,
        String keyToupdate ) throws InvalidInputException;


    public long getSimpleReviewsCount( long iden, double minScore, double maxScore, String profileLevel, boolean fetchAbusive )
        throws InvalidInputException;


    public List<String> getAvailableSurveySources( String profileLevel, long iden ) throws InvalidInputException;

}