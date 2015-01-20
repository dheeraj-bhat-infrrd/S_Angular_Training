package com.realtech.socialsurvey.batch.commons;

import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;

@Component
public class BatchCommon {
	
	@Autowired
	private GenericDao<User, Long> userDao;
	
	private static final Logger LOG = LoggerFactory.getLogger(BatchCommon.class);
	
	public User getCorporateAdmin(Company company) throws InvalidInputException, NoRecordsFetchedException {

		if (company == null) {
			LOG.error("Parameter to getCorporateAdmin is null!");
			throw new InvalidInputException("Parameter to getCorporateAdmin is null!");
		}

		LOG.debug("Fetching corporate admin user for the company with id : " + company.getCompanyId());
		User user = null;
		List<User> users = null;
		
		//Fetching the list of users from USERS table with the IS_OWNER set and having the same company.
		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_COLUMN, company);
		queries.put(CommonConstants.IS_OWNER_COLUMN, CommonConstants.IS_OWNER);

		LOG.debug("Making the database call to USERS table to fetch records.");
		users = userDao.findByKeyValue(User.class, queries);

		if (users == null || users.isEmpty()) {
			LOG.error("No users as corporate admins found for the company with id : " + company.getCompanyId());
			throw new NoRecordsFetchedException("No users as corporate admins found for the company with id : " + company.getCompanyId());
		}

		user = users.get(CommonConstants.INITIAL_INDEX);

		LOG.debug("Returning user found as corporate admin of company with user id : " + user.getUserId());
		return user;
	}

}
