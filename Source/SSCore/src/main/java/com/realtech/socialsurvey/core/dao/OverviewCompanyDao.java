package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.OverviewCompany;

public interface OverviewCompanyDao extends GenericReportingDao<OverviewCompany, String>
{

    public String getOverviewCompanyId( Long companyid );
    
    public OverviewCompany findOverviewCompany( Class<OverviewCompany> entityClass, String overviewCompanyid );
}
