package com.realtech.socialsurvey.core.services.usermanagement;

import java.util.Map;
import com.realtech.socialsurvey.core.entities.User;

public interface UserManagementService {

	public User addCompanyInformation(User user, Map<String, String> organizationalDetails);

}
