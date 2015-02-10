package com.realtech.socialsurvey.core.services.organizationmanagement;

import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface ProfileManagementService {

	public OrganizationUnitSettings finalizeProfileDetail(User user, AccountType accountType, UserSettings settings) throws InvalidInputException;

	public OrganizationUnitSettings finalizeProfile(User user, AccountType accountType, UserSettings settings, long agentId, long branchId,
			long regionId) throws InvalidInputException;
}
