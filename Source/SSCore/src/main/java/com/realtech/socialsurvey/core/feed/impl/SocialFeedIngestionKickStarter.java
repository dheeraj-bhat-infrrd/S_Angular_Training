package com.realtech.socialsurvey.core.feed.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.FeedIngestionEntity;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.exception.NoContextFoundException;

/**
 * Kick starts the feed ingestion
 */
@Component
public class SocialFeedIngestionKickStarter {

	private static final Logger LOG = LoggerFactory.getLogger(SocialFeedIngestionKickStarter.class);
	private static final int BATCH_SIZE = 50;

	@Autowired
	private OrganizationUnitSettingsDao settingsDao;

	@Autowired
	private SocialFeedExecutors executors;

	private ApplicationContext context;

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	@Transactional
	public void startFeedIngestion() {
		LOG.info("Starting feed ingestion.");
		executors.setContext(context);

		startFeedIngestion(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		startFeedIngestion(MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION);
		startFeedIngestion(MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION);
		startFeedIngestion(MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION);

		executors.shutDownExecutors();
	}

	/**
	 * Fetches company feed
	 */
	@Transactional
	public void startFeedIngestion(String collectionName) {
		LOG.info("Kick starting company feed ingestion");
		List<FeedIngestionEntity> tokens = null;
		int currentBatch = 0;

		LOG.debug("Getting a list of entities with access tokens");
		do {
			tokens = settingsDao.fetchSocialMediaTokens(collectionName, currentBatch, BATCH_SIZE);
			if (tokens == null || tokens.size() == 0) {
				LOG.debug("No more tokens for " + collectionName);
				break;
			}
			else {
				LOG.debug("Found " + (currentBatch + tokens.size()) + " tokens for " + collectionName + " till now.");
				// get individual entity
				for (FeedIngestionEntity ingestionEntity : tokens) {
					fetchFeedFromIndividualSocialMediaTokens(ingestionEntity, collectionName);
				}
				if (tokens.size() < BATCH_SIZE) {
					LOG.debug("No more tokens left for " + collectionName + ". Breaking from loop.");
					break;
				}
				LOG.debug("Fetching more tokens from " + collectionName);
				currentBatch += BATCH_SIZE;
			}
		}
		while (true);
	}

	public void fetchFeedFromIndividualSocialMediaTokens(FeedIngestionEntity ingestionEntity, String collectionName) {
		LOG.debug("Fetching social feed for " + collectionName + " with iden: " + ingestionEntity.getIden());

		if (ingestionEntity != null && ingestionEntity.getSocialMediaTokens() != null) {
			LOG.debug("Starting to fetch the feed.");

			try {
				// check for facebook entry
				SocialMediaTokens token = ingestionEntity.getSocialMediaTokens();
				if (token.getFacebookToken() != null) {
					LOG.info("Processing facebook posts for " + collectionName + " with iden: " + ingestionEntity.getIden());
					executors.addFacebookProcessorToPool(ingestionEntity, collectionName);
				}
				else {
					LOG.warn("No facebook token found for " + collectionName + " with iden: " + ingestionEntity.getIden());
				}

				// check for google entry
				if (token.getGoogleToken() != null) {
					LOG.info("Processing google plus activities for " + collectionName + " with iden: " + ingestionEntity.getIden());
					executors.addGoogleProcessorToPool(ingestionEntity, collectionName);
				}
				else {
					LOG.warn("No google+ token found for " + collectionName + " with iden: " + ingestionEntity.getIden());
				}

				// check for linkedin entry
				if (token.getLinkedInToken() != null) {
					LOG.info("Processing linkedin posts for " + collectionName + " with iden: " + ingestionEntity.getIden());
					executors.addLinkedInProcessorToPool(ingestionEntity, collectionName);
				}
				else {
					LOG.warn("No linkedin token found for " + collectionName + " with iden: " + ingestionEntity.getIden());
				}

				// check for rss entry
				if (token.getRssToken() != null) {
					// TODO
				}
				else {
					LOG.warn("No rss token found for " + collectionName + " with iden: " + ingestionEntity.getIden());
				}
				
				// check for twitter entry
				if (token.getTwitterToken() != null) {
					LOG.info("Processing twitter tweets for " + collectionName + " with iden: " + ingestionEntity.getIden());
					executors.addTwitterProcessorToPool(ingestionEntity, collectionName);
				}
				else {
					LOG.warn("No twitter token found for " + collectionName + " with iden: " + ingestionEntity.getIden());
				}
				
				// check for yelp entry
				if (token.getYelpToken() != null) {
					// TODO
				}
				else {
					LOG.warn("No yelp token found for " + collectionName + " with iden: " + ingestionEntity.getIden());
				}
			}
			catch (NoContextFoundException e) {
				executors.shutDownExecutorsNow();
				e.printStackTrace();
				throw e;
			}
		}
		else {
			LOG.error("No social media token present for " + collectionName + " with iden: " + ingestionEntity.getIden());
		}
	}
}