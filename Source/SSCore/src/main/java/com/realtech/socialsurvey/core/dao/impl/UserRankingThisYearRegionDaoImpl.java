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
import com.realtech.socialsurvey.core.dao.UserRankingThisYearRegionDao;
import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;
import com.realtech.socialsurvey.core.entities.UserRankingThisMonthRegion;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearBranch;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingThisYearRegionDaoImpl extends GenericReportingDaoImpl<UserRankingThisYearRegion, String> implements UserRankingThisYearRegionDao{
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingThisYearRegionDaoImpl.class );
	
	private static final String getForThisYearQuery = "select u.user_id, u.rank, u.first_name, u.last_name, u.ranking_score, u.total_reviews,"
        + " u.average_rating, u.sps, u.completed_percentage, u.is_eligible, " + "  a.PROFILE_IMAGE_URL_THUMBNAIL from user_ranking_this_year_region u left outer join agent_settings a "
        +"  on a.USER_ID = u.user_id where u.region_id=? and u.this_year=? order by u.internal_region_rank asc limit ?, ?;";

	
	@Override
    public List<UserRankingThisYearRegion> fetchUserRankingWithProfileForThisYearRegion(Long regionId, int year , int startIndex , int batchSize) {
        LOG.info( "method to fetch user ranking region list for this year, fetchUserRankingWithProfileForThisYearRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisYearRegion.class );
        List<UserRankingThisYearRegion> userRankingList = new ArrayList<>();

        try {
            Query query = getSession().createSQLQuery( getForThisYearQuery );
            query.setParameter( 0, regionId );
            query.setParameter( 1, year );
            query.setParameter( 2, startIndex );
            query.setParameter( 3, batchSize );

            LOG.debug( "QUERY : " + query.getQueryString() );
            List<Object[]> rows = (List<Object[]>) query.list();
            
            for ( Object[] row : rows ) {
                UserRankingThisYearRegion userRankingThisYearRegion = new UserRankingThisYearRegion();
                userRankingThisYearRegion.setUserId( Long.valueOf( String.valueOf( row[0] ) )  );
                userRankingThisYearRegion.setRank( Integer.valueOf( String.valueOf( row[1] ) ) );
                userRankingThisYearRegion.setFirstName( String.valueOf( row[2] ) );
                userRankingThisYearRegion.setLastName( String.valueOf( row[3] ) );
                userRankingThisYearRegion.setRankingScore( Float.valueOf( String.valueOf( row[4] ) ) );
                userRankingThisYearRegion.setTotalReviews( Integer.valueOf( String.valueOf( row[5] ) ) );
                userRankingThisYearRegion.setAverageRating( Integer.valueOf( String.valueOf( row[6] ) )  );
                userRankingThisYearRegion.setSps(  Float.valueOf( String.valueOf( row[7] ) )  );
                userRankingThisYearRegion.setCompletedPercentage(  Float.valueOf( String.valueOf( row[8] ) ) );
                userRankingThisYearRegion.setIsEligible( Integer.valueOf( String.valueOf( row[9] ) ) );
                userRankingThisYearRegion.setProfileImageUrlThumbnail( String.valueOf( row[10] ) );
                userRankingList.add( userRankingThisYearRegion );
                
                
            }
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingWithProfileForThisYearRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingWithProfileForThisYearRegion() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking region list for this year, fetchUserRankingWithProfileForThisYearRegion() finished." );
        return userRankingList;
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
