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
	private static final int numOfThreads = 10;
	
	private ApplicationContext context;
	private ExecutorService facebookExecutor;
	private ExecutorService googleExecutor;
	private ExecutorService twitterExecutor;

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("Creating executors for social feed");
		facebookExecutor = Executors.newFixedThreadPool(numOfThreads);
		googleExecutor = Executors.newFixedThreadPool(numOfThreads);
		twitterExecutor = Executors.newFixedThreadPool(numOfThreads);
		LOG.info("Done Creating executors for social feed");
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
		GoogleFeedIngester googleFeedIngester = context.getBean(GoogleFeedIngester.class);
		googleFeedIngester.setCollectionName(collectionName);
		googleFeedIngester.setIden(ingestionEntity.getIden());
		googleFeedIngester.setToken(ingestionEntity.getSocialMediaTokens().getGoogleToken());
		googleExecutor.execute(googleFeedIngester);
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

	public void shutDownExecutors() {
		LOG.debug("Shutting down executors.");
		facebookExecutor.shutdown();
		googleExecutor.shutdown();
		twitterExecutor.shutdown();

		try {
			facebookExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			googleExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			twitterExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		LOG.debug("Shut down of executors complete.");
	}

	public void shutDownExecutorsNow() {
		LOG.debug("Shutting down executors Now.");
		facebookExecutor.shutdownNow();
		googleExecutor.shutdownNow();
		twitterExecutor.shutdownNow();

		try {
			facebookExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			googleExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			twitterExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		LOG.debug("Shut down of executors complete.");
	}
}