package com.realtech.socialsurvey.core.dao.impl;


import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
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

    

    @Override
    public JobLogDetails getJobLogDetailsOfLastSuccessfulRun() throws InvalidInputException
    {
        LOG.debug(
            "method to fetch the job-log details of last successful run, getJobLogDetailsOfLastSuccessfulRun() started." );
        try {
            // create criteria object for JobLog entity class
            Criteria criteria = getSession().createCriteria( JobLogDetails.class );
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_FINISHED ) );
            criteria.add(Restrictions.eq(CommonConstants.JOB_NAME, CommonConstants.CENTRALIZED_JOB_NAME));
            criteria.addOrder( Order.desc( CommonConstants.JOB_LOG_ID ) );
            criteria.setMaxResults( 1 );
            LOG.debug( "method to fetch the job-log details of last successful run, getJobLogDetailsOfLastSuccessfulRun() finished." );
            return (JobLogDetails) criteria.uniqueResult();
            
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in getJobLogDetailsOfLastSuccessfulRun() :{} ", hibernateException );
            throw new DatabaseException( "Exception caught in getJobLogDetailsOfLastSuccessfulRun() ", hibernateException );
        }

    }
    
    @Override
    public JobLogDetails getJobLogDetailsOfLatestRun() throws InvalidInputException
    {
        LOG.debug(
            "method to fetch the job-log details of last run, getJobLogDetailsOfLatestRun() started." );
        try {
            // create criteria object for JobLog entity class
            Criteria criteria = getSession().createCriteria( JobLogDetails.class );
            criteria.addOrder( Order.desc( CommonConstants.JOB_LOG_ID ) );
            criteria.setMaxResults( 1 );
            LOG.debug( "method to fetch the job-log details of last run, getJobLogDetailsOfLatestRun() finished." );
            return (JobLogDetails) criteria.uniqueResult();
            
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in getJobLogDetailsOfLatestRun() :{} ", hibernateException );
            throw new DatabaseException( "Exception caught in getJobLogDetailsOfLatestRun() ", hibernateException );
        }

    }
    
    @Override
    public JobLogDetails getLastCentrelisedRun()
    {
    	LOG.debug("method to fetch the job-log details of last scheduled centrelised run getLastCentrelisedRun() started");
        try {
            // create criteria object for JobLog entity class
            Criteria criteria = getSession().createCriteria( JobLogDetails.class );
            criteria.add(Restrictions.eq(CommonConstants.IS_MANUAL, false));
            criteria.add(Restrictions.eq(CommonConstants.JOB_NAME, CommonConstants.CENTRALIZED_JOB_NAME));
            criteria.addOrder( Order.desc( CommonConstants.JOB_LOG_ID ) );
            criteria.setMaxResults( 1 );
            LOG.debug( "method to fetch the job-log details of last run, getJobLogDetailsOfLatestRun() finished." );
            return (JobLogDetails) criteria.uniqueResult();
            
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in getJobLogDetailsOfLatestRun() :{} ", hibernateException );
            throw new DatabaseException( "Exception caught in getJobLogDetailsOfLatestRun() ", hibernateException );
        }
    	
    }
    
    @Override
    public long insertJobLog(JobLogDetails jobLogDetails) throws InvalidInputException
    {
        LOG.debug(
            "method to insert the job-log details for user ranking, insertJobLog() started." );
        try {
        	super.save(jobLogDetails);
            return jobLogDetails.getJobLogId();
            
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in insertJobLog() :{} ", hibernateException );
            throw new DatabaseException( "Exception caught in insertJobLog() ", hibernateException );
        }

    }
    
    @Override
    public void updateJobLog(JobLogDetails jobLogDetails) throws InvalidInputException
    {
        LOG.debug(
            "method to insert the job-log details for user ranking, insertJobLog() started." );
        try {
        	super.saveOrUpdate(jobLogDetails);            
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in insertJobLog() :{} ", hibernateException );
            throw new DatabaseException( "Exception caught in insertJobLog() ", hibernateException );
        }

    }
    
    @Override
    public JobLogDetails getJobLogDetailsOfLatestRunForEntity(long entityId , String entityType , String jobName) throws InvalidInputException
    {
        LOG.debug(
            "method to fetch the job-log details of last run for entity, getJobLogDetailsOfLatestRunForEntity() started." );
        try {
            // create criteria object for JobLog entity class
            Criteria criteria = getSession().createCriteria( JobLogDetails.class );
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_FINISHED ) );
            criteria.add(Restrictions.eq(CommonConstants.ENTITY_ID_COLUMN, entityId));
            criteria.add(Restrictions.eq(CommonConstants.ENTITY_TYPE_COLUMN, entityType));
            criteria.add(Restrictions.eq(CommonConstants.JOB_NAME,jobName));
            criteria.addOrder( Order.desc( CommonConstants.JOB_LOG_ID ) );
            criteria.setMaxResults( 1 );
            LOG.debug( "method to fetch the job-log details of last run of entity, getJobLogDetailsOfLatestRunForEntity() finished." );
            return (JobLogDetails) criteria.uniqueResult();
            
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in getJobLogDetailsOfLatestRun() :{} ", hibernateException );
            throw new DatabaseException( "Exception caught in getJobLogDetailsOfLatestRun() ", hibernateException );
        }

    }


}
