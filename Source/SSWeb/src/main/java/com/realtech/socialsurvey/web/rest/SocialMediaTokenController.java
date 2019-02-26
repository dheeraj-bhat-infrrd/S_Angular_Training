package com.realtech.socialsurvey.web.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.FacebookPage;
import com.realtech.socialsurvey.core.entities.LinkedinUserProfileResponse;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfileStage;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsSetter;
import com.realtech.socialsurvey.core.services.social.SocialAsyncService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.web.common.JspResolver;
import com.realtech.socialsurvey.web.common.TokenHandler;
import com.realtech.socialsurvey.web.util.RequestUtils;

import facebook4j.Facebook;
import facebook4j.FacebookException;

/**
 * 
 * @author rohit
 *
 */
@Controller
public class SocialMediaTokenController
{
    
    
    private static final Logger LOG = LoggerFactory.getLogger( SocialMediaTokenController.class );
    
    
    
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
    
    @Autowired
    private RequestUtils requestUtils;
    
    @Autowired
    private SocialManagementService socialManagementService;
    
    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private ProfileManagementService profileManagementService;
    
    @Autowired
    private TokenHandler tokenHandler;
    
    @Autowired
    private SettingsSetter settingsSetter;
    
    @Autowired
    private SocialAsyncService socialAsyncService;

    @Autowired
    private URLGenerator urlGenerator;
    
    private static final String V1 = "V1";
    
    
    
    @RequestMapping ( value = "/socialauthfromemail", method = RequestMethod.GET)
    public String getSocialAuthPageFromEmail( Model model , HttpServletRequest request )
    {
        LOG.info( "Method getSocialAuthPageFromEmail() called from SocialManagementController" );
        // AuthUrl for diff social networks

        try {


            String q = request.getParameter( "q" );
            Map<String, String> params = urlGenerator.decryptParameters( q );

            String columnName = params.get( "columnName" );
            String columnValue = params.get( "columnValue" );
            String serverBaseUrl = requestUtils.getRequestServerName( request );
            String socialFlow = params.get( "social" );

            switch ( socialFlow ) {

                // Building facebook authUrl
                case CommonConstants.FACEBOOK_SOCIAL_SITE:
                    String facebookOauthRedirectUrl = null;

                    facebookOauthRedirectUrl = socialManagementService.getFbRedirectUrIForEmailRequest( columnName,
                        columnValue, serverBaseUrl );

                    Facebook facebook = socialManagementService.getFacebookInstanceByCallBackUrl( facebookOauthRedirectUrl );
                    model.addAttribute( CommonConstants.SOCIAL_AUTH_URL,
                        facebook.getOAuthAuthorizationURL( facebookOauthRedirectUrl ) );

                    break;

                // Building linkedin authUrl
                case CommonConstants.LINKEDIN_SOCIAL_SITE:

                    String redirectUri = socialManagementService.getLinkedinRedirectUrIForEmailRequest( columnName,
                        columnValue, serverBaseUrl );
                    String linkedInAuth = socialManagementService.getLinkedinAuthUrl( linkedinAuthUri, linkedInApiKey, redirectUri, linkedinScope );

                    model.addAttribute( CommonConstants.SOCIAL_AUTH_URL, linkedInAuth );

                    LOG.info( "Returning the linkedin authorizationurl : " + linkedInAuth );
                    break;
            }

            model.addAttribute( "columnName", columnName );
            model.addAttribute( "columnValue", columnValue );
            model.addAttribute( CommonConstants.MESSAGE, CommonConstants.YES );
        } catch ( InvalidInputException e ) {
            LOG.error( "Error while getting SocialAuthPage" );
            model.addAttribute( CommonConstants.ERROR, CommonConstants.YES );
            return JspResolver.SOCIAL_AUTH_MSG_FOR_EMAIL;
        }
        return JspResolver.FACEBOOK_TOKEN_EMAIL_UPDATE;

    }
    
    
    
    @RequestMapping ( value = "/linkedinauthfromemail", method = RequestMethod.GET)
    public String authenticateLinkedInAccessFromEmail( Model model, HttpServletRequest request )
    {
        LOG.info( "Method authenticateLinkedInAccess() called from SocialManagementController" );
        
        Map<String, String> queryparams = null;
        try {
            String q = request.getParameter( "q" );
            queryparams = urlGenerator.decryptParameters( q );

            String columnName = queryparams.get( "columnName" );
            String columnValue = queryparams.get( "columnValue" );

            model.addAttribute( "columnName", columnName );
            model.addAttribute( "columnValue", columnValue );

            SocialMediaTokens mediaTokens = null;

            long entityId = Long.valueOf( columnValue );
            String entityType = columnName;


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
            String redirectUri = socialManagementService.getLinkedinRedirectUrIForEmailRequest( columnName, columnValue,
                requestUtils.getRequestServerName( request ) );
            params.add( new BasicNameValuePair( "redirect_uri", redirectUri ) );
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
            if ( StringUtils.isNotBlank( expiresInStr ) )
                expiresIn = Long.valueOf( expiresInStr ).longValue();

            // fetching linkedin profile url
            HttpGet httpGet = new HttpGet( linkedinProfileUri + accessToken );
            String basicProfileStr = httpclient.execute( httpGet, new BasicResponseHandler() );
            LinkedinUserProfileResponse profileData = new Gson().fromJson( basicProfileStr, LinkedinUserProfileResponse.class );
            String profileLink = (String) profileData.getSiteStandardProfileRequest().getUrl();

            boolean updated = false;
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( entityId );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                mediaTokens = companySettings.getSocialMediaTokens();
                mediaTokens = tokenHandler.updateLinkedInToken( accessToken, mediaTokens, profileLink, expiresIn, V1 );
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
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                mediaTokens = regionSettings.getSocialMediaTokens();
                mediaTokens = tokenHandler.updateLinkedInToken( accessToken, mediaTokens, profileLink, expiresIn, V1);
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
                updated = true;
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                mediaTokens = branchSettings.getSocialMediaTokens();
                mediaTokens = tokenHandler.updateLinkedInToken( accessToken, mediaTokens, profileLink, expiresIn, V1);
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
                updated = true;
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }

                mediaTokens = agentSettings.getSocialMediaTokens();
                mediaTokens = tokenHandler.updateLinkedInToken( accessToken, mediaTokens, profileLink, expiresIn, V1);
                mediaTokens = socialManagementService.updateAgentSocialMediaTokens( agentSettings, mediaTokens );
                agentSettings.setSocialMediaTokens( mediaTokens );

                // starting async service for data update from linkedin
                socialAsyncService.linkedInDataUpdateAsync( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
                    agentSettings, mediaTokens );

                for ( ProfileStage stage : agentSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "LINKEDIN_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( agentSettings.getProfileStages(), agentSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                updated = true;
            }
            if ( !updated ) {
                throw new InvalidInputException( "Invalid input exception occurred while creating access token for linkedin",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            //Add action to social connection history
            socialManagementService.updateSocialConnectionsHistory( entityType, entityId, mediaTokens,
                CommonConstants.LINKEDIN_SOCIAL_SITE, CommonConstants.SOCIAL_MEDIA_CONNECTED );
        } catch ( Exception e ) {
            LOG.error( "Exception while getting linkedin access token. Reason : " + e.getMessage(), e );
            return JspResolver.SOCIAL_AUTH_MSG_FOR_EMAIL;
        }

        // Updating attributes
        model.addAttribute( CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES );
        model.addAttribute( "socialNetwork", "linkedin" );
        LOG.info( "Method authenticateLinkedInAccess() finished from SocialManagementController" );
        return JspResolver.SOCIAL_AUTH_MSG_FOR_EMAIL;
    }
    
    
    /**
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/facebookauthfromemail", method = RequestMethod.GET)
    public String authenticateFacebookAccessFromEmail( Model model, HttpServletRequest request )
    {
        LOG.info( "Facebook authentication url requested" );
        Map<String, String> params = null;
        boolean isNewUser = true;
        
        try {
            String q = request.getParameter( "q" );
            params = urlGenerator.decryptParameters( q );

            String columnName = params.get( "columnName" );
            String columnValue = params.get( "columnValue" );
            String serverBaseUrl = params.get( "serverBaseUrl" );
            boolean updated = false;
            // On auth error
            String errorCode = request.getParameter( "error" );
            if ( errorCode != null ) {
                LOG.error( "Error code : " + errorCode );
                model.addAttribute( CommonConstants.ERROR, CommonConstants.YES );
                return JspResolver.SOCIAL_AUTH_MESSAGE;
            }


            // Getting Oauth accesstoken for facebook
            String oauthCode = request.getParameter( "code" );

            String facebookOauthRedirectUrl = socialManagementService.getFbRedirectUrIForEmailRequest( columnName, columnValue,
                serverBaseUrl );
            Facebook facebook = socialManagementService.getFacebookInstanceByCallBackUrl( facebookOauthRedirectUrl );
            String profileLink = null;
            facebook4j.auth.AccessToken accessToken = null;
            List<FacebookPage> facebookPages = new ArrayList<>();
            String collection = null;

            String facebookOauthRedirectUrlForRequestServer = socialManagementService
                .getFbRedirectUrIForEmailRequest( columnName, columnValue, requestUtils.getRequestServerName( request ) );
            accessToken = facebook.getOAuthAccessToken( oauthCode, facebookOauthRedirectUrlForRequestServer );
            facebook4j.User fbUser = facebook.getUser( facebook.getId() );
            if ( fbUser != null ) {
                profileLink = facebookUri + facebook.getId(); //fbUser.getLink().toString();
                FacebookPage personalUserAccount = new FacebookPage();
                personalUserAccount.setId( facebook.getId() );
                personalUserAccount.setAccessToken( accessToken.getToken() );
                personalUserAccount.setName( fbUser.getName() );
                personalUserAccount.setProfileUrl( profileLink );
                facebookPages.add( personalUserAccount );
            }


            // Storing token
            SocialMediaTokens mediaTokens;
            OrganizationUnitSettings settings = null;
            String entityType = columnName;
            long entityId = Long.valueOf( columnValue );
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                settings = organizationManagementService.getCompanySettings( entityId );
                collection = MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION;
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                settings = organizationManagementService.getRegionSettings( entityId );
                collection = MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION;
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                settings = organizationManagementService.getBranchSettingsDefault( entityId );
                collection = MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION;
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                settings = userManagementService.getUserSettings( entityId );
                collection = MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION;
            }


            if ( settings == null ) {
                throw new InvalidInputException( "No Agent settings found in current session" );
            }
            mediaTokens = settings.getSocialMediaTokens();
            facebookPages.addAll( socialManagementService.getFacebookPages( accessToken, profileLink ) );
            
            for ( FacebookPage currentFbPage : facebookPages ) {
                if ( currentFbPage.getProfileUrl()
                    .equalsIgnoreCase( settings.getSocialMediaTokens().getFacebookToken().getFacebookPageLink() ) ) {
                    mediaTokens = socialManagementService.updateFacebookPages( accessToken, mediaTokens, facebookPages );
                    mediaTokens = socialManagementService.updateFacebookPagesInMongo( collection, settings.getIden(), mediaTokens );
                    socialManagementService.updateFacebookTokenAndSave( currentFbPage.getAccessToken(), mediaTokens,
                        currentFbPage.getProfileUrl(), collection, settings );
                    isNewUser = false;
                    updated = true;
                    break;
                }
            }
            
            String mediaTokensStr = new Gson().toJson( mediaTokens, SocialMediaTokens.class );
            model.addAttribute( "mediaTokens", mediaTokensStr );


            String fbAccessTokenStr = new Gson().toJson( accessToken, facebook4j.auth.AccessToken.class );
            model.addAttribute( "pageNames", facebookPages );
            model.addAttribute( "fbAccessToken", fbAccessTokenStr );
            model.addAttribute( "columnName", columnName );
            model.addAttribute( "columnValue", columnValue );
            
            if ( !isNewUser && !updated ) {
                throw new InvalidInputException( "Invalid input exception occurred while saving access token for facebook",
                    DisplayMessageConstants.GENERAL_ERROR );
            }
                        
        } catch ( Exception e ) {
            
            LOG.error( "Exception while getting facebook access token. Reason : " + e.getMessage(), e );    
                        
            return JspResolver.SOCIAL_AUTH_MSG_FOR_EMAIL;
        }


        // Updating attributes
        model.addAttribute( CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.NO );
        model.addAttribute( "socialNetwork", "facebook" );
        model.addAttribute( "isNewUser", isNewUser );
        
        if(!isNewUser) {
            model.addAttribute( CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES );
            LOG.info( "Facebook Access tokens obtained and added to mongo successfully!" );
        }
        
        return JspResolver.SOCIAL_AUTH_MSG_FOR_EMAIL;
    }
    
    
    @RequestMapping ( value = "/saveSelectedAccessFacebookTokenForEmail")
    public String saveSelectedAccessFacebookTokenForEmail( Model model, HttpServletRequest request ) throws FacebookException
    {
        LOG.info( "Method saveSelectedAccessFacebookToken() called from SocialManagementController" );
        String selectedAccessFacebookToken = request.getParameter( "selectedAccessFacebookToken" );
        String selectedProfileUrl = request.getParameter( "selectedProfileUrl" );

        long entityId = Long.parseLong( request.getParameter( "columnValue" ) );
        String entityType = request.getParameter( "columnName" );


        boolean updated = false;
        SocialMediaTokens mediaTokens = null;
        String fbAccessTokenStr = request.getParameter( "fbAccessToken" );
        if ( fbAccessTokenStr == null || fbAccessTokenStr.isEmpty() ) {
            LOG.error( "Facebook access token is empty!" );
        }

        facebook4j.auth.AccessToken accessToken = new Gson().fromJson( fbAccessTokenStr, facebook4j.auth.AccessToken.class );
        //create extended token for selected
        String serverBaseUrl = requestUtils.getRequestServerName( request );
    	Facebook facebook = socialManagementService.getFacebookInstance( serverBaseUrl, facebookRedirectUri );
        facebook4j.auth.AccessToken extendedToken = facebook.extendTokenExpiration(accessToken.getToken());
        try {

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( entityId );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                mediaTokens = companySettings.getSocialMediaTokens();
                mediaTokens = socialManagementService.updateFacebookPages( accessToken, mediaTokens,
                    mediaTokens.getFacebookToken().getFacebookPages() );
                mediaTokens.getFacebookToken().setFacebookAccessTokenToPost( extendedToken.getToken() );
                mediaTokens.getFacebookToken().setFacebookPageLink( selectedProfileUrl );
                socialManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, mediaTokens );
                //update SETTINGS_SET_STATUS of COMPANY table to set.
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.FACEBOOK,
                        CommonConstants.SET_SETTINGS );
                    userManagementService.updateCompany( company );
                }
                for ( ProfileStage stage : companySettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "FACEBOOK_PRF" ) ) {
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
                mediaTokens = socialManagementService.updateFacebookPages( accessToken, mediaTokens,
                    mediaTokens.getFacebookToken().getFacebookPages() );
                mediaTokens.getFacebookToken().setFacebookAccessTokenToPost( extendedToken.getToken() );
                mediaTokens.getFacebookToken().setFacebookPageLink( selectedProfileUrl );
                socialManagementService.updateSocialMediaTokens( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION,
                    regionSettings, mediaTokens );
                //update SETTINGS_SET_STATUS of REGION table to set.
                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.FACEBOOK,
                        CommonConstants.SET_SETTINGS );
                    userManagementService.updateRegion( region );
                }
                for ( ProfileStage stage : regionSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "FACEBOOK_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( regionSettings.getProfileStages(), regionSettings,
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                updated = true;
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                mediaTokens = branchSettings.getSocialMediaTokens();
                mediaTokens = socialManagementService.updateFacebookPages( accessToken, mediaTokens,
                    mediaTokens.getFacebookToken().getFacebookPages() );
                mediaTokens.getFacebookToken().setFacebookAccessTokenToPost( extendedToken.getToken() );
                mediaTokens.getFacebookToken().setFacebookPageLink( selectedProfileUrl );
                socialManagementService.updateSocialMediaTokens( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION,
                    branchSettings, mediaTokens );
                //update SETTINGS_SET_STATUS of BRANCH table to set.
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.FACEBOOK,
                        CommonConstants.SET_SETTINGS );
                    userManagementService.updateBranch( branch );
                }

                for ( ProfileStage stage : branchSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "FACEBOOK_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( branchSettings.getProfileStages(), branchSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                updated = true;
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                mediaTokens = agentSettings.getSocialMediaTokens();
                mediaTokens = socialManagementService.updateFacebookPages( accessToken, mediaTokens,
                    mediaTokens.getFacebookToken().getFacebookPages() );
                mediaTokens.getFacebookToken().setFacebookAccessTokenToPost( extendedToken.getToken() );
                mediaTokens.getFacebookToken().setFacebookPageLink( selectedProfileUrl );
                socialManagementService.updateAgentSocialMediaTokens( agentSettings, mediaTokens );
                for ( ProfileStage stage : agentSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "FACEBOOK_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( agentSettings.getProfileStages(), agentSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                updated = true;
            }
            if ( !updated ) {
                throw new InvalidInputException( "Invalid input exception occurred while saving access token for facebook",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            //Add action to social connection history
            socialManagementService.updateSocialConnectionsHistory( entityType, entityId, mediaTokens,
                CommonConstants.FACEBOOK_SOCIAL_SITE, CommonConstants.SOCIAL_MEDIA_CONNECTED );
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "Error while saving access token for facebook to post: " + e.getLocalizedMessage(), e );
        } catch ( NonFatalException e ) {
            LOG.error( "Error setting settings value. Reason : " + e.getLocalizedMessage(), e );
        }

        model.addAttribute( CommonConstants.SUCCESS_ATTRIBUTE, CommonConstants.YES );
        model.addAttribute( "socialNetwork", "facebook" );
        return JspResolver.SOCIAL_AUTH_MSG_FOR_EMAIL;
    }


}
