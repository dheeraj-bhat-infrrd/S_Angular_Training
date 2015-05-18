package com.realtech.socialsurvey.core.starter;

import java.util.List;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.entities.UsercountModificationNotification;
import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Gets the list of users where number of user have been modified
 *
 */
@Component("updatesubscriptionprice")
public class UpdateSubscriptionPriceStarter extends QuartzJobBean{

	public static final Logger LOG = LoggerFactory.getLogger(UpdateSubscriptionPriceStarter.class);
	
	@Autowired
	private ReviseSubscriptionPrice reviseSubscription;
	
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) {
		LOG.info("ExecutingUpdateSubscriptionPriceStarter ");
		initializeDependencies(jobExecutionContext.getMergedJobDataMap());
		List<UsercountModificationNotification> userModificatonRecords = reviseSubscription.getCompaniesWithUserCountModified();
		if(userModificatonRecords != null && !userModificatonRecords.isEmpty()){
			LOG.debug("Found "+userModificatonRecords.size()+" to process");
			for(UsercountModificationNotification userModificationRecord : userModificatonRecords){
				LOG.debug("Fetching data for user modification record: "+userModificationRecord.getUsercountModificationNotificationId());
				try {
					reviseSubscription.processChargeOnSubscription(userModificationRecord);
				}
				catch (NonFatalException e) {
					LOG.error("Could not process subscription for "+userModificationRecord.getCompany(), e);
				}catch (Exception e) {
					LOG.error("Could not process subscription for "+userModificationRecord.getCompany(), e);
				}
			}
		}else{
			LOG.info("No records to modify subscription price");
		}
		
	}
	
	private void initializeDependencies(JobDataMap jobMap) {
		reviseSubscription = (ReviseSubscriptionPrice) jobMap.get("reviseSubscription");
	}
}
