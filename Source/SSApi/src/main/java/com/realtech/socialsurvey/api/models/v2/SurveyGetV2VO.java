package com.realtech.socialsurvey.api.models.v2;

import org.springframework.stereotype.Component;


@Component
public class SurveyGetV2VO
{

    private long surveyId; // survey preinitiation id
    private String reviewId; // mongo survey id
    private TransactionInfoGetV2VO transactionInfo;
    private ServiceProviderInfoV2 serviceProviderInfo;
    private ReviewV2VO review;
    private String reviewStatus;


    public String getReviewStatus() {
		return reviewStatus;
	}


	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}


	public String getReviewId()
    {
        return reviewId;
    }


    public void setReviewId( String reviewId )
    {
        this.reviewId = reviewId;
    }


    public long getSurveyId()
    {
        return surveyId;
    }


    public void setSurveyId( long surveyId )
    {
        this.surveyId = surveyId;
    }


    public TransactionInfoGetV2VO getTransactionInfo()
    {
        return transactionInfo;
    }


    public void setTransactionInfo( TransactionInfoGetV2VO transactionInfo )
    {
        this.transactionInfo = transactionInfo;
    }


    public ServiceProviderInfoV2 getServiceProviderInfo()
    {
        return serviceProviderInfo;
    }


    public void setServiceProviderInfo( ServiceProviderInfoV2 serviceProviderInfo )
    {
        this.serviceProviderInfo = serviceProviderInfo;
    }


    public ReviewV2VO getReview()
    {
        return review;
    }


    public void setReview( ReviewV2VO review )
    {
        this.review = review;
    }

}
