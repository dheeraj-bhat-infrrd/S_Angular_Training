package com.realtech.socialsurvey.core.dao;

import java.util.List;
import java.util.Set;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

/*
 * This interface contains methods which are required for queries and criteria on Branch table.
 */
public interface BranchDao extends GenericDao<Branch, Long> {

	public void deleteBranchesByCompanyId(long companyId);


    public List<Branch> getBranchForBranchIds( Set<Long> branchIds ) throws InvalidInputException;
}
