using System;
using System.Globalization;
using System.IO;
using System.Security.Cryptography;
using System.Text;
/* Network */
using System.Net;
using System.Net.Mail;
using SendGrid;

namespace EncompassSocialSurvey
{
    public class CommonUtility
    {
        public static DateTime ConvertStringToDateTime(string inputDate)
        {
            Logger.Info("Entering the method CommonUtility.ConvertStringToDateTime() for date : " + inputDate);
            try
            {
                // "11/30/2015 01:54 PM"
                string dateWithTimeFormat = "MM/dd/yyyy hh:mm tt";

                string dateFormat = "MM/dd/yyyy";

                DateTime dt;

                try
                {
                    dt = DateTime.ParseExact(inputDate, dateWithTimeFormat, CultureInfo.InvariantCulture);
                }
                catch
                {
                    dt = DateTime.ParseExact(inputDate, dateFormat, CultureInfo.InvariantCulture);
                }
                Logger.Debug("Formatted the input date string " + inputDate + " to date : " + dt.ToString());
                Logger.Info("Exiting the method CommonUtility.ConvertStringToDateTime()");
               
                return dt;
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: EncompassGlobal.GetUserLoginSesssion(): ", ex);
                throw ex;
            }

        }

        public static string keyStr = "6f90b8d50f490e647d92e2a74d2c44d7";
        private static string Encrypt(string PlainText)
        {
            RijndaelManaged aes = new RijndaelManaged();
            aes.BlockSize = 128;
            aes.KeySize = 256;

            // It is equal in java 
            /// Cipher _Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");    
            aes.Mode = CipherMode.CBC;
            aes.Padding = PaddingMode.PKCS7;

            byte[] keyArr = Convert.FromBase64String(keyStr);
            byte[] KeyArrBytes32Value = new byte[32];
            Array.Copy(keyArr, KeyArrBytes32Value, 32);

            // Initialization vector.   
            // It could be any value or generated using a random number generator.
            byte[] ivArr = { 1, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2, 1, 7, 7, 7, 7 };
            byte[] IVBytes16Value = new byte[16];
            Array.Copy(ivArr, IVBytes16Value, 16);

            aes.Key = KeyArrBytes32Value;
            aes.IV = IVBytes16Value;

            ICryptoTransform encrypto = aes.CreateEncryptor();

            byte[] plainTextByte = ASCIIEncoding.UTF8.GetBytes(PlainText);
            byte[] CipherText = encrypto.TransformFinalBlock(plainTextByte, 0, plainTextByte.Length);
            return Convert.ToBase64String(CipherText);

        }

        public static string Decrypt(string CipherText)
        {
            RijndaelManaged aes = new RijndaelManaged();
            aes.BlockSize = 128;
            aes.KeySize = 256;

            aes.Mode = CipherMode.CBC;
            aes.Padding = PaddingMode.PKCS7;

            byte[] keyArr = Convert.FromBase64String(keyStr);
            byte[] KeyArrBytes32Value = new byte[32];
            Array.Copy(keyArr, KeyArrBytes32Value, 32);

            // Initialization vector.   
            // It could be any value or generated using a random number generator.
            byte[] ivArr = { 1, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2, 1, 7, 7, 7, 7 };
            byte[] IVBytes16Value = new byte[16];
            Array.Copy(ivArr, IVBytes16Value, 16);

            aes.Key = KeyArrBytes32Value;
            aes.IV = IVBytes16Value;

            ICryptoTransform decrypto = aes.CreateDecryptor();

            byte[] encryptedBytes = Convert.FromBase64CharArray(CipherText.ToCharArray(), 0, CipherText.Length);
            byte[] decryptedData = decrypto.TransformFinalBlock(encryptedBytes, 0, encryptedBytes.Length);
            return ASCIIEncoding.UTF8.GetString(decryptedData);
        }

        public static void SendMailToAdmin(string subject , string body)
        {
            try
            {
                Logger.Info("Sending mail to admin ");
                Logger.Info("Mail Subject is : " + subject);
                Logger.Info("Mail body is : " + body);

                var credentials = new NetworkCredential(EncompassSocialSurveyConfiguration.SendgridUsername, EncompassSocialSurveyConfiguration.SendgridPassword);

                SendGridMessage myMessage = new SendGridMessage();
                myMessage.AddTo(EncompassSocialSurveyConfiguration.AdminEmailAddress);
                myMessage.From = new MailAddress(EncompassSocialSurveyConfiguration.SendgridFromAddress, EncompassSocialSurveyConfiguration.SendgridName);
                myMessage.Subject = subject;
                myMessage.Text = body;
                Logger.Info("Sending mail to admin at : " + EncompassSocialSurveyConfiguration.AdminEmailAddress);
                Logger.Info("message is : " + myMessage.Text);
                var transportWeb = new Web(credentials);
                transportWeb.DeliverAsync(myMessage).Wait(); // wait for sending the mail.
                Logger.Info("mail has been sent successfully to : " + EncompassSocialSurveyConfiguration.AdminEmailAddress);

            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: CommonUtility.sendMailToAdmin(): ", ex);
            }

        }


        public static void SendMailToEmailAdresses(string subject, string body , string[] emailAddresses , string attachmentPath)
        {
            try
            {
                Logger.Info("Sending mail to email address : emailAddresses ");
                Logger.Info("Mail Subject is : " + subject);
                Logger.Info("Mail body is : " + body);

                var credentials = new NetworkCredential(EncompassSocialSurveyConfiguration.SendgridUsername, EncompassSocialSurveyConfiguration.SendgridPassword);

                SendGridMessage myMessage = new SendGridMessage();
                myMessage.AddTo(emailAddresses);
                myMessage.From = new MailAddress(EncompassSocialSurveyConfiguration.SendgridFromAddress, EncompassSocialSurveyConfiguration.SendgridName);
                myMessage.Subject = subject;
                myMessage.Text = body;

                if (attachmentPath != null && !attachmentPath.Equals(""))
                {
                    String[] attachmentList = new string[] { attachmentPath };
                    myMessage.Attachments = attachmentList;
                }
                
                Logger.Info("Sending mail to : " + emailAddresses);
                Logger.Info("message is : " + myMessage.Text);
                var transportWeb = new Web(credentials);
                transportWeb.DeliverAsync(myMessage).Wait(); // wait for sending the mail.
                Logger.Info("mail has been sent successfully to : " + emailAddresses);

            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: CommonUtility.SendMailToEmailAdresses(): ", ex);
                throw ex;
            }

        }

    }
}
