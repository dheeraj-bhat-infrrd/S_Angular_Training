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
import com.realtech.socialsurvey.core.dao.UserRankingPastYearsBranchDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearBranch;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearsBranch;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearsMain;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastYearsBranchDaoImpl extends GenericReportingDaoImpl<UserRankingPastYearsBranch, String> implements UserRankingPastYearsBranchDao
{
    private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastYearsBranchDaoImpl.class );

    @Override
    public List<UserRankingPastYearsBranch> fetchUserRankingForPastYearsBranch(Long branchId , int startIndex , int batchSize) {
        LOG.info( "method to fetch user ranking branch list for past years, fetchUserRankingForPastYearsBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearsBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            if ( startIndex > -1 ) {
                criteria.setFirstResult( startIndex );
            }
            if ( batchSize > -1 ) {
                criteria.setMaxResults( batchSize );
            }
            criteria.addOrder( Order.asc( CommonConstants.INTERNAL_BRANCH_RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForPastYearsBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForPastYearsBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for past years, fetchUserRankingForPastYearsBranch() finished." );
        return (List<UserRankingPastYearsBranch>) criteria.list();
    }

	@Override
	public int fetchUserRankingRankForPastYearsBranch(Long userId, Long branchId) {
		 LOG.info( "method to fetch user ranking Branch Rank for past years, fetchUserRankingRankForPastYearsBranch() started" );
	        Query query = getSession().createSQLQuery( "SELECT internal_branch_rank FROM user_ranking_past_years_branch WHERE user_id = :userId " );
	        query.setParameter( "userId", userId  );
	        int UserRank = (int) query.uniqueResult();
	        LOG.info( "method to fetch user ranking Branch Rank for past years, fetchUserRankingRankForPastYearsBranch() finished." );
	        return UserRank;
	}

	@Override
	public long fetchUserRankingCountForPastYearsBranch(Long branchId) {
		LOG.info( "method to fetch user ranking Branch count for past years, fetchUserRankingRankForPastYearsBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearsBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.info( "method to fetch user ranking Branch count for past years, fetchUserRankingRankForPastYearsBranch() finished." );
            return count.longValue();
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingRankForPastYearsBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingRankForPastYearsBranch() ", hibernateException );
        }
	}

}
