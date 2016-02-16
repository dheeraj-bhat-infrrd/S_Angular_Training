package com.realtech.socialsurvey.core.services.upload.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.entities.BranchUploadVO;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.RegionUploadVO;
import com.realtech.socialsurvey.core.entities.UploadValidation;
import com.realtech.socialsurvey.core.entities.UserUploadVO;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.upload.HierarchyDownloadService;
import com.realtech.socialsurvey.core.services.upload.UploadValidationService;


public class HierarchyUploadServiceImplTest
{

    static Logger LOG = LoggerFactory.getLogger( HierarchyUploadServiceImplTest.class );

    @Spy
    @InjectMocks
    private HierarchyUploadServiceImpl hierarchyUploadServiceImpl;

    @Mock
    private Utils utils;

    @Mock
    private UploadValidationService uploadValidationService;

    @Mock
    private HierarchyDownloadService hierarchyDownloadService;


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
    public void testValidateUserUploadFileIfCompanyInvalidFileNameInvalid() throws InvalidInputException
    {
        hierarchyUploadServiceImpl.validateUserUploadFile( null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testValidateUserUploadFileIfCompanyInvalidFileNameValid() throws InvalidInputException
    {
        hierarchyUploadServiceImpl.validateUserUploadFile( null, "src/test/resources/testCSV.xlsx" );
    }


    @Test
    public void testValidateUserUploadFileIfCompanyValidFileNameValid() throws InvalidInputException
    {
        Company company = new Company();
        company.setCompany( "Raremile" );
        File file = new File( "." );
        Mockito.when( hierarchyDownloadService.fetchUpdatedHierarchyStructure( company ) ).thenReturn( getHierarchyUpload() );
        UploadValidation validationObj = hierarchyUploadServiceImpl.validateUserUploadFile( company,
            "file:///" + file.getAbsolutePath() + "/src/test/resources/testCSV.xlsx" );
        Assert.assertNotNull( validationObj );
        Assert.assertNotNull( validationObj.getUpload() );
        Assert.assertEquals( 11, validationObj.getUpload().getRegions().size() );
        Assert.assertEquals( 78, validationObj.getUpload().getBranches().size() );
        Assert.assertEquals( 50, validationObj.getUpload().getUsers().size() );
    }


    @Test
    public void testValidateUserUploadFileForSnapshotData() throws InvalidInputException
    {
        Company company = new Company();
        company.setCompany( "Raremile" );
        Mockito.when( hierarchyDownloadService.fetchUpdatedHierarchyStructure( company ) ).thenReturn( getHierarchyUpload() );
        File file = new File( "." );
        UploadValidation validationObj = hierarchyUploadServiceImpl.validateUserUploadFile( company,
            "file:///" + file.getAbsolutePath() + "/src/test/resources/testCSV.xlsx" );
        Assert.assertNotNull( validationObj );
        Assert.assertEquals( 76, validationObj.getNumberOfBranchesAdded() );
        Assert.assertEquals( 0, validationObj.getNumberOfBranchesDeleted() );
        Assert.assertEquals( 1, validationObj.getNumberOfBranchesModified() );
        Assert.assertEquals( 9, validationObj.getNumberOfRegionsAdded() );
        Assert.assertEquals( 0, validationObj.getNumberOfRegionsDeleted() );
        Assert.assertEquals( 1, validationObj.getNumberOfRegionsModified() );
        Assert.assertEquals( 48, validationObj.getNumberOfUsersAdded() );
        Assert.assertEquals( 0, validationObj.getNumberOfUsersDeleted() );
        Assert.assertEquals( 1, validationObj.getNumberOfUsersModified() );
    }
    
    @Test
    public void testValidateUserUploadFileForDeletedUsers() throws InvalidInputException
    {
        Company company = new Company();
        company.setCompany( "Raremile" );
        Mockito.when( hierarchyDownloadService.fetchUpdatedHierarchyStructure( company ) ).thenReturn( getHierarchyUpload_deletedUser() );
        File file = new File( "." );
        UploadValidation validationObj = hierarchyUploadServiceImpl.validateUserUploadFile( company,
            "file:///" + file.getAbsolutePath() + "/src/test/resources/testCSV.xlsx" );
        Assert.assertNotNull( validationObj );
        Assert.assertEquals( 1, validationObj.getNumberOfUsersDeleted() );
    }

    private HierarchyUpload getHierarchyUpload_deletedUser()
    {
        HierarchyUpload upload = new HierarchyUpload();
        List<BranchUploadVO> branches = new ArrayList<BranchUploadVO>();
        branches.add( getBranch( "ABC", "abcdefh", "ABC", true, "Bangalore", "KA", "123456", 1 ) );
        branches.add( getBranch( "SCT", "abcdefh", "ABC", true, "Bangalore", "KA", "123456", 1 ) );
        upload.setBranches( branches );
        List<RegionUploadVO> regions = new ArrayList<RegionUploadVO>();
        regions.add( getRegion( "ABC", "abcdefh", "12 sdvdv", "12 sdvdv", "Bangalore", "India", "KA", "123456", 1 ) );
        regions.add( getRegion( "Lee", "abcdefh", "12 sdvdv", "12 sdvdv", "Bangalore", "India", "KA", "123456", 1 ) );
        upload.setRegions( regions );
        List<UserUploadVO> users = new ArrayList<UserUploadVO>();
        List<String> branchAdmins = new ArrayList<String>();
        branchAdmins.add( "ABC" );
        branchAdmins.add( "SCT" );
        List<String> regionAdmins = new ArrayList<String>();
        regionAdmins.add( "ABC" );
        regionAdmins.add( "Lee" );
        users.add( getUser( "XYZ", "cdcdvd", "asncj@dmck.com", "ABC", "ABC", branchAdmins, regionAdmins, 1 ) );
        users.add( getUser( "mmckinley", "cdcdvd", "asncj@dmck.com", "ABC", "ABC", branchAdmins, regionAdmins, 1 ) );
        upload.setUsers( users );
        return upload;
    }
    
    private HierarchyUpload getHierarchyUpload()
    {
        HierarchyUpload upload = new HierarchyUpload();
        List<BranchUploadVO> branches = new ArrayList<BranchUploadVO>();
        branches.add( getBranch( "ABC", "abcdefh", "ABC", true, "Bangalore", "KA", "123456", 1 ) );
        branches.add( getBranch( "SCT", "abcdefh", "ABC", true, "Bangalore", "KA", "123456", 1 ) );
        upload.setBranches( branches );
        List<RegionUploadVO> regions = new ArrayList<RegionUploadVO>();
        regions.add( getRegion( "ABC", "abcdefh", "12 sdvdv", "12 sdvdv", "Bangalore", "India", "KA", "123456", 1 ) );
        regions.add( getRegion( "Lee", "abcdefh", "12 sdvdv", "12 sdvdv", "Bangalore", "India", "KA", "123456", 1 ) );
        upload.setRegions( regions );
        List<UserUploadVO> users = new ArrayList<UserUploadVO>();
        List<String> branchAdmins = new ArrayList<String>();
        branchAdmins.add( "ABC" );
        branchAdmins.add( "SCT" );
        List<String> regionAdmins = new ArrayList<String>();
        regionAdmins.add( "ABC" );
        regionAdmins.add( "Lee" );
        users.add( getUser( "XYZ", "cdcdvd", "asncj@dmck.com", "ABC", "ABC", branchAdmins, regionAdmins, 1 ) );
        users.add( getUser( "sbrennan", "cdcdvd", "asncj@dmck.com", "ABC", "ABC", branchAdmins, regionAdmins, 1 ) );
        upload.setUsers( users );
        return upload;
    }


    private UserUploadVO getUser( String userId, String name, String email, String regionId, String branchId,
        List<String> regionAdmin, List<String> branchAdmin, int rowNum )
    {
        UserUploadVO user = new UserUploadVO();
        user.setSourceUserId( userId );
        user.setFirstName( name );
        user.setEmailId( email );
        user.setSourceRegionId( regionId );
        user.setSourceBranchId( branchId );
        user.setAssignedBranchesAdmin( branchAdmin );
        user.setAssignedRegionsAdmin( regionAdmin );
        user.setRowNum( rowNum );
        return user;
    }


    private BranchUploadVO getBranch( String branchId, String name, String regionId, boolean isAddressSet, String city,
        String state, String zip, int rowNum )
    {
        BranchUploadVO branch = new BranchUploadVO();
        branch.setSourceBranchId( branchId );
        branch.setBranchName( name );
        branch.setAddressSet( isAddressSet );
        branch.setBranchCity( city );
        branch.setBranchState( state );
        branch.setBranchZipcode( zip );
        branch.setRowNum( rowNum );
        branch.setSourceRegionId( regionId );
        return branch;
    }


    private RegionUploadVO getRegion( String regionId, String name, String address1, String address2, String city,
        String country, String state, String zip, int rowNum )
    {
        RegionUploadVO region = new RegionUploadVO();
        region.setSourceRegionId( regionId );
        region.setRegionName( name );
        region.setRegionAddress1( address1 );
        region.setRegionAddress2( address2 );
        region.setRegionCity( city );
        region.setRegionCountry( country );
        region.setRegionState( state );
        region.setRegionZipcode( zip );
        region.setRowNum( rowNum );
        return region;
    }

}
