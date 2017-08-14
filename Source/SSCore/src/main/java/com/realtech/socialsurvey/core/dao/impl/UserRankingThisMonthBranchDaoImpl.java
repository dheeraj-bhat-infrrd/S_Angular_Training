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
import com.realtech.socialsurvey.core.dao.UserRankingThisMonthBranchDao;
import com.realtech.socialsurvey.core.entities.UserRankingThisMonthBranch;
import com.realtech.socialsurvey.core.entities.UserRankingThisMonthMain;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingThisMonthBranchDaoImpl extends GenericReportingDaoImpl<UserRankingThisMonthBranch, String> implements UserRankingThisMonthBranchDao{
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingThisMonthBranchDaoImpl.class );
	
	@Override
	public List<UserRankingThisMonthBranch> fetchUserRankingForThisMonthBranch(Long branchId, int month, int year , int startIndex , int batchSize) {
		LOG.info( "method to fetch user ranking branch list for this month, fetchUserRankingForThisMonthBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisMonthBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_MONTH, month ) ); 
            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) );   
            if ( startIndex > -1 ) {
                criteria.setFirstResult( startIndex );
            }
            if ( batchSize > -1 ) {
                criteria.setMaxResults( batchSize );
            }
            criteria.addOrder( Order.asc( CommonConstants.INTERNAL_BRANCH_RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForThisMonthBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForThisMonthBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for this month, fetchUserRankingForThisMonthBranch() finished." );
        return (List<UserRankingThisMonthBranch>) criteria.list();
	}

	@Override
	public int fetchUserRankingRankForThisMonthBranch(Long userId, Long branchId, int year) {
		LOG.info( "method to fetch user ranking Branch Rank for this month, fetchUserRankingRankForThisMonthBranch() started" );
        Query query = getSession().createSQLQuery( "SELECT internal_branch_rank FROM user_ranking_this_month_branch WHERE user_id = :userId " );
        query.setParameter( "userId", userId  );
        int UserRank = (int) query.uniqueResult();
        LOG.info( "method to fetch user ranking Branch Rank for this month, fetchUserRankingRankForThisMonthBranch() finished." );
        return UserRank;
	}

	@Override
	public long fetchUserRankingCountForThisMonthBranch(Long branchId, int year, int month) {
		 LOG.info( "method to fetch user ranking Branch count for this month, fetchUserRankingCountForThisMonthBranch() started" );
	        Criteria criteria = getSession().createCriteria( UserRankingThisMonthBranch.class );
	        try {
	            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
	            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) ); 
	            criteria.add( Restrictions.eq( CommonConstants.THIS_MONTH, month ) );
	            criteria.setProjection( Projections.rowCount() );
	            Long count = (Long) criteria.uniqueResult();
	            LOG.info( "method to fetch user ranking Branch count for this month, fetchUserRankingCountForThisMonthBranch() finished." );
	            return count.longValue();
	            }
	        catch ( HibernateException hibernateException ) {
	            LOG.error( "Exception caught in fetchUserRankingCountForThisMonthBranch() ", hibernateException );
	            throw new DatabaseException( "Exception caught in fetchUserRankingCountForThisMonthBranch() ", hibernateException );
	        }
	}

}
