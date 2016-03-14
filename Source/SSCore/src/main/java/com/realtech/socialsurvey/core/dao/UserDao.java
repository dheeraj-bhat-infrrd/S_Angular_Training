package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;

/*
 * This interface contains methods which are required for queries and criteria on User table.
 */
public interface UserDao extends GenericDao<User, Long> {

	public List<User> fetchUsersBySimilarEmailId(User user, String emailId);

	public long getUsersCountForCompany(Company company);

	public List<User> getUsersForCompany(Company company);

	// JIRA SS-76 by RM-06 : BOC
	
	public User getActiveUser(String emailId) throws NoRecordsFetchedException;

	public List<User> fetchUsersByEmailId(List<String> emailIds);

	public void deleteUsersByCompanyId(long companyId);

    User getActiveUserByEmailAndCompany( String emailId, Company company ) throws NoRecordsFetchedException;

    public List<User> getUsersForUserIds( List<Long> userIds ) throws InvalidInputException;

    public List<User> getUsersForCompany( Company company, int start, int batch ) throws InvalidInputException;

    public List<Long> getUserIdsUnderCompanyBasedOnProfileMasterId( long companyId, int profileMasterId, int start, int batchSize )
        throws InvalidInputException;
	// JIRA SS-76 by RM-06 : EOC

}
