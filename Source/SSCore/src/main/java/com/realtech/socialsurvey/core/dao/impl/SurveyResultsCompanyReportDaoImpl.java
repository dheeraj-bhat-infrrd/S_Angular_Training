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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.dao.SurveyResultsCompanyReportDao;
import com.realtech.socialsurvey.core.entities.SurveyResponseTable;
import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class SurveyResultsCompanyReportDaoImpl extends GenericReportingDaoImpl<SurveyResultsCompanyReport, String> implements SurveyResultsCompanyReportDao{

	private static final Logger LOG = LoggerFactory.getLogger( SurveyResultsCompanyReportDaoImpl.class );
	
	/* The limit is applied to the surveyResults table and a left outer join to survey response
	 *  so the surveys like zillow who dont have a response are not missed */
	private static final String getSurveyResultAllTImeByCompanyIdQuery = "select ab.SURVEY_DETAILS_ID,sr.answer,ab.USER_FIRST_NAME,ab.USER_LAST_NAME,ab.CUSTOMER_FIRST_NAME,ab.CUSTOMER_LAST_NAME,ab.SURVEY_SENT_DATE,"+
        "ab.SURVEY_COMPLETED_DATE,ab.TIME_INTERVAL,ab.SURVEY_SOURCE,ab.SURVEY_SOURCE_ID,ab.SURVEY_SCORE,ab.GATEWAY,ab.CUSTOMER_COMMENTS,"+
        "ab.AGREED_TO_SHARE,ab.BRANCH_NAME,ab.CLICK_THROUGH_FOR_COMPANY,ab.CLICK_THROUGH_FOR_AGENT,ab.CLICK_THROUGH_FOR_REGION,ab.CLICK_THROUGH_FOR_BRANCH "+
        "from (select srcr.SURVEY_DETAILS_ID,srcr.USER_FIRST_NAME,srcr.USER_LAST_NAME,srcr.CUSTOMER_FIRST_NAME,srcr.CUSTOMER_LAST_NAME,srcr.SURVEY_SENT_DATE,"+
        "srcr.SURVEY_COMPLETED_DATE,srcr.TIME_INTERVAL,srcr.SURVEY_SOURCE,srcr.SURVEY_SOURCE_ID,srcr.SURVEY_SCORE,srcr.GATEWAY,srcr.CUSTOMER_COMMENTS,"+
        "srcr.AGREED_TO_SHARE,srcr.BRANCH_NAME,srcr.CLICK_THROUGH_FOR_COMPANY,srcr.CLICK_THROUGH_FOR_AGENT,srcr.CLICK_THROUGH_FOR_REGION,srcr.CLICK_THROUGH_FOR_BRANCH "+
        "from survey_results_company_report srcr where srcr.COMPANY_ID = ? limit ?,?) as ab "+
        "left outer join survey_response sr on ab.SURVEY_DETAILS_ID = sr.SURVEY_DETAILS_ID "+
        "order by sr.SURVEY_DETAILS_ID, sr.QUESTION_ID ";
    
    private static final String getSurveyResultsByStartDateQuery = "select ab.SURVEY_DETAILS_ID,sr.answer,ab.USER_FIRST_NAME,ab.USER_LAST_NAME,ab.CUSTOMER_FIRST_NAME,ab.CUSTOMER_LAST_NAME,"+
        "ab.SURVEY_SENT_DATE,ab.SURVEY_COMPLETED_DATE,ab.TIME_INTERVAL,ab.SURVEY_SOURCE,ab.SURVEY_SOURCE_ID,ab.SURVEY_SCORE,ab.GATEWAY,"+
        "ab.CUSTOMER_COMMENTS,ab.AGREED_TO_SHARE,ab.BRANCH_NAME,ab.CLICK_THROUGH_FOR_COMPANY,ab.CLICK_THROUGH_FOR_AGENT,ab.CLICK_THROUGH_FOR_REGION,"+
        "ab.CLICK_THROUGH_FOR_BRANCH from (select "+ "srcr.SURVEY_DETAILS_ID,srcr.USER_FIRST_NAME,srcr.USER_LAST_NAME,srcr.CUSTOMER_FIRST_NAME,srcr.CUSTOMER_LAST_NAME,srcr.SURVEY_SENT_DATE,"+
        "srcr.SURVEY_COMPLETED_DATE,srcr.TIME_INTERVAL,srcr.SURVEY_SOURCE,srcr.SURVEY_SOURCE_ID,srcr.SURVEY_SCORE,srcr.GATEWAY,srcr.CUSTOMER_COMMENTS,"+
        "srcr.AGREED_TO_SHARE,srcr.BRANCH_NAME,srcr.CLICK_THROUGH_FOR_COMPANY,srcr.CLICK_THROUGH_FOR_AGENT,srcr.CLICK_THROUGH_FOR_REGION,srcr.CLICK_THROUGH_FOR_BRANCH "+ 
        "from survey_results_company_report srcr where srcr.COMPANY_ID = ? and srcr.SURVEY_COMPLETED_DATE >= ? limit ?,?) as ab left outer join "+ 
        "survey_response sr on ab.SURVEY_DETAILS_ID = sr.SURVEY_DETAILS_ID order by sr.SURVEY_DETAILS_ID, sr.QUESTION_ID";

    private static final String getSurveyResultsByEndDateQuery = "select ab.SURVEY_DETAILS_ID,sr.answer,ab.USER_FIRST_NAME,ab.USER_LAST_NAME,ab.CUSTOMER_FIRST_NAME,ab.CUSTOMER_LAST_NAME,"+
            "ab.SURVEY_SENT_DATE,ab.SURVEY_COMPLETED_DATE,ab.TIME_INTERVAL,ab.SURVEY_SOURCE,ab.SURVEY_SOURCE_ID,ab.SURVEY_SCORE,ab.GATEWAY,"+
            "ab.CUSTOMER_COMMENTS,ab.AGREED_TO_SHARE,ab.BRANCH_NAME,ab.CLICK_THROUGH_FOR_COMPANY,ab.CLICK_THROUGH_FOR_AGENT,ab.CLICK_THROUGH_FOR_REGION,"+
            "ab.CLICK_THROUGH_FOR_BRANCH from (select "+ "srcr.SURVEY_DETAILS_ID,srcr.USER_FIRST_NAME,srcr.USER_LAST_NAME,srcr.CUSTOMER_FIRST_NAME,srcr.CUSTOMER_LAST_NAME,srcr.SURVEY_SENT_DATE,"+
            "srcr.SURVEY_COMPLETED_DATE,srcr.TIME_INTERVAL,srcr.SURVEY_SOURCE,srcr.SURVEY_SOURCE_ID,srcr.SURVEY_SCORE,srcr.GATEWAY,srcr.CUSTOMER_COMMENTS,"+
            "srcr.AGREED_TO_SHARE,srcr.BRANCH_NAME,srcr.CLICK_THROUGH_FOR_COMPANY,srcr.CLICK_THROUGH_FOR_AGENT,srcr.CLICK_THROUGH_FOR_REGION,srcr.CLICK_THROUGH_FOR_BRANCH "+ 
            "from survey_results_company_report srcr where srcr.COMPANY_ID = ? and srcr.SURVEY_COMPLETED_DATE <= ? limit ?,?) as ab left outer join "+ 
            "survey_response sr on ab.SURVEY_DETAILS_ID = sr.SURVEY_DETAILS_ID order by sr.SURVEY_DETAILS_ID, sr.QUESTION_ID";
    
    private static final String getSurveyResultsByStartAndEndDateQuery = "select ab.SURVEY_DETAILS_ID,sr.answer,ab.USER_FIRST_NAME,ab.USER_LAST_NAME,ab.CUSTOMER_FIRST_NAME,ab.CUSTOMER_LAST_NAME,"+
        "ab.SURVEY_SENT_DATE,ab.SURVEY_COMPLETED_DATE,ab.TIME_INTERVAL,ab.SURVEY_SOURCE,ab.SURVEY_SOURCE_ID,ab.SURVEY_SCORE,ab.GATEWAY,"+
        "ab.CUSTOMER_COMMENTS,ab.AGREED_TO_SHARE,ab.BRANCH_NAME,ab.CLICK_THROUGH_FOR_COMPANY,ab.CLICK_THROUGH_FOR_AGENT,ab.CLICK_THROUGH_FOR_REGION,"+
        "ab.CLICK_THROUGH_FOR_BRANCH from (select "+ "srcr.SURVEY_DETAILS_ID,srcr.USER_FIRST_NAME,srcr.USER_LAST_NAME,srcr.CUSTOMER_FIRST_NAME,srcr.CUSTOMER_LAST_NAME,srcr.SURVEY_SENT_DATE,"+
        "srcr.SURVEY_COMPLETED_DATE,srcr.TIME_INTERVAL,srcr.SURVEY_SOURCE,srcr.SURVEY_SOURCE_ID,srcr.SURVEY_SCORE,srcr.GATEWAY,srcr.CUSTOMER_COMMENTS,"+
        "srcr.AGREED_TO_SHARE,srcr.BRANCH_NAME,srcr.CLICK_THROUGH_FOR_COMPANY,srcr.CLICK_THROUGH_FOR_AGENT,srcr.CLICK_THROUGH_FOR_REGION,srcr.CLICK_THROUGH_FOR_BRANCH "+ 
        "from survey_results_company_report srcr where srcr.COMPANY_ID = ? and srcr.SURVEY_COMPLETED_DATE >= ? and srcr.SURVEY_COMPLETED_DATE <= ? limit ?,?) as ab left outer join "+ 
        "survey_response sr on ab.SURVEY_DETAILS_ID = sr.SURVEY_DETAILS_ID order by sr.SURVEY_DETAILS_ID, sr.QUESTION_ID";

	
	
	    @SuppressWarnings ( "unchecked")
	    @Override
	    @Transactional(value = "transactionManagerForReporting")
	    public Map<String, SurveyResultsCompanyReport> getSurveyResultForCompanyId( long companyId , Timestamp startDate , Timestamp endDate , int startIndex , int batchSize)
	    {
	        LOG.debug( "Method getSurveyResultForCompanyId started for CompanyId : " + companyId );
	        Query query = null ;
	        try{
	            if(startDate != null && endDate != null){
	                query = getSession().createSQLQuery( getSurveyResultsByStartAndEndDateQuery );
	                query.setParameter( 1, startDate );
	                query.setParameter( 2, endDate );
	                query.setParameter( 3, startIndex );
	                query.setParameter( 4, batchSize );
	            }else if(startDate != null && endDate == null){
	                query = getSession().createSQLQuery( getSurveyResultsByStartDateQuery );
	                query.setParameter( 1, startDate );
	                query.setParameter( 2, startIndex );
	                query.setParameter( 3, batchSize );
	            }else if(startDate == null && endDate != null){
	                query = getSession().createSQLQuery( getSurveyResultsByEndDateQuery );
	                query.setParameter( 1, endDate );
	                query.setParameter( 2, startIndex );
	                query.setParameter( 3, batchSize );
	            }else if( startDate == null && endDate == null){
	                query = getSession().createSQLQuery( getSurveyResultAllTImeByCompanyIdQuery );
	                query.setParameter( 1, startIndex );
	                query.setParameter( 2, batchSize );
	            }
	            
	            query.setParameter( 0, companyId );
	            LOG.debug( "QUERY : " + query.getQueryString() );
	            List<Object[]> rows = (List<Object[]>) query.list();
	            Map<String,SurveyResultsCompanyReport> surveyResultMap = new HashMap<String, SurveyResultsCompanyReport>();
	            
	            //map the answer to the survey details id 
	            for ( Object[] row : rows ) {
	                SurveyResponseTable SurveyResponseTable = new SurveyResponseTable();
	                SurveyResponseTable.setAnswer( String.valueOf( row[1] )  );
	                List<SurveyResponseTable> surveyResponseList = new ArrayList<SurveyResponseTable>();
	                SurveyResultsCompanyReport surveyResultsCompanyReport = null;
	                String surveyDetailsId = String.valueOf( row[0] );
	                  

	                if(surveyResultMap.get( surveyDetailsId ) != null){
	                    surveyResultsCompanyReport = surveyResultMap.get( surveyDetailsId );
	                    surveyResponseList = surveyResultsCompanyReport.getSurveyResponseList();
	                    surveyResponseList.add( SurveyResponseTable );
	                    surveyResultsCompanyReport.setSurveyResponseList( surveyResponseList );

	                }else{
	                    surveyResultsCompanyReport = new SurveyResultsCompanyReport();
	                    surveyResponseList.add( SurveyResponseTable );
	                    surveyResultsCompanyReport.setSurveyResponseList( surveyResponseList );
	                    surveyResultsCompanyReport.setSurveyDetailsId( String.valueOf( row[0] ) );
	                    surveyResultsCompanyReport.setUserFirstName( String.valueOf( row[2] ) );
	                    surveyResultsCompanyReport.setUserLastName( String.valueOf( row[3] ) );
	                    surveyResultsCompanyReport.setCustomerFirstName( String.valueOf( row[4] ) );
	                    surveyResultsCompanyReport.setCustomerLastName( String.valueOf( row[5] )  );
	                    surveyResultsCompanyReport.setSurveySentDate( (Timestamp) ( row[6] ) );
	                    surveyResultsCompanyReport.setSurveyCompletedDate( (Timestamp) ( row[7] ));
	                    surveyResultsCompanyReport.setTimeInterval( (Integer)( row[8]  ));
	                    surveyResultsCompanyReport.setSurveySource( String.valueOf( row[9] ) );
	                    surveyResultsCompanyReport.setSurveySourceId( String.valueOf( row[10] ));
	                    surveyResultsCompanyReport.setSurveyScore(((BigDecimal)( row[11])).doubleValue());
	                    surveyResultsCompanyReport.setGateway( String.valueOf( row[12] ));
	                    surveyResultsCompanyReport.setCustomerComments( String.valueOf( row[13] ) );
	                    surveyResultsCompanyReport.setAgreedToShare(  String.valueOf( row[14] ) );
	                    surveyResultsCompanyReport.setBranchName( String.valueOf( row[15] ) );
	                    surveyResultsCompanyReport.setClickTroughForCompany( String.valueOf( row[16] ) );
	                    surveyResultsCompanyReport.setClickTroughForAgent( String.valueOf( row[17] ) ); 
	                    surveyResultsCompanyReport.setClickTroughForRegion( String.valueOf( row[18] ) );
	                    surveyResultsCompanyReport.setClickTroughForBranch( String.valueOf( row[19] ) );             
	                }
	                
	                
	                surveyResultMap.put( surveyDetailsId , surveyResultsCompanyReport );
	                
	            }
	            return surveyResultMap;
	        } catch ( Exception hibernateException ) {
	            LOG.error( "Exception caught in getSurveyResultForCompanyId() ", hibernateException );
	            throw new DatabaseException( "Exception caught in getSurveyResultForCompanyId() ", hibernateException );
	        }
	    }
}
