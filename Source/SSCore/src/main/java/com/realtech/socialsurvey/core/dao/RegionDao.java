package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.Region;


/*
 * This interface contains methods which are required for queries and criteria on Region table.
 */
public interface RegionDao extends GenericDao<Region, Long> {

	public void deleteRegionsByCompanyId(long companyId);


}
