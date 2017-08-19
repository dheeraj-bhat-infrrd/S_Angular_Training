package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UserRankingPastMonthBranchDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastMonthBranch;
import com.realtech.socialsurvey.core.entities.UserRankingPastMonthMain;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastMonthBranchDaoImpl extends GenericReportingDaoImpl<UserRankingPastMonthBranch, String> implements UserRankingPastMonthBranchDao{

	private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastMonthBranchDaoImpl.class );
	
	@Override
	public List<UserRankingPastMonthBranch> fetchUserRankingForPastMonthBranch(Long branchId, int month, int year , int startIndex , int batchSize) {
		LOG.info( "method to fetch user ranking branch list for past month, fetchUserRankingForPastMonthBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastMonthBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_MONTH, month ) ); 
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) );  
            if ( startIndex > -1 ) {
                criteria.setFirstResult( startIndex );
            }
            if ( batchSize > -1 ) {
                criteria.setMaxResults( batchSize );
            }
            criteria.addOrder( Order.asc( CommonConstants.INTERNAL_BRANCH_RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForPastMonthBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForPastMonthBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for past month, fetchUserRankingForPastMonthBranch() finished." );
        return (List<UserRankingPastMonthBranch>) criteria.list();
	}

	@Override
    public List<UserRankingPastMonthBranch> fetchUserRankingReportForPastMonthBranch(Long branchId, int month, int year) {
        LOG.info( "method to fetch user ranking branch list for past month, fetchUserRankingReportForPastMonthBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastMonthBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_MONTH, month ) ); 
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) );  
            criteria.addOrder( Order.asc( CommonConstants.INTERNAL_BRANCH_RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingReportForPastMonthBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingReportForPastMonthBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for past month, fetchUserRankingReportForPastMonthBranch() finished." );
        return (List<UserRankingPastMonthBranch>) criteria.list();
    }

	@Override
	public int fetchUserRankingRankForPastMonthBranch(Long userId, Long branchId, int year, int month) {
		LOG.info( "method to fetch user ranking Branch Rank for past month, fetchUserRankingRankFoPastMonthBranch() started" );
        Query query = getSession().createSQLQuery( "SELECT internal_branch_rank FROM user_ranking_past_month_branch WHERE user_id = :userId AND month = :month" );
        query.setParameter( "userId", userId  );
        query.setParameter( "month", month  );
        int UserRank = (int) query.uniqueResult();
        LOG.info( "method to fetch user ranking Branch Rank for this month, fetchUserRankingRankFoPastMonthBranch() finished." );
        return UserRank;
	}

	@Override
	public long fetchUserRankingCountForPastMonthBranch(Long branchId, int month, int year) {
		LOG.info( "method to fetch user ranking Branch count for past month, fetchUserRankingCountForPastMonthBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastMonthBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) ); 
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_MONTH, month ) ); 
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.info( "method to fetch user ranking Branch count for past month, fetchUserRankingCountForPastMonthBranch() finished." );
            return count.longValue();
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingCountForPastMonthBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingCountForPastMonthBranch() ", hibernateException );
        }
	}

}
