package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.OverviewBranch;

public interface OverviewBranchDao extends GenericReportingDao<OverviewBranch, String>
{

    public String getOverviewBranchId(Long branchid);
    
    public OverviewBranch findOverviewBranch(Class<OverviewBranch> entityClass, String overviewBranchid);
    
}
