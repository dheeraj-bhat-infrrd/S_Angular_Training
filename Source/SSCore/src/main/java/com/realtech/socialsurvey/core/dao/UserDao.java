package com.realtech.socialsurvey.core.dao;

import java.util.List;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;

/*
 * This interface contains methods which are required for queries and criteria on User table.
 */
public interface UserDao extends GenericDao<User, Long> {

	public User fetchUserByEmailId(User user, String emailId) throws NoRecordsFetchedException;

	public List<User> fetchUsersBySimilarEmailId(User user, String emailId);
	
	public int getUsersCountForCompany(Company company);

}
