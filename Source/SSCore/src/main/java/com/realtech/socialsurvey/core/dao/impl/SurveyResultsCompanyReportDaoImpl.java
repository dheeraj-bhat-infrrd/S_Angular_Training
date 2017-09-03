package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyResultsCompanyReportDao;
import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class SurveyResultsCompanyReportDaoImpl extends GenericReportingDaoImpl<SurveyResultsCompanyReport, String> implements SurveyResultsCompanyReportDao{

	private static final Logger LOG = LoggerFactory.getLogger( SurveyResultsCompanyReportDaoImpl.class );
	
	

	
	@SuppressWarnings ( "unchecked")
    @Override
	public List<SurveyResultsCompanyReport> fetchSurveyResultsCompanyReportByCompanyId(Long companyId,Timestamp startDate, Timestamp endDate) {
		LOG.info( "method to fetch survey results company report based on companyId,fetchSurveyResultsCompanyReportByCompanyId() started" );
        Criteria criteria = getSession().createCriteria( SurveyResultsCompanyReport.class );
        List<SurveyResultsCompanyReport> testSurveyResult;
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.SURVEY_RESULTS_IS_DELETED, false ) );

            if(startDate != null && endDate != null){
            Criterion criterion = Restrictions.and(
                    Restrictions.ge( CommonConstants.SURVEY_RESULTS_REPORT_MODIFIED_ON, startDate),
                    Restrictions.le( CommonConstants.SURVEY_RESULTS_REPORT_MODIFIED_ON, endDate) );
                criteria.add( criterion );
               
            }
            criteria.setResultTransformer( Criteria.DISTINCT_ROOT_ENTITY );

        } catch ( Exception hibernateException ) {
            LOG.error( "Exception caught in fetchSurveyResultsCompanyReportByCompanyId() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchSurveyResultsCompanyReportByCompanyId() ", hibernateException );
        }
        LOG.info( "method to fetch results company report based on companyId, fetchSurveyResultsCompanyReportByCompanyId() finished." );
        return (List<SurveyResultsCompanyReport>) criteria.list();

       
	}
}
