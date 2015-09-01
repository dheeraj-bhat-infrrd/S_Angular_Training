package com.realtech.socialsurvey.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import retrofit.mime.TypedByteArray;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.FacebookPage;
import com.realtech.socialsurvey.core.entities.FacebookToken;
import com.realtech.socialsurvey.core.entities.GoogleToken;
import com.realtech.socialsurvey.core.entities.LinkedInToken;
import com.realtech.socialsurvey.core.entities.LinkedinUserProfileResponse;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfileStage;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.TwitterToken;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.entities.ZillowToken;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.integration.zillow.ZillowIntegrationApi;
import com.realtech.socialsurvey.core.integration.zillow.ZillowIntergrationApiBuilder;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.social.SocialAsyncService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.web.common.ErrorResponse;
import com.realtech.socialsurvey.web.common.JspResolver;
import com.realtech.socialsurvey.web.util.RequestUtils;

import facebook4j.Account;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.ResponseList;


/**
 * Controller to manage social media oauth and pull/push posts
 */
@Controller
public class SocialManagementController
{

    private static final Logger LOG = LoggerFactory.getLogger( SocialManagementController.class );

    @Autowired
    private SessionHelper sessionHelper;

    @Autowired
    private SocialAsyncService socialAsyncService;

    @Autowired
    private SocialManagementService socialManagementService;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private ZillowIntergrationApiBuilder zillowIntergrationApiBuilder;

    @Autowired
    private RequestUtils requestUtils;

    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;

    // Facebook
    @Value ( "${FB_REDIRECT_URI}")
    private String facebookRedirectUri;

    @Value ( "${FB_CLIENT_ID}")
    private String facebookClientId;

    @Value ( "${FB_CLIENT_SECRET}")
    private String facebookClientSecret;

    @Value ( "${FB_URI}")
    private String facebookUri;

    // LinkedIn
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
    @Value ( "${LINKED_IN_ACCESS_URI}")
    private String linkedinAccessUri;
    @Value ( "${LINKED_IN_PROFILE_URI}")
    private String linkedinProfileUri;
    @Value ( "${LINKED_IN_SCOPE}")
    private String linkedinScope;

    // Google
    @Value ( "${GOOGLE_API_KEY}")
    private String googleApiKey;
    @Value ( "${GOOGLE_API_SECRET}")
    private String googleApiSecret;
    @Value ( "${GOOGLE_REDIRECT_URI}")
    private String googleApiRedirectUri;
    @Value ( "${GOOGLE_API_SCOPE}")
    private String googleApiScope;
    @Value ( "${GOOGLE_SHARE_URI}")
    private String googleShareUri;
    @Value ( "${GOOGLE_PROFILE_URI}")
    private String googleProfileUri;

    @Value ( "${ZILLOW_WEBSERVICE_ID}")
    private String zillowWebserviceId;


    /**
     * Returns the social authorization page
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/socialauth", method = RequestMethod.GET)
    public String getSocialAuthPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Method getSocialAuthPage() called from SocialManagementController" );
        HttpSession session = request.getSession( false );
        if ( session == null ) {
            LOG.error( "Session is null!" );
        }

        // AuthUrl for diff social networks
        String socialNetwork = request.getParameter( "social" );
        String socialFlow = request.getParameter( "flow" );

        String columnName = request.getParameter( "columnName" );
        if ( columnName != null ) {
            String columnValue = request.getParameter( "columnValue" );
            model.addAttribute( "fromDashboard", 1 );
            session.setAttribute( "columnName", columnName );
            session.setAttribute( "columnValue", columnValue );
        }

        session.removeAttribute( CommonConstants.SOCIAL_FLOW );
        String serverBaseUrl = requestUtils.getRequestServerName( request );
        switch ( socialNetwork ) {

        // Building facebook authUrl
            case "facebook":
                Facebook facebook = socialManagementService.getFacebookInstance( serverBaseUrl );

                // Setting authUrl in model
                session.setAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN, facebook );
                model.addAttribute( CommonConstants.SOCIAL_AUTH_URL,
                    facebook.getOAuthAuthorizationURL( serverBaseUrl + facebookRedirectUri ) );

                break;

            // Building twitter authUrl
            case "twitter":
                RequestToken requestToken;
                try {
                    requestToken = socialManagementService.getTwitterRequestToken( serverBaseUrl );
                } catch ( Exception e ) {
                    LOG.error( "Exception while getting request token. Reason : " + e.getMessage(), e );
                    model.addAttribute( "message", e.getMessage() );
                    return JspResolver.ERROR_PAGE;
                }

                // We will keep the request token in session
                session.setAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN, requestToken );
                model.addAttribute( CommonConstants.SOCIAL_AUTH_URL, requestToken.getAuthorizationURL() );

                LOG.info( "Returning the twitter authorizationurl : " + requestToken.getAuthorizationURL() );
                break;

            // Building linkedin authUrl
            case "linkedin":
                if ( socialFlow != null && !socialFlow.isEmpty() ) {
                    session.setAttribute( CommonConstants.SOCIAL_FLOW, socialFlow );
                }

                StringBuilder linkedInAuth = new StringBuilder( linkedinAuthUri ).append( "?response_type=" ).append( "code" );
                linkedInAuth.append( "&client_id=" ).append( linkedInApiKey );
                linkedInAuth.append( "&redirect_uri=" ).append( serverBaseUrl ).append( linkedinRedirectUri );
                linkedInAuth.append( "&state=" ).append( "SOCIALSURVEY" );
                linkedInAuth.append( "&scope=" ).append( linkedinScope );

                model.addAttribute( CommonConstants.SOCIAL_AUTH_URL, linkedInAuth.toString() );

                LOG.info( "Returning the linkedin authorizationurl : " + linkedInAuth.toString() );
                break;

            // Building Google authUrl
            case "google":
                StringBuilder googleAuth = new StringBuilder( "https://accounts.google.com/o/oauth2/auth" );
                googleAuth.append( "?scope=" ).append( googleApiScope );
                googleAuth.append( "&state=" ).append( "security_token" );
                googleAuth.append( "&response_type=" ).append( "code" );
                googleAuth.append( "&redirect_uri=" ).append( serverBaseUrl + googleApiRedirectUri );
                googleAuth.append( "&client_id=" ).append( googleApiKey );
                googleAuth.append( "&access_type=" ).append( "offline" );
                googleAuth.append( "&approval_prompt=" ).append( "force" );

                model.addAttribute( CommonConstants.SOCIAL_AUTH_URL, googleAuth.toString() );

                LOG.info( "Returning the google authorizationurl : " + googleAuth.toString() );
                break;

            case "zillow":
                break;
            // TODO Building Yelp authUrl
            case "yelp":
                break;

            // TODO Building RSS authUrl
            case "rss":
                break;

            default:
                LOG.error( "Social Network Type invalid in getSocialAuthPage" );
        }

        model.addAttribute( CommonConstants.MESSAGE, CommonConstants.YES );
        if ( socialNetwork.equalsIgnoreCase( "facebook" ) )
            return JspResolver.SOCIAL_FACEBOOK_INTERMEDIATE;
        else if ( socialNetwork.equalsIgnoreCase( "zillow" ) )
            return JspResolver.SOCIAL_ZILLOW_INTERMEDIATE;
        else
            return JspResolver.SOCIAL_AUTH_MESSAGE;
    }


    /**
     * The url that Facebook send request to with the oauth verification code
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/facebookauth", method = RequestMethod.GET)
    public String authenticateFacebookAccess( Model model, HttpServletRequest request )
    {
        LOG.info( "Facebook authentication url requested" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        AccountType accountType = (AccountType) session.getAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION );
        if ( session.getAttribute( "columnName" ) != null ) {
            String columnName = (String) session.getAttribute( "columnName" );
            String columnValue = (String) session.getAttribute( "columnValue" );
            session.removeAttribute( "columnName" );
            session.removeAttribute( "columnValue" );
            model.addAttribute( "columnName", columnName );
            model.addAttribute( "columnValue", columnValue );
            model.addAttribute( "fromDashboard", 1 );
        }

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            // On auth error
            String errorCode = request.getParameter( "error" );
            if ( errorCode != null ) {
                LOG.error( "Error code : " + errorCode );
                model.addAttribute( CommonConstants.ERROR, CommonConstants.YES );
                return JspResolver.SOCIAL_AUTH_MESSAGE;
            }

            // Getting Oauth accesstoken for facebook
            String oauthCode = request.getParameter( "code" );
            Facebook facebook = (Facebook) session.getAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
            String profileLink = null;
            facebook4j.auth.AccessToken accessToken = null;
            List<FacebookPage> facebookPages = new ArrayList<>();
            try {
                accessToken = facebook.getOAuthAccessToken( oauthCode, requestUtils.getRequestServerName( request )
                    + facebookRedirectUri );
                facebook4j.User fbUser = facebook.getUser( facebook.getId() );
                if ( user != null ) {
                    profileLink = fbUser.getLink().toString();
                    FacebookPage personalUserAccount = new FacebookPage();
                    personalUserAccount.setAccessToken( accessToken.getToken() );
                    personalUserAccount.setName( fbUser.getName() );
                    personalUserAccount.setProfileUrl( profileLink );
                    facebookPages.add( personalUserAccount );
                }
            } catch ( FacebookException e ) {
                LOG.error( "Error while creating access token for facebook: " + e.getLocalizedMessage(), e );
            }
            boolean updated = false;

            // Storing token
            SocialMediaTokens mediaTokens;
            int accountMasterId = accountType.getValue();
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user.getCompany()
                    .getCompanyId() );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                mediaTokens = companySettings.getSocialMediaTokens();
                mediaTokens = updateFacebookToken( accessToken, mediaTokens, profileLink );
                facebookPages.addAll( mediaTokens.getFacebookToken().getFacebookPages() );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, mediaTokens );
                companySettings.setSocialMediaTokens( mediaTokens );
                for ( ProfileStage stage : companySettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "FACEBOOK_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( companySettings.getProfileStages(), companySettings,
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                userSettings.setCompanySettings( companySettings );
                updated = true;
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                mediaTokens = regionSettings.getSocialMediaTokens();
                mediaTokens = updateFacebookToken( accessToken, mediaTokens, profileLink );
                facebookPages.addAll( mediaTokens.getFacebookToken().getFacebookPages() );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, mediaTokens );
                regionSettings.setSocialMediaTokens( mediaTokens );
                for ( ProfileStage stage : regionSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "FACEBOOK_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( regionSettings.getProfileStages(), regionSettings,
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                userSettings.getRegionSettings().put( entityId, regionSettings );
                updated = true;
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                mediaTokens = branchSettings.getSocialMediaTokens();
                mediaTokens = updateFacebookToken( accessToken, mediaTokens, profileLink );
                facebookPages.addAll( mediaTokens.getFacebookToken().getFacebookPages() );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, mediaTokens );
                branchSettings.setSocialMediaTokens( mediaTokens );
                for ( ProfileStage stage : branchSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "FACEBOOK_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( branchSettings.getProfileStages(), branchSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                userSettings.getBranchSettings().put( entityId, branchSettings );
                updated = true;
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN )
                || accountMasterId == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }

                mediaTokens = agentSettings.getSocialMediaTokens();
                mediaTokens = updateFacebookToken( accessToken, mediaTokens, profileLink );
                facebookPages.addAll( mediaTokens.getFacebookToken().getFacebookPages() );
                mediaTokens = socialManagementService.updateAgentSocialMediaTokens( agentSettings, mediaTokens );
                agentSettings.setSocialMediaTokens( mediaTokens );
                for ( ProfileStage stage : agentSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "FACEBOOK_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( agentSettings.getProfileStages(), agentSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                userSettings.setAgentSettings( agentSettings );
                updated = true;
            }
            if ( !updated ) {
                throw new InvalidInputException( "Invalid input exception occurred while creating access token for facebook",
                    DisplayMessageConstants.GENERAL_ERROR );
            }
            model.addAttribute( "pageNames", facebookPages );
        } catch ( Exception e ) {
            session.removeAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
            LOG.error( "Exception while getting facebook access token. Reason : " + e.getMessage(), e );
            return JspResolver.SOCIAL_AUTH_MESSAGE;
        }

        // Updating attributes
        session.removeAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
        model.addAttribute( CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES );
        model.addAttribute( "socialNetwork", "facebook" );
        LOG.info( "Facebook Access tokens obtained and added to mongo successfully!" );
        return JspResolver.SOCIAL_FACEBOOK_INTERMEDIATE;
    }


    @ResponseBody
    @RequestMapping ( value = "/saveSelectedAccessFacebookToken")
    public String saveSelectedAccessFacebookToken( Model model, HttpServletRequest request )
    {
        LOG.info( "Method saveSelectedAccessFacebookToken() called from SocialManagementController" );
        String selectedAccessFacebookToken = request.getParameter( "selectedAccessFacebookToken" );
        String selectedProfileUrl = request.getParameter( "selectedProfileUrl" );

        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        AccountType accountType = (AccountType) session.getAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );

        int accountMasterId = accountType.getValue();
        if ( session.getAttribute( "columnName" ) != null ) {
            String columnName = (String) session.getAttribute( "columnName" );
            String columnValue = (String) session.getAttribute( "columnValue" );
            session.removeAttribute( "columnName" );
            session.removeAttribute( "columnValue" );
            model.addAttribute( "columnName", columnName );
            model.addAttribute( "columnValue", columnValue );
            model.addAttribute( "fromDashboard", 1 );
        }

        boolean updated = false;
        SocialMediaTokens mediaTokens = null;
        try {
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user.getCompany()
                    .getCompanyId() );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                mediaTokens = companySettings.getSocialMediaTokens();
                mediaTokens.getFacebookToken().setFacebookAccessTokenToPost( selectedAccessFacebookToken );
                mediaTokens.getFacebookToken().setFacebookPageLink( selectedProfileUrl );
                socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, mediaTokens );
                updated = true;
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                mediaTokens = regionSettings.getSocialMediaTokens();
                mediaTokens.getFacebookToken().setFacebookAccessTokenToPost( selectedAccessFacebookToken );
                mediaTokens.getFacebookToken().setFacebookPageLink( selectedProfileUrl );
                socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, mediaTokens );
                updated = true;
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                mediaTokens = branchSettings.getSocialMediaTokens();
                mediaTokens.getFacebookToken().setFacebookAccessTokenToPost( selectedAccessFacebookToken );
                mediaTokens.getFacebookToken().setFacebookPageLink( selectedProfileUrl );
                socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, mediaTokens );
                updated = true;
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN )
                || accountMasterId == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                mediaTokens = agentSettings.getSocialMediaTokens();
                mediaTokens.getFacebookToken().setFacebookAccessTokenToPost( selectedAccessFacebookToken );
                mediaTokens.getFacebookToken().setFacebookPageLink( selectedProfileUrl );
                socialManagementService.updateAgentSocialMediaTokens( agentSettings, mediaTokens );
                updated = true;
            }
            if ( !updated ) {
                throw new InvalidInputException( "Invalid input exception occurred while saving access token for facebook",
                    DisplayMessageConstants.GENERAL_ERROR );
            }
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "Error while saving access token for facebook to post: " + e.getLocalizedMessage(), e );
        }
        model.addAttribute( "socialNetwork", "facebook" );
        return JspResolver.SOCIAL_FACEBOOK_INTERMEDIATE;
    }


    private SocialMediaTokens updateFacebookToken( facebook4j.auth.AccessToken accessToken, SocialMediaTokens mediaTokens,
        String profileLink )
    {
        LOG.debug( "Method updateFacebookToken() called from SocialManagementController" );
        if ( mediaTokens == null ) {
            LOG.debug( "Media tokens do not exist. Creating them and adding the facebook access token" );
            mediaTokens = new SocialMediaTokens();
            mediaTokens.setFacebookToken( new FacebookToken() );
        } else {
            LOG.debug( "Updating the existing media tokens for facebook" );
            if ( mediaTokens.getFacebookToken() == null ) {
                mediaTokens.setFacebookToken( new FacebookToken() );
            }
        }

        if ( profileLink != null )
            mediaTokens.getFacebookToken().setFacebookPageLink( profileLink );

        mediaTokens.getFacebookToken().setFacebookAccessToken( accessToken.getToken() );
        mediaTokens.getFacebookToken().setFacebookAccessTokenCreatedOn( System.currentTimeMillis() );
        if ( accessToken.getExpires() != null )
            mediaTokens.getFacebookToken().setFacebookAccessTokenExpiresOn( accessToken.getExpires() );

        mediaTokens.getFacebookToken().setFacebookAccessTokenToPost( accessToken.getToken() );

        Facebook facebook = new FacebookFactory().getInstance();
        facebook.setOAuthAppId( facebookClientId, facebookClientSecret );
        facebook.setOAuthAccessToken( new facebook4j.auth.AccessToken( accessToken.getToken() ) );

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
        mediaTokens.getFacebookToken().setFacebookPages( facebookPages );

        LOG.debug( "Method updateFacebookToken() finished from SocialManagementController" );
        return mediaTokens;
    }


    /**
     * The url that twitter send request to with the oauth verification code
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/twitterauth", method = RequestMethod.GET)
    public String authenticateTwitterAccess( Model model, HttpServletRequest request )
    {
        LOG.info( "Twitter authentication url requested" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        AccountType accountType = (AccountType) session.getAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION );
        if ( session.getAttribute( "columnName" ) != null ) {
            String columnName = (String) session.getAttribute( "columnName" );
            String columnValue = (String) session.getAttribute( "columnValue" );
            session.removeAttribute( "columnName" );
            session.removeAttribute( "columnValue" );
            model.addAttribute( "columnName", columnName );
            model.addAttribute( "columnValue", columnValue );
            model.addAttribute( "fromDashboard", 1 );
        }
        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            // On auth error
            String errorCode = request.getParameter( "oauth_problem" );
            if ( errorCode != null ) {
                LOG.error( "Error code : " + errorCode );
                model.addAttribute( CommonConstants.ERROR, CommonConstants.YES );
                return JspResolver.SOCIAL_AUTH_MESSAGE;
            }

            // Getting Oauth accesstoken for Twitter
            AccessToken accessToken = null;
            String profileLink = null;
            Twitter twitter = socialManagementService.getTwitterInstance();
            String oauthVerifier = request.getParameter( "oauth_verifier" );
            RequestToken requestToken = (RequestToken) session.getAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
            try {
                accessToken = twitter.getOAuthAccessToken( requestToken, oauthVerifier );
                twitter4j.User twitterUser = twitter.showUser( twitter.getId() );
                if ( twitterUser != null && twitterUser.getScreenName() != null )
                    profileLink = CommonConstants.TWITTER_BASE_URL + twitterUser.getScreenName();
            } catch ( TwitterException te ) {
                if ( TwitterException.UNAUTHORIZED == te.getStatusCode() ) {
                    LOG.error( "Unable to get the access token. Reason: UNAUTHORISED" );
                } else {
                    LOG.error( te.getErrorMessage() );
                }

                throw new NonFatalException( "Unable to procure twitter access token" );
            }
            boolean updated = false;

            // Storing token
            SocialMediaTokens mediaTokens;
            int accountMasterId = accountType.getValue();
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user.getCompany()
                    .getCompanyId() );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                mediaTokens = companySettings.getSocialMediaTokens();
                mediaTokens = updateTwitterToken( accessToken, mediaTokens, profileLink );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, mediaTokens );
                companySettings.setSocialMediaTokens( mediaTokens );
                for ( ProfileStage stage : companySettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "TWITTER_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( companySettings.getProfileStages(), companySettings,
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                userSettings.setCompanySettings( companySettings );
                updated = true;
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                mediaTokens = regionSettings.getSocialMediaTokens();
                mediaTokens = updateTwitterToken( accessToken, mediaTokens, profileLink );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, mediaTokens );
                regionSettings.setSocialMediaTokens( mediaTokens );
                for ( ProfileStage stage : regionSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "TWITTER_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( regionSettings.getProfileStages(), regionSettings,
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                userSettings.getRegionSettings().put( entityId, regionSettings );
                updated = true;
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                mediaTokens = branchSettings.getSocialMediaTokens();
                mediaTokens = updateTwitterToken( accessToken, mediaTokens, profileLink );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, mediaTokens );
                branchSettings.setSocialMediaTokens( mediaTokens );
                for ( ProfileStage stage : branchSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "TWITTER_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( branchSettings.getProfileStages(), branchSettings,
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
                userSettings.getBranchSettings().put( entityId, branchSettings );
                updated = true;
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN )
                || accountMasterId == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }

                mediaTokens = agentSettings.getSocialMediaTokens();
                mediaTokens = updateTwitterToken( accessToken, mediaTokens, profileLink );
                mediaTokens = socialManagementService.updateAgentSocialMediaTokens( agentSettings, mediaTokens );
                agentSettings.setSocialMediaTokens( mediaTokens );
                for ( ProfileStage stage : agentSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "TWITTER_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( agentSettings.getProfileStages(), agentSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                userSettings.setAgentSettings( agentSettings );
                updated = true;
            }
            if ( !updated ) {
                throw new InvalidInputException( "Invalid input exception occurred while creating access token for twitter",
                    DisplayMessageConstants.GENERAL_ERROR );
            }
        } catch ( Exception e ) {
            session.removeAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
            LOG.error( "Exception while getting twitter access token. Reason : " + e.getMessage(), e );
            return JspResolver.SOCIAL_AUTH_MESSAGE;
        }

        // Updating attributes
        session.removeAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
        model.addAttribute( CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES );
        model.addAttribute( "socialNetwork", "twitter" );
        LOG.info( "Twitter Access tokens obtained and added to mongo successfully!" );
        return JspResolver.SOCIAL_AUTH_MESSAGE;
    }


    private SocialMediaTokens updateTwitterToken( AccessToken accessToken, SocialMediaTokens mediaTokens, String profileLink )
    {
        LOG.debug( "Method updateTwitterToken() called from SocialManagementController" );
        if ( mediaTokens == null ) {
            LOG.debug( "Media tokens do not exist. Creating them and adding the twitter access token" );
            mediaTokens = new SocialMediaTokens();
            mediaTokens.setTwitterToken( new TwitterToken() );
        } else {
            LOG.debug( "Updating the existing media tokens for twitter" );
            if ( mediaTokens.getTwitterToken() == null ) {
                mediaTokens.setTwitterToken( new TwitterToken() );
            }
        }

        if ( profileLink != null )
            mediaTokens.getTwitterToken().setTwitterPageLink( profileLink );

        mediaTokens.getTwitterToken().setTwitterAccessToken( accessToken.getToken() );
        mediaTokens.getTwitterToken().setTwitterAccessTokenSecret( accessToken.getTokenSecret() );
        mediaTokens.getTwitterToken().setTwitterAccessTokenCreatedOn( System.currentTimeMillis() );

        LOG.debug( "Method updateTwitterToken() finished from SocialManagementController" );
        return mediaTokens;
    }


    /**
     * The url that LinkedIn send request to with the oauth verification code
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/linkedinauth", method = RequestMethod.GET)
    public String authenticateLinkedInAccess( Model model, HttpServletRequest request )
    {
        LOG.info( "Method authenticateLinkedInAccess() called from SocialManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        AccountType accountType = (AccountType) session.getAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION );
        if ( session.getAttribute( "columnName" ) != null ) {
            String columnName = (String) session.getAttribute( "columnName" );
            String columnValue = (String) session.getAttribute( "columnValue" );
            session.removeAttribute( "columnName" );
            session.removeAttribute( "columnValue" );
            model.addAttribute( "columnName", columnName );
            model.addAttribute( "columnValue", columnValue );
            model.addAttribute( "fromDashboard", 1 );
        }
        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            // On auth error
            String errorCode = request.getParameter( "error" );
            if ( errorCode != null ) {
                LOG.error( "Error code : " + errorCode );
                model.addAttribute( CommonConstants.ERROR, CommonConstants.YES );
                return JspResolver.SOCIAL_AUTH_MESSAGE;
            }

            // Getting Oauth accesstoken for Linkedin
            String oauthCode = request.getParameter( "code" );
            List<NameValuePair> params = new ArrayList<NameValuePair>( 2 );
            params.add( new BasicNameValuePair( "grant_type", "authorization_code" ) );
            params.add( new BasicNameValuePair( "code", oauthCode ) );
            params.add( new BasicNameValuePair( "redirect_uri", requestUtils.getRequestServerName( request )
                + linkedinRedirectUri ) );
            params.add( new BasicNameValuePair( "client_id", linkedInApiKey ) );
            params.add( new BasicNameValuePair( "client_secret", linkedInApiSecret ) );

            // fetching access token
            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost( linkedinAccessUri );
            httpPost.setEntity( new UrlEncodedFormEntity( params, "UTF-8" ) );
            String accessTokenStr = httpclient.execute( httpPost, new BasicResponseHandler() );
            Map<String, Object> map = new Gson().fromJson( accessTokenStr, new TypeToken<Map<String, String>>() {}.getType() );
            String accessToken = (String) map.get( "access_token" );

            // fetching linkedin profile url
            HttpGet httpGet = new HttpGet( linkedinProfileUri + accessToken );
            String basicProfileStr = httpclient.execute( httpGet, new BasicResponseHandler() );
            LinkedinUserProfileResponse profileData = new Gson().fromJson( basicProfileStr, LinkedinUserProfileResponse.class );
            String profileLink = (String) profileData.getSiteStandardProfileRequest().getUrl();

            SocialMediaTokens mediaTokens;
            boolean updated = false;
            int accountMasterId = accountType.getValue();
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user.getCompany()
                    .getCompanyId() );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                mediaTokens = companySettings.getSocialMediaTokens();
                mediaTokens = updateLinkedInToken( accessToken, mediaTokens, profileLink );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, mediaTokens );
                companySettings.setSocialMediaTokens( mediaTokens );
                for ( ProfileStage stage : companySettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "LINKEDIN_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( companySettings.getProfileStages(), companySettings,
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                userSettings.setCompanySettings( companySettings );
                updated = true;
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                mediaTokens = regionSettings.getSocialMediaTokens();
                mediaTokens = updateLinkedInToken( accessToken, mediaTokens, profileLink );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, mediaTokens );
                regionSettings.setSocialMediaTokens( mediaTokens );
                for ( ProfileStage stage : regionSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "LINKEDIN_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( regionSettings.getProfileStages(), regionSettings,
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                userSettings.getRegionSettings().put( entityId, regionSettings );
                updated = true;
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                mediaTokens = branchSettings.getSocialMediaTokens();
                mediaTokens = updateLinkedInToken( accessToken, mediaTokens, profileLink );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, mediaTokens );
                branchSettings.setSocialMediaTokens( mediaTokens );
                for ( ProfileStage stage : branchSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "LINKEDIN_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( branchSettings.getProfileStages(), branchSettings,
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
                userSettings.getBranchSettings().put( entityId, branchSettings );
                updated = true;
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN )
                || accountMasterId == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }

                mediaTokens = agentSettings.getSocialMediaTokens();
                mediaTokens = updateLinkedInToken( accessToken, mediaTokens, profileLink );
                mediaTokens = socialManagementService.updateAgentSocialMediaTokens( agentSettings, mediaTokens );
                agentSettings.setSocialMediaTokens( mediaTokens );

                String socialFlow = (String) session.getAttribute( CommonConstants.SOCIAL_FLOW );
                if ( socialFlow != null && socialFlow.equalsIgnoreCase( CommonConstants.FLOW_REGISTRATION ) ) {
                    // starting service for data update from linkedin
                    agentSettings = (AgentSettings) socialAsyncService.linkedInDataUpdate(
                        MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, mediaTokens );
                } else {
                    // starting async service for data update from linkedin
                    socialAsyncService.linkedInDataUpdateAsync( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
                        agentSettings, mediaTokens );
                }
                for ( ProfileStage stage : agentSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "LINKEDIN_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( agentSettings.getProfileStages(), agentSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                userSettings.setAgentSettings( agentSettings );
                updated = true;
            }
            if ( !updated ) {
                throw new InvalidInputException( "Invalid input exception occurred while creating access token for linkedin",
                    DisplayMessageConstants.GENERAL_ERROR );
            }
        } catch ( Exception e ) {
            LOG.error( "Exception while getting linkedin access token. Reason : " + e.getMessage(), e );
            return JspResolver.SOCIAL_AUTH_MESSAGE;
        }

        // Updating attributes
        model.addAttribute( CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES );
        model.addAttribute( "socialNetwork", "linkedin" );
        LOG.info( "Method authenticateLinkedInAccess() finished from SocialManagementController" );
        return JspResolver.SOCIAL_AUTH_MESSAGE;
    }


    private SocialMediaTokens updateLinkedInToken( String accessToken, SocialMediaTokens mediaTokens, String profileLink )
    {
        LOG.debug( "Method updateLinkedInToken() called from SocialManagementController" );
        if ( mediaTokens == null ) {
            LOG.debug( "Media tokens do not exist. Creating them and adding the LinkedIn access token" );
            mediaTokens = new SocialMediaTokens();
            mediaTokens.setLinkedInToken( new LinkedInToken() );
        } else {
            if ( mediaTokens.getLinkedInToken() == null ) {
                LOG.debug( "Updating the existing media tokens for LinkedIn" );
                mediaTokens.setLinkedInToken( new LinkedInToken() );
            }
        }

        mediaTokens.getLinkedInToken().setLinkedInAccessToken( accessToken );
        if ( profileLink != null ) {
            profileLink = profileLink.split( "&" )[0];
            mediaTokens.getLinkedInToken().setLinkedInPageLink( profileLink );
        }
        mediaTokens.getLinkedInToken().setLinkedInAccessTokenCreatedOn( System.currentTimeMillis() );

        LOG.debug( "Method updateLinkedInToken() finished from SocialManagementController" );
        return mediaTokens;
    }


    /**
     * The url that Google send request to with the oauth verification code
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/googleauth", method = RequestMethod.GET)
    public String authenticateGoogleAccess( Model model, HttpServletRequest request )
    {
        LOG.info( "Method authenticateGoogleAccess() called from SocialManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        AccountType accountType = (AccountType) session.getAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION );
        if ( session.getAttribute( "columnName" ) != null ) {
            String columnName = (String) session.getAttribute( "columnName" );
            String columnValue = (String) session.getAttribute( "columnValue" );
            session.removeAttribute( "columnName" );
            session.removeAttribute( "columnValue" );
            model.addAttribute( "columnName", columnName );
            model.addAttribute( "columnValue", columnValue );
            model.addAttribute( "fromDashboard", 1 );
        }
        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            // On auth error
            String errorCode = request.getParameter( "error" );
            if ( errorCode != null ) {
                LOG.error( "Error code : " + errorCode );
                model.addAttribute( CommonConstants.ERROR, CommonConstants.YES );
                return JspResolver.SOCIAL_AUTH_MESSAGE;
            }

            // Getting Oauth accesstoken for Google+
            String OAuthCode = request.getParameter( "code" );

            OAuthRequest googleAuth = new OAuthRequest( Verb.POST, "https://accounts.google.com/o/oauth2/token" );
            googleAuth.addBodyParameter( "code", OAuthCode );
            googleAuth.addBodyParameter( "client_id", googleApiKey );
            googleAuth.addBodyParameter( "client_secret", googleApiSecret );
            googleAuth.addBodyParameter( "redirect_uri", requestUtils.getRequestServerName( request ) + googleApiRedirectUri );
            googleAuth.addBodyParameter( "grant_type", "authorization_code" );
            Response tokenResponse = googleAuth.send();

            String accessToken = "";
            String refreshToken = "";
            Map<String, Object> tokenData = new Gson().fromJson( tokenResponse.getBody(),
                new TypeToken<Map<String, String>>() {}.getType() );
            if ( tokenData != null ) {
                LOG.debug( "Google access token: " + tokenData.get( "access_token" ) + ", Refresh Token: "
                    + tokenData.get( "refresh_token" ) );
                accessToken = tokenData.get( "access_token" ).toString();
                refreshToken = tokenData.get( "refresh_token" ).toString();
            }
            LOG.info( "Access Token: " + accessToken + ", Refresh Token: " + refreshToken );

            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet( googleProfileUri + accessToken );
            String basicProfileStr = httpclient.execute( httpGet, new BasicResponseHandler() );
            Map<String, Object> profileData = new Gson().fromJson( basicProfileStr,
                new TypeToken<Map<String, String>>() {}.getType() );
            String profileLink = null;
            if ( profileData != null ) {
                profileLink = profileData.get( "link" ).toString();
            }
            boolean updated = false;

            // Storing access token
            SocialMediaTokens mediaTokens;
            int accountMasterId = accountType.getValue();
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user.getCompany()
                    .getCompanyId() );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                mediaTokens = companySettings.getSocialMediaTokens();
                mediaTokens = updateGoogleToken( accessToken, refreshToken, mediaTokens, profileLink );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, mediaTokens );
                companySettings.setSocialMediaTokens( mediaTokens );
                for ( ProfileStage stage : companySettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "GOOGLE_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( companySettings.getProfileStages(), companySettings,
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                userSettings.setCompanySettings( companySettings );
                updated = true;
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                mediaTokens = regionSettings.getSocialMediaTokens();
                mediaTokens = updateGoogleToken( accessToken, refreshToken, mediaTokens, profileLink );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, mediaTokens );
                regionSettings.setSocialMediaTokens( mediaTokens );
                for ( ProfileStage stage : regionSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "GOOGLE_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( regionSettings.getProfileStages(), regionSettings,
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                userSettings.getRegionSettings().put( entityId, regionSettings );
                updated = true;
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                mediaTokens = branchSettings.getSocialMediaTokens();
                mediaTokens = updateGoogleToken( accessToken, refreshToken, mediaTokens, profileLink );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, mediaTokens );
                branchSettings.setSocialMediaTokens( mediaTokens );
                for ( ProfileStage stage : branchSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "GOOGLE_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( branchSettings.getProfileStages(), branchSettings,
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
                userSettings.getBranchSettings().put( entityId, branchSettings );
                updated = true;
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN )
                || accountMasterId == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }

                mediaTokens = agentSettings.getSocialMediaTokens();
                mediaTokens = updateGoogleToken( accessToken, refreshToken, mediaTokens, profileLink );
                mediaTokens = socialManagementService.updateAgentSocialMediaTokens( agentSettings, mediaTokens );
                agentSettings.setSocialMediaTokens( mediaTokens );
                for ( ProfileStage stage : agentSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "GOOGLE_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( agentSettings.getProfileStages(), agentSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                userSettings.setAgentSettings( agentSettings );
                updated = true;
            }
            if ( !updated ) {
                throw new InvalidInputException( "Invalid input exception occurred while creating access token for google",
                    DisplayMessageConstants.GENERAL_ERROR );
            }
        } catch ( Exception e ) {
            session.removeAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
            LOG.error( "Exception while getting google access token. Reason : " + e.getMessage(), e );
            return JspResolver.SOCIAL_AUTH_MESSAGE;
        }

        // Updating attributes
        session.removeAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
        model.addAttribute( CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES );
        model.addAttribute( "socialNetwork", "google" );
        LOG.info( "Method authenticateGoogleAccess() finished from SocialManagementController" );
        return JspResolver.SOCIAL_AUTH_MESSAGE;
    }


    private SocialMediaTokens updateGoogleToken( String accessToken, String refreshToken, SocialMediaTokens mediaTokens,
        String profileLink )
    {
        LOG.debug( "Method updateGoogleToken() called from SocialManagementController" );
        if ( mediaTokens == null ) {
            LOG.debug( "Media tokens do not exist. Creating them and adding the Google access token" );
            mediaTokens = new SocialMediaTokens();
            mediaTokens.setGoogleToken( new GoogleToken() );
        } else {
            LOG.debug( "Updating the existing media tokens for google plus" );
            if ( mediaTokens.getGoogleToken() == null ) {
                mediaTokens.setGoogleToken( new GoogleToken() );
            }
        }
        if ( profileLink != null )
            mediaTokens.getGoogleToken().setProfileLink( profileLink );
        mediaTokens.getGoogleToken().setGoogleAccessToken( accessToken );
        mediaTokens.getGoogleToken().setGoogleRefreshToken( refreshToken );
        mediaTokens.getGoogleToken().setGoogleAccessTokenCreatedOn( System.currentTimeMillis() );

        LOG.debug( "Method updateGoogleToken() finished from SocialManagementController" );
        return mediaTokens;
    }


    private SocialMediaTokens updateZillow( SocialMediaTokens mediaTokens, String profileLink, String zillowScreenName )
    {
        LOG.debug( "Method updateGoogleToken() called from SocialManagementController" );
        if ( mediaTokens == null ) {
            LOG.debug( "Media tokens do not exist. Creating them and adding the Google access token" );
            mediaTokens = new SocialMediaTokens();
            mediaTokens.setZillowToken( new ZillowToken() );
        } else {
            LOG.debug( "Updating the existing media tokens for google plus" );
            if ( mediaTokens.getZillowToken() == null ) {
                mediaTokens.setZillowToken( new ZillowToken() );
            }
        }
        if ( profileLink != null ) {
            mediaTokens.getZillowToken().setZillowProfileLink( profileLink );
        }
        if ( zillowScreenName != null ) {
            mediaTokens.getZillowToken().setZillowScreenName( zillowScreenName );
        }


        LOG.debug( "Method updateZillow() finished from SocialManagementController" );
        return mediaTokens;
    }


    @ResponseBody
    @RequestMapping ( value = "/postonfacebook", method = RequestMethod.GET)
    public String postToFacebook( HttpServletRequest request )
    {
        LOG.info( "Method to post feedback of customer to facebook started." );
        String agentName = request.getParameter( "agentName" );
        String custFirstName = request.getParameter( "firstName" );
        String custLastName = request.getParameter( "lastName" );
        String agentIdStr = request.getParameter( "agentId" );
        boolean facebookNotSetup = true;
        double rating = 0;
        long agentId = 0;
        try {
            agentId = Long.parseLong( agentIdStr );
            String ratingStr = request.getParameter( "score" );
            rating = Double.parseDouble( ratingStr );
        } catch ( NumberFormatException e ) {
            LOG.error(
                "Number format exception caught in postToFacebook() while trying to convert agent Id. Nested exception is ", e );
            return e.getMessage();
        }

        User user = sessionHelper.getCurrentUser();
        List<OrganizationUnitSettings> settings = socialManagementService.getBranchAndRegionSettingsForUser( user.getUserId() );

        String agentProfileLink = "";
        AgentSettings agentSettings;
        try {
            agentSettings = userManagementService.getUserSettings( agentId );
            if ( agentSettings != null && agentSettings.getProfileUrl() != null ) {
                agentProfileLink = agentSettings.getProfileUrl();
            }
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException caught in postToFacebook(). Nested exception is ", e );
        }

        String facebookMessage = rating + "-Star Survey Response from " + custFirstName + " " + custLastName + " for "
            + agentName + " on Social Survey - view at " + applicationBaseUrl + CommonConstants.AGENT_PROFILE_FIXED_URL
            + agentProfileLink;
        facebookMessage = facebookMessage.replaceAll( "null", "" );

        for ( OrganizationUnitSettings setting : settings ) {
            try {
                if ( setting != null )
                    if ( !socialManagementService.updateStatusIntoFacebookPage( setting, facebookMessage,
                        requestUtils.getRequestServerName( request ) ) )
                        facebookNotSetup = false;
            } catch ( FacebookException | InvalidInputException e ) {
                LOG.error(
                    "FacebookException/InvalidInputException caught in postToFacebook() while trying to post to facebook. Nested excption is ",
                    e );
            }
        }

        LOG.info( "Method to post feedback of customer to facebook finished." );
        return facebookNotSetup + "";
    }


    @ResponseBody
    @RequestMapping ( value = "/postontwitter", method = RequestMethod.GET)
    public String postToTwitter( HttpServletRequest request )
    {
        LOG.info( "Method to post feedback of customer on twitter started." );
        boolean twitterNotSetup = true;
        try {
            String agentName = request.getParameter( "agentName" );
            String custFirstName = request.getParameter( "firstName" );
            String custLastName = request.getParameter( "lastName" );
            String agentIdStr = request.getParameter( "agentId" );

            double rating = 0;
            long agentId = 0;
            try {
                agentId = Long.parseLong( agentIdStr );
                String ratingStr = request.getParameter( "score" );
                rating = Double.parseDouble( ratingStr );
            } catch ( NumberFormatException e ) {
                LOG.error(
                    "Number format exception caught in postToTwitter() while trying to convert agent Id. Nested exception is ",
                    e );
                return e.getMessage();
            }

            String agentProfileLink = "";
            AgentSettings agentSettings;
            try {
                agentSettings = userManagementService.getUserSettings( agentId );
                if ( agentSettings != null && agentSettings.getProfileUrl() != null ) {
                    agentProfileLink = agentSettings.getProfileUrl();
                }
            } catch ( InvalidInputException e ) {
                LOG.error( "InvalidInputException caught in postToFacebook(). Nested exception is ", e );
            }

            User user = sessionHelper.getCurrentUser();
            List<OrganizationUnitSettings> settings = socialManagementService.getBranchAndRegionSettingsForUser( user
                .getUserId() );
            String twitterMessage = rating + "-Star Survey Response from " + custFirstName + custLastName + " for " + agentName
                + " on @SocialSurveyMe - view at " + applicationBaseUrl + CommonConstants.AGENT_PROFILE_FIXED_URL
                + agentProfileLink;
            twitterMessage = twitterMessage.replaceAll( "null", "" );

            for ( OrganizationUnitSettings setting : settings ) {
                try {
                    if ( setting != null )
                        if ( !socialManagementService.tweet( setting, twitterMessage ) )
                            twitterNotSetup = false;
                } catch ( TwitterException e ) {
                    LOG.error(
                        "TwitterException caught in postToTwitter() while trying to post to twitter. Nested excption is ", e );
                    throw new NonFatalException(
                        "TwitterException caught in postToTwitter() while trying to post to twitter in postToTwitter(). Nested exception is ",
                        e );
                }
            }

        } catch ( NonFatalException e ) {
            LOG.error(
                "Non fatal Exception caught in postToTwitter() while trying to post to social networking sites. Nested excption is ",
                e );
            return e.getMessage();
        }

        LOG.info( "Method to post feedback of customer to various pages of twitter finished." );
        return twitterNotSetup + "";
    }


    @ResponseBody
    @RequestMapping ( value = "/postonlinkedin", method = RequestMethod.GET)
    public String postToLinkedin( HttpServletRequest request )
    {
        LOG.info( "Method to post feedback of customer on twitter started." );
        String agentName = request.getParameter( "agentName" );
        String custFirstName = request.getParameter( "firstName" );
        String custLastName = request.getParameter( "lastName" );
        String agentIdStr = request.getParameter( "agentId" );
        String feedback = request.getParameter( "review" );
        boolean linkedinNotSetup = true;

        double rating = 0;
        long agentId = 0;
        try {
            agentId = Long.parseLong( agentIdStr );
            String ratingStr = request.getParameter( "score" );
            rating = Double.parseDouble( ratingStr );
        } catch ( NumberFormatException e ) {
            LOG.error(
                "Number format exception caught in postToLinkedin() while trying to convert agent Id. Nested exception is ", e );
            return e.getMessage();
        }

        String agentProfileLink = "";
        AgentSettings agentSettings;
        try {
            agentSettings = userManagementService.getUserSettings( agentId );
            if ( agentSettings != null && agentSettings.getProfileUrl() != null ) {
                agentProfileLink = agentSettings.getProfileUrl();
            }
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException caught in postToFacebook(). Nested exception is ", e );
        }

        User user = sessionHelper.getCurrentUser();
        List<OrganizationUnitSettings> settings = socialManagementService.getBranchAndRegionSettingsForUser( user.getUserId() );
        String message = rating + "-Star Survey Response from " + custFirstName + custLastName + " for " + agentName
            + " on SocialSurvey ";
        String linkedinProfileUrl = applicationBaseUrl + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
        message += linkedinProfileUrl;
        message = message.replaceAll( "null", "" );
        String linkedinMessageFeedback = "From : " + custFirstName + " " + custLastName + " " + feedback;
        for ( OrganizationUnitSettings setting : settings ) {
            try {
                if ( setting != null )
                    if ( !socialManagementService
                        .updateLinkedin( setting, message, linkedinProfileUrl, linkedinMessageFeedback ) )
                        linkedinNotSetup = false;
            } catch ( NonFatalException e ) {
                LOG.error( "NonFatalException caught in postToLinkedin() while trying to post to twitter. Nested excption is ",
                    e );
            }
        }

        LOG.info( "Method to post feedback of customer to various pages of twitter finished." );
        return linkedinNotSetup + "";
    }


    @ResponseBody
    @RequestMapping ( value = "/getyelplink", method = RequestMethod.GET)
    public String getYelpLink( HttpServletRequest request )
    {
        LOG.info( "Method to get Yelp details, getYelpLink() started." );
        Map<String, String> yelpUrl = new HashMap<String, String>();

        try {
            sessionHelper.getCanonicalSettings( request.getSession( false ) );
            OrganizationUnitSettings settings = (OrganizationUnitSettings) request.getSession( false ).getAttribute(
                CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );

            if ( settings.getSocialMediaTokens() != null && settings.getSocialMediaTokens().getYelpToken() != null ) {
                yelpUrl.put( "relativePath", settings.getSocialMediaTokens().getYelpToken().getYelpPageLink() );
            }
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "Exception occured in getYelpLink() while trying to post into Yelp." );
            ErrorResponse response = new ErrorResponse();
            response.setErrCode( "Error while trying to post on Yelp." );
            response.setErrMessage( e.getMessage() );
            return new Gson().toJson( response );
        }

        LOG.info( "Method to get Yelp details, getYelpLink() finished." );
        return new Gson().toJson( yelpUrl );
    }


    @ResponseBody
    @RequestMapping ( value = "/getgooglepluslink", method = RequestMethod.GET)
    public String getGooglePlusLink( HttpServletRequest request )
    {
        LOG.info( "Method to get Google details, getGooglePlusLink() started." );
        Map<String, String> googleUrl = new HashMap<String, String>();

        try {
            sessionHelper.getCanonicalSettings( request.getSession( false ) );
            OrganizationUnitSettings settings = (OrganizationUnitSettings) request.getSession( false ).getAttribute(
                CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );

            if ( settings.getSocialMediaTokens() != null && settings.getSocialMediaTokens().getGoogleToken() != null ) {
                googleUrl.put( "host", googleShareUri );
                googleUrl.put( "relativePath", settings.getSocialMediaTokens().getGoogleToken().getProfileLink() );
            }
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "Exception occured in getGooglePlusLink() while trying to post into Google." );
            ErrorResponse response = new ErrorResponse();
            response.setErrCode( "Error while trying to post on Google." );
            response.setErrMessage( e.getMessage() );
            return new Gson().toJson( response );
        }

        LOG.info( "Method to get Google details, getGooglePlusLink() finished." );
        return new Gson().toJson( googleUrl );
    }


    @RequestMapping ( value = "/linkedindataimport")
    public String linkedInDataImport( HttpServletRequest request, Model model )
    {
        LOG.info( "Method linkedInDataImport() called from SocialManagementController" );
        /*String facebookProfileUrl = "";
        String twitterProfileUrl = "";
        String googleProfileUrl = "";*/
        String linkedinProfileUrl = "";
        try {
            sessionHelper.getCanonicalSettings( request.getSession( false ) );
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "Exception caught while importing links for various pages of the Agent. Nested exception is ",
                e.getMessage() );
            return JspResolver.ERROR_PAGE;
        }
        HttpSession session = request.getSession( false );
        UserSettings usersettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
        if ( usersettings != null && usersettings.getAgentSettings() != null
            && usersettings.getAgentSettings().getSocialMediaTokens() != null ) {
            /*if(usersettings.getAgentSettings().getSocialMediaTokens().getFacebookToken()!=null
            	&& usersettings.getAgentSettings().getSocialMediaTokens().getFacebookToken().getFacebookPageLink()!=null){
            	facebookProfileUrl = usersettings.getAgentSettings().getSocialMediaTokens().getFacebookToken().getFacebookPageLink();
            }
            if(usersettings.getAgentSettings().getSocialMediaTokens().getFacebookToken()!=null
            	&& usersettings.getAgentSettings().getSocialMediaTokens().getTwitterToken().getTwitterPageLink()!=null){
            	twitterProfileUrl = usersettings.getAgentSettings().getSocialMediaTokens().getTwitterToken().getTwitterPageLink();
            }
            if(usersettings.getAgentSettings().getSocialMediaTokens().getGoogleToken()!=null
            		&& usersettings.getAgentSettings().getSocialMediaTokens().getGoogleToken().getProfileLink()!=null){
            		googleProfileUrl = usersettings.getAgentSettings().getSocialMediaTokens().getGoogleToken().getProfileLink();
            }*/
            if ( usersettings.getAgentSettings().getSocialMediaTokens().getLinkedInToken() != null
                && usersettings.getAgentSettings().getSocialMediaTokens().getLinkedInToken().getLinkedInPageLink() != null ) {
                linkedinProfileUrl = usersettings.getAgentSettings().getSocialMediaTokens().getLinkedInToken()
                    .getLinkedInPageLink();
            }
        }
        model.addAttribute( "linkedinProfileUrl", linkedinProfileUrl );
        /*model.addAttribute("facebookProfileUrl", facebookProfileUrl);
        model.addAttribute("twitterProfileUrl", twitterProfileUrl);
        model.addAttribute("googleProfileUrl", googleProfileUrl);*/
        return JspResolver.LINKEDIN_IMPORT;
    }


    @ResponseBody
    @RequestMapping ( value = "/finalizeprofileimage")
    public String finalizeProfileImage( Model model, HttpServletRequest request )
    {
        LOG.info( "Method finalizeProfileImage() called from SocialManagementController" );
        HttpSession session = request.getSession( false );

        AgentSettings agentSettings = null;
        try {
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }
            agentSettings = userManagementService.getUserSettings( entityId );
        } catch ( InvalidInputException e ) {
            LOG.error( "Exception caught while importing links for various pages of the Agent. Nested exception is ",
                e.getMessage() );
        }
        socialAsyncService.updateLinkedInProfileImage( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
            agentSettings );

        LOG.info( "Method finalizeProfileImage() finished from SocialManagementController" );
        return CommonConstants.SUCCESS_ATTRIBUTE;
    }


    @RequestMapping ( value = "/sendsurveyinvitation")
    public String sendSurveyInvite()
    {
        LOG.info( "Method sendSurveyInvite() called from SocialManagementController" );
        return JspResolver.HEADER_SURVEY_INVITE;
    }


    @RequestMapping ( value = "/sendsurveyinvitationadmin")
    public String sendSurveyInviteAdmin( Model model, HttpServletRequest request )
    {
        LOG.info( "Method sendSurveyInvite() called from SocialManagementController" );
        model.addAttribute( "columnName", request.getParameter( "columnName" ) );
        model.addAttribute( "columnValue", request.getParameter( "columnValue" ) );
        return JspResolver.HEADER_SURVEY_INVITE_ADMIN;
    }


    @SuppressWarnings ( "unchecked")
    @RequestMapping ( value = "/zillowSaveInfo")
    public String saveZillowDetails( Model model, HttpServletRequest request )
    {
        LOG.info( "Method saveZillowDetails() called from SocialManagementController" );
        HttpSession session = request.getSession( false );
        ZillowIntegrationApi zillowIntegrationApi = zillowIntergrationApiBuilder.getZellowIntegrationApi();
        User user = sessionHelper.getCurrentUser();
        String zillowScreenName = request.getParameter( "zillowProfileName" );

        if ( zillowScreenName == null || zillowScreenName == "" ) {
            model.addAttribute( "Error", "Please provide either the zillow screen name or zillow emailadress" );

        } else {
            try {
                String profileLink = null;
                retrofit.client.Response response = null;
                UserSettings userSettings = (UserSettings) session
                    .getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
                AccountType accountType = (AccountType) session.getAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION );
                long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
                String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
                if ( userSettings == null || entityType == null ) {
                    throw new InvalidInputException( "No user settings found in session" );
                }
                String errorCode = request.getParameter( "error" );
                if ( errorCode != null ) {
                    LOG.error( "Error code : " + errorCode );
                    model.addAttribute( CommonConstants.ERROR, CommonConstants.YES );
                    return JspResolver.SOCIAL_AUTH_MESSAGE;
                }
                String jsonString = null;
                if ( zillowScreenName.contains( "-" ) ) {
                    zillowScreenName = zillowScreenName.replace( "-", " " );
                }
                zillowScreenName = zillowScreenName.trim();
                response = zillowIntegrationApi.fetchZillowReviewsByScreenname( zillowWebserviceId, zillowScreenName );

                Map<String, Object> map = null;
                boolean updated = false;
                if ( response != null ) {
                    jsonString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
                }
                if ( jsonString != null ) {
                    map = new ObjectMapper().readValue( jsonString, new TypeReference<HashMap<String, Object>>() {} );
                }

                Map<String, Object> responseMap = new HashMap<String, Object>();
                Map<String, Object> messageMap = new HashMap<String, Object>();
                Map<String, Object> resultMap = new HashMap<String, Object>();
                Map<String, Object> proInfoMap = new HashMap<String, Object>();
                if ( map != null ) {
                    responseMap = (HashMap<String, Object>) map.get( "response" );
                    messageMap = (HashMap<String, Object>) map.get( "message" );
                    String code = (String) messageMap.get( "code" );
                    if ( !code.equalsIgnoreCase( "0" ) ) {
                        String errorMessage = (String) messageMap.get( "text" );
                        throw new NonFatalException( "Error code from zillow " + code + " detail error message is "
                            + errorMessage );
                    }
                    if ( responseMap != null ) {
                        resultMap = (HashMap<String, Object>) responseMap.get( "results" );
                        if ( resultMap != null ) {
                            proInfoMap = (HashMap<String, Object>) resultMap.get( "proInfo" );
                            if ( proInfoMap != null ) {
                                profileLink = (String) proInfoMap.get( "profileURL" );
                            }
                        }
                    }
                }
                SocialMediaTokens mediaTokens;
                int accountMasterId = accountType.getValue();
                if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                    OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user
                        .getCompany().getCompanyId() );
                    if ( companySettings == null ) {
                        throw new InvalidInputException( "No company settings found in current session" );
                    }
                    mediaTokens = companySettings.getSocialMediaTokens();
                    mediaTokens = updateZillow( mediaTokens, profileLink, zillowScreenName );
                    mediaTokens = socialManagementService.updateSocialMediaTokens(
                        MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, mediaTokens );
                    companySettings.setSocialMediaTokens( mediaTokens );
                    for ( ProfileStage stage : companySettings.getProfileStages() ) {
                        if ( stage.getProfileStageKey().equalsIgnoreCase( "ZILLOW_PRF" ) ) {
                            stage.setStatus( CommonConstants.STATUS_INACTIVE );
                        }
                    }
                    profileManagementService.updateProfileStages( companySettings.getProfileStages(), companySettings,
                        MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                    userSettings.setCompanySettings( companySettings );
                    updated = true;

                } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                    OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                    if ( regionSettings == null ) {
                        throw new InvalidInputException( "No Region settings found in current session" );
                    }
                    mediaTokens = regionSettings.getSocialMediaTokens();
                    mediaTokens = updateZillow( mediaTokens, profileLink, zillowScreenName );
                    mediaTokens = socialManagementService.updateSocialMediaTokens(
                        MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, mediaTokens );
                    regionSettings.setSocialMediaTokens( mediaTokens );
                    for ( ProfileStage stage : regionSettings.getProfileStages() ) {
                        if ( stage.getProfileStageKey().equalsIgnoreCase( "ZILLOW_PRF" ) ) {
                            stage.setStatus( CommonConstants.STATUS_INACTIVE );
                        }
                    }
                    profileManagementService.updateProfileStages( regionSettings.getProfileStages(), regionSettings,
                        MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                    userSettings.getRegionSettings().put( entityId, regionSettings );
                    updated = true;
                } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                    OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                    if ( branchSettings == null ) {
                        throw new InvalidInputException( "No Branch settings found in current session" );
                    }
                    mediaTokens = branchSettings.getSocialMediaTokens();
                    mediaTokens = updateZillow( mediaTokens, profileLink, zillowScreenName );
                    mediaTokens = socialManagementService.updateSocialMediaTokens(
                        MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, mediaTokens );
                    branchSettings.setSocialMediaTokens( mediaTokens );
                    for ( ProfileStage stage : branchSettings.getProfileStages() ) {
                        if ( stage.getProfileStageKey().equalsIgnoreCase( "ZILLOW_PRF" ) ) {
                            stage.setStatus( CommonConstants.STATUS_INACTIVE );
                        }
                    }
                    profileManagementService.updateProfileStages( branchSettings.getProfileStages(), branchSettings,
                        MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
                    userSettings.getBranchSettings().put( entityId, branchSettings );
                    updated = true;
                } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN )
                    || accountMasterId == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                    AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                    if ( agentSettings == null ) {
                        throw new InvalidInputException( "No Agent settings found in current session" );
                    }

                    mediaTokens = agentSettings.getSocialMediaTokens();
                    mediaTokens = updateZillow( mediaTokens, profileLink, zillowScreenName );
                    mediaTokens = socialManagementService.updateAgentSocialMediaTokens( agentSettings, mediaTokens );
                    agentSettings.setSocialMediaTokens( mediaTokens );
                    for ( ProfileStage stage : agentSettings.getProfileStages() ) {
                        if ( stage.getProfileStageKey().equalsIgnoreCase( "ZILLOW_PRF" ) ) {
                            stage.setStatus( CommonConstants.STATUS_INACTIVE );
                        }
                    }
                    profileManagementService.updateProfileStages( agentSettings.getProfileStages(), agentSettings,
                        MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                    userSettings.setAgentSettings( agentSettings );
                    updated = true;
                }
                if ( !updated ) {
                    throw new InvalidInputException( "Invalid input exception occurred while creating access token for google",
                        DisplayMessageConstants.GENERAL_ERROR );
                }


            } catch ( Exception e ) {
                session.removeAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
                model.addAttribute( CommonConstants.ERROR, CommonConstants.YES );
                LOG.error( "Exception while setting zillow profile link. Reason : " + e.getMessage(), e );
                return JspResolver.SOCIAL_AUTH_MESSAGE;
            }

            session.removeAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
            model.addAttribute( CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES );
            model.addAttribute( "socialNetwork", "zillow" );

        }
        return JspResolver.SOCIAL_AUTH_MESSAGE;
    }


    @ResponseBody
    @RequestMapping ( value = "/profileUrl")
    public String getProfileUrl( HttpServletRequest request )
    {
        LOG.info( "Method getProfileUrl() called from SocialManagementController" );
        String socialNetwork = request.getParameter( "socialNetwork" );
        HttpSession session = request.getSession( false );
        UserSettings usersettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );

        String profileUrl = "";
        if ( socialNetwork.equalsIgnoreCase( "facebook" ) ) {
            if ( usersettings != null && usersettings.getAgentSettings() != null
                && usersettings.getAgentSettings().getSocialMediaTokens() != null
                && usersettings.getAgentSettings().getSocialMediaTokens().getFacebookToken() != null
                && usersettings.getAgentSettings().getSocialMediaTokens().getFacebookToken().getFacebookPageLink() != null ) {
                profileUrl = usersettings.getAgentSettings().getSocialMediaTokens().getFacebookToken().getFacebookPageLink();
            }
        } else if ( socialNetwork.equalsIgnoreCase( "twitter" ) ) {
            if ( usersettings != null && usersettings.getAgentSettings() != null
                && usersettings.getAgentSettings().getSocialMediaTokens() != null
                && usersettings.getAgentSettings().getSocialMediaTokens().getTwitterToken().getTwitterPageLink() != null ) {
                profileUrl = usersettings.getAgentSettings().getSocialMediaTokens().getTwitterToken().getTwitterPageLink();
            }
        } else if ( socialNetwork.equals( "linkedin" ) ) {
            if ( usersettings != null && usersettings.getAgentSettings() != null
                && usersettings.getAgentSettings().getSocialMediaTokens() != null
                && usersettings.getAgentSettings().getSocialMediaTokens().getLinkedInToken() != null
                && usersettings.getAgentSettings().getSocialMediaTokens().getLinkedInToken().getLinkedInPageLink() != null ) {
                profileUrl = usersettings.getAgentSettings().getSocialMediaTokens().getLinkedInToken().getLinkedInPageLink();
            }
        } else if ( socialNetwork.equalsIgnoreCase( "google" ) ) {
            if ( usersettings != null && usersettings.getAgentSettings() != null
                && usersettings.getAgentSettings().getSocialMediaTokens() != null
                && usersettings.getAgentSettings().getSocialMediaTokens().getGoogleToken() != null
                && usersettings.getAgentSettings().getSocialMediaTokens().getGoogleToken().getProfileLink() != null ) {
                profileUrl = usersettings.getAgentSettings().getSocialMediaTokens().getGoogleToken().getProfileLink();
            }
        } else if ( socialNetwork.equalsIgnoreCase( "zillow" ) ) {
            if ( usersettings != null && usersettings.getAgentSettings() != null
                && usersettings.getAgentSettings().getSocialMediaTokens() != null
                && usersettings.getAgentSettings().getSocialMediaTokens().getZillowToken() != null ) {
                if ( usersettings.getAgentSettings().getSocialMediaTokens().getZillowToken().getZillowProfileLink() != null ) {
                    profileUrl = usersettings.getAgentSettings().getSocialMediaTokens().getZillowToken().getZillowProfileLink();
                }
            }
        }

        LOG.info( "Method getProfileUrl() finished from SocialManagementController" );
        return profileUrl;
    }


    @ResponseBody
    @RequestMapping ( value = "/disconnectsocialmedia", method = RequestMethod.POST)
    public String disconnectSocialMedia( HttpServletRequest request )
    {
        String socialMedia = request.getParameter( "socialMedia" );

        try {
            if ( socialMedia == null || socialMedia.isEmpty() ) {
                throw new InvalidInputException( "Social media can not be null or empty" );
            }

            User user = sessionHelper.getCurrentUser();
            HttpSession session = request.getSession();
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            // Check for the collection to update
            OrganizationUnitSettings unitSettings = null;
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                unitSettings = organizationManagementService.getCompanySettings( user.getCompany().getCompanyId() );
                unitSettings = socialManagementService.disconnectSocialNetwork( socialMedia, unitSettings,
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                userSettings.setCompanySettings( unitSettings );
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                unitSettings = organizationManagementService.getRegionSettings( entityId );
                unitSettings = socialManagementService.disconnectSocialNetwork( socialMedia, unitSettings,
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                userSettings.getRegionSettings().put( entityId, unitSettings );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                unitSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                unitSettings = socialManagementService.disconnectSocialNetwork( socialMedia, unitSettings,
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
                userSettings.getBranchSettings().put( entityId, unitSettings );
            }
            if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                unitSettings = userManagementService.getUserSettings( entityId );
                unitSettings = socialManagementService.disconnectSocialNetwork( socialMedia, unitSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                userSettings.setAgentSettings( (AgentSettings) unitSettings );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "Exception occured in disconnectSocialNetwork() while disconnecting with the social Media." );
            return "failue";
        }

        return "success";
    }


    @RequestMapping ( value = "/getsocialmediatokenonsettingspage", method = RequestMethod.GET)
    public String getSocialMediaTokenonSettingsPage( HttpServletRequest request, Model model )
    {
        LOG.info( "Inside getSocialMediaTokenonSettingsPage() method" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession();

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            // Check for the collection to update
            OrganizationUnitSettings unitSettings = null;
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                unitSettings = organizationManagementService.getCompanySettings( user );
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                unitSettings = organizationManagementService.getRegionSettings( entityId );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                unitSettings = organizationManagementService.getBranchSettingsDefault( entityId );
            }
            if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                unitSettings = userManagementService.getUserSettings( entityId );
            }

            SocialMediaTokens tokens = unitSettings.getSocialMediaTokens();

            if ( tokens != null ) {
                if ( tokens.getFacebookToken() != null && tokens.getFacebookToken().getFacebookPageLink() != null ) {
                    model.addAttribute( "facebookLink", tokens.getFacebookToken().getFacebookPageLink() );
                }
                if ( tokens.getGoogleToken() != null && tokens.getGoogleToken().getProfileLink() != null ) {
                    model.addAttribute( "googleLink", tokens.getGoogleToken().getProfileLink() );
                }
                if ( tokens.getTwitterToken() != null && tokens.getTwitterToken().getTwitterPageLink() != null ) {
                    model.addAttribute( "twitterLink", tokens.getTwitterToken().getTwitterPageLink() );
                }
                if ( tokens.getLinkedInToken() != null && tokens.getLinkedInToken().getLinkedInPageLink() != null ) {
                    model.addAttribute( "linkedinLink", tokens.getLinkedInToken().getLinkedInPageLink() );
                }
                if ( tokens.getZillowToken() != null && tokens.getZillowToken().getZillowProfileLink() != null ) {
                    model.addAttribute( "zillowLink", tokens.getZillowToken().getZillowProfileLink() );
                }
            }
        } catch ( NonFatalException e ) {
            LOG.error( "Exception occured in getSocialMediaTokenonSettingsPage()" );
            return "failue";
        }
        return JspResolver.SOCIAL_MEDIA_TOKENS;
    }
}