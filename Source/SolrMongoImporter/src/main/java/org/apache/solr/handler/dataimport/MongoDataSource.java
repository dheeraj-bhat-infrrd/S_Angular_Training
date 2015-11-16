package org.apache.solr.handler.dataimport;


import static org.apache.solr.handler.dataimport.DataImportHandlerException.SEVERE;
import static org.apache.solr.handler.dataimport.DataImportHandlerException.wrapAndThrow;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.ReadPreference;
import com.mongodb.util.JSON;


public class MongoDataSource extends DataSource<Iterator<Map<String, Object>>>
{

    private static final Logger LOG = LoggerFactory.getLogger( MongoDataSource.class );

    private DBCollection mongoCollection;
    private DB mongoDb;
    private Mongo mongoConnection;

    private DBCursor mongoCursor;


    //private MongoTemplate mongoTemplate;
    @SuppressWarnings ( "deprecation")
    @Override
    public void init( Context context, Properties initProps )
    {
        /*@SuppressWarnings({ "resource" }) ApplicationContext springContext = new ClassPathXmlApplicationContext("spring-config.xml");
        LOG.info( "Application Context : " + springContext );
        LOG.info( "MT : " + springContext.getBean( "mongoTemplate" ) );
        mongoTemplate = (MongoTemplate) springContext.getBean( "mongoTemplate" );
        LOG.info( "MongoTemplate : " + mongoTemplate );
        this.mongoDb = mongoTemplate.getDb();*/
        String databaseName = initProps.getProperty( DATABASE );
        /*String host           = initProps.getProperty( HOST, "localhost" );
        String port           = initProps.getProperty( PORT, "27017"     );*/
        String url = initProps.getProperty( URL, DEFAULT_URL );
        LOG.info( "DB: " + databaseName + " URL : " + url );
        MongoClientURI mongoURI = new MongoClientURI( url );
        String username = initProps.getProperty( USERNAME );
        String password = initProps.getProperty( PASSWORD );

        if ( databaseName == null ) {
            throw new DataImportHandlerException( SEVERE, "Database must be supplied" );
        }

        try {
            MongoClient mongo = new MongoClient( mongoURI );
            mongo.setReadPreference( ReadPreference.secondaryPreferred() );
            //this.mongoTemplate = new MongoTemplate( mongo, databaseName );
            this.mongoConnection = mongo;
            this.mongoDb = mongo.getDB( databaseName );

            if ( username != null ) {
                if ( this.mongoDb.authenticate( username, password.toCharArray() ) == false ) {
                    throw new DataImportHandlerException( SEVERE, "Mongo Authentication Failed" );
                }
            }

        } catch ( UnknownHostException e ) {
            throw new DataImportHandlerException( SEVERE, "Unable to connect to Mongo" );
        }
        LOG.info( "DataSource Init finished" );
    }


    @Override
    public Iterator<Map<String, Object>> getData( String query )
    {

        DBObject queryObject = (DBObject) JSON.parse( query );
        LOG.debug( "Executing MongoQuery: " + query.toString() );

        long start = System.currentTimeMillis();
        mongoCursor = this.mongoCollection.find( queryObject );
        LOG.trace( "Time taken for mongo :" + ( System.currentTimeMillis() - start ) );

        ResultSetIterator resultSet = new ResultSetIterator( mongoCursor );
        return resultSet.getIterator();
    }


    public Iterator<Map<String, Object>> getData( String query, String collection )
    {
        //this.mongoCollection = this.mongoTemplate.getCollection( collection );
        this.mongoCollection = this.mongoDb.getCollection( collection );
        return getData( query );
    }


    private class ResultSetIterator
    {
        DBCursor MongoCursor;

        Iterator<Map<String, Object>> rSetIterator;


        public ResultSetIterator( DBCursor MongoCursor )
        {
            this.MongoCursor = MongoCursor;


            rSetIterator = new Iterator<Map<String, Object>>() {
                public boolean hasNext()
                {
                    return hasnext();
                }


                public Map<String, Object> next()
                {
                    return getARow();
                }


                public void remove()
                {/* do nothing */
                }
            };


        }


        public Iterator<Map<String, Object>> getIterator()
        {
            return rSetIterator;
        }


        private Map<String, Object> getARow()
        {
            DBObject mongoObject = getMongoCursor().next();

            Map<String, Object> result = new HashMap<String, Object>();
            Set<String> keys = mongoObject.keySet();
            Iterator<String> iterator = keys.iterator();


            while ( iterator.hasNext() ) {
                String key = iterator.next();
                Object innerObject = mongoObject.get( key );

                result.put( key, innerObject );
            }

            return result;
        }


        private boolean hasnext()
        {
            if ( MongoCursor == null )
                return false;
            try {
                if ( MongoCursor.hasNext() ) {
                    return true;
                } else {
                    close();
                    return false;
                }
            } catch ( MongoException e ) {
                close();
                wrapAndThrow( SEVERE, e );
                return false;
            }
        }


        private void close()
        {
            try {
                if ( MongoCursor != null )
                    MongoCursor.close();
            } catch ( Exception e ) {
                LOG.warn( "Exception while closing result set", e );
            } finally {
                MongoCursor = null;
            }
        }
    }


    private DBCursor getMongoCursor()
    {
        return this.mongoCursor;
    }


    @Override
    public void close()
    {
        if ( this.mongoCursor != null ) {
            this.mongoCursor.close();
        }

        if ( this.mongoConnection != null ) {
            this.mongoConnection.close();
        }
    }


    public static final String DATABASE = "database";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String URL = "url";
    public static final String DEFAULT_URL = "mongodb://localhost:27017";

}
