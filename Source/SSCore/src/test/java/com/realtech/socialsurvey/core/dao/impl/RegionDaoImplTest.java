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

public class RegionDaoImplTest
{
    @InjectMocks
    private RegionDaoImpl regionDaoImpl;


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
    public void testGetRegionForRegionIdsWithNullRegionIds() throws InvalidInputException{
        regionDaoImpl.getRegionForRegionIds( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetRegionForRegionIdsWithEmptyRegionIds() throws InvalidInputException{
        regionDaoImpl.getRegionForRegionIds( new HashSet<Long>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetRegionIdsForCompanyIdWithInvalidCompanyId() throws InvalidInputException{
        regionDaoImpl.getRegionIdsForCompanyId( 0 );
    }
}
