package com.realtech.socialsurvey.core.services.payment.impl;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.payment.Payment;

public class BrainTreePaymentImpl implements Payment {

	@Override
	public void subscribe(User user, Company company, int accountsMasterId, String planId, String nonce) throws NonFatalException {
		// TODO Auto-generated method stub

	}

}
