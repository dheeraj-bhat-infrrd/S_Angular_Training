package com.realtech.socialsurvey.core.entities;

public class ZillowToken {

	private String zillowId;
	private String zillowProfileLink;
	private String zillowScreenName;
	private String zillowEmailAddress;
	private String lastUpdated;

	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getZillowId() {
		return zillowId;
	}

	public void setZillowId(String zillowId) {
		this.zillowId = zillowId;
	}

	public String getZillowProfileLink() {
		return zillowProfileLink;
	}

	public void setZillowProfileLink(String zillowProfileLink) {
		this.zillowProfileLink = zillowProfileLink;
	}

	public String getZillowScreenName()
    {
        return zillowScreenName;
    }

    public void setZillowScreenName( String zillowScreenName )
    {
        this.zillowScreenName = zillowScreenName;
    }

    public String getZillowEmailAddress()
    {
        return zillowEmailAddress;
    }

    public void setZillowEmailAddress( String zillowEmailAddress )
    {
        this.zillowEmailAddress = zillowEmailAddress;
    }

    @Override
	public String toString() {
		return "ZillowToken [zillowId=" + zillowId + ", zillowProfileLink=" + zillowProfileLink + "]";
	}
}