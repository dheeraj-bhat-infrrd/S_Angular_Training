package com.realtech.socialsurvey.core.dao.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.entities.ForwardMailDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public class MongoForwardMailDetailsDaoImplTest
{
    @InjectMocks
    private MongoForwardMailDetailsDaoImpl mongoForwardMailDetailsDaoImpl;


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
    public void testInsertForwardMailDetailsWithNullForwardMailDetails() throws InvalidInputException
    {
        mongoForwardMailDetailsDaoImpl.insertForwardMailDetails( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testInsertForwardMailDetailsWithNullSenderMailId() throws InvalidInputException
    {
        mongoForwardMailDetailsDaoImpl.insertForwardMailDetails( new ForwardMailDetails() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testInsertForwardMailDetailsWithEmptySenderMailId() throws InvalidInputException
    {
        ForwardMailDetails forwardMailDetails = new ForwardMailDetails();
        forwardMailDetails.setSenderMailId( TestConstants.TEST_EMPTY_STRING );
        mongoForwardMailDetailsDaoImpl.insertForwardMailDetails( forwardMailDetails );
    }


    @Test ( expected = InvalidInputException.class)
    public void testInsertForwardMailDetailsWithNullRecipientMailId() throws InvalidInputException
    {
        ForwardMailDetails forwardMailDetails = new ForwardMailDetails();
        forwardMailDetails.setSenderMailId( TestConstants.TEST_MAIL_ID_STRING );
        mongoForwardMailDetailsDaoImpl.insertForwardMailDetails( forwardMailDetails );
    }


    @Test ( expected = InvalidInputException.class)
    public void testInsertForwardMailDetailsWithEmptyRecipientMailId() throws InvalidInputException
    {
        ForwardMailDetails forwardMailDetails = new ForwardMailDetails();
        forwardMailDetails.setSenderMailId( TestConstants.TEST_MAIL_ID_STRING );
        forwardMailDetails.setRecipientMailId( TestConstants.TEST_EMPTY_STRING );
        mongoForwardMailDetailsDaoImpl.insertForwardMailDetails( forwardMailDetails );
    }


    @Test ( expected = InvalidInputException.class)
    public void testInsertForwardMailDetailsWithNullMessageId() throws InvalidInputException
    {
        ForwardMailDetails forwardMailDetails = new ForwardMailDetails();
        forwardMailDetails.setSenderMailId( TestConstants.TEST_MAIL_ID_STRING );
        forwardMailDetails.setRecipientMailId( TestConstants.TEST_MAIL_ID_STRING );
        mongoForwardMailDetailsDaoImpl.insertForwardMailDetails( forwardMailDetails );
    }


    @Test ( expected = InvalidInputException.class)
    public void testInsertForwardMailDetailsWithEmptyMessageId() throws InvalidInputException
    {
        ForwardMailDetails forwardMailDetails = new ForwardMailDetails();
        forwardMailDetails.setSenderMailId( TestConstants.TEST_MAIL_ID_STRING );
        forwardMailDetails.setRecipientMailId( TestConstants.TEST_MAIL_ID_STRING );
        forwardMailDetails.setRecipientMailId( TestConstants.TEST_EMPTY_STRING );
        mongoForwardMailDetailsDaoImpl.insertForwardMailDetails( forwardMailDetails );
    }


    @Test ( expected = InvalidInputException.class)
    public void testCheckIfForwardMailDetailsExistWithNullSenderMailId() throws InvalidInputException
    {
        mongoForwardMailDetailsDaoImpl.checkIfForwardMailDetailsExist( null, TestConstants.TEST_MAIL_ID_STRING,
            TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testCheckIfForwardMailDetailsExistWithEmptySenderMailId() throws InvalidInputException
    {
        mongoForwardMailDetailsDaoImpl.checkIfForwardMailDetailsExist( TestConstants.TEST_EMPTY_STRING,
            TestConstants.TEST_MAIL_ID_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testCheckIfForwardMailDetailsExistWithNullRecipientMailId() throws InvalidInputException
    {
        mongoForwardMailDetailsDaoImpl.checkIfForwardMailDetailsExist( TestConstants.TEST_MAIL_ID_STRING, null,
            TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testCheckIfForwardMailDetailsExistWithEmptyRecipientMailId() throws InvalidInputException
    {
        mongoForwardMailDetailsDaoImpl.checkIfForwardMailDetailsExist( TestConstants.TEST_MAIL_ID_STRING,
            TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testCheckIfForwardMailDetailsExistWithNullMessageId() throws InvalidInputException
    {
        mongoForwardMailDetailsDaoImpl.checkIfForwardMailDetailsExist( TestConstants.TEST_MAIL_ID_STRING,
            TestConstants.TEST_MAIL_ID_STRING, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testCheckIfForwardMailDetailsExistWithEmptyMessageId() throws InvalidInputException
    {
        mongoForwardMailDetailsDaoImpl.checkIfForwardMailDetailsExist( TestConstants.TEST_MAIL_ID_STRING,
            TestConstants.TEST_MAIL_ID_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateStatusOfForwardMailDetailsWithNullId() throws InvalidInputException
    {
        mongoForwardMailDetailsDaoImpl.updateStatusOfForwardMailDetails( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateStatusOfForwardMailDetailsWithEmptyId() throws InvalidInputException
    {
        mongoForwardMailDetailsDaoImpl.updateStatusOfForwardMailDetails( TestConstants.TEST_EMPTY_STRING );
    }
}
