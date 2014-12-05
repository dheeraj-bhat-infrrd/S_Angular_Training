package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.util.Map;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface OrganizationManagementService {

	public User addCompanyInformation(User user, Map<String, String> organizationalDetails);

	public AccountType addAccountTypeForCompanyAndUpdateStage(User user, String accountType) throws InvalidInputException;
	
	public int fetchAccountTypeForCompany(Company company) throws InvalidInputException;
}
