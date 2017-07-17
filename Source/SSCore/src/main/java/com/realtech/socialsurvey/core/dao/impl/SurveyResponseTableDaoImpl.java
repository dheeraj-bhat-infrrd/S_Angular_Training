package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyResponseTableDao;
import com.realtech.socialsurvey.core.entities.SurveyResponseTable;
import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;
import com.realtech.socialsurvey.core.exception.DatabaseException;

public class SurveyResponseTableDaoImpl extends GenericDaoImpl<SurveyResponseTable, String> implements SurveyResponseTableDao{

	private static final Logger LOG = LoggerFactory.getLogger( SurveyResponseTableDaoImpl.class );
	
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

        LOG.info( "method to fetch branch based on companyId, fetchSurveyResponseBySurveyDetailsId() finished." );
        return (List<SurveyResponseTable>) criteria.list();
	}

}
