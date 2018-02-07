package com.realtech.socialsurvey.cipher;

import com.realtech.socialsurvey.cipher.helper.CipherHelper;

/*
 * Cipher service to encrypt or decipher text data by replicating Social Survey cipher model 
 */
public class SocialSurveyCipher {

	public static void main(String[] args) {

		System.out.println("");
		System.out.println(
				"##################################################################################################################");

		System.out.println("");
		System.out.println("******************************************** Starting Cipher service *********************************************");
		System.out.println("");
		System.out.println("");

		// gather necessary data
		String key = System.getenv("key") == null ? (System.getProperty("key") == null ? "" : System.getProperty("key"))
				: System.getenv("key");
		String text = System.getenv("text") == null
				? (System.getProperty("text") == null ? "" : System.getProperty("text"))
				: System.getenv("text");
		String mode = System.getenv("mode") == null
				? (System.getProperty("mode") == null ? "" : System.getProperty("mode"))
				: System.getenv("mode");

		// check for the validity of the provided environment variables
		if (!mode.equals("D") && !mode.equals("E")) {
			System.out.println(
					"*************** Cannot find cipher mode in the environment, add '-Dmode=E/D' and try again!! ***************");
			System.out.println();
			System.out.println(
					"##################################################################################################################");
			return;
		} else if (mode.equals("D") && text.isEmpty()) {
			System.out.println(
					"*************** Cannot find cipher text, add '-Dtext=CIPHER-TEXT' and try again!!! ***************");
			System.out.println();
			System.out.println(
					"##################################################################################################################");
			return;
		}

		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println(
				"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("");
		System.out.println("");

		if (key.isEmpty()) {
			System.out.println(
					"Cannot find a non-empty cipher key '-Dkey' in the environment, assuming the key is empty string .......... ");
		}

		if (text.isEmpty()) {
			System.out.println(
					"Cannot find a non-empty text '-Dtext' to work with, assuming the key is empty string .......... ");
		}

		System.out.println("");
		System.out.println("");
		System.out.println(
				"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");

		System.out.println("initial checks done, proceeding .......");
		final CipherHelper cipherHelperInstance = new CipherHelper();

		if (mode.equals("E")) {

			System.out.println("");
			System.out.println(" Encrypting: text presented outside of arg list: '" + text + "', cipher: "
					+ cipherHelperInstance.encryptAES(text, key));

			System.out.println("");
			System.out.println("");
			System.out.println(" Now Encrypting the list of arguments(if any)");
			System.out.println("");
			if (args == null || args.length == 0) {
				System.out.println(" List of arguments not found .....");
			} else {
				for (String arg : args) {
					System.out.println(
							" Encrypting: text: '" + arg + "', cipher: " + cipherHelperInstance.encryptAES(arg, key));
				}
			}

		} else {

			System.out.println("");
			System.out.println(" Decrypting Cipher presented outside of arg list: '" + text + "', text: "
					+ cipherHelperInstance.decryptAES(text, key));

			System.out.println("");
			System.out.println("");
			System.out.println(" Now Decrypting the list of arguments(if any)");
			System.out.println("");
			if (args == null || args.length == 0) {
				System.out.println(" List of arguments not found .....");
			} else {
				for (String arg : args) {
					System.out.println(
							" Decrypting: Cipher: '" + arg + "', text: " + cipherHelperInstance.decryptAES(arg, key));
				}
			}

		}
		System.out.println("");
		System.out.println("");
		System.out.println("");

		System.out.println("");
		System.out.println("******************************************** Closing up cipher service, bye **************************************");

		System.out.println();
		System.out.println(
				"##################################################################################################################");
	}
}
