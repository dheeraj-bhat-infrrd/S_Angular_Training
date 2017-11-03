package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ReportingSurveyPreInititationDao;
import com.realtech.socialsurvey.core.entities.ReportingSurveyPreInititation;


@Repository
public class ReportingSurveyPreInititationDaoImpl extends GenericReportingDaoImpl<ReportingSurveyPreInititation, String>
    implements ReportingSurveyPreInititationDao
{
    private static final Logger LOG = LoggerFactory.getLogger( ReportingSurveyPreInititationDaoImpl.class );

    private static final String MAIN_SELECT_QUERY = "select u.FIRST_NAME,u.LAST_NAME,u.EMAIL_ID,spi.CUSTOMER_FIRST_NAME,"
        + "spi.CUSTOMER_LAST_NAME,spi.CUSTOMER_EMAIL_ID,spi.SURVEY_SOURCE_ID,spi.SURVEY_SOURCE,spi.CREATED_ON_EST,"
        + "spi.REMINDER_COUNTS,spi.LAST_REMINDER_TIME_EST from survey_pre_initiation spi inner join user_profile up "
        + "on spi.AGENT_ID = up.AGENT_ID inner join users u on up.AGENT_ID = u.USER_ID "
        + "where spi.STATUS in (1,2) :entityIdCondition :dateCondition limit :startIndex , :batchSize";
    //entityId condition Strings 
    private static final String COMPANY_ID_CONDITION = " and up.COMPANY_ID= :entityId";
    private static final String REGION_ID_CONDITION = " and up.REGION_ID= :entityId";
    private static final String BRANCH_ID_CONDITION = " and up.BRANCH_ID= :entityId";
    private static final String AGENT_ID_CONDITION = " and up.AGENT_ID= :entityId";
    //Date condition strings
    private static final String START_DATE_CONDITION = " and spi.CREATED_ON_EST >= :startDate";
    private static final String END_DATE_CONDITION = " and spi.CREATED_ON_EST <= :endDate";
    private static final String START_AND_END_DATE_CONDITION = " and spi.CREATED_ON_EST >= :startDate and spi.CREATED_ON_EST <= :endDate";


    @SuppressWarnings ( "unchecked")
    @Override
    public List<ReportingSurveyPreInititation> getIncompleteSurveyForReporting( String entityType, long entityId,
        Timestamp startDate, Timestamp endDate, int startIndex, int batchSize )
    {
        List<ReportingSurveyPreInititation> incompleteSurveyReportData = new ArrayList<ReportingSurveyPreInititation>();
        List<Object[]> object = null;
        try {
            Query query = createFinalQuery( entityType, entityId, startDate, endDate, startIndex, batchSize );
            //Get the result set.
            object = query.list();
        } catch ( Exception hibernateException ) {
            LOG.error( "Exception caught in getIncompleteSurveyForReporting() while getting the result.", hibernateException );
        }
        for ( Object[] row : object ) {
            ReportingSurveyPreInititation reportingSurveyPreInititation = new ReportingSurveyPreInititation();
            reportingSurveyPreInititation.setAgentFirstName( row[0] == null ? "" : (String)row[0] );
            reportingSurveyPreInititation.setAgentLastName( row[1] == null ? "" : (String)row[1] );
            reportingSurveyPreInititation.setAgentEmailId( row[2] == null ? "" : (String)row[2] );
            reportingSurveyPreInititation.setCustomerFirstName( row[3] == null ? "" : (String)row[3] );
            reportingSurveyPreInititation.setCustomerLastName( row[4] == null ? "" : (String)row[4] );
            reportingSurveyPreInititation.setCustomerEmailId( row[5] == null ? "" : (String)row[5] );
            reportingSurveyPreInititation.setSurveySourceId( row[6] == null ? "" : (String)row[6] );
            reportingSurveyPreInititation.setSurveySource( row[7] == null ? "" : (String)row[7] );
            reportingSurveyPreInititation.setCreatedOnEst( (String) row[8] );
            reportingSurveyPreInititation.setReminderCounts( (Integer) row[9] );
            reportingSurveyPreInititation.setLastReminderTimeEst( (String) row[10] );
            incompleteSurveyReportData.add( reportingSurveyPreInititation );
        }
        return incompleteSurveyReportData;
    }

    /**
     * This method replaces the values in the query string, generates the query object and returns the same.
     * @param entityType
     * @param entityId
     * @param startDate
     * @param endDate
     * @param startIndex
     * @param batchSize
     * @return
     * @throws Exception
     */
    private Query createFinalQuery( String entityType, long entityId, Timestamp startDate, Timestamp endDate, int startIndex,
        int batchSize ) throws Exception
    {
        String finalQueryString = getFinalQueryString( entityType, startDate, endDate );
        Query query = getSession().createSQLQuery( finalQueryString );
        if ( finalQueryString.indexOf( ":entityId" ) > 0 ) {
            query.setParameter( "entityId", entityId );
        }
        if ( finalQueryString.indexOf( ":startDate" ) > 0 ) {
            query.setParameter( "startDate", startDate );
        }
        if ( finalQueryString.indexOf( ":endDate" ) > 0 ) {
            query.setParameter( "endDate", endDate );
        }
        query.setParameter( "startIndex", startIndex );
        query.setParameter( "batchSize", batchSize );
        return query;
    }


    /**
     * This method constructs the required query string.
     * @param entityType
     * @param startDate
     * @param endDate
     * @return
     */
    private String getFinalQueryString( String entityType, Timestamp startDate, Timestamp endDate )
    {
        String queryString = MAIN_SELECT_QUERY;

        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            queryString = MAIN_SELECT_QUERY.replace( ":entityIdCondition", COMPANY_ID_CONDITION );
        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            queryString = MAIN_SELECT_QUERY.replace( ":entityIdCondition", REGION_ID_CONDITION );
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            queryString = MAIN_SELECT_QUERY.replace( ":entityIdCondition", BRANCH_ID_CONDITION );
        } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
            queryString = MAIN_SELECT_QUERY.replace( ":entityIdCondition", AGENT_ID_CONDITION );
        }

        //Set start and end Date value as per the condition.
        if ( startDate != null && endDate != null ) {
            queryString = queryString.replace( ":dateCondition", START_AND_END_DATE_CONDITION );
        } else if ( startDate != null && endDate == null ) {
            queryString = queryString.replace( ":dateCondition", START_DATE_CONDITION );
        } else if ( startDate == null && endDate != null ) {
            queryString = queryString.replace( ":dateCondition", END_DATE_CONDITION );
        } else {
            queryString = queryString.replace( ":dateCondition", "" );
        }
        return queryString;
    }
}
