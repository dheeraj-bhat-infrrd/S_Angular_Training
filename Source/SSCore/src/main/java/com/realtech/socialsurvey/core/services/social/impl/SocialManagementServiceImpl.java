package com.realtech.socialsurvey.core.services.social.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.ExternalSurveyTrackerDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.RegionDao;
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
import com.realtech.socialsurvey.core.entities.ComplaintResolutionSettings;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.EntityMediaPostResponseDetails;
import com.realtech.socialsurvey.core.entities.ExternalSurveyTracker;
import com.realtech.socialsurvey.core.entities.FacebookPage;
import com.realtech.socialsurvey.core.entities.FacebookToken;
import com.realtech.socialsurvey.core.entities.LinkedInToken;
import com.realtech.socialsurvey.core.entities.MediaPostDetails;
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
import com.realtech.socialsurvey.core.enums.SurveyErrorCode;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsSetter;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.social.SocialMediaExceptionHandler;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.CommonUtils;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
import com.realtech.socialsurvey.core.vo.SurveyPreInitiationList;
import com.realtech.socialsurvey.core.vo.UserList;
import com.realtech.socialsurvey.core.workbook.utils.WorkbookData;
import com.realtech.socialsurvey.core.workbook.utils.WorkbookOperations;

import facebook4j.Account;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.PostUpdate;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


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
    private Utils utils;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private SolrSearchService solrSearchService;

    @Autowired
    private BatchTrackerService batchTrackerService;

    @Autowired
    private UserProfileDao userProfileDao;

    @Autowired
    private UserDao userDao;


    @Resource
    @Qualifier ( "branch")
    private BranchDao branchDao;

    @Autowired
    private RegionDao regionDao;

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

    @Autowired
    private SocialMediaExceptionHandler socialMediaExceptionHandler;

    @Autowired
    private URLGenerator urlGenerator;

    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String applicationAdminEmail;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String applicationAdminName;

    // Facebook
    @Value ( "${FB_CLIENT_ID}")
    private String facebookClientId;
    @Value ( "${FB_CLIENT_SECRET}")
    private String facebookAppSecret;
    @Value ( "${FB_URI}")
    private String facebookUri;
    @Value ( "${FB_SCOPE}")
    private String facebookScope;
    @Value ( "${FB_REDIRECT_URI}")
    private String facebookRedirectUri;
    @Value ( "${FB_REDIRECT_URI_FOR_MAIL}")
    private String facebookRedirectUriForMail;
    @Value ( "${FB_GRAPH_URI}")
    private String fbGraphUrl;


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
    @Value ( "${LINKED_IN_API_KEY}")
    private String linkedInApiKey;
    @Value ( "${LINKED_IN_API_SECRET}")
    private String linkedInApiSecret;
    @Value ( "${LINKED_IN_REDIRECT_URI}")
    private String linkedinRedirectUri;
    @Value ( "${LINKED_IN_AUTH_URI}")
    private String linkedinAuthUri;
    @Value ( "${LINKED_IN_SCOPE}")
    private String linkedinScope;

    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;

    @Value ( "${LINKED_IN_REDIRECT_URI_FOR_MAIL}")
    private String linkedinREdirectUriForMail;

    @Value ( "${APPLICATION_LOGO_URL}")
    private String applicationLogoUrl;

    @Value ( "${APPLICATION_LOGO_LINKEDIN_URL}")
    private String applicationLogoUrlForLinkedin;

    @Value ( "${CUSTOM_SOCIALNETWORK_POST_COMPANY_ID}")
    private String customisedSocialNetworkCompanyId;

    @Value ( "${GSF_SOCIALNETWORK_POST_COMPANY_ID}")
    private String gsfSocialNetworkCompanyId;

    @Value ( "${ZILLOW_AUTO_POST_THRESHOLD}")
    private int zillowAutoPostThreshold;

    @Autowired
    private SocialPostDao socialPostDao;

    @Autowired
    private ZillowTempPostDao zillowTempPostDao;

    @Autowired
    private ExternalSurveyTrackerDao externalSurveyTrackerDao;

    @Autowired
    private WorkbookOperations workbookOperations;

    @Autowired
    private WorkbookData workbookData;

    @Autowired
    private CommonUtils commonUtils;

    private final int batchSize = 50;

    private static final String STYLE_ATTR = "align=\"center\"style=\"display:block; width: 150px; height: 40px; line-height: 40px;float: left; margin: 5px ;text-decoration:none;background: #009FE3; border-bottom: 2px solid #077faf; color: #fff; text-align: center; border-radius: 3px; font-size: 15px;border: 0;\"";

    @Value ( "${FB_CLIENT_ID}")
    private String fbAppId;


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
    public Facebook getFacebookInstanceByCallBackUrl( String callBackUrl )
    {
        facebook4j.conf.ConfigurationBuilder confBuilder = new facebook4j.conf.ConfigurationBuilder();
        confBuilder.setOAuthAppId( facebookClientId );
        confBuilder.setOAuthAppSecret( facebookAppSecret );
        confBuilder.setOAuthCallbackURL( callBackUrl );
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
    public SocialMediaTokens updateSocialMediaTokens( String collection, long iden, SocialMediaTokens mediaTokens )
        throws InvalidInputException
    {
        if ( mediaTokens == null ) {
            throw new InvalidInputException( "Social Tokens passed can not be null" );
        }
        LOG.info( "Updating Social Tokens information" );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden(
            MongoOrganizationUnitSettingDaoImpl.KEY_SOCIAL_MEDIA_TOKENS, mediaTokens, iden, collection );
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
    public boolean updateStatusIntoFacebookPage( OrganizationUnitSettings settings, String message, String serverBaseUrl,
        long companyId, String completeProfileUrl ) throws InvalidInputException, FacebookException
    {
        if ( settings == null ) {
            throw new InvalidInputException( "AgentSettings can not be null" );
        }
        LOG.info( "Updating Social Tokens information" );
        boolean facebookNotSetup = true;
        Facebook facebook = getFacebookInstance( serverBaseUrl );
        if ( settings != null ) {
            if ( settings.getSocialMediaTokens() != null ) {
                if ( settings.getSocialMediaTokens().getFacebookToken() != null
                    && settings.getSocialMediaTokens().getFacebookToken().getFacebookAccessToken() != null ) {
                    if ( settings.getSocialMediaTokens().getFacebookToken().getFacebookAccessTokenToPost() != null )
                        facebook.setOAuthAccessToken( new AccessToken(
                            settings.getSocialMediaTokens().getFacebookToken().getFacebookAccessTokenToPost(), null ) );
                    else
                        facebook.setOAuthAccessToken( new AccessToken(
                            settings.getSocialMediaTokens().getFacebookToken().getFacebookAccessToken(), null ) );
                    try {
                        facebookNotSetup = false;

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
                    twitter.setOAuthAccessToken( new twitter4j.auth.AccessToken(
                        agentSettings.getSocialMediaTokens().getTwitterToken().getTwitterAccessToken(),
                        agentSettings.getSocialMediaTokens().getTwitterToken().getTwitterAccessTokenSecret() ) );
                    try {
                        twitterNotSetup = false;
                        if ( companyId == Long.parseLong( customisedSocialNetworkCompanyId ) ) {
                            message = message.replace( "@SocialSurveyMe", "#REMAXagentreviews" );
                        } else if ( companyId == Long.parseLong( gsfSocialNetworkCompanyId ) ) {
                            message = String.format( CommonConstants.GSF_TWITTER_MESSAGE,
                                agentSettings.getCompleteProfileUrl() );
                            message = message.replaceAll( "null", "" );
                        }
                        StatusUpdate statusUpdate = new StatusUpdate( message );
                        if ( companyId == Long.parseLong( customisedSocialNetworkCompanyId ) ) {
                            try {
                                statusUpdate.setMedia( "Picture",
                                    new URL( "https://don7n2as2v6aa.cloudfront.net/remax-twitter-image.jpg?imgmax=800" )
                                        .openStream() );
                            } catch ( MalformedURLException e ) {
                                LOG.error( "error while posting image on twitter: " + e.getMessage(), e );
                            } catch ( IOException e ) {
                                LOG.error( "error while posting image on twitter: " + e.getMessage(), e );
                            }
                        }
                        twitter.updateStatus( statusUpdate );
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
    public boolean updateLinkedin( OrganizationUnitSettings settings, String collectionName, String message,
        String linkedinProfileUrl, String linkedinMessageFeedback, OrganizationUnitSettings companySettings, boolean isZillow,
        AgentSettings agentSettings, SocialMediaPostResponse linkedinPostResponse, String surveyId ) throws NonFatalException
    {
        if ( settings == null ) {
            throw new InvalidInputException( "AgentSettings can not be null" );
        }
        boolean linkedinNotSetup = true;
        LOG.info( "updateLinkedin() started." );
        if ( settings != null ) {
            if ( settings.getSocialMediaTokens() != null ) {
                if ( settings.getSocialMediaTokens().getLinkedInToken() != null
                    && settings.getSocialMediaTokens().getLinkedInToken().getLinkedInAccessToken() != null ) {
                    linkedinNotSetup = false;
                    String linkedInPost = new StringBuilder( linkedInRestApiUri ).substring( 0,
                        linkedInRestApiUri.length() - 1 );
                    linkedInPost += "/shares?oauth2_access_token="
                        + settings.getSocialMediaTokens().getLinkedInToken().getLinkedInAccessToken();
                    linkedInPost += "&format=json";
                    //SS-585 begin
                    String reviewId = Integer.toHexString( String.valueOf( System.currentTimeMillis() ).hashCode() )
                        + Integer.toHexString( String.valueOf( settings.getIden() ).hashCode() );
                    linkedInPost += "&reviewid=" + reviewId;
                    //SS-585 end
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
                        String profileUrl = "";
                        if( surveyId != null && !surveyId.isEmpty()){
                            profileUrl  = surveyHandler.getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL
                                + agentSettings.getProfileUrl() + "/" + String.valueOf( surveyId ) ;
                        }
                        else{
                            profileUrl = surveyHandler.getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL                
                            + agentSettings.getProfileUrl();
                        }
                        message = StringEscapeUtils.escapeXml( message );

                        message = message.replace( "&amp;lmnlf;", "\\n" ).replace( "&amp;dash;", "\\u2014" );

                        String linkedPostJSON = "{\"comment\": \"" + message + "\",\"content\": {" + "\"title\": \"" + title
                            + "\"," + "\"description\": \"" + description + "\"," + "\"submitted-url\": \"" + profileUrl
                            + "\",  " + "\"submitted-image-url\": \"" + imageUrl + "\"},"
                            + "\"visibility\": {\"code\": \"anyone\" }}";
                        StringEntity entity = new StringEntity( linkedPostJSON );
                        post.setEntity( entity );
                        try {
                            HttpResponse response = client.execute( post );
                            String responseString = response.toString();
                            LOG.info( "Server response while posting on linkedin : " + responseString );
                            JSONObject entityUpdateResponseObj = new JSONObject( EntityUtils.toString( response.getEntity() ) );

                            if ( response.getStatusLine() != null
                                && response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED ) {
                                //call social media error handler for linkedin exception
                                socialMediaExceptionHandler.handleLinkedinException( settings, collectionName );
                            }

                            if ( responseString.contains( "201 Created" ) ) {
                                String updateUrl = (String) entityUpdateResponseObj.get( "updateUrl" );
                                linkedinPostResponse.setReferenceUrl( updateUrl );
                                linkedinPostResponse.setResponseMessage( "Ok" );
                            } else {
                                linkedinPostResponse.setResponseMessage( (String) entityUpdateResponseObj.get( "message" ) );
                            }
                            linkedinPostResponse
                                .setAccessToken( settings.getSocialMediaTokens().getLinkedInToken().getLinkedInAccessToken() );
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
        LOG.info(
            "Method to get settings of branches and regions current agent belongs to, getSettingsForBranchesAndRegionsInHierarchy() started." );
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
        LOG.info(
            "Method to get settings of branches and regions current agent belongs to, getSettingsForBranchesAndRegionsInHierarchy() finished." );

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
        LOG.info(
            "Method to get settings of branches and regions current agent belongs to, getSettingsForBranchesAndRegionsInHierarchy() started." );
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
        LOG.info(
            "Method to get settings of branches and regions current agent belongs to, getSettingsForBranchesAndRegionsInHierarchy() finished." );

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
                    settings.add( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
                        profile.getCompany().getCompanyId(), CommonConstants.REGION_NAME_COLUMN ) );
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
    public OrganizationUnitSettings disconnectSocialNetwork( String socialMedia, boolean removeFeed,
        OrganizationUnitSettings unitSettings, String collectionName ) throws InvalidInputException
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
                if ( removeFeed ) {
                    //Remove from SOCIAL_POST
                    removeFromSocialPosts( collectionName, unitSettings.getIden(), socialMedia );
                }
                break;

            case CommonConstants.TWITTER_SOCIAL_SITE:
                profileStage.setOrder( ProfileStages.TWITTER_PRF.getOrder() );
                profileStage.setProfileStageKey( ProfileStages.TWITTER_PRF.name() );
                keyToUpdate = MongoOrganizationUnitSettingDaoImpl.KEY_TWITTER_SOCIAL_MEDIA_TOKEN;
                if ( removeFeed ) {
                    //Remove from SOCIAL_POST
                    removeFromSocialPosts( collectionName, unitSettings.getIden(), socialMedia );
                }
                break;

            case CommonConstants.GOOGLE_SOCIAL_SITE:
                profileStage.setOrder( ProfileStages.GOOGLE_PRF.getOrder() );
                profileStage.setProfileStageKey( ProfileStages.GOOGLE_PRF.name() );
                keyToUpdate = MongoOrganizationUnitSettingDaoImpl.KEY_GOOGLE_SOCIAL_MEDIA_TOKEN;
                if ( removeFeed ) {
                    //Remove from SOCIAL_POST
                    removeFromSocialPosts( collectionName, unitSettings.getIden(), socialMedia );
                }
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

            case CommonConstants.GOOGLE_BUSINESS_SOCIAL_SITE:
                ignore = true;
                keyToUpdate = MongoOrganizationUnitSettingDaoImpl.KEY_GOOGLE_BUSINESS_SOCIAL_MEDIA_TOKEN;
                break;

            default:
                throw new InvalidInputException( "Invalid social media token entered" );
        }

        OrganizationUnitSettings organizationUnitSettings = organizationUnitSettingsDao
            .removeKeyInOrganizationSettings( unitSettings, keyToUpdate, collectionName );

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


    void removeFromSocialPosts( String collectionName, long entityId, String source ) throws InvalidInputException
    {
        LOG.info( "Method to remove social posts started for collectionName : " + collectionName + " entityId : " + entityId
            + " source : " + source );
        if ( collectionName == null || collectionName.isEmpty() )
            throw new InvalidInputException( "Entity type cannot be empty" );
        if ( entityId <= 0 )
            throw new InvalidInputException( "Invalid entity Id" );
        if ( source == null || source.isEmpty() )
            throw new InvalidInputException( "Source cannot be empty" );

        String entityType = null;
        switch ( collectionName ) {
            case CommonConstants.AGENT_SETTINGS_COLLECTION:
                entityType = CommonConstants.AGENT_ID_COLUMN;
                break;
            case CommonConstants.BRANCH_SETTINGS_COLLECTION:
                entityType = CommonConstants.BRANCH_ID_COLUMN;
                break;
            case CommonConstants.REGION_SETTINGS_COLLECTION:
                entityType = CommonConstants.REGION_ID_COLUMN;
                break;
            case CommonConstants.COMPANY_SETTINGS_COLLECTION:
                entityType = CommonConstants.COMPANY_ID_COLUMN;
                break;
            default:
                throw new InvalidInputException( "Invalid entity type :" + entityType );
        }

        //Remove from mongo
        socialPostDao.removeSocialPostsForEntityAndSource( entityType, entityId, source );

        //Remove from solr
        try {
            solrSearchService.removeSocialPostsFromSolr( entityType, entityId, source );
        } catch ( SolrException e ) {
            throw new InvalidInputException( "A Solr exception occurred while removing social posts. Reason : ", e );
        }

        LOG.info( "Method to remove social posts finished for collectionName : " + collectionName + " entityId : " + entityId
            + " source : " + source );
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
    public RegionMediaPostResponseDetails getRMPRDFromRMPRDList(
        List<RegionMediaPostResponseDetails> regionMediaPostResponseDetailsList, long regionId )
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
    public BranchMediaPostResponseDetails getBMPRDFromBMPRDList(
        List<BranchMediaPostResponseDetails> branchMediaPostResponseDetailsList, long branchId )
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
     * @param agentId
     * @return
     */
    private String getProfileUrlOfPrimaryEntityOfAgent( long agentId )
    {

        LOG.info( "method getProfileUrlOfPrimaryEntityOfAgent started for agent with id " + agentId );
        String profileurl = null;
        OrganizationUnitSettings primaryProfileSetting = null;
        Map<String, Long> profile = null;
        try {
            profile = userManagementService.getPrimaryUserProfileByAgentId( agentId );

            if ( profile != null ) {
                Branch branch = branchDao.findById( Branch.class, profile.get( CommonConstants.BRANCH_ID_COLUMN ) );
                if ( branch.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_NO ) {
                    primaryProfileSetting = organizationManagementService.getBranchSettingsDefault( branch.getBranchId() );

                } else {
                    Region region = regionDao.findById( Region.class, profile.get( CommonConstants.REGION_ID_COLUMN ) );
                    if ( region.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_NO ) {
                        primaryProfileSetting = organizationManagementService.getRegionSettings( region.getRegionId() );
                    } else {
                        primaryProfileSetting = organizationManagementService
                            .getCompanySettings( profile.get( CommonConstants.COMPANY_ID_COLUMN ) );
                    }
                }
            }
            if ( primaryProfileSetting != null )
                profileurl = primaryProfileSetting.getCompleteProfileUrl();

        } catch ( ProfileNotFoundException | InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "No profile found for user with id " + agentId );
        }
        LOG.info( "method getProfileUrlOfPrimaryEntityOfAgent completed for agent with id " + agentId );
        return profileurl;

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
        SocialMediaPostDetails socialMediaPostDetails, SocialMediaPostResponseDetails socialMediaPostResponseDetails,
        boolean isZillow, boolean isAgentsHidden ) throws InvalidInputException, NoRecordsFetchedException
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

        String updatedFacebookMessage = facebookMessage;

        long agentId = socialMediaPostDetails.getAgentMediaPostDetails().getAgentId();
        AgentSettings agentSettings = userManagementService.getUserSettings( agentId );


        //get profile url
        String profileurl = agentSettings.getCompleteProfileUrl();
        //if company is hidden than show the url of the entity where user is assigned
        if ( isAgentsHidden ) {
            String priamryProfileUrl = getProfileUrlOfPrimaryEntityOfAgent(
                socialMediaPostDetails.getAgentMediaPostDetails().getAgentId() );
            if ( !StringUtils.isBlank( priamryProfileUrl ) )
                profileurl = priamryProfileUrl;
        }


        //Post for agent
        //do not post for agents if agents are hiiden
        if ( !isAgentsHidden ) {
            if ( socialMediaPostDetails.getAgentMediaPostDetails() != null ) {
                if ( agentSettings != null ) {
                    User agent = userManagementService.getUserByUserId( agentSettings.getIden() );
                    if ( agent != null && agent.getStatus() != CommonConstants.STATUS_INACTIVE ) {
                        postToFacebookForAHierarchy( companyId, agentId, profileurl, facebookMessage, updatedFacebookMessage,
                            rating, serverBaseUrl, agentSettings, MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
                            socialMediaPostDetails.getAgentMediaPostDetails(), agentMediaPostResponseDetails, isZillow,
                            isAgentsHidden );
                    }
                }
            }
        }


        if ( accountMasterId != CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {

            //Post for company
            if ( socialMediaPostDetails.getCompanyMediaPostDetails() != null ) {
                OrganizationUnitSettings companySetting = organizationManagementService
                    .getCompanySettings( socialMediaPostDetails.getCompanyMediaPostDetails().getCompanyId() );
                if ( companySetting != null ) {
                    Company company = organizationManagementService.getCompanyById( companySetting.getIden() );
                    if ( company != null && company.getStatus() != CommonConstants.STATUS_INACTIVE ) {
                        postToFacebookForAHierarchy( companyId, agentId, profileurl, facebookMessage, updatedFacebookMessage,
                            rating, serverBaseUrl, companySetting,
                            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION,
                            socialMediaPostDetails.getCompanyMediaPostDetails(), companyMediaPostResponseDetails, isZillow,
                            isAgentsHidden );
                    }
                }
            }


            //Post for regions
            for ( RegionMediaPostDetails regionMediaPostDetails : socialMediaPostDetails.getRegionMediaPostDetailsList() ) {
                if ( regionMediaPostDetails != null ) {

                    OrganizationUnitSettings setting = organizationManagementService
                        .getRegionSettings( regionMediaPostDetails.getRegionId() );
                    if ( setting != null ) {
                        Region region = userManagementService.getRegionById( setting.getIden() );
                        if ( region != null && region.getStatus() != CommonConstants.STATUS_INACTIVE ) {
                            RegionMediaPostResponseDetails regionMediaPostResponseDetails = getRMPRDFromRMPRDList(
                                regionMediaPostResponseDetailsList, regionMediaPostDetails.getRegionId() );
                            postToFacebookForAHierarchy( companyId, agentId, profileurl, facebookMessage,
                                updatedFacebookMessage, rating, serverBaseUrl, setting,
                                MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionMediaPostDetails,
                                regionMediaPostResponseDetails, isZillow, isAgentsHidden );
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
                    Branch branch = userManagementService.getBranchById( setting.getIden() );
                    if ( branch != null && branch.getStatus() != CommonConstants.STATUS_INACTIVE ) {
                        if ( setting != null ) {
                            postToFacebookForAHierarchy( companyId, agentId, profileurl, facebookMessage,
                                updatedFacebookMessage, rating, serverBaseUrl, setting,
                                MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchMediaPostDetails,
                                branchMediaPostResponseDetails, isZillow, isAgentsHidden );
                        }
                    }
                }

            }
        }

        LOG.debug( "Method postToFacebookForHierarchy() ended" );
    }


    /**
     * 
     * @param companyId
     * @param profileUrl
     * @param facebookMessage
     * @param updatedFacebookMessage
     * @param rating
     * @param serverBaseUrl
     * @param setting
     * @param collectionType
     * @param mediaPostDetails
     * @param mediaPostResponseDetails
     * @param isZillow
     * @param isAgentsHidden
     * @throws InvalidInputException
     */
    void postToFacebookForAHierarchy( long companyId, long agentId, String profileUrl, String facebookMessage,
        String updatedFacebookMessage, double rating, String serverBaseUrl, OrganizationUnitSettings setting,
        String collectionType, MediaPostDetails mediaPostDetails, EntityMediaPostResponseDetails mediaPostResponseDetails,
        boolean isZillow, boolean isAgentsHidden ) throws InvalidInputException
    {
        try {

            if ( surveyHandler.canPostOnSocialMedia( setting, rating ) ) {
                if ( !isZillow ) {
                    String profileUrlWithMessage = getClientCompanyProfileUrlForAgentToPostInSocialMedia( agentId, setting,
                        collectionType );
                    if ( profileUrlWithMessage == null || profileUrlWithMessage.isEmpty() ) {
                        profileUrlWithMessage = setting.getCompleteProfileUrl() + "/";
                    } else {
                        profileUrlWithMessage = profileUrlWithMessage + ".";
                    }
                    updatedFacebookMessage = facebookMessage + profileUrlWithMessage;
                }
                if ( !updateStatusIntoFacebookPage( setting, updatedFacebookMessage, serverBaseUrl, companyId, profileUrl ) ) {
                    List<String> socialList = mediaPostDetails.getSharedOn();
                    if ( !socialList.contains( CommonConstants.FACEBOOK_SOCIAL_SITE ) ) {
                        socialList.add( CommonConstants.FACEBOOK_SOCIAL_SITE );
                    }
                    mediaPostDetails.setSharedOn( socialList );

                    SocialMediaPostResponse facebookPostResponse = new SocialMediaPostResponse();
                    facebookPostResponse
                        .setAccessToken( setting.getSocialMediaTokens().getFacebookToken().getFacebookAccessTokenToPost() );
                    facebookPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                    facebookPostResponse.setResponseMessage( "Ok" );
                    if ( mediaPostResponseDetails.getFacebookPostResponseList() == null ) {
                        mediaPostResponseDetails.setFacebookPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                    }
                    mediaPostResponseDetails.getFacebookPostResponseList().add( facebookPostResponse );
                }
            }
        } catch ( FacebookException e ) {
            LOG.error( "FacebookException caught in postToSocialMedia() while trying to post to facebook. Nested excption is ",
                e );
            //call social media error handler for facebook exception
            socialMediaExceptionHandler.handleFacebookException( e, setting, collectionType );
            //update Social Media Post Response
            SocialMediaPostResponse facebookPostResponse = new SocialMediaPostResponse();
            facebookPostResponse
                .setAccessToken( setting.getSocialMediaTokens().getFacebookToken().getFacebookAccessTokenToPost() );
            facebookPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
            facebookPostResponse.setResponseMessage( e.getMessage() );
            if ( mediaPostResponseDetails.getFacebookPostResponseList() == null )
                mediaPostResponseDetails.setFacebookPostResponseList( new ArrayList<SocialMediaPostResponse>() );
            mediaPostResponseDetails.getFacebookPostResponseList().add( facebookPostResponse );
            reportBug( "Facebook", setting.getProfileName(), e );
        }
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
        SocialMediaPostResponseDetails socialMediaPostResponseDetails, OrganizationUnitSettings companySettings,
        boolean isZillow, boolean isAgentsHidden, String surveyId ) throws InvalidInputException, NoRecordsFetchedException
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

        String updatedLinkedInMessage = linkedinMessage;

        AgentSettings agentSettings = userManagementService
            .getUserSettings( socialMediaPostDetails.getAgentMediaPostDetails().getAgentId() );

        //Post for agent
        //do not post for agents ig agents are hiiden for comapny
        if ( !isAgentsHidden ) {
            if ( socialMediaPostDetails.getAgentMediaPostDetails() != null ) {

                if ( agentSettings != null ) {
                    User agent = userManagementService.getUserByUserId( agentSettings.getIden() );
                    if ( agent != null && agent.getStatus() != CommonConstants.STATUS_INACTIVE ) {
                        postToLinkedInForAHierarchy( agentSettings,
                            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, rating, isZillow,
                            updatedLinkedInMessage, linkedinMessage, linkedinProfileUrl, linkedinMessageFeedback,
                            companySettings, agentSettings, socialMediaPostDetails.getAgentMediaPostDetails(),
                            agentMediaPostResponseDetails, surveyId );
                    }
                }
            }
        }

        if ( accountMasterId != CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {

            //Post for company
            if ( socialMediaPostDetails.getCompanyMediaPostDetails() != null ) {
                OrganizationUnitSettings companySetting = organizationManagementService
                    .getCompanySettings( socialMediaPostDetails.getCompanyMediaPostDetails().getCompanyId() );
                if ( companySetting != null ) {
                    Company company = userManagementService.getCompanyById( companySetting.getIden() );
                    if ( company != null && company.getStatus() != CommonConstants.STATUS_INACTIVE ) {
                        postToLinkedInForAHierarchy( companySetting,
                            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, rating, isZillow,
                            updatedLinkedInMessage, linkedinMessage, linkedinProfileUrl, linkedinMessageFeedback,
                            companySettings, agentSettings, socialMediaPostDetails.getCompanyMediaPostDetails(),
                            companyMediaPostResponseDetails, surveyId );
                    }
                }
            }


            //Post for regions
            for ( RegionMediaPostDetails regionMediaPostDetails : socialMediaPostDetails.getRegionMediaPostDetailsList() ) {
                if ( regionMediaPostDetails != null ) {
                    OrganizationUnitSettings setting = organizationManagementService
                        .getRegionSettings( regionMediaPostDetails.getRegionId() );

                    if ( setting != null ) {
                        RegionMediaPostResponseDetails regionMediaPostResponseDetails = getRMPRDFromRMPRDList(
                            regionMediaPostResponseDetailsList, regionMediaPostDetails.getRegionId() );
                        Region region = userManagementService.getRegionById( setting.getIden() );
                        if ( region != null && region.getStatus() != CommonConstants.STATUS_INACTIVE ) {
                            postToLinkedInForAHierarchy( setting,
                                MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, rating, isZillow,
                                updatedLinkedInMessage, linkedinMessage, linkedinProfileUrl, linkedinMessageFeedback,
                                companySettings, agentSettings, regionMediaPostDetails, regionMediaPostResponseDetails, surveyId );
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
                        Branch branch = userManagementService.getBranchById( setting.getIden() );
                        if ( branch != null && branch.getStatus() != CommonConstants.STATUS_INACTIVE ) {
                            postToLinkedInForAHierarchy( setting,
                                MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, rating, isZillow,
                                updatedLinkedInMessage, linkedinMessage, linkedinProfileUrl, linkedinMessageFeedback,
                                companySettings, agentSettings, branchMediaPostDetails, branchMediaPostResponseDetails, surveyId );
                        }
                    }
                }
            }
        }

        LOG.debug( "Method postToLinkedInForHierarchy() ended" );
    }


    void postToLinkedInForAHierarchy( OrganizationUnitSettings setting, String collectionName, Double rating, boolean isZillow,
        String updatedLinkedInMessage, String linkedinMessage, String linkedinProfileUrl, String linkedinMessageFeedback,
        OrganizationUnitSettings companySettings, AgentSettings agentSettings, MediaPostDetails mediaPostDetails,
        EntityMediaPostResponseDetails mediaPostResponseDetails, String surveyId )
    {
        try {
            if ( surveyHandler.canPostOnSocialMedia( setting, rating ) ) {
                if ( !isZillow ) {
                    String profileUrlWithMessage = getClientCompanyProfileUrlForAgentToPostInSocialMedia(
                        agentSettings.getIden(), setting, collectionName );
                    if ( profileUrlWithMessage == null || profileUrlWithMessage.isEmpty() ) {
                        profileUrlWithMessage = setting.getCompleteProfileUrl() + "/.";
                    } else {
                        profileUrlWithMessage = profileUrlWithMessage + ".";
                    }
                    updatedLinkedInMessage = linkedinMessage + profileUrlWithMessage;
                }

                SocialMediaPostResponse linkedinPostResponse = new SocialMediaPostResponse();
                linkedinPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );

                if ( !updateLinkedin( setting, collectionName, updatedLinkedInMessage, linkedinProfileUrl,
                    linkedinMessageFeedback, companySettings, isZillow, agentSettings, linkedinPostResponse, surveyId ) ) {
                    List<String> socialList = mediaPostDetails.getSharedOn();
                    if ( !socialList.contains( CommonConstants.LINKEDIN_SOCIAL_SITE ) )
                        socialList.add( CommonConstants.LINKEDIN_SOCIAL_SITE );
                    mediaPostDetails.setSharedOn( socialList );


                    if ( mediaPostResponseDetails.getLinkedinPostResponseList() == null )
                        mediaPostResponseDetails.setLinkedinPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                    mediaPostResponseDetails.getLinkedinPostResponseList().add( linkedinPostResponse );
                }
            }
        } catch ( Exception e ) {
            //update SocialMediaPostResponse object
            SocialMediaPostResponse linkedinPostResponse = new SocialMediaPostResponse();
            linkedinPostResponse.setAccessToken( setting.getSocialMediaTokens().getLinkedInToken().getLinkedInAccessToken() );
            linkedinPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
            linkedinPostResponse.setResponseMessage( e.getMessage() );
            if ( mediaPostResponseDetails.getLinkedinPostResponseList() == null )
                mediaPostResponseDetails.setLinkedinPostResponseList( new ArrayList<SocialMediaPostResponse>() );
            mediaPostResponseDetails.getLinkedinPostResponseList().add( linkedinPostResponse );

            reportBug( "Linkedin", setting.getProfileName(), e );
        }
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
        SocialMediaPostDetails socialMediaPostDetails, SocialMediaPostResponseDetails socialMediaPostResponseDetails,
        boolean isAgentsHidden ) throws InvalidInputException, NoRecordsFetchedException
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
        //do not post for agent if agent ar hiiden for company
        if ( !isAgentsHidden ) {
            if ( socialMediaPostDetails.getAgentMediaPostDetails() != null ) {
                AgentSettings agentSettings = userManagementService
                    .getUserSettings( socialMediaPostDetails.getAgentMediaPostDetails().getAgentId() );
                if ( agentSettings != null ) {
                    User agent = userManagementService.getUserByUserId( agentSettings.getIden() );
                    if ( agent != null && agent.getStatus() != CommonConstants.STATUS_INACTIVE ) {
                        postToTwitterForAHierarchy( agentSettings, rating, companyId, twitterMessage,
                            socialMediaPostDetails.getAgentMediaPostDetails(), agentMediaPostResponseDetails );
                    }
                }
            }
        }


        if ( accountMasterId != CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {

            //Post for company
            if ( socialMediaPostDetails.getCompanyMediaPostDetails() != null ) {
                OrganizationUnitSettings companySetting = organizationManagementService
                    .getCompanySettings( socialMediaPostDetails.getCompanyMediaPostDetails().getCompanyId() );
                if ( companySetting != null ) {
                    Company company = userManagementService.getCompanyById( companySetting.getIden() );
                    if ( company != null && company.getStatus() != CommonConstants.STATUS_INACTIVE ) {
                        postToTwitterForAHierarchy( companySetting, rating, companyId, twitterMessage,
                            socialMediaPostDetails.getCompanyMediaPostDetails(), companyMediaPostResponseDetails );
                    }
                }
            }


            //post for regions
            for ( RegionMediaPostDetails regionMediaPostDetails : socialMediaPostDetails.getRegionMediaPostDetailsList() ) {
                if ( regionMediaPostDetails != null ) {
                    OrganizationUnitSettings setting = organizationManagementService
                        .getRegionSettings( regionMediaPostDetails.getRegionId() );
                    if ( setting != null ) {
                        RegionMediaPostResponseDetails regionMediaPostResponseDetails = getRMPRDFromRMPRDList(
                            regionMediaPostResponseDetailsList, regionMediaPostDetails.getRegionId() );
                        Region region = userManagementService.getRegionById( setting.getIden() );
                        if ( region != null && region.getStatus() != CommonConstants.STATUS_INACTIVE ) {
                            postToTwitterForAHierarchy( setting, rating, companyId, twitterMessage, regionMediaPostDetails,
                                regionMediaPostResponseDetails );
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
                        Branch branch = userManagementService.getBranchById( setting.getIden() );
                        if ( branch != null && branch.getStatus() != CommonConstants.STATUS_INACTIVE ) {
                            postToTwitterForAHierarchy( setting, rating, companyId, twitterMessage, branchMediaPostDetails,
                                branchMediaPostResponseDetails );
                        }
                    }

                }

            }
        }

        LOG.debug( "Method postToTwitterForHierarchy() ended" );
    }


    public void postToTwitterForAHierarchy( OrganizationUnitSettings setting, Double rating, long companyId,
        String twitterMessage, MediaPostDetails mediaPostDetails, EntityMediaPostResponseDetails mediaPostResponseDetails )
        throws InvalidInputException
    {
        try {
            if ( surveyHandler.canPostOnSocialMedia( setting, rating ) ) {
                if ( !tweet( setting, twitterMessage, companyId ) ) {
                    List<String> socialList = mediaPostDetails.getSharedOn();
                    if ( !socialList.contains( CommonConstants.TWITTER_SOCIAL_SITE ) )
                        socialList.add( CommonConstants.TWITTER_SOCIAL_SITE );
                    mediaPostDetails.setSharedOn( socialList );

                    SocialMediaPostResponse twitterPostResponse = new SocialMediaPostResponse();
                    twitterPostResponse
                        .setAccessToken( setting.getSocialMediaTokens().getTwitterToken().getTwitterAccessToken() );
                    twitterPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
                    twitterPostResponse.setResponseMessage( "Ok" );
                    if ( mediaPostResponseDetails.getTwitterPostResponseList() == null )
                        mediaPostResponseDetails.setTwitterPostResponseList( new ArrayList<SocialMediaPostResponse>() );
                    mediaPostResponseDetails.getTwitterPostResponseList().add( twitterPostResponse );
                }
            }
        } catch ( TwitterException e ) {
            LOG.error( "TwitterException caught in postToSocialMedia() while trying to post to twitter. Nested excption is ",
                e );

            SocialMediaPostResponse twitterPostResponse = new SocialMediaPostResponse();
            twitterPostResponse.setAccessToken( setting.getSocialMediaTokens().getTwitterToken().getTwitterAccessToken() );
            twitterPostResponse.setPostDate( new Date( System.currentTimeMillis() ) );
            twitterPostResponse.setResponseMessage( e.getMessage() );
            if ( mediaPostResponseDetails.getTwitterPostResponseList() == null )
                mediaPostResponseDetails.setTwitterPostResponseList( new ArrayList<SocialMediaPostResponse>() );
            mediaPostResponseDetails.getTwitterPostResponseList().add( twitterPostResponse );

            reportBug( "Twitter", setting.getProfileName(), e );
        }
    }


    /*
     * (non-Javadoc)
     * @see com.realtech.socialsurvey.core.services.social.SocialManagementService#postToSocialMedia(java.lang.String, java.lang.String, java.lang.String, java.lang.String, long, double, java.lang.String, java.lang.String, boolean, java.lang.String, boolean)
     */
    @Override
    public boolean postToSocialMedia( String agentName, String agentProfileLink, String custFirstName, String custLastName,
        long agentId, double rating, String surveyId, String feedback, boolean isAbusive, String serverBaseUrl,
        boolean onlyPostToSocialSurvey ) throws NonFatalException
    {

        LOG.info( "Method to post feedback of customer to various pages of social networking sites started." );
        boolean successfullyPosted = true;

        //format rating
        rating = surveyHandler.getFormattedSurveyScore( rating );

        if ( agentProfileLink == null || agentProfileLink.isEmpty() ) {
            throw new InvalidInputException(
                "Invalid parameter passed : passed input parameter agentProfileLink is null or empty" );
        }

        try {
            String customerDisplayName = emailFormatHelper.getCustomerDisplayNameForEmail( custFirstName, custLastName );
            User agent = userManagementService.getUserByUserId( agentId );

            if ( agent.getCompany() == null || agent.getCompany().getStatus() == CommonConstants.STATUS_INACTIVE
                || agent.getStatus() == CommonConstants.STATUS_INACTIVE )
                return true;

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

            SurveyDetails surveyDetails = surveyHandler.getSurveyDetails( surveyId );
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
                companyMediaPostResponseDetails
                    .setCompanyId( socialMediaPostDetails.getCompanyMediaPostDetails().getCompanyId() );
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
                if ( getBMPRDFromBMPRDList( branchMediaPostResponseDetailsList,
                    branchMediaPostDetails.getBranchId() ) == null ) {
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
                if ( getRMPRDFromRMPRDList( regionMediaPostResponseDetailsList,
                    regionMediaPostDetails.getRegionId() ) == null ) {
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


            // Do not show full name of agent if agents are hidden for the company
            OrganizationUnitSettings companySetting = companySettings.get( 0 );
            boolean isCompanyAgentHidden = companySetting.isHiddenSection();
            if ( isCompanyAgentHidden ) {
                agentName = commonUtils.getAgentNameForHiddenAgentCompany( agent.getFirstName(), agent.getLastName() );
            }

            //if onlyPostToSocialSurvey is false than only post on the social media otherwise just add social survey channel in social media post list
            if ( !isAbusive && !onlyPostToSocialSurvey ) {
                //Facebook message
                String facebookMessage = buildFacebookAutoPostMessage( customerDisplayName, agentName, rating, feedback,
                    surveyHandler.getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink + "/",
                    false );

                postToFacebookForHierarchy( facebookMessage, rating, serverBaseUrl, accountMasterId, socialMediaPostDetails,
                    socialMediaPostResponseDetails, false, isCompanyAgentHidden );


                //LinkedIn message
                String linkedinMessage = buildLinkedInAutoPostMessage( customerDisplayName, agentName, rating, feedback,
                    surveyHandler.getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink, false );

                String linkedinProfileUrl = surveyHandler.getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL
                    + agentProfileLink;
                String linkedinMessageFeedback = "From : " + customerDisplayName + " - " + feedback;

                postToLinkedInForHierarchy( linkedinMessage, rating, linkedinProfileUrl, linkedinMessageFeedback,
                    accountMasterId, socialMediaPostDetails, socialMediaPostResponseDetails, companySettings.get( 0 ), false,
                    isCompanyAgentHidden, surveyId );

                // Twitter message
                OrganizationUnitSettings agentSettings = organizationManagementService.getAgentSettings( agentId );
                String profileUrlWithMessage = getClientCompanyProfileUrlForAgentToPostInSocialMedia( agentId, agentSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                if ( profileUrlWithMessage == null || profileUrlWithMessage.isEmpty() ) {
                    profileUrlWithMessage = surveyHandler.getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL
                        + agentProfileLink;
                }
                String twitterMessage = buildTwitterAutoPostMessage( customerDisplayName, agentName, rating, feedback,
                    profileUrlWithMessage, false );
                postToTwitterForHierarchy( twitterMessage, rating, serverBaseUrl, accountMasterId, socialMediaPostDetails,
                    socialMediaPostResponseDetails, isCompanyAgentHidden );

            }

            surveyDetails.setSocialMediaPostResponseDetails( socialMediaPostResponseDetails );

            socialMediaPostDetails.getAgentMediaPostDetails().setSharedOn( agentSocialList );
            socialMediaPostDetails.getCompanyMediaPostDetails().setSharedOn( companySocialList );
            surveyDetails.setSocialMediaPostDetails( socialMediaPostDetails );
            surveyHandler.updateSurveyDetailsBySurveyId( surveyDetails );


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

            case CommonConstants.GOOGLE_BUSINESS_SOCIAL_SITE:
                if ( ( mediaTokens.getGoogleBusinessToken() != null )
                    && ( mediaTokens.getGoogleBusinessToken().getGoogleBusinessLink() != null )
                    && !( mediaTokens.getGoogleBusinessToken().getGoogleBusinessLink().isEmpty() ) )
                    socialUpdateAction.setLink( mediaTokens.getGoogleBusinessToken().getGoogleBusinessLink() );
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
                disconnectSocialNetwork( socialMedia, true, unitSettings, collection );
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
                disconnectSocialNetwork( socialMedia, true, unitSettings, collection );
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
                disconnectSocialNetwork( socialMedia, true, unitSettings, collection );
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
                disconnectSocialNetwork( socialMedia, true, unitSettings, collection );
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
                disconnectSocialNetwork( socialMedia, true, unitSettings, collection );
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
                disconnectSocialNetwork( socialMedia, true, unitSettings, collection );
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
                disconnectSocialNetwork( socialMedia, true, unitSettings, collection );
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
                disconnectSocialNetwork( socialMedia, true, unitSettings, collection );
                //Update settings set status
                updateSettingsSetStatusByEntityType( entityType, entityId, settings, unset );
                //update social connections history
                updateSocialConnectionsHistory( entityType, entityId, mediaTokens, socialMedia,
                    CommonConstants.SOCIAL_MEDIA_DISCONNECTED );
            }
            if ( mediaTokens.getGoogleBusinessToken() != null ) {
                String socialMedia = CommonConstants.GOOGLE_BUSINESS_SOCIAL_SITE;
                SettingsForApplication settings = SettingsForApplication.GOOGLE_BUSINESS;
                //disconnect social network in mongo
                disconnectSocialNetwork( socialMedia, true, unitSettings, collection );
                //Update settings set status
                updateSettingsSetStatusByEntityType( entityType, entityId, settings, unset );
                //update social connections history
                updateSocialConnectionsHistory( entityType, entityId, mediaTokens, socialMedia,
                    CommonConstants.SOCIAL_MEDIA_DISCONNECTED );
            }

            //Finally unset SocialMediaTokens
            removeSocialMediaTokens( unitSettings, collection );

        } catch ( ProfileNotFoundException e ) {
            throw new InvalidInputException(
                "Profile not found for entityType : " + entityType + " and entityID : " + entityId );
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
        String reviewUrl, double rating, int autoPostStatus, int complaintResolutionStatus, Timestamp reviewDate,
        String postedOn )
    {
        externalSurveyTrackerDao.saveExternalSurveyTracker( entityColumnName, entityId, source, sourceLink, reviewUrl, rating,
            autoPostStatus, complaintResolutionStatus, reviewDate, postedOn );
    }


    @Override
    public void removeProcessedZillowTempPosts( List<Long> processedZillowTempPostIds )
    {
        zillowTempPostDao.removeProcessedZillowTempPosts( processedZillowTempPostIds );
    }


    @Transactional
    @Override
    public SurveyPreInitiationList getUnmatchedPreInitiatedSurveys( long companyId, int startIndex, int batchSize , long count)
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
        //function shd be called only once
        if(count == -1){
            surveyPreInitiationListVO.setTotalRecord( surveyPreInitiationDao.getUnmatchedPreInitiatedSurveyCount( companyId ) );
        }else{
            surveyPreInitiationListVO.setTotalRecord( count );
        }
        return surveyPreInitiationListVO;
    }


    @Transactional
    @Override
    public SurveyPreInitiationList getProcessedPreInitiatedSurveys( long companyId, int startIndex, int batchSize , long count)
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
        if( count == -1 ){
            surveyPreInitiationListVO.setTotalRecord( surveyPreInitiationDao.getProcessedPreInitiatedSurveyCount( companyId ) );
        }else{
            surveyPreInitiationListVO.setTotalRecord( count );
        }
        return surveyPreInitiationListVO;
    }


    @Override
    public void updateAgentIdOfSurveyPreinitiationRecordsForEmail( User user, String emailAddress ) throws InvalidInputException
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


    @Override
    public void updateSurveyPreinitiationRecordsAsIgnored( String emailAddress ) throws InvalidInputException
    {
        if ( emailAddress == null || emailAddress.isEmpty() ) {
            throw new InvalidInputException( " Wrong parameter passed : emailAddress is null oe empty " );
        }

        LOG.debug( "method getProcessedPreInitiatedSurveys called for email id : " + emailAddress );
        surveyPreInitiationDao.updateSurveyPreinitiationRecordsAsIgnored( emailAddress );
    }


    @Override
    public String buildFacebookAutoPostMessage( String customerDisplayName, String agentName, double rating, String feedback,
        String linkUrl, boolean isZillow )
    {
        // String facebookMessage = customerDisplayName + " gave " + agentName + " a " + ratingFormat.format( rating )
        //    + "-star review on" + ( isZillow ? " Zillow via" : " " ) + "SocialSurvey saying : \"" + feedback
        //    + "\".\nView this and more at " + linkUrl + "/";
        String facebookMessage = rating + " Star Review on " + ( isZillow ? "Zillow" : "SocialSurvey" ) + " \u2014 " + feedback
            + " by " + customerDisplayName + " for " + agentName + "\n" + ( isZillow ? linkUrl : "" );
        return facebookMessage;
    }


    @Override
    public String buildLinkedInAutoPostMessage( String customerDisplayName, String agentName, double rating, String feedback,
        String linkUrl, boolean isZillow )
    {
        String linkedInComment = feedback != null && feedback.length() > 500 ? feedback.substring( 0, 500 ) : feedback;
        linkedInComment = feedback != null && feedback.length() > 500
            ? ( linkedInComment.substring( 0, linkedInComment.lastIndexOf( " " ) ) + " ..." ) : linkedInComment;

        // String linkedinMessage = customerDisplayName + " gave " + agentName + " a " + ratingFormat.format( rating )
        //    + "-star review on" + ( isZillow ? " Zillow via " : " " ) + "SocialSurvey saying : \"" + linkedInComment
        //    + "\". View this and more at " + linkUrl;
        String linkedinMessage = rating + " Star Review on " + ( isZillow ? "Zillow" : "SocialSurvey" ) + " &dash; "
            + linkedInComment + " by " + customerDisplayName + " for " + agentName + "&lmnlf;" + ( isZillow ? linkUrl : "" );
        return linkedinMessage;
    }


    @Override
    public String buildTwitterAutoPostMessage( String customerDisplayName, String agentName, double rating, String feedback,
        String linkUrl, boolean isZillow )
    {
        // String twitterMessage = customerDisplayName + " gave " + agentName + " a " + ratingFormat.format( rating )
        //    + "-star review" + ( isZillow ? " @Zillow via " : " " ) + "@SocialSurveyMe. "
        //    + linkUrl;
        String twitterMessage = rating + " Star Review on " + ( isZillow ? "#Zillow" : "#SocialSurvey" ) + " by "
            + customerDisplayName + " for " + agentName + "\n" + linkUrl;
        return twitterMessage;
    }


    @Override
    public Map<Long, List<SocialUpdateAction>> getSocialConnectionsHistoryForEntities( String entityType, List<Long> entityIds )
        throws InvalidInputException, ProfileNotFoundException
    {
        //Check if any of the parameters are null or empty
        if ( entityType == null || entityType.isEmpty() ) {
            throw new InvalidInputException( "Invalid entity type. EntityType : " + entityType );
        }

        if ( entityIds == null || entityIds.isEmpty() ) {
            throw new InvalidInputException( "Invalid entity ID. Entity ID : " + entityIds );
        }

        Map<Long, List<SocialUpdateAction>> socialUpdatesMap = new HashMap<Long, List<SocialUpdateAction>>();

        List<SocialUpdateAction> socialUpdatesList = socialPostDao.getSocialConnectionHistoryByEntityIds( entityType,
            entityIds );
        for ( Long entityId : entityIds ) {
            socialUpdatesMap.put( entityId, new ArrayList<SocialUpdateAction>() );
        }
        List<SocialUpdateAction> curSocialUpdateList;
        for ( SocialUpdateAction curSocialUpdate : socialUpdatesList ) {
            curSocialUpdateList = socialUpdatesMap.get( curSocialUpdate.getAgentId() );
            curSocialUpdateList.add( curSocialUpdate );
        }

        return socialUpdatesMap;
    }


    @Override
    @Transactional
    public SurveyPreInitiationList getCorruptPreInitiatedSurveys( long companyId, int startIndex, int batchSize , long count )
        throws InvalidInputException
    {
        LOG.debug( "method getCorruptPreInitiatedSurveys called for company id : " + companyId );
        SurveyPreInitiationList surveyPreInitiationListVO = new SurveyPreInitiationList();
        if ( companyId <= 0 ) {
            throw new InvalidInputException( " Wrong parameter passed : companyId is invalid " );
        }

        List<SurveyPreInitiation> surveyPreInitiations = surveyPreInitiationDao.getCorruptPreInitiatedSurveys( companyId,
            startIndex, batchSize );
        for ( SurveyPreInitiation survey : surveyPreInitiations ) {
            if ( survey.getErrorCode() != null )
                survey.setErrorCodeDescription( SurveyErrorCode.valueOf( survey.getErrorCode() ).getValue() );
            else
                survey.setErrorCodeDescription( SurveyErrorCode.NOT_KNOWN.getValue() );
        }
        surveyPreInitiationListVO.setSurveyPreInitiationList( surveyPreInitiations );
        if( count == -1 ){
            surveyPreInitiationListVO.setTotalRecord( surveyPreInitiationDao.getCorruptPreInitiatedSurveyCount( companyId ) );
        }else{
            surveyPreInitiationListVO.setTotalRecord( count );
        }
        return surveyPreInitiationListVO;
    }


    @Override
    @Transactional
    public XSSFWorkbook getUserSurveyReportByTabId( int tabId, long companyId )
        throws InvalidInputException, NoRecordsFetchedException
    {
        SurveyPreInitiationList surveyPreInitiationListVO = null;
        Map<Integer, List<Object>> data = null;
        XSSFWorkbook workbook = null;
        if ( tabId == CommonConstants.UNMATCHED_USER_TABID ) {
            surveyPreInitiationListVO = this.getUnmatchedPreInitiatedSurveys( companyId, -1, -1, -1 );
            data = workbookData.getUnmatchedOrProcessedSurveyDataToBeWrittenInSheet( surveyPreInitiationListVO );
        } else if ( tabId == CommonConstants.PROCESSED_USER_TABID ) {
            surveyPreInitiationListVO = this.getProcessedPreInitiatedSurveys( companyId, -1, -1, -1 );
            data = workbookData.getUnmatchedOrProcessedSurveyDataToBeWrittenInSheet( surveyPreInitiationListVO );
        } else if ( tabId == CommonConstants.MAPPED_USER_TABID ) {
            UserList userList = new UserList();
            userList = userManagementService.getUsersAndEmailMappingForCompany( companyId, -1, -1, -1 );
            data = workbookData.getMappedSurveyDataToBeWrittenInSheet( userList );
        } else if ( tabId == CommonConstants.CORRUPT_USER_TABID ) {
            surveyPreInitiationListVO = this.getCorruptPreInitiatedSurveys( companyId, -1, -1 , -1 );
            data = workbookData.getCorruptSurveyDataToBeWrittenInSheet( surveyPreInitiationListVO );
        }
        workbook = workbookOperations.createWorkbook( data );
        return workbook;
    }


    @Override
    public void imcompleteSocialPostReminderSender()
    {
        try {
            //update last run start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_INCOMPLETE_SOCIAL_POST_REMINDER_SENDER,
                CommonConstants.BATCH_NAME_INCOMPLETE_SOCIAL_POST_REMINDER_SENDER );

            StringBuilder links = new StringBuilder();
            User user = null;
            for ( Company company : organizationManagementService.getAllCompanies() ) {
                List<SurveyDetails> incompleteSocialPostCustomers = surveyHandler
                    .getIncompleteSocialPostSurveys( company.getCompanyId() );
                for ( SurveyDetails survey : incompleteSocialPostCustomers ) {
                    // To fetch settings of agents/closest in the hierarchy
                    Map<String, String> socialSitesWithSettings = new HashMap<>();
                    try {
                        socialSitesWithSettings = getSocialSitesWithSettingsConfigured( survey );
                    } catch ( InvalidInputException e ) {
                        LOG.error( "InvalidInputException caught in executeInternal() for SocialpostReminderMail" );
                        continue;
                    }

                    links = new StringBuilder();
                    try {
                        user = userManagementService.getUserByUserId( survey.getAgentId() );
                    } catch ( InvalidInputException e ) {
                        LOG.error( "InvalidInputException occured while fetch agent settings/ user details for user id "
                            + survey.getAgentId() + ". Nested exception is ", e );
                        continue;
                    }
                    links.append( "<div style=\"width: 320px;margin: auto;clear:both;\">" );
                    if ( socialSitesWithSettings.get( CommonConstants.REALTOR_LABEL ) != null ) {
                        links.append(
                            "<a " + STYLE_ATTR + " href=" + socialSitesWithSettings.get( CommonConstants.REALTOR_LABEL ) + ">"
                                + CommonConstants.REALTOR_LABEL + "</a>" );
                    }
                    if ( socialSitesWithSettings.get( CommonConstants.LENDING_TREE_LABEL ) != null ) {
                        links.append(
                            "<a " + STYLE_ATTR + " href=" + socialSitesWithSettings.get( CommonConstants.LENDING_TREE_LABEL )
                                + ">" + CommonConstants.LENDING_TREE_LABEL + "</a>" );
                    }
                    if ( socialSitesWithSettings.get( CommonConstants.ZILLOW_LABEL ) != null ) {
                        links
                            .append( "<a " + STYLE_ATTR + " href=" + socialSitesWithSettings.get( CommonConstants.ZILLOW_LABEL )
                                + ">" + CommonConstants.ZILLOW_LABEL + "</a>" );
                    }
                    if ( socialSitesWithSettings.get( CommonConstants.YELP_LABEL ) != null ) {
                        links.append( "<a " + STYLE_ATTR + " href=" + socialSitesWithSettings.get( CommonConstants.YELP_LABEL )
                            + ">" + CommonConstants.YELP_LABEL + "</a>" );
                    }
                    if ( socialSitesWithSettings.get( CommonConstants.GOOGLE_PLUS_LABEL ) != null ) {
                        links.append(
                            "<a " + STYLE_ATTR + " href=" + socialSitesWithSettings.get( CommonConstants.GOOGLE_PLUS_LABEL )
                                + ">" + CommonConstants.GOOGLE_PLUS_LABEL + "</a>" );
                    }
                    if ( socialSitesWithSettings.get( CommonConstants.LINKEDIN_LABEL ) != null ) {
                        links.append(
                            "<a " + STYLE_ATTR + " href=" + socialSitesWithSettings.get( CommonConstants.LINKEDIN_LABEL ) + ">"
                                + CommonConstants.LINKEDIN_LABEL + "</a>" );
                    }
                    if ( socialSitesWithSettings.get( CommonConstants.TWITTER_LABEL ) != null ) {
                        links.append(
                            "<a " + STYLE_ATTR + " href=" + socialSitesWithSettings.get( CommonConstants.TWITTER_LABEL ) + ">"
                                + CommonConstants.TWITTER_LABEL + "</a>" );
                    }
                    if ( socialSitesWithSettings.get( CommonConstants.FACEBOOK_LABEL ) != null ) {
                        links.append(
                            "<a " + STYLE_ATTR + " href=" + socialSitesWithSettings.get( CommonConstants.FACEBOOK_LABEL ) + ">"
                                + CommonConstants.FACEBOOK_LABEL + "</a>" );
                    }
                    links.append( "</div>" );
                    // Send email to complete social post for survey to each customer.
                    if ( !links.toString().isEmpty() ) {
                        try {
                            surveyHandler.sendSocialPostReminderMail( survey.getCustomerEmail(), survey.getCustomerFirstName(),
                                survey.getCustomerLastName(), user, links.toString() );
                            surveyHandler.updateReminderCountForSocialPosts( survey.getAgentId(), survey.getCustomerEmail() );
                        } catch ( InvalidInputException | UndeliveredEmailException | ProfileNotFoundException e ) {
                            LOG.error(
                                "Exception caught in IncompleteSurveyReminderSender.main while trying to send reminder mail to "
                                    + survey.getCustomerFirstName() + " for completion of survey. Nested exception is ",
                                e );
                            continue;
                        }
                    }
                }
            }

            //Update last build time in batch tracker table
            batchTrackerService
                .updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_INCOMPLETE_SOCIAL_POST_REMINDER_SENDER );

        } catch ( Exception e ) {
            LOG.error( "Error in IncompleteSocialPostReminderSender", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType(
                    CommonConstants.BATCH_TYPE_INCOMPLETE_SOCIAL_POST_REMINDER_SENDER, e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError(
                    CommonConstants.BATCH_NAME_INCOMPLETE_SOCIAL_POST_REMINDER_SENDER, System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in IncompleteSocialPostReminderSender " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    private Map<String, String> getSocialSitesWithSettingsConfigured( SurveyDetails survey ) throws InvalidInputException
    {
        LOG.debug(
            "Method to get settings of agent and admins in the hierarchy getSocialSitesWithSettingsConfigured() started." );
        long agentId = survey.getAgentId();
        OrganizationUnitSettings agentSettings = userManagementService.getUserSettings( agentId );
        Map<String, List<OrganizationUnitSettings>> settingsMap = getSettingsForBranchesRegionsAndCompanyInAgentsHierarchy(
            agentId );
        List<OrganizationUnitSettings> companySettings = settingsMap
            .get( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
        List<OrganizationUnitSettings> regionSettings = settingsMap
            .get( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
        List<OrganizationUnitSettings> branchSettings = settingsMap
            .get( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
        Map<String, String> socialSiteUrlMap = new HashMap<String, String>();

        // Enabling Zillow, Realtor, Lending tree, Google Business and Yelp from agent settings
        if ( agentSettings != null ) {
            if ( agentSettings.getSocialMediaTokens() != null ) {
                if ( agentSettings.getSocialMediaTokens().getRealtorToken() != null ) {
                    socialSiteUrlMap.put( CommonConstants.REALTOR_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.REALTOR_LABEL, agentSettings ) );
                }
                if ( agentSettings.getSocialMediaTokens().getLendingTreeToken() != null ) {
                    socialSiteUrlMap.put( CommonConstants.LENDING_TREE_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.LENDING_TREE_LABEL, agentSettings ) );
                }
                if ( agentSettings.getSocialMediaTokens().getZillowToken() != null ) {
                    socialSiteUrlMap.put( CommonConstants.ZILLOW_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.ZILLOW_LABEL, agentSettings ) );
                }
                if ( agentSettings.getSocialMediaTokens().getYelpToken() != null ) {
                    socialSiteUrlMap.put( CommonConstants.YELP_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.YELP_LABEL, agentSettings ) );
                }
                if ( agentSettings.getSocialMediaTokens().getGoogleBusinessToken() != null ) {
                    socialSiteUrlMap.put( CommonConstants.GOOGLE_BUSINESS_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.GOOGLE_BUSINESS_LABEL, agentSettings ) );
                }
            }
        }

        // Enabling Zillow, Realtor, Lending tree, Google Business and Yelp if anyone closest in hierarchy to agent has
        // configured in settings.
        for ( OrganizationUnitSettings setting : branchSettings ) {
            if ( setting.getSocialMediaTokens() != null ) {
                if ( setting.getSocialMediaTokens().getRealtorToken() != null
                    && socialSiteUrlMap.get( CommonConstants.REALTOR_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.REALTOR_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.REALTOR_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getLendingTreeToken() != null
                    && socialSiteUrlMap.get( CommonConstants.LENDING_TREE_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.LENDING_TREE_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.LENDING_TREE_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getZillowToken() != null
                    && socialSiteUrlMap.get( CommonConstants.ZILLOW_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.ZILLOW_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.ZILLOW_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getYelpToken() != null
                    && socialSiteUrlMap.get( CommonConstants.YELP_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.YELP_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.YELP_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getGoogleBusinessToken() != null
                    && socialSiteUrlMap.get( CommonConstants.GOOGLE_BUSINESS_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.GOOGLE_BUSINESS_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.GOOGLE_BUSINESS_LABEL, setting ) );
                }
            }
        }
        for ( OrganizationUnitSettings setting : regionSettings ) {
            if ( setting.getSocialMediaTokens() != null ) {
                if ( setting.getSocialMediaTokens().getRealtorToken() != null
                    && socialSiteUrlMap.get( CommonConstants.REALTOR_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.REALTOR_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.REALTOR_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getLendingTreeToken() != null
                    && socialSiteUrlMap.get( CommonConstants.LENDING_TREE_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.LENDING_TREE_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.LENDING_TREE_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getZillowToken() != null
                    && socialSiteUrlMap.get( CommonConstants.ZILLOW_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.ZILLOW_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.ZILLOW_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getYelpToken() != null
                    && socialSiteUrlMap.get( CommonConstants.YELP_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.YELP_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.YELP_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getGoogleBusinessToken() != null
                    && socialSiteUrlMap.get( CommonConstants.GOOGLE_BUSINESS_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.GOOGLE_BUSINESS_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.GOOGLE_BUSINESS_LABEL, setting ) );
                }
            }
        }
        for ( OrganizationUnitSettings setting : companySettings ) {
            if ( setting.getSocialMediaTokens() != null ) {
                if ( setting.getSocialMediaTokens().getRealtorToken() != null
                    && socialSiteUrlMap.get( CommonConstants.REALTOR_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.REALTOR_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.REALTOR_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getLendingTreeToken() != null
                    && socialSiteUrlMap.get( CommonConstants.LENDING_TREE_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.LENDING_TREE_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.LENDING_TREE_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getZillowToken() != null
                    && socialSiteUrlMap.get( CommonConstants.ZILLOW_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.ZILLOW_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.ZILLOW_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getYelpToken() != null
                    && socialSiteUrlMap.get( CommonConstants.YELP_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.YELP_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.YELP_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getGoogleBusinessToken() != null
                    && socialSiteUrlMap.get( CommonConstants.GOOGLE_BUSINESS_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.GOOGLE_BUSINESS_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.GOOGLE_BUSINESS_LABEL, setting ) );
                }
            }
        }

        // build social site url's like Google Plus, LinkedIn, Twitter and Facebook
        socialSiteUrlMap.put( CommonConstants.GOOGLE_PLUS_LABEL,
            generateSocialSiteUrl( survey, CommonConstants.GOOGLE_PLUS_LABEL, agentSettings ) );
        socialSiteUrlMap.put( CommonConstants.LINKEDIN_LABEL,
            generateSocialSiteUrl( survey, CommonConstants.LINKEDIN_LABEL, agentSettings ) );
        socialSiteUrlMap.put( CommonConstants.TWITTER_LABEL,
            generateSocialSiteUrl( survey, CommonConstants.TWITTER_LABEL, agentSettings ) );
        socialSiteUrlMap.put( CommonConstants.FACEBOOK_LABEL,
            generateSocialSiteUrl( survey, CommonConstants.FACEBOOK_LABEL, agentSettings ) );
        LOG.debug( "Method getSocialSitesWithSettingsConfigured() finished" );
        return socialSiteUrlMap;
    }


    private String generateSocialSiteUrl( SurveyDetails survey, String socialSite,
        OrganizationUnitSettings organizationUnitSettings ) throws InvalidInputException
    {
        LOG.debug( "Method to generate URL for social sites, generateSocialSiteUrl() started." );
        double fmt_Rating = surveyHandler.getFormattedSurveyScore( survey.getScore() );
        String url = "";
        String customerDisplayName = new EmailFormatHelper().getCustomerDisplayNameForEmail( survey.getCustomerFirstName(),
            survey.getCustomerLastName() );
        String reviewText = fmt_Rating + "-star response from " + customerDisplayName + " for " + survey.getAgentName()
            + " at SocialSurvey - " + survey.getReview();
        reviewText = utils.urlEncodeText( reviewText );
        switch ( socialSite ) {
            case CommonConstants.GOOGLE_BUSINESS_LABEL:
                url = organizationUnitSettings.getSocialMediaTokens().getGoogleBusinessToken().getGoogleBusinessLink();
                break;
            case CommonConstants.REALTOR_LABEL:
                url = organizationUnitSettings.getSocialMediaTokens().getRealtorToken().getRealtorProfileLink()
                    + "#reviews-section";
                break;
            case CommonConstants.LENDING_TREE_LABEL:
                url = organizationUnitSettings.getSocialMediaTokens().getLendingTreeToken().getLendingTreeProfileLink();
                break;
            case CommonConstants.ZILLOW_LABEL:
                url = organizationUnitSettings.getSocialMediaTokens().getZillowToken().getZillowProfileLink();
                break;
            case CommonConstants.YELP_LABEL:
                url = organizationUnitSettings.getSocialMediaTokens().getYelpToken().getYelpPageLink();
                break;
            case CommonConstants.GOOGLE_PLUS_LABEL:
                url = "https://plus.google.com/share?url=" + organizationUnitSettings.getCompleteProfileUrl();
                break;
            case CommonConstants.LINKEDIN_LABEL:
                url += "https://www.linkedin.com/shareArticle?mini=true&url="
                    + organizationUnitSettings.getCompleteProfileUrl() + "/" + survey.get_id() + "&title=&summary=" + reviewText + "&source=";
                break;
            case CommonConstants.TWITTER_LABEL:
                url += "https://twitter.com/intent/tweet?text=" + reviewText + ".&url="
                    + organizationUnitSettings.getCompleteProfileUrl();
                break;
            case CommonConstants.FACEBOOK_LABEL:
                url += "https://www.facebook.com/dialog/feed?app_id=" + fbAppId + "&link="
                    + organizationUnitSettings.getCompleteProfileUrl() + "&description=" + reviewText
                    + ".&redirect_uri=https://www.facebook.com";
                break;
        }

        LOG.debug( "Method to generate URL for social sites, generateSocialSiteUrl() ended." );
        return url;
    }


    @Override
    public void zillowReviewProcessorStarter()
    {
        try {
            //update last run start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_ZILLOW_REVIEW_PROCESSOR_AND_AUTO_POSTER,
                CommonConstants.BATCH_NAME_ZILLOW_REVIEW_PROCESSOR_AND_AUTO_POSTER );

            // Fetch All companies
            for ( Company company : organizationManagementService.getAllCompanies() ) {
                try {
                    long companyId = company.getCompanyId();
                    List<OrganizationUnitSettings> regionSettings = new ArrayList<OrganizationUnitSettings>();
                    List<OrganizationUnitSettings> branchSettings = new ArrayList<OrganizationUnitSettings>();
                    List<OrganizationUnitSettings> agentSettings = new ArrayList<OrganizationUnitSettings>();
                    Map<Long, OrganizationUnitSettings> agentIdSettingsMap = new HashMap<Long, OrganizationUnitSettings>();

                    // find all users connected to zillow
                    LOG.debug( "Fetching settings of agents connected to zillow under company id : " + companyId );
                    int start = 0;
                    List<Long> batchUserIdList = new ArrayList<Long>();
                    do {
                        batchUserIdList = organizationManagementService.getAgentIdsUnderCompany( companyId, start, batchSize );
                        if ( batchUserIdList != null && batchUserIdList.size() > 0 ) {
                            // fetch zillow settings for these ids and add to list
                            List<OrganizationUnitSettings> currBatchUserSettings = organizationManagementService
                                .fetchUnitSettingsConnectedToZillow(
                                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, batchUserIdList );
                            if ( currBatchUserSettings != null && !currBatchUserSettings.isEmpty() ) {
                                agentSettings.addAll( currBatchUserSettings );
                            }
                        }
                        start += batchSize;
                    } while ( batchUserIdList != null && batchUserIdList.size() == batchSize );

                    // find all branches connected to zillow
                    LOG.debug( "Fetching settings of branches connected to zillow under company id : " + companyId );
                    start = 0;
                    List<Long> batchBranchIdList = new ArrayList<Long>();
                    do {
                        batchBranchIdList = organizationManagementService.getBranchIdsUnderCompany( companyId, start,
                            batchSize );
                        if ( batchBranchIdList != null && batchBranchIdList.size() > 0 ) {
                            // fetch zillow settings for these ids and add to list
                            List<OrganizationUnitSettings> currBatchBatchSettings = organizationManagementService
                                .fetchUnitSettingsConnectedToZillow(
                                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, batchBranchIdList );
                            if ( currBatchBatchSettings != null && !currBatchBatchSettings.isEmpty() ) {
                                branchSettings.addAll( currBatchBatchSettings );
                            }
                        }
                        start += batchSize;
                    } while ( batchBranchIdList != null && batchBranchIdList.size() == batchSize );

                    // find all regions connected to zillow.
                    LOG.debug( "Fetching settings of regions connected to zillow under company id : " + companyId );
                    start = 0;
                    List<Long> batchRegionIdList = new ArrayList<Long>();
                    do {
                        batchRegionIdList = organizationManagementService.getRegionIdsUnderCompany( companyId, start,
                            batchSize );
                        if ( batchRegionIdList != null && batchRegionIdList.size() > 0 ) {
                            // fetch zillow settings for these ids and add to list
                            List<OrganizationUnitSettings> currBatchRegionSettings = organizationManagementService
                                .fetchUnitSettingsConnectedToZillow(
                                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, batchRegionIdList );
                            if ( currBatchRegionSettings != null && !currBatchRegionSettings.isEmpty() ) {
                                regionSettings.addAll( currBatchRegionSettings );
                            }
                        }
                        start += batchSize;
                    } while ( batchRegionIdList != null && batchRegionIdList.size() == batchSize );


                    // Fetch Company Settings
                    OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( companyId );

                    // Fetch & save zillow reviews for agents
                    if ( agentSettings != null && !agentSettings.isEmpty() ) {
                        for ( OrganizationUnitSettings agentSetting : agentSettings ) {
                            agentIdSettingsMap.put( agentSetting.getIden(), agentSetting );
                            LOG.debug( "Fetching and saving zillow reviews for agent id : " + agentSetting.getIden() );
                            profileManagementService.fetchAndSaveZillowData( agentSetting,
                                MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, companyId, true, false );
                            LOG.debug( "Fetched and saved zillow reviews for agent id : " + agentSetting.getIden() );
                        }
                    }

                    // Fetch & save zillow reviews for branches
                    if ( branchSettings != null && !branchSettings.isEmpty() ) {
                        for ( OrganizationUnitSettings branchSetting : branchSettings ) {
                            LOG.debug( "Fetching and saving zillow reviews for branch id : " + branchSetting.getIden() );
                            profileManagementService.fetchAndSaveZillowData( branchSetting,
                                MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, companyId, true, false );
                            LOG.debug( "Fetched and saved zillow reviews for branch id : " + branchSetting.getIden() );
                        }
                    }

                    // Fetch & save zillow reviews for regions
                    if ( regionSettings != null && !regionSettings.isEmpty() ) {
                        for ( OrganizationUnitSettings regionSetting : regionSettings ) {
                            LOG.debug( "Fetching and saving zillow reviews for region id : " + regionSetting.getIden() );
                            profileManagementService.fetchAndSaveZillowData( regionSetting,
                                MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, companyId, true, false );
                            LOG.debug( "Fetched and saved zillow reviews for region id : " + regionSetting.getIden() );
                        }
                    }

                    // Fetch & save zillow reviews for company
                    if ( companySettings != null && companySettings.getSocialMediaTokens() != null
                        && companySettings.getSocialMediaTokens().getZillowToken() != null ) {
                        LOG.debug( "Fetching and saving zillow reviews for company id : " + companyId );
                        profileManagementService.fetchAndSaveZillowData( companySettings,
                            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companyId, true, false );
                        LOG.debug( "Fetched and saved zillow reviews for company id : " + companyId );
                    }

                    // Fetch all zillow data from temp table and trigger auto post
                    List<ZillowTempPost> zillowTempPostList = getAllZillowTempPosts();
                    List<Long> processedZillowTempPostIds = new ArrayList<Long>();
                    if ( zillowTempPostList != null && !zillowTempPostList.isEmpty() ) {
                        for ( ZillowTempPost zillowTempPost : zillowTempPostList ) {
                            try {
                                if ( zillowTempPost != null ) {

                                    //TODO : remove this . Temporary fix for Zillow review URl
                                    if ( StringUtils.isEmpty( zillowTempPost.getZillowReviewUrl() ) ) {
                                        zillowTempPost.setZillowReviewUrl( zillowTempPost.getZillowReviewSourceLink() );
                                    }


                                    // change this to support another units in hierarchy
                                    OrganizationUnitSettings agentSetting = agentIdSettingsMap
                                        .get( zillowTempPost.getEntityId() );
                                    SurveyDetails surveyDetails = surveyHandler
                                        .getSurveyDetails( zillowTempPost.getZillowSurveyId() );
                                    if ( checkReviewCanBePostedToSocialMedia( zillowTempPost, agentSetting, companySettings,
                                        surveyDetails ) ) {
                                        // post the zillow review to social media
                                        List<String> postedOnList = null;
                                        boolean autoPostSuccess = false;
                                        String postedOn = "";
                                        try {
                                            postedOnList = postToSocialMedia( zillowTempPost, agentSetting, surveyDetails,
                                                agentSetting );
                                        } catch ( Exception e ) {
                                            LOG.error( "Error occurred while posting to social media. Reason", e );
                                        }
                                        int postToSocialMedia = 0;
                                        if ( postedOnList != null && postedOnList.size() > 0 ) {
                                            postToSocialMedia = CommonConstants.YES;
                                            autoPostSuccess = true;
                                            postedOn = postedOnList.toString().replace( "[", "" ).replace( "]", "" )
                                                .replace( ", ", "," );
                                        }

                                        // check review for complaint resolution
                                        boolean complaintResStatus = triggerComplaintResolutionWorkflowForZillowReview(
                                            companySettings, zillowTempPost, surveyDetails, agentSetting, postToSocialMedia,
                                            postedOn );

                                        if ( !complaintResStatus && autoPostSuccess ) {
                                            // add to external survey tracker
                                            saveExternalSurveyTracker( zillowTempPost.getEntityColumnName(),
                                                zillowTempPost.getEntityId(), CommonConstants.SURVEY_SOURCE_ZILLOW,
                                                agentSetting.getSocialMediaTokens().getZillowToken().getZillowProfileLink(),
                                                zillowTempPost.getZillowReviewUrl(), zillowTempPost.getZillowReviewRating(),
                                                postToSocialMedia, CommonConstants.NO, zillowTempPost.getZillowReviewDate(),
                                                postedOn );
                                        }
                                    }
                                    // add to zillow temp post id to processed list
                                    processedZillowTempPostIds.add( zillowTempPost.getId() );
                                }
                            } catch ( Exception e ) {
                                LOG.error( "Error occurred while auto posting zillow review to social media", e );
                            }
                        }
                        // remove processed zillow temp posts
                        if ( !processedZillowTempPostIds.isEmpty() ) {
                            removeProcessedZillowTempPosts( processedZillowTempPostIds );
                        }
                    }

                } catch ( Exception e ) {
                    LOG.error( "Exception occurred while processing zillow for company id : " + company.getCompanyId() );
                    //update batch tracker with error message
                    batchTrackerService.updateErrorForBatchTrackerByBatchType(
                        CommonConstants.BATCH_TYPE_ZILLOW_REVIEW_PROCESSOR_AND_AUTO_POSTER, e.getMessage() );
                    //send report bug mail to admin
                    batchTrackerService.sendMailToAdminRegardingBatchError(
                        CommonConstants.BATCH_NAME_ZILLOW_REVIEW_PROCESSOR_AND_AUTO_POSTER, System.currentTimeMillis(), e );
                }
            }

            //Update last build time in batch tracker table
            batchTrackerService
                .updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_ZILLOW_REVIEW_PROCESSOR_AND_AUTO_POSTER );
        } catch ( Exception e ) {
            LOG.error( "Error in ZillowReviewFetchAndAutoPoster", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType(
                    CommonConstants.BATCH_TYPE_ZILLOW_REVIEW_PROCESSOR_AND_AUTO_POSTER, e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError(
                    CommonConstants.BATCH_NAME_ZILLOW_REVIEW_PROCESSOR_AND_AUTO_POSTER, System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in ZillowReviewFetchAndAutoPoster " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report exception mail to admin " );
            }
        }
    }


    /**
     * Method to check whether Zillow Review can be posted to social Media
     * */
    boolean checkReviewCanBePostedToSocialMedia( ZillowTempPost zillowTempPost, OrganizationUnitSettings unitSettings,
        OrganizationUnitSettings companySettings, SurveyDetails survey )
    {
        if ( zillowTempPost == null ) {
            LOG.error( "zillowTempPost passed cannot be null" );
            return false;
        }
        if ( unitSettings == null ) {
            LOG.error( "unitSettings passed cannot be null" );
            return false;
        }
        if ( survey == null ) {
            LOG.error( "survey passed cannot be null" );
            return false;
        }

        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.DATE, -zillowAutoPostThreshold );
        cal.set( Calendar.HOUR, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 0 );
        cal.set( Calendar.MILLISECOND, 0 );

        if ( checkExternalSurveyTrackerExist( zillowTempPost.getEntityColumnName(), zillowTempPost.getEntityId(),
            CommonConstants.SURVEY_SOURCE_ZILLOW, zillowTempPost.getZillowReviewUrl(),
            zillowTempPost.getZillowReviewDate() ) == null && unitSettings.getSurvey_settings() != null
            && !utils.checkReviewForSwearWords( zillowTempPost.getZillowReviewDescription(), surveyHandler.getSwearList() )
            && zillowTempPost.getZillowReviewDate().after( cal.getTime() ) ) {
            return true;
        }

        return false;
    }


    public List<String> postToSocialMedia( ZillowTempPost zillowTempPost, OrganizationUnitSettings organizationUnitSettings,
        SurveyDetails surveyDetails, OrganizationUnitSettings agentSettings ) throws NonFatalException
    {

        LOG.info( "Method to post feedback of customer to various pages of social networking sites started." );
        List<String> postedOnList = new ArrayList<String>();
        try {
            // add else to support other units in hierarchy
            if ( zillowTempPost.getEntityColumnName().equalsIgnoreCase( CommonConstants.AGENT_ID_COLUMN ) ) {
                long agentId = organizationUnitSettings.getIden();
                User agent = userManagementService.getUserByUserId( agentId );
                ContactDetailsSettings contactDetailSettings = organizationUnitSettings.getContact_details();
                String agentName = contactDetailSettings.getName();
                String customerDisplayName = emailFormatHelper
                    .getCustomerDisplayNameForEmail( surveyDetails.getCustomerFirstName(), "" );
                int accountMasterId = 0;
                try {
                    AccountsMaster masterAccount = agent.getCompany().getLicenseDetails().get( CommonConstants.INITIAL_INDEX )
                        .getAccountsMaster();
                    accountMasterId = masterAccount.getAccountsMasterId();
                } catch ( NullPointerException e ) {
                    LOG.error( "NullPointerException caught in postToSocialMedia() while fetching account master id for agent "
                        + agent.getFirstName() );
                }

                Map<String, List<OrganizationUnitSettings>> settingsMap = getSettingsForBranchesAndRegionsInHierarchy(
                    agentId );
                List<OrganizationUnitSettings> companySettings = settingsMap
                    .get( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                List<OrganizationUnitSettings> regionSettings = settingsMap
                    .get( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                List<OrganizationUnitSettings> branchSettings = settingsMap
                    .get( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );

                boolean doAutoPost = false;
                for ( OrganizationUnitSettings companySetting : companySettings ) {
                    if ( companySetting.isAllowZillowAutoPost() && !doAutoPost ) {
                        doAutoPost = companySetting.isAllowZillowAutoPost();
                    }
                }
                for ( OrganizationUnitSettings regionSetting : regionSettings ) {
                    if ( regionSetting.isAllowZillowAutoPost() && !doAutoPost ) {
                        doAutoPost = regionSetting.isAllowZillowAutoPost();
                    }
                }
                for ( OrganizationUnitSettings branchSetting : branchSettings ) {
                    if ( branchSetting.isAllowZillowAutoPost() && !doAutoPost ) {
                        doAutoPost = branchSetting.isAllowZillowAutoPost();
                    }
                }

                if ( agentSettings.isAllowZillowAutoPost() && !doAutoPost ) {
                    doAutoPost = agentSettings.isAllowZillowAutoPost();
                }


                // Since auto post flag is not set true in hierarchy
                if ( false || doAutoPost ) {
                    return postedOnList;
                }

                SocialMediaPostDetails socialMediaPostDetails = surveyHandler.getSocialMediaPostDetailsBySurvey( surveyDetails,
                    companySettings.get( 0 ), regionSettings, branchSettings );

                //create socialMediaPostResponseDetails object
                SocialMediaPostResponseDetails socialMediaPostResponseDetails = surveyDetails
                    .getSocialMediaPostResponseDetails();
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
                    companyMediaPostResponseDetails
                        .setCompanyId( socialMediaPostDetails.getCompanyMediaPostDetails().getCompanyId() );
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
                    if ( getBMPRDFromBMPRDList( branchMediaPostResponseDetailsList,
                        branchMediaPostDetails.getBranchId() ) == null ) {
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
                    if ( getRMPRDFromRMPRDList( regionMediaPostResponseDetailsList,
                        regionMediaPostDetails.getRegionId() ) == null ) {
                        regionMediaPostResponseDetailsList.add( regionMediaPostResponseDetails );
                    }
                }

                socialMediaPostResponseDetails.setAgentMediaPostResponseDetails( agentMediaPostResponseDetails );
                socialMediaPostResponseDetails.setCompanyMediaPostResponseDetails( companyMediaPostResponseDetails );
                socialMediaPostResponseDetails.setBranchMediaPostResponseDetailsList( branchMediaPostResponseDetailsList );
                socialMediaPostResponseDetails.setRegionMediaPostResponseDetailsList( regionMediaPostResponseDetailsList );

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

                String feedback = Jsoup.parse( zillowTempPost.getZillowReviewDescription() ).text();

                double rating = surveyHandler.getFormattedSurveyScore( zillowTempPost.getZillowReviewRating() );


                // Do not show full name of agent if agents are hidden for the company
                OrganizationUnitSettings companySetting = companySettings.get( 0 );
                boolean isCompanyAgentHidden = companySetting.isHiddenSection();
                if ( isCompanyAgentHidden ) {
                    agentName = commonUtils.getAgentNameForHiddenAgentCompany( agent.getFirstName(), agent.getLastName() );
                }

                //Facebook message
                String facebookMessage = buildFacebookAutoPostMessage( customerDisplayName, agentName, rating, feedback,
                    zillowTempPost.getZillowReviewUrl(), true );

                postToFacebookForHierarchy( facebookMessage, zillowTempPost.getZillowReviewRating(),
                    zillowTempPost.getZillowReviewUrl(), accountMasterId, socialMediaPostDetails,
                    socialMediaPostResponseDetails, true, isCompanyAgentHidden );


                // LinkedIn Message
                String linkedinMessage = buildLinkedInAutoPostMessage( customerDisplayName, agentName, rating, feedback,
                    zillowTempPost.getZillowReviewUrl(), true );

                String linkedinProfileUrl = zillowTempPost.getZillowReviewUrl();
                String linkedinMessageFeedback = "From : " + surveyDetails.getCustomerFirstName() + " - " + feedback;

                postToLinkedInForHierarchy( linkedinMessage, zillowTempPost.getZillowReviewRating(), linkedinProfileUrl,
                    linkedinMessageFeedback, accountMasterId, socialMediaPostDetails, socialMediaPostResponseDetails,
                    companySettings.get( 0 ), true, isCompanyAgentHidden, null );

                // Twitter message 
                String twitterMessage = buildTwitterAutoPostMessage( customerDisplayName, agentName, rating, feedback,
                    zillowTempPost.getZillowReviewUrl(), true );

                postToTwitterForHierarchy( twitterMessage, zillowTempPost.getZillowReviewRating(),
                    zillowTempPost.getZillowReviewUrl(), accountMasterId, socialMediaPostDetails,
                    socialMediaPostResponseDetails, isCompanyAgentHidden );


                surveyDetails.setSocialMediaPostResponseDetails( socialMediaPostResponseDetails );

                socialMediaPostDetails.getAgentMediaPostDetails().setSharedOn( agentSocialList );
                socialMediaPostDetails.getCompanyMediaPostDetails().setSharedOn( companySocialList );
                surveyDetails.setSocialMediaPostDetails( socialMediaPostDetails );
                surveyHandler.updateSurveyDetails( surveyDetails );

                // check if auto post triggered anywhere in hierarchy
                if ( agentSocialList != null && agentSocialList.size() > 0 ) {
                    for ( String agentSocialPostMedia : agentSocialList ) {
                        if ( !postedOnList.contains( agentSocialPostMedia ) ) {
                            postedOnList.add( agentSocialPostMedia );
                        }
                    }
                } else if ( companySocialList != null && companySocialList.size() > 0 ) {
                    for ( String companySocialPostMedia : companySocialList ) {
                        if ( !postedOnList.contains( companySocialPostMedia ) ) {
                            postedOnList.add( companySocialPostMedia );
                        }
                    }
                } else if ( socialMediaPostDetails != null && socialMediaPostDetails.getRegionMediaPostDetailsList() != null ) {
                    for ( RegionMediaPostDetails regionMediaPostDetailsList : socialMediaPostDetails
                        .getRegionMediaPostDetailsList() ) {
                        if ( regionMediaPostDetailsList != null && regionMediaPostDetailsList.getSharedOn() != null
                            && regionMediaPostDetailsList.getSharedOn().size() > 0 ) {
                            for ( String regionSocialPostMedia : regionMediaPostDetailsList.getSharedOn() ) {
                                if ( !postedOnList.contains( regionSocialPostMedia ) ) {
                                    postedOnList.add( regionSocialPostMedia );
                                }
                            }
                        }
                    }
                } else if ( socialMediaPostDetails != null && socialMediaPostDetails.getBranchMediaPostDetailsList() != null ) {
                    for ( BranchMediaPostDetails branchMediaPostDetails : socialMediaPostDetails
                        .getBranchMediaPostDetailsList() ) {
                        if ( branchMediaPostDetails != null && branchMediaPostDetails.getSharedOn() != null
                            && branchMediaPostDetails.getSharedOn().size() > 0 ) {
                            for ( String branchSocialPostMedia : branchMediaPostDetails.getSharedOn() ) {
                                if ( !postedOnList.contains( branchSocialPostMedia ) ) {
                                    postedOnList.add( branchSocialPostMedia );
                                }
                            }
                        }
                    }
                }
            }
        } catch ( NonFatalException e ) {
            LOG.error(
                "Non fatal Exception caught in postToSocialMedia() while trying to post to social networking sites. Nested excption is ",
                e );
            throw new NonFatalException( e.getMessage() );
        }
        LOG.info( "Method to post feedback of customer to various pages of social networking sites finished." );
        return postedOnList;
    }


    private boolean triggerComplaintResolutionWorkflowForZillowReview( OrganizationUnitSettings companySettings,
        ZillowTempPost zillowTempPost, SurveyDetails survey, OrganizationUnitSettings unitSettings, int autoPostSuccess,
        String postedOn )
    {
        LOG.info(
            "Method to trigger complaint resolution workflow for a review, triggerComplaintResolutionWorkflowForZillowReview started." );
        // trigger complaint resolution workflow if configured
        if ( companySettings.getSurvey_settings() != null
            && companySettings.getSurvey_settings().getComplaint_res_settings() != null ) {
            ComplaintResolutionSettings complaintRegistrationSettings = companySettings.getSurvey_settings()
                .getComplaint_res_settings();
            ExternalSurveyTracker externalSurveyTracker = checkExternalSurveyTrackerExist( zillowTempPost.getEntityColumnName(),
                zillowTempPost.getEntityId(), CommonConstants.SURVEY_SOURCE_ZILLOW, zillowTempPost.getZillowReviewUrl(),
                zillowTempPost.getZillowReviewDate() );
            if ( complaintRegistrationSettings.isEnabled()
                && ( ( zillowTempPost.getZillowReviewRating() > 0d && complaintRegistrationSettings.getRating() > 0d
                    && zillowTempPost.getZillowReviewRating() <= complaintRegistrationSettings.getRating() ) )
                && ( externalSurveyTracker == null
                    || externalSurveyTracker.getComplaintResolutionStatus() == CommonConstants.NO ) ) {
                try {
                    survey.setUnderResolution( true );
                    surveyHandler.updateSurveyAsUnderResolution( survey.get_id() );
                    emailServices.sendZillowReviewComplaintHandleMail( complaintRegistrationSettings.getMailId(),
                        zillowTempPost.getZillowReviewerName(), String.valueOf( zillowTempPost.getZillowReviewRating() ),
                        zillowTempPost.getZillowReviewUrl() );

                    // add complaint resolution status in External Survey Tracker
                    saveExternalSurveyTracker( zillowTempPost.getEntityColumnName(), zillowTempPost.getEntityId(),
                        CommonConstants.SURVEY_SOURCE_ZILLOW,
                        unitSettings.getSocialMediaTokens().getZillowToken().getZillowProfileLink(),
                        zillowTempPost.getZillowReviewUrl(), zillowTempPost.getZillowReviewRating(), autoPostSuccess,
                        CommonConstants.YES, zillowTempPost.getZillowReviewDate(), postedOn );
                    return true;
                } catch ( InvalidInputException | UndeliveredEmailException e ) {
                    LOG.error( "Error while sending complaint resolution mail to admins. Reason :", e );
                    return true;
                }
            }
        }
        LOG.info(
            "Method to trigger complaint resolution workflow for a review, triggerComplaintResolutionWorkflowForZillowReview finished." );
        return false;
    }


    /**
     * 
     * @param collectionName
     * @param iden
     * @param facebookToken
     */
    public void updateFacebookToken( String collectionName, long iden, FacebookToken facebookToken )
    {
        LOG.info( "Method updateFacebookToken() started" );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden(
            MongoOrganizationUnitSettingDaoImpl.KEY_FACEBOOK_SOCIAL_MEDIA_TOKEN, facebookToken, iden, collectionName );
        LOG.info( "Method updateFacebookToken() ended" );
    }


    /**
     * 
     * @param collectionName
     * @param iden
     * @param linkedInToken
     */
    public void updateLinkedinToken( String collectionName, long iden, LinkedInToken linkedInToken )
    {
        LOG.info( "Method updateLinkedinToken() started" );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden(
            MongoOrganizationUnitSettingDaoImpl.KEY_LINKEDIN_SOCIAL_MEDIA_TOKEN, linkedInToken, iden, collectionName );
        LOG.info( "Method updateLinkedinToken() ended" );
    }


    /**
     * 
     * @param accessToken
     * @param mediaTokens
     * @param profileLink
     * @return
     */
    @Override
    public SocialMediaTokens updateFacebookToken( facebook4j.auth.AccessToken accessToken, SocialMediaTokens mediaTokens,
        String profileLink )
    {
        LOG.debug( "Method updateFacebookToken() called from SocialManagementController" );
        if ( mediaTokens == null ) {
            LOG.debug( "Media tokens do not exist. Creating them and adding the facebook access token" );
            mediaTokens = new SocialMediaTokens();
        }

        //check for facebook token
        FacebookToken facebookToken = mediaTokens.getFacebookToken();
        if ( facebookToken == null ) {
            facebookToken = new FacebookToken();
        }

        //update profile link
        if ( profileLink != null )
            facebookToken.setFacebookPageLink( profileLink );

        //update access tokem
        facebookToken.setFacebookAccessToken( accessToken.getToken() );
        facebookToken.setFacebookAccessTokenCreatedOn( System.currentTimeMillis() );
        if ( accessToken.getExpires() != null )
            facebookToken.setFacebookAccessTokenExpiresOn( accessToken.getExpires() );

        facebookToken.setFacebookAccessTokenToPost( accessToken.getToken() );

        Facebook facebook = new FacebookFactory().getInstance();
        facebook.setOAuthAppId( facebookClientId, facebookAppSecret );
        facebook.setOAuthAccessToken( new facebook4j.auth.AccessToken( accessToken.getToken() ) );

        //update facebook pages
        ResponseList<Account> accounts;
        List<FacebookPage> facebookPages = new ArrayList<FacebookPage>();
        try {
            accounts = facebook.getAccounts();
            FacebookPage facebookPage = null;
            for ( Account account : accounts ) {
                facebookPage = new FacebookPage();
                facebookPage.setId( account.getId() );
                facebookPage.setName( account.getName() );
                facebookPage.setAccessToken( account.getAccessToken() );
                facebookPage.setCategory( account.getCategory() );
                facebookPage.setProfileUrl( facebookUri.concat( account.getId() ) );
                facebookPages.add( facebookPage );
            }
        } catch ( FacebookException e ) {
            LOG.error( "Error while creating access token for facebook: " + e.getLocalizedMessage(), e );
        }
        facebookToken.setFacebookPages( facebookPages );

        //update expiry email alert detail
        facebookToken.setTokenExpiryAlertSent( false );
        facebookToken.setTokenExpiryAlertEmail( null );
        facebookToken.setTokenExpiryAlertTime( null );


        //update facebook token in media token
        mediaTokens.setFacebookToken( facebookToken );

        LOG.debug( "Method updateFacebookToken() finished from SocialManagementController" );
        return mediaTokens;
    }


    @Override
    public String getFbRedirectUrIForEmailRequest( String columnName, String columnValue, String baseUrl )
        throws InvalidInputException
    {
        LOG.info( "method getFbRedirectUrIForEmailRequest started " );
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put( "columnName", columnName );
        urlParams.put( "columnValue", columnValue );
        urlParams.put( "serverBaseUrl", baseUrl );

        String facebookOauthRedirectUrl = urlGenerator.generateUrl( urlParams, baseUrl + facebookRedirectUriForMail );
        LOG.debug( "generated fb redirect url is : " + facebookOauthRedirectUrl );

        LOG.info( "method getFbRedirectUrIForEmailRequest ended " );
        return facebookOauthRedirectUrl;
    }


    @Override
    public String getLinkedinRedirectUrIForEmailRequest( String columnName, String columnValue, String baseUrl )
        throws InvalidInputException
    {
        LOG.info( "method getLinkedinRedirectUrIForEmailRequest started " );

        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put( "columnName", columnName );
        urlParams.put( "columnValue", columnValue );
        urlParams.put( "serverBaseUrl", baseUrl );

        String linkedinOauthRedirectUrl = urlGenerator.generateUrl( urlParams, baseUrl + linkedinREdirectUriForMail );
        LOG.debug( "generated linkedin redirect url is : " + linkedinOauthRedirectUrl );

        LOG.info( "method getLinkedinRedirectUrIForEmailRequest ended " );
        return linkedinOauthRedirectUrl;
    }


    /**
     * 
     * @param redirectUri
     * @return
     */
    @Override
    public String getLinkedinAuthUrl( String redirectUri )
    {

        LOG.info( "Method getLinkedinAuthUrl started" );

        StringBuilder linkedInAuth = new StringBuilder( linkedinAuthUri ).append( "?response_type=" ).append( "code" );
        linkedInAuth.append( "&client_id=" ).append( linkedInApiKey );
        linkedInAuth.append( "&redirect_uri=" ).append( redirectUri );
        linkedInAuth.append( "&state=" ).append( "SOCIALSURVEY" );
        linkedInAuth.append( "&scope=" ).append( linkedinScope );

        LOG.info( "Method getLinkedinAuthUrl ended" );
        return linkedInAuth.toString();
    }


    /**
     * 
     * @param url
     * @throws InvalidInputException 
     */
    @Override
    public void askFaceBookToReScrapePage( String url ) throws InvalidInputException
    {
        LOG.info( "method forceFaceBookToReScrapePage started for page " + url );
        if ( StringUtils.isEmpty( url ) ) {
            throw new InvalidInputException( "Passed Parameter Url is null or empty" );
        }

        String facebookRescrapeUrl = fbGraphUrl + "?scrape=true&id=" + url;

        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.postForLocation( facebookRescrapeUrl, Object.class );
        } catch ( Exception e ) {
            LOG.error( "Error while asking fb to rescrape the url " + url );
        }

        LOG.info( "method forceFaceBookToReScrapePage finished for page " + url );
    }


    @Override
    public String getClientCompanyProfileUrlForAgentToPostInSocialMedia( Long agentId, OrganizationUnitSettings unitSettings,
        String collectionType ) throws InvalidInputException
    {

        String profileLink = null;

        Map<String, List<OrganizationUnitSettings>> settingsMap = getSettingsForBranchesAndRegionsInHierarchy( agentId );
        List<OrganizationUnitSettings> companySettings = settingsMap
            .get( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
        List<OrganizationUnitSettings> regionSettings = settingsMap
            .get( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
        List<OrganizationUnitSettings> branchSettings = settingsMap
            .get( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );

        if ( unitSettings == null ) {
            throw new InvalidInputException( "unitSettings can't be null" );
        }

        if ( companySettings.get( 0 ).getSurvey_settings().isAutoPostLinkToUserSiteEnabled() ) {
            switch ( collectionType ) {
                case MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION:
                    if ( unitSettings.getContact_details() != null
                        && unitSettings.getContact_details().getWeb_addresses() != null
                        && unitSettings.getContact_details().getWeb_addresses().getWork() != null ) {
                        profileLink = unitSettings.getContact_details().getWeb_addresses().getWork();
                    }
                case MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION:
                    if ( collectionType.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
                        if ( profileLink != null ) {
                            return profileLink;
                        } else {
                            if ( branchSettings != null && branchSettings.size() != 0
                                && branchSettings.get( 0 ).getContact_details() != null
                                && branchSettings.get( 0 ).getContact_details().getWeb_addresses() != null
                                && branchSettings.get( 0 ).getContact_details().getWeb_addresses().getWork() != null ) {
                                profileLink = branchSettings.get( 0 ).getContact_details().getWeb_addresses().getWork();
                            }
                        }
                    } else {
                        if ( unitSettings.getContact_details() != null
                            && unitSettings.getContact_details().getWeb_addresses() != null
                            && unitSettings.getContact_details().getWeb_addresses().getWork() != null ) {
                            profileLink = unitSettings.getContact_details().getWeb_addresses().getWork();
                        }
                    }
                case MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION:
                    if ( collectionType.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION )
                        || collectionType.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
                        if ( profileLink != null ) {
                            return profileLink;
                        } else {
                            if ( regionSettings != null && regionSettings.size() != 0
                                && regionSettings.get( 0 ).getContact_details() != null
                                && regionSettings.get( 0 ).getContact_details().getWeb_addresses() != null
                                && regionSettings.get( 0 ).getContact_details().getWeb_addresses().getWork() != null ) {
                                profileLink = regionSettings.get( 0 ).getContact_details().getWeb_addresses().getWork();
                            }
                        }
                    } else {
                        if ( unitSettings.getContact_details() != null
                            && unitSettings.getContact_details().getWeb_addresses() != null
                            && unitSettings.getContact_details().getWeb_addresses().getWork() != null ) {
                            profileLink = unitSettings.getContact_details().getWeb_addresses().getWork();
                        }
                    }
                case MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION:
                    if ( collectionType.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION )
                        || collectionType.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION )
                        || collectionType.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {
                        if ( profileLink != null ) {
                            return profileLink;
                        } else {
                            if ( companySettings != null && companySettings.size() != 0
                                && companySettings.get( 0 ).getContact_details() != null
                                && companySettings.get( 0 ).getContact_details().getWeb_addresses() != null
                                && companySettings.get( 0 ).getContact_details().getWeb_addresses().getWork() != null ) {
                                profileLink = companySettings.get( 0 ).getContact_details().getWeb_addresses().getWork();
                            }
                        }
                    } else {
                        if ( unitSettings.getContact_details() != null
                            && unitSettings.getContact_details().getWeb_addresses() != null
                            && unitSettings.getContact_details().getWeb_addresses().getWork() != null ) {
                            profileLink = unitSettings.getContact_details().getWeb_addresses().getWork();
                        }
                    }
                    break;

                default:
                    throw new InvalidInputException( "Invalid collection type" );
            }
        }
        return profileLink;
    }

}
