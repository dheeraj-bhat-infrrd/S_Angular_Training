package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ScoreStatsOverallCompanyDao;
import com.realtech.socialsurvey.core.entities.ScoreStatsOverallCompany;
import com.realtech.socialsurvey.core.exception.DatabaseException;

public class ScoreStatsOverallCompanyDaoImpl extends GenericReportingDaoImpl<ScoreStatsOverallCompany, String> implements ScoreStatsOverallCompanyDao{

	private static final Logger LOG = LoggerFactory.getLogger( ScoreStatsOverallCompanyDaoImpl.class );
	
	@Override
	public List<ScoreStatsOverallCompany> fetchScoreStatsOverallForCompany(Long companyId, int startMonth, int endMonth, int year) {
		LOG.info( "Method to fetch all the score stats overall for company,fetchScoreStatsOverallForCompany() started." );
        Criteria criteria = getSession().createCriteria( ScoreStatsOverallCompany.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.ge( CommonConstants.MONTH_VAL, startMonth ) );
            criteria.add( Restrictions.le( CommonConstants.MONTH_VAL, endMonth ) );
            criteria.add( Restrictions.eq( CommonConstants.YEAR_VAL, year ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchScoreStatsOverallForCompany() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchScoreStatsOverallForCompany() ", hibernateException );
        }

        LOG.info( "Method to fetch all the score stats overall for company,fetchScoreStatsOverallForCompany() finished." );

        return (List<ScoreStatsOverallCompany>) criteria.list();
	}

}
