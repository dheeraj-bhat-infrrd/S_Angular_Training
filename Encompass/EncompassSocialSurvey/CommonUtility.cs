using System;
using System.Globalization;

namespace EncompassSocialSurvey
{
    public class CommonUtility
    {
        public static DateTime ConvertStringToDateTime(string inputDate)
        {
            Logger.Info("Entering the method CommonUtility.ConvertStringToDateTime()");
            try
            {
                DateTime dt = DateTime.ParseExact(inputDate, "MM/dd/yyyy", CultureInfo.InvariantCulture);

                Logger.Info("Exiting the method CommonUtility.ConvertStringToDateTime()");
                return dt;
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: EncompassGlobal.GetUserLoginSesssion(): ", ex);
                throw ex;
            }

        }
    }
}
