package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.OverviewBranch;
import com.realtech.socialsurvey.core.entities.OverviewCompany;

public interface OverviewBranchDao extends GenericDao<OverviewBranch, String>
{

    public String getOverviewBranchId(Long branchid);
    
    public OverviewBranch findOverviewBranch(Class<OverviewBranch> entityClass, String overviewBranchid);
    
}
