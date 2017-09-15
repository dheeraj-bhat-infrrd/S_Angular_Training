package com.realtech.socialsurvey.core.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OverviewRegionYearDao;
import com.realtech.socialsurvey.core.entities.OverviewRegionMonth;
import com.realtech.socialsurvey.core.entities.OverviewRegionYear;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class OverviewRegionYearDaoImpl extends GenericReportingDaoImpl<OverviewRegionYear, String> implements OverviewRegionYearDao
{
    private static final Logger LOG = LoggerFactory.getLogger(OverviewRegionYearDaoImpl.class);
    
    @Override
    public OverviewRegionYear fetchOverviewForRegionBasedOnYear(Long regionId, int year) throws NullPointerException {
        LOG.info( "method to fetch over view region based on year,fetchOverviewForRegionBasedOnYear() started" );
        Criteria criteria = getSession().createCriteria( OverviewRegionYear.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.eq( CommonConstants.AGGREGATE_BY_YEAR, year ) );
            LOG.info( "method to fetch over view region based on year, fetchOverviewForRegionBasedOnYear() finished." );
            Object result = criteria.uniqueResult();
            if(result == null )return null;
            else return (OverviewRegionYear) result;
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchOverviewForRegionBasedOnYear() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchOverviewForRegionBasedOnYear() ", hibernateException );
        }  
    }

}
