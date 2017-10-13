package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.DigestDao;
import com.realtech.socialsurvey.core.entities.Digest;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


@Component
public class DigestDaoImpl extends GenericReportingDaoImpl<Digest, String> implements DigestDao
{
    private static final Logger LOG = LoggerFactory.getLogger( DigestDaoImpl.class );


    @SuppressWarnings ( "unchecked")
    @Override
    @Transactional
    public List<Digest> fetchDigestDataForNMonthsInAYear( long companyId, int startMonth, int endMonth, int year )
        throws InvalidInputException
    {

        LOG.debug( "method to fetch digest data, fetchDigestDataForNMonths() started." );

        if ( companyId <= 0 ) {
            LOG.error( "company Identifier can not be null" );
            throw new InvalidInputException( "company ID is null." );
        } else if ( startMonth <= 0 || startMonth > 12 ) {
            LOG.error( "start month number can not be outside of the range 1-12" );
            throw new InvalidInputException( "Start month number should always be within 1-12 range." );
        } else if ( endMonth < startMonth || startMonth + ( startMonth - endMonth ) > 12 ) {
            LOG.error( "end month number can not be outside of the range 'start month' - 12" );
            throw new InvalidInputException( "End month number should always be within 'start month' - 12 range." );
        } else if ( year <= 0 ) {
            LOG.error( "start year number can not be less than or equal to zero" );
            throw new InvalidInputException( "Start year number should be greater than zero." );
        }

        try {

            // create criteria object for Digest entity class
            Criteria criteria = getSession().createCriteria( Digest.class );
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.ge( CommonConstants.MONTH, startMonth ) );
            criteria.add( Restrictions.le( CommonConstants.MONTH, endMonth ) );
            criteria.add( Restrictions.eq( CommonConstants.YEAR, year ) );


            LOG.info( "method to fetch digest data, fetchDigestData() finished." );
            return (List<Digest>) criteria.list();

        } catch ( HibernateException hibernateException ) {

            LOG.error( "Exception caught in fetchDigestData() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchDigestData() ", hibernateException );

        }
    }

}
