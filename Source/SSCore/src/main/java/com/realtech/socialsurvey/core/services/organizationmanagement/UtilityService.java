package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.util.Map;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;


public interface UtilityService {
	
	/**
	 * Method to populate the vertical master map
	 * @return Map<Integer, VerticalsMaster>
	 */
	public Map<Integer, VerticalsMaster> populateVerticalMastersMap();

	public Map<Integer, ProfilesMaster> populateProfileMastersMap();
}
