package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the FEED_STATUS database table.
 */
@Entity
@Table(name = "FEED_STATUS")
@NamedQuery(name = "FeedStatus.findAll", query = "SELECT f FROM FeedStatus f")
public class FeedStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FEED_STATUS_ID")
	private long feedStatusId;

	@Column(name = "COMPANY_ID")
	private long companyId;

	@Column(name = "REGION_ID")
	private long regionId;

	@Column(name = "BRANCH_ID")
	private long branchId;

	@Column(name = "AGENT_ID")
	private long agentId;

	@Column(name = "FEED_SOURCE")
	private String feedSource;

	@Column(name = "LAST_FETCHED_POST_ID")
	private String lastFetchedPostId;

	@Column(name = "LAST_FETCHED_TILL")
	private Timestamp lastFetchedTill;

	@Column(name = "RETRIES")
	private long retries;

	@Column(name = "REMINDER_SENT_ON")
	private Timestamp reminderSentOn;

	public long getFeedStatusId() {
		return feedStatusId;
	}

	public void setFeedStatusId(long feedStatusId) {
		this.feedStatusId = feedStatusId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public long getRegionId() {
		return regionId;
	}

	public void setRegionId(long regionId) {
		this.regionId = regionId;
	}

	public long getBranchId() {
		return branchId;
	}

	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}

	public long getAgentId() {
		return agentId;
	}

	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}

	public String getFeedSource() {
		return feedSource;
	}

	public void setFeedSource(String feedSource) {
		this.feedSource = feedSource;
	}

	public String getLastFetchedPostId() {
		return lastFetchedPostId;
	}

	public void setLastFetchedPostId(String lastFetchedPostId) {
		this.lastFetchedPostId = lastFetchedPostId;
	}

	public Timestamp getLastFetchedTill() {
		return lastFetchedTill;
	}

	public void setLastFetchedTill(Timestamp lastFetchedTill) {
		this.lastFetchedTill = lastFetchedTill;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public long getRetries() {
		return retries;
	}

	public void setRetries(long retries) {
		this.retries = retries;
	}

	public Timestamp getReminderSentOn() {
		return reminderSentOn;
	}

	public void setReminderSentOn(Timestamp reminderSentOn) {
		this.reminderSentOn = reminderSentOn;
	}

	@Override
	public String toString() {
		return "FeedStatus [feedStatusId=" + feedStatusId + ", companyId=" + companyId + ", regionId=" + regionId + ", branchId=" + branchId
				+ ", agentId=" + agentId + ", feedSource=" + feedSource + ", lastFetchedPostId=" + lastFetchedPostId + ", lastFetchedTill="
				+ lastFetchedTill + "]";
	}
}