package com.realtech.socialsurvey.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name= "digest")
public class Digest
{
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "digest_id")
    private String digestId;
    
    @Column (name = "company_id")
    private Long companyId;
    
    @Column (name = "company_name")
    private String companyName;
    
    @Column (name = "month")
    private int month;
    
    @Column (name = "year")
    private int year;
    
    @Column (name = "average_score_rating")
    private Double averageScoreRating;
    
    @Column (name = "user_count")
    private Long userCount;
    
    @Column (name = "total_transactions")
    private Long totalTransactions;
    
    @Column (name = "completed_transactions")
    private Long completedTransactions;
    
    @Column (name = "survey_completion_rate")
    private Double surveyCompletionRate;
    
    @Column (name = "sps")
    private Double sps;
    
    @Column (name = "promoters")
    private Long promoters;
    
    @Column (name = "detractors")
    private Long detractors;
    
    @Column (name = "passives")
    private Long passives;
    
    @Column (name = "total_completed_reviews")
    private Long totalCompletedReviews;
    
    @Column (name = "trx_month")
    private String trxMonth;

	public String getDigestId() {
		return digestId;
	}

	public void setDigestId(String digestId) {
		this.digestId = digestId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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

	public Double getAverageScoreRating() {
		return averageScoreRating;
	}

	public void setAverageScoreRating(Double averageScoreRating) {
		this.averageScoreRating = averageScoreRating;
	}

	public Long getUserCount() {
		return userCount;
	}

	public void setUserCount(Long userCount) {
		this.userCount = userCount;
	}

	public Long getTotalTransactions() {
		return totalTransactions;
	}

	public void setTotalTransactions(Long totalTransactions) {
		this.totalTransactions = totalTransactions;
	}

	public Long getCompletedTransactions() {
		return completedTransactions;
	}

	public void setCompletedTransactions(Long completedTransactions) {
		this.completedTransactions = completedTransactions;
	}

	public Double getSurveyCompletionRate() {
		return surveyCompletionRate;
	}

	public void setSurveyCompletionRate(Double surveyCompletionRate) {
		this.surveyCompletionRate = surveyCompletionRate;
	}

	public Double getSps() {
		return sps;
	}

	public void setSps(Double sps) {
		this.sps = sps;
	}

	public Long getPromoters() {
		return promoters;
	}

	public void setPromoters(Long promoters) {
		this.promoters = promoters;
	}

	public Long getDetractors() {
		return detractors;
	}

	public void setDetractors(Long detractors) {
		this.detractors = detractors;
	}

	public Long getPassives() {
		return passives;
	}

	public void setPassives(Long passives) {
		this.passives = passives;
	}

	public Long getTotalCompletedReviews() {
		return totalCompletedReviews;
	}

	public void setTotalCompletedReviews(Long totalCompletedReviews) {
		this.totalCompletedReviews = totalCompletedReviews;
	}

	public String getTrxMonth() {
		return trxMonth;
	}

	public void setTrxMonth(String trxMonth) {
		this.trxMonth = trxMonth;
	}

	@Override
	public String toString() {
		return "Digest [digestId=" + digestId + ", companyId=" + companyId + ", companyName=" + companyName + ", month="
				+ month + ", year=" + year + ", averageScoreRating=" + averageScoreRating + ", userCount=" + userCount
				+ ", totalTransactions=" + totalTransactions + ", completedTransactions=" + completedTransactions
				+ ", surveyCompletionRate=" + surveyCompletionRate + ", sps=" + sps + ", promoters=" + promoters
				+ ", detractors=" + detractors + ", passives=" + passives + ", totalCompletedReviews="
				+ totalCompletedReviews + ", trxMonth=" + trxMonth + "]";
	}
}
