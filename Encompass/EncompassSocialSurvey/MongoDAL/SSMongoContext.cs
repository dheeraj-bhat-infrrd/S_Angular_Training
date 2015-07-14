using MongoDB.Bson.Serialization;
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

        private static string MongoCompanyInfoCollection
        {
            get
            {
                string dbName = "";
                if (false == string.IsNullOrWhiteSpace(System.Configuration.ConfigurationManager.AppSettings["MongoCompanyInfoCollection"]))
                {
                    dbName = System.Configuration.ConfigurationManager.AppSettings["MongoCompanyInfoCollection"];
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

                // Ignore the extra element
                //
                BsonClassMappToIgnoreExtraElement();
                //
                return this.Database.GetCollection<SoicalSurveryCompanyInfo>(SSMongoContext.MongoCompanyInfoCollection);

            }
        }

        /// <summary>
        /// Mongo is schema less, and it works based on parent collection, but in this case we're interested in child object company_info
        /// So it's better the ignore the rest of the fields from based (parent) collection
        /// </summary>
        public static void BsonClassMappToIgnoreExtraElement()
        {
            BsonClassMap.RegisterClassMap<SoicalSurveryCompanyInfo>(map =>
            {
                map.AutoMap();
                map.SetIgnoreExtraElements(true);

                BsonClassMap.RegisterClassMap<CrmInfo>(childMap =>
                {
                    childMap.AutoMap();
                    childMap.SetIgnoreExtraElements(true);
                });
            });
        }
    }

}
