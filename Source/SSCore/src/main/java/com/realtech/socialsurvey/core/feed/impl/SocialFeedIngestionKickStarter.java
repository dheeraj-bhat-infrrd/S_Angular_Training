package com.realtech.socialsurvey.core.feed.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SocialPostDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.FeedIngestionEntity;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoContextFoundException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;


/**
 * Kick starts the feed ingestion
 */
@Component
public class SocialFeedIngestionKickStarter
{

    private static final Logger LOG = LoggerFactory.getLogger( SocialFeedIngestionKickStarter.class );
    private static final int BATCH_SIZE = 50;

    @Autowired
    private OrganizationUnitSettingsDao settingsDao;

    @Autowired
    private SocialPostDao socialPostDao;

    @Autowired
    private SocialFeedExecutors executors;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private BatchTrackerService batchTrackerService;

    @Value ( "${SOCIALPOST_SPAN_DAYS}")
    private long socialPostSpanDays;


    @Transactional
    public void startFeedIngestion()
    {
        LOG.info( "Starting feed ingestion." );
        try {
            //update last run start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_SOCIAL_FEED_INGESTER, CommonConstants.BATCH_NAME_SOCIAL_FEED_INGESTER );

            executors.setContext( context );

            // Fetching New posts
            startFeedIngestion( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
            startFeedIngestion( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
            startFeedIngestion( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
            startFeedIngestion( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

            /*executors.shutDownExecutors();*/

            // Deleting older posts
            startOlderFeedPurging();

            //Update last build time in batch tracker table
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_SOCIAL_FEED_INGESTER );
        } catch ( Exception e ) {
            LOG.error( "Error in feed ingestion", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType( CommonConstants.BATCH_TYPE_SOCIAL_FEED_INGESTER,
                    e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_SOCIAL_FEED_INGESTER,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in feed ingestion " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    @Transactional
    public void startOlderFeedPurging()
    {
        LOG.info( "Kick starting older feed Purging" );
        // check to avoid purging
        if ( socialPostSpanDays > 0l ) {
            long timeSpanInMilliSecs = TimeUnit.MILLISECONDS.convert( socialPostSpanDays, TimeUnit.DAYS );
            socialPostDao.purgeOlderSocialPosts( timeSpanInMilliSecs );
        }
    }


    /**
     * Fetches company feed
     */
    @Transactional
    public void startFeedIngestion( String collectionName )
    {
        LOG.info( "Kick starting company feed ingestion" );
        List<FeedIngestionEntity> tokens = null;
        int currentBatch = 0;

        LOG.debug( "Getting a list of entities with access tokens" );
        do {
            tokens = settingsDao.fetchSocialMediaTokens( collectionName, currentBatch, BATCH_SIZE );
            if ( tokens == null || tokens.size() == 0 ) {
                LOG.debug( "No more tokens for " + collectionName );
                break;
            } else {
                LOG.debug( "Found " + ( currentBatch + tokens.size() ) + " tokens for " + collectionName + " till now." );
                // get individual entity
                for ( FeedIngestionEntity ingestionEntity : tokens ) {
                    try {
                        fetchFeedFromIndividualSocialMediaTokens( ingestionEntity, collectionName );
                    } catch ( Exception e ) {
                        LOG.warn( "Exception for " + collectionName + " and " + ingestionEntity.getIden() );
                    }
                }

                if ( tokens.size() < BATCH_SIZE ) {
                    LOG.debug( "No more tokens left for " + collectionName + ". Breaking from loop." );
                    break;
                }
                LOG.debug( "Fetching more tokens from " + collectionName );
                currentBatch += BATCH_SIZE;
            }
        } while ( true );
    }


    public void fetchFeedFromIndividualSocialMediaTokens( FeedIngestionEntity ingestionEntity, String collectionName )
    {
        LOG.debug( "Fetching social feed for " + collectionName + " with iden: " + ingestionEntity.getIden() );

        if ( ingestionEntity != null && ingestionEntity.getSocialMediaTokens() != null ) {
            LOG.debug( "Starting to fetch the feed." );

            try {
                // check for facebook token
                SocialMediaTokens token = ingestionEntity.getSocialMediaTokens();
                if ( token.getFacebookToken() != null ) {
                    LOG.info( "Processing facebook posts for " + collectionName + " with iden: " + ingestionEntity.getIden() );
                    executors.addFacebookProcessorToPool( ingestionEntity, collectionName );
                } else {
                    LOG.warn( "No facebook token found for " + collectionName + " with iden: " + ingestionEntity.getIden() );
                }

                // check for google token
                if ( token.getGoogleToken() != null ) {
                    LOG.info( "Processing google plus activities for " + collectionName + " with iden: "
                        + ingestionEntity.getIden() );
                    executors.addGoogleProcessorToPool( ingestionEntity, collectionName );
                } else {
                    LOG.warn( "No google+ token found for " + collectionName + " with iden: " + ingestionEntity.getIden() );
                }

                // check for linkedin token
                if ( token.getLinkedInToken() != null ) {
                    // TODO
                } else {
                    LOG.warn( "No linkedin token found for " + collectionName + " with iden: " + ingestionEntity.getIden() );
                }

                // check for rss token
                if ( token.getRssToken() != null ) {
                    // TODO
                } else {
                    LOG.warn( "No rss token found for " + collectionName + " with iden: " + ingestionEntity.getIden() );
                }

                // check for twitter token
                if ( token.getTwitterToken() != null ) {
                    LOG.info( "Processing twitter tweets for " + collectionName + " with iden: " + ingestionEntity.getIden() );
                    executors.addTwitterProcessorToPool( ingestionEntity, collectionName );
                } else {
                    LOG.warn( "No twitter token found for " + collectionName + " with iden: " + ingestionEntity.getIden() );
                }

                // check for yelp token
                if ( token.getYelpToken() != null ) {
                    // TODO
                } else {
                    LOG.warn( "No yelp token found for " + collectionName + " with iden: " + ingestionEntity.getIden() );
                }
            } catch ( NoContextFoundException e ) {
                executors.shutDownExecutorsNow();
                e.printStackTrace();
                throw e;
            }
        } else {
            LOG.error( "No social media token present for " + collectionName + " with iden: " + ingestionEntity.getIden() );
        }
    }
}