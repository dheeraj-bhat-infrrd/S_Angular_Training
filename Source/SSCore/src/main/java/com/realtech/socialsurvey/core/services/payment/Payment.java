package com.realtech.socialsurvey.core.services.payment;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Handles the payment options for the application
 *
 */
public interface Payment {
	
	public boolean subscribe(User user, Company company, int accountsMasterId, String planId, String nonce) throws NonFatalException;

}
