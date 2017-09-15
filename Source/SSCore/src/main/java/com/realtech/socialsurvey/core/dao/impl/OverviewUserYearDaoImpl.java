package com.realtech.socialsurvey.core.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OverviewUserYearDao;
import com.realtech.socialsurvey.core.entities.OverviewUserMonth;
import com.realtech.socialsurvey.core.entities.OverviewUserYear;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class OverviewUserYearDaoImpl extends GenericReportingDaoImpl<OverviewUserYear, String> implements OverviewUserYearDao
{
    private static final Logger LOG = LoggerFactory.getLogger(OverviewUserYearDaoImpl.class);
    

    @Override
    public OverviewUserYear fetchOverviewForUserBasedOnYear(Long userId, int year) throws NullPointerException {
        LOG.info( "method to fetch over view user based on year,fetchOverviewForUserBasedOnYear() started" );
        Criteria criteria = getSession().createCriteria( OverviewUserYear.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.USER_ID, userId ) );
            criteria.add( Restrictions.eq( CommonConstants.AGGREGATE_BY_YEAR, year ) );
            LOG.info( "method to fetch over view user based on year, fetchOverviewForUserBasedOnYear() finished." );
            Object result = criteria.uniqueResult();
            if(result == null )return null;
            else return (OverviewUserYear) result;
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchOverviewForUserBasedOnYear() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchOverviewForUserBasedOnYear() ", hibernateException );
        }  
    }

}
