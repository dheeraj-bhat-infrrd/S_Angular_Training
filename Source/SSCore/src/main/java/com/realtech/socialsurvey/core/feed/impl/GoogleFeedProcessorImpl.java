package com.realtech.socialsurvey.core.feed.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.FeedStatus;
import com.realtech.socialsurvey.core.entities.GooglePlusPost;
import com.realtech.socialsurvey.core.entities.GooglePlusSocialPost;
import com.realtech.socialsurvey.core.entities.GoogleToken;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.feed.SocialNetworkDataProcessor;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;


@Component ( "googleFeed")
@Scope ( value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GoogleFeedProcessorImpl implements SocialNetworkDataProcessor<GooglePlusPost, GoogleToken>
{

    private static final Logger LOG = LoggerFactory.getLogger( GoogleFeedProcessorImpl.class );
    private static final String FEED_SOURCE = "google";
    private static final int RETRIES_INITIAL = 0;
    private static final int PAGE_SIZE = 100;

    @Autowired
    private GenericDao<FeedStatus, Long> feedStatusDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private OrganizationUnitSettingsDao settingsDao;

    @Autowired
    private SurveyHandler surveyHandler;

    @Autowired
    private EmailServices emailServices;
    
    @Autowired
    private GenericDao<Region, Long> regionDao;
    
    @Resource
    @Qualifier ( "branch" )
    private BranchDao branchDao;
    
    @Autowired
    private GenericDao<User, Long> userDao;

    @Value ( "${SOCIAL_CONNECT_RETRY_THRESHOLD}")
    private long socialConnectRetryThreshold;

    @Value ( "${SOCIAL_CONNECT_REMINDER_THRESHOLD}")
    private long socialConnectThreshold;

    @Value ( "${SOCIAL_CONNECT_REMINDER_INTERVAL_DAYS}")
    private long socialConnectInterval;

    @Value ( "${GOOGLE_API_KEY}")
    private String googleApiKey;

    @Value ( "${GOOGLE_API_SECRET}")
    private String googleApiSecretKey;

    @Value ( "${GOOGLE_API_SCOPE}")
    private String googleApiScope;

    @Value ( "${GOOGLE_SHARE_URI}")
    private String googleShareURI;

    private FeedStatus status;
    private long profileId;
    private Timestamp lastFetchedTill;
    private String lastFetchedPostId;
    private String nextPageToken;
    private boolean allPostUpdated;
    List<GooglePlusPost> posts = new ArrayList<GooglePlusPost>();


    @Override
    @Transactional
    public void preProcess( long iden, String collection, GoogleToken token )
    {
        List<FeedStatus> statuses = null;
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.FEED_SOURCE_COLUMN, FEED_SOURCE );

        switch ( collection ) {
            case MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION:
                queries.put( CommonConstants.COMPANY_ID_COLUMN, iden );

                statuses = feedStatusDao.findByKeyValue( FeedStatus.class, queries );
                if ( statuses != null && statuses.size() > 0 ) {
                    status = statuses.get( CommonConstants.INITIAL_INDEX );
                }

                if ( status == null ) {
                    status = new FeedStatus();
                    status.setFeedSource( FEED_SOURCE );
                    status.setCompanyId( iden );
                } else {
                    lastFetchedPostId = status.getLastFetchedPostId();
                    lastFetchedTill = status.getLastFetchedTill();
                }
                break;

            case MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION:
                queries.put( CommonConstants.REGION_ID_COLUMN, iden );

                statuses = feedStatusDao.findByKeyValue( FeedStatus.class, queries );
                if ( statuses != null && statuses.size() > 0 ) {
                    status = statuses.get( CommonConstants.INITIAL_INDEX );
                }

                if ( status == null ) {
                    status = new FeedStatus();
                    status.setFeedSource( FEED_SOURCE );
                    status.setRegionId( iden );
                } else {
                    lastFetchedPostId = status.getLastFetchedPostId();
                    lastFetchedTill = status.getLastFetchedTill();
                }
                break;

            case MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION:
                queries.put( CommonConstants.BRANCH_ID_COLUMN, iden );

                statuses = feedStatusDao.findByKeyValue( FeedStatus.class, queries );
                if ( statuses != null && statuses.size() > 0 ) {
                    status = statuses.get( CommonConstants.INITIAL_INDEX );
                }

                if ( status == null ) {
                    status = new FeedStatus();
                    status.setFeedSource( FEED_SOURCE );
                    status.setBranchId( iden );
                } else {
                    lastFetchedPostId = status.getLastFetchedPostId();
                    lastFetchedTill = status.getLastFetchedTill();
                }
                break;

            case MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION:
                queries.put( CommonConstants.AGENT_ID_COLUMN, iden );

                statuses = feedStatusDao.findByKeyValue( FeedStatus.class, queries );
                if ( statuses != null && statuses.size() > 0 ) {
                    status = statuses.get( CommonConstants.INITIAL_INDEX );
                }

                if ( status == null ) {
                    status = new FeedStatus();
                    status.setFeedSource( FEED_SOURCE );
                    status.setAgentId( iden );
                } else {
                    lastFetchedPostId = status.getLastFetchedPostId();
                    lastFetchedTill = status.getLastFetchedTill();
                }
                break;
        }

        profileId = iden;
    }


    @Override
    @Transactional
    public List<GooglePlusPost> fetchFeed( long iden, String collection, GoogleToken token ) throws NonFatalException
    {
        LOG.info( "Getting google posts for " + collection + " with id: " + iden );

        try {
            String accessToken = token.getGoogleAccessToken();

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet( createGooglePlusFeedURL( accessToken ) );
            HttpResponse response = httpClient.execute( getRequest );

            if ( response.getStatusLine().getStatusCode() != 200 ) {
                LOG.error( "Failed : HTTP error code : " + response.getStatusLine().getStatusCode() );

                OAuthRequest request = new OAuthRequest( Verb.POST, "https://accounts.google.com/o/oauth2/token" );
                request.addBodyParameter( "grant_type", "refresh_token" );
                request.addBodyParameter( "refresh_token", token.getGoogleRefreshToken() );
                request.addBodyParameter( "client_id", googleApiKey );
                request.addBodyParameter( "client_secret", googleApiSecretKey );
                Response tokenResponse = request.send();

                if ( tokenResponse.getCode() != 200 ) {
                    throw new IOException( "Google access token expired" );
                }

                Map<String, Object> tokenData = new Gson().fromJson( tokenResponse.getBody(),
                    new TypeToken<Map<String, String>>() {}.getType() );
                if ( tokenData != null ) {
                    accessToken = tokenData.get( "access_token" ).toString();
                }

                getRequest = new HttpGet( createGooglePlusFeedURL( accessToken ) );
                response = httpClient.execute( getRequest );
            }
            status.setRetries( RETRIES_INITIAL );

            InputStreamReader jsonReader = new InputStreamReader( response.getEntity().getContent() );

            try {
                JsonParser jsonParser = new JsonParser();
                JsonObject parentObj = jsonParser.parse( jsonReader ).getAsJsonObject();
                if ( null != parentObj.get( "nextPageToken" ) ) {
                    nextPageToken = parentObj.get( "nextPageToken" ).getAsString();
                } else {
                    nextPageToken = null;
                }

                JsonArray array = (JsonArray) parentObj.get( "items" );
                if ( array != null && array.size() > 0 ) {
                    Timestamp profileUpdatedOn = convertStringToDate( parentObj.get( "updated" ).getAsString() );

                    for ( JsonElement jsonElement : array ) {
                        if ( jsonElement == null ) {
                            continue;
                        }
                        JsonObject items = (JsonObject) jsonElement;
                        if ( items.get( "title" ) == null || items.get( "title" ).getAsString().isEmpty() ) {
                            continue;
                        }

                        Timestamp postCreatedOn = convertStringToDate( items.get( "published" ).getAsString() );
                        JsonObject actor = (JsonObject) items.get( "actor" );
                        if ( items.get( "id" ).getAsString().equalsIgnoreCase( lastFetchedPostId ) ) {
                            allPostUpdated = true;
                            break;
                        }
                        System.out.println( "items:" + items.toString() );
                        GooglePlusPost post = new GooglePlusPost();
                        post.setId( items.get( "id" ).getAsString() );
                        post.setCreatedOn( postCreatedOn );
                        post.setPost( items.get( "title" ).getAsString() );
                        post.setUrl( items.get( "url" ).getAsString() );
                        post.setPostedBy( actor.get( "displayName" ).getAsString() );
                        post.setLastUpdatedOn( profileUpdatedOn );
                        posts.add( post );
                    }

                    if ( posts != null && !posts.isEmpty() ) {
                        lastFetchedPostId = posts.get( 0 ).getId();
                    }

                    while ( nextPageToken != null ) {

                        LOG.info( "Going through all pages until we dont find a next page or the post already exist in mongo" );
                        if ( allPostUpdated ) {
                            break;
                        }
                        fetchFeed( iden, collection, token );
                    }
                }
            } catch ( NonFatalException e ) {
                LOG.error( "Exception in Google feed extration. Reason: " + e.getMessage() );
                throw new NonFatalException( e.getMessage() );
            }
        } catch ( IOException e ) {
            LOG.error( "Exception in Google feed extration. Reason: " + e.getMessage() );

            if ( lastFetchedPostId == null || lastFetchedPostId.isEmpty() ) {
                lastFetchedPostId = "0";
            }

            // setting no.of retries
            status.setRetries( status.getRetries() + 1 );
            status.setLastFetchedPostId( lastFetchedPostId );

            //if last reminder time is null than set is as epoch time
            if(status.getReminderSentOn() == null){
            	SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
            	try {
					Timestamp remimderSentOn = new Timestamp(sdf.parse(CommonConstants.EPOCH_REMINDER_TIME).getTime());
					status.setReminderSentOn( remimderSentOn );
				}
				catch (ParseException pe) {
					pe.printStackTrace();
				}
                
            }
            
            Timestamp timestamp = new Timestamp( System.currentTimeMillis() );
            DateTime currentTime = new DateTime( timestamp.getTime() );
            DateTime sentTime = new DateTime( status.getReminderSentOn().getTime() );
            Days days = Days.daysBetween( sentTime, currentTime );

            // sending reminder mail and increasing counter
            if ( status.getRemindersSent() < socialConnectThreshold && days.getDays() >= socialConnectInterval
                && status.getRetries() >= socialConnectRetryThreshold ) {
                ContactDetailsSettings contactDetailsSettings = settingsDao
                    .fetchOrganizationUnitSettingsById( iden, collection ).getContact_details();
                String userEmail = contactDetailsSettings.getMail_ids().getWork();

                emailServices.sendSocialConnectMail( userEmail, contactDetailsSettings.getName(), userEmail, FEED_SOURCE );

                status.setReminderSentOn( timestamp );
                status.setRemindersSent( status.getRemindersSent() + 1 );
            }

            feedStatusDao.saveOrUpdate( status );
        }

        return posts;
    }


    private String createGooglePlusFeedURL( String accessToken )
    {
        // Add parameters which are required in response fetch results.
        StringBuffer url = new StringBuffer( "https://www.googleapis.com/plus/v1/people/me/activities/public?access_token="
            + accessToken );
        url.append( "&fields=nextPageToken,updated,items(id,title,published,url,actor)" );
        url.append( "&maxResults=" ).append( PAGE_SIZE );

        if ( nextPageToken != null && !nextPageToken.isEmpty() ) {
            url.append( "&pageToken=" ).append( nextPageToken );
        }

        return url.toString();
    }


    private Timestamp convertStringToDate( String dateStr ) throws NonFatalException
    {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );
        try {
            date = format.parse( dateStr );
        } catch ( ParseException e ) {
            throw new NonFatalException( "Unable to parse date : ", e.getMessage() );
        }

        Calendar c = Calendar.getInstance();
        c.setTime( date );
        c.set( Calendar.MILLISECOND, 0 );

        return new Timestamp( c.getTimeInMillis() );
    }


    @Override
    public boolean processFeed( List<GooglePlusPost> posts, String collection ) throws NonFatalException
    {
        LOG.info( "Process tweets for organizationUnit " + collection );
        Date lastFetchedOn = null;
        boolean inserted = false;
        GooglePlusSocialPost socialPost = null;
        for ( GooglePlusPost post : posts ) {
            if ( post.getId().equalsIgnoreCase( status.getLastFetchedPostId() ) ) {
                break;
            }
            if ( lastFetchedTill == null ) {
                socialPost = new GooglePlusSocialPost();
                socialPost.setPost( post );
                lastFetchedOn = post.getCreatedOn();
                socialPost.setPostText( post.getPost() );
                socialPost.setPostedBy( post.getPostedBy() );
                socialPost.setSource( FEED_SOURCE );
                socialPost.setPostId( post.getId() );
                socialPost.setTimeInMillis( post.getCreatedOn().getTime() );
                socialPost.setPostUrl( post.getUrl() );
            }

            if ( lastFetchedTill != null && lastFetchedTill.before( post.getCreatedOn() ) ) {
                socialPost = new GooglePlusSocialPost();
                socialPost.setPost( post );
                lastFetchedOn = post.getCreatedOn();
                socialPost.setPostText( post.getPost() );
                socialPost.setPostedBy( post.getPostedBy() );
                socialPost.setSource( FEED_SOURCE );
                socialPost.setPostId( post.getId() );
                socialPost.setTimeInMillis( post.getCreatedOn().getTime() );
                socialPost.setPostUrl( post.getUrl() );
            }

            if ( socialPost == null )
                break;
            switch ( collection ) {
                case MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION:
                    socialPost.setCompanyId( profileId );
                    break;

                case MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION:
                    Region region = regionDao.findById( Region.class, profileId );
                    if ( region != null ) {
                        Company company = region.getCompany();
                        if ( company !=  null ) {
                            socialPost.setCompanyId( company.getCompanyId() );
                        }
                    }
                    socialPost.setRegionId( profileId );
                    break;

                case MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION:
                    Branch branch = branchDao.findById( Branch.class, profileId );
                    if ( branch != null ) {
                        Company company = branch.getCompany();
                        if ( company != null ) {
                            socialPost.setCompanyId( company.getCompanyId() );
                        }
                    }
                    socialPost.setBranchId( profileId );
                    break;

                case MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION:
                    User user = userDao.findById( User.class, profileId );
                    if ( user != null ) {
                        Company company = user.getCompany();
                        if ( company != null ) {
                            socialPost.setCompanyId( company.getCompanyId() );
                        }
                    }
                    socialPost.setAgentId( profileId );
                    break;
            }

            // pushing to mongo
            mongoTemplate.insert( socialPost, CommonConstants.SOCIAL_POST_COLLECTION );
            inserted = true;
        }

        // updating last fetched details
        if ( lastFetchedOn != null ) {
            lastFetchedTill = new Timestamp( lastFetchedOn.getTime() );
        }
        return inserted;
    }


    @Override
    @Transactional
    public void postProcess( long iden, String collection, boolean anyRecordInserted ) throws NonFatalException
    {
        if ( lastFetchedPostId == null || lastFetchedPostId.isEmpty() ) {
            lastFetchedPostId = "0";
        }

        status.setLastFetchedTill( lastFetchedTill );
        status.setLastFetchedPostId( lastFetchedPostId );
        if ( anyRecordInserted ) {
            if ( collection.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
                surveyHandler.updateModifiedOnColumnForEntity( CommonConstants.COMPANY_ID_COLUMN, profileId );
            } else if ( collection.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {
                surveyHandler.updateModifiedOnColumnForEntity( CommonConstants.REGION_ID_COLUMN, profileId );
            } else if ( collection.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
                surveyHandler.updateModifiedOnColumnForEntity( CommonConstants.BRANCH_ID_COLUMN, profileId );
            } else if ( collection.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
                surveyHandler.updateModifiedOnColumnForEntity( CommonConstants.AGENT_ID_COLUMN, profileId );
            }
        }

        feedStatusDao.saveOrUpdate( status );
    }
}