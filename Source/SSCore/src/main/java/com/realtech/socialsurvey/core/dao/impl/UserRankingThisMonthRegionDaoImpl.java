package com.realtech.socialsurvey.core.dao.impl;

import java.util.ArrayList;
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
	
	private static final String getForThisYearQuery = "select u.user_id, u.rank, u.first_name, u.last_name, u.ranking_score, u.total_reviews,"
        + " u.average_rating, u.sps, u.completed_percentage, u.is_eligible, " + "  a.PROFILE_IMAGE_URL_THUMBNAIL from user_ranking_this_month_region u left outer join agent_settings a "
        +"  on a.USER_ID = u.user_id where u.region_id=? and u.this_month=? and u.this_year=? order by u.internal_region_rank asc limit ?, ?;";


	@Override
    public List<UserRankingThisMonthRegion> fetchUserRankingWithProfileForThisMonthRegion(Long regionId, int month, int year , int startIndex , int batchSize) {
        LOG.info( "method to fetch user ranking region list for this month, fetchUserRankingWithProfileForThisMonthRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisMonthRegion.class );
        List<UserRankingThisMonthRegion> userRankingList = new ArrayList<>();

        try {
            Query query = getSession().createSQLQuery( getForThisYearQuery );
            query.setParameter( 0, regionId );
            query.setParameter( 1, month );
            query.setParameter( 2, year );
            query.setParameter( 3, startIndex );
            query.setParameter( 4, batchSize );

            LOG.debug( "QUERY : " + query.getQueryString() );
            List<Object[]> rows = (List<Object[]>) query.list();
            
            for ( Object[] row : rows ) {
                UserRankingThisMonthRegion userRankingThisMonthRegion = new UserRankingThisMonthRegion();
                userRankingThisMonthRegion.setUserId( Long.valueOf( String.valueOf( row[0] ) )  );
                userRankingThisMonthRegion.setRank( Integer.valueOf( String.valueOf( row[1] ) ) );
                userRankingThisMonthRegion.setFirstName( String.valueOf( row[2] ) );
                userRankingThisMonthRegion.setLastName( String.valueOf( row[3] ) );
                userRankingThisMonthRegion.setRankingScore( Float.valueOf( String.valueOf( row[4] ) ) );
                userRankingThisMonthRegion.setTotalReviews( Integer.valueOf( String.valueOf( row[5] ) ) );
                userRankingThisMonthRegion.setAverageRating( Float.valueOf( String.valueOf( row[6] ) )  );
                userRankingThisMonthRegion.setSps(  Float.valueOf( String.valueOf( row[7] ) )  );
                userRankingThisMonthRegion.setCompletedPercentage(  Float.valueOf( String.valueOf( row[8] ) ) );
                userRankingThisMonthRegion.setIsEligible( Integer.valueOf( String.valueOf( row[9] ) ) );
                userRankingThisMonthRegion.setProfileImageUrlThumbnail( String.valueOf( row[10] ) );
                userRankingList.add( userRankingThisMonthRegion );
                
                
            }
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingWithProfileForThisMonthRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingWithProfileForThisMonthRegion() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking region list for this month, fetchUserRankingWithProfileForThisMonthRegion() finished." );
        return userRankingList;
    }
    
	@Override
    public List<UserRankingThisMonthRegion> fetchUserRankingReportForThisMonthRegion(Long regionId, int month, int year ) {
        LOG.info( "method to fetch user ranking region list for this month, fetchUserRankingForThisMonthRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisMonthRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_MONTH, month ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) );  
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
