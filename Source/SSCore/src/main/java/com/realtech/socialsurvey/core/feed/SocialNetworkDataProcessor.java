package com.realtech.socialsurvey.core.feed;

import java.util.List;
import com.realtech.socialsurvey.core.exception.NonFatalException;


/**
 * Interface to fetch data from Social Network
 */
public interface SocialNetworkDataProcessor<K, V>
{
    /**
     * Pre processor hook
     * 
     * @param iden
     * @param organizationUnit
     * @param token
     */
    public void preProcess( long iden, String collection, V token );


    /**
     * Fetches feed for the provided identifier. The identifier could be company, region, branch or
     * agent id
     * 
     * @param iden
     * @param organizationUnit
     * @param token
     * @return
     * @throws NonFatalException
     */
    public List<K> fetchFeed( long iden, String collection, V token ) throws NonFatalException;


    /**
     * Processes the list of feed.
     * 
     * @param feed
     * @param organizationUnit
     * @return 
     * @throws NonFatalException
     */
    public boolean processFeed( List<K> feed, String collection ) throws NonFatalException;


    /**
     * Post processor hook
     * 
     * @param iden
     * @param organizationUnit
     * @throws NonFatalException
     */
    public void postProcess( long iden, String collection, boolean recordInserted ) throws NonFatalException;
}