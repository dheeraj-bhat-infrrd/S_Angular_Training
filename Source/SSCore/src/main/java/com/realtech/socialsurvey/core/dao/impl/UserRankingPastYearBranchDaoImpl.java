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
import com.realtech.socialsurvey.core.dao.UserRankingPastYearBranchDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearBranch;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearMain;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearBranch;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastYearBranchDaoImpl extends GenericReportingDaoImpl<UserRankingPastYearBranch, String> implements UserRankingPastYearBranchDao{
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastYearBranchDaoImpl.class );

	private static final String getForThisYearQuery = "select u.user_id, u.rank, u.first_name, u.last_name, u.ranking_score, u.total_reviews,"
        + " u.average_rating, u.sps, u.completed_percentage, u.is_eligible, " + "  a.PROFILE_IMAGE_URL_THUMBNAIL from user_ranking_past_year_branch u left outer join agent_settings a "
        +"  on a.USER_ID = u.user_id where u.branch_id=? and u.year=? order by u.internal_branch_rank asc limit ?, ?;";
	
	@Override
    public List<UserRankingPastYearBranch> fetchUserRankingWithProfileForPastYearBranch(Long branchId, int year , int startIndex , int batchSize) {
        LOG.info( "method to fetch user ranking branch list for past year, fetchUserRankingWithProfileForPastYearBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearBranch.class );
        List<UserRankingPastYearBranch> userRankingList = new ArrayList<>();

        try {
            Query query = getSession().createSQLQuery( getForThisYearQuery );
            query.setParameter( 0, branchId );
            query.setParameter( 1, year );
            query.setParameter( 2, startIndex );
            query.setParameter( 3, batchSize );

            LOG.debug( "QUERY : " + query.getQueryString() );
            List<Object[]> rows = (List<Object[]>) query.list();
            
            for ( Object[] row : rows ) {
                UserRankingPastYearBranch userRankingPastYearBranch = new UserRankingPastYearBranch();
                userRankingPastYearBranch.setUserId( Long.valueOf( String.valueOf( row[0] ) )  );
                userRankingPastYearBranch.setRank( Integer.valueOf( String.valueOf( row[1] ) ) );
                userRankingPastYearBranch.setFirstName( String.valueOf( row[2] ) );
                userRankingPastYearBranch.setLastName( String.valueOf( row[3] ) );
                userRankingPastYearBranch.setRankingScore( Float.valueOf( String.valueOf( row[4] ) ) );
                userRankingPastYearBranch.setTotalReviews( Integer.valueOf( String.valueOf( row[5] ) ) );
                userRankingPastYearBranch.setAverageRating( Float.valueOf( String.valueOf( row[6] ) )  );
                userRankingPastYearBranch.setSps(  Float.valueOf( String.valueOf( row[7] ) )  );
                userRankingPastYearBranch.setCompletedPercentage(  Float.valueOf( String.valueOf( row[8] ) ) );
                userRankingPastYearBranch.setIsEligible( Integer.valueOf( String.valueOf( row[9] ) ) );
                userRankingPastYearBranch.setProfileImageUrlThumbnail( String.valueOf( row[10] ) );
                userRankingList.add( userRankingPastYearBranch );
                
                
            }
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingWithProfileForPastYearBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingWithProfileForPastYearBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for past year, fetchUserRankingWithProfileForPastYearBranch() finished." );
        return userRankingList;
    }

	@Override
    public List<UserRankingPastYearBranch> fetchUserRankingReportForPastYearBranch(Long branchId, int year) {
        LOG.info( "method to fetch user ranking branch list for past year, fetchUserRankingForPastYearBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) ); 
            criteria.addOrder( Order.asc( CommonConstants.INTERNAL_BRANCH_RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForPastYearBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForPastYearBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for past year, fetchUserRankingForPastYearBranch() finished." );
        return (List<UserRankingPastYearBranch>) criteria.list();
    }
	@Override
	public int fetchUserRankingRankForPastYearBranch(Long userId, Long branchId, int year) {
		LOG.info( "method to fetch user ranking Branch Rank for past year, fetchUserRankingRankForPastYearBranch() started" );
        Query query = getSession().createSQLQuery( "SELECT internal_branch_rank FROM user_ranking_past_year_branch WHERE user_id = :userId " );
        query.setParameter( "userId", userId  );
        int UserRank = (int) query.uniqueResult();
        LOG.info( "method to fetch user ranking Branch Rank for past year, fetchUserRankingRankForPastYearBranch() finished." );
        return UserRank;
	}

	@Override
	public long fetchUserRankingCountForPastYearBranch(Long branchId, int year) {
		 LOG.info( "method to fetch user ranking Branch count for past year, fetchUserRankingCountForPastYearBranch() started" );
	        Criteria criteria = getSession().createCriteria( UserRankingPastYearBranch.class );
	        try {
	            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
	            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) ); 
	            criteria.setProjection( Projections.rowCount() );
	            Long count = (Long) criteria.uniqueResult();
	            LOG.info( "method to fetch user ranking Branch count for past year, fetchUserRankingCountForPastYearBranch() finished." );
	            return count.longValue();
	            }
	        catch ( HibernateException hibernateException ) {
	            LOG.error( "Exception caught in fetchUserRankingCountForPastYearBranch() ", hibernateException );
	            throw new DatabaseException( "Exception caught in fetchUserRankingCountForPastYearBranch() ", hibernateException );
	        }  
	}

}
