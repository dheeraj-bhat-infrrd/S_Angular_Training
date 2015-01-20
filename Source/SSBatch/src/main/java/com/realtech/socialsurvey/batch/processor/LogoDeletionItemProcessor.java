package com.realtech.socialsurvey.batch.processor;
// SS-84 RM03
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;

public class LogoDeletionItemProcessor implements ItemProcessor<String, String>, InitializingBean {

	private List<String> logosInUse = null;

	@Autowired
	private OrganizationUnitSettingsDao settingsDao;

	private static final Logger LOG = LoggerFactory.getLogger(LogoDeletionItemProcessor.class);

	@Override
	public String process(String item) throws Exception {

		LOG.info("Processor called to process item : " + item);
		LOG.info("Size of list : " + logosInUse.size());

		if (logosInUse.contains(item)) {
			//The item is being used so we return null
			LOG.info("Item : " + item + " is currently being used as a logo. So returning null.");
			return null;
		}
		else {
			//item is not being used so we return it for deletion
			LOG.info("Item : " + item + " is currently not being used as a logo. So returning item to writer.");
			return item;
		}
	}

	@Override
	public void afterPropertiesSet() {
		
		//Fetches all the logos being used from the COMPANY_SETTINGS collection in mongodb
		LOG.info("Fetching the list of logos currently in use.");
		logosInUse = settingsDao.fetchLogoList();
		LOG.info("Fetched the list of logos in use from mongo");
	}
}
