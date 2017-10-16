package com.realtech.socialsurvey.core.dao.impl;

import java.math.BigInteger;
import java.sql.Timestamp;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.dao.SurveyResponseTableDao;
import com.realtech.socialsurvey.core.entities.SurveyResponseTable;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class SurveyResponseTableDaoImpl extends GenericReportingDaoImpl<SurveyResponseTable, String> implements SurveyResponseTableDao{

	private static final Logger LOG = LoggerFactory.getLogger( SurveyResponseTableDaoImpl.class );
	
	private static final String getAllTimeMaxResponse = "select count(sr.QUESTION_ID) as qno from "+
	    "survey_results_company_report src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
	    "where src.COMPANY_ID = ? "+
	    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
	private static final String getMaxResponseByStartDate = "select count(sr.QUESTION_ID) as qno from "+
	    "survey_results_company_report src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
	    "where src.COMPANY_ID = ? and src.SURVEY_COMPLETED_DATE >= ? "+
	    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
	
	private static final String getMaxResponseByEndDate = "select count(sr.QUESTION_ID) as qno from "+
	    "survey_results_company_report src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
	    "where src.COMPANY_ID = ? and src.SURVEY_COMPLETED_DATE <= ? "+
	    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
	
	private static final String getMaxResponseByStartAndEndDate = "select count(sr.QUESTION_ID) as qno from "+
	    "survey_results_company_report src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
	    "where src.COMPANY_ID = ? and src.SURVEY_COMPLETED_DATE >= ? and src.SURVEY_COMPLETED_DATE <= ? "+
	    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";

    @Override
    @Transactional(value = "transactionManagerForReporting")
    public int getMaxResponseForCompanyId(long companyId , Timestamp startDate , Timestamp endDate )
    {
	    LOG.debug( "Method getMaxResponseForCompanyId started for CompanyId : " + companyId );
        Query query = null ;
        try{
            if(startDate != null && endDate != null){
                query = getSession().createSQLQuery( getMaxResponseByStartAndEndDate );
                query.setParameter( 1, startDate );
                query.setParameter( 2, endDate );
            }else if(startDate != null && endDate == null){
                query = getSession().createSQLQuery( getMaxResponseByStartDate );
                query.setParameter( 1, startDate );
            }else if(startDate == null && endDate != null){
                query = getSession().createSQLQuery( getMaxResponseByEndDate );
                query.setParameter( 1, endDate );
            }else if( startDate == null && endDate == null){
                query = getSession().createSQLQuery( getAllTimeMaxResponse );
            }
            query.setParameter( 0, companyId );
            LOG.debug( "QUERY : " + query.getQueryString() );
            return ( (BigInteger) query.uniqueResult() ).intValue();
        } catch ( Exception hibernateException ) {
            LOG.error( "Exception caught in getMaxResponseForCompanyId() ", hibernateException );
            throw new DatabaseException( "Exception caught in getMaxResponseForCompanyId() ", hibernateException );
        }
    }
	
 
}
