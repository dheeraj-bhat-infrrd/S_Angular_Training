package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.io.IOException;
import java.sql.Timestamp;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.AgentRankingReportComparator;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.SocialPostsComparator;
import com.realtech.socialsurvey.core.commons.SurveyResultsComparator;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.impl.MongoSocialPostDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.BranchMediaPostDetails;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.RegionMediaPostDetails;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.DashboardService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
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
    private UserProfileDao userProfileDao;

    @Transactional
    @Override
    public long getAllSurveyCount(String columnName, long columnValue, int numberOfDays) throws InvalidInputException{
    	LOG.info("Get all survey count for "+columnName+" and value "+columnValue+" with number of days: "+numberOfDays);
    	
    	if(columnName == null || columnName.isEmpty()){
    	    throw new InvalidInputException("Wrong input parameter : passed input parameter column name is null or empty");
    	}
    	if(columnValue <= 0l ){
            throw new InvalidInputException("Wrong input parameter : passed input parameter column value is invalid");
        }
    	
    	Timestamp endDate = new Timestamp(System.currentTimeMillis());
    	Calendar startTime =  Calendar.getInstance();
    	startTime.add(Calendar.DATE, -1*numberOfDays);
        // strip the time component of start time
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        startTime.set(Calendar.MILLISECOND, 0);
        Timestamp startDate = new Timestamp(startTime.getTimeInMillis());
    	long completedSurveyCount = getCompletedSurveyCount(columnName, columnValue, startDate, endDate, true);
    	// TODO: remove hard coding
        long companyId = -1;
        long agentId = -1;
        Set<Long> agentIds = null;
        if(columnName.equals("companyId")){
        	// agent list will be null
        	companyId = columnValue;
        }else if(columnName.equals("agentId")){
        	// agent list will have one element, the agent id
        	agentId = columnValue;
        }else if(columnName.equals("regionId")){
        	agentIds = userProfileDao.findUserIdsByRegion(columnValue);
        }else if(columnName.equals("branchId")){
        	agentIds = userProfileDao.findUserIdsByBranch(columnValue);
        }
    	//long incompleteSurveyCount = surveyPreInitiationDao.getIncompleteSurveyCount(companyId, agentId, CommonConstants.STATUS_ACTIVE, startDate, endDate, agentIds);
        //JIRA SS-1350 begin
        long incompleteSurveyCount = 0;
        if ( companyId > 0l || agentId > 0l || ( agentIds != null && !agentIds.isEmpty() ) ) {
            incompleteSurveyCount = surveyPreInitiationDao.getIncompleteSurveyCount( companyId, agentId,
                CommonConstants.STATUS_ACTIVE, startDate, endDate, agentIds );
        }
        //JIRA SS-1350 end
        LOG.debug("Completed survey: "+completedSurveyCount);
    	LOG.debug("Incomplete survey: "+incompleteSurveyCount);
    	return completedSurveyCount+incompleteSurveyCount;
    }
    
    @Override
    public long getCompleteSurveyCount(String columnName, long columnValue, int numberOfDays) throws InvalidInputException{
    	LOG.info("Get completed survey count for "+columnName+" and value "+columnValue+" with number of days: "+numberOfDays);
    	
    	if(columnName == null || columnName.isEmpty()){
            throw new InvalidInputException("Wrong input parameter : passed input parameter column name is null or empty");
        }
        if(columnValue <= 0l ){
            throw new InvalidInputException("Wrong input parameter : passed input parameter column value is invalid");
        }
        
    	Timestamp endDate = new Timestamp(System.currentTimeMillis());
    	Calendar startTime =  Calendar.getInstance();
    	startTime.add(Calendar.DATE, -1*numberOfDays);
        // strip the time component of start time
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        startTime.set(Calendar.MILLISECOND, 0);
        Timestamp startDate = new Timestamp(startTime.getTimeInMillis());
    	return getCompletedSurveyCount(columnName, columnValue, startDate, endDate, true);
    }
    
    private long getCompletedSurveyCount(String columnName, long columnValue, Timestamp startDate, Timestamp endDate, boolean filterAbusive) throws InvalidInputException{
    	return surveyDetailsDao.getCompletedSurveyCount(columnName, columnValue, startDate, endDate, filterAbusive);
    }
    
    @Override
    public long getClickedSurveyCountForPastNdays( String columnName, long columnValue, int numberOfDays ) throws InvalidInputException
    {
        if(columnName == null || columnName.isEmpty()){
            throw new InvalidInputException("Wrong input parameter : passed input parameter column name is null or empty");
        }
        if(columnValue <= 0l ){
            throw new InvalidInputException("Wrong input parameter : passed input parameter column value is invalid");
        }
        
        return surveyDetailsDao.getClickedSurveyCount( columnName, columnValue, numberOfDays, true );
    }


    @Override
    public long getSocialPostsForPastNdaysWithHierarchy( String columnName, long columnValue, int numberOfDays ) throws InvalidInputException
    {
        if(columnName == null || columnName.isEmpty()){
            throw new InvalidInputException("Wrong input parameter : passed input parameter column name is null or empty");
        }
        if(columnValue <= 0l ){
            throw new InvalidInputException("Wrong input parameter : passed input parameter column value is invalid");
        }
        
        return surveyDetailsDao.getSocialPostsCountBasedOnHierarchy( numberOfDays, columnName, columnValue );
    }


    @Override
    public double getSurveyScore( String columnName, long columnValue, int numberOfDays, boolean realtechAdmin ) throws InvalidInputException
    {
        if(columnName == null || columnName.isEmpty()){
            throw new InvalidInputException("Wrong input parameter : passed input parameter column name is null or empty");
        }
        if(columnValue <= 0l ){
            throw new InvalidInputException("Wrong input parameter : passed input parameter column value is invalid");
        }
        
        return surveyDetailsDao.getRatingForPastNdays( columnName, columnValue, numberOfDays, false, realtechAdmin, false );
    }


    @Override
    public int getProfileCompletionPercentage( User user, String columnName, long columnValue,
        OrganizationUnitSettings organizationUnitSettings ) throws InvalidInputException
    {
        LOG.info( "Method to calculate profile completion percentage started." );
        
        if(columnName == null || columnName.isEmpty()){
            throw new InvalidInputException("Wrong input parameter : passed input parameter column name is null or empty");
        }
        if(columnValue <= 0l ){
            throw new InvalidInputException("Wrong input parameter : passed input parameter column value is invalid");
        }
        if(user == null ){
            throw new InvalidInputException("Wrong input parameter : passed input parameter user is null");
        }
        if(organizationUnitSettings == null ){
            throw new InvalidInputException("Wrong input parameter : passed input parameter organizationUnitSettings is null");
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
    public int getBadges( double surveyScore, int surveyCount, int socialPosts, int profileCompleteness ) throws InvalidInputException
    {
        LOG.info( "Method to calculate number of badges started." );
        if(surveyScore < 0 || surveyCount < 0 || socialPosts < 0 || profileCompleteness < 0){
            throw new InvalidInputException("Invalid input parameter : should not be less than zero");
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
    	LOG.info("Getting survey details for graph for "+columnName+" with value "+columnValue+" for number of days "+numberOfDays+". Reatech admin flag: "+realtechAdmin);
        
    	if(columnName == null || columnName.isEmpty()){
            throw new InvalidInputException("Wrong input parameter : passed input parameter column name is null or empty");
        }
        if(columnValue <= 0l ){
            throw new InvalidInputException("Wrong input parameter : passed input parameter column value is invalid");
        }
    	
    	String criteria = "";
        Calendar startTime = Calendar.getInstance();
        switch ( numberOfDays ) {
            case 30:
                criteria = CommonConstants.AGGREGATE_BY_WEEK;
                startTime.add(Calendar.DATE, -30);
                break;
            case 60:
                criteria = CommonConstants.AGGREGATE_BY_WEEK;
                startTime.add(Calendar.DATE, -60);
                break;
            case 90:
                criteria = CommonConstants.AGGREGATE_BY_WEEK;
                startTime.add(Calendar.DATE, -90);
                break;
            case 365:
                criteria = CommonConstants.AGGREGATE_BY_MONTH;
                startTime.add(Calendar.DATE, -365);
                break;
        }
        // strip the time component of start time
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        startTime.set(Calendar.MILLISECOND, 0);
        
        Timestamp startDate = new Timestamp(startTime.getTimeInMillis());
        Timestamp endDate = new Timestamp(System.currentTimeMillis());
        LOG.debug("Getting sent surveys aggregation");
        Map<Integer, Integer> completedSurveys = surveyDetailsDao.getCompletedSurveyAggregationCount(columnName, columnValue, startDate, endDate, criteria);
        // Since the values will be modified while aggregating total surveys, copying the value to another map
        Map<Integer, Integer> completedSurveyToBeProcessed = null;
        if(completedSurveys != null && completedSurveys.size() > 0){
        	completedSurveyToBeProcessed = new HashMap<>();
        	completedSurveyToBeProcessed.putAll(completedSurveys);
        }
        // TODO: remove hard coding
        long companyId = -1;
        long agentId = -1;
        Set<Long> agentIds = null;
        if(columnName.equals("companyId")){
        	// agent list will be null
        	companyId = columnValue;
        }else if(columnName.equals("agentId")){
        	// agent list will have one element, the agent id
        	agentId = columnValue;
        }else if(columnName.equals("regionId")){
        	agentIds = userProfileDao.findUserIdsByRegion(columnValue);
        }else if(columnName.equals("branchId")){
        	agentIds = userProfileDao.findUserIdsByBranch(columnValue);
        }
        Map<Integer, Integer> incompleteSurveys = surveyPreInitiationDao.getIncompletSurveyAggregationCount(companyId, agentId, CommonConstants.STATUS_ACTIVE, startDate, endDate, agentIds, criteria);
        LOG.debug("Aggregating completed and incomplete surveys");
        Map<Integer, Integer> allSurveysSent = aggregateAllSurveysSent(incompleteSurveys, completedSurveyToBeProcessed);
        
        LOG.debug("Getting clicked surveys");
        Map<Integer, Integer> clickedSurveys = surveyDetailsDao.getClickedSurveyAggregationCount(columnName, columnValue, startDate, endDate, criteria);
        
        LOG.debug("Getting social posts count.");
        Map<Integer, Integer> socialPosts = surveyDetailsDao.getSocialPostsAggregationCount(columnName, columnValue, startDate, endDate, criteria);
        
        Map<String, Map<Integer, Integer>> map = new HashMap<String, Map<Integer, Integer>>();
        map.put( "clicked", clickedSurveys );
        map.put( "sent", allSurveysSent );
        map.put( "complete", completedSurveys );
        map.put( "socialposts", socialPosts );
        return map;
    }


    Map<Integer, Integer> aggregateAllSurveysSent(Map<Integer, Integer> incompleteSurveys, Map<Integer, Integer> completedSurveys){
    	LOG.debug("Aggregating all surveys");
    	if((incompleteSurveys == null || incompleteSurveys.size() == 0) && (completedSurveys != null && completedSurveys.size() > 0)){
    		return completedSurveys;
    	}else if((completedSurveys == null || completedSurveys.size() == 0) && (incompleteSurveys != null && incompleteSurveys.size() > 0)){
    		return incompleteSurveys;
    	}else if((completedSurveys == null || completedSurveys.size() == 0) && (incompleteSurveys == null || incompleteSurveys.size() > 0)){
    		return null;
    	}else{
    		// both the maps are present
    		for(Integer incompleteSurveyKey : incompleteSurveys.keySet()){
    			if(completedSurveys.containsKey(incompleteSurveyKey)){
    				int totalValue = incompleteSurveys.get(incompleteSurveyKey) + completedSurveys.get(incompleteSurveyKey);
    				incompleteSurveys.put(incompleteSurveyKey, totalValue);
    				// remove the object from the other map
    				completedSurveys.remove(incompleteSurveyKey);
    			}
    		}
    		// there might be some records left in the completed survey which needs to be put in the other map
    		incompleteSurveys.putAll(completedSurveys);
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
        if(fileLocation == null || fileLocation.isEmpty()){
            throw new InvalidInputException("Invalid input parameter : passed input parameter fileLocation is null or empty");
        }
        if(surveyDetails == null){
            throw new InvalidInputException("Invalid input parameter : passed input parameter surveyDetails is null");
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
        if(fileName == null || fileName.isEmpty()){
            throw new InvalidInputException("Invalid input parameter : passed input parameter fileName is null or empty");
        }
        if(socialPosts == null){
            throw new InvalidInputException("Invalid input parameter : passed input parameter surveyDetails is null");
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
        if(fileLocation == null || fileLocation.isEmpty()){
            throw new InvalidInputException("Invalid input parameter : passed input parameter fileLocation is null or empty");
        }
        if(surveyDetails == null){
            throw new InvalidInputException("Invalid input parameter : passed input parameter surveyDetails is null");
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

                surveyDetailsToPopulate.add( survey.getScore() );
                for ( SurveyResponse response : survey.getSurveyResponse() ) {
                    surveyDetailsToPopulate.add( response.getAnswer() );
                }

                surveyDetailsToPopulate.add( survey.getMood() );
                surveyDetailsToPopulate.add( survey.getReview() );
                if(survey.getMood()!= null && survey.getMood().equals(CommonConstants.SURVEY_MOOD_GREAT) && survey.getAgreedToShare() != null && !survey.getAgreedToShare().isEmpty()){
                	String status = survey.getAgreedToShare();
                    if ( status.equals( "true" ) ) {
                        surveyDetailsToPopulate.add( CommonConstants.STATUS_YES );
                    } else {
                        surveyDetailsToPopulate.add( CommonConstants.STATUS_NO );
                    }
                }else {
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
                if(survey.getSocialMediaPostDetails() != null){
                	Set<String> socialMedia = new HashSet<>();
                	if(survey.getSocialMediaPostDetails().getCompanyMediaPostDetails() != null && survey.getSocialMediaPostDetails().getCompanyMediaPostDetails().getSharedOn() != null && !survey.getSocialMediaPostDetails().getCompanyMediaPostDetails().getSharedOn().isEmpty()){
                		socialMedia.addAll(survey.getSocialMediaPostDetails().getCompanyMediaPostDetails().getSharedOn());
                	}
                	if(survey.getSocialMediaPostDetails().getAgentMediaPostDetails() != null && survey.getSocialMediaPostDetails().getAgentMediaPostDetails().getSharedOn() != null && !survey.getSocialMediaPostDetails().getAgentMediaPostDetails().getSharedOn().isEmpty()){
                		socialMedia.addAll(survey.getSocialMediaPostDetails().getAgentMediaPostDetails().getSharedOn());
                	}
                	if(survey.getSocialMediaPostDetails().getRegionMediaPostDetailsList() != null && !survey.getSocialMediaPostDetails().getRegionMediaPostDetailsList().isEmpty()){
                		for(RegionMediaPostDetails regionMediaDetail : survey.getSocialMediaPostDetails().getRegionMediaPostDetailsList()){
                			if(regionMediaDetail.getSharedOn() != null && !regionMediaDetail.getSharedOn().isEmpty()){
                				socialMedia.addAll(regionMediaDetail.getSharedOn());
                			}
                		}
                	}
                	if(survey.getSocialMediaPostDetails().getBranchMediaPostDetailsList() != null && !survey.getSocialMediaPostDetails().getBranchMediaPostDetailsList().isEmpty()){
                		for(BranchMediaPostDetails branchMediaDetail : survey.getSocialMediaPostDetails().getBranchMediaPostDetailsList()){
                			if(branchMediaDetail.getSharedOn() != null && !branchMediaDetail.getSharedOn().isEmpty()){
                				socialMedia.addAll(branchMediaDetail.getSharedOn());
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
        
        if(fileLocation == null || fileLocation.isEmpty()){
            throw new InvalidInputException("Invalid input parameter : passed input parameter fileLocation is null or empty");
        }
        if(agentDetails == null){
            throw new InvalidInputException("Invalid input parameter : passed input parameter agentDetails is null");
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

}
// JIRA SS-137 BY RM05:EOC