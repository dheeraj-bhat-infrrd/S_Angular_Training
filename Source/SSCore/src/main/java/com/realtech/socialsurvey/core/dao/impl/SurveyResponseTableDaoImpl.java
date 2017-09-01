package com.realtech.socialsurvey.core.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyResponseTableDao;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.SurveyResponseTable;
import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class SurveyResponseTableDaoImpl extends GenericReportingDaoImpl<SurveyResponseTable, String> implements SurveyResponseTableDao{

	private static final Logger LOG = LoggerFactory.getLogger( SurveyResponseTableDaoImpl.class );
	
	private static final String getSurveyResponseByCompanyIdQuery = "select sr.SURVEY_DETAILS_ID,sr.answer"
        + " from survey_results_company_report srcr " + "inner join survey_response sr on srcr.SURVEY_DETAILS_ID = sr.SURVEY_DETAILS_ID"+" where srcr.COMPANY_ID = ? order by sr.SURVEY_DETAILS_ID";
	
	@Override
	public List<SurveyResponseTable> fetchSurveyResponsesBySurveyDetailsId(String surveyDetailsId) {
		
		LOG.info( "method to fetch survey response based on surveyDetailsId,fetchSurveyResponseBySurveyDetailsId() started" );
        Criteria criteria = getSession().createCriteria( SurveyResponseTable.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.SURVEY_DETAILS_ID_COLUMN, surveyDetailsId ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchSurveyResponseBySurveyDetailsId() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchSurveyResponseBySurveyDetailsId() ", hibernateException );
        }
        
        List<SurveyResponseTable> surveyResponse = criteria.list();
        LOG.info( "method to fetch branch based on companyId, fetchSurveyResponseBySurveyDetailsId() finished." );
        return (surveyResponse);
	}


	@Override
    @Transactional(value = "transactionManagerForReporting")
    public Map<String, List<SurveyResponseTable>> geSurveyResponseForCompanyId( long companyId )
    {
	    LOG.debug( "Method geSurveyResponseForCompanyId started for CompanyId : " + companyId );
        try{
            Query query = getSession().createSQLQuery( getSurveyResponseByCompanyIdQuery );
            query.setParameter( 0, companyId );
            Map<String, List<SurveyResponseTable>> surveyResponseMap = new HashMap<String, List<SurveyResponseTable>>();
            LOG.debug( "QUERY : " + query.getQueryString() );
            List<Object[]> rows = (List<Object[]>) query.list();
            List<String> surveyDetailsList = new ArrayList<String>();
            //create a list of surveyDetails Id
            for ( Object[] row : rows ) {
                String surveyDetailsId = String.valueOf( row[0] );
                if(!surveyDetailsList.contains( surveyDetailsId )){
                    surveyDetailsList.add( surveyDetailsId );
                }
            }
            
            //map the answer to the survey details id 
            for ( Object[] row : rows ) {
                SurveyResponseTable SurveyResponseTable = new SurveyResponseTable();
                SurveyResponseTable.setAnswer( String.valueOf( row[1] )  );
                
                List<SurveyResponseTable> surveyResponseList = null;
                
                String surveyDetailsId = String.valueOf( row[0] );
                if(surveyResponseMap.get( surveyDetailsId )!=null)
                    surveyResponseList = surveyResponseMap.get( surveyDetailsId );
                else surveyResponseList = new ArrayList<SurveyResponseTable>();
                
                surveyResponseList.add( SurveyResponseTable );
                
                surveyResponseMap.put( surveyDetailsId , surveyResponseList );
                
            }
            return surveyResponseMap;
        } catch ( Exception hibernateException ) {
            LOG.error( "Exception caught in fetchSurveyResponseBySurveyDetailsId() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchSurveyResponseBySurveyDetailsId() ", hibernateException );
        }
    }
}
