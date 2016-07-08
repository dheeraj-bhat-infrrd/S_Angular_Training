package com.realtech.socialsurvey.auth.services.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.realtech.socialsurvey.auth.model.CustomUserDetails;
import com.realtech.socialsurvey.auth.repositories.UserRepository;
import com.realtech.socialsurvey.auth.entities.User;
import com.realtech.socialsurvey.auth.entities.UserProfile;

@Service("CustomUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user;
		try {
			user = userRepository.findByEmailWithRoles(username);
			Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
			for (UserProfile role : user.getUserProfiles()) {
				authorities.add(new SimpleGrantedAuthority(role.getProfilesMaster().getProfile()));
			}

			CustomUserDetails userDetails = new CustomUserDetails(user.getLoginName(), user.getLoginPassword(), authorities);
			userDetails.setFirstName(user.getFirstName());
			userDetails.setLastName(user.getLastName());

			return userDetails;
		} catch (Exception e) {
			throw new UsernameNotFoundException(
					String.format("Error getting User %s with the following error: %s", username, e.getMessage()));
		}
	}
}