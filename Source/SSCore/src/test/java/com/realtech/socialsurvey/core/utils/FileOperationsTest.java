package com.realtech.socialsurvey.core.utils;

import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.entities.FileContentReplacements;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public class FileOperationsTest
{
    @InjectMocks
    private FileOperations fileOperations;

    // this is just a dummy path no file exist in this location
    private String dummyFilePath = "/home/test/testfile.txt";


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
    public void testGetContentFromFileWithNullFileName() throws InvalidInputException
    {
        fileOperations.getContentFromFile( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetContentFromFileWithEmptyFileName() throws InvalidInputException
    {
        fileOperations.getContentFromFile( TestConstants.TEST_EMPTY_STRING );
    }


    @Test
    public void testGetContentFromFileWithDummyFileName() throws InvalidInputException
    {
        assertEquals( "File Content does not match expected", 0, fileOperations.getContentFromFile( dummyFilePath ).length() );
    }


    /**
     * Test case to check whether the method reads actual content
     * commented as this needs an actual file name path to be provided
     * for successful passing of the test case.
     * */
    //@Test
    public void testGetContentFromFileWithActualFileName() throws InvalidInputException
    {
        assertEquals( "File Content does not match expected", 0, fileOperations.getContentFromFile( dummyFilePath ).length() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testReplaceFileContentsWithFileContentReplacements() throws InvalidInputException
    {
        fileOperations.replaceFileContents( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testReplaceFileContentsWithFileContentReplacementsHavingNullFileName() throws InvalidInputException
    {
        fileOperations.replaceFileContents( new FileContentReplacements() );
    }


    /**
     * Test case to check whether the method reads actual content and does not replace
     * file contents. Commented as this needs an actual file name path to be provided
     * for successful passing of the test case.
     * */
    //@Test
    public void testReplaceFileContentsWithFileContentReplacementsHavingActualFileNameAndNoReplacementArgs()
        throws InvalidInputException
    {
        assertEquals( "File Content does not match expected", 0, fileOperations.getContentFromFile( dummyFilePath ).length() );
    }


    /**
     * Test case to check whether the method reads actual content and does replace
     * file contents. Commented as this needs an actual file name path to be provided
     * for successful passing of the test case.
     * */
    //@Test
    public void testReplaceFileContentsWithFileContentReplacementsHavingActualFileNameAndReplacementArgs()
        throws InvalidInputException
    {
        assertEquals( "File Content does not match expected", 0, fileOperations.getContentFromFile( dummyFilePath ).length() );
    }
}
