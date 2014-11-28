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
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.AccountsMaster;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.User;
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

	BraintreeGateway gateway = null;	
	
	/**
	 * Initialises the Braintree gateway.
	 */
	public void initialise(){		
		
		LOG.info("Initialising gateway with keys: " + merchantId + " : " + publicKey + " : " + privateKey );
		gateway = new BraintreeGateway(
		        Environment.SANDBOX,
		        merchantId,
		        publicKey,
		        privateKey
		    );		
	}
	
	/**
	 * Makes the dao calls to update LicenseDetails table.
	 * @param accountsMasterId
	 * @param companyId
	 * @param userId
	 */
	public void updateLicenseTable(int accountsMasterId,int companyId,Integer userId){
		
		AccountsMaster accountsMaster = accountsMasterDao.findById(AccountsMaster.class, accountsMasterId);
		Company company = companyDao.findById(Company.class, companyId);
		LicenseDetail ld = new LicenseDetail();
		ld.setAccountsMaster(accountsMaster);
		ld.setCompany(company);
		ld.setCreatedBy(userId.toString());
		ld.setModifiedBy(userId.toString());
		ld.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		ld.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		ld.setPaymentMode("A");
		ld.setNextRetryTime(new Timestamp(1));
		ld.setStatus(1);
		ld.setLicenseStartDate(new Timestamp(System.currentTimeMillis()));
		ld.setPaymentRetries(0);
		licenseDetailDao.save(ld);		
	}
	
	/**
	 * Adds a customer to the Braintree vault with customer details but without payment details.
	 * @param user User object
	 * @param company Company object
	 * @return Success or Failure of the operation.
	 * @throws InvalidInputException 
	 */
	public boolean addCustomerWithoutPayment(User user) throws InvalidInputException{
		
		if(user == null){
			LOG.error("addCustomerWithoutPayment : parameter is null!");
			throw new InvalidInputException("addCustomerWithoutPayment : parameter is null!");
		}
		
		CustomerRequest request = new CustomerRequest()
			.company(user.getCompany().getCompany())
			.id(Integer.toString(user.getUserId()))
			.email(user.getEmailId())
			.firstName(user.getDisplayName());

		
		Result<Customer> result = gateway.customer().create(request);
		
		LOG.info("addCustomerWithPayment : adding user " + Integer.toString(user.getUserId()) + " : Status : " + result.isSuccess() + " Message : " + result.getMessage());
		
		return result.isSuccess();		
	}
	
	/**
	 * Adds a customer to the Braintree vault with customer details and payment method.
	 * @param user User object
	 * @param company Company object
	 * @param nonce payment nonce String given by Braintree
	 * @return Success or Failure of the operation.
	 * @throws InvalidInputException 
	 */
	public boolean addCustomerWithPayment(Company company, String nonce) throws InvalidInputException{
		
		if(company == null){
			LOG.error("addCustomerWithPayment : first parameter is null!");
			throw new InvalidInputException("addCustomerWithPayment : first parameter is null!");
		}
		
		if(nonce == null || nonce.equals("")){
			LOG.error("addCustomerWithPayment : second parameter is null or empty!");
			throw new InvalidInputException("addCustomerWithPayment : parameter is null or empty!");
		}
		CustomerRequest request = new CustomerRequest()
			.id(Integer.toString(company.getCompanyId()))
			.firstName(company.getCompany())
			.paymentMethodNonce(nonce);
		
		Result<Customer> result = gateway.customer().create(request);
		
		LOG.info("addCustomerWithPayment : adding user " + Integer.toString(company.getCompanyId()) + " : Status : " + result.isSuccess() + " Message : " + result.getMessage());
		
		return result.isSuccess();		
	}
	
	/**
	 * Checks if the Braintree vault has a customer with a particular ID.
	 * @param customerId a String
	 * @return True or False based on existence.
	 * @throws InvalidInputException 
	 */
	public boolean containsCustomer(String customerId) throws InvalidInputException{
		
		if(customerId == null || customerId.equals("")){
			LOG.error("containsCustomer : parameter is null or empty!");
			throw new InvalidInputException("containsCustomer : parameter is null or empty!");
		}
		
		try {
			Customer customer = gateway.customer().find(customerId);
			LOG.info("containsCustomer : Found customer " + customerId + "Name : " + customer.getFirstName());
			return true;
		}
		catch (NotFoundException e) {
			LOG.info("Customer " + customerId + " Not Found!");
			return false;
		}
		
	}
	
	/**
	 * Subscribes a particular customer with identified by the ID to a plan identified by plan ID.
	 * @param customerId a String
	 * @param planId a String
	 * @return Success or Failure of the operation.
	 * @throws InvalidInputException 
	 */
	public boolean subscribeCustomer(String customerId,String planId) throws InvalidInputException{
		
		if(customerId == null || customerId.equals("")){
			LOG.error("subscribeCustomer : first parameter is null or empty!");
			throw new InvalidInputException("subscribeCustomer : first parameter is null or empty!");			
		}
		
		if(planId == null || planId.equals("")){
			LOG.error("subscribeCustomer : second parameter is null or empty!");
			throw new InvalidInputException("subscribeCustomer : second parameter is null or empty!");	
		}
		
		Customer customer = gateway.customer().find(customerId);
		SubscriptionRequest request = new SubscriptionRequest()
			.planId(planId)
			.paymentMethodToken(customer.getPaymentMethods().get(0).getToken());
		
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
	public boolean subscribe(User user,Company company, Integer planId, String nonce) throws NonFatalException {
		
		boolean result=false;
		if(company == null){
			LOG.error("subscribe : first parameter is null!");
			throw new InvalidInputException("subscribe : first parameter is null!");	
		}
		
		if(planId == null || planId.equals("")){
			LOG.error("subscribe : second parameter is null or empty!");
			throw new InvalidInputException("subscribe : second parameter is null or empty!");	
		}
		
		if(nonce == null || nonce.equals("")){
			LOG.error("subscribe : third parameter is null or empty!");
			throw new InvalidInputException("subscribe : third parameter is null or empty!");
		}
		
		
		String planIdString = propertyFileReader.getProperty("config.properties", planId.toString());
		
		Integer companyId = company.getCompanyId();
		if(containsCustomer(companyId.toString())){
			result = subscribeCustomer(companyId.toString(), planIdString);
		}
		else{
			addCustomerWithPayment(company, nonce);
			result = subscribeCustomer(companyId.toString(), planIdString);
		}	
		if(result){
			updateLicenseTable(planId, companyId,user.getUserId());
		}
		return result;
	}
	
	/**
	 * Returns a Braintree client token that is used by the frontend to setup the 
	 * drop-in UI.
	 * @return
	 */
	public String getClientToken(){
		
		LOG.info("Generating client token");
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
		
		if(customerId == null || customerId.equals("")){
			LOG.error("getClientTokenWithCustomerId : parameter is null or empty!");
			throw new InvalidInputException("getClientTokenWithCustomerId : parameter is null or empty!");			
		}
		
		LOG.info("Generating client token for customer ID : " + customerId);
		ClientTokenRequest request = new ClientTokenRequest().customerId(customerId);
		String clientToken = gateway.clientToken().generate(request);
		LOG.info("Client token for customer ID " + customerId +" : " + clientToken );
		return clientToken;
	}
			
	/**
	 * Returns the instance of the gateway being used.
	 * @return
	 */
	public BraintreeGateway getGatewayInstance(){
		return gateway;
	}
	
}
