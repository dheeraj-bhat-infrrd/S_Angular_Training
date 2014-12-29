package com.realtech.socialsurvey.processor;
//JIRA: SS-61: By RM03

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.braintreegateway.Transaction;
import com.braintreegateway.exceptions.UnexpectedException;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.RetriedTransaction;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.RetryUnsuccessfulException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.payment.Payment;

/**
 * This is the custom item proccessor class that processes the LicenseDetail table.
 */
@Component
public class CustomItemProcessor implements ItemProcessor<LicenseDetail, LicenseDetail> {

	private Timestamp now = new Timestamp(System.currentTimeMillis());

	@Autowired
	private GenericDao<RetriedTransaction, Integer> retriedTransactionDao;

	@Autowired
	private GenericDao<LicenseDetail, Integer> licenseDetailDao;

	@Autowired
	private GenericDao<User, Integer> userDao;

	@Autowired
	private GenericDao<Company, Integer> companyDao;

	@Autowired
	Payment paymentGateway;

	@Value("${ADMIN_EMAIL_ID}")
	String recipientMailId;

	@Value("${MAX_PAYMENT_RETRIES}")
	int maxPaymentRetries;

	@Autowired
	EmailServices emailServices;

	@Value("${PAYMENT_RETRY_DAYS}")
	private int retryDays;

	private static final Logger LOG = LoggerFactory.getLogger(CustomItemProcessor.class);

	private User getCorporateAdmin(Company company) throws InvalidInputException, NoRecordsFetchedException {

		if (company == null) {
			LOG.error("Parameter to getCorporateAdmin is null!");
			throw new InvalidInputException("Parameter to getCorporateAdmin is null!");
		}

		LOG.debug("Fetching corporate user for the company with id : " + company.getCompanyId());
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

	private RetriedTransaction checkForExistingTransactions(LicenseDetail licenseDetail) throws InvalidInputException {

		if (licenseDetail == null) {
			LOG.error("Null parameter given to checkForExistingTransactions!");
			throw new InvalidInputException("Null parameter given to checkForExistingTransactions!");
		}
		
		LOG.debug("Checking for existing transactions for the License Id : " + licenseDetail.getLicenseId());
		RetriedTransaction existingTransaction = null;
		
		// find records in RETRIED_TRANSACTIONS table with the same license id or in hibernate sense the object.
		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.LICENSE_DETAIL_COLUMN, licenseDetail);

		LOG.debug("Querying RetriedTransaction table for transactions for license id : " + licenseDetail.getLicenseId());

		List<RetriedTransaction> retriedTransactions = retriedTransactionDao.findByKeyValue(RetriedTransaction.class, queries);

		if (retriedTransactions == null || retriedTransactions.isEmpty()) {

			LOG.debug("No transactions found for the id : " + licenseDetail.getLicenseId());
			return null;
		}

		existingTransaction = retriedTransactions.get(CommonConstants.INITIAL_INDEX);

		LOG.debug("Transaction found. Returning it.");
		return existingTransaction;
	}

	private boolean removeTransactionAndUpdateLicenseDetail(LicenseDetail licenseDetail, RetriedTransaction retriedTransaction)
			throws InvalidInputException {

		boolean status = false;

		if (licenseDetail == null) {
			LOG.error("Parameter given to removeTransactionAndUpdateLicenseDetail is null!");
			throw new InvalidInputException("Parameter given to removeTransactionAndUpdateLicenseDetail is null!");
		}

		if (retriedTransaction == null) {
			LOG.error("Second parameter given to removeTransactionAndUpdateLicenseDetail is null!");
			throw new InvalidInputException("Second parameter given to removeTransactionAndUpdateLicenseDetail is null!");
		}

		LOG.debug("Updating License detail entity with id : " + licenseDetail.getLicenseId() + " to show that it isnt due on subscription");

		licenseDetail.setNextRetryTime(new Timestamp((CommonConstants.EPOCH_TIME_IN_MILLIS)));
		licenseDetail.setPaymentRetries(CommonConstants.INITIAL_PAYMENT_RETRIES);
		licenseDetail.setIsSubscriptionDue(CommonConstants.SUBSCRIPTION_NOT_DUE);
		licenseDetail.setModifiedOn(now);

		LOG.debug("Deleteing Retried Transaction entity with id : " + retriedTransaction.getRetryId() + "from the table.");
		retriedTransactionDao.delete(retriedTransaction);

		LOG.debug("All changes for License Id : " + licenseDetail.getLicenseId() + " made successfully");

		return status;

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
		catch (InvalidInputException e1) {
			LOG.error("CustomItemProcessor : InvalidInputException caught when sending Fatal Exception mail. Message : " + e1.getMessage());
		}
		catch (UndeliveredEmailException e1) {
			LOG.error("CustomItemProcessor : UndeliveredEmailException caught when sending Fatal Exception mail. Message : " + e1.getMessage());

		}

	}

	private Transaction retryChargeAndSendMail(LicenseDetail licenseDetail) throws InvalidInputException, RetryUnsuccessfulException,
			NoRecordsFetchedException {

		if (licenseDetail == null) {
			LOG.error("Parameter given to removeTransactionAndUpdateLicenseDetail is null!");
			throw new InvalidInputException("Parameter given to removeTransactionAndUpdateLicenseDetail is null!");
		}

		LOG.debug("Retrying charge for subscription id : " + licenseDetail.getSubscriptionId());
		Transaction transaction = null;
		
		//Retrying the subscription charge.
		transaction = paymentGateway.retrySubscriptionCharge(licenseDetail.getSubscriptionId());

		LOG.debug("Retry successful. Sending email.");

		// Sending retry email to client.
		LOG.debug("Fetching the corporate admin");
		User user = getCorporateAdmin(licenseDetail.getCompany());
		LOG.debug("Sending mail for retrying subscription charge.");

		try {
			emailServices.sendRetryChargeEmail(user.getEmailId(), user.getDisplayName(), String.valueOf(licenseDetail.getPaymentRetries() + 1));
		}
		catch (InvalidInputException e1) {
			LOG.error("CustomItemProcessor : InvalidInputException caught when sending Fatal Exception mail. Message : " + e1.getMessage());
		}
		catch (UndeliveredEmailException e1) {
			LOG.error("CustomItemProcessor : UndeliveredEmailException caught when sending Fatal Exception mail. Message : " + e1.getMessage());

		}

		LOG.info("Returning transaction");
		return transaction;

	}

	@Transactional
	public LicenseDetail process(LicenseDetail licenseDetail) {
		
		LOG.info("Processing the record with License Detail Id : " + licenseDetail.getLicenseId() );
		Calendar thisDay = Calendar.getInstance();
		thisDay.add(Calendar.DATE, retryDays);

		if (licenseDetail.getPaymentRetries() <= maxPaymentRetries) {

			try {

				LOG.info("Number of retries for License id : " + licenseDetail.getLicenseId() + " has not exceeded.");

				RetriedTransaction existingTransaction = checkForExistingTransactions(licenseDetail);

				if (existingTransaction != null) {

					LOG.info("Transaction already exists. transaction id : " + existingTransaction.getTransactionId());

					LOG.info("Checking if transaction with id : " + existingTransaction.getTransactionId() + "is settling.");
					// Check if the transaction is settling
					if (paymentGateway.checkTransactionSettling(existingTransaction.getTransactionId())) {

						LOG.info("Transaction is settling. So updating License Detail with new retry time.");
						licenseDetail.setNextRetryTime(new Timestamp(thisDay.getTimeInMillis()));
						licenseDetail.setModifiedOn(now);
						return licenseDetail;
					}

					LOG.info("Checking if transaction with id : " + existingTransaction.getTransactionId() + "is settled.");
					// Check if the transaction has been settled
					if (paymentGateway.checkTransactionSettled(existingTransaction.getTransactionId())) {

						LOG.info("Transaction has been settled to updating LicenseDetail to show that it isnt due and removing record from RetriedTransaction.");
						removeTransactionAndUpdateLicenseDetail(licenseDetail, existingTransaction);
						return licenseDetail;
					}

					// So it isnt settling or settled so. We need to retry charge with existing
					// transaction.
					LOG.info("Transaction is not settling or settled so retrying charge!");
					Transaction transaction = retryChargeAndSendMail(licenseDetail);

					LOG.info("Updating Retried Transaction table with new retry details.");
					existingTransaction.setTransactionId(transaction.getId());
					existingTransaction.setAmount(transaction.getAmount().floatValue());
					existingTransaction.setModifiedOn(now);

					retriedTransactionDao.saveOrUpdate(existingTransaction);

					LOG.info("Updating License Detail entity to reflect changes of new retry.");
					licenseDetail.setPaymentRetries(licenseDetail.getPaymentRetries() + CommonConstants.PAYMENT_INCREMENT);
					licenseDetail.setNextRetryTime(new Timestamp(thisDay.getTimeInMillis()));
					licenseDetail.setModifiedOn(now);

				}
				else {

					LOG.info("Transaction does not exist so retrying the charge.");

					Transaction transaction = null;
					RetriedTransaction retriedTransaction = new RetriedTransaction();

					transaction = retryChargeAndSendMail(licenseDetail);

					LOG.info("Updating Retried Transaction table with the new transaction details.");

					retriedTransaction.setTransactionId(transaction.getId());
					retriedTransaction.setAmount(transaction.getAmount().floatValue());
					retriedTransaction.setLicenseDetail(licenseDetail);
					retriedTransaction.setPaymentToken(transaction.getSubscription().getPaymentMethodToken());
					retriedTransaction.setStatus(CommonConstants.STATUS_ACTIVE);
					retriedTransaction.setCreatedBy(CommonConstants.ADMIN_USER_NAME);
					retriedTransaction.setCreatedOn(now);
					retriedTransaction.setModifiedBy(CommonConstants.ADMIN_USER_NAME);
					retriedTransaction.setModifiedOn(now);

					retriedTransactionDao.saveOrUpdate(retriedTransaction);

					LOG.info("RetriedTransaction table updated.");
					LOG.info("Updating LicenseDetails table with payment retries and the next retry time.");

					licenseDetail.setPaymentRetries(licenseDetail.getPaymentRetries() + CommonConstants.PAYMENT_INCREMENT);
					licenseDetail.setNextRetryTime(new Timestamp(thisDay.getTimeInMillis()));
					licenseDetail.setModifiedOn(now);

				}

			}
			catch (InvalidInputException e) {
				LOG.error("InvalidInputException caught : Message : " + e.getMessage());
				LOG.info("Processing of item : License detail object with id : " + licenseDetail.getLicenseId() + " UNSUCCESSFUL");
				return null;
			}
			catch (NoRecordsFetchedException e) {
				LOG.error("NoRecordsFetchedException caught : Message : " + e.getMessage());
				LOG.info("Processing of item : License detail object with id : " + licenseDetail.getLicenseId() + " UNSUCCESSFUL");
				return null;
			}
			catch (RetryUnsuccessfulException e) {
				LOG.error("RetryUnsuccessfulException caught : Message : " + e.getMessage());
				LOG.info("Processing of item : License detail object with id : " + licenseDetail.getLicenseId() + " UNSUCCESSFUL");
				return null;
			}
			catch (DatabaseException e) {
				LOG.error("Database Exception caught : Message : " + e.getMessage());
				sendFailureMail(e);
				LOG.info("Processing of item : License detail object with id : " + licenseDetail.getLicenseId() + " UNSUCCESSFUL");
				return null;
			}
			catch (UnexpectedException e) {
				LOG.error("UnexpectedException caught : Message : " + e.getMessage());
				sendFailureMail(e);
				LOG.info("Processing of item : License detail object with id : " + licenseDetail.getLicenseId() + " UNSUCCESSFUL");
				return null;
			}

		}
		else {

			// if the number of payment retries have already exceeded the configured number of max
			// retries
			LOG.info("Number of retries for License id : " + licenseDetail.getLicenseId() + " has exceeded.");

			User user = null;
			Company company = licenseDetail.getCompany();

			try {

				LOG.info("Fetching the corporate admin for the company");
				user = getCorporateAdmin(company);

				// Block the user by setting the status of that company to 0
				LOG.info("Blocking the user by changing status in the Company table.");
				company.setStatus(CommonConstants.STATUS_INACTIVE);
				company.setModifiedOn(now);
				companyDao.merge(company);

				LOG.info("Updating LicenseDetails table with payment retries and the next retry time.");
				licenseDetail.setNextRetryTime(new Timestamp(thisDay.getTimeInMillis()));
				licenseDetail.setModifiedOn(now);

				// Now a mail regarding the same is sent to the user
				LOG.info("Sending a mail regarding the blocking of subscription to the user.");
				try {
					emailServices.sendRetryExhaustedEmail(user.getEmailId(), user.getDisplayName());
				}
				catch (InvalidInputException e1) {
					LOG.error("CustomItemProcessor : InvalidInputException caught when sending Fatal Exception mail. Message : " + e1.getMessage());
				}
				catch (UndeliveredEmailException e1) {
					LOG.error("CustomItemProcessor : UndeliveredEmailException caught when sending Fatal Exception mail. Message : "
							+ e1.getMessage());
				}

			}
			catch (InvalidInputException e) {
				LOG.error("InvalidInputException caught : Message : " + e.getMessage());
				LOG.info("Processing of item : License detail object with id : " + licenseDetail.getLicenseId() + " UNSUCCESSFUL");
				return null;
			}
			catch (NoRecordsFetchedException e) {
				LOG.error("NoRecordsFetchedException caught : Message : " + e.getMessage());
				LOG.info("Processing of item : License detail object with id : " + licenseDetail.getLicenseId() + " UNSUCCESSFUL");
				return null;
			}
			catch (DatabaseException e) {
				LOG.error("Database Exception caught : Message : " + e.getMessage());
				sendFailureMail(e);
				LOG.info("Processing of item : License detail object with id : " + licenseDetail.getLicenseId() + " UNSUCCESSFUL");
				return null;
			}

		}

		LOG.info("Successfully processed License detail object with id : " + licenseDetail.getLicenseId());

		return licenseDetail;

	}

}
