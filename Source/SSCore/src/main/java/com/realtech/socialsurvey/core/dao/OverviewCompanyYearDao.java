package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.OverviewCompanyYear;

public interface OverviewCompanyYearDao extends GenericReportingDao<OverviewCompanyYear , String>
{

    OverviewCompanyYear fetchOverviewForCompanyBasedOnYear( Long companyId, int year ) throws NullPointerException;

}
