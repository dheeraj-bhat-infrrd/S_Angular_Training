using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EncompassSocialSurvey
{
    public class EncryptionHelper
    {
        public String getNullSafeString(String s)
        {
            if (s != null)
            {
                return s;
            }
            else
            {
                return "";
            }
        }

        //public byte[] hexStringToByteArray(String hexString)
        //{

        //    if (hexString == null)
        //    {
        //        Logger.Error("Null parameter passed to hexStringToByteArray of EncryptionHelper!");
        //        throw new InvalidOperationException("Null parameter passed to hexStringToByteArray of EncryptionHelper!");
        //    }

        //    Logger.Info(" hexStringToByteArray() : input parameter : " + hexString);

        //    int len = hexString.Length;
        //    byte[] byteArray = new byte[len / 2];
        //    for (int i = 0; i < len; i += 2)
        //    {
        //        //byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        //        byteArray[i / 2] = (byte)((Character.digit(hexString[i], 16) << 4) + Character.digit(hexString[i + 1], 16));
        //    }

        //    Logger.Info(" hexStringToByteArray() : output : " + byteArray.ToString());
        //    return byteArray;
        //}

        public static byte[] hexStringToByteArray(string hex)
        {
            if (hex.Length % 2 == 1)
                throw new Exception("The binary key cannot have an odd number of digits");

            byte[] arr = new byte[hex.Length >> 1];

            for (int i = 0; i < hex.Length >> 1; ++i)
            {
                arr[i] = (byte)((GetHexVal(hex[i << 1]) << 4) + (GetHexVal(hex[(i << 1) + 1])));
            }

            return arr;
        }

        public static int GetHexVal(char hex)
        {
            int val = (int)hex;
            //For uppercase A-F letters:
            return val - (val < 58 ? 48 : 55);
            //For lowercase a-f letters:
            //return val - (val < 58 ? 48 : 87);
            //Or the two combined, but a bit slower:
            //return val - (val < 58 ? 48 : (val < 97 ? 55 : 87));
        }

        //
        //    public String byteArrayToHexString(byte[] byteArray) throws InvalidInputException {

        //    if (byteArray == null) {
        //        LOG.error("Null parameter passed to byteArrayToHexString of EncryptionHelper!");
        //        throw new InvalidInputException("Null parameter passed to byteArrayToHexString of EncryptionHelper!");
        //    }

        //    StringBuffer hexString = new StringBuffer();
        //    for (int i = 0; i < byteArray.length; i++) {
        //        byte temp = byteArray[i];

        //        String s = Integer.toHexString(Byte.valueOf(temp));

        //        while (s.length() < 2) {
        //            s = "0" + s;
        //        }
        //        s = s.substring(s.length() - 2);
        //        hexString.append(s);
        //    }
        //    String hexStr = hexString.toString();
        //    LOG.info("byteArrayToHexString() Output : " + hexStr);
        //    return hexStr;
        //}


        public static string byteArrayToHexString(byte[] Bytes)
        {
            StringBuilder Result = new StringBuilder(Bytes.Length * 2);
            string HexAlphabet = "0123456789ABCDEF";

            foreach (byte B in Bytes)
            {
                Result.Append(HexAlphabet[(int)(B >> 4)]);
                Result.Append(HexAlphabet[(int)(B & 0xF)]);
            }

            return Result.ToString();
        }

        //public SecretKeySpec generateAES256Key(String plainTextKey) throws InvalidInputException {

        //        if (plainTextKey == null) {
        //            LOG.error("Null parameter passed to generateAES256Key of EncryptionHelper!");
        //            throw new InvalidInputException("Null parameter passed to generateAES256Key of EncryptionHelper!");
        //        }

        //        LOG.info("generateAES256Key() :  input parameter : " + plainTextKey);

        //        byte[] key;
        //        // Random salt
        //        String salt = "6f90b8d50f490e647d92e2a74d2c44d7";
        //        try {
        //            MessageDigest md = MessageDigest.getInstance("SHA-256");
        //            md.update((getNullSafeString(plainTextKey) + salt).getBytes());
        //            key = md.digest();
        //        }
        //        catch (NoSuchAlgorithmException ex) {
        //            // SHA-256 hash key
        //            key = hexStringToByteArray("43c2fbc4e027b47c3d8eaff48f1bcb4fa3ecbe0585b1993e5f92b0b07b92eebb");
        //        }
        //        return new SecretKeySpec(key, "AES");
        //    }

    }
}
