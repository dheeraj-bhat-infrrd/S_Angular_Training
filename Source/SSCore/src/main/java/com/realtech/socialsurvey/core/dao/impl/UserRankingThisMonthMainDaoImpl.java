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
import com.realtech.socialsurvey.core.dao.UserRankingThisMonthMainDao;
import com.realtech.socialsurvey.core.entities.UserRankingThisMonthMain;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearMain;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingThisMonthMainDaoImpl extends GenericReportingDaoImpl<UserRankingThisMonthMain, String> implements UserRankingThisMonthMainDao{
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingThisMonthMainDaoImpl.class );
	
	private static final String getForThisYearQuery = "select u.user_id, u.rank, u.first_name, u.last_name, u.ranking_score, u.total_reviews,"
        + " u.average_rating, u.sps, u.completed_percentage, u.is_eligible, " + "  a.PROFILE_IMAGE_URL_THUMBNAIL from user_ranking_this_month_main u left outer join agent_settings a "
        +"  on a.USER_ID = u.user_id where u.company_id=? and u.this_month=? and u.this_year=? order by u.rank asc limit ?, ?;";

	
	@Override
    public List<UserRankingThisMonthMain> fetchUserRankingWithProfileForThisMonthMain(Long companyId, int month, int year , int startIndex , int batchSize) {
        LOG.info( "method to fetch user ranking Main list for this month, fetchUserRankingWithProfileForThisMonthMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisMonthMain.class );
        List<UserRankingThisMonthMain> userRankingList = new ArrayList<>();

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
                UserRankingThisMonthMain userRankingThisMonthMain = new UserRankingThisMonthMain();
                userRankingThisMonthMain.setUserId( Long.valueOf( String.valueOf( row[0] ) )  );
                userRankingThisMonthMain.setRank( Integer.valueOf( String.valueOf( row[1] ) ) );
                userRankingThisMonthMain.setFirstName( String.valueOf( row[2] ) );
                userRankingThisMonthMain.setLastName( String.valueOf( row[3] ) );
                userRankingThisMonthMain.setRankingScore( Float.valueOf( String.valueOf( row[4] ) ) );
                userRankingThisMonthMain.setTotalReviews( Integer.valueOf( String.valueOf( row[5] ) ) );
                userRankingThisMonthMain.setAverageRating( Integer.valueOf( String.valueOf( row[6] ) )  );
                userRankingThisMonthMain.setSps(  Float.valueOf( String.valueOf( row[7] ) )  );
                userRankingThisMonthMain.setCompletedPercentage(  Float.valueOf( String.valueOf( row[8] ) ) );
                userRankingThisMonthMain.setIsEligible( Integer.valueOf( String.valueOf( row[9] ) ) );
                userRankingThisMonthMain.setProfileImageUrlThumbnail( String.valueOf( row[10] ) );
                userRankingList.add( userRankingThisMonthMain );
                
                
            }
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingWithProfileForThisMonthMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingWithProfileForThisMonthMain() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking main list for this month, fetchUserRankingWithProfileForThisMonthMain() finished." );
        return userRankingList;
    }
	
	@Override
    public List<UserRankingThisMonthMain> fetchUserRankingReportForThisMonthMain(Long companyId, int month, int year ) {
        LOG.info( "method to fetch user ranking Main list for this month, fetchUserRankingReportForThisMonthMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisMonthMain.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_MONTH, month ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) );   
            criteria.addOrder( Order.asc( CommonConstants.RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingReportForThisMonthMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingReportForThisMonthMain() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking main list for this month, fetchUserRankingReportForThisMonthMain() finished." );
        return (List<UserRankingThisMonthMain>) criteria.list();
    }

	@Override
    public int fetchUserRankingRankForThisMonthMain(Long userId , Long companyId, int year) {
        LOG.info( "method to fetch user ranking Main Rank for this month, fetchUserRankingRankForThisMonthMain() started" );
        Query query = getSession().createSQLQuery( "SELECT rank FROM user_ranking_this_month_main WHERE user_id = :userId " );
        query.setParameter( "userId", userId  );
        int UserRank = (int) query.uniqueResult();
        LOG.info( "method to fetch user ranking Main Rank for this month, fetchUserRankingRankForThisMonthMain() finished." );
        return UserRank;
    }
    
    @Override
    public long fetchUserRankingCountForThisMonthMain(Long companyId, int year , int month) {
        LOG.info( "method to fetch user ranking Main count for this month, fetchUserRankingCountForThisMonthMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisMonthMain.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) ); 
            criteria.add( Restrictions.eq( CommonConstants.THIS_MONTH, month ) );
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.info( "method to fetch user ranking main count for this month, fetchUserRankingCountForThisMonthMain() finished." );
            return count.longValue();
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingCountForThisMonthMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingCountForThisMonthMain() ", hibernateException );
        }

        
    }
}
