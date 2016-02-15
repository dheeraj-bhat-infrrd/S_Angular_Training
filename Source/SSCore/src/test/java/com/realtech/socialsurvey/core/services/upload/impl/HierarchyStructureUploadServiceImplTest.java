package com.realtech.socialsurvey.core.services.upload.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;

public class HierarchyStructureUploadServiceImplTest
{
    @InjectMocks
    private HierarchyStructureUploadServiceImpl hierarchyStructureUpload;
    
    @Mock
    private OrganizationManagementService organizationManagementService;
    

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


    @Test(expected=InvalidInputException.class)
    public void uploadHierarchyWithNullUploadStructure() throws InvalidInputException{
        hierarchyStructureUpload.uploadHierarchy( null, new Company(), new User() );
    }
    
    @Test(expected=InvalidInputException.class)
    public void uploadHierarchyWithNullCompany() throws InvalidInputException{
        hierarchyStructureUpload.uploadHierarchy( new HierarchyUpload(), null, new User() );
    }
    
    @Test(expected=InvalidInputException.class)
    public void uploadHierarchyWithNullUser() throws InvalidInputException{
        hierarchyStructureUpload.uploadHierarchy( new HierarchyUpload(), new Company(), null );
    }
    
    @Test(expected=InvalidInputException.class)
    public void uploadHierarchyWithNonAdminUser() throws InvalidInputException{
        User user = new User();
        hierarchyStructureUpload.uploadHierarchy( new HierarchyUpload(), new Company(), user );
    }

}
