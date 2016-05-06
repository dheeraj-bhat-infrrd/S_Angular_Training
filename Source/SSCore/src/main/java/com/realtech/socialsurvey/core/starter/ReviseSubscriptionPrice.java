package com.realtech.socialsurvey.core.starter;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.UsercountModificationNotificationDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UsercountModificationNotification;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
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
	private GenericDao<UserProfile, Long> userProfileDao;

	@Autowired
	private Payment payment;

	@Autowired
	private UsercountModificationNotificationDao userCountModificationDao;
	
	@Autowired
	private EmailServices emailServices;

	@Autowired
	private BatchTrackerService batchTrackerService;

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
		Map<String, Object> subscriptionResult = calculateSubscriptionAmountAndCharge(userModificationRecord.getCompany());
		boolean subscriptionStatus = (Boolean)subscriptionResult.get(CommonConstants.SUBSCRIPTION_PRICE_CHANGED);
		if(subscriptionStatus){
			LOG.debug("Sending subscription price revision mail");
			sendNotificationMail(userModificationRecord.getCompany(), subscriptionResult);
			LOG.debug("Subscription price revision mail sent");
		}
		deleteUserCountNotificationTable(userModificationRecord);
	}

	/**
	 * Calculates the subscription amount for a company id and number of users. The amount is based
	 * on the account type for the company. After calculating the batch will charge the account
	 *
	 * @param company
	 * @return
	 * @throws NonFatalException
     */
	@Transactional
	public Map<String, Object> calculateSubscriptionAmountAndCharge(Company company) throws NonFatalException {
		Map<String, Object> paymentResult = null;
		if (company != null) {
			LOG.info("Finding the amount to be charged for company id: " + company.getCompany());
			// get the subscription id, license type
			paymentResult = payment.updateSubscriptionPriceBasedOnUsersCount(company);
		}
		return paymentResult;
	}

	/**
	 * Deletes the record from notification table.
	 *
	 * @param userCountModificationCount
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
	
	@Transactional
	private void sendNotificationMail(Company company, Map<String, Object> values){
		LOG.debug("Sending mail to company "+company.getCompany()+" for charge");
		// get the mail id for the company
		Map<String, Object> queryMap = new HashMap<>();
		ProfilesMaster profilesMaster = new ProfilesMaster();
		profilesMaster.setProfileId(CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID);
		queryMap.put(CommonConstants.PROFILE_MASTER_COLUMN, profilesMaster);
		queryMap.put(CommonConstants.COMPANY_COLUMN, company);
		List<UserProfile> userProfiles = userProfileDao.findByKeyValue(UserProfile.class, queryMap);
		if(userProfiles != null && !userProfiles.isEmpty()){
			LOG.debug("Found the company admin profile for company "+company.getCompany());
			String emailId = userProfiles.get(CommonConstants.INITIAL_INDEX).getEmailId();
			String name = userProfiles.get(CommonConstants.INITIAL_INDEX).getUser().getFirstName();
			LOG.debug("Company admin email id for company "+company.getCompany()+" is "+emailId);
			String oldAmount = String.valueOf(values.get(CommonConstants.SUBSCRIPTION_OLD_PRICE));
			String revisedAmount = String.valueOf(values.get(CommonConstants.SUBSCRIPTION_REVISED_PRICE));
			String numOfUsers = String.valueOf(values.get(CommonConstants.SUBSCRIPTION_REVISED_NUMOFUSERS));
			try {
				emailServices.sendSubscriptionRevisionMail(emailId, name, oldAmount, revisedAmount, numOfUsers);
			}
			catch (InvalidInputException | UndeliveredEmailException e) {
				LOG.error("Could not send mail to company "+company.getCompany(), e);
			}
		}
		
	}

	@Transactional
	public void updateSubscriptionPriceStarter() {
		try {
			//update last run start time
			batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
				CommonConstants.BATCH_TYPE_UPDATE_SUBSCRIPTION_PRICE_STARTER,
				CommonConstants.BATCH_NAME_UPDATE_SUBSCRIPTION_PRICE_STARTER );

			List<UsercountModificationNotification> userModificatonRecords = getCompaniesWithUserCountModified();
			if ( userModificatonRecords != null && !userModificatonRecords.isEmpty() ) {
				LOG.debug( "Found " + userModificatonRecords.size() + " to process" );
				for ( UsercountModificationNotification userModificationRecord : userModificatonRecords ) {
					LOG.debug( "Fetching data for user modification record: "
						+ userModificationRecord.getUsercountModificationNotificationId() );
					try {
						processChargeOnSubscription( userModificationRecord );
					} catch ( NonFatalException e ) {
						LOG.error( "Could not process subscription for " + userModificationRecord.getCompany(), e );
					} catch ( Exception e ) {
						LOG.error( "Could not process subscription for " + userModificationRecord.getCompany(), e );
					}
				}
			} else {
				LOG.info( "No records to modify subscription price" );
			}

			//Update last build time in batch tracker table
			batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_UPDATE_SUBSCRIPTION_PRICE_STARTER );
		} catch ( Exception e ) {
			LOG.error( "Error in UpdateSubscriptionPriceStarter", e );
			try {
				//update batch tracker with error message
				batchTrackerService.updateErrorForBatchTrackerByBatchType(
					CommonConstants.BATCH_TYPE_UPDATE_SUBSCRIPTION_PRICE_STARTER, e.getMessage() );
				//send report bug mail to admin
				batchTrackerService.sendMailToAdminRegardingBatchError(
					CommonConstants.BATCH_NAME_UPDATE_SUBSCRIPTION_PRICE_STARTER, System.currentTimeMillis(), e );
			} catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
				LOG.error( "Error while updating error message in UpdateSubscriptionPriceStarter " );
			} catch ( UndeliveredEmailException e1 ) {
				LOG.error( "Error while sending report excption mail to admin " );
			}
		}
	}

}
