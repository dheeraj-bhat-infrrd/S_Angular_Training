package com.realtech.socialsurvey.core.utils.images.impl;

import java.awt.image.BufferedImage;
import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public class ImageProcessorImplTest
{
    @InjectMocks
    private ImageProcessorImpl imageProcessorImpl;


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
    public void testProcessImageWithNullImageFileName() throws ImageProcessingException, InvalidInputException
    {
        imageProcessorImpl.processImage( null, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testProcessImageWithEmptyImageFileName() throws ImageProcessingException, InvalidInputException
    {
        imageProcessorImpl.processImage( TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testProcessImageWithNullImageType() throws ImageProcessingException, InvalidInputException
    {
        imageProcessorImpl.processImage( TestConstants.TEST_STRING, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testProcessImageWithEmptyImageType() throws ImageProcessingException, InvalidInputException
    {
        imageProcessorImpl.processImage( TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testProcessImageWithNullImage() throws ImageProcessingException, InvalidInputException
    {
        imageProcessorImpl.processImage( null, 100, 100, ".jpeg" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testProcessImageWithNullImageExtension() throws ImageProcessingException, InvalidInputException
    {
        imageProcessorImpl.processImage( new BufferedImage( 100, 100, BufferedImage.TYPE_INT_RGB ), 100, 100, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testProcessImageWithEmptyImageExtension() throws ImageProcessingException, InvalidInputException
    {
        imageProcessorImpl.processImage( new BufferedImage( 100, 100, BufferedImage.TYPE_INT_RGB ), 100, 100,
            TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetImageFromCloudWithNullImageFileName() throws ImageProcessingException, InvalidInputException
    {
        imageProcessorImpl.getImageFromCloud( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetImageFromCloudWithEmptyImageFileName() throws ImageProcessingException, InvalidInputException
    {
        imageProcessorImpl.getImageFromCloud( TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testWriteImageWithNullImage() throws ImageProcessingException, InvalidInputException
    {
        imageProcessorImpl.writeImage( TestConstants.TEST_STRING, null, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testWriteImageWithNullDestinationFileName() throws ImageProcessingException, InvalidInputException
    {
        imageProcessorImpl.writeImage( null, new File( TestConstants.TEST_STRING ), TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testWriteImageWithEmptyDestinationFileName() throws ImageProcessingException, InvalidInputException
    {
        imageProcessorImpl.writeImage( TestConstants.TEST_EMPTY_STRING, new File( TestConstants.TEST_STRING ),
            TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testWriteImageWithNullImageType() throws ImageProcessingException, InvalidInputException
    {
        imageProcessorImpl.writeImage( TestConstants.TEST_STRING, new File( TestConstants.TEST_STRING ), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testWriteImageWithEmptyImageType() throws ImageProcessingException, InvalidInputException
    {
        imageProcessorImpl.writeImage( TestConstants.TEST_STRING, new File( TestConstants.TEST_STRING ),
            TestConstants.TEST_EMPTY_STRING );
    }
}
