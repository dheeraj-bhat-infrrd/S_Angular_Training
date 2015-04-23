package com.realtech.socialsurvey.core.entities;

import java.util.List;

/**
 * User List with count entity from the search
 */
public class UserListFromSearch {

	private long userFound;
	private List<ProListUser> users;

	public long getUserFound() {
		return userFound;
	}

	public void setUserFound(long userFound) {
		this.userFound = userFound;
	}

	public List<ProListUser> getUsers() {
		return users;
	}

	public void setUsers(List<ProListUser> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "userFound: " + userFound + "\tusers: " + users;
	}
}