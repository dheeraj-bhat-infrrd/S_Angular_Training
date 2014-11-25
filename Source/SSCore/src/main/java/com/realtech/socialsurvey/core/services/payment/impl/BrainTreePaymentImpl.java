package com.realtech.socialsurvey.core.services.payment.impl;

import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.payment.Payment;

@Component
public class BrainTreePaymentImpl implements Payment {

	@Override
	public void subscribe(User user, Company company, int accountsMasterId, String planId, String nonce) throws NonFatalException {
		// TODO Auto-generated method stub

	}

}
