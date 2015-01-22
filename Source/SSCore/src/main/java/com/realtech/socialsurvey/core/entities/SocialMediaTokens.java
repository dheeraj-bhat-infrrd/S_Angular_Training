package com.realtech.socialsurvey.core.entities;

public class SocialMediaTokens {

	private FacebookToken facebookToken;
	private TwitterToken twitterToken;
	private LinkdenInToken linkdenInToken;
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

	public LinkdenInToken getLinkdenInToken() {
		return linkdenInToken;
	}

	public void setLinkdenInToken(LinkdenInToken linkdenInToken) {
		this.linkdenInToken = linkdenInToken;
	}

	public YelpToken getYelpToken() {
		return yelpToken;
	}

	public void setYelpToken(YelpToken yelpToken) {
		this.yelpToken = yelpToken;
	}

	@Override
	public String toString() {
		return "SocialMediaTokens [facebookToken=" + facebookToken + ", twitterToken=" + twitterToken + ", linkdenInToken=" + linkdenInToken
				+ ", yelpToken=" + yelpToken + "]";
	}
	
	

}
