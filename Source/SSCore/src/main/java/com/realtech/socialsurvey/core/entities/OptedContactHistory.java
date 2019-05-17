package com.realtech.socialsurvey.core.entities;

public class OptedContactHistory {
	
    private int status;
    private int level;
    private long modifiedOn;
    private int modifiedBy;
    private String incomingMessageBody;
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public long getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(long modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public int getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(int modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public String getIncomingMessageBody() {
		return incomingMessageBody;
	}
	public void setIncomingMessageBody(String incomingMessageBody) {
		this.incomingMessageBody = incomingMessageBody;
	}
}
