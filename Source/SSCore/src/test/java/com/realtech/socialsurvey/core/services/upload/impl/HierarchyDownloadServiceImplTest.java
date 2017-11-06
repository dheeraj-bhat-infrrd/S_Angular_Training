package com.realtech.socialsurvey.core.services.upload.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.HierarchyUploadDao;
import com.realtech.socialsurvey.core.dao.RegionDao;
import com.realtech.socialsurvey.core.dao.UserDao;
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

}
