using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EncompassSocialSurvey.DAL
{
    public class EncompassSocialSurveryContext
    {
        private static string ConnectionString
        {
            get
            {
                string connString = "";
                if (null != System.Configuration.ConfigurationManager.ConnectionStrings["EncompassSocialSurvey"])
                {
                    connString = System.Configuration.ConfigurationManager.ConnectionStrings["EncompassSocialSurvey"].ConnectionString.ToString();
                }
                
                //
                return connString;
            }
        }

        private MySqlConnection _dbConnection = null;
        public MySqlConnection DBConnnection
        {
            get
            {
                // if null, create new connection
                if (null == this._dbConnection)
                    this._dbConnection = new MySqlConnection(EncompassSocialSurveryContext.ConnectionString);

                // if closed open new connnection
                if(this._dbConnection.State == System.Data.ConnectionState.Closed)
                     this._dbConnection.Open();

                //
                return this._dbConnection;
            }
        }


    }
}
