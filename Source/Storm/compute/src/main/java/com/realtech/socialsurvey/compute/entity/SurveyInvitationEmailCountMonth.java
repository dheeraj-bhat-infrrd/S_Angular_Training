/**
 * 
 */
package com.realtech.socialsurvey.compute.entity;

/**
 * @author Subhrajit
 *
 */
public class SurveyInvitationEmailCountMonth {
	
	private int id;
	private int agentId;
	private int companyId;
	private int month;
	private int year;
	private int attempted;
	private int delivered;
	private int differed;
	private int blocked;
	private int opened;
	private int spamed;
	private int unsubscribed;
	private int bounced;
	private int linkClicked;
	private int received;
	public int getAgentId() {
		return agentId;
	}
	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}
	public int getCompanyId() {
		return companyId;
	}
	public void setCompanyId(int companyId) {
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
	public int getAttempted() {
		return attempted;
	}
	public void setAttempted(int attempted) {
		this.attempted = attempted;
	}
	public int getDelivered() {
		return delivered;
	}
	public void setDelivered(int delivered) {
		this.delivered = delivered;
	}
	public int getDiffered() {
		return differed;
	}
	public void setDiffered(int differed) {
		this.differed = differed;
	}
	public int getBlocked() {
		return blocked;
	}
	public void setBlocked(int blocked) {
		this.blocked = blocked;
	}
	public int getOpened() {
		return opened;
	}
	public void setOpened(int opened) {
		this.opened = opened;
	}
	public int getSpamed() {
		return spamed;
	}
	public void setSpamed(int spamed) {
		this.spamed = spamed;
	}
	public int getUnsubscribed() {
		return unsubscribed;
	}
	public void setUnsubscribed(int unsubscribed) {
		this.unsubscribed = unsubscribed;
	}
	public int getBounced() {
		return bounced;
	}
	public void setBounced(int bounced) {
		this.bounced = bounced;
	}
	public int getLinkClicked() {
		return linkClicked;
	}
	public void setLinkClicked(int linkClicked) {
		this.linkClicked = linkClicked;
	}
	public int getReceived() {
		return received;
	}
	public void setReceived(int received) {
		this.received = received;
	}
	@Override
	public String toString() {
		return "SurveyInvitationEmailCountMonth [id=" + id + ", agentId=" + agentId + ", companyId=" + companyId
				+ ", month=" + month + ", year=" + year + ", attempted=" + attempted + ", delivered=" + delivered
				+ ", differed=" + differed + ", blocked=" + blocked + ", opened=" + opened + ", spamed=" + spamed
				+ ", unsubscribed=" + unsubscribed + ", bounced=" + bounced + ", linkClicked=" + linkClicked
				+ ", received=" + received + "]";
	}
	
	
	

}
