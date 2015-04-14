package com.realtech.socialsurvey.core.feed;

import java.util.List;
import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Interface to fetch data from Social Network
 *
 */
public interface SocialNetworkDataProcessor<K, V> {

	/**
	 * Fetches feed for the provided identifier. The identifier could be company, region, branch or agent id
	 * @param iden
	 * @param organizationUnit
	 * @param token
	 * @return
	 * @throws NonFatalException
	 */
	public List<K> fetchFeed(long iden, String organizationUnit, V token) throws NonFatalException;
	
	/**
	 * Processes the list of feed.
	 * @param feed
	 * @param organizationUnit
	 * @throws NonFatalException
	 */
	public void processFeed(List<K> feed, String organizationUnit) throws NonFatalException;
	
	/**
	 * Updates the last process time
	 * @param iden
	 * @param organizationUnit
	 * @throws NonFatalException
	 */
	public void updateLastProcessedTimestamp(long iden, String organizationUnit) throws NonFatalException;
}
