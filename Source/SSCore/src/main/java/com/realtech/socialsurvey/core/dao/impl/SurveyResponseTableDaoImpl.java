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
	
	// For Company
	private static final String GET_ALL_TIME_MAX_RESPONSE_COMPANY = "select count(sr.QUESTION_ID) as qno from "+
	    "survey_results_company_report src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
	    "where src.COMPANY_ID = ? "+
	    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
	private static final String GET_MAX_RESPONSE_BY_START_DATE_COMPANY = "select count(sr.QUESTION_ID) as qno from "+
	    "survey_results_company_report src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
	    "where src.COMPANY_ID = ? and src.SURVEY_COMPLETED_DATE >= ? "+
	    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
	
	private static final String GET_MAX_RESPONSE_BY_END_DATE_COMPANY = "select count(sr.QUESTION_ID) as qno from "+
	    "survey_results_company_report src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
	    "where src.COMPANY_ID = ? and src.SURVEY_COMPLETED_DATE <= ? "+
	    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
	
	private static final String GET_MAX_RESPONSE_BY_START_AND_END_DATE_COMPANY = "select count(sr.QUESTION_ID) as qno from "+
	    "survey_results_company_report src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
	    "where src.COMPANY_ID = ? and src.SURVEY_COMPLETED_DATE >= ? and src.SURVEY_COMPLETED_DATE <= ? "+
	    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";

	//For Region
	private static final String GET_ALL_TIME_MAX_RESPONSE_REGION = "select count(sr.QUESTION_ID) as qno from "+
		    "survey_results_report_region src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
		    "where src.REGION_ID = ? "+
		    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
	private static final String GET_MAX_RESPONSE_BY_START_DATE_REGION = "select count(sr.QUESTION_ID) as qno from "+
		    "survey_results_report_region src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
		    "where src.REGION_ID = ? and src.SURVEY_COMPLETED_DATE >= ? "+
		    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
		
	private static final String GET_MAX_RESPONSE_BY_END_DATE_REGION = "select count(sr.QUESTION_ID) as qno from "+
		    "survey_results_report_region src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
		    "where src.REGION_ID = ? and src.SURVEY_COMPLETED_DATE <= ? "+
		    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
		
	private static final String GET_MAX_RESPONSE_BY_START_AND_END_DATE_REGION = "select count(sr.QUESTION_ID) as qno from "+
		    "survey_results_report_region src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
		    "where src.REGION_ID = ? and src.SURVEY_COMPLETED_DATE >= ? and src.SURVEY_COMPLETED_DATE <= ? "+
		    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
		
	//For Branch
	
	private static final String GET_ALL_TIME_MAX_RESPONSE_BRANCH = "select count(sr.QUESTION_ID) as qno from "+
		    "urvey_results_report_branch src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
		    "where src.BRANCH_ID = ? "+
		    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
	private static final String GET_MAX_RESPONSE_BY_START_DATE_BRANCH = "select count(sr.QUESTION_ID) as qno from "+
		    "urvey_results_report_branch src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
		    "where src.BRANCH_ID = ? and src.SURVEY_COMPLETED_DATE >= ? "+
		    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
		
	private static final String GET_MAX_RESPONSE_BY_END_DATE_BRANCH = "select count(sr.QUESTION_ID) as qno from "+
		    "urvey_results_report_branch src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
		    "where src.BRANCH_ID = ? and src.SURVEY_COMPLETED_DATE <= ? "+
		    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
		
	private static final String GET_MAX_RESPONSE_BY_START_AND_END_DATE_BRANCH = "select count(sr.QUESTION_ID) as qno from "+
		    "survey_results_report_branch src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
		    "where src.BRANCH_ID = ? and src.SURVEY_COMPLETED_DATE >= ? and src.SURVEY_COMPLETED_DATE <= ? "+
		    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
		
	//For User
		
	private static final String GET_ALL_TIME_MAX_RESPONSE_USER = "select count(sr.QUESTION_ID) as qno from "+
			    "survey_results_company_report src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
			    "where src.AGENT_ID = ? "+
			    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
	private static final String GET_MAX_RESPONSE_BY_START_DATE_USER = "select count(sr.QUESTION_ID) as qno from "+
			    "survey_results_company_report src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
			    "where src.AGENT_ID = ? and src.SURVEY_COMPLETED_DATE >= ? "+
			    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
			
	private static final String GET_MAX_RESPONSE_BY_END_DATE_USER = "select count(sr.QUESTION_ID) as qno from "+
			    "survey_results_company_report src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
			    "where src.AGENT_ID = ? and src.SURVEY_COMPLETED_DATE <= ? "+
			    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
			
	private static final String GET_MAX_RESPONSE_BY_START_AND_END_DATE_USER = "select count(sr.QUESTION_ID) as qno from "+
			    "survey_results_company_report src inner join survey_response sr on src.SURVEY_DETAILS_ID=sr.SURVEY_DETAILS_ID "+
			    "where src.AGENT_ID = ? and src.SURVEY_COMPLETED_DATE >= ? and src.SURVEY_COMPLETED_DATE <= ? "+
			    "group by sr.SURVEY_DETAILS_ID order by qno desc limit 1";
    @Override
    @Transactional(value = "transactionManagerForReporting")
    public int getMaxResponseForCompanyId(long companyId , Timestamp startDate , Timestamp endDate )
    {
	    LOG.debug( "Method getMaxResponseForCompanyId started for CompanyId : " + companyId );
        Query query = null ;
        try{
            if(startDate != null && endDate != null){
                query = getSession().createSQLQuery( GET_MAX_RESPONSE_BY_START_AND_END_DATE_COMPANY );
                query.setParameter( 1, startDate );
                query.setParameter( 2, endDate );
            }else if(startDate != null && endDate == null){
                query = getSession().createSQLQuery( GET_MAX_RESPONSE_BY_START_DATE_COMPANY );
                query.setParameter( 1, startDate );
            }else if(startDate == null && endDate != null){
                query = getSession().createSQLQuery( GET_MAX_RESPONSE_BY_END_DATE_COMPANY );
                query.setParameter( 1, endDate );
            }else if( startDate == null && endDate == null){
                query = getSession().createSQLQuery( GET_ALL_TIME_MAX_RESPONSE_COMPANY );
            }
            query.setParameter( 0, companyId );
            LOG.debug( "QUERY : " + query.getQueryString() );
            return ( (BigInteger) query.uniqueResult() ).intValue();
        } catch ( Exception hibernateException ) {
            LOG.error( "Exception caught in getMaxResponseForCompanyId() ", hibernateException );
            throw new DatabaseException( "Exception caught in getMaxResponseForCompanyId() ", hibernateException );
        }
    }

	@Override
	public int getMaxResponseForRegionId(Long regionId, Timestamp startDate, Timestamp endDate) {
		LOG.debug( "Method getMaxResponseForCompanyId started for CompanyId : " + regionId );
        Query query = null ;
        try{
            if(startDate != null && endDate != null){
                query = getSession().createSQLQuery( GET_MAX_RESPONSE_BY_START_AND_END_DATE_REGION );
                query.setParameter( 1, startDate );
                query.setParameter( 2, endDate );
            }else if(startDate != null && endDate == null){
                query = getSession().createSQLQuery( GET_MAX_RESPONSE_BY_START_DATE_REGION );
                query.setParameter( 1, startDate );
            }else if(startDate == null && endDate != null){
                query = getSession().createSQLQuery( GET_MAX_RESPONSE_BY_END_DATE_REGION );
                query.setParameter( 1, endDate );
            }else if( startDate == null && endDate == null){
                query = getSession().createSQLQuery( GET_ALL_TIME_MAX_RESPONSE_REGION );
            }
            query.setParameter( 0, regionId );
            LOG.debug( "QUERY : " + query.getQueryString() );
            return ( (BigInteger) query.uniqueResult() ).intValue();
        } catch ( Exception hibernateException ) {
            LOG.error( "Exception caught in getMaxResponseForCompanyId() ", hibernateException );
            throw new DatabaseException( "Exception caught in getMaxResponseForCompanyId() ", hibernateException );
        }
	}

	@Override
	public int getMaxResponseForBranchId(Long branchId, Timestamp startDate, Timestamp endDate) {
		LOG.debug( "Method getMaxResponseForCompanyId started for CompanyId : " + branchId );
        Query query = null ;
        try{
            if(startDate != null && endDate != null){
                query = getSession().createSQLQuery( GET_MAX_RESPONSE_BY_START_AND_END_DATE_BRANCH );
                query.setParameter( 1, startDate );
                query.setParameter( 2, endDate );
            }else if(startDate != null && endDate == null){
                query = getSession().createSQLQuery( GET_MAX_RESPONSE_BY_START_DATE_BRANCH );
                query.setParameter( 1, startDate );
            }else if(startDate == null && endDate != null){
                query = getSession().createSQLQuery( GET_MAX_RESPONSE_BY_END_DATE_BRANCH );
                query.setParameter( 1, endDate );
            }else if( startDate == null && endDate == null){
                query = getSession().createSQLQuery( GET_ALL_TIME_MAX_RESPONSE_BRANCH );
            }
            query.setParameter( 0, branchId );
            LOG.debug( "QUERY : " + query.getQueryString() );
            return ( (BigInteger) query.uniqueResult() ).intValue();
        } catch ( Exception hibernateException ) {
            LOG.error( "Exception caught in getMaxResponseForCompanyId() ", hibernateException );
            throw new DatabaseException( "Exception caught in getMaxResponseForCompanyId() ", hibernateException );
        }
	}

	@Override
	public int getMaxResponseForUserId(Long userId, Timestamp startDate, Timestamp endDate) {
		LOG.debug( "Method getMaxResponseForUserId started for UserId : " + userId );
        Query query = null ;
        try{
            if(startDate != null && endDate != null){
                query = getSession().createSQLQuery( GET_MAX_RESPONSE_BY_START_AND_END_DATE_USER );
                query.setParameter( 1, startDate );
                query.setParameter( 2, endDate );
            }else if(startDate != null && endDate == null){
                query = getSession().createSQLQuery( GET_MAX_RESPONSE_BY_START_DATE_USER );
                query.setParameter( 1, startDate );
            }else if(startDate == null && endDate != null){
                query = getSession().createSQLQuery( GET_MAX_RESPONSE_BY_END_DATE_USER );
                query.setParameter( 1, endDate );
            }else if( startDate == null && endDate == null){
                query = getSession().createSQLQuery( GET_ALL_TIME_MAX_RESPONSE_USER );
            }
            query.setParameter( 0, userId );
            LOG.debug( "QUERY : " + query.getQueryString() );
            return ( (BigInteger) query.uniqueResult() ).intValue();
        } catch ( Exception hibernateException ) {
            LOG.error( "Exception caught in getMaxResponseForUserId() ", hibernateException );
            throw new DatabaseException( "Exception caught in getMaxResponseForUserId() ", hibernateException );
        }
	}
	
 
}
