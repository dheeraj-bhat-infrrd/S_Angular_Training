package com.realtech.socialsurvey.core.commons;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.services.generator.impl.UrlGeneratorImpl;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author RM03
 * JIRA Ticket SS-6
 */

public class EncryptionService {
	
	private static final Logger LOG = LoggerFactory.getLogger(UrlGeneratorImpl.class);
	
	public static String getNullSafeString(String s) {
        if (s != null) {
                return s;
        }
        else {
                return "";
        }
	}
	
	public static byte[] hexStringToByteArray(String hexString) {
		int len = hexString.length();
		byte[] byteArray = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
		}
		return byteArray;
	}

	public static String byteArrayToHexString(byte[] byteArray) {
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
		return hexString.toString();
	}


	// Algorithm to generate a key to use with AES (256-bit)
	public static SecretKeySpec generateAES256Key(String plainTextKey) {
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
	//		return new SecretKeySpec("0123456789012345".getBytes(), "AES");
	}


	
	// Call encryptAES instead
	private static byte[] encryptAES256Bytes(byte[] plainText, SecretKeySpec key){
		
		byte[] encryptBytes = null;
		try {
			// Instantiate the cipher
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			// Return encrypted text
			encryptBytes = cipher.doFinal(plainText);
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
		return encryptBytes;
	}

	// Call decryptAES instead
	private static byte[] decryptAES256Bytes(byte[] encryptedText, SecretKeySpec key){
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


	// Encrypt string with AES 256
	public static String encryptAES(String plainText, String plainTextKey) {
		if(plainText==null)
			return null;
		return byteArrayToHexString(encryptAES256Bytes(plainText.getBytes(), generateAES256Key(plainTextKey)));
	}

	// Decrypt string with AES 256
	public static String decryptAES(String encryptedHexString, String plainTextKey) {
		if(encryptedHexString ==null || encryptedHexString.isEmpty())
			return null;
		return new String(decryptAES256Bytes(hexStringToByteArray(encryptedHexString), generateAES256Key(plainTextKey)));
	}
	
	
 
}
