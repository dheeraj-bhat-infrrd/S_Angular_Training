package com.realtech.socialsurvey.core.services.payment;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Subscription;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Handles the payment options for the application
 *
 */
public interface Payment {
	
	
	/**
	 * Method to initialise the gateway.
	 * @return 
	 */
	public BraintreeGateway getGatewayInstance();
	
		
	/**
	 * Method to generate client token to be used by the front end.
	 * @return
	 */
	public String getClientToken();
	
	/**
	 * Method used to generate a token for a specific customer to be used by the front end.
	 * @param customerId
	 * @return
	 * @throws InvalidInputException
	 */
	public String getClientTokenWithCustomerId(String customerId) throws InvalidInputException;
			
	/**
	 * Method used to subscribe a customer to a particular plan.
	 * @param user
	 * @param company
	 * @param planId
	 * @param nonce
	 * @return
	 * @throws NonFatalException
	 */
	public boolean subscribe(User user,Company company, int planId, String nonce) throws NonFatalException;
	
	public boolean retryPaymentAndUpdateLicenseTable(Subscription subscription) throws InvalidInputException;
		
}
