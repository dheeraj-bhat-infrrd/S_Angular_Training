package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.OverviewRegionMonth;

public interface OverviewRegionMonthDao extends GenericReportingDao<OverviewRegionMonth, String>
{

    OverviewRegionMonth fetchOverviewForRegionBasedOnMonth( Long userId, int month, int year ) throws NullPointerException;

}
