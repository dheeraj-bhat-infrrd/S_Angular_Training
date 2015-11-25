package com.realtech.socialsurvey.core.services.support.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;


public class UserSupportServiceImplTest
{
    private UserSupportServiceImpl userSupportServiceImpl;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
        userSupportServiceImpl = new UserSupportServiceImpl();
    }


    @After
    public void tearDown() throws Exception
    {}


    //Tests for sendHelpMailToAdmin
    @Test ( expected = InvalidInputException.class)
    public void sendHelpMailToAdminTestSenderEmailNull() throws NonFatalException
    {
        userSupportServiceImpl.sendHelpMailToAdmin( null, "test", "test", null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendHelpMailToAdminTestSenderEmailEmpty() throws NonFatalException
    {
        userSupportServiceImpl.sendHelpMailToAdmin( "", "test", "test", null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendHelpMailToAdminTestSenderNameNull() throws NonFatalException
    {
        userSupportServiceImpl.sendHelpMailToAdmin( "test", null, "test", null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendHelpMailToAdminTestSenderNameEmpty() throws NonFatalException
    {
        userSupportServiceImpl.sendHelpMailToAdmin( "test", "", "test", null, null );
    }
}
