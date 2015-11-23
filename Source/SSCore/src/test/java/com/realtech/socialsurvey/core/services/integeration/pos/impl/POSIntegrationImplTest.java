package com.realtech.socialsurvey.core.services.integeration.pos.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.entities.integration.Agent;
import com.realtech.socialsurvey.core.entities.integration.Customer;
import com.realtech.socialsurvey.core.entities.integration.Engagement;
import com.realtech.socialsurvey.core.entities.integration.EngagementProcessingStatus;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.integeration.pos.AgentNotAvailableException;

public class POSIntegrationImplTest
{
    @InjectMocks
    private POSIntegrationImpl posIntegrationImpl;
    
    @Mock
    private SurveyPreInitiationDao surveyPreInitiationDao;
    
    Engagement engagement;
    Agent agent;
    
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
        engagement = new Engagement();
        agent = new Agent();
    }

    @After
    public void tearDown() throws Exception {}
    
    @Test ( expected = InvalidInputException.class)
    public void testGetLastRunTimeForEmptySource() throws InvalidInputException{
        posIntegrationImpl.getLastRunTime( "" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetLastRunTimeForNullSource() throws InvalidInputException{
        posIntegrationImpl.getLastRunTime( null );
    }
    
    
    @Test ( expected = InvalidInputException.class)
    public void testGetProcessedRecordsForEmptySource() throws InvalidInputException{
        Mockito.when( surveyPreInitiationDao.getProcessedIds( Mockito.anyString(), new Timestamp( Mockito.anyLong()))).thenReturn( new ArrayList<EngagementProcessingStatus>() );
        posIntegrationImpl.getProcessedRecords( "", new Timestamp( 0l ) );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetProcessedRecordsForNullSource() throws InvalidInputException{
        Mockito.when( surveyPreInitiationDao.getProcessedIds( Mockito.anyString(), new Timestamp( Mockito.anyLong()))).thenReturn( new ArrayList<EngagementProcessingStatus>() );
        posIntegrationImpl.getProcessedRecords( null , new Timestamp( 0l ) );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testInsertSurveyPreInitiationRecordForNullEngagement() throws InvalidInputException, AgentNotAvailableException{
        posIntegrationImpl.insertSurveyPreInitiationRecord( null, true );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testInsertSurveyPreInitiationRecordForNullAgent() throws InvalidInputException, AgentNotAvailableException{
        posIntegrationImpl.insertSurveyPreInitiationRecord( engagement , true );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testInsertSurveyPreInitiationRecordForEmptyAgent() throws InvalidInputException, AgentNotAvailableException{
        engagement.setAgent( agent );
        posIntegrationImpl.insertSurveyPreInitiationRecord( engagement , true );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testInsertSurveyPreInitiationRecordForNullAgentEmailId() throws InvalidInputException, AgentNotAvailableException{
        agent.setAgentEmailId( null );
        engagement.setAgent( agent );
        posIntegrationImpl.insertSurveyPreInitiationRecord( engagement , true );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testInsertSurveyPreInitiationRecordForEmptyAgentEmailId() throws InvalidInputException, AgentNotAvailableException{
        agent.setAgentEmailId( "" );
        engagement.setAgent( agent );
        posIntegrationImpl.insertSurveyPreInitiationRecord( engagement , true );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testInsertSurveyPreInitiationRecordForNullCustomerList() throws InvalidInputException, AgentNotAvailableException{
        agent.setAgentEmailId( "test" );
        engagement.setAgent( agent );
        posIntegrationImpl.insertSurveyPreInitiationRecord( engagement , true );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testInsertSurveyPreInitiationRecordForEmptyCustomerList() throws InvalidInputException, AgentNotAvailableException{
        agent.setAgentEmailId( "test" );
        List<Customer> customerList = new ArrayList<Customer>();
        engagement.setCustomers( customerList );
        engagement.setAgent( agent );
        posIntegrationImpl.insertSurveyPreInitiationRecord( engagement , true );
    }
    
    @Test ( expected = AgentNotAvailableException.class)
    public void testInsertSurveyPreInitiationRecordForInvalidAgent() throws InvalidInputException, AgentNotAvailableException{
        agent.setAgentEmailId( "nishit+none@raremile.com" );
        Customer customer = new Customer();
        List<Customer> customerList = new ArrayList<Customer>();
        customerList.add( customer );
        engagement.setCustomers( customerList );
        engagement.setAgent( agent );
        posIntegrationImpl.insertSurveyPreInitiationRecord( engagement , true );
    }
    
}
