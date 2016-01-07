package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.entities.AbuseReporterDetails;
import com.realtech.socialsurvey.core.entities.AbusiveSurveyReportWrapper;
import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.BranchMediaPostDetails;
import com.realtech.socialsurvey.core.entities.RegionMediaPostDetails;
import com.realtech.socialsurvey.core.entities.ReporterDetail;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.SurveyPreInitiationService;


/*
 * Provides list of operations to be performed on SurveyDetails collection of mongo. SurveyDetails
 * collection contains list of surveys taken by customers. It also contains answers provided by
 * customers for questions specific to an agent.
 */
@Repository
public class MongoSurveyDetailsDaoImpl implements SurveyDetailsDao
{

    private static final Logger LOG = LoggerFactory.getLogger( MongoSurveyDetailsDaoImpl.class );

    public static final String SURVEY_DETAILS_COLLECTION = "SURVEY_DETAILS";
    public static final String ABS_REPORTER_DETAILS_COLLECTION = "ABUSE_REPORTER_DETAILS";

    public static final String ZILLOW_CALL_COUNT = "ZILLOW_CALL_COUNT";

    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private SurveyPreInitiationService surveyPreInitiationService;


    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;


    /*
     * Method to fetch survey details on the basis of agentId and customer email.
     */
    @Override
    public SurveyDetails getSurveyByAgentIdAndCustomerEmail( long agentId, String customerEmail, String firstName,
        String lastName )
    {
        LOG.info( "Method getSurveyByAgentIdAndCustomerEmail() to insert details of survey started." );
        Query query = new Query( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.CUSTOMER_EMAIL_COLUMN ).is( customerEmail ) );
        if ( firstName != null && !firstName.isEmpty() ) {
            query.addCriteria( Criteria.where( "customerFirstName" ).is( firstName ) );
        }
        if ( lastName != null && !lastName.isEmpty() ) {
            query.addCriteria( Criteria.where( "customerLastName" ).is( lastName ) );
        }
        List<SurveyDetails> surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        if ( surveys == null || surveys.size() == 0 )
            return null;
        LOG.info( "Method insertSurveyDetails() to insert details of survey finished." );
        return surveys.get( CommonConstants.INITIAL_INDEX );
    }


    @Override
    public SurveyDetails getSurveyBySurveyMongoId( String surveyMongoId )
    {
        LOG.info( "Method getSurveyBySurveyMongoId() to insert details of survey started." );
        SurveyDetails survey = mongoTemplate.findById( surveyMongoId, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method getSurveyBySurveyMongoId() finished" );
        return survey;
    }


    /*
     * Method to insert survey details into the SURVEY_DETAILS collection.
     */
    @Override
    public void insertSurveyDetails( SurveyDetails surveyDetails )
    {
        LOG.info( "Method insertSurveyDetails() to insert details of survey started." );
        mongoTemplate.insert( surveyDetails, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method insertSurveyDetails() to insert details of survey finished." );
    }


    /*
     * Method to update email id by appending timestamp in documents from SurveyDetails collection
     * by agent id and customer's email-id.
     */
    @Override
    public void updateEmailForExistingFeedback( long agentId, String customerEmail )
    {
        LOG.info( "Method updateEmailForExistingFeedback() to insert details of survey started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.CUSTOMER_EMAIL_COLUMN ).is( customerEmail ) );
        Update update = new Update();
        update.set( CommonConstants.CUSTOMER_EMAIL_COLUMN, customerEmail + "#" + new Timestamp( System.currentTimeMillis() ) );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method updateEmailForExistingFeedback() to insert details of survey finished." );
    }


    /*
     * Method to update questions for survey in SURVEY_DETAILS collection.
     */
    @Override
    public void updateCustomerResponse( long agentId, String customerEmail, SurveyResponse surveyResponse, int stage )
    {
        LOG.info( "Method updateCustomerResponse() to update response provided by customer started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.CUSTOMER_EMAIL_COLUMN ).is( customerEmail ) );
        Update update = new Update();
        update.set( "stage", stage );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        update.pull( "surveyResponse", new BasicDBObject( "question", surveyResponse.getQuestion() ) );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        mongoTemplate.updateMulti( query, new Update().push( "surveyResponse", surveyResponse ), SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method updateCustomerResponse() to update response provided by customer finished." );
    }


    /*
     * Method to update answer and response for gateway question of survey in SURVEY_DETAILS
     * collection.
     */
    @Override
    public void updateGatewayAnswer( long agentId, String customerEmail, String mood, String review, boolean isAbusive,
        String agreedToShare )
    {
        LOG.info( "Method updateGatewayAnswer() to update review provided by customer started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.CUSTOMER_EMAIL_COLUMN ).is( customerEmail ) );
        Update update = new Update();
        update.set( CommonConstants.STAGE_COLUMN, CommonConstants.SURVEY_STAGE_COMPLETE );
        update.set( CommonConstants.MOOD_COLUMN, mood );
        update.set( "review", review );
        update.set( CommonConstants.IS_ABUSIVE_COLUMN, isAbusive );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        update.set( CommonConstants.EDITABLE_SURVEY_COLUMN, false );
        update.set( CommonConstants.AGREE_SHARE_COLUMN, agreedToShare );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method updateGatewayAnswer() to update review provided by customer finished." );
    }


    /*
     * Method to calculate and update final score based upon rating questions.
     */
    @Override
    public void updateFinalScore( long agentId, String customerEmail )
    {
        LOG.info( "Method to calculate and update final score based upon rating questions started." );
        Query query = new Query();
        List<String> ratingType = new ArrayList<>();
        ratingType.add( "sb-range-smiles" );
        ratingType.add( "sb-range-scale" );
        ratingType.add( "sb-range-star" );
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.CUSTOMER_EMAIL_COLUMN ).is( customerEmail ) );
        query.addCriteria( Criteria.where( "surveyResponse.questionType" ).in( ratingType ) );
        List<SurveyResponse> surveyResponse = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION )
            .get( CommonConstants.INITIAL_INDEX ).getSurveyResponse();
        double noOfResponse = 0;
        double answer = 0;
        for ( SurveyResponse response : surveyResponse ) {
            if ( response.getQuestionType().equals( ratingType.get( CommonConstants.INITIAL_INDEX ) )
                || response.getQuestionType().equals( ratingType.get( 1 ) )
                || response.getQuestionType().equals( ratingType.get( 2 ) ) ) {
                if ( response.getAnswer() != null && !response.getAnswer().isEmpty() ) {
                    answer += Integer.parseInt( response.getAnswer() );
                    noOfResponse++;
                }
            }
        }
        Update update = new Update();
        update.set( CommonConstants.SCORE_COLUMN, Math.round( answer / noOfResponse * 1000.0 ) / 1000.0 );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method to calculate and update final score based upon rating questions finished." );
    }


    @Override
    public void updateSurveyAsAbusive( String surveyMongoId, String reporterEmail, String reporterName )
    {
        LOG.info( "Method updateSurveyAsAbusive() to mark survey as abusive started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyMongoId ) );
        Update update = new Update();
        update.set( CommonConstants.IS_ABUSIVE_COLUMN, true );
        update.set( CommonConstants.CREATED_ON, new Date() );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        update.set( CommonConstants.IS_ABUSIVE_REPORTED_BY_USER_COLUMN, true );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );

        query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.SURVEY_ID_COLUMN ).is( surveyMongoId ) );
        update = new Update();
        update.set( CommonConstants.SURVEY_ID_COLUMN, surveyMongoId );
        update.push( CommonConstants.ABUSE_REPORTERS_COLUMN, new ReporterDetail( reporterName, reporterEmail ) );
        mongoTemplate.upsert( query, update, ABS_REPORTER_DETAILS_COLLECTION );
        LOG.info( "Method updateSurveyAsAbusive() to mark survey as abusive finished." );
    }


    @Override
    public void updateSurveyAsUnAbusive( String surveyMongoId )
    {
        LOG.info( "Method updateSurveyAsUnAbusive() to mark survey as unAbusive started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyMongoId ) );
        Update update = new Update();
        update.set( CommonConstants.IS_ABUSIVE_COLUMN, false );
        update.set( CommonConstants.IS_UNMARKED_ABUSIVE_COLUMN, true );
        update.set( CommonConstants.CREATED_ON, new Date() );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );

        query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.SURVEY_ID_COLUMN ).is( surveyMongoId ) );
        mongoTemplate.remove( query, ABS_REPORTER_DETAILS_COLLECTION );

        LOG.info( "Method updateSurveyAsUnAbusive() to mark survey as unAbusive finished." );
    }


    @Override
    public void updateSurveyAsClicked( long agentId, String customerEmail )
    {
        LOG.info( "Method updateSurveyAsClicked() to mark survey as clicked started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.CUSTOMER_EMAIL_COLUMN ).is( customerEmail ) );
        Update update = new Update();
        update.set( CommonConstants.SURVEY_CLICKED_COLUMN, true );
        update.set( CommonConstants.CREATED_ON, new Date() );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method updateSurveyAsClicked() to mark survey as clicked finished." );
    }


    // This method returns all the surveys that have been clicked by customers so far.
    // If columnName field is passed null value it returns count of all the survey.
    // "columnName" field can contain either of "agentId/branchId/regionId/companyId".
    // "columnValue" field can contain respective values for the columnName.

    @Override
    public long getClickedSurveyCount( String columnName, long columnValue, int noOfDays, boolean filterAbusive )
    {
        LOG.info( "Method to get count of total number of surveys clicked so far, getClickedSurveyCount() started." );
        Date endDate = Calendar.getInstance().getTime();
        Date startDate = getNdaysBackDate( noOfDays );
        Query query = new Query( Criteria.where( CommonConstants.SURVEY_CLICKED_COLUMN ).is( true ) );
        /*query.addCriteria(Criteria.where("surveyResponse").size(0));*/
        if ( columnName != null ) {
            query.addCriteria( Criteria.where( columnName ).is( columnValue ) );
        }
        if ( filterAbusive ) {
            query.addCriteria( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( false ) );
        }
        query.addCriteria( Criteria.where( CommonConstants.CREATED_ON ).gte( startDate ).lte( endDate ) );
        LOG.info( "Method to get count of total number of surveys clicked so far, getClickedSurveyCount() finished." );
        return mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
    }


    // This method returns all the surveys that have been completed by customers so far.
    // If columnName field is passed null value it returns count of all the survey.
    // "columnName" field can contain either of "agentId/branchId/regionId/companyId".
    // "columnValue" field can contain respective values for the columnName.

    @Override
    public long getCompletedSurveyCount( String columnName, long columnValue, int noOfDays )
    {
        LOG.info( "Method to get count of total number of surveys completed so far, getCompletedSurveyCount() started." );
        Date endDate = Calendar.getInstance().getTime();
        Date startDate = getNdaysBackDate( noOfDays );
        Query query = new Query( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) );
        if ( columnName != null ) {
            query.addCriteria( Criteria.where( columnName ).is( columnValue ) );
        }
        query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ).lte( endDate ) );
        LOG.info( "Method to get count of total number of surveys completed so far, getCompletedSurveyCount() finished." );
        return mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
    }


    // This method returns all the surveys that are not yet completed by customers.
    // If columnName field is passed null value it returns count of all the survey.
    // "columnName" field can contain either of "agentId/branchId/regionId/companyId".
    // "columnValue" field can contain respective values for the columnName.

    @Override
    public long getIncompleteSurveyCount( String columnName, long columnValue, int noOfDays )
    {
        LOG.info( "Method to get count of surveys which are not yet completed, getIncompleteSurveyCount() started." );
        Date endDate = Calendar.getInstance().getTime();
        Date startDate = getNdaysBackDate( noOfDays );
        Query query = new Query( Criteria.where( CommonConstants.STAGE_COLUMN ).ne( CommonConstants.SURVEY_STAGE_COMPLETE ) );
        if ( columnName != null ) {
            query.addCriteria( Criteria.where( columnName ).is( columnValue ) );
        }
        query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ).lte( endDate ) );
        LOG.info( "Method to get count of surveys which are not yet completed, getIncompleteSurveyCount() finished." );
        return mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
    }


    // This method returns a map of customers count based upon their mood.
    // Map contains Mood --> Customers count mapping for each mood for a given
    // agent/branch/region/company.

    @Override
    public Map<String, Long> getCountOfCustomersByMood( String columnName, long columnValue )
    {
        LOG.info( "Method to get customers according to their mood, getCountOfCustomersByMood() started." );
        TypedAggregation<SurveyDetails> aggregation;
        if ( columnName == null ) {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class, Aggregation
                .group( CommonConstants.MOOD_COLUMN ).count().as( "count" ) );
        } else {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class, Aggregation.match( Criteria.where(
                columnName ).is( columnValue ) ), Aggregation.group( CommonConstants.MOOD_COLUMN ).count().as( "count" ) );
        }

        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );
        Map<String, Long> moodSplit = new HashMap<>();
        if ( result != null ) {
            @SuppressWarnings ( "unchecked") List<BasicDBObject> moodCount = (List<BasicDBObject>) result.getRawResults().get(
                "result" );
            for ( BasicDBObject o : moodCount ) {
                moodSplit.put( o.get( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).toString(),
                    Long.parseLong( o.get( "count" ).toString() ) );
            }
        }
        LOG.info( "Method to get customers according to their mood, getCountOfCustomersByMood() finished." );
        return moodSplit;
    }


    // This method returns the customers' count based upon the number of reminder mails sent to
    // them.

    @Override
    public Map<String, Long> getCountOfCustomersByReminderMails( String columnName, long columnValue )
    {
        LOG.info( "Method to get customers according to the number of reminder emails sent, getCountOfCustomersByReminderMails() started." );
        TypedAggregation<SurveyDetails> aggregation;
        if ( columnName == null ) {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class, Aggregation
                .group( CommonConstants.REMINDER_COUNT_COLUMN ).count().as( "count" ) );
        } else {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class, Aggregation.match( Criteria.where(
                columnName ).is( columnValue ) ), Aggregation.group( CommonConstants.REMINDER_COUNT_COLUMN ).count()
                .as( "count" ) );
        }

        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );
        Map<String, Long> reminderCountSplit = new HashMap<>();
        if ( result != null ) {
            @SuppressWarnings ( "unchecked") List<BasicDBObject> reminderCount = (List<BasicDBObject>) result.getRawResults()
                .get( "result" );
            for ( BasicDBObject reminder : reminderCount ) {
                reminderCountSplit.put( reminder.get( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).toString(),
                    Long.parseLong( reminder.get( "count" ).toString() ) );
            }
        }
        LOG.info( "Method to get customers according to the number of reminder emails sent, getCountOfCustomersByReminderMails() finished." );
        return reminderCountSplit;
    }


    // This method returns the customers' count based upon their current stage i.e. number of
    // questions they have answered so far.

    @Override
    public Map<String, Long> getCountOfCustomersByStage( String columnName, long columnValue )
    {
        LOG.info( "Method to get customers according to stage of survey, getCountOfCustomersByStage() started." );
        TypedAggregation<SurveyDetails> aggregation;
        if ( columnName == null ) {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class, Aggregation
                .group( CommonConstants.STAGE_COLUMN ).count().as( "count" ) );
        } else {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class, Aggregation.match( Criteria.where(
                columnName ).is( columnValue ) ), Aggregation.group( CommonConstants.STAGE_COLUMN ).count().as( "count" ) );
        }
        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );
        Map<String, Long> stageCountSplit = new HashMap<>();
        if ( result != null ) {
            @SuppressWarnings ( "unchecked") List<BasicDBObject> stageCount = (List<BasicDBObject>) result.getRawResults().get(
                "result" );
            for ( BasicDBObject stage : stageCount ) {
                stageCountSplit.put( stage.get( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).toString(),
                    Long.parseLong( stage.get( "count" ).toString() ) );
            }
        }
        LOG.info( "Method to get customers according to stage of survey, getCountOfCustomersByStage() finished." );
        return stageCountSplit;
    }


    // Method to return number of surveys taken in a given month of a particular year.

    @Override
    public long getTotalSurveyCountByMonth( int year, int month )
    {
        LOG.info( "Method to get count of total number of surveys taken in a given month and year, getTotalSurveyCountByMonth() started." );
        Calendar calendar = Calendar.getInstance();
        calendar.set( year, month, 1 );
        Date startDate = calendar.getTime();
        // Returns max value for date in the month set in Calendar instance.
        calendar.set( year, month, calendar.getActualMaximum( 5 ) );
        Date endDate = calendar.getTime();
        Query query = new Query( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) );
        query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) );
        LOG.info( "Method to get count of total number of surveys taken in a given month and year, getTotalSurveyCountByMonth() finished." );
        return mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
    }


    @Override
    @SuppressWarnings ( "unchecked")
    public double getRatingForPastNdays( String columnName, long columnValue, int noOfDays, boolean aggregateAbusive,
        boolean realtechAdmin, boolean includeZillow, long zillowReviewCount, long zillowTotalReviewScore )
    {
        LOG.info( "Method getRatingOfAgentForPastNdays(), to calculate rating of agent started for columnName: " + columnName
            + " columnValue:" + columnValue + " noOfDays:" + noOfDays + " aggregateAbusive:" + aggregateAbusive );
        Date startDate = null;
        /**
         * if days is not set, take the start date as 1 jan 1970
         */
        if ( noOfDays == -1 ) {
            startDate = new Date( 0l );
        } else {
            startDate = getNdaysBackDate( noOfDays );
        }

        Date endDate = Calendar.getInstance().getTime();

        Query query = new Query();

        /**
         * adding isabusive criteria only if fetch abusive flag is false, i.e only non abusive posts
         * are to be fetched else fetch all the records
         */
        if ( !aggregateAbusive ) {
            query.addCriteria( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( aggregateAbusive ) );
        }

        if ( !realtechAdmin ) {
            query.addCriteria( Criteria.where( columnName ).is( columnValue ) );
        }

        query.addCriteria( Criteria
            .where( CommonConstants.MODIFIED_ON_COLUMN )
            .lte( endDate )
            .andOperator( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ),
                Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ) );

        TypedAggregation<SurveyDetails> aggregation = null;
        if ( !aggregateAbusive && !realtechAdmin ) {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class, Aggregation.match( Criteria.where(
                CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ), Aggregation.match( Criteria.where(
                CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ), Aggregation.match( Criteria.where( columnName ).is(
                columnValue ) ), Aggregation.match( Criteria.where( CommonConstants.STAGE_COLUMN ).is(
                CommonConstants.SURVEY_STAGE_COMPLETE ) ), Aggregation.match( Criteria
                .where( CommonConstants.IS_ABUSIVE_COLUMN ).is( aggregateAbusive ) ), Aggregation.group( columnName )
                .sum( CommonConstants.SCORE_COLUMN ).as( "total_score" ) );
        } else if ( aggregateAbusive && !realtechAdmin ) {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class, Aggregation.match( Criteria.where(
                CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ), Aggregation.match( Criteria.where(
                CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ), Aggregation.match( Criteria.where( columnName ).is(
                columnValue ) ), Aggregation.match( Criteria.where( CommonConstants.STAGE_COLUMN ).is(
                CommonConstants.SURVEY_STAGE_COMPLETE ) ), Aggregation.group( columnName ).sum( CommonConstants.SCORE_COLUMN )
                .as( "total_score" ) );
        } else {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class, Aggregation.match( Criteria.where(
                CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ), Aggregation.match( Criteria.where(
                CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ), Aggregation.match( Criteria.where(
                CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ), Aggregation.group( columnName )
                .sum( CommonConstants.SCORE_COLUMN ).as( "total_score" ) );
        }

        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );
        long reviewsCount = mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Count of aggregated results :" + reviewsCount );
        double rating = 0;
        if ( result != null && reviewsCount > 0 ) {
            List<DBObject> basicDBObject = (List<DBObject>) result.getRawResults().get( "result" );
            if ( includeZillow ) {
                double totalRating = reviewsCount > 0 ? (double) basicDBObject.get( 0 ).get( "total_score" ) : 0;
                if ( zillowReviewCount > 0 ) {
                    reviewsCount += zillowReviewCount;
                    totalRating += zillowTotalReviewScore;
                }
                if ( zillowReviewCount > 0 || reviewsCount > 0 )
                    rating = totalRating / reviewsCount;
            } else
                rating = (double) basicDBObject.get( 0 ).get( "total_score" ) / reviewsCount;
        } else if ( includeZillow ) {
            if ( zillowReviewCount > 0 )
                rating = zillowTotalReviewScore / zillowReviewCount;
        }
        LOG.info( "Method getRatingOfAgentForPastNdays(), to calculate rating of agent finished." );
        return rating;
    }


    // January is denoted with 0.
    public double getRatingByMonth( String columnName, long columnValue, int year, int month )
    {
        LOG.info( "Method getRatingOfAgentByMonth(), to calculate rating of agent started." );
        Calendar calendar = Calendar.getInstance();
        calendar.set( year, month, 1 );
        Date startDate = calendar.getTime();
        // Returns max value for date in the month set in Calendar instance.
        calendar.set( year, month, calendar.getActualMaximum( 5 ) );
        Date endDate = calendar.getTime();
        Query query = new Query( Criteria
            .where( columnName )
            .is( columnValue )
            .andOperator( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ),
                Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ) );
        long count = mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
        if ( count < 3 ) {
            LOG.info( columnName + " " + columnValue + " does not qualify for calculation of rating. Returning..." );
            return -1;
        }
        TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
            Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ),
            Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ),
            Aggregation.match( Criteria.where( columnName ).is( columnValue ) ), Aggregation.group( columnName )
                .sum( CommonConstants.SCORE_COLUMN ).as( "total_score" ) );

        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );
        double rating = 0;
        if ( result != null ) {
            rating = ( (long) result.getRawResults().get( "total_score" ) ) / count;
        }
        LOG.info( "Method getRatingOfAgentByMonth(), to calculate rating of agent finished." );
        return rating;
    }


    // Method to get count of posts shared by customers on various social networking sites for
    // "agent/branch/region/company/all".
    // Returns posts count on that site.

    @Override
    public long getSocialPostsCount( String columnName, long columnValue, int numberOfDays )
    {
        LOG.info( "Method to count number of social posts by customers, getSocialPostsCount() started." );
        Date endDate = Calendar.getInstance().getTime();
        Date startDate = getNdaysBackDate( numberOfDays );
        TypedAggregation<SurveyDetails> aggregation;
        if ( columnName == null ) {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class, Aggregation.match( Criteria
                .where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ).lte( endDate ) ),
                Aggregation.unwind( "sharedOn" ), Aggregation.group( "sharedOn" ).count().as( "count" ) );
        } else {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class, Aggregation.match( Criteria.where(
                columnName ).is( columnValue ) ), Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN )
                .gte( startDate ).lte( endDate ) ), Aggregation.unwind( "sharedOn" ), Aggregation.group().count().as( "count" ) );
        }
        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );

        @SuppressWarnings ( "unchecked") List<BasicDBObject> shares = (List<BasicDBObject>) result.getRawResults().get(
            "result" );
        long socialPostCount = 0;
        if ( shares != null && shares.size() != 0 ) {
            socialPostCount = (int) shares.get( CommonConstants.INITIAL_INDEX ).get( "count" );
        }
        return socialPostCount;
    }


    @Override
    public long getSocialPostsCountBasedOnHierarchy( int numberOfDays, String columnName, long columnValue )
    {
        LOG.info( "Method to count number of social posts by customers, getSocialPostsCount() started." );
        long socialPostCount = 0;

        Date endDate = null;
        Date startDate = null;
        if ( numberOfDays >= 0 ) {
            endDate = Calendar.getInstance().getTime();
            startDate = getNdaysBackDate( numberOfDays );
        }

        Query query = new Query();
        if ( columnName == null ) {
        } else {
            query.addCriteria( Criteria.where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN ).ne( null ) );
            if ( columnName.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) ) {
                query.addCriteria( Criteria.where(
                    CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.COMPANY_MEDIA_POST_DETAILS_COLUMN )
                    .ne( null ) );
                query.addCriteria( Criteria.where(
                    CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.COMPANY_MEDIA_POST_DETAILS_COLUMN
                        + "." + CommonConstants.COMPANY_ID_COLUMN ).is( columnValue ) );
            } else if ( columnName.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN ) ) {
                query.addCriteria( Criteria.where(
                    CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN )
                    .ne( null ) );
                query.addCriteria( Criteria.where(
                    CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN
                        + "." + CommonConstants.REGION_ID_COLUMN ).is( columnValue ) );

            } else if ( columnName.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN ) ) {
                query.addCriteria( Criteria.where(
                    CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.BRANCH_MEDIA_POST_DETAILS_COLUMN )
                    .ne( null ) );
                query.addCriteria( Criteria.where(
                    CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.BRANCH_MEDIA_POST_DETAILS_COLUMN
                        + "." + CommonConstants.BRANCH_ID_COLUMN ).is( columnValue ) );
            } else if ( columnName.equalsIgnoreCase( CommonConstants.AGENT_ID_COLUMN ) ) {

                query.addCriteria( Criteria.where(
                    CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.AGENT_MEDIA_POST_DETAILS_COLUMN )
                    .ne( null ) );
                query.addCriteria( Criteria.where(
                    CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.AGENT_MEDIA_POST_DETAILS_COLUMN
                        + "." + CommonConstants.AGENT_ID_COLUMN ).is( columnValue ) );

            }

            if ( startDate != null && endDate == null ) {
                query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) );
            } else if ( startDate == null && endDate != null ) {
                query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) );
            } else if ( startDate != null && endDate != null ) {
                query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ).lte( endDate ) );
            }


            List<SurveyDetails> surveyDetails = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );

            if ( surveyDetails != null ) {
                for ( SurveyDetails survey : surveyDetails ) {

                    List<String> sharedOnAgent = survey.getSocialMediaPostDetails().getAgentMediaPostDetails().getSharedOn();
                    if ( sharedOnAgent != null ) {
                        socialPostCount += sharedOnAgent.size();
                    }
                    if ( survey.getSocialMediaPostDetails().getBranchMediaPostDetailsList() != null ) {
                        for ( BranchMediaPostDetails branchMediaPostDetails : survey.getSocialMediaPostDetails()
                            .getBranchMediaPostDetailsList() ) {
                            if ( branchMediaPostDetails.getSharedOn() != null ) {
                                socialPostCount += branchMediaPostDetails.getSharedOn().size();
                            }
                        }
                    }
                    if ( survey.getSocialMediaPostDetails().getRegionMediaPostDetailsList() != null ) {
                        for ( RegionMediaPostDetails regionMediaPostDetails : survey.getSocialMediaPostDetails()
                            .getRegionMediaPostDetailsList() ) {
                            if ( regionMediaPostDetails.getSharedOn() != null ) {
                                socialPostCount += regionMediaPostDetails.getSharedOn().size();
                            }
                        }
                    }
                    if ( survey.getSocialMediaPostDetails().getCompanyMediaPostDetails() != null ) {
                        List<String> sharedOnCompany = survey.getSocialMediaPostDetails().getCompanyMediaPostDetails()
                            .getSharedOn();
                        if ( sharedOnCompany != null ) {
                            socialPostCount += sharedOnCompany.size();
                        }
                    }


                    /*if ( columnName.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) ) {
                        List<String> sharedOnAgent = survey.getSocialMediaPostDetails().getAgentMediaPostDetails()
                            .getSharedOn();
                        if ( sharedOnAgent != null ) {
                            socialPostCount += sharedOnAgent.size();
                        }
                        if ( survey.getSocialMediaPostDetails().getBranchMediaPostDetailsList() != null ) {
                            for ( BranchMediaPostDetails branchMediaPostDetails : survey.getSocialMediaPostDetails()
                                .getBranchMediaPostDetailsList() ) {
                                if ( branchMediaPostDetails.getSharedOn() != null ) {
                                    socialPostCount += branchMediaPostDetails.getSharedOn().size();
                                }
                            }
                        }
                        if ( survey.getSocialMediaPostDetails().getRegionMediaPostDetailsList() != null ) {
                            for ( RegionMediaPostDetails regionMediaPostDetails : survey.getSocialMediaPostDetails()
                                .getRegionMediaPostDetailsList() ) {
                                if ( regionMediaPostDetails.getSharedOn() != null ) {
                                    socialPostCount += regionMediaPostDetails.getSharedOn().size();
                                }
                            }
                        }
                        if ( survey.getSocialMediaPostDetails().getCompanyMediaPostDetails() != null ) {
                            List<String> sharedOnCompany = survey.getSocialMediaPostDetails().getCompanyMediaPostDetails()
                                .getSharedOn();
                            if ( sharedOnCompany != null ) {
                                socialPostCount += sharedOnCompany.size();
                            }
                        }

                    } else if ( columnName.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN ) ) {
                        List<String> sharedOnAgent = survey.getSocialMediaPostDetails().getAgentMediaPostDetails()
                            .getSharedOn();
                        if ( sharedOnAgent != null ) {
                            socialPostCount += sharedOnAgent.size();
                        }
                        if ( survey.getSocialMediaPostDetails().getRegionMediaPostDetailsList() != null ) {
                            for ( RegionMediaPostDetails regionMediaPostDetails : survey.getSocialMediaPostDetails()
                                .getRegionMediaPostDetailsList() ) {
                                if ( regionMediaPostDetails.getSharedOn() != null ) {
                                    socialPostCount += regionMediaPostDetails.getSharedOn().size();
                                }
                            }
                        }
                        if ( survey.getSocialMediaPostDetails().getBranchMediaPostDetailsList() != null ) {
                            for ( BranchMediaPostDetails branchMediaPostDetails : survey.getSocialMediaPostDetails()
                                .getBranchMediaPostDetailsList() ) {
                                if ( branchMediaPostDetails.getRegionId() == columnValue ) {
                                    if ( branchMediaPostDetails.getSharedOn() != null ) {
                                        socialPostCount += branchMediaPostDetails.getSharedOn().size();
                                    }
                                }
                            }
                        }

                    } else if ( columnName.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN ) ) {
                        List<String> sharedOnAgent = survey.getSocialMediaPostDetails().getAgentMediaPostDetails()
                            .getSharedOn();
                        if ( sharedOnAgent != null ) {
                            socialPostCount += sharedOnAgent.size();
                        }
                        for ( BranchMediaPostDetails branchMediaPostDetails : survey.getSocialMediaPostDetails()
                            .getBranchMediaPostDetailsList() ) {
                            socialPostCount += branchMediaPostDetails.getSharedOn().size();
                        }

                    } else if ( columnName.equalsIgnoreCase( CommonConstants.AGENT_ID_COLUMN ) ) {
                        List<String> sharedOnAgent = survey.getSocialMediaPostDetails().getAgentMediaPostDetails()
                            .getSharedOn();
                        if ( sharedOnAgent != null ) {
                            socialPostCount += sharedOnAgent.size();
                        }
                        if ( survey.getSocialMediaPostDetails().getBranchMediaPostDetailsList() != null ) {
                            for ( BranchMediaPostDetails branchMediaPostDetails : survey.getSocialMediaPostDetails()
                                .getBranchMediaPostDetailsList() ) {
                                if ( branchMediaPostDetails.getSharedOn() != null ) {
                                    socialPostCount += branchMediaPostDetails.getSharedOn().size();
                                }
                            }
                        }
                        if ( survey.getSocialMediaPostDetails().getRegionMediaPostDetailsList() != null ) {
                            for ( RegionMediaPostDetails regionMediaPostDetails : survey.getSocialMediaPostDetails()
                                .getRegionMediaPostDetailsList() ) {
                                if ( regionMediaPostDetails.getSharedOn() != null ) {
                                    socialPostCount += regionMediaPostDetails.getSharedOn().size();
                                }
                            }
                        }
                        if ( survey.getSocialMediaPostDetails().getCompanyMediaPostDetails() != null ) {
                            List<String> sharedOnCompany = survey.getSocialMediaPostDetails().getCompanyMediaPostDetails()
                                .getSharedOn();
                            if ( sharedOnCompany != null ) {
                                socialPostCount += sharedOnCompany.size();
                            }
                        }

                    }*/
                }

            }
        }

        return socialPostCount;
    }


    // Method to get count of surveys initiated by customers and agents separately.
    // Columns can only be from : {agentId/branchId/regionId}

    @Override
    public Map<String, Long> getCountOfSurveyInitiators( String columnName, long columnValue )
    {
        LOG.info( "Method to count number of surveys initiators, getCountOfSurveyInitiators() started." );
        TypedAggregation<SurveyDetails> aggregation;
        if ( columnName == null ) {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class, Aggregation
                .group( CommonConstants.INITIATED_BY_COLUMN ).count().as( "count" ) );
        } else {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class, Aggregation.match( Criteria.where(
                columnName ).is( columnValue ) ), Aggregation.group( CommonConstants.INITIATED_BY_COLUMN ).count().as( "count" ) );
        }
        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );
        Map<String, Long> initiatorCountSplit = new HashMap<>();
        if ( result != null ) {
            @SuppressWarnings ( "unchecked") List<BasicDBObject> initiatorCount = (List<BasicDBObject>) result.getRawResults()
                .get( "result" );
            for ( BasicDBObject post : initiatorCount ) {
                initiatorCountSplit.put( post.get( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).toString(),
                    Long.parseLong( post.get( "count" ).toString() ) );
            }
        }
        LOG.info( "Method to count number of surveys initiators, getCountOfSurveyInitiators() finished." );
        return initiatorCountSplit;
    }


    /*
     * Returns a list of feedbacks provided by customers. First sorted on score then on date (both
     * descending). ColumnName can be "agentId/branchId/regionId/companyId". ColumnValue should be
     * value for respective column. limitScore is the max score under which reviews have to be shown
     */
    @Override
    public List<SurveyDetails> getFeedbacks( String columnName, long columnValue, int start, int rows, double startScore,
        double limitScore, boolean fetchAbusive, Date startDate, Date endDate, String sortCriteria )
    {
        LOG.info( "Method to fetch all the feedbacks from SURVEY_DETAILS collection, getFeedbacks() started." );

        Query query = new Query();
        if ( columnName != null ) {
            query.addCriteria( Criteria.where( columnName ).is( columnValue ) );
        }

        /**
         * fetching only completed surveys
         */
        query.addCriteria( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) );
        // query.fields().exclude( "surveyResponse" );
        if ( startDate != null && endDate != null ) {
            query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate )
                .andOperator( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ) );
        } else if ( startDate != null ) {
            query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) );
        } else if ( endDate != null ) {
            query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) );
        }

        /**
         * adding isabusive criteria only if fetch abusive flag is false, i.e only non abusive posts
         * are to be fetched else fetch all the records
         */
        if ( !fetchAbusive ) {
            query.addCriteria( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) );
        }

        if ( startScore > -1 && limitScore > -1 ) {
            query.addCriteria( new Criteria().andOperator( Criteria.where( CommonConstants.SCORE_COLUMN ).gte( startScore ),
                Criteria.where( CommonConstants.SCORE_COLUMN ).lte( limitScore ) ) );
        }

        if ( start > -1 ) {
            query.skip( start );
        }
        if ( rows > -1 ) {
            query.limit( rows );
        }
        
        query.with( new Sort( Sort.Direction.DESC, CommonConstants.MODIFIED_ON_COLUMN ) );

        /*if ( sortCriteria != null && sortCriteria.equalsIgnoreCase( CommonConstants.REVIEWS_SORT_CRITERIA_DATE ) )
            query.with( new Sort( Sort.Direction.DESC, CommonConstants.MODIFIED_ON_COLUMN ) );
        else if ( sortCriteria != null && sortCriteria.equalsIgnoreCase( CommonConstants.REVIEWS_SORT_CRITERIA_FEATURE ) ) {
            query.with( new Sort( Sort.Direction.DESC, CommonConstants.SCORE_COLUMN ) );
            query.with( new Sort( Sort.Direction.DESC, CommonConstants.MODIFIED_ON_COLUMN ) );
        } else {
            query.with( new Sort( Sort.Direction.DESC, CommonConstants.MODIFIED_ON_COLUMN ) );
            query.with( new Sort( Sort.Direction.DESC, CommonConstants.SCORE_COLUMN ) );
        }*/
        List<SurveyDetails> surveysWithReviews = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );

        LOG.info( "Method to fetch all the feedbacks from SURVEY_DETAILS collection, getFeedbacks() finished." );
        return surveysWithReviews;
    }


    @Override
    public long getFeedBacksCount( String columnName, long columnValue, double startScore, double limitScore,
        boolean fetchAbusive, boolean notRecommended, boolean includeZillow, long zillowReviewCount )
    {
        LOG.info( "Method getFeedBacksCount started for columnName:" + columnName + " columnValue:" + columnValue
            + " startScore:" + startScore + " limitScore:" + limitScore + " and fetchAbusive:" + fetchAbusive );
        Query query = new Query();
        if ( columnName != null ) {
            query.addCriteria( Criteria.where( columnName ).is( columnValue ) );
        }
        /**
         * fetching only completed surveys
         */
        query.addCriteria( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) );

        /**
         * adding isabusive criteria only if fetch abusive flag is false, i.e only non abusive posts
         * are to be fetched else fetch all the records
         */
        if ( !fetchAbusive ) {
            query.addCriteria( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) );
        }

        /**
         * adding limit for score if specified
         */
        if ( startScore > -1 && limitScore > -1 ) {
            if ( notRecommended ) {
                query.addCriteria( new Criteria().andOperator(
                    Criteria.where( CommonConstants.SCORE_COLUMN ).gte( startScore ),
                    Criteria.where( CommonConstants.SCORE_COLUMN ).lt( limitScore ) ) );
            } else {
                query.addCriteria( new Criteria().andOperator(
                    Criteria.where( CommonConstants.SCORE_COLUMN ).gte( startScore ),
                    Criteria.where( CommonConstants.SCORE_COLUMN ).lte( limitScore ) ) );
            }
        }

        long feedBackCount = mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
        if ( includeZillow && !notRecommended ) {
            // get zillow review count based on column name
            feedBackCount += zillowReviewCount;
        }
        LOG.info( "Method getFeedBacksCount executed successfully.Returning feedBackCount:" + feedBackCount );
        return feedBackCount;
    }


    /*
     * Returns a list of survey which are not yet competed by customers.Sorted on date (
     * descending). ColumnName can be "agentId/branchId/regionId/companyId". ColumnValue should be
     * value for respective column. limitScore is the max score under which reviews have to be shown
     */

    @Override
    public List<SurveyDetails> getIncompleteSurvey( String columnName, long columnValue, int start, int rows,
        double startScore, double limitScore, Date startDate, Date endDate )
    {
        LOG.info( "Method to fetch all the incomplete survey from SURVEY_DETAILS collection, getIncompleteSurvey() started." );
        Query query = new Query();
        if ( columnName != null ) {
            query.addCriteria( Criteria.where( columnName ).is( columnValue ) );
        }
        if ( startScore > 0 && limitScore > 0 ) {
            query.addCriteria( new Criteria().andOperator( Criteria.where( CommonConstants.SCORE_COLUMN ).gte( startScore ),
                Criteria.where( CommonConstants.SCORE_COLUMN ).lte( limitScore ) ) );
        }
        query.addCriteria( Criteria.where( CommonConstants.STAGE_COLUMN ).ne( CommonConstants.SURVEY_STAGE_COMPLETE ) );
        if ( start > -1 ) {
            query.skip( start );
        }
        if ( rows > -1 ) {
            query.limit( rows );
        }
        if ( startDate != null ) {
            query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) );
        }
        if ( endDate != null ) {
            query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) );
        }
        query.with( new Sort( Sort.Direction.DESC, CommonConstants.MODIFIED_ON_COLUMN ) );
        List<SurveyDetails> surveysWithReviews = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );

        LOG.info( "Method to fetch all the incoplete survey from SURVEY_DETAILS collection, getIncompleteSurvey() finished." );
        return surveysWithReviews;
    }


    /*
     * Method to increase reminder count by 1.
     */
    @Override
    public void updateReminderCount( long agentId, String customerEmail )
    {
        LOG.info( "Method to increase reminder count by 1, updateReminderCount() started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.CUSTOMER_EMAIL_COLUMN ).is( customerEmail ) );
        Update update = new Update();
        update.inc( CommonConstants.REMINDER_COUNT_COLUMN, 1 );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method to increase reminder count by 1, updateReminderCount() finished." );
    }


    @Override
    public long getCompletedSurveyCount( String organizationUnitColumn, long organizationUnitColumnValue, Timestamp startDate,
        Timestamp endDate, boolean filterAbusive ) throws InvalidInputException
    {
        LOG.info( "Getting completed survey count for " + organizationUnitColumn + " with value " + organizationUnitColumnValue );
        if ( organizationUnitColumn == null || organizationUnitColumn.isEmpty() ) {
            LOG.warn( "organizationUnitColumn is empty" );
            throw new InvalidInputException( "organizationUnitColumn is empty" );
        }
        if ( organizationUnitColumnValue <= 0l ) {
            LOG.warn( "organizationUnitColumnValue is invalid" );
            throw new InvalidInputException( "organizationUnitColumnValue is invalid" );
        }
        Query query = new Query();
        query.addCriteria( Criteria.where( organizationUnitColumn ).is( organizationUnitColumnValue ) );
        // Commented as Zillow surveys are not stored in database, SS-1276
        // query.addCriteria(Criteria.where(CommonConstants.SURVEY_SOURCE_COLUMN).ne(CommonConstants.SURVEY_SOURCE_ZILLOW));
        query.addCriteria( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) );
        if ( filterAbusive ) {
            query.addCriteria( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( false ) );
        }
        if ( startDate != null && endDate == null ) {
            query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) );
        }
        if ( endDate != null && startDate == null ) {
            query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) );
        }
        if ( startDate != null && endDate != null ) {
            query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate )
                .andOperator( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ) );
        }
        LOG.debug( "Query: " + query.toString() );
        long count = mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Found " + count + " completed surveys" );
        return count;
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public Map<Integer, Integer> getCompletedSurveyAggregationCount( String organizationUnitColumn,
        long organizationUnitColumnValue, Timestamp startDate, Timestamp endDate, String aggregateBy )
        throws InvalidInputException
    {
        Map<Integer, Integer> aggregatedResult = null;
        LOG.info( "Aggregating completed surveys. Input organizationUnitColumn: " + organizationUnitColumn
            + " \t organizationUnitColumnValue: " + organizationUnitColumnValue + " \t startDate: " + startDate
            + " \t endDate: " + endDate + " \t aggregateBy: " + aggregateBy );
        if ( aggregateBy == null || aggregateBy.isEmpty() ) {
            LOG.debug( "aggregate by field is empty" );
            throw new InvalidInputException( "aggregate by field is empty" );
        }
        LOG.debug( "Getting the result aggregated by " + aggregateBy );
        // DONT MODIFY IF YOU DONT KNOW WHAT YOU ARE DOING
        // Using BasicDBObject for aggregation
        BasicDBList pipeline = new BasicDBList();
        if ( organizationUnitColumn != null && !organizationUnitColumn.isEmpty() ) {
            // adding organization unit
            pipeline
                .add( new BasicDBObject( "$match", new BasicDBObject( organizationUnitColumn, organizationUnitColumnValue ) ) );
        }
        // match survey stage
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.STAGE_COLUMN,
            CommonConstants.SURVEY_STAGE_COMPLETE ) ) );
        // Commented as Zillow surveys are not stored in database, SS-1276
        // match non zillow survey
        // pipeline.add(new BasicDBObject("$match", new BasicDBObject(CommonConstants.SURVEY_SOURCE_COLUMN, new BasicDBObject("$ne",CommonConstants.SURVEY_SOURCE_ZILLOW))));
        // match non abusive survey
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.IS_ABUSIVE_COLUMN, false ) ) );
        // match start date
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.MODIFIED_ON_COLUMN, new BasicDBObject(
            "$gte", new Date( startDate.getTime() ) ) ) ) );
        // match end date
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.MODIFIED_ON_COLUMN, new BasicDBObject(
            "$lte", new Date( endDate.getTime() ) ) ) ) );
        // add projection
        BasicDBObject projectionObject = new BasicDBObject( CommonConstants.MODIFIED_ON_COLUMN, 1 );
        BasicDBList modifiedOnList = new BasicDBList();
        modifiedOnList.add( "$modifiedOn" );
        BasicDBObject yearDBObject = new BasicDBObject( "$year", modifiedOnList );
        BasicDBList multiplyDBList = new BasicDBList();
        multiplyDBList.add( yearDBObject );
        multiplyDBList.add( 100 );
        BasicDBObject multiplyDBObject = new BasicDBObject( "$multiply", multiplyDBList );
        BasicDBList addDBList = new BasicDBList();
        addDBList.add( multiplyDBObject );
        BasicDBObject groupColObjectValue = null;
        if ( aggregateBy.equals( CommonConstants.AGGREGATE_BY_WEEK ) ) {
            BasicDBObject weekDBObject = new BasicDBObject( "$week", modifiedOnList );
            addDBList.add( weekDBObject );
        } else if ( aggregateBy.equals( CommonConstants.AGGREGATE_BY_MONTH ) ) {
            BasicDBObject monthDBObject = new BasicDBObject( "$month", modifiedOnList );
            addDBList.add( monthDBObject );
        }
        groupColObjectValue = new BasicDBObject( "$add", addDBList );
        projectionObject.append( "groupCol", groupColObjectValue );
        pipeline.add( new BasicDBObject( "$project", projectionObject ) );
        // grouping
        pipeline.add( new BasicDBObject( "$group", new BasicDBObject( "_id", "$groupCol" ).append( "count", new BasicDBObject(
            "$sum", 1 ) ) ) );
        BasicDBObject aggregationObject = new BasicDBObject( "aggregate", SURVEY_DETAILS_COLLECTION ).append( "pipeline",
            pipeline );

        CommandResult aggregateResult = mongoTemplate.executeCommand( aggregationObject );

        List<BasicDBObject> aggregatedData = null;
        if ( aggregateResult.containsField( "result" ) ) {
            aggregatedData = (List<BasicDBObject>) aggregateResult.get( "result" );
            if ( aggregatedData.size() > 0 ) {
                aggregatedResult = new HashMap<>();
                for ( BasicDBObject data : aggregatedData ) {
                    aggregatedResult.put( Integer.parseInt( data.get( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).toString() ),
                        Integer.parseInt( data.get( "count" ).toString() ) );
                }
            }
        }
        LOG.info( "Returning aggregating completed survey results" );
        return aggregatedResult;
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public Map<Integer, Integer> getClickedSurveyAggregationCount( String organizationUnitColumn,
        long organizationUnitColumnValue, Timestamp startDate, Timestamp endDate, String aggregateBy )
        throws InvalidInputException
    {
        Map<Integer, Integer> aggregatedResult = null;
        LOG.info( "Aggregating clicked surveys. Input organizationUnitColumn: " + organizationUnitColumn
            + " \t organizationUnitColumnValue: " + organizationUnitColumnValue + " \t startDate: " + startDate
            + " \t endDate: " + endDate + " \t aggregateBy: " + aggregateBy );
        if ( aggregateBy == null || aggregateBy.isEmpty() ) {
            LOG.debug( "aggregate by field is empty" );
            throw new InvalidInputException( "aggregate by field is empty" );
        }
        LOG.debug( "Getting the result aggregated by " + aggregateBy );
        // DONT MODIFY IF YOU DONT KNOW WHAT YOU ARE DOING
        // Using BasicDBObject for aggregation
        BasicDBList pipeline = new BasicDBList();
        if ( organizationUnitColumn != null && !organizationUnitColumn.isEmpty() ) {
            // adding organization unit
            pipeline
                .add( new BasicDBObject( "$match", new BasicDBObject( organizationUnitColumn, organizationUnitColumnValue ) ) );
        }
        // Commented as Zillow surveys are not stored in database, SS-1276
        // match non zillow survey
        // pipeline.add(new BasicDBObject("$match", new BasicDBObject(CommonConstants.SURVEY_SOURCE_COLUMN, new BasicDBObject("$ne",CommonConstants.SURVEY_SOURCE_ZILLOW))));
        // match start date
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.CREATED_ON, new BasicDBObject( "$gte",
            new Date( startDate.getTime() ) ) ) ) );
        // match end date
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.CREATED_ON, new BasicDBObject( "$lte",
            new Date( endDate.getTime() ) ) ) ) );
        // add projection
        BasicDBObject projectionObject = new BasicDBObject( CommonConstants.CREATED_ON, 1 );
        BasicDBList createdOnList = new BasicDBList();
        createdOnList.add( "$createdOn" );
        BasicDBObject yearDBObject = new BasicDBObject( "$year", createdOnList );
        BasicDBList multiplyDBList = new BasicDBList();
        multiplyDBList.add( yearDBObject );
        multiplyDBList.add( 100 );
        BasicDBObject multiplyDBObject = new BasicDBObject( "$multiply", multiplyDBList );
        BasicDBList addDBList = new BasicDBList();
        addDBList.add( multiplyDBObject );
        BasicDBObject groupColObjectValue = null;
        if ( aggregateBy.equals( CommonConstants.AGGREGATE_BY_WEEK ) ) {
            BasicDBObject weekDBObject = new BasicDBObject( "$week", createdOnList );
            addDBList.add( weekDBObject );
        } else if ( aggregateBy.equals( CommonConstants.AGGREGATE_BY_MONTH ) ) {
            BasicDBObject monthDBObject = new BasicDBObject( "$month", createdOnList );
            addDBList.add( monthDBObject );
        }
        groupColObjectValue = new BasicDBObject( "$add", addDBList );
        projectionObject.append( "groupCol", groupColObjectValue );
        pipeline.add( new BasicDBObject( "$project", projectionObject ) );
        // grouping
        pipeline.add( new BasicDBObject( "$group", new BasicDBObject( "_id", "$groupCol" ).append( "count", new BasicDBObject(
            "$sum", 1 ) ) ) );
        BasicDBObject aggregationObject = new BasicDBObject( "aggregate", SURVEY_DETAILS_COLLECTION ).append( "pipeline",
            pipeline );

        CommandResult aggregateResult = mongoTemplate.executeCommand( aggregationObject );

        List<BasicDBObject> aggregatedData = null;
        if ( aggregateResult.containsField( "result" ) ) {
            aggregatedData = (List<BasicDBObject>) aggregateResult.get( "result" );
            if ( aggregatedData.size() > 0 ) {
                aggregatedResult = new HashMap<>();
                for ( BasicDBObject data : aggregatedData ) {
                    aggregatedResult.put( Integer.parseInt( data.get( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).toString() ),
                        Integer.parseInt( data.get( "count" ).toString() ) );
                }
            }
        }
        LOG.info( "Returning aggregating completed survey results" );
        return aggregatedResult;
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public Map<Integer, Integer> getSocialPostsAggregationCount( String organizationUnitColumn,
        long organizationUnitColumnValue, Timestamp startDate, Timestamp endDate, String aggregateBy )
        throws InvalidInputException
    {
        Map<Integer, Integer> aggregatedResult = null;
        LOG.info( "Aggregating completed surveys. Input organizationUnitColumn: " + organizationUnitColumn
            + " \t organizationUnitColumnValue: " + organizationUnitColumnValue + " \t startDate: " + startDate
            + " \t endDate: " + endDate + " \t aggregateBy: " + aggregateBy );
        if ( aggregateBy == null || aggregateBy.isEmpty() ) {
            LOG.debug( "aggregate by field is empty" );
            throw new InvalidInputException( "aggregate by field is empty" );
        }
        LOG.debug( "Getting the result aggregated by " + aggregateBy );
        // DONT MODIFY IF YOU DONT KNOW WHAT YOU ARE DOING
        // Using BasicDBObject for aggregation
        BasicDBList pipeline = new BasicDBList();
        if ( organizationUnitColumn != null && !organizationUnitColumn.isEmpty() ) {
            // adding organization unit
            pipeline
                .add( new BasicDBObject( "$match", new BasicDBObject( organizationUnitColumn, organizationUnitColumnValue ) ) );
        }
        // match mood as great
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.MOOD_COLUMN, "Great" ) ) );
        // match agreed to share
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.AGREE_SHARE_COLUMN, "true" ) ) );
        // match if social media post details exists
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN,
            new BasicDBObject( "$exists", true ) ) ) );
        // match start date
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.MODIFIED_ON_COLUMN, new BasicDBObject(
            "$gte", new Date( startDate.getTime() ) ) ) ) );
        // match end date
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.MODIFIED_ON_COLUMN, new BasicDBObject(
            "$lte", new Date( endDate.getTime() ) ) ) ) );
        // add projection level 1
        BasicDBObject firstProjectionObject = new BasicDBObject( CommonConstants.MODIFIED_ON_COLUMN, 1 );
        // add agent post detail column
        firstProjectionObject.append( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
            + CommonConstants.AGENT_MEDIA_POST_DETAILS_COLUMN, 1 );
        // add company post detail column
        firstProjectionObject.append( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
            + CommonConstants.COMPANY_MEDIA_POST_DETAILS_COLUMN, 1 );
        // add region post detail column
        firstProjectionObject.append( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
            + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN, 1 );
        // add branch post detail column
        firstProjectionObject.append( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
            + CommonConstants.BRANCH_MEDIA_POST_DETAILS_COLUMN, 1 );
        // add to pieline
        pipeline.add( new BasicDBObject( "$project", firstProjectionObject ) );
        // unwind region media post column as it is an array
        pipeline.add( new BasicDBObject( "$unwind", "$" + CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
            + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN ) );
        // unwind branch media post column as it is an array
        pipeline.add( new BasicDBObject( "$unwind", "$" + CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
            + CommonConstants.BRANCH_MEDIA_POST_DETAILS_COLUMN ) );

        // add projection level 2 to get count of each level
        BasicDBObject secondProjectionObject = new BasicDBObject( CommonConstants.MODIFIED_ON_COLUMN, 1 );
        BasicDBList agentPostCountDBList = new BasicDBList();
        agentPostCountDBList.add( "$socialMediaPostDetails.agentMediaPostDetails.sharedOn" );
        agentPostCountDBList.add( new BasicDBList() );
        BasicDBObject agentPostsCount = new BasicDBObject( "$size", new BasicDBObject( "$ifNull", agentPostCountDBList ) );
        secondProjectionObject.append( "agentPostsCount", agentPostsCount );

        BasicDBList regionPostCountDBList = new BasicDBList();
        regionPostCountDBList.add( "$socialMediaPostDetails.regionMediaPostDetailsList.sharedOn" );
        regionPostCountDBList.add( new BasicDBList() );
        BasicDBObject regionPostsCount = new BasicDBObject( "$size", new BasicDBObject( "$ifNull", regionPostCountDBList ) );
        secondProjectionObject.append( "regionPostsCount", regionPostsCount );

        BasicDBList branchPostCountDBList = new BasicDBList();
        branchPostCountDBList.add( "$socialMediaPostDetails.branchMediaPostDetailsList.sharedOn" );
        branchPostCountDBList.add( new BasicDBList() );
        BasicDBObject branchPostsCount = new BasicDBObject( "$size", new BasicDBObject( "$ifNull", branchPostCountDBList ) );
        secondProjectionObject.append( "branchPostsCount", branchPostsCount );

        BasicDBList companyPostCountDBList = new BasicDBList();
        companyPostCountDBList.add( "$socialMediaPostDetails.companyMediaPostDetails.sharedOn" );
        companyPostCountDBList.add( new BasicDBList() );
        BasicDBObject companyPostsCount = new BasicDBObject( "$size", new BasicDBObject( "$ifNull", companyPostCountDBList ) );
        secondProjectionObject.append( "companyPostsCount", companyPostsCount );
        pipeline.add( new BasicDBObject( "$project", secondProjectionObject ) );

        BasicDBObject thirdProjectionObject = new BasicDBObject( CommonConstants.MODIFIED_ON_COLUMN, 1 );
        BasicDBList modifiedOnList = new BasicDBList();

        BasicDBList countAdditionList = new BasicDBList();
        countAdditionList.add( "$agentPostsCount" );
        countAdditionList.add( "$regionPostsCount" );
        countAdditionList.add( "$branchPostsCount" );
        countAdditionList.add( "$companyPostsCount" );

        thirdProjectionObject.append( "postsCount", new BasicDBObject( "$add", countAdditionList ) );

        modifiedOnList.add( "$modifiedOn" );
        BasicDBObject yearDBObject = new BasicDBObject( "$year", modifiedOnList );
        BasicDBList multiplyDBList = new BasicDBList();
        multiplyDBList.add( yearDBObject );
        multiplyDBList.add( 100 );
        BasicDBObject multiplyDBObject = new BasicDBObject( "$multiply", multiplyDBList );
        BasicDBList addDBList = new BasicDBList();
        addDBList.add( multiplyDBObject );
        BasicDBObject groupColObjectValue = null;
        if ( aggregateBy.equals( CommonConstants.AGGREGATE_BY_WEEK ) ) {
            BasicDBObject weekDBObject = new BasicDBObject( "$week", modifiedOnList );
            addDBList.add( weekDBObject );
        } else if ( aggregateBy.equals( CommonConstants.AGGREGATE_BY_MONTH ) ) {
            BasicDBObject monthDBObject = new BasicDBObject( "$month", modifiedOnList );
            addDBList.add( monthDBObject );
        }
        groupColObjectValue = new BasicDBObject( "$add", addDBList );
        thirdProjectionObject.append( "groupCol", groupColObjectValue );
        pipeline.add( new BasicDBObject( "$project", thirdProjectionObject ) );
        // grouping
        pipeline.add( new BasicDBObject( "$group", new BasicDBObject( "_id", "$groupCol" ).append( "count", new BasicDBObject(
            "$sum", "$postsCount" ) ) ) );
        BasicDBObject aggregationObject = new BasicDBObject( "aggregate", SURVEY_DETAILS_COLLECTION ).append( "pipeline",
            pipeline );

        CommandResult aggregateResult = mongoTemplate.executeCommand( aggregationObject );

        List<BasicDBObject> aggregatedData = null;
        if ( aggregateResult.containsField( "result" ) ) {
            aggregatedData = (List<BasicDBObject>) aggregateResult.get( "result" );
            if ( aggregatedData.size() > 0 ) {
                aggregatedResult = new HashMap<>();
                for ( BasicDBObject data : aggregatedData ) {
                    aggregatedResult.put( Integer.parseInt( data.get( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).toString() ),
                        Integer.parseInt( data.get( "count" ).toString() ) );
                }
            }
        }
        LOG.info( "Returning aggregating completed survey results" );
        return aggregatedResult;
    }


    @Override
    public List<SurveyDetails> getIncompleteSurveyCustomers( long companyId, int surveyReminderInterval, int maxReminders )
    {
        LOG.info( "Method to get list of customers who have not yet completed their survey, getIncompleteSurveyCustomers() started." );
        Date cutOffDate = getNdaysBackDate( surveyReminderInterval );
        Query query = new Query();
        query.addCriteria( new Criteria().andOperator( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( companyId ),
            Criteria.where( CommonConstants.LAST_REMINDER_FOR_INCOMPLETE_SURVEY ).lte( cutOffDate ),
            Criteria.where( CommonConstants.REMINDER_COUNT_COLUMN ).lt( maxReminders ) ) );
        List<SurveyDetails> surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method to get list of customers who have not yet completed their survey, getIncompleteSurveyCustomers() finished." );
        return surveys;
    }


    private Date getNdaysBackDate( int noOfDays )
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, noOfDays * ( -1 ) );
        Date startDate = calendar.getTime();
        return startDate;
    }


    @Override
    public void updateReminderCount( List<Long> agents, List<String> customers )
    {
        LOG.info( "Method to increase reminder count by 1, updateReminderCount() started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).in( agents ) );
        query.addCriteria( Criteria.where( CommonConstants.CUSTOMER_EMAIL_COLUMN ).in( customers ) );
        Update update = new Update();
        update.inc( CommonConstants.REMINDER_COUNT_COLUMN, 1 );
        Date date = new Date();
        update.set( CommonConstants.MODIFIED_ON_COLUMN, date );
        update.set( CommonConstants.LAST_REMINDER_FOR_INCOMPLETE_SURVEY, date );
        update.push( CommonConstants.REMINDERS_FOR_INCOMPLETE_SURVEYS, date );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method to increase reminder count by 1, updateReminderCount() finished." );
    }


    @Override
    public void updateReminderCountForSocialPost( Long agentId, String customerEmail )
    {
        LOG.info( "Method to increase reminder count by 1, updateReminderCountForSocialPost() started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.CUSTOMER_EMAIL_COLUMN ).is( customerEmail ) );
        Update update = new Update();
        Date date = new Date();
        update.set( CommonConstants.MODIFIED_ON_COLUMN, date );
        update.set( CommonConstants.LAST_REMINDER_FOR_SOCIAL_POST, date );
        update.push( CommonConstants.REMINDERS_FOR_SOCIAL_POSTS, date );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method to increase reminder count by 1, updateReminderCountForSocialPost() finished." );
    }


    @Override
    public List<SurveyDetails> getIncompleteSocialPostCustomersEmail( long companyId, int surveyReminderInterval,
        int maxReminders, float autopostScore )
    {
        LOG.info( "Method to get list of customers who have not yet shared their survey on all the social networking sites, getIncompleteSocialPostCustomersEmail() started." );
        Date cutOffDate = getNdaysBackDate( surveyReminderInterval );
        Query query = new Query();

        query.addCriteria( new Criteria().andOperator( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( companyId ),
            new Criteria().orOperator( Criteria.where( CommonConstants.LAST_REMINDER_FOR_SOCIAL_POST ).lte( cutOffDate ),
                Criteria.where( CommonConstants.LAST_REMINDER_FOR_SOCIAL_POST ).exists( false ) ),
            Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ),
            Criteria.where( CommonConstants.SCORE_COLUMN ).gte( autopostScore ),
            Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( false ),
            Criteria.where( CommonConstants.MOOD_COLUMN ).is( CommonConstants.SURVEY_MOOD_GREAT ) ) );

        List<SurveyDetails> surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        ListIterator<SurveyDetails> surveyIterator = surveys.listIterator();
        SurveyDetails surveyDetail;
        while ( surveyIterator.hasNext() ) {
            surveyDetail = surveyIterator.next();
            if ( surveyDetail.getRemindersForSocialPosts() != null
                && surveyDetail.getRemindersForSocialPosts().size() >= maxReminders ) {
                surveyIterator.remove();
            }
        }
        LOG.info( "Method to get list of customers who have not yet completed their survey on all the social networking sites, getIncompleteSocialPostCustomersEmail() finished." );
        return surveys;
    }


    @Override
    public void updateSharedOn( String socialSite, long agentId, String customerEmail )
    {
        LOG.info( "updateSharedOn() started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.CUSTOMER_EMAIL_COLUMN ).is( customerEmail ) );
        Update update = new Update();
        update.addToSet( CommonConstants.SHARED_ON_COLUMN, socialSite );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.info( "updateSharedOn() finished." );
    }


    @Override
    public void changeStatusOfSurvey( long agentId, String customerEmail, String firstName, String lastName, boolean editable )
    {
        LOG.info( "Method to update status of survey in SurveyDetails collection, changeStatusOfSurvey() started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.CUSTOMER_EMAIL_COLUMN ).is( customerEmail ) );
        query.addCriteria( Criteria.where( "customerFirstName" ).is( firstName ) );
        query.addCriteria( Criteria.where( "customerLastName" ).is( lastName ) );
        Update update = new Update();
        update.set( CommonConstants.EDITABLE_SURVEY_COLUMN, editable );
        update.set( CommonConstants.STAGE_COLUMN, 0 );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method to update status of survey in SurveyDetails collection, changeStatusOfSurvey() finished." );
    }


    @Override
    public void getAverageScore( Date startDate, Date endDate, Map<Long, AgentRankingReport> agentReportData,
        String columnName, long columnValue, boolean fetchAbusive )
    {

        TypedAggregation<SurveyDetails> aggregation;
        if ( startDate != null && endDate != null ) {
            aggregation = new TypedAggregation<SurveyDetails>(
                SurveyDetails.class,
                Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                // Commented as Zillow surveys are not stored in database, SS-1276
                // Aggregation.match( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).ne( CommonConstants.SURVEY_SOURCE_ZILLOW ) ),
                Aggregation.match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ), Aggregation
                    .match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ), Aggregation
                    .match( Criteria.where( CommonConstants.CREATED_ON ).lte( endDate ) ), Aggregation
                    .group( CommonConstants.AGENT_ID_COLUMN ).avg( CommonConstants.SCORE_COLUMN ).as( "score" ) );
        } else if ( startDate != null && endDate == null )
            aggregation = new TypedAggregation<SurveyDetails>(
                SurveyDetails.class,
                Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                // Commented as Zillow surveys are not stored in database, SS-1276
                // Aggregation.match( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).ne( CommonConstants.SURVEY_SOURCE_ZILLOW ) ),
                Aggregation.match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ), Aggregation
                    .match( Criteria.where( CommonConstants.CREATED_ON ).gte( startDate ) ), Aggregation
                    .group( CommonConstants.AGENT_ID_COLUMN ).avg( CommonConstants.SCORE_COLUMN ).as( "score" ) );
        else if ( startDate == null && endDate != null )
            aggregation = new TypedAggregation<SurveyDetails>(
                SurveyDetails.class,
                Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                // Commented as Zillow surveys are not stored in database, SS-1276
                // Aggregation.match( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).ne( CommonConstants.SURVEY_SOURCE_ZILLOW ) ),
                Aggregation.match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ), Aggregation
                    .match( Criteria.where( CommonConstants.CREATED_ON ).lte( endDate ) ), Aggregation
                    .group( CommonConstants.AGENT_ID_COLUMN ).avg( CommonConstants.SCORE_COLUMN ).as( "score" ) );
        else {
            aggregation = new TypedAggregation<SurveyDetails>(
                SurveyDetails.class,
                Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                // Commented as Zillow surveys are not stored in database, SS-1276
                // Aggregation.match( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).ne( CommonConstants.SURVEY_SOURCE_ZILLOW ) ),
                Aggregation.match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ), Aggregation
                    .group( CommonConstants.AGENT_ID_COLUMN ).avg( CommonConstants.SCORE_COLUMN ).as( "score" ) );
        }

        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );

        //create rating format to format survey score
        DecimalFormat ratingFormat = CommonConstants.SOCIAL_RANKING_FORMAT;
        ratingFormat.setMinimumFractionDigits( 1 );
        ratingFormat.setMaximumFractionDigits( 1 );

        if ( result != null ) {
            @SuppressWarnings ( "unchecked") List<BasicDBObject> averageSCore = (List<BasicDBObject>) result.getRawResults()
                .get( "result" );
            for ( BasicDBObject o : averageSCore ) {
                AgentRankingReport agentRankingReport;
                long agentId = Long.parseLong( o.get( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).toString() );
                if ( agentReportData.containsKey( agentId ) ) {
                    agentRankingReport = agentReportData.get( agentId );
                } else {
                    agentRankingReport = new AgentRankingReport();
                }

                double score = Double.parseDouble( o.get( "score" ).toString() );
                try {
                    score = Double.parseDouble( ratingFormat.format( score ) );
                } catch ( NumberFormatException e ) {
                    LOG.error( "Error while parsing survey score " );
                }
                agentRankingReport.setAverageScore( score );

                agentReportData.put( agentId, agentRankingReport );
            }
        }
    }


    @Override
    public void getCompletedSurveysCount( Date startDate, Date endDate, Map<Long, AgentRankingReport> agentReportData,
        String columnName, long columnValue, boolean fetchAbusive )
    {
        TypedAggregation<SurveyDetails> aggregation;
        if ( startDate != null && endDate != null ) {
            aggregation = new TypedAggregation<SurveyDetails>(
                SurveyDetails.class,
                Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                // Commented as Zillow surveys are not stored in database, SS-1276
                // Aggregation.match( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).ne( CommonConstants.SURVEY_SOURCE_ZILLOW ) ),
                Aggregation.match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ), Aggregation
                    .match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ), Aggregation
                    .match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ), Aggregation
                    .group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );
        } else if ( startDate != null && endDate == null )
            aggregation = new TypedAggregation<SurveyDetails>(
                SurveyDetails.class,
                Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                // Commented as Zillow surveys are not stored in database, SS-1276
                // Aggregation.match( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).ne( CommonConstants.SURVEY_SOURCE_ZILLOW ) ),
                Aggregation.match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ), Aggregation
                    .match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ), Aggregation
                    .group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );
        else if ( startDate == null && endDate != null )
            aggregation = new TypedAggregation<SurveyDetails>(
                SurveyDetails.class,
                Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                // Commented as Zillow surveys are not stored in database, SS-1276
                // Aggregation.match( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).ne( CommonConstants.SURVEY_SOURCE_ZILLOW ) ),
                Aggregation.match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ), Aggregation
                    .match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ), Aggregation
                    .group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );
        else {
            aggregation = new TypedAggregation<SurveyDetails>(
                SurveyDetails.class,
                Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                //            		Commented as Zillow surveys are not stored in database, SS-1276
                //            		Aggregation.match( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).ne( CommonConstants.SURVEY_SOURCE_ZILLOW ) ),
                Aggregation.match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ), Aggregation
                    .group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );
        }

        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );

        if ( result != null ) {
            @SuppressWarnings ( "unchecked") List<BasicDBObject> sentSurveys = (List<BasicDBObject>) result.getRawResults()
                .get( "result" );
            for ( BasicDBObject o : sentSurveys ) {
                long agentId = Long.parseLong( o.get( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).toString() );
                AgentRankingReport agentRankingReport;
                if ( agentReportData.containsKey( agentId ) ) {
                    agentRankingReport = agentReportData.get( agentId );
                } else {
                    agentRankingReport = new AgentRankingReport();
                }

                agentRankingReport.setCompletedSurveys( ( Long.parseLong( o.get( "count" ).toString() ) ) );
                agentReportData.put( agentId, agentRankingReport );
            }
        }
    }


    // JIRA SS-137 and 158 : EOC

    @Override
    public long noOfPreInitiatedSurveys( String columnName, long columnValue, Date startDate, Date endDate )
    {
        String profileLevel = "";
        if ( columnName.equals( CommonConstants.COMPANY_ID ) ) {
            profileLevel = CommonConstants.PROFILE_LEVEL_COMPANY;
        } else if ( columnName.equals( CommonConstants.REGION_ID ) ) {
            profileLevel = CommonConstants.PROFILE_LEVEL_REGION;
        } else if ( columnName.equals( CommonConstants.BRANCH_ID ) ) {
            profileLevel = CommonConstants.PROFILE_LEVEL_BRANCH;
        } else if ( columnName.equals( CommonConstants.AGENT_ID ) ) {
            profileLevel = CommonConstants.PROFILE_LEVEL_INDIVIDUAL;
        }

        long noOfPreInitiatedSurveys = 0l;
        try {
            List<SurveyPreInitiation> preInitiations = surveyPreInitiationService.getIncompleteSurvey( columnValue, 0, 0, 0,
                -1, profileLevel, startDate, endDate, false );
            for ( SurveyPreInitiation initiation : preInitiations ) {
                if ( initiation.getStatus() == CommonConstants.SURVEY_STATUS_PRE_INITIATED ) {
                    noOfPreInitiatedSurveys++;
                }
            }
        } catch ( InvalidInputException e ) {
            LOG.error(
                "InvalidInputException caught in noOfPreInitiatedSurveys() while fetching reviews. Nested exception is ", e );
        }
        return noOfPreInitiatedSurveys;
    }


    @Override
    public SurveyDetails getSurveyBySourceSourceIdAndMongoCollection( String surveySourceId, long iden, String collectionName )
    {
        Query query = new Query( Criteria.where( CommonConstants.SURVEY_SOURCE_ID_COLUMN ).is( surveySourceId ) );

        if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
            query.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( iden ) );
        } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {
            query.addCriteria( Criteria.where( CommonConstants.REGION_ID_COLUMN ).is( iden ) );
        } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
            query.addCriteria( Criteria.where( CommonConstants.BRANCH_ID_COLUMN ).is( iden ) );
        } else if ( collectionName.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
            query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( iden ) );
        }
        List<SurveyDetails> surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        if ( surveys == null || surveys.size() == 0 )
            return null;
        LOG.info( "Method insertSurveyDetails() to insert details of survey finished." );
        return surveys.get( CommonConstants.INITIAL_INDEX );
    }


    // Commented as Zillow surveys are not stored in database, SS-1276
    //    @Override
    //    public void removeZillowSurveysByEntity( String entityType, long entityId )
    //    {
    //        LOG.info( "Method removeZillowSurveysByEntity() started" );
    //        Query query = new Query( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).is(
    //            CommonConstants.SURVEY_SOURCE_ZILLOW ) );
    //        query.addCriteria( Criteria.where( entityType ).is( entityId ) );
    //        mongoTemplate.remove( query, SURVEY_DETAILS_COLLECTION );
    //        LOG.info( "Method removeZillowSurveysByEntity() finished" );
    //    }

    //    Commented as Zillow surveys are not stored in database, SS-1276
    //    @Override
    //    public void removeExcessZillowSurveysByEntity( String entityType, long entityId )
    //    {
    //        LOG.info( "Method removeExcessZillowSurveysByEntity() started" );
    //        
    //                Query query = new Query( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).is(
    //                    CommonConstants.SURVEY_SOURCE_ZILLOW ) );
    //                query.addCriteria( Criteria.where( entityType ).is( entityId ) );
    //                query.with( new Sort( Sort.Direction.ASC, "createdOn" ) );
    //                List<DBObject> surveys = mongoTemplate.find( query, DBObject.class, SURVEY_DETAILS_COLLECTION );
    //                int count = surveys.size();
    //                if ( count > 10 ) {
    //                    int noOfSurveysToRemove = count - 10;
    //                    for ( int i = 0; i < noOfSurveysToRemove; i++ ) {
    //                        mongoTemplate.remove( surveys.get( i ), SURVEY_DETAILS_COLLECTION );
    //                    }
    //                }
    //        LOG.info( "Method removeExcessZillowSurveysByEntity() finished" );
    //    }


    @Override
    public long getSurveysReporetedAsAbusiveCount()
    {
        LOG.info( "Method getSurveysReporetedAsAbusiveCount() to get count of surveys marked as abusive started." );
        long count = mongoTemplate.getCollection( ABS_REPORTER_DETAILS_COLLECTION ).getCount();
        LOG.info( "Method getSurveysReporetedAsAbusiveCount() to get count of surveys marked as abusive finished." );
        return count;
    }


    @Override
    public List<AbusiveSurveyReportWrapper> getSurveysReporetedAsAbusive( int start, int rows )
    {
        LOG.info( "Method getSurveysReporetedAsAbusive() to retrieve surveys marked as abusive started." );
        Query query = new Query( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( true ) );
        query.with( new Sort( Sort.Direction.ASC, CommonConstants.DEFAULT_MONGO_ID_COLUMN ) );
        if ( start > -1 ) {
            query.skip( start );
        }
        if ( rows > -1 ) {
            query.limit( rows );
        }

        List<SurveyDetails> surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        if ( surveys == null || surveys.size() == 0 )
            return null;

        List<String> surveyIds = new ArrayList<String>();
        for ( SurveyDetails survey : surveys ) {
            surveyIds.add( survey.get_id() );
        }

        query = new Query( Criteria.where( CommonConstants.SURVEY_ID_COLUMN ).in( surveyIds ) );
        query.with( new Sort( Sort.Direction.ASC, CommonConstants.SURVEY_ID_COLUMN ) );

        List<AbuseReporterDetails> absReporterDetails = mongoTemplate.find( query, AbuseReporterDetails.class,
            ABS_REPORTER_DETAILS_COLLECTION );

        //create AbuseReporterDetails object for the surveys reported abusive by application
        AbuseReporterDetails absReporterDetailForApp = new AbuseReporterDetails();
        Set<ReporterDetail> abuseReportersForApp = new HashSet<ReporterDetail>();
        abuseReportersForApp.add( new ReporterDetail( CommonConstants.REPORT_ABUSE_BY_APPLICSTION_NAME,
            CommonConstants.REPORT_ABUSE_BY_APPLICSTION_EMAIL ) );
        absReporterDetailForApp.setAbuseReporters( abuseReportersForApp );

        List<AbusiveSurveyReportWrapper> abusiveSurveyReports = new ArrayList<AbusiveSurveyReportWrapper>();
        for ( SurveyDetails survey : surveys ) {
            if ( survey.isAbuseRepByUser() ) {
                if ( absReporterDetails != null && absReporterDetails.size() > 0 ) {
                    boolean reporterDetailsFound = false;
                    for ( AbuseReporterDetails absReporterDetail : absReporterDetails ) {
                        if ( absReporterDetail.getSurveyId().equals( survey.get_id() ) ) {
                            abusiveSurveyReports.add( new AbusiveSurveyReportWrapper( survey, absReporterDetail ) );
                            reporterDetailsFound = true;
                            break;
                        }
                    }
                    if ( !reporterDetailsFound )
                        // to handle existing surveys where reporter info not saved
                        abusiveSurveyReports.add( new AbusiveSurveyReportWrapper( survey, null ) );
                } else {
                    // to handle existing surveys where reporter info not saved
                    abusiveSurveyReports.add( new AbusiveSurveyReportWrapper( survey, null ) );
                }
            } else {

                abusiveSurveyReports.add( new AbusiveSurveyReportWrapper( survey, absReporterDetailForApp ) );
            }
        }

        LOG.info( "Method getSurveysReporetedAsAbusive() to retrieve surveys marked as abusive finished." );
        return abusiveSurveyReports;
    }


    @Override
    public int fetchZillowCallCount()
    {
        LOG.info( "Method fetchZillowCallCount() started" );
        Query query = new Query();
        int count = 0;
        DBObject zillowCallCount = mongoTemplate.findOne( query, DBObject.class, ZILLOW_CALL_COUNT );
        if ( zillowCallCount == null ) {
            LOG.debug( "Count not found. Setting count to 0" );
        } else {
            count = (int) zillowCallCount.get( "count" );
            LOG.debug( "Count value : " + count );
        }
        LOG.info( "Method fetchZillowCallCount() finished" );
        return count;
    }


    @Override
    public void resetZillowCallCount()
    {
        LOG.info( "Method resetZillowCallCount() started" );
        Query query = new Query();
        LOG.debug( "Resetting count value" );
        Update update = new Update().set( "count", 0 );
        mongoTemplate.upsert( query, update, ZILLOW_CALL_COUNT );
        LOG.info( "Method resetZillowCallCount() finished" );
    }


    @Override
    public void updateZillowCallCount()
    {
        LOG.info( "Method updateZillowCallCount() started" );
        int count = 0;
        LOG.debug( "Fetching the latest value of count." );
        Query query = new Query();
        count = fetchZillowCallCount() + 1;
        LOG.debug( "Updating count value" );
        Update update = new Update().set( "count", count );
        mongoTemplate.upsert( query, update, ZILLOW_CALL_COUNT );
        LOG.info( "Method updateZillowCallCount() finished" );
    }


    @Override
    public void updateSurveyDetails( SurveyDetails surveyDetails )
    {
        LOG.info( "Method insertSurveyDetails() to insert details of survey started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( surveyDetails.getAgentId() ) );
        query.addCriteria( Criteria.where( CommonConstants.CUSTOMER_EMAIL_COLUMN ).is( surveyDetails.getCustomerEmail() ) );
        Update update = new Update();
        update.set( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN, surveyDetails.getSocialMediaPostDetails() );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method insertSurveyDetails() to insert details of survey finished." );
    }


    public void updateSurveyAsUnderResolution( String surveyId )
    {
        LOG.info( "Method updateSurveyAsUnderResolution() to mark survey as under resolution started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyId ) );
        Update update = new Update();
        update.set( CommonConstants.UNDER_RESOLUTION_COLUMN, true );
        mongoTemplate.upsert( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method updateSurveyAsUnderResolution() to mark survey as under resolution finished." );
    }


    @Override
    public List<AbusiveSurveyReportWrapper> getSurveysReporetedAsAbusive( long companyId, int start, int rows )
    {
        LOG.info( "Method getSurveysReporetedAsAbusive() to retrieve surveys marked as abusive for a company started." );
        Query query = new Query( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( true ) );
        query.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( companyId ) );
        query.with( new Sort( Sort.Direction.ASC, CommonConstants.DEFAULT_MONGO_ID_COLUMN ) );
        if ( start > -1 ) {
            query.skip( start );
        }
        if ( rows > -1 ) {
            query.limit( rows );
        }

        List<SurveyDetails> surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        if ( surveys == null || surveys.size() == 0 )
            return null;

        List<String> surveyIds = new ArrayList<String>();
        for ( SurveyDetails survey : surveys ) {
            surveyIds.add( survey.get_id() );
        }

        query = new Query( Criteria.where( CommonConstants.SURVEY_ID_COLUMN ).in( surveyIds ) );
        query.with( new Sort( Sort.Direction.ASC, CommonConstants.SURVEY_ID_COLUMN ) );
        if ( start > -1 ) {
            query.skip( start );
        }
        if ( rows > -1 ) {
            query.limit( rows );
        }

        List<AbuseReporterDetails> absReporterDetails = mongoTemplate.find( query, AbuseReporterDetails.class,
            ABS_REPORTER_DETAILS_COLLECTION );

        List<AbusiveSurveyReportWrapper> abusiveSurveyReports = new ArrayList<AbusiveSurveyReportWrapper>();
        for ( SurveyDetails survey : surveys ) {
            if ( absReporterDetails != null && absReporterDetails.size() > 0 ) {
                boolean reporterDetailsFound = false;
                for ( AbuseReporterDetails absReporterDetail : absReporterDetails ) {
                    if ( absReporterDetail.getSurveyId().equals( survey.get_id() ) ) {
                        abusiveSurveyReports.add( new AbusiveSurveyReportWrapper( survey, absReporterDetail ) );
                        reporterDetailsFound = true;
                        break;
                    }
                }
                if ( !reporterDetailsFound )
                    // to handle existing surveys where reporter info not saved
                    abusiveSurveyReports.add( new AbusiveSurveyReportWrapper( survey, null ) );
            } else {
                // to handle existing surveys where reporter info not saved
                abusiveSurveyReports.add( new AbusiveSurveyReportWrapper( survey, null ) );
            }
        }

        LOG.info( "Method getSurveysReporetedAsAbusive() to retrieve surveys marked as abusive for a company finished." );
        return abusiveSurveyReports;
    }


    @Override
    public List<SurveyDetails> getSurveyDetailsByAgentAndCompany( long companyId )
    {
        LOG.info( "Method getSurveyDetailsByAgentAndCompany() to insert details of survey started." );
        List<SurveyDetails> surveys = new ArrayList<SurveyDetails>();
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( companyId ) );

        surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        return surveys;
    }


    public List<SurveyDetails> getSurveysUnderResolution( long companyId, int start, int rows )
    {
        Query query = new Query( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( companyId ) );
        query.addCriteria( Criteria.where( CommonConstants.IS_UNDER_RESOLUTION_COLUMN ).is( true ) );
        query.with( new Sort( Sort.Direction.ASC, CommonConstants.DEFAULT_MONGO_ID_COLUMN ) );
        if ( start > -1 ) {
            query.skip( start );
        }
        if ( rows > -1 ) {
            query.limit( rows );
        }

        List<SurveyDetails> surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        if ( surveys == null || surveys.size() == 0 )
            return null;
        else
            return surveys;
    }


    @Override
    public long getSurveysUnderResolutionCount( long companyId )
    {
        LOG.info( "Method getSurveysUnderResolutionCount() to get count of surveys marked as under resolution for a company started." );
        Query query = new Query( Criteria.where( CommonConstants.UNDER_RESOLUTION_COLUMN ).is( true ) );
        query.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( companyId ) );
        long count = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION ).size();
        LOG.info( "Method getSurveysUnderResolutionCount() to get count of surveys marked as under resolution for a company finished." );
        return count;

    }


    @Override
    public long getSurveysReporetedAsAbusiveCount( long companyId )
    {
        LOG.info( "Method getSurveysReporetedAsAbusiveCount() to get count of surveys marked as abusive for a company started." );
        Query query = new Query( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( true ) );
        query.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( companyId ) );
        long count = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION ).size();
        LOG.info( "Method getSurveysUnderResolutionCount() to get count of surveys marked as abusive for a company finished." );
        return count;
    }


    @Override
    public List<Long> getEntityIdListForModifiedReview( String columnName, long modifiedAfter )
    {
        LOG.debug( "method getEntityIdListForModifiedReview() started" );
        //TODO change the date to time stamp
        Date startDate = new Date( modifiedAfter );

        Query query = new Query( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) );


        query.fields().include( columnName );

        List<SurveyDetails> surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );


        List<Long> entityIdList = new ArrayList<Long>();
        long idToAdd = 0l;
        for ( SurveyDetails survey : surveys ) {
            if ( columnName.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                idToAdd = survey.getAgentId();
            } else if ( columnName.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                idToAdd = survey.getBranchId();
            } else if ( columnName.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                idToAdd = survey.getRegionId();
            } else if ( columnName.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                idToAdd = survey.getCompanyId();
            }

            if ( !entityIdList.contains( idToAdd ) && idToAdd > 0l )
                entityIdList.add( idToAdd );
        }

        LOG.debug( "method getEntityIdListForModifiedReview() ended" );
        return entityIdList;
    }


    /*
     * 
     */
    @Override
    public Map<Long, Integer> getSurveyCountForAgents( List<Long> agentIdList, boolean fetchAbusive ) throws ParseException
    {
        LOG.info( "Method to get getSurveyCountForAgents called" );
        TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
            Aggregation.match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
            Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ),
            Aggregation.match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).in( agentIdList ) ), Aggregation
                .group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );


        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );
        Map<Long, Integer> surveyCountForEntities = new HashMap<>();
        //inserting count as zero for all records. aggregation doesn't return group with zero record.
        for ( Long agentId : agentIdList ) {
            surveyCountForEntities.put( agentId, 0 );
        }

        @SuppressWarnings ( "unchecked") List<BasicDBObject> surveyCountList = (List<BasicDBObject>) result.getRawResults()
            .get( "result" );

        for ( BasicDBObject o : surveyCountList ) {
            surveyCountForEntities.put( new Double( o.get( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).toString() ).longValue(),
                Integer.parseInt( o.get( "count" ).toString() ) );
        }

        return surveyCountForEntities;
    }
}