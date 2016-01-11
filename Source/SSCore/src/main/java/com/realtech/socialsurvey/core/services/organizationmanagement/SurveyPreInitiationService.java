package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.util.Set;

public interface SurveyPreInitiationService {

	public void deleteSurveyReminder(Set<Long> incompleteSurveyIds);
}