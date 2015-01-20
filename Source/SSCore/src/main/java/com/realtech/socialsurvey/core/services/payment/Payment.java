package com.realtech.socialsurvey.core.services.payment;

import java.math.BigDecimal;
import java.sql.Timestamp;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Subscription;
import com.braintreegateway.Transaction;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentRetryUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionCancellationUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionPastDueException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUpgradeUnsuccessfulException;

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
	 * @throws NoRecordsFetchedException 
	 * @throws NonFatalException
	 */
	public boolean subscribe(User user,Company company, int planId, String nonce) throws InvalidInputException, PaymentException, NoRecordsFetchedException;
	
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
	 * @throws PaymentRetryUnsuccessfulException 
	 */
	public Transaction retrySubscriptionCharge(String subscriptionId) throws InvalidInputException, PaymentRetryUnsuccessfulException;
	
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

	/**
	 * Checks if the payment is made for a particular company.
	 * @param company
	 * @return
	 * @throws InvalidInputException
	 */
	public boolean checkIfPaymentMade(Company company) throws InvalidInputException;
	
	/**
	 * Returns the disable date timestamp for a subscription id.
	 * @param subscriptionId
	 * @return
	 * @throws NoRecordsFetchedException
	 * @throws PaymentException
	 * @throws InvalidInputException 
	 */
	public Timestamp getDateForCompanyDeactivation(String subscriptionId) throws NoRecordsFetchedException, PaymentException, InvalidInputException;


	/**
	 * Unsubscribes the user from the payment gateway
	 * @param subscriptionId
	 * @throws SubscriptionCancellationUnsuccessfulException 
	 * @throws InvalidInputException 
	 */
	public void unsubscribe(String subscriptionId) throws SubscriptionCancellationUnsuccessfulException, InvalidInputException;
	
	/**
	 * Upgrades the plan for a particular subscription.
	 * @param company
	 * @param newAccountsMasterId
	 * @throws InvalidInputException 
	 * @throws NoRecordsFetchedException 
	 * @throws SubscriptionPastDueException 
	 * @throws PaymentException 
	 * @throws SubscriptionUpgradeUnsuccessfulException 
	 */
	public void upgradePlanForSubscription(Company company,int newAccountsMasterId) throws InvalidInputException, NoRecordsFetchedException, SubscriptionPastDueException, PaymentException, SubscriptionUpgradeUnsuccessfulException;

}
