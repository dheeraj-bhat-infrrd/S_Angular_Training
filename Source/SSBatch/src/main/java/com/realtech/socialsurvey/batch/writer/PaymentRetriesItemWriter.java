package com.realtech.socialsurvey.batch.writer;
//JIRA: SS-61: By RM03

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

/**
 * This is the custom item writer for the paymentRetries job.
 */
public class PaymentRetriesItemWriter implements ItemWriter<Map<String, Object>> {

	@Autowired
	private GenericDao<LicenseDetail, Long> licenseDetailDao;

	@Autowired
	private GenericDao<RetriedTransaction, Long> retriedTransactionDao;

	@Autowired
	private GenericDao<Company, Long> companyDao;

	@Autowired
	private EmailServices emailServices;

	@Value("${ADMIN_EMAIL_ID}")
	private String recipientMailId;

	private static final Logger LOG = LoggerFactory.getLogger(PaymentRetriesItemWriter.class);

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
		
		//For this case we update the License Detail table and delete the record from the Retried Transactions table.
		LOG.info("Updating the database for license detail object with id : " + licenseDetail.getLicenseId() + " and retried transaction object with id : " + retriedTransaction.getRetryId());
		LOG.debug("Updating License Detail table.");
		licenseDetailDao.update(licenseDetail);
		LOG.debug("Delete record from Retried Transaction");
		retriedTransactionDao.update(retriedTransaction);
		LOG.debug("Update Successful!");
	}

	private void updateSettling(LicenseDetail licenseDetail) throws InvalidInputException {

		if (licenseDetail == null) {
			LOG.error("Null parameter given to checkForExistingTransactions!");
			throw new InvalidInputException("Null parameter given to checkForExistingTransactions!");
		}
		
		//For this case we just update the License Details table.
		LOG.debug("Updating License Detail object with id : " + licenseDetail.getLicenseId() );
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
		
		//For this case we need to update both the License Details and the Retried Transactions table.
		LOG.info("Updating the database for license detail object with id : " + licenseDetail.getLicenseId() + " and retried transaction object with id : " + retriedTransaction.getRetryId());
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
		
		//For this case we update the License Details and the Company table.
		LOG.info("Updating the database for license detail object with id : " + licenseDetail.getLicenseId() + " and company object with id : " + company.getCompanyId());
		LOG.debug("Updating License Details table.");
		licenseDetailDao.update(licenseDetail);
		LOG.debug("Updating company table");
		companyDao.merge(company);
		LOG.debug("Update Successful!");
	}

	@Transactional
	@Override
	public void write(List<? extends Map<String, Object>> items) throws Exception {
				
		//Iterate through all the map objects check the case and call appropriate write methods.
		LOG.info("Payment Retries Writer called to write objects.");
		for (Map<String, Object> writerObjectMap : items) {

			try {
				
				//Check if the case key exists
				if(writerObjectMap.get(CommonConstants.CASE_KEY) == null){
					LOG.error("Case Key is empty or null. PLease check if case is passed!");
					throw new InvalidInputException("Case Key is empty or null. PLease check if case is passed!");
				}
				
				switch ((String) writerObjectMap.get(CommonConstants.CASE_KEY)) {

					case CommonConstants.CASE_SETTLING:
						LOG.info("Case Settling");
						//Check if license detail object is passed.
						if(writerObjectMap.get(CommonConstants.LICENSE_DETAIL_OBJECT_KEY) == null){
							LOG.error("Required License Detail object has not been passed in the map. Please check.");
							throw new InvalidInputException("Required License Detail object has not been passed in the map. Please check.");
						}
						//Updating the database for the case settling.
						updateSettling((LicenseDetail)writerObjectMap.get(CommonConstants.LICENSE_DETAIL_OBJECT_KEY));
						break;

					case CommonConstants.CASE_SETTLED:
						LOG.info("Case Settled");
						//Check if License Detail object and Retried Transaction objects have been passed in the map.
						if(writerObjectMap.get(CommonConstants.LICENSE_DETAIL_OBJECT_KEY) == null | writerObjectMap.get(CommonConstants.RETRIED_TRANSACTION_OBJECT_KEY) == null ){
							LOG.error("Required objects have not been passed in the map. Please check.");
							throw new InvalidInputException("Required objects have not been passed in the map. Please check.");
						}
						//Updating the database for case settled.
						updateSettled((LicenseDetail) writerObjectMap.get(CommonConstants.LICENSE_DETAIL_OBJECT_KEY),(RetriedTransaction) writerObjectMap.get(CommonConstants.RETRIED_TRANSACTION_OBJECT_KEY));
						break;

					case CommonConstants.CASE_GENERAL:
						LOG.info("General Case!");
						//Check if License Detail object and Retried Transaction objects have been passed in the map.
						if(writerObjectMap.get(CommonConstants.LICENSE_DETAIL_OBJECT_KEY) == null | writerObjectMap.get(CommonConstants.RETRIED_TRANSACTION_OBJECT_KEY) == null ){
							LOG.error("Required objects have not been passed in the map. Please check.");
							throw new InvalidInputException("Required objects have not been passed in the map. Please check.");
						}
						//Updating the database for the case general.
						generalUpdate((LicenseDetail) writerObjectMap.get(CommonConstants.LICENSE_DETAIL_OBJECT_KEY),(RetriedTransaction) writerObjectMap.get(CommonConstants.RETRIED_TRANSACTION_OBJECT_KEY));
						break;

					case CommonConstants.CASE_RETRIES_EXCEEDED:
						LOG.info("Case Retries Exceeded!");
						//Check if License Detail object and Company objects have been passed in the map.
						if(writerObjectMap.get(CommonConstants.LICENSE_DETAIL_OBJECT_KEY) == null | writerObjectMap.get(CommonConstants.COMPANY_OBJECT_KEY) == null ){
							LOG.error("Required objects have not been passed in the map. Please check.");
							throw new InvalidInputException("Required objects have not been passed in the map. Please check.");
						}
						//Updating the database for retries exceeded.
						updateRetriesExceeded((LicenseDetail) writerObjectMap.get(CommonConstants.LICENSE_DETAIL_OBJECT_KEY),(Company) writerObjectMap.get(CommonConstants.COMPANY_OBJECT_KEY));
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
		LOG.info("All objects written by Payment Retries Writer.");
	}

}
