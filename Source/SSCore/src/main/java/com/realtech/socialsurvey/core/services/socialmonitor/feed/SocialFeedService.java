package com.realtech.socialsurvey.core.services.socialmonitor.feed;

import com.realtech.socialsurvey.core.entities.SocialFeed;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * @author manish
 *
 */
public interface SocialFeedService
{
    /**
     * Method to save social feed data in mongo
     * @param socialFeed
     * @return
     * @throws InvalidInputException 
     */
    public SocialFeed saveFeed(SocialFeed socialFeed) throws InvalidInputException;
}
