package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.integration.EngagementProcessingStatus;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

public class UserManagementServiceImplTest
{

    @InjectMocks
    private UserManagementServiceImpl userManagementServiceImpl;
    
    @Mock
    private URLGenerator urlGenerator;
    
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
    public void testGetProfilesMasterByIdForInvalidProfileId() throws InvalidInputException
    {
        userManagementServiceImpl.getProfilesMasterById( 0 );
    }
    
    /*@Test
    public void testCheckIfTheLinkHasExpiredForNullUrlParameter() throws InvalidInputException
    {
        Mockito.when( urlGenerator.decryptParameters( Mockito.anyString() ) ).thenReturn( null );
        assertEquals( "Test", false, userManagementServiceImpl.checkIfTheLinkHasExpired( "test" ) );
    }
    */
    
    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileCompletionStageForNullUser() throws InvalidInputException
    {
        userManagementServiceImpl.updateProfileCompletionStage( null, 2, "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileCompletionStageForInvalidProfileMasterId() throws InvalidInputException
    {
        userManagementServiceImpl.updateProfileCompletionStage( new User(), 0 , "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileCompletionStageForNullProfileCompletionStage() throws InvalidInputException
    {
        userManagementServiceImpl.updateProfileCompletionStage( new User(), 1 , null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileCompletionStageForEmptyProfileCompletionStage() throws InvalidInputException
    {
        userManagementServiceImpl.updateProfileCompletionStage( new User(), 1 , "" );
    }
    
    @Test
    public void testVerifyAccountForNullUrlParams() throws InvalidInputException, SolrException
    {
        Mockito.when( urlGenerator.decryptParameters( Mockito.anyString() ) ).thenReturn( null );
        userManagementServiceImpl.verifyAccount( "test" );
    }
}
