package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;

import com.realtech.socialsurvey.core.enums.SocialFeedStatus;
import com.realtech.socialsurvey.core.enums.TextActionType;

public class Actions implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SocialFeedStatus socialFeedStatus;
	private TextActionType textActionType;
	private String text;

	public SocialFeedStatus getSocialFeedStatus() {
		return socialFeedStatus;
	}

	public void setSocialFeedStatus(SocialFeedStatus socialFeedStatus) {
		this.socialFeedStatus = socialFeedStatus;
	}

	public TextActionType getTextActionType() {
		return textActionType;
	}

	public void setTextActionType(TextActionType textActionType) {
		this.textActionType = textActionType;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "Actions [socialFeedStatus=" + socialFeedStatus + ", textActionType="
				+ textActionType + ", text=" + text + "]";
	}

}
