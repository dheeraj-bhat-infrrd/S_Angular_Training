package com.realtech.socialsurvey.core.dao.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyResultsCompanyReportDao;
import com.realtech.socialsurvey.core.entities.SurveyResponseTable;
import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Repository
public class SurveyResultsCompanyReportDaoImpl extends GenericReportingDaoImpl<SurveyResultsCompanyReport, String>
		implements SurveyResultsCompanyReportDao {

	private static final Logger LOG = LoggerFactory.getLogger(SurveyResultsCompanyReportDaoImpl.class);

	/*
	 * The limit is applied to the surveyResults table and a left outer join to
	 * survey response so the surveys like zillow who dont have a response are
	 * not missed
	 */
	private static final String GET_SURVEY_RESULT_ALL_TIME_BY_COMPANY_ID_QUERY = "select ab.SURVEY_DETAILS_ID,sr.answer,ab.USER_FIRST_NAME,ab.USER_LAST_NAME,ab.CUSTOMER_FIRST_NAME,ab.CUSTOMER_LAST_NAME,ab.SURVEY_SENT_DATE,"
        + "ab.SURVEY_COMPLETED_DATE,ab.TIME_INTERVAL,ab.SURVEY_SOURCE,ab.SURVEY_SOURCE_ID,ab.SURVEY_SCORE,ab.GATEWAY,ab.CUSTOMER_COMMENTS,"
        + "ab.AGREED_TO_SHARE,ab.BRANCH_NAME,ab.CLICK_THROUGH_FOR_COMPANY,ab.CLICK_THROUGH_FOR_AGENT,ab.CLICK_THROUGH_FOR_REGION,ab.CLICK_THROUGH_FOR_BRANCH,"
        + "ab.PARTICIPANT_TYPE,ab.AGENT_EMAILID,ab.CUSTOMER_EMAIL_ID,ab.STATE,ab.CITY "
        + "from (select srcr.SURVEY_DETAILS_ID,srcr.USER_FIRST_NAME,srcr.USER_LAST_NAME,srcr.CUSTOMER_FIRST_NAME,srcr.CUSTOMER_LAST_NAME,srcr.SURVEY_SENT_DATE,"
        + "srcr.SURVEY_COMPLETED_DATE,srcr.TIME_INTERVAL,srcr.SURVEY_SOURCE,srcr.SURVEY_SOURCE_ID,srcr.SURVEY_SCORE,srcr.GATEWAY,srcr.CUSTOMER_COMMENTS,"
        + "srcr.AGREED_TO_SHARE,srcr.BRANCH_NAME,srcr.CLICK_THROUGH_FOR_COMPANY,srcr.CLICK_THROUGH_FOR_AGENT,srcr.CLICK_THROUGH_FOR_REGION,srcr.CLICK_THROUGH_FOR_BRANCH,"
        + "srcr.PARTICIPANT_TYPE,srcr.AGENT_EMAILID,srcr.CUSTOMER_EMAIL_ID,srcr.STATE,srcr.CITY "
        + "from survey_results_company_report srcr :idCondition :dateCondition limit :startIndex , :batchSize ) as ab "
        + "left outer join survey_response sr on ab.SURVEY_DETAILS_ID = sr.SURVEY_DETAILS_ID "
        + "order by sr.SURVEY_DETAILS_ID, sr.QUESTION_ID ";
    
	private static final String COMPANY_ID_CONDITION = " where srcr.COMPANY_ID= :entityId ";
    private static final String REGION_ID_CONDITION = " where srcr.AGENT_ID in (select distinct up.USER_ID from user_profile up where up.region_id= :entityId ) ";
    private static final String BRANCH_ID_CONDITION = " where srcr.AGENT_ID in (select distinct up.USER_ID from user_profile up where up.branch_id= :entityId ) ";
    private static final String AGENT_ID_CONDITION = " where srcr.AGENT_ID= :entityId ";
    
    private static final String START_DATE_CONDITION = " and srcr.SURVEY_COMPLETED_DATE >= :startDate ";
    private static final String END_DATE_CONDITION = " and srcr.SURVEY_COMPLETED_DATE <= :endDate ";
    private static final String START_AND_END_DATE_CONDITION = " and srcr.SURVEY_COMPLETED_DATE >= :startDate and srcr.SURVEY_COMPLETED_DATE <= :endDate ";
    
    
    @SuppressWarnings ( "unchecked")
    @Override
    public Map<String, SurveyResultsCompanyReport> getSurveyResultForEntityTypeId( String entityType, Long entityId, Timestamp startDate,
        Timestamp endDate, int startIndex, int batchSize )
    {

        String finalQuery = GET_SURVEY_RESULT_ALL_TIME_BY_COMPANY_ID_QUERY;

        switch ( entityType ) {
            case CommonConstants.COMPANY_ID:
                finalQuery = finalQuery.replace( ":idCondition", COMPANY_ID_CONDITION );
                break;
            case CommonConstants.REGION_ID:
                finalQuery = finalQuery.replace( ":idCondition", REGION_ID_CONDITION );
                break;
            case CommonConstants.BRANCH_ID:
                finalQuery = finalQuery.replace( ":idCondition", BRANCH_ID_CONDITION );
                break;
            case CommonConstants.AGENT_ID:
                finalQuery = finalQuery.replace( ":idCondition", AGENT_ID_CONDITION );
                break;
            default:
                finalQuery.replace( ":idCondition", "" );
                break;
        }
        Query query = null;
        try {

            if ( startDate != null && endDate != null ) {
                finalQuery = finalQuery.replace( ":dateCondition", START_AND_END_DATE_CONDITION );
                query = getSession().createSQLQuery( finalQuery );
                query.setParameter( "startDate", startDate );
                query.setParameter( "endDate", endDate );
            } else if ( startDate != null && endDate == null ) {
                finalQuery = finalQuery.replace( ":dateCondition", START_DATE_CONDITION );
                query = getSession().createSQLQuery( finalQuery );
                query.setParameter( "startDate", startDate );
            } else if ( startDate == null && endDate != null ) {
                finalQuery = finalQuery.replace( ":dateCondition", END_DATE_CONDITION );
                query = getSession().createSQLQuery( finalQuery );
                query.setParameter( "endDate", endDate );
            } else {
                finalQuery = finalQuery.replace( ":dateCondition", "" );
                query = getSession().createSQLQuery( finalQuery );
            }
            query.setParameter( "entityId", entityId );
            query.setParameter( "startIndex", startIndex );
            query.setParameter( "batchSize", batchSize );

            LOG.debug( "QUERY : " + query.getQueryString() );
            //Run the query.
            List<Object[]> rows = (List<Object[]>) query.list();

            Map<String, SurveyResultsCompanyReport> surveyResultMap = new HashMap<>();

            // map the answer to the survey details id
            for ( Object[] row : rows ) {
                SurveyResponseTable surveyResponseTable = new SurveyResponseTable();
                surveyResponseTable.setAnswer( String.valueOf( row[1] ) );
                List<SurveyResponseTable> surveyResponseList = new ArrayList<>();
                SurveyResultsCompanyReport surveyResultsCompanyReport = null;
                String surveyDetailsId = String.valueOf( row[0] );

                if ( surveyResultMap.get( surveyDetailsId ) != null ) {
                    surveyResultsCompanyReport = surveyResultMap.get( surveyDetailsId );
                    surveyResponseList = surveyResultsCompanyReport.getSurveyResponseList();
                    surveyResponseList.add( surveyResponseTable );
                    surveyResultsCompanyReport.setSurveyResponseList( surveyResponseList );

                } else {
                    surveyResultsCompanyReport = new SurveyResultsCompanyReport();
                    surveyResponseList.add( surveyResponseTable );
                    surveyResultsCompanyReport.setSurveyResponseList( surveyResponseList );
                    surveyResultsCompanyReport.setSurveyDetailsId( String.valueOf( row[0] ) );
                    surveyResultsCompanyReport.setUserFirstName( String.valueOf( row[2] ) );
                    surveyResultsCompanyReport.setUserLastName( String.valueOf( row[3] ) );
                    surveyResultsCompanyReport.setCustomerFirstName( String.valueOf( row[4] ) );
                    surveyResultsCompanyReport.setCustomerLastName( String.valueOf( row[5] ) );
                    if(row[6]!= null) {
                        surveyResultsCompanyReport.setSurveySentDate( (Timestamp) ( row[6] ) );
                        surveyResultsCompanyReport.setTimeInterval( (Integer) ( row[8] ) );
                    }
                    surveyResultsCompanyReport.setSurveyCompletedDate( (Timestamp) ( row[7] ) );
                    surveyResultsCompanyReport.setSurveySource( String.valueOf( row[9] ) );
                    surveyResultsCompanyReport.setSurveySourceId( String.valueOf( row[10] ) );
                    surveyResultsCompanyReport.setSurveyScore( ( (BigDecimal) ( row[11] ) ).doubleValue() );
                    surveyResultsCompanyReport.setGateway( String.valueOf( row[12] ) );
                    surveyResultsCompanyReport.setCustomerComments( String.valueOf( row[13] ) );
                    surveyResultsCompanyReport.setAgreedToShare( String.valueOf( row[14] ) );
                    surveyResultsCompanyReport.setBranchName( String.valueOf( row[15] ) );
                    surveyResultsCompanyReport.setClickTroughForCompany( String.valueOf( row[16] ) );
                    surveyResultsCompanyReport.setClickTroughForAgent( String.valueOf( row[17] ) );
                    surveyResultsCompanyReport.setClickTroughForRegion( String.valueOf( row[18] ) );
                    surveyResultsCompanyReport.setClickTroughForBranch( String.valueOf( row[19] ) );
                    surveyResultsCompanyReport.setParticipantType( String.valueOf( row[20] ) );
                    surveyResultsCompanyReport.setAgentEmailId( String.valueOf( row[21] ) );
                    surveyResultsCompanyReport.setCustomerEmailId( String.valueOf( row[22] ) );
                    surveyResultsCompanyReport.setState( String.valueOf( row[23] ) );
                    surveyResultsCompanyReport.setCity( String.valueOf( row[24] ) );
                }
                surveyResultMap.put( surveyDetailsId, surveyResultsCompanyReport );
            }
            return surveyResultMap;
        } catch ( Exception hibernateException ) {
            LOG.error( "Exception caught in getSurveyResultForCompanyId() ", hibernateException );
            throw new DatabaseException( "Exception caught in getSurveyResultForCompanyId() ", hibernateException );
        }
    }
}