package com.realtech.socialsurvey.batch.writer;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.RetriedTransaction;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;

public class CustomItemWriter implements ItemWriter<Map<String, Object>> {

	@Autowired
	private GenericDao<LicenseDetail, Integer> licenseDetailDao;

	@Autowired
	private GenericDao<RetriedTransaction, Integer> retriedTransactionDao;

	@Autowired
	private GenericDao<Company, Integer> companyDao;

	@Autowired
	private EmailServices emailServices;

	@Value("${ADMIN_EMAIL_ID}")
	private String recipientMailId;

	private static final Logger LOG = LoggerFactory.getLogger(CustomItemWriter.class);

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

	private void updateSettled(LicenseDetail licenseDetail, RetriedTransaction retriedTransaction) throws InvalidInputException {

		if (licenseDetail == null) {
			LOG.error("Null first parameter given to checkForExistingTransactions!");
			throw new InvalidInputException("Null first parameter given to checkForExistingTransactions!");
		}

		if (retriedTransaction == null) {
			LOG.error("Null second parameter given to checkForExistingTransactions!");
			throw new InvalidInputException("Null second parameter given to checkForExistingTransactions!");
		}

		LOG.debug("Updating License Detail table.");
		licenseDetailDao.update(licenseDetail);
		LOG.debug("Delete record from Retried Transaction");
		retriedTransactionDao.delete(retriedTransaction);
		LOG.debug("Update Successful!");
	}

	private void updateSettling(LicenseDetail licenseDetail) throws InvalidInputException {

		if (licenseDetail == null) {
			LOG.error("Null parameter given to checkForExistingTransactions!");
			throw new InvalidInputException("Null parameter given to checkForExistingTransactions!");
		}

		LOG.debug("Updating License Detail Table.");
		licenseDetailDao.update(licenseDetail);

		LOG.debug("Update Successful!");
	}

	private void generalUpdate(LicenseDetail licenseDetail, RetriedTransaction retriedTransaction) throws InvalidInputException {

		if (licenseDetail == null) {
			LOG.error("Null first parameter given to checkForExistingTransactions!");
			throw new InvalidInputException("Null first parameter given to checkForExistingTransactions!");
		}

		if (retriedTransaction == null) {
			LOG.error("Null second parameter given to checkForExistingTransactions!");
			throw new InvalidInputException("Null second parameter given to checkForExistingTransactions!");
		}

		LOG.debug("Updating Retried Transaction table.");
		retriedTransactionDao.saveOrUpdate(retriedTransaction);
		LOG.debug("Updating License Detail table.");
		licenseDetailDao.update(licenseDetail);
		LOG.debug("Update Successful!");
	}

	private void updateRetriesExceeded(LicenseDetail licenseDetail, Company company) throws InvalidInputException {

		if (licenseDetail == null) {
			LOG.error("Null first parameter given to checkForExistingTransactions!");
			throw new InvalidInputException("Null first parameter given to checkForExistingTransactions!");
		}

		if (company == null) {
			LOG.error("Null second parameter given to checkForExistingTransactions!");
			throw new InvalidInputException("Null second parameter given to checkForExistingTransactions!");
		}

		LOG.debug("Updating License Details table.");
		licenseDetailDao.saveOrUpdate(licenseDetail);
		LOG.debug("Updating company table");
		companyDao.saveOrUpdate(company);
		LOG.debug("Update Successful!");
	}

	@Transactional
	@Override
	public void write(List<? extends Map<String, Object>> items) throws Exception {

		LOG.info("Custom Writer called to write objects.");
		for (Map<String, Object> writerObjectMap : items) {

			try {
				switch ((String) writerObjectMap.get(CommonConstants.CASE_KEY)) {

					case CommonConstants.CASE_SETTLING:
						LOG.info("Case Settling");
						updateSettling((LicenseDetail) writerObjectMap.get(CommonConstants.LICENSE_DETAIL_OBJECT_KEY));
						break;

					case CommonConstants.CASE_SETTLED:
						LOG.info("Case Settled");
						updateSettled((LicenseDetail) writerObjectMap.get(CommonConstants.LICENSE_DETAIL_OBJECT_KEY),
								(RetriedTransaction) writerObjectMap.get(CommonConstants.RETRIED_TRANSACTION_OBJECT_KEY));
						break;

					case CommonConstants.CASE_GENERAL:
						LOG.info("General Case!");
						generalUpdate((LicenseDetail) writerObjectMap.get(CommonConstants.LICENSE_DETAIL_OBJECT_KEY),
								(RetriedTransaction) writerObjectMap.get(CommonConstants.RETRIED_TRANSACTION_OBJECT_KEY));
						break;

					case CommonConstants.CASE_RETRIES_EXCEEDED:
						LOG.info("Case Retries Exceeded!");
						updateRetriesExceeded((LicenseDetail) writerObjectMap.get(CommonConstants.LICENSE_DETAIL_OBJECT_KEY),
								(Company) writerObjectMap.get(CommonConstants.COMPANY_OBJECT_KEY));
						break;

					default:
						throw new InvalidInputException("Invalid case given!");
				}

			}
			catch (DatabaseException e) {
				LOG.error("Database Exception caught : Message : " + e.getMessage());
				sendFailureMail(e);
			}
			catch (InvalidInputException e) {
				LOG.error("InvalidInputException caught : Message : " + e.getMessage());
			}

		}

	}

}
