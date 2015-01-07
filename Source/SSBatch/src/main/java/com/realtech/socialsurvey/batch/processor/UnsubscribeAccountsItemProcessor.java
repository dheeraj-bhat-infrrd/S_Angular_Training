package com.realtech.socialsurvey.batch.processor;
// SS-74 RM03

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.braintreegateway.exceptions.UnexpectedException;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.DisabledAccount;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionCancellationUnsuccessfulException;

/**
 * The processor class for the batch job for cancelling subscriptions
 */
public class UnsubscribeAccountsItemProcessor implements ItemProcessor<DisabledAccount, Map<String, Object>>{
	
	@Autowired
	private Payment gateway;
	
	@Autowired
	private GenericDao<Company, Long> companyDao;
	
	@Autowired
	private GenericDao<DisabledAccount, Long> disabledAccountDao;
	
	@Autowired
	private GenericDao<User, Long> userDao;
	
	@Value("${ADMIN_EMAIL_ID}")
	private String recipientMailId;
	
	@Autowired
	private EmailServices emailServices;
	
	Map<String, Object> writerObjectsMap = new HashMap<String, Object>();
	
	private static final Logger LOG = LoggerFactory.getLogger(UnsubscribeAccountsItemProcessor.class);
	
	private User getCorporateAdmin(Company company) throws InvalidInputException, NoRecordsFetchedException {

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
	
	private void sendFailureMail(Exception e) {

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
	
	private void sendEmailSendingFailureMail(String destinationMailId,String displayName,Exception e) {

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

	@Override
	public Map<String,Object> process(DisabledAccount disabledAccount) {
		
		LOG.info("Processing the Disabled Account record with id : " + disabledAccount.getId());
		
		try{
			Company company = disabledAccount.getCompany();
			
			//Make Api call to cancel the subscription.
			LOG.info("Making api call to Braintree to cancel the subscription.");
			gateway.unsubscribe(disabledAccount.getLicenseDetail().getSubscriptionId());
			LOG.info("Cancellation successful.");
			
			//Sending mail to the respective user. We fetch the corporate admin for the company
			User user = getCorporateAdmin(company);
			LOG.info("Sending mail to the respective corporate admin with email id : " + user.getEmailId());
			try {
				emailServices.sendAccountDisabledMail(user.getEmailId(), user.getDisplayName());
				throw new UndeliveredEmailException("Email not delivered.");
			}
			catch (UndeliveredEmailException e) {
				LOG.error("UnsubscribeAccountsItemProcessor : Exception caught when sending account disabled mail. Message : " + e.getMessage());
				sendEmailSendingFailureMail(user.getEmailId(), user.getDisplayName(), e);
			}
			LOG.info("Email successfully sent!");
			
			//Updating the company record to reflect changes
			LOG.info("Updating corresponding company entity to reflect changes");
			company.setStatus(CommonConstants.STATUS_INACTIVE);
			company.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			writerObjectsMap.put(CommonConstants.COMPANY_OBJECT_KEY, company);
			LOG.info("Updated company entity");
			
			//Performing soft delete of the disabled account record
			LOG.info("Updating the disabled account record to perform soft delete.");
			disabledAccount.setStatus(CommonConstants.STATUS_INACTIVE);
			disabledAccount.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			writerObjectsMap.put(CommonConstants.DISABLED_ACCOUNT_OBJECT_KEY, disabledAccount);
			LOG.info("Updated disabled account entity to reflect changes.");			
			
		}
		catch(SubscriptionCancellationUnsuccessfulException e){			
			
			LOG.error("Subscription cancellation unsuccessful for Disabled Account with id : " + disabledAccount.getId());
			sendFailureMail(e);		
			LOG.info("Processing of the item with id : " + disabledAccount.getId() + " UNSUCCESSFUL!");
			return null;
			
		}
		catch(UnexpectedException e){
			
			LOG.error("Unexpected Exception caught : Message : " + e.getMessage());
			sendFailureMail(e);
			LOG.info("Processing of the item with id : " + disabledAccount.getId() + " UNSUCCESSFUL!");
			return null;
		}
		catch (InvalidInputException | NoRecordsFetchedException e) {
			
			LOG.error("Invalid Input Exception caught : Message : " + e.getMessage(),e);
			LOG.info("Processing of item : License detail object with id : " + disabledAccount.getId() + " UNSUCCESSFUL");
			return null;
		}
		
		LOG.info("Processing of the Disabled Account record with id : " + disabledAccount.getId() + " Successful.");
		return writerObjectsMap;
	}

}
