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
import com.realtech.socialsurvey.core.dao.ScoreStatsOverallBranchDao;
import com.realtech.socialsurvey.core.entities.ScoreStatsOverallBranch;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class ScoreStatsOverallBranchDaoImpl extends GenericReportingDaoImpl<ScoreStatsOverallBranch, String> implements ScoreStatsOverallBranchDao{

	private static final Logger LOG = LoggerFactory.getLogger( ScoreStatsOverallBranchDaoImpl.class );

	@Override
	public List<ScoreStatsOverallBranch> fetchScoreStatsOverallForBranch(Long branchId, int startMonth, int startYear , int endMonth , int endYear) {
		LOG.info( "Method to fetch all the score stats overall for branch,fetchScoreStatsOverallForBranch() started." );
        Criteria criteria = getSession().createCriteria( ScoreStatsOverallBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId) );
            Criterion rest1= Restrictions.and(Restrictions.eq( CommonConstants.YEAR_VAL, endYear ), 
                Restrictions.le( CommonConstants.MONTH_VAL, endMonth ));
            if( startMonth != 1){
                Criterion rest2= Restrictions.and(Restrictions.eq( CommonConstants.YEAR_VAL, startYear ), 
                    Restrictions.ge( CommonConstants.MONTH_VAL, startMonth ));
                criteria.add(Restrictions.or(rest1, rest2));
            }else if( startMonth == 1){
                criteria.add(rest1);

            }            
            criteria.addOrder( Order.asc( CommonConstants.YEAR_VAL ) );
            criteria.addOrder( Order.asc( CommonConstants.MONTH_VAL ) );
            LOG.info( "Method to fetch all the score stats overall for branch,fetchScoreStatsOverallForBranch() finished." );

            return (List<ScoreStatsOverallBranch>) criteria.list();
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchScoreStatsOverallForBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchScoreStatsOverallForBranch() ", hibernateException );
        }

       
	}

}
