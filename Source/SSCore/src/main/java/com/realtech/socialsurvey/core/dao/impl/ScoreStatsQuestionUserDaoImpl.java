package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ScoreStatsQuestionUserDao;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionUser;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class ScoreStatsQuestionUserDaoImpl extends GenericReportingDaoImpl<ScoreStatsQuestionUser, String> implements ScoreStatsQuestionUserDao{

	private static final Logger LOG = LoggerFactory.getLogger( ScoreStatsQuestionUserDaoImpl.class );

	@SuppressWarnings("unchecked")
	@Override
    public List<ScoreStatsQuestionUser> fetchScoreStatsQuestionForUser( Long userId, int startMonth, int startYear,
        int endMonth, int endYear, List<Long> questionIds )
    {
		LOG.debug( "Method to fetch all the score stats question for user,fetchScoreStatsQuestionForUser() started." );
        Criteria criteria = getSession().createCriteria( ScoreStatsQuestionUser.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.USER_ID, userId ) );
            criteria.add(Restrictions.in(CommonConstants.QUESTION_ID, questionIds));
            Criterion rest1= Restrictions.and(Restrictions.eq( CommonConstants.YEAR_VAL, endYear ), 
                Restrictions.le( CommonConstants.MONTH_VAL, endMonth ));
            if( startMonth != 1){
                Criterion rest2= Restrictions.and(Restrictions.eq( CommonConstants.YEAR_VAL, startYear ), 
                    Restrictions.ge( CommonConstants.MONTH_VAL, startMonth ));
                criteria.add(Restrictions.or(rest1, rest2));
            }else if( startMonth == 1){
                criteria.add(rest1);

            }
            
            criteria.addOrder( Order.asc( CommonConstants.QUESTION_ID ) );
            criteria.addOrder( Order.asc( CommonConstants.YEAR_VAL ) );
            criteria.addOrder( Order.asc( CommonConstants.MONTH_VAL ) );
            
            LOG.debug( "Method to fetch all the score stats question for user,fetchScoreStatsQuestionForUser() finished." );

            return (List<ScoreStatsQuestionUser>) criteria.list();
        } catch ( HibernateException hibernateException ) {
            LOG.warn( "Exception caught in fetchScoreStatsQuestionForUser() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchScoreStatsQuestionForUser() ", hibernateException );
        }

       
    }

}
