package com.realtech.socialsurvey.web.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

public class UserVO implements UserDetails {

	private static final long serialVersionUID = 7303242226123871178L;

	public UserVO() {
		this.authorities = new GrantedAuthority[] { new GrantedAuthorityImpl("ROLE_USER") };
	}

	private Long id;
	private String displayName;
	private String email;
	private String password;
	private int isOwner;
	private boolean accountNonExpired = true;
	private boolean accountNonLocked = true;
	private boolean credentialsNonExpired = true;
	private boolean enabled = true;
	private GrantedAuthority[] authorities;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String emailID) {
		this.email = emailID;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getIsOwner() {
		return isOwner;
	}

	public void setIsOwner(int isOwner) {
		this.isOwner = isOwner;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	public void setAuthorities(GrantedAuthority[] authorities) {
		this.authorities = authorities.clone();
	}

	public String getUsername() {
		return this.email;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public boolean isEnabled() {
		return enabled;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserVO [id=" + id + ", displayName=" + displayName + ", email=" + email + "]";
	}
}