/**
 * 
 */
package com.realtech.socialsurvey.compute.entity;

import java.io.Serializable;

/**
 * @author Subhrajit
 *
 */
public class SurveyInvitationEmailCountMonth implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private long agentId;
	private long companyId;
	private int month;
	private int year;
	private long attempted;
	private long delivered;
	private long differed;
	private long blocked;
	private long opened;
	private long spamed;
	private long unsubscribed;
	private long bounced;
	private long linkClicked;
	private long received;
	private long dropped;
	private String agentName;
	private String emailId;
	private String branchName;
	private String regionName;
	
	public long getAgentId() {
		return agentId;
	}
	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}
	public long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public long getAttempted() {
		return attempted;
	}
	public void setAttempted(long attempted) {
		this.attempted = attempted;
	}
	public long getDelivered() {
		return delivered;
	}
	public void setDelivered(long delivered) {
		this.delivered = delivered;
	}
	public long getDiffered() {
		return differed;
	}
	public void setDiffered(long differed) {
		this.differed = differed;
	}
	public long getBlocked() {
		return blocked;
	}
	public void setBlocked(long blocked) {
		this.blocked = blocked;
	}
	public long getOpened() {
		return opened;
	}
	public void setOpened(long opened) {
		this.opened = opened;
	}
	public long getSpamed() {
		return spamed;
	}
	public void setSpamed(long spamed) {
		this.spamed = spamed;
	}
	public long getUnsubscribed() {
		return unsubscribed;
	}
	public void setUnsubscribed(long unsubscribed) {
		this.unsubscribed = unsubscribed;
	}
	public long getBounced() {
		return bounced;
	}
	public void setBounced(long bounced) {
		this.bounced = bounced;
	}
	public long getLinkClicked() {
		return linkClicked;
	}
	public void setLinkClicked(long linkClicked) {
		this.linkClicked = linkClicked;
	}
	public long getReceived() {
		return received;
	}
	public void setReceived(long received) {
		this.received = received;
	}
	public long getDropped() {
		return dropped;
	}
	public void setDropped(long dropped) {
		this.dropped = dropped;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	@Override
	public String toString() {
		return "SurveyInvitationEmailCountMonth [id=" + id + ", agentId=" + agentId + ", companyId=" + companyId
				+ ", month=" + month + ", year=" + year + ", attempted=" + attempted + ", delivered=" + delivered
				+ ", differed=" + differed + ", blocked=" + blocked + ", opened=" + opened + ", spamed=" + spamed
				+ ", unsubscribed=" + unsubscribed + ", bounced=" + bounced + ", linkClicked=" + linkClicked
				+ ", received=" + received + ", dropped=" + dropped + ", agentName=" + agentName + ", emailId="
				+ emailId + "]";
	}
}