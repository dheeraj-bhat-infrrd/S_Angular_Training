package com.realtech.socialsurvey.core.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OverviewCompanyMonthDao;
import com.realtech.socialsurvey.core.entities.OverviewCompanyMonth;
import com.realtech.socialsurvey.core.entities.OverviewRegionMonth;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class OverviewCompanyMonthDaoImpl extends GenericReportingDaoImpl<OverviewCompanyMonth, String> implements OverviewCompanyMonthDao
{

    private static final Logger LOG = LoggerFactory.getLogger(OverviewCompanyMonthDaoImpl.class);
    
    @Override
    public OverviewCompanyMonth fetchOverviewForCompanyBasedOnMonth(Long companyId,int month, int year) throws NullPointerException {
        LOG.info( "method to fetch over view company based on month,fetchOverviewForCompanyBasedOnMonth() started" );
        Criteria criteria = getSession().createCriteria( OverviewCompanyMonth.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.AGGREGATE_BY_MONTH, month ) );
            criteria.add( Restrictions.eq( CommonConstants.AGGREGATE_BY_YEAR, year ) );
            LOG.info( "method to fetch over view company based on month, fetchOverviewForCompanyBasedOnMonth() finished." );
            Object result = criteria.uniqueResult();
            if(result == null )return null;
            else return (OverviewCompanyMonth) result;
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchOverviewForCompanyBasedOnMonth() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchOverviewForCompanyBasedOnMonth() ", hibernateException );
        }  
    }
}
