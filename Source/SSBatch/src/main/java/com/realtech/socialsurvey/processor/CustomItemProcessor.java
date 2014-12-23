package com.realtech.socialsurvey.processor;

import java.sql.Timestamp;
import java.util.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.braintreegateway.Transaction;
import com.braintreegateway.exceptions.UnexpectedException;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.RetriedTransaction;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.payment.Payment;

@Component
public class CustomItemProcessor implements ItemProcessor<LicenseDetail, LicenseDetail> {

	@Autowired
	private GenericDao<RetriedTransaction, Integer> retriedTransactionDao;

	@Autowired
	private GenericDao<LicenseDetail, Integer> licenseDetailDao;

	@Autowired
	Payment paymentGateway;
	
	@Value("${ADMIN_EMAIL_ID}")
	String recipientMailId;
	
	@Autowired
	EmailServices emailServices;

	@Value("${PAYMENT_RETRY_DAYS}")
	private int retryDays;

	private static final Logger LOG = LoggerFactory.getLogger(CustomItemProcessor.class);

	public LicenseDetail process(LicenseDetail licenseDetail) {

		// Setting the calendar to retry days ahead of current time
		Calendar thisDay = Calendar.getInstance();
		thisDay.add(Calendar.DATE, retryDays);

		LOG.info("Processing item : License detail object with id : " + licenseDetail.getLicenseId());
		Timestamp now = new Timestamp(System.currentTimeMillis());
		if (now.after(licenseDetail.getNextRetryTime()) && licenseDetail.getPaymentRetries() <= 3) {

			LOG.info("Retrying the payment and updating tables.");
			RetriedTransaction retriedTransaction = new RetriedTransaction();
			Transaction transaction = null;

			try {
				transaction = paymentGateway.retrySubscriptionCharge(licenseDetail.getSubscriptionId());
			}
			catch (InvalidInputException | NoRecordsFetchedException e) {
				LOG.error("Exception caught while retrying subscription. Message : " + e.getMessage());
				return null;
			}
			catch (FatalException | UnexpectedException e) {
				
				LOG.error("Fatal Exception caught. Message : " + e.getMessage() );
				try {
					emailServices.sendFatalExceptionEmail(recipientMailId, e.getStackTrace().toString());
				}
				catch (InvalidInputException e1) {
					LOG.error("CustomItemProcessor : InvalidInputException : " + e1.getMessage());
				}
				catch (UndeliveredEmailException e1) {
					LOG.error("CustomItemProcessor : UndeliveredEmailException : " + e1.getMessage());

				}
			}

			if (transaction == null) {

				LOG.error("Retry was unsuccessful for subscription Id : " + licenseDetail.getSubscriptionId());
				return null;

			}

			LOG.info("Updating Retried Transaction table with the transaction details.");

			retriedTransaction.setTransactionId(transaction.getId());
			retriedTransaction.setAmount(transaction.getAmount().floatValue());
			retriedTransaction.setLicenseDetail(licenseDetail);
			retriedTransaction.setPaymentToken(transaction.getSubscription().getPaymentMethodToken());
			retriedTransaction.setStatus(CommonConstants.STATUS_ACTIVE);
			retriedTransaction.setCreatedBy(CommonConstants.ADMIN_USER_NAME);
			retriedTransaction.setCreatedOn(now);
			retriedTransaction.setModifiedBy(CommonConstants.ADMIN_USER_NAME);
			retriedTransaction.setModifiedOn(now);

			retriedTransactionDao.save(retriedTransaction);

			LOG.info("RetriedTransaction table updated.");
			LOG.info("Updating LicenseDetails table with payment retries and the next retry time.");

			licenseDetail.setPaymentRetries(licenseDetail.getPaymentRetries() + CommonConstants.PAYMENT_INCREMENT);
			licenseDetail.setNextRetryTime(new Timestamp(thisDay.getTimeInMillis()));
			licenseDetail.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			return licenseDetail;

		}
		else {
			return null;
		}
	}

}
