package com.realtech.socialsurvey.core.dao.impl;

import java.util.ArrayList;
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
import com.realtech.socialsurvey.core.entities.UserRankingThisYearRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastYearRegionDaoImpl extends GenericReportingDaoImpl< UserRankingPastYearRegion, String>implements UserRankingPastYearRegionDao {
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastYearRegionDaoImpl.class );
	
	private static final String getForThisYearQuery = "select u.user_id, u.rank, u.first_name, u.last_name, u.ranking_score, u.total_reviews,"
        + " u.average_rating, u.sps, u.completed_percentage, u.is_eligible, " + "  a.PROFILE_IMAGE_URL_THUMBNAIL from user_ranking_past_year_region u left outer join agent_settings a "
        +"  on a.USER_ID = u.user_id where u.region_id=? and u.year=? order by u.internal_region_rank asc limit ?, ?;";

	
	@Override
    public List<UserRankingPastYearRegion> fetchUserRankingWithProfileForPastYearRegion(Long regionId, int year , int startIndex , int batchSize) {
        LOG.info( "method to fetch user ranking region list for past year, fetchUserRankingWithProfileForPastYearRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearRegion.class );
        List<UserRankingPastYearRegion> userRankingList = new ArrayList<>();

        try {
            Query query = getSession().createSQLQuery( getForThisYearQuery );
            query.setParameter( 0, regionId );
            query.setParameter( 1, year );
            query.setParameter( 2, startIndex );
            query.setParameter( 3, batchSize );

            LOG.debug( "QUERY : " + query.getQueryString() );
            List<Object[]> rows = (List<Object[]>) query.list();
            
            for ( Object[] row : rows ) {
                UserRankingPastYearRegion userRankingPastYearRegion = new UserRankingPastYearRegion();
                userRankingPastYearRegion.setUserId( Long.valueOf( String.valueOf( row[0] ) )  );
                userRankingPastYearRegion.setRank( Integer.valueOf( String.valueOf( row[1] ) ) );
                userRankingPastYearRegion.setFirstName( String.valueOf( row[2] ) );
                userRankingPastYearRegion.setLastName( String.valueOf( row[3] ) );
                userRankingPastYearRegion.setRankingScore( Float.valueOf( String.valueOf( row[4] ) ) );
                userRankingPastYearRegion.setTotalReviews( Integer.valueOf( String.valueOf( row[5] ) ) );
                userRankingPastYearRegion.setAverageRating( Integer.valueOf( String.valueOf( row[6] ) )  );
                userRankingPastYearRegion.setSps(  Float.valueOf( String.valueOf( row[7] ) )  );
                userRankingPastYearRegion.setCompletedPercentage(  Float.valueOf( String.valueOf( row[8] ) ) );
                userRankingPastYearRegion.setIsEligible( Integer.valueOf( String.valueOf( row[9] ) ) );
                userRankingPastYearRegion.setProfileImageUrlThumbnail( String.valueOf( row[10] ) );
                userRankingList.add( userRankingPastYearRegion );
                
                
            }
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingWithProfileForPastYearRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingWithProfileForPastYearRegion() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking region list for past year, fetchUserRankingWithProfileForPastYearRegion() finished." );
        return userRankingList;
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
