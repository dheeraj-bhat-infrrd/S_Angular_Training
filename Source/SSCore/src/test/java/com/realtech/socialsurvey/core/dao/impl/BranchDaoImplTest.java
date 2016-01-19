package com.realtech.socialsurvey.core.dao.impl;

import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public class BranchDaoImplTest
{
    @InjectMocks
    private BranchDaoImpl branchDaoImpl;


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
    public void testGetBranchForBranchIdsWithNullRegionIds() throws InvalidInputException{
        branchDaoImpl.getBranchForBranchIds( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchForBranchIdsWithEmptyRegionIds() throws InvalidInputException{
        branchDaoImpl.getBranchForBranchIds( new HashSet<Long>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchesForCompanyWithInvalidCompanyId() throws InvalidInputException{
        branchDaoImpl.getBranchesForCompany( 0, 0, 0, 50 );
    }
}
