/**
 * 
 */
package com.realtech.socialsurvey.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Subhrajit
 *
 */
@Entity
@Table(name = "invitation_mail_count_month")
public class SurveyInvitationEmailCountMonth {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "invitation_mail_count_month_id")
	@JsonIgnoreProperties(ignoreUnknown = true)
	private long id;
	
	@Column(name = "agent_id")
	private long agentId;
	
	@Column(name = "company_id")
	private long companyId;
	
	@Column(name = "agent_name")
	private String agentName;
	
	@Column(name = "agent_email")
	private String emailId;
	
	@Column(name = "month")
	private int month;
	
	@Column(name = "year")
	private int year;
	
	@Column(name = "attempted_count")
	private long attempted;
	
	@Column(name = "delivered")
	private long delivered;
	
	@Column(name = "deffered")
	private long differed;
	
	@Column(name = "blocked")
	private long blocked;
	
	@Column(name = "opened")
	private long opened;
	
	@Column(name = "spam")
	private long spamed;
	
	@Column(name = "unsubscribed")
	private long unsubscribed;
	
	@Column(name = "bounced")
	private long bounced;
	
	@Column(name = "link_clicked")
	private long linkClicked;
	
	@Column(name = "received")
	private long received;
	
	@Column(name = "dropped")
	private long dropped;
	
	@Transient
	private String branchName;
	@Transient
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
	public long getMonth() {
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
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
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
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	@Override
	public String toString() {
		return "SurveyInvitationEmailCountMonth [id=" + id + ", agentId=" + agentId + ", companyId=" + companyId
				+ ", agentName=" + agentName + ", emailId=" + emailId + ", month=" + month + ", year=" + year
				+ ", attempted=" + attempted + ", delivered=" + delivered + ", differed=" + differed + ", blocked="
				+ blocked + ", opened=" + opened + ", spamed=" + spamed + ", unsubscribed=" + unsubscribed
				+ ", bounced=" + bounced + ", linkClicked=" + linkClicked + ", received=" + received + ", dropped="
				+ dropped + ", branchName=" + branchName + ", regionName=" + regionName
				+ "]";
	}
}