package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.UnavailableException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.ExternalApiCallDetailsDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SocialPostDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.ZillowHierarchyDao;
import com.realtech.socialsurvey.core.dao.ZillowTempPostDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.Achievement;
import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Association;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BreadCrumb;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyPositions;
import com.realtech.socialsurvey.core.entities.CompanyProfileData;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ContactNumberSettings;
import com.realtech.socialsurvey.core.entities.ExternalAPICallDetails;
import com.realtech.socialsurvey.core.entities.FacebookPixelToken;
import com.realtech.socialsurvey.core.entities.FacebookToken;
import com.realtech.socialsurvey.core.entities.GoogleBusinessToken;
import com.realtech.socialsurvey.core.entities.GoogleToken;
import com.realtech.socialsurvey.core.entities.IndividualReviewAggregate;
import com.realtech.socialsurvey.core.entities.LenderRef;
import com.realtech.socialsurvey.core.entities.LendingTreeToken;
import com.realtech.socialsurvey.core.entities.Licenses;
import com.realtech.socialsurvey.core.entities.LinkedInProfileData;
import com.realtech.socialsurvey.core.entities.LinkedInToken;
import com.realtech.socialsurvey.core.entities.LockSettings;
import com.realtech.socialsurvey.core.entities.MailContent;
import com.realtech.socialsurvey.core.entities.MailContentSettings;
import com.realtech.socialsurvey.core.entities.MailIdSettings;
import com.realtech.socialsurvey.core.entities.MiscValues;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfileStage;
import com.realtech.socialsurvey.core.entities.PublicProfileAggregate;
import com.realtech.socialsurvey.core.entities.RealtorToken;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SettingsDetails;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.SocialProfileToken;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.TwitterToken;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserCompositeEntity;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.entities.WebAddressSettings;
import com.realtech.socialsurvey.core.entities.YelpToken;
import com.realtech.socialsurvey.core.entities.ZillowTempPost;
import com.realtech.socialsurvey.core.entities.ZillowToken;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.ProfileRedirectionException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.integration.zillow.FetchZillowReviewBody;
import com.realtech.socialsurvey.core.integration.zillow.FetchZillowReviewBodyByNMLS;
import com.realtech.socialsurvey.core.integration.zillow.ZillowIntegrationAgentApi;
import com.realtech.socialsurvey.core.integration.zillow.ZillowIntegrationLenderApi;
import com.realtech.socialsurvey.core.integration.zillow.ZillowIntergrationApiBuilder;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;
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
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsLocker;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsManager;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsSetter;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


@DependsOn ( "generic")
@Component
public class ProfileManagementServiceImpl implements ProfileManagementService, InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( ProfileManagementServiceImpl.class );

    // Zillow JSON response map key
    public static final String ZILLOW_JSON_CODE_KEY = "code";
    public static final String ZILLOW_JSON_TEXT_KEY = "text";

    // Zillow JSON response map error text prefix
    public static final String ZILLOW_JSON_ERROR_TEXT_PREFIX = "Error";

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private BatchTrackerService batchTrackerService;

    @Autowired
    private SettingsSetter settingsSetter;

    @Autowired
    private UserProfileDao userProfileDao;

    @Autowired
    private GenericDao<Company, Long> companyDao;

    @Autowired
    private GenericDao<Region, Long> regionDao;

    @Resource
    @Qualifier ( "branch")
    private BranchDao branchDao;

    @Resource
    @Qualifier ( "user")
    private UserDao usersDao;

    @Autowired
    private GenericDao<User, Long> userDao;

    @Autowired
    private SurveyDetailsDao surveyDetailsDao;

    @Autowired
    private SocialPostDao socialPostDao;

    @Autowired
    private Utils utils;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private SocialManagementService socialManagementService;

    @Autowired
    private SolrSearchService solrSearchService;

    @Autowired
    private SurveyPreInitiationDao surveyPreInitiationDao;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private URLGenerator urlGenerator;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private SettingsLocker settingsLocker;

    @Autowired
    private ZillowIntergrationApiBuilder zillowIntegrationApiBuilder;

    @Autowired
    private SurveyHandler surveyHandler;

    @Autowired
    private EmailFormatHelper emailFormatHelper;

    @Resource
    @Qualifier ( "nocaptcha")
    private CaptchaValidation captchaValidation;

    @Value ( "${ZILLOW_WEBSERVICE_ID}")
    private String zwsId;

    @Value ( "${ZILLOW_PARTNER_ID}")
    private String zillowPartnerId;

    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;

    @Value ( "${FB_CLIENT_ID}")
    private String facebookAppId;

    @Value ( "${GOOGLE_API_KEY}")
    private String googlePlusId;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String applicationAdminName;

    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String applicationAdminEmail;

    @Value ( "${VALIDATE_CAPTCHA}")
    private String validateCaptcha;

    @Value ( "${CAPTCHA_SECRET}")
    private String captchaSecretKey;

    //    @Autowired
    //    private ZillowUpdateService zillowUpdateService;

    @Autowired
    private ZillowHierarchyDao zillowHierarchyDao;

    @Value ( "${PARAM_ORDER_TAKE_SURVEY}")
    String paramOrderTakeSurvey;
    @Value ( "${PARAM_ORDER_TAKE_SURVEY_CUSTOMER}")
    String paramOrderTakeSurveyCustomer;
    @Value ( "${PARAM_ORDER_TAKE_SURVEY_REMINDER}")
    String paramOrderTakeSurveyReminder;

    @Value ( "${ZILLOW_AGENT_API_ENDPOINT}")
    private String zillowAgentApiEndpoint;

    @Value ( "${ZILLOW_LENDER_API_ENDPOINT}")
    private String zillowLenderApiEndpoint;

    @Value ( "${AMAZON_IMAGE_BUCKET}")
    private String amazonImageBucket;

    @Value ( "${CDN_PATH}")
    private String cdnUrl;

    @Value ( "${FACEBOOK_PIXEL_IMAGE_TAG}")
    private String fbPixelImageTag;

    @Autowired
    private ExternalApiCallDetailsDao externalApiCallDetailsDao;

    @Autowired
    private ZillowTempPostDao zillowTempPostDao;


    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.debug( "afterPropertiesSet called for profile management service" );
    }


    @Override
    public LockSettings aggregateParentLockSettings( User user, AccountType accountType, UserSettings settings, long branchId,
        long regionId, int profilesMaster ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method aggregateParentLockSettings() called from ProfileManagementService" );
        if ( user == null ) {
            throw new InvalidInputException( "User is not set." );
        }
        if ( settings == null ) {
            throw new InvalidInputException( "Invalid user settings." );
        }
        if ( accountType == null ) {
            throw new InvalidInputException( "Invalid account type." );
        }

        LockSettings parentLockSettings = null;
        // If user is Company Admin, Lock settings would be default
        if ( profilesMaster == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID ) {
            LOG.debug( "Setting default LockSettings for Company Admin" );
            parentLockSettings = new LockSettings();
        }

        // If user is not Company Admin, Lock settings need to be aggregated
        else {
            OrganizationUnitSettings branchSettings = null;
            OrganizationUnitSettings regionSettings = null;
            switch ( accountType ) {
                case FREE:
                case INDIVIDUAL:
                case TEAM:
                    // Individual
                    if ( profilesMaster == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                        LOG.debug( "Setting company LockSettings for Agent of Individual/Team account type" );
                        parentLockSettings = settings.getCompanySettings().getLockSettings();
                    }
                    break;

                case COMPANY:
                    // Branch Admin
                    if ( profilesMaster == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                        LOG.debug( "Setting company LockSettings for Branch Admin of Company account type" );
                        parentLockSettings = settings.getCompanySettings().getLockSettings();
                    }

                    // Individual
                    else if ( profilesMaster == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                        LOG.debug( "Aggregating LockSettings till Branch for Agent of Company account type" );
                        branchSettings = organizationManagementService.getBranchSettingsDefault( branchId );
                        parentLockSettings = lockSettingsTillBranch( settings.getCompanySettings(), null, branchSettings );
                    }
                    break;

                case ENTERPRISE:
                    // Region Admin
                    if ( profilesMaster == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                        LOG.debug( "Setting company LockSettings for Region Admin of Enterprise account type" );
                        parentLockSettings = settings.getCompanySettings().getLockSettings();
                    }

                    // Branch Admin
                    else if ( profilesMaster == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                        LOG.debug( "Aggregating LockSettings till Region for Branch Admin of Enterprise account type" );
                        if ( branchId > 0l ) {
                            branchSettings = organizationManagementService.getBranchSettingsDefault( branchId );

                            Branch branch = branchDao.findById( Branch.class, branchId );
                            regionId = branch.getRegion().getRegionId();
                        }
                        if ( regionId > 0l ) {
                            regionSettings = organizationManagementService.getRegionSettings( regionId );
                        }
                        parentLockSettings = lockSettingsTillRegion( settings.getCompanySettings(), regionSettings );
                    }

                    // Individual
                    else if ( profilesMaster == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                        LOG.debug( "Aggregating LockSettings till Branch for Agent of Enterprise account type" );
                        if ( branchId > 0l ) {
                            branchSettings = organizationManagementService.getBranchSettingsDefault( branchId );

                            Branch branch = branchDao.findById( Branch.class, branchId );
                            regionId = branch.getRegion().getRegionId();
                        }
                        if ( regionId > 0l ) {
                            regionSettings = organizationManagementService.getRegionSettings( regionId );
                        }
                        parentLockSettings = lockSettingsTillBranch( settings.getCompanySettings(), regionSettings,
                            branchSettings );
                    }
                    break;

                default:
                    throw new InvalidInputException( "Account type is invalid in aggregateParentLockSettings" );
            }
        }
        LOG.debug( "Method aggregateParentLockSettings() finished from ProfileManagementService" );
        return parentLockSettings;
    }


    LockSettings lockSettingsTillRegion( OrganizationUnitSettings companySettings, OrganizationUnitSettings regionSettings )
        throws InvalidInputException
    {
        LOG.debug( "Method lockSettingsTillRegion() called from ProfileManagementService" );
        if ( companySettings == null ) {
            throw new InvalidInputException( "No Settings found" );
        }

        // Fetching Company Lock settings
        LockSettings parentLock = new LockSettings();
        parentLock = aggregateLockSettings( companySettings.getLockSettings(), parentLock );

        // Aggregate Region Lock settings if exists
        if ( regionSettings != null ) {
            parentLock = aggregateLockSettings( regionSettings.getLockSettings(), parentLock );
        }
        LOG.debug( "Method lockSettingsTillRegion() finished from ProfileManagementService" );
        return parentLock;
    }


    LockSettings lockSettingsTillBranch( OrganizationUnitSettings companySettings, OrganizationUnitSettings regionSettings,
        OrganizationUnitSettings branchSettings ) throws InvalidInputException
    {
        LOG.debug( "Method lockSettingsTillBranch() called from ProfileManagementService" );
        if ( companySettings == null ) {
            throw new InvalidInputException( "No Settings found" );
        }

        // Fetching Company Lock settings
        LockSettings parentLock = new LockSettings();
        parentLock = aggregateLockSettings( companySettings.getLockSettings(), parentLock );

        // Aggregate Region Lock settings if exists
        if ( regionSettings != null ) {
            parentLock = aggregateLockSettings( regionSettings.getLockSettings(), parentLock );
        }

        // Aggregate Branch Lock settings if exists
        if ( branchSettings != null ) {
            parentLock = aggregateLockSettings( branchSettings.getLockSettings(), parentLock );
        }
        LOG.debug( "Method lockSettingsTillBranch() finished from ProfileManagementService" );
        return parentLock;
    }


    LockSettings aggregateLockSettings( LockSettings higherLock, LockSettings parentLock )
    {
        LOG.debug( "Method aggregateLockSettings() called from ProfileManagementService" );

        // Aggregate parentLockSettings with higherLockSettings
        if ( higherLock != null ) {
            if ( higherLock.getIsLogoLocked() ) {
                parentLock.setLogoLocked( true );
            }
            if ( higherLock.getIsWebAddressLocked() ) {
                parentLock.setWebAddressLocked( true );
            }
            if ( higherLock.getIsBlogAddressLocked() ) {
                parentLock.setBlogAddressLocked( true );
            }
            if ( higherLock.getIsWorkPhoneLocked() ) {
                parentLock.setWorkPhoneLocked( true );
            }
            if ( higherLock.getIsPersonalPhoneLocked() ) {
                parentLock.setPersonalPhoneLocked( true );
            }
            if ( higherLock.getIsFaxPhoneLocked() ) {
                parentLock.setFaxPhoneLocked( true );
            }
            if ( higherLock.getIsAboutMeLocked() ) {
                parentLock.setAboutMeLocked( true );
            }
        }
        LOG.debug( "Method aggregateLockSettings() finished from ProfileManagementService" );
        return parentLock;
    }


    @Override
    public OrganizationUnitSettings aggregateUserProfile( User user, AccountType accountType, UserSettings settings,
        long branchId, long regionId, int profilesMaster ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method aggregateUserProfile() called from ProfileManagementService" );
        if ( user == null ) {
            throw new InvalidInputException( "User is not set." );
        }
        if ( settings == null ) {
            throw new InvalidInputException( "Invalid user settings." );
        }
        if ( accountType == null ) {
            throw new InvalidInputException( "Invalid account type." );
        }

        OrganizationUnitSettings userProfile = null;
        // If user is Company Admin, returning CompanyAdmin Profile
        if ( profilesMaster == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID ) {
            LOG.debug( "Setting Company Profile for Company Admin" );
            userProfile = settings.getCompanySettings();
        }

        // If user is not Company Admin, Profile need to be aggregated
        else {
            OrganizationUnitSettings branchSettings = null;
            OrganizationUnitSettings regionSettings = null;
            switch ( accountType ) {
                case FREE:
                case INDIVIDUAL:
                case TEAM:
                    // Individual
                    if ( profilesMaster == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                        LOG.debug( "Aggregate Profile for Agent of Individual/Team account type" );
                        userProfile = aggregateAgentProfile( settings.getCompanySettings(), null, null,
                            settings.getAgentSettings() );
                    }
                    break;

                case COMPANY:
                    LOG.debug( "Company account type" );
                    branchSettings = organizationManagementService.getBranchSettingsDefault( branchId );

                    // Branch Admin
                    if ( profilesMaster == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                        LOG.debug( "Aggregate Profile for BranchAdmin of Company account type" );
                        userProfile = aggregateBranchProfile( settings.getCompanySettings(), null, branchSettings );
                    }

                    // Individual
                    else if ( profilesMaster == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                        LOG.debug( "Aggregate Profile for Agent of Company account type" );
                        userProfile = aggregateAgentProfile( settings.getCompanySettings(), null, branchSettings,
                            settings.getAgentSettings() );
                    }
                    break;

                case ENTERPRISE:
                    LOG.debug( "Enterprise account type" );
                    if ( branchId > 0l ) {
                        branchSettings = organizationManagementService.getBranchSettingsDefault( branchId );

                        Branch branch = branchDao.findById( Branch.class, branchId );
                        regionId = branch.getRegion().getRegionId();
                    }
                    if ( regionId > 0l ) {
                        regionSettings = organizationManagementService.getRegionSettings( regionId );
                    }

                    // Region Admin
                    if ( profilesMaster == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                        LOG.debug( "Aggregate Profile for RegionAdmin of Enterprise account type" );
                        userProfile = aggregateRegionProfile( settings.getCompanySettings(), regionSettings );
                    }

                    // Branch Admin
                    else if ( profilesMaster == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                        LOG.debug( "Aggregate Profile for BranchAdmin of Enterprise account type" );
                        userProfile = aggregateBranchProfile( settings.getCompanySettings(), regionSettings, branchSettings );
                    }

                    // Individual
                    else if ( profilesMaster == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                        LOG.debug( "Aggregate Profile for Agent of Enterprise account type" );
                        userProfile = aggregateAgentProfile( settings.getCompanySettings(), regionSettings, branchSettings,
                            settings.getAgentSettings() );
                    }
                    break;

                default:
                    throw new InvalidInputException( "Account type is invalid in aggregateUserProfile" );
            }
        }
        LOG.debug( "Method aggregateUserProfile() finished from ProfileManagementService" );
        return userProfile;
    }


    OrganizationUnitSettings aggregateRegionProfile( OrganizationUnitSettings companySettings,
        OrganizationUnitSettings regionSettings ) throws InvalidInputException
    {
        LOG.debug( "Method aggregateRegionProfile() called from ProfileManagementService" );
        if ( companySettings == null || regionSettings == null ) {
            throw new InvalidInputException( "No Settings found" );
        }

        // Aggregate Company Profile settings
        LockSettings userLock = new LockSettings();
        regionSettings = aggregateProfileData( companySettings, regionSettings, userLock );

        // Aggregate Region Profile Settings
        // to reflect lockSettings of Region
        regionSettings = aggregateProfileData( regionSettings, regionSettings, userLock );
        regionSettings.setLockSettings( userLock );

        LOG.debug( "Method aggregateRegionProfile() finished from ProfileManagementService" );
        return regionSettings;
    }


    OrganizationUnitSettings aggregateBranchProfile( OrganizationUnitSettings companySettings,
        OrganizationUnitSettings regionSettings, OrganizationUnitSettings branchSettings ) throws InvalidInputException
    {
        LOG.debug( "Method aggregateBranchProfile() called from ProfileManagementService" );
        if ( companySettings == null || branchSettings == null ) {
            throw new InvalidInputException( "No Settings found" );
        }

        // Aggregate Company Profile settings
        LockSettings userLock = new LockSettings();
        branchSettings = aggregateProfileData( companySettings, branchSettings, userLock );

        // Aggregate Region Profile settings if exists
        if ( regionSettings != null ) {
            branchSettings = aggregateProfileData( regionSettings, branchSettings, userLock );
        }

        // Aggregate Branch Profile Settings
        // to reflect lockSettings of Branch
        branchSettings = aggregateProfileData( branchSettings, branchSettings, userLock );
        branchSettings.setLockSettings( userLock );

        LOG.debug( "Method aggregateBranchProfile() finished from ProfileManagementService" );
        return branchSettings;
    }


    OrganizationUnitSettings aggregateAgentProfile( OrganizationUnitSettings companySettings,
        OrganizationUnitSettings regionSettings, OrganizationUnitSettings branchSettings,
        OrganizationUnitSettings agentSettings ) throws InvalidInputException
    {
        LOG.debug( "Method aggregateAgentProfile() called from ProfileManagementService" );
        if ( companySettings == null || agentSettings == null ) {
            throw new InvalidInputException( "No Settings found" );
        }

        // Aggregate Company Profile settings
        LockSettings userLock = new LockSettings();
        agentSettings = (AgentSettings) aggregateProfileData( companySettings, agentSettings, userLock );

        AgentSettings agentSettingsType = null;
        if ( agentSettings instanceof AgentSettings ) {
            agentSettingsType = (AgentSettings) agentSettings;
            // sort the company positions. since we are type casting the settings here, we are
            // sorting the same here
            sortCompanyPositions( agentSettingsType.getPositions() );
        }

        // Aggregate Region Profile settings if exists
        if ( regionSettings != null ) {
            agentSettings = aggregateProfileData( regionSettings, agentSettings, userLock );
        }

        // Aggregate Branch Profile settings if exists
        if ( branchSettings != null ) {
            agentSettings = aggregateProfileData( branchSettings, agentSettings, userLock );
        }

        // No Aggregation needed Agent Profile Settings
        // manully setting since agent do not have lockSettings
        agentSettings.setLockSettings( userLock );

        LOG.debug( "Method aggregateAgentProfile() finished from ProfileManagementService" );
        return ( agentSettingsType != null ? agentSettingsType : agentSettings );
    }


    OrganizationUnitSettings aggregateProfileData( OrganizationUnitSettings parentProfile, OrganizationUnitSettings userProfile,
        LockSettings userLock )
    {
        LOG.debug( "Method aggregateProfileData() called from ProfileManagementService" );

        if ( userProfile.getContact_details() == null ) {
            userProfile.setContact_details( new ContactDetailsSettings() );
        }
        if ( userProfile.getContact_details().getWeb_addresses() == null ) {
            userProfile.getContact_details().setWeb_addresses( new WebAddressSettings() );
        }
        if ( userProfile.getContact_details().getContact_numbers() == null ) {
            userProfile.getContact_details().setContact_numbers( new ContactNumberSettings() );
        }
        if ( userProfile.getSurvey_settings() == null ) {
            userProfile.setSurvey_settings( parentProfile.getSurvey_settings() );
        }

        // Aggregate parentProfile data with userProfile
        LockSettings parentLock = parentProfile.getLockSettings();
        if ( parentLock != null ) {
            // Logo
            //JIRA SS-1363 begin
            /*if ( parentProfile.getLogoThumbnail() != null ) {
                if ( parentLock.getIsLogoLocked() && !userLock.getIsLogoLocked() ) {
                    userProfile.setLogo( parentProfile.getLogoThumbnail() );
                    userLock.setLogoLocked( true );
                }
                if ( !parentLock.getIsLogoLocked() && !userLock.getIsLogoLocked() ) {
                    if ( userProfile.getLogoThumbnail() == null || userProfile.getLogoThumbnail().equals( "" ) ) {
                        userProfile.setLogo( parentProfile.getLogoThumbnail() );
                    }
                }
            }*/
            if ( parentProfile.getLogo() != null ) {
                if ( parentLock.getIsLogoLocked() && !userLock.getIsLogoLocked() ) {
                    userProfile.setLogo( parentProfile.getLogo() );
                    userLock.setLogoLocked( true );
                }
                if ( !parentLock.getIsLogoLocked() && !userLock.getIsLogoLocked() ) {
                    if ( userProfile.getLogo() == null || userProfile.getLogo().equals( "" ) ) {
                        userProfile.setLogo( parentProfile.getLogo() );
                    }
                }
            }
            //JIRA SS-1363 end

            // Basic Contact details
            if ( parentProfile.getContact_details() != null ) {
                if ( parentLock.getIsAboutMeLocked() && !userLock.getIsAboutMeLocked()
                    && parentProfile.getContact_details().getAbout_me() != null ) {
                    userProfile.getContact_details().setAbout_me( parentProfile.getContact_details().getAbout_me() );
                    userLock.setAboutMeLocked( true );
                }
            }

            // Web addresses
            if ( parentProfile.getContact_details().getWeb_addresses() != null ) {
                if ( parentLock.getIsWebAddressLocked() && !userLock.getIsWebAddressLocked()
                    && userProfile.getContact_details().getWeb_addresses() != null ) {
                    userProfile.getContact_details().getWeb_addresses()
                        .setWork( parentProfile.getContact_details().getWeb_addresses().getWork() );
                    userLock.setWebAddressLocked( true );
                }
                if ( parentLock.getIsBlogAddressLocked() && !userLock.getIsBlogAddressLocked()
                    && userProfile.getContact_details().getWeb_addresses() != null ) {
                    userProfile.getContact_details().getWeb_addresses()
                        .setBlogs( parentProfile.getContact_details().getWeb_addresses().getBlogs() );
                    userLock.setBlogAddressLocked( true );
                }
            }

            // Phone numbers
            if ( parentProfile.getContact_details().getContact_numbers() != null ) {
                if ( parentLock.getIsWorkPhoneLocked() && !userLock.getIsWorkPhoneLocked()
                    && userProfile.getContact_details().getContact_numbers() != null ) {
                    userProfile.getContact_details().getContact_numbers()
                        .setWork( parentProfile.getContact_details().getContact_numbers().getWork() );
                    userProfile.getContact_details().getContact_numbers()
                        .setPhone1( parentProfile.getContact_details().getContact_numbers().getPhone1() );
                    userLock.setWorkPhoneLocked( true );
                }
                if ( parentLock.getIsPersonalPhoneLocked() && !userLock.getIsPersonalPhoneLocked()
                    && userProfile.getContact_details().getContact_numbers() != null ) {
                    userProfile.getContact_details().getContact_numbers()
                        .setPersonal( parentProfile.getContact_details().getContact_numbers().getPersonal() );
                    userProfile.getContact_details().getContact_numbers()
                        .setPhone2( parentProfile.getContact_details().getContact_numbers().getPhone2() );
                    userLock.setPersonalPhoneLocked( true );
                }
                if ( parentLock.getIsFaxPhoneLocked() && !userLock.getIsFaxPhoneLocked()
                    && userProfile.getContact_details().getContact_numbers() != null ) {
                    userProfile.getContact_details().getContact_numbers()
                        .setFax( parentProfile.getContact_details().getContact_numbers().getFax() );
                    userLock.setFaxPhoneLocked( true );
                }
            }
        }
        LOG.debug( "Method aggregateProfileData() finished from ProfileManagementService" );
        return userProfile;
    }


    // Logo
    @Override
    public void updateLogo( String collection, OrganizationUnitSettings companySettings, String logo )
        throws InvalidInputException
    {
        if ( logo == null || logo.isEmpty() ) {
            throw new InvalidInputException( "Logo passed can not be null or empty" );
        }
        if ( collection == null || collection.isEmpty() ) {
            throw new InvalidInputException( "Collection name passed can not be null or empty" );
        }
        if ( companySettings == null ) {
            throw new InvalidInputException( "Company settings passed can not be null" );
        }
        LOG.debug( "Updating logo" );
        /*organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings( MongoOrganizationUnitSettingDaoImpl.KEY_LOGO,
            logo, companySettings, collection );*/
        organizationManagementService.updateImageForOrganizationUnitSetting( companySettings.getIden(), logo, null, null,
            collection, CommonConstants.IMAGE_TYPE_LOGO, false, false );
        /*organizationUnitSettingsDao.updateImageForOrganizationUnitSetting( companySettings.getIden(), logo, collection,
            CommonConstants.IMAGE_TYPE_LOGO, false, false );*/
        LOG.debug( "Logo updated successfully" );
    }


    // ProfileImage
    @Override
    public void updateProfileImage( String collection, OrganizationUnitSettings companySettings, String image )
        throws InvalidInputException
    {
        if ( image == null || image.isEmpty() ) {
            throw new InvalidInputException( "image passed can not be null or empty" );
        }
        if ( collection == null || collection.isEmpty() ) {
            throw new InvalidInputException( "Collection name passed can not be null or empty" );
        }
        if ( companySettings == null ) {
            throw new InvalidInputException( "Company settings passed can not be null" );
        }
        LOG.debug( "Updating image" );
        /*organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_IMAGE, image, companySettings, collection );*/
        /*organizationUnitSettingsDao.updateImageForOrganizationUnitSetting( companySettings.getIden(), image, collection,
            CommonConstants.IMAGE_TYPE_PROFILE, false, false );*/
        organizationManagementService.updateImageForOrganizationUnitSetting( companySettings.getIden(), image, null, null,
            collection, CommonConstants.IMAGE_TYPE_PROFILE, false, false );
        LOG.debug( "Image updated successfully" );
    }
    
    @Override
    public void removeProfileImage( String collection, OrganizationUnitSettings unitSettings ) throws InvalidInputException
    {
        if ( collection == null || collection.isEmpty() ) {
            throw new InvalidInputException( "Collection name passed can not be null or empty" );
        }
        if ( unitSettings == null ) {
            throw new InvalidInputException( "Company settings passed can not be null" );
        }
        LOG.debug( "removing image from mongo" );
        organizationUnitSettingsDao.removeImageForOrganizationUnitSetting( unitSettings.getIden(), collection, false,
            CommonConstants.IMAGE_TYPE_PROFILE );

        LOG.debug( "updating solr" );
        Map<String, Object> updateMap = new HashMap<String, Object>();
        updateMap.put( CommonConstants.PROFILE_IMAGE_THUMBNAIL_COLUMN, "" );
        updateMap.put( CommonConstants.PROFILE_IMAGE_URL_SOLR, "" );
        updateMap.put( CommonConstants.IS_PROFILE_IMAGE_SET_SOLR, false );
        try {
            solrSearchService.editUserInSolrWithMultipleValues( unitSettings.getIden(), updateMap );
        } catch ( SolrException e ) {
            LOG.error( "SolrException occured while updating user in solr. Reason : ", e );
            throw new InvalidInputException( "SolrException occured while updating user in solr. Reason : ", e );
        }

        LOG.debug( "Image removed successfully" );
    }



    // vertical
    @Override
    @Transactional
    public void updateVertical( String collection, OrganizationUnitSettings companySettings, String vertical )
        throws InvalidInputException
    {
        if ( vertical == null || vertical.isEmpty() ) {
            throw new InvalidInputException( "vertical passed can not be null or empty" );
        }
        if ( collection == null || collection.isEmpty() ) {
            throw new InvalidInputException( "Collection name passed can not be null or empty" );
        }
        if ( companySettings == null ) {
            throw new InvalidInputException( "Company settings passed can not be null" );
        }
        LOG.debug( "Updating vertical" );
        if ( collection.equals( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {

            List<VerticalsMaster> verticalsMasters = organizationManagementService.getAllVerticalsMaster();
            VerticalsMaster verticalsMaster = null;

            for ( VerticalsMaster vm : verticalsMasters ) {
                if ( vertical.equals( vm.getVerticalName() ) ) {
                    verticalsMaster = vm;
                    break;
                }
            }

            if ( verticalsMaster == null ) {
                throw new InvalidInputException( "Invalid vertial name passed" );
            }

            Company company = companyDao.findById( Company.class, companySettings.getIden() );
            company.setVerticalsMaster( verticalsMaster );
            companyDao.update( company );
        }
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_VERTICAL, vertical, companySettings, collection );
        LOG.debug( "vertical updated successfully" );
    }


    // Associations
    @Override
    public List<Association> addAssociations( String collection, OrganizationUnitSettings unitSettings,
        List<Association> associations ) throws InvalidInputException
    {
        if ( associations == null ) {
            throw new InvalidInputException( "Association name passed can not be null" );
        }
        if ( collection == null || collection.isEmpty() ) {
            throw new InvalidInputException( "Collection name passed can not be null or empty" );
        }
        if ( unitSettings == null ) {
            throw new InvalidInputException( "Unit settings passed can not be null" );
        }
        LOG.debug( "Adding associations" );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_ASSOCIATION, associations, unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
        LOG.debug( "Associations added successfully" );
        return associations;
    }


    @Override
    public List<Association> addAgentAssociations( String collection, AgentSettings agentSettings,
        List<Association> associations ) throws InvalidInputException
    {
        if ( associations == null ) {
            throw new InvalidInputException( "Association name passed can not be null" );
        }
        if ( collection == null || collection.isEmpty() ) {
            throw new InvalidInputException( "Collection name passed can not be null or empty" );
        }
        if ( agentSettings == null ) {
            throw new InvalidInputException( "Agent settings passed can not be null" );
        }
        LOG.debug( "Adding associations" );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings( MongoOrganizationUnitSettingDaoImpl.KEY_ASSOCIATION,
            associations, agentSettings );
        LOG.debug( "Associations added successfully" );
        return associations;
    }


    // Lock Settings
    @Override
    public LockSettings updateLockSettings( String collection, OrganizationUnitSettings unitSettings,
        LockSettings lockSettings ) throws InvalidInputException
    {
        if ( lockSettings == null ) {
            throw new InvalidInputException( "LockSettings passed can not be null" );
        }
        if ( collection == null || collection.isEmpty() ) {
            throw new InvalidInputException( "Collection name passed can not be null or empty" );
        }
        if ( unitSettings == null ) {
            throw new InvalidInputException( "Unit settings passed can not be null" );
        }
        LOG.debug( "Updating lock detail information" );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_LOCK_SETTINGS, lockSettings, unitSettings, collection );
        LOG.debug( "lock details updated successfully" );
        return lockSettings;
    }


    // Contact details
    @Override
    public ContactDetailsSettings updateContactDetails( String collection, OrganizationUnitSettings unitSettings,
        ContactDetailsSettings contactDetailsSettings ) throws InvalidInputException
    {
        if ( contactDetailsSettings == null ) {
            throw new InvalidInputException( "Contact details passed can not be null" );
        }
        if ( collection == null || collection.isEmpty() ) {
            throw new InvalidInputException( "Collection name passed can not be null or empty" );
        }
        if ( unitSettings == null ) {
            throw new InvalidInputException( "Unit settings passed can not be null" );
        }
        LOG.debug( "Updating contact detail information" );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_CONTACT_DETAIL_SETTINGS, contactDetailsSettings, unitSettings, collection );
        // Update the seo content flag to true
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_SEO_CONTENT_MODIFIED, true, unitSettings, collection );
        LOG.debug( "Contact details updated successfully" );
        return contactDetailsSettings;
    }


    @Override
    public ContactDetailsSettings updateAgentContactDetails( String collection, AgentSettings agentSettings,
        ContactDetailsSettings contactDetailsSettings ) throws InvalidInputException
    {
        if ( contactDetailsSettings == null ) {
            throw new InvalidInputException( "Contact details passed can not be null" );
        }
        if ( collection == null || collection.isEmpty() ) {
            throw new InvalidInputException( "Collection name passed can not be null or empty" );
        }
        if ( agentSettings == null ) {
            throw new InvalidInputException( "Agent settings passed can not be null" );
        }
        LOG.debug( "Updating contact detail information" );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_CONTACT_DETAIL_SETTINGS, contactDetailsSettings, agentSettings );
        // Update the seo content flag to true
        organizationUnitSettingsDao.updateParticularKeyAgentSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_SEO_CONTENT_MODIFIED, true, agentSettings );
        LOG.debug( "Contact details updated successfully" );
        return contactDetailsSettings;
    }


    // Achievements
    @Override
    public List<Achievement> addAchievements( String collection, OrganizationUnitSettings unitSettings,
        List<Achievement> achievements ) throws InvalidInputException
    {
        if ( achievements == null ) {
            throw new InvalidInputException( "Achievements passed can not be null or empty" );
        }
        if ( collection == null || collection.isEmpty() ) {
            throw new InvalidInputException( "Collection name passed can not be null or empty" );
        }
        if ( unitSettings == null ) {
            throw new InvalidInputException( "Unit settings passed can not be null" );
        }
        LOG.debug( "Adding achievements" );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_ACHIEVEMENTS, achievements, unitSettings, collection );
        LOG.debug( "Achievements added successfully" );
        return achievements;
    }


    @Override
    public List<Achievement> addAgentAchievements( String collection, AgentSettings agentSettings,
        List<Achievement> achievements ) throws InvalidInputException
    {
        if ( achievements == null ) {
            throw new InvalidInputException( "Achievements passed can not be null or empty" );
        }
        if ( collection == null || collection.isEmpty() ) {
            throw new InvalidInputException( "Collection name passed can not be null or empty" );
        }
        if ( agentSettings == null ) {
            throw new InvalidInputException( "Agent settings passed can not be null" );
        }
        LOG.debug( "Adding achievements" );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings( MongoOrganizationUnitSettingDaoImpl.KEY_ACHIEVEMENTS,
            achievements, agentSettings );
        LOG.debug( "Achievements added successfully" );
        return achievements;
    }


    // Licenses
    @Override
    public Licenses addLicences( String collection, OrganizationUnitSettings unitSettings, List<String> authorisedIn )
        throws InvalidInputException
    {
        if ( authorisedIn == null ) {
            throw new InvalidInputException( "Authorised In list passed can not be null" );
        }
        if ( collection == null || collection.isEmpty() ) {
            throw new InvalidInputException( "Collection name passed can not be null or empty" );
        }
        if ( unitSettings == null ) {
            throw new InvalidInputException( "Unit settings passed can not be null" );
        }

        Licenses licenses = unitSettings.getLicenses();
        if ( licenses == null ) {
            LOG.debug( "Licenses not present for current profile, create a new license object" );
            licenses = new Licenses();
        }
        licenses.setAuthorized_in( authorisedIn );
        LOG.debug( "Adding Licences list" );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_LICENCES, licenses, unitSettings, collection );
        LOG.debug( "Licence authorisations added successfully" );
        return licenses;
    }


    @Override
    public Licenses addAgentLicences( String collection, AgentSettings agentSettings, List<String> authorisedIn )
        throws InvalidInputException
    {
        if ( authorisedIn == null ) {
            throw new InvalidInputException( "Authorised In list passed can not be null" );
        }
        if ( collection == null || collection.isEmpty() ) {
            throw new InvalidInputException( "Collection name passed can not be null or empty" );
        }
        if ( agentSettings == null ) {
            throw new InvalidInputException( "Agent settings passed can not be null" );
        }

        Licenses licenses = agentSettings.getLicenses();
        if ( licenses == null ) {
            LOG.debug( "Licenses not present for current profile, create a new license object" );
            licenses = new Licenses();
        }
        licenses.setAuthorized_in( authorisedIn );
        LOG.debug( "Adding Licences list" );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings( MongoOrganizationUnitSettingDaoImpl.KEY_LICENCES,
            licenses, agentSettings );
        LOG.debug( "Licence authorisations added successfully" );
        return licenses;
    }


    // Social Tokens
    @Override
    public void updateSocialMediaTokens( String collection, OrganizationUnitSettings unitSettings,
        SocialMediaTokens mediaTokens ) throws InvalidInputException
    {
        if ( mediaTokens == null ) {
            throw new InvalidInputException( "Media tokens passed was null" );
        }
        if ( collection == null || collection.isEmpty() ) {
            throw new InvalidInputException( "Collection name passed can not be null or empty" );
        }
        if ( unitSettings == null ) {
            throw new InvalidInputException( "Unit settings passed can not be null" );
        }
        LOG.debug( "Updating the social media tokens in profile." );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_SOCIAL_MEDIA_TOKENS, mediaTokens, unitSettings, collection );
        LOG.debug( "Successfully updated the social media tokens." );
    }


    @Override
    public void disconnectSelectedSocialMedia( String collection, OrganizationUnitSettings unitSettings,
        SocialMediaTokens mediaTokens, String keyToUpdate ) throws InvalidInputException
    {
        if ( mediaTokens == null ) {
            throw new InvalidInputException( "Media tokens passed was null" );
        }
        if ( collection == null || collection.isEmpty() ) {
            throw new InvalidInputException( "Collection name passed can not be null or empty" );
        }
        if ( unitSettings == null ) {
            throw new InvalidInputException( "Unit settings passed can not be null" );
        }
        if ( keyToUpdate == null ) {
            throw new InvalidInputException( "key passed can not be null" );
        }
        LOG.debug( "Deleting google business from social media tokens in profile." );
        organizationUnitSettingsDao.removeKeyInOrganizationSettings( unitSettings, keyToUpdate, collection );
        LOG.debug( "Successfully deleted google business from social media tokens." );
    }


    // Disclaimer
    @Override
    public void updateDisclaimer( String collection, OrganizationUnitSettings unitSettings, String disclaimer )
        throws InvalidInputException
    {
        if ( disclaimer == null || disclaimer.isEmpty() ) {
            throw new InvalidInputException( "disclaimer passed can not be null or empty" );
        }
        if ( collection == null || collection.isEmpty() ) {
            throw new InvalidInputException( "Collection name passed can not be null or empty" );
        }
        if ( unitSettings == null ) {
            throw new InvalidInputException( "Unit settings passed can not be null" );
        }
        LOG.debug( "Updating disclaimer" );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_DISCLAIMER, disclaimer, unitSettings, collection );
        LOG.debug( "Disclaimer updated successfully" );
    }


    /**
     * Method to fetch all users under the specified branch of specified company
     */
    @Override
    @Transactional
    public List<AgentSettings> getIndividualsForBranch( String companyProfileName, String branchProfileName )
        throws InvalidInputException, ProfileNotFoundException
    {
        if ( companyProfileName == null || companyProfileName.isEmpty() ) {
            throw new ProfileNotFoundException( "companyProfileName is null or empty in getIndividualsForBranch" );
        }
        if ( branchProfileName == null || branchProfileName.isEmpty() ) {
            throw new ProfileNotFoundException( "branchProfileName is null or empty in getIndividualsForBranch" );
        }
        LOG.debug( "Method getIndividualsForBranch called for companyProfileName: " + companyProfileName + " branchProfileName:"
            + branchProfileName );
        List<AgentSettings> users = null;
        OrganizationUnitSettings branchSettings = getBranchByProfileName( companyProfileName, branchProfileName );
        if ( branchSettings != null ) {
            LOG.debug( "Fetching user profiles for branchId: " + branchSettings.getIden() );
            users = getIndividualsByBranchId( branchSettings.getIden() );
        }
        LOG.debug( "Method getIndividualsForBranch executed successfully" );
        return users;
    }


    /**
     * Method to fetch all users under the specified region of specified company
     * 
     * @throws NoRecordsFetchedException
     */
    @Override
    @Transactional
    public List<AgentSettings> getIndividualsForRegion( String companyProfileName, String regionProfileName )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException
    {
        if ( companyProfileName == null || companyProfileName.isEmpty() ) {
            throw new InvalidInputException( "companyProfileName is null or empty in getIndividualsForRegion" );
        }
        if ( regionProfileName == null || regionProfileName.isEmpty() ) {
            throw new InvalidInputException( "regionProfileName is null or empty in getIndividualsForRegion" );
        }
        LOG.debug( "Method getIndividualsForRegion called for companyProfileName:" + companyProfileName
            + " and branchProfileName:" + regionProfileName );
        List<AgentSettings> users = null;
        OrganizationUnitSettings regionSettings = getRegionByProfileName( companyProfileName, regionProfileName );
        if ( regionSettings != null ) {
            users = getIndividualsByRegionId( regionSettings.getIden() );
        }

        LOG.debug( "Method getIndividualsForRegion executed successfully" );
        return users;
    }


    /**
     * Method to fetch all individuals directly linked to a company
     * @throws NoRecordsFetchedException
     * @throws InvalidInputException
     * @throws ProfileNotFoundException
     */
    @Override
    @Transactional
    public List<AgentSettings> getIndividualsForCompany( String companyProfileName )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException
    {
        if ( companyProfileName == null || companyProfileName.isEmpty() ) {
            throw new InvalidInputException( "companyProfileName is null or empty in getIndividualsForCompany" );
        }
        LOG.debug( "Method getIndividualsForCompany called for companyProfileName: " + companyProfileName );
        List<AgentSettings> users = null;
        OrganizationUnitSettings companySettings = getCompanyProfileByProfileName( companyProfileName );
        if ( companySettings != null ) {
            Region defaultRegion = organizationManagementService
                .getDefaultRegionForCompany( companyDao.findById( Company.class, companySettings.getIden() ) );
            if ( defaultRegion != null ) {
                Branch defaultBranch = organizationManagementService.getDefaultBranchForRegion( defaultRegion.getRegionId() );
                users = getIndividualsByBranchId( defaultBranch.getBranchId() );
            }
        }
        LOG.debug( "Method getIndividualsForCompany executed successfully" );
        return users;
    }


    /**
     * Method to fetch all individuals directly linked to a company
     * @throws NoRecordsFetchedException
     * @throws InvalidInputException
     * @throws ProfileNotFoundException
     */
    @Override
    @Transactional
    public List<AgentSettings> getIndividualsForCompany( long companyId )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException
    {
        if ( companyId <= 0l ) {
            throw new InvalidInputException( "Invalid companyId passed in getIndividualsForCompany" );
        }
        LOG.debug( "Method getIndividualsForCompany called for companyId: " + companyId );
        List<AgentSettings> users = null;
        Region defaultRegion = organizationManagementService
            .getDefaultRegionForCompany( companyDao.findById( Company.class, companyId ) );
        if ( defaultRegion != null ) {
            Branch defaultBranch = organizationManagementService.getDefaultBranchForRegion( defaultRegion.getRegionId() );
            users = getIndividualsByBranchId( defaultBranch.getBranchId() );
        }
        LOG.debug( "Method getIndividualsForCompany executed successfully" );
        return users;
    }


    /**
     * Method to get the region based on profile name
     */
    @Override
    @Transactional
    public OrganizationUnitSettings getRegionByProfileName( String companyProfileName, String regionProfileName )
        throws ProfileNotFoundException, InvalidInputException
    {
        LOG.debug( "Method getRegionByProfileName called for companyProfileName:" + companyProfileName
            + " and regionProfileName:" + regionProfileName );
        OrganizationUnitSettings companySettings = null;
        if ( companyProfileName == null || companyProfileName.isEmpty() ) {
            throw new ProfileNotFoundException( "companyProfileName is null or empty in getRegionByProfileName" );
        }
        if ( regionProfileName == null || regionProfileName.isEmpty() ) {
            throw new ProfileNotFoundException( "regionProfileName is null or empty in getRegionByProfileName" );
        }

        companySettings = getCompanyProfileByProfileName( companyProfileName );
        if ( companySettings == null ) {
            LOG.error( "Unable to fetch company settings, invalid input provided by the user" );
            throw new ProfileNotFoundException( "Unable to get company settings " );
        }
        /**
         * generate profileUrl and fetch the region by profileUrl since profileUrl for any region is
         * unique, whereas profileName is unique only within a company
         */
        String profileUrl = utils.generateRegionProfileUrl( companyProfileName, regionProfileName );

        OrganizationUnitSettings regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileUrl(
            profileUrl, MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
        if ( regionSettings == null ) {
            throw new ProfileNotFoundException( "Unable to get region settings " );
        }

        LOG.debug( "Generating final region settings based on lock settings" );
        regionSettings = aggregateRegionProfile( companySettings, regionSettings );
        LOG.debug( "Method getRegionByProfileName excecuted successfully" );
        return regionSettings;
    }


    /**
     * Method to get the branch based on profile name
     */
    @Override
    @Transactional
    public OrganizationUnitSettings getBranchByProfileName( String companyProfileName, String branchProfileName )
        throws ProfileNotFoundException, InvalidInputException
    {
        LOG.debug( "Method getBranchByProfileName called for companyProfileName:" + companyProfileName
            + " and branchProfileName:" + branchProfileName );

        OrganizationUnitSettings companySettings = getCompanyProfileByProfileName( companyProfileName );
        if ( companySettings == null ) {
            LOG.error( "Unable to fetch company settings, invalid input provided by the user" );
            throw new ProfileNotFoundException( "Unable to get company settings " );
        }
        /**
         * generate profileUrl and fetch the branch by profileUrl since profileUrl for any branch is
         * unique, whereas profileName is unique only within a company
         */
        String profileUrl = utils.generateBranchProfileUrl( companyProfileName, branchProfileName );
        OrganizationUnitSettings branchSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileUrl(
            profileUrl, MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );

        if ( branchSettings == null ) {
            LOG.error( "Unable to fetch branch settings, invalid input provided by the user" );
            throw new ProfileNotFoundException( "Unable to get branch settings " );
        }

        LOG.debug( "Fetching branch from db to identify the region" );
        Branch branch = branchDao.findById( Branch.class, branchSettings.getIden() );
        if ( branch == null ) {
            LOG.error( "Unable to get branch with this iden " + branchSettings.getIden() );
            throw new ProfileNotFoundException( "Unable to get branch with this iden " + branchSettings.getIden() );

        }
        OrganizationUnitSettings regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
            branch.getRegion().getRegionId(), MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );

        branchSettings = aggregateBranchProfile( companySettings, regionSettings, branchSettings );

        LOG.debug( "Method getBranchByProfileName excecuted successfully" );
        return branchSettings;
    }


    /**
     * JIRA:SS-117 by RM02 Method to get the company details based on profile name
     */
    @Override
    @Transactional
    public OrganizationUnitSettings getCompanyProfileByProfileName( String profileName ) throws ProfileNotFoundException
    {
        LOG.debug( "Method getCompanyDetailsByProfileName called for profileName : " + profileName );
        if ( profileName == null || profileName.isEmpty() ) {
            throw new ProfileNotFoundException( "profile name is null or empty while getting company details" );
        }
        OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName(
            profileName, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
        if ( companySettings == null ) {
            LOG.error( "Unable to find company settings with profile name : " + profileName );
            throw new ProfileNotFoundException( "Unable to find company settings with profile name : " + profileName );
        }

        LOG.debug( "Successfully executed method getCompanyDetailsByProfileName. Returning :" + companySettings );
        return companySettings;
    }


    /**
     * Method to get profile of an individual
     * 
     * @throws NoRecordsFetchedException
     */
    @Override
    @Transactional
    public OrganizationUnitSettings getIndividualByProfileName( String agentProfileName )
        throws ProfileNotFoundException, InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method getIndividualByProfileName called for agentProfileName:" + agentProfileName );

        OrganizationUnitSettings agentSettings = null;
        if ( agentProfileName == null || agentProfileName.isEmpty() ) {
            throw new ProfileNotFoundException( "agentProfileName is null or empty while getting agent settings" );
        }

        agentSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName( agentProfileName,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
        if ( agentSettings == null ) {
            throw new ProfileNotFoundException( "No settings found for agent while fetching agent profile" );
        }

        User user = userDao.findById( User.class, agentSettings.getIden() );

        LOG.debug( "Fetching user profiles for agentId: " + agentSettings.getIden() );
        UserProfile userProfile = null;
        for ( UserProfile profile : user.getUserProfiles() ) {
            if ( profile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID
                && profile.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                userProfile = profile;
                break;
            }
        }
        if ( userProfile == null ) {
            throw new ProfileNotFoundException( "User profiles not found while fetching agent profile" );
        }

        long companyId = userProfile.getCompany().getCompanyId();
        LOG.debug( "Fetching company settings for companyId: " + companyId );
        OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( companyId,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        long regionId = userProfile.getRegionId();
        OrganizationUnitSettings regionSettings = null;
        if ( regionId > 0l ) {
            LOG.debug( "Fetching region settings for regionId: " + regionId );
            regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( regionId,
                MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
        }

        long branchId = userProfile.getBranchId();
        OrganizationUnitSettings branchSettings = null;
        if ( branchId > 0l ) {
            LOG.debug( "Fetching branch settings for regionId: " + branchId );
            branchSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( branchId,
                MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
        }

        // LockSettings parentLock = lockSettingsTillBranch(companySettings, regionSettings,
        // branchSettings);
        agentSettings = aggregateAgentProfile( companySettings, regionSettings, branchSettings, agentSettings );
        agentSettings = aggregateAgentDetails( user, agentSettings, agentSettings.getLockSettings() );

        LOG.debug( "Method getIndividualByProfileName executed successfully" );
        return agentSettings;
    }


    @Override
    @Transactional
    public OrganizationUnitSettings getIndividualSettingsByProfileName( String agentProfileName )
        throws ProfileNotFoundException, InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method getIndividualByProfileName called for agentProfileName:" + agentProfileName );

        OrganizationUnitSettings agentSettings = null;
        if ( agentProfileName == null || agentProfileName.isEmpty() ) {
            throw new ProfileNotFoundException( "agentProfileName is null or empty while getting agent settings" );
        }
        agentSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName( agentProfileName,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
        if ( agentSettings == null ) {
            throw new ProfileNotFoundException( "No settings found for agent while fetching agent profile" );
        }
        return agentSettings;
    }


    @Override
    @Transactional
    public Map<String, Long> getPrimaryHierarchyByAgentProfile( OrganizationUnitSettings agentSettings )
        throws InvalidInputException, ProfileNotFoundException
    {
        LOG.debug( "Inside method getPrimaryHierarchyByAgentProfile " );
        Map<String, Long> hierarchyMap = userManagementService.getPrimaryUserProfileByAgentId( agentSettings.getIden() );
        LOG.debug( "Returning from getPrimaryHierarchyByAgentProfile " );
        return hierarchyMap;
    }


    @Override
    @Transactional
    public SocialMediaTokens aggregateSocialProfiles( OrganizationUnitSettings unitSettings, String entity )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method aggregateSocialProfiles called for agentProfileName:" + unitSettings.getProfileName() );

        long companyId = 0l;
        if ( entity.equals( CommonConstants.AGENT_ID ) ) {
            User user = userDao.findById( User.class, unitSettings.getIden() );
            companyId = user.getCompany().getCompanyId();
        } else if ( entity.equals( CommonConstants.BRANCH_ID ) ) {
            Branch branch = branchDao.findById( Branch.class, unitSettings.getIden() );
            companyId = branch.getCompany().getCompanyId();
        } else if ( entity.equals( CommonConstants.REGION_ID ) ) {
            Region region = regionDao.findById( Region.class, unitSettings.getIden() );
            companyId = region.getCompany().getCompanyId();
        }

        LOG.debug( "Fetching company settings for companyId: " + companyId );
        OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( companyId,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        // Check if unit settings or company settings have any tokens
        // if company social token and entity tokens are null return null
        if ( unitSettings.getSocialMediaTokens() == null && companySettings.getSocialMediaTokens() == null ) {
            return null;
        }

        // Aggregate urls
        SocialMediaTokens entityTokens = validateSocialMediaTokens( unitSettings );

        if ( companySettings.getSocialMediaTokens() != null ) {
            SocialMediaTokens companyTokens = validateSocialMediaTokens( companySettings );

            if ( ( entityTokens.getFacebookToken().getFacebookPageLink() == null
                || entityTokens.getFacebookToken().getFacebookPageLink().equals( "" ) )
                && companyTokens.getFacebookToken().getFacebookPageLink() != null
                && !companyTokens.getFacebookToken().getFacebookPageLink().equals( "" ) ) {
                entityTokens.getFacebookToken().setFacebookPageLink( companyTokens.getFacebookToken().getFacebookPageLink() );
            }
            if ( ( entityTokens.getGoogleToken().getProfileLink() == null
                || entityTokens.getGoogleToken().getProfileLink().equals( "" ) )
                && companyTokens.getGoogleToken().getProfileLink() != null
                && !companyTokens.getGoogleToken().getProfileLink().equals( "" ) ) {
                entityTokens.getGoogleToken().setProfileLink( companyTokens.getGoogleToken().getProfileLink() );
            }
            if ( ( entityTokens.getLinkedInToken().getLinkedInPageLink() == null
                || entityTokens.getLinkedInToken().getLinkedInPageLink().equals( "" ) )
                && companyTokens.getLinkedInToken().getLinkedInPageLink() != null
                && !companyTokens.getLinkedInToken().getLinkedInPageLink().equals( "" ) ) {
                entityTokens.getLinkedInToken().setLinkedInPageLink( companyTokens.getLinkedInToken().getLinkedInPageLink() );
            }
            if ( ( entityTokens.getRssToken().getProfileLink() == null
                || entityTokens.getRssToken().getProfileLink().equals( "" ) )
                && companyTokens.getRssToken().getProfileLink() != null
                && !companyTokens.getRssToken().getProfileLink().equals( "" ) ) {
                entityTokens.getRssToken().setProfileLink( companyTokens.getRssToken().getProfileLink() );
            }
            if ( ( entityTokens.getTwitterToken().getTwitterPageLink() == null
                || entityTokens.getTwitterToken().getTwitterPageLink().equals( "" ) )
                && companyTokens.getTwitterToken().getTwitterPageLink() != null
                && !companyTokens.getTwitterToken().getTwitterPageLink().equals( "" ) ) {
                entityTokens.getTwitterToken().setTwitterPageLink( companyTokens.getTwitterToken().getTwitterPageLink() );
            }
            if ( ( entityTokens.getYelpToken().getYelpPageLink() == null
                || entityTokens.getYelpToken().getYelpPageLink().equals( "" ) )
                && companyTokens.getYelpToken().getYelpPageLink() != null
                && !companyTokens.getYelpToken().getYelpPageLink().equals( "" ) ) {
                entityTokens.getYelpToken().setYelpPageLink( companyTokens.getYelpToken().getYelpPageLink() );
            }
            if ( ( entityTokens.getZillowToken().getZillowProfileLink() == null
                || entityTokens.getZillowToken().getZillowProfileLink().equals( "" ) )
                && companyTokens.getZillowToken().getZillowProfileLink() != null
                && !companyTokens.getZillowToken().getZillowProfileLink().equals( "" ) ) {
                entityTokens.getZillowToken().setZillowProfileLink( companyTokens.getZillowToken().getZillowProfileLink() );
            }
            if ( ( entityTokens.getLendingTreeToken().getLendingTreeProfileLink() == null
                || entityTokens.getLendingTreeToken().getLendingTreeProfileLink().equals( "" ) )
                && companyTokens.getLendingTreeToken().getLendingTreeProfileLink() != null
                && !companyTokens.getLendingTreeToken().getLendingTreeProfileLink().equals( "" ) ) {
                entityTokens.getLendingTreeToken()
                    .setLendingTreeProfileLink( companyTokens.getLendingTreeToken().getLendingTreeProfileLink() );
            }
            if ( ( entityTokens.getRealtorToken().getRealtorProfileLink() == null
                || entityTokens.getRealtorToken().getRealtorProfileLink().equals( "" ) )
                && companyTokens.getRealtorToken().getRealtorProfileLink() != null
                && !companyTokens.getRealtorToken().getRealtorProfileLink().equals( "" ) ) {
                entityTokens.getRealtorToken().setRealtorProfileLink( companyTokens.getRealtorToken().getRealtorProfileLink() );
            }
            if ( ( entityTokens.getGoogleBusinessToken().getGoogleBusinessLink() == null
                || entityTokens.getGoogleBusinessToken().getGoogleBusinessLink().equals( "" ) )
                && companyTokens.getGoogleBusinessToken().getGoogleBusinessLink() != null
                && !companyTokens.getGoogleBusinessToken().getGoogleBusinessLink().equals( "" ) ) {
                entityTokens.getGoogleBusinessToken()
                    .setGoogleBusinessLink( companyTokens.getGoogleBusinessToken().getGoogleBusinessLink() );
            }
        }

        LOG.debug( "Method aggregateSocialProfiles executed successfully: " + entityTokens.toString() );
        return entityTokens;
    }


    SocialMediaTokens validateSocialMediaTokens( OrganizationUnitSettings unitSettings )
    {
        SocialMediaTokens mediaTokens;
        if ( unitSettings.getSocialMediaTokens() == null ) {
            mediaTokens = new SocialMediaTokens();
        } else {
            mediaTokens = unitSettings.getSocialMediaTokens();
        }

        if ( mediaTokens.getFacebookToken() == null ) {
            mediaTokens.setFacebookToken( new FacebookToken() );
        }
        if ( mediaTokens.getGoogleToken() == null ) {
            mediaTokens.setGoogleToken( new GoogleToken() );
        }
        if ( mediaTokens.getLinkedInToken() == null ) {
            mediaTokens.setLinkedInToken( new LinkedInToken() );
        }
        if ( mediaTokens.getRssToken() == null ) {
            mediaTokens.setRssToken( new SocialProfileToken() );
        }
        if ( mediaTokens.getTwitterToken() == null ) {
            mediaTokens.setTwitterToken( new TwitterToken() );
        }
        if ( mediaTokens.getYelpToken() == null ) {
            mediaTokens.setYelpToken( new YelpToken() );
        }
        if ( mediaTokens.getZillowToken() == null ) {
            mediaTokens.setZillowToken( new ZillowToken() );
        }
        if ( mediaTokens.getLendingTreeToken() == null ) {
            mediaTokens.setLendingTreeToken( new LendingTreeToken() );
        }
        if ( mediaTokens.getRealtorToken() == null ) {
            mediaTokens.setRealtorToken( new RealtorToken() );
        }
        if ( mediaTokens.getGoogleBusinessToken() == null ) {
            mediaTokens.setGoogleBusinessToken( new GoogleBusinessToken() );
        }
        return mediaTokens;
    }


    /**
     * Method to get User by profileName
     * 
     * @throws NoRecordsFetchedException
     */
    @Override
    @Transactional
    public User getUserByProfileName( String agentProfileName, boolean checkStatus ) throws ProfileNotFoundException
    {
        LOG.debug( "Method getUserProfilesByProfileName called for agentProfileName:" + agentProfileName );

        OrganizationUnitSettings agentSettings = null;
        if ( agentProfileName == null || agentProfileName.isEmpty() ) {
            throw new ProfileNotFoundException( "agentProfileName is null or empty while getting agent settings" );
        }

        agentSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName( agentProfileName,
            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
        if ( agentSettings == null ) {
            throw new ProfileNotFoundException( "No settings found for agent while fetching agent profile" );
        }

        User user = userDao.findById( User.class, agentSettings.getIden() );
        if ( user.getStatus() == CommonConstants.STATUS_INACTIVE && checkStatus ) {
            throw new ProfileNotFoundException( "No active agent found." );
        }

        LOG.debug( "Method getUserProfilesByProfileName executed successfully" );
        return user;
    }


    @Override
    @Transactional
    public UserCompositeEntity getCompositeUserObjectByProfileName( String agentProfileName, boolean checkStatus )
        throws ProfileNotFoundException
    {
        LOG.debug(
            "Getting the user composite object by profile name: " + agentProfileName + " and check status: " + checkStatus );
        if ( agentProfileName == null || agentProfileName.isEmpty() ) {
            LOG.error( "agentProfileName is null or empty while getting agent settings" );
            throw new ProfileNotFoundException( "agentProfileName is null or empty while getting agent settings" );
        }
        UserCompositeEntity compositeUserObject = null;
        AgentSettings agentSettings = null;
        User user = null;
        OrganizationUnitSettings organizationUnitSettings = organizationUnitSettingsDao
            .fetchOrganizationUnitSettingsByProfileName( agentProfileName,
                MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
        if ( organizationUnitSettings == null || ( organizationUnitSettings.getStatus() != null
            && ( organizationUnitSettings.getStatus().equalsIgnoreCase( CommonConstants.STATUS_DELETED_MONGO ) ) ) ) {
            LOG.warn( "No profile found with profile name: " + agentProfileName );
            throw new ProfileNotFoundException( "No profile found with profile name: " + agentProfileName );
        } else {
            LOG.debug( "Found the setting. Converting into agent settings" );
            agentSettings = (AgentSettings) organizationUnitSettings;
            // handle the cases where record is present in the mongo but not in SQL
            try {
                user = userDao.findById( User.class, agentSettings.getIden() );
            } catch ( HibernateException e ) {
                LOG.error( "No active agent found in SQL.", e );
                throw new ProfileNotFoundException( "No active agent found in SQL." );
            }
            if ( user == null || ( user.getStatus() == CommonConstants.STATUS_INACTIVE && checkStatus ) ) {
                LOG.error( "No active agent found." );
                throw new ProfileNotFoundException( "No active agent found." );
            }
            compositeUserObject = new UserCompositeEntity();
            compositeUserObject.setUser( user );
            compositeUserObject.setAgentSettings( agentSettings );
        }
        LOG.debug( "Returning the user composite object." );
        return compositeUserObject;
    }


    @Override
    @Transactional
    public List<AgentSettings> getIndividualsByBranchId( long branchId ) throws InvalidInputException
    {
        LOG.debug( "Method getIndividualsByBranchId called for branchId:" + branchId );
        List<AgentSettings> users = null;
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        queries.put( CommonConstants.BRANCH_ID_COLUMN, branchId );
        queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
            userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) );
        List<UserProfile> userProfiles = userProfileDao.findByKeyValueAscendingWithAlias( UserProfile.class, queries,
            Arrays.asList( new String[] { "firstName", "lastName" } ), "user" );
        if ( userProfiles != null && !userProfiles.isEmpty() ) {
            users = new ArrayList<AgentSettings>();
            for ( UserProfile userProfile : userProfiles ) {
                users.add( organizationUnitSettingsDao.fetchAgentSettingsById( userProfile.getUser().getUserId() ) );
            }
            LOG.debug( "Returning :" + users.size() + " individuals for branch : " + branchId );
        }
        LOG.debug( "Method getIndividualsByBranchId executed successfully" );
        return users;
    }


    /**
     * Method to get individuals by branchId
     * 
     * @param branchId
     * @param startIndex
     * @param batchSize
     * @return List of {AgentSettings}
     */
    @Override
    @Transactional
    public List<AgentSettings> getIndividualsByBranchId( long branchId, int startIndex, int batchSize )
        throws InvalidInputException
    {
        LOG.debug( "Method getIndividualsByBranchId called for branchId:" + branchId + ", startIndex: " + startIndex
            + ", batchSize: " + batchSize );
        List<AgentSettings> users = null;
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        queries.put( CommonConstants.BRANCH_ID_COLUMN, branchId );
        queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
            userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) );
        List<UserProfile> userProfiles = userProfileDao.findUserProfilesInBatch( queries, startIndex, batchSize );
        if ( userProfiles != null && !userProfiles.isEmpty() ) {
            users = new ArrayList<AgentSettings>();
            for ( UserProfile userProfile : userProfiles ) {
                users.add( organizationUnitSettingsDao.fetchAgentSettingsById( userProfile.getUser().getUserId() ) );
            }
            LOG.debug( "Returning :" + users.size() + " individuals for branch : " + branchId );
        }
        LOG.debug( "Method getIndividualsByBranchId executed successfully" );
        return users;
    }


    @Override
    public long getReviewsCountForCompany( long companyId, double minScore, double maxScore, boolean fetchAbusive,
        boolean notRecommended )
    {
        LOG.debug( "Method getReviewsCountForCompany called for companyId:" + companyId + " minscore:" + minScore + " maxscore:"
            + maxScore );
        long reviewsCount = 0;
        reviewsCount = surveyDetailsDao.getFeedBacksCount( CommonConstants.COMPANY_ID_COLUMN, companyId, minScore, maxScore,
            fetchAbusive, notRecommended, false, 0l );
        LOG.debug( "Method getReviewsCountForCompany executed successfully" );
        return reviewsCount;
    }


    /**
     * Method to fetch all users under the specified region
     */
    @Override
    @Transactional
    public List<AgentSettings> getIndividualsByRegionId( long regionId ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method getIndividualsByRegionId called for regionId: " + regionId );
        List<AgentSettings> users = null;
        if ( regionId <= 0l ) {
            throw new InvalidInputException( "Region id is not set for getIndividualsByRegionId" );
        }
        Branch defaultBranch = organizationManagementService.getDefaultBranchForRegion( regionId );

        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        queries.put( CommonConstants.REGION_ID_COLUMN, regionId );
        queries.put( CommonConstants.BRANCH_ID_COLUMN, defaultBranch.getBranchId() );
        queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
            userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) );

        LOG.debug( "calling method to fetch user profiles under region :" + regionId );
        List<UserProfile> userProfiles = userProfileDao.findByKeyValueAscendingWithAlias( UserProfile.class, queries,
            Arrays.asList( new String[] { "firstName", "lastName" } ), "user" );

        if ( userProfiles != null && !userProfiles.isEmpty() ) {
            LOG.debug( "Obtained userProfiles with size : " + userProfiles.size() );
            users = new ArrayList<AgentSettings>();
            for ( UserProfile userProfile : userProfiles ) {
                users.add( organizationUnitSettingsDao.fetchAgentSettingsById( userProfile.getUser().getUserId() ) );
            }
        }
        LOG.debug( "Method getIndividualsByRegionId executed successfully" );
        return users;
    }


    @Override
    @Transactional
    public List<AgentSettings> getIndividualsByRegionId( long regionId, int startIndex, int batchSize )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method getIndividualsByRegionId called for regionId: " + regionId );
        List<AgentSettings> users = null;
        if ( regionId <= 0l ) {
            throw new InvalidInputException( "Region id is not set for getIndividualsByRegionId" );
        }
        Branch defaultBranch = organizationManagementService.getDefaultBranchForRegion( regionId );

        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        queries.put( CommonConstants.REGION_ID_COLUMN, regionId );
        queries.put( CommonConstants.BRANCH_ID_COLUMN, defaultBranch.getBranchId() );
        queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
            userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) );

        LOG.debug( "calling method to fetch user profiles under region :" + regionId );
        List<UserProfile> userProfiles = userProfileDao.findUserProfilesInBatch( queries, startIndex, batchSize );
        ;

        if ( userProfiles != null && !userProfiles.isEmpty() ) {
            LOG.debug( "Obtained userProfiles with size : " + userProfiles.size() );
            users = new ArrayList<AgentSettings>();
            for ( UserProfile userProfile : userProfiles ) {
                users.add( organizationUnitSettingsDao.fetchAgentSettingsById( userProfile.getUser().getUserId() ) );
            }
        }
        LOG.debug( "Method getIndividualsByRegionId executed successfully" );
        return users;
    }


    /**
     * Method to fetch reviews based on the profile level specified, iden is one of
     * agentId/branchId/regionId or companyId based on the profile level
     */
    @Override
    public List<SurveyDetails> getReviews( long iden, double startScore, double limitScore, int startIndex, int numOfRows,
        String profileLevel, boolean fetchAbusive, Date startDate, Date endDate, String sortCriteria )
        throws InvalidInputException
    {
        LOG.debug(
            "Method getReviews called for iden: {} startScore: {} limitScore: {} startIndex: {}  numOfRows: {} profileLevel: {}",
            iden, startScore, limitScore, startIndex, numOfRows, profileLevel );
        List<SurveyDetails> surveyDetails = null;
        if ( iden <= 0l ) {
            LOG.warn( "iden is invalid while fetching reviews" );
            throw new InvalidInputException( "iden is invalid while fetching reviews" );
        }

        Calendar calendar = Calendar.getInstance();
        if ( startDate != null ) {
            calendar.setTime( startDate );
            calendar.add( Calendar.DATE, 0 );
            startDate = calendar.getTime();
        }
        if ( endDate != null ) {
            calendar.setTime( endDate );
            calendar.add( Calendar.DATE, 1 );
            endDate = calendar.getTime();
        }

        String idenColumnName = getIdenColumnNameFromProfileLevel( profileLevel );
        surveyDetails = surveyDetailsDao.getFeedbacks( idenColumnName, iden, startIndex, numOfRows, startScore, limitScore,
            fetchAbusive, startDate, endDate, sortCriteria );

        //TODO : remove this . Temporary fix for Zillow review URl
        if ( surveyDetails != null && surveyDetails.size() > 0 ) {
            for ( SurveyDetails review : surveyDetails ) {
                if ( review != null && "Zillow".equalsIgnoreCase( review.getSource() ) ) {
                    if ( StringUtils.isEmpty( review.getSourceId() ) ) {
                        review.setSourceId( review.getCompleteProfileUrl() );
                    }
                }
            }
        }

        return surveyDetails;
    }


    @Override
    public List<SurveyDetails> getReviewsForReports( long iden, double startScore, double limitScore, int startIndex,
        int numOfRows, String profileLevel, boolean fetchAbusive, Date startDate, Date endDate, String sortCriteria )
        throws InvalidInputException
    {
        LOG.info( "Method getReviews called for iden:" + iden + " startScore:" + startScore + " limitScore:" + limitScore
            + " startIndex:" + startIndex + " numOfRows:" + numOfRows + " profileLevel:" + profileLevel );
        List<SurveyDetails> surveyDetails = null;
        if ( iden <= 0l ) {
            throw new InvalidInputException( "iden is invalid while fetching reviews" );
        }

        Calendar calendar = Calendar.getInstance();
        if ( startDate != null ) {
            calendar.setTime( startDate );
            calendar.set( Calendar.HOUR_OF_DAY, 0 );
            calendar.set( Calendar.MINUTE, 0 );
            calendar.set( Calendar.SECOND, 0 );
            calendar.set( Calendar.MILLISECOND, 0 );
            startDate = calendar.getTime();
        }
        if ( endDate != null ) {
            calendar.setTime( endDate );
            calendar.set( Calendar.HOUR_OF_DAY, 23 );
            calendar.set( Calendar.MINUTE, 59 );
            calendar.set( Calendar.SECOND, 59 );
            calendar.set( Calendar.MILLISECOND, 0 );
            endDate = calendar.getTime();
        }

        String idenColumnName = getIdenColumnNameFromProfileLevel( profileLevel );
        surveyDetails = surveyDetailsDao.getFeedbacksForReports( idenColumnName, iden, startIndex, numOfRows, startScore,
            limitScore, fetchAbusive, startDate, endDate, sortCriteria );

        //TODO : remove this . Temporary fix for Zillow review URl
        for ( SurveyDetails review : surveyDetails ) {
            if ( review.getSource().equals( "Zillow" ) ) {
                if ( StringUtils.isEmpty( review.getSourceId() ) ) {
                    review.setSourceId( review.getCompleteProfileUrl() );
                }
            }
        }

        return surveyDetails;
    }


    /**
     * Method to get average ratings based on the profile level specified, iden is one of
     * agentId/branchId/regionId or companyId based on the profile level
     */
    @Override
    public double getAverageRatings( long iden, String profileLevel, boolean aggregateAbusive ) throws InvalidInputException
    {
        return getAverageRatings( iden, profileLevel, aggregateAbusive, false, 0, 0 );
    }


    @Override
    public double getAverageRatings( long iden, String profileLevel, boolean aggregateAbusive, boolean includeZillow,
        long zillowTotalScore, long zillowReviewCount ) throws InvalidInputException
    {
        LOG.debug( "Method getAverageRatings called for iden : {} profilelevel: {}", iden, profileLevel );
        if ( iden <= 0l ) {
            LOG.warn( "iden is invalid for getting average rating os a company" );
            throw new InvalidInputException( "iden is invalid for getting average rating os a company" );
        }
        String idenColumnName = getIdenColumnNameFromProfileLevel( profileLevel );
        double averageRating = surveyDetailsDao.getRatingForPastNdays( idenColumnName, iden, -1, aggregateAbusive, false,
            includeZillow, zillowReviewCount, zillowTotalScore );

        //get formatted survey score using rating format  
        averageRating = surveyHandler.getFormattedSurveyScore( averageRating );

        LOG.debug( "Method getAverageRatings executed successfully.Returning: {}", averageRating );
        return averageRating;
    }


    /**
     * Method to get iden column name from profile level
     * 
     * @param profileLevel
     * @return
     * @throws InvalidInputException
     */
    String getIdenColumnNameFromProfileLevel( String profileLevel ) throws InvalidInputException
    {
        LOG.debug( "Getting iden column name for profile level: {}", profileLevel );
        String idenColumnName = null;
        if ( profileLevel == null || profileLevel.isEmpty() ) {
            LOG.warn( "profile level is null or empty while getting iden column name" );
            throw new InvalidInputException( "profile level is null or empty while getting iden column name" );
        }
        switch ( profileLevel ) {
            case CommonConstants.PROFILE_LEVEL_COMPANY:
                idenColumnName = CommonConstants.COMPANY_ID_COLUMN;
                break;
            case CommonConstants.PROFILE_LEVEL_REGION:
                idenColumnName = CommonConstants.REGION_ID_COLUMN;
                break;
            case CommonConstants.PROFILE_LEVEL_BRANCH:
                idenColumnName = CommonConstants.BRANCH_ID_COLUMN;
                break;
            case CommonConstants.PROFILE_LEVEL_INDIVIDUAL:
                idenColumnName = CommonConstants.AGENT_ID_COLUMN;
                break;
            case CommonConstants.PROFILE_LEVEL_REALTECH_ADMIN:
                break;
            default:
                LOG.warn( "Invalid profile level while getting iden column name" );
                throw new InvalidInputException( "Invalid profile level while getting iden column name" );
        }
        LOG.debug( "Returning column name: {} for profile level {} profileLevel", idenColumnName, profileLevel );
        return idenColumnName;
    }


    /**
     * Method to get reviews count based on the profile level specified, iden is one of
     * agentId/branchId/regionId or companyId based on the profile level within limit of rating
     * score specified
     */
    @Override
    public long getReviewsCount( long iden, double minScore, double maxScore, String profileLevel, boolean fetchAbusive,
        boolean notRecommended ) throws InvalidInputException
    {
        return getReviewsCount( iden, minScore, maxScore, profileLevel, fetchAbusive, notRecommended, false, 0 );
    }


    @Override
    public long getReviewsCount( long iden, double minScore, double maxScore, String profileLevel, boolean fetchAbusive,
        boolean notRecommended, boolean includeZillow, long zillowReviewCount ) throws InvalidInputException
    {
        LOG.debug( "Method getReviewsCount called for iden: {}  minscore: {}  maxscore: {}  profilelevel: {}", iden, minScore,
            maxScore, profileLevel );
        if ( iden <= 0l ) {
            LOG.warn( "Iden is invalid for getting reviews count" );
            throw new InvalidInputException( "Iden is invalid for getting reviews count" );
        }
        long reviewsCount = 0;
        String idenColumnName = getIdenColumnNameFromProfileLevel( profileLevel );
        reviewsCount = surveyDetailsDao.getFeedBacksCount( idenColumnName, iden, minScore, maxScore, fetchAbusive,
            notRecommended, includeZillow, zillowReviewCount );
        LOG.debug( "Method getReviewsCount executed successfully. Returning reviewsCount: {}", reviewsCount );
        return reviewsCount;
    }


    /**
     * Method to get the list of individuals for branch/region or company as specified ide in one of
     * branchId/regionId/companyId
     * 
     * @throws SolrException
     */
    @Override
    public Collection<UserFromSearch> getProListByProfileLevel( long iden, String profileLevel, int start, int numOfRows )
        throws InvalidInputException, SolrException
    {
        LOG.debug( "Method getProListByProfileLevel called for iden: " + iden + " profileLevel:" + profileLevel + " start:"
            + start + " numOfRows:" + numOfRows );
        if ( iden <= 0l ) {
            throw new InvalidInputException( "iden is invalid in getProListByProfileLevel" );
        }
        if ( profileLevel == null || profileLevel.isEmpty() ) {
            throw new InvalidInputException( "profile level is null in getProListByProfileLevel" );
        }
        String idenFieldName = null;
        Collection<UserFromSearch> solrSearchResult = null;
        switch ( profileLevel ) {
            case CommonConstants.PROFILE_LEVEL_COMPANY:
                idenFieldName = CommonConstants.COMPANY_ID_SOLR;
                break;
            case CommonConstants.PROFILE_LEVEL_REGION:
                idenFieldName = CommonConstants.REGIONS_SOLR;
                break;
            case CommonConstants.PROFILE_LEVEL_BRANCH:
                idenFieldName = CommonConstants.BRANCHES_SOLR;
                break;
            default:
                throw new InvalidInputException( "profile level is invalid in getProListByProfileLevel" );
        }
        solrSearchResult = solrSearchService.searchUsersByIden( iden, idenFieldName, true, start, numOfRows );

        LOG.debug( "Method getProListByProfileLevel finished successfully" );
        return solrSearchResult;
    }


    @Override
    public void generateVerificationUrl( Map<String, String> urlParams, String applicationUrl, String recipientMailId,
        String recipientName ) throws InvalidInputException, UndeliveredEmailException
    {
        String verficationUrl = urlGenerator.generateUrl( urlParams, applicationUrl );
        emailServices.sendEmailVerificationMail( verficationUrl, recipientMailId, recipientName );
    }


    /**
     * 
     * @param mailIds
     * @param entityType
     * @param userSettings
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    @Override
    public void generateAndSendEmailVerificationRequestLinkToAdmin( List<MiscValues> mailIds, long companyId, String entityType,
        OrganizationUnitSettings entitySettings ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method generateAndSendEmailVerificationRequestLinkToAdmin started " );
        Map<String, String> urlParams = null;

        if ( entitySettings == null ) {
            throw new InvalidInputException( "Invalid argument passed , passed entity setting is null: " );
        }


        User companyAdmin = userManagementService.getCompanyAdmin( companyId );
        if ( companyAdmin == null ) {
            throw new InvalidInputException( "No admin found for passed company id : " + companyId );
        }

        String adminName = companyAdmin.getFirstName();
        if ( companyAdmin.getLastName() != null && !companyAdmin.getLastName().isEmpty() ) {
            adminName = companyAdmin.getFirstName() + " " + companyAdmin.getLastName();
        }

        for ( MiscValues mailId : mailIds ) {
            String key = mailId.getKey();
            String emailId = mailId.getValue();
            if ( key.equalsIgnoreCase( CommonConstants.EMAIL_TYPE_WORK ) ) {
                urlParams = new HashMap<String, String>();
                urlParams.put( CommonConstants.EMAIL_ID, emailId );
                urlParams.put( CommonConstants.EMAIL_TYPE, CommonConstants.EMAIL_TYPE_WORK );
                urlParams.put( CommonConstants.ENTITY_ID_COLUMN, entitySettings.getIden() + "" );
                urlParams.put( CommonConstants.ENTITY_TYPE_COLUMN, entityType );
                urlParams.put( CommonConstants.URL_PARAM_VERIFICATION_REQUEST_TYPE,
                    CommonConstants.URL_PARAM_VERIFICATION_REQUEST_TYPE_TO_ADMIN );

                String verficationUrl = urlGenerator.generateUrl( urlParams,
                    applicationBaseUrl + CommonConstants.REQUEST_MAPPING_EMAIL_EDIT_VERIFICATION );
                emailServices.sendEmailVerificationRequestMailToAdmin( verficationUrl, companyAdmin.getEmailId(), adminName,
                    emailId, entitySettings.getContact_details().getName() );
            }
        }


    }


    @Override
    @Transactional
    public String updateEmailVerificationStatus( String urlParamsStr ) throws InvalidInputException, NonFatalException
    {
        Map<String, String> urlParams = urlGenerator.decryptParameters( urlParamsStr );
        if ( urlParams == null || urlParams.isEmpty() ) {
            throw new InvalidInputException( "Url params are invalid for email verification" );
        }

        String emailAddress = urlParams.get( CommonConstants.EMAIL_ID );
        String emailType = urlParams.get( CommonConstants.EMAIL_TYPE );
        long iden = Long.parseLong( urlParams.get( CommonConstants.ENTITY_ID_COLUMN ) );
        String collection = urlParams.get( CommonConstants.ENTITY_TYPE_COLUMN );
        String verificationType = urlParams.get( CommonConstants.URL_PARAM_VERIFICATION_REQUEST_TYPE );

        OrganizationUnitSettings unitSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( iden,
            collection );
        ContactDetailsSettings contactDetails = unitSettings.getContact_details();
        MailIdSettings mailIds = contactDetails.getMail_ids();
        User companyAdmin = null;

        if ( verificationType == null || verificationType.isEmpty() ) {
            throw new InvalidInputException(
                "Url params are invalid for email verification. Parameter Verification type missing" );
        }

        if ( emailType.equals( CommonConstants.EMAIL_TYPE_WORK ) ) {
            String emailVerified = mailIds.getWorkEmailToVerify();

            if ( emailVerified == null || emailVerified.isEmpty() || !emailVerified.equals( emailAddress ) ) {
                throw new InvalidInputException( "Email Id to verify does not match with our records" );
            }

            mailIds.setWork( emailVerified );
            mailIds.setWorkEmailToVerify( null );
            mailIds.setWorkEmailVerified( true );

            if ( collection.equals( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
                updateCompanyEmail( iden, emailVerified );

                Company company = userManagementService.getCompanyById( iden );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.EMAIL_ID_WORK, true );
                    userManagementService.updateCompany( company );
                }

            } else if ( collection.equals( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {

                Region region = userManagementService.getRegionById( iden );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.EMAIL_ID_WORK, true );
                    userManagementService.updateRegion( region );
                    companyAdmin = userManagementService.getCompanyAdmin( region.getCompany().getCompanyId() );
                }

            } else if ( collection.equals( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
                Branch branch = userManagementService.getBranchById( iden );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.EMAIL_ID_WORK, true );
                    userManagementService.updateBranch( branch );
                    companyAdmin = userManagementService.getCompanyAdmin( branch.getCompany().getCompanyId() );
                }
            } else if ( collection.equals( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {

                // Update User login name and email id
                User user = userManagementService.getUserByUserId( iden );
                user.setEmailId( emailVerified );
                user.setLoginName( emailVerified );
                userManagementService.updateUser( user, iden, true );

                updateIndividualEmail( iden, emailVerified );
                
                //update the corrupted record for newly registered user's email id
                surveyPreInitiationDao.updateAgentIdOfPreInitiatedSurveysByAgentEmailAddress( user, user.getLoginName() );

                // Fix for JIRA: SS-1358 - Updating email address should update SOLR records as well
                // BEGIN
                updateEmailIdInSolr( emailVerified, iden );
                // Fix for JIRA: SS-1358 - Updating email address should update SOLR records as well
                // END
                //get company admin
                companyAdmin = userManagementService.getCompanyAdmin( user.getCompany().getCompanyId() );
            }

            //send email verified mail to admin
            if ( verificationType.equalsIgnoreCase( CommonConstants.URL_PARAM_VERIFICATION_REQUEST_TYPE_TO_ADMIN ) ) {
                //send mail to entity
                emailServices.sendEmailVerifiedNotificationMail( emailVerified, unitSettings.getContact_details().getName() );
                //send mail to admin
                if ( companyAdmin != null ) {
                    String adminName = companyAdmin.getFirstName();
                    if ( companyAdmin.getLastName() != null && !companyAdmin.getLastName().isEmpty() ) {
                        adminName = companyAdmin.getFirstName() + " " + companyAdmin.getLastName();
                    }
                    emailServices.sendEmailVerifiedNotificationMailToAdmin( companyAdmin.getLoginName(), adminName,
                        emailVerified, unitSettings.getContact_details().getName() );
                }
            }
        } else if ( emailType.equals( CommonConstants.EMAIL_TYPE_PERSONAL ) ) {
            String emailVerified = mailIds.getPersonalEmailToVerify();

            if ( emailVerified == null || emailVerified.isEmpty() || !emailVerified.equals( emailAddress ) ) {
                throw new InvalidInputException( "Email Id to verify does not match with our records" );
            }

            if ( collection.equals( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
                Company company = userManagementService.getCompanyById( iden );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.EMAIL_ID_PERSONAL, true );
                    userManagementService.updateCompany( company );
                }
            } else if ( collection.equals( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {
                Region region = userManagementService.getRegionById( iden );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.EMAIL_ID_PERSONAL, true );
                    userManagementService.updateRegion( region );
                }
            } else if ( collection.equals( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
                Branch branch = userManagementService.getBranchById( iden );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.EMAIL_ID_PERSONAL, true );
                    userManagementService.updateBranch( branch );
                }
            }

            mailIds.setPersonal( mailIds.getPersonalEmailToVerify() );
            mailIds.setPersonalEmailToVerify( null );
            mailIds.setPersonalEmailVerified( true );
        }
        contactDetails.setMail_ids( mailIds );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_CONTACT_DETAIL_SETTINGS, contactDetails, unitSettings, collection );

        return verificationType;
    }


    /**
     * Method to fetch reviews based on the profile level specified, iden is one of
     * agentId/branchId/regionId or companyId based on the profile level
     */
    @Override
    @Transactional
    public List<SurveyPreInitiation> getIncompleteSurvey( long iden, double startScore, double limitScore, int startIndex,
        int numOfRows, String profileLevel, Date startDate, Date endDate, boolean realtechAdmin ) throws InvalidInputException
    {
        LOG.debug(
            "Method getIncompleteSurvey() called for iden: {} startScore: {} limitScore:{} startIndex: {} numOfRows: {} profileLevel: {}",
            iden, startScore, limitScore, startIndex, numOfRows, profileLevel );
        if ( iden <= 0l ) {
            LOG.warn( "iden is invalid while fetching incomplete reviews" );
            throw new InvalidInputException( "iden is invalid while fetching incomplete reviews" );
        }
        boolean isCompanyAdmin = false;
        Set<Long> agentIds = new HashSet<>();
        if ( profileLevel.equalsIgnoreCase( CommonConstants.PROFILE_LEVEL_COMPANY ) ) {
            isCompanyAdmin = true;
        } else {
            agentIds = getAgentIdsByProfileLevel( profileLevel, iden );
        }
        Timestamp startTime = null;
        Timestamp endTime = null;
        if ( startDate != null )
            startTime = new Timestamp( startDate.getTime() );
        if ( endDate != null )
            endTime = new Timestamp( endDate.getTime() );

        List<SurveyPreInitiation> surveys = null;
        if ( iden > 0l || ( agentIds != null && !agentIds.isEmpty() ) ) {
            surveys = surveyPreInitiationDao.getIncompleteSurvey( startTime, endTime, startIndex, numOfRows, agentIds,
                isCompanyAdmin, iden, realtechAdmin );
        }

        return surveys;
    }


    @Override
    @Transactional
    public long getIncompleteSurveyCount( long iden, String profileLevel, Date startDate, Date endDate )
        throws InvalidInputException
    {
        LOG.debug( "Getting incomplete survey count." );
        long count = 0;
        if ( iden <= 0l ) {
            LOG.warn( "iden is invalid while fetching incomplete reviews." );
            throw new InvalidInputException( "iden is invalid while fetching incomplete reviews" );
        }
        long companyId = -1;
        long agentId = -1;
        Timestamp startTime = null;
        Timestamp endTime = null;
        Set<Long> agentIds = null;
        if ( profileLevel.equalsIgnoreCase( CommonConstants.PROFILE_LEVEL_COMPANY ) ) {
            companyId = iden;
        } else if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_INDIVIDUAL ) ) {
            agentId = iden;
        } else {
            agentIds = getAgentIdsByProfileLevel( profileLevel, iden );
        }
        if ( startDate != null )
            startTime = new Timestamp( startDate.getTime() );
        if ( endDate != null )
            endTime = new Timestamp( endDate.getTime() );

        if ( companyId > 0l || agentId > 0l || ( agentIds != null && !agentIds.isEmpty() ) ) {
            count = surveyPreInitiationDao.getIncompleteSurveyCount( companyId, agentId,
                new int[] { CommonConstants.SURVEY_STATUS_PRE_INITIATED, CommonConstants.SURVEY_STATUS_INITIATED }, startTime,
                endTime, agentIds );
        }
        return count;
    }


    /**
     * Method to fetch all users for the list of branches specified
     */
    @Override
    public List<AgentSettings> getIndividualsByBranchIds( Set<Long> branchIds ) throws InvalidInputException
    {
        LOG.debug( "Method getIndividualsByBranchIds called for branchIds:" + branchIds );
        List<AgentSettings> users = null;
        if ( branchIds != null && !branchIds.isEmpty() ) {
            users = new ArrayList<AgentSettings>();
            for ( long branchId : branchIds ) {
                List<AgentSettings> tempUsers = getIndividualsByBranchId( branchId );
                if ( tempUsers != null && !tempUsers.isEmpty() ) {
                    users.addAll( tempUsers );
                }
            }
        }
        LOG.debug( "Method getIndividualsByBranchIds executed successfully" );
        return users;
    }


    /**
     * Method to fetch all users under the specified list of regions
     */
    @Override
    public List<AgentSettings> getIndividualsByRegionIds( Set<Long> regionIds )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method getIndividualsByBranchIds called for regionIds:" + regionIds );
        List<AgentSettings> users = null;
        if ( regionIds != null && !regionIds.isEmpty() ) {
            users = new ArrayList<AgentSettings>();
            for ( long regionId : regionIds ) {
                List<AgentSettings> tempUsers = getIndividualsByRegionId( regionId );
                if ( tempUsers != null && !tempUsers.isEmpty() ) {
                    users.addAll( tempUsers );
                }
            }
        }
        LOG.debug( "Method getIndividualsByRegionIds executed successfully" );
        return users;
    }


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
     * @throws ProfileNotFoundException 
     */
    @Override
    public void findProfileMailIdAndSendMail( String companyProfileName, String profileName, String message, String senderName,
        String senderMailId, String profileType )
        throws InvalidInputException, NoRecordsFetchedException, UndeliveredEmailException, ProfileNotFoundException
    {
        if ( companyProfileName == null || companyProfileName.isEmpty() ) {
            LOG.error( "Contact Us : companyProfileName parameter is empty or null!" );
            throw new InvalidInputException( "Contact Us : companyProfileName parameter is empty or null!" );
        }
        if ( profileName == null || profileName.isEmpty() ) {
            LOG.error( "Contact Us : profileName parameter is empty or null!" );
            throw new InvalidInputException( "Contact Us : profileName parameter is empty or null!" );
        }
        if ( message == null || message.isEmpty() ) {
            LOG.error( "Contact Us : message parameter is empty or null!" );
            throw new InvalidInputException( "Contact Us : message parameter is empty or null!" );
        }
        if ( senderName == null || senderName.isEmpty() ) {
            LOG.error( "Contact Us : senderName parameter is empty or null!" );
            throw new InvalidInputException( "Contact Us : senderName parameter is empty or null!" );
        }
        if ( senderMailId == null || senderMailId.isEmpty() ) {
            LOG.error( "Contact Us : senderMailId parameter is empty or null!" );
            throw new InvalidInputException( "Contact Us : senderMailId parameter is empty or null!" );
        }
        if ( profileType == null || profileType.isEmpty() ) {
            LOG.error( "Contact Us : profileType parameter is empty or null!" );
            throw new InvalidInputException( "Contact Us : profileType parameter is empty or null!" );
        }
        OrganizationUnitSettings settings = null;

        //Fetch the companysettings to first check if we have route all Contact Us emails to the Company Admin only
        LOG.debug( "Fetching the company settings from mongo for the company with profile name : " + companyProfileName );
        OrganizationUnitSettings companySettings = organizationUnitSettingsDao
            .fetchOrganizationUnitSettingsByProfileName( companyProfileName, CommonConstants.COMPANY_SETTINGS_COLLECTION );
        LOG.debug( "Settings fetched from mongo!" );
        
        
      //Fetch the companysettings to first check if we have route all Contact Us emails to the Company Admin only
        LOG.debug( "Fetching the agent settings from mongo for the company with profile name : " + profileName );
        OrganizationUnitSettings agentSettings = organizationUnitSettingsDao
            .fetchOrganizationUnitSettingsByProfileName( profileName, CommonConstants.AGENT_SETTINGS_COLLECTION );
        LOG.debug( "Settings fetched from mongo!" );

        
        List<String> recepients = new ArrayList<String>();
        
        
        if ( companySettings.isContactUsEmailsRoutedToCompanyAdmin() ) {
            settings = companySettings;
        } else {
        		//always send mail to company admin as well
        		recepients.add(companySettings.getContact_details().getMail_ids().getWork()) ;
        	
            if ( profileType.equals( CommonConstants.PROFILE_LEVEL_INDIVIDUAL ) ) {
            		//Agent settings are already fetched, re-use that
                settings = agentSettings;
                if ( settings != null
                    && ( settings.getContact_details() == null || settings.getContact_details().getMail_ids() == null
                        || StringUtils.isEmpty( settings.getContact_details().getMail_ids().getWork() ) ) ) {
                    Map<String, Long> hierarchyDetails = getHierarchyDetailsByEntity( CommonConstants.AGENT_ID,
                        settings.getIden() );
                    long regionId = hierarchyDetails.get( CommonConstants.REGION_ID_COLUMN );
                    long branchId = hierarchyDetails.get( CommonConstants.BRANCH_ID_COLUMN );
                    settings = organizationManagementService.getBranchSettingsDefault( branchId );
                    if ( settings == null || settings.getContact_details() == null
                        || settings.getContact_details().getMail_ids() == null
                        || StringUtils.isEmpty( settings.getContact_details().getMail_ids().getWork() ) ) {
                        settings = organizationManagementService.getRegionSettings( regionId );
                        if ( settings == null || settings.getContact_details() == null
                            || settings.getContact_details().getMail_ids() == null
                            || StringUtils.isEmpty( settings.getContact_details().getMail_ids().getWork() ) ) {
                            //Company settings are already fetched, re-use that
                            settings = companySettings;
                        }
                    }
                }
                LOG.debug( "Settings fetched from mongo!" );
            } else if ( profileType.equals( CommonConstants.PROFILE_LEVEL_BRANCH ) ) {
                LOG.debug( "Fetching the branch settings from mongo for the branch with profile name : " + profileName );
                settings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName( profileName,
                    CommonConstants.BRANCH_SETTINGS_COLLECTION );
                if ( settings != null
                    && ( settings.getContact_details() == null || settings.getContact_details().getMail_ids() == null
                        || StringUtils.isEmpty( settings.getContact_details().getMail_ids().getWork() ) ) ) {
                    Map<String, Long> hierarchyDetails = getHierarchyDetailsByEntity( CommonConstants.BRANCH_ID,
                        settings.getIden() );
                    long regionId = hierarchyDetails.get( CommonConstants.REGION_ID_COLUMN );
                    settings = organizationManagementService.getRegionSettings( regionId );
                    if ( settings == null || settings.getContact_details() == null
                        || settings.getContact_details().getMail_ids() == null
                        || StringUtils.isEmpty( settings.getContact_details().getMail_ids().getWork() ) ) {
                        //Company settings are already fetched, re-use that
                        settings = companySettings;
                    }
                }
                LOG.debug( "Settings fetched from mongo!" );
            } else if ( profileType.equals( CommonConstants.PROFILE_LEVEL_REGION ) ) {
                LOG.debug( "Fetching the region settings from mongo for the region with profile name : " + profileName );
                settings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName( profileName,
                    CommonConstants.REGION_SETTINGS_COLLECTION );
                if ( settings != null
                    && ( settings.getContact_details() == null || settings.getContact_details().getMail_ids() == null
                        || StringUtils.isEmpty( settings.getContact_details().getMail_ids().getWork() ) ) ) {
                    //Company settings are already fetched, re-use that
                    settings = companySettings;
                }
                LOG.debug( "Settings fetched from mongo!" );
            } else if ( profileType.equals( CommonConstants.PROFILE_LEVEL_COMPANY ) ) {
                //Company settings are already fetched, re-use that
                settings = companySettings;
            } else {
                LOG.error( "Profile level not known:{}", profileType );
                throw new InvalidInputException( "Profile level not known:" + profileType );
            }
        }

        if ( settings != null ) {
            LOG.debug( "Sending the contact us mail to the agent along with admin" );
            recepients.add(settings.getContact_details().getMail_ids().getWork()) ;
            emailServices.sendContactUsMail( recepients,
                settings.getContact_details().getName(), senderName, senderMailId, agentSettings.getContact_details().getMail_ids().getWork(), agentSettings.getContact_details().getName(), message );
            LOG.debug( "Contact us mail sent!" );
        } else {
            LOG.error( "No records found for profile settings of profile name: {}, profile type: {} in mongo", profileName,
                profileType );
            throw new NoRecordsFetchedException( "No records found for profile settings of profile name: " + profileName
                + ", profile type: " + profileType + " in mongo" );
        }
    }


    /*
     * Method to store status of a user into the mongo.
     */
    @Override
    public void addSocialPosts( User user, long entityId, String entityType, String postText ) throws InvalidInputException
    {
        LOG.debug( "Method to add post to a user's profile started." );
        SocialPost socialPost = new SocialPost();
        socialPost.setPostedBy( user.getFirstName() + " " + user.getLastName() );
        socialPost.setPostText( postText );
        socialPost.setSource( "SocialSurvey" );

        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            socialPost.setCompanyId( user.getCompany().getCompanyId() );
        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            socialPost.setRegionId( entityId );
            socialPost.setCompanyId( user.getCompany().getCompanyId() );
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            socialPost.setBranchId( entityId );
            socialPost.setCompanyId( user.getCompany().getCompanyId() );
        } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
            entityId = user.getUserId();
            socialPost.setAgentId( entityId );
            socialPost.setCompanyId( user.getCompany().getCompanyId() );
        }

        socialPost.setTimeInMillis( System.currentTimeMillis() );
        socialPostDao.addPostToUserProfile( socialPost );
        LOG.debug( "Updating modified on column in aagent hierarchy fro agent " + user.getFirstName() );
        surveyHandler.updateModifiedOnColumnForEntity( entityType, entityId );
        LOG.debug( "Method to add post to a user's profile finished." );
    }


    /*
     * Method to delete status of a user into the mongo.
     */
    @Override
    public void deleteSocialPost( String postMongoId ) throws InvalidInputException
    {
        LOG.debug( "Method to delete post to a user's profile started." );
        SocialPost socialPost = socialPostDao.getPostByMongoObjectId( postMongoId );
        if ( socialPost == null ) {
            throw new InvalidInputException( "No Status Found", DisplayMessageConstants.GENERAL_ERROR );
        }

        if ( !socialPost.getSource().equals( CommonConstants.POST_SOURCE_SOCIAL_SURVEY ) ) {
            throw new InvalidInputException( "Not a SocialSurvey Status", DisplayMessageConstants.GENERAL_ERROR );
        }

        long agentId = socialPost.getAgentId();
        long regionId = socialPost.getRegionId();
        long companyId = socialPost.getCompanyId();
        long branchId = socialPost.getBranchId();
        socialPostDao.removePostFromUsersProfile( socialPost );
        //JIRA SS-1329
        try {
            solrSearchService.removeSocialPostFromSolr( postMongoId );
        } catch ( SolrException e ) {
            throw new InvalidInputException( "Error removing social post from Solr. Reason : ", e );
        }
        LOG.debug( "Updating modified on column in aagent hierarchy fro agent " );
        if ( companyId > 0 ) {
            surveyHandler.updateModifiedOnColumnForEntity( CommonConstants.COMPANY_ID_COLUMN, companyId );
        }
        if ( regionId > 0 ) {
            surveyHandler.updateModifiedOnColumnForEntity( CommonConstants.REGION_ID_COLUMN, regionId );
        }
        if ( branchId > 0 ) {
            surveyHandler.updateModifiedOnColumnForEntity( CommonConstants.BRANCH_ID_COLUMN, branchId );
        }
        if ( agentId > 0 ) {
            surveyHandler.updateModifiedOnColumnForEntity( CommonConstants.AGENT_ID_COLUMN, agentId );
        }
        LOG.debug( "Method to delete post to a user's profile finished." );
    }


    /*
     * Method to fetch social posts for a particular user.
     */
    @Override
    public List<SocialPost> getSocialPosts( long entityId, String entityType, int startIndex, int batchSize )
        throws InvalidInputException
    {
        LOG.debug( "Method to fetch social posts , getSocialPosts() started." );
        if ( entityType == null ) {
            throw new InvalidInputException( "No entity type found in session", DisplayMessageConstants.GENERAL_ERROR );
        }

        String key = CommonConstants.AGENT_ID;
        long iden = entityId;

        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            key = CommonConstants.COMPANY_ID;
        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            key = CommonConstants.REGION_ID;
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            key = CommonConstants.BRANCH_ID;
        }

        List<SocialPost> posts = socialPostDao.getSocialPosts( iden, key, startIndex, batchSize );
        LOG.debug( "Method to fetch social posts , getSocialPosts() finished." );
        return posts;
    }


    /*
     * Method to fetch social posts for a particular user.
     */
    @Override
    public List<SocialPost> getCumulativeSocialPosts( long entityId, String entityType, int startIndex, int numOfRows,
        String profileLevel, Date startDate, Date endDate ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method to fetch social posts , getCumulativeSocialPosts() started." );
        List<SocialPost> posts = new ArrayList<SocialPost>();
        if ( entityType == null ) {
            throw new InvalidInputException( "No entity type found in session", DisplayMessageConstants.GENERAL_ERROR );
        }

        //If agent, get social posts for only that agent.
        if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
            posts = socialPostDao.getSocialPosts( entityId, CommonConstants.AGENT_ID, startIndex, numOfRows, startDate,
                endDate );
            //If company, get social posts for that company, all the regions, branches and agents in that company.
        } else if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            //Get social posts for company
            posts = socialPostDao.getSocialPosts( entityId, CommonConstants.COMPANY_ID, startIndex, numOfRows, startDate,
                endDate );
            Company company = organizationManagementService.getCompanyById( entityId );
            //Get social posts for all the regions in the company.
            for ( Region region : company.getRegions() ) {
                posts.addAll( socialPostDao.getSocialPosts( region.getRegionId(), CommonConstants.REGION_ID, startIndex,
                    numOfRows, startDate, endDate ) );
            }
            //Get social posts for all the branches in the company
            for ( Branch branch : company.getBranches() ) {
                posts.addAll( socialPostDao.getSocialPosts( branch.getBranchId(), CommonConstants.BRANCH_ID, startIndex,
                    numOfRows, startDate, endDate ) );
            }
            //Get social posts for all the users in the company
            for ( User user : company.getUsers() ) {
                posts.addAll( socialPostDao.getSocialPosts( user.getUserId(), CommonConstants.AGENT_ID, startIndex, numOfRows,
                    startDate, endDate ) );
            }
            //Get all social posts for region
        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            Region region = userManagementService.getRegionById( entityId );
            //Get social posts for the region
            posts = socialPostDao.getSocialPosts( entityId, CommonConstants.REGION_ID, startIndex, numOfRows, startDate,
                endDate );
            //Get social posts for all the branches in the region
            for ( Branch branch : region.getBranches() ) {
                posts.addAll( socialPostDao.getSocialPosts( branch.getBranchId(), CommonConstants.BRANCH_ID, startIndex,
                    numOfRows, startDate, endDate ) );
            }
            //Get social posts for all the users in the region

            if ( getIndividualsByRegionId( entityId ) != null ) {
                for ( AgentSettings user : getIndividualsByRegionId( entityId ) ) {
                    posts.addAll( socialPostDao.getSocialPosts( user.getIden(), CommonConstants.AGENT_ID, startIndex, numOfRows,
                        startDate, endDate ) );
                }
            }
            //Get all social posts for branch
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            //Get social posts for the branch
            posts = socialPostDao.getSocialPosts( entityId, CommonConstants.BRANCH_ID, startIndex, numOfRows, startDate,
                endDate );
            //Get social posts for all the users in the branch
            if ( getIndividualsByBranchId( entityId ) != null ) {
                for ( AgentSettings user : getIndividualsByBranchId( entityId ) ) {
                    posts.addAll( socialPostDao.getSocialPosts( user.getIden(), CommonConstants.AGENT_ID, startIndex, numOfRows,
                        startDate, endDate ) );
                }
            }
        }
        LOG.debug( "Method to fetch social posts , getCumulativeSocialPosts() finished." );
        return posts;
    }


    /*
     * Method to fetch social posts for a particular user.
     */
    @Override
    public long getPostsCountForUser( String columnName, long columnValue )
    {
        LOG.debug( "Method to fetch count of social posts for a particular user, getPostsCountForUser() started." );
        long postsCount = socialPostDao.getPostsCountByUserId( columnName, columnValue );
        LOG.debug( "Method to fetch count of social posts for a particular user, getPostsCountForUser() finished." );
        return postsCount;
    }


    @Override
    public void updateLinkedInProfileData( String collectionName, OrganizationUnitSettings organizationUnitSettings,
        LinkedInProfileData linkedInProfileData ) throws InvalidInputException
    {
        LOG.debug( "Updating linked in profile data into " + collectionName );
        if ( linkedInProfileData == null ) {
            throw new InvalidInputException( "LinkedInProfile details passed can not be null" );
        }
        LOG.debug( "Updating linkedin profile detail information" );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_LINKEDIN_PROFILEDATA, linkedInProfileData, organizationUnitSettings,
            collectionName );

        String profileImageUrl = organizationUnitSettings.getProfileImageUrl();

        if ( ( profileImageUrl == null || profileImageUrl.trim().isEmpty() ) && linkedInProfileData.getPictureUrls() != null
            && linkedInProfileData.getPictureUrls().get_total() > 0 ) {
            profileImageUrl = linkedInProfileData.getPictureUrls().getValues().get( 0 );
            //Set profileImage and thumbnail
            updateProfileImage( collectionName, organizationUnitSettings, profileImageUrl );
            /*organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
                MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_IMAGE, profileImageUrl, organizationUnitSettings,
                collectionName );*/
        }

        LOG.debug( "Updated the linkedin profile data." );

    }


    @Override
    public void updateAgentExpertise( AgentSettings agentSettings, List<String> expertise ) throws InvalidInputException
    {
        if ( expertise == null || expertise.isEmpty() ) {
            throw new InvalidInputException( "Expertise list is not proper" );
        }
        LOG.debug( "Updating agent expertise" );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings( MongoOrganizationUnitSettingDaoImpl.KEY_EXPERTISE,
            expertise, agentSettings );
        LOG.debug( "Updated agent expertise." );
    }


    @Override
    public void updateAgentHobbies( AgentSettings agentSettings, List<String> hobbies ) throws InvalidInputException
    {
        if ( hobbies == null || hobbies.isEmpty() ) {
            throw new InvalidInputException( "Hobbies list is not proper" );
        }
        LOG.debug( "Updating agent hobbies" );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings( MongoOrganizationUnitSettingDaoImpl.KEY_HOBBIES, hobbies,
            agentSettings );
        LOG.debug( "Updated agent hobbies." );
    }


    @Override
    public void updateAgentCompanyPositions( AgentSettings agentSettings, List<CompanyPositions> companyPositions )
        throws InvalidInputException
    {
        if ( companyPositions == null || companyPositions.isEmpty() ) {
            throw new InvalidInputException( "Company positions passed are not proper" );
        }
        LOG.debug( "Updating company positions" );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings( MongoOrganizationUnitSettingDaoImpl.KEY_COMPANY_POSITIONS,
            companyPositions, agentSettings );
        LOG.debug( "Updated company positions." );
    }


    @Override
    public void updateProfileStages( List<ProfileStage> profileStages, OrganizationUnitSettings settings,
        String collectionName )
    {
        LOG.debug( "Method to update profile stages started." );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_STAGES, profileStages, settings, collectionName );
        LOG.debug( "Method to update profile stages finished." );
    }


    @Override
    public void setAgentProfileUrlForReview( List<SurveyDetails> reviews ) throws InvalidInputException
    {
        String profileUrl;
        String facebookShareUrl = "app_id=" + facebookAppId;
        String googleApiKey = googlePlusId;
        if ( reviews != null && !reviews.isEmpty() ) {
            for ( SurveyDetails review : reviews ) {
                String baseProfileUrl;
                //JIRA SS-1286
                /*Collection<UserFromSearch> documents = solrSearchService.searchUsersByIden( review.getAgentId(),
                    CommonConstants.USER_ID_SOLR, true, 0, 1 );*/
                // adding completeProfileUrl
                OrganizationUnitSettings unitSetting = null;
                if ( review.getSource() != null && !review.getSource().isEmpty()
                    && review.getSource().equals( CommonConstants.SURVEY_SOURCE_ZILLOW ) ) {
                    if ( review.getAgentId() > 0 ) {
                        unitSetting = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( review.getAgentId(),
                            CommonConstants.AGENT_SETTINGS_COLLECTION );
                        baseProfileUrl = applicationBaseUrl + CommonConstants.AGENT_PROFILE_FIXED_URL;
                    } else if ( review.getBranchId() > 0 ) {
                        unitSetting = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( review.getBranchId(),
                            CommonConstants.BRANCH_SETTINGS_COLLECTION );
                        baseProfileUrl = applicationBaseUrl + CommonConstants.BRANCH_PROFILE_FIXED_URL;
                    } else if ( review.getRegionId() > 0 ) {
                        unitSetting = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( review.getRegionId(),
                            CommonConstants.REGION_SETTINGS_COLLECTION );
                        baseProfileUrl = applicationBaseUrl + CommonConstants.REGION_PROFILE_FIXED_URL;
                    } else if ( review.getCompanyId() > 0 ) {
                        unitSetting = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( review.getCompanyId(),
                            CommonConstants.COMPANY_SETTINGS_COLLECTION );
                        baseProfileUrl = applicationBaseUrl + CommonConstants.COMPANY_PROFILE_FIXED_URL;
                    } else {
                        LOG.warn( "The zillow review with ID : {} does not have any hierarchy ID set", review.get_id() );
                        throw new InvalidInputException(
                            "The zillow review with ID : " + review.get_id() + "does not have any hierarchy ID set" );
                    }
                } else {
                    if ( review.getAgentId() > 0 ) {
                        unitSetting = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( review.getAgentId(),
                            CommonConstants.AGENT_SETTINGS_COLLECTION );
                        baseProfileUrl = applicationBaseUrl + CommonConstants.AGENT_PROFILE_FIXED_URL;
                    } else if ( review.getBranchId() > 0 ) {
                        unitSetting = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( review.getBranchId(),
                            CommonConstants.BRANCH_SETTINGS_COLLECTION );
                        baseProfileUrl = applicationBaseUrl + CommonConstants.BRANCH_PROFILE_FIXED_URL;
                    } else if ( review.getRegionId() > 0 ) {
                        unitSetting = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( review.getRegionId(),
                            CommonConstants.REGION_SETTINGS_COLLECTION );
                        baseProfileUrl = applicationBaseUrl + CommonConstants.REGION_PROFILE_FIXED_URL;
                    } else if ( review.getCompanyId() > 0 ) {
                        unitSetting = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( review.getCompanyId(),
                            CommonConstants.COMPANY_SETTINGS_COLLECTION );
                        baseProfileUrl = applicationBaseUrl + CommonConstants.COMPANY_PROFILE_FIXED_URL;
                    } else {
                        LOG.warn( "The Review with ID : {} does not have any hierarchy ID set", review.get_id() );
                        throw new InvalidInputException(
                            "The Review with ID : " + review.get_id() + "does not have any hierarchy ID set" );
                    }

                }
                if ( unitSetting != null ) {
                    profileUrl = (String) unitSetting.getProfileUrl();
                    review.setCompleteProfileUrl( baseProfileUrl + profileUrl );
                    review.setGoogleApi( googleApiKey );
                    review.setFaceBookShareUrl( facebookShareUrl );
                } else {
                    LOG.warn( "An agent with ID : {} does not exist", review.getAgentId() );
                    throw new InvalidInputException( "An agent with ID : " + review.getAgentId() + " does not exist" );
                }

                /*OrganizationUnitSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( review
                    .getAgentId() );*/
                if ( unitSetting != null && unitSetting.getSocialMediaTokens() != null ) {
                    SocialMediaTokens mediaTokens = unitSetting.getSocialMediaTokens();

                    // adding yelpUrl
                    if ( mediaTokens.getYelpToken() != null && mediaTokens.getYelpToken().getYelpPageLink() != null ) {
                        review.setYelpProfileUrl( mediaTokens.getYelpToken().getYelpPageLink() );
                    }

                    // adding zillowUrl
                    if ( mediaTokens.getZillowToken() != null && mediaTokens.getZillowToken().getZillowProfileLink() != null ) {
                        review.setZillowProfileUrl( mediaTokens.getZillowToken().getZillowProfileLink() );
                    }

                    // adding lendingTreeUrl
                    if ( mediaTokens.getLendingTreeToken() != null
                        && mediaTokens.getLendingTreeToken().getLendingTreeProfileLink() != null ) {
                        review.setLendingTreeProfileUrl( mediaTokens.getLendingTreeToken().getLendingTreeProfileLink() );
                    }
                    if ( mediaTokens.getRealtorToken() != null
                        && mediaTokens.getRealtorToken().getRealtorProfileLink() != null ) {
                        review.setRealtorProfileUrl( mediaTokens.getRealtorToken().getRealtorProfileLink() );
                    }
                    if ( mediaTokens.getGoogleBusinessToken() != null
                        && mediaTokens.getGoogleBusinessToken().getGoogleBusinessLink() != null ) {
                        review.setGoogleBusinessProfileUrl( mediaTokens.getGoogleBusinessToken().getGoogleBusinessLink() );
                    }
                }
            }
        }
    }


    List<CompanyPositions> sortCompanyPositions( List<CompanyPositions> positions )
    {
        LOG.debug( "Sorting company positions" );
        if ( positions != null && positions.size() > 0 ) {
            Collections.sort( positions );
        }
        return positions;
    }


    Set<Long> getAgentIdsByProfileLevel( String profileLevel, long iden ) throws InvalidInputException
    {
        if ( profileLevel == null || profileLevel.isEmpty() ) {
            LOG.warn( "profile level is null or empty while getting agents." );
            throw new InvalidInputException( "profile level is null or empty while getting agents" );
        }
        Set<Long> userIds = new HashSet<>();
        switch ( profileLevel ) {
            case CommonConstants.PROFILE_LEVEL_REGION:
                userIds = userProfileDao.findUserIdsByRegion( iden );
                return userIds;
            case CommonConstants.PROFILE_LEVEL_BRANCH:
                userIds = userProfileDao.findUserIdsByBranch( iden );
                return userIds;
            case CommonConstants.PROFILE_LEVEL_INDIVIDUAL:
                userIds.add( iden );
                return userIds;
            default:
                LOG.warn( "Invalid profile level while getting iden column name." );
                throw new InvalidInputException( "Invalid profile level while getting iden column name" );
        }
    }


    @Override
    @Transactional
    public String aggregateDisclaimer( OrganizationUnitSettings unitSettings, String entity ) throws InvalidInputException
    {
        LOG.debug( "Method aggregateDisclaimer() called from ProfileManagementService" );
        String disclaimer = "";

        if ( unitSettings.getDisclaimer() != null && !unitSettings.getDisclaimer().isEmpty() ) {
            return unitSettings.getDisclaimer();
        }

        OrganizationUnitSettings entitySetting = null;
        if ( entity.equals( CommonConstants.AGENT_ID ) ) {
            User user = userManagementService.getUserByUserId( unitSettings.getIden() );

            for ( UserProfile userProfile : user.getUserProfiles() ) {
                if ( userProfile.getProfilesMaster()
                    .getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                    if ( userProfile.getBranchId() > 0l ) {
                        entitySetting = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
                            userProfile.getBranchId(), MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
                    } else {
                        LOG.warn( "Not a valid branch id for branch profile: " + userProfile + ". Skipping the record" );
                    }
                }
                if ( userProfile.getProfilesMaster()
                    .getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                    if ( userProfile.getRegionId() > 0l ) {
                        entitySetting = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
                            userProfile.getRegionId(), MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                    } else {
                        LOG.warn( "Not a valid region id for region profile: " + userProfile + ". Skipping the record" );
                    }
                }
                if ( userProfile.getProfilesMaster()
                    .getProfileId() == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID ) {
                    if ( userProfile.getRegionId() > 0l ) {
                        entitySetting = organizationManagementService.getCompanySettings( user );
                    } else {
                        LOG.warn( "Not a valid company" );
                    }
                }

                if ( entitySetting != null && entitySetting.getDisclaimer() != null
                    && !entitySetting.getDisclaimer().isEmpty() ) {
                    return entitySetting.getDisclaimer();
                }
                entitySetting = null;
            }

            if ( disclaimer.isEmpty() ) {
                entitySetting = organizationManagementService.getCompanySettings( user );
                disclaimer = entitySetting.getDisclaimer();
            }
        } else if ( entity.equals( CommonConstants.BRANCH_ID ) ) {
            Branch branch = branchDao.findById( Branch.class, unitSettings.getIden() );

            // check for region
            entitySetting = organizationManagementService.getRegionSettings( branch.getRegion().getRegionId() );
            if ( entitySetting != null && entitySetting.getDisclaimer() != null && !entitySetting.getDisclaimer().isEmpty() ) {
                return entitySetting.getDisclaimer();
            }

            // check for company
            entitySetting = organizationManagementService.getCompanySettings( branch.getCompany().getCompanyId() );
            if ( entitySetting != null && entitySetting.getDisclaimer() != null && !entitySetting.getDisclaimer().isEmpty() ) {
                return entitySetting.getDisclaimer();
            }
        } else if ( entity.equals( CommonConstants.REGION_ID ) ) {
            Region region = regionDao.findById( Region.class, unitSettings.getIden() );

            // check for company
            entitySetting = organizationManagementService.getCompanySettings( region.getCompany().getCompanyId() );
            if ( entitySetting != null && entitySetting.getDisclaimer() != null && !entitySetting.getDisclaimer().isEmpty() ) {
                return entitySetting.getDisclaimer();
            }
        }

        LOG.debug( "Method aggregateDisclaimer() called from ProfileManagementService" );
        return disclaimer;
    }


    @Override
    @Transactional
    public List<AgentRankingReport> getAgentReport( long iden, String columnName, Date startDate, Date endDate, Object object )
        throws InvalidInputException
    {
        LOG.debug( "Method to get Agent's Report for a specific time and all time started." );
        if ( columnName == null || columnName.isEmpty() ) {
            throw new InvalidInputException( "Null/Empty value passed for profile level." );
        }
        if ( iden < 0 ) {
            throw new InvalidInputException( "Invalid value passed for iden of profile level." );
        }

        Calendar calendar = Calendar.getInstance();
        if ( startDate != null ) {
            calendar.setTime( startDate );
            calendar.set( Calendar.HOUR_OF_DAY, 0 );
            calendar.set( Calendar.MINUTE, 0 );
            calendar.set( Calendar.SECOND, 0 );
            calendar.set( Calendar.MILLISECOND, 0 );
            startDate = calendar.getTime();
        }
        if ( endDate != null ) {
            calendar.setTime( endDate );
            calendar.set( Calendar.HOUR_OF_DAY, 23 );
            calendar.set( Calendar.MINUTE, 59 );
            calendar.set( Calendar.SECOND, 59 );
            calendar.set( Calendar.MILLISECOND, 0 );
            endDate = calendar.getTime();
        }

        Map<Long, AgentRankingReport> agentReportData = new HashMap<>();
        // Generate entries for all active users in the company
        initializeAgentReportData( agentReportData, columnName, iden );
        surveyDetailsDao.getAverageScore( startDate, endDate, agentReportData, columnName, iden, false );
        surveyDetailsDao.getCompletedSurveysCount( startDate, endDate, agentReportData, columnName, iden, false );
        // FIX for JIRA: SS-1112: BOC
        surveyPreInitiationDao.getIncompleteSurveysCount( startDate, endDate, agentReportData );
        // FIX for JIRA: SS-1112: EOC
        organizationUnitSettingsDao.setAgentDetails( agentReportData );

        LOG.debug( "Method to get Agent's Report for a specific time and all time finished." );
        return new ArrayList<>( agentReportData.values() );
    }


    /**
     * Method to initialize agent report data(to include all active agent in the company)
     * @param agentReportData
     * @param columnName
     * @param iden
     */
    void initializeAgentReportData( Map<Long, AgentRankingReport> agentReportData, String columnName, long iden )
    {
        Set<Long> agentIds = new HashSet<Long>();
        switch ( columnName ) {
            case CommonConstants.COMPANY_ID_COLUMN:
                Company company = companyDao.findById( Company.class, iden );
                agentIds = usersDao.getActiveUserIdsForCompany( company );
                break;
            case CommonConstants.REGION_ID_COLUMN:
                agentIds = userProfileDao.findUserIdsByRegion( iden );
                break;
            case CommonConstants.BRANCH_ID_COLUMN:
                agentIds = userProfileDao.findUserIdsByBranch( iden );
                break;
        }
        for ( Long agentId : agentIds ) {
            agentReportData.put( agentId, new AgentRankingReport() );
        }
    }


    @Override
    @Transactional
    public List<BreadCrumb> getIndividualsBreadCrumb( Long userId )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException
    {
        User user = userDao.findById( User.class, userId );

        List<UserProfile> userProfiles = user.getUserProfiles();
        UserProfile userProfile = null;
        for ( UserProfile element : userProfiles ) {
            if (element.getStatus() == CommonConstants.STATUS_ACTIVE && element.getIsPrimary() == CommonConstants.IS_PRIMARY_TRUE ) {
                userProfile = element;
                break;
            }
        }
        if ( userProfile == null ) {
            throw new ProfileNotFoundException( "No records found  " );
        }

        Company company = userProfile.getCompany();
        AccountType accountType = AccountType
            .getAccountType( company.getLicenseDetails().get( 0 ).getAccountsMaster().getAccountsMasterId() );

        LOG.debug( "Method getIndividualsBreadCrumb called :" );
        List<BreadCrumb> breadCrumbList = new ArrayList<>();

        switch ( accountType.getValue() ) {
            case CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL:
                updateCrumbListWithVerticalName( breadCrumbList, company );
                break;
            case CommonConstants.ACCOUNTS_MASTER_TEAM:
                updateCrumbListWithCompanyName( breadCrumbList, company );
                updateCrumbListWithVerticalName( breadCrumbList, company );
                break;
            case CommonConstants.ACCOUNTS_MASTER_COMPANY:
                Branch compBranch = branchDao.findById( Branch.class, userProfile.getBranchId() );
                updateCrumbListWithBranchName( breadCrumbList, compBranch );

                updateCrumbListWithCompanyName( breadCrumbList, company );
                updateCrumbListWithVerticalName( breadCrumbList, company );
                break;
            case CommonConstants.ACCOUNTS_MASTER_ENTERPRISE:
                Branch branch = branchDao.findById( Branch.class, userProfile.getBranchId() );
                updateCrumbListWithBranchName( breadCrumbList, branch );

                //JIRA SS-1337
                if ( branch == null ) {
                    throw new InvalidInputException( "No branch with ID : " + userProfile.getBranchId() + " was found" );
                } else if ( branch.getRegion() == null ) {
                    throw new InvalidInputException(
                        "No region associated to branch with ID : " + userProfile.getBranchId() + " was found" );
                }
                Region region = branch.getRegion();
                //Region region = regionDao.findById( Region.class, userProfile.getRegionId() );
                updateCrumbListWithRegionName( breadCrumbList, region );

                updateCrumbListWithCompanyName( breadCrumbList, company );
                updateCrumbListWithVerticalName( breadCrumbList, company );
                break;
            default:
                throw new InvalidInputException( "Invalid account type detected" );
        }

        Collections.reverse( breadCrumbList );
        LOG.debug( "Method getIndividualsBreadCrumb finished :" );
        return breadCrumbList;
    }


    @Override
    @Transactional
    public List<BreadCrumb> getRegionsBreadCrumb( OrganizationUnitSettings regionProfile )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method getRegionsBreadCrumb called :" );
        List<BreadCrumb> breadCrumbList = new ArrayList<>();

        Region region = regionDao.findById( Region.class, regionProfile.getIden() );
        Company company = region.getCompany();
        updateCrumbListWithCompanyName( breadCrumbList, company );
        updateCrumbListWithVerticalName( breadCrumbList, company );

        Collections.reverse( breadCrumbList );
        LOG.debug( "Method getRegionsBreadCrumb finished :" );
        return breadCrumbList;
    }


    @Override
    @Transactional
    public List<BreadCrumb> getBranchsBreadCrumb( OrganizationUnitSettings branchProfile )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method getBranchsBreadCrumb called :" );
        List<BreadCrumb> breadCrumbList = new ArrayList<>();

        Branch branch = branchDao.findById( Branch.class, branchProfile.getIden() );

        Region region = branch.getRegion();
        updateCrumbListWithRegionName( breadCrumbList, region );

        Company company = branch.getCompany();
        updateCrumbListWithCompanyName( breadCrumbList, company );
        updateCrumbListWithVerticalName( breadCrumbList, company );

        Collections.reverse( breadCrumbList );
        LOG.debug( "Method getBranchsBreadCrumb finished :" );
        return breadCrumbList;
    }


    void updateCrumbListWithCompanyName( List<BreadCrumb> breadCrumbList, Company company ) throws InvalidInputException
    {
        BreadCrumb breadCrumb = new BreadCrumb();

        OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( company.getCompanyId() );
        breadCrumb.setBreadCrumbProfile( company.getCompany() );
        breadCrumb.setBreadCrumbUrl( companySettings.getCompleteProfileUrl() );
        breadCrumb.setHideFromBreadCrumb( companySettings.getHideFromBreadCrumb() );
        breadCrumbList.add( breadCrumb );
    }


    void updateCrumbListWithBranchName( List<BreadCrumb> breadCrumbList, Branch branch )
        throws InvalidInputException, NoRecordsFetchedException
    {
        if ( branch.getIsDefaultBySystem() != CommonConstants.IS_DEFAULT_BY_SYSTEM_YES ) {
            OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettings( branch.getBranchId() )
                .getOrganizationUnitSettings();
            BreadCrumb breadCrumb = new BreadCrumb();
            breadCrumb.setBreadCrumbProfile( branch.getBranch() );
            breadCrumb.setBreadCrumbUrl( branchSettings.getCompleteProfileUrl() );
            breadCrumb.setHideFromBreadCrumb( branchSettings.getHideFromBreadCrumb() );
            breadCrumbList.add( breadCrumb );
        }
    }


    void updateCrumbListWithRegionName( List<BreadCrumb> breadCrumbList, Region region ) throws InvalidInputException
    {
        if ( region.getIsDefaultBySystem() != CommonConstants.IS_DEFAULT_BY_SYSTEM_YES ) {
            OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( region.getRegionId() );
            BreadCrumb breadCrumb = new BreadCrumb();
            breadCrumb.setBreadCrumbProfile( region.getRegion() );
            breadCrumb.setBreadCrumbUrl( regionSettings.getCompleteProfileUrl() );
            breadCrumb.setHideFromBreadCrumb( regionSettings.getHideFromBreadCrumb() );
            breadCrumbList.add( breadCrumb );
        }
    }


    void updateCrumbListWithVerticalName( List<BreadCrumb> breadCrumbList, Company company )
    {
        BreadCrumb breadCrumb = new BreadCrumb();
        breadCrumb.setBreadCrumbProfile( company.getVerticalsMaster().getVerticalName() );
        breadCrumbList.add( breadCrumb );
    }


    @Override
    @Transactional
    public List<OrganizationUnitSettings> getCompanyList( String verticalName )
        throws InvalidInputException, ProfileNotFoundException
    {
        LOG.debug( "Method getCompanyList called :" );
        List<OrganizationUnitSettings> companyList = organizationUnitSettingsDao.getCompanyListByVerticalName( verticalName );
        LOG.debug( "Method getCompanyList finished :" );
        return companyList;
    }


    @Override
    @Transactional
    public void updateCompanyName( long userId, long companyId, String companyName ) throws InvalidInputException
    {
        LOG.debug( "Method updateCompanyName of profileManagementService called for companyId : " + companyId );

        Company company = companyDao.findById( Company.class, companyId );
        if ( company == null ) {
            throw new InvalidInputException( "No company present for the specified companyId" );
        }
        company.setCompany( companyName );
        company.setModifiedBy( String.valueOf( userId ) );
        company.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        companyDao.update( company );

        LOG.debug( "Successfully completed method to update company name" );
    }


    @Override
    @Transactional
    public void updateRegionName( long userId, long regionId, String regionName ) throws InvalidInputException
    {
        LOG.debug( "Method updateRegionName of profileManagementService called for regionId : " + regionId );

        Region region = regionDao.findById( Region.class, regionId );
        if ( region == null ) {
            throw new InvalidInputException( "No region present for the specified regionId" );
        }
        region.setRegion( regionName );
        region.setModifiedBy( String.valueOf( userId ) );
        region.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        regionDao.update( region );

        LOG.debug( "Successfully completed method to update region name" );
    }


    @Override
    @Transactional
    public void updateBranchName( long userId, long branchId, String branchName ) throws InvalidInputException
    {
        LOG.debug( "Method updateBranchName of profileManagementService called for branchId : " + branchId );

        Branch branch = branchDao.findById( Branch.class, branchId );
        if ( branch == null ) {
            throw new InvalidInputException( "No branch present for the specified branchId" );
        }
        branch.setBranch( branchName );
        branch.setModifiedBy( String.valueOf( userId ) );
        branch.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        branchDao.update( branch );

        LOG.debug( "Successfully completed method to update branch name" );
    }


    @Override
    @Transactional
    public void updateIndividualName( long userId, long individualId, String individualName ) throws InvalidInputException
    {
        LOG.debug( "Method updateIndividualName of profileManagementService called for individualId : " + individualId );

        User user = userDao.findById( User.class, individualId );
        if ( user == null ) {
            throw new InvalidInputException( "No user present for the specified individualId" );
        }
        String nameArray[] = null;
        if ( individualName != null && !individualName.equalsIgnoreCase( "" ) ) {
            nameArray = individualName.split( " " );
        }

        if ( nameArray == null ) {
            throw new InvalidInputException( "Invalid name, please provide a valid name " );
        }

        user.setFirstName( nameArray[0] );
        String lastName = "";
        if ( nameArray.length > 1 ) {
            for ( int i = 1; i <= nameArray.length - 1; i++ ) {
                lastName += nameArray[i] + " ";
            }
        }
        if ( lastName != null && !lastName.equalsIgnoreCase( "" ) ) {
            lastName = lastName.trim();
            user.setLastName( lastName );
        } else {
            // Fix for SS-1442 : Last name is not updated to blank when updated agent name contains only first name
            user.setLastName( "" );
        }
        user.setModifiedBy( String.valueOf( userId ) );
        user.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        userDao.update( user );

        LOG.debug( "Successfully completed method to update individual name" );
    }


    @Override
    @Transactional
    public void updateCompanyEmail( long companyId, String emailId ) throws NonFatalException
    {
        LOG.debug( "Method updateCompanyEmail of profileManagementService called for companyId : " + companyId );

        Company company = companyDao.findById( Company.class, companyId );
        if ( company == null ) {
            throw new InvalidInputException( "No company present for the specified companyId" );
        }

        User companyAdmin = null;
        // fetch company admin from users table
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.IS_OWNER_COLUMN, CommonConstants.IS_OWNER );
        queries.put( CommonConstants.COMPANY_COLUMN, company );
        List<User> users = userDao.findByKeyValue( User.class, queries );
        if ( users != null && users.size() > 0 ) {
            companyAdmin = users.get( CommonConstants.INITIAL_INDEX );
        }
        if ( companyAdmin != null ) {
            // Fix for JIRA: SS-1198: Updating of email addres should change the login id as well:
            // BEGIN
            companyAdmin.setLoginName( emailId );
            // Fix for JIRA: SS-1198: Updating of email addres should change the login id as well:
            // END
            companyAdmin.setEmailId( emailId );
            companyAdmin.setModifiedBy( String.valueOf( companyAdmin.getUserId() ) );
            companyAdmin.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            userDao.update( companyAdmin );

            for ( UserProfile userProfile : companyAdmin.getUserProfiles() ) {
                userProfile.setEmailId( emailId );
                userProfile.setModifiedBy( String.valueOf( companyAdmin.getUserId() ) );
                userProfile.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
                userProfileDao.update( userProfile );
            }

            company.setModifiedBy( String.valueOf( companyAdmin.getUserId() ) );
            company.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            companyDao.update( company );

            // Fix for JIRA: SS-1358 - Updating email address should update SOLR records as well
            // BEGIN
            updateEmailIdInSolr( emailId, companyAdmin.getUserId() );
            // Fix for JIRA: SS-1358 - Updating email address should update SOLR records as well
            // END

            LOG.debug( "Successfully completed method to update company email" );
        } else {
            LOG.error( "Could not find the owner of the company" );
        }
    }


    @Override
    @Transactional
    public void updateIndividualEmail( long userId, String emailId ) throws InvalidInputException
    {
        LOG.debug( "Method updateIndividualEmail of profileManagementService called for userId : " + userId );

        User user = userDao.findById( User.class, userId );
        if ( user == null ) {
            throw new InvalidInputException( "No user present for the specified userId" );
        }
        user.setEmailId( emailId );
        user.setModifiedBy( String.valueOf( userId ) );
        user.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        userDao.update( user );

        for ( UserProfile userProfile : user.getUserProfiles() ) {
            userProfile.setEmailId( emailId );
            userProfile.setModifiedBy( String.valueOf( userId ) );
            userProfile.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            userProfileDao.update( userProfile );
        }

        LOG.debug( "Successfully completed method to update individual email" );
    }


    @Override
    public Map<String, String> findNamesfromProfileName( String profileName )
    {
        String nameArray[] = null;
        List<String> list = new ArrayList<String>();
        Map<String, String> map = new HashMap<String, String>();
        String firstName = "";
        String lastName = "";
        if ( profileName.contains( "-" ) ) {
            nameArray = profileName.split( "-" );
            for ( String name : nameArray ) {
                list.add( name );
            }
            if ( isNumeric( list.get( list.size() - 1 ) ) ) {
                list.remove( list.size() - 1 );
            }
        }
        if ( list.size() < 2 ) {
            firstName = profileName;

        } else if ( list.size() == 2 ) {
            firstName = list.get( 0 );
            lastName = list.get( 1 );
        } else {
            for ( int i = 0; i < list.size() - 1; i++ ) {
                firstName = firstName + list.get( i ) + " ";
            }
            firstName = firstName.trim();
            lastName = list.get( list.size() - 1 );
        }

        map.put( CommonConstants.PATTERN_FIRST, firstName );
        map.put( CommonConstants.PATTERN_LAST, lastName );
        return map;
    }


    static boolean isNumeric( String str )
    {
        try {
            Double.parseDouble( str );
        } catch ( NumberFormatException nfe ) {
            return false;
        }
        return true;
    }


    @Override
    public OrganizationUnitSettings aggregateAgentDetails( User user, OrganizationUnitSettings profileSettings,
        LockSettings parentLockSettings ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method aggregateAgentDetails() called from ProfileManagementService" );
        if ( profileSettings == null ) {
            throw new InvalidInputException( "No aggregated Settings found" );
        }

        String logoUrl = "";
        OrganizationUnitSettings entitySettings = null;
        ContactDetailsSettings contactDetails = null;
        AgentSettings agentSettings = null;
        if ( profileSettings instanceof AgentSettings ) {
            agentSettings = (AgentSettings) profileSettings;
        }

        // checking all assigned branches for address
        for ( UserProfile userProfile : user.getUserProfiles() ) {
            if ( userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID
                && userProfile.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                // get the branch profile if it is not present in the branch settings
                if ( userProfile.getBranchId() > 0l ) {
                    Branch branch = userManagementService.getBranchById( userProfile.getBranchId() );
                    if ( branch.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_YES )
                        break;
                    entitySettings = organizationManagementService.getBranchSettingsDefault( userProfile.getBranchId() );
                    contactDetails = entitySettings.getContact_details();
                    if ( contactDetails != null && contactDetails.getAddress1() != null ) {
                        if ( !parentLockSettings.getIsLogoLocked() && entitySettings.getLogoThumbnail() != null
                            && !entitySettings.getLogoThumbnail().isEmpty() ) {
                            logoUrl = entitySettings.getLogoThumbnail();
                        }
                        break;
                    }
                }
            }
        }

        // check logo url in region of branch
        if ( !parentLockSettings.getIsLogoLocked() && entitySettings != null && contactDetails != null ) {
            if ( logoUrl == null || logoUrl.isEmpty() ) {
                Branch branch = branchDao.findById( Branch.class, entitySettings.getIden() );
                if ( branch.getRegion().getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_NO ) {
                    OrganizationUnitSettings regionSettings = organizationManagementService
                        .getRegionSettings( branch.getRegion().getRegionId() );
                    if ( regionSettings.getLogoThumbnail() != null && !regionSettings.getLogoThumbnail().isEmpty() ) {
                        logoUrl = regionSettings.getLogoThumbnail();
                    }
                }
            }
        }

        // checking all company for address if null
        if ( contactDetails == null ) {
            entitySettings = organizationManagementService.getCompanySettings( user );
            contactDetails = entitySettings.getContact_details();
        }
        if ( !parentLockSettings.getIsLogoLocked() && ( logoUrl == null || logoUrl.isEmpty() ) ) {
            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
            //JIRA SS-1363 begin
            /*if ( companySettings.getLogoThumbnail() != null && !companySettings.getLogoThumbnail().isEmpty() ) {
                logoUrl = companySettings.getLogoThumbnail();
            }*/
            if ( companySettings.getLogo() != null && !companySettings.getLogo().isEmpty() ) {
                logoUrl = companySettings.getLogo();
            }
            //JIRA SS-1363 end
        }

        // add the company profile data into agent settings
        CompanyProfileData companyProfileData = new CompanyProfileData();
        companyProfileData.setName( contactDetails.getName() );
        companyProfileData.setAddress1( contactDetails.getAddress1() );
        companyProfileData.setAddress2( contactDetails.getAddress2() );
        companyProfileData.setCity( contactDetails.getCity() );
        companyProfileData.setState( contactDetails.getState() );
        companyProfileData.setCountry( contactDetails.getCountry() );
        companyProfileData.setCountryCode( contactDetails.getCountryCode() );
        companyProfileData.setZipcode( contactDetails.getZipcode() );
        companyProfileData.setCompanyLogo( logoUrl );

        if ( agentSettings != null ) {
            if ( !parentLockSettings.getIsLogoLocked() && logoUrl != null && !logoUrl.isEmpty()
                && ( agentSettings.getLogo() == null || agentSettings.getLogo().isEmpty() ) ) {
                agentSettings.setLogo( logoUrl );
            }
            agentSettings.setCompanyProfileData( companyProfileData );
        }

        LOG.debug( "Method aggregateAgentDetails() finished from ProfileManagementService" );
        return ( agentSettings != null ? agentSettings : profileSettings );
    }


    @Override
    public void addOrUpdateAgentPositions( List<CompanyPositions> companyPositions, AgentSettings agentSettings )
    {
        LOG.debug( "Method addOrUpdateAgentPositions() called to update agent positions" );

        organizationUnitSettingsDao.updateParticularKeyAgentSettings( MongoOrganizationUnitSettingDaoImpl.KEY_POSTIONS,
            companyPositions, agentSettings );
    }


    @Override
    @Transactional
    public Map<String, Long> getHierarchyDetailsByEntity( String entityType, long entityId )
        throws InvalidInputException, ProfileNotFoundException
    {
        Map<String, Long> hierarchyDetials = new HashMap<String, Long>();
        Map<String, Long> hierarchyMap = new HashMap<String, Long>();
        long companyId = 0;
        long regionId = 0;
        long branchId = 0;
        long agentId = 0;

        if ( entityType.equalsIgnoreCase( CommonConstants.COMPANY_ID ) ) {
            companyId = entityId;
        } else if ( entityType.equalsIgnoreCase( CommonConstants.REGION_ID ) ) {
            Company company = organizationManagementService.getPrimaryCompanyByRegion( entityId );
            if ( company == null ) {
                throw new InvalidInputException( "Company not found for this region " );
            }
            companyId = company.getCompanyId();
            regionId = entityId;

        } else if ( entityType.equalsIgnoreCase( CommonConstants.BRANCH_ID ) ) {
            Region region = organizationManagementService.getPrimaryRegionByBranch( entityId );
            if ( region == null ) {
                throw new InvalidInputException( "Region not found for this branch " );
            }
            Company company = region.getCompany();
            if ( company == null ) {
                throw new InvalidInputException( "Company not found for this region " );
            }
            companyId = company.getCompanyId();
            regionId = region.getRegionId();
            branchId = entityId;
        } else if ( entityType.equalsIgnoreCase( CommonConstants.AGENT_ID ) ) {
            hierarchyMap = userManagementService.getPrimaryUserProfileByAgentId( entityId );
            agentId = entityId;
            companyId = hierarchyMap.get( CommonConstants.COMPANY_ID_COLUMN );
            regionId = hierarchyMap.get( CommonConstants.REGION_ID_COLUMN );
            branchId = hierarchyMap.get( CommonConstants.BRANCH_ID_COLUMN );
        } else {
            throw new InvalidInputException( "Entity Type Is Invalid " );
        }
        hierarchyDetials.put( CommonConstants.COMPANY_ID_COLUMN, companyId );
        hierarchyDetials.put( CommonConstants.REGION_ID_COLUMN, regionId );
        hierarchyDetials.put( CommonConstants.BRANCH_ID_COLUMN, branchId );
        hierarchyDetials.put( CommonConstants.AGENT_ID_COLUMN, agentId );
        return hierarchyDetials;
    }


    @Override
    @Transactional
    public Map<SettingsForApplication, OrganizationUnit> getPrimaryHierarchyByEntity( String entityType, long entityId )
        throws InvalidInputException, InvalidSettingsStateException, ProfileNotFoundException
    {
        boolean logoLocked = true;
        boolean webAddressLocked = true;
        boolean phoneNumberLocked = true;
        AgentSettings unitSettings = null;
        LOG.debug( "Inside method getPrimaryHeirarchyByEntity for entity " + entityType );
        Map<String, Long> hierarchyDetails = getHierarchyDetailsByEntity( entityType, entityId );
        long companyId = hierarchyDetails.get( CommonConstants.COMPANY_ID_COLUMN );
        long regionId = hierarchyDetails.get( CommonConstants.REGION_ID_COLUMN );
        long branchId = hierarchyDetails.get( CommonConstants.BRANCH_ID_COLUMN );
        List<SettingsDetails> settingsDetailsList = settingsManager.getScoreForCompleteHeirarchy( companyId, branchId,
            regionId );

        LOG.debug( "Calculate lock and setting score " );
        Map<String, BigInteger> totalScore = settingsManager.calculateSettingsScore( settingsDetailsList );
        BigInteger currentLockAggregateValue = totalScore.get( CommonConstants.LOCK_SCORE );
        BigInteger currentSetAggregateValue = totalScore.get( CommonConstants.SETTING_SCORE );

        if ( entityType.equalsIgnoreCase( CommonConstants.AGENT_ID ) ) {
            if ( !settingsLocker.isSettingsValueLocked( OrganizationUnit.COMPANY, currentLockAggregateValue,
                SettingsForApplication.LOGO ) ) {
                if ( !settingsLocker.isSettingsValueLocked( OrganizationUnit.REGION, currentLockAggregateValue,
                    SettingsForApplication.LOGO ) ) {
                    if ( !settingsLocker.isSettingsValueLocked( OrganizationUnit.BRANCH, currentLockAggregateValue,
                        SettingsForApplication.LOGO ) ) {
                        logoLocked = false;
                    }
                }
            }
            if ( !settingsLocker.isSettingsValueLocked( OrganizationUnit.COMPANY, currentLockAggregateValue,
                SettingsForApplication.PHONE ) ) {
                if ( !settingsLocker.isSettingsValueLocked( OrganizationUnit.REGION, currentLockAggregateValue,
                    SettingsForApplication.PHONE ) ) {
                    if ( !settingsLocker.isSettingsValueLocked( OrganizationUnit.BRANCH, currentLockAggregateValue,
                        SettingsForApplication.PHONE ) ) {
                        phoneNumberLocked = false;
                    }
                }
            }
            if ( !settingsLocker.isSettingsValueLocked( OrganizationUnit.COMPANY, currentLockAggregateValue,
                SettingsForApplication.WEB_ADDRESS_WORK ) ) {
                if ( !settingsLocker.isSettingsValueLocked( OrganizationUnit.REGION, currentLockAggregateValue,
                    SettingsForApplication.WEB_ADDRESS_WORK ) ) {
                    if ( !settingsLocker.isSettingsValueLocked( OrganizationUnit.BRANCH, currentLockAggregateValue,
                        SettingsForApplication.WEB_ADDRESS_WORK ) ) {
                        webAddressLocked = false;
                    }
                }
            }
            unitSettings = userManagementService.getAgentSettingsForUserProfiles( entityId );
            if ( unitSettings == null ) {
                LOG.error( "unit settings is null" );
            }
        }


        //check if work email is locked by company
        if ( settingsLocker.isSettingsValueLocked( OrganizationUnit.COMPANY, currentLockAggregateValue,
            SettingsForApplication.EMAIL_ID_WORK ) ) {
            //update currentLockAggregateValue
            String sCurrentLockValue = String.valueOf( currentLockAggregateValue );
            if ( sCurrentLockValue.length() >= SettingsForApplication.EMAIL_ID_WORK.getIndex() ) {
                String preIndexLockValueSubString = sCurrentLockValue.substring( 0,
                    sCurrentLockValue.length() - SettingsForApplication.EMAIL_ID_WORK.getIndex() );
                String indexLockValueSubString = String.valueOf( CommonConstants.LOCKED_BY_NONE );
                String postIndexLockValueSubString = sCurrentLockValue
                    .substring( sCurrentLockValue.length() - SettingsForApplication.EMAIL_ID_WORK.getIndex() + 1 );

                sCurrentLockValue = preIndexLockValueSubString + indexLockValueSubString + postIndexLockValueSubString;
                currentLockAggregateValue = new BigInteger(sCurrentLockValue) ;

            }

        }


        Map<SettingsForApplication, OrganizationUnit> closestSettings = settingsManager
            .getClosestSettingLevel( String.valueOf( currentSetAggregateValue ), String.valueOf( currentLockAggregateValue ) );


        if ( entityType.equalsIgnoreCase( CommonConstants.AGENT_ID ) ) {
            if ( unitSettings != null ) {
                if ( !logoLocked ) {
                    if ( unitSettings.getLogo() != null && !unitSettings.getLogo().isEmpty() ) {
                        closestSettings.put( SettingsForApplication.LOGO, OrganizationUnit.AGENT );
                    }
                }
                if ( !webAddressLocked ) {
                    if ( unitSettings.getContact_details() != null ) {
                        if ( unitSettings.getContact_details().getWeb_addresses() != null ) {
                            if ( unitSettings.getContact_details().getWeb_addresses().getWork() != null
                                && !unitSettings.getContact_details().getWeb_addresses().getWork().isEmpty() ) {
                                closestSettings.put( SettingsForApplication.WEB_ADDRESS_WORK, OrganizationUnit.AGENT );
                            }
                        }
                    }
                }
                if ( !phoneNumberLocked ) {
                    if ( unitSettings.getContact_details() != null ) {
                        if ( unitSettings.getContact_details().getContact_numbers() != null ) {
                            if ( unitSettings.getContact_details().getContact_numbers().getWork() != null
                                && !unitSettings.getContact_details().getContact_numbers().getWork().isEmpty() ) {
                                closestSettings.put( SettingsForApplication.PHONE, OrganizationUnit.AGENT );
                            }
                        }
                    }
                }

                if ( unitSettings.getContact_details() != null ) {
                    if ( ( unitSettings.getContact_details().getAddress1() != null
                        && !unitSettings.getContact_details().getAddress1().isEmpty() )
                        || ( unitSettings.getContact_details().getAddress2() != null
                            && !unitSettings.getContact_details().getAddress2().isEmpty() ) ) {
                        closestSettings.put( SettingsForApplication.ADDRESS, OrganizationUnit.AGENT );
                    }
                }
                if ( unitSettings.getContact_details() != null ) {
                    if ( unitSettings.getContact_details().getLocation() != null
                        && !unitSettings.getContact_details().getLocation().isEmpty() ) {
                        closestSettings.put( SettingsForApplication.LOCATION, OrganizationUnit.AGENT );
                    }
                }

                if ( unitSettings.getContact_details() != null ) {
                    if ( unitSettings.getContact_details().getAbout_me() != null
                        && !unitSettings.getContact_details().getAbout_me().isEmpty() ) {
                        closestSettings.put( SettingsForApplication.ABOUT_ME, OrganizationUnit.AGENT );
                    }
                }

                if ( unitSettings.getContact_details() != null ) {
                    if ( unitSettings.getContact_details().getMail_ids() != null ) {
                        if ( unitSettings.getContact_details().getMail_ids().getWork() != null
                            && !unitSettings.getContact_details().getMail_ids().getWork().isEmpty() ) {
                            closestSettings.put( SettingsForApplication.EMAIL_ID_WORK, OrganizationUnit.AGENT );
                        }
                    }
                }

                if ( unitSettings.getContact_details() != null ) {
                    if ( unitSettings.getContact_details().getMail_ids() != null ) {
                        if ( unitSettings.getContact_details().getMail_ids().getPersonal() != null
                            && !unitSettings.getContact_details().getMail_ids().getPersonal().isEmpty() ) {
                            closestSettings.put( SettingsForApplication.EMAIL_ID_PERSONAL, OrganizationUnit.AGENT );
                        }
                    }
                }

                if ( unitSettings.getSocialMediaTokens() != null ) {
                    if ( unitSettings.getSocialMediaTokens().getFacebookToken() != null ) {
                        closestSettings.put( SettingsForApplication.FACEBOOK, OrganizationUnit.AGENT );
                    }
                }
                if ( unitSettings.getSocialMediaTokens() != null ) {
                    if ( unitSettings.getSocialMediaTokens().getTwitterToken() != null ) {
                        closestSettings.put( SettingsForApplication.TWITTER, OrganizationUnit.AGENT );
                    }
                }
                if ( unitSettings.getSocialMediaTokens() != null ) {
                    if ( unitSettings.getSocialMediaTokens().getLinkedInToken() != null ) {
                        closestSettings.put( SettingsForApplication.LINKED_IN, OrganizationUnit.AGENT );
                    }
                }
                if ( unitSettings.getSocialMediaTokens() != null ) {
                    if ( unitSettings.getSocialMediaTokens().getGoogleToken() != null ) {
                        closestSettings.put( SettingsForApplication.GOOGLE_PLUS, OrganizationUnit.AGENT );
                    }
                }
                if ( unitSettings.getSocialMediaTokens() != null ) {
                    if ( unitSettings.getSocialMediaTokens().getYelpToken() != null ) {
                        closestSettings.put( SettingsForApplication.YELP, OrganizationUnit.AGENT );
                    }
                }
                if ( unitSettings.getSocialMediaTokens() != null ) {
                    if ( unitSettings.getSocialMediaTokens().getZillowToken() != null ) {
                        closestSettings.put( SettingsForApplication.ZILLOW, OrganizationUnit.AGENT );
                    }
                }
                if ( unitSettings.getSocialMediaTokens() != null ) {
                    if ( unitSettings.getSocialMediaTokens().getRealtorToken() != null ) {
                        closestSettings.put( SettingsForApplication.REALTOR, OrganizationUnit.AGENT );
                    }
                }
                if ( unitSettings.getSocialMediaTokens() != null ) {
                    if ( unitSettings.getSocialMediaTokens().getLendingTreeToken() != null ) {
                        closestSettings.put( SettingsForApplication.LENDING_TREE, OrganizationUnit.AGENT );
                    }
                }
                if ( unitSettings.getSocialMediaTokens() != null ) {
                    if ( unitSettings.getSocialMediaTokens().getGoogleBusinessToken() != null ) {
                        closestSettings.put( SettingsForApplication.GOOGLE_BUSINESS, OrganizationUnit.AGENT );
                    }
                }

            }
        }
        return closestSettings;

    }


    @Override
    @Transactional
    public OrganizationUnitSettings getRegionSettingsByProfileName( String companyProfileName, String regionProfileName )
        throws ProfileNotFoundException, InvalidInputException
    {
        LOG.debug( "Method getRegionByProfileName called for companyProfileName:" + companyProfileName
            + " and regionProfileName:" + regionProfileName );
        if ( regionProfileName == null || regionProfileName.isEmpty() ) {
            throw new ProfileNotFoundException( "regionProfileName is null or empty in getRegionByProfileName" );
        }
        /**
         * generate profileUrl and fetch the region by profileUrl since profileUrl for any region is
         * unique, whereas profileName is unique only within a company
         */
        String profileUrl = utils.generateRegionProfileUrl( companyProfileName, regionProfileName );

        OrganizationUnitSettings regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileUrl(
            profileUrl, MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );

        return regionSettings;
    }


    @Override
    @Transactional
    public OrganizationUnitSettings getBranchSettingsByProfileName( String companyProfileName, String branchProfileName )
        throws ProfileNotFoundException, InvalidInputException
    {
        LOG.debug( "Method getBranchSettingsByProfileName called for companyProfileName:" + companyProfileName
            + " and branchProfileName:" + branchProfileName );

        OrganizationUnitSettings companySettings = getCompanyProfileByProfileName( companyProfileName );
        if ( companySettings == null ) {
            LOG.error( "Unable to fetch company settings, invalid input provided by the user" );
            throw new ProfileNotFoundException( "Unable to get company settings " );
        }

        /**
         * generate profileUrl and fetch the branch by profileUrl since profileUrl for any branch is
         * unique, whereas profileName is unique only within a company
         */
        String profileUrl = utils.generateBranchProfileUrl( companyProfileName, branchProfileName );
        OrganizationUnitSettings branchSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileUrl(
            profileUrl, MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );

        if ( branchSettings == null ) {
            LOG.error( "Unable to fetch branch settings, invalid input provided by the user" );
            throw new ProfileNotFoundException( "Unable to get branch settings " );
        }

        return branchSettings;
    }


    @Override
    @Transactional
    public OrganizationUnitSettings getRegionProfileByBranch( OrganizationUnitSettings branchSettings )
        throws ProfileNotFoundException
    {

        LOG.debug( "Fetching branch from db to identify the region" );
        Branch branch = branchDao.findById( Branch.class, branchSettings.getIden() );
        if ( branch == null ) {
            LOG.error( "Unable to get branch with this iden " + branchSettings.getIden() );
            throw new ProfileNotFoundException( "Unable to get branch with this iden " + branchSettings.getIden() );

        }

        OrganizationUnitSettings regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
            branch.getRegion().getRegionId(), MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
        return regionSettings;
    }


    @Override
    public OrganizationUnitSettings fillUnitSettings( OrganizationUnitSettings unitSettings, String currentProfileName,
        OrganizationUnitSettings companyUnitSettings, OrganizationUnitSettings regionUnitSettings,
        OrganizationUnitSettings branchUnitSettings, OrganizationUnitSettings agentUnitSettings,
        Map<SettingsForApplication, OrganizationUnit> map, boolean isFetchRequiredDataFromHierarchy )
    {

        if ( currentProfileName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
            companyUnitSettings = unitSettings;
            return companyUnitSettings;
        } else if ( currentProfileName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {
            regionUnitSettings = unitSettings;
            regionUnitSettings = setAggregateBasicData( regionUnitSettings, companyUnitSettings );
            regionUnitSettings = setAggregateProfileData( regionUnitSettings, companyUnitSettings, regionUnitSettings, null,
                null, map, isFetchRequiredDataFromHierarchy );
            return regionUnitSettings;
        } else if ( currentProfileName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
            branchUnitSettings = setAggregateBasicData( branchUnitSettings, regionUnitSettings );
            branchUnitSettings = setAggregateProfileData( branchUnitSettings, companyUnitSettings, regionUnitSettings,
                branchUnitSettings, null, map, isFetchRequiredDataFromHierarchy );
            return branchUnitSettings;
        } else if ( currentProfileName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
            agentUnitSettings = setAggregateProfileData( agentUnitSettings, companyUnitSettings, regionUnitSettings,
                branchUnitSettings, agentUnitSettings, map, isFetchRequiredDataFromHierarchy );
            return agentUnitSettings;
        } else {
            return null;
        }

    }


    OrganizationUnitSettings setAggregateBasicData( OrganizationUnitSettings userProfile,
        OrganizationUnitSettings parentProfile )
    {

        if ( userProfile.getContact_details() == null ) {
            userProfile.setContact_details( new ContactDetailsSettings() );
        }
        if ( userProfile.getContact_details().getWeb_addresses() == null ) {
            userProfile.getContact_details().setWeb_addresses( new WebAddressSettings() );
        }
        if ( userProfile.getContact_details().getContact_numbers() == null ) {
            userProfile.getContact_details().setContact_numbers( new ContactNumberSettings() );
        }
        if ( userProfile.getSurvey_settings() == null ) {
            userProfile.setSurvey_settings( parentProfile.getSurvey_settings() );
        }
        return userProfile;
    }


    OrganizationUnitSettings setAggregateProfileData( OrganizationUnitSettings userProfile,
        OrganizationUnitSettings companyUnitSettings, OrganizationUnitSettings regionUnitSettings,
        OrganizationUnitSettings branchUnitSettings, OrganizationUnitSettings agentUnitSettings,
        Map<SettingsForApplication, OrganizationUnit> map, boolean isFetchRequiredDataFromHierarchy )
    {
        //Set logoThumbnail along with logo
        for ( Map.Entry<SettingsForApplication, OrganizationUnit> entry : map.entrySet() ) {
            if ( entry.getKey() == SettingsForApplication.LOGO ) {
                //JIRA SS-1363 begin
                /*if ( entry.getValue() == OrganizationUnit.COMPANY ) {
                    userProfile.setLogo( companyUnitSettings.getLogoThumbnail() );
                    userProfile.setLogoThumbnail( companyUnitSettings.getLogoThumbnail() );
                } else if ( entry.getValue() == OrganizationUnit.REGION ) {
                    userProfile.setLogo( regionUnitSettings.getLogoThumbnail() );
                    userProfile.setLogoThumbnail( regionUnitSettings.getLogoThumbnail() );
                } else if ( entry.getValue() == OrganizationUnit.BRANCH ) {
                    userProfile.setLogo( branchUnitSettings.getLogoThumbnail() );
                    userProfile.setLogoThumbnail( branchUnitSettings.getLogoThumbnail() );
                } else if ( entry.getValue() == OrganizationUnit.AGENT ) {
                    userProfile.setLogo( agentUnitSettings.getLogoThumbnail() );
                    userProfile.setLogoThumbnail( agentUnitSettings.getLogoThumbnail() );
                }*/
                if ( entry.getValue() == OrganizationUnit.COMPANY ) {
                    userProfile.setLogo( companyUnitSettings.getLogo() );
                    userProfile.setLogoThumbnail( companyUnitSettings.getLogoThumbnail() );
                } else if ( entry.getValue() == OrganizationUnit.REGION ) {
                    userProfile.setLogo( regionUnitSettings.getLogo() );
                    userProfile.setLogoThumbnail( regionUnitSettings.getLogoThumbnail() );
                } else if ( entry.getValue() == OrganizationUnit.BRANCH ) {
                    userProfile.setLogo( branchUnitSettings.getLogo() );
                    userProfile.setLogoThumbnail( branchUnitSettings.getLogoThumbnail() );
                } else if ( entry.getValue() == OrganizationUnit.AGENT ) {
                    userProfile.setLogo( agentUnitSettings.getLogo() );
                    userProfile.setLogoThumbnail( agentUnitSettings.getLogoThumbnail() );
                }
                //JIRA SS-1363 end

            } else if ( entry.getKey() == SettingsForApplication.LOCATION ) {
                ContactDetailsSettings contactDetails = userProfile.getContact_details();
                if ( contactDetails == null ) {
                    contactDetails = new ContactDetailsSettings();
                }
                if ( entry.getValue() == OrganizationUnit.COMPANY && companyUnitSettings.getContact_details() != null ) {
                    contactDetails.setLocation( companyUnitSettings.getContact_details().getLocation() );
                } else if ( entry.getValue() == OrganizationUnit.REGION && regionUnitSettings.getContact_details() != null ) {
                    contactDetails.setLocation( regionUnitSettings.getContact_details().getLocation() );
                } else if ( entry.getValue() == OrganizationUnit.BRANCH && branchUnitSettings.getContact_details() != null ) {
                    contactDetails.setLocation( branchUnitSettings.getContact_details().getLocation() );
                } else if ( entry.getValue() == OrganizationUnit.AGENT && agentUnitSettings.getContact_details() != null ) {
                    contactDetails.setLocation( agentUnitSettings.getContact_details().getLocation() );
                }
                userProfile.setContact_details( contactDetails );
            } else if ( entry.getKey() == SettingsForApplication.WEB_ADDRESS_WORK ) {
                ContactDetailsSettings contactDetails = userProfile.getContact_details();
                if ( contactDetails == null ) {
                    contactDetails = new ContactDetailsSettings();
                }
                WebAddressSettings webAddressSettings = contactDetails.getWeb_addresses();
                if ( webAddressSettings == null ) {
                    webAddressSettings = new WebAddressSettings();
                }
                if ( entry.getValue() == OrganizationUnit.COMPANY && companyUnitSettings.getContact_details() != null
                    && companyUnitSettings.getContact_details().getWeb_addresses() != null ) {
                    webAddressSettings.setWork( companyUnitSettings.getContact_details().getWeb_addresses().getWork() );
                } else if ( entry.getValue() == OrganizationUnit.REGION && regionUnitSettings.getContact_details() != null
                    && regionUnitSettings.getContact_details().getWeb_addresses() != null ) {
                    webAddressSettings.setWork( regionUnitSettings.getContact_details().getWeb_addresses().getWork() );
                } else if ( entry.getValue() == OrganizationUnit.BRANCH && branchUnitSettings.getContact_details() != null
                    && branchUnitSettings.getContact_details().getWeb_addresses() != null ) {
                    webAddressSettings.setWork( branchUnitSettings.getContact_details().getWeb_addresses().getWork() );
                } else if ( entry.getValue() == OrganizationUnit.AGENT && agentUnitSettings.getContact_details() != null
                    && agentUnitSettings.getContact_details().getWeb_addresses() != null ) {
                    webAddressSettings.setWork( agentUnitSettings.getContact_details().getWeb_addresses().getWork() );
                }
                contactDetails.setWeb_addresses( webAddressSettings );
                userProfile.setContact_details( contactDetails );
            } else if ( entry.getKey() == SettingsForApplication.WEB_ADDRESS_PERSONAL ) {
                ContactDetailsSettings contactDetails = userProfile.getContact_details();
                if ( contactDetails == null ) {
                    contactDetails = new ContactDetailsSettings();
                }
                WebAddressSettings webAddressSettings = contactDetails.getWeb_addresses();
                if ( webAddressSettings == null ) {
                    webAddressSettings = new WebAddressSettings();
                }
                if ( entry.getValue() == OrganizationUnit.COMPANY && companyUnitSettings.getContact_details() != null
                    && companyUnitSettings.getContact_details().getWeb_addresses() != null ) {
                    webAddressSettings.setPersonal( companyUnitSettings.getContact_details().getWeb_addresses().getPersonal() );
                } else if ( entry.getValue() == OrganizationUnit.REGION && regionUnitSettings.getContact_details() != null
                    && regionUnitSettings.getContact_details().getWeb_addresses() != null ) {
                    webAddressSettings.setPersonal( regionUnitSettings.getContact_details().getWeb_addresses().getPersonal() );
                } else if ( entry.getValue() == OrganizationUnit.BRANCH && branchUnitSettings.getContact_details() != null
                    && branchUnitSettings.getContact_details().getWeb_addresses() != null ) {
                    webAddressSettings.setPersonal( branchUnitSettings.getContact_details().getWeb_addresses().getPersonal() );
                } else if ( entry.getValue() == OrganizationUnit.AGENT && agentUnitSettings.getContact_details() != null
                    && agentUnitSettings.getContact_details().getWeb_addresses() != null ) {
                    webAddressSettings.setPersonal( agentUnitSettings.getContact_details().getWeb_addresses().getPersonal() );
                }
                contactDetails.setWeb_addresses( webAddressSettings );
                userProfile.setContact_details( contactDetails );
            } else if ( entry.getKey() == SettingsForApplication.EMAIL_ID_WORK ) {
                ContactDetailsSettings contactDetails = userProfile.getContact_details();
                if ( contactDetails == null ) {
                    contactDetails = new ContactDetailsSettings();
                }
                MailIdSettings mailIdSettings = contactDetails.getMail_ids();
                if ( mailIdSettings == null ) {
                    mailIdSettings = new MailIdSettings();
                }
                if ( entry.getValue() == OrganizationUnit.COMPANY && companyUnitSettings.getContact_details() != null
                    && companyUnitSettings.getContact_details().getMail_ids() != null ) {
                    mailIdSettings.setWork( companyUnitSettings.getContact_details().getMail_ids().getWork() );
                } else if ( entry.getValue() == OrganizationUnit.REGION && regionUnitSettings.getContact_details() != null
                    && regionUnitSettings.getContact_details().getMail_ids() != null ) {
                    mailIdSettings.setWork( regionUnitSettings.getContact_details().getMail_ids().getWork() );
                } else if ( entry.getValue() == OrganizationUnit.BRANCH && branchUnitSettings.getContact_details() != null
                    && branchUnitSettings.getContact_details().getMail_ids() != null ) {
                    mailIdSettings.setWork( branchUnitSettings.getContact_details().getMail_ids().getWork() );
                } else if ( entry.getValue() == OrganizationUnit.AGENT && agentUnitSettings.getContact_details() != null
                    && agentUnitSettings.getContact_details().getMail_ids() != null ) {
                    mailIdSettings.setWork( agentUnitSettings.getContact_details().getMail_ids().getWork() );
                }
                contactDetails.setMail_ids( mailIdSettings );
                userProfile.setContact_details( contactDetails );
            } else if ( entry.getKey() == SettingsForApplication.EMAIL_ID_PERSONAL ) {
                ContactDetailsSettings contactDetails = userProfile.getContact_details();
                if ( contactDetails == null ) {
                    contactDetails = new ContactDetailsSettings();
                }
                MailIdSettings mailIdSettings = contactDetails.getMail_ids();
                if ( mailIdSettings == null ) {
                    mailIdSettings = new MailIdSettings();
                }
                if ( entry.getValue() == OrganizationUnit.COMPANY && companyUnitSettings.getContact_details() != null
                    && companyUnitSettings.getContact_details().getMail_ids() != null ) {
                    mailIdSettings.setPersonal( companyUnitSettings.getContact_details().getMail_ids().getPersonal() );
                } else if ( entry.getValue() == OrganizationUnit.REGION && regionUnitSettings.getContact_details() != null
                    && regionUnitSettings.getContact_details().getMail_ids() != null ) {
                    mailIdSettings.setPersonal( regionUnitSettings.getContact_details().getMail_ids().getPersonal() );
                } else if ( entry.getValue() == OrganizationUnit.BRANCH && branchUnitSettings.getContact_details() != null
                    && branchUnitSettings.getContact_details().getMail_ids() != null ) {
                    mailIdSettings.setPersonal( branchUnitSettings.getContact_details().getMail_ids().getPersonal() );
                } else if ( entry.getValue() == OrganizationUnit.AGENT && agentUnitSettings.getContact_details() != null
                    && agentUnitSettings.getContact_details().getMail_ids() != null ) {
                    mailIdSettings.setPersonal( agentUnitSettings.getContact_details().getMail_ids().getPersonal() );
                }
                contactDetails.setMail_ids( mailIdSettings );
                userProfile.setContact_details( contactDetails );
            } else if ( entry.getKey() == SettingsForApplication.ADDRESS ) {
                ContactDetailsSettings contactDetails = userProfile.getContact_details();
                if ( contactDetails == null ) {
                    contactDetails = new ContactDetailsSettings();
                }
                if ( entry.getValue() == OrganizationUnit.COMPANY && companyUnitSettings.getContact_details() != null ) {
                    contactDetails.setAddress( companyUnitSettings.getContact_details().getAddress() );
                    contactDetails.setAddress1( companyUnitSettings.getContact_details().getAddress1() );
                    contactDetails.setAddress2( companyUnitSettings.getContact_details().getAddress2() );
                    contactDetails.setZipcode( companyUnitSettings.getContact_details().getZipcode() );
                    contactDetails.setState( companyUnitSettings.getContact_details().getState() );
                    contactDetails.setCity( companyUnitSettings.getContact_details().getCity() );
                    contactDetails.setCountry( companyUnitSettings.getContact_details().getCountry() );
                    contactDetails.setCountryCode( companyUnitSettings.getContact_details().getCountryCode() );
                } else if ( entry.getValue() == OrganizationUnit.REGION && regionUnitSettings.getContact_details() != null ) {
                    contactDetails.setAddress( regionUnitSettings.getContact_details().getAddress() );
                    contactDetails.setAddress1( regionUnitSettings.getContact_details().getAddress1() );
                    contactDetails.setAddress2( regionUnitSettings.getContact_details().getAddress2() );
                    contactDetails.setZipcode( regionUnitSettings.getContact_details().getZipcode() );
                    contactDetails.setState( regionUnitSettings.getContact_details().getState() );
                    contactDetails.setCity( regionUnitSettings.getContact_details().getCity() );
                    contactDetails.setCountry( regionUnitSettings.getContact_details().getCountry() );
                    contactDetails.setCountryCode( regionUnitSettings.getContact_details().getCountryCode() );

                } else if ( entry.getValue() == OrganizationUnit.BRANCH && branchUnitSettings.getContact_details() != null ) {
                    contactDetails.setAddress( branchUnitSettings.getContact_details().getAddress() );
                    contactDetails.setAddress1( branchUnitSettings.getContact_details().getAddress1() );
                    contactDetails.setAddress2( branchUnitSettings.getContact_details().getAddress2() );
                    contactDetails.setZipcode( branchUnitSettings.getContact_details().getZipcode() );
                    contactDetails.setState( branchUnitSettings.getContact_details().getState() );
                    contactDetails.setCity( branchUnitSettings.getContact_details().getCity() );
                    contactDetails.setCountry( branchUnitSettings.getContact_details().getCountry() );
                    contactDetails.setCountryCode( branchUnitSettings.getContact_details().getCountryCode() );
                } else if ( entry.getValue() == OrganizationUnit.AGENT && agentUnitSettings.getContact_details() != null ) {
                    contactDetails.setAddress( agentUnitSettings.getContact_details().getAddress() );
                    contactDetails.setAddress1( agentUnitSettings.getContact_details().getAddress1() );
                    contactDetails.setAddress2( agentUnitSettings.getContact_details().getAddress2() );
                    contactDetails.setZipcode( agentUnitSettings.getContact_details().getZipcode() );
                    contactDetails.setState( agentUnitSettings.getContact_details().getState() );
                    contactDetails.setCity( agentUnitSettings.getContact_details().getCity() );
                    contactDetails.setCountry( agentUnitSettings.getContact_details().getCountry() );
                    contactDetails.setCountryCode( agentUnitSettings.getContact_details().getCountryCode() );
                }
                userProfile.setContact_details( contactDetails );
            } else if ( entry.getKey() == SettingsForApplication.PHONE ) {
                ContactDetailsSettings contactDetails = userProfile.getContact_details();
                if ( contactDetails == null ) {
                    contactDetails = new ContactDetailsSettings();
                }
                ContactNumberSettings contactNumberSettings = contactDetails.getContact_numbers();
                if ( contactNumberSettings == null ) {
                    contactNumberSettings = new ContactNumberSettings();
                }
                if ( entry.getValue() == OrganizationUnit.COMPANY && companyUnitSettings.getContact_details() != null
                    && companyUnitSettings.getContact_details().getContact_numbers() != null ) {
                    contactNumberSettings.setWork( companyUnitSettings.getContact_details().getContact_numbers().getWork() );
                    contactNumberSettings
                        .setPhone1( companyUnitSettings.getContact_details().getContact_numbers().getPhone1() );
                } else if ( entry.getValue() == OrganizationUnit.REGION && regionUnitSettings.getContact_details() != null
                    && regionUnitSettings.getContact_details().getContact_numbers() != null ) {
                    contactNumberSettings.setWork( regionUnitSettings.getContact_details().getContact_numbers().getWork() );
                    contactNumberSettings.setPhone1( regionUnitSettings.getContact_details().getContact_numbers().getPhone1() );
                } else if ( entry.getValue() == OrganizationUnit.BRANCH && branchUnitSettings.getContact_details() != null
                    && branchUnitSettings.getContact_details().getContact_numbers() != null ) {
                    contactNumberSettings.setWork( branchUnitSettings.getContact_details().getContact_numbers().getWork() );
                    contactNumberSettings.setPhone1( branchUnitSettings.getContact_details().getContact_numbers().getPhone1() );
                } else if ( entry.getValue() == OrganizationUnit.AGENT && agentUnitSettings.getContact_details() != null
                    && agentUnitSettings.getContact_details().getContact_numbers() != null ) {
                    contactNumberSettings.setWork( agentUnitSettings.getContact_details().getContact_numbers().getWork() );
                    contactNumberSettings.setPhone1( agentUnitSettings.getContact_details().getContact_numbers().getPhone1() );
                }
                contactDetails.setContact_numbers( contactNumberSettings );
                userProfile.setContact_details( contactDetails );
            } else if ( entry.getKey() == SettingsForApplication.FACEBOOK ) {
                SocialMediaTokens socialMediaTokens = userProfile.getSocialMediaTokens();
                if ( socialMediaTokens == null ) {
                    socialMediaTokens = new SocialMediaTokens();
                }
                //get facebook token from upper hierarchy in case of public profile page.
                if ( isFetchRequiredDataFromHierarchy ) {
                    if ( entry.getValue() == OrganizationUnit.COMPANY && companyUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setFacebookToken( companyUnitSettings.getSocialMediaTokens().getFacebookToken() );
                    } else if ( entry.getValue() == OrganizationUnit.REGION
                        && regionUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setFacebookToken( regionUnitSettings.getSocialMediaTokens().getFacebookToken() );
                    } else if ( entry.getValue() == OrganizationUnit.BRANCH
                        && branchUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setFacebookToken( branchUnitSettings.getSocialMediaTokens().getFacebookToken() );
                    } else if ( entry.getValue() == OrganizationUnit.AGENT
                        && agentUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setFacebookToken( agentUnitSettings.getSocialMediaTokens().getFacebookToken() );
                    }
                }
                userProfile.setSocialMediaTokens( socialMediaTokens );
            } else if ( entry.getKey() == SettingsForApplication.GOOGLE_PLUS ) {
                SocialMediaTokens socialMediaTokens = userProfile.getSocialMediaTokens();
                if ( socialMediaTokens == null ) {
                    socialMediaTokens = new SocialMediaTokens();
                }
                //get google plus token from upper hierarchy in case of public profile page.
                if ( isFetchRequiredDataFromHierarchy ) {
                    if ( entry.getValue() == OrganizationUnit.COMPANY && companyUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setGoogleToken( companyUnitSettings.getSocialMediaTokens().getGoogleToken() );
                    } else if ( entry.getValue() == OrganizationUnit.REGION
                        && regionUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setGoogleToken( regionUnitSettings.getSocialMediaTokens().getGoogleToken() );
                    } else if ( entry.getValue() == OrganizationUnit.BRANCH
                        && branchUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setGoogleToken( branchUnitSettings.getSocialMediaTokens().getGoogleToken() );
                    } else if ( entry.getValue() == OrganizationUnit.AGENT
                        && agentUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setGoogleToken( agentUnitSettings.getSocialMediaTokens().getGoogleToken() );
                    }
                }
                userProfile.setSocialMediaTokens( socialMediaTokens );
            } else if ( entry.getKey() == SettingsForApplication.TWITTER ) {
                SocialMediaTokens socialMediaTokens = userProfile.getSocialMediaTokens();
                if ( socialMediaTokens == null ) {
                    socialMediaTokens = new SocialMediaTokens();
                }
                //get twitter token from upper hierarchy in case of public profile page.
                if ( isFetchRequiredDataFromHierarchy ) {
                    if ( entry.getValue() == OrganizationUnit.COMPANY && companyUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setTwitterToken( companyUnitSettings.getSocialMediaTokens().getTwitterToken() );
                    } else if ( entry.getValue() == OrganizationUnit.REGION
                        && regionUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setTwitterToken( regionUnitSettings.getSocialMediaTokens().getTwitterToken() );
                    } else if ( entry.getValue() == OrganizationUnit.BRANCH
                        && branchUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setTwitterToken( branchUnitSettings.getSocialMediaTokens().getTwitterToken() );
                    } else if ( entry.getValue() == OrganizationUnit.AGENT
                        && agentUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setTwitterToken( agentUnitSettings.getSocialMediaTokens().getTwitterToken() );
                    }
                }
                userProfile.setSocialMediaTokens( socialMediaTokens );
            } else if ( entry.getKey() == SettingsForApplication.LINKED_IN ) {
                SocialMediaTokens socialMediaTokens = userProfile.getSocialMediaTokens();
                if ( socialMediaTokens == null ) {
                    socialMediaTokens = new SocialMediaTokens();
                }
                //get twitter token from upper hierarchy in case of public profile page.
                if ( isFetchRequiredDataFromHierarchy ) {
                    if ( entry.getValue() == OrganizationUnit.COMPANY && companyUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setLinkedInToken( companyUnitSettings.getSocialMediaTokens().getLinkedInToken() );
                    } else if ( entry.getValue() == OrganizationUnit.REGION
                        && regionUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setLinkedInToken( regionUnitSettings.getSocialMediaTokens().getLinkedInToken() );
                    } else if ( entry.getValue() == OrganizationUnit.BRANCH
                        && branchUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setLinkedInToken( branchUnitSettings.getSocialMediaTokens().getLinkedInToken() );
                    } else if ( entry.getValue() == OrganizationUnit.AGENT
                        && agentUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setLinkedInToken( agentUnitSettings.getSocialMediaTokens().getLinkedInToken() );
                    }
                }
                userProfile.setSocialMediaTokens( socialMediaTokens );
            } else if ( entry.getKey() == SettingsForApplication.LENDING_TREE ) {
                SocialMediaTokens socialMediaTokens = userProfile.getSocialMediaTokens();
                if ( socialMediaTokens == null ) {
                    socialMediaTokens = new SocialMediaTokens();
                }
                //get lending tree token from upper hierarchy in case of public profile page.
                if ( isFetchRequiredDataFromHierarchy ) {
                    if ( entry.getValue() == OrganizationUnit.COMPANY && companyUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens
                            .setLendingTreeToken( companyUnitSettings.getSocialMediaTokens().getLendingTreeToken() );
                    } else if ( entry.getValue() == OrganizationUnit.REGION
                        && regionUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens
                            .setLendingTreeToken( regionUnitSettings.getSocialMediaTokens().getLendingTreeToken() );
                    } else if ( entry.getValue() == OrganizationUnit.BRANCH
                        && branchUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens
                            .setLendingTreeToken( branchUnitSettings.getSocialMediaTokens().getLendingTreeToken() );
                    } else if ( entry.getValue() == OrganizationUnit.AGENT
                        && agentUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setLendingTreeToken( agentUnitSettings.getSocialMediaTokens().getLendingTreeToken() );
                    }
                }
                userProfile.setSocialMediaTokens( socialMediaTokens );
            } else if ( entry.getKey() == SettingsForApplication.YELP ) {
                SocialMediaTokens socialMediaTokens = userProfile.getSocialMediaTokens();
                if ( socialMediaTokens == null ) {
                    socialMediaTokens = new SocialMediaTokens();
                }
                //get yelp token from upper hierarchy in case of public profile page.
                if ( isFetchRequiredDataFromHierarchy ) {
                    if ( entry.getValue() == OrganizationUnit.COMPANY && companyUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setYelpToken( companyUnitSettings.getSocialMediaTokens().getYelpToken() );
                    } else if ( entry.getValue() == OrganizationUnit.REGION
                        && regionUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setYelpToken( regionUnitSettings.getSocialMediaTokens().getYelpToken() );
                    } else if ( entry.getValue() == OrganizationUnit.BRANCH
                        && branchUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setYelpToken( branchUnitSettings.getSocialMediaTokens().getYelpToken() );
                    } else if ( entry.getValue() == OrganizationUnit.AGENT
                        && agentUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setYelpToken( agentUnitSettings.getSocialMediaTokens().getYelpToken() );
                    }
                }
                userProfile.setSocialMediaTokens( socialMediaTokens );
            } else if ( entry.getKey() == SettingsForApplication.REALTOR ) {
                SocialMediaTokens socialMediaTokens = userProfile.getSocialMediaTokens();
                if ( socialMediaTokens == null ) {
                    socialMediaTokens = new SocialMediaTokens();
                }
                //get realtor token from upper hierarchy in case of public profile page.
                if ( isFetchRequiredDataFromHierarchy ) {
                    if ( entry.getValue() == OrganizationUnit.COMPANY && companyUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setRealtorToken( companyUnitSettings.getSocialMediaTokens().getRealtorToken() );
                    } else if ( entry.getValue() == OrganizationUnit.REGION
                        && regionUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setRealtorToken( regionUnitSettings.getSocialMediaTokens().getRealtorToken() );
                    } else if ( entry.getValue() == OrganizationUnit.BRANCH
                        && branchUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setRealtorToken( branchUnitSettings.getSocialMediaTokens().getRealtorToken() );
                    } else if ( entry.getValue() == OrganizationUnit.AGENT
                        && agentUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setRealtorToken( agentUnitSettings.getSocialMediaTokens().getRealtorToken() );
                    }
                }
                userProfile.setSocialMediaTokens( socialMediaTokens );
            } else if ( entry.getKey() == SettingsForApplication.GOOGLE_BUSINESS ) {
                SocialMediaTokens socialMediaTokens = userProfile.getSocialMediaTokens();
                if ( socialMediaTokens == null ) {
                    socialMediaTokens = new SocialMediaTokens();
                }
                //get google business token from upper hierarchy in case of public profile page.
                if ( isFetchRequiredDataFromHierarchy ) {
                    if ( entry.getValue() == OrganizationUnit.COMPANY && companyUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens
                            .setGoogleBusinessToken( companyUnitSettings.getSocialMediaTokens().getGoogleBusinessToken() );
                    } else if ( entry.getValue() == OrganizationUnit.REGION
                        && regionUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens
                            .setGoogleBusinessToken( regionUnitSettings.getSocialMediaTokens().getGoogleBusinessToken() );
                    } else if ( entry.getValue() == OrganizationUnit.BRANCH
                        && branchUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens
                            .setGoogleBusinessToken( branchUnitSettings.getSocialMediaTokens().getGoogleBusinessToken() );
                    } else if ( entry.getValue() == OrganizationUnit.AGENT
                        && agentUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens
                            .setGoogleBusinessToken( agentUnitSettings.getSocialMediaTokens().getGoogleBusinessToken() );
                    }
                }
                userProfile.setSocialMediaTokens( socialMediaTokens );
            } else if ( entry.getKey() == SettingsForApplication.ZILLOW ) {
                SocialMediaTokens socialMediaTokens = userProfile.getSocialMediaTokens();
                if ( socialMediaTokens == null ) {
                    socialMediaTokens = new SocialMediaTokens();
                }
                //get zillow token from upper hierarchy in case of public profile page.
                if ( isFetchRequiredDataFromHierarchy ) {
                    if ( entry.getValue() == OrganizationUnit.COMPANY && companyUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setZillowToken( companyUnitSettings.getSocialMediaTokens().getZillowToken() );
                    } else if ( entry.getValue() == OrganizationUnit.REGION
                        && regionUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setZillowToken( regionUnitSettings.getSocialMediaTokens().getZillowToken() );
                    } else if ( entry.getValue() == OrganizationUnit.BRANCH
                        && branchUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setZillowToken( branchUnitSettings.getSocialMediaTokens().getZillowToken() );
                    } else if ( entry.getValue() == OrganizationUnit.AGENT
                        && agentUnitSettings.getSocialMediaTokens() != null ) {
                        socialMediaTokens.setZillowToken( agentUnitSettings.getSocialMediaTokens().getZillowToken() );
                    }
                }
                userProfile.setSocialMediaTokens( socialMediaTokens );
            }
        }
        return userProfile;
    }


    public void updateEmailsWithLogo( OrganizationUnitSettings unitSettings, String logoUrl, String collectionName )
    {
        LOG.debug( "Inside method updateEmailsWithLogo for UnitSettings " );
        List<String> paramOrder = null;

        MailContentSettings mailContentSettings = unitSettings.getMail_content();
        if ( mailContentSettings != null ) {
            MailContent takeSurveyMailContent = mailContentSettings.getTake_survey_mail();
            if ( takeSurveyMailContent != null ) {
                String mailBody = takeSurveyMailContent.getMail_body();
                try {
                    mailBody = organizationManagementService
                        .readMailContentFromFile( CommonConstants.SURVEY_REQUEST_MAIL_FILENAME );
                    paramOrder = new ArrayList<String>( Arrays.asList( paramOrderTakeSurvey.split( "," ) ) );
                } catch ( IOException e ) {

                }
                mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody, paramOrder );
                mailBody = mailBody.replaceAll( "\\[LogoUrl\\]", logoUrl );
                takeSurveyMailContent.setMail_body( mailBody );
            }
            mailContentSettings.setTake_survey_mail( takeSurveyMailContent );
            MailContent takeSureyMailCustomerContent = mailContentSettings.getTake_survey_mail_customer();
            if ( takeSureyMailCustomerContent != null ) {
                String mailBody = takeSureyMailCustomerContent.getMail_body();
                try {
                    mailBody = organizationManagementService
                        .readMailContentFromFile( CommonConstants.SURVEY_CUSTOMER_REQUEST_MAIL_FILENAME );
                    paramOrder = new ArrayList<String>( Arrays.asList( paramOrderTakeSurveyCustomer.split( "," ) ) );
                } catch ( IOException e ) {
                }
                mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody, paramOrder );
                mailBody = mailBody.replaceAll( "\\[LogoUrl\\]", logoUrl );
                takeSureyMailCustomerContent.setMail_body( mailBody );
            }
            mailContentSettings.setTake_survey_mail_customer( takeSureyMailCustomerContent );
            MailContent surveyReminderMailContent = mailContentSettings.getTake_survey_reminder_mail();
            if ( surveyReminderMailContent != null ) {
                String mailBody = surveyReminderMailContent.getMail_body();
                try {
                    mailBody = organizationManagementService
                        .readMailContentFromFile( CommonConstants.SURVEY_REMINDER_MAIL_FILENAME );
                    paramOrder = new ArrayList<String>( Arrays.asList( paramOrderTakeSurveyReminder.split( "," ) ) );
                } catch ( IOException e ) {
                }
                mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody, paramOrder );
                mailBody = mailBody.replaceAll( "\\[LogoUrl\\]", logoUrl );
                surveyReminderMailContent.setMail_body( mailBody );
            }
            mailContentSettings.setTake_survey_reminder_mail( surveyReminderMailContent );
            MailContent restartSurveyMailContent = mailContentSettings.getRestart_survey_mail();
            if ( restartSurveyMailContent != null ) {
                String mailBody = restartSurveyMailContent.getMail_body();
                try {
                    mailBody = organizationManagementService
                        .readMailContentFromFile( CommonConstants.SURVEY_REQUEST_MAIL_FILENAME );
                    paramOrder = new ArrayList<String>( Arrays.asList( paramOrderTakeSurvey.split( "," ) ) );
                } catch ( IOException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody, paramOrder );
                mailBody = mailBody.replaceAll( "\\[LogoUrl\\]", logoUrl );
                restartSurveyMailContent.setMail_body( mailBody );
            }
            mailContentSettings.setRestart_survey_mail( restartSurveyMailContent );
        }
        unitSettings.setMail_content( mailContentSettings );
        organizationManagementService.updateMailContentForOrganizationUnit( mailContentSettings, unitSettings, collectionName );
    }


    Map<String, Object> convertJsonStringToMap( String jsonString ) throws JsonParseException, JsonMappingException, IOException
    {
        Map<String, Object> map = new ObjectMapper().readValue( jsonString, new TypeReference<HashMap<String, Object>>() {} );
        return map;
    }


    @SuppressWarnings ( "unchecked")
    List<SurveyDetails> fetchAndSaveZillowFeeds( OrganizationUnitSettings profile, String collectionName, long companyId,
        boolean fromBatch, boolean fromPublicPage ) throws InvalidInputException, UnavailableException
    {
        if ( profile == null )
            throw new InvalidInputException( "Profile setting passed cannot be null" );
        if ( collectionName == null || collectionName.isEmpty() ) {
            throw new InvalidInputException( "Collection name passed cannot be null or empty" );
        }
        LOG.info( "Method to Fetch social feed for " + collectionName + " with iden: " + profile.getIden() + " started" );
        List<SurveyDetails> surveyDetailsList = new ArrayList<SurveyDetails>();
        if ( profile != null && profile.getSocialMediaTokens() != null ) {

            SocialMediaTokens token = profile.getSocialMediaTokens();
            if ( token != null ) {
                if ( token.getZillowToken() != null ) {
                    LOG.info( "Starting to fetch the feed." );
                    String responseString = null;
                    ZillowToken zillowToken = token.getZillowToken();
                    String zillowScreenName = zillowToken.getZillowScreenName();
                    String zillowLenderId = zillowToken.getZillowLenderId();
                    LenderRef zillowLenderRef = zillowToken.getLenderRef();
                    Response response = null;

                    // if nmls found, use it
                    if ( zillowLenderRef != null && zillowLenderRef.getNmlsId() != null ) {
                        LOG.info( "NmlsId found for enity. So getting records from lender API using NmlsId id : "
                            + zillowLenderRef.getNmlsId() + " and screen name : " + zillowScreenName );
                        FetchZillowReviewBodyByNMLS fetchZillowReviewBodyByNMLS = new FetchZillowReviewBodyByNMLS();
                        LenderRef lenderRef = new LenderRef();
                        lenderRef.setNmlsId( zillowLenderRef.getNmlsId() );
                        fetchZillowReviewBodyByNMLS.setLenderRef( lenderRef );
                        fetchZillowReviewBodyByNMLS.setPartnerId( zillowPartnerId );
                        ZillowIntegrationLenderApi zillowIntegrationLenderApi = zillowIntegrationApiBuilder
                            .getZillowIntegrationLenderApi();
                        response = zillowIntegrationLenderApi.fetchZillowReviewsByNMLS( fetchZillowReviewBodyByNMLS );

                        if ( response != null ) {
                            responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
                        }

                        //save to api call details
                        Gson gson = new Gson();
                        String requestBody = gson.toJson( fetchZillowReviewBodyByNMLS );

                        LOG.info( "NMLS id : " + zillowLenderRef.getNmlsId() + " Zillow Data: " + responseString );

                        saveExternalAPICallDetailForZillowLender( requestBody, responseString );

                        if ( responseString != null ) {
                            Map<String, Object> map = null;
                            try {
                                map = convertJsonStringToMap( responseString );
                            } catch ( IOException e ) {
                                LOG.error( "Exception caught while parsing zillow reviews" + e.getMessage() );
                                reportBugOnZillowFetchFail( profile.getProfileName(), zillowScreenName, e );
                                throw new UnavailableException( "Zillow reviews could not be fetched for " + profile.getIden()
                                    + " zillow account " + zillowScreenName );
                            }

                            if ( map != null ) {
                                surveyDetailsList = buildSurveyDetailFromZillowLenderReviewMap( map );
                                LOG.info( "no of records found from zillow is " + surveyDetailsList.size() );
                                surveyDetailsList = fillSurveyDetailsFromReviewMap( surveyDetailsList, collectionName, profile,
                                    companyId, fromBatch, fromPublicPage );

                            }
                        }
                    } else if ( !StringUtils.isEmpty( zillowLenderId ) ) {
                        LOG.info( "LendeId found for enity. So getting records from lender API using lender id : "
                            + zillowLenderId + " and screen name : " + zillowScreenName );
                        FetchZillowReviewBody fetchZillowReviewBody = new FetchZillowReviewBody();
                        fetchZillowReviewBody.setLenderId( zillowLenderId );
                        fetchZillowReviewBody.setPartnerId( zillowPartnerId );
                        ZillowIntegrationLenderApi zillowIntegrationLenderApi = zillowIntegrationApiBuilder
                            .getZillowIntegrationLenderApi();
                        response = zillowIntegrationLenderApi.fetchZillowReviewsByLenderId( fetchZillowReviewBody );

                        if ( response != null ) {
                            responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
                        }

                        //save to api call details
                        Gson gson = new Gson();
                        String requestBody = gson.toJson( fetchZillowReviewBody );
                        saveExternalAPICallDetailForZillowLender( requestBody, responseString );

                        if ( responseString != null ) {
                            Map<String, Object> map = null;
                            try {
                                map = convertJsonStringToMap( responseString );
                            } catch ( IOException e ) {
                                LOG.error( "Exception caught while parsing zillow reviews" + e.getMessage() );
                                reportBugOnZillowFetchFail( profile.getProfileName(), zillowScreenName, e );
                                throw new UnavailableException( "Zillow reviews could not be fetched for " + profile.getIden()
                                    + " zillow account " + zillowScreenName );
                            }

                            if ( map != null ) {
                                surveyDetailsList = buildSurveyDetailFromZillowLenderReviewMap( map );
                                LOG.info( "no of records found from zillow is " + surveyDetailsList.size() );
                                surveyDetailsList = fillSurveyDetailsFromReviewMap( surveyDetailsList, collectionName, profile,
                                    companyId, fromBatch, fromPublicPage );

                            }
                        }
                    } else if ( !StringUtils.isEmpty( zillowScreenName ) ) {
                        try {
                            LOG.info( "LendeId not found for enity. So getting records from screen API using  screen name : "
                                + zillowScreenName );
                            // Replace - with spaces in zillow screen name
                            zillowScreenName = zillowScreenName.replaceAll( "-", " " );
                            ZillowIntegrationAgentApi zillowIntegrationAgentApi = zillowIntegrationApiBuilder
                                .getZillowIntegrationAgentApi();
                            response = zillowIntegrationAgentApi.fetchZillowReviewsByScreennameWithMaxCount( zwsId,
                                zillowScreenName );


                        } catch ( Exception e ) {
                            LOG.error( "Exception caught while fetching zillow reviews" + e.getMessage() );
                            reportBugOnZillowFetchFail( profile.getProfileName(), zillowScreenName, e );
                            throw new UnavailableException( "Zillow reviews could not be fetched for " + profile.getIden()
                                + " zillow account " + zillowScreenName );
                        }


                        if ( response != null ) {
                            responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
                        }

                        //save to api call details
                        saveExternalAPICallDetailForZillowAgent( zillowScreenName, responseString );

                        if ( responseString != null ) {
                            Map<String, Object> map = null;
                            try {
                                map = convertJsonStringToMap( responseString );
                                if ( checkMapForError( map ) ) {
                                    reportBugOnZillowFetchFail( profile.getProfileName(), zillowScreenName,
                                        new Exception( (String) map.get( ZILLOW_JSON_TEXT_KEY ) ) );
                                    // return new ArrayList<SurveyDetails>();
                                }
                            } catch ( IOException e ) {
                                LOG.error( "Exception caught while parsing zillow reviews" + e.getMessage() );
                                reportBugOnZillowFetchFail( profile.getProfileName(), zillowScreenName, e );
                                throw new UnavailableException( "Zillow reviews could not be fetched for " + profile.getIden()
                                    + " zillow account " + zillowScreenName );
                            }

                            if ( map != null ) {
                                //modify zillow call count
                                modifyZillowCallCount( map );
                                surveyDetailsList = buildSurveyDetailFromZillowAgentReviewMap( map );
                                LOG.info( "no of records found from zillow is " + surveyDetailsList.size() );
                                surveyDetailsList = fillSurveyDetailsFromReviewMap( surveyDetailsList, collectionName, profile,
                                    companyId, fromBatch, fromPublicPage );

                            }
                        }

                    } else {
                        LOG.debug( "Old zillow url. Modify and get the proper screen name. But for now bypass and do nothing" );
                        // TODO: Convert to proper format from the old url format
                    }
                }
            }
        } else {
            LOG.error( "No social media token present for " + collectionName + " with iden: " + profile.getIden() );
        }

        LOG.info( "Method to Fetch social feed for " + collectionName + " with iden: " + profile.getIden() + " ended" );

        return surveyDetailsList;
    }


    @Override
    @SuppressWarnings ( "unchecked")
    public List<SurveyDetails> buildSurveyDetailFromZillowLenderReviewMap( Map<String, Object> map )
    {

        List<SurveyDetails> surveyDetailsList = new ArrayList<SurveyDetails>();

        List<HashMap<String, Object>> reviews = new ArrayList<HashMap<String, Object>>();

        reviews = (List<HashMap<String, Object>>) map.get( "reviews" );
        if ( reviews != null ) {
            for ( Map<String, Object> review : reviews ) {
                HashMap<String, Object> individualReviewee = (HashMap<String, Object>) review.get( "individualReviewee" );
                HashMap<String, Object> reviewerName = (HashMap<String, Object>) review.get( "reviewerName" );
                String displayReviewerName = (String) reviewerName.get("displayName");
                String customerFirstName = null;
                if ( ! StringUtils.isEmpty(displayReviewerName) ) {
                		customerFirstName = displayReviewerName;
                } else {
                    customerFirstName = (String) reviewerName.get( "screenName" );
                }


                String profileName = null;
                String zillowProfileUrl = CommonConstants.ZILLOW_PROFILE_URL;
                if ( individualReviewee != null ) {
                    profileName = (String) individualReviewee.get( "screenName" );

                    //SS-1226 : Zillow reviews' social posts are displaying broken link 
                    //empty space replaced by %20 for FaceBook posts
                    if ( profileName != null && profileName.contains( " " ) ) {
                        profileName = profileName.replace( " ", "%20" );
                    }

                    zillowProfileUrl += profileName;
                }


                String sourceId = (String) review.get( "reviewId" );
                String reviewDescription = (String) review.get( "content" );
                String summary = (String) review.get( "title" );
                String createdDateStr = (String) review.get( "created" );
                String dateOfServiceStr = (String) review.get( "dateOfService" );
                String completeProfileUrl = (String) review.get( "reviewerLink" );
                Double score = ( (Integer) review.get( "rating" ) ).doubleValue();
                boolean isAbusive = false;
                Date createdDate = convertStringToDateForZillowLenders( createdDateStr );
                Date dateOfService = convertStringToDateForZillowLenders( dateOfServiceStr );

                SurveyDetails surveyDetails = new SurveyDetails();
                surveyDetails.setCompleteProfileUrl( completeProfileUrl );
                surveyDetails.setCustomerFirstName( customerFirstName );
                surveyDetails.setReview( reviewDescription );
                surveyDetails.setEditable( false );
                surveyDetails.setStage( CommonConstants.SURVEY_STAGE_COMPLETE );
                surveyDetails.setScore( score );
                surveyDetails.setSource( CommonConstants.SURVEY_SOURCE_ZILLOW );
                surveyDetails.setSourceId( zillowProfileUrl );
                surveyDetails.setCompleteProfileUrl( zillowProfileUrl );
                //ModifiedOn set to current date
                Date currentDate = new Date( System.currentTimeMillis() );
                surveyDetails.setModifiedOn( currentDate );
                surveyDetails.setCreatedOn( createdDate );
                surveyDetails.setAgreedToShare( "true" );
                surveyDetails.setAbusive( isAbusive );
                surveyDetails.setAbuseRepByUser( false );
                surveyDetails.setShowSurveyOnUI( true );
                surveyDetails.setSurveyCompletedDate( createdDate );
                surveyDetails.setSurveyUpdatedDate( createdDate );
                surveyDetails.setSurveyTransactionDate( dateOfService );

                // saving zillow review summary
                surveyDetails.setSummary( summary );

                surveyDetailsList.add( surveyDetails );
            }
        }

        return surveyDetailsList;
    }


    @Override
    @SuppressWarnings ( "unchecked")
    public List<SurveyDetails> buildSurveyDetailFromZillowAgentReviewMap( Map<String, Object> map )
    {

        Map<String, Object> responseMap = new HashMap<String, Object>();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Map<String, Object> proReviews = new HashMap<String, Object>();
        Map<String, Object> proInfoMap = new HashMap<String, Object>();
        List<HashMap<String, Object>> reviews = new ArrayList<HashMap<String, Object>>();

        List<SurveyDetails> surveyDetailsList = new ArrayList<SurveyDetails>();

        String profileLink = null;
        responseMap = (HashMap<String, Object>) map.get( "response" );
        if ( responseMap != null ) {
            resultMap = (HashMap<String, Object>) responseMap.get( "results" );
            if ( resultMap != null ) {
                proInfoMap = (HashMap<String, Object>) resultMap.get( "proInfo" );
                if ( proInfoMap != null ) {
                    profileLink = (String) proInfoMap.get( "profileURL" );
                }
                proReviews = (HashMap<String, Object>) resultMap.get( "proReviews" );
                if ( proReviews != null ) {
                    reviews = (List<HashMap<String, Object>>) proReviews.get( "review" );
                    if ( reviews != null ) {
                        for ( Map<String, Object> review : reviews ) {
                            String sourceId = (String) review.get( "reviewURL" );
                            String reviewDescription = (String) review.get( "description" );
                            String summary = (String) review.get( "reviewSummary" );
                            String createdDate = (String) review.get( "reviewDate" );
                            String reviewerProfileUrl = (String) review.get( "reviewerLink" );
                            String customerFirstName = (String) review.get( "reviewer" );
                            Double score = Double.valueOf(
                                review.get( "rating" ).toString().length() > 0 ? (String) review.get( "rating" ) : "0" );
                            boolean isAbusive = false;

                            SurveyDetails surveyDetails = new SurveyDetails();
                            surveyDetails.setCompleteProfileUrl( profileLink );
                            surveyDetails.setCustomerFirstName( customerFirstName );
                            surveyDetails.setReview( reviewDescription );
                            surveyDetails.setEditable( false );
                            surveyDetails.setStage( CommonConstants.SURVEY_STAGE_COMPLETE );
                            surveyDetails.setScore( score );
                            surveyDetails.setSource( CommonConstants.SURVEY_SOURCE_ZILLOW );
                            surveyDetails.setSourceId( sourceId );
                            //ModifiedOn set to current date
                            Date currentDate = new Date( System.currentTimeMillis() );
                            surveyDetails.setModifiedOn( currentDate );
                            surveyDetails.setCreatedOn( convertStringToDate( createdDate ) );
                            surveyDetails.setAgreedToShare( "true" );
                            surveyDetails.setAbusive( isAbusive );
                            surveyDetails.setAbuseRepByUser( false );
                            surveyDetails.setShowSurveyOnUI( true );
                            surveyDetails.setSurveyCompletedDate( convertStringToDate( createdDate ) );
                            surveyDetails.setSurveyUpdatedDate( convertStringToDate( createdDate ) );
                            surveyDetails.setSurveyTransactionDate( convertStringToDate( createdDate ) );

                            // saving zillow review summary
                            surveyDetails.setSummary( summary );

                            surveyDetailsList.add( surveyDetails );
                        }
                    }
                }
            }
        }


        return surveyDetailsList;
    }


    /**
     * 
     * @param map
     */
    @Override
    @SuppressWarnings ( "unchecked")
    public void modifyZillowCallCount( Map<String, Object> map )
    {

        Map<String, Object> messageMap = new HashMap<String, Object>();
        messageMap = (HashMap<String, Object>) map.get( "message" );
        String code = (String) messageMap.get( "code" );
        if ( code.equalsIgnoreCase( "7" ) ) {
            String errorMessage = (String) messageMap.get( "text" );
            int count = socialManagementService.fetchZillowCallCount();
            if ( count != 0 ) {
                LOG.debug( "Zillow API call count exceeded limit. Sending mail to admin." );
                try {
                    emailServices.sendZillowCallExceededMailToAdmin( count );
                    surveyDetailsDao.resetZillowCallCount();
                } catch ( InvalidInputException e ) {
                    LOG.error( "Sending the mail to the admin failed due to invalid input. Reason : ", e );
                } catch ( UndeliveredEmailException e ) {
                    LOG.error( "The email failed to get delivered. Reason : ", e );
                }
            }
            LOG.error( "Error code : " + code + " Error description : " + errorMessage );
        } else if ( !code.equalsIgnoreCase( "0" ) ) {
            String errorMessage = (String) messageMap.get( "text" );
            LOG.error( "Error code : " + code + " Error description : " + errorMessage );
        } else {
            surveyDetailsDao.updateZillowCallCount();
        }

    }


    /**
     * 
     * @param zillowScreenName
     * @param responseString
     * @throws InvalidInputException
     */
    private void saveExternalAPICallDetailForZillowAgent( String zillowScreenName, String responseString )
        throws InvalidInputException
    {

        ExternalAPICallDetails zillowAPICallDetails = new ExternalAPICallDetails();
        zillowAPICallDetails.setHttpMethod( CommonConstants.HTTP_METHOD_GET );
        zillowAPICallDetails.setRequest( zillowAgentApiEndpoint + CommonConstants.ZILLOW_CALL_REQUEST + "&zws-id=" + zwsId
            + "&screenname=" + zillowScreenName );


        zillowAPICallDetails.setResponse( responseString );
        zillowAPICallDetails.setRequestTime( new Date( System.currentTimeMillis() ) );
        zillowAPICallDetails.setSource( CommonConstants.ZILLOW_SOCIAL_SITE );
        //Store this record in mongo
        externalApiCallDetailsDao.insertApiCallDetails( zillowAPICallDetails );
    }


    /**
    * 
    * @param zillowScreenName
    * @param responseString
    * @throws InvalidInputException
    */
    private void saveExternalAPICallDetailForZillowLender( String requestBody, String responseString )
        throws InvalidInputException
    {

        ExternalAPICallDetails zillowAPICallDetails = new ExternalAPICallDetails();
        zillowAPICallDetails.setHttpMethod( CommonConstants.HTTP_METHOD_POST );
        zillowAPICallDetails.setRequest( zillowLenderApiEndpoint + "/getPublishedLenderReviews" );
        zillowAPICallDetails.setRequestBody( requestBody );

        zillowAPICallDetails.setResponse( responseString );
        zillowAPICallDetails.setRequestTime( new Date( System.currentTimeMillis() ) );
        zillowAPICallDetails.setSource( CommonConstants.ZILLOW_SOCIAL_SITE );
        //Store this record in mongo
        externalApiCallDetailsDao.insertApiCallDetails( zillowAPICallDetails );
    }


    @Override
    public Date convertStringToDate( String dateString )
    {

        DateFormat format = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        Date date;
        try {
            date = format.parse( dateString );
        } catch ( ParseException e ) {
            return null;
        }
        return date;
    }


    public Date convertStringToDateForZillowLenders( String dateString )
    {

        DateFormat format = new SimpleDateFormat( "yyyy-MM-dd", Locale.ENGLISH );
        Date date;
        try {
            date = format.parse( dateString );
        } catch ( ParseException e ) {
            return null;
        }
        return date;
    }


    @Override
    public LockSettings fetchHierarchyLockSettings( long companyId, long branchId, long regionId, String entityType )
        throws NonFatalException
    {
        LOG.debug( "Method fetchHierarchyLockSettings() called from ProfileManagementService" );
        boolean logoLocked = true;
        boolean webAddressLocked = true;
        boolean phoneNumberLocked = true;
        boolean workEmailLocked = true;
        List<SettingsDetails> settingsDetailsList = settingsManager.getScoreForCompleteHeirarchy( companyId, branchId,
            regionId );
        Map<String, BigInteger> totalScore = settingsManager.calculateSettingsScore( settingsDetailsList );
        BigInteger currentLockAggregateValue = totalScore.get( CommonConstants.LOCK_SCORE );
        LockSettings parentLock = new LockSettings();

        if ( entityType.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) ) {
            logoLocked = false;
            webAddressLocked = false;
            phoneNumberLocked = false;
            workEmailLocked = false;
        } else if ( entityType.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN ) ) {
            if ( !checkIfSettingLockedByOrganization( OrganizationUnit.COMPANY, SettingsForApplication.LOGO,
                currentLockAggregateValue ) ) {
                logoLocked = false;
            }
            if ( !checkIfSettingLockedByOrganization( OrganizationUnit.COMPANY, SettingsForApplication.WEB_ADDRESS_WORK,
                currentLockAggregateValue ) ) {
                webAddressLocked = false;
            }
            if ( !checkIfSettingLockedByOrganization( OrganizationUnit.COMPANY, SettingsForApplication.PHONE,
                currentLockAggregateValue ) ) {
                phoneNumberLocked = false;
            }
            if ( !checkIfSettingLockedByOrganization( OrganizationUnit.COMPANY, SettingsForApplication.EMAIL_ID_WORK,
                currentLockAggregateValue ) ) {
                workEmailLocked = false;
            }

            //check only for company
            if ( !checkIfSettingLockedByOrganization( OrganizationUnit.COMPANY, SettingsForApplication.EMAIL_ID_WORK,
                currentLockAggregateValue ) ) {
                workEmailLocked = false;
            }
        } else if ( entityType.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN ) ) {

            if ( !checkIfSettingLockedByOrganization( OrganizationUnit.COMPANY, SettingsForApplication.LOGO,
                currentLockAggregateValue ) ) {
                logoLocked = false;
            }
            if ( !checkIfSettingLockedByOrganization( OrganizationUnit.COMPANY, SettingsForApplication.WEB_ADDRESS_WORK,
                currentLockAggregateValue ) ) {
                webAddressLocked = false;
            }
            if ( !checkIfSettingLockedByOrganization( OrganizationUnit.COMPANY, SettingsForApplication.PHONE,
                currentLockAggregateValue ) ) {
                phoneNumberLocked = false;
            }

            if ( !logoLocked ) {
                if ( !checkIfSettingLockedByOrganization( OrganizationUnit.REGION, SettingsForApplication.LOGO,
                    currentLockAggregateValue ) ) {
                    logoLocked = false;
                }
            }
            if ( !webAddressLocked ) {
                if ( !checkIfSettingLockedByOrganization( OrganizationUnit.COMPANY, SettingsForApplication.WEB_ADDRESS_WORK,
                    currentLockAggregateValue ) ) {
                    webAddressLocked = false;
                }
            }
            if ( !phoneNumberLocked ) {
                if ( !checkIfSettingLockedByOrganization( OrganizationUnit.COMPANY, SettingsForApplication.PHONE,
                    currentLockAggregateValue ) ) {
                    phoneNumberLocked = false;
                }
            }

            //check only for company
            if ( !checkIfSettingLockedByOrganization( OrganizationUnit.COMPANY, SettingsForApplication.EMAIL_ID_WORK,
                currentLockAggregateValue ) ) {
                workEmailLocked = false;
            }
        } else if ( entityType.equalsIgnoreCase( CommonConstants.AGENT_ID_COLUMN ) ) {
            if ( !checkIfSettingLockedByOrganization( OrganizationUnit.COMPANY, SettingsForApplication.LOGO,
                currentLockAggregateValue ) ) {
                logoLocked = false;
            }
            if ( !checkIfSettingLockedByOrganization( OrganizationUnit.COMPANY, SettingsForApplication.WEB_ADDRESS_WORK,
                currentLockAggregateValue ) ) {
                webAddressLocked = false;
            }
            if ( !checkIfSettingLockedByOrganization( OrganizationUnit.COMPANY, SettingsForApplication.PHONE,
                currentLockAggregateValue ) ) {
                phoneNumberLocked = false;
            }

            if ( !logoLocked ) {
                if ( !checkIfSettingLockedByOrganization( OrganizationUnit.REGION, SettingsForApplication.LOGO,
                    currentLockAggregateValue ) ) {
                    logoLocked = false;
                }

            }
            if ( !webAddressLocked ) {
                if ( !checkIfSettingLockedByOrganization( OrganizationUnit.REGION, SettingsForApplication.WEB_ADDRESS_WORK,
                    currentLockAggregateValue ) ) {
                    webAddressLocked = false;
                }
            }
            if ( !phoneNumberLocked ) {
                if ( !checkIfSettingLockedByOrganization( OrganizationUnit.REGION, SettingsForApplication.PHONE,
                    currentLockAggregateValue ) ) {
                    phoneNumberLocked = false;
                }
            }


            if ( !logoLocked ) {
                if ( !checkIfSettingLockedByOrganization( OrganizationUnit.BRANCH, SettingsForApplication.LOGO,
                    currentLockAggregateValue ) ) {
                    logoLocked = false;
                }

            }
            if ( !webAddressLocked ) {
                if ( !checkIfSettingLockedByOrganization( OrganizationUnit.BRANCH, SettingsForApplication.WEB_ADDRESS_WORK,
                    currentLockAggregateValue ) ) {
                    webAddressLocked = false;
                }
            }
            if ( !phoneNumberLocked ) {
                if ( !checkIfSettingLockedByOrganization( OrganizationUnit.BRANCH, SettingsForApplication.PHONE,
                    currentLockAggregateValue ) ) {
                    phoneNumberLocked = false;
                }

            }

            //check only if company locked
            if ( !checkIfSettingLockedByOrganization( OrganizationUnit.COMPANY, SettingsForApplication.EMAIL_ID_WORK,
                currentLockAggregateValue ) ) {
                workEmailLocked = false;
            }
        }
        parentLock.setLogoLocked( logoLocked );
        parentLock.setWebAddressLocked( webAddressLocked );
        parentLock.setWorkPhoneLocked( phoneNumberLocked );
        parentLock.setWorkEmailLocked( workEmailLocked );

        return parentLock;
    }


    boolean checkIfSettingLockedByOrganization( OrganizationUnit unit, SettingsForApplication settingsforApplications,
    		BigInteger currentLockValue )
    {
        LOG.debug( "Inside method getLogoLockedByCompany " );
        if ( settingsLocker.isSettingsValueLocked( unit, currentLockValue, settingsforApplications ) ) {
            return true;
        } else {
            return false;
        }
    }


    void updateEmailIdInSolr( String emailId, long iden ) throws NonFatalException
    {
        if ( iden <= 0 ) {
            LOG.error( "Invalid iden passed in updateEmailIdInSolr" );
            throw new InvalidInputException( "Invalid iden passed in updateEmailIdInSolr" );
        }
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "Email id passed cannot be null or empty in updateEmailIdInSolr" );
        }
        LOG.debug( "Updating verified email id info into solr for user id : " + iden );
        Map<String, Object> editKeys = new HashMap<String, Object>();
        editKeys.put( CommonConstants.USER_LOGIN_NAME_SOLR, emailId );
        editKeys.put( CommonConstants.USER_EMAIL_ID_SOLR, emailId );
        try {
            solrSearchService.editUserInSolrWithMultipleValues( iden, editKeys );
        } catch ( SolrException se ) {
            throw new NonFatalException( se.getMessage() );
        }
        LOG.debug( "Updated verified email id info into solr for user id : " + iden );
    }


    /**
     * Method to fetch Zillow data
     * @param profile
     * @param collection
     * @throws InvalidInputException
     * @throws UnavailableException
     * */
    @Override
    public List<SurveyDetails> fetchAndSaveZillowData( OrganizationUnitSettings profile, String collection, long companyId,
        boolean fromBatch, boolean fromPublicPage ) throws InvalidInputException, UnavailableException
    {

        if ( profile == null || collection == null || collection.isEmpty() ) {
            LOG.debug( "Invalid parameters passed to fetchZillowData for fetching zillow feed" );
            throw new InvalidInputException( "Invalid parameters passed to fetchZillowData for fetching zillow feed" );
        }
        LOG.debug( "Method to fetch zillow feed called for ID :" + profile.getIden() + " of collection : " + collection );
        if ( profile.getSocialMediaTokens() != null && profile.getSocialMediaTokens().getZillowToken() != null ) {
            // fetching zillow feed
            LOG.debug( "Fetching zillow feed for " + profile.getId() + " from " + collection );
            List<SurveyDetails> surveyDetailsList = fetchAndSaveZillowFeeds( profile, collection, companyId, fromBatch,
                fromPublicPage );
            LOG.debug( "Method to fetch zillow feed finished." );
            return surveyDetailsList;
        } else {
            LOG.debug( "Zillow is not added for the profile" );
            throw new InvalidInputException( "Zillow is not added for the profile" );
        }
    }


    @Override
    @Transactional
    public Map<String, Long> getZillowTotalScoreAndReviewCountForProfileLevel( String profileLevel, long iden )
    {
        if ( profileLevel == null || profileLevel.isEmpty() ) {
            LOG.error( "column name is null or empty while getting total review count and score for a column name and id" );
            return null;
        }
        if ( iden <= 0l ) {
            LOG.error( "Invalid id passed while getting total review count and score for a column name and id" );
            return null;
        }
        try {
            switch ( profileLevel ) {
                case CommonConstants.PROFILE_LEVEL_COMPANY:
                    return zillowHierarchyDao.getZillowReviewCountAndTotalScoreForAllUnderCompany( iden );
                case CommonConstants.PROFILE_LEVEL_REGION:
                    return zillowHierarchyDao.getZillowReviewCountAndTotalScoreForAllUnderRegion( iden );
                case CommonConstants.PROFILE_LEVEL_BRANCH:
                    return zillowHierarchyDao.getZillowReviewCountAndTotalScoreForAllUnderBranch( iden );
                case CommonConstants.PROFILE_LEVEL_INDIVIDUAL:
                    User user = userDao.findById( User.class, iden );
                    long zillowReviewCount = 0;
                    long zillowTotalScore = 0;
                    if ( user != null && user.getIsZillowConnected() == CommonConstants.YES ) {
                        zillowReviewCount = user.getZillowReviewCount();
                        zillowTotalScore = (long) ( user.getZillowAverageScore() * zillowReviewCount );
                    }
                    Map<String, Long> zillowTotalScoreAndAverageMap = new HashMap<String, Long>();
                    zillowTotalScoreAndAverageMap.put( CommonConstants.ZILLOW_REVIEW_COUNT_COLUMN, zillowReviewCount );
                    zillowTotalScoreAndAverageMap.put( CommonConstants.ZILLOW_TOTAL_SCORE, zillowTotalScore );
                    return zillowTotalScoreAndAverageMap;
                default:
                    LOG.error( "Invalid profile level passed while getting ids under a profile level" );
            }
        } catch ( Exception e ) {
            LOG.error( "Exception occurred while fetching zillow total score and average for profile level and id. Reason : ",
                e );
        }
        return null;
    }


    /**
     * Method to get ids under a unit based on profile level
     * @param unitName
     * @param iden
     * @param exception
     * */
    void reportBugOnZillowFetchFail( String unitName, String zillowScreenName, Exception exception )
    {
        try {
            LOG.debug( "Building error message for the zillow review fetch failure" );
            String errorMsg = "<br>" + exception.getMessage()
                + "<br><br>Error while fetching zillow reviews for a unit/Agent<br>";
            errorMsg += "<br>Social Application : Zillow<br>";
            errorMsg += "<br>Unit/Agent Name : " + unitName + "<br>";
            errorMsg += "<br>Zillow Screen Name : " + zillowScreenName + "<br>";
            errorMsg += "<br>StackTrace : <br>" + ExceptionUtils.getStackTrace( exception ).replaceAll( "\n", "<br>" ) + "<br>";
            LOG.debug( "Error message built for zillow review fetch failure" );
            LOG.debug( "Sending bug mail to admin for zillow review fetch failure" );
            emailServices.sendReportBugMailToAdmin( applicationAdminName, errorMsg, applicationAdminEmail );
            LOG.debug( "Sent bug mail to admin for zillow review fetch failure" );
        } catch ( UndeliveredEmailException ude ) {
            LOG.error( "error while sending report bug mail to admin ", ude );
        } catch ( InvalidInputException iie ) {
            LOG.error( "error while sending report bug mail to admin ", iie );
        }
    }


    boolean checkMapForError( Map<String, Object> map )
    {
        if ( map != null && map.containsKey( ZILLOW_JSON_CODE_KEY ) ) {
            int code = Integer.parseInt( ( String.valueOf( map.get( ZILLOW_JSON_CODE_KEY ) ) ) );
            if ( code > 0 && map.containsKey( ZILLOW_JSON_TEXT_KEY ) && map.get( ZILLOW_JSON_TEXT_KEY ) != null
                && ( (String) map.get( ZILLOW_JSON_TEXT_KEY ) ).startsWith( ZILLOW_JSON_ERROR_TEXT_PREFIX ) ) {
                return true;
            }
        }
        return false;
    }


    @Override
    public List<SurveyDetails> fillSurveyDetailsFromReviewMap( List<SurveyDetails> surveyDetailsList, String collectionName,
        OrganizationUnitSettings profile, long companyId, boolean fromBatch, boolean fromPublicPage )
        throws InvalidInputException
    {
        LOG.info( "Method fillSurveyDetailsFromReviewMap " + collectionName + " with iden: " + profile.getIden() + " started" );
        String idenColumnName = "";
        if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
            idenColumnName = CommonConstants.COMPANY_ID_COLUMN;
        } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {
            idenColumnName = CommonConstants.REGION_ID_COLUMN;
        } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
            idenColumnName = CommonConstants.BRANCH_ID_COLUMN;
        } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
            idenColumnName = CommonConstants.AGENT_ID_COLUMN;
        }
        Map<String, Object> queries = new HashMap<String, Object>();
        List<String> latestSurveyIdList = new ArrayList<String>();
        queries.put( idenColumnName, profile.getIden() );

        // LOG.debug( "Deleting existing reviews for profile type : " + idenColumnName + " and profile id : " + profile.getIden() );
        // surveyHandler.deleteExistingZillowSurveysByEntity( idenColumnName, profile.getIden() );
        // LOG.debug( "Deleted existing reviews for profile type : " + idenColumnName + " and profile id : " + profile.getIden() );
        for ( int i = 0; i < surveyDetailsList.size(); i++ ) {
            SurveyDetails surveyDetails = surveyDetailsList.get( i );

            //            TODO -  need to remove this after fixing zillow issue
            //queries.put( CommonConstants.SURVEY_SOURCE_ID_COLUMN, sourceId );
            queries.put( CommonConstants.REVIEW_COLUMN, surveyDetails.getReview() );

            if ( fromBatch ) {
                utils.checkReviewForSwearWords( surveyDetails.getReview(), surveyHandler.getSwearList() );
            }

            LOG.info( "checking if survey already exist in database with review : " + surveyDetails.getReview() );
            SurveyDetails existingSurveyDetails = surveyDetailsDao.getZillowReviewByQueryMap( queries );
            if ( existingSurveyDetails == null ) {
                LOG.info( "no survey found in database for current review" );
                if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
                    surveyDetails.setCompanyId( profile.getIden() );
                } else if ( collectionName
                    .equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {
                    surveyDetails.setRegionId( profile.getIden() );
                    surveyDetails.setCompanyId( companyId );
                    surveyDetails.setRegionName(  profile.getContact_details().getName()  );
                } else if ( collectionName
                    .equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
                    try {
                        Branch branch = branchDao.findById( Branch.class, profile.getIden() );
                        if ( branch != null ) {
                            surveyDetails.setRegionId( branch.getRegion().getRegionId() );
                            surveyDetails.setRegionName(branch.getRegion().getRegion());
                        }
                    } catch ( Exception e ) {
                        LOG.error( "Could not find by branch details for id : " + profile.getIden(), e );
                    }
                    surveyDetails.setBranchId( profile.getIden() );
                    surveyDetails.setCompanyId( companyId );
                    surveyDetails.setBranchName(  profile.getContact_details().getName()  );
                } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
                    try {
                        Map<String, Long> agentDetailsMap = userProfileDao.findPrimaryUserProfileByAgentId( profile.getIden() );
                        if ( agentDetailsMap != null && agentDetailsMap.size() > 0 ) {
                        		Branch branch = branchDao.findById( Branch.class,  agentDetailsMap.get( CommonConstants.BRANCH_ID_COLUMN )  );
                        		Region region = branch.getRegion();
                        		surveyDetails.setRegionId( agentDetailsMap.get( CommonConstants.REGION_ID_COLUMN ) );
                        		surveyDetails.setBranchId( agentDetailsMap.get( CommonConstants.BRANCH_ID_COLUMN ) );
                        		surveyDetails.setBranchName(branch.getBranch());
                        		surveyDetails.setRegionName(region.getRegion());
                        }
                        
                    } catch ( Exception e ) {
                        LOG.error( "Could not find by agent hierarchy details for id : " + profile.getIden(), e );
                    }
                    surveyDetails.setAgentId( profile.getIden() );
                    if ( profile.getContact_details() != null && profile.getContact_details().getName() != null
                        && profile.getContact_details().getName().trim().length() > 0 ) {
                        surveyDetails.setAgentName( profile.getContact_details().getName() );
                        if ( profile.getContact_details().getMail_ids().getWork() != null )
                            surveyDetails.setAgentEmailId( profile.getContact_details().getMail_ids().getWork() );
                    }
                    surveyDetails.setCompanyId( companyId );
                }


                LOG.info( "saving survey to database" );
                surveyHandler.insertSurveyDetails( surveyDetails );
                //update surveydetail in list
                surveyDetailsList.set( i, surveyDetails );
                latestSurveyIdList.add( surveyDetails.get_id() );
                // Commented as Zillow reviews are saved in Social Survey, SS-307
                // if ( zillowReviewScoreTotal == -1 )
                //    zillowReviewScoreTotal = surveyDetails.getScore();
                // else
                //    zillowReviewScoreTotal += surveyDetails.getScore();


            } else if ( ( existingSurveyDetails.getSummary() == null
                || existingSurveyDetails.getSummary().trim().length() == 0 )
                && ( surveyDetails.getSummary() != null && surveyDetails.getSummary().length() > 0 ) ) {
                LOG.info( "Existing survey found in database for current review" );
                existingSurveyDetails.setSummary( surveyDetails.getSummary() );
                existingSurveyDetails.setReview( surveyDetails.getReview() );
                surveyHandler.updateZillowSummaryInExistingSurveyDetails( existingSurveyDetails );

                existingSurveyDetails.setSourceId( surveyDetails.getSourceId() );
                surveyHandler.updateZillowSourceIdInExistingSurveyDetails( existingSurveyDetails );
                //update surveydetail in list
                surveyDetailsList.set( i, existingSurveyDetails );
                latestSurveyIdList.add( existingSurveyDetails.get_id() );
            } else if ( existingSurveyDetails.getSourceId() == null || existingSurveyDetails.getSourceId().isEmpty() ) {

            } else if ( existingSurveyDetails.getSurveyUpdatedDate() == null ) {
                existingSurveyDetails.setSurveyUpdatedDate( existingSurveyDetails.getSurveyCompletedDate() );
                surveyHandler.updateZillowSurveyUpdatedDateInExistingSurveyDetails( existingSurveyDetails );
            }

            //SS-1214: handling Column 'ZILLOW_SURVEY_ID' cannot be null for the Table: ZILLOW_TEMP_POST
            //if survey is new, surveyDetails.get_id() will not be null, coz, a new data entry happened to SURVEY_DETAILS Mongo Collection
            if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) && fromBatch
                && surveyDetails != null && surveyDetails.get_id() != null ) {
                LOG.info( "Saving review in temp table" );
                postToTempTable( collectionName, profile, surveyDetails );
            }
        }
        if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
            long reviewCount = getReviewsCount( profile.getIden(), -1, -1, CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false,
                false, false, 0 );
            try {
                solrSearchService.editUserInSolr( profile.getIden(), CommonConstants.REVIEW_COUNT_SOLR,
                    String.valueOf( reviewCount ) );
            } catch ( SolrException e ) {
                LOG.error( "Exception occurred while updating zillow review count in solr for agent id : " + profile.getIden()
                    + ". Reason : " + e );
            }
        }
        try {
            LOG.info( "Resetting showSurveyOnUI property for review ids not in list :" + latestSurveyIdList );
            surveyDetailsDao.resetShowSurveyOnUIPropertyForNonLatestReviews( idenColumnName, profile.getIden(),
                latestSurveyIdList );
            LOG.info( "Reset showSurveyOnUI property for review ids not in list successfull." );
        } catch ( Exception e ) {
            LOG.error( "Exception occurred while resetting showSurveyOnUI property for review ids not in list :"
                + latestSurveyIdList + ". Reason :", e );
        }
        LOG.info( "Method fillSurveyDetailsFromReviewMap " + collectionName + " with iden: " + profile.getIden() + " ended" );
        return surveyDetailsList;
    }


    /**
     * 
     * @param profile
     * @param collectionName
     * @param surveyDetails
     * @throws InvalidInputException
     */
    void pushToZillowPostTemp( OrganizationUnitSettings profile, String collectionName, SurveyDetails surveyDetails )
        throws InvalidInputException
    {
        if ( profile == null ) {
            throw new InvalidInputException( "Profile passed as argument in pushToZillowPostTemp cannot be null" );
        }

        if ( collectionName == null || collectionName.isEmpty() ) {
            throw new InvalidInputException(
                "Collection Name passed as argument in pushToZillowPostTemp cannot be null or empty" );
        }

        if ( surveyDetails == null ) {
            throw new InvalidInputException( "Survey Details passed as argument in pushToZillowPostTemp cannot be null" );
        }


        LOG.info( "Method called to push fetched Zillow Review into temp table,pushToZillowPostTemp started" );
        String columnName = null;
        if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
            columnName = CommonConstants.COMPANY_ID_COLUMN;
        } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {
            columnName = CommonConstants.REGION_ID_COLUMN;
        } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
            columnName = CommonConstants.BRANCH_ID_COLUMN;
        } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
            columnName = CommonConstants.AGENT_ID_COLUMN;
        }

        // Building Zillow Temp Post Object
        ZillowTempPost zillowTempPost = new ZillowTempPost();
        zillowTempPost.setEntityColumnName( columnName );
        zillowTempPost.setEntityId( profile.getIden() );
        zillowTempPost.setZillowReviewUrl( surveyDetails.getSourceId() );
        if ( profile.getSocialMediaTokens() != null && profile.getSocialMediaTokens().getZillowToken() != null
            && profile.getSocialMediaTokens().getZillowToken().getZillowProfileLink() != null ) {
            zillowTempPost.setZillowReviewSourceLink( profile.getSocialMediaTokens().getZillowToken().getZillowProfileLink() );
        } else {
            zillowTempPost.setZillowReviewSourceLink( "" );
        }
        zillowTempPost.setZillowReviewRating( surveyDetails.getScore() );
        zillowTempPost.setZillowReviewerName( surveyDetails.getCustomerFirstName() );
        zillowTempPost.setZillowReviewSummary( surveyDetails.getSummary() );
        zillowTempPost.setZillowReviewDescription( surveyDetails.getReview() );
        zillowTempPost.setZillowReviewDate( new Timestamp( surveyDetails.getCreatedOn().getTime() ) );
        zillowTempPost.setZillowSurveyId( surveyDetails.get_id() );

        // Persisting Zillow Temp Post Object
        zillowTempPostDao.saveOrUpdateZillowTempPost( zillowTempPost );

        LOG.info( "Method called to push fetched Zillow Review into temp table,pushToZillowPostTemp ended" );
    }


    @Transactional
    public void postToTempTable( String collectionName, OrganizationUnitSettings profile, SurveyDetails surveyDetails )
    {
        try {
            pushToZillowPostTemp( profile, collectionName, surveyDetails );
        } catch ( Exception e ) {
            LOG.error( "Exception occurred while pushing Zillow review into temp table. Reason :", e );
        }
    }


    /**
     * method to remove tokens from profile detail
     * @param profile
     */
    @Override
    public void removeTokensFromProfile( OrganizationUnitSettings profile )
    {
        LOG.debug( "Inside method removeTokensFromProfile" );
        if ( profile != null ) {
            if ( profile.getSocialMediaTokens() != null ) {
                SocialMediaTokens socialMediaTokens = profile.getSocialMediaTokens();
                if ( socialMediaTokens != null ) {
                    if ( socialMediaTokens.getFacebookToken() != null ) {
                        socialMediaTokens.getFacebookToken().setFacebookAccessToken( null );
                        socialMediaTokens.getFacebookToken().setFacebookAccessTokenToPost( null );
                        socialMediaTokens.getFacebookToken().setFacebookPages( null );
                    }
                    if ( socialMediaTokens.getLinkedInToken() != null ) {
                        socialMediaTokens.getLinkedInToken().setLinkedInAccessToken( null );
                    }
                    if ( socialMediaTokens.getTwitterToken() != null ) {
                        socialMediaTokens.getTwitterToken().setTwitterAccessToken( null );
                        socialMediaTokens.getTwitterToken().setTwitterAccessTokenSecret( null );
                    }
                    if ( socialMediaTokens.getGoogleToken() != null ) {
                        socialMediaTokens.getGoogleToken().setGoogleAccessToken( null );
                        socialMediaTokens.getGoogleToken().setGoogleRefreshToken( null );
                    }
                }
            }
        }
    }


    @Override
    public void imageLoader()
    {
        try {
            new File( CommonConstants.TEMP_FOLDER ).mkdir();

            // update last start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType( CommonConstants.BATCH_TYPE_IMAGE_LOADER,
                CommonConstants.BATCH_NAME_IMAGE_LOADER );

            // Fetch all the profile images pointing to linkedin for company, regions, branches and individuals.
            Map<Long, OrganizationUnitSettings> companySettings = organizationManagementService
                .getSettingsMapWithLinkedinImage( CommonConstants.COMPANY );
            Map<Long, OrganizationUnitSettings> regionSettings = organizationManagementService
                .getSettingsMapWithLinkedinImage( CommonConstants.REGION_COLUMN );
            Map<Long, OrganizationUnitSettings> branchSettings = organizationManagementService
                .getSettingsMapWithLinkedinImage( CommonConstants.BRANCH_NAME_COLUMN );
            Map<Long, OrganizationUnitSettings> agentSettings = organizationManagementService
                .getSettingsMapWithLinkedinImage( "agent" );

            // Process all the company profile images.
            for ( Map.Entry<Long, OrganizationUnitSettings> companySetting : companySettings.entrySet() ) {
                try {
                    String image = loadImages( companySetting.getValue() );
                    if ( image != null ) {
                        updateProfileImage( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION,
                            companySetting.getValue(), image );
                    }
                } catch ( Exception e ) {
                    LOG.error( "Exception caught in ImageLoader while copying image from linkedin to SocialSurvey server. "
                        + "Nested exception is ", e );
                    continue;
                }
            }

            // Process all the region profile images.
            for ( Map.Entry<Long, OrganizationUnitSettings> regionSetting : regionSettings.entrySet() ) {
                try {
                    String image = loadImages( regionSetting.getValue() );
                    if ( image != null ) {
                        updateProfileImage( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION,
                            regionSetting.getValue(), image );
                    }
                } catch ( Exception e ) {
                    LOG.error( "Exception caught in ImageLoader while copying image from linkedin to SocialSurvey server. "
                        + "Nested exception is ", e );
                    continue;
                }
            }

            // Process all the branch profile images.
            for ( Map.Entry<Long, OrganizationUnitSettings> branchSetting : branchSettings.entrySet() ) {
                try {
                    String image = loadImages( branchSetting.getValue() );
                    if ( image != null ) {
                        updateProfileImage( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION,
                            branchSetting.getValue(), image );
                    }
                } catch ( Exception e ) {
                    LOG.error( "Exception caught in ImageLoader while copying image from linkedin to SocialSurvey server. "
                        + "Nested exception is ", e );
                    continue;
                }
            }

            // Process all the individual profile images.
            for ( Map.Entry<Long, OrganizationUnitSettings> agentSetting : agentSettings.entrySet() ) {
                try {
                    String image = loadImages( agentSetting.getValue() );
                    if ( image != null ) {
                        updateProfileImage( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
                            agentSetting.getValue(), image );
                    }
                } catch ( Exception e ) {
                    LOG.error( "Exception caught in ImageLoader while copying image from linkedin to SocialSurvey server. "
                        + "Nested exception is ", e );
                    continue;
                }
            }

            //updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_IMAGE_LOADER );
            LOG.debug( "Completed ImageUploader" );
        } catch ( Exception e ) {
            LOG.error( "Error in ImageUploader", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType( CommonConstants.BATCH_TYPE_IMAGE_LOADER,
                    e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_IMAGE_LOADER,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in ImageUploader " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    private String loadImages( OrganizationUnitSettings setting ) throws Exception
    {
        String linkedinImageUrl = setting.getProfileImageUrl();
        String imageName = java.util.UUID.randomUUID().toString();
        if ( linkedinImageUrl.contains( ".png" ) || linkedinImageUrl.contains( ".PNG" ) ) {
            imageName = imageName + ".png";
        } else if ( linkedinImageUrl.contains( ".jpg" ) || linkedinImageUrl.contains( ".JPG" ) ) {
            imageName = imageName + ".jpg";
        } else if ( linkedinImageUrl.contains( ".jpeg" ) || linkedinImageUrl.contains( ".JPEG" ) ) {
            imageName = imageName + ".jpeg";
        }

        String destination = copyImage( linkedinImageUrl, imageName );
        return destination;
    }


    private BufferedImage getImageFromUrl( String imageUrl )
    {
        BufferedImage image = null;
        try {
            URL url = new URL( imageUrl );
            image = ImageIO.read( url );
        } catch ( IOException e ) {
            LOG.error( "Exception caught while reading image" + e.getMessage() );
        }
        return image;
    }

    @Override
    public String copyImage( String source, String imageName ) throws Exception
    {
        LOG.info( "Method copyImage called for url {} and imageName {}" , source , imageName );
        String fileName = null;
        try {
            BufferedImage image = getImageFromUrl( source );
            if ( image != null ) {
                File tempImage = new File( CommonConstants.TEMP_FOLDER + CommonConstants.FILE_SEPARATOR + imageName );
                tempImage.createNewFile();
                if ( tempImage.exists() ) {
                    if ( imageName.endsWith( ".jpg" ) || imageName.endsWith( ".JPG" ) ) {
                        ImageIO.write( image, "jpg", tempImage );
                    } else if ( imageName.endsWith( ".jpeg" ) || imageName.endsWith( ".JPEG" ) ) {
                        ImageIO.write( image, "png", tempImage );
                    } else if ( imageName.endsWith( ".png" ) || imageName.endsWith( ".PNG" ) ) {
                        ImageIO.write( image, "png", tempImage );
                    }
                    fileName = fileUploadService.uploadProfileImageFile( tempImage, imageName, false );
                    FileUtils
                        .deleteQuietly( new File( CommonConstants.TEMP_FOLDER + CommonConstants.FILE_SEPARATOR + imageName ) );
                    LOG.info( "Successfully retrieved photo of contact with file name {} " , fileName );
                } else {
                    LOG.warn("Error while creating temp image. Can't able to process image {}" , source);
                    return null;
                }
            } else {
                LOG.warn( "error while getting image. Cant able to process image {} " , source );
                return null;
            }
        } catch ( Exception e ) {
            LOG.error( e.getMessage() + ": " + source );
            throw e;
        }

        return cdnUrl + CommonConstants.FILE_SEPARATOR + amazonImageBucket + CommonConstants.FILE_SEPARATOR + fileName;

    }


    //method that returns sort criteria for a company
    @Override
    public String processSortCriteria( long companyId, String sortCriteria )
    {
        if ( sortCriteria == null )
            return CommonConstants.REVIEWS_SORT_CRITERIA_DATE;
        switch ( sortCriteria ) {
            case CommonConstants.REVIEWS_SORT_CRITERIA_DEFAULT: {
                String sortSettings = null;
                if ( companyId > 0 ) {
                    try {
                        sortSettings = organizationManagementService.getCompanySettings( companyId ).getReviewSortCriteria();
                    } catch ( InvalidInputException error ) {
                        LOG.error( "company not found, choosing alternate sort criteria {}", error );
                    }
                }
                if ( sortSettings != "default" ) {
                    return processSortCriteria( companyId, sortSettings );
                } else {
                    return CommonConstants.REVIEWS_SORT_CRITERIA_DATE;
                }
            }
            case CommonConstants.REVIEWS_SORT_CRITERIA_FEATURE:

            case CommonConstants.REVIEWS_SORT_CRITERIA_DATE:
                return sortCriteria;

            default:
                return CommonConstants.REVIEWS_SORT_CRITERIA_DATE;
        }
    }


    @Override
    @SuppressWarnings ( "unchecked")
    //method to return nmls_id
    public Integer fetchAndSaveNmlsId( OrganizationUnitSettings profile, String collectionName, long companyId,
        boolean fromBatch, boolean fromPublicPage ) throws InvalidInputException, UnavailableException
    {
        Integer nmlsId = null;
        if ( profile == null )
            throw new InvalidInputException( "Profile setting passed cannot be null" );
        if ( collectionName == null || collectionName.isEmpty() ) {
            throw new InvalidInputException( "Collection name passed cannot be null or empty" );
        }
        LOG.info( "Method to Fetch social feed for " + collectionName + " with iden: " + profile.getIden() + " started" );
        List<SurveyDetails> surveyDetailsList = new ArrayList<SurveyDetails>();
        if ( profile != null && profile.getSocialMediaTokens() != null ) {

            SocialMediaTokens token = profile.getSocialMediaTokens();
            if ( token != null ) {
                if ( token.getZillowToken() != null ) {
                    LOG.info( "Starting to fetch the feed." );

                    ZillowToken zillowToken = token.getZillowToken();
                    LenderRef zillowLenderRef = zillowToken.getLenderRef();

                    // if nmls found, return it 
                    if ( zillowLenderRef != null && zillowLenderRef.getNmlsId() != null ) {
                        LOG.info( "NmlsId found for enity. So getting records from lender API using NmlsId id : "
                            + zillowLenderRef.getNmlsId() );
                        nmlsId = zillowLenderRef.getNmlsId();

                    }
                }
            }
        }
        LOG.info( "NMLS id : " + nmlsId );
        return nmlsId;
    }


    @Override
    public String buildJsonMessageWithStatus( int status, String message )
    {

        JSONObject jsonMessage = new JSONObject();
        String jsonString = null;
        LOG.debug( "Building json response" );
        try {
            jsonMessage.put( CommonConstants.SUCCESS_ATTRIBUTE, status );
            jsonMessage.put( CommonConstants.MESSAGE, message );

            jsonString = jsonMessage.toString();
            LOG.debug( "Returning json response : {}", jsonString );
            return jsonString;

        } catch ( JSONException e ) {
            LOG.error( "Exception occured while building json response : {}", e.getMessage(), e );
            return "Exception occured while building json response : " + e.getMessage();
        }
    }


    @Override
    public PublicProfileAggregate buildPublicProfileAggregate( PublicProfileAggregate profileAggregate, boolean isBotRequest )
        throws InvalidInputException, ProfileNotFoundException, InvalidSettingsStateException, NoRecordsFetchedException,
        ProfileRedirectionException
    {
        // null and empty checks
        profileInputChecks( profileAggregate );

        LOG.debug( "method buildPublicProfileAggregate started for profile: {}", profileAggregate.getProfileName() );

        Map<String, String> profileLevelData = buildBasicProfileLevelData( profileAggregate );
        String entityId = profileLevelData.get( CommonConstants.ENTITY_ID_COLUMN );
        String collectionUnderConcern = profileLevelData.get( CommonConstants.COLLECTION_TYPE );


        Map<String, OrganizationUnitSettings> profileHierarchyMap = generateProfileHierarchyMap( profileAggregate );

        OrganizationUnitSettings companyProfile = profileHierarchyMap.get( CommonConstants.PROFILE_LEVEL_COMPANY );
        OrganizationUnitSettings regionProfile = profileHierarchyMap.get( CommonConstants.PROFILE_LEVEL_REGION );
        OrganizationUnitSettings branchProfile = profileHierarchyMap.get( CommonConstants.PROFILE_LEVEL_BRANCH );
        AgentSettings individualProfile = (AgentSettings) profileHierarchyMap.get( CommonConstants.PROFILE_LEVEL_INDIVIDUAL );
        OrganizationUnitSettings profileUnderConcern = profileHierarchyMap.get( profileAggregate.getProfileLevel() );

        // redirection checks while agent profile 
        checkForProfileRedirection( profileAggregate );

        Map<SettingsForApplication, OrganizationUnit> map = getPrimaryHierarchyByEntity( entityId,
            profileUnderConcern.getIden() );

        //  migrating the hideSectionsFromProfilePage value from company to profile under concern
        profileUnderConcern.setHideSectionsFromProfilePage( companyProfile.getHideSectionsFromProfilePage() );
        profileUnderConcern.setHiddenSection( companyProfile.isHiddenSection() );

        profileUnderConcern = fillUnitSettings( profileUnderConcern, collectionUnderConcern, companyProfile, regionProfile,
            branchProfile, individualProfile, map, true );

        // aggregated disclaimer
            String disclaimer = aggregateDisclaimer( profileUnderConcern, entityId );
            if ( StringUtils.isNotEmpty( disclaimer ) )
                profileUnderConcern.setDisclaimer( disclaimer );
        

        //remove sensitive info from profile JSON from company profile 
        removeTokensFromProfile( companyProfile );

        //remove sensitive info from profile JSON from profile under concern
        removeTokensFromProfile( profileUnderConcern );

        // populate profile aggregate
        profileAggregate.setProfileUrl( profileLevelData.get( CommonConstants.PROFILE_URL ) );
        profileAggregate.setFindAProCompanyProfileName( companyProfile.getProfileName() );
        profileAggregate.setProfile( profileUnderConcern );
        profileAggregate.setProfileJson( new Gson().toJson(
            profileUnderConcern instanceof AgentSettings ? (AgentSettings) profileUnderConcern : profileUnderConcern ) );
        profileAggregate
            .setReviewSortCriteria( processSortCriteria( companyProfile.getIden(), companyProfile.getReviewSortCriteria() ) );
        profileAggregate.setAverageRating(
            getAverageRatings( profileUnderConcern.getIden(), profileAggregate.getProfileLevel(), false, false, 0, 0 ) );
        profileAggregate.setReviewCount( getReviewsCount( profileUnderConcern.getIden(), -1, -1,
            profileAggregate.getProfileLevel(), false, false, false, 0 ) );
        profileAggregate.setReviews( isBotRequest
            ? getReviews( profileUnderConcern.getIden(), -1, -1, -1, CommonConstants.USER_AGENT_NUMBER_REVIEWS,
                profileAggregate.getProfileLevel(), false, null, null, processSortCriteria( companyProfile.getIden(), null ) )
            : null );

        // NOTE: It was decided not to show Social posts on the UI. So not fetching anymore.

        //set company name
        profileAggregate.setCompanyName(companyProfile.getContact_details().getName());
        
        // build the individual review aggregate
        profileAggregate.setReviewAggregate( buildReviewAggregate( profileAggregate ) );

        LOG.debug( "method buildPublicProfileAggregate finished for profile: {}", profileAggregate.getProfileName() );
        return profileAggregate;
    }


    private IndividualReviewAggregate buildReviewAggregate( PublicProfileAggregate profileAggregate )
        throws InvalidInputException
    {
        LOG.debug( "method individualReviewAggregate() started." );

        if ( StringUtils.isEmpty( profileAggregate.getSurveyId() ) ) {
            return null;
        }

        // get the review under concern
        IndividualReviewAggregate reviewAggregate = validateAndProcessSurveyId( profileAggregate );


        LOG.debug( "method individualReviewAggregate() finished." );
        return reviewAggregate;
    }


    private IndividualReviewAggregate validateAndProcessSurveyId( PublicProfileAggregate profileAggregate )
        throws InvalidInputException
    {
        SurveyDetails review = null;
        OrganizationUnitSettings unitSettings = null;

        IndividualReviewAggregate reviewAggregate = new IndividualReviewAggregate();

        // get the review
        review = surveyHandler.getSurveyDetails( profileAggregate.getSurveyId() );

        if ( review == null ) {
            reviewAggregate.setSurveyIdValid( false );
            reviewAggregate.setInvalidMessage( "Review under concern was not found." );
            return reviewAggregate;
        }


        // check if the review belongs to the hierarchy under concern
        if ( !doesReviewConformToProfileLevel( profileAggregate, review ) ) {
            reviewAggregate.setSurveyIdValid( false );
            reviewAggregate.setInvalidMessage( "Review under concern doesn't belong to the underlying profile." );
            return reviewAggregate;
        }

        // get the unit settings
        try {
            unitSettings = fetchAppropriateUnitSettings( reviewAggregate, review );
            buildTitle( unitSettings );

        } catch ( NoRecordsFetchedException error ) {
            LOG.error( "NoRecordsFetchedException: unable to fetch settings.", error );
            reviewAggregate.setSurveyIdValid( false );
            reviewAggregate.setInvalidMessage( "Review under concern is not related any hierarchy." );
            return reviewAggregate;
        } catch ( InvalidInputException error ) {
            LOG.error( "InvalidInputException: unable to fetch settings.", error );
            reviewAggregate.setSurveyIdValid( false );
            reviewAggregate.setInvalidMessage( "Unable to come up with hierarchy information." );
            return reviewAggregate;
        }

        // set sourceID
        if ( CommonConstants.SURVEY_SOURCE_ZILLOW.equalsIgnoreCase( review.getSource() )
            && StringUtils.isEmpty( review.getSourceId() ) ) {
            review.setSourceId( review.getCompleteProfileUrl() );
        }

        //This is added to get the agent's APP ID and profile URL 
        //DO NOT REMOVE!
        setAgentProfileUrlForReview( Arrays.asList( review ) );

        reviewAggregate.setSurveyIdValid( true );
        reviewAggregate.setUnitSettings( unitSettings );
        reviewAggregate.setReview( review );
        return reviewAggregate;

    }


    private void buildTitle( OrganizationUnitSettings unitSettings )
    {
        LOG.debug( "buildTitle() started" );

        if ( unitSettings == null || unitSettings.getContact_details() == null
            || StringUtils.isNotEmpty( unitSettings.getContact_details().getTitle() ) ) {
            return;
        }


        String location = unitSettings.getContact_details().getLocation();
        String vertical = unitSettings.getVertical();

        unitSettings.getContact_details().setTitle( vertical
            + ( StringUtils.isNotEmpty( location ) ? ", " + location : buildLocation( unitSettings.getContact_details() ) ) );
    }


    private String buildLocation( ContactDetailsSettings contactDetails )
    {
        if ( contactDetails == null ) {
            return "";
        } else {

            List<String> locationList = new ArrayList<>();

            if ( StringUtils.isNotEmpty( contactDetails.getCity() ) ) {
                locationList.add( contactDetails.getCity() );
            }

            if ( StringUtils.isNotEmpty( contactDetails.getState() ) ) {
                locationList.add( contactDetails.getState() );
            }

            if ( StringUtils.isNotEmpty( contactDetails.getCountry() ) ) {
                locationList.add( contactDetails.getCountry() );
            }

            return locationList.isEmpty() ? "" : ( ", " + StringUtils.join( locationList, ", " ) );

        }
    }


    private OrganizationUnitSettings fetchAppropriateUnitSettings( IndividualReviewAggregate reviewAggregate, SurveyDetails review )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "fetching appropriate unit settings." );
        if ( review.getAgentId() > 0 ) {
            reviewAggregate.setProfileLevel( CommonConstants.PROFILE_LEVEL_INDIVIDUAL );
            return organizationManagementService.getAgentSettings( review.getAgentId() );
        } else if ( review.getBranchId() > 0 ) {
            reviewAggregate.setProfileLevel( CommonConstants.PROFILE_LEVEL_BRANCH );
            return organizationManagementService.getBranchSettingsDefault( review.getBranchId() );
        } else if ( review.getRegionId() > 0 ) {
            reviewAggregate.setProfileLevel( CommonConstants.PROFILE_LEVEL_REGION );
            return organizationManagementService.getRegionSettings( review.getRegionId() );
        } else if ( review.getCompanyId() > 0 ) {
            reviewAggregate.setProfileLevel( CommonConstants.PROFILE_LEVEL_COMPANY );
            return organizationManagementService.getCompanySettings( review.getCompanyId() );
        } else {
            throw new InvalidInputException( "Unable to find a hierarchy associated with the review." );
        }
    }


    private boolean doesReviewConformToProfileLevel( PublicProfileAggregate profileAggregate, SurveyDetails review )
    {
        if ( CommonConstants.PROFILE_LEVEL_INDIVIDUAL.equals( profileAggregate.getProfileLevel() ) ) {
            return review.getAgentId() == profileAggregate.getProfile().getIden() ? true : false;
        } else if ( CommonConstants.PROFILE_LEVEL_BRANCH.equals( profileAggregate.getProfileLevel() ) ) {
            return review.getBranchId() == profileAggregate.getProfile().getIden() ? true : false;
        } else if ( CommonConstants.PROFILE_LEVEL_REGION.equals( profileAggregate.getProfileLevel() ) ) {
            return review.getRegionId() == profileAggregate.getProfile().getIden() ? true : false;
        } else if ( CommonConstants.PROFILE_LEVEL_COMPANY.equals( profileAggregate.getProfileLevel() ) ) {
            return review.getCompanyId() == profileAggregate.getProfile().getIden() ? true : false;
        } else {
            return false;
        }
    }


    private void checkForProfileRedirection( PublicProfileAggregate profileAggregate ) throws ProfileRedirectionException
    {
        if ( CommonConstants.PROFILE_LEVEL_INDIVIDUAL.equals( profileAggregate.getProfileLevel() ) ) {
            if ( !profileAggregate.isAgent() ) {
                LOG.error( "The profile provided indicates that the user is not an agent." );
                throw new ProfileRedirectionException( "The profile provided indicates that the user is not an agent." );
            } else if ( profileAggregate.isHiddenSection() ) {
                LOG.error( "The company settings indicate that hidden flag is set" );
                throw new ProfileRedirectionException( "The profile to be displayed is hidden indicated by company settings." );
            }
        }
    }


    private Map<String, OrganizationUnitSettings> generateProfileHierarchyMap( PublicProfileAggregate profileAggregate )
        throws ProfileNotFoundException, InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "method generateProfileHierarchyMap() running." );
        OrganizationUnitSettings companyProfile = null;
        OrganizationUnitSettings regionProfile = null;
        OrganizationUnitSettings branchProfile = null;
        AgentSettings individualProfile = null;

        Map<String, OrganizationUnitSettings> agentHierarchyMap = null;

        Map<String, OrganizationUnitSettings> profileHierarchyMap = new HashMap<>();
        Map<String, Long> idHierarchyMap = new HashMap<>();

        /* fetch the necessary profiles */

        // fetch profile agent profile
        if ( CommonConstants.PROFILE_LEVEL_INDIVIDUAL.equals( profileAggregate.getProfileLevel() ) ) {

            agentHierarchyMap = buildAgentProfileMap( profileAggregate );

            companyProfile = agentHierarchyMap.get( CommonConstants.PROFILE_LEVEL_COMPANY );
            regionProfile = agentHierarchyMap.get( CommonConstants.PROFILE_LEVEL_REGION );
            branchProfile = agentHierarchyMap.get( CommonConstants.PROFILE_LEVEL_BRANCH );
            individualProfile = (AgentSettings) agentHierarchyMap.get( CommonConstants.PROFILE_LEVEL_INDIVIDUAL );
        }

        else {

            // company profile
            companyProfile = getCompanyProfileByProfileName( profileAggregate.getCompanyProfileName() );

            boolean doCheckForIncompleteProfile = CommonConstants.PROFILE_LEVEL_COMPANY
                .equals( profileAggregate.getProfileLevel() );
            boolean isProfilePageHidden = ( doCheckForIncompleteProfile && companyProfile != null
                && companyProfile.isHidePublicPage() );

            hierarchySettingsValidityCheck( companyProfile, CommonConstants.COMPANY_COLUMN, profileAggregate.getProfileName(),
                doCheckForIncompleteProfile, isProfilePageHidden );

            // region profile
            if ( CommonConstants.PROFILE_LEVEL_REGION.equals( profileAggregate.getProfileLevel() ) ) {

                regionProfile = getRegionSettingsByProfileName( profileAggregate.getCompanyProfileName(),
                    profileAggregate.getProfileName() );
                hierarchySettingsValidityCheck( regionProfile, CommonConstants.REGION_COLUMN, profileAggregate.getProfileName(),
                    true, regionProfile.isHidePublicPage() );

            } else if ( CommonConstants.PROFILE_LEVEL_BRANCH.equals( profileAggregate.getProfileLevel() ) ) {

                // branch profile
                branchProfile = getBranchSettingsByProfileName( profileAggregate.getCompanyProfileName(),
                    profileAggregate.getProfileName() );
                hierarchySettingsValidityCheck( branchProfile, CommonConstants.BRANCH_NAME_COLUMN,
                    profileAggregate.getProfileName(), true, branchProfile.isHidePublicPage() );

                // region for branch profile
                regionProfile = getRegionProfileByBranch( branchProfile );
                hierarchySettingsValidityCheck( regionProfile, CommonConstants.REGION_COLUMN, profileAggregate.getProfileName(),
                    false, false );
            }
        }

        // set the complete profile URLs of hierarchies
        profileAggregate.setCompleteCompanyProfileUrl( companyProfile.getCompleteProfileUrl() );
        idHierarchyMap.put( CommonConstants.COMPANY_ID_COLUMN, companyProfile.getIden() );

        if ( regionProfile != null ) {
            profileAggregate.setCompleteRegionProfileUrl( regionProfile.getCompleteProfileUrl() );
            idHierarchyMap.put( CommonConstants.REGION_ID_COLUMN, regionProfile.getIden() );
        }

        if ( branchProfile != null ) {
            profileAggregate.setCompleteBranchProfileUrl( branchProfile.getCompleteProfileUrl() );
            idHierarchyMap.put( CommonConstants.BRANCH_ID_COLUMN, branchProfile.getIden() );
        }

        // IDs required for profile redirection
        profileAggregate.setHierarchyMap( idHierarchyMap );

        profileHierarchyMap.put( CommonConstants.PROFILE_LEVEL_INDIVIDUAL, individualProfile );
        profileHierarchyMap.put( CommonConstants.PROFILE_LEVEL_BRANCH, branchProfile );
        profileHierarchyMap.put( CommonConstants.PROFILE_LEVEL_REGION, regionProfile );
        profileHierarchyMap.put( CommonConstants.PROFILE_LEVEL_COMPANY, companyProfile );

        return profileHierarchyMap;

    }


    private void hierarchySettingsValidityCheck( OrganizationUnitSettings profile, String profileType,
        String profileUnderConcern, boolean checkForIncompleteProfile, boolean isPublicPageHidden )
        throws ProfileNotFoundException
    {
        LOG.debug( "method hierarchySettingsValidityCheck() running" );

        boolean isProfileNull = ( profile == null );
        boolean isProfileStatusNotNull = !isProfileNull && ( profile.getStatus() != null );
        boolean isProfileDeleted = isProfileStatusNotNull
            && profile.getStatus().equalsIgnoreCase( CommonConstants.STATUS_DELETED_MONGO );
        boolean isProfileIncomplete = isProfileStatusNotNull
            && profile.getStatus().equalsIgnoreCase( CommonConstants.STATUS_INCOMPLETE_MONGO );

        if ( isProfileNull
            || ( isProfileStatusNotNull && ( isProfileDeleted || ( checkForIncompleteProfile && isProfileIncomplete ) ) )
            || isPublicPageHidden ) {
            LOG.error( "No settings found for {} while fetching profile: {}", profileType, profileUnderConcern );
            throw new ProfileNotFoundException( "No settings found for a hierarchy while fetching profile under concern." );
        }
    }


    private Map<String, OrganizationUnitSettings> buildAgentProfileMap( PublicProfileAggregate profileAggregate )
        throws ProfileNotFoundException, InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "method buildAgentProfileMap() started" );
        Map<String, OrganizationUnitSettings> profileHierarchyMap = new HashMap<>();
        OrganizationUnitSettings companyProfile = null;
        OrganizationUnitSettings regionProfile = null;
        OrganizationUnitSettings branchProfile = null;
        AgentSettings individualProfile = null;


        // get the user composite object
        UserCompositeEntity userCompositeObject = getCompositeUserObjectByProfileName( profileAggregate.getProfileName(),
            true );

        // individual profile
        individualProfile = userCompositeObject.getAgentSettings();
        User user = userCompositeObject.getUser();

        hierarchySettingsValidityCheck( individualProfile, CommonConstants.AGENT_COLUMN, profileAggregate.getProfileName(),
            false, individualProfile.isHidePublicPage() );

        // get other hierarchy settings
        Map<String, Long> agentHierarchyMap = getPrimaryHierarchyByAgentProfile( individualProfile );
        LOG.debug( "Got the primary hierarchy." );

        if ( agentHierarchyMap == null ) {
            LOG.error( "Unable to fetch primary profile for this user while parsing agent profile page" );
            throw new FatalException( "Unable to fetch primary profile for this user " + individualProfile.getIden() );
        }

        companyProfile = organizationManagementService.getCompanySettings( user.getCompany().getCompanyId() );
        hierarchySettingsValidityCheck( companyProfile, CommonConstants.COMPANY_COLUMN, profileAggregate.getProfileName(),
            false, false );

        // set the company profile name
        profileAggregate.setCompanyProfileName( StringUtils.lowerCase( companyProfile.getProfileName() ) );

        LOG.debug( "Company ID : {} Region ID : {} Branch ID : {}", agentHierarchyMap.get( CommonConstants.COMPANY_ID_COLUMN ),
            agentHierarchyMap.get( CommonConstants.REGION_ID_COLUMN ),
            agentHierarchyMap.get( CommonConstants.BRANCH_ID_COLUMN ) );

        regionProfile = organizationManagementService
            .getRegionSettings( agentHierarchyMap.get( CommonConstants.REGION_ID_COLUMN ) );
        hierarchySettingsValidityCheck( regionProfile, CommonConstants.REGION_COLUMN, profileAggregate.getProfileName(), false,
            false );

        branchProfile = organizationManagementService
            .getBranchSettingsDefault( agentHierarchyMap.get( CommonConstants.BRANCH_ID_COLUMN ) );
        hierarchySettingsValidityCheck( branchProfile, CommonConstants.BRANCH_NAME_COLUMN, profileAggregate.getProfileName(),
            false, false );

        // set agent and company related flags
        profileAggregate.setAgent( isAgent( user ) );
        profileAggregate.setHiddenSection( companyProfile.isHiddenSection() );
        
        //For an agent, if login is prevented, then agent's public page should be hidden and redirected to branch/region/company
        profileAggregate.setHiddenSection( individualProfile.isHidePublicPage() );


        //set vertical name from the company
        individualProfile.setVertical( user.getCompany().getVerticalsMaster().getVerticalName() );

        //Aggregate agent details
        individualProfile = (AgentSettings) aggregateAgentDetails( user, individualProfile,
            individualProfile.getLockSettings() == null ? new LockSettings() : individualProfile.getLockSettings() );


        //set survey settings in individual profile
        if ( individualProfile.getSurvey_settings() == null ) {
            individualProfile.setSurvey_settings( companyProfile.getSurvey_settings() );
        }

        profileHierarchyMap.put( CommonConstants.PROFILE_LEVEL_INDIVIDUAL, individualProfile );
        profileHierarchyMap.put( CommonConstants.PROFILE_LEVEL_BRANCH, branchProfile );
        profileHierarchyMap.put( CommonConstants.PROFILE_LEVEL_REGION, regionProfile );
        profileHierarchyMap.put( CommonConstants.PROFILE_LEVEL_COMPANY, companyProfile );

        LOG.debug( "method buildAgentProfileMap() finished" );
        return profileHierarchyMap;

    }


    private Map<String, String> buildBasicProfileLevelData( PublicProfileAggregate profileAggregate )
        throws InvalidInputException
    {
        LOG.debug( "method getEntityAndCollectionDataByProfileLevel() running" );
        Map<String, String> profileLevelData = new HashMap<>();

        String profileLevel = profileAggregate.getProfileLevel();
        String baseProfileUrl = applicationBaseUrl + "pages/";

        if ( CommonConstants.PROFILE_LEVEL_COMPANY.equals( profileLevel ) ) {
            profileLevelData.put( CommonConstants.ENTITY_ID_COLUMN, CommonConstants.COMPANY_ID );
            profileLevelData.put( CommonConstants.COLLECTION_TYPE,
                MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
            profileLevelData.put( CommonConstants.PROFILE_URL,
                baseProfileUrl + CommonConstants.COMPANY + "/" + profileAggregate.getCompanyProfileName() );

        } else if ( CommonConstants.PROFILE_LEVEL_REGION.equals( profileLevel ) ) {
            profileLevelData.put( CommonConstants.ENTITY_ID_COLUMN, CommonConstants.REGION_ID );
            profileLevelData.put( CommonConstants.COLLECTION_TYPE,
                MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
            profileLevelData.put( CommonConstants.PROFILE_URL, baseProfileUrl + CommonConstants.REGION_COLUMN + "/"
                + profileAggregate.getCompanyProfileName() + "/" + profileAggregate.getProfileName() );

        } else if ( CommonConstants.PROFILE_LEVEL_BRANCH.equals( profileLevel ) ) {
            profileLevelData.put( CommonConstants.ENTITY_ID_COLUMN, CommonConstants.BRANCH_ID );
            profileLevelData.put( CommonConstants.COLLECTION_TYPE,
                MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
            profileLevelData.put( CommonConstants.PROFILE_URL, baseProfileUrl + CommonConstants.OFFICE + "/"
                + profileAggregate.getCompanyProfileName() + "/" + profileAggregate.getProfileName() );

        } else if ( CommonConstants.PROFILE_LEVEL_INDIVIDUAL.equals( profileLevel ) ) {
            profileLevelData.put( CommonConstants.ENTITY_ID_COLUMN, CommonConstants.AGENT_ID );
            profileLevelData.put( CommonConstants.COLLECTION_TYPE,
                MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
            profileLevelData.put( CommonConstants.PROFILE_URL, baseProfileUrl + profileAggregate.getProfileName() );

        } else {
            LOG.error( "Invalid profile level in getEntityAndCollectionDataByProfileLevel()" );
            throw new InvalidInputException( "Please provide a valid profile level." );
        }
        return profileLevelData;
    }


    private void profileInputChecks( PublicProfileAggregate profileAggregate ) throws InvalidInputException
    {
        LOG.debug( "method profileInputCheck() running" );

        if ( profileAggregate == null ) {
            LOG.error( "Basic profile information is not present." );
            throw new InvalidInputException( "Not enough profile information in profile aggregate." );
        }

        // null and empty checks on profile name and level
        if ( StringUtils.isEmpty( profileAggregate.getProfileLevel() )
            || StringUtils.isEmpty( profileAggregate.getProfileName() ) ) {
            LOG.error( "Not enough profile information to build from profile aggregate." );
            throw new InvalidInputException( "Not enough profile information to build from profile aggregate." );
        }

        // validate profile level value
        if ( !Arrays
            .asList( CommonConstants.PROFILE_LEVEL_COMPANY, CommonConstants.PROFILE_LEVEL_BRANCH,
                CommonConstants.PROFILE_LEVEL_REGION, CommonConstants.PROFILE_LEVEL_INDIVIDUAL )
            .contains( profileAggregate.getProfileLevel() ) ) {
            LOG.error( "Invalid profile level" );
            throw new InvalidInputException( "Please specify a valid profile level." );
        }

        // resolve company profile name necessity
        if ( StringUtils.isEmpty( profileAggregate.getCompanyProfileName() )
            && !CommonConstants.PROFILE_LEVEL_INDIVIDUAL.equals( profileAggregate.getProfileLevel() ) ) {
            LOG.error( "Company profile name is Necessary." );
            throw new InvalidInputException( "Please provide a valid company profile name." );
        }

    }


    @Override
    public boolean isAgent( User user ) throws InvalidInputException
    {
        if ( user == null ) {
            LOG.error( "User cannot be null." );
            throw new InvalidInputException( "User object not specified" );
        }

        LOG.debug( "method isAgent() started for user {}", user.getUserId() );

        List<UserProfile> userProfiles = user.getUserProfiles();

        if ( userProfiles == null || userProfiles.isEmpty() ) {
            LOG.warn( "Invalid individual profile." );
            return false;
        }

        for ( UserProfile profile : user.getUserProfiles() ) {
            if ( CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID == profile.getProfilesMaster().getProfileId() ) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String publicProfileRedirection( PublicProfileAggregate profileAggregate )
        throws ProfileNotFoundException, InvalidInputException
    {
        LOG.debug( "method publicProfileRedirection() started." );
        if ( profileAggregate == null || profileAggregate.getHierarchyMap() == null ) {
            LOG.error( "Profile data is not specified." );
            throw new ProfileNotFoundException( "Profile data is not specified." );
        }

        if ( StringUtils.isEmpty( profileAggregate.getCompleteCompanyProfileUrl() ) ) {
            LOG.error( "company profile URL not specified." );
            throw new InvalidInputException( "company profile URL not specified." );
        }

        if ( StringUtils.isEmpty( profileAggregate.getProfileName() ) ) {
            LOG.error( "profile name is not specified." );
            throw new InvalidInputException( "profile name is not specified." );
        }

        String redirectionUrl = "";

        if ( !profileAggregate.isAgent() ) {

            LOG.debug( "The user with profile name: {} is not an agent.", profileAggregate.getProfileName() );
            LOG.info( "Service to redirect to company profile page executed" );
            redirectionUrl = profileAggregate.getCompleteCompanyProfileUrl();

        } else if ( profileAggregate.isHiddenSection() ) {

            LOG.debug( "The profile with name: {} is not visible as hidden section flag is set.",
                profileAggregate.getProfileName() );
            redirectionUrl = determineProfileAlternate( profileAggregate );

        } else {
            redirectionUrl = profileAggregate.getCompleteCompanyProfileUrl();
        }

        // add the surveyId at the if present
        redirectionUrl += ( StringUtils.isNotEmpty( profileAggregate.getSurveyId() ) ? ( "/" + profileAggregate.getSurveyId() )
            : "" );

        LOG.debug( "method publicProfileRedirection() finished." );
        return redirectionUrl;
    }


    private String determineProfileAlternate( PublicProfileAggregate profileAggregate ) throws InvalidInputException
    {
        long regionId = profileAggregate.getHierarchyMap().get( CommonConstants.REGION_ID_COLUMN );
        long branchId = profileAggregate.getHierarchyMap().get( CommonConstants.BRANCH_ID_COLUMN );

        Branch branch = userManagementService.getBranchById( branchId );

        if ( branch == null || branch.getIsDefaultBySystem() == 1 ) {
            Region region = userManagementService.getRegionById( regionId );
            if ( region == null || region.getIsDefaultBySystem() == 1 ) {

                LOG.info( "Service to redirect to company profile page executed" );
                return profileAggregate.getCompleteCompanyProfileUrl();
            } else {
                if ( StringUtils.isEmpty( profileAggregate.getCompleteRegionProfileUrl() ) ) {
                    throw new InvalidInputException( "Region profile URL is not specified" );
                } else {
                    LOG.info( "Service to redirect to region profile page executed" );
                    return profileAggregate.getCompleteRegionProfileUrl();
                }
            }
        } else {
            if ( StringUtils.isEmpty( profileAggregate.getCompleteBranchProfileUrl() ) ) {
                throw new InvalidInputException( "Branch profile URL is not specified" );
            } else {
                LOG.info( "Service to redirect to branch profile page executed" );
                return profileAggregate.getCompleteBranchProfileUrl();
            }
        }
    }


    @Override
    public boolean isCaptchaForContactUsMailProcessed( String remoteAddress, String captchaResponse )
        throws InvalidInputException
    {
        LOG.debug( "method isCaptchaForContactUsMailProcessed() running" );
        if ( validateCaptcha.equals( CommonConstants.YES_STRING ) ) {
            return captchaValidation.isCaptchaValid( remoteAddress, captchaSecretKey, captchaResponse );
        } else {
            return true;
        }
    }


    /**
     * 
     * @param entityType
     * @param entityId
     * @param pixcelId
     * @param userSettings
     * @throws NonFatalException
     */
    @Override
    public void updateFacebookPixelId( String entityType, long entityId, String pixelId, UserSettings userSettings )
        throws NonFatalException
    {

        SocialMediaTokens socialMediaTokens;

        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( entityId );
            if ( companySettings == null ) {
                throw new InvalidInputException( "No company settings found in current session" );
            }
            socialMediaTokens = companySettings.getSocialMediaTokens();
            socialMediaTokens = updateFacebookPixelIdInMediaTokens( pixelId, socialMediaTokens,
                companySettings.getProfileName() );
            updateSocialMediaTokens( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings,
                socialMediaTokens );

            companySettings.setSocialMediaTokens( socialMediaTokens );
            userSettings.setCompanySettings( companySettings );

        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
            if ( regionSettings == null ) {
                throw new InvalidInputException( "No Region settings found in current session" );
            }
            socialMediaTokens = regionSettings.getSocialMediaTokens();
            socialMediaTokens = updateFacebookPixelIdInMediaTokens( pixelId, socialMediaTokens,
                regionSettings.getProfileName() );
            updateSocialMediaTokens( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings,
                socialMediaTokens );

            regionSettings.setSocialMediaTokens( socialMediaTokens );
            userSettings.getRegionSettings().put( entityId, regionSettings );

        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
            if ( branchSettings == null ) {
                throw new InvalidInputException( "No Branch settings found in current session" );
            }
            socialMediaTokens = branchSettings.getSocialMediaTokens();
            socialMediaTokens = updateFacebookPixelIdInMediaTokens( pixelId, socialMediaTokens,
                branchSettings.getProfileName() );
            updateSocialMediaTokens( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings,
                socialMediaTokens );

            branchSettings.setSocialMediaTokens( socialMediaTokens );
            userSettings.getRegionSettings().put( entityId, branchSettings );

        } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
            AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
            if ( agentSettings == null ) {
                throw new InvalidInputException( "No Agent settings found in current session" );
            }
            socialMediaTokens = agentSettings.getSocialMediaTokens();
            socialMediaTokens = updateFacebookPixelIdInMediaTokens( pixelId, socialMediaTokens,
                agentSettings.getProfileName() );
            updateSocialMediaTokens( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings,
                socialMediaTokens );

            agentSettings.setSocialMediaTokens( socialMediaTokens );
            userSettings.setAgentSettings( agentSettings );
        } else {
            throw new InvalidInputException( "Invalid input exception occurred in updating lendingTree token.",
                DisplayMessageConstants.GENERAL_ERROR );
        }


        //Update social connection history
        socialManagementService.updateSocialConnectionsHistory( entityType, entityId, socialMediaTokens,
            CommonConstants.FACEBOOK_PIXEL_ID, CommonConstants.SOCIAL_MEDIA_CONNECTED );

        LOG.info( "facebookPixelId updated successfully" );

    }


    private SocialMediaTokens updateFacebookPixelIdInMediaTokens( String pixelId, SocialMediaTokens socialMediaTokens,
        String profileName )
    {
        LOG.debug( "Method updateFacebookPixelIdInMediaTokens() called" );
        if ( socialMediaTokens == null ) {
            LOG.debug( "No social media token in profile added" );
            socialMediaTokens = new SocialMediaTokens();
        }
        if ( socialMediaTokens.getFacebookPixelToken() == null ) {
            socialMediaTokens.setFacebookPixelToken( new FacebookPixelToken() );
        }

        FacebookPixelToken facebookPixelToken = socialMediaTokens.getFacebookPixelToken();
        facebookPixelToken.setPixelId( pixelId );
        String fbPixelImageTagWithId = MessageFormat.format( fbPixelImageTag, pixelId, profileName );
        facebookPixelToken.setPixelImgTag( fbPixelImageTagWithId );
        socialMediaTokens.setFacebookPixelToken( facebookPixelToken );
        LOG.debug( "Method updateFacebookPixelIdInMediaTokens() finished" );
        return socialMediaTokens;
    }


    @Override
    public List<MiscValues> processEmailIdsInput( String emailIdsStr ) throws NonFatalException
    {
        try {
            return new ObjectMapper().readValue( emailIdsStr,
                TypeFactory.defaultInstance().constructCollectionType( List.class, MiscValues.class ) );
        } catch ( IOException error ) {
            LOG.warn( "Unable to parse mailIDs" );
            throw new NonFatalException( "Unable to parse mailIDs", DisplayMessageConstants.GENERAL_ERROR, error );
        }
    }


    @Override
    public void updateEmailIdInContactDetails( ContactDetailsSettings contactDetails, List<MiscValues> mailIds )
        throws InvalidInputException, UserAlreadyExistsException
    {
        LOG.info( "method updateEmailIdInContactDetails() started" );
        String workEmailId = null;
        String personalEmailId = null;
        List<MiscValues> others = new ArrayList<>();

        if( contactDetails == null ){
            LOG.warn( "Contact details is not specified" );
            throw new InvalidInputException( "Contact details is not specified" );
        } else if( mailIds == null || mailIds.isEmpty() ){
            LOG.warn( "New mail IDs not specified" );
            throw new InvalidInputException( "New mail IDs not specified" );            
        }

        for ( MiscValues mailId : mailIds ) {
            if ( CommonConstants.EMAIL_TYPE_WORK.equalsIgnoreCase( mailId.getKey() ) ) {
                workEmailId = mailId.getValue();
            } else if ( CommonConstants.EMAIL_TYPE_PERSONAL.equalsIgnoreCase( mailId.getKey() ) ) {
                personalEmailId = mailId.getValue();
            } else {
                others.add( mailId );
            }
        }

        if ( StringUtils.isEmpty( personalEmailId ) && StringUtils.isEmpty( workEmailId ) && others.isEmpty() ) {
            LOG.debug( "Nothing to update" );
            LOG.warn( "No email IDs specified to update" );
            throw new InvalidInputException( "No email IDs specified to update" );
        }

        MailIdSettings mailIdSettings = contactDetails.getMail_ids();
        
        if( mailIdSettings == null ){
            mailIdSettings = new MailIdSettings();
            contactDetails.setMail_ids( mailIdSettings );
        }
        
        // update personal and work and other e-mail IDs
        if ( StringUtils.isNotEmpty( workEmailId ) ) {

            mailIdSettings.setWorkEmailToVerify( workEmailId );
            mailIdSettings.setWorkEmailVerified( false );
        }

        if ( StringUtils.isNotEmpty( personalEmailId ) ) {
            mailIdSettings.setPersonalEmailToVerify( personalEmailId );
            mailIdSettings.setPersonalEmailVerified( false );
        }

        if ( !others.isEmpty() ) {
            mailIdSettings.setOthers( others );
        }
        
        LOG.info( "method updateEmailIdInContactDetails() finished" );
    }

    @Override
    public void updateVerifiedEmail( OrganizationUnitSettings unitSettings, boolean isWorkEmailLockedByCompany, long companyId,
        String collectionType, List<MiscValues> mailIds )
        throws InvalidInputException, UndeliveredEmailException, UserAlreadyExistsException
    {
        LOG.info( "method updateVerifiedEmail() started" );
        
        if( unitSettings == null ){
            LOG.warn( "Settings are not specified" );
            throw new InvalidInputException( "Settings are not specified" );
        } else if( mailIds == null || mailIds.isEmpty() ){
            LOG.warn( "New mail IDs are not specified" );
            throw new InvalidInputException( "New mail IDs are not specified" );            
        } else if( StringUtils.isEmpty( collectionType ) ){
            LOG.warn( "Target collection not specified" );
            throw new InvalidInputException( "Target collection not specified" );
        }
        
        ContactDetailsSettings contactDetailsSettings = unitSettings.getContact_details();

        // Send verification Links
        if ( isWorkEmailLockedByCompany ) {
            generateAndSendEmailVerificationRequestLinkToAdmin( mailIds, companyId, collectionType, unitSettings );
        } else {
            sendVerificationLinks( mailIds, collectionType, unitSettings );
        }

        contactDetailsSettings = updateMailSettings( contactDetailsSettings, mailIds, isWorkEmailLockedByCompany );

        if ( unitSettings instanceof AgentSettings ) {
            contactDetailsSettings = updateAgentContactDetails( collectionType, (AgentSettings) unitSettings,
                contactDetailsSettings );
        } else {
            contactDetailsSettings = updateContactDetails( collectionType, unitSettings, contactDetailsSettings );
        }

        unitSettings.setContact_details( contactDetailsSettings );
        LOG.info( "method updateVerifiedEmail() finished" );
    }


    private void checkForExistingUser( String workEmailId ) throws UserAlreadyExistsException, InvalidInputException
    {
        try {
            userManagementService.getUserByEmailAddress( workEmailId );
            throw new UserAlreadyExistsException( "User already exists with emailId : " + workEmailId );
        } catch ( NoRecordsFetchedException e ) {
            LOG.debug( "User not registerd already with email Id : {}", workEmailId );
        }

    }


    /**
     *
     * @param mailIds
     * @param entityType
     * @param userSettings
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    // send verification links
    private void sendVerificationLinks( List<MiscValues> mailIds, String entityType, OrganizationUnitSettings userSettings )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method sendVerificationLinks() called from ProfileManagementController" );
        Map<String, String> urlParams = null;

        for ( MiscValues mailId : mailIds ) {
            String key = mailId.getKey();
            String emailId = mailId.getValue();
            if ( key.equalsIgnoreCase( CommonConstants.EMAIL_TYPE_WORK ) ) {
                urlParams = new HashMap<>();
                urlParams.put( CommonConstants.EMAIL_ID, emailId );
                urlParams.put( CommonConstants.EMAIL_TYPE, CommonConstants.EMAIL_TYPE_WORK );
                urlParams.put( CommonConstants.ENTITY_ID_COLUMN, Long.toString( userSettings.getIden() ) );
                urlParams.put( CommonConstants.ENTITY_TYPE_COLUMN, entityType );
                urlParams.put( CommonConstants.URL_PARAM_VERIFICATION_REQUEST_TYPE,
                    CommonConstants.URL_PARAM_VERIFICATION_REQUEST_TYPE_TO_USER );

                generateVerificationUrl( urlParams,
                    applicationBaseUrl + CommonConstants.REQUEST_MAPPING_EMAIL_EDIT_VERIFICATION, emailId,
                    userSettings.getContact_details().getName() );
            }
        }
        LOG.debug( "Method sendVerificationLinks() finished from ProfileManagementController" );
    }


    // Update mail IDs
    private ContactDetailsSettings updateMailSettings( ContactDetailsSettings contactDetailsSettings, List<MiscValues> mailIds,
        boolean verifiedByAdmin ) throws InvalidInputException, UserAlreadyExistsException
    {
        LOG.debug( "Method updateMailSettings() called from ProfileManagementController" );
        if ( contactDetailsSettings == null ) {
            throw new InvalidInputException( "No contact details object found for user" );
        }

        MailIdSettings mailIdSettings = contactDetailsSettings.getMail_ids();
        if ( mailIdSettings == null ) {
            LOG.debug( "No maild ids added, create new mail id object in contact details" );
            mailIdSettings = new MailIdSettings();
        }

        List<MiscValues> others = null;
        for ( MiscValues mailId : mailIds ) {
            String key = mailId.getKey();
            String value = mailId.getValue();
            if ( key.equalsIgnoreCase( CommonConstants.EMAIL_TYPE_WORK ) ) {
                checkForExistingUser( value );
                mailIdSettings.setWorkEmailToVerify( value );
                mailIdSettings.setWorkEmailVerified( false );
                mailIdSettings.setWorkMailVerifiedByAdmin( verifiedByAdmin );
            } else if ( key.equalsIgnoreCase( CommonConstants.EMAIL_TYPE_PERSONAL ) ) {
                mailIdSettings.setPersonal( value );
                mailIdSettings.setPersonalEmailToVerify( value );
                mailIdSettings.setPersonalEmailVerified( false );
            } else {
                if ( others == null ) {
                    others = new ArrayList<>();
                }
                others.add( mailId );
            }
        }

        mailIdSettings.setOthers( others );
        contactDetailsSettings.setMail_ids( mailIdSettings );
        LOG.debug( "Method updateMailSettings() finished from ProfileManagementController" );
        return contactDetailsSettings;
    }
}