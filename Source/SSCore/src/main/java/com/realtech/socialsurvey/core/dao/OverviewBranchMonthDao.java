package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.OverviewBranchMonth;

public interface OverviewBranchMonthDao extends GenericReportingDao<OverviewBranchMonth, String>
{

    OverviewBranchMonth fetchOverviewForBranchBasedOnMonth( Long userId, int month, int year ) throws NullPointerException;

}
