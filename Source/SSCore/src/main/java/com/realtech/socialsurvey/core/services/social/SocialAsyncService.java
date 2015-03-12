package com.realtech.socialsurvey.core.services.social;

import java.util.concurrent.Future;
import com.realtech.socialsurvey.core.entities.LinkedInToken;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;

public interface SocialAsyncService {
	public Future<OrganizationUnitSettings> linkedInDataUpdate(String collection, OrganizationUnitSettings unitSettings, LinkedInToken linkedInToken);
}
