package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.integration.EngagementProcessingStatus;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface SurveyPreInitiationDao extends GenericDao<SurveyPreInitiation, Long> {

	/**
	 * Gets the last run time for the source
	 * @param source
	 * @return
	 * @throws InvalidInputException
	 */
	public Timestamp getLastRunTime(String source) throws InvalidInputException;
	
	/**
	 * Gets a list of processed ids
	 * @param source
	 * @param timestamp
	 * @return
	 * @throws InvalidInputException
	 */
	public List<EngagementProcessingStatus> getProcessedIds(String source, Timestamp timestamp) throws InvalidInputException;

	public List<SurveyPreInitiation> getIncompleteSurvey(Timestamp startTime, Timestamp endTime, int start, int row, Set<Long> agentIds,
			boolean isCompanyAdmin, long companyId, boolean realtechAdmin) throws InvalidInputException;

	public List<SurveyPreInitiation> getIncompleteSurveyForReminder(long companyId, int surveyReminderInterval, int maxReminders);

    public void getIncompleteSurveysCount( Date startDate, Date endDate, Map<Long, AgentRankingReport> agentReportData );

}
