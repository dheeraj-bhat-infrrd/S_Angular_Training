package com.realtech.socialsurvey.api.transformers;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	 private static final String BORROWER = "BORROWER";
	 private static final String COBORROWER = "COBORROWER";
	 private static final String BUYER = "BUYER";
	 private static final String SELLER = "SELLER";
	 
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
		
		//always add borrower email cause it's a madatory feild and file wont process
		List<SurveyPreInitiation> surveyPreInitiations = new ArrayList<>();
		Set<String> noDuplicates = new HashSet<>();
        noDuplicates.add( transactionInfo.getCustomer1Email() );
        SurveyPreInitiation surveyPreInitiation = transformingSubObjects(transactionInfo, serviceProviderInfo, BORROWER, companyId, surveySource);
        if(surveyPreInitiation != null)
            surveyPreInitiations.add(surveyPreInitiation);
         
         // check if model contains two customer
        if ( !StringUtils.isBlank( transactionInfo.getCustomer2Email() )
            || !StringUtils.isBlank( transactionInfo.getCustomer2FirstName() ) ) {

            if ( StringUtils.isBlank( transactionInfo.getCustomer2Email() ) )
                throw new InvalidInputException( "Invalid input passed. customer2Email can't be null or empty" );
            if ( StringUtils.isBlank( transactionInfo.getCustomer2FirstName() ) )
                throw new InvalidInputException( "Invalid input passed. customer2FirstName can't be null or empty" );
            surveyPreInitiation = transformingSubObjects(transactionInfo, serviceProviderInfo, COBORROWER, companyId, surveySource);
            if(surveyPreInitiation != null) {
                if(!noDuplicates.add( transactionInfo.getCustomer2Email() )) {
                    surveyPreInitiation.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_DUPLICATE_RECORD );
                }
                surveyPreInitiations.add( surveyPreInitiation );
            }
        }
        
        if ( !StringUtils.isBlank( transactionInfo.getBuyerEmail() )
            || !StringUtils.isBlank( transactionInfo.getBuyerFirstName() ) ) {

            if ( StringUtils.isBlank( transactionInfo.getBuyerEmail() ) )
                throw new InvalidInputException( "Invalid input passed. buyerEmail can't be null or empty" );
            if ( StringUtils.isBlank( transactionInfo.getBuyerFirstName() ) )
                throw new InvalidInputException( "Invalid input passed. buyerFirstName can't be null or empty" );
            surveyPreInitiation = transformingSubObjects(transactionInfo, serviceProviderInfo, BUYER, companyId, surveySource);
            if(surveyPreInitiation != null) {
                if(!noDuplicates.add( transactionInfo.getBuyerEmail() )) {
                    surveyPreInitiation.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_DUPLICATE_RECORD );
                }
                surveyPreInitiations.add( surveyPreInitiation );
            }
        }
        if ( !StringUtils.isBlank( transactionInfo.getSellerEmail() )
            || !StringUtils.isBlank( transactionInfo.getSellerFirstName() ) ) {

            if ( StringUtils.isBlank( transactionInfo.getSellerEmail() ) )
                throw new InvalidInputException( "Invalid input passed. sellerEmail can't be null or empty" );
            if ( StringUtils.isBlank( transactionInfo.getSellerFirstName() ) )
                throw new InvalidInputException( "Invalid input passed. sellerFirstName can't be null or empty" );
            surveyPreInitiation = transformingSubObjects(transactionInfo, serviceProviderInfo, SELLER, companyId, surveySource);
            if(surveyPreInitiation != null) {
                if(!noDuplicates.add( transactionInfo.getSellerEmail() )) {
                    surveyPreInitiation.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_DUPLICATE_RECORD );
                }
                surveyPreInitiations.add( surveyPreInitiation );
            }
        }
    		
        return surveyPreInitiations;
	}
	

    public SurveyPreInitiation transformingSubObjects( TransactionInfoPutVO transactionInfo,
        ServiceProviderInfo serviceProviderInfo, String participant, long companyId, String surveySource )
        throws InvalidInputException
    {
        Date date = null;
        try {
            date = df.parse( transactionInfo.getTransactionDate() );
        } catch ( ParseException e ) {
            throw new InvalidInputException( "Transaction Date with invalid format" );
        }
        Boolean participantAdded = true;
        SurveyPreInitiation surveyPreInitiation = new SurveyPreInitiation();
        if ( participant.equals( BORROWER ) ) {
            surveyPreInitiation.setCustomerEmailId( transactionInfo.getCustomer1Email() );
            surveyPreInitiation.setCustomerFirstName( transactionInfo.getCustomer1FirstName() );
            surveyPreInitiation.setCustomerLastName( transactionInfo.getCustomer1LastName() );
            surveyPreInitiation.setParticipantType( CommonConstants.SURVEY_PARTICIPANT_TYPE_BORROWER );
        }else if ( participant.equals( COBORROWER )) {
            surveyPreInitiation.setCustomerEmailId( transactionInfo.getCustomer2Email() );
            surveyPreInitiation.setCustomerFirstName( transactionInfo.getCustomer2FirstName() );
            surveyPreInitiation.setCustomerLastName( transactionInfo.getCustomer2LastName() );
            surveyPreInitiation.setParticipantType( CommonConstants.SURVEY_PARTICIPANT_TYPE_COBORROWER );
        }else if ( participant.equals( BUYER )) {
            surveyPreInitiation.setCustomerEmailId( transactionInfo.getBuyerEmail() );
            surveyPreInitiation.setCustomerFirstName( transactionInfo.getBuyerFirstName() );
            surveyPreInitiation.setCustomerLastName( transactionInfo.getBuyerLastName() );
            surveyPreInitiation.setParticipantType( CommonConstants.SURVEY_PARTICIPANT_TYPE_BUYER_AGENT );
        }else if ( participant.equals( SELLER )) {
            surveyPreInitiation.setCustomerEmailId( transactionInfo.getSellerEmail() );
            surveyPreInitiation.setCustomerFirstName( transactionInfo.getSellerFirstName() );
            surveyPreInitiation.setCustomerLastName( transactionInfo.getSellerLastName() );
            surveyPreInitiation.setParticipantType( CommonConstants.SURVEY_PARTICIPANT_TYPE_SELLER_AGENT );
        }else {
            participantAdded = false;
        }

        if(participantAdded) {
            surveyPreInitiation.setAgentEmailId( serviceProviderInfo.getServiceProviderEmail() );
            surveyPreInitiation.setAgentName( serviceProviderInfo.getServiceProviderName() );
            surveyPreInitiation.setCompanyId( companyId );

            surveyPreInitiation.setSurveySource( surveySource );
            surveyPreInitiation.setEngagementClosedTime( new Timestamp( date.getTime() ) );
            surveyPreInitiation.setSurveySourceId( transactionInfo.getTransactionRef() );
            surveyPreInitiation.setCity( transactionInfo.getTransactionCity() );
            surveyPreInitiation.setState( transactionInfo.getTransactionState() );
            surveyPreInitiation.setTransactionType( transactionInfo.getTransactionType() );

            surveyPreInitiation.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
            surveyPreInitiation.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            surveyPreInitiation.setLastReminderTime( utils.convertEpochDateToTimestamp() );
            surveyPreInitiation.setStatus( CommonConstants.STATUS_SURVEYPREINITIATION_NOT_PROCESSED );
        }

        return surveyPreInitiation;
    }

	@Override
	public SurveyPutVO transformDomainObjectToApiResponse(
			List<SurveyPreInitiation> d, Object... objects) {
		// TODO Auto-generated method stub
		return null;
	}


}
