package com.realtech.socialsurvey.core.services.search.impl;

import java.util.HashSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

public class SolrSearchServiceImplTest
{
    @InjectMocks
    private SolrSearchServiceImpl solrSearchServiceImpl;


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
    
    @Test(expected = InvalidInputException.class)
    public void testSearchRegionsForNullRegionPattern() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchRegions( null , new Company(), new HashSet<Long>(), 0, 5 );
    }
    
    @Test(expected = InvalidInputException.class)
    public void testSearchRegionsForNullCompany() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchRegions( "test", null, new HashSet<Long>(), 0, 5 );
    }
    

}
