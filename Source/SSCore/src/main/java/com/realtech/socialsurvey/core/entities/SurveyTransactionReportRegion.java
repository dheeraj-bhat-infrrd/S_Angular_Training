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
    private Long month;
    
    @Column ( name = "year")
    private Long year;
    
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
    private Long transactionCompleted_;
    
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

}
