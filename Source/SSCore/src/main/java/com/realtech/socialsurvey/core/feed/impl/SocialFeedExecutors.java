package com.realtech.socialsurvey.core.feed.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.entities.FeedIngestionEntity;
import com.realtech.socialsurvey.core.exception.NoContextFoundException;

/**
 * Executors to manage thread for fetching social feed
 */
@Component
public class SocialFeedExecutors implements InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(SocialFeedExecutors.class);

	private int numOfThreads = 10;
	private ApplicationContext context;
	private ExecutorService twitterExecutor;
	private ExecutorService facebookExecutor;
	//private ExecutorService googleExecutor;
	//private ExecutorService yelpExecutor;

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("Creating executors for social feed");
		twitterExecutor = Executors.newFixedThreadPool(numOfThreads);
		facebookExecutor = Executors.newFixedThreadPool(numOfThreads);
		//googleExecutor = Executors.newFixedThreadPool(numOfThreads);
		//yelpExecutor = Executors.newFixedThreadPool(numOfThreads);
		LOG.info("Done Creating executors for social feed");
	}

	public void addTwitterProcessorToPool(FeedIngestionEntity ingestionEntity, String collectionName) throws NoContextFoundException {
		LOG.info("Adding twitter details to pool");
		if (context == null) {
			throw new NoContextFoundException("No Application context found");
		}
		
		TwitterFeedIngester twitterFeedIngester = context.getBean(TwitterFeedIngester.class);
		twitterFeedIngester.setCollectionName(collectionName);
		twitterFeedIngester.setIden(ingestionEntity.getIden());
		twitterFeedIngester.setToken(ingestionEntity.getSocialMediaTokens().getTwitterToken());
		twitterExecutor.execute(twitterFeedIngester);
	}

	public void addFacebookProcessorToPool(FeedIngestionEntity ingestionEntity, String collectionName) throws NoContextFoundException {
		LOG.info("Adding Facebook details to pool");
		if (context == null) {
			throw new NoContextFoundException("No Application context found");
		}
		
		FacebookFeedIngester facebookFeedIngester = context.getBean(FacebookFeedIngester.class);
		facebookFeedIngester.setCollectionName(collectionName);
		facebookFeedIngester.setIden(ingestionEntity.getIden());
		facebookFeedIngester.setToken(ingestionEntity.getSocialMediaTokens().getFacebookToken());
		facebookExecutor.execute(facebookFeedIngester);
	}

	public void addGoogleProcessorToPool(FeedIngestionEntity ingestionEntity, String collectionName) throws NoContextFoundException {
		LOG.info("Adding Google details to pool");
		if (context == null) {
			throw new NoContextFoundException("No Application context found");
		}
		GoogleFeedIngester googleFeedIngester =context.getBean(GoogleFeedIngester.class);
		googleFeedIngester.setCollectionName(collectionName);
		googleFeedIngester.setIden(ingestionEntity.getIden());
		googleFeedIngester.setToken(ingestionEntity.getSocialMediaTokens().getGoogleToken());
		twitterExecutor.execute(googleFeedIngester);
	}

	public void addYelpProcessorToPool(FeedIngestionEntity ingestionEntity, String collectionName) throws NoContextFoundException {
		LOG.info("Adding Yelp details to pool");
		if (context == null) {
			throw new NoContextFoundException("No Application context found");
		}
		// TODO
	}

	public void shutDownExecutors() {
		LOG.debug("Shutting down executors.");
		twitterExecutor.shutdown();
		facebookExecutor.shutdown();
		//googleExecutor.shutdown();
		//yelpExecutor.shutdown();

		try {
			twitterExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			facebookExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			//googleExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			//yelpExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		LOG.debug("Shut down of executors complete.");
	}

	public void shutDownExecutorsNow() {
		LOG.debug("Shutting down executors Now.");
		twitterExecutor.shutdownNow();
		facebookExecutor.shutdownNow();
		//googleExecutor.shutdownNow();
		//yelpExecutor.shutdownNow();

		try {
			twitterExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			facebookExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			//googleExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			//yelpExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		LOG.debug("Shut down of executors complete.");
	}
}