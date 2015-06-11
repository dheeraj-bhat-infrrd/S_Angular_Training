package com.realtech.socialsurvey.web.security;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.authentication.AuthenticationService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;

public class UserAuthProvider extends DaoAuthenticationProvider {

	private static final Logger LOG = LoggerFactory.getLogger(UserAuthProvider.class);

	@Autowired
	private AuthenticationService authenticationService;

	@Override
	public Authentication authenticate(Authentication authentication) {
		LOG.info("Inside authenticate Method of UserAuthProvider");

		String username = authentication.getName();
		String password = authentication.getCredentials().toString();
		User user;
		try {
			LOG.debug("Validating the form parameters");
			validateLoginFormParameters(username, password);
			user = authenticationService.getUserWithLoginName(username);

			LOG.debug("Check if company status is active");
			if (user.getCompany().getStatus() == CommonConstants.STATUS_INACTIVE) {
				throw new InvalidInputException("Company is inactive in login", DisplayMessageConstants.COMPANY_INACTIVE);
			}

			LOG.debug("Checking if user is not in inactive mode");
			if (user.getStatus() == CommonConstants.STATUS_INACTIVE) {
				throw new InvalidInputException("User not active in login", DisplayMessageConstants.USER_INACTIVE);
			}

			authenticationService.validateUser(user, password);
			if (user != null) {
				// Invalidate All other users in a browser
				SecurityContextHolder.clearContext();

				List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
				grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
				Authentication auth = new UsernamePasswordAuthenticationToken(user, password, grantedAuths);
				LOG.info("Authentication provided for user : " + user.getEmailId());
				return auth;
			}
		}
		catch (NoRecordsFetchedException e) {
			LOG.error("User not found in the system: " + e.getMessage());
			throw new UsernameNotFoundException("User not found in the system");
		}
		catch (InvalidInputException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	@Override
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		LOG.info("Inside setUserDetailsService Method of UserAuthProvider");
		super.setUserDetailsService(userDetailsService);
		LOG.info("Completed setUserDetailsService Method of UserAuthProvider");
	}

	/**
	 * Verify the login Form Parameters
	 * 
	 * @param username
	 * @param password
	 * @throws InvalidInputException
	 */
	private void validateLoginFormParameters(String username, String password) throws InvalidInputException {
		LOG.debug("Validating Login form paramters loginName :" + username);
		if (username == null || username.isEmpty()) {
			throw new InvalidInputException("User name passed can not be null", DisplayMessageConstants.INVALID_USERNAME);
		}
		if (password == null || password.isEmpty()) {
			throw new InvalidInputException("Password passed can not be null", DisplayMessageConstants.INVALID_PASSWORD);
		}
		LOG.debug("Login form parameters validated successfully");
	}
}