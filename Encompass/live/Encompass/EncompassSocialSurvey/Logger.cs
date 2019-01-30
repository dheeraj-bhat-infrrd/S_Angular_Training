using log4net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;

namespace EncompassSocialSurvey
{
    public class Logger
    {
        //private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        public static void XmlConfigure()
        {
            log4net.Config.XmlConfigurator.Configure();
        }

        private static readonly ILog Log = LogManager.GetLogger("ApplicationLog");

        private static readonly ILog PFwebServicelogger = LogManager.GetLogger("PathFinderWebserviceLog");

        public static void Debug(string logStatement)
        {
            Log.Debug(logStatement);
        }

        public static void Info(string logStatement)
        {
            Log.Info(logStatement);
        }

        public static void Fatal(string logStatement)
        {
            Log.Fatal(logStatement);
        }

        public static void Error(string logStatement)
        {
            Log.Error(logStatement);
        }

        public static void Error(string logStatement, Exception ex)
        {
            Log.Error(ex.Message, ex);
        }

        public static void Warn(string logStatement)
        {
            Log.Warn(logStatement);
        }

        public static void Pfwsdebug(string logStatement)
        {
            PFwebServicelogger.Debug(logStatement);
        }

        public static void Pfwserror(string logStatement, Exception ex)
        {
            PFwebServicelogger.Error(ex.Message, ex);
        }

        public static void Pfwserror(string logStatement)
        {
            PFwebServicelogger.Error(logStatement);
        }


        public static string ConvertObjextToString(object obj)
        {
            try
            {
                if (PFwebServicelogger.IsDebugEnabled)
                {
                    Logger.Debug("CovertTOxmlMethodCalled");
                    if (obj == null) return "Null Object";
                    XmlDocument doc = new XmlDocument();
                    System.Xml.Serialization.XmlSerializer serializer = new System.Xml.Serialization.XmlSerializer(obj.GetType());
                    System.IO.MemoryStream stream = new System.IO.MemoryStream();
                    try
                    {
                        serializer.Serialize(stream, obj);
                        stream.Position = 0;
                        doc.Load(stream);
                        return doc.InnerXml;
                    }
                    catch (Exception exp)
                    {
                        Logger.Debug("LOW IMPACT: Logger: ConvertObjextToString(): An exception occured while converting Obj to XML");
                        return exp.Message;
                    }
                    finally
                    {
                        stream.Close();
                        stream.Dispose();
                    }
                }
                else
                {
                    return string.Empty;
                }
            }
            catch (Exception exp)
            {
                Logger.Debug("LOW IMPACT: Logger: ConvertObjextToString(): An exception occured while converting Obj to XML");
                return exp.Message;
            }
        }

    }
}
