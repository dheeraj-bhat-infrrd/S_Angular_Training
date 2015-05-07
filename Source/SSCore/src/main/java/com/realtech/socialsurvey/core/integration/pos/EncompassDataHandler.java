package com.realtech.socialsurvey.core.integration.pos;

import java.sql.Timestamp;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.integration.Engagement;
import com.realtech.socialsurvey.core.entities.integration.EngagementProcessingStatus;
import com.realtech.socialsurvey.core.entities.integration.EngagementWrapper;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.integeration.pos.AgentNotAvailableException;
import com.realtech.socialsurvey.core.services.integeration.pos.POSIntegration;

/**
 * Handles the encompass data
 */
@Component
public class EncompassDataHandler {

	private static final Logger LOG = LoggerFactory.getLogger(EncompassDataHandler.class);

	private static final int NUM_OF_RECORDS = 50; // default number of records

	@Autowired
	private IntergrationApiBuilder builder;

	@Autowired
	private POSIntegration posIntegration;

	private Timestamp lastProcessedTime;

	/**
	 * Gets closed engagement
	 * 
	 * @throws InvalidInputException
	 */
	public void fetchClosedEngagments() throws InvalidInputException {
		LOG.info("Fetching closed engagements");
		preProcess();
		long startRec = 0;
		EncompassIntegrationAPI ecompassIntegrationApi = builder.getEncompassApiHandler();
		EngagementWrapper encompassResultWrapper = null;
		try {
			do {
				if (startRec >= 0) {
					encompassResultWrapper = getClosedRecords(ecompassIntegrationApi, startRec);
					processEncompassEngagements(encompassResultWrapper.getEngagements());
					startRec = encompassResultWrapper.getNextRecord();
				}
				else {
					break;
				}
			}
			while (true);
		}
		catch (FatalException fe) {
			LOG.error("Fatal exception. Ending abruptly.", fe);
		}
		postProcess(ecompassIntegrationApi);
		LOG.info("Done fetching closed engagements");
	}

	// makes the api call to get the records
	private EngagementWrapper getClosedRecords(EncompassIntegrationAPI ecompassIntegrationApi, long startRec) {
		LOG.debug("Getting closed records");
		EngagementWrapper wrapper = ecompassIntegrationApi.getClosedEngagements(startRec, NUM_OF_RECORDS);
		return wrapper;
	}

	private void processEncompassEngagements(List<Engagement> engagements) {
		LOG.debug("Inserting engagements into the pre intitation database");
		if (engagements != null && engagements.size() > 0) {
			for (Engagement engagement : engagements) {
				try {
					posIntegration.insertSurveyPreInitiationRecord(engagement, true);
				}
				catch (AgentNotAvailableException e) {
					LOG.warn("Skipping agent " + engagement.getAgent().getAgentEmailId());
				}
				catch (InvalidInputException | FatalException e) {
					LOG.error("Error while inserting pre initiation record.", e);
				}
			}
		}
	}

	private void preProcess() throws InvalidInputException {
		LOG.debug("Fetching the last run time");
		lastProcessedTime = posIntegration.getLastRunTime(CommonConstants.CRM_INFO_SOURCE_ENCOMPASS);
	}

	private void postProcess(EncompassIntegrationAPI ecompassIntegrationApi) throws InvalidInputException {
		LOG.debug("Getting the records inserted after the last run time");
		List<EngagementProcessingStatus> processedRecords = posIntegration.getProcessedRecords(CommonConstants.CRM_INFO_SOURCE_ENCOMPASS,
				lastProcessedTime);
		ecompassIntegrationApi.updateProcessingStatus(processedRecords);
	}
}
