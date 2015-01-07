package com.realtech.socialsurvey.batch.writer;
//SS-74 RM03

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
import com.realtech.socialsurvey.core.entities.DisabledAccount;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;

/**
 * The writer class for the batch job for cancelling subscriptions
 */
public class UnsubscribeAccountsItemWriter implements ItemWriter<Map<String, Object>> {

	@Autowired
	private GenericDao<Company, Long> companyDao;

	@Autowired
	private GenericDao<DisabledAccount, Long> disabledAccountDao;

	@Autowired
	private EmailServices emailServices;

	@Value("${ADMIN_EMAIL_ID}")
	private String recipientMailId;

	private static final Logger LOG = LoggerFactory.getLogger(UnsubscribeAccountsItemWriter.class);

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

	@Transactional
	@Override
	public void write(List<? extends Map<String, Object>> items) throws Exception {

		Company company = null;
		DisabledAccount disabledAccount = null;

		// Iterate through all the map objects and write them into the database
		LOG.info("Unsubscribe Accounts Writer called to write objects");
		for (Map<String, Object> writerMap : items) {

			try {

				if (writerMap.get(CommonConstants.COMPANY_OBJECT_KEY) == null || writerMap.get(CommonConstants.DISABLED_ACCOUNT_OBJECT_KEY) == null) {
					LOG.error("Required objects have not been passed in the map. Please check.");
					throw new InvalidInputException("Required objects have not been passed in the map. Please check.");
				}

				LOG.info("Extracting the objects from the map");
				company = (Company) writerMap.get(CommonConstants.COMPANY_OBJECT_KEY);
				disabledAccount = (DisabledAccount) writerMap.get(CommonConstants.DISABLED_ACCOUNT_OBJECT_KEY);

				LOG.info("Updating objects for disabled account record with id : " + disabledAccount.getId());
				LOG.info("Updating the company object in the database");
				companyDao.merge(company);
				LOG.info("Company object successfully updated.");

				LOG.info("Updating the disabled account object in the database");
				disabledAccountDao.update(disabledAccount);
				LOG.info("Disabled account object successfully updated.");

				LOG.info("Objects for disabled account record with id : " + disabledAccount.getId() + "successfully updated.");

			}
			catch (InvalidInputException e) {
				LOG.error("InvalidInputException caught : Message : " + e.getMessage());
				LOG.info("Writing for disabled account with id : " + disabledAccount.getId() + " UNSUCCESSFUL!");
			}
			catch (DatabaseException e) {
				LOG.error("Database Exception caught : Message : " + e.getMessage());
				sendFailureMail(e);
				LOG.info("Writing for disabled account with id : " + disabledAccount.getId() + " UNSUCCESSFUL!");
			}

		}
	}
}
