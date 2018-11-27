package com.realtech.socialsurvey.core.entities.integration.external.linkedin;

import java.io.Serializable;

/**
 * @author manish
 *
 */
public class LinkedInRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String comment;
	private LinkedInContent content;
	private LinkedInVisibility visibility;

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the content
	 */
	public LinkedInContent getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(LinkedInContent content) {
		this.content = content;
	}

	/**
	 * @return the visibility
	 */
	public LinkedInVisibility getVisibility() {
		return visibility;
	}

	/**
	 * @param visibility the visibility to set
	 */
	public void setVisibility(LinkedInVisibility visibility) {
		this.visibility = visibility;
	}
}