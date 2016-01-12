package com.realtech.socialsurvey.core.dao.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.realtech.socialsurvey.core.exception.InvalidInputException;


public class UserProfileDaoImplTest
{
    @InjectMocks
    private UserProfileDaoImpl userProfileDaoImpl;


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
    public void testUpdateRegionIdForParticularBranchForInvalidRegionId() throws InvalidInputException
    {
        userProfileDaoImpl.updateRegionIdForBranch( 0l, 1l );
    }
    
    
    @Test ( expected = InvalidInputException.class)
    public void testUpdateRegionIdForParticularBranchForInvalidBranchId() throws InvalidInputException
    {
        userProfileDaoImpl.updateRegionIdForBranch( 1l, 0l );
    }
}
