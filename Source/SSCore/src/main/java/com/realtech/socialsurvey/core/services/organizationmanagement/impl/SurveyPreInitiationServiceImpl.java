package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
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


	@Transactional
	@Override
	public void deleteSurveyReminder(Set<Long> incompleteSurveyIds) {
		LOG.debug("Method deleteSurveyReminder() called");
		surveyPreInitiationDao.deleteSurveysWithIds(incompleteSurveyIds);
		LOG.debug("Method deleteSurveyReminder() finished");
	}
}