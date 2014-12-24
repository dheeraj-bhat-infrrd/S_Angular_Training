package com.realtech.socialsurvey.core.services.payment;

import java.math.BigDecimal;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Subscription;
import com.braintreegateway.Transaction;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.RetryUnsuccessfulException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;

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
	
	/**
	 * Function to create a Braintree transaction with a particular payment method token and an amount
	 * @param paymentMethodToken
	 * @param amount
	 * @return
	 * @throws InvalidInputException
	 */
	public String makePayment(String paymentMethodToken, BigDecimal amount) throws InvalidInputException;
	
	/**
	 * Updates the number of retries in the LicenseDetail table and sends a mail to the User.
	 * @param subscription
	 * @return boolean value
	 * @throws UndeliveredEmailException 
	 * @throws NoRecordsFetchedException 
	 */
	public void updateRetriesForPayment(Subscription subscription) throws InvalidInputException, UndeliveredEmailException, NoRecordsFetchedException;
	
	/**
	 * Retries payment for a particular subscription id and returns the Transaction object.
	 * @param subscriptionId
	 * @return
	 * @throws InvalidInputException
	 * @throws RetryUnsuccessfulException 
	 */
	public Transaction retrySubscriptionCharge(String subscriptionId) throws InvalidInputException, RetryUnsuccessfulException;
	
	/**
	 * Checks if the status of a particular transaction is settling.
	 * @param transactionId
	 * @return
	 * @throws NoRecordsFetchedException
	 * @throws InvalidInputException
	 */
	public boolean checkTransactionSettling(String transactionId) throws NoRecordsFetchedException, InvalidInputException;
	
	/**
	 * Checks if the status of a particular transaction is settled.
	 * @param transactionId
	 * @return
	 * @throws NoRecordsFetchedException
	 * @throws InvalidInputException
	 */
	public boolean checkTransactionSettled(String transactionId) throws NoRecordsFetchedException, InvalidInputException; 
}
