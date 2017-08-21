package com.realtech.socialsurvey.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ScoreStatsQuestionUserDao;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionCompany;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionUser;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class ScoreStatsQuestionUserDaoImpl extends GenericReportingDaoImpl<ScoreStatsQuestionUser, String> implements ScoreStatsQuestionUserDao{

	private static final Logger LOG = LoggerFactory.getLogger( ScoreStatsQuestionUserDaoImpl.class );

	@Override
	public List<ScoreStatsQuestionUser> fetchScoreStatsQuestionForUser(Long userId, Long questionId, int startMonth, int endMonth, int year) {
		LOG.info( "Method to fetch all the score stats question for user,fetchScoreStatsQuestionForUser() started." );
        Criteria criteria = getSession().createCriteria( ScoreStatsQuestionUser.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.USER_ID, userId ) );
            criteria.add( Restrictions.eq( CommonConstants.QUESTION_ID, questionId ) );
            criteria.add( Restrictions.ge( CommonConstants.MONTH_VAL, startMonth ) );
            criteria.add( Restrictions.le( CommonConstants.MONTH_VAL, endMonth ) );
            criteria.add( Restrictions.eq( CommonConstants.YEAR_VAL, year ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchScoreStatsQuestionForUser() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchScoreStatsQuestionForUser() ", hibernateException );
        }

        LOG.info( "Method to fetch all the score stats question for user,fetchScoreStatsQuestionForUser() finished." );

        return (List<ScoreStatsQuestionUser>) criteria.list();	}

	@Override
	public List<Long> fetchActiveQuestionsForUser(Long userId, int startMonth, int endMonth, int year) {

		LOG.info( "Method to fetch active questions for User,fetchActiveQuestionForUser() started." );

		Criteria criteria = getSession().createCriteria(ScoreStatsQuestionUser.class);
		try {
            criteria.add( Restrictions.eq( CommonConstants.USER_ID, userId ) );
            criteria.add( Restrictions.ge( CommonConstants.MONTH_VAL, startMonth ) );
            criteria.add( Restrictions.le( CommonConstants.MONTH_VAL, endMonth ) );
            criteria.add( Restrictions.eq( CommonConstants.YEAR_VAL, year ) );
            criteria.setProjection(Projections.distinct(Projections.property(CommonConstants.QUESTION_ID)));
            criteria.addOrder( Order.asc( CommonConstants.QUESTION_ID ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchActiveQuestionForUser() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchActiveQuestionForUser() ", hibernateException );
        }
		
		LOG.info( "Method to fetch active questions for User,fetchActiveQuestionForUser() finished." );
		
		List<Long> activeQuestionsList = new ArrayList<>();
		
		for( ScoreStatsQuestionUser scoreStatsQuestionUser : (List<ScoreStatsQuestionUser>) criteria.list()){
			activeQuestionsList.add(scoreStatsQuestionUser.getQuestionId());
		}
		return activeQuestionsList;
	}
	
}
