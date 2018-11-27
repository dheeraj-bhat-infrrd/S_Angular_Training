package com.realtech.socialsurvey.core.entities.integration.external.linkedin;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * @author manish
 *
 */
public class LinkedInContent implements Serializable {

	private static final long serialVersionUID = 1L;

	private String title;
	private String description;
	@SerializedName("submitted-url")
	private String submittedUrl;
	@SerializedName("submitted-image-url")
	private String submittedImageUrl;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the submittedUrl
	 */
	public String getSubmittedUrl() {
		return submittedUrl;
	}

	/**
	 * @param submittedUrl the submittedUrl to set
	 */
	public void setSubmittedUrl(String submittedUrl) {
		this.submittedUrl = submittedUrl;
	}

	/**
	 * @return the submittedImageUrl
	 */
	public String getSubmittedImageUrl() {
		return submittedImageUrl;
	}

	/**
	 * @param submittedImageUrl the submittedImageUrl to set
	 */
	public void setSubmittedImageUrl(String submittedImageUrl) {
		this.submittedImageUrl = submittedImageUrl;
	}
}