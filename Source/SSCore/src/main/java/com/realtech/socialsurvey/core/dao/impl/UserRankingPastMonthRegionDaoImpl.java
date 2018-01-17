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
import com.realtech.socialsurvey.core.dao.UserRankingPastMonthRegionDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastMonthBranch;
import com.realtech.socialsurvey.core.entities.UserRankingPastMonthMain;
import com.realtech.socialsurvey.core.entities.UserRankingPastMonthRegion;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastMonthRegionDaoImpl extends GenericReportingDaoImpl<UserRankingPastMonthRegion, String> implements UserRankingPastMonthRegionDao{
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastMonthRegionDaoImpl.class );
	
	private static final String getForThisYearQuery = "select u.user_id, u.rank, u.first_name, u.last_name, u.ranking_score, u.total_reviews,"
        + " u.average_rating, u.sps, u.completed_percentage, u.is_eligible, " + "  a.PROFILE_IMAGE_URL_THUMBNAIL from user_ranking_past_month_region u left outer join agent_settings a "
        +"  on a.USER_ID = u.user_id where u.region_id=? and u.month=? and u.year=? order by u.internal_region_rank asc limit ?, ?;";
	
	@Override
    public List<UserRankingPastMonthRegion> fetchUserRankingWithProfileForPastMonthRegion(Long regionId, int month, int year , int startIndex , int batchSize) {
        LOG.info( "method to fetch user ranking region list for past month, fetchUserRankingWithProfileForPastMonthRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastMonthRegion.class );
        List<UserRankingPastMonthRegion> userRankingList = new ArrayList<>();

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
                UserRankingPastMonthRegion userRankingPastMonthRegion = new UserRankingPastMonthRegion();
                userRankingPastMonthRegion.setUserId( Long.valueOf( String.valueOf( row[0] ) )  );
                userRankingPastMonthRegion.setRank( Integer.valueOf( String.valueOf( row[1] ) ) );
                userRankingPastMonthRegion.setFirstName( String.valueOf( row[2] ) );
                userRankingPastMonthRegion.setLastName( String.valueOf( row[3] ) );
                userRankingPastMonthRegion.setRankingScore( Float.valueOf( String.valueOf( row[4] ) ) );
                userRankingPastMonthRegion.setTotalReviews( Integer.valueOf( String.valueOf( row[5] ) ) );
                userRankingPastMonthRegion.setAverageRating( Float.valueOf( String.valueOf( row[6] ) )  );
                userRankingPastMonthRegion.setSps(  Float.valueOf( String.valueOf( row[7] ) )  );
                userRankingPastMonthRegion.setCompletedPercentage(  Float.valueOf( String.valueOf( row[8] ) ) );
                userRankingPastMonthRegion.setIsEligible( Integer.valueOf( String.valueOf( row[9] ) ) );
                userRankingPastMonthRegion.setProfileImageUrlThumbnail( String.valueOf( row[10] ) );
                userRankingList.add( userRankingPastMonthRegion );
                
                
            }
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingWithProfileForPastMonthRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingWithProfileForPastMonthRegion() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking region list for past month, fetchUserRankingWithProfileForPastMonthRegion() finished." );
        return userRankingList;
    }

	@Override
    public List<UserRankingPastMonthRegion> fetchUserRankingReportForPastMonthRegion(Long regionId, int month, int year) {
        LOG.info( "method to fetch user ranking region list for past month, fetchUserRankingReportForPastMonthRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastMonthRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_MONTH, month ) );   
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) ); 
          
            criteria.addOrder( Order.asc( CommonConstants.INTERNAL_REGION_RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingReportForPastMonthRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingReportForPastMonthRegion() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking region list for past month, fetchUserRankingReportForPastMonthRegion() finished." );
        return (List<UserRankingPastMonthRegion>) criteria.list();
    }

	@Override
	public int fetchUserRankingRankForPastMonthRegion(Long userId, Long regionId, int year, int month) {
		LOG.info( "method to fetch user ranking Region Rank for past month, fetchUserRankingRankFoPastMonthRegion() started" );
        Query query = getSession().createSQLQuery( "SELECT internal_region_rank FROM user_ranking_past_month_region WHERE user_id = :userId AND month = :month" );
        query.setParameter( "userId", userId  );
        query.setParameter( "month", month  );
        int UserRank = (int) query.uniqueResult();
        LOG.info( "method to fetch user ranking Region Rank for this month, fetchUserRankingRankFoPastMonthRegion() finished." );
        return UserRank;
	}

	@Override
	public long fetchUserRankingCountForPastMonthRegion(Long regionId,  int month,int year) {
		LOG.debug( "method to fetch user ranking Region count for past month, fetchUserRankingCountForPastMonthRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastMonthRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) ); 
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_MONTH, month ) ); 
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.debug( "method to fetch user ranking Region count for past month, fetchUserRankingCountForPastMonthRegion() finished." );
            return count.longValue();
            }
        catch ( HibernateException hibernateException ) {
            LOG.warn( "Exception caught in fetchUserRankingCountForPastMonthRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingCountForPastMonthRegion() ", hibernateException );
        }
	}

    @Override
    public List<UserRankingPastMonthRegion> fetchTopTenUserRankingsForARegion( long regionId, int monthUnderConcern, int year )
    {
        LOG.debug( "method fetchTopTenUserRankingsForARegion() running" );
        try {
            Criteria criteria = getSession().createCriteria( UserRankingPastMonthRegion.class );

            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_MONTH, monthUnderConcern ) );
            criteria.add( Restrictions.eq( CommonConstants.IS_ELIGIBLE, CommonConstants.ONE ) );
            
            criteria.addOrder( Order.asc( CommonConstants.RANK ) );
            criteria.setFirstResult( CommonConstants.INITIAL_INDEX );
            criteria.setMaxResults( 10 );

            return (List<UserRankingPastMonthRegion>) criteria.list();
        } catch ( HibernateException hibernateException ) {
            LOG.warn( "Exception caught in fetchTopTenUserRankingsForARegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchTopTenUserRankingsForARegion() ", hibernateException );
        }
    }

}
