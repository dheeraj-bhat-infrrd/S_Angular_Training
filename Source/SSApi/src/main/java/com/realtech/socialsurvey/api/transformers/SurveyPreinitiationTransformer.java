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
		String surveySource = ( String ) objects[1];
		
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
		SurveyPreInitiation surveyPreInitiationBorrower = new SurveyPreInitiation();

		surveyPreInitiationBorrower.setAgentEmailId(serviceProviderInfo.getServiceProviderEmail());
		surveyPreInitiationBorrower.setAgentName(serviceProviderInfo.getServiceProviderName());
		surveyPreInitiationBorrower.setCompanyId(companyId);
		 
		surveyPreInitiationBorrower.setCustomerEmailId(transactionInfo.getCustomer1Email());
		surveyPreInitiationBorrower.setCustomerFirstName(transactionInfo.getCustomer1FirstName());
		surveyPreInitiationBorrower.setCustomerLastName(transactionInfo.getCustomer1LastName());
		 
		surveyPreInitiationBorrower.setSurveySource(surveySource);
		surveyPreInitiationBorrower.setEngagementClosedTime(new Timestamp(date.getTime()));
		surveyPreInitiationBorrower.setSurveySourceId(transactionInfo.getTransactionRef());
		surveyPreInitiationBorrower.setCity(transactionInfo.getTransactionCity());
		surveyPreInitiationBorrower.setState(transactionInfo.getTransactionState());
        surveyPreInitiationBorrower.setTransactionType(transactionInfo.getTransactionType());
        //adding property address feild to save in survey pre initiation
        surveyPreInitiationBorrower.setPropertyAddress( transactionInfo.getPropertyAddress() );

		 
		surveyPreInitiationBorrower.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
		surveyPreInitiationBorrower.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
		surveyPreInitiationBorrower.setLastReminderTime( utils.convertEpochDateToTimestamp() );
		surveyPreInitiationBorrower.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_NOT_PROCESSED );
		surveyPreInitiations.add(surveyPreInitiationBorrower);
         
         // check if model contains two customer
        if(! StringUtils.isBlank(transactionInfo.getCustomer2Email()) || ! StringUtils.isBlank(transactionInfo.getCustomer2FirstName())){
        	
        	if(StringUtils.isBlank(transactionInfo.getCustomer2Email()))
    			throw new InvalidInputException("Invalid input passed. customer2Email can't be null or empty");
    		if(StringUtils.isBlank(transactionInfo.getCustomer2FirstName()))
    			throw new InvalidInputException("Invalid input passed. customer2FirstName can't be null or empty");
        
    		//check if both email ids are different, if yes than process 2nd as well
    		if( ! transactionInfo.getCustomer2Email().equalsIgnoreCase(transactionInfo.getCustomer2FirstName())) {
    			SurveyPreInitiation surveyPreInitiationCoBorrower = new SurveyPreInitiation();
            	surveyPreInitiationCoBorrower.setAgentEmailId(serviceProviderInfo.getServiceProviderEmail());
            	surveyPreInitiationCoBorrower.setAgentName(serviceProviderInfo.getServiceProviderName());
        		surveyPreInitiationCoBorrower.setCompanyId(companyId);

        		 
            	surveyPreInitiationCoBorrower.setCustomerEmailId(transactionInfo.getCustomer2Email());
            	surveyPreInitiationCoBorrower.setCustomerFirstName(transactionInfo.getCustomer2FirstName());
            	surveyPreInitiationCoBorrower.setCustomerLastName(transactionInfo.getCustomer2LastName());
        		 
            	surveyPreInitiationCoBorrower.setSurveySource(surveySource);
            	surveyPreInitiationCoBorrower.setEngagementClosedTime(new Timestamp(date.getTime()));
            	surveyPreInitiationCoBorrower.setSurveySourceId(transactionInfo.getTransactionRef());
            	surveyPreInitiationCoBorrower.setCity(transactionInfo.getTransactionCity());
            	surveyPreInitiationCoBorrower.setState(transactionInfo.getTransactionState());
            	surveyPreInitiationCoBorrower.setTransactionType(transactionInfo.getTransactionType());
        		 
            	surveyPreInitiationCoBorrower.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
            	surveyPreInitiationCoBorrower.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            	surveyPreInitiationCoBorrower.setLastReminderTime( utils.convertEpochDateToTimestamp() );
            	surveyPreInitiationCoBorrower.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_NOT_PROCESSED );
            	
            	if( transactionInfo.getCustomer1Email().equalsIgnoreCase( transactionInfo.getCustomer2Email()) ){
                    surveyPreInitiationCoBorrower.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_DUPLICATE_RECORD );
            	}
            	surveyPreInitiations.add( surveyPreInitiationCoBorrower );
             }
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
