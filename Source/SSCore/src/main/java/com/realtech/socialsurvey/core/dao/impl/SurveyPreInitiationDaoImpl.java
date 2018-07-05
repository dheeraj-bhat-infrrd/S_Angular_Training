package com.realtech.socialsurvey.core.dao.impl;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.integration.EngagementProcessingStatus;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


@Component ( "surveypreinitiation")
public class SurveyPreInitiationDaoImpl extends GenericDaoImpl<SurveyPreInitiation, Long> implements SurveyPreInitiationDao
{

    private static final Logger LOG = LoggerFactory.getLogger( SurveyPreInitiationDaoImpl.class );

    @Resource
    @Qualifier ( "user")
    private UserDao userDao;

    private static final String proceesedCurruptedSurvey = "select SURVEY_PRE_INITIATION_ID ,  SURVEY_SOURCE_ID  , COMPANY_ID , AGENT_EMAILID , "
        + "CUSTOMER_FIRST_NAME  , CUSTOMER_LAST_NAME , CUSTOMER_EMAIL_ID , AGENT_ID , STATUS , CREATED_ON , ENGAGEMENT_CLOSED_TIME, AGENT_NAME  from "
        + "SURVEY_PRE_INITIATION "
        + " where COMPANY_ID= :companyId AND ( (AGENT_EMAILID IN "
        + " (select EMAIL_ID from USER_EMAIL_MAPPING where COMPANY_ID=:companyId) ) OR  ( AGENT_EMAILID IN "
        + " (select EMAIL_ID from COMPANY_IGNORED_EMAIL_MAPPING where COMPANY_ID=:companyId) ) ) ORDER BY ENGAGEMENT_CLOSED_TIME DESC";

    private static final String proceesedCurruptedSurveyCount = "select count(*)  from " + "SURVEY_PRE_INITIATION "
        + " where COMPANY_ID= :companyId AND ( (AGENT_EMAILID IN "
        + " (select EMAIL_ID from USER_EMAIL_MAPPING where COMPANY_ID=:companyId AND STATUS=" + CommonConstants.STATUS_ACTIVE
        + ") ) OR  ( AGENT_EMAILID IN "
        + " (select EMAIL_ID from COMPANY_IGNORED_EMAIL_MAPPING where COMPANY_ID=:companyId AND STATUS="
        + CommonConstants.STATUS_ACTIVE + ") ) )";


    @Override
    public Timestamp getLastRunTime( String source ) throws InvalidInputException
    {
        LOG.info( "Get the max created time for source " + source );
        if ( source == null || source.isEmpty() ) {
            LOG.debug( "Source is not provided." );
            throw new InvalidInputException( "Souce is not provided." );
        }
        Timestamp lastRunTime = null;
        Criteria criteria = getSession().createCriteria( SurveyPreInitiation.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.SURVEY_SOURCE_KEY_COLUMN, source ) );
            criteria.setProjection( Projections.max( CommonConstants.CREATED_ON ) );
            Object result = criteria.uniqueResult();
            if ( result instanceof Timestamp ) {
                lastRunTime = (Timestamp) result;
            }
        } catch ( HibernateException ex ) {
            LOG.error( "Exception caught in getLastRunTime() ", ex );
            throw new DatabaseException( "Exception caught in getLastRunTime() ", ex );
        }
        return lastRunTime;
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<EngagementProcessingStatus> getProcessedIds( String source, Timestamp timestamp ) throws InvalidInputException
    {
        if ( source == null || source.isEmpty() ) {
            LOG.warn( "Source is not present." );
            throw new InvalidInputException( "Source is not present." );
        }
        LOG.info( "Getting processed ids for source " + source + " after timestamp "
            + ( timestamp != null ? String.valueOf( timestamp ) : "" ) );
        List<EngagementProcessingStatus> processedRecords = null;
        Criteria criteria = getSession().createCriteria( SurveyPreInitiation.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.SURVEY_SOURCE_KEY_COLUMN, source ) );
            if ( timestamp != null ) {
                criteria.add( Restrictions.ge( CommonConstants.CREATED_ON, timestamp ) );
            }
            criteria.setProjection( Projections.property( CommonConstants.SURVEY_SOURCE_ID_COLUMN ) );
            criteria.setProjection( Projections.property( CommonConstants.STATUS_COLUMN ) );
            processedRecords = (List<EngagementProcessingStatus>) criteria.list();
        } catch ( HibernateException ex ) {
            LOG.error( "Exception caught in getProcessedIds() ", ex );
            throw new DatabaseException( "Exception caught in getProcessedIds() ", ex );
        }
        return processedRecords;
    }


    // Method to get list of incomplete surveys to display in Dash board and profile page.
    @SuppressWarnings ( "unchecked")
    @Override
    public List<SurveyPreInitiation> getIncompleteSurvey( Timestamp startDate, Timestamp endDate, int start, int row,
        Set<Long> agentIds, boolean isCompanyAdmin, long companyId, boolean realtechAdmin ) throws DatabaseException
    {
        Criteria criteria = getSession().createCriteria( SurveyPreInitiation.class );
        try {
            if ( startDate != null )
                criteria.add( Restrictions.ge( CommonConstants.MODIFIED_ON_COLUMN, startDate ) );
            if ( endDate != null )
                criteria.add( Restrictions.le( CommonConstants.MODIFIED_ON_COLUMN, endDate ) );
            if ( row > 0 )
                criteria.setMaxResults( row );
            if ( start > 0 )
                criteria.setFirstResult( start );

            if ( !realtechAdmin ) {
                if ( !isCompanyAdmin && agentIds.size() > 0 )
                    criteria.add( Restrictions.in( CommonConstants.AGENT_ID_COLUMN, agentIds ) );
                else {
                    criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
                }
            }
            // JIRA: SS-1357: Begin
            criteria.add( Restrictions.in( CommonConstants.STATUS_COLUMN, new Integer[] {
                CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED, CommonConstants.SURVEY_STATUS_INITIATED } ) );
            // JIRA: SS-1357: End
            criteria.addOrder( Order.desc( CommonConstants.MODIFIED_ON_COLUMN ) );
            return criteria.list();
        } catch ( HibernateException e ) {
            LOG.error( "Exception caught in getIncompleteSurvey() ", e );
            throw new DatabaseException( "Exception caught in getIncompleteSurvey() ", e );
        }
    }


    // Method to get incomplete survey list for sending reminder mail.
    @SuppressWarnings ( "unchecked")
    @Override
    public List<SurveyPreInitiation> getIncompleteSurveyForReminder( long companyId, int surveyReminderInterval,
        int maxReminders )
    {
        LOG.info( "Method getIncompleteSurveyForReminder() started." );
        Criteria criteria = getSession().createCriteria( SurveyPreInitiation.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.le( "lastReminderTime", new Timestamp( new Date().getTime() - surveyReminderInterval
                * 24 * 60 * 60 * 1000 ) ) );
            criteria.add( Restrictions.and(
                Restrictions.ne( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_SURVEYPREINITIATION_COMPLETE ),
                Restrictions.ne( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_SURVEYPREINITIATION_DELETED ) ) );
            if ( maxReminders > 0 )
                criteria.add( Restrictions.lt( "reminderCounts", maxReminders ) );
            LOG.info( "Method getIncompleteSurveyForReminder() finished." );
            return criteria.list();
        } catch ( HibernateException e ) {
            LOG.error( "Exception caught in getIncompleteSurveyForReminder() ", e );
            throw new DatabaseException( "Exception caught in getIncompleteSurveyForReminder() ", e );
        }
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public void getIncompleteSurveysCount( Date startDate, Date endDate, Map<Long, AgentRankingReport> agentReportData )
    {
        LOG.info( "Method getIncompleteSurveysCount() started" );
        List<SurveyPreInitiation> surveys = new ArrayList<>();

        Criteria criteria = getSession().createCriteria( SurveyPreInitiation.class );

        criteria.add( Restrictions.in( CommonConstants.STATUS_COLUMN, Arrays.asList(
            CommonConstants.SURVEY_STATUS_PRE_INITIATED, CommonConstants.SURVEY_STATUS_INITIATED,
            CommonConstants.STATUS_SURVEYPREINITIATION_DELETED ) ) );

        criteria.add( Restrictions.in( CommonConstants.AGENT_ID, agentReportData.keySet() ) );


        try {
            if ( startDate != null && endDate != null ) {
                criteria.add( Restrictions.ge( CommonConstants.CREATED_ON, new Timestamp( startDate.getTime() ) ) );
                criteria.add( Restrictions.le( CommonConstants.CREATED_ON, new Timestamp( endDate.getTime() ) ) );
            } else if ( startDate != null && endDate == null )
                criteria.add( Restrictions.ge( CommonConstants.CREATED_ON, new Timestamp( startDate.getTime() ) ) );
            else if ( startDate == null && endDate != null )
                criteria.add( Restrictions.le( CommonConstants.CREATED_ON, new Timestamp( endDate.getTime() ) ) );

            LOG.info( "criteria to get incomplete survey count for user ranking report is : "  +criteria.toString() );
            surveys = criteria.list();
        } catch ( HibernateException e ) {
            LOG.error( "Exception caught in getIncomplgetIncompleteSurveysCounteteSurveyForReminder() ", e );
            throw new DatabaseException( "Exception caught in getIncompleteSurveysCount() ", e );
        }

        for ( SurveyPreInitiation survey : surveys ) {

            AgentRankingReport agentRankingReport = null;
            if ( agentReportData.containsKey( survey.getAgentId() ) ) {
                agentRankingReport = agentReportData.get( survey.getAgentId() );
                agentRankingReport.setAgentName( survey.getAgentName() );
                agentRankingReport.setIncompleteSurveys( agentRankingReport.getIncompleteSurveys() + 1 );
                agentReportData.put( survey.getAgentId(), agentRankingReport );
            }

        }
        LOG.info( "Method getIncompleteSurveysCount() finished" );
    }


    @Override
    public void deleteSurveysWithIds( Set<Long> incompleteSurveyIds )
    {
        LOG.info( "Method deleteSurveysWithIds() started" );
        //First get the list of surveys that will be deleted
        String deleteQuery = "update SurveyPreInitiation set status=0, modifiedOn=(:modifiedOnTime) where surveyPreIntitiationId in (:incompleteSurveyIds) and status in (:statuses)";
        Query query = getSession().createQuery( deleteQuery );
        query.setParameter( "modifiedOnTime", new Timestamp( System.currentTimeMillis() ) );
        query.setParameterList( "incompleteSurveyIds", incompleteSurveyIds );
        query.setParameterList( "statuses", new Integer[] { CommonConstants.SURVEY_STATUS_PRE_INITIATED,
            CommonConstants.SURVEY_STATUS_INITIATED } );
        query.executeUpdate();
    }


    /**
    * Method to fetch preinitiated surveys by IDs
    * 
    * @param incompleteSurveyIds
    * @return
    */
    @SuppressWarnings ( "unchecked")
    @Override
    public List<SurveyPreInitiation> fetchSurveysByIds( Set<Long> incompleteSurveyIds )
    {
        LOG.info( "Method fetchSurveysByIds() started" );
        Criteria criteria = getSession().createCriteria( SurveyPreInitiation.class );
        criteria.add( Restrictions.and(
            Restrictions.ne( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_SURVEYPREINITIATION_COMPLETE ),
            Restrictions.ne( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_SURVEYPREINITIATION_DELETED ) ) );
        criteria.add( Restrictions.in( CommonConstants.SURVEY_PREINITIATION_ID_COLUMN, incompleteSurveyIds ) );
        return criteria.list();
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public long getIncompleteSurveyCount( long companyId, long agentId, int[] status, Timestamp startDate, Timestamp endDate,
        Set<Long> agentIds )
    {
        LOG.debug( "getting incomplete survey count" );
        StringBuilder queryBuilder = new StringBuilder( "SELECT COUNT(*) AS COUNT FROM SURVEY_PRE_INITIATION WHERE " );
        boolean whereFlag = false; // used if where is
        if ( companyId > 0l ) {
            queryBuilder.append( " COMPANY_ID = :companyId" );
            whereFlag = true;
        }
        if(status != null){
        	StringBuilder statusCrit = new StringBuilder(" STATUS IN (");
        	for(int i=0; i< status.length; i++){
        		if(i != 0){
        			statusCrit.append(",");
        		}
        		statusCrit.append(status[i]);
        	}
        	statusCrit.append(")");
	        if ( whereFlag ) {
	        	queryBuilder.append( " AND").append(statusCrit.toString());
	        } else {
	            queryBuilder.append( statusCrit.toString());
	            whereFlag = true;
	        }
        }
        if ( startDate != null ) {
            if ( whereFlag ) {
                queryBuilder.append( " AND CREATED_ON >= :startDate" );
            } else {
                queryBuilder.append( " CREATED_ON >= :startDate" );
                whereFlag = true;
            }
        }
        if ( endDate != null ) {
            if ( whereFlag ) {
                queryBuilder.append( " AND CREATED_ON <= :endDate" );
            } else {
                queryBuilder.append( " CREATED_ON <= :endDate" );
                whereFlag = true;
            }
        }
        if ( agentId > 0l ) {
            if ( whereFlag ) {
                queryBuilder.append( " AND AGENT_ID = :agentId" );
            } else {
                queryBuilder.append( " AGENT_ID = :agentId" );
                whereFlag = true;
            }
        } else if ( agentIds != null && agentIds.size() > 0 ) {
            if ( whereFlag ) {
                queryBuilder.append( " AND AGENT_ID IN (:agentIds)" );
            } else {
                queryBuilder.append( " AGENT_ID IN (:agentIds)" );
                whereFlag = true;
            }
        }
        Query query = null;
        query = getSession().createSQLQuery( queryBuilder.toString() );
        if ( companyId > 0l ) {
            query.setParameter( "companyId", companyId );
        }
        //query.setParameter("status", status);
        if ( startDate != null ) {
            query.setParameter( "startDate", startDate );
        }
        if ( endDate != null ) {
            query.setParameter( "endDate", endDate );
        }
        if ( agentId > 0l ) {
            query.setParameter( "agentId", agentId );
        } else if ( agentIds != null && agentIds.size() > 0 ) {
            query.setParameterList( "agentIds", agentIds );
        }
        List<BigInteger> results = query.list();
        long count = 0l;
        if ( results != null && results.size() > 0 ) {
            count = results.get( 0 ).longValue();
        }
        return count;
    }


    @Override
    public Map<Integer, Integer> getIncompletSurveyAggregationCount( long companyId, long agentId, int status,
        Timestamp startDate, Timestamp endDate, Set<Long> agentIds, String aggregateBy ) throws InvalidInputException
    {
        LOG.debug( "Getting incomplete survey aggregated count for company id : {} \t status: {} \t startDate {} \t end date: {} \t aggregatedBy: {}" , companyId, status, startDate, endDate, aggregateBy );
        Map<Integer, Integer> aggregateResult = null;
        StringBuilder queryBuilder = new StringBuilder();
        if ( aggregateBy == null || aggregateBy.isEmpty() ) {
            LOG.warn( "Aggregate by is null" );
            throw new InvalidInputException( "Aggregate by is null" );
        }
        boolean whereFlag = false; // used if where is 
        if ( aggregateBy.equals( CommonConstants.AGGREGATE_BY_WEEK ) ) {
            queryBuilder
                .append( "SELECT YEARWEEK(CREATED_ON) AS SENT_DATE, COUNT(SURVEY_PRE_INITIATION_ID) AS NUM_OF_SURVEYS FROM SURVEY_PRE_INITIATION WHERE " );
        } else if ( aggregateBy.equals( CommonConstants.AGGREGATE_BY_DAY ) ) {
            queryBuilder
                .append( "SELECT DATE(CREATED_ON) AS SENT_DATE, COUNT(SURVEY_PRE_INITIATION_ID) AS NUM_OF_SURVEYS FROM SURVEY_PRE_INITIATION WHERE " );
        } else if ( aggregateBy.equals( CommonConstants.AGGREGATE_BY_MONTH ) ) {
            queryBuilder
                .append( "SELECT EXTRACT(YEAR_MONTH FROM CREATED_ON) AS SENT_DATE, COUNT(SURVEY_PRE_INITIATION_ID) AS NUM_OF_SURVEYS FROM SURVEY_PRE_INITIATION WHERE " );
        }
        if ( companyId > 0l ) {
            queryBuilder.append( " COMPANY_ID = :companyId" );
            whereFlag = true;
        }
        // Change the incomplete count for status 1 and 2 - Important! Needs to be modified later to accomodate status list in input
        if ( whereFlag ) {
            queryBuilder.append( " AND STATUS IN (0,1,2)" );
        } else {
            queryBuilder.append( " STATUS IN (0,1,2)" );
            whereFlag = true;
        }
        if ( startDate != null ) {
            if ( whereFlag ) {
                queryBuilder.append( " AND CREATED_ON >= :startDate" );
            } else {
                queryBuilder.append( " CREATED_ON >= :startDate" );
                whereFlag = true;
            }
        }
        if ( endDate != null ) {
            if ( whereFlag ) {
                queryBuilder.append( " AND CREATED_ON <= :endDate" );
            } else {
                queryBuilder.append( " CREATED_ON <= :endDate" );
                whereFlag = true;
            }
        }
        if ( agentId > 0l ) {
            if ( whereFlag ) {
                queryBuilder.append( " AND AGENT_ID = :agentId" );
            } else {
                queryBuilder.append( " AGENT_ID = :agentId" );
                whereFlag = true;
            }
        } else if ( agentIds != null && agentIds.size() > 0 ) {
            if ( whereFlag ) {
                queryBuilder.append( " AND AGENT_ID IN (:agentIds)" );
            } else {
                queryBuilder.append( " AGENT_ID IN (:agentIds)" );
                whereFlag = true;
            }
        }
        if ( aggregateBy.equals( CommonConstants.AGGREGATE_BY_WEEK ) ) {
            queryBuilder.append( " GROUP BY YEARWEEK(CREATED_ON) ORDER BY SENT_DATE" );
        } else if ( aggregateBy.equals( CommonConstants.AGGREGATE_BY_DAY ) ) {
            queryBuilder.append( " GROUP BY DATE(CREATED_ON) ORDER BY SENT_DATE" );
        } else if ( aggregateBy.equals( CommonConstants.AGGREGATE_BY_MONTH ) ) {
            queryBuilder.append( " GROUP BY EXTRACT(YEAR_MONTH FROM CREATED_ON) ORDER BY SENT_DATE" );
        }
        Query query = null;
        query = getSession().createSQLQuery( queryBuilder.toString() );
        if ( companyId > 0l ) {
            query.setParameter( "companyId", companyId );
        }
        //query.setParameter("status", status);
        if ( startDate != null ) {
            query.setParameter( "startDate", startDate );
        }
        if ( endDate != null ) {
            query.setParameter( "endDate", endDate );
        }
        if ( agentId > 0l ) {
            query.setParameter( "agentId", agentId );
        } else if ( agentIds != null && agentIds.size() > 0 ) {
            query.setParameterList( "agentIds", agentIds );
        }
        
        LOG.debug( "query for get incompleted survey for garph is : {}", query.toString() );

        
        @SuppressWarnings ( "unchecked") List<Object[]> results = query.list();
        if ( results != null && results.size() > 0 ) {
            aggregateResult = new HashMap<Integer, Integer>();
            for ( Object[] result : results ) {
                aggregateResult.put( (Integer) result[0], ( (BigInteger) result[1] ).intValue() );
            }
        }
        return aggregateResult;
    }


    /**
     * Method to delete SurveyPreInitiation records for a specific agent ID
     * @param agentId
     * @throws InvalidInputException
     */
    @Override
    public void deletePreInitiatedSurveysForAgent( long agentId, int status ) throws InvalidInputException
    {
        LOG.info( "Method to delete SurveyPreInitiation records for agent ID : " + agentId + " started." );
        //Check if the ID is valid
        if ( agentId <= 0l ) {
            throw new InvalidInputException( "Invalid agent ID : " + agentId );
        }
        String deleteQuery = "update SurveyPreInitiation set status=(:status), modifiedOn=(:modifiedOnTime) where agentId = (:deletedAgentId) and status in (:statuses)";
        Query query = getSession().createQuery( deleteQuery );
        query.setParameter( "modifiedOnTime", new Timestamp( System.currentTimeMillis() ) );
        query.setParameter( "deletedAgentId", agentId );
        query.setParameter( "status", status );
        query.setParameterList( "statuses", new Integer[] { CommonConstants.SURVEY_STATUS_PRE_INITIATED,
            CommonConstants.SURVEY_STATUS_INITIATED } );
        query.executeUpdate();
        LOG.info( "Method to delete SurveyPreInitiation records for agent ID : " + agentId + " finished." );
    }


    /**
     * Method to update agent info when survey moved from one user to another
     * @throws InvalidInputException
     * */
    @Override
    public void updateAgentInfoOfPreInitiatedSurveys( long fromUserId, User toUser ) throws InvalidInputException
    {

        if ( fromUserId <= 0l ) {
            throw new InvalidInputException( "Invalid from agent id : " + fromUserId );
        }

        if ( toUser == null ) {
            throw new InvalidInputException( "To agent passed cannot be null" );
        }
        LOG.info( "Method to update pre initiated surveys agent id from " + fromUserId + " to " + toUser.getUserId()
            + " started." );
        String queryStr = "UPDATE SURVEY_PRE_INITIATION SET AGENT_ID = ?, AGENT_NAME=?,AGENT_EMAILID=?, MODIFIED_ON=?, COMPANY_ID=? WHERE AGENT_ID = ?";
        Query query = getSession().createSQLQuery( queryStr );
        query.setParameter( 0, toUser.getUserId() );
        query.setParameter( 1, toUser.getFirstName() + ( toUser.getLastName() == null ? "" : " " + toUser.getLastName() ) );
        query.setParameter( 2, toUser.getEmailId() );
        query.setParameter( 3, new Timestamp( System.currentTimeMillis() ) );
        query.setParameter( 4, toUser.getCompany().getCompanyId() );
        query.setParameter( 5, fromUserId );
        query.executeUpdate();
        LOG.info( "Method to update pre initiated surveys agent id from " + fromUserId + " to " + toUser.getUserId()
            + " ended." );
    }
    

    @Override
    public void updateAgentInfoOfPreInitiatedSurvey( long surveyPreinitiatinId, User toUser ) throws InvalidInputException
    {

        if ( surveyPreinitiatinId <= 0l ) {
            throw new InvalidInputException( "Invalid from agent id : " + surveyPreinitiatinId );
        }

        if ( toUser == null ) {
            throw new InvalidInputException( "To agent passed cannot be null" );
        }
        LOG.info( "Updating pre initiated surveys for id:{} to agent:{}", surveyPreinitiatinId, toUser.getUserId() );
        String queryStr = "UPDATE SURVEY_PRE_INITIATION SET AGENT_ID = ?, AGENT_NAME=?,AGENT_EMAILID=?, MODIFIED_ON=?, COMPANY_ID=? WHERE SURVEY_PRE_INITIATION_ID = ?";
        Query query = getSession().createSQLQuery( queryStr );
        query.setParameter( 0, toUser.getUserId() );
        query.setParameter( 1, toUser.getFirstName() + ( toUser.getLastName() == null ? "" : " " + toUser.getLastName() ) );
        query.setParameter( 2, toUser.getEmailId() );
        query.setParameter( 3, new Timestamp( System.currentTimeMillis() ) );
        query.setParameter( 4, toUser.getCompany().getCompanyId() );
        query.setParameter( 5, surveyPreinitiatinId );
        query.executeUpdate();
        LOG.info( "Updating pre initiated surveys for id:{} to agent:{}", surveyPreinitiatinId, toUser.getUserId() );

    }


    // Method to get incomplete survey list for sending reminder mail.
    @SuppressWarnings ( "unchecked")
    @Override
    public List<SurveyPreInitiation> getUnmatchedPreInitiatedSurveys( long companyId, int start, int batch )
    {
        LOG.info( "Method getUnmatchedPreInitiatedSurveys() started." );
        Criteria criteria = getSession().createCriteria( SurveyPreInitiation.class, "surveyPreInitiation" );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.AGENT_ID_COLUMN, CommonConstants.DEFAULT_AGENT_ID ) );
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN,
                CommonConstants.STATUS_SURVEYPREINITIATION_MISMATCH_RECORD ) );
            criteria.addOrder( Order.desc( CommonConstants.ENGAGEMENT_CLOSED_TIME ) );
            if ( start > -1 )
                criteria.setFirstResult( start );
            if ( batch > -1 )
                criteria.setMaxResults( batch );
            LOG.info( "Method getUnmatchedPreInitiatedSurveys() finished." );
            return criteria.list();
        } catch ( HibernateException e ) {
            LOG.error( "Exception caught in getUnmatchedPreInitiatedSurveys() ", e );
            throw new DatabaseException( "Exception caught in getUnmatchedPreInitiatedSurveys() ", e );
        }
    }


    // Method to get incomplete survey list for sending reminder mail.
    @Override
    public long getUnmatchedPreInitiatedSurveyCount( long companyId )
    {
        LOG.info( "Method getUnmatchedPreInitiatedSurveyCount() started." );
        Criteria criteria = getSession().createCriteria( SurveyPreInitiation.class, "surveyPreInitiation" );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.AGENT_ID_COLUMN, CommonConstants.DEFAULT_AGENT_ID ) );
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN,
                CommonConstants.STATUS_SURVEYPREINITIATION_MISMATCH_RECORD ) );
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.info( "Method getUnmatchedPreInitiatedSurveyCount() finished." );
            return count.longValue();
        } catch ( HibernateException e ) {
            LOG.error( "Exception caught in getUnmatchedPreInitiatedSurveyCount() ", e );
            throw new DatabaseException( "Exception caught in getUnmatchedPreInitiatedSurveyCount() ", e );
        }
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<SurveyPreInitiation> getProcessedPreInitiatedSurveys( long companyId, int start, int batch )
    {
        LOG.info( "Method getUnmatchedPreInitiatedSurveys() started." );

        Query query = getSession().createSQLQuery( proceesedCurruptedSurvey );
        query.setParameter( "companyId", companyId );
        if ( start > -1 ) {
            query.setFirstResult( start );
        }
        if ( batch > -1 ) {
            query.setMaxResults( batch );
        }

        LOG.debug( "QUERY : " + query.getQueryString() );
        List<Object[]> rows = (List<Object[]>) query.list();
        List<SurveyPreInitiation> surveyPreInitiations = new ArrayList<SurveyPreInitiation>();
        for ( Object[] row : rows ) {
            SurveyPreInitiation surveyPreInitiation = new SurveyPreInitiation();
            surveyPreInitiation.setSurveyPreIntitiationId( Long.parseLong( String.valueOf( row[0] ) ) );
            surveyPreInitiation.setSurveySourceId( String.valueOf( row[1] ) );
            surveyPreInitiation.setCompanyId( Long.parseLong( String.valueOf( row[2] ) ) );
            surveyPreInitiation.setAgentEmailId( String.valueOf( row[3] ) );
            surveyPreInitiation.setCustomerFirstName( String.valueOf( row[4] ) );
            surveyPreInitiation.setCustomerLastName( String.valueOf( row[5] ) );
            surveyPreInitiation.setCustomerEmailId( String.valueOf( row[6] ) );
            surveyPreInitiation.setAgentId( Long.valueOf( String.valueOf( row[7] ) ) );
            surveyPreInitiation.setStatus( Integer.valueOf( String.valueOf( row[8] ) ) );
            surveyPreInitiation.setCreatedOn( Timestamp.valueOf( String.valueOf( row[9] ) ) );
            surveyPreInitiation.setEngagementClosedTime( Timestamp.valueOf( String.valueOf( row[10] ) ) );
            surveyPreInitiation.setAgentName( String.valueOf( row[11] ) );
            if ( surveyPreInitiation.getAgentId() > 0 ) {
                User user = userDao.findById( User.class, surveyPreInitiation.getAgentId() );
                if ( user != null ) {
                    User agent = new User();
                    agent.setUserId( user.getUserId() );
                    agent.setEmailId( user.getEmailId() );
                    agent.setLoginName( user.getLoginName() );
                    surveyPreInitiation.setUser( agent );
                }
            }
            surveyPreInitiations.add( surveyPreInitiation );

        }


        return surveyPreInitiations;
    }


    // Method to get incomplete survey list for sending reminder mail.
    @SuppressWarnings ( "unchecked")
    @Override
    public long getProcessedPreInitiatedSurveyCount( long companyId )
    {
        LOG.info( "Method getUnmatchedPreInitiatedSurveyCount() started." );
        try {

            Query query = getSession().createSQLQuery( proceesedCurruptedSurveyCount );
            query.setParameter( "companyId", companyId );
            List<Object[]> rows = (List<Object[]>) query.list();
            long count = 0;
            if ( rows != null && !rows.isEmpty() )
                count = Long.valueOf( String.valueOf( rows.get( 0 ) ) );

            LOG.info( "Method getProcessedPreInitiatedSurveyCount() finished." );
            return count;
        } catch ( HibernateException e ) {
            LOG.error( "Exception caught in getProcessedPreInitiatedSurveyCount() ", e );
            throw new DatabaseException( "Exception caught in getUnmatchedPreInitiatedSurveyCount() ", e );
        }
    }


    /**
     * Method to update agent info when survey moved from one user to another
     * @throws InvalidInputException
     * */
    @Override
    public void updateAgentIdOfPreInitiatedSurveysByAgentEmailAddress( User agent, String agentEmailAddress )
        throws InvalidInputException
    {
        if ( agent == null ) {
            throw new InvalidInputException( "Null parameter user passed " );
        }

        if ( agentEmailAddress == null ) {
            throw new InvalidInputException( "agentEmailAddress passed cannot be null" );
        }
        LOG.info( "Method to update updateAgentIdOfPreInitiatedSurveys started." );
        String queryStr = "UPDATE SURVEY_PRE_INITIATION SET STATUS = "
            + CommonConstants.STATUS_SURVEYPREINITIATION_NOT_PROCESSED + ", MODIFIED_ON=? WHERE LCASE(AGENT_EMAILID) = LCASE(?) AND STATUS IN ("
            + CommonConstants.STATUS_SURVEYPREINITIATION_MISMATCH_RECORD + ", "
            + CommonConstants.STATUS_SURVEYPREINITIATION_IGNORED_RECORD + ") ";
        Query query = getSession().createSQLQuery( queryStr );
        query.setParameter( 0, new Timestamp( System.currentTimeMillis() ) );
        query.setParameter( 1, agentEmailAddress );

        query.executeUpdate();
        LOG.info( "Method updateAgentIdOfPreInitiatedSurveys  ended." );
    }


    @Override
    public void updateSurveyPreinitiationRecordsAsIgnored( String agentEmailAddress ) throws InvalidInputException
    {
        if ( agentEmailAddress == null ) {
            throw new InvalidInputException( "agentEmailAddress passed cannot be null" );
        }
        LOG.info( "Method to update updateSurveyPreinitiationRecordsAsIgnored started." );
        String queryStr = "UPDATE SURVEY_PRE_INITIATION SET  STATUS = "
            + CommonConstants.STATUS_SURVEYPREINITIATION_IGNORED_RECORD + ", MODIFIED_ON=?  WHERE AGENT_EMAILID = ? AND STATUS = "
            + CommonConstants.STATUS_SURVEYPREINITIATION_MISMATCH_RECORD;
        Query query = getSession().createSQLQuery( queryStr );
        query.setParameter( 0, new Timestamp( System.currentTimeMillis() ) );
        query.setParameter( 1, agentEmailAddress );
        query.executeUpdate();
        LOG.info( "Method updateSurveyPreinitiationRecordsAsIgnored  ended." );
    }


    @Override
    public Map<Long, Date> getLatestSurveySentForAgent( long companyId )
    {
        LOG.info( "Method getLatestSurveySentForAgent() started." );
        Criteria criteria = getSession().createCriteria( SurveyPreInitiation.class );
        Map<Long, Date> latestSurveySentForAgent = new HashMap<Long, Date>();
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add( CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED );
        statusList.add( CommonConstants.SURVEY_STATUS_INITIATED );
        statusList.add( CommonConstants.STATUS_SURVEYPREINITIATION_COMPLETE );

        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.in( CommonConstants.STATUS_COLUMN, statusList ) );

            criteria.setProjection( Projections.projectionList()
                .add( Projections.groupProperty( CommonConstants.AGENT_ID_COLUMN ) )
                .add( Projections.max( CommonConstants.CREATED_ON ) ) );

            List<Object[]> result = criteria.list();
            for ( Object[] obj : result ) {
                Long agentId = (Long) obj[0];
                Date latestSurveyDate = (Date) obj[1];

                latestSurveySentForAgent.put( agentId, latestSurveyDate );
            }
            LOG.info( "Method getLatestSurveySentForAgent() finished." );

        } catch ( HibernateException e ) {
            LOG.error( "Exception caught in getLatestSurveySentForAgent() ", e );
            throw new DatabaseException( "Exception caught in getLatestSurveySentForAgent() ", e );
        }

        return latestSurveySentForAgent;
    }


    // Method to get list of incomplete surveys to display in Dash board and profile page.
    @SuppressWarnings ( "unchecked")
    @Override
    @Transactional
    public List<SurveyPreInitiation> getValidSurveyByAgentIdAndCustomeEmailForPastNDays( long agentId, String customerEmail,
        int noOfDays ) throws DatabaseException
    {
        LOG.info( "Method getSurveyByAgentIdAndCustomeEmailForPastNDays() started." );
        Criteria criteria = getSession().createCriteria( SurveyPreInitiation.class );
        try {
            //agent id
            criteria.add( Restrictions.eq( CommonConstants.AGENT_ID_COLUMN, agentId ) );
            //customer Email
            criteria.add( Restrictions.eq( CommonConstants.CUSTOMER_EMAIL_ID_KEY_COLUMN, customerEmail ) );


            //days criteria
            if ( noOfDays > 0 ) {

                Calendar startTime = Calendar.getInstance();
                startTime.add( Calendar.DATE, -1 * noOfDays );
                // strip the time component of start time
                startTime.set( Calendar.HOUR_OF_DAY, 0 );
                startTime.set( Calendar.MINUTE, 0 );
                startTime.set( Calendar.SECOND, 0 );
                startTime.set( Calendar.MILLISECOND, 0 );

                Timestamp startDate = new Timestamp( startTime.getTimeInMillis() );

                criteria.add( Restrictions.ge( CommonConstants.CREATED_ON, startDate ) );
            }
            
            //status criteria
            List<Integer> statusList = new ArrayList<Integer>();
            statusList.add( CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED );
            statusList.add( CommonConstants.SURVEY_STATUS_INITIATED );
            statusList.add( CommonConstants.STATUS_SURVEYPREINITIATION_COMPLETE );
            
            criteria.add( Restrictions.in( CommonConstants.STATUS_COLUMN,  statusList ) );
            
            
            LOG.info( "Method getSurveyByAgentIdAndCustomeEmailForPastNDays() finished." );
            return criteria.list();
        } catch ( HibernateException e ) {
            LOG.error( "Exception caught in getSurveyByAgentIdAndCustomeEmailForPastNDays() ", e );
            throw new DatabaseException( "Exception caught in getSurveyByAgentIdAndCustomeEmailForPastNDays() ", e );
        }

    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<SurveyPreInitiation> getValidSurveyByAgentIdAndCustomeEmail( long agentId, String customerEmail )
        throws DatabaseException
    {
        LOG.debug( "Method getSurveyByAgentIdAndCustomeEmail() started." );
        Criteria criteria = getSession().createCriteria( SurveyPreInitiation.class );
        try {
            //agent id
            criteria.add( Restrictions.eq( CommonConstants.AGENT_ID_COLUMN, agentId ) );
            //customer Email
            criteria.add( Restrictions.eq( CommonConstants.CUSTOMER_EMAIL_ID_KEY_COLUMN, customerEmail ) );
            //status criteria
            List<Integer> statusList = new ArrayList<Integer>();
            statusList.add( CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED );
            statusList.add( CommonConstants.SURVEY_STATUS_INITIATED );
            statusList.add( CommonConstants.STATUS_SURVEYPREINITIATION_COMPLETE );
            
            criteria.add( Restrictions.in( CommonConstants.STATUS_COLUMN,  statusList ) );
            LOG.debug( "Method getSurveyByAgentIdAndCustomeEmail() finihed." );
            return criteria.list();
        } catch ( HibernateException e ) {
            LOG.error( "Exception caught in getSurveyByAgentIdAndCustomeEmailForPastNDays() ", e );
            throw new DatabaseException( "Exception caught in getSurveyByAgentIdAndCustomeEmailForPastNDays() ", e );
        }
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<SurveyPreInitiation> getCorruptPreInitiatedSurveys( long companyId, int start, int batch )
    {
        LOG.info( "Method getCorruptPreInitiatedSurveys() started." );
        Criteria criteria = getSession().createCriteria( SurveyPreInitiation.class, "surveyPreInitiation" );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN,
                CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD ) );
            criteria.addOrder( Order.desc( CommonConstants.ENGAGEMENT_CLOSED_TIME ) );
            if ( start > -1 )
                criteria.setFirstResult( start );
            if ( batch > -1 )
                criteria.setMaxResults( batch );
            LOG.info( "Method getCorruptPreInitiatedSurveys() finished." );
            return criteria.list();
        } catch ( HibernateException e ) {
            LOG.error( "Exception caught in getCorruptPreInitiatedSurveys() ", e );
            throw new DatabaseException( "Exception caught in getCorruptPreInitiatedSurveys() ", e );
        }
    }


    @Override
    public long getCorruptPreInitiatedSurveyCount( long companyId )
    {
        LOG.info( "Method getCorruptPreInitiatedSurveyCount() started." );
        Criteria criteria = getSession().createCriteria( SurveyPreInitiation.class, "surveyPreInitiation" );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN,
                CommonConstants.STATUS_SURVEYPREINITIATION_CORRUPT_RECORD ) );
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.info( "Method getCorruptPreInitiatedSurveyCount() finished." );
            return count.longValue();
        } catch ( HibernateException e ) {
            LOG.error( "Exception caught in getCorruptPreInitiatedSurveyCount() ", e );
            throw new DatabaseException( "Exception caught in getCorruptPreInitiatedSurveyCount() ", e );

        }
    }
    
    
    
    @SuppressWarnings ( "unchecked")
    @Override
    public Map<Long, SurveyPreInitiation> getPreInitiatedSurveyForIds( List<Long> surveyPreinitiationIds )
    {
        LOG.info( "Method getPreInitiatedSurveyForIds() started." );
        Map<Long, SurveyPreInitiation> surveyPreinitiationMap = new HashMap<Long, SurveyPreInitiation>();
        
        Criteria criteria = getSession().createCriteria( SurveyPreInitiation.class, "surveyPreInitiation" );

        criteria.add( Restrictions.in( CommonConstants.SURVEY_PREINITIATION_ID_COLUMN, surveyPreinitiationIds ) );
        LOG.info( "Method getUnmatchedPreInitiatedSurveyCount() finished." );
        List<SurveyPreInitiation> surveyPreInitiationList = criteria.list();

        for(SurveyPreInitiation surveyPreInitiation : surveyPreInitiationList){
            surveyPreinitiationMap.put( surveyPreInitiation.getSurveyPreIntitiationId(), surveyPreInitiation );
        }

        return surveyPreinitiationMap;
    }

    
    @Override
    public List<SurveyPreInitiation> getPreInitiatedSurveyForCompanyByCriteria( int start, int row, List<Long> userIds  , Long startSurveyPreinitiationId, Timestamp startEngagementClosedTime , long companyId )
    {
        Criteria criteria = getSession().createCriteria( SurveyPreInitiation.class );


        if ( row > 0 )
            criteria.setMaxResults( row );
        if ( start > 0 )
            criteria.setFirstResult( start );

        criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
        criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED ) );
        
        if(userIds != null){
            criteria.add( Restrictions.in( CommonConstants.AGENT_ID_COLUMN, userIds ) );
        }
        
        if(startEngagementClosedTime != null){
            criteria.add( Restrictions.ge( CommonConstants.ENGAGEMENT_CLOSED_TIME, startEngagementClosedTime ) );
        }
        
        if(startSurveyPreinitiationId != null && startSurveyPreinitiationId > 0){
            criteria.add( Restrictions.ge( CommonConstants.SURVEY_PREINITIATION_ID_COLUMN, startSurveyPreinitiationId ) );
        }
        
        criteria.addOrder( Order.desc( CommonConstants.MODIFIED_ON_COLUMN ) );
        return criteria.list();

    }
    
    
    @Override
    public void updateCompanyIdForAllRecordsForAgent( String agentEmailId , long companyId)
    {
        
        LOG.debug( "Method updateCompanyIdForAllRecordsForAgent started." );
        String queryStr = "UPDATE SURVEY_PRE_INITIATION SET  COMPANY_ID = ?, MODIFIED_ON=? WHERE AGENT_EMAILID = ?";
        Query query = getSession().createSQLQuery( queryStr );
        query.setParameter( 0, companyId );
        query.setParameter( 1, new Timestamp( System.currentTimeMillis() ) );
        query.setParameter( 2, agentEmailId );
        LOG.info( "query to update company id is " + query.toString() );
        query.executeUpdate();
        LOG.debug( "Method updateCompanyIdForAllRecordsForAgent  ended." );
    }
    
    
    @Override
    public void disconnectSurveysFromAgent( long agentId)
    {
        
        LOG.debug( "Method updateCompanyIdForAllRecordsForAgent started." );
        String queryStr = "UPDATE SURVEY_PRE_INITIATION SET  AGENT_ID = 0, MODIFIED_ON=? WHERE AGENT_ID = ?";
        Query query = getSession().createSQLQuery( queryStr );
        query.setParameter( 0, new Timestamp( System.currentTimeMillis() ) );
        query.setParameter( 1, agentId );
        LOG.info( "query to update company id is " + query.toString() );
        query.executeUpdate();
        LOG.debug( "Method updateCompanyIdForAllRecordsForAgent  ended." );
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getReceivedCountForDate(String startDate, String endDate, int startIndex, int batchSize) {
		String queryStr = "select u.USER_ID,count(spi.SURVEY_PRE_INITIATION_ID) as c,u.COMPANY_ID,"
				+ "concat(u.FIRST_NAME,' ',coalesce(u.LAST_NAME,'')) as agent_name,u.EMAIL_ID from USERS u "
				+ "left join SURVEY_PRE_INITIATION spi on u.user_id=spi.agent_id and u.status in (1,2) "
				+ "and spi.survey_source not in ('3rd Party Review') and spi.created_on between :startdate and :endDate "
				+ "and spi.agent_id != 0 group by u.user_id";
		Query query = getSession().createSQLQuery(queryStr);
		query.setParameter("startdate", startDate);
		query.setParameter("endDate", endDate);
		query.setFirstResult(startIndex);
		query.setFetchSize(batchSize);
		LOG.debug("query to fetch data for email report : {}", query.toString());
		return query.list();
	}
}
