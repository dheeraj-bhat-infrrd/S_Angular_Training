package com.realtech.socialsurvey.api.transformers;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.ReviewVO;
import com.realtech.socialsurvey.api.models.SurveyVO;
import com.realtech.socialsurvey.api.models.TransactionInfo;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;

@Component
public class SurveyTransformer implements Transformer<SurveyVO, SurveyDetails, SurveyVO>{

	@Override
	public SurveyDetails transformApiRequestToDomainObject(SurveyVO a,
			Object... objects) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SurveyVO transformDomainObjectToApiResponse(SurveyDetails d,
			Object... objects) {
		
		SurveyPreInitiation surveyPreInitiation;
		TransactionInfo transactionInfo = new TransactionInfo();
		ReviewVO review = new ReviewVO();
		SurveyVO survey = new SurveyVO();
		
		if (objects[0] != null && objects[0] instanceof SurveyPreInitiation) {
			surveyPreInitiation = (SurveyPreInitiation) objects[0];
			transactionInfo.setCustomer1Email(surveyPreInitiation.getCustomerEmailId());
			
			transactionInfo.setCustomer1FirstName(surveyPreInitiation.getCustomerFirstName());
			transactionInfo.setCustomer1LastName(surveyPreInitiation.getCustomerLastName());

			transactionInfo.setServiceProviderEmail(surveyPreInitiation.getAgentEmailId());
			transactionInfo.setServiceProviderName(surveyPreInitiation.getAgentName());

			transactionInfo.setTransactionCity(surveyPreInitiation.getCity());
			transactionInfo.setTransactionState(surveyPreInitiation.getState());

			transactionInfo.setTransactionDate(String.valueOf(surveyPreInitiation
					.getEngagementClosedTime()));
			transactionInfo.setTransactionRef(surveyPreInitiation.getSurveySourceId());

			survey.setSurveyId(Long.valueOf(surveyPreInitiation.getSurveyPreIntitiationId()));
		}

		if( d != null ){
			review.setSummary(d.getSummary());
			review.setDescription(d.getReview());
			review.setRating(String.valueOf(d.getScore()));
			review.setReviewDate(d.getModifiedOn().toString());
			review.setSource(d.getSource());
			//TODO Fix CRM verified
			//review.setIsCRMVerified(surveyPreInitiation.);
			review.setIsReportedAbusive(d.isAbusive());
		}
		
		
		survey.setReview(review);
		survey.setTransactionInfo(transactionInfo);
		
		return survey;
	}

}
