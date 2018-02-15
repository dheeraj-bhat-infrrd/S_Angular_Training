package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.List;

public class SocialMonitorResponseData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long count;
	private String status;
	private List<SocialMonitorFeedData> socialMonitorFeedData;

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<SocialMonitorFeedData> getSocialMonitorFeedData() {
		return socialMonitorFeedData;
	}

	public void setSocialMonitorFeedData(List<SocialMonitorFeedData> socialMonitorFeedData) {
		this.socialMonitorFeedData = socialMonitorFeedData;
	}

	@Override
	public String toString() {
		return "SocialMonitorResponseData [count=" + count + ", status=" + status + ", socialMonitorFeedData="
				+ socialMonitorFeedData + "]";
	}

}
