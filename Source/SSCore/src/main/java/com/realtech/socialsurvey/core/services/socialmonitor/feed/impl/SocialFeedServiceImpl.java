package com.realtech.socialsurvey.core.services.socialmonitor.feed.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.dao.MongoSocialFeedDao;
import com.realtech.socialsurvey.core.dao.impl.MongoSocialFeedDaoImpl;
import com.realtech.socialsurvey.core.entities.SocialResponseObject;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.socialmonitor.feed.SocialFeedService;


/**
 * @author manish
 *
 */
@DependsOn ( "generic")
@Component
public class SocialFeedServiceImpl implements SocialFeedService
{
    private static final Logger LOG = LoggerFactory.getLogger( SocialFeedServiceImpl.class );
    @Autowired
    MongoSocialFeedDao mongoSocialFeedDao;

    @Override
    public SocialResponseObject<?> saveFeed( SocialResponseObject<?> socialFeed ) throws InvalidInputException 
    {
        LOG.info( "Inside save feed method {}" , socialFeed);
        if(socialFeed == null){
            throw new InvalidInputException( "Feed cannt be null or empy" );
        }
        mongoSocialFeedDao.insertSocialFeed( socialFeed, MongoSocialFeedDaoImpl.SOCIAL_FEED_COLLECTION );
        LOG.info( "End of save feed method" );
        return socialFeed;
    }

    @Override
    public long getDuplicatePostsCount(int hash, long companyId) throws InvalidInputException {
        LOG.info("Executing getDuplicatePostsCount method with hash = {} and companyId = {} ", hash, companyId);
        if(companyId  <= 0){
            throw new InvalidInputException( "companyId cannot be 0" );
        }
        return mongoSocialFeedDao.getDuplicatePostsCount(hash, companyId);
    }

    @Override
    public long updateDuplicateCount(int hash, long companyId, long duplicateCount) throws InvalidInputException {
        LOG.info("Executing updateDuplicateCount method with hash = {}, companyId = {} and duplicateCount = {} ", hash, companyId, duplicateCount);
        if( duplicateCount  <= 0 || companyId <= 0){
            throw new InvalidInputException( "companyId or duplicateCount cannot be <= 0" );
        }
        return mongoSocialFeedDao.updateDuplicateCount(hash, companyId, duplicateCount);
    }

    @Override
    public SocialResponseObject<?> getSocialPost(String postId) {
        return mongoSocialFeedDao.getSocialPost(postId);
    }

}