package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
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
import com.realtech.socialsurvey.core.dao.CustomAggregationOperation;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.entities.AbuseReporterDetails;
import com.realtech.socialsurvey.core.entities.AbusiveSurveyReportWrapper;
import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.ApiRequestDetails;
import com.realtech.socialsurvey.core.entities.ReporterDetail;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


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
    public static final String API_REQUEST_DETAILS = "API_REQUEST_DETAILS";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value ( "${MAX_SOCIAL_POST_REMINDER_INTERVAL}")
    private int maxSocialPostReminderInterval;

    @Value ( "${CONSIDER_ONLY_LATEST_SURVEYS}")
    private String considerOnlyLatestSurveys;


    /*
     * Method to fetch survey details on the basis of survey preinitiated id.
     */
    @Override
    public SurveyDetails getSurveyBySurveyPreIntitiationId( long surveyPreIntitiationId )
    {
        LOG.debug( "Method getSurveyBySurveyPreIntitiationId() to insert details of survey started." );
        Query query = new Query(
            Criteria.where( CommonConstants.SURVEY_PREINITIATION_ID_COLUMN ).is( surveyPreIntitiationId ) );

        List<SurveyDetails> surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        if ( surveys == null || surveys.size() == 0 )
            return null;
        LOG.debug( "Method insertSurveyDetails() to insert details of survey finished." );
        return surveys.get( CommonConstants.INITIAL_INDEX );
    }


    /*
     * Method to fetch survey details on the basis of agentId and customer email.
     */
    @Override
	public SurveyDetails getSurveyByAgentIdAndCustomerEmail( long agentId, String customerEmail, String firstName,
        String lastName )
    {
        LOG.debug( "Method getSurveyByAgentIdAndCustomerEmail() to insert details of survey started." );
        Query query = new Query( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.CUSTOMER_EMAIL_COLUMN ).is( customerEmail ) );
        if ( firstName != null && !firstName.isEmpty() ) {
            query.addCriteria( Criteria.where( "customerFirstName" ).is( firstName ) );
        }
        if ( lastName != null && !lastName.isEmpty() ) {
            query.addCriteria( Criteria.where( "customerLastName" ).is( lastName ) );
        }
        
        //get the oldest record
        query.with( new Sort( Sort.Direction.ASC, CommonConstants.CREATED_ON ) );
        List<SurveyDetails> surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        if ( surveys == null || surveys.size() == 0 )
            return null;
        LOG.debug( "Method insertSurveyDetails() to insert details of survey finished." );
        return surveys.get( CommonConstants.INITIAL_INDEX );
    }


    @Override
    public SurveyDetails getSurveyByAgentIdAndCustomerEmailAndNoOfDays( long agentId, String customerEmail, String firstName,
        String lastName, int noOfDays )
    {
        LOG.debug( "Method getSurveyByAgentIdAndCustomerEmailAndNoOfDays()  started." );

        Date startDate = null;
        if ( noOfDays == -1 ) {
            startDate = new Date( 0l );
        } else {
            startDate = getNdaysBackDate( noOfDays );
        }


        Query query = new Query( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.CUSTOMER_EMAIL_COLUMN ).is( customerEmail ) );
        if ( firstName != null && !firstName.isEmpty() ) {
            query.addCriteria( Criteria.where( "customerFirstName" ).is( firstName ) );
        }
        if ( lastName != null && !lastName.isEmpty() ) {
            query.addCriteria( Criteria.where( "customerLastName" ).is( lastName ) );
        }

        query.addCriteria( Criteria.where( CommonConstants.CREATED_ON ).gte( startDate ) );
        
        List<SurveyDetails> surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        if ( surveys == null || surveys.size() == 0 )
            return null;
        LOG.debug( "Method insertSurveyDetails() to insert details of survey finished." );
        return surveys.get( CommonConstants.INITIAL_INDEX );
    }


    @Override
    public SurveyDetails getSurveyBySurveyMongoId( String surveyMongoId )
    {
        LOG.debug( "Method getSurveyBySurveyMongoId() to insert details of survey started." );
        SurveyDetails survey = mongoTemplate.findById( surveyMongoId, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method getSurveyBySurveyMongoId() finished" );
        return survey;
    }


    /*
     * Method to insert survey details into the SURVEY_DETAILS collection.
     */
    @Override
    public void insertSurveyDetails( SurveyDetails surveyDetails )
    {
        LOG.debug( "Method insertSurveyDetails() to insert details of survey started." );
        mongoTemplate.insert( surveyDetails, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method insertSurveyDetails() to insert details of survey finished." );
    }


    /*
     * Method to update email id by appending timestamp in documents from SurveyDetails collection
     * by agent id and customer's email-id.
     */
    @Override
    public void updateEmailForExistingFeedback( long agentId, String customerEmail )
    {
        LOG.debug( "Method updateEmailForExistingFeedback() to insert details of survey started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.CUSTOMER_EMAIL_COLUMN ).is( customerEmail ) );
        Update update = new Update();
        update.set( CommonConstants.CUSTOMER_EMAIL_COLUMN, customerEmail + "#" + new Timestamp( System.currentTimeMillis() ) );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method updateEmailForExistingFeedback() to insert details of survey finished." );
    }


    /*
     * Method to update questions for survey in SURVEY_DETAILS collection.
     */
    @Override
    public void updateCustomerResponse( String surveyId, SurveyResponse surveyResponse, int stage )
    {
        LOG.debug( "Method updateCustomerResponse() to update response provided by customer started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyId ) );
        Update update = new Update();
        update.set( "stage", stage );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        update.pull( "surveyResponse", new BasicDBObject( "question", surveyResponse.getQuestion() ) );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        mongoTemplate.updateMulti( query, new Update().push( "surveyResponse", surveyResponse ), SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method updateCustomerResponse() to update response provided by customer finished." );
    }
    
    @Override
    public void updateCustomerResponse( String surveyId, SurveyResponse surveyResponse)
    {
        LOG.debug( "Method updateCustomerResponse() to update response provided by customer started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyId ) );
        Update update = new Update();
        update.pull( "surveyResponse", new BasicDBObject( "question", surveyResponse.getQuestion() ) );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        mongoTemplate.updateMulti( query, new Update().push( "surveyResponse", surveyResponse ), SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method updateCustomerResponse() to update response provided by customer finished." );
    }


    /*
     * Method to update answer and response for gateway question of survey in SURVEY_DETAILS
     * collection.
     */
    @Override
    public void updateGatewayAnswer( String surveyId, String mood, String review, boolean isAbusive, String agreedToShare )
    {
        LOG.info( "Method updateGatewayAnswer() to update review provided by customer started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyId ) );
        Update update = new Update();
        update.set( CommonConstants.STAGE_COLUMN, CommonConstants.SURVEY_STAGE_COMPLETE );
        update.set( CommonConstants.MOOD_COLUMN, mood );
        update.set( CommonConstants.OPEN_RETAKE_SURVEY_REQUEST_COLUMN, false );
        update.set( "review", review );
        update.set( CommonConstants.IS_ABUSIVE_COLUMN, isAbusive );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        SurveyDetails details = getSurveyBySurveyMongoId( surveyId );
        if ( details != null && details.getSurveyCompletedDate() != null ) {
            update.set( CommonConstants.SURVEY_UPDATED_DATE_COLUMN, new Date() );
        } else {
            Date date = new Date();
            update.set( CommonConstants.SURVEY_UPDATED_DATE_COLUMN, date );
            update.set( CommonConstants.SURVEY_COMPLETED_DATE_COLUMN, date );
        }
        update.set( CommonConstants.EDITABLE_SURVEY_COLUMN, false );
        update.set( CommonConstants.AGREE_SHARE_COLUMN, agreedToShare );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method updateGatewayAnswer() to update review provided by customer finished." );
    }


    /*
     * Method to calculate and update final score based upon rating questions.
     */
    @Override
    public void updateFinalScore( String surveyId )
    {
        LOG.info( "Method to calculate and update final score based upon rating questions started." );
        Query query = new Query();
        List<String> ratingType = new ArrayList<>();
        ratingType.add( "sb-range-smiles" );
        ratingType.add( "sb-range-scale" );
        ratingType.add( "sb-range-star" );
        ratingType.add( "sb-range-0to10" );
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyId ) );
        query.addCriteria( Criteria.where( "surveyResponse.questionType" ).in( ratingType ) );
        List<SurveyResponse> surveyResponse = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION )
            .get( CommonConstants.INITIAL_INDEX ).getSurveyResponse();
        double noOfResponse = 0;
        double answer = 0;
        Update update = new Update();
        for ( SurveyResponse response : surveyResponse ) {
            if ( response.getQuestionType().equals( ratingType.get( CommonConstants.INITIAL_INDEX ) )
                || response.getQuestionType().equals( ratingType.get( 1 ) )
                || response.getQuestionType().equals( ratingType.get( 2 ) )
                || response.getQuestionType().equals( ratingType.get( 3 ) )
                && ( response.getAnswer() != null && !response.getAnswer().isEmpty() )) {
                //check if question type is 0to10 and divide answer by 2
                if(response.getQuestionType().equals( ratingType.get( 3 ))){
                    if(response.isConsiderForScore()){
                        int npsAnswer = Integer.parseInt( response.getAnswer() );
                        answer += (double) npsAnswer/2;
                        noOfResponse++;
                    }
                    //check if isNpsQuestion ans set npsScore
                    if(response.getIsNpsQuestion()){
                        update.set( CommonConstants.NPS_SCORE_COLUMN, response.getAnswer() );
                    }
                }else{
                    answer += Integer.parseInt( response.getAnswer() );
                    noOfResponse++;
                }
            }
        }
        update.set( CommonConstants.SCORE_COLUMN, Math.round( answer / noOfResponse * 1000.0 ) / 1000.0 );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method to calculate and update final score based upon rating questions finished." );
    }


    @Override
    public void updateSurveyAsAbusive( String surveyMongoId, String reporterEmail, String reporterName, String reportReason )
    {
        LOG.debug( "Method updateSurveyAsAbusive() to mark survey as abusive started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyMongoId ) );
        Update update = new Update();
        update.set( CommonConstants.IS_ABUSIVE_COLUMN, true );
        update.set( CommonConstants.IS_ABUSIVE_REPORTED_BY_USER_COLUMN, true );
        update.set( CommonConstants.SURVEY_LAST_ABUSE_REPORTED_DATE, new Date() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );

        query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.SURVEY_ID_COLUMN ).is( surveyMongoId ) );
        update = new Update();
        update.set( CommonConstants.SURVEY_ID_COLUMN, surveyMongoId );
        update.push( CommonConstants.ABUSE_REPORTERS_COLUMN, new ReporterDetail( reporterName, reporterEmail, reportReason ) );
        mongoTemplate.upsert( query, update, ABS_REPORTER_DETAILS_COLLECTION );
        LOG.debug( "Method updateSurveyAsAbusive() to mark survey as abusive finished." );
    }


    @Override
    public void updateSurveyAsUnAbusive( String surveyMongoId )
    {
        LOG.debug( "Method updateSurveyAsUnAbusive() to mark survey as unAbusive started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyMongoId ) );
        Update update = new Update();
        update.set( CommonConstants.IS_ABUSIVE_COLUMN, false );
        update.set( CommonConstants.IS_UNMARKED_ABUSIVE_COLUMN, true );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );

        query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.SURVEY_ID_COLUMN ).is( surveyMongoId ) );
        mongoTemplate.remove( query, ABS_REPORTER_DETAILS_COLLECTION );

        LOG.debug( "Method updateSurveyAsUnAbusive() to mark survey as unAbusive finished." );
    }


    @Override
    public void updateSurveyAsClicked( String surveyMongoId )
    {
        LOG.debug( "Method updateSurveyAsClicked() to mark survey as clicked started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyMongoId ) );
        Update update = new Update();
        update.set( CommonConstants.SURVEY_CLICKED_COLUMN, true );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method updateSurveyAsClicked() to mark survey as clicked finished." );
    }


    // This method returns all the surveys that have been clicked by customers so far.
    // If columnName field is passed null value it returns count of all the survey.
    // "columnName" field can contain either of "agentId/branchId/regionId/companyId".
    // "columnValue" field can contain respective values for the columnName.

    @Override
    public long getClickedSurveyCount( String columnName, long columnValue, int noOfDays, boolean filterAbusive )
    {
        LOG.debug( "Method to get count of total number of surveys clicked so far, getClickedSurveyCount() started." );
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
        Query query = new Query( Criteria.where( CommonConstants.SURVEY_CLICKED_COLUMN ).is( true ) );
        /*query.addCriteria(Criteria.where("surveyResponse").size(0));*/
        if ( columnName != null ) {
            query.addCriteria( Criteria.where( columnName ).is( columnValue ) );
        }
        if ( filterAbusive ) {
            query.addCriteria( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( false ) );
        }
        if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
            query.addCriteria( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) );
        }
        query.addCriteria( Criteria.where( CommonConstants.CREATED_ON ).gte( startDate ).lte( endDate ) );
        //SS-670
        /*query.addCriteria(
            Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).ne( CommonConstants.SURVEY_SOURCE_3RD_PARTY ) );*/
        LOG.debug( "Method to get count of total number of surveys clicked so far, getClickedSurveyCount() finished." );
        return mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
    }


    // This method returns all the surveys that have been completed by customers so far.
    // If columnName field is passed null value it returns count of all the survey.
    // "columnName" field can contain either of "agentId/branchId/regionId/companyId".
    // "columnValue" field can contain respective values for the columnName.

    @Override
    public long getCompletedSurveyCount( String columnName, long columnValue, int noOfDays )
    {
        LOG.debug( "Method to get count of total number of surveys completed so far, getCompletedSurveyCount() started." );
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
        Query query = new Query( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) );
        if ( columnName != null ) {
            query.addCriteria( Criteria.where( columnName ).is( columnValue ) );
        }
        query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ).lte( endDate ) );
        if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
            query.addCriteria( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) );
        }
        LOG.debug( "Method to get count of total number of surveys completed so far, getCompletedSurveyCount() finished." );
        return mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
    }


    // This method returns all the surveys that are not yet completed by customers.
    // If columnName field is passed null value it returns count of all the survey.
    // "columnName" field can contain either of "agentId/branchId/regionId/companyId".
    // "columnValue" field can contain respective values for the columnName.

    @Override
    public long getIncompleteSurveyCount( String columnName, long columnValue, int noOfDays )
    {
        LOG.debug( "Method to get count of surveys which are not yet completed, getIncompleteSurveyCount() started." );
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
        Query query = new Query( Criteria.where( CommonConstants.STAGE_COLUMN ).ne( CommonConstants.SURVEY_STAGE_COMPLETE ) );
        if ( columnName != null ) {
            query.addCriteria( Criteria.where( columnName ).is( columnValue ) );
        }
        query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ).lte( endDate ) );
        LOG.debug( "Method to get count of surveys which are not yet completed, getIncompleteSurveyCount() finished." );
        return mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
    }


    // This method returns a map of customers count based upon their mood.
    // Map contains Mood --> Customers count mapping for each mood for a given
    // agent/branch/region/company.

    @Override
    public Map<String, Long> getCountOfCustomersByMood( String columnName, long columnValue )
    {
        LOG.debug( "Method to get customers according to their mood, getCountOfCustomersByMood() started." );
        TypedAggregation<SurveyDetails> aggregation;
        if ( columnName == null ) {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                Aggregation.group( CommonConstants.MOOD_COLUMN ).count().as( "count" ) );
        } else {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                Aggregation.group( CommonConstants.MOOD_COLUMN ).count().as( "count" ) );
        }

        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );
        Map<String, Long> moodSplit = new HashMap<>();
        if ( result != null ) {
            @SuppressWarnings ( "unchecked") List<BasicDBObject> moodCount = (List<BasicDBObject>) result.getRawResults()
                .get( "result" );
            for ( BasicDBObject o : moodCount ) {
                moodSplit.put( o.get( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).toString(),
                    Long.parseLong( o.get( "count" ).toString() ) );
            }
        }
        LOG.debug( "Method to get customers according to their mood, getCountOfCustomersByMood() finished." );
        return moodSplit;
    }


    // This method returns the customers' count based upon the number of reminder mails sent to
    // them.

    @Override
    public Map<String, Long> getCountOfCustomersByReminderMails( String columnName, long columnValue )
    {
        LOG.debug(
            "Method to get customers according to the number of reminder emails sent, getCountOfCustomersByReminderMails() started." );
        TypedAggregation<SurveyDetails> aggregation;
        if ( columnName == null ) {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                Aggregation.group( CommonConstants.REMINDER_COUNT_COLUMN ).count().as( "count" ) );
        } else {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                Aggregation.group( CommonConstants.REMINDER_COUNT_COLUMN ).count().as( "count" ) );
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
        LOG.debug(
            "Method to get customers according to the number of reminder emails sent, getCountOfCustomersByReminderMails() finished." );
        return reminderCountSplit;
    }


    // This method returns the customers' count based upon their current stage i.e. number of
    // questions they have answered so far.

    @Override
    public Map<String, Long> getCountOfCustomersByStage( String columnName, long columnValue )
    {
        LOG.debug( "Method to get customers according to stage of survey, getCountOfCustomersByStage() started." );
        TypedAggregation<SurveyDetails> aggregation;
        if ( columnName == null ) {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                Aggregation.group( CommonConstants.STAGE_COLUMN ).count().as( "count" ) );
        } else {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                Aggregation.group( CommonConstants.STAGE_COLUMN ).count().as( "count" ) );
        }
        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );
        Map<String, Long> stageCountSplit = new HashMap<>();
        if ( result != null ) {
            @SuppressWarnings ( "unchecked") List<BasicDBObject> stageCount = (List<BasicDBObject>) result.getRawResults()
                .get( "result" );
            for ( BasicDBObject stage : stageCount ) {
                stageCountSplit.put( stage.get( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).toString(),
                    Long.parseLong( stage.get( "count" ).toString() ) );
            }
        }
        LOG.debug( "Method to get customers according to stage of survey, getCountOfCustomersByStage() finished." );
        return stageCountSplit;
    }


    // Method to return number of surveys taken in a given month of a particular year.

    @Override
    public long getTotalSurveyCountByMonth( int year, int month )
    {
        LOG.debug(
            "Method to get count of total number of surveys taken in a given month and year, getTotalSurveyCountByMonth() started." );
        Calendar calendar = Calendar.getInstance();
        calendar.set( year, month, 1 );
        Date startDate = calendar.getTime();
        // Returns max value for date in the month set in Calendar instance.
        calendar.set( year, month, calendar.getActualMaximum( 5 ) );
        Date endDate = calendar.getTime();
        Query query = new Query( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) );
        query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) );
        LOG.debug(
            "Method to get count of total number of surveys taken in a given month and year, getTotalSurveyCountByMonth() finished." );
        return mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
    }


    @Override
    @SuppressWarnings ( "unchecked")
    public double getRatingForPastNdays( String columnName, long columnValue, int noOfDays, boolean aggregateAbusive,
        boolean realtechAdmin, boolean includeZillow, long zillowReviewCount, double zillowTotalReviewScore )
    {
        LOG.debug(
            "Method getRatingOfAgentForPastNdays(), to calculate rating of agent started for columnName: {} columnValue: {} noOfDays: {} aggregateAbusive: {}",
            columnName, columnValue, noOfDays, aggregateAbusive );
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

        if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
            query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ).andOperator(
                Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ),
                Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ),
                Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) ) );
        } else {
            query.addCriteria( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ).andOperator(
                Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ),
                Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ) );
        }

        TypedAggregation<SurveyDetails> aggregation = null;
        if ( !aggregateAbusive && !realtechAdmin ) {
            if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ),
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ),
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) ),
                    Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( aggregateAbusive ) ),
                    Aggregation.group( columnName ).sum( CommonConstants.SCORE_COLUMN ).as( "total_score" ) );
            } else {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ),
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ),
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( aggregateAbusive ) ),
                    Aggregation.group( columnName ).sum( CommonConstants.SCORE_COLUMN ).as( "total_score" ) );
            }
        } else if ( aggregateAbusive && !realtechAdmin ) {
            if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ),
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) ),
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.group( columnName ).sum( CommonConstants.SCORE_COLUMN ).as( "total_score" ) );
            } else {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ),
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ),
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.group( columnName ).sum( CommonConstants.SCORE_COLUMN ).as( "total_score" ) );
            }
        } else {
            if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) ),
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.group( columnName ).sum( CommonConstants.SCORE_COLUMN ).as( "total_score" ) );
            } else {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ),
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.group( columnName ).sum( CommonConstants.SCORE_COLUMN ).as( "total_score" ) );
            }
        }

        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );
        long reviewsCount = mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Count of aggregated results : {}", reviewsCount );
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
            } else {
                if ( !basicDBObject.isEmpty() && basicDBObject.get( 0 ).get( "total_score" ) != null ) {
                    rating = (double) basicDBObject.get( 0 ).get( "total_score" ) / reviewsCount;
                }
            }
        } else if ( includeZillow ) {
            if ( zillowReviewCount > 0 )
                rating = zillowTotalReviewScore / zillowReviewCount;
        }
        LOG.debug( "Method getRatingOfAgentForPastNdays(), to calculate rating of agent finished." );
        return rating;
    }


    // January is denoted with 0.
    public double getRatingByMonth( String columnName, long columnValue, int year, int month )
    {
        LOG.debug( "Method getRatingOfAgentByMonth(), to calculate rating of agent started." );
        Calendar calendar = Calendar.getInstance();
        calendar.set( year, month, 1 );
        Date startDate = calendar.getTime();
        // Returns max value for date in the month set in Calendar instance.
        calendar.set( year, month, calendar.getActualMaximum( 5 ) );
        Date endDate = calendar.getTime();
        Query query = new Query( Criteria.where( columnName ).is( columnValue ).andOperator(
            Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ),
            Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ) );
        long count = mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
        if ( count < 3 ) {
            LOG.debug( columnName + " " + columnValue + " does not qualify for calculation of rating. Returning..." );
            return -1;
        }
        TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
            Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ),
            Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ),
            Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
            Aggregation.group( columnName ).sum( CommonConstants.SCORE_COLUMN ).as( "total_score" ) );

        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );
        double rating = 0;
        if ( result != null ) {
            rating = ( (long) result.getRawResults().get( "total_score" ) ) / count;
        }
        LOG.debug( "Method getRatingOfAgentByMonth(), to calculate rating of agent finished." );
        return rating;
    }


    // Method to get count of posts shared by customers on various social networking sites for
    // "agent/branch/region/company/all".
    // Returns posts count on that site.

    @Override
    public long getSocialPostsCount( String columnName, long columnValue, int numberOfDays )
    {
        LOG.debug( "Method to count number of social posts by customers, getSocialPostsCount() started." );
        Date startDate = null;
        /**
         * if days is not set, take the start date as 1 jan 1970
         */
        if ( numberOfDays == -1 ) {
            startDate = new Date( 0l );
        } else {
            startDate = getNdaysBackDate( numberOfDays );
        }

        Date endDate = Calendar.getInstance().getTime();
        TypedAggregation<SurveyDetails> aggregation;
        if ( columnName == null ) {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ).lte( endDate ) ),
                Aggregation.unwind( "sharedOn" ), Aggregation.group( "sharedOn" ).count().as( "count" ) ).withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
        } else {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ).lte( endDate ) ),
                Aggregation.unwind( "sharedOn" ), Aggregation.group().count().as( "count" ) ).withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
        }
        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );

        @SuppressWarnings ( "unchecked") List<BasicDBObject> shares = (List<BasicDBObject>) result.getRawResults()
            .get( "result" );
        long socialPostCount = 0;
        if ( shares != null && shares.size() != 0 ) {
            socialPostCount = (int) shares.get( CommonConstants.INITIAL_INDEX ).get( "count" );
        }
        return socialPostCount;
    }


    @Override
    public long getSocialPostsCountBasedOnHierarchy( int numberOfDays, String columnName, long columnValue,
        boolean fetchAbusive, boolean forStatistics )
    {
        LOG.debug( "Method to count number of social posts by customers, getSocialPostsCountBasedOnHierarchy() started." );
        long socialPostCount = 0;
        Date endDate = null;
        Date startDate = null;
        if ( numberOfDays >= 0 ) {
            endDate = Calendar.getInstance().getTime();
            startDate = getNdaysBackDate( numberOfDays );
        }

        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) );
        //criteria for abusive reviews
        if ( !fetchAbusive ) {

            criteriaList.add( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) );
        }
        if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
            criteriaList.add( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) );
        }

        if ( forStatistics ) {
            criteriaList.add( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN )
                .nin( Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) );
        } else {
            criteriaList
                .add( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).ne( CommonConstants.SURVEY_SOURCE_3RD_PARTY ) );
        }

        if ( columnName == null ) {
        } else {
            criteriaList.add( Criteria.where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN ).ne( null ) );
            if ( columnName.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) ) {
                criteriaList.add( Criteria.where(
                    CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.COMPANY_MEDIA_POST_DETAILS_COLUMN )
                    .ne( null ) );
                criteriaList.add( Criteria
                    .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
                        + CommonConstants.COMPANY_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.COMPANY_ID_COLUMN )
                    .is( columnValue ) );
            } else if ( columnName.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN ) ) {
                criteriaList.add( Criteria.where(
                    CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN )
                    .ne( null ) );
                criteriaList.add( Criteria
                    .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
                        + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.REGION_ID_COLUMN )
                    .is( columnValue ) );

            } else if ( columnName.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN ) ) {
                criteriaList.add( Criteria.where(
                    CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.BRANCH_MEDIA_POST_DETAILS_COLUMN )
                    .ne( null ) );
                criteriaList.add( Criteria
                    .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
                        + CommonConstants.BRANCH_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.BRANCH_ID_COLUMN )
                    .is( columnValue ) );
            } else if ( columnName.equalsIgnoreCase( CommonConstants.AGENT_ID_COLUMN ) ) {

                criteriaList.add( Criteria.where(
                    CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.AGENT_MEDIA_POST_DETAILS_COLUMN )
                    .ne( null ) );
                criteriaList.add( Criteria
                    .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
                        + CommonConstants.AGENT_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.AGENT_ID_COLUMN )
                    .is( columnValue ) );

            }

            if ( startDate != null && endDate == null ) {
                criteriaList.add( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) );
            } else if ( startDate == null && endDate != null ) {
                criteriaList.add( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) );
            } else if ( startDate != null && endDate != null ) {
                criteriaList.add( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ).lte( endDate ) );
            }

            criteriaList.add( Criteria.where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN ).exists( true ) );

            Criteria criteria = new Criteria().andOperator( criteriaList.toArray( new Criteria[criteriaList.size()] ) );

            //BEGIN PERFORMANCE OPTIMISATION
            List<AggregationOperation> operations = new ArrayList<>();
            operations.add( new MatchOperation( criteria ) );
            DBObject firstGroup = new BasicDBObject( "$group",
                new BasicDBObject( CommonConstants.DEFAULT_MONGO_ID_COLUMN, "$_id" )
                    .append( "agent", new BasicDBObject( "$push", "$socialMediaPostDetails.agentMediaPostDetails" ) )
                    .append( "company", new BasicDBObject( "$push", "$socialMediaPostDetails.companyMediaPostDetails" ) )
                    .append( "branch", new BasicDBObject( "$first", "$socialMediaPostDetails.branchMediaPostDetailsList" ) )
                    .append( "region", new BasicDBObject( "$first", "$socialMediaPostDetails.regionMediaPostDetailsList" ) ) );
            operations.add( new CustomAggregationOperation( firstGroup ) );
            DBObject project = new BasicDBObject( "$project", new BasicDBObject( "comb",
                new BasicDBObject( "$setUnion", Arrays.asList( "$company", "$branch", "$region", "$agent" ) ) ) );
            operations.add( new CustomAggregationOperation( project ) );
            operations.add( Aggregation.unwind( "$comb" ) );
            DBObject secondGroup = new BasicDBObject( "$group",
                new BasicDBObject( CommonConstants.DEFAULT_MONGO_ID_COLUMN, "count" ).append( "count",
                    new BasicDBObject( "$sum", new BasicDBObject( "$size", "$comb.sharedOn" ) ) ) );
            operations.add( new CustomAggregationOperation( secondGroup ) );
            Aggregation aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class, operations ).withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
            LOG.debug( "Aggregation : " + aggregation.toString() );
            AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
                SurveyDetails.class );

            if ( result != null ) {
                @SuppressWarnings ( "unchecked") List<BasicDBObject> initiatorCount = (List<BasicDBObject>) result
                    .getRawResults().get( "result" );
                if ( initiatorCount != null && !initiatorCount.isEmpty() ) {
                    Map<String, Object> resultObj = (Map<String, Object>) initiatorCount.get( 0 );
                    String countStr = resultObj.get( "count" ).toString();
                    socialPostCount = Long.parseLong( countStr );
                }
            }
        }
        LOG.debug( "Method to count number of social posts by customers, getSocialPostsCountBasedOnHierarchy() finished." );
        return socialPostCount;
    }


    // Method to get count of surveys initiated by customers and agents separately.
    // Columns can only be from : {agentId/branchId/regionId}

    @Override
    public Map<String, Long> getCountOfSurveyInitiators( String columnName, long columnValue )
    {
        LOG.debug( "Method to count number of surveys initiators, getCountOfSurveyInitiators() started." );
        TypedAggregation<SurveyDetails> aggregation;
        if ( columnName == null ) {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                Aggregation.group( CommonConstants.INITIATED_BY_COLUMN ).count().as( "count" ) );
        } else {
            aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                Aggregation.group( CommonConstants.INITIATED_BY_COLUMN ).count().as( "count" ) );
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
        LOG.debug( "Method to count number of surveys initiators, getCountOfSurveyInitiators() finished." );
        return initiatorCountSplit;
    }


    /*
     * Returns a list of feedbacks provided by customers. First sorted on score then on date (both
     * descending). ColumnName can be "agentId/branchId/regionId/companyId". ColumnValue should be
     * value for respective column. limitScore is the max score under which reviews have to be shown
     */
    @Override
    public List<SurveyDetails> getFeedbacks( String columnName, long columnValue, int start, int rows, double startScore,
        double limitScore, boolean fetchAbusive, Date startDate, Date endDate, String sortCriteria, List<String> surveySources, String order )
    {
        LOG.debug( "Method to fetch all the feedbacks from SURVEY_DETAILS collection, getFeedbacks() started." );

        Query query = new Query();
        if ( columnName != null ) {
            query.addCriteria( Criteria.where( columnName ).is( columnValue ) );
        }

        /**
         * fetching only completed surveys
         */
        //query.addCriteria( Criteria.where( CommonConstants.REVIEW_COLUMN ).exists( true ).nin( "" ) );
        query.addCriteria( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) );
        
        if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
            query.addCriteria( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) );
        }
        query.fields().exclude( CommonConstants.SOCIAL_MEDIA_POST_RESPONSE_DETAILS_COLUMN );
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

        // Commented as Zillow reviews for individuals will also be shown from cache
        // if ( columnName.equalsIgnoreCase( CommonConstants.AGENT_ID_COLUMN ) ) {
        //    query
        //        .addCriteria( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).ne( CommonConstants.SURVEY_SOURCE_ZILLOW ) );
        // }

        if ( sortCriteria != null && sortCriteria.equals( CommonConstants.REVIEWS_SORT_CRITERIA_FEATURE ) ) {
            if ( startScore > -1 && limitScore > -1 ) {
                query.addCriteria( new Criteria().andOperator( Criteria.where( CommonConstants.SCORE_COLUMN ).gte( startScore ),
                    Criteria.where( CommonConstants.SCORE_COLUMN ).lte( limitScore ) ) );
            }
        }

        if ( start > -1 ) {
            query.skip( start );
        }
        if ( rows > -1 ) {
            query.limit( rows );
        }

        
        if( surveySources != null && !surveySources.isEmpty() ) {
            query.addCriteria( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).in( surveySources ) );
        }
        
        
        if( CommonConstants.WIDGET_ORDER_NEWEST_FIRST.equals( order ) ) {  
            query.with( new Sort( Sort.Direction.DESC, CommonConstants.SURVEY_UPDATED_DATE_COLUMN ) );
        }  else if( CommonConstants.WIDGET_ORDER_OLDEST_FIRST.equals( order ) ) {
            query.with( new Sort( Sort.Direction.ASC, CommonConstants.SURVEY_UPDATED_DATE_COLUMN ) );
        } else if( CommonConstants.WIDGET_ORDER_HIGEST_RATING_FIRST.equals( order ) ) {
            query.with( new Sort( Sort.Direction.DESC, CommonConstants.SCORE_COLUMN ) );
        } else if( CommonConstants.WIDGET_ORDER_LOWEST_RATING_FIRST.equals( order ) ) {
            query.with( new Sort( Sort.Direction.ASC, CommonConstants.SCORE_COLUMN ) );
        } else {
            query.with( new Sort( Sort.Direction.DESC, CommonConstants.SURVEY_UPDATED_DATE_COLUMN ) );
        }
        

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

        LOG.debug( "Method to fetch all the feedbacks from SURVEY_DETAILS collection, getFeedbacks() finished." );
        return surveysWithReviews;
    }


    @Override
    public List<SurveyDetails> getFeedbacksForReports( String columnName, long columnValue, int start, int rows,
        double startScore, double limitScore, boolean fetchAbusive, Date startDate, Date endDate, String sortCriteria )
    {
        LOG.debug( "Method to fetch all the feedbacks from SURVEY_DETAILS collection, getFeedbacks() started." );

        Query query = new Query();
        if ( columnName != null ) {
            query.addCriteria( Criteria.where( columnName ).is( columnValue ) );
        }

        /**
         * fetching only completed surveys
         */
        query.addCriteria( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) );
        if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
            query.addCriteria( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) );
        }
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

        query.with( new Sort( Sort.Direction.DESC, CommonConstants.CREATED_ON ) );

        List<SurveyDetails> surveysWithReviews = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );

        LOG.debug( "Method to fetch all the feedbacks from SURVEY_DETAILS collection, getFeedbacks() finished." );
        return surveysWithReviews;
    }


    @Override
    public long getFeedBacksCount( String columnName, long columnValue, double startScore, double limitScore,
        boolean fetchAbusive, boolean notRecommended, boolean includeZillow, long zillowReviewCount )
    {
        LOG.debug(
            "Method getFeedBacksCount started for columnName: {} columnValue: {} startScore {} limitScore: {} and fetchAbusive: {}",
            columnName, columnValue, startScore, limitScore, fetchAbusive );
        Query query = new Query();
        if ( columnName != null ) {
            query.addCriteria( Criteria.where( columnName ).is( columnValue ) );
        }
        /**
         * fetching only completed surveys
         */
        if ( notRecommended ) {
            query.addCriteria( Criteria.where( CommonConstants.REVIEW_COLUMN ).exists( true ).nin( "" ) );
        } else {
            query.addCriteria( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) );
        }
        if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
            query.addCriteria( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) );
        }

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
                query.addCriteria( new Criteria().andOperator( Criteria.where( CommonConstants.SCORE_COLUMN ).gte( startScore ),
                    Criteria.where( CommonConstants.SCORE_COLUMN ).lt( limitScore ) ) );
            } else {
                query.addCriteria( new Criteria().andOperator( Criteria.where( CommonConstants.SCORE_COLUMN ).gte( startScore ),
                    Criteria.where( CommonConstants.SCORE_COLUMN ).lte( limitScore ) ) );
            }
        }

        long feedBackCount = mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
        if ( includeZillow && !notRecommended ) {
            // get zillow review count based on column name
            feedBackCount += zillowReviewCount;
        }
        LOG.debug( "Method getFeedBacksCount executed successfully.Returning feedBackCount: {}", feedBackCount );
        return feedBackCount;
    }


    @Override
    public long getSimpleFeedBacksCount( String columnName, long columnValue, double startScore, double limitScore, boolean fetchAbusive )
    {
        LOG.debug(
            "Method getSimpleFeedBacksCount started for columnName: {} columnValue: {} startScore {} limitScore: {} and fetchAbusive: {}",
            columnName, columnValue, startScore, limitScore, fetchAbusive );
        Query query = new Query();
        if ( columnName != null ) {
            query.addCriteria( Criteria.where( columnName ).is( columnValue ) );
        }
        /**
         * fetching only completed surveys
         */
        query.addCriteria( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) );
        
        if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
            query.addCriteria( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) );
        }

        /**
         * adding isAbusive criteria only if fetch abusive flag is false, i.e only non abusive posts
         * are to be fetched else fetch all the records
         */
        if ( !fetchAbusive ) {
            query.addCriteria( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) );
        }

        if ( startScore > -1 && limitScore > -1 ) {
            query.addCriteria( new Criteria().andOperator( Criteria.where( CommonConstants.SCORE_COLUMN ).gte( startScore ),
                Criteria.where( CommonConstants.SCORE_COLUMN ).lt( limitScore ) ) );
        } else {
            if( startScore > -1 ) {
                query.addCriteria( Criteria.where( CommonConstants.SCORE_COLUMN ).gte( startScore ) );
            }
            
            if( limitScore > -1 ) {
                query.addCriteria( Criteria.where( CommonConstants.SCORE_COLUMN ).lte( limitScore ) );
            }
        }
        

        long feedBackCount = mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method getSimpleFeedBacksCount executed successfully.Returning feedBackCount: {}", feedBackCount );
        return feedBackCount;
    }

    
    /*
     * Returns a list of survey which are not yet competed by customers.Sorted on date (
     * descending). ColumnName can be "agentId/branchId/regionId/companyId". ColumnValue should be
     * value for respective column. limitScore is the max score under which reviews have to be shown
     */

    @Override
    public List<SurveyDetails> getIncompleteSurvey( String columnName, long columnValue, int start, int rows, double startScore,
        double limitScore, Date startDate, Date endDate )
    {
        LOG.debug( "Method to fetch all the incomplete survey from SURVEY_DETAILS collection, getIncompleteSurvey() started." );
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

        LOG.debug( "Method to fetch all the incoplete survey from SURVEY_DETAILS collection, getIncompleteSurvey() finished." );
        return surveysWithReviews;
    }


    /*
     * Method to increase reminder count by 1.
     */
    @Override
    public void updateReminderCount( long agentId, String customerEmail )
    {
        LOG.debug( "Method to increase reminder count by 1, updateReminderCount() started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.CUSTOMER_EMAIL_COLUMN ).is( customerEmail ) );
        Update update = new Update();
        update.inc( CommonConstants.REMINDER_COUNT_COLUMN, 1 );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method to increase reminder count by 1, updateReminderCount() finished." );
    }


    @Override
    public long getCompletedSurveyCount( String organizationUnitColumn, long organizationUnitColumnValue, Timestamp startDate,
        Timestamp endDate, boolean filterAbusive, boolean filterZillowAnd3rdPartyReviews ) throws InvalidInputException
    {
        LOG.debug(
            "Getting completed survey count for " + organizationUnitColumn + " with value " + organizationUnitColumnValue );
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
        query.addCriteria( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) );

        if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
            query.addCriteria( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) );
        }
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
        if ( filterZillowAnd3rdPartyReviews ) {
            query.addCriteria( Criteria.where( CommonConstants.SOURCE_COLUMN )
                .nin( Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) );
        }
        LOG.debug( "Query: " + query.toString() );
        long count = mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Found " + count + " completed surveys" );
        return count;
    }


    @Override
    public long getCompletedSurveyCountForStatistics( String organizationUnitColumn, long organizationUnitColumnValue,
        Timestamp startDate, Timestamp endDate, boolean filterAbusive ) throws InvalidInputException
    {
        LOG.debug( "Getting completed survey count for {} with value {}", organizationUnitColumn, organizationUnitColumnValue );
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
        query.addCriteria( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) );

        // Do not show zillow review in statistics completed count, SS-307
        query.addCriteria( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN )
            .nin( Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) );
        if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
            query.addCriteria( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) );
        }
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
        if(LOG.isDebugEnabled()) {
            LOG.debug( "Query: {}" , query.toString() );
        }
        long count = mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Found {} completed surveys", count );
        return count;
    }


    @Override
    public long getZillowImportCount( String organizationUnitColumn, long organizationUnitColumnValue, Timestamp startDate,
        Timestamp endDate, boolean filterAbusive ) throws InvalidInputException
    {
        LOG.debug(
            "Getting completed survey count for {} with value {}" , organizationUnitColumn,  organizationUnitColumnValue );
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
        query.addCriteria( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) );

        // Do not show zillow review in statistics completed count, SS-307
        query.addCriteria( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).is( CommonConstants.SURVEY_SOURCE_ZILLOW ) );
        if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
            query.addCriteria( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) );
        }
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
        if(LOG.isDebugEnabled()) {
            LOG.debug( "Query: {}" , query.toString() );
        }
        long count = mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Found {} completed surveys",count );
        return count;
    }


    @Override
    public long get3rdPartyImportCount( String organizationUnitColumn, long organizationUnitColumnValue, Timestamp startDate,
        Timestamp endDate, boolean filterAbusive ) throws InvalidInputException
    {
        LOG.debug(
            "Getting get3rdPartyImportCount for {} with value {}" , organizationUnitColumn, organizationUnitColumnValue );
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
        query.addCriteria( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) );

        // Do not show zillow review in statistics completed count, SS-307
        query.addCriteria(
            Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).is( CommonConstants.SURVEY_SOURCE_3RD_PARTY ) );
        if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
            query.addCriteria( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) );
        }
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
        if(LOG.isDebugEnabled()) {
            LOG.debug( "Query: {}" , query.toString() );
        }
        long count = mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Found {} get3rdParty surveys", count );
        return count;
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public Map<Integer, Integer> getCompletedSurveyAggregationCount( String organizationUnitColumn,
        long organizationUnitColumnValue, Timestamp startDate, Timestamp endDate, String aggregateBy )
        throws InvalidInputException
    {
        Map<Integer, Integer> aggregatedResult = null;
        LOG.debug( "Aggregating completed surveys. Input organizationUnitColumn: {} \t organizationUnitColumnValue: {} \t startDate: {} \t endDate: {} \t aggregateBy: {}", organizationUnitColumn, organizationUnitColumnValue, startDate, endDate, aggregateBy );
        if ( aggregateBy == null || aggregateBy.isEmpty() ) {
            LOG.warn( "aggregate by field is empty" );
            throw new InvalidInputException( "aggregate by field is empty" );
        }
        LOG.debug( "Getting the result aggregated by {}" , aggregateBy );
        // DONT MODIFY IF YOU DONT KNOW WHAT YOU ARE DOING
        // Using BasicDBObject for aggregation
        BasicDBList pipeline = new BasicDBList();
        if ( organizationUnitColumn != null && !organizationUnitColumn.isEmpty() ) {
            // adding organization unit
            pipeline
                .add( new BasicDBObject( "$match", new BasicDBObject( organizationUnitColumn, organizationUnitColumnValue ) ) );
        }
        // match survey stage
        pipeline.add( new BasicDBObject( "$match",
            new BasicDBObject( CommonConstants.STAGE_COLUMN, CommonConstants.SURVEY_STAGE_COMPLETE ) ) );
        if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
            pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN, true ) ) );
        }
        pipeline.add(
            new BasicDBObject( "$match", new BasicDBObject( CommonConstants.SURVEY_SOURCE_COLUMN, new BasicDBObject( "$nin",
                Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ) ) );
        // match non abusive survey
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.IS_ABUSIVE_COLUMN, false ) ) );
        // match start date
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.MODIFIED_ON_COLUMN,
            new BasicDBObject( "$gte", new Date( startDate.getTime() ) ) ) ) );
        // match end date
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.MODIFIED_ON_COLUMN,
            new BasicDBObject( "$lte", new Date( endDate.getTime() ) ) ) ) );
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
        pipeline.add( new BasicDBObject( "$group",
            new BasicDBObject( "_id", "$groupCol" ).append( "count", new BasicDBObject( "$sum", 1 ) ) ) );
        BasicDBObject aggregationObject = new BasicDBObject( "aggregate", SURVEY_DETAILS_COLLECTION ).append( "pipeline",
            pipeline );
        
        if(LOG.isDebugEnabled()) {
            LOG.debug(
                "aggregation for get completed survey for garph is :{} , {}", aggregationObject.toString(), aggregationObject );
        }
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
        LOG.debug( "Returning aggregating completed survey results" );
        return aggregatedResult;
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public Map<Integer, Integer> getClickedSurveyAggregationCount( String organizationUnitColumn,
        long organizationUnitColumnValue, Timestamp startDate, Timestamp endDate, String aggregateBy )
        throws InvalidInputException
    {
        Map<Integer, Integer> aggregatedResult = null;
        LOG.debug( "Aggregating clicked surveys. Input organizationUnitColumn: {} \t organizationUnitColumnValue: {} \t startDate: {} \t endDate: {} \t aggregateBy: {}" , organizationUnitColumn, organizationUnitColumnValue, startDate, endDate, aggregateBy  );
        if ( aggregateBy == null || aggregateBy.isEmpty() ) {
            LOG.warn( "aggregate by field is empty" );
            throw new InvalidInputException( "aggregate by field is empty" );
        }
        LOG.debug( "Getting the result aggregated by {}" , aggregateBy );
        // DONT MODIFY IF YOU DONT KNOW WHAT YOU ARE DOING
        // Using BasicDBObject for aggregation
        BasicDBList pipeline = new BasicDBList();
        if ( organizationUnitColumn != null && !organizationUnitColumn.isEmpty() ) {
            // adding organization unit
            pipeline
                .add( new BasicDBObject( "$match", new BasicDBObject( organizationUnitColumn, organizationUnitColumnValue ) ) );
        }
        //match for non abusive reviews
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.IS_ABUSIVE_COLUMN, false ) ) );
        if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
            pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN, true ) ) );
        }
        // match the survey stage should be complete
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.SURVEY_CLICKED_COLUMN, true ) ) );

        // Commented as Zillow surveys are not stored in database, SS-1276
        // match non zillow survey
        pipeline.add(
            new BasicDBObject( "$match", new BasicDBObject( CommonConstants.SURVEY_SOURCE_COLUMN, new BasicDBObject( "$nin",
                Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ) ) );
        // match start date
        pipeline.add( new BasicDBObject( "$match",
            new BasicDBObject( CommonConstants.CREATED_ON, new BasicDBObject( "$gte", new Date( startDate.getTime() ) ) ) ) );
        // match end date
        pipeline.add( new BasicDBObject( "$match",
            new BasicDBObject( CommonConstants.CREATED_ON, new BasicDBObject( "$lte", new Date( endDate.getTime() ) ) ) ) );
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
        pipeline.add( new BasicDBObject( "$group",
            new BasicDBObject( "_id", "$groupCol" ).append( "count", new BasicDBObject( "$sum", 1 ) ) ) );
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
        LOG.debug( "Returning aggregating completed survey results" );
        return aggregatedResult;
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public Map<Integer, Integer> getSocialPostsAggregationCount( String organizationUnitColumn,
        long organizationUnitColumnValue, Timestamp startDate, Timestamp endDate, String aggregateBy )
        throws InvalidInputException
    {
        Map<Integer, Integer> aggregatedResult = null;
        LOG.debug( "Aggregating completed surveys. Input organizationUnitColumn: {} \t organizationUnitColumnValue: {} \t startDate: {} \t endDate: {}  \t aggregateBy: {}" , organizationUnitColumn, organizationUnitColumnValue, startDate, endDate, aggregateBy );
        if ( aggregateBy == null || aggregateBy.isEmpty() ) {
            LOG.warn( "aggregate by field is empty" );
            throw new InvalidInputException( "aggregate by field is empty" );
        }
        LOG.debug( "Getting the result aggregated by {}", aggregateBy );
        // DONT MODIFY IF YOU DONT KNOW WHAT YOU ARE DOING
        // Using BasicDBObject for aggregation
        BasicDBList pipeline = new BasicDBList();
        if ( organizationUnitColumn != null && !organizationUnitColumn.isEmpty() ) {
            // adding organization unit
            pipeline
                .add( new BasicDBObject( "$match", new BasicDBObject( organizationUnitColumn, organizationUnitColumnValue ) ) );
        }

        //match for non abusive reviews
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.IS_ABUSIVE_COLUMN, false ) ) );
        if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
            pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN, true ) ) );
        }

        // exclude zillow surveys
        pipeline.add(
            new BasicDBObject( "$match", new BasicDBObject( CommonConstants.SURVEY_SOURCE_COLUMN, new BasicDBObject( "$nin",
                Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ) ) );
        // match the survey stage should be complete
        pipeline.add( new BasicDBObject( "$match",
            new BasicDBObject( CommonConstants.STAGE_COLUMN, CommonConstants.SURVEY_STAGE_COMPLETE ) ) );
        // match if social media post details exists
        pipeline.add( new BasicDBObject( "$match",
            new BasicDBObject( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN, new BasicDBObject( "$exists", true ) ) ) );
        // match start date
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.MODIFIED_ON_COLUMN,
            new BasicDBObject( "$gte", new Date( startDate.getTime() ) ) ) ) );
        // match end date
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.MODIFIED_ON_COLUMN,
            new BasicDBObject( "$lte", new Date( endDate.getTime() ) ) ) ) );
        // add projection level 1
        BasicDBObject firstProjectionObject = new BasicDBObject( CommonConstants.MODIFIED_ON_COLUMN, 1 );
        // add agent post detail column
        firstProjectionObject.append(
            CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.AGENT_MEDIA_POST_DETAILS_COLUMN, 1 );
        // add company post detail column
        firstProjectionObject.append(
            CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.COMPANY_MEDIA_POST_DETAILS_COLUMN, 1 );
        // add region post detail column
        firstProjectionObject.append(
            CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN, 1 );
        // add branch post detail column
        firstProjectionObject.append(
            CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.BRANCH_MEDIA_POST_DETAILS_COLUMN, 1 );
        // add to pieline
        pipeline.add( new BasicDBObject( "$project", firstProjectionObject ) );
        // unwind region media post column as it is an array
        pipeline.add( new BasicDBObject( "$unwind",
            "$" + CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN ) );
        // unwind branch media post column as it is an array
        pipeline.add( new BasicDBObject( "$unwind",
            "$" + CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.BRANCH_MEDIA_POST_DETAILS_COLUMN ) );

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
        pipeline.add( new BasicDBObject( "$group",
            new BasicDBObject( "_id", "$groupCol" ).append( "count", new BasicDBObject( "$sum", "$postsCount" ) ) ) );
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
        LOG.debug( "Returning aggregating completed survey results" );
        return aggregatedResult;
    }


    @Override
    public List<SurveyDetails> getIncompleteSurveyCustomers( long companyId, int surveyReminderInterval, int maxReminders )
    {
        LOG.debug(
            "Method to get list of customers who have not yet completed their survey, getIncompleteSurveyCustomers() started." );
        Date cutOffDate = getNdaysBackDate( surveyReminderInterval );
        Query query = new Query();
        query.addCriteria( new Criteria().andOperator( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( companyId ),
            Criteria.where( CommonConstants.LAST_REMINDER_FOR_INCOMPLETE_SURVEY ).lte( cutOffDate ),
            Criteria.where( CommonConstants.REMINDER_COUNT_COLUMN ).lt( maxReminders ) ) );
        List<SurveyDetails> surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        LOG.debug(
            "Method to get list of customers who have not yet completed their survey, getIncompleteSurveyCustomers() finished." );
        return surveys;
    }


    private Date getNdaysBackDateForIncompleteSocialPostSurveys( int noOfDays )
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.HOUR_OF_DAY, 0 );
        calendar.set( Calendar.MINUTE, 0 );
        calendar.set( Calendar.SECOND, 0 );
        calendar.set( Calendar.MILLISECOND, 0 );
        calendar.add( Calendar.DATE, noOfDays * ( -1 ) );
        Date startDate = calendar.getTime();
        return startDate;
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
        LOG.debug( "Method to increase reminder count by 1, updateReminderCount() started." );
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
        LOG.debug( "Method to increase reminder count by 1, updateReminderCount() finished." );
    }


    @Override
    public void updateReminderCountForSocialPost( Long agentId, String customerEmail )
    {
        LOG.debug( "Method to increase reminder count by 1, updateReminderCountForSocialPost() started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.CUSTOMER_EMAIL_COLUMN ).is( customerEmail ) );
        Update update = new Update();
        Date date = new Date();
        update.set( CommonConstants.LAST_REMINDER_FOR_SOCIAL_POST, date );
        update.push( CommonConstants.REMINDERS_FOR_SOCIAL_POSTS, date );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method to increase reminder count by 1, updateReminderCountForSocialPost() finished." );
    }


    @Override
    public List<SurveyDetails> getIncompleteSocialPostCustomersEmail( long companyId, int surveyReminderInterval,
        int maxReminders )
    {
        LOG.debug(
            "Method to get list of customers who have not yet shared their survey on all the social networking sites, getIncompleteSocialPostCustomersEmail() started." );
        Date cutOffCompletionDate = getNdaysBackDateForIncompleteSocialPostSurveys( maxSocialPostReminderInterval );
        Date cutOffDate = getNdaysBackDateForIncompleteSocialPostSurveys( surveyReminderInterval );
        Query query = new Query();

        query.addCriteria( new Criteria().andOperator( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( companyId ),
            Criteria.where( CommonConstants.SURVEY_COMPLETED_DATE_COLUMN ).gte( cutOffCompletionDate ).lt( cutOffDate ),
            Criteria.where( CommonConstants.LAST_REMINDER_FOR_SOCIAL_POST ).exists( false ),
            Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ),
            Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( false ),
            Criteria.where( CommonConstants.MOOD_COLUMN ).is( CommonConstants.SURVEY_MOOD_GREAT ) ) );

        List<SurveyDetails> surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        LOG.debug(
            "Method to get list of customers who have not yet completed their survey on all the social networking sites, getIncompleteSocialPostCustomersEmail() finished." );
        return surveys;
    }


    @Override
    public void changeStatusOfSurvey( String surveyId, boolean editable )
    {
        LOG.debug( "Method to update status of survey in SurveyDetails collection, changeStatusOfSurvey() started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyId ) );
        Update update = new Update();
        update.set( CommonConstants.EDITABLE_SURVEY_COLUMN, editable );
        update.set( CommonConstants.STAGE_COLUMN, 0 );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method to update status of survey in SurveyDetails collection, changeStatusOfSurvey() finished." );
    }


    @Override
    public void getAverageScore( Date startDate, Date endDate, Map<Long, AgentRankingReport> agentReportData, String columnName,
        long columnValue, boolean fetchAbusive )
    {

        TypedAggregation<SurveyDetails> aggregation;
        if ( startDate != null && endDate != null ) {
            if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) ),
                    Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ),
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ),
                    Aggregation.match( Criteria.where( CommonConstants.CREATED_ON ).lte( endDate ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN ).nin(
                        Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ),
                    Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).avg( CommonConstants.SCORE_COLUMN ).as( "score" ) );
            } else {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ),
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ),
                    Aggregation.match( Criteria.where( CommonConstants.CREATED_ON ).lte( endDate ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN ).nin(
                        Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ),
                    Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).avg( CommonConstants.SCORE_COLUMN ).as( "score" ) );
            }
        } else if ( startDate != null && endDate == null ) {
            if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) ),
                    Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ),
                    Aggregation.match( Criteria.where( CommonConstants.CREATED_ON ).gte( startDate ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN ).nin(
                        Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ),
                    Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).avg( CommonConstants.SCORE_COLUMN ).as( "score" ) );
            } else {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ),
                    Aggregation.match( Criteria.where( CommonConstants.CREATED_ON ).gte( startDate ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN ).nin(
                        Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ),
                    Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).avg( CommonConstants.SCORE_COLUMN ).as( "score" ) );
            }
        }

        else if ( startDate == null && endDate != null ) {
            if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) ),
                    Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ),
                    Aggregation.match( Criteria.where( CommonConstants.CREATED_ON ).lte( endDate ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN ).nin(
                        Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ),
                    Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).avg( CommonConstants.SCORE_COLUMN ).as( "score" ) );
            } else {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ),
                    Aggregation.match( Criteria.where( CommonConstants.CREATED_ON ).lte( endDate ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN ).nin(
                        Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ),
                    Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).avg( CommonConstants.SCORE_COLUMN ).as( "score" ) );
            }
        } else {
            if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) ),
                    Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN ).nin(
                        Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ),
                    Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).avg( CommonConstants.SCORE_COLUMN ).as( "score" ) );
            } else {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN ).nin(
                        Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ),
                    Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).avg( CommonConstants.SCORE_COLUMN ).as( "score" ) );
            }
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
            if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) ),
                    Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ),
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ),
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN ).nin(
                        Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ),
                    Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );
            } else {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ),
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ),
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN ).nin(
                        Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ),
                    Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );
            }
        } else if ( startDate != null && endDate == null ) {
            if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) ),
                    Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ),
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN ).nin(
                        Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ),
                    Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );
            } else {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ),
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).gte( startDate ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN ).nin(
                        Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ),
                    Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );
            }
        } else if ( startDate == null && endDate != null ) {
            if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) ),
                    Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ),
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN ).nin(
                        Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ),
                    Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );
            } else {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ),
                    Aggregation.match( Criteria.where( CommonConstants.MODIFIED_ON_COLUMN ).lte( endDate ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN ).nin(
                        Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ),
                    Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );
            }
        } else {
            if ( considerOnlyLatestSurveys.equalsIgnoreCase( CommonConstants.YES_STRING ) ) {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN ).is( true ) ),
                    Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN ).nin(
                        Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ),
                    Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );
            } else {
                aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
                    Aggregation.match( Criteria.where( columnName ).is( columnValue ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
                    Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ),
                    Aggregation
                        .match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
                    Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN ).nin(
                        Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ),
                    Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );
            }
        }

        LOG.info( "aggregation to get complete survey count for user ranking report is : " + aggregation );
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
        LOG.debug( "Method insertSurveyDetails() to insert details of survey finished." );
        return surveys.get( CommonConstants.INITIAL_INDEX );
    }


    // Commented as Zillow surveys are not stored in database, SS-1276
    //    @Override
    //    public void removeZillowSurveysByEntity( String entityType, long entityId )
    //    {
    //        LOG.debug( "Method removeZillowSurveysByEntity() started" );
    //        Query query = new Query( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).is(
    //            CommonConstants.SURVEY_SOURCE_ZILLOW ) );
    //        query.addCriteria( Criteria.where( entityType ).is( entityId ) );
    //        mongoTemplate.remove( query, SURVEY_DETAILS_COLLECTION );
    //        LOG.debug( "Method removeZillowSurveysByEntity() finished" );
    //    }

    // Commented as Zillow surveys are not stored in database, SS-1276
    //    @Override
    //    public void removeExcessZillowSurveysByEntity( String entityType, long entityId )
    //    {
    //        LOG.debug( "Method removeExcessZillowSurveysByEntity() started" );
    //
    //        Query query = new Query( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).is(
    //            CommonConstants.SURVEY_SOURCE_ZILLOW ) );
    //        query.addCriteria( Criteria.where( entityType ).is( entityId ) );
    //        query.with( new Sort( Sort.Direction.ASC, "createdOn" ) );
    //        List<DBObject> surveys = mongoTemplate.find( query, DBObject.class, SURVEY_DETAILS_COLLECTION );
    //        int count = surveys.size();
    //        if ( count > 10 ) {
    //            int noOfSurveysToRemove = count - 10;
    //            for ( int i = 0; i < noOfSurveysToRemove; i++ ) {
    //                mongoTemplate.remove( surveys.get( i ), SURVEY_DETAILS_COLLECTION );
    //            }
    //        }
    //        LOG.debug( "Method removeExcessZillowSurveysByEntity() finished" );
    //    }


    @Override
    public void removeExistingZillowSurveysByEntity( String entityType, long entityId )
    {
        LOG.debug( "Method removeExistingZillowSurveysByEntity() started" );

        Query query = new Query(
            Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).is( CommonConstants.SURVEY_SOURCE_ZILLOW ) );
        query.addCriteria( Criteria.where( entityType ).is( entityId ) );
        if ( entityType.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) ) {
            query.addCriteria( Criteria.where( CommonConstants.REGION_ID_COLUMN ).is( 0 ) );
            query.addCriteria( Criteria.where( CommonConstants.BRANCH_ID_COLUMN ).is( 0 ) );
            query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( 0 ) );
        } else if ( entityType.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN ) ) {
            query.addCriteria( Criteria.where( CommonConstants.BRANCH_ID_COLUMN ).is( 0 ) );
            query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( 0 ) );
        } else if ( entityType.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN ) ) {
            query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( 0 ) );
        }
        query.with( new Sort( Sort.Direction.ASC, "createdOn" ) );
        mongoTemplate.remove( query, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method removeExistingZillowSurveysByEntity() finished" );
    }


    @Override
    public long getSurveysReporetedAsAbusiveCount()
    {
        LOG.debug( "Method getSurveysReporetedAsAbusiveCount() to get count of surveys marked as abusive started." );
        long count = mongoTemplate.getCollection( ABS_REPORTER_DETAILS_COLLECTION ).getCount();
        LOG.debug( "Method getSurveysReporetedAsAbusiveCount() to get count of surveys marked as abusive finished." );
        return count;
    }


    @Override
    public List<AbusiveSurveyReportWrapper> getSurveysReporetedAsAbusive( int start, int rows )
    {
        LOG.debug( "Method getSurveysReporetedAsAbusive() to retrieve surveys marked as abusive started." );
        Query query = new Query( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( true ) );
        query.with( new Sort( Sort.Direction.DESC, CommonConstants.SURVEY_LAST_ABUSE_REPORTED_DATE ) );
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
        abuseReportersForApp.add( new ReporterDetail( CommonConstants.REPORT_ABUSE_BY_APPLICATION_NAME,
            CommonConstants.REPORT_ABUSE_BY_APPLICATION_EMAIL, CommonConstants.REPORT_ABUSE_BY_APPLICATION_REASON ) );
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

        LOG.debug( "Method getSurveysReporetedAsAbusive() to retrieve surveys marked as abusive finished." );
        return abusiveSurveyReports;
    }


    @Override
    public int fetchZillowCallCount()
    {
        LOG.debug( "Method fetchZillowCallCount() started" );
        Query query = new Query();
        int count = 0;
        DBObject zillowCallCount = mongoTemplate.findOne( query, DBObject.class, ZILLOW_CALL_COUNT );
        if ( zillowCallCount == null ) {
            LOG.debug( "Count not found. Setting count to 0" );
        } else {
            count = (int) zillowCallCount.get( "count" );
            LOG.debug( "Count value : " + count );
        }
        LOG.debug( "Method fetchZillowCallCount() finished" );
        return count;
    }


    @Override
    public void resetZillowCallCount()
    {
        LOG.debug( "Method resetZillowCallCount() started" );
        Query query = new Query();
        LOG.debug( "Resetting count value" );
        Update update = new Update().set( "count", 0 );
        mongoTemplate.upsert( query, update, ZILLOW_CALL_COUNT );
        LOG.debug( "Method resetZillowCallCount() finished" );
    }


    @Override
    public void updateZillowCallCount()
    {
        LOG.debug( "Method updateZillowCallCount() started" );
        int count = 0;
        LOG.debug( "Fetching the latest value of count." );
        Query query = new Query();
        count = fetchZillowCallCount() + 1;
        LOG.debug( "Updating count value" );
        Update update = new Update().set( "count", count );
        mongoTemplate.upsert( query, update, ZILLOW_CALL_COUNT );
        LOG.debug( "Method updateZillowCallCount() finished" );
    }


    @Override
    public void updateSurveyDetails( SurveyDetails surveyDetails )
    {
        LOG.debug( "Method insertSurveyDetails() to insert details of survey started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( surveyDetails.getAgentId() ) );
        if ( surveyDetails.getSource().equalsIgnoreCase( CommonConstants.SURVEY_SOURCE_ZILLOW ) ) {
            query.addCriteria( Criteria.where( CommonConstants.SURVEY_SOURCE_ID_COLUMN ).is( surveyDetails.getSourceId() ) );
        } else {
            query.addCriteria( Criteria.where( CommonConstants.CUSTOMER_EMAIL_COLUMN ).is( surveyDetails.getCustomerEmail() ) );
        }
        Update update = new Update();
        update.set( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN, surveyDetails.getSocialMediaPostDetails() );
        update.set( CommonConstants.SOCIAL_MEDIA_POST_RESPONSE_DETAILS_COLUMN,
            surveyDetails.getSocialMediaPostResponseDetails() );
        update.set( CommonConstants.SURVEY_PREINITIATION_ID_COLUMN, surveyDetails.getSurveyPreIntitiationId() );
        update.set( CommonConstants.RETAKE_SURVEY_COLUMN, surveyDetails.isRetakeSurvey() );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        update.set( CommonConstants.COMPANY_ID_COLUMN, surveyDetails.getCompanyId() );
        update.set( CommonConstants.REGION_ID_COLUMN, surveyDetails.getRegionId() );
        update.set( CommonConstants.BRANCH_ID_COLUMN, surveyDetails.getBranchId() );
        update.set( CommonConstants.AGENT_ID_COLUMN, surveyDetails.getAgentId() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method insertSurveyDetails() to insert details of survey finished." );
    }


    @Override
    public void updateSurveyDetailsBySurveyId( SurveyDetails surveyDetails )
    {
        LOG.debug( "Method insertSurveyDetails() to insert details of survey started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyDetails.get_id() ) );

        Update update = new Update();
        update.set( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN, surveyDetails.getSocialMediaPostDetails() );
        update.set( CommonConstants.SOCIAL_MEDIA_POST_RESPONSE_DETAILS_COLUMN,
            surveyDetails.getSocialMediaPostResponseDetails() );
        update.set( CommonConstants.SURVEY_PREINITIATION_ID_COLUMN, surveyDetails.getSurveyPreIntitiationId() );
        update.set( CommonConstants.RETAKE_SURVEY_COLUMN, surveyDetails.isRetakeSurvey() );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method insertSurveyDetails() to insert details of survey finished." );
    }


    public void updateSurveyAsUnderResolution( String surveyId )
    {
        LOG.debug( "Method updateSurveyAsUnderResolution() to mark survey as under resolution started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyId ) );
        Update update = new Update();
        update.set( CommonConstants.ABUSIVE_NOTIFY_COLUMN, true );
        mongoTemplate.upsert( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method updateSurveyAsUnderResolution() to mark survey as under resolution finished." );
    }


    @Override
    public List<AbusiveSurveyReportWrapper> getSurveysReporetedAsAbusive( long companyId, int start, int rows )
    {
        LOG.debug( "Method getSurveysReporetedAsAbusive() to retrieve surveys marked as abusive for a company started." );
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

        LOG.debug( "Method getSurveysReporetedAsAbusive() to retrieve surveys marked as abusive for a company finished." );
        return abusiveSurveyReports;
    }


    @Override
    public List<SurveyDetails> getSurveyDetailsByAgentAndCompany( long companyId )
    {
        LOG.debug( "Method getSurveyDetailsByAgentAndCompany() to insert details of survey started." );
        List<SurveyDetails> surveys = new ArrayList<SurveyDetails>();
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( companyId ) );

        surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        return surveys;
    }


    @Override
    public List<SurveyDetails> getSurveyDetailsForUser( long userId )
    {
        LOG.debug( "Method getSurveyDetailsByAgentAndCompany() to insert details of survey started." );
        List<SurveyDetails> surveys = new ArrayList<SurveyDetails>();
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID ).is( userId ) );

        surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        return surveys;
    }


    @Override
    public List<SurveyDetails> getSurveyDetailsForRegionOnly( long regionId )
    {
        LOG.debug( "Method getSurveyDetailsForRegionOnly() to insert details of survey started." );
        List<SurveyDetails> surveys = new ArrayList<SurveyDetails>();
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.REGION_ID_COLUMN ).is( regionId )
            .andOperator( Criteria.where( CommonConstants.BRANCH_ID_COLUMN ).lte( 0 ) )
            .andOperator( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).lte( 0 ) ) );

        surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        return surveys;
    }


    @Override
    public List<SurveyDetails> getSurveyDetailsForBranchOnly( long branchId )
    {
        LOG.debug( "Method getSurveyDetailsForBranchOnly() to insert details of survey started." );
        List<SurveyDetails> surveys = new ArrayList<SurveyDetails>();
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.BRANCH_ID_COLUMN ).is( branchId )
            .andOperator( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).lte( 0 ) ) );

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
        LOG.debug(
            "Method getSurveysUnderResolutionCount() to get count of surveys marked as under resolution for a company started." );
        Query query = new Query( Criteria.where( CommonConstants.UNDER_RESOLUTION_COLUMN ).is( true ) );
        query.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( companyId ) );
        long count = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION ).size();
        LOG.debug(
            "Method getSurveysUnderResolutionCount() to get count of surveys marked as under resolution for a company finished." );
        return count;

    }


    @Override
    public long getSurveysReporetedAsAbusiveCount( long companyId )
    {
        LOG.debug(
            "Method getSurveysReporetedAsAbusiveCount() to get count of surveys marked as abusive for a company started." );
        Query query = new Query( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( true ) );
        query.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( companyId ) );
        long count = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION ).size();
        LOG.debug(
            "Method getSurveysUnderResolutionCount() to get count of surveys marked as abusive for a company finished." );
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
        LOG.debug( "Method to get getSurveyCountForAgents called" );
        TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
            Aggregation.match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
            Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( fetchAbusive ) ),
            Aggregation.match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).in( agentIdList ) ),
            Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );


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


    /**
     * Method to remove surveys from mongo by SurveyPreInitiation
     * @param surveys
     */
    @Override
    public void deleteSurveysBySurveyPreInitiation( List<SurveyPreInitiation> surveys )
    {
        LOG.debug( "Method deleteSurveysBySurveyPreInitiation() to delete surveys started." );
        if ( surveys == null || surveys.isEmpty() ) {
            LOG.debug( "No surveys present." );
            return;
        }
        Map<Long, List<String>> surveysToDelete = new HashMap<Long, List<String>>();

        for ( SurveyPreInitiation survey : surveys ) {
            List<String> mailIds = null;
            //Check if agentId exists in surveysToDelete
            if ( surveysToDelete.containsKey( survey.getAgentId() ) ) {
                //Get the current survey's agent ID
                mailIds = surveysToDelete.get( survey.getAgentId() );
            }
            if ( mailIds == null ) {
                mailIds = new ArrayList<String>();
            }
            mailIds.add( survey.getCustomerEmailId() );
            surveysToDelete.put( survey.getAgentId(), mailIds );
        }

        //Ensure that the survey is editable
        Criteria criteria = Criteria.where( CommonConstants.STAGE_COLUMN ).ne( CommonConstants.SURVEY_STAGE_COMPLETE );
        //Add criteria for each of the agentId - customerEmailId pairs
        List<Criteria> criterias = new ArrayList<Criteria>();
        for ( Long agentId : surveysToDelete.keySet() ) {
            Criteria criterion = Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId )
                .and( CommonConstants.CUSTOMER_EMAIL_COLUMN ).in( surveysToDelete.get( agentId ) );
            criterias.add( criterion );
        }
        Criteria surveyCriteria = new Criteria().orOperator( criterias.toArray( new Criteria[criterias.size()] ) );
        Query query = new Query( new Criteria().andOperator( criteria, surveyCriteria ) );

        mongoTemplate.remove( query, SURVEY_DETAILS_COLLECTION );

        LOG.debug( "Method deleteSurveysBySurveyPreInitiation() to delete surveys finished." );
    }


    /**
     * Method to delete incomplete surveys for a particular agent ID from Survey Details Collection
     * @param agentId
     * @throws InvalidInputException 
     */
    @Override
    public void deleteIncompleteSurveysForAgent( long agentId ) throws InvalidInputException
    {
        LOG.debug( "Method to delete incomplete surveys for agent ID : " + agentId + " started." );
        //Check if agentId is valid
        if ( agentId <= 0l ) {
            throw new InvalidInputException( "Invalid agent ID : " + agentId );
        }
        //Ensure that the survey is editable
        Criteria criteria = Criteria.where( CommonConstants.STAGE_COLUMN ).ne( CommonConstants.SURVEY_STAGE_COMPLETE );
        criteria.and( CommonConstants.AGENT_ID_COLUMN ).is( agentId );
        Query query = new Query( criteria );
        mongoTemplate.remove( query, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method to delete incomplete surveys for agent ID : " + agentId + " finished." );
    }


    /**
     * Method to update agent info when surveys moved from one user to another
     * @throws InvalidInputException
     * */
    @Override
    public void updateAgentInfoInSurveys( long fromUserId, User toUser, UserProfile toUserProfile ) throws InvalidInputException
    {
        if ( fromUserId <= 0l )
            throw new InvalidInputException( "Invalid fromUserId passed in updateAgentIdOfSurveys()" );
        if ( toUser == null )
            throw new InvalidInputException( "toUser passed cannot be null in updateAgentIdOfSurveys()" );
        if ( toUserProfile == null )
            throw new InvalidInputException( "toUser's user profile passed cannot be null in updateAgentIdOfSurveys()" );

        LOG.debug( "Method updateAgentIdOfSurveys() to update agent ids when survey moved from one to another user started." );
        //updating social media post details
        updateSocialMediaPostDetails( CommonConstants.AGENT_ID_COLUMN, fromUserId, toUserProfile );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( fromUserId ) );
        Update update = new Update();
        update.set( CommonConstants.AGENT_ID_COLUMN, toUser.getUserId() );
        update.set( CommonConstants.AGENT_NAME_COLUMN,
            toUser.getFirstName() + ( toUser.getLastName() == null ? "" : " " + toUser.getLastName() ) );
        update.set( CommonConstants.REGION_ID_COLUMN, toUserProfile.getRegionId() );
        update.set( CommonConstants.BRANCH_ID_COLUMN, toUserProfile.getBranchId() );
        update.set( CommonConstants.COMPANY_ID_COLUMN, toUserProfile.getCompany().getCompanyId() );
        
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method updateAgentIdOfSurveys() to update agent ids when survey moved from one to another user finished." );

    }
    
    @Override
    public void updateAgentInfoInSurveyBySPI( long surveyPreInitiationId, User toUser, UserProfile toUserProfile ) throws InvalidInputException
    {
        if ( surveyPreInitiationId <= 0l )
            throw new InvalidInputException( "Invalid surveyPreInitiationId passed in updateAgentInfoInSurveyBySPI()" );
        if ( toUser == null )
            throw new InvalidInputException( "toUser passed cannot be null in updateAgentInfoInSurveyBySPI()" );
        if ( toUserProfile == null )
            throw new InvalidInputException( "toUser's user profile passed cannot be null in updateAgentInfoInSurveyBySPI()" );

        LOG.debug( "Method updateAgentInfoInSurveyBySPI() to update agent id when survey moved from one to another user started." );
        //updating social media post details
        updateSocialMediaPostDetails( CommonConstants.SURVEY_PREINITIATION_ID_COLUMN,surveyPreInitiationId, toUserProfile );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.SURVEY_PREINITIATION_ID_COLUMN ).is( surveyPreInitiationId ) );
        Update update = new Update();
        update.set( CommonConstants.AGENT_ID_COLUMN, toUser.getUserId() );
        update.set( CommonConstants.AGENT_NAME_COLUMN,
            toUser.getFirstName() + ( toUser.getLastName() == null ? "" : " " + toUser.getLastName() ) );
        update.set( CommonConstants.REGION_ID_COLUMN, toUserProfile.getRegionId() );
        update.set( CommonConstants.BRANCH_ID_COLUMN, toUserProfile.getBranchId() );
        update.set( CommonConstants.COMPANY_ID_COLUMN, toUserProfile.getCompany().getCompanyId() );
        
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );

        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method updateAgentIdOfSurveys() to update agent ids when survey moved from one to another user finished." );

    }


    private void updateSocialMediaPostDetails( String fieldName, long keyId, UserProfile toUserProfile )
    {

        //update agent media post details
        Query query = new Query();
        Update update = new Update();
        query.addCriteria( Criteria.where( fieldName ).is( keyId ) );
        query.addCriteria( Criteria.where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN ).exists( true ) );
        query.addCriteria( Criteria
            .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.AGENT_MEDIA_POST_DETAILS_COLUMN )
            .exists( true ) );
        query
            .addCriteria(
                Criteria
                    .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
                        + CommonConstants.AGENT_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.AGENT_ID_COLUMN )
                    .exists( true ) );
        update.set( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.AGENT_MEDIA_POST_DETAILS_COLUMN
            + "." + CommonConstants.AGENT_ID_COLUMN, toUserProfile.getAgentId() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );

        //update branch media post details
        query = new Query();
        update = new Update();
        query.addCriteria( Criteria.where( fieldName ).is( keyId ) );
        query.addCriteria( Criteria.where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN ).exists( true ) );
        query
            .addCriteria( Criteria
                .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
                    + CommonConstants.BRANCH_MEDIA_POST_DETAILS_COLUMN + ".0." + CommonConstants.BRANCH_ID_COLUMN )
                .exists( true ) );
        update.set( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.BRANCH_MEDIA_POST_DETAILS_COLUMN
            + ".0." + CommonConstants.BRANCH_ID_COLUMN, toUserProfile.getBranchId() );
        update.set( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.BRANCH_MEDIA_POST_DETAILS_COLUMN
            + ".0." + CommonConstants.REGION_ID_COLUMN, toUserProfile.getRegionId() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );

        //update region media post details
        query = new Query();
        update = new Update();
        query.addCriteria( Criteria.where( fieldName ).is( keyId ) );
        query.addCriteria( Criteria.where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN ).exists( true ) );
        query
            .addCriteria( Criteria
                .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
                    + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN + ".0." + CommonConstants.REGION_ID_COLUMN )
                .exists( true ) );
        update.set( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN
            + ".0." + CommonConstants.REGION_ID_COLUMN, toUserProfile.getRegionId() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );


        //update company media post details
        query = new Query();
        update = new Update();
        query.addCriteria( Criteria.where( fieldName ).is( keyId ) );
        query.addCriteria( Criteria.where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN ).exists( true ) );
        query
            .addCriteria( Criteria
                .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
                    + CommonConstants.COMPANY_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.COMPANY_ID_COLUMN )
                .exists( true ) );
        update.set( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.COMPANY_MEDIA_POST_DETAILS_COLUMN
            + "." + CommonConstants.COMPANY_ID_COLUMN, toUserProfile.getCompany().getCompanyId() );

        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );

    }


    /**
     * Method to get Zillow reviews based on queries
     * @param queries
     * @throws InvalidInputException
     * */
    @Override
    public SurveyDetails getZillowReviewByQueryMap( Map<String, Object> queries ) throws InvalidInputException
    {
        if ( queries == null || queries.isEmpty() ) {
            throw new InvalidInputException(
                "queries passed cannot be null or empty in getZillowReviewByEntityAndReviewUrl()" );
        }
        LOG.debug( "Method getZillowReviewByEntityAndReviewUrl() to find Zillow reviews based on queries started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).is( CommonConstants.SURVEY_SOURCE_ZILLOW ) );
        for ( String columnName : queries.keySet() ) {
            if ( columnName.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) ) {
                query.addCriteria( Criteria.where( CommonConstants.REGION_ID_COLUMN ).is( 0 ) );
                query.addCriteria( Criteria.where( CommonConstants.BRANCH_ID_COLUMN ).is( 0 ) );
                query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( 0 ) );
                query.addCriteria( Criteria.where( columnName ).is( queries.get( columnName ) ) );
            } else if ( columnName.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN ) ) {
                query.addCriteria( Criteria.where( CommonConstants.BRANCH_ID_COLUMN ).is( 0 ) );
                query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( 0 ) );
                query.addCriteria( Criteria.where( columnName ).is( queries.get( columnName ) ) );
            } else if ( columnName.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN ) ) {
                query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( 0 ) );
                query.addCriteria( Criteria.where( columnName ).is( queries.get( columnName ) ) );
            } else
                query.addCriteria( Criteria.where( columnName ).is( queries.get( columnName ) ) );
        }
        List<SurveyDetails> surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method getZillowReviewByEntityAndReviewUrl() to find Zillow reviews based on queries finished." );
        if ( surveys == null || surveys.isEmpty() ) {
            return null;
        } else {
            return surveys.get( CommonConstants.INITIAL_INDEX );
        }
    }


    /**
     * Method to reset the showSurveyOnUI for Zillow Reviews stored in SURVEY_DETAILS_COLLECTION for those not part of id list passed in argument
     * @param columnName
     * @param id
     * @param latestSurveyIdList
     * */
    @Override
    public void resetShowSurveyOnUIPropertyForNonLatestReviews( String columnName, long id, List<String> latestSurveyIdList )
    {
        if ( latestSurveyIdList == null || latestSurveyIdList.isEmpty() ) {
            LOG.error( "latestSurveyIdList passed is null or empty" );
            return;
        }
        LOG.debug(
            "Method resetShowSurveyOnUIPropertyForNonLatestReviews() to reset the showSurveyOnUI for Zillow Reviews started." );
        Query query = new Query();
        Query latestSurveysQuery = new Query();
        query.addCriteria( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).is( CommonConstants.SURVEY_SOURCE_ZILLOW ) );
        latestSurveysQuery
            .addCriteria( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).is( CommonConstants.SURVEY_SOURCE_ZILLOW ) );
        if ( columnName.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) ) {
            query.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( id ) );
            query.addCriteria( Criteria.where( CommonConstants.REGION_ID_COLUMN ).is( 0 ) );
            query.addCriteria( Criteria.where( CommonConstants.BRANCH_ID_COLUMN ).is( 0 ) );
            query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( 0 ) );

            latestSurveysQuery.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( id ) );
            latestSurveysQuery.addCriteria( Criteria.where( CommonConstants.REGION_ID_COLUMN ).is( 0 ) );
            latestSurveysQuery.addCriteria( Criteria.where( CommonConstants.BRANCH_ID_COLUMN ).is( 0 ) );
            latestSurveysQuery.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( 0 ) );
        } else if ( columnName.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN ) ) {
            query.addCriteria( Criteria.where( CommonConstants.REGION_ID_COLUMN ).is( id ) );
            query.addCriteria( Criteria.where( CommonConstants.BRANCH_ID_COLUMN ).is( 0 ) );
            query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( 0 ) );

            latestSurveysQuery.addCriteria( Criteria.where( CommonConstants.REGION_ID_COLUMN ).is( id ) );
            latestSurveysQuery.addCriteria( Criteria.where( CommonConstants.BRANCH_ID_COLUMN ).is( 0 ) );
            latestSurveysQuery.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( 0 ) );
        } else if ( columnName.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN ) ) {
            query.addCriteria( Criteria.where( CommonConstants.BRANCH_ID_COLUMN ).is( id ) );
            query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( 0 ) );

            latestSurveysQuery.addCriteria( Criteria.where( CommonConstants.BRANCH_ID_COLUMN ).is( id ) );
            latestSurveysQuery.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( 0 ) );
        } else if ( columnName.equalsIgnoreCase( CommonConstants.AGENT_ID_COLUMN ) ) {
            query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( id ) );

            latestSurveysQuery.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( id ) );
        } else {
            LOG.error( columnName + " is an unknown name" );
            return;
        }
        List<ObjectId> objectIdList = new ArrayList<ObjectId>();
        for ( String latestSurveyId : latestSurveyIdList ) {
            ObjectId objID = new ObjectId( latestSurveyId );
            objectIdList.add( objID );
        }

        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).not().in( objectIdList ) );
        latestSurveysQuery.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).in( objectIdList ) );
        List<SurveyDetails> surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );
        List<SurveyDetails> latestSurveys = mongoTemplate.find( latestSurveysQuery, SurveyDetails.class,
            SURVEY_DETAILS_COLLECTION );

        if ( surveys != null && !surveys.isEmpty() ) {
            for ( SurveyDetails surveyDetails : surveys ) {

                // Perform update of showSurveyOnUI property for this survey
                Query updateQuery = new Query();
                updateQuery.addCriteria(
                    Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( new ObjectId( surveyDetails.get_id() ) ) );

                Update update = new Update();
                update.set( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN, false );

                mongoTemplate.updateFirst( updateQuery, update, SURVEY_DETAILS_COLLECTION );
            }
        }

        if ( latestSurveys != null && !latestSurveys.isEmpty() ) {
            for ( SurveyDetails surveyDetails : latestSurveys ) {

                // Perform update of showSurveyOnUI property for this survey
                Query updateQuery = new Query();
                updateQuery.addCriteria(
                    Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( new ObjectId( surveyDetails.get_id() ) ) );

                Update update = new Update();
                update.set( CommonConstants.SHOW_SURVEY_ON_UI_COLUMN, true );

                mongoTemplate.updateFirst( updateQuery, update, SURVEY_DETAILS_COLLECTION );
            }
        }
        LOG.debug(
            "Method resetShowSurveyOnUIPropertyForNonLatestReviews() to reset the showSurveyOnUI for Zillow Reviews finished." );
    }


    @Override
    public void updateZillowSummaryInExistingSurveyDetails( SurveyDetails surveyDetails )
    {
        String surveyMongoId = surveyDetails.get_id();
        LOG.debug( "Method updateZillowSummaryInExistingSurveyDetails() to update summary and description for ." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyMongoId ) );
        Update update = new Update();
        update.set( CommonConstants.SUMMARY_COLUMN, surveyDetails.getSummary() );
        update.set( CommonConstants.REVIEW_COLUMN, surveyDetails.getReview() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method updateSurveyAsAbusive() to mark survey as abusive finished." );
    }


    @Override
    public void updateZillowSourceIdInExistingSurveyDetails( SurveyDetails surveyDetails )
    {
        String surveyMongoId = surveyDetails.get_id();
        LOG.info( "Method updateZillowSourceIdInExistingSurveyDetails() to update source id started " );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyMongoId ) );
        Update update = new Update();
        update.set( CommonConstants.SURVEY_SOURCE_ID_COLUMN, surveyDetails.getSourceId() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method updateZillowSourceIdInExistingSurveyDetails finished." );
    }


    @Override
    public void updateZillowSurveyUpdatedDateInExistingSurveyDetails( SurveyDetails surveyDetails )
    {
        String surveyMongoId = surveyDetails.get_id();
        LOG.info( "Method updateZillowSurveyUpdatedDateInExistingSurveyDetails() to update source id started " );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyMongoId ) );
        Update update = new Update();
        update.set( CommonConstants.SURVEY_UPDATED_DATE_COLUMN, surveyDetails.getSurveyUpdatedDate() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method updateZillowSurveyUpdatedDateInExistingSurveyDetails finished." );
    }


    /**
     * 
     * @param companyId
     * @return
     */
    @Override
    public Map<Long, Date> getLatestCompletedSurveyDateForAgents( long companyId )
    {

        LOG.debug( "Method getLatestCompletedSurveyDateForAgents started." );

        TypedAggregation<SurveyDetails> aggregation;
        aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
            Aggregation.match( Criteria.where( CommonConstants.COMPANY_ID ).is( companyId ) ),
            Aggregation.match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
            Aggregation.group( CommonConstants.AGENT_ID ).first( CommonConstants.MODIFIED_ON_COLUMN )
                .as( "lastModifiedDate" ) );

        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );

        Map<Long, Date> agentLastCompltedSurveyDate = new HashMap<Long, Date>();

        if ( result != null && result.getRawResults() != null ) {
            @SuppressWarnings ( "unchecked") List<DBObject> basicDBObject = (List<DBObject>) result.getRawResults()
                .get( "result" );

            for ( DBObject row : basicDBObject ) {
                Date lastDate = (Date) row.get( "lastModifiedDate" );
                Long agentId = (Long) row.get( CommonConstants.DEFAULT_MONGO_ID_COLUMN );
                agentLastCompltedSurveyDate.put( agentId, lastDate );
            }
        }

        LOG.debug( "Method getLatestCompletedSurveyDateForAgents ended." );
        return agentLastCompltedSurveyDate;
    }


    @Override
    public void updateModifiedDateForSurvey( String surveyId, Date date )
    {
        LOG.debug( "Method updateModifiedDateForSurvey() started" );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyId ) );
        Update update = new Update();
        update.set( CommonConstants.MODIFIED_ON_COLUMN, date );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method updateModifiedDateForSurvey() finished" );
    }


    /*
     * Method to fetch survey details on the basis of agentId and customer email.
     */
    @Override
    public List<SurveyDetails> getFilteredSurveys( int start, int batchSize, long companyId, String mood,
        Long startSurveyID, Date startReviewDate, Date startTransactionDate, List<Long> userIds, boolean isRetaken )
    {
        LOG.debug( "Method getSurveyByStartIndexAndStatus() started." );

        Query query = new Query();
        //add company id criteria
        query.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( companyId ) );

        //exclude abusive surveys
        query.addCriteria( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( false ) );

        // add status criteria
        query.addCriteria( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) );
        

        //add mood criteria
        if ( !StringUtils.isEmpty( mood ) ) {
            query.addCriteria( Criteria.where( CommonConstants.MOOD_COLUMN ).is( mood ) );
        }

        //add survey id criteria
        if ( startSurveyID != null && startSurveyID > 0l )
            query.addCriteria( Criteria.where( CommonConstants.SURVEY_PREINITIATION_ID_COLUMN ).gte( startSurveyID ) );


        // review date criteria
        if ( startReviewDate != null )
            query.addCriteria( Criteria.where( CommonConstants.SURVEY_COMPLETED_DATE_COLUMN ).gte( startReviewDate ) );

        // transaction date criteria
        if ( startTransactionDate != null )
            query.addCriteria( Criteria.where( CommonConstants.SURVEY_TRANSACTION_DATE_COLUMN ).gte( startTransactionDate ) );

        //user id criteria
        if ( userIds != null )
            query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).in( userIds ) );
        
        //add retake survey acriteria
        if ( isRetaken )
            query.addCriteria( Criteria.where( CommonConstants.RETAKE_SURVEY_COLUMN ).is( true ) );

        //get the oldest record
        query.with( new Sort( Sort.Direction.DESC, CommonConstants.MODIFIED_ON_COLUMN ) );

        if ( start > 0 )
            query.skip( start );

        if ( batchSize > 0 )
            query.limit( batchSize );

        List<SurveyDetails> surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );

        LOG.debug( "Method getSurveyByStartIndexAndStage() " );
        return surveys;
    }


    @Override
    public Long getFilteredSurveyCount( long companyId, String mood, Long startSurveyID, Date startReviewDate,
        Date startTransactionDate, List<Long> userIds, boolean isRetaken )
    {
        LOG.debug( "Method to get count of total number of surveys completed so far, getCompletedSurveyCount() started." );

        Query query = new Query();
        //add company id criteria
        query.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( companyId ) );
        
        //exclude abusive surveys
        query.addCriteria( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( false ) );


        // add status criteria
        query.addCriteria( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) );
        

        //add mood criteria
        if ( !StringUtils.isEmpty( mood ) ) {
            query.addCriteria( Criteria.where( CommonConstants.MOOD_COLUMN ).is( mood ) );
        }

        //add survey id criteria
        if ( startSurveyID != null && startSurveyID > 0l )
            query.addCriteria( Criteria.where( CommonConstants.SURVEY_PREINITIATION_ID_COLUMN ).gte( startSurveyID ) );

        // transaction date criteria
        if ( startTransactionDate != null )
            query.addCriteria( Criteria.where( CommonConstants.SURVEY_TRANSACTION_DATE_COLUMN ).gte( startTransactionDate ) );

        // review date criteria
        if ( startReviewDate != null )
            query.addCriteria( Criteria.where( CommonConstants.SURVEY_COMPLETED_DATE_COLUMN ).gte( startReviewDate ) );

        //user id criteria
        if ( userIds != null )
            query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).in( userIds ) );

        //add retake survey acriteria
        if ( isRetaken )
            query.addCriteria( Criteria.where( CommonConstants.RETAKE_SURVEY_COLUMN ).is( true ) );
        
        LOG.debug( "Method to get count of total number of surveys completed so far, getCompletedSurveyCount() finished." );
        return mongoTemplate.count( query, SURVEY_DETAILS_COLLECTION );

    }

    
    @SuppressWarnings("unchecked")
	@Override
    public Float getFilteredSurveyAvgScore( long companyId,  String mood, Long startSurveyID, Date startReviewDate,
        Date startTransactionDate, List<Long> userIds, boolean isRetaken )
    {
        LOG.debug( "Method to avg score of surveys, getFilteredSurveyAvgScore() started." );

        
        BasicDBList pipeline = new BasicDBList();
        //match for non abusive reviews
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.COMPANY_ID_COLUMN , companyId ) ) );
        
        //exclude abusive surveys
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.IS_ABUSIVE_COLUMN , false ) ) );

        // add status criteria
        pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.STAGE_COLUMN ,  CommonConstants.SURVEY_STAGE_COMPLETE  ) ) );
        
        //add mood criteria
        if ( !StringUtils.isEmpty( mood ) ) {
            pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.MOOD_COLUMN , mood ) ) );
        }

        //add survey id criteria
        if ( startSurveyID != null && startSurveyID > 0l )
            pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.SURVEY_PREINITIATION_ID_COLUMN,  new BasicDBObject( "$gte" , startSurveyID ) )) );

        // transaction date criteria
        if ( startTransactionDate != null )
            pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.SURVEY_TRANSACTION_DATE_COLUMN,  new BasicDBObject( "$gte" , startTransactionDate ) )) );

        // review date criteria
        if ( startReviewDate != null )
            pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.SURVEY_COMPLETED_DATE_COLUMN,  new BasicDBObject( "$gte" , startReviewDate ) )) );

        //user id criteria
        if ( userIds != null )
            pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.AGENT_ID_COLUMN,  new BasicDBObject( "$in" , userIds ) )) );

        //add retake survey acriteria
        if ( isRetaken )
            pipeline.add( new BasicDBObject( "$match", new BasicDBObject( CommonConstants.RETAKE_SURVEY_COLUMN , true ) ) );
        
        
        // grouping
        pipeline.add( new BasicDBObject( "$group",  new BasicDBObject( "_id", null ).append( "score", new BasicDBObject( "$avg",  "$score"  ) ) ) );
        BasicDBObject aggregationObject = new BasicDBObject( "aggregate", SURVEY_DETAILS_COLLECTION ).append( "pipeline",
            pipeline );
        
        CommandResult aggregateResult = mongoTemplate.executeCommand( aggregationObject );
        float avgScore = 0;
        List<BasicDBObject> aggregatedData = null;
        if ( aggregateResult.containsField( "result" ) ) {
            aggregatedData = (List<BasicDBObject>) aggregateResult.get( "result" );
            if ( aggregatedData.size() > 0 ) {
                for ( BasicDBObject data : aggregatedData ) {
                     avgScore = Float.parseFloat( data.get( "score" ).toString() ) ;
                }
            }
        }
        
       
                
        LOG.debug( "Method to avg score of surveys, getFilteredSurveyAvgScore() finished." );
        return avgScore;

    }

    @Override
    public void insertApiRequestDetails( ApiRequestDetails apiRequestDetails )
    {
        LOG.debug( "Method started insertApiRequestDetails started." );
        mongoTemplate.insert( apiRequestDetails, API_REQUEST_DETAILS );
        LOG.debug( "Method started insertApiRequestDetails finished." );
    }


    @Override
    public Map<Long, Long> getTotalReviewsCountForAllUsersOfCompany( long companyId )
    {
        Map<Long, Long> totalReviewsByAgentId = new HashMap<Long, Long>();
        TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
            Aggregation.match( Criteria.where( CommonConstants.COMPANY_ID ).is( companyId ) ),
            Aggregation.match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
            Aggregation.match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
            Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );

        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );
        if ( result != null ) {
            @SuppressWarnings ( "unchecked") List<BasicDBObject> reviewsCount = (List<BasicDBObject>) result.getRawResults()
                .get( "result" );
            for ( BasicDBObject reviewCount : reviewsCount ) {
                totalReviewsByAgentId.put(
                    Long.parseLong( reviewCount.get( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).toString() ),
                    Long.parseLong( reviewCount.get( "count" ).toString() ) );
            }
        }
        return totalReviewsByAgentId;
    }


    @Override
    public Map<Long, Long> getSocialSurveyReviewsCountForAllUsersOfCompany( long companyId )
    {
        Map<Long, Long> ssReviewsByAgentId = new HashMap<Long, Long>();
        TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
            Aggregation.match( Criteria.where( CommonConstants.COMPANY_ID ).is( companyId ) ),
            Aggregation.match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
            Aggregation.match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
            Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN )
                .nin( Arrays.asList( CommonConstants.SURVEY_SOURCE_ZILLOW, CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ) ),
            Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );

        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );
        if ( result != null ) {
            @SuppressWarnings ( "unchecked") List<BasicDBObject> reviewsCount = (List<BasicDBObject>) result.getRawResults()
                .get( "result" );
            for ( BasicDBObject reviewCount : reviewsCount ) {
                ssReviewsByAgentId.put( Long.parseLong( reviewCount.get( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).toString() ),
                    Long.parseLong( reviewCount.get( "count" ).toString() ) );
            }
        }
        return ssReviewsByAgentId;
    }


    @Override
    public Map<Long, Long> getZillowReviewsCountForAllUsersOfCompany( long companyId )
    {
        Map<Long, Long> zillowReviewsByAgentId = new HashMap<Long, Long>();
        TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
            Aggregation.match( Criteria.where( CommonConstants.COMPANY_ID ).is( companyId ) ),
            Aggregation.match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
            Aggregation.match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
            Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN ).is( CommonConstants.SURVEY_SOURCE_ZILLOW ) ),
            Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );

        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );
        if ( result != null ) {
            @SuppressWarnings ( "unchecked") List<BasicDBObject> reviewsCount = (List<BasicDBObject>) result.getRawResults()
                .get( "result" );
            for ( BasicDBObject reviewCount : reviewsCount ) {
                zillowReviewsByAgentId.put(
                    Long.parseLong( reviewCount.get( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).toString() ),
                    Long.parseLong( reviewCount.get( "count" ).toString() ) );
            }
        }
        return zillowReviewsByAgentId;
    }


    @Override
    public Map<Long, Long> getAbusiveReviewsCountForAllUsersOfCompany( long companyId )
    {
        Map<Long, Long> abusiveReviewsByAgentId = new HashMap<Long, Long>();
        TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
            Aggregation.match( Criteria.where( CommonConstants.COMPANY_ID ).is( companyId ) ),
            Aggregation.match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
            Aggregation.match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
            Aggregation.match( Criteria.where( CommonConstants.IS_ABUSIVE_COLUMN ).is( true ) ),
            Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );

        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );
        if ( result != null ) {
            @SuppressWarnings ( "unchecked") List<BasicDBObject> reviewsCount = (List<BasicDBObject>) result.getRawResults()
                .get( "result" );
            for ( BasicDBObject reviewCount : reviewsCount ) {
                abusiveReviewsByAgentId.put(
                    Long.parseLong( reviewCount.get( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).toString() ),
                    Long.parseLong( reviewCount.get( "count" ).toString() ) );
            }
        }
        return abusiveReviewsByAgentId;
    }


    @Override
    public Map<Long, Long> getThirdPartyReviewsCountForAllUsersOfCompany( long companyId )
    {
        Map<Long, Long> thirdPartyReviewsByAgentId = new HashMap<Long, Long>();
        TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>( SurveyDetails.class,
            Aggregation.match( Criteria.where( CommonConstants.COMPANY_ID ).is( companyId ) ),
            Aggregation.match( Criteria.where( CommonConstants.STAGE_COLUMN ).is( CommonConstants.SURVEY_STAGE_COMPLETE ) ),
            Aggregation.match( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).ne( CommonConstants.DEFAULT_AGENT_ID ) ),
            Aggregation.match( Criteria.where( CommonConstants.SOURCE_COLUMN ).is( CommonConstants.SURVEY_SOURCE_3RD_PARTY ) ),
            Aggregation.group( CommonConstants.AGENT_ID_COLUMN ).count().as( "count" ) );

        AggregationResults<SurveyDetails> result = mongoTemplate.aggregate( aggregation, SURVEY_DETAILS_COLLECTION,
            SurveyDetails.class );
        if ( result != null ) {
            @SuppressWarnings ( "unchecked") List<BasicDBObject> reviewsCount = (List<BasicDBObject>) result.getRawResults()
                .get( "result" );
            for ( BasicDBObject reviewCount : reviewsCount ) {
                thirdPartyReviewsByAgentId.put(
                    Long.parseLong( reviewCount.get( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).toString() ),
                    Long.parseLong( reviewCount.get( "count" ).toString() ) );
            }
        }
        return thirdPartyReviewsByAgentId;
    }


    @Override
    public List<SurveyDetails> getAllSurveys( int start, int rows )
    {
        Query query = new Query();
        if ( start > -1 ) {
            query.skip( start );
        }
        if ( rows > -1 ) {
            query.limit( rows );
        }
        query.addCriteria( Criteria.where( CommonConstants.SURVEY_SOURCE_COLUMN ).nin( Arrays.asList( "Zillow" ) ) );
        // query.addCriteria( Criteria.where( CommonConstants.SURVEY_SOURCE_ID_COLUMN ).is( null ) );
        List<SurveyDetails> surveys = mongoTemplate.find( query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION );

        return surveys;
    }


    @Override
    public void updateSurveySourceIdInMongo( SurveyDetails survey )
    {
        Query query = new Query();
        query.addCriteria(
            Criteria.where( CommonConstants.SURVEY_PREINITIATION_ID_COLUMN ).is( survey.getSurveyPreIntitiationId() ) );
        Update update = new Update();
        update.set( CommonConstants.SURVEY_SOURCE_ID_COLUMN, survey.getSourceId() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
    }


    @Override
    public void updateTransactionDateInExistingSurveyDetails( SurveyDetails surveyDetails )
    {
        String surveyMongoId = surveyDetails.get_id();
        LOG.info( "Method updateTransactionDateInExistingSurveyDetails() to update source id started " );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyMongoId ) );
        Update update = new Update();
        update.set( CommonConstants.SURVEY_TRANSACTION_DATE_COLUMN, surveyDetails.getSurveyTransactionDate() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method updateTransactionDateInExistingSurveyDetails finished." );
    }


    @Override
    public void updateBranchIdRegionIdForAllSurveysOfAgent( long agentId, long branchId, long regionId )
    {
        LOG.debug( "Method updateBranchIdRegionIdForAllSurveysOfAgent() started for agentId + " + agentId );
        Query query = new Query();

        //update branch id , region id
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        Update update = new Update();
        update.set( CommonConstants.BRANCH_ID_COLUMN, branchId );
        update.set( CommonConstants.REGION_ID_COLUMN, regionId );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date( System.currentTimeMillis() ) );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );

        //update branch media post details
        query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN ).exists( true ) );
        query.addCriteria( Criteria
            .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.BRANCH_MEDIA_POST_DETAILS_COLUMN )
            .exists( true ) );
        query
            .addCriteria( Criteria
                .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
                    + CommonConstants.BRANCH_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.SHARED_ON_COLUMN )
                .exists( true ) );
        update = new Update();
        update.set( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.BRANCH_MEDIA_POST_DETAILS_COLUMN
            + ".0." + CommonConstants.BRANCH_ID_COLUMN, branchId );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );

        //update region media post details
        query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN ).exists( true ) );
        query.addCriteria( Criteria
            .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN )
            .exists( true ) );
        query
            .addCriteria( Criteria
                .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
                    + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.SHARED_ON_COLUMN )
                .exists( true ) );
        update = new Update();
        update.set( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN
            + ".0." + CommonConstants.REGION_ID_COLUMN, regionId );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );


        LOG.debug( "Method updateBranchIdRegionIdForAllSurveysOfAgent() finished for agentId + " + agentId );
    }


    @Override
    public void moveSurveysAlongWithUser( long agentId, long branchId, long regionId, long companyId )
    {
        LOG.info( "Method moveSurveysAlongWithUser() startedfor user  " + agentId );
        Query query = new Query();

        //update branch id , region id, company id
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        Update update = new Update();
        update.set( CommonConstants.BRANCH_ID_COLUMN, branchId );
        update.set( CommonConstants.REGION_ID_COLUMN, regionId );
        update.set( CommonConstants.COMPANY_ID_COLUMN, companyId );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );

        //update branch media post details
        query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN ).exists( true ) );
        query.addCriteria( Criteria
            .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.BRANCH_MEDIA_POST_DETAILS_COLUMN )
            .exists( true ) );
        query
            .addCriteria( Criteria
                .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
                    + CommonConstants.BRANCH_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.SHARED_ON_COLUMN )
                .exists( true ) );
        update = new Update();
        update.set( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.BRANCH_MEDIA_POST_DETAILS_COLUMN
            + ".0." + CommonConstants.BRANCH_ID_COLUMN, branchId );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );

        //update region media post details
        query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN ).exists( true ) );
        query.addCriteria( Criteria
            .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN )
            .exists( true ) );
        query
            .addCriteria( Criteria
                .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
                    + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.SHARED_ON_COLUMN )
                .exists( true ) );
        update = new Update();
        update.set( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN
            + ".0." + CommonConstants.REGION_ID_COLUMN, regionId );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );


        //update company media post details
        query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        query.addCriteria( Criteria.where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN ).exists( true ) );
        query.addCriteria( Criteria
            .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.COMPANY_MEDIA_POST_DETAILS_COLUMN )
            .exists( true ) );
        query
            .addCriteria( Criteria
                .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
                    + CommonConstants.COMPANY_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.SHARED_ON_COLUMN )
                .exists( true ) );
        update = new Update();
        update.set( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.COMPANY_MEDIA_POST_DETAILS_COLUMN
            + "." + CommonConstants.COMPANY_ID_COLUMN, regionId );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );


        LOG.info( "Method moveSurveysAlongWithUser finished." );
    }


    @Override
    public void updateAgentIdInSurveyDetail( SurveyDetails surveyDetails )
    {
        LOG.debug( "Method updateAgentIdInSurveyDetail() to update agentId of survey started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyDetails.get_id() ) );

        Update update = new Update();
        update.set( CommonConstants.AGENT_ID_COLUMN, surveyDetails.getAgentId() );

        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method updateAgentIdInSurveyDetail() to update agentId of survey finished." );
    }


    @Override
    public void disconnectSurveysFromWithUser( long agentId )
    {
        LOG.info( "Method moveSurveysAlongWithUser() startedfor user  " + agentId );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.AGENT_ID_COLUMN ).is( agentId ) );
        Update update = new Update();
        update.set( CommonConstants.AGENT_ID_COLUMN, 0l );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method moveSurveysAlongWithUser finished." );
    }


    @Override
    public void updateRegionIdForAllSurveysOfBranch( long branchId, long regionId )
    {
        LOG.debug( "Method updateRegionIdForAllSurveysOfBranch() started for branchId + " + branchId );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.BRANCH_ID_COLUMN ).is( branchId ) );
        Update update = new Update();
        update.set( CommonConstants.REGION_ID_COLUMN, regionId );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );


        query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.BRANCH_ID_COLUMN ).is( branchId ) );
        query.addCriteria( Criteria.where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN ).exists( true ) );
        query.addCriteria( Criteria
            .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN )
            .exists( true ) );
        query
            .addCriteria( Criteria
                .where( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "."
                    + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.SHARED_ON_COLUMN )
                .exists( true ) );

        update = new Update();
        update.set( CommonConstants.SOCIAL_MEDIA_POST_DETAILS_COLUMN + "." + CommonConstants.REGION_MEDIA_POST_DETAILS_COLUMN
            + ".0." + CommonConstants.REGION_ID_COLUMN, regionId );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );

        LOG.debug( "Method updateRegionIdForAllSurveysOfBranch() finished for branchId + " + branchId );
    }
    
    @Override
    public void updateSurveyDetailsForRetake( SurveyDetails surveyDetails )
    {
        LOG.debug( "Method insertSurveyDetails() to insert details of survey started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyDetails.get_id() ) );

        Update update = new Update();
        update.set( CommonConstants.STAGE_COLUMN, surveyDetails.getStage());
        update.set( CommonConstants.NPS_SCORE_COLUMN, surveyDetails.getNpsScore() );
        update.set( CommonConstants.REVIEW_COLUMN , surveyDetails.getReview() );
        update.set( CommonConstants.RETAKE_SURVEY_COLUMN, surveyDetails.isRetakeSurvey());
        update.set( CommonConstants.NO_OF_RETAKE_COLUMN, surveyDetails.getNoOfRetake() );
        update.set( CommonConstants.LAST_RETAKE_REQUEST_DATE_COLUMN, surveyDetails.getLastRetakeRequestDate());
        update.set( CommonConstants.RETAKE_SURVEY_HISTORY_COLUMN, surveyDetails.getRetakeSurveyHistory());
        update.set( CommonConstants.OPEN_RETAKE_SURVEY_REQUEST_COLUMN, surveyDetails.isOpenRetakeSurveyRequest());
        update.set( CommonConstants.MODIFIED_ON_COLUMN, new Date() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method insertSurveyDetails() to insert details of survey finished." );
    }

	@Override
	public List<SurveyDetails> getSurveyDetailsForCompanyAndQuestion(long companyId, String question) {
		Query query = new Query();
		query.addCriteria(Criteria.where(CommonConstants.COMPANY_ID_COLUMN).is(companyId));
		query.addCriteria(Criteria.where(CommonConstants.SURVEY_RESPONSE_QUESTION).is(question));
		query.addCriteria(Criteria.where(CommonConstants.SURVEY_RESPONSE_QUESTION_TYPE).is(CommonConstants.QUESTION_TYPE_MCQ));
		List<SurveyDetails> surveys = mongoTemplate.find(query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION);
		return surveys;
	}


	@Override
	public void updateSurveyNPSScore(SurveyDetails surveyDetails) {
		LOG.debug( "Method updateSurveyNPSScore started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyDetails.get_id() ) );

        Update update = new Update();
        update.set( CommonConstants.NPS_SCORE_COLUMN, surveyDetails.getNpsScore() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method updateSurveyNPSScore finished." );
	}
	
	@Override
    public void updateSourceDetailInExistingSurveyDetails( SurveyDetails surveyDetails )
    {
        String surveyMongoId = surveyDetails.get_id();
        LOG.info( "Method updateZillowSourceIdInExistingSurveyDetails() to update source id started " );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyMongoId ) );
        Update update = new Update();
        update.set( CommonConstants.SURVEY_SOURCE_ID_COLUMN, surveyDetails.getSourceId() );
        update.set( CommonConstants.SURVEY_SOURCE_COLUMN, surveyDetails.getSource() );
        mongoTemplate.updateMulti( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.info( "Method updateZillowSourceIdInExistingSurveyDetails finished." );
    }


	@Override
	public void updateSurveyAsAbusiveNotify(String surveyId) {
		LOG.debug( "Method updateSurveyAsAbusiveNotify() to mark survey as abusive notify started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( surveyId ) );
        Update update = new Update();
        update.set( CommonConstants.ABUSIVE_NOTIFY_COLUMN, true );
        mongoTemplate.upsert( query, update, SURVEY_DETAILS_COLLECTION );
        LOG.debug( "Method updateSurveyAsAbusiveNotify() to mark survey as abusive notify finished." );
	
	}
	
	@Override
    public SurveyDetails getsurveyFromSurveyPreinitiationId(long surveyPreinitiationId) {
	    LOG.debug( "Method getAgentIdFromSurveyPreinitiationId()  started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.SURVEY_PREINITIATION_ID_COLUMN ).is( surveyPreinitiationId ) );
        SurveyDetails survey = mongoTemplate.findOne(query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION);
        return survey;
	}


    @SuppressWarnings ( "unchecked")
    @Override
    public List<String> getDistinctValues( String queryKey, Object value, String field )
    {
        return mongoTemplate.getCollection( SURVEY_DETAILS_COLLECTION ).distinct(field, new Query( Criteria.where( queryKey ).is( value ) ).getQueryObject() );
    }
}