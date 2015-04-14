package com.realtech.socialsurvey.core.feed.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.entities.FeedIngestionEntity;

/**
 * Executors to manage thread for fetching social feed
 *
 */
@Component
public class SocialFeedExecutors implements InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(SocialFeedExecutors.class);
	
	private int numOfThreads = 10;
	
	private ExecutorService twitterExecutor;
	
	@Autowired
	private TwitterFeedIngester twitterFeedIngester;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("Creating executors for social feed");
		twitterExecutor = Executors.newFixedThreadPool(numOfThreads);
		// TODO Create executors for each social media type
		LOG.info("Done Creating executors for social feed");
	}

	public void addTwitterProcessorToPool(FeedIngestionEntity ingestionEntity, String collectionName){
		LOG.info("Adding twitter details to pool");
		twitterFeedIngester.setCollectionName(collectionName);
		twitterFeedIngester.setIden(ingestionEntity.getIden());
		twitterFeedIngester.setToken(ingestionEntity.getSocialMediaTokens().getTwitterToken());
		twitterExecutor.execute(twitterFeedIngester);
	}
	
	public void shutDownExecutors(){
		LOG.debug("Shutting down executors.");
		// TODO: shutdown other executors too
		twitterExecutor.shutdown();
		try {
			// TODO: awaitTermination for other executors too
			twitterExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOG.debug("Shut down of executors complete.");
	}
	
}
