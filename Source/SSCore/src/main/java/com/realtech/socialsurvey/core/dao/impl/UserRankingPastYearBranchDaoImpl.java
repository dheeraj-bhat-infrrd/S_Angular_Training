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
import com.realtech.socialsurvey.core.dao.UserRankingPastYearBranchDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearBranch;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearMain;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastYearBranchDaoImpl extends GenericReportingDaoImpl<UserRankingPastYearBranch, String> implements UserRankingPastYearBranchDao{
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastYearBranchDaoImpl.class );

	@Override
	public List<UserRankingPastYearBranch> fetchUserRankingForPastYearBranch(Long branchId, int year , int startIndex , int batchSize) {
		LOG.info( "method to fetch user ranking branch list for past year, fetchUserRankingForPastYearBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
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
            LOG.error( "Exception caught in fetchUserRankingForPastYearBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForPastYearBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for past year, fetchUserRankingForPastYearBranch() finished." );
        return (List<UserRankingPastYearBranch>) criteria.list();
	}

	@Override
    public List<UserRankingPastYearBranch> fetchUserRankingReportForPastYearBranch(Long branchId, int year) {
        LOG.info( "method to fetch user ranking branch list for past year, fetchUserRankingForPastYearBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) ); 
            criteria.addOrder( Order.asc( CommonConstants.INTERNAL_BRANCH_RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForPastYearBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForPastYearBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for past year, fetchUserRankingForPastYearBranch() finished." );
        return (List<UserRankingPastYearBranch>) criteria.list();
    }
	@Override
	public int fetchUserRankingRankForPastYearBranch(Long userId, Long branchId, int year) {
		LOG.info( "method to fetch user ranking Branch Rank for past year, fetchUserRankingRankForPastYearBranch() started" );
        Query query = getSession().createSQLQuery( "SELECT internal_branch_rank FROM user_ranking_past_year_branch WHERE user_id = :userId " );
        query.setParameter( "userId", userId  );
        int UserRank = (int) query.uniqueResult();
        LOG.info( "method to fetch user ranking Branch Rank for past year, fetchUserRankingRankForPastYearBranch() finished." );
        return UserRank;
	}

	@Override
	public long fetchUserRankingCountForPastYearBranch(Long branchId, int year) {
		 LOG.info( "method to fetch user ranking Branch count for past year, fetchUserRankingCountForPastYearBranch() started" );
	        Criteria criteria = getSession().createCriteria( UserRankingPastYearBranch.class );
	        try {
	            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
	            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) ); 
	            criteria.setProjection( Projections.rowCount() );
	            Long count = (Long) criteria.uniqueResult();
	            LOG.info( "method to fetch user ranking Branch count for past year, fetchUserRankingCountForPastYearBranch() finished." );
	            return count.longValue();
	            }
	        catch ( HibernateException hibernateException ) {
	            LOG.error( "Exception caught in fetchUserRankingCountForPastYearBranch() ", hibernateException );
	            throw new DatabaseException( "Exception caught in fetchUserRankingCountForPastYearBranch() ", hibernateException );
	        }  
	}

}
