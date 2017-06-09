package com.realtech.socialsurvey.core.dao.impl;

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
	
	@Override
	public List<UserRankingPastMonthMain> fetchUserRankingForPastMonthMain(Long companyId, int month, int year , int startIndex , int batchSize) {
		LOG.info( "method to fetch user ranking Main list for past month, fetchUserRankingForPastMonthMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastMonthMain.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_MONTH, month ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) );
            if ( startIndex > -1 ) {
                criteria.setFirstResult( startIndex );
            }
            if ( batchSize > -1 ) {
                criteria.setMaxResults( batchSize );
            }
            criteria.addOrder( Order.asc( CommonConstants.RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForPastMonthMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForPastMonthMain() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking main list for past month, fetchUserRankingForPastMonthMain() finished." );
        return (List<UserRankingPastMonthMain>) criteria.list();
	}
	
	@Override
    public int fetchUserRankingRankForPastMonthMain(Long userId , Long companyId, int year) {
        LOG.info( "method to fetch user ranking Main Rank for past month, fetchUserRankingRankFoPastMonthMain() started" );
        Query query = getSession().createSQLQuery( "SELECT rank FROM user_ranking_past_month_main WHERE user_id = :userId " );
        query.setParameter( "userId", userId  );
        int UserRank = (int) query.uniqueResult();
        LOG.info( "method to fetch user ranking Main Rank for this month, fetchUserRankingRankFoPastMonthMain() finished." );
        return UserRank;
    }
    
    @Override
    public long fetchUserRankingCountForPastMonthMain(Long companyId, int year , int month , Long userId ) {
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
