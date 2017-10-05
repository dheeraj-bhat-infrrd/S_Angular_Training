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


    public List<Branch> getBranchesForCompany( long companyId, int isDefault, int start, int batch )
        throws InvalidInputException;


    public List<Long> getBranchIdsUnderCompany( long companyId, int start, int batchSize ) throws InvalidInputException;
    
    public long getRegionIdByBranchId(long branchId);


	List<Branch> getBranchesForRegion(long regionId, int isDefault, int start, int batch) throws InvalidInputException;
}
