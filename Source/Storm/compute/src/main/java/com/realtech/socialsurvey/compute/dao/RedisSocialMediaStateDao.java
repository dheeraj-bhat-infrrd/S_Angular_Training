package com.realtech.socialsurvey.compute.dao;

import java.io.Serializable;

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
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public boolean addWithExpire( String key, String value, int seconds );

    /**
     * @param key
     * @return
     */
    public long getTTLForKey( String key);
}
