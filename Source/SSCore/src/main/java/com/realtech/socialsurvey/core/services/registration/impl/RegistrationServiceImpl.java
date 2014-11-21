package com.realtech.socialsurvey.core.services.registration.impl;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.registration.RegistrationService;

@Component
public class RegistrationServiceImpl implements RegistrationService {

	private static final Logger LOG = LoggerFactory.getLogger(RegistrationServiceImpl.class);
	
	@Autowired
	private URLGenerator urlGenerator;
	@Autowired
	EmailServices emailServices;
	
	@Override
	public void inviteCorporateToRegister(String firstName, String lastName, String emailId) throws InvalidInputException {
		LOG.info("Inviting corporate to register. Details\t first name:"+firstName+"\t lastName: "+lastName+"\t email id: "+emailId);
		// TODO: Retrieve base url
		String baseUrl = "";
		Map<String, String> urlParams = new HashMap<>();
		urlParams.put("firstName", firstName);
		urlParams.put("lastName", lastName);
		urlParams.put("emailId", emailId);
		LOG.debug("Generating URL");
		String url = urlGenerator.generateUrl(urlParams, baseUrl);
		// invite the user
		inviteUser(url, emailId);
	}
	
	@Transactional(rollbackFor = {NonFatalException.class, FatalException.class})
	private void inviteUser(String url, String emailId){
		
	}

}
