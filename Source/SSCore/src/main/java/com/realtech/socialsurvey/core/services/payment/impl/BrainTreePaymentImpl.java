package com.realtech.socialsurvey.core.services.payment.impl;

// JIRA: SS-15: By RM03

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
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
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.AccountsMaster;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.RetriedTransaction;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.generator.impl.UrlGeneratorImpl;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.utils.PropertyFileReader;

/**
 * This class implements the Payment interface and makes calls to the Braintree APIs to make
 * payments and subscriptions.
 */

@Component
public class BrainTreePaymentImpl implements Payment, InitializingBean {

	@Autowired
	private GenericDao<LicenseDetail, Integer> licenseDetailDao;

	@Autowired
	private GenericDao<AccountsMaster, Integer> accountsMasterDao;

	@Autowired
	private GenericDao<RetriedTransaction, Integer> retriedTransactionDao;

	@Autowired
	private PropertyFileReader propertyFileReader;

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

	private static final Logger LOG = LoggerFactory.getLogger(UrlGeneratorImpl.class);

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
	private void updateLicenseTable(int accountsMasterId, Company company, long userId, String subscriptionId) throws InvalidInputException {

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
	 */
	private boolean addCustomerWithPayment(Company company, String nonce) throws InvalidInputException {

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

		// Requesting Braintree to create a new customer object
		Result<Customer> result = gateway.customer().create(request);

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
	 */
	private boolean containsCustomer(String customerId) throws InvalidInputException {

		boolean result = false;

		if (customerId == null || customerId.isEmpty()) {
			LOG.error("containsCustomer : parameter is null or empty!");
			throw new InvalidInputException("containsCustomer : parameter is null or empty!");
		}

		LOG.debug("BrainTreePaymentImpl : containsCustomer() : Executing method.");
		LOG.debug("Parameters provided : customerId : " + customerId);

		try {
			// API call to Braintree to find customer with particular Id
			Customer customer = gateway.customer().find(customerId);
			LOG.debug("containsCustomer : Found customer " + customerId + "Name : " + customer.getFirstName());
			result = true;
		}
		catch (NotFoundException e) {
			LOG.debug("Customer " + customerId + " Not Found!");
			result = false;
		}

		return result;

	}

	/**
	 * Subscribes a particular customer with identified by the ID to a plan identified by plan ID.
	 * 
	 * @param customerId
	 *            a String
	 * @param planId
	 *            a String
	 * @return Success or Failure of the operation.
	 * @throws NonFatalException
	 */
	private String subscribeCustomer(String customerId, String planId) throws NonFatalException {

		String resultStatus = null;

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
		if (containsCustomer(customerId)) {
			Customer customer = gateway.customer().find(customerId);
			String paymentToken;
			try {
				paymentToken = customer.getPaymentMethods().get(0).getToken();
			}
			catch (Exception e) {
				LOG.error("Customer with id " + customerId + " : payment token error!");
				throw new NonFatalException("Customer with id " + customerId + " : payment token error!");
			}

			// Make a subscription request
			SubscriptionRequest request = new SubscriptionRequest().planId(planId).paymentMethodToken(paymentToken);

			Result<Subscription> result = gateway.subscription().create(request);

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
	 */
	@Override
	@Transactional
	public boolean subscribe(User user, Company company, int planId, String nonce) throws NonFatalException {

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
		if (containsCustomer(String.valueOf(company.getCompanyId()))) {
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

	private String makePayment(String paymentMethodToken, BigDecimal amount) throws InvalidInputException {

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
	public boolean retryPaymentAndUpdateLicenseTable(Subscription subscription) throws InvalidInputException {

		LOG.info("Retrying payment and updating the license table!");
		boolean status = false;
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, retryDays);

		if (subscription == null) {
			LOG.error("Parameter to retryPaymentAndUpdateLicenseTable() is null");
			throw new InvalidInputException("Parameter to retryPaymentAndUpdateLicenseTable() is null");
		}

		LOG.debug("Executing retryPaymentAndUpdateLicenseTable with parameter : " + subscription.toString());

		String paymentMethodToken = subscription.getPaymentMethodToken();
		BigDecimal amount = subscription.getPrice();
		String subscriptionId = subscription.getId();

		// Checking if a transaction has already been made in less than retry time.
		LicenseDetail licenseDetail = licenseDetailDao.findByColumn(LicenseDetail.class, "subscriptionId", subscriptionId).get(0);
		Timestamp timeOfNotification = new Timestamp(System.currentTimeMillis());

		if (timeOfNotification.before(licenseDetail.getNextRetryTime())) {
			LOG.info("Transaction has already been made in less than the configured retry interval!");
			// If transaction has already been made we stop and return true and wait for the next
			// retry time.
			return true;
		}

		LOG.debug("Making Payment with the tstoken : " + paymentMethodToken + " and amount : " + amount + " for subscription id : " + subscriptionId);

		String transactionId = makePayment(paymentMethodToken, amount);

		if (transactionId != null) {
			LOG.info("Payment successful with id : " + transactionId);
			status = true;
		}
		else {
			LOG.info("Payment failed!");
			status = false;
		}

		if (status) {
			LOG.info("Updating LicenseDetail table with subscriptionId : " + subscriptionId);
			licenseDetail.setPaymentRetries(licenseDetail.getPaymentRetries() + 1);
			licenseDetail.setNextRetryTime(new Timestamp(now.getTimeInMillis()));
			licenseDetail.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			licenseDetailDao.saveOrUpdate(licenseDetail);
			LOG.info("License table updated!");

			LOG.info("Adding details to the RetriedTransaction table.");
			RetriedTransaction retriedTransaction = new RetriedTransaction();
			retriedTransaction.setLicenseDetail(licenseDetail);
			retriedTransaction.setAmount(amount.floatValue());
			retriedTransaction.setPaymentToken(paymentMethodToken);
			retriedTransaction.setStatus(CommonConstants.STATUS_ACTIVE);
			retriedTransaction.setTransactionId(transactionId);
			retriedTransaction.setCreatedBy(CommonConstants.ADMIN_USER_NAME);
			retriedTransaction.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			retriedTransaction.setModifiedBy(CommonConstants.ADMIN_USER_NAME);
			retriedTransaction.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			retriedTransactionDao.save(retriedTransaction);

			LOG.info("RetriedTransaction table updated!");
		}

		return status;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

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
