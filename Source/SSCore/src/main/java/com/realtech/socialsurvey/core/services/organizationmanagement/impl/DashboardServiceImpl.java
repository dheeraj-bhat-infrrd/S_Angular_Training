package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.io.IOException;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.AgentRankingReportComparator;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.SocialPostsComparator;
import com.realtech.socialsurvey.core.commons.SurveyResultsComparator;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.RegionDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.dao.impl.MongoSocialPostDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.BillingReportData;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchMediaPostDetails;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.entities.Licenses;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RegionMediaPostDetails;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.DashboardService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;


// JIRA SS-137 BY RM05:BOC
/**
 * Class with methods defined to show dash board of user.
 */
@Component
public class DashboardServiceImpl implements DashboardService, InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( DashboardServiceImpl.class );

    //SS-1354: Using date format from CommonConstants
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat( CommonConstants.DATE_FORMAT );
    private static Map<String, Integer> weightageColumns;

    @Autowired
    private SurveyHandler surveyHandler;

    @Autowired
    private SurveyDetailsDao surveyDetailsDao;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private SurveyPreInitiationDao surveyPreInitiationDao;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private UserProfileDao userProfileDao;

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private GenericDao<FileUpload, Long> fileUploadDao;

    @Autowired
    private RegionDao regionDao;

    @Resource
    @Qualifier ( "branch")
    private BranchDao branchDao;

    @Transactional
    @Override
    public long getAllSurveyCount( String columnName, long columnValue, int numberOfDays ) throws InvalidInputException
    {
        LOG.info( "Get all survey count for " + columnName + " and value " + columnValue + " with number of days: "
            + numberOfDays );

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
            incompleteSurveyCount = surveyPreInitiationDao.getIncompleteSurveyCount( companyId, agentId,
                CommonConstants.STATUS_ACTIVE, startDate, endDate, agentIds );
        }
        //JIRA SS-1350 end
        LOG.debug( "Completed survey: " + completedSurveyCount );
        LOG.debug( "Incomplete survey: " + incompleteSurveyCount );
        return completedSurveyCount + incompleteSurveyCount;
    }


    @Override
    public long getCompleteSurveyCount( String columnName, long columnValue, int numberOfDays ) throws InvalidInputException
    {
        LOG.info( "Get completed survey count for " + columnName + " and value " + columnValue + " with number of days: "
            + numberOfDays );

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
        return getCompletedSurveyCount( columnName, columnValue, startDate, endDate, true );
    }


    private long getCompletedSurveyCount( String columnName, long columnValue, Timestamp startDate, Timestamp endDate,
        boolean filterAbusive ) throws InvalidInputException
    {
        return surveyDetailsDao.getCompletedSurveyCount( columnName, columnValue, startDate, endDate, filterAbusive );
    }


    @Override
    public long getClickedSurveyCountForPastNdays( String columnName, long columnValue, int numberOfDays )
        throws InvalidInputException
    {
        if ( columnName == null || columnName.isEmpty() ) {
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column name is null or empty" );
        }
        if ( columnValue <= 0l ) {
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column value is invalid" );
        }

        return surveyDetailsDao.getClickedSurveyCount( columnName, columnValue, numberOfDays, true );
    }


    @Override
    public long getSocialPostsForPastNdaysWithHierarchy( String columnName, long columnValue, int numberOfDays )
        throws InvalidInputException
    {
        if ( columnName == null || columnName.isEmpty() ) {
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column name is null or empty" );
        }
        if ( columnValue <= 0l ) {
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column value is invalid" );
        }

        return surveyDetailsDao.getSocialPostsCountBasedOnHierarchy( numberOfDays, columnName, columnValue, false );
    }


    @Override
    public double getSurveyScore( String columnName, long columnValue, int numberOfDays, boolean realtechAdmin )
        throws InvalidInputException
    {
        if ( columnName == null || columnName.isEmpty() ) {
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column name is null or empty" );
        }
        if ( columnValue <= 0l ) {
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column value is invalid" );
        }
        return surveyDetailsDao
            .getRatingForPastNdays( columnName, columnValue, numberOfDays, false, realtechAdmin, false, 0, 0 );
    }


    @Override
    public int getProfileCompletionPercentage( User user, String columnName, long columnValue,
        OrganizationUnitSettings organizationUnitSettings ) throws InvalidInputException
    {
        LOG.info( "Method to calculate profile completion percentage started." );

        if ( columnName == null || columnName.isEmpty() ) {
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column name is null or empty" );
        }
        if ( columnValue <= 0l ) {
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column value is invalid" );
        }
        if ( user == null ) {
            throw new InvalidInputException( "Wrong input parameter : passed input parameter user is null" );
        }
        if ( organizationUnitSettings == null ) {
            throw new InvalidInputException( "Wrong input parameter : passed input parameter organizationUnitSettings is null" );
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
        LOG.info( "Method to calculate profile completion percentage finished." );
        try {
            return (int) Math.round( currentWeight * 100 / totalWeight );
        } catch ( ArithmeticException e ) {
            LOG.error( "Exception caught in getProfileCompletionPercentage(). Nested exception is ", e );
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
        LOG.info( "Method to calculate number of badges started." );
        if ( surveyScore < 0 || surveyCount < 0 || socialPosts < 0 || profileCompleteness < 0 ) {
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
        int overallPercentage = (int) Math.round( normalizedSurveyScore + normalizedProfileCompleteness + normalizedSurveyCount
            + normalizedSocialPosts );
        if ( overallPercentage < 34 )
            badges = 1;
        else if ( overallPercentage < 67 )
            badges = 2;
        else
            badges = 3;
        LOG.info( "Method to calculate number of badges finished." );
        return badges;
    }


    @Transactional
    @Override
    public Map<String, Map<Integer, Integer>> getSurveyDetailsForGraph( String columnName, long columnValue, int numberOfDays,
        boolean realtechAdmin ) throws ParseException, InvalidInputException
    {
        LOG.info( "Getting survey details for graph for " + columnName + " with value " + columnValue + " for number of days "
            + numberOfDays + ". Reatech admin flag: " + realtechAdmin );

        if ( columnName == null || columnName.isEmpty() ) {
            throw new InvalidInputException( "Wrong input parameter : passed input parameter column name is null or empty" );
        }
        if ( columnValue <= 0l ) {
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
        Map<Integer, Integer> incompleteSurveys = surveyPreInitiationDao.getIncompletSurveyAggregationCount( companyId,
            agentId, CommonConstants.STATUS_ACTIVE, startDate, endDate, agentIds, criteria );
        LOG.debug( "Aggregating completed and incomplete surveys" );
        Map<Integer, Integer> allSurveysSent = aggregateAllSurveysSent( incompleteSurveys, completedSurveyToBeProcessed );

        LOG.debug( "Getting clicked surveys" );
        Map<Integer, Integer> clickedSurveys = surveyDetailsDao.getClickedSurveyAggregationCount( columnName, columnValue,
            startDate, endDate, criteria );

        LOG.debug( "Getting social posts count." );
        Map<Integer, Integer> socialPosts = surveyDetailsDao.getSocialPostsAggregationCount( columnName, columnValue,
            startDate, endDate, criteria );

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
            && ( incompleteSurveys == null || incompleteSurveys.size() > 0 ) ) {
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
        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Create a blank sheet
        XSSFSheet sheet = workbook.createSheet();
        XSSFDataFormat df = workbook.createDataFormat();
        CellStyle style = workbook.createCellStyle();

        //SS-1354: Using date format from CommonConstants
        style.setDataFormat( df.getFormat( CommonConstants.DATE_FORMAT ) );
        Integer counter = 1;

        // This data needs to be written (List<Object>)
        Map<String, List<Object>> data = new TreeMap<>();
        List<Object> surveyDetailsToPopulate = new ArrayList<>();
        for ( SurveyPreInitiation survey : surveyDetails ) {
            surveyDetailsToPopulate.add( survey.getCustomerFirstName() );
            surveyDetailsToPopulate.add( survey.getCustomerLastName() );
            surveyDetailsToPopulate.add( survey.getCustomerEmailId() );
            surveyDetailsToPopulate.add( survey.getCreatedOn() );
            surveyDetailsToPopulate.add( survey.getModifiedOn() );

            /*try {
                surveyDetailsToPopulate.add( surveyHandler.composeLink( survey.getAgentId(), survey.getCustomerEmailId(),
                    survey.getCustomerFirstName(), survey.getCustomerLastName() ) );
            } catch ( InvalidInputException e ) {
                LOG.error( "Invalid input exception caught in downloadIncompleteSurveyData(). Nested exception is ", e );
            }*/
            data.put( ( ++counter ).toString(), surveyDetailsToPopulate );
            surveyDetailsToPopulate = new ArrayList<>();
        }

        surveyDetailsToPopulate.add( "First Name" );
        surveyDetailsToPopulate.add( "Last Name" );
        surveyDetailsToPopulate.add( "Email Id" );
        surveyDetailsToPopulate.add( "Started On" );
        surveyDetailsToPopulate.add( "Last Updated On" );
        //surveyDetailsToPopulate.add( "Link To Survey" );

        data.put( "1", surveyDetailsToPopulate );

        // Iterate over data and write to sheet
        Set<String> keyset = data.keySet();
        int rownum = 0;
        for ( String key : keyset ) {
            Row row = sheet.createRow( rownum++ );
            List<Object> objArr = data.get( key );

            int cellnum = 0;
            for ( Object obj : objArr ) {
                Cell cell = row.createCell( cellnum++ );
                if ( obj instanceof String )
                    cell.setCellValue( (String) obj );
                else if ( obj instanceof Integer )
                    cell.setCellValue( (Integer) obj );
                else if ( obj instanceof Date ) {
                    cell.setCellStyle( style );
                    cell.setCellValue( (Date) obj );
                }
            }
        }

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

        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Create a blank sheet
        XSSFSheet sheet = workbook.createSheet();
        XSSFDataFormat df = workbook.createDataFormat();
        CellStyle style = workbook.createCellStyle();

        //SS-1354: Using date format from CommonConstants
        style.setDataFormat( df.getFormat( CommonConstants.DATE_FORMAT ) );
        Integer counter = 1;

        // Sorting SurveyResults
        Collections.sort( socialPosts, new SocialPostsComparator() );

        // This data needs to be written (List<Object>)
        Map<String, List<Object>> data = new TreeMap<>();
        List<Object> socialPostsToPopulate = new ArrayList<>();
        for ( SocialPost post : socialPosts ) {
            if ( post.getSource() != null && !post.getSource().isEmpty() ) {
                socialPostsToPopulate.add( post.getPostText() );
                socialPostsToPopulate.add( DATE_FORMATTER.format( new Date( post.getTimeInMillis() ) ) );
                socialPostsToPopulate.add( post.getSource() );
                try {
                    if ( post.getAgentId() > 0 ) {
                        socialPostsToPopulate.add( "user" );

                        socialPostsToPopulate.add( organizationManagementService.getAgentSettings( post.getAgentId() )
                            .getProfileName() );

                    } else if ( post.getBranchId() > 0 ) {
                        socialPostsToPopulate.add( "branch" );
                        socialPostsToPopulate.add( organizationManagementService.getBranchSettingsDefault( post.getBranchId() )
                            .getProfileName() );
                    } else if ( post.getRegionId() > 0 ) {
                        socialPostsToPopulate.add( "region" );
                        socialPostsToPopulate.add( organizationManagementService.getRegionSettings( post.getRegionId() )
                            .getProfileName() );
                    } else if ( post.getCompanyId() > 0 ) {
                        socialPostsToPopulate.add( "company" );
                        socialPostsToPopulate.add( organizationManagementService.getCompanySettings( post.getCompanyId() )
                            .getProfileName() );
                    } else {
                        socialPostsToPopulate.add( "unavailable" );
                        socialPostsToPopulate.add( "unavailable" );
                    }
                    socialPostsToPopulate.add( post.getPostedBy() );
                    socialPostsToPopulate.add( post.getPostUrl() );
                } catch ( InvalidInputException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch ( NoRecordsFetchedException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                data.put( ( ++counter ).toString(), socialPostsToPopulate );
                socialPostsToPopulate = new ArrayList<>();
            }
        }

        // Setting up headers
        socialPostsToPopulate.add( CommonConstants.HEADER_POST_COMMENT );
        socialPostsToPopulate.add( CommonConstants.HEADER_POST_DATE );
        socialPostsToPopulate.add( CommonConstants.HEADER_POST_SOURCE );
        socialPostsToPopulate.add( CommonConstants.HEADER_POST_LEVEL );
        socialPostsToPopulate.add( CommonConstants.HEADER_POST_LEVEL_NAME );
        socialPostsToPopulate.add( CommonConstants.HEADER_POSTED_BY );
        socialPostsToPopulate.add( CommonConstants.HEADER_POST_URL );


        data.put( "1", socialPostsToPopulate );

        // Iterate over data and write to sheet
        Set<String> keyset = data.keySet();
        int rownum = 0;
        for ( String key : keyset ) {
            Row row = sheet.createRow( rownum++ );
            List<Object> objArr = data.get( key );
            int cellnum = 0;
            for ( Object obj : objArr ) {
                Cell cell = row.createCell( cellnum++ );
                if ( obj instanceof String )
                    cell.setCellValue( (String) obj );
                else if ( obj instanceof Integer )
                    cell.setCellValue( (Integer) obj );
                else if ( obj instanceof Double )
                    cell.setCellValue( (Double) obj );
                else if ( obj instanceof Date ) {
                    cell.setCellStyle( style );
                    cell.setCellValue( (Date) obj );
                }
            }
        }
        return workbook;
    }


    /*
     * Method to create excel file from all the completed survey data.
     */
    @Override
    public XSSFWorkbook downloadCustomerSurveyResultsData( List<SurveyDetails> surveyDetails, String fileLocation )
        throws IOException, InvalidInputException
    {
        if ( fileLocation == null || fileLocation.isEmpty() ) {
            throw new InvalidInputException( "Invalid input parameter : passed input parameter fileLocation is null or empty" );
        }
        if ( surveyDetails == null ) {
            throw new InvalidInputException( "Invalid input parameter : passed input parameter surveyDetails is null" );
        }

        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Create a blank sheet
        XSSFSheet sheet = workbook.createSheet();
        XSSFDataFormat df = workbook.createDataFormat();
        CellStyle style = workbook.createCellStyle();

        //SS-1354: Using date format from CommonConstants
        style.setDataFormat( df.getFormat( CommonConstants.DATE_FORMAT ) );
        Integer counter = 1;

        // Sorting SurveyResults
        Collections.sort( surveyDetails, new SurveyResultsComparator() );

        //create rating format to format survey score
        DecimalFormat ratingFormat = CommonConstants.SOCIAL_RANKING_FORMAT;
        ratingFormat.setMinimumFractionDigits( 1 );
        ratingFormat.setMaximumFractionDigits( 1 );

        // Finding max questions
        int max = 0;
        int internalMax = 0;
        for ( SurveyDetails survey : surveyDetails ) {
            if ( survey.getSurveyResponse() != null ) {
                internalMax = survey.getSurveyResponse().size();
                if ( internalMax > max ) {
                    max = internalMax;
                }
            }
        }

        // This data needs to be written (List<Object>)
        Map<String, List<Object>> data = new TreeMap<>();
        List<Object> surveyDetailsToPopulate = new ArrayList<>();
        for ( SurveyDetails survey : surveyDetails ) {
            // exclude reviews which dont have survey answers, like zillow
            if ( survey.getSurveyResponse() != null ) {
                String agentName = survey.getAgentName();
                surveyDetailsToPopulate.add( agentName.substring( 0, agentName.lastIndexOf( ' ' ) ) );
                surveyDetailsToPopulate.add( agentName.substring( agentName.lastIndexOf( ' ' ) + 1 ) );
                surveyDetailsToPopulate.add( survey.getCustomerFirstName() );
                surveyDetailsToPopulate.add( survey.getCustomerLastName() );
                surveyDetailsToPopulate.add( DATE_FORMATTER.format( survey.getCreatedOn() ) );
                surveyDetailsToPopulate.add( DATE_FORMATTER.format( survey.getModifiedOn() ) );
                surveyDetailsToPopulate.add( Days.daysBetween( new DateTime( survey.getCreatedOn() ),
                    new DateTime( survey.getModifiedOn() ) ).getDays() );

                if ( survey.getSource() != null && !survey.getSource().isEmpty() ) {
                    if ( survey.getSource().equals( CommonConstants.SURVEY_REQUEST_AGENT ) )
                        surveyDetailsToPopulate.add( "user" );
                    else
                        surveyDetailsToPopulate.add( survey.getSource() );
                } else {
                    surveyDetailsToPopulate.add( MongoSocialPostDaoImpl.KEY_SOURCE_SS );
                }

                //add score
                surveyDetailsToPopulate.add( ratingFormat.format( survey.getScore() ) );
                for ( SurveyResponse response : survey.getSurveyResponse() ) {
                    surveyDetailsToPopulate.add( response.getAnswer() );
                }

                surveyDetailsToPopulate.add( survey.getMood() );
                surveyDetailsToPopulate.add( survey.getReview() );
                if ( survey.getMood() != null && survey.getMood().equals( CommonConstants.SURVEY_MOOD_GREAT )
                    && survey.getAgreedToShare() != null && !survey.getAgreedToShare().isEmpty() ) {
                    String status = survey.getAgreedToShare();
                    if ( status.equals( "true" ) ) {
                        surveyDetailsToPopulate.add( CommonConstants.STATUS_YES );
                    } else {
                        surveyDetailsToPopulate.add( CommonConstants.STATUS_NO );
                    }
                } else {
                    surveyDetailsToPopulate.add( CommonConstants.STATUS_NO );
                }
                /*if ( survey.getAgreedToShare() != null && !survey.getAgreedToShare().isEmpty() ) {
                    String status = survey.getAgreedToShare();
                    if ( status.equals( "true" ) ) {
                        surveyDetailsToPopulate.add( CommonConstants.STATUS_YES );
                    } else {
                        surveyDetailsToPopulate.add( CommonConstants.STATUS_NO );
                    }
                } else if ( survey.getSharedOn() != null && !survey.getSharedOn().isEmpty() ) {
                    surveyDetailsToPopulate.add( CommonConstants.STATUS_YES );
                } else {
                    surveyDetailsToPopulate.add( CommonConstants.STATUS_NO );
                }*/
                if ( survey.getSocialMediaPostDetails() != null ) {
                    Set<String> socialMedia = new HashSet<>();
                    if ( survey.getSocialMediaPostDetails().getCompanyMediaPostDetails() != null
                        && survey.getSocialMediaPostDetails().getCompanyMediaPostDetails().getSharedOn() != null
                        && !survey.getSocialMediaPostDetails().getCompanyMediaPostDetails().getSharedOn().isEmpty() ) {
                        socialMedia.addAll( survey.getSocialMediaPostDetails().getCompanyMediaPostDetails().getSharedOn() );
                    }
                    if ( survey.getSocialMediaPostDetails().getAgentMediaPostDetails() != null
                        && survey.getSocialMediaPostDetails().getAgentMediaPostDetails().getSharedOn() != null
                        && !survey.getSocialMediaPostDetails().getAgentMediaPostDetails().getSharedOn().isEmpty() ) {
                        socialMedia.addAll( survey.getSocialMediaPostDetails().getAgentMediaPostDetails().getSharedOn() );
                    }
                    if ( survey.getSocialMediaPostDetails().getRegionMediaPostDetailsList() != null
                        && !survey.getSocialMediaPostDetails().getRegionMediaPostDetailsList().isEmpty() ) {
                        for ( RegionMediaPostDetails regionMediaDetail : survey.getSocialMediaPostDetails()
                            .getRegionMediaPostDetailsList() ) {
                            if ( regionMediaDetail.getSharedOn() != null && !regionMediaDetail.getSharedOn().isEmpty() ) {
                                socialMedia.addAll( regionMediaDetail.getSharedOn() );
                            }
                        }
                    }
                    if ( survey.getSocialMediaPostDetails().getBranchMediaPostDetailsList() != null
                        && !survey.getSocialMediaPostDetails().getBranchMediaPostDetailsList().isEmpty() ) {
                        for ( BranchMediaPostDetails branchMediaDetail : survey.getSocialMediaPostDetails()
                            .getBranchMediaPostDetailsList() ) {
                            if ( branchMediaDetail.getSharedOn() != null && !branchMediaDetail.getSharedOn().isEmpty() ) {
                                socialMedia.addAll( branchMediaDetail.getSharedOn() );
                            }
                        }
                    }
                    surveyDetailsToPopulate.add( StringUtils.join( socialMedia, "," ) );
                }

                data.put( ( ++counter ).toString(), surveyDetailsToPopulate );
                surveyDetailsToPopulate = new ArrayList<>();
            }
        }

        // Setting up headers
        surveyDetailsToPopulate.add( CommonConstants.HEADER_AGENT_FIRST_NAME );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_AGENT_LAST_NAME );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_CUSTOMER_FIRST_NAME );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_CUSTOMER_LAST_NAME );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_SURVEY_SENT_DATE );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_SURVEY_COMPLETED_DATE );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_SURVEY_TIME_INTERVAL );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_SURVEY_SOURCE );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_SURVEY_SCORE );
        for ( counter = 1; counter <= max; counter++ ) {
            surveyDetailsToPopulate.add( CommonConstants.HEADER_SURVEY_QUESTION + counter );
        }
        surveyDetailsToPopulate.add( CommonConstants.HEADER_SURVEY_GATEWAY );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_CUSTOMER_COMMENTS );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_AGREED_SHARE );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_CLICK_THROUGH );

        data.put( "1", surveyDetailsToPopulate );

        // Iterate over data and write to sheet
        Set<String> keyset = data.keySet();
        int rownum = 0;
        for ( String key : keyset ) {
            Row row = sheet.createRow( rownum++ );
            List<Object> objArr = data.get( key );
            int cellnum = 0;
            for ( Object obj : objArr ) {
                Cell cell = row.createCell( cellnum++ );
                if ( obj instanceof String )
                    cell.setCellValue( (String) obj );
                else if ( obj instanceof Integer )
                    cell.setCellValue( (Integer) obj );
                else if ( obj instanceof Double )
                    cell.setCellValue( (Double) obj );
                else if ( obj instanceof Date ) {
                    cell.setCellStyle( style );
                    cell.setCellValue( (Date) obj );
                }
            }
        }
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

        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Create a blank sheet
        XSSFSheet sheet = workbook.createSheet();
        XSSFDataFormat df = workbook.createDataFormat();
        CellStyle style = workbook.createCellStyle();

        //SS-1354: Using date format from CommonConstants
        style.setDataFormat( df.getFormat( CommonConstants.DATE_FORMAT ) );
        Integer counter = 1;

        // Sorting AgentRankingReports
        Collections.sort( agentDetails, new AgentRankingReportComparator() );

        // This data needs to be written (List<Object>)
        Map<String, List<Object>> data = new TreeMap<>();
        List<Object> surveyDetailsToPopulate = new ArrayList<>();
        int agentRank = 1;
        for ( AgentRankingReport agentDetail : agentDetails ) {
            surveyDetailsToPopulate.add( agentRank++ );
            surveyDetailsToPopulate.add( agentDetail.getAgentFirstName() );
            surveyDetailsToPopulate.add( agentDetail.getAgentLastName() );
            surveyDetailsToPopulate.add( agentDetail.getAverageScore() );
            surveyDetailsToPopulate.add( agentDetail.getIncompleteSurveys() + agentDetail.getCompletedSurveys() );

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis( agentDetail.getRegistrationDate() );
            surveyDetailsToPopulate.add( DATE_FORMATTER.format( calendar.getTime() ) );

            data.put( ( ++counter ).toString(), surveyDetailsToPopulate );
            surveyDetailsToPopulate = new ArrayList<>();
        }

        // Setting up headers
        surveyDetailsToPopulate.add( CommonConstants.HEADER_AGENT_RANK );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_FIRST_NAME );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_LAST_NAME );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_AVG_SCORE );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_SUM_SURVEYS );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_REGISTRATION_DATE );

        data.put( "1", surveyDetailsToPopulate );

        // Iterate over data and write to sheet
        Set<String> keyset = data.keySet();
        int rownum = 0;
        for ( String key : keyset ) {
            Row row = sheet.createRow( rownum++ );
            List<Object> objArr = data.get( key );
            int cellnum = 0;
            for ( Object obj : objArr ) {
                Cell cell = row.createCell( cellnum++ );
                if ( obj instanceof String )
                    cell.setCellValue( (String) obj );
                else if ( obj instanceof Integer )
                    cell.setCellValue( (Integer) obj );
                else if ( obj instanceof Double )
                    cell.setCellValue( (Double) obj );
                else if ( obj instanceof Long )
                    cell.setCellValue( (Long) obj );
                else if ( obj instanceof Date ) {
                    cell.setCellStyle( style );
                    cell.setCellValue( (Date) obj );
                }
            }
        }
        return workbook;
    }


    /**
     * Method to create excel file for user adoption report.
     */
    @Override
    @Transactional
    public XSSFWorkbook downloadUserAdoptionReportData( long companyId ) throws InvalidInputException,
        NoRecordsFetchedException
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

        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Create a blank sheet
        XSSFSheet sheet = workbook.createSheet();
        Integer counter = 1;

        // This data needs to be written (List<Object>)
        Map<String, List<Object>> data = new TreeMap<>();
        List<Object> userAdoptionReportToPopulate = new ArrayList<>();

        for ( Object[] row : rows ) {
            // row 0 - company
            // row 1 - region
            // row 2 - branch
            // row 3 - invited users
            // row 4 - active users
            // row 5 - adoption rate
            userAdoptionReportToPopulate.add( String.valueOf( row[0] ) );
            if ( row[1] != null && !CommonConstants.DEFAULT_REGION_NAME.equalsIgnoreCase( String.valueOf( row[1] ) ) )
                userAdoptionReportToPopulate.add( String.valueOf( row[1] ) );
            else
                userAdoptionReportToPopulate.add( "" );
            if ( row[2] != null && !CommonConstants.DEFAULT_BRANCH_NAME.equalsIgnoreCase( String.valueOf( row[2] ) ) )
                userAdoptionReportToPopulate.add( String.valueOf( row[2] ) );
            else
                userAdoptionReportToPopulate.add( "" );
            Integer userCount = new Integer( String.valueOf( row[3] ) );
            Integer activeUserCount = new Integer( String.valueOf( row[4] ) );
            String adoptionRate = String.valueOf( row[5] ).replace( "\\.00", "" );
            userAdoptionReportToPopulate.add( userCount );
            userAdoptionReportToPopulate.add( activeUserCount );
            userAdoptionReportToPopulate.add( adoptionRate != "null" ? adoptionRate : "0%" );

            data.put( ( ++counter ).toString(), userAdoptionReportToPopulate );
            userAdoptionReportToPopulate = new ArrayList<>();
        }

        // Setting up headers
        userAdoptionReportToPopulate.add( CommonConstants.HEADER_COMPANY );
        userAdoptionReportToPopulate.add( CommonConstants.HEADER_REGION );
        userAdoptionReportToPopulate.add( CommonConstants.HEADER_BRANCH );
        userAdoptionReportToPopulate.add( CommonConstants.HEADER_INVITED_USERS );
        userAdoptionReportToPopulate.add( CommonConstants.HEADER_ACTIVE_USERS );
        userAdoptionReportToPopulate.add( CommonConstants.HEADER_ADOPTION_RATES );

        data.put( "1", userAdoptionReportToPopulate );

        // Iterate over data and write to sheet
        Set<String> keyset = data.keySet();
        DecimalFormat decimalFormat = new DecimalFormat( "#0" );
        decimalFormat.setRoundingMode( RoundingMode.DOWN );

        int rownum = 0;
        for ( String key : keyset ) {
            Row row = sheet.createRow( rownum++ );
            List<Object> objArr = data.get( key );
            int cellnum = 0;
            for ( Object obj : objArr ) {
                Cell cell = row.createCell( cellnum++ );
                if ( obj instanceof String )
                    cell.setCellValue( (String) obj );
                else if ( obj instanceof Integer )
                    cell.setCellValue( (Integer) obj );
                else if ( obj instanceof Double )
                    cell.setCellValue( decimalFormat.format( obj ) );
            }
        }
        return workbook;
    }


    /**
     * Method to return records of billing report data based on start index and batch size
     */
    @Override
    @Transactional
    public List<BillingReportData> getBillingReportRecords( int startIndex, int batchSize )
    {
        LOG.info( "Method getBillingReportRecords started for startIndex : " + startIndex + " and batchSize : " + batchSize );
        return companyDao.getAllUsersInCompanysForBillingReport( startIndex, batchSize );
    }


    /**
     * Method to check if billing report entries exist
     */
    @Transactional
    @Override
    public List<FileUpload> getBillingReportToBeSent() throws NoRecordsFetchedException
    {
        LOG.info( "Check if billing report entries exist" );
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.FILE_UPLOAD_TYPE_COLUMN, CommonConstants.FILE_UPLOAD_BILLING_REPORT );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        List<FileUpload> filesToBeUploaded = fileUploadDao.findByKeyValue( FileUpload.class, queries );
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
    public XSSFWorkbook downloadCompanyHierarchyReportData( long companyId ) throws InvalidInputException,
        NoRecordsFetchedException
    {
        if ( companyId <= 0l ) {
            throw new InvalidInputException( "Invalid input parameter : passed input parameter companyId is invalid" );
        }

        Company company = companyDao.findById( Company.class, companyId );
        Region defaultRegion = organizationManagementService.getDefaultRegionForCompany( company );
        Branch defaultBranchOfDefaultRegion = organizationManagementService.getDefaultBranchForRegion( defaultRegion
            .getRegionId() );
        List<Region> regionList = new ArrayList<Region>();
        List<Branch> branchList = new ArrayList<Branch>();
        List<Branch> defaultBranchList = new ArrayList<Branch>();
        List<User> userList = new ArrayList<User>();


        int batch = 50;
        try {
            int start = 0;
            List<Region> batchRegionList = new ArrayList<Region>();
            do {
                batchRegionList = regionDao.getRegionsForCompany( companyId, start, batch );
                if ( batchRegionList != null && batchRegionList.size() > 0 )
                    regionList.addAll( batchRegionList );
                start += batch;
            } while ( batchRegionList != null && batchRegionList.size() == batch );

            start = 0;
            List<Branch> batchBranchList = new ArrayList<Branch>();
            do {
                batchBranchList = branchDao.getBranchesForCompany( companyId, CommonConstants.NO, start, batch );
                if ( batchBranchList != null && batchBranchList.size() > 0 )
                    branchList.addAll( batchBranchList );
                start += batch;
            } while ( batchBranchList != null && batchBranchList.size() == batch );

            start = 0;
            batchBranchList = new ArrayList<Branch>();
            do {
                batchBranchList = branchDao.getBranchesForCompany( companyId, CommonConstants.YES, start, batch );
                if ( batchBranchList != null && batchBranchList.size() > 0 )
                    defaultBranchList.addAll( batchBranchList );
                start += batch;
            } while ( batchBranchList != null && batchBranchList.size() == batch );

            start = 0;
            List<User> batchUserList = new ArrayList<User>();
            do {
                batchUserList = userDao.getUsersForCompany( company, start, batch );
                if ( batchUserList != null && batchUserList.size() > 0 )
                    userList.addAll( batchUserList );
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
        User companyAdmin = null;
        if ( userList != null && userList.size() > 0 ) {
            for ( User user : userList ) {
                String regionId = "";
                String regionIdAsAdmin = "";
                String branchId = "";
                String branchIdAsAdmin = "";
                boolean isCompanyAdminHasAnotherRole = false;
                boolean isCompanyAdmin = user.getIsOwner() == CommonConstants.YES;
                List<UserProfile> userProfileList = user.getUserProfiles();
                if ( userProfileList != null && userProfileList.size() > 0 ) {
                    for ( UserProfile userProfile : userProfileList ) {
                        if ( userProfile.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                            if ( defaultRegion.getRegionId() != userProfile.getRegionId()
                                && CommonConstants.DEFAULT_REGION_ID != userProfile.getRegionId()
                                && !regionId.contains( String.valueOf( userProfile.getRegionId() ) ) )
                                regionId += userProfile.getRegionId() + ",";
                            if ( !defaultBranchIdList.contains( userProfile.getBranchId() )
                                && CommonConstants.DEFAULT_BRANCH_ID != userProfile.getBranchId()
                                && defaultBranchOfDefaultRegion.getBranchId() != userProfile.getBranchId()
                                && !branchId.contains( String.valueOf( userProfile.getBranchId() ) ) )
                                branchId += userProfile.getBranchId() + ",";
                            if ( userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID
                                && !agentIds.contains( user.getUserId() ) ) {
                                agentIds.add( user.getUserId() );
                                if ( isCompanyAdmin )
                                    isCompanyAdminHasAnotherRole = true;
                            }
                            if ( userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                                if ( defaultRegion.getRegionId() != userProfile.getRegionId()
                                    && CommonConstants.DEFAULT_REGION_ID != userProfile.getRegionId()
                                    && !regionIdAsAdmin.contains( String.valueOf( userProfile.getRegionId() ) ) ) {
                                    regionIdAsAdmin += userProfile.getRegionId() + ",";
                                    if ( isCompanyAdmin )
                                        isCompanyAdminHasAnotherRole = true;
                                }
                            }
                            if ( userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                                if ( !defaultBranchIdList.contains( userProfile.getBranchId() )
                                    && CommonConstants.DEFAULT_BRANCH_ID != userProfile.getBranchId()
                                    && defaultBranchOfDefaultRegion.getBranchId() != userProfile.getBranchId()
                                    && !branchIdAsAdmin.contains( String.valueOf( userProfile.getBranchId() ) ) ) {
                                    branchIdAsAdmin += userProfile.getBranchId() + ",";
                                    if ( isCompanyAdmin )
                                        isCompanyAdminHasAnotherRole = true;
                                }
                            }
                        }
                    }
                }
                if ( regionId.length() > 0 )
                    userIdRegionIdsMap.put( user.getUserId(), regionId.substring( 0, regionId.length() - 1 ) );
                if ( branchId.length() > 0 )
                    userIdBranchIdsMap.put( user.getUserId(), branchId.substring( 0, branchId.length() - 1 ) );
                if ( regionIdAsAdmin.length() > 0 )
                    userIdRegionAsAdminIdsMap.put( user.getUserId(),
                        regionIdAsAdmin.substring( 0, regionIdAsAdmin.length() - 1 ) );
                if ( branchIdAsAdmin.length() > 0 )
                    userIdBranchAsAdminIdsMap.put( user.getUserId(),
                        branchIdAsAdmin.substring( 0, branchIdAsAdmin.length() - 1 ) );
                if ( !isCompanyAdmin || isCompanyAdminHasAnotherRole )
                    userIdList.add( user.getUserId() );
                else
                    companyAdmin = user;

            }
        }

        if ( companyAdmin != null ) {
            userList.remove( companyAdmin );
        }

        if ( userIdList.size() > 0 ) {
            List<AgentSettings> agentSettingsList = organizationUnitSettingsDao.fetchMultipleAgentSettingsById( userIdList );
            if ( agentSettingsList != null && agentSettingsList.size() > 0 ) {
                for ( AgentSettings agentSettings : agentSettingsList ) {
                    userIdSettingsMap.put( agentSettings.getIden(), agentSettings );
                }
            }
        }

        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Create a blank sheet
        XSSFSheet userSheet = workbook.createSheet( "Users" );
        XSSFSheet branchSheet = workbook.createSheet( "Offices" );
        XSSFSheet regionSheet = workbook.createSheet( "Regions" );
        Integer usersCounter = 2;
        Integer branchesCounter = 2;
        Integer regionsCounter = 2;

        // This data needs to be written (List<Object>)
        Map<Integer, List<Object>> usersData = new TreeMap<>();
        Map<Integer, List<Object>> branchesData = new TreeMap<>();
        Map<Integer, List<Object>> regionsData = new TreeMap<>();
        List<Object> userReportToPopulate = new ArrayList<>();
        List<Object> branchesReportToPopulate = new ArrayList<>();
        List<Object> regionsReportToPopulate = new ArrayList<>();

        // loop on users to populate users sheet
        if ( userList != null && userList.size() > 0 ) {
            for ( User user : userList ) {
                // col 0 - user id
                // col 1 -  firstname
                // col 2 - last name
                // col 3 - title
                // col 4 - branch ids 
                // col 5 - region ids
                // col 6 - public page - Yes if user is an agent 
                // col 7 - branch ids where he is admin
                // col 8 - region ids where he is admin
                // col 9 - email
                // col 10 - phone
                // col 11 - website
                // col 12 - license
                // col 13 - legal disclaimer
                // col 14 - photo - profile image url
                // col 15 - about me
                AgentSettings userSettings = userIdSettingsMap.get( user.getUserId() );

                userReportToPopulate.add( user.getUserId() );
                userReportToPopulate.add( user.getFirstName() );
                if ( user.getLastName() != null && !user.getLastName().trim().equalsIgnoreCase( "" )
                    && !user.getLastName().trim().equalsIgnoreCase( "null" ) )
                    userReportToPopulate.add( user.getLastName() );
                else
                    userReportToPopulate.add( "" );
                if ( userSettings != null && userSettings.getContact_details() != null
                    && userSettings.getContact_details().getTitle() != null )
                    userReportToPopulate.add( userSettings.getContact_details().getTitle() );
                else
                    userReportToPopulate.add( "" );
                if ( userIdBranchIdsMap.get( user.getUserId() ) != null
                    && userIdBranchIdsMap.get( user.getUserId() ).length() > 0 )
                    userReportToPopulate.add( userIdBranchIdsMap.get( user.getUserId() ) );
                else
                    userReportToPopulate.add( "" );
                if ( userIdRegionIdsMap.get( user.getUserId() ) != null
                    && userIdRegionIdsMap.get( user.getUserId() ).length() > 0 )
                    userReportToPopulate.add( userIdRegionIdsMap.get( user.getUserId() ) );
                else
                    userReportToPopulate.add( "" );
                if ( agentIds.size() > 0 && agentIds.contains( user.getUserId() ) )
                    userReportToPopulate.add( CommonConstants.CHR_YES );
                else
                    userReportToPopulate.add( CommonConstants.CHR_NO );
                if ( userIdBranchAsAdminIdsMap.get( user.getUserId() ) != null
                    && userIdBranchAsAdminIdsMap.get( user.getUserId() ).length() > 0 )
                    userReportToPopulate.add( userIdBranchAsAdminIdsMap.get( user.getUserId() ) );
                else
                    userReportToPopulate.add( "" );
                if ( userIdRegionAsAdminIdsMap.get( user.getUserId() ) != null
                    && userIdRegionAsAdminIdsMap.get( user.getUserId() ).length() > 0 )
                    userReportToPopulate.add( userIdRegionAsAdminIdsMap.get( user.getUserId() ) );
                else
                    userReportToPopulate.add( "" );
                userReportToPopulate.add( user.getEmailId() );
                if ( userSettings != null && userSettings.getContact_details() != null
                    && userSettings.getContact_details().getContact_numbers() != null
                    && userSettings.getContact_details().getContact_numbers().getWork() != null )
                    userReportToPopulate.add( userSettings.getContact_details().getContact_numbers().getWork() );
                else
                    userReportToPopulate.add( "" );
                if ( userSettings != null && userSettings.getContact_details() != null
                    && userSettings.getContact_details().getWeb_addresses() != null
                    && userSettings.getContact_details().getWeb_addresses().getWork() != null )
                    userReportToPopulate.add( userSettings.getContact_details().getWeb_addresses().getWork() );
                else
                    userReportToPopulate.add( "" );
                if ( userSettings != null && userSettings.getLicenses() != null ) {
                    Licenses licenses = userSettings.getLicenses();
                    List<String> authorizedInList = licenses.getAuthorized_in();
                    String authorizedIns = "";
                    if ( authorizedInList != null && authorizedInList.size() > 0 ) {
                        for ( String authorizedIn : authorizedInList ) {
                            authorizedIns += authorizedIn + ",";
                        }
                        userReportToPopulate.add( authorizedIns.substring( 0, authorizedIns.length() - 1 ) );
                    } else
                        userReportToPopulate.add( "" );
                } else
                    userReportToPopulate.add( "" );
                if ( userSettings != null && userSettings.getDisclaimer() != null && userSettings.getDisclaimer().length() > 0 )
                    userReportToPopulate.add( userSettings.getDisclaimer() );
                else
                    userReportToPopulate.add( "" );
                if ( userSettings != null && userSettings.getProfileImageUrl() != null )
                    userReportToPopulate.add( userSettings.getProfileImageUrl() );
                else
                    userReportToPopulate.add( "" );
                if ( userSettings != null && userSettings.getContact_details().getAbout_me() != null
                    && userSettings.getContact_details().getAbout_me().length() > 0 )
                    userReportToPopulate.add( userSettings.getContact_details().getAbout_me() );
                else
                    userReportToPopulate.add( "" );

                usersData.put( ( ++usersCounter ), userReportToPopulate );
                userReportToPopulate = new ArrayList<>();
            }
        }
        // Setting up user sheet headers
        userReportToPopulate.add( CommonConstants.CHR_USERS_USER_ID );
        userReportToPopulate.add( CommonConstants.CHR_USERS_FIRST_NAME );
        userReportToPopulate.add( CommonConstants.CHR_USERS_LAST_NAME );
        userReportToPopulate.add( CommonConstants.CHR_USERS_TITLE );
        userReportToPopulate.add( CommonConstants.CHR_USERS_OFFICE_ASSIGNMENTS );
        userReportToPopulate.add( CommonConstants.CHR_USERS_REGION_ASSIGNMENTS );
        userReportToPopulate.add( CommonConstants.CHR_USERS_PUBLIC_PROFILE );
        userReportToPopulate.add( CommonConstants.CHR_USERS_OFFICE_ADMIN_PRIVILEGE );
        userReportToPopulate.add( CommonConstants.CHR_USERS_REGION_ADMIN_PRIVILEGE );
        userReportToPopulate.add( CommonConstants.CHR_USERS_EMAIL );
        userReportToPopulate.add( CommonConstants.CHR_USERS_PHONE );
        userReportToPopulate.add( CommonConstants.CHR_USERS_WEBSITE );
        userReportToPopulate.add( CommonConstants.CHR_USERS_LICENSE );
        userReportToPopulate.add( CommonConstants.CHR_USERS_LEGAL_DISCLAIMER );
        userReportToPopulate.add( CommonConstants.CHR_USERS_PHOTO );
        userReportToPopulate.add( CommonConstants.CHR_USERS_ABOUT_ME_DESCRIPTION );

        usersData.put( 1, userReportToPopulate );

        userReportToPopulate = new ArrayList<>();

        // setting up user sheet header descriptions
        userReportToPopulate.add( CommonConstants.CHR_USERS_USER_ID_DESC );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( "" );
        userReportToPopulate.add( CommonConstants.CHR_USERS_TITLE_DESC );
        userReportToPopulate.add( CommonConstants.CHR_USERS_OFFICE_ASSIGNMENTS_DESC );
        userReportToPopulate.add( CommonConstants.CHR_USERS_REGION_ASSIGNMENTS_DESC );
        userReportToPopulate.add( CommonConstants.CHR_USERS_PUBLIC_PROFILE_DESC );
        userReportToPopulate.add( CommonConstants.CHR_USERS_OFFICE_ADMIN_PRIVILEGE_DESC );
        userReportToPopulate.add( CommonConstants.CHR_USERS_REGION_ADMIN_PRIVILEGE_DESC );
        userReportToPopulate.add( CommonConstants.CHR_USERS_EMAIL_DESC );
        userReportToPopulate.add( CommonConstants.CHR_USERS_PHONE_DESC );
        userReportToPopulate.add( CommonConstants.CHR_USERS_WEBSITE_DESC );
        userReportToPopulate.add( CommonConstants.CHR_USERS_LICENSE_DESC );
        userReportToPopulate.add( CommonConstants.CHR_USERS_LEGAL_DISCLAIMER_DESC );
        userReportToPopulate.add( CommonConstants.CHR_USERS_PHOTO_DESC );
        userReportToPopulate.add( CommonConstants.CHR_USERS_ABOUT_ME_DESCRIPTION_DESC );

        usersData.put( 2, userReportToPopulate );

        // loop on branches to populate branches sheet
        if ( branchList != null && branchList.size() > 0 ) {
            for ( Branch branch : branchList ) {
                // col 0 office id
                // col 1 office name
                // col 2 region id
                // col 3 address 1
                // col 4 address 2
                // col 5 city
                // col 6 state
                // col 7 zip
                branchesReportToPopulate.add( branch.getBranchId() );
                branchesReportToPopulate.add( branch.getBranch() );
                if ( branch.getRegion() != null )
                    branchesReportToPopulate.add( branch.getRegion().getRegionId() );
                else
                    branchesReportToPopulate.add( "" );
                branchesReportToPopulate.add( branch.getAddress1() );
                branchesReportToPopulate.add( branch.getAddress2() );
                branchesReportToPopulate.add( branch.getCity() );
                branchesReportToPopulate.add( branch.getState() );
                branchesReportToPopulate.add( branch.getZipcode() );

                branchesData.put( ( ++branchesCounter ), branchesReportToPopulate );
                branchesReportToPopulate = new ArrayList<>();
            }
        }

        // Setting up branch sheet headers
        branchesReportToPopulate.add( CommonConstants.CHR_BRANCH_BRANCH_ID );
        branchesReportToPopulate.add( CommonConstants.CHR_BRANCH_BRANCH_NAME );
        branchesReportToPopulate.add( CommonConstants.CHR_REGION_REGION_ID );
        branchesReportToPopulate.add( CommonConstants.CHR_ADDRESS_1 );
        branchesReportToPopulate.add( CommonConstants.CHR_ADDRESS_2 );
        branchesReportToPopulate.add( CommonConstants.CHR_CITY );
        branchesReportToPopulate.add( CommonConstants.CHR_STATE );
        branchesReportToPopulate.add( CommonConstants.CHR_ZIP );

        branchesData.put( 1, branchesReportToPopulate );

        branchesReportToPopulate = new ArrayList<>();

        // setting up branch sheet header descriptions
        branchesReportToPopulate.add( CommonConstants.CHR_ID_DESC );
        branchesReportToPopulate.add( "" );
        branchesReportToPopulate.add( CommonConstants.CHR_BRANCH_REGION_ID_DESC );
        branchesReportToPopulate.add( "" );
        branchesReportToPopulate.add( "" );
        branchesReportToPopulate.add( "" );
        branchesReportToPopulate.add( "" );
        branchesReportToPopulate.add( "" );

        branchesData.put( 2, branchesReportToPopulate );

        // loop on region to populate region sheet
        if ( regionList != null && regionList.size() > 0 ) {
            for ( Region region : regionList ) {
                // col 0 region id
                // col 1 region name
                // col 2 address 1
                // col 3 address 2
                // col 4 city
                // col 5 state
                // col 6 zip
                regionsReportToPopulate.add( region.getRegionId() );
                regionsReportToPopulate.add( region.getRegion() );
                regionsReportToPopulate.add( region.getAddress1() );
                regionsReportToPopulate.add( region.getAddress2() );
                regionsReportToPopulate.add( region.getCity() );
                regionsReportToPopulate.add( region.getState() );
                regionsReportToPopulate.add( region.getZipcode() );

                regionsData.put( ( ++regionsCounter ), regionsReportToPopulate );
                regionsReportToPopulate = new ArrayList<>();
            }
        }
        // Setting up branch sheet headers
        regionsReportToPopulate.add( CommonConstants.CHR_REGION_REGION_ID );
        regionsReportToPopulate.add( CommonConstants.CHR_REGION_REGION_NAME );
        regionsReportToPopulate.add( CommonConstants.CHR_ADDRESS_1 );
        regionsReportToPopulate.add( CommonConstants.CHR_ADDRESS_2 );
        regionsReportToPopulate.add( CommonConstants.CHR_CITY );
        regionsReportToPopulate.add( CommonConstants.CHR_STATE );
        regionsReportToPopulate.add( CommonConstants.CHR_ZIP );

        regionsData.put( 1, regionsReportToPopulate );

        regionsReportToPopulate = new ArrayList<>();

        // setting up branch sheet header descriptions
        regionsReportToPopulate.add( CommonConstants.CHR_ID_DESC );
        regionsReportToPopulate.add( CommonConstants.CHR_REGION_REGION_NAME_DESC );
        regionsReportToPopulate.add( "" );
        regionsReportToPopulate.add( "" );
        regionsReportToPopulate.add( "" );
        regionsReportToPopulate.add( "" );
        regionsReportToPopulate.add( "" );

        regionsData.put( 2, regionsReportToPopulate );


        // Iterate over data and write to sheet
        Set<Integer> keyset = usersData.keySet();

        int rownum = 0;
        for ( Integer key : keyset ) {
            Row row = userSheet.createRow( rownum++ );
            List<Object> objArr = usersData.get( key );
            int cellnum = 0;
            for ( Object obj : objArr ) {
                Cell cell = row.createCell( cellnum++ );
                if ( obj instanceof String )
                    cell.setCellValue( (String) obj );
                if ( obj instanceof Long )
                    cell.setCellValue( String.valueOf( (Long) obj ) );
            }
        }
        // Iterate over data and write to sheet
        keyset = branchesData.keySet();

        rownum = 0;
        for ( Integer key : keyset ) {
            Row row = branchSheet.createRow( rownum++ );
            List<Object> objArr = branchesData.get( key );
            int cellnum = 0;
            for ( Object obj : objArr ) {
                Cell cell = row.createCell( cellnum++ );
                if ( obj instanceof String )
                    cell.setCellValue( (String) obj );
                if ( obj instanceof Long )
                    cell.setCellValue( String.valueOf( (Long) obj ) );
            }
        }
        // Iterate over data and write to sheet
        keyset = regionsData.keySet();

        rownum = 0;
        for ( Integer key : keyset ) {
            Row row = regionSheet.createRow( rownum++ );
            List<Object> objArr = regionsData.get( key );
            int cellnum = 0;
            for ( Object obj : objArr ) {
                Cell cell = row.createCell( cellnum++ );
                if ( obj instanceof String )
                    cell.setCellValue( (String) obj );
                if ( obj instanceof Long )
                    cell.setCellValue( String.valueOf( (Long) obj ) );
            }
        }
        return workbook;
    }
}
// JIRA SS-137 BY RM05:EOC