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
	
	
	public void initialise();
	
	public boolean addCustomerWithPayment(Company company,String nonce) throws InvalidInputException;
	
	public String getClientToken();
	
	public String getClientTokenWithCustomerId(String customerId) throws InvalidInputException;
			
	public boolean subscribe(User user,Company company, Integer planId, String nonce) throws NonFatalException;
	
	public BraintreeGateway getGatewayInstance();

}
