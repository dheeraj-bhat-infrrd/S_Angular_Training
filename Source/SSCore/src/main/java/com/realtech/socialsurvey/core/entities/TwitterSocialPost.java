package com.realtech.socialsurvey.core.entities;

import twitter4j.Status;

public class TwitterSocialPost extends SocialPost {

	private Status tweet;

	public Status getTweet() {
		return tweet;
	}

	public void setTweet(Status tweet) {
		this.tweet = tweet;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}