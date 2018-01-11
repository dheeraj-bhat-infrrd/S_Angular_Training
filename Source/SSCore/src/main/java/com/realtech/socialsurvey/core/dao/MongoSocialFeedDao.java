package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.SocialFeed;

public interface MongoSocialFeedDao
{
    public void insertSocialFeed( SocialFeed socialFeed, String collectionName );
}
