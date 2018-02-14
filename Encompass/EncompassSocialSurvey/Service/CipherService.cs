using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;

namespace EncompassSocialSurvey.Service
{
    class CipherService
    {

        // SHA-256 hasher to generate AES key  
        private SHA256 hasher = SHA256.Create();


        #region public cipher methods, encrypt and decrypt



        /*
         *  Method to encrypt string data using AES-256 cipher with a secret key( string ) hashed using SHA-256 algorithm 
         */
        public string encrypt(string plaintext, string key)
        {

            // Step 1: concat 'key' and 'salt' ( default Social Survey Salt ) to get the AES-256 cipher key
            // Step 2: decode the cipher key string to bytes
            // Step 3: hash the cipher key bytes with SHA-256
            // Step 4: decode the text string to be encrypted to bytes
            // Step 5: use AES-256 encryption
            // Step 6: encode the encrypted cipher bytes to hexadecimal string

            byte[] cipherKey = hasher.ComputeHash(byteDecoder(key + EncompassSocialSurveyConstant.SOCIAL_SURVEY_ENCRYPTION_SALT, EncompassSocialSurveyConstant.CIPHER_BYTE_CODING_TYPE));
            return byteArrayToHexString(encryptAES256Bytes(byteDecoder(plaintext, EncompassSocialSurveyConstant.CIPHER_BYTE_CODING_TYPE), cipherKey));
        }



        /*
         *  Method to decrypt hexadecimal string using AES-256 cipher with a secret key( string ) hashed using SHA-256 algorithm 
         */
        public string decrypt(string cipherText, string key)
        {

            // Step 1: concat 'key' and 'salt' ( default Social Survey Salt ) to get the AES-256 cipher key
            // Step 2: decode the cipher key string to bytes
            // Step 3: hash the cipher key bytes with SHA-256
            // Step 4: decode the encrypted hexadecimal string to bytes
            // Step 5: use AES-256 decryption
            // Step 6: encode the decrypted bytes to get plain text

            byte[] cipherKey = hasher.ComputeHash(byteDecoder(key + EncompassSocialSurveyConstant.SOCIAL_SURVEY_ENCRYPTION_SALT, EncompassSocialSurveyConstant.CIPHER_BYTE_CODING_TYPE));
            return byteEncoder(decryptAES256Bytes(hexStringToByteArray(cipherText), cipherKey), EncompassSocialSurveyConstant.CIPHER_BYTE_CODING_TYPE);
        }




        #endregion




        #region private support menthods



        /*
         *  Method to encrypt data in bytes with a cipher key in bytes and return cipher in bytes
         */
        private byte[] encryptAES256Bytes(byte[] data, byte[] cipherKey)
        {
            // resultant encrypted byte array
            byte[] encryptedBytes = null;

            // rijndael algorithm, a more generic version of AES 
            using (RijndaelManaged AES = aesCipherSpout(cipherKey))
            {
                // encryption
                encryptedBytes = AES.CreateEncryptor().TransformFinalBlock(data, 0, data.Length);
            }
            return encryptedBytes;
        }



        /*
         *  Method to decrypt data in bytes with a cipher key in bytes and return plain text in bytes
         */
        private byte[] decryptAES256Bytes(byte[] cipher, byte[] cipherKey)
        {
            // resultant decrypted byte array
            byte[] decryptedBytes = null;

            // rijndael algorithm, a more generic version of AES 
            using (RijndaelManaged AES = aesCipherSpout(cipherKey))
            {
                // decryption
                decryptedBytes = AES.CreateDecryptor().TransformFinalBlock(cipher, 0, cipher.Length);
            }
            return decryptedBytes;
        }



        /*
         *  Method that creates and initializes AES-256 cipher instance using RijndaelManaged
         */
        private RijndaelManaged aesCipherSpout(byte[] cipherKey)
        {

            // set up new RijndaelManaged instance
            RijndaelManaged rijndaelInstance = new RijndaelManaged();

            rijndaelInstance.KeySize = 256;  // cipher key size after Hashing with SHA-256
            rijndaelInstance.BlockSize = 128;  // AES block size
            rijndaelInstance.Key = cipherKey;  // cipher key
            rijndaelInstance.Mode = CipherMode.ECB; // mode of cipher( Electronic Code Block ) ~~ used in Social Survey web application
            rijndaelInstance.Padding = PaddingMode.PKCS7; // default mode of padding ~~ used in Social Survey web application

            return rijndaelInstance;
        }




        // methods to covert hexadecimal string and byte array to each other
        #region hex-string conversion

        private byte[] hexStringToByteArray(string hexString)
        {
            byte[] byteArray = new byte[hexString.Length / 2];
            for (int i = 0; i < hexString.Length; i += 2)
            {
                byteArray[i / 2] = (byte)((Convert.ToInt32(hexString[i].ToString(), 16) << 4) + Convert.ToInt32(hexString[(i + 1)].ToString(), 16));
            }
            return byteArray;
        }

        private String byteArrayToHexString(byte[] byteArray)
        {
            System.Text.StringBuilder hexString = new System.Text.StringBuilder();
            for (int i = 0; i < byteArray.Length; i++)
            {
                byte temp = byteArray[i];
                string s = Convert.ToInt32(temp).ToString("X");
                while (s.Length < 2)
                {
                    s = "0" + s;
                }
                s = s.Substring(s.Length - 2);
                hexString.Append(s);
            }
            return hexString.ToString();
        }

        #endregion





        // methods to convert bytes and Strings to each other using specified code page
        #region byte-string conversion, encoder/decoder

        private string byteEncoder(byte[] data, int encodingTypeCode)
        {
            return System.Text.Encoding.GetEncoding(encodingTypeCode).GetString(data);
        }

        private byte[] byteDecoder(string data, int decodingTypeCode)
        {
            return System.Text.Encoding.GetEncoding(decodingTypeCode).GetBytes(data);
        }

        #endregion




        #endregion


    }

}
