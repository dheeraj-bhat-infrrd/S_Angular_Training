package com.realtech.socialsurvey.core.services.socialmonitor.feed;

import com.realtech.socialsurvey.core.entities.SocialResponseObject;
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
    public SocialResponseObject<?> saveFeed(SocialResponseObject<?> socialFeed) throws InvalidInputException;
}
