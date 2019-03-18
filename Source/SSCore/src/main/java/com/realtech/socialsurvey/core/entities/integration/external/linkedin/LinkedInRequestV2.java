package com.realtech.socialsurvey.core.entities.integration.external.linkedin;

import java.io.Serializable;

public class LinkedInRequestV2 implements Serializable{

	private static final long serialVersionUID = 1L;

	private LinkedInText text;
	private LinkedInContent content;
	private LinkedInVisibility visibility;
	private String owner;

	public LinkedInText getText() {
		return text;
	}

	public void setText(LinkedInText text) {
		this.text = text;
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

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

}
