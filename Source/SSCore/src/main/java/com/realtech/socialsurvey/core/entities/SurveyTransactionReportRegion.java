package com.realtech.socialsurvey.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name= "survey_transaction_report_region")
public class SurveyTransactionReportRegion
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "survey_transaction_report_region_id")
    private String surveyTransactionReportRegionId;
    
    @Column ( name = "month")
    private int month;
    
    @Column ( name = "year")
    private int year;
    
    @Column ( name = "user_name")
    private String userName;
    
    @Column ( name = "user_id")
    private Long userId;
    
    @Column ( name = "nmls")
    private String nmls;
    
    @Column ( name = "license_id")
    private String licenseId;
    
    @Column ( name = "company_name")
    private String companyName;
    
    @Column ( name = "company_id")
    private Long companyId;
    
    @Column ( name = "region_name")
    private String regionName;
    
    @Column ( name = "region_id")
    private Long regionId;
    
    @Column ( name = "branch_name")
    private String branchName;
    
    @Column ( name = "total_reviews")
    private Long totalReviews;
    
    @Column ( name = "total_zillow_reviews")
    private Long totalZillowReviews;
    
    @Column ( name = "total_3rd_party_reviews")
    private Long total_3rdPartyReviews;
    
    @Column ( name = "total_verified_customer_reviews")
    private Long totalVerifiedCustomerReviews;
    
    @Column ( name = "total_unverified_customer_reviews")
    private Long totalUnverifiedCustomerReviews;
    
    @Column ( name = "total_social_survey_reviews")
    private Long totalSocialSurveyReviews;
    
    @Column ( name = "total_abusive_reviews")
    private Long totalAbusiveReviews;
    
    @Column ( name = "total_retake_reviews")
    private Long totalRetakeReviews;
    
    @Column ( name = "total_retake_completed")
    private Long totalRetakeCompleted;
    
    @Column ( name = "transaction_received_by_source")
    private Long transactionReceivedBySource;
    
    @Column ( name = "transaction_sent")
    private Long transactionSent;
    
    @Column ( name = "transaction_unprocessable")
    private Long transactionUnprocessable;
    
    @Column ( name = "transaction_clicked")
    private Long transactionClicked;
    
    @Column ( name = "transaction_completed_")
    private Long transactionCompleted;
    
    @Column ( name = "transaction_partially_completed")
    private Long transactionPartiallyCompleted;
    
    @Column ( name = "transaction_unopened")
    private Long transactionUnopened;
    
    @Column ( name = "transaction_duplicates")
    private Long transactionDuplicates;
    
    @Column ( name = "transaction_mismatched")
    private Long transactionMismatched;
    
    @Column ( name = "transaction_unassigned")
    private Long transactionUnassigned;
    
    @Column (name = "email_id")
    private String emailId;

    public String getSurveyTransactionReportRegionId()
    {
        return surveyTransactionReportRegionId;
    }

    public void setSurveyTransactionReportRegionId( String surveyTransactionReportRegionId )
    {
        this.surveyTransactionReportRegionId = surveyTransactionReportRegionId;
    }

    public int getMonth()
    {
        return month;
    }

    public void setMonth( int month )
    {
        this.month = month;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear( int year )
    {
        this.year = year;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName( String userName )
    {
        this.userName = userName;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId( Long userId )
    {
        this.userId = userId;
    }

    public String getNmls()
    {
        return nmls;
    }

    public void setNmls( String nmls )
    {
        this.nmls = nmls;
    }

    public String getLicenseId()
    {
        return licenseId;
    }

    public void setLicenseId( String licenseId )
    {
        this.licenseId = licenseId;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName( String companyName )
    {
        this.companyName = companyName;
    }

    public Long getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId( Long companyId )
    {
        this.companyId = companyId;
    }

    public String getRegionName()
    {
        return regionName;
    }

    public void setRegionName( String regionName )
    {
        this.regionName = regionName;
    }

    public Long getRegionId()
    {
        return regionId;
    }

    public void setRegionId( Long regionId )
    {
        this.regionId = regionId;
    }

    public String getBranchName()
    {
        return branchName;
    }

    public void setBranchName( String branchName )
    {
        this.branchName = branchName;
    }

    public Long getTotalReviews()
    {
        return totalReviews;
    }

    public void setTotalReviews( Long totalReviews )
    {
        this.totalReviews = totalReviews;
    }

    public Long getTotalZillowReviews()
    {
        return totalZillowReviews;
    }

    public void setTotalZillowReviews( Long totalZillowReviews )
    {
        this.totalZillowReviews = totalZillowReviews;
    }

    public Long getTotal_3rdPartyReviews()
    {
        return total_3rdPartyReviews;
    }

    public void setTotal_3rdPartyReviews( Long total_3rdPartyReviews )
    {
        this.total_3rdPartyReviews = total_3rdPartyReviews;
    }

    public Long getTotalVerifiedCustomerReviews()
    {
        return totalVerifiedCustomerReviews;
    }

    public void setTotalVerifiedCustomerReviews( Long totalVerifiedCustomerReviews )
    {
        this.totalVerifiedCustomerReviews = totalVerifiedCustomerReviews;
    }

    public Long getTotalUnverifiedCustomerReviews()
    {
        return totalUnverifiedCustomerReviews;
    }

    public void setTotalUnverifiedCustomerReviews( Long totalUnverifiedCustomerReviews )
    {
        this.totalUnverifiedCustomerReviews = totalUnverifiedCustomerReviews;
    }

    public Long getTotalSocialSurveyReviews()
    {
        return totalSocialSurveyReviews;
    }

    public void setTotalSocialSurveyReviews( Long totalSocialSurveyReviews )
    {
        this.totalSocialSurveyReviews = totalSocialSurveyReviews;
    }

    public Long getTotalAbusiveReviews()
    {
        return totalAbusiveReviews;
    }

    public void setTotalAbusiveReviews( Long totalAbusiveReviews )
    {
        this.totalAbusiveReviews = totalAbusiveReviews;
    }

    public Long getTotalRetakeReviews()
    {
        return totalRetakeReviews;
    }

    public void setTotalRetakeReviews( Long totalRetakeReviews )
    {
        this.totalRetakeReviews = totalRetakeReviews;
    }

    public Long getTotalRetakeCompleted()
    {
        return totalRetakeCompleted;
    }

    public void setTotalRetakeCompleted( Long totalRetakeCompleted )
    {
        this.totalRetakeCompleted = totalRetakeCompleted;
    }

    public Long getTransactionReceivedBySource()
    {
        return transactionReceivedBySource;
    }

    public void setTransactionReceivedBySource( Long transactionReceivedBySource )
    {
        this.transactionReceivedBySource = transactionReceivedBySource;
    }

    public Long getTransactionSent()
    {
        return transactionSent;
    }

    public void setTransactionSent( Long transactionSent )
    {
        this.transactionSent = transactionSent;
    }

    public Long getTransactionUnprocessable()
    {
        return transactionUnprocessable;
    }

    public void setTransactionUnprocessable( Long transactionUnprocessable )
    {
        this.transactionUnprocessable = transactionUnprocessable;
    }

    public Long getTransactionClicked()
    {
        return transactionClicked;
    }

    public void setTransactionClicked( Long transactionClicked )
    {
        this.transactionClicked = transactionClicked;
    }

    public Long getTransactionCompleted()
    {
        return transactionCompleted;
    }

    public void setTransactionCompleted( Long transactionCompleted )
    {
        this.transactionCompleted = transactionCompleted;
    }

    public Long getTransactionPartiallyCompleted()
    {
        return transactionPartiallyCompleted;
    }

    public void setTransactionPartiallyCompleted( Long transactionPartiallyCompleted )
    {
        this.transactionPartiallyCompleted = transactionPartiallyCompleted;
    }

    public Long getTransactionUnopened()
    {
        return transactionUnopened;
    }

    public void setTransactionUnopened( Long transactionUnopened )
    {
        this.transactionUnopened = transactionUnopened;
    }

    public Long getTransactionDuplicates()
    {
        return transactionDuplicates;
    }

    public void setTransactionDuplicates( Long transactionDuplicates )
    {
        this.transactionDuplicates = transactionDuplicates;
    }

    public Long getTransactionMismatched()
    {
        return transactionMismatched;
    }

    public void setTransactionMismatched( Long transactionMismatched )
    {
        this.transactionMismatched = transactionMismatched;
    }

    public Long getTransactionUnassigned()
    {
        return transactionUnassigned;
    }

    public void setTransactionUnassigned( Long transactionUnassigned )
    {
        this.transactionUnassigned = transactionUnassigned;
    }

    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }

    public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	@Override
	public String toString() {
		return "SurveyTransactionReportRegion [surveyTransactionReportRegionId=" + surveyTransactionReportRegionId
				+ ", month=" + month + ", year=" + year + ", userName=" + userName + ", userId=" + userId + ", nmls="
				+ nmls + ", licenseId=" + licenseId + ", companyName=" + companyName + ", companyId=" + companyId
				+ ", regionName=" + regionName + ", regionId=" + regionId + ", branchName=" + branchName
				+ ", totalReviews=" + totalReviews + ", totalZillowReviews=" + totalZillowReviews
				+ ", total_3rdPartyReviews=" + total_3rdPartyReviews + ", totalVerifiedCustomerReviews="
				+ totalVerifiedCustomerReviews + ", totalUnverifiedCustomerReviews=" + totalUnverifiedCustomerReviews
				+ ", totalSocialSurveyReviews=" + totalSocialSurveyReviews + ", totalAbusiveReviews="
				+ totalAbusiveReviews + ", totalRetakeReviews=" + totalRetakeReviews + ", totalRetakeCompleted="
				+ totalRetakeCompleted + ", transactionReceivedBySource=" + transactionReceivedBySource
				+ ", transactionSent=" + transactionSent + ", transactionUnprocessable=" + transactionUnprocessable
				+ ", transactionClicked=" + transactionClicked + ", transactionCompleted=" + transactionCompleted
				+ ", transactionPartiallyCompleted=" + transactionPartiallyCompleted + ", transactionUnopened="
				+ transactionUnopened + ", transactionDuplicates=" + transactionDuplicates + ", transactionMismatched="
				+ transactionMismatched + ", transactionUnassigned=" + transactionUnassigned + ", emailId=" + emailId
				+ "]";
	}

}