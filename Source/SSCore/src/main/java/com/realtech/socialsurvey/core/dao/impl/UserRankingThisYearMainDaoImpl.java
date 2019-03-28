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
import com.realtech.socialsurvey.core.dao.UserRankingThisYearMainDao;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearMain;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingThisYearMainDaoImpl extends GenericReportingDaoImpl<UserRankingThisYearMain, String> implements UserRankingThisYearMainDao{
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingThisYearMainDaoImpl.class );
	
	private static final String getForThisYearQuery = "select u.user_id, u.rank, u.first_name, u.last_name, u.ranking_score, u.total_reviews,"
        + " u.average_rating, u.sps, u.completed_percentage, u.is_eligible, " + "  a.PROFILE_IMAGE_URL_THUMBNAIL from user_ranking_this_year_main u left outer join agent_settings a "
	    +"  on a.USER_ID = u.user_id where u.company_id=? and u.this_year=? order by u.rank asc limit ?, ?;";

	
	@Override
    public List<UserRankingThisYearMain> fetchUserRankingWithProfileForThisYearMain(Long companyId, int year , int startIndex , int batchSize) {
        LOG.debug( "method to fetch user ranking Main list for this year, fetchUserRankingWithProfileForThisYearMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisYearMain.class );
        List<UserRankingThisYearMain> userRankingList = new ArrayList<>();

        try {
            Query query = getSession().createSQLQuery( getForThisYearQuery );
            query.setParameter( 0, companyId );
            query.setParameter( 1, year );
            query.setParameter( 2, startIndex );
            query.setParameter( 3, batchSize );

            LOG.debug( "QUERY : " + query.getQueryString() );
            List<Object[]> rows = (List<Object[]>) query.list();
            
            for ( Object[] row : rows ) {
                UserRankingThisYearMain userRankingThisYearMain = new UserRankingThisYearMain();
                userRankingThisYearMain.setUserId( Long.valueOf( String.valueOf( row[0] ) )  );
                userRankingThisYearMain.setRank( Integer.valueOf( String.valueOf( row[1] ) ) );
                userRankingThisYearMain.setFirstName( String.valueOf( row[2] ) );
                userRankingThisYearMain.setLastName( String.valueOf( row[3] ) );
                userRankingThisYearMain.setRankingScore( Float.valueOf( String.valueOf( row[4] ) ) );
                userRankingThisYearMain.setTotalReviews( Integer.valueOf( String.valueOf( row[5] ) ) );
                userRankingThisYearMain.setAverageRating( Float.valueOf( String.valueOf( row[6] ) )  );
                userRankingThisYearMain.setSps(  Float.valueOf( String.valueOf( row[7] ) )  );
                userRankingThisYearMain.setCompletedPercentage(  Float.valueOf( String.valueOf( row[8] ) ) );
                userRankingThisYearMain.setIsEligible( Integer.valueOf( String.valueOf( row[9] ) ) );
                userRankingThisYearMain.setProfileImageUrlThumbnail( String.valueOf( row[10] ) );
                userRankingList.add( userRankingThisYearMain );
                
                
            }
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingWithProfileForThisYearMain(): {}", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingWithProfileForThisYearMain() ", hibernateException );
        }

        LOG.debug( "method to fetch user ranking main list for this year, fetchUserRankingWithProfileForThisYearMain() finished." );
        return userRankingList;
    }
    
	
	@Override
    public List<UserRankingThisYearMain> fetchUserRankingReportForThisYearMain(Long companyId, int year) {
        LOG.info( "method to fetch user ranking Main list for this year, fetchUserRankingForThisYearMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisYearMain.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) );    
            criteria.addOrder( Order.asc( CommonConstants.RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingReportForThisYearMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingReportForThisYearMain() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking main list for this year, fetchUserRankingReportForThisYearMain() finished." );
        return (List<UserRankingThisYearMain>) criteria.list();
    }
	
	
	@Override
    public int fetchUserRankingRankForThisYearMain(Long userId , Long companyId, int year) {
        LOG.info( "method to fetch user ranking Main Rank for this year, fetchUserRankingRankForThisYearMain() started" );
        Query query = getSession().createSQLQuery( "SELECT rank FROM user_ranking_this_year_main WHERE user_id = :userId " );
        query.setParameter( "userId", userId  );
        Object result = query.uniqueResult();
        int userRank = 0;
        if(result != null) {
            userRank = (int) query.uniqueResult();
        }
        LOG.info( "method to fetch user ranking Main Rank for this year, fetchUserRankingRankForThisYearMain() finished." );
        return userRank;
    }
	
	@Override
    public long fetchUserRankingCountForThisYearMain(Long companyId, int year) {
        LOG.debug( "method to fetch user ranking Main count for this year, fetchUserRankingCountForThisYearMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisYearMain.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) );    
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.debug( "method to fetch user ranking main count for this year, fetchUserRankingCountForThisYearMain() finished." );
            return count.longValue();
            }
        catch ( HibernateException hibernateException ) {
            LOG.warn( "Exception caught in fetchUserRankingCountForThisYearMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingCountForThisYearMain() ", hibernateException );
        }  
    }
}
