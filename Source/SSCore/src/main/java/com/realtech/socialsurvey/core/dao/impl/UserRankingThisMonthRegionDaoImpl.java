package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UserRankingThisMonthRegionDao;
import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;
import com.realtech.socialsurvey.core.entities.UserRankingThisMonthBranch;
import com.realtech.socialsurvey.core.entities.UserRankingThisMonthRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingThisMonthRegionDaoImpl extends GenericReportingDaoImpl<UserRankingThisMonthRegion, String> implements UserRankingThisMonthRegionDao {
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingThisMonthRegionDaoImpl.class );

	@Override
	public List<UserRankingThisMonthRegion> fetchUserRankingForThisMonthRegion(Long regionId, int month, int year , int startIndex , int batchSize) {
		LOG.info( "method to fetch user ranking region list for this month, fetchUserRankingForThisMonthRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisMonthRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_MONTH, month ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) );  
            if ( startIndex > -1 ) {
                criteria.setFirstResult( startIndex );
            }
            if ( batchSize > -1 ) {
                criteria.setMaxResults( batchSize );
            }
            criteria.addOrder( Order.asc( CommonConstants.INTERNAL_REGION_RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForThisMonthRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForThisMonthRegion() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking region list for this month, fetchUserRankingForThisMonthRegion() finished." );
        return (List<UserRankingThisMonthRegion>) criteria.list();
		
	}

	@Override
	public int fetchUserRankingRankForThisMonthRegion(Long userId, Long regionId, int year) {
		LOG.info( "method to fetch user ranking Region Rank for this month, fetchUserRankingRankForThisMonthRegion() started" );
        Query query = getSession().createSQLQuery( "SELECT internal_region_rank FROM user_ranking_this_month_region WHERE user_id = :userId " );
        query.setParameter( "userId", userId  );
        int UserRank = (int) query.uniqueResult();
        LOG.info( "method to fetch user ranking Region Rank for this month, fetchUserRankingRankForThisMonthRegion() finished." );
        return UserRank;
	}

	@Override
	public long fetchUserRankingCountForThisMonthRegion(Long regionId, int month, int year) {
		 LOG.info( "method to fetch user ranking Region count for this month, fetchUserRankingCountForThisMonthRegion() started" );
	        Criteria criteria = getSession().createCriteria( UserRankingThisMonthRegion.class );
	        try {
	            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
	            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) ); 
	            criteria.add( Restrictions.eq( CommonConstants.THIS_MONTH, month ) );
	            criteria.setProjection( Projections.rowCount() );
	            Long count = (Long) criteria.uniqueResult();
	            LOG.info( "method to fetch user ranking Region count for this month, fetchUserRankingCountForThisMonthRegion() finished." );
	            return count.longValue();
	            }
	        catch ( HibernateException hibernateException ) {
	            LOG.error( "Exception caught in fetchUserRankingCountForThisMonthRegion() ", hibernateException );
	            throw new DatabaseException( "Exception caught in fetchUserRankingCountForThisMonthRegion() ", hibernateException );
	        }
	}

}
