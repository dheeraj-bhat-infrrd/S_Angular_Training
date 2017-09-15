package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.OverviewUserYear;

public interface OverviewUserYearDao extends GenericReportingDao<OverviewUserYear, String>
{

    OverviewUserYear fetchOverviewForUserBasedOnYear( Long userId, int year ) throws NullPointerException;

}
