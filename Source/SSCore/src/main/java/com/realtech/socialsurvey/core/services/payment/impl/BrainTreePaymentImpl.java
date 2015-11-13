package com.realtech.socialsurvey.core.services.payment.impl;

// JIRA: SS-15: By RM03

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
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
import com.braintreegateway.ValidationError;
import com.braintreegateway.ValidationErrors;
import com.braintreegateway.exceptions.BraintreeException;
import com.braintreegateway.exceptions.DownForMaintenanceException;
import com.braintreegateway.exceptions.NotFoundException;
import com.braintreegateway.exceptions.UnexpectedException;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.CoreCommon;
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
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.payment.exception.CardUpdateUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.CreditCardException;
import com.realtech.socialsurvey.core.services.payment.exception.CustomerDeletionUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentRetryUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionCancellationUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionPastDueException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUpgradeUnsuccessfulException;
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
	private GenericDao<Company, Long> companyDao;

	@Autowired
	private PropertyFileReader propertyFileReader;

	@Autowired
	private EmailServices emailServices;

	@Autowired
	private CoreCommon coreCommonServices;

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
	
	private static final DecimalFormat AMOUNT_FORMAT = new DecimalFormat("###.##"); 

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
		
		// set rounding mode
		AMOUNT_FORMAT.setRoundingMode(RoundingMode.HALF_EVEN);
	}

	private void cancelSubscription(String subscriptionId) throws NoRecordsFetchedException, PaymentException {

		LOG.info("Cancelling the subscription with id : " + subscriptionId);

		Result<Subscription> result = null;

		try {
			LOG.debug("Cancelling the subscription.");
			result = gateway.subscription().cancel(subscriptionId);
		}
		catch (NotFoundException e) {
			LOG.error("Subscription with id : " + subscriptionId + " not found in the vault.");
			throw new NoRecordsFetchedException("Subscription with id : " + subscriptionId + " not found in the vault.");

		}
		catch (UnexpectedException | DownForMaintenanceException e) {
			LOG.error("Unexpected Exception occured when cancelling subscription with id : " + subscriptionId);
			throw new PaymentException("Unexpected Exception occured when cancelling subscription with id : " + subscriptionId);
		}
		catch (BraintreeException e) {
			LOG.error("BraintreeException occured when cancelling subscription with id : " + subscriptionId + " message : " + e.getMessage());
			throw new PaymentException("BraintreeException occured when cancelling subscription with id : " + subscriptionId, e);
		}

		if (result.isSuccess()) {
			LOG.info("Subscription cancellation successful id : " + subscriptionId);
		}
		else {
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
	@Transactional
	@Override
	public void insertIntoLicenseTable(int accountsMasterId, User user, String subscriptionId) throws InvalidInputException {

		if (accountsMasterId <= 0) {
			LOG.error("updateLicenseTable : accountsMasterId parameter is invalid");
			throw new InvalidInputException("updateLicenseTable : accountsMasterId parameter is invalid");
		}

		if (user == null) {
			LOG.error("updateLicenseTable : userId parameter is null or invalid");
			throw new InvalidInputException("updateLicenseTable : userId parameter is null or invalid");
		}

		if (accountsMasterId != CommonConstants.ACCOUNTS_MASTER_FREE && (subscriptionId == null || subscriptionId.isEmpty())) {
			LOG.error("updateLicenseTable : subscriptionId parameter is null or invalid");
			throw new InvalidInputException("updateLicenseTable : subscriptionId parameter is null or invalid");
		}

		Company company = user.getCompany();

		AccountsMaster accountsMaster = accountsMasterDao.findById(AccountsMaster.class, accountsMasterId);
		if (accountsMaster == null) {
			LOG.error("updateLicenseTable : null returned by dao for accountsMaster");
			throw new InvalidInputException("updateLicenseTable : null returned by dao for accountsMaster");
		}

		LOG.debug("BrainTreePaymentImpl : updateLicenseTable() : Executing method.");
		LOG.debug("Parameters provided : accountsMasterId : " + accountsMasterId + ", company : " + company.toString() + ", userId : "
				+ user.getUserId());

		LOG.debug("Inserting into LicenseDetail Table");
		LicenseDetail licenseDetail = new LicenseDetail();
		if (accountsMasterId != CommonConstants.ACCOUNTS_MASTER_FREE) {
			LOG.debug("Not a free account. Hence, a subscription id is necessary.");
			licenseDetail.setSubscriptionId(subscriptionId);
		}
		else {
			LOG.debug("For free account there will be no subscription id. Hence, setting it as null.");
			licenseDetail.setSubscriptionId(null);
		}
		licenseDetail.setAccountsMaster(accountsMaster);
		licenseDetail.setCompany(company);
		licenseDetail.setCreatedBy(String.valueOf(user.getUserId()));
		licenseDetail.setModifiedBy(String.valueOf(user.getUserId()));
		licenseDetail.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		licenseDetail.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		licenseDetail.setPaymentMode(user.getCompany().getBillingMode());
		licenseDetail.setNextRetryTime(new Timestamp(CommonConstants.EPOCH_TIME_IN_MILLIS));
		licenseDetail.setSubscriptionIdSource(CommonConstants.PAYMENT_GATEWAY);
		licenseDetail.setStatus(CommonConstants.STATUS_ACTIVE);
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		licenseDetail.setLicenseStartDate(currentTime);
		licenseDetail.setLicenseEndDate(currentTime);
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
	 * @throws CreditCardException
	 * @throws SubscriptionUnsuccessfulException
	 */
	private void addCustomerWithPayment(Company company, String nonce) throws InvalidInputException, PaymentException, CreditCardException,
			SubscriptionUnsuccessfulException {

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
		catch (UnexpectedException | DownForMaintenanceException e) {
			LOG.error("addCustomerWithPayment() : Unexpected exception occured while adding customer id : " + company.getCompanyId()
					+ " to the vault. Message : " + e.getMessage());
			throw new PaymentException("addCustomerWithPayment() : Unexpected exception occured while adding customer id : " + company.getCompanyId()
					+ " to the vault");
		}
		catch (BraintreeException e) {
			LOG.error("addCustomerWithPayment() : Unexpected exception occured while adding customer id : " + company.getCompanyId()
					+ " to the vault. Message : " + e.getMessage());
			throw new PaymentException("addCustomerWithPayment() : Unexpected exception occured while adding customer id : " + company.getCompanyId()
					+ " to the vault.", e);
		}

		LOG.debug("addCustomerWithPayment : adding user " + Long.toString(company.getCompanyId()) + " : Status : " + result.isSuccess()
				+ " Message : " + result.getMessage());

		if (!result.isSuccess()) {
			ValidationErrors creditCardErrors = result.getErrors().forObject("customer").forObject("creditCard");
			if (creditCardErrors.size() > 0) {
				String errorMessage = "";
				for (ValidationError error : creditCardErrors.getAllValidationErrors()) {
					errorMessage += " Error Code : " + error.getCode();
					errorMessage += " Error message : " + error.getMessage() + "\n";
				}

				throw new CreditCardException("Credit Card Validation failed, reason : \n " + errorMessage);
			}

			List<ValidationError> allErrors = result.getErrors().getAllDeepValidationErrors();
			if (allErrors.size() > 0) {
				String errorMessage = "";
				for (ValidationError error : allErrors) {
					errorMessage += " Error Code : " + error.getCode();
					errorMessage += " Error message : " + error.getMessage() + "\n";
				}

				throw new SubscriptionUnsuccessfulException("Subscription creation failed, reason : \n " + errorMessage);
			}

		}
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
		catch (UnexpectedException | DownForMaintenanceException e) {
			LOG.error("containsCustomer() : Unexpected Exception has occured while searching for customer with id " + customerId + " Message : "
					+ e.getMessage());
			throw new PaymentException("containsCustomer() : Unexpected Exception has occured while searching for customer with id " + customerId);
		}
		catch (BraintreeException e) {
			LOG.error("containsCustomer() : BraintreeException has occured while searching for customer with id " + customerId + " Message : "
					+ e.getMessage());
			throw new PaymentException("containsCustomer() : BraintreeException has occured while searching for customer with id " + customerId);
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
	 * @throws SubscriptionUnsuccessfulException
	 * @throws NoRecordsFetchedException
	 * @throws CreditCardException
	 * @throws NonFatalException
	 */
	private String subscribeCustomer(String customerId, String planId) throws InvalidInputException, PaymentException,
			SubscriptionUnsuccessfulException, NoRecordsFetchedException, CreditCardException {

		String subscriptionId = null;
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
				LOG.error("subscribeCustomer(): Unexpected Exception occured while subscribing customer with id : " + customerId + " Message : "
						+ e.getMessage());
				throw new PaymentException("subscribeCustomer(): Unexpected Exception occured while subscribing customer with id : " + customerId);
			}
			catch (BraintreeException e) {
				LOG.error("subscribeCustomer(): BraintreeException occured while subscribing customer with id : " + customerId + " Message : "
						+ e.getMessage());
				throw new PaymentException("subscribeCustomer(): BraintreeException occured while subscribing customer with id : " + customerId);
			}

			LOG.debug("subscribeCustomer : customerId : " + customerId + " for planId : " + planId + " Status : " + result.isSuccess()
					+ " Message : " + result.getMessage());
			if (result.isSuccess()) {
				subscriptionId = result.getTarget().getId();
				LOG.info("Subscription successful, subscription id : " + subscriptionId);
			}
			else {

				LOG.info("Result : " + result.isSuccess() + " message : " + result.getMessage());
				ValidationErrors creditCardErrors = result.getErrors().forObject("subscription").forObject("creditCard");
				if (creditCardErrors.size() > 0) {
					String errorMessage = "";
					for (ValidationError error : creditCardErrors.getAllValidationErrors()) {
						errorMessage += " Error Code : " + error.getCode();
						errorMessage += " Error message : " + error.getMessage() + "\n";
					}
					throw new CreditCardException("Credit Card Validation failed, reason : \n " + errorMessage,
							DisplayMessageConstants.CREDIT_CARD_INVALID);
				}
				ValidationErrors allErrors = result.getErrors();
				if (allErrors.size() > 0) {
					String errorMessage = "";
					for (ValidationError error : creditCardErrors.getAllValidationErrors()) {
						errorMessage += " Error Code : " + error.getCode();
						errorMessage += " Error message : " + error.getMessage() + "\n";
					}
					LOG.info(errorMessage);
					throw new CreditCardException("Credit Card Validation failed, reason : \n " + errorMessage,
							DisplayMessageConstants.CREDIT_CARD_INVALID);
				}
				if (!result.getTransaction().getProcessorResponseCode().isEmpty()) {
					LOG.error("Subscription Unsuccessful : PROCESSOR REJECTED message : " + result.getMessage());
					throw new SubscriptionUnsuccessfulException("Subscription Unsuccessful : message : " + result.getMessage(),
							DisplayMessageConstants.BANK_REJECTED);
				}

				LOG.error("Subscription Unsuccessful : message : " + result.getMessage());
				throw new SubscriptionUnsuccessfulException("Subscription Unsuccessful : message : " + result.getMessage());
			}
		}
		else {
			LOG.error("Customer with id " + customerId + " not found in vault to make subscription!");
			throw new NoRecordsFetchedException("Customer with id " + customerId + " not found in vault to make subscription!");
		}
		return subscriptionId;
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
	 * @throws SubscriptionUnsuccessfulException
	 * @throws CreditCardException
	 */
	@Override
	@Transactional
	public void subscribe(User user, int accountsMasterId, String nonce) throws InvalidInputException, PaymentException, NoRecordsFetchedException,
			SubscriptionUnsuccessfulException, CreditCardException {

		String subscriptionId = null;

		if (user == null) {
			LOG.error("subscribe : user parameter is null!");
			throw new InvalidInputException("subscribe : user parameter is null!");
		}
		// Checking for the range of allowed account type, which is 1 to 5
		if (accountsMasterId < CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL || accountsMasterId > CommonConstants.ACCOUNTS_MASTER_FREE) {
			LOG.error("subscribe : accountsMasterId parameter is invalid! parameter value : " + String.valueOf(accountsMasterId));
			throw new InvalidInputException("subscribe : accountsMasterId parameter is invalid!parameter value : " + String.valueOf(accountsMasterId));
		}
		// Free account will not have a nonce
		if (accountsMasterId != CommonConstants.ACCOUNTS_MASTER_FREE && (nonce == null || nonce.isEmpty())) {
			LOG.error("subscribe : nonce parameter is null or empty!");
			throw new InvalidInputException("subscribe : nonce parameter is null or empty!");
		}

		// Getting the company from the user
		Company company = user.getCompany();

		if (accountsMasterId != CommonConstants.ACCOUNTS_MASTER_FREE) {
			LOG.info("Making a subscription!");
			LOG.debug("BrainTreePaymentImpl : subscribe() : Executing method.");

			LOG.debug("Parameters provided : User : " + user.toString() + ", Company : " + company.toString() + ", paymentNonce : " + nonce);

			LOG.debug("Fetching the planId string using property file");
			// Get the plan name used in Braintree

			String braintreePlanName = getBraintreePlanId(accountsMasterId);

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
				addCustomerWithPayment(company, nonce);
				LOG.info("Customer Added. Making subscription.");
				subscriptionId = subscribeCustomer(String.valueOf(company.getCompanyId()), braintreePlanName);
			}
		}
		LOG.info("Subscription successful. Updating the license table.");
		try {
			insertIntoLicenseTable(accountsMasterId, user, subscriptionId);
			LOG.info("LicenseDetail table update done!");
		}
		catch (DatabaseException e) {
			LOG.info("Database update was unsuccessful so reverting the braintree subscription.");
			cancelSubscription(subscriptionId);
			LOG.info("Reverted the subscription.");
			throw e;
		}
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

	@Override
	@Transactional
	/**
	 * On getting a SUBSCRIPTION WENT PAST DUE webhook it updates license details table and sends a mail
	 * @param subscription
	 * @return boolean value
	 * @throws UndeliveredEmailException 
	 * @throws NoRecordsFetchedException 
	 */
	public void changeLicenseToPastDue(Subscription subscription) throws InvalidInputException, UndeliveredEmailException, NoRecordsFetchedException {

		if (subscription == null) {
			LOG.error("subscription parameter to retryPaymentAndUpdateLicenseTable() is null");
			throw new InvalidInputException("subscription parameter to retryPaymentAndUpdateLicenseTable() is null");
		}

		LOG.info("Updating the license table with the next retry time!");

		LicenseDetail licenseDetail = null;
		List<LicenseDetail> licenseDetails = null;
		List<User> users = null;
		User user = null;

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

		LOG.info("Updating LicenseDetail table with subscriptionId : " + subscriptionId);
		licenseDetail.setIsSubscriptionDue(CommonConstants.SUBSCRIPTION_DUE);
		licenseDetail.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		licenseDetailDao.update(licenseDetail);
		LOG.info("License table updated!");

		LOG.info("Sending email to the customer!");
		emailServices.sendSubscriptionChargeUnsuccessfulEmail(user.getEmailId(), user.getFirstName() + " " + user.getLastName(),
				String.valueOf(retryDays));

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
	 * Returns the disable date timestamp for a subscription id. Set the disable date to a day
	 * before the billing date
	 * 
	 * @param subscriptionId
	 * @return
	 * @throws NoRecordsFetchedException
	 * @throws PaymentException
	 * @throws InvalidInputException
	 */
	@Override
	public Timestamp getDateForCompanyDeactivation(String subscriptionId) throws NoRecordsFetchedException, PaymentException, InvalidInputException {

		if (subscriptionId == null || subscriptionId.isEmpty()) {
			LOG.error("subscriptionId parameter given to getDisableDate is null or empty");
			throw new InvalidInputException("subscriptionId parameter given to getDisableDate is null or empty");
		}
		LOG.info("Fetching the disable date for the subscription id : " + subscriptionId);
		Timestamp disableDate = null;
		Calendar billingDate = null;

		try {
			billingDate = gateway.subscription().find(subscriptionId).getNextBillingDate();
		}
		catch (NotFoundException e) {
			LOG.error("Subscription details not found in the Braintree vault for id :" + subscriptionId);
			throw new NoRecordsFetchedException("Subscription details not found in the Braintree vault for id :" + subscriptionId);
		}
		catch (UnexpectedException | DownForMaintenanceException e) {
			LOG.error("getDisableDate(): Unexpected Exception occured while fetching disable date for subscription id : " + subscriptionId);
			throw new PaymentException("getDisableDate(): Unexpected Exception occured while fetching disable date for subscription id : "
					+ subscriptionId);
		}
		catch (BraintreeException e) {
			LOG.error("getDisableDate(): BraintreeException occured while fetching disable date for subscription id : " + subscriptionId);
			throw new PaymentException("getDisableDate(): BraintreeException occured while fetching disable date for subscription id : "
					+ subscriptionId);
		}

		// Set the disable date to a day before the billing date. So we subtract the a day from the
		// billing date.
		billingDate.add(Calendar.DATE, -1);
		disableDate = new Timestamp(billingDate.getTimeInMillis());

		LOG.info("Returning the billing date : " + disableDate.toString());
		return disableDate;
	}

	/**
	 * Unsubscribes the user from the payment gateway
	 * 
	 * @param subscriptionId
	 * @throws SubscriptionCancellationUnsuccessfulException
	 * @throws InvalidInputException
	 */
	@Override
	public void unsubscribe(String subscriptionId) throws SubscriptionCancellationUnsuccessfulException, InvalidInputException {

		if (subscriptionId == null || subscriptionId.isEmpty()) {
			LOG.error("subscriptionId parameter given to unsubscribe is null or empty");
			throw new InvalidInputException("subscriptionId parameter given to unsubscribe is null or empty");
		}
		LOG.info("Cancelling the subscription with id : " + subscriptionId);

		Result<Subscription> result = gateway.subscription().cancel(subscriptionId);

		if (result.isSuccess()) {
			LOG.info("Subscription cancelletion successful!");
		}
		else {
			LOG.error("Subscription cancellation unsuccessful : Message : " + result.getMessage());
			throw new SubscriptionCancellationUnsuccessfulException("Subscription cancellation unsuccessful : Message : " + result.getMessage());
		}
	}

	
	/*
     * (non-Javadoc)
     * @see com.realtech.socialsurvey.core.services.payment.Payment#deleteCustomer(java.lang.String)
     */
    @Override
    public void deleteCustomer(String customerId) throws CustomerDeletionUnsuccessfulException, InvalidInputException {

        if (customerId == null || customerId.isEmpty()) {
            LOG.error("customerId parameter given to delete the customer is null or empty");
            throw new InvalidInputException("customerId parameter given to delete the customer is null or empty");
        }
        LOG.info("Deleting the customer with id : " + customerId);

        Result<Customer> result = gateway.customer().delete( customerId );

        if (result.isSuccess()) {
            LOG.info("Customer deletion successful!");
        }
        else {
            LOG.error("Customer deletion unsuccessful : Message : " + result.getMessage());
            throw new CustomerDeletionUnsuccessfulException("Customer deletion unsuccessful : Message : " + result.getMessage());
        }
    }
	
	/**
	 * Makes a braintree api call to upgrade a subscription.
	 * 
	 * @param subscriptionId
	 * @param amount
	 * @param braintreePlanId
	 * @throws PaymentException
	 * @throws InvalidInputException
	 * @throws SubscriptionUpgradeUnsuccessfulException
	 * @throws NoRecordsFetchedException
	 */
	private void upgradeSubscription(String subscriptionId, float amount, String braintreePlanId) throws PaymentException, InvalidInputException,
			SubscriptionUpgradeUnsuccessfulException, NoRecordsFetchedException {

		if (subscriptionId == null || subscriptionId.isEmpty()) {
			LOG.error("upgradeSubscription : subscriptionId parameter is null or empty");
			throw new InvalidInputException("upgradeSubscription : subscriptionId parameter is null or empty");
		}

		if (amount < 0) {
			LOG.error("upgradeSubscription : amount parameter is invalid");
			throw new InvalidInputException("upgradeSubscription : amount parameter is invalid");
		}

		if (braintreePlanId == null || braintreePlanId.isEmpty()) {
			LOG.error("upgradeSubscription : braintreePlanId parameter is null or empty");
			throw new InvalidInputException("upgradeSubscription : braintreePlanId parameter is null or empty");
		}
		String sAmount = AMOUNT_FORMAT.format(amount);
		LOG.debug("Creating the subscription request object");
		SubscriptionRequest updateRequest = new SubscriptionRequest().price(new BigDecimal(sAmount));

		Result<Subscription> result = null;
		try {
			LOG.debug("Making api call to upgrade the subscription");
			result = gateway.subscription().update(subscriptionId, updateRequest);
		}
		catch (NotFoundException e) {
			LOG.error("upgradeSubscription : NotFoundException has occured");
			throw new NoRecordsFetchedException("upgradeSubscription : NotFoundException has occured");
		}
		catch (UnexpectedException | DownForMaintenanceException e) {
			LOG.error("upgradeSubscription : UexpectedException has occured");
			throw new PaymentException("upgradeSubscription : UexpectedException has occured", DisplayMessageConstants.PAYMENT_GATEWAY_EXCEPTION);
		}
		catch (BraintreeException e) {
			LOG.error("upgradeSubscription : BraintreeException has occured. Message : " + e.getMessage(), e);
			throw new PaymentException("upgradeSubscription : BraintreeException has occured", DisplayMessageConstants.PAYMENT_GATEWAY_EXCEPTION);
		}

		if (result.isSuccess()) {
			LOG.debug("Subscription upgrade successful! ");
		}
		else {
			LOG.debug("Subscription upgrade unsuccessful, message : " + result.getMessage());
			String errorMessage = "";
			if (result.getErrors().getAllValidationErrors().size() > 0) {
				for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
					errorMessage += "Error code : " + error.getCode();
					errorMessage += " Message : " + error.getMessage() + "\n";
				}
			}
			if (!result.getTransaction().getProcessorResponseCode().isEmpty()) {
				throw new SubscriptionUpgradeUnsuccessfulException("Subscription upgrade unsuccessful, message : \n" + errorMessage,
						DisplayMessageConstants.BANK_REJECTED);
			}
			throw new SubscriptionUpgradeUnsuccessfulException("Subscription upgrade unsuccessful, message : \n" + errorMessage,
					DisplayMessageConstants.SUBSCRIPTION_UPGRADE_UNSUCCESSFUL);
		}
	}

	/**
	 * Fetches the braintree plan id from the property file
	 * 
	 * @param accountsMasterId
	 * @return
	 * @throws InvalidInputException
	 */
	private String getBraintreePlanId(int accountsMasterId) throws InvalidInputException {
		if (accountsMasterId < CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL && accountsMasterId > CommonConstants.ACCOUNTS_MASTER_FREE) {
			LOG.error("getBraintreePlanId : accountsMAsterId parameter is invalid!");
			throw new InvalidInputException("getBraintreePlanId : accountsMAsterId parameter is invalid!");
		}
		LOG.debug("getBraintreePlanId called");
		// Getting the braintree id for the new plan
		String braintreePlanId = propertyFileReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE, String.valueOf(accountsMasterId));
		if (braintreePlanId == null) {
			LOG.error("Invalid Plan ID provided for subscription.");
			throw new InvalidInputException("Invalid Plan ID provided for subscription.");
		}
		LOG.debug("Returning the braintree plan name");
		return braintreePlanId;
	}

	/**
	 * Updates the license detail table on plan upgrade
	 * 
	 * @param licenseDetail
	 * @param company
	 * @param newAccountsMaster
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	private void updateLicenseDetailsTableOnPlanUpgrade(User user, LicenseDetail licenseDetail, Company company, AccountsMaster newAccountsMaster,
			String subscriptionId) throws InvalidInputException, NoRecordsFetchedException {

		if (licenseDetail == null) {
			LOG.error("updateLicenseDetailsTableOnPlanUpgrade : license detail parameter is null!");
			throw new InvalidInputException("updateLicenseDetailsTableOnPlanUpgrade : license detail parameter is null!");
		}
		if (company == null) {
			LOG.error("updateLicenseDetailsTableOnPlanUpgrade : company parameter is null!");
			throw new InvalidInputException("updateLicenseDetailsTableOnPlanUpgrade : company parameter is null!");
		}
		if (newAccountsMaster == null) {
			LOG.error("updateLicenseDetailsTableOnPlanUpgrade : newAccountsMaster parameter is null!");
			throw new InvalidInputException("updateLicenseDetailsTableOnPlanUpgrade : newAccountsMaster parameter is null!");
		}
		if (subscriptionId == null && licenseDetail.getAccountsMaster().getAccountsMasterId() == CommonConstants.ACCOUNTS_MASTER_FREE) {
			LOG.error("updateLicenseDetailsTableOnPlanUpgrade : subscriptionId parameter is null!");
			throw new InvalidInputException("updateLicenseDetailsTableOnPlanUpgrade : subscriptionId parameter is null!");
		}

		// Updating license detail table.
		LOG.info("Updating the License Detail table to show changes");
		if (checkIfItIsAFreeAccount(user)) {
			licenseDetail.setSubscriptionId(subscriptionId);
		}
		licenseDetail.setAccountsMaster(newAccountsMaster);
		licenseDetail.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		licenseDetail.setModifiedBy(String.valueOf(user.getUserId()));
		licenseDetailDao.update(licenseDetail);
		LOG.debug("License Detail Table updated.Updating the company object to reflect the change.");
		company.setLicenseDetails(Arrays.asList(licenseDetail));
		LOG.debug("Company entity updated");
		LOG.info("updateLicenseDetailsTableOnPlanUpgrade execution complete!");

	}

	/**
	 * Checks if particular user belongs to free account
	 * 
	 * @param user
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	private boolean checkIfItIsAFreeAccount(User user) throws InvalidInputException, NoRecordsFetchedException {
		if (user == null) {
			LOG.error("checkIfItIsAFreeAccount : user parameter null!");
			throw new InvalidInputException("checkIfItIsAFreeAccount : user parameter null!");
		}
		LOG.debug("checkIfItIsAFreeAccount called!");
		boolean status = false;
		// We get the accountsmaster for the current user
		List<LicenseDetail> licenseDetails = user.getCompany().getLicenseDetails();
		if (licenseDetails == null || licenseDetails.isEmpty()) {
			LOG.error("checkIfItIsAFreeAccount : No License details record found for user id : " + user.getUserId());
			throw new NoRecordsFetchedException("checkIfItIsAFreeAccount : No License details record found for user id : " + user.getUserId());
		}
		AccountsMaster currentAccountsMaster = licenseDetails.get(CommonConstants.INITIAL_INDEX).getAccountsMaster();

		if (currentAccountsMaster.getAccountsMasterId() == CommonConstants.ACCOUNTS_MASTER_FREE) {
			LOG.info(" The user is currently under free plan");
			status = true;
		}
		LOG.debug("End of checkIfItIsAFreeAccount");
		return status;
	}

	/**
	 * Upgrades the plan for a particular subscription.
	 * 
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
	@Transactional
	@Override
	public void upgradePlanForSubscription(User user, int newAccountsMasterId, String nonce) throws InvalidInputException, NoRecordsFetchedException,
			SubscriptionPastDueException, PaymentException, SubscriptionUpgradeUnsuccessfulException, SolrException, UndeliveredEmailException,
			SubscriptionUnsuccessfulException, CreditCardException {

		if (user == null) {
			LOG.error("upgradePlanForSubscription : User parameter given is null.");
			throw new InvalidInputException("upgradePlanForSubscription : User parameter given is null.");
		}
		if (newAccountsMasterId < 0) {
			LOG.error("upgradePlanForSubscription : newAccountsMasterId parameter given is invalid");
			throw new InvalidInputException("upgradePlanForSubscription : newAccountsMasterId parameter given is invalid");
		}
		if (checkIfItIsAFreeAccount(user) && nonce == null) {
			LOG.error("upgradePlanForSubscription : nonce parameter given is invalid");
			throw new InvalidInputException("upgradePlanForSubscription : nonce parameter given is invalid");
		}
		LOG.info("upgradePlanForSubscription called!");
		Company company = user.getCompany();
		// We need the subscription id in case of free account
		String subscriptionId = null;
		double originalPrice = 0.0; // holds the original price of the subscription

		// Fetching the new accounts master record
		LOG.debug("Fetching the new accounts master record from the database.");
		AccountsMaster newAccountsMaster = accountsMasterDao.findById(AccountsMaster.class, newAccountsMasterId);
		if (newAccountsMaster == null) {
			LOG.error("upgradePlanForSubscription : null returned by dao for accountsMaster");
			throw new InvalidInputException("upgradePlanForSubscription : null returned by dao for accountsMaster");
		}
		LOG.debug("Accounts master record fetched.");

		LicenseDetail licenseDetail = company.getLicenseDetails().get(CommonConstants.INITIAL_INDEX);
		if (licenseDetail.getIsSubscriptionDue() == CommonConstants.YES) {
			LOG.error("Subscription is past due! So cannot upgrade it to new plan");
			throw new SubscriptionPastDueException("Subscription is past due! So cannot upgrade it to new plan");
		}

		String braintreePlanId = getBraintreePlanId(newAccountsMasterId);

		if (checkIfItIsAFreeAccount(user)) {
			// In case of free account we check if user is in braintree vault, if not we add him and
			// make a fresh subscription.
			Customer customer = containsCustomer(String.valueOf(company.getCompanyId()));

			if (customer != null) {
				LOG.debug("Customer found in vault. Making subscription.");
				// If he does just subscribe the customer
				subscriptionId = subscribeCustomer(String.valueOf(company.getCompanyId()), braintreePlanId);
			}
			else {
				LOG.debug("Customer does not exist in the vault.Adding customer to vault.");
				// If he doesnt add him to the vault and subscribe him
				addCustomerWithPayment(company, nonce);
				LOG.info("Customer Added. Making subscription.");
				subscriptionId = subscribeCustomer(String.valueOf(company.getCompanyId()), braintreePlanId);
			}
		}
		else {
			// Making API call to Braintree to update subscription.
			LOG.info("Subscription isnt due. So upgrading the subscription");
			// get original price of the subscription
			originalPrice = getSubscriptionPriceFromBraintree(company);
			double newPrice = calculateAmount(company, newAccountsMaster);
			upgradeSubscription(licenseDetail.getSubscriptionId(), (float) newPrice, braintreePlanId);
			LOG.info("Subscription upgraded at braintree");
		}

		try {
			// Update the branches and the regions and add settings to mongo
			LOG.info("API call successful, updating the branch and region databases");
			// organizationManagementService.upgradeAccount(company, newAccountsMasterId);
			updateLicenseDetailsTableOnPlanUpgrade(user, licenseDetail, company, newAccountsMaster, subscriptionId);

		}
		catch (DatabaseException e) {
			LOG.error("Database exception caught while performing the update.Reverting the upgrade!");
			// Getting the braintree id for the old plan
			String braintreeOldPlanId = getBraintreePlanId(licenseDetail.getAccountsMaster().getAccountsMasterId());
			// Reverting the account to the old plan
			LOG.info("Reverting the subscription to the old plan");
			// assuming original price cannot be null
			upgradeSubscription(licenseDetail.getSubscriptionId(), (float) originalPrice, braintreeOldPlanId);
			throw e;
		}

		LOG.info("Sending mail to the customer about the upgrade");
		emailServices.sendAccountUpgradeMail(user.getEmailId(), user.getFirstName() + " " + user.getLastName(), user.getLoginName());
		LOG.info("Mail successfully sent");

		LOG.info("Subscription with id : " + licenseDetail.getSubscriptionId() + " successfully upgraded!");

	}

	/**
	 * Fetches the current card details for a particular subscription
	 * 
	 * @param subscriptionId
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws PaymentException
	 */
	@Override
	public Map<String, String> getCurrentPaymentDetails(String subscriptionId) throws InvalidInputException, NoRecordsFetchedException,
			PaymentException {

		LOG.info("getCurrentPaymentDetails called to fetch the current payment method");

		if (subscriptionId == null || subscriptionId.isEmpty()) {
			LOG.error("getCurrentPaymentDetails : subscriptionId parameter is null or empty");
			throw new InvalidInputException("getCurrentPaymentDetails : subscriptionId parameter is null or empty");
		}

		Map<String, String> paymentDetailsMap = new HashMap<>();

		try {

			// Firstly we get the subscription whose payment method we need
			Subscription subscription = null;
			try {
				LOG.debug("Fetching the subscription object from the vault for subscription id : " + subscriptionId);
				subscription = gateway.subscription().find(subscriptionId);
			}
			catch (NotFoundException e) {
				LOG.error("NotFoundException caught while fetching subscription with id : " + subscriptionId);
				throw new NoRecordsFetchedException("NotFoundException caught while fetching subscription with id : " + subscriptionId);
			}

			// Once we have the subscription we use the payment method token to get the payment
			// method from the vault
			CreditCard currentPaymentMethod = null;
			try {
				LOG.debug("Fetching the payment method object from the vault for payment token id : " + subscription.getPaymentMethodToken());
				currentPaymentMethod = (CreditCard) gateway.paymentMethod().find(subscription.getPaymentMethodToken());
			}
			catch (NotFoundException e) {
				LOG.error("NotFoundException caught while fetching payment method with id : " + subscription.getPaymentMethodToken());
				throw new NoRecordsFetchedException("NotFoundException caught while fetching payment method with id : "
						+ subscription.getPaymentMethodToken());
			}

			// Now we build the hashmap to be returned
			LOG.debug("Payment details fetched. Building the Hashmap to return");
			paymentDetailsMap.put(CommonConstants.CARD_NUMBER, currentPaymentMethod.getMaskedNumber());
			paymentDetailsMap.put(CommonConstants.CARD_TYPE, currentPaymentMethod.getCardType());
			paymentDetailsMap.put(CommonConstants.CARD_HOLDER_NAME, currentPaymentMethod.getCardholderName());
			paymentDetailsMap.put(CommonConstants.ISSUING_BANK, currentPaymentMethod.getIssuingBank());
			paymentDetailsMap.put(CommonConstants.IMAGE_URL, currentPaymentMethod.getImageUrl());

			LOG.debug("Payment details map built");

		}
		catch (UnexpectedException | DownForMaintenanceException e) {
			LOG.error("UnexpectedException caught : message : " + e.getMessage());
			throw new PaymentException("UnexpectedException caught : message : " + e.getMessage(), DisplayMessageConstants.PAYMENT_GATEWAY_EXCEPTION);
		}
		catch (BraintreeException e) {
			LOG.error("BraintreeException caught : message : " + e.getMessage());
			throw new PaymentException("BraintreeException caught : message : " + e.getMessage(), DisplayMessageConstants.PAYMENT_GATEWAY_EXCEPTION);
		}

		LOG.info("Returning the payment method details");
		return paymentDetailsMap;
	}

	/**
	 * Changes the card for a particular customer and subscription
	 * 
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
	@Override
	public void changePaymentMethod(String subscriptionId, String paymentNonce, String customerId) throws InvalidInputException,
			NoRecordsFetchedException, PaymentException, CreditCardException, CardUpdateUnsuccessfulException {

		LOG.info(" changePaymentMethod called to change payment method");

		if (subscriptionId == null || subscriptionId.isEmpty()) {
			LOG.error("changePaymentMethod : subscriptionId parameter is null or empty");
			throw new InvalidInputException("getCurrentPaymentDetails : subscriptionId parameter is null or empty");
		}
		if (paymentNonce == null || paymentNonce.isEmpty()) {
			LOG.error("changePaymentMethod : paymentNonce parameter is null or empty");
			throw new InvalidInputException("getCurrentPaymentDetails : paymentNonce parameter is null or empty");
		}

		try {
			// Firstly we get the subscription whose payment method we need
			Subscription subscription = null;
			try {
				LOG.debug("Fetching the subscription object from the vault for subscription id : " + subscriptionId);
				subscription = gateway.subscription().find(subscriptionId);
			}
			catch (NotFoundException e) {
				LOG.error("NotFoundException caught while fetching subscription with id : " + subscriptionId);
				throw new NoRecordsFetchedException("NotFoundException caught while fetching subscription with id : " + subscriptionId);
			}

			// Next we update the customer with the new payment nonce
			LOG.info("Updating the payment method for customer with id : " + customerId + " to : " + paymentNonce);
			CustomerRequest customerRequest = new CustomerRequest();
			customerRequest.creditCard().paymentMethodNonce(paymentNonce).options().updateExistingToken(subscription.getPaymentMethodToken())
					.verifyCard(true).done();
			Result<Customer> result = gateway.customer().update(customerId, customerRequest);
			if (result.isSuccess()) {
				LOG.info("Result : " + result.isSuccess());
			}
			else {
				LOG.info("Result : " + result.isSuccess() + " message : " + result.getMessage());
				ValidationErrors creditCardErrors = result.getErrors().forObject("customer").forObject("creditCard");
				if (creditCardErrors.size() > 0) {
					String errorMessage = "";
					for (ValidationError error : creditCardErrors.getAllValidationErrors()) {
						errorMessage += " Error Code : " + error.getCode();
						errorMessage += " Error message : " + error.getMessage() + "\n";
					}
					throw new CreditCardException("Credit Card Validation failed, reason : \n " + errorMessage,
							DisplayMessageConstants.CREDIT_CARD_INVALID);
				}
				List<ValidationError> allErrors = result.getErrors().getAllDeepValidationErrors();
				if (allErrors.size() > 0) {
					String errorMessage = "";
					for (ValidationError error : allErrors) {
						errorMessage += " Error Code : " + error.getCode();
						errorMessage += " Error message : " + error.getMessage() + "\n";
					}

					throw new CardUpdateUnsuccessfulException("Subscription creation failed, reason : \n " + errorMessage);
				}
				if (!result.getTransaction().getProcessorResponseCode().isEmpty()) {
					throw new CardUpdateUnsuccessfulException("Subscription creation failed, reason : \n " + result.getMessage(),
							DisplayMessageConstants.BANK_REJECTED);

				}
				throw new CardUpdateUnsuccessfulException("Subscription creation failed, reason : \n " + result.getMessage());

			}

		}
		catch (UnexpectedException | DownForMaintenanceException e) {
			LOG.error("UnexpectedException caught : message : " + e.getMessage());
			throw new PaymentException("UnexpectedException caught : message : " + e.getMessage(), DisplayMessageConstants.PAYMENT_GATEWAY_EXCEPTION);
		}
		catch (BraintreeException e) {
			LOG.error("BraintreeException caught : message : " + e.getMessage());
			throw new PaymentException("BraintreeException caught : message : " + e.getMessage(), DisplayMessageConstants.PAYMENT_GATEWAY_EXCEPTION);
		}

		LOG.info("Card details changed successfully!");
	}

	/**
	 * Returns the balance amount while upgrading from one plan to another
	 * 
	 * @param fromAccountsMasterId
	 * @param toAccountsMasterId
	 * @return
	 * @throws InvalidInputException
	 */
	@Transactional
	@Override
	public float getBalacnceAmountForPlanUpgrade(Company company, int fromAccountsMasterId, int toAccountsMasterId) throws InvalidInputException {

		if (company == null) {
			LOG.error("getBalacnceAmountForPlanUpgrade : Invalid company parameter ");
			throw new InvalidInputException("getBalacnceAmountForPlanUpgrade : Invalid fromAccountsMasterId parameter ");
		}
		if (fromAccountsMasterId <= 0) {
			LOG.error("getBalacnceAmountForPlanUpgrade : Invalid fromAccountsMasterId parameter ");
			throw new InvalidInputException("getBalacnceAmountForPlanUpgrade : Invalid fromAccountsMasterId parameter ");
		}

		if (toAccountsMasterId <= 1) {
			LOG.error("getBalacnceAmountForPlanUpgrade : Invalid toAccountsMasterId parameter ");
			throw new InvalidInputException("getBalacnceAmountForPlanUpgrade : Invalid toAccountsMasterId parameter ");
		}

		// find the number of users for the company
		long numOfUsers = findNumberOfUsersForCompany(company);
		// We fetch the accounts master records for each.

		AccountsMaster fromAccountsMaster = accountsMasterDao.findById(AccountsMaster.class, fromAccountsMasterId);
		AccountsMaster toAccountsMaster = accountsMasterDao.findById(AccountsMaster.class, toAccountsMasterId);

		return (toAccountsMaster.getAmount() * numOfUsers) - (fromAccountsMaster.getAmount() * numOfUsers);
	}

	/**
	 * Checks for an existing transaction in retried transaction table
	 * 
	 * @param subscriptionId
	 * @return
	 * @throws InvalidInputException
	 */
	@Transactional
	@Override
	public RetriedTransaction checkForExistingTransaction(LicenseDetail licenseDetail) throws InvalidInputException {

		if (licenseDetail == null) {
			LOG.error("Null parameter given to checkForExistingTransactions!");
			throw new InvalidInputException("Null parameter given to checkForExistingTransactions!");
		}

		LOG.debug("Checking for existing transactions for the License Id : " + licenseDetail.getLicenseId());
		RetriedTransaction existingTransaction = null;

		// find records in RETRIED_TRANSACTIONS table with the same license id or in hibernate sense
		// the object.
		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.LICENSE_DETAIL_COLUMN, licenseDetail);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);

		LOG.debug("Querying RetriedTransaction table for transactions for license id : " + licenseDetail.getLicenseId());

		List<RetriedTransaction> retriedTransactions = retriedTransactionDao.findByKeyValue(RetriedTransaction.class, queries);

		if (retriedTransactions == null || retriedTransactions.isEmpty()) {

			LOG.debug("No transactions found for the id : " + licenseDetail.getLicenseId());
			return null;
		}

		existingTransaction = retriedTransactions.get(CommonConstants.INITIAL_INDEX);

		LOG.debug("Transaction found. Returning it.");
		return existingTransaction;
	}

	/**
	 * On getting a SUBSCRIPTION CHARGED UNSUCCESSFULLY webhook increments the number of retries and
	 * blocks account if necessary
	 * 
	 * @throws InvalidInputException
	 * @throws PaymentRetryUnsuccessfulException
	 * @throws NoRecordsFetchedException
	 */
	@Transactional
	@Override
	public void incrementRetriesAndSendMail(Subscription subscription) throws InvalidInputException, NoRecordsFetchedException {
		if (subscription == null) {
			LOG.error("subscription parameter is null or empty");
			throw new InvalidInputException("subscription parameter is null or empty");
		}

		boolean retriesExceeded = false;
		List<LicenseDetail> licenseDetails = licenseDetailDao.findByColumn(LicenseDetail.class, CommonConstants.SUBSCRIPTION_ID_COLUMN,
				subscription.getId());

		if (licenseDetails == null || licenseDetails.isEmpty()) {
			LOG.error("No license details record found for the subscription id : " + subscription.getId());
			throw new NoRecordsFetchedException("No license details record found for the subscription id : " + subscription.getId());
		}
		LicenseDetail licenseDetail = licenseDetails.get(CommonConstants.INITIAL_INDEX);

		// Sending retry email to client.
		LOG.debug("Fetching the corporate admin");
		User user = coreCommonServices.getCorporateAdmin(licenseDetail.getCompany());
		LOG.debug("Sending mail for retrying subscription charge.");

		if (licenseDetail.getPaymentRetries() >= CommonConstants.PAYMENT_RETRIES - 1) {
			retriesExceeded = true;
			licenseDetail.setPaymentRetries(licenseDetail.getPaymentRetries() + CommonConstants.PAYMENT_INCREMENT);
			licenseDetail.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			licenseDetailDao.update(licenseDetail);
			
			Company company = user.getCompany();
			company.setStatus(CommonConstants.STATUS_PAYMENT_FAILED);
			companyDao.update(company);
		}
		else {
			LOG.info("Updating License Detail entity to reflect changes of new retry.");
			licenseDetail.setPaymentRetries(licenseDetail.getPaymentRetries() + CommonConstants.PAYMENT_INCREMENT);
			licenseDetail.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			licenseDetailDao.update(licenseDetail);
		}

		try {
			if (retriesExceeded) {
				emailServices.sendAccountBlockingMail(user.getEmailId(), user.getFirstName() + " " + user.getLastName(), user.getLoginName());
			}
			else {
				emailServices.sendRetryChargeEmail(user.getEmailId(), user.getFirstName() + " " + user.getLastName(), user.getLoginName());
			}
		}
		catch (InvalidInputException e1) {
			LOG.error("CustomItemProcessor : Exception caught when sending retry charge mail. Message : " + e1.getMessage());
			coreCommonServices.sendEmailSendingFailureMail(user.getEmailId(), user.getFirstName() + " " + user.getLastName(), e1);
		}
		catch (UndeliveredEmailException e) {
			LOG.error("CustomItemProcessor : Exception caught when sending retry charge mail. Message : " + e.getMessage());
			coreCommonServices.sendEmailSendingFailureMail(user.getEmailId(), user.getFirstName() + " " + user.getLastName(), e);
		}
	}

	/**
	 * On recieveing SUBSCRIPTION CHARGED SUCCESSFULLY webhook it checks if license is past due and
	 * updates it
	 * 
	 * @param licenseDetail
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws UndeliveredEmailException
	 */
	@Transactional
	@Override
	public void checkIfCompanyIsDisabledOrSubscriptionIsPastDueAndEnableIt(Subscription subscription) throws InvalidInputException,
			NoRecordsFetchedException, UndeliveredEmailException {
		if (subscription == null) {
			LOG.error("subscription parameter is null or empty!");
			throw new InvalidInputException("subscription parameter is null or empty!");
		}
		LOG.info("checkIfCompanyIsDisabledOrSubscriptionIsPastDueAndEnableIt called to enable subscription id : " + subscription.getId());

		List<LicenseDetail> licenseDetails = licenseDetailDao.findByColumn(LicenseDetail.class, CommonConstants.SUBSCRIPTION_ID_COLUMN,
				subscription.getId());
		if (licenseDetails == null || licenseDetails.isEmpty()) {
			LOG.error("License details objecy not found for subscription id : " + subscription.getId());
			throw new NoRecordsFetchedException("License details objecy not found for subscription id : " + subscription.getId());
		}

		LicenseDetail licenseDetail = licenseDetails.get(CommonConstants.INITIAL_INDEX);
		if (licenseDetail.getIsSubscriptionDue() == CommonConstants.YES) {

			Company company = licenseDetail.getCompany();
			if (company == null) {
				LOG.error("company property not found in license detail parameter");
				throw new NoRecordsFetchedException("company property not found in license detail parameter");
			}

			if (licenseDetail.getPaymentRetries() == CommonConstants.PAYMENT_RETRIES) {
				// Now we update the company table to reflect change
				company.setStatus(CommonConstants.STATUS_ACTIVE);
				companyDao.update(company);
				LOG.debug("Company table updated!");
			}
			
			// We set the license details record to reflect changes
			licenseDetail.setIsSubscriptionDue(CommonConstants.NO);
			licenseDetail.setPaymentRetries(CommonConstants.INITIAL_PAYMENT_RETRIES);
			licenseDetailDao.update(licenseDetail);
			LOG.debug("License detail table updated!");

			User user = coreCommonServices.getCorporateAdmin(company);
			if (user == null) {
				LOG.error("Corporate admin not found for company : " + company.getCompanyId());
				throw new NoRecordsFetchedException("Corporate admin not found for company : " + company.getCompanyId());
			}

			emailServices.sendAccountReactivationMail(user.getEmailId(), user.getFirstName() + " " + user.getLastName(), user.getLoginName());
			LOG.info("Company activated!");
		}

		LOG.info("checkIfCompanyIsDisabledOrSubscriptionIsPastDueAndEnableIt execution complete!");
	}

	@Transactional
	@Override
	public void intimateUser(Subscription subscription, int notificationType) throws InvalidInputException, NoRecordsFetchedException,
			UndeliveredEmailException {
		if (subscription == null) {
			throw new InvalidInputException("Subscription passed is null");
		}
		LOG.info("Initmating user for subscription type : " + subscription.toString() + " with notification type: " + notificationType);
		// get the user from subscription
		LicenseDetail licenseDetail = null;
		List<LicenseDetail> licenseDetails = licenseDetailDao.findByColumn(LicenseDetail.class, CommonConstants.SUBSCRIPTION_ID_COLUMN,
				subscription.getId());

		if (licenseDetails.isEmpty() || licenseDetails == null) {
			LOG.error("Subscription details not found in the LicenseDetail table.");
			throw new NoRecordsFetchedException("Subscription details not found in the LicenseDetail table.");
		}
		else {
			licenseDetail = licenseDetails.get(CommonConstants.INITIAL_INDEX);
		}

		// Getting the list of corporate admins from user profiles table
		Map<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_COLUMN, licenseDetail.getCompany());
		queries.put(CommonConstants.IS_OWNER_COLUMN, CommonConstants.IS_OWNER);
		List<User> users = userDao.findByKeyValue(User.class, queries);
		User user = null;
		if (users.isEmpty() || users == null) {
			LOG.error("Corporate Admin details not found in the User table.");
			throw new NoRecordsFetchedException("Corporate Admin details not found in the User table.");
		}
		else {
			user = users.get(CommonConstants.INITIAL_INDEX);
		}
		// send mail to the user
		if (notificationType == CommonConstants.SUBSCRIPTION_WENT_PAST_DUE) {
			LOG.debug("Sending subscription past due date mail");
			emailServices.sendSubscriptionChargeUnsuccessfulEmail(user.getEmailId(), user.getFirstName() + " " + user.getLastName(),
					String.valueOf(retryDays));
		}
		else if (notificationType == CommonConstants.SUBSCRIPTION_CHARGED_SUCCESSFULLY) {
			LOG.debug("Sending charge successful mail");
			// TODO: implement
		}
		else if (notificationType == CommonConstants.SUBSCRIPTION_CHARGED_UNSUCCESSFULLY) {
			LOG.debug("Sending charge unsuccessful mail");
			emailServices.sendRetryChargeEmail(user.getEmailId(), user.getFirstName() + " " + user.getLastName(), user.getLoginName());
		}
	}

	@Transactional
	@Override
	public Map<String, Object> updateSubscriptionPriceBasedOnUsersCount(Company company) throws InvalidInputException, NoRecordsFetchedException, PaymentException,
			SubscriptionUpgradeUnsuccessfulException {
		if (company == null) {
			LOG.warn("Company is null while updating the subscription price");
			throw new InvalidInputException("Subscription id cannot be null");
		}
		Map<String, Object> resultMap = new HashMap<>();
		boolean priceChanged = false;
		LOG.debug("Updating the braintree subscription for comapny: " + company.toString());
		// get the number of users for the company
		long numOfUsers = findNumberOfUsersForCompany(company);
		// get the current accounts master account linked with the company
		double amount = company.getLicenseDetails().get(CommonConstants.INITIAL_INDEX).getAccountsMaster().getAmount() * numOfUsers;
		String sAmount = AMOUNT_FORMAT.format(amount);
		// get the current price
		double previousAmount = getSubscriptionPriceFromBraintree(company);
		LOG.debug("Previous amount: "+previousAmount+"\t Revised amount: "+sAmount+" for company "+company.getCompanyId());
		if (previousAmount != Double.parseDouble(sAmount)) {
			priceChanged = true;
			LOG.debug("Upgrading the account for " + company.getCompany() + " with by " + numOfUsers + " users for an amount of " + amount);
			// Calling braintree for updating the amount
			LOG.debug("Calling braintree for updating the amount");
			// proration is not required
			SubscriptionRequest updateRequest = new SubscriptionRequest().price(new BigDecimal(sAmount));

			Result<Subscription> result = null;
			try {
				LOG.debug("Making api call to revising the price of the subscription");
				result = gateway.subscription().update(company.getLicenseDetails().get(CommonConstants.INITIAL_INDEX).getSubscriptionId(),
						updateRequest);
			}
			catch (NotFoundException e) {
				LOG.error("updateSubscriptionPrice : NotFoundException has occured");
				throw new NoRecordsFetchedException("updateSubscriptionPrice : NotFoundException has occured");
			}
			catch (UnexpectedException | DownForMaintenanceException e) {
				LOG.error("updateSubscriptionPrice : UexpectedException has occured");
				throw new PaymentException("updateSubscriptionPrice : UexpectedException has occured",
						DisplayMessageConstants.PAYMENT_GATEWAY_EXCEPTION);
			}
			catch (BraintreeException e) {
				LOG.error("updateSubscriptionPrice : BraintreeException has occured. Message : " + e.getMessage(), e);
				throw new PaymentException("updateSubscriptionPrice : BraintreeException has occured",
						DisplayMessageConstants.PAYMENT_GATEWAY_EXCEPTION);
			}

			if (result.isSuccess()) {
				LOG.debug("Update Subscription Price successful! ");
			}
			else {
				LOG.debug("UpdateSubscriptionPrice unsuccessful, message : " + result.getMessage());
				String errorMessage = "";
				if (result.getErrors().getAllValidationErrors().size() > 0) {
					for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
						errorMessage += "Error code : " + error.getCode();
						errorMessage += " Message : " + error.getMessage() + "\n";
					}
				}
				if (!result.getTransaction().getProcessorResponseCode().isEmpty()) {
					throw new SubscriptionUpgradeUnsuccessfulException("UpdateSubscriptionPrice unsuccessful, message : \n" + errorMessage,
							DisplayMessageConstants.BANK_REJECTED);
				}
				throw new SubscriptionUpgradeUnsuccessfulException("UpdateSubscriptionPrice unsuccessful, message : \n" + errorMessage,
						DisplayMessageConstants.SUBSCRIPTION_UPGRADE_UNSUCCESSFUL);
			}
		}
		resultMap.put(CommonConstants.SUBSCRIPTION_PRICE_CHANGED, priceChanged);
		resultMap.put(CommonConstants.SUBSCRIPTION_OLD_PRICE, previousAmount);
		resultMap.put(CommonConstants.SUBSCRIPTION_REVISED_PRICE, sAmount);
		resultMap.put(CommonConstants.SUBSCRIPTION_REVISED_NUMOFUSERS, numOfUsers);
		return resultMap;
	}

	@Transactional
	private double calculateAmount(Company company, AccountsMaster accountsMaster) throws InvalidInputException {
		if (company == null) {
			LOG.error("Company is null in calculateAmount");
			throw new InvalidInputException("Company is null in calculateAmount");
		}
		if (accountsMaster == null) {
			LOG.error("AccountsMaster is null in calculateAmount");
			throw new InvalidInputException("AccountsMaster is null in calculateAmount");
		}
		LOG.debug("Calculating the amount for company " + company.getCompany() + " and accounts master: " + accountsMaster.getAccountName());
		// get the amount for the accounts master based on the number of users in the company
		long numOfUsers = findNumberOfUsersForCompany(company);

		LOG.debug("Found " + numOfUsers + " users for company " + company.getCompany());
		// return the amount
		return accountsMaster.getAmount() * numOfUsers;
	}

	@Transactional
	private long findNumberOfUsersForCompany(Company company) {
		LOG.debug("Finding number of active users for company " + company.toString());
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put(CommonConstants.COMPANY_COLUMN, company);
		queryMap.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		long numOfUsers = userDao.findNumberOfRowsByKeyValue(User.class, queryMap);
		return numOfUsers;
	}

	@Transactional
	private double getSubscriptionPriceFromBraintree(Company company) throws InvalidInputException {
		if (company == null) {
			LOG.error("Company is null in getSubscriptionPriceFromBraintree.");
			throw new InvalidInputException("Company is null in getSubscriptionPriceFromBraintree.");
		}
		LOG.debug("Getting present subscription price for company: " + company.getCompany());
		double price = 0.0;
		String subscriptionId = company.getLicenseDetails().get(CommonConstants.INITIAL_INDEX).getSubscriptionId();
		Subscription subscription = gateway.subscription().find(subscriptionId);
		if (subscription != null) {
			price = subscription.getPrice().doubleValue();
		}
		LOG.debug("Returning price " + price + " for company " + company.getCompany());
		return price;
	}

}
