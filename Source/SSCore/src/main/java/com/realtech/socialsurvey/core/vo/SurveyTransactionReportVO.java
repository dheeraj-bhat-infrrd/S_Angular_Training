/**
 * 
 */
package com.realtech.socialsurvey.core.vo;

import java.io.Serializable;

/**
 * @author Subhrajit
 *
 */
public class SurveyTransactionReportVO implements Serializable{
	

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int month;
    private int year;
    private String trxMonth;
    private String userName;
    private Long userId;
    private String nmls;
    private String licenseId;
    private String companyName;
    private Long companyId;
    private String regionName;
    private String branchName;
    private Long totalReviews;
    private Long totalZillowReviews;
    private Long total_3rdPartyReviews;
    private Long totalVerifiedCustomerReviews;
    private Long totalUnverifiedCustomerReviews;
    private Long totalSocialSurveyReviews;
    private Long totalAbusiveReviews;
    private Long totalRetakeReviews;
    private Long totalRetakeCompleted;
    private Long transactionReceivedBySource;
    private Long transactionSent;
    private Long transactionUnprocessable;
    private Long transactionClicked;
    private Long transactionCompleted;
    private Long transactionPartiallyCompleted;
    private Long transactionUnopened;
    private Long transactionDuplicates;
    private String emailId;
    
    
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
	public String getTrxMonth() {
		return trxMonth;
	}
	public void setTrxMonth(String trxMonth) {
		this.trxMonth = trxMonth;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getNmls() {
		return nmls;
	}
	public void setNmls(String nmls) {
		this.nmls = nmls;
	}
	public String getLicenseId() {
		return licenseId;
	}
	public void setLicenseId(String licenseId) {
		this.licenseId = licenseId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public Long getTotalReviews() {
		return totalReviews;
	}
	public void setTotalReviews(Long totalReviews) {
		this.totalReviews = totalReviews;
	}
	public Long getTotalZillowReviews() {
		return totalZillowReviews;
	}
	public void setTotalZillowReviews(Long totalZillowReviews) {
		this.totalZillowReviews = totalZillowReviews;
	}
	public Long getTotal_3rdPartyReviews() {
		return total_3rdPartyReviews;
	}
	public void setTotal_3rdPartyReviews(Long total_3rdPartyReviews) {
		this.total_3rdPartyReviews = total_3rdPartyReviews;
	}
	public Long getTotalVerifiedCustomerReviews() {
		return totalVerifiedCustomerReviews;
	}
	public void setTotalVerifiedCustomerReviews(Long totalVerifiedCustomerReviews) {
		this.totalVerifiedCustomerReviews = totalVerifiedCustomerReviews;
	}
	public Long getTotalUnverifiedCustomerReviews() {
		return totalUnverifiedCustomerReviews;
	}
	public void setTotalUnverifiedCustomerReviews(Long totalUnverifiedCustomerReviews) {
		this.totalUnverifiedCustomerReviews = totalUnverifiedCustomerReviews;
	}
	public Long getTotalSocialSurveyReviews() {
		return totalSocialSurveyReviews;
	}
	public void setTotalSocialSurveyReviews(Long totalSocialSurveyReviews) {
		this.totalSocialSurveyReviews = totalSocialSurveyReviews;
	}
	public Long getTotalAbusiveReviews() {
		return totalAbusiveReviews;
	}
	public void setTotalAbusiveReviews(Long totalAbusiveReviews) {
		this.totalAbusiveReviews = totalAbusiveReviews;
	}
	public Long getTotalRetakeReviews() {
		return totalRetakeReviews;
	}
	public void setTotalRetakeReviews(Long totalRetakeReviews) {
		this.totalRetakeReviews = totalRetakeReviews;
	}
	public Long getTotalRetakeCompleted() {
		return totalRetakeCompleted;
	}
	public void setTotalRetakeCompleted(Long totalRetakeCompleted) {
		this.totalRetakeCompleted = totalRetakeCompleted;
	}
	public Long getTransactionReceivedBySource() {
		return transactionReceivedBySource;
	}
	public void setTransactionReceivedBySource(Long transactionReceivedBySource) {
		this.transactionReceivedBySource = transactionReceivedBySource;
	}
	public Long getTransactionSent() {
		return transactionSent;
	}
	public void setTransactionSent(Long transactionSent) {
		this.transactionSent = transactionSent;
	}
	public Long getTransactionUnprocessable() {
		return transactionUnprocessable;
	}
	public void setTransactionUnprocessable(Long transactionUnprocessable) {
		this.transactionUnprocessable = transactionUnprocessable;
	}
	public Long getTransactionClicked() {
		return transactionClicked;
	}
	public void setTransactionClicked(Long transactionClicked) {
		this.transactionClicked = transactionClicked;
	}
	public Long getTransactionCompleted() {
		return transactionCompleted;
	}
	public void setTransactionCompleted(Long transactionCompleted) {
		this.transactionCompleted = transactionCompleted;
	}
	public Long getTransactionPartiallyCompleted() {
		return transactionPartiallyCompleted;
	}
	public void setTransactionPartiallyCompleted(Long transactionPartiallyCompleted) {
		this.transactionPartiallyCompleted = transactionPartiallyCompleted;
	}
	public Long getTransactionUnopened() {
		return transactionUnopened;
	}
	public void setTransactionUnopened(Long transactionUnopened) {
		this.transactionUnopened = transactionUnopened;
	}
	public Long getTransactionDuplicates() {
		return transactionDuplicates;
	}
	public void setTransactionDuplicates(Long transactionDuplicates) {
		this.transactionDuplicates = transactionDuplicates;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	@Override
	public String toString() {
		return "SurveyTransactionReportVO [month=" + month + ", year=" + year + ", trxMonth=" + trxMonth + ", userName="
				+ userName + ", userId=" + userId + ", nmls=" + nmls + ", licenseId=" + licenseId + ", companyName="
				+ companyName + ", companyId=" + companyId + ", regionName=" + regionName + ", branchName=" + branchName
				+ ", totalReviews=" + totalReviews + ", totalZillowReviews=" + totalZillowReviews
				+ ", total_3rdPartyReviews=" + total_3rdPartyReviews + ", totalVerifiedCustomerReviews="
				+ totalVerifiedCustomerReviews + ", totalUnverifiedCustomerReviews=" + totalUnverifiedCustomerReviews
				+ ", totalSocialSurveyReviews=" + totalSocialSurveyReviews + ", totalAbusiveReviews="
				+ totalAbusiveReviews + ", totalRetakeReviews=" + totalRetakeReviews + ", totalRetakeCompleted="
				+ totalRetakeCompleted + ", transactionReceivedBySource=" + transactionReceivedBySource
				+ ", transactionSent=" + transactionSent + ", transactionUnprocessable=" + transactionUnprocessable
				+ ", transactionClicked=" + transactionClicked + ", transactionCompleted=" + transactionCompleted
				+ ", transactionPartiallyCompleted=" + transactionPartiallyCompleted + ", transactionUnopened="
				+ transactionUnopened + ", transactionDuplicates=" + transactionDuplicates + ", emailId=" + emailId
				+ "]";
	}

}
