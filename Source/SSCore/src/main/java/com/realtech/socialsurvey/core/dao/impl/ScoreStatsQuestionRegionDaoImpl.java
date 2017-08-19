package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ScoreStatsQuestionRegionDao;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

public class ScoreStatsQuestionRegionDaoImpl extends GenericReportingDaoImpl<ScoreStatsQuestionRegion, String> implements ScoreStatsQuestionRegionDao{

	private static final Logger LOG = LoggerFactory.getLogger( ScoreStatsQuestionRegionDaoImpl.class );

	@Override
	public List<ScoreStatsQuestionRegion> fetchScoreStatsQuestionForRegion(Long regionId, int startMonth, int endMonth, int year) {
		LOG.info( "Method to fetch all the score stats question for region,fetchScoreStatsQuestionForRegion() started." );
        Criteria criteria = getSession().createCriteria( ScoreStatsQuestionRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.ge( CommonConstants.MONTH_VAL, startMonth ) );
            criteria.add( Restrictions.le( CommonConstants.MONTH_VAL, endMonth ) );
            criteria.add( Restrictions.eq( CommonConstants.YEAR_VAL, year ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchScoreStatsQuestionForRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchScoreStatsQuestionForRegion() ", hibernateException );
        }

        LOG.info( "Method to fetch all the score stats question for region,fetchScoreStatsQuestionForRegion() finished." );

        return (List<ScoreStatsQuestionRegion>) criteria.list();
	}

}
