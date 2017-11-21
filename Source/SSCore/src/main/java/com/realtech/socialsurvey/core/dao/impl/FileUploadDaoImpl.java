package com.realtech.socialsurvey.core.dao.impl;

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
import org.springframework.transaction.annotation.Transactional;

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
            criteria.add( Restrictions.eq( CommonConstants.SHOW_ON_UI_COLUMN , true ) );
            criteria.add( Restrictions.in( CommonConstants.FILE_UPLOAD_TYPE_COLUMN, Arrays.asList(
                CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_STATS_REPORT, CommonConstants.FILE_UPLOAD_REPORTING_VERIFIED_USERS_REPORT , CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_USERS_REPORT, CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_RESULTS_REPORT
                ,CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_TRANSACTION_REPORT,CommonConstants.FILE_UPLOAD_REPORTING_USER_RANKING_MONTHLY_REPORT,CommonConstants.FILE_UPLOAD_REPORTING_USER_RANKING_YEARLY_REPORT
                ,CommonConstants.FILE_UPLOAD_REPORTING_INCOMPLETE_SURVEY_REPORT) ) );
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
            criteria.add( Restrictions.eq( CommonConstants.SHOW_ON_UI_COLUMN , true ) );
            criteria.add( Restrictions.in( CommonConstants.FILE_UPLOAD_TYPE_COLUMN, Arrays.asList(
                CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_STATS_REPORT, CommonConstants.FILE_UPLOAD_REPORTING_VERIFIED_USERS_REPORT , CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_USERS_REPORT, CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_RESULTS_REPORT
                ,CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_TRANSACTION_REPORT,CommonConstants.FILE_UPLOAD_REPORTING_USER_RANKING_MONTHLY_REPORT,CommonConstants.FILE_UPLOAD_REPORTING_USER_RANKING_YEARLY_REPORT
                ,CommonConstants.FILE_UPLOAD_REPORTING_INCOMPLETE_SURVEY_REPORT) ) );
            criteria.setProjection( Projections.rowCount() );
            Long count = (Long) criteria.uniqueResult();
            LOG.info( "Method getRecentActivityCountForReporting() finished." );
            return count.longValue();
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in findRecentActivityForReporting().", hibernateException );
            throw new DatabaseException( "HibernateException caught in findRecentActivityForReporting().", hibernateException );
        }
    }
    
    @Override
    @Transactional
    public void changeShowOnUiStatus( FileUpload fileUpload ){
        super.update( fileUpload );
    }
    
    @SuppressWarnings("unchecked")
    @Override
	public FileUpload getLatestActivityForReporting( Long entityId ){
    	Criteria criteria = getSession().createCriteria(FileUpload.class);
    	criteria.add( Restrictions.eq( CommonConstants.FILE_UPLOAD_TYPE_COLUMN , CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_DETAILS_REPORT ) );
    	criteria.setMaxResults(1);
    	criteria.addOrder(Order.desc(CommonConstants.CREATED_ON));
    	List<FileUpload> resultList = criteria.list();
    	if(resultList != null && resultList.size() > 0){
    		return resultList.get(0);
    	}
    	return null;
    }
}
