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
import com.realtech.socialsurvey.core.dao.UserRankingPastYearsMainDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearMain;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearsMain;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastYearsMainDaoImpl extends GenericReportingDaoImpl<UserRankingPastYearsMain, String> implements UserRankingPastYearsMainDao
{
    private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastYearsMainDaoImpl.class );
    
    private static final String getForThisYearQuery = "select u.user_id, u.rank, u.first_name, u.last_name, u.ranking_score, u.total_reviews,"
        + " u.average_rating, u.sps, u.completed_percentage, u.is_eligible, " + "  a.PROFILE_IMAGE_URL_THUMBNAIL from user_ranking_past_years_main u left outer join agent_settings a "
        +"  on a.USER_ID = u.user_id where u.company_id=? order by u.rank asc limit ?, ?;";


    @Override
    public List<UserRankingPastYearsMain> fetchUserRankingWithProfileForPastYearsMain(Long companyId, int startIndex , int batchSize) {
        LOG.info( "method to fetch user ranking Main list for past years, fetchUserRankingWithProfileForPastYearsMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearsMain.class );
        List<UserRankingPastYearsMain> userRankingList = new ArrayList<>();

        try {
            Query query = getSession().createSQLQuery( getForThisYearQuery );
            query.setParameter( 0, companyId );
            query.setParameter( 1, startIndex );
            query.setParameter( 2, batchSize );

            LOG.debug( "QUERY : " + query.getQueryString() );
            List<Object[]> rows = (List<Object[]>) query.list();
            
            for ( Object[] row : rows ) {
                UserRankingPastYearsMain userRankingPastYearsMain = new UserRankingPastYearsMain();
                userRankingPastYearsMain.setUserId( Long.valueOf( String.valueOf( row[0] ) )  );
                userRankingPastYearsMain.setRank( Integer.valueOf( String.valueOf( row[1] ) ) );
                userRankingPastYearsMain.setFirstName( String.valueOf( row[2] ) );
                userRankingPastYearsMain.setLastName( String.valueOf( row[3] ) );
                userRankingPastYearsMain.setRankingScore( Float.valueOf( String.valueOf( row[4] ) ) );
                userRankingPastYearsMain.setTotalReviews( Integer.valueOf( String.valueOf( row[5] ) ) );
                userRankingPastYearsMain.setAverageRating( Float.valueOf( String.valueOf( row[6] ) )  );
                userRankingPastYearsMain.setSps(  Float.valueOf( String.valueOf( row[7] ) )  );
                userRankingPastYearsMain.setCompletedPercentage(  Float.valueOf( String.valueOf( row[8] ) ) );
                userRankingPastYearsMain.setIsEligible( Integer.valueOf( String.valueOf( row[9] ) ) );
                userRankingPastYearsMain.setProfileImageUrlThumbnail( String.valueOf( row[10] ) );
                userRankingList.add( userRankingPastYearsMain );
                
                
            }
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingWithProfileForPastYearsMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingWithProfileForPastYearsMain() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking main list for past years, fetchUserRankingWithProfileForPastYearsMain() finished." );
        return userRankingList;
    }
    
    @Override
    public int fetchUserRankingRankForPastYearsMain(Long userId , Long companyId) {
        LOG.info( "method to fetch user ranking Main Rank for past years, fetchUserRankingRankForPastYearsMain() started" );
        Query query = getSession().createSQLQuery( "SELECT rank FROM user_ranking_past_years_main WHERE user_id = :userId " );
        query.setParameter( "userId", userId  );
        int UserRank = (int) query.uniqueResult();
        LOG.info( "method to fetch user ranking Main Rank for past years, fetchUserRankingRankForPastYearsMain() finished." );
        return UserRank;
    }
    
    @Override
    public long fetchUserRankingCountForPastYearsMain(Long companyId) {
        LOG.debug( "method to fetch user ranking Main count for past years, fetchUserRankingRankForPastYearsMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearsMain.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.debug( "method to fetch user ranking main count for past years, fetchUserRankingRankForPastYearsMain() finished." );
            return count.longValue();
            }
        catch ( HibernateException hibernateException ) {
            LOG.warn( "Exception caught in fetchUserRankingRankForPastYearsMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingRankForPastYearsMain() ", hibernateException );
        }  
    }

}
