package com.realtech.socialsurvey.core.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.FileUploadDao;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class FileUploadDaoImpl extends GenericDaoImpl<FileUpload, Long> implements FileUploadDao
{
    private static final Logger LOG = LoggerFactory.getLogger( FileUploadDaoImpl.class );


    @Override
    public List<FileUpload> findRecentActivityForReporting(long entityId , String entityType , int startIndex , int batchSize){
        LOG.info( "method to fetch Recent Activity findRecentActivityForReporting() started " );
        Criteria criteria = getSession().createCriteria( FileUpload.class );
        try{
            criteria.add( Restrictions.eq( CommonConstants.PROFILE_VALUE_COLUMN , entityId ) );
            criteria.add( Restrictions.eq( CommonConstants.PROFILE_LEVEL_COLUMN , entityType ) );
            criteria.add( Restrictions.in( CommonConstants.FILE_UPLOAD_TYPE_COLUMN, Arrays.asList(
                CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_STATS_REPORT, CommonConstants.FILE_UPLOAD_REPORTING_USER_ADOPTION_REPORT) ) );
            if ( startIndex > -1 ) {
                criteria.setFirstResult( startIndex );
            }
            if ( batchSize > -1 ) {
                criteria.setMaxResults( batchSize );
            }
            criteria.addOrder( Order.desc( CommonConstants.MODIFIED_ON_COLUMN ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in findRecentActivityForReporting().", hibernateException );
            throw new DatabaseException( "HibernateException caught in findRecentActivityForReporting().", hibernateException );
        }
        return (List<FileUpload>)criteria.list();
    }
    
    @Override
    public long getRecentActivityCountForReporting(long entityId , String entityType){
        LOG.info( "method to fetch Recent Activity findRecentActivityForReporting() started " );
        Criteria criteria = getSession().createCriteria( FileUpload.class );
        try{

            criteria.add( Restrictions.eq( CommonConstants.PROFILE_VALUE_COLUMN , entityId ) );
            criteria.add( Restrictions.eq( CommonConstants.PROFILE_LEVEL_COLUMN , entityType ) );
            criteria.add( Restrictions.in( CommonConstants.FILE_UPLOAD_TYPE_COLUMN, Arrays.asList(
                CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_STATS_REPORT, CommonConstants.FILE_UPLOAD_REPORTING_USER_ADOPTION_REPORT) ) );
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.info( "Method getRecentActivityCountForReporting() finished." );
            return count.longValue();
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in findRecentActivityForReporting().", hibernateException );
            throw new DatabaseException( "HibernateException caught in findRecentActivityForReporting().", hibernateException );
        }
    }
}
