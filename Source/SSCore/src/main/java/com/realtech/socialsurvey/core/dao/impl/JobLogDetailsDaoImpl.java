package com.realtech.socialsurvey.core.dao.impl;


import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.JobLogDetailsDao;
import com.realtech.socialsurvey.core.entities.JobLogDetails;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


@Component
public class JobLogDetailsDaoImpl extends GenericReportingDaoImpl<JobLogDetails, Long>implements JobLogDetailsDao
{


    private static final Logger LOG = LoggerFactory.getLogger( JobLogDetailsDaoImpl.class );


    @SuppressWarnings ( "unchecked")
    @Override
    public JobLogDetails getLatestJobLogDetails()
    {

        LOG.debug( "method to fetch the latest job-log details, getLatestJobLogDetails() started." );
        try {

            // create criteria object for JobLog entity class
            Criteria criteria = getSession().createCriteria( JobLogDetails.class );
            DetachedCriteria maxId = DetachedCriteria.forClass( JobLogDetails.class )
                .setProjection( Projections.max( "jobLogId" ) );
            criteria.add( Property.forName( "jobLogId" ).eq( maxId ) );
            criteria.setMaxResults( 1 );
            LOG.info( "method to fetch the latest job-log details, getLatestJobLogDetails() finished.",
                criteria.uniqueResult() );
            Object result = criteria.uniqueResult();
            if ( result == null )
                return null;
            else
                return (JobLogDetails) result;

        } catch ( HibernateException hibernateException ) {

            LOG.error( "Exception caught in getLatestJobLogDetails() :{} ", hibernateException );
            throw new DatabaseException( "Exception caught in getLatestJobLogDetails() ", hibernateException );

        }

    }
    

    @SuppressWarnings ( "unchecked")
    @Override
    public JobLogDetails getJobLogDetailsOfLastSuccessfulRun() throws InvalidInputException
    {
        LOG.debug(
            "method to fetch the job-log details of last successful run, getJobLogDetailsOfLastSuccessfulRun() started." );
        try {
            // create criteria object for JobLog entity class
            Criteria criteria = getSession().createCriteria( JobLogDetails.class );
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_FINISHED ) );
            criteria.addOrder( Order.desc( "jobLogId" ) );
            criteria.setMaxResults( 1 );
            LOG.debug( "method to fetch the job-log details of last successful run, getJobLogDetailsOfLastSuccessfulRun() finished." );
            return (JobLogDetails) criteria.uniqueResult();
            
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in getJobLogDetailsOfLastSuccessfulRun() :{} ", hibernateException );
            throw new DatabaseException( "Exception caught in getJobLogDetailsOfLastSuccessfulRun() ", hibernateException );
        }

    }


}
