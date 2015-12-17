package com.realtech.socialsurvey.core.dao.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
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
    public void testGetIdsUnderBranchConnectedToZillowWithInvalidBranchId() throws InvalidInputException
    {
        zillowHierarchyDaoImpl.getIdsUnderBranchConnectedToZillow( 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetZillowReviewCountAndTotalScoreForAllUnderBranchWithInvalidBranchId() throws InvalidInputException
    {
        zillowHierarchyDaoImpl.getZillowReviewCountAndTotalScoreForAllUnderBranch( 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetIdsUnderRegionConnectedToZillowWithInvalidRegionId() throws InvalidInputException
    {
        zillowHierarchyDaoImpl.getIdsUnderRegionConnectedToZillow( 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetZillowReviewCountAndTotalScoreForAllUnderRegionWithInvalidRegionId() throws InvalidInputException
    {
        zillowHierarchyDaoImpl.getZillowReviewCountAndTotalScoreForAllUnderRegion( 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetIdsUnderCompanyConnectedToZillowWithInvalidCompanyId() throws InvalidInputException
    {
        zillowHierarchyDaoImpl.getIdsUnderCompanyConnectedToZillow( 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetZillowReviewCountAndTotalScoreForAllUnderCompanyWithInvalidBranchId() throws InvalidInputException
    {
        zillowHierarchyDaoImpl.getZillowReviewCountAndTotalScoreForAllUnderCompany( 0 );
    }
}
