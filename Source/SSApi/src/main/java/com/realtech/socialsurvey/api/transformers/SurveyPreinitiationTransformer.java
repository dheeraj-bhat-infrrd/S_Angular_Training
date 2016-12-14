package com.realtech.socialsurvey.api.transformers;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.ServiceProviderInfo;
import com.realtech.socialsurvey.api.models.SurveyPutVO;
import com.realtech.socialsurvey.api.models.TransactionInfoPutVO;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


@Component
public class SurveyPreinitiationTransformer implements Transformer<SurveyPutVO, List<SurveyPreInitiation>, SurveyPutVO> {

	
	 private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 
	 @Autowired
	 private Utils utils;
	
	@Override
	public List<SurveyPreInitiation> transformApiRequestToDomainObject(
			SurveyPutVO a, Object... objects) throws InvalidInputException {

		if(a == null)
			throw new InvalidInputException("Invalid input passed. surveyDetail milssing");
		
		
		long companyId = (Long) objects[0];
		
		TransactionInfoPutVO transactionInfo = a.getTransactionInfo();
		ServiceProviderInfo serviceProviderInfo = a.getServiceProviderInfo();
		Date date = null;
		
		if(transactionInfo == null)
			throw new InvalidInputException("Invalid input passed. TransactionInfo milssing");
		
		if(serviceProviderInfo == null)
			throw new InvalidInputException("Invalid input passed. serviceProviderInfo milssing");
		
		if(StringUtils.isBlank(transactionInfo.getCustomer1Email()))
			throw new InvalidInputException("Invalid input passed. Customer1Email can't be null or empty");
		if(StringUtils.isBlank(transactionInfo.getCustomer1FirstName()))
			throw new InvalidInputException("Invalid input passed. Customer1FirstName can't be null or empty");
		if(StringUtils.isBlank(serviceProviderInfo.getServiceProviderEmail()))
			throw new InvalidInputException("Invalid input passed. ServiceProviderEmail can't be null or empty");
			
		try {
			date = df.parse(transactionInfo.getTransactionDate());
		} catch (ParseException e) {
			throw new InvalidInputException("Transaction Date with invalid format");
		}
		
		List<SurveyPreInitiation> surveyPreInitiations = new ArrayList<SurveyPreInitiation>();
		SurveyPreInitiation surveyPreInitiation = new SurveyPreInitiation();

		surveyPreInitiation.setAgentEmailId(serviceProviderInfo.getServiceProviderEmail());
		surveyPreInitiation.setAgentName(serviceProviderInfo.getServiceProviderName());
		surveyPreInitiation.setCompanyId(companyId);
		 
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
		surveyPreInitiations.add(surveyPreInitiation);
         
         // check if model contains two customer
        if(! StringUtils.isBlank(transactionInfo.getCustomer2Email()) && ! StringUtils.isBlank(transactionInfo.getCustomer2FirstName())){
        	SurveyPreInitiation surveyPreInitiation2 = new SurveyPreInitiation();
        	surveyPreInitiation2.setAgentEmailId(serviceProviderInfo.getServiceProviderEmail());
        	surveyPreInitiation2.setAgentName(serviceProviderInfo.getServiceProviderName());
    		surveyPreInitiation2.setCompanyId(companyId);

    		 
        	surveyPreInitiation2.setCustomerEmailId(transactionInfo.getCustomer2Email());
        	surveyPreInitiation2.setCustomerFirstName(transactionInfo.getCustomer2FirstName());
        	surveyPreInitiation2.setCustomerLastName(transactionInfo.getCustomer2LastName());
    		 
        	surveyPreInitiation2.setSurveySource("API");
        	surveyPreInitiation2.setEngagementClosedTime(new Timestamp(date.getTime()));
        	surveyPreInitiation2.setSurveySourceId(transactionInfo.getTransactionRef());
        	surveyPreInitiation2.setCity(transactionInfo.getTransactionCity());
        	surveyPreInitiation2.setState(transactionInfo.getTransactionState());
    		 
        	surveyPreInitiation2.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
        	surveyPreInitiation2.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        	surveyPreInitiation2.setLastReminderTime( utils.convertEpochDateToTimestamp() );
        	surveyPreInitiation2.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_NOT_PROCESSED );
        	surveyPreInitiations.add(surveyPreInitiation2);
         }

		return surveyPreInitiations;
	}

	@Override
	public SurveyPutVO transformDomainObjectToApiResponse(
			List<SurveyPreInitiation> d, Object... objects) {
		// TODO Auto-generated method stub
		return null;
	}


}
