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
import com.realtech.socialsurvey.core.dao.NpsReportMonthDao;
import com.realtech.socialsurvey.core.entities.NpsReportMonth;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


@Component
public class NpsReportMonthDaoImpl extends GenericReportingDaoImpl<NpsReportMonth, String> implements NpsReportMonthDao
{

    private static final Logger LOG = LoggerFactory.getLogger( NpsReportMonthDaoImpl.class );


    @SuppressWarnings ( "unchecked")
    @Override
    public List<NpsReportMonth> fetchNpsReportMonth( long companyId, int month, int year ) throws InvalidInputException
    {
        LOG.debug( "method to fetch nps report for month started" );
        
        try {
            Criteria criteria = getSession().createCriteria( NpsReportMonth.class );
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.AGGREGATE_BY_MONTH, month ) );
            criteria.add( Restrictions.eq( CommonConstants.AGGREGATE_BY_YEAR, year ) );
            criteria.addOrder( Order.asc( "companyName" ) );
            criteria.addOrder( Order.asc( "regionName" ) );
            criteria.addOrder( Order.asc( "branchName" ) );
            return (List<NpsReportMonth>) criteria.list();
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchNpsReportMonth() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchNpsReportMonth() ", hibernateException );
        }


    }
}

