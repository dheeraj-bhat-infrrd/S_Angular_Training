/**
 * 
 */
package com.realtech.socialsurvey.core.vo;

/**
 * @author Subhrajit
 *
 */
public class SurveyInvitationEmailCountVO {
	
	private String agentName;
	private String emailId;
	private String branchName;
	private String regionName;
	private long received;
	private long attempted;
	private long delivered;
	private long bounced;
	private long differed;
	private long opened;
	private long linkClicked;
	private long dropped;
	private int month;
	private int year;
	
	
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
	public long getReceived() {
		return received;
	}
	public void setReceived(long received) {
		this.received = received;
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
	public long getBounced() {
		return bounced;
	}
	public void setBounced(long bounced) {
		this.bounced = bounced;
	}
	public long getDiffered() {
		return differed;
	}
	public void setDiffered(long differed) {
		this.differed = differed;
	}
	public long getOpened() {
		return opened;
	}
	public void setOpened(long opened) {
		this.opened = opened;
	}
	public long getLinkClicked() {
		return linkClicked;
	}
	public void setLinkClicked(long linkClicked) {
		this.linkClicked = linkClicked;
	}
	public long getDropped() {
		return dropped;
	}
	public void setDropped(long dropped) {
		this.dropped = dropped;
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
	@Override
	public String toString() {
		return "SurveyInvitationEmailCountVO [agentName=" + agentName + ", emailId=" + emailId + ", branchName="
				+ branchName + ", regionName=" + regionName + ", received=" + received + ", attempted=" + attempted
				+ ", delivered=" + delivered + ", bounced=" + bounced + ", differed=" + differed + ", opened=" + opened
				+ ", linkClicked=" + linkClicked + ", dropped=" + dropped + ", month=" + month + ", year=" + year + "]";
	}
	

}
