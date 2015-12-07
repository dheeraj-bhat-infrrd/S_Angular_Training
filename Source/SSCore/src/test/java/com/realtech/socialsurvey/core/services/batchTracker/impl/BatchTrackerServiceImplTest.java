package com.realtech.socialsurvey.core.services.batchTracker.impl;

import java.sql.Timestamp;
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


    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetLastRunTimeByBatchTypeWhenEmptyListFound() throws NoRecordsFetchedException
    {
        Mockito
            .when( batchTrackerDao.findByColumn( Mockito.eq( BatchTracker.class ), Mockito.anyString(), Mockito.anyObject() ) )
            .thenReturn( new ArrayList<BatchTracker>() );
        batchTrackerServiceImpl.getLastRunEndTimeByBatchType( "" );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testGetLastRunTimeByBatchTypeWhenNoBatchTrackerFound() throws NoRecordsFetchedException
    {
        Mockito
            .when( batchTrackerDao.findByColumn( Mockito.eq( BatchTracker.class ), Mockito.anyString(), Mockito.anyObject() ) )
            .thenReturn( null );
        batchTrackerServiceImpl.getLastRunEndTimeByBatchType( "" );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testUpdateModifiedOnColumnByBatchTypeWhenEmptyListFound() throws NoRecordsFetchedException,
        InvalidInputException
    {
        Mockito
            .when( batchTrackerDao.findByColumn( Mockito.eq( BatchTracker.class ), Mockito.anyString(), Mockito.anyObject() ) )
            .thenReturn( new ArrayList<BatchTracker>() );
        batchTrackerServiceImpl.updateLastRunEndTimeByBatchType( "" );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testUpdateModifiedOnColumnByBatchTypeWhenNoBatchTrackerFound() throws NoRecordsFetchedException,
        InvalidInputException
    {
        Mockito
            .when( batchTrackerDao.findByColumn( Mockito.eq( BatchTracker.class ), Mockito.anyString(), Mockito.anyObject() ) )
            .thenReturn( null );
        batchTrackerServiceImpl.updateLastRunEndTimeByBatchType( "" );
    }


    //Tests for updateModifiedOnColumnByBatchTypeAndTime
    @Test ( expected = NoRecordsFetchedException.class)
    public void updateModifiedOnColumnByBatchTypeAndTimeTestBatchTrackListEmpty() throws NoRecordsFetchedException,
        InvalidInputException
    {
        Mockito
            .when( batchTrackerDao.findByColumn( Mockito.eq( BatchTracker.class ), Mockito.anyString(), Mockito.anyString() ) )
            .thenReturn( new ArrayList<BatchTracker>() );
        batchTrackerServiceImpl.updateModifiedOnColumnByBatchTypeAndTime( "test", new Timestamp( 0 ) );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void updateModifiedOnColumnByBatchTypeAndTimeTestBatchTrackListNull() throws NoRecordsFetchedException,
        InvalidInputException
    {
        Mockito
            .when( batchTrackerDao.findByColumn( Mockito.eq( BatchTracker.class ), Mockito.anyString(), Mockito.anyString() ) )
            .thenReturn( null );
        batchTrackerServiceImpl.updateModifiedOnColumnByBatchTypeAndTime( "test", new Timestamp( 0 ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void updateModifiedOnColumnByBatchTypeAndTimeTestBatchTypeNull() throws NoRecordsFetchedException,
        InvalidInputException
    {
        batchTrackerServiceImpl.updateModifiedOnColumnByBatchTypeAndTime( null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void updateModifiedOnColumnByBatchTypeAndTimeTestBatchTypeEmpty() throws NoRecordsFetchedException,
        InvalidInputException
    {
        batchTrackerServiceImpl.updateModifiedOnColumnByBatchTypeAndTime( "", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void updateModifiedOnColumnByBatchTypeAndTimeTestTimeNull() throws NoRecordsFetchedException, InvalidInputException
    {
        batchTrackerServiceImpl.updateModifiedOnColumnByBatchTypeAndTime( "test", null );
    }
}
