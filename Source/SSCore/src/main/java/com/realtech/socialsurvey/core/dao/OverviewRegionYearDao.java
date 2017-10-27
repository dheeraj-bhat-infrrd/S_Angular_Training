package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.OverviewRegionYear;

public interface OverviewRegionYearDao extends GenericReportingDao<OverviewRegionYear, String>
{

    OverviewRegionYear fetchOverviewForRegionBasedOnYear( Long regionId, int year ) throws NullPointerException;

}
