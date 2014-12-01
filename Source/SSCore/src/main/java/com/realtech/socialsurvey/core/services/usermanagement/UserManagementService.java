package com.realtech.socialsurvey.core.services.usermanagement;

import java.util.Map;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface UserManagementService {

	public User addCompanyInformation(User user, Map<String, String> organizationalDetails);

	public AccountType addAccountTypeForCompany(User user, String accountType) throws InvalidInputException;
}
