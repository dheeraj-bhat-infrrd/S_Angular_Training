package com.realtech.socialsurvey.core.entities;

import java.util.List;
import org.apache.solr.common.SolrDocument;

/**
 * User List with count entity from the search
 */
public class UserListFromSearch {

	private long userFound;
	private List<SolrDocument> users;

	public long getUserFound() {
		return userFound;
	}

	public void setUserFound(long userFound) {
		this.userFound = userFound;
	}

	public List<SolrDocument> getUsers() {
		return users;
	}

	public void setUsers(List<SolrDocument> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "userFound: " + userFound + "\tusers: " + users;
	}
}