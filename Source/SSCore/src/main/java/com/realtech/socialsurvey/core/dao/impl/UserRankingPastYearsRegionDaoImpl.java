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
import com.realtech.socialsurvey.core.dao.UserRankingPastYearsRegionDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearRegion;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearsRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastYearsRegionDaoImpl extends GenericReportingDaoImpl<UserRankingPastYearsRegion, String> implements UserRankingPastYearsRegionDao
{

    private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastYearsRegionDaoImpl.class );

    @Override
    public List<UserRankingPastYearsRegion> fetchUserRankingForPastYearsRegion(Long regionId , int startIndex , int batchSize) {
        LOG.info( "method to fetch user ranking region list for past years, fetchUserRankingForPastsYearRegion() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearsRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            if ( startIndex > -1 ) {
                criteria.setFirstResult( startIndex );
            }
            if ( batchSize > -1 ) {
                criteria.setMaxResults( batchSize );
            }
            criteria.addOrder( Order.asc( CommonConstants.RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForPastsYearRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForPastsYearRegion() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking region list for past years, fetchUserRankingForPastsYearRegion() finished." );
        return (List<UserRankingPastYearsRegion>) criteria.list();
    }
}
