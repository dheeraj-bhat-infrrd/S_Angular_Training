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
import com.realtech.socialsurvey.core.dao.UserRankingPastYearsRegionDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearRegion;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearsBranch;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearsRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastYearsRegionDaoImpl extends GenericReportingDaoImpl<UserRankingPastYearsRegion, String> implements UserRankingPastYearsRegionDao
{

    private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastYearsRegionDaoImpl.class );
    
    private static final String getForThisYearQuery = "select u.user_id, u.rank, u.first_name, u.last_name, u.ranking_score, u.total_reviews,"
        + " u.average_rating, u.sps, u.completed_percentage, u.is_eligible, " + "  a.PROFILE_IMAGE_URL_THUMBNAIL from user_ranking_past_years_region u left outer join agent_settings a "
        +"  on a.USER_ID = u.user_id where u.region_id=? order by u.internal_region_rank asc limit ?, ?;";

    @Override
    public List<UserRankingPastYearsRegion> fetchUserRankingWithProfileForPastYearsRegion(Long regionId, int startIndex , int batchSize) {
        LOG.info( "method to fetch user ranking region list for past years, fetchUserRankingWithProfileForPastYearsRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearsRegion.class );
        List<UserRankingPastYearsRegion> userRankingList = new ArrayList<>();

        try {
            Query query = getSession().createSQLQuery( getForThisYearQuery );
            query.setParameter( 0, regionId );
            query.setParameter( 1, startIndex );
            query.setParameter( 2, batchSize );

            LOG.debug( "QUERY : " + query.getQueryString() );
            List<Object[]> rows = (List<Object[]>) query.list();
            
            for ( Object[] row : rows ) {
                UserRankingPastYearsRegion userRankingPastYearsRegion = new UserRankingPastYearsRegion();
                userRankingPastYearsRegion.setUserId( Long.valueOf( String.valueOf( row[0] ) )  );
                userRankingPastYearsRegion.setRank( Integer.valueOf( String.valueOf( row[1] ) ) );
                userRankingPastYearsRegion.setFirstName( String.valueOf( row[2] ) );
                userRankingPastYearsRegion.setLastName( String.valueOf( row[3] ) );
                userRankingPastYearsRegion.setRankingScore( Float.valueOf( String.valueOf( row[4] ) ) );
                userRankingPastYearsRegion.setTotalReviews( Integer.valueOf( String.valueOf( row[5] ) ) );
                userRankingPastYearsRegion.setAverageRating( Integer.valueOf( String.valueOf( row[6] ) )  );
                userRankingPastYearsRegion.setSps(  Float.valueOf( String.valueOf( row[7] ) )  );
                userRankingPastYearsRegion.setCompletedPercentage(  Float.valueOf( String.valueOf( row[8] ) ) );
                userRankingPastYearsRegion.setIsEligible( Integer.valueOf( String.valueOf( row[9] ) ) );
                userRankingPastYearsRegion.setProfileImageUrlThumbnail( String.valueOf( row[10] ) );
                userRankingList.add( userRankingPastYearsRegion );
                
                
            }
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingWithProfileForPastYearsRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingWithProfileForPastYearsRegion() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking region list for past years, fetchUserRankingWithProfileForPastYearsRegion() finished." );
        return userRankingList;
    }
    

	@Override
	public int fetchUserRankingRankForPastYearsRegion(Long userId, Long regionId) {
		LOG.info( "method to fetch user ranking Region Rank for past years, fetchUserRankingRankForPastYearsRegion() started" );
        Query query = getSession().createSQLQuery( "SELECT internal_region_rank FROM user_ranking_past_years_region WHERE user_id = :userId " );
        query.setParameter( "userId", userId  );
        int UserRank = (int) query.uniqueResult();
        LOG.info( "method to fetch user ranking Region Rank for past years, fetchUserRankingRankForPastYearsRegion() finished." );
        return UserRank;
	}

	@Override
	public long fetchUserRankingCountForPastYearsRegion(Long regionId) {
		LOG.info( "method to fetch user ranking Region count for past years, fetchUserRankingRankForPastYearsRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearsRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.info( "method to fetch user ranking Region count for past years, fetchUserRankingRankForPastYearsRegion() finished." );
            return count.longValue();
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingRankForPastYearsRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingRankForPastYearsRegion() ", hibernateException );
        }
	}
}
