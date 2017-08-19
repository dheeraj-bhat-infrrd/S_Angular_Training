package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ScoreStatsQuestionBranchDao;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionBranch;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionCompany;
import com.realtech.socialsurvey.core.exception.DatabaseException;

public class ScoreStatsQuestionBranchDaoImpl extends GenericReportingDaoImpl<ScoreStatsQuestionBranch, String> implements ScoreStatsQuestionBranchDao{

	private static final Logger LOG = LoggerFactory.getLogger( ScoreStatsQuestionBranchDaoImpl.class );

	@Override
	public List<ScoreStatsQuestionBranch> fetchScoreStatsQuestionForBranch(Long branchId, int startMonth, int endMonth, int year) {
		LOG.info( "Method to fetch all the score stats question for branch,fetchScoreStatsQuestionForBranch() started." );
        Criteria criteria = getSession().createCriteria( ScoreStatsQuestionBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
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

}
