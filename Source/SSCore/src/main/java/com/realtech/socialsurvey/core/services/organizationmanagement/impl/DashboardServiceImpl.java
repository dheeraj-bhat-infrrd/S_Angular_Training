package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.AgentRankingReportComparator;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.SocialPostsComparator;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.EmailAttachment;
import com.realtech.socialsurvey.core.entities.FeedStatus;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.entities.HierarchyUploadAggregate;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.SocialUpdateAction;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.DashboardService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.services.upload.HierarchyDownloadService;
import com.realtech.socialsurvey.core.utils.CommonUtils;
import com.realtech.socialsurvey.core.workbook.utils.WorkbookData;
import com.realtech.socialsurvey.core.workbook.utils.WorkbookOperations;


// JIRA SS-137 BY RM05:BOC
/**
 * Class with methods defined to show dash board of user.
 */
@Component
public class DashboardServiceImpl implements DashboardService, InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( DashboardServiceImpl.class );

    private static Map<String, Integer> weightageColumns;

    @Autowired
    private SurveyDetailsDao surveyDetailsDao;

    @Autowired
    private GenericDao<FeedStatus, Long> feedStatusDao;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private SurveyPreInitiationDao surveyPreInitiationDao;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private SocialManagementService socialManagementService;

    @Autowired
    private UserProfileDao userProfileDao;

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private GenericDao<FileUpload, Long> fileUploadDao;

    @Resource
    @Qualifier ( "branch")
    private BranchDao branchDao;

    @Autowired
    private HierarchyDownloadService hierarchyDownloadService;

    @Value ( "${FILE_DIRECTORY_LOCATION}")
    private String fileDirectoryLocation;

    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String adminEmailId;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String adminName;

    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;
    
    @Autowired
    private WorkbookOperations workbookOperations;

    @Autowired
    private WorkbookData workbookData;

    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private UserManagementService userManagementService;
    
    @Autowired
    private FileUploadService fileUploadService;


    @Transactional
    @Override
    public long getAllSurveyCount( String columnName, long columnValue, int numberOfDays ) throws InvalidInputException
    {
        LOG.info(
            "Get all survey count for " + columnName + " and value " + columnValue + " with number of days: " + numberOfDays );

        if ( columnName == null || columnName.isEmpty() ) {
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column name is null or empty" );
        }
        if ( columnValue <= 0l ) {
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column value is invalid" );
        }

        Calendar startTime = Calendar.getInstance();
        startTime.add( Calendar.DATE, -1 * numberOfDays );
        // strip the time component of start time
        startTime.set( Calendar.HOUR_OF_DAY, 0 );
        startTime.set( Calendar.MINUTE, 0 );
        startTime.set( Calendar.SECOND, 0 );
        startTime.set( Calendar.MILLISECOND, 0 );

        Timestamp startDate = null;
        Timestamp endDate = null;
        if ( numberOfDays >= 0 ) {
            startDate = new Timestamp( startTime.getTimeInMillis() );
            endDate = new Timestamp( System.currentTimeMillis() );
        }

        long completedSurveyCount = getCompletedSurveyCount( columnName, columnValue, startDate, endDate, true );
        // TODO: remove hard coding
        long companyId = -1;
        long agentId = -1;
        Set<Long> agentIds = null;
        if ( columnName.equals( "companyId" ) ) {
            // agent list will be null
            companyId = columnValue;
        } else if ( columnName.equals( "agentId" ) ) {
            // agent list will have one element, the agent id
            agentId = columnValue;
        } else if ( columnName.equals( "regionId" ) ) {
            agentIds = userProfileDao.findUserIdsByRegion( columnValue );
        } else if ( columnName.equals( "branchId" ) ) {
            agentIds = userProfileDao.findUserIdsByBranch( columnValue );
        }
        //long incompleteSurveyCount = surveyPreInitiationDao.getIncompleteSurveyCount(companyId, agentId, CommonConstants.STATUS_ACTIVE, startDate, endDate, agentIds);
        //JIRA SS-1350 begin
        long incompleteSurveyCount = 0;
        if ( companyId > 0l || agentId > 0l || ( agentIds != null && !agentIds.isEmpty() ) ) {
            incompleteSurveyCount = surveyPreInitiationDao.getIncompleteSurveyCount( companyId,
                agentId, new int[] { CommonConstants.STATUS_SURVEYPREINITIATION_DELETED,
                    CommonConstants.SURVEY_STATUS_PRE_INITIATED, CommonConstants.SURVEY_STATUS_INITIATED },
                startDate, endDate, agentIds );
        }
        //JIRA SS-1350 end
        LOG.debug( "Completed survey: " + completedSurveyCount );
        LOG.debug( "Incomplete survey: " + incompleteSurveyCount );
        return completedSurveyCount + incompleteSurveyCount;
    }


    @Transactional
    @Override
    public long getAllSurveyCountForStatistics( String columnName, long columnValue, int numberOfDays )
        throws InvalidInputException
    {
        LOG.info(
            "Get all survey count for {} and value {} with number of days: {}", columnName, columnValue, numberOfDays );

        if ( columnName == null || columnName.isEmpty() ) {
            LOG.warn( "Wrong input parameter : passed input parameter column name is null or empty" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column name is null or empty" );
        }
        if ( columnValue <= 0l ) {
            LOG.warn( "Wrong input parameter : passed input parameter column value is invalid" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column value is invalid" );
        }

        Calendar startTime = Calendar.getInstance();
        startTime.add( Calendar.DATE, -1 * numberOfDays );
        // strip the time component of start time
        startTime.set( Calendar.HOUR_OF_DAY, 0 );
        startTime.set( Calendar.MINUTE, 0 );
        startTime.set( Calendar.SECOND, 0 );
        startTime.set( Calendar.MILLISECOND, 0 );

        Timestamp startDate = null;
        Timestamp endDate = null;
        if ( numberOfDays >= 0 ) {
            startDate = new Timestamp( startTime.getTimeInMillis() );
            endDate = new Timestamp( System.currentTimeMillis() );
        }

        long completedSurveyCount = getCompletedSurveyCountForStatistics( columnName, columnValue, startDate, endDate, true );
        // TODO: remove hard coding
        long companyId = -1;
        long agentId = -1;
        Set<Long> agentIds = null;
        if ( columnName.equals( "companyId" ) ) {
            // agent list will be null
            companyId = columnValue;
        } else if ( columnName.equals( "agentId" ) ) {
            // agent list will have one element, the agent id
            agentId = columnValue;
        } else if ( columnName.equals( "regionId" ) ) {
            agentIds = userProfileDao.findUserIdsByRegion( columnValue );
        } else if ( columnName.equals( "branchId" ) ) {
            agentIds = userProfileDao.findUserIdsByBranch( columnValue );
        }
        //long incompleteSurveyCount = surveyPreInitiationDao.getIncompleteSurveyCount(companyId, agentId, CommonConstants.STATUS_ACTIVE, startDate, endDate, agentIds);
        //JIRA SS-1350 begin
        long incompleteSurveyCount = 0;
        if ( companyId > 0l || agentId > 0l || ( agentIds != null && !agentIds.isEmpty() ) ) {
            incompleteSurveyCount = surveyPreInitiationDao.getIncompleteSurveyCount( companyId,
                agentId, new int[] { CommonConstants.STATUS_SURVEYPREINITIATION_DELETED,
                    CommonConstants.SURVEY_STATUS_PRE_INITIATED, CommonConstants.SURVEY_STATUS_INITIATED },
                startDate, endDate, agentIds );
        }
        //JIRA SS-1350 end
        LOG.debug( "Completed survey: {}" , completedSurveyCount );
        LOG.debug( "Incomplete survey: {}" , incompleteSurveyCount );
        return completedSurveyCount + incompleteSurveyCount;
    }


    @Override
    public long getCompleteSurveyCount( String columnName, long columnValue, int numberOfDays ) throws InvalidInputException
    {
        LOG.info( "Get completed survey count for {} and value {} with number of days: {}", columnName, columnValue, numberOfDays );

        if ( columnName == null || columnName.isEmpty() ) {
            LOG.warn( "Wrong input parameter : passed input parameter column name is null or empty" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column name is null or empty" );
        }
        if ( columnValue <= 0l ) {
            LOG.warn( "Wrong input parameter : passed input parameter column value is invalid" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column value is invalid" );
        }


        Calendar startTime = Calendar.getInstance();
        startTime.add( Calendar.DATE, -1 * numberOfDays );
        // strip the time component of start time
        startTime.set( Calendar.HOUR_OF_DAY, 0 );
        startTime.set( Calendar.MINUTE, 0 );
        startTime.set( Calendar.SECOND, 0 );
        startTime.set( Calendar.MILLISECOND, 0 );

        Timestamp startDate = null;
        Timestamp endDate = null;
        if ( numberOfDays >= 0 ) {
            startDate = new Timestamp( startTime.getTimeInMillis() );
            endDate = new Timestamp( System.currentTimeMillis() );
        }
        return getCompletedSurveyCountForStatistics( columnName, columnValue, startDate, endDate, true );
    }


    private long getCompletedSurveyCount( String columnName, long columnValue, Timestamp startDate, Timestamp endDate,
        boolean filterAbusive ) throws InvalidInputException
    {
        //JIRA SS-580(exclude zillow reviews)
        return surveyDetailsDao.getCompletedSurveyCount( columnName, columnValue, startDate, endDate, filterAbusive, true );
    }


    private long getCompletedSurveyCountForStatistics( String columnName, long columnValue, Timestamp startDate,
        Timestamp endDate, boolean filterAbusive ) throws InvalidInputException
    {
        return surveyDetailsDao.getCompletedSurveyCountForStatistics( columnName, columnValue, startDate, endDate,
            filterAbusive );
    }


    @Override
    public long getClickedSurveyCountForPastNdays( String columnName, long columnValue, int numberOfDays )
        throws InvalidInputException
    {
        if ( columnName == null || columnName.isEmpty() ) {
            LOG.warn( "Wrong input parameter : passed input parameter column name is null or empty" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column name is null or empty" );
        }
        if ( columnValue <= 0l ) {
            LOG.warn( "Wrong input parameter : passed input parameter column value is invalid" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column value is invalid" );
        }

        return surveyDetailsDao.getClickedSurveyCount( columnName, columnValue, numberOfDays, true );
    }


    @Override
    public long getSocialPostsForPastNdaysWithHierarchy( String columnName, long columnValue, int numberOfDays )
        throws InvalidInputException
    {
        if ( columnName == null || columnName.isEmpty() ) {
            LOG.warn( "Wrong input parameter : passed input parameter column name is null or empty" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column name is null or empty" );
        }
        if ( columnValue <= 0l ) {
            LOG.warn( "Wrong input parameter : passed input parameter column value is invalid" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column value is invalid" );
        }

        return surveyDetailsDao.getSocialPostsCountBasedOnHierarchy( numberOfDays, columnName, columnValue, false, false );
    }


    @Override
    public long getSocialPostsForPastNdaysWithHierarchyForStatistics( String columnName, long columnValue, int numberOfDays )
        throws InvalidInputException
    {
        if ( columnName == null || columnName.isEmpty() ) {
            LOG.warn( "Wrong input parameter : passed input parameter column name is null or empty" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column name is null or empty" );
        }
        if ( columnValue <= 0l ) {
            LOG.warn( "Wrong input parameter : passed input parameter column value is invalid" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column value is invalid" );
        }

        return surveyDetailsDao.getSocialPostsCountBasedOnHierarchy( numberOfDays, columnName, columnValue, false, false );
    }


    @Override
    public double getSurveyScore( String columnName, long columnValue, int numberOfDays, boolean realtechAdmin )
        throws InvalidInputException
    {
        if ( columnName == null || columnName.isEmpty() ) {
            LOG.warn( "Wrong input parameter : passed input parameter column name is null or empty" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column name is null or empty" );
        }
        if ( columnValue <= 0l ) {
            LOG.warn( "Wrong input parameter : passed input parameter column name is null or empty" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column value is invalid" );
        }
        return surveyDetailsDao.getRatingForPastNdays( columnName, columnValue, numberOfDays, false, realtechAdmin, false, 0,
            0 );
    }


    @Override
    public int getProfileCompletionPercentage( User user, String columnName, long columnValue,
        OrganizationUnitSettings organizationUnitSettings ) throws InvalidInputException
    {
        LOG.debug( "Method to calculate profile completion percentage started" );

        if ( columnName == null || columnName.isEmpty() ) {
            LOG.warn( "Wrong input parameter : passed input parameter column name is null or empty" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column name is null or empty" );
        }
        if ( columnValue <= 0l ) {
            LOG.warn( "Wrong input parameter : passed input parameter column value is invalid" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column value is invalid" );
        }
        if ( user == null ) {
            LOG.warn( "Wrong input parameter : passed input parameter user is null" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter user is null" );
        }
        if ( organizationUnitSettings == null ) {
            LOG.warn( "Wrong input parameter : passed input parameter organizationUnitSettings is null" );
            throw new InvalidInputException(
                "Wrong input parameter : passed input parameter organizationUnitSettings is null" );
        }

        int totalWeight = 0;
        double currentWeight = 0;
        if ( weightageColumns.containsKey( "email" ) ) {
            totalWeight += weightageColumns.get( "email" );
            if ( organizationUnitSettings.getContact_details() != null
                && organizationUnitSettings.getContact_details().getMail_ids() != null )
                currentWeight += weightageColumns.get( "email" );
        }
        if ( weightageColumns.containsKey( "about_me" ) ) {
            totalWeight += weightageColumns.get( "about_me" );
            if ( organizationUnitSettings.getContact_details() != null
                && organizationUnitSettings.getContact_details().getAbout_me() != null )
                currentWeight += weightageColumns.get( "about_me" );
        }
        if ( weightageColumns.containsKey( "contact_number" ) ) {
            totalWeight += weightageColumns.get( "contact_number" );
            if ( organizationUnitSettings.getContact_details() != null
                && organizationUnitSettings.getContact_details().getContact_numbers() != null )
                currentWeight += weightageColumns.get( "contact_number" );
        }
        if ( weightageColumns.containsKey( "profile_image" ) ) {
            totalWeight += weightageColumns.get( "profile_image" );
            if ( organizationUnitSettings.getProfileImageUrl() != null )
                currentWeight += weightageColumns.get( "profile_image" );
        }
        if ( weightageColumns.containsKey( "title" ) ) {
            totalWeight += weightageColumns.get( "title" );
            if ( organizationUnitSettings.getContact_details().getTitle() != null )
                currentWeight += weightageColumns.get( "title" );
        }
        LOG.debug( "Method to calculate profile completion percentage finished" );
        try {
            return (int) Math.round( currentWeight * 100 / totalWeight );
        } catch ( ArithmeticException e ) {
            LOG.error( "Exception caught in getProfileCompletionPercentage()", e );
            return 0;
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception
    {
        weightageColumns = new HashMap<>();
        weightageColumns.put( "email", 1 );
        weightageColumns.put( "about_me", 1 );
        weightageColumns.put( "title", 1 );
        weightageColumns.put( "profile_image", 1 );
        weightageColumns.put( "contact_number", 1 );
    }


    /*
     * Method to calculate number of badges based upon surveyScore, count of surveys sent and
     * profile completeness.
     */
    @Override
    public int getBadges( double surveyScore, int surveyCount, int socialPosts, int profileCompleteness )
        throws InvalidInputException
    {
        LOG.debug( "Method to calculate number of badges started" );
        if ( surveyScore < 0 || surveyCount < 0 || socialPosts < 0 || profileCompleteness < 0 ) {
            LOG.warn( "Invalid input parameter : should not be less than zero" );
            throw new InvalidInputException( "Invalid input parameter : should not be less than zero" );
        }

        int badges = 0;
        double normalizedSurveyScore = surveyScore * 25 / CommonConstants.MAX_SURVEY_SCORE;
        double normalizedProfileCompleteness = profileCompleteness * 25 / 100;
        if ( surveyCount > CommonConstants.MAX_SENT_SURVEY_COUNT )
            surveyCount = CommonConstants.MAX_SENT_SURVEY_COUNT;
        double normalizedSurveyCount = surveyCount * 25 / CommonConstants.MAX_SENT_SURVEY_COUNT;
        if ( socialPosts > CommonConstants.MAX_SOCIAL_POSTS )
            socialPosts = CommonConstants.MAX_SOCIAL_POSTS;
        double normalizedSocialPosts = socialPosts * 25 / CommonConstants.MAX_SOCIAL_POSTS;
        int overallPercentage = (int) Math
            .round( normalizedSurveyScore + normalizedProfileCompleteness + normalizedSurveyCount + normalizedSocialPosts );
        if ( overallPercentage < 34 )
            badges = 1;
        else if ( overallPercentage < 67 )
            badges = 2;
        else
            badges = 3;
        LOG.debug( "Method to calculate number of badges finished." );
        return badges;
    }


    @Transactional
    @Override
    public Map<String, Map<Integer, Integer>> getSurveyDetailsForGraph( String columnName, long columnValue, int numberOfDays,
        boolean realtechAdmin ) throws ParseException, InvalidInputException
    {
        LOG.info( "Getting survey details for graph for {} with value {} for number of days {}. Reatech admin flag: {}", columnName, columnValue, numberOfDays, realtechAdmin );

        if ( columnName == null || columnName.isEmpty() ) {
            LOG.warn( "Wrong input parameter : passed input parameter column name is null or empty" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column name is null or empty" );
        }
        if ( columnValue <= 0l ) {
            LOG.warn( "Wrong input parameter : passed input parameter column value is invalid" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column value is invalid" );
        }

        String criteria = "";
        Calendar startTime = Calendar.getInstance();
        switch ( numberOfDays ) {
            case 30:
                criteria = CommonConstants.AGGREGATE_BY_WEEK;
                startTime.add( Calendar.DATE, -30 );
                break;
            case 60:
                criteria = CommonConstants.AGGREGATE_BY_WEEK;
                startTime.add( Calendar.DATE, -60 );
                break;
            case 90:
                criteria = CommonConstants.AGGREGATE_BY_WEEK;
                startTime.add( Calendar.DATE, -90 );
                break;
            case 365:
                criteria = CommonConstants.AGGREGATE_BY_MONTH;
                startTime.add( Calendar.DATE, -365 );
                break;
        }
        // strip the time component of start time
        startTime.set( Calendar.HOUR_OF_DAY, 0 );
        startTime.set( Calendar.MINUTE, 0 );
        startTime.set( Calendar.SECOND, 0 );
        startTime.set( Calendar.MILLISECOND, 0 );

        Timestamp startDate = new Timestamp( startTime.getTimeInMillis() );
        Timestamp endDate = new Timestamp( System.currentTimeMillis() );
        LOG.debug( "Getting sent surveys aggregation" );
        Map<Integer, Integer> completedSurveys = surveyDetailsDao.getCompletedSurveyAggregationCount( columnName, columnValue,
            startDate, endDate, criteria );
        // Since the values will be modified while aggregating total surveys, copying the value to another map
        Map<Integer, Integer> completedSurveyToBeProcessed = null;
        if ( completedSurveys != null && completedSurveys.size() > 0 ) {
            completedSurveyToBeProcessed = new HashMap<>();
            completedSurveyToBeProcessed.putAll( completedSurveys );
        }
        // TODO: remove hard coding
        long companyId = -1;
        long agentId = -1;
        Set<Long> agentIds = null;
        if ( columnName.equals( "companyId" ) ) {
            // agent list will be null
            companyId = columnValue;
        } else if ( columnName.equals( "agentId" ) ) {
            // agent list will have one element, the agent id
            agentId = columnValue;
        } else if ( columnName.equals( "regionId" ) ) {
            agentIds = userProfileDao.findUserIdsByRegion( columnValue );
        } else if ( columnName.equals( "branchId" ) ) {
            agentIds = userProfileDao.findUserIdsByBranch( columnValue );
        }


        Map<Integer, Integer> incompleteSurveys = new HashMap<Integer, Integer>();
        if ( companyId > 0l || agentId > 0l || ( agentIds != null && !agentIds.isEmpty() ) ) {
            incompleteSurveys = surveyPreInitiationDao.getIncompletSurveyAggregationCount( companyId, agentId,
                CommonConstants.STATUS_ACTIVE, startDate, endDate, agentIds, criteria );
            LOG.debug( "Aggregating completed and incomplete surveys" );
        }

        Map<Integer, Integer> allSurveysSent = aggregateAllSurveysSent( incompleteSurveys, completedSurveyToBeProcessed );

        LOG.debug( "Getting clicked surveys" );
        Map<Integer, Integer> clickedSurveys = surveyDetailsDao.getClickedSurveyAggregationCount( columnName, columnValue,
            startDate, endDate, criteria );

        LOG.debug( "Getting social posts count." );
        Map<Integer, Integer> socialPosts = surveyDetailsDao.getSocialPostsAggregationCount( columnName, columnValue, startDate,
            endDate, criteria );

        Map<String, Map<Integer, Integer>> map = new HashMap<String, Map<Integer, Integer>>();
        map.put( "clicked", clickedSurveys );
        map.put( "sent", allSurveysSent );
        map.put( "complete", completedSurveys );
        map.put( "socialposts", socialPosts );
        return map;
    }


    Map<Integer, Integer> aggregateAllSurveysSent( Map<Integer, Integer> incompleteSurveys,
        Map<Integer, Integer> completedSurveys )
    {
        LOG.debug( "Aggregating all surveys" );
        if ( ( incompleteSurveys == null || incompleteSurveys.size() == 0 )
            && ( completedSurveys != null && completedSurveys.size() > 0 ) ) {
            return completedSurveys;
        } else if ( ( completedSurveys == null || completedSurveys.size() == 0 )
            && ( incompleteSurveys != null && incompleteSurveys.size() > 0 ) ) {
            return incompleteSurveys;
        } else if ( ( completedSurveys == null || completedSurveys.size() == 0 )
            && ( incompleteSurveys == null || incompleteSurveys.size() == 0 ) ) {
            return null;
        } else {
            // both the maps are present
            for ( Integer incompleteSurveyKey : incompleteSurveys.keySet() ) {
                if ( completedSurveys.containsKey( incompleteSurveyKey ) ) {
                    int totalValue = incompleteSurveys.get( incompleteSurveyKey ) + completedSurveys.get( incompleteSurveyKey );
                    incompleteSurveys.put( incompleteSurveyKey, totalValue );
                    // remove the object from the other map
                    completedSurveys.remove( incompleteSurveyKey );
                }
            }
            // there might be some records left in the completed survey which needs to be put in the other map
            incompleteSurveys.putAll( completedSurveys );
            return incompleteSurveys;
        }
    }


    /*
     * Method to create excel file from all the incomplete survey data.
     */
    @Override
    public XSSFWorkbook downloadIncompleteSurveyData( List<SurveyPreInitiation> surveyDetails, String fileLocation )
        throws IOException, InvalidInputException
    {
        if ( fileLocation == null || fileLocation.isEmpty() ) {
            throw new InvalidInputException( "Invalid input parameter : passed input parameter fileLocation is null or empty" );
        }
        if ( surveyDetails == null ) {
            throw new InvalidInputException( "Invalid input parameter : passed input parameter surveyDetails is null" );
        }
        Map<Integer, List<Object>> data = workbookData.getIncompleteSurveyDataToBeWrittenInSheet( surveyDetails );
        XSSFWorkbook workbook = workbookOperations.createWorkbook( data );
        return workbook;
    }


    /*
     * Method to create excel file for Social posts.
     */
    @Override
    public XSSFWorkbook downloadSocialMonitorData( List<SocialPost> socialPosts, String fileName ) throws InvalidInputException
    {
        if ( fileName == null || fileName.isEmpty() ) {
            throw new InvalidInputException( "Invalid input parameter : passed input parameter fileName is null or empty" );
        }
        if ( socialPosts == null ) {
            throw new InvalidInputException( "Invalid input parameter : passed input parameter surveyDetails is null" );
        }
        Collections.sort( socialPosts, new SocialPostsComparator() );
        Map<Integer, List<Object>> data = workbookData.getSocialMonitorDataToBeWrittenInSheet( socialPosts );
        XSSFWorkbook workbook = workbookOperations.createWorkbook( data );
        return workbook;
    }


    /*
     * Method to create excel file from all the completed survey data.
     */
    @Override
    public XSSFWorkbook downloadCustomerSurveyResultsData( List<SurveyDetails> surveyDetails, String fileLocation,
        String profileLevel, long companyId ) throws IOException, InvalidInputException
    {
        if ( fileLocation == null || fileLocation.isEmpty() ) {
            throw new InvalidInputException( "Invalid input parameter : passed input parameter fileLocation is null or empty" );
        }
        if ( surveyDetails == null ) {
            throw new InvalidInputException( "Invalid input parameter : passed input parameter surveyDetails is null" );
        }

        if ( profileLevel == null || profileLevel.isEmpty() ) {
            throw new InvalidInputException( "Invalid input parameter : passed input parameter profileLevel is null or empty" );
        }

        if ( companyId <= 0l ) {
            throw new InvalidInputException( "Invalid input parameter : passed input parameter company id is invalid" );
        }

        //do not use Collections.sort because of the performance issue
        //Collections.sort( surveyDetails, new SurveyResultsComparator() );
        Map<Integer, List<Object>> data = workbookData.getCustomerSurveyResultDataToBeWrittenInSheet( surveyDetails,
            companyId );
        XSSFWorkbook workbook = workbookOperations.createWorkbook( data );
        return workbook;
    }


    /*
     * Method to create excel file from all the agents' detailed data.
     */
    @Override
    public XSSFWorkbook downloadAgentRankingData( List<AgentRankingReport> agentDetails, String fileLocation )
        throws IOException, InvalidInputException
    {
        if ( fileLocation == null || fileLocation.isEmpty() ) {
            throw new InvalidInputException( "Invalid input parameter : passed input parameter fileLocation is null or empty" );
        }
        if ( agentDetails == null ) {
            throw new InvalidInputException( "Invalid input parameter : passed input parameter agentDetails is null" );
        }
        Collections.sort( agentDetails, new AgentRankingReportComparator() );
        Map<Integer, List<Object>> data = workbookData.getAgentRankingDataToBeWrittenInSheet( agentDetails );
        XSSFWorkbook workbook = workbookOperations.createWorkbook( data );
        return workbook;
    }


    /**
     * Method to create excel file for user adoption report.
     */
    @Override
    @Transactional
    public XSSFWorkbook downloadUserAdoptionReportData( long companyId ) throws InvalidInputException, NoRecordsFetchedException
    {
        if ( companyId <= 0l ) {
            throw new InvalidInputException( "Invalid input parameter : passed input parameter companyId is invalid" );
        }

        // get user adoption data
        List<Object[]> rows = companyDao.getUserAdoptionData( companyId );

        // check whether report info are available
        if ( rows == null || rows.isEmpty() ) {
            LOG.error( "No user adoption data found for the company id : " + companyId );
            throw new NoRecordsFetchedException( "No user adoption data found for the company id : " + companyId );
        }

        DecimalFormat decimalFormat = new DecimalFormat( "#0" );
        decimalFormat.setRoundingMode( RoundingMode.DOWN );
        Map<Integer, List<Object>> data = workbookData.getUserAdoptionReportDataToBeWrittenInSheet( rows );
        XSSFWorkbook workbook = workbookOperations.createWorkbook( data, decimalFormat );
        return workbook;
    }


    /**
     * Method to check if billing report entries exist
     */
    @Override
    @Transactional
    public List<FileUpload> getReportsToBeSent() throws NoRecordsFetchedException
    {
        LOG.info( "Check if billing report entries exist" );

        List<Integer> uploadTypeList = new ArrayList<Integer>();
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_BILLING_REPORT );
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_COMPANY_USERS_REPORT );
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_COMPANY_HIERARCHY_REPORT );
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_COMPANY_REGISTRATION_REPORT );
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_SURVEY_DATA_REPORT );
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_USER_RANKING_REPORT );
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_INCOMPLETE_SURVEY_REPORT );
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_SOCIAL_MONITOR_REPORT );
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_USER_ADOPTION_REPORT );
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_STATS_REPORT);
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_REPORTING_VERIFIED_USERS_REPORT );
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_USERS_REPORT );
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_RESULTS_REPORT );
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_TRANSACTION_REPORT);
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_REPORTING_USER_RANKING_MONTHLY_REPORT);
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_REPORTING_USER_RANKING_YEARLY_REPORT);
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_DETAILS_REPORT);
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_REPORTING_INCOMPLETE_SURVEY_REPORT);
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_REPORTING_NPS_WEEK_REPORT );
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_REPORTING_NPS_MONTH_REPORT );
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_REPORTING_BRANCH_RANKING_MONTHLY_REPORT);
        uploadTypeList.add( CommonConstants.FILE_UPLOAD_REPORTING_BRANCH_RANKING_YEARLY_REPORT);
        Criterion fileUploadTypeCriteria = Restrictions.in( CommonConstants.FILE_UPLOAD_TYPE_COLUMN, uploadTypeList );
        List<Integer> statusList = new ArrayList<Integer>();
        //get only active records
        statusList.add( CommonConstants.STATUS_ACTIVE );
        Criterion statusCriteria = Restrictions.in( CommonConstants.STATUS_COLUMN, statusList );
        List<FileUpload> filesToBeUploaded = fileUploadDao.findByCriteria( FileUpload.class, fileUploadTypeCriteria,
            statusCriteria );
        if ( filesToBeUploaded == null || filesToBeUploaded.isEmpty() ) {
            throw new NoRecordsFetchedException( "No billing report entries exist" );
        }
        return filesToBeUploaded;
    }


    /**
     * 
     * @return
     * @throws NoRecordsFetchedException
     */
    @Override
    @Transactional
    public List<FileUpload> getActiveBillingReports() throws NoRecordsFetchedException
    {
        LOG.info( "Method getActiveBillingReports called" );

        Criterion fileUploadTypeCriteria = Restrictions.eq( CommonConstants.FILE_UPLOAD_TYPE_COLUMN,
            CommonConstants.FILE_UPLOAD_BILLING_REPORT );
        List<Integer> statusList = new ArrayList<Integer>();
        //get only active records
        statusList.add( CommonConstants.STATUS_ACTIVE );
        statusList.add( CommonConstants.STATUS_UNDER_PROCESSING );
        Criterion statusCriteria = Restrictions.in( CommonConstants.STATUS_COLUMN, statusList );
        List<FileUpload> filesToBeUploaded = fileUploadDao.findByCriteria( FileUpload.class, fileUploadTypeCriteria,
            statusCriteria );
        if ( filesToBeUploaded == null || filesToBeUploaded.isEmpty() ) {
            throw new NoRecordsFetchedException( "No billing report entries exist" );
        }
        return filesToBeUploaded;
    }


    /**
     * Method to delete surveys from mongo given the survey preinitiation details
     * @param surveys
     */
    @Override
    public void deleteSurveyDetailsByPreInitiation( List<SurveyPreInitiation> surveys )
    {
        LOG.info( "method deleteSurveyDetailsByPreInitiation() started." );
        surveyDetailsDao.deleteSurveysBySurveyPreInitiation( surveys );
        LOG.info( "method deleteSurveyDetailsByPreInitiation() finished." );
    }


    /**
     * Method to create excel file for company hierarchy report.
     */
    @Override
    @Transactional
    public XSSFWorkbook downloadCompanyHierarchyReportData( long companyId )
        throws InvalidInputException, NoRecordsFetchedException
    {
        if ( companyId <= 0l ) {
            throw new InvalidInputException( "Invalid input parameter : passed input parameter companyId is invalid" );
        }

        Company company = companyDao.findById( Company.class, companyId );
        HierarchyUploadAggregate hierarchyAggregate = hierarchyDownloadService.fetchUpdatedHierarchyUploadStructure( company );
        return hierarchyDownloadService.generateHierarchyDownloadReport( hierarchyAggregate.getHierarchyUpload(), company );
    }


    @Override
    public long getZillowImportCount( String columnName, long columnValue, int numberOfDays ) throws InvalidInputException
    {

        LOG.info( "Get completed survey count for {} and value {} with number of days: {}", columnName, columnValue, numberOfDays );

        if ( columnName == null || columnName.isEmpty() ) {
            LOG.warn( "Wrong input parameter : passed input parameter column name is null or empty" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column name is null or empty" );
        }
        if ( columnValue <= 0l ) {
            LOG.warn( "Wrong input parameter : passed input parameter column value is invalid" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column value is invalid" );
        }


        Calendar startTime = Calendar.getInstance();
        startTime.add( Calendar.DATE, -1 * numberOfDays );
        // strip the time component of start time
        startTime.set( Calendar.HOUR_OF_DAY, 0 );
        startTime.set( Calendar.MINUTE, 0 );
        startTime.set( Calendar.SECOND, 0 );
        startTime.set( Calendar.MILLISECOND, 0 );

        Timestamp startDate = null;
        Timestamp endDate = null;
        if ( numberOfDays >= 0 ) {
            startDate = new Timestamp( startTime.getTimeInMillis() );
            endDate = new Timestamp( System.currentTimeMillis() );
        }
        return getZillowImportCount( columnName, columnValue, startDate, endDate, true );

    }


    @Override
    public long get3rdPartyImportCount( String columnName, long columnValue, int numberOfDays ) throws InvalidInputException
    {
        LOG.info( "Get get3rdPartyImportCount for {} and value {} with number of days: {}", columnName, columnValue,  numberOfDays );
        if ( columnName == null || columnName.isEmpty() ) {
            LOG.warn( "Wrong input parameter : passed input parameter column name is null or empty" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column name is null or empty" );
        }
        if ( columnValue <= 0l ) {
            LOG.warn( "Wrong input parameter : passed input parameter column value is invalid" );
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column value is invalid" );
        }
        Calendar startTime = Calendar.getInstance();
        startTime.add( Calendar.DATE, -1 * numberOfDays );
        // strip the time component of start time
        startTime.set( Calendar.HOUR_OF_DAY, 0 );
        startTime.set( Calendar.MINUTE, 0 );
        startTime.set( Calendar.SECOND, 0 );
        startTime.set( Calendar.MILLISECOND, 0 );
        Timestamp startDate = null;
        Timestamp endDate = null;
        if ( numberOfDays >= 0 ) {
            startDate = new Timestamp( startTime.getTimeInMillis() );
            endDate = new Timestamp( System.currentTimeMillis() );
        }
        return get3rdPartyImportCount( columnName, columnValue, startDate, endDate, true );
    }


    long getZillowImportCount( String columnName, long columnValue, Timestamp startDate, Timestamp endDate,
        boolean filterAbusive ) throws InvalidInputException
    {
        return surveyDetailsDao.getZillowImportCount( columnName, columnValue, startDate, endDate, filterAbusive );
    }


    long get3rdPartyImportCount( String columnName, long columnValue, Timestamp startDate, Timestamp endDate,
        boolean filterAbusive ) throws InvalidInputException
    {
        return surveyDetailsDao.get3rdPartyImportCount( columnName, columnValue, startDate, endDate, filterAbusive );
    }


    /**
     * Method to create excel file for company user report.
     * @throws ProfileNotFoundException 
     */
    @Override
    @Transactional
    public Map<Integer, List<Object>> downloadCompanyUsersReportData( long companyId )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException
    {
        if ( companyId <= 0l ) {
            throw new InvalidInputException( "Invalid input parameter : passed input parameter companyId is invalid" );
        }

        Company company = companyDao.findById( Company.class, companyId );
        Region defaultRegion = organizationManagementService.getDefaultRegionForCompany( company );
        Branch defaultBranchOfDefaultRegion = organizationManagementService
            .getDefaultBranchForRegion( defaultRegion.getRegionId() );

        List<Branch> defaultBranchList = new ArrayList<Branch>();
        List<User> userList = new ArrayList<User>();
        Map<Long, List<UserProfile>> userAndProfileMap = new HashMap<Long, List<UserProfile>>();

        Map<Long, List<FeedStatus>> userAndFeedStatusMap = new HashMap<Long, List<FeedStatus>>();


        final int batch = 100;
        try {

            int start = 0;
            List<Branch> batchBranchList = new ArrayList<Branch>();
            do {
                batchBranchList = branchDao.getBranchesForCompany( companyId, CommonConstants.YES, start, batch );
                if ( batchBranchList != null && batchBranchList.size() > 0 )
                    defaultBranchList.addAll( batchBranchList );
                start += batch;
            } while ( batchBranchList != null && batchBranchList.size() == batch );

            start = 0;
            List<User> batchUserList = new ArrayList<User>();
            Map<Long, List<UserProfile>> batchUserAndProfileMap;
            Map<Long, List<FeedStatus>> batchUserAndFeedStatusMap;
            List<Long> batchUserIds;
            do {
                batchUserIds = new ArrayList<Long>();
                batchUserList = userDao.getUsersForCompany( company, start, batch );
                if ( batchUserList != null && batchUserList.size() > 0 ) {

                    for ( User user : batchUserList ) {
                        batchUserIds.add( user.getUserId() );
                    }
                    //get profiles for users
                    batchUserAndProfileMap = userProfileDao.getUserProfilesForUsers( batchUserIds );
                    //add batch profiles to profile list
                    userAndProfileMap.putAll( batchUserAndProfileMap );


                    //get batch feed status
                    Map<String, Object> queries = new HashMap<String, Object>();
                    queries.put( "agentId", batchUserIds );
                    List<FeedStatus> feedStatusList = feedStatusDao.findByColumnForMultipleValues( FeedStatus.class, "agentId",
                        batchUserIds );
                    batchUserAndFeedStatusMap = new HashMap<Long, List<FeedStatus>>();
                    for ( Long userId : batchUserIds ) {
                        batchUserAndFeedStatusMap.put( userId, new ArrayList<FeedStatus>() );
                    }
                    for ( FeedStatus feedStatus : feedStatusList ) {
                        List<FeedStatus> curUserFeedList = batchUserAndFeedStatusMap.get( feedStatus.getAgentId() );
                        curUserFeedList.add( feedStatus );
                        // batchUserAndFeedStatusMap.put( feedStatus.getAgentId(), curUserFeedList );
                    }

                    userAndFeedStatusMap.putAll( batchUserAndFeedStatusMap );
                    //add batch user to the user list
                    userList.addAll( batchUserList );
                }
                start += batch;
            } while ( batchUserList != null && batchUserList.size() == batch );

        } catch ( Exception e ) {
            LOG.error( "Exception occurred while fetching region or branches or users for company. Reason : ", e );
        }


        List<Long> defaultBranchIdList = new ArrayList<Long>();
        if ( defaultBranchList != null && defaultBranchList.size() > 0 ) {
            for ( Branch defaultBranch : defaultBranchList ) {
                defaultBranchIdList.add( defaultBranch.getBranchId() );
            }
        }

        List<Long> userIdList = new ArrayList<Long>();
        Map<Long, String> userIdRegionIdsMap = new HashMap<Long, String>();
        Map<Long, String> userIdBranchIdsMap = new HashMap<Long, String>();
        Map<Long, String> userIdRegionAsAdminIdsMap = new HashMap<Long, String>();
        Map<Long, String> userIdBranchAsAdminIdsMap = new HashMap<Long, String>();
        List<Long> agentIds = new ArrayList<Long>();
        Map<Long, AgentSettings> userIdSettingsMap = new HashMap<Long, AgentSettings>();
        Map<Long, List<SocialUpdateAction>> socialMediaActionMap = new HashMap<Long, List<SocialUpdateAction>>();
        Map<Long, Date> latestSurveySentForAgent = new HashMap<Long, Date>();
        Map<Long, Date> latestSurveyCompletedForAgent = new HashMap<Long, Date>();
        Map<Long, Long> totalReviewsCountForAgent = new HashMap<Long, Long>();
        Map<Long, Long> socialSurveyReviewsCountForAgent = new HashMap<Long, Long>();
        Map<Long, Long> zillowReviewsCountForAgent = new HashMap<Long, Long>();
        Map<Long, Long> abusiveReviewsCountForAgent = new HashMap<Long, Long>();
        Map<Long, Long> thirdPartyReviewsCountForAgent = new HashMap<Long, Long>();

        User companyAdmin = null;
        if ( userList != null && userList.size() > 0 ) {
            for ( User user : userList ) {
                String regionId = "";
                String regionsName = "";
                String regionIdAsAdmin = "";
                String regionsAsAdminName = "";
                String branchId = "";
                String branchsName = "";
                String branchIdAsAdmin = "";
                String branchNameAsAdmin = "";
                boolean isCompanyAdminHasAnotherRole = false;
                boolean isCompanyAdmin = user.getIsOwner() == CommonConstants.YES;
                //get user profiles for user from map
                List<UserProfile> userProfileList = userAndProfileMap.get( user.getUserId() );
                if ( userProfileList != null && userProfileList.size() > 0 ) {
                    for ( UserProfile userProfile : userProfileList ) {
                        if ( userProfile.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                            if ( defaultRegion.getRegionId() != userProfile.getRegionId()
                                && CommonConstants.DEFAULT_REGION_ID != userProfile.getRegionId()
                                && !regionId.contains( String.valueOf( userProfile.getRegionId() ) ) ) {
                                regionId += userProfile.getRegionId() + ",";
                                regionsName += userProfile.getRegionName() + ",";
                            }
                            if ( !defaultBranchIdList.contains( userProfile.getBranchId() )
                                && CommonConstants.DEFAULT_BRANCH_ID != userProfile.getBranchId()
                                && defaultBranchOfDefaultRegion.getBranchId() != userProfile.getBranchId()
                                && !branchId.contains( String.valueOf( userProfile.getBranchId() ) ) ) {
                                branchId += userProfile.getBranchId() + ",";
                                branchsName += userProfile.getBranchName() + ",";
                            }
                            if ( userProfile.getProfilesMaster()
                                .getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID
                                && !agentIds.contains( user.getUserId() ) ) {
                                agentIds.add( user.getUserId() );
                                if ( isCompanyAdmin )
                                    isCompanyAdminHasAnotherRole = true;
                            }
                            if ( userProfile.getProfilesMaster()
                                .getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                                if ( defaultRegion.getRegionId() != userProfile.getRegionId()
                                    && CommonConstants.DEFAULT_REGION_ID != userProfile.getRegionId()
                                    && !regionIdAsAdmin.contains( String.valueOf( userProfile.getRegionId() ) ) ) {
                                    regionIdAsAdmin += userProfile.getRegionId() + ",";
                                    regionsAsAdminName += userProfile.getRegionName() + ",";
                                    if ( isCompanyAdmin )
                                        isCompanyAdminHasAnotherRole = true;
                                }
                            }
                            if ( userProfile.getProfilesMaster()
                                .getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                                if ( !defaultBranchIdList.contains( userProfile.getBranchId() )
                                    && CommonConstants.DEFAULT_BRANCH_ID != userProfile.getBranchId()
                                    && defaultBranchOfDefaultRegion.getBranchId() != userProfile.getBranchId()
                                    && !branchIdAsAdmin.contains( String.valueOf( userProfile.getBranchId() ) ) ) {
                                    branchIdAsAdmin += userProfile.getBranchId() + ",";
                                    branchNameAsAdmin += userProfile.getBranchName() + ",";
                                    if ( isCompanyAdmin )
                                        isCompanyAdminHasAnotherRole = true;
                                }
                            }
                        }
                    }
                }
                if ( regionsName.length() > 0 ) {
                    userIdRegionIdsMap.put( user.getUserId(), regionsName.substring( 0, regionsName.length() - 1 ) );
                }
                if ( branchsName.length() > 0 ) {
                    userIdBranchIdsMap.put( user.getUserId(), branchsName.substring( 0, branchsName.length() - 1 ) );
                }
                if ( regionsAsAdminName.length() > 0 ) {
                    userIdRegionAsAdminIdsMap.put( user.getUserId(),
                        regionsAsAdminName.substring( 0, regionsAsAdminName.length() - 1 ) );
                }
                if ( branchNameAsAdmin.length() > 0 ) {
                    userIdBranchAsAdminIdsMap.put( user.getUserId(),
                        branchNameAsAdmin.substring( 0, branchNameAsAdmin.length() - 1 ) );
                }
                //company admin
                if ( !isCompanyAdmin || isCompanyAdminHasAnotherRole )
                    userIdList.add( user.getUserId() );

                if ( isCompanyAdmin )
                    companyAdmin = user;

            }
        }

        /* if ( companyAdmin != null ) {
             userList.remove( companyAdmin );
         }*/

        if ( userIdList.size() > 0 ) {
            List<AgentSettings> agentSettingsList = organizationUnitSettingsDao.fetchMultipleAgentSettingsById( userIdList );
            if ( agentSettingsList != null && agentSettingsList.size() > 0 ) {
                for ( AgentSettings agentSettings : agentSettingsList ) {
                    userIdSettingsMap.put( agentSettings.getIden(), agentSettings );
                }
            }

            //get user social media action detail
            socialMediaActionMap = socialManagementService
                .getSocialConnectionsHistoryForEntities( CommonConstants.AGENT_ID_COLUMN, userIdList );
        }

        latestSurveyCompletedForAgent = surveyDetailsDao.getLatestCompletedSurveyDateForAgents( companyId );
        latestSurveySentForAgent = surveyPreInitiationDao.getLatestSurveySentForAgent( companyId );

        // get all types of reviews counts
        totalReviewsCountForAgent = surveyDetailsDao.getTotalReviewsCountForAllUsersOfCompany( companyId );
        socialSurveyReviewsCountForAgent = surveyDetailsDao.getSocialSurveyReviewsCountForAllUsersOfCompany( companyId );
        zillowReviewsCountForAgent = surveyDetailsDao.getZillowReviewsCountForAllUsersOfCompany( companyId );
        abusiveReviewsCountForAgent = surveyDetailsDao.getAbusiveReviewsCountForAllUsersOfCompany( companyId );
        thirdPartyReviewsCountForAgent = surveyDetailsDao.getThirdPartyReviewsCountForAllUsersOfCompany( companyId );

        //get user data map to craete excel from available users details
        Map<Integer, List<Object>> usersData = createUserDataForCompnyUserReport( userList, agentIds, companyAdmin,
            userIdSettingsMap, userIdRegionIdsMap, userIdBranchIdsMap, userIdRegionAsAdminIdsMap, userIdBranchAsAdminIdsMap,
            socialMediaActionMap, userAndFeedStatusMap, latestSurveyCompletedForAgent, latestSurveySentForAgent,
            totalReviewsCountForAgent, socialSurveyReviewsCountForAgent, zillowReviewsCountForAgent,
            abusiveReviewsCountForAgent, thirdPartyReviewsCountForAgent );

        return usersData;
    }


    /**
     * 
     * @param userList
     * @param agentIds
     * @param companyAdmin
     * @param userIdSettingsMap
     * @param userIdRegionIdsMap
     * @param userIdBranchIdsMap
     * @param userIdRegionAsAdminIdsMap
     * @param userIdBranchAsAdminIdsMap
     * @param socialMediaActionMap
     * @param userAndFeedStatusMap
     * @param latestSurveyCompletedForAgent
     * @param latestSurveySentForAgent
     * @param thirdPartyReviewsCountForAgent 
     * @param abusiveReviewsCountForAgent 
     * @param zillowReviewsCountForAgent 
     * @param socialSurveyReviewsCountForAgent 
     * @param totalReviewsCountForAgent 
     * @return
     */
    @SuppressWarnings ( "deprecation")
    public Map<Integer, List<Object>> createUserDataForCompnyUserReport( List<User> userList, List<Long> agentIds,
        User companyAdmin, Map<Long, AgentSettings> userIdSettingsMap, Map<Long, String> userIdRegionIdsMap,
        Map<Long, String> userIdBranchIdsMap, Map<Long, String> userIdRegionAsAdminIdsMap,
        Map<Long, String> userIdBranchAsAdminIdsMap, Map<Long, List<SocialUpdateAction>> socialMediaActionMap,
        Map<Long, List<FeedStatus>> userAndFeedStatusMap, Map<Long, Date> latestSurveyCompletedForAgent,
        Map<Long, Date> latestSurveySentForAgent, Map<Long, Long> totalReviewsCountForAgent,
        Map<Long, Long> socialSurveyReviewsCountForAgent, Map<Long, Long> zillowReviewsCountForAgent,
        Map<Long, Long> abusiveReviewsCountForAgent, Map<Long, Long> thirdPartyReviewsCountForAgent )
    {

        Integer usersCounter = 2;

        // This data needs to be written (List<Object>)
        Map<Integer, List<Object>> usersData = new TreeMap<>();
        List<Object> userReportToPopulate = new ArrayList<>();

        // loop on users to populate users sheet
        if ( userList != null && userList.size() > 0 ) {
            for ( User user : userList ) {
                // col 0 -  firstname
                // col 1 - last name
                // col 2 - email
                // col 3 - social survey access lavel
                // col 4 - branch ids 
                // col 5 - region ids
                // col 6 - branch ids where he is admin
                // col 7 - region ids where he is admin
                // col 8 - SOCIAL_SURVEY_INVITE_SENT
                // col 9 - DATE_LAST_INVITE_SENT
                // col 10 - PROFILE_VERIFIED
                // col 11 - DATE_OF_LAST_LOGIN
                // col 12 - PROFILE_COMPLETE
                // col 13 - photo - profile image url
                // col 14 - about me
                // col 15 - SocialSurvey Profile Url
                // col 16 - Total Reviews
                // col 17 - SocialSuvrey Reviews
                // col 18 - Zillow reviews
                // col 19 - Abusive reviews
                // col 20 - 3rd Party reviews

                AgentSettings userSettings = userIdSettingsMap.get( user.getUserId() );
                //social media action
                List<SocialUpdateAction> userSocialMediaAction = socialMediaActionMap.get( user.getUserId() );
                List<FeedStatus> userFeedStatus = userAndFeedStatusMap.get( user.getUserId() );

                String usersBranchIds = userIdBranchIdsMap.get( user.getUserId() );
                String usersRegionIds = userIdRegionIdsMap.get( user.getUserId() );

                String usersBranchAdminIds = userIdBranchAsAdminIdsMap.get( user.getUserId() );
                String usersRegionAdminIds = userIdRegionAsAdminIdsMap.get( user.getUserId() );


                //1.first name
                userReportToPopulate.add( user.getFirstName() );

                //2.last name
                if ( user.getLastName() != null && !user.getLastName().trim().equalsIgnoreCase( "" )
                    && !user.getLastName().trim().equalsIgnoreCase( "null" ) )
                    userReportToPopulate.add( user.getLastName() );
                else
                    userReportToPopulate.add( "" );

                //3.email
                userReportToPopulate.add( user.getEmailId() );

                //4.social survey access lavel
                String ssAccessLavel = "";
                if ( user.getUserId() == companyAdmin.getUserId() )
                    ssAccessLavel += "Company Admin , ";
                if ( usersRegionAdminIds != null && usersRegionAdminIds.length() > 0 )
                    ssAccessLavel += "Region Admin , ";
                if ( usersBranchAdminIds != null && usersBranchAdminIds.length() > 0 )
                    ssAccessLavel += "Region Admin , ";
                if ( agentIds.size() > 0 && agentIds.contains( user.getUserId() ) )
                    ssAccessLavel += "User , ";
                if ( ssAccessLavel.contains( "," ) )
                    ssAccessLavel = ssAccessLavel.substring( 0, ssAccessLavel.lastIndexOf( "," ) );

                userReportToPopulate.add( ssAccessLavel );

                //5.office assignments
                if ( usersBranchIds != null && usersBranchIds.length() > 0 )
                    userReportToPopulate.add( usersBranchIds );
                else
                    userReportToPopulate.add( "" );

                //6.region assignments
                if ( usersRegionIds != null && usersRegionIds.length() > 0 )
                    userReportToPopulate.add( usersRegionIds );
                else
                    userReportToPopulate.add( "" );

                //7.office admin 
                if ( usersBranchAdminIds != null && usersBranchAdminIds.length() > 0 )
                    userReportToPopulate.add( usersBranchAdminIds );
                else
                    userReportToPopulate.add( "" );

                //8.region admin 
                if ( usersRegionAdminIds != null && usersRegionAdminIds.length() > 0 )
                    userReportToPopulate.add( usersRegionAdminIds );
                else
                    userReportToPopulate.add( "" );

                //9. SOCIAL_SURVEY_INVITE_SENT
                if ( user.getCreatedOn() != null && user.getCreatedOn().toLocaleString() != null )
                    userReportToPopulate.add( user.getCreatedOn().toLocaleString() );
                else
                    userReportToPopulate.add( "" );

                //10. DATE_LAST_INVITE_SENT
                if( user.getLastInvitationSentDate() != null && !user.getLastInvitationSentDate().equals( new Timestamp( 0 ) ) ){
                    userReportToPopulate.add( user.getLastInvitationSentDate().toLocaleString());
                } else{
                    userReportToPopulate.add( "" );
                }

                //11.PROFILE_VERIFIED
                if ( user.getStatus() == CommonConstants.STATUS_NOT_VERIFIED )
                    userReportToPopulate.add( CommonConstants.CHR_2 );
                else
                    userReportToPopulate.add( CommonConstants.CHR_1 );

                //12. DATE_OF_LAST_LOGIN
                if ( user.getLastLogin() != null && user.getLastLogin().toLocaleString() != null )
                    userReportToPopulate.add( user.getLastLogin().toLocaleString() );
                else
                    userReportToPopulate.add( "" );


                //13. PROFILE_COMPLETE
                String profileComplete = "";
                if ( userSettings != null ) {
                    profileComplete += "( ";
                    //profile photo
                    if ( userSettings.getProfileImageUrl() != null && !userSettings.getProfileImageUrl().isEmpty() )
                        profileComplete += "Photo = Yes";
                    else
                        profileComplete += "Photo = No";

                    profileComplete += " , ";

                    //logo
                    if ( userSettings.getLogo() != null && !userSettings.getLogo().isEmpty() )
                        profileComplete += "Logo = Yes";
                    else
                        profileComplete += "Logo = No";

                    profileComplete += " , ";

                    //Title
                    if ( userSettings.getContact_details() != null && userSettings.getContact_details().getTitle() != null
                        && !userSettings.getContact_details().getTitle().isEmpty() )
                        profileComplete += "Title = Yes";
                    else
                        profileComplete += "Title = No";

                    profileComplete += " , ";

                    //Location
                    if ( userSettings.getContact_details() != null && userSettings.getContact_details().getLocation() != null
                        && !userSettings.getContact_details().getLocation().isEmpty() )
                        profileComplete += "Location = Yes";
                    else
                        profileComplete += "Location = No";

                    profileComplete += " , ";

                    //Industry
                    if ( userSettings.getContact_details() != null && userSettings.getContact_details().getIndustry() != null
                        && !userSettings.getContact_details().getIndustry().isEmpty() )
                        profileComplete += "Industry = Yes";
                    else
                        profileComplete += "Industry = No";

                    profileComplete += " , ";

                    //Licenses
                    if ( userSettings.getLicenses() != null )
                        profileComplete += "Licenses = Yes";
                    else
                        profileComplete += "Licenses = No";
                    profileComplete += " , ";


                    //Disclaimer
                    if ( userSettings.getDisclaimer() != null && !userSettings.getDisclaimer().isEmpty() )
                        profileComplete += "Disclaimer = Yes";
                    else
                        profileComplete += "Disclaimer = No";

                    profileComplete += " , ";

                    //About
                    if ( userSettings.getContact_details() != null && userSettings.getContact_details().getAbout_me() != null
                        && !userSettings.getContact_details().getAbout_me().isEmpty() )
                        profileComplete += "About = Yes";
                    else
                        profileComplete += "About = No";

                    profileComplete += " ) ";
                }
                userReportToPopulate.add( profileComplete );

                //Socially Connected
                if ( userSocialMediaAction != null && !userSocialMediaAction.isEmpty() )
                    userReportToPopulate.add( CommonConstants.CHR_YES );
                else
                    userReportToPopulate.add( CommonConstants.CHR_NO );


                //get latest facebbok , twitter social media
                SocialUpdateAction latestFacebookSocialUpdate = null;
                SocialUpdateAction latestTwitterSocialUpdate = null;
                SocialUpdateAction latestLinkedInSocialUpdate = null;
                if ( userSocialMediaAction != null ) {
                    for ( SocialUpdateAction socialUpdateAction : userSocialMediaAction ) {
                        if ( socialUpdateAction.getSocialMediaSource().equals( CommonConstants.FACEBOOK_SOCIAL_SITE ) ) {
                            if ( latestFacebookSocialUpdate == null
                                || socialUpdateAction.getUpdateTime().after( latestFacebookSocialUpdate.getUpdateTime() ) )
                                latestFacebookSocialUpdate = socialUpdateAction;
                        }

                        if ( socialUpdateAction.getSocialMediaSource().equals( CommonConstants.TWITTER_SOCIAL_SITE ) ) {
                            if ( latestTwitterSocialUpdate == null
                                || socialUpdateAction.getUpdateTime().after( latestTwitterSocialUpdate.getUpdateTime() ) )
                                latestTwitterSocialUpdate = socialUpdateAction;
                        }

                        if ( socialUpdateAction.getSocialMediaSource().equals( CommonConstants.LINKEDIN_SOCIAL_SITE ) ) {
                            if ( latestLinkedInSocialUpdate == null
                                || socialUpdateAction.getUpdateTime().after( latestLinkedInSocialUpdate.getUpdateTime() ) )
                                latestLinkedInSocialUpdate = socialUpdateAction;
                        }
                    }
                }


                FeedStatus twitterFeedStatus = null;
                FeedStatus fbFeedStatus = null;
                FeedStatus linkedInFeedStatus = null;
                if ( userFeedStatus != null ) {
                    for ( FeedStatus feedStatus : userFeedStatus ) {
                        if ( feedStatus.getFeedSource().equals( CommonConstants.FACEBOOK_SOCIAL_SITE ) ) {
                            fbFeedStatus = feedStatus;
                        }
                        if ( feedStatus.getFeedSource().equals( CommonConstants.TWITTER_SOCIAL_SITE ) ) {
                            twitterFeedStatus = feedStatus;
                        }
                        if ( feedStatus.getFeedSource().equals( CommonConstants.LINKEDIN_SOCIAL_SITE ) ) {
                            linkedInFeedStatus = feedStatus;
                        }
                    }
                }

                //facebook

                //connection stablished
                if ( latestFacebookSocialUpdate != null ) {
                    userReportToPopulate.add( CommonConstants.CHR_YES );
                } else {
                    userReportToPopulate.add( CommonConstants.CHR_NO );
                }

                //connection status
                if ( latestFacebookSocialUpdate != null ) {
                    userReportToPopulate.add( latestFacebookSocialUpdate.getAction() );
                } else {
                    userReportToPopulate.add( "" );
                }

                //last fetch
                if ( fbFeedStatus != null && fbFeedStatus.getLastFetchedTill() != null ) {
                    userReportToPopulate.add( fbFeedStatus.getLastFetchedTill().toLocaleString() );
                } else {
                    userReportToPopulate.add( "" );
                }


                //twitter
                //connection stablished
                if ( latestTwitterSocialUpdate != null ) {
                    userReportToPopulate.add( CommonConstants.CHR_YES );
                } else {
                    userReportToPopulate.add( CommonConstants.CHR_NO );
                }

                //connection status
                if ( latestTwitterSocialUpdate != null ) {
                    userReportToPopulate.add( latestTwitterSocialUpdate.getAction() );
                } else {
                    userReportToPopulate.add( "" );
                }

                //last fetch
                if ( twitterFeedStatus != null && twitterFeedStatus.getLastFetchedTill() != null ) {
                    userReportToPopulate.add( twitterFeedStatus.getLastFetchedTill().toLocaleString() );
                } else {
                    userReportToPopulate.add( "" );
                }

                //linkedin
                //connection stablished
                if ( latestLinkedInSocialUpdate != null ) {
                    userReportToPopulate.add( CommonConstants.CHR_YES );
                } else {
                    userReportToPopulate.add( CommonConstants.CHR_NO );
                }

                //connection status
                if ( latestLinkedInSocialUpdate != null ) {
                    userReportToPopulate.add( latestLinkedInSocialUpdate.getAction() );
                } else {
                    userReportToPopulate.add( "" );
                }

                //last fetch
                if ( linkedInFeedStatus != null && linkedInFeedStatus.getLastFetchedTill() != null ) {
                    userReportToPopulate.add( linkedInFeedStatus.getLastFetchedTill().toLocaleString() );
                } else {
                    userReportToPopulate.add( "" );
                }


                //google
                if ( userSettings != null && userSettings.getSocialMediaTokens() != null
                    && userSettings.getSocialMediaTokens().getGoogleToken() != null ) {
                    userReportToPopulate.add( userSettings.getSocialMediaTokens().getGoogleToken().getProfileLink() );
                } else {
                    userReportToPopulate.add( "" );
                }

                //zillow
                if ( userSettings != null && userSettings.getSocialMediaTokens() != null
                    && userSettings.getSocialMediaTokens().getZillowToken() != null ) {
                    userReportToPopulate.add( userSettings.getSocialMediaTokens().getZillowToken().getZillowProfileLink() );
                } else {
                    userReportToPopulate.add( "" );
                }

                //yelp 
                if ( userSettings != null && userSettings.getSocialMediaTokens() != null
                    && userSettings.getSocialMediaTokens().getYelpToken() != null ) {
                    userReportToPopulate.add( userSettings.getSocialMediaTokens().getYelpToken().getYelpPageLink() );
                } else {
                    userReportToPopulate.add( "" );
                }

                //realtor
                if ( userSettings != null && userSettings.getSocialMediaTokens() != null
                    && userSettings.getSocialMediaTokens().getRealtorToken() != null ) {
                    userReportToPopulate.add( userSettings.getSocialMediaTokens().getRealtorToken().getRealtorProfileLink() );
                } else {
                    userReportToPopulate.add( "" );
                }

                //google business
                if ( userSettings != null && userSettings.getSocialMediaTokens() != null
                    && userSettings.getSocialMediaTokens().getGoogleBusinessToken() != null ) {
                    userReportToPopulate
                        .add( userSettings.getSocialMediaTokens().getGoogleBusinessToken().getGoogleBusinessLink() );
                } else {
                    userReportToPopulate.add( "" );
                }

                //lending tree
                if ( userSettings != null && userSettings.getSocialMediaTokens() != null
                    && userSettings.getSocialMediaTokens().getLendingTreeToken() != null ) {
                    userReportToPopulate
                        .add( userSettings.getSocialMediaTokens().getLendingTreeToken().getLendingTreeProfileLink() );
                } else {
                    userReportToPopulate.add( "" );
                }

                //data adoption latestSurveyForAgent
                userReportToPopulate.add( "" );

                //DATE_LAST_SURVEY_SENT
                Date latestSentSurveyDate = latestSurveySentForAgent.get( user.getUserId() );
                if ( latestSentSurveyDate != null && latestSentSurveyDate.toLocaleString() != null )
                    userReportToPopulate.add( latestSentSurveyDate.toLocaleString() );
                else
                    userReportToPopulate.add( "" );

                //DATE_LAST_SURVEY_POSTED
                Date latestCompletedSurveyDate = latestSurveyCompletedForAgent.get( user.getUserId() );
                if ( latestCompletedSurveyDate != null && latestCompletedSurveyDate.toLocaleString() != null )
                    userReportToPopulate.add( latestCompletedSurveyDate.toLocaleString() );
                else
                    userReportToPopulate.add( "" );

                String userAddress = "";
                if ( userSettings != null && userSettings.getContact_details() != null ) {
                    if ( userSettings.getContact_details().getAddress1() != null ) {
                        userAddress = userSettings.getContact_details().getAddress1();
                    }
                    if ( userSettings.getContact_details().getAddress2() != null ) {
                        if ( userAddress.isEmpty() )
                            userAddress = userSettings.getContact_details().getAddress2();
                        else
                            userAddress += " " + userSettings.getContact_details().getAddress2();
                    }
                }
                userReportToPopulate.add( userAddress );

                // Social survey profile url
                String profileUrl = "";
                if ( userSettings != null && userSettings.getProfileUrl() != null ) {
                    profileUrl = applicationBaseUrl + CommonConstants.AGENT_PROFILE_FIXED_URL + userSettings.getProfileUrl();
                }
                userReportToPopulate.add( profileUrl );

                // Total Reviews (SS + Zillow + 3rd Party Reviews)
                userReportToPopulate.add( totalReviewsCountForAgent != null && !totalReviewsCountForAgent.isEmpty()
                    && totalReviewsCountForAgent.get( user.getUserId() ) != null
                        ? totalReviewsCountForAgent.get( user.getUserId() ) : 0 );

                // SocialSurvey Reviews
                userReportToPopulate
                    .add( socialSurveyReviewsCountForAgent != null && !socialSurveyReviewsCountForAgent.isEmpty()
                        && socialSurveyReviewsCountForAgent.get( user.getUserId() ) != null
                            ? socialSurveyReviewsCountForAgent.get( user.getUserId() ) : 0 );

                // Zillow Reviews
                userReportToPopulate.add( zillowReviewsCountForAgent != null && !zillowReviewsCountForAgent.isEmpty()
                    && zillowReviewsCountForAgent.get( user.getUserId() ) != null
                        ? zillowReviewsCountForAgent.get( user.getUserId() ) : 0 );

                // Abusive Reviews
                userReportToPopulate.add( abusiveReviewsCountForAgent != null && !abusiveReviewsCountForAgent.isEmpty()
                    && abusiveReviewsCountForAgent.get( user.getUserId() ) != null
                        ? abusiveReviewsCountForAgent.get( user.getUserId() ) : 0 );

                // 3rd Party Reviews
                userReportToPopulate.add( thirdPartyReviewsCountForAgent != null && !thirdPartyReviewsCountForAgent.isEmpty()
                    && thirdPartyReviewsCountForAgent.get( user.getUserId() ) != null
                        ? thirdPartyReviewsCountForAgent.get( user.getUserId() ) : 0 );

                usersData.put( ( ++usersCounter ), userReportToPopulate );
                userReportToPopulate = new ArrayList<>();
            }
        }
        // Setting up user sheet headers
        userReportToPopulate.add( CommonConstants.CHR_USERS_FIRST_NAME );
        userReportToPopulate.add( CommonConstants.CHR_USERS_LAST_NAME );
        userReportToPopulate.add( CommonConstants.CHR_USERS_EMAIL );
        userReportToPopulate.add( CommonConstants.SOCIAL_SURVEY_ACCESS_LAVEL );

        userReportToPopulate.add( CommonConstants.CHR_USERS_OFFICE_ASSIGNMENTS );
        userReportToPopulate.add( CommonConstants.CHR_USERS_REGION_ASSIGNMENTS );
        userReportToPopulate.add( CommonConstants.CHR_USERS_OFFICE_ADMIN_PRIVILEGE );
        userReportToPopulate.add( CommonConstants.CHR_USERS_REGION_ADMIN_PRIVILEGE );

        userReportToPopulate.add( CommonConstants.SOCIAL_SURVEY_INVITE_SENT );
        userReportToPopulate.add( CommonConstants.DATE_LAST_INVITE_SENT );
        userReportToPopulate.add( CommonConstants.PROFILE_VERIFIED );
        userReportToPopulate.add( CommonConstants.DATE_OF_LAST_LOGIN );
        userReportToPopulate.add( CommonConstants.PROFILE_COMPLETE );

        userReportToPopulate.add( CommonConstants.SOCIALLY_CONNECTED );
        userReportToPopulate.add( CommonConstants.CHR_FACEBOOK );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );

        userReportToPopulate.add( CommonConstants.CHR_TWITTER );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );

        userReportToPopulate.add( CommonConstants.CHR_LINKEDIN );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );


        userReportToPopulate.add( CommonConstants.CHR_GOOGLE );
        userReportToPopulate.add( CommonConstants.CHR_ZILLOW );
        userReportToPopulate.add( CommonConstants.CHR_YELP );
        userReportToPopulate.add( CommonConstants.CHR_REALTOR );
        userReportToPopulate.add( CommonConstants.CHR_GOOGLE_BUSINESS );
        userReportToPopulate.add( CommonConstants.CHR_LENDING_TREE );

        userReportToPopulate.add( CommonConstants.DATE_ADOPTION_COMPLETED );
        userReportToPopulate.add( CommonConstants.DATE_LAST_SURVEY_SENT );
        userReportToPopulate.add( CommonConstants.DATE_LAST_SURVEY_POSTED );
        userReportToPopulate.add( CommonConstants.USER_ADDRESS );
        userReportToPopulate.add( CommonConstants.SOCIAL_SURVEY_PROFILE_URL );
        userReportToPopulate.add( CommonConstants.TOTAL_REVIEWS );
        userReportToPopulate.add( CommonConstants.SOCIAL_SURVEY_REVIEWS );
        userReportToPopulate.add( CommonConstants.ZILLOW_REVIEWS );
        userReportToPopulate.add( CommonConstants.ABUSIVE_REVIEWS );
        userReportToPopulate.add( CommonConstants.THIRD_PARTY_REVIEWS );

        usersData.put( 1, userReportToPopulate );

        userReportToPopulate = new ArrayList<>();

        // setting up user sheet header descriptions
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );

        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );

        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "(1=Green, 2=Red)" );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "(Photo, Company Logo, Title, Location, Industry, Licenses, Disclaimer, About)" );

        userReportToPopulate.add( "" );
        userReportToPopulate.add( CommonConstants.DATE_CONNECTION_ESTABLISHED );
        userReportToPopulate.add( CommonConstants.CONNECTION_STATUS );
        userReportToPopulate.add( CommonConstants.DATE_OF_LAST_POST );

        userReportToPopulate.add( CommonConstants.DATE_CONNECTION_ESTABLISHED );
        userReportToPopulate.add( CommonConstants.CONNECTION_STATUS );
        userReportToPopulate.add( CommonConstants.DATE_OF_LAST_POST );

        userReportToPopulate.add( CommonConstants.DATE_CONNECTION_ESTABLISHED );
        userReportToPopulate.add( CommonConstants.CONNECTION_STATUS );
        userReportToPopulate.add( CommonConstants.DATE_OF_LAST_POST );


        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );

        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );

        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "(SS + Zillow + 3rd Party Reviews)" );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );

        usersData.put( 2, userReportToPopulate );

        return usersData;
    }


    @Override
    @Transactional
    public void generateCompanyReportAndMail( Map<Integer, List<Object>> usersData, String recipientMailId,
        String recipientName, Company company ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "method generateCompanyReportAndMail started" );
        if ( company == null ) {
            throw new InvalidInputException( "Passed parameter company is null" );
        }
        String fileName = "Company_Users_Report-" + ( new Timestamp( new Date().getTime() ) )
            + CommonConstants.EXCEL_FILE_EXTENSION;
        fileName = StringUtils.replace(fileName, " ", "_");
        XSSFWorkbook workbook = workbookOperations.createWorkbook( usersData );
        String subject = CommonConstants.COMPANY_USERS_REPORT_MAIL_SUBJ + company.getCompany();
        String body = CommonConstants.COMPANY_USERS_REPORT_MAIL_BODY;
        createExcelFileAndMail( fileName, workbook, recipientMailId, recipientName, subject, body );
        LOG.info( "method generateCompanyReportAndMail ended" );
    }


    @Override
    @Transactional
    public void generateCompanyHierarchyReportAndMail( long companyId, String recipientMailId, String recipientName )
        throws InvalidInputException, NoRecordsFetchedException, UndeliveredEmailException
    {
        LOG.info( "Method generateCompanyHierarchyReportAndMail started" );
        Date date = new Date();
        String fileName = "Company_Hierarchy_Report-" + ( new Timestamp( date.getTime() ) )
            + CommonConstants.EXCEL_FILE_EXTENSION;
        fileName = StringUtils.replace(fileName, " ", "_");
        XSSFWorkbook workbook = downloadCompanyHierarchyReportData( companyId );
        if ( workbook == null ) {
            throw new InvalidInputException( "unable to create workbook" );
        }
        String subject = "Company Hierarchy Report";
        String body = "Here is the company hierarchy report you requested. Please refer to the attachment for the report";
        createExcelFileAndMail( fileName, workbook, recipientMailId, recipientName, subject, body );
        LOG.info( "method generateCompanyHierarchyReportAndMail ended" );

    }


    @Override
    public void generateCompanyRegistrationReportAndMail( Timestamp startDate, Timestamp endDate, String recipientMailId,
        String recipientName ) throws InvalidInputException, UndeliveredEmailException
    {
        Date date = new Date();
        List<Company> companyList = organizationManagementService.getCompaniesByDateRange( startDate, endDate );
        String fileName = "Company_Registration_Report-" + ( new Timestamp( date.getTime() ) )
            + CommonConstants.EXCEL_FILE_EXTENSION;
        fileName = StringUtils.replace(fileName, " ", "_");
        XSSFWorkbook workbook = organizationManagementService.downloadCompanyReport( companyList );
        String subject = "Company Registration Report";
        String body = "Here is the company registration report you requested. Please refer to the attachment for the report";
        createExcelFileAndMail( fileName, workbook, recipientMailId, recipientName, subject, body );
    }


    @Override
    public void generateSurveyDataReportAndMail( Timestamp startDate, Timestamp endDate, String profileLevel, long profileValue,
        long userId, long companyId, String recipientMailId, String recipientName )
        throws InvalidInputException, IOException, UndeliveredEmailException
    {
        Date date = new Date();
        boolean fetchAbusive = false;
        List<SurveyDetails> surveyDetails = new ArrayList<>();
        List<SurveyDetails> surveyDetailsBatch = new ArrayList<>();

        final int batch = 1000;
        int start = 0;

        do {
            surveyDetailsBatch = profileManagementService.getReviewsForReports( profileValue, -1, -1, start, batch,
                profileLevel, fetchAbusive, startDate, endDate, null );
            surveyDetails.addAll( surveyDetailsBatch );
            start += batch;

        } while ( surveyDetailsBatch.size() == batch );


        User user = userDao.findById( User.class, userId );
        String fileName = "Survey_Results-" + profileLevel + "-" + user.getFirstName() + "_" + user.getLastName() + "-"
            + ( new Timestamp( date.getTime() ) );
        fileName = CommonUtils.generateCleanFileName( fileName );
        fileName = fileName + CommonConstants.EXCEL_FILE_EXTENSION;
        XSSFWorkbook workbook = this.downloadCustomerSurveyResultsData( surveyDetails, fileName, profileLevel, companyId );
        String subject = "Survey Data Report";
        String body = "Here is the survey data report you requested. Please refer to the attachment for the report";
        if ( recipientMailId != null && !recipientMailId.isEmpty() && !user.isSuperAdmin()
            && !userManagementService.isUserSocialSurveyAdmin( userId ) ) {
            recipientName = user.getFirstName() + " " + user.getLastName();
        }
        createExcelFileAndMail( fileName, workbook, recipientMailId, recipientName, subject, body );
    }


    @Override
    public void generateUserRankingReportAndMail( Timestamp startDate, Timestamp endDate, String profileLevel,
        long profileValue, long userId, long companyId, String recipientMailId, String recipientName )
        throws InvalidInputException, IOException, UndeliveredEmailException
    {
        Date date = new Date();
        List<AgentRankingReport> agentRanking = new ArrayList<>();
        String colmName = null;
        switch ( profileLevel ) {
            case CommonConstants.PROFILE_LEVEL_COMPANY:
                colmName = CommonConstants.COMPANY_ID_COLUMN;
                break;
            case CommonConstants.PROFILE_LEVEL_REGION:
                colmName = CommonConstants.REGION_ID_COLUMN;
                break;
            case CommonConstants.PROFILE_LEVEL_BRANCH:
                colmName = CommonConstants.BRANCH_ID_COLUMN;
                break;
            case CommonConstants.PROFILE_LEVEL_INDIVIDUAL:
                colmName = CommonConstants.AGENT_ID_COLUMN;
                break;
        }
        agentRanking = profileManagementService.getAgentReport( profileValue, colmName, startDate, endDate, null );
        User user = userDao.findById( User.class, userId );
        if ( recipientMailId != null && !recipientMailId.isEmpty() ) {
            recipientName = user.getFirstName() + " " + user.getLastName();
        }
        String fileName = "User_Ranking_Report-" + profileLevel + "-" + user.getFirstName() + "_" + user.getLastName() + "-"
            + ( new Timestamp( date.getTime() ) );
        fileName = CommonUtils.generateCleanFileName( fileName );
        fileName = fileName + CommonConstants.EXCEL_FILE_EXTENSION;
        XSSFWorkbook workbook = this.downloadAgentRankingData( agentRanking, fileName );
        String subject = "User Ranking Report";
        String body = "Here is the user ranking report you requested. Please refer to the attachment for the report";
        createExcelFileAndMail( fileName, workbook, recipientMailId, recipientName, subject, body );
    }


    @Override
    public void generateSocialMonitorReportAndMail( Timestamp startDate, Timestamp endDate, String profileLevel,
        long profileValue, long userId, long companyId, String recipientMailId, String recipientName )
        throws InvalidInputException, UndeliveredEmailException, NoRecordsFetchedException
    {
        Date date = new Date();
        List<SocialPost> socialPosts = new ArrayList<>();
        String colmName = null;
        switch ( profileLevel ) {
            case CommonConstants.PROFILE_LEVEL_COMPANY:
                colmName = CommonConstants.COMPANY_ID_COLUMN;
                break;
            case CommonConstants.PROFILE_LEVEL_REGION:
                colmName = CommonConstants.REGION_ID_COLUMN;
                break;
            case CommonConstants.PROFILE_LEVEL_BRANCH:
                colmName = CommonConstants.BRANCH_ID_COLUMN;
                break;
            case CommonConstants.PROFILE_LEVEL_INDIVIDUAL:
                colmName = CommonConstants.AGENT_ID_COLUMN;
                break;
        }
        socialPosts = profileManagementService.getCumulativeSocialPosts( profileValue, colmName, -1, -1, profileLevel,
            startDate, endDate );
        User user = userDao.findById( User.class, userId );
        if ( recipientMailId != null && !recipientMailId.isEmpty() ) {
            recipientName = user.getFirstName() + " " + user.getLastName();
        }
        String fileName = "Social_Monitor-" + profileLevel + "-" + user.getFirstName() + "_" + user.getLastName() + "-"
            + ( new Timestamp( date.getTime() ) );
        fileName = CommonUtils.generateCleanFileName( fileName );
        fileName = fileName + CommonConstants.EXCEL_FILE_EXTENSION;
        XSSFWorkbook workbook = this.downloadSocialMonitorData( socialPosts, fileName );
        String subject = "Social Monitor Report";
        String body = "Here is the social monitor report you requested. Please refer to the attachment for the report";
        createExcelFileAndMail( fileName, workbook, recipientMailId, recipientName, subject, body );
    }


    @Override
    public void generateIncompleteSurveyReportAndMail( Timestamp startDate, Timestamp endDate, String profileLevel,
        long profileValue, long userId, long companyId, String recipientMailId, String recipientName )
        throws InvalidInputException, IOException, UndeliveredEmailException
    {
        Date date = new Date();
        List<SurveyPreInitiation> surveyDetails = new ArrayList<>();
        User user = userDao.findById( User.class, userId );
        boolean realtechAdmin = recipientMailId == null || recipientMailId.isEmpty();
        surveyDetails = profileManagementService.getIncompleteSurvey( profileValue, 0, 0, -1, -1, profileLevel, startDate,
            endDate, false );
        if ( !realtechAdmin ) {
            recipientName = user.getFirstName() + " " + user.getLastName();
        }
        String fileName = "Incomplete_Survey_" + profileLevel + "-" + user.getFirstName() + "_" + user.getLastName() + "-"
            + ( new Timestamp( date.getTime() ) );
        fileName = CommonUtils.generateCleanFileName( fileName );
        fileName = fileName + CommonConstants.EXCEL_FILE_EXTENSION;
        XSSFWorkbook workbook = this.downloadIncompleteSurveyData( surveyDetails, fileName );
        String subject = "Incomplete Survey Report";
        String body = "Here is the incomplete survey report you requested. Please refer to the attachment for the report";
        createExcelFileAndMail( fileName, workbook, recipientMailId, recipientName, subject, body );
    }


    @Override
    @Transactional
    public void generateUserAdoptionReportAndMail( Timestamp startDate, Timestamp endDate, String profileLevel,
        long profileValue, long userId, long companyId, String recipientMailId, String recipientName )
        throws InvalidInputException, UndeliveredEmailException, NoRecordsFetchedException
    {
        User user = userDao.findById( User.class, userId );
        String fileName = "User_Adoption_Report-" + profileLevel + "-" + user.getFirstName() + "_" + user.getLastName() + "-"
            + ( new Timestamp( new Date().getTime() ) );
        fileName = CommonUtils.generateCleanFileName( fileName );
        fileName = fileName + CommonConstants.EXCEL_FILE_EXTENSION;
        XSSFWorkbook workbook = this.downloadUserAdoptionReportData( profileValue );
        String subject = "User Adoption Report";
        String body = "Here is the user adoption report you requested. Please refer to the attachment for the report";
        createExcelFileAndMail( fileName, workbook, recipientMailId, recipientName, subject, body );
    }


    private void createExcelFileAndMail( String fileName, XSSFWorkbook workbook, String recipientMailId, String recipientName,
        String subject, String body ) throws InvalidInputException, UndeliveredEmailException
    {
        // Create file and write report into it
        boolean excelCreated = false;
        FileOutputStream fileOutput = null;
        InputStream inputStream = null;
        File file = null;
        String filePath = null;
        try {
            file = new File( fileDirectoryLocation + File.separator + fileName );
            fileOutput = new FileOutputStream( file );
            file.createNewFile();
            workbook.write( fileOutput );
            filePath = file.getPath();
            excelCreated = true;
        } catch ( FileNotFoundException fe ) {
            LOG.error( "Exception caught while generating report " + fileName + ": " + fe.getMessage() );
        } catch ( IOException e ) {
            LOG.error( "Exception caught while generating report " + fileName + ": " + e.getMessage() );
        } finally {
            try {
                if ( fileOutput != null )
                    fileOutput.close();
                if ( inputStream != null ) {
                    inputStream.close();
                }
            } catch ( IOException e ) {
                LOG.error( "Exception caught while generating report " + fileName + ": " + e.getMessage() );
                excelCreated = false;
            }
        }
        
        boolean excelUploaded = false;
        if ( excelCreated ) {
            try {
                filePath = fileUploadService.uploadOldReport( file, fileName );
                
                excelUploaded = true;
            } catch ( NonFatalException e ) {
                LOG.error( "Exception caught while uploading old report", e);
            }
            LOG.debug( "fileUpload on s3 step is done for filename : {}", fileName );
        }

        // Mail the report to the admin
        if ( excelCreated && excelUploaded) {
            List<EmailAttachment> attachments = new ArrayList<EmailAttachment>();
            attachments.add( new EmailAttachment(fileName, filePath) );
            String mailId = null;
            if ( recipientMailId == null || recipientMailId.isEmpty() ) {
                mailId = adminEmailId;
            } else {
                mailId = recipientMailId;
            }

            String name = null;
            if ( recipientName == null || recipientName.isEmpty() ) {
                name = adminName;
            } else {
                name = recipientName;
            }

            body += ". You can download the report using link <a href='" + filePath + "'>" + filePath + "</a>";
            LOG.debug( "sending mail to : " + name + " at : " + mailId );
            emailServices.sendCustomMail( name, mailId, subject, body, attachments );
        }
    }
}
// JIRA SS-137 BY RM05:EOC