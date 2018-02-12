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

    /**
     * Gets the count of all duplicate social posts
     * @param hash
     * @param companyId
     * @return
     */
    long getDuplicatePostsCount(int hash, long companyId) throws InvalidInputException;

    /**
     * Updates all posts matching given hash and companyId with the given duplicateCount
     * @param hash
     * @param companyId
     * @param duplicateCount
     * @return
     */
    long updateDuplicateCount(int hash, long companyId, long duplicateCount) throws InvalidInputException;

    /**
     * Gets the post match the given postId
     * @param postId
     * @return
     */
    SocialResponseObject<?> getSocialPost(String postId);
}