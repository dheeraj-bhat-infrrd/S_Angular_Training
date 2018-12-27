/**
 * 
 */
package com.realtech.socialsurvey.compute.topology.spouts;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.mybusiness.v4.MyBusiness;
import com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao;
import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import com.realtech.socialsurvey.compute.topology.bolts.reviews.GoogleAuthorizationBolt;

/**
 * @author Subhrajit
 *
 */
public class GoogleAuthorizationSpout extends BaseComputeSpout
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(GoogleAuthorizationBolt.class);
    
    SpoutOutputCollector _collector;
    private RedisSocialMediaStateDao redisSocialMediaStateDao;
    
    
    private static final String APPLICATION_NAME =
        "SocialSurvey Google My Business API";
    private static final java.io.File DATA_STORE_DIR =
        new java.io.File(System.getProperty("user.home"),
            ".store/mybusiness_credentials");
    private static FileDataStoreFactory dataStoreFactory;
    private static HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();
    
    private static final String SCOPE = "https://www.googleapis.com/auth/plus.business.manage";
    private static final String ACCESS_TYPE = "offline";
    
    @Override
    public void open( @SuppressWarnings ( "rawtypes") Map conf, TopologyContext context, SpoutOutputCollector collector )
    {
        super.open( conf, context, collector );
        this._collector = collector;
        this.redisSocialMediaStateDao = new RedisSocialMediaStateDaoImpl();
    }


    /* (non-Javadoc)
     * @see org.apache.storm.spout.ISpout#nextTuple()
     */
    @Override
    public void nextTuple()
    {
        try {
         // Check every 5 second for waitForNextFetch
            Utils.sleep( 5000 );
            if ( !redisSocialMediaStateDao.waitForNextGoogleReviewFetch() ) {
                httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                dataStoreFactory = new FileDataStoreFactory( DATA_STORE_DIR );

                Credential credential = authorize();
                MyBusiness mybusiness = new MyBusiness.Builder( httpTransport, JSON_FACTORY, credential )
                    .setApplicationName( APPLICATION_NAME ).build();
                _collector.emit( new Values( mybusiness ) );
            }
        } catch ( Exception e ) {
            LOG.error( "Failed to authorize google api.", e );
        }
    }
    
    private static Credential authorize() throws Exception {
        // Creates an InputStream to hold the client ID and secret.
        InputStream secrets = GoogleAuthorizationSpout.class.getClassLoader().getResourceAsStream("client_secrets.json");

        // Prompts the user if no credential is found.
        if (secrets == null) {
            LOG.error("Enter Client ID and Secret from Google API Console into google-my-business-api-sample/src/main/resources/client_secrets.json");
            System.exit(1);
        }

        // Uses the InputStream to create an instance of GoogleClientSecrets.
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
            new InputStreamReader(secrets));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
            || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            LOG.error("Enter Client ID and Secret from Google API Console into google-my-business-api-sample/src/main/resources/client_secrets.json");
            System.exit(1);
        }

        // Sets up the authorization code flow.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            httpTransport, JSON_FACTORY, clientSecrets,
            Collections.singleton(SCOPE))
            .setDataStoreFactory(dataStoreFactory)
            .setAccessType( ACCESS_TYPE )
            .setApprovalPrompt( "force" )
            .build();
        // Returns the credential.
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }


    /* (non-Javadoc)
     * @see org.apache.storm.topology.IComponent#declareOutputFields(org.apache.storm.topology.OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields( OutputFieldsDeclarer outputFieldsDeclarer )
    {
        outputFieldsDeclarer.declare( new Fields( "myBusiness" ) );

    }

}
