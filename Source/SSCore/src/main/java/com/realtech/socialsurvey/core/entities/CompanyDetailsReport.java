/**
 * 
 */
package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Subhrajit
 *
 */
@Entity
@Table(name = "company_details_report")
public class CompanyDetailsReport implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "company_details_report_id")
	private String companyDetailsReportId;
	
	@Column(name = "company_id")
	private Integer companyId;
	
	@Column(name = "company")
	private String companyName;
	
	@Column(name = "user_count")
	private Integer userCount;
	
	@Column(name = "verified_user_count")
	private Integer verifiedUserCount;
	
	@Column(name = "verified_percent")
	private Double verifiedPercent;
	
	@Column(name = "region_count")
	private Integer regionCount;
	
	@Column(name = "branch_count")
	private Integer branchCount;
	
	@Column(name = "completion_rate")
	private Double completionRate;
	
	@Column(name = "verified_gmb")
	private Integer verifiedGmb;
	
	@Column(name = "missing_gmb")
	private Integer missingGmb;
	
	@Column(name = "mismatch_count")
	private Integer mismatchCount;
	
	@Column(name = "missing_photo_count")
	private Integer missingPhotoCount;
	
	@Column(name = "missing_url_count")
	private Integer missingURLCount;
	
	@Column(name = "facebook_connection_count")
	private Integer facebookConnectionCount;
	
	@Column(name = "twitter_connection_count")
	private Integer twitterConnectionCount;
	
	@Column(name = "linkedin_connection_count")
	private Integer linkedinConnectionCount;

	public String getCompanyDetailsReportId() {
		return companyDetailsReportId;
	}

	public void setCompanyDetailsReportId(String companyDetailsReportId) {
		this.companyDetailsReportId = companyDetailsReportId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getUserCount() {
		return userCount;
	}

	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}

	public Integer getVerifiedUserCount() {
		return verifiedUserCount;
	}

	public void setVerifiedUserCount(Integer verifiedUserCount) {
		this.verifiedUserCount = verifiedUserCount;
	}

	public Double getVerifiedPercent() {
		return verifiedPercent;
	}

	public void setVerifiedPercent(Double verifiedPercent) {
		this.verifiedPercent = verifiedPercent;
	}

	public Integer getRegionCount() {
		return regionCount;
	}

	public void setRegionCount(Integer regionCount) {
		this.regionCount = regionCount;
	}

	public Integer getBranchCount() {
		return branchCount;
	}

	public void setBranchCount(Integer branchCount) {
		this.branchCount = branchCount;
	}

	public Double getCompletionRate() {
		return completionRate;
	}

	public void setCompletionRate(Double completionRate) {
		this.completionRate = completionRate;
	}

	public Integer getVerifiedGmb() {
		return verifiedGmb;
	}

	public void setVerifiedGmb(Integer verifiedGmb) {
		this.verifiedGmb = verifiedGmb;
	}

	public Integer getMissingGmb() {
		return missingGmb;
	}

	public void setMissingGmb(Integer missingGmb) {
		this.missingGmb = missingGmb;
	}

	public Integer getMismatchCount() {
		return mismatchCount;
	}

	public void setMismatchCount(Integer mismatchCount) {
		this.mismatchCount = mismatchCount;
	}

	public Integer getMissingPhotoCount() {
		return missingPhotoCount;
	}

	public void setMissingPhotoCount(Integer missingPhotoCount) {
		this.missingPhotoCount = missingPhotoCount;
	}

	public Integer getMissingURLCount() {
		return missingURLCount;
	}

	public void setMissingURLCount(Integer missingURLCount) {
		this.missingURLCount = missingURLCount;
	}

	public Integer getFacebookConnectionCount() {
		return facebookConnectionCount;
	}

	public void setFacebookConnectionCount(Integer facebookConnectionCount) {
		this.facebookConnectionCount = facebookConnectionCount;
	}

	public Integer getTwitterConnectionCount() {
		return twitterConnectionCount;
	}

	public void setTwitterConnectionCount(Integer twitterConnectionCount) {
		this.twitterConnectionCount = twitterConnectionCount;
	}

	public Integer getLinkedinConnectionCount() {
		return linkedinConnectionCount;
	}

	public void setLinkedinConnectionCount(Integer linkedinConnectionCount) {
		this.linkedinConnectionCount = linkedinConnectionCount;
	}

	@Override
	public String toString() {
		return "CompanyDetailsReport [companyDetailsReportId=" + companyDetailsReportId + ", companyId=" + companyId
				+ ", companyName=" + companyName + ", userCount=" + userCount + ", verifiedUserCount="
				+ verifiedUserCount + ", verifiedPercent=" + verifiedPercent + ", regionCount=" + regionCount
				+ ", branchCount=" + branchCount + ", completionRate=" + completionRate + ", verifiedGmb=" + verifiedGmb
				+ ", missingGmb=" + missingGmb + ", mismatchCount=" + mismatchCount + ", missingPhotoCount="
				+ missingPhotoCount + ", missingURLCount=" + missingURLCount + ", facebookConnectionCount="
				+ facebookConnectionCount + ", twitterConnectionCount=" + twitterConnectionCount
				+ ", linkedinConnectionCount=" + linkedinConnectionCount + "]";
	}
}