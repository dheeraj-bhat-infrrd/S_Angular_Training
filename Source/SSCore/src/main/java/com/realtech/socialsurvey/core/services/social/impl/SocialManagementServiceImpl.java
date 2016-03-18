package com.realtech.socialsurvey.core.services.social.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ExternalSurveyTrackerDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SocialPostDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.ZillowTempPostDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AccountsMaster;
import com.realtech.socialsurvey.core.entities.AgentMediaPostResponseDetails;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchMediaPostDetails;
import com.realtech.socialsurvey.core.entities.BranchMediaPostResponseDetails;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyMediaPostResponseDetails;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ExternalSurveyTracker;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfileStage;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RegionMediaPostDetails;
import com.realtech.socialsurvey.core.entities.RegionMediaPostResponseDetails;
import com.realtech.socialsurvey.core.entities.SocialMediaPostDetails;
import com.realtech.socialsurvey.core.entities.SocialMediaPostResponse;
import com.realtech.socialsurvey.core.entities.SocialMediaPostResponseDetails;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SocialUpdateAction;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.ZillowTempPost;
import com.realtech.socialsurvey.core.enums.ProfileStages;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsSetter;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
import com.realtech.socialsurvey.core.vo.SurveyPreInitiationList;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.PostUpdate;
import facebook4j.auth.AccessToken;


/**
 * JIRA:SS-34 BY RM02 Implementation for User management services
 */
@DependsOn ( "generic")
@Component
public class SocialManagementServiceImpl implements SocialManagementService, InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( SocialManagementServiceImpl.class );

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private EmailFormatHelper emailFormatHelper;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private UserProfileDao userProfileDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SurveyHandler surveyHandler;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private SurveyDetailsDao surveyDetailsDao;

    @Autowired
    private ProfileManagementService profileManagementService;

//    @Autowired
//    private ZillowUpdateService zillowUpdateService;

    @Autowired
    private SurveyPreInitiationDao surveyPreInitiationDao;

    @Autowired
    private SettingsSetter settingsSetter;

    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String applicationAdminEmail;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String applicationAdminName;

    // Facebook
    @Value ( "${FB_CLIENT_ID}")
    private String facebookClientId;
    @Value ( "${FB_CLIENT_SECRET}")
    private String facebookAppSecret;
    @Value ( "${FB_SCOPE}")
    private String facebookScope;
    @Value ( "${FB_REDIRECT_URI}")
    private String facebookRedirectUri;

    // Twitter
    @Value ( "${TWITTER_CONSUMER_KEY}")
    private String twitterConsumerKey;
    @Value ( "${TWITTER_CONSUMER_SECRET}")
    private String twitterConsumerSecret;
    @Value ( "${TWITTER_REDIRECT_URI}")
    private String twitterRedirectUri;

    // Linkedin
    @Value ( "${LINKED_IN_REST_API_URI}")
    private String linkedInRestApiUri;

    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;

    @Value ( "${APPLICATION_LOGO_URL}")
    private String applicationLogoUrl;

    @Value ( "${APPLICATION_LOGO_LINKEDIN_URL}")
    private String applicationLogoUrlForLinkedin;

    @Value ( "${CUSTOM_SOCIALNETWORK_POST_COMPANY_ID}")
    private String customisedSocialNetworkCompanyId;

    @Autowired
    private SocialPostDao socialPostDao;

    @Autowired
    private ZillowTempPostDao zillowTempPostDao;

    @Autowired
    private ExternalSurveyTrackerDao externalSurveyTrackerDao;


    /**
     * Returns the Twitter request token for a particular URL
     * 
     * @return
     * @throws TwitterException
     */
    @Override
    public RequestToken getTwitterRequestToken( String serverBaseUrl ) throws TwitterException
    {
        Twitter twitter = getTwitterInstance();
        RequestToken requestToken = twitter.getOAuthRequestToken( serverBaseUrl + twitterRedirectUri );
        return requestToken;
    }


    @Override
    public Twitter getTwitterInstance()
    {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey( twitterConsumerKey );
        builder.setOAuthConsumerSecret( twitterConsumerSecret );
        Configuration configuration = builder.build();

        return new TwitterFactory( configuration ).getInstance();
    }


    /**
     * Returns the Facebook request token for a particular URL
     * 
     * @return
     * @throws TwitterException
     */
    @Override
    public Facebook getFacebookInstance( String serverBaseUrl )
    {
        facebook4j.conf.ConfigurationBuilder confBuilder = new facebook4j.conf.ConfigurationBuilder();
        confBuilder.setOAuthAppId( facebookClientId );
        confBuilder.setOAuthAppSecret( facebookAppSecret );
        confBuilder.setOAuthCallbackURL( serverBaseUrl + facebookRedirectUri );
        confBuilder.setOAuthPermissions( facebookScope );
        facebook4j.conf.Configuration configuration = confBuilder.build();

        return new FacebookFactory( configuration ).getInstance();
    }


    @Override
    public void afterPropertiesSet() throws Exception
    {
        // TODO Auto-generated method stub
    }


    // Social Media Tokens update
    @Override
    public SocialMediaTokens updateSocialMediaTokens( String collection, OrganizationUnitSettings unitSettings,
        SocialMediaTokens mediaTokens ) throws InvalidInputException
    {
        if ( mediaTokens == null ) {
            throw new InvalidInputException( "Social Tokens passed can not be null" );
        }
        LOG.info( "Updating Social Tokens information" );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_SOCIAL_MEDIA_TOKENS, mediaTokens, unitSettings, collection );
        LOG.info( "Social Tokens updated successfully" );
        return mediaTokens;
    }


    @Override
    public SocialMediaTokens updateAgentSocialMediaTokens( AgentSettings agentSettings, SocialMediaTokens mediaTokens )
        throws InvalidInputException
    {
        if ( mediaTokens == null ) {
            throw new InvalidInputException( "Social Tokens passed can not be null" );
        }
        LOG.info( "Updating Social Tokens information" );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_SOCIAL_MEDIA_TOKENS, mediaTokens, agentSettings );
        LOG.info( "Social Tokens updated successfully" );
        return mediaTokens;
    }


    @Override
    public boolean updateStatusIntoFacebookPage( OrganizationUnitSettings agentSettings, String message, String serverBaseUrl,
        long companyId ) throws InvalidInputException, FacebookException
    {
        if ( agentSettings == null ) {
            throw new InvalidInputException( "AgentSettings can not be null" );
        }
        LOG.info( "Updating Social Tokens information" );
        boolean facebookNotSetup = true;
        Facebook facebook = getFacebookInstance( serverBaseUrl );
        if ( agentSettings != null ) {
            if ( agentSettings.getSocialMediaTokens() != null ) {
                if ( agentSettings.getSocialMediaTokens().getFacebookToken() != null
                    && agentSettings.getSocialMediaTokens().getFacebookToken().getFacebookAccessToken() != null ) {
                    if ( agentSettings.getSocialMediaTokens().getFacebookToken().getFacebookAccessTokenToPost() != null )
                        facebook.setOAuthAccessToken( new AccessToken( agentSettings.getSocialMediaTokens().getFacebookToken()
                            .getFacebookAccessTokenToPost(), null ) );
                    else
                        facebook.setOAuthAccessToken( new AccessToken( agentSettings.getSocialMediaTokens().getFacebookToken()
                            .getFacebookAccessToken(), null ) );
                    try {
                        facebookNotSetup = false;
                        String completeProfileUrl = agentSettings.getCompleteProfileUrl();
                        if ( completeProfileUrl == null || completeProfileUrl.isEmpty() ) {
                            completeProfileUrl = surveyHandler.getApplicationBaseUrl()
                                + CommonConstants.AGENT_PROFILE_FIXED_URL + agentSettings.getProfileUrl();
                        }
                        // Updating customised data
                        PostUpdate postUpdate = new PostUpdate( message );
                        postUpdate.setCaption( completeProfileUrl );
                        try {
                            postUpdate.setLink( new URL( completeProfileUrl ) );
                        } catch ( MalformedURLException e1 ) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        // TODO: Hard coded bad code: DELETE: BEGIN
                        if ( companyId == Long.parseLong( customisedSocialNetworkCompanyId ) ) {
                            try {
                                postUpdate
                                    .setPicture( new URL( "https://don7n2as2v6aa.cloudfront.net/remax-facebook-image.png" ) );
                            } catch ( MalformedURLException e ) {
                                LOG.warn( "Could not set the URL" );
                            }
                        }
                        // TODO: Hard coded bad code: DELETE: END
                        facebook.postFeed( postUpdate );
                        // facebook.postStatusMessage(message);
                    } catch ( RuntimeException e ) {
                        LOG.error( "Runtime exception caught while trying to post on facebook. Nested exception is ", e );
                    }

                }
            }
        }
        LOG.info( "Status updated successfully" );
        return facebookNotSetup;
    }


    @Override
    public boolean tweet( OrganizationUnitSettings agentSettings, String message, long companyId )
        throws InvalidInputException, TwitterException
    {
        if ( agentSettings == null ) {
            throw new InvalidInputException( "AgentSettings can not be null" );
        }
        LOG.info( "Getting Social Tokens information" );
        boolean twitterNotSetup = true;
        if ( agentSettings != null ) {
            if ( agentSettings.getSocialMediaTokens() != null ) {
                if ( agentSettings.getSocialMediaTokens().getTwitterToken() != null
                    && agentSettings.getSocialMediaTokens().getTwitterToken().getTwitterAccessTokenSecret() != null ) {
                    Twitter twitter = getTwitterInstance();
                    twitter.setOAuthAccessToken( new twitter4j.auth.AccessToken( agentSettings.getSocialMediaTokens()
                        .getTwitterToken().getTwitterAccessToken(), agentSettings.getSocialMediaTokens().getTwitterToken()
                        .getTwitterAccessTokenSecret() ) );
                    try {
                        twitterNotSetup = false;
                        // TODO: Hard coded bad code: DELETE: BEGIN
                        if ( companyId == Long.parseLong( customisedSocialNetworkCompanyId ) ) {
                            message = message.replace( "@SocialSurveyMe", "#REMAXagentreviews" );
                        }
                        // TODO: Hard coded bad code: DELETE: END
                        StatusUpdate statusUpdate = new StatusUpdate( message );
                        // TODO: Hard coded bad code: DELETE: BEGIN
                        if ( companyId == Long.parseLong( customisedSocialNetworkCompanyId ) ) {
                            try {
                                statusUpdate.setMedia( "Picture", new URL(
                                    "https://don7n2as2v6aa.cloudfront.net/remax-twitter-image.jpg?imgmax=800" ).openStream() );
                            } catch ( MalformedURLException e ) {
                                LOG.error( "error while posting image on twitter: " + e.getMessage(), e );
                            } catch ( IOException e ) {
                                LOG.error( "error while posting image on twitter: " + e.getMessage(), e );
                            }
                        }
                        twitter.updateStatus( statusUpdate );
                        // twitter.updateStatus(message);
                    } catch ( RuntimeException e ) {
                        LOG.error( "Runtime exception caught while trying to tweet. Nested exception is ", e );
                    }
                }
            }
        }
        LOG.info( "Social Tokens updated successfully" );
        return twitterNotSetup;
    }


    @Override
    public boolean updateLinkedin( OrganizationUnitSettings agentSettings, String message, String linkedinProfileUrl,
        String linkedinMessageFeedback, OrganizationUnitSettings companySettings, boolean isZillow ) throws NonFatalException
    {
        if ( agentSettings == null ) {
            throw new InvalidInputException( "AgentSettings can not be null" );
        }
        boolean linkedinNotSetup = true;
        LOG.info( "updateLinkedin() started." );
        if ( agentSettings != null ) {
            if ( agentSettings.getSocialMediaTokens() != null ) {
                if ( agentSettings.getSocialMediaTokens().getLinkedInToken() != null
                    && agentSettings.getSocialMediaTokens().getLinkedInToken().getLinkedInAccessToken() != null ) {
                    linkedinNotSetup = false;
                    String linkedInPost = new StringBuilder( linkedInRestApiUri )
                        .substring( 0, linkedInRestApiUri.length() - 1 );
                    linkedInPost += "/shares?oauth2_access_token="
                        + agentSettings.getSocialMediaTokens().getLinkedInToken().getLinkedInAccessToken();
                    linkedInPost += "&format=json";
                    try {
                        HttpClient client = HttpClientBuilder.create().build();
                        HttpPost post = new HttpPost( linkedInPost );

                        // add header
                        post.setHeader( "Content-Type", "application/json" );
                        // String a = "{\"comment\": \"\",\"content\": {" + "\"title\": \"\"," + "\"description\": \"" + message
                        //    + "-" + linkedinMessageFeedback + "\"," + "\"submitted-url\": \"" + linkedinProfileUrl + "\",  "
                        //    + "\"submitted-image-url\": \"" + applicationLogoUrlForLinkedin + "\"},"
                        //    + "\"visibility\": {\"code\": \"anyone\" }}";
                        // StringEntity entity = new StringEntity( a );

                        ContactDetailsSettings agentContactDetailsSettings = agentSettings.getContact_details();
                        String agentTitle = agentContactDetailsSettings.getTitle();
                        String companyName = companySettings.getContact_details().getName();
                        String location = agentContactDetailsSettings.getLocation();
                        String industry = agentContactDetailsSettings.getIndustry();
                        if ( industry == null || industry.isEmpty() ) {
                            industry = companySettings.getContact_details().getIndustry();
                        }

                        String title = WordUtils.capitalize( agentContactDetailsSettings.getName() ) + ", "
                            + ( agentTitle != null && !agentTitle.isEmpty() ? agentTitle + ", " : "" ) + companyName + ", "
                            + ( location != null && !location.isEmpty() ? location + ", " : "" ) + industry
                            + " Professional Reviews | " + ( isZillow ? "Zillow" : "SocialSurvey.me" );

                        String description = "Reviews for " + agentContactDetailsSettings.getName() + ". "
                            + agentContactDetailsSettings.getFirstName() + " is a  " + industry + " professional in "
                            + ( location != null && !location.isEmpty() ? location : "" ) + ". "
                            + agentContactDetailsSettings.getFirstName() + " is the "
                            + ( agentTitle != null && !agentTitle.isEmpty() ? agentTitle : "" ) + " of " + companyName + ".";

                        String imageUrl = applicationLogoUrlForLinkedin;

                        if ( agentSettings.getProfileImageUrl() != null && !agentSettings.getProfileImageUrl().isEmpty() ) {
                            imageUrl = agentSettings.getProfileImageUrl();
                        } else if ( companySettings.getLogo() != null && !companySettings.getLogo().isEmpty() ) {
                            imageUrl = companySettings.getLogo();
                        }

                        String profileUrl = surveyHandler.getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL
                            + agentSettings.getProfileUrl();
                        message = StringEscapeUtils.escapeXml( message );

                        String linkedPostJSON = "{\"comment\": \"" + message + "\",\"content\": {" + "\"title\": \"" + title
                            + "\"," + "\"description\": \"" + description + "\","
                            + "\"submitted-url\": \"" + profileUrl + "\",  " + "\"submitted-image-url\": \"" + imageUrl
                            + "\"}," + "\"visibility\": {\"code\": \"anyone\" }}";
                        StringEntity entity = new StringEntity( linkedPostJSON );
                        post.setEntity( entity );
                        try {
                            HttpResponse response = client.execute( post );
                            LOG.info( "Server response while posting on linkedin : " + response.toString() );
                        } catch ( RuntimeException e ) {
                            LOG.error(
                                "Runtime exception caught while trying to add an update on linkedin. Nested exception is ", e );
                        }
                    } catch ( IOException e ) {
                        throw new NonFatalException( "IOException caught while posting on Linkedin. Nested exception is ", e );
                    }
                }
            }
        }
        LOG.info( "updateLinkedin() finished" );
        return linkedinNotSetup;
    }


    @Override
    @Transactional
    public Map<String, List<OrganizationUnitSettings>> getSettingsForBranchesAndRegionsInHierarchy( long agentId )
        throws InvalidInputException
    {
        LOG.info( "Method to get settings of branches and regions current agent belongs to, getSettingsForBranchesAndRegionsInHierarchy() started." );
        User user = userDao.findById( User.class, agentId );
        Map<String, List<OrganizationUnitSettings>> map = new HashMap<String, List<OrganizationUnitSettings>>();
        List<OrganizationUnitSettings> companySettings = new ArrayList<>();
        List<OrganizationUnitSettings> branchSettings = new ArrayList<>();
        List<OrganizationUnitSettings> regionSettings = new ArrayList<>();
        Set<Long> branchIds = new HashSet<>();
        Set<Long> regionIds = new HashSet<>();
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.USER_COLUMN, user );
        queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
            userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) );
        List<UserProfile> userProfiles = userProfileDao.findByKeyValue( UserProfile.class, queries );
        for ( UserProfile userProfile : userProfiles ) {
            long branchId = userProfile.getBranchId();
            if ( branchId > 0 ) {
                Branch branch = userManagementService.getBranchById( branchId );
                if ( branch != null ) {
                    if ( branch.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_NO ) {
                        LOG.debug( "This agent belongs to branch " );
                        branchIds.add( userProfile.getBranchId() );
                    } else {
                        long regionId = userProfile.getRegionId();
                        if ( regionId > 0 ) {
                            Region region = userManagementService.getRegionById( regionId );
                            if ( region != null ) {
                                LOG.info( "This agent belongs to region " );
                                if ( region.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_NO ) {
                                    regionIds.add( regionId );
                                }
                            }
                        }
                    }
                }
            }
        }

        for ( Long branchId : branchIds ) {
            branchSettings.add( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( branchId,
                CommonConstants.BRANCH_SETTINGS_COLLECTION ) );
        }

        for ( Long regionId : regionIds ) {
            regionSettings.add( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( regionId,
                CommonConstants.REGION_SETTINGS_COLLECTION ) );
        }


        companySettings.add( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( user.getCompany().getCompanyId(),
            CommonConstants.COMPANY_SETTINGS_COLLECTION ) );


        map.put( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings );
        map.put( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings );
        map.put( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings );
        LOG.info( "Method to get settings of branches and regions current agent belongs to, getSettingsForBranchesAndRegionsInHierarchy() finished." );

        return map;
    }


    /**
     * Method to get Company, all Region and Branch Settings for an agent
     * */
    @Override
    @Transactional
    public Map<String, List<OrganizationUnitSettings>> getSettingsForBranchesRegionsAndCompanyInAgentsHierarchy( long agentId )
        throws InvalidInputException
    {
        LOG.info( "Method to get settings of branches and regions current agent belongs to, getSettingsForBranchesAndRegionsInHierarchy() started." );
        User user = userDao.findById( User.class, agentId );
        Map<String, List<OrganizationUnitSettings>> map = new HashMap<String, List<OrganizationUnitSettings>>();
        List<OrganizationUnitSettings> companySettings = new ArrayList<>();
        List<OrganizationUnitSettings> branchSettings = new ArrayList<>();
        List<OrganizationUnitSettings> regionSettings = new ArrayList<>();
        Set<Long> branchIds = new HashSet<>();
        Set<Long> regionIds = new HashSet<>();
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.USER_COLUMN, user );
        queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
            userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) );
        List<UserProfile> userProfiles = userProfileDao.findByKeyValue( UserProfile.class, queries );
        for ( UserProfile userProfile : userProfiles ) {
            long branchId = userProfile.getBranchId();
            if ( branchId > 0 ) {
                Branch branch = userManagementService.getBranchById( branchId );
                if ( branch != null ) {
                    if ( branch.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_NO ) {
                        LOG.debug( "This agent belongs to branch id : " + userProfile.getBranchId() );
                        branchIds.add( userProfile.getBranchId() );
                    }
                }
            }
            long regionId = userProfile.getRegionId();
            if ( regionId > 0 ) {
                Region region = userManagementService.getRegionById( regionId );
                if ( region != null ) {
                    if ( region.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_NO ) {
                        LOG.info( "This agent belongs to region id : " + regionId );
                        regionIds.add( regionId );
                    }
                }
            }
        }

        for ( Long branchId : branchIds ) {
            branchSettings.add( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( branchId,
                CommonConstants.BRANCH_SETTINGS_COLLECTION ) );
        }

        for ( Long regionId : regionIds ) {
            regionSettings.add( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( regionId,
                CommonConstants.REGION_SETTINGS_COLLECTION ) );
        }


        companySettings.add( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( user.getCompany().getCompanyId(),
            CommonConstants.COMPANY_SETTINGS_COLLECTION ) );


        map.put( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings );
        map.put( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings );
        map.put( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings );
        LOG.info( "Method to get settings of branches and regions current agent belongs to, getSettingsForBranchesAndRegionsInHierarchy() finished." );

        return map;
    }


    /*
     * Method to get settings of branches, regions and company current user is admin of.
     */
    @Override
    @Transactional
    public List<OrganizationUnitSettings> getBranchAndRegionSettingsForUser( long userId )
    {
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.USER_COLUMN, userDao.findById( User.class, userId ) );
        List<UserProfile> userProfiles = userProfileDao.findByKeyValue( UserProfile.class, queries );
        List<OrganizationUnitSettings> settings = new ArrayList<>();
        for ( UserProfile profile : userProfiles ) {
            switch ( profile.getProfilesMaster().getProfileId() ) {
                case CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID:
                    settings.add( organizationUnitSettingsDao.fetchAgentSettingsById( userId ) );
                    break;
                case CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID:
                    settings.add( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( profile.getBranchId(),
                        CommonConstants.BRANCH_SETTINGS_COLLECTION ) );
                    break;

                case CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID:
                    settings.add( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( profile.getRegionId(),
                        CommonConstants.REGION_NAME_COLUMN ) );
                    break;

                case CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID:
                    settings.add( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( profile.getCompany()
                        .getCompanyId(), CommonConstants.REGION_NAME_COLUMN ) );
                    break;
            }
        }
        return settings;

    }


    /**
     * Method to unset Social Media Tokens from a unit setting
     * 
     * @param unitSettings
     * @param collectionName
     * @throws InvalidInputException
     */
    void removeSocialMediaTokens( OrganizationUnitSettings unitSettings, String collectionName ) throws InvalidInputException
    {
        LOG.info( "Method removeSocialMediaTokens started." );
        String keyToUpdate = CommonConstants.SOCIAL_MEDIA_TOKEN_MONGO_KEY;
        if ( unitSettings == null ) {
            throw new InvalidInputException( "Invalid unit settings" );
        }
        if ( collectionName == null || collectionName.isEmpty() ) {
            throw new InvalidInputException( "collection name is empty" );
        }
        organizationUnitSettingsDao.removeKeyInOrganizationSettings( unitSettings, keyToUpdate, collectionName );
    }


    @Override
    public OrganizationUnitSettings disconnectSocialNetwork( String socialMedia, OrganizationUnitSettings unitSettings,
        String collectionName ) throws InvalidInputException
    {
        LOG.debug( "Method disconnectSocialNetwork() called" );

        String keyToUpdate = null;
        boolean ignore = false;
        ProfileStage profileStage = new ProfileStage();
        switch ( socialMedia ) {
            case CommonConstants.FACEBOOK_SOCIAL_SITE:
                profileStage.setOrder( ProfileStages.FACEBOOK_PRF.getOrder() );
                profileStage.setProfileStageKey( ProfileStages.FACEBOOK_PRF.name() );
                keyToUpdate = MongoOrganizationUnitSettingDaoImpl.KEY_FACEBOOK_SOCIAL_MEDIA_TOKEN;
                break;

            case CommonConstants.TWITTER_SOCIAL_SITE:
                profileStage.setOrder( ProfileStages.TWITTER_PRF.getOrder() );
                profileStage.setProfileStageKey( ProfileStages.TWITTER_PRF.name() );
                keyToUpdate = MongoOrganizationUnitSettingDaoImpl.KEY_TWITTER_SOCIAL_MEDIA_TOKEN;
                break;

            case CommonConstants.GOOGLE_SOCIAL_SITE:
                profileStage.setOrder( ProfileStages.GOOGLE_PRF.getOrder() );
                profileStage.setProfileStageKey( ProfileStages.GOOGLE_PRF.name() );
                keyToUpdate = MongoOrganizationUnitSettingDaoImpl.KEY_GOOGLE_SOCIAL_MEDIA_TOKEN;
                break;

            case CommonConstants.LINKEDIN_SOCIAL_SITE:
                profileStage.setOrder( ProfileStages.LINKEDIN_PRF.getOrder() );
                profileStage.setProfileStageKey( ProfileStages.LINKEDIN_PRF.name() );
                keyToUpdate = MongoOrganizationUnitSettingDaoImpl.KEY_LINKEDIN_SOCIAL_MEDIA_TOKEN;
                break;

            case CommonConstants.ZILLOW_SOCIAL_SITE:
                profileStage.setOrder( ProfileStages.ZILLOW_PRF.getOrder() );
                profileStage.setProfileStageKey( ProfileStages.ZILLOW_PRF.name() );
                keyToUpdate = MongoOrganizationUnitSettingDaoImpl.KEY_ZILLOW_SOCIAL_MEDIA_TOKEN;
                break;

            case CommonConstants.YELP_SOCIAL_SITE:
                profileStage.setOrder( ProfileStages.YELP_PRF.getOrder() );
                profileStage.setProfileStageKey( ProfileStages.YELP_PRF.name() );
                keyToUpdate = MongoOrganizationUnitSettingDaoImpl.KEY_YELP_SOCIAL_MEDIA_TOKEN;
                break;

            case CommonConstants.LENDINGTREE_SOCIAL_SITE:
                ignore = true;
                keyToUpdate = MongoOrganizationUnitSettingDaoImpl.KEY_LENDINGTREE_SOCIAL_MEDIA_TOKEN;
                break;

            case CommonConstants.REALTOR_SOCIAL_SITE:
                ignore = true;
                keyToUpdate = MongoOrganizationUnitSettingDaoImpl.KEY_REALTOR_SOCIAL_MEDIA_TOKEN;
                break;

            default:
                throw new InvalidInputException( "Invalid social media token entered" );
        }

        OrganizationUnitSettings organizationUnitSettings = organizationUnitSettingsDao.removeKeyInOrganizationSettings(
            unitSettings, keyToUpdate, collectionName );

        if ( !ignore ) {
            profileStage.setStatus( CommonConstants.STATUS_ACTIVE );
            List<ProfileStage> profileStageList = unitSettings.getProfileStages();
            if ( !profileStageList.contains( profileStage ) ) {
                profileStageList.add( profileStage );
            } else {
                profileStageList.add( profileStageList.indexOf( profileStage ), profileStage );
            }
            profileManagementService.updateProfileStages( profileStageList, unitSettings, collectionName );
        }

        LOG.debug( "Method disconnectSocialNetwork() finished" );

        return organizationUnitSettings;
    }


    @Override
    public SocialMediaTokens checkOrAddZillowLastUpdated( SocialMediaTokens mediaTokens ) throws InvalidInputException
    {
        if ( mediaTokens == null ) {
            throw new InvalidInputException( "Invalid media token" );
        }
        if ( mediaTokens.getZillowToken() == null ) {
            throw new InvalidInputException( "zillow token not found" );
        }

        try {
            mediaTokens.getZillowToken().getLastUpdated();
        } catch ( Exception e ) {
            mediaTokens.getZillowToken().setLastUpdated( "" );
        }
        return mediaTokens;
    }


    @Override
    public void resetZillowCallCount()
    {
        LOG.info( "Method resetZillowCallCount() started" );
        surveyDetailsDao.resetZillowCallCount();
        LOG.info( "Method resetZillowCallCount() finished" );
    }


    @Override
    public int fetchZillowCallCount()
    {
        LOG.info( "Method fetchZillowCallCount() started" );
        int count = surveyDetailsDao.fetchZillowCallCount();
        LOG.info( "Method fetchZillowCallCount() finished" );
        return count;
    }


    @Override
    public void updateZillowCallCount()
    {
        LOG.info( "Method updateZillowCallCount() started" );
        surveyDetailsDao.updateZillowCallCount();
        LOG.info( "Method updateZillowCallCount() finished" );
    }


    /**
     * 
     * @param regionMediaPostResponseDetailsList
     * @param regionId
     * @return
     */
    @Override
    public RegionMediaPostResponseDetails getRMPRDFromRMPRDList(List<RegionMediaPostResponseDetails> regionMediaPostResponseDetailsList , long regionId)
    {
        LOG.debug( "Inside method getRMPRDFromRMPRDList()" );
        if ( regionMediaPostResponseDetailsList == null || regionMediaPostResponseDetailsList.isEmpty() ) {
            return null;
        }

        for ( RegionMediaPostResponseDetails regionMediaPostResponseDetails : regionMediaPostResponseDetailsList ) {
            if ( regionMediaPostResponseDetails.getRegionId() == regionId )
                return regionMediaPostResponseDetails;
        }
        return null;
    }


    /**
     * 
     * @param branchMediaPostResponseDetailsList
     * @param branchId
     * @return
     */
    @Override
    public BranchMediaPostResponseDetails getBMPRDFromBMPRDList(List<BranchMediaPostResponseDetails> branchMediaPostResponseDetailsList , long branchId)
    {
        LOG.debug( "Inside method getBMPRDFromBMPRDList()" );
        if ( branchMediaPostResponseDetailsList == null || branchMediaPostResponseDetailsList.isEmpty() ) {
            return null;
        }

        for ( BranchMediaPostResponseDetails branchMediaPostResponseDetails : branchMediaPostResponseDetailsList ) {
            if ( branchMediaPostResponseDetails.getBranchId() == branchId )
                return branchMediaPostResponseDetails;
        }
        return null;
    }


    /**
     * 
     * @param facebookMessage
     * @param rating
     * @param serverBaseUrl
     * @param accountMasterId
     * @param socialMediaPostDetails
     * @param socialMediaPostResponseDetails
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    @Override
    public void postToFacebookForHierarchy( String facebookMessage, double rating, String serverBaseUrl, int accountMasterId,
        SocialMediaPostDetails socialMediaPostDetails, SocialMediaPostResponseDetails socialMediaPostResponseDetails )
        throws InvalidInputException, NoRecordsFetchedException
    {

        LOG.debug( "Method postToFacebookForHierarchy() started" );
        if ( socialMediaPostDetails == null ) {
            throw new InvalidInputException( "passed parameter socialMediaPostDetails is null" );
        }
        if ( socialMediaPostResponseDetails == null ) {
            throw new InvalidInputException( "passed parameter socialMediaPostResponseDetails is null" );
        }

        long companyId = socialMediaPostDetails.getCompanyMediaPostDetails().getCompanyId();

        AgentMediaPostResponseDetails agentMediaPostResponseDetails = socialMediaPostResponseDetails
            .getAgentMediaPostResponseDetails();
        CompanyMediaPostResponseDetails companyMediaPostResponseDetails = socialMediaPostResponseDetails
            .getCompanyMediaPostResponseDetails();
        List<RegionMediaPostResponseDetails> regionMediaPostResponseDetailsList = socialMediaPostResponseDetails
            .getRegionMediaPostResponseDetailsList();
        List<BranchMediaPostResponseDetails> branchMediaPostResponseDetailsList = socialMediaPostResponseDetails
            .getBranchMediaPostResponseDetailsList();

        //Post for agent
        if ( socialMediaPostDetails.getAgentMediaPostDetails() != null ) {
            AgentSettings agentSettings = userManagementService.getUserSettings( socialMediaPostDetails
                .getAgentMediaPostDetails().getAgentId() );
            if ( agentSettings != null ) {
                try {
                    if ( surveyHandler.canPostOnSocialMedia( agentSettings, rating ) ) {
                        List<String> agentSocialList = socialMediaPostDetails.getAgentMediaPostDetails().getSharedOn();
                        if ( !updateStatusIntoFacebookPage( agentSettings, facebookMessage, serverBaseUrl, companyId ) ) {
                            if ( !agentSocialList.contains( CommonConstants.FACEBOOK_SOCIAL_SITE ) )
                                agentSocialList.add( CommonConstants.FACEBOOK_SOCIAL_SITE );

                            SocialMediaPostResponse facebookPostResponse = new SocialMediaPostResponse();
                            facebookPostResponse.setAccessToken( agentSettings.getSocialMediaTokens().getFacebookToken()
                                .getFacebookAccessTokenToPost() );
                            facebookPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                            facebookPostResponse.setResponseMessage( "Ok" );
                            if ( agentMediaPostResponseDetails.getFacebookPostResponseList() == null )
                                agentMediaPostResponseDetails
                                    .setFacebookPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                            agentMediaPostResponseDetails.getFacebookPostResponseList().add( facebookPostResponse );

                        }
                    }
                } catch ( FacebookException e ) {
                    LOG.error(
                        "FacebookException caught in postToSocialMedia() while trying to post to facebook. Nested excption is ",
                        e );
                    SocialMediaPostResponse facebookPostResponse = new SocialMediaPostResponse();
                    facebookPostResponse.setAccessToken( agentSettings.getSocialMediaTokens().getFacebookToken()
                        .getFacebookAccessTokenToPost() );
                    facebookPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                    facebookPostResponse.setResponseMessage( e.getMessage() );
                    if ( agentMediaPostResponseDetails.getFacebookPostResponseList() == null )
                        agentMediaPostResponseDetails.setFacebookPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                    agentMediaPostResponseDetails.getFacebookPostResponseList().add( facebookPostResponse );
                    reportBug( "Facebook", agentSettings.getProfileName(), e );
                }
            }
        }


        if ( accountMasterId != CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {

            //Post for company
            if ( socialMediaPostDetails.getCompanyMediaPostDetails() != null ) {
                OrganizationUnitSettings companySetting = organizationManagementService
                    .getCompanySettings( socialMediaPostDetails.getCompanyMediaPostDetails().getCompanyId() );
                if ( companySetting != null ) {
                    try {
                        if ( surveyHandler.canPostOnSocialMedia( companySetting, rating ) ) {
                            if ( !updateStatusIntoFacebookPage( companySetting, facebookMessage, serverBaseUrl,
                                companySetting.getIden() ) ) {
                                List<String> companySocialList = socialMediaPostDetails.getCompanyMediaPostDetails()
                                    .getSharedOn();
                                if ( !companySocialList.contains( CommonConstants.FACEBOOK_SOCIAL_SITE ) )
                                    companySocialList.add( CommonConstants.FACEBOOK_SOCIAL_SITE );

                                SocialMediaPostResponse facebookPostResponse = new SocialMediaPostResponse();
                                facebookPostResponse.setAccessToken( companySetting.getSocialMediaTokens().getFacebookToken()
                                    .getFacebookAccessTokenToPost() );
                                facebookPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                                facebookPostResponse.setResponseMessage( "Ok" );
                                if ( companyMediaPostResponseDetails.getFacebookPostResponseList() == null )
                                    companyMediaPostResponseDetails
                                        .setFacebookPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                                companyMediaPostResponseDetails.getFacebookPostResponseList().add( facebookPostResponse );

                            }
                        }
                    } catch ( FacebookException e ) {
                        LOG.error(
                            "FacebookException caught in postToSocialMedia() while trying to post to facebook for company. Nested excption is ",
                            e );
                        SocialMediaPostResponse facebookPostResponse = new SocialMediaPostResponse();
                        facebookPostResponse.setAccessToken( companySetting.getSocialMediaTokens().getFacebookToken()
                            .getFacebookAccessTokenToPost() );
                        facebookPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                        facebookPostResponse.setResponseMessage( e.getMessage() );
                        if ( companyMediaPostResponseDetails.getFacebookPostResponseList() == null )
                            companyMediaPostResponseDetails
                                .setFacebookPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                        companyMediaPostResponseDetails.getFacebookPostResponseList().add( facebookPostResponse );
                        reportBug( "Facebook", companySetting.getProfileName(), e );
                    }
                }
            }


            //Post for regions
            for ( RegionMediaPostDetails regionMediaPostDetails : socialMediaPostDetails.getRegionMediaPostDetailsList() ) {
                if ( regionMediaPostDetails != null ) {

                    OrganizationUnitSettings setting = organizationManagementService.getRegionSettings( regionMediaPostDetails
                        .getRegionId() );
                    if ( setting != null ) {
                        RegionMediaPostResponseDetails regionMediaPostResponseDetails = getRMPRDFromRMPRDList(
                            regionMediaPostResponseDetailsList, regionMediaPostDetails.getRegionId() );

                        try {
                            if ( surveyHandler.canPostOnSocialMedia( setting, rating ) ) {
                                if ( !updateStatusIntoFacebookPage( setting, facebookMessage, serverBaseUrl, companyId ) ) {
                                    List<String> regionSocialList = regionMediaPostDetails.getSharedOn();
                                    if ( !regionSocialList.contains( CommonConstants.FACEBOOK_SOCIAL_SITE ) )
                                        regionSocialList.add( CommonConstants.FACEBOOK_SOCIAL_SITE );
                                    regionMediaPostDetails.setSharedOn( regionSocialList );

                                    SocialMediaPostResponse facebookPostResponse = new SocialMediaPostResponse();
                                    facebookPostResponse.setAccessToken( setting.getSocialMediaTokens().getFacebookToken()
                                        .getFacebookAccessTokenToPost() );
                                    facebookPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                                    facebookPostResponse.setResponseMessage( "Ok" );
                                    if ( regionMediaPostResponseDetails.getFacebookPostResponseList() == null )
                                        regionMediaPostResponseDetails
                                            .setFacebookPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                                    regionMediaPostResponseDetails.getFacebookPostResponseList().add( facebookPostResponse );
                                }
                            }
                        } catch ( FacebookException e ) {
                            LOG.error(
                                "FacebookException caught in postToSocialMedia() while trying to post to facebook. Nested excption is ",
                                e );
                            SocialMediaPostResponse facebookPostResponse = new SocialMediaPostResponse();
                            facebookPostResponse.setAccessToken( setting.getSocialMediaTokens().getFacebookToken()
                                .getFacebookAccessTokenToPost() );
                            facebookPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                            facebookPostResponse.setResponseMessage( e.getMessage() );
                            if ( regionMediaPostResponseDetails.getFacebookPostResponseList() == null )
                                regionMediaPostResponseDetails
                                    .setFacebookPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                            regionMediaPostResponseDetails.getFacebookPostResponseList().add( facebookPostResponse );
                            reportBug( "Facebook", setting.getProfileName(), e );
                        }
                    }

                }

            }

            //Post for branches
            for ( BranchMediaPostDetails branchMediaPostDetails : socialMediaPostDetails.getBranchMediaPostDetailsList() ) {
                if ( branchMediaPostDetails != null ) {
                    OrganizationUnitSettings setting = organizationManagementService
                        .getBranchSettingsDefault( branchMediaPostDetails.getBranchId() );
                    BranchMediaPostResponseDetails branchMediaPostResponseDetails = getBMPRDFromBMPRDList(
                        branchMediaPostResponseDetailsList, branchMediaPostDetails.getBranchId() );

                    if ( setting != null ) {
                        try {
                            if ( surveyHandler.canPostOnSocialMedia( setting, rating ) ) {
                                if ( !updateStatusIntoFacebookPage( setting, facebookMessage, serverBaseUrl, companyId ) ) {
                                    List<String> branchSocialList = branchMediaPostDetails.getSharedOn();
                                    if ( !branchSocialList.contains( CommonConstants.FACEBOOK_SOCIAL_SITE ) )
                                        branchSocialList.add( CommonConstants.FACEBOOK_SOCIAL_SITE );
                                    branchMediaPostDetails.setSharedOn( branchSocialList );

                                    SocialMediaPostResponse facebookPostResponse = new SocialMediaPostResponse();
                                    facebookPostResponse.setAccessToken( setting.getSocialMediaTokens().getFacebookToken()
                                        .getFacebookAccessTokenToPost() );
                                    facebookPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                                    facebookPostResponse.setResponseMessage( "Ok" );
                                    if ( branchMediaPostResponseDetails.getFacebookPostResponseList() == null )
                                        branchMediaPostResponseDetails
                                            .setFacebookPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                                    branchMediaPostResponseDetails.getFacebookPostResponseList().add( facebookPostResponse );
                                }
                            }

                        } catch ( FacebookException e ) {
                            LOG.error(
                                "FacebookException caught in postToSocialMedia() while trying to post to facebook. Nested excption is ",
                                e );
                            SocialMediaPostResponse facebookPostResponse = new SocialMediaPostResponse();
                            facebookPostResponse.setAccessToken( setting.getSocialMediaTokens().getFacebookToken()
                                .getFacebookAccessTokenToPost() );
                            facebookPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                            facebookPostResponse.setResponseMessage( e.getMessage() );
                            if ( branchMediaPostResponseDetails.getFacebookPostResponseList() == null )
                                branchMediaPostResponseDetails
                                    .setFacebookPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                            branchMediaPostResponseDetails.getFacebookPostResponseList().add( facebookPostResponse );
                            reportBug( "Facebook", setting.getProfileName(), e );
                        }
                    }
                }

            }
        }

        LOG.debug( "Method postToFacebookForHierarchy() ended" );
    }


    /**
     *  
     * @param linkedinMessage
     * @param rating
     * @param linkedinProfileUrl
     * @param linkedinMessageFeedback
     * @param accountMasterId
     * @param socialMediaPostDetails
     * @param socialMediaPostResponseDetails
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    @Override
    public void postToLinkedInForHierarchy( String linkedinMessage, double rating, String linkedinProfileUrl,
        String linkedinMessageFeedback, int accountMasterId, SocialMediaPostDetails socialMediaPostDetails,
        SocialMediaPostResponseDetails socialMediaPostResponseDetails, OrganizationUnitSettings companySettings, boolean isZillow ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method postToLinkedInForHierarchy() started" );
        if ( socialMediaPostDetails == null ) {
            throw new InvalidInputException( "passed parameter socialMediaPostDetails is null" );
        }
        if ( socialMediaPostResponseDetails == null ) {
            throw new InvalidInputException( "passed parameter socialMediaPostResponseDetails is null" );
        }

        AgentMediaPostResponseDetails agentMediaPostResponseDetails = socialMediaPostResponseDetails
            .getAgentMediaPostResponseDetails();
        CompanyMediaPostResponseDetails companyMediaPostResponseDetails = socialMediaPostResponseDetails
            .getCompanyMediaPostResponseDetails();
        List<RegionMediaPostResponseDetails> regionMediaPostResponseDetailsList = socialMediaPostResponseDetails
            .getRegionMediaPostResponseDetailsList();
        List<BranchMediaPostResponseDetails> branchMediaPostResponseDetailsList = socialMediaPostResponseDetails
            .getBranchMediaPostResponseDetailsList();


        //Post for agent
        if ( socialMediaPostDetails.getAgentMediaPostDetails() != null ) {
            AgentSettings agentSettings = userManagementService.getUserSettings( socialMediaPostDetails
                .getAgentMediaPostDetails().getAgentId() );
            if ( agentSettings != null ) {
                try {
                    if ( surveyHandler.canPostOnSocialMedia( agentSettings, rating ) ) {
                        if ( !updateLinkedin( agentSettings, linkedinMessage, linkedinProfileUrl, linkedinMessageFeedback,
                            companySettings, isZillow ) ) {
                            List<String> agentSocialList = socialMediaPostDetails.getAgentMediaPostDetails().getSharedOn();
                            if ( !agentSocialList.contains( CommonConstants.LINKEDIN_SOCIAL_SITE ) )
                                agentSocialList.add( CommonConstants.LINKEDIN_SOCIAL_SITE );

                            SocialMediaPostResponse linkedinPostResponse = new SocialMediaPostResponse();
                            linkedinPostResponse.setAccessToken( agentSettings.getSocialMediaTokens().getLinkedInToken()
                                .getLinkedInAccessToken() );
                            linkedinPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                            linkedinPostResponse.setResponseMessage( "Ok" );
                            if ( agentMediaPostResponseDetails.getLinkedinPostResponseList() == null )
                                agentMediaPostResponseDetails
                                    .setLinkedinPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                            agentMediaPostResponseDetails.getLinkedinPostResponseList().add( linkedinPostResponse );
                        }
                    }
                } catch ( Exception e ) {
                    SocialMediaPostResponse linkedinPostResponse = new SocialMediaPostResponse();
                    linkedinPostResponse.setAccessToken( agentSettings.getSocialMediaTokens().getLinkedInToken()
                        .getLinkedInAccessToken() );
                    linkedinPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                    linkedinPostResponse.setResponseMessage( e.getMessage() );
                    if ( agentMediaPostResponseDetails.getLinkedinPostResponseList() == null )
                        agentMediaPostResponseDetails.setLinkedinPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                    agentMediaPostResponseDetails.getLinkedinPostResponseList().add( linkedinPostResponse );

                    reportBug( "Linkedin", agentSettings.getProfileName(), e );
                }
            }
        }

        if ( accountMasterId != CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {

            //Post for company
            if ( socialMediaPostDetails.getCompanyMediaPostDetails() != null ) {
                OrganizationUnitSettings companySetting = organizationManagementService
                    .getCompanySettings( socialMediaPostDetails.getCompanyMediaPostDetails().getCompanyId() );
                if ( companySetting != null ) {
                    try {
                        if ( surveyHandler.canPostOnSocialMedia( companySetting, rating ) ) {
                            if ( !updateLinkedin( companySetting, linkedinMessage, linkedinProfileUrl, linkedinMessageFeedback,
                                companySetting, isZillow ) ) {
                                List<String> companySocialList = socialMediaPostDetails.getCompanyMediaPostDetails()
                                    .getSharedOn();
                                if ( !companySocialList.contains( CommonConstants.LINKEDIN_SOCIAL_SITE ) )
                                    companySocialList.add( CommonConstants.LINKEDIN_SOCIAL_SITE );

                                SocialMediaPostResponse linkedinPostResponse = new SocialMediaPostResponse();
                                linkedinPostResponse.setAccessToken( companySetting.getSocialMediaTokens().getLinkedInToken()
                                    .getLinkedInAccessToken() );
                                linkedinPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                                linkedinPostResponse.setResponseMessage( "Ok" );
                                if ( companyMediaPostResponseDetails.getLinkedinPostResponseList() == null )
                                    companyMediaPostResponseDetails
                                        .setLinkedinPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                                companyMediaPostResponseDetails.getLinkedinPostResponseList().add( linkedinPostResponse );
                            }
                        }
                    } catch ( Exception e ) {

                        SocialMediaPostResponse linkedinPostResponse = new SocialMediaPostResponse();
                        linkedinPostResponse.setAccessToken( companySetting.getSocialMediaTokens().getLinkedInToken()
                            .getLinkedInAccessToken() );
                        linkedinPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                        linkedinPostResponse.setResponseMessage( e.getMessage() );
                        if ( companyMediaPostResponseDetails.getLinkedinPostResponseList() == null )
                            companyMediaPostResponseDetails
                                .setLinkedinPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                        companyMediaPostResponseDetails.getLinkedinPostResponseList().add( linkedinPostResponse );

                        reportBug( "Linkedin", companySetting.getProfileName(), e );
                    }
                }
            }


            //Post for regions
            for ( RegionMediaPostDetails regionMediaPostDetails : socialMediaPostDetails.getRegionMediaPostDetailsList() ) {
                if ( regionMediaPostDetails != null ) {
                    OrganizationUnitSettings setting = organizationManagementService.getRegionSettings( regionMediaPostDetails
                        .getRegionId() );

                    if ( setting != null ) {
                        RegionMediaPostResponseDetails regionMediaPostResponseDetails = getRMPRDFromRMPRDList(
                            regionMediaPostResponseDetailsList, regionMediaPostDetails.getRegionId() );

                        try {
                            if ( surveyHandler.canPostOnSocialMedia( setting, rating ) ) {
                                if ( !updateLinkedin( setting, linkedinMessage, linkedinProfileUrl, linkedinMessageFeedback,
                                    companySettings, isZillow ) ) {
                                    List<String> regionSocialList = regionMediaPostDetails.getSharedOn();
                                    if ( !regionSocialList.contains( CommonConstants.LINKEDIN_SOCIAL_SITE ) )
                                        regionSocialList.add( CommonConstants.LINKEDIN_SOCIAL_SITE );
                                    regionMediaPostDetails.setSharedOn( regionSocialList );

                                    SocialMediaPostResponse linkedinPostResponse = new SocialMediaPostResponse();
                                    linkedinPostResponse.setAccessToken( setting.getSocialMediaTokens().getLinkedInToken()
                                        .getLinkedInAccessToken() );
                                    linkedinPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                                    linkedinPostResponse.setResponseMessage( "Ok" );
                                    if ( regionMediaPostResponseDetails.getLinkedinPostResponseList() == null )
                                        regionMediaPostResponseDetails
                                            .setLinkedinPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                                    regionMediaPostResponseDetails.getLinkedinPostResponseList().add( linkedinPostResponse );
                                }
                            }
                        } catch ( Exception e ) {
                            SocialMediaPostResponse linkedinPostResponse = new SocialMediaPostResponse();
                            linkedinPostResponse.setAccessToken( setting.getSocialMediaTokens().getLinkedInToken()
                                .getLinkedInAccessToken() );
                            linkedinPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                            linkedinPostResponse.setResponseMessage( e.getMessage() );
                            if ( regionMediaPostResponseDetails.getLinkedinPostResponseList() == null )
                                regionMediaPostResponseDetails
                                    .setLinkedinPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                            regionMediaPostResponseDetails.getLinkedinPostResponseList().add( linkedinPostResponse );

                            reportBug( "Linkedin", setting.getProfileName(), e );
                        }
                    }
                }
            }

            //post for branches
            for ( BranchMediaPostDetails branchMediaPostDetails : socialMediaPostDetails.getBranchMediaPostDetailsList() ) {
                if ( branchMediaPostDetails != null ) {
                    OrganizationUnitSettings setting = organizationManagementService
                        .getBranchSettingsDefault( branchMediaPostDetails.getBranchId() );
                    if ( setting != null ) {
                        BranchMediaPostResponseDetails branchMediaPostResponseDetails = getBMPRDFromBMPRDList(
                            branchMediaPostResponseDetailsList, branchMediaPostDetails.getBranchId() );

                        try {
                            if ( surveyHandler.canPostOnSocialMedia( setting, rating ) ) {
                                if ( !updateLinkedin( setting, linkedinMessage, linkedinProfileUrl, linkedinMessageFeedback,
                                    companySettings, isZillow ) ) {
                                    List<String> branchSocialList = branchMediaPostDetails.getSharedOn();
                                    if ( !branchSocialList.contains( CommonConstants.LINKEDIN_SOCIAL_SITE ) )
                                        branchSocialList.add( CommonConstants.LINKEDIN_SOCIAL_SITE );
                                    branchMediaPostDetails.setSharedOn( branchSocialList );

                                    SocialMediaPostResponse linkedinPostResponse = new SocialMediaPostResponse();
                                    linkedinPostResponse.setAccessToken( setting.getSocialMediaTokens().getLinkedInToken()
                                        .getLinkedInAccessToken() );
                                    linkedinPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                                    linkedinPostResponse.setResponseMessage( "Ok" );
                                    if ( branchMediaPostResponseDetails.getLinkedinPostResponseList() == null )
                                        branchMediaPostResponseDetails
                                            .setLinkedinPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                                    branchMediaPostResponseDetails.getLinkedinPostResponseList().add( linkedinPostResponse );
                                }
                            }

                        } catch ( Exception e ) {
                            SocialMediaPostResponse linkedinPostResponse = new SocialMediaPostResponse();
                            linkedinPostResponse.setAccessToken( setting.getSocialMediaTokens().getLinkedInToken()
                                .getLinkedInAccessToken() );
                            linkedinPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                            linkedinPostResponse.setResponseMessage( e.getMessage() );
                            if ( branchMediaPostResponseDetails.getLinkedinPostResponseList() == null )
                                branchMediaPostResponseDetails
                                    .setLinkedinPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                            branchMediaPostResponseDetails.getLinkedinPostResponseList().add( linkedinPostResponse );

                            reportBug( "Linkedin", setting.getProfileName(), e );
                        }
                    }
                }
            }
        }

        LOG.debug( "Method postToLinkedInForHierarchy() ended" );
    }


    /**
     * 
     * @param twitterMessage
     * @param rating
     * @param serverBaseUrl
     * @param accountMasterId
     * @param socialMediaPostDetails
     * @param socialMediaPostResponseDetails
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    @Override
    public void postToTwitterForHierarchy( String twitterMessage, double rating, String serverBaseUrl, int accountMasterId,
        SocialMediaPostDetails socialMediaPostDetails, SocialMediaPostResponseDetails socialMediaPostResponseDetails )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method postToTwitterForHierarchy() started" );
        if ( socialMediaPostDetails == null ) {
            throw new InvalidInputException( "Passed parameter socialMediaPostDetails is null" );
        }
        if ( socialMediaPostResponseDetails == null ) {
            throw new InvalidInputException( "Passed parameter socialMediaPostResponseDetails is null" );
        }

        long companyId = socialMediaPostDetails.getCompanyMediaPostDetails().getCompanyId();

        AgentMediaPostResponseDetails agentMediaPostResponseDetails = socialMediaPostResponseDetails
            .getAgentMediaPostResponseDetails();
        CompanyMediaPostResponseDetails companyMediaPostResponseDetails = socialMediaPostResponseDetails
            .getCompanyMediaPostResponseDetails();
        List<RegionMediaPostResponseDetails> regionMediaPostResponseDetailsList = socialMediaPostResponseDetails
            .getRegionMediaPostResponseDetailsList();
        List<BranchMediaPostResponseDetails> branchMediaPostResponseDetailsList = socialMediaPostResponseDetails
            .getBranchMediaPostResponseDetailsList();

        //Post for agent
        if ( socialMediaPostDetails.getAgentMediaPostDetails() != null ) {
            AgentSettings agentSettings = userManagementService.getUserSettings( socialMediaPostDetails
                .getAgentMediaPostDetails().getAgentId() );
            if ( agentSettings != null ) {
                try {
                    if ( surveyHandler.canPostOnSocialMedia( agentSettings, rating ) ) {
                        if ( !tweet( agentSettings, twitterMessage, companyId ) ) {
                            List<String> agentSocialList = socialMediaPostDetails.getAgentMediaPostDetails().getSharedOn();
                            if ( !agentSocialList.contains( CommonConstants.TWITTER_SOCIAL_SITE ) )
                                agentSocialList.add( CommonConstants.TWITTER_SOCIAL_SITE );

                            SocialMediaPostResponse twitterPostResponse = new SocialMediaPostResponse();
                            twitterPostResponse.setAccessToken( agentSettings.getSocialMediaTokens().getTwitterToken()
                                .getTwitterAccessToken() );
                            twitterPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                            twitterPostResponse.setResponseMessage( "Ok" );
                            if ( agentMediaPostResponseDetails.getTwitterPostResponseList() == null )
                                agentMediaPostResponseDetails
                                    .setTwitterPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                            agentMediaPostResponseDetails.getTwitterPostResponseList().add( twitterPostResponse );
                        }
                    }
                } catch ( TwitterException e ) {
                    LOG.error(
                        "TwitterException caught in postToSocialMedia() while trying to post to twitter. Nested excption is ",
                        e );
                    SocialMediaPostResponse twitterPostResponse = new SocialMediaPostResponse();
                    twitterPostResponse.setAccessToken( agentSettings.getSocialMediaTokens().getTwitterToken()
                        .getTwitterAccessToken() );
                    twitterPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                    twitterPostResponse.setResponseMessage( e.getMessage() );
                    if ( agentMediaPostResponseDetails.getTwitterPostResponseList() == null )
                        agentMediaPostResponseDetails.setTwitterPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                    agentMediaPostResponseDetails.getTwitterPostResponseList().add( twitterPostResponse );

                    reportBug( "Twitter", agentSettings.getProfileName(), e );
                }
            }

        }


        if ( accountMasterId != CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {

            //Post for company
            if ( socialMediaPostDetails.getCompanyMediaPostDetails() != null ) {
                OrganizationUnitSettings companySetting = organizationManagementService
                    .getCompanySettings( socialMediaPostDetails.getCompanyMediaPostDetails().getCompanyId() );
                if ( companySetting != null ) {
                    try {
                        if ( surveyHandler.canPostOnSocialMedia( companySetting, rating ) ) {
                            if ( !tweet( companySetting, twitterMessage, companyId ) ) {
                                List<String> companySocialList = socialMediaPostDetails.getCompanyMediaPostDetails()
                                    .getSharedOn();
                                if ( !companySocialList.contains( CommonConstants.TWITTER_SOCIAL_SITE ) )
                                    companySocialList.add( CommonConstants.TWITTER_SOCIAL_SITE );

                                SocialMediaPostResponse twitterPostResponse = new SocialMediaPostResponse();
                                twitterPostResponse.setAccessToken( companySetting.getSocialMediaTokens().getTwitterToken()
                                    .getTwitterAccessToken() );
                                twitterPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                                twitterPostResponse.setResponseMessage( "Ok" );
                                if ( companyMediaPostResponseDetails.getTwitterPostResponseList() == null )
                                    companyMediaPostResponseDetails
                                        .setTwitterPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                                companyMediaPostResponseDetails.getTwitterPostResponseList().add( twitterPostResponse );
                            }
                        }
                    } catch ( TwitterException e ) {
                        LOG.error(
                            "TwitterException caught in postToSocialMedia() while trying to post to twitter. Nested excption is ",
                            e );

                        SocialMediaPostResponse twitterPostResponse = new SocialMediaPostResponse();
                        twitterPostResponse.setAccessToken( companySetting.getSocialMediaTokens().getTwitterToken()
                            .getTwitterAccessToken() );
                        twitterPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                        twitterPostResponse.setResponseMessage( e.getMessage() );
                        if ( companyMediaPostResponseDetails.getTwitterPostResponseList() == null )
                            companyMediaPostResponseDetails
                                .setTwitterPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                        companyMediaPostResponseDetails.getTwitterPostResponseList().add( twitterPostResponse );

                        reportBug( "Twitter", companySetting.getProfileName(), e );
                    }
                }
            }


            //post for regions
            for ( RegionMediaPostDetails regionMediaPostDetails : socialMediaPostDetails.getRegionMediaPostDetailsList() ) {
                if ( regionMediaPostDetails != null ) {
                    OrganizationUnitSettings setting = organizationManagementService.getRegionSettings( regionMediaPostDetails
                        .getRegionId() );
                    if ( setting != null ) {
                        RegionMediaPostResponseDetails regionMediaPostResponseDetails = getRMPRDFromRMPRDList(
                            regionMediaPostResponseDetailsList, regionMediaPostDetails.getRegionId() );

                        try {
                            if ( surveyHandler.canPostOnSocialMedia( setting, rating ) ) {
                                if ( !tweet( setting, twitterMessage, companyId ) ) {
                                    List<String> regionSocialList = regionMediaPostDetails.getSharedOn();
                                    if ( !regionSocialList.contains( CommonConstants.TWITTER_SOCIAL_SITE ) )
                                        regionSocialList.add( CommonConstants.TWITTER_SOCIAL_SITE );
                                    regionMediaPostDetails.setSharedOn( regionSocialList );

                                    SocialMediaPostResponse twitterPostResponse = new SocialMediaPostResponse();
                                    twitterPostResponse.setAccessToken( setting.getSocialMediaTokens().getTwitterToken()
                                        .getTwitterAccessToken() );
                                    twitterPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                                    twitterPostResponse.setResponseMessage( "Ok" );
                                    if ( regionMediaPostResponseDetails.getTwitterPostResponseList() == null )
                                        regionMediaPostResponseDetails
                                            .setTwitterPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                                    regionMediaPostResponseDetails.getTwitterPostResponseList().add( twitterPostResponse );
                                }
                            }
                        } catch ( TwitterException e ) {
                            LOG.error(
                                "TwitterException caught in postToSocialMedia() while trying to post to twitter. Nested excption is ",
                                e );

                            SocialMediaPostResponse twitterPostResponse = new SocialMediaPostResponse();
                            twitterPostResponse.setAccessToken( setting.getSocialMediaTokens().getTwitterToken()
                                .getTwitterAccessToken() );
                            twitterPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                            twitterPostResponse.setResponseMessage( e.getMessage() );
                            if ( regionMediaPostResponseDetails.getTwitterPostResponseList() == null )
                                regionMediaPostResponseDetails
                                    .setTwitterPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                            regionMediaPostResponseDetails.getTwitterPostResponseList().add( twitterPostResponse );

                            reportBug( "Twitter", setting.getProfileName(), e );
                        }
                    }

                }

            }

            //post for branches
            for ( BranchMediaPostDetails branchMediaPostDetails : socialMediaPostDetails.getBranchMediaPostDetailsList() ) {
                if ( branchMediaPostDetails != null ) {
                    OrganizationUnitSettings setting = organizationManagementService
                        .getBranchSettingsDefault( branchMediaPostDetails.getBranchId() );
                    if ( setting != null ) {
                        BranchMediaPostResponseDetails branchMediaPostResponseDetails = getBMPRDFromBMPRDList(
                            branchMediaPostResponseDetailsList, branchMediaPostDetails.getBranchId() );

                        try {


                            if ( surveyHandler.canPostOnSocialMedia( setting, rating ) ) {
                                if ( !tweet( setting, twitterMessage, companyId ) ) {
                                    List<String> branchSocialList = branchMediaPostDetails.getSharedOn();
                                    if ( !branchSocialList.contains( CommonConstants.TWITTER_SOCIAL_SITE ) )
                                        branchSocialList.add( CommonConstants.TWITTER_SOCIAL_SITE );
                                    branchMediaPostDetails.setSharedOn( branchSocialList );

                                    SocialMediaPostResponse twitterPostResponse = new SocialMediaPostResponse();
                                    twitterPostResponse.setAccessToken( setting.getSocialMediaTokens().getTwitterToken()
                                        .getTwitterAccessToken() );
                                    twitterPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                                    twitterPostResponse.setResponseMessage( "Ok" );
                                    if ( branchMediaPostResponseDetails.getTwitterPostResponseList() == null )
                                        branchMediaPostResponseDetails
                                            .setTwitterPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                                    branchMediaPostResponseDetails.getTwitterPostResponseList().add( twitterPostResponse );
                                }
                            }

                        } catch ( TwitterException e ) {
                            LOG.error(
                                "TwitterException caught in postToSocialMedia() while trying to post to twitter. Nested excption is ",
                                e );

                            SocialMediaPostResponse twitterPostResponse = new SocialMediaPostResponse();
                            twitterPostResponse.setAccessToken( setting.getSocialMediaTokens().getTwitterToken()
                                .getTwitterAccessToken() );
                            twitterPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                            twitterPostResponse.setResponseMessage( e.getMessage() );
                            if ( branchMediaPostResponseDetails.getTwitterPostResponseList() == null )
                                branchMediaPostResponseDetails
                                    .setTwitterPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                            branchMediaPostResponseDetails.getTwitterPostResponseList().add( twitterPostResponse );

                            reportBug( "Twitter", setting.getProfileName(), e );
                        }
                    }

                }

            }
        }

        LOG.debug( "Method postToTwitterForHierarchy() ended" );
    }


    /*
     * (non-Javadoc)
     * @see com.realtech.socialsurvey.core.services.social.SocialManagementService#postToSocialMedia(java.lang.String, java.lang.String, java.lang.String, java.lang.String, long, double, java.lang.String, java.lang.String, boolean, java.lang.String, boolean)
     */
    @Override
    public boolean postToSocialMedia( String agentName, String agentProfileLink, String custFirstName, String custLastName,
        long agentId, double rating, String customerEmail, String feedback, boolean isAbusive, String serverBaseUrl,
        boolean onlyPostToSocialSurvey ) throws NonFatalException
    {

        LOG.info( "Method to post feedback of customer to various pages of social networking sites started." );
        boolean successfullyPosted = true;

        if ( agentProfileLink == null || agentProfileLink.isEmpty() ) {
            throw new InvalidInputException(
                "Invalid parameter passed : passed input parameter agentProfileLink is null or empty" );
        }

        try {
            String customerDisplayName = emailFormatHelper.getCustomerDisplayNameForEmail( custFirstName, custLastName );

            DecimalFormat ratingFormat = CommonConstants.SOCIAL_RANKING_FORMAT;
            ratingFormat.setMinimumFractionDigits( 1 );
            ratingFormat.setMaximumFractionDigits( 1 );
            User agent = userManagementService.getUserByUserId( agentId );
            int accountMasterId = 0;
            try {
                AccountsMaster masterAccount = agent.getCompany().getLicenseDetails().get( CommonConstants.INITIAL_INDEX )
                    .getAccountsMaster();
                accountMasterId = masterAccount.getAccountsMasterId();
            } catch ( NullPointerException e ) {
                LOG.error( "NullPointerException caught in postToSocialMedia() while fetching account master id for agent "
                    + agent.getFirstName() );
                successfullyPosted = false;
            }

            Map<String, List<OrganizationUnitSettings>> settingsMap = getSettingsForBranchesAndRegionsInHierarchy( agentId );
            List<OrganizationUnitSettings> companySettings = settingsMap
                .get( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
            List<OrganizationUnitSettings> regionSettings = settingsMap
                .get( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
            List<OrganizationUnitSettings> branchSettings = settingsMap
                .get( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );

            SurveyDetails surveyDetails = surveyHandler.getSurveyDetails( agentId, customerEmail, custFirstName, custLastName );
            SocialMediaPostDetails socialMediaPostDetails = surveyHandler.getSocialMediaPostDetailsBySurvey( surveyDetails,
                companySettings.get( 0 ), regionSettings, branchSettings );

            //create socialMediaPostResponseDetails object
            SocialMediaPostResponseDetails socialMediaPostResponseDetails = surveyDetails.getSocialMediaPostResponseDetails();
            if ( socialMediaPostResponseDetails == null ) {
                socialMediaPostResponseDetails = new SocialMediaPostResponseDetails();
            }
            AgentMediaPostResponseDetails agentMediaPostResponseDetails = socialMediaPostResponseDetails
                .getAgentMediaPostResponseDetails();
            if ( agentMediaPostResponseDetails == null ) {
                agentMediaPostResponseDetails = new AgentMediaPostResponseDetails();
                agentMediaPostResponseDetails.setAgentId( socialMediaPostDetails.getAgentMediaPostDetails().getAgentId() );
            }
            CompanyMediaPostResponseDetails companyMediaPostResponseDetails = socialMediaPostResponseDetails
                .getCompanyMediaPostResponseDetails();
            if ( companyMediaPostResponseDetails == null ) {
                companyMediaPostResponseDetails = new CompanyMediaPostResponseDetails();
                companyMediaPostResponseDetails.setCompanyId( socialMediaPostDetails.getCompanyMediaPostDetails()
                    .getCompanyId() );
            }
            List<RegionMediaPostResponseDetails> regionMediaPostResponseDetailsList = socialMediaPostResponseDetails
                .getRegionMediaPostResponseDetailsList();
            if ( regionMediaPostResponseDetailsList == null ) {
                regionMediaPostResponseDetailsList = new ArrayList<RegionMediaPostResponseDetails>();
            }
            List<BranchMediaPostResponseDetails> branchMediaPostResponseDetailsList = socialMediaPostResponseDetails
                .getBranchMediaPostResponseDetailsList();
            if ( branchMediaPostResponseDetailsList == null ) {
                branchMediaPostResponseDetailsList = new ArrayList<BranchMediaPostResponseDetails>();
            }


            if ( socialMediaPostDetails.getAgentMediaPostDetails().getSharedOn() == null ) {
                socialMediaPostDetails.getAgentMediaPostDetails().setSharedOn( new ArrayList<String>() );
            }
            if ( socialMediaPostDetails.getCompanyMediaPostDetails().getSharedOn() == null ) {
                socialMediaPostDetails.getCompanyMediaPostDetails().setSharedOn( new ArrayList<String>() );
            }

            List<String> agentSocialList = socialMediaPostDetails.getAgentMediaPostDetails().getSharedOn();
            List<String> companySocialList = socialMediaPostDetails.getCompanyMediaPostDetails().getSharedOn();


            for ( BranchMediaPostDetails branchMediaPostDetails : socialMediaPostDetails.getBranchMediaPostDetailsList() ) {
                if ( branchMediaPostDetails.getSharedOn() == null ) {
                    branchMediaPostDetails.setSharedOn( new ArrayList<String>() );
                }
                //create BranchMediaPostResponseDetails 
                BranchMediaPostResponseDetails branchMediaPostResponseDetails = new BranchMediaPostResponseDetails();
                branchMediaPostResponseDetails.setBranchId( branchMediaPostDetails.getBranchId() );
                branchMediaPostResponseDetails.setRegionId( branchMediaPostDetails.getRegionId() );
                if ( getBMPRDFromBMPRDList( branchMediaPostResponseDetailsList, branchMediaPostDetails.getBranchId() ) == null ) {
                    branchMediaPostResponseDetailsList.add( branchMediaPostResponseDetails );
                }
            }
            for ( RegionMediaPostDetails regionMediaPostDetails : socialMediaPostDetails.getRegionMediaPostDetailsList() ) {
                if ( regionMediaPostDetails.getSharedOn() == null ) {
                    regionMediaPostDetails.setSharedOn( new ArrayList<String>() );
                }
                //create RegionMediaPostResponseDetails 
                RegionMediaPostResponseDetails regionMediaPostResponseDetails = new RegionMediaPostResponseDetails();
                regionMediaPostResponseDetails.setRegionId( regionMediaPostDetails.getRegionId() );
                if ( getRMPRDFromRMPRDList( regionMediaPostResponseDetailsList, regionMediaPostDetails.getRegionId() ) == null ) {
                    regionMediaPostResponseDetailsList.add( regionMediaPostResponseDetails );
                }
            }

            socialMediaPostResponseDetails.setAgentMediaPostResponseDetails( agentMediaPostResponseDetails );
            socialMediaPostResponseDetails.setCompanyMediaPostResponseDetails( companyMediaPostResponseDetails );
            socialMediaPostResponseDetails.setBranchMediaPostResponseDetailsList( branchMediaPostResponseDetailsList );
            socialMediaPostResponseDetails.setRegionMediaPostResponseDetailsList( regionMediaPostResponseDetailsList );

            //Social Survey
            if ( !agentSocialList.contains( CommonConstants.SOCIAL_SURVEY_SOCIAL_SITE ) )
                agentSocialList.add( CommonConstants.SOCIAL_SURVEY_SOCIAL_SITE );

            if ( !companySocialList.contains( CommonConstants.SOCIAL_SURVEY_SOCIAL_SITE ) )
                companySocialList.add( CommonConstants.SOCIAL_SURVEY_SOCIAL_SITE );

            for ( RegionMediaPostDetails regionMediaPostDetails : socialMediaPostDetails.getRegionMediaPostDetailsList() ) {
                List<String> regionSocialList = regionMediaPostDetails.getSharedOn();
                if ( !regionSocialList.contains( CommonConstants.SOCIAL_SURVEY_SOCIAL_SITE ) )
                    regionSocialList.add( CommonConstants.SOCIAL_SURVEY_SOCIAL_SITE );
                regionMediaPostDetails.setSharedOn( regionSocialList );


            }
            for ( BranchMediaPostDetails branchMediaPostDetails : socialMediaPostDetails.getBranchMediaPostDetailsList() ) {
                List<String> branchSocialList = branchMediaPostDetails.getSharedOn();
                if ( !branchSocialList.contains( CommonConstants.SOCIAL_SURVEY_SOCIAL_SITE ) )
                    branchSocialList.add( CommonConstants.SOCIAL_SURVEY_SOCIAL_SITE );
                branchMediaPostDetails.setSharedOn( branchSocialList );
            }

            //if onlyPostToSocialSurvey is false than only post on the social media otherwise just add social survey channel in social media post list
            if ( !isAbusive && !onlyPostToSocialSurvey ) {
                // Facebook
                //                facebook older message pattern
                //                String facebookMessage = ratingFormat.format( rating ) + "-Star Survey Response from " + customerDisplayName
                //                    + " for " + agentName + " on Social Survey - view at " + surveyHandler.getApplicationBaseUrl()
                //                    + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
                //                facebookMessage += "\n Feedback : " + feedback;

                String facebookMessage = customerDisplayName + " gave " + agentName + " a " + ratingFormat.format( rating )
                    + "-star review on SocialSurvey saying : \"" + feedback + "\".\nView this and more at "
                    + surveyHandler.getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink+"/";

                postToFacebookForHierarchy( facebookMessage, rating, serverBaseUrl, accountMasterId, socialMediaPostDetails,
                    socialMediaPostResponseDetails );

                // LinkedIn
                String linkedinMessage = customerDisplayName + " gave " + agentName + " a " + ratingFormat.format( rating )
                    + "-star review on SocialSurvey saying : \"" + feedback + "\". View this and more at "
                    + surveyHandler.getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
                String linkedinProfileUrl = surveyHandler.getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL
                    + agentProfileLink;
                String linkedinMessageFeedback = "From : " + customerDisplayName + " - " + feedback;

                postToLinkedInForHierarchy( linkedinMessage, rating, linkedinProfileUrl, linkedinMessageFeedback,
                    accountMasterId, socialMediaPostDetails, socialMediaPostResponseDetails, companySettings.get( 0 ), false );

                // Twitter

                // String twitterMessage = String.format( CommonConstants.TWITTER_MESSAGE, ratingFormat.format( rating ),
                //    customerDisplayName, agentName, "@SocialSurveyMe" )
                //    + surveyHandler.getApplicationBaseUrl()
                //    + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
                String twitterMessage = customerDisplayName + " gave " + agentName + " a " + ratingFormat.format( rating )
                    + "-star review @SocialSurveyMe. " + surveyHandler.getApplicationBaseUrl()
                    + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
                postToTwitterForHierarchy( twitterMessage, rating, serverBaseUrl, accountMasterId, socialMediaPostDetails,
                    socialMediaPostResponseDetails );

            }

            surveyDetails.setSocialMediaPostResponseDetails( socialMediaPostResponseDetails );

            socialMediaPostDetails.getAgentMediaPostDetails().setSharedOn( agentSocialList );
            socialMediaPostDetails.getCompanyMediaPostDetails().setSharedOn( companySocialList );
            surveyDetails.setSocialMediaPostDetails( socialMediaPostDetails );
            surveyHandler.updateSurveyDetails( surveyDetails );


        } catch ( NonFatalException e ) {
            LOG.error(
                "Non fatal Exception caught in postToSocialMedia() while trying to post to social networking sites. Nested excption is ",
                e );
            successfullyPosted = false;
            throw new NonFatalException( e.getMessage() );
        }
        LOG.info( "Method to post feedback of customer to various pages of social networking sites finished." );
        return successfullyPosted;

    }


    private void reportBug( String socAppName, String name, Exception e )
    {
        try {
            LOG.info( "Building error message for the auto post failure" );
            String errorMsg = "<br>" + e.getMessage() + "<br><br>";
            if ( socAppName.length() > 0 )
                errorMsg += "Social Application : " + socAppName;
            errorMsg += "<br>Agent Name : " + name + "<br>";
            errorMsg += "<br>StackTrace : <br>" + ExceptionUtils.getStackTrace( e ).replaceAll( "\n", "<br>" ) + "<br>";
            LOG.info( "Error message built for the auto post failure" );
            LOG.info( "Sending bug mail to admin for the auto post failure" );
            emailServices.sendReportBugMailToAdmin( applicationAdminName, errorMsg, applicationAdminEmail );
            LOG.info( "Sent bug mail to admin for the auto post failure" );
        } catch ( UndeliveredEmailException ude ) {
            LOG.error( "error while sending report bug mail to admin ", ude );
        } catch ( InvalidInputException iie ) {
            LOG.error( "error while sending report bug mail to admin ", iie );
        }
    }


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
    @Override
    public void updateSocialConnectionsHistory( String entityType, long entityId, SocialMediaTokens mediaTokens,
        String socialMedia, String action ) throws InvalidInputException, ProfileNotFoundException
    {
        //Check if any of the parameters are null or empty
        if ( entityType == null || entityType.isEmpty() ) {
            throw new InvalidInputException( "Invalid entity type. EntityType : " + entityType );
        }

        if ( entityId <= 0l ) {
            throw new InvalidInputException( "Invalid entity ID. Entity ID : " + entityId );
        }

        if ( mediaTokens == null ) {
            throw new InvalidInputException( "MediaTokens is null" );
        }

        if ( action == null || action.isEmpty() ) {
            throw new InvalidInputException( "action is invalid. Action : " + action );
        }

        if ( socialMedia == null || socialMedia.isEmpty() ) {
            throw new InvalidInputException( "social media invalid. Social Media : " + socialMedia );
        }
        SocialUpdateAction socialUpdateAction = new SocialUpdateAction();

        //Check for the correct media token and set the appropriate link
        switch ( socialMedia ) {
            case CommonConstants.FACEBOOK_SOCIAL_SITE:
                if ( ( mediaTokens.getFacebookToken() != null )
                    && ( mediaTokens.getFacebookToken().getFacebookPageLink() != null )
                    && !( mediaTokens.getFacebookToken().getFacebookPageLink().isEmpty() ) )
                    socialUpdateAction.setLink( mediaTokens.getFacebookToken().getFacebookPageLink() );
                break;

            case CommonConstants.TWITTER_SOCIAL_SITE:
                if ( ( mediaTokens.getTwitterToken() != null ) && ( mediaTokens.getTwitterToken().getTwitterPageLink() != null )
                    && !( mediaTokens.getTwitterToken().getTwitterPageLink().isEmpty() ) )
                    socialUpdateAction.setLink( mediaTokens.getTwitterToken().getTwitterPageLink() );
                break;

            case CommonConstants.GOOGLE_SOCIAL_SITE:
                if ( ( mediaTokens.getGoogleToken() != null ) && ( mediaTokens.getGoogleToken().getProfileLink() != null )
                    && !( mediaTokens.getGoogleToken().getProfileLink().isEmpty() ) )
                    socialUpdateAction.setLink( mediaTokens.getGoogleToken().getProfileLink() );
                break;

            case CommonConstants.LINKEDIN_SOCIAL_SITE:
                if ( ( mediaTokens.getLinkedInToken() != null )
                    && ( mediaTokens.getLinkedInToken().getLinkedInPageLink() != null )
                    && !( mediaTokens.getLinkedInToken().getLinkedInPageLink().isEmpty() ) )
                    socialUpdateAction.setLink( mediaTokens.getLinkedInToken().getLinkedInPageLink() );
                break;

            case CommonConstants.ZILLOW_SOCIAL_SITE:
                if ( ( mediaTokens.getZillowToken() != null ) && ( mediaTokens.getZillowToken().getZillowProfileLink() != null )
                    && !( mediaTokens.getZillowToken().getZillowProfileLink().isEmpty() ) )
                    socialUpdateAction.setLink( mediaTokens.getZillowToken().getZillowProfileLink() );
                break;

            case CommonConstants.YELP_SOCIAL_SITE:
                if ( ( mediaTokens.getYelpToken() != null ) && ( mediaTokens.getYelpToken().getYelpPageLink() != null )
                    && !( mediaTokens.getYelpToken().getYelpPageLink().isEmpty() ) )
                    socialUpdateAction.setLink( mediaTokens.getYelpToken().getYelpPageLink() );
                break;

            case CommonConstants.REALTOR_SOCIAL_SITE:
                if ( ( mediaTokens.getRealtorToken() != null )
                    && ( mediaTokens.getRealtorToken().getRealtorProfileLink() != null )
                    && !( mediaTokens.getRealtorToken().getRealtorProfileLink().isEmpty() ) )
                    socialUpdateAction.setLink( mediaTokens.getRealtorToken().getRealtorProfileLink() );
                break;

            case CommonConstants.LENDINGTREE_SOCIAL_SITE:
                if ( ( mediaTokens.getLendingTreeToken() != null )
                    && ( mediaTokens.getLendingTreeToken().getLendingTreeProfileLink() != null )
                    && !( mediaTokens.getLendingTreeToken().getLendingTreeProfileLink().isEmpty() ) )
                    socialUpdateAction.setLink( mediaTokens.getLendingTreeToken().getLendingTreeProfileLink() );
                break;

            default:
                throw new InvalidInputException( "Invalid social media token entered" );
        }
        Map<String, Long> hierarchyList = profileManagementService.getHierarchyDetailsByEntity( entityType, entityId );

        socialUpdateAction.setAction( action );
        socialUpdateAction.setAgentId( hierarchyList.get( CommonConstants.AGENT_ID ) );
        socialUpdateAction.setBranchId( hierarchyList.get( CommonConstants.BRANCH_ID ) );
        socialUpdateAction.setRegionId( hierarchyList.get( CommonConstants.REGION_ID ) );
        socialUpdateAction.setCompanyId( hierarchyList.get( CommonConstants.COMPANY_ID ) );
        socialUpdateAction.setSocialMediaSource( socialMedia );

        socialPostDao.addActionToSocialConnectionHistory( socialUpdateAction );
    }


    /**
     * Method to disconnect user from all social connections
     * 
     * @param entityType
     * @param entityId
     * @throws InvalidInputException 
     */
    @Override
    public void disconnectAllSocialConnections( String entityType, long entityId ) throws InvalidInputException
    {
        //Check for validity of entityType and entityId
        if ( entityType == null || entityType.isEmpty() ) {
            throw new InvalidInputException( "Entity type cannot be empty" );
        }

        if ( entityId <= 0l ) {
            throw new InvalidInputException( "Invalid entity ID entered. ID : " + entityId );
        }

        //Unset settings for each social media source
        boolean unset = CommonConstants.UNSET_SETTINGS;
        String collection = null;
        OrganizationUnitSettings unitSettings = null;
        SocialMediaTokens mediaTokens = null;
        //Get social media tokens and unit settings
        // Check for the collection to update
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            unitSettings = organizationManagementService.getCompanySettings( entityId );
            collection = MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION;
            if ( unitSettings == null ) {
                throw new InvalidInputException( "Unit settings null for type company and ID : " + entityId );
            }
        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            unitSettings = organizationManagementService.getRegionSettings( entityId );
            collection = MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION;
            if ( unitSettings == null ) {
                throw new InvalidInputException( "Unit settings null for type region and ID : " + entityId );
            }
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            try {
                unitSettings = organizationManagementService.getBranchSettingsDefault( entityId );
            } catch ( NoRecordsFetchedException e ) {
                throw new InvalidInputException( "Unit settings null for type branch and ID : " + entityId );
            }
            collection = MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION;
            if ( unitSettings == null ) {
                throw new InvalidInputException( "Unit settings null for type branch and ID : " + entityId );
            }
        } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
            unitSettings = userManagementService.getUserSettings( entityId );
            collection = MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION;
            if ( unitSettings == null ) {
                throw new InvalidInputException( "Unit settings null for type agent and ID : " + entityId );
            }
        } else {
            throw new InvalidInputException( "Invalid entity type : " + entityType );
        }

        //Check if social media tokens exist
        if ( unitSettings.getSocialMediaTokens() == null ) {
            LOG.debug( "No social media tokens exist for entityType : " + entityType + " entityId : " + entityId );
            return;
        }

        mediaTokens = unitSettings.getSocialMediaTokens();
        //Make a backup of the mediaTokens
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            CommonConstants.DELETED_SOCIAL_MEDIA_TOKENS_COLUMN, mediaTokens, unitSettings, collection );

        try {
            if ( mediaTokens.getFacebookToken() != null ) {
                String socialMedia = CommonConstants.FACEBOOK_SOCIAL_SITE;
                SettingsForApplication settings = SettingsForApplication.FACEBOOK;
                //disconnect social network in mongo
                disconnectSocialNetwork( socialMedia, unitSettings, collection );
                //Update settings set status
                updateSettingsSetStatusByEntityType( entityType, entityId, settings, unset );
                //update social connections history
                updateSocialConnectionsHistory( entityType, entityId, mediaTokens, socialMedia,
                    CommonConstants.SOCIAL_MEDIA_DISCONNECTED );
            }
            if ( mediaTokens.getTwitterToken() != null ) {
                String socialMedia = CommonConstants.TWITTER_SOCIAL_SITE;
                SettingsForApplication settings = SettingsForApplication.TWITTER;
                //disconnect social network in mongo
                disconnectSocialNetwork( socialMedia, unitSettings, collection );
                //Update settings set status
                updateSettingsSetStatusByEntityType( entityType, entityId, settings, unset );
                //update social connections history
                updateSocialConnectionsHistory( entityType, entityId, mediaTokens, socialMedia,
                    CommonConstants.SOCIAL_MEDIA_DISCONNECTED );
            }
            if ( mediaTokens.getGoogleToken() != null ) {
                String socialMedia = CommonConstants.GOOGLE_SOCIAL_SITE;
                SettingsForApplication settings = SettingsForApplication.GOOGLE_PLUS;
                //disconnect social network in mongo
                disconnectSocialNetwork( socialMedia, unitSettings, collection );
                //Update settings set status
                updateSettingsSetStatusByEntityType( entityType, entityId, settings, unset );
                //update social connections history
                updateSocialConnectionsHistory( entityType, entityId, mediaTokens, socialMedia,
                    CommonConstants.SOCIAL_MEDIA_DISCONNECTED );
            }
            if ( mediaTokens.getLinkedInToken() != null ) {
                String socialMedia = CommonConstants.LINKEDIN_SOCIAL_SITE;
                SettingsForApplication settings = SettingsForApplication.LINKED_IN;
                //disconnect social network in mongo
                disconnectSocialNetwork( socialMedia, unitSettings, collection );
                //Update settings set status
                updateSettingsSetStatusByEntityType( entityType, entityId, settings, unset );
                //update social connections history
                updateSocialConnectionsHistory( entityType, entityId, mediaTokens, socialMedia,
                    CommonConstants.SOCIAL_MEDIA_DISCONNECTED );
            }
            if ( mediaTokens.getZillowToken() != null ) {
                String socialMedia = CommonConstants.ZILLOW_SOCIAL_SITE;
                SettingsForApplication settings = SettingsForApplication.ZILLOW;
                //disconnect social network in mongo
                disconnectSocialNetwork( socialMedia, unitSettings, collection );
                //Update settings set status
                updateSettingsSetStatusByEntityType( entityType, entityId, settings, unset );
                //update social connections history
                updateSocialConnectionsHistory( entityType, entityId, mediaTokens, socialMedia,
                    CommonConstants.SOCIAL_MEDIA_DISCONNECTED );
            }
            if ( mediaTokens.getYelpToken() != null ) {
                String socialMedia = CommonConstants.YELP_SOCIAL_SITE;
                SettingsForApplication settings = SettingsForApplication.YELP;
                //disconnect social network in mongo
                disconnectSocialNetwork( socialMedia, unitSettings, collection );
                //Update settings set status
                updateSettingsSetStatusByEntityType( entityType, entityId, settings, unset );
                //update social connections history
                updateSocialConnectionsHistory( entityType, entityId, mediaTokens, socialMedia,
                    CommonConstants.SOCIAL_MEDIA_DISCONNECTED );
            }
            if ( mediaTokens.getRealtorToken() != null ) {
                String socialMedia = CommonConstants.REALTOR_SOCIAL_SITE;
                SettingsForApplication settings = SettingsForApplication.REALTOR;
                //disconnect social network in mongo
                disconnectSocialNetwork( socialMedia, unitSettings, collection );
                //Update settings set status
                updateSettingsSetStatusByEntityType( entityType, entityId, settings, unset );
                //update social connections history
                updateSocialConnectionsHistory( entityType, entityId, mediaTokens, socialMedia,
                    CommonConstants.SOCIAL_MEDIA_DISCONNECTED );
            }
            if ( mediaTokens.getLendingTreeToken() != null ) {
                String socialMedia = CommonConstants.LENDINGTREE_SOCIAL_SITE;
                SettingsForApplication settings = SettingsForApplication.LENDING_TREE;
                //disconnect social network in mongo
                disconnectSocialNetwork( socialMedia, unitSettings, collection );
                //Update settings set status
                updateSettingsSetStatusByEntityType( entityType, entityId, settings, unset );
                //update social connections history
                updateSocialConnectionsHistory( entityType, entityId, mediaTokens, socialMedia,
                    CommonConstants.SOCIAL_MEDIA_DISCONNECTED );
            }

            //Finally unset SocialMediaTokens
            removeSocialMediaTokens( unitSettings, collection );

        } catch ( ProfileNotFoundException e ) {
            throw new InvalidInputException( "Profile not found for entityType : " + entityType + " and entityID : " + entityId );
        }
    }


    /**
     * Method to set settings status by entity type
     * 
     * @param entityType
     * @param entityId
     * @param settings
     * @param setValue
     * @throws InvalidInputException
     */
    void updateSettingsSetStatusByEntityType( String entityType, long entityId, SettingsForApplication settings,
        boolean setValue ) throws InvalidInputException
    {
        //Null checks for entityType and entityId
        if ( entityType == null || entityType.isEmpty() ) {
            throw new InvalidInputException( "Invalid entity type : " + entityType );
        }

        if ( entityId <= 0l ) {
            throw new InvalidInputException( "Invalid entity ID : " + entityId );
        }
        try {
            switch ( entityType ) {
                case CommonConstants.COMPANY_ID_COLUMN:
                    //update SETTINGS_SET_STATUS to unset in COMPANY table
                    Company company = organizationManagementService.getCompanyById( entityId );
                    if ( company != null ) {
                        settingsSetter.setSettingsValueForCompany( company, settings, setValue );
                        userManagementService.updateCompany( company );
                    }
                    break;

                case CommonConstants.REGION_ID_COLUMN:
                    //update SETTINGS_SET_STATUS to unset in REGION table
                    Region region = userManagementService.getRegionById( entityId );
                    if ( region != null ) {
                        settingsSetter.setSettingsValueForRegion( region, settings, setValue );
                        userManagementService.updateRegion( region );
                    }
                    break;

                case CommonConstants.BRANCH_ID_COLUMN:
                    //update SETTINGS_SET_STATUS to unset in BRANCH table
                    Branch branch = userManagementService.getBranchById( entityId );
                    if ( branch != null ) {
                        settingsSetter.setSettingsValueForBranch( branch, settings, setValue );
                        userManagementService.updateBranch( branch );
                    }
                    break;

                case CommonConstants.AGENT_ID_COLUMN:
                    break;
                default:
                    throw new InvalidInputException( "Invalid entity type : " + entityType );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException occured while setting values for company. Reason : ", e );
        }
    }


    @Override
    public List<ZillowTempPost> getAllZillowTempPosts()
    {
        return zillowTempPostDao.findAll( ZillowTempPost.class );
    }


    @Override
    public ExternalSurveyTracker checkExternalSurveyTrackerExist( String entityColumnName, long entityId, String source,
        String reviewUrl, Timestamp reviewDate )
    {
        return externalSurveyTrackerDao.checkExternalSurveyTrackerDetailsExist( entityColumnName, entityId, source, reviewUrl,
            reviewDate );
    }


    @Override
    public void saveExternalSurveyTracker( String entityColumnName, long entityId, String source, String sourceLink,
        String reviewUrl, double rating, int autoPostStatus, int complaintResolutionStatus, Timestamp reviewDate, String postedOn )
    {
        externalSurveyTrackerDao.saveExternalSurveyTracker( entityColumnName, entityId, source, sourceLink, reviewUrl, rating, autoPostStatus,
            complaintResolutionStatus, reviewDate, postedOn );
    }


    @Override
    public void removeProcessedZillowTempPosts( List<Long> processedZillowTempPostIds )
    {
        zillowTempPostDao.removeProcessedZillowTempPosts( processedZillowTempPostIds );
    }


    @Transactional
    @Override
    public SurveyPreInitiationList getUnmatchedPreInitiatedSurveys( long companyId, int startIndex, int batchSize )
        throws InvalidInputException
    {
        LOG.debug( "method getUnmatchedPreInitiatedSurveys called for company id : " + companyId );
        SurveyPreInitiationList surveyPreInitiationListVO = new SurveyPreInitiationList();
        if ( companyId <= 0 ) {
            throw new InvalidInputException( " Wrong parameter passed : companyId is invalid " );
        }

        List<SurveyPreInitiation> surveyPreInitiations = surveyPreInitiationDao.getUnmatchedPreInitiatedSurveys( companyId,
            startIndex, batchSize );
        surveyPreInitiationListVO.setSurveyPreInitiationList( surveyPreInitiations );
        surveyPreInitiationListVO.setTotalRecord( surveyPreInitiationDao.getUnmatchedPreInitiatedSurveyCount( companyId ) );
        return surveyPreInitiationListVO;
    }


    @Transactional
    @Override
    public SurveyPreInitiationList getProcessedPreInitiatedSurveys( long companyId, int startIndex, int batchSize )
        throws InvalidInputException
    {
        LOG.debug( "method getProcessedPreInitiatedSurveys called for company id : " + companyId );
        SurveyPreInitiationList surveyPreInitiationListVO = new SurveyPreInitiationList();
        if ( companyId <= 0 ) {
            throw new InvalidInputException( " Wrong parameter passed : companyId is invalid " );
        }

        List<SurveyPreInitiation> surveyPreInitiations = surveyPreInitiationDao.getProcessedPreInitiatedSurveys( companyId,
            startIndex, batchSize );
        surveyPreInitiationListVO.setSurveyPreInitiationList( surveyPreInitiations );
        surveyPreInitiationListVO.setTotalRecord( surveyPreInitiationDao.getProcessedPreInitiatedSurveyCount( companyId ) );
        return surveyPreInitiationListVO;
    }


    @Transactional
    @Override
    public void updateAgentIdOfSurveyPreinitiationRecordsForEmail( User user, String emailAddress )
        throws InvalidInputException
    {
        if ( user == null ) {
            throw new InvalidInputException( " Wrong parameter passed : user is null " );
        }
        if ( emailAddress == null || emailAddress.isEmpty() ) {
            throw new InvalidInputException( " Wrong parameter passed : emailAddress is null oe empty " );
        }
        LOG.debug( "method getProcessedPreInitiatedSurveys called for agentId id : " + user.getUserId() );
        surveyPreInitiationDao.updateAgentIdOfPreInitiatedSurveysByAgentEmailAddress( user, emailAddress );
    }


    @Transactional
    @Override
    public void updateSurveyPreinitiationRecordsAsIgnored( String emailAddress ) throws InvalidInputException
    {
        if ( emailAddress == null || emailAddress.isEmpty() ) {
            throw new InvalidInputException( " Wrong parameter passed : emailAddress is null oe empty " );
        }

        LOG.debug( "method getProcessedPreInitiatedSurveys called for email id : " + emailAddress );
        surveyPreInitiationDao.updateSurveyPreinitiationRecordsAsIgnored( emailAddress );
    }
}
