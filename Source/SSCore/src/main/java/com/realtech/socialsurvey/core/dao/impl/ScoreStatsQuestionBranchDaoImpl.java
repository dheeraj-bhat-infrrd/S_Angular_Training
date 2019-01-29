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
import com.realtech.socialsurvey.core.dao.ScoreStatsQuestionBranchDao;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionBranch;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class ScoreStatsQuestionBranchDaoImpl extends GenericReportingDaoImpl<ScoreStatsQuestionBranch, String> implements ScoreStatsQuestionBranchDao{

	private static final Logger LOG = LoggerFactory.getLogger( ScoreStatsQuestionBranchDaoImpl.class );

	@SuppressWarnings("unchecked")
	@Override
    public List<ScoreStatsQuestionBranch> fetchScoreStatsQuestionForBranch( Long branchId, int startMonth, int startYear,
        int endMonth, int endYear, List<Long> questionIds )
    {
		LOG.debug( "Method to fetch all the score stats question for branch,fetchScoreStatsQuestionForBranch() started." );
        Criteria criteria = getSession().createCriteria( ScoreStatsQuestionBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
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
            
            LOG.debug( "Method to fetch all the score stats question for branch,fetchScoreStatsQuestionForBranch() finished." );

            return (List<ScoreStatsQuestionBranch>) criteria.list();
        } catch ( HibernateException hibernateException ) {
            LOG.warn( "Exception caught in fetchScoreStatsQuestionForBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchScoreStatsQuestionForBranch() ", hibernateException );
        }

       
	}

}
