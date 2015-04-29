package com.realtech.socialsurvey.core.starter;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.realtech.socialsurvey.core.entities.UsercountModificationNotification;
import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Gets the list of users where number of user have been modified
 *
 */
public class UpdateSubscriptionPriceStarter {

	public static final Logger LOG = LoggerFactory.getLogger(UpdateSubscriptionPriceStarter.class);
	
	public static void main(String[] args){
		LOG.info("Fetching the comapnies who modified user count");
		LOG.debug("Loading the application context");
		ApplicationContext context = new ClassPathXmlApplicationContext("ss-starter-config.xml");
		ReviseSubscriptionPrice batch = context.getBean(ReviseSubscriptionPrice.class);
		List<UsercountModificationNotification> userModificatonRecords = batch.getCompaniesWithUserCountModified();
		if(userModificatonRecords != null && !userModificatonRecords.isEmpty()){
			LOG.debug("Found "+userModificatonRecords.size()+" to process");
			for(UsercountModificationNotification userModificationRecord : userModificatonRecords){
				LOG.debug("Fetching data for user modification record: "+userModificationRecord.getUsercountModificationNotificationId());
				try {
					batch.processChargeOnSubscription(userModificationRecord);
				}
				catch (NonFatalException e) {
					LOG.error("Could not process subscription for "+userModificationRecord.getCompany());
				}
			}
		}else{
			LOG.info("No records to modify subscription price");
		}
	}
}
