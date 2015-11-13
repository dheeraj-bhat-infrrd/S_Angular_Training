package com.realtech.socialsurvey.batch.processor;

// SS-74 RM03
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.braintreegateway.exceptions.UnexpectedException;
import com.realtech.socialsurvey.batch.commons.BatchCommon;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.CoreCommon;
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
public class UnsubscribeAccountsItemProcessor implements ItemProcessor<DisabledAccount, Map<String, Object>> {
	@Autowired
	private Payment gateway;

	@Autowired
	private GenericDao<Company, Long> companyDao;

	@Autowired
	private GenericDao<DisabledAccount, Long> disabledAccountDao;

	@Autowired
	private GenericDao<User, Long> userDao;

	@Autowired
	private EmailServices emailServices;

	@Autowired
	private BatchCommon commonServices;

	@Autowired
	private CoreCommon coreCommonServices;

	private Map<String, Object> writerObjectsMap = new HashMap<String, Object>();

	private static final Logger LOG = LoggerFactory.getLogger(UnsubscribeAccountsItemProcessor.class);

	@Override
	public Map<String, Object> process(DisabledAccount disabledAccount) {
		LOG.info("Processing the Disabled Account record with id : " + disabledAccount.getId());

		try {
			Company company = disabledAccount.getCompany();

			// Make Api call to cancel the subscription.
			LOG.info("Making api call to Braintree to cancel the subscription.");
			gateway.unsubscribe(disabledAccount.getLicenseDetail().getSubscriptionId());
			LOG.info("Cancellation successful.");

			// Sending mail to the respective user. We fetch the corporate admin for the company
			User user = commonServices.getCorporateAdmin(company);
			LOG.info("Sending mail to the respective corporate admin with email id : " + user.getEmailId());
			emailServices.sendAccountDisabledMail(user.getEmailId(), user.getFirstName() + " " + user.getLastName(), user.getLoginName());
			LOG.info("Email successfully sent!");

			// Updating the company record to reflect changes
			LOG.info("Updating corresponding company entity to reflect changes");
			company.setStatus(CommonConstants.STATUS_INACTIVE);
			company.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			writerObjectsMap.put(CommonConstants.COMPANY_OBJECT_KEY, company);
			LOG.info("Updated company entity");

			// Performing soft delete of the disabled account record
			LOG.info("Updating the disabled account record to perform soft delete.");
			disabledAccount.setStatus(CommonConstants.STATUS_INACTIVE);
			disabledAccount.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			writerObjectsMap.put(CommonConstants.DISABLED_ACCOUNT_OBJECT_KEY, disabledAccount);
			LOG.info("Updated disabled account entity to reflect changes.");
		}
		catch (SubscriptionCancellationUnsuccessfulException e) {
			LOG.error("Subscription cancellation unsuccessful for Disabled Account with id : " + disabledAccount.getId());
			coreCommonServices.sendFailureMail(e);
			LOG.info("Processing of the item with id : " + disabledAccount.getId() + " UNSUCCESSFUL!");
			return null;
		}
		catch (UnexpectedException e) {
			LOG.error("Unexpected Exception caught : Message : " + e.getMessage());
			coreCommonServices.sendFailureMail(e);
			LOG.info("Processing of the item with id : " + disabledAccount.getId() + " UNSUCCESSFUL!");
			return null;
		}
		catch (InvalidInputException | NoRecordsFetchedException | UndeliveredEmailException e) {
			LOG.error("Invalid Input Exception caught : Message : " + e.getMessage(), e);
			LOG.info("Processing of item : License detail object with id : " + disabledAccount.getId() + " UNSUCCESSFUL");
			return null;
		}

		LOG.info("Processing of the Disabled Account record with id : " + disabledAccount.getId() + " Successful.");
		return writerObjectsMap;
	}
}