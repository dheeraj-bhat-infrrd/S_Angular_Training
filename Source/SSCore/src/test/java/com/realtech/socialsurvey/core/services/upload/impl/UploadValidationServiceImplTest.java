package com.realtech.socialsurvey.core.services.upload.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.entities.BranchUploadVO;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.RegionUploadVO;
import com.realtech.socialsurvey.core.entities.UploadValidation;
import com.realtech.socialsurvey.core.entities.UserUploadVO;


public class UploadValidationServiceImplTest
{
    private static Logger LOG = LoggerFactory.getLogger( UploadValidationServiceImplTest.class );

    @Spy
    @InjectMocks
    private UploadValidationServiceImpl uploadValidationServiceImpl;


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


    @Test
    public void testRegionValidationsWithNoErrors()
    {
        UploadValidation validation = new UploadValidation();
        HierarchyUpload upload = new HierarchyUpload();
        List<RegionUploadVO> regions = new ArrayList<RegionUploadVO>();
        regions.add( getRegion( "ABC", "abcdefh", "12 sdvdv", "12 sdvdv", "Bangalore", "India", "KA", "123456", 1 ) );
        upload.setRegions( regions );
        validation.setUpload( upload );
        uploadValidationServiceImpl.validateRegions( validation );
        Assert.assertEquals( 0, validation.getRegionValidationErrors().size() );
        LOG.info( "" + validation.getRegionValidationErrors() );
    }


    @Test
    public void testRegionValidationsWithErrors()
    {
        UploadValidation validation = new UploadValidation();
        HierarchyUpload upload = new HierarchyUpload();
        List<RegionUploadVO> regions = new ArrayList<RegionUploadVO>();
        regions.add( getRegion( null, null, null, null, null, null, null, null, 1 ) );
        regions.add( getRegion( "ABC", null, "12 sdvdv", "12 sdvdv", "Bangalore", "India", "KA", "123456", 2 ) );
        regions.add( getRegion( null, "acc", "12 sdvdv", "12 sdvdv", "Bangalore", "India", "KA", "123456", 3 ) );
        upload.setRegions( regions );
        validation.setUpload( upload );
        uploadValidationServiceImpl.validateRegions( validation );
        Assert.assertEquals( 4, validation.getRegionValidationErrors().size() );
        LOG.info( "" + validation.getRegionValidationErrors() );
    }


    @Test
    public void testBranchValidationsWithNoErrors()
    {
        UploadValidation validation = new UploadValidation();
        HierarchyUpload upload = new HierarchyUpload();
        List<BranchUploadVO> branches = new ArrayList<BranchUploadVO>();
        branches.add( getBranch( "ABC", "abcdefh", "ABC", true, "Bangalore", "KA", "123456", 1 ) );
        upload.setBranches( branches );
        List<RegionUploadVO> regions = new ArrayList<RegionUploadVO>();
        regions.add( getRegion( "ABC", "abcdefh", "12 sdvdv", "12 sdvdv", "Bangalore", "India", "KA", "123456", 1 ) );
        upload.setRegions( regions );
        validation.setUpload( upload );
        uploadValidationServiceImpl.validateBranches( validation );
        LOG.info( "Errors: " + validation.getBranchValidationErrors() );
        LOG.info( "Warnings: " + validation.getBranchValidationWarnings() );
        Assert.assertEquals( 0, validation.getBranchValidationErrors().size() );
        Assert.assertEquals( 0, validation.getBranchValidationWarnings().size() );
    }


    @Test
    public void testBranchValidationsWithErrors()
    {
        UploadValidation validation = new UploadValidation();
        HierarchyUpload upload = new HierarchyUpload();
        List<BranchUploadVO> branches = new ArrayList<BranchUploadVO>();
        branches.add( getBranch( null, null, null, false, null, null, null, 1 ) );
        branches.add( getBranch( "ABC", "ABC", "ABC", false, "ABC", "KA", "123456", 2 ) );
        branches.add( getBranch( "ABC", "ABC", "sdf", true, "ABC", "KA", "123456", 3 ) );
        upload.setBranches( branches );
        List<RegionUploadVO> regions = new ArrayList<RegionUploadVO>();
        regions.add( getRegion( "ABC", "abcdefh", "12 sdvdv", "12 sdvdv", "Bangalore", "India", "KA", "123456", 1 ) );
        upload.setRegions( regions );
        validation.setUpload( upload );
        uploadValidationServiceImpl.validateBranches( validation );
        LOG.info( "Errors: " + validation.getBranchValidationErrors() );
        LOG.info( "Warnings: " + validation.getBranchValidationWarnings() );
        Assert.assertEquals( 6, validation.getBranchValidationErrors().size() );
        Assert.assertEquals( 1, validation.getBranchValidationWarnings().size() );
    }


    @Test
    public void testUserValidationsWithNoErrors()
    {
        UploadValidation validation = new UploadValidation();
        HierarchyUpload upload = new HierarchyUpload();
        List<BranchUploadVO> branches = new ArrayList<BranchUploadVO>();
        branches.add( getBranch( "ABC", "abcdefh", "ABC", true, "Bangalore", "KA", "123456", 1 ) );
        branches.add( getBranch( "DEF", "abcdefh", "ABC", true, "Bangalore", "KA", "123456", 1 ) );
        upload.setBranches( branches );
        List<RegionUploadVO> regions = new ArrayList<RegionUploadVO>();
        regions.add( getRegion( "ABC", "abcdefh", "12 sdvdv", "12 sdvdv", "Bangalore", "India", "KA", "123456", 1 ) );
        regions.add( getRegion( "DEF", "abcdefh", "12 sdvdv", "12 sdvdv", "Bangalore", "India", "KA", "123456", 1 ) );
        upload.setRegions( regions );
        List<UserUploadVO> users = new ArrayList<UserUploadVO>();
        List<String> branchAdmins = new ArrayList<String>();
        branchAdmins.add( "ABC" );
        branchAdmins.add( "DEF" );
        List<String> regionAdmins = new ArrayList<String>();
        regionAdmins.add( "ABC" );
        regionAdmins.add( "DEF" );
        users.add( getUser( "XYZ", "cdcdvd", "asncj@dmck.com", "ABC", "ABC", branchAdmins, regionAdmins, 1 ) );
        upload.setUsers( users );
        validation.setUpload( upload );
        uploadValidationServiceImpl.validateUsers( validation );
        LOG.info( "Errors: " + validation.getUserValidationErrors() );
        LOG.info( "Warnings: " + validation.getUserValidationWarnings() );
        Assert.assertEquals( 0, validation.getUserValidationErrors().size() );
        Assert.assertEquals( 0, validation.getUserValidationWarnings().size() );
    }


    @Test
    public void testUserValidationsWithErrors()
    {
        UploadValidation validation = new UploadValidation();
        HierarchyUpload upload = new HierarchyUpload();
        List<BranchUploadVO> branches = new ArrayList<BranchUploadVO>();
        branches.add( getBranch( "ABC", "abcdefh", "ABC", true, "Bangalore", "KA", "123456", 1 ) );
        branches.add( getBranch( "DEF", "abcdefh", "ABC", true, "Bangalore", "KA", "123456", 1 ) );
        upload.setBranches( branches );
        List<RegionUploadVO> regions = new ArrayList<RegionUploadVO>();
        regions.add( getRegion( "ABC", "abcdefh", "12 sdvdv", "12 sdvdv", "Bangalore", "India", "KA", "123456", 1 ) );
        regions.add( getRegion( "DEF", "abcdefh", "12 sdvdv", "12 sdvdv", "Bangalore", "India", "KA", "123456", 1 ) );
        upload.setRegions( regions );
        List<UserUploadVO> users = new ArrayList<UserUploadVO>();
        List<String> branchAdmins = new ArrayList<String>();
        branchAdmins.add( "ABC" );
        branchAdmins.add( "dcd" );
        List<String> regionAdmins = new ArrayList<String>();
        regionAdmins.add( "ABC" );
        regionAdmins.add( "dcds" );
        users.add( getUser( null, null, null, null, null, null, null, 1 ) );
        users.add( getUser( "XYZ", "cdcdvd", "asncj@dmck.com", "ABC", "ABC", branchAdmins, regionAdmins, 2 ) );
        users.add( getUser( "XYZ", "cdcdvd", "asncj@dmck.com", "xdc", "cdf", branchAdmins, regionAdmins, 3 ) );
        upload.setUsers( users );
        validation.setUpload( upload );
        uploadValidationServiceImpl.validateUsers( validation );
        LOG.info( "Errors: " + validation.getUserValidationErrors() );
        LOG.info( "Warnings: " + validation.getUserValidationWarnings() );
        Assert.assertEquals( 9, validation.getUserValidationErrors().size() );
        Assert.assertEquals( 4, validation.getUserValidationWarnings().size() );
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
