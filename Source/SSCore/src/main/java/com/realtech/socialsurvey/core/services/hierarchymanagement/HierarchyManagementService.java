/**
 * 
 */
package com.realtech.socialsurvey.core.services.hierarchymanagement;

import java.util.List;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * Services for managing hierarchy
 */
public interface HierarchyManagementService {

	public List<Branch> getAllBranchesForCompany(Company company) throws InvalidInputException;

	public List<Region> getAllRegionsForCompany(Company company) throws InvalidInputException;

	public void deleteBranch(long branchId) throws InvalidInputException;

	public void deleteRegion(long regionId) throws InvalidInputException;

	public boolean isMaxBranchAdditionExceeded(User user, AccountType accountType) throws InvalidInputException;

	public boolean isMaxRegionAdditionExceeded(User user, AccountType accountType) throws InvalidInputException;

}
