package com.realtech.socialsurvey.core.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OverviewBranchYearDao;
import com.realtech.socialsurvey.core.entities.OverviewBranchMonth;
import com.realtech.socialsurvey.core.entities.OverviewBranchYear;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class OverviewBranchYearDaoImpl extends GenericReportingDaoImpl<OverviewBranchYear, String> implements OverviewBranchYearDao
{

    private static final Logger LOG = LoggerFactory.getLogger(OverviewBranchYearDaoImpl.class);

    @Override
    public OverviewBranchYear fetchOverviewForBranchBasedOnYear(Long branchId, int year) throws NullPointerException {
        LOG.info( "method to fetch over view branch based on year,fetchOverviewForBranchBasedOnYear() started" );
        Criteria criteria = getSession().createCriteria( OverviewBranchYear.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.AGGREGATE_BY_YEAR, year ) );
            LOG.info( "method to fetch over view branch based on year, fetchOverviewForBranchBasedOnYear() finished." );
            Object result = criteria.uniqueResult();
            if(result == null )return null;
            else return (OverviewBranchYear) result;
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchOverviewForBranchBasedOnYear() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchOverviewForBranchBasedOnYear() ", hibernateException );
        }  
    }
}
