package com.realtech.socialsurvey.compute.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.exception.RuntimeParamParsingException;


/**
 * Functionality on environment constants
 * @author nishit
 *
 */
public class EnvConstants
{

    private static final Logger LOG = LoggerFactory.getLogger( EnvConstants.class );
    
    public static final String PROFILE = "profile";
    public static final String CLUSTER = "cluster";
    
    public static final String SUBMIT_TOPOLOGY = "submit-topo";
    public static final String LOCAL_TOPOLOGY = "local-topo";
    
    public static final String PROFILE_DEV = "dev";
    public static final String PROFILE_DEMO = "demo";
    public static final String PROFILE_STAGE = "stage";
    public static final String PROFILE_PROD = "prod";
    
    private static Map<String, String> runtimeParamsHashmap = new HashMap<>();

    private static final List<String> AVAILABLE_PROFILES = Arrays.asList( PROFILE_DEV, PROFILE_DEMO, PROFILE_STAGE, PROFILE_PROD );


    private EnvConstants()
    {}


    /**
     * Gets the send grid for .me domain from environment. If not found then returns the value from system property
     * @return
     */
    public static Optional<String> sendGridMeApiKeys()
    {
        String value = null;
        value = System.getenv( ComputeConstants.SENDGRID_ME_API_KEY ) != null
            ? System.getenv( ComputeConstants.SENDGRID_ME_API_KEY )
            : System.getProperty( ComputeConstants.SENDGRID_ME_API_KEY );
        if ( value != null ) {
            LOG.info( "Found Sendgrid ME Api key. Applying the key." );
            return Optional.of( value );
        } else {
            LOG.warn( "Could not find Sendgrid ME Api key. This could be potentially cause problems." );
            return Optional.empty();
        }
    }


    /**
     * Gets the send grid for .us domain from environment. If not found then returns the value from system property
     * @return
     */
    public static Optional<String> sendGridUsApiKeys()
    {
        String value = null;
        value = System.getenv( ComputeConstants.SENDGRID_US_API_KEY ) != null
            ? System.getenv( ComputeConstants.SENDGRID_US_API_KEY )
            : System.getProperty( ComputeConstants.SENDGRID_US_API_KEY );
        if ( value != null ) {
            LOG.info( "Found Sendgrid US Api key. Applying the key." );
            return Optional.of( value );
        } else {
            LOG.warn( "Could not find Sendgrid US Api key. This could be potentially cause problems." );
            return Optional.empty();
        }
    }

    /**
     * Gets the amazon access key from environment. If not found then returns the value from system property
     * @return
     */
    public static Optional<String> amazonAccessKey()
    {
        String value = null;
        value = System.getenv( ComputeConstants.AMAZON_ACCESS_KEY ) != null
            ? System.getenv( ComputeConstants.AMAZON_ACCESS_KEY )
            : System.getProperty( ComputeConstants.AMAZON_ACCESS_KEY );
        if ( value != null ) {
            LOG.info( "Found Amazon Access key. Applying the key." );
            return Optional.of( value );
        } else {
            LOG.warn( "Could not find  Amazon Access key. This could be potentially cause problems." );
            return Optional.empty();
        }
    }

    /**
     * Gets the amazon secret key from environment. If not found then returns the value from system property
     * @return
     */
    public static Optional<String> amazonSecretKey()
    {
        String value = null;
        value = System.getenv( ComputeConstants.AMAZON_SECRET_KEY ) != null
            ? System.getenv( ComputeConstants.AMAZON_SECRET_KEY )
            : System.getProperty( ComputeConstants.AMAZON_SECRET_KEY );
        if ( value != null ) {
            LOG.info( "Found Amazon Secret key. Applying the key." );
            return Optional.of( value );
        } else {
            LOG.warn( "Could not find Amazon Secret key. This could be potentially cause problems." );
            return Optional.empty();
        }
    }

    /**
     * Gets the twitter consumer key from environment. If not found then returns the value from system property
     * @return
     */
    public static Optional<String> twitterConsumerKey() {
        String consumerKey = System.getenv( ComputeConstants.TWITTER_CONSUMER_KEY ) != null
                ? System.getenv( ComputeConstants.TWITTER_CONSUMER_KEY )
                : System.getProperty( ComputeConstants.TWITTER_CONSUMER_KEY );
        if ( consumerKey != null ) {
            LOG.info( "Found Twitter consumer key. Applying the key." );
            return Optional.of( consumerKey );
        } else {
            LOG.warn( "Could not find Twitter consumer key. This could potentially cause problems." );
            return Optional.empty();
        }
    }

    /**
     * Gets the twitter consumer secret from environment. If not found then returns the value from system property
     * @return
     */
    public static Optional<String> twitterConsumerSecret() {
        String consumerSecret = System.getenv( ComputeConstants.TWITTER_CONSUMER_SECRET ) != null
                ? System.getenv( ComputeConstants.TWITTER_CONSUMER_SECRET )
                : System.getProperty( ComputeConstants.TWITTER_CONSUMER_SECRET );
        if ( consumerSecret != null ) {
            LOG.info( "Found Twitter consumer secret. Applying the key." );
            return Optional.of( consumerSecret );
        } else {
            LOG.warn( "Could not find Twitter consumer secret. This could potentially cause problems." );
            return Optional.empty();
        }
    }

    public static String getProfile()
    {
        return runtimeParamsHashmap.get( PROFILE );
    }


    public static String getCluster()
    {
        return runtimeParamsHashmap.get( CLUSTER );
    }


    public static void runtimeParams( String... params )
    {
        if ( runtimeParamsHashmap.isEmpty() ) {
            if ( params.length > 0 ) {
                // first param should be environment
                if(!AVAILABLE_PROFILES.contains(params[0])){
                    LOG.warn( "Invalid profile passed {}", params[0] );
                    throw new RuntimeParamParsingException( "Invalid profile "+params[0] ) ;
                }
                runtimeParamsHashmap.put( PROFILE, params[0] );
                // Second param should be for cluster deployment
                if(params.length > 1) {
                    if(params[1].equals( SUBMIT_TOPOLOGY )) {
                        runtimeParamsHashmap.put(CLUSTER, SUBMIT_TOPOLOGY);
                    }else {
                        runtimeParamsHashmap.put(CLUSTER, LOCAL_TOPOLOGY);
                    }
                }else {
                    runtimeParamsHashmap.put(CLUSTER, LOCAL_TOPOLOGY);
                }
            }else {
                runtimeParamsHashmap.put( PROFILE, PROFILE_DEV );
                runtimeParamsHashmap.put( CLUSTER, LOCAL_TOPOLOGY );
            }
        }
    }
    
    public static Map<String, String> getRuntimeParamsMap(){
        return runtimeParamsHashmap;
    }
}
