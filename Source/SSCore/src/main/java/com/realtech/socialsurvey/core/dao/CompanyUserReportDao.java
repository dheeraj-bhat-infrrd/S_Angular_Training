package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.CompanyUserReport;

public interface CompanyUserReportDao extends GenericReportingDao<CompanyUserReport, String>
{

    List<CompanyUserReport> fetchCompanyUserReportByCompanyId( Long companyId );

}
