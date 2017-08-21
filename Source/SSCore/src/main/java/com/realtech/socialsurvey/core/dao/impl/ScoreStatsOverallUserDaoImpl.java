package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ScoreStatsOverallUserDao;
import com.realtech.socialsurvey.core.entities.ScoreStatsOverallUser;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class ScoreStatsOverallUserDaoImpl extends GenericReportingDaoImpl<ScoreStatsOverallUser, String> implements ScoreStatsOverallUserDao {

	private static final Logger LOG = LoggerFactory.getLogger( ScoreStatsOverallUserDaoImpl.class );

	@Override
	public List<ScoreStatsOverallUser> fetchScoreStatsOverallForUser(Long userId, int startMonth, int endMonth, int year) {
		LOG.info( "Method to fetch all the score stats overall for user,fetchScoreStatsOverallForUser() started." );
        Criteria criteria = getSession().createCriteria( ScoreStatsOverallUser.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.USER_ID, userId) );
            criteria.add( Restrictions.ge( CommonConstants.MONTH_VAL, startMonth ) );
            criteria.add( Restrictions.le( CommonConstants.MONTH_VAL, endMonth ) );
            criteria.add( Restrictions.eq( CommonConstants.YEAR_VAL, year ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchScoreStatsOverallForUser() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchScoreStatsOverallForUser() ", hibernateException );
        }

        LOG.info( "Method to fetch all the score stats overall for user,fetchScoreStatsOverallForUser() finished." );

        return (List<ScoreStatsOverallUser>) criteria.list();
	}

}
