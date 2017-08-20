package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ScoreStatsOverallRegionDao;
import com.realtech.socialsurvey.core.entities.ScoreStatsOverallRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class ScoreStatsOverallRegionDaoImpl extends GenericReportingDaoImpl<ScoreStatsOverallRegion, String> implements ScoreStatsOverallRegionDao{

	private static final Logger LOG = LoggerFactory.getLogger( ScoreStatsOverallRegionDaoImpl.class );

	@Override
	public List<ScoreStatsOverallRegion> fetchScoreStatsOverallForRegion(Long regionId, int startMonth, int endMonth, int year) {
		LOG.info( "Method to fetch all the score stats overall for region,fetchScoreStatsOverallForRegion() started." );
        Criteria criteria = getSession().createCriteria( ScoreStatsOverallRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId) );
            criteria.add( Restrictions.ge( CommonConstants.MONTH_VAL, startMonth ) );
            criteria.add( Restrictions.le( CommonConstants.MONTH_VAL, endMonth ) );
            criteria.add( Restrictions.eq( CommonConstants.YEAR_VAL, year ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchScoreStatsOverallForRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchScoreStatsOverallForRegion() ", hibernateException );
        }

        LOG.info( "Method to fetch all the score stats overall for region,fetchScoreStatsOverallForRegion() finished." );

        return (List<ScoreStatsOverallRegion>) criteria.list();
	}

}
