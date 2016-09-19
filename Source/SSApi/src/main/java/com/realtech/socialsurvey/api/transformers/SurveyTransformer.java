package com.realtech.socialsurvey.api.transformers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.ReviewVO;
import com.realtech.socialsurvey.api.models.ServiceProviderInfo;
import com.realtech.socialsurvey.api.models.SurveyGetVO;
import com.realtech.socialsurvey.api.models.TransactionInfoGetVO;
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
		TransactionInfoGetVO transactionInfo = new TransactionInfoGetVO();
		ServiceProviderInfo serviceProviderInfo = new ServiceProviderInfo();
		ReviewVO review = new ReviewVO();
		SurveyGetVO survey = new SurveyGetVO();
		
		if (objects[0] != null && objects[0] instanceof SurveyPreInitiation) {
			surveyPreInitiation = (SurveyPreInitiation) objects[0];
			transactionInfo.setCustomerEmail(surveyPreInitiation.getCustomerEmailId());
			
			transactionInfo.setCustomerFirstName(surveyPreInitiation.getCustomerFirstName());
			transactionInfo.setCustomerLastName(surveyPreInitiation.getCustomerLastName());

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
				transactionInfo.setCustomerEmail(d.getCustomerEmail());
				transactionInfo.setCustomerFirstName(d.getCustomerFirstName());
				transactionInfo.setCustomerLastName(d.getCustomerLastName());
				serviceProviderInfo.setServiceProviderName(d.getAgentName());
				transactionInfo.setTransactionCity(d.getCity());
				transactionInfo.setTransactionState(d.getState());
				transactionInfo.setTransactionDate(String.valueOf(d.getSurveyTransactionDate()));
				transactionInfo.setTransactionRef(d.getSourceId());				
				
				try{
				User user = userManagementService.getUserObjByUserId(d.getAgentId());
				serviceProviderInfo.setServiceProviderEmail(user.getEmailId());
				}catch(Exception e){
					
				}
				
			}
			survey.setReviewId(d.get_id());
			review.setSummary(d.getSummary());
			review.setDescription(d.getReview());
			review.setRating(String.valueOf(d.getScore()));
			review.setReviewDate(d.getModifiedOn().toString());
			review.setSource(d.getSource());
			review.setIsReportedAbusive(d.isAbusive());
			
			//review.setIsCRMVerified(surveyPreInitiation);
			boolean isCRMVerified = false;
			if(d.getSource().equals("encompass") || d.getSource().equals("DOTLOOP"))
				isCRMVerified = true;
			review.setIsCRMVerified(isCRMVerified);

		}
		
		
		survey.setReview(review);
		survey.setTransactionInfo(transactionInfo);
		survey.setServiceProviderInfo(serviceProviderInfo);
		
		return survey;
	}

}
