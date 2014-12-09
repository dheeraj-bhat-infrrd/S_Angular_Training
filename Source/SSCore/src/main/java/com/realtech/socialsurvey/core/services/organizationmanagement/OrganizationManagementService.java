package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.util.Map;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface OrganizationManagementService {

	public User addCompanyInformation(User user, Map<String, String> organizationalDetails);

	public AccountType addAccountTypeForCompanyAndUpdateStage(User user, String accountType) throws InvalidInputException;
	
	public long fetchAccountTypeMasterIdForCompany(Company company) throws InvalidInputException;
	
	public Branch addBranch(User user, Region region, String branchName, int isDefaultBySystem);

	public Region addRegion(User user, int isDefaultBySystem, String regionName);
}
