package com.realtech.socialsurvey.core.entities;

import java.util.List;

/**
 * Settings object for Social links for a profile
 */
public class SocialLinksSettings {

	private String facebook;
	private String linkedin;
	private String googleplus;
	private String twitter;
	private String yelp;
	private List<String> personal;
	private List<String> others;

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getLinkedin() {
		return linkedin;
	}

	public void setLinkedin(String linkedin) {
		this.linkedin = linkedin;
	}

	public String getGoogleplus() {
		return googleplus;
	}

	public void setGoogleplus(String googleplus) {
		this.googleplus = googleplus;
	}

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public String getYelp() {
		return yelp;
	}

	public void setYelp(String yelp) {
		this.yelp = yelp;
	}

	public List<String> getPersonal() {
		return personal;
	}

	public void setPersonal(List<String> personal) {
		this.personal = personal;
	}

	public List<String> getOthers() {
		return others;
	}

	public void setOthers(List<String> others) {
		this.others = others;
	}

	@Override
	public String toString() {
		return "facebook: " + facebook + "\t linkedin: " + linkedin + "\t googleplus: " + googleplus + "\t twitter: " + twitter + "\t yelp: " + yelp
				+ "\t personal: " + (personal != null ? personal.toString() : "null") + "\t others: " + (others != null ? others.toString() : "null");
	}

}
