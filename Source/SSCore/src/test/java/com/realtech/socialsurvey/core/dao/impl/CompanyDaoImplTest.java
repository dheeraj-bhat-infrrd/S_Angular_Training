package com.realtech.socialsurvey.core.dao.impl;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.realtech.socialsurvey.core.exception.InvalidInputException;

public class CompanyDaoImplTest
{
    @InjectMocks
    private CompanyDaoImpl companyDaoImpl;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    @AfterClass
    public static void tearDownAfterClass() throws Exception {}


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks( this );
    }


    @After
    public void tearDown() throws Exception {}


    @Test ( expected = InvalidInputException.class)
    public void testGetAllUsersAndAdminsUnderACompanyGroupedByBranchesWithInvalidCompanyId() throws InvalidInputException
    {
        companyDaoImpl.getAllUsersAndAdminsUnderACompanyGroupedByBranches( 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllActiveUsersAndAdminsUnderACompanyGroupedByBranchesWithInvalidCompanyId() throws InvalidInputException
    {
        companyDaoImpl.getAllActiveUsersAndAdminsUnderACompanyGroupedByBranches( 0 );
    }
}
