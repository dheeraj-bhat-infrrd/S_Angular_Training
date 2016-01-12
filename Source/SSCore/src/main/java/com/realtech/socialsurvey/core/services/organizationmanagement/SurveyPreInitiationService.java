package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.util.List;
import java.util.Set;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;

public interface SurveyPreInitiationService {

	public List<SurveyPreInitiation> deleteSurveyReminder(Set<Long> incompleteSurveyIds);
}