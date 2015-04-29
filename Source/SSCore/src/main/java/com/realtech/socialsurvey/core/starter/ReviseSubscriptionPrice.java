package com.realtech.socialsurvey.core.starter;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.UsercountModificationNotificationDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.UsercountModificationNotification;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.payment.Payment;

/**
 * Revises subscription price based on the user
 */
@Component
public class ReviseSubscriptionPrice {

	public static final Logger LOG = LoggerFactory.getLogger(ReviseSubscriptionPrice.class);

	@Autowired
	private GenericDao<Company, Long> companyDao;

	@Autowired
	private Payment payment;

	@Autowired
	private UsercountModificationNotificationDao userCountModificationDao;

	@Transactional
	public List<UsercountModificationNotification> getCompaniesWithUserCountModified() {
		LOG.info("Getting the list of companies whose subscription needs to be modified");
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		List<UsercountModificationNotification> userModificationRecords = userCountModificationDao.findByKeyValue(
				UsercountModificationNotification.class, queryMap);
		return userModificationRecords;
	}

	@Transactional(rollbackFor = NonFatalException.class)
	public void processChargeOnSubscription(UsercountModificationNotification userModificationRecord) throws NonFatalException {
		LOG.debug("processChargeOnSubscription for user count modification id: " + userModificationRecord.getUsercountModificationNotificationId());
		// update the user count modification record status to under processing.
		userModificationRecord.setStatus(CommonConstants.STATUS_UNDER_PROCESSING);
		userModificationRecord.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		userCountModificationDao.update(userModificationRecord);
		LOG.debug("User count modification set to under processing");
		if(calculateSubscriptionAmountAndCharge(userModificationRecord.getCompany())){
			// TODO: send mail
		}
		deleteUserCountNotificationTable(userModificationRecord);
	}

	/**
	 * Calculates the subscription amount for a company id and number of users. The amount is based
	 * on the account type for the company. After calculating the batch will charge the account
	 * 
	 * @param companyId
	 * @param numOfUsers
	 * @throws NonFatalException
	 */
	@Transactional
	public boolean calculateSubscriptionAmountAndCharge(Company company) throws NonFatalException {
		boolean suscriptionCharged = false;
		if (company != null) {
			LOG.info("Finding the amount to be charged for company id: " + company.getCompany());
			// get the subscription id, license type
			suscriptionCharged = payment.updateSubscriptionPriceBasedOnUsersCount(company);
		}
		return suscriptionCharged;
	}

	/**
	 * Deletes the record from notification table.
	 * 
	 * @param company
	 */
	@Transactional
	public void deleteUserCountNotificationTable(UsercountModificationNotification userCountModificationCount) {
		LOG.info("Deleting the record for user count modification: " + userCountModificationCount.getUsercountModificationNotificationId());
		// check for the status if its under processing. It is possible that webapp might have set
		// the status back to active if user count has been modified while charging process was
		// under way
		// TODO: check status before deleting. Deleting directly for now
		userCountModificationDao.deleteByIdAndStatus(userCountModificationCount, CommonConstants.STATUS_UNDER_PROCESSING);
		LOG.info("Deleted the user count modification record.");
		
	}

}
