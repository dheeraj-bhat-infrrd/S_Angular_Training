package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

// JIRA: SS-27: By RM05: BOC
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import com.realtech.socialsurvey.core.commons.*;
import com.realtech.socialsurvey.core.dao.*;
import com.realtech.socialsurvey.core.entities.*;
import com.realtech.socialsurvey.core.enums.*;
import com.realtech.socialsurvey.core.vo.*;
import com.realtech.socialsurvey.core.vo.BranchVO;
import com.realtech.socialsurvey.core.vo.ManageTeamBulkActionVo;
import com.realtech.socialsurvey.core.vo.ManageTeamBulkResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.FilterKeywordsComparator;
import com.realtech.socialsurvey.core.commons.ProfileCompletionList;
import com.realtech.socialsurvey.core.commons.TrustedSourceComparator;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.DisabledAccountDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.MongoApplicationSettingsDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.RedisDao;
import com.realtech.socialsurvey.core.dao.RegionDao;
import com.realtech.socialsurvey.core.dao.RemovedUserDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserInviteDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.UsercountModificationNotificationDao;
import com.realtech.socialsurvey.core.dao.ZillowHierarchyDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import static com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl.*;
import com.realtech.socialsurvey.core.entities.ftp.FtpSurveyResponse;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.DashboardService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserAssignmentException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UtilityService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ZillowUpdateService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionCancellationUnsuccessfulException;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.searchengine.SearchEngineManagementServices;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsLocker;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsSetter;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.social.SocialMediaExceptionHandler;
import com.realtech.socialsurvey.core.services.socialmonitor.feed.SocialFeedService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.core.utils.ZipCodeExclusionStrategy;
import com.realtech.socialsurvey.core.utils.images.ImageProcessor;
import com.realtech.socialsurvey.core.workbook.utils.WorkbookData;
import com.realtech.socialsurvey.core.workbook.utils.WorkbookOperations;

@DependsOn ( "generic")
@Component
public class OrganizationManagementServiceImpl implements OrganizationManagementService, InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( OrganizationManagementServiceImpl.class );
    private static Map<Integer, VerticalsMaster> verticalsMastersMap = new HashMap<Integer, VerticalsMaster>();
    public static final String SUCCESS_MESSAGE = "Organization Settings updated Successfully";

    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private GenericDao<FileUpload, Long> fileUploadDao;

    @Autowired
    private GenericDao<CrmBatchTracker, Long> crmBatchTrackerDao;

    @Autowired
    private GenericDao<SurveyPreInitiation, Long> surveyPreInitiationDao;

    @Autowired
    private GenericDao<UserApiKey, Long> userApiKeyDao;

    @Autowired
    private GenericDao<LicenseDetail, Long> licenceDetailDao;

    @Autowired
    private GenericDao<VerticalsMaster, Integer> verticalMastersDao;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private GenericDao<StateLookup, Integer> stateLookupDao;

    @Autowired
    private GenericDao<ZipCodeLookup, Integer> zipCodeLookupDao;

    @Autowired
    private GenericDao<EncompassSdkVersion, Long> encompassSdkVersionDao;

    @Autowired
    private DisabledAccountDao disabledAccountDao;

    @Resource
    @Qualifier ( "branch")
    private BranchDao branchDao;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private RegionDao regionDao;

    @Autowired
    private UserProfileDao userProfileDao;

    @Autowired
    private SolrSearchService solrSearchService;

    @Autowired
    private RemovedUserDao removedUserDao;

    @Autowired
    private UserInviteDao userInviteDao;

    @Autowired
    private GenericDao<VerticalCrmMapping, Long> verticalCrmMappingDo;

    @Autowired
    private GenericDao<SurveyCompanyMapping, Long> surveyCompanyMappingDao;

    @Autowired
    private UsercountModificationNotificationDao usercountModificationNotificationDao;

    @Autowired
    private SurveyDetailsDao surveyDetailsDao;

    @Autowired
    private Utils utils;

    @Autowired
    private Payment gateway;

    @Autowired
    private EmailFormatHelper emailFormatHelper;

    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private ImageProcessor imageProcessor;

    @Autowired
    private Payment payment;

    @Autowired
    private GenericDao<LicenseDetail, Long> licenseDetailDao;

    @Autowired
    private GenericDao<RetriedTransaction, Long> retriedTransactionDao;

    @Autowired
    private GenericDao<CompanyHiddenNotification, Long> companyHiddenNotificationDao;

    @Autowired
    GenericDao<UploadStatus, Long> uploadStatusDao;

    @Autowired
    private EncryptionHelper encryptionHelper;

    @Autowired
    private SurveyBuilder surveyBuilder;
    
    @Autowired
    private SurveyHandler surveyHandler;


    @Autowired
    private RedisDao redisDao;

    @Autowired
    @Qualifier("genericReporting")
    private GenericReportingDao<CompanyStatistics,Long> genericReportingDao;

    @Autowired
    private GenericDao<UserVo, Long> genericDao;

    @Value ( "${HAPPY_TEXT}")
    private String happyText;
    @Value ( "${NEUTRAL_TEXT}")
    private String neutralText;
    @Value ( "${SAD_TEXT}")
    private String sadText;

    @Value ( "${HAPPY_TEXT_COMPLETE}")
    private String happyTextComplete;
    @Value ( "${NEUTRAL_TEXT_COMPLETE}")
    private String neutralTextComplete;
    @Value ( "${SAD_TEXT_COMPLETE}")
    private String sadTextComplete;
    
    @Value ( "${PARAM_ORDER_TAKE_SURVEY}")
    String paramOrderTakeSurvey;
    @Value ( "${PARAM_ORDER_TAKE_SURVEY_CUSTOMER}")
    String paramOrderTakeSurveyCustomer;
    @Value ( "${PARAM_ORDER_TAKE_SURVEY_REMINDER}")
    String paramOrderTakeSurveyReminder;
    @Value ( "${PARAM_ORDER_SURVEY_COPLETION_MAIL}")
    String paramOrderSurveyCompletionMail;
    @Value ( "${PARAM_ORDER_SOCIAL_POST_REMINDER}")
    String paramOrderSocialPostReminder;
    @Value ( "${PARAM_ORDER_INCOMPLETE_SURVEY_REMINDER}")
    String paramOrderIncompleteSurveyReminder;
    @Value ( "${PARAM_ORDER_SURVEY_COMPLETION_UNPLEASANT_MAIL}")
    String paramOrderSurveyCompletionUnpleasantMail;
    @Value ( "${ACCOUNT_PERM_DELETE_SPAN}")
    String accountPermDeleteSpan;


    @Value ( "${APPLICATION_SUPPORT_EMAIL}")
    private String applicationSupportEmail;

    @Value ( "${APPLICATION_SUPPORT_NAME}")
    private String applicationSupportName;

    @Value ( "${CDN_PATH}")
    String cdnPath;
    
    @Value ( "${TOKEN_REFRESH_INTERVAL}")
    private int tokenRefreshInterval;
    

    @Value ( "${SOCIAL_MONITOR_DEFAULT_KEYWORDS}")
    private String socialMonitorDefaultKeywords;

    @Autowired
    private ProfileCompletionList profileCompletionList;

    @Autowired
    private UtilityService utilityService;

    @Autowired
    private SettingsLocker settingsLocker;

    @Autowired
    private SettingsSetter settingsSetter;

    @Autowired
    private GenericDao<CollectionDotloopProfileMapping, Long> collectionDotloopProfileMappingDao;

    @Autowired
    private GenericDao<LoopProfileMapping, Long> loopProfileMappingDao;

    @Autowired
    private ZillowHierarchyDao zillowHierarchyDao;

    @Value ( "${BATCH_SIZE}")
    private int pageSize;

    @Autowired
    private ZillowUpdateService zillowUpdateService;

    @Autowired
    private SocialManagementService socialManagementService;

    @Autowired
    private BatchTrackerService batchTrackerService;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private GenericDao<Event, Long> eventDao;

    @Autowired
    private WorkbookOperations workbookOperations;

    @Autowired
    private WorkbookData workbookData;
    
    @Autowired
    private SocialMediaExceptionHandler socialMediaExceptionHandler;
    
    @Autowired
    private SocialFeedService socialFeedService;
    
    @Autowired
    private SearchEngineManagementServices searchEngineManagementServices;
    
    @Autowired
    private MongoApplicationSettingsDao mongoApplicationSettingsDao;
    
    @Autowired
    private DashboardService dashboardServiceImpl;
    
    private List<String> keyList = null;

    /**
     * This method adds a new company and updates the same for current user and all its user
     * profiles.
     * 
     * @throws SolrException
     * @throws InvalidInputException
     */
    @Override
    @Transactional ( rollbackFor = { NonFatalException.class, FatalException.class })
    public User addCompanyInformation( User user, Map<String, String> organizationalDetails )
        throws SolrException, InvalidInputException
    {
        LOG.debug( "Method addCompanyInformation started for user " + user.getLoginName() );
        Company company = addCompany( user, organizationalDetails.get( CommonConstants.COMPANY_NAME ),
            CommonConstants.STATUS_ACTIVE, organizationalDetails.get( CommonConstants.VERTICAL ),
            organizationalDetails.get( CommonConstants.BILLING_MODE_COLUMN ) );

        LOG.debug( "Calling method for updating company of user" );
        updateCompanyForUser( user, company );

        LOG.debug( "Calling method for updating company for user profiles" );
        updateCompanyForUserProfile( user, company );

        LOG.debug( "Calling method for adding organizational details" );
        addOrganizationalDetails( user, company, organizationalDetails );

        // update vertical in mongo
        AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( user.getUserId() );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings( KEY_VERTICAL,
            organizationalDetails.get( CommonConstants.VERTICAL ), agentSettings );

        LOG.debug( "Method addCompanyInformation finished for user " + user.getLoginName() );
        return user;
    }


    // JIRA: SS-28: By RM05: BOC
    /*
     * To add account as per the choice of User.
     */
    @Override
    @Transactional ( rollbackFor = { NonFatalException.class, FatalException.class })
    public AccountType addAccountTypeForCompany( User user, String strAccountType ) throws InvalidInputException, SolrException
    {
        LOG.debug( "Method addAccountTypeForCompany started for user : " + user.getLoginName() );
        if ( strAccountType == null || strAccountType.isEmpty() ) {
            throw new InvalidInputException( "account type is null or empty while adding account type fro company" );
        }
        int accountTypeValue = 0;
        try {
            accountTypeValue = Integer.parseInt( strAccountType );
        } catch ( NumberFormatException e ) {
            LOG.error( "NumberFormatException for account type :" + strAccountType );
            throw new InvalidInputException( "account type is not valid while adding account type fro company" );
        }
        AccountType accountType = AccountType.getAccountType( accountTypeValue );

        LOG.debug( "Creating default hierarchy and user profiles for the selected account type :" + accountType.getName() );
        createDefaultHierarchy( user, accountType );
        LOG.debug( "Successfully created default hierarchy and user profiles" );

        user = userManagementService.getUserByUserId( user.getUserId() );
        userManagementService.setProfilesOfUser( user );
        solrSearchService.addUserToSolr( user );

        LOG.debug( "Method addAccountTypeForCompany finished." );
        return accountType;
    }


    /**
     * Method to add default branch/region/user profiles for a user and account type
     * 
     * @param user
     * @param accountType
     * @throws InvalidInputException
     * @throws SolrException
     */
    void createDefaultHierarchy( User user, AccountType accountType ) throws InvalidInputException, SolrException
    {
        LOG.debug( "Method createDefaultHierarchy started for user : " + user.getLoginName() );

        LOG.debug( "Adding the default region" );
        Region region = addNewRegion( user, CommonConstants.DEFAULT_REGION_NAME, CommonConstants.YES, null, null, null, null,
            null, null, null );

        ProfilesMaster profilesMaster = userManagementService
            .getProfilesMasterById( CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID );

        //user profile for region admin will not be primary
        LOG.debug( "Creating user profile for region admin" );
        UserProfile userProfileRegionAdmin = userManagementService.createUserProfile( user, user.getCompany(),
            user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID, CommonConstants.DEFAULT_BRANCH_ID, region.getRegionId(),
            profilesMaster.getProfileId(), CommonConstants.IS_PRIMARY_FALSE, CommonConstants.PROFILE_STAGES_COMPLETE,
            CommonConstants.STATUS_ACTIVE, String.valueOf( user.getUserId() ), String.valueOf( user.getUserId() ) );
        userProfileDao.save( userProfileRegionAdmin );

        LOG.debug( "Adding the default branch" );
        Branch branch = addNewBranch( user, region.getRegionId(), CommonConstants.YES, CommonConstants.DEFAULT_BRANCH_NAME,
            null, null, null, null, null, null, null );
        profilesMaster = userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID );

        LOG.debug( "Creating user profile for branch admin" );
        //these cases will be applicable for only new users
        //in case of enterprise account make branch admin profile as isPrimary 
        //and in case of individual account make agent profile as isPrimary
        int isPrimaryprofile;
        if ( accountType == AccountType.ENTERPRISE ) {
            isPrimaryprofile = CommonConstants.IS_PRIMARY_TRUE;
        } else {
            isPrimaryprofile = CommonConstants.IS_PRIMARY_FALSE;
        }

        UserProfile userProfileBranchAdmin = userManagementService.createUserProfile( user, user.getCompany(),
            user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID, branch.getBranchId(), region.getRegionId(),
            profilesMaster.getProfileId(), isPrimaryprofile, CommonConstants.PROFILE_STAGES_COMPLETE,
            CommonConstants.STATUS_ACTIVE, String.valueOf( user.getUserId() ), String.valueOf( user.getUserId() ) );
        userProfileDao.save( userProfileBranchAdmin );

        /**
         * For an individual, a default agent profile is created
         */
        if ( accountType == AccountType.INDIVIDUAL || accountType == AccountType.FREE ) {
            profilesMaster = userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID );

            LOG.debug( "Creating user profile for agent" );
            UserProfile userProfileAgent = userManagementService.createUserProfile( user, user.getCompany(), user.getEmailId(),
                user.getUserId(), branch.getBranchId(), region.getRegionId(), profilesMaster.getProfileId(),
                CommonConstants.IS_PRIMARY_TRUE, CommonConstants.PROFILE_STAGES_COMPLETE, CommonConstants.STATUS_ACTIVE,
                String.valueOf( user.getUserId() ), String.valueOf( user.getUserId() ) );   
            userProfileDao.save( userProfileAgent );

        }

        LOG.debug( "Updating profile stage to payment stage for account type :" + accountType.getName() );
        userManagementService.updateProfileCompletionStage( user, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID,
            CommonConstants.DASHBOARD_STAGE );

        LOG.debug( "Method createDefaultHierarchy finished." );
    }


    // JIRA: SS-28: By RM05: EOC

    /**
     * Fetch the account type master id passing
     * 
     * @author RM-06
     * @param company
     * @return account master id
     */
    @Override
    @Transactional
    public long fetchAccountTypeMasterIdForCompany( Company company ) throws InvalidInputException
    {

        LOG.debug( "Fetch account type for company :" + company.getCompany() );

        List<LicenseDetail> licenseDetails = licenceDetailDao.findByColumn( LicenseDetail.class, CommonConstants.COMPANY,
            company );
        if ( licenseDetails == null || licenseDetails.isEmpty() ) {
            LOG.error( "No license object present for the company : " + company.getCompany() );
            return 0;
        }
        LOG.debug( "Successfully fetched the License detail for the current user's company" );

        // return the account type master ID
        return licenseDetails.get( 0 ).getAccountsMaster().getAccountsMasterId();
    };


    /*
     * This method adds a new company into the COMPANY table.
     */
    Company addCompany( User user, String companyName, int isRegistrationComplete, String vertical, String billingMode )
    {
        LOG.debug( "Method addCompany started for user " + user.getLoginName() );
        Company company = new Company();
        company.setCompany( companyName );
        company.setIsRegistrationComplete( isRegistrationComplete );
        company.setStatus( CommonConstants.STATUS_ACTIVE );
        company.setBillingMode( billingMode );
        // We fetch the vertical and set it
        VerticalsMaster verticalsMaster = verticalMastersDao
            .findByColumn( VerticalsMaster.class, CommonConstants.VERTICALS_MASTER_NAME_COLUMN, vertical )
            .get( CommonConstants.INITIAL_INDEX );
        company.setVerticalsMaster( verticalsMaster );
        //remove this code or remove hard coded status
        company.setSettingsLockStatus( "0" );
        company.setSettingsSetStatus( "0" );
        company.setCreatedBy( String.valueOf( user.getUserId() ) );
        company.setModifiedBy( String.valueOf( user.getUserId() ) );
        company.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
        company.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        LOG.debug( "Method addCompany finished." );
        return companyDao.save( company );
    }


    /**
     * This method updates company details for current user
     * 
     * @param user
     * @param company
     * @return
     */
    User updateCompanyForUser( User user, Company company )
    {
        LOG.debug( "Method updateCompanyForUser started for user " + user.getLoginName() );
        user.setCompany( company );
        user.setIsOwner( CommonConstants.IS_OWNER );
        userDao.update( user );
        LOG.debug( "Method updateCompanyForUser finished for user " + user.getLoginName() );
        return user;
    }


    /**
     * This method updates company details in all the user profiles of current user.
     * 
     * @param user
     * @param company
     */
    void updateCompanyForUserProfile( User user, Company company )
    {
        LOG.debug( "Method updateCompanyForUserProfile started for user " + user.getLoginName() );
        user = userDao.findById( User.class, user.getUserId() );

        List<UserProfile> userProfiles = user.getUserProfiles();
        if ( userProfiles != null ) {
            for ( UserProfile userProfile : userProfiles ) {
                userProfile.setCompany( company );
                userProfileDao.update( userProfile );
            }
        } else {
            LOG.warn( "No profiles found for user : " + user.getUserId() );
        }
        LOG.debug( "Method updateCompanyForUserProfile finished for user " + user.getLoginName() );
    }


    /**
     * This method adds all the key and value pairs into mongo collection COMPANY_SETTINGS
     * 
     * @param user
     * @param company
     * @param organizationalDetails
     * @throws InvalidInputException
     */
    @Override
    @SuppressWarnings ( "unused")
    public void addOrganizationalDetails( User user, Company company, Map<String, String> organizationalDetails )
        throws InvalidInputException
    {
        LOG.debug( "Method addOrganizationalDetails called." );

        // create a organization settings object
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        LockSettings lockSettings = new LockSettings();
        companySettings.setIden( company.getCompanyId() );
        companySettings.setCompanyId( company.getCompanyId() );
        if ( organizationalDetails.get( CommonConstants.LOGO_NAME ) != null ) {
            companySettings.setLogo( organizationalDetails.get( CommonConstants.LOGO_NAME ) );
            companySettings.setLogoThumbnail( organizationalDetails.get( CommonConstants.LOGO_NAME ) );
            companySettings.setLogoImageProcessed( false );
            try {
                settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.LOGO, true );
                settingsLocker.lockSettingsValueForCompany( company, SettingsForApplication.LOGO, true );
            } catch ( NonFatalException e ) {
                LOG.error( "Exception Caught " + e.getMessage() );
            }
            lockSettings.setLogoLocked( true );
        }

        companySettings.setLockSettings( lockSettings );
        ContactDetailsSettings contactDetailSettings = new ContactDetailsSettings();
        contactDetailSettings.setName( company.getCompany() );
        if ( organizationalDetails.get( CommonConstants.ADDRESS ) != null ) {
            try {
                settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.ADDRESS, true );
            } catch ( NonFatalException e ) {
                LOG.error( "Exception Caught " + e.getMessage() );
            }
            contactDetailSettings.setAddress( organizationalDetails.get( CommonConstants.ADDRESS ) );
        }
        contactDetailSettings.setAddress1( organizationalDetails.get( CommonConstants.ADDRESS1 ) );
        contactDetailSettings.setAddress2( organizationalDetails.get( CommonConstants.ADDRESS2 ) );
        contactDetailSettings.setZipcode( organizationalDetails.get( CommonConstants.ZIPCODE ) );
        contactDetailSettings.setCountry( organizationalDetails.get( CommonConstants.COUNTRY ) );
        contactDetailSettings.setCountryCode( organizationalDetails.get( CommonConstants.COUNTRY_CODE ) );
        contactDetailSettings.setState( organizationalDetails.get( CommonConstants.STATE ) );
        contactDetailSettings.setCity( organizationalDetails.get( CommonConstants.CITY ) );
      
        // Add work phone number in contact details
        ContactNumberSettings contactNumberSettings = new ContactNumberSettings();
        if ( organizationalDetails.get( CommonConstants.COMPANY_CONTACT_NUMBER ) != null ) {
            contactNumberSettings.setWork( organizationalDetails.get( CommonConstants.COMPANY_CONTACT_NUMBER ) );
            try {
                settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.PHONE, true );
            } catch ( NonFatalException e ) {
                LOG.error( "Exception Caught " + e.getMessage() );
            }
        }
        contactDetailSettings.setContact_numbers( contactNumberSettings );

        // Add work Mail id in contact details
        MailIdSettings mailIdSettings = new MailIdSettings();
        mailIdSettings.setWork( user.getEmailId() );
        contactDetailSettings.setMail_ids( mailIdSettings );
        
        //set Geo Location
        companySettings.setGeoLocation(searchEngineManagementServices.getGeoLocForSettings(contactDetailSettings));

        companySettings.setUniqueIdentifier( organizationalDetails.get( CommonConstants.UNIQUE_IDENTIFIER ) );
        companySettings.setVertical( organizationalDetails.get( CommonConstants.VERTICAL ) );
        companySettings.setContact_details( contactDetailSettings );
        companySettings.setProfileName( generateProfileNameForCompany( company.getCompany(), company.getCompanyId() ) );
        companySettings.setProfileUrl( CommonConstants.FILE_SEPARATOR + companySettings.getProfileName() );
        companySettings.setCreatedOn( System.currentTimeMillis() );
        companySettings.setCreatedBy( String.valueOf( user.getUserId() ) );
        companySettings.setModifiedOn( System.currentTimeMillis() );
        companySettings.setModifiedBy( String.valueOf( user.getUserId() ) );


        // Adding default text for various flows of survey.
        SurveySettings surveySettings = new SurveySettings();
        surveySettings.setHappyText( happyText );
        surveySettings.setNeutralText( neutralText );
        surveySettings.setSadText( sadText );
        surveySettings.setHappyTextComplete( happyTextComplete );
        surveySettings.setNeutralTextComplete( neutralTextComplete );
        surveySettings.setSadTextComplete( sadTextComplete );
        surveySettings.setAutoPostEnabled( true );
        surveySettings.setShow_survey_above_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
        surveySettings.setAuto_post_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );

        surveySettings.setSurvey_reminder_interval_in_days( CommonConstants.DEFAULT_REMINDERMAIL_INTERVAL );
        surveySettings.setMax_number_of_survey_reminders( CommonConstants.DEFAULT_MAX_REMINDER_COUNT );

        surveySettings.setSocial_post_reminder_interval_in_days( CommonConstants.DEFAULT_SOCIAL_POST_REMINDERMAIL_INTERVAL );
        surveySettings.setMax_number_of_social_pos_reminders( CommonConstants.DEFAULT_MAX_SOCIAL_POST_REMINDER_COUNT );

        companySettings.setSurvey_settings( surveySettings );

        //set Ranking Settings
        RankingRequirements rankingRequirements = new RankingRequirements();
        rankingRequirements.setMinCompletedPercentage( CommonConstants.MIN_COMPLETED_PERCENTAGE );
        rankingRequirements.setMinDaysOfRegistration( CommonConstants.MIN_DAYS_OF_REGISTRATION );
        rankingRequirements.setMinNoOfReviews( CommonConstants.MIN_NO_OF_REVIEWS );
        rankingRequirements.setMonthOffset( CommonConstants.MONTH_OFFSET );
        rankingRequirements.setYearOffset( CommonConstants.YEAR_OFFSET );

        companySettings.setRankingRequirements( rankingRequirements );

        // set seo content flag
        companySettings.setSeoContentModified( true );

        // set default profile stages.
        companySettings.setProfileStages( profileCompletionList.getDefaultProfileCompletionList( false ) );
        
        //adding swear words list to a newly created company 
        companySettings.setSwearWords( surveyHandler.getSwearList() );

        // Setting default values for mail content in Mail content settings of company settings.
        String takeSurveyMail = "";
        String takeSurveyReminderMail = "";
        String takeSurveyByCustomerMail = "";
        String surveyCompletionMail = "";
        String socialPostReminderMail = "";
        String incompleteSurveyReminderMail = "";

        String takeSurveyMailSubj = "";
        String takeSurveyReminderMailSubj = "";
        String takeSurveyByCustomerMailSubj = "";
        String surveyCompletionMailSubj = "";
        String socialPostReminderMailSubj = "";
        String incompleteSurveyReminderMailSubj = "";
        try {
            takeSurveyMail = readMailContentFromFile( CommonConstants.SURVEY_REQUEST_MAIL_FILENAME );
            takeSurveyByCustomerMail = readMailContentFromFile( CommonConstants.SURVEY_CUSTOMER_REQUEST_MAIL_FILENAME );
            takeSurveyReminderMail = readMailContentFromFile( CommonConstants.SURVEY_REMINDER_MAIL_FILENAME );
            surveyCompletionMail = readMailContentFromFile( CommonConstants.SURVEY_COMPLETION_MAIL_FILENAME );
            socialPostReminderMail = readMailContentFromFile( CommonConstants.SOCIAL_POST_REMINDER_MAIL_FILENAME );
            incompleteSurveyReminderMail = readMailContentFromFile( CommonConstants.RESTART_SURVEY_MAIL_FILENAME );

            takeSurveyMailSubj = CommonConstants.SURVEY_MAIL_SUBJECT + "[AgentName]";
            takeSurveyByCustomerMailSubj = CommonConstants.SURVEY_MAIL_SUBJECT_CUSTOMER;
            takeSurveyReminderMailSubj = CommonConstants.REMINDER_MAIL_SUBJECT;
            surveyCompletionMailSubj = CommonConstants.SURVEY_COMPLETION_MAIL_SUBJECT;
            socialPostReminderMailSubj = CommonConstants.SOCIAL_POST_REMINDER_MAIL_SUBJECT;
            incompleteSurveyReminderMailSubj = CommonConstants.RESTART_SURVEY_MAIL_SUBJECT;
        } catch ( IOException e ) {
            LOG.error(
                "IOException occured in addOrganizationalDetails while copying default Email content. Nested exception is ",
                e );
        }

        MailContentSettings mailContentSettings = new MailContentSettings();
        companySettings.setMail_content( mailContentSettings );
        companySettings.setShowSummitPopup( false );

        LOG.debug( "Inserting company settings." );
        OrganizationUnitSettings oldCompanySettings = null;
        if ( companySettings.getUniqueIdentifier() != null && !companySettings.getUniqueIdentifier().isEmpty() ) {
            oldCompanySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByUniqueIdentifier(
                companySettings.getUniqueIdentifier(), COMPANY_SETTINGS_COLLECTION );
        }
        if ( oldCompanySettings == null ) {
            organizationUnitSettingsDao.insertOrganizationUnitSettings( companySettings,
                COMPANY_SETTINGS_COLLECTION );
        } else {
            organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByCriteria(
                KEY_CONTACT_DETAIL_SETTINGS, contactDetailSettings,
                KEY_IDENTIFIER, oldCompanySettings.getId(),
                COMPANY_SETTINGS_COLLECTION );
        }

        LOG.debug( "Method addOrganizationalDetails finished" );
    }


    /**
     * JIRA:SS-117 by RM02 Method to generate profile name for a company based on some rules
     * 
     * @param companyName
     * @param iden
     * @return
     * @throws InvalidInputException
     */
    @Override
    public String generateProfileNameForCompany( String companyName, long iden ) throws InvalidInputException
    {
        LOG.debug( "Generating profile name for companyName:" + companyName + " and iden:" + iden );
        String profileName = null;
        if ( companyName == null || companyName.isEmpty() ) {
            throw new InvalidInputException( "Company name is null or empty while generating profile name" );
        }
        // profileName = companyName.replaceAll(" ", "-").toLowerCase();
        profileName = utils.prepareProfileName( companyName );

        LOG.debug( "Checking uniqueness of profile name generated : " + profileName + " by querying mongo" );

        OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName(
            profileName, COMPANY_SETTINGS_COLLECTION );
        /**
         * if there exists a company with the profile name formed, append company iden to get the
         * unique profile name
         */
        if ( companySettings != null ) {

            LOG.debug( "Profile name generated is already taken by a company, appending iden to get a new and unique one" );
            profileName = utils.appendIdenToProfileName( profileName, iden );
        }
        LOG.debug( "Successfully generated profile name. Returning : " + profileName );
        return profileName;

    }


    /**
     * Method to add a new region
     * 
     * @param user
     * @param isDefaultBySystem
     * @param regionName
     * @return
     */
    @Override
    public Region addRegion( User user, int isDefaultBySystem, String regionName )
    {
        LOG.debug( "Method addRegion started for user : " + user.getLoginName() + " isDefaultBySystem : " + isDefaultBySystem
            + " regionName :" + regionName );
        Region region = new Region();
        region.setCompany( user.getCompany() );
        region.setIsDefaultBySystem( isDefaultBySystem );
        region.setStatus( CommonConstants.STATUS_ACTIVE );
        region.setRegion( regionName );
        region.setCreatedBy( String.valueOf( user.getUserId() ) );
        region.setModifiedBy( String.valueOf( user.getUserId() ) );
        region.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
        //TODO remove this code or remove hard coded status
        region.setSettingsLockStatus( "0" );
        region.setSettingsSetStatus( "0" );
        region.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        region = regionDao.save( region );
        LOG.debug( "Method addRegion finished." );
        return region;
    }


    /**
     * Method to add a new Branch
     * 
     * @param user
     * @param region
     * @param branchName
     * @param isDefaultBySystem
     * @return
     */
    @Override
    public Branch addBranch( User user, Region region, String branchName, int isDefaultBySystem )
    {
        LOG.debug( "Method addBranch started for user : " + user.getLoginName() );
        Branch branch = new Branch();
        branch.setCompany( user.getCompany() );
        branch.setRegion( region );
        branch.setStatus( CommonConstants.STATUS_ACTIVE );
        branch.setBranch( branchName );
        branch.setIsDefaultBySystem( isDefaultBySystem );
        branch.setCreatedBy( String.valueOf( user.getUserId() ) );
        branch.setModifiedBy( String.valueOf( user.getUserId() ) );
        branch.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
        branch.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        //TODO remove set lock status from here or remove hard coded status
        branch.setSettingsLockStatus( "0" );
        branch.setSettingsSetStatus( "0" );
        branch = branchDao.save( branch );
        LOG.debug( "Method addBranch finished." );
        return branch;
    }


    @Override
    public void editCompanySettings( User user )
    {
        LOG.debug( "Editing company information by user: " + user.toString() );
    }


    @Override
    public OrganizationUnitSettings getCompanySettings( User user ) throws InvalidInputException
    {
        if ( user == null ) {
            throw new InvalidInputException( "User is not set" );
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "Get company settings for the user: {}", user.toString() );
        }
        // get the company id
        if ( user.getCompany() == null ) {
            LOG.warn( "User object is partially set. Could not find the comany details" );
            throw new InvalidInputException( "User object is partially set. Could not find the comany details" );
        }
        long companyId = user.getCompany().getCompanyId();
        OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( companyId,
            COMPANY_SETTINGS_COLLECTION );

        // Filter profile stages.
        if ( companySettings != null && companySettings.getProfileStages() != null ) {
            companySettings
                .setProfileStages( profileCompletionList.getProfileCompletionList( companySettings.getProfileStages() ) );
        }
        return companySettings;
    }


    @Override
    public OrganizationUnitSettings
    getCompanySettings( long companyId ) throws InvalidInputException
    {

        LOG.debug( "Get company settings for the companyId: {} ", companyId );

        OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( companyId,
            COMPANY_SETTINGS_COLLECTION );

        // Filter profile stages.
        if ( companySettings != null && companySettings.getProfileStages() != null ) {
            companySettings
                .setProfileStages( profileCompletionList.getProfileCompletionList( companySettings.getProfileStages() ) );
        }

        return companySettings;
    }


    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService#addKeyworodsToCompanySettings(long, java.util.List)
     */
    @Override
    public List<Keyword> addKeyworodsToCompanySettings( long companyId, List<Keyword> keywordsToAdd )
        throws InvalidInputException
    {
        LOG.debug( "Get company settings for the companyId: {}", companyId );
        OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( companyId,
            COMPANY_SETTINGS_COLLECTION );

        List<Keyword> companyFilterKeywords = companySettings.getFilterKeywords();
        if ( !CollectionUtils.isEmpty( keywordsToAdd ) ) {
            // setting
            if ( companySettings.getFilterKeywords() == null ) {
                companyFilterKeywords = new ArrayList<Keyword>();
            }

            for ( Keyword keyword : keywordsToAdd ) {

                if ( StringUtils.isEmpty( keyword.getPhrase() ) ) {
                    LOG.warn( "Phrase is empty or null so skipping" );
                    continue;
                    //throw new InvalidInputException( "Phrase can't be empty/blank" );
                }

                int idIndex = -1, phraseIndex = -1;
                for ( int i = 0; i < companyFilterKeywords.size(); i++ ) {
                    Keyword companyFilterKeyword = companyFilterKeywords.get( i );

                    if ( StringUtils.isNotEmpty( keyword.getId() ) ) {
                        if ( companyFilterKeyword.getId() != null && companyFilterKeyword.getId().equals( keyword.getId() ) ) {
                            idIndex = i;
                            // both index found exit loop
                            if ( phraseIndex != -1 ) {
                                break;
                            }
                        }
                    }

                    if ( companyFilterKeyword.getPhrase() != null
                        && companyFilterKeyword.getPhrase().equalsIgnoreCase( keyword.getPhrase() ) ) {
                        phraseIndex = i;
                        // both index found exit loop
                        if ( idIndex != -1 ) {
                            break;
                        }
                    }
                }

                long createdtime = new Date().getTime();
                if ( idIndex != -1 ) {
                    if ( phraseIndex != -1 && phraseIndex != idIndex ) {
                        LOG.warn( "Phrase already exist in keywords" );
                        throw new InvalidInputException( "Phrase already exist in keywords", "400" );
                    } else {
                        LOG.info( "Id found in existing keyword so updateing keyword" );
                        Keyword companyFilterKeyword = companyFilterKeywords.get( idIndex );
                        companyFilterKeyword.setModifiedOn( createdtime );
                        companyFilterKeyword.setPhrase( keyword.getPhrase() );
                        companyFilterKeyword.setMonitorType( keyword.getMonitorType() );
                    }
                } else {

                    if ( StringUtils.isNotEmpty( keyword.getId() ) ) {
                        throw new InvalidInputException( "Keyword does not exist for given Id", "400" );
                    }

                    if ( phraseIndex != -1 ) {
                        LOG.warn( "Phrase already exist in keywords" );
                        throw new InvalidInputException( "Phrase already exist in keywords", "400" );
                    } else {
                        LOG.info( "Adding new keyword" );
                        Keyword keywordNew = new Keyword();
                        keywordNew.setCreatedOn( createdtime );
                        keywordNew.setModifiedOn( createdtime );
                        keywordNew.setPhrase( keyword.getPhrase() );
                        keywordNew.setId( UUID.randomUUID().toString() );
                        keywordNew.setStatus( 1 );
                        keywordNew.setMonitorType( keyword.getMonitorType() );
                        companyFilterKeywords.add( keywordNew );
                    }
                }
            }
            // Updating filterKeywords in OrganizationUnitSettings
            organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
                KEY_FILTER_KEYWORDS, companyFilterKeywords, companySettings,
                COMPANY_SETTINGS_COLLECTION );

            //update the redis with the keywords
            redisDao.addKeywords( companyId, companyFilterKeywords );

        } else {
            LOG.debug( "Keywords are empty so skiping operation" );
        }
        return companyFilterKeywords;
    }


    @Override
    public Map<String, Long> getFacebookAndTwitterLocks( String lockType ) throws InvalidInputException
    {
        LOG.debug( "Method getFacebookAndTwitterLocks() started" );
        if ( lockType != null && !lockType.equalsIgnoreCase( "facebook" ) && !lockType.equalsIgnoreCase( "twitter" ) ) {
            LOG.warn( "" );
            throw new InvalidInputException( "Invalid value passed for lockType", "400" );
        }
        Map<String, Long> resultList = new HashMap<>();
        if ( lockType == null ) {
            resultList.putAll( redisDao.getFacebookLock() );
            resultList.putAll( redisDao.getTwitterLock() );
        } else if ( lockType.equalsIgnoreCase( "facebook" ) ) {
            resultList.putAll( redisDao.getFacebookLock() );
        } else if ( lockType.equalsIgnoreCase( "twitter" ) ) {
            resultList.putAll( redisDao.getTwitterLock() );
        }
        return resultList;

    }


    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService#enableKeyworodForCompanySettings(long, java.lang.String)
     */
    public Keyword enableKeyworodForCompanySettings( long companyId, String keywordId ) throws InvalidInputException
    {
        return updateKeyworodStatusForCompanySettings( companyId, keywordId, 1 );
    }


    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService#disableKeyworodForCompanySettings(long, java.lang.String)
     */
    public Keyword disableKeyworodForCompanySettings( long companyId, String keywordId ) throws InvalidInputException
    {
        return updateKeyworodStatusForCompanySettings( companyId, keywordId, 0 );
    }


    /**
     * Method to update keyword by keyword id and keyword status
     * @param companyId
     * @param keywordId
     * @param status : it can be 0(disabled)/1(enabled)
     * @return : keyword object which status is updated 
     * @throws InvalidInputException
     */
    private Keyword updateKeyworodStatusForCompanySettings( long companyId, String keywordId, int status )
        throws InvalidInputException
    {
        LOG.debug( "Get company settings for the companyId: {}", companyId );
        Keyword keywordToUpdate = null;
        long modifiedOn = new Date().getTime();

        OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( companyId,
            COMPANY_SETTINGS_COLLECTION );
        // setting
        if ( companySettings != null ) {
            List<Keyword> companyFilterKeywords = companySettings.getFilterKeywords();

            if ( !CollectionUtils.isEmpty( companyFilterKeywords ) ) {
                for ( Keyword keyword : companyFilterKeywords ) {
                    if ( keyword.getId().equals( keywordId ) ) {
                        keyword.setStatus( status );
                        keyword.setModifiedOn( modifiedOn );

                        // Update
                        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
                            KEY_FILTER_KEYWORDS, companyFilterKeywords, companySettings,
                            COMPANY_SETTINGS_COLLECTION );

                        keywordToUpdate = keyword;
                        break;
                    }
                }

            } else {
                LOG.error( "Record not found for keyword id {}", keywordId );
            }

        } else {
            LOG.error( "Record not found for company id {}", companyId );
        }

        // Updating filterKeywords in OrganizationUnitSettings
        return keywordToUpdate;
    }


    
    @Override
    public int getKeywordCount(long companyId) throws InvalidInputException{
    	 LOG.debug( "Get company settings for the companyId: {}", companyId );
         OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( companyId,
             COMPANY_SETTINGS_COLLECTION );
         
         if ( companySettings == null ) {
             throw new InvalidInputException( "Company setting doesn't exist for company id", "400" );
         }

         List<Keyword> allKeywords = companySettings.getFilterKeywords();
         List<Keyword> companyFilterKeywords = new ArrayList<>();
         
         if ( allKeywords != null && !allKeywords.isEmpty() ) {
             for ( Keyword keyword : allKeywords ) { 
            	 if ( keyword.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                     companyFilterKeywords.add( keyword );
                 }
             }
         }
         
    	return companyFilterKeywords.size();
    }
    
    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService#getCompanyKeywordsByCompanyId(long)
     */
    @Override
    public FilterKeywordsResponse getCompanyKeywordsByCompanyId( long companyId, int startIndex, int limit, String monitorType, String searchPhrase )
        throws InvalidInputException
    {
        LOG.debug( "Get company settings for the companyId: {}", companyId );
        OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( companyId,
            COMPANY_SETTINGS_COLLECTION );

        if ( companySettings == null ) {
            throw new InvalidInputException( "Company setting doesn't exist for company id", "400" );
        }

        List<Keyword> allKeywords = companySettings.getFilterKeywords();
        List<Keyword> companyFilterKeywords = new ArrayList<>();
        if ( allKeywords != null && !allKeywords.isEmpty() ) {
            for ( Keyword keyword : allKeywords ) { 
                if(searchPhrase != null && !searchPhrase.isEmpty()) {
                    if(keyword.getPhrase().toLowerCase().contains( searchPhrase.trim().toLowerCase() ) && keyword.getStatus() == CommonConstants.STATUS_ACTIVE) {
                        companyFilterKeywords.add( keyword );
                    }
                }
                else if ( keyword.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                    companyFilterKeywords.add( keyword );
                }
            }
        }
        List<Keyword> keywords = new ArrayList<>();
        FilterKeywordsResponse filterKeywordsResponse = new FilterKeywordsResponse();
        if ( companyFilterKeywords != null && !companyFilterKeywords.isEmpty() ) {
            Collections.sort( companyFilterKeywords, new FilterKeywordsComparator() );

            if ( monitorType == null || monitorType.isEmpty() ) {
                keywords = companyFilterKeywords;
                filterKeywordsResponse.setMonitorType( "ALL" );
            } else {
                for ( Keyword keyword : companyFilterKeywords ) {
                    if ( monitorType.equalsIgnoreCase( keyword.getMonitorType().toString() ) ) {
                        keywords.add( keyword );
                    }
                }
                filterKeywordsResponse.setMonitorType( monitorType.toUpperCase() );
            }
            
            //If limit is -1, it means that we should be sending *ALL* keywords
            if(limit == -1){
                //Setting limit to total keywords count
                limit = keywords.size();
            }
            
            filterKeywordsResponse.setCount( keywords.size() );
            if ( startIndex >= keywords.size() ) {
                filterKeywordsResponse.setMonitorType( null );
                filterKeywordsResponse.setCount( 0 );
                filterKeywordsResponse.setFilterKeywords( null );
            } else if ( limit >= keywords.size() && startIndex < keywords.size() ) {
                filterKeywordsResponse.setFilterKeywords( keywords.subList( startIndex, keywords.size() ) );
            } else {
                if ( startIndex + limit >= keywords.size() ) {
                    filterKeywordsResponse.setFilterKeywords( keywords.subList( startIndex, keywords.size() ) );
                } else {
                    filterKeywordsResponse.setFilterKeywords( keywords.subList( startIndex, startIndex + limit ) );
                }
            }
        }
        return filterKeywordsResponse;
    }


    @Override
    public Map<Long, OrganizationUnitSettings> getRegionSettingsForUserProfiles( List<UserProfile> userProfiles )
        throws InvalidInputException
    {
        Map<Long, OrganizationUnitSettings> regionSettings = null;
        if ( userProfiles != null && userProfiles.size() > 0 ) {
            LOG.debug( "Get region settings for the user profiles: " + userProfiles.toString() );
            regionSettings = new HashMap<Long, OrganizationUnitSettings>();
            OrganizationUnitSettings regionSetting = null;
            // get the region profiles and get the settings for each of them.
            for ( UserProfile userProfile : userProfiles ) {
                regionSetting = new OrganizationUnitSettings();
                if ( userProfile.getProfilesMaster()
                    .getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                    LOG.debug( "Getting settings for " + userProfile );
                    // get the region id and get the profile
                    if ( userProfile.getRegionId() > 0l ) {
                        regionSetting = getRegionSettings( userProfile.getRegionId() );
                        if ( regionSetting != null ) {
                            regionSettings.put( userProfile.getRegionId(), regionSetting );
                        }
                    } else {
                        LOG.warn( "Not a valid region id for region profile: " + userProfile + ". Skipping the record" );
                    }
                }
            }
        } else {
            throw new InvalidInputException( "User profiles are not set" );
        }

        return regionSettings;
    }


    @Override
    public Map<Long, OrganizationUnitSettings> getBranchSettingsForUserProfiles( List<UserProfile> userProfiles )
        throws InvalidInputException, NoRecordsFetchedException
    {
        Map<Long, OrganizationUnitSettings> branchSettings = null;
        if ( userProfiles != null && userProfiles.size() > 0 ) {
            LOG.debug( "Get branch settings for the user profiles: " + userProfiles.toString() );
            branchSettings = new HashMap<Long, OrganizationUnitSettings>();
            BranchSettings branchSetting = null;
            // get the branch profiles and get the settings for each of them.
            for ( UserProfile userProfile : userProfiles ) {
                branchSetting = new BranchSettings();
                if ( userProfile.getProfilesMaster()
                    .getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                    LOG.debug( "Getting settings for " + userProfile );
                    // get the branch id and get the profile
                    if ( userProfile.getBranchId() > 0l ) {
                        branchSetting = getBranchSettings( userProfile.getBranchId() );
                        if ( branchSetting != null && branchSetting.getOrganizationUnitSettings() != null ) {
                            branchSettings.put( userProfile.getBranchId(), branchSetting.getOrganizationUnitSettings() );
                        }
                    } else {
                        LOG.warn( "Not a valid branch id for branch profile: " + userProfile + ". Skipping the record" );
                    }
                }
            }
        } else {
            throw new InvalidInputException( "User profiles are not set" );
        }

        return branchSettings;
    }


    @Override
    public OrganizationUnitSettings getRegionSettings( long regionId ) throws InvalidInputException
    {
        OrganizationUnitSettings regionSettings = null;
        if ( regionId <= 0l ) {
            LOG.warn( "Invalid region id. : {}", regionId );
            throw new InvalidInputException( "Invalid region id. : " + regionId );
        }
        LOG.debug( "Get the region settings for region id: " + regionId );
        regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( regionId,
            REGION_SETTINGS_COLLECTION );

        // Filter profile stages.
        if ( regionSettings != null && regionSettings.getProfileStages() != null ) {
            regionSettings
                .setProfileStages( profileCompletionList.getProfileCompletionList( regionSettings.getProfileStages() ) );
        }

        return regionSettings;
    }


    /**
     * Method to fetch branch settings along with the required region settings of region to which
     * the branch belongs
     */
    @Transactional
    @Override
    public BranchSettings getBranchSettings( long branchId ) throws InvalidInputException, NoRecordsFetchedException
    {
        OrganizationUnitSettings organizationUnitSettings = null;
        BranchSettings branchSettings = null;
        if ( branchId <= 0l ) {
            throw new InvalidInputException( "Invalid branch id. :" + branchId );
        }
        LOG.debug( "Get the branch settings for branch id: " + branchId );
        organizationUnitSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( branchId,
            BRANCH_SETTINGS_COLLECTION );

        // Filter profile stages.
        if ( organizationUnitSettings != null && organizationUnitSettings.getProfileStages() != null ) {
            organizationUnitSettings.setProfileStages(
                profileCompletionList.getProfileCompletionList( organizationUnitSettings.getProfileStages() ) );
        }

        branchSettings = new BranchSettings();
        branchSettings.setOrganizationUnitSettings( organizationUnitSettings );

        Branch branch = branchDao.findById( Branch.class, branchId );
        if ( branch == null ) {
            throw new NoRecordsFetchedException( "No branch present in db for branchId : " + branchId );
        }
        long regionId = branch.getRegion().getRegionId();

        if ( branch.getRegion().getIsDefaultBySystem() != CommonConstants.YES ) {
            LOG.debug( "fetching region settings for regionId : " + regionId );
            branchSettings.setRegionId( regionId );
            branchSettings.setRegionName( branch.getRegion().getRegion() );
        } else {
            branchSettings.setRegionId( regionId );
            branchSettings.setRegionName( branch.getRegion().getRegion() );
            LOG.debug( "Branch belongs to default region" );
        }

        LOG.debug( "Successfully fetched the branch settings for branch id: " + branchId + " returning : " + branchSettings );
        return branchSettings;
    }


    @Transactional
    @Override
    public OrganizationUnitSettings getBranchSettingsDefault( long branchId )
        throws InvalidInputException, NoRecordsFetchedException
    {
        OrganizationUnitSettings organizationUnitSettings = null;
        BranchSettings branchSettings = null;
        if ( branchId <= 0l ) {
            LOG.warn( "Invalid branch id. : {}", branchId );
            throw new InvalidInputException( "Invalid branch id. :" + branchId );
        }
        LOG.debug( "Get the branch settings for branch id: {}", branchId );
        organizationUnitSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( branchId,
            BRANCH_SETTINGS_COLLECTION );

        // Filter profile stages.
        if ( organizationUnitSettings != null && organizationUnitSettings.getProfileStages() != null ) {
            organizationUnitSettings.setProfileStages(
                profileCompletionList.getProfileCompletionList( organizationUnitSettings.getProfileStages() ) );
        }

        LOG.debug( "Successfully fetched the branch settings for branch id: {} returning : {}", branchId, branchSettings );
        return organizationUnitSettings;
    }
    

    @Override
    public SocialMediaStatusVO getProfileUrlAndStatus( String socialNetwork, long entityId, String entityType )
        throws InvalidInputException, NoRecordsFetchedException
    {
        SocialMediaStatusVO socialMediaStatus = new SocialMediaStatusVO();

        // Check for the collection to update
        OrganizationUnitSettings unitSettings = null;
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            unitSettings = getCompanySettings( entityId );
        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            unitSettings = getRegionSettings( entityId );
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            unitSettings = getBranchSettingsDefault( entityId );
        } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
            unitSettings = userManagementService.getUserSettings( entityId );
        }

        if ( unitSettings == null || unitSettings.getSocialMediaTokens() == null ) {
            LOG.info( "returning empty Object for entityType:{}, entityId: {}", entityType, entityId );
            return socialMediaStatus;
        }

        socialMediaStatus = getProfileFromUnitSettingBySocialNetwork( unitSettings.getSocialMediaTokens(), socialNetwork );

        LOG.info("Method getProfileUrl() finished from SocialManagementController, returnning -  {} for entityType:{}, entityId: {}",
            socialMediaStatus, entityType, entityId );
        return socialMediaStatus;
    }
    
    
    /**
     * Method to 
     * @param socialMediaTokens
     * @param socialNetwork
     * @return
     */
    @Override
    public SocialMediaStatusVO getProfileFromUnitSettingBySocialNetwork( SocialMediaTokens socialMediaTokens,
        String socialNetwork )
    {
        SocialMediaStatusVO socialMediaStatus = new SocialMediaStatusVO();
        if ( socialNetwork.equalsIgnoreCase( "facebook" ) && socialMediaTokens.getFacebookToken() != null
            && socialMediaTokens.getFacebookToken().getFacebookPageLink() != null ) {
            socialMediaStatus.setUrl( socialMediaTokens.getFacebookToken().getFacebookPageLink() );
            socialMediaStatus.setConnected( true );
        } else if ( socialNetwork.equalsIgnoreCase( "twitter" )
            && socialMediaTokens.getTwitterToken().getTwitterPageLink() != null ) {
            socialMediaStatus.setUrl( socialMediaTokens.getTwitterToken().getTwitterPageLink() );
            socialMediaStatus.setConnected( true );
        } else if ( socialNetwork.equals( "linkedin" ) ) {

            socialMediaStatus.setUrl( socialMediaTokens.getLinkedInProfileUrl() );
            if ( socialMediaTokens.getLinkedInV2Token() == null ) {
                return socialMediaStatus;
            }

            if ( StringUtils.isNotEmpty( socialMediaTokens.getLinkedInV2Token().getLinkedInPageLink() ) ) {
                socialMediaStatus.setUrl( socialMediaTokens.getLinkedInV2Token().getLinkedInPageLink() );
            }
            if ( StringUtils.isNotEmpty( socialMediaTokens.getLinkedInV2Token().getLinkedInAccessToken() ) ) {
                socialMediaStatus.setConnected( true );
            }

        } else if ( socialNetwork.equalsIgnoreCase( "zillow" ) && socialMediaTokens.getZillowToken() != null
            && socialMediaTokens.getZillowToken().getZillowProfileLink() != null ) {
            socialMediaStatus.setUrl( socialMediaTokens.getZillowToken().getZillowProfileLink() );
            socialMediaStatus.setConnected( true );
        } else if ( socialNetwork.equalsIgnoreCase( "instagram" ) && socialMediaTokens.getInstagramToken() != null
            && socialMediaTokens.getInstagramToken().getPageLink() != null ) {
            socialMediaStatus.setUrl( socialMediaTokens.getInstagramToken().getPageLink() );
            socialMediaStatus.setConnected( true );
        }
        return socialMediaStatus;
    }


    @Transactional
    @Override
    public AgentSettings getAgentSettings( long agentId ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Getting agent settings for id: " + agentId );
        AgentSettings agentSettings = null;
        if ( agentId <= 0l ) {
            LOG.error( "Agent id is not passed to fetch the agent settings" );
            throw new InvalidInputException( "Agent id is not passed to fetch the agent settings" );
        }
        agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( agentId );
        if ( agentSettings == null ) {
            LOG.error( "Could not find agent settings for id: " + agentId );
            throw new NoRecordsFetchedException( "Could not find agent settings for id: " + agentId );
        }
        return agentSettings;
    }


    @Override
    public void updateCRMDetails( OrganizationUnitSettings companySettings, CRMInfo crmInfo, String fullyQualifiedClass )
        throws InvalidInputException
    {
        if ( companySettings == null ) {
            throw new InvalidInputException( "Company settings cannot be null." );
        }
        if ( crmInfo == null ) {
            throw new InvalidInputException( "CRM info cannot be null." );
        }
        LOG.debug( "Updating comapnySettings: " + companySettings + " with crm info: " + crmInfo );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            KEY_CRM_INFO, crmInfo, companySettings,
            COMPANY_SETTINGS_COLLECTION );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            KEY_CRM_INFO_CLASS, fullyQualifiedClass, companySettings,
            COMPANY_SETTINGS_COLLECTION );
        LOG.debug( "Updated the record successfully" );
    }


    @Override
    public void updateCRMDetailsForAnyUnitSettings( OrganizationUnitSettings unitSettings, String collectionName,
        CRMInfo crmInfo, String fullyQualifiedClass ) throws InvalidInputException
    {
        if ( unitSettings == null ) {
            throw new InvalidInputException( "Unit settings cannot be null." );
        }
        if ( crmInfo == null ) {
            throw new InvalidInputException( "CRM info cannot be null." );
        }
        LOG.debug( "Updating unitSettings: " + unitSettings + " with crm info: " + crmInfo );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            KEY_CRM_INFO, crmInfo, unitSettings, collectionName );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            KEY_CRM_INFO_CLASS, fullyQualifiedClass, unitSettings, collectionName );
        LOG.debug( "Updated the record successfully" );
    }


    @Override
    public boolean updateSurveySettings( OrganizationUnitSettings companySettings, SurveySettings surveySettings )
        throws InvalidInputException
    {
        if ( companySettings == null ) {
            throw new InvalidInputException( "Company settings cannot be null." );
        }

        LOG.debug( "Updating comapnySettings: " + companySettings + " with surveySettings: " + surveySettings );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            KEY_SURVEY_SETTINGS, surveySettings, companySettings, COMPANY_SETTINGS_COLLECTION );
        LOG.debug( "Updated the record successfully" );

        return true;
    }
    
    @Override
    public boolean updateSecondaryWorkflow( String entityType, OrganizationUnitSettings unitSettings, SurveySettings surveySettings )
        throws InvalidInputException
    {
        if ( unitSettings == null ) {
            throw new InvalidInputException( "Settings cannot be null." );
        }

        LOG.debug( "Updating " + entityType + " with surveySettings: " + surveySettings );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_SURVEY_SETTINGS, surveySettings, unitSettings,
            entityType );
        LOG.debug( "Updated the record successfully" );

        return true;
    }


    @Override
    public boolean updateScoreForSurvey( String collectionName, OrganizationUnitSettings unitSettings,
        SurveySettings surveySettings ) throws InvalidInputException
    {
        if ( unitSettings == null ) {
            throw new InvalidInputException( "Unit settings cannot be null." );
        }

        LOG.debug( "Updating unitSettings: " + unitSettings + " with surveySettings: " + surveySettings );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            KEY_SURVEY_SETTINGS, surveySettings, unitSettings, collectionName );
        LOG.debug( "Updated the record successfully" );

        return true;
    }


    @Override
    public void updateLocationEnabled( OrganizationUnitSettings companySettings, boolean isLocationEnabled )
        throws InvalidInputException
    {
        if ( companySettings == null ) {
            throw new InvalidInputException( "Company settings cannot be null." );
        }

        LOG.debug( "Updating companySettings: " + companySettings + " with locationEnabled: " + isLocationEnabled );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            KEY_LOCATION_ENABLED, isLocationEnabled, companySettings, COMPANY_SETTINGS_COLLECTION );
        LOG.debug( "Updated the record successfully" );
    }


    @Override
    public void updateAccountDisabled( OrganizationUnitSettings companySettings, boolean isAccountDisabled )
        throws InvalidInputException
    {
        if ( companySettings == null ) {
            throw new InvalidInputException( "Company settings cannot be null." );
        }

        LOG.debug( "Updating companySettings: " + companySettings + " with AccountDisabled: " + isAccountDisabled );
        //Set isAccountDisabled in mongo
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            KEY_ACCOUNT_DISABLED, isAccountDisabled, companySettings, COMPANY_SETTINGS_COLLECTION );

        LOG.debug( "Updated the isAccountDisabled successfully" );
    }


    @Override
    public MailContentSettings updateSurveyParticipationMailBody( OrganizationUnitSettings companySettings, String mailSubject,
        String mailBody, String mailCategory ) throws InvalidInputException
    {
        if ( companySettings == null ) {
            throw new InvalidInputException( "Company settings cannot be null." );
        }
        if ( mailSubject == null && mailBody == null ) {
            throw new InvalidInputException( "Either mail subject or mail body should be sent " );
        }

        if ( mailCategory == null ) {
            throw new InvalidInputException( "Invalid mail category." );
        }
        LOG.debug(
            "Updating " + mailCategory + " for settings: " + companySettings.toString() + " with mail body: " + mailBody );

        // updating mail details
        List<String> paramOrder = new ArrayList<String>();
        mailBody = emailFormatHelper.replaceEmailBodyParamsWithDefaultValue( mailBody, paramOrder );

        MailContent mailContent = new MailContent();
        mailContent.setMail_subject( mailSubject );
        mailContent.setMail_body( mailBody );
        mailContent.setParam_order( paramOrder );

        MailContentSettings originalContentSettings = companySettings.getMail_content();
        if ( mailCategory.equals( CommonConstants.SURVEY_MAIL_BODY_CATEGORY ) ) {
            originalContentSettings.setTake_survey_mail( mailContent );
        } else if ( mailCategory.equals( CommonConstants.SURVEY_REMINDER_MAIL_BODY_CATEGORY ) ) {
            originalContentSettings.setTake_survey_reminder_mail( mailContent );
        } else if ( mailCategory.equals( CommonConstants.SURVEY_COMPLETION_MAIL_BODY_CATEGORY ) ) {
            originalContentSettings.setSurvey_completion_mail( mailContent );
        } else if ( mailCategory.equals( CommonConstants.SURVEY_COMPLETION_UNPLEASANT_MAIL_BODY_CATEGORY ) ) {
            originalContentSettings.setSurvey_completion_unpleasant_mail( mailContent );
        } else if ( mailCategory.equals( CommonConstants.SOCIAL_POST_REMINDER_MAIL_BODY_CATEGORY ) ) {
            originalContentSettings.setSocial_post_reminder_mail( mailContent );
        } else if ( mailCategory.equals( CommonConstants.RESTART_SURVEY_MAIL_BODY_CATEGORY ) ) {
            originalContentSettings.setRestart_survey_mail( mailContent );
        } else {
            throw new InvalidInputException( "Invalid mail category" );
        }

        LOG.debug( "Updating company settings mail content" );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            KEY_MAIL_CONTENT, originalContentSettings, companySettings, COMPANY_SETTINGS_COLLECTION );
        LOG.debug( "Updated company settings mail content" );

        return originalContentSettings;
    }


    @Override
    public MailContent deleteMailBodyFromSetting( OrganizationUnitSettings companySettings, String mailCategory )
        throws NonFatalException
    {
        if ( companySettings == null ) {
            throw new InvalidInputException( "Company settings cannot be null." );
        }
        if ( mailCategory == null ) {
            throw new InvalidInputException( "Invalid mail category." );
        }
        LOG.debug( "Deleting " + mailCategory + " for settings: " + companySettings.toString() );

        MailContent mailContent = new MailContent();
        String mailBody = null;
        String mailSubject = null;
        List<String> paramOrder = null;

        // TODO updating mail details
        MailContentSettings originalContentSettings = companySettings.getMail_content();
        if ( mailCategory.equals( CommonConstants.SURVEY_MAIL_BODY_CATEGORY ) ) {
            mailSubject = CommonConstants.SURVEY_MAIL_SUBJECT + "[AgentName]";
            try {
                mailBody = readMailContentFromFile( CommonConstants.SURVEY_REQUEST_MAIL_FILENAME );
            } catch ( IOException e ) {
                throw new NonFatalException( "Error occurred while parsing mail content.",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            paramOrder = new ArrayList<String>( Arrays.asList( paramOrderTakeSurvey.split( "," ) ) );

            mailContent = new MailContent();
            mailContent.setMail_subject( mailSubject );
            mailContent.setMail_body( mailBody );
            mailContent.setParam_order( paramOrder );

            originalContentSettings.setTake_survey_mail( null );
        } else if ( mailCategory.equals( CommonConstants.SURVEY_REMINDER_MAIL_BODY_CATEGORY ) ) {
            mailSubject = CommonConstants.REMINDER_MAIL_SUBJECT;
            try {
                mailBody = readMailContentFromFile( CommonConstants.SURVEY_REMINDER_MAIL_FILENAME );
            } catch ( IOException e ) {
                throw new NonFatalException( "Error occurred while parsing mail content.",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            paramOrder = new ArrayList<String>( Arrays.asList( paramOrderTakeSurveyReminder.split( "," ) ) );

            mailContent = new MailContent();
            mailContent.setMail_subject( mailSubject );
            mailContent.setMail_body( mailBody );
            mailContent.setParam_order( paramOrder );

            originalContentSettings.setTake_survey_reminder_mail( null );
        } else if ( mailCategory.equals( CommonConstants.SURVEY_COMPLETION_MAIL_BODY_CATEGORY ) ) {
            mailSubject = CommonConstants.SURVEY_COMPLETION_MAIL_SUBJECT;
            try {
                mailBody = readMailContentFromFile( CommonConstants.SURVEY_COMPLETION_MAIL_FILENAME );
            } catch ( IOException e ) {
                throw new NonFatalException( "Error occurred while parsing mail content.",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            paramOrder = new ArrayList<String>( Arrays.asList( paramOrderSurveyCompletionMail.split( "," ) ) );

            mailContent = new MailContent();
            mailContent.setMail_subject( mailSubject );
            mailContent.setMail_body( mailBody );
            mailContent.setParam_order( paramOrder );

            originalContentSettings.setSurvey_completion_mail( null );
        } else if ( mailCategory.equals( CommonConstants.SURVEY_COMPLETION_UNPLEASANT_MAIL_BODY_CATEGORY ) ) {
            mailSubject = CommonConstants.SURVEY_COMPLETION_UNPLEASANT_MAIL_SUBJECT;
            try {
                mailBody = readMailContentFromFile( CommonConstants.SURVEY_COMPLETION_UNPLEASANT_MAIL_FILENAME );
            } catch ( IOException e ) {
                throw new NonFatalException( "Error occurred while parsing mail content.",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            paramOrder = new ArrayList<String>( Arrays.asList( paramOrderSurveyCompletionUnpleasantMail.split( "," ) ) );

            mailContent = new MailContent();
            mailContent.setMail_subject( mailSubject );
            mailContent.setMail_body( mailBody );
            mailContent.setParam_order( paramOrder );

            originalContentSettings.setSurvey_completion_unpleasant_mail( null );
        } else if ( mailCategory.equals( CommonConstants.SOCIAL_POST_REMINDER_MAIL_BODY_CATEGORY ) ) {
            mailSubject = CommonConstants.SOCIAL_POST_REMINDER_MAIL_SUBJECT;
            try {
                mailBody = readMailContentFromFile( CommonConstants.SOCIAL_POST_REMINDER_MAIL_FILENAME );
            } catch ( IOException e ) {
                throw new NonFatalException( "Error occurred while parsing mail content.",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            paramOrder = new ArrayList<String>( Arrays.asList( paramOrderSocialPostReminder.split( "," ) ) );

            mailContent = new MailContent();
            mailContent.setMail_subject( mailSubject );
            mailContent.setMail_body( mailBody );
            mailContent.setParam_order( paramOrder );

            originalContentSettings.setSocial_post_reminder_mail( null );
        } else if ( mailCategory.equals( CommonConstants.RESTART_SURVEY_MAIL_BODY_CATEGORY ) ) {
            mailSubject = CommonConstants.RESTART_SURVEY_MAIL_SUBJECT;
            try {
                mailBody = readMailContentFromFile( CommonConstants.RESTART_SURVEY_MAIL_FILENAME );
            } catch ( IOException e ) {
                throw new NonFatalException( "Error occurred while parsing mail content.",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            paramOrder = new ArrayList<String>( Arrays.asList( paramOrderIncompleteSurveyReminder.split( "," ) ) );

            mailContent = new MailContent();
            mailContent.setMail_subject( mailSubject );
            mailContent.setMail_body( mailBody );
            mailContent.setParam_order( paramOrder );

            originalContentSettings.setRestart_survey_mail( null );
        } else {
            throw new InvalidInputException( "Invalid mail category" );
        }

        LOG.debug( "Deleting company settings mail content" );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            KEY_MAIL_CONTENT, originalContentSettings, companySettings, COMPANY_SETTINGS_COLLECTION );
        LOG.debug( "Deleting company settings mail content" );

        return mailContent;
    }


    @Override
    public MailContentSettings revertSurveyParticipationMailBody( OrganizationUnitSettings companySettings,
        String mailCategory ) throws NonFatalException
    {
        if ( companySettings == null ) {
            throw new InvalidInputException( "Company settings cannot be null." );
        }
        if ( mailCategory == null ) {
            throw new InvalidInputException( "Invalid mail category." );
        }
        LOG.debug( "Reverting " + mailCategory + " for settings: " + companySettings.toString() );

        String mailBody = null;
        String mailSubject = null;
        List<String> paramOrder = null;

        // TODO updating mail details
        MailContentSettings originalContentSettings = companySettings.getMail_content();
        if ( mailCategory.equals( CommonConstants.SURVEY_MAIL_BODY_CATEGORY ) ) {
            mailSubject = CommonConstants.SURVEY_MAIL_SUBJECT + "[AgentName]";
            try {
                mailBody = readMailContentFromFile( CommonConstants.SURVEY_REQUEST_MAIL_FILENAME );
            } catch ( IOException e ) {
                throw new NonFatalException( "Error occurred while parsing mail content.",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            paramOrder = new ArrayList<String>( Arrays.asList( paramOrderTakeSurvey.split( "," ) ) );

            MailContent mailContent = new MailContent();
            mailContent.setMail_subject( mailSubject );
            mailContent.setMail_body( mailBody );
            mailContent.setParam_order( paramOrder );

            originalContentSettings.setTake_survey_mail( mailContent );
        } else if ( mailCategory.equals( CommonConstants.SURVEY_REMINDER_MAIL_BODY_CATEGORY ) ) {
            mailSubject = CommonConstants.REMINDER_MAIL_SUBJECT;
            try {
                mailBody = readMailContentFromFile( CommonConstants.SURVEY_REMINDER_MAIL_FILENAME );
            } catch ( IOException e ) {
                throw new NonFatalException( "Error occurred while parsing mail content.",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            paramOrder = new ArrayList<String>( Arrays.asList( paramOrderTakeSurveyReminder.split( "," ) ) );

            MailContent mailContent = new MailContent();
            mailContent.setMail_subject( mailSubject );
            mailContent.setMail_body( mailBody );
            mailContent.setParam_order( paramOrder );

            originalContentSettings.setTake_survey_reminder_mail( mailContent );
        } else if ( mailCategory.equals( CommonConstants.SURVEY_COMPLETION_MAIL_BODY_CATEGORY ) ) {
            mailSubject = CommonConstants.SURVEY_COMPLETION_MAIL_SUBJECT;
            try {
                mailBody = readMailContentFromFile( CommonConstants.SURVEY_COMPLETION_MAIL_FILENAME );
            } catch ( IOException e ) {
                throw new NonFatalException( "Error occurred while parsing mail content.",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            paramOrder = new ArrayList<String>( Arrays.asList( paramOrderSurveyCompletionMail.split( "," ) ) );

            MailContent mailContent = new MailContent();
            mailContent.setMail_subject( mailSubject );
            mailContent.setMail_body( mailBody );
            mailContent.setParam_order( paramOrder );

            originalContentSettings.setSurvey_completion_mail( mailContent );
        } else if ( mailCategory.equals( CommonConstants.SOCIAL_POST_REMINDER_MAIL_BODY_CATEGORY ) ) {
            mailSubject = CommonConstants.SOCIAL_POST_REMINDER_MAIL_SUBJECT;
            try {
                mailBody = readMailContentFromFile( CommonConstants.SOCIAL_POST_REMINDER_MAIL_FILENAME );
            } catch ( IOException e ) {
                throw new NonFatalException( "Error occurred while parsing mail content.",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            paramOrder = new ArrayList<String>( Arrays.asList( paramOrderSocialPostReminder.split( "," ) ) );

            MailContent mailContent = new MailContent();
            mailContent.setMail_subject( mailSubject );
            mailContent.setMail_body( mailBody );
            mailContent.setParam_order( paramOrder );

            originalContentSettings.setSocial_post_reminder_mail( mailContent );
        } else if ( mailCategory.equals( CommonConstants.RESTART_SURVEY_MAIL_BODY_CATEGORY ) ) {
            mailSubject = CommonConstants.RESTART_SURVEY_MAIL_SUBJECT;
            try {
                mailBody = readMailContentFromFile( CommonConstants.RESTART_SURVEY_MAIL_FILENAME );
            } catch ( IOException e ) {
                throw new NonFatalException( "Error occurred while parsing mail content.",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            paramOrder = new ArrayList<String>( Arrays.asList( paramOrderIncompleteSurveyReminder.split( "," ) ) );

            MailContent mailContent = new MailContent();
            mailContent.setMail_subject( mailSubject );
            mailContent.setMail_body( mailBody );
            mailContent.setParam_order( paramOrder );

            originalContentSettings.setRestart_survey_mail( mailContent );
        } else {
            throw new InvalidInputException( "Invalid mail category" );
        }

        LOG.debug( "Reverting company settings mail content" );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            KEY_MAIL_CONTENT, originalContentSettings, companySettings, COMPANY_SETTINGS_COLLECTION );
        LOG.debug( "Reverting company settings mail content" );

        return originalContentSettings;
    }


    @Override
    public ArrayList<String> getSurveyParamOrder( String mailCategory ) throws InvalidInputException
    {
        if ( mailCategory.equals( CommonConstants.SURVEY_MAIL_BODY_CATEGORY ) ) {
            return new ArrayList<String>( Arrays.asList( paramOrderTakeSurvey.split( "," ) ) );
        } else if ( mailCategory.equals( CommonConstants.SURVEY_REMINDER_MAIL_BODY_CATEGORY ) ) {
            return new ArrayList<String>( Arrays.asList( paramOrderTakeSurveyReminder.split( "," ) ) );
        } else if ( mailCategory.equals( CommonConstants.SURVEY_COMPLETION_MAIL_BODY_CATEGORY ) ) {
            return new ArrayList<String>( Arrays.asList( paramOrderSurveyCompletionMail.split( "," ) ) );
        } else if ( mailCategory.equals( CommonConstants.SURVEY_COMPLETION_UNPLEASANT_MAIL_BODY_CATEGORY ) ) {
            return new ArrayList<String>( Arrays.asList( paramOrderSurveyCompletionUnpleasantMail.split( "," ) ) );
        } else if ( mailCategory.equals( CommonConstants.SOCIAL_POST_REMINDER_MAIL_BODY_CATEGORY ) ) {
            return new ArrayList<String>( Arrays.asList( paramOrderSocialPostReminder.split( "," ) ) );
        } else if ( mailCategory.equals( CommonConstants.RESTART_SURVEY_MAIL_BODY_CATEGORY ) ) {
            return new ArrayList<String>( Arrays.asList( paramOrderIncompleteSurveyReminder.split( "," ) ) );
        } else {
            throw new InvalidInputException( "Invalid mail category" );
        }
    }


    @Override
    @Transactional
    public DisabledAccount addDisabledAccount( long companyId, boolean forceDisable, long userId )
        throws InvalidInputException, NoRecordsFetchedException, PaymentException
    {
        LOG.info( "Adding the disabled account to the database for company id : " + companyId );
        if ( companyId <= 0 ) {
            LOG.error( "addDisabledAccount : Invalid companyId has been given." );
            throw new InvalidInputException( "addDisabledAccount : Invalid companyId has been given." );
        }
        List<LicenseDetail> licenseDetails = null;

        // Fetching the company entity from database
        LOG.debug( "Fetching the company record from the database" );
        Company company = companyDao.findById( Company.class, companyId );

        // Fetching the license details for the company
        LOG.debug( "Fetching the License Detail record from the database" );
        HashMap<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.COMPANY_COLUMN, company );
        licenseDetails = licenceDetailDao.findByKeyValue( LicenseDetail.class, queries );

        if ( licenseDetails == null || licenseDetails.isEmpty() ) {
            LOG.error( "No license detail records have been found for company id : " + companyId );
            throw new NoRecordsFetchedException( "No license detail records have been found for company id : " + companyId );
        }

        LicenseDetail licenseDetail = licenseDetails.get( CommonConstants.INITIAL_INDEX );

        //check if already an entry in database
        HashMap<String, Object> queriesForDisableAccount = new HashMap<>();
        queriesForDisableAccount.put( CommonConstants.COMPANY_COLUMN, company );
        List<DisabledAccount> disabledAccounts = disabledAccountDao.findByKeyValue( DisabledAccount.class,
            queriesForDisableAccount );
        DisabledAccount disabledAccount = null;
        if ( disabledAccounts != null && disabledAccounts.size() > 0 ) {
            LOG.debug( "Found existing entry in the database." );
            disabledAccount = disabledAccounts.get( CommonConstants.INITIAL_INDEX );
            LOG.debug( "Updating the Disabled Account entity to the database" );

        } else {
            LOG.debug( "Preparing the DisabledAccount entity to be saved in the database." );
            disabledAccount = new DisabledAccount();
            disabledAccount.setCompany( company );
            disabledAccount.setCreatedBy(
                userId == CommonConstants.REALTECH_ADMIN_ID ? CommonConstants.ADMIN_USER_NAME : String.valueOf( userId ) );
            disabledAccount.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
            LOG.debug( "Adding the Disabled Account entity to the database" );
        }


        disabledAccount.setLicenseDetail( licenseDetail );
        disabledAccount.setStatus( CommonConstants.STATUS_ACTIVE );

        if ( forceDisable ) {
            disabledAccount.setDisableDate( new Timestamp( System.currentTimeMillis() ) );
            disabledAccount.setForceDelete( true );
        } else {
            disabledAccount.setForceDelete( false );
            if ( licenseDetail.getPaymentMode().equals( CommonConstants.BILLING_MODE_AUTO ) ) {
                disabledAccount.setDisableDate( gateway.getDateForCompanyDeactivation( licenseDetail.getSubscriptionId() ) );
            } else {
                disabledAccount.setDisableDate( new Timestamp( System.currentTimeMillis() ) );
            }
        }
        disabledAccount.setModifiedBy(
            userId == CommonConstants.REALTECH_ADMIN_ID ? CommonConstants.ADMIN_USER_NAME : String.valueOf( userId ) );
        disabledAccount.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        disabledAccount = disabledAccountDao.saveOrUpdate( disabledAccount );
        return disabledAccount;
    }


    @Override
    @Transactional
    public void inactiveDisabledAccount( long companyId ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Deleting the Disabled Account pertaining to company id : " + companyId );
        if ( companyId <= 0 ) {
            LOG.error( "addDisabledAccount : Invalid companyId has been given." );
            throw new InvalidInputException( "addDisabledAccount : Invalid companyId has been given." );
        }
        List<DisabledAccount> disabledAccounts = null;

        // Fetching the company entity from database
        LOG.debug( "Fetching the company record from the database" );
        Company company = companyDao.findById( Company.class, companyId );

        // Fetching the disabled account entity for the company
        LOG.debug( "Fetching the Disabled Account from the database" );
        HashMap<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.COMPANY_COLUMN, company );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        disabledAccounts = disabledAccountDao.findByKeyValue( DisabledAccount.class, queries );

        if ( disabledAccounts == null || disabledAccounts.isEmpty() ) {
            LOG.error( "No disabled account records have been found for company id : " + companyId );
            throw new NoRecordsFetchedException( "No disabled account records have been found for company id : " + companyId );
        }

        DisabledAccount disabledAccount = disabledAccounts.get( CommonConstants.INITIAL_INDEX );
        disabledAccount.setStatus( CommonConstants.STATUS_INACTIVE );
        LOG.debug( "Removing the disabled account record with id : " + disabledAccount.getId() + "from the database." );

        // Perform soft delete of the record in the database
        disabledAccountDao.update( disabledAccount );
        LOG.debug( "Record successfully deleted from the database!" );
    }


    @Override
    @Transactional
    public void markDisabledAccountAsProcessed( Company company ) throws InvalidInputException
    {
        if ( company == null ) {
            LOG.error( "addDisabledAccount : Invalid companyId has been given." );
            throw new InvalidInputException( "addDisabledAccount : Invalid companyId has been given." );
        }

        LOG.debug( "Marking the Disabled Account as processed for company id : " + company.getCompanyId() );

        List<DisabledAccount> disabledAccounts = null;

        // Fetching the disabled account entity for the company
        LOG.debug( "Fetching the Disabled Account from the database" );
        HashMap<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.COMPANY_COLUMN, company );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        disabledAccounts = disabledAccountDao.findByKeyValue( DisabledAccount.class, queries );

        if ( disabledAccounts != null && !disabledAccounts.isEmpty() ) {
            DisabledAccount disabledAccount = disabledAccounts.get( CommonConstants.INITIAL_INDEX );
            disabledAccount.setStatus( CommonConstants.DISABLED_ACCOUNT_PROCESSED );
            LOG.debug( "Marking the disabled account record with id : " + disabledAccount.getId() + " as processed" );
            // Perform soft delete of the record in the database
            disabledAccountDao.update( disabledAccount );
        }

        LOG.debug( "Record successfully marked As Processed!" );
    }


    @Override
    public SocialMediaTokens getAgentSocialMediaTokens( long iden ) throws InvalidInputException
    {
        SocialMediaTokens tokens = null;
        if ( iden > 0l ) {
            LOG.debug( "Getting social media tokens for agent id: " + iden );
            tokens = organizationUnitSettingsDao.fetchSocialMediaTokens( CommonConstants.AGENT_SETTINGS_COLLECTION, iden );
        } else {
            LOG.error( "Invalid identified passed" );
            throw new InvalidInputException( "Invalid identified passed" );
        }
        return tokens;
    }


    /**
     * Method to upgrade a default region to a region
     * 
     * @param region
     * @return
     * @throws InvalidInputException
     */
    Region upgradeDefaultRegion( Region region ) throws InvalidInputException
    {

        LOG.debug( "Upgrading the default region to a user made region" );
        if ( region == null ) {
            LOG.error( "upgradeDefaultRegion Region parameter is invalid or null" );
            throw new InvalidInputException( "upgradeDefaultRegion Region parameter is invalid or null" );
        }

        LOG.debug( "Changing the record to change the flag IS_DEFAULT_BY_SYSTEM " );
        region.setIsDefaultBySystem( CommonConstants.STATUS_INACTIVE );
        region.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        LOG.debug( "Updating the database to show change from default region to region" );
        regionDao.update( region );
        LOG.debug( " Region upgrade successful. Returning the region" );

        return region;
    }


    /**
     * Method to upgrade a default branch to a branch
     * 
     * @param company
     * @return
     * @throws InvalidInputException
     */
    Branch upgradeDefaultBranch( Branch branch ) throws InvalidInputException
    {

        LOG.debug( "Upgrading default branch to a user made branch" );
        if ( branch == null ) {
            LOG.error( "upgradeDefaultBranch Branch parameter is invalid or null" );
            throw new InvalidInputException( "upgradeDefaultBranch Branch parameter is invalid or null" );
        }

        // Update the branch record in the database to make it into a user made branch by changing
        // the
        // IS_DEFAULT_BY_SYSTEM flag in the record
        LOG.debug( "Changing the record to change the flag IS_DEFAULT_BY_SYSTEM " );
        branch.setIsDefaultBySystem( CommonConstants.STATUS_INACTIVE );
        branch.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        LOG.debug( "Updating the database to show change from default branch to branch" );
        branchDao.update( branch );
        LOG.debug( " Branch upgrade successful. Returning the branch" );
        return branch;

    }


    /**
     * Function to check if only default region exists for a company
     * 
     * @param company
     * @return
     * @throws InvalidInputException
     */
    Region fetchDefaultRegion( Company company ) throws InvalidInputException
    {

        if ( company == null ) {
            LOG.error( " fetchDefaultRegion : Company parameter is null" );
            throw new InvalidInputException( " fetchDefaultRegion : Company parameter is null" );
        }

        LOG.debug( "Checking if only default region exists" );
        Region defaultRegion = null;

        // We fetch all the regions for a particular company
        HashMap<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.COMPANY_COLUMN, company );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );

        LOG.debug( "Fetching all regions for company with id : " + company.getCompanyId() );
        List<Region> regionList = regionDao.findByKeyValue( Region.class, queries );

        if ( regionList == null || regionList.isEmpty() ) {
            LOG.debug( "No regions found for company with id : " + company.getCompanyId() );
            defaultRegion = null;
        }
        // Check if only default region exists
        else if ( regionList.size() == CommonConstants.STATUS_ACTIVE
            && regionList.get( CommonConstants.INITIAL_INDEX ).getIsDefaultBySystem() == CommonConstants.STATUS_ACTIVE ) {
            // Only default region exists
            LOG.debug( "Only default region exists for company with id : " + company.getCompanyId() );
            defaultRegion = regionList.get( CommonConstants.INITIAL_INDEX );
        } else {
            // More than one regions exist so no default region exists. Return null.
            LOG.debug( "More than one regions exist. So returning null" );
            defaultRegion = null;
        }
        return defaultRegion;

    }


    /**
     * Function to check if only default branch exists for a company
     * 
     * @param company
     * @return
     * @throws InvalidInputException
     */
    Branch fetchDefaultBranch( Company company ) throws InvalidInputException
    {

        if ( company == null ) {
            LOG.error( " fetchDefaultBranch : Company parameter is null" );
            throw new InvalidInputException( " fetchDefaultBranch : Company parameter is null" );
        }

        LOG.debug( "Checking if only default branch exists" );
        Branch defaultBranch = null;

        // We fetch all the branches for a particular company
        HashMap<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.COMPANY_COLUMN, company );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );

        LOG.debug( "Fetching all branches for company with id : " + company.getCompanyId() );
        List<Branch> branchList = branchDao.findByKeyValue( Branch.class, queries );

        if ( branchList == null || branchList.isEmpty() ) {
            LOG.debug( "No branches found for company with id : " + company.getCompanyId() );
            defaultBranch = null;
        }
        // Check if only default branch exists
        else if ( branchList.size() == CommonConstants.STATUS_ACTIVE
            && branchList.get( CommonConstants.INITIAL_INDEX ).getIsDefaultBySystem() == CommonConstants.STATUS_ACTIVE ) {
            // Only default branch exists
            LOG.debug( "Only default branch exists for company with id : " + company.getCompanyId() );
            defaultBranch = branchList.get( CommonConstants.INITIAL_INDEX );
        } else {
            // More than one branches exist so no default branch exists. Return null.
            LOG.debug( branchList.size() + " branches found. So returning null." );
            defaultBranch = null;
        }

        return defaultBranch;

    }


    /**
     * Method to upgrade plan to Company
     * 
     * @param company
     * @throws InvalidInputException
     * @throws SolrException
     * @throws NoRecordsFetchedException
     */
    void upgradeToCompany( Company company ) throws InvalidInputException, SolrException, NoRecordsFetchedException
    {

        LOG.debug( "Upgrading to Company" );
        if ( company == null ) {
            LOG.error( " upgradeToCompany : Company parameter is null" );
            throw new InvalidInputException( " upgradeToCompany : Company parameter is null" );
        }

        /**
         * In case of upgrading to Company plan we check if default branch exists and upgrade it to
         * a branch We also add the branch settings to mongo collection BRANCH_SETTINGS and to solr.
         */

        LOG.debug( "checking if only default branch exists and fetching it" );
        Branch defaultBranch = fetchDefaultBranch( company );
        if ( defaultBranch != null ) {
            LOG.debug( "Default branch exists. Upgrading it to branch" );
            Branch upgradedBranch = upgradeDefaultBranch( defaultBranch );
            LOG.debug( "Adding the upgraded branch to mongo collection BRANCH_SETTINGS" );
            insertBranchSettings( upgradedBranch );
            LOG.debug( "Successfully added settings to mongo, adding the new branch to solr" );
            solrSearchService.addOrUpdateBranchToSolr( upgradedBranch );
            LOG.debug( "Solr update successful" );
        } else {
            LOG.error( "No default branch found for company with id : " + company.getCompanyId() );
            throw new NoRecordsFetchedException( "No default branch found for company with id : " + company.getCompanyId() );
        }

        LOG.debug( "Databases upgraded successfully!" );
    }


    /**
     * Method to upgrade plan to Enterprise
     * 
     * @param company
     * @param fromAccountsMasterId
     * @throws InvalidInputException
     * @throws SolrException
     * @throws NoRecordsFetchedException
     */
    void upgradeToEnterprise( Company company, int fromAccountsMasterId )
        throws InvalidInputException, SolrException, NoRecordsFetchedException
    {

        LOG.debug( "Upgrading to Enterprise" );
        if ( company == null ) {
            LOG.error( " upgradeToEnterprise : Company parameter is null" );
            throw new InvalidInputException( " upgradeToEnterprise : Company parameter is null" );
        }
        if ( fromAccountsMasterId <= 0 || fromAccountsMasterId > 3 ) {
            LOG.error( " upgradeToCompany : fromAccountsMaster parameter is invalid" );
            throw new InvalidInputException( " upgradeToCompany : fromAccountsMaster parameter is invalid" );
        }

        /**
         * In case of upgrading to Enterprise plan we check if default branch exists and upgrade it
         * to a branch And then we upgrade find the default region and upgrade it user made region.
         * We also add the branch settings to mongo collection BRANCH_SETTINGS and to solr.
         */

        LOG.debug( "checking if only default branch exists and fetching it" );
        Branch defaultBranch = fetchDefaultBranch( company );
        if ( defaultBranch != null ) {

            LOG.debug( "Default branch exists. Upgrading it to branch" );
            Branch upgradedBranch = upgradeDefaultBranch( defaultBranch );

            LOG.debug( "Adding the upgraded branch to mongo collection BRANCH_SETTINGS" );
            insertBranchSettings( upgradedBranch );

            LOG.debug( "Successfully added settings to mongo, adding the new branch to solr" );
            solrSearchService.addOrUpdateBranchToSolr( upgradedBranch );
            LOG.debug( "Solr update successful" );

            LOG.debug( "Fetching the default region" );
            Region defaultRegion = fetchDefaultRegion( company );
            if ( defaultRegion == null ) {
                LOG.error( "No default region found for company with id : " + company.getCompanyId() );
                throw new NoRecordsFetchedException(
                    "No default region found for company with id : " + company.getCompanyId() );
            }
            LOG.debug( "Default region exists, upgrading it" );
            Region upgradedRegion = upgradeDefaultRegion( defaultRegion );

            LOG.debug( "Adding the upgraded region to mongo collection REGION_SETTINGS" );
            insertRegionSettings( upgradedRegion );
            LOG.debug( "Successfully added settings to mongo, adding the new region to solr" );

            solrSearchService.addOrUpdateRegionToSolr( upgradedRegion );
            LOG.debug( "Solr update successful" );
        } else {
            if ( fromAccountsMasterId != 3 ) {
                LOG.error( "No default branch found for company with id : " + company.getCompanyId() );
                throw new NoRecordsFetchedException(
                    "No default branch found for company with id : " + company.getCompanyId() );
            }
            LOG.debug( "Fetching the default region" );
            Region defaultRegion = fetchDefaultRegion( company );
            if ( defaultRegion == null ) {
                LOG.error( "No default region found for company with id : " + company.getCompanyId() );
                throw new NoRecordsFetchedException(
                    "No default region found for company with id : " + company.getCompanyId() );
            }
            LOG.debug( "Default region exists, upgrading it" );
            Region upgradedRegion = upgradeDefaultRegion( defaultRegion );

            LOG.debug( "Adding the upgraded region to mongo collection REGION_SETTINGS" );
            insertRegionSettings( upgradedRegion );

            LOG.debug( "Successfully added settings to mongo, adding the new region to solr" );
            solrSearchService.addOrUpdateRegionToSolr( upgradedRegion );
            LOG.debug( "Solr update successful" );
        }

        LOG.debug( "Databases upgraded successfully!" );
    }


    /**
     * Method called to update databases on plan upgrade
     * 
     * @param company
     * @param newAccountsMasterPlanId
     * @throws NoRecordsFetchedException
     * @throws InvalidInputException
     * @throws SolrException
     */
    @Override
    @Transactional
    public void upgradeAccount( Company company, int newAccountsMasterPlanId )
        throws NoRecordsFetchedException, InvalidInputException, SolrException
    {

        if ( company == null ) {
            LOG.error( "upgradePlanAtBackend Company parameter is invalid or null" );
            throw new InvalidInputException( "upgradePlanAtBackend Company parameter is invalid or null" );
        }
        if ( newAccountsMasterPlanId <= 0 ) {
            LOG.error( "upgradePlanAtBackend AccountsMaster id parameter is invalid" );
            throw new InvalidInputException( "upgradePlanAtBackend AccountsMaster id parameter is invalid" );
        }

        // We fetch the license detail record to find the current plan
        LOG.debug( "Finding the current plan" );
        List<LicenseDetail> licenseDetails = null;
        LicenseDetail currentLicenseDetail = null;

        LOG.debug( "Making the database call to find record for company with id : " + company.getCompanyId() );
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.COMPANY_COLUMN, company );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        licenseDetails = licenceDetailDao.findByKeyValue( LicenseDetail.class, queries );

        // Check if license details exist
        if ( licenseDetails == null || licenseDetails.isEmpty() ) {
            LOG.error( "No license details records found for company with id : " + company.getCompanyId() );
            throw new NoRecordsFetchedException(
                "No license details records found for company with id : " + company.getCompanyId() );
        }

        currentLicenseDetail = licenseDetails.get( CommonConstants.INITIAL_INDEX );
        LOG.debug( "License detail object for company with id : " + company.getCompanyId() + " fetched" );
        int currentAccountsMasterId = currentLicenseDetail.getAccountsMaster().getAccountsMasterId();

        // Now we update the Region and Branch tables in the database to reflect changes
        LOG.debug( "Updating the regions and the branches for plan upgrade" );

        switch ( newAccountsMasterPlanId ) {
            case CommonConstants.ACCOUNTS_MASTER_TEAM:
                if ( currentAccountsMasterId <= 0 || currentAccountsMasterId > 1 ) {
                    LOG.error(
                        " upgradeAccount : fromAccountsMaster parameter is invalid : value is : " + currentAccountsMasterId );
                    throw new InvalidInputException(
                        " upgradeAccount : fromAccountsMaster parameter is invalid: value is : " + currentAccountsMasterId );
                }
                /**
                 * In case of upgrading to the team account we need to change only the license
                 * details table and add default branch to solr.
                 */
                LOG.debug( "checking if only default branch exists and fetching it" );
                Branch defaultBranch = fetchDefaultBranch( company );
                if ( defaultBranch != null ) {
                    LOG.debug( "Adding the new branch to solr" );
                    solrSearchService.addOrUpdateBranchToSolr( defaultBranch );
                    LOG.debug( "Solr update successful" );
                } else {
                    LOG.error( "No default branch found for company with id : " + company.getCompanyId() );
                    throw new NoRecordsFetchedException(
                        "No default branch found for company with id : " + company.getCompanyId() );
                }
                LOG.debug( "Databases updated to Team plan" );
                break;

            case CommonConstants.ACCOUNTS_MASTER_COMPANY:
                // We check if the plan we are changing from and the plan we are changing to are
                // correct
                if ( currentAccountsMasterId <= 0 || currentAccountsMasterId > 2 ) {
                    LOG.error(
                        " upgradeAccount : fromAccountsMaster parameter is invalid: value is : " + currentAccountsMasterId );
                    throw new InvalidInputException(
                        " upgradeAccount : fromAccountsMaster parameter is invalid: value is : " + currentAccountsMasterId );
                }
                LOG.debug( "Calling the database update method for Company plan" );
                upgradeToCompany( company );
                LOG.debug( "Databases updated to Company plan" );
                break;

            case CommonConstants.ACCOUNTS_MASTER_ENTERPRISE:
                // We check if the plan we are changing from and the plan we are changing to are
                // correct
                if ( currentAccountsMasterId <= 0 || currentAccountsMasterId > 3 ) {
                    LOG.error(
                        " upgradeAccount : fromAccountsMaster parameter is invalid: value is : " + currentAccountsMasterId );
                    throw new InvalidInputException(
                        " upgradeAccount : fromAccountsMaster parameter is invalid: value is : " + currentAccountsMasterId );
                }
                LOG.debug( "Calling the database update method for Enterprise plan" );
                upgradeToEnterprise( company, currentAccountsMasterId );
                LOG.debug( "Databases updated to Enterprise plan" );
                break;

            default:
                LOG.error( " upgradeAccount : Invalid accounts master id parameter given" );
                throw new InvalidInputException( " upgradeAccount : Invalid accounts master id parameter given" );
        }
        LOG.debug( "Upgrade successful!" );
    }


    /**
     * Method to fetch all regions of a company based on company profile name
     * 
     * @param companyProfileName
     * @return
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public List<Region> getRegionsForCompany( String companyProfileName ) throws InvalidInputException, ProfileNotFoundException
    {
        LOG.debug( "Method getRegionsForCompany called for companyProfileName:" + companyProfileName );
        OrganizationUnitSettings companySettings = profileManagementService
            .getCompanyProfileByProfileName( companyProfileName );
        List<Region> regions = null;
        if ( companySettings != null ) {
            long companyId = companySettings.getIden();
            LOG.debug( "Fetching regions for company : " + companyId );
            regions = getRegionsForCompany( companyId );
        } else {
            LOG.warn( "No company settings found for profileName : " + companyProfileName );
        }
        return regions;
    }


    /**
     * Method to fetch all regions of a company based on company id
     *
     * @param companyId
     * @return
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public List<Region> getRegionsForCompany( long companyId ) throws InvalidInputException, ProfileNotFoundException
    {
        if ( companyId < 0l )
            throw new InvalidInputException( "Invalid company id passed as argument " );
        LOG.debug( "Method getRegionsForCompany called for companyId:" + companyId );
        List<Region> regions = null;
        /**
         * Adding columns to be fetched in the list
         */
        List<String> columnNames = new ArrayList<String>();
        columnNames.add( CommonConstants.REGION_NAME_COLUMN );
        columnNames.add( CommonConstants.REGION_ID_COLUMN );
        columnNames.add( CommonConstants.PROFILE_NAME_COLUMN );

        /**
         * Building criteria
         */
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.COMPANY_COLUMN, companyDao.findById( Company.class, companyId ) );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        queries.put( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.STATUS_INACTIVE );
        regions = regionDao.findProjectionsByKeyValue( Region.class, columnNames, queries, CommonConstants.REGION_COLUMN );

        //SS-1421: populate the address of the regions from Solr before sending this list back
        populateAddressOfRegionFromSolr( regions, companyId );

        return regions;
    }


    /*
     * SS-1421: Method to query Solr for the region address and populate the given Region list
     */
    private void populateAddressOfRegionFromSolr( List<Region> regions, long companyId )
    {
        try {

            //Get the regions in the company from Solr
            String regionsSearchedString = solrSearchService.fetchRegionsByCompany( companyId, Integer.MAX_VALUE );
            Type searchedRegionsList = new TypeToken<List<RegionFromSearch>>() {}.getType();
            List<RegionFromSearch> regionSearchedList = new Gson().fromJson( regionsSearchedString, searchedRegionsList );

            //Iterate over the searched regions
            for ( RegionFromSearch regionFromSearch : regionSearchedList ) {
                for ( Region region : regions ) {
                    if ( region.getRegionId() == regionFromSearch.getRegionId() ) {

                        //populate the address into the region objects
                        region.setAddress1( regionFromSearch.getAddress1() );
                        region.setAddress2( regionFromSearch.getAddress2() );
                    }
                }
            }
        } catch ( SolrException e ) {
            LOG.error( "SolrException while searching for region for company ID: " + companyId + ". Reason : " + e.getMessage(),
                e );
        } catch ( MalformedURLException e ) {
            LOG.error( "MalformedURLException while searching for region for company ID: " + companyId + ". Reason : "
                + e.getMessage(), e );
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException while searching for region for company ID: " + companyId + ". Reason : "
                + e.getMessage(), e );
        }
    }


    /**
     * Method to fetch all regions of a company based on company id
     *
     * @param searchKey
     * @return
     * @throws SolrException 
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public List<Region> getRegionsBySearchKey( String searchKey ) throws InvalidInputException, SolrException
    {
        LOG.debug( "Method getRegionsBySearchKey called for search key:" + searchKey );
        if ( searchKey == null )
            throw new InvalidInputException( "Invalid searchKey passed as argument " );
        List<Region> regions = new ArrayList<Region>();
        List<SolrDocument> solrDocumentList = solrSearchService.searchBranchRegionOrAgentByNameForAdmin(
            CommonConstants.REGION_NAME_SOLR, ClientUtils.escapeQueryChars( searchKey ) );


        	for ( SolrDocument document : solrDocumentList ) {
        		try {
        			Region region = new Region();
                    region.setRegionId( Long.parseLong( document.get( CommonConstants.REGION_ID_SOLR ).toString() ) );
                    region.setRegionName( document.get( CommonConstants.REGION_NAME_SOLR ).toString() );
                    region.setRegion( document.get( CommonConstants.REGION_NAME_SOLR ).toString() );
                    if ( document.get( CommonConstants.ADDRESS1_SOLR ) != null )
                        region.setAddress1( document.get( CommonConstants.ADDRESS1_SOLR ).toString() );
                    if ( document.get( CommonConstants.ADDRESS2_SOLR ) != null )
                        region.setAddress2( document.get( CommonConstants.ADDRESS2_SOLR ).toString() );
                    regions.add( region );
        		} catch(NumberFormatException | NullPointerException e ) {
                	LOG.error("Exception while setting the solr search result to object",e);
                }
            }

        return regions;
    }


    @Override
    @Transactional
    public List<Branch> getBranchesBySearchKey( String searchKey ) throws InvalidInputException, SolrException
    {
        LOG.debug( "Method getBranchesBySearchKey called for search key:" + searchKey );
        if ( searchKey == null )
            throw new InvalidInputException( "Invalid searchKey passed as argument " );
        List<Branch> branches = new ArrayList<Branch>();
        List<SolrDocument> solrDocumentList = solrSearchService.searchBranchRegionOrAgentByNameForAdmin(
            CommonConstants.BRANCH_NAME_SOLR, ClientUtils.escapeQueryChars( searchKey ) );


        	for ( SolrDocument document : solrDocumentList ) {
        		try {
        			Branch branch = new Branch();
                    branch.setBranchId( Long.parseLong( document.get( CommonConstants.BRANCH_ID_SOLR ).toString() ) );
                    branch.setBranchName( document.get( CommonConstants.BRANCH_NAME_SOLR ).toString() );
                    branch.setBranch( document.get( CommonConstants.BRANCH_NAME_SOLR ).toString() );
                    if ( document.get( CommonConstants.ADDRESS1_SOLR ) != null )
                        branch.setAddress1( document.get( CommonConstants.ADDRESS1_SOLR ).toString() );
                    if ( document.get( CommonConstants.ADDRESS2_SOLR ) != null )
                        branch.setAddress2( document.get( CommonConstants.ADDRESS2_SOLR ).toString() );
                    branches.add( branch );
        		} catch(NumberFormatException | NullPointerException e ) {
                	LOG.error("Exception while setting the solr search result to object",e);
                }
            }

        return branches;
    }


    @SuppressWarnings ( "unchecked")
    @Override
    @Transactional
    public List<UserFromSearch> getUsersBySearchKey( String searchKey ) throws InvalidInputException, SolrException
    {
        LOG.debug( "Method getUsersBySearchKey called for search key:" + searchKey );
        if ( searchKey == null )
            throw new InvalidInputException( "Invalid searchKey passed as argument " );
        List<UserFromSearch> users = new ArrayList<UserFromSearch>();
        List<SolrDocument> solrDocumentList = solrSearchService.searchBranchRegionOrAgentByNameForAdmin(
            CommonConstants.USER_DISPLAY_NAME_SOLR, ClientUtils.escapeQueryChars( searchKey ) );


		for (SolrDocument document : solrDocumentList) {
			try {
				UserFromSearch user = new UserFromSearch();
				user.setUserId(Long.parseLong(document.get(CommonConstants.USER_ID_SOLR).toString()));
				user.setEmailId(document.get(CommonConstants.USER_EMAIL_ID_SOLR).toString());
				user.setDisplayName(document.get(CommonConstants.USER_DISPLAY_NAME_SOLR).toString());
				user.setCompanyId(Long.parseLong(document.get(CommonConstants.COMPANY_ID_SOLR).toString()));
				user.setAgent(Boolean.parseBoolean(document.get(CommonConstants.IS_AGENT_SOLR).toString()));
				user.setStatus(Integer.parseInt(document.get(CommonConstants.STATUS_SOLR).toString()));
				user.setIsOwner(Integer.parseInt(document.get(CommonConstants.IS_OWNER_COLUMN).toString()));
				user.setBranchAdmin(
						Boolean.parseBoolean(document.get(CommonConstants.IS_BRANCH_ADMIN_SOLR).toString()));
				user.setRegionAdmin(
						Boolean.parseBoolean(document.get(CommonConstants.IS_REGION_ADMIN_SOLR).toString()));
				user.setRegions((List<Long>) document.get(CommonConstants.REGIONS_SOLR));
				user.setBranches((List<Long>) document.get(CommonConstants.BRANCHES_SOLR));
				users.add(user);
			} catch (NumberFormatException | NullPointerException e) {
				LOG.error("Exception while setting the solr search result to object", e);
			}
		}

        return users;
    }


    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.debug( "afterPropertiesSet called for organization managemnet service" );
        
        keyList = new ArrayList<>();
        keyList.add( "customerSuccessId" );
        keyList.add( "customerSuccessOwner" );
        keyList.add( "closedDate" );
        keyList.add( "rvp" );
        keyList.add( "tmcClient" );
        keyList.add( "surveyDataSource" );
        keyList.add( "dbaForCompany" );
        keyList.add( "potential" );
        keyList.add( "gapAnalysisDate" );
        keyList.add( "customerSettings" );
        keyList.add( "servicesSold" );
        keyList.add( "transferReviewPolicy" );
        keyList.add( "tag" );
        keyList.add( "realtorSurveys" );
        keyList.add( "happyWorkflowSettings" );
        keyList.add( "primaryPoc" );
        keyList.add( "poc2" );
        keyList.add( "email" );
        keyList.add( "emailPoc2" );
        keyList.add( "secondaryEmail" );
        keyList.add( "phonePoc2" );
        keyList.add( "phone" );
        keyList.add( "lastConversationDate" );
        
        Map<Integer, VerticalsMaster> verticalsMap = new HashMap<>();
        // Populate the verticals master map
        verticalsMap = utilityService.populateVerticalMastersMap();
        if ( !verticalsMap.isEmpty() )
            verticalsMastersMap.putAll( verticalsMap );
        LOG.debug( "afterPropertiesSet finished for organization managemnet service" );

    }


    @Override
    public List<VerticalsMaster> getAllVerticalsMaster() throws InvalidInputException
    {
        LOG.debug( "Method getAllVerticalsMaster called to fetch the list of vertical masters" );
        List<VerticalsMaster> verticalsMasters = new ArrayList<>();
        for ( Map.Entry<Integer, VerticalsMaster> entry : verticalsMastersMap.entrySet() ) {
            verticalsMasters.add( entry.getValue() );
        }
        if ( verticalsMasters.isEmpty() ) {
            throw new InvalidInputException( "No verticals master found" );
        }
        LOG.debug( "Method getAllVerticalsMaster successfully finished" );
        return verticalsMasters;
    }


    /**
     * Method to fetch all the branches that are directly linked to a company based on companyProfileName
     * 
     * @throws NoRecordsFetchedException
     */
    @Override
    @Transactional
    public List<Branch> getBranchesUnderCompany( String companyProfileName )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException
    {
        LOG.debug( "Method getBranchesUnderCompany called for companyProfileName : " + companyProfileName );
        if ( companyProfileName == null || companyProfileName.isEmpty() ) {
            throw new InvalidInputException( "companyProfileName is null or empty in getBranchesUnderCompany" );
        }
        List<Branch> branches = null;
        OrganizationUnitSettings companySettings = profileManagementService
            .getCompanyProfileByProfileName( companyProfileName );
        if ( companySettings != null ) {
            branches = getBranchesUnderCompany( companySettings.getIden() );
        } else {
            LOG.warn( "No company settings found for profileName : " + companyProfileName );
        }
        LOG.debug( "Method getBranchesUnderCompany executed sucessfully" );

        return branches;
    }


    /**
     * Method to fetch all the branches that are directly linked to a company based on company Id
     *
     * @throws NoRecordsFetchedException
     */
    @Override
    @Transactional
    public List<Branch> getBranchesUnderCompany( long companyId )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException
    {
        if ( companyId < 0l )
            throw new InvalidInputException( "Invalid company id passed as argument " );

        List<Branch> branches = null;
        List<String> columnNames = new ArrayList<String>();
        columnNames.add( CommonConstants.BRANCH_ID_COLUMN );
        columnNames.add( CommonConstants.BRANCH_NAME_COLUMN );
        columnNames.add( CommonConstants.PROFILE_NAME_COLUMN );

        Map<String, Object> queries = new HashMap<String, Object>();

        Company company = companyDao.findById( Company.class, companyId );
        Region defaultRegion = getDefaultRegionForCompany( company );

        queries.put( CommonConstants.COMPANY_COLUMN, company );
        queries.put( CommonConstants.REGION_COLUMN, defaultRegion );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        queries.put( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.NO );

        branches = branchDao.findProjectionsByKeyValue( Branch.class, columnNames, queries,
            CommonConstants.BRANCH_NAME_COLUMN );

        //SS-1421: populate the address of the branches from Solr before sending this list back
        populateAddressOfBranchFromSolr( branches, defaultRegion.getRegionId(), null, null );

        LOG.debug( "Method getBranchesUnderCompany executed sucessfully" );

        return branches;
    }


    /*
     * SS-1421: Method to query Solr for the branch address and populate the given Branch list. This method
     * caters to a) Searching for all branches within a Company as well as b) Searching for all branches within a region
     */
    private void populateAddressOfBranchFromSolr( List<Branch> branches, long regionId, Set<Long> branchIds, Company company )
    {
        try {
            String branchesSearchedString = null;

            //If the regionId is 0, do a Solr search for all branches under the company. Else search within the given region only.
            if ( regionId == 0 ) {
                branchesSearchedString = solrSearchService.searchBranches( "", company, CommonConstants.BRANCH_ID_SOLR,
                    branchIds, 0, Integer.MAX_VALUE );
            } else {
                branchesSearchedString = solrSearchService.searchBranchesByRegion( regionId, 0, Integer.MAX_VALUE );
            }

            //Proceed if Solr returns a non null String
            if ( branchesSearchedString != null ) {
                Type searchedBranchesList = new TypeToken<List<BranchFromSearch>>() {}.getType();
                List<BranchFromSearch> branchSearchedList = new Gson().fromJson( branchesSearchedString, searchedBranchesList );

                //Iterate over the searched branches
                for ( BranchFromSearch branchFromSearch : branchSearchedList ) {
                    for ( Branch branch : branches ) {
                        if ( branch.getBranchId() == branchFromSearch.getBranchId() ) {

                            //Populate the branch address into the branch objects
                            branch.setAddress1( branchFromSearch.getAddress1() );
                            branch.setAddress2( branchFromSearch.getAddress2() );
                        }
                    }
                }
            }
        } catch ( SolrException e ) {
            //Just log the exception in case there's some error fetching the branches from Solr. No need to propagate.
            LOG.error( "SolrException while searching for branches for region ID: " + regionId + ". Reason : " + e.getMessage(),
                e );
        } catch ( InvalidInputException e ) {
            //Just log the exception in case there's some error fetching the branches from Solr. No need to propagate.
            LOG.error( "InvalidInputException while searching for branches for region ID: " + regionId + ". Reason : "
                + e.getMessage(), e );
        }
    }


    /**
     * Method to get the default region of a company
     * 
     * @param company
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    @Override
    @Transactional
    public Region getDefaultRegionForCompany( Company company ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method getDefaultRegionForCompany called for :" + company );
        Region region = null;
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.COMPANY_COLUMN, company );
        queries.put( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.YES );
        List<Region> regions = regionDao.findByKeyValue( Region.class, queries );
        if ( regions == null || regions.isEmpty() ) {
            throw new NoRecordsFetchedException( "No default region found for company :" + company );
        }
        region = regions.get( 0 );

        LOG.debug( "Method getDefaultRegionForCompany excecuted successfully" );
        return region;
    }


    /**
     * Method to fetch the default branch associated with a region
     * 
     * @param region
     * @return
     * @throws NoRecordsFetchedException
     */
    @Override
    @Transactional
    public Branch getDefaultBranchForRegion( long regionId ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Method getDefaultBranchForRegion called for regionId : " + regionId );
        if ( regionId <= 0l ) {
            throw new InvalidInputException( "region id is invalid in getDefaultBranchForRegion" );
        }
        Branch branch = null;
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.REGION_COLUMN, regionDao.findById( Region.class, regionId ) );
        queries.put( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.YES );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );

        List<Branch> branches = branchDao.findByKeyValue( Branch.class, queries );
        if ( branches == null || branches.isEmpty() ) {
            // TODO add condition for max default branches
            throw new NoRecordsFetchedException( "No default branch present for regionId:" + regionId );
        }
        branch = branches.get( 0 );
        LOG.debug( "Method getDefaultBranchForRegion finished" );
        return branch;
    }


    /**
     * Method to get list of branches linked to a region
     */
    @Override
    @Transactional
    public List<Branch> getBranchesForRegion( String companyProfileName, String regionProfileName )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException
    {
        if ( companyProfileName == null || companyProfileName.isEmpty() ) {
            throw new InvalidInputException( "companyProfileName is null or empty in getBranchesForRegion" );
        }
        if ( regionProfileName == null || regionProfileName.isEmpty() ) {
            throw new InvalidInputException( "regionProfileName is null or empty in getBranchesForRegion" );
        }
        LOG.debug( "Method getBranchesForRegion called for companyProfileName:" + companyProfileName + " and regionProfileName:"
            + regionProfileName );
        List<Branch> branches = null;
        OrganizationUnitSettings regionSettings = profileManagementService.getRegionByProfileName( companyProfileName,
            regionProfileName );
        if ( regionSettings != null ) {
            branches = getBranchesByRegionId( regionSettings.getIden() );
        } else {
            throw new NoRecordsFetchedException( "No region settings found for regionProfileName : " + regionProfileName );
        }
        LOG.debug( "Method getBranchesForRegion executed successfully." );
        return branches;
    }


    @Override
    @Transactional
    public List<Branch> getBranchesByRegionId( long regionId ) throws InvalidInputException
    {
        LOG.debug( "Method getBranchesByRegionId called for regionId:" + regionId );

        if ( regionId <= 0l ) {
            throw new InvalidInputException( "region id is invalid while fetching branches" );
        }
        List<Branch> branches = null;
        List<String> columnNames = new ArrayList<String>();
        columnNames.add( CommonConstants.BRANCH_ID_COLUMN );
        columnNames.add( CommonConstants.BRANCH_NAME_COLUMN );
        columnNames.add( CommonConstants.PROFILE_NAME_COLUMN );

        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.REGION_COLUMN, regionDao.findById( Region.class, regionId ) );
        queries.put( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.NO );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );

        branches = branchDao.findProjectionsByKeyValue( Branch.class, columnNames, queries,
            CommonConstants.BRANCH_NAME_COLUMN );

        //SS-1421: populate the address of the branches from Solr before sending this list back
        populateAddressOfBranchFromSolr( branches, regionId, null, null );

        LOG.debug( "Method getBranchesByRegionId completed successfully" );
        return branches;
    }


    /**
     * Method to add a new region and assign the user to the newly created region if userId or
     * emailId is provided
     * 
     * @throws UserAssignmentException
     */
    @Override
    @Transactional
    public Map<String, Object> addNewRegionWithUser( User user, String regionName, int isDefaultBySystem, String address1,
        String address2, String country, String countryCode, String state, String city, String zipcode, long selectedUserId,
        String[] emailIdsArray, boolean isAdmin, boolean holdSendingMail, boolean isAddedByRealtechOrSSAdmin )
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        LOG.debug( "Method addNewRegionWithUser called for user:" + user + " regionName:" + regionName + " isDefaultBySystem:"
            + isDefaultBySystem + " selectedUserId:" + selectedUserId + " emailIdsArray:" + emailIdsArray + " isAdmin:"
            + isAdmin );
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, List<User>> userMap = new HashMap<String, List<User>>();
        Region region = addNewRegion( user, regionName, isDefaultBySystem, address1, address2, country, countryCode, state,
            city, zipcode );

        LOG.debug( "Adding default branch for the new region created" );
        addNewBranch( user, region.getRegionId(), CommonConstants.YES, CommonConstants.DEFAULT_BRANCH_NAME, null, null, null,
            null, null, null, null );

        /**
         * If userId or email is provided, call the service for adding and assigning user to the
         * newly created region
         */
        if ( selectedUserId > 0l ) {
            LOG.debug( "Fetching user for selectedUserId " + selectedUserId + "to assign to the region" );
            User assigneeUser = userDao.findById( User.class, selectedUserId );
            if ( assigneeUser == null ) {
                throw new NoRecordsFetchedException( "No user found in db for selectedUserId:" + selectedUserId );
            }
            if ( validateUserAssignment( user, assigneeUser, userMap ) ) {
                try {
                    assignRegionToUser( user, region.getRegionId(), assigneeUser, isAdmin );
                } catch ( InvalidInputException | NoRecordsFetchedException | SolrException e ) {
                    LOG.error( "Exception while assigning region to a user. Reason:" + e.getMessage(), e );
                    throw new UserAssignmentException( e.getMessage(), e );
                }
            }
        } else if ( emailIdsArray != null && emailIdsArray.length > 0 ) {
            LOG.debug( "Fetching users list to assign to the region" );
            userMap = getUsersFromEmailIdsAndInvite( emailIdsArray, user, holdSendingMail, true, isAddedByRealtechOrSSAdmin );
            List<User> assigneeUsers = userMap.get( CommonConstants.VALID_USERS_LIST );

            if ( assigneeUsers != null && !assigneeUsers.isEmpty() ) {
                for ( User assigneeUser : assigneeUsers ) {
                    if ( !validateUserAssignment( user, assigneeUser, userMap ) ) {
                        continue;
                    }
                    try {
                        assignRegionToUser( user, region.getRegionId(), assigneeUser, isAdmin );
                    } catch ( InvalidInputException | NoRecordsFetchedException | SolrException e ) {
                        LOG.error( "Exception while assigning region to a user. Reason:" + e.getMessage(), e );
                        throw new UserAssignmentException( e.getMessage(), e );
                    }
                }
            }
        }
        map.put( CommonConstants.REGION_OBJECT, region );
        if ( userMap != null ) {
            map.put( CommonConstants.INVALID_USERS_LIST, userMap.get( CommonConstants.INVALID_USERS_LIST ) );
            map.put( CommonConstants.INVALID_USERS_ASSIGN_LIST, userMap.get( CommonConstants.INVALID_USERS_ASSIGN_LIST ) );
        }
        LOG.debug( "Method addNewRegionWithUser completed successfully" );
        return map;
    }


    /**
     * Method to get the list of users for emailIds specified, and if the user doesn't exist for
     * that company invite the user
     * 
     * @param emailIdsArray
     * @param adminUser
     * @return
     * @throws InvalidInputException
     */
    @Override
    public Map<String, List<User>> getUsersFromEmailIdsAndInvite( String[] emailIdsArray, User adminUser,
        boolean holdSendingMail, boolean sendMail, boolean isAddedByRealtechOrSSAdmin ) throws InvalidInputException
    {
        LOG.debug( "Method getUsersFromEmailIds called for emailIdsArray:" + emailIdsArray );
        List<User> users = new ArrayList<User>();
        List<User> invalidUsers = new ArrayList<User>();
        Map<String, List<User>> usersMap = new HashMap<String, List<User>>();
        for ( String emailId : emailIdsArray ) {
            if ( emailId.contains( "\"" ) ) {
                emailId = emailId.replace( "\"", "" );
            }
            String firstName = "";
            String lastName = "";
            User user = null;
            String toRemove = null;
            if ( emailId.indexOf( "@" ) != -1 && emailId.indexOf( "." ) != -1 ) {
                if ( emailId.contains( " " ) ) {
                    String[] userArray = emailId.split( " " );
                    String[] userInformation = removeElements( userArray, "" );
                    List<String> tempList = new LinkedList<String>();
                    for ( String str : userInformation ) {
                        tempList.add( str );
                    }
                    String tempString = "";
                    for ( int i = 0; i < tempList.size(); i++ ) {

                        LOG.debug( "removing extra spaces " );
                        if ( tempList.get( i ).equalsIgnoreCase( "<" ) ) {
                            if ( i + 1 < tempList.size() ) {
                                if ( !tempList.get( i + 1 ).contains( "<" ) ) {
                                    tempString = tempList.get( i ).concat( tempList.get( i + 1 ) );

                                    toRemove = tempList.get( i + 1 );
                                    if ( i + 2 < tempList.size() ) {

                                        if ( tempList.get( i + 2 ).equalsIgnoreCase( ">" ) ) {
                                            tempString = tempString.concat( tempList.get( i + 2 ) );


                                        }
                                    }
                                }
                            }
                        } else if ( tempList.get( i ).equalsIgnoreCase( ">" ) ) {
                            if ( !tempList.get( i - 1 ).contains( ">" ) ) {
                                if ( tempString.isEmpty() ) {
                                    tempString = tempList.get( i - 1 ).concat( tempList.get( i ) );
                                    toRemove = tempList.get( i - 1 );
                                }

                            }
                        }

                    }
                    if ( !tempString.isEmpty() ) {
                        tempList.add( tempString );
                    }
                    Iterator<String> it = tempList.iterator();
                    while ( it.hasNext() ) {
                        String iteratedValue = it.next();
                        if ( iteratedValue.equalsIgnoreCase( "<" ) || iteratedValue.equalsIgnoreCase( ">" ) ) {
                            it.remove();
                        }
                        if ( toRemove != null ) {
                            if ( iteratedValue.equalsIgnoreCase( toRemove ) ) {
                                it.remove();
                            }
                        }
                    }
                    userInformation = tempList.toArray( new String[tempList.size()] );
                    if ( userInformation.length >= 3 ) {
                        LOG.debug( "This contains middle name as well" );
                        for ( int i = 0; i < userInformation.length - 1; i++ ) {
                            firstName = firstName + userInformation[i] + " ";
                        }
                        firstName = firstName.trim();
                        lastName = userInformation[userInformation.length - 1];
                        if ( lastName.contains( "<" ) ) {
                            emailId = lastName.substring( lastName.indexOf( "<" ) + 1, lastName.length() - 1 );
                            lastName = lastName.substring( 0, lastName.indexOf( "<" ) );
                            if ( lastName.equalsIgnoreCase( "" ) ) {
                                lastName = userInformation[userInformation.length - 2];
                                if ( firstName.contains( lastName ) ) {
                                    firstName = firstName.substring( 0, firstName.indexOf( lastName ) );
                                }
                            }
                        }

                    } else if ( userInformation.length == 2 ) {
                        firstName = userInformation[0];
                        lastName = userInformation[1];
                        if ( lastName.contains( "<" ) ) {
                            emailId = lastName.substring( lastName.indexOf( "<" ) + 1, lastName.length() - 1 );
                            lastName = lastName.substring( 0, lastName.indexOf( "<" ) );
                        }
                    }
                } else {
                    LOG.debug( "Contains no space hence wont have a last name" );
                    lastName = null;
                    if ( emailId.contains( "<" ) ) {
                        firstName = emailId.substring( 0, emailId.indexOf( "<" ) );
                        if ( firstName.equalsIgnoreCase( "" ) ) {
                            firstName = emailId.substring( emailId.indexOf( "<" ) + 1, emailId.indexOf( "@" ) );
                        }
                        emailId = emailId.substring( emailId.indexOf( "<" ) + 1, emailId.indexOf( ">" ) );

                    } else {
                        LOG.debug( "This doesnt contain a first name and last name" );
                        firstName = emailId.substring( 0, emailId.indexOf( "@" ) );
                        lastName = "";
                    }

                }
            }
            if ( validateEmail( emailId ) ) {

                try {
                    user = userManagementService.getUserByEmailAddress( emailId );
                } catch ( NoRecordsFetchedException e ) {
                    /**
                     * if no user is present with the specified emailId, send an invite to register
                     */
                    try {
                        user = userManagementService.inviteUserToRegister( adminUser, firstName, lastName, emailId,
                            holdSendingMail, sendMail, false, isAddedByRealtechOrSSAdmin );
                    } catch ( UserAlreadyExistsException | UndeliveredEmailException | NoRecordsFetchedException e1 ) {
                        LOG.debug( "Exception in getUsersFromEmailIds while inviting a new user. Reason:" + e1.getMessage(),
                            e1 );
                    }
                }
            } else {
                LOG.error( "This email address " + emailId + " is not a valid email" );
                User invalidUser = new User();
                invalidUser.setEmailId( emailId );
                invalidUsers.add( invalidUser );
            }
            if ( user != null ) {
                users.add( user );

            }


        }
        usersMap.put( CommonConstants.VALID_USERS_LIST, users );
        usersMap.put( CommonConstants.INVALID_USERS_LIST, invalidUsers );
        LOG.debug( "Method getUsersFromEmailIds executed successfully. Returning users size :" + users.size() );
        return usersMap;
    }


    public static String[] removeElements( String[] input, String deleteMe )
    {
        List<String> result = new LinkedList<String>();

        for ( String item : input )
            if ( !deleteMe.equals( item ) )
                result.add( item );

        String[] modifiedArray = result.toArray( new String[result.size()] );
        return modifiedArray;
    }


    /**
     * Method to validate single/multiple emailIds provided for assigning a user to a hierarchy
     * level
     * 
     * @param selectedUserId
     * @param selectedUserEmail
     * @return
     * @throws InvalidInputException
     */
    @Override
    public Boolean validateEmail( String emailId ) throws InvalidInputException
    {
        LOG.debug( "Method validateAndParseEmailIds called" );
        Pattern pattern = Pattern.compile( CommonConstants.EMAIL_REGEX, Pattern.CASE_INSENSITIVE );
        Matcher matcher = pattern.matcher( emailId );
        return matcher.matches();
    }


    /***
     * 
     * @param userProfileNew
     * @param userProfiles
     * @return
     */
    int checkWillNewProfileBePrimary( UserProfile userProfileNew, List<UserProfile> userProfiles )
    {

        LOG.debug( "Method checkWillNewProfileBePrimary called in OrganizationManagementService for email id"
            + userProfileNew.getEmailId() );

        int isPrimary = CommonConstants.IS_PRIMARY_FALSE;
        boolean noOldProfileIsPrimary = true;

        if ( userProfiles != null && !userProfiles.isEmpty() ) {
            for ( UserProfile profile : userProfiles ) {

                if ( profile.getIsPrimary() == CommonConstants.IS_PRIMARY_TRUE ) {

                    LOG.debug( "An old primary profile founded for email id " + userProfileNew.getEmailId() );

                    noOldProfileIsPrimary = false;

                    boolean isOldProfileDefault = false;
                    boolean isOldProfileAdmin = false;
                    boolean isOldProfileAgent = false;
                    //get the value of all three variables
                    Branch branch = branchDao.findById( Branch.class, profile.getBranchId() );
                    if ( branch != null && branch.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_YES ) {
                        isOldProfileDefault = true;
                    }

                    if ( profile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                        isOldProfileAgent = true;
                    } else {
                        isOldProfileAdmin = true;
                    }
                    //if old primary profile is default than remove primary from that and mark new profile as primary
                    if ( isOldProfileDefault ) {
                        LOG.debug( "Old primary profile has a default branch " );
                        //check if new profile is for default branch
                        Branch newProfileBranch = branchDao.findById( Branch.class, userProfileNew.getBranchId() );
                        //if new profile's branch is default than new profile will not be primary
                        if ( newProfileBranch != null
                            && newProfileBranch.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_YES
                            && userProfileNew.getProfilesMaster()
                                .getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {

                            Region newProfileRegion = regionDao.findById( Region.class, userProfileNew.getRegionId() );

                            // if both branches are default and if new profiles region is not default than make new profile as primary
                            if ( newProfileRegion != null
                                && newProfileRegion.getIsDefaultBySystem() != CommonConstants.IS_DEFAULT_BY_SYSTEM_YES ) {
                                isPrimary = CommonConstants.IS_PRIMARY_TRUE;
                            } else {
                                isPrimary = CommonConstants.IS_PRIMARY_FALSE;
                            }
                            // if new profile's branch is not default than make new profile as primary and change old one  	
                        } else {
                            profile.setIsPrimary( CommonConstants.IS_PRIMARY_FALSE );
                            userProfileDao.update( profile );
                            isPrimary = CommonConstants.IS_PRIMARY_TRUE;
                        }

                    } else if ( isOldProfileAdmin ) {
                        LOG.debug(
                            "Old primary profile is an admin profile of type " + profile.getProfilesMaster().getProfile() );
                        //if old profile is for admin and new is for agent than remove primary from old and mark new profile as primary
                        if ( userProfileNew.getProfilesMaster()
                            .getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                            profile.setIsPrimary( CommonConstants.IS_PRIMARY_FALSE );
                            userProfileDao.update( profile );
                            isPrimary = CommonConstants.IS_PRIMARY_TRUE;
                        } else {
                            isPrimary = CommonConstants.IS_PRIMARY_FALSE;
                        }
                        // if old profile is for agent and its not default than mark new profile as not primary
                    } else if ( isOldProfileAgent ) {
                        LOG.debug( "old primary profile is an agent profile" );
                        isPrimary = CommonConstants.IS_PRIMARY_FALSE;
                    }

                }
            }
            //if no old profile is there for user than make new profile as primary
        } else {
            LOG.debug( "No old profile found for user. New Profile will be primary" );
            isPrimary = CommonConstants.IS_PRIMARY_TRUE;
        }

        // if no old profile is primary than also new profile will be primary
        if ( noOldProfileIsPrimary ) {
            LOG.debug( "No old profile is primary for user so new profile will be primary" );
            isPrimary = CommonConstants.IS_PRIMARY_TRUE;
        }
        return isPrimary;
    }


    /**
     * Method to assign a region to a user
     * 
     * @throws SolrException
     */
    @Transactional
    @Override
    public void assignRegionToUser( User adminUser, long regionId, User assigneeUser, boolean isAdmin )
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        if ( adminUser == null ) {
            throw new InvalidInputException( "Admin user is null in assignRegionToUser" );
        }
        if ( regionId <= 0l ) {
            throw new InvalidInputException( "Region id is invalid in assignRegionToUser" );
        }
        if ( assigneeUser == null ) {
            throw new InvalidInputException( "assignee user is null in assignRegionToUser" );
        }
        LOG.debug( "Method to assignRegionToUser called for regionId : " + regionId + " and assigneeUser : "
            + assigneeUser.getUserId() + " isAdmin:" + isAdmin );

        List<UserProfile> userProfiles = assigneeUser.getUserProfiles();
        if ( userProfiles == null || userProfiles.isEmpty() ) {
            userProfiles = new ArrayList<UserProfile>();
        }

        int profileMasterId = 0;
        if ( isAdmin ) {
            profileMasterId = CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID;
        } else {
            profileMasterId = CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID;
        }

        LOG.debug( "Fetching default branch for region : " + regionId );
        Branch defaultBranch = getDefaultBranchForRegion( regionId );

        UserProfile userProfileNew = userManagementService.createUserProfile( assigneeUser, adminUser.getCompany(),
            assigneeUser.getEmailId(), assigneeUser.getUserId(), defaultBranch.getBranchId(), regionId, profileMasterId,
            CommonConstants.IS_PRIMARY_FALSE, CommonConstants.DASHBOARD_STAGE, CommonConstants.STATUS_ACTIVE,
            String.valueOf( adminUser.getUserId() ), String.valueOf( adminUser.getUserId() ) );


        // check if user profile already exists
        int indexToRemove = -1;
        if ( userProfiles != null && !userProfiles.isEmpty() ) {
            for ( UserProfile profile : userProfiles ) {
                if ( profile.getRegionId() == userProfileNew.getRegionId()
                    && profile.getBranchId() == userProfileNew.getBranchId()
                    && profile.getProfilesMaster() == userProfileNew.getProfilesMaster()
                    && profile.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                    throw new InvalidInputException(
                        messageUtils.getDisplayMessage( DisplayMessageConstants.USER_ASSIGNMENT_ALREADY_EXISTS,
                            DisplayMessageType.ERROR_MESSAGE ).getMessage() );
                }

                // Updating existing assignment
                else if ( profile.getRegionId() == userProfileNew.getRegionId()
                    && profile.getBranchId() == userProfileNew.getBranchId()
                    && profile.getProfilesMaster() == userProfileNew.getProfilesMaster()
                    && profile.getStatus() == CommonConstants.STATUS_INACTIVE ) {
                    indexToRemove = userProfiles.indexOf( profile );
                    profile.setStatus( CommonConstants.STATUS_ACTIVE );
                    userProfileNew = profile;
                }
            }
        }

        //check if new profile will be primary or not
        int isPrimary = checkWillNewProfileBePrimary( userProfileNew, userProfiles );
        userProfileNew.setIsPrimary( isPrimary );

        //move reviews
        if ( userProfileNew.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
            surveyDetailsDao.updateBranchIdRegionIdForAllSurveysOfAgent( assigneeUser.getUserId(), defaultBranch.getBranchId(),
                regionId );
        }

        // Remove if the profile from list
        if ( indexToRemove != -1 ) {
            userProfiles.remove( indexToRemove );
        }

        userProfileDao.save( userProfileNew );
        if ( assigneeUser.getIsAtleastOneUserprofileComplete() == CommonConstants.STATUS_INACTIVE ) {
            LOG.debug( "Updating isAtleastOneProfileComplete as active for user : " + assigneeUser.getUserId() );
            assigneeUser.setIsAtleastOneUserprofileComplete( CommonConstants.STATUS_ACTIVE );
            userDao.update( assigneeUser );
        }

        /**
         * add newly created user profile to the list of user profiles in user object
         */
        userProfiles.add( userProfileNew );
        assigneeUser.setUserProfiles( userProfiles );
        userManagementService.setProfilesOfUser( assigneeUser );

        AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( assigneeUser.getUserId() );
        assigneeUser.setProfileName( agentSettings.getProfileName() );
        assigneeUser.setProfileUrl( agentSettings.getProfileUrl() );
        solrSearchService.addUserToSolr( assigneeUser );

        userManagementService.updateUserCountModificationNotification( assigneeUser.getCompany() );

        LOG.debug(
            "Method to assignRegionToUser finished for regionId : " + regionId + " and userId : " + assigneeUser.getUserId() );
    }


    /**
     * Method to add a new region and assign the user to the newly created branch if userId or
     * emailId is provided
     * 
     * @throws UserAssignmentException
     */
    @Override
    @Transactional
    public Map<String, Object> addNewBranchWithUser( User user, String branchName, long regionId, int isDefaultBySystem,
        String address1, String address2, String country, String countryCode, String state, String city, String zipcode,
        long selectedUserId, String[] emailIdsArray, boolean isAdmin, boolean holdSendingMail, boolean isAddedByRealtechOrSSAdmin )
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, List<User>> userMap = new HashMap<String, List<User>>();

        LOG.debug( "Method addNewBranchWithUser called for user:" + user + " branchName:" + branchName + "regionId: " + regionId
            + " isDefaultBySystem:" + isDefaultBySystem + " selectedUserId:" + selectedUserId + " emailIdsArray:"
            + emailIdsArray + " isAdmin:" + isAdmin );

        Branch branch = addNewBranch( user, regionId, isDefaultBySystem, branchName, address1, address2, country, countryCode,
            state, city, zipcode );

        /**
         * If userId or email is provided, call the service for adding and assigning user to the
         * newly created branch
         */
        if ( selectedUserId > 0l ) {
            LOG.debug( "Fetching user for selectedUserId " + selectedUserId + "to assign to the branch" );
            User assigneeUser = userDao.findById( User.class, selectedUserId );
            if ( assigneeUser == null ) {
                throw new NoRecordsFetchedException( "No user found in db for selectedUserId:" + selectedUserId );
            }
            try {
                assignBranchToUser( user, branch.getBranchId(), branch.getRegion().getRegionId(), assigneeUser, isAdmin );
            } catch ( InvalidInputException | NoRecordsFetchedException | SolrException e ) {
                LOG.error( "Exception while assigning branch to a user. Reason:" + e.getMessage(), e );
                throw new UserAssignmentException( e.getMessage(), e );
            }
        } else if ( emailIdsArray != null && emailIdsArray.length > 0 ) {
            LOG.debug( "Fetching users list to assign to the branch" );
            userMap = getUsersFromEmailIdsAndInvite( emailIdsArray, user, holdSendingMail, true, isAddedByRealtechOrSSAdmin );
            List<User> assigneeUsers = userMap.get( CommonConstants.VALID_USERS_LIST );

            if ( assigneeUsers != null && !assigneeUsers.isEmpty() ) {
                for ( User assigneeUser : assigneeUsers ) {
                    if ( !validateUserAssignment( user, assigneeUser, userMap ) ) {
                        continue;
                    }
                    try {
                        assignBranchToUser( user, branch.getBranchId(), branch.getRegion().getRegionId(), assigneeUser,
                            isAdmin );
                    } catch ( InvalidInputException | NoRecordsFetchedException | SolrException e ) {
                        LOG.error( "Exception while assigning branch to a user. Reason:" + e.getMessage(), e );
                        throw new UserAssignmentException( e.getMessage(), e );
                    }
                }
            }
        }
        map.put( CommonConstants.BRANCH_OBJECT, branch );
        if ( userMap != null ) {
            map.put( CommonConstants.INVALID_USERS_LIST, userMap.get( CommonConstants.INVALID_USERS_LIST ) );
            map.put( CommonConstants.INVALID_USERS_ASSIGN_LIST, userMap.get( CommonConstants.INVALID_USERS_ASSIGN_LIST ) );
        }
        LOG.debug( "Method addNewBranchWithUser completed successfully" );
        return map;
    }


    /**
     * Method to assign a branch to a user
     */
    @Override
    @Transactional
    public void assignBranchToUser( User adminUser, long branchId, long regionId, User assigneeUser, boolean isAdmin )
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        if ( adminUser == null ) {
            throw new InvalidInputException( "Admin user is null in assignBranchToUser" );
        }
        if ( branchId <= 0l ) {
            throw new InvalidInputException( "Branch id is invalid in assignBranchToUser" );
        }
        if ( regionId <= 0l ) {
            throw new InvalidInputException( "Region id is invalid in assignBranchToUser" );
        }
        if ( assigneeUser == null ) {
            throw new InvalidInputException( "assignee user is null in assignBranchToUser" );
        }
        LOG.debug( "Method assignBranchToUser called for adminUser:" + adminUser + " branchId:" + branchId + " regionId"
            + regionId + "assigneeUser:" + assigneeUser + " isAdmin:" + isAdmin );

        List<UserProfile> userProfiles = assigneeUser.getUserProfiles();
        if ( userProfiles == null || userProfiles.isEmpty() ) {
            userProfiles = new ArrayList<UserProfile>();
        }

        int profileMasterId = 0;
        if ( isAdmin ) {
            profileMasterId = CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID;
        } else {
            profileMasterId = CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID;
        }

        UserProfile userProfileNew = userManagementService.createUserProfile( assigneeUser, adminUser.getCompany(),
            assigneeUser.getEmailId(), assigneeUser.getUserId(), branchId, regionId, profileMasterId,
            CommonConstants.IS_PRIMARY_FALSE, CommonConstants.DASHBOARD_STAGE, CommonConstants.STATUS_ACTIVE,
            String.valueOf( adminUser.getUserId() ), String.valueOf( adminUser.getUserId() ) );

        // check if user profile already exists
        int indexToRemove = -1;
        if ( userProfiles != null && !userProfiles.isEmpty() ) {
            for ( UserProfile profile : userProfiles ) {
                if ( profile.getBranchId() == userProfileNew.getBranchId()
                    && profile.getProfilesMaster() == userProfileNew.getProfilesMaster()
                    && profile.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                    throw new InvalidInputException(
                        messageUtils.getDisplayMessage( DisplayMessageConstants.USER_ASSIGNMENT_ALREADY_EXISTS,
                            DisplayMessageType.ERROR_MESSAGE ).getMessage() );
                }

                // Updating existing assignment
                else if ( profile.getBranchId() == userProfileNew.getBranchId()
                    && profile.getProfilesMaster() == userProfileNew.getProfilesMaster()
                    && profile.getStatus() == CommonConstants.STATUS_INACTIVE ) {
                    indexToRemove = userProfiles.indexOf( profile );
                    profile.setStatus( CommonConstants.STATUS_ACTIVE );
                    userProfileNew = profile;
                }
            }
        }

        //check if profile will be primary or not
        int isPrimary = checkWillNewProfileBePrimary( userProfileNew, userProfiles );
        userProfileNew.setIsPrimary( isPrimary );

        //move reviews
        if ( userProfileNew.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
            surveyDetailsDao.updateBranchIdRegionIdForAllSurveysOfAgent( assigneeUser.getUserId(), branchId, regionId );
        }


        // Remove if the profile from list
        if ( indexToRemove != -1 ) {
            userProfiles.remove( indexToRemove );
        }
        userProfileDao.save( userProfileNew );

        if ( assigneeUser.getIsAtleastOneUserprofileComplete() == CommonConstants.STATUS_INACTIVE ) {
            LOG.debug( "Updating isAtleastOneProfileComplete as active for user : " + assigneeUser.getUserId() );
            assigneeUser.setIsAtleastOneUserprofileComplete( CommonConstants.STATUS_ACTIVE );
            userDao.update( assigneeUser );
        }

        /**
         * add newly created user profile to the list of user profiles in user object
         */
        userProfiles.add( userProfileNew );
        assigneeUser.setUserProfiles( userProfiles );
        userManagementService.setProfilesOfUser( assigneeUser );

        AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( assigneeUser.getUserId() );
        assigneeUser.setProfileName( agentSettings.getProfileName() );
        assigneeUser.setProfileUrl( agentSettings.getProfileUrl() );
        solrSearchService.addUserToSolr( assigneeUser );

        userManagementService.updateUserCountModificationNotification( assigneeUser.getCompany() );

        LOG.debug( "Method assignBranchToUser executed successfully" );
    }


    /**
     * Method to add a new user or assign existing user under a company/region or branch
     * 
     * @throws UserAssignmentException
     */
    @Override
    @Transactional
    public Map<String, Object> addIndividual( User adminUser, long selectedUserId, long branchId, long regionId,
        String[] emailIdsArray, boolean isAdmin, boolean holdSendingMail, boolean sendMail, boolean isAddedByRealtechOrSSAdmin, boolean isSocialMonitorAdmin, String firstName, String lastName )
        throws InvalidInputException, NoRecordsFetchedException, SolrException, UserAssignmentException
    {
        LOG.debug( "Method addIndividual called for adminUser:" + adminUser + " branchId:" + branchId + " regionId:" + regionId
            + " isAdmin:" + isAdmin );
        List<User> assigneeUsers = null;
        Map<String, List<User>> userMap = new HashMap<String, List<User>>();
        Map<String, Object> map = new HashMap<String, Object>();
        if ( selectedUserId > 0l ) {
            LOG.debug( "Fetching user for selectedUserId " + selectedUserId );
            User assigneeUser = userDao.findById( User.class, selectedUserId );
            if ( assigneeUser == null ) {
                throw new NoRecordsFetchedException( "No user found in db for selectedUserId:" + selectedUserId );
            }
            assigneeUsers = new ArrayList<User>();
            assigneeUsers.add( assigneeUser );
        } else if ( emailIdsArray != null && emailIdsArray.length > 0 ) {
            LOG.debug( "Fetching users list for the email addresses provided" );
            if(firstName == null || firstName.isEmpty())
                userMap = getUsersFromEmailIdsAndInvite( emailIdsArray, adminUser, holdSendingMail, sendMail, isAddedByRealtechOrSSAdmin );
            else
                userMap = getUsersFromEmailIdsAndInvite( emailIdsArray, adminUser, holdSendingMail, sendMail, isAddedByRealtechOrSSAdmin, firstName, lastName );
            assigneeUsers = userMap.get( CommonConstants.VALID_USERS_LIST );
        }

        if ( (assigneeUsers != null && !assigneeUsers.isEmpty() )  && !isSocialMonitorAdmin) {
            /**
             * if branchId is provided, add the individual to specified branch
             */
            if ( branchId > 0l ) {
                LOG.debug( "assigning individual(s) to branch :" + branchId + " in addIndividual" );
                for ( User assigneeUser : assigneeUsers ) {
                    if ( !validateUserAssignment( adminUser, assigneeUser, userMap ) ) {
                        continue;
                    }
                    assignBranchToUser( adminUser, branchId, regionId, assigneeUser, isAdmin );
                }
            }
            /**
             * else if regionId is provided, add the individual to specified region
             */
            else if ( regionId > 0l ) {
                LOG.debug( "assigning individual(s) to region :" + regionId + " in addIndividual" );
                for ( User assigneeUser : assigneeUsers ) {
                    if ( !validateUserAssignment( adminUser, assigneeUser, userMap ) ) {
                        continue;
                    }
                    assignRegionToUser( adminUser, regionId, assigneeUser, isAdmin );
                }
            }
            /**
             * else assign the individual to company (i.e under default region)
             */
            else {
                LOG.debug( "assigning individual(s) to company in addIndividual" );
                Region region = getDefaultRegionForCompany( adminUser.getCompany() );
                if ( region == null ) {
                    throw new NoRecordsFetchedException( "No default region found for company while adding individual" );
                }
                for ( User assigneeUser : assigneeUsers ) {
                    if ( !validateUserAssignment( adminUser, assigneeUser, userMap ) ) {
                        continue;
                    }
                    assignRegionToUser( adminUser, region.getRegionId(), assigneeUser, isAdmin );
                }
            }

        }
        //update UserProfile if the user is a Social monitor admin
        int flag = 0;
        if ( isSocialMonitorAdmin ) {
            for ( User assigneeUser : assigneeUsers ) {
                Region region = getDefaultRegionForCompany( adminUser.getCompany() );
                Branch branch = getDefaultBranchForRegion(region.getRegionId());
                if ( selectedUserId == 0l ) {
                    LOG.debug( "assigning individual(s) to company in addIndividual" );
                   
                    if ( validateUserAssignment( adminUser, assigneeUser, userMap ) ) {
                        assignRegionToUser( adminUser, region.getRegionId(), assigneeUser, isAdmin );
                    }

                }

                flag = 1;
                List<UserProfile> userProfiles = assigneeUser.getUserProfiles();
                if ( userProfiles != null && !userProfiles.isEmpty() ) {
                    for ( UserProfile profile : userProfiles ) {
                        if ( ( profile.getUser().getUserId() == ( assigneeUser.getUserId() ) )
                            && profile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_SM_ADMIN_PROFILE_ID
                            && profile.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                            flag = 2;
                            break;
                        }
                    }
                }
                if ( flag == 1 ) {
                		// create a social monitor profile for user using default branch and region of company
                    UserProfile userProfile = userManagementService.createUserProfile( assigneeUser, adminUser.getCompany(),
                        assigneeUser.getEmailId(), assigneeUser.getUserId(), branch.getBranchId(),
                        region.getRegionId(), CommonConstants.PROFILES_MASTER_SM_ADMIN_PROFILE_ID,
                        CommonConstants.IS_PRIMARY_FALSE, CommonConstants.PROFILE_STAGES_COMPLETE,
                        CommonConstants.STATUS_ACTIVE, String.valueOf( adminUser.getUserId() ),
                        String.valueOf( adminUser.getUserId() ) );
                    userProfileDao.save( userProfile );
                }

            }

        }
        LOG.debug( "Method addNewIndividual executed successfully" );
        if ( userMap != null ) {
            map.put( CommonConstants.INVALID_USERS_LIST, userMap.get( CommonConstants.INVALID_USERS_LIST ) );
            map.put( CommonConstants.INVALID_USERS_ASSIGN_LIST, userMap.get( CommonConstants.INVALID_USERS_ASSIGN_LIST ) );
            map.put( CommonConstants.VALID_USERS_LIST, userMap.get( CommonConstants.VALID_USERS_LIST ) );
        }
        return map;
    }


    /**
     * Fetch list of branches in a company
     * 
     * @param company
     * @return List of branches
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public List<Branch> getAllBranchesForCompany( Company company ) throws InvalidInputException
    {
        if ( company == null ) {
            LOG.error( "Company object passed can not be null" );
            throw new InvalidInputException( "Invalid Company passed" );
        }
        LOG.debug( "Fetching the list of branches for company :" + company.getCompany() );
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.COMPANY_COLUMN, company );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        List<Branch> branchList = branchDao.findByKeyValue( Branch.class, queries );
        LOG.debug( "Branch list fetched for the company " + company );
        return branchList;
    }


    @Override
    @Transactional
    public List<Branch> getAllBranchesForCompanyWithProjections( Company company ) throws InvalidInputException
    {
        if ( company == null ) {
            LOG.error( "Company object passed can not be null" );
            throw new InvalidInputException( "Invalid Company passed" );
        }
        LOG.debug( "Fetching the list of branches for company :" + company.getCompany() );

        List<String> projections = new ArrayList<>();
        projections.add( CommonConstants.BRANCH_ID_COLUMN );
        projections.add( CommonConstants.BRANCH_NAME_COLUMN );
        projections.add( CommonConstants.IS_DEFAULT_BY_SYSTEM );

        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.COMPANY_COLUMN, company );
        queries.put( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.STATUS_INACTIVE );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );

        List<Branch> branchList = branchDao.findProjectionsAscOrderByKeyValue( Branch.class, projections, queries,
            CommonConstants.BRANCH_OBJECT );
        LOG.debug( "Branch list fetched for the company " + company );
        return branchList;
    }


    /**
     * Fetch list of regions in a company
     * 
     * @param company
     * @return List of regions
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public List<Region> getAllRegionsForCompany( Company company ) throws InvalidInputException
    {
        if ( company == null ) {
            LOG.error( "Company object passed can not be null" );
            throw new InvalidInputException( "Invalid Company passed" );
        }

        LOG.debug( "Fetching the list of regions for company :" + company.getCompany() );

        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.COMPANY_COLUMN, company );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );

        List<Region> regionList = regionDao.findByKeyValue( Region.class, queries );
        LOG.debug( "Region list fetched for the company " + company );
        return regionList;
    }


    @Override
    @Transactional
    public List<Region> getAllRegionsForCompanyWithProjections( Company company ) throws InvalidInputException
    {
        if ( company == null ) {
            LOG.error( "Company object passed can not be null" );
            throw new InvalidInputException( "Invalid Company passed" );
        }
        LOG.debug( "Fetching the list of regions for company :" + company.getCompany() );

        List<String> projections = new ArrayList<>();
        projections.add( CommonConstants.REGION_ID_COLUMN );
        projections.add( CommonConstants.REGION_COLUMN );
        projections.add( CommonConstants.IS_DEFAULT_BY_SYSTEM );

        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.COMPANY_COLUMN, company );
        queries.put( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.STATUS_INACTIVE );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );

        List<Region> regionList = regionDao.findProjectionsAscOrderByKeyValue( Region.class, projections, queries,
            CommonConstants.REGION_OBJECT );
        LOG.debug( "Region list fetched for the company " + company );
        return regionList;
    }


    /**
     * Updates status of a branch
     * 
     * @param user
     * @param branchId
     * @param status
     * @throws InvalidInputException
     * @throws SolrException
     */
    @Override
    @Transactional
    public void updateBranchStatus( User user, long branchId, int status ) throws InvalidInputException, SolrException
    {
        LOG.debug( "Update branch of id :" + branchId + " status to :" + status );
        if ( user == null ) {
            throw new InvalidInputException( "User is null in updateRegionStatus" );
        }
        if ( branchId <= 0l ) {
            throw new InvalidInputException( "BranchId is not set in updateRegionStatus" );
        }

        LOG.debug( "Fetching the branch object by ID" );
        Branch branch = branchDao.findById( Branch.class, branchId );
        if ( branch == null ) {
            LOG.error( "No branch present with the branch Id :" + branchId );
            throw new InvalidInputException( "No branch present with the branch Id :" + branchId );
        }

        branch.setStatus( status );
        branch.setModifiedBy( String.valueOf( user.getUserId() ) );
        branch.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        branchDao.update( branch );

        LOG.debug( "Updating document of the branch in solr" );
        solrSearchService.addOrUpdateBranchToSolr( branch );
        LOG.debug( "Branch status for branch ID :" + branchId + "/t successfully updated to:" + status );
    }


    /**
     * Updates the status of region
     * 
     * @param regionId
     * @throws InvalidInputException
     * @throws SolrException
     */
    @Override
    @Transactional
    public void updateRegionStatus( User user, long regionId, int status ) throws InvalidInputException, SolrException
    {
        LOG.debug( "Method updateRegionStatus called for regionId : " + regionId + " and status : " + status );
        if ( user == null ) {
            throw new InvalidInputException( "User is null in updateRegionStatus" );
        }
        if ( regionId <= 0l ) {
            throw new InvalidInputException( "RegionId is not set in updateRegionStatus" );
        }
        Region region = regionDao.findById( Region.class, regionId );
        if ( region == null ) {
            LOG.error( "No region present with the region Id :" + regionId );
            throw new InvalidInputException( "No region present with the region Id :" + regionId );
        }
        region.setStatus( status );
        region.setModifiedBy( String.valueOf( user.getUserId() ) );
        region.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        regionDao.update( region );

        LOG.debug( "Updating document of the region in solr" );
        solrSearchService.addOrUpdateRegionToSolr( region );

        LOG.debug( "Region status for region ID :" + regionId + "/t successfully updated to " + status );
    }


    /**
     * Fetch list of branches in a company for a Region
     * 
     * @param regionId
     * @return List of branches
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public List<Branch> getAllBranchesInRegion( long regionId ) throws InvalidInputException
    {
        if ( regionId <= 0l ) {
            throw new InvalidInputException( "RegionId is not set in getAllBranchesForRegion" );
        }
        Region region = regionDao.findById( Region.class, regionId );
        if ( region == null ) {
            LOG.error( "No region present with the region Id :" + regionId );
            throw new InvalidInputException( "No region present with the region Id :" + regionId );
        }
        LOG.debug( "Fetching the list of branches for region :" + region );

        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.REGION_COLUMN, region );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        List<Branch> branchList = branchDao.findByKeyValue( Branch.class, queries );

        LOG.debug( "Branch list fetched for the region " + region );
        return branchList;
    }


    @Override
    @Transactional
    public List<Branch> getAllBranchesInRegionWithProjections( long regionId ) throws InvalidInputException
    {
        if ( regionId <= 0l ) {
            throw new InvalidInputException( "RegionId is not set in getAllBranchesForRegion" );
        }

        Region region = regionDao.findById( Region.class, regionId );
        if ( region == null ) {
            LOG.error( "No region present with the region Id :" + regionId );
            throw new InvalidInputException( "No region present with the region Id :" + regionId );
        }
        LOG.debug( "Fetching the list of branches for region :" + region );

        List<String> projections = new ArrayList<>();
        projections.add( CommonConstants.BRANCH_ID_COLUMN );
        projections.add( CommonConstants.BRANCH_NAME_COLUMN );
        projections.add( CommonConstants.IS_DEFAULT_BY_SYSTEM );

        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.REGION_COLUMN, region );
        queries.put( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.STATUS_INACTIVE );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );

        List<Branch> branchList = branchDao.findProjectionsByKeyValue( Branch.class, projections, queries );

        LOG.debug( "Branch list fetched for the region " + region );
        return branchList;
    }


    /**
     * Method to fetch count of branches in a company for a Region
     * 
     * @param regionId
     * @return List of branches
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public long getCountBranchesInRegion( long regionId ) throws InvalidInputException
    {
        if ( regionId <= 0l ) {
            throw new InvalidInputException( "RegionId is not set in getAllBranchesForRegion" );
        }
        Region region = regionDao.findById( Region.class, regionId );
        if ( region == null ) {
            LOG.error( "No region present with the region Id :" + regionId );
            throw new InvalidInputException( "No region present with the region Id :" + regionId );
        }
        LOG.debug( "Fetching the list of branches for region :" + region );

        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.REGION_COLUMN, region );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        queries.put( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.NO );
        long branchCount = branchDao.findNumberOfRowsByKeyValue( Branch.class, queries );

        LOG.debug( "Branch list fetched for the region " + region );
        return branchCount;
    }


    /**
     * Method to fetch UserProfiles associated with a branch
     * 
     * @param branchId
     * @return
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public List<UserProfile> getAllUserProfilesInBranch( long branchId ) throws InvalidInputException
    {
        if ( branchId <= 0l ) {
            throw new InvalidInputException( "BranchId is not set in getAllUserProfilesForBranch" );
        }
        LOG.debug( "Fetching the list of users for branch :" + branchId );

        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.BRANCH_ID_COLUMN, branchId );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        List<UserProfile> userList = userProfileDao.findByKeyValue( UserProfile.class, queries );

        LOG.debug( "Users list fetched for the branch " + branchId );
        return userList;
    }


    /**
     * Method to fetch count of UserProfiles associated with a branch
     * 
     * @param company
     * @param branchId
     * @return
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public long getCountUsersInBranch( long branchId ) throws InvalidInputException
    {
        if ( branchId <= 0l ) {
            throw new InvalidInputException( "RegionId is not set in getAllUserProfilesForBranch" );
        }
        LOG.debug( "Fetching the list of users for branch :" + branchId );

        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.BRANCH_ID_COLUMN, branchId );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        long usersCount = userProfileDao.findNumberOfRowsByKeyValue( UserProfile.class, queries );

        LOG.debug( "Users list fetched for the branch " + branchId );
        return usersCount;
    }


    /**
     * Method to check if branches allowed to be added have succeeded the max limit for a user and
     * account type
     * 
     * @param user
     * @param accountType
     * @return
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public boolean isBranchAdditionAllowed( User user, AccountType accountType ) throws InvalidInputException
    {
        LOG.debug( "Method to check if further branch addition is allowed, called for user : " + user );
        if ( user == null ) {
            throw new InvalidInputException( "User is null in isBranchAdditionAllowed" );
        }
        if ( accountType == null ) {
            throw new InvalidInputException( "Account type is null in isBranchAdditionAllowed" );
        }
        boolean isBranchAdditionAllowed = true;
        /**
         * check is branch addition allowed on the basis of account type
         */
        switch ( accountType ) {
            case INDIVIDUAL:
                LOG.debug( "Checking branch addition for account type INDIVIDUAL" );
                isBranchAdditionAllowed = false;
                break;
            case TEAM:
                LOG.debug( "Checking branch addition for account type TEAM" );
                isBranchAdditionAllowed = false;
                break;
            case COMPANY:
                LOG.debug( "Checking branch addition for account type COMPANY" );
                isBranchAdditionAllowed = true;
                break;
            case ENTERPRISE:
                LOG.debug( "Checking branch addition for account type INDIVIDUAL" );
                isBranchAdditionAllowed = true;
                break;
            default:
                throw new InvalidInputException( "Account type is invalid in isBranchAdditionAllowed" );
        }
        /**
         * check is branch addition is allowed on the basis of profile level of the user, it is
         * allowed only for the region and company admin
         */
        if ( isBranchAdditionAllowed ) {
            if ( user.isCompanyAdmin() || user.isRegionAdmin() ) {
                isBranchAdditionAllowed = true;
            } else {
                isBranchAdditionAllowed = false;
            }
        }
        LOG.debug( "Returning from isBranchAdditionAllowed for user : " + user.getUserId() + " isBranchAdditionAllowed is :"
            + isBranchAdditionAllowed );

        return isBranchAdditionAllowed;
    }


    /**
     * Method to check if regions allowed to be added have succeeded the max limit for a user and
     * account type
     * 
     * @param user
     * @param accountType
     * @return
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public boolean isRegionAdditionAllowed( User user, AccountType accountType ) throws InvalidInputException
    {
        LOG.debug( "Method to check if further region addition is allowed called for user : " + user );
        if ( user == null ) {
            throw new InvalidInputException( "User is null in isRegionAdditionAllowed" );
        }
        if ( accountType == null ) {
            throw new InvalidInputException( "Account type is null in isRegionAdditionAllowed" );
        }
        boolean isRegionAdditionAllowed = true;
        /**
         * check is region addition allowed on the basis of account type
         */
        switch ( accountType ) {
            case INDIVIDUAL:
                LOG.debug( "Checking Region addition for account type INDIVIDUAL" );
                isRegionAdditionAllowed = false;
                break;
            case TEAM:
                LOG.debug( "Checking Region addition for account type TEAM" );
                isRegionAdditionAllowed = false;
                break;
            case COMPANY:
                LOG.debug( "Checking Region addition for account type COMPANY" );
                isRegionAdditionAllowed = false;
                break;
            case ENTERPRISE:
                LOG.debug( "Checking Region addition for account type ENTERPRISE" );
                isRegionAdditionAllowed = true;
                break;
            default:
                throw new InvalidInputException( "Account type is invalid in isRegionAdditionAllowed" );
        }
        /**
         * check is region allowed on the basis of profile level of the user, region addition is
         * allowed only if the user is company admin
         */
        if ( isRegionAdditionAllowed ) {
            if ( user.isCompanyAdmin() ) {
                isRegionAdditionAllowed = true;
            } else {
                isRegionAdditionAllowed = false;
            }
        }
        LOG.debug( "Returning from isRegionAdditionAllowed for user : " + user.getUserId() + " isRegionAdditionAllowed is :"
            + isRegionAdditionAllowed );
        return isRegionAdditionAllowed;
    }


    /**
     * Method to add a new branch from UI
     * 
     * @param user
     * @param regionId
     * @param branchName
     * @param branchAddress1
     * @param branchAddress2
     * @return
     * @throws InvalidInputException
     * @throws SolrException
     */
    @Override
    @Transactional ( rollbackFor = { NonFatalException.class, FatalException.class })
    public Branch addNewBranch( User user, long regionId, int isDefaultBySystem, String branchName, String branchAddress1,
        String branchAddress2, String branchCountry, String branchCountryCode, String branchState, String branchCity,
        String branchZipcode ) throws InvalidInputException, SolrException
    {
        if ( user == null ) {
            throw new InvalidInputException( "User is null in addNewBranch" );
        }
        if ( branchName == null || branchName.isEmpty() ) {
            throw new InvalidInputException( "Branch name is null in addNewBranch" );
        }
        /*if ( branchAddress1 == null || branchAddress1.isEmpty() ) {
            throw new InvalidInputException( "Branch address is null in addNewBranch" );
        }*/
        LOG.debug( "Method add new branch called for regionId : " + regionId + " and branchName : " + branchName );
        Region region = null;
        LOG.debug( "Fetching region for branch to be added" );
        /**
         * If region is selected by user, select it from db
         */
        if ( regionId > 0l ) {
            region = regionDao.findById( Region.class, regionId );
        }
        /**
         * else select the default region from db for that company
         */
        else {
            LOG.debug( "Selecting the default region for company" );
            Map<String, Object> queries = new HashMap<String, Object>();
            queries.put( CommonConstants.COMPANY, user.getCompany() );
            queries.put( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.YES );
            List<Region> regions = regionDao.findByKeyValue( Region.class, queries );
            if ( regions != null && !regions.isEmpty() ) {
                region = regions.get( 0 );
            }
        }
        if ( region == null ) {
            throw new InvalidInputException( "No region is present in db for the company while adding branch" );
        }

        Branch branch = addBranch( user, region, branchName, isDefaultBySystem );
        branch.setAddress1( branchAddress1 );
        branch.setAddress2( branchAddress2 );
        branch.setCountry( branchCountry );
        branch.setState( branchState );
        branch.setCity( branchCity );
        branch.setZipcode( branchZipcode );
        branch.setCountryCode( branchCountryCode );
        branch.setBranchName( branchName );

        if ( ( branchAddress1 != null && !branchAddress1.isEmpty() )
            || ( branchAddress2 != null && !branchAddress2.isEmpty() ) ) {
            try {
                settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.ADDRESS, true );
            } catch ( NonFatalException nonFatalException ) {
                LOG.error(
                    "NonFatalException while updating profile address details. Reason :" + nonFatalException.getMessage(),
                    nonFatalException );
            }
            userManagementService.updateBranch( branch );
        }

        LOG.debug( "Updating branch table with profile name" );
        branchDao.update( branch );

        LOG.debug( "Adding new branch into mongo" );
        try {
            insertBranchSettings( branch );
        } catch ( NonFatalException | FatalException e ) {
            LOG.error( "NonfatalException caught in addNewBranch(). Nested exception is ", e );
            List<Long> branchIds = new ArrayList<>();
            branchIds.add( branch.getBranchId() );
            organizationUnitSettingsDao.removeOganizationUnitSettings( branchIds,
                BRANCH_SETTINGS_COLLECTION );
            throw e;
        }

        LOG.debug( "Adding newly added branch to solr" );
        try {
            solrSearchService.addOrUpdateBranchToSolr( branch );
        } catch ( SolrException | FatalException e ) {
            LOG.error( "NonfatalEXception caught in addNewBranch(). Nested exception is ", e );
            List<Long> branchIds = new ArrayList<>();
            branchIds.add( branch.getBranchId() );
            organizationUnitSettingsDao.removeOganizationUnitSettings( branchIds,
                BRANCH_SETTINGS_COLLECTION );
            solrSearchService.removeBranchesFromSolr( branchIds );
            throw e;
        }

        LOG.debug( "Successfully completed method add new branch for regionId : " + region.getRegionId() + " and branchName : "
            + branchName );
        return branch;

    }


    @Override
    public void removeOrganizationUnitSettings( List<Long> idsToRemove, String collectionName )
    {
        LOG.debug( "Method to remove OrganizationUnitSettings removeOrganizationUnitSettings() started." );
        organizationUnitSettingsDao.removeOganizationUnitSettings( idsToRemove,
            BRANCH_SETTINGS_COLLECTION );
        LOG.debug( "Method to remove OrganizationUnitSettings removeOrganizationUnitSettings() finished." );
    }


    @Override
    public void removeOrganizationUnitSettings( Long idToRemove, String collectionName )
    {
        LOG.debug( "Method to remove OrganizationUnitSettings removeOrganizationUnitSettings() started." );
        List<Long> ids = new ArrayList<>();
        ids.add( idToRemove );
        organizationUnitSettingsDao.removeOganizationUnitSettings( ids,
            BRANCH_SETTINGS_COLLECTION );
        LOG.debug( "Method to remove OrganizationUnitSettings removeOrganizationUnitSettings() finished." );
    }


    /**
     * Method to generate profile name and profile url for a branch and also set them in
     * organization unit settings
     * 
     * @param branch
     * @param organizationSettings
     * @throws InvalidInputException
     */
    void generateAndSetBranchProfileNameAndUrl( Branch branch, OrganizationUnitSettings organizationSettings )
        throws InvalidInputException
    {
        LOG.debug( "Method to generate branch profile name called for branch: " + branch );
        String branchProfileName = null;
        if ( branch == null ) {
            throw new InvalidInputException( "Branch is null in generateAndSetRegionProfileNameAndUrl" );
        }
        String branchName = branch.getBranch();
        if ( branchName == null || branchName.isEmpty() ) {
            throw new InvalidInputException( "Branch name is null or empty in generateAndSetRegionProfileNameAndUrl" );
        }

        // branchProfileName = branchName.trim().replaceAll(" ", "-").toLowerCase();
        branchProfileName = utils.prepareProfileName( branchName );

        OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
            branch.getCompany().getCompanyId(), COMPANY_SETTINGS_COLLECTION );
        if ( companySettings != null ) {
            String companyProfileName = companySettings.getProfileName();
            String branchProfileUrl = utils.generateBranchProfileUrl( companyProfileName, branchProfileName );

            LOG.debug( "Checking if profileName:" + branchProfileName + " is already taken by a branch in the company :"
                + branch.getCompany() );
            /**
             * Uniqueness of profile name is checked by url since combination of company profile
             * name and branch profile name is unique
             */
            OrganizationUnitSettings branchSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileUrl(
                branchProfileUrl, BRANCH_SETTINGS_COLLECTION );
            /**
             * if there exists a branch with the profile name formed, append branch iden to get the
             * unique profile name and also regenerate url with new profile name
             */
            if ( branchSettings != null ) {
                LOG.debug( "Profile name was not unique hence appending id to it to get a unique one" );
                // branchProfileName = branchProfileName + "-" + branch.getBranchId();
                branchProfileName = utils.appendIdenToProfileName( branchProfileName, branch.getBranchId() );
                branchProfileUrl = utils.generateBranchProfileUrl( companyProfileName, branchProfileName );
            }
            organizationSettings.setProfileName( branchProfileName );
            organizationSettings.setProfileUrl( branchProfileUrl );
            /**
             * set profile name in branch for setting value in sql tables
             */
            branch.setProfileName( branchProfileName );
        } else {
            LOG.warn( "Company settings not found in generateAndSetRegionProfileNameAndUrl" );
        }

        LOG.debug( "Method to generate and set branch profile name and url excecuted successfully" );
    }


    /**
     * Method to form ContactDetailsSettings object from branch
     * 
     * @param branch
     * @return
     */
    ContactDetailsSettings getContactDetailsSettingsFromBranch( Branch branch )
    {
        LOG.debug( "Method getContactDetailsSettingsFromBranch called for branch :" + branch );
        ContactDetailsSettings contactSettings = new ContactDetailsSettings();
        contactSettings.setName( branch.getBranch() );
        contactSettings.setAddress1( branch.getAddress1() );

        if ( branch.getAddress2() != null && !branch.getAddress2().isEmpty() ) {
            contactSettings.setAddress( branch.getAddress1() + ", " + branch.getAddress2() );
            contactSettings.setAddress2( branch.getAddress2() );
        } else {
            contactSettings.setAddress( branch.getAddress1() );
        }

        if ( branch.getCountry() != null && !branch.getCountry().isEmpty() ) {
            contactSettings.setCountry( branch.getCountry() );
        }

        if ( branch.getCountryCode() != null && !branch.getCountryCode().isEmpty() ) {
            contactSettings.setCountryCode( branch.getCountryCode() );
        }

        if ( branch.getState() != null && !branch.getState().isEmpty() ) {
            contactSettings.setState( branch.getState() );
        }

        if ( branch.getCity() != null && !branch.getCity().isEmpty() ) {
            contactSettings.setCity( branch.getCity() );
        }

        if ( branch.getZipcode() != null && !branch.getZipcode().isEmpty() ) {
            contactSettings.setZipcode( branch.getZipcode() );
        }

        LOG.debug( "Method getContactDetailsSettingsFromBranch finished.Returning :" + contactSettings );
        return contactSettings;
    }


    /**
     * Method to form ContactDetailsSettings object from region
     * 
     * @param region
     * @return
     */
    ContactDetailsSettings getContactDetailsSettingsFromRegion( Region region )
    {
        LOG.debug( "Method getContactDetailsSettingsFromRegion called for branch :" + region );
        ContactDetailsSettings contactSettings = new ContactDetailsSettings();
        contactSettings.setName( region.getRegion() );
        contactSettings.setAddress1( region.getAddress1() );

        if ( region.getAddress2() != null && !region.getAddress2().isEmpty() ) {
            contactSettings.setAddress( region.getAddress1() + ", " + region.getAddress2() );
            contactSettings.setAddress2( region.getAddress2() );
        } else {
            contactSettings.setAddress( region.getAddress1() );
        }

        if ( region.getCountry() != null && !region.getCountry().isEmpty() ) {
            contactSettings.setCountry( region.getCountry() );
        }

        if ( region.getCountryCode() != null && !region.getCountryCode().isEmpty() ) {
            contactSettings.setCountryCode( region.getCountryCode() );
        }

        if ( region.getState() != null && !region.getState().isEmpty() ) {
            contactSettings.setState( region.getState() );
        }

        if ( region.getCity() != null && !region.getCity().isEmpty() ) {
            contactSettings.setCity( region.getCity() );
        }

        if ( region.getZipcode() != null && !region.getZipcode().isEmpty() ) {
            contactSettings.setZipcode( region.getZipcode() );
        }

        LOG.debug( "Method getContactDetailsSettingsFromRegion finished.Returning :" + contactSettings );
        return contactSettings;
    }


    /**
     * Method to add a new region
     * 
     * @param user
     * @param regionName
     * @param address1
     * @param address2
     * @return
     * @throws InvalidInputException
     * @throws SolrException
     */
    @Override
    @Transactional ( rollbackFor = { NonFatalException.class, FatalException.class })
    public Region addNewRegion( User user, String regionName, int isDefaultBySystem, String address1, String address2,
        String country, String countryCode, String state, String city, String zipcode )
        throws InvalidInputException, SolrException
    {
        if ( user == null ) {
            throw new InvalidInputException( "User is null in addNewRegion" );
        }
        if ( regionName == null || regionName.isEmpty() ) {
            throw new InvalidInputException( "Region name is null in addNewRegion" );
        }
        LOG.debug( "Method add new region called for regionName : " + regionName );

        Region region = addRegion( user, isDefaultBySystem, regionName );
        region.setAddress1( address1 );
        region.setAddress2( address2 );
        region.setCountry( country );
        region.setCountryCode( countryCode );
        region.setState( state );
        region.setCity( city );
        region.setZipcode( zipcode );

        if ( ( address1 != null && !address1.isEmpty() ) || ( address2 != null && !address2.isEmpty() ) ) {
            try {
                settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.ADDRESS, true );
            } catch ( NonFatalException nonFatalException ) {
                LOG.error(
                    "NonFatalException while updating profile address details. Reason :" + nonFatalException.getMessage(),
                    nonFatalException );
            }
            userManagementService.updateRegion( region );
        }

        LOG.debug( "Calling method to insert region settings" );
        try {
            insertRegionSettings( region );
        } catch ( NonFatalException | FatalException e ) {
            List<Long> regionIds = new ArrayList<>();
            regionIds.add( region.getRegionId() );
            organizationUnitSettingsDao.removeOganizationUnitSettings( regionIds,
                REGION_SETTINGS_COLLECTION );
            throw e;
        }
        regionDao.update( region );

        LOG.debug( "Updating solr with newly inserted region" );
        try {
            solrSearchService.addOrUpdateRegionToSolr( region );
        } catch ( SolrException | FatalException e ) {
            LOG.error( "SolrException caught in addNewRegion(). Nested exception is ", e );
            List<Long> regionIds = new ArrayList<>();
            regionIds.add( region.getRegionId() );
            organizationUnitSettingsDao.removeOganizationUnitSettings( regionIds,
                REGION_SETTINGS_COLLECTION );
            solrSearchService.removeRegionsFromSolr( regionIds );
            throw e;
        }

        LOG.debug( "Successfully completed method add new region for regionName : " + regionName );
        return region;
    }


    /**
     * Method to generate profile name and profile url for a region and also set them in
     * organization unit settings
     * 
     * @param region
     * @return
     * @throws InvalidInputException
     */
    void generateAndSetRegionProfileNameAndUrl( Region region, OrganizationUnitSettings organizationSettings )
        throws InvalidInputException
    {
        LOG.debug( "Method generateAndSetRegionProfileNameAndUrl called for region: " + region );
        String regionProfileName = null;
        if ( region == null ) {
            throw new InvalidInputException( "Region is null in generateAndSetRegionProfileNameAndUrl" );
        }
        String regionName = region.getRegion();
        if ( regionName == null || regionName.isEmpty() ) {
            throw new InvalidInputException( "Region name is null or empty in generateAndSetRegionProfileNameAndUrl" );
        }

        // regionProfileName = regionName.trim().replaceAll(" ", "-").toLowerCase();
        regionProfileName = utils.prepareProfileName( regionName );
        LOG.debug( "Checking if profileName:" + regionProfileName + " is already taken by a region in the company :"
            + region.getCompany() );

        OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
            region.getCompany().getCompanyId(), COMPANY_SETTINGS_COLLECTION );
        if ( companySettings != null ) {
            String companyProfileName = companySettings.getProfileName();
            String regionProfileUrl = utils.generateRegionProfileUrl( companyProfileName, regionProfileName );

            /**
             * Uniqueness of profile name is checked by url since combination of company profile
             * name and region profile name is unique
             */
            OrganizationUnitSettings regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileUrl(
                regionProfileUrl, REGION_SETTINGS_COLLECTION );
            /**
             * if there exists a region with the profile name formed, append region iden to get the
             * unique profile name and also regenerate url with new profile name
             */
            if ( regionSettings != null ) {
                LOG.debug( "Profile name was not unique hence appending id to it to get a unique one" );
                // regionProfileName = regionProfileName + "-" + region.getRegionId();
                regionProfileName = utils.appendIdenToProfileName( regionProfileName, region.getRegionId() );
                regionProfileUrl = utils.generateRegionProfileUrl( companyProfileName, regionProfileName );
            }
            organizationSettings.setProfileName( regionProfileName );
            organizationSettings.setProfileUrl( regionProfileUrl );

            /**
             * Set the profile name in region object to update in sql later
             */
            region.setProfileName( regionProfileName );
        } else {
            LOG.warn( "Company settings not found in generateAndSetRegionProfileNameAndUrl" );
        }

        LOG.debug( "Method generateAndSetRegionProfileNameAndUrl excecuted successfully" );
    }


    /**
     * Method to check whether a user can view region based on his profiles
     * 
     * @param userProfiles
     * @return
     */
    boolean isRegionViewAllowed( List<UserProfile> userProfiles )
    {
        // TODO implement this
        return true;
    }


    /**
     * Method to check whether a user can view branch based on his profiles
     * 
     * @param userProfiles
     * @return
     */
    boolean isBranchViewAllowed( List<UserProfile> userProfiles )
    {
        // TODO implement this
        return true;
    }


    /**
     * Method to check whether a user has privileges to build hierarchy
     */
    @Override
    public boolean canBuildHierarchy( User user, AccountType accountType )
    {
        // TODO implement this
        return true;
    }


    /**
     * Method to check whether a user has privileges to edit company information
     */
    @Override
    public boolean canEditCompany( User user, AccountType accountType )
    {
        // TODO Auto-generated method stub
        return true;
    }


    /**
     * Method to insert region settings into mongo
     * 
     * @param region
     * @throws InvalidInputException
     */
    public void insertRegionSettings( Region region ) throws InvalidInputException
    {
        LOG.debug( "Method for inserting region settings called for region : " + region );
        OrganizationUnitSettings organizationSettings = new OrganizationUnitSettings();
        organizationSettings.setIden( region.getRegionId() );
        organizationSettings.setCompanyId(region.getCompany().getCompanyId());
        // set is default flag
        boolean isDefaultFlag = false;
        if ( region.getIsDefaultBySystem() == CommonConstants.YES ) {
            isDefaultFlag = true;
        }
        organizationSettings.setDefaultBySystem( isDefaultFlag );
        // set the seo content mdified to true, so that batch pick this record up
        organizationSettings.setSeoContentModified( true );
        organizationSettings.setCreatedBy( region.getCreatedBy() );
        organizationSettings.setCreatedOn( System.currentTimeMillis() );
        organizationSettings.setModifiedBy( region.getModifiedBy() );
        organizationSettings.setModifiedOn( System.currentTimeMillis() );
        organizationSettings.setVertical( region.getCompany().getVerticalsMaster().getVerticalName() );

        // Calling method to generate and set region profile name and url
        generateAndSetRegionProfileNameAndUrl( region, organizationSettings );

        ContactDetailsSettings contactSettings = getContactDetailsSettingsFromRegion( region );
        organizationSettings.setContact_details( contactSettings );
        //updating geoLoc
        organizationSettings.setGeoLocation(searchEngineManagementServices.getGeoLocForSettings(contactSettings));
        organizationSettings.setLockSettings( new LockSettings() );

        if ( organizationSettings.getSurvey_settings() == null ) {
            SurveySettings surveySettings = new SurveySettings();
            surveySettings.setShow_survey_above_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
            surveySettings.setAutoPostEnabled( true );
            surveySettings.setAuto_post_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
            organizationSettings.setSurvey_settings( surveySettings );
        }

        // set default profile stages.
        organizationSettings.setProfileStages( profileCompletionList.getDefaultProfileCompletionList( false ) );

        // default digest flag
        if ( !isDefaultFlag ) {
            organizationSettings.setSendMonthlyDigestMail( true );
        }
        
        organizationSettings.setShowSummitPopup( false );

        organizationUnitSettingsDao.insertOrganizationUnitSettings( organizationSettings,
            REGION_SETTINGS_COLLECTION );
        LOG.debug( "Method for inserting region settings finished" );
    }


    /**
     * Method to insert branch settings into mongo
     * 
     * @param branch
     * @throws InvalidInputException
     */
    public void insertBranchSettings( Branch branch ) throws InvalidInputException
    {
        LOG.debug( "Method to insert branch settings called for branch : " + branch );
        OrganizationUnitSettings organizationSettings = new OrganizationUnitSettings();
        organizationSettings.setIden( branch.getBranchId() );
        organizationSettings.setCompanyId(branch.getCompany().getCompanyId());
        // set is default flag
        boolean isDefaultFlag = false;
        if ( branch.getIsDefaultBySystem() == CommonConstants.YES ) {
            isDefaultFlag = true;
        }
        // set the seo content mdified to true, so that batch pick this record up
        organizationSettings.setSeoContentModified( true );
        organizationSettings.setDefaultBySystem( isDefaultFlag );
        organizationSettings.setCreatedBy( branch.getCreatedBy() );
        organizationSettings.setCreatedOn( System.currentTimeMillis() );
        organizationSettings.setModifiedBy( branch.getModifiedBy() );
        organizationSettings.setModifiedOn( System.currentTimeMillis() );
        organizationSettings.setVertical( branch.getCompany().getVerticalsMaster().getVerticalName() );

        // Calling method to generate and set profile name and profile url
        generateAndSetBranchProfileNameAndUrl( branch, organizationSettings );

        if ( organizationSettings.getSurvey_settings() == null ) {
            SurveySettings surveySettings = new SurveySettings();
            surveySettings.setShow_survey_above_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
            surveySettings.setAutoPostEnabled( true );
            surveySettings.setAuto_post_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
            organizationSettings.setSurvey_settings( surveySettings );
        }

        ContactDetailsSettings contactSettings = getContactDetailsSettingsFromBranch( branch );
        organizationSettings.setContact_details( contactSettings );
        //setting geoLocation
        organizationSettings.setGeoLocation(searchEngineManagementServices.getGeoLocForSettings(contactSettings));
        organizationSettings.setLockSettings( new LockSettings() );

        // set default profile stages.
        organizationSettings.setProfileStages( profileCompletionList.getDefaultProfileCompletionList( false ) );

        // default digest flag
        if ( !isDefaultFlag ) {
            organizationSettings.setSendMonthlyDigestMail( true );
        }
        
        organizationSettings.setShowSummitPopup( false );

        organizationUnitSettingsDao.insertOrganizationUnitSettings( organizationSettings,
            BRANCH_SETTINGS_COLLECTION );
        LOG.debug( "Method to insert branch settings finished for branch : " + branch );
    }


    /**
     * Method to get the list of region ids for a user and profile master id specified
     */
    @Override
    @Transactional
    public Set<Long> getRegionIdsForUser( User user, int profileMasterId )
        throws InvalidInputException, NoRecordsFetchedException
    {
        if ( profileMasterId <= 0l ) {
            throw new InvalidInputException( "Profile master id is not specified in getRegionIdsForUser" );
        }
        if ( user == null ) {
            throw new InvalidInputException( "User is null in getRegionIdsForUser" );
        }
        LOG.debug( "Method getRegionIdsForUser called for profileMasterId: " + profileMasterId + " and user:" + user );
        Set<Long> regionIds = null;
        List<UserProfile> userProfiles = user.getUserProfiles();
        if ( userProfiles == null || userProfiles.isEmpty() ) {
            userProfiles = userManagementService.getAllUserProfilesForUser( user );
        }
        if ( userProfiles == null || userProfiles.isEmpty() ) {
            throw new NoRecordsFetchedException( "No user profile found for the user in getRegionIdsForUser" );
        }
        regionIds = new HashSet<Long>();
        for ( UserProfile userProfile : userProfiles ) {
            if ( userProfile.getProfilesMaster().getProfileId() == profileMasterId ) {
                regionIds.add( userProfile.getRegionId() );
            }
        }
        LOG.debug( "Method getRegionIdsForUser executed successfully. Returning: " + regionIds );
        return regionIds;
    }


    /**
     * Method to get the list of branch ids for a user and profile master id specified
     */
    public Set<Long> getBranchIdsForUser( User user, int profileMasterId )
        throws InvalidInputException, NoRecordsFetchedException
    {
        if ( profileMasterId <= 0l ) {
            throw new InvalidInputException( "Profile master id is not specified in getBranchIdsForUser" );
        }
        if ( user == null ) {
            throw new InvalidInputException( "User is null in getBranchIdsForUser" );
        }
        LOG.debug( "Method getBranchIdsForUser called for profileMasterId: " + profileMasterId + " and user:" + user );
        Set<Long> branchIds = null;
        List<UserProfile> userProfiles = user.getUserProfiles();
        if ( userProfiles == null || userProfiles.isEmpty() ) {
            userProfiles = userManagementService.getAllUserProfilesForUser( user );
        }
        if ( userProfiles == null || userProfiles.isEmpty() ) {
            throw new NoRecordsFetchedException( "No user profile found for the user in getBranchIdsForUser" );
        }
        branchIds = new HashSet<Long>();
        for ( UserProfile userProfile : userProfiles ) {
            if ( userProfile.getProfilesMaster().getProfileId() == profileMasterId ) {
                branchIds.add( userProfile.getBranchId() );
            }
        }
        return branchIds;
    }


    /**
     * Method to get all branches under the regions specified
     */
    @Override
    @Transactional
    public List<Branch> getBranchesByRegionIds( Set<Long> regionIds ) throws InvalidInputException
    {
        LOG.debug( "Method getBranchesByRegionIds called for regionIds:" + regionIds );
        List<Branch> branches = null;
        if ( regionIds != null && !regionIds.isEmpty() ) {
            branches = new ArrayList<Branch>();
            for ( long regionId : regionIds ) {
                List<Branch> tempBranches = getBranchesByRegionId( regionId );
                if ( tempBranches != null && !tempBranches.isEmpty() ) {
                    branches.addAll( tempBranches );
                }
            }
        }
        LOG.debug( "Method getBranchesByRegionIds executed successfully" );
        return branches;
    }


    /**
     * Method to get the list of all the company ids
     */
    @Override
    @Transactional
    public Set<Company> getAllCompanies()
    {
        LOG.debug( "Method to get list of all companies, getAllCompanies() started" );
        Set<Company> companies = new HashSet<Company>( companyDao.findAllActive( Company.class ) );
        LOG.debug( "Method to get list of all companies, getAllCompanies() finished" );
        return companies;
    }


    /**
     * Method to get a list of all the regions
     * 
     * @return
     */
    @Override
    @Transactional
    public List<Region> getAllRegions()
    {
        LOG.debug( "Method getAllRegions started" );
        List<Region> regions = regionDao.findAll( Region.class );
        LOG.debug( "Method getAllRegions finished" );
        return regions;
    }


    /**
     * Method to get a list of all the branches
     * 
     * @return
     */
    @Override
    @Transactional
    public List<Branch> getAllBranches()
    {
        LOG.debug( "Method getAllBranches started" );
        List<Branch> branches = branchDao.findAll( Branch.class );
        LOG.debug( "Method getAllBranches finished" );
        return branches;
    }


    /**
     * Method to get a list of all the users
     * 
     * @return
     */
    @Override
    @Transactional
    public List<User> getAllUsers()
    {
        LOG.debug( "Method getAllUsers started" );
        List<User> users = userDao.findAll( User.class );
        LOG.debug( "Method getAllUsers finished" );
        return users;
    }


    @Override
    public Map<Long, BranchFromSearch> fetchBranchesMapByCompany( long companyId )
        throws InvalidInputException, SolrException, MalformedURLException
    {

        long branchCount = solrSearchService.fetchBranchCountByCompany( companyId );
        String branchesResult = solrSearchService.fetchBranchesByCompany( companyId, (int) branchCount );

        // convert branches to map
        Type searchedBranchesList = new TypeToken<List<BranchFromSearch>>() {}.getType();
        List<BranchFromSearch> branchList = new Gson().fromJson( branchesResult, searchedBranchesList );

        Map<Long, BranchFromSearch> branches = new HashMap<Long, BranchFromSearch>();
        for ( BranchFromSearch branch : branchList ) {
            branches.put( branch.getBranchId(), branch );
        }

        // Fetch all the branches' settings from Mongo
        List<OrganizationUnitSettings> branchSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsForMultipleIds(
            branches.keySet(), BRANCH_SETTINGS_COLLECTION );
        for ( OrganizationUnitSettings setting : branchSettings ) {
            if ( branches.containsKey( setting.getIden() ) ) {
                BranchFromSearch branchFromSearch = branches.get( setting.getIden() );
                if ( setting.getContact_details() != null ) {
                    branchFromSearch.setCity( setting.getContact_details().getCity() );
                    branchFromSearch.setState( setting.getContact_details().getState() );
                    branchFromSearch.setZipcode( setting.getContact_details().getZipcode() );
                    branchFromSearch.setCountry( setting.getContact_details().getCountry() );
                    branchFromSearch.setCountryCode( setting.getContact_details().getCountryCode() );
                }
            }
        }
        return branches;
    }


    @Override
    public String fetchBranchesByCompany( long companyId ) throws InvalidInputException, SolrException, MalformedURLException
    {

        long branchCount = solrSearchService.fetchBranchCountByCompany( companyId );
        String branchesResult = "";
        if ( branchCount > 0 )
            branchesResult = solrSearchService.fetchBranchesByCompany( companyId, (int) branchCount );
        return branchesResult;
    }


    @Override
    public Map<Long, RegionFromSearch> fetchRegionsMapByCompany( long companyId )
        throws InvalidInputException, SolrException, MalformedURLException
    {
        long regionsCount = solrSearchService.fetchRegionCountByCompany( companyId );
        String regionsResult = solrSearchService.fetchRegionsByCompany( companyId, (int) regionsCount );

        // convert regions to map
        Type searchedRegionsList = new TypeToken<List<RegionFromSearch>>() {}.getType();
        List<RegionFromSearch> regionsList = new Gson().fromJson( regionsResult, searchedRegionsList );

        Map<Long, RegionFromSearch> regions = new HashMap<Long, RegionFromSearch>();
        for ( RegionFromSearch region : regionsList ) {
            regions.put( region.getRegionId(), region );
        }
        return regions;
    }


    @Override
    public String fetchRegionsByCompany( long companyId ) throws InvalidInputException, SolrException, MalformedURLException
    {
        LOG.debug( "Method called to fetch the regions by company for company id : " + companyId );
        long regionsCount = solrSearchService.fetchRegionCountByCompany( companyId );
        String regionsResult = "";
        if ( regionsCount > 0 )
            regionsResult = solrSearchService.fetchRegionsByCompany( companyId, (int) regionsCount );
        return regionsResult;
    }


    /**
     * Method to get the list of branches from solr which are directly assigned to the company
     * 
     * @throws NoRecordsFetchedException
     * @throws SolrException
     */
    @Override
    @Transactional
    public List<BranchFromSearch> getBranchesUnderCompanyFromSolr( Company company, int start )
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        if ( company == null ) {
            throw new InvalidInputException( "company is null in getBranchesUnderCompanyFromSolr" );
        }
        LOG.debug( "Method getBranchesUnderCompanyFromSolr called for company:" + company + " and start:" + start );
        List<BranchFromSearch> branches = null;
        Region defaultRegion = getDefaultRegionForCompany( company );
        int branchCount = (int) solrSearchService.getBranchCountByRegion( defaultRegion.getRegionId() );
        String branchesJson = solrSearchService.searchBranchesByRegion( defaultRegion.getRegionId(), start, branchCount );
        LOG.debug( "branchesJson obtained from solr is:" + branchesJson );

        Type searchedBranchesList = new TypeToken<List<BranchFromSearch>>() {}.getType();
        branches = new Gson().fromJson( branchesJson, searchedBranchesList );

        LOG.debug( "Method getBranchesUnderCompanyFromSolr executed successfully" );
        return branches;
    }


    /**
     * Method to get the list of users from solr which are directly assigned to the company
     */
    @Override
    @Transactional
    public List<UserFromSearch> getUsersUnderCompanyFromSolr( Company company, int start )
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        if ( company == null ) {
            throw new InvalidInputException( "company is null in getUsersUnderCompanyFromSolr" );
        }
        LOG.debug( "Method getUsersUnderCompanyFromSolr called for company:{} and start:{}",company, start);
        Region defaultRegion = getDefaultRegionForCompany( company );
        Branch defaultBranch = getDefaultBranchForRegion( defaultRegion.getRegionId() );
        int usersCount = (int) solrSearchService.getUsersCountByIden( defaultBranch.getBranchId(),
            CommonConstants.BRANCHES_SOLR, false );
        Collection<UserFromSearch> usersResult = solrSearchService.searchUsersByIden( defaultBranch.getBranchId(),
            CommonConstants.BRANCHES_SOLR, false, start, usersCount );
        List<UserFromSearch> users = new ArrayList<>(usersResult);
        LOG.debug( "Solr result returned for users of company is: {}",users );
        LOG.debug( "Method getUsersUnderRegionFromSolr executed successfully" );
        return users;
    }


    /**
     * Method to get all users under the company from solr
     * 
     * @param company
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     * @throws SolrException
     */
    @Override
    @Transactional
    public String getAllUsersUnderCompanyFromSolr( Company company )
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        if ( company == null ) {
            throw new InvalidInputException( "company is null in getUsersUnderCompanyFromSolr" );
        }
        LOG.debug( "Method getAllUsersUnderCompanyFromSolr called for company:" + company );
        int usersCount = (int) solrSearchService.getUsersCountByIden( company.getCompanyId(), CommonConstants.COMPANY_ID_SOLR,
            false );
        Collection<UserFromSearch> usersResult = solrSearchService.searchUsersByIden( company.getCompanyId(),
            CommonConstants.COMPANY_ID_SOLR, false, 0, usersCount );
        String usersJson = new Gson().toJson( usersResult );
        LOG.debug( "Solr result returned for users of company is:" + usersJson );
        return usersJson;
    }


    @Override
    @Transactional
    public List<UserFromSearch> getUsersUnderRegionFromSolr( Set<Long> regionIds, int start, int rows )
        throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        if ( regionIds == null || regionIds.isEmpty() ) {
            throw new InvalidInputException( "region ids are null in getUsersUnderRegionFromSolr" );
        }
        LOG.debug(
            "Method getUsersUnderRegionFromSolr called for regionIds:" + regionIds + " and start:" + start + " rows:" + rows );
        List<UserFromSearch> users = null;
        Set<Long> branchIds = new HashSet<Long>();
        for ( long regionId : regionIds ) {
            Branch branch = getDefaultBranchForRegion( regionId );
            branchIds.add( branch.getBranchId() );
        }
        int userCount = rows;
        if ( rows == -1 ) {
            userCount = (int) solrSearchService.getUsersCountByBranches( branchIds );
        }
        String usersJson = solrSearchService.searchUsersByBranches( branchIds, start, userCount );
        LOG.debug( "Solr result returned for users of regions is:" + usersJson );
        /**
         * convert users to Object
         */
        Type searchedUsersList = new TypeToken<List<UserFromSearch>>() {}.getType();
        users = new Gson().fromJson( usersJson, searchedUsersList );

        LOG.debug( "Method getUsersUnderRegionFromSolr executed successfully" );
        return users;
    }


    /**
     * Method to update a region and assign user if specified
     */
    @Override
    @Transactional
    public Map<String, Object> updateRegion( User user, long regionId, String regionName, String address1, String address2,
        String country, String countryCode, String state, String city, String zipcode, long selectedUserId,
        String[] emailIdsArray, boolean isAdmin, boolean holdSendingMail, boolean isAddedByRealtechOrSSAdmin )
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, List<User>> userMap = new HashMap<String, List<User>>();
        if ( user == null ) {
            throw new InvalidInputException( "User is null in update region" );
        }
        if ( regionName == null || regionName.isEmpty() ) {
            throw new InvalidInputException( "Region name is null in update region" );
        }
        if ( regionId <= 0l ) {
            throw new InvalidInputException( "Region id is invalid in update region" );
        }
        LOG.debug(
            "Method update region called for regionId:" + regionId + " regionName : " + regionName + " ,address1:" + address1 );
        Region region = regionDao.findById( Region.class, regionId );
        if ( region == null ) {
            throw new NoRecordsFetchedException( "No region present for the required id in database while updating region" );
        }
        region.setRegion( regionName );
        region.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        region.setModifiedBy( String.valueOf( user.getUserId() ) );
        region.setAddress1( address1 );
        region.setAddress2( address2 );
        region.setCountry( country );
        region.setCountryCode( countryCode );
        region.setState( state );
        region.setCity( city );
        region.setZipcode( zipcode );
        regionDao.update( region );

        LOG.debug( "Updating region in mongo" );
        ContactDetailsSettings contactDetailsSettings = getContactDetailsSettingsFromRegion( region );
        organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByCriteria(
            KEY_CONTACT_DETAIL_SETTINGS, contactDetailsSettings,
            KEY_IDENTIFIER, regionId,
            REGION_SETTINGS_COLLECTION );

        LOG.debug( "Updating region in solr" );
        solrSearchService.addOrUpdateRegionToSolr( region );

        /**
         * If userId or email is provided, call the service for adding and assigning user to the
         * updated region
         */
        if ( selectedUserId > 0l ) {
            LOG.debug( "Fetching user for selectedUserId " + selectedUserId + "to assign to the region" );
            User assigneeUser = userDao.findById( User.class, selectedUserId );
            if ( assigneeUser == null ) {
                throw new NoRecordsFetchedException( "No user found in db for selectedUserId:" + selectedUserId );
            }
            try {
                assignRegionToUser( user, region.getRegionId(), assigneeUser, isAdmin );
            } catch ( InvalidInputException | NoRecordsFetchedException | SolrException e ) {
                LOG.error( "Exception while assigning region to a user. Reason:" + e.getMessage(), e );
                throw new UserAssignmentException( e.getMessage(), e );
            }
        } else if ( emailIdsArray != null && emailIdsArray.length > 0 ) {
            LOG.debug( "Fetching users list to assign to the region" );
            userMap = getUsersFromEmailIdsAndInvite( emailIdsArray, user, holdSendingMail, true, isAddedByRealtechOrSSAdmin );
            List<User> assigneeUsers = userMap.get( CommonConstants.VALID_USERS_LIST );

            if ( assigneeUsers != null && !assigneeUsers.isEmpty() ) {
                for ( User assigneeUser : assigneeUsers ) {
                    try {
                        assignRegionToUser( user, region.getRegionId(), assigneeUser, isAdmin );
                    } catch ( InvalidInputException | NoRecordsFetchedException | SolrException e ) {
                        LOG.error( "Exception while assigning region to a user. Reason:" + e.getMessage(), e );
                        throw new UserAssignmentException( e.getMessage(), e );
                    }
                }
            }
        }
        LOG.debug( "Method to update region completed successfully" );
        map.put( CommonConstants.REGION_OBJECT, region );
        if ( userMap != null ) {
            map.put( CommonConstants.INVALID_USERS_LIST, userMap.get( CommonConstants.INVALID_USERS_LIST ) );
        }
        return map;
    }


    /**
     * Method to update a branch
     * 
     * @throws SolrException
     */
    @Override
    @Transactional
    public void updateBranch( long branchId, long regionId, String branchName, String branchAddress1, String branchAddress2,
        User user ) throws InvalidInputException, SolrException
    {
        if ( user == null ) {
            throw new InvalidInputException( "User is null in update branch" );
        }
        if ( branchName == null || branchName.isEmpty() ) {
            throw new InvalidInputException( "Branch name is null in update branch" );
        }
        if ( branchAddress1 == null || branchAddress1.isEmpty() ) {
            throw new InvalidInputException( "Branch address is null in update branch" );
        }
        if ( branchId <= 0l ) {
            throw new InvalidInputException( "Branch id is invalid in update branch" );
        }

        LOG.debug( "Method update branch called for branchId:" + branchId + " ,regionId:" + regionId + " branchName : "
            + branchName + " ,branchAddress:" + branchAddress1 );
        Branch branch = branchDao.findById( Branch.class, branchId );
        if ( branch == null ) {
            throw new InvalidInputException( "No branch present for the required id in database while updating branch" );
        }
        LOG.debug( "Checking if the region of branch is changed" );

        /**
         * In case of branch attached to default region, regionId is 0 hence perform update only
         * when the regionId is not the default one
         */
        if ( regionId > 0l && regionId != branch.getRegion().getRegionId() ) {
            Region region = regionDao.findById( Region.class, regionId );
            if ( region == null ) {
                throw new InvalidInputException( "No region present for the required id in database while updating branch" );
            }
            branch.setRegion( region );
        }
        branch.setBranch( branchName );
        branch.setAddress1( branchAddress1 );
        branch.setAddress2( branchAddress2 );
        branch.setModifiedBy( String.valueOf( user.getUserId() ) );
        branch.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        branchDao.update( branch );

        LOG.debug( "Update branch in mongo" );
        ContactDetailsSettings contactDetailsSettings = getContactDetailsSettingsFromBranch( branch );
        organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByCriteria(
            KEY_CONTACT_DETAIL_SETTINGS, contactDetailsSettings,
            KEY_IDENTIFIER, branchId,
            BRANCH_SETTINGS_COLLECTION );

        LOG.debug( "Updating branch in solr" );
        solrSearchService.addOrUpdateBranchToSolr( branch );
        LOG.debug( "Method to update branch completed successfully" );
    }


    /**
     * Method to update a branch
     */
    @Override
    @Transactional
    public Map<String, Object> updateBranch( User user, long branchId, long regionId, String branchName, String address1,
        String address2, String country, String countryCode, String state, String city, String zipcode, long selectedUserId,
        String[] emailIdsArray, boolean isAdmin, boolean holdSendingMail, boolean isAddedByRealtechOrSSAdmin )
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, List<User>> userMap = new HashMap<String, List<User>>();
        LOG.debug(
            "Method updateBranch called for branchId:" + branchId + " regionId:" + regionId + " branchName:" + branchName );
        if ( user == null ) {
            throw new InvalidInputException( "User is null in update branch" );
        }
        if ( branchName == null || branchName.isEmpty() ) {
            throw new InvalidInputException( "Branch name is null in update branch" );
        }
        if ( branchId <= 0l ) {
            throw new InvalidInputException( "Branch id is invalid in update branch" );
        }
        if ( address1 == null || address1.isEmpty() ) {
            throw new InvalidInputException( "Branch address is null in update branch" );
        }
        Branch branch = branchDao.findById( Branch.class, branchId );
        if ( branch == null ) {
            throw new NoRecordsFetchedException( "No branch present for the required id in database while updating branch" );
        }
        LOG.debug( "Checking if the region of branch is changed" );

        Region defaultRegion = getDefaultRegionForCompany( user.getCompany() );
        if ( regionId <= 0l ) {
            regionId = defaultRegion.getRegionId();
        }

        /**
         * Perform update only when the regionId is not same as the previous regionId
         */
        if ( regionId != branch.getRegion().getRegionId() ) {
            Region region = null;
            if ( regionId == defaultRegion.getRegionId() ) {
                region = defaultRegion;
            } else {
                region = regionDao.findById( Region.class, regionId );
            }

            if ( region == null ) {
                throw new NoRecordsFetchedException(
                    "No region present for the required id in database while updating branch" );
            } else {
                //Update user profiles here.
                userProfileDao.updateRegionIdForBranch( branchId, region.getRegionId() );

                //update survey details
                surveyDetailsDao.updateRegionIdForAllSurveysOfBranch( branchId, region.getRegionId() );

                //Get and update user
                SolrDocumentList users = null;
                int pageNo = 1;
                do {
                    users = solrSearchService.findUsersInBranch( branchId, pageSize * ( pageNo - 1 ), pageSize );
                    Map<Long, List<Long>> userRegionsMap = updateRegionIdForUsers( users, region.getRegionId(),
                        branch.getRegion().getRegionId(), branchId );
                    solrSearchService.updateRegionsForMultipleUsers( userRegionsMap );
                    pageNo++;
                } while ( !( users == null || users.isEmpty() ) );

            }
            branch.setRegion( region );
        }
        branch.setBranch( branchName );
        branch.setAddress1( address1 );
        branch.setAddress2( address2 );
        branch.setCountry( country );
        branch.setCountryCode( countryCode );
        branch.setState( state );
        branch.setCity( city );
        branch.setZipcode( zipcode );
        branch.setModifiedBy( String.valueOf( user.getUserId() ) );
        branch.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        branchDao.update( branch );

        LOG.debug( "Update branch in mongo" );
        ContactDetailsSettings contactDetailsSettings = getContactDetailsSettingsFromBranch( branch );
        organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByCriteria(
            KEY_CONTACT_DETAIL_SETTINGS, contactDetailsSettings,
            KEY_IDENTIFIER, branchId,
            BRANCH_SETTINGS_COLLECTION );

        LOG.debug( "Updating branch in solr" );
        solrSearchService.addOrUpdateBranchToSolr( branch );

        /**
         * If userId or email is provided, call the service for adding and assigning user to the
         * newly created branch
         */
        if ( selectedUserId > 0l ) {
            LOG.debug( "Fetching user for selectedUserId " + selectedUserId + "to assign to the branch" );
            User assigneeUser = userDao.findById( User.class, selectedUserId );
            if ( assigneeUser == null ) {
                throw new NoRecordsFetchedException( "No user found in db for selectedUserId:" + selectedUserId );
            }
            try {
                assignBranchToUser( user, branch.getBranchId(), branch.getRegion().getRegionId(), assigneeUser, isAdmin );
            } catch ( InvalidInputException | NoRecordsFetchedException | SolrException e ) {
                LOG.error( "Exception while assigning branch to a user. Reason:" + e.getMessage(), e );
                throw new UserAssignmentException( e.getMessage(), e );
            }
        } else if ( emailIdsArray != null && emailIdsArray.length > 0 ) {
            LOG.debug( "Fetching users list to assign to the branch" );
            userMap = getUsersFromEmailIdsAndInvite( emailIdsArray, user, holdSendingMail, true, isAddedByRealtechOrSSAdmin );
            List<User> assigneeUsers = userMap.get( CommonConstants.VALID_USERS_LIST );

            if ( assigneeUsers != null && !assigneeUsers.isEmpty() ) {
                for ( User assigneeUser : assigneeUsers ) {
                    try {
                        assignBranchToUser( user, branch.getBranchId(), branch.getRegion().getRegionId(), assigneeUser,
                            isAdmin );
                    } catch ( InvalidInputException | NoRecordsFetchedException | SolrException e ) {
                        LOG.error( "Exception while assigning branch to a user. Reason:" + e.getMessage(), e );
                        throw new UserAssignmentException( e.getMessage(), e );
                    }
                }
            }
        }
        map.put( CommonConstants.BRANCH_OBJECT, branch );
        map.put( CommonConstants.INVALID_USERS_LIST, userMap.get( CommonConstants.INVALID_USERS_LIST ) );
        LOG.debug( "Method to update branch completed successfully" );
        return map;
    }


    /*
     * Method to read default survey mail content from EmailTemplate which will be store into the
     * Company Settings.
     */
    public String readMailContentFromFile( String fileName ) throws IOException
    {
        LOG.debug( "readSurveyReminderMailContentFromFile() started" );
        BufferedReader reader = new BufferedReader(
            new InputStreamReader( this.getClass().getClassLoader().getResourceAsStream( fileName ) ) );
        StringBuilder content = new StringBuilder();
        String line = reader.readLine();
        try {
            while ( line != null ) {
                content.append( line );
                line = reader.readLine();
            }
        } finally {
            reader.close();
        }
        LOG.debug( "readSurveyReminderMailContentFromFile() finished" );
        return content.toString();
    }


    /*
     * Method to fetch the list of states in US
     */
    @Override
    @Transactional
    public List<StateLookup> getUsStateList()
    {
        LOG.debug( "Method getUSStateList called to fetch the list of states in US" );
        List<StateLookup> lookups = stateLookupDao.findByKeyValueAscending( StateLookup.class, new HashMap<String, Object>(),
            "statename" );
        return lookups;
    }


    /*
     * Method to fetch the list of cities and zipcodes in the state
     */
    @Override
    @Transactional
    public String getZipCodesByStateId( int stateId )
    {
        LOG.debug( "Method getZipCodesByStateId called to fetch the list of cities and zipcodes in the state" );
        StateLookup state = stateLookupDao.findById( StateLookup.class, stateId );
        List<ZipCodeLookup> zipCodes = (List<ZipCodeLookup>) zipCodeLookupDao.findByColumn( ZipCodeLookup.class, "stateLookup",
            state );
        Gson gson = new GsonBuilder().setExclusionStrategies( new ZipCodeExclusionStrategy() ).create();
        for ( ZipCodeLookup s : zipCodes ) {
            gson.toJson( s );
        }
        return gson.toJson( zipCodes );
    }


    /*
     * Method to disable accounts who have exceeded their billing cycle and return their list.
     */
    @Override
    @Transactional
    public List<DisabledAccount> disableAccounts( Date maxDisableDate )
    {
        LOG.debug( "Method getDisabledAccounts started." );
        try {
            List<DisabledAccount> disabledAccounts = disabledAccountDao.getAccountsToDisable( maxDisableDate );
            LOG.debug( "Method getDisabledAccounts finished." );
            return disabledAccounts;
        } catch ( DatabaseException e ) {
            LOG.error( "Database exception caught in getDisabledAccounts(). Nested exception is ", e );
            throw e;
        }
    }


    /*
     * Method to disable accounts who have exceeded their billing cycle and return their list.
     */
    @Override
    @Transactional
    public List<DisabledAccount> getAccountsForPurge( int graceSpan )
    {
        LOG.debug( "Method getAccountsForPurge started." );
        try {
            LOG.debug( "Method getAccountsForPurge finished." );
            List<DisabledAccount> disabledAccounts = disabledAccountDao.getAccountsForPurge( graceSpan );
            return disabledAccounts;
        } catch ( DatabaseException e ) {
            LOG.error( "Database exception caught in getAccountsForPurge(). Nested exception is ", e );
            throw e;
        }
    }


    /**
     * Method to remove a company from Solr
     * @param company
     * @throws InvalidInputException
     * @throws SolrException
     */
    @Override
    public void deleteCompanyFromSolr( Company company ) throws InvalidInputException, SolrException
    {
        if ( company == null ) {
            throw new InvalidInputException( "The company object is null" );
        }
        LOG.debug( "Method to delete the company : " + company.getCompany() + " from Solr started." );
        removeUsersInCompanyFromSolr( company );
        removeBranchesInCompanyFromSolr( company );
        removeRegionsInCompanyFromSolr( company );
        LOG.debug( "Method to delete the company : " + company.getCompany() + " from Solr finished." );
    }


    /**
     * Method to remove all the users in a company from Solr
     * @param company
     * @throws InvalidInputException
     * @throws SolrException
     */
    void removeUsersInCompanyFromSolr( Company company ) throws InvalidInputException, SolrException
    {
        LOG.debug( "Method to remove all users in company : " + company.getCompany() + " started" );
        List<Long> agentIds = null;
        do {
            agentIds = solrSearchService.searchUserIdsByCompany( company.getCompanyId() );
            if ( agentIds == null || agentIds.isEmpty() ) {
                break;
            }
            solrSearchService.removeUsersFromSolr( agentIds );
        } while ( true );
        LOG.debug( "Method to remove all users in company : " + company.getCompany() + " finished" );
    }


    /**
     * Method to remove all the branches in a company from Solr
     * @param company
     * @throws InvalidInputException
     * @throws SolrException
     */
    void removeBranchesInCompanyFromSolr( Company company ) throws InvalidInputException, SolrException
    {
        LOG.debug( "Method to remove all branches in company : " + company.getCompany() + " started" );
        List<Long> branchIds = null;
        do {
            branchIds = solrSearchService.searchBranchIdsByCompany( company.getCompanyId() );
            if ( branchIds == null || branchIds.isEmpty() ) {
                break;
            }
            solrSearchService.removeBranchesFromSolr( branchIds );
        } while ( true );
        LOG.debug( "Method to remove all branches in company : " + company.getCompany() + " finished" );
    }


    /**
     * Method to remove all the regions in a company from Solr
     * @param company
     * @throws InvalidInputException
     * @throws SolrException
     */
    void removeRegionsInCompanyFromSolr( Company company ) throws InvalidInputException, SolrException
    {
        LOG.debug( "Method to remove all regions in company : " + company.getCompany() + " started" );
        List<Long> regionIds = null;
        do {
            regionIds = solrSearchService.searchRegionIdsByCompany( company.getCompanyId() );
            if ( regionIds == null || regionIds.isEmpty() ) {
                break;
            }
            solrSearchService.removeRegionsFromSolr( regionIds );
        } while ( true );
        LOG.debug( "Method to remove all regions in company : " + company.getCompany() + " finished" );
    }


    /*
     * Method to purge all the details of the given company(Not recoverable).
     */
    @Override
    @Transactional
    public void purgeCompany( Company company ) throws InvalidInputException, SolrException
    {
        LOG.debug( "Method purgeCompany started." );
        try {
            if ( company == null ) {
                LOG.error( "Null value passed for company in purgeCompany(). Returning..." );
                throw new InvalidInputException( "Null value passed for company in purgeCompany()." );
            }

            List<Long> agentIds = null;
            do {
                agentIds = solrSearchService.searchUserIdsByCompany( company.getCompanyId() );
                if ( agentIds == null || agentIds.isEmpty() ) {
                    break;
                }
                organizationUnitSettingsDao.removeOganizationUnitSettings( agentIds,
                    CommonConstants.AGENT_SETTINGS_COLLECTION );
                solrSearchService.removeUsersFromSolr( agentIds );
            } while ( true );

            // Deleting all the users of company from MySQL
            userProfileDao.deleteUserProfilesByCompany( company.getCompanyId() );

            // Delete foreign key references from Removed users.
            removedUserDao.deleteRemovedUsersByCompany( company.getCompanyId() );

            userDao.deleteUsersByCompanyId( company.getCompanyId() );

            List<Long> branchIds = null;
            do {
                branchIds = solrSearchService.searchBranchIdsByCompany( company.getCompanyId() );
                if ( branchIds == null || branchIds.isEmpty() ) {
                    break;
                }
                organizationUnitSettingsDao.removeOganizationUnitSettings( branchIds,
                    CommonConstants.BRANCH_SETTINGS_COLLECTION );
                solrSearchService.removeBranchesFromSolr( branchIds );
            } while ( true );

            // Deleting all the branches of company from MySQL
            branchDao.deleteBranchesByCompanyId( company.getCompanyId() );

            List<Long> regionIds = null;
            do {
                regionIds = solrSearchService.searchRegionIdsByCompany( company.getCompanyId() );
                if ( regionIds == null || regionIds.isEmpty() ) {
                    break;
                }
                organizationUnitSettingsDao.removeOganizationUnitSettings( regionIds,
                    CommonConstants.REGION_SETTINGS_COLLECTION );
                solrSearchService.removeRegionsFromSolr( regionIds );
            } while ( true );

            // Deleting all the regions of company from MySQL
            regionDao.deleteRegionsByCompanyId( company.getCompanyId() );

            List<Long> companyIds = new ArrayList<>();
            companyIds.add( company.getCompanyId() );

            organizationUnitSettingsDao.removeOganizationUnitSettings( companyIds,
                CommonConstants.COMPANY_SETTINGS_COLLECTION );

            // Delete all the details from tables which are related to current company.
            performPreCompanyDeletions( company.getCompanyId() );

            // Deleting company from MySQL
            companyDao.delete( company );
        } catch ( DatabaseException e ) {
            LOG.error( "Database exception caught in purgeCompany(). Nested exception is ", e );
            throw e;
        }
    }


    /*
     * Method to delete all the details of the given company(recoverable).
     */
    @Override
    @Transactional
    public void deleteCompany( Company company, User loggedInUser, int status ) throws InvalidInputException, SolrException
    {
        LOG.debug( "Method deleteCompany started." );
        try {
            if ( company == null ) {
                LOG.error( "Null value passed for company in deleteCompany(). Returning..." );
                throw new InvalidInputException( "Null value passed for company in deleteCompany()." );
            }

            // Deleting users
            if ( company.getUsers() != null && !company.getUsers().isEmpty() ) {
                for ( User user : company.getUsers() ) {
                    if ( CommonConstants.STATUS_INACTIVE != user.getStatus() ) {
                        userManagementService.deleteUserDataFromAllSources( loggedInUser, user.getUserId(),
                            CommonConstants.STATUS_COMPANY_DELETED, false, true );

                    }
                }
            }

            // Deleting branches
            if ( company.getBranches() != null && !company.getBranches().isEmpty() ) {
                for ( Branch branch : company.getBranches() ) {
                    if ( CommonConstants.STATUS_INACTIVE != branch.getStatus() ) {
                        this.deleteBranchDataFromAllSources( branch.getBranchId(), loggedInUser, null,
                            CommonConstants.STATUS_COMPANY_DELETED );
                    }
                }
            }

            // Deleting regions
            if ( company.getRegions() != null && !company.getRegions().isEmpty() ) {
                for ( Region region : company.getRegions() ) {
                    if ( CommonConstants.STATUS_INACTIVE != region.getStatus() ) {
                        this.deleteRegionDataFromAllSources( region.getRegionId(), loggedInUser, null,
                            CommonConstants.STATUS_COMPANY_DISABLED );
                    }
                }
            }

            //Update profile name and url
            this.updateProfileUrlAndStatusForDeletedEntity( CommonConstants.COMPANY_ID_COLUMN, company.getCompanyId() );

            //Remove social media connections
            socialManagementService.disconnectAllSocialConnections( CommonConstants.COMPANY_ID_COLUMN, company.getCompanyId() );

            // updating record in disabled account
            markDisabledAccountAsProcessed( company );

            // Deleting company from MySQL
            company.setStatus( status );
            this.updateCompany( company );
        } catch ( DatabaseException e ) {
            LOG.error( "Database exception caught in getAccountsForPurge(). Nested exception is ", e );
            throw e;
        }
    }


    @Override
    @Transactional
    public void deleteBranchDataFromAllSources( long branchId, User user, UserHierarchyAssignments assignments, int status )
        throws InvalidInputException, SolrException
    {
        LOG.debug( "Method deleteBranchDataFromAllSources called for branchId:" + branchId );

        // Deactivating branch in MySql db.
        this.updateBranchStatus( user, branchId, status );

        // Removing the branch from user hierarchy assignments being stored in session
        if ( assignments != null ) {
            removeBranchFromUserHierarchyAssignmentsInSession( branchId, assignments );
        }

        //Update profile name and url
        this.updateProfileUrlAndStatusForDeletedEntity( CommonConstants.BRANCH_ID_COLUMN, branchId );

        //Remove social media connections
        socialManagementService.disconnectAllSocialConnections( CommonConstants.BRANCH_ID_COLUMN, branchId );

        // Removing branch data from solr.
        solrSearchService.removeBranchFromSolr( branchId );

        LOG.debug( "Method deleteBranchDataFromAllSources executed successfully" );
    }


    @Override
    @Transactional
    public void deleteRegionDataFromAllSources( long regionId, User user, UserHierarchyAssignments assignments, int status )
        throws InvalidInputException, SolrException
    {
        LOG.debug( "Method deleteRegionDataFromAllSources called for branchId:" + regionId );

        // Deactivating region in MySql db.
        this.updateRegionStatus( user, regionId, status );

        // Removing the region from user hierarchy assignments being stored in session
        if ( assignments != null ) {
            removeRegionFromUserHierarchyAssignmentsInSession( regionId, assignments );
        }

        //Update profile name and url
        this.updateProfileUrlAndStatusForDeletedEntity( CommonConstants.REGION_ID_COLUMN, regionId );

        //Remove social media connections
        socialManagementService.disconnectAllSocialConnections( CommonConstants.REGION_ID_COLUMN, regionId );

        // Removing region data from solr.
        solrSearchService.removeRegionFromSolr( regionId );

        LOG.debug( "Method deleteRegionDataFromAllSources executed successfully" );
    }


    private void removeBranchFromUserHierarchyAssignmentsInSession( long branchId, UserHierarchyAssignments assignments )
    {
        LOG.debug( "Method removeBranchFromUserHierarchyAssignmentsInSession called for branchId:" + branchId );
        Map<Long, String> branches = assignments.getBranches();
        if ( branches != null && branches.containsKey( branchId ) ) {
            branches.remove( branchId );
        }
        LOG.debug( "Method removeBranchFromUserHierarchyAssignmentsInSession executed successfully" );
    }


    private void removeRegionFromUserHierarchyAssignmentsInSession( long regionId, UserHierarchyAssignments assignments )
    {
        LOG.debug( "Method removeRegionFromUserHierarchyAssignmentsInSession called for regionId:" + regionId );
        Map<Long, String> regions = assignments.getRegions();
        if ( regions != null && regions.containsKey( regionId ) ) {
            regions.remove( regionId );
        }
        LOG.debug( "Method removeRegionFromUserHierarchyAssignmentsInSession executed successfully" );
    }


    // Method to update an existing company details.
    @Override
    @Transactional
    public void updateCompany( Company company ) throws DatabaseException
    {
        LOG.debug( "Method to change company details updateCompany() started." );
        companyDao.merge( company );
        LOG.debug( "Method to change company details updateCompany() finished." );
    }


    @Override
    public void deactivateCompanyInMongo( Company company ) throws InvalidInputException
    {
        LOG.debug( "Method to deactivate company in mongo started" );
        if ( company == null ) {
            throw new InvalidInputException( "Company object is null" );
        }
        OrganizationUnitSettings companySettings = getCompanySettings( company.getCompanyId() );

        //Set status of company setting to DELETED
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings( CommonConstants.STATUS_COLUMN,
            CommonConstants.STATUS_DELETED_MONGO, companySettings,
            COMPANY_SETTINGS_COLLECTION );

        LOG.debug( "Method to deactivate company in mongo finished" );
    }


    void performPreCompanyDeletions( long companyId )
    {
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.COMPANY_ID_COLUMN, companyId );
        List<LicenseDetail> licenseDetails = licenseDetailDao.findByColumn( LicenseDetail.class, "company.companyId",
            companyId );
        // Delete from PaymentRetry table
        List<String> conditions = new ArrayList<>();
        if ( licenseDetails != null && !licenseDetails.isEmpty() ) {
            StringBuilder licenseIds = new StringBuilder( "(" );
            for ( LicenseDetail license : licenseDetails ) {
                licenseIds.append( license.getLicenseId() ).append( "," );
            }
            int commaIndex = licenseIds.lastIndexOf( "," );
            if ( commaIndex != -1 ) {
                licenseIds = new StringBuilder( licenseIds.substring( 0, commaIndex ) );
                licenseIds.append( ")" );
            }

            conditions.add( "licenseDetail.licenseId in " + licenseIds );
            retriedTransactionDao.deleteByCondition( "RetriedTransaction", conditions );
        }
        conditions.clear();

        conditions.add( "company.companyId = " + companyId );

        disabledAccountDao.deleteByCondition( "DisabledAccount", conditions );

        licenseDetailDao.deleteByCondition( "LicenseDetail", conditions );

        userInviteDao.deleteByCondition( "UserInvite", conditions );

        usercountModificationNotificationDao.deleteByCondition( "UsercountModificationNotification", conditions );

        surveyCompanyMappingDao.deleteByCondition( "SurveyCompanyMapping", conditions );

        fileUploadDao.deleteByCondition( "FileUpload", conditions );

        //Delete entries from UPLOAD_STATUS table
        List<String> deletionConditions = new ArrayList<String>();
        deletionConditions.add( "company.companyId = " + companyId );
        uploadStatusDao.deleteByCondition( "UploadStatus", deletionConditions );

        List<CrmBatchTracker> crmBatchTrackerList = crmBatchTrackerDao.findByColumn( CrmBatchTracker.class, "companyId",
            companyId );
        if ( crmBatchTrackerList != null ) {
            for ( CrmBatchTracker crmBatchTracker : crmBatchTrackerList ) {
                crmBatchTrackerDao.delete( crmBatchTracker );
            }
        }
        List<SurveyPreInitiation> surveyPreInitiationList = surveyPreInitiationDao.findByColumn( SurveyPreInitiation.class,
            "companyId", companyId );
        if ( surveyPreInitiationList != null ) {
            for ( SurveyPreInitiation surveyPreInitiation : surveyPreInitiationList ) {
                surveyPreInitiationDao.delete( surveyPreInitiation );
            }
        }

        LOG.debug( "Deleting user api keys for this company " );
        List<UserApiKey> userApiKeyList = userApiKeyDao.findByColumn( UserApiKey.class, "companyId", companyId );
        if ( userApiKeyList != null ) {
            for ( UserApiKey userApiKey : userApiKeyList ) {
                userApiKeyDao.delete( userApiKey );
            }
        }
    }


    @Override
    @Transactional
    public List<VerticalCrmMapping> getCrmMapping( User user ) throws InvalidInputException
    {
        user = userDao.findById( User.class, user.getUserId() );
        List<VerticalCrmMapping> mappings = user.getCompany().getVerticalsMaster().getVerticalCrmMappings();

        if ( mappings == null || mappings.isEmpty() ) {
            Map<String, Object> queries = new HashMap<>();
            VerticalsMaster defaultVerticalMaster = verticalMastersDao.findById( VerticalsMaster.class,
                CommonConstants.DEFAULT_VERTICAL_ID );
            queries.put( "verticalsMaster", defaultVerticalMaster );
            List<VerticalCrmMapping> defaultMappings = verticalCrmMappingDo.findByKeyValue( VerticalCrmMapping.class, queries );
            mappings.addAll( defaultMappings );
        }

        for ( VerticalCrmMapping mapping : mappings ) {
            mapping.getCrmMaster().getCrmMasterId();
            mapping.getCrmMaster().getCrmName();
        }
        return mappings;
    }


    @Override
    public Map<Long, OrganizationUnitSettings> getSettingsMapWithLinkedinImage( String profileLevel )
    {

        String collectionName = "";
        String regexpattern = "^((?!" + cdnPath + ").)*$";
        switch ( profileLevel ) {
            case CommonConstants.COMPANY:
                collectionName = COMPANY_SETTINGS_COLLECTION;
                break;
            case CommonConstants.REGION_COLUMN:
                collectionName = REGION_SETTINGS_COLLECTION;
                break;
            case CommonConstants.BRANCH_NAME_COLUMN:
                collectionName = BRANCH_SETTINGS_COLLECTION;
                break;
            case "agent":
                collectionName = AGENT_SETTINGS_COLLECTION;
                break;
        }
        return organizationUnitSettingsDao.getSettingsMapWithLinkedinImageUrl( collectionName, regexpattern );
    }


    @Override
    public SurveySettings retrieveDefaultSurveyProperties()
    {
        SurveySettings surveySettings = new SurveySettings();
        surveySettings.setHappyText( happyText );
        surveySettings.setNeutralText( neutralText );
        surveySettings.setSadText( sadText );
        surveySettings.setHappyTextComplete( happyTextComplete );
        surveySettings.setNeutralTextComplete( neutralTextComplete );
        surveySettings.setSadTextComplete( sadTextComplete );
       
        return surveySettings;
    }
    
    @Override
    public SurveySettings retrieveDefaultSecondaryWorkflowValues() {
        
        SurveySettings surveySettings = new SurveySettings();
        ApplicationSettings applicationSettings = mongoApplicationSettingsDao.getApplicationSettings();
        surveySettings.setHappyTextPartner( applicationSettings.getDefaultHappyTextPartner() );
        surveySettings.setNeutralTextPartner( applicationSettings.getDefaultNeutralTextPartner() );
        surveySettings.setSadTextPartner( applicationSettings.getDefaultSadTextPartner() );
        surveySettings.setHappyTextCompletePartner( applicationSettings.getDefaultHappyTextCompletePartner() );
        surveySettings.setNeutralTextCompletePartner( applicationSettings.getDefaultNeutralTextCompletePartner() );
        surveySettings.setSadTextCompletePartner( applicationSettings.getDefaultSadTextCompletePartner() );
        return surveySettings;
    }


    @Override
    public String resetDefaultSurveyText( SurveySettings surveySettings, String mood )
    {
        if ( mood.equalsIgnoreCase( "happy" ) ) {
            surveySettings.setHappyText( happyText );
            return happyText;
        } else if ( mood.equalsIgnoreCase( "neutral" ) ) {
            surveySettings.setNeutralText( neutralText );
            return neutralText;
        } else if ( mood.equalsIgnoreCase( "sad" ) ) {
            surveySettings.setSadText( sadText );
            return sadText;
        } else if ( mood.equalsIgnoreCase( "happyComplete" ) ) {
            surveySettings.setHappyTextComplete( happyTextComplete );
            return happyTextComplete;
        } else if ( mood.equalsIgnoreCase( "neutralComplete" ) ) {
            surveySettings.setNeutralTextComplete( neutralTextComplete );
            return neutralTextComplete;
        } else if ( mood.equalsIgnoreCase( "sadComplete" ) ) {
            surveySettings.setSadTextComplete( sadTextComplete );
            return sadTextComplete;
        } else if(mood.contains( "Partner" )) {
            
            SurveySettings defaultSurveySettingsForPartner = this.retrieveDefaultSecondaryWorkflowValues();
            
            if ( mood.equalsIgnoreCase( "happyPartner" ) ) {
                surveySettings.setHappyTextPartner( defaultSurveySettingsForPartner.getHappyTextPartner() );
                return defaultSurveySettingsForPartner.getHappyTextPartner();
            } else if ( mood.equalsIgnoreCase( "neutralPartner" ) ) {
                surveySettings.setNeutralTextPartner( defaultSurveySettingsForPartner.getNeutralTextPartner() );
                return defaultSurveySettingsForPartner.getNeutralTextPartner();
            } else if ( mood.equalsIgnoreCase( "sadPartner" ) ) {
                surveySettings.setSadTextPartner( defaultSurveySettingsForPartner.getSadTextPartner() );
                return defaultSurveySettingsForPartner.getSadTextPartner();
            } else if ( mood.equalsIgnoreCase( "happyCompletePartner" ) ) {
                surveySettings.setHappyTextCompletePartner( defaultSurveySettingsForPartner.getHappyTextCompletePartner() );
                return defaultSurveySettingsForPartner.getHappyTextCompletePartner();
            } else if ( mood.equalsIgnoreCase( "neutralCompletePartner" ) ) {
                surveySettings.setNeutralTextCompletePartner( defaultSurveySettingsForPartner.getNeutralTextCompletePartner() );
                return defaultSurveySettingsForPartner.getNeutralTextCompletePartner();
            } else if ( mood.equalsIgnoreCase( "sadCompletePartner" ) ) {
                surveySettings.setSadTextCompletePartner( defaultSurveySettingsForPartner.getSadTextCompletePartner() );
                return defaultSurveySettingsForPartner.getSadTextCompletePartner();
            }
            else {                
                return "";
            }
        }
        else {
            return "";
        }
    }


    @Override
    @Transactional
    public List<Company> getCompaniesByName( String namePattern )
    {
        List<Company> companyList = new ArrayList<>();
        List<Object[]> result = companyDao.searchCompaniesByName( namePattern );
        for(Object[] obj : result) {
            Company company = new Company();
            company.setCompanyId( Long.parseLong( obj[0].toString() ) );
            company.setCompany( (String) obj[1] );
            company.setStatus( Integer.parseInt( obj[2].toString() ) );
            companyList.add( company );
        }   
        return companyList;
    }


    @Override
    @Transactional
    public Company getCompanyById( long companyId )
    {
        return companyDao.findById( Company.class, companyId );
    }


    @Override
    public List<OrganizationUnitSettings> getAllCompaniesFromMongo()
    {
        LOG.debug( "Method getAllCompaniesFromMongo() called" );

        List<OrganizationUnitSettings> unitSettings = organizationUnitSettingsDao.getCompanyList();

        return unitSettings;
    }


    @Override
    public List<AgentSettings> getAllAgentsFromMongo()
    {
        LOG.debug( "Method AgentSettings() called" );

        List<AgentSettings> unitSettings = organizationUnitSettingsDao.getAllAgentSettings();

        return unitSettings;
    }


    @Transactional
    @Override
    public List<OrganizationUnitSettings> getAllActiveCompaniesFromMongo()
    {
        LOG.debug( "Method getAllCompaniesFromMongo() called" );

        List<Company> companyList = companyDao.findAllActive( Company.class );

        Set<Long> companyIds = new HashSet<>();

        for ( Company company : companyList ) {
            companyIds.add( company.getCompanyId() );
        }

        List<OrganizationUnitSettings> unitSettings = organizationUnitSettingsDao.getCompanyListByIds( companyIds );
        //Collections.sort( unitSettings, new OrganizationUnitSettingsComparator() );
        return unitSettings;
    }


    @Override
    public List<OrganizationUnitSettings> getCompaniesByNameFromMongo( String searchKey )
    {
        LOG.debug( "Method getCompaniesByNameFromMongo() called" );
        List<OrganizationUnitSettings> unitSettings = organizationUnitSettingsDao.getCompanyListByKey( searchKey );
        return unitSettings;
    }


    @Transactional
    @Override
    public List<OrganizationUnitSettings> getCompaniesByKeyValueFromMongo( String searchKey, int accountType, int status,
        boolean inCompleteCompany, int noOfDays )
    {
        LOG.debug( "Method getCompaniesByNameFromMongo() called" );
        Calendar startTime = Calendar.getInstance();
        startTime.add( Calendar.DATE, -1 * noOfDays );
        // strip the time component of start time
        startTime.set( Calendar.HOUR_OF_DAY, 0 );
        startTime.set( Calendar.MINUTE, 0 );
        startTime.set( Calendar.SECOND, 0 );
        startTime.set( Calendar.MILLISECOND, 0 );

        Timestamp startDate = null;
        if ( noOfDays >= 0 )
            startDate = new Timestamp( startTime.getTimeInMillis() );

        List<Long> companyIdList = companyDao.searchCompaniesByNameAndKeyValue( searchKey, accountType, status,
            inCompleteCompany, startDate );

        Set<Long> companyIds = new HashSet<>();
        companyIds.addAll( companyIdList );

        List<OrganizationUnitSettings> unitSettings = organizationUnitSettingsDao.getCompanyListByIds( companyIds );
        //Collections.sort( unitSettings, new OrganizationUnitSettingsComparator() ); 
        return unitSettings;
    }


    /**
     * Method to get the company list by date range
     */
    @Override
    @Transactional
    public List<Company> getCompaniesByDateRange( Date startDate, Date endDate )
    {
        Timestamp startTime = null;
        Timestamp endTime = null;
        if ( startDate != null )
            startTime = new Timestamp( startDate.getTime() );
        if ( endDate != null )
            endTime = new Timestamp( endDate.getTime() );

        List<Company> companies = companyDao.getCompaniesByDateRange( startTime, endTime );

        return companies;
    }


    @Override
    public XSSFWorkbook downloadCompanyReport( List<Company> companies )
    {
        Map<Integer, List<Object>> data = workbookData.getCompanyReportDataToBeWrittenInSheet( companies );
        XSSFWorkbook workbook = workbookOperations.createWorkbook( data, "d-mm-yyyy" );
        return workbook;
    }


    @Transactional
    @Override
    public List<FeedIngestionEntity> fetchSocialMediaTokens( String collectionName, int skipCount, int batchSize )
    {
        List<FeedIngestionEntity> fieldIngestionEntities = organizationUnitSettingsDao.fetchSocialMediaTokens( collectionName,
            skipCount, batchSize );
        return fieldIngestionEntities;
    }


    @Override
    @Transactional
    public SocialMediaTokensPaginated fetchSocialMediaTokensPaginated( int skipCount, int batchSize )
        throws InvalidInputException
    {
        LOG.info( "Inside method fetchSocialMediaTokensResponse" );
        SocialMediaTokensPaginated socialMediaTokensPaginated = new SocialMediaTokensPaginated();
        int totalRecord = 0;

        // fetch companies media token
        long companyTokensCount = organizationUnitSettingsDao
            .getSocialMediaTokensCount( COMPANY_SETTINGS_COLLECTION );
        long regionTokensCount = organizationUnitSettingsDao
            .getSocialMediaTokensCount( REGION_SETTINGS_COLLECTION );
        long branchTokensCount = organizationUnitSettingsDao
            .getSocialMediaTokensCount( BRANCH_SETTINGS_COLLECTION );
        long agentTokensCount = organizationUnitSettingsDao
            .getSocialMediaTokensCount( AGENT_SETTINGS_COLLECTION );

        if ( skipCount < companyTokensCount ) {
            List<SocialMediaTokenResponse> companiesMediaTokens = organizationUnitSettingsDao
                .getSocialMediaTokensByCollection( COMPANY_SETTINGS_COLLECTION,
                    skipCount, batchSize );

            totalRecord = companiesMediaTokens.size();

            socialMediaTokensPaginated.setCompaniesTokens( companiesMediaTokens );

            if ( totalRecord < batchSize ) {
                List<SocialMediaTokenResponse> regionsMediaTokens = organizationUnitSettingsDao
                    .getSocialMediaTokensByCollection( REGION_SETTINGS_COLLECTION, 0,
                        batchSize - totalRecord );
                totalRecord += regionsMediaTokens.size();

                socialMediaTokensPaginated.setRegionsTokens( regionsMediaTokens );

                if ( totalRecord < batchSize ) {
                    List<SocialMediaTokenResponse> branchMediaTokens = organizationUnitSettingsDao
                        .getSocialMediaTokensByCollection( BRANCH_SETTINGS_COLLECTION,
                            0, batchSize - totalRecord );
                    totalRecord += branchMediaTokens.size();
                    socialMediaTokensPaginated.setBranchesTokens( branchMediaTokens );

                    if ( totalRecord < batchSize ) {
                        List<SocialMediaTokenResponse> agentMediaTokens = organizationUnitSettingsDao
                            .getSocialMediaTokensByCollection(
                                AGENT_SETTINGS_COLLECTION, 0,
                                batchSize - totalRecord );
                        totalRecord +=agentMediaTokens.size();
                        socialMediaTokensPaginated.setAgentsTokens( agentMediaTokens );
                    }
                }
            }
        } else if ( skipCount < companyTokensCount + regionTokensCount ) {

            List<SocialMediaTokenResponse> regionsMediaTokens = organizationUnitSettingsDao
                .getSocialMediaTokensByCollection( REGION_SETTINGS_COLLECTION,
                    (int) ( skipCount - companyTokensCount ), batchSize );
            totalRecord += regionsMediaTokens.size();

            socialMediaTokensPaginated.setRegionsTokens( regionsMediaTokens );

            if ( totalRecord < batchSize ) {
                List<SocialMediaTokenResponse> branchMediaTokens = organizationUnitSettingsDao
                    .getSocialMediaTokensByCollection( BRANCH_SETTINGS_COLLECTION, 0,
                        batchSize - totalRecord );
                totalRecord += branchMediaTokens.size();
                socialMediaTokensPaginated.setBranchesTokens( branchMediaTokens );

                if ( totalRecord < batchSize ) {
                    List<SocialMediaTokenResponse> agentMediaTokens = organizationUnitSettingsDao
                        .getSocialMediaTokensByCollection( AGENT_SETTINGS_COLLECTION, 0,
                            batchSize - totalRecord );
                    totalRecord +=agentMediaTokens.size();
                    socialMediaTokensPaginated.setAgentsTokens( agentMediaTokens );
                }
            }
        } else if ( skipCount < companyTokensCount + regionTokensCount + branchTokensCount ) {
            List<SocialMediaTokenResponse> branchMediaTokens = organizationUnitSettingsDao.getSocialMediaTokensByCollection(
                BRANCH_SETTINGS_COLLECTION,
                (int) ( skipCount - ( companyTokensCount + regionTokensCount ) ), batchSize );
            totalRecord += branchMediaTokens.size();
            socialMediaTokensPaginated.setBranchesTokens( branchMediaTokens );

            if ( totalRecord < batchSize ) {
                List<SocialMediaTokenResponse> agentMediaTokens = organizationUnitSettingsDao
                    .getSocialMediaTokensByCollection( AGENT_SETTINGS_COLLECTION, 0,
                        batchSize - totalRecord );
                totalRecord +=agentMediaTokens.size();
                socialMediaTokensPaginated.setAgentsTokens( agentMediaTokens );
            }
        } else if ( skipCount < companyTokensCount + regionTokensCount + branchTokensCount + agentTokensCount ) {
            List<SocialMediaTokenResponse> agentMediaTokens = organizationUnitSettingsDao.getSocialMediaTokensByCollection(
                AGENT_SETTINGS_COLLECTION,
                (int) ( skipCount - ( companyTokensCount + regionTokensCount + branchTokensCount ) ), batchSize );
            totalRecord +=agentMediaTokens.size();
            socialMediaTokensPaginated.setAgentsTokens( agentMediaTokens );
        }
        
        socialMediaTokensPaginated.setTotalRecord(totalRecord);

        setCompanyIdsMap( socialMediaTokensPaginated );

        LOG.info( "End of method fetchSocialMediaTokensResponse" );
        return socialMediaTokensPaginated;
    }
    

    /**
     * Method to set companies map in social media paginated response.
     * @param socialMediaTokensPaginated
     */
    private void setCompanyIdsMap( SocialMediaTokensPaginated socialMediaTokensPaginated )
    {
        LOG.debug( "Inside  setCompanyIdsMap" );
        List<Long> regionIds = new ArrayList<>();

        for ( SocialMediaTokenResponse regionToken : socialMediaTokensPaginated.getRegionsTokens() ) {
            regionIds.add( regionToken.getIden() );
        }

        Map<Long, Long> regionCompanyIdMap = regionDao.getCompanyIdsForRegionIds( regionIds );
        socialMediaTokensPaginated.setRegionCompanyIdMap( regionCompanyIdMap );

        List<Long> branchIds = new ArrayList<>();
        for ( SocialMediaTokenResponse branchToken : socialMediaTokensPaginated.getBranchesTokens() ) {
            branchIds.add( branchToken.getIden() );
        }

        Map<Long, Long> branchCompanyIdMap = branchDao.getCompanyIdsForBranchIds( branchIds );
        socialMediaTokensPaginated.setBranchCompanyIdMap( branchCompanyIdMap );

        List<Long> userIds = new ArrayList<>();
        for ( SocialMediaTokenResponse agentToken : socialMediaTokensPaginated.getAgentsTokens() ) {
            userIds.add( agentToken.getIden() );
        }

        Map<Long, Long> userCompanyIdMap = userDao.getCompanyIdsForUserIds( userIds );
        socialMediaTokensPaginated.setAgentCompanyIdMap( userCompanyIdMap );
        LOG.debug( "End of setCompanyIdsMap" );
    }


    /**
     * Populate SocialMediaTokenResponse from FeedIngestionEntity
     * @param feedIngestionEntity
     * @param profileType
     * @return
     */
    private SocialMediaTokenResponse setSocialMediaTokenResponse(SocialMediaTokenResponse  feedIngestionEntity, long companyId,
        ProfileType profileType )
    {
        if ( feedIngestionEntity != null ) {
            SocialMediaTokenResponse socialMediaTokenResponse = new SocialMediaTokenResponse();
            socialMediaTokenResponse.setIden( feedIngestionEntity.getIden() );
            socialMediaTokenResponse.setCompanyId( companyId );
            socialMediaTokenResponse.setProfileType( profileType );
            socialMediaTokenResponse.setSocialMediaTokens( feedIngestionEntity.getSocialMediaTokens() );
            socialMediaTokenResponse.setProfileImageUrl( feedIngestionEntity.getProfileImageUrl() );
            return socialMediaTokenResponse;
        }
        return null;
    }


    @Transactional
    @Override
    public void updateBranchProfileName( long branchId, String profileName )
    {
        LOG.debug( "Method updateBranchProfileName() started." );
        Branch branch = branchDao.findById( Branch.class, branchId );
        branch.setProfileName( profileName );
        branchDao.update( branch );
        LOG.debug( "Method updateBranchProfileName() finished." );

    }


    @Transactional
    @Override
    public void updateRegionProfileName( long regionId, String profileName )
    {
        LOG.debug( "Method updateRegionProfileName() started." );
        Region region = regionDao.findById( Region.class, regionId );
        region.setProfileName( profileName );
        regionDao.update( region );
        LOG.debug( "Method updateRegionProfileName() finished." );
    }


    @Override
    @Transactional
    public Company getPrimaryCompanyByRegion( long regionId )
    {
        Region region = regionDao.findById( Region.class, regionId );
        Company company = null;
        if ( region != null ) {
            company = region.getCompany();
        }
        return company;
    }


    @Override
    @Transactional
    public Region getPrimaryRegionByBranch( long branchId )
    {
        Region region = null;
        Branch branch = branchDao.findById( Branch.class, branchId );
        if ( branch != null ) {
            region = branch.getRegion();
        }

        return region;
    }


    @Override
    @Transactional
    public void updateMailContentForOrganizationUnit( MailContentSettings mailContentSettings,
        OrganizationUnitSettings organizationUnitSettings, String collectionName )
    {
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            KEY_MAIL_CONTENT, mailContentSettings, organizationUnitSettings,
            collectionName );
    }


    @Override
    @Transactional
    public void updateRegion( Region region )
    {
        LOG.debug( "Method to change region details updateRegion() started." );
        regionDao.merge( region );
        LOG.debug( "Method to change region details updateRegion() finished." );
    }


    @Override
    @Transactional
    public void updateBranch( Branch branch )
    {
        LOG.debug( "Method to change branch details updateBranch() started." );
        branchDao.merge( branch );
        LOG.debug( "Method to change branch details updateBranch() finished." );
    }


    @Override
    @Transactional
    public long getLoopsCountByProfile( String profileId, String collectionName, long collectionId )
        throws InvalidInputException
    {
        if ( profileId == null || profileId.isEmpty() ) {
            LOG.error( "Profile id is not passed to get loop count" );
            throw new InvalidInputException( "Profile id is not passed to get loop count" );
        }
        LOG.debug( "Inside method getLoopsByProfile for profileId " + profileId );
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.KEY_DOTLOOP_PROFILE_ID_COLUMN, profileId );
        if ( collectionName.equalsIgnoreCase( COMPANY_SETTINGS_COLLECTION ) ) {
            queries.put( "companyId", collectionId );
        } else if ( collectionName.equalsIgnoreCase( REGION_SETTINGS_COLLECTION ) ) {
            queries.put( "regionId", collectionId );
        } else if ( collectionName.equalsIgnoreCase( BRANCH_SETTINGS_COLLECTION ) ) {
            queries.put( "branchId", collectionId );
        } else if ( collectionName.equalsIgnoreCase( AGENT_SETTINGS_COLLECTION ) ) {
            queries.put( "agentId", collectionId );
        }
        long numberOfLoops = loopProfileMappingDao.findNumberOfRowsByKeyValue( LoopProfileMapping.class, queries );
        return numberOfLoops;
    }


    @Override
    @Transactional
    public void saveLoopsForProfile( LoopProfileMapping loopProfileMapping ) throws InvalidInputException
    {
        if ( loopProfileMapping == null ) {
            LOG.error( "null loop profile mapping sent for insert" );
            throw new InvalidInputException( "null loop profile mapping sent for insert" );
        }
        LOG.debug( "Inside method saveLoopsForProfile " );
        loopProfileMappingDao.save( loopProfileMapping );

    }


    @Override
    @Transactional
    public LoopProfileMapping getLoopByProfileAndLoopId( String profileId, String loopId, String collectionName,
        long collectionId ) throws InvalidInputException
    {
        if ( profileId == null || profileId.isEmpty() || loopId == null || loopId.isEmpty() ) {
            LOG.error( "Profile id/ loop id is not set to fetch loop profile data" );
            throw new InvalidInputException( "Profile id/ loop id is not set to fetch loop profile data" );
        }
        LOG.debug( "Getting loop for profile id: " + profileId + " and loop id: " + loopId );
        LoopProfileMapping loop = null;
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.KEY_DOTLOOP_PROFILE_ID_COLUMN, profileId );
        queries.put( CommonConstants.KEY_DOTLOOP_PROFILE_LOOP_ID_COLUMN, loopId );
        if ( collectionName.equalsIgnoreCase( COMPANY_SETTINGS_COLLECTION ) ) {
            queries.put( "companyId", collectionId );
        } else if ( collectionName.equalsIgnoreCase( REGION_SETTINGS_COLLECTION ) ) {
            queries.put( "regionId", collectionId );
        } else if ( collectionName.equalsIgnoreCase( BRANCH_SETTINGS_COLLECTION ) ) {
            queries.put( "branchId", collectionId );
        } else if ( collectionName.equalsIgnoreCase( AGENT_SETTINGS_COLLECTION ) ) {
            queries.put( "agentId", collectionId );
        }
        List<LoopProfileMapping> loops = loopProfileMappingDao.findByKeyValue( LoopProfileMapping.class, queries );
        if ( loops != null && loops.size() > 0 ) {
            loop = loops.get( CommonConstants.INITIAL_INDEX );
        }
        return loop;
    }


    @Override
    @Transactional
    public CollectionDotloopProfileMapping getCollectionDotloopMappingByCollectionIdAndProfileId( String collectionName,
        long organizationUnitId, String profileId ) throws InvalidInputException
    {
        if ( organizationUnitId <= 0l || profileId == null || profileId.isEmpty() ) {
            LOG.error( "Company id/ profile id is not provided to get company dotloop mapping" );
            throw new InvalidInputException( "Company id/ profile id is not provided to get company dotloop mapping" );
        }
        LOG.debug( "Inside method getCollectionDotloopMappingByCollectionIdAndProfileId for unit " + organizationUnitId );

        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( "profileId", profileId );

        if ( collectionName.equalsIgnoreCase( COMPANY_SETTINGS_COLLECTION ) ) {
            queries.put( "companyId", organizationUnitId );
        } else if ( collectionName.equalsIgnoreCase( REGION_SETTINGS_COLLECTION ) ) {
            queries.put( "regionId", organizationUnitId );
        } else if ( collectionName.equalsIgnoreCase( BRANCH_SETTINGS_COLLECTION ) ) {
            queries.put( "branchId", organizationUnitId );
        } else if ( collectionName.equalsIgnoreCase( AGENT_SETTINGS_COLLECTION ) ) {
            queries.put( "agentId", organizationUnitId );
        }


        List<CollectionDotloopProfileMapping> collectionDotLoopProfileMappingList = collectionDotloopProfileMappingDao
            .findByKeyValue( CollectionDotloopProfileMapping.class, queries );
        if ( collectionDotLoopProfileMappingList == null || collectionDotLoopProfileMappingList.isEmpty() ) {
            return null;
        } else {
            return collectionDotLoopProfileMappingList.get( CommonConstants.INITIAL_INDEX );
        }

    }


    @Override
    @Transactional
    public CollectionDotloopProfileMapping saveCollectionDotLoopProfileMapping(
        CollectionDotloopProfileMapping collectionDotloopProfileMapping ) throws InvalidInputException
    {
        if ( collectionDotloopProfileMapping == null ) {
            LOG.error( "Company dotloop profile mapping is null for insert" );
            throw new InvalidInputException( "Company dotloop profile mapping is null for insert" );
        }
        LOG.debug( "Inside method saveCollectionDotloopProfileMapping " );
        return collectionDotloopProfileMappingDao.save( collectionDotloopProfileMapping );

    }


    @Override
    @Transactional
    public void updateCollectionDotLoopProfileMapping( CollectionDotloopProfileMapping collectionDotloopProfileMapping )
        throws InvalidInputException
    {
        if ( collectionDotloopProfileMapping == null ) {
            LOG.error( "Company dotloop profile mapping is null for update" );
            throw new InvalidInputException( "Company dotloop profile mapping is null for update" );
        }
        LOG.debug( "Inside method savecollectionDotloopProfileMapping " );
        collectionDotloopProfileMappingDao.update( collectionDotloopProfileMapping );

    }


    @Override
    @Transactional
    public CollectionDotloopProfileMapping getCollectionDotloopMappingByProfileId( String profileId )
        throws InvalidInputException
    {
        if ( profileId == null || profileId.isEmpty() ) {
            LOG.error( "Profile id is null to fetch company dot loop mapping" );
            throw new InvalidInputException( "Profile id is null to fetch company dot loop mapping" );
        }
        List<CollectionDotloopProfileMapping> collectionDotloopProfileMappingList = collectionDotloopProfileMappingDao
            .findByColumn( CollectionDotloopProfileMapping.class, "profileId", profileId );
        if ( collectionDotloopProfileMappingList.isEmpty() ) {
            return null;
        } else {
            return collectionDotloopProfileMappingList.get( 0 );
        }
    }


    @Override
    @Transactional
    public List<OrganizationUnitSettings> getOrganizationUnitSettingsForCRMSource( String crmSource, String collectionName )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Getting list of crm info for source: " + crmSource );
        List<OrganizationUnitSettings> organizationUnitSettingsList = null;
        organizationUnitSettingsList = organizationUnitSettingsDao.getOrganizationUnitListWithCRMSource( crmSource,
            collectionName );
        LOG.debug( "Returning organization unit settings list with provided crm list" );
        return organizationUnitSettingsList;
    }


    @Override
    @Transactional
    public List<OrganizationUnitSettings> getCompanyListForEncompass( String state, String encompassVersion )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Getting list of encompass crm info for state : " + state + " and version : " + encompassVersion );
        List<OrganizationUnitSettings> organizationUnitSettingsList = null;
        organizationUnitSettingsList = organizationUnitSettingsDao.getCompanyListForEncompass( state, encompassVersion );
        LOG.debug( "Returning company settings list with provided crm list" );
        return organizationUnitSettingsList;
    }


    /**
     * Method to fetch profile image url for a list of entities
     * 
     * @param entityType
     * @param entityList
     * @return
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public List<ProfileImageUrlData> fetchProfileImageUrlsForEntityList( String entityType, HashSet<Long> entityList )
        throws InvalidInputException
    {
        LOG.debug( "Method fetchProfileImageUrlsForEntityList() called" );
        return organizationUnitSettingsDao.fetchProfileImageUrlsForEntityList( entityType, entityList );
    }


    /**
     * Method to get list of unprocessed images
     * 
     * @param collectionName
     * @param imageType
     * @return
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public Map<Long, String> getListOfUnprocessedImages( String collectionName, String imageType ) throws InvalidInputException
    {
        LOG.debug( "Method getCollectionListOfUnprocessedImages called" );
        return organizationUnitSettingsDao.getCollectionListOfUnprocessedImages( collectionName, imageType );
    }


    /**
     * Method to update image for organization unit setting
     * 
     * @param iden
     * @param imgFileName
     * @param collectionName
     * @param imageType
     * @param flagValue
     * @param isThumbnail
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public void updateImageForOrganizationUnitSetting( long iden, String imgFileName, String thumbnailFileName,
        String rectangularThumbnailFileName, String collectionName, String imageType, boolean flagValue, boolean isThumbnail )
        throws InvalidInputException
    {
        LOG.debug( "Method updateImageForOrganizationUnitSetting called" );
        LOG.debug( "updating mongodb" );
        organizationUnitSettingsDao.updateImageForOrganizationUnitSetting( iden, imgFileName, thumbnailFileName,
            rectangularThumbnailFileName, collectionName, imageType, flagValue, isThumbnail );
        LOG.debug( "updated mongodb" );
        if ( imageType == CommonConstants.IMAGE_TYPE_PROFILE ) {
            LOG.debug( "updating solr" );
            Map<String, Object> updateMap = new HashMap<String, Object>();
            if ( !( isThumbnail ) ) {
                updateMap.put( CommonConstants.PROFILE_IMAGE_THUMBNAIL_COLUMN, imgFileName );
                updateMap.put( CommonConstants.PROFILE_IMAGE_URL_SOLR, imgFileName );
                updateMap.put( CommonConstants.IS_PROFILE_IMAGE_SET_SOLR, true );
            } else {
                updateMap.put( CommonConstants.PROFILE_IMAGE_THUMBNAIL_COLUMN, thumbnailFileName );
            }
            try {
                solrSearchService.editUserInSolrWithMultipleValues( iden, updateMap );
            } catch ( SolrException e ) {
                LOG.error( "SolrException occured while updating user in solr. Reason : ", e );
                throw new InvalidInputException( "SolrException occured while updating user in solr. Reason : ", e );
            }
        }
        LOG.debug( "Method updateImageForOrganizationUnitSetting finished" );
    }


    @Override
    @Transactional
    public List<Region> getRegionsForRegionIds( Set<Long> regionIds ) throws InvalidInputException
    {
        if ( regionIds == null || regionIds.isEmpty() ) {
            LOG.error( "Region ids passed cannot be null or empty" );
            throw new InvalidInputException( "Region ids passed cannot be null or empty" );
        }
        LOG.debug( "Method getRegionsForRegionIds called to get Region for regionIds : " + regionIds );
        List<Region> regions = regionDao.getRegionForRegionIds( regionIds );

        //SS-1421: populate the address of the regions from Solr before sending this list back
        //Use the company ID of the first region in the list
        populateAddressOfRegionFromSolr( regions, regions.get( 0 ).getCompany().getCompanyId() );

        LOG.debug( "Method getRegionsForRegionIds called to get Region for regionIds : " + regionIds );
        return regions;
    }


    @Override
    @Transactional
    public List<Branch> getBranchesForBranchIds( Set<Long> branchIds ) throws InvalidInputException
    {
        if ( branchIds == null || branchIds.isEmpty() ) {
            LOG.error( "Branch ids passed cannot be null or empty" );
            throw new InvalidInputException( "Branch ids passed cannot be null or empty" );
        }
        LOG.debug( "Method getBranchesForBranchIds called to get Branch for branchIds : " + branchIds );
        List<Branch> branches = branchDao.getBranchForBranchIds( branchIds );

        //SS-1421: populate the address of the branches from Solr before sending this list back
        populateAddressOfBranchFromSolr( branches, 0, branchIds, branches.get( 0 ).getCompany() );
        LOG.debug( "Method getBranchesForBranchIds finished getting Branch for branchIds : " + branchIds );
        return branches;
    }


    /**
     * Method to change profileurl of entity on delete
     * JIRA SS-1365
     * 
     * @param entityType
     * @param entityId
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public void updateProfileUrlAndStatusForDeletedEntity( String entityType, long entityId ) throws InvalidInputException
    {
        LOG.debug( "Updating profile url for deleted entity type : " + entityType + " with ID : " + entityId );

        if ( entityType == null || entityType.isEmpty() ) {
            throw new InvalidInputException( "Invalid entityType : " + entityType );
        }
        if ( entityId <= 0l ) {
            throw new InvalidInputException( "Invalid Id passed for entityType : " + entityType + ". Id : " + entityId );
        }
        String collectionName = null;
        OrganizationUnitSettings unitSettings = null;
        switch ( entityType ) {
            case CommonConstants.COMPANY_ID_COLUMN:
                unitSettings = getCompanySettings( entityId );
                collectionName = COMPANY_SETTINGS_COLLECTION;
                break;
            case CommonConstants.AGENT_ID_COLUMN:
                unitSettings = userManagementService.getUserSettings( entityId );
                collectionName = AGENT_SETTINGS_COLLECTION;
                break;

            case CommonConstants.BRANCH_ID_COLUMN:
                try {
                    unitSettings = getBranchSettingsDefault( entityId );
                } catch ( NoRecordsFetchedException e ) {
                    throw new InvalidInputException( "No branch setting exists for ID : " + entityId );
                }
                collectionName = BRANCH_SETTINGS_COLLECTION;
                break;

            case CommonConstants.REGION_ID_COLUMN:
                unitSettings = getRegionSettings( entityId );
                collectionName = REGION_SETTINGS_COLLECTION;
                break;

            default:
                throw new InvalidInputException( "Invalid entity type : " + entityType );
        }
        if ( unitSettings == null ) {
            throw new InvalidInputException(
                "No unit setting with the entity type : " + entityType + " ID : " + entityId + " found." );
        }

        //Get the contact details
        ContactDetailsSettings contactDetails = unitSettings.getContact_details();
        if ( contactDetails == null ) {
            throw new InvalidInputException( "Invalid profile name found for userID : " + entityId );
        }

        //Get name
        if ( contactDetails.getName() == null || contactDetails.getName().isEmpty() ) {
            throw new InvalidInputException( "Name cannot be empty. (entityType : " + entityType + " ID : " + entityId );
        }
        String name = contactDetails.getName();
        //Convert to lower case
        name = name.toLowerCase();
        //Replace space with -
        name = name.replace( ' ', '-' );

        //Set profile name
        String newProfileName = name + "-" + entityId;


        //Get existing profileUrl
        String existingProfileUrl = unitSettings.getProfileUrl();
        if ( existingProfileUrl == null || existingProfileUrl.isEmpty() ) {
            throw new InvalidInputException( "Existing profile url cannot be empty" );
        }
        String subUrl = existingProfileUrl.substring( 0, existingProfileUrl.lastIndexOf( '/' ) );
        String newProfileUrl = subUrl + "/" + newProfileName;
        if ( newProfileUrl.equals( existingProfileUrl ) ) {
            LOG.debug( "There is no need to update profile url." );
        } else {
            //Update profileUrl in Mongo
            organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
                KEY_PROFILE_URL, newProfileUrl, unitSettings, collectionName );
            organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
                KEY_PROFILE_NAME, newProfileName, unitSettings, collectionName );

        }
        // update the isActive status to false
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings( KEY_STATUS,
            CommonConstants.STATUS_DELETED_MONGO, unitSettings, collectionName );

        LOG.debug( "Finished updating profile url for deleted entity type : " + entityType + " with ID : " + entityId );
    }


    /*/**
     * Method to fetch regions connected to zillow
     * @param regionIds
     * */
    /*@Override
    @Transactional
    public Set<Long> getRegionsConnectedToZillow( Set<Long> regionIds )
    {
        if ( regionIds == null || regionIds.isEmpty() ) {
            LOG.error( "Region ids passed cannot be null or empty " );
            return null;
        }
        LOG.debug( "Method getRegionsConnectedToZillow called to fetch regions connected to zillow for region ids : "
            + regionIds );
        Set<Long> zillowConnectedRegionIds = new HashSet<Long>();
        LOG.debug( "Fetching region setings for regions ids" );
        List<OrganizationUnitSettings> regionSettings = organizationUnitSettingsDao
            .fetchOrganizationUnitSettingsForMultipleIds( new HashSet<Long>( regionIds ),
                REGION_SETTINGS_COLLECTION );
        LOG.debug( "Fetched region setings for regions ids" );
        if ( regionSettings == null || regionSettings.isEmpty() ) {
            LOG.error( "Region settings could not be found for region ids : " + regionIds );
            return null;
        }
        // Checking whether any region settings has zillow token set
        LOG.debug( "Checking whether any region settings has zillow token set" );
        for ( OrganizationUnitSettings regionSetting : regionSettings ) {
            if ( regionSetting != null && regionSetting.getSocialMediaTokens() != null
                && regionSetting.getSocialMediaTokens().getZillowToken() != null ) {
                zillowConnectedRegionIds.add( regionSetting.getIden() );
            }
        }
        LOG.debug( "Found " + zillowConnectedRegionIds.size() + " regions connected to zillow" );
        LOG.debug( "Method getRegionsConnectedToZillow called to fetch regions connected to zillow for region ids : "
            + regionIds );
        return zillowConnectedRegionIds;
    }*/


    /*/**
     * Method to fetch branches connected to zillow
     * @param regionIds
     * @throws InvalidInputException
     * */
    /* @Override
    @Transactional
    public Set<Long> getBranchesConnectedToZillow( Set<Long> branchIds ) throws InvalidInputException
    {
        if ( branchIds == null || branchIds.isEmpty() ) {
            LOG.error( "Branch ids passed cannot be null or empty in getBranchesUnderRegionsConnectedToZillow()" );
            throw new InvalidInputException(
                "Branch ids passed cannot be null or empty in getBranchesUnderRegionsConnectedToZillow()" );
        }
        LOG.debug( "Method to fetch branch settings for branch ids : " + branchIds
            + ", getBranchesUnderRegionConnectedToZillow called" );
        List<OrganizationUnitSettings> branchSettings = organizationUnitSettingsDao
            .fetchOrganizationUnitSettingsForMultipleIds( branchIds,
                BRANCH_SETTINGS_COLLECTION );
        if ( branchSettings == null || branchSettings.isEmpty() ) {
            LOG.error( "Branch settings could not be found for branch ids : " + branchIds );
            return null;
        }
        LOG.debug( "Fitering branch ids which have zillow social media token" );
        Set<Long> zillowConnectedBranchIds = new HashSet<Long>();
        for ( OrganizationUnitSettings branchSetting : branchSettings ) {
            if ( branchSetting != null && branchSetting.getSocialMediaTokens() != null
                && branchSetting.getSocialMediaTokens().getZillowToken() != null )
                zillowConnectedBranchIds.add( branchSetting.getIden() );
        }
        LOG.debug( "Method to fetch branch settings for branch ids : " + branchIds
            + ", getBranchesUnderRegionConnectedToZillow call ended" );
        return zillowConnectedBranchIds;
    }*/


    /*/**
     * Method to fetch individuals for regions connected to zillow
     * @param regionIds
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     * */
    /*@Override
    @Transactional
    public Set<Long> getIndividualsForRegionsConnectedWithZillow( Set<Long> regionIds ) throws InvalidInputException,
        NoRecordsFetchedException
    {
        if ( regionIds == null || regionIds.isEmpty() )regionId
            throw new InvalidInputException(
                "Region ids passed cannot be null or empty in getIndividualsForRegionsConnectedWithZillow()" );
        List<AgentSettings> agentSettingsList = profileManagementService.getIndividualsByRegionIds( regionIds );
        Set<Long> zillowConnectedIndividualIds = new HashSet<Long>();
        if ( agentSettingsList == null || agentSettingsList.isEmpty() ) {
            LOG.error( "Agents settings could not be found for regions ids : " + regionIds );
            return null;
        }
        for ( AgentSettings agentSettings : agentSettingsList ) {
            if ( agentSettings != null && agentSettings.getSocialMediaTokens() != null
                && agentSettings.getSocialMediaTokens().getZillowToken() != null ) {
                zillowConnectedIndividualIds.add( agentSettings.getIden() );
            }
        }
        return zillowConnectedIndividualIds;
    }*/


    /*/**
     * Method to fetch individuals for company connected to zillow
     * @param regionIds
     * @throws InvalidInputException
     * */
    /*@Override
    @Transactional
    public Set<Long> getIndividualsForCompanyConnectedWithZillow( long companyId ) throws InvalidInputException,
        ProfileNotFoundException, NoRecordsFetchedException
    {
        if ( companyId <= 0l )
            throw new InvalidInputException( "Invalid company id passed in getIndividualsForRegionsConnectedWithZillow()" );
        List<AgentSettings> agentSettingsList = profileManagementService.getIndividualsForCompany( companyId );
        Set<Long> zillowConnectedIndividualIds = new HashSet<Long>();
        if ( agentSettingsList == null || agentSettingsList.isEmpty() ) {
            LOG.error( "Agents settings could not be found for company id : " + companyId );
            return null;
        }
        for ( AgentSettings agentSettings : agentSettingsList ) {
            if ( agentSettings != null && agentSettings.getSocialMediaTokens() != null
                && agentSettings.getSocialMediaTokens().getZillowToken() != null ) {
                zillowConnectedIndividualIds.add( agentSettings.getIden() );
            }
        }
        return zillowConnectedIndividualIds;
    }*/


    /*/**
     * Method to fetch individuals for branches connected to zillow
     * @param regionIds
     * @throws InvalidInputException
     * */
    /*@Override
    @Transactional
    public Set<Long> getIndividualsForBranchesConnectedWithZillow( Set<Long> branchIds ) throws InvalidInputException
    {
        if ( branchIds == null || branchIds.isEmpty() )
            throw new InvalidInputException(
                "Branch ids passed cannot be null or empty in getIndividualsForBranchesConnectedWithZillow()" );
        List<AgentSettings> agentSettingsList = profileManagementService.getIndividualsByBranchIds( branchIds );
        Set<Long> zillowConnectedIndividualIds = new HashSet<Long>();
        if ( agentSettingsList == null || agentSettingsList.isEmpty() ) {
            LOG.error( "Agents settings could not be found for branch ids : " + branchIds );
            return null;
        }
        for ( AgentSettings agentSettings : agentSettingsList ) {
            if ( agentSettings != null && agentSettings.getSocialMediaTokens() != null
                && agentSettings.getSocialMediaTokens().getZillowToken() != null ) {
                zillowConnectedIndividualIds.add( agentSettings.getIden() );
            }
        }
        return zillowConnectedIndividualIds;
    }*/


    /* /**
     * Method to get all the ids of regions, branches and individuals under a company connected to zillow
     * */
    /* @Override
    public Map<String, Set<Long>> getAllIdsUnderCompanyConnectedToZillow( long companyId )
    {
        Map<String, Set<Long>> hierarchyIdsMap = new LinkedHashMap<String, Set<Long>>();
        try {
            if ( companyId <= 0l ) {
                LOG.error( "Invalid company Id passed in getAllIdsUnderCompanyConnectedToZillow" );
                throw new InvalidInputException( "Invalid company Id passed in getAllIdsUnderCompanyConnectedToZillow" );
            }
    
            // Fetch all regions under company
            List<Region> regions = getRegionsForCompany( companyId );
            Set<Long> regionIds = new HashSet<Long>();
    
            if ( regions == null || regions.isEmpty() ) {
                LOG.debug( "Could not find regions for company id: " + companyId );
            } else {
    
                for ( Region region : regions ) {
                    regionIds.add( region.getRegionId() );
                }
    
                // Get Regions connected to zillow
                Set<Long> zillowConnectedRegions = getRegionsConnectedToZillow( regionIds );
    
                if ( zillowConnectedRegions != null && !zillowConnectedRegions.isEmpty() )
                    hierarchyIdsMap.put( CommonConstants.PROFILE_TYPE_REGION, zillowConnectedRegions );
    
                hierarchyIdsMap.putAll( getAllIdsUnderRegionsConnectedToZillow( regionIds ) );
    
                Set<Long> branchIds = new HashSet<Long>();
    
                // Fetch all branches under company
                List<Branch> branches = getBranchesUnderCompany( companyId );
    
                if ( branches == null || branches.isEmpty() ) {
                    LOG.debug( "Could not find branches under company id: " + companyId );
                } else {
                    for ( Branch branch : branches ) {
                        branchIds.add( branch.getBranchId() );
                    }
    
                    // Get all Branches under regions of a company connected to zillow
                    Set<Long> zillowConnectedBranches = getBranchesConnectedToZillow( branchIds );
    
                    if ( zillowConnectedBranches != null && !zillowConnectedBranches.isEmpty() ) {
                        if ( hierarchyIdsMap.get( CommonConstants.PROFILE_TYPE_BRANCH ) != null
                            && !hierarchyIdsMap.get( CommonConstants.PROFILE_TYPE_BRANCH ).isEmpty() ) {
                            Set<Long> existingBranchIds = hierarchyIdsMap.get( CommonConstants.PROFILE_TYPE_BRANCH );
                            zillowConnectedBranches.addAll( existingBranchIds );
                            hierarchyIdsMap.put( CommonConstants.PROFILE_TYPE_BRANCH, zillowConnectedBranches );
                        } else
                            hierarchyIdsMap.put( CommonConstants.PROFILE_TYPE_BRANCH, zillowConnectedBranches );
                    }
                }
    
                // Get all individuals under company
                List<AgentSettings> individualSettingsList = profileManagementService.getIndividualsForCompany( companyId );
                Set<Long> zillowConnectedIndividuals = new HashSet<Long>();
                if ( hierarchyIdsMap.get( CommonConstants.PROFILE_TYPE_INDIVIDUAL ) != null
                    && !hierarchyIdsMap.get( CommonConstants.PROFILE_TYPE_INDIVIDUAL ).isEmpty() )
                    zillowConnectedIndividuals.addAll( hierarchyIdsMap.get( CommonConstants.PROFILE_TYPE_INDIVIDUAL ) );
                if ( individualSettingsList != null && !individualSettingsList.isEmpty() ) {
                    for ( AgentSettings individualSettings : individualSettingsList ) {
                        if ( individualSettings != null && individualSettings.getSocialMediaTokens() != null
                            && individualSettings.getSocialMediaTokens().getZillowToken() != null ) {
                            zillowConnectedIndividuals.add( individualSettings.getIden() );
                        }
                    }
                } else {
                    LOG.debug( "Could not find individuals for company id: " + companyId );
                }
    
                if ( zillowConnectedIndividuals != null && !zillowConnectedIndividuals.isEmpty() ) {
                    hierarchyIdsMap.put( CommonConstants.PROFILE_TYPE_INDIVIDUAL, zillowConnectedIndividuals );
                }
            }
        } catch ( InvalidInputException e ) {
            LOG.error( "Could not fetch unit settings for company id : " + companyId, e );
        } catch ( NoRecordsFetchedException e ) {
            LOG.error( "Could not fetch unit settings for company id : " + companyId, e );
        } catch ( ProfileNotFoundException e ) {
            LOG.error( "Could not fetch unit settings for company id : " + companyId, e );
        }
        return hierarchyIdsMap;
    }*/


    /*
    /**
     * Method to get all the ids of branches and individuals under a region connected to zillow
     * */
    /*@Override
    public Map<String, Set<Long>> getAllIdsUnderRegionsConnectedToZillow( Set<Long> regionIds )
    {
        Map<String, Set<Long>> hierarchyIdsMap = new LinkedHashMap<String, Set<Long>>();
        try {
            // Fetch all branches under regions
            List<Branch> branches = getBranchesByRegionIds( regionIds );
            Set<Long> branchIds = new HashSet<Long>();
            Set<Long> zillowConnectedIndividualIds = new HashSet<Long>();
    
            if ( branches == null || branches.isEmpty() ) {
                LOG.debug( "Could not find branches for region ids: " + regionIds );
            } else {
                for ( Branch branch : branches ) {
                    branchIds.add( branch.getBranchId() );
                }
    
                // Get Branches connected to zillow
                Set<Long> zillowConnectedBranches = getBranchesConnectedToZillow( branchIds );
                if ( zillowConnectedBranches != null && !zillowConnectedBranches.isEmpty() )
                    hierarchyIdsMap.put( CommonConstants.PROFILE_TYPE_BRANCH, zillowConnectedBranches );
    
                // Get individuals under branches of region 
                Set<Long> individualIdsUnderBranch = getIndividualsForBranchesConnectedWithZillow( branchIds );
    
                // get all individuals under regions
                Set<Long> individualIdsUnderRegion = getIndividualsForRegionsConnectedWithZillow( regionIds );
    
                if ( individualIdsUnderRegion != null && !individualIdsUnderRegion.isEmpty() )
                    zillowConnectedIndividualIds.addAll( individualIdsUnderRegion );
                if ( individualIdsUnderBranch != null && !individualIdsUnderBranch.isEmpty() )
                    zillowConnectedIndividualIds.addAll( individualIdsUnderBranch );
                if ( zillowConnectedIndividualIds != null && !zillowConnectedIndividualIds.isEmpty() )
                    hierarchyIdsMap.put( CommonConstants.PROFILE_TYPE_INDIVIDUAL, zillowConnectedIndividualIds );
            }
        } catch ( InvalidInputException e ) {
            LOG.error( "Could not fetch unit settings for region ids : " + regionIds, e );
        } catch ( NoRecordsFetchedException e ) {
            LOG.error( "Could not found unit settings for region ids : " + regionIds, e );
        }
        return hierarchyIdsMap;
    }*/


    /*
    /**
     * Method to get all individual ids under a region connected to zillow
     * */
    /*@Override
    public Map<String, Set<Long>> getAllIdsUnderBranchConnectedToZillow( long branchId )
    {
        Map<String, Set<Long>> hierarchyIdsMap = new LinkedHashMap<String, Set<Long>>();
        try {
            // get all individuals under branch connected with zillow,
            Set<Long> individualIds = getIndividualsForBranchesConnectedWithZillow( new HashSet<Long>(
                Arrays.asList( new Long[] { branchId } ) ) );
            if ( individualIds != null && !individualIds.isEmpty() )
                hierarchyIdsMap.put( CommonConstants.PROFILE_TYPE_INDIVIDUAL, individualIds );
        } catch ( InvalidInputException e ) {
            LOG.error( "Could not fetch individuals under branch id: " + branchId, e );
        }
        return hierarchyIdsMap;
    }*/

    @Override
    @Transactional
    public Set<Long> getAllRegionsUnderCompanyConnectedToZillow( long companyId, int start_index, int batch_size )
        throws InvalidInputException
    {
        if ( companyId <= 0 ) {
            LOG.error( "Invalid companyId passed in getAllRegionsUnderCompanyConnectedToZillow" );
            throw new InvalidInputException( "Invalid companyId passed in getAllRegionsUnderCompanyConnectedToZillow" );
        }

        return zillowHierarchyDao.getRegionIdsUnderCompanyConnectedToZillow( companyId, start_index, batch_size );
    }


    @Override
    @Transactional
    public Set<Long> getAllBranchesUnderProfileTypeConnectedToZillow( String profileType, long iden, int start_index,
        int batch_size ) throws InvalidInputException
    {

        if ( profileType == null || profileType.isEmpty() ) {
            LOG.error( "profile type passed cannot be null or empty in getAllBranchesUnderProfileTypeConnectedToZillow" );
            throw new InvalidInputException(
                "profile type passed cannot be null or empty in getAllBranchesUnderProfileTypeConnectedToZillow" );
        }

        if ( iden <= 0l ) {
            LOG.error( "Invalid id passed in getAllBranchesUnderProfileTypeConnectedToZillow" );
            throw new InvalidInputException( "Invalid id passed in getAllBranchesUnderProfileTypeConnectedToZillow" );
        }

        switch ( profileType ) {
            case CommonConstants.PROFILE_TYPE_COMPANY:
                return zillowHierarchyDao.getBranchIdsUnderCompanyConnectedToZillow( iden, start_index, batch_size );

            case CommonConstants.PROFILE_TYPE_REGION:
                return zillowHierarchyDao.getBranchIdsUnderRegionConnectedToZillow( iden, start_index, batch_size );

            default:
                throw new InvalidInputException(
                    "Invalid profile type passed in getAllBranchesUnderProfileTypeConnectedToZillow" );
        }
    }


    @Override
    @Transactional
    public Set<Long> getAllUsersUnderProfileTypeConnectedToZillow( String profileType, long iden, int start_index,
        int batch_size ) throws InvalidInputException
    {
        if ( profileType == null || profileType.isEmpty() ) {
            LOG.error( "profile type passed cannot be null or empty in getAllUsersUnderProfileTypeConnectedToZillow" );
            throw new InvalidInputException(
                "profile type passed cannot be null or empty in getAllUsersUnderProfileTypeConnectedToZillow" );
        }

        if ( iden <= 0l ) {
            LOG.error( "Invalid id passed in getAllUsersUnderProfileTypeConnectedToZillow" );
            throw new InvalidInputException( "Invalid id passed in getAllUsersUnderProfileTypeConnectedToZillow" );
        }

        switch ( profileType ) {
            case CommonConstants.PROFILE_TYPE_COMPANY:
                return zillowHierarchyDao.getUserIdsUnderCompanyConnectedToZillow( iden, start_index, batch_size );

            case CommonConstants.PROFILE_TYPE_REGION:
                return zillowHierarchyDao.getUserIdsUnderRegionConnectedToZillow( iden, start_index, batch_size );

            case CommonConstants.PROFILE_TYPE_BRANCH:
                return zillowHierarchyDao.getUserIdsUnderBranchConnectedToZillow( iden, start_index, batch_size );

            default:
                throw new InvalidInputException(
                    "Invalid profile type passed in getAllUsersUnderProfileTypeConnectedToZillow" );
        }
    }


    /**
     * Method to get update map for updating regionId for user in solr on moving branch from one region to another
     * @param userList
     * @param newRegionId
     * @return 
     * @throws InvalidInputException
     */
    @SuppressWarnings ( "unchecked")
    Map<Long, List<Long>> updateRegionIdForUsers( SolrDocumentList userList, long newRegionId, long oldRegionId,
        long curBranchId ) throws InvalidInputException
    {
        LOG.debug( "Method to update regions for users in solr started for newRegionId = " + newRegionId + " oldRegionId = "
            + oldRegionId + " current branch Id = " + curBranchId + " started." );
        if ( userList == null ) {
            throw new InvalidInputException( "userList is null" );
        }

        if ( newRegionId <= 0l ) {
            throw new InvalidInputException( "newRegionId = " + newRegionId + " is invalid." );
        }

        if ( oldRegionId <= 0l ) {
            throw new InvalidInputException( "oldRegionId = " + oldRegionId + " is invalid." );
        }

        if ( curBranchId <= 0l ) {
            throw new InvalidInputException( "curBranchId = " + curBranchId + " is invalid." );
        }

        Map<Long, List<Long>> userRegionsMap = new HashMap<Long, List<Long>>();
        //Iterate through each user
        for ( SolrDocument user : userList ) {
            boolean append = false;
            //Get branches for current user
            List<Long> branches = (List<Long>) user.get( CommonConstants.BRANCHES_SOLR );
            /*
             * For every branch in branches except for current branchId, see if they have regionId = oldRegionId
             * If the above check is true, then add the newRegionId to the regions list. Otherwise, replace the oldRegionId
             * with the new one in the regions list
             * If the new region Id is already present, remove/keep oldRegionId based on the above check
             */
            for ( Long branchId : branches ) {
                //Get branch data from id
                Branch branch = userManagementService.getBranchById( branchId );
                if ( branch == null ) {
                    throw new InvalidInputException( "No branch found for branchId : " + branchId );
                }
                if ( branch.getRegion() == null ) {
                    throw new InvalidInputException( "No region found for branchId : " + branchId );
                }
                long currentRegionId = branch.getRegion().getRegionId();
                /*
                 * If the current regionId is the same as the old one and the branchId is different,
                 * then keep the regionId and append the new one started       
                 * else replace the oldRegionId with the new one
                 */
                if ( currentRegionId == oldRegionId && branchId != curBranchId ) {
                    append = true;
                    break;
                }
            }
            //Get list of regions for user
            List<Long> regions = (List<Long>) user.get( CommonConstants.REGIONS_SOLR );
            if ( regions == null ) {
                regions = new ArrayList<Long>();
                append = true;
            }
            if ( regions.contains( newRegionId ) ) {
                if ( append ) {
                    //No changes to be made. Don't store in map. Go to next user.
                    continue;
                } else {
                    //Remove old region id
                    regions.remove( oldRegionId );
                }
            } else {
                if ( append ) {
                    regions.add( newRegionId );
                } else {
                    //Remove old region id
                    regions.remove( oldRegionId );
                    //Add new region Id
                    regions.add( newRegionId );
                }
            }
            //Store details in map for update
            userRegionsMap.put( (Long) user.get( CommonConstants.USER_ID_SOLR ), regions );
        }
        LOG.debug( "Method to update regions for users in solr started for newRegionId = " + newRegionId + " oldRegionId = "
            + oldRegionId + " current branch Id = " + curBranchId + " finished." );
        return userRegionsMap;
    }


    @Override
    @Transactional
    public List<Branch> getAllNonDefaultBranches() throws NoRecordsFetchedException
    {
        LOG.debug( "Getting all non default active branches" );
        Criterion statusCrit = Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        Criterion nonDefaultCrit = Restrictions.eq( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.NO );
        List<Branch> branches = branchDao.findByCriteria( Branch.class, statusCrit, nonDefaultCrit );
        if ( branches == null || branches.isEmpty() ) {
            LOG.warn( "No active branches found." );
            throw new NoRecordsFetchedException( "No active branches found." );
        }
        return branches;
    }


    @Override
    @Transactional
    public List<Region> getAllNonDefaultRegions() throws NoRecordsFetchedException
    {
        LOG.debug( "Getting all non default active regions" );
        Criterion statusCrit = Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        Criterion nonDefaultCrit = Restrictions.eq( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.NO );
        List<Region> regions = regionDao.findByCriteria( Region.class, statusCrit, nonDefaultCrit );
        if ( regions == null || regions.isEmpty() ) {
            LOG.warn( "No active regions found." );
            throw new NoRecordsFetchedException( "No active regions found." );
        }
        return regions;
    }


    @Override
    public List<HierarchySettingsCompare> mismatchBranchHierarchySettings( List<Branch> branches )
    {
        LOG.debug( "Getting mismatched hierarchy settings for branches" );
        List<HierarchySettingsCompare> compareObjects = new ArrayList<>();
        if ( branches != null && !branches.isEmpty() ) {
            BranchSettings branchSettings = null;
            HierarchySettingsCompare compareObject = null;
            for ( Branch branch : branches ) {
                // get the branch settings
                try {
                    branchSettings = getBranchSettings( branch.getBranchId() );
                    BigInteger actualHierachySettings = getHierarchySettings( branchSettings.getOrganizationUnitSettings(),
                        OrganizationUnit.BRANCH );
                    if ( actualHierachySettings != new BigInteger( branch.getSettingsSetStatus() ) ) {
                        compareObject = new HierarchySettingsCompare();
                        compareObject.setId( branch.getBranchId() );
                        compareObject.setCurrentValue( new BigInteger( branch.getSettingsSetStatus() ) );
                        compareObject.setExpectedValue( actualHierachySettings );
                        compareObjects.add( compareObject );
                    }

                } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                    LOG.warn( "Could not find branch settings for " + branch.getBranchId() );
                }
            }
        }
        return compareObjects;
    }


    @Override
    public List<HierarchySettingsCompare> mismatchRegionHierarchySettings( List<Region> regions )
    {
        LOG.debug( "Getting mismatched hierarchy settings for regions" );
        List<HierarchySettingsCompare> compareObjects = new ArrayList<>();
        if ( regions != null && !regions.isEmpty() ) {
            OrganizationUnitSettings regionSettings = null;
            HierarchySettingsCompare compareObject = null;
            for ( Region region : regions ) {
                // get the region settings
                try {
                    regionSettings = getRegionSettings( region.getRegionId() );
                    BigInteger actualHierachySettings = getHierarchySettings( regionSettings, OrganizationUnit.REGION );
                    if ( actualHierachySettings != new BigInteger( region.getSettingsSetStatus() ) ) {
                        compareObject = new HierarchySettingsCompare();
                        compareObject.setId( region.getRegionId() );
                        compareObject.setCurrentValue( new BigInteger( region.getSettingsSetStatus() ) );
                        compareObject.setExpectedValue( actualHierachySettings );
                        compareObjects.add( compareObject );
                    }

                } catch ( InvalidInputException e ) {
                    LOG.warn( "Could not find region settings for " + region.getRegionId() );
                }
            }
        }
        return compareObjects;
    }


    BigInteger getHierarchySettings( OrganizationUnitSettings unitSettings, OrganizationUnit organizationUnit )
        throws InvalidInputException
    {
        LOG.debug( "Getting current hierarchy settings " );
        BigInteger setterValue =  BigInteger.valueOf(0);
        int factor = -1;
        if ( organizationUnit == OrganizationUnit.COMPANY ) {
            factor = 1;
        } else if ( organizationUnit == OrganizationUnit.REGION ) {
            factor = 2;
        } else if ( organizationUnit == OrganizationUnit.BRANCH ) {
            factor = 4;
        } else {
            throw new InvalidInputException( "Invalid organization unit" );
        }
        if ( unitSettings.getLogo() != null ) {
            LOG.debug( "Logo is set" );
            setterValue = setterValue.add( SettingsForApplication.LOGO.getOrder().multiply( BigInteger.valueOf(factor) ) );
        } else {
            LOG.debug( "Logo is not set" );
        }
        if ( unitSettings.getContact_details() != null ) {
            if ( unitSettings.getContact_details().getAddress() != null ) {
                LOG.debug( "Address is set" );
                setterValue = setterValue.add( SettingsForApplication.ADDRESS.getOrder().multiply( BigInteger.valueOf(factor) ) );
            } else {
                LOG.debug( "Address is not set" );
            }
            if ( unitSettings.getContact_details().getContact_numbers() != null ) {
                LOG.debug( "Contact number is set" );
                setterValue = setterValue.add( SettingsForApplication.PHONE.getOrder().multiply( BigInteger.valueOf(factor) ) );

            } else {
                LOG.debug( "Contact number is not set" );
            }
            // skipping location
            if ( unitSettings.getContact_details().getWeb_addresses() != null ) {
                LOG.debug( "Web address is set" );
                setterValue = setterValue.add( SettingsForApplication.WEB_ADDRESS_WORK.getOrder().multiply( BigInteger.valueOf(factor) ) );

            } else {
                LOG.debug( "Web address is not set" );
            }
            if ( unitSettings.getContact_details().getAbout_me() != null ) {
                LOG.debug( "About me is set" );
                setterValue = setterValue.add( SettingsForApplication.ABOUT_ME.getOrder().multiply( BigInteger.valueOf(factor) ) );

            } else {
                LOG.debug( "About me is not set" );
            }
            if ( unitSettings.getContact_details().getMail_ids() != null
                && unitSettings.getContact_details().getMail_ids().getWork() != null ) {
                LOG.debug( "Work email id is set" );
                setterValue = setterValue.add( SettingsForApplication.EMAIL_ID_WORK.getOrder().multiply( BigInteger.valueOf(factor) ) );

            } else {
                LOG.debug( "Work email id is not set" );
            }
        }
        if ( unitSettings.getSocialMediaTokens() != null ) {
            if ( unitSettings.getSocialMediaTokens().getFacebookToken() != null ) {
                LOG.debug( "Facebook is set" );
                setterValue = setterValue.add( SettingsForApplication.FACEBOOK.getOrder().multiply( BigInteger.valueOf(factor) ) );

            } else {
                LOG.debug( "Facebook is not set" );
            }
            if ( unitSettings.getSocialMediaTokens().getTwitterToken() != null ) {
                LOG.debug( "Twitter is set" );
                setterValue = setterValue.add( SettingsForApplication.TWITTER.getOrder().multiply( BigInteger.valueOf(factor) ) );

            } else {
                LOG.debug( "Twitter is not set" );
            }
            if ( unitSettings.getSocialMediaTokens().getLinkedInV2Token() != null ) {
                LOG.debug( "Linkedin is set" );
                setterValue = setterValue.add( SettingsForApplication.LINKED_IN.getOrder().multiply( BigInteger.valueOf(factor) ) );

            } else {
                LOG.debug( "Linkedin is not set" );
            }
            if ( unitSettings.getSocialMediaTokens().getYelpToken() != null ) {
                LOG.debug( "Yelp is set" );
                setterValue = setterValue.add( SettingsForApplication.YELP.getOrder().multiply( BigInteger.valueOf(factor) ) );

            } else {
                LOG.debug( "Yelp is not set" );
            }
            if ( unitSettings.getSocialMediaTokens().getZillowToken() != null ) {
                LOG.debug( "Zillow is set" );
                setterValue = setterValue.add( SettingsForApplication.ZILLOW.getOrder().multiply( BigInteger.valueOf(factor) ) );
            } else {
                LOG.debug( "Zillow is not set" );
            }
            if ( unitSettings.getSocialMediaTokens().getRealtorToken() != null ) {
                LOG.debug( "Realtor is set" );
                setterValue = setterValue.add( SettingsForApplication.REALTOR.getOrder().multiply( BigInteger.valueOf(factor) ) );

            } else {
                LOG.debug( "Realtor is not set" );
            }
            if ( unitSettings.getSocialMediaTokens().getLendingTreeToken() != null ) {
                LOG.debug( "Lending tree is set" );
                setterValue = setterValue.add( SettingsForApplication.LENDING_TREE.getOrder().multiply( BigInteger.valueOf(factor) ) );

            } else {
                LOG.debug( "Lending tree is not set" );
            }
            if ( unitSettings.getSocialMediaTokens().getGoogleBusinessToken() != null ) {
                LOG.debug( "Google business is set" );
                setterValue = setterValue.add( SettingsForApplication.GOOGLE_BUSINESS.getOrder().multiply( BigInteger.valueOf(factor) ) );

            } else {
                LOG.debug( "Google business is not set" );
            }
            
            if ( unitSettings.getSocialMediaTokens().getInstagramToken() != null ) {
                LOG.debug( "Instagram is set" );
                setterValue = setterValue.add( SettingsForApplication.INSTAGRAM.getOrder().multiply( BigInteger.valueOf(factor) ) );

            } else {
                LOG.debug( "Instagram is not set" );
            }
        }
        LOG.debug( "Final Settings setter value : " + setterValue );
        return setterValue;
    }


    boolean validateUserAssignment( User adminUser, User assigneeUser, Map<String, List<User>> userMap )
    {
        LOG.debug( "Method to check user assignment is possible for " + assigneeUser + ", validateUserAssignment called" );

        boolean success = true;
        if ( adminUser == null ) {
            LOG.error( "adminUser passed in argument is empty, hence user assignment not possible" );
            return false;
        }

        if ( assigneeUser == null ) {
            LOG.error( "assigneeUser passed in argument is empty, hence user assignment not possible" );
            return false;
        }
        if ( adminUser.getCompany().getCompanyId() != assigneeUser.getCompany().getCompanyId() ) {
            LOG.error(
                assigneeUser.getEmailId() + " already exist and belongs to a different company than the user in session" );
            success = false;
        }

        if ( !success ) {
            List<User> invalidUserAssignList = userMap.get( CommonConstants.INVALID_USERS_ASSIGN_LIST );
            if ( invalidUserAssignList == null ) {
                invalidUserAssignList = new ArrayList<User>();
            }
            invalidUserAssignList.add( assigneeUser );
            userMap.put( CommonConstants.INVALID_USERS_ASSIGN_LIST, invalidUserAssignList );
        }

        LOG.debug( "Method to check user assignment is possible for " + assigneeUser + ", validateUserAssignment called" );
        return success;

    }


    /**
     * Method to fetch all region ids under a company
     *
     * @param companyId
     * @return
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public List<Long> getRegionIdsUnderCompany( long companyId, int start, int batchSize ) throws InvalidInputException
    {
        if ( companyId <= 0l )
            throw new InvalidInputException( "Invalid company id passed as argument " );
        LOG.debug( "Method getRegionIdsUnderCompany called for companyId:" + companyId );
        List<Long> regionIds = regionDao.getRegionIdsUnderCompany( companyId, start, batchSize );
        LOG.debug( "Method getRegionIdsUnderCompany call ended for companyId:" + companyId );
        return regionIds;
    }


    /**
     * Method to fetch all branch ids under a company
     *
     * @param companyId
     * @return
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public List<Long> getBranchIdsUnderCompany( long companyId, int start, int batchSize ) throws InvalidInputException
    {
        if ( companyId <= 0l )
            throw new InvalidInputException( "Invalid company id passed as argument " );
        LOG.debug( "Method getBranchIdsUnderCompany called for companyId:" + companyId );
        List<Long> branchIds = branchDao.getBranchIdsUnderCompany( companyId, start, batchSize );
        LOG.debug( "Method getBranchIdsUnderCompany call ended for companyId:" + companyId );
        return branchIds;
    }


    /**
     * Method to fetch all agent ids under a company
     *
     * @param companyId
     * @return
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public List<Long> getAgentIdsUnderCompany( long companyId, int start, int batchSize ) throws InvalidInputException
    {
        if ( companyId <= 0l )
            throw new InvalidInputException( "Invalid company id passed as argument " );
        LOG.debug( "Method getAgentIdsUnderCompany called for companyId:" + companyId );
        List<Long> userIds = userDao.getUserIdsUnderCompanyBasedOnProfileMasterId( companyId,
            CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID, start, batchSize );
        LOG.debug( "Method getAgentIdsUnderCompany call ended for companyId:" + companyId );
        return userIds;
    }


    /** Method to fetch unit settings connected to zillow
     * @param collectionName
     * @param ids
     * */
    @Transactional
    @Override
    public List<OrganizationUnitSettings> fetchUnitSettingsConnectedToZillow( String collectionName, List<Long> ids )
    {
        List<OrganizationUnitSettings> unitSettings = organizationUnitSettingsDao
            .fetchUnitSettingsConnectedToZillow( collectionName, ids );
        return unitSettings;
    }


    /** Method to fetch unit settings connected facebook or twitter or linked or google
     * @param collectionName
     * */
    @Transactional
    @Override
    public List<OrganizationUnitSettings> fetchUnitSettingsForSocialMediaTokens( String collectionName )
    {
        if ( StringUtils.isNotEmpty( collectionName ) )
            return organizationUnitSettingsDao.fetchUnitSettingsForSocialMediaTokens( collectionName );
        else
            return null;
    }


    @Override
    public UploadValidation validateUserUploadSheet( String uploadFileName ) throws InvalidInputException
    {
        // get the file and read the file
        if ( uploadFileName == null || uploadFileName.isEmpty() ) {
            LOG.error( "Uploaded file is not present " + uploadFileName );
            throw new InvalidInputException( "Uploaded file is not present " + uploadFileName );
        }
        // read the file

        return null;
    }


    @Override
    public void pushZillowReviews( List<SurveyDetails> surveyDetailsList, String collectionName,
        OrganizationUnitSettings profileSettings, long companyId ) throws InvalidInputException
    {
        zillowUpdateService.pushZillowReviews( surveyDetailsList, collectionName, profileSettings, companyId );
    }


    @Override
    @Transactional
    public void accountDeactivator()
    {
        try {
            // update last start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_ACCOUNT_DEACTIVATOR, CommonConstants.BATCH_NAME_ACCOUNT_DEACTIVATOR );

            List<DisabledAccount> disabledAccounts = disableAccounts( new Date() );
            for ( DisabledAccount account : disabledAccounts ) {
                try {
                    Company company = account.getCompany();
                    try {
                        unsubscribeCompany( company );
                    } catch ( SubscriptionCancellationUnsuccessfulException e ) {
                        LOG.error( "The subscription has already been cancelled for company: {}", company.getCompanyId() );
                    }
                    sendAccountDeletedNotificationMail( account );
                    purgeCompanyDetails( company );
                } catch ( InvalidInputException e ) {
                    LOG.error( "Invalid Input Exception caught while sending email to the company admin. Nested exception is ",
                        e );
                }
            }
            //updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_ACCOUNT_DEACTIVATOR );
            LOG.debug( "Completed AccountDeactivator" );
        } catch ( Exception e ) {
            LOG.error( "Error in AccountDeactivator", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType( CommonConstants.BATCH_TYPE_ACCOUNT_DEACTIVATOR,
                    e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_ACCOUNT_DEACTIVATOR,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in AccountDeactivator " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    @Override
    @Transactional
    public void logEvent( String eventType, String action, String modifiedBy, long companyId, int agentId, int regionId,
        int branchId )
    {
        LOG.debug( "Logging connection event in event table" );
        Event event = new Event();
        event.setAction( action );
        event.setAgentId( agentId );
        event.setBranchId( branchId );
        event.setCompanyId( companyId );
        event.setEventType( eventType );
        event.setModifiedBy( modifiedBy );
        event.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        event.setRegionId( regionId );
        eventDao.save( event );
        LOG.debug( "Logging connection event in event table completes successfully" );
    }


    @Override
    public void updateUserEncryptedIdOfSetting( AgentSettings agentSettings, String userEncryptedId )
    {
        LOG.debug( "Inside method updateUserEncryptedIdOfSetting for userEncryptedId : " + userEncryptedId );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings( KEY_USER_ENCRYPTED_ID,
            userEncryptedId, agentSettings );
    }


    @Override
    @Transactional
    public void deactivatedAccountPurger()
    {
        try {
            // update last start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_DEACTIVATED_ACCOUNT_PURGER, CommonConstants.BATCH_NAME_DEACTIVATED_ACCOUNT_PURGER );

            int maxDaysToPurgeAccount = Integer.parseInt( accountPermDeleteSpan );
            List<DisabledAccount> disabledAccounts = getAccountsForPurge( maxDaysToPurgeAccount );
            for ( DisabledAccount account : disabledAccounts ) {
                Company company = account.getCompany();
                purgeCompanyDetails( company );

            }

            //updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_DEACTIVATED_ACCOUNT_PURGER );
            LOG.debug( "Completed DeactivatedAccountPurger" );
        } catch ( Exception e ) {
            LOG.error( "Error in DeactivatedAccountPurger", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType(
                    CommonConstants.BATCH_TYPE_DEACTIVATED_ACCOUNT_PURGER, e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_DEACTIVATED_ACCOUNT_PURGER,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in DeactivatedAccountPurger " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    /*
     * Method to purge all the details of the company
    */
    @Transactional
    private void purgeCompanyDetails( Company company )
    {
        LOG.debug( "Method to delete all the company details purgeCompany() started." );

        try {
            User user = userManagementService.getAdminUserByCompanyId( company.getCompanyId() );
            deleteCompany( company, user, CommonConstants.STATUS_COMPANY_DELETED );
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException caught in purgeCompany(). Nested exception is ", e );
        } catch ( SolrException e ) {
            LOG.error( "SolrException caught in purgeCompany(). Nested exception is ", e );
        }

        LOG.debug( "Method to delete all the company details purgeCompany() finished." );
    }


    private void sendAccountDeletedNotificationMail( DisabledAccount disabledAccount ) throws InvalidInputException
    {
        // Send email to notify each company admin that the company account will be deactivated after 30 days so that they can take required steps.
        Company company = disabledAccount.getCompany();
        Map<String, String> companyAdmin = new HashMap<String, String>();
        try {
            companyAdmin = solrSearchService.getCompanyAdmin( company.getCompanyId() );
        } catch ( SolrException e1 ) {
            LOG.error(
                "SolrException caught in sendAccountDeletedNotificationMail() while trying to send mail to the company admin ." );
        }
        try {
            if ( companyAdmin != null && companyAdmin.get( "emailId" ) != null )
                emailServices.sendAccountDeletionMail( companyAdmin.get( "emailId" ), companyAdmin.get( "displayName" ),
                    companyAdmin.get( "loginName" ) );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught while sending mail to " + companyAdmin.get( "displayName" ) + " .Nested exception is ",
                e );
        }
    }


    @Override
    public void hierarchySettingsCorrector()
    {
        try {
            // update last start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_HIERARCHY_SETTINGS_CORRECTOR,
                CommonConstants.BATCH_NAME_HIERARCHY_SETTINGS_CORRECTOR );
            // get a list of all the companies and find all the values set
            Set<Company> companyList = getAllCompanies();
            LOG.debug( "Got " + companyList.size() + " companies" );
            for ( Company company : companyList ) {
                OrganizationUnitSettings companySetting = null;
                try {
                    companySetting = getCompanySettings( company.getCompanyId() );
                } catch ( InvalidInputException e1 ) {
                    LOG.error( "Exception caught ", e1 );
                }
                if ( companySetting != null ) {
                    processCompany( companySetting );
                    try {
                        List<Region> regions = company.getRegions();
                        for ( Region region : regions ) {
                            // get region settings
                            OrganizationUnitSettings regionSetting = getRegionSettings( region.getRegionId() );
                            processRegion( regionSetting, region );
                        }

                    } catch ( InvalidInputException e ) {
                        LOG.error( "Could not get regions for company profile " + companySetting.getProfileName(), e );
                    }
                }
            }
            //updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_HIERARCHY_SETTINGS_CORRECTOR );
        } catch ( Exception e ) {
            LOG.error( "Error in HierarchySettingsCorrector", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType(
                    CommonConstants.BATCH_TYPE_HIERARCHY_SETTINGS_CORRECTOR, e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_HIERARCHY_SETTINGS_CORRECTOR,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in HierarchySettingsCorrector " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    private void processRegion( OrganizationUnitSettings regionSetting, Region region )
    {
        LOG.debug( "Processing region " + region.getRegion() );
        BigInteger setterValue = new BigInteger("0");
        LOG.debug( "Getting details of region: " + region.getRegion() );
        if ( regionSetting.getLogo() != null ) {
            LOG.debug( "Logo is set" );
            setterValue.add( SettingsForApplication.LOGO.getOrder().multiply( BigInteger.valueOf( 2 ) ) );
        } else {
            LOG.debug( "Logo is not set" );
        }
        if ( regionSetting.getContact_details() != null ) {
            if ( regionSetting.getContact_details().getAddress() != null ) {
                LOG.debug( "Address is set" );
                setterValue.add( SettingsForApplication.ADDRESS.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Address is not set" );
            }
            if ( regionSetting.getContact_details().getContact_numbers() != null ) {
                LOG.debug( "Contact number is set" );
                setterValue.add( SettingsForApplication.PHONE.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Contact number is not set" );
            }
            // skipping location  check for work too
            if ( regionSetting.getContact_details().getWeb_addresses() != null ) {
                LOG.debug( "Web address is set" );
                setterValue.add( SettingsForApplication.WEB_ADDRESS_WORK.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Web address is not set" );
            }
            if ( regionSetting.getContact_details().getAbout_me() != null ) {
                LOG.debug( "About me is set" );
                setterValue.add( SettingsForApplication.ABOUT_ME.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "About me is not set" );
            }
            if ( regionSetting.getContact_details().getMail_ids() != null
                && regionSetting.getContact_details().getMail_ids().getWork() != null ) {
                LOG.debug( "Work email id is set" );
                setterValue.add( SettingsForApplication.EMAIL_ID_WORK.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Work email id is not set" );
            }
        }
        if ( regionSetting.getSocialMediaTokens() != null ) {
            if ( regionSetting.getSocialMediaTokens().getFacebookToken() != null ) {
                LOG.debug( "Facebook is set" );
                setterValue.add( SettingsForApplication.FACEBOOK.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Facebook is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getTwitterToken() != null ) {
                LOG.debug( "Twitter is set" );
                setterValue.add( SettingsForApplication.TWITTER.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Twitter is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getLinkedInV2Token() != null ) {
                LOG.debug( "Linkedin is set" );
                setterValue.add( SettingsForApplication.LINKED_IN.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Linkedin is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getYelpToken() != null ) {
                LOG.debug( "Yelp is set" );
                setterValue.add( SettingsForApplication.YELP.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Yelp is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getZillowToken() != null ) {
                LOG.debug( "Zillow is set" );
                setterValue.add( SettingsForApplication.ZILLOW.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Zillow is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getRealtorToken() != null ) {
                LOG.debug( "Realtor is set" );
                setterValue.add( SettingsForApplication.REALTOR.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Realtor is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getLendingTreeToken() != null ) {
                LOG.debug( "Lending tree is set" );
                setterValue.add( SettingsForApplication.LENDING_TREE.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Lending tree is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getGoogleBusinessToken() != null ) {
                LOG.debug( "Google business is set" );
                setterValue.add( SettingsForApplication.GOOGLE_BUSINESS.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Google business is not set" );
            }
            
            if ( regionSetting.getSocialMediaTokens().getInstagramToken() != null ) {
                LOG.debug( "Instagram  is set" );
                setterValue.add( SettingsForApplication.INSTAGRAM.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Instagram is not set" );
            }
        }
        LOG.debug( "Final Settings setter value : " + setterValue );
        region.setSettingsSetStatus( String.valueOf( setterValue ).split( "." )[0] );
        // update the values to company
        updateRegion( region );
        // get list of branches for region
        try {
            List<Branch> branches = region.getBranches();
            for ( Branch branch : branches ) {
                try {
                    OrganizationUnitSettings branchSetting = getBranchSettingsDefault( branch.getBranchId() );
                    processBranch( branchSetting, branch);
                } catch ( NoRecordsFetchedException e ) {
                    LOG.error( "Could not get branches setting for " + branch.getBranch(), e );
                }
            }
        } catch ( InvalidInputException e ) {
            LOG.error( "Could not get branches for region " + region.getRegionId(), e );
        }
    }

    private void processOnlyRegion( OrganizationUnitSettings regionSetting, Region region )
    {
        LOG.debug( "Processing region " + region.getRegion() );
        BigInteger setterValue = BigInteger.valueOf(0);
        LOG.debug( "Getting details of region: " + region.getRegion() );
        if ( regionSetting.getLogo() != null ) {
            LOG.debug( "Logo is set" );
            setterValue.add( SettingsForApplication.LOGO.getOrder().multiply( BigInteger.valueOf( 2 ) ) );
        } else {
            LOG.debug( "Logo is not set" );
        }
        if ( regionSetting.getContact_details() != null ) {
            if ( regionSetting.getContact_details().getAddress() != null ) {
                LOG.debug( "Address is set" );
                setterValue.add( SettingsForApplication.ADDRESS.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Address is not set" );
            }
            if ( regionSetting.getContact_details().getContact_numbers() != null ) {
                LOG.debug( "Contact number is set" );
                setterValue.add( SettingsForApplication.PHONE.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Contact number is not set" );
            }
            // skipping location
            if ( regionSetting.getContact_details().getWeb_addresses() != null && regionSetting.getContact_details().getWeb_addresses().getWork() != null ) {
                LOG.debug( "Web address is set" );
                setterValue.add( SettingsForApplication.WEB_ADDRESS_WORK.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Web address is not set" );
            }
            if ( regionSetting.getContact_details().getAbout_me() != null ) {
                LOG.debug( "About me is set" );
                setterValue.add( SettingsForApplication.ABOUT_ME.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "About me is not set" );
            }
            if ( regionSetting.getContact_details().getMail_ids() != null
                && regionSetting.getContact_details().getMail_ids().getWork() != null ) {
                LOG.debug( "Work email id is set" );
                setterValue.add( SettingsForApplication.EMAIL_ID_WORK.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Work email id is not set" );
            }
        }
        if ( regionSetting.getSocialMediaTokens() != null ) {
            if ( regionSetting.getSocialMediaTokens().getFacebookToken() != null ) {
                LOG.debug( "Facebook is set" );
                setterValue.add( SettingsForApplication.FACEBOOK.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Facebook is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getTwitterToken() != null ) {
                LOG.debug( "Twitter is set" );
                setterValue.add( SettingsForApplication.TWITTER.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Twitter is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getLinkedInV2Token() != null ) {
                LOG.debug( "Linkedin is set" );
                setterValue.add( SettingsForApplication.LINKED_IN.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Linkedin is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getYelpToken() != null ) {
                LOG.debug( "Yelp is set" );
                setterValue.add( SettingsForApplication.YELP.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Yelp is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getZillowToken() != null ) {
                LOG.debug( "Zillow is set" );
                setterValue.add( SettingsForApplication.ZILLOW.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Zillow is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getRealtorToken() != null ) {
                LOG.debug( "Realtor is set" );
                setterValue.add( SettingsForApplication.REALTOR.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Realtor is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getLendingTreeToken() != null ) {
                LOG.debug( "Lending tree is set" );
                setterValue.add( SettingsForApplication.LENDING_TREE.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Lending tree is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getGoogleBusinessToken() != null ) {
                LOG.debug( "Google business is set" );
                setterValue.add( SettingsForApplication.GOOGLE_BUSINESS.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Google business is not set" );
            }
            
            if ( regionSetting.getSocialMediaTokens().getInstagramToken() != null ) {
                LOG.debug( "Instagram  is set" );
                setterValue.add( SettingsForApplication.INSTAGRAM.getOrder().multiply( BigInteger.valueOf( 2 ) ) );

            } else {
                LOG.debug( "Instagram  is not set" );
            }
        }
        LOG.debug( "Final Settings setter value : " + setterValue );
        region.setSettingsSetStatus( String.valueOf( setterValue ) );
        // update the values to company
        updateRegion( region );
    }

    private void processBranch( OrganizationUnitSettings branchSetting, Branch branch )
    {
        LOG.debug( "Updating details for branch " + branch.getBranch() );
        BigInteger setterValue = BigInteger.valueOf(0);
        LOG.debug( "Getting details of branch: " + branch.getRegion() );
        if ( branchSetting.getLogo() != null ) {
            LOG.debug( "Logo is set" );
            setterValue.add( SettingsForApplication.LOGO.getOrder().multiply( BigInteger.valueOf( 4 ) ) );
        } else {
            LOG.debug( "Logo is not set" );
        }
        if ( branchSetting.getContact_details() != null ) {
            if ( branchSetting.getContact_details().getAddress() != null ) {
                LOG.debug( "Address is set" );
                setterValue.add( SettingsForApplication.ADDRESS.getOrder().multiply( BigInteger.valueOf( 4 ) ) );

            } else {
                LOG.debug( "Address is not set" );
            }
            if ( branchSetting.getContact_details().getContact_numbers() != null ) {
                LOG.debug( "Contact number is set" );
                setterValue.add( SettingsForApplication.PHONE.getOrder().multiply( BigInteger.valueOf( 4 ) ) );

            } else {
                LOG.debug( "Contact number is not set" );
            }
            // skipping location
            if ( branchSetting.getContact_details().getWeb_addresses() != null && branchSetting.getContact_details().getWeb_addresses().getWork() != null) {
                LOG.debug( "Web address is set" );
                setterValue.add( SettingsForApplication.WEB_ADDRESS_WORK.getOrder().multiply( BigInteger.valueOf( 4 ) ) );

            } else {
                LOG.debug( "Web address is not set" );
            }
            if ( branchSetting.getContact_details().getAbout_me() != null ) {
                LOG.debug( "About me is set" );
                setterValue.add( SettingsForApplication.ABOUT_ME.getOrder().multiply( BigInteger.valueOf( 4 ) ) );

            } else {
                LOG.debug( "About me is not set" );
            }
            if ( branchSetting.getContact_details().getMail_ids() != null
                && branchSetting.getContact_details().getMail_ids().getWork() != null ) {
                LOG.debug( "Work email id is set" );
                setterValue.add( SettingsForApplication.EMAIL_ID_WORK.getOrder().multiply( BigInteger.valueOf( 4 ) ) );

            } else {
                LOG.debug( "Work email id is not set" );
            }
        }
        if ( branchSetting.getSocialMediaTokens() != null ) {
            if ( branchSetting.getSocialMediaTokens().getFacebookToken() != null ) {
                LOG.debug( "Facebook is set" );
                setterValue.add( SettingsForApplication.FACEBOOK.getOrder().multiply( BigInteger.valueOf( 4 ) ) );

            } else {
                LOG.debug( "Facebook is not set" );
            }
            if ( branchSetting.getSocialMediaTokens().getTwitterToken() != null ) {
                LOG.debug( "Twitter is set" );
                setterValue.add( SettingsForApplication.TWITTER.getOrder().multiply( BigInteger.valueOf( 4 ) ) );

            } else {
                LOG.debug( "Twitter is not set" );
            }
            if ( branchSetting.getSocialMediaTokens().getLinkedInV2Token() != null ) {
                LOG.debug( "Linkedin is set" );
                setterValue.add( SettingsForApplication.LINKED_IN.getOrder().multiply( BigInteger.valueOf( 4 ) ) );

            } else {
                LOG.debug( "Linkedin is not set" );
            }
            if ( branchSetting.getSocialMediaTokens().getYelpToken() != null ) {
                LOG.debug( "Yelp is set" );
                setterValue.add( SettingsForApplication.YELP.getOrder().multiply( BigInteger.valueOf( 4 ) ) );

            } else {
                LOG.debug( "Yelp is not set" );
            }
            if ( branchSetting.getSocialMediaTokens().getZillowToken() != null ) {
                LOG.debug( "Zillow is set" );
                setterValue.add( SettingsForApplication.ZILLOW.getOrder().multiply( BigInteger.valueOf( 4 ) ) );

            } else {
                LOG.debug( "Zillow is not set" );
            }
            if ( branchSetting.getSocialMediaTokens().getRealtorToken() != null ) {
                LOG.debug( "Realtor is set" );
                setterValue.add( SettingsForApplication.REALTOR.getOrder().multiply( BigInteger.valueOf( 4 ) ) );

            } else {
                LOG.debug( "Realtor is not set" );
            }
            if ( branchSetting.getSocialMediaTokens().getLendingTreeToken() != null ) {
                LOG.debug( "Lending tree is set" );
                setterValue.add( SettingsForApplication.LENDING_TREE.getOrder().multiply( BigInteger.valueOf( 4 ) ) );

            } else {
                LOG.debug( "Lending tree is not set" );
            }
            if ( branchSetting.getSocialMediaTokens().getGoogleBusinessToken() != null ) {
                LOG.debug( "Google business is set" );
                setterValue.add( SettingsForApplication.GOOGLE_BUSINESS.getOrder().multiply( BigInteger.valueOf( 4 ) ) );

            } else {
                LOG.debug( "Google business is not set" );
            }
            
            if ( branchSetting.getSocialMediaTokens().getInstagramToken() != null ) {
                LOG.debug( "Instagram is set" );
                setterValue.add( SettingsForApplication.INSTAGRAM.getOrder().multiply( BigInteger.valueOf( 4 ) ) );

            } else {
                LOG.debug( "Instagram is not set" );
            }
        }
        LOG.debug( "Final Settings setter value : " + setterValue );
        branch.setSettingsSetStatus( String.valueOf( setterValue ).split( "." )[0] );
        // update the values to company
        updateBranch( branch );
    }


    private void processCompany( OrganizationUnitSettings companySetting )
    {
    		BigInteger setterValue = BigInteger.valueOf(0);
        /* String lockValue = "0";*/
        // get a the company id and get the company from SQL
        LOG.debug( "Getting details of company: " + companySetting.getIden() );
        Company company = getCompanyById( companySetting.getIden() );
        LOG.debug( "Checking for all the values that can be set for " + company.getCompany() );
        if ( companySetting.getLogo() != null ) {
            LOG.debug( "Logo is set" );
            setterValue.add( SettingsForApplication.LOGO.getOrder().multiply( BigInteger.valueOf( 1 ) ) );
            // lock the logo
            /* lockValue = "1";*/
        } else {
            LOG.debug( "Logo is not set" );
        }
        if ( companySetting.getContact_details() != null ) {
            if ( companySetting.getContact_details().getAddress() != null ) {
                LOG.debug( "Address is set" );
                setterValue.add( SettingsForApplication.ADDRESS.getOrder().multiply( BigInteger.valueOf( 1 ) ) );

            } else {
                LOG.debug( "Address is not set" );
            }
            if ( companySetting.getContact_details().getContact_numbers() != null ) {
                LOG.debug( "Contact number is set" );
                setterValue.add( SettingsForApplication.PHONE.getOrder().multiply( BigInteger.valueOf( 1 ) ) );

            } else {
                LOG.debug( "Contact number is not set" );
            }
            // skipping location
            if ( companySetting.getContact_details().getWeb_addresses() != null && companySetting.getContact_details().getWeb_addresses().getWork() != null) {
                LOG.debug( "Web address is set" );
                setterValue.add( SettingsForApplication.WEB_ADDRESS_WORK.getOrder().multiply( BigInteger.valueOf( 1 ) ) );

            } else {
                LOG.debug( "Web address is not set" );
            }
            if ( companySetting.getContact_details().getAbout_me() != null ) {
                LOG.debug( "About me is set" );
                setterValue.add( SettingsForApplication.ABOUT_ME.getOrder().multiply( BigInteger.valueOf( 1 ) ) );

            } else {
                LOG.debug( "About me is not set" );
            }
            if ( companySetting.getContact_details().getMail_ids() != null
                && companySetting.getContact_details().getMail_ids().getWork() != null ) {
                LOG.debug( "Work email id is set" );
                setterValue.add( SettingsForApplication.EMAIL_ID_WORK.getOrder().multiply( BigInteger.valueOf( 1 ) ) );

            } else {
                LOG.debug( "Work email id is not set" );
            }
        }
        if ( companySetting.getSocialMediaTokens() != null ) {
            if ( companySetting.getSocialMediaTokens().getFacebookToken() != null ) {
                LOG.debug( "Facebook is set" );
                setterValue.add( SettingsForApplication.FACEBOOK.getOrder().multiply( BigInteger.valueOf( 1 ) ) );

            } else {
                LOG.debug( "Facebook is not set" );
            }
            if ( companySetting.getSocialMediaTokens().getTwitterToken() != null ) {
                LOG.debug( "Twitter is set" );
                setterValue.add( SettingsForApplication.TWITTER.getOrder().multiply( BigInteger.valueOf( 1 ) ) );

            } else {
                LOG.debug( "Twitter is not set" );
            }
            if ( companySetting.getSocialMediaTokens().getLinkedInV2Token() != null ) {
                LOG.debug( "Linkedin is set" );
                setterValue.add( SettingsForApplication.LINKED_IN.getOrder().multiply( BigInteger.valueOf( 1 ) ) );

            } else {
                LOG.debug( "Linkedin is not set" );
            }
            if ( companySetting.getSocialMediaTokens().getYelpToken() != null ) {
                LOG.debug( "Yelp is set" );
                setterValue.add( SettingsForApplication.YELP.getOrder().multiply( BigInteger.valueOf( 1 ) ) );

            } else {
                LOG.debug( "Yelp is not set" );
            }
            if ( companySetting.getSocialMediaTokens().getZillowToken() != null ) {
                LOG.debug( "Zillow is set" );
                setterValue.add( SettingsForApplication.ZILLOW.getOrder().multiply( BigInteger.valueOf( 1 ) ) );

            } else {
                LOG.debug( "Zillow is not set" );
            }
            if ( companySetting.getSocialMediaTokens().getRealtorToken() != null ) {
                LOG.debug( "Realtor is set" );
                setterValue.add( SettingsForApplication.REALTOR.getOrder().multiply( BigInteger.valueOf( 1 ) ) );

            } else {
                LOG.debug( "Realtor is not set" );
            }
            if ( companySetting.getSocialMediaTokens().getLendingTreeToken() != null ) {
                LOG.debug( "Lending tree is set" );
                setterValue.add( SettingsForApplication.LENDING_TREE.getOrder().multiply( BigInteger.valueOf( 1 ) ) );

            } else {
                LOG.debug( "Lending tree is not set" );
            }
            if ( companySetting.getSocialMediaTokens().getGoogleBusinessToken() != null ) {
                LOG.debug( "Google business is set" );
                setterValue.add( SettingsForApplication.GOOGLE_BUSINESS.getOrder().multiply( BigInteger.valueOf( 1 ) ) );

            } else {
                LOG.debug( "Google business is not set" );
            }
            
            if ( companySetting.getSocialMediaTokens().getInstagramToken() != null ) {
                LOG.debug( "Instagram is set" );
                setterValue.add( SettingsForApplication.INSTAGRAM.getOrder().multiply( BigInteger.valueOf( 1 ) ) );

            } else {
                LOG.debug( "Instagram is not set" );
            }
        }

        LOG.debug( "Final Settings setter value : " + setterValue );
        /*    LOG.debug( "Final Settings locker value : " + lockValue );*/
        company.setSettingsSetStatus( String.valueOf( setterValue ).split( "." )[0] );
        /* company.setSettingsLockStatus( lockValue );*/
        // update the values to company
        updateCompany( company );
    }


    @Override
    public void imageProcessorStarter()
    {
        try {
            // update last start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_IMAGE_PROCESSING_STARTER, CommonConstants.BATCH_NAME_IMAGE_PROCESSING_STARTER );

            Map<Long, String> images = null;
            // get unprocessed company profile images
            images = getUnprocessedProfileImages( CommonConstants.COMPANY_SETTINGS_COLLECTION );
            Map<String, String> processedImgs = null;
            if ( images != null ) {
                for ( long id : images.keySet() ) {
                    try {
                        processedImgs = imageProcessor.processImage( images.get( id ), CommonConstants.IMAGE_TYPE_PROFILE );
                        updateImage( id, processedImgs.get( CommonConstants.SQUARE_THUMBNAIL ),
                            processedImgs.get( CommonConstants.RECTANGULAR_THUMBNAIL ),
                            CommonConstants.COMPANY_SETTINGS_COLLECTION, CommonConstants.IMAGE_TYPE_PROFILE );

                    } catch ( Exception e ) {
                        LOG.error( "Skipping... Could not process image: " + id + " : " + images.get( id ), e );
                        try {
                            //send report bug mail to admin
                            batchTrackerService.sendMailToAdminRegardingBatchError(
                                CommonConstants.BATCH_NAME_IMAGE_PROCESSING_STARTER, System.currentTimeMillis(),
                                new Exception( e.getMessage() + " for profileImage of companyId:" + id, e ) );
                        } catch ( InvalidInputException e1 ) {
                            LOG.error( "Error while updating error message in processing of images " );
                        } catch ( UndeliveredEmailException e1 ) {
                            LOG.error( "Error while sending report excption mail to admin " );
                        }
                        organizationUnitSettingsDao.removeImageForOrganizationUnitSetting( id,
                            CommonConstants.COMPANY_SETTINGS_COLLECTION, false, CommonConstants.IMAGE_TYPE_PROFILE );
                    }
                }

                //ask fb to rescrape pages with new thumbnail
                askFbToRescrapePagesForSettings( images.keySet(), CommonConstants.COMPANY_SETTINGS_COLLECTION );
            }


            // get unprocessed region profile images
            images = getUnprocessedProfileImages( CommonConstants.REGION_SETTINGS_COLLECTION );
            if ( images != null ) {
                for ( long id : images.keySet() ) {
                    try {
                        processedImgs = imageProcessor.processImage( images.get( id ), CommonConstants.IMAGE_TYPE_PROFILE );
                        updateImage( id, processedImgs.get( CommonConstants.SQUARE_THUMBNAIL ),
                            processedImgs.get( CommonConstants.RECTANGULAR_THUMBNAIL ),
                            CommonConstants.REGION_SETTINGS_COLLECTION, CommonConstants.IMAGE_TYPE_PROFILE );
                    } catch ( Exception e ) {
                        LOG.error( "Skipping... Could not process image: " + id + " : " + images.get( id ), e );
                        try {
                            //send report bug mail to admin
                            batchTrackerService.sendMailToAdminRegardingBatchError(
                                CommonConstants.BATCH_NAME_IMAGE_PROCESSING_STARTER, System.currentTimeMillis(),
                                new Exception( e.getMessage() + " for profileImage of regionId:" + id, e ) );
                        } catch ( InvalidInputException e1 ) {
                            LOG.error( "Error while updating error message in processing of images " );
                        } catch ( UndeliveredEmailException e1 ) {
                            LOG.error( "Error while sending report excption mail to admin " );
                        }
                        organizationUnitSettingsDao.removeImageForOrganizationUnitSetting( id,
                            CommonConstants.REGION_SETTINGS_COLLECTION, false, CommonConstants.IMAGE_TYPE_PROFILE );
                    }
                }

                //ask fb to rescrape pages with new thumbnail
                askFbToRescrapePagesForSettings( images.keySet(), CommonConstants.REGION_SETTINGS_COLLECTION );
            }


            // get unprocessed branch profile images
            images = getUnprocessedProfileImages( CommonConstants.BRANCH_SETTINGS_COLLECTION );
            if ( images != null ) {
                for ( long id : images.keySet() ) {
                    try {
                        processedImgs = imageProcessor.processImage( images.get( id ), CommonConstants.IMAGE_TYPE_PROFILE );
                        updateImage( id, processedImgs.get( CommonConstants.SQUARE_THUMBNAIL ),
                            processedImgs.get( CommonConstants.RECTANGULAR_THUMBNAIL ),
                            CommonConstants.BRANCH_SETTINGS_COLLECTION, CommonConstants.IMAGE_TYPE_PROFILE );
                    } catch ( Exception e ) {
                        LOG.error( "Skipping... Could not process image: " + id + " : " + images.get( id ), e );
                        try {
                            //send report bug mail to admin
                            batchTrackerService.sendMailToAdminRegardingBatchError(
                                CommonConstants.BATCH_NAME_IMAGE_PROCESSING_STARTER, System.currentTimeMillis(),
                                new Exception( e.getMessage() + " for profileImage of companyId:" + id,
                                    new Exception( e.getMessage() + " for profileImage of branchId:" + id, e ) ) );
                        } catch ( InvalidInputException e1 ) {
                            LOG.error( "Error while updating error message in processing of images " );
                        } catch ( UndeliveredEmailException e1 ) {
                            LOG.error( "Error while sending report excption mail to admin " );
                        }
                        organizationUnitSettingsDao.removeImageForOrganizationUnitSetting( id,
                            CommonConstants.BRANCH_SETTINGS_COLLECTION, false, CommonConstants.IMAGE_TYPE_PROFILE );
                    }
                }

                //ask fb to rescrape pages with new thumbnail
                askFbToRescrapePagesForSettings( images.keySet(), CommonConstants.BRANCH_SETTINGS_COLLECTION );

            }


            // get unprocessed agent profile images
            images = getUnprocessedProfileImages( CommonConstants.AGENT_SETTINGS_COLLECTION );
            if ( images != null ) {
                for ( long id : images.keySet() ) {
                    try {
                        processedImgs = imageProcessor.processImage( images.get( id ), CommonConstants.IMAGE_TYPE_PROFILE );
                        updateImage( id, processedImgs.get( CommonConstants.SQUARE_THUMBNAIL ),
                            processedImgs.get( CommonConstants.RECTANGULAR_THUMBNAIL ),
                            CommonConstants.AGENT_SETTINGS_COLLECTION, CommonConstants.IMAGE_TYPE_PROFILE );
                    } catch ( Exception e ) {
                        LOG.error( "Skipping... Could not process image: " + id + " : " + images.get( id ), e );
                        try {
                            //send report bug mail to admin
                            batchTrackerService.sendMailToAdminRegardingBatchError(
                                CommonConstants.BATCH_NAME_IMAGE_PROCESSING_STARTER, System.currentTimeMillis(),
                                new Exception( e.getMessage() + " for profileImage of companyId:" + id, e ) );
                        } catch ( InvalidInputException e1 ) {
                            LOG.error( "Error while updating error message in processing of images " );
                        } catch ( UndeliveredEmailException e1 ) {
                            LOG.error( "Error while sending report excption mail to admin " );
                        }
                        organizationUnitSettingsDao.removeImageForOrganizationUnitSetting( id,
                            CommonConstants.AGENT_SETTINGS_COLLECTION, false, CommonConstants.IMAGE_TYPE_PROFILE );
                    }
                }

                //ask fb to rescrape pages with new thumbnail
                askFbToRescrapePagesForSettings( images.keySet(), CommonConstants.AGENT_SETTINGS_COLLECTION );
            }


            // get unprocessed company logo images
            images = getUnprocessedLogoImages( CommonConstants.COMPANY_SETTINGS_COLLECTION );
            if ( images != null ) {
                for ( long id : images.keySet() ) {
                    try {
                        processedImgs = imageProcessor.processImage( images.get( id ), CommonConstants.IMAGE_TYPE_LOGO );
                        updateImage( id, processedImgs.get( CommonConstants.SQUARE_THUMBNAIL ),
                            processedImgs.get( CommonConstants.RECTANGULAR_THUMBNAIL ),
                            CommonConstants.COMPANY_SETTINGS_COLLECTION, CommonConstants.IMAGE_TYPE_LOGO );
                    } catch ( Exception e ) {
                        LOG.error( "Skipping... Could not process image: " + id + " : " + images.get( id ), e );
                        try {
                            //send report bug mail to admin
                            batchTrackerService.sendMailToAdminRegardingBatchError(
                                CommonConstants.BATCH_NAME_IMAGE_PROCESSING_STARTER, System.currentTimeMillis(),
                                new Exception( e.getMessage() + " for logo of companyId:" + id, e ) );
                        } catch ( InvalidInputException e1 ) {
                            LOG.error( "Error while updating error message in processing of images " );
                        } catch ( UndeliveredEmailException e1 ) {
                            LOG.error( "Error while sending report excption mail to admin " );
                        }
                        organizationUnitSettingsDao.removeImageForOrganizationUnitSetting( id,
                            CommonConstants.COMPANY_SETTINGS_COLLECTION, false, CommonConstants.IMAGE_TYPE_LOGO );
                    }
                }
            }
            // get unprocessed region logo images
            images = getUnprocessedLogoImages( CommonConstants.REGION_SETTINGS_COLLECTION );
            if ( images != null ) {
                for ( long id : images.keySet() ) {
                    try {
                        processedImgs = imageProcessor.processImage( images.get( id ), CommonConstants.IMAGE_TYPE_LOGO );
                        updateImage( id, processedImgs.get( CommonConstants.SQUARE_THUMBNAIL ),
                            processedImgs.get( CommonConstants.RECTANGULAR_THUMBNAIL ),
                            CommonConstants.REGION_SETTINGS_COLLECTION, CommonConstants.IMAGE_TYPE_LOGO );
                    } catch ( Exception e ) {
                        LOG.error( "Skipping... Could not process image: " + id + " : " + images.get( id ), e );
                        try {
                            //send report bug mail to admin
                            batchTrackerService.sendMailToAdminRegardingBatchError(
                                CommonConstants.BATCH_NAME_IMAGE_PROCESSING_STARTER, System.currentTimeMillis(),
                                new Exception( e.getMessage() + " for logo of regionId:" + id, e ) );
                        } catch ( InvalidInputException e1 ) {
                            LOG.error( "Error while updating error message in processing of images " );
                        } catch ( UndeliveredEmailException e1 ) {
                            LOG.error( "Error while sending report excption mail to admin " );
                        }
                        organizationUnitSettingsDao.removeImageForOrganizationUnitSetting( id,
                            CommonConstants.REGION_SETTINGS_COLLECTION, false, CommonConstants.IMAGE_TYPE_LOGO );
                    }
                }
            }
            // get unprocessed branch logo images
            images = getUnprocessedLogoImages( CommonConstants.BRANCH_SETTINGS_COLLECTION );
            if ( images != null ) {
                for ( long id : images.keySet() ) {
                    try {
                        processedImgs = imageProcessor.processImage( images.get( id ), CommonConstants.IMAGE_TYPE_LOGO );
                        updateImage( id, processedImgs.get( CommonConstants.SQUARE_THUMBNAIL ),
                            processedImgs.get( CommonConstants.RECTANGULAR_THUMBNAIL ),
                            CommonConstants.BRANCH_SETTINGS_COLLECTION, CommonConstants.IMAGE_TYPE_LOGO );
                    } catch ( Exception e ) {
                        LOG.error( "Skipping... Could not process image: " + id + " : " + images.get( id ), e );
                        try {
                            //send report bug mail to admin
                            batchTrackerService.sendMailToAdminRegardingBatchError(
                                CommonConstants.BATCH_NAME_IMAGE_PROCESSING_STARTER, System.currentTimeMillis(),
                                new Exception( e.getMessage() + " for logo of branchId:" + id, e ) );
                        } catch ( InvalidInputException e1 ) {
                            LOG.error( "Error while updating error message in processing of images " );
                        } catch ( UndeliveredEmailException e1 ) {
                            LOG.error( "Error while sending report excption mail to admin " );
                        }
                        organizationUnitSettingsDao.removeImageForOrganizationUnitSetting( id,
                            CommonConstants.BRANCH_SETTINGS_COLLECTION, false, CommonConstants.IMAGE_TYPE_LOGO );
                    }
                }
            }

            // get unprocessed branch logo images
            images = getUnprocessedLogoImages( CommonConstants.AGENT_SETTINGS_COLLECTION );
            if ( images != null ) {
                for ( long id : images.keySet() ) {
                    try {
                        processedImgs = imageProcessor.processImage( images.get( id ), CommonConstants.IMAGE_TYPE_LOGO );
                        updateImage( id, processedImgs.get( CommonConstants.SQUARE_THUMBNAIL ),
                            processedImgs.get( CommonConstants.RECTANGULAR_THUMBNAIL ),
                            CommonConstants.AGENT_SETTINGS_COLLECTION, CommonConstants.IMAGE_TYPE_LOGO );
                    } catch ( Exception e ) {
                        LOG.error( "Skipping... Could not process image: " + id + " : " + images.get( id ), e );
                        try {
                            //send report bug mail to admin
                            batchTrackerService.sendMailToAdminRegardingBatchError(
                                CommonConstants.BATCH_NAME_IMAGE_PROCESSING_STARTER, System.currentTimeMillis(),
                                new Exception( e.getMessage() + " for logo of agentId:" + id, e ) );
                        } catch ( InvalidInputException e1 ) {
                            LOG.error( "Error while updating error message in processing of images " );
                        } catch ( UndeliveredEmailException e1 ) {
                            LOG.error( "Error while sending report excption mail to admin " );
                        }
                        organizationUnitSettingsDao.removeImageForOrganizationUnitSetting( id,
                            CommonConstants.AGENT_SETTINGS_COLLECTION, false, CommonConstants.IMAGE_TYPE_LOGO );
                    }
                }
            }

            /*try {
            	imageProcessor
            			.processImage(
            					"https://don7n2as2v6aa.cloudfront.net/userprofilepics/P-ae12f4d2e10a5437b18dbc58c55170737b409c7dd5aa3a5121f77757f94d5acd71b277130acdb9a08b3ea8169734c834aaee0c036840e20915ca0873e8d0ae19.png",
            					CommonConstants.IMAGE_TYPE_PROFILE);
            }
            catch (ImageProcessingException | InvalidInputException e) {
            	LOG.error("Could not process image", e);
            }*/

            //updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_IMAGE_PROCESSING_STARTER );
            LOG.debug( "Finished processing of images" );
        } catch ( Exception e ) {
            LOG.error( "Error in processing of images", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType( CommonConstants.BATCH_TYPE_IMAGE_PROCESSING_STARTER,
                    e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_IMAGE_PROCESSING_STARTER,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in processing of images " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    @Transactional
    @Override
    public Company activateCompany( Company company ) throws InvalidInputException
    {
        LOG.debug( "UserManagementService.activateCompany started" );
        //Activate company in SQL
        company.setStatus( CommonConstants.STATUS_ACTIVE );
        company.setIsRegistrationComplete( 1 );
        company.setRegistrationStage( RegistrationStage.COMPLETE.getCode() );

        companyDao.update( company );
        //Activate company in MongoDB
        OrganizationUnitSettings companySettings = getCompanySettings( company.getCompanyId() );
        if ( companySettings == null ) {
            throw new InvalidInputException( "No company settings found for company : " + company.getCompanyId() );
        }
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings( CommonConstants.STATUS_COLUMN,
            CommonConstants.STATUS_ACTIVE_MONGO, companySettings, CommonConstants.COMPANY_SETTINGS_COLLECTION );
        LOG.debug( "UserManagementService.activateCompany finished" );
        return company;
    }


    private Map<Long, String> getUnprocessedProfileImages( String collection )
    {
        LOG.debug( "Getting unprocessed profile images for collection " + collection );
        Map<Long, String> unprocessedProfileImages = null;
        try {
            unprocessedProfileImages = getListOfUnprocessedImages( collection, CommonConstants.IMAGE_TYPE_PROFILE );
            if ( unprocessedProfileImages == null ) {
                LOG.debug( "No unprocessed profile images exist" );
            }
        } catch ( InvalidInputException e ) {
            LOG.error( "The collection name or the image type is invalid. Reason : ", e );
        }
        LOG.debug( "returning unprocessed profile images" );
        return unprocessedProfileImages;
    }


    private Map<Long, String> getUnprocessedLogoImages( String collection )
    {
        LOG.debug( "Getting unprocessed logo images for collection  " + collection );

        Map<Long, String> unprocessedLogoImages = null;
        if ( collection == null || collection.isEmpty() ) {
            LOG.error( "Collection can't be empty" );
        } else {
            try {
                unprocessedLogoImages = getListOfUnprocessedImages( collection, CommonConstants.IMAGE_TYPE_LOGO );
                if ( unprocessedLogoImages == null ) {
                    LOG.debug( "No unprocessed logo images exist" );
                }
            } catch ( InvalidInputException e ) {
                LOG.error( "The collection name or the image type is invalid. Reason : ", e );
            }
            LOG.debug( "returning unprocessed logo images" );
        }
        return unprocessedLogoImages;
    }


    private void updateImage( long iden, String imgThumbnailFileName, String rectangularThumbnailFileName,
        String collectionName, String imageType ) throws InvalidInputException
    {
        LOG.debug( "Method updateImage started" );
        updateImageForOrganizationUnitSetting( iden, null, imgThumbnailFileName, rectangularThumbnailFileName, collectionName,
            imageType, true, true );
        LOG.debug( "Method updateImage finished" );
    }


    @Override
    public List<String> validateSocailMedia( String columnName, long columnValue )
        throws InvalidInputException, NoRecordsFetchedException
    {
        OrganizationUnitSettings settings = null;
        String collection = null;
        List<String> tokenRefreshList = new ArrayList<>();
        if ( columnName.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) ) {
            settings = getCompanySettings( columnValue );
            collection = COMPANY_SETTINGS_COLLECTION;
        } else if ( columnName.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN ) ) {
            settings = getRegionSettings( columnValue );
            collection = REGION_SETTINGS_COLLECTION;
        } else if ( columnName.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN ) ) {
            settings = getBranchSettingsDefault( columnValue );
            collection = BRANCH_SETTINGS_COLLECTION;
        } else if ( columnName.equalsIgnoreCase( CommonConstants.AGENT_ID_COLUMN ) ) {
            settings = userManagementService.getUserSettings( columnValue );
            collection = AGENT_SETTINGS_COLLECTION;
        }

        //facebook token
        if ( settings != null && settings.getSocialMediaTokens() != null
            && settings.getSocialMediaTokens().getFacebookToken() != null ) {
            if ( socialManagementService.checkFacebookTokenExpiry( settings, collection ) ) {
                FacebookToken facebookToken = settings.getSocialMediaTokens().getFacebookToken();
                if ( !facebookToken.isTokenExpiryAlertSent() ) {
                    LOG.debug( "Alert Mail hasn't send to sending alert mail for entity" );
                    String emailId = socialMediaExceptionHandler.generateAndSendSocialMedialTokenExpiryMail( settings,
                        collection, CommonConstants.FACEBOOK_SOCIAL_SITE );
                    //update alert detail in token
                    if ( emailId != null ) {
                        facebookToken.setTokenExpiryAlertEmail( emailId );
                        facebookToken.setTokenExpiryAlertSent( true );
                        facebookToken.setTokenExpiryAlertTime( new Date() );
                        socialManagementService.updateFacebookToken( collection, settings.getIden(), facebookToken );
                    }
                }
            }
            if ( socialManagementService.checkForFacebookTokenRefresh( settings.getSocialMediaTokens() ) )
                tokenRefreshList.add( CommonConstants.FACEBOOK_SOCIAL_SITE );
        }

        //linkedin v2 token
        if ( settings != null && settings.getSocialMediaTokens() != null
            && settings.getSocialMediaTokens().getLinkedInV2Token() != null ) {
            if ( socialManagementService.checkLinkedInV2TokenExpiry( settings, collection ) ) {
                LinkedInToken linkedInToken = settings.getSocialMediaTokens().getLinkedInV2Token();
                if ( !linkedInToken.isTokenExpiryAlertSent() ) {
                    //send alert mail to entity 
                    String emailId = socialMediaExceptionHandler.generateAndSendSocialMedialTokenExpiryMail( settings,
                        collection, CommonConstants.LINKEDIN_SOCIAL_SITE );
                    //update alert mail detail in linkedin token
                    if ( emailId != null ) {
                        linkedInToken.setTokenExpiryAlertEmail( emailId );
                        linkedInToken.setTokenExpiryAlertSent( true );
                        linkedInToken.setTokenExpiryAlertTime( new Date() );
                        socialManagementService.updateLinkedinV2Token( collection, settings.getIden(), linkedInToken );
                    }
                }
            }
            if ( socialManagementService.checkForLinkedInV2TokenRefresh( settings.getSocialMediaTokens() ) )
                tokenRefreshList.add( CommonConstants.LINKEDIN_SOCIAL_SITE );
        } //linkedin token
        else if ( settings != null && settings.getSocialMediaTokens() != null
            && settings.getSocialMediaTokens().getLinkedInToken() != null ) {
            if ( socialManagementService.checkLinkedInTokenExpiry( settings, collection ) ) {
                LinkedInToken linkedInToken = settings.getSocialMediaTokens().getLinkedInToken();
                if ( !linkedInToken.isTokenExpiryAlertSent() ) {
                    //send alert mail to entity 
                    String emailId = socialMediaExceptionHandler.generateAndSendSocialMedialTokenExpiryMail( settings,
                        collection, CommonConstants.LINKEDIN_SOCIAL_SITE );
                    //update alert mail detail in linkedin token
                    if ( emailId != null ) {
                        linkedInToken.setTokenExpiryAlertEmail( emailId );
                        linkedInToken.setTokenExpiryAlertSent( true );
                        linkedInToken.setTokenExpiryAlertTime( new Date() );
                        socialManagementService.updateLinkedinToken( collection, settings.getIden(), linkedInToken );
                    }
                }
            }
            if ( socialManagementService.checkForLinkedInTokenRefresh( settings.getSocialMediaTokens() ) )
                tokenRefreshList.add( CommonConstants.LINKEDIN_SOCIAL_SITE );
        }
        return tokenRefreshList;
    }


    /**
     * 
     */
    @Override
    public List<String> getExpiredSocailMedia( String columnName, long columnValue )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "method getExpiredSocailMedia started" );
        Set<String> socialMedias = new HashSet<>();
        OrganizationUnitSettings settings = null;
        if ( columnName.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) ) {
            settings = getCompanySettings( columnValue );
        } else if ( columnName.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN ) ) {
            settings = getRegionSettings( columnValue );
        } else if ( columnName.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN ) ) {
            settings = getBranchSettingsDefault( columnValue );
        } else if ( columnName.equalsIgnoreCase( CommonConstants.AGENT_ID_COLUMN ) ) {
            settings = userManagementService.getUserSettings( columnValue );
        }

        if ( settings == null || settings.getSocialMediaTokens() == null ) {
            LOG.debug( "method getExpiredSocailMedia ended" );
            return new ArrayList<>( socialMedias );
        }

        //facebook token
        if ( settings.getSocialMediaTokens().getFacebookToken() != null ) {
            if ( settings.getSocialMediaTokens().getFacebookToken().isTokenExpiryAlertSent() ) {
                //check if token is still expired
                socialMedias.add( CommonConstants.FACEBOOK_SOCIAL_SITE );
            }
        }

        //linkedin V2 token
        if ( settings.getSocialMediaTokens().getLinkedInV2Token() != null ) {
            if ( settings.getSocialMediaTokens().getLinkedInV2Token().isTokenExpiryAlertSent() ) {
                socialMedias.add( CommonConstants.LINKEDIN_SOCIAL_SITE );
            }
        } else if ( settings.getSocialMediaTokens().getLinkedInToken() != null ) {
            if ( settings.getSocialMediaTokens().getLinkedInToken().isTokenExpiryAlertSent() ) {
                socialMedias.add( CommonConstants.LINKEDIN_SOCIAL_SITE );
            }
        }
        LOG.debug( "method getExpiredSocailMedia ended" );
        return new ArrayList<>( socialMedias );
    }


    @Override
    @Transactional
    public List<CompanyHiddenNotification> getCompaniesWithHiddenSectionEnabled()
    {
        LOG.debug( "Getting the list of companies whose hidden section is set" );
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        List<CompanyHiddenNotification> hiddenCompanyRecords = companyHiddenNotificationDao
            .findByKeyValue( CompanyHiddenNotification.class, queryMap );
        return hiddenCompanyRecords;
    }


    @Transactional
    public void deleteCompanyHiddenNotificationRecord( CompanyHiddenNotification record )
    {
        LOG.debug( "updating the record for company hidden notification: " + record.getCompanyHiddenNotificationId() );
        record.setStatus( CommonConstants.STATUS_INACTIVE );
        record.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        companyHiddenNotificationDao.saveOrUpdate( record );
        LOG.debug( "updated the company hidden notification record." );
    }


    @Override
    @Transactional
    public List<Company> getCompaniesByBillingModeAuto()
    {
        List<Company> companyList = companyDao.getCompaniesByBillingModeAuto();
        return companyList;
    }


    /**
     * 
     * @param entityIds
     * @param collectionName
     */
    @Override
    public void askFbToRescrapePagesForSettings( Set<Long> entityIds, String collectionName )
    {
        LOG.info( "Method askFbToRescrapePagesForSettings started" );
        List<OrganizationUnitSettings> settingsList = organizationUnitSettingsDao
            .fetchOrganizationUnitSettingsForMultipleIds( entityIds, collectionName );
        for ( OrganizationUnitSettings settings : settingsList ) {
            //ask facebook to rescrape image
            try {
                socialManagementService.askFaceBookToReScrapePage( settings.getCompleteProfileUrl() );
            } catch ( InvalidInputException e ) {
                LOG.error( "Error while asking facebook to rescrape page" );
            }
        }
        LOG.info( "Method askFbToRescrapePagesForSettings finished" );
    }


    @Override
    public void unsubscribeCompany( Company company )
        throws SubscriptionCancellationUnsuccessfulException, InvalidInputException
    {

        LOG.info( "method unsubscribeCompany started" );
        if ( company == null ) {
            throw new InvalidInputException( "Passed parameter company is null" );
        }
        List<LicenseDetail> licenseDetails = company.getLicenseDetails();
        if ( licenseDetails.size() > 0 ) {
            // Unsubscribing company from braintree
            LicenseDetail licenseDetail = licenseDetails.get( 0 );
            if ( licenseDetail.getPaymentMode().equals( CommonConstants.BILLING_MODE_AUTO ) ) {
                LOG.debug( "Unsubscribing company from braintree " );
                payment.unsubscribe( licenseDetail.getSubscriptionId() );
            }
        }
        LOG.info( "method unsubscribeCompany finished" );
    }


    @Override
    public void updateSortCriteriaForCompany( OrganizationUnitSettings companySettings, String sortCriteria )
        throws InvalidInputException
    {
        if ( companySettings == null ) {
            throw new InvalidInputException( "Company settings cannot be null." );
        }

        LOG.debug( "Updating companySettings: " + companySettings + " with sortCriteria: " + sortCriteria );
        //Set isAccountDisabled in mongo
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            KEY_REVIEW_SORT_CRITERIA, sortCriteria, companySettings,
            COMPANY_SETTINGS_COLLECTION );

        LOG.debug( "Updated the isAccountDisabled successfully" );
    }


    //JIRA SS-975
    @Override
    public void updateSendEmailThroughForCompany( OrganizationUnitSettings companySettings, String sendEmailThrough )
        throws InvalidInputException
    {
        if ( companySettings == null ) {
            throw new InvalidInputException( "Company settings cannot be null." );
        }

        LOG.debug( "Updating companySettings: " + companySettings + " with sendEmailThrough: " + sendEmailThrough );
        //Set sendemailthrough in mongo
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            KEY_SEND_EMAIL_THROUGH, sendEmailThrough, companySettings,
            COMPANY_SETTINGS_COLLECTION );

        LOG.debug( "Updated the sendemailthrough successfully" );
    }

    //END JIRA SS-975


    /**
     * 
     * @param companySettings
     * @param isAccountDisabled
     * @param userId
     * @throws NonFatalException
     */
    @Override
    public void processCancelSubscriptionRequest( OrganizationUnitSettings companySettings, boolean isAccountDisabled,
        long userId ) throws NonFatalException
    {

        LOG.info( "processCancelSubscriptionRequest companySettings: " + companySettings + " with AccountDisabled: "
            + isAccountDisabled + " started" );
        try {
            //Set isAccountDisabled in mongo
            organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
                KEY_ACCOUNT_DISABLED, isAccountDisabled, companySettings,
                COMPANY_SETTINGS_COLLECTION );

            LOG.debug( "Updated the isAccountDisabled successfully" );
            if ( isAccountDisabled ) {
                addDisabledAccount( companySettings.getIden(), false, userId );
                //send mail to support admin
                emailServices.sendCancelSubscriptionRequestAlertMail( applicationSupportEmail, applicationSupportName,
                    companySettings.getContact_details().getName() );
            } else {
                inactiveDisabledAccount( companySettings.getIden() );
            }
        } catch ( InvalidInputException | NoRecordsFetchedException | PaymentException e ) {
            LOG.error( "Error while processing cancel subscription for  company " + companySettings.getIden(), e );
            throw new NonFatalException( "Error while processing cancel subscription" );
        }

        LOG.info( "processCancelSubscriptionRequest companySettings: " + companySettings + " with AccountDisabled: "
            + isAccountDisabled + " finished" );
    }


    /**
     * 
     * @param company
     * @throws NonFatalException 
     */
    @Override
    public void processDeactivateCompany( Company company, long userId ) throws NonFatalException
    {

        LOG.info( "processDeactivateCompany company: " + company.getCompanyId() + " started" );
        company.setStatus( CommonConstants.STATUS_INACTIVE );
        try {
            //adding entry to disable account
            DisabledAccount account = addDisabledAccount( company.getCompanyId(), true, userId );
            //updating company in mysql
            updateCompany( company );
            //Update company settings to deleted
            deactivateCompanyInMongo( company );
            //Deactivate company in solr
            deleteCompanyFromSolr( company );
            //unsubscribeCompany
            unsubscribeCompany( company );
            //send mail
            sendAccountDeletedNotificationMail( account );
        } catch ( InvalidInputException | NoRecordsFetchedException | PaymentException e ) {
            LOG.error( "Error while Deativating company " + company.getCompanyId(), e );
            throw new NonFatalException( "Error while processing Deactivate Company" );
        }
        LOG.info( "processDeactivateCompany company: " + company.getCompanyId() + " finished" );
    }


    @Override
    public void updateAllowPartnerSurveyForAllUsers( Set<Long> userIds, boolean allowPartnerSurvey )
        throws InvalidInputException
    {
        if ( userIds == null ) {
            throw new InvalidInputException( "User ids cannot be null." );
        }

        LOG.info( "Updating AllowPartnerSurvey for users with allowPartnerSurvey value : " + allowPartnerSurvey );
        List<Object> userIdList = new ArrayList<Object>();
        userIdList.addAll( userIds );

        organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByInCriteria(
            KEY_ALLOW_PARTNER_SURVEY, allowPartnerSurvey,
            KEY_IDEN, userIdList,
            AGENT_SETTINGS_COLLECTION );

        LOG.info( "Updated the AllowPartnerSurvey for users successfully" );
    }
    
    @Override
    public void updateKeyForAllUsersOfCollection( List<Long> userIds, String field, boolean value, String collectionType)
        throws InvalidInputException
    {
        if ( userIds == null || userIds.isEmpty() ) {
            return;
        }

        LOG.info( "Updating {} for users with value {} ", field, value );
        List<Object> userIdList = new ArrayList<Object>();
        userIdList.addAll( userIds );

        organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByInCriteria(field, value,
            MongoOrganizationUnitSettingDaoImpl.KEY_IDEN, userIdList,
            collectionType );

        LOG.info( "Updated {} for users with value {} ", field, value );
    }

    @Override
    public void updatellowPartnerSurveyForUser( AgentSettings agentSettings, boolean allowPartnerSurvey )
    {
        LOG.debug( "Inside method updatellowPartnerSurveyForUser for user : " + agentSettings.getIden() );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings(
            KEY_ALLOW_PARTNER_SURVEY, allowPartnerSurvey, agentSettings );
    }


    @Override
    public boolean isPartnerSurveyAllowedForComapny( long companyId )
    {
        LOG.debug( "Inside method isPartnerSurveyAllowedForComapny for company : " + companyId );
        OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( companyId,
            COMPANY_SETTINGS_COLLECTION );
        if ( companySettings != null ) {
            if ( companySettings.isAllowPartnerSurvey() ) {
                    return true;
            }
        }
        return false;
    }


    public void updateSurveyAssignments( User user, List<UserProfile> userProfileList, long oldUserProfileId )
    {
        LOG.debug( "Method updateBranchIdRegionIdForAllSurveysOfAgent() started for agentId + " + user.getUserId() );
        for ( UserProfile profile : userProfileList ) {
            if ( profile.getUserProfileId() != oldUserProfileId ) {
                if ( profile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                    surveyDetailsDao.updateBranchIdRegionIdForAllSurveysOfAgent( user.getUserId(), profile.getBranchId(),
                        profile.getRegionId() );
                    break;
                }
            }
        }
        LOG.debug( "Method updateBranchIdRegionIdForAllSurveysOfAgent() finished for agentId + " + user.getUserId() );
    }


    @Override
    public List<User> getUsersUnderBranch( Branch branch ) throws InvalidInputException
    {
        LOG.info( "getUsersUnderBranch started" );
        List<User> usersUnderBranch = new ArrayList<>();
        for ( long userId : userProfileDao.findUserIdsByBranch( branch.getBranchId() ) ) {
            usersUnderBranch.add( userManagementService.getUserByUserId( userId ) );
        }
        LOG.info( "getUsersUnderBranch finished" );
        return usersUnderBranch;
    }


    /* updating MySQL tables with company specific data 
     */
    @Override
    @Transactional
    public void updateCompanyIdInMySQLForUser( User userToBeRelocated, Company targetCompany ) throws InvalidInputException
    {
        LOG.info( "Method updateCompanyIdInMySQLForUser started" );

        if ( !userToBeRelocated.isCompanyAdmin() ) {
            //update user details in MySQL database
            userToBeRelocated.setCompany( targetCompany );
            userToBeRelocated.setModifiedOn( new Timestamp(new Date().getTime() ));
            userManagementService.updateUser( userToBeRelocated );

            //update UserEmailMapping table with CompanyId in MySQL database
            try {
                for ( UserEmailMapping userEmailMapping : userManagementService
                    .getUserEmailMappingsForUser( userToBeRelocated.getUserId() ) ) {
                		userEmailMapping.setModifiedOn( new Timestamp(new Date().getTime() ));
                		userEmailMapping.setCompany( targetCompany );
                    userManagementService.updateUserEmailMapping( userEmailMapping );
                }
            } catch ( NoRecordsFetchedException noRecordsFetchedException ) {
                LOG.warn( "No user email mappings found, proceeding to update other MySQL entities " );
            }


            //updating removed user table with CompanyId
            for ( RemovedUser removedUser : removedUserDao.findByColumn( RemovedUser.class, CommonConstants.USER_COLUMN,
                userToBeRelocated ) ) {
                removedUser.setCompany( targetCompany );
                removedUserDao.update( removedUser );
            }
        }
        LOG.info( "Method updateCompanyIdInMySQLForUser finished" );
    }


    @Override
    public List<EncompassSdkVersion> getActiveEncompassSdkVersions()
    {
        LOG.info( "Method getActiveEncompassSdkVersions started" );
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        List<EncompassSdkVersion> encompassSdkVersions = encompassSdkVersionDao.findByKeyValue( EncompassSdkVersion.class,
            queries );
        LOG.info( "Method getActiveEncompassSdkVersions finisshed" );
        return encompassSdkVersions;

    }


    @Override
    public String getEncompassHostByVersion( String sdkVersion ) throws InvalidInputException
    {
        LOG.info( "Method getActiveEncompassSdkVersions started" );
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        queries.put( CommonConstants.ENCOMPASS_SDK_VERSION_COLUMN, sdkVersion );
        List<EncompassSdkVersion> encompassSdkVersions = encompassSdkVersionDao.findByKeyValue( EncompassSdkVersion.class,
            queries );
        if ( encompassSdkVersions == null || encompassSdkVersions.size() == 0 ) {
            throw new InvalidInputException( "Unsupported Encompass Version" );
        }
        LOG.info( "Method getActiveEncompassSdkVersions finisshed" );
        return encompassSdkVersions.get( CommonConstants.INITIAL_INDEX ).getHostName();

    }


    @Override
    @Transactional
    public List<CompanyView> getAllActiveEnterpriseCompanyViews()
    {
        List<Company> allCompanies = getAllActiveEnterpriseCompanies();
        List<CompanyView> allCompanyViews = new ArrayList<>();
        for ( Company company : allCompanies ) {
            CompanyView companyView = new CompanyView();
            companyView.setCompanyId( company.getCompanyId() );
            companyView.setCompany( company.getCompany() );
            allCompanyViews.add( companyView );
        }
        return allCompanyViews;

    }


    @Override
    @Transactional
    public List<Company> getAllActiveEnterpriseCompanies()
    {
        LOG.info( "Method getAllActiveCompaniesByAccountType started" );
        List<Company> companies = companyDao.getCompaniesByStatusAndAccountMasterId( CommonConstants.STATUS_ACTIVE,
            CommonConstants.ACCOUNTS_MASTER_ENTERPRISE );
        LOG.info( "Method getAllActiveEnterpriseCompanies finisshed" );
        return companies;

    }


    @Override
    @Transactional
    public List<Company> getCompaniesByCompanyIds( Set<Long> companyIds )
    {
        LOG.info( "Method getCompaniesByCompanyIds started" );
        List<Company> companies = companyDao.getCompanyListByIds( companyIds );
        LOG.info( "Method getCompaniesByCompanyIds finisshed" );
        return companies;

    }


    @Override
    @Transactional
    public Map<Long, Long> getUsersCountForCompanies()
    {
        LOG.info( "Method getUsersCountForCompanies started" );
        Map<Long, Long> companiesUserCount = userDao.getUsersCountForCompanies();
        LOG.info( "Method getUsersCountForCompanies finisshed" );
        return companiesUserCount;
    }


    @Override
    @Transactional
    public List<Long> getHiddenPublicPageCompanyIds()
    {
        return organizationUnitSettingsDao
            .getHiddenPublicPagesEntityIds( COMPANY_SETTINGS_COLLECTION );
    }


    @Override
    @Transactional
    public List<Long> getHiddenPublicPageRegionIds()
    {
        return organizationUnitSettingsDao
            .getHiddenPublicPagesEntityIds( REGION_SETTINGS_COLLECTION );
    }


    @Override
    @Transactional
    public List<Long> getHiddenPublicPageBranchIds()
    {
        return organizationUnitSettingsDao
            .getHiddenPublicPagesEntityIds( BRANCH_SETTINGS_COLLECTION );
    }


    @Override
    @Transactional
    public List<Long> getHiddenPublicPageUserIds()
    {
        return organizationUnitSettingsDao
            .getHiddenPublicPagesEntityIds( AGENT_SETTINGS_COLLECTION );
    }


    @Transactional
    @Override
    public List<OrganizationUnitSettings> getCompaniesForTransactionMonitor()
    {
        List<Company> companies = getAllActiveEnterpriseCompanies();
        List<Long> companyIds = new ArrayList<Long>();
        for ( Company company : companies ) {
            companyIds.add( company.getCompanyId() );
        }
        return organizationUnitSettingsDao.getCompaniesForTransactionMonitor( companyIds );
    }


    @Override
    public void updateTransactionMonitorSettingForCompany( long companyId, boolean includeForTransactionMonitor )
    {
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden(
            KEY_INCLUDE_FOR_TRANSACTION_MONITOR, includeForTransactionMonitor, companyId,
            COMPANY_SETTINGS_COLLECTION );
    }


    /**
     * 
     * @param companySettings
     * @param regionSettings
     * @param branchSetting
     * @param unitSettings
     * @return
     */
    @Override
    public String getFacebookPixelImageTagsFromHierarchy( OrganizationUnitSettings companySettings,
        OrganizationUnitSettings regionSettings, OrganizationUnitSettings branchSetting, OrganizationUnitSettings unitSettings )
    {

        LOG.debug( "method  getFacebookPixelImageTagsFromHierarchy started" );
        String facebookPixelTag = "";

        if ( companySettings != null && companySettings.getSocialMediaTokens() != null
            && companySettings.getSocialMediaTokens().getFacebookPixelToken() != null )
            if ( !StringUtils.isEmpty( companySettings.getSocialMediaTokens().getFacebookPixelToken().getPixelImgTag() ) )
                facebookPixelTag += companySettings.getSocialMediaTokens().getFacebookPixelToken().getPixelImgTag();

        if ( regionSettings != null && regionSettings.getSocialMediaTokens() != null
            && regionSettings.getSocialMediaTokens().getFacebookPixelToken() != null )
            if ( !StringUtils.isEmpty( regionSettings.getSocialMediaTokens().getFacebookPixelToken().getPixelImgTag() ) )
                facebookPixelTag += regionSettings.getSocialMediaTokens().getFacebookPixelToken().getPixelImgTag();

        if ( branchSetting != null && branchSetting.getSocialMediaTokens() != null
            && branchSetting.getSocialMediaTokens().getFacebookPixelToken() != null )
            if ( !StringUtils.isEmpty( branchSetting.getSocialMediaTokens().getFacebookPixelToken().getPixelImgTag() ) )
                facebookPixelTag += branchSetting.getSocialMediaTokens().getFacebookPixelToken().getPixelImgTag();

        if ( unitSettings != null && unitSettings.getSocialMediaTokens() != null
            && unitSettings.getSocialMediaTokens().getFacebookPixelToken() != null )
            if ( !StringUtils.isEmpty( unitSettings.getSocialMediaTokens().getFacebookPixelToken().getPixelImgTag() ) )
                facebookPixelTag += unitSettings.getSocialMediaTokens().getFacebookPixelToken().getPixelImgTag();

        LOG.debug( "the fenerated facebook pixel tag is : {}", facebookPixelTag );
        LOG.debug( "method  getFacebookPixelImageTagsFromHierarchy finished" );
        return facebookPixelTag;
    }


    @Override
    public boolean updateDigestRecipients( String entityType, long entityId, Set<String> emails )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "method updateDigestRecipients() started" );
        if ( StringUtils.isEmpty( entityType ) ) {
            LOG.warn( "Entity type is not specified" );
            throw new InvalidInputException( "Entity type is not specified" );
        } else if ( entityId <= 0 ) {
            LOG.warn( "Entity Id is invalid" );
            throw new InvalidInputException( "Entity Id is invalid" );
        }

        OrganizationUnitSettings unitSettings = null;
        String collectionType = "";

        if ( CommonConstants.COMPANY_ID_COLUMN.equals( entityType ) ) {
            unitSettings = getCompanySettings( entityId );
            collectionType = CommonConstants.COMPANY_SETTINGS_COLLECTION;
        } else if ( CommonConstants.REGION_ID_COLUMN.equals( entityType ) ) {
            unitSettings = getRegionSettings( entityId );
            collectionType = CommonConstants.REGION_SETTINGS_COLLECTION;
        } else if ( CommonConstants.BRANCH_ID_COLUMN.equals( entityType ) ) {
            unitSettings = getBranchSettingsDefault( entityId );
            collectionType = CommonConstants.BRANCH_SETTINGS_COLLECTION;
        } else if ( CommonConstants.AGENT_ID_COLUMN.equals( entityType ) ) {
            unitSettings = getAgentSettings( entityId );
            collectionType = CommonConstants.AGENT_SETTINGS_COLLECTION;
        } else {
            LOG.warn( "Entity Type is invalid" );
            throw new InvalidInputException( "Entity type is invalid" );
        }

        if ( unitSettings == null ) {
            LOG.warn( "settings are not specified" );
            throw new InvalidInputException( "settings cannot be null." );
        }

        LOG.trace( "Updating unitSettings: {} with digest recipients: {}", unitSettings, emails );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            KEY_DIGEST_RECIPIENTS, emails, unitSettings, collectionType );
        LOG.debug( "Updated the record successfully" );

        return true;
    }


    @Override
    public Set<String> parseEmailsList( String emailsStr )
    {
        LOG.debug( "method parseEmailsList() running" );
        Set<String> emailList = null;

        if ( emailsStr == null ) {
            return emailList;
        }

        emailList = new HashSet<>();

        for ( String email : StringUtils.deleteWhitespace( emailsStr ).split( "," ) ) {
            if ( !StringUtils.isEmpty( email ) ) {
                emailList.add( email );
            }
        }

        return emailList;
    }


    /**
     * 
     * @param alertType
     * @return
     */
    @Override
    public List<OrganizationUnitSettings> getCompaniesByAlertType( String alertType )
    {
        LOG.debug( "method getCompaniesByAlertType started for alertType {}", alertType );
        List<Company> companies = getAllActiveEnterpriseCompanies();
        List<Long> companyIds = new ArrayList<Long>();

        for ( Company company : companies )
            companyIds.add( company.getCompanyId() );

        LOG.debug( "method getCompaniesByAlertType finished for alertType {}", alertType );
        return organizationUnitSettingsDao.fetchCompaniesByAlertType( alertType, companyIds );
    }


    @Override
    public String getCollectionFromProfileLevel( String profileLevel ) throws InvalidInputException
    {
        LOG.debug( "method getCollectionFromProfileLevel started" );
        if ( CommonConstants.PROFILE_LEVEL_COMPANY.equals( profileLevel ) ) {
            return COMPANY_SETTINGS_COLLECTION;
        } else if ( CommonConstants.PROFILE_LEVEL_REGION.equals( profileLevel ) ) {
            return REGION_SETTINGS_COLLECTION;
        } else if ( CommonConstants.PROFILE_LEVEL_BRANCH.equals( profileLevel ) ) {
            return BRANCH_SETTINGS_COLLECTION;
        } else if ( CommonConstants.PROFILE_LEVEL_INDIVIDUAL.equals( profileLevel ) ) {
            return AGENT_SETTINGS_COLLECTION;
        } else {
            LOG.warn( "Invalid profile level" );
            throw new InvalidInputException( "Invalid profile level" );
        }
    }


    @Override
    public Set<String> getAdminEmailsSpecificForAHierarchy( String profileLevel, long iden ) throws InvalidInputException
    {
        LOG.debug( "method getAdminEmaillsForAhierarchy started" );
        List<UserProfile> adminList = new ArrayList<>();
        Set<String> regionAdminEmailList = null;
        Set<String> emailSet = null;

        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );

        if ( CommonConstants.PROFILE_LEVEL_COMPANY.equals( profileLevel ) ) {
            queries.put( CommonConstants.COMPANY_COLUMN, getCompanyById( iden ) );
            queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
                userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID ) );
        } else if ( CommonConstants.PROFILE_LEVEL_REGION.equals( profileLevel ) ) {
            queries.put( CommonConstants.REGION_ID_COLUMN, iden );
            queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
                userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) );
        } else if ( CommonConstants.PROFILE_LEVEL_BRANCH.equals( profileLevel ) ) {
            queries.put( CommonConstants.BRANCH_ID_COLUMN, iden );
            queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
                userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) );

            // get region specific administrator email list
            regionAdminEmailList = getAdminEmailsSpecificForAHierarchy( CommonConstants.PROFILE_LEVEL_REGION,
                userManagementService.getBranchById( iden ).getRegion().getRegionId() );

        } else if ( CommonConstants.PROFILE_LEVEL_INDIVIDUAL.equals( profileLevel ) ) {
            queries.put( CommonConstants.USER_COLUMN, userManagementService.getUserByUserId( iden ) );
            queries.put( CommonConstants.PROFILE_MASTER_COLUMN,
                userManagementService.getProfilesMasterById( CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) );
        } else {
            LOG.warn( "Invalid profile level" );
            throw new InvalidInputException( "Invalid profile level" );
        }

        adminList.addAll( userProfileDao.findByKeyValue( UserProfile.class, queries ) );

        if ( !adminList.isEmpty() ) {
            emailSet = new HashSet<>();
            for ( UserProfile admin : adminList ) {
                User user = admin.getUser();
                if ( CommonConstants.PROFILE_LEVEL_COMPANY.equals( profileLevel )
                    || CommonConstants.PROFILE_LEVEL_INDIVIDUAL.equals( profileLevel ) ) {
                    emailSet.add( user.getEmailId() );
                } else if ( CommonConstants.PROFILE_LEVEL_REGION.equals( profileLevel )
                    && user.getIsOwner() != CommonConstants.STATUS_ACTIVE ) {
                    emailSet.add( user.getEmailId() );
                } else if ( CommonConstants.PROFILE_LEVEL_BRANCH.equals( profileLevel )
                    && user.getIsOwner() != CommonConstants.STATUS_ACTIVE
                    && !regionAdminEmailList.contains( user.getEmailId() ) ) {
                    emailSet.add( user.getEmailId() );
                }
            }
        } else {
            emailSet = Collections.emptySet();
        }

        LOG.debug( "method getAdminEmaillsForAhierarchy finished" );
        return emailSet;
    }


    @Override
    public List<Long> filterCompanyIdsByStatus( List<Long> companies, String status ) throws InvalidInputException
    {
        LOG.info( "method filterCompanyIdsByStatus() started" );
        if ( companies == null || companies.isEmpty() ) {
            LOG.warn( "company list is non-existent" );
            return companies;
        } else if ( StringUtils.isEmpty( status ) ) {
            LOG.warn( "filter parameter status is not present" );
            return companies;
        } else if ( status.equals( CommonConstants.STATUS_ACTIVE_MONGO ) ) {
            return companyDao.filterIdsByStatus( companies, Arrays.asList( 1 ) );
        } else if ( status.equals( CommonConstants.STATUS_DELETED_MONGO ) ) {
            return companyDao.filterIdsByStatus( companies, Arrays.asList( 11 ) );
        } else {
            LOG.warn( "Invalid filter for fetching encompass" );
            throw new InvalidInputException( "Invalid filter for fetching encompass" );
        }
    }


    @Override
    public boolean decryptEncompassPasswordIfPossible( OrganizationUnitSettings unitSettings )
    {
        LOG.info( "Deciphering encompass password" );
        if ( unitSettings != null && unitSettings.getCrm_info() != null
            && unitSettings.getCrm_info().getCrm_source().equalsIgnoreCase( CommonConstants.CRM_SOURCE_ENCOMPASS ) ) {
            EncompassCrmInfo crmInfo = (EncompassCrmInfo) unitSettings.getCrm_info();
            if ( StringUtils.isNotEmpty( crmInfo.getCrm_password() ) ) {
                try {
                    // deciphering the encompass password
                    crmInfo.setCrm_password( encryptionHelper.decryptAES( crmInfo.getCrm_password(), "" ) );
                    LOG.debug( "Successfully decrypted encompass password for company with ID: {}", unitSettings.getIden() );
                } catch ( InvalidInputException error ) {
                    LOG.warn(
                        "decryptEncompassPasswordIfPossible(): encompass password is not encrypted for company with ID: {}",
                        unitSettings.getIden() );
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void updateAbusiveMailService( long companyId, String mailId ) throws NonFatalException
    {

        LOG.info( "Update abusive mail" );
        OrganizationUnitSettings unitSettings = null;
        AbusiveMailSettings originalAbusiveMailSettings = new AbusiveMailSettings();
        mailId = checkValidMail( mailId );

        unitSettings = getCompanySettings( companyId );

        if ( unitSettings == null )
            throw new NonFatalException( "Company settings cannot be found for id : " + companyId );
        // if surveySettings doesnt exist add it
        if ( unitSettings.getSurvey_settings() == null ) {
            addSurveySettings( unitSettings );
        }

        if ( unitSettings.getSurvey_settings().getAbusive_mail_settings() != null )
            originalAbusiveMailSettings = unitSettings.getSurvey_settings().getAbusive_mail_settings();

        originalAbusiveMailSettings.setMailId( mailId );
        unitSettings.getSurvey_settings().setAbusive_mail_settings( originalAbusiveMailSettings );
        updateSurveySettings( unitSettings, unitSettings.getSurvey_settings() );

    }


    @Override
    public void unsetAbusiveMailService( long companyId ) throws NonFatalException
    {

        LOG.info( "Unset abusive mail" );
        OrganizationUnitSettings unitSettings = null;

        unitSettings = getCompanySettings( companyId );

        if ( unitSettings == null )
            throw new NonFatalException( "Company settings cannot be found for id : " + companyId );
        // if surveySettings doesnt exist add it
        if ( unitSettings.getSurvey_settings().getAbusive_mail_settings() != null ) {
            unsetKey( unitSettings, KEY_ABUSIVE_EMAIL_SETTING,
                CommonConstants.COMPANY_SETTINGS_COLLECTION );
        }

    }


    @Override
    public void unsetComplaintResService( long companyId ) throws NonFatalException
    {

        LOG.info( "Unset Complaint Resolution mail" );
        OrganizationUnitSettings unitSettings = null;

        unitSettings = getCompanySettings( companyId );

        if ( unitSettings == null )
            throw new NonFatalException( "Company settings cannot be found for id : " + companyId );
        // if surveySettings doesnt exist add it
        if ( unitSettings.getSurvey_settings().getComplaint_res_settings() != null ) {
            unsetKey( unitSettings, KEY_COMPLAINT_RESOLUTION_SETTING,
                CommonConstants.COMPANY_SETTINGS_COLLECTION );
        }

    }
    
    @Override
    public void unsetWebAddressInProfile( long entityId ,String entityType) throws NonFatalException
    {

        LOG.info( "Unset web address mail" );
        OrganizationUnitSettings unitSettings = null;
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            unitSettings = getCompanySettings( entityId );
            if ( unitSettings == null ) {
                throw new InvalidInputException( "No company settings found in current session" );
            }
            unsetWebAddMongo(unitSettings,COMPANY_SETTINGS_COLLECTION);
            //once the settings are unset we have to change the SETTINGS_SET_STATUS for each hierarchy 
            //so that getPrimaryHierarchyByEntity knows which is the closest heierarchy it should pick from 
            processCompany( getCompanySettings( entityId ));

        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            unitSettings = getRegionSettings( entityId );
            if ( unitSettings == null ) {
                throw new InvalidInputException( "No Region settings found in current session" );
            }
            unsetWebAddMongo(getRegionSettings( entityId ),REGION_SETTINGS_COLLECTION);
            Region region = regionDao.findById( Region.class, entityId );
            processOnlyRegion( getRegionSettings( entityId ), region);
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
           unitSettings = getBranchSettingsDefault( entityId );
            if ( unitSettings == null ) {
                throw new InvalidInputException( "No Branch settings found in current session" );
            }
            unsetWebAddMongo(unitSettings,BRANCH_SETTINGS_COLLECTION);
            Branch branch = branchDao.findById( Branch.class, entityId );
            //get back the unit settings
            processBranch( getBranchSettingsDefault( entityId ), branch );
        } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
            unitSettings = userManagementService.getUserSettings( entityId );
            if ( unitSettings == null ) {
                throw new InvalidInputException( "No Agent settings found in current session" );
            }
            unsetWebAddMongo(unitSettings,AGENT_SETTINGS_COLLECTION);
        } else {
            throw new InvalidInputException( "Invalid input exception occurred while updating web addresses.",
                DisplayMessageConstants.GENERAL_ERROR );
        }       
      

    }
    
    
    public void unsetWebAddMongo(OrganizationUnitSettings unitSettings , String collection) throws InvalidInputException {
        LOG.info( "Unset web address mail in mongo for collection : {}",collection );
        if (collection != null && unitSettings.getContact_details().getWeb_addresses() != null ) {
            unsetKey( unitSettings, KEY_CONTACT_DETAILS_WEB_ADD_WORK,
                collection);
        }
    }

    @Override
    public boolean unsetKey( OrganizationUnitSettings companySettings, String keyToUpdate, String collectionName )
        throws InvalidInputException
    {
        if ( companySettings == null ) {
            throw new InvalidInputException( "Company settings cannot be null." );
        }

        LOG.debug( "unsetting comapnySettings: {} for key: {}", companySettings, keyToUpdate );
        organizationUnitSettingsDao.removeKeyInOrganizationSettings( companySettings, keyToUpdate, collectionName );
        LOG.debug( "Updated the record successfully" );

        return true;
    }


    public String checkValidMail( String mailId ) throws InvalidInputException
    {
        String mailIDStr = "";

        if ( !mailId.contains( "," ) ) {
            if ( !validateEmail( mailId.trim() ) )
                throw new InvalidInputException( "Mail id - {}   entered as send alert to input is invalid", mailId );
            else
                mailId = mailId.trim();
        } else {
            String mailIds[] = mailId.split( "," );
			for (String mailID : mailIds) {
				if (!validateEmail(mailID.trim()))
					throw new InvalidInputException(
							"Mail id - {} entered amongst the mail ids as send alert to input is invalid",mailId);
				else
					mailIDStr += mailID.trim() + " , ";
			}
			mailId = mailIDStr.substring(0, mailIDStr.length() - 2);
		}
		return mailId;
	}


    public void addSurveySettings( OrganizationUnitSettings unitSettings )
    {

        // Adding default text for various flows of survey.
        SurveySettings surveySettings = new SurveySettings();
        surveySettings.setHappyText( happyText );
        surveySettings.setNeutralText( neutralText );
        surveySettings.setSadText( sadText );
        surveySettings.setHappyTextComplete( happyTextComplete );
        surveySettings.setNeutralTextComplete( neutralTextComplete );
        surveySettings.setSadTextComplete( sadTextComplete );
        surveySettings.setAutoPostEnabled( true );
        surveySettings.setShow_survey_above_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
        surveySettings.setAuto_post_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );

        surveySettings.setSurvey_reminder_interval_in_days( CommonConstants.DEFAULT_REMINDERMAIL_INTERVAL );
        unitSettings.setSurvey_settings( surveySettings );
    }


    @Override
    public boolean doesSurveyHaveNPSQuestions( User user )
    {
        if ( user != null ) {
            try {
                for ( SurveyQuestionDetails survey : surveyBuilder.getAllActiveQuestionsOfMappedSurvey( user ) ) {
                    if ( survey.getIsNPSQuestion() == CommonConstants.ONE ) {
                        return true;
                    }
                }
            } catch ( InvalidInputException e ) {
                LOG.error( "unable to get active questions for COMPANY with ID: {}", user.getCompany().getCompanyId() );
            }
        }
        return false;
    }
	
	/**
	 * Update isLoginPrevented flag in Mongo for user
	 */
	@Override
    public void updateIsLoginPreventedForUser( Long userId, boolean isLoginPrevented ) throws InvalidInputException
    {
	    if ( userId == null || userId == 0l ) {
            throw new InvalidInputException( "userId is null or 0" );
        }
        
        List<Long> userIdList = new ArrayList<Long>();
        userIdList.add( userId );
        
        LOG.info( "Inside method updateIsLoginPreventedForUser for user : {}", userId );
        updateIsLoginPreventedForUsers( userIdList, isLoginPrevented );
    }
    
	
	/**
     * Update isLoginPrevented flag in Mongo for list of users
     */
    @Override
    public void updateIsLoginPreventedForUsers( List<Long> userIdList, boolean isLoginPrevented ) throws InvalidInputException
    {
        if ( userIdList == null || userIdList.isEmpty() ) {
            throw new InvalidInputException( "List of userIds is null" );
        }
        LOG.info( "Inside method updateIsLoginPreventedForUsers " );
        organizationUnitSettingsDao.updateIsLoginPreventedForUsersInMongo( userIdList, isLoginPrevented );
    }
    
    
    /**
     * Update hidePublicPage flag in Mongo for user
     */
    @Override
    public void updateHidePublicPageForUser( Long userId, boolean hidePublicPage ) throws InvalidInputException
    {
        if ( userId == null || userId == 0l ) {
            throw new InvalidInputException( "userId is null or 0" );
        }
        
        List<Long> userIdList = new ArrayList<Long>();
        userIdList.add( userId );
        
        LOG.info( "Inside method updateHidePublicPageForUser for user : {}", userId );
        updateHidePublicPageForUsers( userIdList, hidePublicPage );
    }
    
    
    /**
     * Update hidePublicPage flag in Mongo for a list of user
     */
    @Override
    public void updateHidePublicPageForUsers( List<Long> userIdList, boolean hidePublicPage ) throws InvalidInputException
    {
        if ( userIdList == null || userIdList.isEmpty() ) {
            throw new InvalidInputException( "List of userIds is null" );
        }
        LOG.info( "Inside method updateHidePublicPageForUsers " );
        organizationUnitSettingsDao.updateHidePublicPageForUsers( userIdList, hidePublicPage );
    }
    

    @Override
    public List<Keyword> deleteKeywordsFromCompany( long companyId, List<String> keywordIds ) throws InvalidInputException
    {
        LOG.debug( "Method deleteKeywordsFromCompany started for companyId {}", companyId );
        if ( companyId <= 0 || keywordIds.isEmpty() || keywordIds == null ) {
            LOG.warn( "Invalid input parameters" );
            throw new InvalidInputException( "Invalid input parameters" );
        }
        List<Keyword> companyFilterKeywords = new ArrayList<>();
        OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( companyId,
            CommonConstants.COMPANY_SETTINGS_COLLECTION );
        if ( companySettings != null ) {
            List<Keyword> keywords = companySettings.getFilterKeywords();
            if ( keywords != null && !keywords.isEmpty() ) {
                for ( String keywordId : keywordIds ) {
                    for ( Keyword keyword : keywords ) {
                        if ( keyword.getId().equalsIgnoreCase( keywordId ) ) {
                            keyword.setStatus( CommonConstants.STATUS_INACTIVE );
                            keyword.setModifiedOn( new Date().getTime() );
                        }
                    }
                }
                // Updating filterKeywords in OrganizationUnitSettings
                organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
                    KEY_FILTER_KEYWORDS, keywords, companySettings,
                    COMPANY_SETTINGS_COLLECTION );
            } else {
                LOG.warn( "The list of keywords is empty" );
                throw new InvalidInputException( "keywords do not exist for the company" );
            }
            for ( Keyword keywordToAdd : keywords ) {
                if ( keywordToAdd.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                    companyFilterKeywords.add( keywordToAdd );
                }
            }
        }
        Collections.sort( companyFilterKeywords, new FilterKeywordsComparator() );
        
        // update redis with the keywords
        redisDao.addKeywords( companyId, companyFilterKeywords );
        return companyFilterKeywords;

    }
    
    
    @Override
    public List<Keyword> addKeywordToCompanySettings( long companyId, Keyword keyword ) throws InvalidInputException
    {
        LOG.debug( "Get company settings for the companyId: {}", companyId );
        OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( companyId,
            COMPANY_SETTINGS_COLLECTION );

        List<Keyword> companyFilterKeywords = companySettings.getFilterKeywords();
        List<Keyword> filterKeywordsAdded = new ArrayList<>();
        if ( keyword != null ) {
            // setting
            if ( companySettings.getFilterKeywords() == null || companySettings.getFilterKeywords().isEmpty()) {
                companyFilterKeywords = new ArrayList<>();
            }

            int flag = 0;

            if ( StringUtils.isNotEmpty( keyword.getId() ) ) {
                flag = 1;
                for ( int i = 0; i < companyFilterKeywords.size(); i++ ) {
                    Keyword companyFilterKeyword = companyFilterKeywords.get( i );

                    if ( keyword.getId() != null && companyFilterKeyword.getId().equals( keyword.getId() )
                        && companyFilterKeyword.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                        flag = 2;
                        if ( keyword.getPhrase() != null ) {
                            if ( companyFilterKeyword.getPhrase().equalsIgnoreCase( keyword.getPhrase() ) ) {
                                LOG.warn( "Phrase already exists" );
                            } else {
                                companyFilterKeyword.setPhrase( keyword.getPhrase() );
                                companyFilterKeyword.setModifiedOn( new Date().getTime() );
                            }
                        }
                        if ( keyword.getMonitorType() != null
                            && !keyword.getMonitorType().equals( companyFilterKeyword.getMonitorType() ) ) {
                            companyFilterKeyword.setMonitorType( keyword.getMonitorType() );
                            companyFilterKeyword.setModifiedOn( new Date().getTime() );
                        }
                    }
                }
            }

            if ( flag == 1 ) {
                throw new InvalidInputException( "Keyword does not exist for given Id", "400" );
            }
            if ( flag == 0 ) {
                for ( Keyword duplicateKeyword : companyFilterKeywords ) {
                    if ( duplicateKeyword.getPhrase().equalsIgnoreCase( keyword.getPhrase() )
                        && duplicateKeyword.getMonitorType().equals( keyword.getMonitorType() )
                        && duplicateKeyword.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                        LOG.warn( "duplicate entry" );
                        throw new InvalidInputException( "duplicate entry", "400" );
                    }
                }
                LOG.info( "Adding new keyword" );
                Keyword keywordNew = new Keyword();
                keywordNew.setCreatedOn( new Date().getTime() );
                keywordNew.setModifiedOn( new Date().getTime() );
                keywordNew.setPhrase( keyword.getPhrase() );
                keywordNew.setId( UUID.randomUUID().toString() );
                keywordNew.setStatus( CommonConstants.STATUS_ACTIVE );
                keywordNew.setMonitorType( keyword.getMonitorType() );
                companyFilterKeywords.add( keywordNew );
            }
            // Updating filterKeywords in OrganizationUnitSettings
            organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
                KEY_FILTER_KEYWORDS, companyFilterKeywords, companySettings,
                COMPANY_SETTINGS_COLLECTION );

        } else {
            LOG.debug( "Keywords are empty so skiping operation" );
        }
        for ( Keyword keywordToAdd : companyFilterKeywords ) {
            if ( keywordToAdd.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                filterKeywordsAdded.add( keywordToAdd );
            }
        }
        Collections.sort( filterKeywordsAdded, new FilterKeywordsComparator() );
        
        // update the redis with the keywords
        redisDao.addKeywords( companyId, filterKeywordsAdded );
        
        return filterKeywordsAdded;
    }


    @Override
    public List<Keyword> addMultiplePhrasesToCompany( long companyId, MultiplePhrasesVO multiplePhrasesVO )
        throws InvalidInputException
    {
        LOG.debug( "Get company settings for the companyId: {}", companyId );
        OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( companyId,
            COMPANY_SETTINGS_COLLECTION );

        List<Keyword> companyFilterKeywords = companySettings.getFilterKeywords();
        List<Keyword> filterKeywordsAdded = new ArrayList<>();
        int flag = 0;

        if ( multiplePhrasesVO != null ) {
            if ( companySettings.getFilterKeywords() == null || companySettings.getFilterKeywords().isEmpty() ) {
                companyFilterKeywords = new ArrayList<>();
            }

            for ( String phrase : multiplePhrasesVO.getPhrases() ) {
                flag = 1;
                for ( Keyword duplicateKeyword : companyFilterKeywords ) {
                    if ( duplicateKeyword.getPhrase().equalsIgnoreCase( phrase.trim() )
                        && duplicateKeyword.getMonitorType().equals( multiplePhrasesVO.getMonitorType() )
                        && duplicateKeyword.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                        LOG.warn( "duplicate entry" );
                        flag = 2;
                        break;
                    }
                }
                if ( flag == 1 ) {
                    LOG.info( "Adding new keyword" );
                    Keyword keywordNew = new Keyword();
                    keywordNew.setCreatedOn( new Date().getTime() );
                    keywordNew.setModifiedOn( new Date().getTime() );
                    keywordNew.setPhrase( phrase.trim() );
                    keywordNew.setId( UUID.randomUUID().toString() );
                    keywordNew.setStatus( CommonConstants.STATUS_ACTIVE );
                    keywordNew.setMonitorType( multiplePhrasesVO.getMonitorType() );
                    companyFilterKeywords.add( keywordNew );
                }

            }
            // Updating filterKeywords in OrganizationUnitSettings
            organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
                KEY_FILTER_KEYWORDS, companyFilterKeywords, companySettings,
                COMPANY_SETTINGS_COLLECTION );

        } else {
            LOG.debug( "Keywords are empty so skipping operation" );
        }
        for ( Keyword keywordToAdd : companyFilterKeywords ) {
            if ( keywordToAdd.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                filterKeywordsAdded.add( keywordToAdd );
            }
        }
        Collections.sort( filterKeywordsAdded, new FilterKeywordsComparator() );

        // update the redis with the keywords
        redisDao.addKeywords( companyId, filterKeywordsAdded );
        
        return filterKeywordsAdded;
    }
    
    @Override
    public boolean enableSocialMonitorToggle( long companyId, boolean socialMonitorFlag )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "method enableSocialMonitorToggle() started for companyId : {}", companyId);

        if ( companyId <= 0 ) {
            LOG.warn( "companyId is invalid: {}", companyId);
            throw new InvalidInputException( "companyId is invalid" );
        }

        OrganizationUnitSettings unitSettings = getCompanySettings( companyId );

        if ( unitSettings == null ) {
            LOG.warn( "OrganizationSettings are not fournd for company id {}", companyId );
            throw new InvalidInputException( "OrganizationSettings are not fournd for company id " +companyId );
        }

        // Set default keywords in Company setting
        if ( socialMonitorFlag && ( unitSettings.getFilterKeywords() == null || unitSettings.getFilterKeywords().isEmpty() ) ) {
            List<Keyword> defaultFilterKeywords = createDefaultKeywordList();
            organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
                KEY_FILTER_KEYWORDS, defaultFilterKeywords, unitSettings,
                COMPANY_SETTINGS_COLLECTION );
            //update the redis with the keywords
            redisDao.addKeywords( companyId, defaultFilterKeywords );
        }

        // update social monitor flag
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            SOCIAL_MONITOR_ENABLED, socialMonitorFlag, unitSettings,
            CommonConstants.COMPANY_SETTINGS_COLLECTION );

        //update companyIds which have enabled/disabled socialMonitor
        redisDao.updateCompanyIdsForSM( companyId, socialMonitorFlag );

        LOG.debug( "method enableSocialMonitorToggle() finished." );
        return true;
    }


    /**
     * Method to create default keyword list from property file comma separated list.
     * @return : returns Keyword objects
     */
    private List<Keyword> createDefaultKeywordList()
    {
        LOG.debug( "Adding default keywords in company settings" );
        List<Keyword> defaultKeywords = new ArrayList<>();
        if ( StringUtils.isNotBlank( socialMonitorDefaultKeywords ) ) {
            long createdtime = new Date().getTime();
            String[] defaultPhrase = socialMonitorDefaultKeywords.split( "," );
            for ( int i = 0; i < defaultPhrase.length; i++ ) {
                if(StringUtils.isNotBlank( defaultPhrase[i] )){
                    Keyword keywordNew = new Keyword();
                    keywordNew.setCreatedOn( createdtime );
                    keywordNew.setModifiedOn( createdtime );
                    keywordNew.setPhrase( defaultPhrase[i].trim() );
                    keywordNew.setId( UUID.randomUUID().toString() );
                    keywordNew.setStatus( CommonConstants.STATUS_ACTIVE );
                    keywordNew.setMonitorType( MonitorType.KEYWORD_MONITOR );
                    defaultKeywords.add( keywordNew );
                }
            }
        }
        return defaultKeywords;
    }


    @Override
    public ContactDetailsSettings fetchContactDetailByEncryptedId( String encryptedId, String collection )
    {
        return organizationUnitSettingsDao.fetchContactDetailByEncryptedId( encryptedId, collection );
    }
    
    
    
    /**
     * Update socialMediaTokens in Mongo for a user
     */
    @Override
    public void updateSocialMediaForUser( Long userId, boolean disableSocialMediaTokens ) throws InvalidInputException
    {
        if ( userId == null || userId == 0l ) {
            throw new InvalidInputException( "userId is null or 0" );
        }
        
        List<Long> userIdList = new ArrayList<Long>();
        userIdList.add( userId );
        
        LOG.info( "Inside method updateSocialMediaForUser " );
        updateSocialMediaForUsers( userIdList, disableSocialMediaTokens );
    }
    
    
    /**
     * Update socialMediaTokens in Mongo for a list of users
     */
    @Override
    public void updateSocialMediaForUsers( List<Long> userIdList, boolean disableSocialMediaTokens ) throws InvalidInputException
    {
        if ( userIdList == null || userIdList.isEmpty() ) {
            throw new InvalidInputException( "List of userIds is null" );
        }
        LOG.info( "Inside method updateSocialMediaForUsers " );
        organizationUnitSettingsDao.updateSocialMediaForUsers( userIdList, disableSocialMediaTokens );
    }
    
    @Override
    public boolean updateUserAdditionDeletionRecipients( String entityType, long entityId, Set<String> emails )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "method updateUserAdditionDeletionRecipients() started" );
        if ( StringUtils.isEmpty( entityType ) ) {
            LOG.warn( "Entity type is not specified" );
            throw new InvalidInputException( "Entity type is not specified" );
        } else if ( entityId <= 0 ) {
            LOG.warn( "Entity Id is invalid" );
            throw new InvalidInputException( "Entity Id is invalid" );
        }

        OrganizationUnitSettings unitSettings = null;
        String collectionType = "";

        if ( CommonConstants.COMPANY_ID_COLUMN.equals( entityType ) ) {
            unitSettings = getCompanySettings( entityId );
            collectionType = CommonConstants.COMPANY_SETTINGS_COLLECTION;
        } else if ( CommonConstants.REGION_ID_COLUMN.equals( entityType ) ) {
            unitSettings = getRegionSettings( entityId );
            collectionType = CommonConstants.REGION_SETTINGS_COLLECTION;
        } else if ( CommonConstants.BRANCH_ID_COLUMN.equals( entityType ) ) {
            unitSettings = getBranchSettingsDefault( entityId );
            collectionType = CommonConstants.BRANCH_SETTINGS_COLLECTION;
        } else if ( CommonConstants.AGENT_ID_COLUMN.equals( entityType ) ) {
            unitSettings = getAgentSettings( entityId );
            collectionType = CommonConstants.AGENT_SETTINGS_COLLECTION;
        } else {
            LOG.warn( "Entity Type is invalid" );
            throw new InvalidInputException( "Entity type is invalid" );
        }

        if ( unitSettings == null ) {
            LOG.warn( "settings are not specified" );
            throw new InvalidInputException( "settings cannot be null." );
        }

        LOG.trace( "Updating unitSettings: {} with user addition/deletion notification recipients: {}", unitSettings, emails );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            KEY_USER_ADD_DELETE_NOTIFICATION_RECIPIENTS, emails, unitSettings, collectionType );
        LOG.debug( "Updated the record successfully" );

        return true;
    }

    /**
     *Updates socialmedia token
     * @param collection
     * @param iden
     * @param fieldToUpdate
     * @param value
     */
    @Override public boolean updateSocialMediaToken( String collection, long iden, String fieldToUpdate, boolean value )
        throws InvalidInputException
    {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "method updateSocialMediaToken() called" );
        }
        if(collection == null || collection.isEmpty()) throw  new InvalidInputException( "Collection name is invalid" );
        else if(iden <= 0) throw  new InvalidInputException( "iden is invalid" );
        else if(fieldToUpdate == null || fieldToUpdate.isEmpty()) throw  new InvalidInputException( "FieldName is invalid" );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden( fieldToUpdate, value, iden, collection );
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "method updateSocialMediaToken() completed" );
        }
        return true;
    }


    /**
    * @param adminUser
    * @param user
    * @throws InvalidInputException
    * @throws UndeliveredEmailException
    * @throws NoRecordsFetchedException
    */
   @Override
   public void sendUserAdditionMail( User adminUser, User user )
       throws InvalidInputException, UndeliveredEmailException, NoRecordsFetchedException
   {
       LOG.debug( "sending user adition mail" );

       if ( adminUser == null || user == null ) {
           LOG.warn( "user details not specified for user addition mail" );
           throw new InvalidInputException( "user details not specified for user addition mail" );
       }

       if ( adminUser.isSuperAdmin() || userManagementService.isUserSocialSurveyAdmin( adminUser ) ) {
           LOG.debug( "Usaer was added by a social survey addmin, aborting" );
           return;
       }

       if ( user.getCompany() == null ) {
           LOG.warn( "User added, is not associated with a company" );
           return;
       }

       if ( user.isCompanyAdmin() ) {
           LOG.debug( "company admin is the added user, aborting" );
           return;
       }

       Set<String> recipients = new HashSet<>();
       long companyId = user.getCompany().getCompanyId();
       OrganizationUnitSettings companySettings = getCompanySettings( companyId );

       if ( companySettings != null && companySettings.getUserAddDeleteNotificationRecipients() != null ) {
           recipients.addAll( companySettings.getUserAddDeleteNotificationRecipients() );
       }

       if ( user.getCompany() != null ) {
           recipients.add( userManagementService.getCompanyAdmin( companyId ).getEmailId() );
       }

       if ( recipients.isEmpty() ) {
           LOG.debug( "No user addition notification mail recipient found" );
           return;
       }


       emailServices.sendUserAdditionMail( recipients,
           adminUser.getFirstName() + ( StringUtils.isEmpty( adminUser.getLastName() ) ? "" : " " + adminUser.getLastName() ),
           adminUser.getEmailId(), user, getAgentSettings( user.getUserId() ) );
   }


   /**
    * 
    * @param adminUser
    * @param user
    * @throws InvalidInputException
    * @throws UndeliveredEmailException
    * @throws NoRecordsFetchedException
    */
   @Override
   public void sendUserDeletionMail( User adminUser, User user )
       throws InvalidInputException, UndeliveredEmailException, NoRecordsFetchedException
   {
       LOG.debug( "sending user deletion mail" );

       if ( adminUser == null || user == null ) {
           LOG.warn( "user details not specified for user deletion mail" );
           throw new InvalidInputException( "user details not specified for user deletion mail" );
       }

       if ( user.getCompany() == null ) {
           LOG.warn( "User to be deleted, is not associated with a company" );
           return;
       }

       if ( adminUser.isSuperAdmin() || userManagementService.isUserSocialSurveyAdmin( adminUser ) ) {
           LOG.debug( "User was deleted by a social survey addmin, aborting" );
           return;
       }

       if ( user.isCompanyAdmin() ) {
           LOG.debug( "company admin is the deleted user, aborting" );
           return;
       }

       Set<String> recipients = new HashSet<>();
       long companyId = user.getCompany().getCompanyId();
       OrganizationUnitSettings companySettings = getCompanySettings( companyId );

       if ( companySettings != null && companySettings.getUserAddDeleteNotificationRecipients() != null ) {
           recipients.addAll( companySettings.getUserAddDeleteNotificationRecipients() );
       }

       if ( user.getCompany() != null ) {
           recipients.add( userManagementService.getCompanyAdmin( companyId ).getEmailId() );
       }

       if ( recipients.isEmpty() ) {
           LOG.debug( "No user deletion notification mail recipient found" );
           return;
       }

       emailServices.sendUserDeletionMail( recipients,
           adminUser.getFirstName() + ( StringUtils.isEmpty( adminUser.getLastName() ) ? "" : " " + adminUser.getLastName() ),
           adminUser.getEmailId(), user, getAgentSettings( user.getUserId() ) );
   }
    
    
    /**
     * 
     */
    @Override
    public boolean updateEncompassVersion( long companyId, String version ) throws InvalidInputException
    {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "method updateEncompassVersion() called" );
        }
        if ( companyId <= 0 ) {
            LOG.warn( "company ID specified is invalid" );
            throw new InvalidInputException( "company ID specified is invalid" );
        } else if ( StringUtils.isEmpty( version ) ) {
            LOG.warn( "encompass version specified is invalid" );
            throw new InvalidInputException( "encompass version specified is invalid" );
        }


        OrganizationUnitSettings companySettings = getCompanySettings( companyId );

        if ( companySettings == null ) {
            LOG.warn( "company ID specified is does not exist" );
            throw new InvalidInputException( "company ID specified is does not exist" );
        } else if ( companySettings.getCrm_info() == null || !StringUtils.equals( companySettings.getCrm_info().getCrm_source(),
            CommonConstants.CRM_INFO_SOURCE_ENCOMPASS ) ) {
            LOG.warn( "company specified does not have encompass connection" );
            throw new InvalidInputException( "company specified does not have encompass connection" );
        }

        boolean isVersionSupported = false;
        for ( EncompassSdkVersion encompassVersion : encompassSdkVersionDao.findAll( EncompassSdkVersion.class ) ) {
            if ( encompassVersion.getSdkVersion().trim().equals( version.trim() ) ) {
                isVersionSupported = true;
                break;
            }
        }

        if ( !isVersionSupported ) {
            LOG.warn( "The new encompass version specified is not supported" );
            throw new InvalidInputException( "The new encompass version specified is not supported" );
        }

        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "updating encompass version for company with ID : {}, from {} to {}", companyId,
                ( (EncompassCrmInfo) companySettings.getCrm_info() ).getVersion(), version );
        }

        EncompassCrmInfo encompassCrmInfo = (EncompassCrmInfo) companySettings.getCrm_info();
        encompassCrmInfo.setVersion( version.trim() );

        updateCRMDetails( companySettings, encompassCrmInfo, EncompassCrmInfo.class.getName() );

        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "method updateEncompassVersion() finished" );
        }
        return false;
    }
    
    @Override
    public List<SocialMonitorTrustedSource> addTrustedSourceToCompany( long companyId , String trustedSource ) throws InvalidInputException
	{
		LOG.debug("Get company settings for the companyId: {}", companyId);
		OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
				companyId, COMPANY_SETTINGS_COLLECTION);

		if(StringUtils.isEmpty(trustedSource)) {
			throw new InvalidInputException("Invalid parameter passed. trustedSource can't be empty ");
		}
		if(companySettings == null) {
			throw new InvalidInputException("Invalid companyId passed. No company setting found for given companyId ");
		}
		
		//get existing  trusted sources for company
		List<SocialMonitorTrustedSource> companyTrustedSources = companySettings.getSocialMonitorTrustedSources();
		if (companyTrustedSources == null || companyTrustedSources.isEmpty())
			companyTrustedSources = new ArrayList<SocialMonitorTrustedSource>();

		//check if trusted source is already present
		boolean isAleadyExist = false;
		for (SocialMonitorTrustedSource currentTrustedSource : companyTrustedSources) {
			if (currentTrustedSource.getSource().equalsIgnoreCase(trustedSource.trim())
					&& currentTrustedSource.getStatus() == CommonConstants.STATUS_ACTIVE) {
				LOG.warn("duplicate entry");
				isAleadyExist = true;
				break;
			}
		}
		
		// create new trusted source entity
		if ( ! isAleadyExist) {
			LOG.info("Adding new socialMonitorTrustedSource");
			SocialMonitorTrustedSource socialMonitorTrustedSource = new SocialMonitorTrustedSource();
			socialMonitorTrustedSource.setCreatedOn(new Date().getTime());
			socialMonitorTrustedSource.setModifiedOn(new Date().getTime());
			socialMonitorTrustedSource.setSource(trustedSource.trim());
			socialMonitorTrustedSource.setId(UUID.randomUUID().toString());
			socialMonitorTrustedSource.setStatus(CommonConstants.STATUS_ACTIVE);
			companyTrustedSources.add(socialMonitorTrustedSource);
		}

		// save updated companyTrustedSources in mongo settings
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
				KEY_TRUSTED_SOURCES, companyTrustedSources, companySettings,
				COMPANY_SETTINGS_COLLECTION);

		//create truested sources to save in redis
		List<SocialMonitorTrustedSource> companyTrustedSourcesToAdd = new ArrayList<SocialMonitorTrustedSource>();
		for (SocialMonitorTrustedSource socialMonitorTrustedSource : companyTrustedSources) {
			if (socialMonitorTrustedSource.getStatus() == CommonConstants.STATUS_ACTIVE) {
				companyTrustedSourcesToAdd.add(socialMonitorTrustedSource);
			}
		}
		Collections.sort(companyTrustedSourcesToAdd, new TrustedSourceComparator());

		// update the redis with the trusted sources
		redisDao.addTruestedSources(companyId, companyTrustedSourcesToAdd);

		//To update former posts to resolved for the given trusted source
		socialFeedService.updateTrustedSourceForFormerLists(companyId,trustedSource);

		return companyTrustedSourcesToAdd;
	}
 
    //updating all agents of a particular company
    @Override
    public boolean updateEntitySettings( String entityType, long entityId, String flagToBeUpdated, String status )
    {
        try {
            if ( entityType == null || entityType.isEmpty() ) {
                throw new InvalidInputException( "Entity type is null" );
            }
            if ( flagToBeUpdated == null || flagToBeUpdated.isEmpty() ) {
                throw new InvalidInputException( "settings flag to be updated is null" );
            }
            if ( status == null || status.isEmpty() ) {
                throw new InvalidInputException( "value to update is null" );
            }
            OrganizationUnitSettings unitSettings = null;
            
            String collection = null;
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                unitSettings = getCompanySettings( entityId );
                collection = COMPANY_SETTINGS_COLLECTION;
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                unitSettings = getRegionSettings( entityId );
                collection = REGION_SETTINGS_COLLECTION;
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                unitSettings = getBranchSettingsDefault( entityId );
                collection = BRANCH_SETTINGS_COLLECTION;
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                unitSettings = userManagementService.getUserSettings( entityId );
                collection = AGENT_SETTINGS_COLLECTION;
            } else {
                throw new InvalidInputException( "Invalid input exception occurred while updating web addresses.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }
            if ( unitSettings == null ) {
                throw new InvalidInputException(
                    "No settings found for entityType " + entityType + "and entityId " + entityId );
            }
            
            if ( flagToBeUpdated.equals( MongoOrganizationUnitSettingDaoImpl.KEY_HIDE_PUBLIC_PAGE )
                || flagToBeUpdated.equals( MongoOrganizationUnitSettingDaoImpl.KEY_HIDDEN_SECTION )
                || flagToBeUpdated.equals( MongoOrganizationUnitSettingDaoImpl.KEY_SEND_EMAIL_FROM_COMPANY )
                || flagToBeUpdated.equals( MongoOrganizationUnitSettingDaoImpl.KEY_HIDE_FROM_BREAD_CRUMB )
                || flagToBeUpdated.equals( MongoOrganizationUnitSettingDaoImpl.KEY_INCLUDE_FOR_TRANSACTION_MONITOR )
                || flagToBeUpdated.equals( MongoOrganizationUnitSettingDaoImpl.KEY_ALLOW_OVERRIDE_FOR_SOCIAL_MEDIA )
                || flagToBeUpdated.equals( MongoOrganizationUnitSettingDaoImpl.KEY_ALLOW_CONFIGURE_SECONDARY_WORKFLOW )) {
            	
                boolean flag = Boolean.parseBoolean( status );
                organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden( flagToBeUpdated, flag,
                    unitSettings.getIden(), collection );
            }
            else if (flagToBeUpdated.equals( KEY_SEND_EMAIL_THROUGH)) {
                organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden( flagToBeUpdated, status,
                    unitSettings.getIden(), collection );
            }
            if(flagToBeUpdated.equals( KEY_HIDDEN_SECTION) && collection.equals(CommonConstants.COMPANY_SETTINGS_COLLECTION)) {
            	boolean flag = Boolean.parseBoolean( status );
            	searchEngineManagementServices.updateHiddenSectionForAgentsOfCompany(unitSettings.getIden(), collection, flagToBeUpdated, flag);
            }
            return true;
        } catch ( InvalidInputException e ) {
            LOG.error( "Invalid Input Exception occurred while updating settings", e );
        } catch ( NoRecordsFetchedException e ) {
            LOG.error( "No redords found while updating settings", e );
        }
        return false;
    }

    @Override
    public void updateAgentProfileDisable(long companyId , boolean isAgentProfileDisabled) throws InvalidInputException {
        LOG.info( "disabling editing for agent profile page's for companyId:{}",companyId );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden( KEY_AGENT_PROFILE_DISABLED,
            isAgentProfileDisabled, companyId,COMPANY_SETTINGS_COLLECTION  );
        
    }
    
    //use this function updateKeyOrganizationUnitSettingsByInCriteria
    //updating a list of agents to not be able to edit their profile
    @Override
    public void updateAgentsProfileDisable(List<Long> agentIds , boolean isAgentProfileDisabled) throws InvalidInputException {
        LOG.info( "disabling editing for agent profile page's for agentId's:{}",agentIds );
        if ( agentIds == null ) {
            throw new InvalidInputException( "User ids cannot be null." );
        }

        List<Object> userIdList = new ArrayList<>();
        userIdList.addAll( agentIds );
        organizationUnitSettingsDao.updateKeyOrganizationUnitSettingsByInCriteria( KEY_AGENT_PROFILE_DISABLED,
            isAgentProfileDisabled, KEY_IDEN,userIdList,AGENT_SETTINGS_COLLECTION  );
    }



    @Override
    public void updateCopyToClipBoardSettings( long companyId, boolean updateCopyToClipBoardSetting )
    {
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden(
            KEY_IS_COPY_TO_CLIPBOARD, updateCopyToClipBoardSetting, companyId,
            COMPANY_SETTINGS_COLLECTION );
        
    }

    @Override
    public boolean isSocialMonitorAdmin( Long agentId ) throws InvalidInputException
    {
        LOG.debug( "Method isSocialMonitorAdmin called for agentId:" + agentId );
        if ( agentId < 0l ) {
            LOG.error( "Invalid agent id passed as argument " );
            throw new InvalidInputException( "Invalid agent id passed as argument " );
        }


        List<UserProfile> userProfiles = new ArrayList<>();
        User user = userDao.findById( User.class, agentId );
        if ( user != null && (user.getStatus() == CommonConstants.STATUS_ACTIVE || user.getStatus() == CommonConstants.STATUS_NOT_VERIFIED)) {
            userProfiles = user.getUserProfiles();
        }

        if ( userProfiles != null && !userProfiles.isEmpty() ) {
            for ( UserProfile userProfile : userProfiles ) {
                if ( userProfile.getUser().getUserId() == agentId
                    && userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_SM_ADMIN_PROFILE_ID
                    && userProfile.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public List<SocialMonitorTrustedSource> removeTrustedSourceToCompany( long companyId , String trustedSource ) throws InvalidInputException
    {
        LOG.debug("Get company settings for the companyId: {}", companyId);
        
        if(StringUtils.isEmpty(trustedSource)) {
            throw new InvalidInputException("Invalid parameter passed. trustedSource can't be empty ");
        }
        
        OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
                companyId, COMPANY_SETTINGS_COLLECTION);
        
        if(companySettings == null) {
            throw new InvalidInputException("Invalid companyId passed. No company setting found for given companyId ");
        }
        
        //get existing  trusted sources for company
        List<SocialMonitorTrustedSource> companyTrustedSources = companySettings.getSocialMonitorTrustedSources();
        if (companyTrustedSources == null || companyTrustedSources.isEmpty())
            companyTrustedSources = new ArrayList<>();

        boolean doesNotExist = true;
        //check if trusted source is already present
        Iterator<SocialMonitorTrustedSource> currentTrustedSourceItr = companyTrustedSources.iterator();
        while (currentTrustedSourceItr.hasNext()) {
            SocialMonitorTrustedSource currentTrustedSource = currentTrustedSourceItr.next();
            if (currentTrustedSource.getSource().equalsIgnoreCase(trustedSource.trim())
                    && currentTrustedSource.getStatus() == CommonConstants.STATUS_ACTIVE) {
                LOG.debug( "trusted source : {} exists in the list", trustedSource);
                doesNotExist = false;
                currentTrustedSourceItr.remove();

            }
        }
        
        //if trusted source doesn't exist in list through exception
        if(doesNotExist) {
            throw new InvalidInputException("Trusted source doesn't exist for given companyId ");
        }
        
        // Updating fromTrustedSource to false.
        socialFeedService.updateForRemoveTrustedSource(companyId, trustedSource);
        
        // save updated companyTrustedSources in mongo settings
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
                KEY_TRUSTED_SOURCES, companyTrustedSources, companySettings,
                COMPANY_SETTINGS_COLLECTION);

        //create trusted sources to save in redis
        List<SocialMonitorTrustedSource> companyTrustedSourcesToAdd = new ArrayList<>();
        for (SocialMonitorTrustedSource socialMonitorTrustedSource : companyTrustedSources) {
            if (socialMonitorTrustedSource.getStatus() == CommonConstants.STATUS_ACTIVE) {
                companyTrustedSourcesToAdd.add(socialMonitorTrustedSource);
            }
        }
        Collections.sort(companyTrustedSourcesToAdd, new TrustedSourceComparator());

        // update the redis with the trusted sources
        redisDao.addTruestedSources(companyId, companyTrustedSourcesToAdd);
        return companyTrustedSourcesToAdd;
    }


    @Override
    public boolean hasRegisteredForSummit(Long companyId) throws InvalidInputException {
         LOG.debug( "Method hasRegisteredForSummit called for companyId:" + companyId );
         if ( companyId < 0l ) {
            LOG.error( "Invalid company id passed as argument " );
            throw new InvalidInputException( "Invalid company id passed as argument " );
         }
    
         return organizationUnitSettingsDao.hasRegisteredForSummit(companyId).hasRegisteredForSummit();
    }


    @Override
    public void setHasRegisteredForSummit(Long companyId, boolean hasRegisteredForSummit) throws InvalidInputException {
         LOG.debug( "Method hasRegisteredForSummit called for companyId:" + companyId );
         if ( companyId < 0l ) {
            LOG.error( "Invalid company id passed as argument " );
            throw new InvalidInputException( "Invalid company id passed as argument " );
         }
         
         organizationUnitSettingsDao.updateHasRegisteredForSummit(companyId, hasRegisteredForSummit);
    }
    
    @Override
    public boolean isShowSummitPopup(Long entityId, String entityType) throws InvalidInputException {
         LOG.debug( "Method isShowSummitPopup called for entityId:" + entityId );
         if ( entityId < 0l ) {
            LOG.error( "Invalid entityId passed as argument " );
            throw new InvalidInputException( "Invalid entityId passed as argument " );
         }
    
         return organizationUnitSettingsDao.isShowSummitPopup(entityId,entityType).isShowSummitPopup();
    }



    @Override
    public void setShowSummitPopup(Long entityId, String entityType, boolean isShowSummitPopup) throws InvalidInputException {
         LOG.debug( "Method hasRegisteredForSummit called for entityId:" + entityId );
         if ( entityId < 0l ) {
            LOG.error( "Invalid entityId passed as argument " );
            throw new InvalidInputException( "Invalid entityId passed as argument " );
         }
         
         organizationUnitSettingsDao.updateShowSummitPopup(entityId, entityType, isShowSummitPopup);
    }

    
     @Override 
     public void setFtpInfo(Long companyId ,TransactionSourceFtp transactionSourceFtp) throws InvalidInputException 
     {
         LOG.info( "Inside method setFtpInfo" );
         
             //get companySettings
             OrganizationUnitSettings companySettings = getCompanySettings( companyId );
             
             if ( companySettings == null ) {
                 throw new InvalidInputException( "Company settings cannot be null." );
             }
             
             organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
                 KEY_FTP_INFO, transactionSourceFtp, companySettings,
                 COMPANY_SETTINGS_COLLECTION );
             LOG.debug( "Updated the record successfully" );
         
     }
     
     //
     @Override
     public TransactionSourceFtp fetchFtpInfo(Long companyId , Long ftpId) {
         LOG.info( "inside service class to fetch the ftp info for companyId : {} . ftpId : {}",companyId,ftpId );
         return organizationUnitSettingsDao.fetchFileHeaderMapper(companyId,ftpId);
     }
     
     @Override
     public void updateFtpMailService(long companyId,  long ftpId ,String mailId) throws NonFatalException {
    
         LOG.info("Update ftp mail for companyId: {} , ftpId : {} , mail:{}",companyId,ftpId,mailId);
         mailId = checkValidMail(mailId);
         List<TransactionSourceFtp> transactionSourceFtpList = organizationUnitSettingsDao.fetchTransactionFtpListActive(companyId);
         for(TransactionSourceFtp transactionSourceFtp : transactionSourceFtpList) {
             if(transactionSourceFtp.getFtpId() == ftpId) {
                 transactionSourceFtp.setEmailId(mailId);
                 break;
             }
         }
         organizationUnitSettingsDao.updateFtpTransaction(companyId,transactionSourceFtpList);
    
     }
     
     @Override
     public void sendCompletionMailService(long companyId , long ftpId , String s3FileLocation ,FtpSurveyResponse ftpSurveyResponse ) throws InvalidInputException, UndeliveredEmailException {
         LOG.debug("send ftp successfull mail for companyId: {} , ftpId : {} ",companyId,ftpId); 
         Company company = companyDao.findById( Company.class, companyId );
         String companyName = company.getCompany();
         TransactionSourceFtp transactionSourceFtp = fetchFtpInfo( companyId, ftpId );
         String ftpMailId = transactionSourceFtp.getEmailId();
         String ftpErrorHtml = "";
         if (ftpSurveyResponse.getErrorMessage() != null && !ftpSurveyResponse.getErrorMessage().isEmpty()) {
             ftpErrorHtml = createHtmlForFtpError( ftpSurveyResponse.getErrorMessage() );
         }
         //get time and file name from s3location
         String lastIndex = s3FileLocation.substring( s3FileLocation.lastIndexOf( '/' )+1).trim();
         String fileName = lastIndex;
         String timeStamp = lastIndex.substring( lastIndex.lastIndexOf( '_' )+1,lastIndex.indexOf( '.' ) );
         emailServices.sendFtpSuccessMail( companyName, utils.convertDateToTimeZone( Long.parseLong( timeStamp ), CommonConstants.TIMEZONE_EST ) , fileName, ftpSurveyResponse, ftpMailId, ftpErrorHtml );
     }
     
     private String createHtmlForFtpError(Map<Integer, String> errorMessages) {
         String errorHtml = "";
         for(Map.Entry<Integer,String> errorMsg : errorMessages.entrySet()) {
             errorHtml += "<br/><span>&bull; </span>Row "+errorMsg.getKey()+" ERROR: "+errorMsg.getValue()+".";
         }
         return errorHtml;
     }
     
     private String convertToUTC(String timeStamp) {
         Timestamp time = new Timestamp( Long.parseLong( timeStamp ) );
         Date date=new Date(time.getTime());  
         final TimeZone tz = TimeZone.getTimeZone("UTC");
         final SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");
         formatter.setTimeZone(tz);
         return formatter.format(date);
     }
     
     
     
     @Override
     public OrganizationUnitSettings getEntitySettings( long entityId, String entityType ) throws InvalidInputException
     {
         OrganizationUnitSettings unitSettings = null;
         try {
             if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) || entityType.equals( CommonConstants.PROFILE_LEVEL_COMPANY ) ) {
                 unitSettings = getCompanySettings( entityId );
             } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) || entityType.equals( CommonConstants.PROFILE_LEVEL_INDIVIDUAL ) ) {
                 unitSettings = getAgentSettings( entityId );
             } else if ( entityType.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN ) || entityType.equals( CommonConstants.PROFILE_LEVEL_BRANCH ) ) {
                 unitSettings = getBranchSettingsDefault( entityId );
             } else if ( entityType.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN ) || entityType.equals( CommonConstants.PROFILE_LEVEL_REGION ) ) {
                 unitSettings = getRegionSettings( entityId );
             } else {
                 throw new InvalidInputException( "Invalid entity type passed." );
             }
         } catch ( InvalidInputException | NoRecordsFetchedException e ) {
             LOG.error( "Either invalid entity type was passed or no records were found.", e );
             throw new InvalidInputException( "Either an invalid entity type was passed or no records were found." );
         }
         return unitSettings;
     }


    @Override
    public Map<String,Map<String,String>> getProfileNameMapForCompany( long iden ) throws InvalidInputException
    {
        Map<String,Map<String,String>> profileNameMap = new HashMap<>();
        Map<String,String> companyProfileNameMap = null;
        Map<String,String> regionProfileNameMap = null;
        Map<String,String> branchProfileNameMap = null;
        Map<String,String> userProfileNameMap = null;
        

        OrganizationUnitSettings companySettings = getCompanySettings( iden );
        List<Long> regionIds = getRegionIdsUnderCompany( iden, -1, -1 );
        List<Long> branchIds = getBranchIdsUnderCompany( iden, -1, -1 );
        List<Long> agentIds = getAgentIdsUnderCompany( iden, -1, -1 );

        List<OrganizationUnitSettings> regionSettingsList = organizationUnitSettingsDao
            .fetchOrganizationUnitSettingsForMultipleIds( new HashSet<Long>( regionIds ),
                REGION_SETTINGS_COLLECTION );
        List<OrganizationUnitSettings> branchSettingsList = organizationUnitSettingsDao
            .fetchOrganizationUnitSettingsForMultipleIds( new HashSet<Long>( branchIds ),
                BRANCH_SETTINGS_COLLECTION );
        List<OrganizationUnitSettings> agentSettingsList = organizationUnitSettingsDao
            .fetchOrganizationUnitSettingsForMultipleIds( new HashSet<Long>( agentIds ),
                AGENT_SETTINGS_COLLECTION );

        companyProfileNameMap = new HashMap<>();
        if ( companySettings.getContact_details() != null ) {
            companyProfileNameMap.put( companySettings.getProfileName(), companySettings.getContact_details().getName() );
        } else {
            companyProfileNameMap.put( companySettings.getProfileName(), null );
        }

        if ( regionSettingsList != null && !regionSettingsList.isEmpty() ) {
            regionProfileNameMap = new HashMap<>();
            for ( OrganizationUnitSettings regionSettings : regionSettingsList ) {
                if ( regionSettings.getContact_details() != null ) {
                    regionProfileNameMap.put( regionSettings.getProfileName(), regionSettings.getContact_details().getName() );
                } else {
                    regionProfileNameMap.put( regionSettings.getProfileName(), null );
                }
            }
        }


        if ( branchSettingsList != null && !branchSettingsList.isEmpty() ) {
            branchProfileNameMap = new HashMap<>();
            for ( OrganizationUnitSettings branchSettings : branchSettingsList ) {
                if ( branchSettings.getContact_details() != null ) {
                    branchProfileNameMap.put( branchSettings.getProfileName(), branchSettings.getContact_details().getName() );
                } else {
                    branchProfileNameMap.put( branchSettings.getProfileName(), null );
                }
            }
        }

        if ( agentSettingsList != null && !agentSettingsList.isEmpty() ) {
            userProfileNameMap = new HashMap<>();
            for ( OrganizationUnitSettings agentSettings : agentSettingsList ) {
                if ( agentSettings.getContact_details() != null ) {
                    userProfileNameMap.put( agentSettings.getProfileName(), agentSettings.getContact_details().getName() );
                } else {
                    userProfileNameMap.put( agentSettings.getProfileName(), null );
                }
            }
        }


        profileNameMap.put( CommonConstants.PROFILE_LEVEL_COMPANY, companyProfileNameMap );
        profileNameMap.put( CommonConstants.PROFILE_LEVEL_REGION, regionProfileNameMap );
        profileNameMap.put( CommonConstants.PROFILE_LEVEL_BRANCH, branchProfileNameMap );
        profileNameMap.put( CommonConstants.PROFILE_LEVEL_INDIVIDUAL, userProfileNameMap );
        return profileNameMap;
    }
    
    /**
     * @param emailIdsArray
     * @param adminUser
     * @param holdSendingMail
     * @param sendMail
     * @param isAddedByRealtechOrSSAdmin
     * @param firstName
     * @param lastName
     * @return
     * @throws InvalidInputException
     */
    @Override
    public Map<String, List<User>> getUsersFromEmailIdsAndInvite( String[] emailIdsArray, User adminUser,
        boolean holdSendingMail, boolean sendMail, boolean isAddedByRealtechOrSSAdmin, String firstName, String lastName  ) throws InvalidInputException
    {
        LOG.debug( "Method getUsersFromEmailIds called for emailIdsArray:" + emailIdsArray );
        List<User> users = new ArrayList<User>();
        List<User> invalidUsers = new ArrayList<User>();
        Map<String, List<User>> usersMap = new HashMap<String, List<User>>();
        for ( String emailId : emailIdsArray ) {
            if ( emailId.contains( "\"" ) ) {
                emailId = emailId.replace( "\"", "" );
            }
            User user = null;
            if ( validateEmail( emailId ) ) {

                try {
                    user = userManagementService.getUserByEmailAddress( emailId );
                } catch ( NoRecordsFetchedException e ) {
                    /**
                     * if no user is present with the specified emailId, send an invite to register
                     */
                    try {
                        user = userManagementService.inviteUserToRegister( adminUser, firstName, lastName, emailId,
                            holdSendingMail, sendMail, false, isAddedByRealtechOrSSAdmin );
                    } catch ( UserAlreadyExistsException | UndeliveredEmailException | NoRecordsFetchedException e1 ) {
                        LOG.debug( "Exception in getUsersFromEmailIds while inviting a new user. Reason:" + e1.getMessage(),
                            e1 );
                    }
                }
            } else {
                LOG.error( "This email address " + emailId + " is not a valid email" );
                User invalidUser = new User();
                invalidUser.setEmailId( emailId );
                invalidUsers.add( invalidUser );
            }
            if ( user != null ) {
                users.add( user );

            }


        }
        usersMap.put( CommonConstants.VALID_USERS_LIST, users );
        usersMap.put( CommonConstants.INVALID_USERS_LIST, invalidUsers );
        LOG.debug( "Method getUsersFromEmailIds executed successfully. Returning users size :" + users.size() );
        return usersMap;
    }

    @Override
    @Transactional
    public SocialMediaTokensPaginated fetchFbTokensPaginated( int skipCount, int batchSize )
        throws InvalidInputException
    {
        LOG.info( "Inside method fetchFbTokensResponse" );
        SocialMediaTokensPaginated socialMediaTokensPaginated = new SocialMediaTokensPaginated();
        int totalRecord = 0;

        // fetch companies media token
        long companyTokensCount = organizationUnitSettingsDao
            .getFacebookTokensCount( COMPANY_SETTINGS_COLLECTION );
        long regionTokensCount = organizationUnitSettingsDao
            .getFacebookTokensCount( REGION_SETTINGS_COLLECTION );
        long branchTokensCount = organizationUnitSettingsDao
            .getFacebookTokensCount( BRANCH_SETTINGS_COLLECTION );
        long agentTokensCount = organizationUnitSettingsDao
            .getFacebookTokensCount( AGENT_SETTINGS_COLLECTION );

        if ( skipCount < companyTokensCount ) {
            List<SocialMediaTokenResponse> companiesMediaTokens = organizationUnitSettingsDao
                .getFbTokensByCollection( COMPANY_SETTINGS_COLLECTION,
                    skipCount, batchSize );

            totalRecord = companiesMediaTokens.size();

            socialMediaTokensPaginated.setCompaniesTokens( companiesMediaTokens );

            if ( totalRecord < batchSize ) {
                List<SocialMediaTokenResponse> regionsMediaTokens = organizationUnitSettingsDao
                    .getFbTokensByCollection( REGION_SETTINGS_COLLECTION, 0,
                        batchSize - totalRecord );
                totalRecord += regionsMediaTokens.size();

                socialMediaTokensPaginated.setRegionsTokens( regionsMediaTokens );

                if ( totalRecord < batchSize ) {
                    List<SocialMediaTokenResponse> branchMediaTokens = organizationUnitSettingsDao
                        .getFbTokensByCollection( BRANCH_SETTINGS_COLLECTION,
                            0, batchSize - totalRecord );
                    totalRecord += branchMediaTokens.size();
                    socialMediaTokensPaginated.setBranchesTokens( branchMediaTokens );

                    if ( totalRecord < batchSize ) {
                        List<SocialMediaTokenResponse> agentMediaTokens = organizationUnitSettingsDao
                            .getFbTokensByCollection(
                                AGENT_SETTINGS_COLLECTION, 0,
                                batchSize - totalRecord );
                        totalRecord +=agentMediaTokens.size();
                        socialMediaTokensPaginated.setAgentsTokens( agentMediaTokens );
                    }
                }
            }
        } else if ( skipCount < companyTokensCount + regionTokensCount ) {

            List<SocialMediaTokenResponse> regionsMediaTokens = organizationUnitSettingsDao
                .getFbTokensByCollection( REGION_SETTINGS_COLLECTION,
                    (int) ( skipCount - companyTokensCount ), batchSize );
            totalRecord += regionsMediaTokens.size();

            socialMediaTokensPaginated.setRegionsTokens( regionsMediaTokens );

            if ( totalRecord < batchSize ) {
                List<SocialMediaTokenResponse> branchMediaTokens = organizationUnitSettingsDao
                    .getFbTokensByCollection( BRANCH_SETTINGS_COLLECTION, 0,
                        batchSize - totalRecord );
                totalRecord += branchMediaTokens.size();
                socialMediaTokensPaginated.setBranchesTokens( branchMediaTokens );

                if ( totalRecord < batchSize ) {
                    List<SocialMediaTokenResponse> agentMediaTokens = organizationUnitSettingsDao
                        .getFbTokensByCollection( AGENT_SETTINGS_COLLECTION, 0,
                            batchSize - totalRecord );
                    totalRecord +=agentMediaTokens.size();
                    socialMediaTokensPaginated.setAgentsTokens( agentMediaTokens );
                }
            }
        } else if ( skipCount < companyTokensCount + regionTokensCount + branchTokensCount ) {
            List<SocialMediaTokenResponse> branchMediaTokens = organizationUnitSettingsDao.getFbTokensByCollection(
                BRANCH_SETTINGS_COLLECTION,
                (int) ( skipCount - ( companyTokensCount + regionTokensCount ) ), batchSize );
            totalRecord += branchMediaTokens.size();
            socialMediaTokensPaginated.setBranchesTokens( branchMediaTokens );

            if ( totalRecord < batchSize ) {
                List<SocialMediaTokenResponse> agentMediaTokens = organizationUnitSettingsDao
                    .getFbTokensByCollection( AGENT_SETTINGS_COLLECTION, 0,
                        batchSize - totalRecord );
                totalRecord +=agentMediaTokens.size();
                socialMediaTokensPaginated.setAgentsTokens( agentMediaTokens );
            }
        } else if ( skipCount < companyTokensCount + regionTokensCount + branchTokensCount + agentTokensCount ) {
            List<SocialMediaTokenResponse> agentMediaTokens = organizationUnitSettingsDao.getFbTokensByCollection(
                AGENT_SETTINGS_COLLECTION,
                (int) ( skipCount - ( companyTokensCount + regionTokensCount + branchTokensCount ) ), batchSize );
            totalRecord +=agentMediaTokens.size();
            socialMediaTokensPaginated.setAgentsTokens( agentMediaTokens );
        }

        socialMediaTokensPaginated.setTotalRecord(totalRecord);

        setCompanyIdsMap( socialMediaTokensPaginated );

        LOG.info( "End of method fetchSocialMediaTokensResponse" );
        return socialMediaTokensPaginated;
    }


    @Override public BranchVO getBranchDetails( long iden ) throws InvalidInputException, NoRecordsFetchedException
    {
        BranchVO branchVO;
        if ( iden <= 0l ) {
            throw new InvalidInputException( "Invalid branch id. : " + iden );
        }
        Branch branch = branchDao.findById( Branch.class, iden );
        if ( branch == null ) {
            throw new NoRecordsFetchedException( "No branch present in db for branchId : " + iden );
        } else {
             branchVO = new BranchVO();
            branchVO.setBranchId( iden );
            branchVO.setBranch( branch.getBranchName() );
            branchVO.setRegionId( branch.getRegion().getRegionId() );
            branchVO.setRegionName( branch.getRegion().getRegion() );
        }
        return branchVO;
    }


    @Override
    public List<OrganizationUnitIds> getDetailsFromPlaceId( String placeId ) throws InvalidInputException, ProfileNotFoundException
    {
        String placeIdKey = "socialMediaTokens.googleBusinessToken.googleBusinessLink";
        placeId = "https://search.google.com/local/writereview?placeid=".concat( placeId );
        List<OrganizationUnitIds> ouIdList = new ArrayList<>();
        // Agent Settings
        List<OrganizationUnitSettings> agentSettingsList = organizationUnitSettingsDao.getOrganizationSettingsByKey( placeIdKey,
            placeId, CommonConstants.AGENT_SETTINGS_COLLECTION );
        if ( agentSettingsList != null && agentSettingsList.size() > 0 ) {
            for ( OrganizationUnitSettings agentSettings : agentSettingsList ) {
                OrganizationUnitIds ouId = new OrganizationUnitIds();
                User user = userDao.findById( User.class, agentSettings.getIden() );
                Map<String, Long> hierarchyMap = profileManagementService.getHierarchyDetailsByEntity( CommonConstants.AGENT_ID,
                    agentSettings.getIden() );
                ouId.setCompleteProfileUrl( agentSettings.getCompleteProfileUrl() );
                ouId.setAgentName( user.getFirstName().concat( " " ).concat( user.getLastName() ) );
                ouId.setAgentId( agentSettings.getIden() );
                ouId.setBranchId( hierarchyMap.get( CommonConstants.BRANCH_ID_COLUMN ) );
                ouId.setRegionId( hierarchyMap.get( CommonConstants.REGION_ID_COLUMN ) );
                ouId.setCompanyId( hierarchyMap.get( CommonConstants.COMPANY_ID_COLUMN ) );
                ouId.setProfileType( CommonConstants.AGENT_SETTINGS_COLLECTION );
                ouId.setSocialMediaLastFetched( agentSettings.getSocialMediaLastFetched() );
                ouIdList.add( ouId );
            }
        }

        // Branch Settings
        List<OrganizationUnitSettings> branchSettingsList = organizationUnitSettingsDao
            .getOrganizationSettingsByKey( placeIdKey, placeId, CommonConstants.BRANCH_SETTINGS_COLLECTION );
        if ( branchSettingsList != null && branchSettingsList.size() > 0 ) {
            for ( OrganizationUnitSettings branchSettings : branchSettingsList ) {
                OrganizationUnitIds ouId = new OrganizationUnitIds();
                Branch branch = branchDao.findById( Branch.class, branchSettings.getIden() );
                ouId.setBranchId( branch.getBranchId() );
                ouId.setRegionId( branch.getRegion().getRegionId() );
                ouId.setCompanyId( branch.getCompany().getCompanyId() );
                ouId.setProfileType( CommonConstants.BRANCH_SETTINGS_COLLECTION );
                ouId.setSocialMediaLastFetched( branchSettings.getSocialMediaLastFetched() );
                ouIdList.add( ouId );
            }
        }

        // Region Settings
        List<OrganizationUnitSettings> regionSettingsList = organizationUnitSettingsDao
            .getOrganizationSettingsByKey( placeIdKey, placeId, CommonConstants.REGION_SETTINGS_COLLECTION );
        if ( regionSettingsList != null && regionSettingsList.size() > 0 ) {
            for ( OrganizationUnitSettings regionSettings : regionSettingsList ) {
                OrganizationUnitIds ouId = new OrganizationUnitIds();
                Region region = regionDao.findById( Region.class, regionSettings.getIden() );
                ouId.setRegionId( region.getRegionId() );
                ouId.setCompanyId( region.getCompany().getCompanyId() );
                ouId.setProfileType( CommonConstants.REGION_SETTINGS_COLLECTION );
                ouId.setSocialMediaLastFetched( regionSettings.getSocialMediaLastFetched() );
                ouIdList.add( ouId );
            }
        }

        // Company Settings
        List<OrganizationUnitSettings> companySettingsList = organizationUnitSettingsDao
            .getOrganizationSettingsByKey( placeIdKey, placeId, CommonConstants.COMPANY_SETTINGS_COLLECTION );
        if ( companySettingsList != null && companySettingsList.size() > 0 ) {
            for ( OrganizationUnitSettings companySettings : companySettingsList ) {
                OrganizationUnitIds ouId = new OrganizationUnitIds();
                ouId.setCompanyId( companySettings.getIden() );
                ouId.setProfileType( CommonConstants.COMPANY_SETTINGS_COLLECTION );
                ouId.setSocialMediaLastFetched( companySettings.getSocialMediaLastFetched() );
                ouIdList.add( ouId );
            }
        }
        return ouIdList;
    }


    @Override
    public boolean updateSocialMediaLastFetched( String profile, long iden, String socialMedia, long current, long previous )
        throws InvalidInputException
    {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "method updateSocialMediaLastFetched() called" );
        }
        String fieldToUpdate;
        Object value;

        if(profile == null || profile.isEmpty()) throw  new InvalidInputException( "Collection name is invalid" );

        else if(iden <= 0) throw  new InvalidInputException( "iden is invalid" );

        else if(current < 0 || previous < 0) throw  new InvalidInputException( " Invalid current/previous values " );

        switch ( socialMedia ) {
            case CommonConstants.SURVEY_SOURCE_FACEBOOK :
                fieldToUpdate = "socialMediaLastFetched.fbReviewLastFetched";
                value = new FBReviewLastFetched(previous,current);
                break;
            case CommonConstants.SURVEY_SOURCE_GOOGLE :
                fieldToUpdate = "socialMediaLastFetched.googleReviewLastFetched";
                value = new GoogleReviewLastFetched(previous, current);
                break;

            default :
                LOG.error( "Invalid socialMedia" );
                throw new InvalidInputException( "Invalid socialMedia {}", socialMedia );
        }
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden( fieldToUpdate, value, iden, profile );
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "method updateSocialMediaToken() completed" );
        }
        return true;
    }


    @Override public boolean resetSocialMediaLastFetched( String profile, long iden, String socialMedia )
        throws InvalidInputException
    {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "method resetSocialMediaLastFetched() called" );
        }

        String fieldToUpdate = null;
        Object value =null;

        if(profile == null || profile.isEmpty()) throw  new InvalidInputException( "Collection name is invalid" );

        else if(iden <= 0) throw  new InvalidInputException( "iden is invalid" );

        /* First find the previous value of the socialMediaLastFetched to update the current value */
        SocialMediaLastFetched socialMediaLastFetched = organizationUnitSettingsDao.fetchSocialMediaLastFetched(iden,profile)
            .getSocialMediaLastFetched();

        switch ( socialMedia ){
            case CommonConstants.SURVEY_SOURCE_FACEBOOK :
                if(socialMediaLastFetched.getFbReviewLastFetched() != null ){
                    fieldToUpdate = KEY_FBREVIEW_LASTFETCHED_CURRENT;
                    value = socialMediaLastFetched.getFbReviewLastFetched().getPrevious();
                }
                break;
            case CommonConstants.SURVEY_SOURCE_GOOGLE :
                if(socialMediaLastFetched.getGoogleReviewLastFetched() != null ){
                    fieldToUpdate = KEY_GOOGLE_REVIEW_LAST_FETCHED_CURRENT;
                    value = socialMediaLastFetched.getGoogleReviewLastFetched().getPrevious();
                }
                break;

            default:  LOG.error( "Invalid socialMedia {}", socialMedia );
                throw new InvalidInputException( "Invalid socialMedia {}", socialMedia );
        }

        if(fieldToUpdate != null && value!= null)
            organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden( fieldToUpdate, value, iden, profile );

        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "method updateSocialMediaToken() completed" );
        }
        return true;
    }

    //Unsets the key of a particular collection for a given iden
    @Override
    public boolean unsetKey( long iden, String keyToUpdate, String collectionName )
        throws InvalidInputException
    {
        if ( iden <= 0 ) {
            throw new InvalidInputException( "Company settings cannot be null." );
        }

        LOG.info( "unsetting key {} for {} with iden {}", keyToUpdate, collectionName , iden);
        organizationUnitSettingsDao.removeKeyInOrganizationSettings( iden, keyToUpdate, collectionName );
        LOG.debug( "Updated the record successfully" );

        return true;
    }

    @Override public ManageTeamBulkResponse assignBranchToUsers( List<Long> userIds, long adminId, long branchId )
        throws InvalidInputException
    {
        if( userIds == null || userIds.isEmpty() ) {
            throw new InvalidInputException( "Invalid userIds provided !!!" );
        }
        if(adminId <= 0l )
            throw new InvalidInputException( "Invalid adminId : "+ adminId );
        if(branchId <= 0l)
            throw new InvalidInputException( "Invalid branchId : "+ branchId );


        List<ManageTeamBulkActionVo> successItems = new ArrayList<>();
        List<ManageTeamBulkActionVo> failedItems = new ArrayList<>();
        ManageTeamBulkActionVo manageTeamBulkActionVo = null;

        //get the admin details by using adminID
        User admin = userManagementService.getUserByUserId( adminId );

        //get the regionId by quering from branch
        long regionId = branchDao.getRegionIdByBranchId( branchId );

        for ( long userId : userIds ) {
            try {
                User assigneeUser = userManagementService.getUserByUserId( userId );
                manageTeamBulkActionVo = new ManageTeamBulkActionVo();
                manageTeamBulkActionVo.setUserId( userId );

                this.assignBranchToUser( admin, branchId, regionId, assigneeUser );

                manageTeamBulkActionVo.setMessage( "User has been assigned successfully to given branch" );
                successItems.add( manageTeamBulkActionVo );

            } catch ( InvalidInputException | SolrException exception ) {
                manageTeamBulkActionVo.setMessage( exception.getMessage() );
                failedItems.add( manageTeamBulkActionVo );
            }
        }
        return new ManageTeamBulkResponse(successItems, failedItems);
    }


    @Override public ManageTeamBulkResponse assignRegionToUsers( List<Long> userIds, long adminId, long regionId )
        throws InvalidInputException, NoRecordsFetchedException
    {
        if( userIds == null || userIds.isEmpty() ) {
            throw new InvalidInputException( "Invalid userIds provided !!!" );
        }
        if(adminId <= 0l )
            throw new InvalidInputException( "Invalid adminId : "+ adminId );
        if(regionId <= 0l)
            throw new InvalidInputException( "Invalid regionId : "+ regionId );


        List<ManageTeamBulkActionVo> successItems = new ArrayList<>();
        List<ManageTeamBulkActionVo> failedItems = new ArrayList<>();
        ManageTeamBulkActionVo manageTeamBulkActionVo = null;

        //get the admin details by using adminID
        User admin = userManagementService.getUserByUserId( adminId );

        for ( long userId : userIds ) {
            try {
                User assigneeUser = userManagementService.getUserByUserId( userId );
                manageTeamBulkActionVo = new ManageTeamBulkActionVo();
                manageTeamBulkActionVo.setUserId( userId );

                this.assignRegionToUser( admin, regionId, assigneeUser );

                manageTeamBulkActionVo.setMessage( "User has been assigned successfully to given region" );
                successItems.add( manageTeamBulkActionVo );

            } catch ( InvalidInputException | SolrException exception ) {
                manageTeamBulkActionVo.setMessage( exception.getMessage() );
                failedItems.add( manageTeamBulkActionVo );
            }
        }
        return new ManageTeamBulkResponse(successItems, failedItems);
    }

    /**
     * Save the notification details in the {@link OrganizationUnitSettings}
     * But make sure the same notification is not saved again avoiding
     * overwriting the {@link Notification#recievedOn} using {@link OrganizationUnitSettingsDao#fetchNotification(long, String)}
     * Calls {@link OrganizationUnitSettingsDao#saveNotification(long, Notification)}
     * @param companyId
     * @param errorMessage
     * @param receivedOn
     * @param type
     */
    @Override public void saveNotification( long companyId, String errorMessage, long receivedOn, String type )
        throws InvalidInputException
    {
        LOG.debug( "Saving notification details in COMPANY" );
        Notification notification = organizationUnitSettingsDao.fetchNotification( companyId, errorMessage );
        NotificationType notificationType;
        //if the error is not present then save it

        if( notification == null ){
            if(type.equalsIgnoreCase( NotificationType.ERROR.name() ))
                notificationType = NotificationType.ERROR;
            else if(type.equalsIgnoreCase( NotificationType.SUCCESS.name() ))
                notificationType = NotificationType.SUCCESS;
            else
                throw new InvalidInputException( " Invalid notification type {}", type );

            Notification newNotification = new Notification( errorMessage, receivedOn, notificationType, false );
            organizationUnitSettingsDao.saveNotification( companyId, newNotification );

        }
        //else ignore it
        else
            LOG.info( "Notification is already present!!! Ignoring" );
    }

    @Override public void updateAllowPartnerSurvey( OrganizationUnitSettings unitSettings, boolean allowPartnerSurvey )
    {
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(KEY_ALLOW_PARTNER_SURVEY,
            allowPartnerSurvey, unitSettings, COMPANY_SETTINGS_COLLECTION);
    }
    
    @Override
    public void updateAllowPartnerSurveyForCollection( OrganizationUnitSettings unitSettings, boolean allowPartnerSurvey, String collectionType ) {
        
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_ALLOW_PARTNER_SURVEY,
            allowPartnerSurvey, unitSettings, collectionType);
    }
    
    /**
     * Updates {@link Notification#isDisabled} to true
     * Calls {@link OrganizationUnitSettingsDao#updateParticularKeyOrganizationUnitSettingsByIden(String, Object, long, String)}
     * (long, Notification)}
     * @param companyId
     */
    @Override public void disableNotification( long companyId )
    {
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden(
            NOTIFICATION_ISDISABLED, true, companyId,
            COMPANY_SETTINGS_COLLECTION );
    }


    /**
     * Fetches all the necessary company statistics from the reporting DB
     * Calls {@link GenericReportingDao#executeNativeQuery(Class, Map, String)}
     * @param companyId
     * @return
     * @throws NoRecordsFetchedException,InvalidInputException
     */
    @Override public CompanyStatistics fetchCompanyStatistics( long companyId )
        throws NoRecordsFetchedException, InvalidInputException
    {
        LOG.debug( "Method fetchCompanyStatistics started for company {}", companyId );

        if(companyId <= 0)
            throw new InvalidInputException( "CompanyId :" + companyId + "  provided for fetching the companyStatistics is invalid");

        List<CompanyStatistics> companyStatisticsList = null;
        CompanyStatistics companyStatistics;
        Map<String, Object> queries = new HashMap<>(  );
        queries.put( CommonConstants.COMPANY_ID, companyId );

        try {
            companyStatisticsList = genericReportingDao.executeNativeQuery( CompanyStatistics.class,
                queries,SqlQueries.COMPANY_STATISTICS_QUERY);
        } catch ( Exception e ){
            LOG.error( " Error occured while trying to fetch company statistics for companyId : {} from reporting DB {} ",
                companyId, e.getMessage() );
            throw new NoRecordsFetchedException( "Something went wrong while fetching company statistics for companyId : " + companyId );
        }

        if(companyStatisticsList == null || companyStatisticsList.isEmpty()){
            throw new NoRecordsFetchedException( "No company statistics were found in reportingDB with companyId :" + companyId );
        }
            companyStatistics = companyStatisticsList.get( 0 );

            long totalHierarchyCount = companyStatistics.getUserCount() +
                companyStatistics.getRegionCount() + companyStatistics.getBranchCount() + 1; //here 1 denotes the company

            companyStatistics.setVerifiedPercent(
                (long) Math.ceil((companyStatistics.getVerifiedUserCount() * 1d) / companyStatistics.getUserCount()*100) );
            companyStatistics.setTwitterPercent(
                (long) Math.ceil(((companyStatistics.getTwitterConnectionCount()*1d) / companyStatistics.getUserCount())*100) );
            companyStatistics.setFacebookPercent(
                (long) Math.ceil(((companyStatistics.getFacebookConnectionCount()*1d) / companyStatistics.getUserCount())*100 ) );
            companyStatistics.setLinkedInPercent(
                (long) Math.ceil(((companyStatistics.getLinkedinConnectionCount()*1d) / companyStatistics.getUserCount())*100 ) );

            companyStatistics.setMissingPhotoPercentForUsers(
                (long) Math.ceil(((companyStatistics.getMissingPhotoCountForUsers()*1d) / companyStatistics.getUserCount()) * 100 ) );
            companyStatistics.setBranchGmbPercent(
                (long) Math.ceil(((companyStatistics.getBranchVerifiedGmb()*1d) / companyStatistics.getBranchCount()) * 100 ) );
            companyStatistics.setRegionGmbPercent(
                (long) Math.ceil(((companyStatistics.getRegionVerifiedGmb()*1d) / companyStatistics.getRegionCount()) * 100 ) );

        if( LOG.isDebugEnabled()) {
            LOG.debug( "Method fetchCompanyStatistics ended for company {}", companyStatistics );
        }
        return companyStatistics;
    }


    /**
     * This method does the following tasks
     * 1.assigns the user to the given branch
     * 2.Overrides the previous branch assignments in which the user is an agents (Note: Doesnot
     * override or remove the admin assignments of the user)
     * 3.Updates the user with the new assignments in solr
     * 4.Move the reviews
     * @param adminUser
     * @param branchId
     * @param regionId
     * @param assigneeUser
     * @throws InvalidInputException
     * @throws SolrException
     */
    public void assignBranchToUser( User adminUser, long branchId, long regionId, User assigneeUser )
        throws InvalidInputException, SolrException
    {
        if ( adminUser == null ) {
            throw new InvalidInputException( "Invalid adminUser !!!" );
        }
        if ( branchId <= 0l ) {
            throw new InvalidInputException( "Invalid branchId : " + branchId );
        }
        if ( regionId <= 0l ) {
            throw new InvalidInputException( "Invalid regionId : " + regionId + " for branchId : " + branchId  );
        }
        if ( assigneeUser == null ) {
            throw new InvalidInputException( "Invalid assigneeUser !!!" );
        }
        if(assigneeUser.getStatus() == CommonConstants.STATUS_INACTIVE){
            throw new InvalidInputException( "User is currently inactive or the user might be deleted !!!" );
        }
        if(adminUser.getCompany().getCompanyId() != assigneeUser.getCompany().getCompanyId())
            throw new InvalidInputException( "Admin and user doesn't belong to the same company" );

        if( LOG.isDebugEnabled() ){
            LOG.debug( "Method assignBranchToUser called for adminUser: {},  branchId: {}, regionId : {}, assigneeUser: {} " ,
                adminUser , branchId, regionId, assigneeUser );
        }

        if(branchDao.getCompanyIdsForBranchIds(Arrays.asList( branchId )  ).get( branchId ) != assigneeUser.getCompany().getCompanyId()){
            throw new InvalidInputException( "Branch to which the User is being assigned to does not belong to the same company "
                    + "as the user !!!");
        }

        List<UserProfile> userProfiles = assigneeUser.getUserProfiles();
        if ( userProfiles == null || userProfiles.isEmpty() ) {
            userProfiles = new ArrayList<>();
        }

        int profileMasterId = CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID;

        UserProfile userProfileNew = userManagementService.createUserProfile( assigneeUser, adminUser.getCompany(),
            assigneeUser.getEmailId(), assigneeUser.getUserId(), branchId, regionId, profileMasterId,
            CommonConstants.IS_PRIMARY_FALSE, CommonConstants.DASHBOARD_STAGE, CommonConstants.STATUS_ACTIVE,
            String.valueOf( adminUser.getUserId() ), String.valueOf( adminUser.getUserId() ) );

        String branchName = null;
        Branch branch ;

        UserProfile profile;

        if ( userProfiles != null && !userProfiles.isEmpty() ) {

            for (final Iterator iterator = userProfiles.iterator(); iterator.hasNext();) {
                profile = (UserProfile) iterator.next();

                //get the branch name using the branchId
                if(profile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
                    branch = branchDao.findById( Branch.class, profile.getBranchId() );
                    branchName = branch.getBranch();

                    //remove the previous assignments(agent profile) for the user except for the admin assignments
                    if ( !branchName.equalsIgnoreCase( CommonConstants.DEFAULT_BRANCH_NAME )) {
                        userManagementService.removeUserProfile( profile.getUserProfileId() );
                        iterator.remove();
                    }
                }
            }

        }

        //check if profile will be primary or not
        int isPrimary = checkWillNewProfileBePrimary( userProfileNew, userProfiles );
        userProfileNew.setIsPrimary( isPrimary );

        //move reviews
        if ( userProfileNew.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
            surveyDetailsDao.updateBranchIdRegionIdForAllSurveysOfAgent( assigneeUser.getUserId(), branchId, regionId );
        }

        userProfileDao.save( userProfileNew );

        if ( assigneeUser.getIsAtleastOneUserprofileComplete() == CommonConstants.STATUS_INACTIVE ) {
            assigneeUser.setIsAtleastOneUserprofileComplete( CommonConstants.STATUS_ACTIVE );
            userDao.update( assigneeUser );
        }

        /**
         * add newly created user profile to the list of user profiles in user object
         */
        userProfiles.add( userProfileNew );
        assigneeUser.setUserProfiles( userProfiles );
        userManagementService.setProfilesOfUser( assigneeUser );

        AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( assigneeUser.getUserId() );
        assigneeUser.setProfileName( agentSettings.getProfileName() );
        assigneeUser.setProfileUrl( agentSettings.getProfileUrl() );
        solrSearchService.addUserToSolr( assigneeUser );

        userManagementService.updateUserCountModificationNotification( assigneeUser.getCompany() );

        if( LOG.isDebugEnabled() ) {
            LOG.debug( "Method assignBranchToUser executed successfully" );
        }
    }


    /**
     * This method does the following
     * 1. Assigns a user to a region
     * 2.Remove the previous region assignments in which the user belongs to
     * 3.Move the reviews
     * 3.Update the user in the solr
     * (Note: this method doesnot assign the user as an admin and
     * doesnot remove the region assignments in which the user is an admin )
     * @param adminUser
     * @param regionId
     * @param assigneeUser
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     * @throws SolrException
     */
    @Transactional
    @Override
    public void assignRegionToUser( User adminUser, long regionId, User assigneeUser )
        throws InvalidInputException, SolrException, NoRecordsFetchedException
    {
        if ( adminUser == null ) {
            throw new InvalidInputException( "Admin user is null in assignRegionToUser" );
        }
        if ( regionId <= 0l ) {
            throw new InvalidInputException( "Region id is invalid in assignRegionToUser" );
        }
        if ( assigneeUser == null ) {
            throw new InvalidInputException( "assignee user is null in assignRegionToUser" );
        }
        if(assigneeUser.getStatus() == CommonConstants.STATUS_INACTIVE){
            throw new InvalidInputException( "assignee user either inactive or deleted from company!!!" );
        }
        if(adminUser.getCompany().getCompanyId() != assigneeUser.getCompany().getCompanyId())
            throw new InvalidInputException( "Admin and user doesn't belong to the same company" );

        if( LOG.isDebugEnabled() ){
            LOG.debug( "Method to assignRegionToUser called for regionId : {}  and assigneeUser : {}" + regionId,
                assigneeUser.getUserId() );
        }

        List<UserProfile> userProfiles = assigneeUser.getUserProfiles();
        if ( userProfiles == null || userProfiles.isEmpty() ) {
            userProfiles = new ArrayList<>();
        }

        int profileMasterId = CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID;


        Branch defaultBranch = getDefaultBranchForRegion( regionId );

        if( defaultBranch.getCompany().getCompanyId() != assigneeUser.getCompany().getCompanyId() ){
            throw new InvalidInputException( "Region to which the User is being assigned to doesnot belong to the same company "
                + "as the user !!! ");
        }

        UserProfile userProfileNew = userManagementService.createUserProfile( assigneeUser, adminUser.getCompany(),
            assigneeUser.getEmailId(), assigneeUser.getUserId(), defaultBranch.getBranchId(), regionId, profileMasterId,
            CommonConstants.IS_PRIMARY_FALSE, CommonConstants.DASHBOARD_STAGE, CommonConstants.STATUS_ACTIVE,
            String.valueOf( adminUser.getUserId() ), String.valueOf( adminUser.getUserId() ) );

        UserProfile profile;
        String branchName;
        String regionName;

        if ( userProfiles != null && !userProfiles.isEmpty() ) {

            for (final Iterator iterator = userProfiles.iterator(); iterator.hasNext();) {
                profile = (UserProfile) iterator.next();

                //get the branch name and region name using the branchId
                if(profile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
                    Map<String, String> regionBranchMap = branchDao.getBranchAndRegionName( profile.getRegionId(), profile.getBranchId());
                    branchName = regionBranchMap.get( CommonConstants.BRANCH_NAME_COLUMN );
                    regionName = regionBranchMap.get( CommonConstants.REGION_COLUMN );

                    if( LOG.isDebugEnabled() ) {
                        LOG.debug( " ProfileId : {}, Branch : {} ,RegionName : {}", profile.getProfilesMaster().getProfileId(),
                            branchName,regionName );
                    }

                    //remove the previous assignments(agent profile) for the user except for the admin assignments
                    if ( !regionName.equalsIgnoreCase( CommonConstants.DEFAULT_REGION_NAME )
                        && branchName.equalsIgnoreCase( CommonConstants.DEFAULT_BRANCH_NAME ) ) {
                        userManagementService.removeUserProfile( profile.getUserProfileId() );
                        iterator.remove();
                    }
                }
            }
        }

        //check if new profile will be primary or not
        int isPrimary = checkWillNewProfileBePrimary( userProfileNew, userProfiles );
        userProfileNew.setIsPrimary( isPrimary );

        //move reviews
        if ( userProfileNew.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
            surveyDetailsDao.updateBranchIdRegionIdForAllSurveysOfAgent( assigneeUser.getUserId(), defaultBranch.getBranchId(),
                regionId );
        }

        userProfileDao.save( userProfileNew );
        if ( assigneeUser.getIsAtleastOneUserprofileComplete() == CommonConstants.STATUS_INACTIVE ) {
            if( LOG.isDebugEnabled() ){
                LOG.debug( "Updating isAtleastOneProfileComplete as active for user : " + assigneeUser.getUserId() );
            }
            assigneeUser.setIsAtleastOneUserprofileComplete( CommonConstants.STATUS_ACTIVE );
            userDao.update( assigneeUser );
        }

        /**
         * add newly created user profile to the list of user profiles in user object
         */
        userProfiles.add( userProfileNew );
        assigneeUser.setUserProfiles( userProfiles );
        userManagementService.setProfilesOfUser( assigneeUser );

        AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( assigneeUser.getUserId() );
        assigneeUser.setProfileName( agentSettings.getProfileName() );
        assigneeUser.setProfileUrl( agentSettings.getProfileUrl() );
        solrSearchService.addUserToSolr( assigneeUser );

        userManagementService.updateUserCountModificationNotification( assigneeUser.getCompany() );

        if( LOG.isDebugEnabled() ) {
            LOG.debug(
                "Method to assignRegionToUser finished for regionId : " + regionId + " and userId : " + assigneeUser.getUserId() );
        }

    }

    @Override
    @Transactional
    public long getCompanyByProfileName( String profileName )
    {
        try {
    	OrganizationUnitSettings companySettings = profileManagementService.getCompanyProfileByProfileName( profileName );
         if( companySettings != null)
         	return companySettings.getCompanyId();
         
        }catch (ProfileNotFoundException profileNotFoundException) {
            LOG.error( "Company profile not found", profileNotFoundException );
        }
        return 0l;
    }
    
    @Override
    public void updateCompanySettings(OrganizationUnitSettings companySettings, String columnName, String columnValue) {
        
        LOG.info( "Method to update company settings, OrganizationManagementServiceImpl.updateCompanySettings() started." );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings( columnName, columnValue, companySettings, CommonConstants.COMPANY_SETTINGS_COLLECTION );
        LOG.info( "Method to update company settings, OrganizationManagementServiceImpl.updateCompanySettings() finished." );
    }


    /**
     * This method fetches necessary info required to show on admin dashboard from mongo
     * Calls {@link OrganizationUnitSettingsDao#fetchOrganizationUnitSettingsById(long, String, List)}
     * @param companyId
     * @return
     * @throws InvalidInputException 
     */
    @Override 
    public CustomerSuccessInformation fetchCustomerSuccessInformation( long companyId )
        throws NoRecordsFetchedException, InvalidInputException
    {
        if(companyId <=0)
            throw new InvalidInputException( "CompanyId : " + companyId + "  provided for fetching the customer success information is invalid");

        OrganizationUnitSettings companySettings =  organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( companyId,
            COMPANY_SETTINGS_COLLECTION, Arrays.asList( KEY_GOOGLE_BUSINESS_SOCIAL_MEDIA_TOKEN, KEY_FACEBOOK_PIXEL_SOCIAL_MEDIA_TOKEN,
                KEY_CREATED_BY, KEY_MODIFIED_BY, KEY_CREATED_ON, KEY_MODIFIED_ON, KEY_CUSTOMER_SUCCESS_ID, KEY_CUSTOMER_SUCCESS_OWNER,
                KEY_CUSTOMER_SUCCESS_NAME, KEY_CONTACT_DETAILS, KEY_RVP, KEY_ACCOUNT_DISABLED, KEY_CLOSED_DATE, KEY_TMC_CLIENT,
                KEY_SURVEY_DATA_SOURCE, KEY_DBA_FOR_COMPANY, KEY_POTENTIAL, KEY_GAP_ANALYSIS_DATE, KEY_CUSTOMER_SETTINGS, KEY_SERVICES_SOLD,
                KEY_TRANSFER_REVIEW_POLICY, KEY_TAG, KEY_REALTOR_SURVEYS, KEY_HAPPY_WORKFLOW_SETTINGS, KEY_PRIMARY_POC, KEY_POC2, KEY_EMAIL,
                KEY_EMAIL_POC2,KEY_SECONDARY_EMAIL, KEY_PHONE_POC2, KEY_PHONE, KEY_LAST_CONVERSATION_DATE ) );

        if(companySettings == null) {
            throw new NoRecordsFetchedException( "No record found in mongo for companyId : "+ companyId );
        }
        
        CustomerSuccessInformation customerSuccessInfo = new CustomerSuccessInformation();
        customerSuccessInfo.setCustomerSuccessName(companySettings.getCustomerSuccessName());
        customerSuccessInfo.setCustomerSuccessId(companySettings.getCustomerSuccessId());
        customerSuccessInfo.setCustomerSuccessOwner( companySettings.getCustomerSuccessOwner() );
        customerSuccessInfo.setCompanyName( companySettings.getContact_details().getName() );
        customerSuccessInfo.setClosedDate( companySettings.getClosedDate() );
        customerSuccessInfo.setRvp( companySettings.getRvp() );
        customerSuccessInfo.setTmcClient( companySettings.getTmcClient() );
        customerSuccessInfo.setSurveyDataSource( companySettings.getSurveyDataSource() );
        customerSuccessInfo.setDbaForCompany( companySettings.getDbaForCompany() );
        customerSuccessInfo.setPotential( companySettings.getPotential() );
        customerSuccessInfo.setCustomerSettings( companySettings.getCustomerSettings() );
        customerSuccessInfo.setGapAnalysisDate( companySettings.getGapAnalysisDate() );
        customerSuccessInfo.setServicesSold( companySettings.getServicesSold() );
        customerSuccessInfo.setTransferReviewPolicy( companySettings.getTransferReviewPolicy() );
        customerSuccessInfo.setTag( companySettings.getTag() );
        customerSuccessInfo.setRealtorSurveys( companySettings.getRealtorSurveys() );
        customerSuccessInfo.setHappyWorkflowSettings( companySettings.getHappyWorkflowSettings() );
        customerSuccessInfo.setCreatedOn( companySettings.getCreatedOn()  );
        customerSuccessInfo.setModifiedOn( companySettings.getModifiedOn() );
        customerSuccessInfo.setCreatedBy( userDao.getUserName( Long.parseLong( companySettings.getCreatedBy() ) ));
        customerSuccessInfo.setModifiedBy( userDao.getUserName( Long.parseLong( companySettings.getModifiedBy() ) ) );
        customerSuccessInfo.setPrimaryPoc( companySettings.getPrimaryPoc() );
        customerSuccessInfo.setPoc2( companySettings.getPoc2() );
        customerSuccessInfo.setEmail( companySettings.getEmail() );
        customerSuccessInfo.setEmailPoc2( companySettings.getEmailPoc2() );
        customerSuccessInfo.setSecondaryEmail( companySettings.getSecondaryEmail() );
        customerSuccessInfo.setPhonePoc2( companySettings.getPhonePoc2() );
        customerSuccessInfo.setPhone( companySettings.getPhone() );
        customerSuccessInfo.setLastConversationDate( companySettings.getLastConversationDate() );

        //Get OwnerOfCompany
        User user = null;
        
        List<UserProfile> userProfiles = userProfileDao.getCompanyAdminForCompanyId( companyId );
        if( userProfiles != null && !userProfiles.isEmpty() && userProfiles.get( 0 ) != null ) {
        
            user = userProfiles.get( 0 ).getUser();
        }

        //Set ProfileCompletion
        customerSuccessInfo.setProfileCompletion( dashboardServiceImpl.getProfileCompletionPercentage( user,
            CommonConstants.COMPANY_ID_COLUMN, companyId, companySettings ) ) ;

        //Set CompanyGmb
        if(companySettings.getSocialMediaTokens()!= null &&
            companySettings.getSocialMediaTokens().getGoogleBusinessToken() != null &&
            StringUtils.isNotEmpty( companySettings.getSocialMediaTokens().getGoogleBusinessToken().getGoogleBusinessLink() ) ){
            customerSuccessInfo.setCompanyGmb( companySettings.getSocialMediaTokens().getGoogleBusinessToken().getGoogleBusinessLink() );
        } else {
            customerSuccessInfo.setCompanyGmb( CommonConstants.STATUS_NO );
        }

        //Set PixelCampaign
        if(companySettings.getSocialMediaTokens()!= null &&
            companySettings.getSocialMediaTokens().getFacebookPixelToken() != null &&
            StringUtils.isNotEmpty( companySettings.getSocialMediaTokens().getFacebookPixelToken().getPixelId() ) ){
            customerSuccessInfo.setPixelCampaign(CommonConstants.STATUS_YES);
        } else {
            customerSuccessInfo.setPixelCampaign( CommonConstants.STATUS_NO );
        }

        //Set AccountStatus
        customerSuccessInfo.setAccountStatus( companySettings.getIsAccountDisabled() ? CommonConstants.INACTIVE : CommonConstants.ACTIVE );

        return customerSuccessInfo;
    }


    /**
     * Updates the company settings of the provided key with given value
     * Calls {@link OrganizationUnitSettingsDao#updateParticularKeyOrganizationUnitSettingsByIden(String, Object, long, String)}
     * @param companyId
     * @param columnName
     * @param columnValue
     */
    @Override
    public boolean enableIncompleteSurveyDeleteToggle(long companyId, boolean incompleteSurveyDeleteFlag) throws InvalidInputException, NoRecordsFetchedException
    {
    	LOG.debug( "method enableIncompleteSurveyDeleteToggle() started for companyId : {}", companyId);

    	if ( companyId <= 0 ) {
    		LOG.warn( "companyId is invalid: {}", companyId);
    		throw new InvalidInputException( "companyId is invalid" );
    	}

    	OrganizationUnitSettings unitSettings = getCompanySettings( companyId );

    	if ( unitSettings == null ) {
    		LOG.warn( "OrganizationSettings are not fournd for company id {}", companyId );
    		throw new InvalidInputException( "OrganizationSettings are not fournd for company id " +companyId );
    	}
    	// update incomplete survey delete flag
    	organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
    			INCOMPLETE_SURVEY_DELETE_ENABLED, incompleteSurveyDeleteFlag, unitSettings,
    			CommonConstants.COMPANY_SETTINGS_COLLECTION );

    	LOG.debug( "method enableIncompleteSurveyDeleteToggle() finished." );
    	return true;
    }


    @Override public void updateCompanySettings( long companyId, String columnName, Object columnValue )
    {
        LOG.debug( "Method to update company settings using companyId started." );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden( columnName,
            columnValue, companyId, CommonConstants.COMPANY_SETTINGS_COLLECTION );
        LOG.debug( "Method to update company settings using companyId finished" );
    }
    
    @Override
    public List<UserVo> getSocialSurveyAdmins() {
        
        LOG.debug( "Method to get all social survey admins getSocialSurveyAdmins() started." );

        List<UserVo> validSSAdmins = new ArrayList<>(  );
        try {
            validSSAdmins = genericDao.executeNativeQuery( UserVo.class, new HashMap<String, Object>(  ), SqlQueries.SS_ADMINS_QUERY );
        }
        catch(Exception exception) {
            
            LOG.error( "Exception caught in getSocialSurveyAdmins() ", exception );
        }

        if(LOG.isDebugEnabled())
            LOG.debug( "Method to get all social survey admins getSocialSurveyAdmins() finished." );

        return validSSAdmins;
    }
    
    @Override
    public String updateCustomerInformation(long companyId, String keyToBeUpdated, Object value, long modifiedBy) {

        if(LOG.isDebugEnabled())
            LOG.debug( "Method to update customer information started for companyId : {} ", companyId );
        
        String message = null;
        
        try {
            
            if(!keyList.contains( keyToBeUpdated )) {
                
                throw new InvalidInputException("Invalid key to be updated : " + keyToBeUpdated);
            }
            
            /*if(value == null) {
                
                throw new InvalidInputException( "Invalid value to be updated : '" + value + "' for key : '" + keyToBeUpdated + "'" );
            }*/

            if(keyToBeUpdated.equals( KEY_CLOSED_DATE ) ||
                keyToBeUpdated.equals( KEY_GAP_ANALYSIS_DATE ) ||
                keyToBeUpdated.equals( KEY_LAST_CONVERSATION_DATE ) ) {

                value = new Long( value.toString() );
            }
            else if(keyToBeUpdated.equals( KEY_CUSTOMER_SUCCESS_ID )) {
                
                String[] idName = value.toString().split( "," );
                if(idName.length != 2) {
                    
                    throw new InvalidInputException( "Invalid value to be updated : '" + value + "' for key : '" + keyToBeUpdated + "'" );
                }
                
                //Update customerSuccessName
                organizationUnitSettingsDao.updateParticularKeyCompanySettingsByIden( KEY_CUSTOMER_SUCCESS_NAME,
                    idName[1], companyId, modifiedBy );
                
                //Set customerSuccessId value
                value = Long.parseLong( idName[0] );
            }
            if( String.valueOf( value ).isEmpty() ){
                organizationUnitSettingsDao.removeKeyInOrganizationSettings(companyId, keyToBeUpdated, COMPANY_SETTINGS_COLLECTION, modifiedBy );
            }
            else {
                organizationUnitSettingsDao
                    .updateParticularKeyCompanySettingsByIden( keyToBeUpdated, value, companyId, modifiedBy );
            }
            message = "Successfully updated settings";

            if(LOG.isDebugEnabled())
                LOG.debug( "Method to update customer information finished for companyId : {} " , companyId );
        }
        catch(InvalidInputException invalidInputException) {
            
            LOG.error( "InvalidInputException while updating customer information for companyId : " + companyId, invalidInputException );
            message = "Some problem occurred while updating customer success information. Please try again later";
        }
        catch(Exception exception) {
            
            LOG.error( "Exception while updating customer information for companyId : " + companyId, exception );
            message = "Some problem occurred while updating customer success information. Please try again later";
        }
        return message;
    }


    /**
     * Returns the ss-admins notes for the given companyId.
     * For example a call of /fetchNotes/23/start/5/limit/10
     * implies the api will return a max total of 10 ss-admin notes starting from 5th record for companyId 23
     * @param companyId
     * @param startIndex
     * @param limit
     */
    @Override public List<Notes> fetchNotes( long companyId, long startIndex, long limit ) throws InvalidInputException
    {
        if(companyId<= 0)
            throw new InvalidInputException( "CompanyId : " + companyId + " provided for fetching the notes is invalid" );

        else if( startIndex < 0 || limit < 0)
            throw new InvalidInputException(" Invalid pagination params" + "start = " + startIndex + " and limit = "+ limit+" provided for fetching the notes ");

        final OrganizationUnitSettings unitSettings = organizationUnitSettingsDao
            .fetchOrganizationUnitSettingsById( companyId, CommonConstants.COMPANY_SETTINGS_COLLECTION,
                Arrays.asList( KEY_NOTES ) );

        if(unitSettings.getNotes() == null) return new ArrayList<>(  );
        return unitSettings.getNotes();
    }


    @Override public void updateSSAdminNotes( NotesVo notesVo ) throws NonFatalException
    {
            if(notesVo == null || notesVo.getCompanyId() <= 0 || StringUtils.isEmpty( notesVo.getNote() ))
                throw new InvalidInputException( "Provided notes " + notesVo + "is invalid. "  );

            Notes adminNotes = NotesVo.convertToNotesEntity( notesVo );

            organizationUnitSettingsDao.updateParticularKeyArrayOrganizationUnitSettingsByIden( KEY_NOTES,adminNotes,
               notesVo.getCompanyId(), CommonConstants.COMPANY_SETTINGS_COLLECTION );

    }

    @Override
    public String updateOrganizationSettingsByIdAndBooleanValue(long id, boolean value, String fieldToUpdate, String collection)
    {
        LOG.info( "Method to update organization settings for boolean fields started." );
        Map<String, Object> queryMap = new HashMap<>();
        Map<String, Object> updateMap = new HashMap<>();
        queryMap.put( KEY_IDEN, id );
        updateMap.put( fieldToUpdate, value );
        try {
            organizationUnitSettingsDao.updateOrganizationSettingsByQuery( queryMap, updateMap, collection );
            LOG.info( SUCCESS_MESSAGE + " in updateOrganizationSettings()" );
            return "true";
        } catch ( Exception e ) {
            LOG.error( "updateOrganizationSettingsByIdAndBooleanValue() failed to update mongo due to " + e );
            return "false";
        }
    }

    @Override
	public Map<String, String> getStateCodeNameMap() {
		LOG.info("getStateCodeNameMap start");
		try {
			List<StateLookup> stateLookupList = stateLookupDao.findAll(StateLookup.class);
			if(stateLookupList != null && !stateLookupList.isEmpty()) {
				LOG.info("Generating stateCOdeName map");
				Map<String, String> stateCodeMap = new HashMap<String, String>();
				for(StateLookup stateLookup : stateLookupList) {
					stateCodeMap.put(stateLookup.getStatecode(), stateLookup.getStatename());
				}
				return stateCodeMap;
			}
		}catch(Exception e) {
			LOG.error("Caught exception in getStateCodeNameMap " , e);
		}
		return null;
	}
	
	public String getStateCodeByStateName(String stateName) {
		LOG.info("getStateCodeByStateName start");
		try {
			List<StateLookup> statelookup = stateLookupDao.findByColumn(StateLookup.class, "statename", stateName);
			if(statelookup != null && !statelookup.isEmpty()) {
				return statelookup.get(0).getStatecode();
			}
		}catch(Exception e) {
			LOG.error("Caught exception in getStateCodeByStateName " , e);
		}
		return null;
	}

	@Override
    public String saveLinkedInProfileUrl( String entityType, long entityId, String linkedInprofileUrl ) throws InvalidInputException
    {
        LOG.info( "saveLinkedInProfileUrl start" );
        
        if(StringUtils.isEmpty( linkedInprofileUrl )) {
            LOG.warn("LinkedIn profile Url field cann't be empty");
            throw new InvalidInputException( "LinkedIn profile Url field cann't be empty" );
        }

        String profileUrl = setLinkedInUrl( entityType, entityId, linkedInprofileUrl );
        LOG.info( "saveLinkedInProfileUrl end" );
        return profileUrl;
    }
	
	private String setLinkedInUrl(String entityType, long entityId, String linkedInprofileUrl) throws InvalidInputException{
	    String collectionName = null;
        OrganizationUnitSettings unitSettings = null;
        switch ( entityType ) {
            case CommonConstants.COMPANY_ID_COLUMN:
                collectionName = COMPANY_SETTINGS_COLLECTION;
                unitSettings = getCompanySettings( entityId );
                break;
            case CommonConstants.REGION_ID_COLUMN:
                collectionName = REGION_SETTINGS_COLLECTION;
                unitSettings = getRegionSettings( entityId );
                break;
            case CommonConstants.BRANCH_ID_COLUMN:
                collectionName = BRANCH_SETTINGS_COLLECTION;
                try {
                    unitSettings = getBranchSettingsDefault( entityId );
                } catch ( NoRecordsFetchedException e1 ) {
                    LOG.warn( "No branch found for branch id {}", entityId );
                }
                break;
            case CommonConstants.AGENT_ID_COLUMN:
                collectionName = AGENT_SETTINGS_COLLECTION;
                unitSettings = userManagementService.getUserSettings( entityId );
                break;
            default:
                LOG.error( "Invalid entityType {}", entityType );
                throw new InvalidInputException( "Invalid entityType {}", entityType );
        }
        
        if(unitSettings == null){
            throw new InvalidInputException( "UnitSettings not found for  " + entityType+ " and " +entityId  ) ;
        }
        
        try {
            
            if(StringUtils.isNotEmpty( linkedInprofileUrl )) {
                if(unitSettings.getSocialMediaTokens() == null) {
                    unitSettings.setSocialMediaTokens( new SocialMediaTokens() );
                }
                
                unitSettings.getSocialMediaTokens().setLinkedInProfileUrl( linkedInprofileUrl );
                
                if(unitSettings.getSocialMediaTokens().getLinkedInV2Token() != null) {
                    unitSettings.getSocialMediaTokens().getLinkedInV2Token().setLinkedInPageLink( linkedInprofileUrl );
                }
                
                organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
                        KEY_SOCIAL_MEDIA_TOKENS, unitSettings.getSocialMediaTokens(), unitSettings, collectionName );
            } else {
                organizationUnitSettingsDao.removeLinkedInProfileUrlInUnitSettings( entityId, collectionName );
            }
            
        } catch ( Exception e ) {
            LOG.error( "Caught exception in saveLinkedInProfileUrl {}", e);
        }
        return linkedInprofileUrl;
	}

    @Override
    public String deleteLinkedInProfileUrl( String entityType, long entityId ) throws InvalidInputException
    {
        LOG.info( "deleteLinkedInProfileUrl start" );
        String url = setLinkedInUrl( entityType, entityId, null );
        LOG.info( "deleteLinkedInProfileUrl end" );
        return url;
    }
}
