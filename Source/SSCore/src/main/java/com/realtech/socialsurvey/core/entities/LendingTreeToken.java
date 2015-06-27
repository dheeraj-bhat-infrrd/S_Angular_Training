package com.realtech.socialsurvey.core.entities;

public class LendingTreeToken {

	private String lendingTreeId;
	private String lendingTreeProfileLink;

	public String getLendingTreeId() {
		return lendingTreeId;
	}

	public void setLendingTreeId(String lendingTreeId) {
		this.lendingTreeId = lendingTreeId;
	}

	public String getLendingTreeProfileLink() {
		return lendingTreeProfileLink;
	}

	public void setLendingTreeProfileLink(String lendingTreeProfileLink) {
		this.lendingTreeProfileLink = lendingTreeProfileLink;
	}

	@Override
	public String toString() {
		return "YelpToken [lendingTreeId=" + lendingTreeId + ", lendingTreeProfileLink=" + lendingTreeProfileLink + "]";
	}
}