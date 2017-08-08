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
import com.realtech.socialsurvey.core.dao.UserRankingThisYearRegionDao;
import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;
import com.realtech.socialsurvey.core.entities.UserRankingThisMonthRegion;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingThisYearRegionDaoImpl extends GenericReportingDaoImpl<UserRankingThisYearRegion, String> implements UserRankingThisYearRegionDao{
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingThisYearRegionDaoImpl.class );

	@Override
	public List<UserRankingThisYearRegion> fetchUserRankingForThisYearRegion(Long regionId, int year , int startIndex , int batchSize) {
		LOG.info( "method to fetch user ranking region list for this year, fetchUserRankingForThisYearRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisYearRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
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
            LOG.error( "Exception caught in fetchUserRankingForThisYearRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForThisYearRegion() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking region list for this year, fetchUserRankingForThisYearRegion() finished." );
        return (List<UserRankingThisYearRegion>) criteria.list();
	}

}
