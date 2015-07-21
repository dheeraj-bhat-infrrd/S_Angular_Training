package com.realtech.socialsurvey.core.feed.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.FacebookSocialPost;
import com.realtech.socialsurvey.core.entities.FacebookToken;
import com.realtech.socialsurvey.core.entities.FeedStatus;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.feed.SocialNetworkDataProcessor;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;

@Component("facebookFeed")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FacebookFeedProcessorImpl implements SocialNetworkDataProcessor<Post, FacebookToken> {

	private static final Logger LOG = LoggerFactory.getLogger(FacebookFeedProcessorImpl.class);
	private static final String FEED_SOURCE = "facebook";
	private static final int PAGE_SIZE = 200;

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
	
	@Value("${FB_CLIENT_ID}")
	private String facebookClientId;

	@Value("${FB_CLIENT_SECRET}")
	private String facebookClientSecret;

	private FeedStatus status;
	private long profileId;
	private Date lastFetchedTill;
	private String lastFetchedPostId = "";

	@Override
	@Transactional
	public void preProcess(long iden, String collection, FacebookToken token) {
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
					lastFetchedTill = new Date(status.getLastFetchedTill().getTime());
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
					lastFetchedTill = new Date(status.getLastFetchedTill().getTime());
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
					lastFetchedTill = new Date(status.getLastFetchedTill().getTime());
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
					lastFetchedTill = new Date(status.getLastFetchedTill().getTime());
					lastFetchedPostId = status.getLastFetchedPostId();
				}
				break;
		}
		profileId = iden;
	}

	@Override
	@Transactional
	public List<Post> fetchFeed(long iden, String collection, FacebookToken token) throws NonFatalException {
		LOG.info("Getting posts for " + collection + " with id: " + iden);

		// Settings Consumer and Access Tokens
		Facebook facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId(facebookClientId, facebookClientSecret);
		facebook.setOAuthAccessToken(new AccessToken(token.getFacebookAccessTokenToPost()));

		// building query to fetch
		List<Post> posts = new ArrayList<Post>();
		try {
			ResponseList<Post> resultList;
			if (lastFetchedTill != null) {
				Calendar calender = Calendar.getInstance();
			    calender.setTimeInMillis(lastFetchedTill.getTime());
			    calender.add(Calendar.SECOND, 1);
			    Date lastFetchedTillWithoneSecChange=calender.getTime();
				resultList = facebook.getStatuses(new Reading().limit(PAGE_SIZE).since(lastFetchedTillWithoneSecChange));
			}
			else {
				resultList = facebook.getStatuses(new Reading().limit(PAGE_SIZE));
			}
			posts.addAll(resultList);

			while (resultList.getPaging() != null && resultList.getPaging().getNext() != null) {
				resultList = facebook.fetchNext(resultList.getPaging());
				posts.addAll(resultList);
			}
		}
		catch (FacebookException e) {
			LOG.error("Exception in Facebook feed extration. Reason: " + e.getMessage());

			// increasing no.of retries
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

		return posts;
	}

	@Override
	public void processFeed(List<Post> posts, String collection) throws NonFatalException {
		LOG.info("Process posts for organizationUnit " + collection);
		if (lastFetchedTill == null) {
			lastFetchedTill = posts.get(0).getUpdatedTime();
		}

		FacebookSocialPost feed;
		for (Post post : posts) {
			// skipping empty or deleted status from fetched feed
			if (post.getMessage() == null && post.getName() == null && post.getStory() == null) {
				continue;
			}
			
			if (lastFetchedTill.before(post.getUpdatedTime())) {
				lastFetchedTill = post.getUpdatedTime();
			}

			feed = new FacebookSocialPost();
			feed.setPost(post);

			if (post.getMessage() != null) {
				feed.setPostText(post.getMessage());
			}
			else if (post.getName() != null) {
				feed.setPostText(post.getName());
			}
			else {
				feed.setPostText(post.getStory());
			}

			feed.setSource(FEED_SOURCE);
			feed.setPostId(post.getId());
			feed.setPostedBy(post.getFrom().getName());
			feed.setTimeInMillis(post.getUpdatedTime().getTime());

			switch (collection) {
				case MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION:
					feed.setCompanyId(profileId);
					break;

				case MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION:
					feed.setRegionId(profileId);
					break;

				case MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION:
					feed.setBranchId(profileId);
					break;

				case MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION:
					feed.setAgentId(profileId);
					break;
			}

			lastFetchedPostId = post.getId();
			
			// pushing to mongo
			mongoTemplate.insert(feed, CommonConstants.SOCIAL_POST_COLLECTION);
		}
	}

	@Override
	@Transactional
	public void postProcess(long iden, String collection) throws NonFatalException {
		status.setLastFetchedTill(new Timestamp(lastFetchedTill.getTime()));
		status.setLastFetchedPostId(lastFetchedPostId);

		feedStatusDao.saveOrUpdate(status);
	}
}