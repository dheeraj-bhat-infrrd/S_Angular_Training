package com.realtech.socialsurvey.core.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OverviewBranchMonthDao;
import com.realtech.socialsurvey.core.entities.OverviewBranchMonth;
import com.realtech.socialsurvey.core.entities.OverviewUserMonth;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class OverviewBranchMonthDaoImpl extends GenericReportingDaoImpl<OverviewBranchMonth, String> implements OverviewBranchMonthDao
{
    private static final Logger LOG = LoggerFactory.getLogger(OverviewBranchMonthDaoImpl.class);

    @Override
    public OverviewBranchMonth fetchOverviewForBranchBasedOnMonth(Long branchId,int month, int year) throws NullPointerException {
        LOG.info( "method to fetch over view branch based on month,fetchOverviewForBranchBasedOnMonth() started" );
        Criteria criteria = getSession().createCriteria( OverviewBranchMonth.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.AGGREGATE_BY_MONTH, month ) );
            criteria.add( Restrictions.eq( CommonConstants.AGGREGATE_BY_YEAR, year ) );
            LOG.info( "method to fetch over view branch based on month, fetchOverviewForBranchBasedOnMonth() finished." );
            Object result = criteria.uniqueResult();
            if(result == null )return null;
            else return (OverviewBranchMonth) result;
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchOverviewForBranchBasedOnMonth() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchOverviewForBranchBasedOnMonth() ", hibernateException );
        }  
    }
}
