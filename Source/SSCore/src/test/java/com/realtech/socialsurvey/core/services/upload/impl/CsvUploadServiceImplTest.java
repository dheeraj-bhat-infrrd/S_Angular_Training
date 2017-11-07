package com.realtech.socialsurvey.core.services.upload.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import org.mockito.Spy;

import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.entities.BranchUploadVO;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.RegionUploadVO;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserEmailMapping;
import com.realtech.socialsurvey.core.entities.UserUploadVO;
import com.realtech.socialsurvey.core.exception.BranchAdditionException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.RegionAdditionException;
import com.realtech.socialsurvey.core.exception.UserAdditionException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserAssignmentException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


public class CsvUploadServiceImplTest
{
    @Spy
    @InjectMocks
    private CsvUploadServiceImpl csvUploadServiceImpl;

    @Mock
    private OrganizationManagementService organizationManagementService;

    @Mock
    private UserManagementService userManagementService;
    @Mock
    private GenericDao<FileUpload, Long> fileUploadDao;

    @Mock
    private UserDao userDao;


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


    //Tests for createRegionUserAdditionException
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


    //Tests for getRegionIdFromSourceId
    @Test ( expected = BranchAdditionException.class)
    public void getRegionIdFromSourceIdTestUploadedRegionsEmpty() throws BranchAdditionException
    {
        csvUploadServiceImpl.getRegionIdFromSourceId( new ArrayList<RegionUploadVO>(), null );
    }


    //Tests for getBranchIdFromSourceId
    @Test ( expected = UserAdditionException.class)
    public void getBranchIdFromSourceIdTestUploadedBranchesEmpty() throws UserAdditionException
    {
        csvUploadServiceImpl.getBranchIdFromSourceId( new ArrayList<BranchUploadVO>(), null );
    }


    //Tests for checkIfEmailIdExists
    @Test
    public void checkIfEmailIdExistsTestUserExists() throws NoRecordsFetchedException, InvalidInputException
    {
        List<UserEmailMapping> userEmailMappings = new ArrayList<UserEmailMapping>();
        userEmailMappings.add( new UserEmailMapping() );
        User user = new User();
        Company company = new Company();
        user.setCompany( company );
        Mockito.when( userManagementService.getUserByEmailAddress( Mockito.anyString() ) ).thenReturn( user );
        assertTrue( csvUploadServiceImpl.checkIfEmailIdExists( TestConstants.TEST_MAIL_ID_STRING, company ) );
    }


    @SuppressWarnings ( "unchecked")
    @Test
    public void checkIfEmailIdExistsTestUserDoesNotExist() throws NoRecordsFetchedException, InvalidInputException
    {
        Mockito.when( userManagementService.getUserByEmailAddress( Mockito.anyString() ) ).thenThrow(
            NoRecordsFetchedException.class );
        assertFalse( csvUploadServiceImpl.checkIfEmailIdExists( TestConstants.TEST_MAIL_ID_STRING, null ) );
    }


    //Tests for getCompany
    @Test ( expected = InvalidInputException.class)
    public void getCompanyTestCompanyDoesNotExist() throws InvalidInputException
    {
        csvUploadServiceImpl.getCompany( new User() );
    }


    //Tests for getLicenseDetail
    @Test ( expected = InvalidInputException.class)
    public void getLicenseDetailTestDetailsNull() throws InvalidInputException
    {
        csvUploadServiceImpl.getLicenseDetail( new Company() );
    }


    @Test ( expected = InvalidInputException.class)
    public void getLicenseDetailTestDetailsEmpty() throws InvalidInputException
    {
        Company company = new Company();
        company.setLicenseDetails( new ArrayList<LicenseDetail>() );
        csvUploadServiceImpl.getLicenseDetail( company );
    }


    //Tests for addUser
    @Test ( expected = UserAdditionException.class)
    public void addUserTestInvalidRegionId() throws InvalidInputException, NoRecordsFetchedException, SolrException,
        UserAssignmentException, UserAdditionException
    {
        Mockito.doReturn( true ).when( csvUploadServiceImpl ).checkIfEmailIdExists( Mockito.anyString(), (Company) Mockito.any() );
        csvUploadServiceImpl.addUser( new UserUploadVO(), new User() );
    }


    //Tests for assignUser
    @Test ( expected = UserAdditionException.class)
    public void assignUserTestAsigneeUsersNull() throws UserAdditionException, InvalidInputException, SolrException,
        NoRecordsFetchedException, UserAssignmentException
    {
        Mockito.doReturn( false ).when( csvUploadServiceImpl ).checkIfEmailIdExistsWithCompany( Mockito.anyString(), (Company) Mockito.any() );
        csvUploadServiceImpl.assignUser( new UserUploadVO(), new User() );
    }


    @Test ( expected = UserAdditionException.class)
    public void assignUserTestAsigneeUsersEmpty() throws UserAdditionException, InvalidInputException, SolrException,
        NoRecordsFetchedException, UserAssignmentException
    {
        Mockito.doReturn( false ).when( csvUploadServiceImpl ).checkIfEmailIdExistsWithCompany( Mockito.anyString(), (Company) Mockito.any() );
        csvUploadServiceImpl.assignUser( new UserUploadVO(), new User() );
    }
}
