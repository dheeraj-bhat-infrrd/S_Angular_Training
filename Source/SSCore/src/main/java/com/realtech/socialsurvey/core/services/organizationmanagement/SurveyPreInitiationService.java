package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.util.Date;
import java.util.List;
import java.util.Set;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface SurveyPreInitiationService {
	/**
	 * Method to fetch reviews based on the profile level specified, iden is one of
	 * agentId/branchId/regionId or companyId based on the profile level
	 * 
	 * @param iden
	 * @param startScore
	 * @param limitScore
	 * @param startIndex
	 * @param numOfRows
	 * @param profileLevel
	 * @return
	 * @throws InvalidInputException
	 */
	public List<SurveyPreInitiation> getIncompleteSurvey(long iden, double startScore, double limitScore, int startIndex, int numOfRows,
			String profileLevel, Date startDate, Date endDate, boolean realtechAdmin) throws InvalidInputException;

	public List<SurveyPreInitiation> deleteSurveyReminder(Set<Long> incompleteSurveyIds);
}