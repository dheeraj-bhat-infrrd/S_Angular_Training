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
import com.realtech.socialsurvey.core.dao.UserRankingPastYearRegionDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearBranch;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastYearRegionDaoImpl extends GenericReportingDaoImpl< UserRankingPastYearRegion, String>implements UserRankingPastYearRegionDao {
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastYearRegionDaoImpl.class );

	@Override
	public List<UserRankingPastYearRegion> fetchUserRankingForPastYearRegion(Long regionId, int year , int startIndex , int batchSize) {
		LOG.info( "method to fetch user ranking region list for past year, fetchUserRankingForPastYearRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) );  
            if ( startIndex > -1 ) {
                criteria.setFirstResult( startIndex );
            }
            if ( batchSize > -1 ) {
                criteria.setMaxResults( batchSize );
            }
            criteria.addOrder( Order.asc( CommonConstants.INTERNAL_REGION_RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForPastYearRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForPastYearRegion() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking region list for past year, fetchUserRankingForPastYearRegion() finished." );
        return (List<UserRankingPastYearRegion>) criteria.list();
	}
	
	@Override
    public List<UserRankingPastYearRegion> fetchUserRankingReportForPastYearRegion(Long regionId, int year) {
        LOG.info( "method to fetch user ranking region list for past year, fetchUserRankingForPastYearRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) );  
          
            criteria.addOrder( Order.asc( CommonConstants.INTERNAL_REGION_RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForPastYearRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForPastYearRegion() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking region list for past year, fetchUserRankingForPastYearRegion() finished." );
        return (List<UserRankingPastYearRegion>) criteria.list();
    }

	@Override
	public int fetchUserRankingRankForPastYearRegion(Long userId, Long regionId, int year) {
		LOG.info( "method to fetch user ranking Region Rank for past year, fetchUserRankingRankForPastYearRegion() started" );
        Query query = getSession().createSQLQuery( "SELECT internal_region_rank FROM user_ranking_past_year_region WHERE user_id = :userId " );
        query.setParameter( "userId", userId  );
        int UserRank = (int) query.uniqueResult();
        LOG.info( "method to fetch user ranking Region Rank for past year, fetchUserRankingRankForPastYearRegion() finished." );
        return UserRank;
	}

	@Override
	public long fetchUserRankingCountForPastYearRegion(Long regionId, int year) {
		LOG.info( "method to fetch user ranking Region count for past year, fetchUserRankingCountForPastYearRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) ); 
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.info( "method to fetch user ranking Region count for past year, fetchUserRankingCountForPastYearRegion() finished." );
            return count.longValue();
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingCountForPastYearRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingCountForPastYearRegion() ", hibernateException );
        }  
	}
	

}
