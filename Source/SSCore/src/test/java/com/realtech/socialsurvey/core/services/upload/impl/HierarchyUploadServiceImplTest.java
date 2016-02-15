package com.realtech.socialsurvey.core.services.upload.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.upload.UploadValidationService;


public class HierarchyUploadServiceImplTest
{
    @Spy
    @InjectMocks
    private HierarchyUploadServiceImpl hierarchyUploadServiceImpl;

    @Mock
    private Utils utils;

    @Mock
    private UploadValidationService uploadValidationService;


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
    public void testValidateUserUploadFileIfCompanyInvalidFileNameInvalid()
        throws InvalidInputException, ProfileNotFoundException
    {
        hierarchyUploadServiceImpl.validateUserUploadFile( null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testValidateUserUploadFileIfCompanyInvalidFileNameValid() throws InvalidInputException, ProfileNotFoundException
    {
        hierarchyUploadServiceImpl.validateUserUploadFile( null,
            "file:///E:/raremile_projects/socialsurvey/fileupload/Summit_data_import(1).xlsx" );
    }


    @Test
    public void testValidateUserUploadFileIfCompanyValidFileNameValid() throws InvalidInputException, ProfileNotFoundException
    {
        Company company = new Company();
        company.setCompany( "Raremile" );
        hierarchyUploadServiceImpl.validateUserUploadFile( company,
            "file:///E:/raremile_projects/socialsurvey/fileupload/test.xlsx" );
    }
}
