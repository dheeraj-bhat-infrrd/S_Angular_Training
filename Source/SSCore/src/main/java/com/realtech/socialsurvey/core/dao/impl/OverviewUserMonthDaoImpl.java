package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OverviewUserMonthDao;
import com.realtech.socialsurvey.core.entities.OverviewUserMonth;
import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class OverviewUserMonthDaoImpl extends GenericReportingDaoImpl<OverviewUserMonth, String> implements OverviewUserMonthDao
{

    private static final Logger LOG = LoggerFactory.getLogger(OverviewUserMonthDaoImpl.class);
    
    @Override
    public OverviewUserMonth fetchOverviewForUserBasedOnMonth(Long userId,int month, int year) throws NullPointerException {
        LOG.info( "method to fetch over view user based on month,fetchOverviewForUserBasedOnMonth() started" );
        Criteria criteria = getSession().createCriteria( OverviewUserMonth.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.USER_ID, userId ) );
            criteria.add( Restrictions.eq( CommonConstants.AGGREGATE_BY_MONTH, month ) );
            criteria.add( Restrictions.eq( CommonConstants.AGGREGATE_BY_YEAR, year ) );
            LOG.info( "method to fetch over view user based on month, fetchOverviewForUserBasedOnMonth() finished." );
            Object result = criteria.uniqueResult();
            if(result == null )return null;
            else return (OverviewUserMonth) result;
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchOverviewForUserBasedOnMonth() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchOverviewForUserBasedOnMonth() ", hibernateException );
        }  
    }
}
