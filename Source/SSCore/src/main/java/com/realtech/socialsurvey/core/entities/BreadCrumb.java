package com.realtech.socialsurvey.core.entities;

public class BreadCrumb {
	/**
	 * Holds the details of the breadcrumb for the profile
	 */

	public String breadCrumbProfile;
	public String breadCrumbUrl;

	public String getBreadCrumbProfile() {
		return breadCrumbProfile;
	}

	public void setBreadCrumbProfile(String breadCrumbProfile) {
		this.breadCrumbProfile = breadCrumbProfile;
	}

	public String getBreadCrumbUrl() {
		return breadCrumbUrl;
	}

	public void setBreadCrumbUrl(String breadCrumbUrl) {
		this.breadCrumbUrl = breadCrumbUrl;
	}

	@Override
	public String toString() {
		return "breadcrumbprofile: " + breadCrumbProfile + "\t breadcrumburl: " + breadCrumbUrl;
	}

}
