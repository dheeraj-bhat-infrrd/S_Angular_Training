package com.realtech.socialsurvey.core.services.social;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.BranchMediaPostResponseDetails;
import com.realtech.socialsurvey.core.entities.ExternalSurveyTracker;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.RegionMediaPostResponseDetails;
import com.realtech.socialsurvey.core.entities.SocialMediaPostDetails;
import com.realtech.socialsurvey.core.entities.SocialMediaPostResponse;
import com.realtech.socialsurvey.core.entities.SocialMediaPostResponseDetails;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SocialUpdateAction;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.ZillowTempPost;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.vo.SurveyPreInitiationList;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;


/**
 * Interface with methods defined to manage social networks
 */
public interface SocialManagementService
{
    /**
     * Returns the LinkedIn request token for a particular URL
     * 
     * @return
     */
    /*public LinkedInRequestToken getLinkedInRequestToken();
    public LinkedInOAuthService getLinkedInInstance();
    public LinkedInApiClientFactory getLinkedInApiClientFactory();*/

    public RequestToken getTwitterRequestToken( String serverBaseUrl ) throws TwitterException;


    public Twitter getTwitterInstance();


    public Facebook getFacebookInstance( String serverBaseUrl );


    /**
     * Adds the SocialMedia access tokens to mongo
     * 
     * @param user
     * @param accessToken
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public SocialMediaTokens updateSocialMediaTokens( String collection, OrganizationUnitSettings unitSettings,
        SocialMediaTokens mediaTokens ) throws InvalidInputException;


    public SocialMediaTokens updateAgentSocialMediaTokens( AgentSettings agentSettings, SocialMediaTokens mediaTokens )
        throws InvalidInputException;


    /**
     * Posts the survey on facebook
     * @param agentSettings
     * @param message
     * @param serverBaseUrl
     * @param companyId
     * @return
     * @throws InvalidInputException
     * @throws FacebookException
     */
    public boolean updateStatusIntoFacebookPage( OrganizationUnitSettings settings, String message, String serverBaseUrl,
        long companyId, String completeProfileUrl ) throws InvalidInputException, FacebookException;


    /**
     * Post a tweet on behalf of the authorized user
     * @param agentSettings
     * @param message
     * @param companyId
     * @return
     * @throws InvalidInputException
     * @throws TwitterException
     */
    public boolean tweet( OrganizationUnitSettings agentSettings, String message, long companyId )
        throws InvalidInputException, TwitterException;


    public Map<String, List<OrganizationUnitSettings>> getSettingsForBranchesAndRegionsInHierarchy( long agentId )
        throws InvalidInputException;


    public List<OrganizationUnitSettings> getBranchAndRegionSettingsForUser( long userId );


    public boolean updateLinkedin( OrganizationUnitSettings settings, String message, String linkedinProfileUrl,
        String linkedinMessageFeedback, OrganizationUnitSettings companySettings, boolean isZillow, AgentSettings agentSettings,
        SocialMediaPostResponse linkedinPostResponse ) throws NonFatalException;


    public OrganizationUnitSettings disconnectSocialNetwork( String socialMedia, OrganizationUnitSettings unitSettings,
        String collectionName ) throws InvalidInputException;


    public SocialMediaTokens checkOrAddZillowLastUpdated( SocialMediaTokens mediaTokens ) throws InvalidInputException;


    void resetZillowCallCount();


    void updateZillowCallCount();


    int fetchZillowCallCount();


    public boolean postToSocialMedia( String agentName, String agentProfileLink, String custFirstName, String custLastName,
        long agentId, double rating, String surveyId, String feedback, boolean isAbusive, String serverBaseUrl,
        boolean onlyPostToSocialSurvey ) throws NonFatalException;


    /**
     * Method to add entry to social connections history
     *
     * @param entityType
     * @param entityId
     * @param mediaTokens
     * @param socialMedia
     * @param action
     * @throws InvalidInputException
     * @throws ProfileNotFoundException
     */
    void updateSocialConnectionsHistory( String entityType, long entityId, SocialMediaTokens mediaTokens, String socialMedia,
        String action ) throws InvalidInputException, ProfileNotFoundException;


    /**
     * Method to disconnect user from all social connections
     * 
     * @param entityType
     * @param entityId
     * @throws InvalidInputException 
     */
    void disconnectAllSocialConnections( String entityType, long entityId ) throws InvalidInputException;


    void postToFacebookForHierarchy( String facebookMessage, double rating, String serverBaseUrl, int accountMasterId,
        SocialMediaPostDetails socialMediaPostDetails, SocialMediaPostResponseDetails socialMediaPostResponseDetails,
        boolean isZillow ) throws InvalidInputException, NoRecordsFetchedException;


    void postToLinkedInForHierarchy( String linkedinMessage, double rating, String linkedinProfileUrl,
        String linkedinMessageFeedback, int accountMasterId, SocialMediaPostDetails socialMediaPostDetails,
        SocialMediaPostResponseDetails socialMediaPostResponseDetails, OrganizationUnitSettings companySettings,
        boolean isZillow ) throws InvalidInputException, NoRecordsFetchedException;


    void postToTwitterForHierarchy( String twitterMessage, double rating, String serverBaseUrl, int accountMasterId,
        SocialMediaPostDetails socialMediaPostDetails, SocialMediaPostResponseDetails socialMediaPostResponseDetails )
        throws InvalidInputException, NoRecordsFetchedException;


    public Map<String, List<OrganizationUnitSettings>> getSettingsForBranchesRegionsAndCompanyInAgentsHierarchy( long agentId )
        throws InvalidInputException;


    public List<ZillowTempPost> getAllZillowTempPosts();


    public RegionMediaPostResponseDetails getRMPRDFromRMPRDList(
        List<RegionMediaPostResponseDetails> regionMediaPostResponseDetailsList, long regionId );


    public BranchMediaPostResponseDetails getBMPRDFromBMPRDList(
        List<BranchMediaPostResponseDetails> branchMediaPostResponseDetailsList, long branchId );


    ExternalSurveyTracker checkExternalSurveyTrackerExist( String entityColumnName, long entityId, String source,
        String reviewUrl, Timestamp reviewDate );


    public void saveExternalSurveyTracker( String entityColumnName, long entityId, String source, String sourceLink,
        String reviewUrl, double rating, int autoPostStatus, int complaintResolutionStatus, Timestamp reviewDate,
        String postedOn );


    public void removeProcessedZillowTempPosts( List<Long> processedZillowTempPostIds );


    SurveyPreInitiationList getUnmatchedPreInitiatedSurveys( long companyId, int startIndex, int batchSize )
        throws InvalidInputException;


    SurveyPreInitiationList getProcessedPreInitiatedSurveys( long companyId, int startIndex, int batchSize )
        throws InvalidInputException;


    SurveyPreInitiationList getCorruptPreInitiatedSurveys( long companyId, int startIndex, int batchSize )
        throws InvalidInputException;


    void updateAgentIdOfSurveyPreinitiationRecordsForEmail( User user, String emailAddress ) throws InvalidInputException;


    void updateSurveyPreinitiationRecordsAsIgnored( String emailAddress ) throws InvalidInputException;


    public String buildFacebookAutoPostMessage( String customerDisplayName, String agentName, double rating, String feedback,
        String linkUrl, boolean isZillow );


    public String buildLinkedInAutoPostMessage( String customerDisplayName, String agentName, double rating, String feedback,
        String linkUrl, boolean isZillow );


    public String buildTwitterAutoPostMessage( String customerDisplayName, String agentName, double rating, String feedback,
        String linkUrl, boolean isZillow );


    Map<Long, List<SocialUpdateAction>> getSocialConnectionsHistoryForEntities( String entityType, List<Long> entityIds )
        throws InvalidInputException, ProfileNotFoundException;


    public XSSFWorkbook getUserSurveyReportByTabId( int tabId, long companyId )
        throws InvalidInputException, NoRecordsFetchedException;

    public void imcompleteSocialPostReminderSender();
}
// JIRA SS-34 BY RM02 BOC
