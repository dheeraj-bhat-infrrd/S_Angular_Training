package com.realtech.socialsurvey.compute.dao;

import java.io.Serializable;
import java.util.List;

/**
 * @author manish
 *
 */
public interface RedisSocialMediaStateDao extends Serializable
{
    /**
     * Method to save lastFetched
     * @param key is combination of profile type + "_" + id + "_" + pageId. eg - COMPANY_3234234_34234234
     * @param currValue - Should not be null
     * @param prevValue - Should not be null
     * @return
     */
    public boolean saveLastFetched( String key, String currValue, String prevValue);
    
    /**
     * Method to get lastFetched
     * @param key is combination of profile type + "_" + id + "_" + pageId. eg - COMPANY_3234234_34234234
     * @return
     */
    public String getLastFetched(String key);
    
    /**
     * Reset current to previous last fetched 
     * @param key
     * @return
     */
    public boolean resetLastFetched( String key);

    /**
     * Locks the twitter pageId for some seconds
     * @param secondsUntilReset
     * @param pageId
     */
    void setTwitterLock(int secondsUntilReset, String pageId);

    /**
     * Checks if the twitter lock on the given pageId has been resetted
     * @param pageId
     * @return
     */
    boolean isTwitterLockSet(String pageId);

    /** check is wait for next fetch is present in redis
     * @return
     */
    public boolean waitForNextFetch();
    
    /**
     * Set wati for next fetch in redis
     * @param seconds
     * @return
     */
    public boolean setWaitForNextFetch( int seconds );

    /**
     * Add key with TTL
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public boolean addWithExpire( String key, String value, int seconds );

    /**
     * Get TTL for given key
     * @param key
     * @return
     */
    public long getTTLForKey( String key);

    /**
     * Loock on facebook for pageid
     * @param pageId
     * @param secondsUntilReset
     */
    public void setFacebookLockForPage( String pageId, int secondsUntilReset );

    /**
     * Lock on facebook for accessToken
     * @param accessToken
     * @param secondsUntilReset
     */
    public void setFacebookLockForToken( String accessToken, int secondsUntilReset );
    
    /**
     * Lock on facebook for application
     * @param accessToken
     * @param secondsUntilReset
     */
    public void setFacebookLockForApplication( int secondsUntilReset );

    /**
     * check for facebook page lock
     * @param pageId
     * @return
     */
    boolean isFacebookPageLockSet( String pageId );

    /**
     * Check for facebook access token lock
     * @param accessToken
     * @return
     */
    boolean isFacebookTokenLockSet( String accessToken );

    /**
     * Check for facebook application lock
     * @return
     */
    boolean isFacebookApplicationLockSet();
    
    /**
     * Method to get SocialMonitor enabled companyIds
     * @return
     */
    public List<Long> getCompanyIdsForSM();
}
