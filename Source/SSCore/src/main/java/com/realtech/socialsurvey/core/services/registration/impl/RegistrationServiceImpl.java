package com.realtech.socialsurvey.core.services.registration.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.UserInvite;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.registration.RegistrationService;

@Component
public class RegistrationServiceImpl implements RegistrationService {

	private static final Logger LOG = LoggerFactory.getLogger(RegistrationServiceImpl.class);

	@Autowired
	private URLGenerator urlGenerator;

	@Value("{APPLICATION_BASE_URL}")
	private String applicationBaseUrl;

	@Autowired
	private EmailServices emailServices;
	
	@Autowired
	private GenericDao<UserInvite, Integer> userInviteDao;
	
	@Autowired
	private GenericDao<Company, Integer> companyDao;
	
	@Autowired
	private GenericDao<ProfilesMaster, Integer> profilesMasterDao;

	@Override
	@Transactional
	public void inviteCorporateToRegister(String firstName, String lastName, String emailId) throws InvalidInputException, UndeliveredEmailException {
		LOG.info("Inviting corporate to register. Details\t first name:" + firstName + "\t lastName: " + lastName + "\t email id: " + emailId);

		Map<String, String> urlParams = new HashMap<String, String>();
		urlParams.put("firstName", firstName);
		urlParams.put("lastName", lastName);
		urlParams.put("emailId", emailId);

		LOG.debug("Generating URL");
		String url = urlGenerator.generateUrl(urlParams, applicationBaseUrl);

		LOG.debug("Sending invitation for registration");
		inviteUser(url, emailId, firstName, lastName);

		LOG.info("Successfully sent invitation to :" + emailId + " for registration");
	}

	@Transactional(rollbackFor = { NonFatalException.class, FatalException.class })
	private void inviteUser(String url, String emailId, String firstName, String lastName) throws InvalidInputException, UndeliveredEmailException {
		LOG.info("Method inviteUser called with url : " + url + " emailId : " + emailId + " firstname : " + firstName + " lastName : " + lastName);

		String queryParam = extractUrlQueryParam(url);
		
		LOG.debug("Adding a new inviatation into the user_invite table");
		int invitatioId = storeInvitation(queryParam, emailId);
		
		LOG.debug("Calling email services to send registration invitation mail");
		emailServices.sendRegistrationInviteMail(url, emailId, firstName, lastName);

		LOG.debug("Updating invitaion id as successful");
		storeInvitation(invitatioId);
		
		LOG.info("Method inviteUser finished successfully");

	}
	
	/**
	 * Method to extract the query parameter from encrypted url
	 * 
	 * @param url
	 * @return
	 * @throws InvalidInputException
	 */
	private String extractUrlQueryParam(String url)
			throws InvalidInputException {
		if (url == null || url.isEmpty()) {
			throw new InvalidInputException(
					"Url is found null or empty while extracting the query param");
		}
		LOG.debug("Getting query param from the encrypted url " + url);
		String queryParam = url.substring(url.indexOf("q=") + 2, url.length());

		LOG.debug("Returning query param : " + queryParam);
		return queryParam;

	}
	
	/*
	 * This method stores the invitation related info into user_invite.
	 * It sets all the required values in table and puts status as 0.
	 */
	private Integer storeInvitation(String queryParam, String emailId) {
		LOG.info("Method storeInvitation called with query param : " + queryParam + " and emailId : " + emailId);
		UserInvite userInvite = new UserInvite();
		Company company = companyDao.findById(Company.class, 0);
		System.out.println(company.getCompany());
		ProfilesMaster profilesMaster = profilesMasterDao.findById(ProfilesMaster.class, 0);
		userInvite.setCompany(company);
		userInvite.setProfilesMaster(profilesMaster);
		userInvite.setInvitationEmailId(emailId);
		userInvite.setInvitationParameters(queryParam);
		userInvite.setInvitationSentBy(1);
		userInvite.setStatus(0);
		userInvite.setModifiedBy("GUEST");
		userInvite.setCreatedBy("GUEST");
		userInvite = userInviteDao.save(userInvite);
		LOG.info("Method storeInvitation finished");
		return userInvite.getUserInviteId();
	}
	
	/*
	 * This method updates the status for the given invitation id as successful.
	 */
	private void storeInvitation(int invitationId) {
		LOG.info("Method storeInvitation called with invitation ID : " + invitationId);
		UserInvite userInvite = userInviteDao.findById(UserInvite.class, invitationId);
		userInvite.setStatus(1);
		userInvite.setModifiedBy("AGENT");
		userInvite = userInviteDao.saveOrUpdate(userInvite);
		LOG.info("Method storeInvitation finished");
	}

}
