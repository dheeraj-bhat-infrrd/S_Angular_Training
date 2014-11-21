package com.realtech.socialsurvey.core.services.mail.impl;

import java.util.List;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.services.mail.EmailServices;

/**
 * Implementation file for the email services
 *
 */
@Component
public class EmailServicesImpl implements EmailServices {

	@Override
	public void sendRegistrationInviteMail(String url, List<String> recipients) {

	}

}
