package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.integration.EngagementProcessingStatus;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

@Component("surveypreinitiation")
public class SurveyPreInitiationDaoImpl extends GenericDaoImpl<SurveyPreInitiation, Long> implements SurveyPreInitiationDao{

	private static final Logger LOG = LoggerFactory.getLogger(SurveyPreInitiationDaoImpl.class);
	
	@Override
	public Timestamp getLastRunTime(String source) throws InvalidInputException {
		LOG.info("Get the max created time for source "+source);
		if(source == null || source.isEmpty()){
			LOG.debug("Source is not provided.");
			throw new InvalidInputException("Souce is not provided.");
		}
		Timestamp lastRunTime = null;
		Criteria criteria = getSession().createCriteria(SurveyPreInitiation.class);
		try{
			criteria.add(Restrictions.eq(CommonConstants.SURVEY_SOURCE_KEY_COLUMN, source));
			criteria.setProjection(Projections.max(CommonConstants.CREATED_ON));
			Object result = criteria.uniqueResult();
			if(result instanceof Timestamp){
				lastRunTime = (Timestamp)result;
			}
		}catch(HibernateException ex){
			LOG.error("Exception caught in getLastRunTime() ", ex);
			throw new DatabaseException("Exception caught in getLastRunTime() ", ex);
		}
		return lastRunTime;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EngagementProcessingStatus> getProcessedIds(String source, Timestamp timestamp) throws InvalidInputException{
		if(source == null || source.isEmpty()){
			LOG.warn("Source is not present.");
			throw new InvalidInputException("Source is not present.");
		}
		LOG.info("Getting processed ids for source "+source+" after timestamp "+(timestamp!=null? String.valueOf(timestamp):""));
		List<EngagementProcessingStatus> processedRecords = null;
		Criteria criteria = getSession().createCriteria(SurveyPreInitiation.class);
		try{
			criteria.add(Restrictions.eq(CommonConstants.SURVEY_SOURCE_KEY_COLUMN, source));
			if(timestamp != null){
				criteria.add(Restrictions.ge(CommonConstants.CREATED_ON, timestamp));
			}
			criteria.setProjection(Projections.property(CommonConstants.SURVEY_SOURCE_ID_COLUMN));
			criteria.setProjection(Projections.property(CommonConstants.STATUS_COLUMN));
			processedRecords = (List<EngagementProcessingStatus>)criteria.list();
		}catch(HibernateException ex){
			LOG.error("Exception caught in getProcessedIds() ", ex);
			throw new DatabaseException("Exception caught in getProcessedIds() ", ex);
		}
		return processedRecords;
	}

}
