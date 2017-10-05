package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.OverviewBranchYear;

public interface OverviewBranchYearDao extends GenericReportingDao<OverviewBranchYear, String>
{

    OverviewBranchYear fetchOverviewForBranchBasedOnYear( Long branchId, int year ) throws NullPointerException;

}
