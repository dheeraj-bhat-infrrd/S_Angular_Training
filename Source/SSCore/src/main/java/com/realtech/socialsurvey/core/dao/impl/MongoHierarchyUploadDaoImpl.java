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
import com.realtech.socialsurvey.core.dao.HierarchyUploadDao;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.HierarchyUploadIntermediate;
import com.realtech.socialsurvey.core.entities.ParsedHierarchyUpload;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;


@Repository
public class MongoHierarchyUploadDaoImpl implements HierarchyUploadDao
{

    private static final Logger LOG = LoggerFactory.getLogger( MongoHierarchyUploadDaoImpl.class );

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * Method to save hierarchy upload in mongo
     * @param hierarchyUpload
     * @throws InvalidInputException 
     */
    @Override
    public void reinsertHierarchyUploadObjectForACompany( HierarchyUpload hierarchyUpload ) throws InvalidInputException
    {
        LOG.info( "Method to save hierarchy upload object started" );
        saveHierarchyUpload( hierarchyUpload, CommonConstants.HIERARCHY_UPLOAD_COLLECTION );
        LOG.info( "Method to save hierarchy upload object finished" );
    }


    /**
     * Method to save hierarchyupload object into a specific collection
     * @param hierarchyUpload
     * @param collectionName
     * @throws InvalidInputException
     */
    void saveHierarchyUpload( HierarchyUpload hierarchyUpload, String collectionName ) throws InvalidInputException
    {
        if ( hierarchyUpload == null ) {
            LOG.error( "The hierarchy upload object is null" );
            throw new InvalidInputException( "The hierarchy upload object is null" );
        }

        //Delete previous instance
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( hierarchyUpload.getCompanyId() ) );
        mongoTemplate.remove( query, HierarchyUpload.class, collectionName );
        mongoTemplate.insert( hierarchyUpload, collectionName );
    }


    /**
     * Method to save HierarchyUploadIntermediate object into a specific collection
     * @param hierarchyIntermediate
     * @param collectionName
     * @throws InvalidInputException
     */
    void saveHierarchyUploadIntermediate( HierarchyUploadIntermediate hierarchyIntermediate, String collectionName )
        throws InvalidInputException
    {
        if ( hierarchyIntermediate == null ) {
            LOG.error( "The HierarchyUploadIntermediate object is null" );
            throw new InvalidInputException( "The HierarchyUploadIntermediate object is null" );
        }

        //Delete previous instance
        Query query = new Query();
        query.addCriteria(
            Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( hierarchyIntermediate.getCompany().getCompanyId() ) );
        mongoTemplate.remove( query, HierarchyUpload.class, collectionName );
        mongoTemplate.insert( hierarchyIntermediate, collectionName );
    }


    /**
     * Method to update ParsedHierarchyUpload object into a specific collection
     * @param hierarchyIntermediate
     * @param collectionName
     * @throws InvalidInputException
     */
    @Override
    public synchronized void reinsertParsedHierarchyUpload( ParsedHierarchyUpload parsedHierarchyUpload )
        throws InvalidInputException
    {
        if ( parsedHierarchyUpload == null ) {
            LOG.error( "The ParsedHierarchyUpload object is null" );
            throw new InvalidInputException( "The ParsedHierarchyUpload object is null" );
        }

        //Delete previous instance
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( parsedHierarchyUpload.getCompanyId() ) );
        mongoTemplate.remove( query, ParsedHierarchyUpload.class, CommonConstants.PARSED_HIERARCHY_UPLOAD_COLLECTION );
        mongoTemplate.insert( parsedHierarchyUpload, CommonConstants.PARSED_HIERARCHY_UPLOAD_COLLECTION );
    }


    /**
     * Method to fetch hierarchy upload object from a specific collection
     * @param companyId
     * @param collectionName
     * @return
     * @throws InvalidInputException
     */
    HierarchyUpload fetchHierarchyUploadFromCollection( long companyId, String collectionName ) throws InvalidInputException
    {
        //Invalid check
        if ( companyId <= 0l ) {
            throw new InvalidInputException( "Invalid CompanyId : " + companyId );
        }
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( companyId ) );
        //Fetch from mongo
        HierarchyUpload hierarchyUpload = mongoTemplate.findOne( query, HierarchyUpload.class, collectionName );
        return hierarchyUpload;
    }


    /**
     * Method to update status for parsed hierarchy upload object
     * @param companyId
     * @param collectionName
     * @return
     * @throws InvalidInputException
     */
    @Override
    public void updateStatusForParsedHierarchyUpload( long companyId, int status ) throws InvalidInputException
    {
        //Invalid check
        if ( companyId <= 0l ) {
            throw new InvalidInputException( "Invalid CompanyId : " + companyId );
        }

        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( companyId ) );

        Update update = new Update();
        update.set( CommonConstants.STATUS_COLUMN, status );

        mongoTemplate.updateFirst( query, update, ParsedHierarchyUpload.class,
            CommonConstants.PARSED_HIERARCHY_UPLOAD_COLLECTION );
    }


    @Override
    public List<ParsedHierarchyUpload> getActiveHierarchyUploads() throws NoRecordsFetchedException
    {
        Query query = new Query();
        query.addCriteria(
            Criteria.where( CommonConstants.STATUS_COLUMN ).is( CommonConstants.HIERARCHY_UPLOAD_STATUS_INITIATED ) );

        List<ParsedHierarchyUpload> uploads = mongoTemplate.find( query, ParsedHierarchyUpload.class,
            CommonConstants.PARSED_HIERARCHY_UPLOAD_COLLECTION );

        if ( uploads == null || uploads.isEmpty() ) {
            throw new NoRecordsFetchedException( "No hierarchy upload entries exist" );
        }
        return uploads;
    }


    @Override
    public ParsedHierarchyUpload getParsedHierarchyUpload( long companyId )
        throws NoRecordsFetchedException, InvalidInputException
    {
        //Invalid check
        if ( companyId <= 0l ) {
            throw new InvalidInputException( "Invalid CompanyId : " + companyId );
        }
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( companyId ) );

        //Fetch from mongo
        ParsedHierarchyUpload upload = mongoTemplate.findOne( query, ParsedHierarchyUpload.class,
            CommonConstants.PARSED_HIERARCHY_UPLOAD_COLLECTION );

        if ( upload == null ) {
            throw new NoRecordsFetchedException( "No Hierarchy Upload records found for company with Id: " + companyId );
        }

        return upload;
    }
    
    /**
     * Method to fetch hierarchy upload for company
     * @param companyId
     * @return
     * @throws InvalidInputException
     */
    @Override
    public HierarchyUpload getHierarchyUploadByCompany( long companyId ) throws InvalidInputException
    {
        LOG.debug( "Method to get hierarchy upload for companyId : " + companyId + " started" );
        //Fetch from mongo
        HierarchyUpload hierarchyUpload = fetchHierarchyUploadFromCollection( companyId,
            CommonConstants.HIERARCHY_UPLOAD_COLLECTION );
        LOG.debug( "Method to get hierarchy upload for companyId : " + companyId + " finished" );
        return hierarchyUpload;
    }

}
