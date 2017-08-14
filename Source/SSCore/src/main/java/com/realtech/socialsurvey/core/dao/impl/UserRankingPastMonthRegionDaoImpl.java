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
import com.realtech.socialsurvey.core.dao.UserRankingPastMonthRegionDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastMonthBranch;
import com.realtech.socialsurvey.core.entities.UserRankingPastMonthRegion;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastMonthRegionDaoImpl extends GenericReportingDaoImpl<UserRankingPastMonthRegion, String> implements UserRankingPastMonthRegionDao{
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastMonthRegionDaoImpl.class );
	
	@Override
	public List<UserRankingPastMonthRegion> fetchUserRankingForPastMonthRegion(Long regionId, int month, int year , int startIndex , int batchSize) {
		LOG.info( "method to fetch user ranking region list for past month, fetchUserRankingForPastMonthRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastMonthRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_MONTH, month ) );   
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
            LOG.error( "Exception caught in fetchUserRankingForPastMonthRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForPastMonthRegion() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking region list for past month, fetchUserRankingForPastMonthRegion() finished." );
        return (List<UserRankingPastMonthRegion>) criteria.list();
	}

	@Override
	public int fetchUserRankingRankForPastMonthRegion(Long userId, Long regionId, int year) {
		LOG.info( "method to fetch user ranking Region Rank for past month, fetchUserRankingRankFoPastMonthRegion() started" );
        Query query = getSession().createSQLQuery( "SELECT internal_region_rank FROM user_ranking_past_month_region WHERE user_id = :userId " );
        query.setParameter( "userId", userId  );
        int UserRank = (int) query.uniqueResult();
        LOG.info( "method to fetch user ranking Region Rank for this month, fetchUserRankingRankFoPastMonthRegion() finished." );
        return UserRank;
	}

	@Override
	public long fetchUserRankingCountForPastMonthRegion(Long regionId, int year, int month) {
		LOG.info( "method to fetch user ranking Region count for past month, fetchUserRankingCountForPastMonthRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastMonthRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) ); 
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_MONTH, month ) ); 
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.info( "method to fetch user ranking Region count for past month, fetchUserRankingCountForPastMonthRegion() finished." );
            return count.longValue();
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingCountForPastMonthRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingCountForPastMonthRegion() ", hibernateException );
        }
	}

}
