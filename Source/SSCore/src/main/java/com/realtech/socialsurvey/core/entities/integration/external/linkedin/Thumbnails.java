package com.realtech.socialsurvey.core.entities.integration.external.linkedin;

import java.io.Serializable;

public class Thumbnails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String resolvedUrl;

	public String getResolvedUrl() {
		return resolvedUrl;
	}

	public void setResolvedUrl(String resolvedUrl) {
		this.resolvedUrl = resolvedUrl;
	}

}
