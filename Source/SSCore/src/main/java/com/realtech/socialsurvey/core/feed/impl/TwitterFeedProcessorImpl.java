package com.realtech.socialsurvey.core.feed.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.TwitterStatusTimeComparator;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.FeedStatus;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.TwitterSocialPost;
import com.realtech.socialsurvey.core.entities.TwitterToken;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.feed.SocialNetworkDataProcessor;
import com.realtech.socialsurvey.core.services.mail.EmailServices;

@Component("twitterFeed")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TwitterFeedProcessorImpl implements SocialNetworkDataProcessor<Status, TwitterToken> {

	private static final Logger LOG = LoggerFactory.getLogger(TwitterFeedProcessorImpl.class);
	private static final String FEED_SOURCE = "twitter";
	private static final int PAGE_SIZE = 200;
	private static final String twitterUriSplitStr="http";
	@Autowired
	private GenericDao<FeedStatus, Long> feedStatusDao;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private OrganizationUnitSettingsDao settingsDao;

	@Autowired
	private EmailServices emailServices;
	
	@Value("${SOCIAL_CONNECT_REMINDER_THRESHOLD}")
	private long socialConnectThreshold;
	
	@Value("${TWITTER_CONSUMER_KEY}")
	private String twitterConsumerKey;

	@Value("${TWITTER_CONSUMER_SECRET}")
	private String twitterConsumerSecret;

	
	private FeedStatus status;
	private long profileId;
	private Timestamp lastFetchedTill;
	private String lastFetchedPostId = "";

	@Override
	@Transactional
	public void preProcess(long iden, String collection, TwitterToken token) {
		List<FeedStatus> statuses = null;
		Map<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.FEED_SOURCE_COLUMN, FEED_SOURCE);

		switch (collection) {
			case MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION:
				queries.put(CommonConstants.COMPANY_ID_COLUMN, iden);

				statuses = feedStatusDao.findByKeyValue(FeedStatus.class, queries);
				if (statuses != null && statuses.size() > 0) {
					status = statuses.get(CommonConstants.INITIAL_INDEX);
				}

				if (status == null) {
					status = new FeedStatus();
					status.setFeedSource(FEED_SOURCE);
					status.setCompanyId(iden);
				}
				else {
					lastFetchedPostId = status.getLastFetchedPostId();
				}
				break;

			case MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION:
				queries.put(CommonConstants.REGION_ID_COLUMN, iden);

				statuses = feedStatusDao.findByKeyValue(FeedStatus.class, queries);
				if (statuses != null && statuses.size() > 0) {
					status = statuses.get(CommonConstants.INITIAL_INDEX);
				}

				if (status == null) {
					status = new FeedStatus();
					status.setFeedSource(FEED_SOURCE);
					status.setRegionId(iden);
				}
				else {
					lastFetchedPostId = status.getLastFetchedPostId();
				}
				break;

			case MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION:
				queries.put(CommonConstants.BRANCH_ID_COLUMN, iden);

				statuses = feedStatusDao.findByKeyValue(FeedStatus.class, queries);
				if (statuses != null && statuses.size() > 0) {
					status = statuses.get(CommonConstants.INITIAL_INDEX);
				}

				if (status == null) {
					status = new FeedStatus();
					status.setFeedSource(FEED_SOURCE);
					status.setBranchId(iden);
				}
				else {
					lastFetchedPostId = status.getLastFetchedPostId();
				}
				break;

			case MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION:
				queries.put(CommonConstants.AGENT_ID_COLUMN, iden);

				statuses = feedStatusDao.findByKeyValue(FeedStatus.class, queries);
				if (statuses != null && statuses.size() > 0) {
					status = statuses.get(CommonConstants.INITIAL_INDEX);
				}

				if (status == null) {
					status = new FeedStatus();
					status.setFeedSource(FEED_SOURCE);
					status.setAgentId(iden);
				}
				else {
					lastFetchedPostId = status.getLastFetchedPostId();
				}
				break;
		}
		profileId = iden;
	}

	@Override
	@Transactional
	public List<Status> fetchFeed(long iden, String collection, TwitterToken token) throws NonFatalException {
		LOG.info("Getting tweets for " + collection + " with id: " + iden);

		// Settings Consumer and Access Tokens
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(twitterConsumerKey, twitterConsumerSecret);
		twitter.setOAuthAccessToken(new AccessToken(token.getTwitterAccessToken(), token.getTwitterAccessTokenSecret()));

		// building query to fetch
		List<Status> tweets = new ArrayList<Status>();
		try {
			int pageNo = 1;
			ResponseList<Status> resultList;
			do {
				if (lastFetchedPostId.equals("")) {
					resultList = twitter.getUserTimeline(new Paging(pageNo, PAGE_SIZE));
				}
				else {
					resultList = twitter.getUserTimeline(new Paging(pageNo, PAGE_SIZE).sinceId(Long.parseLong(lastFetchedPostId)));
				}

				tweets.addAll(resultList);
				pageNo++;
			}
			while (resultList.size() == PAGE_SIZE);
		}
		catch (TwitterException e) {
			LOG.error("Exception in Twitter feed extration. Reason: " + e.getMessage());

			// setting no.of retries
			status.setRetries(status.getRetries() + 1);
			
			// sending reminder mail and increasing counter
			if (status.getRemindersSent() < socialConnectThreshold) {
				OrganizationUnitSettings unitSettings = settingsDao.fetchOrganizationUnitSettingsById(iden, collection);
				
				String userEmail = unitSettings.getContact_details().getMail_ids().getWork();
				emailServices.sendSocialConnectMail(userEmail, unitSettings.getContact_details().getName(), userEmail, FEED_SOURCE);
				
				status.setRemindersSent(status.getRemindersSent() + 1);
			}
			
			feedStatusDao.saveOrUpdate(status);
		}
		return tweets;
	}

	@Override
	public void processFeed(List<Status> tweets, String collection) throws NonFatalException {
		LOG.info("Process tweets for organizationUnit " + collection);

		Collections.sort(tweets, new TwitterStatusTimeComparator());
		TwitterSocialPost post;
		for (Status tweet : tweets) {
			if (tweet.getText() == null || tweet.getText().isEmpty()) {
				continue;
			}
			
			post = new TwitterSocialPost();
			post.setTweet(tweet);
			post.setPostText(tweet.getText());
			post.setSource(FEED_SOURCE);
			post.setPostId(String.valueOf(tweet.getId()));
			post.setPostedBy(tweet.getUser().getName());
			post.setTimeInMillis(tweet.getCreatedAt().getTime());
			String[] twitterHref=tweet.getText().split(twitterUriSplitStr);
			if(twitterHref.length > 1){
				String postUrl=twitterHref[1];
				post.setPostUrl(twitterUriSplitStr.concat(postUrl));
			}
			switch (collection) {
				case MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION:
					post.setCompanyId(profileId);
					break;

				case MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION:
					post.setRegionId(profileId);
					break;

				case MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION:
					post.setBranchId(profileId);
					break;

				case MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION:
					post.setAgentId(profileId);
					break;
			}

			// updating last fetched details
			lastFetchedTill = new Timestamp(tweet.getCreatedAt().getTime());
			lastFetchedPostId = String.valueOf(tweet.getId());

			// pushing to mongo
			mongoTemplate.insert(post, CommonConstants.SOCIAL_POST_COLLECTION);
		}
	}

	@Override
	@Transactional
	public void postProcess(long iden, String collection) throws NonFatalException {
		status.setLastFetchedTill(lastFetchedTill);
		status.setLastFetchedPostId(lastFetchedPostId);

		feedStatusDao.saveOrUpdate(status);
	}
}