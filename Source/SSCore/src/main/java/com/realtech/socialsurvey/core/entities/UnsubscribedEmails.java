/**
 * 
 */
package com.realtech.socialsurvey.core.entities;

/**
 * Entity class for the unsubscribed_emails mongo collection.
 * @author Subhrajit
 *
 */
public class UnsubscribedEmails {
	
	private String _id;
	private long companyId;
	private long agentId;
	private String emailId;
	private long createdOn;
	private long modifiedOn;
	private int status;
	private int level;
	
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}
	public long getAgentId() {
		return agentId;
	}
	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public long getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}
	public long getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(long modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
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
	@Override
	public String toString() {
		return "UnsubscribedEmails [_id=" + _id + ", companyId=" + companyId + ", agentId=" + agentId + ", emailId="
				+ emailId + ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn + ", status=" + status
				+ ", level=" + level + "]";
	}
	

}
