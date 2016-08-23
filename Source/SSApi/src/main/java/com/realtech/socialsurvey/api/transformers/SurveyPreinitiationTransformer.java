package com.realtech.socialsurvey.api.transformers;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.TransactionInfo;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;


@Component
public class SurveyPreinitiationTransformer implements Transformer<TransactionInfo, SurveyPreInitiation, TransactionInfo> {

	
	 private static final DateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:MM:SS");
	 
	 @Autowired
	 private Utils utils;
	
	@Override
	public SurveyPreInitiation transformApiRequestToDomainObject(
			TransactionInfo a, Object... objects) {
		
		SurveyPreInitiation surveyPreInitiation = new SurveyPreInitiation();
		
		  TransactionInfo transactionInfo = a;
			 
			 Date date = null;
			 //TODO handle exception
			try {
				date = df.parse(transactionInfo.getTransactionDate());
			} catch (ParseException e) {
				e.printStackTrace();
				System.out.println(e);
			}
			 
			 surveyPreInitiation.setAgentEmailId(transactionInfo.getServiceProviderEmail());
			 surveyPreInitiation.setAgentName(transactionInfo.getServiceProviderName());
			 
			 surveyPreInitiation.setCustomerEmailId(transactionInfo.getCustomer1Email());
			 surveyPreInitiation.setCustomerFirstName(transactionInfo.getCustomer1FirstName());
			 surveyPreInitiation.setCustomerLastName(transactionInfo.getCustomer1LastName());
			 
			 surveyPreInitiation.setSurveySource("API");
			 surveyPreInitiation.setEngagementClosedTime(new Timestamp(date.getTime()));
			 surveyPreInitiation.setSurveySourceId(transactionInfo.getTransactionRef());
			 surveyPreInitiation.setCity(transactionInfo.getTransactionCity());
			 surveyPreInitiation.setState(transactionInfo.getTransactionState());
			 
			 surveyPreInitiation.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
             surveyPreInitiation.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
             surveyPreInitiation.setLastReminderTime( utils.convertEpochDateToTimestamp() );
             surveyPreInitiation.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_NOT_PROCESSED );

			 
			 
		 
		
		return surveyPreInitiation;
	}

	@Override
	public TransactionInfo transformDomainObjectToApiResponse(
			SurveyPreInitiation d , Object... objects ) {
		
		TransactionInfo transactionInfo = new TransactionInfo();
		
		transactionInfo.setCustomer1Email(d.getCustomerEmailId());
		transactionInfo.setCustomer1FirstName(d.getCustomerFirstName());
		transactionInfo.setCustomer1LastName(d.getCustomerLastName());
		
		transactionInfo.setServiceProviderEmail(d.getAgentEmailId());
		transactionInfo.setServiceProviderName(d.getAgentName());
		
		transactionInfo.setTransactionCity(d.getCity());
		transactionInfo.setTransactionState(d.getState());
		
		transactionInfo.setTransactionDate(String.valueOf(d.getEngagementClosedTime()));
		transactionInfo.setTransactionRef(d.getSurveySourceId());
		return transactionInfo;
	}

}
