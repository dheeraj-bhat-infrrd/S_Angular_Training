package com.realtech.socialsurvey.compute.dao;

import java.io.Serializable;

/**
 * @author manish
 *
 */
public interface RedisSinceRecordFetchedDao extends Serializable
{
    /**
     * Method to save lastFetched
     * @param key is combination of profile type + "_" + id + "_" + pageId. eg - COMPANY_3234234_34234234
     * @param value
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
}
