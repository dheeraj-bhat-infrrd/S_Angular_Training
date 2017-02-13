package com.realtech.socialsurvey.core.services.reports.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyReportsSearch;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.reports.AdminReports;


@Component
public class AdminReportsImpl implements AdminReports
{

    private static final Logger LOG = LoggerFactory.getLogger( AdminReportsImpl.class );

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private GenericDao<FileUpload, Long> fileUploadDao;


    @Override
    @Transactional ( readOnly = true)
    public List<Company> companyCreationReports( CompanyReportsSearch search )
        throws InvalidInputException, NoRecordsFetchedException
    {
        List<Company> companyList = new ArrayList<Company>();
        if ( search != null ) {

            LOG.info( "Report criteria: " + search.toString() );
            if ( search.getCompanyId() != 0 ) {
                Company company = companyDao.findById( Company.class, search.getCompanyId() );
                if ( company != null ) {
                    if ( search.getLowerEndTime() != null && search.getHigherEndTime() != null ) {
                        LOG.debug( "User has specified time filter as well " );
                        if ( search.getLowerEndTime().before( company.getCreatedOn() )
                            && search.getHigherEndTime().after( company.getCreatedOn() ) ) {
                            companyList.add( company );
                        }
                    } else {
                        companyList.add( company );
                    }
                    companyList.add( company );
                }
            } else if ( search.getCompanyIds() != null && !search.getCompanyIds().isEmpty() ) {
                for ( Long companyId : search.getCompanyIds() ) {
                    Company company = null;
                    if ( companyId != null ) {
                        company = companyDao.findById( Company.class, companyId );
                    }
                    if ( company != null ) {
                        if ( search.getLowerEndTime() != null && search.getHigherEndTime() != null ) {
                            LOG.debug( "User has specified time filter as well " );
                            if ( search.getLowerEndTime().before( company.getCreatedOn() )
                                && search.getHigherEndTime().after( company.getCreatedOn() ) ) {
                                companyList.add( company );
                            }
                        } else {
                            companyList.add( company );
                        }
                    }

                }
            } else if ( search.getLowerEndTime() != null ) {
                if ( search.getHigherEndTime() != null ) {
                    LOG.debug( "Search between time interval" );
                    companyList = companyDao.searchBetweenTimeIntervals( search.getLowerEndTime(), search.getHigherEndTime() );
                } else {
                    LOG.debug( "Search from start time to end" );
                    companyList = companyDao.searchBetweenTimeIntervals( search.getLowerEndTime(), null );
                }
            } else {
                LOG.debug( "No filter applied, hence searching all companies" );
                companyList = companyDao.findAll( Company.class );
            }
        } else {
            LOG.warn( "No criteria set. Will get all the records" );
            companyList = companyDao.findAll( Company.class );
        }
        return companyList;
    }


    /**
     * Method to create an entry in the file upload table for billing report
     */
    @Override
    @Transactional
    public void createEntryInFileUploadForBillingReport( String mailId )
    {
        LOG.info( "Method createEntryInFileUploadForBillingReport() started" );
        //check if an entry already exists
        LOG.info( "Check if billing report entries exist" );
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.FILE_UPLOAD_TYPE_COLUMN, CommonConstants.FILE_UPLOAD_BILLING_REPORT );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_INACTIVE );
        List<FileUpload> filesToBeUploaded = fileUploadDao.findByKeyValue( FileUpload.class, queries );
        if ( filesToBeUploaded == null || filesToBeUploaded.isEmpty() ) {
            //Entry doesn't exist. Create one.
            LOG.debug( "Entry does not exist. Creating one." );
            FileUpload entity = new FileUpload();
            entity.setAdminUserId( CommonConstants.REALTECH_ADMIN_ID );
            entity.setCompany( companyDao.findById( Company.class, CommonConstants.DEFAULT_COMPANY_ID ) );
            entity.setStatus( CommonConstants.STATUS_ACTIVE );
            entity.setUploadType( CommonConstants.FILE_UPLOAD_BILLING_REPORT );
            if ( mailId == null || mailId.isEmpty() ) {
                entity.setFileName( "" );
            } else {
                entity.setFileName( mailId );
            }
            Timestamp currentTime = new Timestamp( System.currentTimeMillis() );
            entity.setCreatedOn( currentTime );
            entity.setModifiedOn( currentTime );
            fileUploadDao.save( entity );
        } else {
            //Entries exist. Change it to active.
            FileUpload entity = filesToBeUploaded.get( 0 );
            LOG.debug( "Entry exists. Modifying it. ID : " + entity.getFileUploadId() );
            entity.setStatus( CommonConstants.STATUS_ACTIVE );
            entity.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            if ( mailId == null || mailId.isEmpty() ) {
                entity.setFileName( "" );
            } else {
                entity.setFileName( mailId );
            }
            fileUploadDao.update( entity );
        }
        LOG.info( "Method createEntryInFileUploadForBillingReport() finished" );
    }


    /**
     * Method to create an entry in the file upload table for company user report
     * @throws InvalidInputException 
     */
    @Override
    @Transactional
    public void createEntryInFileUploadForCompanyUserReport( String mailId, long companyId ) throws InvalidInputException
    {
        LOG.info( "Method createEntryInFileUploadForCompanyUserReport() started" );

        Company company = companyDao.findById( Company.class, companyId );
        if ( company == null || company.getCompanyId() <= 0 ) {
            throw new InvalidInputException( "Invalid companyId passed" );
        }


        LOG.debug( "Entry does not exist. Creating one." );
        FileUpload entity = new FileUpload();
        entity.setAdminUserId( CommonConstants.REALTECH_ADMIN_ID );
        entity.setCompany( company );
        entity.setStatus( CommonConstants.STATUS_ACTIVE );
        entity.setUploadType( CommonConstants.FILE_UPLOAD_COMPANY_USERS_REPORT );
        if ( mailId == null || mailId.isEmpty() ) {
            entity.setFileName( "" );
        } else {
            entity.setFileName( mailId );
        }
        Timestamp currentTime = new Timestamp( System.currentTimeMillis() );
        entity.setCreatedOn( currentTime );
        entity.setModifiedOn( currentTime );
        fileUploadDao.save( entity );

        LOG.info( "Method createEntryInFileUploadForCompanyUserReport() finished" );
    }


    /**
     * Method to create an entry in the file upload table for company hierarchy report
     * @throws InvalidInputException 
     */
    @Override
    @Transactional
    public void createEntryInFileUploadForCompanyHierarchyReport( String mailId, long companyId ) throws InvalidInputException
    {
        LOG.info( "Method createEntryInFileUploadForCompanyHierarchyReport() started" );

        Company company = companyDao.findById( Company.class, companyId );
        if ( company == null || company.getCompanyId() <= 0 ) {
            throw new InvalidInputException( "Invalid companyId passed" );
        }


        LOG.debug( "Entry does not exist. Creating one." );
        FileUpload entity = new FileUpload();
        entity.setAdminUserId( CommonConstants.REALTECH_ADMIN_ID );
        entity.setCompany( company );
        entity.setStatus( CommonConstants.STATUS_ACTIVE );
        entity.setUploadType( CommonConstants.FILE_UPLOAD_COMPANY_HIERARCHY_REPORT );
        if ( mailId == null || mailId.isEmpty() ) {
            entity.setFileName( "" );
        } else {
            entity.setFileName( mailId );
        }
        Timestamp currentTime = new Timestamp( System.currentTimeMillis() );
        entity.setCreatedOn( currentTime );
        entity.setModifiedOn( currentTime );
        fileUploadDao.save( entity );

        LOG.info( "Method createEntryInFileUploadForCompanyHierarchyReport() finished" );
    }


    @Override
    public void createEntryInFileUploadForCompanyRegistrationReport( String mailId, Date startDate, Date endDate )
    {
        LOG.info( "Method createEntryInFileUploadForCompanyReport() started" );
        FileUpload entity = new FileUpload();
        entity.setAdminUserId( CommonConstants.REALTECH_ADMIN_ID );
        entity.setCompany( companyDao.findById( Company.class, CommonConstants.DEFAULT_COMPANY_ID ) );
        entity.setStatus( CommonConstants.STATUS_ACTIVE );
        entity.setUploadType( CommonConstants.FILE_UPLOAD_COMPANY_REGISTRATION_REPORT );

        if ( mailId == null || mailId.isEmpty() ) {
            entity.setFileName( "" );
        } else {
            entity.setFileName( mailId );
        }

        Timestamp currentTime = new Timestamp( System.currentTimeMillis() );
        entity.setCreatedOn( currentTime );
        entity.setModifiedOn( currentTime );

        if ( startDate != null ) {
            Timestamp startTime = new Timestamp( startDate.getTime() );
            entity.setStartDate( startTime );
        }

        if ( endDate != null ) {
            Timestamp endTime = new Timestamp( endDate.getTime() );
            entity.setEndDate( endTime );
        }

        fileUploadDao.save( entity );

        LOG.info( "Method createEntryInFileUploadForCompanyReport() finished" );
    }


    @Override
    public void createEntryInFileUploadForSurveyDataReport( String mailId, Date startDate, Date endDate, long iden,
        String profileLevel, long userId, Company company )
    {
        LOG.info( "Method createEntryInFileUploadForSurveyDataReport() started" );
        FileUpload entity = new FileUpload();
        entity.setAdminUserId( userId );
        entity.setCompany( company );
        entity.setStatus( CommonConstants.STATUS_ACTIVE );
        entity.setUploadType( CommonConstants.FILE_UPLOAD_SURVEY_DATA_REPORT );
        entity.setProfileLevel( profileLevel );
        entity.setProfileValue( iden );

        if ( mailId == null || mailId.isEmpty() ) {
            entity.setFileName( "" );
        } else {
            entity.setFileName( mailId );
        }

        Timestamp currentTime = new Timestamp( System.currentTimeMillis() );
        entity.setCreatedOn( currentTime );
        entity.setModifiedOn( currentTime );

        if ( startDate != null ) {
            Timestamp startTime = new Timestamp( startDate.getTime() );
            entity.setStartDate( startTime );
        }

        if ( endDate != null ) {
            Timestamp endTime = new Timestamp( endDate.getTime() );
            entity.setEndDate( endTime );
        }

        fileUploadDao.save( entity );

        LOG.info( "Method createEntryInFileUploadForSurveyDataReport() finished" );
    }
}

