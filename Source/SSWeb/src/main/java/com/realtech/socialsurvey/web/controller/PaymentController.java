package com.realtech.socialsurvey.web.controller;
//JIRA: SS-15: By RM03

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.web.common.JspResolver;

/**
 * Handles the payment actions by user
 *
 */
@Controller
public class PaymentController {
	
	private static final Logger LOG = LoggerFactory.getLogger(PaymentController.class);
	
	@Autowired
	Payment gateway;
	
	@RequestMapping(value="/payment")
	public String paymentPage(Model model,HttpServletResponse response,HttpServletRequest request){
		
		LOG.info("Request for paymentPage : sending payment.jsp!");
		gateway.initialise();
		model.addAttribute("clienttoken", gateway.getClientToken());
		return JspResolver.PAYMENT;
	}
	
	/**
	 * Method for a user to pay for a plan.
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/subscribe", method= RequestMethod.POST)
	public String subscribeForPlan(Model model, HttpServletRequest request,HttpServletResponse response){
		
		LOG.info("Request for subscribeForPlan : sending subscribe.jsp!");
		
		boolean status=false;
		gateway.initialise();
		User user = new User();
		user.setUserId(10001);
		
		Company company = new Company();
		company.setCompany("Rare Mile Tech");
		company.setCompanyId(1001);
		user.setCompany(company);
		
		
		int planId = 1; // TODO: get from request
		String nonce = com.braintreegateway.test.Nonce.Transactable; // TODO: get from request
		try {
			status = gateway.subscribe(user,company, planId, nonce);
		}
		catch (NonFatalException e) {
			e.printStackTrace();
		}
		 		
		if(status == true){
			model.addAttribute("subscribeStatus", "You have been subscribed!");
		}
		else{
			model.addAttribute("subscribeStatus", "There was an issue! It will be resolved!");
		}
		
		return JspResolver.SUBSCRIBE;
	}

}
