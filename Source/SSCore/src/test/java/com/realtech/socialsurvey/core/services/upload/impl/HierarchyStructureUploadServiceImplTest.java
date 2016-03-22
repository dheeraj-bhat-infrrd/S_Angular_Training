package com.realtech.socialsurvey.core.services.upload.impl;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.realtech.socialsurvey.core.entities.BranchUploadVO;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.RegionUploadVO;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserUploadVO;
import com.realtech.socialsurvey.core.exception.BranchAdditionException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.UserAdditionException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserAssignmentException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


public class HierarchyStructureUploadServiceImplTest
{
    @Spy
    @InjectMocks
    private HierarchyStructureUploadServiceImpl hierarchyStructureUpload;

    @Mock
    private OrganizationManagementService organizationManagementService;

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


    @Test ( expected = InvalidInputException.class)
    public void uploadHierarchyWithNullUploadStructure() throws InvalidInputException
    {
        hierarchyStructureUpload.uploadHierarchy( null, new Company(), new User(), false );
    }


    @Test ( expected = InvalidInputException.class)
    public void uploadHierarchyWithNullCompany() throws InvalidInputException
    {
        hierarchyStructureUpload.uploadHierarchy( new HierarchyUpload(), null, new User(), false );
    }


    @Test ( expected = InvalidInputException.class)
    public void uploadHierarchyWithNullUser() throws InvalidInputException
    {
        hierarchyStructureUpload.uploadHierarchy( new HierarchyUpload(), new Company(), null, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void uploadHierarchyWithNonAdminUser() throws InvalidInputException
    {
        User user = new User();
        hierarchyStructureUpload.uploadHierarchy( new HierarchyUpload(), new Company(), user, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void modifyBranchWithNullAdminUser() throws InvalidInputException, SolrException, NoRecordsFetchedException,
        UserAssignmentException
    {
        hierarchyStructureUpload.modifyBranch( null, new BranchUploadVO() );
    }


    @Test ( expected = InvalidInputException.class)
    public void modifyBranchWithNullBranch() throws InvalidInputException, SolrException, NoRecordsFetchedException,
        UserAssignmentException
    {
        hierarchyStructureUpload.modifyBranch( new User(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void modifyBranchWithNullNewBranch() throws InvalidInputException, SolrException, NoRecordsFetchedException,
        UserAssignmentException
    {
        BranchUploadVO branch = new BranchUploadVO();
        branch.setBranchCountry( "testBranchCountry" );
        branch.setBranchCountryCode( "TBC" );
        Map<String, Object> map = new HashMap<String, Object>();
        Mockito.when(
            organizationManagementService.updateBranch( Mockito.any( User.class ), Mockito.anyLong(), Mockito.anyLong(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
                Mockito.any( String[].class ), Mockito.anyBoolean(), Mockito.anyBoolean() ) ).thenReturn( map );
        hierarchyStructureUpload.modifyBranch( new User(), branch );
    }


    @Test ( expected = InvalidInputException.class)
    public void createBranchWithNullAdminUser() throws InvalidInputException, BranchAdditionException, SolrException
    {
        hierarchyStructureUpload.createBranch( null, new BranchUploadVO(), new HierarchyUpload() );
    }


    @Test ( expected = InvalidInputException.class)
    public void createBranchWithNullBranch() throws InvalidInputException, BranchAdditionException, SolrException
    {
        hierarchyStructureUpload.createBranch( new User(), null, new HierarchyUpload() );
    }


    @Test ( expected = InvalidInputException.class)
    public void modifyRegionWithNullAdminUser() throws InvalidInputException, SolrException, NoRecordsFetchedException,
        UserAssignmentException
    {
        hierarchyStructureUpload.modifyRegion( null, new RegionUploadVO() );
    }


    @Test ( expected = InvalidInputException.class)
    public void modifyRegionWithNullRegion() throws InvalidInputException, SolrException, NoRecordsFetchedException,
        UserAssignmentException
    {
        hierarchyStructureUpload.modifyRegion( new User(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void modifyRegionWithNullNewRegion() throws InvalidInputException, SolrException, NoRecordsFetchedException,
        UserAssignmentException
    {
        RegionUploadVO region = new RegionUploadVO();
        region.setRegionCountry( "testRegionCountry" );
        region.setRegionCountryCode( "TRC" );
        Map<String, Object> map = new HashMap<String, Object>();
        Mockito.when(
            organizationManagementService.updateRegion( Mockito.any( User.class ), Mockito.anyLong(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any( String[].class ),
                Mockito.anyBoolean(), Mockito.anyBoolean() ) ).thenReturn( map );
        hierarchyStructureUpload.modifyRegion( new User(), region );
    }


    @Test ( expected = InvalidInputException.class)
    public void createRegionWithNullAdminUser() throws InvalidInputException, SolrException
    {
        hierarchyStructureUpload.createRegion( null, new RegionUploadVO() );
    }


    @Test ( expected = InvalidInputException.class)
    public void createRegionWithNullRegion() throws InvalidInputException, SolrException
    {
        hierarchyStructureUpload.createRegion( new User(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void getCompanyWithNullCompany() throws InvalidInputException
    {
        hierarchyStructureUpload.getCompany( new User() );
    }


    @Test ( expected = InvalidInputException.class)
    public void getLicenseDetailWithLicenseDetailsNull() throws InvalidInputException
    {
        hierarchyStructureUpload.getLicenseDetail( new Company() );
    }


    @Test ( expected = UserAdditionException.class)
    public void modifyUserWhenUserAlreadyExists() throws UserAdditionException, InvalidInputException, SolrException,
        NoRecordsFetchedException, UserAssignmentException, UndeliveredEmailException
    {
        Mockito.doReturn( false ).when( hierarchyStructureUpload )
            .checkIfEmailIdExistsWithCompany( Matchers.anyString(), Matchers.any( Company.class ) );
        hierarchyStructureUpload.modifyUser( new UserUploadVO(), new User(), new HashMap<String, UserUploadVO>(),
            new HierarchyUpload() );
    }


    @Test ( expected = UserAdditionException.class)
    public void assignUserWhenUserAlreadyExists() throws UserAdditionException, InvalidInputException, SolrException,
        NoRecordsFetchedException, UserAssignmentException
    {
        Mockito.doReturn( false ).when( hierarchyStructureUpload )
            .checkIfEmailIdExistsWithCompany( Matchers.anyString(), Matchers.any( Company.class ) );
        hierarchyStructureUpload.assignUser( new UserUploadVO(), new User(), new HashMap<String, UserUploadVO>(),
            new HierarchyUpload() );
    }


    @Test ( expected = InvalidInputException.class)
    public void checkIfEmailIdExistsForEmailNull() throws InvalidInputException
    {
        Mockito.doReturn( null ).when( hierarchyStructureUpload ).extractEmailId( Matchers.anyString() );
        hierarchyStructureUpload.checkIfEmailIdExists( null, new Company() );
    }


    @Test ( expected = InvalidInputException.class)
    public void checkIfEmailIdExistsWithCompanyForEmailNull() throws InvalidInputException
    {
        Mockito.doReturn( null ).when( hierarchyStructureUpload ).extractEmailId( Matchers.anyString() );
        hierarchyStructureUpload.checkIfEmailIdExistsWithCompany( null, new Company() );
    }


    @Test ( expected = UserAdditionException.class)
    public void addUserWhenUserAlreadyExists() throws InvalidInputException, NoRecordsFetchedException, SolrException,
        UserAssignmentException, UserAdditionException
    {
        Mockito.doReturn( true ).when( hierarchyStructureUpload )
            .checkIfEmailIdExists( Matchers.anyString(), Matchers.any( Company.class ) );
        hierarchyStructureUpload.addUser( new UserUploadVO(), new User(), new HashMap<String, UserUploadVO>(),
            new HierarchyUpload() );
    }


    @Test ( expected = InvalidInputException.class)
    public void addUserForEmailNull() throws InvalidInputException, NoRecordsFetchedException, SolrException,
        UserAssignmentException, UserAdditionException
    {
        Mockito.doReturn( false ).when( hierarchyStructureUpload )
            .checkIfEmailIdExists( Matchers.anyString(), Matchers.any( Company.class ) );
        hierarchyStructureUpload.addUser( new UserUploadVO(), new User(), new HashMap<String, UserUploadVO>(),
            new HierarchyUpload() );
    }


    @Test ( expected = InvalidInputException.class)
    public void updateUserSettingsInMongoForAgentSettingsNull() throws InvalidInputException
    {
        Mockito.when( userManagementService.getAgentSettingsForUserProfiles( Mockito.anyLong() ) ).thenReturn( null );
        hierarchyStructureUpload.updateUserSettingsInMongo( new User(), new UserUploadVO() );
    }

}
