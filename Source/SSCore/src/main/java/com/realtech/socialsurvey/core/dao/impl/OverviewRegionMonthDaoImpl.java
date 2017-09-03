package com.realtech.socialsurvey.core.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OverviewRegionMonthDao;
import com.realtech.socialsurvey.core.entities.OverviewRegionMonth;
import com.realtech.socialsurvey.core.entities.OverviewUserMonth;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class OverviewRegionMonthDaoImpl extends GenericReportingDaoImpl<OverviewRegionMonth, String> implements OverviewRegionMonthDao
{

    private static final Logger LOG = LoggerFactory.getLogger(OverviewRegionMonthDaoImpl.class);
    
    @Override
    public OverviewRegionMonth fetchOverviewForRegionBasedOnMonth(Long regionId,int month, int year) throws NullPointerException {
        LOG.info( "method to fetch over view region based on month,fetchOverviewForRegionBasedOnMonth() started" );
        Criteria criteria = getSession().createCriteria( OverviewRegionMonth.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.eq( CommonConstants.AGGREGATE_BY_MONTH, month ) );
            criteria.add( Restrictions.eq( CommonConstants.AGGREGATE_BY_YEAR, year ) );
            LOG.info( "method to fetch over view region based on month, fetchOverviewForRegionBasedOnMonth() finished." );
            Object result = criteria.uniqueResult();
            if(result == null )return null;
            else return (OverviewRegionMonth) result;
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchOverviewForRegionBasedOnMonth() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchOverviewForRegionBasedOnMonth() ", hibernateException );
        }  
    }
}
