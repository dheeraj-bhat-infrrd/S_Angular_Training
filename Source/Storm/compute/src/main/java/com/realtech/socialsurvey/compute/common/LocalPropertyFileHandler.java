package com.realtech.socialsurvey.compute.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Property file handler for local JVM
 * @author nishit
 *
 */
public class LocalPropertyFileHandler
{

    private static final Logger LOG = LoggerFactory.getLogger( LocalPropertyFileHandler.class );

    private static LocalPropertyFileHandler localPropertyFileHandler;

    private static Map<String, Properties> propertieMap;

    private static String envProfile;

    private static boolean isPrepared = false;


    private LocalPropertyFileHandler()
    {}


    public static synchronized LocalPropertyFileHandler getInstance()
    {
        if ( localPropertyFileHandler == null ) {
            localPropertyFileHandler = new LocalPropertyFileHandler();

        }
        return localPropertyFileHandler;
    }


    public void prepare( String profile )
    {
        if ( !isPrepared ) {
            synchronized ( LocalPropertyFileHandler.class ) {
                if ( !isPrepared ) {
                    propertieMap = new HashMap<>();
                    envProfile = profile;
                    isPrepared = true;
                }
            }
        }
    }


    /**
     * Gets the value of the key from the property file
     * @param propFile
     * @param key
     * @return
     */
    public Optional<String> getProperty( String propFile, String key )
    {
        if ( !propertieMap.containsKey( propFile ) ) {
            synchronized ( propertieMap ) {
                if ( !propertieMap.containsKey( propFile ) ) {
                    try {
                        propertieMap.put( propFile, loadPropertyFile( propFile ) );
                    } catch ( IOException e ) {
                        LOG.error( "Could not find file {}.properties", propFile, e );
                        return Optional.empty();
                    }
                }
            }
        }
        String value = propertieMap.get( propFile ).getProperty( key );
        if ( value != null && !value.isEmpty() ) {
            return Optional.of( value );
        } else {
            return Optional.empty();
        }
    }


    private Properties loadPropertyFile( String propFile ) throws IOException
    {
        InputStream inputStream = LocalPropertyFileHandler.class.getClassLoader()
            .getResourceAsStream( propFile + ".properties" );
        Properties prop = new Properties();
        if ( inputStream != null ) {
            prop.load( inputStream );
        }
        // load the properties for the profile
        inputStream = LocalPropertyFileHandler.class.getClassLoader()
            .getResourceAsStream( propFile + "-" + envProfile + ".properties" );
        if ( inputStream != null ) {
            prop.load( inputStream );
        }
        if ( prop.isEmpty() ) {
            throw new FileNotFoundException(
                "Property file " + propFile + ".properties or " + propFile + "-" + envProfile + ".properties" + " not found" );
        }
        return prop;
    }


    public String getProfile()
    {
        return envProfile;
    }

}
