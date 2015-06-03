using MongoDB.Driver;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EncompassSocialSurvey.MongoDAL
{
    public class SSMongoContext
    {
        public IMongoDatabase Database;

        private static string MongoConnectionString
        {
            get
            {
                string connString = "";
                if (null != System.Configuration.ConfigurationManager.ConnectionStrings["MongoDBSocialSurvey"])
                {
                    connString = System.Configuration.ConfigurationManager.ConnectionStrings["MongoDBSocialSurvey"].ConnectionString.ToString();
                }

                //
                return connString;
            }
        }

        private static string MongoDatabaseName
        {
            get
            {
                string dbName = "";
                if (false == string.IsNullOrWhiteSpace(System.Configuration.ConfigurationManager.AppSettings["MongoDatabaseName"]))
                {
                    dbName = System.Configuration.ConfigurationManager.AppSettings["MongoDatabaseName"];
                }

                return dbName;
            }
        }

        public SSMongoContext()
        {
            //var client = new MongoClient(SSMongoContext.MongoConnectionString);
            //var server = client.GetServer();
            //Database = server.GetDatabase(SSMongoContext.MongoDatabaseName);

            //
            var newClient = new MongoClient(SSMongoContext.MongoConnectionString);
            this.Database = newClient.GetDatabase(SSMongoContext.MongoDatabaseName);
        }

        public IMongoCollection<SoicalSurveryCompanyInfo> SoicalSurveryCompanyInfos
        {
            get
            {
                return this.Database.GetCollection<SoicalSurveryCompanyInfo>("company_settings");
            }
        }
    }

}
