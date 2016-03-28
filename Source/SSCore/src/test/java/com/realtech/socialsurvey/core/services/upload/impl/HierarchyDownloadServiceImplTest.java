package com.realtech.socialsurvey.core.services.upload.impl;

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

import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.HierarchyUploadDao;
import com.realtech.socialsurvey.core.dao.RegionDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.entities.BranchUploadVO;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.RegionUploadVO;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;


public class HierarchyDownloadServiceImplTest
{
    @Spy
    @InjectMocks
    private HierarchyDownloadServiceImpl hierarchyDownloadServiceImpl;

    @Mock
    private HierarchyUploadDao hierarchyUploadDao;

    @Mock
    private OrganizationManagementService organizationManagementService;

    @Mock
    private RegionDao regionDao;

    @Mock
    private BranchDao branchDao;

    @Mock
    private CompanyDao companyDao;

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


    @Test ( expected = InvalidInputException.class)
    public void aggregateHierarchyStructureTestInvalidOldHierarchy() throws InvalidInputException
    {
        hierarchyDownloadServiceImpl.aggregateHierarchyStructure( null, new HierarchyUpload() );
    }


    @Test ( expected = InvalidInputException.class)
    public void aggregateHierarchyStructureTestInvalidCurrentHierarchy() throws InvalidInputException
    {
        hierarchyDownloadServiceImpl.aggregateHierarchyStructure( new HierarchyUpload(), null );
    }


    @SuppressWarnings ( "unchecked")
    @Test ( expected = InvalidInputException.class)
    public void generateUserUploadVOForUserTestUserNull() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( organizationManagementService.getAgentSettings( Mockito.anyLong() ) ).thenThrow(
            NoRecordsFetchedException.class );
        hierarchyDownloadServiceImpl.generateUserUploadVOForUser( null, new HashMap<Long, RegionUploadVO>(),
            new HashMap<Long, BranchUploadVO>(), new HashMap<Long, String>(), new HashMap<String, Long>(),
            new HashMap<Long, List<UserProfile>>() );
    }


    @SuppressWarnings ( "unchecked")
    @Test ( expected = InvalidInputException.class)
    public void generateUserUploadVOForUserTestNoAgentSettings() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( organizationManagementService.getAgentSettings( Mockito.anyLong() ) ).thenThrow(
            NoRecordsFetchedException.class );
        hierarchyDownloadServiceImpl.generateUserUploadVOForUser( new User(), new HashMap<Long, RegionUploadVO>(),
            new HashMap<Long, BranchUploadVO>(), new HashMap<Long, String>(), new HashMap<String, Long>(),
            new HashMap<Long, List<UserProfile>>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void getRegionUploadVOForRegionTestRegionNull() throws InvalidInputException
    {
        hierarchyDownloadServiceImpl
            .getRegionUploadVOForRegion( null, new HashMap<Long, String>(), new HashMap<String, Long>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void getRegionUploadVOForRegionTestRegionSettingsNull() throws InvalidInputException
    {
        Mockito.when( organizationManagementService.getRegionSettings( Mockito.anyLong() ) ).thenReturn( null );
        hierarchyDownloadServiceImpl
            .getRegionUploadVOForRegion( null, new HashMap<Long, String>(), new HashMap<String, Long>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void generateBranchUploadVOForBranchTestBranchNull() throws InvalidInputException
    {
        hierarchyDownloadServiceImpl.generateBranchUploadVOForBranch( null, new HashMap<Long, String>(),
            new HashMap<String, Long>(), new HashMap<Long, RegionUploadVO>() );
    }
}
