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
import com.realtech.socialsurvey.core.dao.UserRankingPastMonthMainDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastMonthMain;
import com.realtech.socialsurvey.core.entities.UserRankingThisMonthMain;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastMonthMainDaoImpl extends GenericReportingDaoImpl<UserRankingPastMonthMain, String> implements UserRankingPastMonthMainDao{
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastMonthMainDaoImpl.class );
	
	private static final String getForThisYearQuery = "select u.user_id, u.rank, u.first_name, u.last_name, u.ranking_score, u.total_reviews,"
        + " u.average_rating, u.sps, u.completed_percentage, u.is_eligible, " + "  a.PROFILE_IMAGE_URL_THUMBNAIL from user_ranking_past_month_main u left outer join agent_settings a "
        +"  on a.USER_ID = u.user_id where u.company_id=? and u.month=? and u.year=? order by u.rank asc limit ?, ?;";
	

	@Override
    public List<UserRankingPastMonthMain> fetchUserRankingWithProfileForPastMonthMain(Long companyId, int month, int year , int startIndex , int batchSize) {
        LOG.info( "method to fetch user ranking Main list for past month, fetchUserRankingWithProfileForPastMonthMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastMonthMain.class );
        List<UserRankingPastMonthMain> userRankingList = new ArrayList<>();

        try {
            Query query = getSession().createSQLQuery( getForThisYearQuery );
            query.setParameter( 0, companyId );
            query.setParameter( 1, month );
            query.setParameter( 2, year );
            query.setParameter( 3, startIndex );
            query.setParameter( 4, batchSize );

            LOG.debug( "QUERY : " + query.getQueryString() );
            List<Object[]> rows = (List<Object[]>) query.list();
            
            for ( Object[] row : rows ) {
                UserRankingPastMonthMain userRankingPastMonthMain = new UserRankingPastMonthMain();
                userRankingPastMonthMain.setUserId( Long.valueOf( String.valueOf( row[0] ) )  );
                userRankingPastMonthMain.setRank( Integer.valueOf( String.valueOf( row[1] ) ) );
                userRankingPastMonthMain.setFirstName( String.valueOf( row[2] ) );
                userRankingPastMonthMain.setLastName( String.valueOf( row[3] ) );
                userRankingPastMonthMain.setRankingScore( Float.valueOf( String.valueOf( row[4] ) ) );
                userRankingPastMonthMain.setTotalReviews( Integer.valueOf( String.valueOf( row[5] ) ) );
                userRankingPastMonthMain.setAverageRating( Float.valueOf( String.valueOf( row[6] ) )  );
                userRankingPastMonthMain.setSps(  Float.valueOf( String.valueOf( row[7] ) )  );
                userRankingPastMonthMain.setCompletedPercentage(  Float.valueOf( String.valueOf( row[8] ) ) );
                userRankingPastMonthMain.setIsEligible( Integer.valueOf( String.valueOf( row[9] ) ) );
                userRankingPastMonthMain.setProfileImageUrlThumbnail( String.valueOf( row[10] ) );
                userRankingList.add( userRankingPastMonthMain );
                
                
            }
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingWithProfileForPastMonthMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingWithProfileForPastMonthMain() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking main list for past month, fetchUserRankingWithProfileForPastMonthMain() finished." );
        return userRankingList;
    }
	
	@Override
    public List<UserRankingPastMonthMain> fetchUserRankingrReportForPastMonthMain(Long companyId, int month, int year) {
        LOG.info( "method to fetch user ranking Main list for past month, fetchUserRankingrReportForPastMonthMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastMonthMain.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_MONTH, month ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) );
            criteria.addOrder( Order.asc( CommonConstants.RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingrReportForPastMonthMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingrReportForPastMonthMain() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking main list for past month, fetchUserRankingrReportForPastMonthMain() finished." );
        return (List<UserRankingPastMonthMain>) criteria.list();
    }
	
	@Override
    public int fetchUserRankingRankForPastMonthMain(Long userId , Long companyId, int year,int month) {
        LOG.info( "method to fetch user ranking Main Rank for past month, fetchUserRankingRankFoPastMonthMain() started" );
        Query query = getSession().createSQLQuery( "SELECT rank FROM user_ranking_past_month_main WHERE user_id = :userId AND month = :month" );
        query.setParameter( "userId", userId  );
        query.setParameter( "month", month  );
        int UserRank = (int) query.uniqueResult();
        LOG.info( "method to fetch user ranking Main Rank for this month, fetchUserRankingRankFoPastMonthMain() finished." );
        return UserRank;
    }
    
    @Override
    public long fetchUserRankingCountForPastMonthMain(Long companyId, int year , int month) {
        LOG.info( "method to fetch user ranking Main count for past month, fetchUserRankingCountForPastMonthMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastMonthMain.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) ); 
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_MONTH, month ) ); 
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.info( "method to fetch user ranking main count for past month, fetchUserRankingCountForPastMonthMain() finished." );
            return count.longValue();
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingCountForPastMonthMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingCountForPastMonthMain() ", hibernateException );
        }
    }

}
