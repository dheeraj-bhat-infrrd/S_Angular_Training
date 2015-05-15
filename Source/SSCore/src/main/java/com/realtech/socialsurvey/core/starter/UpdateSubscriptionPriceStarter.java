package com.realtech.socialsurvey.core.starter;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.entities.UsercountModificationNotification;
import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Gets the list of users where number of user have been modified
 *
 */
@Component("updatesubscriptionprice")
public class UpdateSubscriptionPriceStarter {

	public static final Logger LOG = LoggerFactory.getLogger(UpdateSubscriptionPriceStarter.class);
	
	@Autowired
	private ReviseSubscriptionPrice batch;
	
	public void execute(){
		LOG.info("ExecutingUpdateSubscriptionPriceStarter ");
		List<UsercountModificationNotification> userModificatonRecords = batch.getCompaniesWithUserCountModified();
		if(userModificatonRecords != null && !userModificatonRecords.isEmpty()){
			LOG.debug("Found "+userModificatonRecords.size()+" to process");
			for(UsercountModificationNotification userModificationRecord : userModificatonRecords){
				LOG.debug("Fetching data for user modification record: "+userModificationRecord.getUsercountModificationNotificationId());
				try {
					batch.processChargeOnSubscription(userModificationRecord);
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
}
