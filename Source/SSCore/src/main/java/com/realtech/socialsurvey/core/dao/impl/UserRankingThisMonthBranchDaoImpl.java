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
import com.realtech.socialsurvey.core.dao.UserRankingThisMonthBranchDao;
import com.realtech.socialsurvey.core.entities.UserRankingThisMonthBranch;
import com.realtech.socialsurvey.core.entities.UserRankingThisMonthMain;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingThisMonthBranchDaoImpl extends GenericReportingDaoImpl<UserRankingThisMonthBranch, String> implements UserRankingThisMonthBranchDao{
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingThisMonthBranchDaoImpl.class );
	
	private static final String getForThisYearQuery = "select u.user_id, u.rank, u.first_name, u.last_name, u.ranking_score, u.total_reviews,"
        + " u.average_rating, u.sps, u.completed_percentage, u.is_eligible, " + "  a.PROFILE_IMAGE_URL_THUMBNAIL from user_ranking_this_month_branch u left outer join agent_settings a "
        +"  on a.USER_ID = u.user_id where u.branch_id=? and u.this_month=? and u.this_year=? order by u.internal_branch_rank asc limit ?, ?;";

	@Override
    public List<UserRankingThisMonthBranch> fetchUserRankingWithProfileForThisMonthBranch(Long branchId, int month, int year , int startIndex , int batchSize) {
        LOG.info( "method to fetch user ranking Branch list for this month, fetchUserRankingWithProfileForThisMonthBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisMonthBranch.class );
        List<UserRankingThisMonthBranch> userRankingList = new ArrayList<>();

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
                UserRankingThisMonthBranch userRankingThisMonthBranch = new UserRankingThisMonthBranch();
                userRankingThisMonthBranch.setUserId( Long.valueOf( String.valueOf( row[0] ) )  );
                userRankingThisMonthBranch.setRank( Integer.valueOf( String.valueOf( row[1] ) ) );
                userRankingThisMonthBranch.setFirstName( String.valueOf( row[2] ) );
                userRankingThisMonthBranch.setLastName( String.valueOf( row[3] ) );
                userRankingThisMonthBranch.setRankingScore( Float.valueOf( String.valueOf( row[4] ) ) );
                userRankingThisMonthBranch.setTotalReviews( Integer.valueOf( String.valueOf( row[5] ) ) );
                userRankingThisMonthBranch.setAverageRating( Float.valueOf( String.valueOf( row[6] ) )  );
                userRankingThisMonthBranch.setSps(  Float.valueOf( String.valueOf( row[7] ) )  );
                userRankingThisMonthBranch.setCompletedPercentage(  Float.valueOf( String.valueOf( row[8] ) ) );
                userRankingThisMonthBranch.setIsEligible( Integer.valueOf( String.valueOf( row[9] ) ) );
                userRankingThisMonthBranch.setProfileImageUrlThumbnail( String.valueOf( row[10] ) );
                userRankingList.add( userRankingThisMonthBranch );
                
                
            }
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingWithProfileForThisMonthBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingWithProfileForThisMonthBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for this month, fetchUserRankingWithProfileForThisMonthBranch() finished." );
        return userRankingList;
    }
    
	@Override
    public List<UserRankingThisMonthBranch> fetchUserRankingReportForThisMonthBranch(Long branchId, int month, int year ) {
        LOG.info( "method to fetch user ranking branch list for this month, fetchUserRankingReportForThisMonthBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisMonthBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_MONTH, month ) ); 
            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) );   
            criteria.addOrder( Order.asc( CommonConstants.INTERNAL_BRANCH_RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingReportForThisMonthBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingReportForThisMonthBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for this month, fetchUserRankingReportForThisMonthBranch() finished." );
        return (List<UserRankingThisMonthBranch>) criteria.list();
    }

	@Override
	public int fetchUserRankingRankForThisMonthBranch(Long userId, Long branchId, int year) {
		LOG.info( "method to fetch user ranking Branch Rank for this month, fetchUserRankingRankForThisMonthBranch() started" );
        Query query = getSession().createSQLQuery( "SELECT internal_branch_rank FROM user_ranking_this_month_branch WHERE user_id = :userId " );
        query.setParameter( "userId", userId  );
        int UserRank = (int) query.uniqueResult();
        LOG.info( "method to fetch user ranking Branch Rank for this month, fetchUserRankingRankForThisMonthBranch() finished." );
        return UserRank;
	}

	@Override
	public long fetchUserRankingCountForThisMonthBranch(Long branchId, int month,  int year) {
		 LOG.debug( "method to fetch user ranking Branch count for this month, fetchUserRankingCountForThisMonthBranch() started" );
	        Criteria criteria = getSession().createCriteria( UserRankingThisMonthBranch.class );
	        try {
	            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
	            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) ); 
	            criteria.add( Restrictions.eq( CommonConstants.THIS_MONTH, month ) );
	            criteria.setProjection( Projections.rowCount() );
	            Long count = (Long) criteria.uniqueResult();
	            LOG.debug( "method to fetch user ranking Branch count for this month, fetchUserRankingCountForThisMonthBranch() finished." );
	            return count.longValue();
	            }
	        catch ( HibernateException hibernateException ) {
	            LOG.warn( "Exception caught in fetchUserRankingCountForThisMonthBranch() ", hibernateException );
	            throw new DatabaseException( "Exception caught in fetchUserRankingCountForThisMonthBranch() ", hibernateException );
	        }
	}

}
