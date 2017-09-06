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
import com.realtech.socialsurvey.core.dao.UserRankingPastYearsBranchDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearBranch;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearsBranch;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearsMain;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastYearsBranchDaoImpl extends GenericReportingDaoImpl<UserRankingPastYearsBranch, String> implements UserRankingPastYearsBranchDao
{
    private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastYearsBranchDaoImpl.class );

    private static final String getForThisYearQuery = "select u.user_id, u.rank, u.first_name, u.last_name, u.ranking_score, u.total_reviews,"
        + " u.average_rating, u.sps, u.completed_percentage, u.is_eligible, " + "  a.PROFILE_IMAGE_URL_THUMBNAIL from user_ranking_past_years_branch u left outer join agent_settings a "
        +"  on a.USER_ID = u.user_id where u.branch_id=? order by u.internal_branch_rank asc limit ?, ?;";
    
    
    @Override
    public List<UserRankingPastYearsBranch> fetchUserRankingWithProfileForPastYearsBranch(Long branchId, int startIndex , int batchSize) {
        LOG.info( "method to fetch user ranking branch list for past years, fetchUserRankingWithProfileForPastYearsBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearsBranch.class );
        List<UserRankingPastYearsBranch> userRankingList = new ArrayList<>();

        try {
            Query query = getSession().createSQLQuery( getForThisYearQuery );
            query.setParameter( 0, branchId );
            query.setParameter( 1, startIndex );
            query.setParameter( 2, batchSize );

            LOG.debug( "QUERY : " + query.getQueryString() );
            List<Object[]> rows = (List<Object[]>) query.list();
            
            for ( Object[] row : rows ) {
                UserRankingPastYearsBranch userRankingPastYearsBranch = new UserRankingPastYearsBranch();
                userRankingPastYearsBranch.setUserId( Long.valueOf( String.valueOf( row[0] ) )  );
                userRankingPastYearsBranch.setRank( Integer.valueOf( String.valueOf( row[1] ) ) );
                userRankingPastYearsBranch.setFirstName( String.valueOf( row[2] ) );
                userRankingPastYearsBranch.setLastName( String.valueOf( row[3] ) );
                userRankingPastYearsBranch.setRankingScore( Float.valueOf( String.valueOf( row[4] ) ) );
                userRankingPastYearsBranch.setTotalReviews( Integer.valueOf( String.valueOf( row[5] ) ) );
                userRankingPastYearsBranch.setAverageRating( Integer.valueOf( String.valueOf( row[6] ) )  );
                userRankingPastYearsBranch.setSps(  Float.valueOf( String.valueOf( row[7] ) )  );
                userRankingPastYearsBranch.setCompletedPercentage(  Float.valueOf( String.valueOf( row[8] ) ) );
                userRankingPastYearsBranch.setIsEligible( Integer.valueOf( String.valueOf( row[9] ) ) );
                userRankingPastYearsBranch.setProfileImageUrlThumbnail( String.valueOf( row[10] ) );
                userRankingList.add( userRankingPastYearsBranch );
                
                
            }
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingWithProfileForPastYearsBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingWithProfileForPastYearsBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for past years, fetchUserRankingWithProfileForPastYearsBranch() finished." );
        return userRankingList;
    }


	@Override
	public int fetchUserRankingRankForPastYearsBranch(Long userId, Long branchId) {
		 LOG.info( "method to fetch user ranking Branch Rank for past years, fetchUserRankingRankForPastYearsBranch() started" );
	        Query query = getSession().createSQLQuery( "SELECT internal_branch_rank FROM user_ranking_past_years_branch WHERE user_id = :userId " );
	        query.setParameter( "userId", userId  );
	        int UserRank = (int) query.uniqueResult();
	        LOG.info( "method to fetch user ranking Branch Rank for past years, fetchUserRankingRankForPastYearsBranch() finished." );
	        return UserRank;
	}

	@Override
	public long fetchUserRankingCountForPastYearsBranch(Long branchId) {
		LOG.info( "method to fetch user ranking Branch count for past years, fetchUserRankingRankForPastYearsBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearsBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.info( "method to fetch user ranking Branch count for past years, fetchUserRankingRankForPastYearsBranch() finished." );
            return count.longValue();
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingRankForPastYearsBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingRankForPastYearsBranch() ", hibernateException );
        }
	}

}
