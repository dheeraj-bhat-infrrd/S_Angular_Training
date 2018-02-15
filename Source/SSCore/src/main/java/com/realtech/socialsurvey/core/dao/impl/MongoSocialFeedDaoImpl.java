package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import com.mongodb.WriteResult;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.MongoSocialFeedDao;
import com.realtech.socialsurvey.core.entities.ActionHistory;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialFeedsActionUpdate;
import com.realtech.socialsurvey.core.entities.SocialMonitorMacro;
import com.realtech.socialsurvey.core.entities.SocialResponseObject;


@Repository
public class MongoSocialFeedDaoImpl implements MongoSocialFeedDao, InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( MongoSocialFeedDaoImpl.class );

    @Autowired
    private MongoTemplate mongoTemplate;

    public static final String SOCIAL_FEED_COLLECTION = "SOCIAL_FEED_COLLECTION";
    public static final String KEY_IDENTIFIER = "_id";
    private static final String HASH = "hash";
    private static final String COMPANY_ID = "companyId";
    private static final String DUPLICATE_COUNT = "duplicateCount";
    private static final String POST_ID = "postId";
    private static final String FLAGGED = "flagged";
    private static final String FEED_TYPE = "type";

    @Override
    public void insertSocialFeed( SocialResponseObject<?> socialFeed, String collectionName )
    {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "Creating {} document. Social feed id: {}", collectionName, socialFeed.getId() );
            LOG.debug( "Inserting into {}. Object: {}", collectionName, socialFeed.toString() );
        }

        mongoTemplate.insert( socialFeed, collectionName );
        LOG.debug( "Inserted into {}", collectionName );
    }

    @Override
    public long getDuplicatePostsCount(int hash, long companyId) {
        if(LOG.isDebugEnabled()){
            LOG.debug("Fetching count of duplicate posts with hash = {} and companyId = {}", hash, companyId);
        }
        Query query = new  Query();
        query.addCriteria(Criteria.where(HASH).is(hash).and(COMPANY_ID).is(companyId));
        return mongoTemplate.count(query, SOCIAL_FEED_COLLECTION);
    }

    @Override
    public long updateDuplicateCount(int hash, long companyId, long duplicateCount) {
        if(LOG.isDebugEnabled()) {
            LOG.debug("Updating posts with duplicateCount {} having hash = {} ", duplicateCount, hash);
        }
        Query query = new Query().addCriteria(Criteria.where(HASH).is(hash).and(COMPANY_ID).is(companyId));
        Update update = new Update().set(DUPLICATE_COUNT, duplicateCount);
        WriteResult result = mongoTemplate.updateMulti(query, update, SOCIAL_FEED_COLLECTION);
        return result.getN();
    }

    @Override
    public boolean isSocialPostSaved(String postId) {
        if(LOG.isDebugEnabled()) {
            LOG.debug("Fetching posts with postId {} ", postId);
        }
        Query query = new Query().addCriteria(Criteria.where(POST_ID).is(postId));
        return mongoTemplate.exists(query, SocialResponseObject.class, SOCIAL_FEED_COLLECTION);
    }
    
	@Override
	public void updateSocialFeed(SocialFeedsActionUpdate socialFeedsActionUpdate, List<ActionHistory> actionHistories, int updateFlag, String collectionName) {
		LOG.debug("Method updateSocialFeed() started");
		List<String> postIds = socialFeedsActionUpdate.getPostIds();
		for (String postId : postIds) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Updating {} document. Social feed id: {}", collectionName, postId);
			}
			Query query = new Query();

			query.addCriteria(Criteria.where("postId").is(postId));

			Update update = new Update();

			if(updateFlag == 1) {
				update.set("flagged", socialFeedsActionUpdate.isFlagged());
			} else if(updateFlag == 2 ) {
				update.set("flagged", false);
				update.set("status", socialFeedsActionUpdate.getStatus());
			}
			for(ActionHistory actionHistory : actionHistories) {
				update.push("actionHistory", actionHistory);
				mongoTemplate.updateFirst(query, update, collectionName);
			}
			LOG.debug("Updated {}", collectionName);
		}

	}

    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.debug( "Checking if collections are created in mongodb" );
        if ( !mongoTemplate.collectionExists( SOCIAL_FEED_COLLECTION ) ) {
            LOG.debug( "Creating {}" , SOCIAL_FEED_COLLECTION );
            mongoTemplate.createCollection( SOCIAL_FEED_COLLECTION );
        }

    }

	@Override
	public SocialResponseObject getSocialFeed(String postId, String collectionName) {
		LOG.debug("Fetching Social Feed for postId {}", postId);
		Query query = new Query();

		query.addCriteria(Criteria.where("postId").is(postId));

		return mongoTemplate.findOne(query, SocialResponseObject.class, collectionName);
	}

	@Override
	public OrganizationUnitSettings FetchMacros(long companyId) {
		LOG.debug("Fetching Macros from COMAPNY_SETTINGS");
		Query query = new Query();

		query.addCriteria(Criteria.where("iden").is(companyId));

		return mongoTemplate.findOne(query, OrganizationUnitSettings.class,
				CommonConstants.COMPANY_SETTINGS_COLLECTION);

	}

	@Override
	public void updateMacros(SocialMonitorMacro socialMonitorMacro, long companyId) {
		LOG.debug("Updating Macros in COMPANY_SETTINGS");
		Query query = new Query();

		query.addCriteria(Criteria.where("iden").is(companyId));

		Update update = new Update();

		update.addToSet("socialMonitorMacros", socialMonitorMacro);

		mongoTemplate.updateFirst(query, update, CommonConstants.COMPANY_SETTINGS_COLLECTION);
		LOG.debug("Updated {}", CommonConstants.COMPANY_SETTINGS_COLLECTION);

	}

	@Override
	public void updateMacroCount(List<SocialMonitorMacro> socialMonitorMacros, long companyId) {
		LOG.debug("Updating Macro count in COMPANY_SETTINGS");
		
		Query query = new Query();
		
		query.addCriteria(Criteria.where("iden").is(companyId));
		
		Update update = new Update();

		update.set("socialMonitorMacros", socialMonitorMacros);
		
		mongoTemplate.updateFirst(query, update, CommonConstants.COMPANY_SETTINGS_COLLECTION);
		LOG.debug("Updated {}", CommonConstants.COMPANY_SETTINGS_COLLECTION);
		
	}

	@Override
	public List<SocialResponseObject> getAllSocialFeeds(long profileId, String key, int startIndex, int limit,
			boolean flag, String status, List<String> feedtype) {
		LOG.debug("Fetching Social Feeds");
		Query query = new Query();

		if(flag) {
			if (key.equals(CommonConstants.COMPANY_ID)) {
				query.addCriteria(Criteria.where(CommonConstants.COMPANY_ID).is(profileId)
						.andOperator((Criteria.where(FLAGGED).is(flag)), (Criteria.where(FEED_TYPE).in(feedtype))));
			} else if (key.equals(CommonConstants.REGION_ID)) {
				query.addCriteria(Criteria.where(CommonConstants.REGION_ID).is(profileId)
						.andOperator((Criteria.where(FLAGGED).is(flag)), (Criteria.where(FEED_TYPE).in(feedtype))));
			} else if (key.equals(CommonConstants.BRANCH_ID)) {
				query.addCriteria(Criteria.where(CommonConstants.BRANCH_ID).is(profileId)
						.andOperator((Criteria.where(FLAGGED).is(flag)), (Criteria.where(FEED_TYPE).in(feedtype))));
			} else {
				query.addCriteria(Criteria.where(CommonConstants.AGENT_ID).is(profileId)
						.andOperator((Criteria.where(FLAGGED).is(flag)), (Criteria.where(FEED_TYPE).in(feedtype))));
			}
		} else if(!status.isEmpty() && !flag) {
			if (key.equals(CommonConstants.COMPANY_ID)) {
				query.addCriteria(Criteria.where(CommonConstants.COMPANY_ID).is(profileId)
						.andOperator((Criteria.where(CommonConstants.STATUS_COLUMN).is(status.toUpperCase())), (Criteria.where(FEED_TYPE).in(feedtype))));
			} else if (key.equals(CommonConstants.REGION_ID)) {
				query.addCriteria(Criteria.where(CommonConstants.REGION_ID).is(profileId)
						.andOperator((Criteria.where(CommonConstants.STATUS_COLUMN).is(status.toUpperCase())), (Criteria.where(FEED_TYPE).in(feedtype))));
			} else if (key.equals(CommonConstants.BRANCH_ID)) {
				query.addCriteria(Criteria.where(CommonConstants.BRANCH_ID).is(profileId)
						.andOperator((Criteria.where(CommonConstants.STATUS_COLUMN).is(status.toUpperCase())), (Criteria.where(FEED_TYPE).in(feedtype))));
			} else {
				query.addCriteria(Criteria.where(CommonConstants.AGENT_ID).is(profileId)
						.andOperator((Criteria.where(CommonConstants.STATUS_COLUMN).is(status.toUpperCase())), (Criteria.where(FEED_TYPE).in(feedtype))));
			}
		} else {
			if (key.equals(CommonConstants.COMPANY_ID)) {
				query.addCriteria(Criteria.where(CommonConstants.COMPANY_ID).is(profileId).andOperator(Criteria.where(FEED_TYPE).in(feedtype)));
			} else if (key.equals(CommonConstants.REGION_ID)) {
				query.addCriteria(Criteria.where(CommonConstants.REGION_ID).is(profileId).andOperator(Criteria.where(FEED_TYPE).in(feedtype)));
			} else if (key.equals(CommonConstants.BRANCH_ID)) {
				query.addCriteria(Criteria.where(CommonConstants.BRANCH_ID).is(profileId).andOperator(Criteria.where(FEED_TYPE).in(feedtype)));
			} else {
				query.addCriteria(Criteria.where(CommonConstants.AGENT_ID).is(profileId).andOperator(Criteria.where(FEED_TYPE).in(feedtype)));
			}
		}
		
		query.with(new Sort(Sort.Direction.DESC, "_id"));
		if (startIndex > -1) {
			query.skip(startIndex);
		}
		if (limit > -1) {
			query.limit(limit);
		}

		List<SocialResponseObject> socialResponseObjects = mongoTemplate.find(query, SocialResponseObject.class,
				SOCIAL_FEED_COLLECTION);

		return socialResponseObjects;
	}

	@Override
	public long getAllSocialFeedsCount(long profileId, String key, boolean flag,
			String status, List<String> feedtype) {
		LOG.debug("Fetching Social Feeds count");
		Query query = new Query();

		if(flag) {
			if (key.equals(CommonConstants.COMPANY_ID)) {
				query.addCriteria(Criteria.where(CommonConstants.COMPANY_ID).is(profileId)
						.andOperator((Criteria.where(FLAGGED).is(flag)), (Criteria.where(FEED_TYPE).in(feedtype))));
			} else if (key.equals(CommonConstants.REGION_ID)) {
				query.addCriteria(Criteria.where(CommonConstants.REGION_ID).is(profileId)
						.andOperator((Criteria.where(FLAGGED).is(flag)), (Criteria.where(FEED_TYPE).in(feedtype))));
			} else if (key.equals(CommonConstants.BRANCH_ID)) {
				query.addCriteria(Criteria.where(CommonConstants.BRANCH_ID).is(profileId)
						.andOperator((Criteria.where(FLAGGED).is(flag)), (Criteria.where(FEED_TYPE).in(feedtype))));
			} else {
				query.addCriteria(Criteria.where(CommonConstants.AGENT_ID).is(profileId)
						.andOperator((Criteria.where(FLAGGED).is(flag)), (Criteria.where(FEED_TYPE).in(feedtype))));
			}
		} else if(!status.isEmpty() && !flag) {
			if (key.equals(CommonConstants.COMPANY_ID)) {
				query.addCriteria(Criteria.where(CommonConstants.COMPANY_ID).is(profileId)
						.andOperator((Criteria.where(CommonConstants.STATUS_COLUMN).is(status.toUpperCase())), (Criteria.where(FEED_TYPE).in(feedtype))));
			} else if (key.equals(CommonConstants.REGION_ID)) {
				query.addCriteria(Criteria.where(CommonConstants.REGION_ID).is(profileId)
						.andOperator((Criteria.where(CommonConstants.STATUS_COLUMN).is(status.toUpperCase())), (Criteria.where(FEED_TYPE).in(feedtype))));
			} else if (key.equals(CommonConstants.BRANCH_ID)) {
				query.addCriteria(Criteria.where(CommonConstants.BRANCH_ID).is(profileId)
						.andOperator((Criteria.where(CommonConstants.STATUS_COLUMN).is(status.toUpperCase())), (Criteria.where(FEED_TYPE).in(feedtype))));
			} else {
				query.addCriteria(Criteria.where(CommonConstants.AGENT_ID).is(profileId)
						.andOperator((Criteria.where(CommonConstants.STATUS_COLUMN).is(status.toUpperCase())), (Criteria.where(FEED_TYPE).in(feedtype))));
			}
		} else {
			if (key.equals(CommonConstants.COMPANY_ID)) {
				query.addCriteria(Criteria.where(CommonConstants.COMPANY_ID).is(profileId).andOperator(Criteria.where(FEED_TYPE).in(feedtype)));
			} else if (key.equals(CommonConstants.REGION_ID)) {
				query.addCriteria(Criteria.where(CommonConstants.REGION_ID).is(profileId).andOperator(Criteria.where(FEED_TYPE).in(feedtype)));
			} else if (key.equals(CommonConstants.BRANCH_ID)) {
				query.addCriteria(Criteria.where(CommonConstants.BRANCH_ID).is(profileId).andOperator(Criteria.where(FEED_TYPE).in(feedtype)));
			} else {
				query.addCriteria(Criteria.where(CommonConstants.AGENT_ID).is(profileId).andOperator(Criteria.where(FEED_TYPE).in(feedtype)));
			}
		}
		
		return mongoTemplate.count(query, SOCIAL_FEED_COLLECTION);
	}	
	

}
