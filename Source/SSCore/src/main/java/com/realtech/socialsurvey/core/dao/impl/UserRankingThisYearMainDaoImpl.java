package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
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
	public List<UserRankingThisYearMain> fetchUserRankingForThisYearMain(Long companyId, int year) {
		LOG.info( "method to fetch user ranking Main list for this year, fetchUserRankingForThisYearMain() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisYearMain.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) );            
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForThisYearMain() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForThisYearMain() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking main list for this year, fetchUserRankingForThisYearMain() finished." );
        return (List<UserRankingThisYearMain>) criteria.list();
	}
	
}
