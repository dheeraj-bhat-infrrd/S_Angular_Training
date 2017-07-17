package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyResultsCompanyReportDao;
import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;
import com.realtech.socialsurvey.core.exception.DatabaseException;

public class SurveyResultsCompanyReportDaoImpl extends GenericReportingDaoImpl<SurveyResultsCompanyReport, String> implements SurveyResultsCompanyReportDao{

	private static final Logger LOG = LoggerFactory.getLogger( SurveyResultsCompanyReportDaoImpl.class );
	
	@Override
	public List<SurveyResultsCompanyReport> fetchSurveyResultsCompanyReportByCompanyId(Long companyId) {
		LOG.info( "method to fetch survey results company report based on companyId,fetchSurveyResultsCompanyReportByCompanyId() started" );
        Criteria criteria = getSession().createCriteria( SurveyResultsCompanyReport.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchSurveyResultsCompanyReportByCompanyId() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchSurveyResultsCompanyReportByCompanyId() ", hibernateException );
        }

        LOG.info( "method to fetch branch based on companyId, fetchSurveyResultsCompanyReportByCompanyId() finished." );
        return (List<SurveyResultsCompanyReport>) criteria.list();
	}
}
