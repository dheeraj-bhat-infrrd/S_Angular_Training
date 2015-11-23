package com.realtech.socialsurvey.core.services.integeration.pos.impl;

import java.sql.Timestamp;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.integration.Customer;
import com.realtech.socialsurvey.core.entities.integration.Engagement;
import com.realtech.socialsurvey.core.entities.integration.EngagementProcessingStatus;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.integeration.pos.AgentNotAvailableException;
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
		if(source == null || source.isEmpty()){
		    throw new InvalidInputException("passed parameter source is invalid");
		}
		List<EngagementProcessingStatus> recordsList = surveyPreInitiationDao.getProcessedIds(source, lastRunTime);
		return recordsList;
	}
	
	
	@Transactional
	@Override
	public void insertSurveyPreInitiationRecord(Engagement engagement, boolean checkForAgentExistence) throws InvalidInputException, AgentNotAvailableException{
		if(engagement == null){
			LOG.warn("Engagement cannot be null.");
			throw new InvalidInputException("Engagement cannot be null.");
		}
		LOG.info("Inserting engagement record");
		// get the agent email id and check if that email id is present in the repository. If not throw exception
		if(engagement.getAgent() != null){
			if(engagement.getAgent().getAgentEmailId() != null && !engagement.getAgent().getAgentEmailId().isEmpty()){
				LOG.debug("checking if "+engagement.getAgent().getAgentEmailId()+" is present.");
				if(engagement.getCustomers() != null && !engagement.getCustomers().isEmpty()){
					long agentId = -1l;
					if(checkForAgentExistence){
						agentId = getAgentId(engagement.getAgent().getAgentEmailId());
					}
					if(agentId > -1l || !checkForAgentExistence){
						// insert the record in database
						SurveyPreInitiation preInititation = null;
						for(Customer customer : engagement.getCustomers()){
							preInititation = new SurveyPreInitiation();
							preInititation.setSurveySource(engagement.getSource());
							preInititation.setSurveySourceId(engagement.getEngagementId());
							preInititation.setCompanyId(engagement.getCompanyId());
							preInititation.setAgentId(agentId);
							preInititation.setCustomerFirstName(customer.getFirstName());
							preInititation.setCustomerLastName(customer.getLastName());
							if(customer.getFirstName() == null && customer.getName() != null){
								preInititation.setCustomerFirstName(customer.getName());
								preInititation.setCustomerLastName(null);
							}
							preInititation.setCustomerEmailId(customer.getEmailId());
							preInititation.setEngagementClosedTime(new Timestamp(engagement.getEngagementEndTime()));
							preInititation.setReminderCounts(0);
							preInititation.setLastReminderTime(new Timestamp(System.currentTimeMillis()));
							preInititation.setStatus(CommonConstants.STATUS_ACTIVE);
							preInititation.setCreatedOn(new Timestamp(System.currentTimeMillis()));
							preInititation.setModifiedOn(new Timestamp(System.currentTimeMillis()));
							surveyPreInitiationDao.save(preInititation);
						}
					}else{
						throw new AgentNotAvailableException("Agent is not available");
					}
				}else{
					throw new InvalidInputException("Customer details are not present in engagement");
				}
			}else{
				throw new InvalidInputException("Agent email detail is not set in engagement");
			}
		}else{
			throw new InvalidInputException("Agent details is not set in engagement");
		}
		
	}
	
	private long getAgentId(String emailId){
		LOG.debug("checking if "+emailId+" is present in our repository.");
		// TODO: remove
		if(emailId.equals("nishit+none@raremile.com"))
			return -1;
		else
			return 2;
	}

}
