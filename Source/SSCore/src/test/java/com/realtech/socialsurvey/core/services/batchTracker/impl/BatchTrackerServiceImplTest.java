package com.realtech.socialsurvey.core.services.batchTracker.impl;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.BatchTracker;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.impl.BatchTrackerServiceImpl;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;


public class BatchTrackerServiceImplTest
{
    @InjectMocks
    private BatchTrackerServiceImpl batchTrackerServiceImpl;

    @Mock
    private GenericDao<BatchTracker, Long> batchTrackerDao;


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
    public void testGetLastRunTimeByBatchTypeForNullBatchType() throws NoRecordsFetchedException, InvalidInputException
    {
        batchTrackerServiceImpl.getLastRunEndTimeByBatchType( null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetLastRunTimeByBatchTypeForEmptyBatchType() throws NoRecordsFetchedException, InvalidInputException
    {
        batchTrackerServiceImpl.getLastRunEndTimeByBatchType( "" );
    }

    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetLastRunTimeByBatchTypeWhenEmptyListFound() throws NoRecordsFetchedException, InvalidInputException
    {
        Mockito
            .when( batchTrackerDao.findByColumn( Mockito.eq( BatchTracker.class ), Mockito.anyString(), Mockito.anyObject() ) )
            .thenReturn( new ArrayList<BatchTracker>() );
        batchTrackerServiceImpl.getLastRunEndTimeByBatchType( "test" );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetLastRunTimeByBatchTypeWhenNoBatchTrackerFound() throws NoRecordsFetchedException, InvalidInputException
    {
        Mockito
            .when( batchTrackerDao.findByColumn( Mockito.eq( BatchTracker.class ), Mockito.anyString(), Mockito.anyObject() ) )
            .thenReturn( null );
        batchTrackerServiceImpl.getLastRunEndTimeByBatchType( "test" );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testUpdateLastRunEndTimeByBatchTypeWhenEmptyListFound() throws NoRecordsFetchedException,
        InvalidInputException
    {
        Mockito
            .when( batchTrackerDao.findByColumn( Mockito.eq( BatchTracker.class ), Mockito.anyString(), Mockito.anyObject() ) )
            .thenReturn( new ArrayList<BatchTracker>() );
        batchTrackerServiceImpl.updateLastRunEndTimeByBatchType( "test" );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testUpdateLastRunEndTimeByBatchTypeWhenNoBatchTrackerFound() throws NoRecordsFetchedException,
        InvalidInputException
    {
        Mockito
            .when( batchTrackerDao.findByColumn( Mockito.eq( BatchTracker.class ), Mockito.anyString(), Mockito.anyObject() ) )
            .thenReturn( null );
        batchTrackerServiceImpl.updateLastRunEndTimeByBatchType( "test" );
    }


    //Tests for updateModifiedOnColumnByBatchTypeAndTime
    @Test ( expected = NoRecordsFetchedException.class)
    public void updateLastRunEndTimeByBatchTypeAndTimeTestBatchTrackListEmpty() throws NoRecordsFetchedException,
        InvalidInputException
    {
        Mockito
            .when( batchTrackerDao.findByColumn( Mockito.eq( BatchTracker.class ), Mockito.anyString(), Mockito.anyString() ) )
            .thenReturn( new ArrayList<BatchTracker>() );
        batchTrackerServiceImpl.updateLastRunEndTimeByBatchType( "test" );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void updateLastRunEndTimeByBatchTypeAndTimeTestBatchTrackListNull() throws NoRecordsFetchedException,
        InvalidInputException
    {
        Mockito
            .when( batchTrackerDao.findByColumn( Mockito.eq( BatchTracker.class ), Mockito.anyString(), Mockito.anyString() ) )
            .thenReturn( null );
        batchTrackerServiceImpl.updateLastRunEndTimeByBatchType( "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateLastRunEndTimeByBatchTypeAndTimeForBatchTypeNull() throws NoRecordsFetchedException,
        InvalidInputException
    {
        batchTrackerServiceImpl.updateLastRunEndTimeByBatchType( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateLastRunEndTimeByBatchTypeAndTimeForBatchTypeEmpty() throws NoRecordsFetchedException,
        InvalidInputException
    {
        batchTrackerServiceImpl.updateLastRunEndTimeByBatchType( "" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testUpdateErrorForBatchTrackerByBatchTypeForBatchTypeEmpty() throws NoRecordsFetchedException,
        InvalidInputException
    {
        batchTrackerServiceImpl.updateErrorForBatchTrackerByBatchType( "" , "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testUpdateErrorForBatchTrackerByBatchTypeForBatchTypeNull() throws NoRecordsFetchedException,
        InvalidInputException
    {
        batchTrackerServiceImpl.updateErrorForBatchTrackerByBatchType( null , "test" );
    }
    
    @Test ( expected = NoRecordsFetchedException.class)
    public void testUpdateErrorForBatchTrackerByBatchTypeForNUllListFound() throws NoRecordsFetchedException,
        InvalidInputException
    {
        Mockito
        .when( batchTrackerDao.findByColumn( Mockito.eq( BatchTracker.class ), Mockito.anyString(), Mockito.anyString() ) )
        .thenReturn( null );
        batchTrackerServiceImpl.updateErrorForBatchTrackerByBatchType( "test" , "test" );
    }
    
    
    @Test ( expected = NoRecordsFetchedException.class)
    public void testUpdateErrorForBatchTrackerByBatchTypeForEmptyListFound() throws NoRecordsFetchedException,
        InvalidInputException
    {
        Mockito
        .when( batchTrackerDao.findByColumn( Mockito.eq( BatchTracker.class ), Mockito.anyString(), Mockito.anyString() ) )
        .thenReturn( new ArrayList<BatchTracker>() );
        batchTrackerServiceImpl.updateErrorForBatchTrackerByBatchType( "test" , "test" );
    }

    
    @Test ( expected = InvalidInputException.class)
    public void testSendMailToAdminRegardingBatchErrorForNullException() throws NoRecordsFetchedException,
        InvalidInputException, UndeliveredEmailException
    {
        batchTrackerServiceImpl.sendMailToAdminRegardingBatchError( "test", 100l, null );
    }
}
