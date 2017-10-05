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
import com.realtech.socialsurvey.core.dao.UserRankingThisYearBranchDao;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearBranch;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearMain;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingThisYearBranchDaoImpl extends GenericReportingDaoImpl<UserRankingThisYearBranch, String> implements UserRankingThisYearBranchDao{

	private static final Logger LOG = LoggerFactory.getLogger( UserRankingThisYearBranchDaoImpl.class );
	
	private static final String getForThisYearQuery = "select u.user_id, u.rank, u.first_name, u.last_name, u.ranking_score, u.total_reviews,"
        + " u.average_rating, u.sps, u.completed_percentage, u.is_eligible, " + "  a.PROFILE_IMAGE_URL_THUMBNAIL from user_ranking_this_year_branch u left outer join agent_settings a "
        +"  on a.USER_ID = u.user_id where u.branch_id=? and u.this_year=? order by u.internal_branch_rank asc limit ?, ?;";
	
	
	@Override
    public List<UserRankingThisYearBranch> fetchUserRankingWithProfileForThisYearBranch(Long branchId, int year , int startIndex , int batchSize) {
        LOG.info( "method to fetch user ranking branch list for this year, fetchUserRankingWithProfileForThisYearBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisYearBranch.class );
        List<UserRankingThisYearBranch> userRankingList = new ArrayList<>();

        try {
            Query query = getSession().createSQLQuery( getForThisYearQuery );
            query.setParameter( 0, branchId );
            query.setParameter( 1, year );
            query.setParameter( 2, startIndex );
            query.setParameter( 3, batchSize );

            LOG.debug( "QUERY : " + query.getQueryString() );
            List<Object[]> rows = (List<Object[]>) query.list();
            
            for ( Object[] row : rows ) {
                UserRankingThisYearBranch userRankingThisYearBranch = new UserRankingThisYearBranch();
                userRankingThisYearBranch.setUserId( Long.valueOf( String.valueOf( row[0] ) )  );
                userRankingThisYearBranch.setRank( Integer.valueOf( String.valueOf( row[1] ) ) );
                userRankingThisYearBranch.setFirstName( String.valueOf( row[2] ) );
                userRankingThisYearBranch.setLastName( String.valueOf( row[3] ) );
                userRankingThisYearBranch.setRankingScore( Float.valueOf( String.valueOf( row[4] ) ) );
                userRankingThisYearBranch.setTotalReviews( Integer.valueOf( String.valueOf( row[5] ) ) );
                userRankingThisYearBranch.setAverageRating( Float.valueOf( String.valueOf( row[6] ) )  );
                userRankingThisYearBranch.setSps(  Float.valueOf( String.valueOf( row[7] ) )  );
                userRankingThisYearBranch.setCompletedPercentage(  Float.valueOf( String.valueOf( row[8] ) ) );
                userRankingThisYearBranch.setIsEligible( Integer.valueOf( String.valueOf( row[9] ) ) );
                userRankingThisYearBranch.setProfileImageUrlThumbnail( String.valueOf( row[10] ) );
                userRankingList.add( userRankingThisYearBranch );
                
                
            }
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingWithProfileForThisYearBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingWithProfileForThisYearBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for this year, fetchUserRankingWithProfileForThisYearBranch() finished." );
        return userRankingList;
    }
	
	@Override
    public List<UserRankingThisYearBranch> fetchUserRankingReportForThisYearBranch(Long branchId, int year ) {
        LOG.info( "method to fetch user ranking branch list for this year, fetchUserRankingReportForThisYearBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisYearBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) );
            criteria.addOrder( Order.asc( CommonConstants.INTERNAL_BRANCH_RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingReportForThisYearBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingReportForThisYearBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for this year, fetchUserRankingReportForThisYearBranch() finished." );
        return (List<UserRankingThisYearBranch>) criteria.list();
    }

	@Override
	public int fetchUserRankingRankForThisYearBranch(Long userId, Long branchId, int year) {
		 LOG.info( "method to fetch user ranking Branch Rank for this year, fetchUserRankingRankForThisYearBranch() started" );
	        Query query = getSession().createSQLQuery( "SELECT internal_branch_rank FROM user_ranking_this_year_branch WHERE user_id = :userId " );
	        query.setParameter( "userId", userId  );
	        int UserRank = (int) query.uniqueResult();
	        LOG.info( "method to fetch user ranking Branch Rank for this year, fetchUserRankingRankForThisYearBranch() finished." );
	        return UserRank;
	}

	@Override
	public long fetchUserRankingCountForThisYearBranch(Long branchId, int year) {
		LOG.info( "method to fetch user ranking Branch count for this year, fetchUserRankingCountForThisYearBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisYearBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) );    
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.info( "method to fetch user ranking Branch count for this year, fetchUserRankingCountForThisYearBranch() finished." );
            return count.longValue();
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingCountForThisYearBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingCountForThisYearBranch() ", hibernateException );
        }
	}
	
}
