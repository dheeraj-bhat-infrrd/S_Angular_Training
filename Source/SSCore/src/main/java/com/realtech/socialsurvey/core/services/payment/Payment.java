package com.realtech.socialsurvey.core.services.payment;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Subscription;
import com.braintreegateway.Transaction;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.payment.exception.CardUpdateUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.CreditCardException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentRetryUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionCancellationUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionPastDueException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUpgradeUnsuccessfulException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

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
	 * @throws SubscriptionUnsuccessfulException 
	 * @throws CreditCardException 
	 * @throws NonFatalException
	 */
	public void subscribe(User user, int planId, String nonce) throws InvalidInputException, PaymentException, NoRecordsFetchedException, SubscriptionUnsuccessfulException, CreditCardException;
	
	/**
	 * Method used to subscribe customer to a free account.
	 * @param user
	 * @param accountsMasterId
	 * @throws InvalidInputException
	 */
	//public void subscribeForFreeAccount(User user,int accountsMasterId) throws InvalidInputException;

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
	 * @throws SolrException 
	 * @throws UndeliveredEmailException 
	 * @throws SubscriptionUnsuccessfulException 
	 * @throws CreditCardException 
	 */
	public void upgradePlanForSubscription(User user,int newAccountsMasterId, String nonce) throws InvalidInputException, NoRecordsFetchedException, SubscriptionPastDueException, PaymentException, SubscriptionUpgradeUnsuccessfulException, SolrException, UndeliveredEmailException, SubscriptionUnsuccessfulException, CreditCardException;
	
	/**
	 * Fetches the current card details for a particular subscription
	 * @param subscriptionId
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws PaymentException
	 */
	public Map<String, String> getCurrentPaymentDetails(String subscriptionId) throws InvalidInputException, NoRecordsFetchedException, PaymentException;
		
	/**
	 * Changes the card for a particular customer and subscription
	 * @param subscriptionId
	 * @param paymentNonce
	 * @param customerId
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws PaymentException
	 * @throws CreditCardException 
	 * @throws CardUpdateUnsuccessfulException 
	 */
	public void changePaymentMethod(String subscriptionId,String paymentNonce, String customerId) throws InvalidInputException, NoRecordsFetchedException, PaymentException, CreditCardException, CardUpdateUnsuccessfulException;
	
	/**
	 * Returns the balance amount while upgrading from one plan to another
	 * @param fromAccountsMasterId
	 * @param toAccountsMasterId
	 * @return
	 * @throws InvalidInputException
	 */
	public float getBalacnceAmountForPlanUpgrade(int fromAccountsMasterId,int toAccountsMasterId) throws InvalidInputException;
}
