package com.realtech.socialsurvey.core.services.payment.impl;

// JIRA: SS-15: By RM03

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentRetryUnsuccessfulException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
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
	private GenericDao<AccountsMaster, Long> accountsMasterDao;

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

	/**
	 * Returns the the Braintree gateway.
	 */
	public BraintreeGateway getGatewayInstance() {
		return gateway;
	}

	/**
	 * Makes the dao calls to update LicenseDetails table.
	 * 
	 * @param accountsMasterId
	 * @param companyId
	 * @param userId
	 * @throws InvalidInputException
	 */
	private void updateLicenseTable(Long accountsMasterId, Company company, long userId, String subscriptionId) throws InvalidInputException {

		if (accountsMasterId <= 0) {
			LOG.error("updateLicenseTable : first parameter is invalid");
			throw new InvalidInputException("updateLicenseTable : first parameter is invalid");
		}

		if (company == null) {
			LOG.error("updateLicenseTable : second parameter is null or invalid");
			throw new InvalidInputException("updateLicenseTable : second parameter is null or invalid");
		}

		if (userId <= 0) {
			LOG.error("updateLicenseTable : third parameter is invalid");
			throw new InvalidInputException("updateLicenseTable : third parameter is invalid");
		}

		if (subscriptionId == null || subscriptionId.isEmpty()) {
			LOG.error("updateLicenseTable : fourth parameter is null or invalid");
			throw new InvalidInputException("updateLicenseTable : fourth parameter is null or invalid");
		}

		AccountsMaster accountsMaster = accountsMasterDao.findById(AccountsMaster.class, accountsMasterId);
		if (accountsMaster == null) {
			LOG.error("updateLicenseTable : null returned by dao for accountsMaster");
			throw new InvalidInputException("updateLicenseTable : null returned by dao for accountsMaster");
		}

		LOG.debug("BrainTreePaymentImpl : updateLicenseTable() : Executing method.");
		LOG.debug("Parameters provided : accountsMasterId : " + accountsMasterId + ", company : " + company.toString() + ", userId : " + userId);

		LOG.debug("Updating LicenseDetail Table");
		LicenseDetail licenseDetail = new LicenseDetail();
		licenseDetail.setSubscriptionId(subscriptionId);
		licenseDetail.setAccountsMaster(accountsMaster);
		licenseDetail.setCompany(company);
		licenseDetail.setCreatedBy(String.valueOf(userId));
		licenseDetail.setModifiedBy(String.valueOf(userId));
		licenseDetail.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		licenseDetail.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		licenseDetail.setPaymentMode(CommonConstants.AUTO_PAYMENT_MODE);
		licenseDetail.setNextRetryTime(new Timestamp(CommonConstants.EPOCH_TIME_IN_MILLIS));
		licenseDetail.setSubscriptionIdSource(CommonConstants.PAYMENT_GATEWAY);
		licenseDetail.setStatus(CommonConstants.STATUS_ACTIVE);
		licenseDetail.setLicenseStartDate(new Timestamp(System.currentTimeMillis()));
		licenseDetail.setPaymentRetries(CommonConstants.INITIAL_PAYMENT_RETRIES);
		licenseDetailDao.save(licenseDetail);
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
			LOG.error("addCustomerWithPayment : first parameter is null!");
			throw new InvalidInputException("addCustomerWithPayment : first parameter is null!");
		}

		if (nonce == null || nonce.isEmpty()) {
			LOG.error("addCustomerWithPayment : second parameter is null or empty!");
			throw new InvalidInputException("addCustomerWithPayment : parameter is null or empty!");
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
			LOG.error("containsCustomer : parameter is null or empty!");
			throw new InvalidInputException("containsCustomer : parameter is null or empty!");
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
			LOG.error("subscribeCustomer : first parameter is null or empty!");
			throw new InvalidInputException("subscribeCustomer : first parameter is null or empty!");
		}

		if (planId == null || planId.isEmpty()) {
			LOG.error("subscribeCustomer : second parameter is null or empty!");
			throw new InvalidInputException("subscribeCustomer : second parameter is null or empty!");
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
	 * @param planId
	 * @param nonce
	 * @throws InvalidInputException
	 * @throws PaymentException
	 */
	@Override
	@Transactional
	public boolean subscribe(User user, Company company, Long planId, String nonce) throws InvalidInputException, PaymentException {

		boolean result = false;
		String subscriptionId = null;

		if (company == null) {
			LOG.error("subscribe : first parameter is null!");
			throw new InvalidInputException("subscribe : first parameter is null!");
		}

		if (planId <= 0) {
			LOG.error("subscribe : second parameter is invalid! parameter value : " + String.valueOf(planId));
			throw new InvalidInputException("subscribe : second parameter is invalid!parameter value : " + String.valueOf(planId));
		}

		if (nonce == null || nonce.isEmpty()) {
			LOG.error("subscribe : third parameter is null or empty!");
			throw new InvalidInputException("subscribe : third parameter is null or empty!");
		}

		LOG.info("Making a subscription!");
		LOG.debug("BrainTreePaymentImpl : subscribe() : Executing method.");
		LOG.debug("Parameters provided : User : " + user.toString() + ", Company : " + company.toString() + ", paymentNonce : " + nonce);

		LOG.debug("Fetching the planId string using property file");
		// Get the plan name used in Braintree

		String planIdString = propertyFileReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE, String.valueOf(planId));
		if (planIdString == null) {
			LOG.error("Invalid Plan ID provided for subscription.");
			throw new InvalidInputException("Invalid Plan ID provided for subscription.");
		}

		// Check if the customer already exists in the vault.

		Customer customer = containsCustomer(String.valueOf(company.getCompanyId()));

		if (customer != null) {
			LOG.debug("Customer found in vault. Making subscription.");
			// If he does just subscribe the customer
			subscriptionId = subscribeCustomer(String.valueOf(company.getCompanyId()), planIdString);
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
				subscriptionId = subscribeCustomer(String.valueOf(company.getCompanyId()), planIdString);
			}
		}

		if (subscriptionId != null) {
			result = true;
			LOG.info("Subscription successful. Updating the license table.");
			updateLicenseTable(planId, company, user.getUserId(), subscriptionId);
			LOG.info("LicenseDetail table update done!");
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
			LOG.error("getClientTokenWithCustomerId : parameter is null or empty!");
			throw new InvalidInputException("getClientTokenWithCustomerId : parameter is null or empty!");
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
			LOG.error("makePayment() : first parameter is null or invalid!");
			throw new InvalidInputException("makePayment() : first parameter is null or invalid!");
		}

		if (amount == null || amount.equals(0)) {
			LOG.error("makePayment() : second parameter is null or invalid!");
			throw new InvalidInputException("makePayment() : second parameter is null or invalid!");
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

		LOG.info("Updating the license table with the next retry time!");

		LicenseDetail licenseDetail = null;
		List<LicenseDetail> licenseDetails = null;
		List<User> users = null;
		User user = null;

		// Setting the calendar to retry days ahead of current time
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, retryDays);

		if (subscription == null) {
			LOG.error("Parameter to retryPaymentAndUpdateLicenseTable() is null");
			throw new InvalidInputException("Parameter to retryPaymentAndUpdateLicenseTable() is null");
		}

		LOG.debug("Executing retryPaymentAndUpdateLicenseTable with parameter : " + subscription.toString());

		String subscriptionId = subscription.getId();

		// Checking if the notification is the first one. If not LicenseDetails table isnt updated
		// again.
		licenseDetails = licenseDetailDao.findByColumn(LicenseDetail.class, "subscriptionId", subscriptionId);

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
		queries.put("company", licenseDetail.getCompany());
		queries.put("isOwner", CommonConstants.IS_OWNER);
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
		licenseDetailDao.saveOrUpdate(licenseDetail);
		LOG.info("License table updated!");

		LOG.info("Sending email to the customer!");
		emailServices.sendSubscriptionChargeUnsuccessfulEmail(user.getEmailId(), user.getDisplayName(), String.valueOf(retryDays));

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
			LOG.error("Parameter to retrySubscriptionCharge() is empty or null!");
			throw new InvalidInputException("Parameter to retrySubscriptionCharge() is empty or null!");
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

			LOG.error("Parameter to checkTransactionSettling is null or empty");
			throw new InvalidInputException("Parameter to checkTransactionSettling is null or empty");

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

			LOG.error("Parameter to checkTransactionSettled is null or empty");
			throw new InvalidInputException("Parameter to checkTransactionSettled is null or empty");

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
			LOG.error("Argument given to checkIfPaymentMade is null or invalid");
			throw new InvalidInputException("Argument given to checkIfPaymentMade is null or invalid");
		}

		LOG.info("Checking if the payment is made for comapny with id : " + company.getCompanyId());
		boolean status = false;

		List<LicenseDetail> licenseDetails = new ArrayList<LicenseDetail>();

		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_COLUMN, company);

		LOG.info("Querying the LicenseDetails table to check if payment is made.");
		licenseDetails = licenseDetailDao.findByKeyValue(LicenseDetail.class, queries);

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
	 */
	@Override
	public Timestamp getDateForCompanyDeactivation(String subscriptionId) throws NoRecordsFetchedException, PaymentException{
		
		if(subscriptionId == null || subscriptionId.isEmpty()){
			LOG.error("Parameter given to getDisableDate is null or empty");
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
}