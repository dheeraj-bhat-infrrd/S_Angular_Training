package com.realtech.socialsurvey.core.services.integeration.pos.impl;

import java.sql.Timestamp;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.entities.integration.EngagementProcessingStatus;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.integeration.pos.POSIntegration;

/**
 * Implementation file for POS Integration
 */
@Component
public class POSIntegrationImpl implements POSIntegration {

	private static final Logger LOG = LoggerFactory.getLogger(POSIntegrationImpl.class);

	@Resource
	@Qualifier("surveypreinitiation")
	private SurveyPreInitiationDao surveyPreInitiationDao;

	@Transactional
	@Override
	public Timestamp getLastRunTime(String source) throws InvalidInputException {
		LOG.info("Getting last run time for source " + source);
		if (source == null || source.isEmpty()) {
			LOG.debug("Source is not provided to find the last run time.");
			throw new InvalidInputException("Source is not provided to find the last run time.");
		}
		return surveyPreInitiationDao.getLastRunTime(source);
	}
	
	@Transactional
	@Override
	public List<EngagementProcessingStatus> getProcessedRecords(String source, Timestamp lastRunTime) throws InvalidInputException{
		LOG.info("Getting survey pre initiation records after "+lastRunTime);
		List<EngagementProcessingStatus> recordsList = surveyPreInitiationDao.getProcessedIds(source, lastRunTime);
		return recordsList;
	}

}
