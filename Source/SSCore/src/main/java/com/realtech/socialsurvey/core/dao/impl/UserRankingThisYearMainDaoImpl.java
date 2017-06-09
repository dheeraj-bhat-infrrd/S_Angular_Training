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
import com.realtech.socialsurvey.core.dao.UserRankingThisYearMainDao;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearMain;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingThisYearMainDaoImpl extends GenericReportingDaoImpl<UserRankingThisYearMain, String> implements UserRankingThisYearMainDao{
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingThisYearMainDaoImpl.class );
	
	@Override
	public List<UserRankingThisYearMain> fetchUserRankingForThisYearMain(Long companyId, int year , int startIndex , int batchSize) {
		LOG.info( "method to fetch user ranking Main list for this year, fetchUserRankingForThisYearMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisYearMain.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) );    
            if ( startIndex > -1 ) {
                criteria.setFirstResult( startIndex );
            }
            if ( batchSize > -1 ) {
                criteria.setMaxResults( batchSize );
            }
            criteria.addOrder( Order.asc( CommonConstants.RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForThisYearMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForThisYearMain() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking main list for this year, fetchUserRankingForThisYearMain() finished." );
        return (List<UserRankingThisYearMain>) criteria.list();
	}
	
	
	@Override
    public int fetchUserRankingRankForThisYearMain(Long userId , Long companyId, int year) {
        LOG.info( "method to fetch user ranking Main Rank for this year, fetchUserRankingRankForThisYearMain() started" );
        Query query = getSession().createSQLQuery( "SELECT rank FROM user_ranking_this_year_main WHERE user_id = :userId " );
        query.setParameter( "userId", userId  );
        int UserRank = (int) query.uniqueResult();
        LOG.info( "method to fetch user ranking Main Rank for this year, fetchUserRankingRankForThisYearMain() finished." );
        return UserRank;
    }
	
	@Override
    public long fetchUserRankingCountForThisYearMain(Long companyId, int year , Long userId ) {
        LOG.info( "method to fetch user ranking Main count for this year, fetchUserRankingCountForThisYearMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisYearMain.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) );    
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.info( "method to fetch user ranking main count for this year, fetchUserRankingCountForThisYearMain() finished." );
            return count.longValue();
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingCountForThisYearMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingCountForThisYearMain() ", hibernateException );
        }  
    }
}
