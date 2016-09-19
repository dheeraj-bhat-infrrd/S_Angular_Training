package com.realtech.socialsurvey.api.transformers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.ReviewVO;
import com.realtech.socialsurvey.api.models.ServiceProviderInfo;
import com.realtech.socialsurvey.api.models.SurveyGetVO;
import com.realtech.socialsurvey.api.models.TransactionInfo;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;

@Component
public class SurveyTransformer implements Transformer<SurveyGetVO, SurveyDetails, SurveyGetVO>{

	@Autowired
	UserManagementService userManagementService; 
	
	@Override
	public SurveyDetails transformApiRequestToDomainObject(SurveyGetVO a,
			Object... objects) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SurveyGetVO transformDomainObjectToApiResponse(SurveyDetails d,
			Object... objects) {
		
		SurveyPreInitiation surveyPreInitiation;
		TransactionInfo transactionInfo = new TransactionInfo();
		ServiceProviderInfo serviceProviderInfo = new ServiceProviderInfo();
		ReviewVO review = new ReviewVO();
		SurveyGetVO survey = new SurveyGetVO();
		
		if (objects[0] != null && objects[0] instanceof SurveyPreInitiation) {
			surveyPreInitiation = (SurveyPreInitiation) objects[0];
			transactionInfo.setCustomer1Email(surveyPreInitiation.getCustomerEmailId());
			
			transactionInfo.setCustomer1FirstName(surveyPreInitiation.getCustomerFirstName());
			transactionInfo.setCustomer1LastName(surveyPreInitiation.getCustomerLastName());

			serviceProviderInfo.setServiceProviderEmail(surveyPreInitiation.getAgentEmailId());
			serviceProviderInfo.setServiceProviderName(surveyPreInitiation.getAgentName());

			transactionInfo.setTransactionCity(surveyPreInitiation.getCity());
			transactionInfo.setTransactionState(surveyPreInitiation.getState());

			transactionInfo.setTransactionDate(String.valueOf(surveyPreInitiation
					.getEngagementClosedTime()));
			transactionInfo.setTransactionRef(surveyPreInitiation.getSurveySourceId());

			survey.setSurveyId(Long.valueOf(surveyPreInitiation.getSurveyPreIntitiationId()));
		}

		if( d != null ){
			if(objects[0] == null){
				transactionInfo.setCustomer1Email(d.getCustomerEmail());
				transactionInfo.setCustomer1FirstName(d.getCustomerFirstName());
				transactionInfo.setCustomer1LastName(d.getCustomerLastName());
				serviceProviderInfo.setServiceProviderName(d.getAgentName());
				transactionInfo.setTransactionCity(d.getCity());
				transactionInfo.setTransactionState(d.getState());
				transactionInfo.setTransactionDate(String.valueOf(d.getSurveyTransactionDate()));
				survey.setReviewId(d.get_id());
				
				try{
				User user = userManagementService.getUserObjByUserId(d.getAgentId());
				serviceProviderInfo.setServiceProviderEmail(user.getEmailId());
				}catch(Exception e){
					
				}
				
			}
			
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
		survey.setServiceProviderInfo(serviceProviderInfo);
		
		return survey;
	}

}
