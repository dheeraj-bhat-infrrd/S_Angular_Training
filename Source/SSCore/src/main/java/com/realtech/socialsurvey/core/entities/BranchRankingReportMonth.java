/**
 * 
 */
package com.realtech.socialsurvey.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Subhrajit
 *
 */
@Entity
@Table(name = "user_ranking_report_branch")
public class BranchRankingReportMonth {
	
	@Id
	@Column(name = "user_ranking_report_branch_id")
	String id;
	
	@Column(name = "rank_within_company")
	int rankInCompany;
	
	@Column(name = "branch_id")
	long branchId;
	
	@Column(name = "branch_name")
	String branchName;
	
	@Column(name = "region_id")
	long regionId;
	
	@Column(name = "region_name")
	String regionName;
	
	@Column(name = "company_id")
	long companyId;
	
	@Column(name = "user_count")
	int userCount;
	
	@Column(name = "avg_score")
	double averageScore;
	
	@Column(name = "ranking_score")
	double rankingScore;
	
	@Column(name = "completion_percentage")
	double completionPercentage;
	
	@Column(name = "total_reviews")
	int totalreviews;
	
	@Column(name = "sps")
	double sps;
	
	@Column(name = "public_page_url")
	String publicPageURL;
	
	@Column(name = "is_eligible")
	int isEligible;
	
	@Column(name = "month")
	int month;
	
	@Column(name = "year")
	int year;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getRankInCompany() {
		return rankInCompany;
	}

	public void setRankInCompany(int rankInCompany) {
		this.rankInCompany = rankInCompany;
	}

	public long getBranchId() {
		return branchId;
	}

	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public long getRegionId() {
		return regionId;
	}

	public void setRegionId(long regionId) {
		this.regionId = regionId;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public int getUserCount() {
		return userCount;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}

	public double getAverageScore() {
		return averageScore;
	}

	public void setAverageScore(double averageScore) {
		this.averageScore = averageScore;
	}

	public double getRankingScore() {
		return rankingScore;
	}

	public void setRankingScore(double rankingScore) {
		this.rankingScore = rankingScore;
	}

	public double getCompletionPercentage() {
		return completionPercentage;
	}

	public void setCompletionPercentage(double completionPercentage) {
		this.completionPercentage = completionPercentage;
	}

	public int getTotalreviews() {
		return totalreviews;
	}

	public void setTotalreviews(int totalreviews) {
		this.totalreviews = totalreviews;
	}

	public double getSps() {
		return sps;
	}

	public void setSps(double sps) {
		this.sps = sps;
	}

	public String getPublicPageURL() {
		return publicPageURL;
	}

	public void setPublicPageURL(String publicPageURL) {
		this.publicPageURL = publicPageURL;
	}

	public int getIsEligible() {
		return isEligible;
	}

	public void setIsEligible(int isEligible) {
		this.isEligible = isEligible;
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
		return "BranchRankingReportMonth [id=" + id + ", rankInCompany=" + rankInCompany + ", branchId=" + branchId
				+ ", branchName=" + branchName + ", regionId=" + regionId + ", regionName=" + regionName
				+ ", companyId=" + companyId + ", userCount=" + userCount + ", averageScore=" + averageScore
				+ ", rankingScore=" + rankingScore + ", completionPercentage=" + completionPercentage
				+ ", totalreviews=" + totalreviews + ", sps=" + sps + ", publicPageURL=" + publicPageURL
				+ ", isEligible=" + isEligible + ", month=" + month + ", year=" + year + "]";
	}
	
}