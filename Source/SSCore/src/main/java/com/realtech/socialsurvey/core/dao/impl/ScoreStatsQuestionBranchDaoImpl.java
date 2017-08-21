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
import com.realtech.socialsurvey.core.dao.ScoreStatsQuestionBranchDao;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionBranch;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionCompany;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class ScoreStatsQuestionBranchDaoImpl extends GenericReportingDaoImpl<ScoreStatsQuestionBranch, String> implements ScoreStatsQuestionBranchDao{

	private static final Logger LOG = LoggerFactory.getLogger( ScoreStatsQuestionBranchDaoImpl.class );

	@Override
	public List<ScoreStatsQuestionBranch> fetchScoreStatsQuestionForBranch(Long branchId, Long questionId, int startMonth, int endMonth, int year) {
		LOG.info( "Method to fetch all the score stats question for branch,fetchScoreStatsQuestionForBranch() started." );
        Criteria criteria = getSession().createCriteria( ScoreStatsQuestionBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.QUESTION_ID, questionId ) );
            criteria.add( Restrictions.ge( CommonConstants.MONTH_VAL, startMonth ) );
            criteria.add( Restrictions.le( CommonConstants.MONTH_VAL, endMonth ) );
            criteria.add( Restrictions.eq( CommonConstants.YEAR_VAL, year ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchScoreStatsQuestionForBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchScoreStatsQuestionForBranch() ", hibernateException );
        }

        LOG.info( "Method to fetch all the score stats question for branch,fetchScoreStatsQuestionForBranch() finished." );

        return (List<ScoreStatsQuestionBranch>) criteria.list();
	}

	@Override
	public List<Long> fetchActiveQuestionsForBranch(Long branchId, int startMonth, int endMonth, int year) {
		LOG.info( "Method to fetch active questions for branch,fetchActiveQuestionForBranch() started." );

		Criteria criteria = getSession().createCriteria(ScoreStatsQuestionBranch.class);
		try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.ge( CommonConstants.MONTH_VAL, startMonth ) );
            criteria.add( Restrictions.le( CommonConstants.MONTH_VAL, endMonth ) );
            criteria.add( Restrictions.eq( CommonConstants.YEAR_VAL, year ) );
            criteria.setProjection(Projections.distinct(Projections.property(CommonConstants.QUESTION_ID)));
            criteria.addOrder( Order.asc( CommonConstants.QUESTION_ID ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchActiveQuestionForBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchActiveQuestionForBranch() ", hibernateException );
        }
		
		LOG.info( "Method to fetch active questions for branch,fetchActiveQuestionForBranch() finished." );
		
		List<Long> activeQuestionsList = new ArrayList<>();
		
		for( ScoreStatsQuestionBranch scoreStatsQuestionBranch : (List<ScoreStatsQuestionBranch>) criteria.list()){
			activeQuestionsList.add(scoreStatsQuestionBranch.getQuestionId());
		}
		return activeQuestionsList;
	}

}
