package com.realtech.socialsurvey.core.services.payment.impl;

//JIRA: SS-15: By RM03

import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.braintreegateway.exceptions.NotFoundException;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.AccountsMaster;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.generator.impl.UrlGeneratorImpl;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.utils.PropertyFileReader;

/**
 * This class implements the Payment interface and makes calls to the Braintree APIs
 * to make payments and subscriptions. 
 */


@Component
public class BrainTreePaymentImpl implements Payment {
	
	@Autowired
	GenericDao<LicenseDetail, Integer> licenseDetailDao;
	
	@Autowired
	GenericDao<AccountsMaster, Integer> accountsMasterDao;
	
	@Autowired
	GenericDao<Company, Integer> companyDao;
	
	@Autowired
	PropertyFileReader propertyFileReader;
	
	@Value("${MERCHANT_ID}")
	String merchantId;
	
	@Value("${PUBLIC_KEY}")
	String publicKey;
	
	@Value("${PRIVATE_KEY}")
	String privateKey;
	
	private static final Logger LOG = LoggerFactory.getLogger(UrlGeneratorImpl.class);

	private BraintreeGateway gateway = null;	
	
	/**
	 * Initialises the Braintree gateway.
	 */
	public synchronized BraintreeGateway initialise(){	
		
		LOG.info("BraintreePaymentImpl : initialise() : Executing method " );		
		if(gateway == null){
			LOG.info("Initialising gateway with keys: " + merchantId + " : " + publicKey + " : " + privateKey );
			gateway = new BraintreeGateway(
			        Environment.SANDBOX,
			        merchantId,
			        publicKey,
			        privateKey
			    );				
		}
		return gateway;			
	}
	
	/**
	 * Makes the dao calls to update LicenseDetails table.
	 * @param accountsMasterId
	 * @param companyId
	 * @param userId
	 * @throws InvalidInputException 
	 */
	private void updateLicenseTable(int accountsMasterId,Company company,int userId) throws InvalidInputException{
								
		AccountsMaster accountsMaster = accountsMasterDao.findById(AccountsMaster.class, accountsMasterId);
		if(accountsMaster == null){
			LOG.error("updateLicenseTable : null returned by dao for accountsMaster");
			throw new InvalidInputException("updateLicenseTable : null returned by dao for accountsMaster");
		}
		
		LOG.debug("BrainTreePaymentImpl : updateLicenseTable() : Executing method.");
		LOG.debug("Parameters provided : accountsMasterId : " + accountsMasterId + ", company : " + company.toString() + ", userId : " + userId );
		
		LOG.debug("Updating LicenseDetail Table");
		LicenseDetail licenseDetail = new LicenseDetail();
		licenseDetail.setAccountsMaster(accountsMaster);
		licenseDetail.setCompany(company);
		licenseDetail.setCreatedBy(String.valueOf(userId));
		licenseDetail.setModifiedBy(String.valueOf(userId));
		licenseDetail.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		licenseDetail.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		licenseDetail.setPaymentMode("A");
		licenseDetail.setNextRetryTime(new Timestamp(86400000));
		licenseDetail.setStatus(1);
		licenseDetail.setLicenseStartDate(new Timestamp(System.currentTimeMillis()));
		licenseDetail.setPaymentRetries(0);
		licenseDetailDao.save(licenseDetail);		
		LOG.debug("LicenseDetail table updated");
	}
		
	/**
	 * Adds a customer to the Braintree vault with customer details and payment method.
	 * @param user User object
	 * @param company Company object
	 * @param nonce payment nonce String given by Braintree
	 * @return Success or Failure of the operation.
	 * @throws InvalidInputException 
	 */
	private boolean addCustomerWithPayment(Company company, String nonce) throws InvalidInputException{
						
		if(company == null){
			LOG.error("addCustomerWithPayment : first parameter is null!");
			throw new InvalidInputException("addCustomerWithPayment : first parameter is null!");
		}
		
		if(nonce == null || nonce.isEmpty()){
			LOG.error("addCustomerWithPayment : second parameter is null or empty!");
			throw new InvalidInputException("addCustomerWithPayment : parameter is null or empty!");
		}
		
		LOG.debug("BrainTreePaymentImpl : addCustomerWithPayment() : Executing method.");
		LOG.debug("Parameters provided : Company : " + company.toString() + ", payment nonce : " + nonce);
		
		//Creating a new customer object
		CustomerRequest request = new CustomerRequest()
			.id(String.valueOf(company.getCompanyId()))
			.firstName(company.getCompany())
			.paymentMethodNonce(nonce);
		
		//Requesting Braintree to create a new customer object
		Result<Customer> result = gateway.customer().create(request);
		
		LOG.debug("addCustomerWithPayment : adding user " + Integer.toString(company.getCompanyId()) + " : Status : " + result.isSuccess() + " Message : " + result.getMessage());
		
		return result.isSuccess();		
	}
	
	/**
	 * Checks if the Braintree vault has a customer with a particular ID.
	 * @param customerId a String
	 * @return True or False based on existence.
	 * @throws InvalidInputException 
	 */
	private boolean containsCustomer(String customerId) throws InvalidInputException{
		
		boolean result = false;
		
		if(customerId == null || customerId.isEmpty()){
			LOG.error("containsCustomer : parameter is null or empty!");
			throw new InvalidInputException("containsCustomer : parameter is null or empty!");
		}
		
		LOG.debug("BrainTreePaymentImpl : containsCustomer() : Executing method.");
		LOG.debug("Parameters provided : customerId : " + customerId);
				
		try {
			//API call to Braintree to find customer with particular Id
			Customer customer = gateway.customer().find(customerId);
			LOG.info("containsCustomer : Found customer " + customerId + "Name : " + customer.getFirstName());
			result = true;
		}
		catch (NotFoundException e) {
			LOG.info("Customer " + customerId + " Not Found!");
			result =false;
		}
		
		return result;
		
	}
	
	/**
	 * Subscribes a particular customer with identified by the ID to a plan identified by plan ID.
	 * @param customerId a String
	 * @param planId a String
	 * @return Success or Failure of the operation.
	 * @throws InvalidInputException 
	 */
	private boolean subscribeCustomer(String customerId,String planId) throws InvalidInputException{
		
		if(customerId == null || customerId.isEmpty()){
			LOG.error("subscribeCustomer : first parameter is null or empty!");
			throw new InvalidInputException("subscribeCustomer : first parameter is null or empty!");			
		}
		
		if(planId == null || planId.isEmpty()){
			LOG.error("subscribeCustomer : second parameter is null or empty!");
			throw new InvalidInputException("subscribeCustomer : second parameter is null or empty!");	
		}
		
		LOG.debug("BrainTreePaymentImpl : subscribeCustomer() : Executing method.");
		LOG.debug("Parameters provided : customerId : " + customerId + ", planId : " + planId );
		
		//Fetch the customer
		Customer customer = gateway.customer().find(customerId);
		String paymentToken;
		try{
			paymentToken = customer.getPaymentMethods().get(0).getToken();
		}
		catch(Exception e){
			LOG.error("Customer with id " + customerId + " : payment token error!");
			throw new FatalException("Customer with id " + customerId + " : payment token error!");			
		}
		
		//Make a subscription request
		SubscriptionRequest request = new SubscriptionRequest()
			.planId(planId)
			.paymentMethodToken(paymentToken);
		
		Result<Subscription> result = gateway.subscription().create(request);
		
		LOG.info("subscribeCustomer : customerId : " + customerId + " for planId : " + planId + " Status : " + result.isSuccess()+ " Message : " + result.getMessage() );
		return result.isSuccess();		
	}
	
	/**
	 * This is the service called by the controller to make a subscription.
	 * @param user
	 * @param company
	 * @param planId
	 * @param nonce
	 */
	@Override
	@Transactional
	public boolean subscribe(User user,Company company, int planId, String nonce) throws NonFatalException {
		
		boolean result=false;
		
		if(company == null){
			LOG.error("subscribe : first parameter is null!");
			throw new InvalidInputException("subscribe : first parameter is null!");	
		}
		
		if( planId <= 0){
			LOG.error("subscribe : second parameter is invalid! parameter value : " + String.valueOf(planId));
			throw new InvalidInputException("subscribe : second parameter is invalid!parameter value : " + String.valueOf(planId));	
		}
		
		if(nonce == null || nonce.isEmpty()){
			LOG.error("subscribe : third parameter is null or empty!");
			throw new InvalidInputException("subscribe : third parameter is null or empty!");
		}
		
		LOG.info("BrainTreePaymentImpl : subscribe() : Executing method.");
		LOG.info("Parameters provided : User : " + user.toString() + ", Company : " + company.toString() + ", paymentNonce : " + nonce);
		
		LOG.info("sbscribe() : Fetching the planId string using property file");
		//Get the plan name used in Braintree
		String planIdString = propertyFileReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE, String.valueOf(planId));
		
		LOG.info("subscribe() : Making the subscription "); 
		//Check if the customer already exists in the vault.
		if(containsCustomer(String.valueOf(company.getCompanyId()))){
			
			//If he does just subscribe the customer
			result = subscribeCustomer(String.valueOf(company.getCompanyId()), planIdString);
		}
		else{
			//If he doesnt add him to the vault and subscribe him
			addCustomerWithPayment(company, nonce);
			result = subscribeCustomer(String.valueOf(company.getCompanyId()), planIdString);
		}	
		
		LOG.info("subscribe() : Updating the license table.");		
		if(result){
			updateLicenseTable(planId, company,user.getUserId());
		}
		LOG.info("subscribe(); : LicenseDetail table update done!");
		return result;
	}
	
	/**
	 * Returns a Braintree client token that is used by the frontend to setup the 
	 * drop-in UI.
	 * @return
	 */
	public String getClientToken(){
		
		LOG.info("BrainTreePaymentImpl : getClientToken() : Executing method.");
				
		//API call to generate client token
		String clientToken = gateway.clientToken().generate();
		
		LOG.info("Client token : " + clientToken );
		return clientToken;
	}
	
	/**
	 * Returns a Braintree client token with the user payment details encrypted 
	 * into it.
	 * @param customerId String containing customer ID
	 * @return
	 * @throws InvalidInputException 
	 */
	public String getClientTokenWithCustomerId(String customerId) throws InvalidInputException{
		
		if(customerId == null || customerId.isEmpty()){
			LOG.error("getClientTokenWithCustomerId : parameter is null or empty!");
			throw new InvalidInputException("getClientTokenWithCustomerId : parameter is null or empty!");			
		}
		LOG.info("BrainTreePaymentImpl : getClientTokenWithCustomerId() : Executing method.");
		LOG.info("Parameters provided : customerId : " + customerId);
	
		//API call to generate client token for a particular Id
		ClientTokenRequest request = new ClientTokenRequest().customerId(customerId);
		String clientToken = gateway.clientToken().generate(request);
		
		LOG.info("Client token for customer ID " + customerId +" : " + clientToken );
		return clientToken;
	}
		
}
