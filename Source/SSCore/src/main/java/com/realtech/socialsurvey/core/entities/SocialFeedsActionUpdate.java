package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.Set;

import com.realtech.socialsurvey.core.enums.SocialFeedActionType;
import com.realtech.socialsurvey.core.enums.SocialFeedStatus;
import com.realtech.socialsurvey.core.enums.TextActionType;

public class SocialFeedsActionUpdate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Set<String> postIds;
	private SocialFeedActionType actionType;
	private TextActionType textActionType;
	private String text;
	private String userName;
	private String userEmailId;
	private long createdOn;
	private String macroId = "";


	public Set<String> getPostIds()
    {
        return postIds;
    }

    public void setPostIds( Set<String> postIds )
    {
        this.postIds = postIds;
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

	public String getUserEmailId()
    {
        return userEmailId;
    }

    public void setUserEmailId( String userEmailId )
    {
        this.userEmailId = userEmailId;
    }

    public SocialFeedActionType getActionType()
    {
        return actionType;
    }

    public void setActionType( SocialFeedActionType actionType )
    {
        this.actionType = actionType;
    }

    @Override
    public String toString()
    {
        return "SocialFeedsActionUpdate [postIds=" + postIds + ", actionType=" + actionType + ", textActionType="
            + textActionType + ", text=" + text + ", userName=" + userName + ", userEmailId=" + userEmailId + ", createdOn="
            + createdOn + ", macroId=" + macroId + "]";
    }

}
