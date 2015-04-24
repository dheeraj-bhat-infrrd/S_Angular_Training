package com.realtech.socialsurvey.core.commons;
// RM03
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;

/**
 * Contains methods used in all projects.
 *
 */
@Component
public class CoreCommon {
	
	@Value("${ADMIN_EMAIL_ID}")
	private String recipientMailId;
	
	@Autowired
	private EmailServices emailServices;
	
	@Autowired
	private GenericDao<User, Long> userDao;
	
	private static final Logger LOG = LoggerFactory.getLogger(CoreCommon.class);

	/**
	 * Sends a failure mail with exception details to the admin.
	 * @param e
	 */
	public void sendFailureMail(Exception e) {

		LOG.debug("Sending failure mail to recpient : " + recipientMailId);
		String stackTrace = ExceptionUtils.getFullStackTrace(e);
		// replace all dollars in the stack trace with \$
		stackTrace = stackTrace.replace("$", "\\$");

		try {
			emailServices.sendFatalExceptionEmail(recipientMailId, stackTrace);
			LOG.debug("Failure mail sent to admin.");
		}
		catch (InvalidInputException | UndeliveredEmailException e1) {
			LOG.error("CustomItemProcessor : Exception caught when sending Fatal Exception mail. Message : " + e1.getMessage());
		}
	}
	
	/**
	 * Send the mail to admin when an email sending fails with the email id and the name to whom
	 * the email had to be sent to.
	 * @param destinationMailId
	 * @param displayName
	 * @param e
	 */
	public void sendEmailSendingFailureMail(String destinationMailId,String displayName,Exception e) {

		LOG.debug("Sending failure mail to recpient : " + recipientMailId);
		String stackTrace = ExceptionUtils.getFullStackTrace(e);
		// replace all dollars in the stack trace with \$
		stackTrace = stackTrace.replace("$", "\\$");

		try {
			emailServices.sendEmailSendingFailureMail(recipientMailId, destinationMailId, displayName, stackTrace);;
			LOG.debug("Failure mail sent to admin.");
		}
		catch (InvalidInputException | UndeliveredEmailException e1) {
			LOG.error("CustomItemProcessor : sendEmailSendingFailureMail : Exception caught when sending Exception mail. Message : " + e1.getMessage());
		}
	}
	
	/**
	 * Method that returns the corporate admin's User object.
	 * @param company
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
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
			//No corporate admins have been found
			LOG.error("No users as corporate admins found for the company with id : " + company.getCompanyId());
			throw new NoRecordsFetchedException("No users as corporate admins found for the company with id : " + company.getCompanyId());
		}
		
		//The user object to be returned
		user = users.get(CommonConstants.INITIAL_INDEX);

		LOG.debug("Returning user found as corporate admin of company with user id : " + user.getUserId());
		return user;
	}

}
