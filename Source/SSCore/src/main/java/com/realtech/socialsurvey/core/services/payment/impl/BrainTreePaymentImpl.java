package com.realtech.socialsurvey.core.services.payment.impl;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
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
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.generator.impl.UrlGeneratorImpl;
import com.realtech.socialsurvey.core.services.payment.Payment;

//@Component
public class BrainTreePaymentImpl implements Payment {
	
	private static final Logger LOG = LoggerFactory.getLogger(UrlGeneratorImpl.class);

	BraintreeGateway gateway = null;	
	
	public void initialise(){		
		gateway = new BraintreeGateway(
		        Environment.SANDBOX,
		        "q5vjmsqhvr5swjzd",
		        "cr8ht78s8kgfyw7t",
		        "0950310c09106463f32aa2f931af5ef0"
		    );		
	}
	
	private boolean addCustomerWithoutPayment(User user, Company company){

		CustomerRequest request = new CustomerRequest()
			.company(user.getCompany().getCompany())
			.id(Integer.toString(user.getUserId()))
			.email(user.getEmailId())
			.firstName(user.getDisplayName());

		
		Result<Customer> result = gateway.customer().create(request);
		
		LOG.info("addCustomerWithPayment : adding user " + Integer.toString(user.getUserId()) + " : Status : " + result.isSuccess() + " Message : " + result.getMessage());
		
		return result.isSuccess();		
	}
	
	public boolean addCustomerWithPayment(User user, Company company,String nonce){
		CustomerRequest request = new CustomerRequest()
			.company(user.getCompany().getCompany())
			.id(Integer.toString(user.getUserId()))
			.email(user.getEmailId())
			.firstName(user.getDisplayName())
			.paymentMethodNonce(nonce);
		
		Result<Customer> result = gateway.customer().create(request);
		
		LOG.info("addCustomerWithPayment : adding user " + Integer.toString(user.getUserId()) + " : Status : " + result.isSuccess() + " Message : " + result.getMessage());
		
		return result.isSuccess();		
	}
	
	public boolean containsCustomer(String customerId){
		
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
	
	public boolean subscribeCustomer(String customerId,String planId){
		
		Customer customer = gateway.customer().find(customerId);
		SubscriptionRequest request = new SubscriptionRequest()
			.planId(planId)
			.paymentMethodToken(customer.getPaymentMethods().get(0).getToken());
		
		Result<Subscription> result = gateway.subscription().create(request);
		
		LOG.info("subscribeCustomer : customerId : " + customerId + " for planId : " + planId + " Status : " + result.isSuccess()+ " Message : " + result.getMessage() );
		return result.isSuccess();		
	}

	@Override
	public void subscribe(User user, Company company, int accountsMasterId, String planId, String nonce) throws NonFatalException {
		
		Integer userId = user.getUserId();
		if(containsCustomer(userId.toString())){
			boolean result = subscribeCustomer(userId.toString(), planId);
		}
		else{
			addCustomerWithPayment(user, company, nonce);
			boolean result = subscribeCustomer(userId.toString(), planId);
		}	
	}
	
	public String getClientToken(){
		
		return gateway.clientToken().generate();
	}
	
	public String getClientTokenWithCustomerId(String customerId){
		
		ClientTokenRequest request = new ClientTokenRequest().customerId(customerId);
		return gateway.clientToken().generate(request);
	}
	
	public String makePayment(int amount,String paymentNonce){
		
		TransactionRequest request = new TransactionRequest()
	    .amount(new BigDecimal(amount))
	    .paymentMethodNonce(paymentNonce);

		Result<Transaction> result = gateway.transaction().sale(request);
		
		if(result.isSuccess()){
			String transactionId = result.getTarget().getId();
			LOG.info(" makePayment : for amount : " + amount + " Status : " + result.isSuccess() + " Message : " + result.getMessage() + " Transaction ID : " + transactionId );
			return transactionId;
		}
		else{
			LOG.info(" makePayment : for amount : " + amount + " Status : " + result.isSuccess() + " Message : " + result.getMessage() );
			return null;

		}
				
	}
	
	public static void main(String[] args){
		
		BrainTreePaymentImpl payment = new BrainTreePaymentImpl();
		payment.initialise();
		
		Company company = new Company();
		company.setCompany("Rare Mile Tech");
		
		User user = new User();
		user.setCompany(company);
		user.setDisplayName("nishit kannan");
		user.setEmailId("nishit@raremile.com");
		user.setUserId(100002);
		
		System.out.println(payment.makePayment(1000, com.braintreegateway.test.Nonce.Transactable));
		
	}
}
