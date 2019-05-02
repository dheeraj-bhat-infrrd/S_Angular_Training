package com.realtech.socialsurvey.core.dao;

import java.util.List;
import java.util.Map;
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

	Map<String, String> getBranchAndRegionName(long regionId, long branchId) throws InvalidInputException;

    public List<Long> getBranchIdsOfCompany( long companyId ) throws InvalidInputException;
    
    public List<Long> getAllBranchIdsOfCompany( long companyId ) throws InvalidInputException;
    
    public List<Long> getAllBranchIdsOfRegion( long regionId ) throws InvalidInputException;

    public Map<Long, Long> getCompanyIdsForBranchIds( List<Long> branchIds );
    

    public List<Long> getBranchIdsOfRegion( long regionId, int isDefault, int batch, int start ) throws InvalidInputException;


	/**
	 * @param branchId
	 * @return
	 */
	public long checkIfBranchIsDefault(long branchId);


	String getCompanyNameForBranchId(long branchId);


    public List<Long> getBranchIdList( String entityType, long entityId );

}
