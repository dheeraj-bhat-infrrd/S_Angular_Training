package com.realtech.socialsurvey.core.services.upload.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.BranchAdditionException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.RegionAdditionException;
import com.realtech.socialsurvey.core.exception.UserAdditionException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserAssignmentException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


public class CsvUploadServiceImplTest
{
    @InjectMocks
    private CsvUploadServiceImpl csvUploadServiceImpl;

    @Mock
    private OrganizationManagementService organizationManagementService;

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
        MockitoAnnotations.initMocks( this );
    }


    @After
    public void tearDown() throws Exception
    {}


    //Tests for parseAndUploadTempCsv
    @Test ( expected = InvalidInputException.class)
    public void parseAndUploadTempCsvTestFileUploadNull() throws InvalidInputException, ProfileNotFoundException
    {
        csvUploadServiceImpl.parseAndUploadTempCsv( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void parseAndUploadTempCsvTestFileNameNull() throws InvalidInputException, ProfileNotFoundException
    {
        FileUpload fileUpload = new FileUpload();
        csvUploadServiceImpl.parseAndUploadTempCsv( fileUpload );
    }


    @Test ( expected = InvalidInputException.class)
    public void parseAndUploadTempCsvTestFileNameEmpty() throws InvalidInputException, ProfileNotFoundException
    {
        FileUpload fileUpload = new FileUpload();
        fileUpload.setFileName( "" );
        csvUploadServiceImpl.parseAndUploadTempCsv( fileUpload );
    }


    @Test ( expected = InvalidInputException.class)
    public void parseAndUploadTempCsvTestCompanyNull() throws InvalidInputException, ProfileNotFoundException
    {
        FileUpload fileUpload = new FileUpload();
        fileUpload.setFileName( "test" );
        csvUploadServiceImpl.parseAndUploadTempCsv( fileUpload );
    }


    @Test ( expected = InvalidInputException.class)
    public void parseAndUploadTempCsvTestAdminUserIdInvalid() throws InvalidInputException, ProfileNotFoundException
    {
        FileUpload fileUpload = new FileUpload();
        fileUpload.setFileName( "test" );
        fileUpload.setCompany( new Company() );
        fileUpload.setAdminUserId( -1l );
        csvUploadServiceImpl.parseAndUploadTempCsv( fileUpload );
    }


    //Tests for createUser
    @Test ( expected = InvalidInputException.class)
    public void createUserTestUserNull() throws InvalidInputException, UserAdditionException, NoRecordsFetchedException,
        SolrException, UserAssignmentException
    {
        csvUploadServiceImpl.createUser( null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void createUserTestUserUploadNull() throws InvalidInputException, UserAdditionException, NoRecordsFetchedException,
        SolrException, UserAssignmentException
    {
        csvUploadServiceImpl.createUser( new User(), null );
    }


    //Tests for createBranch
    @Test ( expected = InvalidInputException.class)
    public void createBranchTestUserNull() throws InvalidInputException, UserAdditionException, NoRecordsFetchedException,
        SolrException, UserAssignmentException, BranchAdditionException
    {
        csvUploadServiceImpl.createBranch( null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void createBranchTestUserUploadNull() throws InvalidInputException, UserAdditionException,
        NoRecordsFetchedException, SolrException, UserAssignmentException, BranchAdditionException
    {
        csvUploadServiceImpl.createBranch( new User(), null );
    }


    //Tests for createRegion
    @Test ( expected = InvalidInputException.class)
    public void createRegionTestUserNull() throws InvalidInputException, UserAdditionException, NoRecordsFetchedException,
        SolrException, UserAssignmentException, BranchAdditionException, RegionAdditionException
    {
        csvUploadServiceImpl.createRegion( null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void createRegionTestUserUploadNull() throws InvalidInputException, UserAdditionException,
        NoRecordsFetchedException, SolrException, UserAssignmentException, BranchAdditionException, RegionAdditionException
    {
        csvUploadServiceImpl.createRegion( new User(), null );
    }


    //Tests for createAndReturnErrors
    @Test ( expected = InvalidInputException.class)
    public void createAndReturnErrorsTestUploadObjectsNull() throws InvalidInputException, NoRecordsFetchedException,
        SolrException, UserAssignmentException
    {
        csvUploadServiceImpl.createAndReturnErrors( null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void createAndReturnErrorsTestUploadObjectsEmpty() throws InvalidInputException, NoRecordsFetchedException,
        SolrException, UserAssignmentException
    {
        csvUploadServiceImpl.createAndReturnErrors( new HashMap<String, List<Object>>(), null );
    }


    //Tests for getFilesToBeUploaded
    @SuppressWarnings ( "unchecked")
    @Test ( expected = NoRecordsFetchedException.class)
    public void postProcessTestFilesToBeUploadedNull() throws NoRecordsFetchedException
    {
        Mockito.when( fileUploadDao.findByKeyValue( Mockito.eq( FileUpload.class ), Mockito.anyMap() ) ).thenReturn( null );
        csvUploadServiceImpl.getFilesToBeUploaded();
    }


    @SuppressWarnings ( "unchecked")
    @Test ( expected = NoRecordsFetchedException.class)
    public void postProcessTestFilesToBeUploadedEmpty() throws NoRecordsFetchedException
    {
        Mockito.when( fileUploadDao.findByKeyValue( Mockito.eq( FileUpload.class ), Mockito.anyMap() ) ).thenReturn(
            new ArrayList<FileUpload>() );
        csvUploadServiceImpl.getFilesToBeUploaded();
    }


    //Tests for updateFileUploadRecord
    @Test ( expected = InvalidInputException.class)
    public void updateFileUploadRecord() throws InvalidInputException
    {
        csvUploadServiceImpl.updateFileUploadRecord( null );
    }
}
