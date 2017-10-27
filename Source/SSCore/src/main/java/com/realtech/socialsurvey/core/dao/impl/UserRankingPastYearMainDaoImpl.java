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
import com.realtech.socialsurvey.core.dao.UserRankingPastYearMainDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearMain;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearMain;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastYearMainDaoImpl extends GenericReportingDaoImpl<UserRankingPastYearMain, String> implements UserRankingPastYearMainDao{
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastYearMainDaoImpl.class );

	private static final String getForThisYearQuery = "select u.user_id, u.rank, u.first_name, u.last_name, u.ranking_score, u.total_reviews,"
        + " u.average_rating, u.sps, u.completed_percentage, u.is_eligible, " + "  a.PROFILE_IMAGE_URL_THUMBNAIL from user_ranking_past_year_main u left outer join agent_settings a "
        +"  on a.USER_ID = u.user_id where u.company_id=? and u.year=? order by u.rank asc limit ?, ?;";
	
	
	@Override
    public List<UserRankingPastYearMain> fetchUserRankingWithProfileForPastYearMain(Long companyId, int year , int startIndex , int batchSize) {
        LOG.info( "method to fetch user ranking Main list for past year, fetchUserRankingWithProfileForPastYearMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearMain.class );
        List<UserRankingPastYearMain> userRankingList = new ArrayList<>();

        try {
            Query query = getSession().createSQLQuery( getForThisYearQuery );
            query.setParameter( 0, companyId );
            query.setParameter( 1, year );
            query.setParameter( 2, startIndex );
            query.setParameter( 3, batchSize );

            LOG.debug( "QUERY : " + query.getQueryString() );
            List<Object[]> rows = (List<Object[]>) query.list();
            
            for ( Object[] row : rows ) {
                UserRankingPastYearMain userRankingPastYearMain = new UserRankingPastYearMain();
                userRankingPastYearMain.setUserId( Long.valueOf( String.valueOf( row[0] ) )  );
                userRankingPastYearMain.setRank( Integer.valueOf( String.valueOf( row[1] ) ) );
                userRankingPastYearMain.setFirstName( String.valueOf( row[2] ) );
                userRankingPastYearMain.setLastName( String.valueOf( row[3] ) );
                userRankingPastYearMain.setRankingScore( Float.valueOf( String.valueOf( row[4] ) ) );
                userRankingPastYearMain.setTotalReviews( Integer.valueOf( String.valueOf( row[5] ) ) );
                userRankingPastYearMain.setAverageRating( Float.valueOf( String.valueOf( row[6] ) )  );
                userRankingPastYearMain.setSps(  Float.valueOf( String.valueOf( row[7] ) )  );
                userRankingPastYearMain.setCompletedPercentage(  Float.valueOf( String.valueOf( row[8] ) ) );
                userRankingPastYearMain.setIsEligible( Integer.valueOf( String.valueOf( row[9] ) ) );
                userRankingPastYearMain.setProfileImageUrlThumbnail( String.valueOf( row[10] ) );
                userRankingList.add( userRankingPastYearMain );
                
                
            }
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingWithProfileForPastYearMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingWithProfileForPastYearMain() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking main list for past year, fetchUserRankingWithProfileForPastYearMain() finished." );
        return userRankingList;
    }
	
	@Override
    public List<UserRankingPastYearMain> fetchUserRankingReportForPastYearMain(Long companyId, int year) {
        LOG.info( "method to fetch user ranking Main list for past year, fetchUserRankingForPastYearMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearMain.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) ); 
           
            criteria.addOrder( Order.asc( CommonConstants.RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForPastYearMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForPastYearMain() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking main list for past year, fetchUserRankingForPastYearMain() finished." );
        return (List<UserRankingPastYearMain>) criteria.list();
    }

	@Override
    public int fetchUserRankingRankForPastYearMain(Long userId , Long companyId, int year) {
        LOG.info( "method to fetch user ranking Main Rank for past year, fetchUserRankingRankForPastYearMain() started" );
        Query query = getSession().createSQLQuery( "SELECT rank FROM user_ranking_past_year_main WHERE user_id = :userId " );
        query.setParameter( "userId", userId  );
        int UserRank = (int) query.uniqueResult();
        LOG.info( "method to fetch user ranking Main Rank for past year, fetchUserRankingRankForPastYearMain() finished." );
        return UserRank;
    }
    
    @Override
    public long fetchUserRankingCountForPastYearMain(Long companyId, int year ) {
        LOG.info( "method to fetch user ranking Main count for past year, fetchUserRankingCountForPastYearMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearMain.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) ); 
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.info( "method to fetch user ranking main count for past year, fetchUserRankingCountForPastYearMain() finished." );
            return count.longValue();
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingCountForPastYearMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingCountForPastYearMain() ", hibernateException );
        }  
    }
}
