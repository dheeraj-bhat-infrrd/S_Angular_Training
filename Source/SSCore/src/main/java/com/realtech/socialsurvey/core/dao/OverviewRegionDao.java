package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.OverviewRegion;

public interface OverviewRegionDao extends GenericReportingDao<OverviewRegion , String>
{

    public String getOverviewRegionId( Long regionId);
    
    public OverviewRegion findOverviewRegion(Class<OverviewRegion> entityClass, String overviewRegionId);
}
