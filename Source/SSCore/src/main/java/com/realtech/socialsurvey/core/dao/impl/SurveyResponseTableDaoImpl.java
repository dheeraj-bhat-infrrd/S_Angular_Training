package com.realtech.socialsurvey.core.dao.impl;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyResponseTableDao;
import com.realtech.socialsurvey.core.entities.SurveyResponseTable;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class SurveyResponseTableDaoImpl extends GenericReportingDaoImpl<SurveyResponseTable, String> implements SurveyResponseTableDao{

	private static final Logger LOG = LoggerFactory.getLogger( SurveyResponseTableDaoImpl.class );
	
	// For Company
	private static final String GET_MAX_RESPONSE_BASE_QUERY = "select count(sr.QUESTION_ID) as qno from "+
	    "survey_results_company_report srcr inner join survey_response sr on srcr.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
	    " :idCondition :dateCondition "+
	    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
	
	private static final String COMPANY_ID_CONDITION = " where srcr.COMPANY_ID= :entityId ";
    private static final String REGION_ID_CONDITION = " where srcr.AGENT_ID in (select distinct up.USER_ID from user_profile up where up.region_id= :entityId ) ";
    private static final String BRANCH_ID_CONDITION = " where srcr.AGENT_ID in (select distinct up.USER_ID from user_profile up where up.branch_id= :entityId ) ";
    private static final String AGENT_ID_CONDITION = " where srcr.AGENT_ID= :entityId ";
    
    private static final String START_DATE_CONDITION = " and srcr.SURVEY_COMPLETED_DATE >= :startDate ";
    private static final String END_DATE_CONDITION = " and srcr.SURVEY_COMPLETED_DATE <= :endDate ";
    private static final String START_AND_END_DATE_CONDITION = " and srcr.SURVEY_COMPLETED_DATE >= :startDate and srcr.SURVEY_COMPLETED_DATE <= :endDate ";
    
    @Override
    public int getMaxQuestion(long entityId, String entityType, Timestamp startDate, Timestamp endDate) {
        String finalQuery = GET_MAX_RESPONSE_BASE_QUERY;

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

            LOG.debug( "QUERY : " + query.getQueryString() );
            //Run the query.
            BigInteger maxQuestion = (BigInteger) query.uniqueResult();
            if(maxQuestion != null){
                return maxQuestion.intValue();
            } else {
                return 0;
            }
        }catch ( Exception hibernateException ) {
            LOG.error( "Exception caught in getMaxResponseForCompanyId() ", hibernateException );
            throw new DatabaseException( "Exception caught in getMaxResponseForCompanyId() ", hibernateException );
        }
    }
}