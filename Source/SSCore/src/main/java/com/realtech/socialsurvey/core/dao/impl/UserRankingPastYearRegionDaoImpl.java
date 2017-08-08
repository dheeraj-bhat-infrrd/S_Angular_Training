package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UserRankingPastYearRegionDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastYearRegionDaoImpl extends GenericReportingDaoImpl< UserRankingPastYearRegion, String>implements UserRankingPastYearRegionDao {
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastYearRegionDaoImpl.class );

	@Override
	public List<UserRankingPastYearRegion> fetchUserRankingForPastYearRegion(Long regionId, int year) {
		LOG.info( "method to fetch user ranking region list for past year, fetchUserRankingForPastYearRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) );            
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForPastYearRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForPastYearRegion() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking region list for past year, fetchUserRankingForPastYearRegion() finished." );
        return (List<UserRankingPastYearRegion>) criteria.list();
	}
	

}
