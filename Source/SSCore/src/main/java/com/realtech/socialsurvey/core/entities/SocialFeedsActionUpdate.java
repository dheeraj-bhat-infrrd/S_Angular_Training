package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.realtech.socialsurvey.core.enums.SocialFeedStatus;
import com.realtech.socialsurvey.core.enums.TextActionType;

public class SocialFeedsActionUpdate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> postIds;
	private boolean flagged;
	private SocialFeedStatus status;
	private TextActionType textActionType;
	private String text;
	private String userName;
	private long createdOn;
	private String macroId = "";

	public List<String> getPostIds() {
		return postIds;
	}

	public void setPostIds(List<String> postIds) {
		this.postIds = postIds;
	}

	public boolean isFlagged() {
		return flagged;
	}

	public void setFlagged(boolean flagged) {
		this.flagged = flagged;
	}

	public SocialFeedStatus getStatus() {
		return status;
	}

	public void setStatus(SocialFeedStatus status) {
		this.status = status;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public String getMacroId() {
		return macroId;
	}

	public void setMacroId(String macroId) {
		this.macroId = macroId;
	}

	@Override
	public String toString() {
		return "SocialFeedsActionUpdate [postIds=" + postIds + ", flagged=" + flagged + ", status=" + status
				+ ", textActionType=" + textActionType + ", text=" + text + ", userName=" + userName + ", createdOn="
				+ createdOn + ", macroId=" + macroId + "]";
	}

}
