package com.realtech.socialsurvey.core.services.upload.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import com.realtech.socialsurvey.TestConstants;


public class UploadUtilsTest
{
    @InjectMocks
    private UploadUtils uploadUtils;


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


    //Tests for imageSize
    @Test
    public void imageSizeTestMaxBytesInvalid()
    {
        Whitebox.setInternalState( uploadUtils, "maxBytes", -1 );
        assertTrue( "", uploadUtils.imageSize( new File( TestConstants.TEST_FILE_PATH ) ) );
    }


    @Test
    public void imageSizeTestMaxBytesValid()
    {
        Whitebox.setInternalState( uploadUtils, "maxBytes", 0 );
        assertFalse( "", uploadUtils.imageSize( new File( TestConstants.TEST_FILE_PATH ) ) );
    }


    //Tests for imageFormat
    /**
     * The below test case uses a static method createImageInputStream which is part of ImageIO
     * This method cannot be mocked using Mockito. 
     */
    /*    @Test
        public void imageFormatTestSetFormatsContainsFormatName()
        {
            assertFalse( "", uploadUtils.imageFormat( new File( TestConstants.TEST_FILE_PATH ) ) );
        }*/

    //Tests for imageDimension
    @Test
    public void imageDimensionTestMaxWidthMaxHeightNegativeOne()
    {
        Whitebox.setInternalState( uploadUtils, "maxWidth", -1 );
        Whitebox.setInternalState( uploadUtils, "maxHeight", -1 );
        assertTrue( "", uploadUtils.imageDimension( new File( TestConstants.TEST_FILE_PATH ) ) );
    }


    /**
     * The remaining test cases use a static method createImageInputStream which is part of ImageIO which cannot be mocked using Mockito
     */

    //Tests for cropImage
    @Test
    public void cropImageTestBufferedImageWithinBounds()
    {
        assertEquals( "", 5, uploadUtils.cropImage( new BufferedImage( 10, 10, 10 ), 5, 5, 5, 5 ).getWidth() );
        assertEquals( "", 5, uploadUtils.cropImage( new BufferedImage( 10, 10, 10 ), 5, 5, 5, 5 ).getHeight() );
    }


    @Test
    public void cropImageTestBufferedImageOutOfBounds()
    {
        assertEquals( "", null, uploadUtils.cropImage( new BufferedImage( 10, 10, 10 ), 5, 5, 10, 10 ) );
    }
}
