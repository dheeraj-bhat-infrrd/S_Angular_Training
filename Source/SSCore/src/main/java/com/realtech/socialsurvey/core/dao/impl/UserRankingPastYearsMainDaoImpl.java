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
import com.realtech.socialsurvey.core.dao.UserRankingPastYearsMainDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearMain;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearsMain;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastYearsMainDaoImpl extends GenericReportingDaoImpl<UserRankingPastYearsMain, String> implements UserRankingPastYearsMainDao
{
    private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastYearsMainDaoImpl.class );

    @Override
    public List<UserRankingPastYearsMain> fetchUserRankingForPastYearsMain(Long companyId, int startIndex , int batchSize) {
        LOG.info( "method to fetch user ranking Main list for past year, fetchUserRankingForPastYearsMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearsMain.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            if ( startIndex > -1 ) {
                criteria.setFirstResult( startIndex );
            }
            if ( batchSize > -1 ) {
                criteria.setMaxResults( batchSize );
            }
            criteria.addOrder( Order.asc( CommonConstants.RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForPastYearsMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForPastYearsMain() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking main list for past years, fetchUserRankingForPastYearsMain() finished." );
        return (List<UserRankingPastYearsMain>) criteria.list();
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
        LOG.info( "method to fetch user ranking Main count for past years, fetchUserRankingRankForPastYearsMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearsMain.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.info( "method to fetch user ranking main count for past years, fetchUserRankingRankForPastYearsMain() finished." );
            return count.longValue();
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingRankForPastYearsMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingRankForPastYearsMain() ", hibernateException );
        }  
    }

}
