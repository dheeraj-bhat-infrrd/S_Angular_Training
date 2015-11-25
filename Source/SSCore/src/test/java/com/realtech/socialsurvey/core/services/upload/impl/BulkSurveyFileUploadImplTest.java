package com.realtech.socialsurvey.core.services.upload.impl;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;


public class BulkSurveyFileUploadImplTest
{
    @InjectMocks
    private BulkSurveyFileUploadImpl bulkSurveyFileUploadImpl;

    @Mock
    private GenericDao<FileUpload, Long> fileUploadDao;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
        //bulkSurveyFileUploadImpl = new BulkSurveyFileUploadImpl();
        MockitoAnnotations.initMocks( this );
    }


    @After
    public void tearDown() throws Exception
    {}


    //Tests for uploadBulkSurveyFile
    @Test ( expected = InvalidInputException.class)
    public void uploadBulkSurveyFileTestFileUploadNull() throws InvalidInputException, ProfileNotFoundException
    {
        bulkSurveyFileUploadImpl.uploadBulkSurveyFile( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void uploadBulkSurveyFileTestFileNameNull() throws InvalidInputException, ProfileNotFoundException
    {
        FileUpload fileUpload = new FileUpload();
        bulkSurveyFileUploadImpl.uploadBulkSurveyFile( fileUpload );
    }


    @Test ( expected = InvalidInputException.class)
    public void uploadBulkSurveyFileTestFileNameEmpty() throws InvalidInputException, ProfileNotFoundException
    {
        FileUpload fileUpload = new FileUpload();
        fileUpload.setFileName( "" );
        bulkSurveyFileUploadImpl.uploadBulkSurveyFile( fileUpload );
    }


    @Test ( expected = InvalidInputException.class)
    public void uploadBulkSurveyFileTestCompanyNull() throws InvalidInputException, ProfileNotFoundException
    {
        FileUpload fileUpload = new FileUpload();
        fileUpload.setFileName( "test" );
        bulkSurveyFileUploadImpl.uploadBulkSurveyFile( fileUpload );
    }


    //Tests for getSurveyUploadFiles
    @SuppressWarnings ( "unchecked")
    @Test ( expected = NoRecordsFetchedException.class)
    public void getSurveyUploadFilesTestFilesToBeUploadedNull() throws NoRecordsFetchedException
    {
        Mockito.when( fileUploadDao.findByKeyValue( Mockito.eq( FileUpload.class ), Mockito.anyMap() ) ).thenReturn( null );
        bulkSurveyFileUploadImpl.getSurveyUploadFiles();
    }


    @SuppressWarnings ( "unchecked")
    @Test ( expected = NoRecordsFetchedException.class)
    public void getSurveyUploadFilesTestFilesToBeUploadedEmpty() throws NoRecordsFetchedException
    {
        Mockito.when( fileUploadDao.findByKeyValue( Mockito.eq( FileUpload.class ), Mockito.anyMap() ) ).thenReturn(
            new ArrayList<FileUpload>() );
        bulkSurveyFileUploadImpl.getSurveyUploadFiles();
    }


    //Tests for updateFileUploadRecord
    @Test ( expected = InvalidInputException.class)
    public void updateFileUploadRecordTestFileuploadNull() throws InvalidInputException
    {
        bulkSurveyFileUploadImpl.updateFileUploadRecord( null );
    }
}
