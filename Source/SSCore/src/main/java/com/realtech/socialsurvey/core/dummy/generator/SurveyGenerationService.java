package com.realtech.socialsurvey.core.dummy.generator;

import java.util.List;
import com.realtech.socialsurvey.core.entities.User;

public interface SurveyGenerationService {

	List<User> getAgents(List<String> emailIds);
	
}
