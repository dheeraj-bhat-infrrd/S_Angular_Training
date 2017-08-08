package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UserRankingPastYearMainDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearMain;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastYearMainDaoImpl extends GenericReportingDaoImpl<UserRankingPastYearMain, String> implements UserRankingPastYearMainDao{
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastYearMainDaoImpl.class );

	@Override
	public List<UserRankingPastYearMain> fetchUserRankingForPastYearMain(Long companyId, int year , int startIndex , int batchSize) {
		LOG.info( "method to fetch user ranking Main list for past year, fetchUserRankingForPastYearMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearMain.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
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
            LOG.error( "Exception caught in fetchUserRankingForPastYearMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForPastYearMain() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking main list for past year, fetchUserRankingForPastYearMain() finished." );
        return (List<UserRankingPastYearMain>) criteria.list();
	}

}
