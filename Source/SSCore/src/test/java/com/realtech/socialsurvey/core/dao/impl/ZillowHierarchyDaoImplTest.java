package com.realtech.socialsurvey.core.dao.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public class ZillowHierarchyDaoImplTest
{
    @InjectMocks
    private ZillowHierarchyDaoImpl zillowHierarchyDaoImpl;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception {}


    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks( this );
    }


    @After
    public void tearDown() throws Exception {}


    @Test ( expected = InvalidInputException.class)
    public void testGetZillowReviewCountAndTotalScoreForAllUnderBranchWithInvalidBranchId() throws InvalidInputException
    {
        zillowHierarchyDaoImpl.getZillowReviewCountAndTotalScoreForAllUnderBranch( 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetZillowReviewCountAndTotalScoreForAllUnderRegionWithInvalidRegionId() throws InvalidInputException
    {
        zillowHierarchyDaoImpl.getZillowReviewCountAndTotalScoreForAllUnderRegion( 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetZillowReviewCountAndTotalScoreForAllUnderCompanyWithInvalidCompanyId() throws InvalidInputException
    {
        zillowHierarchyDaoImpl.getZillowReviewCountAndTotalScoreForAllUnderCompany( 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetRegionIdsUnderCompanyConnectedToZillowWithInvalidCompanyId() throws InvalidInputException
    {
        zillowHierarchyDaoImpl.getRegionIdsUnderCompanyConnectedToZillow( 0, TestConstants.TEST_INT, TestConstants.TEST_INT );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchIdsUnderCompanyConnectedToZillowWithInvalidCompanyId() throws InvalidInputException
    {
        zillowHierarchyDaoImpl.getBranchIdsUnderCompanyConnectedToZillow( 0, TestConstants.TEST_INT, TestConstants.TEST_INT );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchIdsUnderRegionConnectedToZillowWithInvalidRegionId() throws InvalidInputException
    {
        zillowHierarchyDaoImpl.getBranchIdsUnderRegionConnectedToZillow( 0, TestConstants.TEST_INT, TestConstants.TEST_INT );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserIdsUnderCompanyConnectedToZillowWithInvalidCompanyId() throws InvalidInputException
    {
        zillowHierarchyDaoImpl.getUserIdsUnderCompanyConnectedToZillow( 0, TestConstants.TEST_INT, TestConstants.TEST_INT );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserIdsUnderRegionConnectedToZillowWithInvalidRegionId() throws InvalidInputException
    {
        zillowHierarchyDaoImpl.getUserIdsUnderRegionConnectedToZillow( 0, TestConstants.TEST_INT, TestConstants.TEST_INT );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserIdsUnderBranchConnectedToZillowWithInvalidBranchId() throws InvalidInputException
    {
        zillowHierarchyDaoImpl.getUserIdsUnderBranchConnectedToZillow( 0, TestConstants.TEST_INT, TestConstants.TEST_INT );
    }
}
