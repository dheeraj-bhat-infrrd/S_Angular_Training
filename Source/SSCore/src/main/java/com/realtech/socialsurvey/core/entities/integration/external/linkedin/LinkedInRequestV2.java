package com.realtech.socialsurvey.core.entities.integration.external.linkedin;

import java.io.Serializable;

public class LinkedInRequestV2 implements Serializable{

	private static final long serialVersionUID = 1L;

	private LinkedInText text;
	private LinkedInV2Content content;
	private LinkedInDistributionTarget distribution;
	
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
	public LinkedInV2Content getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(LinkedInV2Content content) {
		this.content = content;
	}


	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public LinkedInDistributionTarget getDistribution() {
		return distribution;
	}

	public void setDistribution(LinkedInDistributionTarget distribution) {
		this.distribution = distribution;
	}

}
