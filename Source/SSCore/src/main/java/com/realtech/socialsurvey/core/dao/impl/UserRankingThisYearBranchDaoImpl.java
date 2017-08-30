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
import com.realtech.socialsurvey.core.dao.UserRankingThisYearBranchDao;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearBranch;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearMain;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingThisYearBranchDaoImpl extends GenericReportingDaoImpl<UserRankingThisYearBranch, String> implements UserRankingThisYearBranchDao{

	private static final Logger LOG = LoggerFactory.getLogger( UserRankingThisYearBranchDaoImpl.class );
	
	@Override
	public List<UserRankingThisYearBranch> fetchUserRankingForThisYearBranch(Long branchId, int year , int startIndex, int batchSize ) {
		LOG.info( "method to fetch user ranking branch list for this year, fetchUserRankingForThisYearBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisYearBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
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
            LOG.error( "Exception caught in fetchUserRankingForThisYearBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForThisYearBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for this year, fetchUserRankingForThisYearBranch() finished." );
        return (List<UserRankingThisYearBranch>) criteria.list();
	}
	
	@Override
    public List<UserRankingThisYearBranch> fetchUserRankingReportForThisYearBranch(Long branchId, int year ) {
        LOG.info( "method to fetch user ranking branch list for this year, fetchUserRankingReportForThisYearBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisYearBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) );
            criteria.addOrder( Order.asc( CommonConstants.INTERNAL_BRANCH_RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingReportForThisYearBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingReportForThisYearBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for this year, fetchUserRankingReportForThisYearBranch() finished." );
        return (List<UserRankingThisYearBranch>) criteria.list();
    }

	@Override
	public int fetchUserRankingRankForThisYearBranch(Long userId, Long branchId, int year) {
		 LOG.info( "method to fetch user ranking Branch Rank for this year, fetchUserRankingRankForThisYearBranch() started" );
	        Query query = getSession().createSQLQuery( "SELECT internal_branch_rank FROM user_ranking_this_year_branch WHERE user_id = :userId " );
	        query.setParameter( "userId", userId  );
	        int UserRank = (int) query.uniqueResult();
	        LOG.info( "method to fetch user ranking Branch Rank for this year, fetchUserRankingRankForThisYearBranch() finished." );
	        return UserRank;
	}

	@Override
	public long fetchUserRankingCountForThisYearBranch(Long branchId, int year) {
		LOG.info( "method to fetch user ranking Branch count for this year, fetchUserRankingCountForThisYearBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisYearBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) );    
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.info( "method to fetch user ranking Branch count for this year, fetchUserRankingCountForThisYearBranch() finished." );
            return count.longValue();
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingCountForThisYearBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingCountForThisYearBranch() ", hibernateException );
        }
	}
	
}
