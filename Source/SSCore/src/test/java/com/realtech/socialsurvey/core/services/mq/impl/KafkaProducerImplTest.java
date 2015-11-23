package com.realtech.socialsurvey.core.services.mq.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.realtech.socialsurvey.core.enums.EmailHeader;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public class KafkaProducerImplTest
{
    private KafkaProducerImpl kafkaProducerImpl;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
        kafkaProducerImpl = new KafkaProducerImpl();
    }


    @After
    public void tearDown() throws Exception
    {}


    @Test ( expected = InvalidInputException.class)
    public void queueEmailTestHeaderNull() throws InvalidInputException
    {
        kafkaProducerImpl.queueEmail( null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void queueEmailTestContentNull() throws InvalidInputException
    {
        kafkaProducerImpl.queueEmail( EmailHeader.ACCOUNT_DISABLED, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void queueEmailTestContentEmpty() throws InvalidInputException
    {
        kafkaProducerImpl.queueEmail( EmailHeader.ACCOUNT_DISABLED, "" );
    }
}
