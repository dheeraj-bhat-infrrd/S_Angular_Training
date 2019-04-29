package com.realtech.socialsurvey.core.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


/*
 * This interface contains methods which are required for queries and criteria on Region table.
 */
public interface RegionDao extends GenericDao<Region, Long> {

	public void deleteRegionsByCompanyId(long companyId);


    public List<Region> getRegionForRegionIds( Set<Long> regionIds ) throws InvalidInputException;


    public List<Region> getRegionsForCompany( long companyId, int start, int batch ) throws InvalidInputException;


    public List<Long> getRegionIdsUnderCompany( long companyId, int start, int batchSize ) throws InvalidInputException;
    
    public List<Long> getRegionIdsOfCompany( long companyId ) throws InvalidInputException;
    
    public List<Long> getRegionIdsUnderCompany( long companyId ) throws InvalidInputException;

    public Map<Long, Long> getCompanyIdsForRegionIds( List<Long> regionIds );


	/**
	 * @param regionId
	 * @return
	 */
	public long checkIfRegionIsDefault(long regionId);

}
