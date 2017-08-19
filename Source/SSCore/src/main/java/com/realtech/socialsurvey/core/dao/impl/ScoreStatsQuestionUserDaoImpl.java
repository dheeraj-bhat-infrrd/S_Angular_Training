package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ScoreStatsQuestionUserDao;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionCompany;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionUser;
import com.realtech.socialsurvey.core.exception.DatabaseException;

public class ScoreStatsQuestionUserDaoImpl extends GenericReportingDaoImpl<ScoreStatsQuestionUser, String> implements ScoreStatsQuestionUserDao{

	private static final Logger LOG = LoggerFactory.getLogger( ScoreStatsQuestionUserDaoImpl.class );

	@Override
	public List<ScoreStatsQuestionUser> fetchScoreStatsQuestionForUser(Long userId, int startMonth, int endMonth, int year) {
		LOG.info( "Method to fetch all the score stats question for user,fetchScoreStatsQuestionForUser() started." );
        Criteria criteria = getSession().createCriteria( ScoreStatsQuestionUser.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.USER_ID, userId ) );
            criteria.add( Restrictions.ge( CommonConstants.MONTH_VAL, startMonth ) );
            criteria.add( Restrictions.le( CommonConstants.MONTH_VAL, endMonth ) );
            criteria.add( Restrictions.eq( CommonConstants.YEAR_VAL, year ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchScoreStatsQuestionForUser() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchScoreStatsQuestionForUser() ", hibernateException );
        }

        LOG.info( "Method to fetch all the score stats question for user,fetchScoreStatsQuestionForUser() finished." );

        return (List<ScoreStatsQuestionUser>) criteria.list();	}
	
}
