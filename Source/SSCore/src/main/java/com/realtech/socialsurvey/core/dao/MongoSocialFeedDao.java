package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.SocialResponseObject;

public interface MongoSocialFeedDao
{
    public void insertSocialFeed( SocialResponseObject<?> socialFeed, String collectionName );
}
