package com.realtech.socialsurvey.core.utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public class EncryptionHelperTest
{
    @InjectMocks
    private EncryptionHelper encryptionHelper;


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
    public void testHexStringToByteArrayWithNullHexString() throws InvalidInputException
    {
        encryptionHelper.hexStringToByteArray( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testByteArrayToHexStringWithNullByteArray() throws InvalidInputException
    {
        encryptionHelper.byteArrayToHexString( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGenerateAES256KeyWithNullPlainTextKey() throws InvalidInputException
    {
        encryptionHelper.generateAES256Key( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testEncryptAESWithNullPlainText() throws InvalidInputException
    {
        encryptionHelper.encryptAES( null, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testEncryptAESWithNullPlainTextKey() throws InvalidInputException
    {
        encryptionHelper.encryptAES( TestConstants.TEST_STRING, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDecryptAESWithNullPlainText() throws InvalidInputException
    {
        encryptionHelper.decryptAES( null, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDecryptAESWithNullPlainTextKey() throws InvalidInputException
    {
        encryptionHelper.decryptAES( TestConstants.TEST_STRING, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testEncodeBase64WithNullPlainText() throws InvalidInputException
    {
        encryptionHelper.encodeBase64( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testEncodeBase64WithEmptyPlainText() throws InvalidInputException
    {
        encryptionHelper.encodeBase64( TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDecodeBase64WithNullPlainText() throws InvalidInputException
    {
        encryptionHelper.decodeBase64( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDecodeBase64WithEmptyPlainText() throws InvalidInputException
    {
        encryptionHelper.decodeBase64( TestConstants.TEST_EMPTY_STRING );
    }
}
