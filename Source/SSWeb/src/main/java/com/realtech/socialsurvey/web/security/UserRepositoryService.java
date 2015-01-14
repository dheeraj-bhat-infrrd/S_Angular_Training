package com.realtech.socialsurvey.web.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.authentication.AuthenticationService;

public class UserRepositoryService implements UserDetailsService {

	private static final Logger LOG = LoggerFactory.getLogger(UserRepositoryService.class);

	@Autowired
	private AuthenticationService authenticationService;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		LOG.info("Method: loadUserByUsername called.");
		UserVO userVO = null;
		User user = null;
		try {
			LOG.info("auth service : " + authenticationService);
			user = authenticationService.getUserWithLoginName(username);
			LOG.info("user : " + user.getEmailId());
		}
		catch (NoRecordsFetchedException e) {
			throw new UsernameNotFoundException("User not found in the system");
		}

		if (user != null) {
			userVO = new UserVO();
			userVO.setId(user.getUserId());
			userVO.setEmail(user.getEmailId());
			userVO.setPassword(user.getLoginPassword());
			userVO.setDisplayName(user.getDisplayName());
			userVO.setIsOwner(user.getIsOwner());
		}
		LOG.info("Method: loadUserByUsername finished.");
		return userVO;
	}
}