package com.realtech.socialsurvey.core.entities;

public class SocialMediaTokens {

	private FacebookToken facebookToken;
	private TwitterToken twitterToken;
	private LinkedInToken linkedInToken;
	private YelpToken yelpToken;

	public FacebookToken getFacebookToken() {
		return facebookToken;
	}

	public void setFacebookToken(FacebookToken facebookToken) {
		this.facebookToken = facebookToken;
	}

	public TwitterToken getTwitterToken() {
		return twitterToken;
	}

	public void setTwitterToken(TwitterToken twitterToken) {
		this.twitterToken = twitterToken;
	}

	public LinkedInToken getLinkedInToken() {
		return linkedInToken;
	}

	public void setLinkedInToken(LinkedInToken linkedInToken) {
		this.linkedInToken = linkedInToken;
	}

	public YelpToken getYelpToken() {
		return yelpToken;
	}

	public void setYelpToken(YelpToken yelpToken) {
		this.yelpToken = yelpToken;
	}

	@Override
	public String toString() {
		return "SocialMediaTokens [facebookToken=" + facebookToken + ", twitterToken=" + twitterToken + ", linkdenInToken=" + linkedInToken
				+ ", yelpToken=" + yelpToken + "]";
	}

}
