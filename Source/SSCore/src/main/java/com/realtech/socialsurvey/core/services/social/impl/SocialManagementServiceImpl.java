package com.realtech.socialsurvey.core.services.social.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfileStage;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.enums.ProfileStages;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;

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
    private UserManagementService userManagementService;

    @Autowired
    private UserProfileDao userProfileDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SurveyDetailsDao surveyDetailsDao;

    @Autowired
    private ProfileManagementService profileManagementService;

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
                        // Updating customised data
                        PostUpdate postUpdate = new PostUpdate( message );
                        postUpdate.setCaption( agentSettings.getCompleteProfileUrl() );
                        try {
                            postUpdate.setLink( new URL( agentSettings.getCompleteProfileUrl() ) );
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
        String linkedinMessageFeedback ) throws NonFatalException
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
                        String a = "{\"comment\": \"\",\"content\": {" + "\"title\": \"\"," + "\"description\": \"" + message
                            + "-" + linkedinMessageFeedback + "\"," + "\"submitted-url\": \"" + linkedinProfileUrl + "\",  "
                            + "\"submitted-image-url\": \"" + applicationLogoUrlForLinkedin + "\"},"
                            + "\"visibility\": {\"code\": \"anyone\" }}";
                        StringEntity entity = new StringEntity( a );
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
    public List<OrganizationUnitSettings> getSettingsForBranchesAndRegionsInHierarchy( long agentId )
        throws InvalidInputException
    {
        LOG.info( "Method to get settings of branches and regions current agent belongs to, getSettingsForBranchesAndRegionsInHierarchy() started." );
        List<OrganizationUnitSettings> settings = new ArrayList<>();
        Set<Long> branchIds = new HashSet<>();
        Set<Long> regionIds = new HashSet<>();
        Set<Long> companyIds = new HashSet<>();
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.USER_COLUMN, userDao.findById( User.class, agentId ) );
        queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
            userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) );
        List<UserProfile> userProfiles = userProfileDao.findByKeyValue( UserProfile.class, queries );
        for ( UserProfile userProfile : userProfiles ) {
            branchIds.add( userProfile.getBranchId() );
            regionIds.add( userProfile.getRegionId() );
            companyIds.add( userProfile.getCompany().getCompanyId() );
        }

        for ( Long branchId : branchIds ) {
            settings.add( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( branchId,
                CommonConstants.BRANCH_SETTINGS_COLLECTION ) );
        }

        for ( Long regionId : regionIds ) {
            settings.add( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( regionId,
                CommonConstants.REGION_SETTINGS_COLLECTION ) );
        }

        for ( Long companyId : companyIds ) {
            settings.add( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( companyId,
                CommonConstants.COMPANY_SETTINGS_COLLECTION ) );
        }

        LOG.info( "Method to get settings of branches and regions current agent belongs to, getSettingsForBranchesAndRegionsInHierarchy() finished." );
        return settings;
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
}