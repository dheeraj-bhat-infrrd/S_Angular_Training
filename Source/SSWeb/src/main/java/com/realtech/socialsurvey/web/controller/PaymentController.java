package com.realtech.socialsurvey.web.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.payment.Payment;

/**
 * Handles the payment actions by user
 *
 */
@Controller
public class PaymentController {
	
	@Autowired
	Payment payment;
	
	/**
	 * Method for a user to pay for a plan
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/payforplan", method= RequestMethod.POST)
	public String payForPlan(Model model, HttpServletRequest request){
		// TODO: Validate form fields
		User user = null; // TODO: Get from session
		Company company = null; //TODO: Get from user object
		String sAccountTypeId = request.getParameter("accountTypeId");
		// TODO: Convert sAccountTypeId to int
		int iAccountId = Integer.parseInt(sAccountTypeId);
		String planId = null; // TODO: get from request
		String nonce = null; // TODO: get from request
		try {
			payment.subscribe(user, company, iAccountId, planId, nonce);
		}
		catch (NonFatalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
