package com.realtech.socialsurvey.web.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ExternalApiCallDetailsDao;
import com.realtech.socialsurvey.core.dao.SocialPostDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.*;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.integration.zillow.FetchZillowReviewBodyByNMLS;
import com.realtech.socialsurvey.core.integration.zillow.ZillowIntegrationAgentApi;
import com.realtech.socialsurvey.core.integration.zillow.ZillowIntegrationLenderApi;
import com.realtech.socialsurvey.core.integration.zillow.ZillowIntergrationApiBuilder;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsSetter;
import com.realtech.socialsurvey.core.services.social.SocialAsyncService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.ErrorResponse;
import com.realtech.socialsurvey.web.common.JspResolver;
import com.realtech.socialsurvey.web.common.TokenHandler;
import com.realtech.socialsurvey.web.util.RequestUtils;
import facebook4j.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.solr.common.SolrDocumentList;
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

import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;


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
    private SolrSearchService solrSearchService;

    @Autowired
    private MessageUtils messageUtils;

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

    @Autowired
    private SettingsSetter settingsSetter;

    @Autowired
    private EmailFormatHelper emailFormatHelper;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private TokenHandler tokenHandler;

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

    // Instagram
    @Value ( "${IG_REDIRECT_URI}" )
    private String instagramRedirectUri;
    @Value ( "${IG_URI}")
    private String instagramUri;

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
    
    @Value ( "${SURVEY_CSV_UPLOAD_AGENT_TEMPLATE}")
    private Object surveyCsvAgentTemplate;

    @Value ( "${SURVEY_CSV_UPLOAD_ADMIN_TEMPLATE}")
    private Object surveyCsvAdminTemplate;

    @Autowired
    private SurveyHandler surveyHandler;

    //TODO : DAO must not be used in controllers
    @Autowired
    private SocialPostDao socialPostDao;

    @Autowired
    private BatchTrackerService batchTrackerService;

    private final static int SOLR_BATCH_SIZE = 20;

    @Value ( "${ZILLOW_AGENT_API_ENDPOINT}")
    private String zillowAgentApiEndpoint;
    
    @Value ( "${ZILLOW_LENDER_API_ENDPOINT}")
    private String zillowLenderApiEndpoint;
    
    

    @Autowired
    private ExternalApiCallDetailsDao externalApiCallDetailsDao;

    @Autowired
    private URLGenerator urlGenerator;
    
    @Value ( "${ZILLOW_PARTNER_ID}")
    private String zillowPartnerId;
    
    @Autowired
    private ZillowIntergrationApiBuilder zillowIntegrationApiBuilder;


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
        
        String isFixSocialMedia = request.getParameter( "isFixSocialMedia" );
        if ( isFixSocialMedia != null ) {
            session.setAttribute( "isFixSocialMedia", 1 );
            model.addAttribute( "isFixSocialMedia", 1 );

        }else{
            session.removeAttribute( "isFixSocialMedia" ); 
        }
        
        String isManual = request.getParameter( "isManual" );
        if ( isManual != null ) {
            model.addAttribute( "isManual", isManual );

        }

        session.removeAttribute( CommonConstants.SOCIAL_FLOW );
        String serverBaseUrl = requestUtils.getRequestServerName( request );
        switch ( socialNetwork ) {

            // Building facebook authUrl
            case "facebook":
                Facebook facebook = socialManagementService.getFacebookInstance( serverBaseUrl, facebookRedirectUri );
                // Setting authUrl in model
                session.setAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN, facebook );
                model.addAttribute( CommonConstants.SOCIAL_AUTH_URL,
                    facebook.getOAuthAuthorizationURL( serverBaseUrl + facebookRedirectUri ) );
                model.addAttribute(CommonConstants.CALLBACK, "./saveSelectedAccessFacebookToken.do");
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
                String linkedInAuth = socialManagementService.getLinkedinAuthUrl( serverBaseUrl + linkedinRedirectUri );
                model.addAttribute( CommonConstants.SOCIAL_AUTH_URL, linkedInAuth );

                LOG.info( "Returning the linkedin authorizationurl : {}", linkedInAuth );
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

                if(LOG.isInfoEnabled())
                    LOG.info( "Returning the google authorizationurl : {}", googleAuth.toString() );
                break;

            case "zillow":
                break;
            // TODO Building Yelp authUrl
            case "yelp":
                break;

            // TODO Building RSS authUrl
            case "rss":
                break;

            case "instagram" :
                Facebook fb = socialManagementService.getFacebookInstance( serverBaseUrl, instagramRedirectUri );

                // Setting authUrl in model
                session.setAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN, fb );
                model.addAttribute( CommonConstants.SOCIAL_AUTH_URL,
                        fb.getOAuthAuthorizationURL( serverBaseUrl + instagramRedirectUri ) );
                break;

            default:
                LOG.error( "Social Network Type invalid in getSocialAuthPage" );
        }

        model.addAttribute( CommonConstants.MESSAGE, CommonConstants.YES );
        if ( socialNetwork.equalsIgnoreCase( "facebook" ) || socialNetwork.equalsIgnoreCase("instagram") )
            return JspResolver.SOCIAL_FACEBOOK_INTERMEDIATE;
        else if ( socialNetwork.equalsIgnoreCase( "zillow" ) ) {
            session.setAttribute( "zillowNonLenderURI", CommonConstants.ZILLOW_PROFILE_URL);
            session.setAttribute( "zillowLenderURI", CommonConstants.ZILLOW_LENDER_PROFILE_URL);
            return JspResolver.SOCIAL_ZILLOW_INTERMEDIATE;
        }
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
        boolean isNewUser = true;

        try {
            UserSettings userSettings = (UserSettings) session
                .getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            // On auth error
            String errorCode = request.getParameter( "error" );
            if ( errorCode != null ) {
                LOG.error( "Error code : {}", errorCode );
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
                accessToken = facebook.getOAuthAccessToken( oauthCode,
                    requestUtils.getRequestServerName( request ) + facebookRedirectUri );
                facebook4j.User fbUser = facebook.getUser( facebook.getId() );
                if ( user != null ) {
                    profileLink = facebookUri + facebook.getId();
                    FacebookPage personalUserAccount = new FacebookPage();
                    personalUserAccount.setId( facebook.getId() );
                    personalUserAccount.setAccessToken( accessToken.getToken() );
                    personalUserAccount.setName( fbUser.getName() );
                    personalUserAccount.setProfileUrl( profileLink );
                    facebookPages.add( personalUserAccount );
                }
            } catch ( FacebookException e ) {
                LOG.error( "Error while creating access token for facebook: ", e );
            }
            boolean updated = false;

            // Storing token
            SocialMediaTokens mediaTokens = new SocialMediaTokens();
            boolean isFixSocialMedia = false;
            int accountMasterId = accountType.getValue();
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService
                    .getCompanySettings( user.getCompany().getCompanyId() );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                mediaTokens = companySettings.getSocialMediaTokens();
                isFixSocialMedia = socialManagementService.checkForFacebookTokenRefresh( mediaTokens );
                facebookPages.addAll( socialManagementService.getFacebookPages( accessToken, profileLink ) );
                mediaTokens = socialManagementService.updateFacebookPages( accessToken, mediaTokens, facebookPages );
                if ( isFixSocialMedia )
                    isNewUser = !socialManagementService.updateFacebookTokenForExistingUser( facebookPages, companySettings,
                        mediaTokens, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                else
                    mediaTokens = socialManagementService.updateFacebookPagesInMongo(
                        MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings.getIden(),
                        mediaTokens );
                
                companySettings.setSocialMediaTokens( mediaTokens );
                userSettings.setCompanySettings( companySettings );
                updated = true;
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    LOG.warn( "No Region settings found in current session" );
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                mediaTokens = regionSettings.getSocialMediaTokens();
                isFixSocialMedia = socialManagementService.checkForFacebookTokenRefresh( mediaTokens );
                facebookPages.addAll( socialManagementService.getFacebookPages( accessToken, profileLink ) );
                mediaTokens = socialManagementService.updateFacebookPages( accessToken, mediaTokens, facebookPages );
                if ( isFixSocialMedia )
                    isNewUser = !socialManagementService.updateFacebookTokenForExistingUser( facebookPages, regionSettings,
                        mediaTokens, MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                else
                    mediaTokens = socialManagementService.updateFacebookPagesInMongo(
                        MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings.getIden(), mediaTokens );
                regionSettings.setSocialMediaTokens( mediaTokens );
                userSettings.getRegionSettings().put( entityId, regionSettings );
                updated = true;
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    LOG.warn( "No Branch settings found in current session" );
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                mediaTokens = branchSettings.getSocialMediaTokens();
                isFixSocialMedia = socialManagementService.checkForFacebookTokenRefresh( mediaTokens );
                facebookPages.addAll( socialManagementService.getFacebookPages( accessToken, profileLink ) );
                mediaTokens = socialManagementService.updateFacebookPages( accessToken, mediaTokens, facebookPages );
                if ( isFixSocialMedia )
                    isNewUser = !socialManagementService.updateFacebookTokenForExistingUser( facebookPages, branchSettings,
                        mediaTokens, MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
                else
                    mediaTokens = socialManagementService.updateFacebookPagesInMongo(
                        MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings.getIden(),
                        mediaTokens );
                branchSettings.setSocialMediaTokens( mediaTokens );
                userSettings.getBranchSettings().put( entityId, branchSettings );
                updated = true;
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN )
                || accountMasterId == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    LOG.warn( "No Agent settings found in current session" );
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }

                mediaTokens = agentSettings.getSocialMediaTokens();
                isFixSocialMedia = socialManagementService.checkForFacebookTokenRefresh( mediaTokens );
                facebookPages.addAll( socialManagementService.getFacebookPages( accessToken, profileLink ) );
                mediaTokens = socialManagementService.updateFacebookPages( accessToken, mediaTokens, facebookPages );
                if ( isFixSocialMedia )
                    isNewUser = !socialManagementService.updateFacebookTokenForExistingUser( facebookPages, agentSettings,
                        mediaTokens, MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                else
                    mediaTokens = socialManagementService.updateFacebookPagesInMongo(
                        MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings.getIden(),
                        mediaTokens );
                agentSettings.setSocialMediaTokens( mediaTokens );
                userSettings.setAgentSettings( agentSettings );
                updated = true;
            }
            if ( !updated ) {
                LOG.warn( "Invalid input exception occurred while creating access token for facebook" );
                throw new InvalidInputException( "Invalid input exception occurred while creating access token for facebook",
                    DisplayMessageConstants.GENERAL_ERROR );
            }
            String fbAccessTokenStr = new Gson().toJson( accessToken, facebook4j.auth.AccessToken.class );
            model.addAttribute( "pageNames", facebookPages );
            
            model.addAttribute( "fbAccessToken", fbAccessTokenStr );
            String mediaTokensStr = new Gson().toJson( mediaTokens, SocialMediaTokens.class );
            model.addAttribute( "mediaTokens", mediaTokensStr );

        } catch ( Exception e ) {
            session.removeAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
            LOG.error( "Exception while getting facebook access token. Reason : ", e );
            return JspResolver.SOCIAL_AUTH_MESSAGE;
        }

        // Updating attributes
        session.removeAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
        model.addAttribute( CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES );
        model.addAttribute( "isNewUser", isNewUser );
        model.addAttribute( "socialNetwork", "facebook" );
        model.addAttribute(CommonConstants.CALLBACK, "./saveSelectedAccessFacebookToken.do");
        LOG.info( "Facebook Access tokens obtained and added to mongo successfully!" );
        return JspResolver.SOCIAL_FACEBOOK_INTERMEDIATE;
    }

    /**
     * The url that Facebook send request to with the oauth verification code
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/instagramAuth", method = RequestMethod.GET)
    public String authenticateInstagramAccess( Model model, HttpServletRequest request ) {
        LOG.info( "Instagram authentication url requested" );
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
            UserSettings userSettings = (UserSettings) session
                    .getAttribute(CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION);
            long entityId = (long) session.getAttribute(CommonConstants.ENTITY_ID_COLUMN);
            String entityType = (String) session.getAttribute(CommonConstants.ENTITY_TYPE_COLUMN);
            if (userSettings == null || entityType == null) {
                throw new InvalidInputException("No user settings found in session");
            }

            // On auth error
            String errorCode = request.getParameter("error");
            if (errorCode != null) {
                LOG.error("Error code : {}", errorCode);
                model.addAttribute(CommonConstants.ERROR, CommonConstants.YES);
                return JspResolver.SOCIAL_AUTH_MESSAGE;
            }

            // Getting Oauth accesstoken for facebook
            String oauthCode = request.getParameter("code");
            Facebook facebook = (Facebook) session.getAttribute(CommonConstants.SOCIAL_REQUEST_TOKEN);
            String profileLink = null;
            facebook4j.auth.AccessToken accessToken = null;
            List<FacebookPage> facebookPages = new ArrayList<>();
            List<Account> accounts = new ArrayList<>();

            accessToken = facebook.getOAuthAccessToken(oauthCode,
                    requestUtils.getRequestServerName(request) + instagramRedirectUri);
            facebook4j.User fbUser = facebook.getUser(facebook.getId());

            // no need to show the user facebook personal account as cannot be connected instagram business profiles

            //get all fb pages connected to instagram using pagination
            ResponseList<Account> resultList;
            Reading reading = new Reading().limit( 25 );
            resultList = facebook.getAccounts( reading );
            if(resultList != null){
                accounts.addAll( resultList );
            }

            while ( resultList!= null && resultList.getPaging() != null && resultList.getPaging().getNext() != null ) {
                resultList = facebook.fetchNext( resultList.getPaging() );
                accounts.addAll( resultList );
            }

            Map<String, String> params = new HashMap<>();
            params.put("fields", "connected_instagram_account{username}");

            //convert Facebook account to SS entity
            FacebookPage facebookPage ;
            RawAPIResponse response ;
            for ( Account account : accounts ) {
                //check if the page is connected to valid instagram account and add to facebookpages list
                response = facebook.callGetAPI(account.getId(), params);
                if( response.asJSONObject().has("connected_instagram_account") ) {
                    facebookPage = new FacebookPage();
                    facebookPage.setId(account.getId());
                    facebookPage.setName(account.getName());
                    facebookPage.setAccessToken(account.getAccessToken());
                    facebookPage.setCategory(account.getCategory());
                    facebookPage.setProfileUrl(instagramUri.concat(response.asJSONObject().
                            getJSONObject("connected_instagram_account").getString("username")));
                    facebookPages.add(facebookPage);
                }
            }

            if(facebookPages.isEmpty()){
                model.addAttribute( "isPageListEmpty", true );
            }
            
            String fbAccessTokenStr = new Gson().toJson( accessToken, facebook4j.auth.AccessToken.class );
            model.addAttribute( "pageNames", facebookPages );
            model.addAttribute( "fbAccessToken", fbAccessTokenStr );
        }
        catch ( FacebookException e ) {
            LOG.error( "Error while creating access token for facebook: " , e );
        }
        catch ( Exception e ) {
            session.removeAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
            LOG.error( "Exception while getting facebook access token. Reason : " , e );
            return JspResolver.SOCIAL_AUTH_MESSAGE;
        }

        // Updating attributes
        session.removeAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
        model.addAttribute( CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES );

        model.addAttribute(CommonConstants.CALLBACK, "./saveSelectedAccessInstagramToken.do");
        model.addAttribute( "socialNetwork", "instagram" );
        LOG.info( "Instagram authentication completed" );
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

        if ( session.getAttribute( "isFixSocialMedia" ) != null ) {
            model.addAttribute( "isFixSocialMedia", 1 );
        }

        boolean updated = false;
        SocialMediaTokens mediaTokens = null;
        String fbAccessTokenStr = request.getParameter( "fbAccessToken" );
        if ( fbAccessTokenStr == null || fbAccessTokenStr.isEmpty() ) {
            LOG.error( "Facebook access token is empty!" );
        }

        facebook4j.auth.AccessToken accessToken = new Gson().fromJson( fbAccessTokenStr, facebook4j.auth.AccessToken.class );
        try {

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService
                    .getCompanySettings( user.getCompany().getCompanyId() );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                mediaTokens = companySettings.getSocialMediaTokens();
                mediaTokens = socialManagementService.updateFacebookPages( accessToken, mediaTokens,
                    mediaTokens.getFacebookToken().getFacebookPages() );
                socialManagementService.updateFacebookTokenAndSave( selectedAccessFacebookToken, mediaTokens,
                    selectedProfileUrl, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings );
                updated = true;
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                mediaTokens = regionSettings.getSocialMediaTokens();
                mediaTokens = socialManagementService.updateFacebookPages( accessToken, mediaTokens,
                    mediaTokens.getFacebookToken().getFacebookPages() );
                socialManagementService.updateFacebookTokenAndSave( selectedAccessFacebookToken, mediaTokens,
                    selectedProfileUrl, MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings );
                updated = true;
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                mediaTokens = branchSettings.getSocialMediaTokens();
                mediaTokens = socialManagementService.updateFacebookPages( accessToken, mediaTokens,
                    mediaTokens.getFacebookToken().getFacebookPages() );
                socialManagementService.updateFacebookTokenAndSave( selectedAccessFacebookToken, mediaTokens,
                    selectedProfileUrl, MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings );
                updated = true;
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN )
                || accountMasterId == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                mediaTokens = agentSettings.getSocialMediaTokens();
                mediaTokens = socialManagementService.updateFacebookPages( accessToken, mediaTokens,
                    mediaTokens.getFacebookToken().getFacebookPages() );
                socialManagementService.updateFacebookTokenAndSave( selectedAccessFacebookToken, mediaTokens,
                    selectedProfileUrl, MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings );
                updated = true;
            }
            if ( !updated ) {
                throw new InvalidInputException( "Invalid input exception occurred while saving access token for facebook",
                    DisplayMessageConstants.GENERAL_ERROR );
            }


            //get detail of expire social media
            boolean isSocialMediaExpired = false;
            if ( organizationManagementService.getExpiredSocailMedia( entityType, entityId ).size() > 0 )
                isSocialMediaExpired = true;
            session.setAttribute( "isSocialMediaExpired", isSocialMediaExpired );

            //Add action to social connection history
            socialManagementService.updateSocialConnectionsHistory( entityType, entityId, mediaTokens,
                CommonConstants.FACEBOOK_SOCIAL_SITE, CommonConstants.SOCIAL_MEDIA_CONNECTED );
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "Error while saving access token for facebook to post: ", e );
        } catch ( NonFatalException e ) {
            LOG.error( "Error setting settings value. Reason : ", e );
        }

        model.addAttribute( "socialNetwork", "facebook" );
        return JspResolver.SOCIAL_FACEBOOK_INTERMEDIATE;
    }

    @ResponseBody
    @RequestMapping ( value = "/saveSelectedAccessInstagramToken")
    public String saveSelectedAccessInstagramToken( Model model, HttpServletRequest request ){
        LOG.info( " Trying to save selected Instagram " );
        String selectedAccessToken = request.getParameter( "selectedAccessFacebookToken" );
        String selectedProfileUrl = request.getParameter( "selectedProfileUrl" );
        String selectedProfileId = request.getParameter("selectedProfileId");
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

        if ( session.getAttribute( "isFixSocialMedia" ) != null ) {
            model.addAttribute( "isFixSocialMedia", 1 );
        }

        boolean updated = false;
        SocialMediaTokens mediaTokens = null;
        String fbAccessTokenStr = request.getParameter( "fbAccessToken" );
        if ( fbAccessTokenStr == null || fbAccessTokenStr.isEmpty() ) {
            LOG.error( "Facebook access token is empty!" );
        }

        facebook4j.auth.AccessToken accessToken = new Gson().fromJson( fbAccessTokenStr, facebook4j.auth.AccessToken.class );

        try {
            //create the instagram token
            InstagramToken instagramToken = new InstagramToken();
            instagramToken.setId(selectedProfileId);
            instagramToken.setAccessTokenToPost(selectedAccessToken);
            instagramToken.setPageLink(selectedProfileUrl);
            instagramToken.setAccessToken(selectedAccessToken);
            instagramToken.setAccessTokenCreatedOn(System.currentTimeMillis());
            instagramToken.setTokenExpiryAlertSent( false );
            instagramToken.setTokenExpiryAlertEmail( null );
            instagramToken.setTokenExpiryAlertTime( null );

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService
                        .getCompanySettings(user.getCompany().getCompanyId());
                if (companySettings == null) {
                    throw new InvalidInputException("No company settings found in current session");
                }

                mediaTokens = companySettings.getSocialMediaTokens();
                if(mediaTokens == null)
                    mediaTokens = new SocialMediaTokens();
                mediaTokens.setInstagramToken(instagramToken);
                socialManagementService.updateSocialMediaTokens(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION,
                        companySettings, mediaTokens );
                //update SETTINGS_SET_STATUS of COMPANY table to set.
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.INSTAGRAM,
                            CommonConstants.SET_SETTINGS );
                    userManagementService.updateCompany( company );
                }
                for ( ProfileStage stage : companySettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "INSTAGRAM_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( companySettings.getProfileStages(), companySettings,
                        MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                updated = true;
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                mediaTokens = regionSettings.getSocialMediaTokens();
                if(mediaTokens == null)
                    mediaTokens = new SocialMediaTokens();
                mediaTokens.setInstagramToken(instagramToken);
                socialManagementService.updateSocialMediaTokens( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION,
                        regionSettings, mediaTokens );
                //update SETTINGS_SET_STATUS of REGION table to set.
                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.INSTAGRAM,
                            CommonConstants.SET_SETTINGS );
                    userManagementService.updateRegion( region );
                }
                for ( ProfileStage stage : regionSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "INSTAGRAM_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( regionSettings.getProfileStages(), regionSettings,
                        MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                updated = true;
            } else if( entityType.equals( CommonConstants.BRANCH_ID_COLUMN )) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                mediaTokens = branchSettings.getSocialMediaTokens();
                if(mediaTokens == null)
                    mediaTokens = new SocialMediaTokens();
                mediaTokens.setInstagramToken(instagramToken);
                socialManagementService.updateSocialMediaTokens( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION,
                branchSettings, mediaTokens );
                //update SETTINGS_SET_STATUS of BRANCH table to set.
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.INSTAGRAM,
                            CommonConstants.SET_SETTINGS );
                    userManagementService.updateBranch( branch );
                }

                for ( ProfileStage stage : branchSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "INSTAGRAM_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( branchSettings.getProfileStages(), branchSettings,
                        MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
                updated = true;
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN )
                    || accountMasterId == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                AgentSettings agentSettings = userManagementService.getUserSettings(entityId);
                if (agentSettings == null) {
                    throw new InvalidInputException("No Agent settings found in current session");
                }
                mediaTokens = agentSettings.getSocialMediaTokens();
                if(mediaTokens == null)
                    mediaTokens = new SocialMediaTokens();
                mediaTokens.setInstagramToken(instagramToken);
                socialManagementService.updateAgentSocialMediaTokens( agentSettings, mediaTokens );

                for ( ProfileStage stage : agentSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "INSTAGRAM_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( agentSettings.getProfileStages(), agentSettings,
                        MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                updated = true;
            }

            if ( !updated ) {
                throw new InvalidInputException( "Invalid input exception occurred while saving access token for instagram",
                        DisplayMessageConstants.GENERAL_ERROR );
            }

            //get detail of expire social media
            boolean isSocialMediaExpired = false;
            if(organizationManagementService.getExpiredSocailMedia( entityType, entityId ).size() > 0)
                isSocialMediaExpired = true;
            session.setAttribute( "isSocialMediaExpired" , isSocialMediaExpired );

            //Add action to social connection history
            socialManagementService.updateSocialConnectionsHistory( entityType, entityId, mediaTokens,
                    CommonConstants.INSTAGRAM_SOCIAL_SITE, CommonConstants.SOCIAL_MEDIA_CONNECTED );

        }
        catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "Error while saving access token for instagram to post: ", e );
        } catch ( NonFatalException e ) {
            LOG.error( "Error setting settings value. Reason : ", e );
        }
        model.addAttribute( "socialNetwork", "instagram" );
        return JspResolver.SOCIAL_FACEBOOK_INTERMEDIATE;
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
        SocialMediaTokens mediaTokens = null;
        try {
            UserSettings userSettings = (UserSettings) session
                .getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || entityType == null ) {
                LOG.warn( "No user settings found in session" );
                throw new InvalidInputException( "No user settings found in session" );
            }

            // On auth error
            String errorCode = request.getParameter( "oauth_problem" );
            if ( errorCode != null ) {
                LOG.error( "Error code : {}", errorCode );
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
            int accountMasterId = accountType.getValue();
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService
                    .getCompanySettings( user.getCompany().getCompanyId() );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                mediaTokens = companySettings.getSocialMediaTokens();
                mediaTokens = updateTwitterToken( accessToken, mediaTokens, profileLink );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, mediaTokens );
                companySettings.setSocialMediaTokens( mediaTokens );
                //update SETTINGS_SET_STATUS of COMPANY table to set.
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.TWITTER,
                        CommonConstants.SET_SETTINGS );
                    userManagementService.updateCompany( company );
                }

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
                    LOG.warn( "No Region settings found in current session" );
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                mediaTokens = regionSettings.getSocialMediaTokens();
                mediaTokens = updateTwitterToken( accessToken, mediaTokens, profileLink );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, mediaTokens );
                regionSettings.setSocialMediaTokens( mediaTokens );
                //update SETTINGS_SET_STATUS of REGION table to set.
                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.TWITTER,
                        CommonConstants.SET_SETTINGS );
                    userManagementService.updateRegion( region );
                }
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
                    LOG.warn( "No Branch settings found in current session" );
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                mediaTokens = branchSettings.getSocialMediaTokens();
                //update SETTINGS_SET_STATUS of BRANCH table to set.
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.TWITTER,
                        CommonConstants.SET_SETTINGS );
                    userManagementService.updateBranch( branch );
                }
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
                    LOG.warn( "No Agent settings found in current session" );
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

            //Add action to social connection history
            socialManagementService.updateSocialConnectionsHistory( entityType, entityId, mediaTokens,
                CommonConstants.TWITTER_SOCIAL_SITE, CommonConstants.SOCIAL_MEDIA_CONNECTED );
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
        if ( session.getAttribute( "isFixSocialMedia" ) != null ) {
            model.addAttribute( "isFixSocialMedia", 1 );
        }
        
        
        SocialMediaTokens mediaTokens = null;
        try {
            UserSettings userSettings = (UserSettings) session
                .getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            // On auth error
            String errorCode = request.getParameter( "error" );
            if ( errorCode != null ) {
                LOG.error( "Error code : {}", errorCode );
                model.addAttribute( CommonConstants.ERROR, CommonConstants.YES );
                return JspResolver.SOCIAL_AUTH_MESSAGE;
            }

            // Getting Oauth accesstoken for Linkedin
            String oauthCode = request.getParameter( "code" );
            List<NameValuePair> params = new ArrayList<NameValuePair>( 2 );
            params.add( new BasicNameValuePair( "grant_type", "authorization_code" ) );
            params.add( new BasicNameValuePair( "code", oauthCode ) );
            params.add(
                new BasicNameValuePair( "redirect_uri", requestUtils.getRequestServerName( request ) + linkedinRedirectUri ) );
            params.add( new BasicNameValuePair( "client_id", linkedInApiKey ) );
            params.add( new BasicNameValuePair( "client_secret", linkedInApiSecret ) );

            // fetching access token
            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost( linkedinAccessUri );
            httpPost.setEntity( new UrlEncodedFormEntity( params, "UTF-8" ) );
            String accessTokenStr = httpclient.execute( httpPost, new BasicResponseHandler() );
            Map<String, Object> map = new Gson().fromJson( accessTokenStr, new TypeToken<Map<String, String>>() {}.getType() );
            String accessToken = (String) map.get( "access_token" );
            String expiresInStr = (String) map.get( "expires_in" );
            long expiresIn = 0;
            if(StringUtils.isNotBlank( expiresInStr ))
                expiresIn = Long.valueOf( expiresInStr ).longValue();

            // fetching linkedin profile url
            HttpGet httpGet = new HttpGet( linkedinProfileUri + accessToken );
            String basicProfileStr = httpclient.execute( httpGet, new BasicResponseHandler() );
            LinkedinUserProfileResponse profileData = new Gson().fromJson( basicProfileStr, LinkedinUserProfileResponse.class );
            String profileLink = profileData.getPublicProfileUrl();

            boolean updated = false;
            int accountMasterId = accountType.getValue();
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService
                    .getCompanySettings( user.getCompany().getCompanyId() );
                if ( companySettings == null ) {
                    LOG.warn( "No company settings found in current session" );
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                mediaTokens = companySettings.getSocialMediaTokens();
                mediaTokens = tokenHandler.updateLinkedInToken( accessToken, mediaTokens, profileLink, expiresIn );
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
                //update SETTINGS_SET_STATUS of COMPANY table to set.
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.LINKED_IN,
                        CommonConstants.SET_SETTINGS );
                    userManagementService.updateCompany( company );
                }

                updated = true;
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    LOG.warn( "No Region settings found in current session" );
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                mediaTokens = regionSettings.getSocialMediaTokens();
                mediaTokens = tokenHandler.updateLinkedInToken( accessToken, mediaTokens, profileLink, expiresIn );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, mediaTokens );
                regionSettings.setSocialMediaTokens( mediaTokens );
                //update SETTINGS_SET_STATUS of REGION table to set.
                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.LINKED_IN,
                        CommonConstants.SET_SETTINGS );
                    userManagementService.updateRegion( region );
                }

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
                    LOG.warn( "No Branch settings found in current session" );
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                mediaTokens = branchSettings.getSocialMediaTokens();
                mediaTokens = tokenHandler.updateLinkedInToken( accessToken, mediaTokens, profileLink, expiresIn );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, mediaTokens );
                branchSettings.setSocialMediaTokens( mediaTokens );
                //update SETTINGS_SET_STATUS of BRANCH table to set.
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.LINKED_IN,
                        CommonConstants.SET_SETTINGS );
                    userManagementService.updateBranch( branch );
                }
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
                    LOG.warn( "No Agent settings found in current session" );
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }

                mediaTokens = agentSettings.getSocialMediaTokens();
                mediaTokens = tokenHandler.updateLinkedInToken( accessToken, mediaTokens, profileLink, expiresIn );
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

            
            //get detail of expire social media
            boolean isSocialMediaExpired = false;
            if(organizationManagementService.getExpiredSocailMedia( entityType, entityId ).size() > 0){
                isSocialMediaExpired = true;                            
            }
            
            session.setAttribute( "isSocialMediaExpired" , isSocialMediaExpired );
            
            //Add action to social connection history
            socialManagementService.updateSocialConnectionsHistory( entityType, entityId, mediaTokens,
                CommonConstants.LINKEDIN_SOCIAL_SITE, CommonConstants.SOCIAL_MEDIA_CONNECTED );
        } catch ( Exception e ) {
            LOG.error( "Exception while getting linkedin access token. Reason : ", e );
            return JspResolver.SOCIAL_AUTH_MESSAGE;
        }

        // Updating attributes
        model.addAttribute( CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES );
        model.addAttribute( "socialNetwork", "linkedin" );
        LOG.info( "Method authenticateLinkedInAccess() finished from SocialManagementController" );
        return JspResolver.SOCIAL_AUTH_MESSAGE;
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
        SocialMediaTokens mediaTokens = null;
        try {
            UserSettings userSettings = (UserSettings) session
                .getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            // On auth error
            String errorCode = request.getParameter( "error" );
            if ( errorCode != null ) {
                LOG.error( "Error code : {}", errorCode );
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
            String expiresIn = "";
            Map<String, Object> tokenData = new Gson().fromJson( tokenResponse.getBody(),
                new TypeToken<Map<String, String>>() {}.getType() );
            if ( tokenData != null ) {
                accessToken = (String) tokenData.get( "access_token" );
                refreshToken = (String) tokenData.get( "refresh_token" );
                expiresIn = (String) tokenData.get( "expires_in" );                
            }
            LOG.debug( "Google access token: {}, Refresh Token: {}, Expires In: {}",accessToken, refreshToken, expiresIn );

            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet( googleProfileUri + accessToken );
            String basicProfileStr = httpclient.execute( httpGet, new BasicResponseHandler() );
            Map<String, Object> profileData = new Gson().fromJson( basicProfileStr,
                new TypeToken<Map<String, String>>() {}.getType() );
            String profileLink = null;
            if ( profileData != null ) {
                if(profileData.get( "link" ) == null){
                    LOG.debug( "Google Plus account is not exist for the entity {} with id : {}", entityType, entityId );
                    session.removeAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
                    model.addAttribute( CommonConstants.NO_GOOGLE_PLUS_FOUND, CommonConstants.YES );
                    return JspResolver.SOCIAL_AUTH_MESSAGE;
                }else{
                    profileLink = profileData.get( "link" ).toString();                    
                }
            }
            boolean updated = false;

            // Storing access token
            int accountMasterId = accountType.getValue();
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService
                    .getCompanySettings( user.getCompany().getCompanyId() );
                if ( companySettings == null ) {
                    LOG.warn( "No company settings found in current session" );
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                mediaTokens = companySettings.getSocialMediaTokens();
                mediaTokens = updateGoogleToken( accessToken, refreshToken, mediaTokens, profileLink, expiresIn );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, mediaTokens );
                companySettings.setSocialMediaTokens( mediaTokens );
                //update SETTINGS_SET_STATUS of COMPANY table to set.
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.GOOGLE_PLUS,
                        CommonConstants.SET_SETTINGS );
                    userManagementService.updateCompany( company );
                }
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
                    LOG.warn( "No Region settings found in current session" );
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                mediaTokens = regionSettings.getSocialMediaTokens();
                mediaTokens = updateGoogleToken( accessToken, refreshToken, mediaTokens, profileLink, expiresIn );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, mediaTokens );
                regionSettings.setSocialMediaTokens( mediaTokens );
                //update SETTINGS_SET_STATUS of REGION table to set.
                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.GOOGLE_PLUS,
                        CommonConstants.SET_SETTINGS );
                    userManagementService.updateRegion( region );
                }

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
                    LOG.warn( "No Branch settings found in current session" );
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                mediaTokens = branchSettings.getSocialMediaTokens();
                mediaTokens = updateGoogleToken( accessToken, refreshToken, mediaTokens, profileLink, expiresIn );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, mediaTokens );
                branchSettings.setSocialMediaTokens( mediaTokens );
                //update SETTINGS_SET_STATUS of BRANCH table to set.
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.GOOGLE_PLUS,
                        CommonConstants.SET_SETTINGS );
                    userManagementService.updateBranch( branch );
                }
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
                mediaTokens = updateGoogleToken( accessToken, refreshToken, mediaTokens, profileLink, expiresIn );
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
                LOG.warn( "Invalid input exception occurred while creating access token for google" );
                throw new InvalidInputException( "Invalid input exception occurred while creating access token for google",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            //Add action to social connection history
            socialManagementService.updateSocialConnectionsHistory( entityType, entityId, mediaTokens,
                CommonConstants.GOOGLE_SOCIAL_SITE, CommonConstants.SOCIAL_MEDIA_CONNECTED );
        } catch ( Exception e ) {
            session.removeAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
            LOG.error( "Exception while getting google access token. Reason : ", e );
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
        String profileLink, String expiresIn )
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
        if(StringUtils.isNotBlank( expiresIn ))
            mediaTokens.getGoogleToken().setGoogleAccessTokenExpiresIn( Long.valueOf( expiresIn ).longValue() );

        LOG.debug( "Method updateGoogleToken() finished from SocialManagementController" );
        return mediaTokens;
    }


    private SocialMediaTokens updateZillow( SocialMediaTokens mediaTokens, String profileLink, String zillowScreenName, Integer nmlsId )
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
        if ( nmlsId != null ) {
        	LenderRef lenderRef = mediaTokens.getZillowToken().getLenderRef();
        	if(lenderRef == null)
        		lenderRef = new LenderRef();
        	
        	lenderRef.setNmlsId(nmlsId);
            mediaTokens.getZillowToken().setLenderRef(lenderRef);
        } else {//if no NMLS, remove existing NMLS id, SS-1225
            if(mediaTokens.getZillowToken() != null && mediaTokens.getZillowToken().getLenderRef() != null ) {
                mediaTokens.getZillowToken().setLenderRef(null);
            }
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
                "Number format exception caught in postToFacebook() while trying to convert agent Id. Nested exception is ",
                e );
            return e.getMessage();
        }
       

        User user = sessionHelper.getCurrentUser();
        List<OrganizationUnitSettings> settings = socialManagementService.getBranchAndRegionSettingsForUser( user.getUserId() );

        String agentProfileLink = "";
        AgentSettings agentSettings;
        String custDisplayName = null;
        try {
            custDisplayName = emailFormatHelper.getCustomerDisplayNameForEmail( custFirstName, custLastName );
            agentSettings = userManagementService.getUserSettings( agentId );
            if ( agentSettings != null && agentSettings.getProfileUrl() != null ) {
                agentProfileLink = agentSettings.getProfileUrl();
            }
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException caught in postToFacebook(). Nested exception is ", e );
        }

        String facebookMessage = surveyHandler.getFormattedSurveyScore( rating ) + "-Star Survey Response from "
            + custDisplayName + " for " + agentName + " on SocialSurvey - view at " + applicationBaseUrl
            + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
        facebookMessage = facebookMessage.replaceAll( "null", "" );

        for ( OrganizationUnitSettings setting : settings ) {
            try {
                if ( setting != null )
                    if ( !socialManagementService.updateStatusIntoFacebookPage( setting, facebookMessage,
                        requestUtils.getRequestServerName( request ), user.getCompany().getCompanyId(), agentProfileLink ) )
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
            String custDisplayName = emailFormatHelper.getCustomerDisplayNameForEmail( custFirstName, custLastName );
            List<OrganizationUnitSettings> settings = socialManagementService
                .getBranchAndRegionSettingsForUser( user.getUserId() );
            
            String twitterMessage = String.format( CommonConstants.TWITTER_MESSAGE,
                surveyHandler.getFormattedSurveyScore( rating ), custDisplayName, agentName, "@SocialSurveyMe" )
                + applicationBaseUrl + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
            twitterMessage = twitterMessage.replaceAll( "null", "" );

            for ( OrganizationUnitSettings setting : settings ) {
                try {
                    if ( setting != null )
                        if ( !socialManagementService.tweet( setting, twitterMessage, user.getCompany().getCompanyId() ) )
                            twitterNotSetup = false;
                } catch ( TwitterException e ) {
                    LOG.warn(
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
    @RequestMapping ( value = "/getyelplink", method = RequestMethod.GET)
    public String getYelpLink( HttpServletRequest request )
    {
        LOG.info( "Method to get Yelp details, getYelpLink() started." );
        Map<String, String> yelpUrl = new HashMap<String, String>();

        try {
            sessionHelper.getCanonicalSettings( request.getSession( false ) );
            OrganizationUnitSettings settings = (OrganizationUnitSettings) request.getSession( false )
                .getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
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
            OrganizationUnitSettings settings = (OrganizationUnitSettings) request.getSession( false )
                .getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );

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
      
            if ( usersettings.getAgentSettings().getSocialMediaTokens().getLinkedInToken() != null
                && usersettings.getAgentSettings().getSocialMediaTokens().getLinkedInToken().getLinkedInPageLink() != null ) {
                linkedinProfileUrl = usersettings.getAgentSettings().getSocialMediaTokens().getLinkedInToken()
                    .getLinkedInPageLink();
            }
        }
        model.addAttribute( "linkedinProfileUrl", linkedinProfileUrl );
        
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
                LOG.warn( "No user settings found in session" );
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
    public String sendSurveyInvite( Model model )
    {
        model.addAttribute( "templateUrl", surveyCsvAgentTemplate );

        LOG.info( "Method sendSurveyInvite() called from SocialManagementController" );
        return JspResolver.HEADER_SURVEY_INVITE;
    }


    /**
     * Method to get the generate report pop up for dry run
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/dryrun")
    public String dryRun( Model model )
    {
        LOG.info( "Method to display the generate report popup for dry run started" );
        User user = sessionHelper.getCurrentUser();
        String emailId = "";
        String noOfDays = "";
        try {
            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
            if ( companySettings.getCrm_info() != null
                && companySettings.getCrm_info().getCrm_source().equals( CommonConstants.CRM_INFO_SOURCE_ENCOMPASS ) ) {
                EncompassCrmInfo encompassCrmInfo = (EncompassCrmInfo) companySettings.getCrm_info();
                if ( encompassCrmInfo.getEmailAddressForReport() != null
                    && !( encompassCrmInfo.getEmailAddressForReport().isEmpty() ) ) {
                    emailId = encompassCrmInfo.getEmailAddressForReport();
                }
                if ( encompassCrmInfo.getNumberOfDays() > 0 ) {
                    noOfDays = String.valueOf( encompassCrmInfo.getNumberOfDays() );
                }
            }
            model.addAttribute( "emailId", emailId );
            model.addAttribute( "NumberOfDays", noOfDays );
        } catch ( Exception e ) {
            LOG.error( "An exception occured while fetching the generate report pop up. Reason :", e );
            return CommonConstants.ERROR;
        }
        LOG.info( "Method to display the generate report popup for dry run finished" );
        return JspResolver.DRY_RUN;
    }


    @RequestMapping ( value = "/sendsurveyinvitationadmin")
    public String sendSurveyInviteAdmin( Model model, HttpServletRequest request )
    {
        LOG.info( "Method sendSurveyInvite() called from SocialManagementController" );
        model.addAttribute( "columnName", request.getParameter( "columnName" ) );
        model.addAttribute( "columnValue", request.getParameter( "columnValue" ) );
        model.addAttribute( "templateUrl", surveyCsvAdminTemplate );
        return JspResolver.HEADER_SURVEY_INVITE_ADMIN;
    }
    
    @RequestMapping ( value = "/zillowValidateNMLS", method = RequestMethod.POST)
    @ResponseBody
    public String zillowValidateNMLS( Model model, HttpServletRequest request )
    {
    	LOG.info( "Method zillowValidateNMLS() called from SocialManagementController" );
        HttpSession session = request.getSession( false );
        ZillowIntegrationAgentApi zillowIntegrationApi = zillowIntergrationApiBuilder.getZillowIntegrationAgentApi();
        User user = sessionHelper.getCurrentUser();
        String zillowScreenName = request.getParameter( "zillowProfileName" );
        String nmlsTemp = request.getParameter("nmls");
        Integer nmlsId = null;
        if(nmlsTemp != null && nmlsTemp.trim().length() > 0) {
            try {
        	nmlsId = new Integer(nmlsTemp);
            } catch (Exception ex) {
                return "invalid-nmls"; 
            }
        }
        SocialMediaTokens mediaTokens = null;
        OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
            .getAttribute( CommonConstants.USER_ACCOUNT_SETTINGS );
        if ( profileSettings == null ) {
            profileSettings = (OrganizationUnitSettings) session.getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
        }
        
        //find screen name by nmls id
        mediaTokens = profileSettings.getSocialMediaTokens();
        ZillowToken zillowToken = null;
        if(mediaTokens != null)  {
        	if( mediaTokens.getZillowToken() != null ) {
        		zillowToken = profileSettings.getSocialMediaTokens().getZillowToken();
        	} else {
        		zillowToken = new ZillowToken();
        		mediaTokens.setZillowToken(zillowToken);
        		profileSettings.getSocialMediaTokens().setZillowToken(zillowToken);
        	}
        } else {
        	mediaTokens = new SocialMediaTokens();
        	zillowToken = new ZillowToken();
        	LenderRef lenderRef = new LenderRef();
        	zillowToken.setLenderRef(lenderRef);
        	mediaTokens.setZillowToken(zillowToken);
        	if(profileSettings.getSocialMediaTokens() != null)
        	    profileSettings.getSocialMediaTokens().setZillowToken(zillowToken);
        	else {
        	    profileSettings.setSocialMediaTokens(mediaTokens);
        	}
        }
        
        LenderRef zillowLenderRef = zillowToken.getLenderRef();
        retrofit.client.Response response = null;
        String screenName = null;
        if(nmlsId != null) {
        	LOG.info( "NmlsId found for enity. So getting records from lender API using NmlsId id : {} and screen name : {}", nmlsId, zillowScreenName );
        	FetchZillowReviewBodyByNMLS fetchZillowReviewBodyByNMLS = new FetchZillowReviewBodyByNMLS();                       
            LenderRef lenderRef = new LenderRef();
            lenderRef.setNmlsId(nmlsId);
            fetchZillowReviewBodyByNMLS.setLenderRef(lenderRef);
            fetchZillowReviewBodyByNMLS.setPartnerId( zillowPartnerId );
            ZillowIntegrationLenderApi zillowIntegrationLenderApi = zillowIntegrationApiBuilder.getZillowIntegrationLenderApi();
            try {
            	//Call zillow to fetch reviews for the profile name
	            response = zillowIntegrationLenderApi.fetchZillowReviewsByNMLS( fetchZillowReviewBodyByNMLS );
	            
	            if ( response != null ) {	        
	                String responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
	                
	                if ( responseString != null ) {
                        Map<String, Object> map = null;
                        try {
                            map = convertJsonStringToMap( responseString );
                            
                            if ( map != null ) {
                            	List<SurveyDetails> surveyDetailsList = new ArrayList<SurveyDetails>();
                                surveyDetailsList = profileManagementService.buildSurveyDetailFromZillowLenderReviewMap( map );
                                if(LOG.isDebugEnabled())
                                    LOG.debug( "no of records found from zillow is {}", surveyDetailsList.size() );
                                if(surveyDetailsList != null && surveyDetailsList.size() > 0) {
                                	List<HashMap<String, Object>> reviews = new ArrayList<HashMap<String, Object>>();
                                	reviews = (List<HashMap<String, Object>>) map.get( "reviews" );
                                    if ( reviews != null ) {
                                        for ( Map<String, Object> review : reviews ) {
                                        	HashMap<String, Object> individualReviewee = (HashMap<String, Object>) review.get( "individualReviewee" );
                                        	//get screen name 
                                        	screenName = (String) individualReviewee.get("screenName");
                                        	break;
                                        }
                                    }
                                }
                                //if screenName == null, no review present, ask to enter screen name
                                if(screenName == null ) {
                                	return "no-screen-name";
                                }
                            }
                        } catch ( IOException e ) {
                            LOG.warn( "Exception caught while parsing zillow reviews {}", e.getMessage() );
                            throw new UnavailableException( "Zillow reviews could not be fetched for  nmls: " + nmlsId );
                        }
	                }
	            }
            } catch (Exception e) {
            	return "invalid-nmls";
            }
        } else {
        	//throw error
        }
        
        session.removeAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
        model.addAttribute( CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES );
        model.addAttribute( "socialNetwork", "zillow" );
       
        
        profileSettings.getSocialMediaTokens().getZillowToken().setZillowScreenName(screenName);
        
        session.setAttribute( CommonConstants.USER_ACCOUNT_SETTINGS, profileSettings );
        session.setAttribute( CommonConstants.USER_PROFILE_SETTINGS, profileSettings );
        return  new Gson().toJson( profileSettings );
    }
    
    Map<String, Object> convertJsonStringToMap( String jsonString ) throws JsonParseException, JsonMappingException, IOException
    {
        Map<String, Object> map = new ObjectMapper().readValue( jsonString, new TypeReference<HashMap<String, Object>>() {} );
        return map;
    }
    
    //SS-1225
    /**
     * <b>SS-1225 : While adding Zillow profile using screen name, 
     * if the API returns a valid NMLS id, capture the NMLS id and store it for the user zillow profile.</b><br/><br/>
     * 
     * (For Mortgage user) if Zillow returns N/A or any other error for NMLS, user still can add by screen name. 
     * If the corresponding service returns a valid NMLSID format we will add NMLSID and screen name, otherwise only screen name.<br/><br/>
     * 
     * If no valid NMLS is found, existing NMLS id will be removed from DB (if already exists).
     * 
     */
    @SuppressWarnings ( { "unchecked", "unused" })
    @RequestMapping ( value = "/zillowSaveInfoByScreenNameForMortgage")
    @ResponseBody
    public String zillowSaveInfoByScreenNameForMortgage( Model model, HttpServletRequest request )
    {
        LOG.info( "Method zillowSaveInfoByScreenNameForMortgage() called from SocialManagementController" );
        HttpSession session = request.getSession( false );
        ZillowIntegrationAgentApi zillowIntegrationApi = zillowIntergrationApiBuilder.getZillowIntegrationAgentApi();
        User user = sessionHelper.getCurrentUser();
        
        String zillowScreenName = request.getParameter( "zillowScreenName" );
        Integer nmlsId = null;
        
        SocialMediaTokens mediaTokens = null;
        OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
            .getAttribute( CommonConstants.USER_ACCOUNT_SETTINGS );
        if ( profileSettings == null ) {
            profileSettings = (OrganizationUnitSettings) session.getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
        }
        
        try {
            String profileLink = null;
            retrofit.client.Response response = null;
            UserSettings userSettings = (UserSettings) session
                .getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            AccountType accountType = (AccountType) session.getAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            String collectionName = "";

            if ( userSettings == null || entityType == null || profileSettings == null ) {
                LOG.warn( "No user settings found in session" );
                throw new InvalidInputException( "No user settings found in session" );
            }
            
            //decode the zillow url
            zillowScreenName = java.net.URLDecoder.decode(zillowScreenName, "UTF-8");

            // if user has changed his Zillow account, then delete existing Zillow reviews
            if ( checkZillowAccountChanged( profileSettings, zillowScreenName ) ) {
                if(LOG.isDebugEnabled())
                    LOG.debug( "Deleting zillow feed for agent ID : {}", profileSettings.getIden() );
                surveyHandler.deleteExistingZillowSurveysByEntity( entityType, profileSettings.getIden() );
            }

            String errorCode = request.getParameter( "error" );
            if ( errorCode != null ) {
                LOG.error( "Error code : {}", errorCode );
                model.addAttribute( CommonConstants.ERROR, CommonConstants.YES );
                return JspResolver.SOCIAL_AUTH_MESSAGE;
            }
            switch ( entityType ) {
                case CommonConstants.AGENT_ID_COLUMN:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION;
                    break;

                case CommonConstants.BRANCH_ID_COLUMN:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION;
                    break;

                case CommonConstants.REGION_ID_COLUMN:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION;
                    break;

                case CommonConstants.COMPANY_ID_COLUMN:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION;
                    break;
            }
            
            String jsonString = null;
            int zillowReviewCount = 0;
            double zillowTotalScore = 0;
            if ( zillowScreenName.contains( "-" ) ) {
                zillowScreenName = zillowScreenName.replace( "-", " " );
            }
            zillowScreenName = zillowScreenName.trim();
            response = zillowIntegrationApi.fetchZillowReviewsByScreennameWithMaxCount( zillowWebserviceId,
                zillowScreenName );

            Map<String, Object> map = null;
            boolean updated = false;
            if ( response != null ) {
                jsonString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
            }

            //Store the API call details
            ExternalAPICallDetails zillowAPICallDetails = new ExternalAPICallDetails();
            zillowAPICallDetails.setHttpMethod( CommonConstants.HTTP_METHOD_GET );
            zillowAPICallDetails.setRequest( zillowAgentApiEndpoint + CommonConstants.ZILLOW_CALL_REQUEST + "&zws-id="
                + zillowWebserviceId + "&screenname=" + zillowScreenName );
            zillowAPICallDetails.setResponse( jsonString );
            zillowAPICallDetails.setRequestTime( new Date( System.currentTimeMillis() ) );
            zillowAPICallDetails.setSource( CommonConstants.ZILLOW_SOCIAL_SITE );
            //Store this record in mongo
            externalApiCallDetailsDao.insertApiCallDetails( zillowAPICallDetails );

            if ( jsonString != null ) {
                map = new ObjectMapper().readValue( jsonString, new TypeReference<HashMap<String, Object>>() {} );
            }

           String error = null;
           if(map != null){
               //get Profile url
               Map<String, Object> responseMap = new HashMap<String, Object>();
               Map<String, Object> resultMap = new HashMap<String, Object>();
               Map<String, Object> proReviews = new HashMap<String, Object>();
               Map<String, Object> proInfoMap = new HashMap<String, Object>();
               responseMap = (HashMap<String, Object>) map.get( "response" );
               if ( responseMap != null ) {
                   resultMap = (HashMap<String, Object>) responseMap.get( "results" );
                   if ( resultMap != null ) {
                       proInfoMap = (HashMap<String, Object>) resultMap.get( "proInfo" );
                       if ( proInfoMap != null ) {
                           profileLink = (String) proInfoMap.get( "profileURL" );
                           String NMLSNumber = (String) proInfoMap.get( "NMLSNumber" );
                           //If NMLSNumber is present, convert to Integer if a valid number
                           if(NMLSNumber != null && NMLSNumber.trim().length() > 0) {
                               try{
                                   nmlsId = new Integer(NMLSNumber);
                               } catch (Exception ex) {
                                   //for invalid number, no need to store NMLSID to DB, only screen name and link will be added.
                                   LOG.info( "Zillow exception : {}", ex );
                               }
                           }
                       }
                   }
               }    
               
               Map<String, Object> messageRes = (HashMap<String, Object>) map.get( "message" );                   
               error = (String) messageRes.get( "code" );
               
               if(error != null && !error.equals("0")) {//Zillow is returning 0 for successfull profile
                   return CommonConstants.ZILLOW_PROFILE_ERROR;
               }
            
               //update zillow count
               profileManagementService.modifyZillowCallCount( map );
               List<SurveyDetails> surveyDetailsList =  profileManagementService.buildSurveyDetailFromZillowAgentReviewMap( map );
                if ( surveyDetailsList != null && !surveyDetailsList.isEmpty() ) {
                    organizationManagementService.pushZillowReviews( surveyDetailsList, collectionName, profileSettings,
                        user.getCompany().getCompanyId() );
                }
            }
            
            int accountMasterId = accountType.getValue();
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService
                    .getCompanySettings( user.getCompany().getCompanyId() );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                mediaTokens = companySettings.getSocialMediaTokens();
                mediaTokens = updateZillow( mediaTokens, profileLink, zillowScreenName, nmlsId );
                mediaTokens = socialManagementService.checkOrAddZillowLastUpdated( mediaTokens );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, mediaTokens );
                companySettings.setSocialMediaTokens( mediaTokens );
                //update SETTINGS_SET_STATUS of COMPANY table to set.
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.ZILLOW,
                        CommonConstants.SET_SETTINGS );
                    
                    userManagementService.updateCompany( company );
                }
                for ( ProfileStage stage : companySettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "ZILLOW_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( companySettings.getProfileStages(), companySettings,
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                userSettings.setCompanySettings( companySettings );
                profileSettings.setSocialMediaTokens( companySettings.getSocialMediaTokens() );
                updated = true;

            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    LOG.warn( "No Region settings found in current session" );
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                mediaTokens = regionSettings.getSocialMediaTokens();
                mediaTokens = updateZillow( mediaTokens, profileLink, zillowScreenName, nmlsId );
                mediaTokens = socialManagementService.checkOrAddZillowLastUpdated( mediaTokens );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, mediaTokens );
                regionSettings.setSocialMediaTokens( mediaTokens );
                //update SETTINGS_SET_STATUS of REGION table to set.
                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.ZILLOW,
                        CommonConstants.SET_SETTINGS );
                    
                    userManagementService.updateRegion( region );
                }
                for ( ProfileStage stage : regionSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "ZILLOW_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( regionSettings.getProfileStages(), regionSettings,
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                userSettings.getRegionSettings().put( entityId, regionSettings );
                profileSettings.setSocialMediaTokens( regionSettings.getSocialMediaTokens() );
                updated = true;
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService
                    .getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                mediaTokens = branchSettings.getSocialMediaTokens();
                mediaTokens = updateZillow( mediaTokens, profileLink, zillowScreenName, nmlsId );
                mediaTokens = socialManagementService.checkOrAddZillowLastUpdated( mediaTokens );
                mediaTokens = socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, mediaTokens );
                branchSettings.setSocialMediaTokens( mediaTokens );
                //update SETTINGS_SET_STATUS of BRANCH table to set.
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.ZILLOW,
                        CommonConstants.SET_SETTINGS );
                    
                    userManagementService.updateBranch( branch );
                }
                for ( ProfileStage stage : branchSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "ZILLOW_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( branchSettings.getProfileStages(), branchSettings,
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
                userSettings.getBranchSettings().put( entityId, branchSettings );
                profileSettings.setSocialMediaTokens( branchSettings.getSocialMediaTokens() );
                updated = true;
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN )
                || accountMasterId == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    LOG.warn( "No Agent settings found in current session" );
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                mediaTokens = agentSettings.getSocialMediaTokens();
                mediaTokens = updateZillow( mediaTokens, profileLink, zillowScreenName, nmlsId );
                mediaTokens = socialManagementService.checkOrAddZillowLastUpdated( mediaTokens );
                mediaTokens = socialManagementService.updateAgentSocialMediaTokens( agentSettings, mediaTokens );
                agentSettings.setSocialMediaTokens( mediaTokens );
                User agent = userManagementService.getUserByUserId( agentSettings.getIden() );
                if ( agent != null ) {
                    
                    userManagementService.updateUser( agent );

                    // updating solr review count for user
                    long reviewCount = profileManagementService.getReviewsCount( agent.getUserId(), -1, -1,
                        CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false, false, true, zillowReviewCount );
                    solrSearchService.editUserInSolr( agent.getUserId(), CommonConstants.REVIEW_COUNT_SOLR,
                        String.valueOf( reviewCount ) );
                }
                for ( ProfileStage stage : agentSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "ZILLOW_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( agentSettings.getProfileStages(), agentSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                userSettings.setAgentSettings( agentSettings );
                profileSettings.setSocialMediaTokens( agentSettings.getSocialMediaTokens() );
                updated = true;
            }
            if ( !updated ) {
                LOG.warn( "Invalid input exception occurred while creating access token for zillow" );
                throw new InvalidInputException( "Invalid input exception occurred while creating access token for zillow",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            //Add action to social connection history
            socialManagementService.updateSocialConnectionsHistory( entityType, entityId, mediaTokens,
                CommonConstants.ZILLOW_SOCIAL_SITE, CommonConstants.SOCIAL_MEDIA_CONNECTED );
        } catch ( Exception e ) {
             LOG.error( "Exception while setting zillow profile link. Reason : " , e );
            return CommonConstants.ERROR;
        }

        session.removeAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
        model.addAttribute( CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES );
        model.addAttribute( "socialNetwork", "zillow" );
        session.setAttribute( CommonConstants.USER_ACCOUNT_SETTINGS, profileSettings );
        session.setAttribute( CommonConstants.USER_PROFILE_SETTINGS, profileSettings );
    
        return CommonConstants.SUCCESS_ATTRIBUTE;
    }

    @SuppressWarnings ( { "unchecked", "unused" })
    @RequestMapping ( value = "/zillowSaveInfo")
    @ResponseBody
    public String saveZillowDetails( Model model, HttpServletRequest request )
    {
        LOG.info( "Method saveZillowDetails() called from SocialManagementController" );
        HttpSession session = request.getSession( false );
        ZillowIntegrationAgentApi zillowIntegrationApi = zillowIntergrationApiBuilder.getZillowIntegrationAgentApi();
        User user = sessionHelper.getCurrentUser();
        //SS-1224 - zillowScreenName update
        String zillowScreenName = request.getParameter( "zillowScreenName" );
        String nmlsTemp = request.getParameter( "nmlsId" );
        Integer nmlsId = null;
        if(nmlsTemp != null && nmlsTemp.trim().length() > 0)
        	nmlsId = new Integer(nmlsTemp);
        SocialMediaTokens mediaTokens = null;
        OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
            .getAttribute( CommonConstants.USER_ACCOUNT_SETTINGS );
        if ( profileSettings == null ) {
            profileSettings = (OrganizationUnitSettings) session.getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
        }
        
    
        
        
        if ( zillowScreenName == null || zillowScreenName.equals("") ) {
            model.addAttribute( "Error", "Please provide either the zillow screen name." );
        } else {
            try {
                String profileLink = null;
                retrofit.client.Response response = null;
                UserSettings userSettings = (UserSettings) session
                    .getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
                AccountType accountType = (AccountType) session.getAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION );
                long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
                String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
                String collectionName = "";

                if ( userSettings == null || entityType == null || profileSettings == null ) {
                    throw new InvalidInputException( "No user settings found in session" );
                }
                
                
                //decode the zillow url
                zillowScreenName = java.net.URLDecoder.decode(zillowScreenName, "UTF-8");
               
                
                // if user has changed his Zillow account, then delete existing Zillow reviews
                if ( checkZillowAccountChanged( profileSettings, zillowScreenName ) ) {
                    LOG.debug( "Deleting zillow feed for agent ID : {}", profileSettings.getIden() );
                    surveyHandler.deleteExistingZillowSurveysByEntity( entityType, profileSettings.getIden() );
                }

                String errorCode = request.getParameter( "error" );
                if ( errorCode != null ) {
                    LOG.error( "Error code : {}", errorCode );
                    model.addAttribute( CommonConstants.ERROR, CommonConstants.YES );
                    return JspResolver.SOCIAL_AUTH_MESSAGE;
                }
                switch ( entityType ) {
                    case CommonConstants.AGENT_ID_COLUMN:
                        collectionName = MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION;
                        break;

                    case CommonConstants.BRANCH_ID_COLUMN:
                        collectionName = MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION;
                        break;

                    case CommonConstants.REGION_ID_COLUMN:
                        collectionName = MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION;
                        break;

                    case CommonConstants.COMPANY_ID_COLUMN:
                        collectionName = MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION;
                        break;

                }
                String jsonString = null;
                int zillowReviewCount = 0;
                double zillowTotalScore = 0;
                if ( zillowScreenName.contains( "-" ) ) {
                    zillowScreenName = zillowScreenName.replace( "-", " " );
                }
                zillowScreenName = zillowScreenName.trim();
                response = zillowIntegrationApi.fetchZillowReviewsByScreennameWithMaxCount( zillowWebserviceId,
                    zillowScreenName );

                Map<String, Object> map = null;
                boolean updated = false;
                if ( response != null ) {
                    jsonString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
                }

                //Store the API call details
                ExternalAPICallDetails zillowAPICallDetails = new ExternalAPICallDetails();
                zillowAPICallDetails.setHttpMethod( CommonConstants.HTTP_METHOD_GET );
                zillowAPICallDetails.setRequest( zillowAgentApiEndpoint + CommonConstants.ZILLOW_CALL_REQUEST + "&zws-id="
                    + zillowWebserviceId + "&screenname=" + zillowScreenName );
                zillowAPICallDetails.setResponse( jsonString );
                zillowAPICallDetails.setRequestTime( new Date( System.currentTimeMillis() ) );
                zillowAPICallDetails.setSource( CommonConstants.ZILLOW_SOCIAL_SITE );
                //Store this record in mongo
                externalApiCallDetailsDao.insertApiCallDetails( zillowAPICallDetails );

                if ( jsonString != null ) {
                    map = new ObjectMapper().readValue( jsonString, new TypeReference<HashMap<String, Object>>() {} );
                }

               String error = null;
               if(map != null){
                   //get Profile url
                   Map<String, Object> responseMap = new HashMap<String, Object>();
                   Map<String, Object> resultMap = new HashMap<String, Object>();
                   Map<String, Object> proReviews = new HashMap<String, Object>();
                   Map<String, Object> proInfoMap = new HashMap<String, Object>();
                   responseMap = (HashMap<String, Object>) map.get( "response" );
                   if ( responseMap != null ) {
                       resultMap = (HashMap<String, Object>) responseMap.get( "results" );
                       if ( resultMap != null ) {
                           proInfoMap = (HashMap<String, Object>) resultMap.get( "proInfo" );
                           if ( proInfoMap != null ) {
                               profileLink = (String) proInfoMap.get( "profileURL" );
                           }
                       }
                   }    
                   
                   Map<String, Object> messageRes = (HashMap<String, Object>) map.get( "message" );                   
                   error = (String) messageRes.get( "code" );
                   
	               if(error != null && !error.equals("0")) {//Zillow is returning 0 for successfull profile
	            	   return CommonConstants.ZILLOW_PROFILE_ERROR;
	               }
               	
                   //update zillow count
                   profileManagementService.modifyZillowCallCount( map );
                   List<SurveyDetails> surveyDetailsList = profileManagementService
                        .buildSurveyDetailFromZillowAgentReviewMap( map );
                    if ( surveyDetailsList != null && !surveyDetailsList.isEmpty() ) {
                        organizationManagementService.pushZillowReviews( surveyDetailsList, collectionName, profileSettings,
                            user.getCompany().getCompanyId() );
                    }
               }
                
               
               	
                int accountMasterId = accountType.getValue();
                if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                    OrganizationUnitSettings companySettings = organizationManagementService
                        .getCompanySettings( user.getCompany().getCompanyId() );
                    if ( companySettings == null ) {
                        LOG.warn( "No company settings found in current session" );
                        throw new InvalidInputException( "No company settings found in current session" );
                    }
                    mediaTokens = companySettings.getSocialMediaTokens();
                    mediaTokens = updateZillow( mediaTokens, profileLink, zillowScreenName, nmlsId );
                    mediaTokens = socialManagementService.checkOrAddZillowLastUpdated( mediaTokens );
                    mediaTokens = socialManagementService.updateSocialMediaTokens(
                        MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, mediaTokens );
                    companySettings.setSocialMediaTokens( mediaTokens );
                    //update SETTINGS_SET_STATUS of COMPANY table to set.
                    Company company = userManagementService.getCompanyById( companySettings.getIden() );
                    if ( company != null ) {
                        settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.ZILLOW,
                            CommonConstants.SET_SETTINGS );
                       
                        userManagementService.updateCompany( company );
                    }
                    for ( ProfileStage stage : companySettings.getProfileStages() ) {
                        if ( stage.getProfileStageKey().equalsIgnoreCase( "ZILLOW_PRF" ) ) {
                            stage.setStatus( CommonConstants.STATUS_INACTIVE );
                        }
                    }
                    profileManagementService.updateProfileStages( companySettings.getProfileStages(), companySettings,
                        MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                    userSettings.setCompanySettings( companySettings );
                    profileSettings.setSocialMediaTokens( companySettings.getSocialMediaTokens() );
                    updated = true;

                } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                    OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                    if ( regionSettings == null ) {
                        throw new InvalidInputException( "No Region settings found in current session" );
                    }
                    mediaTokens = regionSettings.getSocialMediaTokens();
                    mediaTokens = updateZillow( mediaTokens, profileLink, zillowScreenName, nmlsId );
                    mediaTokens = socialManagementService.checkOrAddZillowLastUpdated( mediaTokens );
                    mediaTokens = socialManagementService.updateSocialMediaTokens(
                        MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, mediaTokens );
                    regionSettings.setSocialMediaTokens( mediaTokens );
                    //update SETTINGS_SET_STATUS of REGION table to set.
                    Region region = userManagementService.getRegionById( regionSettings.getIden() );
                    if ( region != null ) {
                        settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.ZILLOW,
                            CommonConstants.SET_SETTINGS );
                        
                        userManagementService.updateRegion( region );
                    }
                    for ( ProfileStage stage : regionSettings.getProfileStages() ) {
                        if ( stage.getProfileStageKey().equalsIgnoreCase( "ZILLOW_PRF" ) ) {
                            stage.setStatus( CommonConstants.STATUS_INACTIVE );
                        }
                    }
                    profileManagementService.updateProfileStages( regionSettings.getProfileStages(), regionSettings,
                        MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                    userSettings.getRegionSettings().put( entityId, regionSettings );
                    profileSettings.setSocialMediaTokens( regionSettings.getSocialMediaTokens() );
                    updated = true;
                } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                    OrganizationUnitSettings branchSettings = organizationManagementService
                        .getBranchSettingsDefault( entityId );
                    if ( branchSettings == null ) {
                        LOG.warn( "No Branch settings found in current session" );
                        throw new InvalidInputException( "No Branch settings found in current session" );
                    }
                    mediaTokens = branchSettings.getSocialMediaTokens();
                    mediaTokens = updateZillow( mediaTokens, profileLink, zillowScreenName, nmlsId );
                    mediaTokens = socialManagementService.checkOrAddZillowLastUpdated( mediaTokens );
                    mediaTokens = socialManagementService.updateSocialMediaTokens(
                        MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, mediaTokens );
                    branchSettings.setSocialMediaTokens( mediaTokens );
                    //update SETTINGS_SET_STATUS of BRANCH table to set.
                    Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                    if ( branch != null ) {
                        settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.ZILLOW,
                            CommonConstants.SET_SETTINGS );
                        
                        userManagementService.updateBranch( branch );
                    }
                    for ( ProfileStage stage : branchSettings.getProfileStages() ) {
                        if ( stage.getProfileStageKey().equalsIgnoreCase( "ZILLOW_PRF" ) ) {
                            stage.setStatus( CommonConstants.STATUS_INACTIVE );
                        }
                    }
                    profileManagementService.updateProfileStages( branchSettings.getProfileStages(), branchSettings,
                        MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
                    userSettings.getBranchSettings().put( entityId, branchSettings );
                    profileSettings.setSocialMediaTokens( branchSettings.getSocialMediaTokens() );
                    updated = true;
                } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN )
                    || accountMasterId == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                    AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                    if ( agentSettings == null ) {
                        LOG.warn( "No Agent settings found in current session" );
                        throw new InvalidInputException( "No Agent settings found in current session" );
                    }
                    mediaTokens = agentSettings.getSocialMediaTokens();
                    mediaTokens = updateZillow( mediaTokens, profileLink, zillowScreenName, nmlsId );
                    mediaTokens = socialManagementService.checkOrAddZillowLastUpdated( mediaTokens );
                    mediaTokens = socialManagementService.updateAgentSocialMediaTokens( agentSettings, mediaTokens );
                    agentSettings.setSocialMediaTokens( mediaTokens );
                    User agent = userManagementService.getUserByUserId( agentSettings.getIden() );
                    if ( agent != null ) {
                        
                        userManagementService.updateUser( agent );

                        // updating solr review count for user
                        long reviewCount = profileManagementService.getReviewsCount( agent.getUserId(), -1, -1,
                            CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false, false, true, zillowReviewCount );
                        solrSearchService.editUserInSolr( agent.getUserId(), CommonConstants.REVIEW_COUNT_SOLR,
                            String.valueOf( reviewCount ) );
                    }
                    for ( ProfileStage stage : agentSettings.getProfileStages() ) {
                        if ( stage.getProfileStageKey().equalsIgnoreCase( "ZILLOW_PRF" ) ) {
                            stage.setStatus( CommonConstants.STATUS_INACTIVE );
                        }
                    }
                    profileManagementService.updateProfileStages( agentSettings.getProfileStages(), agentSettings,
                        MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                    userSettings.setAgentSettings( agentSettings );
                    profileSettings.setSocialMediaTokens( agentSettings.getSocialMediaTokens() );
                    updated = true;
                }
                if ( !updated ) {
                    throw new InvalidInputException( "Invalid input exception occurred while creating access token for zillow",
                        DisplayMessageConstants.GENERAL_ERROR );
                }

                //Add action to social connection history
                socialManagementService.updateSocialConnectionsHistory( entityType, entityId, mediaTokens,
                    CommonConstants.ZILLOW_SOCIAL_SITE, CommonConstants.SOCIAL_MEDIA_CONNECTED );
            } catch ( Exception e ) {
                LOG.error( "Exception while setting zillow profile link. Reason : " , e );
                return CommonConstants.ERROR;
            }

            session.removeAttribute( CommonConstants.SOCIAL_REQUEST_TOKEN );
            model.addAttribute( CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES );
            model.addAttribute( "socialNetwork", "zillow" );
            session.setAttribute( CommonConstants.USER_ACCOUNT_SETTINGS, profileSettings );
            session.setAttribute( CommonConstants.USER_PROFILE_SETTINGS, profileSettings );
        }
        return CommonConstants.SUCCESS_ATTRIBUTE;
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
        } else if ( socialNetwork.equalsIgnoreCase( "instagram" ) ) {
            if ( usersettings != null && usersettings.getAgentSettings() != null
                    && usersettings.getAgentSettings().getSocialMediaTokens() != null
                    && usersettings.getAgentSettings().getSocialMediaTokens().getInstagramToken() != null ) {
                if ( usersettings.getAgentSettings().getSocialMediaTokens().getInstagramToken().getPageLink() != null ) {
                    profileUrl = usersettings.getAgentSettings().getSocialMediaTokens().getInstagramToken().getPageLink();
                }
            }
        }

        LOG.info( "Method getProfileUrl() finished from SocialManagementController" );
        return profileUrl;
    }
    
    /**
     * Disconnect Zillow either with or without deleting Zillow reviews
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/disconnectZillow", method = RequestMethod.POST)
    public String disconnectZillow( HttpServletRequest request )
    {
        String socialMedia =  CommonConstants.ZILLOW_SOCIAL_SITE;
        String keepOrDeleteReview = request.getParameter( "keepOrDeleteReview" );
        boolean removeFeed = true;
        
        HttpSession session = request.getSession();
        OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
            .getAttribute( CommonConstants.USER_ACCOUNT_SETTINGS );
        SocialMediaTokens mediaTokens = null;
        try {
            UserSettings userSettings = (UserSettings) session
                .getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || entityType == null ) {
                LOG.warn( "No user settings found in session" );
                throw new InvalidInputException( "No user settings found in session" );
            }
            if ( socialMedia == null || socialMedia.isEmpty() ) {
                LOG.warn( "Social media can not be null or empty" );
                throw new InvalidInputException( "Social media can not be null or empty" );
            }
            
            SettingsForApplication settings = SettingsForApplication.ZILLOW;
            boolean isZillow = false;
            boolean unset = CommonConstants.UNSET_SETTINGS;
            
            // Check for the collection to update
            OrganizationUnitSettings unitSettings = null;
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                unitSettings = organizationManagementService.getCompanySettings( entityId );
                if(!checkForExistiongProfile(unitSettings))
                    return "no-zillow";
                mediaTokens = unitSettings.getSocialMediaTokens();
                unitSettings = socialManagementService.disconnectSocialNetwork( socialMedia, removeFeed, unitSettings,
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                userSettings.setCompanySettings( unitSettings );
                //update SETTINGS_SET_STATUS to unset in COMPANY table
                Company company = userManagementService.getCompanyById( entityId );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, settings, unset );
                    
                    userManagementService.updateCompany( company );
                }
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                unitSettings = organizationManagementService.getRegionSettings( entityId );
                if(!checkForExistiongProfile(unitSettings))
                    return "no-zillow";
                mediaTokens = unitSettings.getSocialMediaTokens();
                unitSettings = socialManagementService.disconnectSocialNetwork( socialMedia, removeFeed, unitSettings,
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                userSettings.getRegionSettings().put( entityId, unitSettings );
                //update SETTINGS_SET_STATUS to unset in REGION table
                Region region = userManagementService.getRegionById( entityId );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, settings, unset );
                    // Set IS_ZILLOW_CONNECTED to false
                    // if ( isZillow ) {
                    //    region.setIsZillowConnected( CommonConstants.ZILLOW_DISCONNECTED );
                    //    region.setZillowAverageScore( 0.0 );
                    //    region.setZillowReviewCount( 0 );
                    // }
                    userManagementService.updateRegion( region );
                }
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                unitSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if(!checkForExistiongProfile(unitSettings))
                    return "no-zillow";
                mediaTokens = unitSettings.getSocialMediaTokens();
                unitSettings = socialManagementService.disconnectSocialNetwork( socialMedia, removeFeed, unitSettings,
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
                userSettings.getBranchSettings().put( entityId, unitSettings );
                //update SETTINGS_SET_STATUS to unset in BRANCH table
                Branch branch = userManagementService.getBranchById( entityId );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, settings, unset );
                    // Set IS_ZILLOW_CONNECTED to false
                    // if ( isZillow ) {
                    //    branch.setIsZillowConnected( CommonConstants.ZILLOW_DISCONNECTED );
                    //    branch.setZillowAverageScore( 0.0 );
                    //    branch.setZillowReviewCount( 0 );
                    // }
                    userManagementService.updateBranch( branch );
                }
            }
            if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                unitSettings = userManagementService.getUserSettings( entityId );
                
                if(!checkForExistiongProfile(unitSettings))
                    return "no-zillow";
                
                mediaTokens = unitSettings.getSocialMediaTokens();
                unitSettings = socialManagementService.disconnectSocialNetwork( socialMedia, removeFeed, unitSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                userSettings.setAgentSettings( (AgentSettings) unitSettings );
            }
            
            if(profileSettings != null) {
                profileSettings.setSocialMediaTokens( unitSettings.getSocialMediaTokens() );
            }

            // Remove zillow reviews on disconnect.
            if ( socialMedia.equals( CommonConstants.ZILLOW_SOCIAL_SITE ) && keepOrDeleteReview != null && keepOrDeleteReview.equals( "delete-review" )) {//and is to delete reviews
                LOG.debug( "Deleting zillow feed for agent ID : " + entityId );
                surveyHandler.deleteExistingZillowSurveysByEntity( entityType, entityId );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "Exception occured in disconnectSocialNetwork() while disconnecting with the social Media. Reason : ",
                e );
            return "failue";
        }

        session.setAttribute( CommonConstants.USER_ACCOUNT_SETTINGS, profileSettings );
        return "success";
    }
    
    private Boolean checkForExistiongProfile(OrganizationUnitSettings unitSettings) {
        if(unitSettings != null
            && ( unitSettings.getSocialMediaTokens() == null
            || unitSettings.getSocialMediaTokens().getZillowToken() == null )) {
            return false;
            //"no-zillow"
        } else {
            return true;
        }
    }


    @ResponseBody
    @RequestMapping ( value = "/disconnectsocialmedia", method = RequestMethod.POST)
    public String disconnectSocialMedia( HttpServletRequest request )
    {
        String socialMedia = request.getParameter( "socialMedia" );
        String removeFeedStr = request.getParameter( "removeFeed" );
        boolean removeFeed = false;
        if ( removeFeedStr != null && !removeFeedStr.isEmpty() )
        {
            removeFeed = Boolean.parseBoolean( removeFeedStr );
        }
        HttpSession session = request.getSession();
        OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
            .getAttribute( CommonConstants.USER_ACCOUNT_SETTINGS );
        SocialMediaTokens mediaTokens = null;
        try {
            UserSettings userSettings = (UserSettings) session
                .getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }
            if ( socialMedia == null || socialMedia.isEmpty() ) {
                throw new InvalidInputException( "Social media can not be null or empty" );
            }

            boolean isZillow = false;
            boolean unset = CommonConstants.UNSET_SETTINGS;
            SettingsForApplication settings;

            switch ( socialMedia ) {
                case CommonConstants.FACEBOOK_SOCIAL_SITE:
                    settings = SettingsForApplication.FACEBOOK;
                    break;

                case CommonConstants.TWITTER_SOCIAL_SITE:
                    settings = SettingsForApplication.TWITTER;
                    break;

                case CommonConstants.GOOGLE_SOCIAL_SITE:
                    settings = SettingsForApplication.GOOGLE_PLUS;
                    break;

                case CommonConstants.LINKEDIN_SOCIAL_SITE:
                    settings = SettingsForApplication.LINKED_IN;
                    break;

                case CommonConstants.ZILLOW_SOCIAL_SITE:
                    settings = SettingsForApplication.ZILLOW;
                    isZillow = true;
                    break;

                case CommonConstants.INSTAGRAM_SOCIAL_SITE:
                    settings = SettingsForApplication.INSTAGRAM;
                    break;

                default:
                    throw new InvalidInputException( "Invalid social media token entered" );
            }

            // Check for the collection to update
            OrganizationUnitSettings unitSettings = null;
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                unitSettings = organizationManagementService.getCompanySettings( entityId );
                mediaTokens = unitSettings.getSocialMediaTokens();
                unitSettings = socialManagementService.disconnectSocialNetwork( socialMedia, removeFeed, unitSettings,
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                userSettings.setCompanySettings( unitSettings );
                //update SETTINGS_SET_STATUS to unset in COMPANY table
                Company company = userManagementService.getCompanyById( entityId );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, settings, unset );
                    
                    userManagementService.updateCompany( company );
                }
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                unitSettings = organizationManagementService.getRegionSettings( entityId );
                mediaTokens = unitSettings.getSocialMediaTokens();
                unitSettings = socialManagementService.disconnectSocialNetwork( socialMedia, removeFeed, unitSettings,
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                userSettings.getRegionSettings().put( entityId, unitSettings );
                //update SETTINGS_SET_STATUS to unset in REGION table
                Region region = userManagementService.getRegionById( entityId );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, settings, unset );
                    // Set IS_ZILLOW_CONNECTED to false
                    // if ( isZillow ) {
                    //    region.setIsZillowConnected( CommonConstants.ZILLOW_DISCONNECTED );
                    //    region.setZillowAverageScore( 0.0 );
                    //    region.setZillowReviewCount( 0 );
                    // }
                    userManagementService.updateRegion( region );
                }
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                unitSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                mediaTokens = unitSettings.getSocialMediaTokens();
                unitSettings = socialManagementService.disconnectSocialNetwork( socialMedia, removeFeed, unitSettings,
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
                userSettings.getBranchSettings().put( entityId, unitSettings );
                //update SETTINGS_SET_STATUS to unset in BRANCH table
                Branch branch = userManagementService.getBranchById( entityId );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, settings, unset );
                    // Set IS_ZILLOW_CONNECTED to false
                    // if ( isZillow ) {
                    //    branch.setIsZillowConnected( CommonConstants.ZILLOW_DISCONNECTED );
                    //    branch.setZillowAverageScore( 0.0 );
                    //    branch.setZillowReviewCount( 0 );
                    // }
                    userManagementService.updateBranch( branch );
                }
            }
            if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                unitSettings = userManagementService.getUserSettings( entityId );
                mediaTokens = unitSettings.getSocialMediaTokens();
                unitSettings = socialManagementService.disconnectSocialNetwork( socialMedia, removeFeed, unitSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                userSettings.setAgentSettings( (AgentSettings) unitSettings );
            }
            profileSettings.setSocialMediaTokens( unitSettings.getSocialMediaTokens() );

            // Remove zillow reviews on disconnect.
            if ( socialMedia.equals( CommonConstants.ZILLOW_SOCIAL_SITE ) ) {
                LOG.debug( "Deleting zillow feed for agent ID : " + entityId );
                surveyHandler.deleteExistingZillowSurveysByEntity( entityType, entityId );
            }

            // Set IS_ZILLOW_CONNECTED to false
            if ( isZillow ) {
               
                long reviewCount = profileManagementService.getReviewsCount( entityId, -1, -1,
                    CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false, false );
                solrSearchService.editUserInSolr( entityId, CommonConstants.REVIEW_COUNT_SOLR, String.valueOf( reviewCount ) );
            }

            //Add action to social connection history
            socialManagementService.updateSocialConnectionsHistory( entityType, entityId, mediaTokens, socialMedia,
                CommonConstants.SOCIAL_MEDIA_DISCONNECTED );
        } catch ( NonFatalException e ) {
            LOG.error( "Exception occured in disconnectSocialNetwork() while disconnecting with the social Media. Reason : ",
                e );
            return "failue";
        }

        session.setAttribute( CommonConstants.USER_ACCOUNT_SETTINGS, profileSettings );
        return "success";
    }


    @RequestMapping ( value = "/getsocialmediatokenonsettingspage", method = RequestMethod.GET)
    public String getSocialMediaTokenonSettingsPage( HttpServletRequest request, Model model )
    {
        LOG.info( "Inside getSocialMediaTokenonSettingsPage() method" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession();

        try {
            UserSettings userSettings = (UserSettings) session
                .getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
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

            //Code to determine if social media can be overridden during autologin. 
            //If user is real tech or SS admin then allow override for social media.REALTECH_USER_ID is set only for real tech or SS admin 
            //else allow override based on company specific boolean being stored in mongo
            boolean allowOverrideForSocialMedia = false;
            Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
            if ( adminUserid != null ) {
                allowOverrideForSocialMedia = true;
            } else if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                allowOverrideForSocialMedia = unitSettings.isAllowOverrideForSocialMedia();
            } else {
                OrganizationUnitSettings companySettings = organizationManagementService
                    .getCompanySettings( user.getCompany().getCompanyId() );
                allowOverrideForSocialMedia = companySettings.isAllowOverrideForSocialMedia();
            }
            model.addAttribute( "allowOverrideForSocialMedia", allowOverrideForSocialMedia );

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
                    model.addAttribute( "profileSettings", tokens.getZillowToken() );
                    session.setAttribute( CommonConstants.USER_ACCOUNT_SETTINGS, unitSettings );
                }
                if ( tokens.getYelpToken() != null && tokens.getYelpToken().getYelpPageLink() != null ) {
                    model.addAttribute( "yelpLink", tokens.getYelpToken().getYelpPageLink() );
                }
                if ( tokens.getLendingTreeToken() != null
                    && tokens.getLendingTreeToken().getLendingTreeProfileLink() != null ) {
                    model.addAttribute( "lendingtreeLink", tokens.getLendingTreeToken().getLendingTreeProfileLink() );
                }
                if ( tokens.getRealtorToken() != null && tokens.getRealtorToken().getRealtorProfileLink() != null ) {
                    model.addAttribute( "realtorLink", tokens.getRealtorToken().getRealtorProfileLink() );
                }
	            if ( tokens.getGoogleBusinessToken() != null
		            && tokens.getGoogleBusinessToken().getGoogleBusinessLink() != null ) {
		            model.addAttribute( "googleBusinessLink", tokens.getGoogleBusinessToken().getGoogleBusinessLink() );
	            }
                if ( tokens.getInstagramToken() != null
                        && tokens.getInstagramToken().getPageLink() != null ) {
                    model.addAttribute( "instagramLink", tokens.getInstagramToken().getPageLink() );
                }
            }
        } catch ( NonFatalException e ) {
            LOG.error( "Exception occured in getSocialMediaTokenonSettingsPage()" );
            return "failue";
        }
        return JspResolver.SOCIAL_MEDIA_TOKENS;
    }


    /**
     * Method to show the social monitor page
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/showsocialmonitortpage", method = RequestMethod.GET)
    public String initSocialMonitorPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Social Monitor page started" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );

        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        //Validate user
        try {
            if ( user == null ) {
                LOG.warn( "No user found in session" );
                throw new InvalidInputException( "No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION );
            }
            if ( user.getStatus() != CommonConstants.STATUS_ACTIVE ) {
                LOG.error( "Inactive or unauthorized users can not access social monitor page" );
                model.addAttribute( "message", messageUtils.getDisplayMessage(
                    DisplayMessageConstants.USER_MANAGEMENT_NOT_AUTHORIZED, DisplayMessageType.ERROR_MESSAGE ) );
            }
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException in while showing social monitor. Reason : ",nonFatalException );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        //JIRA SS-1287
        try {
            Long lastBuild = batchTrackerService
                .getLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_SOCIAL_MONITOR_LAST_BUILD );
            model.addAttribute( "lastBuild", lastBuild );
        } catch ( NoRecordsFetchedException | InvalidInputException e ) {
            LOG.error( "NoRecordsFetchedException while getting last build time. Reason  : ", e );
        }

        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            model.addAttribute( "columnName", entityType );
            model.addAttribute( "columnValue", entityId );
            model.addAttribute( "showSendSurveyPopupAdmin", String.valueOf( true ) );
        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            model.addAttribute( "columnName", entityType );
            model.addAttribute( "columnValue", entityId );
            model.addAttribute( "showSendSurveyPopupAdmin", String.valueOf( true ) );
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            model.addAttribute( "columnName", entityType );
            model.addAttribute( "columnValue", entityId );
            model.addAttribute( "showSendSurveyPopupAdmin", String.valueOf( true ) );
        } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
            model.addAttribute( "columnName", CommonConstants.AGENT_ID_COLUMN );
            model.addAttribute( "columnValue", entityId );
        }
        return JspResolver.SOCIAL_MONITOR;
    }


    /**
     * Method to search for social posts given a search query
     * 
     * @param model
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/searchSocialPosts", method = RequestMethod.GET)
    public String searchSocialPosts( Model model, HttpServletRequest request )
    {
        LOG.info( "Method searchSocialPosts() started." );
        int startIndex = 0;
        int batchSize = 0;
        String posts = "";
        long count = 0;
        Set<Long> userIdSet = new HashSet<Long>();
        Set<Long> branchIdSet = new HashSet<Long>();
        Set<Long> regionIdSet = new HashSet<Long>();
        Set<Long> companyIdSet = new HashSet<Long>();
        try {
            String startIndexStr = request.getParameter( "startIndex" );
            String batchSizeStr = request.getParameter( "batchSize" );
            String entityType = request.getParameter( "entityType" );
            long entityId = Long.parseLong( request.getParameter( "entityId" ) );
            try {
                if ( startIndexStr == null || startIndexStr.isEmpty() ) {
                    LOG.warn( "Invalid value found in startIndex. It cannot be null or empty." );
                    throw new InvalidInputException( "Invalid value found in startIndex. It cannot be null or empty." );
                }
                if ( batchSizeStr == null || batchSizeStr.isEmpty() ) {
                    LOG.error( "Invalid value found in batchSizeStr. It cannot be null or empty." );
                    batchSize = SOLR_BATCH_SIZE;
                }

                startIndex = Integer.parseInt( startIndexStr );
                batchSize = Integer.parseInt( batchSizeStr );
            } catch ( NumberFormatException e ) {
                LOG.warn( "NumberFormatException while finding social posts. Reason : " , e );
                throw new NonFatalException( "NumberFormatException while searching for social posts", e );
            }
            String searchQuery = request.getParameter( "searchQuery" );
            if ( searchQuery == null || searchQuery.isEmpty() ) {
                LOG.error( "Invalid search key passed in method searchSocialPosts()." );
                searchQuery = "";
            } else {
                model.addAttribute( "currentSearchQuery", searchQuery.trim() );
            }
            User admin = sessionHelper.getCurrentUser();
            if ( admin == null ) {
                LOG.warn( "No user found in session" );
                throw new InvalidInputException( "No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION );
            }

            // fetching users from solr
            try {
                SolrDocumentList results = solrSearchService.searchPostText( entityType, entityId, startIndex, batchSize,
                    searchQuery.trim() );
                count = results.getNumFound();
                Collection<SocialPost> postlist = solrSearchService.getSocialPostsFromSolrDocuments( results );

                //Store the posts, along with agent,branch,region and company names where present.
                SocialMonitorData socialMonitorData = new SocialMonitorData();
                Collection<SocialMonitorPost> socialMonitorPosts = new ArrayList<SocialMonitorPost>();
                List<ProfileImageUrlData> profileImageUrlList = new ArrayList<ProfileImageUrlData>();
                for ( SocialPost item : postlist ) {
                    if ( item.getPostText() == null || item.getPostText().isEmpty() ) {
                        LOG.debug( "Empty post found! Skipping" );
                        continue;
                    }
                    SocialMonitorPost socialMonitorPost = new SocialMonitorPost();

                    socialMonitorPost.set_id( item.get_id() );
                    socialMonitorPost.setCompanyId( item.getCompanyId() );
                    socialMonitorPost.setRegionId( item.getRegionId() );
                    socialMonitorPost.setBranchId( item.getBranchId() );
                    socialMonitorPost.setAgentId( item.getAgentId() );
                    socialMonitorPost.setTimeInMillis( item.getTimeInMillis() );
                    socialMonitorPost.setPostId( item.getPostId() );
                    socialMonitorPost.setPostedBy( item.getPostedBy() );
                    socialMonitorPost.setPostText( item.getPostText() );
                    socialMonitorPost.setPostUrl( item.getPostUrl() );
                    socialMonitorPost.setSource( item.getSource() );
                    if ( item.getCompanyId() > 0 ) {
                        socialMonitorPost.setCompanyName(
                            organizationManagementService.getCompanySettings( item.getCompanyId() ).getProfileName() );
                        companyIdSet.add( item.getCompanyId() );
                    }
                    if ( item.getRegionId() > 0 ) {
                        socialMonitorPost.setRegionName(
                            organizationManagementService.getRegionSettings( item.getRegionId() ).getProfileName() );
                        regionIdSet.add( item.getRegionId() );
                    }
                    if ( item.getBranchId() > 0 ) {
                        socialMonitorPost.setBranchName(
                            organizationManagementService.getBranchSettings( item.getBranchId() ).getRegionName() );
                        branchIdSet.add( item.getBranchId() );
                    }
                    if ( item.getAgentId() > 0 ) {
                        socialMonitorPost.setAgentName(
                            organizationManagementService.getAgentSettings( item.getAgentId() ).getProfileName() );
                        userIdSet.add( item.getAgentId() );
                    }

                    socialMonitorPosts.add( socialMonitorPost );
                }
                //Get profile Images
                if ( !( companyIdSet.isEmpty() ) ) {
                    profileImageUrlList.addAll( organizationManagementService.fetchProfileImageUrlsForEntityList(
                        CommonConstants.COMPANY_ID_COLUMN, (HashSet<Long>) companyIdSet ) );
                }
                if ( !( regionIdSet.isEmpty() ) ) {
                    profileImageUrlList.addAll( organizationManagementService
                        .fetchProfileImageUrlsForEntityList( CommonConstants.REGION_ID_COLUMN, (HashSet<Long>) regionIdSet ) );
                }
                if ( !( branchIdSet.isEmpty() ) ) {
                    profileImageUrlList.addAll( organizationManagementService
                        .fetchProfileImageUrlsForEntityList( CommonConstants.BRANCH_ID_COLUMN, (HashSet<Long>) branchIdSet ) );
                }
                if ( !( userIdSet.isEmpty() ) ) {
                    profileImageUrlList.addAll( organizationManagementService
                        .fetchProfileImageUrlsForEntityList( CommonConstants.USER_ID, (HashSet<Long>) userIdSet ) );
                }
                socialMonitorData.setSocialMonitorPosts( (List<SocialMonitorPost>) socialMonitorPosts );
                socialMonitorData.setCount( count );
                socialMonitorData.setProfileImageUrlDataList( profileImageUrlList );
                posts = new Gson().toJson( socialMonitorData );

            } catch ( MalformedURLException e ) {
                LOG.warn( "MalformedURLException while searching for social posts. Reason : ", e );
                throw new NonFatalException( "MalformedURLException while searching for social posts.",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while searching for social posts. Reason : ",nonFatalException );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }

        LOG.info( "Method searchSocialPosts() finished." );
        return posts;
    }


    //TODO : get a confirmation on this
    private boolean checkZillowAccountChanged( OrganizationUnitSettings profileSettings, String zillowScreenName )
    {
        if ( profileSettings == null || profileSettings.getSocialMediaTokens() == null
            || profileSettings.getSocialMediaTokens().getZillowToken() == null
            || profileSettings.getSocialMediaTokens().getZillowToken().getZillowScreenName() == null ) {
            LOG.error(
                "zillow settings missing in profile id : {} in checkZillowAccountChanged()",profileSettings.getIden() );
            return false;
        }
        if ( zillowScreenName == null || zillowScreenName.isEmpty() ) {
            LOG.error( "zillowScreenName passed is null or empty in checkZillowAccountChanged()" );
            return false;
        }
        if ( !profileSettings.getSocialMediaTokens().getZillowToken().getZillowScreenName()
            .equalsIgnoreCase( zillowScreenName ) ) {
            return true;
        }
        return false;
    }
    /**
     * Method to show the Listings Manager page
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/showlistingsmanagerpage", method = RequestMethod.GET)
    public String initListingsManagerPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Listings Manager page started" );
        User user = sessionHelper.getCurrentUser();

        //Validate user
        try {
            if ( user == null ) {
                LOG.warn( "No user found in session" );
                throw new InvalidInputException( "No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION );
            }
            if ( user.getStatus() != CommonConstants.STATUS_ACTIVE ) {
                LOG.error( "Inactive or unauthorized users can not access Listings Manager page" );
                model.addAttribute( "message", messageUtils.getDisplayMessage(
                    DisplayMessageConstants.USER_MANAGEMENT_NOT_AUTHORIZED, DisplayMessageType.ERROR_MESSAGE ) );
            }
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException in while showing Listings Manager. Reason : " + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        return JspResolver.LISTINGS_MANAGER;
    }
    
    @ResponseBody
    @RequestMapping ( value = "/postonlinkedin", method = RequestMethod.POST)
    public boolean postToLinkedIn( HttpServletRequest request )
    {
        LOG.info( "Method to post feedback of customer to linkedin started." );
        String entityType = request.getParameter( "entityType" );
        String surveyMongoId = request.getParameter( "surveyMongoId" );
        long entityId = Long.parseLong( request.getParameter( "entityId" ) );
        return socialManagementService.manualPostToLinkedInForEntity( entityType, entityId, surveyMongoId );
    }

}