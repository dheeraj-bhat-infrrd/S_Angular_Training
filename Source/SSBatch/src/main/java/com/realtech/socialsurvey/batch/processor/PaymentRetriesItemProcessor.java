package com.realtech.socialsurvey.batch.processor;

// JIRA: SS-61: By RM03

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.braintreegateway.Transaction;
import com.braintreegateway.exceptions.UnexpectedException;
import com.realtech.socialsurvey.batch.commons.BatchCommon;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.CoreCommon;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.RetriedTransaction;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentRetryUnsuccessfulException;

/**
 * This is the custom item processor class that processes the LicenseDetail table.
 */
@Component
public class PaymentRetriesItemProcessor implements ItemProcessor<LicenseDetail, Map<String, Object>> {

	private Timestamp now = new Timestamp(System.currentTimeMillis());

	@Autowired
	private GenericDao<RetriedTransaction, Long> retriedTransactionDao;

	@Autowired
	private GenericDao<User, Long> userDao;

	@Autowired
	private Payment paymentGateway;

	@Value("${MAX_PAYMENT_RETRIES}")
	private int maxPaymentRetries;

	@Autowired
	private EmailServices emailServices;

	@Value("${PAYMENT_RETRY_DAYS}")
	private int retryDays;
	
	@Autowired
	private BatchCommon commonServices;
	
	@Autowired
	private CoreCommon coreCommonServices;
	
	private Map<String, Object> writerObjectsMap;

	private static final Logger LOG = LoggerFactory.getLogger(PaymentRetriesItemProcessor.class);


	private RetriedTransaction checkForExistingTransactions(LicenseDetail licenseDetail) throws InvalidInputException {

		if (licenseDetail == null) {
			LOG.error("Null parameter given to checkForExistingTransactions!");
			throw new InvalidInputException("Null parameter given to checkForExistingTransactions!");
		}

		LOG.debug("Checking for existing transactions for the License Id : " + licenseDetail.getLicenseId());
		RetriedTransaction existingTransaction = null;

		// find records in RETRIED_TRANSACTIONS table with the same license id or in hibernate sense
		// the object.
		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.LICENSE_DETAIL_COLUMN, licenseDetail);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);

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

	private void updateLicenseDetail(LicenseDetail licenseDetail) throws InvalidInputException {

		if (licenseDetail == null) {
			LOG.error("Parameter given to removeTransactionAndUpdateLicenseDetail is null!");
			throw new InvalidInputException("Parameter given to removeTransactionAndUpdateLicenseDetail is null!");
		}

		LOG.debug("Updating License detail entity with id : " + licenseDetail.getLicenseId() + " to show that it isnt due on subscription");
		// Update the License Detail object to show that it isnt due subscription.
		licenseDetail.setNextRetryTime(new Timestamp((CommonConstants.EPOCH_TIME_IN_MILLIS)));
		licenseDetail.setPaymentRetries(CommonConstants.INITIAL_PAYMENT_RETRIES);
		licenseDetail.setIsSubscriptionDue(CommonConstants.SUBSCRIPTION_NOT_DUE);
		licenseDetail.setModifiedOn(now);

		LOG.debug("All changes for License Id : " + licenseDetail.getLicenseId() + " made successfully");

	}

	private Transaction retryChargeAndSendMail(LicenseDetail licenseDetail) throws InvalidInputException, PaymentRetryUnsuccessfulException,
			NoRecordsFetchedException {

		if (licenseDetail == null) {
			LOG.error("Parameter given to removeTransactionAndUpdateLicenseDetail is null!");
			throw new InvalidInputException("Parameter given to removeTransactionAndUpdateLicenseDetail is null!");
		}

		LOG.debug("Retrying charge for subscription id : " + licenseDetail.getSubscriptionId());
		Transaction transaction = null;

		// Retrying the subscription charge.
		transaction = paymentGateway.retrySubscriptionCharge(licenseDetail.getSubscriptionId());

		LOG.debug("Retry successful. Sending email.");

		// Sending retry email to client.
		LOG.debug("Fetching the corporate admin");
		User user = commonServices.getCorporateAdmin(licenseDetail.getCompany());
		LOG.debug("Sending mail for retrying subscription charge.");

		try {
			emailServices.sendRetryChargeEmail(user.getEmailId(), user.getFirstName() + " " + user.getLastName(), user.getLoginName());
		}
		catch (InvalidInputException e1) {
			LOG.error("CustomItemProcessor : Exception caught when sending retry charge mail. Message : " + e1.getMessage());

			coreCommonServices.sendEmailSendingFailureMail(user.getEmailId(), user.getFirstName()+" "+user.getLastName(), e1);
		}
		catch (UndeliveredEmailException e) {
			LOG.error("CustomItemProcessor : Exception caught when sending retry charge mail. Message : " + e.getMessage());

			coreCommonServices.sendEmailSendingFailureMail(user.getEmailId(), user.getFirstName()+" "+user.getLastName(), e);
		}

		LOG.info("Returning transaction");
		return transaction;

	}

	@Transactional
	public Map<String, Object> process(LicenseDetail licenseDetail) {

		LOG.info("Processing the record with License Detail Id : " + licenseDetail.getLicenseId());
		Calendar thisDay = Calendar.getInstance();
		thisDay.add(Calendar.DATE, retryDays);
		writerObjectsMap = new HashMap<String, Object>();

		if (licenseDetail.getPaymentRetries() <= maxPaymentRetries) {

			try {

				LOG.info("Number of retries for License id : " + licenseDetail.getLicenseId() + " has not exceeded.");

				RetriedTransaction existingTransaction = checkForExistingTransactions(licenseDetail);

				if (existingTransaction != null) {

					LOG.info("Transaction already exists. transaction id : " + existingTransaction.getTransactionId());

					LOG.info("Checking if transaction with id : " + existingTransaction.getTransactionId() + "is settling.");
					// Check if the transaction is settling
					if (paymentGateway.checkTransactionSettling(existingTransaction.getTransactionId())) {

						// The transaction is settling. So we just update the next retry time of the
						// License Detail object.
						LOG.info("Transaction is settling. So updating License Detail with new retry time.");
						licenseDetail.setNextRetryTime(new Timestamp(thisDay.getTimeInMillis()));
						licenseDetail.setModifiedOn(now);
						writerObjectsMap.put(CommonConstants.CASE_KEY, CommonConstants.CASE_SETTLING);
						writerObjectsMap.put(CommonConstants.LICENSE_DETAIL_OBJECT_KEY, licenseDetail);
					}
					// Check if the transaction has been settled
					else if (paymentGateway.checkTransactionSettled(existingTransaction.getTransactionId())) {

						// The transaction is settled to we delete the record from the Retried
						// Tranasction table and update License Detail object.
						LOG.info("Transaction has been settled to updating LicenseDetail to show that it isnt due and removing record from RetriedTransaction.");
						updateLicenseDetail(licenseDetail);
						existingTransaction.setStatus(CommonConstants.STATUS_INACTIVE);
						writerObjectsMap.put(CommonConstants.CASE_KEY, CommonConstants.CASE_SETTLED);
						writerObjectsMap.put(CommonConstants.LICENSE_DETAIL_OBJECT_KEY, licenseDetail);
						writerObjectsMap.put(CommonConstants.RETRIED_TRANSACTION_OBJECT_KEY, existingTransaction);

					}
					else {
						// So it isnt settling or settled so. We need to retry charge with existing
						// transaction.
						LOG.info("Transaction is not settling or settled so retrying charge!");
						Transaction transaction = retryChargeAndSendMail(licenseDetail);

						LOG.info("Updating Retried Transaction record with new retry details.");
						existingTransaction.setTransactionId(transaction.getId());
						existingTransaction.setAmount(transaction.getAmount().floatValue());
						existingTransaction.setModifiedOn(now);

						LOG.info("Updating License Detail entity to reflect changes of new retry.");
						licenseDetail.setPaymentRetries(licenseDetail.getPaymentRetries() + CommonConstants.PAYMENT_INCREMENT);
						licenseDetail.setNextRetryTime(new Timestamp(thisDay.getTimeInMillis()));
						licenseDetail.setModifiedOn(now);

						writerObjectsMap.put(CommonConstants.CASE_KEY, CommonConstants.CASE_GENERAL);
						writerObjectsMap.put(CommonConstants.LICENSE_DETAIL_OBJECT_KEY, licenseDetail);
						writerObjectsMap.put(CommonConstants.RETRIED_TRANSACTION_OBJECT_KEY, existingTransaction);
					}
				}
				else {

					LOG.info("Transaction does not exist so retrying the charge.");
					// There isnt a transaction existing. So we retry.
					Transaction transaction = null;
					RetriedTransaction retriedTransaction = new RetriedTransaction();

					transaction = retryChargeAndSendMail(licenseDetail);

					LOG.info("Updating Retried Transaction record with the new transaction details.");

					retriedTransaction.setTransactionId(transaction.getId());
					retriedTransaction.setAmount(transaction.getAmount().floatValue());
					retriedTransaction.setLicenseDetail(licenseDetail);
					retriedTransaction.setPaymentToken(transaction.getSubscription().getPaymentMethodToken());
					retriedTransaction.setStatus(CommonConstants.STATUS_ACTIVE);
					retriedTransaction.setCreatedBy(CommonConstants.ADMIN_USER_NAME);
					retriedTransaction.setCreatedOn(now);
					retriedTransaction.setModifiedBy(CommonConstants.ADMIN_USER_NAME);
					retriedTransaction.setModifiedOn(now);

					LOG.info("Updating LicenseDetails entity with payment retries and the next retry time.");

					licenseDetail.setPaymentRetries(licenseDetail.getPaymentRetries() + CommonConstants.PAYMENT_INCREMENT);
					licenseDetail.setNextRetryTime(new Timestamp(thisDay.getTimeInMillis()));
					licenseDetail.setModifiedOn(now);

					writerObjectsMap.put(CommonConstants.CASE_KEY, CommonConstants.CASE_GENERAL);
					writerObjectsMap.put(CommonConstants.LICENSE_DETAIL_OBJECT_KEY, licenseDetail);
					writerObjectsMap.put(CommonConstants.RETRIED_TRANSACTION_OBJECT_KEY, retriedTransaction);

				}

			}
			catch (InvalidInputException | NoRecordsFetchedException | PaymentRetryUnsuccessfulException e) {
				LOG.error("Exception caught : Message : " + e.getMessage(), e);
				LOG.info("Processing of item : License detail object with id : " + licenseDetail.getLicenseId() + " UNSUCCESSFUL");
				return null;
			}
			catch (UnexpectedException e) {
				LOG.error("UnexpectedException caught : Message : " + e.getMessage(),e);
				coreCommonServices.sendFailureMail(e);
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

				// Fetch the admin of the company.
				LOG.info("Fetching the corporate admin for the company");
				user = commonServices.getCorporateAdmin(company);

				// Block the user by setting the status of that company to 0
				LOG.info("Blocking the user by changing status in the Company entity");
				company.setStatus(CommonConstants.STATUS_INACTIVE);
				company.setModifiedOn(now);

				LOG.info("Updating LicenseDetails record with payment retries and the next retry time.");
				licenseDetail.setNextRetryTime(new Timestamp(thisDay.getTimeInMillis()));
				licenseDetail.setModifiedOn(now);

				// Now a mail regarding the same is sent to the user
				LOG.info("Sending a mail regarding the blocking of subscription to the user.");
				try {
					emailServices.sendRetryExhaustedEmail(user.getEmailId(), user.getFirstName() + " " + user.getLastName(), user.getLoginName());
					LOG.info("Mail successfully sent.");
				}
				catch (InvalidInputException e1) {
					LOG.error("Exception caught when sending Fatal Exception mail. Message : " + e1.getMessage(),e1);
					coreCommonServices.sendEmailSendingFailureMail(user.getEmailId(), user.getFirstName()+" "+user.getLastName(), e1);
				}
				catch (UndeliveredEmailException e) {
					LOG.error("Exception caught when sending Fatal Exception mail. Message : " + e.getMessage(),e);
					coreCommonServices.sendEmailSendingFailureMail(user.getEmailId(), user.getFirstName()+" "+user.getLastName(), e);
				}

				LOG.info("Mail sent. Preparing map to be sent to the writer.");
				writerObjectsMap.put(CommonConstants.CASE_KEY, CommonConstants.CASE_RETRIES_EXCEEDED);
				writerObjectsMap.put(CommonConstants.LICENSE_DETAIL_OBJECT_KEY, licenseDetail);
				writerObjectsMap.put(CommonConstants.COMPANY_OBJECT_KEY, company);

			}
			catch (InvalidInputException | NoRecordsFetchedException e) {
				LOG.error("Exception caught : Message : " + e.getMessage(), e);
				LOG.info("Processing of item : License detail object with id : " + licenseDetail.getLicenseId() + " UNSUCCESSFUL");
				return null;
			}
		}

		LOG.info("Successfully processed License detail object with id : " + licenseDetail.getLicenseId());

		return writerObjectsMap;

	}
}
