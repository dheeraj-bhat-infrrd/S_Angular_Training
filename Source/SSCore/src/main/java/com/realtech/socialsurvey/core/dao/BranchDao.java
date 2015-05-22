package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.Branch;

/*
 * This interface contains methods which are required for queries and criteria on Branch table.
 */
public interface BranchDao extends GenericDao<Branch, Long> {

	public void deleteBranchesByCompanyId(long companyId);


}
