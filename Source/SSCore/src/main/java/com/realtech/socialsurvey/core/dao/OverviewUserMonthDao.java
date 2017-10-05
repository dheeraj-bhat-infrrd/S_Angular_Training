package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.OverviewUserMonth;

public interface OverviewUserMonthDao extends GenericReportingDao<OverviewUserMonth, String>
{

    OverviewUserMonth fetchOverviewForUserBasedOnMonth( Long userId, int month, int year );

}
