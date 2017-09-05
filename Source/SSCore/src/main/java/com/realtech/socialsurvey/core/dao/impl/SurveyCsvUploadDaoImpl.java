package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyCsvUploadDao;
import com.realtech.socialsurvey.core.entities.SurveyCsvInfo;


@Repository
public class SurveyCsvUploadDaoImpl implements SurveyCsvUploadDao
{
    private static final Logger LOG = LoggerFactory.getLogger( SurveyCsvUploadDaoImpl.class );

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String HIERARCHY_TYPE = "hierarchyType";
    private static final String HIERARCHY_ID = "hierarchyId";
    private static final String FILE_URL = "fileUrl";
    private static final String FILE_NAME = "fileName";
    private static final String UPLOADED_DATE = "uploadedDate";
    private static final String UPLOADER_EMAIL = "uploaderEmail";
    private static final String USER_ID = "userId";
    private static final String CSV_UPLOAD_COMPLETED_DATE = "csvUploadCompletedDate";


    @Override
    public void createEntryForSurveyCsvUpload( SurveyCsvInfo csvInfo )
    {
        LOG.debug( "method SurveyCsvUploadDaoImpl.createEntryForSurveyCsvUpload called." );
        mongoTemplate.insert( csvInfo, CommonConstants.SURVEY_CSV_UPLOAD_COLLECTION );
    }


    @Override
    public void updateStatusForSurveyCsvUpload( String _id, int status )
    {
        LOG.debug( "method SurveyCsvUploadDaoImpl.updateStatusForSurveyCsvUpload called." );

        Query updateQuery = new Query();
        updateQuery.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( _id ) );

        Update update = new Update();
        update.set( CommonConstants.STATUS_COLUMN, status );

        mongoTemplate.updateFirst( updateQuery, update, CommonConstants.SURVEY_CSV_UPLOAD_COLLECTION );
    }


    @Override
    public List<SurveyCsvInfo> getActiveSurveyCsvUploads()
    {
        LOG.debug( "method SurveyCsvUploadDaoImpl.getActiveSurveyCsvUploads called." );

        Query searchQuery = new Query();
        searchQuery.addCriteria( Criteria.where( CommonConstants.STATUS_COLUMN ).is( CommonConstants.STATUS_ACTIVE ) );

        return mongoTemplate.find( searchQuery, SurveyCsvInfo.class, CommonConstants.SURVEY_CSV_UPLOAD_COLLECTION );

    }


    @Override
    public void updateSurveyCsvUpload( SurveyCsvInfo csvInfo )
    {
        LOG.debug( "method SurveyCsvUploadDaoImpl.updateStatusForSurveyCsvUpload called." );

        Query updateQuery = new Query();
        updateQuery.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( csvInfo.get_id() ) );

        Update update = new Update();
        update.set( CommonConstants.STATUS_COLUMN, csvInfo.getStatus() );
        update.set( HIERARCHY_TYPE, csvInfo.getHierarchyType() );
        update.set( HIERARCHY_ID, csvInfo.getHierarchyId() );
        update.set( FILE_URL, csvInfo.getFileUrl() );
        update.set( FILE_NAME, csvInfo.getFileName() );
        update.set( UPLOADED_DATE, csvInfo.getUploadedDate() );
        update.set( UPLOADER_EMAIL, csvInfo.getUploaderEmail() );
        update.set( USER_ID, csvInfo.getInitiatedUserId() );
        update.set( CSV_UPLOAD_COMPLETED_DATE, csvInfo.getCsvUploadCompletedDate() );

        mongoTemplate.updateFirst( updateQuery, update, CommonConstants.SURVEY_CSV_UPLOAD_COLLECTION );
    }


    @Override
    public boolean doesFileUploadExist( String fileName, String uploaderEmail )
    {
        LOG.debug( "method SurveyCsvUploadDaoImpl.doesFileUploadExist called." );

        Query searchQuery = new Query();
        searchQuery.addCriteria( Criteria.where( FILE_NAME ).is( fileName ) );
        searchQuery.addCriteria( Criteria.where( UPLOADER_EMAIL ).is( uploaderEmail ) );
        searchQuery.addCriteria( Criteria.where( CommonConstants.STATUS_COLUMN ).is( CommonConstants.STATUS_ACTIVE ) );
        long count = mongoTemplate.count( searchQuery, CommonConstants.SURVEY_CSV_UPLOAD_COLLECTION );

        return count > 0 ? true : false;

    }
}
