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
import com.realtech.socialsurvey.core.dao.UserRankingThisYearRegionDao;
import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;
import com.realtech.socialsurvey.core.entities.UserRankingThisMonthRegion;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearBranch;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingThisYearRegionDaoImpl extends GenericReportingDaoImpl<UserRankingThisYearRegion, String> implements UserRankingThisYearRegionDao{
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingThisYearRegionDaoImpl.class );

	@Override
	public List<UserRankingThisYearRegion> fetchUserRankingForThisYearRegion(Long regionId, int year , int startIndex , int batchSize) {
		LOG.info( "method to fetch user ranking region list for this year, fetchUserRankingForThisYearRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisYearRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
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
            LOG.error( "Exception caught in fetchUserRankingForThisYearRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForThisYearRegion() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking region list for this year, fetchUserRankingForThisYearRegion() finished." );
        return (List<UserRankingThisYearRegion>) criteria.list();
	}
	
	@Override
    public List<UserRankingThisYearRegion> fetchUserRankinReportForThisYearRegion(Long regionId, int year ) {
        LOG.info( "method to fetch user ranking region list for this year, fetchUserRankinReportForThisYearRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisYearRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) );   
            criteria.addOrder( Order.asc( CommonConstants.INTERNAL_REGION_RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankinReportForThisYearRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankinReportForThisYearRegion() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking region list for this year, fetchUserRankinReportForThisYearRegion() finished." );
        return (List<UserRankingThisYearRegion>) criteria.list();
    }

	@Override
	public int fetchUserRankingRankForThisYearRegion(Long userId, Long regionId, int year) {
		LOG.info( "method to fetch user ranking Region Rank for this year, fetchUserRankingRankForThisYearRegion() started" );
        Query query = getSession().createSQLQuery( "SELECT internal_region_rank FROM user_ranking_this_year_region WHERE user_id = :userId " );
        query.setParameter( "userId", userId  );
        int UserRank = (int) query.uniqueResult();
        LOG.info( "method to fetch user ranking Region Rank for this year, fetchUserRankingRankForThisYearRegion() finished." );
        return UserRank;
	}

	@Override
	public long fetchUserRankingCountForThisYearRegion(Long regionId, int year) {
		LOG.info( "method to fetch user ranking Region count for this year, fetchUserRankingCountForThisYearRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisYearRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) );    
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.info( "method to fetch user ranking Region count for this year, fetchUserRankingCountForThisYearRegion() finished." );
            return count.longValue();
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingCountForThisYearRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingCountForThisYearRegion() ", hibernateException );
        }
	}

}
