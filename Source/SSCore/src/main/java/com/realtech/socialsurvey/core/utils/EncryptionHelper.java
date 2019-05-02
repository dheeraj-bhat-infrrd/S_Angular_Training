package com.realtech.socialsurvey.core.utils;

// JIRA: SS-6: By RM03

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * This class houses all the encryption utilities required for the project.
 */
@Component
public class EncryptionHelper {

	private static final Logger LOG = LoggerFactory.getLogger(EncryptionHelper.class);

	/**
	 * Checks if String is null and returns blank String if it is null.
	 * 
	 * @param s
	 *            is a String to be checked.
	 * @return the String as it is if not null else a blank string.
	 */
	public String getNullSafeString(String s) {
		if (s != null) {
			return s;
		}
		else {
			return "";
		}
	}

	/**
	 * Converts a hexadecimal string to a byte array.
	 * 
	 * @param hexString
	 * @return a byte array of the hex string
	 * @throws InvalidInputException
	 */
	public byte[] hexStringToByteArray(String hexString) throws InvalidInputException {

		if (hexString == null) {
			LOG.error("Null parameter passed to hexStringToByteArray of EncryptionHelper!");
			throw new InvalidInputException("Null parameter passed to hexStringToByteArray of EncryptionHelper!");
		}

		LOG.debug(" hexStringToByteArray() : input parameter : " + hexString);

		int len = hexString.length();
		byte[] byteArray = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
		}

		LOG.debug(" hexStringToByteArray() : output : " + byteArray.toString());
		return byteArray;
	}

	/**
	 * converts a byte array into hexadecimal string.
	 * 
	 * @param byteArray
	 * @return a hexadecimal string of the byte array.
	 * @throws InvalidInputException
	 */
	public String byteArrayToHexString(byte[] byteArray) throws InvalidInputException {

		if (byteArray == null) {
			LOG.error("Null parameter passed to byteArrayToHexString of EncryptionHelper!");
			throw new InvalidInputException("Null parameter passed to byteArrayToHexString of EncryptionHelper!");
		}

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			byte temp = byteArray[i];

			String s = Integer.toHexString(Byte.valueOf(temp));

			while (s.length() < 2) {
				s = "0" + s;
			}
			s = s.substring(s.length() - 2);
			hexString.append(s);
		}
		String hexStr = hexString.toString();
		LOG.info("byteArrayToHexString() Output : " + hexStr);
		return hexStr;
	}

	/**
	 * Generates SecretKEySpec object from the plain text key.
	 * 
	 * @param plainTextKey
	 * @return a SecretKeySpec object
	 * @throws InvalidInputException
	 */
	public SecretKeySpec generateAES256Key(String plainTextKey) throws InvalidInputException {

		if (plainTextKey == null) {
			LOG.error("Null parameter passed to generateAES256Key of EncryptionHelper!");
			throw new InvalidInputException("Null parameter passed to generateAES256Key of EncryptionHelper!");
		}

		LOG.debug("generateAES256Key() :  input parameter : " + plainTextKey);

		byte[] key;
		// Random salt
		String salt = "6f90b8d50f490e647d92e2a74d2c44d7";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update((getNullSafeString(plainTextKey) + salt).getBytes());
			key = md.digest();
		}
		catch (NoSuchAlgorithmException ex) {
			// SHA-256 hash key
			key = hexStringToByteArray("43c2fbc4e027b47c3d8eaff48f1bcb4fa3ecbe0585b1993e5f92b0b07b92eebb");
		}
		return new SecretKeySpec(key, "AES");
	}

	/**
	 * Converts the plain text byte array using the key into encrypted byte array.
	 * 
	 * @param plainText
	 * @param key
	 * @return encrypted text Byte Array
	 * @throws InvalidInputException
	 */
	byte[] encryptAES256Bytes(byte[] plainText, SecretKeySpec key) throws InvalidInputException {

		if (plainText == null) {
			LOG.error("Null parameter passed as first argument to encryptAES256Bytes of EncryptionHelper!");
			throw new InvalidInputException("Null parameter passed as first argument to encryptAES256Bytes of EncryptionHelper!");
		}

		if (key == null) {
			LOG.error("Null parameter passed as second argument to encryptAES256Bytes of EncryptionHelper!");
			throw new InvalidInputException("Null parameter passed as second argument to encryptAES256Bytes of EncryptionHelper!");
		}

		byte[] encryptBytes = null;
		try {
			// Instantiate the cipher
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			// Return encrypted text
			encryptBytes = cipher.doFinal(plainText);
		}
		catch (NoSuchAlgorithmException e) {
			LOG.error("NoSuchAlgorithmException while encrypting: {}", e.getMessage());
			throw new FatalException("NoSuchAlgorithmException while encrypting: " + e.getMessage(), e);
		}
		catch (NoSuchPaddingException e) {
			LOG.error("NoSuchPaddingException while encrypting: {}", e.getMessage());
			throw new FatalException("NoSuchPaddingException while encrypting: " + e.getMessage(), e);
		}
		catch (InvalidKeyException e) {
			LOG.error("InvalidKeyException while encrypting: {}", e.getMessage());
			throw new FatalException("InvalidKeyException while encrypting: " + e.getMessage(), e);
		}
		catch (IllegalBlockSizeException e) {
			LOG.error("IllegalBlockSizeException while encrypting: {}", e.getMessage());
			throw new FatalException("IllegalBlockSizeException while encrypting: " + e.getMessage(), e);
		}
		catch (BadPaddingException e) {
			LOG.error("BadPaddingException while encrypting: {}", e.getMessage());
			throw new InvalidInputException("BadPaddingException while encrypting: " + e.getMessage(), e);
		}
		return encryptBytes;
	}

	/**
	 * Converts cipher text byte array using the key into plain text byte array.
	 * 
	 * @param encryptedText
	 * @param key
	 * @return plain text Byte Array
	 * @throws InvalidInputException
	 */
	byte[] decryptAES256Bytes(byte[] encryptedText, SecretKeySpec key) throws InvalidInputException {

		if (encryptedText == null) {
			LOG.error("Null parameter passed as first argument to decryptAES256Bytes of EncryptionHelper!");
			throw new InvalidInputException("Null parameter passed as first argument to decryptAES256Bytes of EncryptionHelper!");
		}

		if (key == null) {
			LOG.error("Null parameter passed as second argument to decryptAES256Bytes of EncryptionHelper!");
			throw new InvalidInputException("Null parameter passed as second argument to decryptAES256Bytes of EncryptionHelper!");
		}

		byte[] decryptBytes = null;
		try {
			// Instantiate the cipher
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			// Return plain text
			decryptBytes = cipher.doFinal(encryptedText);
		}
		catch (NoSuchAlgorithmException e) {
			LOG.error("NoSuchAlgorithmException while encrypting: " + e.getMessage(), e);
			throw new FatalException("NoSuchAlgorithmException while encrypting: " + e.getMessage(), e);
		}
		catch (NoSuchPaddingException e) {
			LOG.error("NoSuchPaddingException while encrypting: " + e.getMessage(), e);
			throw new FatalException("NoSuchPaddingException while encrypting: " + e.getMessage(), e);
		}
		catch (InvalidKeyException e) {
			LOG.error("InvalidKeyException while encrypting: " + e.getMessage(), e);
			throw new FatalException("InvalidKeyException while encrypting: " + e.getMessage(), e);
		}
		catch (IllegalBlockSizeException e) {
			LOG.error("IllegalBlockSizeException while encrypting: " + e.getMessage(), e);
			throw new FatalException("IllegalBlockSizeException while encrypting: " + e.getMessage(), e);
		}
		catch (BadPaddingException e) {
			LOG.error("BadPaddingException while encrypting: " + e.getMessage(), e);
			throw new FatalException("BadPaddingException while encrypting: " + e.getMessage(), e);
		}
		return decryptBytes;
	}

	/**
	 * Takes the plain text String and key String and uses AES encryption to convert it to cipher
	 * text.
	 * 
	 * @param plainText
	 * @param plainTextKey
	 * @return cipher text String
	 * @throws InvalidInputException
	 */
	public String encryptAES(String plainText, String plainTextKey) throws InvalidInputException {
		if (plainText == null) {
			LOG.error("Null parameter passed as first argument to encryptAES of EncryptionHelper!");
			throw new InvalidInputException("Null parameter passed as first argument to encryptAES of EncryptionHelper!");
		}
		if (plainTextKey == null) {
			LOG.error("Null parameter passed as second argument encryptAES of EncryptionHelper!");
			throw new InvalidInputException("Null parameter passed as second argument encryptAES of EncryptionHelper!");
		}
		
		LOG.info("Encrypt AES - input: {}, {}", plainText, plainTextKey);
		String encryptedText = byteArrayToHexString(encryptAES256Bytes(plainText.getBytes(), generateAES256Key(plainTextKey)));
		LOG.info("Encrypt AES - output: {}", encryptedText);
		
		return encryptedText;
	}

	/**
	 * Takes the encrypted hexadecimal String and the key String and uses AES algorithm to convert
	 * it to plain text String
	 * 
	 * @param encryptedHexString
	 * @param plainTextKey
	 * @return plain text String
	 * @throws InvalidInputException
	 */
	public String decryptAES(String encryptedHexString, String plainTextKey) throws InvalidInputException {
		if (encryptedHexString == null) {
			LOG.error("Null parameter passed as first argument to decryptAES of EncryptionHelper!");
			throw new InvalidInputException("Null parameter passed as first argument to decryptAES of EncryptionHelper!");
		}
		if (plainTextKey == null) {
			LOG.error("Null parameter passed as second argument decryptAES of EncryptionHelper!");
			throw new InvalidInputException("Null parameter passed as second argument decryptAES of EncryptionHelper!");
		}

		LOG.debug("Decrypt AES - input: {}, {}", encryptedHexString, plainTextKey);
		String plainText = new String(decryptAES256Bytes(hexStringToByteArray(encryptedHexString), generateAES256Key(plainTextKey)));
		LOG.info("Finished decrypting AES - input:{}, output: {}", encryptedHexString, plainText);
		
		return plainText;
	}

	/**
	 * Method to SHA12 encrypt plane text and convert it to byte array
	 * 
	 * @param plainText
	 * @return
	 * @throws InvalidInputException
	 */
	private byte[] encryptSHA512Bytes(String plainText) {
		LOG.debug("Method encryptSHA512Bytes called for plainText");
		byte key[];
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(getNullSafeString(plainText).getBytes());
			key = md.digest();
		}
		catch (NoSuchAlgorithmException ex) {
			/**
			 * Will return empty string if there is no SHA-512 implementation
			 */
			key = "".getBytes();
		}
		LOG.debug("Method encryptSHA512Bytes finished.Returning byte array");
		return key;
	}

	//JIRA: SS-22 BY RM02
	/**
	 * Method to convert byte array to hex string to be used in application
	 * 
	 * @param plainText
	 * @return
	 * @throws InvalidInputException
	 */
	public String encryptSHA512(String plainText) throws InvalidInputException {
		LOG.debug("encryptSHA512 called");

		byte[] key = encryptSHA512Bytes(plainText);

		LOG.debug("encryptSHA512 finished.Returning encrypted string");
		return byteArrayToHexString(key);
	}


    public String encodeBase64( String plainText ) throws InvalidInputException
    {
        if ( plainText == null || plainText.isEmpty() ) {
            LOG.error( "Plain text passed in argument is empty" );
            throw new InvalidInputException( "Plain text passed in argument is empty" );
        }
        LOG.debug( "Encoding the input into Base64, encodeBase64 called" );

        String encryptedStr = Base64.encodeBase64URLSafeString( plainText.getBytes() );

        LOG.debug( "encodeBase64 finished.Returning encrypted string" );
        return encryptedStr;
    }


    public String decodeBase64( String encryptedText ) throws InvalidInputException
    {
        if ( encryptedText == null || encryptedText.isEmpty() ) {
            LOG.error( "Encrypted text passed in argument is empty" );
            throw new InvalidInputException( "Encrypted text passed in argument is empty" );
        }
        LOG.debug( "Decoding the input into Base64,decodeBase64 called" );

        byte[] key = Base64.decodeBase64( encryptedText.getBytes() );

        LOG.debug( "decodeBase64 finished.Returning decrypted string" );
        return new String( key );
    }
}
