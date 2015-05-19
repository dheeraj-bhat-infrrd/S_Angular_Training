package com.realtech.socialsurvey.core.commons;

public enum ProfileStages {

	FACEBOOK_PRF(1), GOOGLE_PRF(2), TWITTER_PRF(3), YELP_PRF(4);

	private final int order;

	ProfileStages(int order) {
		this.order = order;
	}

	public int getOrder() {
		return this.order;
	}
}
