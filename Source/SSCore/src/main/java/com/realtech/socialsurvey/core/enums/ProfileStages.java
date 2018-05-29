package com.realtech.socialsurvey.core.enums;

public enum ProfileStages {

	LINKEDIN_PRF(1), FACEBOOK_PRF(2), GOOGLE_PRF(3), TWITTER_PRF(4), YELP_PRF(5), LICENSE_PRF(6), HOBBIES_PRF(7), ACHIEVEMENTS_PRF(8), ZILLOW_PRF(9),
	 INSTAGRAM_PRF(10);

	private final int order;

	ProfileStages(int order) {
		this.order = order;
	}

	public int getOrder() {
		return this.order;
	}
}
