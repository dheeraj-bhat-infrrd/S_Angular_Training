package com.realtech.socialsurvey.core.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.WriteResult;
import com.realtech.socialsurvey.core.dao.MongoSocialFeedDao;
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
    public void afterPropertiesSet() throws Exception
    {
        LOG.debug( "Checking if collections are created in mongodb" );
        if ( !mongoTemplate.collectionExists( SOCIAL_FEED_COLLECTION ) ) {
            LOG.debug( "Creating {}" , SOCIAL_FEED_COLLECTION );
            mongoTemplate.createCollection( SOCIAL_FEED_COLLECTION );
        }

    }
}
