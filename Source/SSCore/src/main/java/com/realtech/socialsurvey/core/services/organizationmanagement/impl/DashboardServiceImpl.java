package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.services.organizationmanagement.DashboardService;

// JIRA SS-137 BY RM05:BOC
/**
 * Class with methods defined to show dash board of user.
 */

@Component
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	private SurveyDetailsDao surveyDetailsDao;

	@Override
	public long getAllSurveyCountForPastNdays(String columnName, long columnValue, int numberOfDays) {
		return surveyDetailsDao.getSentSurveyCount(columnName, columnValue, numberOfDays);
	}

	@Override
	public long getCompletedSurveyCountForPastNdays(String columnName, long columnValue, int numberOfDays) {
		return surveyDetailsDao.getCompletedSurveyCount(columnName, columnValue, numberOfDays);
	}

	@Override
	public long getClickedSurveyCountForPastNdays(String columnName, long columnValue, int numberOfDays) {
		return surveyDetailsDao.getClickedSurveyCount(columnName, columnValue, numberOfDays);
	}

	@Override
	public long getSocialPostsForPastNdays(String columnName, long columnValue, int numberOfDays) {
		return surveyDetailsDao.getSocialPostsCount(columnName, columnValue, numberOfDays);
	}

	@Override
	public double getSocialScore(String columnName, long columnValue, int numberOfDays) {
		return surveyDetailsDao.getRatingForPastNdays(columnName, columnValue, numberOfDays);
	}
}
// JIRA SS-137 BY RM05:EOC
