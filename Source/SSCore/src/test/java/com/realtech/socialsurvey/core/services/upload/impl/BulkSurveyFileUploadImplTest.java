package com.realtech.socialsurvey.core.services.upload.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.entities.SurveyUploadVO;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;


public class BulkSurveyFileUploadImplTest
{
    @InjectMocks
    private BulkSurveyFileUploadImpl bulkSurveyFileUploadImpl;

    @Mock
    private GenericDao<FileUpload, Long> fileUploadDao;

    @Mock
    private UserManagementService userManagementService;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
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


    //Tests for checkUploadObject
    @Test
    public void checkUploadObjectTestAgentMailIdNull()
    {
        SurveyUploadVO surveyUpload = new SurveyUploadVO();
        surveyUpload.setAgentEmailId( null );
        assertFalse( bulkSurveyFileUploadImpl.checkUploadObject( surveyUpload ) );
    }


    @Test
    public void checkUploadObjectTestAgentMailIdEmpty()
    {
        SurveyUploadVO surveyUpload = new SurveyUploadVO();
        surveyUpload.setAgentEmailId( "" );
        assertFalse( bulkSurveyFileUploadImpl.checkUploadObject( surveyUpload ) );
    }


    @Test
    public void checkUploadObjectTestCustomerFirstNameNull()
    {
        SurveyUploadVO surveyUpload = new SurveyUploadVO();
        surveyUpload.setAgentEmailId( TestConstants.TEST_MAIL_ID_STRING );
        surveyUpload.setCustomerFirstName( null );
        assertFalse( bulkSurveyFileUploadImpl.checkUploadObject( surveyUpload ) );
    }


    @Test
    public void checkUploadObjectTestCustomerFirstNameEmpty()
    {
        SurveyUploadVO surveyUpload = new SurveyUploadVO();
        surveyUpload.setAgentEmailId( TestConstants.TEST_MAIL_ID_STRING );
        surveyUpload.setCustomerFirstName( "" );
        assertFalse( bulkSurveyFileUploadImpl.checkUploadObject( surveyUpload ) );
    }


    @Test
    public void checkUploadObjectTestCustomerMailIdNull()
    {
        SurveyUploadVO surveyUpload = new SurveyUploadVO();
        surveyUpload.setAgentEmailId( TestConstants.TEST_MAIL_ID_STRING );
        surveyUpload.setCustomerFirstName( "test" );
        surveyUpload.setCustomerEmailId( null );
        assertFalse( bulkSurveyFileUploadImpl.checkUploadObject( surveyUpload ) );
    }


    @Test
    public void checkUploadObjectTestCustomerMailIdEmpty()
    {
        SurveyUploadVO surveyUpload = new SurveyUploadVO();
        surveyUpload.setAgentEmailId( TestConstants.TEST_MAIL_ID_STRING );
        surveyUpload.setCustomerFirstName( "test" );
        surveyUpload.setCustomerEmailId( "" );
        assertFalse( bulkSurveyFileUploadImpl.checkUploadObject( surveyUpload ) );
    }


    @Test
    public void checkUploadObjectTestCustomerMailIdSameAsAgentMailId()
    {
        SurveyUploadVO surveyUpload = new SurveyUploadVO();
        surveyUpload.setAgentEmailId( TestConstants.TEST_MAIL_ID_STRING );
        surveyUpload.setCustomerFirstName( "test" );
        surveyUpload.setCustomerEmailId( TestConstants.TEST_MAIL_ID_STRING );
        assertFalse( bulkSurveyFileUploadImpl.checkUploadObject( surveyUpload ) );
    }


    @Test
    public void checkUploadObjectTestUploadObjectValid()
    {
        SurveyUploadVO surveyUpload = new SurveyUploadVO();
        surveyUpload.setAgentEmailId( TestConstants.TEST_MAIL_ID_STRING );
        surveyUpload.setCustomerFirstName( "test" );
        surveyUpload.setCustomerEmailId( "test1@raremile.com" );
        assertTrue( bulkSurveyFileUploadImpl.checkUploadObject( surveyUpload ) );
    }
}
