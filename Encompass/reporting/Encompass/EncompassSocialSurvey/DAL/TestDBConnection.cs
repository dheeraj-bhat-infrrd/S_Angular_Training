using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EncompassSocialSurvey.DAL
{
    public class TestDBConnection
    {
        EncompassSocialSurveryContext _socialSurveryContext = new EncompassSocialSurveryContext();

        public bool GetTableNames()
        {
            Logger.Info("Entering the method TestDBConnection.GetTableNames()");
            bool returnValue = false;

            string sqlQuery = "SELECT tb.TABLE_NAME FROM INFORMATION_SCHEMA.TABLES as tb WHERE tb.TABLE_SCHEMA='ss_user';";

            MySqlCommand commandToSelect = null;
            MySqlDataReader dataReader = null;

             MySqlConnection mySqlDbConnnection = null;

            try
            {
                 mySqlDbConnnection = _socialSurveryContext.DBConnnection;
                commandToSelect = new MySqlCommand(sqlQuery, mySqlDbConnnection);
                
                dataReader = commandToSelect.ExecuteReader();
                while (dataReader.Read())
                {
                    Logger.Info("Table name: " +  dataReader.GetString(0));
                }
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: TestDBConnection.GetTableNames(): ", ex);
                throw ex;
            }
            finally
            {
                if (null != dataReader && dataReader.IsClosed == false) { dataReader.Close(); }
                if (null != dataReader) { dataReader.Dispose(); }
                if (null != commandToSelect) { commandToSelect.Dispose(); }

                 // anyhow close the db connection
                if (mySqlDbConnnection.State == System.Data.ConnectionState.Open)
                {
                    mySqlDbConnnection.Close();
                    mySqlDbConnnection.Dispose();
                }
            }

            Logger.Info("Exiting the method TestDBConnection.GetTableNames()");
            return returnValue;
        }
    }
}
