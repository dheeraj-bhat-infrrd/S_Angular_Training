package com.realtech.socialsurvey.compute.common;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;


/**
 * Mongo database operation
 * @author nishit
 *
 */
public class MongoDB
{
    private static final Logger LOG = LoggerFactory.getLogger( MongoDB.class );
    private static final String DB_NAME = ComputeConstants.STREAM_DATABASE;
    private static Datastore datastore;

    static {
        String mongoUri = LocalPropertyFileHandler.getInstance()
            .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.MONGO_DB_URI ).orElse( null );
        if ( mongoUri != null ) {
            MongoClient mongoClient = new MongoClient( new MongoClientURI( mongoUri ) );
            Morphia morphia = new Morphia();
            morphia.mapPackage( "com.realtech.socialsurvey.compute.entities" );
            datastore = morphia.createDatastore( mongoClient, DB_NAME );
        } else {
            LOG.warn( "Could not get mongoUri value" );
        }
    }


    /**
     * Returns morphia datastore
     * @return
     */
    public Datastore datastore()
    {
        return datastore;
    }
}
