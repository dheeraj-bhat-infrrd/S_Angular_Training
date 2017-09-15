package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.OverviewCompanyMonth;

public interface OverviewCompanyMonthDao extends GenericReportingDao<OverviewCompanyMonth, String>
{

    OverviewCompanyMonth fetchOverviewForCompanyBasedOnMonth( Long companyId, int month, int year ) throws NullPointerException;

}
