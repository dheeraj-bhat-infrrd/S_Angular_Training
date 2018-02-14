package com.realtech.socialsurvey.cipher.helper;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class CipherHelper {

	/**
	 * Converts a hexadecimal string to a byte array.
	 * 
	 * @param hexString
	 * @return a byte array of the hex string
	 */
	private byte[] hexStringToByteArray(String hexString) {
		int len = hexString.length();
		byte[] byteArray = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
					+ Character.digit(hexString.charAt(i + 1), 16));
		}
		return byteArray;
	}

	/**
	 * converts a byte array into hexadecimal string.
	 * 
	 * @param byteArray
	 * @return a hexadecimal string of the byte array.
	 */
	private String byteArrayToHexString(byte[] byteArray) {
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
		return hexStr;
	}

	/**
	 * Generates SecretKEySpec object from the plain text key.
	 * 
	 * @param plainTextKey
	 * @return a SecretKeySpec object
	 */
	public SecretKeySpec generateAES256Key(String plainTextKey) {

		byte[] key;
		// Random salt
		String salt = "6f90b8d50f490e647d92e2a74d2c44d7";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update((plainTextKey + salt).getBytes());
			key = md.digest();
		} catch (NoSuchAlgorithmException ex) {
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
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	byte[] encryptAES256Bytes(byte[] plainText, SecretKeySpec key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		byte[] encryptBytes = null;

		// Instantiate the cipher
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		// Return encrypted text
		encryptBytes = cipher.doFinal(plainText);

		return encryptBytes;
	}

	/**
	 * Converts cipher text byte array using the key into plain text byte array.
	 * 
	 * @param encryptedText
	 * @param key
	 * @return plain text Byte Array
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	byte[] decryptAES256Bytes(byte[] encryptedText, SecretKeySpec key) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		byte[] decryptBytes = null;

		// Instantiate the cipher
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		// Return plain text
		decryptBytes = cipher.doFinal(encryptedText);

		return decryptBytes;
	}

	/**
	 * Takes the plain text String and key String and uses AES encryption to convert
	 * it to cipher text.
	 * 
	 * @param plainText
	 * @param plainTextKey
	 * @return cipher text String
	 */
	public String encryptAES(String plainText, String plainTextKey) {
		try {
		return byteArrayToHexString(
				encryptAES256Bytes(plainText.getBytes(), generateAES256Key(plainTextKey)));
		} catch( Exception error ) {
			return "Error: " + error.getMessage();
		}
	}

	/**
	 * Takes the encrypted hexadecimal String and the key String and uses AES
	 * algorithm to convert it to plain text String
	 * 
	 * @param encryptedHexString
	 * @param plainTextKey
	 * @return plain text String
	 */
	public String decryptAES(String encryptedHexString, String plainTextKey) {
		try {
		return new String(
				decryptAES256Bytes(hexStringToByteArray(encryptedHexString), generateAES256Key(plainTextKey)));
		} catch( Exception error ) {
			return "Error: " + error.getMessage();
		}
	}

}
