package com.realtech.socialsurvey.core.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OverviewCompanyYearDao;
import com.realtech.socialsurvey.core.entities.OverviewCompanyMonth;
import com.realtech.socialsurvey.core.entities.OverviewCompanyYear;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class OverviewCompanyYearDaoImpl extends GenericReportingDaoImpl<OverviewCompanyYear , String> implements OverviewCompanyYearDao
{
    private static final Logger LOG = LoggerFactory.getLogger(OverviewCompanyYearDaoImpl.class);

    @Override
    public OverviewCompanyYear fetchOverviewForCompanyBasedOnYear(Long companyId, int year) throws NullPointerException {
        LOG.info( "method to fetch over view company based on year,fetchOverviewForCompanyBasedOnYear() started" );
        Criteria criteria = getSession().createCriteria( OverviewCompanyYear.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.AGGREGATE_BY_YEAR, year ) );
            LOG.info( "method to fetch over view company based on year, fetchOverviewForCompanyBasedOnYear() finished." );
            Object result = criteria.uniqueResult();
            if(result == null )return null;
            else return (OverviewCompanyYear) result;
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchOverviewForCompanyBasedOnYear() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchOverviewForCompanyBasedOnYear() ", hibernateException );
        }  
    }

}
