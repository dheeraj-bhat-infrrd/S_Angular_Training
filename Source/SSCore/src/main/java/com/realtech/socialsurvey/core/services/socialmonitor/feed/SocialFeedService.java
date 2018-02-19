package com.realtech.socialsurvey.core.services.socialmonitor.feed;

import com.mongodb.DuplicateKeyException;
import com.realtech.socialsurvey.core.entities.SocialResponseObject;
import java.util.List;

import com.realtech.socialsurvey.core.entities.SocialFeedsActionUpdate;
import com.realtech.socialsurvey.core.entities.SocialMonitorFeedData;
import com.realtech.socialsurvey.core.entities.SocialMonitorMacro;
import com.realtech.socialsurvey.core.entities.SocialMonitorResponseData;
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
     * Updates all posts matching given hash and companyId with the given duplicateCount
     * @param hash
     * @param companyId
     * @param duplicateCount
     * @return
     */
    long updateDuplicateCount(int hash, long companyId) throws InvalidInputException;

    /** Gets all feeds on social monitor based on the flag
     * @param profileId
     * @param profileLevel
     * @param startIndex
     * @param limit
     * @param flag
     * @return
     * @throws InvalidInputException
     */
    public SocialMonitorResponseData getAllSocialPosts(long profileId, String profileLevel, int startIndex, int limit, String status, boolean flag, List<String> feeds) throws InvalidInputException;
        
    /**
     * Update actions and macros on feeds
     * @param socialFeedsActionUpdate
     * @param companyId
     * @throws InvalidInputException
     */
    public void updateActionForFeeds(SocialFeedsActionUpdate socialFeedsActionUpdate, long companyId) throws InvalidInputException;
    
    /**
     * Fetch macros based on companyId
     * @param companyId
     * @return
     * @throws InvalidInputException
     */
    public List<SocialMonitorMacro> getMacros(long companyId) throws InvalidInputException; 
    
    /**
     * add macros to a company
     * @param socialMonitorMacro
     * @param companyId
     * @throws InvalidInputException
     */
    public void updateMacrosForFeeds(SocialMonitorMacro socialMonitorMacro, long companyId) throws InvalidInputException;
    
}
