package com.realtech.socialsurvey.core.services.payment.impl;

// JIRA: SS-15: By RM03

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.CreditCard;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.Subscription;
import com.braintreegateway.SubscriptionRequest;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.exceptions.NotFoundException;
import com.braintreegateway.exceptions.UnexpectedException;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.AccountsMaster;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.RetriedTransaction;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentRetryUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionCancellationUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionPastDueException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUpgradeUnsuccessfulException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.PropertyFileReader;

/**
 * This class implements the Payment interface and makes calls to the Braintree APIs to make
 * payments and subscriptions.
 */

@Component
public class BrainTreePaymentImpl implements Payment, InitializingBean {

	@Autowired
	private GenericDao<LicenseDetail, Long> licenseDetailDao;

	@Autowired
	private GenericDao<AccountsMaster, Integer> accountsMasterDao;

	@Autowired
	private GenericDao<RetriedTransaction, Long> retriedTransactionDao;

	@Autowired
	private GenericDao<User, Long> userDao;

	@Autowired
	private PropertyFileReader propertyFileReader;

	@Autowired
	private EmailServices emailServices;

	@Value("${MERCHANT_ID}")
	private String merchantId;

	@Value("${PUBLIC_KEY}")
	private String publicKey;

	@Value("${PRIVATE_KEY}")
	private String privateKey;

	@Value("${SANDBOX}")
	private int sandboxMode;

	@Value("${PAYMENT_RETRY_DAYS}")
	private int retryDays;

	private static final Logger LOG = LoggerFactory.getLogger(BrainTreePaymentImpl.class);

	private BraintreeGateway gateway = null;
	
	@Autowired
	private OrganizationManagementService organizationManagementService;

	/**
	 * Returns the the Braintree gateway.
	 */
	public BraintreeGateway getGatewayInstance() {
		return gateway;
	}
	
	@Override
	public void afterPropertiesSet() {

		LOG.info("BraintreePaymentImpl : afterPropertiesSet() : Executing method ");
		if (gateway == null) {
			if (sandboxMode == CommonConstants.SANDBOX_MODE_TRUE) {
				LOG.info("Initialising gateway with keys: " + merchantId + " : " + publicKey + " : " + privateKey);
				gateway = new BraintreeGateway(Environment.SANDBOX, merchantId, publicKey, privateKey);
			}
			else {
				LOG.info("Initialising gateway with keys: " + merchantId + " : " + publicKey + " : " + privateKey);
				gateway = new BraintreeGateway(Environment.PRODUCTION, merchantId, publicKey, privateKey);
			}
		}
	}
	
	private void cancelSubscription(String subscriptionId) throws NoRecordsFetchedException, PaymentException{
		
		LOG.info("Cancelling the subscription with id : " + subscriptionId);
		
		Result<Subscription> result = null;
		
		try{
			LOG.debug("Cancelling the subscription.");
			result = gateway.subscription().cancel(subscriptionId);
		}catch (NotFoundException e) {
			LOG.error("Subscription with id : " + subscriptionId + " not found in the vault.");
			throw new NoRecordsFetchedException("Subscription with id : " + subscriptionId + " not found in the vault.");
			
		}catch (UnexpectedException e) {
			LOG.error("Unexpected Exception occured when cancelling subscription with id : " + subscriptionId);
			throw new PaymentException("Unexpected Exception occured when cancelling subscription with id : " + subscriptionId);
		}
		
		if( result.isSuccess() ) {
			LOG.info("Subscription cancellation successful id : " + subscriptionId);			
		}
		else{
			LOG.info("Subsription cancellation for id : " + subscriptionId + " Unsuccessful.");
		}
	}

	/**
	 * Makes the dao calls to update LicenseDetails table.
	 * 
	 * @param accountsMasterId
	 * @param companyId
	 * @param userId
	 * @throws InvalidInputException
	 */
	private void updateLicenseTable(int accountsMasterId, Company company, User	user, String subscriptionId) throws InvalidInputException {

		if (accountsMasterId <= 0) {
			LOG.error("updateLicenseTable : accountsMasterId parameter is invalid");
			throw new InvalidInputException("updateLicenseTable : accountsMasterId parameter is invalid");
		}

		if (company == null) {
			LOG.error("updateLicenseTable : company parameter is null or invalid");
			throw new InvalidInputException("updateLicenseTable : company parameter is null or invalid");
		}

		if (user == null) {
			LOG.error("updateLicenseTable : userId parameter is null or invalid");
			throw new InvalidInputException("updateLicenseTable : userId parameter is null or invalid");
		}

		if (subscriptionId == null || subscriptionId.isEmpty()) {
			LOG.error("updateLicenseTable : subscriptionId parameter is null or invalid");
			throw new InvalidInputException("updateLicenseTable : subscriptionId parameter is null or invalid");
		}

		AccountsMaster accountsMaster = accountsMasterDao.findById(AccountsMaster.class, accountsMasterId);
		if (accountsMaster == null) {
			LOG.error("updateLicenseTable : null returned by dao for accountsMaster");
			throw new InvalidInputException("updateLicenseTable : null returned by dao for accountsMaster");
		}

		LOG.debug("BrainTreePaymentImpl : updateLicenseTable() : Executing method.");
		LOG.debug("Parameters provided : accountsMasterId : " + accountsMasterId + ", company : " + company.toString() + ", userId : " + user.getUserId());

		LOG.debug("Updating LicenseDetail Table");
		LicenseDetail licenseDetail = new LicenseDetail();
		licenseDetail.setSubscriptionId(subscriptionId);
		licenseDetail.setAccountsMaster(accountsMaster);
		licenseDetail.setCompany(company);
		licenseDetail.setCreatedBy(String.valueOf(user.getUserId()));
		licenseDetail.setModifiedBy(String.valueOf(user.getUserId()));
		licenseDetail.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		licenseDetail.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		licenseDetail.setPaymentMode(CommonConstants.AUTO_PAYMENT_MODE);
		licenseDetail.setNextRetryTime(new Timestamp(CommonConstants.EPOCH_TIME_IN_MILLIS));
		licenseDetail.setSubscriptionIdSource(CommonConstants.PAYMENT_GATEWAY);
		licenseDetail.setStatus(CommonConstants.STATUS_ACTIVE);
		licenseDetail.setLicenseStartDate(new Timestamp(System.currentTimeMillis()));
		licenseDetail.setPaymentRetries(CommonConstants.INITIAL_PAYMENT_RETRIES);
		licenseDetailDao.save(licenseDetail);
		LOG.debug("License detail table updated. Updating the company entity.");
		company.setLicenseDetails(Arrays.asList(licenseDetail));
		LOG.debug("Company entity updated.");
		LOG.debug("LicenseDetail table updated");
	}

	/**
	 * Adds a customer to the Braintree vault with customer details and payment method.
	 * 
	 * @param user
	 *            User object
	 * @param company
	 *            Company object
	 * @param nonce
	 *            payment nonce String given by Braintree
	 * @return Success or Failure of the operation.
	 * @throws InvalidInputException
	 * @throws PaymentException
	 */
	private boolean addCustomerWithPayment(Company company, String nonce) throws InvalidInputException, PaymentException {

		if (company == null) {
			LOG.error("addCustomerWithPayment : company parameter is null!");
			throw new InvalidInputException("addCustomerWithPayment : company parameter is null!");
		}

		if (nonce == null || nonce.isEmpty()) {
			LOG.error("addCustomerWithPayment : nonce parameter is null or empty!");
			throw new InvalidInputException("addCustomerWithPayment : nonce parameter is null or empty!");
		}

		LOG.debug("BrainTreePaymentImpl : addCustomerWithPayment() : Executing method.");
		LOG.debug("Parameters provided : Company : " + company.toString() + ", payment nonce : " + nonce);

		// Creating a new customer object
		CustomerRequest request = new CustomerRequest().id(String.valueOf(company.getCompanyId())).firstName(company.getCompany())
				.paymentMethodNonce(nonce);
		Result<Customer> result = null;

		// Requesting Braintree to create a new customer object
		try {
			result = gateway.customer().create(request);

		}
		catch (UnexpectedException e) {
			LOG.error("addCustomerWithPayment() : Unexpected exception occured while adding customer id : " + company.getCompanyId()
					+ " to the vault");
			throw new PaymentException("addCustomerWithPayment() : Unexpected exception occured while adding customer id : " + company.getCompanyId()
					+ " to the vault");
		}

		LOG.debug("addCustomerWithPayment : adding user " + Long.toString(company.getCompanyId()) + " : Status : " + result.isSuccess()
				+ " Message : " + result.getMessage());

		return result.isSuccess();
	}

	/**
	 * Checks if the Braintree vault has a customer with a particular ID.
	 * 
	 * @param customerId
	 *            a String
	 * @return True or False based on existence.
	 * @throws InvalidInputException
	 * @throws PaymentException
	 */
	private Customer containsCustomer(String customerId) throws InvalidInputException, PaymentException {

		Customer customer = null;

		if (customerId == null || customerId.isEmpty()) {
			LOG.error("containsCustomer : customerId parameter is null or empty!");
			throw new InvalidInputException("containsCustomer : customerId parameter is null or empty!");
		}

		LOG.debug("BrainTreePaymentImpl : containsCustomer() : Executing method.");
		LOG.debug("Parameters provided : customerId : " + customerId);

		try {
			// API call to Braintree to find customer with particular Id
			customer = gateway.customer().find(customerId);
			LOG.debug("containsCustomer : Found customer " + customerId + "Name : " + customer.getFirstName());
		}
		catch (NotFoundException e) {
			LOG.error("Customer " + customerId + " Not Found!");
		}
		catch (UnexpectedException e) {
			LOG.error("containsCustomer() : Unexpected Exception has occured while searching for customer with id " + customerId);
			throw new PaymentException("containsCustomer() : Unexpected Exception has occured while searching for customer with id " + customerId);
		}

		return customer;

	}

	/**
	 * Subscribes a particular customer with identified by the ID to a plan identified by plan ID.
	 * 
	 * @param customerId
	 *            a String
	 * @param planId
	 *            a String
	 * @return Success or Failure of the operation.
	 * @throws InvalidInputException
	 * @throws PaymentException
	 * @throws NonFatalException
	 */
	private String subscribeCustomer(String customerId, String planId) throws InvalidInputException, PaymentException {

		String resultStatus = null;
		Customer customer = null;

		if (customerId == null || customerId.isEmpty()) {
			LOG.error("subscribeCustomer : customerId parameter is null or empty!");
			throw new InvalidInputException("subscribeCustomer : customerId parameter is null or empty!");
		}

		if (planId == null || planId.isEmpty()) {
			LOG.error("subscribeCustomer : planId parameter is null or empty!");
			throw new InvalidInputException("subscribeCustomer : planId parameter is null or empty!");
		}

		LOG.debug("BrainTreePaymentImpl : subscribeCustomer() : Executing method.");
		LOG.debug("Parameters provided : customerId : " + customerId + ", planId : " + planId);

		// Fetch the customer
		customer = containsCustomer(customerId);
		if (customer != null) {
			String paymentToken;

			paymentToken = customer.getPaymentMethods().get(CommonConstants.INITIAL_INDEX).getToken();

			// Make a subscription request
			SubscriptionRequest request = new SubscriptionRequest().planId(planId).paymentMethodToken(paymentToken);
			Result<Subscription> result = null;

			try {
				result = gateway.subscription().create(request);
			}
			catch (UnexpectedException e) {
				LOG.error("subscribeCustomer(): Unexpected Exception occured while subscribing customer with id : " + customerId);
				throw new PaymentException("subscribeCustomer(): Unexpected Exception occured while subscribing customer with id : " + customerId);
			}

			LOG.debug("subscribeCustomer : customerId : " + customerId + " for planId : " + planId + " Status : " + result.isSuccess()
					+ " Message : " + result.getMessage());
			if (result.isSuccess()) {
				resultStatus = result.getTarget().getId();
			}
			else {
				resultStatus = null;
			}
		}
		else {

			LOG.error("Customer with id " + customerId + " not found in vault to make subscription!");
			resultStatus = null;
		}

		return resultStatus;
	}

	/**
	 * This is the service called by the controller to make a subscription.
	 * 
	 * @param user
	 * @param company
	 * @param accountsMasterId
	 * @param nonce
	 * @throws InvalidInputException
	 * @throws PaymentException
	 * @throws NoRecordsFetchedException 
	 */
	@Override
	@Transactional
	public boolean subscribe(User user, Company company, int accountsMasterId, String nonce) throws InvalidInputException, PaymentException, NoRecordsFetchedException {

		boolean result = false;
		String subscriptionId = null;
		
		if (user == null) {
			LOG.error("subscribe : user parameter is null!");
			throw new InvalidInputException("subscribe : user parameter is null!");
		}

		if (company == null) {
			LOG.error("subscribe : company parameter is null!");
			throw new InvalidInputException("subscribe : company parameter is null!");
		}

		if (accountsMasterId <= 0) {
			LOG.error("subscribe : accountsMasterId parameter is invalid! parameter value : " + String.valueOf(accountsMasterId));
			throw new InvalidInputException("subscribe : accountsMasterId parameter is invalid!parameter value : " + String.valueOf(accountsMasterId));
		}

		if (nonce == null || nonce.isEmpty()) {
			LOG.error("subscribe : nonce parameter is null or empty!");
			throw new InvalidInputException("subscribe : nonce parameter is null or empty!");
		}

		LOG.info("Making a subscription!");
		LOG.debug("BrainTreePaymentImpl : subscribe() : Executing method.");
		LOG.debug("Parameters provided : User : " + user.toString() + ", Company : " + company.toString() + ", paymentNonce : " + nonce);

		LOG.debug("Fetching the planId string using property file");
		// Get the plan name used in Braintree

		String braintreePlanName = propertyFileReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE, String.valueOf(accountsMasterId));
		if (braintreePlanName == null) {
			LOG.error("Invalid Plan ID provided for subscription.");
			throw new InvalidInputException("Invalid Plan ID provided for subscription.");
		}

		// Check if the customer already exists in the vault.

		Customer customer = containsCustomer(String.valueOf(company.getCompanyId()));

		if (customer != null) {
			LOG.debug("Customer found in vault. Making subscription.");
			// If he does just subscribe the customer
			subscriptionId = subscribeCustomer(String.valueOf(company.getCompanyId()), braintreePlanName);
		}
		else {
			LOG.debug("Customer does not exist in the vault.Adding customer to vault.");
			// If he doesnt add him to the vault and subscribe him
			if (!addCustomerWithPayment(company, nonce)) {
				LOG.error("Addition of customer with id : " + company.getCompanyId() + " failed! Aborting subscription.");
				result = false;
			}
			else {
				LOG.info("Customer Added. Making subscription.");
				subscriptionId = subscribeCustomer(String.valueOf(company.getCompanyId()), braintreePlanName);
			}
		}

		if (subscriptionId != null) {
			result = true;
			LOG.info("Subscription successful. Updating the license table.");
			try{
				updateLicenseTable(accountsMasterId, company, user, subscriptionId);
				LOG.info("LicenseDetail table update done!");
			}catch ( DatabaseException e){
				LOG.info("Database update was unsuccessful so reverting the braintree subscription.");
				cancelSubscription(subscriptionId);
				LOG.info("Reverted the subscription.");
				throw e;
			}
		}
		else {
			LOG.info("Subscription Unsuccessful!");
		}

		return result;
	}

	/**
	 * Returns a Braintree client token that is used by the frontend to setup the drop-in UI.
	 * 
	 * @return
	 */
	public String getClientToken() {

		LOG.info("Making API call to fetch client token!");
		LOG.debug("BrainTreePaymentImpl : getClientToken() : Executing method.");

		// API call to generate client token
		String clientToken = gateway.clientToken().generate();

		LOG.info("Client token : " + clientToken);
		return clientToken;
	}

	/**
	 * Returns a Braintree client token with the user payment details encrypted into it.
	 * 
	 * @param customerId
	 *            String containing customer ID
	 * @return
	 * @throws InvalidInputException
	 */
	public String getClientTokenWithCustomerId(String customerId) throws InvalidInputException {

		if (customerId == null || customerId.isEmpty()) {
			LOG.error("getClientTokenWithCustomerId : customerId parameter is null or empty!");
			throw new InvalidInputException("getClientTokenWithCustomerId : customerId parameter is null or empty!");
		}
		LOG.info("Making API call to fetch client token for customer ID!");
		LOG.debug("BrainTreePaymentImpl : getClientTokenWithCustomerId() : Executing method.");
		LOG.debug("Parameters provided : customerId : " + customerId);

		// API call to generate client token for a particular Id
		ClientTokenRequest request = new ClientTokenRequest().customerId(customerId);
		String clientToken = gateway.clientToken().generate(request);

		LOG.info("Client token for customer ID " + customerId + " : " + clientToken);
		return clientToken;
	}

	/**
	 * Function to create a Braintree transaction with a particular payment method token and an
	 * amount
	 * 
	 * @param paymentMethodToken
	 * @param amount
	 * @return
	 * @throws InvalidInputException
	 */
	public String makePayment(String paymentMethodToken, BigDecimal amount) throws InvalidInputException {

		String transactionId = null;

		if (paymentMethodToken == null || paymentMethodToken.isEmpty()) {
			LOG.error("makePayment() : paymentMethodToken parameter is null or invalid!");
			throw new InvalidInputException("makePayment() : paymentMethodToken parameter is null or invalid!");
		}

		if (amount == null || amount.equals(0)) {
			LOG.error("makePayment() : amount parameter is null or invalid!");
			throw new InvalidInputException("makePayment() : amount parameter is null or invalid!");
		}

		LOG.debug("Executing makePayment with parameters : paymentMethodToken : " + paymentMethodToken + " , amount" + amount);
		// Initiating a Braintree transaction
		TransactionRequest request = new TransactionRequest();
		request.amount(amount);
		request.paymentMethodToken(paymentMethodToken);

		// API call to make the transaction
		Result<Transaction> result = gateway.transaction().sale(request);

		LOG.debug("Payment status : " + result.isSuccess() + " Message : " + result.getMessage());

		if (result.isSuccess()) {
			// Return the transaction Id
			transactionId = result.getTarget().getId();
		}

		return transactionId;
	}

	@Transactional
	/**
	 * Updates the number of retries in the LicenseDetail table and sends a mail to the User.
	 * @param subscription
	 * @return boolean value
	 */
	public void updateRetriesForPayment(Subscription subscription) throws InvalidInputException, UndeliveredEmailException, NoRecordsFetchedException {
		
		if (subscription == null) {
			LOG.error("subscription parameter to retryPaymentAndUpdateLicenseTable() is null");
			throw new InvalidInputException("subscription parameter to retryPaymentAndUpdateLicenseTable() is null");
		}
		
		LOG.info("Updating the license table with the next retry time!");

		LicenseDetail licenseDetail = null;
		List<LicenseDetail> licenseDetails = null;
		List<User> users = null;
		User user = null;

		// Setting the calendar to retry days ahead of current time
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, retryDays);

		LOG.debug("Executing retryPaymentAndUpdateLicenseTable with parameter : " + subscription.toString());

		String subscriptionId = subscription.getId();

		// Checking if the notification is the first one. If not LicenseDetails table isnt updated
		// again.
		licenseDetails = licenseDetailDao.findByColumn(LicenseDetail.class, CommonConstants.SUBSCRIPTION_ID_COLUMN, subscriptionId);

		if (licenseDetails.isEmpty() || licenseDetails == null) {
			LOG.error("Subscription details not found in the LicenseDetail table.");
			throw new NoRecordsFetchedException("Subscription details not found in the LicenseDetail table.");
		}
		else {
			licenseDetail = licenseDetails.get(CommonConstants.INITIAL_INDEX);
		}
		Timestamp timeOfNotification = new Timestamp(System.currentTimeMillis());

		// Getting the list of corporate admins from user profiles table
		Map<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_COLUMN, licenseDetail.getCompany());
		queries.put(CommonConstants.IS_OWNER_COLUMN, CommonConstants.IS_OWNER);
		users = userDao.findByKeyValue(User.class, queries);

		if (users.isEmpty() || users == null) {
			LOG.error("Corporate Admin details not found in the User table.");
			throw new NoRecordsFetchedException("Corporate Admin details not found in the User table.");
		}
		else {
			user = users.get(CommonConstants.INITIAL_INDEX);
		}

		if (timeOfNotification.before(licenseDetail.getNextRetryTime())) {
			LOG.info("License retry date has already been updated!");
			// LicenseDetail table is not updated.
			return;
		}

		LOG.info("Updating LicenseDetail table with subscriptionId : " + subscriptionId);
		licenseDetail.setIsSubscriptionDue(CommonConstants.SUBSCRIPTION_DUE);
		licenseDetail.setNextRetryTime(new Timestamp(now.getTimeInMillis()));
		licenseDetail.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		licenseDetailDao.update(licenseDetail);
		LOG.info("License table updated!");

		LOG.info("Sending email to the customer!");
		emailServices.sendSubscriptionChargeUnsuccessfulEmail(user.getEmailId(), user.getFirstName()+" "+user.getLastName(), String.valueOf(retryDays));

		LOG.info("Email sent successfully!");

	}

	/**
	 * Retries payment for a particular subscription id and returns the Transaction object.
	 * 
	 * @param subscriptionId
	 * @return
	 * @throws InvalidInputException
	 * @throws PaymentRetryUnsuccessfulException
	 */
	public Transaction retrySubscriptionCharge(String subscriptionId) throws InvalidInputException, PaymentRetryUnsuccessfulException {

		if (subscriptionId == null || subscriptionId.isEmpty()) {
			LOG.error("subscriptionId parameter to retrySubscriptionCharge() is empty or null!");
			throw new InvalidInputException("subscriptionId parameter to retrySubscriptionCharge() is empty or null!");
		}

		Result<Transaction> retryResult = null;

		LOG.info("Retrying subscription charge for id : " + subscriptionId);
		Transaction transaction = null;

		retryResult = gateway.subscription().retryCharge(subscriptionId);

		if (retryResult.isSuccess()) {

			LOG.info("Retry of transaction for subscription id : " + subscriptionId + " Status : " + retryResult.isSuccess());
			transaction = retryResult.getTarget();

			LOG.info("Submitting the transaction with id : " + transaction.getId() + " for settlement.");
			Result<Transaction> result = gateway.transaction().submitForSettlement(retryResult.getTarget().getId());

			if (result.isSuccess()) {
				LOG.info("The transaction has been successfully submitted for settlement.");
			}
			else {
				LOG.error("Submission for transaction settlement for id : " + result.getTarget().getId() + " unsuccessful ");
			}
		}
		else {
			LOG.error("Retry for subscription id : " + subscriptionId + " unsuccessful. Message : " + retryResult.getMessage());
			throw new PaymentRetryUnsuccessfulException("Retry for subscription id : " + subscriptionId + " unsuccessful. Message : "
					+ retryResult.getMessage());
		}

		LOG.info("End of the retrySubscriptionCharge method.");
		return transaction;

	}

	/**
	 * Checks if the status of a particular transaction is settling.
	 * 
	 * @param transactionId
	 * @return
	 * @throws NoRecordsFetchedException
	 * @throws InvalidInputException
	 */
	public boolean checkTransactionSettling(String transactionId) throws NoRecordsFetchedException, InvalidInputException {

		if (transactionId == null || transactionId.isEmpty()) {

			LOG.error("transactionId parameter to checkTransactionSettling is null or empty");
			throw new InvalidInputException("transactionId parameter to checkTransactionSettling is null or empty");

		}

		LOG.info("Finding if the transaction with id : " + transactionId + " is settling.");

		boolean status = false;
		Transaction transaction = null;

		try {
			transaction = gateway.transaction().find(transactionId);

		}
		catch (NotFoundException e) {
			LOG.error("Transaction details not found in the Braintree vault for id :" + transactionId);
			throw new NoRecordsFetchedException("Transaction details not found in the Braintree vault for id :" + transactionId);
		}

		if (transaction.getStatus() == Transaction.Status.AUTHORIZED || transaction.getStatus() == Transaction.Status.SETTLING
				|| transaction.getStatus() == Transaction.Status.SUBMITTED_FOR_SETTLEMENT) {
			status = true;
		}

		return status;

	}

	/**
	 * Checks if the status of a particular transaction is settled.
	 * 
	 * @param transactionId
	 * @return
	 * @throws NoRecordsFetchedException
	 * @throws InvalidInputException
	 */
	public boolean checkTransactionSettled(String transactionId) throws NoRecordsFetchedException, InvalidInputException {

		if (transactionId == null || transactionId.isEmpty()) {

			LOG.error("transactionId parameter to checkTransactionSettled is null or empty");
			throw new InvalidInputException("transactionId parameter to checkTransactionSettled is null or empty");

		}

		LOG.info("Finding if the transaction with id : " + transactionId + " is settled.");

		boolean status = false;
		Transaction transaction = null;

		try {
			transaction = gateway.transaction().find(transactionId);

		}
		catch (NotFoundException e) {
			LOG.error("Transaction details not found in the Braintree vault for id :" + transactionId);
			throw new NoRecordsFetchedException("Transaction details not found in the Braintree vault for id :" + transactionId);
		}

		if (transaction.getStatus() == Transaction.Status.SETTLED) {
			status = true;
		}

		return status;

	}

	/**
	 * Checks if the payment has been made for a particular company.
	 * 
	 * @param company
	 * @return boolean
	 * @throws InvalidInputException
	 */
	@Transactional
	@Override
	public boolean checkIfPaymentMade(Company company) throws InvalidInputException {

		if (company == null) {
			LOG.error("company parameter given to checkIfPaymentMade is null or invalid");
			throw new InvalidInputException("company parameter given to checkIfPaymentMade is null or invalid");
		}

		LOG.info("Checking if the payment is made for comapny with id : " + company.getCompanyId());
		boolean status = false;

		List<LicenseDetail> licenseDetails = new ArrayList<LicenseDetail>();

		LOG.info("Querying the LicenseDetails table to check if payment is made.");
		licenseDetails = licenseDetailDao.findByColumn(LicenseDetail.class, CommonConstants.COMPANY_COLUMN, company);

		if (licenseDetails == null || licenseDetails.isEmpty()) {
			LOG.info("Payment has not been made for company with id : " + company.getCompanyId());
			status = false;
		}
		else {
			LOG.info("Payment has been made for company with id : " + company.getCompanyId());
			status = true;
		}

		return status;
	}
	
	/**
	 * Returns the disable date timestamp for a subscription id.
	 * Set the disable date to a day before the billing date
	 * 
	 * @param subscriptionId
	 * @return
	 * @throws NoRecordsFetchedException
	 * @throws PaymentException
	 * @throws InvalidInputException 
	 */
	@Override
	public Timestamp getDateForCompanyDeactivation(String subscriptionId) throws NoRecordsFetchedException, PaymentException, InvalidInputException{
		
		if(subscriptionId == null || subscriptionId.isEmpty()){
			LOG.error("subscriptionId parameter given to getDisableDate is null or empty");
			throw new InvalidInputException("subscriptionId parameter given to getDisableDate is null or empty");
		}
		LOG.info("Fetching the disable date for the subscription id : " + subscriptionId);
		Timestamp disableDate = null;
		Calendar billingDate = null;
		
		try{
			billingDate = gateway.subscription().find(subscriptionId).getNextBillingDate();
		}catch (NotFoundException e) {
			LOG.error("Subscription details not found in the Braintree vault for id :" + subscriptionId);
			throw new NoRecordsFetchedException("Subscription details not found in the Braintree vault for id :" + subscriptionId);
		}
		catch (UnexpectedException e){
			LOG.error("getDisableDate(): Unexpected Exception occured while fetching disable date for subscription id : " + subscriptionId);
			throw new PaymentException("getDisableDate(): Unexpected Exception occured while fetching disable date for subscription id : " + subscriptionId);		
		}
		
		// Set the disable date to a day before the billing date. So we subtract the a day from the billing date.
		billingDate.add(Calendar.DATE, -1);
		disableDate = new Timestamp(billingDate.getTimeInMillis());
		
		LOG.info("Returning the billing date : " + disableDate.toString());		
		return disableDate;
	}
	
	/**
	 * Unsubscribes the user from the payment gateway
	 * @param subscriptionId
	 * @throws SubscriptionCancellationUnsuccessfulException 
	 * @throws InvalidInputException 
	 */
	@Override
	public void unsubscribe(String subscriptionId) throws SubscriptionCancellationUnsuccessfulException, InvalidInputException {
		
		if(subscriptionId == null || subscriptionId.isEmpty()){
			LOG.error("subscriptionId parameter given to unsubscribe is null or empty");
			throw new InvalidInputException("subscriptionId parameter given to unsubscribe is null or empty");
		}
		LOG.info("Cancelling the subscription with id : " + subscriptionId);
		
		Result<Subscription> result = gateway.subscription().cancel(subscriptionId);
		
		if(result.isSuccess()){
			LOG.info("Subscription cancelletion successful!");
		}
		else{
			LOG.error("Subscription cancellation unsuccessful : Message : " + result.getMessage());
			throw new SubscriptionCancellationUnsuccessfulException("Subscription cancellation unsuccessful : Message : " + result.getMessage());
		}		
	}
	
	/**
	 * Makes a braintree api call to upgrade a subscription.
	 * @param subscriptionId
	 * @param amount
	 * @param braintreePlanId
	 * @throws PaymentException 
	 * @throws InvalidInputException 
	 * @throws SubscriptionUpgradeUnsuccessfulException 
	 * @throws NoRecordsFetchedException 
	 */
	private void upgradeSubscription(String subscriptionId, float amount,String braintreePlanId) throws PaymentException, InvalidInputException, SubscriptionUpgradeUnsuccessfulException, NoRecordsFetchedException{
		
		if( subscriptionId == null || subscriptionId.isEmpty()){
			LOG.error("upgradeSubscription : subscriptionId parameter is null or empty");
			throw new InvalidInputException("upgradeSubscription : subscriptionId parameter is null or empty");
		}
		
		if( amount < 0){
			LOG.error("upgradeSubscription : amount parameter is invalid");
			throw new InvalidInputException("upgradeSubscription : amount parameter is invalid");
		}
		
		if( braintreePlanId == null || braintreePlanId.isEmpty()){
			LOG.error("upgradeSubscription : braintreePlanId parameter is null or empty");
			throw new InvalidInputException("upgradeSubscription : braintreePlanId parameter is null or empty");
		}
				
		LOG.debug("Creating the subscription request object");
		SubscriptionRequest updateRequest = new SubscriptionRequest()
		  .price(new BigDecimal(String.valueOf(amount)))
		  .planId(braintreePlanId)
		  .options()
		    .prorateCharges(true)
		    .revertSubscriptionOnProrationFailure(false)
		    .done();
		
		
		Result<Subscription> result = null;
		try{
			LOG.debug("Making api call to upgrade the subscription");
			result = gateway.subscription().update(subscriptionId, updateRequest);
		}
		catch( NotFoundException e){
			LOG.error("upgradeSubscription : NotFoundException has occured");
			throw new NoRecordsFetchedException("upgradeSubscription : NotFoundException has occured");
		}
		catch(UnexpectedException e){
			LOG.error("upgradeSubscription : UexpectedException has occured");
			throw new PaymentException("upgradeSubscription : UexpectedException has occured");
		}
		
		if(result.isSuccess()){
			LOG.debug("Subscription upgrade successful! ");
		}
		else{
			LOG.debug("Subscription upgrade unsuccessful, message : " + result.getMessage());
			throw new SubscriptionUpgradeUnsuccessfulException("Subscription upgrade unsuccessful, message : " + result.getMessage(),DisplayMessageConstants.SUBSCRIPTION_UPGRADE_UNSUCCESSFUL);
		}		
	}
	
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
	 */
	@Transactional
	@Override
	public void upgradePlanForSubscription(User user, int newAccountsMasterId) throws InvalidInputException, NoRecordsFetchedException, SubscriptionPastDueException, PaymentException, SubscriptionUpgradeUnsuccessfulException, SolrException, UndeliveredEmailException {
		
		if( user == null ){
			LOG.error("upgradePlanForSubscription : User parameter given is null.");
			throw new InvalidInputException("upgradePlanForSubscription : User parameter given is null.");
		}
		
		if( newAccountsMasterId < 0 ){
			LOG.error("upgradePlanForSubscription : newAccountsMasterId parameter given is invalid");
			throw new InvalidInputException("upgradePlanForSubscription : newAccountsMasterId parameter given is invalid");
		}
		
		Company company = user.getCompany();
		//Fetching the new accounts master record
		LOG.info("Fetching the new accounts master record from the database.");
		AccountsMaster newAccountsMaster = accountsMasterDao.findById(AccountsMaster.class, newAccountsMasterId);
		if (newAccountsMaster == null) {
			LOG.error("upgradePlanForSubscription : null returned by dao for accountsMaster");
			throw new InvalidInputException("upgradePlanForSubscription : null returned by dao for accountsMaster");
		}
		LOG.info("Accounts master record fetched.");
		
		//Fetching the license detail record for the company as it holds all the current subscription details.
		LOG.info("Fetching the License Detail record for the company with id : " + company.getCompanyId());
		
		List<LicenseDetail> licenseDetails = licenseDetailDao.findByColumn(LicenseDetail.class, CommonConstants.COMPANY_COLUMN, company);
		
		if(licenseDetails == null || licenseDetails.isEmpty()){
			LOG.error("upgradePlanForSubscription : No records fetched for the company ");
			throw new NoRecordsFetchedException("upgradePlanForSubscription : No records fetched for the company ");
		}
		
		LicenseDetail licenseDetail = licenseDetails.get(CommonConstants.INITIAL_INDEX);
		LOG.info("License Detail record fetched.");
		
		//Checking if the subscription is passed due and throwing an exception if it is due.
		LOG.info("Checking if subscription is due");
		if(licenseDetail.getIsSubscriptionDue() == CommonConstants.STATUS_ACTIVE){
			LOG.error("upgradePlanForSubscription : Upgrade not possible as subscription is due.");
			throw new SubscriptionPastDueException("upgradePlanForSubscription : Upgrade not possible as subscription is due.",DisplayMessageConstants.SUBSCRIPTION_PAST_DUE);
		}
		
		//Getting the braintree id for the new plan
		String braintreePlanId = propertyFileReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE, String.valueOf(newAccountsMasterId));
		if (braintreePlanId == null) {
			LOG.error("Invalid Plan ID provided for subscription.");
			throw new InvalidInputException("Invalid Plan ID provided for subscription.");
		}
		
		//Making API call to Braintree to update subscription.
		LOG.info("Subscription isnt due. So upgrading the subscription");
		upgradeSubscription(licenseDetail.getSubscriptionId(), newAccountsMaster.getAmount(),braintreePlanId);
		LOG.info("Subscription upgraded at braintree");
		
		try{
			//Update the branches and the regions and add settings to mongo
			LOG.info("API call successful, updating the branch and region databases");
			organizationManagementService.upgradeAccount(company, newAccountsMasterId);
			//Updating license detail table.
			LOG.info("Updating the License Detail table to show changes");
			licenseDetail.setAccountsMaster(newAccountsMaster);
			licenseDetail.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			licenseDetailDao.update(licenseDetail);
			LOG.info("License Detail Table updated.Updating the company object to reflect the change.");
			company.setLicenseDetails(Arrays.asList(licenseDetail));
			LOG.info("Company entity updated");
		}catch(DatabaseException e){
			
			LOG.error("Database exception caught while performing the update.Reverting the upgrade!");
			
			//Getting the braintree id for the old plan
			String braintreeOldPlanId = propertyFileReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE, String.valueOf(licenseDetail.getAccountsMaster().getAccountsMasterId()));
			if (braintreeOldPlanId == null) {
				LOG.error("Invalid Plan ID provided for subscription.");
				throw new InvalidInputException("Invalid Plan ID provided for subscription.");
			}
			//Reverting the account to the old plan
			LOG.info("Reverting the subscription to the old plan");
			upgradeSubscription(licenseDetail.getSubscriptionId(), licenseDetail.getAccountsMaster().getAmount(), braintreeOldPlanId);
			throw e;
			
		}
		
		LOG.info("Sending mail to the customer about the upgrade");
		emailServices.sendAccountUpgradeMail(user.getEmailId(), user.getFirstName() + " " + user.getLastName());
		LOG.info("Mail successfully sent");		
		
		LOG.info("Subscription with id : " + licenseDetail.getSubscriptionId() + " successfully upgraded!");
		
	}

	@Override
	public Map<String, String> getCurrentPaymentDetails(String subscriptionId) throws InvalidInputException, NoRecordsFetchedException, PaymentException {
		
		LOG.info("getCurrentPaymentDetails called to fetch the current payment method");
		
		if( subscriptionId == null || subscriptionId.isEmpty() ){
			LOG.error("getCurrentPaymentDetails : subscriptionId parameter is null or empty");
			throw new InvalidInputException("getCurrentPaymentDetails : subscriptionId parameter is null or empty");
		}
		
		Map<String, String> paymentDetailsMap = new HashMap<>();
		
		try{		
			
			//Firstly we get the subscription whose payment method we need
			Subscription subscription = null;
			try{
				LOG.debug("Fetching the subscription object from the vault for subscription id : " + subscriptionId);
				subscription = gateway.subscription().find(subscriptionId);
			}catch(NotFoundException e){
				LOG.error("NotFoundException caught while fetching subscription with id : " + subscriptionId );
				throw new NoRecordsFetchedException("NotFoundException caught while fetching subscription with id : " + subscriptionId);
			}
			
			//Once we have the subscription we use the payment method token to get the payment method from the vault
			CreditCard currentPaymentMethod = null;
			try{
				LOG.debug("Fetching the payment method object from the vault for payment token id : " + subscription.getPaymentMethodToken());
				currentPaymentMethod = (CreditCard) gateway.paymentMethod().find(subscription.getPaymentMethodToken());
			}catch(NotFoundException e){
				LOG.error("NotFoundException caught while fetching payment method with id : " + subscription.getPaymentMethodToken() );
				throw new NoRecordsFetchedException("NotFoundException caught while fetching payment method with id : " + subscription.getPaymentMethodToken());
			}
			
			
			//Now we build the hashmap to be returned
			LOG.debug("Payment details fetched. Building the Hashmap to return");
			paymentDetailsMap.put(CommonConstants.CARD_NUMBER, currentPaymentMethod.getMaskedNumber());
			paymentDetailsMap.put(CommonConstants.CARD_TYPE, currentPaymentMethod.getCardType());
			paymentDetailsMap.put(CommonConstants.CARD_HOLDER_NAME, currentPaymentMethod.getCardholderName());
			paymentDetailsMap.put(CommonConstants.ISSUING_BANK, currentPaymentMethod.getIssuingBank());
			paymentDetailsMap.put(CommonConstants.IMAGE_URL, currentPaymentMethod.getImageUrl());
			
			LOG.debug("Payment details map built");
			
		}catch (UnexpectedException e) {
			LOG.error("UnexpectedException caught : message : " + e.getMessage());
			throw new PaymentException("UnexpectedException caught : message : " + e.getMessage());
		}
		
		LOG.info("Returning the payment method details");
		return paymentDetailsMap;
	}

	@Override
	public boolean changePaymentMethod(String subscriptionId, String paymentNonce, String customerId) throws InvalidInputException, NoRecordsFetchedException, PaymentException {
		
		LOG.info(" changePaymentMethod called to change payment method");
		
		if( subscriptionId == null || subscriptionId.isEmpty() ){
			LOG.error("changePaymentMethod : subscriptionId parameter is null or empty");
			throw new InvalidInputException("getCurrentPaymentDetails : subscriptionId parameter is null or empty");
		}
		if( paymentNonce == null || paymentNonce.isEmpty() ){
			LOG.error("changePaymentMethod : paymentNonce parameter is null or empty");
			throw new InvalidInputException("getCurrentPaymentDetails : paymentNonce parameter is null or empty");
		}
		
		boolean status = false;
		
		try{					
			//Firstly we get the subscription whose payment method we need
			Subscription subscription = null;
			try{
				LOG.debug("Fetching the subscription object from the vault for subscription id : " + subscriptionId);
				subscription = gateway.subscription().find(subscriptionId);
			}catch(NotFoundException e){
				LOG.error("NotFoundException caught while fetching subscription with id : " + subscriptionId );
				throw new NoRecordsFetchedException("NotFoundException caught while fetching subscription with id : " + subscriptionId);
			}
			
			//Next we update the customer with the new payment nonce
			LOG.info("Updating the payment method for customer with id : " + customerId + " to : " + paymentNonce);
			CustomerRequest customerRequest = new CustomerRequest();
			customerRequest.creditCard()
								.paymentMethodNonce(paymentNonce)
								.options()
									.updateExistingToken(subscription.getPaymentMethodToken())
									.verifyCard(true)
									.done();
			Result<Customer> result = gateway.customer().update(customerId, customerRequest);
			if(result.isSuccess()){
				LOG.info("Result : " + result.isSuccess());
				status = true;
			}
			else{
				LOG.info("Result : " + result.isSuccess() + " message : " + result.getMessage());
				status = false;
			}
			
		}catch (UnexpectedException e) {
			LOG.error("UnexpectedException caught : message : " + e.getMessage());
			throw new PaymentException("UnexpectedException caught : message : " + e.getMessage());
		}		
		
		LOG.info("Returning status");
		return status;
	}	
}
