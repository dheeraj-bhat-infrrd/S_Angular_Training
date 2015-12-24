package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.SurveyPreInitiationService;

@DependsOn("generic")
@Component
public class SurveyPreInitiationServiceImpl implements SurveyPreInitiationService, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(SurveyPreInitiationServiceImpl.class);

	@Autowired
	private UserProfileDao userProfileDao;

	@Autowired
	private SurveyPreInitiationDao surveyPreInitiationDao;

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("afterPropertiesSet called for profile management service");
	}

	/**
	 * Method to fetch reviews based on the profile level specified, iden is one of
	 * agentId/branchId/regionId or companyId based on the profile level
	 */
	@Override
	@Transactional
	public List<SurveyPreInitiation> getIncompleteSurvey(long iden, double startScore, double limitScore, int startIndex, int numOfRows,
			String profileLevel, Date startDate, Date endDate, boolean realtechAdmin) throws InvalidInputException {
		LOG.info("Method getIncompleteSurvey() called for iden:" + iden + " startScore:" + startScore + " limitScore:" + limitScore + " startIndex:"
				+ startIndex + " numOfRows:" + numOfRows + " profileLevel:" + profileLevel);
		if (iden <= 0l) {
			throw new InvalidInputException("iden is invalid while fetching incomplete reviews");
		}
		boolean isCompanyAdmin = false;
		Set<Long> agentIds = new HashSet<>();
		if (profileLevel.equalsIgnoreCase(CommonConstants.PROFILE_LEVEL_COMPANY)) {
			isCompanyAdmin = true;
		}
		else {
			agentIds = getAgentIdsByProfileLevel(profileLevel, iden);
		}
		Timestamp startTime = null;
		Timestamp endTime = null;
		if (startDate != null)
			startTime = new Timestamp(startDate.getTime());
		if (endDate != null)
			endTime = new Timestamp(endDate.getTime());
		List<SurveyPreInitiation> surveys = surveyPreInitiationDao.getIncompleteSurvey(startTime, endTime, startIndex, numOfRows, agentIds,
				isCompanyAdmin, iden, realtechAdmin);
		return surveys;
	}

	Set<Long> getAgentIdsByProfileLevel(String profileLevel, long iden) throws InvalidInputException {
		if (profileLevel == null || profileLevel.isEmpty()) {
			throw new InvalidInputException("profile level is null or empty while getting agents");
		}
		Set<Long> userIds = new HashSet<>();
		switch (profileLevel) {
			case CommonConstants.PROFILE_LEVEL_REGION:
			    userIds = userProfileDao.findUserIdsByRegion( iden );
                return userIds;
			case CommonConstants.PROFILE_LEVEL_BRANCH:
			    userIds = userProfileDao.findUserIdsByBranch( iden );
			    return userIds;
			case CommonConstants.PROFILE_LEVEL_INDIVIDUAL:
				userIds.add(iden);
				return userIds;
			default:
				throw new InvalidInputException("Invalid profile level while getting iden column name");
		}
	}
	
	@Transactional
	@Override
	public void deleteSurveyReminder(Set<Long> incompleteSurveyIds) {
		LOG.debug("Method deleteSurveyReminder() called");
		surveyPreInitiationDao.deleteSurveysWithIds(incompleteSurveyIds);
		LOG.debug("Method deleteSurveyReminder() finished");
	}
}