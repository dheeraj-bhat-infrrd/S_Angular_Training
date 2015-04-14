package com.realtech.socialsurvey.core.feed.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import com.realtech.socialsurvey.core.entities.TwitterToken;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.feed.SocialNetworkDataProcessor;

@Component
public class TwitterFeedProcessorImpl implements SocialNetworkDataProcessor<Status, TwitterToken> {

	private static final Logger LOG = LoggerFactory.getLogger(TwitterFeedProcessorImpl.class);
	
	@Override
	public List<Status> fetchFeed(long iden, String organizationUnit, TwitterToken token) throws NonFatalException {
		// Get the data for the organization unit
		LOG.info("Getting tweets for "+organizationUnit+" with id: "+iden);
		
		return null;
	}

	@Override
	public void processFeed(List<Status> feed, String organizationUnit) throws NonFatalException {
		// TODO Auto-generated method stub
		LOG.info("Process tweets for organizationUnit "+organizationUnit);
	}

	@Override
	public void updateLastProcessedTimestamp(long iden, String organizationUnit) throws NonFatalException {
		// TODO Auto-generated method stub
		
	}

}
