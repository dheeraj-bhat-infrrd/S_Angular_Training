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
import com.realtech.socialsurvey.core.dao.UserRankingPastMonthBranchDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastMonthBranch;
import com.realtech.socialsurvey.core.entities.UserRankingPastMonthMain;
import com.realtech.socialsurvey.core.entities.UserRankingPastMonthRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastMonthBranchDaoImpl extends GenericReportingDaoImpl<UserRankingPastMonthBranch, String> implements UserRankingPastMonthBranchDao{

	private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastMonthBranchDaoImpl.class );
	
	private static final String getForThisYearQuery = "select u.user_id, u.rank, u.first_name, u.last_name, u.ranking_score, u.total_reviews,"
        + " u.average_rating, u.sps, u.completed_percentage, u.is_eligible, " + "  a.PROFILE_IMAGE_URL_THUMBNAIL from user_ranking_past_month_branch u left outer join agent_settings a "
        +"  on a.USER_ID = u.user_id where u.branch_id=? and u.month=? and u.year=? order by u.internal_branch_rank asc limit ?, ?;";
	

	@Override
    public List<UserRankingPastMonthBranch> fetchUserRankingWithProfileForPastMonthBranch(Long branchId, int month, int year , int startIndex , int batchSize) {
        LOG.info( "method to fetch user ranking branch list for past month, fetchUserRankingWithProfileForPastMonthBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastMonthBranch.class );
        List<UserRankingPastMonthBranch> userRankingList = new ArrayList<>();

        try {
            Query query = getSession().createSQLQuery( getForThisYearQuery );
            query.setParameter( 0, branchId );
            query.setParameter( 1, month );
            query.setParameter( 2, year );
            query.setParameter( 3, startIndex );
            query.setParameter( 4, batchSize );

            LOG.debug( "QUERY : " + query.getQueryString() );
            List<Object[]> rows = (List<Object[]>) query.list();
            
            for ( Object[] row : rows ) {
                UserRankingPastMonthBranch userRankingPastMonthBranch = new UserRankingPastMonthBranch();
                userRankingPastMonthBranch.setUserId( Long.valueOf( String.valueOf( row[0] ) )  );
                userRankingPastMonthBranch.setRank( Integer.valueOf( String.valueOf( row[1] ) ) );
                userRankingPastMonthBranch.setFirstName( String.valueOf( row[2] ) );
                userRankingPastMonthBranch.setLastName( String.valueOf( row[3] ) );
                userRankingPastMonthBranch.setRankingScore( Float.valueOf( String.valueOf( row[4] ) ) );
                userRankingPastMonthBranch.setTotalReviews( Integer.valueOf( String.valueOf( row[5] ) ) );
                userRankingPastMonthBranch.setAverageRating( Float.valueOf( String.valueOf( row[6] ) )  );
                userRankingPastMonthBranch.setSps(  Float.valueOf( String.valueOf( row[7] ) )  );
                userRankingPastMonthBranch.setCompletedPercentage(  Float.valueOf( String.valueOf( row[8] ) ) );
                userRankingPastMonthBranch.setIsEligible( Integer.valueOf( String.valueOf( row[9] ) ) );
                userRankingPastMonthBranch.setProfileImageUrlThumbnail( String.valueOf( row[10] ) );
                userRankingList.add( userRankingPastMonthBranch );
                
                
            }
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingWithProfileForPastMonthBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingWithProfileForPastMonthBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for past month, fetchUserRankingWithProfileForPastMonthBranch() finished." );
        return userRankingList;
    }

	@Override
    public List<UserRankingPastMonthBranch> fetchUserRankingReportForPastMonthBranch(Long branchId, int month, int year) {
        LOG.info( "method to fetch user ranking branch list for past month, fetchUserRankingReportForPastMonthBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastMonthBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_MONTH, month ) ); 
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) );  
            criteria.addOrder( Order.asc( CommonConstants.INTERNAL_BRANCH_RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingReportForPastMonthBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingReportForPastMonthBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for past month, fetchUserRankingReportForPastMonthBranch() finished." );
        return (List<UserRankingPastMonthBranch>) criteria.list();
    }

	@Override
	public int fetchUserRankingRankForPastMonthBranch(Long userId, Long branchId, int year, int month) {
		LOG.info( "method to fetch user ranking Branch Rank for past month, fetchUserRankingRankFoPastMonthBranch() started" );
        Query query = getSession().createSQLQuery( "SELECT internal_branch_rank FROM user_ranking_past_month_branch WHERE user_id = :userId AND month = :month" );
        query.setParameter( "userId", userId  );
        query.setParameter( "month", month  );
        int UserRank = (int) query.uniqueResult();
        LOG.info( "method to fetch user ranking Branch Rank for this month, fetchUserRankingRankFoPastMonthBranch() finished." );
        return UserRank;
	}

	@Override
	public long fetchUserRankingCountForPastMonthBranch(Long branchId, int month, int year) {
		LOG.debug( "method to fetch user ranking Branch count for past month, fetchUserRankingCountForPastMonthBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastMonthBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) ); 
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_MONTH, month ) ); 
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.debug( "method to fetch user ranking Branch count for past month, fetchUserRankingCountForPastMonthBranch() finished." );
            return count.longValue();
            }
        catch ( HibernateException hibernateException ) {
            LOG.warn( "Exception caught in fetchUserRankingCountForPastMonthBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingCountForPastMonthBranch() ", hibernateException );
        }
	}

    @Override
    public List<UserRankingPastMonthBranch> fetchTopTenUserRankingsForABranch( long branchId, int monthUnderConcern, int year )
    {
        LOG.debug( "method fetchTopTenUserRankingsForABranch() running" );
        try {
            Criteria criteria = getSession().createCriteria( UserRankingPastMonthBranch.class );

            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_MONTH, monthUnderConcern ) );
            criteria.add( Restrictions.eq( CommonConstants.IS_ELIGIBLE, CommonConstants.ONE ) );
            
            criteria.addOrder( Order.asc( CommonConstants.RANK ) );
            criteria.setFirstResult( CommonConstants.INITIAL_INDEX );
            criteria.setMaxResults( 10 );

            return (List<UserRankingPastMonthBranch>) criteria.list();
        } catch ( HibernateException hibernateException ) {
            LOG.warn( "Exception caught in fetchTopTenUserRankingsForABranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchTopTenUserRankingsForABranch() ", hibernateException );
        }
    }

}
