package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.NpsReportWeekDao;
import com.realtech.socialsurvey.core.entities.NpsReportWeek;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


@Component
public class NpsReportWeekDaoImpl extends GenericReportingDaoImpl<NpsReportWeek, String> implements NpsReportWeekDao
{

    private static final Logger LOG = LoggerFactory.getLogger( NpsReportWeekDaoImpl.class );


    @SuppressWarnings ( "unchecked")
    @Override
    public List<NpsReportWeek> fetchNpsReportWeek( long companyId, int week, int year ) throws InvalidInputException
    {
        LOG.debug( "method to fetch nps report for week started" );
        try {
            Criteria criteria = getSession().createCriteria( NpsReportWeek.class );
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.AGGREGATE_BY_WEEK, week ) );
            criteria.add( Restrictions.eq( CommonConstants.AGGREGATE_BY_YEAR, year ) );
            criteria.addOrder( Order.asc( "companyId" ) );
            criteria.addOrder( Order.asc( "regionId" ) );
            criteria.addOrder( Order.asc( "branchId" ) );
            return (List<NpsReportWeek>) criteria.list();
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchNpsReportWeek() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchNpsReportWeek() ", hibernateException );
        }


    }
}

