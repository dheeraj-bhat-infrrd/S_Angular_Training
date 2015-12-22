package com.realtech.socialsurvey.core.feed.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.joda.time.Days;
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

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.FacebookSocialPost;
import com.realtech.socialsurvey.core.entities.FacebookToken;
import com.realtech.socialsurvey.core.entities.FeedStatus;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.feed.SocialNetworkDataProcessor;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;


@Component ( "facebookFeed")
@Scope ( value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FacebookFeedProcessorImpl implements SocialNetworkDataProcessor<Post, FacebookToken>
{

    private static final Logger LOG = LoggerFactory.getLogger( FacebookFeedProcessorImpl.class );
    private static final String FEED_SOURCE = "facebook";
    private static final int RETRIES_INITIAL = 0;
    private static final int PAGE_SIZE = 200;

    @Autowired
    private GenericDao<FeedStatus, Long> feedStatusDao;
    
    @Autowired
    private GenericDao<Region, Long> regionDao;
    
    @Resource
    @Qualifier ( "branch" )
    private BranchDao branchDao;
    
    @Autowired
    private GenericDao<User, Long> userDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private OrganizationUnitSettingsDao settingsDao;

    @Autowired
    private SurveyHandler surveyHandler;

    @Autowired
    private EmailServices emailServices;

    @Value ( "${SOCIAL_CONNECT_RETRY_THRESHOLD}")
    private long socialConnectRetryThreshold;

    @Value ( "${SOCIAL_CONNECT_REMINDER_THRESHOLD}")
    private long socialConnectThreshold;

    @Value ( "${SOCIAL_CONNECT_REMINDER_INTERVAL_DAYS}")
    private long socialConnectInterval;

    @Value ( "${FB_CLIENT_ID}")
    private String facebookClientId;

    @Value ( "${FB_CLIENT_SECRET}")
    private String facebookClientSecret;

    @Value ( "${FB_URI}")
    private String facebookUri;

    private FeedStatus status;
    private Date lastFetchedTill;
    private String lastFetchedPostId = "";


    @Override
    @Transactional
    public void preProcess( long iden, String collection, FacebookToken token )
    {
    	LOG.debug("Processing: "+iden+" for collection "+collection+" with token "+token.getFacebookAccessTokenToPost());
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
                    lastFetchedTill = new Date( status.getLastFetchedTill().getTime() );
                    lastFetchedPostId = status.getLastFetchedPostId();
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
                    lastFetchedTill = new Date( status.getLastFetchedTill().getTime() );
                    lastFetchedPostId = status.getLastFetchedPostId();
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
                    lastFetchedTill = new Date( status.getLastFetchedTill().getTime() );
                    lastFetchedPostId = status.getLastFetchedPostId();
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
                    lastFetchedTill = new Date( status.getLastFetchedTill().getTime() );
                    lastFetchedPostId = status.getLastFetchedPostId();
                }
                break;
        }
    }


    @Override
    @Transactional
    public List<Post> fetchFeed( long iden, String collection, FacebookToken token ) throws NonFatalException
    {
        LOG.info( "Getting posts for " + collection + " with id: " + iden+" with token "+token.getFacebookAccessTokenToPost() );

        // Settings Consumer and Access Tokens
        Facebook facebook = new FacebookFactory().getInstance();
        facebook.setOAuthAppId( facebookClientId, facebookClientSecret );
        facebook.setOAuthAccessToken( new AccessToken( token.getFacebookAccessTokenToPost() ) );

        // building query to fetch
        List<Post> posts = new ArrayList<Post>();
        try {
            ResponseList<Post> resultList;
            if ( lastFetchedTill != null ) {
                Calendar calender = Calendar.getInstance();
                calender.setTimeInMillis( lastFetchedTill.getTime() );
                calender.add( Calendar.SECOND, 1 );
                Date lastFetchedTillWithoneSecChange = calender.getTime();
                resultList = facebook.getPosts( new Reading().limit( PAGE_SIZE ).since( lastFetchedTillWithoneSecChange ) );
            } else {
                resultList = facebook.getPosts( new Reading().limit( PAGE_SIZE ) );
            }
            posts.addAll( resultList );

            while ( resultList.getPaging() != null && resultList.getPaging().getNext() != null ) {
                resultList = facebook.fetchNext( resultList.getPaging() );
                posts.addAll( resultList );
            }
            if(posts != null && posts.size() > 0){
            	LOG.debug("Post for id "+iden+" and posted by: "+posts.get(CommonConstants.INITIAL_INDEX).getFrom().getName());
            }
            status.setRetries( RETRIES_INITIAL );
        } catch ( FacebookException e ) {
            LOG.error( "Exception in Facebook feed extration. Reason: " + e.getMessage() );

            if ( lastFetchedPostId == null || lastFetchedPostId.isEmpty() ) {
                lastFetchedPostId = "0";
            }

            // increasing no.of retries
            status.setRetries( status.getRetries() + 1 );
            status.setLastFetchedPostId( lastFetchedPostId );

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


    @Override
    public boolean processFeed( long iden, List<Post> posts, String collection, FacebookToken token ) throws NonFatalException
    {
        LOG.info( "Process posts for organizationUnit " + collection +" and iden "+iden );
        if ( posts == null || posts.isEmpty() ) {
            return false;
        }

        if ( lastFetchedTill == null ) {
            lastFetchedTill = posts.get( 0 ).getUpdatedTime();
        }

        FacebookSocialPost feed;
        boolean inserted = false;
        for ( Post post : posts ) {

            //skip the post if it contains no message.
            if ( post.getMessage() == null || post.getMessage() == "" )
                continue;

            if ( lastFetchedTill.before( post.getUpdatedTime() ) ) {
                lastFetchedTill = post.getUpdatedTime();
            }

            feed = new FacebookSocialPost();
            feed.setPost( post );

            feed.setPostText( post.getMessage() );

            feed.setSource( FEED_SOURCE );
            feed.setPostId( post.getId() );
            feed.setPostedBy( post.getFrom().getName() );
            feed.setTimeInMillis( post.getUpdatedTime().getTime() );
            feed.setPostUrl( facebookUri.concat( post.getId() ) );
            feed.setToken(token.getFacebookAccessTokenToPost());
            switch ( collection ) {
                case MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION:
                    feed.setCompanyId( iden );
                    break;

                case MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION:
                    feed.setRegionId( iden );
                    Region region = regionDao.findById( Region.class, iden );
                    if ( region != null ) {
                        Company company = region.getCompany();
                        if ( company !=  null ) {
                            feed.setCompanyId( company.getCompanyId() );
                        }
                    }
                    break;

                case MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION:
                    Branch branch = branchDao.findById( Branch.class, iden );
                    if ( branch != null ) {
                        Company company = branch.getCompany();
                        if ( company != null ) {
                            feed.setCompanyId( company.getCompanyId() );
                        }
                    }
                    feed.setBranchId( iden );
                    break;

                case MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION:
                    User user = userDao.findById( User.class, iden );
                    if ( user != null ) {
                        Company company = user.getCompany();
                        if ( company != null ) {
                            feed.setCompanyId( company.getCompanyId() );
                        }
                    }
                    feed.setAgentId( iden );
                    break;
            }

            lastFetchedPostId = post.getId();

            // pushing to mongo
            LOG.debug("Posting for id "+iden+" for collection "+collection+" with posted by "+feed.getPostedBy());
            mongoTemplate.insert( feed, CommonConstants.SOCIAL_POST_COLLECTION );
            inserted = true;
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
        if ( lastFetchedTill != null ) {
            status.setLastFetchedTill( new Timestamp( lastFetchedTill.getTime() ) );
        }

        status.setLastFetchedPostId( lastFetchedPostId );

        if ( anyRecordInserted ) {
            if ( collection.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
                surveyHandler.updateModifiedOnColumnForEntity( CommonConstants.COMPANY_ID_COLUMN, iden );
            } else if ( collection.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {
                surveyHandler.updateModifiedOnColumnForEntity( CommonConstants.REGION_ID_COLUMN, iden );
            } else if ( collection.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
                surveyHandler.updateModifiedOnColumnForEntity( CommonConstants.BRANCH_ID_COLUMN, iden );
            } else if ( collection.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
                surveyHandler.updateModifiedOnColumnForEntity( CommonConstants.AGENT_ID_COLUMN, iden );
            }
        }

        feedStatusDao.saveOrUpdate( status );
    }
}