package com.realtech.socialsurvey.core.dao.impl;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.realtech.socialsurvey.core.exception.InvalidInputException;

public class UserDaoImplTest
{
    @InjectMocks
    private UserDaoImpl userDaoImpl;


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
    public void testGetUsersForUserIdsWithNullUserIds() throws InvalidInputException
    {
        userDaoImpl.getUsersForUserIds( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUsersForUserIdsWithEmptyUserIds() throws InvalidInputException
    {
        userDaoImpl.getUsersForUserIds( new ArrayList<Long>() );
    }
}
