package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UserRankingPastMonthMainDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastMonthMain;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastMonthMainDaoImpl extends GenericReportingDaoImpl<UserRankingPastMonthMain, String> implements UserRankingPastMonthMainDao{
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastMonthMainDaoImpl.class );
	
	@Override
	public List<UserRankingPastMonthMain> fetchUserRankingForPastMonthMain(Long companyId, int month, int year) {
		LOG.info( "method to fetch user ranking Main list for past month, fetchUserRankingForPastMonthMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastMonthMain.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_MONTH, month ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) );            
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForPastMonthMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForPastMonthMain() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking main list for past month, fetchUserRankingForPastMonthMain() finished." );
        return (List<UserRankingPastMonthMain>) criteria.list();
	}

}
