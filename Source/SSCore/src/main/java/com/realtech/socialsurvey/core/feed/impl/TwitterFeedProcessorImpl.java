package com.realtech.socialsurvey.core.feed.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.SocialPostDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.FeedStatus;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.TwitterToken;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.feed.SocialNetworkDataProcessor;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TwitterFeedProcessorImpl implements SocialNetworkDataProcessor<Status, TwitterToken> {

	private static final Logger LOG = LoggerFactory.getLogger(TwitterFeedProcessorImpl.class);
	private static final String FEED_SOURCE = "twitter";
	private static final int PAGE_SIZE = 10;

	@Autowired
	private GenericDao<FeedStatus, Long> feedStatusDao;

	@Autowired
	private SocialPostDao socialPostDao;

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
	public void preProcess(long iden, String organizationUnit, TwitterToken token) {
		List<FeedStatus> statuses = null;
		Map<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.FEED_SOURCE_COLUMN, FEED_SOURCE);

		switch (organizationUnit) {
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
	public List<Status> fetchFeed(long iden, String organizationUnit, TwitterToken token) throws NonFatalException {
		LOG.info("Getting tweets for " + organizationUnit + " with id: " + iden);

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
				pageNo ++;
			}
			while (resultList.size() == PAGE_SIZE);
		}
		catch (Exception e) {
			LOG.error("Exception in Twitter feed extration. Reason: " + e.getMessage());
		}
		return tweets;
	}

	@Override
	public void processFeed(List<Status> tweets, String organizationUnit) throws NonFatalException {
		LOG.info("Process tweets for organizationUnit " + organizationUnit);

		SocialPost post;
		for (Status tweet : tweets) {
			post = new SocialPost();
			post.setPostText(tweet.getText());
			post.setSource(FEED_SOURCE);
			post.setPostId(tweet.getId());
			post.setTimeInMillis(tweet.getCreatedAt().getTime());

			switch (organizationUnit) {
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
			socialPostDao.addPostToUserProfile(post);
		}
	}

	@Override
	@Transactional
	public void postProcess(long iden, String organizationUnit) throws NonFatalException {
		status.setLastFetchedTill(lastFetchedTill);
		status.setLastFetchedPostId(lastFetchedPostId);

		feedStatusDao.saveOrUpdate(status);
	}
}