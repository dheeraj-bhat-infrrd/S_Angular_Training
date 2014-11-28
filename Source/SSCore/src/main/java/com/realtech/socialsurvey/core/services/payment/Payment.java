package com.realtech.socialsurvey.core.services.payment;

import com.braintreegateway.BraintreeGateway;
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
	 */
	public void initialise();
	
	/**
	 * Method used to add a customer to the Braintree vault with their payment details.
	 * @param company
	 * @param nonce
	 * @return
	 * @throws InvalidInputException
	 */
	public boolean addCustomerWithPayment(Company company,String nonce) throws InvalidInputException;
	
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
	public boolean subscribe(User user,Company company, Integer planId, String nonce) throws NonFatalException;
	
	/**
	 * Method used to return the instance of gateway being used.
	 * @return
	 */
	public BraintreeGateway getGatewayInstance();

}
