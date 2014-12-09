/**
 * 
 */
package com.realtech.socialsurvey.core.services.organizationmanagement;

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

	public void updateBranchStatus(User user, long branchId, int status) throws InvalidInputException;

	public void updateRegionStatus(User user, long regionId, int status) throws InvalidInputException;

	public boolean isMaxBranchAdditionExceeded(User user, AccountType accountType) throws InvalidInputException;

	public boolean isMaxRegionAdditionExceeded(User user, AccountType accountType) throws InvalidInputException;

	public Branch addNewBranch(User user, long regionId, String branchName) throws InvalidInputException;

	public Region addNewRegion(User user, String regionName) throws InvalidInputException;

}
