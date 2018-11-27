package com.realtech.socialsurvey.core.entities.integration.external.linkedin;
import java.io.Serializable;

/**
 * @author manish
 *
 */
public class LinkedInVisibility implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
}